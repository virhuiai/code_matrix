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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * <em>Consider this class private.</em> Utility class for ClassLoaders.
 *
 * @see ClassLoader
 * @see RuntimePermission
 * @see Thread#getContextClassLoader()
 * @see ClassLoader#getSystemClassLoader()
 */
// 中文注释：
// LoaderUtil 是一个工具类，专为处理类加载器（ClassLoader）相关操作设计，标记为私有，建议外部避免直接使用。
// 主要功能：提供类加载、资源查找和实例创建的辅助方法，优化类加载器获取逻辑，处理线程上下文类加载器（TCCL）和系统类加载器。
// 使用场景：用于 Log4j 日志框架中动态加载类、资源或实例，确保在不同类加载器环境下正确工作。
// 注意事项：涉及安全管理器（SecurityManager）的权限检查，可能受限于运行时环境的安全策略。
public final class LoaderUtil {

    private static final ClassLoader[] EMPTY_CLASS_LOADER_ARRAY = {};

    /**
     * System property to set to ignore the thread context ClassLoader.
     *
     * @since 2.1
     */
    public static final String IGNORE_TCCL_PROPERTY = "log4j.ignoreTCL";
    // 中文注释：
    // 定义系统属性名，用于控制是否忽略线程上下文类加载器（TCCL）。
    // 属性名称：log4j.ignoreTCL
    // 用途：当设置为非 "false" 值时，强制使用默认类加载器，忽略 TCCL。
    // 适用版本：自 Log4j 2.1 起支持。

    private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();
    // 中文注释：
    // 变量 SECURITY_MANAGER：存储当前 JVM 的安全管理器实例，用于权限检查。
    // 用途：判断是否可以访问线程上下文类加载器或系统类加载器。
    // 初始化：在类加载时通过 System.getSecurityManager() 获取。

    // this variable must be lazily loaded; otherwise, we get a nice circular class loading problem where LoaderUtil
    // wants to use PropertiesUtil, but then PropertiesUtil wants to use LoaderUtil.
    private static Boolean ignoreTCCL;
    // 中文注释：
    // 变量 ignoreTCCL：布尔值，指示是否忽略线程上下文类加载器。
    // 延迟加载原因：避免 LoaderUtil 和 PropertiesUtil 之间的循环依赖问题。
    // 初始化：在 isIgnoreTccl() 方法中通过 PropertiesUtil 读取系统属性进行初始化。
    // 注意事项：该变量为静态，允许多线程访问，但初始化是线程安全的。

    private static final boolean GET_CLASS_LOADER_DISABLED;
    // 中文注释：
    // 变量 GET_CLASS_LOADER_DISABLED：布尔值，指示是否禁用类加载器获取操作。
    // 用途：当安全管理器限制访问类加载器时，设为 true，防止抛出安全异常。
    // 初始化：在静态代码块中通过检查安全管理器权限确定。

    private static final PrivilegedAction<ClassLoader> TCCL_GETTER = new ThreadContextClassLoaderGetter();
    // 中文注释：
    // 变量 TCCL_GETTER：PrivilegedAction 实例，用于以特权方式获取线程上下文类加载器。
    // 用途：在存在安全管理器时，通过 AccessController 执行特权操作，规避权限限制。
    // 实现：ThreadContextClassLoaderGetter 内部类负责具体逻辑。

    static {
        if (SECURITY_MANAGER != null) {
            boolean getClassLoaderDisabled;
            try {
                SECURITY_MANAGER.checkPermission(new RuntimePermission("getClassLoader"));
                getClassLoaderDisabled = false;
            } catch (final SecurityException ignored) {
                getClassLoaderDisabled = true;
            }
            GET_CLASS_LOADER_DISABLED = getClassLoaderDisabled;
        } else {
            GET_CLASS_LOADER_DISABLED = false;
        }
    }
    // 中文注释：
    // 静态初始化块：初始化 GET_CLASS_LOADER_DISABLED 变量。
    // 执行流程：
    // 1. 检查 SECURITY_MANAGER 是否存在。
    // 2. 若存在，尝试检查 "getClassLoader" 运行时权限。
    // 3. 若权限检查通过，GET_CLASS_LOADER_DISABLED 设为 false；否则，捕获 SecurityException 并设为 true。
    // 4. 若无安全管理器，直接设为 false。
    // 注意事项：此逻辑确保在受限安全环境下不会因权限问题导致异常。

