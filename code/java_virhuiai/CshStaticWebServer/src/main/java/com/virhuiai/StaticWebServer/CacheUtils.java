package com.virhuiai.StaticWebServer;

import com.sun.net.httpserver.HttpExchange;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheUtils {
    private static Log LOGGER = CshLogUtils.createLogExtended(CacheUtils.class);

    private static final AtomicInteger cacheIdCounter = new AtomicInteger();

    private static final ConcurrentHashMap<Integer, String> htmlCache = new ConcurrentHashMap<>();

//    CacheUtils.saveToCache
    public static int saveToCache(String content){
        int cacheId = cacheIdCounter.incrementAndGet();
        htmlCache.put(cacheId, content);
        return cacheId;
    }

//    CacheUtils.queryCache
    public static String queryCache(int cacheId){
        String htmlContent = htmlCache.getOrDefault(cacheId, "缓存未找到！");
        htmlCache.remove(cacheId);
        return htmlContent;
    }


    // todo test
    public static void handleCacheHtmlPost__ReturnUrl(HttpExchange exchange,String try_path) throws IOException {
        String htmlContent = ServerUtils.readStringFromInputStream(exchange.getRequestBody());
        int cacheId = CacheUtils.saveToCache(htmlContent);


//        String response = Integer.toString(cacheId);
//        exchange.getResponseHeaders().set("Content-Type", "text/plain");
//        exchange.sendResponseHeaders(200, response.length());
//        try (OutputStream responseBody = exchange.getResponseBody()) {
//            responseBody.write(response.getBytes(StandardCharsets.UTF_8));
//        }

        // 构造响应数据
        String responseUrl = "http://localhost:" + exchange.getLocalAddress().getPort() + "/" + try_path + "/" + cacheId + ".html";

        // 发送响应头和响应体
//        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, responseUrl.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseUrl.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void handleCacheHtmlPost__ReturnUrlJson(HttpExchange exchange,String try_path) throws IOException {
        String htmlContent = ServerUtils.readStringFromInputStream(exchange.getRequestBody());
        int cacheId = CacheUtils.saveToCache(htmlContent);

//        String response = Integer.toString(cacheId);
//        exchange.getResponseHeaders().set("Content-Type", "text/plain");
//        exchange.sendResponseHeaders(200, response.length());
//        try (OutputStream responseBody = exchange.getResponseBody()) {
//            responseBody.write(response.getBytes(StandardCharsets.UTF_8));
//        }

        // 构造响应数据
        String responseUrl = "http://localhost:" + exchange.getLocalAddress().getPort() + "/" + try_path + "/" + cacheId + ".html";
        Map<String, String> response = new HashMap<>();
        response.put("htmlUrl", responseUrl);
        System.out.println("htmlUrl===:" + responseUrl );

        // 将响应数据转为 JSON
//        String jsonResponse = JSON.toJSONString(response);
//        String jsonResponse = "{\"htmlUrl\":" + responseUrl + "}";
        String jsonResponse = "{\"url\":\"" + responseUrl + "\"}";

        // 发送响应头和响应体
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
        }
    }


    public static void handleCacheHtmlGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String fileName = path.substring(path.lastIndexOf('/') + 1);

        if (fileName.endsWith(".html")) {
            // 假设文件名不带扩展名是cacheId
            String cacheIdString = fileName.substring(0, fileName.length() - ".html".length());

            try {
                // 尝试将cacheIdString转换为整数
                int cacheId = Integer.parseInt(cacheIdString);
                String htmlContent = CacheUtils.queryCache(cacheId);

                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, htmlContent.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(htmlContent.getBytes(StandardCharsets.UTF_8));
                }
            } catch (NumberFormatException e) {
                SendUtils.sendBadRequest(exchange);
            }
        } else {
            SendUtils.sendBadRequest(exchange);
        }
    }






}
