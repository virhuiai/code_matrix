package com.virhuiai.compact7z;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Collection;

public class Test {

    // 判断是否为 macOS 特有文件
    private static boolean isMacSpecificFile(File file) {
        String fileName = file.getName();
        return fileName.equals(".DS_Store") ||
                fileName.startsWith("._") ||
                fileName.equals("__MACOSX") ||
                fileName.equals(".fseventsd") ||
                file.getAbsolutePath().contains("/__MACOSX/") ||
                file.getAbsolutePath().contains("/.fseventsd/");
    }
    // 自定义文件过滤器，排除 macOS 特有文件
    private static final IOFileFilter MAC_SPECIFIC_FILE_FILTER = new IOFileFilter() {
        @Override
        public boolean accept(File file) {
            return !isMacSpecificFile(file); // 接受非 macOS 特有文件
        }

        @Override
        public boolean accept(File dir, String name) {
            return !isMacSpecificFile(new File(dir, name)); // 接受非 macOS 特有文件
        }
    };


    public static void main(String[] args) {
        // 递归获取目录下所有文件和子目录
//        Collection<File> files = FileUtils.listFilesAndDirs(
//                new File("/Volumes/RamDisk"),
//                MAC_SPECIFIC_FILE_FILTER, // 使用自定义文件过滤器
//                MAC_SPECIFIC_FILE_FILTER  // 使用自定义目录过滤器
//        );

        Collection<File> files = FileUtils.listFiles(
                new File("/Volumes/RamDisk"),
                MAC_SPECIFIC_FILE_FILTER, // 使用自定义文件过滤器
                MAC_SPECIFIC_FILE_FILTER  // 使用自定义目录过滤器
        );
//        for (File file : files) {
//            System.out.printf(files);
//        }
        int ad = 3;
    }
}
