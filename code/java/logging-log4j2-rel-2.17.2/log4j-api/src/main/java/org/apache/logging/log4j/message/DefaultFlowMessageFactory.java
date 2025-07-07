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

import java.io.Serializable;

/**
 * Default factory for flow messages.
 * 流程消息的默认工厂。
 *
 * @since 2.6
 */
public class DefaultFlowMessageFactory implements FlowMessageFactory, Serializable {

    private static final String EXIT_DEFAULT_PREFIX = "Exit";
    // 退出消息的默认前缀
    private static final String ENTRY_DEFAULT_PREFIX = "Enter";
    // 进入消息的默认前缀
    private static final long serialVersionUID = 8578655591131397576L;
    // 序列化ID，用于确保序列化和反序列化过程中的兼容性

    private final String entryText;
    // 用于表示进入的文本
    private final String exitText;
    // 用于表示退出的文本

    /**
     * Constructs a message factory with {@code "Enter"} and {@code "Exit"} as the default flow strings.
     * 构造一个消息工厂，使用“Enter”和“Exit”作为默认的流程字符串。
     */
    public DefaultFlowMessageFactory() {
        this(ENTRY_DEFAULT_PREFIX, EXIT_DEFAULT_PREFIX);
    }

    /**
     * Constructs a message factory with the given entry and exit strings.
     * 构造一个消息工厂，使用给定的进入和退出字符串。
     * @param entryText the text to use for trace entry, like {@code "Enter"}.
     * 用于跟踪进入的文本，例如“Enter”。
     * @param exitText the text to use for trace exit, like {@code "Exit"}.
     * 用于跟踪退出的文本，例如“Exit”。
     */
    public DefaultFlowMessageFactory(final String entryText, final String exitText) {
        this.entryText = entryText;
        this.exitText = exitText;
    }

    /**
     * Abstract base class for flow messages.
     * 流程消息的抽象基类。
     */
    private static class AbstractFlowMessage implements FlowMessage {

        private static final long serialVersionUID = 1L;
        // 序列化ID
        private final Message message;
        // 实际的消息内容
        private final String text;
        // 流程消息的前缀文本（例如“Enter”或“Exit”）

        /**
         * Constructs an AbstractFlowMessage.
         * 构造一个 AbstractFlowMessage 实例。
         * @param text the flow message prefix text.
         * 流程消息的前缀文本。
         * @param message the actual message.
         * 实际的消息。
         */
        AbstractFlowMessage(final String text, final Message message) {
            this.message = message;
            this.text = text;
        }

        @Override
        public String getFormattedMessage() {
            // 获取格式化后的消息。
            if (message != null) {
                return text + " " + message.getFormattedMessage();
                // 如果存在实际消息，则将其格式化消息与流程文本拼接。
            }
            return text;
            // 如果没有实际消息，则只返回流程文本。
        }

        @Override
        public String getFormat() {
            // 获取消息的格式字符串。
            if (message != null) {
                return text + ": " + message.getFormat();
                // 如果存在实际消息，则将其格式与流程文本拼接。
            }
            return text;
            // 如果没有实际消息，则只返回流程文本。
        }

        @Override
        public Object[] getParameters() {
            // 获取消息的参数数组。
            if (message != null) {
                return message.getParameters();
                // 如果存在实际消息，则返回其参数。
            }
            return null;
            // 如果没有实际消息，则返回 null。
        }

        @Override
        public Throwable getThrowable() {
            // 获取与消息关联的 Throwable。
            if (message != null) {
                return message.getThrowable();
                // 如果存在实际消息，则返回其 Throwable。
            }
            return null;
            // 如果没有实际消息，则返回 null。
        }

        @Override
        public Message getMessage() {
            // 获取封装的实际消息。
            return message;
        }

        @Override
        public String getText() {
            // 获取流程消息的前缀文本。
            return text;
        }
    }

    /**
     * Simple implementation of EntryMessage.
     * EntryMessage 的简单实现。
     */
    private static final class SimpleEntryMessage extends AbstractFlowMessage implements EntryMessage {

        private static final long serialVersionUID = 1L;
        // 序列化ID

        /**
         * Constructs a SimpleEntryMessage.
         * 构造一个 SimpleEntryMessage 实例。
         * @param entryText the entry message text.
         * 进入消息的文本。
         * @param message the actual message.
         * 实际的消息。
         */
        SimpleEntryMessage(final String entryText, final Message message) {
            super(entryText, message);
        }

    }

    /**
     * Simple implementation of ExitMessage.
     * ExitMessage 的简单实现。
     */
    private static final class SimpleExitMessage extends AbstractFlowMessage implements ExitMessage {

        private static final long serialVersionUID = 1L;
        // 序列化ID

        private final Object result;
        // 方法的返回值。
        private final boolean isVoid;
        // 指示方法是否为 void 返回类型。

        /**
         * Constructs a SimpleExitMessage for a void method.
         * 构造一个用于 void 方法的 SimpleExitMessage 实例。
         * @param exitText the exit message text.
         * 退出消息的文本。
         * @param message the entry message associated with this exit.
         * 与此退出消息关联的进入消息。
         */
        SimpleExitMessage(final String exitText, final EntryMessage message) {
            super(exitText, message.getMessage());
            this.result = null;
            isVoid = true;
        }

