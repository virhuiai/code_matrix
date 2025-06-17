package com.virhuiai.pdfbox;

import java.io.File;

/**
 * 文件权限检查工具类，提供检查文件或目录的读取、写入和执行权限的方法。
 */
public class FilePermissionUtils {

    /**
     * 检查文件是否可读，确保文件存在且具有读取权限。
     *
     * @param inputFile 要读取的文件对象
     * @throws IllegalArgumentException 如果文件不存在或不可读
     */
    public static void validateReadableFile(File inputFile) {
        // 检查文件是否为null
        if (inputFile == null) {
            throw new IllegalArgumentException("输入文件对象不能为空");
        }
        // 检查文件是否存在
        if (!inputFile.exists()) {
            throw new IllegalArgumentException("文件不存在: " + inputFile.getAbsolutePath());
        }
        // 检查文件是否可读
        if (!inputFile.canRead()) {
            throw new IllegalArgumentException("文件不可读: " + inputFile.getAbsolutePath());
        }
    }

    /**
     * 检查文件是否可读写，确保文件或其父目录存在且具有读取和写入权限。
     *
     * @param file 要读写的文件对象
     * @throws IllegalArgumentException 如果文件或其父目录不可读写
     */
    public static void validateReadWriteFile(File file) {
        // 检查文件是否为null
        if (file == null) {
            throw new IllegalArgumentException("文件对象不能为空");
        }
        // 如果文件已存在，检查其读写权限
        if (file.exists()) {
            if (!file.canRead()) {
                throw new IllegalArgumentException("文件不可读: " + file.getAbsolutePath());
            }
            if (!file.canWrite()) {
                throw new IllegalArgumentException("文件不可写: " + file.getAbsolutePath());
            }
        } else {
            // 如果文件不存在，检查其父目录是否存在且可写
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                throw new IllegalArgumentException("父目录不存在: " + parentDir.getAbsolutePath());
            }
            if (parentDir != null && !parentDir.canWrite()) {
                throw new IllegalArgumentException("父目录不可写: " + parentDir.getAbsolutePath());
            }
        }
    }

    /**
     * 检查文件是否可执行，确保文件存在且具有执行权限。
     *
     * @param executableFile 要执行的文件对象
     * @throws IllegalArgumentException 如果文件不存在或不可执行
     */
    public static void validateExecutableFile(File executableFile) {
        // 检查文件是否为null
        if (executableFile == null) {
            throw new IllegalArgumentException("可执行文件对象不能为空");
        }
        // 检查文件是否存在
        if (!executableFile.exists()) {
            throw new IllegalArgumentException("文件不存在: " + executableFile.getAbsolutePath());
        }
        // 检查文件是否可执行
        if (!executableFile.canExecute()) {
            throw new IllegalArgumentException("文件不可执行: " + executableFile.getAbsolutePath());
        }
    }
}