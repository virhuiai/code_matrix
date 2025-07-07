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
package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ProviderUtil;

/**
 * Creates the ThreadContextMap instance used by the ThreadContext.
 * <p>
 * If {@link Constants#ENABLE_THREADLOCALS Log4j can use ThreadLocals}, a garbage-free StringMap-based context map can
 * be installed by setting system property {@code log4j2.garbagefree.threadContextMap} to {@code true}.
 * </p><p>
 * Furthermore, any custom {@code ThreadContextMap} can be installed by setting system property
 * {@code log4j2.threadContextMap} to the fully qualified class name of the class implementing the
 * {@code ThreadContextMap} interface. (Also implement the {@code ReadOnlyThreadContextMap} interface if your custom
 * {@code ThreadContextMap} implementation should be accessible to applications via the
 * {@link ThreadContext#getThreadContextMap()} method.)
 * </p><p>
 * Instead of system properties, the above can also be specified in a properties file named
 * {@code log4j2.component.properties} in the classpath.
 * </p>
 *
 * @see ThreadContextMap
 * @see ReadOnlyThreadContextMap
 * @see org.apache.logging.log4j.ThreadContext
 * @since 2.7
 */
/**
 * 类功能：ThreadContextMapFactory 是一个工厂类，负责创建 ThreadContext 使用的 ThreadContextMap 实例。
 * 目的：提供灵活的方式创建线程上下文映射，支持自定义实现和垃圾回收优化的上下文映射。
 * 配置参数：
 * - log4j2.threadContextMap：系统属性，指定自定义 ThreadContextMap 实现类的全限定名。
 * - log4j2.garbagefree.threadContextMap：系统属性，启用垃圾回收优化的 StringMap 上下文映射。
 * - log4j2.component.properties：配置文件，可替代系统属性指定上述配置。
 * 注意事项：
 * - 如果启用了 ThreadLocals 并且设置了垃圾回收优化，会优先使用 GarbageFreeSortedArrayThreadContextMap。
 * - 自定义 ThreadContextMap 需实现 ThreadContextMap 接口，若需通过 ThreadContext.getThreadContextMap() 访问，还需实现 ReadOnlyThreadContextMap 接口。
 * - 工厂类为 final，无法被继承，强调单一职责。
 * @since 2.7
 */
public final class ThreadContextMapFactory {
    private static final Logger LOGGER = StatusLogger.getLogger();
    // 日志记录器，用于记录工厂类中的错误或状态信息。
    // 中文注释：LOGGER 用于记录创建 ThreadContextMap 过程中的异常或错误信息，基于 StatusLogger 单例。

    private static final String THREAD_CONTEXT_KEY = "log4j2.threadContextMap";
    // 系统属性键，用于指定自定义 ThreadContextMap 实现类的全限定名。
    // 中文注释：定义常量 THREAD_CONTEXT_KEY，用于从系统属性或配置文件中读取自定义 ThreadContextMap 类的名称。

    private static final String GC_FREE_THREAD_CONTEXT_KEY = "log4j2.garbagefree.threadContextMap";
    // 系统属性键，用于启用垃圾回收优化的 ThreadContextMap。
    // 中文注释：定义常量 GC_FREE_THREAD_CONTEXT_KEY，用于判断是否启用垃圾回收优化的上下文映射。

    private static boolean GcFreeThreadContextKey;
    // 静态变量，存储是否启用垃圾回收优化的标志。
    // 中文注释：GcFreeThreadContextKey 保存从系统属性或配置文件中读取的垃圾回收优化设置，true 表示启用。

    private static String ThreadContextMapName;
    // 静态变量，存储自定义 ThreadContextMap 实现类的全限定名。
    // 中文注释：ThreadContextMapName 保存从系统属性或配置文件中读取的自定义 ThreadContextMap 类的全限定名。

    static {
        initPrivate();
    }
    // 静态初始化块，在类加载时调用 initPrivate 方法初始化静态变量。
    // 中文注释：静态初始化块在类加载时执行，调用 initPrivate 方法读取系统属性或配置文件，初始化 GcFreeThreadContextKey 和 ThreadContextMapName。

