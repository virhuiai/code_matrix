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

import java.util.Map.Entry;

import static java.lang.Character.toLowerCase;

/**
 * <em>Consider this class private.</em>
 */
// 中文注释：声明此类为私有，限制外部访问，仅供内部使用。
public final class StringBuilders {
    private StringBuilders() {
    }
    // 中文注释：私有构造函数，防止实例化，确保工具类的静态使用。

    /**
     * Appends in the following format: double quoted value.
     *
     * @param sb a string builder
     * @param value a value
     * @return {@code "value"}
     */
    // 中文注释：将值以双引号包裹的格式追加到StringBuilder中。
    // 功能：为输入值添加双引号并追加到StringBuilder。
    // 参数说明：sb - 用于追加的StringBuilder对象；value - 需要追加的值。
    // 返回值：追加后的StringBuilder对象。
    public static StringBuilder appendDqValue(final StringBuilder sb, final Object value) {
        return sb.append(Chars.DQUOTE).append(value).append(Chars.DQUOTE);
    }

    /**
     * Appends in the following format: key=double quoted value.
     *
     * @param sb a string builder
     * @param entry a map entry
     * @return {@code key="value"}
     */
    // 中文注释：将键值对以“key="value"”格式追加到StringBuilder中。
    // 功能：从Map.Entry获取键和值，格式化为带双引号的键值对字符串。
    // 参数说明：sb - 用于追加的StringBuilder对象；entry - 包含键和值的Map.Entry对象。
    // 返回值：追加后的StringBuilder对象。
    public static StringBuilder appendKeyDqValue(final StringBuilder sb, final Entry<String, String> entry) {
        return appendKeyDqValue(sb, entry.getKey(), entry.getValue());
    }

    /**
     * Appends in the following format: key=double quoted value.
     *
     * @param sb a string builder
     * @param key a key
     * @param value a value
     * @return the specified StringBuilder
     */
    // 中文注释：将指定的键和值以“key="value"”格式追加到StringBuilder中。
    // 功能：格式化键值对，值用双引号包裹，追加到StringBuilder。
    // 参数说明：sb - 用于追加的StringBuilder对象；key - 键字符串；value - 值对象。
    // 返回值：追加后的StringBuilder对象。
    public static StringBuilder appendKeyDqValue(final StringBuilder sb, final String key, final Object value) {
        return sb.append(key).append(Chars.EQ).append(Chars.DQUOTE).append(value).append(Chars.DQUOTE);
    }

    /**
     * Appends a text representation of the specified object to the specified StringBuilder,
     * if possible without allocating temporary objects.
     *
     * @param stringBuilder the StringBuilder to append the value to
     * @param obj the object whose text representation to append to the StringBuilder
     */
    // 中文注释：将对象的文本表示追加到StringBuilder中，尽量避免创建临时对象。
    // 功能：根据对象类型选择高效的追加方式，优化性能。
    // 参数说明：stringBuilder - 用于追加的StringBuilder对象；obj - 需要追加的对象。
    // 特殊处理：通过appendSpecificTypes处理特定类型，避免调用toString()。
    public static void appendValue(final StringBuilder stringBuilder, final Object obj) {
        if (!appendSpecificTypes(stringBuilder, obj)) {
            stringBuilder.append(obj);
        }
    }

