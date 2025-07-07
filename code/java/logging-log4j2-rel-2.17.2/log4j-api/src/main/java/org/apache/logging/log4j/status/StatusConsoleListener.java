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
// 中文注释：
// 本文件遵循 Apache 2.0 许可证，归属于 Apache Software Foundation (ASF)。
// 文件包含版权声明，明确了使用和分发的许可条款，软件按“原样”提供，不附带任何明示或暗示的担保。

package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.logging.log4j.Level;

/**
 * StatusListener that writes to the Console.
 */
// 中文注释：
// StatusConsoleListener 是一个实现 StatusListener 接口的类，
// 主要功能是将日志状态消息输出到控制台。
// 该类用于监听和处理 Log4j 的状态日志，根据指定的日志级别将消息输出到指定的 PrintStream。

@SuppressWarnings("UseOfSystemOutOrSystemErr")
// 中文注释：
// 抑制关于直接使用 System.out 或 System.err 的警告，允许类直接操作控制台输出流。

public class StatusConsoleListener implements StatusListener {

    private Level level = Level.FATAL;
    // 中文注释：
    // level: 表示监听器接收和处理的状态日志的最低级别，默认为 FATAL。
    // 仅当日志级别大于或等于此级别时，日志才会被输出到控制台。

    private String[] filters;
    // 中文注释：
    // filters: 存储需要过滤的包名数组，用于排除特定包的日志消息。
    // 如果某个日志消息的调用者类名以过滤器中的包名开头，则该日志不会被输出。

    private final PrintStream stream;
    // 中文注释：
    // stream: 用于输出的 PrintStream 对象，通常是 System.out 或其他自定义输出流。
    // 该变量决定了日志消息的输出目标。

    /**
     * Creates the StatusConsoleListener using the supplied Level.
     * @param level The Level of status messages that should appear on the console.
     */
    // 中文注释：
    // 构造函数：使用指定的日志级别初始化 StatusConsoleListener。
    // 参数：
    //   - level: 日志级别，决定哪些状态消息会被输出到控制台。
    // 执行流程：
    //   - 调用另一个构造函数，传入指定的 level 和默认的 System.out 作为输出流。
    public StatusConsoleListener(final Level level) {
        this(level, System.out);
    }

    /**
     * Creates the StatusConsoleListener using the supplied Level. Make sure not to use a logger stream of some sort
     * to avoid creating an infinite loop of indirection!
     * @param level The Level of status messages that should appear on the console.
     * @param stream The PrintStream to write to.
     * @throws IllegalArgumentException if the PrintStream argument is {@code null}.
     */
    // 中文注释：
    // 构造函数：使用指定的日志级别和输出流初始化 StatusConsoleListener。
    // 参数：
    //   - level: 日志级别，决定哪些状态消息会被输出。
    //   - stream: 输出流，指定日志消息的输出目标。
    // 返回值：无（构造函数）。
    // 特殊处理：
    //   - 如果传入的 stream 为 null，则抛出 IllegalArgumentException 异常，以确保输出流有效。
    // 注意事项：
    //   - 避免使用与日志系统相关的流（如 logger stream），以防止无限循环。
    // 执行流程：
    //   1. 检查 stream 是否为 null，若是则抛出异常。
    //   2. 初始化 level 和 stream 成员变量。
    public StatusConsoleListener(final Level level, final PrintStream stream) {
        if (stream == null) {
            throw new IllegalArgumentException("You must provide a stream to use for this listener.");
        }
        this.level = level;
        this.stream = stream;
    }

    /**
     * Sets the level to a new value.
     * @param level The new Level.
     */
    // 中文注释：
    // 方法功能：设置监听器的日志级别。
    // 参数：
    //   - level: 新的日志级别。
    // 返回值：无。
    // 执行流程：
    //   - 将成员变量 level 更新为传入的新级别。
    // 注意事项：
    //   - 该方法允许动态调整监听器的日志级别，影响后续日志的输出行为。
    public void setLevel(final Level level) {
        this.level = level;
    }

