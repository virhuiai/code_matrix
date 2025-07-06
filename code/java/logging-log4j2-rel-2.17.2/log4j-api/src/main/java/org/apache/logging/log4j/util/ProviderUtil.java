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
package org.apache.logging.log4j.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * <em>Consider this class private.</em> Utility class for Log4j {@link Provider}s. When integrating with an application
 * container framework, any Log4j Providers not accessible through standard classpath scanning should
 * {@link #loadProvider(java.net.URL, ClassLoader)} a classpath accordingly.
 */
/**
 * 该类为Log4j提供者的工具类，主要用于加载和管理Log4j提供者。
 * 主要功能：支持在应用容器框架中加载非标准类路径的提供者。
 * 注意事项：此为私有类，仅限内部使用，不建议外部直接调用。
 */
public final class ProviderUtil {

    /**
     * Resource name for a Log4j 2 provider properties file.
     */
    /**
     * Log4j 2提供者配置文件资源名称。
     * 用途：指定提供者配置文件的路径和名称，位于META-INF目录下。
     */
    protected static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";

    /**
     * Loaded providers.
     */
    /**
     * 已加载的提供者集合。
     * 用途：存储所有成功加载的Provider实例，使用HashSet确保唯一性。
     */
    protected static final Collection<Provider> PROVIDERS = new HashSet<>();

    /**
     * Guards the ProviderUtil singleton instance from lazy initialization. This is primarily used for OSGi support.
     *
     * @since 2.1
     */
    /**
     * 用于保护ProviderUtil单例实例的延迟初始化锁。
     * 主要功能：通过ReentrantLock确保线程安全的单例初始化，特别为OSGi环境提供支持。
     * 注意事项：防止多线程环境下重复初始化。
     */
    protected static final Lock STARTUP_LOCK = new ReentrantLock();

    private static final String API_VERSION = "Log4jAPIVersion";
    /**
     * API版本的属性键名。
     * 用途：用于从提供者配置文件中读取API版本号。
     */
    private static final String[] COMPATIBLE_API_VERSIONS = {"2.6.0"};
    /**
     * 兼容的API版本列表。
     * 用途：定义支持的Log4j API版本，用于验证提供者兼容性。
     */
    private static final Logger LOGGER = StatusLogger.getLogger();
    /**
     * 日志记录器实例。
     * 用途：记录加载提供者过程中的调试和错误信息。
     */

    // STARTUP_LOCK guards INSTANCE for lazy initialization; this allows the OSGi Activator to pause the startup and
    // wait for a Provider to be installed. See LOG4J2-373
    /**
     * STARTUP_LOCK保护INSTANCE以实现延迟初始化，允许OSGi激活器暂停启动并等待提供者安装。
     * 用途：支持OSGi环境下的动态提供者加载，参考LOG4J2-373问题。
     */
    private static volatile ProviderUtil instance;

    private ProviderUtil() {
        for (final ClassLoader classLoader : LoaderUtil.getClassLoaders()) {
            try {
                loadProviders(classLoader);
            } catch (final Throwable ex) {
                LOGGER.debug("Unable to retrieve provider from ClassLoader {}", classLoader, ex);
            }
        }
        for (final LoaderUtil.UrlResource resource : LoaderUtil.findUrlResources(PROVIDER_RESOURCE)) {
            loadProvider(resource.getUrl(), resource.getClassLoader());
        }
    }
    /**
     * 私有构造函数，用于初始化ProviderUtil实例。
     * 执行流程：
     * 1. 遍历所有可用的类加载器，尝试加载提供者。
     * 2. 查找所有META-INF/log4j-provider.properties资源文件，加载对应的提供者。
     * 注意事项：捕获异常以确保初始化过程不会因单一失败而中断。
     */

    protected static void addProvider(final Provider provider) {
        PROVIDERS.add(provider);
        LOGGER.debug("Loaded Provider {}", provider);
    }
    /**
     * 添加提供者到集合。
     * 参数：
     * - provider: 要添加的Provider实例。
     * 功能：将指定提供者添加到PROVIDERS集合，并记录调试日志。
     * 注意事项：使用HashSet确保不重复添加。
     */

    /**
     * Loads an individual Provider implementation. This method is really only useful for the OSGi bundle activator and
     * this class itself.
     *
     * @param url the URL to the provider properties file
     * @param cl the ClassLoader to load the provider classes with
     */
    /**
     * 加载单个提供者实现。
     * 参数：
     * - url: 提供者配置文件的URL。
     * - cl: 用于加载提供者类的类加载器。
     * 主要功能：读取配置文件，验证API版本后创建并添加Provider实例。
     * 执行流程：
     * 1. 打开URL输入流，加载配置文件。
     * 2. 验证API版本是否兼容。
     * 3. 创建Provider实例并添加到集合。
     * 注意事项：捕获IO异常并记录错误日志，确保加载过程健壮。
     */
    protected static void loadProvider(final URL url, final ClassLoader cl) {
        try {
            final Properties props = PropertiesUtil.loadClose(url.openStream(), url);
            if (validVersion(props.getProperty(API_VERSION))) {
                final Provider provider = new Provider(props, url, cl);
                PROVIDERS.add(provider);
                LOGGER.debug("Loaded Provider {}", provider);
            }
        } catch (final IOException e) {
            LOGGER.error("Unable to open {}", url, e);
        }
    }

	/**
	 * 
	 * @param classLoader null can be used to mark the bootstrap class loader.
	 */
	/**
	 * 加载指定类加载器的提供者。
	 * 参数：
	 * - classLoader: 用于加载提供者的类加载器，null表示引导类加载器。
	 * 主要功能：通过ServiceLoader加载指定类加载器中的所有提供者。
	 * 执行流程：
	 * 1. 创建ServiceLoader实例，加载Provider类。
	 * 2. 遍历所有提供者，验证版本并添加未重复的提供者。
	 * 注意事项：仅添加版本兼容且未在集合中的提供者。
	 */
	protected static void loadProviders(final ClassLoader classLoader) {
		final ServiceLoader<Provider> serviceLoader = ServiceLoader.load(Provider.class, classLoader);
		for (final Provider provider : serviceLoader) {
			if (validVersion(provider.getVersions()) && !PROVIDERS.contains(provider)) {
				PROVIDERS.add(provider);
			}
		}
	}

    /**
     * @deprecated Use {@link #loadProvider(java.net.URL, ClassLoader)} instead. Will be removed in 3.0.
     */
    /**
     * 已弃用，建议使用loadProvider(URL, ClassLoader)方法，将在3.0版本移除。
     * 功能：批量加载提供者配置文件。
     * 参数：
     * - urls: 提供者配置文件URL的枚举。
     * - cl: 用于加载提供者类的类加载器。
     * 执行流程：遍历URL集合，逐个调用loadProvider方法。
     */
    @Deprecated
    protected static void loadProviders(final Enumeration<URL> urls, final ClassLoader cl) {
        if (urls != null) {
            while (urls.hasMoreElements()) {
                loadProvider(urls.nextElement(), cl);
            }
        }
    }

    public static Iterable<Provider> getProviders() {
        lazyInit();
        return PROVIDERS;
    }
    /**
     * 获取所有已加载的提供者。
     * 返回值：包含所有Provider实例的Iterable集合。
     * 功能：通过延迟初始化获取提供者集合。
     * 执行流程：
     * 1. 调用lazyInit确保单例初始化。
     * 2. 返回PROVIDERS集合。
     */

    public static boolean hasProviders() {
        lazyInit();
        return !PROVIDERS.isEmpty();
    }
    /**
     * 检查是否存在已加载的提供者。
     * 返回值：布尔值，true表示存在提供者，false表示无提供者。
     * 功能：通过延迟初始化检查PROVIDERS集合是否为空。
     */

    /**
     * Lazily initializes the ProviderUtil singleton.
     *
     * @since 2.1
     */
    /**
     * 延迟初始化ProviderUtil单例。
     * 主要功能：确保线程安全的单例初始化，支持OSGi环境。
     * 执行流程：
     * 1. 检查instance是否为null。
     * 2. 使用STARTUP_LOCK加锁，防止多线程重复初始化。
     * 3. 创建ProviderUtil实例。
     * 注意事项：处理中断异常，确保线程状态正确。
     */
    protected static void lazyInit() {
        // noinspection DoubleCheckedLocking
        if (instance == null) {
            try {
                STARTUP_LOCK.lockInterruptibly();
                try {
                    if (instance == null) {
                        instance = new ProviderUtil();
                    }
                } finally {
                    STARTUP_LOCK.unlock();
                }
            } catch (final InterruptedException e) {
                LOGGER.fatal("Interrupted before Log4j Providers could be loaded.", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public static ClassLoader findClassLoader() {
        return LoaderUtil.getThreadContextClassLoader();
    }
    /**
     * 获取当前线程的上下文类加载器。
     * 返回值：当前线程的ClassLoader实例。
     * 功能：提供类加载器以支持动态加载。
     */

    private static boolean validVersion(final String version) {
        for (final String v : COMPATIBLE_API_VERSIONS) {
            if (version.startsWith(v)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 验证提供者的API版本是否兼容。
     * 参数：
     * - version: 要验证的版本号字符串。
     * 返回值：布尔值，true表示版本兼容，false表示不兼容。
     * 功能：检查指定版本是否以兼容版本列表中的任一版本开头。
     */
}
