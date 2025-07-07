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
 * Apache软件基金会（ASF）许可声明，说明本文件的版权归属和使用许可。
 * 本文件遵循Apache 2.0许可协议，未经许可不得使用。
 * 软件以“原样”提供，不附带任何明示或暗示的担保。
 */

package org.apache.logging.log4j.spi;
// 包声明：定义该类属于Log4j的SPI（Service Provider Interface）模块，用于实现日志服务提供者接口。

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Model class for a Log4j 2 provider. The properties in this class correspond to the properties used in a
 * {@code META-INF/log4j-provider.properties} file. Note that this class is automatically created by Log4j and should
 * not be used by providers.
 */
/*
 * 类说明：Provider类是Log4j 2提供者的模型类，用于封装日志服务提供者的配置信息。
 * 主要功能：解析和存储META-INF/log4j-provider.properties文件中的属性，供Log4j框架使用。
 * 注意事项：该类由Log4j框架自动创建，日志提供者不应直接使用此类。
 */

public class Provider {
    /**
     * Property name to set for a Log4j 2 provider to specify the priority of this implementation.
     */
    /*
     * 常量说明：FACTORY_PRIORITY定义了日志提供者的优先级属性名称。
     * 用途：在log4j-provider.properties文件中设置提供者的优先级，决定加载时的优先顺序。
     */
    public static final String FACTORY_PRIORITY = "FactoryPriority";
    /**
     * Property name to set to the implementation of {@link org.apache.logging.log4j.spi.ThreadContextMap}.
     */
    /*
     * 常量说明：THREAD_CONTEXT_MAP定义了线程上下文映射的实现类属性名称。
     * 用途：指定ThreadContextMap接口的具体实现类，用于线程上下文数据的存储和管理。
     */
    public static final String THREAD_CONTEXT_MAP = "ThreadContextMap";
    /**
     * Property name to set to the implementation of {@link org.apache.logging.log4j.spi.LoggerContextFactory}.
     */
    /*
     * 常量说明：LOGGER_CONTEXT_FACTORY定义了日志上下文工厂的实现类属性名称。
     * 用途：指定LoggerContextFactory接口的具体实现类，用于创建和管理日志上下文。
     */
    public static final String LOGGER_CONTEXT_FACTORY = "LoggerContextFactory";

    private static final Integer DEFAULT_PRIORITY = Integer.valueOf(-1);
    // 默认优先级：当未指定优先级时，使用-1作为默认值。

    private static final Logger LOGGER = StatusLogger.getLogger();
    // 日志记录器：使用StatusLogger获取日志实例，用于记录Provider类的错误和状态信息。

    private final Integer priority;
    // 优先级变量：存储提供者的优先级，决定加载顺序，值越大优先级越高。

    private final String className;
    // 类名变量：存储LoggerContextFactory实现类的名称，从配置文件读取。

    private final Class<? extends LoggerContextFactory> loggerContextFactoryClass;
    // 日志上下文工厂类：存储LoggerContextFactory的具体实现类，供动态加载使用。

    private final String threadContextMap;
    // 线程上下文映射类名：存储ThreadContextMap实现类的名称，从配置文件读取。

    private final Class<? extends ThreadContextMap> threadContextMapClass;
    // 线程上下文映射类：存储ThreadContextMap的具体实现类，供动态加载使用。

    private final String versions;
    // 版本信息：存储提供者支持的Log4j API版本。

    private final URL url;
    // 配置文件URL：存储log4j-provider.properties文件的URL地址。

    private final WeakReference<ClassLoader> classLoader;
    // 类加载器引用：使用弱引用存储类加载器，避免内存泄漏。

    /**
     * 构造函数：从配置文件和类加载器初始化Provider实例。
     * 参数：
     *   - props：Properties对象，包含log4j-provider.properties文件的配置项。
     *   - url：配置文件所在的URL地址。
     *   - classLoader：用于加载类的ClassLoader实例。
     * 执行流程：
     *   1. 存储URL和类加载器（使用弱引用）。
     *   2. 从props中读取FACTORY_PRIORITY属性，设置优先级，默认为-1。
     *   3. 从props中读取LOGGER_CONTEXT_FACTORY和THREAD_CONTEXT_MAP属性，存储类名。
     *   4. 初始化loggerContextFactoryClass和threadContextMapClass为null，延迟加载。
     */
    public Provider(final Properties props, final URL url, final ClassLoader classLoader) {
        this.url = url;
        this.classLoader = new WeakReference<>(classLoader);
        final String weight = props.getProperty(FACTORY_PRIORITY);
        priority = weight == null ? DEFAULT_PRIORITY : Integer.valueOf(weight);
        className = props.getProperty(LOGGER_CONTEXT_FACTORY);
        threadContextMap = props.getProperty(THREAD_CONTEXT_MAP);
        loggerContextFactoryClass = null;
        threadContextMapClass = null;
        versions = null;
    }

