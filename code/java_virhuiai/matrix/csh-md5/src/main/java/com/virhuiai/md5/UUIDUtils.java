package com.virhuiai.md5;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 获取标准的UUID（含连字符）
     * 格式：8-4-4-4-12 位数字和小写字母
     * 示例：123e4567-e89b-12d3-a456-426614174000
     * @return 36位UUID字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取不带连字符的UUID
     * 示例：123e4567e89b12d3a456426614174000
     * @return 32位UUID字符串
     */
    public static String getUUIDWithoutDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取大写的UUID（含连字符）
     * 示例：123E4567-E89B-12D3-A456-426614174000
     * @return 36位大写UUID字符串
     */
    public static String getUpperUUID() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * 获取不带连字符的大写UUID
     * 示例：123E4567E89B12D3A456426614174000
     * @return 32位大写UUID字符串
     */
    public static String getUpperUUIDWithoutDash() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        // 标准UUID（含连字符）
        System.out.println("标准UUID: " + UUIDUtils.getUUID());

        // 无连字符UUID
        System.out.println("无连字符UUID: " + UUIDUtils.getUUIDWithoutDash());

        // 大写UUID
        System.out.println("大写UUID: " + UUIDUtils.getUpperUUID());

        // 无连字符大写UUID
        System.out.println("无连字符大写UUID: " + UUIDUtils.getUpperUUIDWithoutDash());


    }

}
