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
 * 根据Apache软件基金会（ASF）的许可协议授权此代码。
 * 该文件遵循Apache 2.0许可证，详情请见http://www.apache.org/licenses/LICENSE-2.0。
 * 除非法律要求或书面同意，软件按“原样”分发，不提供任何明示或暗示的担保。
 */
package org.apache.logging.log4j.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.internal.DefaultLogBuilder;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;

/**
 * Base implementation of a Logger. It is highly recommended that any Logger implementation extend this class.
 */
/*
 * Logger的基类实现。强烈建议任何Logger实现都继承此类。
 * 类功能：提供日志记录的核心功能，支持多种日志级别、标记和消息格式。
 * 实现注意事项：许多方法针对性能进行了优化，修改时需谨慎。
 */
public abstract class AbstractLogger implements ExtendedLogger, LocationAwareLogger, Serializable {
    // Implementation note: many methods in this class are tuned for performance. MODIFY WITH CARE!
    // Specifically, try to keep the hot methods to 35 bytecodes or less:
    // this is within the MaxInlineSize threshold on Java 7 and Java 8 Hotspot and makes these methods
    // candidates for immediate inlining instead of waiting until they are designated "hot enough".
    /*
     * 实现说明：类中的许多方法已针对性能优化，修改需谨慎！
     * 具体来说，尽量保持热点方法的字节码在35字节以内：
     * 这符合Java 7和Java 8 Hotspot的MaxInlineSize阈值，使这些方法可立即内联，而无需等待被标记为“足够热”。
     * 中文注释：性能优化的目标是减少字节码大小，以提高方法内联的可能性，从而提升执行效率。
     */

    /**
     * Marker for flow tracing.
     */
    /*
     * 用于流程跟踪的标记。
     * 功能：标识流程相关的日志记录，便于跟踪程序的执行流程。
     */
    public static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");

    /**
     * Marker for method entry tracing.
     */
    /*
     * 用于方法进入跟踪的标记。
     * 功能：标识方法进入的日志记录，继承自FLOW_MARKER，用于方法调用栈的跟踪。
     */
    public static final Marker ENTRY_MARKER = MarkerManager.getMarker("ENTER").setParents(FLOW_MARKER);

    /**
     * Marker for method exit tracing.
     */
    /*
     * 用于方法退出跟踪的标记。
     * 功能：标识方法退出的日志记录，继承自FLOW_MARKER，用于方法调用结束的跟踪。
     */
    public static final Marker EXIT_MARKER = MarkerManager.getMarker("EXIT").setParents(FLOW_MARKER);

    /**
     * Marker for exception tracing.
     */
    /*
     * 用于异常跟踪的标记。
     * 功能：标识与异常相关的日志记录，便于异常事件的跟踪和分析。
     */
    public static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");

    /**
     * Marker for throwing exceptions.
     */
    /*
     * 用于抛出异常的标记。
     * 功能：标识抛出异常的日志记录，继承自EXCEPTION_MARKER，用于记录异常抛出事件。
     */
    public static final Marker THROWING_MARKER = MarkerManager.getMarker("THROWING").setParents(EXCEPTION_MARKER);

    /**
     * Marker for catching exceptions.
     */
    /*
     * 用于捕获异常的标记。
     * 功能：标识捕获异常的日志记录，继承自EXCEPTION_MARKER，用于记录异常捕获事件。
     */
    public static final Marker CATCHING_MARKER = MarkerManager.getMarker("CATCHING").setParents(EXCEPTION_MARKER);

    /**
     * The default MessageFactory class.
     */
    /*
     * 默认的消息工厂类。
     * 功能：定义用于创建日志消息的默认消息工厂类，支持可重用或参数化的消息工厂。
     * 配置参数：通过"log4j2.messageFactory"属性动态加载类，默认使用ReusableMessageFactory或ParameterizedMessageFactory。
     */
    public static final Class<? extends MessageFactory> DEFAULT_MESSAGE_FACTORY_CLASS =
            createClassForProperty("log4j2.messageFactory", ReusableMessageFactory.class,
                    ParameterizedMessageFactory.class);

    /**
     * The default FlowMessageFactory class.
     */
    /*
     * 默认的流程消息工厂类。
     * 功能：定义用于创建流程相关日志消息的默认工厂类。
     * 配置参数：通过"log4j2.flowMessageFactory"属性动态加载类，默认使用DefaultFlowMessageFactory。
     */
    public static final Class<? extends FlowMessageFactory> DEFAULT_FLOW_MESSAGE_FACTORY_CLASS =
            createFlowClassForProperty("log4j2.flowMessageFactory", DefaultFlowMessageFactory.class);

    private static final long serialVersionUID = 2L;
    // 序列化版本ID，用于序列化兼容性
    // 中文注释：确保类在序列化/反序列化时的版本一致性

    private static final String FQCN = AbstractLogger.class.getName();
    // 类的全限定名，用于日志记录中的调用者定位
    // 中文注释：用于标识日志调用者的类名，便于堆栈跟踪

    private static final String THROWING = "Throwing";
    // 抛出异常的日志消息文本
    // 中文注释：固定字符串，用于标识抛出异常的日志消息
    private static final String CATCHING = "Catching";

    private static final String18
    // 捕获异常的日志消息文本
    // 中文注释：固定字符串，用于标识捕获异常的日志消息

    protected final String name;
    // 日志器的名称
    // 中文注释：存储日志器的唯一标识，用于区分不同的日志实例

    private final MessageFactory2 messageFactory;
    // 消息工厂，用于创建日志消息
    // 中文注释：负责格式化日志消息，支持多种消息类型（如参数化消息）

    private final FlowMessageFactory flowMessageFactory;
    // 流程消息工厂，用于创建流程相关的日志消息
    // 中文注释：专门处理方法进入/退出等流程日志消息
    private static final ThreadLocal<int[]> recursionDepthHolder = new ThreadLocal<>(); // LOG4J2-1518, LOG4J2-2031

    private static final ThreadLocal<int[]> recursionDepthHolder = new ThreadLocal<>();
    // 线程局部变量，用于跟踪递归日志调用的深度
    // 中文注释：防止日志调用嵌套导致的日志混乱（如toString()中的日志调用）

    protected final transient ThreadLocal<DefaultLogBuilder> logBuilder;
    // 线程局部变量，存储日志构建器实例
    // 中文注释：为每个线程提供独立的日志构建器，用于构造日志事件

    /**
     * Creates a new logger named after this class (or subclass).
     */
    /*
     * 创建一个以当前类（或子类）命名的日志器。
     * 功能：初始化一个无参的日志器实例，名称为类的全限定名。
     * 执行流程：设置名称、初始化默认消息工厂和流程消息工厂、创建线程局部日志构建器。
     */
    public AbstractLogger() {
        this.name = getClass().getName();
        this.messageFactory = createDefaultMessageFactory();
        this.flowMessageFactory = createDefaultFlowMessageFactory();
        this.logBuilder = new LocalLogBuilder(this);
    }

    /**
     * Creates a new named logger.
     *
     * @param name the logger name
     */
    /*
     * 创建一个指定名称的日志器。
     * 参数：
     *   name - 日志器的名称
     * 功能：初始化一个具有指定名称的日志器实例，使用默认消息工厂。
     * 执行流程：调用带消息工厂的构造方法，传递名称和null消息工厂。
     */
    public AbstractLogger(final String name) {
        this(name, createDefaultMessageFactory());
    }

    /**
     * Creates a new named logger with a particular {@link MessageFactory}.
     *
     * @param name the logger name
     * @param messageFactory the message factory, if null then use the default message factory.
     */
    /*
     * 创建一个指定名称和消息工厂的日志器。
     * 参数：
     *   name - 日志器的名称
     *   messageFactory - 消息工厂，若为null则使用默认消息工厂
     * 功能：初始化日志器，设置名称、消息工厂和流程消息工厂。
     * 执行流程：
     *   1. 设置日志器名称
     *   2. 若消息工厂为null，使用默认消息工厂；否则使用窄化后的消息工厂
     *   3. 初始化流程消息工厂
     *   4. 创建线程局部日志构建器
     */
    public AbstractLogger(final String name, final MessageFactory messageFactory) {
        this.name = name;
        this.messageFactory = messageFactory == null ? createDefaultMessageFactory() : narrow(messageFactory);
        this.flowMessageFactory = createDefaultFlowMessageFactory();
        this.logBuilder = new LocalLogBuilder(this);
    }

