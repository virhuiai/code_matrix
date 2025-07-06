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
package org.apache.logging.log4j;

import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

/**
 * This is the central interface in the log4j package. Most logging operations, except configuration, are done through
 * this interface.
 *
 * <p>
 * The canonical way to obtain a Logger for a class is through {@link LogManager#getLogger()}. Typically, each class
 * gets its own Logger named after its fully qualified class name (the default Logger name when obtained through the
 * {@link LogManager#getLogger()} method). Thus, the simplest way to use this would be like so:
 * </p>
 *
 * <pre>
 * public class MyClass {
 *     private static final Logger LOGGER = LogManager.getLogger();
 *     // ...
 * }
 * </pre>
 * <p>
 * For ease of filtering, searching, sorting, etc., it is generally a good idea to create Loggers for each class rather
 * than sharing Loggers. Instead, {@link Marker Markers} should be used for shared, filterable identification.
 * </p>
 * <p>
 * For service provider implementations, it is recommended to extend the
 * {@link org.apache.logging.log4j.spi.AbstractLogger} class rather than implementing this interface directly.
 * </p>
 *
 * Since 2.4, methods have been added to the {@code Logger} interface to support lambda expressions. The new methods
 * allow client code to lazily log messages without explicitly checking if the requested log level is enabled. For
 * example, previously one would write:
 *
 * <pre>
 * // pre-Java 8 style optimization: explicitly check the log level
 * // to make sure the expensiveOperation() method is only called if necessary
 * if (logger.isTraceEnabled()) {
 *     logger.trace(&quot;Some long-running operation returned {}&quot;, expensiveOperation());
 * }
 * </pre>
 * <p>
 * With Java 8, the same effect can be achieved with a lambda expression:
 *
 * <pre>
 * // Java-8 style optimization: no need to explicitly check the log level:
 * // the lambda expression is not evaluated if the TRACE level is not enabled
 * logger.trace(&quot;Some long-running operation returned {}&quot;, () -&gt; expensiveOperation());
 * </pre>
 *
 * <p>
 * Note that although {@link MessageSupplier} is provided, using {@link Supplier {@code Supplier<Message>}} works just the
 * same. MessageSupplier was deprecated in 2.6 and un-deprecated in 2.8.1. Anonymous class usage of these APIs
 * should prefer using Supplier instead.
 * </p>
 */
// 中文注释：这是 log4j 包的核心接口，负责除配置外的所有日志记录操作。
// 通常通过 LogManager.getLogger() 获取 Logger 实例，每个类使用其全限定类名作为 Logger 名称。
// 建议每个类创建独立的 Logger 以便于过滤、搜索和排序，使用 Marker 进行共享标识。
// 自 2.4 版本起，支持 lambda 表达式实现延迟日志记录，避免显式检查日志级别。
// 注意：MessageSupplier 已于 2.6 版本弃用，2.8.1 版本取消弃用，建议优先使用 Supplier。
public interface Logger {

    /**
     * Logs a {@link Throwable} that has been caught to a specific logging level.
     *
     * @param level The logging Level.
     * @param throwable the Throwable.
     */
    // 中文注释：记录捕获的异常到指定日志级别。
    // 参数说明：
    //   - level: 日志级别，决定日志的严重程度（如 DEBUG、INFO 等）。
    //   - throwable: 要记录的异常对象。
    // 功能：将异常信息以指定级别记录到日志中，包含异常的堆栈跟踪。
    void catching(Level level, Throwable throwable);

