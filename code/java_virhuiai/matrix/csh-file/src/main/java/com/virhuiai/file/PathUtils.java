package com.virhuiai.file;

import java.nio.file.Path;
import java.nio.file.Paths;

// 路径工具类，提供路径相关的实用方法
// 主要用于处理文件路径的拼接、规范化等操作，适用于跨平台环境
public class PathUtils {
    /**
     * 根据目录和文件名拼接文件路径。
     *
     * @param directory 目录路径
     * @param fileName  文件名
     * @return 拼接后的文件路径
     * @throws IllegalArgumentException 如果目录或文件名为空或无效
     */
    // 方法用于将给定的目录路径和文件名拼接为完整文件路径
    // directory 参数表示文件所在的目录路径（如 "C:/data" 或 "/home/user"）
    // fileName 参数表示文件名（可包含扩展名，如 "example.txt"）
    // 返回值为 String 类型，表示拼接并规范化后的完整路径
    // 如果 directory 或 fileName 为空或仅包含空白字符，会抛出 IllegalArgumentException 异常
    public static String combinePath(String directory, String fileName) {
        // 检查输入参数是否有效
        // 验证 directory 参数是否为 null 或空字符串（去除首尾空白后）
        if (directory == null || directory.trim().isEmpty()) {
            // 如果目录路径无效，抛出异常
            throw new IllegalArgumentException("目录路径不能为空");
            // 异常终止方法执行，确保后续逻辑不会处理无效输入
        }
        // 验证 fileName 参数是否为 null 或空字符串（去除首尾空白后）
        if (fileName == null || fileName.trim().isEmpty()) {
            // 如果文件名无效，抛出异常
            throw new IllegalArgumentException("文件名不能为空");
            // 异常终止方法执行，确保后续逻辑不会处理无效输入
        }

        // 使用 Path API 拼接路径
        // 将 directory 转换为 Path 对象，支持跨平台路径格式
        Path dirPath = Paths.get(directory);
        // dirPath 表示目录的 Path 对象，可能包含绝对或相对路径
        // 使用 resolve 方法将 fileName 追加到目录路径，形成完整路径
        Path fullPath = dirPath.resolve(fileName);
        // fullPath 表示拼接后的完整路径，自动处理路径分隔符（如 "/" 或 "\"）

        // 返回规范化后的路径字符串
        // 使用 normalize 方法移除路径中的冗余部分（如 ".." 或多余的斜杠）
        return fullPath.normalize().toString();
        // 返回值为规范化后的路径字符串，适合跨平台使用
    }
}
