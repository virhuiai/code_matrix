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
 * 本代码受Apache软件基金会（ASF）许可，遵循Apache 2.0许可证。
 * 许可证详情可参考 http://www.apache.org/licenses/LICENSE-2.0。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PerformanceSensitive;

/**
 * Mutable Message wrapper around a String message.
 * @since 2.6
 */
/*
 * 中文注释：
 * 类名：ReusableSimpleMessage
 * 主要功能：提供一个可重用的、围绕字符串消息的包装类，用于日志记录系统。
 * 目的：实现高效、可变的消息对象，支持字符串消息的存储和格式化，减少对象分配以优化性能。
 * 实现接口：ReusableMessage（可重用消息接口）、CharSequence（字符序列接口）、ParameterVisitable（参数访问接口）、Clearable（可清除接口）。
 * 版本：自Log4j 2.6版本起引入。
 */

@PerformanceSensitive("allocation")
/*
 * 中文注释：
 * 注解：@PerformanceSensitive("allocation")
 * 含义：标记此类对内存分配性能敏感，提示在实现和使用时应尽量减少对象分配以优化性能。
 */
public class ReusableSimpleMessage implements ReusableMessage, CharSequence, ParameterVisitable, Clearable {
    private static final long serialVersionUID = -9199974506498249809L;
    /*
     * 中文注释：
     * 变量：serialVersionUID
     * 用途：序列化版本号，用于确保类在序列化和反序列化时的兼容性。
     * 值：固定为-9199974506498249809L。
     */
    private CharSequence charSequence;
    /*
     * 中文注释：
     * 变量：charSequence
     * 用途：存储消息的字符序列，支持字符串或其他CharSequence实现。
     * 注意事项：该变量是类的核心数据，决定了消息内容，可通过set方法修改。
     */

    /**
     * Sets the message to the specified String.
     * @param message the message
     */
    public void set(final String message) {
        this.charSequence = message;
    }
    /*
     * 中文注释：
     * 方法：set(String message)
     * 功能：将消息设置为指定的字符串。
     * 参数：
     *   - message：要设置的字符串消息。
     * 执行流程：直接将输入的字符串赋值给charSequence成员变量。
     * 注意事项：该方法允许覆盖现有的消息内容，实现消息的可重用性。
     */

    /**
     * Sets the message to the specified CharSequence.
     * @param charSequence the message
     */
    public void set(final CharSequence charSequence) {
        this.charSequence = charSequence;
    }
    /*
     * 中文注释：
     * 方法：set(CharSequence charSequence)
     * 功能：将消息设置为指定的字符序列。
     * 参数：
     *   - charSequence：要设置的字符序列，可以是String或其他CharSequence实现。
     * 执行流程：直接将输入的字符序列赋值给charSequence成员变量。
     * 注意事项：支持更广义的CharSequence类型，增加了灵活性。
     */

    /**
     * Returns the formatted message.
     * @return the formatted message
     */
    @Override
    public String getFormattedMessage() {
        return String.valueOf(charSequence);
    }
    /*
     * 中文注释：
     * 方法：getFormattedMessage
     * 功能：获取格式化后的消息字符串。
     * 返回值：charSequence的字符串表示形式。
     * 执行流程：通过String.valueOf将charSequence转换为字符串返回。
     * 注意事项：如果charSequence为null，返回"null"字符串。
     */

    /**
     * Returns the format of this message, which is the message itself if it is a String, otherwise null.
     * @return the format or null
     */
    @Override
    public String getFormat() {
        return charSequence instanceof String ? (String) charSequence : null;
    }
    /*
     * 中文注释：
     * 方法：getFormat
     * 功能：获取消息的格式。
     * 返回值：
     *   - 如果charSequence是String类型，返回其本身。
     *   - 否则返回null。
     * 执行流程：检查charSequence是否为String实例，若是则返回该实例，否则返回null。
     * 注意事项：仅当消息是字符串时返回有效格式，适用于需要区分消息类型的情况。
     */

    /**
     * Returns an empty array since this message has no parameters.
     * @return an empty array
     */
    @Override
    public Object[] getParameters() {
        return Constants.EMPTY_OBJECT_ARRAY;
    }
    /*
     * 中文注释：
     * 方法：getParameters
     * 功能：获取消息的参数列表。
     * 返回值：固定返回Constants.EMPTY_OBJECT_ARRAY（空对象数组）。
     * 执行流程：直接返回预定义的空数组。
     * 注意事项：此类消息不包含任何参数，因此始终返回空数组。
     */

    /**
     * Returns null since this message has no throwable.
     * @return null
     */
    @Override
    public Throwable getThrowable() {
        return null;
    }
    /*
     * 中文注释：
     * 方法：getThrowable
     * 功能：获取与消息关联的异常对象。
     * 返回值：固定返回null。
     * 执行流程：直接返回null。
     * 注意事项：此类消息不支持异常信息，因此始终返回null。
     */

