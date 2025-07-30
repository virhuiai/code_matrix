package com.virhuiai.md5;

import java.util.ArrayList;
import java.util.List;

/**
 * 随机MD5生成工具类
 */
public class RandomMD5Utils {

    /**
     * 生成一个基于UUID的随机MD5
     *
     * @return MD5字符串(32位小写)
     */
    public static String getRandomMD5() {
        return MD5Utils.getMD5(UUIDUtils.getUUID());
    }

    /**
     * 生成一个基于无连字符UUID的随机MD5
     *
     * @return MD5字符串(32位小写)
     */
    public static String getRandomMD5Simple() {
        return MD5Utils.getMD5(UUIDUtils.getUUIDWithoutDash());
    }

    /**
     * 生成多个随机MD5，可选择使用的UUID格式
     *
     * @param count    需要生成的MD5数量
     * @param uuidType UUID类型：1-标准UUID, 2-无连字符UUID, 3-大写UUID, 4-无连字符大写UUID
     * @return MD5字符串列表
     */
    public static List<String> getRandomMD5List(int count, int uuidType) {
        if (count < 1) {
            return new ArrayList<>();
        }

        List<String> md5List = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String uuid;
            switch (uuidType) {
                case 1:
                    uuid = UUIDUtils.getUUID();
                    break;
                case 2:
                    uuid = UUIDUtils.getUUIDWithoutDash();
                    break;
                case 3:
                    uuid = UUIDUtils.getUpperUUID();
                    break;
                case 4:
                    uuid = UUIDUtils.getUpperUUIDWithoutDash();
                    break;
                default:
                    uuid = UUIDUtils.getUUID();
            }
            md5List.add(MD5Utils.getMD5(uuid));
        }
        return md5List;
    }

    /**
     * 生成一个组合多个UUID的MD5
     *
     * @param uuidCount 组合的UUID数量
     * @return MD5字符串(32位小写)
     */
    public static String getCombinedUUIDMD5(int uuidCount) {
        if (uuidCount < 1) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uuidCount; i++) {
            sb.append(UUIDUtils.getUUID());
        }
        return MD5Utils.getMD5(sb.toString());
    }

    /**
     * 生成一个基于时间戳和UUID组合的MD5
     *
     * @return MD5字符串(32位小写)
     */
    public static String getTimeBasedRandomMD5() {
        return MD5Utils.getMD5(System.currentTimeMillis() + UUIDUtils.getUUID());
    }

    /**
     * 生成一个自定义前缀的随机MD5
     *
     * @param prefix 自定义前缀
     * @return MD5字符串(32位小写)
     */
    public static String getPrefixedRandomMD5(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        return MD5Utils.getMD5(prefix + UUIDUtils.getUUID());
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        // 生成单个随机MD5
        System.out.println("随机MD5: " + getRandomMD5());

        // 生成简单随机MD5（基于无连字符UUID）
        System.out.println("简单随机MD5: " + getRandomMD5Simple());

        // 生成多个随机MD5
        System.out.println("\n生成3个随机MD5 (使用无连字符大写UUID):");
        List<String> md5List = getRandomMD5List(3, 4);
        for (String md5 : md5List) {
            System.out.println(md5);
        }

        // 生成组合UUID的MD5
        System.out.println("\n组合2个UUID的MD5:");
        System.out.println(getCombinedUUIDMD5(2));

        // 生成基于时间戳的随机MD5
        System.out.println("\n基于时间戳的随机MD5:");
        System.out.println(getTimeBasedRandomMD5());

        // 生成带前缀的随机MD5
        System.out.println("\n带前缀的随机MD5:");
        System.out.println(getPrefixedRandomMD5("TEST_"));
    }
}