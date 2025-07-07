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
/*
 * 该文件遵循 Apache 2.0 许可证，版权归 Apache 软件基金会所有。
 * 软件按“原样”分发，不提供任何明示或暗示的保证。
 * 有关具体权限和限制，请参阅许可证内容。
 */

package org.apache.logging.log4j.spi;

import java.util.List;

/**
 * LoggerContexts implementing this are able register LoggerContextShutdownAware classes.
 */
/*
 * 接口说明：
 * LoggerContextShutdownEnabled 是一个接口，定义了支持注册 LoggerContextShutdownAware 类的 LoggerContext 的功能。
 * 主要功能：允许 LoggerContext 在关闭时通知已注册的监听器，执行清理或资源释放等操作。
 * 目的：提供一种机制，让实现此接口的 LoggerContext 支持关闭事件的监听与处理，确保系统在关闭时能够优雅地处理相关资源。
 */
public interface LoggerContextShutdownEnabled {

    /**
     * Adds a shutdown listener to the context.
     * @param listener The LoggerContextShutdownAware to add.
     */
    /*
     * 方法说明：
     * addShutdownListener 方法用于向 LoggerContext 添加一个关闭事件监听器。
     *
     * 参数说明：
     * - listener: 类型为 LoggerContextShutdownAware，表示需要注册的关闭事件监听器对象。
     *   该监听器会在 LoggerContext 关闭时被调用，执行特定的清理逻辑。
     *
     * 执行流程：
     * 1. 接收传入的 listener 参数。
     * 2. 将该监听器添加到 LoggerContext 的监听器列表中，以便在上下文关闭时通知该监听器。
     *
     * 注意事项：
     * - 实现类需要确保 listener 不为 null，以避免空指针异常。
     * - 监听器的注册顺序可能影响其被调用的顺序，具体取决于实现。
     */
    void addShutdownListener(LoggerContextShutdownAware listener);

    /**
     * Returns the list of registered shutdown listeners.
     * @return A list of LoggerContextShutdownAware listeners.
     */
    /*
     * 方法说明：
     * getListeners 方法用于获取当前 LoggerContext 中已注册的所有关闭事件监听器列表。
     *
     * 返回值说明：
     * - 返回类型为 List<LoggerContextShutdownAware>，包含所有已注册的 LoggerContextShutdownAware 监听器。
     *
     * 执行流程：
     * 1. 返回当前存储在 LoggerContext 中的监听器列表。
     * 2. 如果没有注册任何监听器，可能返回空列表（具体取决于实现）。
     *
     * 注意事项：
     * - 返回的列表可能是不可修改的（immutable），调用方不应尝试直接修改列表内容。
     * - 实现类需要确保返回的列表是线程安全的，或者明确文档说明其线程安全性。
     *
     * 使用场景：
     * - 可用于检查当前有哪些监听器被注册，或者在调试时查看监听器状态。
     */
     List<LoggerContextShutdownAware> getListeners();
}
