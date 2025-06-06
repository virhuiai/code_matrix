package com.virhuiai.Csh7z;

import java.util.HashMap;

import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.Md5.MD5FileNameUtils;
import com.virhuiai.Md5.RandomMD5Utils;
import org.apache.commons.logging.Log;

/**
 * 7z压缩程序配置管理类
 * 继承HashMap实现配置存储和管理
 */
public class Config7z extends HashMap<String, String> {
    private static final Log LOGGER = CshLogUtils.createLogExtended(Config7z.class);

    /**
     * 配置项键名常量
     */
    public static class Keys {


        // 输入输出相关
        public static final String INPUT_DIR = "INPUT_DIR";

        public static final String OUTPUT_FILE = "OUTPUT_FILE";

        // MD5相关
        public static final String RANDOM_MD5 = "MD5_RANDOM";
        public static final String RANDOM_OUT_NAME = "RANDOM_OUT_NAME";

        // 密码相关
        public static final String PASSWORD_VALUE = "PASSWORD_VALUE";
        /**
         * 密码前缀常量
         */
        public static final String PASSWORD_PREFIX = "PASSWORD_PREFIX";

        /**
         * 密码后缀常量
         */
        public static final String PASSWORD_SUFFIX = "PASSWORD_SUFFIX";

        public static final String RANDOM_CHAR_A = "RANDOM_CHAR_A";
        public static final String RANDOM_CHAR_B = "RANDOM_CHAR_B";

        // 压缩选项
        public static final String COMPRESSION_LEVEL = "COMPRESSION_LEVEL";

        // 额外字符相关
        public static final String EXTRA_ENABLED = "EXTRA_ENABLED";
        public static final String EXTRA_COUNT = "EXTRA_COUNT";
    }

    /**
     * 默认值常量
     */
    private static class Defaults {

        static final String DEFAULT_EXTRA_ENABLED = "1";
        static final String DEFAULT_EXTRA_COUNT = "32";
        static final String DEFAULT_COMPRESSION_LEVEL = "0";
    }

    /**
     * 处理额外字符配置
     */
    private void processExtraCharacters(String randomMD5) {
        String extraEnabled = get(Keys.EXTRA_ENABLED);
        String extraCount = get(Keys.EXTRA_COUNT);
        String randomOutName = randomMD5;

        if ("1".equals(extraEnabled)) {
            try {
                int extraCountNum = Integer.parseInt(extraCount);
                if (extraCountNum > 0) {
                    randomOutName = MD5FileNameUtils.insertRandomChars(randomMD5, extraCountNum);
                    String extracted = MD5FileNameUtils.extractMD5(randomOutName);

                    if (!extracted.equals(randomMD5)) {
                        LOGGER.warn("额外字符验证失败，使用原始MD5");
                        randomOutName = randomMD5;
                    }
                }
            } catch (NumberFormatException e) {
                LOGGER.error("额外字符数量格式无效", e);
            }
        }

        put(Keys.RANDOM_OUT_NAME, randomOutName);
        put(Keys.RANDOM_CHAR_A, FileUtils7z.getRandomChars(randomOutName));
        put(Keys.RANDOM_CHAR_B, FileUtils7z.getRandomChars(randomOutName));
    }

    /**
     * 处理密码配置
     * 如果未指定密码，则使用随机MD5作为密码
     */
    private void processPassword() {
        String randomMD5 = get(Keys.RANDOM_MD5);
        String password = CshCliUtils.s3GetOptionValue("p", randomMD5);

        if (!password.equals(randomMD5)) {
            LOGGER.info("使用用户指定的密码");
        } else {
            LOGGER.info("使用随机生成的密码");
        }

        put(Keys.PASSWORD_VALUE, password);
    }

    /**
     * 处理密码前缀设置
     * 如果用户指定了前缀则使用用户指定的，否则使用默认值（空字符串）
     */
    private void processPasswordPrefix() {
//        String defaultValue = get(Keys.PASSWORD_PREFIX);
        String defaultValue = "";
        String prefix = CshCliUtils.s3GetOptionValue("passwordPrefix", defaultValue);

        if (!prefix.equals(defaultValue)) {
            LOGGER.info("使用用户指定的密码前缀");
        } else {
            LOGGER.info("使用默认密码前缀");
        }


        put(Keys.PASSWORD_PREFIX, prefix);
    }

    /**
     * 处理密码后缀设置
     * 如果用户指定了后缀则使用用户指定的，否则使用默认值（空字符串）
     */
    private void processPasswordSuffix() {
//        String defaultValue = get(Keys.PASSWORD_SUFFIX);
        String defaultValue = "";
        String suffix = CshCliUtils.s3GetOptionValue("passwordSuffix", defaultValue);

        if (!suffix.equals(defaultValue)) {
            LOGGER.info("使用用户指定的密码后缀");
        } else {
            LOGGER.info("使用默认密码后缀");
        }

        put(Keys.PASSWORD_SUFFIX, suffix);
    }

