package com.virhuiai.log.file;

import com.virhuiai.log.file.obj.FileInformation;
import com.virhuiai.log.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
public class FileInfoUtils {

    private static final Log LOGGER = LogFactory.getLog();


    /**
     * 检查目录是否存在并拼接文件名
     *
     * @param directoryPath 目录路径
     * @param fileName 文件名
     * @return 完整的文件路径，如果目录不存在则返回null
     */
    public static String getFullPath(String directoryPath, String fileName) {
        // 创建目录的Path对象
        Path directory = Paths.get(directoryPath);

        // 检查目录是否存在
        if (Files.exists(directory) && Files.isDirectory(directory)) {
            // 目录存在，拼接文件名
            Path fullPath = directory.resolve(fileName);
            return fullPath.toString();
        } else {
            // 目录不存在
            System.out.println("目录不存在：" + directoryPath);
            return null;
        }
    }


    /**
     * org.apache.logging.log4j.core.util
     * Retrieves the file extension from the given File.
     * 从给定的文件中获取文件扩展名。
     * @param file the File object
     * 文件对象
     * @return the file extension (e.g., "txt", "log"), or null if no extension is found
     * 文件扩展名（例如，“txt”，“log”），如果没有找到扩展名则返回null
     */
    public static String getFileExtension(final File file) {
        final String fileName = file.getName(); // 获取文件名
        // 检查文件名中是否存在“.”，且“.”不在文件名的开头
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1); // 返回“.”后面的子字符串作为扩展名
        }
        return null; // 如果没有扩展名则返回null
    }



    /**
     * 获取文件信息
     *
     * @param filePath 文件路径
     * @return FileInformation 包含文件详细信息的对象
     * @throws IOException 如果读取文件属性失败
     */
    public static FileInformation getFileInfo(String filePath) throws IOException {
        // 参数验证
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("文件不存在: " + filePath);
        }

        // 读取文件属性
        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

        // 创建并填充FileInformation对象
        FileInformation fileInfo = new FileInformation();
        fileInfo.setAbsolutePath(path.toAbsolutePath());
        fileInfo.setFileName(path.getFileName().toString());
        fileInfo.setDirectory(Files.isDirectory(path));
        fileInfo.setRegularFile(Files.isRegularFile(path));
        fileInfo.setSize(attrs.size());
        fileInfo.setCreationTime(attrs.creationTime());
        fileInfo.setLastModifiedTime(attrs.lastModifiedTime());
        fileInfo.setLastAccessTime(attrs.lastAccessTime());

        // 记录日志
//        LOGGER.info(fileInfo.toString());

        return fileInfo;
    }

    public static void main(String[] args) {
        String ext = FileInfoUtils.getFileExtension(new File("/Volumes/THAWSPACE/CshProject/code_matrix.git/code/java_virhuiai/2_Csh_Cli/pom.xml"));
        System.out.println(ext);

    }
}
