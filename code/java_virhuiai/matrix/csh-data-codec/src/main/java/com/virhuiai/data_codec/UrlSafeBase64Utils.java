package com.virhuiai.data_codec;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * URL安全的Base64编解码工具类
 *
 * URL安全的Base64编码使用以下64个字符：
 * 大写字母A-Z（26个字符）
 * 小写字母a-z（26个字符）
 * 数字0-9（10个字符）
 * 连字符"-"和下划线"_"（2个字符）
 *
 * 区别于标准Base64：
 * 标准Base64使用"+"和"/"，而URL安全的Base64使用"-"和"_"替代。
 * URL安全的Base64通常不使用填充字符"="。
 *
 * 文件名使用：
 *  URL安全的Base64编码可以用于文件名，因为它不包含在大多数文件系统中被视为特殊字符的符号。
 *  然而，使用时仍需注意以下几点：
 *      a. 某些文件系统可能对文件名长度有限制。
 *      b. 开头的连字符可能在某些系统中造成问题。
 *      c. 文件名中的大小写可能在某些文件系统中不被区分。
 */
public class UrlSafeBase64Utils {

    // 使用enum实现单例模式，缓存编码器和解码器实例
    private enum EncoderDecoderHolder {
        INSTANCE;

        private final Base64.Encoder urlEncoder;
        private final Base64.Decoder urlDecoder;

        EncoderDecoderHolder() {
            this.urlEncoder = Base64.getUrlEncoder();
            this.urlDecoder = Base64.getUrlDecoder();
        }
    }

    // 获取URL安全的Base64编码器
    private static Base64.Encoder getUrlEncoder() {
        return EncoderDecoderHolder.INSTANCE.urlEncoder;
    }

    // 获取URL安全的Base64解码器
    private static Base64.Decoder getUrlDecoder() {
        return EncoderDecoderHolder.INSTANCE.urlDecoder;
    }
    ///////


