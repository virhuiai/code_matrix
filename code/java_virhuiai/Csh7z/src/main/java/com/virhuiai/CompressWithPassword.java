package com.virhuiai;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompressWithPassword {

    private String password;

    /**
     * 压缩指定目录为7z文件并加密
     * @param inputDir 输入目录
     * @param outputFile 输出7z文件
     * @param password 加密密码
     * @throws Exception 压缩过程中的异常
     */
    public void compress(File inputDir, File outputFile, String password) throws Exception {
        this.password = password;

        RandomAccessFile raf = null;
        IOutCreateArchive7z outArchive = null;

        try {
            raf = new RandomAccessFile(outputFile, "rw");
            outArchive = SevenZip.openOutArchive7z();

            // 设置最高压缩级别
            outArchive.setLevel(9);

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
        try {
            File inputDir = new File("/Volumes/RamDisk/classes"); // 要压缩的目录
            File outputFile = new File("/Volumes/RamDisk/classes2.7z"); // 输出的7z文件
            String password = "123456"; // 加密密码

            CompressWithPassword compressor = new CompressWithPassword();
            compressor.compress(inputDir, outputFile, password);

            System.out.println("压缩完成！");

        } catch (Exception e) {
            System.err.println("压缩失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}