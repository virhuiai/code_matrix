package com.virhuiai.log.web_lite.example;

import com.sun.net.httpserver.HttpServer;
import com.virhuiai.cli.CliUtils;
import com.virhuiai.log.logext.LogFactory;
import com.virhuiai.log.web_lite.CreateContextUtils;
import com.virhuiai.log.web_lite.Opt;
import com.virhuiai.log.web_lite.ServerUtils;
import org.apache.commons.logging.Log;

/**
 * Hello world!
 *
 */
public class StaticWebServerApp
{
    private static final Log LOGGER = LogFactory.getLog(StaticWebServerApp.class);
    private static final String YES_1 = "1";
    public static void main( String[] args ) throws Exception {


        // 解析命令行参数
        CliUtils.s1InitArgs(args);
        // 配置所有选项
        for (Opt value : Opt.values()) {
            CliUtils.s2AddOption(options -> options.addOption(value.getOption()));
        }

        // 初始化服务器
        HttpServer staticServer = ServerUtils.initServer();
        // 获取服务器端口
        int portFrontend_ = ServerUtils.getPort(staticServer);

        // --web-lite.bind_try_path
        String bind_try_path = CliUtils.s3GetOptionValue(Opt.BIND_TRY_PATH.getOptionName());
        // 如果 bind 路径不为空
        if (null != bind_try_path) {
            // 按逗号分割
            String[] bindArrOne = bind_try_path.split(",");
            // 遍历分割后的每个项
            for (String bindItem : bindArrOne) {
                // 按冒号分割
                String[] bindArrTwo = bindItem.split(":");
                // 如果分割后长度为 2
//                if (2 == bindArrTwo.length) {
//                    // 创建 bind 上下文
//                    CreateContextUtils.createContextTry(staticServer, bindArrTwo[0], bindArrTwo[1]);
//                } else {
//                    // 记录错误日志，提示长度不符合要求
//                    LOGGER.error("bind_path.长度不符合要求:" + bindArrTwo.length + ",bindItem:" + bindItem);
//                }
                String try_path = bindArrTwo[0];
                if (null != try_path) {
                    // 创建 try 上下文
                    CreateContextUtils.createContextTry(staticServer, try_path);
                }
            }
        }


        // 获取 bind 选项对应的值 web-lite.bind_path
        String bind_path = CliUtils.s3GetOptionValue(Opt.BIND_PATH.getOptionName());
        // 如果 bind 路径不为空
        if (null!= bind_path) {
            // 按逗号分割
            String[] bindArrOne = bind_path.split(",");
            // 遍历分割后的每个项
            for (String bindItem : bindArrOne) {
                // 按冒号分割
                String[] bindArrTwo = bindItem.split(":");
                // 如果分割后长度为 2
                if (2 == bindArrTwo.length) {
                    // 创建 bind 上下文
                    CreateContextUtils.createContext__Bind(staticServer, bindArrTwo[0], bindArrTwo[1]);
                } else {
                    // 记录错误日志，提示长度不符合要求
                    LOGGER.error("bind_path.长度不符合要求:" + bindArrTwo.length + ",bindItem:" + bindItem);
                }
            }
        }

        //放在最后
        // 命令行参数: --web-lite.root_path_last=/Volumes/RamDisk/a
        String ROOT_PATH_LAST = CliUtils.s3GetOptionValue(Opt.ROOT_PATH_LAST.getOptionName());
        if(null != ROOT_PATH_LAST){
            LOGGER.info("ROOT_PATH_LAST:" + ROOT_PATH_LAST);
            // 创建 root 路径的上下文
            CreateContextUtils.createContextRootLast_Path(staticServer, ROOT_PATH_LAST);
        }else{
            // 命令行参数: --web-lite.use-root_path_last_resource=1
            String ROOT_PATH_LAST_RESOURCE = CliUtils.s3GetOptionValue(Opt.ROOT_PATH_LAST_RESOURCE.getOptionName());
            if(YES_1.equals(ROOT_PATH_LAST_RESOURCE)){
                // 创建 root 路径的上下文
                CreateContextUtils.createContextRootLast_Resource(staticServer);
            }
        }

        //
        // 启动服务器
        staticServer.start();

        // 打印信息
        LOGGER.info("成功启动:\nhttp://localhost:" + portFrontend_ + "/index.html");
    }
}
