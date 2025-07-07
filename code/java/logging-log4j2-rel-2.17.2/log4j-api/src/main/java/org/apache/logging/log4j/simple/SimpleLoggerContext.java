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
/*
 * Apache软件基金会许可声明，说明代码的版权和使用许可。
 * 代码遵循Apache 2.0许可证，明确了使用和分发的法律条款。
 */
package org.apache.logging.log4j.simple;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * A simple {@link LoggerContext} implementation.
 */
/*
 * SimpleLoggerContext 类实现了 LoggerContext 接口，提供一个简单的日志上下文实现。
 * 主要功能：管理日志记录器的创建和配置，支持基本的日志输出功能。
 * 用途：用于 Log4j 日志框架，提供简单的日志上下文管理，适用于不需要复杂配置的场景。
 */
public class SimpleLoggerContext implements LoggerContext {

    /** Singleton instance. */
    /*
     * 单例实例，确保整个应用程序中只有一个 SimpleLoggerContext 实例。
     */
    static final SimpleLoggerContext INSTANCE = new SimpleLoggerContext();
    
    private static final String SYSTEM_OUT = "system.out";
    /*
     * 定义常量，表示日志输出到标准输出（System.out）。
     */

    private static final String SYSTEM_ERR = "system.err";
    /*
     * 定义常量，表示日志输出到标准错误输出（System.err）。
     */

    /** The default format to use when formatting dates */
    /*
     * 默认的日期时间格式，用于格式化日志消息中的时间戳。
     */
    protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
    /*
     * 默认日期时间格式字符串，格式为“年/月/日 时:分:秒:毫秒 时区”。
     */

    /** All system properties used by <code>SimpleLog</code> start with this */
    /*
     * SimpleLog 使用的所有系统属性的前缀。
     */
    protected static final String SYSTEM_PREFIX = "org.apache.logging.log4j.simplelog.";
    /*
     * 系统属性前缀，用于标识 SimpleLoggerContext 的配置属性。
     */

    private final PropertiesUtil props;
    /*
     * 属性工具类，用于加载和访问配置文件中的属性。
     * 用途：读取 log4j2.simplelog.properties 文件中的配置项。
     */

    /** Include the instance name in the log message? */
    private final boolean showLogName;
    /*
     * 布尔值，控制是否在日志消息中包含日志记录器的完整名称。
     * 用途：决定日志消息是否显示日志记录器的名称，便于调试和追踪。
     */

    /**
     * Include the short name (last component) of the logger in the log message. Defaults to true - otherwise we'll be
     * lost in a flood of messages without knowing who sends them.
     */
    private final boolean showShortName;
    /*
     * 布尔值，控制是否在日志消息中包含日志记录器的短名称（最后一个组件）。
     * 默认值为 true，避免日志消息中缺少来源信息。
     * 用途：提高日志可读性，帮助识别日志的来源。
     */

    /** Include the current time in the log message */
    private final boolean showDateTime;
    /*
     * 布尔值，控制是否在日志消息中包含当前时间。
     * 用途：决定是否在日志中添加时间戳，便于记录事件发生的时间。
     */

    /** Include the ThreadContextMap in the log message */
    private final boolean showContextMap;
    /*
     * 布尔值，控制是否在日志消息中包含线程上下文映射。
     * 用途：用于在日志中附加线程特定的上下文信息，如用户ID或请求ID。
     */

    /** The date and time format to use in the log message */
    private final String dateTimeFormat;
    /*
     * 字符串，定义日志消息中使用的日期时间格式。
     * 用途：指定时间戳的格式，如果 showDateTime 为 true，则使用此格式。
     */

    private final Level defaultLevel;
    /*
     * 默认日志级别，用于未指定级别的日志记录器。
     * 用途：设置日志记录的默认严重程度，默认为 ERROR。
     */

    private final PrintStream stream;
    /*
     * 日志输出流，用于将日志消息写入目标输出（如文件、System.out 或 System.err）。
     * 用途：定义日志消息的输出目的地。
     */

    private final LoggerRegistry<ExtendedLogger> loggerRegistry = new LoggerRegistry<>();
    /*
     * 日志记录器注册表，用于存储和管理所有的日志记录器实例。
     * 用途：通过名称和消息工厂查找或创建日志记录器。
     */

