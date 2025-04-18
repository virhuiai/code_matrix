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

import com.github.kklisura.cdt.launch.ChromeLauncher;  // 导入Chrome启动器
import com.github.kklisura.cdt.protocol.commands.Page;  // 导入页面操作相关的命令
import com.github.kklisura.cdt.services.ChromeDevToolsService;  // 导入Chrome开发工具服务
import com.github.kklisura.cdt.services.ChromeService;  // 导入Chrome服务
import com.github.kklisura.cdt.services.types.ChromeTab;  // 导入Chrome标签页类型

/**
 * DevTools java客户端的简单导航示例。
 *
 * <p>以下示例将打开Chrome浏览器，创建一个about:blank网址的标签页，然后导航到
 * github.com。接着会等待2秒，然后导航到twitter.com。最后再等待2秒，
 * 最终会关闭标签页并关闭浏览器。
 *
 * @author Kenan Klisura
 */
public class SimpleNavigateToUrlExample {
  public static void main(String[] args) throws InterruptedException {
    // 创建Chrome启动器
    try (final ChromeLauncher launcher = new ChromeLauncher()) {
      // 启动Chrome浏览器，参数为headless模式(true)或常规模式(false)
      final ChromeService chromeService = launcher.launch(false);

      // 创建一个空白标签页，即about:blank
      final ChromeTab tab = chromeService.createTab();

      // 获取此标签页的DevTools服务
      try (final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab)) {
        // 获取页面操作对象
        final Page page = devToolsService.getPage();

        // 导航到github.com网站
        page.navigate("http://github.com");

        // 等待一段时间（2000毫秒 = 2秒）
        Thread.sleep(2000);

        // 导航到twitter.com网站
        page.navigate("http://twitter.com");

        // 再次等待一段时间（2000毫秒 = 2秒）
        Thread.sleep(2000);
      }  // try-with-resources语句会自动关闭DevToolsService资源

      // 关闭标签页
      chromeService.closeTab(tab);
    }  // try-with-resources语句会自动关闭ChromeLauncher资源
  }
}
