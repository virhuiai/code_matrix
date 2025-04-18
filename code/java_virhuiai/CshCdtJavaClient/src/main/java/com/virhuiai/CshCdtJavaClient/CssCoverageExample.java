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
import com.github.kklisura.cdt.protocol.commands.CSS;
import com.github.kklisura.cdt.protocol.commands.DOM;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.protocol.events.page.LoadEventFired;
import com.github.kklisura.cdt.protocol.support.types.EventHandler;
import com.github.kklisura.cdt.protocol.support.types.EventListener;
import com.github.kklisura.cdt.protocol.types.css.CSSStyleSheetHeader;
import com.github.kklisura.cdt.protocol.types.css.RuleUsage;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 这个示例用于分析github.com网站的CSS覆盖率
 *
 * @author Kenan Klisura
 */
public class CssCoverageExample {
  public static void main(String[] args) throws InterruptedException {
    // 创建Chrome启动器
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动Chrome浏览器，参数为false表示以常规模式启动，true则为无头模式
    final ChromeService chromeService = launcher.launch(false);

    // 创建一个空白标签页（即about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 获取此标签页的DevTools服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 启动CSS覆盖率分析并导航到github.com
    final Collection<CoverageData> coverage =
        new CssCoverage(devToolsService).start("https://github.com");

    // 打印表头
    System.out.printf(
        "%92.92s %16.16s %16.16s %16.16s\r\n", "URL", "Total bytes", "Unused bytes", "Coverage");

    // 遍历并打印每个CSS文件的覆盖率数据
    for (CoverageData coverageData : coverage) {
      System.out.printf(
          "%92.92s %16d %16d %15.2f%%\r\n",
          coverageData.getUrl(),
          coverageData.getTotalBytes(),
          coverageData.getUnusedBytes(),
          coverageData.getCoverage());
    }

    // 关闭DevTools服务
    devToolsService.close();
  }

  /**
   * CSS覆盖率分析类，实现了EventHandler<LoadEventFired>接口用于处理页面加载完成事件
   */
  static class CssCoverage implements EventHandler<LoadEventFired> {
    // Chrome DevTools服务
    private ChromeDevToolsService chromeDevToolsService;

    // 事件监听器
    private EventListener eventListener;

    // 阻塞队列，用于在主线程和事件处理线程之间传递覆盖率数据
    private BlockingQueue<List<CoverageData>> queue = new ArrayBlockingQueue<>(1);

    // 存储所有样式表信息的映射表，键为样式表ID
    private Map<String, CSSStyleSheetHeader> styleSheets = new HashMap<>();

    /**
     * 构造函数
     * @param chromeDevToolsService Chrome DevTools服务
     */
    CssCoverage(ChromeDevToolsService chromeDevToolsService) {
      this.chromeDevToolsService = chromeDevToolsService;
    }

    /**
     * 安装事件监听器并导航到指定URL
     * @param url 要导航到的URL
     */
    private void installListenerAndNavigate(String url) {
      final Page page = chromeDevToolsService.getPage();
      final CSS css = chromeDevToolsService.getCSS();
      final DOM dom = chromeDevToolsService.getDOM();

      // 启用DOM和CSS模块
      dom.enable();
      css.enable();

      // 监听样式表添加事件
      css.onStyleSheetAdded(
          styleSheetAddedEvent -> {
            final CSSStyleSheetHeader header = styleSheetAddedEvent.getHeader();
            if (header != null) {
              styleSheets.put(header.getStyleSheetId(), header);
            }
          });

      // 监听样式表移除事件
      css.onStyleSheetRemoved(
          styleSheetRemovedEvent -> {
            styleSheets.remove(styleSheetRemovedEvent.getStyleSheetId());
          });

      // 监听页面加载完成事件
      this.eventListener = page.onLoadEventFired(this);

      // 启用页面模块并导航到指定URL
      page.enable();
      page.navigate(url);
    }

    /**
     * 页面加载完成事件处理方法
     * @param event 加载完成事件
     */
    @Override
    public void onEvent(LoadEventFired event) {
      // 取消事件监听
      eventListener.unsubscribe();
      final CSS css = chromeDevToolsService.getCSS();

      // 开始跟踪CSS规则使用情况
      css.startRuleUsageTracking();

      // 停止跟踪并获取规则使用情况
      final List<RuleUsage> ruleUsages = css.stopRuleUsageTracking();

      // 处理覆盖率数据并放入队列
      queue.offer(processCoverageData(ruleUsages));
    }

    /**
     * 处理规则使用数据，计算覆盖率
     * @param ruleUsages 规则使用情况列表
     * @return 覆盖率数据列表
     */
    private List<CoverageData> processCoverageData(List<RuleUsage> ruleUsages) {
      // 按源URL存储覆盖率数据
      final Map<String, CoverageData> coveragePerSourceURL = new HashMap<>();
      // 按样式表ID存储覆盖率数据
      final Map<String, CoverageData> coverageDataMap = new HashMap<>();

      for (RuleUsage ruleUsage : ruleUsages) {
        final String styleSheetId = ruleUsage.getStyleSheetId();
        final CSSStyleSheetHeader cssStyleSheetHeader = styleSheets.get(styleSheetId);
        final String sourceURL = cssStyleSheetHeader.getSourceURL();

        // 获取或创建覆盖率数据对象
        final CoverageData coverageData =
            coverageDataMap.computeIfAbsent(
                styleSheetId,
                key -> new CoverageData(sourceURL, cssStyleSheetHeader.getLength().longValue()));

        // 减去已使用的字节数（规则的结束偏移量减去开始偏移量）
        coverageData.unusedBytes -= (ruleUsage.getEndOffset() - ruleUsage.getStartOffset());

        // 如果有源URL，则存储到按源URL的映射表中
        if (sourceURL != null && !sourceURL.isEmpty()) {
          coveragePerSourceURL.put(sourceURL, coverageData);
        }
      }

      // 返回按源URL组织的覆盖率数据列表
      return new ArrayList<>(coveragePerSourceURL.values());
    }

    /**
     * 启动覆盖率分析
     * @param url 要分析的URL
     * @return 覆盖率数据列表
     * @throws InterruptedException 如果线程被中断
     */
    public List<CoverageData> start(String url) throws InterruptedException {
      installListenerAndNavigate(url);
      // 阻塞等待直到覆盖率数据可用
      return queue.take();
    }
  }

  /**
   * 覆盖率数据类，存储CSS文件的覆盖率信息
   */
  static class CoverageData {
    private final String url;        // CSS文件的URL
    private final long totalBytes;   // 总字节数
    private long unusedBytes;        // 未使用的字节数

    /**
     * 构造函数
     * @param url CSS文件的URL
     * @param totalBytes 总字节数
     */
    public CoverageData(String url, long totalBytes) {
      this.url = url;
      this.totalBytes = totalBytes;
      this.unusedBytes = totalBytes;  // 初始时假设所有字节都未使用
    }

    /**
     * 获取URL
     * @return CSS文件的URL
     */
    public String getUrl() {
      return url;
    }

    /**
     * 获取总字节数
     * @return 总字节数
     */
    public long getTotalBytes() {
      return totalBytes;
    }

    /**
     * 获取未使用的字节数
     * @return 未使用的字节数
     */
    public long getUnusedBytes() {
      return unusedBytes;
    }

    /**
     * 计算覆盖率百分比
     * @return 未使用字节的百分比（即未覆盖的比例）
     */
    public float getCoverage() {
      return ((float) unusedBytes / totalBytes) * 100;
    }
  }
}
