package com.virhuiai.Csh7z;

// 工具类导入
import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.Md5.MD5FileNameUtils;
import com.virhuiai.Md5.RandomMD5Utils;
// Apache Commons 相关导入
import org.apache.commons.cli.Option;
import org.apache.commons.logging.Log;
// Java 基础类导入
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

    /**
     * 配置输入目录选项
     */
    private static void setupInputDirOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("i")
                .longOpt("inDir")
                .desc("指定要压缩的源目录路径")
                .hasArg()
                .argName("源目录路径")
                .required() // 设置为必需参数
                .build()));
    }

    /**
     * 配置密码选项
     */
    private static void setupPasswordOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("p")
                .longOpt("password")
                .desc("指定压缩文件的加密密码（若不指定则自动生成）")
                .hasArg()
                .argName("加密密码")
                .build()));
    }

    /**
     * 配置输出文件选项
     */
    private static void setupOutputOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("o")
                .longOpt("output")
                .desc("指定输出的7z文件路径（默认在源目录下生成）")
                .hasArg()
                .argName("输出路径")
                .build()));
    }

    /**
     * 配置压缩等级选项
     */
    private static void setupCompressionLevelOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("l")
                .longOpt("level")
                .desc("设置压缩等级 (可用值: " + CompressionLevel.getAvailableLevels() + ")")
                .hasArg()
                .argName("压缩等级")
                .type(Number.class)
                .build()));
    }

    /**
     * 配置额外字符相关选项
     */
    private static void setupExtraCharacterOptions() {
        // 是否插入额外字符选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("e")
                .longOpt("extra")
                .desc("是否在文件名中插入额外字符 (0:否, 1:是)")
                .hasArg()
                .argName("是否启用")
                .type(Number.class) // 指定参数类型为数字
                .build()));

        // 额外字符数量选项
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("extraCount")
                .desc("指定要插入的额外字符数量")
                .hasArg()
                .argName("字符数量")
                .type(Number.class)
                .build()));
    }

    /**
     * 设置命令行选项
     * 配置所有可用的命令行参数
     */
    private static void setupCommandOptions() {
        // 配置输入目录选项
        setupInputDirOption();
        // 配置密码选项
        setupPasswordOption();
        // 配置输出文件选项
        setupOutputOption();
        // 配置压缩等级选项
        setupCompressionLevelOption();
        // 配置额外字符选项
        setupExtraCharacterOptions();
    }

    /**
     * 初始化命令行工具并设置参数选项
     *
     * @param args 命令行参数数组
     */
    private static void initializeCommandLine(String[] args) {
        CshCliUtils.s1InitializeArgs(args);
        LOGGER.debug("接收到的命令行参数: " + String.join(", ", args));
        setupCommandOptions();
    }



    // 使用示例
    public static void main(String[] args) {
        // 初始化命令行工具
        initializeCommandLine(args);
        // 设置命令行选项
        setupCommandOptions();

        // 创建并加载配置
        CompressionConfig config = new CompressionConfig();
        config.loadFromCommandLine();


        String inDir = config.getConfigValue(CompressionConfig.Keys.INPUT_DIR, "");
        String output = config.getConfigValue(CompressionConfig.Keys.OUTPUT_FILE, "");
        String password = FileUtils7z.wrapStr(
                config.getConfigValue(CompressionConfig.Keys.PASSWORD, ""),
                config.getConfigValue(CompressionConfig.Keys.RANDOM_CHAR_B, ""),
                config.getConfigValue(CompressionConfig.Keys.RANDOM_CHAR_A, "")
        );
        Integer compressionLevel = config.getIntConfigValue(CompressionConfig.Keys.COMPRESSION_LEVEL, 0);

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
