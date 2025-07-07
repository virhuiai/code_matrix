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

import org.apache.logging.log4j.util.StringBuilderFormattable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The simplest possible implementation of Message. It just returns the String given as the constructor argument.
 *
 * 这是一个最简单的 Message 接口实现类，仅返回构造函数中传入的字符串。
 * - 主要功能：提供简单的消息存储和返回功能，用于日志记录系统中。
 * - 实现接口：Message, StringBuilderFormattable, CharSequence。
 * - 使用场景：适用于简单的日志消息，无需复杂格式化或参数处理。
 */
public class SimpleMessage implements Message, StringBuilderFormattable, CharSequence {
    private static final long serialVersionUID = -8398002534962715992L;
    // 序列化版本号，用于确保类在序列化/反序列化时的兼容性

    private String message;
    // 存储格式化后的消息字符串
    // - 用途：保存最终的字符串消息，供格式化输出使用
    private transient CharSequence charSequence;
    // 存储原始的字符序列，transient 避免序列化
    // - 用途：临时存储输入的 CharSequence，延迟转换为 String

    /**
     * Basic constructor.
     *
     * 默认构造函数，初始化消息为 null。
     * - 功能：创建一个空的 SimpleMessage 实例。
     * - 执行流程：调用带 String 参数的构造函数，传入 null。
     */
    public SimpleMessage() {
        this(null);
    }

    /**
     * Constructor that includes the message.
     * @param message The String message.
     *
     * 带字符串参数的构造函数。
     * - 功能：初始化 SimpleMessage 实例，并设置消息内容。
     * - 参数说明：
     *   - message: 输入的字符串消息，用于日志记录。
     * - 执行流程：将输入的字符串赋值给 message 和 charSequence 字段。
     */
    public SimpleMessage(final String message) {
        this.message = message;
        this.charSequence = message;
    }

    /**
     * Constructor that includes the message.
     * @param charSequence The CharSequence message.
     *
     * 带字符序列参数的构造函数。
     * - 功能：初始化 SimpleMessage 实例，接受 CharSequence 类型的消息。
     * - 参数说明：
     *   - charSequence: 输入的字符序列消息，延迟转换为 String。
     * - 执行流程：仅将输入的 CharSequence 存储到 charSequence 字段，message 字段延迟初始化。
     * - 注意事项：message 字段在 getFormattedMessage() 调用时才会被初始化，以优化性能。
     */
    public SimpleMessage(final CharSequence charSequence) {
        // this.message = String.valueOf(charSequence); // postponed until getFormattedMessage
        this.charSequence = charSequence;
    }

    /**
     * Returns the message.
     * @return the message.
     *
     * 获取格式化后的消息字符串。
     * - 功能：返回存储的消息内容，若 message 未初始化，则从 charSequence 转换。
     * - 返回值：格式化后的字符串消息。
     * - 执行流程：
     *   1. 检查 message 是否为 null。
     *   2. 若为 null，则将 charSequence 转换为 String 并赋值给 message。
     *   3. 返回 message。
     * - 注意事项：延迟初始化 message 字段以提高性能。
     */
    @Override
    public String getFormattedMessage() {
        return message = message == null ? String.valueOf(charSequence) : message ;
    }

    /**
     * Appends the message to the specified StringBuilder.
     *
     * 将消息追加到指定的 StringBuilder 中。
     * - 功能：将消息内容追加到 StringBuilder，用于高效字符串构建。
     * - 参数说明：
     *   - buffer: 目标 StringBuilder 对象，用于存储追加的消息。
     * - 执行流程：检查 message 是否为 null，若不为空则追加 message，否则追加 charSequence。
     * - 使用场景：用于日志输出或其他需要高效字符串拼接的场景。
     */
    @Override
    public void formatTo(final StringBuilder buffer) {
	buffer.append(message != null ? message : charSequence);
    }

    /**
     * Returns the message.
     * @return the message.
     *
     * 获取消息的原始格式。
     * - 功能：直接返回 message 字段内容。
     * - 返回值：存储的字符串消息，若未初始化则返回 null。
     * - 注意事项：与 getFormattedMessage 不同，此方法不触发 message 初始化。
     */
    @Override
    public String getFormat() {
        return message;
    }

