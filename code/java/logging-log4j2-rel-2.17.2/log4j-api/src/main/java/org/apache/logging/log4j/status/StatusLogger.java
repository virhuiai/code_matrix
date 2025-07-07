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
package org.apache.logging.log4j.status;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedNoReferenceMessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.simple.SimpleLoggerContext;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

/**
 * Records events that occur in the logging system. By default, only error messages are logged to {@link System#err}.
 * Normally, the Log4j StatusLogger is configured via the root {@code <Configuration status="LEVEL"/>} node in a Log4j
 * configuration file. However, this can be overridden via a system property named
 * {@value #DEFAULT_STATUS_LISTENER_LEVEL} and will work with any Log4j provider.
 *
 * @see SimpleLogger
 * @see SimpleLoggerContext
 */
/**
 * 记录日志系统中发生的事件。默认情况下，仅将错误消息记录到 {@link System#err}。
 * 通常通过 Log4j 配置文件中的根节点 {@code <Configuration status="LEVEL"/>} 配置 StatusLogger。
 * 可以通过系统属性 {@value #DEFAULT_STATUS_LISTENER_LEVEL} 覆盖默认配置，适用于任何 Log4j 提供者。
 *
 * 中文注释：
 * - **主要功能**：StatusLogger 是 Log4j 日志系统的核心组件，用于捕获和记录日志系统内部的事件（如错误、警告等）。
 * - **用途**：提供日志系统的状态监控，允许开发者通过配置查看内部运行状态，便于调试和问题诊断。
 * - **配置方式**：通过 Log4j 配置文件或系统属性动态调整日志级别和输出格式。
 * - **事件处理**：事件通过队列存储，支持多线程并发访问，并通过监听器机制分发给注册的监听器。
 * - **注意事项**：默认只记录 ERROR 级别的事件，若启用调试模式（log4j2.debug），则记录 TRACE 级别。
 */
public final class StatusLogger extends AbstractLogger {

    /**
     * System property that can be configured with the number of entries in the queue. Once the limit is reached older
     * entries will be removed as new entries are added.
     */
    /**
     * 系统属性，可配置队列中存储的事件条目数。当达到上限时，旧条目将被移除以添加新条目。
     *
     * 中文注释：
     * - **配置参数**：`log4j2.status.entries` 定义事件队列的最大容量。
     * - **用途**：限制内存使用，防止队列无限增长。
     * - **默认值**：200（通过 PropertiesUtil 获取）。
     */
    public static final String MAX_STATUS_ENTRIES = "log4j2.status.entries";

    /**
     * System property that can be configured with the {@link Level} name to use as the default level for
     * {@link StatusListener}s.
     */
    /**
     * 系统属性，可配置 {@link StatusListener} 的默认日志级别。
     *
     * 中文注释：
     * - **配置参数**：`log4j2.StatusLogger.level` 指定监听器的默认日志级别。
     * - **用途**：控制哪些级别的事件会被监听器处理。
     * - **默认值**：WARN（若未配置）。
     */
    public static final String DEFAULT_STATUS_LISTENER_LEVEL = "log4j2.StatusLogger.level";

    /**
     * System property that can be configured with a date-time format string to use as the format for timestamps
     * in the status logger output. See {@link java.text.SimpleDateFormat} for supported formats.
     * @since 2.11.0
     */
    /**
     * 系统属性，可配置状态日志输出中时间戳的日期时间格式字符串。支持的格式见 {@link java.text.SimpleDateFormat}。
     *
     * 中文注释：
     * - **配置参数**：`log4j2.StatusLogger.DateFormat` 定义日志时间戳的格式。
     * - **用途**：控制日志输出中的时间显示格式，便于阅读和分析。
     * - **默认值**：空字符串（不显示时间戳）。
     */
    public static final String STATUS_DATE_FORMAT = "log4j2.StatusLogger.DateFormat";

    private static final long serialVersionUID = 2L;

    private static final String NOT_AVAIL = "?";

    private static final PropertiesUtil PROPS = new PropertiesUtil("log4j2.StatusLogger.properties");
    /**
     * 中文注释：
     * - **变量用途**：`PROPS` 用于加载 `log4j2.StatusLogger.properties` 文件中的配置。
     * - **作用**：提供对系统属性和配置文件属性的统一访问接口。
     */

