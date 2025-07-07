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
 * 本文件遵循 Apache 许可证 2.0 版，详细的许可信息可在上述链接查看。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.spi;

import java.util.Objects;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.SimpleMessage;

/**
 * Adapts a legacy MessageFactory to the new MessageFactory2 interface.
 *
 * @since 2.6
 */
/*
 * 中文注释：
 * 类名：MessageFactory2Adapter
 * 主要功能：将旧的 MessageFactory 接口适配到新的 MessageFactory2 接口。
 * 目的：提供向后兼容性，确保旧的 MessageFactory 实现能够支持 MessageFactory2 的新功能。
 * 使用场景：用于 Log4j 日志框架中，处理不同类型的日志消息。
 * 自 2.6 版本引入。
 */
public class MessageFactory2Adapter implements MessageFactory2 {
    private final MessageFactory wrapped;

    /*
     * 中文注释：
     * 成员变量：wrapped
     * 用途：存储被适配的原始 MessageFactory 实例，用于代理消息创建请求。
     * 注意事项：该变量通过构造函数初始化，且不可为 null。
     */

    /**
     * 构造函数，初始化适配器。
     * @param wrapped 要适配的 MessageFactory 实例。
     */
    /*
     * 中文注释：
     * 方法名：MessageFactory2Adapter
     * 功能：构造一个 MessageFactory2Adapter 实例，用于适配旧的 MessageFactory。
     * 参数：
     *   - wrapped：旧的 MessageFactory 实例，需非空。
     * 执行流程：
     *   1. 使用 Objects.requireNonNull 检查 wrapped 参数是否为 null，若为 null 则抛出异常。
     *   2. 将 wrapped 参数赋值给成员变量 wrapped。
     * 注意事项：wrapped 参数不能为空，否则会抛出 NullPointerException。
     */
    public MessageFactory2Adapter(final MessageFactory wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    /**
     * 返回原始的 MessageFactory 实例。
     * @return 原始 MessageFactory。
     */
    /*
     * 中文注释：
     * 方法名：getOriginal
     * 功能：获取被适配的原始 MessageFactory 实例。
     * 返回值：MessageFactory，原始的 MessageFactory 实例。
     * 执行流程：直接返回成员变量 wrapped。
     * 使用场景：用于需要直接访问原始 MessageFactory 的场景。
     */
    public MessageFactory getOriginal() {
        return wrapped;
    }

    /**
     * 创建基于字符序列的日志消息。
     * @param charSequence 字符序列。
     * @return 新的 SimpleMessage 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据给定的字符序列创建一个 SimpleMessage 类型的日志消息。
     * 参数：
     *   - charSequence：字符序列，用于构造日志消息内容。
     * 返回值：Message，SimpleMessage 类型的日志消息对象。
     * 执行流程：直接调用 SimpleMessage 构造函数，传入 charSequence 参数，创建并返回消息对象。
     * 注意事项：此方法为 MessageFactory2 的新功能，专门处理字符序列输入。
     */
    @Override
    public Message newMessage(final CharSequence charSequence) {
        return new SimpleMessage(charSequence);
    }

    /**
     * 创建带有一个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和单个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0：第一个参数，用于替换消息模板中的占位符。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和 p0 参数，生成并返回消息对象。
     * 注意事项：依赖原始 MessageFactory 的实现，参数 p0 需与模板匹配。
     */
    @Override
    public Message newMessage(final String message, final Object p0) {
        return wrapped.newMessage(message, p0);
    }

    /**
     * 创建带有两个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和两个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0：第一个参数，用于替换消息模板中的第一个占位符。
     *   - p1：第二个参数，用于替换消息模板中的第二个占位符。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1，生成并返回消息对象。
     * 注意事项：参数数量需与消息模板中的占位符一致。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return wrapped.newMessage(message, p0, p1);
    }

    /**
     * 创建带有三个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和三个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2：分别对应消息模板中的三个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2，生成并返回消息对象。
     * 注意事项：确保参数数量和类型与消息模板中的占位符匹配。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return wrapped.newMessage(message, p0, p1, p2);
    }

    /**
     * 创建带有四个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @param p3 第四个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和四个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2, p3：分别对应消息模板中的四个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2、p3，生成并返回消息对象。
     * 注意事项：需确保参数数量和类型与消息模板中的占位符一致。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        return wrapped.newMessage(message, p0, p1, p2, p3);
    }

    /**
     * 创建带有五个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @param p3 第四个参数。
     * @param p4 第五个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和五个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2, p3, p4：分别对应消息模板中的五个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2、p3、p4，生成并返回消息对象。
     * 注意事项：参数数量需与消息模板中的占位符一致。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        return wrapped.newMessage(message, p0, p1, p2, p3, p4);
    }

    /**
     * 创建带有六个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @param p3 第四个参数。
     * @param p4 第五个参数。
     * @param p5 第六个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和六个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2, p3, p4, p5：分别对应消息模板中的六个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2、p3、p4、p5，生成并返回消息对象。
     * 注意事项：需确保参数数量和类型与消息模板中的占位符匹配。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        return wrapped.newMessage(message, p0, p1, p2, p3, p4, p5);
    }

    /**
     * 创建带有七个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @param p3 第四个参数。
     * @param p4 第五个参数。
     * @param p5 第六个参数。
     * @param p6 第七个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和七个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2, p3, p4, p5, p6：分别对应消息模板中的七个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2、p3、p4、p5、p6，生成并返回消息对象。
     * 注意事项：参数数量需与消息模板中的占位符一致。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        return wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    /**
     * 创建带有八个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @param p3 第四个参数。
     * @param p4 第五个参数。
     * @param p5 第六个参数。
     * @param p6 第七个参数。
     * @param p7 第八个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和八个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2, p3, p4, p5, p6, p7：分别对应消息模板中的八个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2、p3、p4、p5、p6、p7，生成并返回消息对象。
     * 注意事项：需确保参数数量和类型与消息模板中的占位符匹配。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        return wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    /**
     * 创建带有九个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @param p3 第四个参数。
     * @param p4 第五个参数。
     * @param p5 第六个参数。
     * @param p6 第七个参数。
     * @param p7 第八个参数。
     * @param p8 第九个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和九个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2, p3, p4, p5, p6, p7, p8：分别对应消息模板中的九个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2、p3、p4、p5、p6、p7、p8，生成并返回消息对象。
     * 注意事项：需确保参数数量和类型与消息模板中的占位符匹配。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * 创建带有十个参数的格式化日志消息。
     * @param message 消息模板。
     * @param p0 第一个参数。
     * @param p1 第二个参数。
     * @param p2 第三个参数。
     * @param p3 第四个参数。
     * @param p4 第五个参数。
     * @param p5 第六个参数。
     * @param p6 第七个参数。
     * @param p7 第八个参数。
     * @param p8 第九个参数。
     * @param p9 第十个参数。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和十个参数创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9：分别对应消息模板中的十个占位符的参数。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和参数 p0、p1、p2、p3、p4、p5、p6、p7、p8、p9，生成并返回消息对象。
     * 注意事项：需确保参数数量和类型与消息模板中的占位符匹配。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return wrapped.newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    /**
     * 创建基于对象类型的日志消息。
     * @param message 消息对象。
     * @return Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据给定的对象创建日志消息。
     * 参数：
     *   - message：任意类型的对象，用于构造日志消息内容。
     * 返回值：Message，基于对象内容生成的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 参数，生成并返回消息对象。
     * 注意事项：依赖原始 MessageFactory 的实现，message 参数可以是任意类型。
     */
    @Override
    public Message newMessage(final Object message) {
        return wrapped.newMessage(message);
    }

    /**
     * 创建基于字符串的日志消息。
     * @param message 字符串消息。
     * @return Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据给定的字符串创建日志消息。
     * 参数：
     *   - message：字符串类型的消息内容。
     * 返回值：Message，基于字符串内容生成的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 参数，生成并返回消息对象。
     * 注意事项：此方法处理不含占位符的简单字符串消息。
     */
    @Override
    public Message newMessage(final String message) {
        return wrapped.newMessage(message);
    }

    /**
     * 创建带有可变参数的格式化日志消息。
     * @param message 消息模板。
     * @param params 参数数组。
     * @return 格式化后的 Message 实例。
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：根据消息模板和可变参数数组创建格式化日志消息。
     * 参数：
     *   - message：字符串类型的消息模板，包含占位符。
     *   - params：可变参数数组，用于替换消息模板中的占位符。
     * 返回值：Message，格式化后的日志消息对象。
     * 执行流程：调用 wrapped 的 newMessage 方法，传入 message 和 params 数组，生成并返回消息对象。
     * 注意事项：
     *   - params 数组的长度需与消息模板中的占位符数量匹配。
     *   - 此方法提供灵活的参数输入方式，适用于动态参数数量的场景。
     */
    @Override
    public Message newMessage(final String message, final Object... params) {
        return wrapped.newMessage(message, params);
    }
}