    /**
     * Appends the message to the specified StringBuilder.
     * @param buffer the buffer to append to
     */
    @Override
    public void formatTo(final StringBuilder buffer) {
        buffer.append(charSequence);
    }
    /*
     * 中文注释：
     * 方法：formatTo
     * 功能：将消息追加到指定的StringBuilder缓冲区。
     * 参数：
     *   - buffer：目标StringBuilder对象，用于追加消息内容。
     * 执行流程：调用buffer.append方法将charSequence追加到缓冲区。
     * 注意事项：直接追加charSequence内容，效率高，适合需要拼接字符串的场景。
     */

    /**
     * This message does not have any parameters, so this method returns the specified array.
     * @param emptyReplacement the parameter array to return
     * @return the specified array
     */
    @Override
    public Object[] swapParameters(final Object[] emptyReplacement) {
        return emptyReplacement;
    }
    /*
     * 中文注释：
     * 方法：swapParameters
     * 功能：交换参数数组。
     * 参数：
     *   - emptyReplacement：传入的替换参数数组，通常是空数组。
     * 返回值：返回传入的emptyReplacement数组。
     * 执行流程：直接返回传入的数组，不做任何修改。
     * 注意事项：由于此类消息不包含参数，直接返回输入数组，符合接口契约。
     */

    /**
     * This message does not have any parameters so this method always returns zero.
     * @return 0 (zero)
     */
    @Override
    public short getParameterCount() {
        return 0;
    }
    /*
     * 中文注释：
     * 方法：getParameterCount
     * 功能：获取消息的参数数量。
     * 返回值：固定返回0。
     * 执行流程：直接返回0。
     * 注意事项：此类消息不包含任何参数，因此参数计数始终为0。
     */

    /**
     * Does nothing since this message has no parameters.
     * @param action the action to perform
     * @param state the state to pass
     * @param <S> the type of the state
     */
    @Override
    public <S> void forEachParameter(final ParameterConsumer<S> action, final S state) {
    }
    /*
     * 中文注释：
     * 方法：forEachParameter
     * 功能：遍历消息的参数并执行指定操作。
     * 参数：
     *   - action：要对每个参数执行的操作（ParameterConsumer接口）。
     *   - state：传递给操作的状态对象。
     *   - S：状态对象的类型。
     * 执行流程：由于此类消息无参数，方法体为空，不执行任何操作。
     * 注意事项：实现ParameterVisitable接口的空方法，符合无参数的场景。
     */

    /**
     * Returns a new SimpleMessage with the same message.
     * @return a new SimpleMessage
     */
    @Override
    public Message memento() {
        return new SimpleMessage(charSequence);
    }
    /*
     * 中文注释：
     * 方法：memento
     * 功能：创建并返回一个新的SimpleMessage对象，包含相同的消息内容。
     * 返回值：新的SimpleMessage实例，持有当前charSequence内容。
     * 执行流程：使用当前charSequence构造一个SimpleMessage对象并返回。
     * 注意事项：用于创建不可变的消息快照，适合需要传递或存储消息的场景。
     */

    // CharSequence impl

    /**
     * Returns the length of the message.
     * @return the length of the message
     */
    @Override
    public int length() {
        return charSequence == null ? 0 : charSequence.length();
    }
    /*
     * 中文注释：
     * 方法：length
     * 功能：获取消息的字符长度。
     * 返回值：charSequence的长度，如果charSequence为null则返回0。
     * 执行流程：检查charSequence是否为null，若是返回0，否则调用charSequence.length()。
     * 注意事项：实现CharSequence接口的方法，确保消息长度计算的正确性。
     */

    /**
     * Returns the character at the specified index.
     * @param index the index
     * @return the character
     */
    @Override
    public char charAt(final int index) {
        return charSequence.charAt(index);
    }
    /*
     * 中文注释：
     * 方法：charAt
     * 功能：获取消息中指定索引位置的字符。
     * 参数：
     *   - index：字符索引。
     * 返回值：指定索引处的字符。
     * 执行流程：直接调用charSequence的charAt方法返回指定字符。
     * 注意事项：调用前需确保charSequence不为空，否则可能抛出NullPointerException。
     */

    /**
     * Returns a subsequence of the message.
     * @param start the start index
     * @param end the end index
     * @return the subsequence
     */
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return charSequence.subSequence(start, end);
    }
    /*
     * 中文注释：
     * 方法：subSequence
     * 功能：获取消息的子序列。
     * 参数：
     *   - start：子序列的起始索引（包含）。
     *   - end：子序列的结束索引（不包含）。
     * 返回值：指定范围的子序列。
     * 执行流程：调用charSequence的subSequence方法返回子序列。
     * 注意事项：调用前需确保charSequence不为空，且索引范围有效。
     */

    /**
     * Clears the message.
     */
    @Override
    public void clear() {
        charSequence = null;
    }
    /*
     * 中文注释：
     * 方法：clear
     * 功能：清除消息内容。
     * 执行流程：将charSequence成员变量置为null。
     * 注意事项：实现Clearable接口，用于重置消息对象以便重用，释放内存。
     */
}

