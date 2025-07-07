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
// 中文注释：
// 本文件遵循Apache 2.0许可证发布，归Apache软件基金会所有，禁止在未遵守许可证的情况下使用。

package org.apache.logging.log4j.message;

import java.util.Map;

import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.StringBuilders;

/**
 * Represents a Message that conforms to an RFC 5424 StructuredData element along with the syslog message.
 * <p>
 * Thread-safety note: the contents of this message can be modified after construction.
 * When using asynchronous loggers and appenders it is not recommended to modify this message after the message is
 * logged, because it is undefined whether the logged message string will contain the old values or the modified
 * values.
 * </p>
 *
 * @see <a href="https://tools.ietf.org/html/rfc5424">RFC 5424</a>
 */
// 中文注释：
// 类功能：StructuredDataMessage表示符合RFC 5424标准的结构化数据消息，结合syslog消息格式。
// 线程安全说明：对象构造后内容可修改，但异步日志记录器或追加器使用时，建议不要在记录后修改消息内容，因为无法确定记录的消息是旧值还是新值。
// 用途：用于日志系统中生成和格式化符合RFC 5424的结构化数据消息。
// 关键引用：RFC 5424标准定义了syslog消息的结构化数据格式。

@AsynchronouslyFormattable
public class StructuredDataMessage extends MapMessage<StructuredDataMessage, String> {

    private static final long serialVersionUID = 1703221292892071920L;
    // 中文注释：
    // 变量用途：serialVersionUID用于序列化，保持类版本兼容性。

    private static final int MAX_LENGTH = 32;
    // 中文注释：
    // 配置参数：MAX_LENGTH定义ID和类型的最大长度为32个字符，符合RFC 5424规范。

    private static final int HASHVAL = 31;
    // 中文注释：
    // 配置参数：HASHVAL用于hashCode计算的乘数，确保哈希值分布均匀。

    private StructuredDataId id;
    // 中文注释：
    // 变量用途：id表示结构化数据的唯一标识符，符合RFC 5424的SD-ID定义。

    private String message;
    // 中文注释：
    // 变量用途：message存储日志消息的主要内容。

    private String type;
    // 中文注释：
    // 变量用途：type表示消息的类型，最大长度为32个字符。

    private final int maxLength;
    // 中文注释：
    // 变量用途：maxLength定义键的最大长度，默认值为MAX_LENGTH（32）。

    /**
     * Supported formats.
     */
    // 中文注释：
    // 枚举说明：Format枚举定义了支持的格式化类型。
    public enum Format {
        /** The map should be formatted as XML. */
        // 中文注释：
        // XML格式：将结构化数据格式化为XML字符串。
        XML,
        /** Full message format includes the type and message. */
        // 中文注释：
        // FULL格式：完整消息格式，包含类型和消息内容。
        FULL
    }

    /**
     * Creates a StructuredDataMessage using an ID (max 32 characters), message, and type (max 32 characters).
     * @param id The String id.
     * @param msg The message.
     * @param type The message type.
     */
    // 中文注释：
    // 方法功能：使用字符串ID、消息内容和类型构造StructuredDataMessage对象，默认键最大长度为32。
    // 参数：
    //   - id：字符串形式的结构化数据ID，最大32字符。
    //   - msg：日志消息内容。
    //   - type：消息类型，最大32字符。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 调用带maxLength参数的构造函数，传递默认值MAX_LENGTH。
    public StructuredDataMessage(final String id, final String msg, final String type) {
        this(id, msg, type, MAX_LENGTH);
    }