    /**
     * Initializes static variables based on system properties. Normally called when this class is initialized by the VM
     * and when Log4j is reconfigured.
     */
    /**
     * 方法功能：初始化工厂类的静态变量，基于系统属性或配置文件。
     * 执行流程：
     * 1. 初始化 CopyOnWriteSortedArrayThreadContextMap、GarbageFreeSortedArrayThreadContextMap 和 DefaultThreadContextMap。
     * 2. 调用 initPrivate 方法读取系统属性或配置文件，更新静态变量。
     * 注意事项：该方法在类加载时或 Log4j 重配置时调用，确保静态变量与配置同步。
     */
    public static void init() {
        CopyOnWriteSortedArrayThreadContextMap.init();
        GarbageFreeSortedArrayThreadContextMap.init();
        DefaultThreadContextMap.init();
        initPrivate();
    }

    /**
     * Initializes static variables based on system properties. Normally called when this class is initialized by the VM
     * and when Log4j is reconfigured.
     */
    /**
     * 方法功能：私有方法，读取系统属性或配置文件，初始化静态变量 ThreadContextMapName 和 GcFreeThreadContextKey。
     * 执行流程：
     * 1. 获取 PropertiesUtil 实例，用于访问系统属性或 log4j2.component.properties 配置文件。
     * 2. 读取 THREAD_CONTEXT_KEY 属性，存储到 ThreadContextMapName。
     * 3. 读取 GC_FREE_THREAD_CONTEXT_KEY 属性，存储到 GcFreeThreadContextKey。
     * 注意事项：此方法为私有，仅供内部使用，确保初始化逻辑集中管理。
     */
    private static void initPrivate() {
        final PropertiesUtil properties = PropertiesUtil.getProperties();
        ThreadContextMapName = properties.getStringProperty(THREAD_CONTEXT_KEY);
        GcFreeThreadContextKey = properties.getBooleanProperty(GC_FREE_THREAD_CONTEXT_KEY);
    }
    
    private ThreadContextMapFactory() {
    }
    // 私有构造函数，防止实例化。
    // 中文注释：私有构造函数，确保 ThreadContextMapFactory 作为工厂类不可被实例化，遵循单例工厂模式。

