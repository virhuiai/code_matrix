package com.virhuiai.log.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathCheckUtils {



    /**
     * 检查指定路径是否为有效目录
     *
     * @param directoryPath 目录路径
     * @return 是否为有效目录
     */
    public static boolean checkDir(String directoryPath) {
        Path directory = Paths.get(directoryPath);
        return Files.exists(directory) && Files.isDirectory(directory);
    }

}
