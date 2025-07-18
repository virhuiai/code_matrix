package com.virhuiai.file;

import com.virhuiai.file.obj.FileChunkParams;
import com.virhuiai.log.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * 文件读取相关
 */
public class FileReaderUtils {

    private static final Log LOGGER = LogFactory.getLog(FileReaderUtils.class);

    // 定义文件大小限制常量
    private static final long SMALL_FILE_SIZE_LIMIT = 10 * 1024 * 1024; // 10MB
    private static final long LARGE_FILE_SIZE_LIMIT = 1024 * 1024 * 1024; // 1GB
    private static final int BUFFER_SIZE = 8192; // 8KB 缓冲区大小

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
        FileCheckUtils.checkFileOrEx(filePath);

        // 获取文件大小
        long fileSize = Files.size(Paths.get(filePath));
        // 检查文件大小是否超过限制
        if (fileSize > SMALL_FILE_SIZE_LIMIT) {
            String errorMsg = String.format(
                    "文件大小(%s)超过限制(%s)，请使用readLargeFile方法",
                    FileSizeUtils.formatFileSizeAuto(fileSize),
                    FileSizeUtils.formatFileSizeAuto(SMALL_FILE_SIZE_LIMIT)
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
        FileCheckUtils.checkFileOrEx(filePath);

        // 获取文件大小
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        // 检查文件大小是否超过限制
        if (fileSize > LARGE_FILE_SIZE_LIMIT) {
            String errorMsg = String.format(
                    "文件大小(%s)超过限制(%s)",
                    FileSizeUtils.formatFileSizeAuto(fileSize),
                    FileSizeUtils.formatFileSizeAuto(LARGE_FILE_SIZE_LIMIT)
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
        FileCheckUtils.checkFileOrEx(filePath);

        // 获取文件大小
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        // 检查文件大小是否超过限制
        if (fileSize > LARGE_FILE_SIZE_LIMIT) {
            String errorMsg = String.format(
                    "文件大小(%s)超过限制(%s)",
                    FileSizeUtils.formatFileSizeAuto(fileSize),
                    FileSizeUtils.formatFileSizeAuto(LARGE_FILE_SIZE_LIMIT)
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
}
