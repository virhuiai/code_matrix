package com.virhuiai.Csh7z;

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.cli.Option;
import org.apache.commons.logging.Log;

public class OptionUtils7z {
    /**
     * 日志记录器
     * 用于记录程序运行过程中的各种信息
     */
    private static final Log LOGGER = CshLogUtils.createLogExtended(OptionUtils7z.class);

    private OptionUtils7z() {
        throw new AssertionError("工具类禁止实例化");
    }

    /**
     * 配置输入目录选项
     */
    private static void setupInputDirOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("i")
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
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("p")
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
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
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
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
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
     * 配置模式选项
     */
    private static void setupModeOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("m")
                .longOpt("mode")
                .desc("指定操作模式：genMd5=去除非MD5字符，compress=压缩文件")
                .hasArg()
                .argName("操作模式")
//                .required(true)
                .build()));
        // ，extract=解压文件
    }


    /**
     * 配置输入字符串选项
     */
    private static void setupInputStringOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
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
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("p")
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
        CshCliUtils.s1InitializeArgs(args);
        LOGGER.debug("接收到的命令行参数: " + String.join(", ", args));
        // 配置模式选项
        setupModeOption();
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
        // 配置额外字符选项
        setupExtraCharacterOptions();
    }
}
