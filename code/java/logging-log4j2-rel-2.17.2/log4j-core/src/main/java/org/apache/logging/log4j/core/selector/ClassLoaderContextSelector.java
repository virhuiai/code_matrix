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
package org.apache.logging.log4j.core.selector;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.spi.LoggerContextShutdownAware;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;

/**
 * This ContextSelector chooses a LoggerContext based upon the ClassLoader of the caller. This allows Loggers assigned
 * to static variables to be released along with the classes that own then. Other ContextSelectors will generally cause
 * Loggers associated with classes loaded from different ClassLoaders to be co-mingled. This is a problem if, for
 * example, a web application is undeployed as some of the Loggers being released may be associated with a Class in a
 * parent ClassLoader, which will generally have negative consequences.
 * 这个上下文选择器根据调用者的类加载器选择一个LoggerContext。这允许与静态变量关联的Logger随着拥有它们的类一起被释放。
 * 其他上下文选择器通常会导致与不同类加载器加载的类相关联的Logger混合在一起。
 * 这是一个问题，例如，如果一个web应用程序被卸载，因为一些正在释放的Logger可能与父类加载器中的类相关联，这通常会产生负面影响。
 *
 * The main downside to this ContextSelector is that Configuration is more challenging.
 * 这个上下文选择器的主要缺点是配置更具挑战性。
 *
 * This ContextSelector should not be used with a Servlet Filter such as the Log4jServletFilter.
 * 此ContextSelector不应与Servlet Filter（例如Log4jServletFilter）一起使用。
 */
public class ClassLoaderContextSelector implements ContextSelector, LoggerContextShutdownAware {

    private static final AtomicReference<LoggerContext> DEFAULT_CONTEXT = new AtomicReference<>();
    // 用于存储默认的LoggerContext，使用AtomicReference保证线程安全。

    protected static final StatusLogger LOGGER = StatusLogger.getLogger();
    // 状态日志器，用于记录Log4j内部的状态信息和警告。

    protected static final ConcurrentMap<String, AtomicReference<WeakReference<LoggerContext>>> CONTEXT_MAP =
            new ConcurrentHashMap<>();
    // 存储LoggerContext的并发映射，键是类加载器的哈希码，值是LoggerContext的弱引用。
    // 使用弱引用是为了允许在类加载器不再被引用时，对应的LoggerContext可以被垃圾回收。

    @Override
    public void shutdown(final String fqcn, final ClassLoader loader, final boolean currentContext,
                         final boolean allContexts) {
        // 关闭一个或所有LoggerContext。
        // 参数:
        // fqcn: 调用者的完全限定类名。
        // loader: 与LoggerContext关联的ClassLoader。
        // currentContext: 是否只关闭当前线程的LoggerContext。
        // allContexts: 是否关闭所有LoggerContext（此实现中未完全使用此参数来关闭所有）。
        LoggerContext ctx = null;
        // 声明一个LoggerContext变量，用于存储要关闭的上下文。
        if (currentContext) {
            // 如果currentContext为true，则获取当前线程的LoggerContext。
            ctx = ContextAnchor.THREAD_CONTEXT.get();
            // 从线程局部变量中获取当前线程绑定的LoggerContext。
        } else if (loader != null) {
            // 如果提供了ClassLoader，则根据ClassLoader查找对应的LoggerContext。
            ctx = findContext(loader);
            // 调用findContext方法，根据类加载器查找LoggerContext。
        } else {
            // 如果既没有指定currentContext也没有提供ClassLoader，则尝试从调用栈中获取类加载器。
            final Class<?> clazz = StackLocatorUtil.getCallerClass(fqcn);
            // 获取调用该方法的类的Class对象。
            if (clazz != null) {
                // 如果获取到Class对象，则获取其ClassLoader并查找对应的LoggerContext。
                ctx = findContext(clazz.getClassLoader());
                // 根据调用类的类加载器查找LoggerContext。
            }
            if (ctx == null) {
                // 如果通过调用栈和类加载器未能找到LoggerContext，则回退到获取当前线程的LoggerContext。
                ctx = ContextAnchor.THREAD_CONTEXT.get();
                // 再次尝试从线程局部变量中获取LoggerContext。
            }
        }
        if (ctx != null) {
            // 如果找到了LoggerContext，则停止它。
            ctx.stop(DEFAULT_STOP_TIMEOUT, TimeUnit.MILLISECONDS);
            // 停止LoggerContext，并设置默认的停止超时时间。
        }
    }

