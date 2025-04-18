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

// 导入必要的类和接口
import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Network;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.protocol.types.network.RequestPattern;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个展示如何向拦截的请求传递自定义请求头的示例。
 * 在此示例中，每个请求都会添加一个值为'Value'的'Custom-Header'头部。
 *
 *
 * 这段代码主要实现了以下功能：
 *
 * 启动Chrome浏览器
 * 创建一个新的标签页
 * 设置网络请求拦截
 * 为每个拦截的请求添加自定义请求头
 * 导航到GitHub网站
 * 该示例展示了如何使用Chrome DevTools Protocol (CDP) Java客户端库来操作Chrome浏览器并修改网络请求。
 * @author Kenan Klisura
 */
public class PassingCustomHeadersToRequests {
  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数为true表示无头模式（headless），false表示普通模式
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取该标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单独的命令对象
    final Page page = devToolsService.getPage();  // 页面操作命令
    final Network network = devToolsService.getNetwork();  // 网络操作命令

    // 设置请求拦截处理函数
    network.onRequestIntercepted(
        event -> {
          // 获取拦截ID，用于后续处理
          String interceptionId = event.getInterceptionId();

          // 打印日志，显示将为哪个URL添加请求头
          System.out.printf(
              "Adding header to %s request%s", event.getRequest().getUrl(), System.lineSeparator());

          // 创建一个包含自定义请求头的Map
          Map<String, Object> headers = new HashMap<>();
          headers.put("Custom-Header", "Value");  // 添加自定义请求头

          // 继续处理被拦截的请求，同时添加自定义请求头
          // 除headers外的其他参数都设为null，表示不修改这些部分
          network.continueInterceptedRequest(
              interceptionId, null, null, null, null, null, headers, null);
        });

    // 创建请求拦截模式，默认拦截所有请求
    RequestPattern interceptionRequestPattern = new RequestPattern();
    // 设置请求拦截，将拦截模式放入单元素列表中传入
    network.setRequestInterception(Collections.singletonList(interceptionRequestPattern));
    // 启用网络监控
    network.enable();

    // 启用页面事件监控
    page.enable();

    // 导航到github.com网站
    page.navigate("http://github.com");

    // 等待DevTools服务关闭（会阻塞主线程）
    devToolsService.waitUntilClosed();
  }
}
