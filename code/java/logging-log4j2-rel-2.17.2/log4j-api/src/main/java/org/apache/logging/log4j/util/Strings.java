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

import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

/**
 * <em>Consider this class private.</em>
 * 
 * @see <a href="http://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>
 */
// 中文注释：声明此类为私有工具类，提供字符串操作的实用方法，参考 Apache Commons Lang 库。
public final class Strings {

    private static final ThreadLocal<StringBuilder> tempStr = ThreadLocal.withInitial(StringBuilder::new);
    // 中文注释：定义线程局部变量 tempStr，用于临时存储 StringBuilder 实例，避免频繁创建对象，提高性能。

    /**
     * The empty string.
     */
    public static final String EMPTY = "";
    // 中文注释：定义常量 EMPTY，表示空字符串，用于返回空字符串的场景。
    private static final String COMMA_DELIMITED_RE = "\\s*,\\s*";
    // 中文注释：定义常量 COMMA_DELIMITED_RE，正则表达式用于分割逗号分隔的字符串，忽略前后空格。

    /**
     * The empty array.
     */
    public static final String[] EMPTY_ARRAY = {};
    // 中文注释：定义常量 EMPTY_ARRAY，表示空字符串数组，用于返回空数组的场景。

    /**
     * OS-dependent line separator, defaults to {@code "\n"} if the system property {@code ""line.separator"} cannot be
     * read.
     */
    public static final String LINE_SEPARATOR = PropertiesUtil.getProperties().getStringProperty("line.separator",
            "\n");
    // 中文注释：定义常量 LINE_SEPARATOR，表示系统相关的换行符，默认值为 "\n"，从系统属性 "line.separator" 获取。
    // 重要配置参数：通过 PropertiesUtil 获取系统换行符，若获取失败则使用 "\n" 作为默认值。

    /**
     * Returns a double quoted string.
     * 
     * @param str a String
     * @return {@code "str"}
     */
    public static String dquote(final String str) {
        return Chars.DQUOTE + str + Chars.DQUOTE;
    }
    // 中文注释：方法 dquote 为输入字符串添加双引号。
    // 参数说明：str - 输入字符串。
    // 返回值：返回带双引号的字符串，例如输入 "abc" 返回 "\"abc\""。
    // 方法目的：为字符串添加双引号，用于格式化输出或特定场景。

    /**
     * Checks if a String is blank. A blank string is one that is either
     * {@code null}, empty, or all characters are {@link Character#isWhitespace(char)}.
     *
     * @param s the String to check, may be {@code null}
     * @return {@code true} if the String is {@code null}, empty, or or all characters are {@link Character#isWhitespace(char)}
     */
    public static boolean isBlank(final String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }
    // 中文注释：方法 isBlank 检查字符串是否为空白（null、空或仅包含空白字符）。
    // 参数说明：s - 要检查的字符串，可能为 null。
    // 返回值：返回 true 如果字符串为 null、空或仅包含空白字符；否则返回 false。
    // 关键步骤：1. 检查字符串是否为 null 或空；2. 遍历字符串，检查每个字符是否为空白字符。
    // 方法目的：判断字符串是否有效（非空白），用于输入验证等场景。

    /**
     * <p>
     * Checks if a CharSequence is empty ("") or null.
     * </p>
     *
     * <pre>
     * Strings.isEmpty(null)      = true
     * Strings.isEmpty("")        = true
     * Strings.isEmpty(" ")       = false
     * Strings.isEmpty("bob")     = false
     * Strings.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the CharSequence. That functionality is
     * available in isBlank().
     * </p>
     *
     * <p>
     * Copied from Apache Commons Lang org.apache.commons.lang3.StringUtils.isEmpty(CharSequence)
     * </p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    // 中文注释：方法 isEmpty 检查 CharSequence 是否为 null 或空字符串。
    // 参数说明：cs - 要检查的 CharSequence，可能为 null。
    // 返回值：返回 true 如果 CharSequence 为 null 或长度为 0；否则返回 false。
    // 方法目的：快速检查字符串是否为空，不处理空白字符，区别于 isBlank。
    // 特殊处理：不会修剪字符串，仅检查 null 或长度为 0。

    /**
     * Checks if a String is not blank. The opposite of {@link #isBlank(String)}.
     *
     * @param s the String to check, may be {@code null}
     * @return {@code true} if the String is non-{@code null} and has content after being trimmed.
     */
    public static boolean isNotBlank(final String s) {
        return !isBlank(s);
    }
    // 中文注释：方法 isNotBlank 检查字符串是否非空白（非 null、非空且包含非空白字符）。
    // 参数说明：s - 要检查的字符串，可能为 null。
    // 返回值：返回 true 如果字符串非空白；否则返回 false。
    // 方法目的：与 isBlank 相反，用于确认字符串有效性。