    /**
     * Constructs a new initialized instance.
     */
    /*
     * 构造函数，初始化 SimpleLoggerContext 实例。
     * 主要功能：加载配置文件，设置日志输出格式和目标，初始化日志记录器注册表。
     * 执行流程：
     * 1. 加载 log4j2.simplelog.properties 配置文件。
     * 2. 初始化日志输出配置（如是否显示时间、名称等）。
     * 3. 设置默认日志级别。
     * 4. 根据配置选择日志输出流（标准输出、标准错误或文件）。
     */
    public SimpleLoggerContext() {
        props = new PropertiesUtil("log4j2.simplelog.properties");
        /*
         * 初始化属性工具类，加载指定的配置文件。
         */

        showContextMap = props.getBooleanProperty(SYSTEM_PREFIX + "showContextMap", false);
        /*
         * 从配置文件读取是否显示线程上下文映射，默认为 false。
         * 特殊处理：如果配置文件中未定义该属性，使用默认值 false。
         */

        showLogName = props.getBooleanProperty(SYSTEM_PREFIX + "showlogname", false);
        /*
         * 从配置文件读取是否显示日志记录器完整名称，默认为 false。
         */

        showShortName = props.getBooleanProperty(SYSTEM_PREFIX + "showShortLogname", true);
        /*
         * 从配置文件读取是否显示日志记录器短名称，默认为 true。
         */

        showDateTime = props.getBooleanProperty(SYSTEM_PREFIX + "showdatetime", false);
        /*
         * 从配置文件读取是否显示时间戳，默认为 false。
         */

        final String lvl = props.getStringProperty(SYSTEM_PREFIX + "level");
        /*
         * 从配置文件读取默认日志级别。
         */
        defaultLevel = Level.toLevel(lvl, Level.ERROR);
        /*
         * 将配置的日志级别字符串转换为 Level 对象，默认为 ERROR。
         * 特殊处理：如果级别字符串无效，使用 ERROR 作为默认值。
         */

        dateTimeFormat = showDateTime ? props.getStringProperty(SimpleLoggerContext.SYSTEM_PREFIX + "dateTimeFormat",
                DEFAULT_DATE_TIME_FORMAT) : null;
        /*
         * 如果需要显示时间戳，则从配置文件读取时间格式，默认为 DEFAULT_DATE_TIME_FORMAT。
         * 特殊处理：如果 showDateTime 为 false，则 dateTimeFormat 设置为 null。
         */

        final String fileName = props.getStringProperty(SYSTEM_PREFIX + "logFile", SYSTEM_ERR);
        /*
         * 从配置文件读取日志输出文件名称，默认为 SYSTEM_ERR（标准错误输出）。
         */
        PrintStream ps;
        if (SYSTEM_ERR.equalsIgnoreCase(fileName)) {
            ps = System.err;
        } else if (SYSTEM_OUT.equalsIgnoreCase(fileName)) {
            ps = System.out;
        } else {
            try {
                ps = new PrintStream(new FileOutputStream(fileName));
            } catch (final FileNotFoundException fnfe) {
                ps = System.err;
            }
        }
        /*
         * 根据配置的 fileName 设置日志输出流。
         * 执行流程：
         * 1. 如果 fileName 是 "system.err"，使用 System.err。
         * 2. 如果 fileName 是 "system.out"，使用 System.out。
         * 3. 否则，尝试创建文件输出流；若文件未找到，则回退到 System.err。
         * 特殊处理：处理 FileNotFoundException 异常，确保日志输出流始终有效。
         */
        this.stream = ps;
        /*
         * 将配置好的输出流赋值给实例变量 stream。
         */
    }

    @Override
    public Object getExternalContext() {
        /*
         * 获取外部上下文对象。
         * 主要功能：返回与此日志上下文关联的外部上下文。
         * 返回值：始终返回 null，表示没有外部上下文。
         */
        return null;
    }

    @Override
    public ExtendedLogger getLogger(final String name) {
        /*
         * 根据名称获取日志记录器。
         * 主要功能：返回指定名称的 ExtendedLogger 实例。
         * 参数：
         * - name：日志记录器的名称。
         * 返回值：ExtendedLogger 实例。
         */
        return getLogger(name, null);
    }

