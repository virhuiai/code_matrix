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
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 以下示例打开 HackerNews 网站并将其页面打印为 PDF。
 * 目前 PDF 打印功能仅在 Chrome 无头模式下支持。
 *
 * @author Kenan Klisura
 */
public class PrintingPageToPdf {
  public static void main(String[] args) {
    // 创建 Chrome 启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 以无头模式(true)启动 Chrome - 目前 PDF 打印功能仅在 Chrome 无头模式下支持
    final ChromeService chromeService = launcher.launch(true);

    // 创建空白标签页，即 about:blank
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的 DevTools 服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 获取单独的命令
    final Page page = devToolsService.getPage();
    page.enable();

    // 导航到 HackerNews 网站
    page.navigate("https://news.ycombinator.com/");

    // 当页面加载事件触发时执行
    page.onLoadEventFired(
        loadEventFired -> {
          System.out.println("正在打印为 PDF...");

          final String outputFilename = "test.pdf";

          // 设置 PDF 打印参数
          Boolean landscape = false;          // 是否横向打印
          Boolean displayHeaderFooter = false; // 是否显示页眉和页脚
          Boolean printBackground = false;    // 是否打印背景
          Double scale = 1d;                  // 缩放比例
          Double paperWidth = 8.27d;          // 纸张宽度 (A4 纸格式)
          Double paperHeight = 11.7d;         // 纸张高度 (A4 纸格式)
          Double marginTop = 0d;              // 上边距
          Double marginBottom = 0d;           // 下边距
          Double marginLeft = 0d;             // 左边距
          Double marginRight = 0d;            // 右边距
          String pageRanges = "";             // 页面范围
          Boolean ignoreInvalidPageRanges = false; // 是否忽略无效的页面范围
          String headerTemplate = "";         // 页眉模板
          String footerTemplate = "";         // 页脚模板
          Boolean preferCSSPageSize = false;  // 是否优先使用 CSS 页面大小
          PrintToPDFTransferMode mode = PrintToPDFTransferMode.RETURN_AS_BASE_64; // 返回模式为 Base64

          // 调用打印方法，将结果保存到文件
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
          devToolsService.close(); // 关闭 DevTools 服务
        });

    // 等待 DevTools 服务关闭
    devToolsService.waitUntilClosed();
  }

  /**
   * 将 Base64 编码的 PDF 数据保存到文件
   *
   * @param fileName 输出文件名
   * @param printToPDF 包含 Base64 编码的 PDF 数据的对象
   */
  private static void dump(String fileName, PrintToPDF printToPDF) {
    FileOutputStream fileOutputStream = null;
    try {
      // 创建文件对象
      File file = new File(fileName);
      // 创建文件输出流
      fileOutputStream = new FileOutputStream(file);
      // 将 Base64 编码的数据解码并写入文件
      fileOutputStream.write(Base64.getDecoder().decode(printToPDF.getData()));
    } catch (IOException e) {
      // 打印异常堆栈
      e.printStackTrace();
    } finally {
      // 确保文件输出流被正确关闭
      if (fileOutputStream != null) {
        try {
          // 刷新并关闭文件输出流
          fileOutputStream.flush();
          fileOutputStream.close();
        } catch (IOException e) {
          // 打印异常堆栈
          e.printStackTrace();
        }
      }
    }
  }
}
