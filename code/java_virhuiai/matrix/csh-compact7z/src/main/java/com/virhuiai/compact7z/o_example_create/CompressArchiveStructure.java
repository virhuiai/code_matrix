package com.virhuiai.compact7z.o_example_create;

import java.io.File;
import java.util.Random;

/**
 * Some archive formats like GZip only support compression of a single file, while other archive formats allow multiple files and folders to be compressed. In order to demonstrate how those archives can be created, some test file and folder structure is required. The following snippets use a static structure defined by the CompressArchiveStructure class:
 */
public class CompressArchiveStructure {
    public static Item[] create() {

        //     <root>
        //     |
        //     +- info.txt
        //     +- random-100-bytes.dump
        //     +- dir1
        //     |  +- file-in-a-directory1.txt
        //     +- dir2
        //        +- file-in-a-directory2.txt

        Item[] items = new Item[5];

        items[0] = new Item("info.txt", "This is the info");

        byte[] content = new byte[100];
        new Random().nextBytes(content);
        items[1] = new Item("random-100-bytes.dump", content);

        // dir1 doesn't have separate archive item
        items[2] = new Item("dir1" + File.separator + "file1.txt",
                "This file located in a directory 'dir'");

        // dir2 does have separate archive item
        items[3] = new Item("dir2" + File.separator, (byte[]) null);
        items[4] = new Item("dir2" + File.separator + "file2.txt",
                "This file located in a directory 'dir'");
        return items;
    }
}
