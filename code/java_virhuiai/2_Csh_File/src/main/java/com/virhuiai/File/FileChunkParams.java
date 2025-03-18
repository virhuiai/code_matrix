package com.virhuiai.File;

/**
 * 文件分块参数类
 * 使用Builder模式来构建实例
 */
public class FileChunkParams {
    private final String filePath;    // 文件路径
    private final long chunkSize;      // 块大小
    private final long start;         // 起始位置
    private final long fileLength;    // 文件长度

    private FileChunkParams(Builder builder) {
        this.filePath = builder.filePath;
        this.chunkSize = builder.chunkSize;
        this.start = builder.start;
        this.fileLength = builder.fileLength;
    }

    // Getter方法
    public String getFilePath() {
        return filePath;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public long getStart() {
        return start;
    }

    public long getFileLength() {
        return fileLength;
    }

    /**
     * Builder类，用于构建FileChunkParams实例
     */
    public static class Builder {
        private String filePath;
        private long chunkSize;
        private long start;
        private long fileLength;

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder chunkSize(long chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        public Builder start(long start) {
            this.start = start;
            return this;
        }

        public Builder fileLength(long fileLength) {
            this.fileLength = fileLength;
            return this;
        }

        public FileChunkParams build() {
            return new FileChunkParams(this);
        }
    }

    /**
     * 创建新的Builder实例
     *
     * @return 新的Builder实例
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "FileChunkParams{" +
                "filePath='" + filePath + '\'' +
                ", chunkSize=" + chunkSize +
                ", start=" + start +
                ", fileLength=" + fileLength +
                '}';
    }

    public static void main(String[] args) {
        FileChunkParams params = FileChunkParams.newBuilder()
                .filePath("/path/to/file.txt")
                .chunkSize(1024)
                .start(0)
                .fileLength(10240)
                .build();

        System.out.println(params);
    }
}