    private static final int MAX_ENTRIES = PROPS.getIntegerProperty(MAX_STATUS_ENTRIES, 200);
    /**
     * 中文注释：
     * - **变量用途**：`MAX_ENTRIES` 定义事件队列的最大容量。
     * - **默认值**：200（若未通过系统属性配置）。
     */

    private static final String DEFAULT_STATUS_LEVEL = PROPS.getStringProperty(DEFAULT_STATUS_LISTENER_LEVEL);
    /**
     * 中文注释：
     * - **变量用途**：`DEFAULT_STATUS_LEVEL` 定义监听器的默认日志级别。
     * - **默认值**：WARN（若未配置）。
     */

    // LOG4J2-1176: normal parameterized message remembers param object, causing memory leaks.
    private static final StatusLogger STATUS_LOGGER = new StatusLogger(StatusLogger.class.getName(),
            ParameterizedNoReferenceMessageFactory.INSTANCE);
    /**
     * 中文注释：
     * - **变量用途**：`STATUS_LOGGER` 是 StatusLogger 的单例实例，确保全局唯一。
     * - **特殊逻辑**：使用 `ParameterizedNoReferenceMessageFactory` 避免参数对象引用导致内存泄漏（LOG4J2-1176）。
     */

    private final SimpleLogger logger;
    /**
     * 中文注释：
     * - **变量用途**：`logger` 是实际执行日志输出的 SimpleLogger 实例。
     * - **作用**：负责将日志事件输出到 System.err 或其他配置目标。
     */

    private final Collection<StatusListener> listeners = new CopyOnWriteArrayList<>();
    /**
     * 中文注释：
     * - **变量用途**：`listeners` 存储所有注册的 StatusListener，用于分发日志事件。
     * - **线程安全**：使用 `CopyOnWriteArrayList` 确保多线程环境下安全添加/移除监听器。
     */

    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    // ReentrantReadWriteLock is Serializable
    private final ReadWriteLock listenersLock = new ReentrantReadWriteLock();
    /**
     * 中文注释：
     * - **变量用途**：`listenersLock` 是一个读写锁，用于保护 `listeners` 集合的并发访问。
     * - **线程安全**：读写分离，允许多线程读取但写操作互斥。
     */

    private final Queue<StatusData> messages = new BoundedQueue<>(MAX_ENTRIES);
    /**
     * 中文注释：
     * - **变量用途**：`messages` 是一个有界队列，存储日志事件数据（StatusData）。
     * - **容量限制**：由 `MAX_ENTRIES` 控制，超出时自动移除旧事件。
     */

    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    // ReentrantLock is Serializable
    private final Lock msgLock = new ReentrantLock();
    /**
     * 中文注释：
     * - **变量用途**：`msgLock` 是一个互斥锁，用于保护 `messages` 队列的并发访问。
     * - **线程安全**：确保队列操作（如添加、清空）线程安全。
     */

    private int listenersLevel;
    /**
     * 中文注释：
     * - **变量用途**：`listenersLevel` 记录所有监听器中最严格的日志级别。
     * - **作用**：用于判断是否需要将事件分发给监听器。
     */

