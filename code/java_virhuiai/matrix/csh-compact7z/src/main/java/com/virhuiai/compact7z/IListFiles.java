package com.virhuiai.compact7z;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// 接口 IListFiles 定义了列出文件的功能
// 主要用于递归获取指定目录下的文件和子目录，过滤掉 macOS 特有文件
interface  IListFiles {
    // 默认方法 listFiles，用于列出指定目录下符合条件的文件和目录
    // 方法功能：递归遍历指定目录，排除 macOS 特有文件及指定输出文件，返回符合条件的文件和目录集合
    default Collection<File> listFiles(String baseDir, String outputFile){
        // 参数说明：
        // - baseDir: 待扫描的根目录路径
        // - outputFile: 需要排除的输出文件路径（避免包含输出文件本身）
        // 返回值：Collection<File> - 包含所有符合条件的文件和目录的集合

        // 定义 macOS 特有文件的名称列表
        List<String> SPECIFIC_FILES = Arrays.asList(".DS_Store", "__MACOSX", ".fseventsd");
        // 用途：存储常见的 macOS 系统文件名称，用于过滤
        // 说明：这些文件通常由 macOS 系统生成，如 .DS_Store 用于存储 Finder 视图设置

        // 定义 macOS 特有文件名前缀列表
        List<String> SPECIFIC_PREFIXES = Arrays.asList("._");
        // 用途：存储 macOS 特有文件名前缀（如 AppleDouble 文件），用于过滤
        // 说明：以 "._" 开头的文件通常是 macOS 存储元数据的隐藏文件

        // 定义 macOS 特有路径片段列表
        List<String> SPECIFIC_PATHS = Arrays.asList("/__MACOSX/", "/.fseventsd/");
        // 用途：存储 macOS 特有目录路径片段，用于过滤
        // 说明：这些路径通常出现在 macOS 解压或系统事件日志中

        // 定义文件过滤器，实现 IOFileFilter 接口
        IOFileFilter SPECIFIC_FILE_FILTER = new IOFileFilter() {
            // 方法 isMacSpecificFile：检查文件是否为 macOS 特有文件
            // 功能：通过文件名和路径判断文件是否需要被过滤
            // 参数：file - 要检查的文件对象
            // 返回值：boolean - 如果是 macOS 特有文件或需要排除的文件，返回 true
            // Method to check if a file is macOS-specific
            public boolean isMacSpecificFile(File file) {
                // 获取文件名
                String fileName = file.getName();
                // 用途：用于与 macOS 特有文件名和前缀比较
                // 获取文件的绝对路径
                String absolutePath = file.getAbsolutePath();
                // 用途：用于检查路径是否包含 macOS 特有目录
                // 执行流程：
                // 1. 检查文件路径是否与 baseDir 或 outputFile 相同（避免包含根目录或输出文件）
                // 2. 检查文件名是否在 SPECIFIC_FILES 列表中
                // 3. 检查文件名是否以 SPECIFIC_PREFIXES 中的前缀开头
                // 4. 检查文件路径是否包含 SPECIFIC_PATHS 中的路径片段
                return baseDir.equalsIgnoreCase(absolutePath)
                        || outputFile.equalsIgnoreCase(absolutePath)
                        || SPECIFIC_FILES.contains(fileName)
                        || SPECIFIC_PREFIXES.stream().anyMatch(fileName::startsWith)
                        || SPECIFIC_PATHS.stream().anyMatch(absolutePath::contains);
            }

            // 重写 accept 方法，用于文件过滤
            // 功能：决定是否接受指定文件，排除 macOS 特有文件
            // 参数：file - 要检查的文件对象
            // 返回值：boolean - 如果不是 macOS 特有文件，返回 true
            @Override
            public boolean accept(File file) {
                return !isMacSpecificFile(file); // 接受非 macOS 特有文件
                // 注意事项：通过取反 isMacSpecificFile 的结果，决定是否接受文件
            }

            // 重写 accept 方法，用于目录中的文件过滤
            // 功能：检查目录中的指定文件名是否为 macOS 特有文件
            // 参数：
            // - dir: 父目录对象
            // - name: 目录中的文件名
            // 返回值：boolean - 如果不是 macOS 特有文件，返回 true
            @Override
            public boolean accept(File dir, String name) {
                return !isMacSpecificFile(new File(dir, name)); // 接受非 macOS 特有文件
                // 执行流程：构造新的 File 对象并调用 isMacSpecificFile 进行检查
            }
        };
        // 过滤器说明：
        // SPECIFIC_FILE_FILTER 用于过滤文件和目录，确保排除 macOS 特有文件
        // 注意事项：过滤器同时用于文件和目录的筛选，确保递归遍历时一致性

        // 递归获取目录下所有文件和子目录
        // 使用 Apache Commons IO 的 FileUtils.listFilesAndDirs 方法
        Collection<File> files = FileUtils.listFilesAndDirs(
                new File(baseDir),
                SPECIFIC_FILE_FILTER, // 文件过滤器
                // 用途：筛选非 macOS 特有文件
                SPECIFIC_FILE_FILTER  // 目录过滤器
                // 用途：筛选非 macOS 特有目录
        );
        // 执行流程：
        // 1. 将 baseDir 转换为 File 对象作为扫描起点
        // 2. 使用 SPECIFIC_FILE_FILTER 过滤文件和目录
        // 3. 返回包含所有符合条件的文件和目录的集合
        // 注意事项：返回结果包括目录本身和所有符合条件的文件

        // 返回符合条件的文件和目录集合
        return files;
        // 返回值说明：Collection<File> 包含所有非 macOS 特有文件和目录
        // 注意事项：调用者需处理返回的集合，可能需要进一步筛选或处理
    }
}