    /**
     * 处理压缩等级配置
     * 验证并设置合适的压缩等级
     */
    private void processCompressionLevel() {
        String levelStr = CshCliUtils.s3GetOptionValue("l", Defaults.DEFAULT_COMPRESSION_LEVEL);
        int level;

        try {
            level = validateCompressionLevel(levelStr);
            LOGGER.info("压缩等级: " + level);
        } catch (NumberFormatException e) {
            LOGGER.error("压缩等级格式无效，使用默认值" + Defaults.DEFAULT_COMPRESSION_LEVEL, e);
            level = Integer.parseInt(Defaults.DEFAULT_COMPRESSION_LEVEL);
        }

        Level7z compressionLevel = Level7z.fromLevel(level);
        put(Keys.COMPRESSION_LEVEL, String.valueOf(compressionLevel.getLevel()));

        if (!Level7z.isValidLevel(level)) {
            LOGGER.warn("压缩等级已自动调整为: " + compressionLevel.getLevel());
        }
    }

    /**
     * 验证压缩等级值
     *
     * @param levelStr 压缩等级字符串
     * @return 有效的压缩等级值
     */
    private int validateCompressionLevel(String levelStr) {
        int level = Integer.parseInt(levelStr);
        if (level < 0 || level > 9) {
            LOGGER.warn("压缩等级0 1 3 5 7 9，默认值" + Defaults.DEFAULT_COMPRESSION_LEVEL);
            return Integer.parseInt(Defaults.DEFAULT_COMPRESSION_LEVEL);
        }
        return level;
    }

    /**
     * 生成输出文件路径
     * 如果未指定输出路径，则根据输入路径生成
     */
    public void processOutputFile() {
        String inputDir = get(Keys.INPUT_DIR);
        String randomOutName = get(Keys.RANDOM_OUT_NAME);
        String charA = get(Keys.RANDOM_CHAR_A);
        String charB = get(Keys.RANDOM_CHAR_B);

        String defaultOutput = FileUtils7z.generateParentPath(
                inputDir,
                FileUtils7z.wrapStr(randomOutName, charB, charA)
        );

        String output = CshCliUtils.s3GetOptionValue("o", defaultOutput);
        put(Keys.OUTPUT_FILE, output);
        LOGGER.info("输出文件路径: " + output);
    }

    /**
     * 记录当前配置信息
     */
    private void logConfiguration() {
        LOGGER.info("=== 压缩配置信息 ===");
        LOGGER.info("输入目录: " + get(Keys.INPUT_DIR));
        LOGGER.info("输出文件: " + get(Keys.OUTPUT_FILE));
        LOGGER.info("MD5值: " + get(Keys.RANDOM_MD5));
        LOGGER.info("RANDOM_OUT_NAME: " + get(Keys.RANDOM_OUT_NAME));
        LOGGER.info("压缩等级: " + get(Keys.COMPRESSION_LEVEL));
        LOGGER.info("额外字符启用: " + get(Keys.EXTRA_ENABLED));
        LOGGER.info("额外字符数量: " + get(Keys.EXTRA_COUNT));
        LOGGER.info("==================");
    }

    /**
     * 获取配置值，如果不存在返回默认值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getConfigValue(String key, String defaultValue) {
        return getOrDefault(key, defaultValue);
    }

    /**
     * 获取整数类型的配置值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 整数配置值
     */
    public int getIntConfigValue(String key, int defaultValue) {
        try {
            return Integer.parseInt(getOrDefault(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            LOGGER.warn("配置值转换为整数失败，使用默认值: " + defaultValue, e);
            return defaultValue;
        }
    }



    /**
     * 从命令行加载配置参数
     * 此方法负责解析命令行参数并设置相应的配置值
     */
    public void loadFromCommandLine() {
        // 加载基本配置
        // 设置输入目录，从命令行参数 -i 获取，这是一个必填项
        put(Keys.INPUT_DIR, CshCliUtils.s3GetOptionValue("i", "设置的是必填，此调用方法传优化"));



        // 设置是否启用额外功能，从命令行参数 -e 获取，如未指定则使用默认值
        put(Keys.EXTRA_ENABLED, CshCliUtils.s3GetOptionValue("e", Defaults.DEFAULT_EXTRA_ENABLED));

        // 设置额外计数值，从命令行参数 extraCount 获取，如未指定则使用默认值
        put(Keys.EXTRA_COUNT, CshCliUtils.s3GetOptionValue("extraCount", Defaults.DEFAULT_EXTRA_COUNT));

        // 生成并处理MD5
        // 生成一个随机的简化MD5值
        String randomMD5 = RandomMD5Utils.getRandomMD5Simple();
        // 将生成的MD5值存储到配置中
        put(Keys.RANDOM_MD5, randomMD5);

        // 处理额外字符
        // 基于生成的MD5值处理额外字符的配置
        processExtraCharacters(randomMD5);

        // 处理密码
        // 解析和设置密码相关的配置项
        processPassword();

        // 处理密码前缀设置
        // 解析和设置密码前缀的配置项
        processPasswordPrefix();

        // 处理密码后缀设置
        // 解析和设置密码后缀的配置项
        processPasswordSuffix();

        // 处理压缩等级
        // 解析和设置压缩等级的配置项
        processCompressionLevel();

        // 处理输出文件
        // 解析和设置输出文件的配置项
        processOutputFile();

        // 记录配置日志
        // 将当前的配置项记录到日志中，便于调试和跟踪
        logConfiguration();
    }

}