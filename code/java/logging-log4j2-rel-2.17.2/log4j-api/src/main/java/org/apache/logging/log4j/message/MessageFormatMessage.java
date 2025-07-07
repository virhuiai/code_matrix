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
 * 本文件遵循Apache 2.0许可证，归Apache软件基金会所有，用于声明版权和许可信息。
 * 软件按“现状”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Handles messages that consist of a format string conforming to java.text.MessageFormat.
 *
 * @serial In version 2.1, due to a bug in the serialization format, the serialization format was changed along with
 * its {@code serialVersionUID} value.
 */
/*
 * 中文注释：
 * 类功能：MessageFormatMessage 用于处理符合 java.text.MessageFormat 格式的日志消息。
 * 实现目的：将格式化字符串与参数结合，生成结构化的日志消息，支持国际化（Locale）。
 * 序列化说明：2.1 版本因序列化格式错误，修改了格式并更新了 serialVersionUID。
 * 注意事项：类实现了 Message 接口，支持序列化以便在分布式系统中传输消息。
 */
public class MessageFormatMessage implements Message {

    private static final Logger LOGGER = StatusLogger.getLogger();
    /*
     * 中文注释：
     * 变量功能：LOGGER 是用于记录日志的静态 Logger 实例，来自 StatusLogger。
     * 用途：用于记录格式化过程中的错误信息。
     */

    private static final long serialVersionUID = 1L;
    /*
     * 中文注释：
     * 变量功能：serialVersionUID 是序列化版本号，用于确保序列化/反序列化兼容性。
     * 值说明：固定为 1L，确保类在序列化时的版本一致性。
     */

    private static final int HASHVAL = 31;
    /*
     * 中文注释：
     * 变量功能：HASHVAL 是用于计算哈希码的常量因子。
     * 用途：在 hashCode 方法中用于生成对象的哈希值，确保分布均匀。
     */

    private String messagePattern;
    /*
     * 中文注释：
     * 变量功能：messagePattern 存储消息的格式化字符串，符合 java.text.MessageFormat 规范。
     * 用途：定义消息的结构，例如 "User {0} logged in at {1}"。
     */
    private transient Object[] parameters;
    /*
     * 中文注释：
     * 变量功能：parameters 存储格式化参数，transient 表示不直接序列化。
     * 用途：提供消息格式化所需的动态数据，如用户名、时间等。
     */
    private String[] serializedParameters;
    /*
     * 中文注释：
     * 变量功能：serializedParameters 存储序列化后的参数（字符串形式）。
     * 用途：在序列化时保存参数值，反序列化时恢复。
     */
    private transient String formattedMessage;
    /*
     * 中文注释：
     * 变量功能：formattedMessage 存储格式化后的最终消息，transient 表示不序列化。
     * 用途：缓存格式化结果，避免重复计算。
     */
    private transient Throwable throwable;
    /*
     * 中文注释：
     * 变量功能：throwable 存储与消息关联的异常对象，transient 表示不序列化。
     * 用途：记录日志消息中可能包含的异常信息。
     */
    private final Locale locale;
    /*
     * 中文注释：
     * 变量功能：locale 定义消息格式化的语言环境。
     * 用途：支持国际化，确保消息按指定地区格式（如日期、数字）显示。
     */

    /**
     * Constructs a message.
     * 
     * @param locale the locale for this message format
     * @param messagePattern the pattern for this message format
     * @param parameters The objects to format
     * @since 2.6
     */
    /*
     * 中文注释：
     * 方法功能：构造函数，初始化 MessageFormatMessage 对象。
     * 参数说明：
     *   - locale：语言环境，用于控制消息格式化的地区特定行为（如语言、日期格式）。
     *   - messagePattern：消息格式化模板，符合 java.text.MessageFormat 规范。
     *   - parameters：可变参数，传递格式化所需的数据。
     * 执行流程：
     *   1. 保存 locale 和 messagePattern。
     *   2. 保存 parameters 数组。
     *   3. 检查 parameters 最后一个元素是否为 Throwable，若是则保存到 throwable。
     * 注意事项：parameters 可为 null，需处理空数组情况；若包含异常，需特殊处理。
     * @since 2.6 表示此构造函数自版本 2.6 起引入。
     */
    public MessageFormatMessage(final Locale locale, final String messagePattern, final Object... parameters) {
        this.locale = locale;
        this.messagePattern = messagePattern;
        this.parameters = parameters;
        final int length = parameters == null ? 0 : parameters.length;
        if (length > 0 && parameters[length - 1] instanceof Throwable) {
            this.throwable = (Throwable) parameters[length - 1];
        }
    }