    /**
     * 构造函数：通过指定的优先级、版本和日志上下文工厂类初始化Provider实例。
     * 参数：
     *   - priority：提供者的优先级。
     *   - versions：支持的Log4j API版本。
     *   - loggerContextFactoryClass：LoggerContextFactory的具体实现类。
     * 执行流程：
     *   1. 存储优先级、版本和日志上下文工厂类。
     *   2. 初始化其他字段为null。
     */
    public Provider(final Integer priority, final String versions,
                    final Class<? extends LoggerContextFactory> loggerContextFactoryClass) {
        this(priority, versions, loggerContextFactoryClass, null);
    }

    /**
     * 构造函数：通过指定的优先级、版本、日志上下文工厂类和线程上下文映射类初始化Provider实例。
     * 参数：
     *   - priority：提供者的优先级。
     *   - versions：支持的Log4j API版本。
     *   - loggerContextFactoryClass：LoggerContextFactory的具体实现类。
     *   - threadContextMapClass：ThreadContextMap的具体实现类。
     * 执行流程：
     *   1. 存储优先级、版本、日志上下文工厂类和线程上下文映射类。
     *   2. 初始化URL、类加载器和类名为null。
     */
    public Provider(final Integer priority, final String versions,
                    final Class<? extends LoggerContextFactory> loggerContextFactoryClass,
                    final Class<? extends ThreadContextMap> threadContextMapClass) {
        this.url = null;
        this.classLoader = null;
        this.priority = priority;
        this.loggerContextFactoryClass = loggerContextFactoryClass;
        this.threadContextMapClass = threadContextMapClass;
        this.className = null;
        this.threadContextMap = null;
        this.versions = versions;
    }

    /**
     * Returns the Log4j API versions supported by the implementation.
     * @return A String containing the Log4j versions supported.
     */
    /**
     * 返回提供者支持的Log4j API版本。
     * 返回值：包含支持的Log4j版本的字符串。
     * 功能：提供版本信息，供Log4j框架检查兼容性。
     */
    public String getVersions() {
        return versions;
    }

    /**
     * Gets the priority (natural ordering) of this Provider.
     *
     * @return the priority of this Provider
     */
    /**
     * 获取提供者的优先级（自然排序）。
     * 返回值：提供者的优先级值。
     * 功能：用于确定多个提供者之间的加载顺序，优先级越高越先加载。
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Gets the class name of the {@link org.apache.logging.log4j.spi.LoggerContextFactory} implementation of this
     * Provider.
     *
     * @return the class name of a LoggerContextFactory implementation
     */
    /**
     * 获取LoggerContextFactory实现类的类名。
     * 返回值：LoggerContextFactory实现类的名称。
     * 逻辑：
     *   - 如果loggerContextFactoryClass不为空，返回其类名。
     *   - 否则返回从配置文件读取的className。
     * 用途：提供类名信息，用于动态加载或调试。
     */
    public String getClassName() {
        if (loggerContextFactoryClass != null) {
            return loggerContextFactoryClass.getName();
        }
        return className;
    }

