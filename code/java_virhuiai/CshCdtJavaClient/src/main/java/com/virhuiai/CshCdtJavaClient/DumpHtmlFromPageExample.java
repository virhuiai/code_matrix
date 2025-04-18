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

import com.github.kklisura.cdt.launch.ChromeLauncher;      // 导入Chrome启动器类
import com.github.kklisura.cdt.protocol.commands.Page;      // 导入页面操作命令类
import com.github.kklisura.cdt.protocol.commands.Runtime;   // 导入运行时命令类
import com.github.kklisura.cdt.protocol.types.runtime.Evaluate; // 导入评估结果类型
import com.github.kklisura.cdt.services.ChromeDevToolsService; // 导入Chrome开发工具服务类
import com.github.kklisura.cdt.services.ChromeService;      // 导入Chrome服务类
import com.github.kklisura.cdt.services.types.ChromeTab;    // 导入Chrome标签页类型

/**
 * 以下示例从github.com网站获取并输出HTML内容。
 *
 * @author Kenan Klisura
 */
public class DumpHtmlFromPageExample {
  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数为true表示无头模式（不显示界面），false表示正常模式（显示界面）
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（即about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单独的命令对象
    final Page page = devToolsService.getPage();    // 页面操作对象
    final Runtime runtime = devToolsService.getRuntime();  // JavaScript运行时对象

    // 等待页面加载事件触发
    page.onLoadEventFired(
        event -> {
          // 执行JavaScript代码来评估页面内容
          Evaluate evaluation = runtime.evaluate("document.documentElement.outerHTML");
          // 输出获取到的HTML内容
          System.out.println(evaluation.getResult().getValue());

          // 关闭DevTools服务
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
