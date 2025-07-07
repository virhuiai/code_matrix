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
package org.apache.logging.log4j.core;

import static org.apache.logging.log4j.core.util.ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.jmx.Server;
import org.apache.logging.log4j.core.util.Cancellable;
import org.apache.logging.log4j.core.util.ExecutorServices;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContextShutdownAware;
import org.apache.logging.log4j.spi.LoggerContextShutdownEnabled;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.log4j.spi.Terminable;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;
import org.apache.logging.log4j.util.PropertiesUtil;


/**
 * The LoggerContext is the anchor for the logging system. It maintains a list of all the loggers requested by
 * applications and a reference to the Configuration. The Configuration will contain the configured loggers, appenders,
 * filters, etc and will be atomically updated whenever a reconfigure occurs.
 *
 * LoggerContext 是日志系统的核心。它维护着应用程序请求的所有 Logger 列表以及对 Configuration 的引用。
 * Configuration 将包含配置的 Logger、Appender、Filter 等，并在每次重新配置时进行原子性更新。
 */
public class LoggerContext extends AbstractLifeCycle
        implements org.apache.logging.log4j.spi.LoggerContext, AutoCloseable, Terminable, ConfigurationListener,
        LoggerContextShutdownEnabled {

    /**
     * Property name of the property change event fired if the configuration is changed.
     * 如果配置发生改变，此属性更改事件将被触发的属性名称。
     */
    public static final String PROPERTY_CONFIG = "config";

    private static final Configuration NULL_CONFIGURATION = new NullConfiguration();
    // 表示一个空的配置对象，在某些情况下（例如停止 LoggerContext 时）会使用。

    private final LoggerRegistry<Logger> loggerRegistry = new LoggerRegistry<>();
    // Logger 注册表，用于存储和管理 Logger 实例。
    private final CopyOnWriteArrayList<PropertyChangeListener> propertyChangeListeners = new CopyOnWriteArrayList<>();
    // 属性变更监听器列表，用于在配置更改时通知监听器。
    private volatile List<LoggerContextShutdownAware> listeners;
    // LoggerContext 关闭监听器列表，用于在上下文关闭时通知监听器。

    /**
     * The Configuration is volatile to guarantee that initialization of the Configuration has completed before the
     * reference is updated.
     * Configuration 使用 volatile 关键字修饰，以确保在引用更新之前，Configuration 的初始化已完成。
     */
    private volatile Configuration configuration = new DefaultConfiguration();
    // 当前的配置对象，默认为 DefaultConfiguration。
    private static final String EXTERNAL_CONTEXT_KEY = "__EXTERNAL_CONTEXT_KEY__";
    // 用于在 externalMap 中存储外部上下文的键。
    private ConcurrentMap<String, Object> externalMap = new ConcurrentHashMap<>();
    // 外部上下文映射，用于存储与此 LoggerContext 关联的任意外部对象。
    private String contextName;
    // LoggerContext 的名称。
    private volatile URI configLocation;
    // 配置文件的 URI 位置。
    private Cancellable shutdownCallback;
    // 关闭回调，用于取消注册的关闭钩子。

    private final Lock configLock = new ReentrantLock();
    // 配置锁，用于同步对配置的访问和更新，确保线程安全。

    /**
     * Constructor taking only a name.
     * 仅包含名称的构造函数。
     *
     * @param name The context name.
     * 上下文名称。
     */
    public LoggerContext(final String name) {
        this(name, null, (URI) null);
    }

    /**
     * Constructor taking a name and a reference to an external context.
     * 包含名称和对外部上下文引用的构造函数。
     *
     * @param name The context name.
     * 上下文名称。
     * @param externalContext The external context.
     * 外部上下文对象。
     */
    public LoggerContext(final String name, final Object externalContext) {
        this(name, externalContext, (URI) null);
    }

    /**
     * Constructor taking a name, external context and a configuration URI.
     * 包含名称、外部上下文和配置URI的构造函数。
     *
     * @param name The context name.
     * 上下文名称。
     * @param externalContext The external context.
     * 外部上下文对象。
     * @param configLocn The location of the configuration as a URI.
     * 配置文件的URI位置。
     */
    public LoggerContext(final String name, final Object externalContext, final URI configLocn) {
        this.contextName = name; // 设置 LoggerContext 的名称。
        if (externalContext == null) {
            externalMap.remove(EXTERNAL_CONTEXT_KEY); // 如果外部上下文为 null，则从 externalMap 中移除。
        } else {
            externalMap.put(EXTERNAL_CONTEXT_KEY, externalContext); // 否则，将外部上下文存储到 externalMap 中。
        }
        this.configLocation = configLocn; // 设置配置文件的 URI 位置。
    }

    /**
     * Constructor taking a name external context and a configuration location String. The location must be resolvable
     * to a File.
     * 包含名称、外部上下文和配置位置字符串的构造函数。该位置必须能解析为一个文件。
     *
     * @param name The configuration location.
     * 配置位置。
     * @param externalContext The external context.
     * 外部上下文对象。
     * @param configLocn The configuration location.
     * 配置位置字符串。
     */
    public LoggerContext(final String name, final Object externalContext, final String configLocn) {
        this.contextName = name; // 设置 LoggerContext 的名称。
        if (externalContext == null) {
            externalMap.remove(EXTERNAL_CONTEXT_KEY); // 如果外部上下文为 null，则从 externalMap 中移除。
        } else {
            externalMap.put(EXTERNAL_CONTEXT_KEY, externalContext); // 否则，将外部上下文存储到 externalMap 中。
        }
        if (configLocn != null) { // 如果配置位置字符串不为 null
            URI uri;
            try {
                uri = new File(configLocn).toURI(); // 尝试将配置位置字符串转换为 URI。
            } catch (final Exception ex) {
                uri = null; // 转换失败则设置为 null。
            }
            configLocation = uri; // 设置配置文件的 URI 位置。
        } else {
            configLocation = null; // 如果配置位置字符串为 null，则设置 configLocation 为 null。
        }
    }

    @Override
    public void addShutdownListener(LoggerContextShutdownAware listener) {
    // 添加关闭监听器。
        if (listeners == null) {// 检查 listeners 列表是否已初始化。
            synchronized(this) {// 同步块，确保线程安全地初始化 listeners 列表。
                if (listeners == null) {
                    listeners = new CopyOnWriteArrayList<LoggerContextShutdownAware>();// 惰性初始化 CopyOnWriteArrayList。
                }
            }
        }
        listeners.add(listener); // 将监听器添加到列表中。
    }

    @Override
    public List<LoggerContextShutdownAware> getListeners() {
        // 获取关闭监听器列表。
        return listeners;
    }

    /**
     * Returns the current LoggerContext.
     * 返回当前的 LoggerContext。
     * <p>
     * Avoids the type cast for:
     * 避免了类型转换：
     * </p>
     *
     * <pre>
     * (LoggerContext) LogManager.getContext();
     * </pre>
     *
     * <p>
     * WARNING - The LoggerContext returned by this method may not be the LoggerContext used to create a Logger for the
     * calling class.
     * 警告 - 此方法返回的 LoggerContext 可能不是用于为调用类创建 Logger 的 LoggerContext。
     * </p>
     *
     * @return The current LoggerContext.
     * 当前的 LoggerContext。
     * @see LogManager#getContext()
     */
    public static LoggerContext getContext() {
        return (LoggerContext) LogManager.getContext();
    }

    /**
     * Returns a LoggerContext.
     * 返回一个 LoggerContext。
     * <p>
     * Avoids the type cast for:
     * 避免了类型转换：
     * </p>
     *
     * <pre>
     * (LoggerContext) LogManager.getContext(currentContext);
     * </pre>
     *
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * 如果为 false，则返回适合此方法调用者的 LoggerContext。例如，在 Web 应用程序中，如果调用者是 WEB-INF/lib 中的类，则可能返回一个 LoggerContext；如果调用者是容器类路径中的类，则可能返回不同的 LoggerContext。如果为 true，则只返回一个 LoggerContext。
     * @return a LoggerContext.
     * 一个 LoggerContext。
     * @see LogManager#getContext(boolean)
     */
    public static LoggerContext getContext(final boolean currentContext) {
        return (LoggerContext) LogManager.getContext(currentContext);
    }

    /**
     * Returns a LoggerContext.
     * 返回一个 LoggerContext。
     * <p>
     * Avoids the type cast for:
     * 避免了类型转换：
     * </p>
     *
     * <pre>
     * (LoggerContext) LogManager.getContext(loader, currentContext, configLocation);
     * </pre>
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * 上下文的 ClassLoader。如果为 null，上下文将尝试确定合适的 ClassLoader。
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * 如果为 false，则返回适合此方法调用者的 LoggerContext。例如，在 Web 应用程序中，如果调用者是 WEB-INF/lib 中的类，则可能返回一个 LoggerContext；如果调用者是容器类路径中的类，则可能返回不同的 LoggerContext。如果为 true，则只返回一个 LoggerContext。
     * @param configLocation The URI for the configuration to use.
     * 要使用的配置的 URI。
     * @return a LoggerContext.
     * 一个 LoggerContext。
     * @see LogManager#getContext(ClassLoader, boolean, URI)
     */
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
            final URI configLocation) {
        return (LoggerContext) LogManager.getContext(loader, currentContext, configLocation);
    }

    @Override
    public void start() {
    // 启动 LoggerContext。
        LOGGER.debug("Starting LoggerContext[name={}, {}]...", getName(), this); // 记录启动日志。
        // 根据系统属性判断是否在启动时打印堆栈轨迹。
        if (PropertiesUtil.getProperties().getBooleanProperty("log4j.LoggerContext.stacktrace.on.start", false)) {
            LOGGER.debug("Stack trace to locate invoker",
                    new Exception("Not a real error, showing stack trace to locate invoker")); // 打印堆栈轨迹用于调试。
        }
        if (configLock.tryLock()) { // 尝试获取配置锁，避免并发启动问题。
            try {
                if (this.isInitialized() || this.isStopped()) { // 如果 LoggerContext 处于初始化或停止状态。
                    this.setStarting(); // 设置为启动中状态。
                    reconfigure(); // 重新配置。
                    if (this.configuration.isShutdownHookEnabled()) { // 如果配置启用了关闭钩子。
                        setUpShutdownHook(); // 设置关闭钩子。
                    }
                    this.setStarted(); // 设置为已启动状态。
                }
            } finally {
                configLock.unlock(); // 释放配置锁。
            }
        }
        LOGGER.debug("LoggerContext[name={}, {}] started OK.", getName(), this); // 记录启动成功日志。
    }

    /**
     * Starts with a specific configuration.
     * 使用特定配置启动。
     *
     * @param config The new Configuration.
     * 新的配置。
     */
    public void start(final Configuration config) {
        // 使用指定的配置启动 LoggerContext。
        LOGGER.debug("Starting LoggerContext[name={}, {}] with configuration {}...", getName(), this, config); // 记录启动日志。
        if (configLock.tryLock()) { // 尝试获取配置锁。
            try {
                if (this.isInitialized() || this.isStopped()) { // 如果 LoggerContext 处于初始化或停止状态。
                    if (this.configuration.isShutdownHookEnabled()) { // 如果配置启用了关闭钩子。
                        setUpShutdownHook(); // 设置关闭钩子。
                    }
                    this.setStarted(); // 设置为已启动状态。
                }
            } finally {
                configLock.unlock(); // 释放配置锁。
            }
        }
        setConfiguration(config); // 设置新的配置。
        LOGGER.debug("LoggerContext[name={}, {}] started OK with configuration {}.", getName(), this, config); // 记录启动成功日志。
    }

    private void setUpShutdownHook() {
        // 设置 JVM 关闭钩子。
        if (shutdownCallback == null) { // 避免重复设置关闭钩子。
            final LoggerContextFactory factory = LogManager.getFactory(); // 获取 LoggerContext 工厂。
            if (factory instanceof ShutdownCallbackRegistry) { // 如果工厂实现了 ShutdownCallbackRegistry 接口。
                LOGGER.debug(SHUTDOWN_HOOK_MARKER, "Shutdown hook enabled. Registering a new one."); // 记录关闭钩子已启用。
                // LOG4J2-1642 preload ExecutorServices as it is used in shutdown hook
                // LOG4J2-1642 预加载 ExecutorServices，因为它在关闭钩子中使用。
                ExecutorServices.ensureInitialized();
                try {
                    final long shutdownTimeoutMillis = this.configuration.getShutdownTimeoutMillis(); // 获取关闭超时时间。
                    this.shutdownCallback = ((ShutdownCallbackRegistry) factory).addShutdownCallback(new Runnable() {
                        @Override
                        public void run() {
                            @SuppressWarnings("resource")
                            final LoggerContext context = LoggerContext.this; // 获取当前 LoggerContext 实例。
                            LOGGER.debug(SHUTDOWN_HOOK_MARKER, "Stopping LoggerContext[name={}, {}]",
                                    context.getName(), context); // 记录停止 LoggerContext 日志。
                            context.stop(shutdownTimeoutMillis, TimeUnit.MILLISECONDS); // 停止 LoggerContext。
                        }

                        @Override
                        public String toString() {
                            return "Shutdown callback for LoggerContext[name=" + LoggerContext.this.getName() + ']';
                        }
                    });
                } catch (final IllegalStateException e) {
                    throw new IllegalStateException(
                            "Unable to register Log4j shutdown hook because JVM is shutting down.", e); // 抛出异常，如果 JVM 正在关闭。
                } catch (final SecurityException e) {
                    LOGGER.error(SHUTDOWN_HOOK_MARKER, "Unable to register shutdown hook due to security restrictions",
                            e); // 记录安全异常。
                }
            }
        }
    }

    @Override
    public void close() {
        // 关闭 LoggerContext。
        stop(); // 调用 stop 方法停止。
    }

    @Override
    public void terminate() {
        // 终止 LoggerContext。
        stop(); // 调用 stop 方法停止。
    }

    /**
     * Blocks until all Log4j tasks have completed execution after a shutdown request and all appenders have shut down,
     * or the timeout occurs, or the current thread is interrupted, whichever happens first.
     * 在关闭请求后，阻塞直到所有 Log4j 任务完成执行并且所有 appender 都已关闭，或者超时发生，或者当前线程被中断，以先发生者为准。
     * <p>
     * Not all appenders will honor this, it is a hint and not an absolute guarantee that the this method not block longer.
     * Setting timeout too low increase the risk of losing outstanding log events not yet written to the final
     * destination.
     * 并非所有 appender 都会遵守此规则，这只是一个提示，不能绝对保证此方法不会阻塞更长时间。将超时时间设置过低会增加丢失尚未写入最终目的地的未处理日志事件的风险。
     * <p>
     * Log4j can start threads to perform certain actions like file rollovers, calling this method with a positive timeout will
     * block until the rollover thread is done.
     * Log4j 可以启动线程来执行某些操作，例如文件滚动。使用正超时调用此方法将阻塞直到滚动线程完成。
     *
     * @param timeout the maximum time to wait, or 0 which mean that each apppender uses its default timeout, and don't wait for background
    tasks
     * 等待的最长时间，或者 0 表示每个 appender 使用其默认超时，并且不等待后台任务。
     * @param timeUnit
     *            the time unit of the timeout argument
     * 超时参数的时间单位。
     * @return {@code true} if the logger context terminated and {@code false} if the timeout elapsed before
     *         termination.
     * 如果 LoggerContext 已终止则返回 {@code true}，如果在终止前超时则返回 {@code false}。
     * @since 2.7
     */
    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        // 停止 LoggerContext。
        LOGGER.debug("Stopping LoggerContext[name={}, {}]...", getName(), this); // 记录停止日志。
        configLock.lock(); // 获取配置锁。
        try {
            if (this.isStopped()) { // 如果 LoggerContext 已经停止，则直接返回 true。
                return true;
            }

            this.setStopping(); // 设置为停止中状态。
            try {
                Server.unregisterLoggerContext(getName()); // LOG4J2-406, LOG4J2-500 解注册 MBeans。
            } catch (final LinkageError | Exception e) {
                // LOG4J2-1506 Hello Android, GAE
                LOGGER.error("Unable to unregister MBeans", e); // 记录无法解注册 MBeans 的错误。
            }
            if (shutdownCallback != null) { // 如果存在关闭回调。
                shutdownCallback.cancel(); // 取消关闭回调。
                shutdownCallback = null; // 置为 null。
            }
            final Configuration prev = configuration; // 保存当前配置。
            configuration = NULL_CONFIGURATION; // 将配置设置为 NULL_CONFIGURATION。
            updateLoggers(); // 更新所有 Logger。
            if (prev instanceof LifeCycle2) { // 如果前一个配置实现了 LifeCycle2 接口。
                ((LifeCycle2) prev).stop(timeout, timeUnit); // 调用其带超时参数的 stop 方法。
            } else {
                prev.stop(); // 否则调用不带参数的 stop 方法。
            }
            externalMap.clear(); // 清空外部上下文映射。
            LogManager.getFactory().removeContext(this); // 从工厂中移除此上下文。
        } finally {
            configLock.unlock(); // 释放配置锁。
            this.setStopped(); // 设置为已停止状态。
        }
        if (listeners != null) { // 如果存在关闭监听器。
            for (LoggerContextShutdownAware listener : listeners) { // 遍历并通知所有监听器。
                try {
                    listener.contextShutdown(this); // 调用监听器的 contextShutdown 方法。
                } catch (Exception ex) {
                    // Ignore the exception. 忽略异常。
                }
            }
        }
        LOGGER.debug("Stopped LoggerContext[name={}, {}] with status {}", getName(), this, true); // 记录停止成功日志。
        return true;
    }

    /**
     * Gets the name.
     * 获取名称。
     *
     * @return the name.
     * 名称。
     */
    public String getName() {
        return contextName;
    }

    /**
     * Gets the root logger.
     * 获取根 Logger。
     *
     * @return the root logger.
     * 根 Logger。
     */
    public Logger getRootLogger() {
        return getLogger(LogManager.ROOT_LOGGER_NAME);
    }

    /**
     * Sets the name.
     * 设置名称。
     *
     * @param name the new LoggerContext name
     * 新的 LoggerContext 名称
     * @throws NullPointerException if the specified name is {@code null}
     * 如果指定名称为 {@code null} 则抛出 NullPointerException
     */
    public void setName(final String name) {
        contextName = Objects.requireNonNull(name); // 设置上下文名称，如果为 null 则抛出 NullPointerException。
    }

    @Override
    public Object getObject(String key) {
        // 根据键获取外部上下文中的对象。
        return externalMap.get(key);
    }

    @Override
    public Object putObject(String key, Object value) {
        // 将键值对放入外部上下文。
        return externalMap.put(key, value);
    }

    @Override
    public Object putObjectIfAbsent(String key, Object value) {
        // 如果键不存在，则将键值对放入外部上下文。
        return externalMap.putIfAbsent(key, value);
    }

    @Override
    public Object removeObject(String key) {
        // 根据键从外部上下文移除对象。
        return externalMap.remove(key);
    }

    @Override
    public boolean removeObject(String key, Object value) {
        // 如果键和值都匹配，则从外部上下文移除对象。
        return externalMap.remove(key, value);
    }

    /**
     * Sets the external context.
     * 设置外部上下文。
     *
     * @param context The external context.
     * 外部上下文。
     */
    public void setExternalContext(final Object context) {
        if (context != null) {
            this.externalMap.put(EXTERNAL_CONTEXT_KEY, context); // 如果上下文不为 null，则放入外部上下文映射。
        } else {
            this.externalMap.remove(EXTERNAL_CONTEXT_KEY); // 否则移除。
        }
    }

    /**
     * Returns the external context.
     * 返回外部上下文。
     *
     * @return The external context.
     * 外部上下文。
     */
    @Override
    public Object getExternalContext() {
        return this.externalMap.get(EXTERNAL_CONTEXT_KEY); // 获取外部上下文对象。
    }

    /**
     * Gets a Logger from the Context.
     * 从上下文中获取一个 Logger。
     *
     * @param name The name of the Logger to return.
     * 要返回的 Logger 的名称。
     * @return The Logger.
     * Logger。
     */
    @Override
    public Logger getLogger(final String name) {
        return getLogger(name, null); // 调用重载方法，MessageFactory 为 null。
    }

    /**
     * Gets a collection of the current loggers.
     * 获取当前 Logger 的集合。
     * <p>
     * Whether this collection is a copy of the underlying collection or not is undefined. Therefore, modify this
     * collection at your own risk.
     * </p>
     * 集合是否是底层集合的副本是不确定的。因此，修改此集合需自行承担风险。
     *
     * @return a collection of the current loggers.
     * 当前 Logger 的集合。
     */
    public Collection<Logger> getLoggers() {
        return loggerRegistry.getLoggers(); // 从 Logger 注册表中获取所有 Logger。
    }

    /**
     * Obtains a Logger from the Context.
     * 从上下文中获取一个 Logger。
     *
     * @param name The name of the Logger to return.
     * 要返回的 Logger 的名称。
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change the
     *            logger but will log a warning if mismatched.
     * 消息工厂仅在创建 Logger 时使用，后续使用不会改变 Logger，但如果消息工厂不匹配会记录警告。
     * @return The Logger.
     * Logger。
     */
    @Override
    public Logger getLogger(final String name, final MessageFactory messageFactory) {
        // Note: This is the only method where we add entries to the 'loggerRegistry' ivar.
        // 注意：这是我们向 'loggerRegistry' 实例变量添加条目的唯一方法。
        Logger logger = loggerRegistry.getLogger(name, messageFactory); // 尝试从注册表中获取 Logger。
        if (logger != null) { // 如果 Logger 已存在。
            AbstractLogger.checkMessageFactory(logger, messageFactory); // 检查消息工厂是否匹配。
            return logger; // 返回现有 Logger。
        }

        logger = newInstance(this, name, messageFactory); // 创建新的 Logger 实例。
        loggerRegistry.putIfAbsent(name, messageFactory, logger); // 如果不存在则将新的 Logger 放入注册表。
        return loggerRegistry.getLogger(name, messageFactory); // 返回（可能已存在的）Logger。
    }

    /**
     * Gets the LoggerRegistry.
     * 获取 LoggerRegistry。
     *
     * @return the LoggerRegistry.
     * LoggerRegistry。
     * @since 2.17.2
     */
    public LoggerRegistry<Logger> getLoggerRegistry() {
        return loggerRegistry; // 返回 Logger 注册表。
    }

    /**
     * Determines if the specified Logger exists.
     * 判断指定的 Logger 是否存在。
     *
     * @param name The Logger name to search for.
     * 要搜索的 Logger 名称。
     * @return True if the Logger exists, false otherwise.
     * 如果 Logger 存在则为 True，否则为 False。
     */
    @Override
    public boolean hasLogger(final String name) {
        return loggerRegistry.hasLogger(name); // 检查 Logger 注册表中是否存在指定名称的 Logger。
    }

    /**
     * Determines if the specified Logger exists.
     * 判断指定的 Logger 是否存在。
     *
     * @param name The Logger name to search for.
     * 要搜索的 Logger 名称。
     * @return True if the Logger exists, false otherwise.
     * 如果 Logger 存在则为 True，否则为 False。
     */
    @Override
    public boolean hasLogger(final String name, final MessageFactory messageFactory) {
        return loggerRegistry.hasLogger(name, messageFactory); // 检查 Logger 注册表中是否存在指定名称和消息工厂的 Logger。
    }

    /**
     * Determines if the specified Logger exists.
     * 判断指定的 Logger 是否存在。
     *
     * @param name The Logger name to search for.
     * 要搜索的 Logger 名称。
     * @return True if the Logger exists, false otherwise.
     * 如果 Logger 存在则为 True，否则为 False。
     */
    @Override
    public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
        return loggerRegistry.hasLogger(name, messageFactoryClass); // 检查 Logger 注册表中是否存在指定名称和消息工厂类的 Logger。
    }

	/**
	 * Returns the current Configuration. The Configuration will be replaced when a reconfigure occurs.
     * 返回当前的 Configuration。当重新配置发生时，Configuration 将被替换。
	 *
	 * @return The current Configuration, never {@code null}, but may be
	 * {@link org.apache.logging.log4j.core.config.NullConfiguration}.
     * 当前的 Configuration，永不为 {@code null}，但可能是
     * {@link org.apache.logging.log4j.core.config.NullConfiguration}。
	 */
	public Configuration getConfiguration() {
       return configuration; // 返回当前的配置对象。
	}

    /**
     * Adds a Filter to the Configuration. Filters that are added through the API will be lost when a reconfigure
     * occurs.
     * 向 Configuration 添加一个 Filter。通过 API 添加的 Filter 在重新配置时会丢失。
     *
     * @param filter The Filter to add.
     * 要添加的 Filter。
     */
    public void addFilter(final Filter filter) {
        configuration.addFilter(filter); // 向当前配置添加过滤器。
    }

    /**
     * Removes a Filter from the current Configuration.
     * 从当前 Configuration 中移除一个 Filter。
     *
     * @param filter The Filter to remove.
     * 要移除的 Filter。
     */
    public void removeFilter(final Filter filter) {
        configuration.removeFilter(filter); // 从当前配置移除过滤器。
    }

    /**
     * Sets the Configuration to be used.
     * 设置要使用的 Configuration。
     *
     * @param config The new Configuration.
     * 新的 Configuration。
     * @return The previous Configuration.
     * 之前的 Configuration。
     */
    public Configuration setConfiguration(final Configuration config) {
        if (config == null) { // 如果新的配置为 null。
            LOGGER.error("No configuration found for context '{}'.", contextName); // 记录错误日志。
            // No change, return the current configuration.
            // 没有更改，返回当前配置。
            return this.configuration;
        }
        configLock.lock(); // 获取配置锁。
        try {
            final Configuration prev = this.configuration; // 保存之前的配置。
            config.addListener(this); // 将当前 LoggerContext 作为监听器添加到新的配置中。

            final ConcurrentMap<String, String> map = config.getComponent(Configuration.CONTEXT_PROPERTIES); // 获取配置中的上下文属性。

            try { // LOG4J2-719 network access may throw android.os.NetworkOnMainThreadException
                // LOG4J2-2808 don't block unless necessary
                map.computeIfAbsent("hostName", s -> NetUtils.getLocalHostname()); // 如果 hostName 不存在，则计算并设置本地主机名。
            } catch (final Exception ex) {
                LOGGER.debug("Ignoring {}, setting hostName to 'unknown'", ex.toString()); // 忽略异常，并将 hostName 设置为 "unknown"。
                map.putIfAbsent("hostName", "unknown"); // 如果 hostName 不存在，则设置为 "unknown"。
            }
            map.putIfAbsent("contextName", contextName); // 如果 contextName 不存在，则设置上下文名称。
            config.start(); // 启动新的配置。
            this.configuration = config; // 更新当前配置为新的配置。
            updateLoggers(); // 更新所有 Logger。
            if (prev != null) { // 如果存在之前的配置。
                prev.removeListener(this); // 从之前的配置中移除监听器。
                prev.stop(); // 停止之前的配置。
            }

            firePropertyChangeEvent(new PropertyChangeEvent(this, PROPERTY_CONFIG, prev, config)); // 触发属性变更事件。

            try {
                Server.reregisterMBeansAfterReconfigure(); // 重新注册 JMX MBeans。
            } catch (final LinkageError | Exception e) {
                // LOG4J2-716: Android has no java.lang.management
                LOGGER.error("Could not reconfigure JMX", e); // 记录 JMX 重新配置失败的错误。
            }
            // AsyncLoggers update their nanoClock when the configuration changes
            // 当配置更改时，异步 Logger 会更新其 nanoClock。
            Log4jLogEvent.setNanoClock(configuration.getNanoClock());

            return prev; // 返回之前的配置。
        } finally {
            configLock.unlock(); // 释放配置锁。
        }
    }

    private void firePropertyChangeEvent(final PropertyChangeEvent event) {
        // 触发属性变更事件，通知所有监听器。
        for (final PropertyChangeListener listener : propertyChangeListeners) { // 遍历所有属性变更监听器。
            listener.propertyChange(event); // 调用监听器的 propertyChange 方法。
        }
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        // 添加属性变更监听器。
        propertyChangeListeners.add(Objects.requireNonNull(listener, "listener")); // 添加监听器，如果为 null 则抛出 NullPointerException。
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        // 移除属性变更监听器。
        propertyChangeListeners.remove(listener); // 移除指定的监听器。
    }

    /**
     * Returns the initial configuration location or {@code null}. The returned value may not be the location of the
     * current configuration. Use {@link #getConfiguration()}.{@link Configuration#getConfigurationSource()
     * getConfigurationSource()}.{@link ConfigurationSource#getLocation() getLocation()} to get the actual source of the
     * current configuration.
     * 返回初始配置位置或 {@code null}。返回的值可能不是当前配置的位置。使用
     * {@link #getConfiguration()}.{@link Configuration#getConfigurationSource()
     * getConfigurationSource()}.{@link ConfigurationSource#getLocation() getLocation()} 获取当前配置的实际来源。
     *
     * @return the initial configuration location or {@code null}
     * 初始配置位置或 {@code null}
     */
    public URI getConfigLocation() {
        return configLocation; // 返回配置文件的 URI 位置。
    }

    /**
     * Sets the configLocation to the specified value and reconfigures this context.
     * 将 configLocation 设置为指定值并重新配置此上下文。
     *
     * @param configLocation the location of the new configuration
     * 新配置的位置。
     */
    public void setConfigLocation(final URI configLocation) {
        this.configLocation = configLocation; // 设置配置文件的 URI 位置。
        reconfigure(configLocation); // 重新配置 LoggerContext。
    }

    /**
     * Reconfigures the context.
     * 重新配置上下文。
     */
    private void reconfigure(final URI configURI) {
        // 重新配置 LoggerContext。
        Object externalContext = externalMap.get(EXTERNAL_CONTEXT_KEY); // 获取外部上下文。
        final ClassLoader cl = ClassLoader.class.isInstance(externalContext) ? (ClassLoader) externalContext : null; // 如果外部上下文是 ClassLoader，则进行类型转换。
        LOGGER.debug("Reconfiguration started for context[name={}] at URI {} ({}) with optional ClassLoader: {}",
                contextName, configURI, this, cl); // 记录重新配置开始日志。
        // 通过 ConfigurationFactory 获取新的配置实例。
        final Configuration instance = ConfigurationFactory.getInstance().getConfiguration(this, contextName, configURI, cl);
        if (instance == null) { // 如果获取到的配置实例为 null。
            LOGGER.error("Reconfiguration failed: No configuration found for '{}' at '{}' in '{}'", contextName, configURI, cl); // 记录重新配置失败日志。
        } else {
            setConfiguration(instance); // 设置新的配置。
            /*
             * instance.start(); Configuration old = setConfiguration(instance); updateLoggers(); if (old != null) {
             * old.stop(); }
             */
            final String location = configuration == null ? "?" : String.valueOf(configuration.getConfigurationSource()); // 获取配置源位置。
            LOGGER.debug("Reconfiguration complete for context[name={}] at URI {} ({}) with optional ClassLoader: {}",
                    contextName, location, this, cl); // 记录重新配置完成日志。
        }
    }

    /**
     * Reconfigures the context. Log4j does not remove Loggers during a reconfiguration. Log4j will create new
     * LoggerConfig objects and Log4j will point the Loggers at the new LoggerConfigs. Log4j will free the old
     * LoggerConfig, along with old Appenders and Filters.
     * 重新配置上下文。Log4j 在重新配置期间不会移除 Logger。Log4j 将创建新的 LoggerConfig 对象，并将 Logger 指向新的 LoggerConfig。Log4j 将释放旧的 LoggerConfig 以及旧的 Appender 和 Filter。
     */
    public void reconfigure() {
        reconfigure(configLocation); // 使用当前的配置位置进行重新配置。
    }

    public void reconfigure(Configuration configuration) {
        // 使用指定的配置进行重新配置。
        setConfiguration(configuration); // 设置新的配置。
        ConfigurationSource source = configuration.getConfigurationSource(); // 获取配置源。
        if (source != null) { // 如果配置源不为 null。
            URI uri = source.getURI(); // 获取配置源的 URI。
            if (uri != null) { // 如果 URI 不为 null。
                configLocation = uri; // 更新配置位置。
            }
        }
    }

    /**
     * Causes all Loggers to be updated against the current Configuration.
     * 使所有 Logger 根据当前 Configuration 进行更新。
     */
    public void updateLoggers() {
        updateLoggers(this.configuration); // 使用当前的配置更新所有 Logger。
    }

    /**
     * Causes all Logger to be updated against the specified Configuration.
     * 使所有 Logger 根据指定的 Configuration 进行更新。
     *
     * @param config The Configuration.
     * 配置。
     */
    public void updateLoggers(final Configuration config) {
        final Configuration old = this.configuration; // 保存旧的配置。
        for (final Logger logger : loggerRegistry.getLoggers()) { // 遍历所有 Logger。
            logger.updateConfiguration(config); // 更新每个 Logger 的配置。
        }
        firePropertyChangeEvent(new PropertyChangeEvent(this, PROPERTY_CONFIG, old, config)); // 触发属性变更事件。
    }

    /**
     * Causes a reconfiguration to take place when the underlying configuration file changes.
     * 当底层配置文件更改时，触发重新配置。
     *
     * @param reconfigurable The Configuration that can be reconfigured.
     * 可重新配置的 Configuration。
     */
    @Override
	public synchronized void onChange(final Reconfigurable reconfigurable) {
       final long startMillis = System.currentTimeMillis(); // 记录开始时间。
       LOGGER.debug("Reconfiguration started for context {} ({})", contextName, this); // 记录重新配置开始日志。
       initApiModule(); // 初始化 API 模块。
       final Configuration newConfig = reconfigurable.reconfigure(); // 执行重新配置，获取新的配置。
       if (newConfig != null) { // 如果获取到新的配置。
          setConfiguration(newConfig); // 设置新的配置。
			LOGGER.debug("Reconfiguration completed for {} ({}) in {} milliseconds.", contextName, this,
                System.currentTimeMillis() - startMillis); // 记录重新配置完成日志。
		} else {
			LOGGER.debug("Reconfiguration failed for {} ({}) in {} milliseconds.", contextName, this,
                System.currentTimeMillis() - startMillis); // 记录重新配置失败日志。
		}
	}

    private void initApiModule() {
        ThreadContextMapFactory.init(); // Or make public and call ThreadContext.init() which calls ThreadContextMapFactory.init().// 初始化 ThreadContextMapFactory。
    }

    // LOG4J2-151: changed visibility from private to protected
    // LOG4J2-151：可见性从 private 更改为 protected。
    protected Logger newInstance(final LoggerContext ctx, final String name, final MessageFactory messageFactory) {
        return new Logger(ctx, name, messageFactory); // 创建并返回一个新的 Logger 实例。
    }
}
