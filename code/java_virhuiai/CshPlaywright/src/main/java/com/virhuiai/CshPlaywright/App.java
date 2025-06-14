package com.virhuiai.CshPlaywright;


import com.microsoft.playwright.*;

import com.microsoft.playwright.impl.Playwright2;
import com.virhuiai.CshLogUtils.CshLogUtils;
import org.apache.commons.logging.Log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * 主应用类，用于创建和配置HTTP代理服务器
 * 该应用程序基于BrowserMob Proxy实现了一个能够拦截和修改HTTP/HTTPS流量的代理服务器
 */
public class App {
    private static final Log LOGGER = CshLogUtils.createLogExtended(App.class); // 日志记录器

    /**
     * https://mvnrepository.com/artifact/com.microsoft.playwright/playwright
     *
     Playwright build of chromium v956323 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/chromium-956323
     Playwright build of ffmpeg v1007 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/ffmpeg-1007
     Playwright build of firefox v1313 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/firefox-1313
     Playwright build of webkit v1596 downloaded to /Users/virhuiai/Library/Caches/ms-playwright/webkit-1596

     移动后：

     ln -s /Volumes/THAWSPACE/Soft.Freezed/ms-playwright-1.18.0  /Users/virhuiai/Library/Caches/ms-playwright
     ln -s /Volumes/THAWSPACE/Soft.Freezed/ms-playwright-1.29.0  /Users/virhuiai/Library/Caches/ms-playwright

     浏览器默认下载到以下位置
     Windows: %USERPROFILE%\AppData\Local\ms-playwright
     macOS: ~/Library/Caches/ms-playwright
     Linux: ~/.cache/ms-playwright


     Downloading Chromium 109.0.5414.46 (playwright build v1041) from https://playwright.azureedge.net/builds/chromium/1041/chromium-mac.zip
     Downloading Chromium 109.0.5414.46 (playwright build v1041) from https://playwright-akamai.azureedge.net/builds/chromium/1041/chromium-mac.zip
     Downloading Chromium 109.0.5414.46 (playwright build v1041) from https://playwright-verizon.azureedge.net/builds/chromium/1041/chromium-mac.zip

     * @param args
     */
    public static void main(String[] args) {
        LoginStateManager stateManager = new LoginStateManager("/Volumes/RamDisk/playwright_state.json", 24); // 状态有效期24小时


        Playwright.CreateOptions option = new Playwright.CreateOptions();
        HashMap<String, String> opEnv = new HashMap<String, String>();
        //是否设置了跳过浏览器下载的环境变量
        opEnv.put("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1");
        option.setEnv(opEnv);

        try (Playwright playwright = Playwright2.create(option);
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                     .setExecutablePath(Paths.get("/Volumes/THAWSPACE/Soft.Freezed/Google Chrome.app/Contents/MacOS/Google Chrome")).setHeadless(false));
             // 创建带有状态的上下文
             BrowserContext context = stateManager.createContextWithState(browser)) {
//            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));



            // touch /Volumes/RamDisk/playwright_state.json

//            error: Failed to read storage state from file

//            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
//                    .setStorageStatePath(Paths.get(BROWSER_STATE_FILE)));


//            Page page = browser.newPage();
            Page page = context.newPage();

            // 添加一个会在每个页面加载前执行的脚本
//        page.addInitScript("var abcdef='文字改变自addInitScript！';");
            // 或者从文件加载脚本
            // page.addInitScript(Paths.get("path/to/script.js"));

//        page.navigate("file:///Volumes/RamDisk/simple.html");
            // Go to https://weread.qq.com/web/reader/eeb327a0813ab79bcg0177de
            page.navigate("https://weread.qq.com/web/reader/eeb327a0813ab79bcg0177dekecc32f3013eccbc87e4b62e");
            // Press ArrowRight
//        page.press("body:has-text('')", "ArrowRight");
//        page.press("body:has-text('')", "ArrowRight");
//        page.press("body:has-text('')", "ArrowRight");
//        page.click("div:nth-child(0)");





            System.out.println(page.title());

            // 保持程序运行，等待用户输入
            System.out.println("按Enter键关闭浏览器...");
            System.in.read();

            stateManager.saveState(context);




//        browser.close();
//        playwright.close();
        }catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
//        finally {
//            // 手动关闭资源
//            if (browser != null) {
//                browser.close();
//            }
//            playwright.close();
//        }


    }
    // 生成代码
    // mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://weread.qq.com/web/reader/eeb327a0813ab79bcg0177de"
}