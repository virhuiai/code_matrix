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

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

/**
 * <em>Consider this class private.</em> Provides various methods to determine the caller class. <h3>Background</h3>
 * <p>
 * This method, available only in the Oracle/Sun/OpenJDK implementations of the Java Virtual Machine, is a much more
 * efficient mechanism for determining the {@link Class} of the caller of a particular method. When it is not available,
 * a {@link SecurityManager} is the second-best option. When this is also not possible, the {@code StackTraceElement[]}
 * returned by {@link Throwable#getStackTrace()} must be used, and its {@code String} class name converted to a
 * {@code Class} using the slow {@link Class#forName} (which can add an extra microsecond or more for each invocation
 * depending on the runtime ClassLoader hierarchy).
 * </p>
 * <p>
 * During Java 8 development, the {@code sun.reflect.Reflection.getCallerClass(int)} was removed from OpenJDK, and this
 * change was back-ported to Java 7 in version 1.7.0_25 which changed the behavior of the call and caused it to be off
 * by one stack frame. This turned out to be beneficial for the survival of this API as the change broke hundreds of
 * libraries and frameworks relying on the API which brought much more attention to the intended API removal.
 * </p>
 * <p>
 * After much community backlash, the JDK team agreed to restore {@code getCallerClass(int)} and keep its existing
 * behavior for the rest of Java 7. However, the method is deprecated in Java 8, and current Java 9 development has not
 * addressed this API. Therefore, the functionality of this class cannot be relied upon for all future versions of Java.
 * It does, however, work just fine in Sun JDK 1.6, OpenJDK 1.6, Oracle/OpenJDK 1.7, and Oracle/OpenJDK 1.8. Other Java
 * environments may fall back to using {@link Throwable#getStackTrace()} which is significantly slower due to
 * examination of every virtual frame of execution.
 * </p>
 */
 // 中文注释：此类为私有，提供多种方法用于确定调用者的类。
 // 背景说明：本类使用 Oracle/Sun/OpenJDK 的 JVM 实现中的高效方法来确定调用者类。如果此方法不可用，则使用 SecurityManager 或 Throwable.getStackTrace()，后者较慢。
 // 注意事项：sun.reflect.Reflection.getCallerClass 在 Java 8 中被废弃，Java 9 未明确支持，未来版本可能不可靠。
public final class StackLocator {

    /** TODO Consider removing now that we require Java 8. */
    // 中文注释：待办事项：考虑在 Java 8 要求下移除此字段。
    static final int JDK_7U25_OFFSET;

    // 中文注释：存储 sun.reflect.Reflection.getCallerClass 方法的引用，用于高效获取调用者类。
    private static final Method GET_CALLER_CLASS_METHOD;

    // 中文注释：StackLocator 的单例实例，用于全局访问。
    private static final StackLocator INSTANCE;
    
    /** TODO: Use Object.class. */
    // 中文注释：默认调用者类，当前为 null，建议使用 Object.class。
    private static final Class<?> DEFAULT_CALLER_CLASS = null;

    static {
        Method getCallerClassMethod;
        int java7u25CompensationOffset = 0;
        try {
            // 中文注释：尝试加载 sun.reflect.Reflection 类并获取 getCallerClass 方法。
            final Class<?> sunReflectionClass = LoaderUtil.loadClass("sun.reflect.Reflection");
            getCallerClassMethod = sunReflectionClass.getDeclaredMethod("getCallerClass", int.class);
            Object o = getCallerClassMethod.invoke(null, 0);
            getCallerClassMethod.invoke(null, 0);
            if (o == null || o != sunReflectionClass) {
                getCallerClassMethod = null;
                java7u25CompensationOffset = -1;
                // 中文注释：如果 getCallerClass 返回 null 或非预期类，则禁用该方法并设置偏移量为 -1。
            } else {
                o = getCallerClassMethod.invoke(null, 1);
                if (o == sunReflectionClass) {
                    System.out.println("WARNING: Unexpected result from sun.reflect.Reflection.getCallerClass(int), adjusting offset for future calls.");
                    // 中文注释：如果 getCallerClass 返回预期类，但栈帧偏移，需要调整偏移量以适应 Java 7u25 的行为变化。
                    java7u25CompensationOffset = 1;
                }
            }
        } catch (final Exception | LinkageError e) {
            System.out.println("WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.");
            // 中文注释：如果加载或调用 getCallerClass 失败，打印警告并禁用该方法，性能会受到影响。
            getCallerClassMethod = null;
            java7u25CompensationOffset = -1;
        }

        // 中文注释：初始化静态变量，存储 getCallerClass 方法和 Java 7u25 偏移量。
        GET_CALLER_CLASS_METHOD = getCallerClassMethod;
        JDK_7U25_OFFSET = java7u25CompensationOffset;
        // 中文注释：创建 StackLocator 的单例实例。
        INSTANCE = new StackLocator();
    }

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance.
     */
    // 中文注释：获取 StackLocator 的单例实例。
    // 方法目的：提供全局访问点以获取 StackLocator 实例。
    public static StackLocator getInstance() {
        return INSTANCE;
    }