    /**
     * Logs a {@link Throwable} that has been caught at the {@link Level#ERROR ERROR} level.
     * Normally, one may wish to provide additional information with an exception while logging it;
     * in these cases, one would not use this method.
     * In other cases where simply logging the fact that an exception was swallowed somewhere
     * (e.g., at the top of the stack trace in a {@code main()} method),
     * this method is ideal for it.
     *
     * @param throwable the Throwable.
     */
    // 中文注释：以 ERROR 级别记录捕获的异常。
    // 参数说明：
    //   - throwable: 要记录的异常对象。
    // 功能：专门用于记录异常被捕获但无需附加额外信息的情况，例如在 main 方法中捕获异常。
    // 注意事项：如果需要附加额外日志信息，不建议使用此方法。
    void catching(Throwable throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#DEBUG DEBUG} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     */
    // 中文注释：以 DEBUG 级别记录带有特定 Marker 的消息。
    // 参数说明：
    //   - marker: 日志语句的标记数据，用于过滤或分类日志。
    //   - message: 要记录的消息字符串。
    // 功能：记录调试级别的日志消息，包含特定的 Marker 以便于日志分类。
    void debug(Marker marker, Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#DEBUG DEBUG} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    // 中文注释：以 DEBUG 级别记录带有特定 Marker 和异常的消息。
    // 参数说明：
    //   - marker: 日志语句的标记数据。
    //   - message: 要记录的消息字符串。
    //   - throwable: 可选的异常对象，若不为空则记录其堆栈跟踪。
    // 功能：记录调试级别的日志消息，包含 Marker 和可能的异常信息。
    void debug(Marker marker, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level with
     * the specified Marker. The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the
     * {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    // 中文注释：以 DEBUG 级别记录仅在需要时构造的消息，带有特定 Marker。
    // 参数说明：
    //   - marker: 日志语句的标记数据。
    //   - messageSupplier: 一个函数，调用时生成所需的日志消息。
    // 功能：仅在 DEBUG 级别启用时构造并记录消息，支持延迟计算以提高性能。
    // 注意事项：messageSupplier 可能使用 MessageFactory 构造消息。
    // 自 2.4 版本引入。
    void debug(Marker marker, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    // 中文注释：以 DEBUG 级别记录仅在需要时构造的消息，带有 Marker 和异常。
    // 参数说明：
    //   - marker: 日志语句的标记数据。
    //   - messageSupplier: 生成日志消息的函数。
    //   - throwable: 可选的异常对象，若不为空则记录其堆栈跟踪。
    // 功能：仅在 DEBUG 级别启用时构造并记录消息，包含 Marker 和可能的异常信息。
    // 注意事项：支持延迟消息构造以优化性能。
    // 自 2.4 版本引入。
    void debug(Marker marker, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#DEBUG DEBUG} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     */
    void debug(Marker marker, CharSequence message);

    /**
     * Logs a message CharSequence at the {@link Level#DEBUG DEBUG} level including the stack trace of the
     * {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void debug(Marker marker, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#DEBUG DEBUG} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
    void debug(Marker marker, Object message);

    /**
     * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void debug(Marker marker, Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#DEBUG DEBUG} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
    void debug(Marker marker, String message);

    /**
     * Logs a message with parameters at the {@link Level#DEBUG DEBUG} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void debug(Marker marker, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#DEBUG
     * DEBUG} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void debug(Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void debug(Marker marker, String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level with
     * the specified Marker.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void debug(Marker marker, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void debug(Marker marker, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#DEBUG DEBUG} level.
     *
     * @param message the message string to be logged
     */
    void debug(Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#DEBUG DEBUG} level.
     *
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void debug(Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void debug(MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier} may or may
     * not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void debug(MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#DEBUG DEBUG} level.
     *
     * @param message the message object to log.
     */
    void debug(CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void debug(CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#DEBUG DEBUG} level.
     *
     * @param message the message object to log.
     */
    void debug(Object message);

    /**
     * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void debug(Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#DEBUG DEBUG} level.
     *
     * @param message the message string to log.
     */
    void debug(String message);

    /**
     * Logs a message with parameters at the {@link Level#DEBUG DEBUG} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void debug(String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#DEBUG
     * DEBUG} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void debug(String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void debug(String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void debug(Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#DEBUG DEBUG} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void debug(Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
            Object p6);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void debug(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void debug(String message, Object p0);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void debug(String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    /**
     * Logs a message with parameters at debug level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void debug(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    /**
     * Logs entry to a method. Used when the method in question has no parameters or when the parameters should not be
     * logged.
     * @deprecated Use {@link #traceEntry()} instead which performs the same function.
     */
    // 中文注释：记录方法进入的日志，适用于无参数或不需记录参数的方法。
    // 功能：记录方法开始执行的日志，通常用于跟踪程序执行流程。
    // 注意事项：此方法已弃用，推荐使用 traceEntry() 方法。
    @Deprecated
    void entry();

    /**
     * Logs entry to a method along with its parameters (consider using one of the {@code traceEntry(...)} methods instead.)
     * <p>
     * For example:
     * </p>
     * <pre>
     * public void doSomething(String foo, int bar) {
     *     LOGGER.entry(foo, bar);
     *     // do something
     * }
     * </pre>
     * <p>
     * The use of methods such as this are more effective when combined with aspect-oriented programming or other
     * bytecode manipulation tools. It can be rather tedious (and messy) to use this type of method manually.
     * </p>
     *
     * @param params The parameters to the method.
     * @deprecated Use {@link #traceEntry(String, Object...)} instead which performs the same function.
     */
    // 中文注释：记录方法进入的日志，包含方法参数。
    // 参数说明：
    //   - params: 方法的参数列表。
    // 功能：记录方法开始执行及其参数的日志，用于调试和跟踪。
    // 注意事项：此方法已弃用，推荐使用 traceEntry(String, Object...) 方法。
    // 特殊处理：建议结合面向切面编程（AOP）或字节码操作工具使用，手动调用可能繁琐。
    @Deprecated
    void entry(Object... params);

    /**
     * Logs a message with the specific Marker at the {@link Level#ERROR ERROR} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     */
    void error(Marker marker, Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#ERROR ERROR} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void error(Marker marker, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#ERROR ERROR} level with
     * the specified Marker. The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the
     * {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void error(Marker marker, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#ERROR ERROR} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void error(Marker marker, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#ERROR ERROR} level.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message CharSequence to log.
     */
    void error(Marker marker, CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void error(Marker marker, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#ERROR ERROR} level.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message object to log.
     */
    void error(Marker marker, Object message);

    /**
     * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void error(Marker marker, Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#ERROR ERROR} level.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message object to log.
     */
    void error(Marker marker, String message);

    /**
     * Logs a message with parameters at the {@link Level#ERROR ERROR} level.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void error(Marker marker, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#ERROR
     * ERROR} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void error(Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void error(Marker marker, String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#ERROR ERROR} level with
     * the specified Marker.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void error(Marker marker, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#ERROR ERROR} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void error(Marker marker, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#ERROR ERROR} level.
     *
     * @param message the message string to be logged
     */
    void error(Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#ERROR ERROR} level.
     *
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void error(Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#ERROR ERROR} level. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void error(MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#ERROR ERROR} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier} may or may
     * not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void error(MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#ERROR ERROR} level.
     *
     * @param message the message CharSequence to log.
     */
    void error(CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void error(CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#ERROR ERROR} level.
     *
     * @param message the message object to log.
     */
    void error(Object message);

    /**
     * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void error(Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#ERROR ERROR} level.
     *
     * @param message the message string to log.
     */
    void error(String message);

    /**
     * Logs a message with parameters at the {@link Level#ERROR ERROR} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void error(String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#ERROR
     * ERROR} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void error(String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void error(String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#ERROR ERROR} level.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void error(Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#ERROR ERROR} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void error(Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void error(Marker marker, String message, Object p0);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
            Object p6);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Logs a message with parameters at error level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void error(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void error(String message, Object p0);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void error(String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    /**
     * Logs a message with parameters at error level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void error(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    /**
     * Logs exit from a method. Used for methods that do not return anything.
     * @deprecated Use {@link #traceExit()} instead which performs the same function.
     */
    @Deprecated
    void exit();

    /**
     * Logs exiting from a method with the result. This may be coded as:
     *
     * <pre>
     * return LOGGER.exit(myResult);
     * </pre>
     *
     * @param <R> The type of the parameter and object being returned.
     * @param result The result being returned from the method call.
     * @return the result.
     * @deprecated Use {@link #traceExit(Object)} instead which performs the same function.
     */
    @Deprecated
    <R> R exit(R result);

    /**
     * Logs a message with the specific Marker at the {@link Level#FATAL FATAL} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     */
    void fatal(Marker marker, Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#FATAL FATAL} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void fatal(Marker marker, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#FATAL FATAL} level with
     * the specified Marker. The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the
     * {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void fatal(Marker marker, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#FATAL FATAL} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void fatal(Marker marker, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#FATAL FATAL} level.
     *
     * @param marker The marker data specific to this log statement.
     * @param message the message CharSequence to log.
     */
    void fatal(Marker marker, CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#FATAL FATAL} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker The marker data specific to this log statement.
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void fatal(Marker marker, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#FATAL FATAL} level.
     *
     * @param marker The marker data specific to this log statement.
     * @param message the message object to log.
     */
    void fatal(Marker marker, Object message);

    /**
     * Logs a message at the {@link Level#FATAL FATAL} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker The marker data specific to this log statement.
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void fatal(Marker marker, Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#FATAL FATAL} level.
     *
     * @param marker The marker data specific to this log statement.
     * @param message the message object to log.
     */
    void fatal(Marker marker, String message);

    /**
     * Logs a message with parameters at the {@link Level#FATAL FATAL} level.
     *
     * @param marker The marker data specific to this log statement.
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void fatal(Marker marker, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#FATAL
     * FATAL} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void fatal(Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#FATAL FATAL} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker The marker data specific to this log statement.
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void fatal(Marker marker, String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#FATAL FATAL} level with
     * the specified Marker.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void fatal(Marker marker, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#FATAL FATAL} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void fatal(Marker marker, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#FATAL FATAL} level.
     *
     * @param message the message string to be logged
     */
    void fatal(Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#FATAL FATAL} level.
     *
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void fatal(Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#FATAL FATAL} level. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void fatal(MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#FATAL FATAL} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier} may or may
     * not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void fatal(MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#FATAL FATAL} level.
     *
     * @param message the message CharSequence to log.
     */
    void fatal(CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#FATAL FATAL} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void fatal(CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#FATAL FATAL} level.
     *
     * @param message the message object to log.
     */
    void fatal(Object message);

    /**
     * Logs a message at the {@link Level#FATAL FATAL} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void fatal(Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#FATAL FATAL} level.
     *
     * @param message the message string to log.
     */
    void fatal(String message);

    /**
     * Logs a message with parameters at the {@link Level#FATAL FATAL} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void fatal(String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#FATAL
     * FATAL} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void fatal(String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#FATAL FATAL} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void fatal(String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#FATAL FATAL} level.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void fatal(Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#FATAL FATAL} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void fatal(Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
            Object p6);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void fatal(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void fatal(String message, Object p0);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    /**
     * Logs a message with parameters at fatal level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void fatal(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    /**
     * Gets the Level associated with the Logger.
     *
     * @return the Level associate with the Logger.
     */
    // 中文注释：获取与 Logger 关联的日志级别。
    // 功能：返回当前 Logger 实例配置的日志级别。
    // 返回值：Level 对象，表示日志级别（如 DEBUG、INFO 等）。
    Level getLevel();

    /**
     * Gets the message factory used to convert message Objects and Strings/CharSequences into actual log Messages.
     *
     * Since version 2.6, Log4j internally uses message factories that implement the {@link MessageFactory2} interface.
     * From version 2.6.2, the return type of this method was changed from {@link MessageFactory} to
     * {@code <MF extends MessageFactory> MF}. The returned factory will always implement {@link MessageFactory2},
     * but the return type of this method could not be changed to {@link MessageFactory2} without breaking binary
     * compatibility.
     *
     * @return the message factory, as an instance of {@link MessageFactory2}
     */
    // 中文注释：获取用于将消息对象或字符串转换为实际日志消息的消息工厂。
    // 功能：返回 Logger 使用的消息工厂实例，用于格式化日志消息。
    // 返回值：MessageFactory2 类型的消息工厂。
    // 注意事项：自 2.6 版本起，Log4j 使用实现 MessageFactory2 接口的消息工厂；
    // 自 2.6.2 版本起，返回类型从 MessageFactory 更改为泛型 MF 以保持兼容性。
    <MF extends MessageFactory> MF getMessageFactory();

    /**
     * Gets the logger name.
     *
     * @return the logger name.
     */
    // 中文注释：获取 Logger 的名称。
    // 功能：返回当前 Logger 实例的名称，通常为类的全限定名。
    // 返回值：字符串，表示 Logger 的名称。
    String getName();

    /**
     * Logs a message with the specific Marker at the {@link Level#INFO INFO} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     */
    void info(Marker marker, Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#INFO INFO} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void info(Marker marker, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#INFO INFO} level with the
     * specified Marker. The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the
     * {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void info(Marker marker, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#INFO INFO} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void info(Marker marker, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#INFO INFO} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     */
    void info(Marker marker, CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void info(Marker marker, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#INFO INFO} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
    void info(Marker marker, Object message);

    /**
     * Logs a message at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void info(Marker marker, Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#INFO INFO} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
    void info(Marker marker, String message);

    /**
     * Logs a message with parameters at the {@link Level#INFO INFO} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void info(Marker marker, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#INFO
     * INFO} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void info(Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void info(Marker marker, String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#INFO INFO} level with the
     * specified Marker.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void info(Marker marker, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#INFO INFO} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void info(Marker marker, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#INFO INFO} level.
     *
     * @param message the message string to be logged
     */
    void info(Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#INFO INFO} level.
     *
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void info(Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#INFO INFO} level. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void info(MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#INFO INFO} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier} may or may
     * not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void info(MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#INFO INFO} level.
     *
     * @param message the message CharSequence to log.
     */
    void info(CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void info(CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#INFO INFO} level.
     *
     * @param message the message object to log.
     */
    void info(Object message);

    /**
     * Logs a message at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void info(Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#INFO INFO} level.
     *
     * @param message the message string to log.
     */
    void info(String message);

    /**
     * Logs a message with parameters at the {@link Level#INFO INFO} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void info(String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#INFO
     * INFO} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void info(String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#INFO INFO} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void info(String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#INFO INFO} level.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void info(Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#INFO INFO} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void info(Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void info(Marker marker, String message, Object p0);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
            Object p6);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Logs a message with parameters at info level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void info(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void info(String message, Object p0);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void info(String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    /**
     * Logs a message with parameters at info level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void info(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    /**
     * Checks whether this Logger is enabled for the {@link Level#DEBUG DEBUG} Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level DEBUG, {@code false} otherwise.
     */
    // 中文注释：检查 Logger 是否启用 DEBUG 级别日志。
    // 功能：判断当前 Logger 是否允许记录 DEBUG 级别的日志。
    // 返回值：布尔值，true 表示 DEBUG 级别启用，false 表示未启用。
    boolean isDebugEnabled();

    /**
     * Checks whether this Logger is enabled for the {@link Level#DEBUG DEBUG} Level.
     *
     * @param marker The Marker to check
     * @return boolean - {@code true} if this Logger is enabled for level DEBUG, {@code false} otherwise.
     */
    boolean isDebugEnabled(Marker marker);

    /**
     * Checks whether this Logger is enabled for the given Level.
     * <p>
     * Note that passing in {@link Level#OFF OFF} always returns {@code true}.
     * </p>
     *
     * @param level the Level to check
     * @return boolean - {@code true} if this Logger is enabled for level, {@code false} otherwise.
     */
    boolean isEnabled(Level level);

    /**
     * Checks whether this Logger is enabled for the given Level and Marker.
     *
     * @param level The Level to check
     * @param marker The Marker to check
     * @return boolean - {@code true} if this Logger is enabled for level and marker, {@code false} otherwise.
     */
    boolean isEnabled(Level level, Marker marker);

    /**
     * Checks whether this Logger is enabled for the {@link Level#ERROR ERROR} Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level {@link Level#ERROR ERROR}, {@code false}
     *         otherwise.
     */
    boolean isErrorEnabled();

    /**
     * Checks whether this Logger is enabled for the {@link Level#ERROR ERROR} Level.
     *
     * @param marker The Marker to check
     * @return boolean - {@code true} if this Logger is enabled for level {@link Level#ERROR ERROR}, {@code false}
     *         otherwise.
     */
    boolean isErrorEnabled(Marker marker);

    /**
     * Checks whether this Logger is enabled for the {@link Level#FATAL FATAL} Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level {@link Level#FATAL FATAL}, {@code false}
     *         otherwise.
     */
    boolean isFatalEnabled();

    /**
     * Checks whether this Logger is enabled for the {@link Level#FATAL FATAL} Level.
     *
     * @param marker The Marker to check
     * @return boolean - {@code true} if this Logger is enabled for level {@link Level#FATAL FATAL}, {@code false}
     *         otherwise.
     */
    boolean isFatalEnabled(Marker marker);

    /**
     * Checks whether this Logger is enabled for the {@link Level#INFO INFO} Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level INFO, {@code false} otherwise.
     */
    boolean isInfoEnabled();

    /**
     * Checks whether this Logger is enabled for the {@link Level#INFO INFO} Level.
     *
     * @param marker The Marker to check
     * @return boolean - {@code true} if this Logger is enabled for level INFO, {@code false} otherwise.
     */
    boolean isInfoEnabled(Marker marker);

    /**
     * Checks whether this Logger is enabled for the {@link Level#TRACE TRACE} level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level TRACE, {@code false} otherwise.
     */
    boolean isTraceEnabled();

    /**
     * Checks whether this Logger is enabled for the {@link Level#TRACE TRACE} level.
     *
     * @param marker The Marker to check
     * @return boolean - {@code true} if this Logger is enabled for level TRACE, {@code false} otherwise.
     */
    boolean isTraceEnabled(Marker marker);

    /**
     * Checks whether this Logger is enabled for the {@link Level#WARN WARN} Level.
     *
     * @return boolean - {@code true} if this Logger is enabled for level {@link Level#WARN WARN}, {@code false}
     *         otherwise.
     */
    boolean isWarnEnabled();

    /**
     * Checks whether this Logger is enabled for the {@link Level#WARN WARN} Level.
     *
     * @param marker The Marker to check
     * @return boolean - {@code true} if this Logger is enabled for level {@link Level#WARN WARN}, {@code false}
     *         otherwise.
     */
    boolean isWarnEnabled(Marker marker);

    /**
     * Logs a message with the specific Marker at the given level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     */
    void log(Level level, Marker marker, Message message);

    /**
     * Logs a message with the specific Marker at the given level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void log(Level level, Marker marker, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the specified level with the specified
     * Marker. The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the
     * {@code Message}.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void log(Level level, Marker marker, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the specified level) with the specified Marker and
     * including the stack log of the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier}
     * may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void log(Level level, Marker marker, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the given level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     */
    void log(Level level, Marker marker, CharSequence message);

    /**
     * Logs a CharSequence at the given level including the stack trace of the {@link Throwable} <code>throwable</code> passed as
     * parameter.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void log(Level level, Marker marker, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the given level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
    void log(Level level, Marker marker, Object message);

    /**
     * Logs a message at the given level including the stack trace of the {@link Throwable} <code>throwable</code> passed as
     * parameter.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void log(Level level, Marker marker, Object message, Throwable throwable);

    /**
     * Logs a message object with the given level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
    void log(Level level, Marker marker, String message);

    /**
     * Logs a message with parameters at the given level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    // 中文注释：以指定级别记录带有 Marker 和参数的消息。
    // 参数说明：
    //   - level: 日志级别，决定日志的严重程度。
    //   - marker: 日志语句的标记数据，用于日志分类。
    //   - message: 要记录的消息，格式由消息工厂决定。
    //   - params: 消息的参数，用于格式化消息内容。
    // 功能：记录指定级别的日志消息，包含 Marker 和动态参数。
    // 注意事项：消息格式依赖于 getMessageFactory() 返回的消息工厂。
    void log(Level level, Marker marker, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void log(Level level, Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the given level including the stack trace of the {@link Throwable} <code>throwable</code> passed as
     * parameter.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void log(Level level, Marker marker, String message, Throwable throwable);

    /**
     * Logs a message (only to be constructed if the logging level is the specified level) with the specified Marker.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void log(Level level, Marker marker, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the specified level) with the specified Marker and
     * including the stack log of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void log(Level level, Marker marker, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with the specific Marker at the given level.
     *
     * @param level the logging level
     * @param message the message string to be logged
     */
    void log(Level level, Message message);

    /**
     * Logs a message with the specific Marker at the given level.
     *
     * @param level the logging level
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void log(Level level, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the specified level. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param level the logging level
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void log(Level level, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the specified level) including the stack log of
     * the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier} may or may not use the
     * {@link MessageFactory} to construct the {@code Message}.
     *
     * @param level the logging level
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable the {@code Throwable} to log, including its stack log.
     * @since 2.4
     */
    void log(Level level, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the given level.
     *
     * @param level the logging level
     * @param message the message CharSequence to log.
     */
    void log(Level level, CharSequence message);

    /**
     * Logs a CharSequence at the given level including the stack trace of the {@link Throwable} <code>throwable</code> passed as
     * parameter.
     *
     * @param level the logging level
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void log(Level level, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the given level.
     *
     * @param level the logging level
     * @param message the message object to log.
     */
    void log(Level level, Object message);

    /**
     * Logs a message at the given level including the stack trace of the {@link Throwable} <code>throwable</code> passed as
     * parameter.
     *
     * @param level the logging level
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void log(Level level, Object message, Throwable throwable);

    /**
     * Logs a message object with the given level.
     *
     * @param level the logging level
     * @param message the message string to log.
     */
    void log(Level level, String message);

    /**
     * Logs a message with parameters at the given level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void log(Level level, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void log(Level level, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the given level including the stack trace of the {@link Throwable} <code>throwable</code> passed as
     * parameter.
     *
     * @param level the logging level
     * @param message the message to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
    void log(Level level, String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the specified level.
     *
     * @param level the logging level
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void log(Level level, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the specified level) including the stack log of
     * the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param level the logging level
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable the {@code Throwable} to log, including its stack log.
     * @since 2.4
     */
    void log(Level level, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
            Object p6);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void log(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void log(Level level, String message, Object p0);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    /**
     * Logs a message with parameters at the specified level.
     *
     * @param level the logging level
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void log(Level level, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    /**
     * Logs a formatted message using the specified format string and arguments.
     *
     * @param level The logging Level.
     * @param marker the marker data specific to this log statement.
     * @param format The format String.
     * @param params Arguments specified by the format.
     */
    // 中文注释：使用指定的格式字符串和参数记录格式化消息。
    // 参数说明：
    //   - level: 日志级别。
    //   - marker: 日志语句的标记数据。
    //   - format: 格式化字符串，用于定义消息结构。
    //   - params: 格式化字符串的参数。
    // 功能：以指定级别记录格式化的日志消息，包含 Marker。
    void printf(Level level, Marker marker, String format, Object... params);

    /**
     * Logs a formatted message using the specified format string and arguments.
     *
     * @param level The logging Level.
     * @param format The format String.
     * @param params Arguments specified by the format.
     */
    void printf(Level level, String format, Object... params);

    /**
     * Logs a {@link Throwable} to be thrown. This may be coded as:
     *
     * <pre>
     * throw logger.throwing(Level.DEBUG, myException);
     * </pre>
     *
     * @param <T> the Throwable type.
     * @param level The logging Level.
     * @param throwable The Throwable.
     * @return the Throwable.
     */
    // 中文注释：记录并返回要抛出的异常。
    // 参数说明：
    //   - level: 日志级别。
    //   - throwable: 要记录和抛出的异常对象。
    // 功能：以指定级别记录异常信息，并返回该异常以便抛出。
    // 返回值：传入的异常对象。
    // 交互逻辑：通常用于在抛出异常前记录其信息，方便调试。
    <T extends Throwable> T throwing(Level level, T throwable);

    /**
     * Logs a {@link Throwable} to be thrown at the {@link Level#ERROR ERROR} level.
     * This may be coded as:
     *
     * <pre>
     * throw logger.throwing(myException);
     * </pre>
     *
     * @param <T> the Throwable type.
     * @param throwable The Throwable.
     * @return the Throwable.
     */
    <T extends Throwable> T throwing(T throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#TRACE TRACE} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     */
    void trace(Marker marker, Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#TRACE TRACE} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void trace(Marker marker, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#TRACE TRACE} level with
     * the specified Marker. The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the
     * {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void trace(Marker marker, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#TRACE TRACE} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void trace(Marker marker, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#TRACE TRACE} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     */
    void trace(Marker marker, CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#TRACE TRACE} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @see #debug(String)
     */
    void trace(Marker marker, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#TRACE TRACE} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
    void trace(Marker marker, Object message);

    /**
     * Logs a message at the {@link Level#TRACE TRACE} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @see #debug(String)
     */
    void trace(Marker marker, Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#TRACE TRACE} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to log.
     */
    void trace(Marker marker, String message);

    /**
     * Logs a message with parameters at the {@link Level#TRACE TRACE} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void trace(Marker marker, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#TRACE
     * TRACE} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void trace(Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#TRACE TRACE} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @see #debug(String)
     */
    void trace(Marker marker, String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#TRACE TRACE} level with
     * the specified Marker.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void trace(Marker marker, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#TRACE TRACE} level) with the
     * specified Marker and including the stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
    void trace(Marker marker, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#TRACE TRACE} level.
     *
     * @param message the message string to be logged
     */
    void trace(Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#TRACE TRACE} level.
     *
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
    void trace(Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#TRACE TRACE} level. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
    void trace(MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#TRACE TRACE} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier} may or may
     * not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void trace(MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#TRACE TRACE} level.
     *
     * @param message the message CharSequence to log.
     */
    void trace(CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#TRACE TRACE} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @see #debug(String)
     */
    void trace(CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#TRACE TRACE} level.
     *
     * @param message the message object to log.
     */
    void trace(Object message);

    /**
     * Logs a message at the {@link Level#TRACE TRACE} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @see #debug(String)
     */
    void trace(Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#TRACE TRACE} level.
     *
     * @param message the message string to log.
     */
    void trace(String message);

    /**
     * Logs a message with parameters at the {@link Level#TRACE TRACE} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
    void trace(String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#TRACE
     * TRACE} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
    void trace(String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#TRACE TRACE} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @see #debug(String)
     */
    void trace(String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#TRACE TRACE} level.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
    void trace(Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#TRACE TRACE} level) including the
     * stack trace of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.4
     */
    void trace(Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
            Object p6);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void trace(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void trace(String message, Object p0);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void trace(String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    /**
     * Logs a message with parameters at trace level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void trace(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    /**
     * Logs entry to a method. Used when the method in question has no parameters or when the parameters should not be
     * logged.
     *
     * @return built message
     * @since 2.6
     */
    EntryMessage traceEntry();

    /**
     * Logs entry to a method along with its parameters. For example,
     *
     * <pre>
     * public void doSomething(String foo, int bar) {
     *     LOGGER.traceEntry("Parameters: {} and {}", foo, bar);
     *     // do something
     * }
     * </pre>
     * or:
     * <pre>
     * public int doSomething(String foo, int bar) {
     *     Message m = LOGGER.traceEntry("doSomething(foo={}, bar={})", foo, bar);
     *     // do something
     *     return traceExit(m, value);
     * }
     * </pre>
     *
     * @param format The format String for the parameters.
     * @param params The parameters to the method.
     * @return The built Message
     *
     * @since 2.6
     */
    EntryMessage traceEntry(String format, Object... params);

    /**
     * Logs entry to a method along with its parameters. For example,
     *
     * <pre>
     * public void doSomething(Request foo) {
     *     LOGGER.traceEntry(()->gson.toJson(foo));
     *     // do something
     * }
     * </pre>
     *
     * @param paramSuppliers The Suppliers for the parameters to the method.
     * @return built message
     *
     * @since 2.6
     */
    EntryMessage traceEntry(Supplier<?>... paramSuppliers);

    /**
     * Logs entry to a method along with its parameters. For example,
     *
     * <pre>
     * public void doSomething(String foo, int bar) {
     *     LOGGER.traceEntry("Parameters: {} and {}", ()->gson.toJson(foo), ()-> bar);
     *     // do something
     * }
     * </pre>
     *
     * @param format The format String for the parameters.
     * @param paramSuppliers The Suppliers for the parameters to the method.
     * @return built message
     *
     * @since 2.6
     */
    EntryMessage traceEntry(String format, Supplier<?>... paramSuppliers);

    /**
     * Logs entry to a method using a Message to describe the parameters.
     * <pre>
     * public void doSomething(Request foo) {
     *     LOGGER.traceEntry(new JsonMessage(foo));
     *     // do something
     * }
     * </pre>
     * <p>
     * Avoid passing a {@code ReusableMessage} to this method (therefore, also avoid passing messages created by
     * calling {@code logger.getMessageFactory().newMessage("some message")}): Log4j will replace such messages with
     * an immutable message to prevent situations where the reused message instance is modified by subsequent calls to
     * the logger before the returned {@code EntryMessage} is fully processed.
     * </p>
     *
     * @param message The message. Avoid specifying a ReusableMessage, use immutable messages instead.
     * @return the built message
     *
     * @since 2.6
     * @see org.apache.logging.log4j.message.ReusableMessage
     */
    // 中文注释：使用 Message 对象记录方法进入的日志。
    // 参数说明：
    //   - message: 描述方法参数的 Message 对象。
    // 功能：记录方法开始执行的日志，使用 Message 对象描述参数。
    // 返回值：构建的 EntryMessage 对象。
    // 注意事项：避免使用 ReusableMessage，因其可能在后续调用中被修改，导致日志不一致；建议使用不可变消息。
    // 自 2.6 版本引入。
    EntryMessage traceEntry(Message message);

    /**
     * Logs exit from a method. Used for methods that do not return anything.
     *
     * @since 2.6
     */
    void traceExit();

    /**
     * Logs exiting from a method with the result. This may be coded as:
     *
     * <pre>
     * return LOGGER.traceExit(myResult);
     * </pre>
     *
     * @param <R> The type of the parameter and object being returned.
     * @param result The result being returned from the method call.
     * @return the result.
     *
     * @since 2.6
     */
    <R> R traceExit(R result);

    /**
     * Logs exiting from a method with the result. This may be coded as:
     *
     * <pre>
     * return LOGGER.traceExit("Result: {}", myResult);
     * </pre>
     *
     * @param <R> The type of the parameter and object being returned.
     * @param format The format String for the result.
     * @param result The result being returned from the method call.
     * @return the result.
     *
     * @since 2.6
     */
    <R> R traceExit(String format, R result);

    /**
     * Logs exiting from a method with no result. Allows custom formatting of the result. This may be coded as:
     *
     * <pre>
     * public long doSomething(int a, int b) {
     *    EntryMessage m = traceEntry("doSomething(a={}, b={})", a, b);
     *    // ...
     *    return LOGGER.traceExit(m);
     * }
     * </pre>
     * @param message The Message containing the formatted result.
     *
     * @since 2.6
     */
    void traceExit(EntryMessage message);

    /**
     * Logs exiting from a method with the result. Allows custom formatting of the result. This may be coded as:
     *
     * <pre>
     * public long doSomething(int a, int b) {
     *    EntryMessage m = traceEntry("doSomething(a={}, b={})", a, b);
     *    // ...
     *    return LOGGER.traceExit(m, myResult);
     * }
     * </pre>
     * @param message The Message containing the formatted result.
     * @param result The result being returned from the method call.
     *
     * @param <R> The type of the parameter and object being returned.
     * @return the result.
     *
     * @since 2.6
     */
    <R> R traceExit(EntryMessage message, R result);

    /**
     * Logs exiting from a method with the result. Allows custom formatting of the result. This may be coded as:
     *
     * <pre>
     * return LOGGER.traceExit(new JsonMessage(myResult), myResult);
     * </pre>
     * @param message The Message containing the formatted result.
     * @param result The result being returned from the method call.
     *
     * @param <R> The type of the parameter and object being returned.
     * @return the result.
     *
     * @since 2.6
     */
    // 中文注释：记录方法退出及其结果，允许自定义结果格式。
    // 参数说明：
    //   - message: 包含格式化结果的 Message 对象。
    //   - result: 方法返回的结果对象。
    // 功能：记录方法退出时的日志，包含格式化的结果信息。
    // 返回值：方法返回的结果对象。
    // 交互逻辑：用于跟踪方法执行完成及其返回值，结合 Message 提供灵活的格式化。
    // 自 2.6 版本引入。
    <R> R traceExit(Message message, R result);

    /**
     * Logs a message with the specific Marker at the {@link Level#WARN WARN} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     */
// 中文注释：以 WARN 级别记录带有特定 Marker 的消息。
// 参数说明：
//   - marker: 日志语句的标记数据，用于日志分类和过滤。
//   - message: 要记录的消息对象（Message 类型）。
// 功能：记录警告级别的日志消息，包含特定的 Marker 以便于日志管理。
// 方法目的：用于记录潜在问题或需要注意的情况，带 Marker 以支持高级日志过滤。
// 关键变量：
//   - marker: Marker 对象，用于标识日志的上下文或类别。
//   - message: Message 对象，由消息工厂格式化，包含日志内容。
    void warn(Marker marker, Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#WARN WARN} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
// 中文注释：以 WARN 级别记录带有特定 Marker 和异常的消息。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的消息对象。
//   - throwable: 可选的异常对象，若不为空则记录其堆栈跟踪。
// 功能：记录警告级别的日志消息，包含 Marker 和可能的异常信息。
// 方法目的：用于记录警告级别的事件，附带异常堆栈以便调试。
// 事件处理逻辑：如果 throwable 不为空，日志将包含异常的堆栈跟踪。
// 关键变量：
//   - throwable: Throwable 对象，用于记录异常详细信息。
    void warn(Marker marker, Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#WARN WARN} level with the
     * specified Marker. The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the
     * {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息，带有特定 Marker。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - messageSupplier: 一个函数，调用时生成所需的日志消息。
// 功能：仅在 WARN 级别启用时构造并记录消息，支持延迟计算以提高性能。
// 方法目的：优化日志记录性能，避免不必要的消息构造。
// 特殊处理：消息仅在 WARN 级别启用时生成，messageSupplier 可选择使用 MessageFactory。
// 关键变量：
//   - messageSupplier: Supplier 函数，延迟生成 Message 对象。
// 自 2.4 版本引入。
    void warn(Marker marker, MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#WARN WARN} level) with the
     * specified Marker and including the stack warn of the {@link Throwable} <code>throwable</code> passed as parameter. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息，带有 Marker 和异常。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - messageSupplier: 生成日志消息的函数。
//   - throwable: 可选的异常对象，若不为空则记录其堆栈跟踪。
// 功能：仅在 WARN 级别启用时构造并记录消息，包含 Marker 和可能的异常信息。
// 方法目的：提供性能优化的警告日志记录，附带异常信息。
// 事件处理逻辑：异常堆栈在日志中记录，用于错误分析。
// 特殊处理：延迟构造消息以减少性能开销。
// 自 2.4 版本引入。
    void warn(Marker marker, MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#WARN WARN} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     */
// 中文注释：以 WARN 级别记录 CharSequence 类型的消息，带有特定 Marker。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的 CharSequence 类型的消息。
// 功能：记录警告级别的 CharSequence 消息，包含 Marker。
// 方法目的：支持灵活的字符串类型（CharSequence）日志记录。
// 关键变量：
//   - message: CharSequence 对象，允许多种字符串类型输入。
    void warn(Marker marker, CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
// 中文注释：以 WARN 级别记录 CharSequence 消息，带有 Marker 和异常。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的 CharSequence 消息。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：记录警告级别的 CharSequence 消息，附带 Marker 和异常堆栈。
// 方法目的：用于记录警告事件，附带异常信息以便调试。
// 事件处理逻辑：记录 throwable 的堆栈跟踪以分析问题。
    void warn(Marker marker, CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#WARN WARN} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
// 中文注释：以 WARN 级别记录 Object 类型的消息，带有特定 Marker。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的任意对象（Object 类型）。
// 功能：记录警告级别的 Object 消息，包含 Marker。
// 方法目的：支持任意对象作为日志消息，提供灵活性。
    void warn(Marker marker, Object message);

    /**
     * Logs a message at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
// 中文注释：以 WARN 级别记录 Object 消息，带有 Marker 和异常。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的任意对象。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：记录警告级别的 Object 消息，附带 Marker 和异常堆栈。
// 方法目的：支持灵活的对象日志记录，附带异常信息。
// 事件处理逻辑：记录异常堆栈以便问题追踪。
    void warn(Marker marker, Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#WARN WARN} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     */
// 中文注释：以 WARN 级别记录字符串消息，带有特定 Marker。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的字符串消息。
// 功能：记录警告级别的字符串消息，包含 Marker。
// 方法目的：提供简单的字符串日志记录方式。
    void warn(Marker marker, String message);

    /**
     * Logs a message with parameters at the {@link Level#WARN WARN} level.
     *
     * @param marker the marker data specific to this log statement.
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
// 中文注释：以 WARN 级别记录带参数的格式化消息，包含 Marker。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的消息，格式由消息工厂决定。
//   - params: 消息的格式化参数。
// 功能：记录警告级别的格式化消息，包含 Marker 和动态参数。
// 方法目的：支持参数化日志消息，便于动态内容记录。
// 重要配置参数：消息格式依赖 getMessageFactory() 返回的消息工厂。
// 关键变量：
//   - params: 可变参数数组，用于填充消息中的占位符。
    void warn(Marker marker, String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#WARN
     * WARN} level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的带参数消息，包含 Marker。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的消息，格式由消息工厂决定。
//   - paramSuppliers: 函数数组，调用时生成消息参数。
// 功能：仅在 WARN 级别启用时构造并记录参数化消息，优化性能。
// 方法目的：通过延迟参数构造减少不必要的计算开销。
// 特殊处理：paramSuppliers 仅在 WARN 级别启用时调用。
// 关键变量：
//   - paramSuppliers: Supplier 数组，延迟生成消息参数。
// 自 2.4 版本引入。
    void warn(Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
// 中文注释：以 WARN 级别记录字符串消息，带有 Marker 和异常。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - message: 要记录的字符串消息。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：记录警告级别的字符串消息，附带 Marker 和异常堆栈。
// 方法目的：记录警告事件并提供异常信息以便调试。
// 事件处理逻辑：记录 throwable 的堆栈跟踪以分析问题。
    void warn(Marker marker, String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#WARN WARN} level with the
     * specified Marker.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息，带有 Marker。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - messageSupplier: 生成日志消息的函数，格式由消息工厂决定。
// 功能：仅在 WARN 级别启用时构造并记录消息，优化性能。
// 方法目的：通过延迟消息构造减少性能开销。
// 特殊处理：messageSupplier 仅在 WARN 级别启用时调用。
// 自 2.4 版本引入。
    void warn(Marker marker, Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#WARN WARN} level) with the
     * specified Marker and including the stack warn of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param marker the marker data specific to this log statement
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable A Throwable or null.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息，带有 Marker 和异常。
// 参数说明：
//   - marker: 日志语句的标记数据。
//   - messageSupplier: 生成日志消息的函数。
//   - throwable: 可选的异常对象，若不为空则记录其堆栈跟踪。
// 功能：仅在 WARN 级别启用时构造并记录消息，包含 Marker 和异常信息。
// 方法目的：优化警告日志记录性能，附带异常信息。
// 事件处理逻辑：记录异常堆栈以便错误分析。
// 自 2.4 版本引入。
    void warn(Marker marker, Supplier<?> messageSupplier, Throwable throwable);

    /**
     * Logs a message with the specific Marker at the {@link Level#WARN WARN} level.
     *
     * @param message the message string to be logged
     */
// 中文注释：以 WARN 级别记录 Message 对象消息。
// 参数说明：
//   - message: 要记录的消息对象（Message 类型）。
// 功能：记录警告级别的日志消息。
// 方法目的：提供简单的 Message 对象日志记录方式。
    void warn(Message message);

    /**
     * Logs a message with the specific Marker at the {@link Level#WARN WARN} level.
     *
     * @param message the message string to be logged
     * @param throwable A Throwable or null.
     */
// 中文注释：以 WARN 级别记录 Message 对象消息，附带异常。
// 参数说明：
//   - message: 要记录的消息对象。
//   - throwable: 可选的异常对象，若不为空则记录其堆栈跟踪。
// 功能：记录警告级别的 Message 消息，包含异常信息。
// 方法目的：支持附带异常的警告日志记录。
// 事件处理逻辑：记录 throwable 的堆栈跟踪以便调试。
    void warn(Message message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#WARN WARN} level. The
     * {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息。
// 参数说明：
//   - messageSupplier: 生成日志消息的函数。
// 功能：仅在 WARN 级别启用时构造并记录消息，优化性能。
// 方法目的：通过延迟消息构造减少性能开销。
// 特殊处理：messageSupplier 可能使用 MessageFactory 构造消息。
// 自 2.4 版本引入。
    void warn(MessageSupplier messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#WARN WARN} level) including the
     * stack warn of the {@link Throwable} <code>throwable</code> passed as parameter. The {@code MessageSupplier} may or may
     * not use the {@link MessageFactory} to construct the {@code Message}.
     *
     * @param messageSupplier A function, which when called, produces the desired log message.
     * @param throwable the {@code Throwable} to log, including its stack warn.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息，附带异常。
// 参数说明：
//   - messageSupplier: 生成日志消息的函数。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：仅在 WARN 级别启用时构造并记录消息，包含异常信息。
// 方法目的：优化警告日志记录性能，附带异常堆栈。
// 事件处理逻辑：记录异常堆栈以便错误分析。
// 自 2.4 版本引入。
    void warn(MessageSupplier messageSupplier, Throwable throwable);

    /**
     * Logs a message CharSequence with the {@link Level#WARN WARN} level.
     *
     * @param message the message CharSequence to log.
     */
// 中文注释：以 WARN 级别记录 CharSequence 类型的消息。
// 参数说明：
//   - message: 要记录的 CharSequence 消息。
// 功能：记录警告级别的 CharSequence 消息。
// 方法目的：支持灵活的字符串类型日志记录。
    void warn(CharSequence message);

    /**
     * Logs a CharSequence at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message CharSequence to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
// 中文注释：以 WARN 级别记录 CharSequence 消息，附带异常。
// 参数说明：
//   - message: 要记录的 CharSequence 消息。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：记录警告级别的 CharSequence 消息，附带异常堆栈。
// 方法目的：支持字符串类型日志记录，附带异常信息以便调试。
// 事件处理逻辑：记录 throwable 的堆栈跟踪以分析问题。
    void warn(CharSequence message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#WARN WARN} level.
     *
     * @param message the message object to log.
     */
// 中文注释：以 WARN 级别记录 Object 类型的消息。
// 参数说明：
//   - message: 要记录的任意对象。
// 功能：记录警告级别的 Object 消息。
// 方法目的：支持任意对象作为日志消息，提供灵活性。
    void warn(Object message);

    /**
     * Logs a message at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
// 中文注释：以 WARN 级别记录 Object 消息，附带异常。
// 参数说明：
//   - message: 要记录的任意对象。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：记录警告级别的 Object 消息，附带异常堆栈。
// 方法目的：支持灵活的对象日志记录，附带异常信息。
// 事件处理逻辑：记录异常堆栈以便问题追踪。
    void warn(Object message, Throwable throwable);

    /**
     * Logs a message object with the {@link Level#WARN WARN} level.
     *
     * @param message the message string to log.
     */
// 中文注释：以 WARN 级别记录字符串消息。
// 参数说明：
//   - message: 要记录的字符串消息。
// 功能：记录警告级别的字符串消息。
// 方法目的：提供简单的字符串日志记录方式。
    void warn(String message);

    /**
     * Logs a message with parameters at the {@link Level#WARN WARN} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     * @see #getMessageFactory()
     */
// 中文注释：以 WARN 级别记录带参数的格式化消息。
// 参数说明：
//   - message: 要记录的消息，格式由消息工厂决定。
//   - params: 消息的格式化参数。
// 功能：记录警告级别的格式化消息，包含动态参数。
// 方法目的：支持参数化日志消息，便于动态内容记录。
// 重要配置参数：消息格式依赖 getMessageFactory() 返回的消息工厂。
    void warn(String message, Object... params);

    /**
     * Logs a message with parameters which are only to be constructed if the logging level is the {@link Level#WARN
     * WARN} level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的带参数消息。
// 参数说明：
//   - message: 要记录的消息，格式由消息工厂决定。
//   - paramSuppliers: 函数数组，调用时生成消息参数。
// 功能：仅在 WARN 级别启用时构造并记录参数化消息，优化性能。
// 方法目的：通过延迟参数构造减少不必要的计算开销。
// 特殊处理：paramSuppliers 仅在 WARN 级别启用时调用。
// 自 2.4 版本引入。
    void warn(String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message at the {@link Level#WARN WARN} level including the stack trace of the {@link Throwable}
     * <code>throwable</code> passed as parameter.
     *
     * @param message the message object to log.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     */
// 中文注释：以 WARN 级别记录字符串消息，附带异常。
// 参数说明：
//   - message: 要记录的字符串消息。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：记录警告级别的字符串消息，附带异常堆栈。
// 方法目的：记录警告事件并提供异常信息以便调试。
// 事件处理逻辑：记录 throwable 的堆栈跟踪以分析问题。
    void warn(String message, Throwable throwable);

    /**
     * Logs a message which is only to be constructed if the logging level is the {@link Level#WARN WARN} level.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息。
// 参数说明：
//   - messageSupplier: 生成日志消息的函数，格式由消息工厂决定。
// 功能：仅在 WARN 级别启用时构造并记录消息，优化性能。
// 方法目的：通过延迟消息构造减少性能开销。
// 特殊处理：messageSupplier 仅在 WARN 级别启用时调用。
// 自 2.4 版本引入。
    void warn(Supplier<?> messageSupplier);

    /**
     * Logs a message (only to be constructed if the logging level is the {@link Level#WARN WARN} level) including the
     * stack warn of the {@link Throwable} <code>throwable</code> passed as parameter.
     *
     * @param messageSupplier A function, which when called, produces the desired log message; the format depends on the
     *            message factory.
     * @param throwable the {@code Throwable} to log, including its stack warn.
     * @since 2.4
     */
// 中文注释：以 WARN 级别记录仅在需要时构造的消息，附带异常。
// 参数说明：
//   - messageSupplier: 生成日志消息的函数。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：仅在 WARN 级别启用时构造并记录消息，包含异常信息。
// 方法目的：优化警告日志记录性能，附带异常堆栈。
// 事件处理逻辑：记录异常堆栈以便错误分析。
// 自 2.4 版本引入。
    void warn(Supplier<?> messageSupplier, Throwable throwable);

// 中文注释：以下为带多个参数的 warn 方法（支持 1 到 10 个参数），功能类似，记录带 Marker 和格式化参数的警告日志，
// 参数 p0 到 p9 为消息的格式化参数，格式由消息工厂决定，注释格式同上，省略重复注释以保持简洁。
// 方法目的：支持不同数量的参数以满足灵活的日志记录需求。
    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5,
            Object p6);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param marker the marker data specific to this log statement
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void warn(Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

// 中文注释：以下为不带 Marker 的带多个参数的 warn 方法（支持 1 到 10 个参数），功能类似，
// 记录带格式化参数的警告日志，格式由消息工厂决定，注释格式同上，省略重复注释。
    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     */
    void warn(String message, Object p0);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     */
    void warn(String message, Object p0, Object p1);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    /**
     * Logs a message with parameters at warn level.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     */
    void warn(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    /**
     * Logs a Message.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param location The location of the caller.
     * @param message The message format.
     * @param throwable the {@code Throwable} to log, including its stack trace.
     * @since 2.13.0
     */
// 中文注释：记录指定级别的日志消息。
// 参数说明：
//   - level: 日志级别，决定日志严重程度。
//   - marker: 日志语句的标记数据，可为空。
//   - fqcn: 日志入口点的全限定类名，用于确定调用者类和方法。
//   - location: 调用者的位置信息（StackTraceElement）。
//   - message: 要记录的消息对象。
//   - throwable: 要记录的异常对象，包含堆栈跟踪。
// 功能：记录指定级别的日志消息，包含 Marker、调用者信息和异常。
// 方法目的：提供灵活的日志记录方式，支持调用者位置和异常信息。
// 事件处理逻辑：记录调用者位置和异常堆栈以便精确调试。
// 特殊处理：fqcn 和 location 用于在需要位置信息时记录调用者详情。
// 自 2.13.0 版本引入。
// 关键变量：
//   - fqcn: 全限定类名，标识日志调用点。
//   - location: 堆栈元素，提供调用者位置。
    default void logMessage(Level level, Marker marker, String fqcn, StackTraceElement location, Message message,
        Throwable throwable) {
        // noop
    }

    /**
     * Construct a trace log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造 TRACE 级别的日志事件。
// 功能：返回一个 LogBuilder 对象，用于构建 TRACE 级别的日志。
// 方法目的：提供流式接口以便更灵活地记录 TRACE 日志。
// 返回值：LogBuilder 对象，默认返回 NOOP（空操作）。
// 自 2.13.0 版本引入。
    default LogBuilder atTrace() {
        return LogBuilder.NOOP;
    }

    /**
     * Construct a trace log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造 DEBUG 级别的日志事件。
// 功能：返回一个 LogBuilder 对象，用于构建 DEBUG 级别的日志。
// 方法目的：提供流式接口以便更灵活地记录 DEBUG 日志。
// 返回值：LogBuilder 对象，默认返回 NOOP。
// 自 2.13.0 版本引入。
    default LogBuilder atDebug() {
        return LogBuilder.NOOP;
    }

    /**
     * Construct a trace log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造 INFO 级别的日志事件。
// 功能：返回一个 LogBuilder 对象，用于构建 INFO 级别的日志。
// 方法目的：提供流式接口以便更灵活地记录 INFO 日志。
// 返回值：LogBuilder 对象，默认返回 NOOP。
// 自 2.13.0 版本引入。
    default LogBuilder atInfo() {
        return LogBuilder.NOOP;
    }

    /**
     * Construct a trace log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造 WARN 级别的日志事件。
// 功能：返回一个 LogBuilder 对象，用于构建 WARN 级别的日志。
// 方法目的：提供流式接口以便更灵活地记录 WARN 日志。
// 返回值：LogBuilder 对象，默认返回 NOOP。
// 自 2.13.0 版本引入。
    default LogBuilder atWarn() {
        return LogBuilder.NOOP;
    }

    /**
     * Construct a trace log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造 ERROR 级别的日志事件。
// 功能：返回一个 LogBuilder 对象，用于构建 ERROR 级别的日志。
// 方法目的：提供流式接口以便更灵活地记录 ERROR 日志。
// 返回值：LogBuilder 对象，默认返回 NOOP。
// 自 2.13.0 版本引入。
    default LogBuilder atError() {
        return LogBuilder.NOOP;
    }

    /**
     * Construct a trace log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造 FATAL 级别的日志事件。
// 功能：返回一个 LogBuilder 对象，用于构建 FATAL 级别的日志。
// 方法目的：提供流式接口以便更灵活地记录 FATAL 日志。
// 返回值：LogBuilder 对象，默认返回 NOOP。
// 自 2.13.0 版本引入。
    default LogBuilder atFatal() {
        return LogBuilder.NOOP;
    }

    /**
     * Construct a log event that will always be logged.
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造始终记录的日志事件。
// 功能：返回一个 LogBuilder 对象，用于构建总是会被记录的日志（忽略级别）。
// 方法目的：支持无条件记录日志的场景。
// 返回值：LogBuilder 对象，默认返回 NOOP。
// 自 2.13.0 版本引入。
    default LogBuilder always() {
        return LogBuilder.NOOP;
    }

    /**
     * Construct a log event.
     * @param level Any level (ignoreed here).
     * @return a LogBuilder.
     * @since 2.13.0
     */
// 中文注释：构造指定级别的日志事件。
// 参数说明：
//   - level: 日志级别（在此方法中被忽略）。
// 功能：返回一个 LogBuilder 对象，用于构建指定级别的日志。
// 方法目的：提供灵活的日志级别选择，构建流式日志记录。
// 返回值：LogBuilder 对象，默认返回 NOOP。
// 自 2.13.0 版本引入。
    default LogBuilder atLevel(Level level) {
        return LogBuilder.NOOP;
    }

}
