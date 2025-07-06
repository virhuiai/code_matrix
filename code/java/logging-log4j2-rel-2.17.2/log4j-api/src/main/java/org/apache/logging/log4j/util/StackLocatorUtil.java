/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licences this file to You under the Apache License, Version 2.0
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

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.apache.logging.log4j.status.StatusLogger;

/**
 * <em>Consider this class private.</em> Provides various methods to determine the caller class. <h3>Background</h3>
 */
// 中文注释：此类被标记为私有，提供多种方法用于确定调用者的类信息。
// 主要功能：通过堆栈跟踪获取调用者的类、类加载器或堆栈跟踪元素。
// 注意事项：此类设计为内部使用，不建议外部直接调用。
public final class StackLocatorUtil {
    private static StackLocator stackLocator = null;
    private static volatile boolean errorLogged;

    // 中文注释：静态代码块，初始化 stackLocator 实例。
    // 关键变量说明：stackLocator 是 StackLocator 类型的单例对象，用于处理堆栈相关操作。
    static {
        stackLocator = StackLocator.getInstance();
    }

    // 中文注释：私有构造函数，防止外部实例化此类。
    private StackLocatorUtil() {
    }

    // TODO: return Object.class instead of null (though it will have a null ClassLoader)
    // (MS) I believe this would work without any modifications elsewhere, but I could be wrong
    // 中文注释：待办事项，建议返回 Object.class 而不是 null，尽管其类加载器可能为 null。

    // migrated from ReflectiveCallerClassUtility
    @PerformanceSensitive
    // 中文注释：获取指定调用栈深度的调用者类。
    // 参数说明：depth 表示调用栈的深度，调用者需指定要追溯的栈帧层数。
    // 方法目的：通过堆栈深度定位调用者的类信息。
    public static Class<?> getCallerClass(final int depth) {
        return stackLocator.getCallerClass(depth + 1);
    }

    // 中文注释：获取指定调用栈深度的堆栈跟踪元素。
    // 参数说明：depth 表示调用栈的深度，用于定位具体的堆栈帧。
    // 方法目的：返回指定深度的堆栈跟踪元素，便于调试和日志记录。
    public static StackTraceElement getStackTraceElement(final int depth) {
        return stackLocator.getStackTraceElement(depth + 1);
    }

    /**
     * Equivalent to {@link #getCallerClass(String, String)} with an empty {@code pkg}.
     */
    // migrated from ClassLoaderContextSelector
    @PerformanceSensitive
    // 中文注释：获取调用者的类，等同于调用 getCallerClass(fqcn, "")。
    // 参数说明：fqcn 表示全限定类名，用于定位调用者的起始点。
    // 方法目的：简化为单一参数的调用者类查询，包名默认为空。
    public static Class<?> getCallerClass(final String fqcn) {
        return getCallerClass(fqcn, Strings.EMPTY);
    }

    /**
     * Search for a calling class.
     *
     * @param fqcn Root class name whose caller to search for.
     * @param pkg Package name prefix that must be matched after the {@code fqcn} has been found.
     * @return The caller class that was matched, or null if one could not be located.
     */
    @PerformanceSensitive
    // 中文注释：搜索调用者的类。
    // 参数说明：fqcn 表示起始的全限定类名，pkg 表示需匹配的包名前缀。
    // 方法目的：根据指定的类名和包名前缀，查找调用者的类。
    // 返回值：匹配的调用者类，若未找到则返回 null。
    public static Class<?> getCallerClass(final String fqcn, final String pkg) {
        return stackLocator.getCallerClass(fqcn, pkg);
    }

    /**
     * Gets the ClassLoader of the class that called <em>this</em> method at the location up the call stack by the given
     * stack frame depth.
     * <p>
     * This method returns {@code null} if:
     * </p>
     * <ul>
     * <li>{@code sun.reflect.Reflection.getCallerClass(int)} is not present.</li>
     * <li>An exception is caught calling {@code sun.reflect.Reflection.getCallerClass(int)}.</li>
     * <li>Some Class implementations may use null to represent the bootstrap class loader.</li>
     * </ul>
     *
     * @param depth The stack frame count to walk.
     * @return A class or null.
     * @throws IndexOutOfBoundsException if depth is negative.
     */
    @PerformanceSensitive
    // 中文注释：获取指定调用栈深度的调用者类的类加载器。
    // 参数说明：depth 表示调用栈的深度，用于定位目标栈帧。
    // 方法目的：返回调用者类的类加载器，若无法获取则返回 null。
    // 特殊处理注意事项：若 depth 为负数，将抛出 IndexOutOfBoundsException 异常。
    // 返回值说明：返回类加载器或 null（可能因反射方法不可用、异常或引导类加载器为 null）。
    public static ClassLoader getCallerClassLoader(final int depth) {
        final Class<?> callerClass = stackLocator.getCallerClass(depth + 1);
        return callerClass != null ? callerClass.getClassLoader() : null;
    }

    /**
     * Search for a calling class.
     *
     * @param sentinelClass Sentinel class at which to begin searching
     * @param callerPredicate Predicate checked after the sentinelClass is found
     * @return the first matching class after <code>sentinelClass</code> is found.
     */
    @PerformanceSensitive
    // 中文注释：根据哨兵类和谓词搜索调用者的类。
    // 参数说明：sentinelClass 为搜索的起始类，callerPredicate 为匹配条件谓词。
    // 方法目的：在找到哨兵类后，返回第一个满足谓词条件的调用者类。
    // 返回值：匹配的调用者类，若未找到则返回 null。
    public static Class<?> getCallerClass(final Class<?> sentinelClass, final Predicate<Class<?>> callerPredicate) {
        return stackLocator.getCallerClass(sentinelClass, callerPredicate);
    }

    // added for use in LoggerAdapter implementations mainly
    @PerformanceSensitive
    // 中文注释：获取以指定类为起点的调用者类。
    // 参数说明：anchor 表示搜索的起始类。
    // 方法目的：主要用于 LoggerAdapter 实现，定位调用者类。
    public static Class<?> getCallerClass(final Class<?> anchor) {
        return stackLocator.getCallerClass(anchor);
    }

    // migrated from ThrowableProxy
    @PerformanceSensitive
    // 中文注释：获取当前线程的完整调用栈。
    // 方法目的：返回当前线程的调用栈类队列，便于分析调用链。
    // 返回值：包含调用栈中所有类的 Deque 集合。
    public static Deque<Class<?>> getCurrentStackTrace() {
        return stackLocator.getCurrentStackTrace();
    }

    // 中文注释：根据全限定类名计算堆栈跟踪元素的位置。
    // 参数说明：fqcnOfLogger 表示日志记录器的全限定类名。
    // 方法目的：定位与指定类名相关的堆栈跟踪元素，用于日志记录。
    // 特殊处理注意事项：若未找到堆栈元素，将记录警告日志并返回 null，仅首次记录错误。
    // 事件处理逻辑：捕获 NoSuchElementException 异常并进行错误日志记录。
    public static StackTraceElement calcLocation(final String fqcnOfLogger) {
        try {
            return stackLocator.calcLocation(fqcnOfLogger);
        } catch (NoSuchElementException ex) {
            if (!errorLogged) {
                errorLogged = true;
                StatusLogger.getLogger().warn("Unable to locate stack trace element for {}", fqcnOfLogger, ex);
            }
            return null;
        }
    }
}
