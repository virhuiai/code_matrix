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
 * limitations under the License.
 */
package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.StackLocatorUtil;

/**
 * Wrapper class that exposes the protected AbstractLogger methods to support wrapped loggers.
 * 包装类，用于暴露 AbstractLogger 的受保护方法，以支持被包装的日志记录器。
 */
public class ExtendedLoggerWrapper extends AbstractLogger {

    private static final long serialVersionUID = 1L;
    // 序列化ID，用于确保序列化和反序列化时的版本兼容性。

    /**
     * The wrapped Logger.
     * 被包装的 ExtendedLogger 实例。
     */
    protected final ExtendedLogger logger;

    /**
     * Constructor that wraps and existing Logger.
     * 构造函数，用于包装一个已存在的 Logger 实例。
     *
     * @param logger The Logger to wrap.
     * 需要被包装的 Logger 实例。
     * @param name The name of the Logger.
     * 日志记录器的名称。
     * @param messageFactory TODO
     * 消息工厂，用于创建消息对象。此参数在原始注释中标记为TODO，表示可能需要进一步的说明或实现。
     */
    public ExtendedLoggerWrapper(final ExtendedLogger logger, final String name, final MessageFactory messageFactory) {
        super(name, messageFactory);
        // 调用父类 AbstractLogger 的构造函数，初始化日志记录器的名称和消息工厂。
        this.logger = logger;
        // 将传入的 ExtendedLogger 实例赋值给内部的 logger 字段，完成包装。
    }

    @Override
    public Level getLevel() {
        // 方法功能：获取当前日志记录器的日志级别。
        // 代码执行流程：直接调用被包装的 logger 对象的 getLevel 方法。
        return logger.getLevel();
        // 返回值：当前日志记录器的 Level 对象。
    }

