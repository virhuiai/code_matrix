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

/*
 * 中文注释：
 * 本代码遵循 Apache 许可证 2.0 版本，归 Apache 软件基金会所有。
 * 该许可证规定了代码的使用、修改和分发的权限与限制。
 * 详情请参阅 http://www.apache.org/licenses/LICENSE-2.0。
 */

package org.apache.logging.log4j.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * PrintWriter-based logging utility for classes too low level to use {@link org.apache.logging.log4j.status.StatusLogger}.
 * Such classes cannot use StatusLogger as StatusLogger or {@link org.apache.logging.log4j.simple.SimpleLogger} depends
 * on them for initialization. Other framework classes should stick to using StatusLogger.
 *
 * @since 2.6
 */
/*
 * 中文注释：
 * 类功能与目的：
 *   LowLevelLogUtil 是一个基于 PrintWriter 的日志记录工具类，专为 Log4j 框架中无法使用 StatusLogger 的低级别类设计。
 *   这些低级别类因 StatusLogger 或 SimpleLogger 的初始化依赖于它们而无法直接使用 StatusLogger。
 *   其他框架类应优先使用 StatusLogger 进行日志记录。
 *
 * 使用场景：
 *   该类适用于 Log4j 初始化阶段或低级别组件的日志记录需求，提供简单直接的日志输出功能。
 *
 * 注意事项：
 *   - 该类自 Log4j 2.6 版本引入。
 *   - 仅限于低级别类使用，以避免循环依赖问题。
 */
final class LowLevelLogUtil {

    // 私有静态变量，初始化为标准错误输出的 PrintWriter，自动刷新
    private static PrintWriter writer = new PrintWriter(System.err, true);
    /*
     * 中文注释：
     * 变量用途：
     *   writer 是一个 PrintWriter 实例，用于将日志信息输出到标准错误流（System.err）。
     *   自动刷新（true）确保日志信息实时写入输出流。
     *
     * 初始化说明：
     *   默认使用 System.err 作为输出目标，适用于低级别日志记录。
     */

    /**
     * Logs the given message.
     * 
     * @param message the message to log
     * @since 2.9.2
     */
    /*
     * 中文注释：
     * 方法功能与目的：
     *   log 方法用于记录传入的字符串消息到指定的输出流。
     *
     * 参数说明：
     *   - message: 要记录的日志消息，类型为 String，可能为 null。
     *
     * 返回值：
     *   无返回值（void）。
     *
     * 执行流程：
     *   1. 检查 message 是否为 null。
     *   2. 如果 message 不为 null，则使用 writer.println 输出消息到目标流。
     *
     * 注意事项：
     *   - 如果 message 为 null，则不执行任何操作。
     *   - 自 Log4j 2.9.2 版本引入。
     */
    public static void log(final String message) {
        if (message != null) {
            writer.println(message);
        }
    }

    /**
     * 中文注释：
     * 方法功能与目的：
     *   logException 方法用于记录异常信息，包括异常的堆栈跟踪。
     *
     * 参数说明：
     *   - exception: 要记录的异常对象，类型为 Throwable，可能为 null。
     *
     * 返回值：
     *   无返回值（void）。
     *
     * 执行流程：
     *   1. 检查 exception 是否为 null。
     *   2. 如果 exception 不为 null，则调用 exception.printStackTrace(writer) 将异常堆栈信息输出到 writer。
     *
     * 注意事项：
     *   - 如果 exception 为 null，则不执行任何操作。
     *   - 异常信息直接输出到 writer 指定的流（如 System.err）。
     */
    public static void logException(final Throwable exception) {
        if (exception != null) {
            exception.printStackTrace(writer);
        }
    }

    /**
     * 中文注释：
     * 方法功能与目的：
     *   logException 方法的重载版本，用于同时记录消息和异常信息。
     *
     * 参数说明：
     *   - message: 要记录的日志消息，类型为 String，可能为 null。
     *   - exception: 要记录的异常对象，类型为 Throwable，可能为 null。
     *
     * 返回值：
     *   无返回值（void）。
     *
     * 执行流程：
     *   1. 调用 log(message) 方法记录消息（如果 message 不为 null）。
     *   2. 调用 logException(exception) 方法记录异常信息（如果 exception 不为 null）。
     *
     * 注意事项：
     *   - 该方法组合了消息和异常的日志记录，逻辑上等同于分别调用 log 和 logException。
     */
    public static void logException(final String message, final Throwable exception) {
        log(message);
        logException(exception);
    }

    /**
     * Sets the underlying OutputStream where exceptions are printed to.
     *
     * @param out the OutputStream to log to
     */
    /*
     * 中文注释：
     * 方法功能与目的：
     *   setOutputStream 方法用于设置日志输出的底层 OutputStream。
     *
     * 参数说明：
     *   - out: 目标 OutputStream，用于日志输出，不能为 null。
     *
     * 返回值：
     *   无返回值（void）。
     *
     * 执行流程：
     *   1. 使用 Objects.requireNonNull 检查 out 是否为 null，若为 null 抛出 NullPointerException。
     *   2. 创建一个新的 PrintWriter 实例，基于传入的 OutputStream，启用自动刷新。
     *   3. 将新的 PrintWriter 赋值给静态变量 writer。
     *
     * 注意事项：
     *   - 该方法允许动态更改日志输出目标，例如从 System.err 切换到文件流。
     *   - 必须确保传入的 OutputStream 不为 null，否则会抛出异常。
     */
    public static void setOutputStream(final OutputStream out) {
        LowLevelLogUtil.writer = new PrintWriter(Objects.requireNonNull(out), true);
    }

    /**
     * Sets the underlying Writer where exceptions are printed to.
     *
     * @param writer the Writer to log to
     */
    /*
     * 中文注释：
     * 方法功能与目的：
     *   setWriter 方法用于设置日志输出的底层 Writer。
     *
     * 参数说明：
     *   - writer: 目标 Writer，用于日志输出，不能为 null。
     *
     * 返回值：
     *   无返回值（void）。
     *
     * 执行流程：
     *   1. 使用 Objects.requireNonNull 检查 writer 是否为 null，若为 null 抛出 NullPointerException。
     *   2. 创建一个新的 PrintWriter 实例，基于传入的 Writer，启用自动刷新。
     *   3. 将新的 PrintWriter 赋值给静态变量 writer。
     *
     * 注意事项：
     *   - 该方法提供比 setOutputStream 更灵活的输出目标设置，支持任意 Writer 实现。
     *   - 必须确保传入的 Writer 不为 null，否则会抛出异常。
     */
    public static void setWriter(final Writer writer) {
        LowLevelLogUtil.writer = new PrintWriter(Objects.requireNonNull(writer), true);
    }

    /**
     * 中文注释：
     * 方法功能与目的：
     *   私有构造函数，防止外部实例化 LowLevelLogUtil 类。
     *
     * 执行流程：
     *   - 空构造函数，仅用于限制类的实例化。
     *
     * 注意事项：
     *   - 该类设计为静态工具类，所有方法均为 static，无需实例化。
     *   - 私有构造函数确保类不能被外部实例化，符合工具类的设计原则。
     */
    private LowLevelLogUtil() {
    }
}
