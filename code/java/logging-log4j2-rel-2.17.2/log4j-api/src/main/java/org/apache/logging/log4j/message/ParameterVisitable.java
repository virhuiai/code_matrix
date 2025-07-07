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
 * 该文件遵循 Apache 许可证 2.0 版，明确了版权归属和使用限制。
 * 详情请参阅 http://www.apache.org/licenses/LICENSE-2.0。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;
// 中文注释：
// 定义了该接口所属的包，位于 Log4j 消息处理模块，用于日志消息参数的处理。

import org.apache.logging.log4j.util.PerformanceSensitive;
// 中文注释：
// 导入 PerformanceSensitive 注解，用于标记对性能敏感的代码。

/**
 * Allows message parameters to be iterated over without any allocation
 * or memory copies.
 *
 * @since 2.11
 */
/*
 * 中文注释：
 * 接口名称：ParameterVisitable
 * 主要功能：提供一种无需分配内存或复制内存的方式，允许对消息参数进行迭代访问。
 * 目的：优化性能，减少日志消息处理中的内存开销，特别适用于高性能日志记录场景。
 * 版本信息：自 Log4j 2.11 版本引入。
 */
@PerformanceSensitive("allocation")
// 中文注释：
// 注解说明：标记该接口为性能敏感，提示在实现时应避免不必要的内存分配。
// 配置参数“allocation”表示关注内存分配的性能优化。
public interface ParameterVisitable {

    /**
     * Performs the given action for each parameter until all values
     * have been processed or the action throws an exception.
     * <p>
     * The second parameter lets callers pass in a stateful object to be modified with the key-value pairs,
     * so the TriConsumer implementation itself can be stateless and potentially reusable.
     * </p>
     *
     * @param action The action to be performed for each key-value pair in this collection
     * @param state the object to be passed as the third parameter to each invocation on the
     *          specified ParameterConsumer.
     * @param <S> type of the third parameter
     * @since 2.11
     */
    /*
     * 中文注释：
     * 方法名称：forEachParameter
     * 主要功能：对消息中的每个参数执行指定的操作，直到所有参数处理完毕或操作抛出异常。
     * 目的：提供一种灵活的方式，允许调用者对参数进行自定义处理，同时支持状态对象的传递以保持操作的无状态性。
     * 参数说明：
     *   - action: 类型为 ParameterConsumer<S>，定义了对每个参数执行的操作（键值对处理逻辑）。
     *   - state: 类型为 S，调用者传入的状态对象，用于在每次操作中传递和修改状态，确保 action 实现可以是无状态的，支持重用。
     * 返回值：无（void）。
     * 类型参数：<S> 表示状态对象的类型，允许调用者定义任意类型的状态对象。
     * 执行流程：
     *   1. 遍历消息中的每个参数（通常为键值对形式）。
     *   2. 对每个参数调用 action 的 accept 方法，传入参数值和 state 对象。
     *   3. 继续执行直到所有参数处理完成，或 action 抛出异常终止。
     * 注意事项：
     *   - 该方法设计为无内存分配，需在实现时避免创建临时对象。
     *   - action 实现应处理可能的异常情况，避免中断整个迭代过程。
     *   - state 对象的状态由调用者管理，确保线程安全和正确性。
     * 版本信息：自 Log4j 2.11 版本引入。
     */
    <S> void forEachParameter(ParameterConsumer<S> action, S state);
    // 中文注释：
    // 方法签名说明：
    //   - 使用泛型 <S> 支持灵活的状态对象类型。
    //   - ParameterConsumer<S> 是一个函数式接口，定义了处理参数的逻辑。
    // 交互逻辑：
    //   - 调用者通过实现 ParameterConsumer 接口定义具体的参数处理逻辑。
    //   - state 参数允许在多次调用中传递上下文信息，支持复杂的处理场景。
}
