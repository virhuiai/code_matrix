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
 * 代码许可声明：本文件由Apache软件基金会（ASF）根据Apache 2.0许可证授权。
 * 用户需遵守许可证条款，代码按“原样”提供，不附带任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;

/**
 * Creates {@link FormattedMessage} instances for {@link MessageFactory2} methods (and {@link MessageFactory} by
 * extension.)
 * <p>
 * Enables the use of <code>{}</code> parameter markers in message strings.
 * </p>
 * <p>
 * Creates {@link ParameterizedMessage} instances for {@link #newMessage(String, Object...)}.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * 
 * <h4>Note to implementors</h4>
 * <p>
 * This class implements all {@link MessageFactory2} methods.
 * </p>
 */
/*
 * 类功能说明：ParameterizedMessageFactory 是一个用于创建 ParameterizedMessage 实例的工厂类，扩展自 AbstractMessageFactory，
 * 用于支持 MessageFactory2 和 MessageFactory 接口的方法。
 * - 主要功能：生成格式化的日志消息，支持在消息字符串中使用 {} 占位符来动态插入参数。
 * - 不可变性：该类是不可变的，确保线程安全和一致性。
 * - 实现说明：实现了 MessageFactory2 接口的所有方法，提供多种参数数量的 newMessage 方法。
 * - 使用场景：通常用于日志框架中，格式化带有动态参数的日志消息。
 */

public final class ParameterizedMessageFactory extends AbstractMessageFactory {
    /**
     * Instance of ParameterizedMessageFactory.
     */
    /*
     * 静态实例：提供 ParameterizedMessageFactory 的单一实例，遵循单例模式，便于全局访问。
     */
    public static final ParameterizedMessageFactory INSTANCE = new ParameterizedMessageFactory();

    private static final long serialVersionUID = -8970940216592525651L;
    /*
     * 序列化版本号：用于支持类的序列化，保持版本兼容性。
     */

    /**
     * Constructs a message factory.
     */
    /*
     * 构造函数：初始化 ParameterizedMessageFactory 实例。
     * - 功能：创建消息工厂对象，无需额外配置，默认行为支持参数化消息的生成。
     * - 注意事项：构造函数为空，仅用于实例化，实际逻辑在 newMessage 方法中实现。
     */
    public ParameterizedMessageFactory() {
    }

    /**
     * Creates {@link ParameterizedMessage} instances.
     *
     * @param message The message pattern.
     * @param params The message parameters.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(String, Object...)
     */
    /*
     * 方法功能：根据提供的消息模式和参数数组创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：字符串类型的消息模式，包含 {} 占位符，用于格式化输出。
     *   - params：可变参数，类型为 Object 数组，用于填充消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例，包含格式化后的消息内容。
     * - 执行流程：将消息模式和参数传递给 ParameterizedMessage 构造函数，生成格式化消息。
     * - 使用场景：适用于需要动态插入多个参数的日志消息格式化。
     */
    @Override
    public Message newMessage(final String message, final Object... params) {
        return new ParameterizedMessage(message, params);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为单参数情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0：单个 Object 类型的参数，用于替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，优化了单参数场景的性能。
     */
    @Override
    public Message newMessage(final String message, final Object p0) {
        return new ParameterizedMessage(message, p0);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为两个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1：两个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，优化了固定参数数量的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return new ParameterizedMessage(message, p0, p1);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为三个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2：三个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定三个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return new ParameterizedMessage(message, p0, p1, p2);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为四个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2, p3：四个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定四个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return new ParameterizedMessage(message, p0, p1, p2, p3);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为五个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2, p3, p4：五个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定五个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return new ParameterizedMessage(message, p0, p1, p2, p3, p4);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为六个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2, p3, p4, p5：六个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定六个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return new ParameterizedMessage(message, p0, p1, p2, p3, p4, p5);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为七个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2, p3, p4, p5, p6：七个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定七个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        return new ParameterizedMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为八个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2, p3, p4, p5, p6, p7：八个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定八个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        return new ParameterizedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为九个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2, p3, p4, p5, p6, p7, p8：九个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定九个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        return new ParameterizedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * @since 2.6.1
     */
    /*
     * 方法功能：为十个参数的情况创建 ParameterizedMessage 实例。
     * - 参数说明：
     *   - message：消息模式字符串，包含 {} 占位符。
     *   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9：十个 Object 类型的参数，依次替换消息模式中的占位符。
     * - 返回值：返回 ParameterizedMessage 实例。
     * - 注意事项：从 2.6.1 版本开始支持，适用于固定十个参数的场景。
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        return new ParameterizedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
}
