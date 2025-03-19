package com.virhuiai.File;

import java.text.DecimalFormat;

/**
 * 文件大小相关
 */
public class FileSizeUtils {

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
