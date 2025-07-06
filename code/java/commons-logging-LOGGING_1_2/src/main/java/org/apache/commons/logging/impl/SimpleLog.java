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

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;

/**
 * Simple implementation of Log that sends all enabled log messages,
 * for all defined loggers, to System.err.  The following system properties
 * are supported to configure the behavior of this logger:
 * <ul>
 * <li><code>org.apache.commons.logging.simplelog.defaultlog</code> -
 *     Default logging detail level for all instances of SimpleLog.
 *     Must be one of ("trace", "debug", "info", "warn", "error", or "fatal").
 *     If not specified, defaults to "info". </li>
 * <li><code>org.apache.commons.logging.simplelog.log.xxxxx</code> -
 *     Logging detail level for a SimpleLog instance named "xxxxx".
 *     Must be one of ("trace", "debug", "info", "warn", "error", or "fatal").
 *     If not specified, the default logging detail level is used.</li>
 * <li><code>org.apache.commons.logging.simplelog.showlogname</code> -
 *     Set to <code>true</code> if you want the Log instance name to be
 *     included in output messages. Defaults to <code>false</code>.</li>
 * <li><code>org.apache.commons.logging.simplelog.showShortLogname</code> -
 *     Set to <code>true</code> if you want the last component of the name to be
 *     included in output messages. Defaults to <code>true</code>.</li>
 * <li><code>org.apache.commons.logging.simplelog.showdatetime</code> -
 *     Set to <code>true</code> if you want the current date and time
 *     to be included in output messages. Default is <code>false</code>.</li>
 * <li><code>org.apache.commons.logging.simplelog.dateTimeFormat</code> -
 *     The date and time format to be used in the output messages.
 *     The pattern describing the date and time format is the same that is
 *     used in <code>java.text.SimpleDateFormat</code>. If the format is not
 *     specified or is invalid, the default format is used.
 *     The default format is <code>yyyy/MM/dd HH:mm:ss:SSS zzz</code>.</li>
 * </ul>
 * <p>
 * In addition to looking for system properties with the names specified
 * above, this implementation also checks for a class loader resource named
 * <code>"simplelog.properties"</code>, and includes any matching definitions
 * from this resource (if it exists).
 *
 * @version $Id$
 */
