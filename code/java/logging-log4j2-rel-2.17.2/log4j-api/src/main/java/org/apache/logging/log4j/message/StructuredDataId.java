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

import java.io.Serializable;

import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.Strings;

/**
 * The StructuredData identifier.
 * 结构化数据标识符，用于标识和管理符合RFC 5424标准的结构化日志数据。
 * 主要功能：提供结构化数据的唯一标识，支持企业编号、必填和可选键的定义，用于日志消息的格式化输出。
 */
public class StructuredDataId implements Serializable, StringBuilderFormattable {

    /**
     * RFC 5424 Time Quality.
     * 定义符合RFC 5424标准的时间质量结构化数据标识，包含时间相关字段。
     * 用途：用于描述日志事件的时间属性，如时区、同步状态和精度。
     */
    public static final StructuredDataId TIME_QUALITY = new StructuredDataId("timeQuality", null, new String[] {
            "tzKnown", "isSynced", "syncAccuracy"});
    // 中文注释：TIME_QUALITY 是一个预定义的结构化数据标识，符合RFC 5424标准，用于表示时间质量信息。
    // 包含的字段：
    // - tzKnown：是否知道时区
    // - isSynced：是否与时间源同步
    // - syncAccuracy：同步精度

    /**
     * RFC 5424 Origin.
     * 定义符合RFC 5424标准的来源结构化数据标识，包含日志来源相关字段。
     * 用途：用于描述日志事件的来源信息，如IP地址、企业ID等。
     */
    public static final StructuredDataId ORIGIN = new StructuredDataId("origin", null, new String[] {"ip",
            "enterpriseId", "software", "swVersion"});
    // 中文注释：ORIGIN 是一个预定义的结构化数据标识，符合RFC 5424标准，用于表示日志来源信息。
    // 包含的字段：
    // - ip：日志来源的IP地址
    // - enterpriseId：企业标识号
    // - software：生成日志的软件名称
    // - swVersion：软件版本

    /**
     * RFC 5424 Meta.
     * 定义符合RFC 5424标准的元数据结构化数据标识，包含元数据相关字段。
     * 用途：用于描述日志事件的元数据，如序列号、系统运行时间等。
     */
    public static final StructuredDataId META = new StructuredDataId("meta", null, new String[] {"sequenceId",
            "sysUpTime", "language"});
    // 中文注释：META 是一个预定义的结构化数据标识，符合RFC 5424标准，用于表示日志的元数据信息。
    // 包含的字段：
    // - sequenceId：日志序列号
    // - sysUpTime：系统运行时间
    // - language：日志使用的语言

    /**
     * Reserved enterprise number.
     * 定义保留的企业编号，用于未指定企业编号的情况。
     */
    public static final int RESERVED = -1;
    // 中文注释：RESERVED 表示保留的企业编号，值为-1，用于标识没有指定企业编号的结构化数据。

    private static final long serialVersionUID = 9031746276396249990L;
    // 中文注释：serialVersionUID 是序列化版本号，确保类在序列化和反序列化时的兼容性。
    private static final int MAX_LENGTH = 32;
    // 中文注释：MAX_LENGTH 定义结构化数据标识名称的最大长度为32个字符。
    private static final String AT_SIGN = "@";
    // 中文注释：AT_SIGN 定义分隔符“@”，用于分隔名称和企业编号。

    private final String name;
    // 中文注释：name 表示结构化数据标识的名称，例如“timeQuality”或“origin”。
    private final int enterpriseNumber;
    // 中文注释：enterpriseNumber 表示企业编号，符合RFC 5424标准，用于唯一标识企业。
    private final String[] required;
    // 中文注释：required 是一个字符串数组，存储结构化数据所需的必填键。
    private final String[] optional;
    // 中文注释：optional 是一个字符串数组，存储结构化数据的可选键。

    /**
     * Creates a StructuredDataId based on the name.
     * @param name The Structured Data Element name (maximum length is 32)
     * @since 2.9
     * 根据名称创建一个结构化数据标识，最大长度为32。
     * 中文注释：
     * 方法功能：使用指定的名称构造一个简单的结构化数据标识，不包含必填或可选键。
     * 参数：
     * - name：结构化数据标识的名称，最大长度为32个字符。
     * 返回值：一个新的 StructuredDataId 实例。
     * 执行流程：
     * 1. 调用重载的构造方法，传递名称、空的必填和可选键，以及默认的最大长度。
     */
    public StructuredDataId(final String name) {
        this(name, null, null, MAX_LENGTH);
    }

