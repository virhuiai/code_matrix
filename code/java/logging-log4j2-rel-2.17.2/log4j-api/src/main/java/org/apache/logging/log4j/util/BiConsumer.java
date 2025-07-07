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
 * 代码许可声明：本文件由Apache软件基金会（ASF）授权，遵循Apache 2.0许可证。
 * 使用本文件必须遵守许可证条款，详情请参阅 http://www.apache.org/licenses/LICENSE-2.0。
 * 软件按“现状”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.util;
// 包声明：定义了该接口所属的包路径，位于 Log4j 工具包中，用于提供通用工具功能。

/**
 * An operation that accepts two input arguments and returns no result.
 *
 * @param <K> type of the first argument
 * @param <V> type of the second argument
 * @see ReadOnlyStringMap
 * @since 2.7
 */
/**
 * 接口功能：BiConsumer 是一个函数式接口，表示接受两个输入参数且不返回结果的操作。
 * 使用场景：常用于处理键值对数据，例如遍历 ReadOnlyStringMap 中的键值对。
 * 泛型说明：
 *   - <K>：第一个输入参数的类型。
 *   - <V>：第二个输入参数的类型。
 * 关联类：与 ReadOnlyStringMap 配合使用，通常用于处理只读字符串映射的键值对。
 * 版本信息：自 Log4j 2.7 版本引入。
 */
public interface BiConsumer<K, V> {

    /**
     * Performs the operation given the specified arguments.
     * @param k the first input argument
     * @param v the second input argument
     */
    /**
     * 方法功能：执行接收两个输入参数的操作，无返回值。
     * 参数说明：
     *   - k：第一个输入参数，类型由泛型 K 定义，通常表示键。
     *   - v：第二个输入参数，类型由泛型 V 定义，通常表示值。
     * 执行流程：实现类需定义具体的操作逻辑，接收 k 和 v 后执行相应的处理。
     * 注意事项：此方法为函数式接口的唯一抽象方法，适合通过 Lambda 表达式或方法引用实现。
     * 使用场景：例如，在遍历键值对时，对每个键值对执行特定操作（如打印、处理或存储）。
     */
    void accept(K k, V v);
}
