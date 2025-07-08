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

import org.apache.logging.log4j.status.StatusLogger;

/**
 * Closes resources.
 */
// 该类提供关闭各种资源的功能。
public final class Closer {

    private Closer() {
        // empty
    }

    /**
     * Closes an AutoCloseable or ignores if {@code null}.
     *
     * @param closeable the resource to close; may be null
     * @param closeable 要关闭的资源；可能为 null
     * @return Whether the resource was closed.
     * @return 资源是否被成功关闭。
     * @throws Exception if the resource cannot be closed
     * @throws Exception 如果资源无法关闭，则抛出异常。
     * @since 2.8
     * @since 2.8 版本引入
     * @since 2.11.2 returns a boolean instead of being a void return type.
     * @since 2.11.2 版本修改，返回布尔值而非 void。
     */
    public static boolean close(final AutoCloseable closeable) throws Exception {
        // close 方法的主要功能是关闭一个 AutoCloseable 资源。
        // 代码执行流程：
        // 1. 检查传入的 closeable 对象是否为 null。
        if (closeable != null) {
            // 2. 如果不为 null，则通过 StatusLogger 记录调试信息，表明正在关闭哪个资源。
            //    关键变量 closeable.getClass().getSimpleName() 用于获取资源的类名，closeable 用于获取资源的字符串表示。
            StatusLogger.getLogger().debug("Closing {} {}", closeable.getClass().getSimpleName(), closeable);
            // 3. 调用资源的 close() 方法进行关闭。
            closeable.close();
            // 4. 返回 true 表示资源已被尝试关闭。
            return true;
        }
        // 5. 如果 closeable 为 null，则直接返回 false，表示没有资源可关闭。
        return false;
    }

    /**
     * Closes an AutoCloseable and returns {@code true} if it closed without exception.
     *
     * @param closeable the resource to close; may be null
     * @param closeable 要关闭的资源；可能为 null
     * @return true if resource was closed successfully, or false if an exception was thrown
     * @return 如果资源成功关闭返回 true，如果抛出异常返回 false
     */
    public static boolean closeSilently(final AutoCloseable closeable) {
        // closeSilently 方法的主要功能是静默关闭一个 AutoCloseable 资源，即捕获并忽略关闭过程中可能抛出的异常。
        // 代码执行流程：
        // 1. 使用 try-catch 块包裹对 close() 方法的调用，以捕获任何可能发生的异常。
        try {
            // 2. 调用 close() 方法尝试关闭资源。close() 方法会返回一个布尔值，指示是否成功尝试关闭。
            return close(closeable);
        } catch (final Exception ignored) {
            // 3. 如果 close() 方法抛出任何异常，该异常将被捕获并赋值给 ignored 变量（表示忽略）。
            // 4. 捕获到异常后，返回 false，表示资源未能成功关闭（或者在关闭过程中发生了错误）。
            // 特殊处理逻辑：该方法不向上传播关闭资源时可能发生的异常，而是将其静默处理。
            return false;
        }
    }

}
