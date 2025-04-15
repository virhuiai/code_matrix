package com.virhuiai.CshProxy;


import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;

public class AppOld {
    public static void main(String[] args) {

        // 创建 BrowserMobProxy 实例
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        // 启用HTTPS支持
        proxy.setTrustAllServers(true);
//        proxy.start(0); // 0 表示自动选择一个可用端口
        proxy.start(); // 0 表示自动选择一个可用端口

        // 设置请求和响应过滤器
        setUpFilters(proxy);

        System.out.println("Proxy started on port: " + proxy.getPort());
    }

    private static void setUpFilters(BrowserMobProxy proxy) {
        // 添加响应过滤器
        proxy.addResponseFilter((response, contents, messageInfo) -> {
            System.out.println("contents.getContentType()" + contents.getContentType());
            System.out.println("messageInfo.getOriginalUrl():" + messageInfo.getOriginalUrl());
            //   注意: 将 Access-Control-Allow-Origin 设置为 * 会降低安全性，因为它允许任何网站访问您的资源，包括恶意网站。
            System.out.println("response.headers().get(\"Referrer-Policy\"):" + response.headers().get("Referrer-Policy"));
//            response.headers().add("Access-Control-Allow-Origin", "*");

            // strict-origin-when-cross-origin 是一种常见的 Referrer-Policy，用于控制在跨域请求中 HTTP Referrer 头部的信息如何被发送。这种策略意味着仅当请求的目标与请求来源具有相同的源时，浏览器才会发送完整的 referrer URL。如果是跨域请求，浏览器只会发送原始文档的 origin，而不包括完整的 URL 路径。
            // 可以使用开发者工具（如 Chrome DevTools）暂时修改页面的响应头或 HTML meta 标签来测试和开发
            // 可以使用 BrowserMob Proxy 来修改进出的 HTTP 请求和响应。以下是如何使用 BrowserMob Proxy 修改 Referrer-Policy 的示例代码：
//            if (response.headers().contains("Referrer-Policy")) {
//                // 替换现有的 Referrer-Policy 头部
//                response.headers().set("Referrer-Policy", "unsafe-url");
//            } else {
//                // 添加 Referrer-Policy 头部，如果原来没有的话
            response.headers().add("Referrer-Policy", "unsafe-url");
//            }

//            contents.getContentType()application/octet-stream
//            messageInfo.getOriginalUrl():http://weread.qq.com/
//            contents.getContentType()application/javascript
//            messageInfo.getOriginalUrl():http://192.168.8.1/language/lang_zh_cn.js?r=1715347552860
//            contents.getContentType()text/xml
//            contents.getContentType()text/plain
//            messageInfo.getOriginalUrl():http://192.168.8.1/res/iconfont.woff2
//            contents.getContentType()application/json; charset=UTF-8
//            messageInfo.getOriginalUrl():http://192.168.8.1/api/system/deviceinfo
//            contents.getContentType()image/png
//            messageInfo.getOriginalUrl():http://192.168.8.1/res/ic_device_upgrade.png
//            contents.getContentType()text/css
//            messageInfo.getOriginalUrl():http://192.168.8.1/css/common.css?r=1692700033482

//            System.out.println("messageInfo.getOriginalUrl():" + messageInfo.getOriginalUrl());

//            if (messageInfo.getOriginalUrl().contains("https://a.b.c/d.js")) {
//                // 修改响应内容，例如替换某些 JavaScript 代码
//                String content = contents.getTextContents();
//                String modifiedContent = content.replace("oldFunction()", "newFunction()");
//                contents.setTextContents(modifiedContent);
//            } else {
            if (messageInfo.getOriginalUrl().endsWith(".js")) {
                String content = contents.getTextContents();
                if (content.contains("'xxxx'")) {
                    System.out.println("messageInfo.getOriginalUrl():" + messageInfo.getOriginalUrl());
                    String modifiedContent = "window.aaaaaa=3333;" + content;
                    contents.setTextContents(modifiedContent);
                }
            }
//            }
        });
    }
}