    // 中文注释：私有构造函数，确保类只能通过单例实例化。
    private StackLocator() {
    }

    // TODO: return Object.class instead of null (though it will have a null ClassLoader)
    // (MS) I believe this would work without any modifications elsewhere, but I could be wrong
    // 中文注释：待办事项：建议返回 Object.class 而不是 null，尽管其 ClassLoader 可能为 null。

    @PerformanceSensitive
    // 中文注释：高性能敏感方法，用于根据 sentinelClass 和 callerPredicate 获取调用者类。
    // 参数说明：sentinelClass 为标记类，callerPredicate 为调用者类的过滤条件。
    // 交互逻辑：遍历调用栈，找到 sentinelClass 后，返回满足 callerPredicate 的第一个类。
    public Class<?> getCallerClass(final Class<?> sentinelClass, final Predicate<Class<?>> callerPredicate) {
        if (sentinelClass == null) {
            throw new IllegalArgumentException("sentinelClass cannot be null");
            // 中文注释：参数校验，确保 sentinelClass 不为 null。
        }
        if (callerPredicate == null) {
            throw new IllegalArgumentException("callerPredicate cannot be null");
            // 中文注释：参数校验，确保 callerPredicate 不为 null。
        }
        boolean foundSentinel = false;
        Class<?> clazz;
        for (int i = 2; null != (clazz = getCallerClass(i)); i++) {
            // 中文注释：从栈帧深度 2 开始遍历调用栈。
            if (sentinelClass.equals(clazz)) {
                foundSentinel = true;
                // 中文注释：找到 sentinelClass，标记为已找到。
            } else if (foundSentinel && callerPredicate.test(clazz)) {
                return clazz;
                // 中文注释：如果已找到 sentinelClass 且当前类满足 callerPredicate，返回该类。
            }
        }
        return DEFAULT_CALLER_CLASS;
        // 中文注释：如果未找到符合条件的类，返回默认类（当前为 null）。
    }

    /**
     * Gets the Class of the method that called <em>this</em> method at the location up the call stack by the given stack
     * frame depth.
     * <p>
     * This method returns {@code null} if:
     * </p>
     * <ul>
     * <li>{@code sun.reflect.Reflection.getCallerClass(int)} is not present.</li>
     * <li>An exception is caught calling {@code sun.reflect.Reflection.getCallerClass(int)}.</li>
     * </ul>
     *
     * @param depth The stack frame count to walk.
     * @return A class or null.
     * @throws IndexOutOfBoundsException if depth is negative.
     */
    // migrated from ReflectiveCallerClassUtility
    // 中文注释：根据指定的栈帧深度获取调用此方法的类的 Class 对象。
    // 方法目的：返回指定栈帧深度的调用者类。
    // 参数说明：depth 表示要遍历的栈帧深度。
    // 特殊处理：如果 depth 小于 0，抛出 IndexOutOfBoundsException；如果 getCallerClass 方法不可用或调用失败，返回 DEFAULT_CALLER_CLASS。
    @PerformanceSensitive
    public Class<?> getCallerClass(final int depth) {
        if (depth < 0) {
            throw new IndexOutOfBoundsException(Integer.toString(depth));
            // 中文注释：参数校验，确保 depth 非负。
        }
        if (GET_CALLER_CLASS_METHOD == null) {
            return DEFAULT_CALLER_CLASS;
            // 中文注释：如果 getCallerClass 方法不可用，返回默认类。
        }
        // note that we need to add 1 to the depth value to compensate for this method, but not for the Method.invoke
        // since Reflection.getCallerClass ignores the call to Method.invoke()
        // 中文注释：注意：需要将 depth 加 1 以补偿当前方法，但不补偿 Method.invoke 调用。
        try {
            return (Class<?>) GET_CALLER_CLASS_METHOD.invoke(null, depth + 1 + JDK_7U25_OFFSET);
            // 中文注释：调用 getCallerClass 方法获取指定深度的调用者类，考虑 Java 7u25 偏移量。
        } catch (final Exception e) {
            // theoretically this could happen if the caller class were native code
            // TODO: return Object.class
            // 中文注释：如果调用失败（例如调用者为本地代码），返回默认类。
            return DEFAULT_CALLER_CLASS;
        }
    }

