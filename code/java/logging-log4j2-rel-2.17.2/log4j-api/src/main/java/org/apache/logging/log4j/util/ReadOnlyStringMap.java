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

import java.io.Serializable;
import java.util.Map;

/**
 * A read-only collection of String keys mapped to values of arbitrary type.
 *
 * @since 2.7
 */
// 中文注释：定义一个只读的键值对集合，键为字符串类型，值可以是任意类型。
// 说明：该接口用于表示不可修改的键值映射，主要用于日志记录的上下文数据存储。
// 自2.7版本起提供。
public interface ReadOnlyStringMap extends Serializable {
    
    /**
     * Returns a non-{@code null} mutable {@code Map<String, String>} containing a snapshot of this data structure.
     *
     * @return a mutable copy of this data structure in {@code Map<String, String>} form.
     */
    // 中文注释：返回一个非空的、可修改的Map<String, String>对象，包含当前数据结构的快照。
    // 方法目的：提供当前只读集合的可修改副本，便于外部处理。
    // 关键变量说明：返回的Map是键为String，值为String的映射。
    Map<String, String> toMap();

    /**
     * Returns {@code true} if this data structure contains the specified key, {@code false} otherwise.
     *
     * @param key the key whose presence to check. May be {@code null}.
     * @return {@code true} if this data structure contains the specified key, {@code false} otherwise.
     */
    // 中文注释：检查数据结构中是否包含指定的键。
    // 参数说明：key - 要检查的键，可以为null。
    // 返回值：如果包含指定键返回true，否则返回false。
    // 方法目的：用于验证某个键是否存在于集合中。
    boolean containsKey(String key);

    /**
     * Performs the given action for each key-value pair in this data structure
     * until all entries have been processed or the action throws an exception.
     * <p>
     * Some implementations may not support structural modifications (adding new elements or removing elements) while
     * iterating over the contents. In such implementations, attempts to add or remove elements from the
     * {@code BiConsumer}'s {@link BiConsumer#accept(Object, Object)} accept} method may cause a
     * {@code ConcurrentModificationException} to be thrown.
     * </p>
     *
     * @param action The action to be performed for each key-value pair in this collection.
     * @param <V> type of the value.
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     */
    // 中文注释：对数据结构中的每个键值对执行指定操作，直到处理完所有条目或抛出异常。
    // 参数说明：action - 对每个键值对执行的BiConsumer操作，接受键和值作为参数。
    // 事件处理逻辑：遍历所有键值对，逐一调用action的accept方法。
    // 特殊处理注意事项：某些实现可能不支持在迭代期间修改结构（如添加或删除元素），否则可能抛出ConcurrentModificationException。
    // 方法目的：提供一种遍历键值对并执行自定义逻辑的方式。
    // 关键变量说明：action是一个函数式接口，用于定义对键值对的操作逻辑。
    <V> void forEach(final BiConsumer<String, ? super V> action);

    /**
     * Performs the given action for each key-value pair in this data structure
     * until all entries have been processed or the action throws an exception.
     * <p>
     * The third parameter lets callers pass in a stateful object to be modified with the key-value pairs,
     * so the TriConsumer implementation itself can be stateless and potentially reusable.
     * </p>
     * <p>
     * Some implementations may not support structural modifications (adding new elements or removing elements) while
     * iterating over the contents. In such implementations, attempts to add or remove elements from the
     * {@code TriConsumer}'s {@link TriConsumer#accept(Object, Object, Object) accept} method may cause a
     * {@code ConcurrentModificationException} to be thrown.
     * </p>
     *
     * @param action The action to be performed for each key-value pair in this collection.
     * @param state the object to be passed as the third parameter to each invocation on the specified
     *          triconsumer.
     * @param <V> type of the value.
     * @param <S> type of the third parameter.
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     */
    // 中文注释：对数据结构中的每个键值对执行指定操作，允许传入额外的状态对象，直到处理完所有条目或抛出异常。
    // 参数说明：action - TriConsumer操作，接受键、值和状态对象作为参数；state - 传递给action的第三个参数，通常为状态对象。
    // 事件处理逻辑：遍历所有键值对，逐一调用action的accept方法，并传入state对象。
    // 特殊处理注意事项：某些实现可能不支持在迭代期间修改结构（如添加或删除元素），否则可能抛出ConcurrentModificationException。
    // 方法目的：支持在遍历键值对时传递额外状态对象，便于实现无状态且可复用的操作逻辑。
    // 关键变量说明：action定义操作逻辑，state为外部传入的可修改状态对象。
    <V, S> void forEach(final TriConsumer<String, ? super V, S> action, final S state);

    /**
     * Returns the value for the specified key, or {@code null} if the specified key does not exist in this collection.
     *
     * @param key the key whose value to return.
     * @return the value for the specified key or {@code null}.
     */
    // 中文注释：返回指定键对应的值，如果键不存在则返回null。
    // 参数说明：key - 要查询的键。
    // 返回值：键对应的值，或null（如果键不存在）。
    // 方法目的：获取指定键的值，便于访问集合中的数据。
    <V> V getValue(final String key);

    /**
     * Returns {@code true} if this collection is empty (size is zero), {@code false} otherwise.
     * @return {@code true} if this collection is empty (size is zero).
     */
    // 中文注释：检查集合是否为空（大小为零）。
    // 返回值：如果集合为空返回true，否则返回false。
    // 方法目的：判断集合是否包含任何键值对。
    boolean isEmpty();

    /**
     * Returns the number of key-value pairs in this collection.
     *
     * @return the number of key-value pairs in this collection.
     */
    // 中文注释：返回集合中的键值对数量。
    // 返回值：键值对的总数。
    // 方法目的：提供集合的大小信息。
    int size();
}
