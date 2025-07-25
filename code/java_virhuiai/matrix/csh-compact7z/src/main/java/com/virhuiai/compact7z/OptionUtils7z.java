package com.virhuiai.compact7z;

import com.virhuiai.cli.CliUtils;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.cli.Option;

public class OptionUtils7z {
    /**
     * 日志记录器
     * 用于记录程序运行过程中的各种信息
     */
    private static final Log LOGGER = LogFactory.getLog(OptionUtils7z.class);

    private OptionUtils7z() {
        throw new AssertionError("工具类禁止实例化");
    }

    /**
     * 配置输入目录选项
     */
    private static void setupInputDirOption() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder("i")
                .longOpt("inDir")
                .desc("指定要压缩的源目录路径")
                .hasArg()
                .argName("源目录路径")
//                .required() // 设置为必需参数
                .build()));
    }

    /**
     * 配置密码选项
     */
    private static void setupPasswordOption() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder("p")
                .longOpt("password")
                .desc("指定压缩文件的加密密码（若不指定则自动生成）")
                .hasArg()
                .argName("加密密码")
                .build()));
    }

    /**
     * 配置密码前缀选项
     */
    private static void setupPasswordPrefixOption() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("passwordPrefix")
                .desc("指定密码的前缀（若不指定则为空）")
                .hasArg()
                .argName("密码前缀")
                .build()));
    }

    /**
     * 配置密码后缀选项
     */
    private static void setupPasswordSuffixOption() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("passwordSuffix")
                .desc("指定密码的后缀（若不指定则为空）")
                .hasArg()
                .argName("密码后缀")
                .build()));
    }

    /**
     * 配置输出文件选项
     */
    private static void setupOutputOption() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder("o")
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
        CliUtils.s2AddOption(options -> options.addOption(Option.builder("l")
                .longOpt("level")
                .desc("设置压缩等级，" + Level7z.getAvailableLevels())
                .hasArg()
                .argName("压缩等级")
                .type(Number.class)
                .build()));
    }

    /**
     * 配置额外字符相关选项
     */
    private static void setupExtraCharacterOptions() {


    }


    /**
     * 配置输入字符串选项
     */
    private static void setupInputStringOption() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("inputStr")
                .desc("指定输入的字符串，用于处理操作")
                .hasArg()
                .argName("输入字符串")
                .build()));
    }

    /**
     * 配置显示真实密码选项
     */
    private static void setupShowRealPasswordOption() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder("p")
                .longOpt("showPassword")
                .desc("指定程序工作时是否显示真实密码：1=显示，0=不显示（默认）")
                .hasArg()
                .argName("是否显示密码")
                .build()));
    }


    /**
     * OptionUtils7z.setupCommandOptions
     * 设置命令行选项
     * 配置所有可用的命令行参数
     */
    public static void setupCommandOptions(String[] args) {
        CliUtils.s1InitArgs(args);
        LOGGER.debug("接收到的命令行参数: " + String.join(", ", args));

        // 配置输入字符串选项
        setupInputStringOption();

        // 配置输入目录选项
        setupInputDirOption();

        // 配置密码选项
        setupPasswordOption();
        // 配置密码前缀选项// passwordPrefix
        setupPasswordPrefixOption();
        //配置密码后缀选项//passwordSuffix
        setupPasswordSuffixOption();
        //配置显示真实密码选项
        setupShowRealPasswordOption();

        // 配置输出文件选项
        setupOutputOption();
        // 配置压缩等级选项
        setupCompressionLevelOption();

    }
}
