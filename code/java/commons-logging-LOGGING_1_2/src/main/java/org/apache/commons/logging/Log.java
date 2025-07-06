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

package org.apache.commons.logging;

/**
 * A simple logging interface abstracting logging APIs.  In order to be
 * instantiated successfully by {@link LogFactory}, classes that implement
 * this interface must have a constructor that takes a single String
 * parameter representing the "name" of this Log.
 * <p>
 * The six logging levels used by <code>Log</code> are (in order):
 * <ol>
 * <li>trace (the least serious)</li>
 * <li>debug</li>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>fatal (the most serious)</li>
 * </ol>
 * The mapping of these log levels to the concepts used by the underlying
 * logging system is implementation dependent.
 * The implementation should ensure, though, that this ordering behaves
 * as expected.
 * <p>
 * Performance is often a logging concern.
 * By examining the appropriate property,
 * a component can avoid expensive operations (producing information
 * to be logged).
 * <p>
 * For example,
 * <pre>
 *    if (log.isDebugEnabled()) {
 *        ... do something expensive ...
 *        log.debug(theResult);
 *    }
 * </pre>
 * <p>
 * Configuration of the underlying logging system will generally be done
 * external to the Logging APIs, through whatever mechanism is supported by
 * that system.
 *
 * @version $Id$
 */
// 中文注释：定义了一个简单的日志接口，抽象了底层的日志API。
// 实现此接口的类必须有一个接受单一字符串参数（日志名称）的构造函数，
// 以便通过LogFactory成功实例化。
// 日志级别从低到高依次为：trace、debug、info、warn、error、fatal。
// 日志级别的映射由具体实现决定，但需保证顺序正确。
// 通过检查日志级别是否启用，可避免执行昂贵的操作以提升性能。
// 配置通常由底层日志系统通过其支持的机制在外部完成。

public interface Log {

    /**
     * Logs a message with debug log level.
     *
     * @param message log this message
     */
    // 中文注释：记录一条debug级别的日志消息。
    // 参数说明：message - 要记录的日志消息内容。
    // 方法目的：用于输出调试相关的日志信息，通常用于开发和调试阶段。
    void debug(Object message);

    /**
     * Logs an error with debug log level.
     *
     * @param message log this message
     * @param t log this cause
     */
    // 中文注释：记录一条debug级别的错误日志，包含异常信息。
    // 参数说明：message - 要记录的日志消息内容；t - 异常原因（Throwable对象）。
    // 方法目的：记录调试阶段的错误信息，附带异常堆栈信息以便分析。
    void debug(Object message, Throwable t);

    /**
     * Logs a message with error log level.
     *
     * @param message log this message
     */
    // 中文注释：记录一条error级别的日志消息。
    // 参数说明：message - 要记录的错误日志消息内容。
    // 方法目的：用于输出程序运行中的错误信息，通常表示需要关注的异常情况。
    void error(Object message);

    /**
     * Logs an error with error log level.
     *
     * @param message log this message
     * @param t log this cause
     */
    // 中文注释：记录一条error级别的错误日志，包含异常信息。
    // 参数说明：message - 要记录的错误日志消息内容；t - 异常原因（Throwable对象）。
    // 方法目的：记录错误信息及其异常原因，用于诊断程序运行中的问题。
    void error(Object message, Throwable t);

    /**
     * Logs a message with fatal log level.
     *
     * @param message log this message
     */
    // 中文注释：记录一条fatal级别的日志消息。
    // 参数说明：message - 要记录的致命错误日志消息内容。
    // 方法目的：用于输出导致程序无法继续运行的严重错误信息。
    void fatal(Object message);

    /**
     * Logs an error with fatal log level.
     *
     * @param message log this message
     * @param t log this cause
     */
    // 中文注释：记录一条fatal级别的错误日志，包含异常信息。
    // 参数说明：message - 要记录的致命错误日志消息内容；t - 异常原因（Throwable对象）。
    // 方法目的：记录导致程序终止的严重错误及其异常原因。
    void fatal(Object message, Throwable t);

    /**
     * Logs a message with info log level.
     *
     * @param message log this message
     */
    // 中文注释：记录一条info级别的日志消息。
    // 参数说明：message - 要记录的信息日志消息内容。
    // 方法目的：用于输出程序运行中的常规信息，通常用于记录程序状态或关键步骤。
    void info(Object message);

