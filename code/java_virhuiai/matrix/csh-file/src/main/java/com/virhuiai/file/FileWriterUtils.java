package com.virhuiai.file;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 文件写入相关
 */
public class FileWriterUtils {
    private static final Log LOGGER = LogFactory.getLog(FileWriterUtils.class);


    /**
     * 将内容写入文件
     *
     * @param filePath 文件路径
     * @param content  要写入的内容
     * @throws IOException 如果写入失败
     */
    public static void writeContentToFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);

        // 确保文件的父目录存在
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 使用FileOutputStream和FileChannel写入文件
        try (FileOutputStream outputStream = new FileOutputStream(filePath);
             FileChannel fileChannel = outputStream.getChannel()) {
            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
            while (buffer.hasRemaining()) {
                fileChannel.write(buffer);
            }
            // 强制将所有更新写入存储设备
            fileChannel.force(true);
        }
    }


    /**
     * org/apache/logging/log4j/core/util/FileUtils.java
     * org.apache.logging.log4j.core.util.FileUtils#mkdir(java.io.File, boolean)
     * Asserts that the given directory exists and creates it if necessary.
     * 断言给定目录存在，如果不存在则创建它。
     *
     * @param dir the directory that shall exist
     * 期望存在的目录
     * @param createDirectoryIfNotExisting specifies if the directory shall be created if it does not exist.
     * 指定如果目录不存在是否应该创建它。
     * @throws java.io.IOException thrown if the directory could not be created.
     * 如果无法创建目录，则抛出此异常。
     */
    public static void mkdir(final File dir, final boolean createDirectoryIfNotExisting) throws IOException {
        // commons io FileUtils.forceMkdir would be useful here, we just want to omit this dependency
        // 这里可以使用 commons io 的 FileUtils.forceMkdir，但我们希望避免引入此依赖
        if (!dir.exists()) { // 如果目录不存在
            if (!createDirectoryIfNotExisting) { // 如果不允许创建目录
                throw new IOException("The directory " + dir.getAbsolutePath() + " does not exist."); // 抛出目录不存在异常
            }
            if (!dir.mkdirs()) { // 尝试创建目录（包括所有不存在的父目录）
                throw new IOException("Could not create directory " + dir.getAbsolutePath()); // 抛出无法创建目录异常
            }
        }
        if (!dir.isDirectory()) { // 如果路径存在但不是一个目录
            throw new IOException("File " + dir + " exists and is not a directory. Unable to create directory."); // 抛出路径不是目录异常
        }
    }


    /**
     * org/apache/logging/log4j/core/util/FileUtils.java
     * org.apache.logging.log4j.core.util.FileUtils#makeParentDirs(java.io.File)
     * Creates the parent directories for the given File.
     * 为给定文件创建父目录。
     *
     * @param file
     * 要为其创建父目录的文件
     * @throws IOException
     * 如果创建父目录时发生IO错误
     */
    public static void makeParentDirs(final File file) throws IOException {
        // 获取文件的规范路径的父目录
        final File parent = Objects.requireNonNull(file, "file").getCanonicalFile().getParentFile();
        if (parent != null) { // 如果父目录存在
            mkdir(parent, true); // 递归创建父目录
        }
    }
}