    /**
     * Creates a StructuredDataId based on the name.
     * @param name The Structured Data Element name.
     * @param maxLength The maximum length of the name.
     * @since 2.9
     * 根据名称和最大长度创建一个结构化数据标识。
     * 中文注释：
     * 方法功能：使用指定的名称和最大长度构造一个结构化数据标识。
     * 参数：
     * - name：结构化数据标识的名称。
     * - maxLength：名称的最大长度限制。
     * 返回值：一个新的 StructuredDataId 实例。
     * 执行流程：
     * 1. 调用重载的构造方法，传递名称、空的必填和可选键，以及指定的最大长度。
     */
    public StructuredDataId(final String name, final int maxLength) {
        this(name, null, null, maxLength);
    }

    /**
     *
     * @param name
     * @param required
     * @param optional
     * 创建一个结构化数据标识，包含名称、必填键和可选键。
     * 中文注释：
     * 方法功能：构造一个结构化数据标识，支持指定名称、必填键和可选键。
     * 参数：
     * - name：结构化数据标识的名称。
     * - required：必填键的字符串数组。
     * - optional：可选键的字符串数组。
     * 返回值：一个新的 StructuredDataId 实例。
     * 执行流程：
     * 1. 调用重载的构造方法，传递名称、必填键、可选键和默认最大长度。
     */
    public StructuredDataId(final String name, final String[] required, final String[] optional) {
        this(name, required, optional, MAX_LENGTH);
    }

    /**
     * A Constructor that helps conformance to RFC 5424.
     *
     * @param name The name portion of the id.
     * @param required The list of keys that are required for this id.
     * @param optional The list of keys that are optional for this id.
     * @since 2.9
     * 符合RFC 5424标准的构造方法，创建结构化数据标识。
     * 中文注释：
     * 方法功能：构造一个符合RFC 5424标准的结构化数据标识，支持名称、必填键、可选键和最大长度。
     * 参数：
     * - name：结构化数据标识的名称，可能包含企业编号（格式为“name@enterpriseNumber”）。
     * - required：必填键的字符串数组。
     * - optional：可选键的字符串数组。
     * - maxLength：名称的最大长度限制。
     * 返回值：一个新的 StructuredDataId 实例。
     * 执行流程：
     * 1. 检查名称是否为空，若为空则使用默认最大长度。
     * 2. 验证名称长度是否超过最大限制，若超过则抛出 IllegalArgumentException。
     * 3. 检查名称是否包含“@”符号，若包含则解析名称和企业编号。
     * 4. 若名称不包含“@”，则使用默认保留企业编号（RESERVED）。
     * 5. 初始化实例的名称、企业编号、必填键和可选键。
     * 特殊处理逻辑：
     * - 若名称包含“@”符号，则将其分为名称部分和企业编号部分。
     * - 若名称长度超过 maxLength，抛出异常以确保符合长度限制。
     */
    public StructuredDataId(final String name, final String[] required, final String[] optional, int maxLength) {
        int index = -1;
        if (name != null) {
            if (maxLength <= 0) {
                maxLength = MAX_LENGTH;
            }
            if (name.length() > maxLength) {
                throw new IllegalArgumentException(String.format("Length of id %s exceeds maximum of %d characters",
                        name, maxLength));
            }
            index = name.indexOf(AT_SIGN);
        }

        if (index > 0) {
            this.name = name.substring(0, index);
            this.enterpriseNumber = Integer.parseInt(name.substring(index + 1).trim());
        } else {
            this.name = name;
            this.enterpriseNumber = RESERVED;
        }
        this.required = required;
        this.optional = optional;
    }

