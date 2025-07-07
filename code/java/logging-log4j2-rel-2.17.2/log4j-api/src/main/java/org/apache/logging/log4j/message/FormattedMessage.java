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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Handles messages that contain a format String. Dynamically determines if the format conforms to
 * MessageFormat or String.format and if not then uses ParameterizedMessage to format.
 * 处理包含格式字符串的消息。动态判断格式是否符合 MessageFormat 或 String.format，如果都不符合则使用 ParameterizedMessage 进行格式化。
 */
public class FormattedMessage implements Message {

    private static final long serialVersionUID = -665975803997290697L;
    // 序列化ID，用于确保序列化和反序列化时的兼容性。
    private static final int HASHVAL = 31;
    // 用于哈希计算的常量值。
    private static final String FORMAT_SPECIFIER = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
    // Java String.format 格式说明符的正则表达式。
    private static final Pattern MSG_PATTERN = Pattern.compile(FORMAT_SPECIFIER);
    // 编译后的正则表达式模式，用于匹配 String.format 的格式说明符。

    private String messagePattern;
    // 消息的格式模式字符串。
    private transient Object[] argArray;
    // 消息的原始参数数组，transient 关键字表示在序列化时不会被保存。
    private String[] stringArgs;
    // 消息参数的字符串表示形式数组，用于序列化。
    private transient String formattedMessage;
    // 格式化后的消息字符串，transient 关键字表示在序列化时不会被保存。
    private final Throwable throwable;
    // 与消息关联的异常对象。
    private Message message;
    // 实际用于格式化消息的 Message 实现（如 MessageFormatMessage, StringFormattedMessage 或 ParameterizedMessage）。
    private final Locale locale;
    // 消息的区域设置，影响消息的格式化方式。

    /**
     * Constructs with a locale, a pattern and a single parameter.
     * 使用区域设置、消息模式和单个参数构造 FormattedMessage 实例。
     * @param locale The locale
     * 区域设置
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arg The parameter.
     * 单个参数。
     * @since 2.6
     */
    public FormattedMessage(final Locale locale, final String messagePattern, final Object arg) {
        this(locale, messagePattern, new Object[] { arg }, null);
        // 调用包含参数数组和 throwable 的构造函数，将单个参数包装成数组。
    }

    /**
     * Constructs with a locale, a pattern and two parameters.
     * 使用区域设置、消息模式和两个参数构造 FormattedMessage 实例。
     * @param locale The locale
     * 区域设置
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arg1 The first parameter.
     * 第一个参数。
     * @param arg2 The second parameter.
     * 第二个参数。
     * @since 2.6
     */
    public FormattedMessage(final Locale locale, final String messagePattern, final Object arg1, final Object arg2) {
        this(locale, messagePattern, new Object[] { arg1, arg2 });
        // 调用包含参数数组的构造函数，将两个参数包装成数组。
    }

    /**
     * Constructs with a locale, a pattern and a parameter array.
     * 使用区域设置、消息模式和参数数组构造 FormattedMessage 实例。
     * @param locale The locale
     * 区域设置
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arguments The parameter.
     * 参数数组。
     * @since 2.6
     */
    public FormattedMessage(final Locale locale, final String messagePattern, final Object... arguments) {
        this(locale, messagePattern, arguments, null);
        // 调用包含参数数组和 throwable 的构造函数。
    }

    /**
     * Constructs with a locale, a pattern, a parameter array, and a throwable.
     * 使用区域设置、消息模式、参数数组和异常对象构造 FormattedMessage 实例。
     * @param locale The Locale
     * 区域设置
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arguments The parameter.
     * 参数数组。
     * @param throwable The throwable
     * 异常对象。
     * @since 2.6
     */
    public FormattedMessage(final Locale locale, final String messagePattern, final Object[] arguments, final Throwable throwable) {
        this.locale = locale;
        // 初始化区域设置。
        this.messagePattern = messagePattern;
        // 初始化消息模式。
        this.argArray = arguments;
        // 初始化参数数组。
        this.throwable = throwable;
        // 初始化异常对象。
    }

