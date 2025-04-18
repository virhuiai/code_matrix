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
import com.github.kklisura.cdt.protocol.types.page.PrintToPDF;
import com.github.kklisura.cdt.protocol.types.page.PrintToPDFTransferMode;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.factory.impl.DefaultWebSocketContainerFactory;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 此示例演示如何在Tyrus客户端中增加传入缓冲区大小。
 * 使用以下示例修复Tyrus缓冲区溢出问题。
 * 当传入消息大小大于Tyrus客户端中的传入缓冲区时，客户端会断开连接，此时会出现此问题。
 *
 * @author Kenan Klisura
 */
public class IncreasedIncomingBufferInTyrusExample {

  static {
    // 将传入缓冲区设置为24MB
    System.setProperty(
        DefaultWebSocketContainerFactory.WEBSOCKET_INCOMING_BUFFER_PROPERTY,
        Long.toString((long) DefaultWebSocketContainerFactory.MB * 24));
  }

  public static void main(String[] args) {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 以无头模式(true)启动Chrome - 目前PDF打印仅在Chrome无头模式下支持
    final ChromeService chromeService = launcher.launch(true);

    // 创建空白标签页，即about:blank
    final ChromeTab tab = chromeService.createTab();

    // 为此标签页获取DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单个命令实例
    final Page page = devToolsService.getPage();
    page.enable();

    // 导航到github.com
    page.navigate("https://github.com");

    // 页面加载事件触发时的回调
    page.onLoadEventFired(
        loadEventFired -> {
          System.out.println("正在打印为PDF...");

          // 输出文件名
          final String outputFilename = "test.pdf";

          // 设置PDF打印参数
          Boolean landscape = false;          // 是否横向打印
          Boolean displayHeaderFooter = false; // 是否显示页眉和页脚
          Boolean printBackground = false;     // 是否打印背景
          Double scale = 1d;                   // 缩放比例
          Double paperWidth = 8.27d;           // 纸张宽度（A4纸格式）
          Double paperHeight = 11.7d;          // 纸张高度（A4纸格式）
          Double marginTop = 0d;               // 上边距
          Double marginBottom = 0d;            // 下边距
          Double marginLeft = 0d;              // 左边距
          Double marginRight = 0d;             // 右边距
          String pageRanges = "";              // 页面范围
          Boolean ignoreInvalidPageRanges = false; // 是否忽略无效的页面范围
          String headerTemplate = "";          // 页眉模板
          String footerTemplate = "";          // 页脚模板
          Boolean preferCSSPageSize = false;   // 是否优先使用CSS页面大小
          PrintToPDFTransferMode mode = PrintToPDFTransferMode.RETURN_AS_BASE_64; // 返回模式为Base64编码

          // 调用打印方法，并将结果保存为文件
          dump(
              outputFilename,
              devToolsService
                  .getPage()
                  .printToPDF(
                      landscape,
                      displayHeaderFooter,
                      printBackground,
                      scale,
                      paperWidth,
                      paperHeight,
                      marginTop,
                      marginBottom,
                      marginLeft,
                      marginRight,
                      pageRanges,
                      ignoreInvalidPageRanges,
                      headerTemplate,
                      footerTemplate,
                      preferCSSPageSize,
                      mode));

          System.out.println("完成！");
          devToolsService.close(); // 关闭DevTools服务
        });

    // 等待直到DevTools服务关闭
    devToolsService.waitUntilClosed();
  }

  /**
   * 将Base64编码的PDF数据保存到文件
   *
   * @param fileName 输出文件名
   * @param data 包含Base64编码的PDF数据
   */
  private static void dump(String fileName, PrintToPDF data) {
    FileOutputStream fileOutputStream = null;
    try {
      // 创建文件对象
      File file = new File(fileName);
      // 创建文件输出流
      fileOutputStream = new FileOutputStream(file);
      // 将Base64解码并写入文件
      fileOutputStream.write(Base64.getDecoder().decode(data.getData()));
    } catch (IOException e) {
      // 输出IO异常堆栈信息
      e.printStackTrace();
    } finally {
      // 确保文件输出流被正确关闭
      if (fileOutputStream != null) {
        try {
          fileOutputStream.flush(); // 刷新缓冲区
          fileOutputStream.close(); // 关闭文件输出流
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