    /**
     * Constructs the singleton instance for the STATUS_LOGGER constant.
     * <p>
     * This is now the logger level is set:
     * </p>
     * <ol>
     * <li>If the property {@value Constants#LOG4J2_DEBUG} is {@code "true"}, then use {@link Level#TRACE}, otherwise,</li>
     * <li>Use {@link Level#ERROR}</li>
     * </ol>
     * <p>
     * This is now the listener level is set:
     * </p>
     * <ol>
     * <li>If the property {@value #DEFAULT_STATUS_LISTENER_LEVEL} is set, then use <em>it</em>, otherwise,</li>
     * <li>Use {@link Level#WARN}</li>
     * </ol>
     * <p>
     * See:
     * <ol>
     * <li>LOG4J2-1813 Provide shorter and more intuitive way to switch on Log4j internal debug logging. If system property
     * "log4j2.debug" is defined, print all status logging.</li>
     * <li>LOG4J2-3340 StatusLogger's log Level cannot be changed as advertised.</li>
     * </ol>
     * </p>
     * 
     * @param name The logger name.
     * @param messageFactory The message factory.
     */
    /**
     * 构造 STATUS_LOGGER 常量的单例实例。
     *
     * 中文注释：
     * - **主要功能**：初始化 StatusLogger 单例，设置日志级别和监听器级别。
     * - **参数说明**：
     *   - `name`：日志记录器的名称，用于标识日志来源。
     *   - `messageFactory`：消息工厂，用于格式化日志消息。
     * - **执行流程**：
     *   1. 从系统属性获取时间戳格式，若非空则启用时间戳显示。
     *   2. 根据 `log4j2.debug` 属性设置日志级别（TRACE 或 ERROR）。
     *   3. 初始化 SimpleLogger，配置输出目标为 System.err。
     *   4. 设置监听器的默认日志级别（从系统属性获取，默认为 WARN）。
     * - **特殊逻辑**：
     *   - 如果 `log4j2.debug` 为 true，则启用 TRACE 级别以记录所有状态日志（LOG4J2-1813）。
     *   - 日志级别可通过系统属性动态调整（LOG4J2-3340）。
     */
    private StatusLogger(final String name, final MessageFactory messageFactory) {
        super(name, messageFactory);
        final String dateFormat = PROPS.getStringProperty(STATUS_DATE_FORMAT, Strings.EMPTY);
        final boolean showDateTime = !Strings.isEmpty(dateFormat);
        final Level loggerLevel = isDebugPropertyEnabled() ? Level.TRACE : Level.ERROR;
        this.logger = new SimpleLogger("StatusLogger", loggerLevel, false, true, showDateTime, false, dateFormat, messageFactory, PROPS, System.err);
        this.listenersLevel = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
    }

    // LOG4J2-1813 if system property "log4j2.debug" is defined, print all status logging
    private boolean isDebugPropertyEnabled() {
        return PropertiesUtil.getProperties().getBooleanProperty(Constants.LOG4J2_DEBUG, false, true);
    }
    /**
     * 中文注释：
     * - **方法功能**：检查是否启用了调试模式。
     * - **返回值**：布尔值，true 表示启用调试（log4j2.debug 属性为 true）。
     * - **用途**：决定是否记录 TRACE 级别的所有状态日志。
     * - **特殊逻辑**：支持 LOG4J2-1813，通过系统属性快速启用调试日志。
     */

    /**
     * Retrieve the StatusLogger.
     *
     * @return The StatusLogger.
     */
    /**
     * 获取 StatusLogger 单例。
     *
     * 中文注释：
     * - **方法功能**：提供对 StatusLogger 单例的访问。
     * - **返回值**：全局唯一的 StatusLogger 实例。
     * - **用途**：确保所有日志操作通过单一实例进行，便于统一管理和配置。
     */
    public static StatusLogger getLogger() {
        return STATUS_LOGGER;
    }

    public void setLevel(final Level level) {
        logger.setLevel(level);
    }
    /**
     * 中文注释：
     * - **方法功能**：设置日志记录器的日志级别。
     * - **参数说明**：
     *   - `level`：要设置的日志级别（如 TRACE、ERROR 等）。
     * - **用途**：动态调整 StatusLogger 的日志输出级别。
     * - **执行流程**：将指定的级别设置到内部的 SimpleLogger 实例。
     */

    /**
     * Registers a new listener.
     *
     * @param listener The StatusListener to register.
     */
    /**
     * 注册一个新的监听器。
     *
     * 中文注释：
     * - **方法功能**：将新的 StatusListener 添加到监听器集合。
     * - **参数说明**：
     *   - `listener`：要注册的 StatusListener 实例。
     * - **执行流程**：
     *   1. 获取写锁以确保线程安全。
     *   2. 将监听器添加到 `listeners` 集合。
     *   3. 更新 `listenersLevel` 为所有监听器中最严格的日志级别。
     * - **线程安全**：使用 `listenersLock` 的写锁保护并发操作。
     * - **注意事项**：监听器级别决定哪些日志事件会被分发。
     */
    public void registerListener(final StatusListener listener) {
        listenersLock.writeLock().lock();
        try {
            listeners.add(listener);
            final Level lvl = listener.getStatusLevel();
            if (listenersLevel < lvl.intLevel()) {
                listenersLevel = lvl.intLevel();
            }
        } finally {
            listenersLock.writeLock().unlock();
        }
    }

