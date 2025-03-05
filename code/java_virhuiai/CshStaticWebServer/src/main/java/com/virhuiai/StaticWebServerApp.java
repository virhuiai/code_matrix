package com.virhuiai;

import com.sun.net.httpserver.HttpServer;
import com.virhuiai.CshLogUtils.CshLogUtils;
import com.virhuiai.StaticWebServer.ClioptionUtils;
import com.virhuiai.StaticWebServer.CreateContextUtils;
import com.virhuiai.StaticWebServer.ServerUtils;
import com.virhuiai.StaticWebServer.helper.OptionUtilsWeb;
import org.apache.commons.logging.Log;

/**
 * Hello world!
 *
 */
public class StaticWebServerApp
{
    private static Log LOGGER = CshLogUtils.createLogExtended(StaticWebServerApp.class);
    public static void main( String[] args ) throws Exception {
        OptionUtilsWeb.setupCommandOptions(args);
//        // 解析命令行参数
//        ClioptionUtils.parseCmd(args);
//
//        // 初始化服务器
//        HttpServer staticServer = ServerUtils.initServer();
//        // 获取服务器端口
//        int portFrontend_ = ServerUtils.getPort(staticServer);
//
//        // 如果存在 try 选项
//        if (ClioptionUtils.getOptionValue__try()) {
//            // 获取 try 选项对应的路径
//            String try_path = ClioptionUtils.getOptionValue__try_path();
//            // 如果路径不为空
//            if (null!= try_path) {
//                // 创建 try 上下文
//                CreateContextUtils.createContextTry(staticServer, try_path);
//            }
//        }
//
//        // 获取 bind 选项对应的值
//        String bind_path = ClioptionUtils.getOptionValue__bind_path();
//        // 如果 bind 路径不为空
//        if (null!= bind_path) {
//            // 按逗号分割
//            String[] bindArrOne = bind_path.split(",");
//            // 遍历分割后的每个项
//            for (String bindItem : bindArrOne) {
//                // 按冒号分割
//                String[] bindArrTwo = bindItem.split(":");
//                // 如果分割后长度为 2
//                if (2 == bindArrTwo.length) {
//                    // 创建 bind 上下文
//                    CreateContextUtils.createContext__Bind(staticServer, bindArrTwo[0], bindArrTwo[1]);
//                } else {
//                    // 记录错误日志，提示长度不符合要求
//                    LOGGER.error("bind_path.长度不符合要求:" + bindArrTwo.length + ",bindItem:" + bindItem);
//                }
//            }
//        }
//
//        //放在最后, 如果存在 root_resource 选项
//        if (ClioptionUtils.getOptionValue__root_resource()) {
//            // 创建 root 资源的上下文
//            CreateContextUtils.createContextRootLast_Resource(staticServer);
//        } else {
//            // 获取 root_path 选项的值
//            String root_path = ClioptionUtils.getOptionValue__root_path();
//            // 创建 root 路径的上下文
//            CreateContextUtils.createContextRootLast_Path(staticServer, root_path);
//        }
//
//        // 启动服务器
//        staticServer.start();
//
//        // 打印信息
//        LOGGER.error("成功启动:\nhttp://localhost:" + portFrontend_);
    }
}
