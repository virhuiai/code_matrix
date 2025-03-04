package com.virhuiai.StaticWebServer;

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.cli.Option;
import org.apache.commons.logging.Log;

public class OptionUtilsWeb {
    private static final Log LOGGER = CshLogUtils.createLogExtended(OptionUtilsWeb.class);

    private OptionUtilsWeb() {
        throw new AssertionError("工具类禁止实例化");
    }

    /**
     * 配置帮助选项
     */
    private static void setupHelpOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("显示帮助信息")
                .build()));
    }

    /**
     * 配置try模式选项
     */
    private static void setupTryOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("try")
                .desc("try it")
                .hasArg()
                .argName("试一试_打开")
                .build()));

        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("try_path")
                .desc("try it path")
                .hasArg()
                .argName("试一试_路径")
                .build()));
    }

    /**
     * 配置绑定路径选项
     */
    private static void setupBindPathOption() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("bind_path")
                .desc("逗号分隔，再以冒号分隔")
                .hasArg()
                .argName("bind_path")
                .build()));
    }

    /**
     * 配置根目录选项
     */
    private static void setupRootOptions() {
        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("root_resource")
                .desc("根目录使用resource")
                .hasArg()
                .argName("根目录使用resource")
                .build()));

        CshCliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt("root_path")
                .desc("根目录使用root_path")
                .hasArg()
                .argName("根目录使用root_path")
                .build()));
    }

    /**
     * 设置命令行选项
     */
    public static void setupCommandOptions(String[] args) {
        CshCliUtils.s1InitializeArgs(args);
        LOGGER.debug("接收到的命令行参数: " + String.join(", ", args));

        // 配置所有选项
        setupHelpOption();
        setupTryOption();
        setupBindPathOption();
        setupRootOptions();
    }
}