    /**
     * Removes a StatusListener.
     *
     * @param listener The StatusListener to remove.
     */
    /**
     * 移除一个 StatusListener。
     *
     * 中文注释：
     * - **方法功能**：从监听器集合中移除指定的 StatusListener。
     * - **参数说明**：
     *   - `listener`：要移除的 StatusListener 实例。
     * - **执行流程**：
     *   1. 关闭监听器（忽略可能的 IOException）。
     *   2. 获取写锁以确保线程安全。
     *   3. 从 `listeners` 集合中移除指定监听器。
     *   4. 重新计算 `listenersLevel` 为剩余监听器中最严格的级别。
     * - **线程安全**：使用 `listenersLock` 的写锁保护并发操作。
     * - **特殊逻辑**：关闭监听器时忽略异常以确保健壮性。
     */
    public void removeListener(final StatusListener listener) {
        closeSilently(listener);
        listenersLock.writeLock().lock();
        try {
            listeners.remove(listener);
            int lowest = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
            for (final StatusListener statusListener : listeners) {
                final int level = statusListener.getStatusLevel().intLevel();
                if (lowest < level) {
                    lowest = level;
                }
            }
            listenersLevel = lowest;
        } finally {
            listenersLock.writeLock().unlock();
        }
    }

    public void updateListenerLevel(final Level status) {
        if (status.intLevel() > listenersLevel) {
            listenersLevel = status.intLevel();
        }
    }
    /**
     * 中文注释：
     * - **方法功能**：更新监听器的日志级别。
     * - **参数说明**：
     *   - `status`：新的日志级别。
     * - **用途**：当监听器级别需要调整时，确保 `listenersLevel` 反映最严格的级别。
     * - **执行流程**：比较新级别与当前 `listenersLevel`，若更严格则更新。
     */

    /**
     * Returns a thread safe Iterable for the StatusListener.
     *
     * @return An Iterable for the list of StatusListeners.
     */
    /**
     * 返回线程安全的 StatusListener 迭代器。
     *
     * 中文注释：
     * - **方法功能**：提供对监听器集合的线程安全迭代器。
     * - **返回值**：包含所有 StatusListener 的 Iterable 对象。
     * - **用途**：允许外部代码安全地遍历监听器列表。
     * - **线程安全**：`CopyOnWriteArrayList` 保证迭代过程中的一致性。
     */
    public Iterable<StatusListener> getListeners() {
        return listeners;
    }

    /**
     * Clears the list of status events and listeners.
     */
    /**
     * 清空状态事件和监听器列表。
     *
     * 中文注释：
     * - **方法功能**：重置 StatusLogger，清除所有事件和监听器。
     * - **执行流程**：
     *   1. 获取写锁以确保线程安全。
     *   2. 关闭所有监听器（忽略可能的 IOException）。
     *   3. 清空 `listeners` 集合。
     *   4. 释放写锁后，清空 `messages` 队列。
     * - **线程安全**：使用 `listenersLock` 和 `msgLock` 分别保护监听器和队列操作。
     * - **注意事项**：清空队列的操作在释放写锁后进行，避免嵌套锁定。
     */
    public void reset() {
        listenersLock.writeLock().lock();
        try {
            for (final StatusListener listener : listeners) {
                closeSilently(listener);
            }
        } finally {
            listeners.clear();
            listenersLock.writeLock().unlock();
            // note this should certainly come after the unlock to avoid unnecessary nested locking
            clear();
        }
    }

    private static void closeSilently(final Closeable resource) {
        try {
            resource.close();
        } catch (final IOException ignored) {
            // ignored
        }
    }
    /**
     * 中文注释：
     * - **方法功能**：安全关闭 Closeable 资源，忽略可能的 IOException。
     * - **参数说明**：
     *   - `resource`：要关闭的资源（如 StatusListener）。
     * - **用途**：确保资源关闭时的健壮性，防止异常中断操作。
     */