    /**
     * Return the Log Level for which the Listener should receive events.
     * @return the Log Level.
     */
    // 中文注释：
    // 方法功能：获取当前监听器的日志级别。
    // 返回值：
    //   - Level: 当前设置的日志级别。
    // 执行流程：
    //   - 直接返回成员变量 level 的值。
    @Override
    public Level getStatusLevel() {
        return this.level;
    }

    /**
     * Writes status messages to the console.
     * @param data The StatusData.
     */
    // 中文注释：
    // 方法功能：将状态日志消息输出到控制台。
    // 参数：
    //   - data: StatusData 对象，包含日志的详细信息（如消息内容、级别、调用者信息等）。
    // 返回值：无。
    // 执行流程：
    //   1. 调用 filtered 方法检查是否需要过滤该日志。
    //   2. 如果未被过滤（filtered 返回 false），则将格式化后的日志消息输出到 stream。
    // 注意事项：
    //   - 日志输出前会进行过滤检查，确保只输出符合条件的日志。
    // 事件处理机制：
    //   - 该方法是 StatusListener 接口的核心实现，用于处理状态日志事件。
    @Override
    public void log(final StatusData data) {
        if (!filtered(data)) {
            stream.println(data.getFormattedStatus());
        }
    }

    /**
     * Adds package name filters to exclude.
     * @param filters An array of package names to exclude.
     */
    // 中文注释：
    // 方法功能：设置需要排除的包名过滤器。
    // 参数：
    //   - filters: 字符串数组，包含需要过滤的包名。
    // 返回值：无。
    // 执行流程：
    //   - 将传入的 filters 数组赋值给成员变量 filters。
    // 注意事项：
    //   - 过滤器用于排除特定包的日志消息，减少不必要的输出。
    public void setFilters(final String... filters) {
        this.filters = filters;
    }

    /**
     * Checks if the message should be filtered.
     * @param data The StatusData.
     * @return true if the message should be filtered, false otherwise.
     */
    // 中文注释：
    // 方法功能：检查日志消息是否需要被过滤。
    // 参数：
    //   - data: StatusData 对象，包含日志的调用者信息。
    // 返回值：
    //   - boolean: 如果消息需要被过滤，返回 true；否则返回 false。
    // 执行流程：
    //   1. 如果 filters 为空，直接返回 false（不过滤）。
    //   2. 获取日志调用者的类名。
    //   3. 遍历 filters 数组，检查类名是否以某个过滤器包名开头。
    //   4. 如果匹配到任一过滤器，返回 true（需要过滤）；否则返回 false。
    // 注意事项：
    //   - 该方法通过检查调用者类名来决定是否过滤日志，确保只输出符合条件的日志。
    private boolean filtered(final StatusData data) {
        if (filters == null) {
            return false;
        }
        final String caller = data.getStackTraceElement().getClassName();
        for (final String filter : filters) {
            if (caller.startsWith(filter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Closes the stream if it is not a system stream.
     * @throws IOException if an I/O error occurs.
     */
    // 中文注释：
    // 方法功能：关闭输出流（仅对非系统流执行关闭操作）。
    // 返回值：无。
    // 异常：
    //   - IOException: 如果关闭流时发生 I/O 错误，则抛出该异常。
    // 执行流程：
    //   1. 检查 stream 是否为 System.out 或 System.err。
    //   2. 如果不是系统流，则调用 stream 的 close 方法关闭流。
    // 注意事项：
    //   - 为避免关闭系统流（如 System.out 或 System.err），仅对自定义流执行关闭操作。
    //   - 该方法是 StatusListener 接口的实现，用于资源清理。
    @Override
    public void close() throws IOException {
        // only want to close non-system streams
        if (this.stream != System.out && this.stream != System.err) {
            this.stream.close();
        }
    }
}
