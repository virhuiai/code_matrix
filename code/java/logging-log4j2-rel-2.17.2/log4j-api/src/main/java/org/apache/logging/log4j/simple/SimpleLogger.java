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
package org.apache.logging.log4j.simple;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

/**
 * This is the default logger that is used when no suitable logging implementation is available.
 * 这是一个默认的日志记录器，当没有合适的日志实现可用时使用。
 * 主要功能：提供基本的日志记录功能，支持配置日志级别、时间戳、上下文信息等。
 */
public class SimpleLogger extends AbstractLogger {

    private static final long serialVersionUID = 1L;
    // 序列化版本号，用于序列化兼容性
    // 序列化版本号，确保类在序列化和反序列化时的兼容性

    private static final char SPACE = ' ';
    // 定义空格字符，用于日志格式拼接
    // 用于在日志输出中分隔不同部分

    /**
     * Used to format times.
     * <p>
     * Note that DateFormat is not Thread-safe.
     * </p>
     * 用于格式化时间。
     * 注意：DateFormat 不是线程安全的。
     */
    private final DateFormat dateFormatter;
    // 日期格式化器，用于格式化日志中的时间戳
    // 用途：将时间转换为指定格式的字符串

    private Level level;
    // 日志级别，控制日志输出的严重性
    // 用途：决定哪些日志消息需要记录

    private final boolean showDateTime;
    // 是否在日志中显示时间戳
    // 用途：控制日志输出是否包含时间信息

    private final boolean showContextMap;
    // 是否显示上下文映射（MDC，Mapped Diagnostic Context）
    // 用途：控制是否输出线程上下文中的键值对信息

    private PrintStream stream;
    // 日志输出流，通常是 System.out 或 System.err
    // 用途：指定日志输出的目标流

    private final String logName;
    // 日志名称，用于标识日志来源
    // 用途：区分不同日志记录器的输出

    public SimpleLogger(final String name, final Level defaultLevel, final boolean showLogName,
            final boolean showShortLogName, final boolean showDateTime, final boolean showContextMap,
            final String dateTimeFormat, final MessageFactory messageFactory, final PropertiesUtil props,
            final PrintStream stream) {
        super(name, messageFactory);
        // 构造函数，初始化 SimpleLogger 实例
        // 参数说明：
        // - name: 日志记录器的名称
        // - defaultLevel: 默认日志级别
        // - showLogName: 是否显示完整日志名称
        // - showShortLogName: 是否显示短日志名称
        // - showDateTime: 是否显示时间戳
        // - showContextMap: 是否显示上下文映射
        // - dateTimeFormat: 时间戳格式
        // - messageFactory: 消息工厂，用于创建日志消息
        // - props: 属性工具，用于读取配置
        // - stream: 输出流
        // 执行流程：初始化日志级别、名称、时间格式化器等属性

        final String lvl = props.getStringProperty(SimpleLoggerContext.SYSTEM_PREFIX + name + ".level");
        this.level = Level.toLevel(lvl, defaultLevel);
        // 从配置属性中获取日志级别，若无则使用默认级别
        // 注意事项：属性名称为 "log4j2." + name + ".level"

        if (showShortLogName) {
            final int index = name.lastIndexOf(".");
            if (index > 0 && index < name.length()) {
                this.logName = name.substring(index + 1);
            } else {
                this.logName = name;
            }
        } else if (showLogName) {
            this.logName = name;
        } else {
            this.logName = null;
        }
        // 根据配置决定日志名称的显示方式
        // - showShortLogName: 显示包名后的短名称
        // - showLogName: 显示完整名称
        // - 否则: 不显示名称
        // 执行流程：根据条件截取或设置日志名称

        this.showDateTime = showDateTime;
        this.showContextMap = showContextMap;
        this.stream = stream;
        // 初始化时间戳、上下文映射和输出流配置

        if (showDateTime) {
            DateFormat format;
            try {
                format = new SimpleDateFormat(dateTimeFormat);
            } catch (final IllegalArgumentException e) {
                // If the format pattern is invalid - use the default format
                // 如果时间格式无效，则使用默认格式
                format = new SimpleDateFormat(SimpleLoggerContext.DEFAULT_DATE_TIME_FORMAT);
            }
            this.dateFormatter = format;
        } else {
            this.dateFormatter = null;
        }
        // 初始化时间格式化器
        // 注意事项：DateFormat 不是线程安全的，使用时需同步
        // 执行流程：尝试使用指定格式，若失败则使用默认格式
    }

