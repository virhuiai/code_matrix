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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Handles messages that consist of a format string conforming to {@link java.util.Formatter}.
 * 
 * <h4>Note to implementors</h4>
 * <p>
 * This class implements the unrolled args API even though StringFormattedMessage does not. This leaves the room for
 * StringFormattedMessage to unroll itself later.
 * </p>
 */
/*
 * 处理符合 java.util.Formatter 格式的字符串消息。
 * 类的主要功能：提供一种基于格式化字符串的消息处理机制，支持根据指定的消息模式和参数生成格式化后的消息。
 * 注意事项：当前实现支持未展开的参数API，为未来可能的参数展开留有余地。
 */
public class StringFormattedMessage implements Message {

    private static final Logger LOGGER = StatusLogger.getLogger();
    // 静态变量：用于记录日志的Logger实例，获取StatusLogger以记录格式化错误等状态信息。

    private static final long serialVersionUID = -665975803997290697L;
    // 静态变量：序列化版本ID，确保类在序列化和反序列化时的兼容性。

    private static final int HASHVAL = 31;
    // 静态变量：用于hashCode计算的乘数常量，固定为31，用于生成哈希值。

    private String messagePattern;
    // 实例变量：存储消息的格式化模式字符串，例如"User %s logged in at %s"。

    private transient Object[] argArray;
    // 实例变量：存储格式化参数的数组，transient表示不直接序列化，用于格式化消息。

    private String[] stringArgs;
    // 实例变量：存储参数的字符串表示形式，用于序列化时保存参数内容。

    private transient String formattedMessage;
    // 实例变量：存储格式化后的消息内容，transient表示不直接序列化。

    private transient Throwable throwable;
    // 实例变量：存储可能附加的异常对象，transient表示不直接序列化。

    private final Locale locale;
    // 实例变量：存储用于格式化的语言环境（Locale），影响格式化规则（如日期、数字格式）。

   /**
    * Constructs a message.
    * 
    * @param locale the locale for this message format
    * @param messagePattern the pattern for this message format
    * @param arguments The objects to format
    * @since 2.6
    */
    /*
     * 构造函数：初始化一个基于指定语言环境、消息模式和参数的格式化消息。
     * 参数说明：
     *   - locale：语言环境，决定格式化规则（如日期、数字的显示方式）。
     *   - messagePattern：消息格式化模式字符串，符合java.util.Formatter规范。
     *   - arguments：可变参数，包含需要格式化的对象，可能包括异常对象。
     * 执行流程：
     *   1. 初始化语言环境、消息模式和参数数组。
     *   2. 检查参数数组最后一个元素是否为Throwable类型，若是则存储为异常对象。
     * 注意事项：如果参数数组为空或不包含异常，throwable将为null。
     */
    public StringFormattedMessage(final Locale locale, final String messagePattern, final Object... arguments) {
        this.locale = locale;
        this.messagePattern = messagePattern;
        this.argArray = arguments;
        if (arguments != null && arguments.length > 0 && arguments[arguments.length - 1] instanceof Throwable) {
            this.throwable = (Throwable) arguments[arguments.length - 1];
        }
    }

    /**
     * Constructs a message.
     * 
     * @param messagePattern the pattern for this message format
     * @param arguments The objects to format
     * @since 2.6
     */
    /*
     * 构造函数：使用默认语言环境（Locale.Category.FORMAT）初始化格式化消息。
     * 参数说明：
     *   - messagePattern：消息格式化模式字符串。
     *   - arguments：可变参数，包含需要格式化的对象。
     * 执行流程：
     *   1. 调用主构造函数，传入默认语言环境、消息模式和参数。
     * 注意事项：默认语言环境基于JVM的格式化类别设置，可能因系统环境不同而变化。
     */
    public StringFormattedMessage(final String messagePattern, final Object... arguments) {
        this(Locale.getDefault(Locale.Category.FORMAT), messagePattern, arguments);
    }

    /**
     * Returns the formatted message.
     * @return the formatted message.
     */
    /*
     * 方法功能：获取格式化后的消息字符串。
     * 返回值：格式化后的消息字符串。
     * 执行流程：
     *   1. 检查formattedMessage是否已生成。
     *   2. 如果未生成，调用formatMessage方法生成格式化消息。
     *   3. 返回格式化后的消息。
     * 注意事项：formattedMessage是transient的，序列化后需重新生成。
     */
    @Override
    public String getFormattedMessage() {
        if (formattedMessage == null) {
            formattedMessage = formatMessage(messagePattern, argArray);
        }
        return formattedMessage;
    }

    /**
     * Returns the message pattern.
     * @return the message pattern.
     */
    /*
     * 方法功能：获取消息的格式化模式字符串。
     * 返回值：消息模式字符串（messagePattern）。
     * 注意事项：直接返回存储的消息模式，未进行任何处理。
     */
    @Override
    public String getFormat() {
        return messagePattern;
    }

    /**
     * Returns the message parameters.
     * @return the message parameters.
     */
    /*
     * 方法功能：获取消息的参数数组。
     * 返回值：参数对象数组（argArray）或字符串参数数组（stringArgs）。
     * 执行流程：
     *   1. 如果argArray不为空，返回argArray。
     *   2. 否则返回stringArgs（通常在反序列化后使用）。
     * 注意事项：argArray是transient的，序列化后依赖stringArgs存储参数。
     */
    @Override
    public Object[] getParameters() {
        if (argArray != null) {
            return argArray;
        }
        return stringArgs;
    }

