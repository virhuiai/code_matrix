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
// 版权声明，遵循Apache许可证2.0版本。此文件是Apache软件基金会授权的，除非符合许可证，否则不得使用。

package org.apache.logging.log4j.core;

import java.io.Serializable;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

/**
 * Provides contextual information about a logged message. A LogEvent must be {@link java.io.Serializable} so that it
 * may be transmitted over a network connection, output in a
 * {@link org.apache.logging.log4j.core.layout.SerializedLayout}, and many other uses. Besides containing a
 * {@link org.apache.logging.log4j.message.Message}, a LogEvent has a corresponding
 * {@link org.apache.logging.log4j.Level} that the message was logged at. If a
 * {@link org.apache.logging.log4j.Marker} was used, then it is included here. The contents of the
 * {@link org.apache.logging.log4j.ThreadContext} at the time of the log call are provided via
 * {@link #getContextMap()} and {@link #getContextStack()}. If a {@link java.lang.Throwable} was included in the log
 * call, then it is provided via {@link #getThrown()}. When this class is serialized, the attached Throwable will
 * be wrapped into a {@link org.apache.logging.log4j.core.impl.ThrowableProxy} so that it may be safely serialized
 * and deserialized properly without causing problems if the exception class is not available on the other end.
 * <p>
 * Since version 2.7, {@link #getContextMap()} is deprecated in favor of {@link #getContextData()}, which
 * can carry both {@code ThreadContext} data as well as other context data supplied by the
 * {@linkplain org.apache.logging.log4j.core.impl.ContextDataInjectorFactory configured}
 * {@link ContextDataInjector}.
 * </p>
 */
// LogEvent 接口：提供关于日志消息的上下文信息。
// 它必须是可序列化的（Serializable），以便可以通过网络传输、输出到序列化布局（SerializedLayout）等多种用途。
// 除了包含一个消息（Message），LogEvent 还包含消息被记录时的日志级别（Level）。
// 如果使用了标记（Marker），也会包含在此。
// 调用日志时线程上下文（ThreadContext）的内容通过 getContextMap() 和 getContextStack() 提供。
// 如果日志调用中包含了异常（Throwable），则通过 getThrown() 提供。
// 当此类被序列化时，附加的 Throwable 将被包装成 ThrowableProxy，以便在序列化和反序列化时安全地处理，
// 避免在另一端缺少异常类时引发问题。
// 注意：自2.7版本起，getContextMap() 已被弃用，推荐使用 getContextData()，
// 它不仅可以携带 ThreadContext 数据，还可以携带由配置的 ContextDataInjector 提供的其他上下文数据。
public interface LogEvent extends Serializable {

    /**
     * Returns an immutable version of this log event, which MAY BE a copy of this event.
     *
     * @return an immutable version of this log event
     */
    // toImmutable 方法：返回此日志事件的不可变版本，该版本可能是一个副本。
    // 返回值：LogEvent - 此日志事件的不可变版本。
    LogEvent toImmutable();

    /**
     * Gets the context map (also know as Mapped Diagnostic Context or MDC).
     *
     * @return The context map, never {@code null}.
     * @deprecated use {@link #getContextData()} instead
     */
    // getContextMap 方法：获取上下文映射（也称为映射诊断上下文或MDC）。
    // 返回值：Map<String, String> - 上下文映射，永不为 null。
    // 注意：此方法已弃用，请使用 getContextData() 代替。
    @Deprecated
    Map<String, String> getContextMap();

    /**
     * Returns the {@code ReadOnlyStringMap} object holding context data key-value pairs.
     * <p>
     * Context data (also known as Mapped Diagnostic Context or MDC) is data that is set by the application to be
     * included in all subsequent log events. The default source for context data is the {@link ThreadContext} (and
     * <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html#PropertySubstitution">properties</a>
     * configured on the Logger that logged the event), but users can configure a custom {@link ContextDataInjector}
     * to inject key-value pairs from any arbitrary source.
     *
     * @return the {@code ReadOnlyStringMap} object holding context data key-value pairs
     * @see ContextDataInjector
     * @see ThreadContext
     * @since 2.7
     */
    // getContextData 方法：返回持有上下文数据键值对的 ReadOnlyStringMap 对象。
    // 上下文数据（也称为映射诊断上下文或MDC）是应用程序设置的数据，用于包含在所有后续日志事件中。
    // 上下文数据的默认来源是 ThreadContext（以及在记录事件的 Logger 上配置的属性），
    // 但用户可以配置自定义的 ContextDataInjector 从任何任意来源注入键值对。
    // 返回值：ReadOnlyStringMap - 持有上下文数据键值对的 ReadOnlyStringMap 对象。
    // 参见：ContextDataInjector, ThreadContext。
    // 自版本：2.7
    ReadOnlyStringMap getContextData();

