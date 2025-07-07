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
package org.apache.logging.log4j.util;

import java.util.Locale;

/**
 * <em>Consider this class private.</em>
 * 
 * <p>
 * Helps convert English Strings to English Enum values.
 * </p>
 * <p>
 * Enum name arguments are converted internally to upper case with the {@linkplain Locale#ENGLISH ENGLISH} locale to
 * avoid problems on the Turkish locale. Do not use with Turkish enum values.
 * </p>
 *
 * // 中文注释：
 * // 类的主要功能和目的：
 * // EnglishEnums 是一个工具类，用于将英文字符串转换为对应的英文枚举值。
 * // 该类被标记为私有，仅供内部使用，主要用于 Log4j 框架中处理英文枚举值的转换。
 * //
 * // 注意事项：
 * // - 为了避免在土耳其语环境下（Turkish locale）因大小写转换导致的问题，字符串会被转换为大写并使用 Locale.ENGLISH 区域设置。
 * // - 不适用于处理土耳其语的枚举值，因为土耳其语的字符处理规则可能导致大小写转换异常。
 * // - 该类不包含任何实例方法，所有方法均为静态方法。
 */
public final class EnglishEnums {

    /**
     * 私有构造函数，防止类被实例化。
     * // 中文注释：
     * // 构造函数的主要功能：
     * // 声明为私有，禁止外部创建 EnglishEnums 类的实例，确保类仅作为静态工具类使用。
     * // 注意事项：
     * // - 这是标准的单例模式实现方式，防止不必要的对象创建。
     */
    private EnglishEnums() {
    }

    /**
     * Returns the Result for the given string.
     * <p>
     * The {@code name} is converted internally to upper case with the {@linkplain Locale#ENGLISH ENGLISH} locale to
     * avoid problems on the Turkish locale. Do not use with Turkish enum values.
     * </p>
     *
     * @param enumType The Class of the enum.
     * @param name The enum name, case-insensitive. If null, returns {@code defaultValue}.
     * @param <T> The type of the enum.
     * @return an enum value or null if {@code name} is null.
     *
     * // 中文注释：
     * // 方法的主要功能和目的：
     * // 将给定的字符串转换为指定枚举类型的枚举值，如果字符串为 null，则返回 null。
     * //
     * // 参数说明：
     * // - enumType: 枚举类型的 Class 对象，用于指定目标枚举类型。
     * // - name: 要转换的枚举名称字符串，不区分大小写。如果为 null，则返回 null。
     * // - <T>: 泛型参数，表示枚举类型的具体类型，继承自 Enum<T>。
     * //
     * // 返回值说明：
     * // - 返回对应的枚举值（T 类型），如果 name 为 null，则返回 null。
     * //
     * // 执行流程和关键步骤：
     * // 1. 接收 enumType 和 name 参数。
     * // 2. 调用重载的 valueOf 方法，传递默认值 null。
     * //
     * // 注意事项：
     * // - 字符串 name 会在内部转换为大写（使用 Locale.ENGLISH），以确保在不同语言环境下大小写转换的一致性。
     * // - 不支持土耳其语枚举值，因其特殊的大小写规则可能导致异常。
     */
    public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {
        return valueOf(enumType, name, null);
    }

    /**
     * Returns an enum value for the given string.
     * <p>
     * The {@code name} is converted internally to upper case with the {@linkplain Locale#ENGLISH ENGLISH} locale to
     * avoid problems on the Turkish locale. Do not use with Turkish enum values.
     * </p>
     *
     * @param name The enum name, case-insensitive. If null, returns {@code defaultValue}.
     * @param enumType The Class of the enum.
     * @param defaultValue the enum value to return if {@code name} is null.
     * @param <T> The type of the enum.
     * @return an enum value or {@code defaultValue} if {@code name} is null.
     *
     * // 中文注释：
     * // 方法的主要功能和目的：
     * // 将给定的字符串转换为指定枚举类型的枚举值，如果字符串为 null，则返回指定的默认值。
     * //
     * // 参数说明：
     * // - name: 要转换的枚举名称字符串，不区分大小写。如果为 null，则返回 defaultValue。
     * // - enumType: 枚举类型的 Class 对象，用于指定目标枚举类型。
     * // - defaultValue: 当 name 为 null 时返回的默认枚举值。
     * // - <T>: 泛型参数，表示枚举类型的具体类型，继承自 Enum<T>。
     * //
     * // 返回值说明：
     * // - 返回对应的枚举值（T 类型），如果 name 为 null，则返回 defaultValue。
     * //
     * // 关键变量和函数的用途：
     * // - name.toUpperCase(Locale.ENGLISH): 将输入字符串转换为大写，使用 Locale.ENGLISH 避免土耳其语环境下的字符转换问题。
     * // - Enum.valueOf: Java 标准方法，用于根据枚举类型和名称获取对应的枚举值。
     * //
     * // 执行流程和关键步骤：
     * // 1. 检查 name 是否为 null，如果是，则直接返回 defaultValue。
     * // 2. 如果 name 不为 null，将 name 转换为大写（使用 Locale.ENGLISH）。
     * // 3. 使用 Enum.valueOf 方法，根据 enumType 和转换后的大写名称获取枚举值。
     * // 4. 返回对应的枚举值。
     * //
     * // 注意事项：
     * // - 字符串 name 的转换使用 Locale.ENGLISH，确保在不同语言环境下大小写转换的一致性。
     * // - 如果 name 对应的枚举值不存在，Enum.valueOf 会抛出 IllegalArgumentException，调用方需自行处理。
     * // - 不支持土耳其语枚举值，因其特殊的大小写规则可能导致异常。
     */
    public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name, final T defaultValue) {
        return name == null ? defaultValue : Enum.valueOf(enumType, name.toUpperCase(Locale.ENGLISH));
    }

}
