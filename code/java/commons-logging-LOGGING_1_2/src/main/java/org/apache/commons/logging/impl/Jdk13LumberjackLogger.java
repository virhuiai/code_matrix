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
import java.util.logging.LogRecord;
import java.util.StringTokenizer;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;

/**
 * Implementation of the <code>org.apache.commons.logging.Log</code>
 * interface that wraps the standard JDK logging mechanisms that are
 * available in SourceForge's Lumberjack for JDKs prior to 1.4.
 *
 * @version $Id$
 * @since 1.1
 */
// 中文注释：实现org.apache.commons.logging.Log接口，封装JDK标准日志机制，适用于JDK 1.4之前的版本，使用SourceForge的Lumberjack日志框架。
// 代码主要功能：提供日志记录功能，支持不同日志级别（如DEBUG、INFO、ERROR等），并通过JDK的Logger实现日志输出。
public class Jdk13LumberjackLogger implements Log, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -8649807923527610591L;
    // 中文注释：序列化版本ID，用于序列化兼容性，确保类在序列化和反序列化时版本一致。

    // ----------------------------------------------------- Instance Variables

    /**
     * The underlying Logger implementation we are using.
     */
    protected transient Logger logger = null;
    // 中文注释：底层的Logger实例，用于实际的日志记录操作，transient表示不序列化。

    protected String name = null;
    // 中文注释：日志器的名称，用于标识具体的日志实例。

    private String sourceClassName = "unknown";
    // 中文注释：记录日志的源类名，默认为"unknown"，用于标识日志来源的类。

    private String sourceMethodName = "unknown";
    // 中文注释：记录日志的源方法名，默认为"unknown"，用于标识日志来源的方法。

    private boolean classAndMethodFound = false;
    // 中文注释：标志位，指示是否已通过堆栈跟踪找到源类和方法名，初始为false。

    /**
     * This member variable simply ensures that any attempt to initialise
     * this class in a pre-1.4 JVM will result in an ExceptionInInitializerError.
     * It must not be private, as an optimising compiler could detect that it
     * is not used and optimise it away.
     */
    protected static final Level dummyLevel = Level.FINE;
    // 中文注释：重要配置参数，确保在JDK 1.4之前的环境中初始化时抛出异常，防止不兼容的运行环境。
    // 注意事项：该变量不能为private，否则优化编译器可能将其优化掉，导致无法触发预期的初始化错误。

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a named instance of this Logger.
     *
     * @param name Name of the logger to be constructed
     */
    public Jdk13LumberjackLogger(String name) {
        this.name = name;
        logger = getLogger();
    }
    // 中文注释：构造函数，创建指定名称的日志器实例。
    // 参数说明：name - 日志器的名称，用于初始化Logger实例。
    // 方法目的：初始化日志器的名称并获取对应的Logger实例。

    // --------------------------------------------------------- Public Methods

    /**
     * Logs a message with the specified level and optional exception.
     */
    private void log( Level level, String msg, Throwable ex ) {
        if( getLogger().isLoggable(level) ) {
            LogRecord record = new LogRecord(level, msg);
            if( !classAndMethodFound ) {
                getClassAndMethod();
            }
            record.setSourceClassName(sourceClassName);
            record.setSourceMethodName(sourceMethodName);
            if( ex != null ) {
                record.setThrown(ex);
            }
            getLogger().log(record);
        }
    }
    // 中文注释：记录指定级别的日志消息，可选地包含异常信息。
    // 参数说明：
    //   - level：日志级别（如FINE、INFO、SEVERE等）。
    //   - msg：日志消息内容。
    //   - ex：异常对象，可为null，表示无异常。
    // 方法目的：将日志记录到Logger实例中，包含源类和方法信息。
    // 事件处理逻辑：检查日志级别是否可记录前提是如果可记录，则创建LogRecord并设置源信息、异常信息后记录日志。
    // 特殊处理注意事项：如果未找到源类和方法名，会调用getClassAndMethod方法动态获取。

    /**
     * Gets the class and method by looking at the stack trace for the
     * first entry that is not this class.
     */
    private void getClassAndMethod() {
        try {
            Throwable throwable = new Throwable();
            throwable.fillInStackTrace();
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter( stringWriter );
            throwable.printStackTrace( printWriter );
            String traceString = stringWriter.getBuffer().toString();
            StringTokenizer tokenizer =
                new StringTokenizer( traceString, "\n" );
            tokenizer.nextToken();
            String line = tokenizer.nextToken();
            while ( line.indexOf( this.getClass().getName() )  == -1 ) {
                line = tokenizer.nextToken();
            }
            while ( line.indexOf( this.getClass().getName() ) >= 0 ) {
                line = tokenizer.nextToken();
            }
            int start = line.indexOf( "at " ) + 3;
            int end = line.indexOf( '(' );
            String temp = line.substring( start, end );
            int lastPeriod = temp.lastIndexOf( '.' );
            sourceClassName = temp.substring( 0, lastPeriod );
            sourceMethodName = temp.substring( lastPeriod + 1 );
        } catch ( Exception ex ) {
            // ignore - leave class and methodname unknown
        }
        classAndMethodFound = true;
    }
    // 中文注释：通过分析堆栈跟踪获取调用日志的类和方法名。
    // 方法目的：动态确定日志的调用源，跳过当前类的堆栈条目，提取第一个非本类的类和方法名。
    // 特殊处理注意事项：如果发生异常，源类和方法名保持为"unknown"，以避免中断日志记录。

    /**
     * Logs a message with <code>java.util.logging.Level.FINE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#debug(Object)
     */
    public void debug(Object message) {
        log(Level.FINE, String.valueOf(message), null);
    }
    // 中文注释：记录DEBUG级别的日志消息。
    // 参数说明：message - 要记录的日志消息。
    // 方法目的：将消息以FINE级别记录到日志中，不包含异常信息。

    /**
     * Logs a message with <code>java.util.logging.Level.FINE</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    public void debug(Object message, Throwable exception) {
        log(Level.FINE, String.valueOf(message), exception);
    }
    // 中文注释：记录带异常的DEBUG级别日志消息。
    // 参数说明：
    //   - message：日志消息内容。
    //   - exception：关联的异常对象。
    // 方法目的：将DEBUG级别的消息和异常记录到日志中。

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#error(Object)
     */
    public void error(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }
    // 中文注释：记录ERROR级别的日志消息。
    // 参数说明：message - 要记录的日志消息。
    // 方法目的：将消息以SEVERE级别记录到日志中，不包含异常信息。

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    public void error(Object message, Throwable exception) {
        log(Level.SEVERE, String.valueOf(message), exception);
    }
    // 中文注释：记录带异常的ERROR级别日志消息。
    // 参数说明：
    //   - message：日志消息内容。
    //   - exception：关联的异常对象。
    // 方法目的：将ERROR级别的消息和异常记录到日志中。

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#fatal(Object)
     */
    public void fatal(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }
    // 中文注释：记录FATAL级别的日志消息（与ERROR级别相同）。
    // 参数说明：message - 要记录的日志消息。
    // 方法目的：将消息以SEVERE级别记录到日志中，不包含异常信息。

    /**
     * Logs a message with <code>java.util.logging.Level.SEVERE</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    public void fatal(Object message, Throwable exception) {
        log(Level.SEVERE, String.valueOf(message), exception);
    }
    // 中文注释：记录带异常的FATAL级别日志消息（与ERROR级别相同）。
    // 参数说明：
    //   - message：日志消息内容。
    //   - exception：关联的异常对象。
    // 方法目的：将FATAL级别的消息和异常记录到日志中。

    /**
     * Return the native Logger instance we are using.
     */
    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(name);
        }
        return logger;
    }
    // 中文注释：获取底层的Logger实例。
    // 方法目的：返回当前使用的Logger实例，如果未初始化，则根据名称创建新的Logger实例。
    // 关键变量说明：logger - JDK的日志记录器，负责实际的日志输出。

    /**
     * Logs a message with <code>java.util.logging.Level.INFO</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#info(Object)
     */
    public void info(Object message) {
        log(Level.INFO, String.valueOf(message), null);
    }
    // 中文注释：记录INFO级别的日志消息。
    // 参数说明：message - 要记录的日志消息。
    // 方法目的：将消息以INFO级别记录到日志中，不包含异常信息。

    /**
     * Logs a message with <code>java.util.logging.Level.INFO</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    public void info(Object message, Throwable exception) {
        log(Level.INFO, String.valueOf(message), exception);
    }
    // 中文注释：记录带异常的INFO级别日志消息。
    // 参数说明：
    //   - message：日志消息内容。
    //   - exception：关联的异常对象。
    // 方法目的：将INFO级别的消息和异常记录到日志中。

    /**
     * Is debug logging currently enabled?
     */
    public boolean isDebugEnabled() {
        return getLogger().isLoggable(Level.FINE);
    }
    // 中文注释：检查是否启用了DEBUG级别的日志记录。
    // 方法目的：返回Logger是否允许记录FINE级别的日志。
    // 交互逻辑：用于判断是否需要记录DEBUG日志，以优化性能。

    /**
     * Is error logging currently enabled?
     */
    public boolean isErrorEnabled() {
        return getLogger().isLoggable(Level.SEVERE);
    }
    // 中文注释：检查是否启用了ERROR级别的日志记录。
    // 方法目的：返回Logger是否允许记录SEVERE级别的日志。
    // 交互逻辑：用于判断是否需要记录ERROR日志，以优化性能。

    /**
     * Is fatal logging currently enabled?
     */
    public boolean isFatalEnabled() {
        return getLogger().isLoggable(Level.SEVERE);
    }
    // 中文注释：检查是否启用了FATAL级别的日志记录（与ERROR级别相同）。
    // 方法目的：返回Logger是否允许记录SEVERE级别的日志。
    // 交互逻辑：用于判断是否需要记录FATAL日志，以优化性能。

    /**
     * Is info logging currently enabled?
     */
    public boolean isInfoEnabled() {
        return getLogger().isLoggable(Level.INFO);
    }
    // 中文注释：检查是否启用了INFO级别的日志记录。
    // 方法目的：返回Logger是否允许记录INFO级别的日志。
    // 交互逻辑：用于判断是否需要记录INFO日志，以优化性能。

    /**
     * Is trace logging currently enabled?
     */
    public boolean isTraceEnabled() {
        return getLogger().isLoggable(Level.FINEST);
    }
    // 中文注释：检查是否启用了TRACE级别的日志记录。
    // 方法目的：返回Logger是否允许记录FINEST级别的日志。
    // 交互逻辑：用于判断是否需要记录TRACE日志，以优化性能。

    /**
     * Is warn logging currently enabled?
     */
    public boolean isWarnEnabled() {
        return getLogger().isLoggable(Level.WARNING);
    }
    // 中文注释：检查是否启用了WARN级别的日志记录。
    // 方法目的：返回Logger是否允许记录WARNING级别的日志。
    // 交互逻辑：用于判断是否需要记录WARN日志，以优化性能。

    /**
     * Logs a message with <code>java.util.logging.Level.FINEST</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#trace(Object)
     */
    public void trace(Object message) {
        log(Level.FINEST, String.valueOf(message), null);
    }
    // 中文注释：记录TRACE级别的日志消息。
    // 参数说明：message - 要记录的日志消息。
    // 方法目的：将消息以FINEST级别记录到日志中，不包含异常信息。

    /**
     * Logs a message with <code>java.util.logging.Level.FINEST</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    public void trace(Object message, Throwable exception) {
        log(Level.FINEST, String.valueOf(message), exception);
    }
    // 中文注释：记录带异常的TRACE级别日志消息。
    // 参数说明：
    //   - message：日志消息内容。
    //   - exception：关联的异常对象。
    // 方法目的：将TRACE级别的消息和异常记录到日志中。

    /**
     * Logs a message with <code>java.util.logging.Level.WARNING</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#warn(Object)
     */
    public void warn(Object message) {
        log(Level.WARNING, String.valueOf(message), null);
    }
    // 中文注释：记录WARN级别的日志消息。
    // 参数说明：message - 要记录的日志消息。
    // 方法目的：将消息以WARNING级别记录到日志中，不包含异常信息。

    /**
     * Logs a message with <code>java.util.logging.Level.WARNING</code>.
     *
     * @param message to log
     * @param exception log this cause
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    public void warn(Object message, Throwable exception) {
        log(Level.WARNING, String.valueOf(message), exception);
    }
    // 中文注释：记录带异常的WARN级别日志消息。
    // 参数说明：
    //   - message：日志消息内容。
    //   - exception：关联的异常对象。
    // 方法目的：将WARN级别的消息和异常记录到日志中。
}