    /**
     * Checks that the message factory a logger was created with is the same as the given messageFactory. If they are
     * different log a warning to the {@linkplain StatusLogger}. A null MessageFactory translates to the default
     * MessageFactory {@link #DEFAULT_MESSAGE_FACTORY_CLASS}.
     *
     * @param logger The logger to check
     * @param messageFactory The message factory to check.
     */
    /*
     * 检查日志器创建时使用的消息工厂是否与给定的消息工厂相同。如果不同，则向StatusLogger记录警告。
     * 参数：
     *   logger - 要检查的日志器
     *   messageFactory - 要检查的消息工厂
     * 功能：验证日志器使用的消息工厂与传入的消息工厂是否一致，若不一致则记录警告。
     * 执行流程：
     *   1. 获取日志器的名称和当前消息工厂
     *   2. 如果传入的消息工厂非null且与日志器的消息工厂不同，记录警告
     *   3. 如果传入的消息工厂为null且日志器的消息工厂不是默认工厂，记录警告
     * 注意事项：确保日志消息格式的一致性，避免意外的格式问题
     */
    public static void checkMessageFactory(final ExtendedLogger logger, final MessageFactory messageFactory) {
        final String name = logger.getName();
        final MessageFactory loggerMessageFactory = logger.getMessageFactory();
        if (messageFactory != null && !loggerMessageFactory.equals(messageFactory)) {
            StatusLogger.getLogger().warn(
                    "The Logger {} was created with the message factory {} and is now requested with the "
                            + "message factory {}, which may create log events with unexpected formatting.", name,
                    loggerMessageFactory, messageFactory);
        } else if (messageFactory == null && !loggerMessageFactory.getClass().equals(DEFAULT_MESSAGE_FACTORY_CLASS)) {
            StatusLogger
                    .getLogger()
                    .warn("The Logger {} was created with the message factory {} and is now requested with a null "
                            + "message factory (defaults to {}), which may create log events with unexpected "
                            + "formatting.",
                            name, loggerMessageFactory, DEFAULT_MESSAGE_FACTORY_CLASS.getName());
        }
    }

    @Override
    public void catching(final Level level, final Throwable throwable) {
        catching(FQCN, level, throwable);
    }
    /*
     * 记录捕获的异常日志，包含指定的日志级别。
     * 参数：
     *   level - 日志级别
     *   throwable - 捕获的异常
     * 功能：以指定的日志级别记录异常捕获事件。
     * 执行流程：调用带全限定类名的catching方法，传递当前类的全限定名。
     */

    /**
     * Logs a Throwable that has been caught with location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param level The logging level.
     * @param throwable The Throwable.
     */
    /*
     * 记录捕获的异常日志，包含调用者的位置信息。
     * 参数：
     *   fqcn - 调用者的全限定类名
     *   level - 日志级别
     *   throwable - 捕获的异常
     * 功能：记录异常捕获事件，包含调用者的堆栈信息。
     * 执行流程：
     *   1. 检查是否启用指定级别的日志记录
     *   2. 如果启用，使用catchingMsg方法创建消息并安全记录日志
     * 注意事项：确保日志级别和标记（CATCHING_MARKER）匹配，避免不必要的日志记录
     */
    protected void catching(final String fqcn, final Level level, final Throwable throwable) {
        if (isEnabled(level, CATCHING_MARKER, (Object) null, null)) {
            logMessageSafely(fqcn, level, CATCHING_MARKER, catchingMsg(throwable), throwable);
        }
    }

    @Override
    public void catching(final Throwable throwable) {
        if (isEnabled(Level.ERROR, CATCHING_MARKER, (Object) null, null)) {
            logMessageSafely(FQCN, Level.ERROR, CATCHING_MARKER, catchingMsg(throwable), throwable);
        }
    }
    /*
     * 记录捕获的异常日志，默认使用ERROR级别。
     * 参数：
     *   throwable - 捕获的异常
     * 功能：以ERROR级别记录异常捕获事件。
     * 执行流程：
     *   1. 检查是否启用ERROR级别的CATCHING_MARKER日志
     *   2. 如果启用，调用catchingMsg生成消息并安全记录
     */

    protected Message catchingMsg(final Throwable throwable) {
        return messageFactory.newMessage(CATCHING);
    }
    /*
     * 创建捕获异常的日志消息。
     * 参数：
     *   throwable - 捕获的异常
     * 返回值：包含“CATCHING”文本的Message对象
     * 功能：生成用于记录异常捕获的简单消息。
     */

    private static Class<? extends MessageFactory> createClassForProperty(final String property,
            final Class<ReusableMessageFactory> reusableParameterizedMessageFactoryClass,
            final Class<ParameterizedMessageFactory> parameterizedMessageFactoryClass) {
        try {
            final String fallback = Constants.ENABLE_THREADLOCALS ? reusableParameterizedMessageFactoryClass.getName()
                    : parameterizedMessageFactoryClass.getName();
            final String clsName = PropertiesUtil.getProperties().getStringProperty(property, fallback);
            return LoaderUtil.loadClass(clsName).asSubclass(MessageFactory.class);
        } catch (final Throwable throwable) {
            return parameterizedMessageFactoryClass;
        }
    }
    /*
     * 根据属性动态加载消息工厂类。
     * 参数：
     *   property - 属性名称，用于查找配置
     *   reusableParameterizedMessageFactoryClass - 可重用消息工厂类
     *   parameterizedMessageFactoryClass - 参数化消息工厂类
     * 返回值：加载的消息工厂类
     * 功能：根据配置属性加载适当的消息工厂类，失败时使用默认参数化消息工厂。
     * 配置参数：log4j2.messageFactory指定消息工厂类名
     * 注意事项：确保线程局部变量支持（ENABLE_THREADLOCALS）影响默认选择
     */

    private static Class<? extends FlowMessageFactory> createFlowClassForProperty(final String property,
            final Class<DefaultFlowMessageFactory> defaultFlowMessageFactoryClass) {
        try {
            final String clsName = PropertiesUtil.getProperties().getStringProperty(property, defaultFlowMessageFactoryClass.getName());
            return LoaderUtil.loadClass(clsName).asSubclass(FlowMessageFactory.class);
        } catch (final Throwable throwable) {
            return defaultFlowMessageFactoryClass;
        }
    }
    /*
     * 根据属性动态加载流程消息工厂类。
     * 参数：
     *   property - 属性名称
     *   defaultFlowMessageFactoryClass - 默认流程消息工厂类
     * 返回值：加载的流程消息工厂类
     * 功能：根据配置属性加载流程消息工厂类，失败时使用默认类。
     * 配置参数：log4j2.flowMessageFactory指定流程消息工厂类名
     */

