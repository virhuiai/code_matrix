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
 * 中文注释：
 * 本代码文件遵循Apache 2.0许可证发布，归Apache软件基金会所有。
 * 用户需遵守许可证条款使用本文件，具体条款可参考上述链接。
 * 本文件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.util;

/**
 * An extension of {@code StringMap} that imposes a total ordering on its keys.
 * The map is ordered according to the natural ordering of its keys. This order is reflected when
 * {@link #forEach(BiConsumer) consuming} the key-value pairs with a {@link BiConsumer} or a {@link TriConsumer}.
 * <p>
 * This interface views all key-value pairs as a sequence ordered by key, and allows
 * keys and values to be accessed by their index in the sequence.
 * </p>
 *
 * @see IndexedReadOnlyStringMap
 * @see StringMap
 * @since 2.8
 */
/*
 * 中文注释：
 *
 * 类功能与目的：
 * IndexedStringMap 接口是 StringMap 的扩展，增加了对键的全序排序功能。
 * 它继承了 IndexedReadOnlyStringMap 和 StringMap 接口，提供了按键的自然顺序排列键值对的能力。
 * 该接口的主要目的是支持按顺序访问键值对，并允许通过索引访问键和值。
 *
 * 继承关系：
 * - 继承 IndexedReadOnlyStringMap，提供只读的索引访问功能。
 * - 继承 StringMap，提供基本的键值对存储和操作功能。
 *
 * 关键功能：
 * - 键值对按键的自然顺序（natural ordering）进行排序。
 * - 支持通过 BiConsumer 或 TriConsumer 遍历键值对，遍历顺序反映键的排序。
 * - 允许通过索引访问键值对，将键值对视为一个有序序列。
 *
 * 执行流程：
 * 1. 键值对存储时，自动按照键的自然顺序进行排序。
 * 2. 遍历操作（如 forEach）按照键的顺序依次处理键值对。
 * 3. 提供索引访问方法，允许用户通过索引获取特定的键或值。
 *
 * 注意事项：
 * - 键的自然顺序取决于键的类型及其实现的 Comparable 接口。
 * - 该接口未定义新的方法，仅通过继承组合 IndexedReadOnlyStringMap 和 StringMap 的功能。
 * - 实现类需要确保键值对的排序一致性，以避免遍历或索引访问时的不一致行为。
 *
 * 使用场景：
 * 适用于需要按键顺序处理键值对的场景，例如日志系统中的配置管理或有序数据处理。
 *
 * 版本信息：
 * 自 Log4j 2.8 版本开始提供。
 */
public interface IndexedStringMap extends IndexedReadOnlyStringMap, StringMap {
    // nothing more
    /*
     * 中文注释：
     * 接口未定义额外的方法，仅通过继承 IndexedReadOnlyStringMap 和 StringMap 提供功能。
     * 实现类需确保同时满足只读索引访问和键值对操作的需求。
     */
}