    /**
     * Formats the message and return it.
     * @param msgPattern the pattern for this message format
     * @param args The objects to format
     * @return the formatted message
     */
    /*
     * 方法功能：根据消息模式和参数格式化消息。
     * 参数说明：
     *   - msgPattern：消息格式化模式字符串。
     *   - args：需要格式化的参数数组。
     * 返回值：格式化后的消息字符串。
     * 执行流程：
     *   1. 使用String.format结合指定语言环境和参数格式化消息。
     *   2. 如果格式化失败（抛出IllegalFormatException），记录错误并返回原始消息模式。
     * 注意事项：格式化错误不会抛出异常，仅记录日志并返回未格式化的消息模式。
     */
    protected String formatMessage(final String msgPattern, final Object... args) {
        try {
            return String.format(locale, msgPattern, args);
        } catch (final IllegalFormatException ife) {
            LOGGER.error("Unable to format msg: " + msgPattern, ife);
            return msgPattern;
        }
    }

    /**
     * Checks if this message is equal to another object.
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    /*
     * 方法功能：比较当前消息对象与另一对象是否相等。
     * 参数说明：
     *   - o：待比较的对象。
     * 返回值：true表示相等，false表示不相等。
     * 执行流程：
     *   1. 检查是否为同一对象，若是返回true。
     *   2. 检查对象是否为null或类型不同，若是返回false。
     *   3. 比较messagePattern和stringArgs是否相等。
     * 注意事项：比较基于消息模式和字符串参数数组，忽略transient字段。
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final StringFormattedMessage that = (StringFormattedMessage) o;

        if (messagePattern != null ? !messagePattern.equals(that.messagePattern) : that.messagePattern != null) {
            return false;
        }

        return Arrays.equals(stringArgs, that.stringArgs);
    }

    /**
     * Returns a hash code value for the message.
     * @return the hash code
     */
    /*
     * 方法功能：生成消息对象的哈希值。
     * 返回值：哈希值（整数）。
     * 执行流程：
     *   1. 计算messagePattern的哈希值（若为null则为0）。
     *   2. 使用HASHVAL（31）乘以结果，并加上stringArgs的哈希值。
     * 注意事项：哈希值基于messagePattern和stringArgs，transient字段不参与计算。
     */
    @Override
    public int hashCode() {
        int result = messagePattern != null ? messagePattern.hashCode() : 0;
        result = HASHVAL * result + (stringArgs != null ? Arrays.hashCode(stringArgs) : 0);
        return result;
    }

    /**
     * Returns a string representation of the message.
     * @return the formatted message
     */
    /*
     * 方法功能：返回消息的字符串表示形式。
     * 返回值：格式化后的消息字符串。
     * 执行流程：直接调用getFormattedMessage方法获取格式化消息。
     * 注意事项：依赖getFormattedMessage的缓存机制，避免重复格式化。
     */
    @Override
    public String toString() {
        return getFormattedMessage();
    }

    /**
     * Custom serialization method.
     * @param out the output stream
     * @throws IOException if an I/O error occurs
     */
    /*
     * 方法功能：自定义序列化方法，将对象写入输出流。
     * 参数说明：
     *   - out：对象输出流。
     * 执行流程：
     *   1. 调用默认序列化方法写入非transient字段。
     *   2. 调用getFormattedMessage确保formattedMessage已生成。
     *   3. 写入formattedMessage、messagePattern和参数数组长度。
     *   4. 将argArray转换为stringArgs并逐一写入。
     * 注意事项：transient字段（如argArray）通过stringArgs序列化。
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        getFormattedMessage();
        out.writeUTF(formattedMessage);
        out.writeUTF(messagePattern);
        out.writeInt(argArray.length);
        stringArgs = new String[argArray.length];
        int i = 0;
        for (final Object obj : argArray) {
            final String string = String.valueOf(obj);
            stringArgs[i] = string;
            out.writeUTF(string);
            ++i;
        }
    }

    /**
     * Custom deserialization method.
     * @param in the input stream
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be found
     */
    /*
     * 方法功能：自定义反序列化方法，从输入流读取对象。
     * 参数说明：
     *   - in：对象输入流。
     * 执行流程：
     *   1. 调用默认反序列化方法读取非transient字段。
     *   2. 读取formattedMessage、messagePattern和参数数组长度。
     *   3. 初始化stringArgs并逐一读取字符串参数。
     * 注意事项：transient字段（如argArray）通过stringArgs恢复。
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        formattedMessage = in.readUTF();
        messagePattern = in.readUTF();
        final int length = in.readInt();
        stringArgs = new String[length];
        for (int i = 0; i < length; ++i) {
            stringArgs[i] = in.readUTF();
        }
    }

    /**
     * Return the throwable passed to the Message.
     *
     * @return the Throwable.
     */
    /*
     * 方法功能：返回消息中附加的异常对象。
     * 返回值：异常对象（Throwable），可能为null。
     * 注意事项：throwable仅在构造函数中检测到参数数组最后一个元素为Throwable时设置。
     */
    @Override
    public Throwable getThrowable() {
        return throwable;
    }
}
