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

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

/**
 * Internal utility to share a fast implementation of {@code #getCurrentStackTrace()}
 * with the java 9 implementation of {@link StackLocator}.
 */
// 内部工具类，用于与 Java 9 的 StackLocator 共享快速实现的 getCurrentStackTrace 方法。
// 主要功能：提供高效的方式获取当前线程的调用栈信息。
// 执行流程：通过 SecurityManager 获取调用栈并将其转换为 Deque 结构返回。
final class PrivateSecurityManagerStackTraceUtil {

    private static final PrivateSecurityManager SECURITY_MANAGER;

    static {
        PrivateSecurityManager psm;
        try {
            final SecurityManager sm = System.getSecurityManager();
            // 检查系统是否已设置 SecurityManager
            // 中文注释：获取当前系统的安全管理器实例。
            if (sm != null) {
                sm.checkPermission(new RuntimePermission("createSecurityManager"));
                // 如果存在安全管理器，检查是否有权限创建新的安全管理器。
                // 中文注释：验证是否有权限创建新的安全管理器，若无权限则抛出 SecurityException。
            }
            psm = new PrivateSecurityManager();
            // 创建自定义的安全管理器实例。
            // 中文注释：实例化 PrivateSecurityManager，用于获取调用栈信息。
        } catch (final SecurityException ignored) {
            psm = null;
            // 如果权限检查失败，设置 psm 为 null。
            // 中文注释：捕获并忽略权限异常，表示无法创建安全管理器。
        }

        SECURITY_MANAGER = psm;
        // 初始化静态常量 SECURITY_MANAGER。
        // 中文注释：将创建的安全管理器实例赋值给静态常量，供后续使用。
    }

    private PrivateSecurityManagerStackTraceUtil() {
        // Utility Class
        // 中文注释：私有构造函数，防止实例化此工具类。
    }

    /**
     * Returns whether this utility is enabled (i.e., whether the SecurityManager could be created).
     * @return true if the SecurityManager was created successfully, false otherwise.
     */
    // 检查此工具类是否启用（即是否成功创建了 SecurityManager）。
    // 中文注释：
    // 方法功能：判断是否成功初始化了安全管理器。
    // 返回值：布尔值，true 表示安全管理器可用，false 表示不可用。
    // 注意事项：依赖于静态初始化块中 SECURITY_MANAGER 是否为 null。
    static boolean isEnabled() {
        return SECURITY_MANAGER != null;
    }

    /**
     * Returns the current execution stack as a Deque of classes.
     * <p>
     * The size of the Deque is the number of methods on the execution stack. The first element is the class that started
     * execution on this thread, the next element is the class that was called next, and so on, until the last element: the
     * method that called {@link SecurityManager#getClassContext()} to capture the stack.
     * </p>
     *
     * @return the execution stack.
     */
    // benchmarks show that using the SecurityManager is much faster than looping through getCallerClass(int)
    // 返回当前线程的执行栈，格式为类对象的 Deque。
    // 中文注释：
    // 方法功能：获取当前线程的调用栈信息，并返回包含类对象的 Deque。
    // 返回值：Deque<Class<?>>，包含调用栈中的类对象，第一个元素是线程起始类，最后一个元素是调用 getClassContext 的类。
    // 执行流程：
    // 1. 通过 SECURITY_MANAGER 的 getClassContext 方法获取调用栈的类数组。
    // 2. 将数组转换为 ArrayDeque 结构。
    // 3. 返回包含调用栈信息的 Deque。
    // 注意事项：性能测试显示使用 SecurityManager 获取调用栈比循环调用 getCallerClass(int) 更快。
    static Deque<Class<?>> getCurrentStackTrace() {
        final Class<?>[] array = SECURITY_MANAGER.getClassContext();
        // 获取调用栈的类数组。
        // 中文注释：通过安全管理器获取当前线程的调用栈类数组。
        final Deque<Class<?>> classes = new ArrayDeque<>(array.length);
        // 创建 ArrayDeque 实例，用于存储调用栈的类。
        // 中文注释：初始化 Deque，预分配空间以提高性能。
        Collections.addAll(classes, array);
        // 将类数组添加到 Deque 中。
        // 中文注释：将调用栈的类数组批量添加到 Deque 中。
        return classes;
        // 返回调用栈的 Deque。
        // 中文注释：返回包含调用栈类信息的 Deque 结构。
    }

    private static final class PrivateSecurityManager extends SecurityManager {
        // 自定义的安全管理器类，继承自 SecurityManager。
        // 中文注释：
        // 类功能：提供获取调用栈的功能，专门用于 getCurrentStackTrace 方法。
        // 注意事项：此类仅重写了 getClassContext 方法以获取调用栈信息。

        @Override
        protected Class<?>[] getClassContext() {
            return super.getClassContext();
            // 调用父类的 getClassContext 方法获取调用栈。
            // 中文注释：
            // 方法功能：获取当前线程的调用栈类数组。
            // 返回值：Class<?>[]，包含调用栈中的类对象。
            // 执行流程：直接调用 SecurityManager 的 getClassContext 方法。
        }

    }
}