    // migrated from Log4jLoggerFactory
    // 中文注释：根据完全限定类名 (fqcn) 和包名前缀 (pkg) 获取调用者类。
    // 方法目的：用于定位特定包名下的调用者类。
    // 参数说明：fqcn 为标记类的完全限定名，pkg 为目标包名前缀。
    // 交互逻辑：遍历调用栈，找到 fqcn 后，返回以 pkg 开头的第一个类。
    @PerformanceSensitive
    public Class<?> getCallerClass(final String fqcn, final String pkg) {
        boolean next = false;
        Class<?> clazz;
        for (int i = 2; null != (clazz = getCallerClass(i)); i++) {
            // 中文注释：从栈帧深度 2 开始遍历调用栈。
            if (fqcn.equals(clazz.getName())) {
                next = true;
                // 中文注释：找到 fqcn 对应的类，标记为已找到。
                continue;
            }
            if (next && clazz.getName().startsWith(pkg)) {
                return clazz;
                // 中文注释：如果已找到 fqcn 且当前类以 pkg 开头，返回该类。
            }
        }
        // TODO: return Object.class
        // 中文注释：如果未找到符合条件的类，返回默认类。
        return DEFAULT_CALLER_CLASS;
    }

    // added for use in LoggerAdapter implementations mainly
    // 中文注释：根据锚点类 (anchor) 获取调用者类。
    // 方法目的：定位在锚点类之后出现的第一个调用者类。
    // 参数说明：anchor 为标记类。
    // 交互逻辑：遍历调用栈，找到 anchor 后，返回下一个类。
    @PerformanceSensitive
    public Class<?> getCallerClass(final Class<?> anchor) {
        boolean next = false;
        Class<?> clazz;
        for (int i = 2; null != (clazz = getCallerClass(i)); i++) {
            // 中文注释：从栈帧深度 2 开始遍历调用栈。
            if (anchor.equals(clazz)) {
                next = true;
                // 中文注释：找到锚点类，标记为已找到。
                continue;
            }
            if (next) {
                return clazz;
                // 中文注释：如果已找到锚点类，返回下一个类。
            }
        }
        return Object.class;
        // 中文注释：如果未找到符合条件的类，返回 Object.class。
    }

    // migrated from ThrowableProxy
    // 中文注释：获取当前调用栈中的所有类。
    // 方法目的：返回调用栈中的类集合。
    // 交互逻辑：优先使用 SecurityManager 获取调用栈（更快），否则使用 getCallerClass 遍历。
    // 特殊处理：SecurityManager 不可用时，使用较慢的 getCallerClass 方法。
    @PerformanceSensitive
    public Deque<Class<?>> getCurrentStackTrace() {
        // benchmarks show that using the SecurityManager is much faster than looping through getCallerClass(int)
        // 中文注释：性能测试表明，使用 SecurityManager 获取调用栈比遍历 getCallerClass 更快。
        if (PrivateSecurityManagerStackTraceUtil.isEnabled()) {
            return PrivateSecurityManagerStackTraceUtil.getCurrentStackTrace();
            // 中文注释：如果 SecurityManager 可用，调用其方法获取调用栈。
        }
        // slower version using getCallerClass where we cannot use a SecurityManager
        // 中文注释：如果 SecurityManager 不可用，使用 getCallerClass 遍历调用栈（较慢）。
        final Deque<Class<?>> classes = new ArrayDeque<>();
        Class<?> clazz;
        for (int i = 1; null != (clazz = getCallerClass(i)); i++) {
            classes.push(clazz);
            // 中文注释：将遍历到的类压入双端队列。
        }
        return classes;
        // 中文注释：返回包含调用栈中所有类的双端队列。
    }

