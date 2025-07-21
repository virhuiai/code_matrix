package com.virhuiai.compact7z.o_example_create;

public class Item {
    private String path;
    private byte[] content;

    Item(String path, String content) {
        this(path, content.getBytes());
    }

    Item(String path, byte[] content) {
        this.path= path;
        this.content= content;
    }

    String getPath() {
        return path;
    }

    byte[] getContent() {
        return content;
    }
}
