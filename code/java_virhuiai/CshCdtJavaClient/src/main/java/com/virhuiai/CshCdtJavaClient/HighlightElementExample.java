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
import com.github.kklisura.cdt.protocol.commands.*;
import com.github.kklisura.cdt.protocol.types.dom.RGBA;
import com.github.kklisura.cdt.protocol.types.overlay.HighlightConfig;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;

/**
 * 以Chrome检查器相同的方式高亮显示元素。
 *
 * @author Kenan Klisura
 */
public class HighlightElementExample {
  /**
   * 高亮显示节点的方法
   * @param devToolsService Chrome开发工具服务实例
   * @param highlightConfig 高亮配置对象
   * @param selector CSS选择器，用于定位要高亮的元素
   */
  private static void highlightNode(
      ChromeDevToolsService devToolsService, HighlightConfig highlightConfig, String selector) {
    // 获取DOM操作对象
    final DOM dom = devToolsService.getDOM();
    // 获取Overlay操作对象，用于显示高亮效果
    final Overlay overlay = devToolsService.getOverlay();

    // 通过CSS选择器查询需要高亮的节点ID
    final Integer nodeId =
        dom.querySelector(devToolsService.getDOM().getDocument().getNodeId(), selector);
    // 使用overlay高亮节点，其他参数设置为null（表示使用默认值）
    overlay.highlightNode(highlightConfig, nodeId, null, null, null);
  }

  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数false表示非无头模式（即有界面）
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取该标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取各个命令接口
    final Page page = devToolsService.getPage();             // 页面操作接口
    final Network network = devToolsService.getNetwork();    // 网络操作接口
    final Performance performance = devToolsService.getPerformance(); // 性能操作接口

    // 启用DOM操作功能
    devToolsService.getDOM().enable();
    // 启用Overlay功能，允许在页面上显示高亮效果
    devToolsService.getOverlay().enable();

    // 启用性能监控
    performance.enable();
    // 注册网络加载完成事件的回调函数
    network.onLoadingFinished(
        event -> {
          // 创建高亮配置对象
          HighlightConfig highlightConfig = new HighlightConfig();

          // 设置各种高亮显示的颜色
          highlightConfig.setBorderColor(rgba(255, 229, 153, 0.66));        // 边框颜色
          highlightConfig.setContentColor(rgba(111, 168, 220, 0.66));       // 内容区域颜色
          highlightConfig.setCssGridColor(rgb(75, 0, 130));                 // CSS网格颜色
          highlightConfig.setEventTargetColor(rgba(255, 196, 196, 0.66));   // 事件目标颜色
          highlightConfig.setMarginColor(rgba(246, 178, 107, 0.66));        // 外边距颜色
          highlightConfig.setPaddingColor(rgba(147, 196, 125, 0.55));       // 内边距颜色
          highlightConfig.setShapeColor(rgba(96, 82, 117, 0.8));            // 形状颜色
          highlightConfig.setShapeMarginColor(rgba(96, 82, 127, 0.6));      // 形状外边距颜色

          // 配置高亮显示选项
          highlightConfig.setShowExtensionLines(true);   // 显示延伸线
          highlightConfig.setShowInfo(true);             // 显示信息
          highlightConfig.setShowRulers(true);           // 显示标尺
          highlightConfig.setShowStyles(false);          // 不显示样式

          // 高亮显示选择器"h1.h000-mktg"匹配的元素（GitHub页面上的一个标题元素）
          highlightNode(devToolsService, highlightConfig, "h1.h000-mktg");

          // 休眠一段时间，让高亮效果持续显示
          sleep();
        });

    // 启用网络事件监听
    network.enable();

    // 导航到GitHub首页
    page.navigate("https://www.github.com");

    // 等待DevTools服务关闭
    devToolsService.waitUntilClosed();
  }

  /**
   * 线程休眠方法，用于保持程序运行状态
   * 休眠100秒，让高亮效果可以被观察到
   */
  private static void sleep() {
    try {
      Thread.sleep(100000);  // 休眠100秒
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 创建带透明度的RGBA颜色对象
   * @param r 红色分量（0-255）
   * @param g 绿色分量（0-255）
   * @param b 蓝色分量（0-255）
   * @param a 透明度（0-1），0表示完全透明，1表示完全不透明
   * @return 配置好的RGBA对象
   */
  private static RGBA rgba(int r, int g, int b, double a) {
    RGBA result = new RGBA();
    result.setR(r);
    result.setG(g);
    result.setB(b);
    result.setA(a);
    return result;
  }

  /**
   * 创建不透明的RGB颜色对象
   * @param r 红色分量（0-255）
   * @param g 绿色分量（0-255）
   * @param b 蓝色分量（0-255）
   * @return 配置好的RGBA对象（透明度默认为完全不透明）
   */
  private static RGBA rgb(int r, int g, int b) {
    RGBA result = new RGBA();
    result.setR(r);
    result.setG(g);
    result.setB(b);
    return result;
  }
}