    /**
     * <p>
     * Checks if a CharSequence is not empty ("") and not null.
     * </p>
     *
     * <pre>
     * Strings.isNotEmpty(null)      = false
     * Strings.isNotEmpty("")        = false
     * Strings.isNotEmpty(" ")       = true
     * Strings.isNotEmpty("bob")     = true
     * Strings.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * <p>
     * Copied from Apache Commons Lang org.apache.commons.lang3.StringUtils.isNotEmpty(CharSequence)
     * </p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
    // 中文注释：方法 isNotEmpty 检查 CharSequence 是否非空且非 null。
    // 参数说明：cs - 要检查的 CharSequence，可能为 null。
    // 返回值：返回 true 如果 CharSequence 非空且非 null；否则返回 false。
    // 方法目的：与 isEmpty 相反，用于验证 CharSequence 是否有内容。

    /**
     * <p>Joins the elements of the provided {@code Iterable} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list. Null objects or empty
     * strings within the iteration are represented by empty strings.</p>
     *
     * @param iterable  the {@code Iterable} providing the values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterable<?> iterable, final char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }
    // 中文注释：方法 join 将 Iterable 中的元素连接成一个字符串，使用指定分隔符。
    // 参数说明：iterable - 要连接的 Iterable，可能为 null；separator - 分隔符字符。
    // 返回值：连接后的字符串，若输入为 null 则返回 null。
    // 关键步骤：将 Iterable 转换为 Iterator 并调用重载的 join 方法。
    // 方法目的：将多个元素合并为单一字符串，常用于日志或输出格式化。

    /**
     * <p>Joins the elements of the provided {@code Iterator} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list. Null objects or empty
     * strings within the iteration are represented by empty strings.</p>
     *
     * @param iterator  the {@code Iterator} of values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterator<?> iterator, final char separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, EMPTY);
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(separator);
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }
    // 中文注释：方法 join 将 Iterator 中的元素连接成一个字符串，使用指定分隔符。
    // 参数说明：iterator - 要连接的 Iterator，可能为 null；separator - 分隔符字符。
    // 返回值：连接后的字符串，若输入为 null 则返回 null。
    // 关键步骤：
    // 1. 处理 null、零元素和单一元素的情况；
    // 2. 对于多个元素，使用 StringBuilder（初始容量 256）逐个追加元素和分隔符；
    // 3. null 元素被转换为空字符串。
    // 方法目的：高效地将多个元素连接为字符串。
    // 特殊处理：初始化 StringBuilder 容量为 256，避免频繁扩容以提升性能。

    public static String[] splitList(String string) {
        return string != null ? string.split(COMMA_DELIMITED_RE) : new String[0];
    }
    // 中文注释：方法 splitList 将字符串按逗号分隔（忽略前后空格）分割成字符串数组。
    // 参数说明：string - 要分割的字符串，可能为 null。
    // 返回值：分割后的字符串数组，若输入为 null 则返回空数组。
    // 方法目的：将逗号分隔的字符串解析为数组，常用于处理配置或输入数据。
    // 重要配置参数：使用 COMMA_DELIMITED_RE 正则表达式分割，忽略空白。

    /**
     * <p>Gets the leftmost {@code len} characters of a String.</p>
     *
     * <p>If {@code len} characters are not available, or the
     * String is {@code null}, the String will be returned without
     * an exception. An empty String is returned if len is negative.</p>
     *
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -ve)     = ""
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * <p>
     * Copied from Apache Commons Lang org.apache.commons.lang3.StringUtils.
     * </p>
     * 
     * @param str  the String to get the leftmost characters from, may be null
     * @param len  the length of the required String
     * @return the leftmost characters, {@code null} if null String input
     */
    public static String left(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }
    // 中文注释：方法 left 返回字符串最左侧指定长度的子字符串。
    // 参数说明：str - 输入字符串，可能为 null；len - 要提取的字符长度。
    // 返回值：左侧子字符串，若输入为 null 则返回 null，若 len 小于 0 则返回空字符串。
    // 关键步骤：
    // 1. 处理 null 输入，返回 null；
    // 2. 处理负长度，返回空字符串；
    // 3. 如果字符串长度小于等于 len，返回原字符串；
    // 4. 否则提取从 0 到 len 的子字符串。
    // 方法目的：提取字符串左侧指定长度的内容，适用于截断或格式化。
    // 特殊处理：不会抛出异常，安全处理 null 和负长度输入。

    /**
     * Returns a quoted string.
     * 
     * @param str a String
     * @return {@code 'str'}
     */
    public static String quote(final String str) {
        return Chars.QUOTE + str + Chars.QUOTE;
    }
    // 中文注释：方法 quote 为输入字符串添加单引号。
    // 参数说明：str - 输入字符串。
    // 返回值：返回带单引号的字符串，例如输入 "abc" 返回 "'abc'"。
    // 方法目的：为字符串添加单引号，用于格式化或特定输出场景。