    /**
     * Constructs with a pattern and a single parameter.
     * 使用消息模式和单个参数构造 FormattedMessage 实例。
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arg The parameter.
     * 单个参数。
     */
    public FormattedMessage(final String messagePattern, final Object arg) {
        this(messagePattern, new Object[] { arg }, null);
        // 调用包含参数数组和 throwable 的构造函数，将单个参数包装成数组。
    }

    /**
     * Constructs with a pattern and two parameters.
     * 使用消息模式和两个参数构造 FormattedMessage 实例。
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arg1 The first parameter.
     * 第一个参数。
     * @param arg2 The second parameter.
     * 第二个参数。
     */
    public FormattedMessage(final String messagePattern, final Object arg1, final Object arg2) {
        this(messagePattern, new Object[] { arg1, arg2 });
        // 调用包含参数数组的构造函数，将两个参数包装成数组。
    }

    /**
     * Constructs with a pattern and a parameter array.
     * 使用消息模式和参数数组构造 FormattedMessage 实例。
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arguments The parameter.
     * 参数数组。
     */
    public FormattedMessage(final String messagePattern, final Object... arguments) {
        this(messagePattern, arguments, null);
        // 调用包含参数数组和 throwable 的构造函数。
    }

    /**
     * Constructs with a pattern, a parameter array, and a throwable.
     * 使用消息模式、参数数组和异常对象构造 FormattedMessage 实例。
     * @param messagePattern The message pattern.
     * 消息模式字符串。
     * @param arguments The parameter.
     * 参数数组。
     * @param throwable The throwable
     * 异常对象。
     */
    public FormattedMessage(final String messagePattern, final Object[] arguments, final Throwable throwable) {
        this.locale = Locale.getDefault(Locale.Category.FORMAT);
        // 初始化区域设置，使用默认的 FORMAT 类别区域设置。
        this.messagePattern = messagePattern;
        // 初始化消息模式。
        this.argArray = arguments;
        // 初始化参数数组。
        this.throwable = throwable;
        // 初始化异常对象。
    }


    @Override
    public boolean equals(final Object o) {
        // 重写 equals 方法，用于比较两个 FormattedMessage 实例是否相等。
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FormattedMessage that = (FormattedMessage) o;
        // 将 o 强制转换为 FormattedMessage 类型。

        if (messagePattern != null ? !messagePattern.equals(that.messagePattern) : that.messagePattern != null) {
            // 比较 messagePattern 是否相等。
            return false;
        }
        if (!Arrays.equals(stringArgs, that.stringArgs)) {
            // 比较 stringArgs 数组是否相等。
            return false;
        }

        return true;
        // 如果 messagePattern 和 stringArgs 都相等，则认为两个对象相等。
    }

    /**
     * Gets the message pattern.
     * 获取消息模式字符串。
     * @return the message pattern.
     * 消息模式字符串。
     */
    @Override
    public String getFormat() {
        return messagePattern;
        // 返回存储的消息模式。
    }

    /**
     * Gets the formatted message.
     * 获取格式化后的消息字符串。
     * @return the formatted message.
     * 格式化后的消息字符串。
     */
    @Override
    public String getFormattedMessage() {
        if (formattedMessage == null) {
            // 如果 formattedMessage 尚未生成。
            if (message == null) {
                // 如果内部的 Message 实例尚未生成。
                message = getMessage(messagePattern, argArray, throwable);
                // 根据消息模式、参数和异常获取合适的 Message 实例。
            }
            formattedMessage = message.getFormattedMessage();
            // 通过内部 Message 实例获取格式化后的消息。
        }
        return formattedMessage;
        // 返回格式化后的消息。
    }