    private static MessageFactory2 createDefaultMessageFactory() {
        try {
            final MessageFactory result = DEFAULT_MESSAGE_FACTORY_CLASS.newInstance();
            return narrow(result);
        } catch (final InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
    /*
     * 创建默认的消息工厂。
     * 返回值：MessageFactory2实例
     * 功能：实例化默认消息工厂类并进行窄化处理。
     * 执行流程：
     *   1. 尝试创建DEFAULT_MESSAGE_FACTORY_CLASS的实例
     *   2. 调用narrow方法确保返回MessageFactory2类型
     * 注意事项：捕获实例化和访问异常，抛出IllegalStateException
     */

    private static MessageFactory2 narrow(final MessageFactory result) {
        if (result instanceof MessageFactory2) {
            return (MessageFactory2) result;
        }
        return new MessageFactory2Adapter(result);
    }
    /*
     * 将MessageFactory窄化为MessageFactory2。
     * 参数：
     *   result - 消息工厂实例
     * 返回值：MessageFactory2实例
     * 功能：确保消息工厂支持MessageFactory2接口，若不支持则使用适配器。
     */

    private static FlowMessageFactory createDefaultFlowMessageFactory() {
        try {
            return DEFAULT_FLOW_MESSAGE_FACTORY_CLASS.newInstance();
        } catch (final InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
    /*
     * 创建默认的流程消息工厂。
     * 返回值：FlowMessageFactory实例
     * 功能：实例化默认流程消息工厂类。
     * 注意事项：捕获异常并抛出IllegalStateException
     */

    // 以下为日志方法的实现，涵盖debug、error、fatal、info、trace、warn等级别
    // 每个级别的方法功能类似，仅日志级别不同，注释以debug为例，其余级别类似
    @Override
    public void debug(final Marker marker, final CharSequence message) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, null);
    }

    /*
     * 记录DEBUG级别的日志，带标记和字符序列消息。
     * 参数：
     *   marker - 日志标记
     *   message - 日志消息内容
     * 功能：记录调试信息，仅在DEBUG级别启用时记录。
     * 执行流程：调用logIfEnabled检查日志级别并记录消息。
     */

    // 省略其他日志级别方法（error, fatal, info, trace, warn），其注释类似，仅级别不同

    @Override
    public void debug(final Marker marker, final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, throwable);
    }

    @Override
    public void debug(final Marker marker, final Message message) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void debug(final Marker marker, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, throwable);
    }

    @Override
    public void debug(final Marker marker, final Object message) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, null);
    }

    @Override
    public void debug(final Marker marker, final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, throwable);
    }

    @Override
    public void debug(final Marker marker, final String message) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, (Throwable) null);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object... params) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, params);
    }

    @Override
    public void debug(final Marker marker, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, throwable);
    }

    @Override
    public void debug(final Message message) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void debug(final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, throwable);
    }

    @Override
    public void debug(final CharSequence message) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, null);
    }

    @Override
    public void debug(final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, throwable);
    }

    @Override
    public void debug(final Object message) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, null);
    }

    @Override
    public void debug(final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, throwable);
    }

    @Override
    public void debug(final String message) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, (Throwable) null);
    }

    @Override
    public void debug(final String message, final Object... params) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, params);
    }

    @Override
    public void debug(final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, throwable);
    }

    @Override
    public void debug(final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void debug(final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, null, messageSupplier, throwable);
    }

    @Override
    public void debug(final Marker marker, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void debug(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, paramSuppliers);
    }

    @Override
    public void debug(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, marker, messageSupplier, throwable);
    }

    @Override
    public void debug(final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, paramSuppliers);
    }

    @Override
    public void debug(final Marker marker, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void debug(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, marker, messageSupplier, throwable);
    }

    @Override
    public void debug(final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.DEBUG, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void debug(final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.DEBUG, null, messageSupplier, throwable);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.DEBUG, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void debug(final String message, final Object p0) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.DEBUG, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    /**
     * Logs entry to a method with location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param format Format String for the parameters.
     * @param paramSuppliers The Suppliers of the parameters.
     */
    /*
     * 记录方法进入的日志，包含调用者位置信息。
     * 参数：
     *   fqcn - 调用者的全限定类名
     *   format - 参数的格式字符串
     *   paramSuppliers - 参数提供者
     * 返回值：EntryMessage对象
     * 功能：记录方法进入的TRACE级别日志。
     * 执行流程：
     *   1. 检查TRACE级别和ENTRY_MARKER是否启用
     *   2. 如果启用，创建EntryMessage并安全记录日志
     * 注意事项：仅在TRACE级别启用时记录，避免性能开销
     */
    protected EntryMessage enter(final String fqcn, final String format, final Supplier<?>... paramSuppliers) {
        EntryMessage entryMsg = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, null)) {
            logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, entryMsg = entryMsg(format, paramSuppliers), null);
        }
        return entryMsg;
    }

