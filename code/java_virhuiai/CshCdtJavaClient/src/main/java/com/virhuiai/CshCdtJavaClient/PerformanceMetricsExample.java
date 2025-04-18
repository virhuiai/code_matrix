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
import com.github.kklisura.cdt.protocol.commands.Performance;
import com.github.kklisura.cdt.protocol.types.performance.Metric;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.util.List;

/**
 * 性能指标示例
 * 此类展示如何使用Chrome DevTools Protocol收集网页性能指标
 *
 *
 *
 * @author Kenan Klisura
 */
public class PerformanceMetricsExample {
  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数为true表示无头模式（headless），false表示常规模式
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取各个命令接口
    final Page page = devToolsService.getPage();         // 页面操作接口
    final Network network = devToolsService.getNetwork(); // 网络操作接口
    final Performance performance = devToolsService.getPerformance(); // 性能监控接口

    // 启用性能监控
    performance.enable();

    // 注册网络加载完成事件的处理函数
    network.onLoadingFinished(
        event -> {
          // 当页面加载完成时，获取性能指标并关闭标签页和浏览器
          List<Metric> metrics = performance.getMetrics(); // 获取性能指标列表
          try {
            // 遍历并打印所有性能指标
            for (Metric metric : metrics) {
              System.out.println(metric.getName() + ": " + metric.getValue());
            }
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
          // 关闭标签页
          chromeService.closeTab(tab);
          // 关闭浏览器
          launcher.close();
        });

    // 启用网络事件监听
    network.enable();

    // 导航到github.com网站
    page.navigate("https://www.github.com");

    // 等待DevTools服务关闭
    // 这会阻塞主线程，直到devToolsService被关闭
    devToolsService.waitUntilClosed();
  }
}
