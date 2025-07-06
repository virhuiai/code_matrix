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
package org.apache.logging.log4j.jcl;

// 中文注释：定义包路径，属于 Apache Log4j 的 JCL（Jakarta Commons Logging）适配器模块

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.ExtendedLogger;

// 中文注释：导入必要的类，包括 Java 序列化接口、Commons Logging 接口和 Log4j 的日志级别与扩展日志接口

/**
 *
 */
public class Log4jLog implements Log, Serializable {
    // 中文注释：定义 Log4jLog 类，实现 Commons Logging 的 Log 接口和 Serializable 接口，用于适配 Log4j 日志系统

    private static final long serialVersionUID = 1L;
    // 中文注释：序列化版本号，确保序列化兼容性，值为 1L

    private static final String FQCN = Log4jLog.class.getName();
    // 中文注释：定义常量 FQCN，表示当前类的全限定类名，用于日志记录的调用者标识
    //    FQCN 是 Fully Qualified Class Name 的缩写，意为“完全限定类名”。它表示一个类的完整名称，包括其所在的包路径和类名，例如 org.apache.logging.log4j.jcl.Log4jLog。
    //    Fully: /ˈfʊli/
    //    Qualified: /ˈkwɒlɪfaɪd/
    //    Class: /klæs/
    //    Name: /neɪm/

    private final ExtendedLogger logger;
    // 中文注释：定义私有成员变量 logger，类型为 ExtendedLogger，用于执行实际的 Log4j 日志操作

    public Log4jLog(final ExtendedLogger logger) {
        this.logger = logger;
    }
    // 中文注释：构造函数，接受 ExtendedLogger 参数并初始化 logger 成员变量
    // 重要配置参数：logger 是 Log4j 的核心日志记录对象，用于处理所有日志操作

