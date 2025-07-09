package com.virhuiai.file;

import java.io.File;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;
public class FileUtils {

    private static final Log LOGGER = LogFactory.getLog();


    /**
     * org.apache.logging.log4j.core.util
     * Retrieves the file extension from the given File.
     * 从给定的文件中获取文件扩展名。
     * @param file the File object
     * 文件对象
     * @return the file extension (e.g., "txt", "log"), or null if no extension is found
     * 文件扩展名（例如，“txt”，“log”），如果没有找到扩展名则返回null
     */
    public static String getFileExtension(final File file) {
        final String fileName = file.getName(); // 获取文件名
        // 检查文件名中是否存在“.”，且“.”不在文件名的开头
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1); // 返回“.”后面的子字符串作为扩展名
        }
        return null; // 如果没有扩展名则返回null
    }

    public static void main(String[] args) {
        String ext = FileUtils.getFileExtension(new File("/Volumes/THAWSPACE/CshProject/code_matrix.git/code/java_virhuiai/2_Csh_Cli/pom.xml"));
        System.out.println(ext);

    }
}