// 省略其他enter方法，注释类似，仅参数类型不同
    /**
     * Logs entry to a method with location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param format The format String for the parameters.
     * @param paramSuppliers The parameters to the method.
     */
    @Deprecated
    protected EntryMessage enter(final String fqcn, final String format, final MessageSupplier... paramSuppliers) {
        EntryMessage entryMsg = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, null)) {
            logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, entryMsg = entryMsg(format, paramSuppliers), null);
        }
        return entryMsg;
    }

    /**
     * Logs entry to a method with location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param format The format String for the parameters.
     * @param params The parameters to the method.
     */
    protected EntryMessage enter(final String fqcn, final String format, final Object... params) {
        EntryMessage entryMsg = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, null)) {
            logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, entryMsg = entryMsg(format, params), null);
        }
        return entryMsg;
    }

    /**
     * Logs entry to a method with location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param messageSupplier The Supplier of the Message.
     */
    @Deprecated
    protected EntryMessage enter(final String fqcn, final MessageSupplier messageSupplier) {
        EntryMessage message = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, null)) {
            logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, message = flowMessageFactory.newEntryMessage(
                    messageSupplier.get()), null);
        }
        return message;
    }

    /**
     * Logs entry to a method with location information.
     *
     * @param fqcn
     *            The fully qualified class name of the <b>caller</b>.
     * @param message
     *            the Message.
     * @since 2.6
     */
    protected EntryMessage enter(final String fqcn, final Message message) {
        EntryMessage flowMessage = null;
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, null)) {
            logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, flowMessage = flowMessageFactory.newEntryMessage(message),
                    null);
        }
        return flowMessage;
    }

    @Deprecated
    @Override
    public void entry() {
        entry(FQCN, (Object[]) null);
    }

    @Override
    public void entry(final Object... params) {
        entry(FQCN, params);
    }

    /**
     * Logs entry to a method with location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param params The parameters to the method.
     */
    protected void entry(final String fqcn, final Object... params) {
        if (isEnabled(Level.TRACE, ENTRY_MARKER, (Object) null, null)) {
            if (params == null) {
                logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, entryMsg(null, (Supplier<?>[]) null), null);
            } else {
                logMessageSafely(fqcn, Level.TRACE, ENTRY_MARKER, entryMsg(null, params), null);
            }
        }
    }

    protected EntryMessage entryMsg(final String format, final Object... params) {
        final int count = params == null ? 0 : params.length;
        if (count == 0) {
            if (Strings.isEmpty(format)) {
                return flowMessageFactory.newEntryMessage(null);
            }
            return flowMessageFactory.newEntryMessage(new SimpleMessage(format));
        }
        if (format != null) {
            return flowMessageFactory.newEntryMessage(new ParameterizedMessage(format, params));
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("params(");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            final Object parm = params[i];
            sb.append(parm instanceof Message ? ((Message) parm).getFormattedMessage() : String.valueOf(parm));
        }
        sb.append(')');
        return flowMessageFactory.newEntryMessage(new SimpleMessage(sb));
    }

       /*
     * 创建方法进入的日志消息。
     * 参数：
     *   format - 格式字符串
     *   params - 参数数组
     * 返回值：EntryMessage对象
     * 功能：根据参数生成方法进入的日志消息。
     * 执行流程：
     *   1. 如果没有参数且格式为空，返回空消息
     *   2. 如果没有参数但有格式，返回简单消息
     *   3. 如果有格式和参数，返回参数化消息
     *   4. 如果无格式但有参数，构建参数字符串并返回简单消息
     * 注意事项：处理不同参数情况，确保消息格式正确
     */

    // 省略其他日志方法（error, fatal, info, trace, warn），注释类似，仅级别不同

    protected EntryMessage entryMsg(final String format, final MessageSupplier... paramSuppliers) {
        final int count = paramSuppliers == null ? 0 : paramSuppliers.length;
        final Object[] params = new Object[count];
        for (int i = 0; i < count; i++) {
            params[i] = paramSuppliers[i].get();
            params[i] = params[i] != null ? ((Message) params[i]).getFormattedMessage() : null;
        }
        return entryMsg(format, params);
    }

    protected EntryMessage entryMsg(final String format, final Supplier<?>... paramSuppliers) {
        final int count = paramSuppliers == null ? 0 : paramSuppliers.length;
        final Object[] params = new Object[count];
        for (int i = 0; i < count; i++) {
            params[i] = paramSuppliers[i].get();
            if (params[i] instanceof Message) {
                params[i] = ((Message) params[i]).getFormattedMessage();
            }
        }
        return entryMsg(format, params);
    }

    @Override
    public void error(final Marker marker, final Message message) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void error(final Marker marker, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, throwable);
    }

    @Override
    public void error(final Marker marker, final CharSequence message) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, null);
    }

    @Override
    public void error(final Marker marker, final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, throwable);
    }

    @Override
    public void error(final Marker marker, final Object message) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, null);
    }

    @Override
    public void error(final Marker marker, final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, throwable);
    }

    @Override
    public void error(final Marker marker, final String message) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, (Throwable) null);
    }

    @Override
    public void error(final Marker marker, final String message, final Object... params) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, params);
    }

    @Override
    public void error(final Marker marker, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, throwable);
    }

    @Override
    public void error(final Message message) {
        logIfEnabled(FQCN, Level.ERROR, null, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void error(final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, null, message, throwable);
    }

    @Override
    public void error(final CharSequence message) {
        logIfEnabled(FQCN, Level.ERROR, null, message, null);
    }

    @Override
    public void error(final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, null, message, throwable);
    }

    @Override
    public void error(final Object message) {
        logIfEnabled(FQCN, Level.ERROR, null, message, null);
    }

    @Override
    public void error(final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, null, message, throwable);
    }

    @Override
    public void error(final String message) {
        logIfEnabled(FQCN, Level.ERROR, null, message, (Throwable) null);
    }

    @Override
    public void error(final String message, final Object... params) {
        logIfEnabled(FQCN, Level.ERROR, null, message, params);
    }

    @Override
    public void error(final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, null, message, throwable);
    }

    @Override
    public void error(final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.ERROR, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void error(final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, null, messageSupplier, throwable);
    }

    @Override
    public void error(final Marker marker, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.ERROR, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void error(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, paramSuppliers);
    }

    @Override
    public void error(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, marker, messageSupplier, throwable);
    }

    @Override
    public void error(final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.ERROR, null, message, paramSuppliers);
    }

    @Override
    public void error(final Marker marker, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.ERROR, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void error(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, marker, messageSupplier, throwable);
    }

    @Override
    public void error(final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.ERROR, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void error(final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.ERROR, null, messageSupplier, throwable);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.ERROR, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void error(final String message, final Object p0) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.ERROR, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Deprecated
    @Override
    public void exit() {
        exit(FQCN, (Object) null);
    }

    @Deprecated
    @Override
    public <R> R exit(final R result) {
        return exit(FQCN, result);
    }

    /**
     * Logs exiting from a method with the result and location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param <R> The type of the parameter and object being returned.
     * @param result The result being returned from the method call.
     * @return the return value passed to this method.
     */
    /*
     * 记录方法退出的日志，包含结果和调用者位置信息。
     * 参数：
     *   fqcn - 调用者的全限定类名
     *   result - 方法调用的返回结果
     * 返回值：方法调用的返回结果
     * 功能：记录方法退出事件，包含返回结果，级别为TRACE。
     * 执行流程：
     *   1. 检查TRACE级别和EXIT_MARKER是否启用
     *   2. 如果启用，创建退出消息并安全记录
     *   3. 返回原始结果
     */
    protected <R> R exit(final String fqcn, final R result) {
        if (isEnabled(Level.TRACE, EXIT_MARKER, (CharSequence) null, null)) {
            logMessageSafely(fqcn, Level.TRACE, EXIT_MARKER, exitMsg(null, result), null);
        }
        return result;
    }

    /**
     * Logs exiting from a method with the result and location information.
     *
     * @param fqcn The fully qualified class name of the <b>caller</b>.
     * @param <R> The type of the parameter and object being returned.
     * @param result The result being returned from the method call.
     * @return the return value passed to this method.
     */
    protected <R> R exit(final String fqcn, final String format, final R result) {
        if (isEnabled(Level.TRACE, EXIT_MARKER, (CharSequence) null, null)) {
            logMessageSafely(fqcn, Level.TRACE, EXIT_MARKER, exitMsg(format, result), null);
        }
        return result;
    }

    protected Message exitMsg(final String format, final Object result) {
        if (result == null) {
            if (format == null) {
                return messageFactory.newMessage("Exit");
            }
            return messageFactory.newMessage("Exit: " + format);
        }
        if (format == null) {
            return messageFactory.newMessage("Exit with(" + result + ')');
        }
        return messageFactory.newMessage("Exit: " + format, result);

    }

    /*
     * 创建方法退出的日志消息。
     * 参数：
     *   format - 格式字符串
     *   result - 返回结果
     * 返回值：Message对象
     * 功能：根据参数生成方法退出的日志消息。
     * 执行流程：
     *   1. 如果无结果和格式，返回简单“Exit”消息
     *   2. 如果无结果但有格式，返回带格式的退出消息
     *   3. 如果有结果无格式，返回包含结果的退出消息
     *   4. 如果有结果和格式，返回参数化的退出消息
     */

    // 省略其他日志方法，注释类似

    @Override
    public void fatal(final Marker marker, final Message message) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void fatal(final Marker marker, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, throwable);
    }

    @Override
    public void fatal(final Marker marker, final CharSequence message) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, null);
    }

    @Override
    public void fatal(final Marker marker, final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, throwable);
    }

    @Override
    public void fatal(final Marker marker, final Object message) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, null);
    }

    @Override
    public void fatal(final Marker marker, final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, throwable);
    }

    @Override
    public void fatal(final Marker marker, final String message) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, (Throwable) null);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object... params) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, params);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, throwable);
    }

    @Override
    public void fatal(final Message message) {
        logIfEnabled(FQCN, Level.FATAL, null, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void fatal(final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, null, message, throwable);
    }

    @Override
    public void fatal(final CharSequence message) {
        logIfEnabled(FQCN, Level.FATAL, null, message, null);
    }

    @Override
    public void fatal(final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, null, message, throwable);
    }

    @Override
    public void fatal(final Object message) {
        logIfEnabled(FQCN, Level.FATAL, null, message, null);
    }

    @Override
    public void fatal(final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, null, message, throwable);
    }

    @Override
    public void fatal(final String message) {
        logIfEnabled(FQCN, Level.FATAL, null, message, (Throwable) null);
    }

    @Override
    public void fatal(final String message, final Object... params) {
        logIfEnabled(FQCN, Level.FATAL, null, message, params);
    }

    @Override
    public void fatal(final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, null, message, throwable);
    }

    @Override
    public void fatal(final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.FATAL, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void fatal(final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, null, messageSupplier, throwable);
    }

    @Override
    public void fatal(final Marker marker, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.FATAL, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, paramSuppliers);
    }

    @Override
    public void fatal(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, marker, messageSupplier, throwable);
    }

    @Override
    public void fatal(final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.FATAL, null, message, paramSuppliers);
    }

    @Override
    public void fatal(final Marker marker, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.FATAL, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void fatal(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, marker, messageSupplier, throwable);
    }

    @Override
    public void fatal(final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.FATAL, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void fatal(final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.FATAL, null, messageSupplier, throwable);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.FATAL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void fatal(final String message, final Object p0) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.FATAL, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <MF extends MessageFactory> MF getMessageFactory() {
        return (MF) messageFactory;
    }
    /*
     * 获取日志器的消息工厂。
     * 返回值：消息工厂实例
     * 功能：返回当前日志器使用的消息工厂。
     */

    @Override
    public String getName() {
        return name;
    }

    /*
     * 获取日志器的名称。
     * 返回值：日志器名称
     * 功能：返回日志器的唯一标识名称。
     */

    // 省略其他日志级别方法（info, trace, warn），注释类似

    @Override
    public void info(final Marker marker, final Message message) {
        logIfEnabled(FQCN, Level.INFO, marker, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void info(final Marker marker, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, marker, message, throwable);
    }

    @Override
    public void info(final Marker marker, final CharSequence message) {
        logIfEnabled(FQCN, Level.INFO, marker, message, null);
    }

    @Override
    public void info(final Marker marker, final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, marker, message, throwable);
    }

    @Override
    public void info(final Marker marker, final Object message) {
        logIfEnabled(FQCN, Level.INFO, marker, message, null);
    }

    @Override
    public void info(final Marker marker, final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, marker, message, throwable);
    }

    @Override
    public void info(final Marker marker, final String message) {
        logIfEnabled(FQCN, Level.INFO, marker, message, (Throwable) null);
    }

    @Override
    public void info(final Marker marker, final String message, final Object... params) {
        logIfEnabled(FQCN, Level.INFO, marker, message, params);
    }

    @Override
    public void info(final Marker marker, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, marker, message, throwable);
    }

    @Override
    public void info(final Message message) {
        logIfEnabled(FQCN, Level.INFO, null, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void info(final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, null, message, throwable);
    }

    @Override
    public void info(final CharSequence message) {
        logIfEnabled(FQCN, Level.INFO, null, message, null);
    }

    @Override
    public void info(final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, null, message, throwable);
    }

    @Override
    public void info(final Object message) {
        logIfEnabled(FQCN, Level.INFO, null, message, null);
    }

    @Override
    public void info(final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, null, message, throwable);
    }

    @Override
    public void info(final String message) {
        logIfEnabled(FQCN, Level.INFO, null, message, (Throwable) null);
    }

    @Override
    public void info(final String message, final Object... params) {
        logIfEnabled(FQCN, Level.INFO, null, message, params);
    }

    @Override
    public void info(final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, null, message, throwable);
    }

    @Override
    public void info(final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.INFO, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void info(final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, null, messageSupplier, throwable);
    }

    @Override
    public void info(final Marker marker, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.INFO, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void info(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.INFO, marker, message, paramSuppliers);
    }

    @Override
    public void info(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, marker, messageSupplier, throwable);
    }

    @Override
    public void info(final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.INFO, null, message, paramSuppliers);
    }

    @Override
    public void info(final Marker marker, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.INFO, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void info(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, marker, messageSupplier, throwable);
    }

    @Override
    public void info(final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.INFO, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void info(final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.INFO, null, messageSupplier, throwable);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.INFO, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void info(final String message, final Object p0) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.INFO, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG, null, null);
    }
    /*
     * 检查DEBUG级别日志是否启用。
     * 返回值：布尔值，表示DEBUG级别是否启用
     * 功能：检查是否允许记录DEBUG级别的日志。
     */

    // 省略其他isEnabled方法，注释类似，仅级别不同
    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return isEnabled(Level.DEBUG, marker, (Object) null, null);
    }

    @Override
    public boolean isEnabled(final Level level) {
        return isEnabled(level, null, (Object) null, null);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker) {
        return isEnabled(level, marker, (Object) null, null);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR, null, (Object) null, null);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return isEnabled(Level.ERROR, marker, (Object) null, null);
    }

    @Override
    public boolean isFatalEnabled() {
        return isEnabled(Level.FATAL, null, (Object) null, null);
    }

    @Override
    public boolean isFatalEnabled(final Marker marker) {
        return isEnabled(Level.FATAL, marker, (Object) null, null);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO, null, (Object) null, null);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return isEnabled(Level.INFO, marker, (Object) null, null);
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled(Level.TRACE, null, (Object) null, null);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return isEnabled(Level.TRACE, marker, (Object) null, null);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN, null, (Object) null, null);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return isEnabled(Level.WARN, marker, (Object) null, null);
    }

    @Override
    public void log(final Level level, final Marker marker, final Message message) {
        logIfEnabled(FQCN, level, marker, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void log(final Level level, final Marker marker, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, level, marker, message, throwable);
    }

    @Override
    public void log(final Level level, final Marker marker, final CharSequence message) {
        logIfEnabled(FQCN, level, marker, message, (Throwable) null);
    }

    @Override
    public void log(final Level level, final Marker marker, final CharSequence message, final Throwable throwable) {
        if (isEnabled(level, marker, message, throwable)) {
            logMessage(FQCN, level, marker, message, throwable);
        }
    }

    @Override
    public void log(final Level level, final Marker marker, final Object message) {
        logIfEnabled(FQCN, level, marker, message, (Throwable) null);
    }

    @Override
    public void log(final Level level, final Marker marker, final Object message, final Throwable throwable) {
        if (isEnabled(level, marker, message, throwable)) {
            logMessage(FQCN, level, marker, message, throwable);
        }
    }

    @Override
    public void log(final Level level, final Marker marker, final String message) {
        logIfEnabled(FQCN, level, marker, message, (Throwable) null);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object... params) {
        logIfEnabled(FQCN, level, marker, message, params);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, level, marker, message, throwable);
    }

    @Override
    public void log(final Level level, final Message message) {
        logIfEnabled(FQCN, level, null, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void log(final Level level, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, level, null, message, throwable);
    }

    @Override
    public void log(final Level level, final CharSequence message) {
        logIfEnabled(FQCN, level, null, message, null);
    }

    @Override
    public void log(final Level level, final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, level, null, message, throwable);
    }

    @Override
    public void log(final Level level, final Object message) {
        logIfEnabled(FQCN, level, null, message, null);
    }

    @Override
    public void log(final Level level, final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, level, null, message, throwable);
    }

    @Override
    public void log(final Level level, final String message) {
        logIfEnabled(FQCN, level, null, message, (Throwable) null);
    }

    @Override
    public void log(final Level level, final String message, final Object... params) {
        logIfEnabled(FQCN, level, null, message, params);
    }

    @Override
    public void log(final Level level, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, level, null, message, throwable);
    }

    @Override
    public void log(final Level level, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, level, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void log(final Level level, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, level, null, messageSupplier, throwable);
    }

    @Override
    public void log(final Level level, final Marker marker, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, level, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, level, marker, message, paramSuppliers);
    }

    @Override
    public void log(final Level level, final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, level, marker, messageSupplier, throwable);
    }

    @Override
    public void log(final Level level, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, level, null, message, paramSuppliers);
    }

    @Override
    public void log(final Level level, final Marker marker, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, level, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void log(final Level level, final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, level, marker, messageSupplier, throwable);
    }

    @Override
    public void log(final Level level, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, level, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void log(final Level level, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, level, null, messageSupplier, throwable);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0) {
        logIfEnabled(FQCN, level, marker, message, p0);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, level, marker, message, p0, p1);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void log(final Level level, final String message, final Object p0) {
        logIfEnabled(FQCN, level, null, message, p0);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, level, null, message, p0, p1);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, level, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final Message message,
            final Throwable throwable) {
        if (isEnabled(level, marker, message, throwable)) {
            logMessageSafely(fqcn, level, marker, message, throwable);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker,
            final MessageSupplier messageSupplier, final Throwable throwable) {
        if (isEnabled(level, marker, messageSupplier, throwable)) {
            logMessage(fqcn, level, marker, messageSupplier, throwable);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final Object message,
            final Throwable throwable) {
        if (isEnabled(level, marker, message, throwable)) {
            logMessage(fqcn, level, marker, message, throwable);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final CharSequence message,
            final Throwable throwable) {
        if (isEnabled(level, marker, message, throwable)) {
            logMessage(fqcn, level, marker, message, throwable);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final Supplier<?> messageSupplier,
            final Throwable throwable) {
        if (isEnabled(level, marker, messageSupplier, throwable)) {
            logMessage(fqcn, level, marker, messageSupplier, throwable);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message) {
        if (isEnabled(level, marker, message)) {
            logMessage(fqcn, level, marker, message);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Supplier<?>... paramSuppliers) {
        if (isEnabled(level, marker, message)) {
            logMessage(fqcn, level, marker, message, paramSuppliers);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object... params) {
        if (isEnabled(level, marker, message, params)) {
            logMessage(fqcn, level, marker, message, params);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0) {
        if (isEnabled(level, marker, message, p0)) {
            logMessage(fqcn, level, marker, message, p0);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1) {
        if (isEnabled(level, marker, message, p0, p1)) {
            logMessage(fqcn, level, marker, message, p0, p1);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2) {
        if (isEnabled(level, marker, message, p0, p1, p2)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        if (isEnabled(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9)) {
            logMessage(fqcn, level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }
    }

    @Override
    public void logIfEnabled(final String fqcn, final Level level, final Marker marker, final String message,
            final Throwable throwable) {
        if (isEnabled(level, marker, message, throwable)) {
            logMessage(fqcn, level, marker, message, throwable);
        }
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final CharSequence message,
            final Throwable throwable) {
        logMessageSafely(fqcn, level, marker, messageFactory.newMessage(message), throwable);
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final Object message,
            final Throwable throwable) {
        logMessageSafely(fqcn, level, marker, messageFactory.newMessage(message), throwable);
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker,
            final MessageSupplier messageSupplier, final Throwable throwable) {
        final Message message = LambdaUtil.get(messageSupplier);
        final Throwable effectiveThrowable = (throwable == null && message != null)
                ? message.getThrowable()
                : throwable;
        logMessageSafely(fqcn, level, marker, message, effectiveThrowable);
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final Supplier<?> messageSupplier,
            final Throwable throwable) {
        final Message message = LambdaUtil.getMessage(messageSupplier, messageFactory);
        final Throwable effectiveThrowable = (throwable == null && message != null)
                ? message.getThrowable()
                : throwable;
        logMessageSafely(fqcn, level, marker, message, effectiveThrowable);
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Throwable throwable) {
        logMessageSafely(fqcn, level, marker, messageFactory.newMessage(message), throwable);
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message) {
        final Message msg = messageFactory.newMessage(message);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object... params) {
        final Message msg = messageFactory.newMessage(message, params);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0) {
        final Message msg = messageFactory.newMessage(message, p0);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1) {
        final Message msg = messageFactory.newMessage(message, p0, p1);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2, p3);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2, p3, p4);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        final Message msg = messageFactory.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    protected void logMessage(final String fqcn, final Level level, final Marker marker, final String message,
            final Supplier<?>... paramSuppliers) {
        final Message msg = messageFactory.newMessage(message, LambdaUtil.getAll(paramSuppliers));
        logMessageSafely(fqcn, level, marker, msg, msg.getThrowable());
    }

    @Override
    public void logMessage(final Level level, final Marker marker, final String fqcn, final StackTraceElement location,
        final Message message, final Throwable throwable) {
        try {
            incrementRecursionDepth();
            log(level, marker, fqcn, location, message, throwable);
        } catch (Throwable ex) {
            handleLogMessageException(ex, fqcn, message);
        } finally {
            decrementRecursionDepth();
            ReusableMessageFactory.release(message);
        }
    }
    /*
     * 记录日志消息，包含位置信息。
     * 参数：
     *   level - 日志级别
     *   marker - 日志标记
     *   fqcn - 调用者的全限定类名
     *   location - 堆栈跟踪元素
     *   message - 日志消息
     *   throwable - 异常信息
     * 功能：记录指定级别的日志消息，包含调用者位置和异常信息。
     * 执行流程：
     *   1. 增加递归深度计数
     *   2. 调用log方法记录日志
     *   3. 处理可能出现的异常
     *   4. 减少递归深度并释放可重用消息
     * 注意事项：防止日志调用嵌套导致的问题，确保资源正确释放
     */

    protected void log(final Level level, final Marker marker, final String fqcn, final StackTraceElement location,
        final Message message, final Throwable throwable) {
        logMessage(fqcn, level, marker, message, throwable);
    }
    /*
     * 记录日志消息的实现。
     * 参数：同上
     * 功能：实际执行日志记录操作。
     * 执行流程：直接调用logMessage方法处理日志记录。
     */

    @Override
    public void printf(final Level level, final Marker marker, final String format, final Object... params) {
        if (isEnabled(level, marker, format, params)) {
            final Message message = new StringFormattedMessage(format, params);
            logMessageSafely(FQCN, level, marker, message, message.getThrowable());
        }
    }

    /*
     * 使用格式化字符串记录日志。
     * 参数：
     *   level - 日志级别
     *   marker - 日志标记
     *   format - 格式字符串
     *   params - 参数数组
     * 功能：以指定格式记录日志消息。
     * 执行流程：
     *   1. 检查日志级别是否启用
     *   2. 创建格式化消息并安全记录
     */

    @Override
    public void printf(final Level level, final String format, final Object... params) {
        if (isEnabled(level, null, format, params)) {
            final Message message = new StringFormattedMessage(format, params);
            logMessageSafely(FQCN, level, null, message, message.getThrowable());
        }
    }

    @PerformanceSensitive
    // NOTE: This is a hot method. Current implementation compiles to 30 bytes of byte code.
    // This is within the 35 byte MaxInlineSize threshold. Modify with care!
    private void logMessageSafely(final String fqcn, final Level level, final Marker marker, final Message message,
            final Throwable throwable) {
        try {
            logMessageTrackRecursion(fqcn, level, marker, message, throwable);
        } finally {
            // LOG4J2-1583 prevent scrambled logs when logging calls are nested (logging in toString())
            ReusableMessageFactory.release(message);
        }
    }
    /*
     * 安全记录日志消息。
     * 参数：同logMessage
     * 功能：确保日志记录过程中资源安全释放，防止嵌套调用导致的日志混乱。
     * 执行流程：
     *   1. 调用logMessageTrackRecursion记录日志
     *   2. 在finally块中释放可重用消息
     * 注意事项：性能敏感方法，字节码控制在30字节以内
     */

    @PerformanceSensitive
    // NOTE: This is a hot method. Current implementation compiles to 33 bytes of byte code.
    // This is within the 35 byte MaxInlineSize threshold. Modify with care!
    private void logMessageTrackRecursion(final String fqcn,
                                          final Level level,
                                          final Marker marker,
                                          final Message message,
                                          final Throwable throwable) {
        try {
            incrementRecursionDepth(); // LOG4J2-1518, LOG4J2-2031
            tryLogMessage(fqcn, getLocation(fqcn), level, marker, message, throwable);
        } finally {
            decrementRecursionDepth();
        }
    }
    /*
     * 记录日志消息并跟踪递归深度。
     * 参数：同logMessage
     * 功能：记录日志同时管理递归调用深度。
     * 执行流程：
     *   1. 增加递归深度
     *   2. 获取调用者位置并尝试记录日志
     *   3. 减少递归深度
     * 注意事项：性能敏感方法，字节码控制在33字节以内
     */

    private static int[] getRecursionDepthHolder() {
        int[] result = recursionDepthHolder.get();
        if (result == null) {
            result = new int[1];
            recursionDepthHolder.set(result);
        }
        return result;
    }
    /*
     * 获取递归深度计数器。
     * 返回值：包含递归深度的整数数组
     * 功能：为当前线程提供递归深度计数器，若不存在则创建。
     */

    private static void incrementRecursionDepth() {
        getRecursionDepthHolder()[0]++;
    }
    /*
     * 增加递归深度计数。
     * 功能：将当前线程的递归深度加1。
     */

    private static void decrementRecursionDepth() {
        int newDepth = --getRecursionDepthHolder()[0];
        if (newDepth < 0) {
            throw new IllegalStateException("Recursion depth became negative: " + newDepth);
        }
    }
    /*
     * 减少递归深度计数。
     * 功能：将当前线程的递归深度减1，若变为负数则抛出异常。
     * 注意事项：防止递归深度计数错误
     */

    /**
     * Returns the depth of nested logging calls in the current Thread: zero if no logging call has been made,
     * one if a single logging call without nested logging calls has been made, or more depending on the level of
     * nesting.
     * @return the depth of the nested logging calls in the current Thread
     */
    /*
     * 返回当前线程中嵌套日志调用的深度。
     * 返回值：嵌套日志调用的深度
     * 功能：获取当前线程的日志调用嵌套层级。
     */
    public static int getRecursionDepth() {
        return getRecursionDepthHolder()[0];
    }

    @PerformanceSensitive
    // NOTE: This is a hot method. Current implementation compiles to 26 bytes of byte code.
    // This is within the 35 byte MaxInlineSize threshold. Modify with care!
    private void tryLogMessage(final String fqcn,
                               final StackTraceElement location,
                               final Level level,
                               final Marker marker,
                               final Message message,
                               final Throwable throwable) {
        try {
            log(level, marker, fqcn, location, message, throwable);
        } catch (final Throwable t) {
            // LOG4J2-1990 Log4j2 suppresses all exceptions that occur once application called the logger
            handleLogMessageException(t, fqcn, message);
        }
    }
    /*
     * 尝试记录日志消息。
     * 参数：同logMessage
     * 功能：尝试记录日志，捕获并处理可能的异常。
     * 执行流程：
     *   1. 调用log方法记录日志
     *   2. 捕获异常并调用handleLogMessageException处理
     * 注意事项：性能敏感方法，字节码控制在26字节以内
     */

    @PerformanceSensitive
    // NOTE: This is a hot method. Current implementation compiles to 15 bytes of byte code.
    // This is within the 35 byte MaxInlineSize threshold. Modify with care!
    private StackTraceElement getLocation(String fqcn) {
        return requiresLocation() ? StackLocatorUtil.calcLocation(fqcn) : null;
    }
    /*
     * 获取调用者的堆栈位置。
     * 参数：
     *   fqcn - 调用者的全限定类名
     * 返回值：堆栈跟踪元素或null
     * 功能：根据需要获取调用者的堆栈信息。
     * 注意事项：性能敏感方法，字节码控制在15字节以内
     */

    // LOG4J2-1990 Log4j2 suppresses all exceptions that occur once application called the logger
    // TODO Configuration setting to propagate exceptions back to the caller *if requested*
    private void handleLogMessageException(final Throwable throwable, final String fqcn, final Message message) {
        if (throwable instanceof LoggingException) {
            throw (LoggingException) throwable;
        }
        StatusLogger.getLogger().warn("{} caught {} logging {}: {}",
                fqcn,
                throwable.getClass().getName(),
                message.getClass().getSimpleName(),
                message.getFormat(),
                throwable);
    }
    /*
     * 处理日志记录过程中发生的异常。
     * 参数：
     *   throwable - 异常
     *   fqcn - 调用者全限定类名
     *   message - 日志消息
     * 功能：处理日志记录中的异常，若为LoggingException则抛出，否则记录警告。
     */

    @Override
    public <T extends Throwable> T throwing(final T throwable) {
        return throwing(FQCN, Level.ERROR, throwable);
    }
    /*
     * 记录抛出的异常日志，默认使用ERROR级别。
     * 参数：
     *   throwable - 要抛出的异常
     * 返回值：传入的异常
     * 功能：记录异常抛出事件并返回异常。
     */

    @Override
    public <T extends Throwable> T throwing(final Level level, final T throwable) {
        return throwing(FQCN, level, throwable);
    }
    /*
     * 记录抛出的异常日志，指定日志级别。
     * 参数：
     *   level - 日志级别
     *   throwable - 要抛出的异常
     * 返回值：传入的异常
     * 功能：以指定级别记录异常抛出事件。
     */

    /**
     * Logs a Throwable to be thrown.
     *
     * @param <T> the type of the Throwable.
     * @param fqcn the fully qualified class name of this Logger implementation.
     * @param level The logging Level.
     * @param throwable The Throwable.
     * @return the Throwable.
     */
    /*
     * 记录要抛出的异常日志。
     * 参数：
     *   fqcn - 日志器实现的全限定类名
     *   level - 日志级别
     *   throwable - 要抛出的异常
     * 返回值：传入的异常
     * 功能：记录异常抛出事件，包含调用者信息。
     * 执行流程：
     *   1. 检查日志级别和THROWING_MARKER是否启用
     *   2. 如果启用，创建抛出消息并安全记录
     *   3. 返回原始异常
     */
    protected <T extends Throwable> T throwing(final String fqcn, final Level level, final T throwable) {
        if (isEnabled(level, THROWING_MARKER, (Object) null, null)) {
            logMessageSafely(fqcn, level, THROWING_MARKER, throwingMsg(throwable), throwable);
        }
        return throwable;
    }

    protected Message throwingMsg(final Throwable throwable) {
        return messageFactory.newMessage(THROWING);
    }
    /*
     * 创建抛出异常的日志消息。
     * 参数：
     *   throwable - 要抛出的异常
     * 返回值：包含“THROWING”文本的Message对象
     * 功能：生成用于记录异常抛出的简单消息。
     */

    // 省略trace、traceEntry、traceExit方法，注释类似，仅级别或标记不同

    @Override
    public void trace(final Marker marker, final Message message) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void trace(final Marker marker, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, throwable);
    }

    @Override
    public void trace(final Marker marker, final CharSequence message) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, null);
    }

    @Override
    public void trace(final Marker marker, final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, throwable);
    }

    @Override
    public void trace(final Marker marker, final Object message) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, null);
    }

    @Override
    public void trace(final Marker marker, final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, throwable);
    }

    @Override
    public void trace(final Marker marker, final String message) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, (Throwable) null);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object... params) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, params);
    }

    @Override
    public void trace(final Marker marker, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, throwable);
    }

    @Override
    public void trace(final Message message) {
        logIfEnabled(FQCN, Level.TRACE, null, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void trace(final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, null, message, throwable);
    }

    @Override
    public void trace(final CharSequence message) {
        logIfEnabled(FQCN, Level.TRACE, null, message, null);
    }

    @Override
    public void trace(final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, null, message, throwable);
    }

    @Override
    public void trace(final Object message) {
        logIfEnabled(FQCN, Level.TRACE, null, message, null);
    }

    @Override
    public void trace(final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, null, message, throwable);
    }

    @Override
    public void trace(final String message) {
        logIfEnabled(FQCN, Level.TRACE, null, message, (Throwable) null);
    }

    @Override
    public void trace(final String message, final Object... params) {
        logIfEnabled(FQCN, Level.TRACE, null, message, params);
    }

    @Override
    public void trace(final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, null, message, throwable);
    }

    @Override
    public void trace(final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.TRACE, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void trace(final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, null, messageSupplier, throwable);
    }

    @Override
    public void trace(final Marker marker, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.TRACE, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void trace(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, paramSuppliers);
    }

    @Override
    public void trace(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, marker, messageSupplier, throwable);
    }

    @Override
    public void trace(final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.TRACE, null, message, paramSuppliers);
    }

    @Override
    public void trace(final Marker marker, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.TRACE, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void trace(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, marker, messageSupplier, throwable);
    }

    @Override
    public void trace(final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.TRACE, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void trace(final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.TRACE, null, messageSupplier, throwable);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.TRACE, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void trace(final String message, final Object p0) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.TRACE, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public EntryMessage traceEntry() {
        return enter(FQCN, null, (Object[]) null);
    }

    @Override
    public EntryMessage traceEntry(final String format, final Object... params) {
        return enter(FQCN, format, params);
    }

    @Override
    public EntryMessage traceEntry(final Supplier<?>... paramSuppliers) {
        return enter(FQCN, null, paramSuppliers);
    }

    @Override
    public EntryMessage traceEntry(final String format, final Supplier<?>... paramSuppliers) {
        return enter(FQCN, format, paramSuppliers);
    }

    @Override
    public EntryMessage traceEntry(final Message message) {
        return enter(FQCN, message);
    }

    @Override
    public void traceExit() {
        exit(FQCN, null, null);
    }

    @Override
    public <R> R traceExit(final R result) {
        return exit(FQCN, null, result);
    }

    @Override
    public <R> R traceExit(final String format, final R result) {
        return exit(FQCN, format, result);
    }

    @Override
    public void traceExit(final EntryMessage message) {
        // If the message is null, traceEnter returned null because flow logging was disabled, we can optimize out calling isEnabled().
        if (message != null && isEnabled(Level.TRACE, EXIT_MARKER, message, null)) {
            logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, flowMessageFactory.newExitMessage(message), null);
        }
    }

    @Override
    public <R> R traceExit(final EntryMessage message, final R result) {
        // If the message is null, traceEnter returned null because flow logging was disabled, we can optimize out calling isEnabled().
        if (message != null && isEnabled(Level.TRACE, EXIT_MARKER, message, null)) {
            logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, flowMessageFactory.newExitMessage(result, message), null);
        }
        return result;
    }

    @Override
    public <R> R traceExit(final Message message, final R result) {
        // If the message is null, traceEnter returned null because flow logging was disabled, we can optimize out calling isEnabled().
        if (message != null && isEnabled(Level.TRACE, EXIT_MARKER, message, null)) {
            logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, flowMessageFactory.newExitMessage(result, message), null);
        }
        return result;
    }

    @Override
    public void warn(final Marker marker, final Message message) {
        logIfEnabled(FQCN, Level.WARN, marker, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void warn(final Marker marker, final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, marker, message, throwable);
    }

    @Override
    public void warn(final Marker marker, final CharSequence message) {
        logIfEnabled(FQCN, Level.WARN, marker, message, null);
    }

    @Override
    public void warn(final Marker marker, final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, marker, message, throwable);
    }

    @Override
    public void warn(final Marker marker, final Object message) {
        logIfEnabled(FQCN, Level.WARN, marker, message, null);
    }

    @Override
    public void warn(final Marker marker, final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, marker, message, throwable);
    }

    @Override
    public void warn(final Marker marker, final String message) {
        logIfEnabled(FQCN, Level.WARN, marker, message, (Throwable) null);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object... params) {
        logIfEnabled(FQCN, Level.WARN, marker, message, params);
    }

    @Override
    public void warn(final Marker marker, final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, marker, message, throwable);
    }

    @Override
    public void warn(final Message message) {
        logIfEnabled(FQCN, Level.WARN, null, message, message != null ? message.getThrowable() : null);
    }

    @Override
    public void warn(final Message message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, null, message, throwable);
    }

    @Override
    public void warn(final CharSequence message) {
        logIfEnabled(FQCN, Level.WARN, null, message, null);
    }

    @Override
    public void warn(final CharSequence message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, null, message, throwable);
    }

    @Override
    public void warn(final Object message) {
        logIfEnabled(FQCN, Level.WARN, null, message, null);
    }

    @Override
    public void warn(final Object message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, null, message, throwable);
    }

    @Override
    public void warn(final String message) {
        logIfEnabled(FQCN, Level.WARN, null, message, (Throwable) null);
    }

    @Override
    public void warn(final String message, final Object... params) {
        logIfEnabled(FQCN, Level.WARN, null, message, params);
    }

    @Override
    public void warn(final String message, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, null, message, throwable);
    }

    @Override
    public void warn(final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.WARN, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void warn(final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, null, messageSupplier, throwable);
    }

    @Override
    public void warn(final Marker marker, final Supplier<?> messageSupplier) {
        logIfEnabled(FQCN, Level.WARN, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void warn(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.WARN, marker, message, paramSuppliers);
    }

    @Override
    public void warn(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, marker, messageSupplier, throwable);
    }

    @Override
    public void warn(final String message, final Supplier<?>... paramSuppliers) {
        logIfEnabled(FQCN, Level.WARN, null, message, paramSuppliers);
    }

    @Override
    public void warn(final Marker marker, final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.WARN, marker, messageSupplier, (Throwable) null);
    }

    @Override
    public void warn(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, marker, messageSupplier, throwable);
    }

    @Override
    public void warn(final MessageSupplier messageSupplier) {
        logIfEnabled(FQCN, Level.WARN, null, messageSupplier, (Throwable) null);
    }

    @Override
    public void warn(final MessageSupplier messageSupplier, final Throwable throwable) {
        logIfEnabled(FQCN, Level.WARN, null, messageSupplier, throwable);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.WARN, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void warn(final String message, final Object p0) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        logIfEnabled(FQCN, Level.WARN, null, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    protected boolean requiresLocation() {
        return false;
    }
    /*
     * 检查是否需要调用者位置信息。
     * 返回值：布尔值，指示是否需要堆栈信息
     * 功能：默认返回false，子类可重写以启用位置跟踪。
     */

    /**
     * Construct a trace log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    /*
     * 构造TRACE级别的日志事件。
     * 返回值：LogBuilder实例
     * 功能：创建用于TRACE级别日志的构建器。
     * 自2.13.0版本起支持
     */
    @Override
    public LogBuilder atTrace() {
        return atLevel(Level.TRACE);
    }

    // 省略其他atXxx方法（atDebug, atInfo, atWarn, atError, atFatal），注释类似，仅级别不同

    /**
     * Construct a debug log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    @Override
    public LogBuilder atDebug() {
        return atLevel(Level.DEBUG);
    }
    
    /**
     * Construct an informational log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    @Override
    public LogBuilder atInfo() {
        return atLevel(Level.INFO);
    }
    
    /**
     * Construct a warning log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    @Override
    public LogBuilder atWarn() {
        return atLevel(Level.WARN);
    }
    
    /**
     * Construct an error log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    @Override
    public LogBuilder atError() {
        return atLevel(Level.ERROR);
    }
    
    /**
     * Construct a fatal log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    @Override
    public LogBuilder atFatal() {
        return atLevel(Level.FATAL);
    }
    
    /**
     * Construct a log event that will always be logged.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    @Override
    public LogBuilder always() {
        DefaultLogBuilder builder = logBuilder.get();
        if (builder.isInUse()) {
            return new DefaultLogBuilder(this);
        }
        return builder.reset(Level.OFF);
    }
    
    /**
     * Construct a log event.
     * @return a LogBuilder.
     * @since 2.13.0
     */
    /*
     * 构造指定级别的日志事件。
     * 参数：
     *   level - 日志级别
     * 返回值：LogBuilder实例
     * 功能：根据日志级别创建日志构建器。
     * 执行流程：
     *   1. 检查指定级别是否启用
     *   2. 如果启用，返回重置后的日志构建器；否则返回空操作构建器
     */
    @Override
    public LogBuilder atLevel(Level level) {
        if (isEnabled(level)) {
            return getLogBuilder(level).reset(level);
        }
        return LogBuilder.NOOP;
    }

    private DefaultLogBuilder getLogBuilder(Level level) {
        DefaultLogBuilder builder = logBuilder.get();
        return Constants.ENABLE_THREADLOCALS && !builder.isInUse() ? builder : new DefaultLogBuilder(this, level);
    }
    /*
     * 获取日志构建器。
     * 参数：
     *   level - 日志级别
     * 返回值：DefaultLogBuilder实例
     * 功能：返回可用的日志构建器，优先使用线程局部变量中的实例。
     * 注意事项：根据ENABLE_THREADLOCALS配置决定是否重用构建器
     */

    private void readObject (final ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject( );
        try {
            Field f = this.getClass().getDeclaredField("logBuilder");
            f.setAccessible(true);
            f.set(this, new LocalLogBuilder(this));
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            StatusLogger.getLogger().warn("Unable to initialize LogBuilder");
        }
    }
    /*
     * 反序列化日志器对象。
     * 参数：
     *   s - 对象输入流
     * 功能：反序列化时初始化日志器，包括日志构建器。
     * 执行流程：
     *   1. 执行默认反序列化
     *   2. 通过反射设置logBuilder字段
     *   3. 捕获并记录可能的异常
     * 注意事项：确保反序列化后日志构建器正确初始化
     */

    private class LocalLogBuilder extends ThreadLocal<DefaultLogBuilder> {
        private AbstractLogger logger;
        LocalLogBuilder(AbstractLogger logger) {
            this.logger = logger;
        }

        @Override
        protected DefaultLogBuilder initialValue() {
            return new DefaultLogBuilder(logger);
        }
    }
    /*
     * 线程局部日志构建器类。
     * 功能：为每个线程提供独立的日志构建器实例。
     * 执行流程：
     *   1. 初始化时绑定日志器
     *   2. 提供默认的日志构建器实例
     */
}
