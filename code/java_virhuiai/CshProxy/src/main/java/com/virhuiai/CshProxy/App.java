package com.virhuiai.CshProxy;


import com.virhuiai.CshLogUtils.CshLogUtils;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.apache.commons.logging.Log;

/**
 * 主应用类，用于创建和配置HTTP代理服务器
 * 该应用程序基于BrowserMob Proxy实现了一个能够拦截和修改HTTP/HTTPS流量的代理服务器
 */
public class App {
    private static final Log LOGGER = CshLogUtils.createLogExtended(App.class); // 日志记录器


    public static void main(String[] args) {
        // 创建BrowserMobProxy实例，这是一个功能强大的代理服务器
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        // 启用HTTPS支持，信任所有服务器证书
        // 注意：在生产环境中，应该谨慎使用setTrustAllServers(true)，因为这会降低安全性
        proxy.setTrustAllServers(true);

        // 启动代理服务器
        // 注意：这里没有指定端口，代理会自动选择一个可用端口
        proxy.start(0);

        // 设置请求和响应过滤器，用于拦截和修改HTTP流量
        setUpFilters(proxy);

        // 输出代理服务器启动的端口号
        LOGGER.info("代理服务器：\n" +
                "http://127.0.0.1:" + proxy.getPort());

//        builder.getJcefArgs().add("--proxy-server=http://127.0.0.1:49408");// 按代理来
//        builder.getJcefArgs().add("--ignore-certificate-errors");// 禁用证书验证
    }

    /**
     * 设置HTTP响应过滤器
     * 这个方法配置了一个过滤器，用于拦截和修改从服务器返回的HTTP响应
     *
     * @param proxy BrowserMobProxy实例
     */
    private static void setUpFilters(BrowserMobProxy proxy) {
        // 添加响应过滤器，用于处理HTTP响应
        proxy.addResponseFilter((response, contents, messageInfo) -> {
            // 输出响应内容类型
            LOGGER.info("响应内容类型:\n" + contents.getContentType());

            // 输出原始请求URL
            LOGGER.info("原始请求URL:\n" + messageInfo.getOriginalUrl());

            // 输出响应头中的Referrer-Policy
            LOGGER.info("响应头中的Referrer-Policy :" + response.headers().get("Referrer-Policy"));
            // 注释掉的代码：添加CORS头，允许所有来源访问资源
//            response.headers().add("Access-Control-Allow-Origin", "*");

            // strict-origin-when-cross-origin 是一种常见的 Referrer-Policy，用于控制在跨域请求中 HTTP Referrer 头部的信息如何被发送。这种策略意味着仅当请求的目标与请求来源具有相同的源时，浏览器才会发送完整的 referrer URL。如果是跨域请求，浏览器只会发送原始文档的 origin，而不包括完整的 URL 路径。
            // 可以使用开发者工具（如 Chrome DevTools）暂时修改页面的响应头或 HTML meta 标签来测试和开发
            // 可以使用 BrowserMob Proxy 来修改进出的 HTTP 请求和响应。以下是如何使用 BrowserMob Proxy 修改 Referrer-Policy 的示例代码：
//            if (response.headers().contains("Referrer-Policy")) {
//                // 替换现有的 Referrer-Policy 头部
//                response.headers().set("Referrer-Policy", "unsafe-url");
//            } else {
//                // 添加 Referrer-Policy 头部，如果原来没有的话

            // 添加Referrer-Policy头，设置为unsafe-url
            // unsafe-url会在所有请求中发送完整的URL，包括跨域请求
            // 注意：这可能会带来隐私风险，因为它会泄露用户的浏览历史
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

           // 检查请求URL是否为JavaScript文件
            if (messageInfo.getOriginalUrl().endsWith(".js")) {
                // 获取JavaScript内容
                String content = contents.getTextContents();

                // 如果内容包含特定字符串'xxxx'
                if (content.contains("'xxxxabc'")) {
                    // 输出找到目标JavaScript文件的URL
                    System.out.println("messageInfo.getOriginalUrl():" + messageInfo.getOriginalUrl());

                    // 在JavaScript内容前添加自定义代码
                    String modifiedContent = "window.aaaaaa=3333;" + content;

                    // 用修改后的内容替换原始内容
                    contents.setTextContents(modifiedContent);
                }
            }
//            }
        });
    }
}