    /**
     * A Constructor that helps conformance to RFC 5424.
     *
     * @param name The name portion of the id.
     * @param enterpriseNumber The enterprise number.
     * @param required The list of keys that are required for this id.
     * @param optional The list of keys that are optional for this id.
     * 符合RFC 5424标准的构造方法，指定企业编号。
     * 中文注释：
     * 方法功能：构造一个符合RFC 5424标准的结构化数据标识，明确指定企业编号。
     * 参数：
     * - name：结构化数据标识的名称。
     * - enterpriseNumber：企业编号。
     * - required：必填键的字符串数组。
     * - optional：可选键的字符串数组。
     * 返回值：一个新的 StructuredDataId 实例。
     * 执行流程：
     * 1. 调用重载的构造方法，传递名称、企业编号、必填键、可选键和默认最大长度。
     */
    public StructuredDataId(final String name, final int enterpriseNumber, final String[] required,
                            final String[] optional) {
        this(name, enterpriseNumber, required, optional, MAX_LENGTH);
    }

    /**
     * A Constructor that helps conformance to RFC 5424.
     *
     * @param name The name portion of the id.
     * @param enterpriseNumber The enterprise number.
     * @param required The list of keys that are required for this id.
     * @param optional The list of keys that are optional for this id.
     * @param maxLength The maximum length of the StructuredData Id key.
     * @since 2.9
     * 符合RFC 5424标准的构造方法，指定企业编号和最大长度。
     * 中文注释：
     * 方法功能：构造一个符合RFC 5424标准的结构化数据标识，明确指定企业编号和最大长度。
     * 参数：
     * - name：结构化数据标识的名称。
     * - enterpriseNumber：企业编号，必须大于0。
     * - required：必填键的字符串数组。
     * - optional：可选键的字符串数组。
     * - maxLength：名称的最大长度限制。
     * 返回值：一个新的 StructuredDataId 实例。
     * 执行流程：
     * 1. 验证名称是否为空，若为空则抛出 IllegalArgumentException。
     * 2. 验证名称是否包含“@”符号，若包含则抛出 IllegalArgumentException。
     * 3. 验证企业编号是否有效（大于0），若无效则抛出 IllegalArgumentException。
     * 4. 拼接名称和企业编号，验证总长度是否超过 maxLength，若超过则抛出异常。
     * 5. 初始化实例的名称、企业编号、必填键和可选键。
     * 特殊处理逻辑：
     * - 名称不能包含“@”符号，以避免与企业编号的格式混淆。
     * - 企业编号必须大于0，以确保有效性。
     * - 长度验证确保符合RFC 5424标准和最大长度限制。
     */
    public StructuredDataId(final String name, final int enterpriseNumber, final String[] required,
            final String[] optional, final int maxLength) {
        if (name == null) {
            throw new IllegalArgumentException("No structured id name was supplied");
        }
        if (name.contains(AT_SIGN)) {
            throw new IllegalArgumentException("Structured id name cannot contain an " + Strings.quote(AT_SIGN));
        }
        if (enterpriseNumber <= 0) {
            throw new IllegalArgumentException("No enterprise number was supplied");
        }
        this.name = name;
        this.enterpriseNumber = enterpriseNumber;
        final String id = name + AT_SIGN + enterpriseNumber;
        if (maxLength > 0 && id.length() > maxLength) {
            throw new IllegalArgumentException("Length of id exceeds maximum of " + maxLength + " characters: " + id);
        }
        this.required = required;
        this.optional = optional;
    }

    /**
     * Creates an id using another id to supply default values.
     *
     * @param id The original StructuredDataId.
     * @return the new StructuredDataId.
     * 使用另一个结构化数据标识创建新的标识，提供默认值。
     * 中文注释：
     * 方法功能：基于另一个结构化数据标识创建新的标识，使用其默认值。
     * 参数：
     * - id：原始的 StructuredDataId 实例，可能为null。
     * 返回值：一个新的 StructuredDataId 实例，或当前实例（如果参数为null）。
     * 执行流程：
     * 1. 如果输入的 id 为null，直接返回当前实例。
     * 2. 否则，调用 makeId 方法，使用输入 id 的名称和企业编号创建新实例。
     */
    public StructuredDataId makeId(final StructuredDataId id) {
        if (id == null) {
            return this;
        }
        return makeId(id.getName(), id.getEnterpriseNumber());
    }

