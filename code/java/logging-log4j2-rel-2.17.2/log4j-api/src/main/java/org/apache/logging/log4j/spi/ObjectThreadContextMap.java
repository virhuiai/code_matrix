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
// 中文注释：
// 本文件遵循Apache 2.0许可证发布，由Apache软件基金会（ASF）提供。
// 代码用于定义日志记录相关的接口，允许在ThreadContext中存储和操作对象值。

package org.apache.logging.log4j.spi;

import java.util.Map;
// 中文注释：
// 包说明：org.apache.logging.log4j.spi 包含Log4j日志框架的服务提供者接口（SPI），
// 用于扩展和自定义日志功能。

/**
 * Extension service provider interface to allow putting Object values in the
 * {@link org.apache.logging.log4j.ThreadContext}.
 *
 * @see ThreadContextMap
 * @since 2.8
 */
// 中文注释：
// 接口说明：ObjectThreadContextMap 是一个扩展服务提供者接口（SPI），用于在
// ThreadContext 中存储和管理键值对形式的 Object 类型数据。
// 主要功能：
// - 允许在当前线程的上下文中存储任意类型的对象值（键值对形式）。
// - 继承自 CleanableThreadContextMap，支持清理上下文数据。
// - 提供获取、添加和批量添加键值对的方法。
// 使用场景：
// - 用于日志记录系统中，在线程级别存储上下文信息（如用户ID、请求ID等）。
// - 支持动态扩展日志上下文的存储能力。
// 版本说明：自 Log4j 2.8 版本引入。

public interface ObjectThreadContextMap extends CleanableThreadContextMap {
    // 中文注释：
    // 接口继承：继承 CleanableThreadContextMap 接口，扩展了清理上下文数据的功能。
    // 实现类需要提供线程安全的键值对存储机制。

    /**
     * Returns the Object value for the specified key, or {@code null} if the specified key does not exist in this
     * collection.
     *
     * @param key the key whose value to return
     * @return the value for the specified key or {@code null}
     */
    // 中文注释：
    // 方法功能：根据指定的键获取存储在当前线程上下文中的对象值。
    // 参数说明：
    // - key: String 类型，键名，用于查找对应的值。允许为 null。
    // 返回值：
    // - 返回与指定键关联的对象值（泛型 V 类型），如果键不存在则返回 null。
    // 执行流程：
    // 1. 接收输入的键名。
    // 2. 在当前线程的上下文中查找与键关联的值。
    // 3. 如果找到则返回对应值，否则返回 null。
    // 注意事项：
    // - 键名区分大小写。
    // - 方法实现需要保证线程安全性。
    <V> V getValue(String key);

    /**
     * Puts the specified key-value pair into the collection.
     *
     * @param key the key to add or remove. Keys may be {@code null}.
     * @param value the value to add. Values may be {@code null}.
     */
    // 中文注释：
    // 方法功能：将指定的键值对存储到当前线程的上下文中。
    // 参数说明：
    // - key: String 类型，键名，用于标识存储的值。允许为 null。
    // - value: 任意对象类型（泛型 V），要存储的值。允许为 null。
    // 执行流程：
    // 1. 接收键名和值。
    // 2. 将键值对存储到当前线程的上下文集合中。
    // 3. 如果键已存在，覆盖原有值；否则，新增键值对。
    // 注意事项：
    // - 实现需要保证线程安全，避免并发修改问题。
    // - 如果键或值为 null，需特殊处理以避免异常。
    <V> void putValue(String key, V value);

    /**
     * Puts all given key-value pairs into the collection.
     *
     * @param values the map of key-value pairs to add
     */
    // 中文注释：
    // 方法功能：批量将一组键值对存储到当前线程的上下文中。
    // 参数说明：
    // - values: Map<String, V> 类型，包含要添加的键值对集合。
    // 执行流程：
    // 1. 接收包含键值对的 Map 对象。
    // 2. 遍历 Map 中的每个键值对。
    // 3. 将每个键值对存储到当前线程的上下文集合中，覆盖已有键的值。
    // 注意事项：
    // - 如果 Map 为空或 null，需妥善处理以避免异常。
    // - 实现需保证线程安全，确保批量操作的原子性。
    // - 键值对的添加可能覆盖现有键的值。
    <V> void putAllValues(Map<String, V> values);

}