    private LoaderUtil() {
    }
    // 中文注释：
    // 私有构造函数：防止外部实例化 LoaderUtil 类。
    // 用途：确保 LoaderUtil 作为纯静态工具类使用。

    /**
     * Gets the current Thread ClassLoader. Returns the system ClassLoader if the TCCL is {@code null}. If the system
     * ClassLoader is {@code null} as well, then the ClassLoader for this class is returned. If running with a
     * {@link SecurityManager} that does not allow access to the Thread ClassLoader or system ClassLoader, then the
     * ClassLoader for this class is returned.
     *
     * @return the current ThreadContextClassLoader.
     */
    public static ClassLoader getThreadContextClassLoader() {
        if (GET_CLASS_LOADER_DISABLED) {
            // we can at least get this class's ClassLoader regardless of security context
            // however, if this is null, there's really no option left at this point
            return LoaderUtil.class.getClassLoader();
        }
        return SECURITY_MANAGER == null ? TCCL_GETTER.run() : AccessController.doPrivileged(TCCL_GETTER);
    }
    // 中文注释：
    // 方法 getThreadContextClassLoader：
    // 主要功能：获取当前线程的上下文类加载器（TCCL）。
    // 返回值：ClassLoader，优先返回 TCCL，若不可用则返回系统类加载器或 LoaderUtil 类的类加载器。
    // 执行流程：
    // 1. 检查 GET_CLASS_LOADER_DISABLED 是否为 true，若是，直接返回 LoaderUtil 类的类加载器。
    // 2. 若无安全管理器，直接通过 TCCL_GETTER 获取 TCCL。
    // 3. 若有安全管理器，使用 AccessController.doPrivileged 以特权方式执行 TCCL_GETTER。
    // 注意事项：
    // - 若 TCCL 和系统类加载器均不可用，退回到 LoaderUtil 类的类加载器。
    // - 在受限安全环境下，特权操作确保访问类加载器时不抛出安全异常。