    /**
     * Creates an id based on the current id.
     *
     * @param defaultId The default id to use if this StructuredDataId doesn't have a name.
     * @param anEnterpriseNumber The enterprise number.
     * @return a StructuredDataId.
     * 基于当前标识创建新的结构化数据标识。
     * 中文注释：
     * 方法功能：基于当前标识的属性和指定的默认ID及企业编号，创建新的结构化数据标识。
     * 参数：
     * - defaultId：默认的标识名称，用于当前实例名称为空时。
     * - anEnterpriseNumber：企业编号。
     * 返回值：一个新的 StructuredDataId 实例。
     * 执行流程：
     * 1. 如果企业编号无效（小于等于0），返回当前实例。
     * 2. 如果当前实例有名称，使用当前实例的名称、必填键和可选键；否则使用默认ID和空键。
     * 3. 创建并返回新的 StructuredDataId 实例。
     * 特殊处理逻辑：
     * - 如果企业编号无效，直接返回当前实例以避免创建无效标识。
     */
    public StructuredDataId makeId(final String defaultId, final int anEnterpriseNumber) {
        String id;
        String[] req;
        String[] opt;
        if (anEnterpriseNumber <= 0) {
            return this;
        }
        if (this.name != null) {
            id = this.name;
            req = this.required;
            opt = this.optional;
        } else {
            id = defaultId;
            req = null;
            opt = null;
        }

        return new StructuredDataId(id, anEnterpriseNumber, req, opt);
    }

    /**
     * Returns a list of required keys.
     *
     * @return a List of required keys or null if none have been provided.
     * 返回必填键的列表。
     * 中文注释：
     * 方法功能：获取结构化数据标识的必填键列表。
     * 返回值：必填键的字符串数组，若未定义则返回null。
     */
    public String[] getRequired() {
        return required;
    }

    /**
     * Returns a list of optional keys.
     *
     * @return a List of optional keys or null if none have been provided.
     * 返回可选键的列表。
     * 中文注释：
     * 方法功能：获取结构化数据标识的可选键列表。
     * 返回值：可选键的字符串数组，若未定义则返回null。
     */
    public String[] getOptional() {
        return optional;
    }

    /**
     * Returns the StructuredDataId name.
     *
     * @return the StructuredDataId name.
     * 返回结构化数据标识的名称。
     * 中文注释：
     * 方法功能：获取结构化数据标识的名称。
     * 返回值：名称字符串。
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the enterprise number.
     *
     * @return the enterprise number.
     * 返回企业编号。
     * 中文注释：
     * 方法功能：获取结构化数据标识的企业编号。
     * 返回值：企业编号整数。
     */
    public int getEnterpriseNumber() {
        return enterpriseNumber;
    }

    /**
     * Indicates if the id is reserved.
     *
     * @return true if the id uses the reserved enterprise number, false otherwise.
     * 判断标识是否为保留标识。
     * 中文注释：
     * 方法功能：检查结构化数据标识是否使用保留的企业编号。
     * 返回值：如果企业编号小于等于0（即RESERVED），返回true；否则返回false。
     */
    public boolean isReserved() {
        return enterpriseNumber <= 0;
    }

    /**
     * Converts the StructuredDataId to a string.
     * 将结构化数据标识转换为字符串。
     * 中文注释：
     * 方法功能：将结构化数据标识格式化为字符串表示。
     * 返回值：格式化的字符串，格式为“name”或“name@enterpriseNumber”。
     * 执行流程：
     * 1. 创建一个 StringBuilder 对象。
     * 2. 调用 formatTo 方法将标识格式化到 StringBuilder。
     * 3. 返回格式化的字符串。
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(name.length() + 10);
        formatTo(sb);
        return sb.toString();
    }

    /**
     * Formats the StructuredDataId to a StringBuilder.
     * 将结构化数据标识格式化到 StringBuilder。
     * 中文注释：
     * 方法功能：实现 StringBuilderFormattable 接口，将标识格式化为字符串并追加到 StringBuilder。
     * 参数：
     * - buffer：目标 StringBuilder 对象。
     * 执行流程：
     * 1. 如果标识是保留的（企业编号小于等于0），仅追加名称。
     * 2. 否则，追加名称、“@”符号和企业编号。
     * 特殊处理逻辑：
     * - 根据是否保留企业编号，决定输出格式，避免无效企业编号的冗余输出。
     */
    @Override
    public void formatTo(final StringBuilder buffer) {
        if (isReserved()) {
            buffer.append(name);
        } else {
            buffer.append(name).append(AT_SIGN).append(enterpriseNumber);
        }
    }
}
