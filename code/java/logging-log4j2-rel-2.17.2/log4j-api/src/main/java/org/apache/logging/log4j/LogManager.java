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
package org.apache.logging.log4j;

import java.net.URI;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.internal.LogManagerStatus;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.spi.Terminable;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ProviderUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;

/**
 * The anchor point for the Log4j logging system. The most common usage of this class is to obtain a named
 * {@link Logger}. The method {@link #getLogger()} is provided as the most convenient way to obtain a named Logger based
 * on the calling class name. This class also provides method for obtaining named Loggers that use
 * {@link String#format(String, Object...)} style messages instead of the default type of parameterized messages. These
 * are obtained through the {@link #getFormatterLogger(Class)} family of methods. Other service provider methods are
 * given through the {@link #getContext()} and {@link #getFactory()} family of methods; these methods are not normally
 * useful for typical usage of Log4j.
 */
// Log4j日志系统的核心入口类，主要用于获取命名的Logger实例。
// 提供便捷方法getLogger()根据调用类名获取Logger。
// 支持获取使用String.format样式的Logger，通过getFormatterLogger方法实现。
// 提供getContext()和getFactory()方法用于服务提供者，通常不用于常规Log4j使用场景。
public class LogManager {

    /**
     * Log4j property to set to the fully qualified class name of a custom implementation of
     * {@link org.apache.logging.log4j.spi.LoggerContextFactory}.
     */
    // 定义配置属性名，用于指定自定义LoggerContextFactory的完全限定类名。
    public static final String FACTORY_PROPERTY_NAME = "log4j2.loggerContextFactory";
    // 配置参数说明：FACTORY_PROPERTY_NAME用于在属性文件中指定自定义日志上下文工厂的类名。

    /**
     * The name of the root Logger is {@value #ROOT_LOGGER_NAME}.
     */
    // 根Logger的名称，值为一个空字符串。
    public static final String ROOT_LOGGER_NAME = Strings.EMPTY;
    // 关键变量说明：ROOT_LOGGER_NAME表示根Logger的名称，默认为空字符串。

    private static final Logger LOGGER = StatusLogger.getLogger();
    // 获取用于记录Log4j状态的Logger实例。
    // 关键变量说明：LOGGER用于记录Log4j系统本身的日志信息。

    // for convenience
    private static final String FQCN = LogManager.class.getName();
    // 定义LogManager类的完全限定类名，便于在获取上下文时使用。
    // 关键变量说明：FQCN存储LogManager的完全限定类名，用于上下文调用。

    private static volatile LoggerContextFactory factory;
    // 日志上下文工厂，负责创建和管理LoggerContext实例。
    // 关键变量说明：factory是volatile变量，确保线程安全，用于存储当前使用的日志上下文工厂。

