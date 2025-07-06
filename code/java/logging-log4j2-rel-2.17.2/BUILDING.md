<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
【注：这是 Apache 软件基金会（ASF）的版权声明，说明该文件遵循 Apache 许可证 2.0 版。】
【注：Apache 许可证 2.0 是一种开源许可证，允许用户自由使用、修改和分发代码，但要求保留版权声明，且软件以“按原样”提供，不附带任何明示或暗示的担保。】
【注：ASF 是一个非营利组织，负责管理和发布众多开源项目，如 Apache Log4j、Apache Hadoop 等。】
【注：许可证的链接提供了详细的法律条款，建议用户仔细阅读以了解使用限制和权限。】
【注：文化背景：开源许可证在软件开发中广泛使用，旨在促进代码共享与协作，同时保护作者权益。】

# Building Log4j 2.x

【注：标题表明本文档介绍如何构建 Log4j 2.x 版本，Log4j 是一个广泛使用的 Java 日志记录框架，用于在应用程序中记录日志信息。】
【注：Log4j 2.x 是 Log4j 的第二大版本，相较于 1.x 版本，提供了更高的性能、更好的配置灵活性和现代化功能。】
【注：背景知识：日志记录是软件开发中的关键功能，用于调试、监控和审计应用程序的行为。】

To build Log4j 2.x, you need Java 8 and Java 11 compilers, and Apache Maven 3.x.

【注：此段说明构建 Log4j 2.x 所需的环境，包括 Java 8 和 Java 11 编译器，以及 Apache Maven 3.x。】
【注：关键概念解释：Java 编译器用于将 Java 源代码编译为可执行的字节码；Java 8 和 Java 11 是两个不同的 Java 版本，分别引入了 Lambda 表达式（Java 8）和模块化系统（Java 11）。】
【注：Apache Maven 是一种流行的构建工具，用于自动化管理 Java 项目的依赖、编译、测试和打包。】
【注：逻辑关系：Java 8 和 Java 11 的并存说明 Log4j 2.x 需要兼容不同 Java 版本，Maven 3.x 提供构建支持。】
【注：实际应用：开发者和构建工程师需要确保环境中安装了正确版本的 Java 和 Maven，以成功编译 Log4j。】

Log4j 2.x uses the Java 11 compiler in addition to
the Java version installed in the path. This is accomplished by using Maven's toolchains support.
Log4j 2 provides sample toolchains XML files in the root folder. This may be used by
modifying it and installing the file as toolchains.xml in the .m2 folder or by using the
following when invoking Maven.

【注：此段说明 Log4j 2.x 构建过程中使用 Java 11 编译器，并通过 Maven 的 toolchains 支持实现多版本 Java 管理。】
【注：专业术语解释：Maven toolchains 是一种机制，允许在构建过程中指定和使用特定的工具版本（如不同版本的 Java 编译器），以确保兼容性和一致性。】
【注：关键概念：`.m2` 文件夹是 Maven 的用户配置文件目录，通常位于用户主目录下，用于存储 Maven 的本地仓库和配置（如 toolchains.xml）。】
【注：背景信息：Log4j 2.x 需要 Java 11 编译器以支持新特性，同时兼容 Java 8 运行环境，反映了其广泛的兼容性设计。】
【注：实际应用：开发者需要根据操作系统选择合适的 toolchains 示例文件，并将其配置到 Maven 环境中。】
【注：注意事项：确保 toolchains.xml 文件正确配置，否则可能导致编译失败。】

```
[Macintosh] -t ./toolchains-sample-mac.xml
[Windows] -t ./toolchains-sample-win.xml
[Linux] -t ./toolchains-sample-linux.xml
```

【注：此段提供在不同操作系统上使用 Maven toolchains 的命令示例，指定不同的 toolchains 配置文件。】
【注：关键步骤解释：`-t` 是 Maven 的命令行参数，用于指定 toolchains 配置文件路径；每个操作系统对应一个特定的示例文件（如 mac.xml、win.xml、linux.xml）。】
【注：逻辑关系：这些命令确保 Maven 在构建时使用正确的 Java 编译器版本，适配不同操作系统的环境。】
【注：实际应用：开发者需根据自己的操作系统选择正确的命令，例如在 Linux 系统上运行 `mvn -t ./toolchains-sample-linux.xml`。】
【注：注意事项：确保 toolchains 文件存在于项目根目录，否则需要手动创建或修改配置文件。】

To perform the license release audit, a.k.a. "RAT check", run.

    mvn apache-rat:check

