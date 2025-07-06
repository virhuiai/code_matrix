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

import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.logging.Log;

/**
 * Implementation of commons-logging Log interface that delegates all
 * logging calls to the Avalon logging abstraction: the Logger interface.
 * <p>
 * There are two ways in which this class can be used:
 * <ul>
 * <li>the instance can be constructed with an Avalon logger
 * (by calling {@link #AvalonLogger(Logger)}). In this case, it acts
 * as a simple thin wrapping implementation over the logger. This is
 * particularly useful when using a property setter.
 * </li>
 * <li>the {@link #setDefaultLogger} class property can be called which
 * sets the ancestral Avalon logger for this class. Any <code>AvalonLogger</code>
 * instances created through the <code>LogFactory</code> mechanisms will output
 * to child loggers of this <code>Logger</code>.
 * </li>
 * </ul>
 * <p>
 * <strong>Note:</strong> <code>AvalonLogger</code> does not implement Serializable
 * because the constructors available for it make this impossible to achieve in all
 * circumstances; there is no way to "reconnect" to an underlying Logger object on
 * deserialization if one was just passed in to the constructor of the original
 * object. This class <i>was</i> marked Serializable in the 1.0.4 release of
 * commons-logging, but this never actually worked (a NullPointerException would
 * be thrown as soon as the deserialized object was used), so removing this marker
 * is not considered to be an incompatible change.
 *
 * @version $Id$
 */
// 中文注释：
// 该类实现了 commons-logging 的 Log 接口，将所有日志调用委托给 Avalon 日志抽象接口 Logger。
// 使用方式：1) 通过构造函数传入 Avalon Logger 实例，包装为简单的日志实现；2) 通过 setDefaultLogger 设置默认 Logger，所有通过 LogFactory 创建的 AvalonLogger 实例将使用其子 Logger。
// 注意事项：此类不实现 Serializable，因为无法在反序列化时重新连接底层的 Logger 对象。
// 关键功能：提供日志记录功能，桥接 commons-logging 和 Avalon Logger。

public class AvalonLogger implements Log {

    /** Ancestral Avalon logger. */
    // 中文注释：静态变量，存储默认的祖先 Avalon Logger，用于未指定 Logger 的实例。
    private static volatile Logger defaultLogger = null;
    /** Avalon logger used to perform log. */
    // 中文注释：实例变量，存储用于实际日志记录的 Avalon Logger 实例，transient 修饰符避免序列化。
    private final transient Logger logger;

