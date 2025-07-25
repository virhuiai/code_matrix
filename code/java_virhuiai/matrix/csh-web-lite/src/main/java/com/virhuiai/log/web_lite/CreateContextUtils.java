package com.virhuiai.log.web_lite;

import com.sun.net.httpserver.HttpServer;
import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateContextUtils {
    private static final Log LOGGER = LogFactory.getLog(CreateContextUtils.class);

    /**
     * 参数 --root_path_last=/Volumes/RamDisk/a
     * 启动后 在 a目录下放置 .html 文件即可以访问
     *
     * @param server
     * @param root_path_str
     */
    public static void createContextRootLast_Path(HttpServer server, String root_path_str) {
        LOGGER.info("【调用】CreateContextUtils.createContextRootLast_Path");
        server.createContext("/", exchange -> {
            try {
                // 获取请求的相对路径
                String requestPath = exchange.getRequestURI().getPath();

                // 将请求路径与根目录组合,得到完整的文件路径
                Path rootPath = Paths.get(root_path_str);
                Path fullPath = rootPath.resolve(requestPath.substring(1)); // 去掉路径开头的"/"

                // 安全检查:确保请求的文件路径在根目录下
                if (!fullPath.normalize().startsWith(rootPath.normalize())) {
                    LOGGER.warn("检测到非法的路径访问尝试:" + requestPath);
                    SendUtils.sendNotFound(exchange, "非法的路径访问");
                    return;
                }

                // 检查文件是否存在且不是目录
                if (Files.exists(fullPath) && !Files.isDirectory(fullPath)) {
                    LOGGER.debug("找到文件:" + fullPath);
                    SendUtils.sendFile(exchange, fullPath);
                } else {
                    LOGGER.debug("文件不存在:" + fullPath);
                    SendUtils.sendNotFound(exchange, fullPath.toString());
                }

            } catch (IOException e) {
                LOGGER.error("处理请求时发生错误:", e);
                SendUtils.sendInternalError(exchange, e.getMessage());
            }
        });
    }


    /**
     * 创建一个处理根路径请求的HTTP上下文，用于提供静态资源文件服务
     *
     * @param server HTTP服务器实例，用于注册上下文处理器
     */
    public static void createContextRootLast_Resource(HttpServer server) {
        // 记录方法调用的日志信息
        LOGGER.info("【调用】CreateContextUtils.createContextRootLast_Resource");

        // 为根路径"/"创建HTTP请求处理上下文
        server.createContext("/", exchange -> {
            try {
                // 获取请求URI路径，并转换为服务器上对应的资源文件路径
                Path resourcePath = ServerUtils.getResourcePath(exchange.getRequestURI().getPath());

                // 使用assert断言确保resourcePath不为null
                // assert关键字用于调试目的的断言，它验证一个条件是否为true
                // 如果条件为false，会抛出AssertionError异常
                // 注意：assert仅在启用断言的情况下生效（使用-ea或-enableassertions JVM参数）
                // 在生产环境中通常禁用断言，因此不应依赖它进行业务逻辑验证
                assert resourcePath != null;

                // 检查资源路径是否存在且不是目录
                if (Files.exists(resourcePath) && !Files.isDirectory(resourcePath)) {
                    // 如果资源文件存在，发送文件内容给客户端
                    SendUtils.sendFile(exchange, resourcePath);
                } else {
                    // 如果资源不存在或是目录，发送404 Not Found响应
                    SendUtils.sendNotFound(exchange, resourcePath.toString());
                }
            } catch (IOException e) {
                // 处理IO异常，发送500 Internal Server Error响应
                SendUtils.sendInternalError(exchange, e.getMessage());
            }
        });
    }


    /**
     * @param server
     * @param bindPath  要绑定的 URL 路径。
     *                  客户端发送的请求中，如果 URL 与该路径匹配，将由关联的 HttpHandler 来处理请求。
     * @param assetPath
     */
    public static void createContext__Bind(HttpServer server, String bindPath, String assetPath) {
        LOGGER.info("【调用】CreateContextUtils.createContext__Bind，bindPath：" + bindPath + ",assetPath:" + assetPath);
        server.createContext("/" + bindPath, exchange -> {
            try {
                Path rootPath = Paths.get(assetPath);
                if (Files.exists(rootPath) && !Files.isDirectory(rootPath)) {
                    SendUtils.sendFile(exchange, rootPath);
                } else {
                    SendUtils.sendNotFound(exchange, rootPath.toString());
                }
            } catch (IOException e) {
                SendUtils.sendInternalError(exchange, e.getMessage());
            }
        });
    }

    public static void createContextTry(HttpServer server, String try_path) {
        LOGGER.info("【调用】CreateContextUtils.createContextTry");
        server.createContext("/" + try_path, exchange -> {
            try {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    CacheUtils.handleCacheHtmlPost__ReturnUrlJson(exchange,try_path);
//                    CacheUtils.handleCacheHtmlPost__ReturnUrl(exchange,try_path);
                } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    CacheUtils.handleCacheHtmlGet(exchange);
                } else {
                    SendUtils.sendMethodNotAllowed(exchange);
                }
            } catch (IOException e) {
                SendUtils.sendInternalError(exchange, e.getMessage());
            }
        });
    }

//


}
