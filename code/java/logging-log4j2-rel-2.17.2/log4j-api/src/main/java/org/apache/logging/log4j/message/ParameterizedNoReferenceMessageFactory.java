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
// 许可证声明：本代码遵循Apache 2.0许可证，明确了版权归属和使用限制。

package org.apache.logging.log4j.message;

import org.apache.logging.log4j.status.StatusLogger;

/**
 * Creates {@link FormattedMessage} instances for {@link MessageFactory2} methods (and {@link MessageFactory} by
 * extension.)
 * <p>
 * Creates {@link SimpleMessage} objects that do not retain a reference to the parameter object.
 * </p>
 * <p>
 * Intended for use by the {@link StatusLogger}: this logger retains a queue of recently logged messages in memory,
 * causing memory leaks in web applications. (LOG4J2-1176)
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * <h4>Note to implementors</h4>
 * <p>
 * This class does <em>not</em> implement any {@link MessageFactory2} methods and lets the superclass funnel those calls
 * through {@link #newMessage(String, Object...)}.
 * </p>
 */
// 类功能：ParameterizedNoReferenceMessageFactory 用于为 MessageFactory2 和 MessageFactory 创建 FormattedMessage 实例。
// 目的：生成不保留对参数对象引用的 SimpleMessage 对象，主要用于 StatusLogger，避免在 Web 应用中因日志消息队列导致的内存泄漏（LOG4J2-1176）。
// 特性：此类是不可变的（immutable），不直接实现 MessageFactory2 的方法，而是通过父类的 newMessage 方法处理。
public final class ParameterizedNoReferenceMessageFactory extends AbstractMessageFactory {
    private static final long serialVersionUID = 5027639245636870500L;
    // 序列化版本号：用于确保类在序列化和反序列化时的兼容性，固定值为 5027639245636870500L。

    /**
     * Message implementation that only keeps a reference to the error text and the error (if any), not to the
     * message parameters, in order to avoid memory leaks. This addresses LOG4J2-1368.
     * @since 2.6
     */
    // 内部类功能：StatusMessage 是一个消息实现类，仅保留格式化后的消息文本和异常（如果有），不持有消息参数引用，以避免内存泄漏（LOG4J2-1368）。
    // 自 2.6 版本引入。
    static class StatusMessage implements Message {
        private static final long serialVersionUID = 4199272162767841280L;
        // 序列化版本号：确保 StatusMessage 类的序列化兼容性，固定值为 4199272162767841280L。
        private final String formattedMessage;
        // 变量用途：存储格式化后的消息文本。
        private final Throwable throwable;
        // 变量用途：存储与消息关联的异常对象（如果存在）。

        public StatusMessage(final String formattedMessage, final Throwable throwable) {
            this.formattedMessage = formattedMessage;
            this.throwable = throwable;
        }
        // 构造函数功能：初始化 StatusMessage 实例。
        // 参数说明：
        // - formattedMessage：格式化后的消息文本。
        // - throwable：与消息关联的异常对象，可能为 null。
        // 执行流程：将传入的格式化消息和异常存储到实例变量中。

        @Override
        public String getFormattedMessage() {
            return formattedMessage;
        }
        // 方法功能：返回格式化后的消息文本。
        // 返回值：已格式化的消息字符串。
        // 注意事项：直接返回存储的 formattedMessage 变量，不涉及额外计算。

        @Override
        public String getFormat() {
            return formattedMessage;
        }
        // 方法功能：返回消息的格式（与格式化后的消息相同）。
        // 返回值：格式化消息字符串，与 getFormattedMessage 一致。
        // 注意事项：此方法为接口实现，返回与 getFormattedMessage 相同的内容。

        @Override
        public Object[] getParameters() {
            return null;
        }
        // 方法功能：返回消息参数。
        // 返回值：始终返回 null，因为 StatusMessage 不保留参数引用，以避免内存泄漏。
        // 注意事项：此设计是避免内存泄漏的关键。

        @Override
        public Throwable getThrowable() {
            return throwable;
        }
        // 方法功能：返回与消息关联的异常对象。
        // 返回值：存储的 throwable 对象，可能为 null。
        // 注意事项：直接返回存储的异常引用。
    }

    /**
     * Constructs a message factory with default flow strings.
     */
    // 构造函数功能：创建 ParameterizedNoReferenceMessageFactory 实例，使用默认的流程字符串配置。
    // 注意事项：无参数构造，初始化为空，依赖父类 AbstractMessageFactory 的默认行为。
    public ParameterizedNoReferenceMessageFactory() {
    }

    /**
     * Instance of ParameterizedStatusMessageFactory.
     */
    // 单例实例：提供 ParameterizedNoReferenceMessageFactory 的静态实例，方便全局访问。
    public static final ParameterizedNoReferenceMessageFactory INSTANCE = new ParameterizedNoReferenceMessageFactory();
    // 变量用途：INSTANCE 是一个静态常量，用于实现单例模式，确保全局唯一实例。

    /**
     * Creates {@link SimpleMessage} instances containing the formatted parameterized message string.
     *
     * @param message The message pattern.
     * @param params The message parameters.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(String, Object...)
     */
    // 方法功能：创建包含格式化参数化消息字符串的 SimpleMessage 或 StatusMessage 实例。
    // 参数说明：
    // - message：消息模式字符串，包含占位符（如 "{}"）用于格式化。
    // - params：可变参数数组，包含要插入消息模式中的参数。
    // 返回值：Message 接口的实例，可能是 SimpleMessage 或 StatusMessage。
    // 执行流程：
    // 1. 如果 params 为 null，直接创建 SimpleMessage 实例，仅包含消息文本。
    // 2. 否则，创建 ParameterizedMessage 实例进行消息格式化。
    // 3. 使用格式化后的消息和异常（如果有）创建 StatusMessage 实例。
    // 注意事项：此方法确保不保留对 params 的引用，以避免内存泄漏。
    @Override
    public Message newMessage(final String message, final Object... params) {
        if (params == null) {
            return new SimpleMessage(message);
        }
        final ParameterizedMessage msg = new ParameterizedMessage(message, params);
        return new StatusMessage(msg.getFormattedMessage(), msg.getThrowable());
    }
}