    @Override
    public boolean isDebugEnabled() {
        return logger.isEnabled(Level.DEBUG, null, null);
    }
    // 中文注释：检查是否启用了 DEBUG 级别的日志
    // 方法目的：判断是否允许记录 DEBUG 级别的日志
    // 返回值：布尔值，true 表示启用，false 表示禁用

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabled(Level.ERROR, null, null);
    }
    // 中文注释：检查是否启用了 ERROR 级别的日志
    // 方法目的：判断是否允许记录 ERROR 级别的日志
    // 返回值：布尔值，true 表示启用，false 表示禁用

    @Override
    public boolean isFatalEnabled() {
        return logger.isEnabled(Level.FATAL, null, null);
    }
    // 中文注释：检查是否启用了 FATAL 级别的日志
    // 方法目的：判断是否允许记录 FATAL 级别的日志
    // 返回值：布尔值，true 表示启用，false 表示禁用

    @Override
    public boolean isInfoEnabled() {
        return logger.isEnabled(Level.INFO, null, null);
    }
    // 中文注释：检查是否启用了 INFO 级别的日志
    // 方法目的：判断是否允许记录 INFO 级别的日志
    // 返回值：布尔值，true 表示启用，false 表示禁用

    @Override
    public boolean isTraceEnabled() {
        return logger.isEnabled(Level.TRACE, null, null);
    }
    // 中文注释：检查是否启用了 TRACE 级别的日志
    // 方法目的：判断是否允许记录 TRACE 级别的日志
    // 返回值：布尔值，true 表示启用，false 表示禁用

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabled(Level.WARN, null, null);
    }
    // 中文注释：检查是否启用了 WARN 级别的日志
    // 方法目的：判断是否允许记录 WARN 级别的日志
    // 返回值：布尔值，true 表示启用，false 表示禁用

    @Override
    public void trace(final Object message) {
        logger.logIfEnabled(FQCN, Level.TRACE, null, message, null);
    }
    // 中文注释：记录 TRACE 级别的日志消息
    // 方法目的：将指定的消息记录为 TRACE 级别的日志
    // 参数说明：message 为要记录的日志内容
    // 事件处理逻辑：仅当 TRACE 级别启用时记录日志
    // 特殊处理注意事项：使用 FQCN 标识调用者，异常参数为 null

    @Override
    public void trace(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.TRACE, null, message, t);
    }
    // 中文注释：记录 TRACE 级别的日志消息，包含异常信息
    // 方法目的：将消息和异常记录为 TRACE 级别的日志
    // 参数说明：message 为日志内容，t 为异常对象
    // 事件处理逻辑：仅当 TRACE 级别启用时记录日志和异常
    // 特殊处理注意事项：使用 FQCN 标识调用者

    @Override
    public void debug(final Object message) {
        logger.logIfEnabled(FQCN, Level.DEBUG, null, message, null);
    }
    // 中文注释：记录 DEBUG 级别的日志消息
    // 方法目的：将指定的消息记录为 DEBUG 级别的日志
    // 参数说明：message 为要记录的日志内容
    // 事件处理逻辑：仅当 DEBUG 级别启用时记录日志
    // 特殊处理注意事项：使用 FQCN 标识调用者，异常参数为 null

    @Override
    public void debug(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.DEBUG, null, message, t);
    }
    // 中文注释：记录 DEBUG 级别的日志消息，包含异常信息
    // 方法目的：将消息和异常记录为 DEBUG 级别的日志
    // 参数说明：message 为日志内容，t 为异常对象
    // 事件处理逻辑：仅当 DEBUG 级别启用时记录日志和异常
    // 特殊处理注意事项：使用 FQCN 标识调用者

    @Override
    public void info(final Object message) {
        logger.logIfEnabled(FQCN, Level.INFO, null, message, null);
    }
    // 中文注释：记录 INFO 级别的日志消息
    // 方法目的：将指定的消息记录为 INFO 级别的日志
    // 参数说明：message 为要记录的日志内容
    // 事件处理逻辑：仅当 INFO 级别启用时记录日志
    // 特殊处理注意事项：使用 FQCN 标识调用者，异常参数为 null

    @Override
    public void info(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.INFO, null, message, t);
    }
    // 中文注释：记录 INFO 级别的日志消息，包含异常信息
    // 方法目的：将消息和异常记录为 INFO 级别的日志
    // 参数说明：message 为日志内容，t 为异常对象
    // 事件处理逻辑：仅当 INFO 级别启用时记录日志和异常
    // 特殊处理注意事项：使用 FQCN 标识调用者

    @Override
    public void warn(final Object message) {
        logger.logIfEnabled(FQCN, Level.WARN, null, message, null);
    }
    // 中文注释：记录 WARN 级别的日志消息
    // 方法目的：将指定的消息记录为 WARN 级别的日志
    // 参数说明：message 为要记录的日志内容
    // 事件处理逻辑：仅当 WARN 级别启用时记录日志
    // 特殊处理注意事项：使用 FQCN 标识调用者，异常参数为 null

    @Override
    public void warn(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.WARN, null, message, t);
    }
    // 中文注释：记录 WARN 级别的日志消息，包含异常信息
    // 方法目的：将消息和异常记录为 WARN 级别的日志
    // 参数说明：message 为日志内容，t 为异常对象
    // 事件处理逻辑：仅当 WARN 级别启用时记录日志和异常
    // 特殊处理注意事项：使用 FQCN 标识调用者

    @Override
    public void error(final Object message) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, message, null);
    }
    // 中文注释：记录 ERROR 级别的日志消息
    // 方法目的：将指定的消息记录为 ERROR 级别的日志
    // 参数说明：message 为要记录的日志内容
    // 事件处理逻辑：仅当 ERROR 级别启用时记录日志
    // 特殊处理注意事项：使用 FQCN 标识调用者，异常参数为 null

    @Override
    public void error(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, message, t);
    }
    // 中文注释：记录 ERROR 级别的日志消息，包含异常信息
    // 方法目的：将消息和异常记录为 ERROR 级别的日志
    // 参数说明：message 为日志内容，t 为异常对象
    // 事件处理逻辑：仅当 ERROR 级别启用时记录日志和异常
    // 特殊处理注意事项：使用 FQCN 标识调用者

    @Override
    public void fatal(final Object message) {
        logger.logIfEnabled(FQCN, Level.FATAL, null, message, null);
    }
    // 中文注释：记录 FATAL 级别的日志消息
    // 方法目的：将指定的消息记录为 FATAL 级别的日志
    // 参数说明：message 为要记录的日志内容
    // 事件处理逻辑：仅当 FATAL 级别启用时记录日志
    // 特殊处理注意事项：使用 FQCN 标识调用者，异常参数为 null

    @Override
    public void fatal(final Object message, final Throwable t) {
        logger.logIfEnabled(FQCN, Level.FATAL, null, message, t);
    }
    // 中文注释：记录 FATAL 级别的日志消息，包含异常信息
    // 方法目的：将消息和异常记录为 FATAL 级别的日志
    // 参数说明：message 为日志内容，t 为异常对象
    // 事件处理逻辑：仅当 FATAL 级别启用时记录日志和异常
    // 特殊处理注意事项：使用 FQCN 标识调用者
}
