package com.virhuiai.compact7z;

import com.virhuiai.log.CommonRuntimeException;
import com.virhuiai.log.codec.CharsetConverter;
import com.virhuiai.log.logext.LogFactory;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;

public interface IExtractItemsSimple extends IConvertStringToOriginal{

    static final Log LOGGER = LogFactory.getLog();

    // "/Volumes/RamDisk/4b9a4a887e3b49ea2fc25e52b52fd823vTDqA.7z"
    default void extractWithPass(HashMap<String, String> params){
        if (params == null) {
            LOGGER.error("参数为null");
            throw new CommonRuntimeException("compact7z.IExtractItemsSimple", "参数为null");
        }

        String inputFile = params.get("inputFile");
        if(null == inputFile || inputFile.isEmpty()){
            LOGGER.error("参数 inputFile 为null");
            throw new CommonRuntimeException("compact7z.IExtractItemsSimple", "参数 inputFile 为null");
        }

//        String pass = params.get("pass");
//        if(null == pass || pass.isEmpty()){
//            LOGGER.error("参数 pass 为null");
//            throw new CommonRuntimeException("compact7z.IExtractItemsSimple", "参数 pass 为null");
//        }

        String outputDir = params.get("outputDir");
        if(null == outputDir || outputDir.isEmpty()){
            LOGGER.error("参数 outputFile 为null");
            throw new CommonRuntimeException("compact7z.IExtractItemsSimple", "参数 outputFile 为null");
        }


        try(RandomAccessFile randomAccessFile = new RandomAccessFile(inputFile, "r");

            // 中文注释：创建 RandomAccessFile 对象以只读模式打开指定的压缩文件

            // 变量说明：randomAccessFile 用于以随机访问方式读取压缩文件
            // 变量说明：inArchive 表示 SevenZip 库的压缩文件对象，用于操作压缩文件
            IInArchive inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));
        ) {
            // 确保输出目录存在
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs(); // 创建目录（包括父目录）
            }

            // 中文注释：通过 SevenZip 库打开压缩文件，自动检测压缩格式，使用 RandomAccessFileInStream 提供文件输入流

            // Getting simple interface of the archive inArchive
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            // 中文注释：获取压缩文件的简单接口 ISimpleInArchive，便于访问文件项信息

            System.out.println("   Hash   |    Size    | Filename");
            System.out.println("----------+------------+---------");
            // 中文注释：打印表头，显示校验和、文件大小和文件名的列标题

            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                // 中文注释：遍历压缩文件中的所有文件项
                // 变量说明：item 表示压缩文件中的单个文件项，包含路径、大小等信息
                final int[] hash = new int[] { 0 };
                // 变量说明：hash 数组用于存储文件内容的校验和，初始化为 0
                if (!item.isFolder()) {
                    String succPath = item.getPath();
                    succPath = CharsetConverter.convertToOriginal(succPath);

                    // 构造输出文件路径
                    File outputFile = new File(outputDir, succPath);
                    File parentDir = outputFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs(); // 创建文件的父目录
                    }

                    // 中文注释：检查文件项是否为文件夹，仅处理非文件夹项
                    ExtractOperationResult result;
                    // 变量说明：result 用于存储解压操作的结果状态

                    final long[] sizeArray = new long[1];
                    // 变量说明：sizeArray 用于记录解压后文件的大小
                    result = item.extractSlow(new ISequentialOutStream() {
                        // 中文注释：调用 extractSlow 方法解压文件项，传入 ISequentialOutStream 接口实现以处理解压数据
                        // 接口说明：ISequentialOutStream 用于逐字节处理解压后的数据流
                        public int write(byte[] data) throws SevenZipException {
                            // 方法功能说明：实现 ISequentialOutStream 的 write 方法，处理解压数据并计算校验和
                            // 参数说明：data 为解压出的数据字节数组
                            // 返回值说明：返回处理的数据长度
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            // 中文注释：使用 Arrays.hashCode 计算数据的校验和并与当前 hash 值进行异或运算
                            sizeArray[0] += data.length;

//                             将数据写入到指定文件
                            try (FileOutputStream fos = new FileOutputStream(outputFile, true)) {
                                fos.write(data);
                            } catch (IOException e) {
                                LOGGER.error("写入文件失败:" + outputFile.getAbsoluteFile());
                                throw new CommonRuntimeException("compact7z.IExtractItemsSimple", "写入文件失败: " + outputFile.getAbsoluteFile());
                            }

                            // 中文注释：累加数据长度，记录文件总大小
                            return data.length; // Return amount of consumed data
                            // 中文注释：返回已处理的数据长度，通知 SevenZip 库处理完成
                        }
                    });
                    // 中文注释：执行解压操作，extractSlow 方法会调用 write 方法处理解压数据

                    if (result == ExtractOperationResult.OK) {
                        // 中文注释：检查解压操作是否成功
                        System.out.println(String.format("%9X | %10s | %s",
                                hash[0], sizeArray[0], succPath));
                        // 中文注释：如果解压成功，打印文件校验和、大小和路径
                        // 格式说明：%9X 表示校验和以十六进制输出，%10s 表示文件大小，%s 表示文件路径
                    } else {
                        System.err.println("Error extracting item: " + result);
                        // 中文注释：如果解压失败，打印错误信息
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
            LOGGER.error("Error occurs: " + e, e);
            if (e.getMessage().contains("password")) {
                System.out.print("此压缩文件受密码保护，请输入密码: ");
            }
            // 中文注释：捕获并处理解压过程中可能出现的异常，打印错误信息
        } finally {
//            if (inArchive != null) {
//                // 中文注释：检查压缩文件对象是否已初始化
//                try {
//                    inArchive.close();
//                    // 中文注释：关闭压缩文件对象，释放资源
//                } catch (SevenZipException e) {
//                    System.err.println("Error closing archive: " + e);
//                    // 中文注释：处理关闭压缩文件时可能出现的异常
//                }
//            }
//            if (randomAccessFile != null) {
//                // 中文注释：检查 RandomAccessFile 对象是否已初始化
//                try {
//                    randomAccessFile.close();
//                    // 中文注释：关闭文件流，释放资源
//                } catch (IOException e) {
//                    System.err.println("Error closing file: " + e);
//                    // 中文注释：处理关闭文件流时可能出现的异常
//                }
//            }
        }
        // 执行流程说明：
        // 1. 检查命令行参数，获取压缩文件路径
        // 2. 初始化 RandomAccessFile 和 SevenZip 压缩文件对象
        // 3. 获取简单接口，遍历压缩文件中的文件项
        // 4. 对非文件夹项进行解压，计算校验和和文件大小
        // 5. 输出解压结果或错误信息
        // 6. 最后关闭文件和压缩对象，释放资源
        // 注意事项：
        // - 程序依赖 SevenZipJBinding 库，确保库正确配置
        // - 文件路径需有效，否则会抛出 IOException
        // - 解压过程中可能因文件损坏或其他原因抛出 SevenZipException
        // - 资源释放放在 finally 块中，确保无论是否发生异常都能正确关闭
    }
}
