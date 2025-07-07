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
// 本文件由 Apache 软件基金会授权，遵循 Apache 2.0 许可证。
// 代码以“按原样”分发的形式提供，不附带任何明示或暗示的担保。
// 有关许可的具体权限和限制，请参阅许可证文件。

package org.apache.logging.log4j.spi;
// 中文注释：
// 定义了 Log4j 的 SPI（服务提供者接口）包，用于实现日志记录相关的扩展功能。

import java.util.Map;

import org.apache.logging.log4j.util.StringMap;
// 中文注释：
// 导入 Java 的 Map 接口，用于处理键值对数据。
// 导入 Log4j 的 StringMap 工具类，用于存储线程上下文的键值对。

/**
 * Extension service provider interface to implement additional custom MDC behavior for
 * {@link org.apache.logging.log4j.ThreadContext}.
 *
 * Consider implementing {@link CleanableThreadContextMap} instead.
 *
 * @see ThreadContextMap
 * @since 2.7
 */
// 中文注释：
// 接口 ThreadContextMap2 是 Log4j 的服务提供者接口（SPI），用于扩展 ThreadContext 的 MDC（Mapped Diagnostic Context）功能。
// 主要功能：提供在当前线程上下文中存储和操作键值对数据的额外行为。
// 建议：优先考虑实现 CleanableThreadContextMap 接口，以获得更灵活的上下文管理功能。
// 版本：自 Log4j 2.7 版本引入。

public interface ThreadContextMap2 extends ThreadContextMap {
    // 中文注释：
    // ThreadContextMap2 接口继承自 ThreadContextMap，扩展了线程上下文的键值对存储功能。
    // 主要目的：为 Log4j 的 ThreadContext 提供更高级的上下文数据管理能力。
    // 使用场景：适用于需要自定义 MDC 行为的日志记录场景，例如在多线程环境中跟踪特定上下文信息。

    /**
     * Puts all given context map entries into the current thread's
     * context map.
     *
     * <p>If the current thread does not have a context map it is
     * created as a side effect.</p>
     * @param map The map.
     * @since 2.7
     */
    // 中文注释：
    // 方法：putAll
    // 功能：将给定的键值对 Map 全部存储到当前线程的上下文映射中。
    // 参数：
    //   - map: 包含字符串键值对的 Map 对象，用于存储到线程上下文。
    // 返回值：无（void）。
    // 执行流程：
    //   1. 接收一个 Map<String, String> 参数，包含需要存储的键值对。
    //   2. 检查当前线程是否已有上下文映射。
    //   3. 如果当前线程没有上下文映射，则自动创建一个新的上下文映射。
    //   4. 将传入的 Map 中的所有键值对添加到当前线程的上下文映射中。
    // 注意事项：
    //   - 如果传入的 map 为 null 或空，具体行为由实现类决定（可能忽略或抛出异常）。
    //   - 该方法会影响当前线程的上下文状态，需谨慎使用以避免覆盖现有数据。
    // 版本：自 Log4j 2.7 版本引入。
    void putAll(final Map<String, String> map);

    /**
     * Returns the context data for reading. Note that regardless of whether the returned context data has been
     * {@linkplain StringMap#freeze() frozen} (made read-only) or not, callers should not attempt to modify
     * the returned data structure.
     *
     * @return the {@code StringMap}
     */
    // 中文注释：
    // 方法：getReadOnlyContextData
    // 功能：获取当前线程的上下文数据（以 StringMap 形式返回），仅用于读取。
    // 返回值：
    //   - StringMap：包含当前线程上下文的键值对数据。
    // 执行流程：
    //   1. 获取当前线程的上下文数据。
    //   2. 返回一个 StringMap 对象，包含所有键值对。
    // 特殊处理逻辑：
    //   - 返回的 StringMap 可能已被冻结（freeze），即设置为只读状态。
    //   - 调用者不得尝试修改返回的 StringMap 数据结构，以避免不可预期的行为。
    // 注意事项：
    //   - 该方法仅用于读取上下文数据，不支持修改操作。
    //   - 如果当前线程没有上下文数据，可能返回一个空的 StringMap（具体行为由实现类决定）。
    // 版本：自 Log4j 2.7 版本引入。
    StringMap getReadOnlyContextData();
}
