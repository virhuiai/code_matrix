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
package org.apache.logging.log4j.message;

/**
 * A Message that is a collection of Messages.
 * @param <T> The Message type.
 *
 * // 中文注释：
 * // 类功能和目的：
 * // MessageCollectionMessage 是一个接口，定义了一个消息集合，允许存储和操作多个 Message 类型的对象。
 * // 它继承了 Message 接口，并实现了 Iterable 接口，以便能够迭代访问集合中的消息。
 * //
 * // 参数说明：
 * // - <T>：泛型参数，表示集合中存储的 Message 对象的具体类型。
 * //
 * // 关键实现细节：
 * // - 该接口没有定义具体的方法，依赖于继承的 Message 和 Iterable 接口。
 * // - Message 接口通常用于 Log4j 日志框架，表示一条日志消息。
 * // - Iterable<T> 接口使该集合支持 for-each 循环，方便遍历内部的 Message 对象。
 * //
 * // 使用场景：
 * // - 适用于需要批量处理多条日志消息的场景，例如将多条消息组合为一条日志输出。
 * // - 可用于日志框架中需要聚合多个子消息的复杂日志场景。
 * //
 * // 注意事项：
 * // - 实现类需要提供具体的消息集合存储机制（如 List 或 Set）和迭代逻辑。
 * // - 确保实现类遵守 Message 接口的契约，提供格式化消息的功能。
 * // - 由于是接口，具体实现需要处理线程安全、性能优化等问题。
 */
public interface MessageCollectionMessage<T> extends Message, Iterable<T> {


}