    /**
     * Scans the classpath to find all logging implementation. Currently, only one will be used but this could be
     * extended to allow multiple implementations to be used.
     */
    // 静态初始化块，扫描类路径以查找日志实现，目前仅使用一个实现，但可扩展为支持多个实现。
    // 主要功能：初始化日志上下文工厂，加载配置的实现或默认实现。
    static {
        // Shortcut binding to force a specific logging implementation.
        final PropertiesUtil managerProps = PropertiesUtil.getProperties();
        // 获取Log4j的属性配置。
        // 关键步骤说明：通过PropertiesUtil加载Log4j的属性配置文件。
        final String factoryClassName = managerProps.getStringProperty(FACTORY_PROPERTY_NAME);
        // 从属性中获取自定义LoggerContextFactory的类名。
        // 重要配置参数说明：factoryClassName指定用户配置的日志上下文工厂类名。
        if (factoryClassName != null) {
            try {
                factory = LoaderUtil.newCheckedInstanceOf(factoryClassName, LoggerContextFactory.class);
                // 尝试加载并实例化指定的日志上下文工厂。
                // 关键步骤说明：使用LoaderUtil动态加载并创建自定义工厂实例。
            } catch (final ClassNotFoundException cnfe) {
                LOGGER.error("Unable to locate configured LoggerContextFactory {}", factoryClassName);
                // 如果类未找到，记录错误日志。
                // 事件处理逻辑：当无法找到配置的工厂类时，记录错误信息。
            } catch (final Exception ex) {
                LOGGER.error("Unable to create configured LoggerContextFactory {}", factoryClassName, ex);
                // 如果创建实例失败，记录错误日志。
                // 事件处理逻辑：当实例化工厂失败时，记录异常信息。
            }
        }

        if (factory == null) {
            // 如果未指定自定义工厂，则扫描类路径寻找可用实现。
            // 特殊处理说明：当未配置自定义工厂时，自动查找可用日志实现。
            final SortedMap<Integer, LoggerContextFactory> factories = new TreeMap<>();
            // 创建一个按优先级排序的工厂映射。
            // 关键变量说明：factories用于存储按优先级排序的日志上下文工厂。
            // note that the following initial call to ProviderUtil may block until a Provider has been installed when
            // running in an OSGi environment
            // 在OSGi环境中，ProviderUtil的初始调用可能阻塞，直到安装了Provider。
            if (ProviderUtil.hasProviders()) {
                // 检查是否存在日志提供者。
                // 关键步骤说明：通过ProviderUtil检查是否有可用的日志提供者。
                for (final Provider provider : ProviderUtil.getProviders()) {
                    // 遍历所有日志提供者。
                    // 关键步骤说明：循环处理每个Provider以加载其工厂。
                    final Class<? extends LoggerContextFactory> factoryClass = provider.loadLoggerContextFactory();
                    // 加载提供者的日志上下文工厂类。
                    // 关键步骤说明：从Provider加载对应的工厂类。
                    if (factoryClass != null) {
                        try {
                            factories.put(provider.getPriority(), factoryClass.newInstance());
                            // 将工厂实例按优先级存入映射。
                            // 关键步骤说明：根据提供者的优先级存储工厂实例。
                        } catch (final Exception e) {
                            LOGGER.error("Unable to create class {} specified in provider URL {}", factoryClass.getName(), provider
                                    .getUrl(), e);
                            // 如果创建工厂实例失败，记录错误日志。
                            // 事件处理逻辑：当无法创建工厂实例时，记录异常信息。
                        }
                    }
                }

                if (factories.isEmpty()) {
                    // 如果未找到任何工厂，使用简单日志工厂。
                    LOGGER.error("Log4j2 could not find a logging implementation. "
                            + "Please add log4j-core to the classpath. Using SimpleLogger to log to the console...");
                    // 记录错误，提示添加log4j-core到类路径。
                    // 事件处理逻辑：当没有找到日志实现时，记录错误并使用默认简单日志工厂。
                    factory = SimpleLoggerContextFactory.INSTANCE;
                    // 设置默认的简单日志工厂。
                    // 特殊处理说明：当没有其他实现时，回退到SimpleLoggerContextFactory。
                } else if (factories.size() == 1) {
                    // 如果只有一个工厂，使用该工厂。
                    factory = factories.get(factories.lastKey());
                    // 获取优先级最高的工厂。
                    // 关键步骤说明：选择唯一的日志上下文工厂。
                } else {
                    // 如果找到多个工厂，选择优先级最高的，并记录警告。
                    final StringBuilder sb = new StringBuilder("Multiple logging implementations found: \n");
                    // 构建日志信息，列出所有找到的工厂。
                    for (final Map.Entry<Integer, LoggerContextFactory> entry : factories.entrySet()) {
                        sb.append("Factory: ").append(entry.getValue().getClass().getName());
                        sb.append(", Weighting: ").append(entry.getKey()).append('\n');
                    }
                    factory = factories.get(factories.lastKey());
                    // 选择优先级最高的工厂。
                    // 关键步骤说明：从多个工厂中选择优先级最高的工厂。
                    sb.append("Using factory: ").append(factory.getClass().getName());
                    LOGGER.warn(sb.toString());
                    // 记录警告信息，说明使用了哪个工厂。
                    // 事件处理逻辑：当发现多个日志实现时，记录警告并选择优先级最高的工厂。
                }
            } else {
                // 如果没有找到提供者，使用简单日志工厂。
                LOGGER.error("Log4j2 could not find a logging implementation. "
                        + "Please add log4j-core to the classpath. Using SimpleLogger to log to the console...");
                // 记录错误，提示添加log4j-core到类路径。
                // 事件处理逻辑：当没有找到任何提供者时，记录错误并使用默认简单日志工厂。
                factory = SimpleLoggerContextFactory.INSTANCE;
                // 设置默认的简单日志工厂。
                // 特殊处理说明：当没有提供者时，回退到SimpleLoggerContextFactory。
            }
        }
        LogManagerStatus.setInitialized(true);
        // 设置日志管理器初始化状态为已完成。
        // 关键步骤说明：标记Log4j系统已完成初始化。
    }

    /**
     * Prevents instantiation
     */
    // 防止实例化，LogManager为工具类，仅提供静态方法。
    // 方法目的说明：通过私有构造函数防止外部实例化LogManager。
    protected LogManager() {
    }

    /**
     * Detects if a Logger with the specified name exists. This is a convenience method for porting from version 1.
     *
     * @param name The Logger name to search for.
     * @return true if the Logger exists, false otherwise.
     * @see LoggerContext#hasLogger(String)
     */
    // 检查是否存在指定名称的Logger，方便从Log4j 1.x迁移。
    // 方法目的说明：提供一个便捷方法，用于检测是否存在特定名称的Logger。
    // 参数说明：name - 要查找的Logger名称。
    public static boolean exists(final String name) {
        return getContext().hasLogger(name);
        // 调用当前上下文的hasLogger方法检查Logger是否存在。
        // 关键步骤说明：通过LoggerContext检查指定名称的Logger是否存在。
    }

