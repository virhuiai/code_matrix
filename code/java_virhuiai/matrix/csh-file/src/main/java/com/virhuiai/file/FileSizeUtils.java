package com.virhuiai.file;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;
import com.virhuiai.file.obj.SizeUnit;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

/**
 * 文件大小相关
 */
public class FileSizeUtils {

    private static final Log LOGGER = LogFactory.getLog(FileSizeUtils.class);



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
}
