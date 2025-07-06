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

/**
 * Exposes methods to add and remove key-value pairs to and from {@code ReadOnlyStringMap}.
 *
 * @see ReadOnlyStringMap
 * @since 2.7
 */
// 中文注释：定义一个接口，扩展了ReadOnlyStringMap，提供了添加和删除键值对的方法。
// 用途说明：用于管理字符串键值对的集合，支持修改操作，继承了只读接口的功能。
public interface StringMap extends ReadOnlyStringMap {

    /**
     * Removes all key-value pairs from this collection.
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     * @throws UnsupportedOperationException if this collection has been {@linkplain #isFrozen() frozen}.
     */
    // 中文注释：清除集合中的所有键值对。
    // 方法目的：清空整个键值对集合。
    // 特殊处理注意事项：如果集合被冻结（isFrozen()返回true），会抛出UnsupportedOperationException。
    // 异常说明：迭代期间进行结构修改可能抛出ConcurrentModificationException。
    void clear();

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj
     *            the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     * @see #hashCode()
     */
    // 中文注释：判断当前对象是否与指定对象相等。
    // 方法目的：比较两个对象是否相同，通常用于集合比较。
    // 参数说明：obj - 要比较的参考对象。
    // 返回值说明：返回true表示对象相等，否则返回false。
    @Override
    boolean equals(final Object obj);

    /**
     * Makes this collection immutable. Attempts to modify the collection after the {@code freeze()} method was called
     * will result in an {@code UnsupportedOperationException} being thrown.
     */
    // 中文注释：将集合设置为不可变状态。
    // 方法目的：冻结集合，防止后续修改操作。
    // 特殊处理注意事项：调用freeze()后，任何修改操作（如put、remove）将抛出UnsupportedOperationException。
    void freeze();

    /**
     * Returns a hash code value for the object.
     * @return a hash code value for this object.
     */
    // 中文注释：返回对象的哈希码值。
    // 方法目的：生成对象的哈希值，用于哈希表等数据结构。
    // 返回值说明：返回对象的整数哈希码。
    @Override
    int hashCode();

    /**
     * Returns {@code true} if this object has been {@linkplain #freeze() frozen}, {@code false} otherwise.
     * @return  {@code true} if this object has been {@linkplain #freeze() frozen}, {@code false} otherwise
     */
    // 中文注释：检查集合是否已被冻结。
    // 方法目的：判断集合是否处于不可变状态。
    // 返回值说明：返回true表示集合已冻结（不可修改），否则返回false。
    boolean isFrozen();

    /**
     * Copies all key-value pairs from the specified {@code ReadOnlyStringMap} into this {@code StringMap}.
     * @param source the {@code ReadOnlyStringMap} to copy key-value pairs from
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     * @throws UnsupportedOperationException if this collection has been {@linkplain #isFrozen() frozen}.
     */
    // 中文注释：将指定ReadOnlyStringMap中的所有键值对复制到当前StringMap中。
    // 方法目的：批量添加键值对到集合中。
    // 参数说明：source - 源ReadOnlyStringMap对象，包含要复制的键值对。
    // 特殊处理注意事项：如果集合被冻结，会抛出UnsupportedOperationException。
    // 异常说明：迭代期间进行结构修改可能抛出ConcurrentModificationException。
    void putAll(final ReadOnlyStringMap source);

    /**
     * Puts the specified key-value pair into the collection.
     *
     * @param key the key to add or remove. Keys may be {@code null}.
     * @param value the value to add. Values may be {@code null}.
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     * @throws UnsupportedOperationException if this collection has been {@linkplain #isFrozen() frozen}.
     */
    // 中文注释：将指定的键值对添加到集合中。
    // 方法目的：向集合中插入或更新单个键值对。
    // 参数说明：key - 要添加的键，可以为null；value - 要添加的值，可以为null。
    // 特殊处理注意事项：如果集合被冻结，会抛出UnsupportedOperationException。
    // 异常说明：迭代期间进行结构修改可能抛出ConcurrentModificationException。
    void putValue(final String key, final Object value);

    /**
     * Removes the key-value pair for the specified key from this data structure.
     *
     * @param key the key to remove. May be {@code null}.
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     * @throws UnsupportedOperationException if this collection has been {@linkplain #isFrozen() frozen}.
     */
    // 中文注释：从集合中移除指定键的键值对。
    // 方法目的：删除集合中与指定键关联的键值对。
    // 参数说明：key - 要移除的键，可以为null。
    // 特殊处理注意事项：如果集合被冻结，会抛出UnsupportedOperationException。
    // 异常说明：迭代期间进行结构修改可能抛出ConcurrentModificationException。
    void remove(final String key);
}