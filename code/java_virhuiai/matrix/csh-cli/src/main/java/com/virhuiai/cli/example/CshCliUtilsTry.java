package com.virhuiai.cli.example;

import com.virhuiai.cli.CliUtils;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;


import org.apache.commons.cli.Option;

public class CshCliUtilsTry {
//    private static final Log LOGGER = CshLogUtils.createLogExtended(CshCliUtilsTry.class);
    private static final Log LOGGER = LogFactory.getLog();

    /**
     * 主方法，用于测试和演示
     * idea配置测试参数： -m client -h
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        LOGGER.info("开始执行主方法");

        try {
            CliUtils.s1InitArgs(args);
            LOGGER.debug("调试信息：命令行参数 " + String.join(", ", args));

            // 添加模式选项
            CliUtils.s2AddOption(options -> options.addOption(Option.builder("m")
                    .longOpt("mode")
                    .desc("模式 (server或client)")
                    .hasArg()
                    .argName("模式")
                    .build()));

            // 添加帮助选项
            //CshClioptionUtils.addOption(options -> options.addOption("h", "help", false, "显示帮助信息"));

            String mode = CliUtils.s3GetOptionValue("mode", "default");
            LOGGER.info("选择的模式: " + mode);

            String testOption = CliUtils.s3GetOptionValue("abc", "默认值");
            LOGGER.info("测试选项值: " + testOption);
        } catch (IllegalStateException e) {
            LOGGER.error("初始化或获取选项值失败", e);
            CliUtils.printHelp();
        }

        LOGGER.info("主方法执行完毕");
    }
}
