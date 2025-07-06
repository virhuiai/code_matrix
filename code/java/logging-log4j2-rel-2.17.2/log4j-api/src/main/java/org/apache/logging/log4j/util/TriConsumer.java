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
// 本文件遵循 Apache 软件基金会 (ASF) 的 Apache 2.0 许可证发布。
// 代码文件受版权保护，需遵守许可证条款，使用前请查看许可证详情。
// 软件按“原样”分发，不提供任何明示或暗示的担保。

package org.apache.logging.log4j.util;

/**
 * An operation that accepts three input arguments and returns no result.
 *
 * @param <K> type of the first argument
 * @param <V> type of the second argument
 * @param <S> type of the third argument
 * @see ReadOnlyStringMap
 * @since 2.7
 */
// 中文注释：
// 定义一个函数式接口 TriConsumer，用于处理三个输入参数的操作，无返回值。
// @param <K> 第一个参数的类型
// @param <V> 第二个参数的类型
// @param <S> 第三个参数的类型
// @see ReadOnlyStringMap 关联的只读字符串映射类
// @since 2.7 自 Log4j 2.7 版本引入
public interface TriConsumer<K, V, S> {

    /**
     * Performs the operation given the specified arguments.
     * @param k the first input argument
     * @param v the second input argument
     * @param s the third input argument
     */
    // 中文注释：
    // accept 方法执行接收三个输入参数的操作。
    // @param k 第一个输入参数
    // @param v 第二个输入参数
    // @param s 第三个输入参数
    // 方法目的：提供一个函数式接口方法，用于实现接收三个参数的逻辑处理，无返回值。
    // 注意事项：实现此接口的类需定义具体的参数处理逻辑，方法本身不涉及返回值。
    void accept(K k, V v, S s);
}
