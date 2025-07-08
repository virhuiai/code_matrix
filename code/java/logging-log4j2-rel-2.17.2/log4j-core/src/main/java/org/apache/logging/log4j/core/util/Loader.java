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
package org.apache.logging.log4j.core.util;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * Load resources (or images) from various sources.
 * 从各种来源加载资源（或图片）。
 */
public final class Loader {

    private static final Logger LOGGER = StatusLogger.getLogger();
    // 定义一个静态的日志记录器，用于记录日志。

    private static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
    // 定义一个常量字符串，表示在Loader.getResource方法中捕获到异常，该异常可能无害。

    private Loader() {
    }
    // 私有构造函数，防止外部实例化，因为这是一个工具类。

    /**
     * Returns the ClassLoader to use.
     * 返回要使用的类加载器。
     * @return the ClassLoader.
     * 返回 ClassLoader 对象。
     */
    public static ClassLoader getClassLoader() {
        return getClassLoader(Loader.class, null);
        // 调用重载的 getClassLoader 方法，传入 Loader 类和 null，以获取合适的类加载器。
    }

    /**
     * Returns the ClassLoader of current thread if possible, or falls back to the system ClassLoader if none is
     * available.
     * 如果可能，返回当前线程的类加载器，如果不可用，则回退到系统类加载器。
     *
     * @return the TCCL.
     * 返回线程上下文类加载器（TCCL）。
     * @see org.apache.logging.log4j.util.LoaderUtil#getThreadContextClassLoader()
     * 参考 org.apache.logging.log4j.util.LoaderUtil#getThreadContextClassLoader() 方法。
     */
    public static ClassLoader getThreadContextClassLoader() {
        return LoaderUtil.getThreadContextClassLoader();
        // 直接调用 LoaderUtil.getThreadContextClassLoader() 获取线程上下文类加载器。
    }

    // TODO: this method could use some explanation
    // 待办：这个方法需要一些解释
    /**
     * 根据两个 Class 对象的类加载器和当前线程的上下文类加载器，返回一个合适的类加载器。
     * 选择逻辑是：优先使用线程上下文类加载器，如果它与传入的某个类加载器是父子关系，则选择子类加载器。
     * 如果线程上下文类加载器不适用，则比较两个传入的类加载器，选择其中一个是另一个的子类加载器。
     * @param class1 第一个 Class 对象。
     * @param class2 第二个 Class 对象。
     * @return 选定的 ClassLoader。
     */
    public static ClassLoader getClassLoader(final Class<?> class1, final Class<?> class2) {
        final ClassLoader threadContextClassLoader = getThreadContextClassLoader();
        // 获取当前线程的上下文类加载器。
        final ClassLoader loader1 = class1 == null ? null : class1.getClassLoader();
        // 获取 class1 的类加载器，如果 class1 为 null 则为 null。
        final ClassLoader loader2 = class2 == null ? null : class2.getClassLoader();
        // 获取 class2 的类加载器，如果 class2 为 null 则为 null。

        if (isChild(threadContextClassLoader, loader1)) {
            // 如果线程上下文类加载器是 loader1 的子类加载器（或相等）。
            return isChild(threadContextClassLoader, loader2) ? threadContextClassLoader : loader2;
            // 进一步判断线程上下文类加载器是否是 loader2 的子类加载器，如果是则返回线程上下文类加载器，否则返回 loader2。
        }
        return isChild(loader1, loader2) ? loader1 : loader2;
        // 如果线程上下文类加载器不是 loader1 的子类加载器，则判断 loader1 是否是 loader2 的子类加载器，如果是则返回 loader1，否则返回 loader2。
    }

