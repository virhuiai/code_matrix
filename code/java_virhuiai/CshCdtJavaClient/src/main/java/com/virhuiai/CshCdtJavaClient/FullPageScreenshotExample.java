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
import com.github.kklisura.cdt.protocol.commands.Emulation;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.protocol.types.page.CaptureScreenshotFormat;
import com.github.kklisura.cdt.protocol.types.page.LayoutMetrics;
import com.github.kklisura.cdt.protocol.types.page.Viewport;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Takes a full page screenshot. The output screenshot dimensions will be page width x page height,
 * for example when capturing https://news.ycombinator.com/ the output screenshot image will be
 * 1185x1214
 *
 * @author Kenan Klisura
 */
/**
 * 全页面截图示例
 * 生成的截图尺寸将是页面宽度 x 页面高度
 * 例如，当捕获 https://news.ycombinator.com/ 时，输出的截图图像将是 1185x1214
 *
 * @author Kenan Klisura
 */
public class FullPageScreenshotExample {

  /**
   * 捕获全页面截图的方法
   * @param devToolsService Chrome开发工具服务实例
   * @param page Page命令实例
   * @param outputFilename 输出文件名
   */
  private static void captureFullPageScreenshot(
      ChromeDevToolsService devToolsService, Page page, String outputFilename) {
    // 获取页面布局指标
    final LayoutMetrics layoutMetrics = page.getLayoutMetrics();

    // 获取内容宽度和高度
    final double width = layoutMetrics.getContentSize().getWidth();
    final double height = layoutMetrics.getContentSize().getHeight();

    // 获取仿真命令实例
    final Emulation emulation = devToolsService.getEmulation();
    // 设置设备度量覆盖，使Chrome的视口尺寸与页面内容尺寸一致
    emulation.setDeviceMetricsOverride(
        Double.valueOf(width).intValue(), Double.valueOf(height).intValue(), 1.0d, Boolean.FALSE);

    // 创建新的视口对象
    Viewport viewport = new Viewport();
    // 设置缩放比例为1
    viewport.setScale(1d);

    // 可以设置X、Y偏移量
    viewport.setX(0d);
    viewport.setY(0d);

    // 设置需要截图的页面宽度和高度
    viewport.setWidth(width);
    viewport.setHeight(height);

    // 调用captureScreenshot方法并保存截图
    // 参数说明：PNG格式，100%质量，自定义视口，截取完整页面，不裁剪
    dump(
        outputFilename,
        page.captureScreenshot(
            CaptureScreenshotFormat.PNG, 100, viewport, Boolean.TRUE, Boolean.FALSE));
  }

  /**
   * 主方法 - 程序入口点
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome，参数false表示非无头模式（可见界面）
    final ChromeService chromeService = launcher.launch(false);

    // 创建空白标签页（about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 为此标签页创建开发工具服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取Page命令实例
    final Page page = devToolsService.getPage();

    // 注册页面加载事件监听器
    page.onLoadEventFired(
        event -> {
          System.out.println("Taking screenshot..."); // 打印开始截图的消息

          // 调用全页面截图方法
          captureFullPageScreenshot(devToolsService, page, "screenshot.png");

          System.out.println("Done!"); // 打印完成消息

          // 关闭开发工具服务
          devToolsService.close();
        });

    // 启用页面事件
    page.enable();

    // 导航到Hacker News网站
    page.navigate("https://news.ycombinator.com/");

    // 等待直到开发工具服务被关闭
    devToolsService.waitUntilClosed();
  }

  /**
   * 保存Base64编码的数据到文件
   * @param fileName 输出文件名
   * @param data Base64编码的字符串数据
   */
  private static void dump(String fileName, String data) {
    FileOutputStream fileOutputStream = null;
    try {
      //.创建文件对象
      File file = new File(fileName);
      // 创建文件输出流
      fileOutputStream = new FileOutputStream(file);
      // 将Base64数据解码并写入文件
      fileOutputStream.write(Base64.getDecoder().decode(data));
    } catch (IOException e) {
      // 打印IO异常堆栈
      e.printStackTrace();
    } finally {
      // 确保文件输出流被正确关闭
      if (fileOutputStream != null) {
        try {
          // 刷新并关闭文件流
          fileOutputStream.flush();
          fileOutputStream.close();
        } catch (IOException e) {
          // 打印关闭文件时可能出现的IO异常堆栈
          e.printStackTrace();
        }
      }
    }
  }
}
