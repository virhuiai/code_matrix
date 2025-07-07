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
// 本代码文件遵循 Apache 2.0 许可证发布，明确了版权归属和使用限制。
// 详细的许可证内容可参考 http://www.apache.org/licenses/LICENSE-2.0。
// 软件以“现状”提供，不包含任何明示或暗示的担保。

package org.apache.logging.log4j.message;
// 中文注释：
// 包路径：org.apache.logging.log4j.message
// 该包主要用于定义 Log4j 的消息处理相关接口和实现类，负责日志消息的创建与格式化。

/**
 * Creates messages. Implementations can provide different message format syntaxes.
 *
 * @see ParameterizedMessageFactory
 * @see StringFormatterMessageFactory
 */
// 中文注释：
// 接口：MessageFactory
// 主要功能：定义创建日志消息的接口，允许实现类提供不同格式的日志消息生成方式。
// 目的：为 Log4j 日志系统提供统一的日志消息创建入口，支持灵活的消息格式化。
// 关联实现类：
// - ParameterizedMessageFactory：支持参数化消息的工厂类。
// - StringFormatterMessageFactory：支持基于字符串格式化的消息工厂类。

public interface MessageFactory {

    /**
     * Creates a new message based on an Object.
     *
     * @param message
     *            a message object
     * @return a new message
     */
    // 中文注释：
    // 方法：newMessage(Object message)
    // 功能：根据传入的 Object 对象创建新的日志消息。
    // 参数：
    //   - message：任意类型的对象，用于生成日志消息的内容。
    // 返回值：
    //   - Message：返回一个新的 Message 对象，封装了日志消息的内容。
    // 执行流程：
    //   1. 接收一个 Object 类型的参数。
    //   2. 根据实现类的逻辑，将该对象转换为日志消息。
    //   3. 返回封装好的 Message 对象。
    // 注意事项：
    //   - 具体消息格式取决于实现类，可能直接使用对象的 toString() 方法或其他自定义格式化逻辑。
    Message newMessage(Object message);

    /**
     * Creates a new message based on a String.
     *
     * @param message
     *            a message String
     * @return a new message
     */
    // 中文注释：
    // 方法：newMessage(String message)
    // 功能：根据传入的字符串创建新的日志消息。
    // 参数：
    //   - message：字符串，表示日志消息的原始内容。
    // 返回值：
    //   - Message：返回一个新的 Message 对象，封装了字符串格式的日志消息。
    // 执行流程：
    //   1. 接收一个字符串参数。
    //   2. 根据实现类的逻辑，将字符串直接作为日志消息内容或进行特定格式化。
    //   3. 返回封装好的 Message 对象。
    // 注意事项：
    //   - 该方法适用于简单的字符串日志消息，适合无参数的日志场景。
    Message newMessage(String message);

    /**
     * Creates a new parameterized message.
     *
     * @param message
     *            a message template, the kind of message template depends on the implementation.
     * @param params
     *            the message parameters
     * @return a new message
     * @see ParameterizedMessageFactory
     * @see StringFormatterMessageFactory
     */
    // 中文注释：
    // 方法：newMessage(String message, Object... params)
    // 功能：根据消息模板和参数创建参数化的日志消息。
    // 参数：
    //   - message：字符串类型的消息模板，具体格式取决于实现类（例如，占位符 {} 或 %s）。
    //   - params：可变参数列表，提供用于替换消息模板占位符的实际值。
    // 返回值：
    //   - Message：返回一个新的 Message 对象，包含格式化后的日志消息。
    // 执行流程：
    //   1. 接收消息模板和参数数组。
    //   2. 根据实现类的格式化规则，将参数填充到消息模板的占位符中。
    //   3. 生成并返回格式化后的 Message 对象。
    // 特殊处理逻辑：
    //   - 不同实现类支持的占位符语法可能不同（例如，ParameterizedMessageFactory 使用 {}，StringFormatterMessageFactory 使用 %s）。
    //   - 参数数量需与模板中的占位符匹配，否则可能导致格式化错误。
    // 注意事项：
    //   - 该方法适用于需要动态插入参数的复杂日志消息场景。
    //   - 实现类需确保参数的安全处理（如空值检查）以避免运行时异常。
    Message newMessage(String message, Object... params);
}
