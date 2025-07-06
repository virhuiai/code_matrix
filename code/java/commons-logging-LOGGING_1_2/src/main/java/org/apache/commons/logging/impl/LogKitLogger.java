/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.logging.impl;

import java.io.Serializable;
import org.apache.log.Logger;
import org.apache.log.Hierarchy;
import org.apache.commons.logging.Log;

/**
 * Implementation of <code>org.apache.commons.logging.Log</code>
 * that wraps the <a href="http://avalon.apache.org/logkit/">avalon-logkit</a>
 * logging system. Configuration of <code>LogKit</code> is left to the user.
 * <p>
 * <code>LogKit</code> accepts only <code>String</code> messages.
 * Therefore, this implementation converts object messages into strings
 * by called their <code>toString()</code> method before logging them.
 *
 * @version $Id$
 */
// 中文注释：实现 org.apache.commons.logging.Log 接口，封装 avalon-logkit 日志系统。
// 用户需自行配置 LogKit。LogKit 仅接受字符串消息，因此本实现将对象消息通过 toString() 方法转换为字符串后记录。

public class LogKitLogger implements Log, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 3768538055836059519L;
    // 中文注释：序列化版本标识符，用于确保序列化兼容性，值为固定长整型。

    // ------------------------------------------------------------- Attributes

    /** Logging goes to this <code>LogKit</code> logger */
    protected transient volatile Logger logger = null;
    // 中文注释：LogKit 日志记录器实例，负责实际的日志记录，标记为 transient 和 volatile 以支持序列化和线程安全。

    /** Name of this logger */
    protected String name = null;
    // 中文注释：日志记录器的名称，用于标识特定的日志实例。

    // ------------------------------------------------------------ Constructor

    /**
     * Construct <code>LogKitLogger</code> which wraps the <code>LogKit</code>
     * logger with given name.
     *
     * @param name log name
     */
    public LogKitLogger(String name) {
        this.name = name;
        this.logger = getLogger();
    }
    // 中文注释：构造方法，创建 LogKitLogger 实例，包装具有指定名称的 LogKit 日志记录器。
    // 参数说明：
    //   - name：日志记录器的名称，用于标识日志来源。
    // 关键步骤：初始化 name 属性并通过 getLogger() 获取 logger 实例。

    // --------------------------------------------------------- Public Methods

    /**
     * Return the underlying Logger we are using.
     */
    public Logger getLogger() {
        Logger result = logger;
        if (result == null) {
            synchronized(this) {
                result = logger;
                if (result == null) {
                    logger = result = Hierarchy.getDefaultHierarchy().getLoggerFor(name);
                }
            }
        }
        return result;
    }
    // 中文注释：获取底层的 LogKit Logger 实例。
    // 方法目的：提供对 logger 对象的访问，确保线程安全地初始化 logger。
    // 关键步骤：
    //   1. 检查 logger 是否已初始化。
    //   2. 如果未初始化，使用同步块确保线程安全。
    //   3. 通过 Hierarchy.getDefaultHierarchy().getLoggerFor(name) 获取指定名称的 logger。
    // 特殊处理：使用双重检查锁定模式以避免重复初始化，提高性能。

    // ----------------------------------------------------- Log Implementation

    /**
     * Logs a message with <code>org.apache.log.Priority.DEBUG</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#trace(Object)
    */
    public void trace(Object message) {
        debug(message);
    }
    // 中文注释：以 DEBUG 优先级记录日志消息。
    // 方法目的：实现 trace 日志功能，实际调用 debug 方法。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    // 交互逻辑：直接调用 debug 方法处理消息，简化 trace 日志实现。

    /**
     * Logs a message with <code>org.apache.log.Priority.DEBUG</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    public void trace(Object message, Throwable t) {
        debug(message, t);
    }
    // 中文注释：以 DEBUG 优先级记录带异常的日志消息。
    // 方法目的：实现带异常的 trace 日志功能，实际调用 debug 方法。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    //   - t：异常对象，记录相关的错误原因。
    // 交互逻辑：调用 debug 方法处理消息和异常信息。

    /**
     * Logs a message with <code>org.apache.log.Priority.DEBUG</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#debug(Object)
     */
    public void debug(Object message) {
        if (message != null) {
            getLogger().debug(String.valueOf(message));
        }
    }
    // 中文注释：以 DEBUG 优先级记录日志消息。
    // 方法目的：记录调试级别的日志信息。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 debug 方法。
    // 特殊处理：仅当消息非 null 时记录，避免空消息日志。

    /**
     * Logs a message with <code>org.apache.log.Priority.DEBUG</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    public void debug(Object message, Throwable t) {
        if (message != null) {
            getLogger().debug(String.valueOf(message), t);
        }
    }
    // 中文注释：以 DEBUG 优先级记录带异常的日志消息。
    // 方法目的：记录调试级别的日志信息及其关联的异常。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    //   - t：异常对象，记录相关的错误原因。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 debug 方法，并附加异常信息。
    // 特殊处理：仅当消息非 null 时记录，避免空消息日志。

    /**
     * Logs a message with <code>org.apache.log.Priority.INFO</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#info(Object)
     */
    public void info(Object message) {
        if (message != null) {
            getLogger().info(String.valueOf(message));
        }
    }
    // 中文注释：以 INFO 优先级记录日志消息。
    // 方法目的：记录信息级别的日志。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 info 方法。

    /**
     * Logs a message with <code>org.apache.log.Priority.INFO</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    public void info(Object message, Throwable t) {
        if (message != null) {
            getLogger().info(String.valueOf(message), t);
        }
    }
    // 中文注释：以 INFO 优先级记录带异常的日志消息。
    // 方法目的：记录信息级别的日志及其关联的异常。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    //   - t：异常对象，记录相关的错误原因。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 info 方法，并附加异常信息。

    /**
     * Logs a message with <code>org.apache.log.Priority.WARN</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#warn(Object)
     */
    public void warn(Object message) {
        if (message != null) {
            getLogger().warn(String.valueOf(message));
        }
    }
    // 中文注释：以 WARN 优先级记录日志消息。
    // 方法目的：记录警告级别的日志。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 warn 方法。

    /**
     * Logs a message with <code>org.apache.log.Priority.WARN</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    public void warn(Object message, Throwable t) {
        if (message != null) {
            getLogger().warn(String.valueOf(message), t);
        }
    }
    // 中文注释：以 WARN 优先级记录带异常的日志消息。
    // 方法目的：记录警告级别的日志及其关联的异常。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    //   - t：异常对象，记录相关的错误原因。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 warn 方法，并附加异常信息。

    /**
     * Logs a message with <code>org.apache.log.Priority.ERROR</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#error(Object)
     */
    public void error(Object message) {
        if (message != null) {
            getLogger().error(String.valueOf(message));
        }
    }
    // 中文注释：以 ERROR 优先级记录日志消息。
    // 方法目的：记录错误级别的日志。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 error 方法。

    /**
     * Logs a message with <code>org.apache.log.Priority.ERROR</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    public void error(Object message, Throwable t) {
        if (message != null) {
            getLogger().error(String.valueOf(message), t);
        }
    }
    // 中文注释：以 ERROR 优先级记录带异常的日志消息。
    // 方法目的：记录错误级别的日志及其关联的异常。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    //   - t：异常对象，记录相关的错误原因。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 error 方法，并附加异常信息。

    /**
     * Logs a message with <code>org.apache.log.Priority.FATAL_ERROR</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#fatal(Object)
     */
    public void fatal(Object message) {
        if (message != null) {
            getLogger().fatalError(String.valueOf(message));
        }
    }
    // 中文注释：以 FATAL_ERROR 优先级记录日志消息。
    // 方法目的：记录致命错误级别的日志。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 fatalError 方法。

    /**
     * Logs a message with <code>org.apache.log.Priority.FATAL_ERROR</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    public void fatal(Object message, Throwable t) {
        if (message != null) {
            getLogger().fatalError(String.valueOf(message), t);
        }
    }
    // 中文注释：以 FATAL_ERROR 优先级记录带异常的日志消息。
    // 方法目的：记录致命错误级别的日志及其关联的异常。
    // 参数说明：
    //   - message：要记录的日志消息对象。
    //   - t：异常对象，记录相关的错误原因。
    // 关键步骤：
    //   1. 检查消息是否为 null。
    //   2. 将消息对象转换为字符串后调用 logger 的 fatalError 方法，并附加异常信息。

    /**
     * Checks whether the <code>LogKit</code> logger will log messages of priority <code>DEBUG</code>.
     */
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }
    // 中文注释：检查 LogKit 日志记录器是否启用 DEBUG 优先级日志。
    // 方法目的：判断是否允许记录调试级别的日志。
    // 返回值：布尔值，表示 DEBUG 级别日志是否启用。

    /**
     * Checks whether the <code>LogKit</code> logger will log messages of priority <code>ERROR</code>.
     */
    public boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }
    // 中文注释：检查 LogKit 日志记录器是否启用 ERROR 优先级日志。
    // 方法目的：判断是否允许记录错误级别的日志。
    // 返回值：布尔值，表示 ERROR 级别日志是否启用。

    /**
     * Checks whether the <code>LogKit</code> logger will log messages of priority <code>FATAL_ERROR</code>.
     */
    public boolean isFatalEnabled() {
        return getLogger().isFatalErrorEnabled();
    }
    // 中文注释：检查 LogKit 日志记录器是否启用 FATAL_ERROR 优先级日志。
    // 方法目的：判断是否允许记录致命错误级别的日志。
    // 返回值：布尔值，表示 FATAL_ERROR 级别日志是否启用。

    /**
     * Checks whether the <code>LogKit</code> logger will log messages of priority <code>INFO</code>.
     */
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }
    // 中文注释：检查 LogKit 日志记录器是否启用 INFO 优先级日志。
    // 方法目的：判断是否允许记录信息级别的日志。
    // 返回值：布尔值，表示 INFO 级别日志是否启用。

    /**
     * Checks whether the <code>LogKit</code> logger will log messages of priority <code>DEBUG</code>.
     */
    public boolean isTraceEnabled() {
        return getLogger().isDebugEnabled();
    }
    // 中文注释：检查 LogKit 日志记录器是否启用 TRACE 优先级日志。
    // 方法目的：判断是否允许记录跟踪级别的日志（实际复用 DEBUG 级别检查）。
    // 返回值：布尔值，表示 TRACE 级别日志是否启用。

    /**
     * Checks whether the <code>LogKit</code> logger will log messages of priority <code>WARN</code>.
     */
    public boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }
    // 中文注释：检查 LogKit 日志记录器是否启用 WARN 优先级日志。
    // 方法目的：判断是否允许记录警告级别的日志。
    // 返回值：布尔值，表示 WARN 级别日志是否启用。
}
