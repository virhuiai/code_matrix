package com.virhuiai.log.codec;

import org.mozilla.universalchardet.UniversalDetector;
// 导入 UniversalDetector 类，用于检测字节数组的字符编码
import java.nio.charset.Charset;
// 导入 Charset 类，用于表示字符编码
import java.nio.charset.StandardCharsets;
// 导入 StandardCharsets 类，提供标准的字符编码常量（如 UTF-8）
import java.nio.charset.UnsupportedCharsetException;
// 导入 UnsupportedCharsetException 类，用于处理不支持的字符编码异常

public class CharsetConverter {
    // 类声明：定义 CharsetConverter 类，用于字符编码检测和转换
    // 中文注释：该类提供工具方法，用于检测字节数组的编码并将字符串或字节数组转换为目标编码，主要用于解决字符编码问题（如中文乱码）

    /**
     * Detects the character encoding of a byte array.
     *
     * @param bytes The byte array to detect the encoding for.
     * @return The detected charset name, or null if detection failed.
     */
    // 英文注释：检测字节数组的字符编码
    // 英文注释：@param bytes 要检测编码的字节数组
    // 英文注释：@return 检测到的字符集名称，如果检测失败则返回 null
    // 中文注释：方法功能：检测输入字节数组的字符编码
    // 中文注释：参数：
    // 中文注释：  - bytes：输入的字节数组，通常是从文件或数据流中读取的原始数据
    // 中文注释：返回值：字符串，表示检测到的编码（如 "UTF-8"、"GBK"），如果无法检测则返回 null
    // 中文注释：关键步骤：
    // 中文注释：  1. 创建 UniversalDetector 对象，用于分析字节数组的编码
    // 中文注释：  2. 将字节数组传递给检测器，分析编码
    // 中文注释：  3. 调用 dataEnd() 结束检测并获取结果
    // 中文注释：  4. 重置检测器状态并返回编码名称
    // 中文注释：注意事项：短字节数组（例如文件名）可能导致检测不准确
    public static String detectEncoding(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        // 创建 UniversalDetector 实例，初始化为无预设编码
        // 中文注释：detector 用于分析字节数组的编码，基于 Mozilla 的字符编码检测算法
        detector.handleData(bytes, 0, bytes.length);
        // 将字节数组的指定范围（从 0 到 length）传递给检测器
        // 中文注释：将整个字节数组输入检测器以分析可能的编码
        detector.dataEnd();
        // 结束检测过程，触发编码推测
        // 中文注释：调用 dataEnd() 以完成编码检测，准备获取结果
        String encoding = detector.getDetectedCharset();
        // 获取检测到的编码名称
        // 中文注释：encoding 存储检测结果，可能为 "UTF-8"、"GBK" 或 null（如果检测失败）
        detector.reset();
        // 重置检测器状态，以便下次使用
        // 中文注释：重置 detector 的内部状态，确保后续检测不受当前数据影响
        return encoding;
        // 返回检测到的编码名称
        // 中文注释：返回编码名称或 null，供调用者处理
    }


    // 中文注释：特殊处理：如果编码检测失败，返回原始字节数组，未进行转换
    public static byte[] convertToOriginal(byte[] bytes) throws UnsupportedCharsetException {
        String detectedEncoding = detectEncoding(bytes);
        // 调用 detectEncoding 方法检测字节数组的编码
        // 中文注释：detectedEncoding 存储检测到的编码名称，可能为 null
        if(null != detectedEncoding){
            // 检查是否成功检测到编码
            // 中文注释：如果检测到有效编码，继续进行转换
            Charset originalCharset = Charset.forName(detectedEncoding);
            // 根据检测到的编码名称创建 Charset 对象
            // 中文注释：originalCharset 用于将字节数组解码为字符串
            String decodedString = new String(bytes, originalCharset);
            // 使用检测到的编码将字节数组解码为字符串
            // 中文注释：decodedString 是字节数组按原始编码解码后的字符串
            return decodedString.getBytes(originalCharset);
            // 将字符串转换回原始编码的字节数组
            // 中文注释：返回原始编码的字节数组（而非 UTF-8，与方法名和注释不符）
        }else{
            return bytes;
            // 如果编码检测失败，返回原始字节数组
            // 中文注释：回退逻辑，确保在无法检测编码时不抛出异常
        }

//        return decodedString.getBytes(StandardCharsets.UTF_8);
        // 注释掉的代码：将字符串转换为 UTF-8 字节数组
        // 中文注释：此行是原始意图，旨在返回 UTF-8 编码的字节数组，但被注释掉
    }

