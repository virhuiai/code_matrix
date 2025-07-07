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
 * Creates messages. Implementations can provide different message format syntaxes.
 *
 * @see ParameterizedMessageFactory
 * @since 2.6
 */
 // 中文注释：
 // 该接口的主要功能是定义创建日志消息的工厂方法，允许实现类提供不同的消息格式语法。
 // - 目的：为 Log4j 日志框架提供灵活的消息创建机制，支持多种消息格式。
 // - 实现类可以通过继承此接口实现自定义的消息格式化逻辑。
 // - 关联：通常与 ParameterizedMessageFactory 配合使用，以支持参数化消息的创建。
 // - 自 2.6 版本起引入，扩展了 MessageFactory 的功能。
public interface MessageFactory2 extends MessageFactory {
    
    /**
     * Creates a new message for the specified CharSequence.
     * @param charSequence the (potentially mutable) CharSequence
     * @return a new message for the specified CharSequence
     */
     // 中文注释：
     // 方法功能：根据指定的 CharSequence 创建一个新的消息对象。
     // - 参数：
     //   - charSequence: 输入的字符序列，可能是可变的，用于构造消息内容。
     // - 返回值：返回一个新的 Message 对象，封装了输入的字符序列。
     // - 执行流程：实现类将 charSequence 转换为特定格式的日志消息。
     // - 注意事项：需要考虑 charSequence 的可变性，确保消息内容的稳定性。
    Message newMessage(CharSequence charSequence);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和单个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定（如包含占位符的字符串）。
     //   - p0: 第一个参数，用于替换模板中的占位符。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，将 p0 替换到模板中的相应位置，生成最终消息。
     // - 注意事项：模板格式需与实现类（如 ParameterizedMessageFactory）兼容。
    Message newMessage(String message, Object p0);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和两个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0: 第一个参数，用于替换模板中的第一个占位符。
     //   - p1: 第二个参数，用于替换模板中的第二个占位符。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0 和 p1 替换到模板中的相应位置。
     // - 注意事项：模板需包含足够的占位符以匹配参数数量，否则可能抛出异常。
    Message newMessage(String message, Object p0, Object p1);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和三个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2: 分别对应模板中的第一个、第二个、第三个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2 替换到模板中的相应位置。
     // - 注意事项：确保模板中的占位符数量与参数数量一致。
    Message newMessage(String message, Object p0, Object p1, Object p2);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @param p3 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和四个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2, p3: 分别对应模板中的四个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2、p3 替换到模板中的相应位置。
     // - 注意事项：模板需支持至少四个占位符，否则可能导致格式化错误。
    Message newMessage(String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @param p3 a message parameter
     * @param p4 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和五个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2, p3, p4: 分别对应模板中的五个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2、p3、p4 替换到模板中的相应位置。
     // - 注意事项：模板需支持至少五个占位符。
    Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @param p3 a message parameter
     * @param p4 a message parameter
     * @param p5 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和六个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2, p3, p4, p5: 分别对应模板中的六个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2、p3、p4、p5 替换到模板中的相应位置。
     // - 注意事项：模板需支持至少六个占位符。
    Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @param p3 a message parameter
     * @param p4 a message parameter
     * @param p5 a message parameter
     * @param p6 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和七个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2, p3, p4, p5, p6: 分别对应模板中的七个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2、p3、p4、p5、p6 替换到模板中的相应位置。
     // - 注意事项：模板需支持至少七个占位符。
    Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @param p3 a message parameter
     * @param p4 a message parameter
     * @param p5 a message parameter
     * @param p6 a message parameter
     * @param p7 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和八个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2, p3, p4, p5, p6, p7: 分别对应模板中的八个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2、p3、p4、p5、p6、p7 替换到模板中的相应位置。
     // - 注意事项：模板需支持至少八个占位符。
    Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @param p3 a message parameter
     * @param p4 a message parameter
     * @param p5 a message parameter
     * @param p6 a message parameter
     * @param p7 a message parameter
     * @param p8 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和九个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2, p3, p4, p5, p6, p7, p8: 分别对应模板中的九个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2、p3、p4、p5、p6、p7、p8 替换到模板中的相应位置。
     // - 注意事项：模板需支持至少九个占位符。
    Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    /**
     * Creates a new parameterized message.
     *
     * @param message a message template, the kind of message template depends on the implementation.
     * @param p0 a message parameter
     * @param p1 a message parameter
     * @param p2 a message parameter
     * @param p3 a message parameter
     * @param p4 a message parameter
     * @param p5 a message parameter
     * @param p6 a message parameter
     * @param p7 a message parameter
     * @param p8 a message parameter
     * @param p9 a message parameter
     * @return a new message
     * @see ParameterizedMessageFactory
     */
     // 中文注释：
     // 方法功能：根据消息模板和十个参数创建参数化消息。
     // - 参数：
     //   - message: 消息模板，具体格式由实现类决定。
     //   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9: 分别对应模板中的十个占位符的参数。
     // - 返回值：返回一个新的 Message 对象，包含格式化后的消息内容。
     // - 执行流程：实现类解析模板，依次将 p0、p1、p2、p3、p4、p5、p6、p7、p8、p9 替换到模板中的相应位置。
     // - 注意事项：模板需支持至少十个占位符，确保参数数量与模板占位符匹配。
    Message newMessage(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);
}
