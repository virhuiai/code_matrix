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
import com.github.kklisura.cdt.protocol.types.network.ErrorReason;
import com.github.kklisura.cdt.protocol.types.network.RequestPattern;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.util.Collections;

/**
 * 拦截并按URL阻止请求的示例。
 * 由于requestIntercepted事件仍处于实验阶段，它可能在您的浏览器上不起作用。
 *
 * @author Kenan Klisura
 */
public class InterceptAndBlockUrlsExample {
  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数为是否以无头模式(headless)启动：true为无头模式，false为常规模式
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页，即about:blank
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单独的命令对象
    final Page page = devToolsService.getPage();  // 页面操作相关命令
    final Network network = devToolsService.getNetwork();  // 网络相关命令

    // 设置请求拦截事件的处理逻辑
    network.onRequestIntercepted(
        event -> {
          // 获取拦截ID，用于后续继续或阻止请求
          String interceptionId = event.getInterceptionId();
          // 根据URL判断是否应该阻止该请求
          boolean blocked = isBlocked(event.getRequest().getUrl());

          // 打印请求状态和URL信息
          System.out.printf(
              "%s - %s%s",
              (blocked ? "已阻止" : "已允许"),
              event.getRequest().getUrl(),
              System.lineSeparator());

          // 如果需要阻止，则设置错误原因为ABORTED；否则为null（允许请求继续）
          ErrorReason errorReason = blocked ? ErrorReason.ABORTED : null;

          // 继续处理被拦截的请求
          // 参数依次为：拦截ID、错误原因、响应码、响应头、响应体、响应码文本、响应头、认证挑战响应
          network.continueInterceptedRequest(
              interceptionId, errorReason, null, null, null, null, null, null);
        });

    // 页面加载完成事件处理：关闭DevTools服务
    page.onLoadEventFired(event -> devToolsService.close());

    // 创建拦截请求模式（这里是拦截所有请求，因为没有设置具体模式）
    RequestPattern interceptionRequestPattern = new RequestPattern();
    // 设置请求拦截规则
    network.setRequestInterception(Collections.singletonList(interceptionRequestPattern));
    // 启用网络监控
    network.enable();

    // 启用页面事件
    page.enable();

    // 导航到github.com
    page.navigate("http://github.com");

    // 等待直到DevTools服务关闭
    devToolsService.waitUntilClosed();
  }

  /**
   * 判断给定URL是否应该被阻止
   *
   * @param url 请求的URL
   * @return 如果URL以.png或.css结尾，返回true（阻止）；否则返回false（允许）
   */
  public static boolean isBlocked(String url) {
    // 阻止所有PNG图片和CSS文件
    return url.endsWith(".png") || url.endsWith(".css");
  }
}
