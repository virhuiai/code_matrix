package com.virhuiai.web_lite.helper;

import com.virhuiai.cli.CliUtils;

import java.util.HashMap;
public class ConfigWeb extends HashMap<String, String> {
    /**
     * 配置项键名常量
     */
    public static class Keys {
        //        public static final String TRY_ENABLED = "TRY_ENABLED";
//        public static final String TRY_PATH = "TRY_PATH";
//        public static final String BIND_PATH = "BIND_PATH";
//        public static final String ROOT_RESOURCE_ENABLED = "ROOT_RESOURCE_ENABLED";
        //ConfigWeb.Keys.ROOT_PATH_LAST
        public static final String ROOT_PATH_LAST = "root_path_last";
        public static final String ROOT_PATH_LAST_RESOURCE = "root_path_last_resource";
    }



    /**
     * 默认值常量
     */
    private static class Defaults {
        //ConfigWeb.Defaults.ROOT_PATH_LAST
        static final String ROOT_PATH_LAST = null;
        static final String ROOT_PATH_LAST_RESOURCE = "0";
//        static final String DEFAULT_TRY_ENABLED = "0";
//        static final String DEFAULT_ROOT_RESOURCE_ENABLED = "0";
    }

    /**
     * 处理根目录配置
     */
    private void processRootPathLastConfig() {
        String ROOT_PATH_LAST = CliUtils.s3GetOptionValue(Keys.ROOT_PATH_LAST,
                Defaults.ROOT_PATH_LAST);

        put(Keys.ROOT_PATH_LAST, ROOT_PATH_LAST);
    }

    private void processRootPathLastResourceConfig() {
        String ROOT_PATH_LAST = CliUtils.s3GetOptionValue(Keys.ROOT_PATH_LAST_RESOURCE,
                Defaults.ROOT_PATH_LAST_RESOURCE);

        put(Keys.ROOT_PATH_LAST_RESOURCE, ROOT_PATH_LAST);
    }

    /**
     * 从命令行参数加载配置
     */
    public void loadFromCommandLine() {
        processRootPathLastConfig();
        processRootPathLastResourceConfig();

//        processTryConfig();
//        processBindPath();
//        processRootConfig();
//        logConfiguration();
    }

    /**
     * 获取配置值
     */
    public String getConfigValue(String key, String defaultValue) {
        return this.getOrDefault(key, defaultValue);
    }
    public String getConfigValue(String key) {
        return this.get(key);
    }
    /**
     * 获取布尔类型配置值
     */
    public boolean getBooleanConfigValueTrueFst(String key) {
        return "1".equals(get(key));
    }

    public boolean getBooleanConfigValueFalseFst(String key) {
        return !"0".equals(get(key));
    }
}
