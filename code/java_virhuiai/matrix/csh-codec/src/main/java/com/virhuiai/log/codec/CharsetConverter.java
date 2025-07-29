package com.virhuiai.log.codec;

import org.mozilla.universalchardet.UniversalDetector;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public class CharsetConverter {

    /**
     * Detects the character encoding of a byte array.
     *
     * @param bytes The byte array to detect the encoding for.
     * @return The detected charset name, or null if detection failed.
     */
    public static String detectEncoding(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }

    /**
     * Converts a byte array from its original encoding to UTF-8.
     *
     * @param bytes The byte array in the original encoding.
     * @return A new byte array containing the data encoded in UTF-8.
     * @throws UnsupportedCharsetException If the detected encoding is not supported.
     */
    public static byte[] convertToOriginal(byte[] bytes) throws UnsupportedCharsetException {
        String detectedEncoding = detectEncoding(bytes);
        if("null" != detectedEncoding){
            Charset originalCharset = Charset.forName(detectedEncoding);
            String decodedString = new String(bytes, originalCharset);
            return decodedString.getBytes(originalCharset);
        }else{
            return bytes;
        }

//        return decodedString.getBytes(StandardCharsets.UTF_8);
    }

    public static String convertToOriginal(String input) throws UnsupportedCharsetException {
        // 将输入字符串转换为 ISO-8859-1 字节（假设原始数据被错误解码为 ISO-8859-1）
        byte[] bytes;
        try {
            // ISO-8859-1 的单字节映射特性（每个字节直接对应一个 Unicode 字符）确保了从字符串到字节的转换是可逆的，不会丢失原始字节数据。
            // ISO-8859-1 是一种单字节编码，将每个字节（0x00–0xFF）直接映射到对应的 Unicode 字符（U+0000–U+00FF），不会丢失字节数据。这种“无损”映射使它成为许多库（包括 SevenZipJBinding）的默认回退编码，当无法确定实际编码时使用。
            bytes = input.getBytes("ISO-8859-1");
//            bytes = input.getBytes();
//        } catch (java.io.UnsupportedEncodingException e) {
        } catch (Exception e) {
            return input;
//            throw new UnsupportedCharsetException("无法将输入转换为字节: " + e.getMessage());
        }

        // 检测字节的原始编码
        String detectedEncoding = detectEncoding(bytes);
        if (detectedEncoding == null) {
            return input;
//            throw new UnsupportedCharsetException("无法检测输入字符串的编码");
        }
        if("null" != detectedEncoding){
            Charset originalCharset = Charset.forName(detectedEncoding);
            return new String(bytes, originalCharset);
        }else{
            System.out.printf("hi");
            return input;
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
        for (byte b : bytes) {
            System.out.printf("%02X ", b);
        }
        System.out.println();
    }



}