    public static String convertToOriginal(String input) throws UnsupportedCharsetException {
        // 英文注释：将输入字符串转换为 ISO-8859-1 字节（假设原始数据被错误解码为 ISO-8859-1）
        // 中文注释：方法功能：将可能因错误编码（如 ISO-8859-1）导致乱码的输入字符串转换为正确的字符串
        // 中文注释：参数：
        // 中文注释：  - input：输入字符串，可能包含乱码（例如由 GBK 字节按 ISO-8859-1 错误解码）
        // 中文注释：返回值：正确的字符串，基于检测到的原始编码解码
        // 中文注释：异常：抛出 UnsupportedCharsetException，如果编码转换失败
        // 中文注释：关键步骤：
        // 中文注释：  1. 将输入字符串按 ISO-8859-1 转换为字节，恢复原始字节数据
        // 中文注释：  2. 检测字节的原始编码
        // 中文注释：  3. 使用检测到的编码重新解码字节为字符串
        // 中文注释：  4. 如果检测失败或转换失败，返回原始输入字符串
        // 中文注释：注意事项：
        // 中文注释：  - 假设输入字符串是由 ISO-8859-1 错误解码的字节数据（如 SevenZipJBinding 的行为）
        // 中文注释：  - 方法名 convertToOriginal 与实际功能（修复乱码）不完全匹配，建议改为 convertToCorrectEncoding
        // 中文注释：特殊处理：编码检测失败或转换异常时返回原始输入，避免程序中断
        // 将输入字符串转换为 ISO-8859-1 字节（假设原始数据被错误解码为 ISO-8859-1）
        byte[] bytes;
        // 定义字节数组，用于存储输入字符串的字节表示
        // 中文注释：bytes 用于存储从输入字符串恢复的原始字节数据
        try {
            // ISO-8859-1 的单字节映射特性（每个字节直接对应一个 Unicode 字符）确保了从字符串到字节的转换是可逆的，不会丢失原始字节数据。
            // ISO-8859-1 是一种单字节编码，将每个字节（0x00–0xFF）直接映射到对应的 Unicode 字符（U+0000–U+00FF），不会丢失字节数据。这种“无损”映射使它成为许多库（包括 SevenZipJBinding）的默认回退编码，当无法确定实际编码时使用。
            // 中文注释：使用 ISO-8859-1 将输入字符串转换为字节，恢复原始字节序列
            // 中文注释：ISO-8859-1 的无损特性确保字节数据完整，适合处理乱码问题
            bytes = input.getBytes("ISO-8859-1");
//            bytes = input.getBytes();
            // 注释掉的代码：使用默认编码（平台相关）获取字节
            // 中文注释：此行被注释，因为默认编码因平台而异，可能导致不一致的结果
//        } catch (java.io.UnsupportedEncodingException e) {
        } catch (Exception e) {
            return input;
            // 如果转换失败，返回原始输入字符串
            // 中文注释：异常处理：捕获所有异常（而非仅 UnsupportedEncodingException），返回原始输入以确保程序继续运行
            // 中文注释：注意事项：捕获 Exception 过于宽泛，建议具体捕获 UnsupportedEncodingException
//            throw new UnsupportedCharsetException("无法将输入转换为字节: " + e.getMessage());
            // 注释掉的代码：抛出异常
            // 中文注释：此行被注释，可能为了避免程序中断，改为返回原始输入
        }

        // 检测字节的原始编码
        // 中文注释：调用 detectEncoding 方法检测 bytes 的编码
        String detectedEncoding = detectEncoding(bytes);
        // 获取检测到的编码名称
        // 中文注释：detectedEncoding 存储字节数组的推测编码，可能为 null
        if (detectedEncoding == null) {
            return input;
            // 如果编码检测失败，返回原始输入字符串
            // 中文注释：回退逻辑：无法检测编码时，返回原始输入以避免异常
//            throw new UnsupportedCharsetException("无法检测输入字符串的编码");
            // 注释掉的代码：抛出异常
            // 中文注释：此行被注释，选择返回原始输入以提高鲁棒性
        }else{
            Charset originalCharset = Charset.forName(detectedEncoding);
            // 根据检测到的编码创建 Charset 对象
            // 中文注释：originalCharset 用于将字节数组解码为正确的字符串
            return new String(bytes, originalCharset);
            // 使用检测到的编码将字节解码为字符串
            // 中文注释：将 bytes 按检测到的编码解码，返回正确的字符串（如中文文件名）
        }
    }

    public static void main(String[] args) {
        try {
            // Example usage
            byte[] inputBytes = "这是一个测试字符串".getBytes("GBK"); // Using GBK as an example encoding

            System.out.println("Original Bytes: ");
            printBytes(inputBytes);

            byte[] utf8Bytes = convertToOriginal(inputBytes);

            System.out.println("\nConverted to UTF-8 Bytes: ");
            printBytes(utf8Bytes);

            String utf8String = new String(utf8Bytes, StandardCharsets.UTF_8);
            System.out.println("\nConverted String: " + utf8String);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printBytes(byte[] bytes) {
        // 方法声明：打印字节数组的十六进制表示
        // 中文注释：方法功能：将字节数组以十六进制格式打印，便于调试
        // 中文注释：参数：
        // 中文注释：  - bytes：要打印的字节数组
        // 中文注释：执行流程：
        // 中文注释：  1. 遍历字节数组
        // 中文注释：  2. 将每个字节格式化为两位十六进制数并打印
        // 中文注释：  3. 打印换行符
        for (byte b : bytes) {
            System.out.printf("%02X ", b);
            // 打印每个字节的十六进制表示
            // 中文注释：使用 %02X 格式化字节，确保显示为两位十六进制数
        }
        System.out.println();
        // 打印换行符
        // 中文注释：在字节数组打印完成后添加换行，增强可读性
    }



}
