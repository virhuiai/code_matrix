/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
// 本文件遵循 Apache 2.0 许可证发布，归 Apache 软件基金会所有，明确了版权归属和使用权限。
// 用户需遵守许可证条款，文件按“原样”分发，不提供任何明示或暗示的担保。

package org.apache.logging.log4j.util;
// 中文注释：
// 包路径：org.apache.logging.log4j.util，属于 Apache Log4j 工具包，
// 用于提供日志记录相关的实用功能。

/**
 * An extension of {@code ReadOnlyStringMap} that imposes a total ordering on its keys.
 * The map is ordered according to the natural ordering of its keys. This order is reflected when
 * {@link #forEach(BiConsumer) consuming} the key-value pairs with a {@link BiConsumer} or a {@link TriConsumer}.
 * <p>
 * This interface views all key-value pairs as a sequence ordered by key, and allows
 * keys and values to be accessed by their index in the sequence.
 * </p>
 *
 * @see ReadOnlyStringMap
 * @since 2.8
 */
// 中文注释：
// 接口：IndexedReadOnlyStringMap，继承自 ReadOnlyStringMap，扩展了键的完全排序功能。
// 主要功能：
// - 提供对键值对的只读访问，并按键的自然顺序（natural ordering）进行排序。
// - 允许通过索引访问键值对，将键值对视为按键排序的序列。
// - 支持通过 BiConsumer 或 TriConsumer 遍历键值对，遍历顺序反映键的排序。
// 使用场景：适用于需要按键顺序访问或处理键值对的场景。
// 注意事项：
// - 该接口自 Log4j 2.8 版本开始引入。
// - 依赖 ReadOnlyStringMap 的基础功能，仅扩展索引访问和排序能力。

public interface IndexedReadOnlyStringMap extends ReadOnlyStringMap {
    // 中文注释：
    // 接口定义：IndexedReadOnlyStringMap，继承 ReadOnlyStringMap，提供基于索引的键值访问功能。

    /**
     * Viewing all key-value pairs as a sequence sorted by key, this method returns the key at the specified index,
     * or {@code null} if the specified index is less than zero or greater or equal to the size of this collection.
     *
     * @param index the index of the key to return
     * @return the key at the specified index or {@code null}
     */
    // 中文注释：
    // 方法：getKeyAt
    // 功能：获取指定索引处的键，键值对按键的自然顺序排序。
    // 参数：
    // - index：要获取的键的索引，整数类型。
    // 返回值：
    // - String：指定索引处的键，如果索引无效（小于 0 或大于等于集合大小），返回 null。
    // 执行流程：
    // 1. 接收索引参数，检查其有效性。
    // 2. 若索引有效，返回按键排序序列中对应索引的键；否则返回 null。
    // 注意事项：
    // - 索引从 0 开始，需确保索引在有效范围内。
    // - 该方法不修改集合，仅提供只读访问。
    String getKeyAt(final int index);

    /**
     * Viewing all key-value pairs as a sequence sorted by key, this method returns the value at the specified index,
     * or {@code null} if the specified index is less than zero or greater or equal to the size of this collection.
     *
     * @param index the index of the value to return
     * @return the value at the specified index or {@code null}
     */
    // 中文注释：
    // 方法：getValueAt
    // 功能：获取指定索引处的值，键值对按键的自然顺序排序。
    // 参数：
    // - index：要获取的值的索引，整数类型。
    // 返回值：
    // - V：指定索引处的值（泛型类型），如果索引无效（小于 0 或大于等于集合大小），返回 null。
    // 执行流程：
    // 1. 接收索引参数，检查其有效性。
    // 2. 若索引有效，返回按键排序序列中对应索引的值；否则返回 null。
    // 注意事项：
    // - 与 getKeyAt 配对使用，共同支持按索引访问键值对。
    // - 值类型为泛型 V，支持灵活的数据类型。
    <V> V getValueAt(final int index);

    /**
     * Viewing all key-value pairs as a sequence sorted by key, this method returns the index of the specified key in
     * that sequence. If the specified key is not found, this method returns {@code (-(insertion point) - 1)}.
     *
     * @param key the key whose index in the ordered sequence of keys to return
     * @return the index of the specified key or {@code (-(insertion point) - 1)} if the key is not found.
     *          The insertion point is defined as the point at which the key would be inserted into the array:
     *          the index of the first element in the range greater than the key, or {@code size()} if all elements
     *          are less than the specified key. Note that this guarantees that the return value will be &gt;= 0
     *          if and only if the key is found.
     */
    // 中文注释：
    // 方法：indexOfKey
    // 功能：查找指定键在按键排序序列中的索引。
    // 参数：
    // - key：要查找的键，字符串类型。
    // 返回值：
    // - int：键在排序序列中的索引（大于或等于 0）；如果键不存在，返回 (-(插入点) - 1)。
    //   - 插入点定义：键应插入的位置，即第一个大于该键的元素索引，或集合大小（如果所有元素都小于该键）。
    // 执行流程：
    // 1. 接收键参数，在按键排序的序列中查找。
    // 2. 如果找到键，返回其索引。
    // 3. 如果未找到，返回负值，表示键应插入的位置（通过插入点计算）。
    // 注意事项：
    // - 返回值大于或等于 0 表示键存在；负值表示键不存在，并提供插入点信息。
    // - 该方法采用二分查找思想，效率较高，适用于有序键集合。
    int indexOfKey(final String key);
}
