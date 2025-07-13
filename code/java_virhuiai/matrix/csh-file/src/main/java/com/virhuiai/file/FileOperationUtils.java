package com.virhuiai.file;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 文件操作相关
 */
public class FileOperationUtils {

    private static final Log LOGGER = LogFactory.getLog(FileOperationUtils.class);


    /**
     * 复制文件从源路径到目标路径
     * 如果目标文件已存在，会被覆盖
     *
     Path接口的优势：

     - 提供了更丰富的文件操作方法
     - 支持对路径进行标准化和解析
     - 可以方便地获取父目录、文件名等信息

     Files.copy方法的特点：

     - 支持多种复制选项（通过StandardCopyOption枚举）
     - 自动处理文件属性的复制
     - 提供原子性操作保证
     - 适用于各种操作系统

     可能的异常情况：

     - 源文件不存在
     - 没有足够的权限
     - 磁盘空间不足
     - 目标路径的父目录不存在
     *
     * @param sourcePath      源文件路径
     * @param destinationPath 目标文件路径
     * @throws IOException 当文件操作失败时抛出异常
     */
    public static void copyFile(String sourcePath, String destinationPath) throws IOException {
        // 将字符串路径转换为Path对象
        // Path是Java NIO.2引入的表示文件路径的对象，提供了更多的文件操作功能
        Path source = Paths.get(sourcePath);
        Path destination = Paths.get(destinationPath);

        // 使用Files工具类的copy方法复制文件
        // StandardCopyOption.REPLACE_EXISTING 表示如果目标文件已存在则替换它
        // 该方法会自动处理文件复制过程，包括创建必要的目录结构
        // 复制过程是原子性的，要么完全成功，要么完全失败
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }


    /**
     * 删除文件（如果存在）
     *
     * @param filePath 文件路径
     */
    public static void deleteFileIfExists(String filePath) {
        Path path = Paths.get(filePath);
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                LOGGER.info("文件成功删除。");
            } else {
                LOGGER.info("文件不存在或无法删除。");
            }
        } catch (IOException e) {
            LOGGER.error("删除文件时发生IO异常: " + e.getMessage());
        }
    }



}
