package com.virhuiai.Csh7z;

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Csh7zUtils {
    private static final Log LOGGER = CshLogUtils.createLogExtended(Csh7zUtils.class); // 日志记录器

    private String password;

    /**
     * 压缩指定目录为7z文件并加密
     *
     * @param inputDir   输入目录
     * @param outputFile 输出7z文件
     * @param password   加密密码
     * @throws Exception 压缩过程中的异常
     */
    public void compress(File inputDir, File outputFile, String password,int compressionLevel) throws Exception {
        this.password = password;

        RandomAccessFile raf = null;
        IOutCreateArchive7z outArchive = null;

        try {
            raf = new RandomAccessFile(outputFile, "rw");
            outArchive = SevenZip.openOutArchive7z();

            // 设置最高压缩级别
            outArchive.setLevel(compressionLevel);

            // 启用加密
            if (outArchive instanceof IOutFeatureSetEncryptHeader) {
                ((IOutFeatureSetEncryptHeader) outArchive).setHeaderEncryption(true);
            }

            // 启用多线程
            if (outArchive instanceof IOutFeatureSetMultithreading) {
                ((IOutFeatureSetMultithreading) outArchive).setThreadCount(
                        Runtime.getRuntime().availableProcessors()
                );
            }

            // 获取要压缩的文件列表
            Collection<File> files = FileUtils.listFilesAndDirs(
                    inputDir,
                    TrueFileFilter.INSTANCE,
                    TrueFileFilter.INSTANCE
            );
            int itemCount = files.size() - 1; // 减去输入目录本身

            // 执行压缩
            outArchive.createArchive(
                    new RandomAccessFileOutStream(raf),
                    itemCount,
                    new MyCreateCallback(inputDir)
            );

        } finally {
            if (outArchive != null) {
                try {
                    outArchive.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyCreateCallback implements IOutCreateCallback<IOutItem7z>, ICryptoGetTextPassword {
        private final List<File> fileList;
        private final File baseDir;

        public MyCreateCallback(File baseDir) {
            this.baseDir = baseDir;
            Collection<File> files = FileUtils.listFilesAndDirs(
                    baseDir,
                    TrueFileFilter.INSTANCE,
                    TrueFileFilter.INSTANCE
            );
            this.fileList = new ArrayList<>(files);
            this.fileList.remove(baseDir); // 移除根目录
        }

        @Override
        public void setTotal(long total) throws SevenZipException {
        }

        @Override
        public void setCompleted(long complete) throws SevenZipException {
        }

        @Override
        public void setOperationResult(boolean operationResultOk) throws SevenZipException {
        }

        @Override
        public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory)
                throws SevenZipException {

            File file = fileList.get(index);
            String relativePath = getRelativePath(file, baseDir);

            IOutItem7z item = outItemFactory.createOutItem();
            item.setPropertyPath(relativePath);
            item.setPropertyIsDir(file.isDirectory());

            if (!file.isDirectory()) {
                item.setDataSize(file.length());
            }

            return item;
        }

        @Override
        public ISequentialInStream getStream(int index) throws SevenZipException {
            File file = fileList.get(index);
            if (file.isDirectory()) {
                return null;
            }
            try {
                return new RandomAccessFileInStream(new RandomAccessFile(file, "r"));
            } catch (FileNotFoundException e) {
                throw new SevenZipException("Error opening file: " + file.getAbsolutePath(), e);
            }
        }

        @Override
        public String cryptoGetTextPassword() throws SevenZipException {
            return password;
        }
    }

    private String getRelativePath(File file, File baseDir) {
        String filePath = file.getAbsolutePath();
        String basePath = baseDir.getAbsolutePath();

        if (filePath.startsWith(basePath)) {
            String relativePath = filePath.substring(basePath.length());
            return relativePath.startsWith(File.separator) ?
                    relativePath.substring(1) : relativePath;
        }
        return file.getName();
    }

    // 使用示例
    public static void main(String[] args) {


        CshCliUtils.s1InitializeArgs(args);
        LOGGER.debug("调试信息：命令行参数 " + String.join(", ", args));


        // 输入目录选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("i")
                .longOpt("inDir")
                .desc("要压缩的目录")
                .hasArg()
//                .required() // 设置为必需参数
                .argName("目录路径")
                .build()));

        // 密码选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("p")
                .longOpt("password")
                .desc("设置压缩文件密码")
                .hasArg()
                .argName("密码")
                .build()));

        // 输出文件名选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("o")
                .longOpt("output")
                .desc("输出文件名(默认为: compressed.7z)")
                .hasArg()
                .argName("文件名")
                .build()));

        // 压缩等级选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("l")
                .longOpt("level")
                .desc("压缩等级(0-9, 0=不压缩, 9=最大压缩, 默认=6)")
                .hasArg()
                .argName("等级")
                .type(Number.class) // 指定参数类型为数字
                .build()));


        // 添加帮助选项
        //CshClioptionUtils.addOption(options -> options.addOption("h", "help", false, "显示帮助信息"));

        // 获取输入目录（必需参数）
        String inDir = CshCliUtils.s3GetOptionValue("i", "/Volumes/RamDisk/classes");
        LOGGER.info("输入目录: " + inDir);

        // 获取密码（可选参数）
        String password = CshCliUtils.s3GetOptionValue("p", "123456");
        if (password != null) {
            LOGGER.info("已设置压缩密码");
        } else {
            LOGGER.info("未设置压缩密码");
        }

        // 获取输出文件名（可选参数，默认值为compressed.zip）
        String output = CshCliUtils.s3GetOptionValue("o", "/Volumes/RamDisk/classes2.7z");
        LOGGER.info("输出文件: " + output);

        // 获取压缩等级（可选参数，默认值为6）
        String levelStr = CshCliUtils.s3GetOptionValue("l", "6");
        int level;
        try {
            level = Integer.parseInt(levelStr);
            if (level < 0 || level > 9) {
                LOGGER.error("压缩等级必须在0-9之间，将使用默认值6");
                level = 6;
            }
            LOGGER.info("压缩等级: " + level);
        } catch (NumberFormatException e) {
            LOGGER.error("无效的压缩等级，将使用默认值6");
            level = 6;
        }
        CompressionLevel compressionLevel = CompressionLevel.fromLevel(level);
        LOGGER.info("选择的压缩等级: " + compressionLevel);
        if (!CompressionLevel.isValidLevel(level)) {
            LOGGER.warn("输入的压缩等级 " + level + " 已调整为最接近的可用等级: " + compressionLevel);
        }


        try {
            File inputDir = new File(inDir); // 要压缩的目录
            File outputFile = new File(output); // 输出的7z文件


            Csh7zUtils compressor = new Csh7zUtils();
            compressor.compress(inputDir, outputFile, password, compressionLevel.getLevel());

            LOGGER.info("压缩完成！");

        } catch (Exception e) {
            LOGGER.error("压缩失败：" + e.getMessage(),e);
        }
    }
}