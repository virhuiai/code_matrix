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
package org.apache.logging.log4j.core;

import java.io.Serializable;

/**
 * Appends {@link LogEvent}s. An Appender can contain a {@link Layout} if applicable as well
 * as an {@link ErrorHandler}. Typical Appender implementations coordinate with an
 * implementation of {@link org.apache.logging.log4j.core.appender.AbstractManager} to handle external resources
 * such as streams, connections, and other shared state. As Appenders are plugins, concrete implementations need to
 * be annotated with {@link org.apache.logging.log4j.core.config.plugins.Plugin} and need to provide a static
 * factory method annotated with {@link org.apache.logging.log4j.core.config.plugins.PluginFactory}.
 *
 * <p>Most core plugins are written using a related Manager class that handle the actual task of serializing a
 * {@link LogEvent} to some output location. For instance, many Appenders can take
 * advantage of the {@link org.apache.logging.log4j.core.appender.OutputStreamManager} class.</p>
 *
 * <p>It is recommended that Appenders don't do any heavy lifting since there can be many instances of the class
 * being used at any given time. When resources require locking (e.g., through {@link java.nio.channels.FileLock}),
 * it is important to isolate synchronized code to prevent concurrency issues.</p>
 *
 * Appender 接口用于追加 {@link LogEvent}。
 * Appender 可以包含一个 {@link Layout}（如果适用）以及一个 {@link ErrorHandler}。
 * 典型的 Appender 实现会与 {@link org.apache.logging.log4j.core.appender.AbstractManager} 的实现进行协调，以处理外部资源，例如流、连接和其他共享状态。
 * 由于 Appender 是插件，具体的实现需要使用 {@link org.apache.logging.log4j.core.config.plugins.Plugin} 进行注解，并且需要提供一个使用 {@link org.apache.logging.log4j.core.config.plugins.PluginFactory} 注解的静态工厂方法。
 * 大多数核心插件都使用相关的 Manager 类编写，这些 Manager 类负责将 {@link LogEvent} 序列化到某个输出位置的实际任务。例如，许多 Appender 可以利用 {@link org.apache.logging.log4j.core.appender.OutputStreamManager} 类。
 * 建议 Appender 不要执行任何繁重的工作，因为在任何给定时间都可能存在许多该类的实例。当资源需要锁定（例如，通过 {@link java.nio.channels.FileLock}）时，隔离同步代码以防止并发问题非常重要。
 */
public interface Appender extends LifeCycle {

    /**
     * Main {@linkplain org.apache.logging.log4j.core.config.plugins.Plugin#elementType() plugin element type} for
     * Appender plugins.
     *
     * @since 2.6
     */
    // Appender 插件的主要插件元素类型。
    // 该字段用于标识 Appender 在 Log4j 配置中的类型。
    String ELEMENT_TYPE = "appender";
    
    /**
     * Empty array.
     */
    // 空数组。
    // 提供一个空的 Appender 数组，方便在某些场景下作为默认值或避免空指针异常。
    Appender[] EMPTY_ARRAY = {};

    /**
     * Logs a LogEvent using whatever logic this Appender wishes to use. It is typically recommended to use a
     * bridge pattern not only for the benefits from decoupling an Appender from its implementation, but it is also
     * handy for sharing resources which may require some form of locking.
     *
     * @param event The LogEvent.
     */
    // 使用此 Appender 希望使用的任何逻辑记录一个 LogEvent。
    // 通常建议使用桥接模式，不仅可以实现 Appender 与其实现解耦的好处，而且对于共享可能需要某种形式锁定的资源也很方便。
    // 参数: event - 要追加的日志事件。
    void append(LogEvent event);


    /**
     * Gets the name of this Appender.
     *
     * @return name, may be null.
     */
    // 获取此 Appender 的名称。
    // 返回值: name - Appender 的名称，可能为 null。
    String getName();

    /**
     * Returns the Layout used by this Appender if applicable.
     *
     * @return the Layout for this Appender or {@code null} if none is configured.
     */
    // 返回此 Appender 使用的 Layout（如果适用）。
    // 返回值: the Layout - 此 Appender 的 Layout，如果没有配置则为 {@code null}。
    Layout<? extends Serializable> getLayout();

    /**
     * Some Appenders need to propagate exceptions back to the application. When {@code ignoreExceptions} is
     * {@code false} the AppenderControl will allow the exception to percolate.
     *
     * @return {@code true} if exceptions will be logged but not thrown, {@code false} otherwise.
     */
    // 有些 Appender 需要将异常传播回应用程序。当 {@code ignoreExceptions} 为 {@code false} 时，AppenderControl 将允许异常冒泡。
    // 返回值: {@code true} - 如果异常将被记录但不抛出；{@code false} - 否则。
    // 该方法控制 Appender 在处理日志事件时遇到异常时的行为。
    boolean ignoreExceptions();

    /**
     * Gets the {@link ErrorHandler} used for handling exceptions.
     *
     * @return the ErrorHandler for handling exceptions.
     */
    // 获取用于处理异常的 {@link ErrorHandler}。
    // 返回值: the ErrorHandler - 用于处理异常的 ErrorHandler。
    ErrorHandler getHandler();

    /**
     * Sets the {@link ErrorHandler} used for handling exceptions.
     *
     * @param handler the ErrorHandler to use for handling exceptions.
     */
    // 设置用于处理异常的 {@link ErrorHandler}。
    // 参数: handler - 要用于处理异常的 ErrorHandler。
    // 此方法允许动态配置或更改 Appender 的错误处理机制。
    void setHandler(ErrorHandler handler);
}