    /**
     * Constructs a message.
     * 
     * @param messagePattern the pattern for this message format
     * @param parameters The objects to format
     */
    /*
     * 中文注释：
     * 方法功能：构造函数，使用默认语言环境初始化 MessageFormatMessage。
     * 参数说明：
     *   - messagePattern：消息格式化模板。
     *   - parameters：格式化参数。
     * 执行流程：
     *   1. 调用主构造函数，传入默认语言环境（Locale.getDefault(Locale.Category.FORMAT)）。
     * 注意事项：默认语言环境基于系统设置，适合不需要特定地区格式化的场景。
     */
    public MessageFormatMessage(final String messagePattern, final Object... parameters) {
        this(Locale.getDefault(Locale.Category.FORMAT), messagePattern, parameters);
    }

    /**
     * Returns the formatted message.
     * @return the formatted message.
     */
    /*
     * 中文注释：
     * 方法功能：获取格式化后的消息字符串。
     * 返回值：格式化后的消息字符串。
     * 执行流程：
     *   1. 检查 formattedMessage 是否已缓存。
     *   2. 若未缓存，调用 formatMessage 方法生成格式化消息并缓存。
     * 注意事项：使用缓存避免重复格式化，提升性能。
     */
    @Override
    public String getFormattedMessage() {
        if (formattedMessage == null) {
            formattedMessage = formatMessage(messagePattern, parameters);
        }
        return formattedMessage;
    }

    /**
     * Returns the message pattern.
     * @return the message pattern.
     */
    /*
     * 中文注释：
     * 方法功能：获取消息的格式化模板。
     * 返回值：messagePattern 字符串，即消息的格式化模板。
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
     * 中文注释：
     * 方法功能：获取消息的参数。
     * 返回值：parameters 数组（优先）或 serializedParameters 数组（序列化后）。
     * 注意事项：优先返回非序列化的 parameters，若不可用则返回序列化的参数。
     */
    @Override
    public Object[] getParameters() {
        if (parameters != null) {
            return parameters;
        }
        return serializedParameters;
    }

    /**
     * Formats the message and returns it.
     * @param msgPattern the pattern for this message format
     * @param args The objects to format
     * @return the formatted message
     */
    /*
     * 中文注释：
     * 方法功能：根据模板和参数格式化消息。
     * 参数说明：
     *   - msgPattern：消息格式化模板。
     *   - args：格式化参数。
     * 返回值：格式化后的消息字符串。
     * 执行流程：
     *   1. 创建 MessageFormat 实例，传入模板和语言环境。
     *   2. 调用 MessageFormat.format 方法格式化消息。
     *   3. 若发生 IllegalFormatException，记录错误并返回原始模板。
     * 注意事项：捕获格式化异常，确保程序稳定性。
     */
    protected String formatMessage(final String msgPattern, final Object... args) {
        try {
            final MessageFormat temp = new MessageFormat(msgPattern, locale);
            return temp.format(args);
        } catch (final IllegalFormatException ife) {
            LOGGER.error("Unable to format msg: " + msgPattern, ife);
            return msgPattern;
        }
    }

