package com.virhuiai.StaticWebServer;

import com.sun.net.httpserver.HttpServer;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

public class StaticWebServerApp {
    private static final Log LOGGER = CshLogUtils.createLogExtended(StaticWebServerApp.class);

    public static void main(String[] args) throws Exception {
        // 设置命令行选项
        OptionUtilsWeb.setupCommandOptions(args);

        // 加载配置
        ConfigWeb config = new ConfigWeb();
        config.loadFromCommandLine();

        // 初始化服务器
        HttpServer staticServer = ServerUtils.initServer();
        int portFrontend = ServerUtils.getPort(staticServer);

        // 处理try模式配置
        if (config.getBooleanConfigValue(ConfigWeb.Keys.TRY_ENABLED)) {
            String tryPath = config.getConfigValue(ConfigWeb.Keys.TRY_PATH, null);
            if (tryPath != null) {
                CreateContextUtils.createContextTry(staticServer, tryPath);
            }
        }

        // 处理绑定路径配置
        String bindPath = config.getConfigValue(ConfigWeb.Keys.BIND_PATH, null);
        if (bindPath != null) {
            String[] bindArrOne = bindPath.split(",");
            for (String bindItem : bindArrOne) {
                String[] bindArrTwo = bindItem.split(":");
                if (bindArrTwo.length == 2) {
                    CreateContextUtils.createContext__Bind(staticServer, bindArrTwo[0], bindArrTwo[1]);
                } else {
                    LOGGER.error("bind_path长度不符合要求:" + bindArrTwo.length + ",bindItem:" + bindItem);
                }
            }
        }

        // 处理根目录配置
        if (config.getBooleanConfigValue(ConfigWeb.Keys.ROOT_RESOURCE_ENABLED)) {
            CreateContextUtils.createContextRootLast_Resource(staticServer);
        } else {
            String rootPath = config.getConfigValue(ConfigWeb.Keys.ROOT_PATH, null);
            CreateContextUtils.createContextRootLast_Path(staticServer, rootPath);
        }

        // 启动服务器
        staticServer.start();
        LOGGER.info("成功启动:\nhttp://localhost:" + portFrontend);
    }
}