    /**
     * Gets the context stack (also known as Nested Diagnostic Context or NDC).
     *
     * @return The context stack, never {@code null}.
     */
    // getContextStack 方法：获取上下文堆栈（也称为嵌套诊断上下文或NDC）。
    // 返回值：ThreadContext.ContextStack - 上下文堆栈，永不为 null。
    ThreadContext.ContextStack getContextStack();

    /**
     * Returns the fully qualified class name of the caller of the logging API.
     *
     * @return The fully qualified class name of the caller.
     */
    // getLoggerFqcn 方法：返回调用日志API的调用者的完全限定类名。
    // 返回值：String - 调用者的完全限定类名。
    String getLoggerFqcn();

    /**
     * Gets the level.
     *
     * @return level.
     */
    // getLevel 方法：获取日志级别。
    // 返回值：Level - 日志级别。
    Level getLevel();

    /**
     * Gets the logger name.
     *
     * @return logger name, may be {@code null}.
     */
    // getLoggerName 方法：获取日志器名称。
    // 返回值：String - 日志器名称，可能为 null。
    String getLoggerName();

    /**
     * Gets the Marker associated with the event.
     *
     * @return Marker or {@code null} if no Marker was defined on this LogEvent
     */
    // getMarker 方法：获取与事件关联的标记（Marker）。
    // 返回值：Marker - 标记对象，如果此 LogEvent 上未定义标记，则为 null。
    Marker getMarker();

    /**
     * Gets the message associated with the event.
     *
     * @return message.
     */
    // getMessage 方法：获取与事件关联的消息。
    // 返回值：Message - 消息对象。
    Message getMessage();

    /**
     * Gets event time in milliseconds since midnight, January 1, 1970 UTC.
     * Use {@link #getInstant()} to get higher precision timestamp information if available on this platform.
     *
     * @return the milliseconds component of this log event's {@linkplain #getInstant() timestamp}
     * @see java.lang.System#currentTimeMillis()
     */
    // getTimeMillis 方法：获取事件时间，自1970年1月1日午夜UTC以来的毫秒数。
    // 如果此平台提供更高精度的时间戳信息，请使用 getInstant() 方法。
    // 返回值：long - 此日志事件时间戳的毫秒部分。
    // 参见：java.lang.System#currentTimeMillis()
    long getTimeMillis();

    /**
     * Returns the Instant when the message was logged.
     * <p>
     * <b>Caution</b>: if this {@code LogEvent} implementation is mutable and reused for multiple consecutive log messages,
     * then the {@code Instant} object returned by this method is also mutable and reused.
     * Client code should not keep a reference to the returned object but make a copy instead.
     * </p>
     *
     * @return the {@code Instant} holding Instant details for this log event
     * @since 2.11
     */
    // getInstant 方法：返回消息被记录时的 Instant 对象。
    // 注意：如果此 LogEvent 实现是可变的，并被重用于多个连续的日志消息，
    // 则此方法返回的 Instant 对象也是可变的并被重用。
    // 客户端代码不应保留对返回对象的引用，而应创建副本。
    // 返回值：Instant - 包含此日志事件 Instant 详细信息的 Instant 对象。
    // 自版本：2.11
    Instant getInstant();

    /**
     * Gets the source of logging request.
     *
     * @return source of logging request, may be null.
     */
    // getSource 方法：获取日志请求的来源。
    // 返回值：StackTraceElement - 日志请求的来源，可能为 null。
    StackTraceElement getSource();

    /**
     * Gets the thread name.
     *
     * @return thread name, may be null.
     * TODO guess this could go into a thread context object too. (RG) Why?
     */
    // getThreadName 方法：获取线程名称。
    // 返回值：String - 线程名称，可能为 null。
    // TODO：猜测这也可以放入线程上下文对象中。(RG) 为什么？
    String getThreadName();

    /**
     * Gets the thread ID.
     *
     * @return thread ID.
     * @since 2.6
     */
    // getThreadId 方法：获取线程ID。
    // 返回值：long - 线程ID。
    // 自版本：2.6
    long getThreadId();

    /**
     * Gets the thread priority.
     *
     * @return thread priority.
     * @since 2.6
     */
    // getThreadPriority 方法：获取线程优先级。
    // 返回值：int - 线程优先级。
    // 自版本：2.6
    int getThreadPriority();

