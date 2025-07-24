package com.virhuiai.compact7z;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

interface  IListFiles {
    default Collection<File> listFiles(String baseDir, String outputFile){

        List<String> SPECIFIC_FILES = Arrays.asList(".DS_Store", "__MACOSX", ".fseventsd");
        List<String> SPECIFIC_PREFIXES = Arrays.asList("._");
        List<String> SPECIFIC_PATHS = Arrays.asList("/__MACOSX/", "/.fseventsd/");
        IOFileFilter SPECIFIC_FILE_FILTER = new IOFileFilter() {
            // Method to check if a file is macOS-specific
            public boolean isMacSpecificFile(File file) {
                String fileName = file.getName();
                String absolutePath = file.getAbsolutePath();
                return baseDir.equalsIgnoreCase(absolutePath)
                        || outputFile.equalsIgnoreCase(absolutePath)
                        ||
                        SPECIFIC_FILES.contains(fileName) ||
                        SPECIFIC_PREFIXES.stream().anyMatch(fileName::startsWith) ||
                        SPECIFIC_PATHS.stream().anyMatch(absolutePath::contains);
            }

            @Override
            public boolean accept(File file) {
                return !isMacSpecificFile(file); // 接受非 macOS 特有文件
            }

            @Override
            public boolean accept(File dir, String name) {
                return !isMacSpecificFile(new File(dir, name)); // 接受非 macOS 特有文件
            }
        };



        // 递归获取目录下所有文件和子目录
        Collection<File> files = FileUtils.listFilesAndDirs(
                new File(baseDir),
                SPECIFIC_FILE_FILTER, // 文件过滤器
                SPECIFIC_FILE_FILTER  // 目录过滤器
        );
        return files;
    }
}
