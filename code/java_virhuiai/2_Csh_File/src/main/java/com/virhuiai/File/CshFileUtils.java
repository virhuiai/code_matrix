// 定义包名，指明该类所在的包路径
package com.virhuiai.File;

// 导入所需的类

import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

// 定义文件工具类
public class CshFileUtils {

    // 创建日志对象，用于记录操作日志
    // 使用CshLogUtils工具类创建日志实例，传入当前类作为参数
    private static final Log LOGGER = CshLogUtils.createLogExtended(CshFileUtils.class);




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


    /**
     * 检查文件是否存在，不存在则抛出异常
     * 此方法用于在文件操作前进行文件存在性检查
     *
     * @param filePath 文件路径
     * @return 文件名
     * @throws IOException 如果文件不存在
     */
    public static String checkFileOrEx(String filePath) throws IOException {
        // 将字符串路径转换为Path对象
        Path path = Paths.get(filePath);

        // 检查文件是否存在
        // 如果文件不存在，记录错误日志并抛出IOException
        if (!Files.exists(path)) {
            LOGGER.error("文件不存在: " + filePath);
            throw new IOException("文件不存在: " + filePath);
        }

        // 返回文件名（不包含路径）
        return path.getFileName().toString();
    }

    /**
     * 仅检查文件是否存在
     * 此方法是一个简单的文件存在性检查，不抛出异常
     *
     * @param filePath 文件路径
     * @return boolean 文件存在返回true，否则返回false
     */
    public static boolean checkFileOnly(String filePath) {
        // 将字符串路径转换为Path对象
        Path path = Paths.get(filePath);

        // 返回文件是否存在的布尔值
        return Files.exists(path);
    }



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
     * 检查指定路径是否为有效目录
     *
     * @param directoryPath 目录路径
     * @return 是否为有效目录
     */
    public static boolean checkDir(String directoryPath) {
        Path directory = Paths.get(directoryPath);
        return Files.exists(directory) && Files.isDirectory(directory);
    }


    public static void main(String[] args) throws IOException {
        int type;
//        type = 0;//validateFileAndGetSize
//        type = 1;//formatFileSize
//        type = 2;//formatFileSizeAuto
        type = 3;//

        if(3 == type){
            CshFileUtils.getFileInfo("/Volumes/RamDisk/example.xlsx");
        }else if(2 == type){
            long rs = FileSizeUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            String rs1 = FileSizeUtils.formatFileSizeAuto(rs);
            LOGGER.info("formatFileSize,rs1:" + rs1);
        }else if(1 == type){
            long rs = FileSizeUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            String rs1 = FileSizeUtils.formatFileSize(rs, SizeUnit.MB);
            LOGGER.info("formatFileSize,rs1:" + rs1);

        } else if(0 == type){
            long rs = FileSizeUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            LOGGER.info("validateFileAndGetSize,rs:" + rs);
        }

    }
}