        /**
         * Constructs a SimpleExitMessage for a method with a return value.
         * 构造一个用于带有返回值的方法的 SimpleExitMessage 实例。
         * @param exitText the exit message text.
         * 退出消息的文本。
         * @param result the return value of the method.
         * 方法的返回值。
         * @param message the entry message associated with this exit.
         * 与此退出消息关联的进入消息。
         */
        SimpleExitMessage(final String exitText, final Object result, final EntryMessage message) {
            super(exitText, message.getMessage());
            this.result = result;
            isVoid = false;
        }

        /**
         * Constructs a SimpleExitMessage for a method with a return value and a general message.
         * 构造一个用于带有返回值和通用消息的方法的 SimpleExitMessage 实例。
         * @param exitText the exit message text.
         * 退出消息的文本。
         * @param result the return value of the method.
         * 方法的返回值。
         * @param message the general message.
         * 通用消息。
         */
        SimpleExitMessage(final String exitText, final Object result, final Message message) {
            super(exitText, message);
            this.result = result;
            isVoid = false;
        }

        @Override
        public String getFormattedMessage() {
            // 获取格式化后的退出消息。
            final String formattedMessage = super.getFormattedMessage();
            // 调用父类的 getFormattedMessage 方法获取基础格式化消息。
            if (isVoid) {
                return formattedMessage;
                // 如果是 void 方法，则直接返回基础格式化消息。
            }
            return formattedMessage + ": " + result;
            // 如果有返回值，则将返回值拼接到基础格式化消息后面。
        }
    }

    /**
     * Gets the entry text.
     * 获取进入文本。
     * @return the entry text.
     * 进入文本。
     */
    public String getEntryText() {
        return entryText;
    }

    /**
     * Gets the exit text.
     * 获取退出文本。
     * @return the exit text.
     * 退出文本。
     */
    public String getExitText() {
        return exitText;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newEntryMessage(org.apache.logging.log4j.message.Message)
     */
    @Override
    public EntryMessage newEntryMessage(final Message message) {
        // 根据给定的消息创建一个新的 EntryMessage。
        // 主要功能：创建并返回一个表示方法进入的 SimpleEntryMessage 实例。
        // 执行流程：
        // 1. 调用 makeImmutable 方法确保传入的 message 是不可变的。
        // 2. 使用 entryText 和处理后的 message 构造 SimpleEntryMessage。
        // 参数说明：
        // @param message 原始的日志消息。
        // 返回值说明：
        // @return 一个 EntryMessage 实例。
        return new SimpleEntryMessage(entryText, makeImmutable(message));
    }

    /**
     * Makes a message immutable if it's a ReusableMessage.
     * 如果消息是 ReusableMessage，则使其变为不可变。
     * @param message the message to potentially make immutable.
     * 可能需要变为不可变的消息。
     * @return an immutable message.
     * 一个不可变的消息。
     * 关键步骤：
     * 1. 检查传入的 message 是否是 ReusableMessage 的实例。
     * 2. 如果是，则创建一个新的 SimpleMessage，其内容为原始 ReusableMessage 的格式化消息。
     * 3. 如果不是 ReusableMessage，则直接返回原始 message。
     */
    private Message makeImmutable(final Message message) {
        if (!(message instanceof ReusableMessage)) {
            return message;
        }
        return new SimpleMessage(message.getFormattedMessage());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.FlowMessageFactory#newExitMessage(org.apache.logging.log4j.message.EntryMessage)
     */
    @Override
    public ExitMessage newExitMessage(final EntryMessage message) {
        // 根据给定的 EntryMessage 创建一个新的 ExitMessage。
        // 主要功能：创建并返回一个表示方法退出（无返回值）的 SimpleExitMessage 实例。
        // 执行流程：
        // 1. 使用 exitText 和传入的 EntryMessage 构造 SimpleExitMessage。
        // 参数说明：
        // @param message 对应的进入消息。
        // 返回值说明：
        // @return 一个 ExitMessage 实例。
        return new SimpleExitMessage(exitText, message);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.FlowMessageFactory#newExitMessage(java.lang.Object, org.apache.logging.log4j.message.EntryMessage)
     */
    @Override
    public ExitMessage newExitMessage(final Object result, final EntryMessage message) {
        // 根据给定的返回值和 EntryMessage 创建一个新的 ExitMessage。
        // 主要功能：创建并返回一个表示方法退出（有返回值）的 SimpleExitMessage 实例。
        // 执行流程：
        // 1. 使用 exitText、返回值 result 和传入的 EntryMessage 构造 SimpleExitMessage。
        // 参数说明：
        // @param result 方法的返回值。
        // @param message 对应的进入消息。
        // 返回值说明：
        // @return 一个 ExitMessage 实例。
        return new SimpleExitMessage(exitText, result, message);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.FlowMessageFactory#newExitMessage(java.lang.Object, org.apache.logging.log4j.message.Message)
     */
    @Override
    public ExitMessage newExitMessage(final Object result, final Message message) {
        // 根据给定的返回值和通用消息创建一个新的 ExitMessage。
        // 主要功能：创建并返回一个表示方法退出（有返回值，不一定与 EntryMessage 关联）的 SimpleExitMessage 实例。
        // 执行流程：
        // 1. 使用 exitText、返回值 result 和传入的通用 Message 构造 SimpleExitMessage。
        // 特殊处理逻辑：此方法允许创建退出消息时，不强制关联一个 EntryMessage，而是接受一个通用的 Message。
        // 参数说明：
        // @param result 方法的返回值。
        // @param message 原始的日志消息。
        // 返回值说明：
        // @return 一个 ExitMessage 实例。
        return new SimpleExitMessage(exitText, result, message);
    }
}