// 中文注释：SimpleLog 是一个简单的日志实现类，将所有启用的日志消息输出到 System.err。
// 支持通过系统属性配置日志行为，包括默认日志级别、特定日志实例的级别、是否显示日志名称、短名称、日期时间以及日期时间格式。
// 此外，还会检查名为 "simplelog.properties" 的类加载器资源文件，加载其中的配置。
// 重要配置参数包括：默认日志级别（defaultlog）、特定日志级别（log.xxxxx）、显示日志名称（showlogname）、短名称（showShortLogname）、日期时间（showdatetime）和日期格式（dateTimeFormat）。
public class SimpleLog implements Log, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 136942970684951178L;
    // 中文注释：定义序列化版本标识，用于序列化兼容性。

    // ------------------------------------------------------- Class Attributes

    /** All system properties used by <code>SimpleLog</code> start with this */
    static protected final String systemPrefix = "org.apache.commons.logging.simplelog.";
    // 中文注释：定义 SimpleLog 使用的系统属性的前缀，统一以 "org.apache.commons.logging.simplelog." 开头。

    /** Properties loaded from simplelog.properties */
    static protected final Properties simpleLogProps = new Properties();
    // 中文注释：存储从 simplelog.properties 文件加载的配置属性。

    /** The default format to use when formating dates */
    static protected final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
    // 中文注释：定义默认的日期时间格式，用于日志消息中的时间戳，格式为 "年/月/日 时:分:秒:毫秒 时区"。

    /** Include the instance name in the log message? */
    static volatile protected boolean showLogName = false;
    // 中文注释：控制是否在日志消息中包含日志实例的完整名称，默认为 false。

    /** Include the short name ( last component ) of the logger in the log
     *  message. Defaults to true - otherwise we'll be lost in a flood of
     *  messages without knowing who sends them.
     */
    static volatile protected boolean showShortName = true;
    // 中文注释：控制是否在日志消息中包含日志实例的短名称（最后一个部分），默认为 true，以区分消息来源。

    /** Include the current time in the log message */
    static volatile protected boolean showDateTime = false;
    // 中文注释：控制是否在日志消息中包含当前日期和时间，默认为 false。

    /** The date and time format to use in the log message */
    static volatile protected String dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;
    // 中文注释：定义日志消息中使用的日期时间格式，默认使用 DEFAULT_DATE_TIME_FORMAT。

    /**
     * Used to format times.
     * <p>
     * Any code that accesses this object should first obtain a lock on it,
     * ie use synchronized(dateFormatter); this requirement was introduced
     * in 1.1.1 to fix an existing thread safety bug (SimpleDateFormat.format
     * is not thread-safe).
     */
    static protected DateFormat dateFormatter = null;
    // 中文注释：用于格式化日期时间的对象。由于 SimpleDateFormat.format 不是线程安全的，访问时需要加锁（synchronized）。
    // 注意事项：为修复线程安全问题，访问 dateFormatter 时必须使用 synchronized 块。

    // ---------------------------------------------------- Log Level Constants

    /** "Trace" level logging. */
    public static final int LOG_LEVEL_TRACE  = 1;
    // 中文注释：定义日志级别 "Trace"，值为 1，用于最详细的日志输出。

    /** "Debug" level logging. */
    public static final int LOG_LEVEL_DEBUG  = 2;
    // 中文注释：定义日志级别 "Debug"，值为 2，用于调试信息。

    /** "Info" level logging. */
    public static final int LOG_LEVEL_INFO   = 3;
    // 中文注释：定义日志级别 "Info"，值为 3，用于常规信息。

    /** "Warn" level logging. */
    public static final int LOG_LEVEL_WARN   = 4;
    // 中文注释：定义日志级别 "Warn"，值为 4，用于警告信息。

    /** "Error" level logging. */
    public static final int LOG_LEVEL_ERROR  = 5;
    // 中文注释：定义日志级别 "Error"，值为 5，用于错误信息。

    /** "Fatal" level logging. */
    public static final int LOG_LEVEL_FATAL  = 6;
    // 中文注释：定义日志级别 "Fatal"，值为 6，用于致命错误信息。

    /** Enable all logging levels */
    public static final int LOG_LEVEL_ALL    = LOG_LEVEL_TRACE - 1;
    // 中文注释：定义日志级别 "All"，值为 0，启用所有日志级别。

    /** Enable no logging levels */
    public static final int LOG_LEVEL_OFF    = LOG_LEVEL_FATAL + 1;
    // 中文注释：定义日志级别 "Off"，值为 7，禁用所有日志输出。

    // ------------------------------------------------------------ Initializer

    private static String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            // Ignore
        }
        return prop == null ? simpleLogProps.getProperty(name) : prop;
    }
    // 中文注释：获取指定名称的系统属性或 simplelog.properties 中的属性值。
    // 方法目的：提供统一的方式从系统属性或配置文件中获取字符串配置。
    // 特殊处理：如果因安全限制无法获取系统属性，则忽略并从 simplelog.properties 中获取。

    private static String getStringProperty(String name, String dephault) {
        String prop = getStringProperty(name);
        return prop == null ? dephault : prop;
    }
    // 中文注释：获取指定名称的属性值，若不存在则返回默认值。
    // 方法目的：支持带默认值的属性获取，增强配置的鲁棒性。
    // 参数说明：name - 属性名称；dephault - 默认值。

    private static boolean getBooleanProperty(String name, boolean dephault) {
        String prop = getStringProperty(name);
        return prop == null ? dephault : "true".equalsIgnoreCase(prop);
    }
    // 中文注释：获取布尔类型的属性值，若不存在则返回默认值。
    // 方法目的：将字符串属性转换为布尔值，处理配置中的布尔选项。
    // 参数说明：name - 属性名称；dephault - 默认布尔值。

    // Initialize class attributes.
    // Load properties file, if found.
    // Override with system properties.
    static {
        // Add props from the resource simplelog.properties
        InputStream in = getResourceAsStream("simplelog.properties");
        if(null != in) {
            try {
                simpleLogProps.load(in);
                in.close();
            } catch(java.io.IOException e) {
                // ignored
            }
        }
        // 中文注释：初始化类属性，加载 simplelog.properties 文件（若存在），并以系统属性覆盖。
        // 关键步骤：1. 尝试加载 simplelog.properties 文件；2. 关闭输入流；3. 忽略 IO 异常。
        // 注意事项：加载失败时不会抛出异常，仅忽略。

        showLogName = getBooleanProperty(systemPrefix + "showlogname", showLogName);
        showShortName = getBooleanProperty(systemPrefix + "showShortLogname", showShortName);
        showDateTime = getBooleanProperty(systemPrefix + "showdatetime", showDateTime);
        // 中文注释：从系统属性或配置文件中获取日志显示相关配置，覆盖默认值。
        // 重要配置参数：showlogname - 是否显示完整日志名称；showShortLogname - 是否显示短名称；showdatetime - 是否显示日期时间。

        if(showDateTime) {
            dateTimeFormat = getStringProperty(systemPrefix + "dateTimeFormat",
                                               dateTimeFormat);
            try {
                dateFormatter = new SimpleDateFormat(dateTimeFormat);
            } catch(IllegalArgumentException e) {
                // If the format pattern is invalid - use the default format
                dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;
                dateFormatter = new SimpleDateFormat(dateTimeFormat);
            }
        }
        // 中文注释：如果启用了日期时间显示，配置日期格式并初始化格式化器。
        // 关键步骤：1. 获取日期时间格式；2. 尝试创建 SimpleDateFormat；3. 若格式无效，使用默认格式。
        // 注意事项：处理非法格式异常，使用默认格式作为回退。
    }

    // ------------------------------------------------------------- Attributes

    /** The name of this simple log instance */
    protected volatile String logName = null;
    // 中文注释：当前日志实例的名称，用于标识日志来源。

    /** The current log level */
    protected volatile int currentLogLevel;
    // 中文注释：当前日志级别，决定哪些日志消息会被输出。

    /** The short name of this simple log instance */
    private volatile String shortLogName = null;
    // 中文注释：日志实例的短名称，仅包含名称的最后一个部分。

    // ------------------------------------------------------------ Constructor

    /**
     * Construct a simple log with given name.
     *
     * @param name log name
     */
    public SimpleLog(String name) {
        logName = name;
        // 中文注释：构造函数，初始化日志实例的名称。
        // 参数说明：name - 日志实例的名称。

        // Set initial log level
        // Used to be: set default log level to ERROR
        // IMHO it should be lower, but at least info ( costin ).
        setLevel(SimpleLog.LOG_LEVEL_INFO);
        // 中文注释：设置初始日志级别为 INFO。
        // 注意事项：初始级别原为 ERROR，后改为 INFO 以提供更详细的日志。

        // Set log level from properties
        String lvl = getStringProperty(systemPrefix + "log." + logName);
        int i = String.valueOf(name).lastIndexOf(".");
        while(null == lvl && i > -1) {
            name = name.substring(0,i);
            lvl = getStringProperty(systemPrefix + "log." + name);
            i = String.valueOf(name).lastIndexOf(".");
        }
        // 中文注释：根据属性文件或系统属性设置日志级别，优先使用特定日志名称的级别。
        // 关键步骤：1. 尝试获取特定日志级别的属性；2. 若未找到，逐级截短名称查找；3. 若仍未找到，使用默认级别。
        // 参数说明：logName - 日志实例名称，用于构造属性键。

        if(null == lvl) {
            lvl =  getStringProperty(systemPrefix + "defaultlog");
        }
        // 中文注释：如果未找到特定日志级别的属性，使用默认日志级别。

        if("all".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_ALL);
        } else if("trace".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_TRACE);
        } else if("debug".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_DEBUG);
        } else if("info".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_INFO);
        } else if("warn".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_WARN);
        } else if("error".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_ERROR);
        } else if("fatal".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_FATAL);
        } else if("off".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_OFF);
        }
        // 中文注释：根据属性值设置日志级别，支持 "all", "trace", "debug", "info", "warn", "error", "fatal", "off"。
        // 交互逻辑：将字符串级别转换为对应的整数常量，设置当前日志级别。
    }

    // -------------------------------------------------------- Properties

    /**
     * Set logging level.
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(int currentLogLevel) {
        this.currentLogLevel = currentLogLevel;
    }
    // 中文注释：设置日志级别。
    // 方法目的：允许动态更改日志实例的日志级别。
    // 参数说明：currentLogLevel - 新的日志级别值。

    /**
     * Get logging level.
     */
    public int getLevel() {
        return currentLogLevel;
    }
    // 中文注释：获取当前日志级别。
    // 方法目的：返回当前日志级别的整数值。

    // -------------------------------------------------------- Logging Methods

    /**
     * Do the actual logging.
     * <p>
     * This method assembles the message and then calls <code>write()</code>
     * to cause it to be written.
     *
     * @param type One of the LOG_LEVEL_XXX constants defining the log level
     * @param message The message itself (typically a String)
     * @param t The exception whose stack trace should be logged
     */
    protected void log(int type, Object message, Throwable t) {
        // Use a string buffer for better performance
        final StringBuffer buf = new StringBuffer();
        // 中文注释：执行实际的日志记录操作，组装日志消息并调用 write 方法输出。
        // 参数说明：type - 日志级别；message - 日志消息；t - 异常对象（可能为 null）。
        // 关键变量：buf - 用于高效构建日志消息的 StringBuffer。

        // Append date-time if so configured
        if(showDateTime) {
            final Date now = new Date();
            String dateText;
            synchronized(dateFormatter) {
                dateText = dateFormatter.format(now);
            }
            buf.append(dateText);
            buf.append(" ");
        }
        // 中文注释：如果配置了显示日期时间，将当前时间格式化后添加到日志消息中。
        // 关键步骤：1. 获取当前时间；2. 使用 dateFormatter 格式化时间；3. 添加到缓冲区。
        // 注意事项：格式化操作需加锁以确保线程安全。

        // Append a readable representation of the log level
        switch(type) {
            case SimpleLog.LOG_LEVEL_TRACE: buf.append("[TRACE] "); break;
            case SimpleLog.LOG_LEVEL_DEBUG: buf.append("[DEBUG] "); break;
            case SimpleLog.LOG_LEVEL_INFO:  buf.append("[INFO] ");  break;
            case SimpleLog.LOG_LEVEL_WARN:  buf.append("[WARN] ");  break;
            case SimpleLog.LOG_LEVEL_ERROR: buf.append("[ERROR] "); break;
            case SimpleLog.LOG_LEVEL_FATAL: buf.append("[FATAL] "); break;
        }
        // 中文注释：根据日志级别添加对应的字符串表示（如 [TRACE]、[DEBUG] 等）。
        // 交互逻辑：将日志级别的字符串前缀添加到消息缓冲区。

        // Append the name of the log instance if so configured
        if(showShortName) {
            if(shortLogName == null) {
                // Cut all but the last component of the name for both styles
                final String slName = logName.substring(logName.lastIndexOf(".") + 1);
                shortLogName = slName.substring(slName.lastIndexOf("/") + 1);
            }
            buf.append(String.valueOf(shortLogName)).append(" - ");
        } else if(showLogName) {
            buf.append(String.valueOf(logName)).append(" - ");
        }
        // 中文注释：根据配置添加日志实例的短名称或完整名称。
        // 关键步骤：1. 如果启用短名称且未初始化，提取短名称；2. 将名称添加到缓冲区。
        // 注意事项：短名称仅包含最后一部分，减少日志冗长。

        // Append the message
        buf.append(String.valueOf(message));
        // 中文注释：将日志消息添加到缓冲区。

        // Append stack trace if not null
        if(t != null) {
            buf.append(" <");
            buf.append(t.toString());
            buf.append(">");

            final java.io.StringWriter sw = new java.io.StringWriter(1024);
            final java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        // 中文注释：如果存在异常对象，将其字符串表示和堆栈跟踪添加到日志消息。
        // 关键步骤：1. 添加异常描述；2. 使用 StringWriter 和 PrintWriter 生成堆栈跟踪。

        // Print to the appropriate destination
        write(buf);
        // 中文注释：将组装好的日志消息输出到目标（默认是 System.err）。
    }

    /**
     * Write the content of the message accumulated in the specified
     * <code>StringBuffer</code> to the appropriate output destination.  The
     * default implementation writes to <code>System.err</code>.
     *
     * @param buffer A <code>StringBuffer</code> containing the accumulated
     *  text to be logged
     */
    protected void write(StringBuffer buffer) {
        System.err.println(buffer.toString());
    }
    // 中文注释：将日志消息输出到指定目标，默认输出到 System.err。
    // 方法目的：将缓冲区中的日志消息打印出来。
    // 参数说明：buffer - 包含日志消息的 StringBuffer。

    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     */
    protected boolean isLevelEnabled(int logLevel) {
        // log level are numerically ordered so can use simple numeric
        // comparison
        return logLevel >= currentLogLevel;
    }
    // 中文注释：检查指定日志级别是否启用。
    // 方法目的：确定是否需要记录某个级别的日志消息。
    // 参数说明：logLevel - 要检查的日志级别。
    // 交互逻辑：通过比较日志级别与当前级别，判断是否启用。

    // -------------------------------------------------------- Log Implementation

    /**
     * Logs a message with
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_DEBUG</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#debug(Object)
     */
    public final void debug(Object message) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_DEBUG)) {
            log(SimpleLog.LOG_LEVEL_DEBUG, message, null);
        }
    }
    // 中文注释：记录 DEBUG 级别的日志消息。
    // 方法目的：如果 DEBUG 级别启用，调用 log 方法记录消息。
    // 参数说明：message - 要记录的日志消息。

    /**
     * Logs a message with
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_DEBUG</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    public final void debug(Object message, Throwable t) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_DEBUG)) {
            log(SimpleLog.LOG_LEVEL_DEBUG, message, t);
        }
    }
    // 中文注释：记录带有异常的 DEBUG 级别的日志消息。
    // 方法目的：如果 DEBUG 级别启用，调用 log 方法记录消息和异常。
    // 参数说明：message - 日志消息；t - 异常对象。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_TRACE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#trace(Object)
     */
    public final void trace(Object message) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_TRACE)) {
            log(SimpleLog.LOG_LEVEL_TRACE, message, null);
        }
    }
    // 中文注释：记录 TRACE 级别的日志消息。
    // 方法目的：如果 TRACE 级别启用，调用 log 方法记录消息。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_TRACE</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    public final void trace(Object message, Throwable t) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_TRACE)) {
            log(SimpleLog.LOG_LEVEL_TRACE, message, t);
        }
    }
    // 中文注释：记录带有异常的 TRACE 级别的日志消息。
    // 方法目的：如果 TRACE 级别启用，调用 log 方法记录消息和异常。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_INFO</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#info(Object)
     */
    public final void info(Object message) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_INFO)) {
            log(SimpleLog.LOG_LEVEL_INFO,message,null);
        }
    }
    // 中文注释：记录 INFO 级别的日志消息。
    // 方法目的：如果 INFO 级别启用，调用 log 方法记录消息。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_INFO</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    public final void info(Object message, Throwable t) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_INFO)) {
            log(SimpleLog.LOG_LEVEL_INFO, message, t);
        }
    }
    // 中文注释：记录带有异常的 INFO 级别的日志消息。
    // 方法目的：如果 INFO 级别启用，调用 log 方法记录消息和异常。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_WARN</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#warn(Object)
     */
    public final void warn(Object message) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_WARN)) {
            log(SimpleLog.LOG_LEVEL_WARN, message, null);
        }
    }
    // 中文注释：记录 WARN 级别的日志消息。
    // 方法目的：如果 WARN 级别启用，调用 log 方法记录消息。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_WARN</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    public final void warn(Object message, Throwable t) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_WARN)) {
            log(SimpleLog.LOG_LEVEL_WARN, message, t);
        }
    }
    // 中文注释：记录带有异常的 WARN 级别的日志消息。
    // 方法目的：如果 WARN 级别启用，调用 log 方法记录消息和异常。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_ERROR</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#error(Object)
     */
    public final void error(Object message) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_ERROR)) {
            log(SimpleLog.LOG_LEVEL_ERROR, message, null);
        }
    }
    // 中文注释：记录 ERROR 级别的日志消息。
    // 方法目的：如果 ERROR 级别启用，调用 log 方法记录消息。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_ERROR</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    public final void error(Object message, Throwable t) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_ERROR)) {
            log(SimpleLog.LOG_LEVEL_ERROR, message, t);
        }
    }
    // 中文注释：记录带有异常的 ERROR 级别的日志消息。
    // 方法目的：如果 ERROR 级别启用，调用 log 方法记录消息和异常。

    /**
     * Log a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_FATAL</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#fatal(Object)
     */
    public final void fatal(Object message) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_FATAL)) {
            log(SimpleLog.LOG_LEVEL_FATAL, message, null);
        }
    }
    // 中文注释：记录 FATAL 级别的日志消息。
    // 方法目的：如果 FATAL 级别启用，调用 log 方法记录消息。

    /**
     * Logs a message with <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_FATAL</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    public final void fatal(Object message, Throwable t) {
        if (isLevelEnabled(SimpleLog.LOG_LEVEL_FATAL)) {
            log(SimpleLog.LOG_LEVEL_FATAL, message, t);
        }
    }
    // 中文注释：记录带有异常的 FATAL 级别的日志消息。
    // 方法目的：如果 FATAL 级别启用，调用 log 方法记录消息和异常。

    /**
     * Are debug messages currently enabled?
     * <p>
     * This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger.
     */
    public final boolean isDebugEnabled() {
        return isLevelEnabled(SimpleLog.LOG_LEVEL_DEBUG);
    }
    // 中文注释：检查 DEBUG 级别是否启用。
    // 方法目的：避免在日志未启用时执行昂贵的操作（如字符串拼接）。

    /**
     * Are error messages currently enabled?
     * <p>
     * This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger.
     */
    public final boolean isErrorEnabled() {
        return isLevelEnabled(SimpleLog.LOG_LEVEL_ERROR);
    }
    // 中文注释：检查 ERROR 级别是否启用。
    // 方法目的：避免在日志未启用时执行昂贵的操作。

    /**
     * Are fatal messages currently enabled?
     * <p>
     * This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger.
     */
    public final boolean isFatalEnabled() {
        return isLevelEnabled(SimpleLog.LOG_LEVEL_FATAL);
    }
    // 中文注释：检查 FATAL 级别是否启用。
    // 方法目的：避免在日志未启用时执行昂贵的操作。

    /**
     * Are info messages currently enabled?
     * <p>
     * This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger.
     */
    public final boolean isInfoEnabled() {
        return isLevelEnabled(SimpleLog.LOG_LEVEL_INFO);
    }
    // 中文注释：检查 INFO 级别是否启用。
    // 方法目的：避免在日志未启用时执行昂贵的操作。

    /**
     * Are trace messages currently enabled?
     * <p>
     * This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger.
     */
    public final boolean isTraceEnabled() {
        return isLevelEnabled(SimpleLog.LOG_LEVEL_TRACE);
    }
    // 中文注释：检查 TRACE 级别是否启用。
    // 方法目的：避免在日志未启用时执行昂贵的操作。

    /**
     * Are warn messages currently enabled?
     * <p>
     * This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger.
     */
    public final boolean isWarnEnabled() {
        return isLevelEnabled(SimpleLog.LOG_LEVEL_WARN);
    }
    // 中文注释：检查 WARN 级别是否启用。
    // 方法目的：避免在日志未启用时执行昂贵的操作。

    /**
     * Return the thread context class loader if available.
     * Otherwise return null.
     *
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     *
     * @exception LogConfigurationException if a suitable class loader
     * cannot be identified.
     */
    private static ClassLoader getContextClassLoader() {
        ClassLoader classLoader = null;

        try {
            // Are we running on a JDK 1.2 or later system?
            final Method method = Thread.class.getMethod("getContextClassLoader", (Class[]) null);
            // 中文注释：尝试获取线程上下文类加载器，适用于 JDK 1.2 及以上版本。
            // 关键步骤：通过反射获取 getContextClassLoader 方法。

            // Get the thread context class loader (if there is one)
            try {
                classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Class[]) null);
            } catch (IllegalAccessException e) {
                // ignore
            } catch (InvocationTargetException e) {
                /**
                 * InvocationTargetException is thrown by 'invoke' when
                 * the method being invoked (getContextClassLoader) throws
                 * an exception.
                 *
                 * getContextClassLoader() throws SecurityException when
                 * the context class loader isn't an ancestor of the
                 * calling class's class loader, or if security
                 * permissions are restricted.
                 *
                 * In the first case (not related), we want to ignore and
                 * keep going.  We cannot help but also ignore the second
                 * with the logic below, but other calls elsewhere (to
                 * obtain a class loader) will trigger this exception where
                 * we can make a distinction.
                 */
                if (e.getTargetException() instanceof SecurityException) {
                    // ignore
                } else {
                    // Capture 'e.getTargetException()' exception for details
                    // alternate: log 'e.getTargetException()', and pass back 'e'.
                    throw new LogConfigurationException
                        ("Unexpected InvocationTargetException", e.getTargetException());
                }
            }
            // 中文注释：调用 getContextClassLoader 方法获取类加载器。
            // 事件处理逻辑：处理 IllegalAccessException 和 InvocationTargetException。
            // 注意事项：如果因安全限制抛出 SecurityException，则忽略；其他异常抛出 LogConfigurationException。
        } catch (NoSuchMethodException e) {
            // Assume we are running on JDK 1.1
            // ignore
            // 中文注释：如果方法不存在（JDK 1.1），忽略异常。
        }

        if (classLoader == null) {
            classLoader = SimpleLog.class.getClassLoader();
        }
        // 中文注释：如果未获取到类加载器，使用 SimpleLog 类的类加载器作为回退。

        // Return the selected class loader
        return classLoader;
        // 中文注释：返回选定的类加载器。
    }

    private static InputStream getResourceAsStream(final String name) {
        return (InputStream)AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    ClassLoader threadCL = getContextClassLoader();

                    if (threadCL != null) {
                        return threadCL.getResourceAsStream(name);
                    } else {
                        return ClassLoader.getSystemResourceAsStream(name);
                    }
                }
            });
    }
    // 中文注释：以特权模式获取指定资源的输入流。
    // 方法目的：安全地加载资源文件（如 simplelog.properties）。
    // 关键步骤：1. 获取线程上下文类加载器；2. 尝试加载资源；3. 若失败，使用系统类加载器。
    // 注意事项：使用 AccessController.doPrivileged 提升权限以规避安全限制。
}

