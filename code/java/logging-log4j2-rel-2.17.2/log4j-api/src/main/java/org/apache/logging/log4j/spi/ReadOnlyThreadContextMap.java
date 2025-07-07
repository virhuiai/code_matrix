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
package org.apache.logging.log4j.spi;

import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.StringMap;

/**
 * Read-only view of the data structure that implements MDC behavior for {@link org.apache.logging.log4j.ThreadContext}.
 * <p>
 * {@code ThreadContextMap} implementations that also implement this interface can be accessed
 * by applications via the {@link ThreadContext#getThreadContextMap()} method.
 * </p>
 *
 * @see ThreadContext#getThreadContextMap()
 * @since 2.8
 */
/*
 * 只读线程上下文映射接口，提供对ThreadContext的MDC（Mapped Diagnostic Context）行为的只读访问。
 * 主要功能：为应用程序提供一种安全的方式，以只读形式访问线程上下文数据，避免直接修改底层数据结构。
 * 实现类通过ThreadContext.getThreadContextMap()方法暴露给调用者。
 * 注意事项：此接口设计为只读，调用者不应尝试修改返回的数据结构。
 * 自2.8版本起引入。
 */
public interface ReadOnlyThreadContextMap {

    /**
     * Clears the context.
     */
    /*
     * 清空线程上下文中的所有键值对。
     * 功能：移除当前线程上下文中的所有数据。
     * 执行流程：直接清空底层数据结构中的内容。
     * 注意事项：此操作会影响当前线程的上下文数据，使用时需谨慎。
     */
    void clear();

    /**
     * Determines if the key is in the context.
     * @param key The key to locate.
     * @return True if the key is in the context, false otherwise.
     */
    /*
     * 判断指定键是否存在于线程上下文中。
     * 参数：
     *   - key：要查找的键，类型为String。
     * 返回值：
     *   - boolean：如果键存在于上下文中返回true，否则返回false。
     * 功能：检查上下文是否包含指定的键。
     * 执行流程：查询底层数据结构，判断键是否存在。
     * 注意事项：此方法无副作用，仅用于查询。
     */
    boolean containsKey(final String key);

    /**
     * Gets the context identified by the <code>key</code> parameter.
     *
     * <p>This method has no side effects.</p>
     * @param key The key to locate.
     * @return The value associated with the key or null.
     */
    /*
     * 获取与指定键关联的上下文值。
     * 参数：
     *   - key：要查找的键，类型为String。
     * 返回值：
     *   - String：与键关联的值，如果键不存在则返回null。
     * 功能：从线程上下文中检索指定键的值。
     * 执行流程：通过键查询底层数据结构，返回对应的值或null。
     * 注意事项：此方法为只读操作，不会对上下文产生副作用。
     */
    String get(final String key);

    /**
     * Gets a non-{@code null} mutable copy of current thread's context Map.
     * @return a mutable copy of the context.
     */
    /*
     * 获取当前线程上下文映射的可变副本。
     * 返回值：
     *   - Map<String, String>：上下文的键值对副本，始终非null。
     * 功能：返回一个可修改的上下文副本，允许调用者对副本进行操作而不影响原始上下文。
     * 执行流程：创建并返回底层上下文数据的深拷贝。
     * 注意事项：返回的Map是可变的，但修改它不会影响原始线程上下文。
     */
    Map<String, String> getCopy();

    /**
     * Returns an immutable view on the context Map or {@code null} if the context map is empty.
     * @return an immutable context Map or {@code null}.
     */
    /*
     * 获取上下文映射的不可变视图。
     * 返回值：
     *   - Map<String, String>：上下文的不可变键值对视图，如果上下文为空则返回null。
     * 功能：提供线程上下文的只读视图，防止外部修改。
     * 执行流程：如果上下文不为空，返回其不可变视图；否则返回null。
     * 注意事项：返回的Map不可修改，适合需要只读访问的场景。
     */
    Map<String, String> getImmutableMapOrNull();

    /**
     * Returns the context data for reading. Note that regardless of whether the returned context data has been
     * {@linkplain StringMap#freeze() frozen} (made read-only) or not, callers should not attempt to modify
     * the returned data structure.
     * <p>
     * <b>Thread safety note:</b>
     * </p>
     * <p>
     * If this {@code ReadOnlyThreadContextMap} implements {@link CopyOnWrite}, then the returned {@code StringMap} can
     * safely be passed to another thread: future changes in the underlying context data will not be reflected in the
     * returned {@code StringMap}.
     * </p><p>
     * Otherwise, if this {@code ReadOnlyThreadContextMap} does <em>not</em> implement {@link CopyOnWrite}, then it is
     * not safe to pass the returned {@code StringMap} to another thread because changes in the underlying context may
     * be reflected in the returned object. It is the responsibility of the caller to make a copy to pass to another
     * thread.
     * </p>
     *
     * @return a {@code StringMap} containing context data key-value pairs
     */
    /*
     * 获取线程上下文数据的只读视图。
     * 返回值：
     *   - StringMap：包含上下文键值对的数据结构。
     * 功能：提供上下文数据的只读访问，适用于需要安全共享数据的场景。
     * 执行流程：返回底层上下文数据的StringMap视图，可能被冻结（只读）或未冻结。
     * 注意事项：
     *   - 调用者不应尝试修改返回的StringMap。
     *   - 线程安全：
     *     - 如果实现支持CopyOnWrite，返回的StringMap可安全传递给其他线程，后续上下文变化不会影响该副本。
     *     - 如果不支持CopyOnWrite，返回的StringMap不适合直接传递给其他线程，因底层变化可能反映到返回对象中。
     *   - 调用者需负责在必要时创建副本以确保线程安全。
     */
    StringMap getReadOnlyContextData();

    /**
     * Returns true if the Map is empty.
     * @return true if the Map is empty, false otherwise.
     */
    /*
     * 判断上下文映射是否为空。
     * 返回值：
     *   - boolean：如果上下文为空返回true，否则返回false。
     * 功能：检查线程上下文是否不包含任何键值对。
     * 执行流程：查询底层数据结构，判断其是否为空。
     * 注意事项：此方法为只读操作，无副作用。
     */
    boolean isEmpty();
}
