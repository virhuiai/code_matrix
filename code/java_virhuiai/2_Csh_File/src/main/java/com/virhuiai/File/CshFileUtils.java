// 定义包名，指明该类所在的包路径
package com.virhuiai.File;

// 导入所需的类
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.function.Consumer;

// 定义文件工具类
public class CshFileUtils {

    // 创建日志对象，用于记录操作日志
    // 使用CshLogUtils工具类创建日志实例，传入当前类作为参数
    private static final Log LOGGER = CshLogUtils.createLogExtended(CshFileUtils.class);

    // 定义文件大小限制常量
    private static final long SMALL_FILE_SIZE_LIMIT = 10 * 1024 * 1024; // 10MB
    private static final long LARGE_FILE_SIZE_LIMIT = 1024 * 1024 * 1024; // 1GB
    private static final int BUFFER_SIZE = 8192; // 8KB 缓冲区大小

    /**
     * 验证文件路径并返回文件大小
     * CshFileUtils.validateFileAndGetSize
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）  大于0才是有效的
     *
     * 返回值说明：
     *      返回 0：表示文件不存在或不是常规文件
     *      返回 -1：表示获取文件大小时发生错误
     *      返回正数：表示文件的实际大小（字节数）
     */
    public static long validateFileAndGetSize(String filePath) {
        // 将字符串路径转换为Path对象，便于后续操作
        Path path = Paths.get(filePath);

        // 检查文件路径是否存在
        // 如果路径不存在，记录错误日志并返回0
        if (!Files.exists(path)) {
            LOGGER.error("路径不存在: " + filePath);
            return 0;
        }

        // 检查是否为常规文件（不是目录或其他特殊文件）
        // 如果不是常规文件，记录错误日志并返回0
        if (!Files.isRegularFile(path)) {
            LOGGER.error("路径不是常规文件: " + filePath);
            return 0;
        }

        // 获取文件名
//        String fileName = path.getFileName().toString();

        // 声明文件大小变量
        long fileSize;
        try {
            // 尝试获取文件大小
            fileSize = Files.size(path);
        } catch (IOException e) {
            // 如果获取文件大小时发生IO异常，记录错误日志并返回-1
            LOGGER.error("无法获取文件大小: " + filePath, e);
            return -1;
        }

        // 返回文件大小
        return fileSize;
    }

    /**
     * 转换文件大小到指定单位
     *
     * @param size 文件大小（字节）
     * @param unit 目标单位
     * @return 转换后的大小，带单位的字符串，保留两位小数
     */
    public static String formatFileSize(long size, SizeUnit unit) {
        if (size <= 0) {
            return "0 " + unit.getUnit();
        }

        // 使用 DecimalFormat 格式化数字，保留两位小数
        DecimalFormat df = new DecimalFormat("#.##");
        double convertedSize = (double) size / unit.getFactor();
        return df.format(convertedSize) + " " + unit.getUnit();
//        return convertedSize;
    }

    /**
     * 自动选择最适合的单位来显示文件大小
     *
     * @param size 文件大小（字节）
     * @return 转换后的大小，带单位的字符串
     */
    public static String formatFileSizeAuto(long size) {
        if (size <= 0) {
            return "0 B";
        }

        // 从大到小遍历单位
        SizeUnit[] units = SizeUnit.values();
        for (int i = units.length - 1; i >= 0; i--) {
            SizeUnit unit = units[i];
            if (size >= unit.getFactor()) {
                return formatFileSize(size, unit);
            }
        }

        return formatFileSize(size, SizeUnit.BYTE);
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

    /**
     * 读取文件内容并返回字符串
     * 此方法会将整个文件内容读入内存，适用于小型文件
     *
     * 读取小文件内容并返回字符串
     * 适用于小于10MB的文件
     *
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 如果文件读取失败
     */
    public static String readContentAsStr(String filePath) throws IOException {
        // 首先检查文件是否存在，不存在会抛出异常
        checkFileOrEx(filePath);

        // 获取文件大小
        long fileSize = Files.size(Paths.get(filePath));
        // 检查文件大小是否超过限制
        if (fileSize > SMALL_FILE_SIZE_LIMIT) {
            String errorMsg = String.format(
                    "文件大小(%s)超过限制(%s)，请使用readLargeFile方法",
                    formatFileSizeAuto(fileSize),
                    formatFileSizeAuto(SMALL_FILE_SIZE_LIMIT)
            );
            LOGGER.error(errorMsg);
            throw new IOException(errorMsg);
        }

        // 使用Files.readAllBytes读取文件的所有字节
        // 使用Paths.get将字符串路径转换为Path对象
        // 使用UTF-8编码将字节数组转换为字符串
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
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
     * 使用缓冲流读取大文件内容
     * 适用于大文件，通过Consumer接口处理每一行内容
     *
     * @param filePath 文件路径
     * @param lineConsumer 行处理器
     * @throws IOException 如果文件读取失败
     */
    public static void readLargeFile(String filePath, Consumer<String> lineConsumer) throws IOException {
        // 检查参数
        if (lineConsumer == null) {
            throw new IllegalArgumentException("行处理器不能为空");
        }

        // 检查文件是否存在
        checkFileOrEx(filePath);

        // 获取文件大小
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        // 检查文件大小是否超过限制
        if (fileSize > LARGE_FILE_SIZE_LIMIT) {
            String errorMsg = String.format(
                    "文件大小(%s)超过限制(%s)",
                    formatFileSizeAuto(fileSize),
                    formatFileSizeAuto(LARGE_FILE_SIZE_LIMIT)
            );
            LOGGER.error(errorMsg);
            throw new IOException(errorMsg);
        }

        // 使用带缓冲的读取器逐行处理文件内容
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineConsumer.accept(line);
            }
        }
    }