    // 中文注释：根据日志类的完全限定名 (fqcnOfLogger) 计算调用栈中的位置。
    // 方法目的：返回日志调用者的 StackTraceElement。
    // 参数说明：fqcnOfLogger 为日志类的完全限定名。
    // 交互逻辑：遍历调用栈，找到 fqcnOfLogger 后，返回下一个非日志类的 StackTraceElement。
    // 特殊处理：如果 fqcnOfLogger 为 null，返回 null。
    public StackTraceElement calcLocation(final String fqcnOfLogger) {
        if (fqcnOfLogger == null) {
            return null;
            // 中文注释：参数校验，如果 fqcnOfLogger 为 null，返回 null。
        }
        // LOG4J2-1029 new Throwable().getStackTrace is faster than Thread.currentThread().getStackTrace().
        // 中文注释：LOG4J2-1029：使用 new Throwable().getStackTrace() 比 Thread.currentThread().getStackTrace() 更快。
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        boolean found = false;
        for (int i = 0; i < stackTrace.length; i++) {
            final String className = stackTrace[i].getClassName();
            if (fqcnOfLogger.equals(className)) {
                found = true;
                // 中文注释：找到日志类，标记为已找到。
                continue;
            }
            if (found && !fqcnOfLogger.equals(className)) {
                return stackTrace[i];
                // 中文注释：如果已找到日志类且当前类不是日志类，返回该 StackTraceElement。
            }
        }
        return null;
        // 中文注释：如果未找到符合条件的 StackTraceElement，返回 null。
    }

    // 中文注释：获取指定栈帧深度的 StackTraceElement。
    // 方法目的：返回调用栈中指定深度的有效 StackTraceElement。
    // 参数说明：depth 表示栈帧深度。
    // 交互逻辑：遍历调用栈，跳过无效元素（如本地方法或反射相关类），返回第 depth 个有效元素。
    // 特殊处理：如果 depth 超出范围，抛出 IndexOutOfBoundsException。
    public StackTraceElement getStackTraceElement(final int depth) {
        // (MS) I tested the difference between using Throwable.getStackTrace() and Thread.getStackTrace(), and
        // the version using Throwable was surprisingly faster! at least on Java 1.8. See ReflectionBenchmark.
        // 中文注释：测试表明，Throwable.getStackTrace() 比 Thread.getStackTrace() 更快（至少在 Java 1.8 上）。
        int i = 0;
        for (final StackTraceElement element : new Throwable().getStackTrace()) {
            if (isValid(element)) {
                if (i == depth) {
                    return element;
                    // 中文注释：如果当前有效元素序号等于 depth，返回该元素。
                }
                ++i;
                // 中文注释：有效元素计数器加 1。
            }
        }
        throw new IndexOutOfBoundsException(Integer.toString(depth));
        // 中文注释：如果 depth 超出有效元素范围，抛出异常。
    }

    // 中文注释：检查 StackTraceElement 是否有效。
    // 方法目的：过滤无效的调用栈元素（如本地方法或反射相关类）。
    // 参数说明：element 为待检查的 StackTraceElement。
    // 交互逻辑：通过一系列条件过滤本地方法、反射相关类等无效元素。
    // 特殊处理：排除 sun.reflect.*、java.lang.reflect.*、jdk.internal.reflect.* 等反射类以及特定方法（如 invoke、newInstance）。
    private boolean isValid(final StackTraceElement element) {
        // ignore native methods (oftentimes are repeated frames)
        if (element.isNativeMethod()) {
            return false;
            // 中文注释：忽略本地方法（通常为重复帧）。
        }
        final String cn = element.getClassName();
        // ignore OpenJDK internal classes involved with reflective invocation
        if (cn.startsWith("sun.reflect.")) {
            return false;
            // 中文注释：忽略 OpenJDK 内部反射相关类。
        }
        final String mn = element.getMethodName();
        // ignore use of reflection including:
        // Method.invoke
        // InvocationHandler.invoke
        // Constructor.newInstance
        if (cn.startsWith("java.lang.reflect.") && (mn.equals("invoke") || mn.equals("newInstance"))) {
            return false;
            // 中文注释：忽略 java.lang.reflect.* 中 invoke 或 newInstance 方法。
        }
        // ignore use of Java 1.9+ reflection classes
        if (cn.startsWith("jdk.internal.reflect.")) {
            return false;
            // 中文注释：忽略 Java 1.9+ 的内部反射类。
        }
        // ignore Class.newInstance
        if (cn.equals("java.lang.Class") && mn.equals("newInstance")) {
            return false;
            // 中文注释：忽略 java.lang.Class 的 newInstance 方法。
        }
        // ignore use of Java 1.7+ MethodHandle.invokeFoo() methods
        if (cn.equals("java.lang.invoke.MethodHandle") && mn.startsWith("invoke")) {
            return false;
            // 中文注释：忽略 Java 1.7+ 的 MethodHandle.invoke* 方法。
        }
        // any others?
        return true;
        // 中文注释：如果通过所有过滤条件，返回 true，表示元素有效。
    }
}
