package com.virhuiai.Cli;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;

import java.util.List;
import java.util.function.Consumer;

/**
 * 命令行选项工具类：已声明为 final，并添加私有构造函数，防止类被继承或实例化。
 * 提供了添加选项、解析命令行参数、获取选项值等功能
 */
public final class CshCliUtils {
    private static final Log LOGGER = LogFactory.getLog();
//    private static final Log LOGGER = LogFactory.getLog(CshCliUtils.class);

    // 私有构造函数，防止实例化
    private CshCliUtils() {
        throw new AssertionError("工具类不应被实例化");
    }

    private static String[] args; // 存储命令行参数
    private static boolean argsInitialized = false; // 标记args是否已初始化


    /**
     * 初始化命令行参数，只能调用一次
     *
     * @param commandLineArgs 命令行参数数组
     * @throws IllegalStateException 如果方法被多次调用
     */
    public static synchronized void s1InitializeArgs(String[] commandLineArgs) {
        if (argsInitialized) {
            LOGGER.warn("命令行参数已经初始化，不能重复调用此方法");
            return;
        }
        args = commandLineArgs;
        argsInitialized = true;
        LOGGER.info("命令行参数已成功初始化");
    }

    /**
     * 添加命令行选项
     *
     * @param optionAdder 选项添加器，用于定义和添加选项
     */
    public static void s2AddOption(Consumer<Options> optionAdder) {
        // 获取单例中的 Options 对象
        Options options = OptionsSingleton.INSTANCE.getOptions();
        // 调用传入的函数来添加选项
        optionAdder.accept(options);
    }

    /**
     * 获取选项值
     *
     * @param opt 选项名称
     * @param defaultValue 默认值，如果选项未设置则返回此值
     * @return 选项值或默认值
     */
    public static String s3GetOptionValue(String opt, String defaultValue) {
        ensureParsed();
        return OptionsSingleton.INSTANCE.getCmd().getOptionValue(opt, defaultValue);
    }

    /**
     * 获取命令行选项值(无默认值)
     *
     * @param opt 命令行选项的名称
     * @return 命令行选项的值,如果未设置则返回null
     */
    public static String s3GetOptionValue(String opt) {
        ensureParsed();
        return OptionsSingleton.INSTANCE.getCmd().getOptionValue(opt);
    }


    /**
     * 解析命令行参数
     *
     * @return 解析后的 CommandLine 对象
     * @throws ParseException 如果解析过程中发生错误
     * @throws IllegalStateException 如果命令行参数尚未初始化
     */
    public static CommandLine parseCmd() throws ParseException {
        if (!argsInitialized) {
            throw new IllegalStateException("命令行参数尚未初始化，请先调用 initializeArgs 方法");
        }
        CommandLine cmd = OptionsSingleton.INSTANCE.parseCmd(args);
        if (cmd.hasOption("help")) {
            printHelp();
        }
        return cmd;
    }

    /**
     * 确保命令行参数已被解析
     */
    public static void ensureParsed() {
        if (!OptionsSingleton.INSTANCE.isParsed()) {
            try {
                if (!argsInitialized) {
                    throw new IllegalStateException("命令行参数尚未初始化，请先调用 initializeArgs 方法");
                }
                parseCmd();
            } catch (ParseException e) {
                LOGGER.error("自动解析命令行参数失败", e);
                throw new IllegalStateException("自动解析命令行参数失败", e);
            }
        }
    }



    /**
     * 打印帮助信息并退出程序
     */
    public static void printHelp() {
        OptionsSingleton.INSTANCE.printHelp(" "); // 可以 替换 "My程序" 为实际的程序名
        System.exit(0); // 正常退出
    }






}