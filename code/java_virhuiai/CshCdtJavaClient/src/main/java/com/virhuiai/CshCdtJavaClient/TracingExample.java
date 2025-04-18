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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.protocol.commands.Tracing;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Tracing示例。
 * 这个类演示了如何使用Chrome DevTools协议来跟踪页面性能数据。
 *
 * @author Kenan Klisura
 */
public class TracingExample {
  public static void main(String[] args) {
    // 创建一个Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数为false表示非无头模式（可见的浏览器界面）
    // 如果设置为true则为无头模式（不显示浏览器界面）
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单独的命令对象
    final Page page = devToolsService.getPage();  // 页面相关命令
    final Tracing tracing = devToolsService.getTracing();  // 跟踪相关命令

    // 创建一个列表用于存储收集到的跟踪数据
    final List<Object> dataCollectedList = new LinkedList<>();

    // 添加跟踪数据收集的事件监听器
    // 当收集到数据时，将数据添加到dataCollectedList中
    tracing.onDataCollected(
        event -> {
          if (event.getValue() != null) {
            dataCollectedList.addAll(event.getValue());
          }
        });

    // 当跟踪完成时，将收集到的数据保存到JSON文件
    tracing.onTracingComplete(
        event -> {
          // 将跟踪数据转储到文件
          System.out.println("Tracing completed! Dumping to a file.");

          dump("tracing.json", dataCollectedList);

          // 关闭DevTools服务
          devToolsService.close();
        });

    // 设置页面加载完成事件的监听器
    // 当页面加载完成时，结束跟踪
    page.onLoadEventFired(
        event -> {
          tracing.end();
        });

    // 启用页面事件
    page.enable();

    // 开始跟踪
    tracing.start();

    // 导航到github.com
    page.navigate("http://github.com");

    // 等待DevTools服务关闭（阻塞主线程直到服务关闭）
    devToolsService.waitUntilClosed();
  }

  /**
   * 将数据保存到文件的辅助方法
   *
   * @param fileName 目标文件名
   * @param data 要保存的数据
   */
  private static void dump(String fileName, List<Object> data) {
    // 创建JSON对象映射器
    final ObjectMapper objectMapper = new ObjectMapper();

    try {
      // 创建文件对象
      File file = new File(fileName);
      // 将数据写入文件
      objectMapper.writeValue(file, data);
    } catch (IOException e) {
      // 如果出现IO异常，打印堆栈跟踪
      e.printStackTrace();
    }
  }
}
