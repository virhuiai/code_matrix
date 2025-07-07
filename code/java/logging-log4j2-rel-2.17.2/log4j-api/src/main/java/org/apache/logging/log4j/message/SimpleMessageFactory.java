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

// 类功能说明：SimpleMessageFactory 是 AbstractMessageFactory 的一个实现类，用于创建 SimpleMessage 实例。
// 主要用途：为 MessageFactory2 和 MessageFactory 接口提供创建 FormattedMessage 的功能，
//           提供了一种最简单的消息处理方式，直接使用传入的字符串作为消息内容。
// 注意事项：该类是不可变的（immutable），确保线程安全。
// 自 2.5 版本起引入，部分方法在 2.6.1 版本中新增。
/**
 * Creates {@link FormattedMessage} instances for {@link MessageFactory2} methods (and {@link MessageFactory} by
 * extension.)
 * <p>
 * This uses is the simplest possible implementation of {@link Message}, the where you give the message to the
 * constructor argument as a String.
 * </p>
 * <p>
 * Creates {@link StringFormattedMessage} instances for {@link #newMessage(String, Object...)}.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * 
 * <h4>Note to implementors</h4>
 * <p>
 * This class implements all {@link MessageFactory2} methods.
 * </p>
 * 
 * @since 2.5
 */
 // 中文注释：
 // 类功能：该类是 MessageFactory 的简单实现，用于创建基于字符串的 SimpleMessage 实例。
 // 用途：提供一种直接使用字符串作为消息内容的日志消息创建方式，忽略任何格式化参数。
 // 不可变性：类设计为不可变，实例化后无法修改状态，适合多线程环境。
 // 实现接口：实现了 MessageFactory2 的所有方法，并通过继承支持 MessageFactory。
 // 版本说明：自 Apache Log4j 2.5 版本引入，部分方法在 2.6.1 版本新增。
 // 注意事项：该实现忽略所有传入的格式化参数，仅使用 message 字符串创建消息。
public final class SimpleMessageFactory extends AbstractMessageFactory {

    /**
     * Instance of StringFormatterMessageFactory.
     */
     // 中文注释：
     // 变量说明：静态常量 INSTANCE 是 SimpleMessageFactory 的单例实例。
     // 用途：提供全局唯一的工厂实例，避免重复创建，提高性能。
     // 注意事项：单例模式确保线程安全，适合在整个应用中复用。
    public static final SimpleMessageFactory INSTANCE = new SimpleMessageFactory();

    /**
     * Serial version UID for serialization.
     */
     // 中文注释：
     // 变量说明：serialVersionUID 用于序列化过程中的版本控制。
     // 用途：确保序列化和反序列化时的类版本兼容性。
     // 值说明：固定值 4418995198790088516L，用于标识当前类的序列化版本。
    private static final long serialVersionUID = 4418995198790088516L;

    /**
     * Creates {@link StringFormattedMessage} instances.
     *
     * @param message
     *            The message pattern.
     * @param params
     *            The parameters to the message are ignored.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(String, Object...)
     */
     // 中文注释：
     // 方法功能：创建 SimpleMessage 实例，仅使用传入的 message 字符串作为消息内容。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - params: 可变参数，表示消息的格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：
     //   1. 接收 message 字符串和可变参数 params。
     //   2. 创建并返回新的 SimpleMessage 实例，忽略 params 参数。
     // 注意事项：该方法是 MessageFactory2 接口的核心实现，忽略格式化参数以简化消息处理。
    @Override
    public Message newMessage(final String message, final Object... params) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为单参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0: 单个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略 p0。
     // 注意事项：该方法为 2.6.1 版本新增，专门为单参数调用提供便捷接口。
    @Override
    public Message newMessage(final String message, final Object p0) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为双参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1: 两个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略 p0 和 p1。
     // 注意事项：该方法为 2.6.1 版本新增，专门为双参数调用提供便捷接口。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为三参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2: 三个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为四参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2, p3: 四个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为五参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2, p3, p4: 五个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为六参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2, p3, p4, p5: 六个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为七参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2, p3, p4, p5, p6: 七个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为八参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2, p3, p4, p5, p6, p7: 八个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为九参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2, p3, p4, p5, p6, p7, p8: 九个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        return new SimpleMessage(message);
    }

    /**
     * @since 2.6.1
     */
     // 中文注释：
     // 方法功能：为十参数场景创建 SimpleMessage 实例，仅使用 message 字符串。
     // 参数说明：
     //   - message: 字符串，表示消息的原始内容。
     //   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9: 十个格式化参数（在本实现中被忽略）。
     // 返回值：返回 SimpleMessage 实例，仅包含 message 字符串内容。
     // 执行流程：直接调用 SimpleMessage 构造函数，传入 message 参数，忽略所有参数。
     // 注意事项：该方法为 2.6.1 版本新增，支持多参数调用场景。
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        return new SimpleMessage(message);
    }
}