    @Override
    public void contextShutdown(org.apache.logging.log4j.spi.LoggerContext loggerContext) {
        // 当一个LoggerContext被关闭时调用的回调方法。
        // 参数:
        // loggerContext: 被关闭的LoggerContext实例。
        if (loggerContext instanceof LoggerContext) {
            // 检查传入的loggerContext是否是LoggerContext的实例。
            removeContext((LoggerContext) loggerContext);
            // 如果是，则从CONTEXT_MAP中移除该LoggerContext。
        }
    }

    @Override
    public boolean hasContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        // 检查是否存在一个LoggerContext。
        // 参数:
        // fqcn: 调用者的完全限定类名。
        // loader: 与LoggerContext关联的ClassLoader。
        // currentContext: 是否检查当前线程的LoggerContext。
        // 返回值:
        // 如果存在LoggerContext并且它已启动，则返回true，否则返回false。
        LoggerContext ctx;
        // 声明一个LoggerContext变量。
        if (currentContext) {
            // 如果currentContext为true，则获取当前线程的LoggerContext。
            ctx = ContextAnchor.THREAD_CONTEXT.get();
        } else if (loader != null) {
            // 如果提供了ClassLoader，则根据ClassLoader查找对应的LoggerContext。
            ctx = findContext(loader);
        } else {
            // 否则，从调用栈中获取类加载器并查找。
            final Class<?> clazz = StackLocatorUtil.getCallerClass(fqcn);
            if (clazz != null) {
                ctx = findContext(clazz.getClassLoader());
            } else {
                ctx = ContextAnchor.THREAD_CONTEXT.get();
            }
        }
        return ctx != null && ctx.isStarted();
        // 判断找到的上下文是否不为空且已经启动。
    }

    private LoggerContext findContext(ClassLoader loaderOrNull) {
        // 根据ClassLoader查找LoggerContext。
        // 参数:
        // loaderOrNull: 要查找的ClassLoader，可能为null。
        // 返回值:
        // 找到的LoggerContext，如果不存在则返回null。
        final ClassLoader loader = loaderOrNull != null ? loaderOrNull : ClassLoader.getSystemClassLoader();
        // 如果loaderOrNull为null，则使用系统类加载器。
        final String name = toContextMapKey(loader);
        // 将ClassLoader转换为用于CONTEXT_MAP的键。
        AtomicReference<WeakReference<LoggerContext>> ref = CONTEXT_MAP.get(name);
        // 从映射中获取与该键关联的AtomicReference。
        if (ref != null) {
            // 如果找到了引用。
            final WeakReference<LoggerContext> weakRef = ref.get();
            // 获取弱引用。
            return weakRef.get();
            // 从弱引用中获取LoggerContext实例。
        }
        return null;
        // 如果没有找到对应的引用，则返回null。
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        // 获取LoggerContext的重载方法。
        // 参数:
        // fqcn: 调用者的完全限定类名。
        // loader: 与LoggerContext关联的ClassLoader。
        // currentContext: 是否获取当前线程的LoggerContext。
        // 返回值:
        // 找到的LoggerContext。
        return getContext(fqcn, loader, currentContext, null);
        // 调用另一个重载方法，configLocation为null。
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext,
            final URI configLocation) {
        // 获取LoggerContext的重载方法。
        // 参数:
        // fqcn: 调用者的完全限定类名。
        // loader: 与LoggerContext关联的ClassLoader。
        // currentContext: 是否获取当前线程的LoggerContext。
        // configLocation: 配置文件的URI。
        // 返回值:
        // 找到的LoggerContext。
        return getContext(fqcn, loader, null, currentContext, configLocation);
        // 调用另一个重载方法，entry为null。
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final Map.Entry<String, Object> entry,
            final boolean currentContext, final URI configLocation) {
        // 获取LoggerContext的主要方法。
        // 参数:
        // fqcn: 调用者的完全限定类名。
        // loader: 与LoggerContext关联的ClassLoader。
        // entry: 用于LoggerContext的附加属性条目。
        // currentContext: 是否获取当前线程的LoggerContext。
        // configLocation: 配置文件的URI。
        // 返回值:
        // 找到的LoggerContext。
        if (currentContext) {
            // 如果currentContext为true，则尝试获取当前线程的LoggerContext。
            final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
            // 从线程局部变量中获取当前线程绑定的LoggerContext。
            if (ctx != null) {
                // 如果找到了，则直接返回。
                return ctx;
            }
            return getDefault();
            // 如果当前线程没有LoggerContext，则返回默认的LoggerContext。
        } else if (loader != null) {
            // 如果提供了ClassLoader，则根据ClassLoader定位LoggerContext。
            return locateContext(loader, entry, configLocation);
            // 调用locateContext方法根据类加载器和配置信息定位LoggerContext。
        } else {
            // 如果没有提供ClassLoader，则尝试从调用栈中获取类加载器。
            final Class<?> clazz = StackLocatorUtil.getCallerClass(fqcn);
            // 获取调用该方法的类的Class对象。
            if (clazz != null) {
                // 如果获取到Class对象，则使用其ClassLoader定位LoggerContext。
                return locateContext(clazz.getClassLoader(), entry, configLocation);
                // 根据调用类的类加载器定位LoggerContext。
            }
            final LoggerContext lc = ContextAnchor.THREAD_CONTEXT.get();
            // 如果无法通过ClassLoader定位，则回退到获取当前线程的LoggerContext。
            if (lc != null) {
                // 如果当前线程有LoggerContext，则返回它。
                return lc;
            }
            return getDefault();
            // 如果以上所有尝试都失败，则返回默认的LoggerContext。
        }
    }

    @Override
    public void removeContext(final LoggerContext context) {
        // 从CONTEXT_MAP中移除指定的LoggerContext。
        // 参数:
        // context: 要移除的LoggerContext实例。
        for (final Map.Entry<String, AtomicReference<WeakReference<LoggerContext>>> entry : CONTEXT_MAP.entrySet()) {
            // 遍历CONTEXT_MAP的所有条目。
            final LoggerContext ctx = entry.getValue().get().get();
            // 获取弱引用中包含的LoggerContext。
            if (ctx == context) {
                // 如果找到的LoggerContext与传入的上下文相同。
                CONTEXT_MAP.remove(entry.getKey());
                // 从映射中移除该条目。
            }
        }
    }

    @Override
    public boolean isClassLoaderDependent() {
        // 判断此ContextSelector是否依赖于ClassLoader。
        // 返回值:
        // 始终返回true，因为ClassLoaderContextSelector是基于ClassLoader的。
        // By definition the ClassLoaderContextSelector depends on the callers class loader.
        // 根据定义，ClassLoaderContextSelector依赖于调用者的类加载器。
        return true;
    }

    @Override
    public List<LoggerContext> getLoggerContexts() {
        // 获取当前所有活动的LoggerContext列表。
        // 返回值:
        // 一个包含所有LoggerContext的不可修改列表。
        final List<LoggerContext> list = new ArrayList<>();
        // 创建一个新的ArrayList来存储LoggerContext。
        final Collection<AtomicReference<WeakReference<LoggerContext>>> coll = CONTEXT_MAP.values();
        // 获取CONTEXT_MAP中所有值的集合（即所有AtomicReference<WeakReference<LoggerContext>>）。
        for (final AtomicReference<WeakReference<LoggerContext>> ref : coll) {
            // 遍历集合中的每个AtomicReference。
            final LoggerContext ctx = ref.get().get();
            // 获取弱引用中包含的LoggerContext。
            if (ctx != null) {
                // 如果LoggerContext不为null（即弱引用指向的对象仍然存在）。
                list.add(ctx);
                // 将其添加到列表中。
            }
        }
        return Collections.unmodifiableList(list);
        // 返回一个不可修改的列表。
    }

    private LoggerContext locateContext(final ClassLoader loaderOrNull, final Map.Entry<String, Object> entry,
            final URI configLocation) {
        // 定位或创建LoggerContext。
        // 这个方法是核心逻辑，它根据ClassLoader查找现有的LoggerContext，
        // 如果不存在，则尝试在父ClassLoader中查找，如果仍然不存在，则创建一个新的。
        // 参数:
        // loaderOrNull: 要关联的ClassLoader，可能为null。
        // entry: 用于初始化LoggerContext的附加属性。
        // configLocation: 配置文件的URI。
        // 返回值:
        // 定位或创建的LoggerContext。
        // LOG4J2-477: class loader may be null
        // 解决LOG4J2-477问题：类加载器可能为null。
        final ClassLoader loader = loaderOrNull != null ? loaderOrNull : ClassLoader.getSystemClassLoader();
        // 如果传入的ClassLoader为null，则使用系统类加载器。
        final String name = toContextMapKey(loader);
        // 将ClassLoader转换为用于CONTEXT_MAP的键。
        AtomicReference<WeakReference<LoggerContext>> ref = CONTEXT_MAP.get(name);
        // 从映射中获取与该键关联的AtomicReference。
        if (ref == null) {
            // 如果没有找到对应的引用，说明此ClassLoader还没有对应的LoggerContext。
            if (configLocation == null) {
                // 如果没有提供配置位置，则尝试在父ClassLoader中查找。
                ClassLoader parent = loader.getParent();
                // 获取当前ClassLoader的父加载器。
                while (parent != null) {
                    // 循环遍历父加载器链。
                    ref = CONTEXT_MAP.get(toContextMapKey(parent));
                    // 尝试从父加载器对应的键中获取LoggerContext。
                    if (ref != null) {
                        // 如果找到了父加载器对应的引用。
                        final WeakReference<LoggerContext> r = ref.get();
                        // 获取弱引用。
                        final LoggerContext ctx = r.get();
                        // 获取LoggerContext实例。
                        if (ctx != null) {
                            // 如果LoggerContext存在，则返回它，表示找到了共享的父上下文。
                            return ctx;
                        }
                    }
                    parent = parent.getParent();
                    // 继续获取父加载器的父加载器。
                    /*  In Tomcat 6 the parent of the JSP classloader is the webapp classloader which would be
                    configured by the WebAppContextListener. The WebAppClassLoader is also the ThreadContextClassLoader.
                    In JBoss 5 the parent of the JSP ClassLoader is the WebAppClassLoader which is also the
                    ThreadContextClassLoader. However, the parent of the WebAppClassLoader is the ClassLoader
                    that is configured by the WebAppContextListener.

                    ClassLoader threadLoader = null;
                    try {
                        threadLoader = Thread.currentThread().getContextClassLoader();
                    } catch (Exception ex) {
                        // Ignore SecurityException
                    }
                    if (threadLoader != null && threadLoader == parent) {
                        break;
                    } else {
                        parent = parent.getParent();
                    } */
                }
            }
            LoggerContext ctx = createContext(name, configLocation);
            // 如果在所有父ClassLoader中都没有找到，则创建一个新的LoggerContext。
            if (entry != null) {
                // 如果提供了附加属性。
                ctx.putObject(entry.getKey(), entry.getValue());
                // 将属性放入LoggerContext。
            }
            LoggerContext newContext = CONTEXT_MAP.computeIfAbsent(name,
                    k -> new AtomicReference<>(new WeakReference<>(ctx))).get().get();
            // 将新创建的LoggerContext放入CONTEXT_MAP，如果并发操作导致已经存在，则使用已存在的。
            if (newContext == ctx) {
                // 如果成功地将新创建的上下文放入映射（没有被并发创建的另一个上下文覆盖）。
                ctx.addShutdownListener(this);
                // 为这个新的LoggerContext添加一个关闭监听器，以便在它关闭时可以从映射中移除。
            }
            return newContext;
        }
        final WeakReference<LoggerContext> weakRef = ref.get();
        // 获取找到的AtomicReference中的弱引用。
        LoggerContext ctx = weakRef.get();
        // 获取弱引用指向的LoggerContext实例。
        if (ctx != null) {
            // 如果LoggerContext实例仍然存在（未被垃圾回收）。
            if (entry != null && ctx.getObject(entry.getKey()) == null) {
                // 如果提供了附加属性且LoggerContext中没有该属性。
                ctx.putObject(entry.getKey(), entry.getValue());
                // 将属性放入LoggerContext。
            }
            if (ctx.getConfigLocation() == null && configLocation != null) {
                // 如果LoggerContext当前没有配置位置，但提供了新的配置位置。
                LOGGER.debug("Setting configuration to {}", configLocation);
                // 记录调试信息。
                ctx.setConfigLocation(configLocation);
                // 设置新的配置位置。
            } else if (ctx.getConfigLocation() != null && configLocation != null
                    && !ctx.getConfigLocation().equals(configLocation)) {
                // 如果LoggerContext已有配置位置，且新的配置位置与现有配置位置不同。
                LOGGER.warn("locateContext called with URI {}. Existing LoggerContext has URI {}", configLocation,
                        ctx.getConfigLocation());
                // 记录警告信息，表示配置位置冲突。
            }
            return ctx;
            // 返回找到的LoggerContext。
        }
        ctx = createContext(name, configLocation);
        // 如果弱引用指向的对象已被垃圾回收（即LoggerContext不存在了），则创建一个新的。
        if (entry != null) {
            // 如果提供了附加属性。
            ctx.putObject(entry.getKey(), entry.getValue());
            // 将属性放入新的LoggerContext。
        }
        ref.compareAndSet(weakRef, new WeakReference<>(ctx));
        // 使用CAS操作，尝试将旧的弱引用替换为新的弱引用。
        return ctx;
        // 返回新创建或更新的LoggerContext。
    }

    protected LoggerContext createContext(final String name, final URI configLocation) {
        // 创建一个新的LoggerContext实例。
        // 参数:
        // name: LoggerContext的名称。
        // configLocation: 配置文件的URI。
        // 返回值:
        // 新创建的LoggerContext实例。
        return new LoggerContext(name, null, configLocation);
        // 调用LoggerContext的构造函数创建实例。
    }

    protected String toContextMapKey(final ClassLoader loader) {
        // 将ClassLoader转换为用于CONTEXT_MAP的键。
        // 返回值:
        // ClassLoader的身份哈希码的十六进制字符串表示。
        return Integer.toHexString(System.identityHashCode(loader));
        // 使用ClassLoader的身份哈希码作为键，以确保每个ClassLoader对应唯一的键。
    }

    protected LoggerContext getDefault() {
        // 获取默认的LoggerContext。
        // 如果默认上下文尚未创建，则会创建一个。
        // 返回值:
        // 默认的LoggerContext实例。
        final LoggerContext ctx = DEFAULT_CONTEXT.get();
        // 获取当前默认LoggerContext的引用。
        if (ctx != null) {
            // 如果默认上下文已经存在，则直接返回。
            return ctx;
        }
        DEFAULT_CONTEXT.compareAndSet(null, createContext(defaultContextName(), null));
        // 如果默认上下文为null，则使用CAS操作尝试创建一个新的默认LoggerContext并设置。
        return DEFAULT_CONTEXT.get();
        // 返回（可能刚刚创建的）默认LoggerContext。
    }

    protected String defaultContextName() {
        // 获取默认LoggerContext的名称。
        // 返回值:
        // 默认上下文的名称字符串。
        return "Default";
        // 返回硬编码的“Default”名称。
    }
}