    /**
     * Detect if the event would be logged.
     * 检测事件是否会被记录。
     *
     * @param level The logging Level to check.
     * 需要检查的日志级别。
     * @param marker A Marker or null.
     * 一个 Marker 对象，可以为 null，用于标记日志事件。
     * @param message The Message.
     * 需要记录的消息对象。
     * @param t A Throwable.
     * 一个 Throwable 对象，可以为 null，表示与日志事件相关的异常。
     * @return true if the event would be logged for the Level, Marker, Message and Throwable, false otherwise.
     * 如果事件会根据指定的 Level、Marker、Message 和 Throwable 进行日志记录，则返回 true，否则返回 false。
     */
    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable t) {
        // 方法功能：判断在给定日志级别、标记、消息和异常的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        return logger.isEnabled(level, marker, message, t);
        // 返回值：布尔值，表示事件是否会被记录。
    }

    /**
     * Detect if the event would be logged.
     * 检测事件是否会被记录。
     *
     * @param level The logging Level to check.
     * 需要检查的日志级别。
     * @param marker A Marker or null.
     * 一个 Marker 对象，可以为 null。
     * @param message The message CharSequence.
     * 需要记录的消息字符序列。
     * @param t A Throwable.
     * 一个 Throwable 对象，可以为 null。
     * @return true if the event would be logged for the Level, Marker, Object and Throwable, false otherwise.
     * 如果事件会根据指定的 Level、Marker、消息字符序列和 Throwable 进行日志记录，则返回 true，否则返回 false。
     */
    @Override
    public boolean isEnabled(final Level level, final Marker marker, final CharSequence message, final Throwable t) {
        // 方法功能：判断在给定日志级别、标记、字符序列消息和异常的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        return logger.isEnabled(level, marker, message, t);
        // 返回值：布尔值，表示事件是否会被记录。
    }

    /**
     * Detect if the event would be logged.
     * 检测事件是否会被记录。
     *
     * @param level The logging Level to check.
     * 需要检查的日志级别。
     * @param marker A Marker or null.
     * 一个 Marker 对象，可以为 null。
     * @param message The message.
     * 需要记录的消息对象。
     * @param t A Throwable.
     * 一个 Throwable 对象，可以为 null。
     * @return true if the event would be logged for the Level, Marker, Object and Throwable, false otherwise.
     * 如果事件会根据指定的 Level、Marker、Object 消息和 Throwable 进行日志记录，则返回 true，否则返回 false。
     */
    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Object message, final Throwable t) {
        // 方法功能：判断在给定日志级别、标记、任意类型消息对象和异常的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        return logger.isEnabled(level, marker, message, t);
        // 返回值：布尔值，表示事件是否会被记录。
    }

    /**
     * Detect if the event would be logged.
     * 检测事件是否会被记录。
     *
     * @param level The logging Level to check.
     * 需要检查的日志级别。
     * @param marker A Marker or null.
     * 一个 Marker 对象，可以为 null。
     * @param message The message.
     * 需要记录的字符串消息。
     * @return true if the event would be logged for the Level, Marker, message and parameter.
     * 如果事件会根据指定的 Level、Marker 和字符串消息进行日志记录，则返回 true。
     */
    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message) {
        // 方法功能：判断在给定日志级别、标记和字符串消息的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        return logger.isEnabled(level, marker, message);
        // 返回值：布尔值，表示事件是否会被记录。
    }

    /**
     * Detect if the event would be logged.
     * 检测事件是否会被记录。
     *
     * @param level The logging Level to check.
     * 需要检查的日志级别。
     * @param marker A Marker or null.
     * 一个 Marker 对象，可以为 null。
     * @param message The message.
     * 需要记录的字符串消息。
     * @param params The parameters.
     * 消息的参数列表。
     * @return true if the event would be logged for the Level, Marker, message and parameter.
     * 如果事件会根据指定的 Level、Marker、字符串消息和参数进行日志记录，则返回 true。
     */
    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object... params) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和可变参数列表的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        return logger.isEnabled(level, marker, message, params);
        // 返回值：布尔值，表示事件是否会被记录。
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和单个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0: 消息的第一个参数。
        return logger.isEnabled(level, marker, message, p0);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和两个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0: 消息的第一个参数。
        // 参数 p1: 消息的第二个参数。
        return logger.isEnabled(level, marker, message, p0, p1);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和三个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0: 消息的第一个参数。
        // 参数 p1: 消息的第二个参数。
        // 参数 p2: 消息的第三个参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和四个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0 到 p3: 消息的参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2, p3);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和五个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0 到 p4: 消息的参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和六个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0 到 p5: 消息的参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和七个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0 到 p6: 消息的参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和八个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0 到 p7: 消息的参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和九个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0 到 p8: 消息的参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和十个参数的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        // 参数 p0 到 p9: 消息的参数。
        return logger.isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    /**
     * Detect if the event would be logged.
     * 检测事件是否会被记录。
     *
     * @param level The logging Level to check.
     * 需要检查的日志级别。
     * @param marker A Marker or null.
     * 一个 Marker 对象，可以为 null。
     * @param message The message.
     * 需要记录的字符串消息。
     * @param t A Throwable.
     * 一个 Throwable 对象，可以为 null。
     * @return true if the event would be logged for the Level, Marker, message and Throwable, false otherwise.
     * 如果事件会根据指定的 Level、Marker、字符串消息和 Throwable 进行日志记录，则返回 true，否则返回 false。
     */
    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Throwable t) {
        // 方法功能：判断在给定日志级别、标记、字符串消息和异常的情况下，是否会实际记录该日志事件。
        // 代码执行流程：直接调用被包装的 logger 对象的 isEnabled 方法进行判断。
        return logger.isEnabled(level, marker, message, t);
        // 返回值：布尔值，表示事件是否会被记录。
    }

    /**
     * Always log an event. This tends to be already guarded by an enabled check, so this method should not check for
     * the logger level again
     * 总是记录一个事件。此方法通常已经被 enabled 检查所保护，因此不应再次检查日志级别。
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>
     * 调用者的完全限定类名。
     * @param level The logging level
     * 日志级别。
     * @param marker The Marker
     * 日志标记。
     * @param message The Message.
     * 日志消息。
     * @param t A Throwable or null.
     * 一个 Throwable 对象，可以为 null。
     */
    @Override
    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message message,
            final Throwable t) {
        // 方法功能：强制记录一个日志事件，不进行日志级别检查（因为通常在调用此方法前已完成检查）。
        // 代码执行流程：
        // 1. 判断被包装的 logger 是否是 LocationAwareLogger 的实例，并且是否需要定位信息。
        if (logger instanceof LocationAwareLogger && requiresLocation()) {
            // 如果是 LocationAwareLogger 且需要定位，则调用其 logMessage 方法，并通过 StackLocatorUtil 获取调用位置信息。
            ((LocationAwareLogger) logger).logMessage(level, marker, fqcn, StackLocatorUtil.calcLocation(fqcn),
                message, t);
        } else {
            // 否则，直接调用被包装的 logger 的 logMessage 方法。
            logger.logMessage(fqcn, level, marker, message, t);
        }
        // 特殊处理逻辑：根据被包装的 logger 类型和是否需要定位信息，选择不同的日志记录方式。
        // 关键函数：StackLocatorUtil.calcLocation(fqcn) 用于计算调用者的位置信息。
    }
}
