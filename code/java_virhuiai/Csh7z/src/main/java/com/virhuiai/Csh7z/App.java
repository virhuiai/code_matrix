package com.virhuiai.Csh7z;

// 工具类导入

import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.io.File;

/**
 * 7z压缩程序主类
 * 提供命令行界面进行文件压缩操作
 * 支持的命令行参数：
 * -h: 显示帮助信息
 * -e: 是否在文件名中插入额外字符(1:是, 0:否)
 * --extraCount: 插入额外字符的数量
 *
 * @author virhuiai
 * @version 1.0
 */
public class App {
    /**
     * 日志记录器
     * 用于记录程序运行过程中的各种信息
     */
    private static final Log LOGGER = CshLogUtils.createLogExtended(App.class);


    // 使用示例
    public static void main(String[] args) {
        // 初始化命令行工具并设置参数选项
        OptionUtils7z.setupCommandOptions(args);;

        // 创建并加载配置
        Config7z config = new Config7z();
        config.loadFromCommandLine();


        String inDir = config.getConfigValue(Config7z.Keys.INPUT_DIR, "");
        if(null == inDir || inDir.isEmpty()){
//            LOGGER.error("指定要压缩的源目录路径要填写");
            return;
        }

        String output = config.getConfigValue(Config7z.Keys.OUTPUT_FILE, "");
        String password = FileUtils7z.wrapStr(
                config.getConfigValue(Config7z.Keys.PASSWORD, ""),
                config.getConfigValue(Config7z.Keys.RANDOM_CHAR_B, ""),
                config.getConfigValue(Config7z.Keys.RANDOM_CHAR_A, "")
        );
        Integer compressionLevel = config.getIntConfigValue(Config7z.Keys.COMPRESSION_LEVEL, 0);

        LOGGER.info("输出文件: " + output);


        try {
            File inputDir = new File(inDir); // 要压缩的目录
            File outputFile = new File(output); // 输出的7z文件


            Csh7zUtils.compress(inputDir, outputFile
                    , password
                    , compressionLevel);

            LOGGER.info("压缩完成！");

        } catch (Exception e) {
            LOGGER.error("压缩失败：" + e.getMessage(),e);
        }
    }
}
