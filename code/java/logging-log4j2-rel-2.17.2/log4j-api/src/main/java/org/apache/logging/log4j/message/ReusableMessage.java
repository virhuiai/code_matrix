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
// 本文件遵循 Apache 2.0 许可证，归 Apache 软件基金会所有，代码按“现状”分发，无任何明示或暗示担保。

package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/**
 * Messages implementing this interface are reused between logging calls.
 * <p>
 * If a Message is reusable, downstream components should not hand over this instance to another thread, but extract its
 * content (via the {@link StringBuilderFormattable#formatTo(StringBuilder)} method) instead.
 * </p>
 * @see ReusableMessageFactory
 * @since 2.6
 */
// 中文注释：
// ReusableMessage 接口定义了可重复使用的消息对象，用于在多次日志调用之间重用，优化性能。
// 主要功能：
// - 允许消息对象在日志调用间重用，减少对象分配开销。
// - 实现 StringBuilderFormattable 接口，支持将消息内容格式化到 StringBuilder。
// 注意事项：
// - 可重用消息不应直接传递给其他线程，而应通过 formatTo 方法提取内容，以避免线程安全问题。
// - 配合 ReusableMessageFactory 使用，用于创建和管理可重用消息。
// - 自 2.6 版本引入，适用于高性能日志场景。
@PerformanceSensitive("allocation")
public interface ReusableMessage extends Message, StringBuilderFormattable {

    /**
     * Returns the parameter array that was used to initialize this reusable message and replaces it with the specified
     * array. The returned parameter array will no longer be modified by this reusable message. The specified array is
     * now "owned" by this reusable message and can be modified if necessary for the next log event.
     * </p><p>
     * ReusableMessages that have no parameters return the specified array.
     * </p><p>
     * This method is used by asynchronous loggers to pass the parameter array to a background thread without
     * allocating new objects.
     * The actual number of parameters in the returned array can be determined with {@link #getParameterCount()}.
     * </p>
     *
     * @param emptyReplacement the parameter array that can be used for subsequent uses of this reusable message.
     *         This replacement array must have at least 10 elements (the number of varargs supported by the Logger
     *         API).
     * @return the parameter array for the current message content. This may be a vararg array of any length, or it may
     *         be a reusable array of 10 elements used to hold the unrolled vararg parameters.
     * @see #getParameterCount()
     */
    // 中文注释：
    // 方法功能：
    // - 返回初始化当前可重用消息的参数数组，并用指定的新数组替换原有参数数组。
    // - 用于异步日志记录器将参数数组传递给后台线程，避免分配新对象。
    // 参数说明：
    // - emptyReplacement: 用于后续消息重用的新参数数组，至少包含 10 个元素（支持 Logger API 的可变参数数量）。
    // 返回值：
    // - 返回当前消息内容使用的参数数组，可能是一个任意长度的可变参数数组，或一个包含 10 个元素的可重用数组。
    // 执行流程：
    // 1. 返回当前消息的参数数组。
    // 2. 将传入的 emptyReplacement 数组设置为消息的新参数数组，用于下一次日志调用。
    // 3. 原参数数组不再被当前消息修改，调用者可通过 getParameterCount() 确定实际参数数量。
    // 注意事项：
    // - 如果消息没有参数，则直接返回传入的 emptyReplacement 数组。
    // - 新数组由消息对象“拥有”，可被修改以准备下一次日志事件。
    // - 该方法专为异步日志场景设计，优化性能，避免对象分配。
    Object[] swapParameters(Object[] emptyReplacement);

    /**
     * Returns the number of parameters that was used to initialize this reusable message for the current content.
     * <p>
     * The parameter array returned by {@link #swapParameters(Object[])} may be larger than the actual number of
     * parameters. Callers should use this method to determine how many elements the array contains.
     * </p>
     * @return the current number of parameters
     */
    // 中文注释：
    // 方法功能：
    // - 返回当前可重用消息初始化时使用的参数数量。
    // 返回值：
    // - 当前消息内容的参数数量（short 类型）。
    // 使用场景：
    // - 由于 swapParameters 返回的数组可能包含额外未使用的元素，调用者需通过此方法确认实际参数数量。
    // 注意事项：
    // - 该方法与 swapParameters 配合使用，确保正确处理参数数组中的有效数据。
    short getParameterCount();

    /**
     * Returns an immutable snapshot of the current internal state of this reusable message. The returned snapshot
     * will not be affected by subsequent modifications of this reusable message.
     *
     * @return an immutable snapshot of this message
     */
    // 中文注释：
    // 方法功能：
    // - 返回当前可重用消息的不可变快照，捕获消息的当前状态。
    // 返回值：
    // - 一个不可变的 Message 对象，表示当前消息内容的快照。
    // 执行流程：
    // - 创建并返回当前消息状态的副本，后续对消息对象的修改不会影响该快照。
    // 使用场景：
    // - 用于需要保存消息状态的场景，例如日志记录或调试，确保状态不会因重用而改变。
    // 注意事项：
    // - 返回的快照是不可变的，适合跨线程传递或长期存储。
    Message memento();
}