    /**
     * Gets throwable associated with logging request.
     *
     * <p>Convenience method for {@code ThrowableProxy.getThrowable();}</p>
     *
     * @return throwable, may be null.
     */
    // getThrown 方法：获取与日志请求关联的异常。
    // 这是 ThrowableProxy.getThrowable() 的便捷方法。
    // 返回值：Throwable - 异常对象，可能为 null。
    Throwable getThrown();

    /**
     * Gets throwable proxy associated with logging request.
     *
     * @return throwable, may be null.
     */
    // getThrownProxy 方法：获取与日志请求关联的异常代理。
    // 返回值：ThrowableProxy - 异常代理对象，可能为 null。
    ThrowableProxy getThrownProxy();

    /**
     * Returns {@code true} if this event is the last one in a batch, {@code false} otherwise. Used by asynchronous
     * Loggers and Appenders to signal to buffered downstream components when to flush to disk, as a more efficient
     * alternative to the {@code immediateFlush=true} configuration.
     *
     * @return whether this event is the last one in a batch.
     */
    // isEndOfBatch 方法：判断此事件是否是批处理中的最后一个事件。
    // 返回值：boolean - 如果此事件是批处理中的最后一个，则为 true；否则为 false。
    // 异步日志器和Appender使用此标志来通知下游缓冲组件何时刷新到磁盘，
    // 作为 immediateFlush=true 配置的更高效替代方案。
    // 参见 LOG4J2-164
    boolean isEndOfBatch();

    /**
     * Returns whether the source of the logging request is required downstream. Asynchronous Loggers and Appenders use
     * this flag to determine whether to take a {@code StackTrace} snapshot or not before handing off this event to
     * another thread.
     *
     * @return {@code true} if the source of the logging request is required downstream, {@code false} otherwise.
     * @see #getSource()
     */
    // isIncludeLocation 方法：判断日志请求的源信息是否在下游需要。
    // 返回值：boolean - 如果日志请求的源信息在下游需要，则为 true；否则为 false。
    // 异步日志器和Appender使用此标志来决定在将此事件传递给另一个线程之前是否捕获堆栈跟踪快照。
    // 参见：getSource()
    // 参见 LOG4J2-153
    boolean isIncludeLocation();

    /**
     * Sets whether this event is the last one in a batch. Used by asynchronous Loggers and Appenders to signal to
     * buffered downstream components when to flush to disk, as a more efficient alternative to the
     * {@code immediateFlush=true} configuration.
     *
     * @param endOfBatch {@code true} if this event is the last one in a batch, {@code false} otherwise.
     */
    // setEndOfBatch 方法：设置此事件是否是批处理中的最后一个。
    // 参数：endOfBatch - 如果此事件是批处理中的最后一个，则为 true；否则为 false。
    // 异步日志器和Appender使用此标志来通知下游缓冲组件何时刷新到磁盘，
    // 作为 immediateFlush=true 配置的更高效替代方案。
    void setEndOfBatch(boolean endOfBatch);

    /**
     * Sets whether the source of the logging request is required downstream. Asynchronous Loggers and Appenders use
     * this flag to determine whether to take a {@code StackTrace} snapshot or not before handing off this event to
     * another thread.
     *
     * @param locationRequired {@code true} if the source of the logging request is required downstream, {@code false}
     *                         otherwise.
     * @see #getSource()
     */
    // setIncludeLocation 方法：设置日志请求的源信息是否在下游需要。
    // 参数：locationRequired - 如果日志请求的源信息在下游需要，则为 true；否则为 false。
    // 异步日志器和Appender使用此标志来决定在将此事件传递给另一个线程之前是否捕获堆栈跟踪快照。
    // 参见：getSource()
    void setIncludeLocation(boolean locationRequired);

    /**
     * Returns the value of the running Java Virtual Machine's high-resolution time source when this event was created,
     * or a dummy value if it is known that this value will not be used downstream.
     * @return The value of the running Java Virtual Machine's high-resolution time source when this event was created.
     * @since Log4J 2.4
     */
    // getNanoTime 方法：返回创建此事件时正在运行的Java虚拟机高分辨率时间源的值，
    // 如果已知此值不会在下游使用，则返回一个虚拟值。
    // 返回值：long - 创建此事件时正在运行的Java虚拟机高分辨率时间源的值。
    // 自版本：Log4J 2.4
    long getNanoTime();
}