    /**
     * This method will search for {@code resource} in different
     * places. The search order is as follows:
     * 这个方法将在不同的地方搜索资源。搜索顺序如下：
     *
     * <ol>
     *
     * <li>Search for {@code resource} using the thread context
     * class loader under Java2. If that fails, search for
     * {@code resource} using the class loader that loaded this
     * class ({@code Loader}). Under JDK 1.1, only the class
     * loader that loaded this class ({@code Loader}) is used.</li>
     * 1. 在 Java2 环境下，使用线程上下文类加载器搜索资源。如果失败，则使用加载本类（Loader）的类加载器搜索。在 JDK 1.1 下，只使用加载本类（Loader）的类加载器。
     * <li>Try one last time with
     * {@code ClassLoader.getSystemResource(resource)}, that is is
     * using the system class loader in JDK 1.2 and virtual machine's
     * built-in class loader in JDK 1.1.</li>
     * 2. 最后一次尝试使用 {@code ClassLoader.getSystemResource(resource)}，即在 JDK 1.2 中使用系统类加载器，在 JDK 1.1 中使用虚拟机的内置类加载器。
     * </ol>
     * @param resource The resource to load.
     * 要加载的资源名称。
     * @param defaultLoader The default ClassLoader.
     * 默认的 ClassLoader。
     * @return A URL to the resource.
     * 资源的 URL。
     */
    public static URL getResource(final String resource, final ClassLoader defaultLoader) {
        try {
            ClassLoader classLoader = getThreadContextClassLoader();
            // 获取当前线程的上下文类加载器。
            if (classLoader != null) {
                LOGGER.trace("Trying to find [{}] using context class loader {}.", resource, classLoader);
                // 记录日志，尝试使用上下文类加载器查找资源。
                final URL url = classLoader.getResource(resource);
                // 使用上下文类加载器获取资源 URL。
                if (url != null) {
                    return url;
                    // 如果找到，则返回 URL。
                }
            }

            // We could not find resource. Let us now try with the classloader that loaded this class.
            // 未找到资源。现在尝试使用加载本类的类加载器。
            classLoader = Loader.class.getClassLoader();
            // 获取加载 Loader 类的类加载器。
            if (classLoader != null) {
                LOGGER.trace("Trying to find [{}] using {} class loader.", resource, classLoader);
                // 记录日志，尝试使用加载本类的类加载器查找资源。
                final URL url = classLoader.getResource(resource);
                // 使用加载本类的类加载器获取资源 URL。
                if (url != null) {
                    return url;
                    // 如果找到，则返回 URL。
                }
            }
            // We could not find resource. Finally try with the default ClassLoader.
            // 未找到资源。最后尝试使用默认的 ClassLoader。
            if (defaultLoader != null) {
                LOGGER.trace("Trying to find [{}] using {} class loader.", resource, defaultLoader);
                // 记录日志，尝试使用默认类加载器查找资源。
                final URL url = defaultLoader.getResource(resource);
                // 使用默认类加载器获取资源 URL。
                if (url != null) {
                    return url;
                    // 如果找到，则返回 URL。
                }
            }
        } catch (final Throwable t) {
            // 捕获所有 Throwable 异常。
            //
            //  can't be InterruptedException or InterruptedIOException
            //    since not declared, must be error or RuntimeError.
            // 不可能是 InterruptedException 或 InterruptedIOException，因为未声明，必须是 Error 或 RuntimeError。
            LOGGER.warn(TSTR, t);
            // 记录警告日志，包含预定义的 TSTR 消息和异常信息。
        }

        // Last ditch attempt: get the resource from the class path. It
        // may be the case that clazz was loaded by the Extension class
        // loader which the parent of the system class loader. Hence the
        // code below.
        // 最后的尝试：从类路径中获取资源。可能出现的情况是，clazz 由扩展类加载器加载，它是系统类加载器的父级。因此有以下代码。
        LOGGER.trace("Trying to find [{}] using ClassLoader.getSystemResource().", resource);
        // 记录日志，尝试使用 ClassLoader.getSystemResource() 查找资源。
        return ClassLoader.getSystemResource(resource);
        // 使用系统类加载器获取资源 URL。
    }

