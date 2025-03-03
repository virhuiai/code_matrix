package com.virhuiai.Md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    /**
     * 生成字符串的MD5值
     * @param input 输入字符串
     * @return MD5字符串(32位小写)
     */
    public static String getMD5(String input) {
        if (input == null) {
            return null;
        }
        try {
            // 获取MD5实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用UTF-8编码获取字节数组
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
            // 计算MD5
            byte[] mdBytes = md.digest(inputBytes);
            // 转换为16进制字符串
            return bytesToHex(mdBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 生成字节数组的MD5值
     * @param input 输入字节数组
     * @return MD5字符串(32位小写)
     */
    public static String getMD5(byte[] input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mdBytes = md.digest(input);
            return bytesToHex(mdBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 生成文件的MD5值
     * @param file 输入文件
     * @return MD5字符串(32位小写)
     */
    public static String getMD5(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }

        try (InputStream is = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 生成输入流的MD5值
     * @param is 输入流
     * @return MD5字符串(32位小写)
     */
    public static String getMD5(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 将byte值转换为两位16进制值
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) throws IOException {
        // 字符串MD5
        String str = "Hello World";
        System.out.println("字符串MD5: " + MD5Utils.getMD5(str));

        // 字节数组MD5
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        System.out.println("字节数组MD5: " + getMD5(bytes));

        // 文件MD5
        File file = new File("/Volumes/RamDisk/Csh_Cli-1.1.jar");
        System.out.println("文件MD5: " + getMD5(file));
    }
}