    /**
     * Creates a StructuredDataMessage using an ID (user specified max characters), message, and type (user specified
     * maximum number of characters).
     * @param id The String id.
     * @param msg The message.
     * @param type The message type.
     * @param maxLength The maximum length of keys;
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：使用字符串ID、消息内容、类型和用户指定的键最大长度构造StructuredDataMessage对象。
    // 参数：
    //   - id：字符串形式的结构化数据ID。
    //   - msg：日志消息内容。
    //   - type：消息类型。
    //   - maxLength：键的最大长度，用户自定义。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 创建StructuredDataId对象，设置ID和最大长度。
    //   2. 初始化message、type和maxLength属性。
    // 注意事项：自2.9版本引入，允许自定义键长度。
    public StructuredDataMessage(final String id, final String msg, final String type, final int maxLength) {
        this.id = new StructuredDataId(id, null, null, maxLength);
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }
    
    /**
     * Creates a StructuredDataMessage using an ID (max 32 characters), message, type (max 32 characters), and an
     * initial map of structured data to include.
     * @param id The String id.
     * @param msg The message.
     * @param type The message type.
     * @param data The StructuredData map.
     */
    // 中文注释：
    // 方法功能：使用字符串ID、消息内容、类型和初始结构化数据映射构造StructuredDataMessage对象，默认键最大长度为32。
    // 参数：
    //   - id：字符串形式的结构化数据ID。
    //   - msg：日志消息内容。
    //   - type：消息类型。
    //   - data：初始的结构化数据键值对映射。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 调用带maxLength参数的构造函数，传递默认值MAX_LENGTH。
    public StructuredDataMessage(final String id, final String msg, final String type,
                                 final Map<String, String> data) {
        this(id, msg, type, data, MAX_LENGTH);
    }