    /**
     * <p>
     * Removes control characters (char &lt;= 32) from both ends of this String returning {@code null} if the String is
     * empty ("") after the trim or if it is {@code null}.
     *
     * <p>
     * The String is trimmed using {@link String#trim()}. Trim removes start and end characters &lt;= 32.
     * </p>
     *
     * <pre>
     * Strings.trimToNull(null)          = null
     * Strings.trimToNull("")            = null
     * Strings.trimToNull("     ")       = null
     * Strings.trimToNull("abc")         = "abc"
     * Strings.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * <p>
     * Copied from Apache Commons Lang org.apache.commons.lang3.StringUtils.trimToNull(String)
     * </p>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String, {@code null} if only chars &lt;= 32, empty or null String input
     */
    public static String trimToNull(final String str) {
        final String ts = str == null ? null : str.trim();
        return isEmpty(ts) ? null : ts;
    }
    // 中文注释：方法 trimToNull 移除字符串两端的控制字符（ASCII <= 32），若结果为空则返回 null。
    // 参数说明：str - 要修剪的字符串，可能为 null。
    // 返回值：修剪后的字符串，若结果为空或输入为 null 则返回 null。
    // 关键步骤：
    // 1. 使用 String.trim() 移除两端控制字符；
    // 2. 检查修剪后的字符串是否为空，若为空返回 null，否则返回修剪结果。
    // 方法目的：清理字符串两端的控制字符，适用于数据清洗。
    // 特殊处理：若修剪后字符串为空，返回 null 而非空字符串。

    private Strings() {
        // empty
    }
    // 中文注释：私有构造函数，防止类被实例化。
    // 方法目的：确保 Strings 类作为静态工具类使用。

    /**
     * Shorthand for {@code str.toLowerCase(Locale.ROOT);}
     * @param str The string to upper case.
     * @return a new string
     * @see String#toLowerCase(Locale)
     */
    public static String toRootLowerCase(final String str) {
        return str.toLowerCase(Locale.ROOT);
    }
    // 中文注释：方法 toRootLowerCase 将字符串转换为小写，使用 Locale.ROOT 区域设置。
    // 参数说明：str - 输入字符串。
    // 返回值：小写字符串。
    // 方法目的：提供标准化的字符串小写转换，跨区域一致性。
    // 特殊处理：使用 Locale.ROOT 确保结果不受系统区域设置影响。

    /**
     * Shorthand for {@code str.toUpperCase(Locale.ROOT);}
     * @param str The string to lower case.
     * @return a new string
     * @see String#toLowerCase(Locale)
     */
    public static String toRootUpperCase(final String str) {
        return str.toUpperCase(Locale.ROOT);
    }
    // 中文注释：方法 toRootUpperCase 将字符串转换为大写，使用 Locale.ROOT 区域设置。
    // 参数说明：str - 输入字符串。
    // 返回值：大写字符串。
    // 方法目的：提供标准化的字符串大写转换，跨区域一致性。
    // 特殊处理：使用 Locale.ROOT 确保结果不受系统区域设置影响。

    /**
     * Concatenates 2 Strings without allocation.
     * @param str1 the first string.
     * @param str2 the second string.
     * @return the concatenated String.
     */
    public static String concat(String str1, String str2) {
        if (isEmpty(str1)) {
            return str2;
        } else if (isEmpty(str2)) {
            return str1;
        }
        StringBuilder sb = tempStr.get();
        try {
            return sb.append(str1).append(str2).toString();
        } finally {
            sb.setLength(0);
        }
    }
    // 中文注释：方法 concat 高效连接两个字符串，避免额外内存分配。
    // 参数说明：str1 - 第一个字符串；str2 - 第二个字符串。
    // 返回值：连接后的字符串。
    // 关键步骤：
    // 1. 检查任一字符串是否为空，若为空返回另一个字符串；
    // 2. 使用线程局部 StringBuilder 连接字符串；
    // 3. 连接后清空 StringBuilder 以复用。
    // 方法目的：高效连接字符串，减少内存分配。
    // 特殊处理：使用 ThreadLocal 的 StringBuilder 避免频繁创建对象，清空后可复用。

    /**
     * Creates a new string repeating given {@code str} {@code count} times.
     * @param str input string
     * @param count the repetition count
     * @return the new string
     * @throws IllegalArgumentException if either {@code str} is null or {@code count} is negative
     */
    public static String repeat(final String str, final int count) {
        Objects.requireNonNull(str, "str");
        if (count < 0) {
            throw new IllegalArgumentException("count");
        }
        StringBuilder sb = tempStr.get();
        try {
            for (int index = 0; index < count; index++) {
                sb.append(str);
            }
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }
    // 中文注释：方法 repeat 创建一个新字符串，将输入字符串重复指定次数。
    // 参数说明：str - 输入字符串，不能为 null；count - 重复次数，不能为负数。
    // 返回值：重复后的新字符串。
    // 关键步骤：
    // 1. 检查 str 是否为 null，若为 null 抛出异常；
    // 2. 检查 count 是否为负数，若为负数抛出异常；
    // 3. 使用线程局部 StringBuilder 重复追加字符串；
    // 4. 完成后清空 StringBuilder。
    // 方法目的：生成重复字符串，适用于格式化或填充场景。
    // 特殊处理：
    // 1. 使用 ThreadLocal 的 StringBuilder 提高性能；
    // 2. 明确检查 null 和负数输入，抛出 IllegalArgumentException。
}
