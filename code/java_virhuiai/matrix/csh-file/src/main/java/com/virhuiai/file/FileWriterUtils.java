package com.virhuiai.file;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
