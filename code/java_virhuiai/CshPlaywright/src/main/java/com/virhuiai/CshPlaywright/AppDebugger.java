package com.virhuiai.CshPlaywright;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

/**
 * 主应用类，用于创建和配置HTTP代理服务器
 * 该应用程序基于BrowserMob Proxy实现了一个能够拦截和修改HTTP/HTTPS流量的代理服务器
 */
public class AppDebugger {
    private static final Log LOGGER = CshLogUtils.createLogExtended(AppDebugger.class); // 日志记录器

    /**
     Playwright build of chromium v956323 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/chromium-956323
     Playwright build of ffmpeg v1007 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/ffmpeg-1007
     Playwright build of firefox v1313 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/firefox-1313
     Playwright build of webkit v1596 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/webkit-1596

     移动后：
     ln -s /Volumes/THAWSPACE/Soft.Freezed/ms-playwright  /Users/virhuiai/Library/Caches/ms-playwright

     * @param args
     */
    public static void main(String[] args) {
        Playwright playwright = Playwright.create();
//        Browser browser = playwright.chromium().connectOverCDP("http://localhost:9222");
        Browser browser = playwright.chromium().connectOverCDP("http://127.0.0.1:9222");
        // 获取默认上下文和页面，不尝试创建新的浏览器上下文
        Page page = browser.contexts().size() > 0 && browser.contexts().get(0).pages().size() > 0
                ? browser.contexts().get(0).pages().get(0)
                : null;

//        http://localhost:9222/json/list
//[ {
//            "description": "",
//                    "devtoolsFrontendUrl": "/devtools/inspector.html?ws=localhost:9222/devtools/page/84015D47FBA58E32A8264AFFC393BFA7",
//                    "id": "84015D47FBA58E32A8264AFFC393BFA7",
//                    "title": "百度一下，你就知道",
//                    "type": "page",
//                    "url": "https://www.baidu.com/",
//                    "webSocketDebuggerUrl": "ws://localhost:9222/devtools/page/84015D47FBA58E32A8264AFFC393BFA7"
//        } ]

//        Page page = browser.newPage();

        // 添加一个会在每个页面加载前执行的脚本
//        page.addInitScript("var abcdef='文字改变自addInitScript！';");
        // 或者从文件加载脚本
        // page.addInitScript(Paths.get("path/to/script.js"));

//        page.navigate("file:///Volumes/RamDisk/simple.html");
        // Go to https://weread.qq.com/web/reader/eeb327a0813ab79bcg0177de
//        page.navigate("https://weread.qq.com/web/reader/eeb327a0813ab79bcg0177dekecc32f3013eccbc87e4b62e");
        // Press ArrowRight
//        page.press("body:has-text('')", "ArrowRight");
//        page.press("body:has-text('')", "ArrowRight");
//        page.press("body:has-text('')", "ArrowRight");
//        page.click("div:nth-child(0)");
//
//
//
//        System.out.println(page.title());
//        browser.close();
//        playwright.close();
    }
    // 生成代码
    // mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://weread.qq.com/web/reader/eeb327a0813ab79bcg0177de"
}