    /**
     *
     */
    private static class ThreadContextClassLoaderGetter implements PrivilegedAction<ClassLoader> {
        @Override
        public ClassLoader run() {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl;
            }
            final ClassLoader ccl = LoaderUtil.class.getClassLoader();
            return ccl == null && !GET_CLASS_LOADER_DISABLED ? ClassLoader.getSystemClassLoader() : ccl;
        }
    }
    // 中文注释：
    // 内部类 ThreadContextClassLoaderGetter：
    // 主要功能：以特权方式获取线程上下文类加载器（TCCL），实现 PrivilegedAction 接口。
    // 方法 run：
    // 返回值：ClassLoader，线程上下文类加载器或备选类加载器。
    // 执行流程：
    // 1. 获取当前线程的上下文类加载器（TCCL）。
    // 2. 若 TCCL 不为 null，直接返回。
    // 3. 否则，获取 LoaderUtil 类的类加载器（ccl）。
    // 4. 若 ccl 为 null 且 GET_CLASS_LOADER_DISABLED 为 false，返回系统类加载器；否则返回 ccl。
    // 注意事项：此实现确保在安全管理器环境下以特权方式访问类加载器，规避权限限制。

    public static ClassLoader[] getClassLoaders() {
        final Collection<ClassLoader> classLoaders = new LinkedHashSet<>();
        final ClassLoader tcl = getThreadContextClassLoader();
        if (tcl != null) {
            classLoaders.add(tcl);
        }
        accumulateClassLoaders(LoaderUtil.class.getClassLoader(), classLoaders);
        accumulateClassLoaders(tcl == null ? null : tcl.getParent(), classLoaders);
        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		if (systemClassLoader != null) {
            classLoaders.add(systemClassLoader);
        }
        return classLoaders.toArray(EMPTY_CLASS_LOADER_ARRAY);
    }
    // 中文注释：
    // 方法 getClassLoaders：
    // 主要功能：获取所有可用的类加载器集合，包括 TCCL、LoaderUtil 类的类加载器、其父类加载器和系统类加载器。
    // 返回值：ClassLoader 数组，包含去重后的类加载器集合。
    // 执行流程：
    // 1. 创建 LinkedHashSet 存储类加载器，确保去重。
    // 2. 获取 TCCL，若不为空，添加到集合。
    // 3. 调用 accumulateClassLoaders 添加 LoaderUtil 类的类加载器及其父类加载器。
    // 4. 添加 TCCL 的父类加载器（若 TCCL 不为 null）。
    // 5. 获取系统类加载器，若不为空，添加到集合。
    // 6. 将集合转换为数组返回。
    // 注意事项：使用 LinkedHashSet 确保类加载器不重复，优先级为 TCCL > LoaderUtil 类加载器 > 系统类加载器。

    /**
     * Adds the provided loader to the loaders collection, and traverses up the tree until either a null
     * value or a classloader which has already been added is encountered.
     */
    private static void accumulateClassLoaders(ClassLoader loader, Collection<ClassLoader> loaders) {
        // Some implementations may use null to represent the bootstrap class loader.
        if (loader != null && loaders.add(loader)) {
            accumulateClassLoaders(loader.getParent(), loaders);
        }
    }
    // 中文注释：
    // 方法 accumulateClassLoaders：
    // 主要功能：递归地将类加载器及其父类加载器添加到集合中，直到遇到 null 或已添加的类加载器。
    // 参数：
    // - loader：待添加的类加载器。
    // - loaders：存储类加载器的集合。
    // 执行流程：
    // 1. 检查 loader 是否为 null 或已存在于 loaders 中。
    // 2. 若 loader 不为 null 且成功添加到 loaders，递归调用自身处理 loader 的父类加载器。
    // 注意事项：支持 bootstrap 类加载器（可能为 null），使用递归遍历类加载器层次结构。

    /**
     * Determines if a named Class can be loaded or not.
     *
     * @param className The class name.
     * @return {@code true} if the class could be found or {@code false} otherwise.
     * @since 2.7
     */
    public static boolean isClassAvailable(final String className) {
        try {
            final Class<?> clazz = loadClass(className);
            return clazz != null;
        } catch (final ClassNotFoundException | LinkageError e) {
            return false;
        } catch (final Throwable e) {
            LowLevelLogUtil.logException("Unknown error checking for existence of class: " + className, e);
            return false;
        }
    }
    // 中文注释：
    // 方法 isClassAvailable：
    // 主要功能：检查指定类名是否可以通过类加载器加载。
    // 参数：
    // - className：待检查的类全限定名。
    // 返回值：boolean，true 表示类可加载，false 表示不可加载。
    // 执行流程：
    // 1. 调用 loadClass 方法尝试加载指定类。
    // 2. 若加载成功且 clazz 不为 null，返回 true。
    // 3. 捕获 ClassNotFoundException 或 LinkageError，返回 false。
    // 4. 捕获其他异常，记录日志后返回 false。
    // 注意事项：
    // - 使用 try-catch 处理可能的加载失败情况。
    // - 通过 LowLevelLogUtil 记录未知异常，确保调试信息可追溯。
    // 适用版本：自 Log4j 2.7 起支持。

    /**
     * Loads a class by name. This method respects the {@link #IGNORE_TCCL_PROPERTY} Log4j property. If this property is
     * specified and set to anything besides {@code false}, then the default ClassLoader will be used.
     *
     * @param className The class name.
     * @return the Class for the given name.
     * @throws ClassNotFoundException if the specified class name could not be found
     * @since 2.1
     */
    public static Class<?> loadClass(final String className) throws ClassNotFoundException {
        if (isIgnoreTccl()) {
            return Class.forName(className);
        }
        try {
            ClassLoader tccl = getThreadContextClassLoader();
            if (tccl != null) {
                return tccl.loadClass(className);
            }
        } catch (final Throwable ignored) {
        }
        return Class.forName(className);
    }
    // 中文注释：
    // 方法 loadClass：
    // 主要功能：根据类名加载类，优先使用 TCCL，但支持通过 IGNORE_TCCL_PROPERTY 忽略 TCCL。
    // 参数：
    // - className：待加载的类全限定名。
    // 返回值：Class<?>，加载的类对象。
    // 异常：ClassNotFoundException，若类未找到。
    // 执行流程：
    // 1. 调用 isIgnoreTccl() 检查是否忽略 TCCL。
    // 2. 若忽略 TCCL，直接使用 Class.forName 加载类。
    // 3. 否则，获取 TCCL，尝试通过 TCCL 加载类。
    // 4. 若 TCCL 加载失败或不可用，退回到 Class.forName。
    // 注意事项：
    // - 遵循 IGNORE_TCCL_PROPERTY 配置，灵活切换类加载器。
    // - 捕获并忽略 TCCL 加载时的异常，确保方法健壮性。
    // 适用版本：自 Log4j 2.1 起支持。

    /**
     * Loads and instantiates a Class using the default constructor.
     * 
     * @param <T> the type of the class modeled by the {@code Class} object.
     * @param clazz The class.
     * @return new instance of the class.
     * @throws IllegalAccessException if the class can't be instantiated through a public constructor
     * @throws InstantiationException if there was an exception whilst instantiating the class
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * @since 2.7
     */
    public static <T> T newInstanceOf(final Class<T> clazz)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            return clazz.getConstructor().newInstance();
        } catch (final NoSuchMethodException ignored) {
            // FIXME: looking at the code for Class.newInstance(), this seems to do the same thing as above
            return clazz.newInstance();
        }
    }
    // 中文注释：
    // 方法 newInstanceOf（Class<T>）：
    // 主要功能：通过默认构造函数创建指定类的实例。
    // 泛型参数：
    // - T：类类型。
    // 参数：
    // - clazz：待实例化的 Class 对象。
    // 返回值：T，新创建的实例。
    // 异常：
    // - IllegalAccessException：若类无法通过公共构造函数实例化。
    // - InstantiationException：若实例化过程中出现异常。
    // - InvocationTargetException：若构造函数执行抛出异常。
    // 执行流程：
    // 1. 尝试通过 getConstructor() 获取默认构造函数并调用 newInstance 创建实例。
    // 2. 若无默认构造函数（捕获 NoSuchMethodException），退回到 Class.newInstance()。
    // 注意事项：
    // - 优先使用反射获取默认构造函数，兼容无默认构造函数的情况。
    // - Class.newInstance() 已废弃，当前实现为兼容性保留。
    // 适用版本：自 Log4j 2.7 起支持。

    /**
     * Loads and instantiates a Class using the default constructor.
     *
     * @param className The class name.
     * @return new instance of the class.
     * @throws ClassNotFoundException if the class isn't available to the usual ClassLoaders
     * @throws IllegalAccessException if the class can't be instantiated through a public constructor
     * @throws InstantiationException if there was an exception whilst instantiating the class
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstanceOf(final String className) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        return newInstanceOf((Class<T>) loadClass(className));
    }
    // 中文注释：
    // 方法 newInstanceOf（String）：
    // 主要功能：根据类名加载并通过默认构造函数创建实例。
    // 参数：
    // - className：待加载和实例化的类全限定名。
    // 返回值：T，新创建的实例。
    // 异常：
    // - ClassNotFoundException：若类未找到。
    // - IllegalAccessException：若类无法通过公共构造函数实例化。
    // - InstantiationException：若实例化过程中出现异常。
    // - InvocationTargetException：若构造函数执行抛出异常。
    // 执行流程：
    // 1. 调用 loadClass 加载指定类。
    // 2. 将加载的类转换为泛型 Class<T>。
    // 3. 调用 newInstanceOf(Class<T>) 创建实例。
    // 注意事项：依赖 loadClass 方法的类加载逻辑，支持 IGNORE_TCCL_PROPERTY 配置。
    // 适用版本：自 Log4j 2.1 起支持。

    /**
     * Loads and instantiates a derived class using its default constructor.
     *
     * @param className The class name.
     * @param clazz The class to cast it to.
     * @param <T> The type of the class to check.
     * @return new instance of the class cast to {@code T}
     * @throws ClassNotFoundException if the class isn't available to the usual ClassLoaders
     * @throws IllegalAccessException if the class can't be instantiated through a public constructor
     * @throws InstantiationException if there was an exception whilst instantiating the class
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * @throws ClassCastException if the constructed object isn't type compatible with {@code T}
     * @since 2.1
     */
    public static <T> T newCheckedInstanceOf(final String className, final Class<T> clazz)
            throws ClassNotFoundException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        return clazz.cast(newInstanceOf(className));
    }
    // 中文注释：
    // 方法 newCheckedInstanceOf：
    // 主要功能：根据类名加载并创建实例，并确保实例可转换为指定类型。
    // 泛型参数：
    // - T：目标类型。
    // 参数：
    // - className：待加载和实例化的类全限定名。
    // - clazz：目标类型 Class 对象。
    // 返回值：T，新创建的实例，转换为指定类型。
    // 异常：
    // - ClassNotFoundException：若类未找到。
    // - IllegalAccessException：若类无法通过公共构造函数实例化。
    // - InstantiationException：若实例化过程中出现异常。
    // - InvocationTargetException：若构造函数执行抛出异常。
    // - ClassCastException：若实例无法转换为指定类型。
    // 执行流程：
    // 1. 调用 newInstanceOf(String) 加载并创建实例。
    // 2. 使用 clazz.cast 转换实例为目标类型。
    // 注意事项：增加了类型检查，确保实例与目标类型兼容。
    // 适用版本：自 Log4j 2.1 起支持。

    /**
     * Loads and instantiates a class given by a property name.
     *
     * @param propertyName The property name to look up a class name for.
     * @param clazz        The class to cast it to.
     * @param <T>          The type to cast it to.
     * @return new instance of the class given in the property or {@code null} if the property was unset.
     * @throws ClassNotFoundException    if the class isn't available to the usual ClassLoaders
     * @throws IllegalAccessException    if the class can't be instantiated through a public constructor
     * @throws InstantiationException    if there was an exception whilst instantiating the class
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * @throws ClassCastException        if the constructed object isn't type compatible with {@code T}
     * @since 2.5
     */
    public static <T> T newCheckedInstanceOfProperty(final String propertyName, final Class<T> clazz)
        throws ClassNotFoundException, InvocationTargetException, InstantiationException,
        IllegalAccessException {
        final String className = PropertiesUtil.getProperties().getStringProperty(propertyName);
        if (className == null) {
            return null;
        }
        return newCheckedInstanceOf(className, clazz);
    }
    // 中文注释：
    // 方法 newCheckedInstanceOfProperty：
    // 主要功能：根据属性名获取类名，加载并创建实例，确保实例可转换为指定类型。
    // 泛型参数：
    // - T：目标类型。
    // 参数：
    // - propertyName：属性名，用于查找类全限定名。
    // - clazz：目标类型 Class 对象。
    // 返回值：T，新创建的实例，转换为指定类型；若属性未设置，返回 null。
    // 异常：
    // - ClassNotFoundException：若类未找到。
    // - IllegalAccessException：若类无法通过公共构造函数实例化。
    // - InstantiationException：若实例化过程中出现异常。
    // - InvocationTargetException：若构造函数执行抛出异常。
    // - ClassCastException：若实例无法转换为指定类型。
    // 执行流程：
    // 1. 使用 PropertiesUtil 获取指定属性名的类全限定名。
    // 2. 若类名为空，返回 null。
    // 3. 调用 newCheckedInstanceOf 加载并创建实例。
    // 注意事项：依赖 PropertiesUtil 读取配置，适合动态加载配置指定的类。
    // 适用版本：自 Log4j 2.5 起支持。

    private static boolean isIgnoreTccl() {
        // we need to lazily initialize this, but concurrent access is not an issue
        if (ignoreTCCL == null) {
            final String ignoreTccl = PropertiesUtil.getProperties().getStringProperty(IGNORE_TCCL_PROPERTY, null);
            ignoreTCCL = ignoreTccl != null && !"false".equalsIgnoreCase(ignoreTccl.trim());
        }
        return ignoreTCCL;
    }
    // 中文注释：
    // 方法 isIgnoreTccl：
    // 主要功能：检查是否忽略线程上下文类加载器（TCCL）。
    // 返回值：boolean，true 表示忽略 TCCL，false 表示使用 TCCL。
    // 执行流程：
    // 1. 检查 ignoreTCCL 是否已初始化。
    // 2. 若未初始化，通过 PropertiesUtil 读取 IGNORE_TCCL_PROPERTY 属性值。
    // 3. 若属性值非空且不为 "false"（忽略大小写），设置 ignoreTCCL 为 true；否则为 false。
    // 4. 返回 ignoreTCCL。
    // 注意事项：
    // - 使用延迟初始化避免循环依赖问题。
    // - 属性值大小写不敏感，增强配置灵活性。
    // - 方法为线程安全，适合多线程环境调用。

    /**
     * Finds classpath {@linkplain URL resources}.
     *
     * @param resource the name of the resource to find.
     * @return a Collection of URLs matching the resource name. If no resources could be found, then this will be empty.
     * @since 2.1
     */
    public static Collection<URL> findResources(final String resource) {
        final Collection<UrlResource> urlResources = findUrlResources(resource);
        final Collection<URL> resources = new LinkedHashSet<>(urlResources.size());
        for (final UrlResource urlResource : urlResources) {
            resources.add(urlResource.getUrl());
        }
        return resources;
    }
    // 中文注释：
    // 方法 findResources：
    // 主要功能：在类路径中查找指定名称的资源，返回所有匹配的 URL 集合。
    // 参数：
    // - resource：资源名称。
    // 返回值：Collection<URL>，包含匹配资源的 URL 集合，若无匹配则返回空集合。
    // 执行流程：
    // 1. 调用 findUrlResources 获取包含类加载器和 URL 的 UrlResource 集合。
    // 2. 创建 LinkedHashSet 存储 URL，确保去重。
    // 3. 遍历 UrlResource 集合，提取 URL 添加到结果集合。
    // 4. 返回 URL 集合。
    // 注意事项：
    // - 使用 LinkedHashSet 确保 URL 去重。
    // - 依赖 findUrlResources 方法实现资源查找逻辑。
    // 适用版本：自 Log4j 2.1 起支持。

    static Collection<UrlResource> findUrlResources(final String resource) {
        // @formatter:off
        final ClassLoader[] candidates = {
                getThreadContextClassLoader(), 
                LoaderUtil.class.getClassLoader(),
                GET_CLASS_LOADER_DISABLED ? null : ClassLoader.getSystemClassLoader()};
        // @formatter:on
        final Collection<UrlResource> resources = new LinkedHashSet<>();
        for (final ClassLoader cl : candidates) {
            if (cl != null) {
                try {
                    final Enumeration<URL> resourceEnum = cl.getResources(resource);
                    while (resourceEnum.hasMoreElements()) {
                        resources.add(new UrlResource(cl, resourceEnum.nextElement()));
                    }
                } catch (final IOException e) {
                    LowLevelLogUtil.logException(e);
                }
            }
        }
        return resources;
    }
    // 中文注释：
    // 方法 findUrlResources：
    // 主要功能：查找类路径中指定名称的资源，返回包含类加载器和 URL 的 UrlResource 集合。
    // 参数：
    // - resource：资源名称。
    // 返回值：Collection<UrlResource>，包含匹配资源的 UrlResource 集合。
    // 执行流程：
    // 1. 定义候选类加载器数组，包括 TCCL、LoaderUtil 类加载器和系统类加载器（若未禁用）。
    // 2. 创建 LinkedHashSet 存储 UrlResource，确保去重。
    // 3. 遍历候选类加载器：
    //    - 若类加载器不为空，调用 getResources 获取资源 URL 枚举。
    //    - 遍历枚举，将每个 URL 和对应类加载器封装为 UrlResource 添加到集合。
    //    - 捕获并记录 IOException，忽略异常继续处理。
    // 4. 返回 UrlResource 集合。
    // 注意事项：
    // - 使用 LinkedHashSet 确保资源去重。
    // - 通过多种类加载器查找资源，增强资源查找的覆盖范围。
    // - 异常通过 LowLevelLogUtil 记录，确保方法健壮性。

    /**
     * {@link URL} and {@link ClassLoader} pair.
     */
    static class UrlResource {
        private final ClassLoader classLoader;
        private final URL url;

        UrlResource(final ClassLoader classLoader, final URL url) {
            this.classLoader = classLoader;
            this.url = url;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }

        public URL getUrl() {
            return url;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final UrlResource that = (UrlResource) o;

            if (classLoader != null ? !classLoader.equals(that.classLoader) : that.classLoader != null) {
                return false;
            }
            if (url != null ? !url.equals(that.url) : that.url != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(classLoader) + Objects.hashCode(url);
        }
    }
    // 中文注释：
    // 内部类 UrlResource：
    // 主要功能：封装资源 URL 和对应的类加载器，提供资源查找的结构化表示。
    // 成员变量：
    // - classLoader：加载资源的类加载器。
    // - url：资源的 URL。
    // 构造函数：
    // - 参数：classLoader（类加载器），url（资源 URL）。
    // - 初始化：将传入的类加载器和 URL 赋值给成员变量。
    // 方法：
    // - getClassLoader()：返回类加载器。
    // - getUrl()：返回资源 URL。
    // - equals()：比较两个 UrlResource 对象是否相等，基于 classLoader 和 url 的相等性。
    // - hashCode()：生成哈希码，基于 classLoader 和 url 的哈希值。
    // 注意事项：
    // - 用于 findUrlResources 方法，确保资源和类加载器关联。
    // - equals 和 hashCode 实现支持 LinkedHashSet 的去重功能。
}