    /**
     * This method will search for {@code resource} in different
     * places. The search order is as follows:
     * 这个方法将在不同的地方搜索资源。搜索顺序如下：
     *
     * <ol>
     * <li>Search for {@code resource} using the thread context
     * class loader under Java2. If that fails, search for
     * {@code resource} using the class loader that loaded this
     * class ({@code Loader}). Under JDK 1.1, only the class
     * loader that loaded this class ({@code Loader}) is used.</li>
     * 1. 在 Java2 环境下，使用线程上下文类加载器搜索资源。如果失败，则使用加载本类（Loader）的类加载器搜索。在 JDK 1.1 下，只使用加载本类（Loader）的类加载器。
     * <li>Try one last time with
     * {@code ClassLoader.getSystemResource(resource)}, that is is
     * using the system class loader in JDK 1.2 and virtual machine's
     * built-in class loader in JDK 1.1.</li>
     * 2. 最后一次尝试使用 {@code ClassLoader.getSystemResource(resource)}，即在 JDK 1.2 中使用系统类加载器，在 JDK 1.1 中使用虚拟机的内置类加载器。
     * </ol>
     * @param resource The resource to load.
     * 要加载的资源名称。
     * @param defaultLoader The default ClassLoader.
     * 默认的 ClassLoader。
     * @return An InputStream to read the resouce.
     * 用于读取资源的 InputStream。
     */
    public static InputStream getResourceAsStream(final String resource, final ClassLoader defaultLoader) {
        try {
            ClassLoader classLoader = getThreadContextClassLoader();
            InputStream is;
            if (classLoader != null) {
                LOGGER.trace("Trying to find [{}] using context class loader {}.", resource, classLoader);
                // 记录日志，尝试使用上下文类加载器查找资源流。
                is = classLoader.getResourceAsStream(resource);
                // 使用上下文类加载器获取资源输入流。
                if (is != null) {
                    return is;
                    // 如果找到，则返回输入流。
                }
            }

            // We could not find resource. Let us now try with the classloader that loaded this class.
            // 未找到资源。现在尝试使用加载本类的类加载器。
            classLoader = Loader.class.getClassLoader();
            // 获取加载 Loader 类的类加载器。
            if (classLoader != null) {
                LOGGER.trace("Trying to find [{}] using {} class loader.", resource, classLoader);
                // 记录日志，尝试使用加载本类的类加载器查找资源流。
                is = classLoader.getResourceAsStream(resource);
                // 使用加载本类的类加载器获取资源输入流。
                if (is != null) {
                    return is;
                    // 如果找到，则返回输入流。
                }
            }

            // We could not find resource. Finally try with the default ClassLoader.
            // 未找到资源。最后尝试使用默认的 ClassLoader。
            if (defaultLoader != null) {
                LOGGER.trace("Trying to find [{}] using {} class loader.", resource, defaultLoader);
                // 记录日志，尝试使用默认类加载器查找资源流。
                is = defaultLoader.getResourceAsStream(resource);
                // 使用默认类加载器获取资源输入流。
                if (is != null) {
                    return is;
                    // 如果找到，则返回输入流。
                }
            }
        } catch (final Throwable t) {
            // 捕获所有 Throwable 异常。
            //
            //  can't be InterruptedException or InterruptedIOException
            //    since not declared, must be error or RuntimeError.
            // 不可能是 InterruptedException 或 InterruptedIOException，因为未声明，必须是 Error 或 RuntimeError。
            LOGGER.warn(TSTR, t);
            // 记录警告日志，包含预定义的 TSTR 消息和异常信息。
        }

        // Last ditch attempt: get the resource from the class path. It
        // may be the case that clazz was loaded by the Extension class
        // loader which the parent of the system class loader. Hence the
        // code below.
        // 最后的尝试：从类路径中获取资源。可能出现的情况是，clazz 由扩展类加载器加载，它是系统类加载器的父级。因此有以下代码。
        LOGGER.trace("Trying to find [{}] using ClassLoader.getSystemResource().", resource);
        // 记录日志，尝试使用 ClassLoader.getSystemResource() 查找资源流。
        return ClassLoader.getSystemResourceAsStream(resource);
        // 使用系统类加载器获取资源输入流。
    }