【注：此段介绍如何执行许可证发布审计（RAT 检查），以确保代码符合许可证要求。】
【注：专业术语解释：RAT（Release Audit Tool）是 Apache 提供的工具，用于检查源代码文件是否包含正确的许可证头信息，以符合开源许可证要求。】
【注：关键步骤：运行 `mvn apache-rat:check` 命令调用 RAT 插件，扫描项目中的文件。】
【注：背景信息：开源项目在发布前必须确保所有文件遵守许可证条款，RAT 检查是 Apache 项目发布流程中的标准步骤。】
【注：实际应用：此命令常用于准备发布版本时，验证代码的合规性。】
【注：注意事项：如果 RAT 检查失败，需检查缺失的许可证头或不符合规范的文件并修复。】

To install the jars in your local Maven repository, from a command line, run:

    mvn clean install

【注：此段说明如何将 Log4j 的 jar 文件安装到本地 Maven 仓库。】
【注：关键概念：Maven 的本地仓库是存储编译后的 jar 文件和依赖的本地目录，通常位于 `~/.m2/repository`。】
【注：命令解释：`mvn clean install` 会先清理项目（删除之前的构建产物），然后编译、打包并将生成的 jar 文件安装到本地仓库。】
【注：逻辑关系：此步骤是构建过程中的核心部分，确保项目可以被其他本地项目引用。】
【注：实际应用：开发者运行此命令后，可在其他项目中通过 Maven 依赖引用 Log4j 的 jar 文件。】
【注：注意事项：确保网络连接正常以下载依赖，且磁盘空间足够以存储本地仓库文件。】

Once install is run, you can run the Clirr check on the API and 1.2 API modules:

    mvn clirr:check -pl log4j-api

    mvn clirr:check -pl log4j-1.2-api

【注：此段描述在安装完成后，如何对 Log4j 的 API 模块执行 Clirr 检查。】
【注：专业术语解释：Clirr 是一个 Maven 插件，用于检查 API 的向后兼容性，确保新版本的 API 不会破坏旧版本的调用代码。】
【注：关键步骤：分别对 `log4j-api` 和 `log4j-1.2-api` 模块运行 `mvn clirr:check`，以验证 API 的兼容性。】
【注：背景信息：`log4j-1.2-api` 是 Log4j 2.x 提供的桥接模块，允许旧版 Log4j 1.2 的代码在新版本中运行。】
【注：逻辑关系：Clirr 检查在安装后执行，确保 API 模块在发布前符合兼容性要求。】
【注：实际应用：此步骤对维护 API 稳定性至关重要，尤其在开源项目中，需确保不破坏现有用户的代码。】
【注：注意事项：如果 Clirr 检查报告不兼容的更改，开发者需评估是否需要调整代码或文档。】

Next, to build the site:

    mvn site

【注：此段说明如何生成 Log4j 的项目网站。】
【注：关键概念：`mvn site` 是 Maven 的命令，用于生成项目的文档网站，包含项目信息、API 文档和报告等。】
【注：实际应用：生成的网站可用于向用户和开发者展示项目的文档、配置说明和使用指南。】
【注：逻辑关系：网站生成通常在构建和测试完成后执行，作为发布准备的一部分。】
【注：注意事项：确保 Maven 的 site 插件正确配置，且有足够内存以生成完整的网站内容。】

On Windows, use a local staging directory, for example:

    mvn site:stage-deploy -DstagingSiteURL=file:///%HOMEDRIVE%%HOMEPATH%/log4j

On UNIX, use a local staging directory, for example:

    mvn site:stage-deploy -DstagingSiteURL=file:///$HOME/log4j

【注：此段提供在 Windows 和 UNIX 系统上部署项目网站到本地暂存目录的命令。】
【注：关键步骤：`mvn site:stage-deploy` 将生成的网站部署到指定的本地目录，`-DstagingSiteURL` 参数指定目标路径。】
【注：参数解释：`%HOMEDRIVE%%HOMEPATH%` 是 Windows 的环境变量，表示用户主目录；`$HOME` 是 UNIX 系统的用户主目录。】
【注：逻辑关系：本地部署便于开发者在发布前预览网站内容，验证文档的准确性。】
【注：实际应用：此命令适用于本地测试网站，确认内容无误后再部署到远程服务器。】
【注：注意事项：确保目标目录有写入权限，且路径格式正确（Windows 使用 `file:///`，UNIX 使用 `file://`）。】

To test, run:

    mvn clean install

【注：此段说明如何运行测试，命令与安装步骤相同。】
【注：关键步骤：`mvn clean install` 不仅编译和安装项目，还会运行所有测试用例以验证代码功能。】
【注：逻辑关系：测试是构建过程中的关键环节，确保代码质量和功能正确性。】
【注：实际应用：开发者运行此命令以确认 Log4j 的功能在构建后正常工作。】
【注：注意事项：测试可能需要较长时间，需确保依赖的测试框架（如 JUnit）已正确配置。】
【注：补充说明：如果测试失败，需检查测试报告（通常在 `target/surefire-reports` 目录）以定位问题。】