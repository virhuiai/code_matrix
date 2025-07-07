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
 * 版权声明：本文件由Apache软件基金会（ASF）根据一个或多个贡献者许可协议授权。
 * 请参阅随本文件分发的NOTICE文件以获取有关版权所有权的更多信息。
 * ASF根据Apache 2.0许可协议将此文件授权给您；除非符合许可协议，否则不得使用此文件。
 * 您可以在以下地址获取许可协议的副本：http://www.apache.org/licenses/LICENSE-2.0
 * 除非适用法律要求或书面同意，根据许可协议分发的软件按“原样”分发，不提供任何明示或暗示的担保或条件。
 * 请参阅许可协议以了解具体的权限和限制。
 */

package org.apache.logging.log4j.message;

/**
 * Creates {@link FormattedMessage} instances for {@link MessageFactory2} methods (and {@link MessageFactory} by
 * extension.)
 * <p>
 * Enables the use of {@link java.util.Formatter} strings in message strings.
 * </p>
 * <p>
 * Creates {@link StringFormattedMessage} instances for {@link #newMessage(String, Object...)}.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * <h4>Note to implementors</h4>
 * <p>
 * This class implements all {@link MessageFactory2} methods.
 * </p>
 */
/**
 * 类功能：StringFormatterMessageFactory 用于为 MessageFactory2（及其父类 MessageFactory）创建 FormattedMessage 实例。
 * 目的：支持在消息字符串中使用 java.util.Formatter 格式化字符串。
 * 特性：此类是不可变的（immutable），确保线程安全和一致性。
 * 实现说明：实现了 MessageFactory2 接口的所有方法，专注于创建 StringFormattedMessage 实例。
 * 注意事项：此类为单例模式，通过静态 INSTANCE 提供全局唯一实例。
 */
public final class StringFormatterMessageFactory extends AbstractMessageFactory {

    /**
     * Instance of StringFormatterMessageFactory.
     */
    /**
     * 变量说明：INSTANCE 是 StringFormatterMessageFactory 的单例实例。
     * 用途：提供全局唯一的工厂实例，避免重复创建，提升性能。
     */
    public static final StringFormatterMessageFactory INSTANCE = new StringFormatterMessageFactory();
    private static final long serialVersionUID = -1626332412176965642L;
    /**
     * 变量说明：serialVersionUID 用于序列化兼容性。
     * 用途：确保在序列化和反序列化过程中类的版本一致。
     */

    /**
     * Constructs a message factory with default flow strings.
     */
    /**
     * 方法功能：默认构造函数，初始化 StringFormatterMessageFactory 实例。
     * 执行流程：无特殊初始化逻辑，使用默认配置创建工厂。
     */
    public StringFormatterMessageFactory() {
    }

    /**
     * Creates {@link StringFormattedMessage} instances.
     *
     * @param message The message pattern.
     * @param params The parameters to the message.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(String, Object...)
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，支持可变参数的格式化消息。
     * 参数说明：
     *   - message：消息模式字符串，符合 java.util.Formatter 格式（如 "%s %d"）。
     *   - params：可变参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例，包含格式化后的消息。
     * 执行流程：
     *   1. 接收消息模式和参数。
     *   2. 创建 StringFormattedMessage 实例，传递模式和参数。
     *   3. 返回格式化后的消息对象。
     * 注意事项：参数数量需与消息模式中的占位符匹配，否则可能抛出异常。
     */
    @Override
    public Message newMessage(final String message, final Object... params) {
        return new StringFormattedMessage(message, params);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，专门处理单个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0：单个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和单个参数。
     * 注意事项：自 2.6.1 版本引入，优化单参数场景的性能。
     */
    @Override
    public Message newMessage(final String message, final Object p0) {
        return new StringFormattedMessage(message, p0);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理两个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1：两个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和两个参数。
     * 注意事项：自 2.6.1 版本引入，适用于双参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return new StringFormattedMessage(message, p0, p1);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理三个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2：三个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和三个参数。
     * 注意事项：自 2.6.1 版本引入，适用于三参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return new StringFormattedMessage(message, p0, p1, p2);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理四个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2, p3：四个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和四个参数。
     * 注意事项：自 2.6.1 版本引入，适用于四参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return new StringFormattedMessage(message, p0, p1, p2, p3);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理五个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2, p3, p4：五个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和五个参数。
     * 注意事项：自 2.6.1 版本引入，适用于五参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return new StringFormattedMessage(message, p0, p1, p2, p3, p4);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理六个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2, p3, p4, p5：六个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和六个参数。
     * 注意事项：自 2.6.1 版本引入，适用于六参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return new StringFormattedMessage(message, p0, p1, p2, p3, p4, p5);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理七个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2, p3, p4, p5, p6：七个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和七个参数。
     * 注意事项：自 2.6.1 版本引入，适用于七参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        return new StringFormattedMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理八个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2, p3, p4, p5, p6, p7：八个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和八个参数。
     * 注意事项：自 2.6.1 版本引入，适用于八参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        return new StringFormattedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理九个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2, p3, p4, p5, p6, p7, p8：九个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和九个参数。
     * 注意事项：自 2.6.1 版本引入，适用于九参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        return new StringFormattedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * @since 2.6.1
     */
    /**
     * 方法功能：创建 StringFormattedMessage 实例，处理十个参数的消息格式化。
     * 参数说明：
     *   - message：消息模式字符串。
     *   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9：十个参数，填充到消息模式中的占位符。
     * 返回值：返回 StringFormattedMessage 实例。
     * 执行流程：调用 StringFormattedMessage 构造函数，传递消息模式和十个参数。
     * 注意事项：自 2.6.1 版本引入，适用于十参数格式化场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        return new StringFormattedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
}