    protected Message getMessage(final String msgPattern, final Object[] args, final Throwable aThrowable) {
        // 这是一个受保护的方法，用于根据消息模式、参数和异常选择合适的 Message 实现。
        try {
            final MessageFormat format = new MessageFormat(msgPattern);
            // 尝试将消息模式解析为 MessageFormat。
            final Format[] formats = format.getFormats();
            // 获取 MessageFormat 的格式器数组。
            if (formats != null && formats.length > 0) {
                // 如果存在格式器，说明是 MessageFormat 兼容的模式。
                return new MessageFormatMessage(locale, msgPattern, args);
                // 返回 MessageFormatMessage 实例进行处理。
            }
        } catch (final Exception ignored) {
            // Obviously, the message is not a proper pattern for MessageFormat.
            // 捕获异常，表示消息模式不适合 MessageFormat。
        }
        try {
            if (MSG_PATTERN.matcher(msgPattern).find()) {
                // 尝试使用正则表达式匹配 String.format 的格式说明符。
                return new StringFormattedMessage(locale, msgPattern, args);
                // 如果匹配成功，返回 StringFormattedMessage 实例进行处理。
            }
        } catch (final Exception ignored) {
            // Also not properly formatted.
            // 捕获异常，表示消息模式不适合 String.format。
        }
        return new ParameterizedMessage(msgPattern, args, aThrowable);
        // 如果以上两种格式都不匹配，则回退到使用 ParameterizedMessage 进行处理。
    }

    /**
     * Gets the message parameters.
     * 获取消息参数数组。
     * @return the message parameters.
     * 消息参数数组。
     */
    @Override
    public Object[] getParameters() {
        if (argArray != null) {
            // 如果原始参数数组存在。
            return argArray;
            // 返回原始参数数组。
        }
        return stringArgs;
        // 否则返回字符串类型的参数数组（通常在反序列化后使用）。
    }

    @Override
    public Throwable getThrowable() {
        // 获取与消息关联的异常。
        if (throwable != null) {
            // 如果构造函数中直接提供了异常。
            return throwable;
            // 返回该异常。
        }
        if (message == null) {
            // 如果内部的 Message 实例尚未生成。
            message = getMessage(messagePattern, argArray, null);
            // 根据消息模式和参数获取合适的 Message 实例（不传递 throwable，因为我们正在尝试获取它）。
        }
        return message.getThrowable();
        // 通过内部 Message 实例获取异常。
    }


    @Override
    public int hashCode() {
        // 重写 hashCode 方法，用于计算对象的哈希值。
        int result = messagePattern != null ? messagePattern.hashCode() : 0;
        // 计算 messagePattern 的哈希值。
        result = HASHVAL * result + (stringArgs != null ? Arrays.hashCode(stringArgs) : 0);
        // 将 messagePattern 的哈希值与 stringArgs 的哈希值结合，使用 HASHVAL 进行乘法运算。
        return result;
        // 返回最终的哈希值。
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        // 自定义反序列化方法。
        in.defaultReadObject();
        // 调用默认的反序列化方法，恢复非 transient 字段。
        formattedMessage = in.readUTF();
        // 从输入流中读取格式化后的消息字符串。
        messagePattern = in.readUTF();
        // 从输入流中读取消息模式字符串。
        final int length = in.readInt();
        // 从输入流中读取参数数组的长度。
        stringArgs = new String[length];
        // 初始化字符串参数数组。
        for (int i = 0; i < length; ++i) {
            stringArgs[i] = in.readUTF();
            // 循环读取每个字符串参数。
        }
    }

    @Override
    public String toString() {
        // 重写 toString 方法，返回格式化后的消息。
        return getFormattedMessage();
        // 返回 getFormattedMessage 方法的结果。
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        // 自定义序列化方法。
        out.defaultWriteObject();
        // 调用默认的序列化方法，保存非 transient 字段。
        getFormattedMessage();
        // 确保 formattedMessage 字段被初始化（如果尚未）。
        out.writeUTF(formattedMessage);
        // 将格式化后的消息字符串写入输出流。
        out.writeUTF(messagePattern);
        // 将消息模式字符串写入输出流。
        out.writeInt(argArray.length);
        // 将参数数组的长度写入输出流。
        stringArgs = new String[argArray.length];
        // 初始化用于序列化的字符串参数数组。
        int i = 0;
        for (final Object obj : argArray) {
            // 遍历原始参数数组。
            final String string = String.valueOf(obj);
            // 将每个参数转换为字符串。
            stringArgs[i] = string;
            // 存储到 stringArgs 数组中。
            out.writeUTF(string);
            // 将字符串参数写入输出流。
            ++i;
            // 递增计数器。
        }
    }
}
