package com.virhuiai.StaticWebServer;


import com.virhuiai.Cli.CshCliUtils;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.util.HashMap;

public class ConfigWeb extends HashMap<String, String> {
    private static final Log LOGGER = CshLogUtils.createLogExtended(ConfigWeb.class);

    /**
     * 配置项键名常量
     */
    public static class Keys {
        public static final String TRY_ENABLED = "TRY_ENABLED";
        public static final String TRY_PATH = "TRY_PATH";
        public static final String BIND_PATH = "BIND_PATH";
        public static final String ROOT_RESOURCE_ENABLED = "ROOT_RESOURCE_ENABLED";
        public static final String ROOT_PATH = "ROOT_PATH";
    }

    /**
     * 默认值常量
     */
    private static class Defaults {
        static final String DEFAULT_TRY_ENABLED = "0";
        static final String DEFAULT_ROOT_RESOURCE_ENABLED = "0";
    }

    /**
     * 处理Try配置
     */
    private void processTryConfig() {
        String tryEnabled = CshCliUtils.s3GetOptionValue("try", Defaults.DEFAULT_TRY_ENABLED);
        String tryPath = CshCliUtils.s3GetOptionValue("try_path", null);

        put(Keys.TRY_ENABLED, tryEnabled);
        if (tryPath != null) {
            put(Keys.TRY_PATH, tryPath);
        }
    }

    /**
     * 处理绑定路径配置
     */
    private void processBindPath() {
        String bindPath = CshCliUtils.s3GetOptionValue("bind_path", null);
        if (bindPath != null) {
            put(Keys.BIND_PATH, bindPath);
        }
    }

    /**
     * 处理根目录配置
     */
    private void processRootConfig() {
        String rootResourceEnabled = CshCliUtils.s3GetOptionValue("root_resource",
                Defaults.DEFAULT_ROOT_RESOURCE_ENABLED);
        String rootPath = CshCliUtils.s3GetOptionValue("root_path", null);

        put(Keys.ROOT_RESOURCE_ENABLED, rootResourceEnabled);
        if (rootPath != null) {
            put(Keys.ROOT_PATH, rootPath);
        }
    }

    /**
     * 记录当前配置信息
     */
    private void logConfiguration() {
        LOGGER.info("=== 服务器配置信息 ===");
        LOGGER.info("Try模式启用: " + get(Keys.TRY_ENABLED));
        LOGGER.info("Try路径: " + get(Keys.TRY_PATH));
        LOGGER.info("绑定路径: " + get(Keys.BIND_PATH));
        LOGGER.info("使用资源目录: " + get(Keys.ROOT_RESOURCE_ENABLED));
        LOGGER.info("根路径: " + get(Keys.ROOT_PATH));
        LOGGER.info("==================");
    }

    /**
     * 从命令行参数加载配置
     */
    public void loadFromCommandLine() {
        processTryConfig();
        processBindPath();
        processRootConfig();
        logConfiguration();
    }

    /**
     * 获取配置值
     */
    public String getConfigValue(String key, String defaultValue) {
        return getOrDefault(key, defaultValue);
    }

    /**
     * 获取布尔类型配置值
     */
    public boolean getBooleanConfigValue(String key) {
        return "1".equals(get(key));
    }
}
