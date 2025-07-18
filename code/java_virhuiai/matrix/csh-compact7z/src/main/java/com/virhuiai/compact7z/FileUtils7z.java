package com.virhuiai.compact7z;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils7z {

    // 私有构造函数，防止实例化
    private FileUtils7z() {
        throw new AssertionError("工具类不应被实例化");
    }

    /**
     * 根据输入的目录路径和文件名,生成上级目录拼接文件名的7z文件全路径
     *
     * @param directoryPath 目录路径
     * @param fileName      文件名(不带后缀)
     * @return 生成的7z文件全路径
     */
    public static String generateParentPath(String directoryPath, String fileName) {
        // 将目录字符串转换为Path对象,便于后续路径操作
        Path path = Paths.get(directoryPath);

        // 获取该目录的上级目录Path对象
        Path parentPath = path.getParent();

        // 如果没有上级目录(如根目录),则直接返回文件名加7z后缀
        if (parentPath == null) {
            return fileName + ".7z";
        }

        // 使用resolve方法将上级目录路径和文件名组合,并转为字符串返回
        // resolve会根据操作系统自动处理路径分隔符
        return parentPath.resolve(fileName + ".7z").toString();
    }

    /**
     * 从输入字符串中随机提取字符返回
     * @param input
     * @return
     */
    public static String getRandomChars(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return getRandomChars(input, input.length());
    }

    /**
     * FileUtils7z.getRandomChars
     * 从输入字符串中随机提取字符返回
     * @param input
     * @param maxLength
     * @return
     */
    public static String getRandomChars(String input, int maxLength) {
        if (input == null || input.isEmpty() || maxLength <= 0) {
            return "";
        }

        // 确保maxLength不超过输入字符串长度
        maxLength = Math.min(maxLength, input.length());

        // 随机决定要提取的字符数量，范围是1到maxLength
        Random random = new Random();
        int actualLength = random.nextInt(maxLength) + 1;

        // 将输入字符串转换为字符列表
        List<Character> charList = input.chars()
                .mapToObj(ch -> (char)ch)
                .collect(Collectors.toList());

        // 创建结果StringBuilder
        StringBuilder result = new StringBuilder();

        // 随机提取字符
        for (int i = 0; i < actualLength; i++) {
            int index = random.nextInt(charList.size());
            result.append(charList.get(index));
            charList.remove(index);
        }



        return result.toString();
    }

    /**
     * FileUtils7z.wrapStr
     * @param input
     * @param b
     * @param a
     * @return
     */
    public static String wrapStr(String input, String b, String a){
        return "[" + b + "]" + input + "[" + a + "]";
    }
}
