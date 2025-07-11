package com.virhuiai.web_lite.helper;


import com.virhuiai.cli.CliUtils;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.cli.Option;
import org.apache.commons.logging.Log;

public class OptionUtilsWeb {
    private static final Log LOGGER = LogFactory.getLog(OptionUtilsWeb.class);

    private OptionUtilsWeb() {
        throw new AssertionError("工具类禁止实例化");
    }

    /**
     * 配置 root_path_last 选项
     */
    private static void setupRootPathLastOptions() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt(ConfigWeb.Keys.ROOT_PATH_LAST)
                .desc("根目录使用root_path_last")
                .hasArg()
                .argName("根目录使用root_path_last")
                .build()));
    }

    private static void setupRootPathLastResourceOptions() {
        CliUtils.s2AddOption(options -> options.addOption(Option.builder()
                .longOpt(ConfigWeb.Keys.ROOT_PATH_LAST_RESOURCE)
                .desc("根目录使用jar包的resource")
                .hasArg()
                .argName("根目录使用jar包的resource")
                .build()));
    }

//     CliUtils.s2AddOption(options -> options.addOption(Option.builder()
//            .longOpt("root_resource")
//                .desc("根目录使用resource")
//                .hasArg()
//                .argName("根目录使用resource")
//                .build()));

    /**
     * 设置命令行选项
     * OptionUtilsWeb.setupCommandOptions
     */
    public static void s1InitAndS2AddOptions(String[] args) {
        CliUtils.s1InitArgs(args);
        LOGGER.debug("接收到的命令行参数: " + String.join(", ", args));
        // 配置所有选项
        setupRootPathLastOptions();
        setupRootPathLastResourceOptions();// 和上一条互斥

    }



}