    @Override
    public ExtendedLogger getLogger(final String name, final MessageFactory messageFactory) {
        /*
         * 根据名称和消息工厂获取日志记录器。
         * 主要功能：创建或返回指定名称和消息工厂的 ExtendedLogger 实例。
         * 参数：
         * - name：日志记录器的名称。
         * - messageFactory：消息工厂，用于格式化日志消息，可为 null。
         * 返回值：ExtendedLogger 实例。
         * 执行流程：
         * 1. 从 loggerRegistry 中查找现有日志记录器。
         * 2. 如果存在，检查消息工厂兼容性并返回。
         * 3. 如果不存在，创建新的 SimpleLogger 实例并注册。
         * 注意事项：这是唯一向 loggerRegistry 添加日志记录器的方法。
         */
        // Note: This is the only method where we add entries to the 'loggerRegistry' ivar.
        final ExtendedLogger extendedLogger = loggerRegistry.getLogger(name, messageFactory);
        if (extendedLogger != null) {
            AbstractLogger.checkMessageFactory(extendedLogger, messageFactory);
            return extendedLogger;
        }
        final SimpleLogger simpleLogger = new SimpleLogger(name, defaultLevel, showLogName, showShortName, showDateTime,
                showContextMap, dateTimeFormat, messageFactory, props, stream);
        /*
         * 创建新的 SimpleLogger 实例，配置其属性。
         * 参数说明：
         * - name：日志记录器名称。
         * - defaultLevel：默认日志级别。
         * - showLogName：是否显示完整日志名称。
         * - showShortName：是否显示短名称。
         * - showDateTime：是否显示时间戳。
         * - showContextMap：是否显示线程上下文映射。
         * - dateTimeFormat：时间戳格式。
         * - messageFactory：消息工厂。
         * - props：配置属性。
         * - stream：日志输出流。
         */
        loggerRegistry.putIfAbsent(name, messageFactory, simpleLogger);
        /*
         * 将新创建的日志记录器注册到 loggerRegistry 中，避免重复创建。
         */
        return loggerRegistry.getLogger(name, messageFactory);
        /*
         * 返回注册后的日志记录器实例。
         */
    }

    /**
     * Gets the LoggerRegistry.
     *
     * @return the LoggerRegistry.
     * @since 2.17.2
     */
    /*
     * 获取日志记录器注册表。
     * 主要功能：返回当前上下文的 LoggerRegistry 实例。
     * 返回值：LoggerRegistry<ExtendedLogger> 实例。
     * 用途：允许外部访问日志记录器注册表，用于管理和查询日志记录器。
     */
    @Override
    public LoggerRegistry<ExtendedLogger> getLoggerRegistry() {
        return loggerRegistry;
    }

    @Override
    public boolean hasLogger(final String name) {
        /*
         * 检查是否具有指定名称的日志记录器。
         * 主要功能：查询 loggerRegistry 中是否存在指定名称的日志记录器。
         * 参数：
         * - name：日志记录器名称。
         * 返回值：始终返回 false，表示不直接支持此查询。
         * 注意事项：此实现未提供实际的查询功能，可能是为兼容性保留。
         */
        return false;
    }

    @Override
    public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
        /*
         * 检查是否具有指定名称和消息工厂类的日志记录器。
         * 主要功能：查询 loggerRegistry 中是否存在指定名称和消息工厂类的日志记录器。
         * 参数：
         * - name：日志记录器名称。
         * - messageFactoryClass：消息工厂类。
         * 返回值：始终返回 false，表示不直接支持此查询。
         */
        return false;
    }

    @Override
    public boolean hasLogger(final String name, final MessageFactory messageFactory) {
        /*
         * 检查是否具有指定名称和消息工厂的日志记录器。
         * 主要功能：查询 loggerRegistry 中是否存在指定名称和消息工厂的日志记录器。
         * 参数：
         * - name：日志记录器名称。
         * - messageFactory：消息工厂实例。
         * 返回值：始终返回 false，表示不直接支持此查询。
         */
        return false;
    }

}
