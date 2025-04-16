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
public class App {
    private static final Log LOGGER = CshLogUtils.createLogExtended(App.class); // 日志记录器

    /**
     Playwright build of chromium v956323 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/chromium-956323
     Playwright build of ffmpeg v1007 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/ffmpeg-1007
     Playwright build of firefox v1313 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/firefox-1313
     Playwright build of webkit v1596 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/webkit-1596

     * @param args
     */
    public static void main(String[] args) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));




        Page page = browser.newPage();
        page.navigate("https://weread.qq.com/web/reader/eeb327a0813ab79bcg0177de");
        System.out.println(page.title());
//        browser.close();
//        playwright.close();
    }
}