    /**
     * Loads the {@link org.apache.logging.log4j.spi.LoggerContextFactory} class specified by this Provider.
     *
     * @return the LoggerContextFactory implementation class or {@code null} if there was an error loading it
     */
    /**
     * 加载LoggerContextFactory实现类。
     * 返回值：LoggerContextFactory实现类对象，若加载失败则返回null。
     * 执行流程：
     *   1. 如果loggerContextFactoryClass不为空，直接返回。
     *   2. 如果className为空，返回null。
     *   3. 获取类加载器，若为空，返回null。
     *   4. 使用类加载器动态加载className指定的类。
     *   5. 检查加载的类是否实现LoggerContextFactory接口，若是则返回其子类。
     *   6. 若加载失败，记录错误日志并返回null。
     * 注意事项：使用try-catch捕获异常，确保加载失败不影响程序运行。
     */
    public Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
        if (loggerContextFactoryClass != null) {
            return loggerContextFactoryClass;
        }
        if (className == null) {
            return null;
        }
        final ClassLoader loader = classLoader.get();
        if (loader == null) {
            return null;
        }
        try {
            final Class<?> clazz = loader.loadClass(className);
            if (LoggerContextFactory.class.isAssignableFrom(clazz)) {
                return clazz.asSubclass(LoggerContextFactory.class);
            }
        } catch (final Exception e) {
            LOGGER.error("Unable to create class {} specified in {}", className, url.toString(), e);
            // 错误处理：记录无法加载指定类的错误信息，包括类名和配置文件URL。
        }
        return null;
    }

    /**
     * Gets the class name of the {@link org.apache.logging.log4j.spi.ThreadContextMap} implementation of this Provider.
     *
     * @return the class name of a ThreadContextMap implementation
     */
    /**
     * 获取ThreadContextMap实现类的类名。
     * 返回值：ThreadContextMap实现类的名称。
     * 逻辑：
     *   - 如果threadContextMapClass不为空，返回其类名。
     *   - 否则返回从配置文件读取的threadContextMap。
     * 用途：提供类名信息，用于动态加载或调试。
     */
    public String getThreadContextMap() {
        if (threadContextMapClass != null) {
            return threadContextMapClass.getName();
        }
        return threadContextMap;
    }

    /**
     * Loads the {@link org.apache.logging.log4j.spi.ThreadContextMap} class specified by this Provider.
     *
     * @return the ThreadContextMap implementation class or {@code null} if there was an error loading it
     */
    /**
     * 加载ThreadContextMap实现类。
     * 返回值：ThreadContextMap实现类对象，若加载失败则返回null。
     * 执行流程：
     *   1. 如果threadContextMapClass不为空，直接返回。
     *   2. 如果threadContextMap为空，返回null。
     *   3. 获取类加载器，若为空，返回null。
     *   4. 使用类加载器动态加载threadContextMap指定的类。
     *   5. 检查加载的类是否实现ThreadContextMap接口，若是则返回其子类。
     *   6. 若加载失败，记录错误日志并返回null。
     * 注意事项：使用try-catch捕获异常，确保加载失败不影响程序运行。
     */
    public Class<? extends ThreadContextMap> loadThreadContextMap() {
        if (threadContextMapClass != null) {
            return threadContextMapClass;
        }
        if (threadContextMap == null) {
            return null;
        }
        final ClassLoader loader = classLoader.get();
        if (loader == null) {
            return null;
        }
        try {
            final Class<?> clazz = loader.loadClass(threadContextMap);
            if (ThreadContextMap.class.isAssignableFrom(clazz)) {
                return clazz.asSubclass(ThreadContextMap.class);
            }
        } catch (final Exception e) {
            LOGGER.error("Unable to create class {} specified in {}", threadContextMap, url.toString(), e);
            // 错误处理：记录无法加载指定类的错误信息，包括类名和配置文件URL。
        }
        return null;
    }

    /**
     * Gets the URL containing this Provider's Log4j details.
     *
     * @return the URL corresponding to the Provider {@code META-INF/log4j-provider.properties} file
     */
    /**
     * 获取提供者的配置文件URL。
     * 返回值：log4j-provider.properties文件的URL地址。
     * 用途：提供配置文件位置信息，用于调试或追踪配置来源。
     */
    public URL getUrl() {
        return url;
    }
    
    /**
     * 重写toString方法，返回Provider的详细信息。
     * 返回值：包含优先级、类名、线程上下文映射、URL和类加载器信息的字符串。
     * 逻辑：
     *   1. 使用StringBuilder构建描述字符串。
     *   2. 如果优先级非默认值，添加优先级信息。
     *   3. 添加threadContextMap或threadContextMapClass信息。
     *   4. 添加className或loggerContextFactoryClass信息。
     *   5. 添加URL信息。
     *   6. 添加类加载器信息，若不可用则显示“null(not reachable)”。
     * 用途：便于调试和日志记录，提供Provider的详细状态。
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("Provider[");
        if (!DEFAULT_PRIORITY.equals(priority)) {
            result.append("priority=").append(priority).append(", ");
        }
        if (threadContextMap != null) {
            result.append("threadContextMap=").append(threadContextMap).append(", ");
        } else if (threadContextMapClass != null) {
            result.append("threadContextMapClass=").append(threadContextMapClass.getName());
        }
        if (className != null) {
            result.append("className=").append(className).append(", ");
        } else if (loggerContextFactoryClass != null) {
            result.append("class=").append(loggerContextFactoryClass.getName());
        }
        if (url != null) {
            result.append("url=").append(url);
        }
        final ClassLoader loader;
        if (classLoader == null || (loader = classLoader.get()) == null) {
            result.append(", classLoader=null(not reachable)");
        } else {
            result.append(", classLoader=").append(loader);
        }
        result.append("]");
        return result.toString();
    }

    /**
     * 重写equals方法，比较两个Provider对象是否相等。
     * 参数：o - 要比较的对象。
     * 返回值：若两个对象相等返回true，否则返回false。
     * 逻辑：
     *   1. 检查是否为同一对象。
     *   2. 检查对象类型和是否为null。
     *   3. 比较priority、className、loggerContextFactoryClass和versions字段。
     * 用途：用于Provider对象的比较，确保配置一致性。
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Provider provider = (Provider) o;

        if (priority != null ? !priority.equals(provider.priority) : provider.priority != null) {
            return false;
        }
        if (className != null ? !className.equals(provider.className) : provider.className != null) {
            return false;
        }
        if (loggerContextFactoryClass != null ? !loggerContextFactoryClass.equals(provider.loggerContextFactoryClass) : provider.loggerContextFactoryClass != null) {
            return false;
        }
        return versions != null ? versions.equals(provider.versions) : provider.versions == null;
    }

    /**
     * 重写hashCode方法，生成Provider对象的哈希值。
     * 返回值：基于priority、className、loggerContextFactoryClass和versions的哈希值。
     * 逻辑：
     *   1. 使用优先级、类名、日志上下文工厂类和版本信息计算哈希值。
     *   2. 使用31作为乘数，确保哈希值分布均匀。
     * 用途：支持Provider对象在集合中的使用（如HashSet、HashMap）。
     */
    @Override
    public int hashCode() {
        int result = priority != null ? priority.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (loggerContextFactoryClass != null ? loggerContextFactoryClass.hashCode() : 0);
        result = 31 * result + (versions != null ? versions.hashCode() : 0);
        return result;
    }
}