    /**
     * Compares this object with the specified object for equality.
     * @param o the Object to be compared with
     * @return true if the objects are equal
     */
    /*
     * 中文注释：
     * 方法功能：比较两个 MessageFormatMessage 对象是否相等。
     * 参数说明：
     *   - o：待比较的对象。
     * 返回值：true 表示相等，false 表示不相等。
     * 执行流程：
     *   1. 检查是否为同一对象。
     *   2. 检查对象类型和非空性。
     *   3. 比较 messagePattern 和 serializedParameters 是否相等。
     * 注意事项：比较基于序列化后的参数，确保序列化场景一致性。
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final MessageFormatMessage that = (MessageFormatMessage) o;

        if (messagePattern != null ? !messagePattern.equals(that.messagePattern) : that.messagePattern != null) {
            return false;
        }
        return Arrays.equals(serializedParameters, that.serializedParameters);
    }

    /**
     * Returns a hash code value for the object.
     * @return a hash code value
     */
    /*
     * 中文注释：
     * 方法功能：计算对象的哈希值。
     * 返回值：对象的哈希值。
     * 执行流程：
     *   1. 计算 messagePattern 的哈希值（若非空）。
     *   2. 结合 serializedParameters 的哈希值，使用 HASHVAL 因子。
     * 注意事项：确保哈希值在序列化前后一致，适用于哈希表等场景。
     */
    @Override
    public int hashCode() {
        int result = messagePattern != null ? messagePattern.hashCode() : 0;
        result = HASHVAL * result + (serializedParameters != null ? Arrays.hashCode(serializedParameters) : 0);
        return result;
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation
     */
    /*
     * 中文注释：
     * 方法功能：返回对象的字符串表示。
     * 返回值：格式化后的消息字符串（通过 getFormattedMessage 获取）。
     */
    @Override
    public String toString() {
        return getFormattedMessage();
    }

    /**
     * Writes this object to an ObjectOutputStream.
     * @param out the ObjectOutputStream to write to
     * @throws IOException if an I/O error occurs
     */
    /*
     * 中文注释：
     * 方法功能：序列化对象到输出流。
     * 参数说明：
     *   - out：目标 ObjectOutputStream。
     * 执行流程：
     *   1. 调用 getFormattedMessage 确保格式化消息已生成。
     *   2. 写入 formattedMessage 和 messagePattern。
     *   3. 写入 parameters 长度，并将参数转为字符串写入 serializedParameters。
     * 注意事项：parameters 转为字符串存储，确保序列化兼容性。
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        getFormattedMessage();
        out.writeUTF(formattedMessage);
        out.writeUTF(messagePattern);
        final int length = parameters == null ? 0 : parameters.length;
        out.writeInt(length);
        serializedParameters = new String[length];
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                serializedParameters[i] = String.valueOf(parameters[i]);
                out.writeUTF(serializedParameters[i]);
            }
        }
    }

    /**
     * Reads this object from an ObjectInputStream.
     * @param in the ObjectInputStream to read from
     * @throws IOException if an I/O error occurs
     */
    /*
     * 中文注释：
     * 方法功能：从输入流反序列化对象。
     * 参数说明：
     *   - in：源 ObjectInputStream。
     * 执行流程：
     *   1. 清空 parameters 和 throwable。
     *   2. 读取 formattedMessage 和 messagePattern。
     *   3. 读取参数长度，初始化 serializedParameters 并读取参数值。
     * 注意事项：transient 字段（如 parameters）在反序列化后需手动恢复。
     */
    private void readObject(final ObjectInputStream in) throws IOException {
        parameters = null;
        throwable = null;
        formattedMessage = in.readUTF();
        messagePattern = in.readUTF();
        final int length = in.readInt();
        serializedParameters = new String[length];
        for (int i = 0; i < length; ++i) {
            serializedParameters[i] = in.readUTF();
        }
    }

    /**
     * Return the throwable passed to the Message.
     *
     * @return the Throwable.
     */
    /*
     * 中文注释：
     * 方法功能：获取与消息关联的异常对象。
     * 返回值：throwable 对象，若无则为 null。
     */
    @Override
    public Throwable getThrowable() {
        return throwable;
    }
}
