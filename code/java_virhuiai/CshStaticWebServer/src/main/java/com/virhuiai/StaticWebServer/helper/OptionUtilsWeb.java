package com.virhuiai.StaticWebServer.helper;

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

public class OptionUtilsWeb {
    private static final Log LOGGER = CshLogUtils.createLogExtended(OptionUtilsWeb.class);

    private OptionUtilsWeb() {
        throw new AssertionError("工具类禁止实例化");
    }

    /**
     * 设置命令行选项
     * OptionUtilsWeb.setupCommandOptions
     */
    public static void setupCommandOptions(String[] args) {
        CshCliUtils.s1InitializeArgs(args);
        LOGGER.debug("接收到的命令行参数: " + String.join(", ", args));
    }

}
