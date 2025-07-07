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
 * 本文件遵循Apache 2.0许可证发布，归Apache软件基金会所有。
 * 具体许可条款请参阅http://www.apache.org/licenses/LICENSE-2.0。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;
// 中文注释：
// 定义了Log4j消息处理相关的接口，位于org.apache.logging.log4j.message包中。

/**
 * An operation that accepts two input arguments and returns no result.
 *
 * <p>
 * The third parameter lets callers pass in a stateful object to be modified with the key-value pairs,
 * so the ParameterConsumer implementation itself can be stateless and potentially reusable.
 * </p>
 *
 * @param <S> state data
 * @see ReusableMessage
 * @since 2.11
 */
/*
 * 中文注释：
 * 接口名称：ParameterConsumer
 * 主要功能：定义一个接受两个输入参数且无返回值的操作接口，用于处理参数并更新状态对象。
 * 目的：为Log4j的消息处理提供一种灵活的方式，允许调用者传递状态对象，保持实现类的无状态性和可重用性。
 * 泛型参数：
 *   - S：表示状态数据类型，允许调用者传递可修改的状态对象，通常用于存储键值对等信息。
 * 关联类：
 *   - ReusableMessage：ParameterConsumer通常与可重用的消息对象一起使用，以支持高效的消息处理。
 * 版本信息：自Log4j 2.11版本引入。
 * 注意事项：
 *   - 实现类应保持无状态，以确保线程安全和可重用性。
 *   - 状态对象由调用者提供，接口实现仅负责处理参数并更新状态。
 */
public interface ParameterConsumer<S> {

    /**
     * Performs an operation given the specified arguments.
     *
     * @param parameter the parameter
     * @param parameterIndex Index of the parameter
     * @param state
     */
    /*
     * 中文注释：
     * 方法名称：accept
     * 主要功能：执行参数处理操作，接受输入参数并更新状态对象。
     * 参数说明：
     *   - parameter (Object)：需要处理的参数对象，可以是任意类型的数据。
     *   - parameterIndex (int)：参数的索引，表示当前参数在参数列表中的位置。
     *   - state (S)：状态对象，调用者提供的可修改对象，用于存储处理结果（如键值对）。
     * 返回值：无返回值（void），处理结果通过修改state对象实现。
     * 执行流程：
     *   1. 接收parameter、parameterIndex和state三个参数。
     *   2. 根据parameter和parameterIndex执行特定逻辑（如解析参数或提取信息）。
     *   3. 将处理结果存储到state对象中，完成操作。
     * 注意事项：
     *   - 实现类需要确保逻辑线程安全，尤其是在多线程环境中处理state对象。
     *   - parameter可能为null，需在实现中进行空值检查。
     *   - parameterIndex可用于区分参数顺序或标识特定参数的用途。
     * 事件处理机制：
     *   - 该方法通常作为回调机制的一部分，由消息处理框架调用，处理参数并更新状态。
     * 特殊处理逻辑：
     *   - 实现类可根据parameterIndex选择不同的处理逻辑，以支持多样化的参数处理需求。
     */
    void accept(Object parameter, int parameterIndex, S state);

}