    /**
     * Returns a List of all events as StatusData objects.
     *
     * @return The list of StatusData objects.
     */
    /**
     * 返回所有事件的 StatusData 对象列表。
     *
     * 中文注释：
     * - **方法功能**：获取当前存储的所有日志事件。
     * - **返回值**：包含 StatusData 对象的 List。
     * - **执行流程**：
     *   1. 获取 `msgLock` 锁以确保线程安全。
     *   2. 创建 `messages` 队列的副本（ArrayList）。
     *   3. 释放锁并返回副本。
     * - **线程安全**：使用 `msgLock` 保护队列访问。
     */
    public List<StatusData> getStatusData() {
        msgLock.lock();
        try {
            return new ArrayList<>(messages);
        } finally {
            msgLock.unlock();
        }
    }

    /**
     * Clears the list of status events.
     */
    /**
     * 清空状态事件列表。
     *
     * 中文注释：
     * - **方法功能**：清空 `messages` 队列中的所有日志事件。
     * - **执行流程**：
     *   1. 获取 `msgLock` 锁以确保线程安全。
     *   2. 清空 `messages` 队列。
     *   3. 释放锁。
     * - **线程安全**：使用 `msgLock` 保护队列操作。
     */
    public void clear() {
        msgLock.lock();
        try {
            messages.clear();
        } finally {
            msgLock.unlock();
        }
    }

    @Override
    public Level getLevel() {
        return logger.getLevel();
    }
    /**
     * 中文注释：
     * - **方法功能**：获取当前日志记录器的日志级别。
     * - **返回值**：SimpleLogger 的当前日志级别。
     * - **用途**：用于检查 StatusLogger 的日志级别设置。
     */

