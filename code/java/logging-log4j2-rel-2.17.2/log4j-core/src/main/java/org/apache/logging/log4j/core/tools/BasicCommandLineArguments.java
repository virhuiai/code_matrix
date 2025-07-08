/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.tools;
// 定义包名，表示该类属于 Log4j 核心工具模块。

import org.apache.logging.log4j.core.tools.picocli.CommandLine.Option;
// 导入 picocli 库中的 Option 注解，用于命令行参数解析。

/**
 * Basic command line arguments.
 * 基础命令行参数。
 *
 * <p>
 * This class defines common command-line arguments that can be used across various Log4j tools.
 * 这个类定义了可以在各种 Log4j 工具中使用的通用命令行参数。
 * </p>
 */
public class BasicCommandLineArguments {

    /**
     * Help option.
     * 帮助选项。
     *
     * <p>
     * This field is annotated with `@Option` from picocli, indicating it's a command-line option.
     * 此字段使用 picocli 的 `@Option` 注解，表示它是一个命令行选项。
     * </p>
     */
    @Option(names = { "--help", "-?", "-h" }, usageHelp = true, description = "Prints this help and exits.")
    // 定义一个命令行选项，可以通过 --help, -?, 或 -h 触发。
    // usageHelp = true 表示当此选项被使用时，picocli 会自动生成并打印帮助信息，然后退出程序。
    // description 提供了此选项的简短描述，用于帮助信息中。
    private boolean help;
    // help 变量用于存储是否请求显示帮助信息。
    // 默认值为 false，表示不显示帮助。

    /**
     * Returns whether the help option was specified.
     * 返回是否指定了帮助选项。
     *
     * @return true if the help option was specified, false otherwise.
     * 如果指定了帮助选项，则返回 true，否则返回 false。
     */
    public boolean isHelp() {
        // isHelp 方法用于获取 help 字段的值。
        // 这是一个标准的 getter 方法，遵循 JavaBeans 规范。
        return help;
    }

    /**
     * Sets whether the help option was specified.
     * 设置是否指定了帮助选项。
     *
     * @param help true if the help option was specified, false otherwise.
     * 如果指定了帮助选项，则为 true，否则为 false。
     */
    public void setHelp(final boolean help) {
        // setHelp 方法用于设置 help 字段的值。
        // 这是一个标准的 setter 方法，遵循 JavaBeans 规范。
        // final 关键字表示传入的 help 参数在方法内部不能被重新赋值。
        this.help = help;
    }

}
