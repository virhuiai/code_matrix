package com.virhuiai.Csh7z;

// 工具类导入

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.Md5.MD5FileNameUtils;
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
        //从命令行加载配置参数
        config.loadFromCommandLine();

        String mode = CshCliUtils.s3GetOptionValue("mode", null);
        if(null == mode){
            LOGGER.info("未指定模式！");
        }
        if("genMd5".equalsIgnoreCase(mode)){
             String inputStr =  CshCliUtils.s3GetOptionValue("inputStr", "");
            String extracted = MD5FileNameUtils.extractMD5(inputStr);
            LOGGER.info("extracted:" + extracted);
            return;
        }






        String inDir = config.getConfigValue(Config7z.Keys.INPUT_DIR, "");
        if(null == inDir || inDir.isEmpty()){
//            LOGGER.error("指定要压缩的源目录路径要填写");
            return;
        }

        String output = config.getConfigValue(Config7z.Keys.OUTPUT_FILE, "");
        // 第一步：使用FileUtils7z工具类的wrapStr方法处理密码
        // 该方法可能是对原始密码进行包装或加密处理
        // 从配置中获取以下参数：
        //   - PASSWORD_VALUE: 原始密码值（如果未配置则使用空字符串）
        //   - RANDOM_CHAR_B: 可能用于 混淆的随机字符B（如果未配置则使用空字符串）
        //   - RANDOM_CHAR_A: 可能用于 混淆的随机字符A（如果未配置则使用空字符串）
        String password = FileUtils7z.wrapStr(
                config.getConfigValue(Config7z.Keys.PASSWORD_VALUE, ""),
                config.getConfigValue(Config7z.Keys.RANDOM_CHAR_B, ""),
                config.getConfigValue(Config7z.Keys.RANDOM_CHAR_A, "")
        );
        // 第二步：为处理后的密码添加前缀和后缀
        // 从配置中获取密码前缀和后缀，如果未配置则使用空字符串
        // 最终密码格式为：前缀 + 处理后的密码 + 后缀
        password = config.getConfigValue(Config7z.Keys.PASSWORD_PREFIX, "")
                +  password
                + config.getConfigValue(Config7z.Keys.PASSWORD_SUFFIX, "");

        String showPassword = config.getConfigValue("showPassword", "0");
        if("1".equalsIgnoreCase(showPassword)){
            LOGGER.info("PASSWORD_VALUE: " + config.getConfigValue(Config7z.Keys.PASSWORD_VALUE, ""));
            LOGGER.info("RANDOM_CHAR_B: " + config.getConfigValue(Config7z.Keys.RANDOM_CHAR_B, ""));
            LOGGER.info("RANDOM_CHAR_A: " + config.getConfigValue(Config7z.Keys.RANDOM_CHAR_A, ""));
            LOGGER.info("PASSWORD_PREFIX: " + config.getConfigValue(Config7z.Keys.PASSWORD_PREFIX, ""));
            LOGGER.info("PASSWORD_SUFFIX: " + config.getConfigValue(Config7z.Keys.PASSWORD_SUFFIX, ""));
            LOGGER.info("password: " + password);
        }


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
