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

/**
 * Log requests example with DevTools java client.
 * 使用DevTools Java客户端记录请求的示例。
 *
 * <p>The following example will open chrome, create a tab with about:blank url, subscribe to
 * requestWillBeSent event and then navigate to github.com.
 * 以下示例将打开Chrome浏览器，创建一个带有about:blank网址的标签页，订阅requestWillBeSent事件，然后导航到github.com。
 *
 * @author Kenan Klisura
 */
public class LogRequestsExample {
  public static void main(String[] args) {
    // Create chrome launcher.
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // Launch chrome either as headless (true) or regular (false).
    // 启动Chrome浏览器，可以选择无头模式(true)或常规模式(false)
    final ChromeService chromeService = launcher.launch(false);

    // Create empty tab ie about:blank.
    // 创建一个空标签页，即about:blank
    final ChromeTab tab = chromeService.createTab();

    // Get DevTools service to this tab
    // 获取此标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // Get individual commands
    // 获取单独的命令对象
    final Page page = devToolsService.getPage();
    final Network network = devToolsService.getNetwork();

    // Log requests with onRequestWillBeSent event handler.
    // 使用onRequestWillBeSent事件处理器记录请求
    network.onRequestWillBeSent(
        event ->
            System.out.printf(
                "request: %s %s%s",
                event.getRequest().getMethod(),    // 获取HTTP请求方法（GET、POST等）
                event.getRequest().getUrl(),       // 获取请求的URL
                System.lineSeparator()));          // 添加换行符

    // 当页面加载完成时的事件处理
    network.onLoadingFinished(
        event -> {
          // Close the tab and close the browser when loading finishes.
          // 当加载完成时，关闭标签页和浏览器
          chromeService.closeTab(tab);
          launcher.close();
        });

    // Enable network events.
    // 启用网络事件监听
    network.enable();

    // Navigate to github.com.
    // 导航到github.com网站
    page.navigate("http://github.com");

    // 等待直到DevTools服务关闭
    devToolsService.waitUntilClosed();
  }
}
