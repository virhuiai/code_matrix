package com.virhuiai.Csh7z;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils7z {

    // 私有构造函数，防止实例化
    private FileUtils7z() {
        throw new AssertionError("工具类不应被实例化");
    }

    public static String generateParentPath(String directoryPath, String fileName) {
        Path path = Paths.get(directoryPath);
        Path parentPath = path.getParent();
        if (parentPath == null) {
            return fileName + ".7z";
        }
        return parentPath.resolve(fileName + ".7z").toString();
    }
}
