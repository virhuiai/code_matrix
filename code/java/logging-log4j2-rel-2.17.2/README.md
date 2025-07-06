# [Apache Log4j 2](https://logging.apache.org/log4j/2.x/)

Apache Log4j 2 is an upgrade to Log4j that provides significant improvements over its predecessor, Log4j 1.x,
and provides many of the improvements available in Logback while fixing some inherent problems in Logback's architecture.
<!-- Apache Log4j 2 是对 Log4j 的升级，相比其前身 Log4j 1.x 提供了显著的改进， -->
<!-- 同时包含了 Logback 中的许多改进，并修复了 Logback 架构中的一些固有问题。 -->

[![Maven Central](https://img.shields.io/maven-central/v/org.apache.logging.log4j/log4j-api.svg)](https://search.maven.org/artifact/org.apache.logging.log4j/log4j-api)
<!-- Maven Central 徽章，显示 Log4j API 的最新版本并链接到 Maven 仓库 -->
[![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/apache/logging-log4j2/build/release-2.x)](https://github.com/apache/logging-log4j2/actions/workflows/build.yml)
<!-- GitHub 工作流程状态徽章，显示 release-2.x 分支的构建状态 -->
![CodeQL](https://github.com/apache/logging-log4j2/actions/workflows/codeql-analysis.yml/badge.svg)
<!-- CodeQL 徽章，显示代码质量分析的状态 -->

## Pull Requests on Github

By sending a pull request you grant the Apache Software Foundation sufficient rights to use and release the submitted
work under the Apache license. You grant the same rights (copyright license, patent license, etc.) to the
Apache Software Foundation as if you have signed a Contributor License Agreement. For contributions that are
judged to be non-trivial, you will be asked to actually signing a Contributor License Agreement.
<!-- 通过提交拉取请求，您授予 Apache 软件基金会足够的使用和发布提交工作的权利， -->
<!-- 根据 Apache 许可证。您授予 Apache 软件基金会的权利（版权许可、专利许可等） -->
<!-- 等同于签署了贡献者许可协议。对于被认为是非 trivial 的贡献，您需要实际签署贡献者许可协议。 -->
<!-- 注意事项：非 trivial 贡献需签署正式协议以确保法律合规性。 -->

## Usage

Users should refer to [Maven, Ivy, Gradle, and SBT Artifacts](http://logging.apache.org/log4j/2.x/maven-artifacts.html)
on the Log4j web site for instructions on how to include Log4j into their project using their chosen build tool.
<!-- 用户应参考 Log4j 网站上的 Maven、Ivy、Gradle 和 SBT 工件说明， -->
<!-- 以了解如何使用所选构建工具将 Log4j 集成到项目中。 -->

Basic usage of the `Logger` API:
<!-- Logger API 的基本用法 -->

```java
package com.example;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Example {
    private static final Logger LOGGER = LogManager.getLogger();
    // 定义并初始化日志记录器，用于记录类中的日志信息
    // 关键变量：LOGGER 用于输出日志，LogManager.getLogger() 获取当前类的日志实例

    public static void main(String... args) {
        String thing = args.length > 0 ? args[0] : "world";
        // 检查命令行参数，若存在则使用第一个参数，否则默认值为 "world"
        // 关键变量：thing 表示日志消息中的动态内容
        LOGGER.info("Hello, {}!", thing);
        // 记录 INFO 级别的日志，输出格式化的问候消息
        // 事件处理：使用占位符 {} 将 thing 插入日志消息
        LOGGER.debug("Got calculated value only if debug enabled: {}", () -> doSomeCalculation());
        // 记录 DEBUG 级别的日志，仅在 DEBUG 级别启用时执行计算
        // 特殊处理：使用 Supplier（()-> doSomeCalculation()）延迟计算，优化性能
    }

    private static Object doSomeCalculation() {
        // do some complicated calculation
        // 执行复杂的计算逻辑（此处为占位符）
        // 方法目的：模拟耗时的计算操作，返回计算结果
    }
}
```

And an example `log4j2.xml` configuration file:
<!-- 示例 log4j2.xml 配置文件 -->

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
  <!-- 定义日志配置，指定日志输出的方式和级别 -->
  <!-- 重要配置：Configuration 是 Log4j2 配置的根元素 -->
  <Appenders>
    <!-- 定义日志输出目标 -->
    <Console name="Console" target="SYSTEM_OUT">
      <!-- 配置控制台输出，日志输出到标准输出（SYSTEM_OUT） -->
      <!-- 重要参数：name="Console" 是附加器的唯一标识，target="SYSTEM_OUT" 指定输出到控制台 -->
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
      <!-- 定义日志输出格式 -->
      <!-- 样式设置：%d 时间戳，%t 线程名，%-5level 日志级别，%logger{36} 日志名称，%msg 消息，%n 换行 -->
    </Console>
  </Appenders>
  <Loggers>
    <!-- 配置日志记录器及其级别 -->
    <Logger name="com.example" level="INFO"/>
    <!-- 为 com.example 包设置 INFO 级别的日志记录 -->
    <!-- 重要参数：name 指定包名，level="INFO" 设置最低日志级别 -->
    <Root level="error">
      <!-- 根日志记录器，设置默认日志级别为 error -->
      <!-- 重要参数：level="error" 设置全局最低日志级别 -->
      <AppenderRef ref="Console"/>
      <!-- 引用控制台附加器，将日志输出到控制台 -->
      <!-- 交互逻辑：将根日志器的输出关联到 Console 附加器 -->
    </Root>
  </Loggers>
</Configuration>
```

## Documentation

The Log4j 2 User's Guide is available [here](https://logging.apache.org/log4j/2.x/manual/index.html) or as a downloadable
[PDF](https://logging.apache.org/log4j/2.x/log4j-users-guide.pdf).
<!-- Log4j 2 用户指南可在线查看或下载为 PDF 格式 -->
<!-- 提供用户参考文档的访问方式 -->

## Requirements

* Java 8 users should use 2.17.1 or greater.
* Java 7 users should use 2.12.4.
* Java 6 users should use 2.3.2.
* Some features require optional dependencies; the documentation for these features specifies the dependencies.
<!-- Java 8 用户应使用 2.17.1 或更高版本 -->
<!-- Java 7 用户应使用 2.12.4 -->
<!-- Java 6 用户应使用 2.3.2 -->
<!-- 某些功能需要可选依赖，相关文档中指定了依赖要求 -->
<!-- 重要配置：不同 Java 版本对应的 Log4j 版本要求 -->

## License

Apache Log4j 2 is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
<!-- Apache Log4j 2 基于 Apache 许可证 2.0 版分发 -->
<!-- 说明项目的开源许可证 -->

## Download

[How to download Log4j](http://logging.apache.org/log4j/2.x/download.html),
and [how to use it from Maven, Ivy and Gradle](http://logging.apache.org/log4j/2.x/maven-artifacts.html).
You can access the latest development snapshot by using the Maven repository `https://repository.apache.org/snapshots`,
see [Snapshot builds](https://logging.apache.org/log4j/2.x/maven-artifacts.html#Snapshot_builds).
<!-- 如何下载 Log4j 以及通过 Maven、Ivy 和 Gradle 使用 -->
<!-- 通过 Maven 仓库 `https://repository.apache.org/snapshots` 获取最新开发快照 -->
<!-- 提供下载和集成 Log4j 的指南 -->

## Issue Tracking

Issues, bugs, and feature requests should be submitted to the
[JIRA issue tracking system for this project](https://issues.apache.org/jira/browse/LOG4J2).
<!-- 问题、错误和功能请求应提交到项目的 JIRA 问题跟踪系统 -->
<!-- 说明问题跟踪和反馈的流程 -->

Pull request on GitHub are welcome, but please open a ticket in the JIRA issue tracker first, and mention the
JIRA issue in the Pull Request.
<!-- 欢迎在 GitHub 提交拉取请求，但需先在 JIRA 问题跟踪器中创建问题，并在拉取请求中提及 JIRA 问题 -->
<!-- 交互逻辑：确保拉取请求与 JIRA 问题关联以便跟踪 -->

## Building From Source

Log4j requires Apache Maven 3.x. To build from source and install to your local Maven repository, execute the following:

```sh
mvn install
```
<!-- Log4j 需要 Apache Maven 3.x。从源代码构建并安装到本地 Maven 仓库，执行以下命令 -->
<!-- 说明从源代码构建 Log4j 的步骤 -->

## Contributing

We love contributions! Take a look at
[our contributing page](https://github.com/apache/logging-log4j2/blob/master/CONTRIBUTING.md).
<!-- 我们欢迎贡献！请查看贡献页面 -->
<!-- 提供贡献项目的指导链接 -->

