package com.virhuiai.log.md5;

import java.util.*;

/**
 * 在文件名中混入额外的可识别字符，同时保证能够准确地提取出原始MD5值。
 */
public class MD5FileNameUtils {
    // MD5结果字符集
    private static final char[] MD5_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };

    // 可用于文件名的额外字符集
    private static final char[] EXTRA_CHARS = {
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            '-', '_', '.', '@', '#', '$'
    };

    /**
     * 在MD5字符串中随机位置插入指定数量的额外字符
     * @param md5 原始MD5字符串
     * @param extraCount 要插入的额外字符数量
     * @return 混合后的字符串
     */
    public static String insertRandomChars(String md5, int extraCount) {
        // 参数校验
        if (md5 == null || !isMD5String(md5) || extraCount < 0) {
            throw new IllegalArgumentException("无效的输入参数");
        }

        // 初始化随机数生成器
        Random random = new Random();

        // 将MD5字符串转换为字符数组
        char[] result = md5.toCharArray();

        // 创建位置列表
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i <= result.length; i++) {
            positions.add(i);
        }

        // 构建结果StringBuilder
        StringBuilder sb = new StringBuilder();
        int currentPos = 0;

        // 插入额外字符
        for (int i = 0; i < extraCount; i++) {
            // 随机选择插入位置
            int posIndex = random.nextInt(positions.size());
            int insertPos = positions.get(posIndex);
            positions.remove(posIndex);

            // 添加insertPos之前的MD5字符
            while (currentPos < insertPos) {
                sb.append(result[currentPos]);
                currentPos++;
            }

            // 随机选择并插入额外字符
            char extraChar = EXTRA_CHARS[random.nextInt(EXTRA_CHARS.length)];
            sb.append(extraChar);
        }

        // 添加剩余的MD5字符
        while (currentPos < result.length) {
            sb.append(result[currentPos]);
            currentPos++;
        }

        return sb.toString();
    }

    /**
     * 从混合字符串中提取原始MD5值
     * @param mixed 混合后的字符串
     * @return 原始MD5字符串
     */
    public static String extractMD5(String mixed) {
        return extractMD5(mixed, new HashMap<String, String>(){
            {
                putIfAbsent("needCheckMd5", "0");
            }
        });
    }

    /**
     * 从混合字符串中提取原始MD5值
     * @param mixed
     * @param params
     * @return
     */
    public static String extractMD5(String mixed, HashMap<String, String> params) {
        if (mixed == null) {
            return null;
        }

        boolean needCheckMd5 = false;
        if(null != params){
            if(new HashSet<String>(){
                {
                    add("needCheckMd5");
                    add("yes");
                    add("true");
                    add("1");
                }
            }.contains(params.get("needCheckMd5"))){
                needCheckMd5 = true;
            }
        }

        // 创建MD5字符集的HashSet，用于快速查找
        Set<Character> md5CharSet = new HashSet<>();
        for (char c : MD5_CHARS) {
            md5CharSet.add(c);
        }

        // 提取MD5字符
        StringBuilder sb = new StringBuilder();
        for (char c : mixed.toCharArray()) {
            if (md5CharSet.contains(c)) {
                sb.append(c);
            }
        }

        // 验证提取结果
        String result = sb.toString();
        if(needCheckMd5){
            return isMD5String(result) ? result : null;
        }else {
            return result;
        }
    }

    /**
     * 验证字符串是否为有效的MD5值
     * @param str 要验证的字符串
     * @return 是否为有效的MD5值
     */
    private static boolean isMD5String(String str) {
        if (str == null || str.length() != 32) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试用例
        String[] testMD5s = {
                "d41d8cd98f00b204e9800998ecf8427e",
                "e10adc3949ba59abbe56e057f20f883e",
                "098f6bcd4621d373cade4e832627b4f6"
        };

        System.out.println("=== MD5文件名转换测试 ===\n");

        for (String md5 : testMD5s) {
            System.out.println("原始MD5: " + md5);

            // 测试不同数量的额外字符
            for (int extraCount : new int[]{3, 5, 8}) {
                System.out.println("添加 " + extraCount + " 个额外字符:");

                // 生成混合字符串
                String mixed = insertRandomChars(md5, extraCount);
                System.out.println("混合结果: " + mixed);

                // 提取MD5
                String extracted = extractMD5(mixed);
                System.out.println("提取结果: " + extracted);

                // 验证结果
                System.out.println("验证: " + (md5.equals(extracted) ? "成功" : "失败"));
                System.out.println();
            }
            System.out.println("------------------------");
        }
    }
}