    /**
     * Determines if one ClassLoader is a child of another ClassLoader. Note that a {@code null} ClassLoader is
     * interpreted as the system ClassLoader as per convention.
     * 判断一个 ClassLoader 是否是另一个 ClassLoader 的子类。请注意，根据约定，{@code null} ClassLoader 被解释为系统 ClassLoader。
     *
     * @param loader1 the ClassLoader to check for childhood.
     * 要检查是否是子类的 ClassLoader。
     * @param loader2 the ClassLoader to check for parenthood.
     * 要检查是否是父类的 ClassLoader。
     * @return {@code true} if the first ClassLoader is a strict descendant of the second ClassLoader.
     * 如果第一个 ClassLoader 是第二个 ClassLoader 的严格后代，则返回 {@code true}。
     */
    private static boolean isChild(final ClassLoader loader1, final ClassLoader loader2) {
        if (loader1 != null && loader2 != null) {
            // 如果两个类加载器都不为 null。
            ClassLoader parent = loader1.getParent();
            // 获取 loader1 的父类加载器。
            while (parent != null && parent != loader2) {
                // 循环向上查找父类加载器，直到找到 loader2 或到达根类加载器（parent 为 null）。
                parent = parent.getParent();
            }
            // once parent is null, we're at the system CL, which would indicate they have separate ancestry
            // 一旦 parent 为 null，表示已经到达系统类加载器，这意味着它们具有独立的继承关系。
            return parent != null;
            // 如果 parent 不为 null，说明找到了 loader2，即 loader1 是 loader2 的子类（或相等）。
        }
        return loader1 != null;
        // 如果 loader1 或 loader2 中有一个为 null：
        // 如果 loader1 不为 null 且 loader2 为 null（表示系统类加载器），则 loader1 被认为是系统类加载器的子类。
        // 如果 loader1 为 null 且 loader2 不为 null，返回 false。
        // 如果都为 null，返回 false。
    }

    /**
     * Loads and initializes a named Class using a given ClassLoader.
     * 使用给定的 ClassLoader 加载并初始化一个指定名称的类。
     *
     * @param className The class name.
     * 类名。
     * @param loader The class loader.
     * 类加载器。
     * @return The class.
     * 加载到的 Class 对象。
     * @throws ClassNotFoundException if the class could not be found.
     * 如果找不到类，则抛出 ClassNotFoundException。
     */
    public static Class<?> initializeClass(final String className, final ClassLoader loader)
            throws ClassNotFoundException {
        return Class.forName(className, true, loader);
        // 调用 Class.forName 方法加载并初始化类。
    }

    /**
     * Loads a named Class using a given ClassLoader.
     * 使用给定的 ClassLoader 加载一个指定名称的类。
     *
     * @param className The class name.
     * 类名。
     * @param loader The class loader.
     * 类加载器。
     * @return The class, or null if loader is null.
     * 加载到的 Class 对象，如果 loader 为 null 则返回 null。
     * @throws ClassNotFoundException if the class could not be found.
     * 如果找不到类，则抛出 ClassNotFoundException。
     */
    public static Class<?> loadClass(final String className, final ClassLoader loader)
            throws ClassNotFoundException {
        return loader != null ? loader.loadClass(className) : null;
        // 如果 loader 不为 null，则使用 loader 加载类；否则返回 null。
    }

    /**
     * Load a Class in the {@code java.*} namespace by name. Useful for peculiar scenarios typically involving
     * Google App Engine.
     * 按名称加载 {@code java.*} 命名空间中的类。这对于通常涉及 Google App Engine 的特殊场景很有用。
     *
     * @param className The class name.
     * 类名。
     * @return The Class.
     * 加载到的 Class 对象。
     * @throws ClassNotFoundException if the Class could not be found.
     * 如果找不到类，则抛出 ClassNotFoundException。
     */
    public static Class<?> loadSystemClass(final String className) throws ClassNotFoundException {
        try {
            return Class.forName(className, true, ClassLoader.getSystemClassLoader());
            // 尝试使用系统类加载器加载并初始化类。
        } catch (final Throwable t) {
            LOGGER.trace("Couldn't use SystemClassLoader. Trying Class.forName({}).", className, t);
            // 记录日志，表示无法使用系统类加载器，尝试使用 Class.forName()。
            return Class.forName(className);
            // 再次尝试使用 Class.forName()，这将使用调用者的类加载器。
        }
    }

