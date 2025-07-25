// 定义包名，指明该类所在的包路径
package com.virhuiai.file;

// 导入所需的类

import com.virhuiai.file.obj.SizeUnit;
import com.virhuiai.log.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 定义文件工具类
public class FileCheckUtils {

    // 创建日志对象，用于记录操作日志
    // 使用CshLogUtils工具类创建日志实例，传入当前类作为参数
    private static final Log LOGGER = LogFactory.getLog(FileCheckUtils.class);



    /**
     * 检查文件是否存在，不存在则抛出异常
     * 此方法用于在文件操作前进行文件存在性检查
     *
     * @param filePath 文件路径
     * @return 文件名
     * @throws IOException 如果文件不存在
     */
    public static String checkFileOrEx(String filePath) throws IOException {
        // 将字符串路径转换为Path对象
        Path path = Paths.get(filePath);

        // 检查文件是否存在
        // 如果文件不存在，记录错误日志并抛出IOException
        if (!Files.exists(path)) {
            LOGGER.error("文件不存在: " + filePath);
            throw new IOException("文件不存在: " + filePath);
        }

        // 返回文件名（不包含路径）
        return path.getFileName().toString();
    }

    /**
     * 仅检查文件是否存在
     * 此方法是一个简单的文件存在性检查，不抛出异常
     *
     * @param filePath 文件路径
     * @return boolean 文件存在返回true，否则返回false
     */
    public static boolean checkFileOnly(String filePath) {
        // 将字符串路径转换为Path对象
        Path path = Paths.get(filePath);

        // 返回文件是否存在的布尔值
        return Files.exists(path);
    }




    public static void main(String[] args) throws IOException {
        int type;
//        type = 0;//validateFileAndGetSize
//        type = 1;//formatFileSize
//        type = 2;//formatFileSizeAuto
        type = 3;//

        if(3 == type){
            FileInfoUtils.getFileInfo("/Volumes/RamDisk/example.xlsx");
        }else if(2 == type){
            long rs = FileSizeUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            String rs1 = FileSizeUtils.formatFileSizeAuto(rs);
            LOGGER.info("formatFileSize,rs1:" + rs1);
        }else if(1 == type){
            long rs = FileSizeUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            String rs1 = FileSizeUtils.formatFileSize(rs, SizeUnit.MB);
            LOGGER.info("formatFileSize,rs1:" + rs1);

        } else if(0 == type){
            long rs = FileSizeUtils.validateFileAndGetSize("/Volumes/RamDisk/example.xlsx");
            LOGGER.info("validateFileAndGetSize,rs:" + rs);
        }

    }
}
