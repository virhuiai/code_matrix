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
 * 版权声明：本文件由Apache软件基金会（ASF）授权，遵循Apache 2.0许可证。
 * 使用本文件需遵守许可证条款，详情见上述链接。
 * 本文件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.spi;
// 包声明：定义该接口所属的包，位于Log4j的SPI（服务提供者接口）模块，用于扩展日志功能。

/**
 * Extension service provider interface to implement additional custom MDC behavior for
 * {@link org.apache.logging.log4j.ThreadContext}.
 *
 * @see ThreadContextMap
 * @since 2.8
 */
/**
 * 接口说明：CleanableThreadContextMap 是为 Log4j 的 ThreadContext 提供额外自定义 MDC（Mapped Diagnostic Context，映射诊断上下文）行为的扩展服务提供者接口。
 * 主要功能：扩展 ThreadContextMap2，允许用户实现自定义的上下文映射清理行为。
 * 继承关系：继承自 ThreadContextMap2，提供更灵活的线程上下文管理。
 * 使用场景：用于在多线程环境中管理日志上下文信息，特别是在需要清理特定上下文键的场景。
 * 版本信息：自 Log4j 2.8 版本引入。
 */
public interface CleanableThreadContextMap extends ThreadContextMap2 {
    // 接口定义：声明 CleanableThreadContextMap 接口，继承 ThreadContextMap2，提供清理线程上下文的功能。

    /**
     * Removes all given context map keys from the current thread's context map.
     *
     * <p>If the current thread does not have a context map it is
     * created as a side effect.</p>
     *
     * @param keys The keys.
     * @since 2.8
     */
    /**
     * 方法说明：removeAll 方法用于从当前线程的上下文映射中移除指定的所有键。
     * 功能：清理线程上下文中的指定键值对，确保上下文数据的精简或重置。
     * 参数说明：
     *   - keys: Iterable<String> 类型，表示需要移除的键集合。
     * 返回值：无返回值（void）。
     * 执行流程：
     *   1. 检查当前线程是否已有上下文映射。
     *   2. 如果不存在上下文映射，则创建一个新的上下文映射（副作用）。
     *   3. 遍历传入的键集合，从上下文映射中移除对应的键值对。
     * 注意事项：
     *   - 如果传入的键不存在于上下文映射中，方法不会抛出异常。
     *   - 方法是线程安全的，仅影响当前线程的上下文。
     *   - 实现类需确保高效处理键的批量移除。
     * 版本信息：自 Log4j 2.8 版本引入。
     */
    void removeAll(final Iterable<String> keys);
    // 方法声明：定义 removeAll 方法，接收一个可迭代的字符串键集合，用于批量清理线程上下文中的指定键。
}
