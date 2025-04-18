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
 * Could not find chrome binary! Try setting CHROME_PATH env to chrome binary path.
 *
 * 根据给定模式阻止URL。
 * Blocks an URLs given a patterns.
 *
 * @author Kenan Klisura
 */
public class BlockUrlGivenPatternExample {
  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数false表示非无头模式（可见界面），true表示无头模式（不可见界面）
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（即about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单独的命令对象
    final Page page = devToolsService.getPage();   // 页面操作命令
    final Network network = devToolsService.getNetwork();  // 网络操作命令

    // 设置要阻止的URL模式列表（阻止所有CSS、PNG和SVG文件）
    network.setBlockedURLs(Arrays.asList("**/*.css", "**/*.png", "**/*.svg"));

    // 启用网络事件监听
    network.enable();

    // 等待页面加载完成事件
    page.onLoadEventFired(
        event -> {
          // 页面加载完成后关闭DevTools服务
          devToolsService.close();
        });

    // 启用页面事件监听
    page.enable();

    // 导航到github.com网站
    page.navigate("http://github.com");

    // 等待直到DevTools服务关闭
    devToolsService.waitUntilClosed();

    // 关闭标签页
    chromeService.closeTab(tab);
  }
}
