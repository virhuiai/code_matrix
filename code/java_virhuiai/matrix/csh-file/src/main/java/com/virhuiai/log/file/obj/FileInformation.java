package com.virhuiai.log.file.obj;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * 一个文件信息类
 */
public class FileInformation {
    private Path absolutePath;
    private String fileName;
    private boolean isDirectory;
    private boolean isRegularFile;
    private long size;
    private FileTime creationTime;
    private FileTime lastModifiedTime;
    private FileTime lastAccessTime;

    // Getter和Setter方法
    public Path getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(Path absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isRegularFile() {
        return isRegularFile;
    }

    public void setRegularFile(boolean regularFile) {
        isRegularFile = regularFile;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(FileTime creationTime) {
        this.creationTime = creationTime;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(FileTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public FileTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(FileTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    // 重写toString方法，用于生成格式化的文件信息
    @Override
    public String toString() {
        StringBuilder fileInfo = new StringBuilder("文件信息:\n");
        fileInfo.append("路径: ").append(absolutePath).append("\n")
                .append("文件名: ").append(fileName).append("\n")
                .append("是否为目录: ").append(isDirectory).append("\n")
                .append("是否为普通文件: ").append(isRegularFile).append("\n")
                .append("大小: ").append(size).append(" 字节\n")
                .append("创建时间: ").append(creationTime).append("\n")
                .append("最后修改时间: ").append(lastModifiedTime).append("\n")
                .append("最后访问时间: ").append(lastAccessTime);
        return fileInfo.toString();
    }
}
