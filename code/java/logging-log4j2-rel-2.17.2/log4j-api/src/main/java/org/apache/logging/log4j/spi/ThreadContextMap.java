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

/**
 * Service provider interface to implement custom MDC behavior for {@link org.apache.logging.log4j.ThreadContext}.
 * <p>
 * Since 2.8, {@code ThreadContextMap} implementations that implement the {@link ReadOnlyThreadContextMap} interface
 * are accessible to applications via the {@link ThreadContext#getThreadContextMap()} method.
 * </p>
 */
// 中文注释：
// 该接口定义了用于实现自定义MDC（Mapped Diagnostic Context，映射诊断上下文）行为的SPI（服务提供者接口）。
// 主要功能：为Log4j的ThreadContext提供可扩展的键值对存储功能，用于在当前线程中存储和访问上下文数据。
// 实现类可以通过ThreadContext.getThreadContextMap()方法向应用程序公开（自2.8版本起，需实现ReadOnlyThreadContextMap接口）。
// 使用场景：日志记录中需要跟踪线程特定的上下文信息，如用户ID、请求ID等。
// 注意事项：实现类需要保证线程安全，因为该接口用于多线程环境中。
public interface ThreadContextMap {

    /**
     * Clears the context.
     */
    // 中文注释：
    // 方法功能：清空当前线程的上下文映射。
    // 执行流程：移除所有存储在当前线程上下文中的键值对。
    // 注意事项：此操作会影响当前线程的所有上下文数据，需谨慎使用。
    void clear();

    /**
     * Determines if the key is in the context.
     * @param key The key to locate.
     * @return True if the key is in the context, false otherwise.
     */
    // 中文注释：
    // 方法功能：检查指定键是否存在于当前线程的上下文映射中。
    // 参数说明：
    //   - key：要查找的键，类型为String。
    // 返回值：如果键存在于上下文映射中，返回true；否则返回false。
    // 执行流程：查询上下文映射，检查指定键是否存在。
    // 注意事项：此方法不会修改上下文内容，仅执行查询操作。
    boolean containsKey(final String key);

    /**
     * Gets the context identified by the <code>key</code> parameter.
     *
     * <p>This method has no side effects.</p>
     * @param key The key to locate.
     * @return The value associated with the key or null.
     */
    // 中文注释：
    // 方法功能：根据指定键获取当前线程上下文映射中的值。
    // 参数说明：
    //   - key：要查找的键，类型为String。
    // 返回值：与指定键关联的值（String类型），如果键不存在则返回null。
    // 执行流程：从上下文映射中检索指定键的值。
    // 注意事项：
    //   - 该方法不会修改上下文内容，无副作用。
    //   - 返回值为null时，表示键不存在或值本身为null。
    String get(final String key);

    /**
     * Gets a non-{@code null} mutable copy of current thread's context Map.
     * @return a mutable copy of the context.
     */
    // 中文注释：
    // 方法功能：获取当前线程上下文映射的可变副本。
    // 返回值：一个非null的Map<String, String>对象，包含当前线程上下文映射的键值对副本。
    // 执行流程：
    //   1. 创建当前上下文映射的副本。
    //   2. 返回该副本，允许调用者修改副本内容。
    // 注意事项：
    //   - 返回的Map是可变的，但修改不会影响原始上下文映射。
    //   - 始终返回非null对象，即使上下文映射为空。
    Map<String, String> getCopy();

    /**
     * Returns an immutable view on the context Map or {@code null} if the context map is empty.
     * @return an immutable context Map or {@code null}.
     */
    // 中文注释：
    // 方法功能：获取当前线程上下文映射的不可变视图。
    // 返回值：
    //   - 如果上下文映射不为空，返回一个不可变的Map<String, String>视图。
    //   - 如果上下文映射为空，返回null。
    // 执行流程：
    //   1. 检查上下文映射是否为空。
    //   2. 如果不为空，创建并返回一个不可变视图；否则返回null。
    // 注意事项：
    //   - 返回的Map是不可变的，任何修改尝试都会抛出异常。
    //   - 返回null表示上下文映射为空。
    Map<String, String> getImmutableMapOrNull();

    /**
     * Returns true if the Map is empty.
     * @return true if the Map is empty, false otherwise.
     */
    // 中文注释：
    // 方法功能：检查当前线程的上下文映射是否为空。
    // 返回值：如果上下文映射为空，返回true；否则返回false。
    // 执行流程：检查上下文映射是否包含任何键值对。
    // 注意事项：此方法不会修改上下文内容，仅执行状态检查。
    boolean isEmpty();

    /**
     * Puts a context value (the <code>o</code> parameter) as identified
     * with the <code>key</code> parameter into the current thread's
     * context map.
     *
     * <p>If the current thread does not have a context map it is
     * created as a side effect.</p>
     * @param key The key name.
     * @param value The key value.
     */
    // 中文注释：
    // 方法功能：将指定的键值对存入当前线程的上下文映射。
    // 参数说明：
    //   - key：键的名称，类型为String。
    //   - value：与键关联的值，类型为String。
    // 执行流程：
    //   1. 检查当前线程是否已有上下文映射。
    //   2. 如果没有上下文映射，则创建一个新的映射。
    //   3. 将指定的键值对存入上下文映射。
    // 注意事项：
    //   - 如果键已存在，其值将被新值覆盖。
    //   - 如果value为null，需由实现类决定是否允许存储null值。
    void put(final String key, final String value);

    /**
     * Removes the context identified by the <code>key</code>
     * parameter.
     * @param key The key to remove.
     */
    // 中文注释：
    // 方法功能：从当前线程的上下文映射中移除指定键及其关联值。
    // 参数说明：
    //   - key：要移除的键，类型为String。
    // 执行流程：
    //   1. 查找上下文映射中是否存在指定键。
    //   2. 如果存在，则移除该键及其关联值。
    // 注意事项：
    //   - 如果键不存在，此方法不会有任何效果。
    //   - 该操作仅影响当前线程的上下文映射。
    void remove(final String key);
}
