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

//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.LoggerContext;
import com.github.kklisura.cdt.launch.ChromeArguments;
import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import org.slf4j.LoggerFactory;

/**
 * 通过将日志记录器 `com.github.kklisura.cdt.launch.chrome.output` 设置为 DEBUG 级别（可以通过代码
 * 或通过日志配置文件，如示例中的 logback.xml），并向 Chrome 浏览器引入更详细日志记录的参数，可以观察到
 * Chrome 的详细输出。这应该用于调试 Chrome 的一些问题，通常不应在正常操作过程中使用。注意，当您将此
 * 日志记录器设置为 DEBUG 级别时，chrome-launcher:read-line-thread 线程将保持活动状态，直到浏览器关闭。
 *
 * <p>日志记录器 `com.github.kklisura.cdt.launch.chrome.output` 可以通过编程方式设置为 DEBUG 级别，
 * 如下面的示例所示（enableDebugChromeOutput 方法），或者通过在日志配置中添加
 *
 * <pre>
 *   <logger name="com.github.kklisura.cdt.launch.chrome.output" level="DEBUG" />
 * </pre>
 *
 * 来设置日志记录器。
 *
 * @author Kenan Klisura
 */
public class ChromeLoggingExample {

  /**
   * 以编程方式将 `com.github.kklisura.cdt.launch.chrome.output` 设置为 DEBUG 级别，以便可以观察 Chrome 输出。
   */
  private static void enableDebugChromeOutput() {
//    // 获取日志上下文
//    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//    // 设置特定日志记录器的级别为 DEBUG
//    loggerContext.getLogger("com.github.kklisura.cdt.launch.chrome.output").setLevel(Level.DEBUG);
  }

  public static void main(String[] args) throws InterruptedException {
    // 启用 Chrome 调试输出
    enableDebugChromeOutput();

    // 创建 Chrome 启动器实例
    final ChromeLauncher launcher = new ChromeLauncher();

    // 启动 Chrome 浏览器，可以选择无头模式(true)或常规模式(false)
    final ChromeService chromeService =
        launcher.launch(
            ChromeArguments.defaults(false)
                // 设置正确的参数：启用日志记录并指定日志输出到标准错误流
                .enableLogging("stderr")
                // 添加额外参数：设置日志详细级别为1
                .additionalArguments("v", "1")
                .build());

    // 创建一个空白标签页（即 about:blank）
    final ChromeTab tab = chromeService.createTab();

    // 为此标签页获取 DevTools 服务
    final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);

    // 等待一段时间，然后关闭浏览器
    Thread.sleep(2000);

    // 关闭 DevTools 服务
    devToolsService.close();
    // 关闭启动器和相关资源
    launcher.close();
  }
}
