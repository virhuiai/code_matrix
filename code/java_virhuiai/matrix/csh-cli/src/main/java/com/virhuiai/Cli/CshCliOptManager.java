package com.virhuiai.Cli;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.cli.Option;
import org.apache.commons.logging.Log;

import java.util.List;

public class CshCliOptManager {
    private static final Log LOGGER = LogFactory.getLog();
//    private static final Log LOGGER = CshLogUtils.createLogExtended(CshCliOptManager.class);

    // 私有构造函数，防止实例化
    public CshCliOptManager() {
        throw new AssertionError("工具类不应被实例化");
    }


    /**
     * 添加带参数的选项
     * @param opt 短选项名
     * @param longOpt 长选项名
     * @param description 选项描述
     * @param argName 参数名
     */
    public static void addOptionWithArg(String opt, String longOpt, String description, String argName) {
        CshCliUtils.s2AddOption(options -> {
            try {
                Option.Builder builder;

                // 确定使用哪个 Option.Builder
                if (opt != null && opt.length() == 1) {
                    builder = Option.builder(opt);
                } else {
                    builder = Option.builder(); // 这里可以考虑添加默认的短选项
                }

                // 添加选项
                options.addOption(builder
                        .longOpt(longOpt)
                        .desc(description)
                        .argName(argName)
                        .hasArg() // 确保选项有参数
                        .build());
            } catch (IllegalArgumentException e) {
                LOGGER.error("添加" + description + "选项时出现错误: " + e.getMessage(),e);
            }
        });
    }

    public static void addOptionWithArg(String longOpt, String description, String argName) {
        addOptionWithArg(null, longOpt, description, argName);
    }


    /**
     * 添加不带参数的选项
     * @param opt 短选项名
     * @param longOpt 长选项名
     * @param description 选项描述
     */
    public static void addOptionWithoutArg(String opt, String longOpt, String description) {
        CshCliUtils.s2AddOption(options -> {
            try {
                Option.Builder builder;

                // 确定使用哪个 Option.Builder
                if (opt != null && opt.length() == 1) {
                    builder = Option.builder(opt);
                } else {
                    builder = Option.builder(); // 这里可以考虑添加默认的短选项
                }

                options.addOption(builder
                        .longOpt(longOpt)
                        .desc(description)
                        .build());
            } catch (IllegalArgumentException e) {
                LOGGER.error("添加" + description + "选项时出现错误: " + e.getMessage());
            }
        });
    }

    public static void addOptionWithoutArg(String longOpt, String description) {
        addOptionWithoutArg(longOpt, description);
    }

    /**
     * 检查是否包含指定选项
     *
     * @param opt 选项名称
     * @return 如果包含该选项则返回 true，否则返回 false
     */
    public static boolean hasOption(String opt) {
        CshCliUtils.ensureParsed();
        return OptionsSingleton.INSTANCE.getCmd().hasOption(opt);
    }


    /**
     * 获取所有选项值
     *
     * @param opt 选项名称
     * @return 选项的所有值组成的数组
     */
    public static String[] getOptionValues(String opt) {
        CshCliUtils.ensureParsed();
        return OptionsSingleton.INSTANCE.getCmd().getOptionValues(opt);
    }



    /**
     * 获取所有未被识别为选项的参数
     * @return 未被识别的参数列表
     */
    public static List<String> getUnrecognizedOptions() {
        CshCliUtils.ensureParsed();
        return OptionsSingleton.INSTANCE.getCmd().getArgList();
    }

    /**
     * 验证必需的选项是否存在
     * @param requiredOptions 必需选项的数组
     * @throws IllegalArgumentException 如果缺少任何必需的选项
     */
    public static void validateRequiredOptions(String... requiredOptions) {
        CshCliUtils.ensureParsed();
        for (String option : requiredOptions) {
            if (!hasOption(option)) {
                LOGGER.error("缺少必需的选项: " + option);
                throw new IllegalArgumentException("缺少必需的选项: " + option);
            }
        }
    }


}
