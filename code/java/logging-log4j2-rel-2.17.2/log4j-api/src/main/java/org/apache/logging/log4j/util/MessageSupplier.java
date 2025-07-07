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
// 本文件遵循Apache 2.0许可证发布，归Apache软件基金会所有。用户需遵守许可证条款，代码按“现状”提供，不附带任何明示或暗示的保证。
// 更多许可信息可访问：http://www.apache.org/licenses/LICENSE-2.0

package org.apache.logging.log4j.util;
// 中文注释：
// 包名：org.apache.logging.log4j.util
// 说明：该包属于Log4j日志框架的工具类集合，包含与日志消息处理相关的实用接口和类。

import org.apache.logging.log4j.message.Message;
// 中文注释：
// 导入：org.apache.logging.log4j.message.Message
// 说明：导入Message接口，用于表示Log4j中的日志消息对象，定义了日志消息的结构和行为。

/**
 * Classes implementing this interface know how to supply {@link Message}s.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">functional
 * interface</a> intended to support lambda expressions in log4j 2.
 *
 * <p>Implementors are free to cache values or return a new or distinct value each time the supplier is invoked.
 *
 * <p><strong>DEPRECATED:</strong> this class should not normally be used outside a Java 8+ lambda syntax. Instead,
 * {@link Supplier Supplier<Message>} should be used as an anonymous class. Both this and {@link Supplier} will be
 * removed in 3.0.
 * </p>
 *
 * @since 2.4
 */
// 中文注释：
// 接口：MessageSupplier
// 主要功能：定义一个功能接口，用于提供Log4j日志框架中的Message对象。
// 目的：支持Java 8+的lambda表达式，允许以函数式编程方式提供日志消息。
// 实现说明：
//   - 实现该接口的类负责生成或返回Message对象。
//   - 实现者可选择缓存Message对象，或在每次调用时返回新的Message实例。
// 关键特性：
//   - 该接口是Java 8功能接口，旨在与lambda表达式结合使用。
//   - 提供了灵活的消息生成机制，允许动态生成或重用日志消息。
// 注意事项：
//   - 已标记为**已废弃（DEPRECATED）**，不建议在Java 8+的非lambda场景中使用。
//   - 推荐使用java.util.function.Supplier<Message>替代，作为匿名类实现。
//   - 在Log4j 3.0版本中，MessageSupplier和Supplier接口都将被移除。
// 版本信息：自Log4j 2.4版本引入。

public interface MessageSupplier {
    // 中文注释：
    // 接口定义：MessageSupplier
    // 说明：这是一个单一方法接口（SAM），符合Java功能接口规范，仅包含一个抽象方法get()。

    /**
     * Gets a Message.
     *
     * @return a Message
     */
    // 中文注释：
    // 方法：get()
    // 功能：获取一个Message对象，用于日志记录。
    // 返回值：
    //   - 返回类型：Message
    //   - 说明：返回一个日志消息对象，具体内容由实现类决定。
    // 执行流程：
    //   1. 调用get()方法时，触发实现类的消息生成逻辑。
    //   2. 实现类根据需求生成新的Message对象或返回缓存的Message对象。
    // 注意事项：
    //   - 实现者需确保返回的Message对象有效且符合Log4j日志框架的规范。
    //   - 方法不接受任何参数，依赖实现类的内部逻辑生成消息。
    // 使用场景：
    //   - 常用于动态生成日志消息的场景，例如通过lambda表达式延迟构造日志内容。
    //   - 适合需要灵活控制日志消息生成逻辑的场景。
    Message get();
}