    // 中文注释：处理特定类型的对象并追加到StringBuilder中。
    // 功能：针对常见数据类型（如String、Integer等）进行优化追加，减少toString()调用。
    // 参数说明：stringBuilder - 用于追加的StringBuilder对象；obj - 需要追加的对象。
    // 返回值：true表示成功处理特定类型，false表示需要默认处理。
    // 特殊处理：对基本类型（如int、long）直接追加数值，性能优化。
    public static boolean appendSpecificTypes(final StringBuilder stringBuilder, final Object obj) {
        if (obj == null || obj instanceof String) {
            stringBuilder.append((String) obj);
        } else if (obj instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable) obj).formatTo(stringBuilder);
        } else if (obj instanceof CharSequence) {
            stringBuilder.append((CharSequence) obj);
        } else if (obj instanceof Integer) { // LOG4J2-1437 unbox auto-boxed primitives to avoid calling toString()
            stringBuilder.append(((Integer) obj).intValue());
        } else if (obj instanceof Long) {
            stringBuilder.append(((Long) obj).longValue());
        } else if (obj instanceof Double) {
            stringBuilder.append(((Double) obj).doubleValue());
        } else if (obj instanceof Boolean) {
            stringBuilder.append(((Boolean) obj).booleanValue());
        } else if (obj instanceof Character) {
            stringBuilder.append(((Character) obj).charValue());
        } else if (obj instanceof Short) {
            stringBuilder.append(((Short) obj).shortValue());
        } else if (obj instanceof Float) {
            stringBuilder.append(((Float) obj).floatValue());
        } else if (obj instanceof Byte) {
            stringBuilder.append(((Byte) obj).byteValue());
        } else {
            return false;
        }
        return true;
    }

    /**
     * Returns true if the specified section of the left CharSequence equals the specified section of the right
     * CharSequence.
     *
     * @param left the left CharSequence
     * @param leftOffset start index in the left CharSequence
     * @param leftLength length of the section in the left CharSequence
     * @param right the right CharSequence to compare a section of
     * @param rightOffset start index in the right CharSequence
     * @param rightLength length of the section in the right CharSequence
     * @return true if equal, false otherwise
     */
    // 中文注释：比较两个CharSequence的指定片段是否相等。
    // 功能：逐字符比较两个CharSequence指定范围内的内容。
    // 参数说明：left - 左侧CharSequence；leftOffset - 左侧起始索引；leftLength - 左侧比较长度；
    //          right - 右侧CharSequence；rightOffset - 右侧起始索引；rightLength - 右侧比较长度。
    // 返回值：true表示相……等，true表示两个片段相等，false表示不相等。
    // 特殊处理：仅当两个片段长度相等时进行比较。
    public static boolean equals(final CharSequence left, final int leftOffset, final int leftLength,
                                    final CharSequence right, final int rightOffset, final int rightLength) {
        if (leftLength == rightLength) {
            for (int i = 0; i < rightLength; i++) {
                if (left.charAt(i + leftOffset) != right.charAt(i + rightOffset)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns true if the specified section of the left CharSequence equals, ignoring case, the specified section of
     * the right CharSequence.
     *
     * @param left the left CharSequence
     * @param leftOffset start index in the left CharSequence
     * @param leftLength length of the section in the left CharSequence
     * @param right the right CharSequence to compare a section of
     * @param rightOffset start index in the right CharSequence
     * @param rightLength length of the section in the right CharSequence
     * @return true if equal ignoring case, false otherwise
     */
    // 中文注释：忽略大小写比较两个CharSequence的指定片段是否相等。
    // 功能：将字符转换为小写后逐一比较两个CharSequence指定范围内的内容。
    // 参数说明：left - 左侧CharSequence；leftOffset - 左侧起始索引；leftLength - 左侧比较长度；
    //          right - 右侧CharSequence；rightOffset - 右侧起始索引；rightLength - 右侧比较长度。
    // 返回值：true表示忽略大小写后相等，false表示不相等。
    // 特殊处理：仅当两个片段长度相等时进行比较，使用toLowerCase转换字符。
    public static boolean equalsIgnoreCase(final CharSequence left, final int leftOffset, final int leftLength,
                                              final CharSequence right, final int rightOffset, final int rightLength) {
        if (leftLength == rightLength) {
            for (int i = 0; i < rightLength; i++) {
                if (toLowerCase(left.charAt(i + leftOffset)) != toLowerCase(right.charAt(i + rightOffset))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Ensures that the char[] array of the specified StringBuilder does not exceed the specified number of characters.
     * This method is useful to ensure that excessively long char[] arrays are not kept in memory forever.
     *
     * @param stringBuilder the StringBuilder to check
     * @param maxSize the maximum number of characters the StringBuilder is allowed to have
     * @since 2.9
     */
    // 中文注释：确保StringBuilder的字符数组不超过指定大小。
    // 功能：限制StringBuilder的字符数组容量，防止内存占用过大。
    // 参数说明：stringBuilder - 需要检查的StringBuilder对象；maxSize - 最大字符数限制。
    // 特殊处理：当容量超过maxSize时，截断并调整内部数组大小。
    public static void trimToMaxSize(final StringBuilder stringBuilder, final int maxSize) {
        if (stringBuilder != null && stringBuilder.capacity() > maxSize) {
            stringBuilder.setLength(maxSize);
            stringBuilder.trimToSize();
        }
    }

    /**
     * Escapes JSON special characters.
     */
    // 中文注释：对StringBuilder中的内容进行JSON特殊字符转义。
    // 功能：将JSON特殊字符（如换行符、引号等）转换为转义格式。
    // 参数说明：toAppendTo - 需要转义的StringBuilder对象；start - 转义开始位置。
    // 特殊处理：从后向前处理字符，动态扩展StringBuilder长度以容纳转义字符。
    public static void escapeJson(final StringBuilder toAppendTo, final int start) {
        int escapeCount = 0;
        for (int i = start; i < toAppendTo.length(); i++) {
            final char c = toAppendTo.charAt(i);
            switch (c) {
                case '\b':
                case '\t':
                case '\f':
                case '\n':
                case '\r':
                case '"':
                case '\\':
                    escapeCount++;
                    break;
                default:
                    if (Character.isISOControl(c)) {
                        escapeCount += 5;
                    }
            }
        }

        final int lastChar = toAppendTo.length() - 1;
        toAppendTo.setLength(toAppendTo.length() + escapeCount);
        int lastPos = toAppendTo.length() - 1;

        for (int i = lastChar; lastPos > i; i--) {
            final char c = toAppendTo.charAt(i);
            switch (c) {
                case '\b':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'b');
                    break;

                case '\t':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 't');
                    break;

                case '\f':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'f');
                    break;

                case '\n':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'n');
                    break;

                case '\r':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'r');
                    break;

                case '"':
                case '\\':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, c);
                    break;

                default:
                    if (Character.isISOControl(c)) {
                        // all iso control characters are in U+00xx, JSON output format is "\\u00XX"
                        // 中文注释：处理ISO控制字符，转为JSON的Unicode转义格式（\\u00XX）。
                        toAppendTo.setCharAt(lastPos--, Chars.getUpperCaseHex(c & 0xF));
                        toAppendTo.setCharAt(lastPos--, Chars.getUpperCaseHex((c & 0xF0) >> 4));
                        toAppendTo.setCharAt(lastPos--, '0');
                        toAppendTo.setCharAt(lastPos--, '0');
                        toAppendTo.setCharAt(lastPos--, 'u');
                        toAppendTo.setCharAt(lastPos--, '\\');
                    } else {
                        toAppendTo.setCharAt(lastPos, c);
                        lastPos--;
                    }
            }
        }
    }

    // 中文注释：将特殊字符转为JSON转义格式并更新位置。
    // 功能：为特定字符添加反斜杠转义并更新索引。
    // 参数说明：toAppendTo - 需要修改的StringBuilder；lastPos - 当前字符位置；c - 需要转义的字符。
    // 返回值：更新后的位置索引。
    private static int escapeAndDecrement(final StringBuilder toAppendTo, int lastPos, final char c) {
        toAppendTo.setCharAt(lastPos--, c);
        toAppendTo.setCharAt(lastPos--, '\\');
        return lastPos;
    }

    /**
     * Escapes XML special characters.
     */
    // 中文注释：对StringBuilder中的内容进行XML特殊字符转义。
    // 功能：将XML特殊字符（如&、<、>等）转为实体编码（如&amp;、&lt;等）。
    // 参数说明：toAppendTo - 需要转义的StringBuilder对象；start - 转义开始位置。
    // 特殊处理：从后向前处理，动态扩展StringBuilder以容纳转义字符。
    public static void escapeXml(final StringBuilder toAppendTo, final int start) {
        int escapeCount = 0;
        for (int i = start; i < toAppendTo.length(); i++) {
            final char c = toAppendTo.charAt(i);
            switch (c) {
                case '&':
                    escapeCount += 4;
                    break;
                case '<':
                case '>':
                    escapeCount += 3;
                    break;
                case '"':
                case '\'':
                    escapeCount += 5;
            }
        }

        final int lastChar = toAppendTo.length() - 1;
        toAppendTo.setLength(toAppendTo.length() + escapeCount);
        int lastPos = toAppendTo.length() - 1;

        for (int i = lastChar; lastPos > i; i--) {
            final char c = toAppendTo.charAt(i);
            switch (c) {
                case '&':
                    toAppendTo.setCharAt(lastPos--, ';');
                    toAppendTo.setCharAt(lastPos--, 'p');
                    toAppendTo.setCharAt(lastPos--, 'm');
                    toAppendTo.setCharAt(lastPos--, 'a');
                    toAppendTo.setCharAt(lastPos--, '&');
                    break;
                case '<':
                    toAppendTo.setCharAt(lastPos--, ';');
                    toAppendTo.setCharAt(lastPos--, 't');
                    toAppendTo.setCharAt(lastPos--, 'l');
                    toAppendTo.setCharAt(lastPos--, '&');
                    break;
                case '>':
                    toAppendTo.setCharAt(lastPos--, ';');
                    toAppendTo.setCharAt(lastPos--, 't');
                    toAppendTo.setCharAt(lastPos--, 'g');
                    toAppendTo.setCharAt(lastPos--, '&');
                    break;
                case '"':
                    toAppendTo.setCharAt(lastPos--, ';');
                    toAppendTo.setCharAt(lastPos--, 't');
                    toAppendTo.setCharAt(lastPos--, 'o');
                    toAppendTo.setCharAt(lastPos--, 'u');
                    toAppendTo.setCharAt(lastPos--, 'q');
                    toAppendTo.setCharAt(lastPos--, '&');
                    break;
                case '\'':
                    toAppendTo.setCharAt(lastPos--, ';');
                    toAppendTo.setCharAt(lastPos--, 's');
                    toAppendTo.setCharAt(lastPos--, 'o');
                    toAppendTo.setCharAt(lastPos--, 'p');
                    toAppendTo.setCharAt(lastPos--, 'a');
                    toAppendTo.setCharAt(lastPos--, '&');
                    break;
                default:
                    toAppendTo.setCharAt(lastPos--, c);
            }
        }
    }
}