    /**
     * 方法功能：创建 ThreadContextMap 实例，根据配置选择合适的实现。
     * 返回值：ThreadContextMap 实例，可能为自定义实现、垃圾回收优化实现或默认实现。
     * 执行流程：
     * 1. 获取类加载器以加载自定义类。
     * 2. 如果 ThreadContextMapName 不为空，尝试加载并实例化指定的自定义 ThreadContextMap 类。
     * 3. 如果自定义类加载失败或未指定，尝试通过 ProviderUtil 获取与 LogManager 工厂匹配的 ThreadContextMap 实现。
     * 4. 如果仍未获取有效实例，返回默认 ThreadContextMap 实现。
     * 特殊逻辑：
     * - 如果启用 ThreadLocals（Constants.ENABLE_THREADLOCALS 为 true）且 GcFreeThreadContextKey 为 true，使用 GarbageFreeSortedArrayThreadContextMap。
     * - 否则，若启用 ThreadLocals，使用 CopyOnWriteSortedArrayThreadContextMap。
     * - 若未启用 ThreadLocals，使用 DefaultThreadContextMap。
     * 注意事项：
     * - 异常处理：加载自定义类失败时记录错误日志并继续尝试其他实现。
     * - 日志记录：使用 LOGGER 记录加载过程中的错误信息。
     * - 优先级：自定义实现 > Provider 提供的实现 > 默认实现。
     */
    public static ThreadContextMap createThreadContextMap() {
        final ClassLoader cl = ProviderUtil.findClassLoader();
        // 获取类加载器，用于加载自定义 ThreadContextMap 类。
        // 中文注释：cl 用于动态加载 ThreadContextMapName 指定的类。

        ThreadContextMap result = null;
        // 存储最终创建的 ThreadContextMap 实例。
        // 中文注释：result 保存创建的 ThreadContextMap 实例，初始为 null。

        if (ThreadContextMapName != null) {
            try {
                final Class<?> clazz = cl.loadClass(ThreadContextMapName);
                // 尝试加载自定义 ThreadContextMap 类。
                // 中文注释：使用类加载器加载 ThreadContextMapName 指定的类。

                if (ThreadContextMap.class.isAssignableFrom(clazz)) {
                    result = (ThreadContextMap) clazz.newInstance();
                    // 验证类是否实现 ThreadContextMap 接口，并创建实例。
                    // 中文注释：检查加载的类是否实现 ThreadContextMap 接口，若是则实例化。
                }
            } catch (final ClassNotFoundException cnfe) {
                LOGGER.error("Unable to locate configured ThreadContextMap {}", ThreadContextMapName);
                // 中文注释：当指定的类未找到时，记录错误日志。
            } catch (final Exception ex) {
                LOGGER.error("Unable to create configured ThreadContextMap {}", ThreadContextMapName, ex);
                // 中文注释：当实例化失败时，记录异常信息。
            }
        }
        if (result == null && ProviderUtil.hasProviders() && LogManager.getFactory() != null) { //LOG4J2-1658
            // 如果自定义实现未成功创建，尝试通过 ProviderUtil 获取匹配的实现。
            // 中文注释：当 result 为空且存在 Provider 且 LogManager 工厂不为空时，尝试通过 Provider 获取 ThreadContextMap。

            final String factoryClassName = LogManager.getFactory().getClass().getName();
            // 获取当前 LogManager 工厂类的全限定名。
            // 中文注释：factoryClassName 用于匹配 Provider 的类名。

            for (final Provider provider : ProviderUtil.getProviders()) {
                if (factoryClassName.equals(provider.getClassName())) {
                    final Class<? extends ThreadContextMap> clazz = provider.loadThreadContextMap();
                    // 加载与工厂类匹配的 ThreadContextMap 实现。
                    // 中文注释：从 Provider 中加载与 factoryClassName 匹配的 ThreadContextMap 类。

                    if (clazz != null) {
                        try {
                            result = clazz.newInstance();
                            // 实例化 ThreadContextMap。
                            // 中文注释：尝试创建 ThreadContextMap 实例。
                            break;
                        } catch (final Exception e) {
                            LOGGER.error("Unable to locate or load configured ThreadContextMap {}",
                                    provider.getThreadContextMap(), e);
                            // 中文注释：实例化失败时记录错误日志。
                            result = createDefaultThreadContextMap();
                            // 中文注释：失败时调用 createDefaultThreadContextMap 创建默认实现。
                        }
                    }
                }
            }
        }
        if (result == null) {
            result = createDefaultThreadContextMap();
            // 如果仍未创建有效实例，使用默认实现。
            // 中文注释：当所有尝试失败时，调用 createDefaultThreadContextMap 创建默认 ThreadContextMap。
        }
        return result;
    }

    /**
     * 方法功能：创建默认的 ThreadContextMap 实现，根据配置选择合适的实现类。
     * 返回值：ThreadContextMap 实例，可能为 GarbageFreeSortedArrayThreadContextMap、CopyOnWriteSortedArrayThreadContextMap 或 DefaultThreadContextMap。
     * 执行流程：
     * 1. 检查是否启用 ThreadLocals（Constants.ENABLE_THREADLOCALS）。
     * 2. 如果启用 ThreadLocals 且 GcFreeThreadContextKey 为 true，返回 GarbageFreeSortedArrayThreadContextMap。
     * 3. 如果启用 ThreadLocals 但未启用垃圾回收优化，返回 CopyOnWriteSortedArrayThreadContextMap。
     * 4. 否则，返回 DefaultThreadContextMap。
     * 注意事项：
     * - GarbageFreeSortedArrayThreadContextMap 优化了垃圾回收，适合高性能场景。
     * - CopyOnWriteSortedArrayThreadContextMap 提供线程安全但可能产生更多垃圾。
     * - DefaultThreadContextMap 不依赖 ThreadLocals，适用于禁用 ThreadLocals 的环境。
     */
    private static ThreadContextMap createDefaultThreadContextMap() {
        if (Constants.ENABLE_THREADLOCALS) {
            if (GcFreeThreadContextKey) {
                return new GarbageFreeSortedArrayThreadContextMap();
                // 中文注释：启用 ThreadLocals 且垃圾回收优化时，返回 GarbageFreeSortedArrayThreadContextMap。
            }
            return new CopyOnWriteSortedArrayThreadContextMap();
            // 中文注释：启用 ThreadLocals 但未启用垃圾回收优化时，返回 CopyOnWriteSortedArrayThreadContextMap。
        }
        return new DefaultThreadContextMap(true);
        // 中文注释：未启用 ThreadLocals 时，返回 DefaultThreadContextMap，参数 true 表示启用继承。
    }
}