    /**
     * Constructs an <code>AvalonLogger</code> that outputs to the given
     * <code>Logger</code> instance.
     *
     * @param logger the Avalon logger implementation to delegate to
     */
    // 中文注释：
    // 构造函数：创建 AvalonLogger 实例，接受指定的 Avalon Logger 实例。
    // 功能：将日志操作委托给传入的 logger 参数。
    // 参数说明：logger - 用于执行日志记录的 Avalon Logger 实现。
    public AvalonLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Constructs an <code>AvalonLogger</code> that will log to a child
     * of the <code>Logger</code> set by calling {@link #setDefaultLogger}.
     *
     * @param name the name of the avalon logger implementation to delegate to
     */
    // 中文注释：
    // 构造函数：创建 AvalonLogger 实例，使用默认 Logger 的子 Logger 进行日志记录。
    // 功能：基于传入的名称，获取 defaultLogger 的子 Logger 进行日志操作。
    // 参数说明：name - 子 Logger 的名称。
    // 注意事项：如果 defaultLogger 未设置（为 null），将抛出 NullPointerException。
    public AvalonLogger(String name) {
        if (defaultLogger == null) {
            throw new NullPointerException("default logger has to be specified if this constructor is used!");
        }
        this.logger = defaultLogger.getChildLogger(name);
    }

    /**
     * Gets the Avalon logger implementation used to perform logging.
     *
     * @return avalon logger implementation
     */
    // 中文注释：
    // 方法：获取当前用于日志记录的 Avalon Logger 实例。
    // 功能：返回 logger 实例，供外部访问。
    // 返回值：当前的 Avalon Logger 实例。
    public Logger getLogger() {
        return logger;
    }

    /**
     * Sets the ancestral Avalon logger from which the delegating loggers will descend.
     *
     * @param logger the default avalon logger,
     * in case there is no logger instance supplied in constructor
     */
    // 中文注释：
    // 方法：设置默认的祖先 Avalon Logger。
    // 功能：为所有未指定 Logger 的 AvalonLogger 实例设置默认 Logger，子 Logger 将从其派生。
    // 参数说明：logger - 默认的 Avalon Logger 实例。
    // 重要配置：defaultLogger 是静态变量，影响所有通过名称构造的 AvalonLogger 实例。
    public static void setDefaultLogger(Logger logger) {
        defaultLogger = logger;
    }

    /**
    * Logs a message with <code>org.apache.avalon.framework.logger.Logger.debug</code>.
    *
    * @param message to log
    * @param t log this cause
    * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    // 中文注释：
    // 方法：记录 debug 级别的日志消息，包含异常信息。
    // 功能：如果 debug 日志启用，将消息和异常委托给 logger 的 debug 方法。
    // 参数说明：message - 要记录的日志消息；t - 关联的异常对象。
    // 事件处理：检查 debug 日志是否启用，避免不必要的日志操作。
    public void debug(Object message, Throwable t) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.valueOf(message), t);
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.debug</code>.
     *
     * @param message to log.
     * @see org.apache.commons.logging.Log#debug(Object)
     */
    // 中文注释：
    // 方法：记录 debug 级别的日志消息。
    // 功能：如果 debug 日志启用，将消息委托给 logger 的 debug 方法。
    // 参数说明：message - 要记录的日志消息。
    // 事件处理：检查 debug 日志是否启用，优化性能。
    public void debug(Object message) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.valueOf(message));
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.error</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    // 中文注释：
    // 方法：记录 error 级别的日志消息，包含异常信息。
    // 功能：如果 error 日志启用，将消息和异常委托给 logger 的 error 方法。
    // 参数说明：message - 要记录的日志消息；t - 关联的异常对象。
    // 事件处理：仅在 error 日志启用时执行日志操作。
    public void error(Object message, Throwable t) {
        if (getLogger().isErrorEnabled()) {
            getLogger().error(String.valueOf(message), t);
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.error</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#error(Object)
     */
    // 中文注释：
    // 方法：记录 error 级别的日志消息。
    // 功能：如果 error 日志启用，将消息委托给 logger 的 error 方法。
    // 参数说明：message - 要记录的日志消息。
    // 事件处理：检查 error 日志是否启用，减少无用操作。
    public void error(Object message) {
        if (getLogger().isErrorEnabled()) {
            getLogger().error(String.valueOf(message));
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.fatalError</code>.
     *
     * @param message to log.
     * @param t log this cause.
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    // 中文注释：
    // 方法：记录 fatal 级别的日志消息，包含异常信息。
    // 功能：如果 fatal 日志启用，将消息和异常委托给 logger 的 fatalError 方法。
    // 参数说明：message - 要记录的日志消息；t - 关联的异常对象。
    // 事件处理：检查 fatal 日志是否启用，确保高效执行。
    public void fatal(Object message, Throwable t) {
        if (getLogger().isFatalErrorEnabled()) {
            getLogger().fatalError(String.valueOf(message), t);
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.fatalError</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#fatal(Object)
     */
    // 中文注释：
    // 方法：记录 fatal 级别的日志消息。
    // 功能：如果 fatal 日志启用，将消息委托给 logger 的 fatalError 方法。
    // 参数说明：message - 要记录的日志消息。
    // 事件处理：仅在 fatal 日志启用时执行日志操作。
    public void fatal(Object message) {
        if (getLogger().isFatalErrorEnabled()) {
            getLogger().fatalError(String.valueOf(message));
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.info</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    // 中文注释：
    // 方法：记录 info 级别的日志消息，包含异常信息。
    // 功能：如果 info 日志启用，将消息和异常委托给 logger 的 info 方法。
    // 参数说明：message - 要记录的日志消息；t - 关联的异常对象。
    // 事件处理：检查 info 日志是否启用，优化性能。
    public void info(Object message, Throwable t) {
        if (getLogger().isInfoEnabled()) {
            getLogger().info(String.valueOf(message), t);
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.info</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#info(Object)
     */
    // 中文注释：
    // 方法：记录 info 级别的日志消息。
    // 功能：如果 info 日志启用，将消息委托给 logger 的 info 方法。
    // 参数说明：message - 要记录的日志消息。
    // 事件处理：检查 info 日志是否启用，避免无用操作。
    public void info(Object message) {
        if (getLogger().isInfoEnabled()) {
            getLogger().info(String.valueOf(message));
        }
    }

    /**
     * Is logging to <code>org.apache.avalon.framework.logger.Logger.debug</code> enabled?
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    // 中文注释：
    // 方法：检查 debug 级别的日志是否启用。
    // 功能：调用 logger 的 isDebugEnabled 方法，返回是否允许记录 debug 日志。
    // 返回值：true 表示 debug 日志启用，false 表示禁用。
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    /**
     * Is logging to <code>org.apache.avalon.framework.logger.Logger.error</code> enabled?
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    // 中文注释：
    // 方法：检查 error 级别的日志是否启用。
    // 功能：调用 logger 的 isErrorEnabled 方法，返回是否允许记录 error 日志。
    // 返回值：true 表示 error 日志启用，false 表示禁用。
    public boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }

    /**
     * Is logging to <code>org.apache.avalon.framework.logger.Logger.fatalError</code> enabled?
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    // 中文注释：
    // 方法：检查 fatal 级别的日志是否启用。
    // 功能：调用 logger 的 isFatalErrorEnabled 方法，返回是否允许记录 fatal 日志。
    // 返回值：true 表示 fatal 日志启用，false 表示禁用。
    public boolean isFatalEnabled() {
        return getLogger().isFatalErrorEnabled();
    }

    /**
     * Is logging to <code>org.apache.avalon.framework.logger.Logger.info</code> enabled?
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    // 中文注释：
    // 方法：检查 info 级别的日志是否启用。
    // 功能：调用 logger 的 isInfoEnabled 方法，返回是否允许记录 info 日志。
    // 返回值：true 表示 info 日志启用，false 表示禁用。
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    /**
     * Is logging to <code>org.apache.avalon.framework.logger.Logger.debug</code> enabled?
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    // 中文注释：
    // 方法：检查 trace 级别的日志是否启用。
    // 功能：复用 logger 的 isDebugEnabled 方法，将 trace 级别映射到 debug 级别。
    // 返回值：true 表示 trace 日志启用，false 表示禁用。
    // 注意事项：trace 日志实际使用 debug 日志的配置。
    public boolean isTraceEnabled() {
        return getLogger().isDebugEnabled();
    }

    /**
     * Is logging to <code>org.apache.avalon.framework.logger.Logger.warn</code> enabled?
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    // 中文注释：
    // 方法：检查 warn 级别的日志是否启用。
    // 功能：调用 logger 的 isWarnEnabled 方法，返回是否允许记录 warn 日志。
    // 返回值：true 表示 warn 日志启用，false 表示禁用。
    public boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.debug</code>.
     *
     * @param message to log.
     * @param t log this cause.
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    // 中文注释：
    // 方法：记录 trace 级别的日志消息，包含异常信息。
    // 功能：如果 debug 日志启用，将消息和异常委托给 logger 的 debug 方法（trace 映射到 debug）。
    // 参数说明：message - 要记录的日志消息；t - 关联的异常对象。
    // 事件处理：检查 debug 日志是否启用，优化性能。
    // 注意事项：trace 日志实际使用 debug 日志的实现。
    public void trace(Object message, Throwable t) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.valueOf(message), t);
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.debug</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#trace(Object)
     */
    // 中文注释：
    // 方法：记录 trace 级别的日志消息。
    // 功能：如果 debug 日志启用，将消息委托给 logger 的 debug 方法（trace 映射到 debug）。
    // 参数说明：message - 要记录的日志消息。
    // 事件处理：检查 debug 日志是否启用，避免无用操作。
    // 注意事项：trace 日志实际使用 debug 日志的实现。
    public void trace(Object message) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.valueOf(message));
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.warn</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    // 中文注释：
    // 方法：记录 warn 级别的日志消息，包含异常信息。
    // 功能：如果 warn 日志启用，将消息和异常委托给 logger 的 warn 方法。
    // 参数说明：message - 要记录的日志消息；t - 关联的异常对象。
    // 事件处理：检查 warn 日志是否启用，优化性能。
    public void warn(Object message, Throwable t) {
        if (getLogger().isWarnEnabled()) {
            getLogger().warn(String.valueOf(message), t);
        }
    }

    /**
     * Logs a message with <code>org.apache.avalon.framework.logger.Logger.warn</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#warn(Object)
     */
    // 中文注释：
    // 方法：记录 warn 级别的日志消息。
    // 功能：如果 warn 日志启用，将消息委托给 logger 的 warn 方法。
    // 参数说明：message - 要记录的日志消息。
    // 事件处理：检查 warn 日志是否启用，减少无用操作。
    public void warn(Object message) {
        if (getLogger().isWarnEnabled()) {
            getLogger().warn(String.valueOf(message));
        }
    }
}