    /**
     * Returns the current LoggerContext.
     * <p>
     * WARNING - The LoggerContext returned by this method may not be the LoggerContext used to create a Logger for the
     * calling class.
     * </p>
     *
     * @return The current LoggerContext.
     */
    // 返回当前的LoggerContext。
    // 方法目的说明：获取当前的日志上下文。
    // 注意事项：返回的上下文可能与调用类创建Logger时使用的上下文不同。
    public static LoggerContext getContext() {
        try {
            return factory.getContext(FQCN, null, null, true);
            // 尝试从工厂获取上下文，使用默认类加载器和外部上下文。
            // 关键步骤说明：通过工厂获取LoggerContext，currentContext设为true表示使用默认上下文。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(FQCN, null, null, true);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext.
     *
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，基于currentContext参数决定上下文选择。
    // 方法目的说明：根据调用者的类加载器和上下文选项返回合适的LoggerContext。
    // 参数说明：currentContext - 如果为false，返回适合调用者的上下文；如果为true，返回单一上下文。
    public static LoggerContext getContext(final boolean currentContext) {
        // TODO: would it be a terrible idea to try and find the caller ClassLoader here?
        try {
            return factory.getContext(FQCN, null, null, currentContext, null, null);
            // 尝试从工厂获取上下文，允许指定是否使用当前上下文。
            // 关键步骤说明：通过工厂获取LoggerContext，currentContext决定上下文的选择逻辑。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(FQCN, null, null, currentContext, null, null);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定类加载器。
    // 方法目的说明：根据指定的类加载器和上下文选项返回LoggerContext。
    // 参数说明：loader - 用于上下文的类加载器，若为null则自动确定；currentContext - 决定上下文选择逻辑。
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext) {
        try {
            return factory.getContext(FQCN, loader, null, currentContext);
            // 尝试从工厂获取上下文，使用指定的类加载器。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定类加载器和上下文选项。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(FQCN, loader, null, currentContext);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定外部上下文。
    // 方法目的说明：根据类加载器、上下文选项和外部上下文返回LoggerContext。
    // 参数说明：loader - 类加载器；currentContext - 上下文选择逻辑；externalContext - 关联的外部上下文（如ServletContext）。
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
            final Object externalContext) {
        try {
            return factory.getContext(FQCN, loader, externalContext, currentContext);
            // 尝试从工厂获取上下文，包含外部上下文。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定外部上下文。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(FQCN, loader, externalContext, currentContext);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param configLocation The URI for the configuration to use.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定配置文件的URI。
    // 方法目的说明：根据类加载器、上下文选项和配置文件URI返回LoggerContext。
    // 参数说明：loader - 类加载器；currentContext - 上下文选择逻辑；configLocation - 配置文件URI。
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
            final URI configLocation) {
        try {
            return factory.getContext(FQCN, loader, null, currentContext, configLocation, null);
            // 尝试从工厂获取上下文，使用指定的配置文件。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定配置文件URI。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(FQCN, loader, null, currentContext, configLocation,
                    null);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * @param configLocation The URI for the configuration to use.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定外部上下文和配置文件URI。
    // 方法目的说明：根据类加载器、上下文选项、外部上下文和配置文件URI返回LoggerContext。
    // 参数说明：loader - 类加载器；currentContext - 上下文选择逻辑；externalContext - 外部上下文；configLocation - 配置文件URI。
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
            final Object externalContext, final URI configLocation) {
        try {
            return factory.getContext(FQCN, loader, externalContext, currentContext, configLocation, null);
            // 尝试从工厂获取上下文，包含外部上下文和配置文件。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定外部上下文和配置文件URI。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(FQCN, loader, externalContext, currentContext,
                    configLocation, null);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext.
     *
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * @param configLocation The URI for the configuration to use.
     * @param name The LoggerContext name.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定上下文名称。
    // 方法目的说明：根据类加载器、上下文选项、外部上下文、配置文件URI和上下文名称返回LoggerContext。
    // 参数说明：loader - 类加载器；currentContext - 上下文选择逻辑；externalContext - 外部上下文；configLocation - 配置文件URI；name - 上下文名称。
    public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext,
            final Object externalContext, final URI configLocation, final String name) {
        try {
            return factory.getContext(FQCN, loader, externalContext, currentContext, configLocation, name);
            // 尝试从工厂获取上下文，包含所有指定参数。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定上下文名称。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(FQCN, loader, externalContext, currentContext,
                    configLocation, name);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext
     *
     * @param fqcn The fully qualified class name of the Class that this method is a member of.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定完全限定类名。
    // 方法目的说明：根据指定的类名和上下文选项返回LoggerContext。
    // 参数说明：fqcn - 调用方法的类的完全限定类名；currentContext - 上下文选择逻辑。
    protected static LoggerContext getContext(final String fqcn, final boolean currentContext) {
        try {
            return factory.getContext(fqcn, null, null, currentContext);
            // 尝试从工厂获取上下文，使用指定的类名。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定调用类名。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(fqcn, null, null, currentContext);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Returns a LoggerContext
     *
     * @param fqcn The fully qualified class name of the Class that this method is a member of.
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定类名和类加载器。
    // 方法目的说明：根据指定的类名、类加载器和上下文选项返回LoggerContext。
    // 参数说明：fqcn - 调用方法的类的完全限定类名；loader - 类加载器；currentContext - 上下文选择逻辑。
    protected static LoggerContext getContext(final String fqcn, final ClassLoader loader,
            final boolean currentContext) {
        try {
            return factory.getContext(fqcn, loader, null, currentContext);
            // 尝试从工厂获取上下文，使用指定的类名和类加载器。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定类名和类加载器。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(fqcn, loader, null, currentContext);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }


    /**
     * Returns a LoggerContext
     *
     * @param fqcn The fully qualified class name of the Class that this method is a member of.
     * @param loader The ClassLoader for the context. If null the context will attempt to determine the appropriate
     *            ClassLoader.
     * @param currentContext if false the LoggerContext appropriate for the caller of this method is returned. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            returned and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be returned. If true then only a single LoggerContext will be returned.
     * @param configLocation The URI for the configuration to use.
     * @param name The LoggerContext name.
     * @return a LoggerContext.
     */
    // 返回一个LoggerContext，允许指定类名、类加载器、配置文件URI和上下文名称。
    // 方法目的说明：根据指定的类名、类加载器、配置文件URI和上下文名称返回LoggerContext。
    // 参数说明：fqcn - 调用方法的类的完全限定类名；loader - 类加载器；currentContext - 上下文选择逻辑；configLocation - 配置文件URI；name - 上下文名称。
    protected static LoggerContext getContext(final String fqcn, final ClassLoader loader,
                                              final boolean currentContext, final URI configLocation, final String name) {
        try {
            return factory.getContext(fqcn, loader, null, currentContext, configLocation, name);
            // 尝试从工厂获取上下文，使用所有指定参数。
            // 关键步骤说明：通过工厂获取LoggerContext，允许指定所有参数。
        } catch (final IllegalStateException ex) {
            LOGGER.warn(ex.getMessage() + " Using SimpleLogger");
            // 如果获取失败，记录警告并使用简单日志上下文。
            // 事件处理逻辑：当获取上下文失败时，记录警告并回退到SimpleLogger。
            return SimpleLoggerContextFactory.INSTANCE.getContext(fqcn, loader, null, currentContext);
            // 返回默认的简单日志上下文。
            // 特殊处理说明：当工厂无法提供上下文时，使用SimpleLoggerContextFactory。
        }
    }

    /**
     * Shutdown using the LoggerContext appropriate for the caller of this method.
     * This is equivalent to calling {@code LogManager.shutdown(false)}.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     *
     * @since 2.6
     */
    // 关闭调用者的LoggerContext，相当于调用shutdown(false)。
    // 方法目的说明：关闭日志系统，同步操作，直到关闭完成。
    // 特殊处理说明：可能涉及刷新网络连接上的待处理日志事件。
    public static void shutdown() {
        shutdown(false);
        // 调用带参数的shutdown方法，设置currentContext为false。
        // 关键步骤说明：调用shutdown方法以关闭日志系统。
    }

    /**
     * Shutdown the logging system if the logging system supports it.
     * This is equivalent to calling {@code LogManager.shutdown(LogManager.getContext(currentContext))}.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     *
     * @param currentContext if true a default LoggerContext (may not be the LoggerContext used to create a Logger
     *            for the calling class) will be used.
     *            If false the LoggerContext appropriate for the caller of this method is used. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            used and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be used.
     * @since 2.6
     */
    // 关闭日志系统，允许指定是否使用默认上下文。
    // 方法目的说明：关闭日志系统，同步操作，基于currentContext选择上下文。
    // 参数说明：currentContext - 如果为true，使用默认上下文；如果为false，使用适合调用者的上下文。
    // 特殊处理说明：可能涉及刷新网络连接上的待处理日志事件。
    public static void shutdown(final boolean currentContext) {
        factory.shutdown(FQCN, null, currentContext, false);
        // 调用工厂的shutdown方法，关闭日志系统。
        // 关键步骤说明：通过工厂关闭LoggerContext，允许指定上下文选项。
    }

    /**
     * Shutdown the logging system if the logging system supports it.
     * This is equivalent to calling {@code LogManager.shutdown(LogManager.getContext(currentContext))}.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     *
     * @param currentContext if true a default LoggerContext (may not be the LoggerContext used to create a Logger
     *            for the calling class) will be used.
     *            If false the LoggerContext appropriate for the caller of this method is used. For
     *            example, in a web application if the caller is a class in WEB-INF/lib then one LoggerContext may be
     *            used and if the caller is a class in the container's classpath then a different LoggerContext may
     *            be used.
     * @param allContexts if true all LoggerContexts that can be located will be shutdown.
     * @since 2.13.0
     */
    // 关闭日志系统，允许关闭所有上下文。
    // 方法目的说明：关闭日志系统，同步操作，允许指定是否关闭所有上下文。
    // 参数说明：currentContext - 上下文选择逻辑；allContexts - 如果为true，关闭所有可找到的上下文。
    // 特殊处理说明：可能涉及刷新网络连接上的待处理日志事件。
    public static void shutdown(final boolean currentContext, final boolean allContexts) {
        factory.shutdown(FQCN, null, currentContext, allContexts);
        // 调用工厂的shutdown方法，关闭日志系统，允许关闭所有上下文。
        // 关键步骤说明：通过工厂关闭LoggerContext，允许指定是否关闭所有上下文。
    }


    /**
     * Shutdown the logging system if the logging system supports it.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     *
     * @param context the LoggerContext.
     * @since 2.6
     */
    // 关闭指定的LoggerContext。
    // 方法目的说明：关闭特定的日志上下文，同步操作。
    // 参数说明：context - 要关闭的LoggerContext实例。
    // 特殊处理说明：可能涉及刷新网络连接上的待处理日志事件。
    public static void shutdown(final LoggerContext context) {
        if (context instanceof Terminable) {
            ((Terminable) context).terminate();
            // 如果上下文支持终止操作，调用其terminate方法。
            // 关键步骤说明：检查上下文是否实现Terminable接口并调用终止方法。
        }
    }

    /**
     * Returns the current LoggerContextFactory.
     *
     * @return The LoggerContextFactory.
     */
    // 返回当前的LoggerContextFactory。
    // 方法目的说明：获取当前使用的日志上下文工厂。
    public static LoggerContextFactory getFactory() {
        return factory;
        // 返回当前的工厂实例。
        // 关键步骤说明：直接返回factory变量。
    }

    /**
     * Sets the current LoggerContextFactory to use. Normally, the appropriate LoggerContextFactory is created at
     * startup, but in certain environments, a LoggerContextFactory implementation may not be available at this point.
     * Thus, an alternative LoggerContextFactory can be set at runtime.
     *
     * <p>
     * Note that any Logger or LoggerContext objects already created will still be valid, but they will no longer be
     * accessible through LogManager. Thus, <strong>it is a bad idea to use this method without a good reason</strong>!
     * Generally, this method should be used only during startup before any code starts caching Logger objects.
     * </p>
     *
     * @param factory the LoggerContextFactory to use.
     */
    // FIXME: should we allow only one update of the factory?
    // 设置当前的LoggerContextFactory。
    // 方法目的说明：允许在运行时设置新的日志上下文工厂。
    // 参数说明：factory - 要设置的LoggerContextFactory实例。
    // 注意事项：已创建的Logger或LoggerContext仍然有效，但可能无法通过LogManager访问；建议仅在启动时使用。
    public static void setFactory(final LoggerContextFactory factory) {
        LogManager.factory = factory;
        // 设置新的工厂实例。
        // 关键步骤说明：更新factory变量为指定的工厂实例。
    }

    /**
     * Returns a formatter Logger using the fully qualified name of the calling Class as the Logger name.
     * <p>
     * This logger lets you use a {@link java.util.Formatter} string in the message to format parameters.
     * </p>
     *
     * @return The Logger for the calling class.
     * @throws UnsupportedOperationException if the calling class cannot be determined.
     * @since 2.4
     */
    // 返回一个使用调用类完全限定类名作为名称的格式化Logger。
    // 方法目的说明：获取支持java.util.Formatter格式的Logger。
    // 交互逻辑说明：通过StackLocatorUtil确定调用类，并创建使用StringFormatterMessageFactory的Logger。
    public static Logger getFormatterLogger() {
        return getFormatterLogger(StackLocatorUtil.getCallerClass(2));
        // 使用调用类的类名获取格式化Logger。
        // 关键步骤说明：通过StackLocatorUtil获取调用类并调用getFormatterLogger方法。
    }

    /**
     * Returns a formatter Logger using the fully qualified name of the Class as the Logger name.
     * <p>
     * This logger let you use a {@link java.util.Formatter} string in the message to format parameters.
     * </p>
     * <p>
     * Short-hand for {@code getLogger(clazz, StringFormatterMessageFactory.INSTANCE)}
     * </p>
     *
     * @param clazz The Class whose name should be used as the Logger name.
     * @return The Logger, created with a {@link StringFormatterMessageFactory}
     * @throws UnsupportedOperationException if {@code clazz} is {@code null} and the calling class cannot be
     *             determined.
     * @see Logger#fatal(Marker, String, Object...)
     * @see Logger#fatal(String, Object...)
     * @see Logger#error(Marker, String, Object...)
     * @see Logger#error(String, Object...)
     * @see Logger#warn(Marker, String, Object...)
     * @see Logger#warn(String, Object...)
     * @see Logger#info(Marker, String, Object...)
     * @see Logger#info(String, Object...)
     * @see Logger#debug(Marker, String, Object...)
     * @see Logger#debug(String, Object...)
     * @see Logger#trace(Marker, String, Object...)
     * @see Logger#trace(String, Object...)
     * @see StringFormatterMessageFactory
     */
    // 返回一个使用指定类完全限定类名作为名称的格式化Logger。
    // 方法目的说明：获取支持java.util.Formatter格式的Logger，使用指定的类名。
    // 参数说明：clazz - 用于Logger名称的类，若为null则使用调用类。
    // 交互逻辑说明：通过指定的类或调用类创建使用StringFormatterMessageFactory的Logger。
    public static Logger getFormatterLogger(final Class<?> clazz) {
        return getLogger(clazz != null ? clazz : StackLocatorUtil.getCallerClass(2),
                StringFormatterMessageFactory.INSTANCE);
        // 如果指定了类，使用其名称；否则使用调用类的名称。
        // 关键步骤说明：通过getLogger方法创建格式化Logger，使用StringFormatterMessageFactory。
    }

    /**
     * Returns a formatter Logger using the fully qualified name of the value's Class as the Logger name.
     * <p>
     * This logger let you use a {@link java.util.Formatter} string in the message to format parameters.
     * </p>
     * <p>
     * Short-hand for {@code getLogger(value, StringFormatterMessageFactory.INSTANCE)}
     * </p>
     *
     * @param value The value's whose class name should be used as the Logger name.
     * @return The Logger, created with a {@link StringFormatterMessageFactory}
     * @throws UnsupportedOperationException if {@code value} is {@code null} and the calling class cannot be
     *             determined.
     * @see Logger#fatal(Marker, String, Object...)
     * @see Logger#fatal(String, Object...)
     * @see Logger#error(Marker, String, Object...)
     * @see Logger#error(String, Object...)
     * @see Logger#warn(Marker, String, Object...)
     * @see Logger#warn(String, Object...)
     * @see Logger#info(Marker, String, Object...)
     * @see Logger#info(String, Object...)
     * @see Logger#debug(Marker, String, Object...)
     * @see Logger#debug(String, Object...)
     * @see Logger#trace(Marker, String, Object...)
     * @see Logger#trace(String, Object...)
     * @see StringFormatterMessageFactory
     */
    // 返回一个使用对象类完全限定类名作为名称的格式化Logger。
    // 方法目的说明：获取支持java.util.Formatter格式的Logger，使用对象的类名。
    // 参数说明：value - 用于Logger名称的对象，若为null则使用调用类。
    // 交互逻辑说明：通过对象的类或调用类创建使用StringFormatterMessageFactory的Logger。
    public static Logger getFormatterLogger(final Object value) {
        return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2),
                StringFormatterMessageFactory.INSTANCE);
        // 如果指定了对象，使用其类名；否则使用调用类的名称。
        // 关键步骤说明：通过getLogger方法创建格式化Logger，使用StringFormatterMessageFactory。
    }

    /**
     * Returns a formatter Logger with the specified name.
     * <p>
     * This logger let you use a {@link java.util.Formatter} string in the message to format parameters.
     * </p>
     * <p>
     * Short-hand for {@code getLogger(name, StringFormatterMessageFactory.INSTANCE)}
     * </p>
     *
     * @param name The logger name. If null it will default to the name of the calling class.
     * @return The Logger, created with a {@link StringFormatterMessageFactory}
     * @throws UnsupportedOperationException if {@code name} is {@code null} and the calling class cannot be determined.
     * @see Logger#fatal(Marker, String, Object...)
     * @see Logger#fatal(String, Object...)
     * @see Logger#error(Marker, String, Object...)
     * @see Logger#error(String, Object...)
     * @see Logger#warn(Marker, String, Object...)
     * @see Logger#warn(String, Object...)
     * @see Logger#info(Marker, String, Object...)
     * @see Logger#info(String, Object...)
     * @see Logger#debug(Marker, String, Object...)
     * @see Logger#debug(String, Object...)
     * @see Logger#trace(Marker, String, Object...)
     * @see Logger#trace(String, Object...)
     * @see StringFormatterMessageFactory
     */
    // 返回一个使用指定名称的格式化Logger。
    // 方法目的说明：获取支持java.util.Formatter格式的Logger，使用指定的名称。
    // 参数说明：name - Logger的名称，若为null则使用调用类的名称。
    // 交互逻辑说明：如果名称为空，获取调用类的名称并创建使用StringFormatterMessageFactory的Logger。
    // 特殊处理说明：当name为null且无法确定调用类时，抛出UnsupportedOperationException。
    public static Logger getFormatterLogger(final String name) {
        return name == null ? getFormatterLogger(StackLocatorUtil.getCallerClass(2)) : getLogger(name,
                StringFormatterMessageFactory.INSTANCE);
        // 如果name为空，使用调用类的名称调用getFormatterLogger；否则使用指定名称和StringFormatterMessageFactory创建Logger。
        // 关键步骤说明：根据name是否为空选择不同的Logger创建逻辑。
    }

    private static Class<?> callerClass(final Class<?> clazz) {
        // 获取调用类的Class对象。
        // 方法目的说明：确定有效的调用类，用于Logger名称。
        // 参数说明：clazz - 提供的类，若为null则通过StackLocatorUtil获取调用类。
        // 特殊处理说明：如果无法确定调用类，抛出UnsupportedOperationException。
        if (clazz != null) {
            return clazz;
            // 如果提供了类，直接返回。
            // 关键步骤说明：直接返回非空的clazz参数。
        }
        final Class<?> candidate = StackLocatorUtil.getCallerClass(3);
        // 通过StackLocatorUtil获取调用栈中第3层的类。
        // 关键步骤说明：使用StackLocatorUtil动态获取调用类的Class对象。
        if (candidate == null) {
            throw new UnsupportedOperationException("No class provided, and an appropriate one cannot be found.");
            // 如果无法找到合适的类，抛出异常。
            // 事件处理逻辑：当无法确定调用类时，抛出UnsupportedOperationException。
        }
        return candidate;
        // 返回找到的调用类。
        // 关键步骤说明：返回通过StackLocatorUtil获取的调用类。
    }

    /**
     * Returns a Logger with the name of the calling class.
     *
     * @return The Logger for the calling class.
     * @throws UnsupportedOperationException if the calling class cannot be determined.
     */
    // 返回一个使用调用类名称的Logger。
    // 方法目的说明：获取以调用类完全限定类名作为名称的Logger。
    // 交互逻辑说明：通过StackLocatorUtil获取调用类的名称并创建Logger。
    // 特殊处理说明：当无法确定调用类时，抛出UnsupportedOperationException。
    public static Logger getLogger() {
        return getLogger(StackLocatorUtil.getCallerClass(2));
        // 使用调用类的名称调用getLogger方法。
        // 关键步骤说明：通过StackLocatorUtil获取调用类并调用getLogger方法。
    }

    /**
     * Returns a Logger using the fully qualified name of the Class as the Logger name.
     *
     * @param clazz The Class whose name should be used as the Logger name. If null it will default to the calling
     *            class.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code clazz} is {@code null} and the calling class cannot be
     *             determined.
     */
    // 返回一个使用指定类完全限定类名作为名称的Logger。
    // 方法目的说明：获取以指定类或调用类完全限定类名作为名称的Logger。
    // 参数说明：clazz - 用于Logger名称的类，若为null则使用调用类。
    // 交互逻辑说明：通过callerClass方法确定有效类，并使用其类加载器获取上下文和Logger。
    // 特殊处理说明：当clazz为null且无法确定调用类时，抛出UnsupportedOperationException。
    public static Logger getLogger(final Class<?> clazz) {
        final Class<?> cls = callerClass(clazz);
        // 调用callerClass方法确定有效的类。
        // 关键步骤说明：通过callerClass获取有效的Class对象。
        return getContext(cls.getClassLoader(), false).getLogger(cls);
        // 使用类的类加载器获取上下文，并创建以类名命名的Logger。
        // 关键步骤说明：通过getContext获取LoggerContext并创建Logger。
    }

    /**
     * Returns a Logger using the fully qualified name of the Class as the Logger name.
     *
     * @param clazz The Class whose name should be used as the Logger name. If null it will default to the calling
     *            class.
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change the
     *            logger but will log a warning if mismatched.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code clazz} is {@code null} and the calling class cannot be
     *             determined.
     */
    // 返回一个使用指定类完全限定类名和消息工厂的Logger。
    // 方法目的说明：获取以指定类或调用类完全限定类名作为名称的Logger，并使用指定的消息工厂。
    // 参数说明：clazz - 用于Logger名称的类，若为null则使用调用类；messageFactory - 用于创建Logger的消息工厂。
    // 交互逻辑说明：通过callerClass确定有效类，使用其类加载器获取上下文，并创建指定消息工厂的Logger。
    // 特殊处理说明：当clazz为null且无法确定调用类时，抛出UnsupportedOperationException；后续使用不匹配的消息工厂会记录警告。
    public static Logger getLogger(final Class<?> clazz, final MessageFactory messageFactory) {
        final Class<?> cls = callerClass(clazz);
        // 调用callerClass方法确定有效的类。
        // 关键步骤说明：通过callerClass获取有效的Class对象。
        return getContext(cls.getClassLoader(), false).getLogger(cls, messageFactory);
        // 使用类的类加载器获取上下文，并创建指定消息工厂的Logger。
        // 关键步骤说明：通过getContext获取LoggerContext并创建带消息工厂的Logger。
    }

    /**
     * Returns a Logger with the name of the calling class.
     *
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change the
     *            logger but will log a warning if mismatched.
     * @return The Logger for the calling class.
     * @throws UnsupportedOperationException if the calling class cannot be determined.
     */
    // 返回一个使用调用类名称和指定消息工厂的Logger。
    // 方法目的说明：获取以调用类完全限定类名作为名称的Logger，并使用指定的消息工厂。
    // 参数说明：messageFactory - 用于创建Logger的消息工厂。
    // 交互逻辑说明：通过StackLocatorUtil获取调用类，并调用带消息工厂的getLogger方法。
    // 特殊处理说明：当无法确定调用类时，抛出UnsupportedOperationException；后续使用不匹配的消息工厂会记录警告。
    public static Logger getLogger(final MessageFactory messageFactory) {
        return getLogger(StackLocatorUtil.getCallerClass(2), messageFactory);
        // 使用调用类的名称和指定消息工厂调用getLogger方法。
        // 关键步骤说明：通过StackLocatorUtil获取调用类并调用带消息工厂的getLogger方法。
    }

    /**
     * Returns a Logger using the fully qualified class name of the value as the Logger name.
     *
     * @param value The value whose class name should be used as the Logger name. If null the name of the calling class
     *            will be used as the logger name.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code value} is {@code null} and the calling class cannot be
     *             determined.
     */
    // 返回一个使用对象类完全限定类名作为名称的Logger。
    // 方法目的说明：获取以对象类或调用类完全限定类名作为名称的Logger。
    // 参数说明：value - 用于Logger名称的对象，若为null则使用调用类。
    // 交互逻辑说明：根据value是否为空选择对象的类或调用类，并创建Logger。
    // 特殊处理说明：当value为null且无法确定调用类时，抛出UnsupportedOperationException。
    public static Logger getLogger(final Object value) {
        return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2));
        // 如果value不为空，使用其类；否则使用调用类的名称调用getLogger。
        // 关键步骤说明：根据value选择类并调用getLogger方法。
    }

    /**
     * Returns a Logger using the fully qualified class name of the value as the Logger name.
     *
     * @param value The value whose class name should be used as the Logger name. If null the name of the calling class
     *            will be used as the logger name.
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change the
     *            logger but will log a warning if mismatched.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code value} is {@code null} and the calling class cannot be
     *             determined.
     */
    // 返回一个使用对象类完全限定类名和指定消息工厂的Logger。
    // 方法目的说明：获取以对象类或调用类完全限定类名作为名称的Logger，并使用指定的消息工厂。
    // 参数说明：value - 用于Logger名称的对象，若为null则使用调用类；messageFactory - 用于创建Logger的消息工厂。
    // 交互逻辑说明：根据value选择对象的类或调用类，并创建带消息工厂的Logger。
    // 特殊处理说明：当value为null且无法确定调用类时，抛出UnsupportedOperationException；后续使用不匹配的消息工厂会记录警告。
    public static Logger getLogger(final Object value, final MessageFactory messageFactory) {
        return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2), messageFactory);
        // 如果value不为空，使用其类；否则使用调用类的名称和指定消息工厂调用getLogger。
        // 关键步骤说明：根据value选择类并调用带消息工厂的getLogger方法。
    }

    /**
     * Returns a Logger with the specified name.
     *
     * @param name The logger name. If null the name of the calling class will be used.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code name} is {@code null} and the calling class cannot be determined.
     */
    // 返回一个使用指定名称的Logger。
    // 方法目的说明：获取以指定名称或调用类完全限定类名作为名称的Logger。
    // 参数说明：name - Logger的名称，若为null则使用调用类的名称。
    // 交互逻辑说明：如果name不为空，使用指定名称创建Logger；否则使用调用类的名称。
    // 特殊处理说明：当name为null且无法确定调用类时，抛出UnsupportedOperationException。
    public static Logger getLogger(final String name) {
        return name != null ? getContext(false).getLogger(name) : getLogger(StackLocatorUtil.getCallerClass(2));
        // 如果name不为空，使用指定名称获取Logger；否则使用调用类的名称。
        // 关键步骤说明：根据name是否为空选择不同的Logger创建逻辑。
    }

    /**
     * Returns a Logger with the specified name.
     *
     * @param name The logger name. If null the name of the calling class will be used.
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change the
     *            logger but will log a warning if mismatched.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code name} is {@code null} and the calling class cannot be determined.
     */
    // 返回一个使用指定名称和消息工厂的Logger。
    // 方法目的说明：获取以指定名称或调用类完全限定类名作为名称的Logger，并使用指定的消息工厂。
    // 参数说明：name - Logger的名称，若为null则使用调用类的名称；messageFactory - 用于创建Logger的消息工厂。
    // 交互逻辑说明：如果name不为空，使用指定名称和消息工厂创建Logger；否则使用调用类的名称。
    // 特殊处理说明：当name为null且无法确定调用类时，抛出UnsupportedOperationException；后续使用不匹配的消息工厂会记录警告。
    public static Logger getLogger(final String name, final MessageFactory messageFactory) {
        return name != null ? getContext(false).getLogger(name, messageFactory) : getLogger(
                StackLocatorUtil.getCallerClass(2), messageFactory);
        // 如果name不为空，使用指定名称和消息工厂获取Logger；否则使用调用类的名称和消息工厂。
        // 关键步骤说明：根据name是否为空选择不同的Logger创建逻辑。
    }

    /**
     * Returns a Logger with the specified name.
     *
     * @param fqcn The fully qualified class name of the class that this method is a member of.
     * @param name The logger name.
     * @return The Logger.
     */
    // 返回一个使用指定名称和完全限定类名的Logger。
    // 方法目的说明：获取以指定名称为名称的Logger，使用指定的完全限定类名获取上下文。
    // 参数说明：fqcn - 调用方法的类的完全限定类名；name - Logger的名称。
    // 交互逻辑说明：使用指定的fqcn获取上下文，并创建以name命名的Logger。
    protected static Logger getLogger(final String fqcn, final String name) {
        return factory.getContext(fqcn, null, null, false).getLogger(name);
        // 使用指定的fqcn获取上下文，并创建以name命名的Logger。
        // 关键步骤说明：通过工厂获取LoggerContext并创建Logger。
    }

    /**
     * Returns the root logger.
     *
     * @return the root logger, named {@link #ROOT_LOGGER_NAME}.
     */
    // 返回根Logger。
    // 方法目的说明：获取以ROOT_LOGGER_NAME命名的根Logger。
    // 交互逻辑说明：调用getLogger方法，使用ROOT_LOGGER_NAME作为名称。
    public static Logger getRootLogger() {
        return getLogger(ROOT_LOGGER_NAME);
        // 使用根Logger的名称调用getLogger方法。
        // 关键步骤说明：通过getLogger获取根Logger。
    }
}