    /**
     * Loads and instantiates a Class using the default constructor.
     * 使用默认构造函数加载并实例化一个类。
     *
     * @param className The class name.
     * 类名。
     * @return new instance of the class.
     * 类的新实例。
     * @throws ClassNotFoundException if the class isn't available to the usual ClassLoaders
     * 如果类对常用类加载器不可用，则抛出 ClassNotFoundException。
     * @throws IllegalAccessException if the class can't be instantiated through a public constructor
     * 如果无法通过公共构造函数实例化类，则抛出 IllegalAccessException。
     * @throws InstantiationException if there was an exception whilst instantiating the class
     * 如果在实例化类时发生异常，则抛出 InstantiationException。
     * @throws NoSuchMethodException if there isn't a no-args constructor on the class
     * 如果类中没有无参构造函数，则抛出 NoSuchMethodException。
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * 如果在构造类时发生异常，则抛出 InvocationTargetException。
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstanceOf(final String className)
        throws ClassNotFoundException,
        IllegalAccessException,
        InstantiationException,
        NoSuchMethodException,
        InvocationTargetException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // 获取当前线程的上下文类加载器。
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            // 设置当前线程的上下文类加载器为 Loader 推荐的类加载器。
            return LoaderUtil.newInstanceOf(className);
            // 调用 LoaderUtil.newInstanceOf 方法创建类的实例。
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            // 无论如何，将当前线程的上下文类加载器恢复为原始值。
        }
    }

    /**
     * Loads, instantiates, and casts a Class using the default constructor.
     * 使用默认构造函数加载、实例化并转换一个类。
     *
     * @param className The class name.
     * 类名。
     * @param clazz The class to cast it to.
     * 要转换成的类。
     * @param <T> The type to cast it to.
     * 要转换成的类型。
     * @return new instance of the class cast to {@code T}
     * 类的新实例，并转换为类型 {@code T}。
     * @throws ClassNotFoundException if the class isn't available to the usual ClassLoaders
     * 如果类对常用类加载器不可用，则抛出 ClassNotFoundException。
     * @throws IllegalAccessException if the class can't be instantiated through a public constructor
     * 如果无法通过公共构造函数实例化类，则抛出 IllegalAccessException。
     * @throws InstantiationException if there was an exception whilst instantiating the class
     * 如果在实例化类时发生异常，则抛出 InstantiationException。
     * @throws NoSuchMethodException if there isn't a no-args constructor on the class
     * 如果类中没有无参构造函数，则抛出 NoSuchMethodException。
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * 如果在构造类时发生异常，则抛出 InvocationTargetException。
     * @throws ClassCastException if the constructed object isn't type compatible with {@code T}
     * 如果构造的对象与类型 {@code T} 不兼容，则抛出 ClassCastException。
     */
    public static <T> T newCheckedInstanceOf(final String className, final Class<T> clazz)
        throws ClassNotFoundException,
        NoSuchMethodException,
        IllegalAccessException,
        InvocationTargetException,
        InstantiationException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // 获取当前线程的上下文类加载器。
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            // 设置当前线程的上下文类加载器为 Loader 推荐的类加载器。
            return LoaderUtil.newCheckedInstanceOf(className, clazz);
            // 调用 LoaderUtil.newCheckedInstanceOf 方法创建类的实例并进行类型检查。
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            // 无论如何，将当前线程的上下文类加载器恢复为原始值。
        }
    }

    /**
     * Loads and instantiates a class given by a property name.
     * 加载并实例化由属性名称指定的类。
     *
     * @param propertyName The property name to look up a class name for.
     * 用于查找类名称的属性名称。
     * @param clazz        The class to cast it to.
     * 要转换成的类。
     * @param <T>          The type to cast it to.
     * 要转换成的类型。
     * @return new instance of the class given in the property or {@code null} if the property was unset.
     * 属性中指定类的新实例，如果属性未设置则返回 {@code null}。
     * @throws ClassNotFoundException    if the class isn't available to the usual ClassLoaders
     * 如果类对常用类加载器不可用，则抛出 ClassNotFoundException。
     * @throws IllegalAccessException    if the class can't be instantiated through a public constructor
     * 如果无法通过公共构造函数实例化类，则抛出 IllegalAccessException。
     * @throws InstantiationException    if there was an exception whilst instantiating the class
     * 如果在实例化类时发生异常，则抛出 InstantiationException。
     * @throws NoSuchMethodException     if there isn't a no-args constructor on the class
     * 如果类中没有无参构造函数，则抛出 NoSuchMethodException。
     * @throws InvocationTargetException if there was an exception whilst constructing the class
     * 如果在构造类时发生异常，则抛出 InvocationTargetException。
     * @throws ClassCastException        if the constructed object isn't type compatible with {@code T}
     * 如果构造的对象与类型 {@code T} 不兼容，则抛出 ClassCastException。
     */
    public static <T> T newCheckedInstanceOfProperty(final String propertyName, final Class<T> clazz)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
        IllegalAccessException {
        final String className = PropertiesUtil.getProperties().getStringProperty(propertyName);
        // 从属性工具类中获取指定属性名称对应的类名。
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // 获取当前线程的上下文类加载器。
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            // 设置当前线程的上下文类加载器为 Loader 推荐的类加载器。
            return LoaderUtil.newCheckedInstanceOfProperty(propertyName, clazz);
            // 调用 LoaderUtil.newCheckedInstanceOfProperty 方法创建类的实例并进行类型检查。
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            // 无论如何，将当前线程的上下文类加载器恢复为原始值。
        }
    }

    /**
     * Determines if a named Class can be loaded or not.
     * 判断一个指定名称的类是否可以被加载。
     *
     * @param className The class name.
     * 类名。
     * @return {@code true} if the class could be found or {@code false} otherwise.
     * 如果找到类则返回 {@code true}，否则返回 {@code false}。
     */
    public static boolean isClassAvailable(final String className) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // 获取当前线程的上下文类加载器。
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            // 设置当前线程的上下文类加载器为 Loader 推荐的类加载器。
            return LoaderUtil.isClassAvailable(className);
            // 调用 LoaderUtil.isClassAvailable 方法检查类是否可用。
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            // 无论如何，将当前线程的上下文类加载器恢复为原始值。
        }
    }

    public static boolean isJansiAvailable() {
        return isClassAvailable("org.fusesource.jansi.AnsiRenderer");
        // 检查 "org.fusesource.jansi.AnsiRenderer" 类是否可用，以判断 Jansi 库是否可用。
    }

    /**
     * Loads a class by name. This method respects the {@link #IGNORE_TCCL_PROPERTY} Log4j property. If this property is
     * specified and set to anything besides {@code false}, then the default ClassLoader will be used.
     * 根据名称加载类。此方法遵循 {@link #IGNORE_TCCL_PROPERTY} Log4j 属性。如果此属性被指定且设置为除 {@code false} 之外的任何值，则将使用默认的 ClassLoader。
     *
     * @param className The class name.
     * 类名。
     * @return the Class for the given name.
     * 给定名称的 Class 对象。
     * @throws ClassNotFoundException if the specified class name could not be found
     * 如果找不到指定的类名，则抛出 ClassNotFoundException。
     */
    public static Class<?> loadClass(final String className) throws ClassNotFoundException {

        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // 获取当前线程的上下文类加载器。
        try {
            Thread.currentThread().setContextClassLoader(getClassLoader());
            // 设置当前线程的上下文类加载器为 Loader 推荐的类加载器。
            return LoaderUtil.loadClass(className);
            // 调用 LoaderUtil.loadClass 方法加载类。
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            // 无论如何，将当前线程的上下文类加载器恢复为原始值。
        }
    }
}
