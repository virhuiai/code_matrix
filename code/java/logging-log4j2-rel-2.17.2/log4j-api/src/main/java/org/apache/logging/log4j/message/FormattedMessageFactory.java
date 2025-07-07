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
 * limitations under the License.
 */
package org.apache.logging.log4j.message;

/**
 * Creates {@link FormattedMessage} instances for {@link MessageFactory2} methods (and {@link MessageFactory} by
 * extension.)
 * 
 * 为 {@link MessageFactory2} 方法（以及通过扩展的 {@link MessageFactory}）创建 {@link FormattedMessage} 实例。
 *
 * <h4>Note to implementors</h4>
 * <p>
 * This class implements all {@link MessageFactory2} methods.
 * </p>
 *
 * <h4>致实现者注意</h4>
 * <p>
 * 此类实现了所有 {@link MessageFactory2} 方法。
 * </p>
 */
public class FormattedMessageFactory extends AbstractMessageFactory {

    private static final long serialVersionUID = 1L;
    // 序列化ID，用于版本控制

    /**
     * Constructs a message factory with default flow strings.
     */
    public FormattedMessageFactory() {
        // 构造函数，初始化一个使用默认流字符串的消息工厂。
    }

    /**
     * Creates {@link StringFormattedMessage} instances.
     *
     * 创建 {@link StringFormattedMessage} 实例。
     *
     * @param message The message format.
     * @param params Message parameters.
     * @return The Message object.
     *
     * @param message 消息的格式字符串，例如 "Hello, {}!"。
     * @param params 消息的参数，用于填充格式字符串中的占位符。
     * @return 返回一个 {@link Message} 对象，它封装了格式化后的消息。
     *
     * @see MessageFactory#newMessage(String, Object...)
     */
    @Override
    public Message newMessage(final String message, final Object... params) {
        // 实现 newMessage 方法，接收一个消息格式字符串和可变数量的参数。
        // 返回一个新的 FormattedMessage 实例，该实例会根据提供的格式和参数进行消息格式化。
        return new FormattedMessage(message, params);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0) {
        // 创建一个 FormattedMessage 实例，带有一个参数。
        return new FormattedMessage(message, p0);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        // 创建一个 FormattedMessage 实例，带有两个参数。
        return new FormattedMessage(message, p0, p1);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        // 创建一个 FormattedMessage 实例，带有三个参数。
        return new FormattedMessage(message, p0, p1, p2);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        // 创建一个 FormattedMessage 实例，带有四个参数。
        return new FormattedMessage(message, p0, p1, p2, p3);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        // 创建一个 FormattedMessage 实例，带有五个参数。
        return new FormattedMessage(message, p0, p1, p2, p3, p4);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        // 创建一个 FormattedMessage 实例，带有六个参数。
        return new FormattedMessage(message, p0, p1, p2, p3, p4, p5);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        // 创建一个 FormattedMessage 实例，带有七个参数。
        return new FormattedMessage(message, p0, p1, p2, p3, p4, p5, p6);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        // 创建一个 FormattedMessage 实例，带有八个参数。
        return new FormattedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        // 创建一个 FormattedMessage 实例，带有九个参数。
        return new FormattedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        // 创建一个 FormattedMessage 实例，带有十个参数。
        return new FormattedMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
}