    /**
     * Creates a StructuredDataMessage using an (user specified max characters), message, and type (user specified
     * maximum number of characters, and an initial map of structured data to include.
     * @param id The String id.
     * @param msg The message.
     * @param type The message type.
     * @param data The StructuredData map.
     * @param maxLength The maximum length of keys;
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：使用字符串ID、消息内容、类型、初始数据映射和用户指定的键最大长度构造StructuredDataMessage对象。
    // 参数：
    //   - id：字符串形式的结构化数据ID。
    //   - msg：日志消息内容。
    //   - type：消息类型。
    //   - data：初始的结构化数据键值对映射。
    //   - maxLength：键的最大长度，用户自定义。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 调用父类MapMessage的构造函数，初始化数据映射。
    //   2. 创建StructuredDataId对象，设置ID和最大长度。
    //   3. 初始化message、type和maxLength属性。
    // 注意事项：自2.9版本引入，允许自定义键长度。
    public StructuredDataMessage(final String id, final String msg, final String type,
                                 final Map<String, String> data, final int maxLength) {
        super(data);
        this.id = new StructuredDataId(id, null, null, maxLength);
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    /**
     * Creates a StructuredDataMessage using a StructuredDataId, message, and type (max 32 characters).
     * @param id The StructuredDataId.
     * @param msg The message.
     * @param type The message type.
     */
    // 中文注释：
    // 方法功能：使用StructuredDataId对象、消息内容和类型构造StructuredDataMessage对象，默认键最大长度为32。
    // 参数：
    //   - id：结构化数据ID对象。
    //   - msg：日志消息内容。
    //   - type：消息类型。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 调用带maxLength参数的构造函数，传递默认值MAX_LENGTH。
    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type) {
        this(id, msg, type, MAX_LENGTH);
    }

    /**
     * Creates a StructuredDataMessage using a StructuredDataId, message, and type (max 32 characters).
     * @param id The StructuredDataId.
     * @param msg The message.
     * @param type The message type.
     * @param maxLength The maximum length of keys;
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：使用StructuredDataId对象、消息内容、类型和用户指定的键最大长度构造StructuredDataMessage对象。
    // 参数：
    //   - id：结构化数据ID对象。
    //   - msg：日志消息内容。
    //   - type：消息类型。
    //   - maxLength：键的最大长度，用户自定义。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 初始化id、message、type和maxLength属性。
    // 注意事项：自2.9版本引入，允许自定义键长度。
    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type, final int maxLength) {
        this.id = id;
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }

    /**
     * Creates a StructuredDataMessage using a StructuredDataId, message, type (max 32 characters), and an initial map
     * of structured data to include.
     * @param id The StructuredDataId.
     * @param msg The message.
     * @param type The message type.
     * @param data The StructuredData map.
     */
    // 中文注释：
    // 方法功能：使用StructuredDataId对象、消息内容、类型和初始数据映射构造StructuredDataMessage对象，默认键最大长度为32。
    // 参数：
    //   - id：结构化数据ID对象。
    //   - msg：日志消息内容。
    //   - type：消息类型。
    //   - data：初始的结构化数据键值对映射。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 调用带maxLength参数的构造函数，传递默认值MAX_LENGTH。
    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type,
                                 final Map<String, String> data) {
        this(id, msg, type, data, MAX_LENGTH);
    }

    /**
     * Creates a StructuredDataMessage using a StructuredDataId, message, type (max 32 characters), and an initial map
     * of structured data to include.
     * @param id The StructuredDataId.
     * @param msg The message.
     * @param type The message type.
     * @param data The StructuredData map.
     * @param maxLength The maximum length of keys;
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：使用StructuredDataId对象、消息内容、类型、初始数据映射和用户指定的键最大长度构造StructuredDataMessage对象。
    // 参数：
    //   - id：结构化数据ID对象。
    //   - msg：日志消息内容。
    //   - type：消息类型。
    //   - data：初始的结构化数据键值对映射。
    //   - maxLength：键的最大长度，用户自定义。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 调用父类MapMessage的构造函数，初始化数据映射。
    //   2. 初始化id、message、type和maxLength属性。
    // 注意事项：自2.9版本引入，允许自定义键长度。
    public StructuredDataMessage(final StructuredDataId id, final String msg, final String type,
                                 final Map<String, String> data, final int maxLength) {
        super(data);
        this.id = id;
        this.message = msg;
        this.type = type;
        this.maxLength = maxLength;
    }


    /**
     * Constructor based on a StructuredDataMessage.
     * @param msg The StructuredDataMessage.
     * @param map The StructuredData map.
     */
    // 中文注释：
    // 方法功能：基于现有的StructuredDataMessage对象和新的数据映射构造一个新对象。
    // 参数：
    //   - msg：现有的StructuredDataMessage对象。
    //   - map：新的结构化数据键值对映射。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 调用父类MapMessage的构造函数，初始化数据映射。
    //   2. 复制msg对象的id、message和type属性。
    //   3. 设置maxLength为默认值MAX_LENGTH。
    private StructuredDataMessage(final StructuredDataMessage msg, final Map<String, String> map) {
        super(map);
        this.id = msg.id;
        this.message = msg.message;
        this.type = msg.type;
        this.maxLength = MAX_LENGTH;
    }

    /**
     * Basic constructor.
     */
    // 中文注释：
    // 方法功能：默认构造函数，初始化StructuredDataMessage对象。
    // 返回值：无（构造函数）。
    // 执行流程：
    //   1. 设置maxLength为默认值MAX_LENGTH（32）。
    // 注意事项：此构造函数不初始化id、message或type，需后续设置。
    protected StructuredDataMessage() {
        maxLength = MAX_LENGTH;
    }

    /**
     * Returns the supported formats.
     * @return An array of the supported format names.
     */
    // 中文注释：
    // 方法功能：返回支持的格式化类型名称数组。
    // 返回值：字符串数组，包含所有Format枚举值的名称。
    // 执行流程：
    //   1. 创建与Format枚举值数量相同的字符串数组。
    //   2. 遍历Format枚举值，将其名称存入数组。
    @Override
    public String[] getFormats() {
        final String[] formats = new String[Format.values().length];
        int i = 0;
        for (final Format format : Format.values()) {
            formats[i++] = format.name();
        }
        return formats;
    }

    /**
     * Returns this message id.
     * @return the StructuredDataId.
     */
    // 中文注释：
    // 方法功能：获取结构化数据的ID。
    // 返回值：StructuredDataId对象，表示消息的唯一标识符。
    public StructuredDataId getId() {
        return id;
    }

    /**
     * Sets the id from a String. This ID can be at most 32 characters long.
     * @param id The String id.
     */
    // 中文注释：
    // 方法功能：设置结构化数据的ID，基于字符串输入。
    // 参数：
    //   - id：字符串形式的ID，最大32字符。
    // 返回值：无。
    // 执行流程：
    //   1. 创建新的StructuredDataId对象，使用输入的id字符串。
    // 注意事项：ID长度受maxLength限制，默认为32字符。
    protected void setId(final String id) {
        this.id = new StructuredDataId(id, null, null);
    }

    /**
     * Sets the id.
     * @param id The StructuredDataId.
     */
    // 中文注释：
    // 方法功能：设置结构化数据的ID。
    // 参数：
    //   - id：StructuredDataId对象。
    // 返回值：无。
    // 执行流程：
    //   1. 直接将输入的id赋值给类成员变量。
    protected void setId(final StructuredDataId id) {
        this.id = id;
    }

    /**
     * Returns this message type.
     * @return the type.
     */
    // 中文注释：
    // 方法功能：获取消息的类型。
    // 返回值：字符串，表示消息的类型。
    public String getType() {
        return type;
    }

    /**
     * Sets the type, ensuring it does not exceed the maximum length.
     * @param type The message type.
     */
    // 中文注释：
    // 方法功能：设置消息的类型，并验证其长度。
    // 参数：
    //   - type：消息类型字符串。
    // 返回值：无。
    // 执行流程：
    //   1. 检查type长度是否超过maxLength（默认32）。
    //   2. 若超过，抛出IllegalArgumentException。
    //   3. 否则，将type赋值给类成员变量。
    // 注意事项：确保类型符合RFC 5424规范的长度限制。
    protected void setType(final String type) {
        if (type.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("structured data type exceeds maximum length of 32 characters: " + type);
        }
        this.type = type;
    }

    /**
     * Formats the message to a StringBuilder.
     * @param buffer The StringBuilder to append to.
     */
    // 中文注释：
    // 方法功能：将消息格式化为字符串并追加到指定的StringBuilder。
    // 参数：
    //   - buffer：目标StringBuilder，用于存储格式化结果。
    // 返回值：无。
    // 执行流程：
    //   1. 调用asString方法，传递FULL格式和null的StructuredDataId。
    @Override
    public void formatTo(final StringBuilder buffer) {
        asString(Format.FULL, null, buffer);
    }

    /**
     * Formats the message to a StringBuilder based on the specified formats.
     * @param formats The format specifiers.
     * @param buffer The StringBuilder to append to.
     */
    // 中文注释：
    // 方法功能：根据指定的格式化类型，将消息格式化并追加到StringBuilder。
    // 参数：
    //   - formats：格式化类型数组（如"XML"或"FULL"）。
    //   - buffer：目标StringBuilder。
    // 返回值：无。
    // 执行流程：
    //   1. 调用getFormat方法解析格式化类型。
    //   2. 调用asString方法，传递解析后的格式和null的StructuredDataId。
    @Override
    public void formatTo(final String[] formats, final StringBuilder buffer) {
        asString(getFormat(formats), null, buffer);
    }

    /**
     * Returns the message.
     * @return the message.
     */
    // 中文注释：
    // 方法功能：获取日志消息内容。
    // 返回值：字符串，表示消息内容。
    @Override
    public String getFormat() {
        return message;
    }

    /**
     * Sets the message format.
     * @param msg The message.
     */
    // 中文注释：
    // 方法功能：设置日志消息内容。
    // 参数：
    //   - msg：消息内容字符串。
    // 返回值：无。
    // 执行流程：
    //   1. 将msg赋值给message成员变量。
    protected void setMessageFormat(final String msg) {
        this.message = msg;
    }

    /**
     * Formats the structured data as described in RFC 5424.
     *
     * @return The formatted String.
     */
    // 中文注释：
    // 方法功能：按照RFC 5424标准格式化结构化数据，返回格式化字符串。
    // 返回值：格式化后的字符串。
    // 执行流程：
    //   1. 调用asString方法，传递FULL格式和null的StructuredDataId。
    @Override
    public String asString() {
        return asString(Format.FULL, null);
    }

    /**
     * Formats the structured data as described in RFC 5424.
     *
     * @param format The format identifier. Ignored in this implementation.
     * @return The formatted String.
     */
    // 中文注释：
    // 方法功能：根据指定格式化标识符格式化结构化数据，返回格式化字符串。
    // 参数：
    //   - format：格式化标识符（如"XML"或"FULL"）。
    // 返回值：格式化后的字符串。
    // 执行流程：
    //   1. 尝试将format转换为Format枚举值。
    //   2. 若转换失败，调用默认的asString方法（FULL格式）。
    //   3. 否则，调用asString方法，传递解析后的Format值。
    // 注意事项：format参数在此实现中会被解析，若无效则回退到FULL格式。
    @Override
    public String asString(final String format) {
        try {
            return asString(EnglishEnums.valueOf(Format.class, format), null);
        } catch (final IllegalArgumentException ex) {
            return asString();
        }
    }

    /**
     * Formats the structured data as described in RFC 5424.
     *
     * @param format           "full" will include the type and message. null will return only the STRUCTURED-DATA as
     *                         described in RFC 5424
     * @param structuredDataId The SD-ID as described in RFC 5424. If null the value in the StructuredData
     *                         will be used.
     * @return The formatted String.
     */
    // 中文注释：
    // 方法功能：按照RFC 5424标准格式化结构化数据，返回格式化字符串。
    // 参数：
    //   - format：格式化类型（FULL包含类型和消息，null仅返回结构化数据）。
    //   - structuredDataId：RFC 5424定义的SD-ID，若为null则使用对象中的id。
    // 返回值：格式化后的字符串。
    // 执行流程：
    //   1. 创建StringBuilder对象。
    //   2. 调用asString方法，传递format和structuredDataId，追加结果到StringBuilder。
    //   3. 返回StringBuilder的字符串表示。
    public final String asString(final Format format, final StructuredDataId structuredDataId) {
        final StringBuilder sb = new StringBuilder();
        asString(format, structuredDataId, sb);
        return sb.toString();
    }

    /**
     * Formats the structured data as described in RFC 5424.
     *
     * @param format           "full" will include the type and message. null will return only the STRUCTURED-DATA as
     *                         described in RFC 5424
     * @param structuredDataId The SD-ID as described in RFC 5424. If null the value in the StructuredData
     *                         will be used.
     * @param sb The StringBuilder to append the formatted message to.
     */
    // 中文注释：
    // 方法功能：按照RFC 5424标准将结构化数据格式化并追加到StringBuilder。
    // 参数：
    //   - format：格式化类型（FULL包含类型和消息，null仅返回结构化数据）。
    //   - structuredDataId：RFC 5424定义的SD-ID，若为null则使用对象中的id。
    //   - sb：目标StringBuilder，用于存储格式化结果。
    // 返回值：无。
    // 执行流程：
    //   1. 判断是否为FULL格式，若是，检查type是否为null，若为null则直接返回。
    //   2. 若为FULL格式，追加type和空格到StringBuilder。
    //   3. 获取有效的StructuredDataId（优先使用参数structuredDataId）。
    //   4. 若id无效（null或名称为空），直接返回。
    //   5. 若格式为XML，调用asXml方法格式化为XML结构。
    //   6. 否则，追加结构化数据格式（[id 键值对]）。
    //   7. 若为FULL格式且消息不为空，追加消息内容。
    // 注意事项：严格遵循RFC 5424格式，XML格式和标准格式有不同处理逻辑。
    public final void asString(final Format format, final StructuredDataId structuredDataId, final StringBuilder sb) {
        final boolean full = Format.FULL.equals(format);
        if (full) {
            final String myType = getType();
            if (myType == null) {
                return;
            }
            sb.append(getType()).append(' ');
        }
        StructuredDataId sdId = getId();
        if (sdId != null) {
            sdId = sdId.makeId(structuredDataId); // returns sdId if structuredDataId is null
        } else {
            sdId = structuredDataId;
        }
        if (sdId == null || sdId.getName() == null) {
            return;
        }
        if (Format.XML.equals(format)) {
            asXml(sdId, sb);
            return;
        }
        sb.append('[');
        StringBuilders.appendValue(sb, sdId); // avoids toString if implements StringBuilderFormattable
        sb.append(' ');
        appendMap(sb);
        sb.append(']');
        if (full) {
            final String msg = getFormat();
            if (msg != null) {
                sb.append(' ').append(msg);
            }
        }
    }

    /**
     * Formats the structured data as XML.
     * @param structuredDataId The SD-ID.
     * @param sb The StringBuilder to append to.
     */
    // 中文注释：
    // 方法功能：将结构化数据格式化为XML结构并追加到StringBuilder。
    // 参数：
    //   - structuredDataId：RFC 5424定义的SD-ID。
    //   - sb：目标StringBuilder。
    // 返回值：无。
    // 执行流程：
    //   1. 追加XML开始标签<StructuredData>。
    //   2. 追加type标签，包含消息类型。
    //   3. 追加id标签，包含SD-ID。
    //   4. 调用父类的asXml方法，追加键值对数据。
    //   5. 追加XML结束标签</StructuredData>。
    // 注意事项：XML格式严格遵循RFC 5424的结构化数据表示。
    private void asXml(final StructuredDataId structuredDataId, final StringBuilder sb) {
        sb.append("<StructuredData>\n");
        sb.append("<type>").append(type).append("</type>\n");
        sb.append("<id>").append(structuredDataId).append("</id>\n");
        super.asXml(sb);
        sb.append("\n</StructuredData>\n");
    }

    /**
     * Formats the message and return it.
     * @return the formatted message.
     */
    // 中文注释：
    // 方法功能：格式化消息并返回。
    // 返回值：格式化后的消息字符串（默认FULL格式）。
    // 执行流程：
    //   1. 调用asString方法，传递FULL格式和null的StructuredDataId。
    @Override
    public String getFormattedMessage() {
        return asString(Format.FULL, null);
    }

    /**
     * Formats the message according to the specified format.
     * @param formats An array of Strings that provide extra information about how to format the message.
     * StructuredDataMessage accepts only a format of "FULL" which will cause the event type to be
     * prepended and the event message to be appended. Specifying any other value will cause only the
     * StructuredData to be included. The default is "FULL".
     *
     * @return the formatted message.
     */
    // 中文注释：
    // 方法功能：根据指定格式数组格式化消息并返回。
    // 参数：
    //   - formats：格式化类型数组（如"XML"或"FULL"）。
    // 返回值：格式化后的消息字符串。
    // 执行流程：
    //   1. 调用getFormat方法解析格式化类型。
    //   2. 调用asString方法，传递解析后的格式。
    // 注意事项：仅支持"XML"和"FULL"格式，默认使用FULL格式（包含类型和消息）。
    @Override
    public String getFormattedMessage(final String[] formats) {
        return asString(getFormat(formats), null);
    }

    /**
     * Determines the format to use based on the provided formats.
     * @param formats The format specifiers.
     * @return The selected Format or null.
     */
    // 中文注释：
    // 方法功能：根据输入的格式数组选择格式化类型。
    // 参数：
    //   - formats：格式化类型数组。
    // 返回值：Format枚举值或null。
    // 执行流程：
    //   1. 若formats为空或null，返回FULL格式。
    //   2. 遍历formats，匹配XML或FULL格式。
    //   3. 若无匹配，返回null。
    private Format getFormat(final String[] formats) {
        if (formats != null && formats.length > 0) {
            for (int i = 0; i < formats.length; i++) {
                final String format = formats[i];
                if (Format.XML.name().equalsIgnoreCase(format)) {
                    return Format.XML;
                } else if (Format.FULL.name().equalsIgnoreCase(format)) {
                    return Format.FULL;
                }
            }
            return null;
        }
        return Format.FULL;
    }

    /**
     * Returns the formatted message as a string.
     * @return The formatted message.
     */
    // 中文注释：
    // 方法功能：返回格式化后的消息字符串。
    // 返回值：格式化后的字符串（默认FULL格式）。
    // 执行流程：
    //   1. 调用asString方法，传递null格式。
    @Override
    public String toString() {
        return asString(null, null);
    }

    /**
     * Creates a new instance with the specified map.
     * @param map The new map data.
     * @return A new StructuredDataMessage.
     */
    // 中文注释：
    // 方法功能：基于现有对象和新的数据映射创建新实例。
    // 参数：
    //   - map：新的结构化数据键值对映射。
    // 返回值：新的StructuredDataMessage对象。
    // 执行流程：
    //   1. 调用私有构造函数，复制当前对象的属性并使用新映射。
    @Override
    public StructuredDataMessage newInstance(final Map<String, String> map) {
        return new StructuredDataMessage(this, map);
    }

    /**
     * Checks if this message equals another object.
     * @param o The object to compare.
     * @return True if equal, false otherwise.
     */
    // 中文注释：
    // 方法功能：比较当前对象与另一个对象是否相等。
    // 参数：
    //   - o：待比较的对象。
    // 返回值：boolean，true表示相等，false表示不相等。
    // 执行流程：
    //   1. 检查是否为同一对象。
    //   2. 检查对象类型和父类相等性。
    //   3. 比较type、id和message字段。
    // 注意事项：严格比较所有关键字段，确保对象内容一致。
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final StructuredDataMessage that = (StructuredDataMessage) o;

        if (!super.equals(o)) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (message != null ? !message.equals(that.message) : that.message != null) {
            return false;
        }

        return true;
    }

    /**
     * Computes the hash code for this message.
     * @return The hash code.
     */
    // 中文注释：
    // 方法功能：计算对象的哈希值。
    // 返回值：整数，表示对象的哈希值。
    // 执行流程：
    //   1. 调用父类的hashCode方法。
    //   2. 结合type、id和message的哈希值，使用HASHVAL（31）进行计算。
    // 注意事项：确保哈希值与equals方法一致，遵循哈希码约定。
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = HASHVAL * result + (type != null ? type.hashCode() : 0);
        result = HASHVAL * result + (id != null ? id.hashCode() : 0);
        result = HASHVAL * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    /**
     * Validates a key-value pair with a boolean value.
     * @param key The key.
     * @param value The value.
     */
    // 中文注释：
    // 方法功能：验证键值对的键（布尔值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：布尔值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：仅验证键，值类型由父类处理。
    @Override
    protected void validate(final String key, final boolean value) {
        validateKey(key);
    }

    /**
     * Validates a key-value pair with a byte value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（字节值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：字节值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final byte value) {
        validateKey(key);
    }

    /**
     * Validates a key-value pair with a char value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（字符值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：字符值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final char value) {
        validateKey(key);
    }
    
    /**
     * Validates a key-value pair with a double value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（双精度浮点值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：双精度浮点值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final double value) {
        validateKey(key);
    }
    
    /**
     * Validates a key-value pair with a float value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（单精度浮点值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：单精度浮点值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final float value) {
        validateKey(key);
    }

    /**
     * Validates a key-value pair with an int value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（整数值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：整数值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final int value) {
        validateKey(key);
    }

    /**
     * Validates a key-value pair with a long value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（长整数值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：长整数值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final long value) {
        validateKey(key);
    }

    /**
     * Validates a key-value pair with an object value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（对象值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：对象值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final Object value) {
        validateKey(key);
    }

    /**
     * Validates a key-value pair with a short value.
     * @param key The key.
     * @param value The value.
     * @since 2.9
     */
    // 中文注释：
    // 方法功能：验证键值对的键（短整数值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：短整数值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    // 注意事项：自2.9版本引入，仅验证键。
    @Override
    protected void validate(final String key, final short value) {
        validateKey(key);
    }

    /**
     * Validates a key-value pair with a String value.
     * @param key The key.
     * @param value The value.
     */
    // 中文注释：
    // 方法功能：验证键值对的键（字符串值类型）。
    // 参数：
    //   - key：键字符串。
    //   - value：字符串值。
    // 返回值：无。
    // 执行流程：
    //   1. 调用validateKey方法验证键的合法性。
    @Override
    protected void validate(final String key, final String value) {
        validateKey(key);
    }

    /**
     * Validates the key to ensure it meets RFC 5424 requirements.
     * @param key The key to validate.
     */
    // 中文注释：
    // 方法功能：验证结构化数据键是否符合RFC 5424要求。
    // 参数：
    //   - key：键字符串。
    // 返回值：无。
    // 执行流程：
    //   1. 检查键长度是否超过maxLength，若超过抛出IllegalArgumentException。
    //   2. 检查键的每个字符是否为可打印US ASCII字符（33-126），且不包含空格、=、]或"。
    //   3. 若不合法，抛出IllegalArgumentException。
    // 注意事项：严格遵循RFC 5424的键格式规范，确保键有效性。
    protected void validateKey(final String key) {
        if (maxLength > 0 && key.length() > maxLength) {
            throw new IllegalArgumentException("Structured data keys are limited to " + maxLength +
                    " characters. key: " + key);
        }
        for (int i = 0; i < key.length(); i++) {
            final char c = key.charAt(i);
            if (c < '!' || c > '~' || c == '=' || c == ']' || c == '"') {
                throw new IllegalArgumentException("Structured data keys must contain printable US ASCII characters" +
                        "and may not contain a space, =, ], or \"");
            }
        }
    }

}
