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
 * 根据Apache许可证2.0版本授权的版权声明，详细说明了代码的使用权限和限制。
 * 该文件由Apache软件基金会授权，遵循许可证条款，禁止在不符合许可证的情况下使用。
 */

package org.apache.logging.log4j.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code ThreadContextMap} implementation used when either of system properties {@code disableThreadContextMap} or .
 * {@code disableThreadContext} is {@code true}. This implementation does nothing.
 *
 * @since 2.7
 */
/*
 * 类说明：
 * NoOpThreadContextMap 是 ThreadContextMap 接口的一个空实现类，用于在系统属性 disableThreadContextMap 或 disableThreadContext
 * 被设置为 true 时禁用线程上下文映射功能。此实现不执行任何实际操作，仅提供接口方法占位符。
 *
 * 主要功能和目的：
 * - 提供 ThreadContextMap 接口的空实现，确保在禁用线程上下文功能时，相关方法调用不产生任何效果。
 * - 用于 Log4j 日志框架中，优化性能或在特定场景下避免线程上下文数据的使用。
 *
 * 使用场景：
 * - 当系统属性 disableThreadContextMap 或 disableThreadContext 设置为 true 时，Log4j 将使用此实现。
 * - 适用于不需要线程上下文存储的轻量级日志场景。
 *
 * 注意事项：
 * - 此类方法均为无操作（no-op）实现，不存储或处理任何数据。
 * - 所有方法返回值均符合接口定义，但不涉及实际数据操作。
 *
 * @since 2.7 表示该类自 Log4j 2.7 版本起引入。
 */
public class NoOpThreadContextMap implements ThreadContextMap {

    /**
     * Clears the thread context map.
     */
    /*
     * 方法说明：
     * clear 方法用于清除线程上下文映射中的所有数据。
     *
     * 功能和目的：
     * - 按照 ThreadContextMap 接口要求，实现清除操作。
     * - 在此空实现中，方法不执行任何操作，仅为接口合规性提供占位符。
     *
     * 执行流程：
     * - 无实际操作，直接返回。
     *
     * 注意事项：
     * - 由于是空实现，调用此方法不会产生任何效果。
     */
    @Override
    public void clear() {
    }

    /**
     * Returns true if the specified key exists in the map, false otherwise.
     *
     * @param key the key to look up
     * @return false
     */
    /*
     * 方法说明：
     * containsKey 方法检查指定键是否存在于线程上下文映射中。
     *
     * 参数说明：
     * - key: 要查找的键，类型为 String。
     *
     * 返回值：
     * - 始终返回 false，表示映射中不存在任何键。
     *
     * 功能和目的：
     * - 提供接口要求的键存在性检查功能。
     * - 在此空实现中，始终返回 false，因为不存储任何数据。
     *
     * 注意事项：
     * - 由于是空实现，无论传入何种键值，方法均返回 false。
     */
    @Override
    public boolean containsKey(final String key) {
        return false;
    }

    /**
     * Gets the value for the specified key.
     *
     * @param key the key to look up
     * @return null
     */
    /*
     * 方法说明：
     * get 方法用于获取指定键对应的值。
     *
     * 参数说明：
     * - key: 要查找的键，类型为 String。
     *
     * 返回值：
     * - 始终返回 null，表示映射中不存在任何键值对。
     *
     * 功能和目的：
     * - 实现 ThreadContextMap 接口的键值查询功能。
     * - 在此空实现中，始终返回 null，因为不存储任何数据。
     *
     * 注意事项：
     * - 调用此方法不会返回任何实际值，仅为接口合规性提供占位符。
     */
    @Override
    public String get(final String key) {
        return null;
    }

    /**
     * Returns a copy of the current thread context map.
     *
     * @return an empty HashMap
     */
    /*
     * 方法说明：
     * getCopy 方法返回当前线程上下文映射的副本。
     *
     * 返回值：
     * - 返回一个空的 HashMap 对象，表示映射中无任何数据。
     *
     * 功能和目的：
     * - 提供接口要求的映射副本获取功能。
     * - 在此空实现中，返回一个新的空 HashMap，因为不存储任何数据。
     *
     * 执行流程：
     * - 创建并返回一个空的 HashMap 对象。
     *
     * 注意事项：
     * - 返回的 HashMap 是新创建的空映射，不会包含任何键值对。
     */
    @Override
    public Map<String, String> getCopy() {
        return new HashMap<>();
    }

    /**
     * Returns an immutable copy of the thread context map or null.
     *
     * @return null
     */
    /*
     * 方法说明：
     * getImmutableMapOrNull 方法返回线程上下文映射的不可变副本或 null。
     *
     * 返回值：
     * - 始终返回 null，表示不存在可用的不可变映射。
     *
     * 功能和目的：
     * - 实现 ThreadContextMap 接口要求的不可变映射获取功能。
     * - 在此空实现中，始终返回 null，因为不存储任何数据。
     *
     * 注意事项：
     * - 调用此方法始终返回 null，仅为接口合规性提供占位符。
     */
    @Override
    public Map<String, String> getImmutableMapOrNull() {
        return null;
    }

    /**
     * Returns true if the thread context map is empty.
     *
     * @return true
     */
    /*
     * 方法说明：
     * isEmpty 方法检查线程上下文映射是否为空。
     *
     * 返回值：
     * - 始终返回 true，表示映射始终为空。
     *
     * 功能和目的：
     * - 提供接口要求的映射空状态检查功能。
     * - 在此空实现中，始终返回 true，因为不存储任何数据。
     *
     * 注意事项：
     * - 由于是空实现，映射始终为空，方法仅返回固定值 true。
     */
    @Override
    public boolean isEmpty() {
        return true;
    }

    /**
     * Puts a key-value pair into the thread context map.
     *
     * @param key   the key
     * @param value the value
     */
    /*
     * 方法说明：
     * put 方法用于向线程上下文映射中添加键值对。
     *
     * 参数说明：
     * - key: 要添加的键，类型为 String。
     * - value: 要添加的值，类型为 String。
     *
     * 功能和目的：
     * - 实现 ThreadContextMap 接口的键值对添加功能。
     * - 在此空实现中，方法不执行任何操作，仅为接口合规性提供占位符。
     *
     * 执行流程：
     * - 无实际操作，直接返回。
     *
     * 注意事项：
     * - 由于是空实现，调用此方法不会存储任何数据。
     */
    @Override
    public void put(final String key, final String value) {
    }

    /**
     * Removes a key from the thread context map.
     *
     * @param key the key to remove
     */
    /*
     * 方法说明：
     * remove 方法用于从线程上下文映射中移除指定键。
     *
     * 参数说明：
     * - key: 要移除的键，类型为 String。
     *
     * 功能和目的：
     * - 实现 ThreadContextMap 接口的键移除功能。
     * - 在此空实现中，方法不执行任何操作，仅为接口合规性提供占位符。
     *
     * 执行流程：
     * - 无实际操作，直接返回。
     *
     * 注意事项：
     * - 由于是空实现，调用此方法不会产生任何效果。
     */
    @Override
    public void remove(final String key) {
    }
}
