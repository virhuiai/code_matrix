package com.virhuiai.compact7z.o_example_create;

public class Item {
    private String path;
    // 文件的路径或条目在压缩包中的路径
    private byte[] content;
    // 文件的二进制内容

    Item(String path, String content) {
        // 构造函数：根据路径和字符串内容创建Item对象
        this(path, content.getBytes());
        // 调用另一个构造函数，将字符串内容转换为字节数组
    }

    Item(String path, byte[] content) {
        // 构造函数：根据路径和字节数组内容创建Item对象
        this.path= path;
        // 初始化Item的路径
        this.content= content;
        // 初始化Item的内容
    }

    String getPath() {
        // 获取文件或条目的路径
        return path;
        // 返回存储的路径
    }

    byte[] getContent() {
        // 获取文件或条目的二进制内容
        return content;
        // 返回存储的字节数组内容
    }
}