    private static final Set<Character> BASE64_URL_CHARACTERS = new HashSet<>(Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    ));

    /**
     * 是否为有效Base64URL字符串
     *
     * UrlSafeBase64Utils.是否为有效Base64URL字符串
     * 验证字符串是否为有效的Base64 URL字符串
     * PromptUtils.是否为有效Base64URL字符串
     *
     * @param input 需要验证的字符串
     * @return 如果是有效的Base64 URL字符串则返回true，否则返回false
     * @throws IllegalArgumentException 如果输入字符串为null
     */
    public static boolean isValidBase64UrlString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("输入字符串不能为null");
        }
        for (char c : input.toCharArray()) {
            if (!BASE64_URL_CHARACTERS.contains(c)) {
                return false; // 发现无效字符
            }
        }
        return true; // 所有字符有效
    }

    /**
     * 将字符串编码为URL安全的Base64格式
     *
     * 我们使用 StandardCharsets.UTF_8 来明确指定 UTF-8 编码，而不是依赖默认编码。这样可以确保在不同的系统环境下，编码和解码的结果都是一致的。
     *
     * @param input 待编码的字符串
     * @return 编码后的URL安全Base64字符串
     * @throws IllegalArgumentException 如果输入为null
     */
    public static String encodeToString(String input) {
        if (input == null) {
            throw new IllegalArgumentException("输入字符串不能为null");
        }
//        return getUrlEncoder().encodeToString(input.getBytes());
        return getUrlEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将字节数组编码为URL安全的Base64格式
     *
     * @param src 待编码的字节数组
     * @return 编码后的URL安全Base64字符串
     * @throws IllegalArgumentException 如果输入为null
     */
    public static String encodeToStringFromByteArr(byte[] src) {
        if (src == null) {
            throw new IllegalArgumentException("输入字节数组不能为null");
        }
        return getUrlEncoder().encodeToString(src);
    }


    /**
     * 将URL安全的Base64编码字符串解码为普通字符串
     *
     * @param input 待解码的URL安全Base64字符串
     * @return 解码后的字符串
     * @throws IllegalArgumentException 如果输入不是有效的Base64编码
     * @throws IllegalArgumentException 如果输入为null或不是有效的Base64编码
     */
    public static String decode(String input) {
        if (input == null) {
            throw new IllegalArgumentException("输入字符串不能为null");
        }
        try {
//            return new String(getUrlDecoder().decode(input));
            return new String(getUrlDecoder().decode(input), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的Base64输入：" + input, e);
        }
    }

    /**
     * 将URL安全的Base64编码字符串解码为字节数组
     *
     * @param input 待解码的URL安全Base64字符串
     * @return 解码后的字节数组
     * @throws IllegalArgumentException 如果输入不是有效的Base64编码
     * @throws IllegalArgumentException 如果输入为null或不是有效的Base64编码
     */
    public static byte[] decodeToByteArr(String input) {
        if (input == null) {
            throw new IllegalArgumentException("输入字符串不能为null");
        }
        try {
            return getUrlDecoder().decode(input);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的Base64输入：" + input, e);
        }
    }

    /**
     * 计算给定长度的原始数据编码后的Base64字符串长度
     *
     * @param srclen 原始数据长度
     * @return 编码后的Base64字符串长度
     *
     * 在 Base64 编码中，每 3 个字节的原始数据会被编码成 4 个字节的编码数据。如果原始数据的长度不是 3 的倍数，就需要进行填充。
     *
     *
    我们来证明一个更一般的结论：对于任意正整数 n，以及任意长度为 srclen 的输入数据，`(srclen + n - 1) / n` 的结果都能保证 >= `ceil(srclen / n)`

     **证明：**

    1. **证明目标:**

     * 我们需要证明 `(srclen + n - 1) / n >= ceil(srclen / n)`  恒成立。
    2. **证明过程:**

     * 令 `srclen = k * n + r`，其中 k 是非负整数，r 是 srclen 除以 n 的余数 (0 <= r < n)。
     * 则右边 `ceil(srclen / n) = ceil((k * n + r) / n) = k + ceil(r / n)`。

     * 当 r = 0 时，`ceil(r / n) = 0`。
     * 当 r > 0 时，`ceil(r / n) = 1`。
     * 同时左边，`(srclen + n - 1) / n = (k * n + r + n - 1) / n = k + (r + n - 1) / n`。

     * 当 r = 0 时，`(r + n - 1) / n = (n - 1) / n < 1`，为 0。
     * 当 r > 0 时，`2 > (r + n - 1) / n >= 1`，`(r + n - 1) / n` >= 1
     * 因此，无论 r 的取值如何，`(srclen + n - 1) / n` 的结果都大于等于 `ceil(srclen / n)`

     */
    public static int outLength(int srclen) {
        // 计算编码后的长度
        // 每 3 个字节的输入会被编码为 4 个字符的输出
        // 加 2 是为了向上取整，确保有足够的空间进行填充
        return 4 * ((srclen + 2) / 3);
    }


    /**
     * 根据Base64编码后的长度估算原始数据的长度（方法2）
     *
     * 精确度：此方法总是给出原始数据长度的精确下限，不会高估。
     * 使用场景：当需要确保不会高估原始数据长度，或者当处理大量数据且需要精确估算时使用。
     * 特别适用于需要分配缓冲区等情况，因为它保证不会分配过小的空间。
     *
     * @param encodedLen Base64编码后的长度
     * @return 估算的原始数据长度
     */
    public static int inLength(int encodedLen) {
        // 向下取整到最近的 4 的倍数
        int adjustedLen = encodedLen - (encodedLen % 4);
        // 计算最大可能的原始数据长度
        return (adjustedLen / 4) * 3;
    }

    public static void main(String[] args) {
// 1. 基本功能测试
        System.out.println("=== 基本功能测试 ===");
        String originalText = "Hello, World! 你好，世界！";
        String encoded = encodeToString(originalText);
        String decoded = decode(encoded);
        System.out.println("原始文本: " + originalText);
        System.out.println("编码结果: " + encoded);
        System.out.println("解码结果: " + decoded);
        System.out.println("编解码是否一致: " + originalText.equals(decoded));

        // 2. 特殊字符测试
        System.out.println("\n=== 特殊字符测试 ===");
        String specialChars = "!@#$%^&*()_+{}|:\"<>?`~-=[]\\;',./";
        encoded = encodeToString(specialChars);
        decoded = decode(encoded);
        System.out.println("特殊字符: " + specialChars);
        System.out.println("编码结果: " + encoded);
        System.out.println("解码结果: " + decoded);

        // 3. 空字符串测试
        System.out.println("\n=== 空字符串测试 ===");
        String emptyString = "";
        encoded = encodeToString(emptyString);
        decoded = decode(encoded);
        System.out.println("空字符串编码: " + encoded);
        System.out.println("空字符串解码: " + decoded);

        // 4. 字节数组测试
        System.out.println("\n=== 字节数组测试 ===");
        byte[] testBytes = {1, 2, 3, 4, 5};
        String encodedBytes = encodeToStringFromByteArr(testBytes);
        byte[] decodedBytes = decodeToByteArr(encodedBytes);
        System.out.println("原始字节数组: " + Arrays.toString(testBytes));
        System.out.println("编码结果: " + encodedBytes);
        System.out.println("解码结果: " + Arrays.toString(decodedBytes));

        // 5. 验证字符测试
        System.out.println("\n=== 验证字符测试 ===");
        String validBase64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
        String invalidBase64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        System.out.println("有效Base64URL字符串验证: " + isValidBase64UrlString(validBase64));
        System.out.println("无效Base64URL字符串验证: " + isValidBase64UrlString(invalidBase64));

        // 6. 长度计算测试
        System.out.println("\n=== 长度计算测试 ===");
        int[] testLengths = {1, 2, 3, 4, 5, 10, 100};
        for (int len : testLengths) {
            System.out.printf("原始长度: %d, 编码后长度: %d, 解码后估算长度: %d%n",
                    len, outLength(len), inLength(outLength(len)));
        }

        // 7. 异常测试
        System.out.println("\n=== 异常测试 ===");
        try {
            encodeToString(null);
        } catch (IllegalArgumentException e) {
            System.out.println("编码null值异常捕获: " + e.getMessage());
        }

        try {
            decode(null);
        } catch (IllegalArgumentException e) {
            System.out.println("解码null值异常捕获: " + e.getMessage());
        }

        try {
            decode("invalid base64!");
        } catch (IllegalArgumentException e) {
            System.out.println("解码无效Base64异常捕获: " + e.getMessage());
        }
    }
}

