package com.virhuiai.CshCdtJavaClient;

/*-
 * #%L
 * cdt-examples
 * %%
 * Copyright (C) 2018 - 2021 Kenan Klisura
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Network;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.util.Arrays;

/**
 * 阻止特定URL的获取。在本例中阻止png和css文件。
 * Blocks specific urls from fetching. In this case png and css.
 *
 * @author Kenan Klisura
 */
public class BlockUrlsExample {
  public static void main(String[] args) {
    // 创建Chrome启动器
    // Create chrome launcher.
    final ChromeLauncher launcher = new ChromeLauncher();

    // 以无头模式(true)或常规模式(false)启动Chrome浏览器
    // Launch chrome either as headless (true) or regular (false).
    final ChromeService chromeService = launcher.launch(false);

    // 创建空白标签页，即about:blank
    // Create empty tab ie about:blank.
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的DevTools服务
    // Get DevTools service to this tab
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单独的命令对象
    // Get individual commands
    final Page page = devToolsService.getPage();
    final Network network = devToolsService.getNetwork();

    // 设置要阻止的URL模式列表，这里阻止所有PNG和CSS文件
    network.setBlockedURLs(Arrays.asList("*.png", "*.css"));

    // 当页面加载完成事件触发时，关闭DevTools服务
    page.onLoadEventFired(event -> devToolsService.close());

    // 启用网络功能
    network.enable();

    // 启用页面事件
    // Enable page events.
    page.enable();

    // 导航到github.com
    // Navigate to github.com.
    page.navigate("http://github.com");

    // 等待直到DevTools服务关闭
    devToolsService.waitUntilClosed();
  }

  /**
   * 判断给定URL是否应被阻止
   * @param url 要检查的URL
   * @return 如果URL以.png或.css结尾则返回true，表示应该阻止
   */
  public static boolean isBlocked(String url) {
    return url.endsWith(".png") || url.endsWith(".css");
  }
}