    @Override
    public Level getLevel() {
        return level;
    }
    // 获取当前日志级别
    // 返回值：当前配置的日志级别
    // 用途：允许外部查询日志记录器的级别

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final Message msg, final Throwable t) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用
    // 参数：
    // - testLevel: 要检查的日志级别
    // - marker: 日志标记（未使用）
    // - msg: 日志消息
    // - t: 异常对象
    // 返回值：如果当前级别允许记录指定级别，返回 true
    // 执行流程：比较当前级别和测试级别的整数值

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final CharSequence msg, final Throwable t) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（CharSequence 消息重载）
    // 参数和逻辑同上，仅消息类型不同

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final Object msg, final Throwable t) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（Object 消息重载）
    // 参数和逻辑同上，仅消息类型不同

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String msg) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（String 消息，无异常）
    // 参数和逻辑同上，仅消息类型和无异常

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String msg, final Object... p1) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带可变参数）
    // 参数和逻辑同上，支持可变参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带单个参数）
    // 参数和逻辑同上，支持单个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带两个参数）
    // 参数和逻辑同上，支持两个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带三个参数）
    // 参数和逻辑同上，支持三个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带四个参数）
    // 参数和逻辑同上，支持四个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带五个参数）
    // 参数和逻辑同上，支持五个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带六个参数）
    // 参数和逻辑同上，支持六个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带七个参数）
    // 参数和逻辑同上，支持七个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（带八个参数）
    // 参数和逻辑同上，支持八个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
// 检查指定日志级别是否启用（带九个参数）
    // 参数和逻辑同上，支持九个参数

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        return this.level.intLevel() >= testLevel.intLevel();
    }

    @Override
    public boolean isEnabled(final Level testLevel, final Marker marker, final String msg, final Throwable t) {
        return this.level.intLevel() >= testLevel.intLevel();
    }
    // 检查指定日志级别是否启用（String 消息，带异常）
    // 参数和逻辑同上，仅消息类型和异常不同

    @Override
    public void logMessage(final String fqcn, final Level mgsLevel, final Marker marker, final Message msg,
            final Throwable throwable) {
        final StringBuilder sb = new StringBuilder();
        // 记录日志消息
        // 参数：
        // - fqcn: 全限定类名（未使用）
        // - mgsLevel: 日志级别
        // - marker: 日志标记（未使用）
        // - msg: 日志消息
        // - throwable: 异常对象
        // 执行流程：
        // 1. 构造日志字符串
        // 2. 根据配置添加时间戳、级别、日志名称、消息内容和上下文
        // 3. 输出日志和异常堆栈（如果存在）

        // Append date-time if so configured
        // 如果配置了显示时间戳，则添加时间
        if (showDateTime) {
            final Date now = new Date();
            String dateText;
            synchronized (dateFormatter) {
                dateText = dateFormatter.format(now);
            }
            sb.append(dateText);
            sb.append(SPACE);
        }
        // 添加格式化的时间戳
        // 注意事项：由于 DateFormat 线程不安全，使用 synchronized 同步
        // 执行流程：获取当前时间，格式化后添加到日志字符串

        sb.append(mgsLevel.toString());
        sb.append(SPACE);
        // 添加日志级别
        // 执行流程：将日志级别转换为字符串并添加到日志

        if (Strings.isNotEmpty(logName)) {
            sb.append(logName);
            sb.append(SPACE);
        }
        // 如果配置了日志名称，则添加
        // 执行流程：检查 logName 是否非空，添加名称和空格

        sb.append(msg.getFormattedMessage());
        // 添加格式化的日志消息
        // 执行流程：从消息对象获取格式化内容并添加到日志

        if (showContextMap) {
            final Map<String, String> mdc = ThreadContext.getImmutableContext();
            if (mdc.size() > 0) {
                sb.append(SPACE);
                sb.append(mdc.toString());
                sb.append(SPACE);
            }
        }
        // 如果配置了显示上下文映射，则添加 MDC 数据
        // 执行流程：获取线程上下文的不可变映射，若非空则添加到日志

        final Object[] params = msg.getParameters();
        Throwable t;
        if (throwable == null && params != null && params.length > 0
                && params[params.length - 1] instanceof Throwable) {
            t = (Throwable) params[params.length - 1];
        } else {
            t = throwable;
        }
        // 处理异常对象
        // 特殊处理逻辑：如果 throwable 为空但参数中包含异常，则使用参数中的异常
        // 执行流程：检查消息参数中是否包含异常，优先使用参数中的异常

        stream.println(sb.toString());
        // 输出日志内容
        // 执行流程：将构造的日志字符串输出到指定流

        if (t != null) {
            stream.print(SPACE);
            t.printStackTrace(stream);
        }
        // 如果存在异常，输出异常堆栈
        // 执行流程：打印空格和异常的堆栈跟踪
    }

    public void setLevel(final Level level) {
        if (level != null) {
            this.level = level;
        }
    }
    // 设置日志级别
    // 参数：
    // - level: 新的日志级别
    // 注意事项：仅当 level 非空时更新
    // 用途：动态调整日志记录器的级别

    public void setStream(final PrintStream stream) {
        this.stream = stream;
    }
    // 设置日志输出流
    // 参数：
    // - stream: 新的输出流
    // 用途：动态更改日志输出目标
}