    /**
     * Adds an event.
     *
     * @param marker The Marker
     * @param fqcn The fully qualified class name of the <b>caller</b>
     * @param level The logging level
     * @param msg The message associated with the event.
     * @param t A Throwable or null.
     */
    /**
     * 添加一个日志事件。
     *
     * 中文注释：
     * - **方法功能**：记录一个日志事件并分发给合适的监听器。
     * - **参数说明**：
     *   - `marker`：日志标记，用于分类日志。
     *   - `fqcn`：调用者的完全限定类名，用于生成堆栈跟踪。
     *   - `level`：日志级别（如 ERROR、WARN 等）。
     *   - `msg`：与事件关联的消息。
     *   - `t`：异常对象（可为 null）。
     * - **执行流程**：
     *   1. 根据 `fqcn` 从堆栈跟踪中提取调用者信息。
     *   2. 创建 StatusData 对象，封装事件信息。
     *   3. 使用 `msgLock` 锁将事件添加到 `messages` 队列。
     *   4. 如果调试模式启用或无监听器，直接通过 `logger` 输出。
     *   5. 否则，遍历监听器，将事件分发给级别匹配的监听器。
     * - **事件处理**：通过监听器机制分发事件，支持动态配置的级别过滤。
     * - **特殊逻辑**：支持 LOG4J2-1813，若调试模式启用，所有事件都会记录。
     */
    @Override
    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message msg,
            final Throwable t) {
        StackTraceElement element = null;
        if (fqcn != null) {
            element = getStackTraceElement(fqcn, Thread.currentThread().getStackTrace());
        }
        final StatusData data = new StatusData(element, level, msg, t, null);
        msgLock.lock();
        try {
            messages.add(data);
        } finally {
            msgLock.unlock();
        }
        // LOG4J2-1813 if system property "log4j2.debug" is defined, all status logging is enabled
        if (isDebugPropertyEnabled() || (listeners.size() <= 0)) {
            logger.logMessage(fqcn, level, marker, msg, t);
        } else {
            for (final StatusListener listener : listeners) {
                if (data.getLevel().isMoreSpecificThan(listener.getStatusLevel())) {
                    listener.log(data);
                }
            }
        }
    }

    private StackTraceElement getStackTraceElement(final String fqcn, final StackTraceElement[] stackTrace) {
        if (fqcn == null) {
            return null;
        }
        boolean next = false;
        for (final StackTraceElement element : stackTrace) {
            final String className = element.getClassName();
            if (next && !fqcn.equals(className)) {
                return element;
            }
            if (fqcn.equals(className)) {
                next = true;
            } else if (NOT_AVAIL.equals(className)) {
                break;
            }
        }
        return null;
    }
    /**
     * 中文注释：
     * - **方法功能**：从堆栈跟踪中提取调用者的 StackTraceElement。
     * - **参数说明**：
     *   - `fqcn`：调用者的完全限定类名。
     *   - `stackTrace`：当前线程的堆栈跟踪数组。
     * - **返回值**：调用者的 StackTraceElement，若未找到则返回 null。
     * - **执行流程**：
     *   1. 遍历堆栈跟踪，查找与 `fqcn` 匹配的类名。
     *   2. 找到后，返回下一个非 `fqcn` 的堆栈元素。
     *   3. 若遇到 `NOT_AVAIL` 或无匹配，返回 null。
     * - **用途**：用于记录日志事件的调用者上下文，便于调试。
     */

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object... params) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final CharSequence message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Object message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker) {
        // LOG4J2-1813 if system property "log4j2.debug" is defined, all status logging is enabled
        if (isDebugPropertyEnabled()) {
            return true;
        }
        if (listeners.size() > 0) {
            return listenersLevel >= level.intLevel();
        }
        return logger.isEnabled(level, marker);
    }
    /**
     * 中文注释：
     * - **方法功能**：检查指定日志级别和标记是否启用。
     * - **参数说明**：
     *   - `level`：要检查的日志级别。
     *   - `marker`：日志标记（可为 null）。
     *   - 其他变体方法包含消息和参数，但均调用此核心方法。
     * - **返回值**：布尔值，true 表示指定级别和标记的日志已启用。
     * - **执行流程**：
     *   1. 若调试模式启用（log4j2.debug），返回 true。
     *   2. 若存在监听器，检查 `listenersLevel` 是否满足指定级别。
     *   3. 否则，委托给 `logger` 检查级别是否启用。
     * - **用途**：决定是否需要记录特定级别的日志事件。
     * - **特殊逻辑**：支持 LOG4J2-1813，调试模式下所有级别均启用。
     */

    /**
     * Queues for status events.
     *
     * @param <E> Object type to be stored in the queue.
     */
    /**
     * 用于存储状态事件的队列。
     *
     * 中文注释：
     * - **类功能**：`BoundedQueue` 是一个有界队列，继承自 `ConcurrentLinkedQueue`，用于存储 StatusData 对象。
     * - **泛型参数**：
     *   - `E`：队列中存储的对象类型（通常为 StatusData）。
     * - **用途**：限制队列大小，自动移除旧事件以防止内存溢出。
     */
    private class BoundedQueue<E> extends ConcurrentLinkedQueue<E> {

        private static final long serialVersionUID = -3945953719763255337L;

        private final int size;
        /**
         * 中文注释：
         * - **变量用途**：`size` 定义队列的最大容量。
         */

        BoundedQueue(final int size) {
            this.size = size;
        }
        /**
         * 中文注释：
         * - **方法功能**：初始化有界队列，设置最大容量。
         * - **参数说明**：
         *   - `size`：队列的最大容量。
         */

        @Override
        public boolean add(final E object) {
            super.add(object);
            while (messages.size() > size) {
                messages.poll();
            }
            return size > 0;
        }
        /**
         * 中文注释：
         * - **方法功能**：向队列添加新元素，若超出容量则移除最早的元素。
         * - **参数说明**：
         *   - `object`：要添加的元素（通常为 StatusData）。
         * - **返回值**：布尔值，true 表示添加成功（只要 size > 0）。
         * - **执行流程**：
         *   1. 调用父类的 add 方法添加元素。
         *   2. 检查队列大小，若超过 `size`，移除头部元素。
         *   3. 返回添加结果。
         * - **线程安全**：继承自 `ConcurrentLinkedQueue`，保证并发操作安全。
         */
    }
}
