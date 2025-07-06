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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;

/**
 * Implementation of the <code>org.apache.commons.logging.Log</code>
 * interface that wraps the standard JDK logging mechanisms that were
 * introduced in the Merlin release (JDK 1.4).
 *
 * @version $Id$
 */
// 中文注释：实现 org.apache.commons.logging.Log 接口，封装了 JDK 1.4 (Merlin 版本) 引入的标准日志机制。
// 主要功能：提供基于 java.util.logging 的日志记录功能，兼容 Apache Commons Logging 框架。
public class Jdk14Logger implements Log, Serializable {

    /** Serializable version identifier. */
    // 中文注释：序列化版本标识，用于确保序列化兼容性。
    private static final long serialVersionUID = 4784713551416303804L;

    /**
     * This member variable simply ensures that any attempt to initialise
     * this class in a pre-1.4 JVM will result in an ExceptionInInitializerError.
     * It must not be private, as an optimising compiler could detect that it
     * is not used and optimise it away.
     */
    // 中文注释：定义一个静态变量以确保在 JDK 1.4 之前的 JVM 中初始化此类时抛出异常。
    // 注意事项：不能设为 private，避免被优化器移除。
    protected static final Level dummyLevel = Level.FINE;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a named instance of this Logger.
     *
     * @param name Name of the logger to be constructed
     */
    // 中文注释：构造函数，创建指定名称的日志实例。
    // 参数说明：name - 日志器的名称。
    public Jdk14Logger(String name) {
        this.name = name;
        logger = getLogger();
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The underlying Logger implementation we are using.
     */
    // 中文注释：底层使用的 java.util.logging.Logger 实例。
    // 变量用途：存储实际执行日志记录的 Logger 对象。
    protected transient Logger logger = null;

    /**
     * The name of the logger we are wrapping.
     */
    // 中文注释：封装的日志器的名称。
    // 变量用途：记录日志器的标识名称，用于日志输出。
    protected String name = null;

    // --------------------------------------------------------- Protected Methods

    // 中文注释：记录日志的核心方法，处理日志级别、消息和异常。
    // 参数说明：
    //   - level：日志级别（如 Level.FINE、Level.SEVERE 等）。
    //   - msg：日志消息内容。
    //   - ex：关联的异常对象（可为 null）。
    // 事件处理逻辑：检查日志级别是否启用，若启用则记录日志，并尝试获取调用栈信息以确定调用方法。
    // 特殊处理注意事项：通过构造 Throwable 对象获取调用栈，提取调用方法名以记录更详细的日志上下文。
    protected void log( Level level, String msg, Throwable ex ) {
        Logger logger = getLogger();
        if (logger.isLoggable(level)) {
            // Hack (?) to get the stack trace.
            // 中文注释：通过构造 Throwable 对象获取调用栈信息。
            Throwable dummyException = new Throwable();
            StackTraceElement locations[] = dummyException.getStackTrace();
            // LOGGING-132: use the provided logger name instead of the class name
            // 中文注释：使用指定的日志器名称而非类名（修复 LOGGING-132 问题）。
            String cname = name;
            String method = "unknown";
            // Caller will be the third element
            // 中文注释：调用栈的第三个元素为实际调用者。
            if( locations != null && locations.length > 2 ) {
                StackTraceElement caller = locations[2];
                method = caller.getMethodName();
            }
            if( ex == null ) {
                logger.logp( level, cname, method, msg );
            } else {
                logger.logp( level, cname, method, msg, ex );
            }
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Logs a message with <code>java.util.logging.Level.FINE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#debug(Object)
     */
    // 中文注释：记录调试级别的日志（Level.FINE）。
    // 方法目的：实现 Log 接口的 debug 方法，记录调试信息。
    // 参数说明：message - 要记录的调试消息。
    public void debug(Object message) {
        log(Level.FINE, String.valueOf(message), null);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.FINE</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    // 中文注释：记录带异常的调试级别的日志（Level.FINE）。
    // 方法目的：记录调试消息及相关异常信息。
    // 参数说明：
    //   - message：调试消息。
    //   - exception：关联的异常对象。
    public void debug(Object message, Throwable exception) {
        log(Level.FINE, String.valueOf(message), exception);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#error(Object)
     */
    // 中文注释：记录错误级别的日志（Level.SEVERE）。
    // 方法目的：实现 Log 接口的 error 方法，记录错误信息。
    // 参数说明：message - 要记录的错误消息。
    public void error(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    // 中文注释：记录带异常的错误级别的日志（Level.SEVERE）。
    // 方法目的：记录错误消息及相关异常信息。
    // 参数说明：
    //   - message：错误消息。
    //   - exception：关联的异常对象。
    public void error(Object message, Throwable exception) {
        log(Level.SEVERE, String.valueOf(message), exception);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#fatal(Object)
     */
    // 中文注释：记录致命级别的日志（Level.SEVERE）。
    // 方法目的：实现 Log 接口的 fatal 方法，记录致命错误信息。
    // 参数说明：message - 要记录的致命错误消息。
    public void fatal(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    // 中文注释：记录带异常的致命级别的日志（Level.SEVERE）。
    // 方法目的：记录致命错误消息及相关异常信息。
    // 参数说明：
    //   - message：致命错误消息。
    //   - exception：关联的异常对象。
    public void fatal(Object message, Throwable exception) {
        log(Level.SEVERE, String.valueOf(message), exception);
    }

    /**
     * Return the native Logger instance we are using.
     */
    // 中文注释：获取底层使用的 Logger 实例。
    // 方法目的：返回当前使用的 java.util.logging.Logger 对象，若未初始化则根据名称创建。
    // 交互逻辑：延迟初始化 logger，确保 logger 始终可用。
    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(name);
        }
        return logger;
    }

    /**
     * Logs a message with <code>java.util.logging.Level.INFO</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#info(Object)
     */
    // 中文注释：记录信息级别的日志（Level.INFO）。
    // 方法目的：实现 Log 接口的 info 方法，记录信息消息。
    // 参数说明：message - 要记录的信息消息。
    public void info(Object message) {
        log(Level.INFO, String.valueOf(message), null);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.INFO</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    // 中文注释：记录带异常的信息级别的日志（Level.INFO）。
    // 方法目的：记录信息消息及相关异常信息。
    // 参数说明：
    //   - message：信息消息。
    //   - exception：关联的异常对象。
    public void info(Object message, Throwable exception) {
        log(Level.INFO, String.valueOf(message), exception);
    }

    /**
     * Is debug logging currently enabled?
     */
    // 中文注释：检查调试日志是否启用。
    // 方法目的：判断当前是否允许记录 Level.FINE 级别的日志。
    // 交互逻辑：通过底层 Logger 的 isLoggable 方法检查日志级别。
    public boolean isDebugEnabled() {
        return getLogger().isLoggable(Level.FINE);
    }

    /**
     * Is error logging currently enabled?
     */
    // 中文注释：检查错误日志是否启用。
    // 方法目的：判断当前是否允许记录 Level.SEVERE 级别的日志。
    public boolean isErrorEnabled() {
        return getLogger().isLoggable(Level.SEVERE);
    }

    /**
     * Is fatal logging currently enabled?
     */
    // 中文注释：检查致命日志是否启用。
    // 方法目的：判断当前是否允许记录 Level.SEVERE 级别的日志。
    public boolean isFatalEnabled() {
        return getLogger().isLoggable(Level.SEVERE);
    }

    /**
     * Is info logging currently enabled?
     */
    // 中文注释：检查信息日志是否启用。
    // 方法目的：判断当前是否允许记录 Level.INFO 级别的日志。
    public boolean isInfoEnabled() {
        return getLogger().isLoggable(Level.INFO);
    }

    /**
     * Is trace logging currently enabled?
     */
    // 中文注释：检查跟踪日志是否启用。
    // 方法目的：判断当前是否允许记录 Level.FINEST 级别的日志。
    public boolean isTraceEnabled() {
        return getLogger().isLoggable(Level.FINEST);
    }

    /**
     * Is warn logging currently enabled?
     */
    // 中文注释：检查警告日志是否启用。
    // 方法目的：判断当前是否允许记录 Level.WARNING 级别的日志。
    public boolean isWarnEnabled() {
        return getLogger().isLoggable(Level.WARNING);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.FINEST</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#trace(Object)
     */
    // 中文注释：记录跟踪级别的日志（Level.FINEST）。
    // 方法目的：实现 Log 接口的 trace 方法，记录详细跟踪信息。
    // 参数说明：message - 要记录的跟踪消息。
    public void trace(Object message) {
        log(Level.FINEST, String.valueOf(message), null);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.FINEST</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    // 中文注释：记录带异常的跟踪级别的日志（Level.FINEST）。
    // 方法目的：记录跟踪消息及相关异常信息。
    // 参数说明：
    //   - message：跟踪消息。
    //   - exception：关联的异常对象。
    public void trace(Object message, Throwable exception) {
        log(Level.FINEST, String.valueOf(message), exception);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.WARNING</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#warn(Object)
     */
    // 中文注释：记录警告级别的日志（Level.WARNING）。
    // 方法目的：实现 Log 接口的 warn 方法，记录警告信息。
    // 参数说明：message - 要记录的警告消息。
    public void warn(Object message) {
        log(Level.WARNING, String.valueOf(message), null);
    }

    /**
     * Logs a message with <code>java.util.logging.Level.WARNING</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    // 中文注释：记录带异常的警告级别的日志（Level.WARNING）。
    // 方法目的：记录警告消息及相关异常信息。
    // 参数说明：
    //   - message：警告消息。
    //   - exception：关联的异常对象。
    public void warn(Object message, Throwable exception) {
        log(Level.WARNING, String.valueOf(message), exception);
    }
}
