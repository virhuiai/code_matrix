package com.virhuiai.compact7z;

import com.virhuiai.cli.CliUtils;
import com.virhuiai.codec.CharsetConverter;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.virhuiai.log.CommonRuntimeException;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

public interface IExtractItemsStandard extends IConvertStringToOriginal {

    static final Log LOGGER = LogFactory.getLog();

    /**
     * 使用标准接口解压压缩文件
     *
     * @param params 包含解压参数的 HashMap，需包含 "inputFile" 和 "outputDir"
     */
    default void extract(HashMap<String, String> params) {
        if (params == null) {
            LOGGER.error("参数为null");
            throw new CommonRuntimeException("compact7z.IExtractItemsStandard", "参数为null");
        }

        String inputFile = params.get("inputFile");
        if (inputFile == null || inputFile.isEmpty()) {
            LOGGER.error("参数 inputFile 为null");
            throw new CommonRuntimeException("compact7z.IExtractItemsStandard", "参数 inputFile 为null");
        }

        String outputDir = params.get("outputDir");
        if (outputDir == null || outputDir.isEmpty()) {
            LOGGER.error("参数 outputDir 为null");
            throw new CommonRuntimeException("compact7z.IExtractItemsStandard", "参数 outputDir 为null");
        }

        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(inputFile, "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");

            int count = inArchive.getNumberOfItems();
            List<Integer> itemsToExtract = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (!((Boolean) inArchive.getProperty(i, PropID.IS_FOLDER)).booleanValue()) {
                    itemsToExtract.add(i);
                }
            }
            int[] items = new int[itemsToExtract.size()];
            int i = 0;
            for (Integer integer : itemsToExtract) {
                items[i++] = integer;
            }
            inArchive.extract(items, false, new MyExtractCallback(inArchive, outputDir));
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
            LOGGER.error("Error occurs: " + e, e);
            if (e.getMessage().contains("password")) {
                System.out.print("此压缩文件受密码保护 ");//todo
            }
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }

    /**
     * 自定义解压回调类，用于处理解压过程中的数据流和结果
     */
    class MyExtractCallback implements IArchiveExtractCallback {
        private final String outputDir;
        private int hash = 0;
        private int size = 0;
        private int index;
        private IInArchive inArchive;

        public MyExtractCallback(IInArchive inArchive, String outputDir) {
            this.inArchive = inArchive;
            this.outputDir = outputDir;
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }
        }

        @Override
        public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
            this.index = index;
            if (extractAskMode != ExtractAskMode.EXTRACT) {
                return null;
            }

            String rawPath = String.valueOf(inArchive.getProperty(index, PropID.PATH));
            String encoding = CliUtils.s3GetOptionValue(Opt.ENCODING.getOptionName());
            LOGGER.info("encoding: " + encoding);
            Charset originalCharset = Charset.forName(encoding);
            // 强制使用UTF-8编码处理路径，避免乱码
            String path;
            try {
                // 使用UTF-8编码处理路径
                // 使用UTF-8编码处理路径
                path = new String(rawPath.getBytes("ISO-8859-1"), originalCharset);
                LOGGER.warn("编码转换成功: " + path);
            } catch (UnsupportedEncodingException e) {
                // 如果转换失败，使用原始路径
                path = CharsetConverter.bytesToHex(rawPath.getBytes());
                LOGGER.warn("编码转换失败，UnsupportedEncodingException，bytesToHex: " + path, e);
//                try {
//                    path = new String(rawPath.getBytes("ISO-8859-1"), "UTF-8");
//                    LOGGER.info("编码转换，使用原始路径: " + path, e);
//                } catch (UnsupportedEncodingException ex) {
////                    path = rawPath;
//                    path = CharsetConverter.bytesToHex(rawPath.getBytes());
//                    LOGGER.warn("UnsupportedEncodingException,编码转换失败，bytesToHex: " + path, e);
//                }

            }

            File outputFile = new File(outputDir, path);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            return new ISequentialOutStream() {
                @Override
                public int write(byte[] data) throws SevenZipException {
                    hash ^= Arrays.hashCode(data);
                    size += data.length;

                    try (FileOutputStream fos = new FileOutputStream(outputFile, true)) {
                        fos.write(data);
                    } catch (IOException e) {
                        LOGGER.error("写入文件失败:" + outputFile.getAbsoluteFile());
                        throw new CommonRuntimeException("compact7z.IExtractItemsStandard", "写入文件失败: " + outputFile.getAbsoluteFile());
                    }

                    return data.length;
                }
            };
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        }

        @Override
        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            if (extractOperationResult != ExtractOperationResult.OK) {
                System.err.println("Extraction error");
            } else {
                String succPath = (String) inArchive.getProperty(index, PropID.PATH);
                succPath = CharsetConverter.convertToOriginal(succPath);

                System.out.println(String.format("%9X | %10s | %s", hash, size, succPath));
                hash = 0;
                size = 0;
            }
        }

        @Override
        public void setCompleted(long completeValue) throws SevenZipException {
        }

        @Override
        public void setTotal(long total) throws SevenZipException {
        }
    }
}