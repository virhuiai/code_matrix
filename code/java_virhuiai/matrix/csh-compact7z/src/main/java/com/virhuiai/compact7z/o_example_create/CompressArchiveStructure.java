package com.virhuiai.compact7z.o_example_create;

import java.io.File;
import java.util.Random;

/**
 * Some archive formats like GZip only support compression of a single file, while other archive formats allow multiple files and folders to be compressed. In order to demonstrate how those archives can be created, some test file and folder structure is required. The following snippets use a static structure defined by the CompressArchiveStructure class:
 */
// 某些归档格式（如 GZip）仅支持压缩单个文件，而其他归档格式允许压缩多个文件和文件夹。
// 为了演示如何创建这些归档文件，需要一些测试文件和文件夹结构。
// 以下代码片段使用 CompressArchiveStructure 类定义的静态结构。
public class CompressArchiveStructure {
    public static Item[] create() {
        // 该方法用于创建一个预定义的 Item 数组，代表要压缩的文件和文件夹结构。

        //     <root>
        //     |
        //     +- info.txt
        //     +- random-100-bytes.dump
        //     +- dir1
        //     |  +- file-in-a-directory1.txt
        //     +- dir2
        //        +- file-in-a-directory2.txt

        Item[] items = new Item[5];
        // 初始化一个包含 5 个 Item 对象的数组，用于存储文件和目录信息。

        items[0] = new Item("info.txt", "This is the info");
        // 创建第一个 Item，表示一个名为 "info.txt" 的文件，其内容为 "This is the info"。

        byte[] content = new byte[100];
        // 声明一个字节数组，用于存储随机生成的二进制内容。
        new Random().nextBytes(content);
        // 使用 Random 类生成 100 个随机字节，并填充到 content 数组中。
        items[1] = new Item("random-100-bytes.dump", content);
        // 创建第二个 Item，表示一个名为 "random-100-bytes.dump" 的文件，其内容为随机生成的字节。

        // dir1 doesn't have separate archive item
        // dir1 目录本身没有单独的归档项，其下的文件直接指定路径。
        items[2] = new Item("dir1" + File.separator + "file1.txt",
                "This file located in a directory 'dir'");
        // 创建第三个 Item，表示 "dir1" 目录下的 "file1.txt" 文件，其内容为 "This file located in a directory 'dir'"。
        // File.separator 用于在不同操作系统上提供正确的文件路径分隔符。

        // dir2 does have separate archive item
        // dir2 目录有单独的归档项，用于表示目录本身。
        items[3] = new Item("dir2" + File.separator, (byte[]) null);
        // 创建第四个 Item，表示一个名为 "dir2" 的目录。
        // (byte[]) null 表示这是一个目录项，没有具体的文件内容。
        items[4] = new Item("dir2" + File.separator + "file2.txt",
                "This file located in a directory 'dir'");
        // 创建第五个 Item，表示 "dir2" 目录下的 "file2.txt" 文件，其内容为 "This file located in a directory 'dir'"。
        return items;
        // 返回包含所有文件和目录结构的 Item 数组。
    }
}
