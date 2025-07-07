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
// 本文件遵循Apache 2.0许可证，详细的版权信息和许可条款请参见上述链接。
// 文件在“按原样”基础上分发，不提供任何明示或暗示的担保。

package org.apache.logging.log4j.message;

/**
 * Creates {@link FormattedMessage} instances for {@link MessageFactory2} methods (and {@link MessageFactory} by
 * extension.)
 * 
 * <h4>Note to implementors</h4>
 * <p>
 * This class implements all {@link MessageFactory2} methods.
 * </p>
 */
// 中文注释：
// 类名：MessageFormatMessageFactory
// 主要功能：用于为 MessageFactory2 和其扩展接口 MessageFactory 创建 FormattedMessage 实例。
// 实现说明：该类实现了 MessageFactory2 接口的所有方法，提供格式化消息的工厂功能。
// 注意事项：此类是 Log4j 消息处理机制的核心部分，用于生成格式化的日志消息。

public class MessageFormatMessageFactory extends AbstractMessageFactory {
    private static final long serialVersionUID = 3584821740584192453L;
    // 中文注释：
    // 变量名：serialVersionUID
    // 用途：定义序列化版本号，确保类在序列化和反序列化过程中的兼容性。
    // 值：3584821740584192453L，固定值，用于版本控制。

    /**
     * Constructs a message factory with default flow strings.
     */
    // 中文注释：
    // 方法名：MessageFormatMessageFactory
    // 功能：默认构造函数，初始化消息工厂。
    // 执行流程：无参数构造方法，创建 MessageFormatMessageFactory 实例，使用默认配置。
    // 注意事项：不涉及任何参数或特殊配置，适用于标准场景下的消息格式化需求。
    public MessageFormatMessageFactory() {
    }

    /**
     * Creates {@link org.apache.logging.log4j.message.StringFormattedMessage} instances.
     * @param message The message pattern.
     * @param params Parameters to the message.
     * @return The Message.
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(String, Object...)
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：根据消息模式和可变参数创建 StringFormattedMessage 实例。
    // 参数：
    //   - message：String 类型，消息模式（模板），用于格式化最终的日志消息。
    //   - params：Object... 类型，可变参数列表，包含需要插入到消息模式中的参数。
    // 返回值：Message 类型，格式化后的消息对象（StringFormattedMessage 实例）。
    // 执行流程：
    //   1. 接收消息模式和参数列表。
    //   2. 创建并返回 MessageFormatMessage 实例，将消息模式和参数传递给构造函数。
    // 注意事项：此方法支持可变数量的参数，适合灵活的日志消息格式化。
    @Override
    public Message newMessage(final String message, final Object... params) {
        return new MessageFormatMessage(message, params);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为单个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0：Object 类型，单个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和单个参数。
    // 注意事项：自 2.6.1 版本引入，优化单参数场景的调用效率。
    @Override
    public Message newMessage(final String message, final Object p0) {
        return new MessageFormatMessage(message, p0);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为两个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1：Object 类型，两个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和两个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定两个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return new MessageFormatMessage(message, p0, p1);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为三个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2：Object 类型，三个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和三个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定三个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return new MessageFormatMessage(message, p0, p1, p2);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为四个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2, p3：Object 类型，四个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和四个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定四个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return new MessageFormatMessage(message, p0, p1, p2, p3);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为五个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2, p3, p4：Object 类型，五个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和五个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定五个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为六个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2, p3, p4, p5：Object 类型，六个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和六个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定六个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为七个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2, p3, p4, p5, p6：Object 类型，七个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和七个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定七个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为八个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2, p3, p4, p5, p6, p7：Object 类型，八个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和八个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定八个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为九个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2, p3, p4, p5, p6, p7, p8：Object 类型，九个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和九个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定九个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * @since 2.6.1
     */
    // 中文注释：
    // 方法名：newMessage
    // 功能：为十个参数创建格式化消息。
    // 参数：
    //   - message：String 类型，消息模式。
    //   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9：Object 类型，十个参数，插入到消息模式中。
    // 返回值：Message 类型，格式化后的消息对象。
    // 执行流程：调用 MessageFormatMessage 构造函数，传入消息模式和十个参数。
    // 注意事项：自 2.6.1 版本引入，针对固定十个参数的场景优化。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        return new MessageFormatMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
}