    /**
     * Logs an error with info log level.
     *
     * @param message log this message
     * @param t log this cause
     */
    // 中文注释：记录一条info级别的错误日志，包含异常信息。
    // 参数说明：message - 要记录的信息日志消息内容；t - 异常原因（Throwable对象）。
    // 方法目的：记录信息级别的日志并附带异常信息，通常用于记录非严重问题。
    void info(Object message, Throwable t);

    /**
     * Is debug logging currently enabled?
     * <p>
     * Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than debug.
     *
     * @return true if debug is enabled in the underlying logger.
     */
    // 中文注释：检查debug级别日志是否启用。
    // 方法目的：用于避免在debug日志未启用时执行昂贵的操作（如字符串拼接）。
    // 返回值：如果底层日志系统启用了debug级别，则返回true。
    // 特殊处理：调用此方法可优化性能，避免不必要的计算。
    boolean isDebugEnabled();

    /**
     * Is error logging currently enabled?
     * <p>
     * Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than error.
     *
     * @return true if error is enabled in the underlying logger.
     */
    // 中文注释：检查error级别日志是否启用。
    // 方法目的：用于避免在error日志未启用时执行昂贵的操作。
    // 返回值：如果底层日志系统启用了error级别，则返回true。
    // 特殊处理：优化性能，避免不必要的资源消耗。
    boolean isErrorEnabled();

    /**
     * Is fatal logging currently enabled?
     * <p>
     * Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than fatal.
     *
     * @return true if fatal is enabled in the underlying logger.
     */
    // 中文注释：检查fatal级别日志是否启用。
    // 方法目的：用于避免在fatal日志未启用时执行昂贵的操作。
    //- 返回值：如果底层日志系统启用了fatal级别，则返回true。
    // 特殊处理：优化性能，特别是在严重错误日志未启用时。
    boolean isFatalEnabled();

    /**
     * Is info logging currently enabled?
     * <p>
     * Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than info.
     *
     * @return true if info is enabled in the underlying logger.
     */
    // 中文注释：检查info级别日志是否启用。
    // 方法目的：用于避免在info日志未启用时执行昂贵的操作。
    // 返回值：如果底层日志系统启用了info级别，则返回true。
    // 特殊处理：优化性能，减少不必要的计算开销。
    boolean isInfoEnabled();

    /**
     * Is trace logging currently enabled?
     * <p>
     * Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than trace.
     *
     * @return true if trace is enabled in the underlying logger.
     */
    // 中文注释：检查trace级别日志是否启用。
    // 方法目的：用于避免在trace日志未启用时执行昂贵的操作。
    // 返回值：如果底层日志系统启用了trace级别，则返回true。
    // 特殊处理：优化性能，尤其在最低级别的日志未启用时。
    boolean isTraceEnabled();

    /**
     * Is warn logging currently enabled?
     * <p>
     * Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than warn.
     *
     * @return true if warn is enabled in the underlying logger.
     */
    // 中文注释：检查warn级别日志是否启用。
    // 方法目的：用于避免在warn日志未启用时执行昂贵的操作。
    // 返回值：如果底层日志系统启用了warn级别，则返回true。
    // 特殊处理：优化性能，避免不必要的资源消耗。
    boolean isWarnEnabled();

    /**
     * Logs a message with trace log level.
     *
     * @param message log this message
     */
    // 中文注释：记录一条trace级别的日志消息。
    // 参数说明：message - 要记录的跟踪日志消息内容。
    // 方法目的：用于输出最详细的日志信息，通常用于跟踪程序执行细节。
    void trace(Object message);

    /**
     * Logs an error with trace log level.
     *
     * @param message log this message
     * @param t log this cause
     */
    // 中文注释：记录一条trace级别的错误日志，包含异常信息。
    // 参数说明：message - 要记录的跟踪日志消息内容；t - 异常原因（Throwable对象）。
    // 方法目的：记录详细的跟踪信息及其异常原因，用于调试复杂问题。
    void trace(Object message, Throwable t);

    /**
     * Logs a message with warn log level.
     *
     * @param message log this message
     */
    // 中文注释：记录一条warn级别的日志消息。
    // 参数说明：message - 要记录的警告日志消息内容。
    // 方法目的：用于输出警告信息，提示可能的问题但不影响程序运行。
    void warn(Object message);

    /**
     * Logs an error with warn log level.
     *
     * @param message log this message
     * @param t log this cause
     */
    // 中文注释：记录一条warn级别的错误日志，包含异常信息。
    // 参数说明：message - 要记录的警告日志消息内容；t - 异常原因（Throwable对象）。
    // 方法目的：记录警告级别的错误信息及其异常原因，提示潜在问题。
    void warn(Object message, Throwable t);
}
