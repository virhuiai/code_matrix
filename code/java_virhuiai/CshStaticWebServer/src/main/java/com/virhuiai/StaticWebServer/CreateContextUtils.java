package com.virhuiai.StaticWebServer;

import com.sun.net.httpserver.HttpServer;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateContextUtils {
    private static Log LOGGER = CshLogUtils.createLogExtended(CreateContextUtils.class);

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

    public static void createContextRootLast_Resource(HttpServer server) {
        LOGGER.info("【调用】CreateContextUtils.createContextRootLast_Resource");
        server.createContext("/", exchange -> {
            try {
                Path resourcePath = ServerUtils.getResourcePath(exchange.getRequestURI().getPath());
                if (Files.exists(resourcePath) && !Files.isDirectory(resourcePath)) {
                    SendUtils.sendFile(exchange, resourcePath);
                } else {
                    SendUtils.sendNotFound(exchange, resourcePath.toString());
                }
            } catch (IOException e) {
                SendUtils.sendInternalError(exchange, e.getMessage());
            }
        });
    }

    public static void createContextRootLast_Path(HttpServer server, String root_path_str) {
        LOGGER.info("【调用】CreateContextUtils.createContextRootLast_Path");
        server.createContext("/", exchange -> {
            try {
                Path rootPath = Paths.get(root_path_str);
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

    /**
     *
     * @param server
     * @param bindPath 要绑定的 URL 路径。
     *                 客户端发送的请求中，如果 URL 与该路径匹配，将由关联的 HttpHandler 来处理请求。
     * @param assetPath
     */
    public static void createContext__Bind(HttpServer server, String bindPath,String assetPath) {
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
}