    /**
     * Returns null since there are no parameters.
     * @return null.
     *
     * 获取消息参数。
     * - 功能：返回消息的参数列表。
     * - 返回值：始终返回 null，因为 SimpleMessage 不支持参数化消息。
     * - 注意事项：此方法为 Message 接口的实现，适用于无参数的简单消息。
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    /**
     * Compares this object to another for equality.
     *
     * 比较两个 SimpleMessage 对象是否相等。
     * - 功能：基于 charSequence 字段比较两个对象是否相等。
     * - 参数说明：
     *   - o: 待比较的对象。
     * - 返回值：若两个对象的 charSequence 内容相等，则返回 true，否则返回 false。
     * - 执行流程：
     *   1. 检查是否为同一对象。
     *   2. 检查对象是否为 null 或类型不匹配。
     *   3. 比较 charSequence 字段内容。
     * - 注意事项：仅比较 charSequence，message 字段不直接参与比较。
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimpleMessage that = (SimpleMessage) o;

        return !(charSequence != null ? !charSequence.equals(that.charSequence) : that.charSequence != null);
    }

    /**
     * Returns the hash code of the message.
     *
     * 计算对象的哈希值。
     * - 功能：基于 charSequence 字段生成哈希值。
     * - 返回值：charSequence 的哈希值，若为 null 则返回 0。
     * - 注意事项：与 equals 方法一致，仅基于 charSequence 计算。
     */
    @Override
    public int hashCode() {
        return charSequence != null ? charSequence.hashCode() : 0;
    }

    /**
     * Returns the string representation of the message.
     *
     * 返回消息的字符串表示。
     * - 功能：调用 getFormattedMessage 获取格式化消息。
     * - 返回值：格式化后的消息字符串。
     * - 执行流程：直接调用 getFormattedMessage 方法。
     */
    @Override
    public String toString() {
        return getFormattedMessage();
    }

    /**
     * Always returns null.
     *
     * @return null
     *
     * 获取关联的异常对象。
     * - 功能：返回与消息关联的异常。
     * - 返回值：始终返回 null，因为 SimpleMessage 不支持异常信息。
     */
    @Override
    public Throwable getThrowable() {
        return null;
    }


    // CharSequence impl

    /**
     * Returns the length of the message.
     *
     * 返回消息的长度。
     * - 功能：实现 CharSequence 接口的 length 方法。
     * - 返回值：charSequence 的字符长度，若为 null 则返回 0。
     */
    @Override
    public int length() {
        return charSequence == null ? 0 : charSequence.length();
    }

    /**
     * Returns the character at the specified index.
     *
     * 返回指定索引处的字符。
     * - 功能：实现 CharSequence 接口的 charAt 方法。
     * - 参数说明：
     *   - index: 要获取字符的索引位置。
     * - 返回值：指定索引处的字符。
     * - 注意事项：调用 charSequence 的 charAt 方法，若 charSequence 为 null 会抛出异常。
     */
    @Override
    public char charAt(final int index) {
        return charSequence.charAt(index);
    }

    /**
     * Returns a subsequence of the message.
     *
     * 返回消息的子序列。
     * - 功能：实现 CharSequence 接口的 subSequence 方法。
     * - 参数说明：
     *   - start: 子序列的起始索引（包含）。
     *   - end: 子序列的结束索引（不包含）。
     * - 返回值：指定范围的子序列。
     * - 注意事项：调用 charSequence 的 subSequence 方法，若 charSequence 为 null 会抛出异常。
     */
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return charSequence.subSequence(start, end);
    }

    /**
     * Custom serialization method.
     *
     * 自定义序列化方法。
     * - 功能：确保对象序列化时正确保存消息内容。
     * - 参数说明：
     *   - out: 对象输出流，用于序列化。
     * - 执行流程：
     *   1. 调用 getFormattedMessage 初始化 message 字段。
     *   2. 执行默认的序列化操作。
     * - 注意事项：charSequence 为 transient，序列化时仅保存 message。
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        getFormattedMessage(); // initialize the message:String field
        out.defaultWriteObject();
    }

    /**
     * Custom deserialization method.
     *
     * 自定义反序列化方法。
     * - 功能：从输入流中恢复对象状态。
     * - 参数说明：
     *   - in: 对象输入流，用于反序列化。
     * - 执行流程：
     *   1. 执行默认的反序列化操作，恢复 message 字段。
     *   2. 将 message 赋值给 charSequence。
     * - 注意事项：charSequence 在反序列化后需重新初始化为 message。
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        charSequence = message;
    }
}