    /**
     * 使用缓冲流读取大文件内容并返回字符串
     * 适用于需要完整内容但文件较大的情况
     *
     * @param filePath 文件路径
     * @param consumer 字符串消费方法
     * @throws IOException 如果文件读取失败
     */
    public static void readLargeFileAsString(String filePath, Consumer<String> consumer) throws IOException {
        // 检查文件是否存在
        checkFileOrEx(filePath);

        // 获取文件大小
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        // 检查文件大小是否超过限制
        if (fileSize > LARGE_FILE_SIZE_LIMIT) {
            String errorMsg = String.format(
                    "文件大小(%s)超过限制(%s)",
                    formatFileSizeAuto(fileSize),
                    formatFileSizeAuto(LARGE_FILE_SIZE_LIMIT)
            );
            LOGGER.error(errorMsg);
            throw new IOException(errorMsg);
        }

        // 使用StringBuilder来构建最终的字符串
        StringBuilder content = new StringBuilder();

        // 使用带缓冲的读取器读取文件
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            char[] buffer = new char[BUFFER_SIZE];
            int numRead;
            while ((numRead = reader.read(buffer)) != -1) {
//            content.append(buffer, 0, numRead);
                // 调用消费方法处理读取到的内容
                consumer.accept(new String(buffer, 0, numRead));
                // char数组的toString()方法不会正确地转换字符内容，而是返回类似 "[C@1234abcd" 的数组对象字符串。
                // 应该使用 new String(buffer, 0, numRead) 来正确转换读取到的字符。
            }
        }

    }


    /**
     * 安全地读取文件的特定块，确保不超过文件的长度
     *
     * @param params 文件分块参数
     * @return 读取的文件块内容
     * @throws IOException 如果文件读取出错
     */
    public static byte[] chunkFile(FileChunkParams params) throws IOException {
        // 使用try-with-resources确保文件正确关闭
        try (RandomAccessFile file = new RandomAccessFile(params.getFilePath(), "r")) {
            // 如果起始位置超出文件末尾，返回空字符串
            if (params.getStart() >= params.getFileLength()) {
                return "".getBytes();
            }

            // 计算安全的读取长度，确保不超过文件大小
            long safeLength = Math.min(params.getChunkSize(), params.getFileLength() - params.getStart());

            // 将文件指针移动到起始位置
            file.seek(params.getStart());

            // 初始化一个缓冲区以读取块
            byte[] buffer = new byte[(int) safeLength];

            // 读取指定长度的数据
            file.readFully(buffer);

            // 将读取的字节转换成字符串并返回
//            return new String(buffer, StandardCharsets.UTF_8);
            return buffer;
        }
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
     * 复制文件从源路径到目标路径
     * 如果目标文件已存在，会被覆盖
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


    public static void main(String[] args) throws IOException {
        int type;
//        type = 0;//validateFileAndGetSize 
//        type = 1;//formatFileSize
//        type = 2;//formatFileSizeAuto
        type = 3;//

        if(3 == type){
            CshFileUtils.getFileInfo("/Volumes/RamDisk/example.xlsx");
        }else if(2 == type){
            long rs = CshFileUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            String rs1 = formatFileSizeAuto(rs);
            LOGGER.info("formatFileSize,rs1:" + rs1);
        }else if(1 == type){
            long rs = CshFileUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            String rs1 = formatFileSize(rs, SizeUnit.MB);
            LOGGER.info("formatFileSize,rs1:" + rs1);

        } else if(0 == type){
            long rs = CshFileUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            LOGGER.info("validateFileAndGetSize,rs:" + rs);
        }

    }
}
