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
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 截取网页截图的示例。
 * 该类展示了如何使用Chrome DevTools Protocol库来启动Chrome浏览器，
 * 导航到指定页面，并截取屏幕截图。
 *
 * @author Kenan Klisura
 */
public class TakeScreenshotExample {
  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数为true表示无头模式（不显示浏览器界面），false表示常规模式（显示浏览器界面）
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（即about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取该标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取Page命令对象，用于控制页面操作
    final Page page = devToolsService.getPage();

    // 注册页面加载完成事件的监听器
    page.onLoadEventFired(
        event -> {
          System.out.println("Taking screenshot..."); // 输出提示信息：正在截图
          dump("screenshot.png", page.captureScreenshot()); // 捕获截图并保存到文件
          System.out.println("Done!"); // 输出提示信息：完成

          devToolsService.close(); // 关闭DevTools服务
        });

    // 启用页面事件监听
    page.enable();

    // 导航到GitHub网站
    page.navigate("http://github.com");

    // 等待DevTools服务关闭（这会阻塞主线程直到服务关闭）
    devToolsService.waitUntilClosed();
  }

  /**
   * 将Base64编码的数据保存到文件
   *
   * @param fileName 目标文件名
   * @param data Base64编码的字符串数据
   */
  private static void dump(String fileName, String data) {
    FileOutputStream fileOutputStream = null;
    try {
      // 创建文件对象
      File file = new File(fileName);
      // 创建文件输出流
      fileOutputStream = new FileOutputStream(file);
      // 将Base64编码的数据解码并写入文件
      fileOutputStream.write(Base64.getDecoder().decode(data));
    } catch (IOException e) {
      // 捕获并打印IO异常
      e.printStackTrace();
    } finally {
      // 确保文件流被正确关闭，即使出现异常
      if (fileOutputStream != null) {
        try {
          fileOutputStream.flush(); // 刷新缓冲区
          fileOutputStream.close(); // 关闭文件流
        } catch (IOException e) {
          // 捕获并打印关闭文件时可能出现的IO异常
          e.printStackTrace();
        }
      }
    }
  }
}
