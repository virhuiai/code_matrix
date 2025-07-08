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

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Registry used for Runnable shutdown callback instances. Due to differing requirements of how late in the JVM
 * lifecycle Log4j should be shut down, this interface is provided for customizing how to register shutdown hook
 * callbacks. Implementations may optionally implement {@link org.apache.logging.log4j.core.LifeCycle}.
 *
 * 用于注册 Runnable 关闭回调实例的注册表。由于 Log4j 在 JVM 生命周期中何时关闭的要求不同，
 * 此接口用于自定义如何注册关闭钩子回调。实现可以选择性地实现 {@link org.apache.logging.log4j.core.LifeCycle} 接口。
 *
 * @since 2.1
 */
public interface ShutdownCallbackRegistry {

    /**
     * System property to set to choose the ShutdownCallbackRegistry.
     * 用于设置选择 ShutdownCallbackRegistry 实现的系统属性。
     */
    String SHUTDOWN_CALLBACK_REGISTRY = "log4j.shutdownCallbackRegistry";

    /**
     * System property to set to override the global ability to register shutdown hooks.
     * 用于设置覆盖全局注册关闭钩子能力的系统属性。
     */
    String SHUTDOWN_HOOK_ENABLED = "log4j.shutdownHookEnabled";

    /**
     * Shared Marker to indicate log messages corresponding to shutdown hooks.
     * 共享的 Marker，用于指示与关闭钩子相关的日志消息。
     */
    Marker SHUTDOWN_HOOK_MARKER = MarkerManager.getMarker("SHUTDOWN HOOK");

    /**
     * Adds a Runnable shutdown callback to this class.
     * 将一个 Runnable 关闭回调添加到此类中。
     *
     * Note: The returned {@code Cancellable} must be retained on heap by caller
     * to avoid premature garbage-collection of the registered callback (and to ensure
     * the callback runs on shutdown).
     * 注意：调用者必须在堆上保留返回的 {@code Cancellable}，
     * 以避免已注册的回调过早被垃圾回收（并确保回调在关闭时运行）。
     *
     * @param callback the shutdown callback to be executed upon shutdown.
     * 要在关闭时执行的关闭回调。
     * @return a Cancellable wrapper of the provided callback or {@code null} if the shutdown hook is disabled and
     * cannot be added.
     * 提供的回调的可取消包装器，如果关闭钩子被禁用且无法添加，则返回 {@code null}。
     * @since 2.1
     */
    Cancellable addShutdownCallback(Runnable callback);
}
