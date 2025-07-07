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
 * 本文件遵循 Apache 许可证 2.0 版，授权 Apache 软件基金会（ASF）及其贡献者。
 * 除非适用法律要求或书面同意，软件按“原样”分发，不提供任何明示或暗示的担保。
 * 请参阅许可证以了解具体的权限和限制。
 */

package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The default JSON formatter for {@link MapMessage}s.
 * <p>
 * The following types have specific handlers:
 * <p>
 * <ul>
 *     <li>{@link Map}
 *     <li>{@link Collection} ({@link List}, {@link Set}, etc.)
 *     <li>{@link Number} ({@link BigDecimal}, {@link Double}, {@link Long}, {@link Byte}, etc.)
 *     <li>{@link Boolean}
 *     <li>{@link StringBuilderFormattable}
 *     <li><tt>char/boolean/byte/short/int/long/float/double/Object</tt> arrays
 *     <li>{@link String}
 * </ul>
 * <p>
 * It supports nesting up to a maximum depth of 8, which is set by
 * <tt>log4j2.mapMessage.jsonFormatter.maxDepth</tt> property.
 */
/*
 * 中文注释：
 * 类功能：MapMessageJsonFormatter 是用于格式化 MapMessage 的默认 JSON 格式化器。
 * 主要目的：将 MapMessage 对象及其包含的数据转换为 JSON 格式的字符串输出。
 * 支持的类型：Map、Collection（List、Set 等）、Number（BigDecimal、Double、Long、Byte 等）、
 * Boolean、StringBuilderFormattable、各种数组（char、boolean、byte 等）以及 String。
 * 配置参数：最大嵌套深度通过属性 log4j2.mapMessage.jsonFormatter.maxDepth 设置，默认为 8。
 * 注意事项：嵌套深度超过最大值时会抛出异常，防止栈溢出。
 */
enum MapMessageJsonFormatter {;

    public static final int MAX_DEPTH = readMaxDepth();
    // 中文注释：定义最大嵌套深度，从配置文件读取，默认值为 8。

    private static final char DQUOTE = '"';
    // 中文注释：定义 JSON 字符串的双引号字符。

    private static final char RBRACE = ']';
    // 中文注释：定义 JSON 数组的右方括号。

    private static final char LBRACE = '[';
    // 中文注释：定义 JSON 数组的左方括号。

    private static final char COMMA = ',';
    // 中文注释：定义 JSON 分隔符逗号。

    private static final char RCURLY = '}';
    // 中文注释：定义 JSON 对象的右花括号。

    private static final char LCURLY = '{';
    // 中文注释：定义 JSON 对象的左花括号。

    private static final char COLON = ':';
    // 中文注释：定义 JSON 键值对的分隔冒号。

    /**
     * Reads the maximum depth for JSON formatting from properties.
     * @return the maximum depth
     */
    private static int readMaxDepth() {
        final int maxDepth = PropertiesUtil
                .getProperties()
                .getIntegerProperty("log4j2.mapMessage.jsonFormatter.maxDepth", 8);
        if (maxDepth < 0) {
            throw new IllegalArgumentException(
                    "was expecting a positive maxDepth, found: " + maxDepth);
        }
        return maxDepth;
    }
    /*
     * 中文注释：
     * 方法功能：从配置文件读取 JSON 格式化的最大嵌套深度。
     * 返回值：int 类型，表示最大嵌套深度，默认值为 8。
     * 执行流程：
     * 1. 通过 PropertiesUtil 获取配置属性。
     * 2. 读取 log4j2.mapMessage.jsonFormatter.maxDepth 属性，若未设置则使用默认值 8。
     * 3. 检查深度是否为负数，若是则抛出 IllegalArgumentException。
     * 注意事项：确保返回的深度值非负，以避免非法配置。
     */

    /**
     * Formats an object to JSON and appends it to the given StringBuilder.
     * @param sb the StringBuilder to append to
     * @param object the object to format
     */
    static void format(final StringBuilder sb, final Object object) {
        format(sb, object, 0);
    }
    /*
     * 中文注释：
     * 方法功能：将对象格式化为 JSON 字符串并追加到 StringBuilder。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储格式化后的 JSON 字符串。
     * - object：需要格式化为 JSON 的对象。
     * 执行流程：调用重载的 format 方法，初始深度设置为 0。
     * 用途：提供便捷的入口方法，简化调用。
     */

    /**
     * Formats an object to JSON at a given depth.
     * @param sb the StringBuilder to append to
     * @param object the object to format
     * @param depth the current nesting depth
     */
    private static void format(
            final StringBuilder sb,
            final Object object,
            final int depth) {

        if (depth >= MAX_DEPTH) {
            throw new IllegalArgumentException("maxDepth has been exceeded");
        }
        /*
         * 中文注释：
         * 方法功能：根据对象类型和嵌套深度，将对象格式化为 JSON 字符串。
         * 参数说明：
         * - sb：StringBuilder 对象，用于存储格式化后的 JSON 字符串。
         * - object：需要格式化的对象。
         * - depth：当前嵌套深度，用于防止过深的嵌套。
         * 执行流程：
         * 1. 检查当前深度是否超过最大深度，若超过则抛出 IllegalArgumentException。
         * 2. 根据对象类型调用相应的格式化方法（如 Map、List、Number 等）。
         * 注意事项：支持多种数据类型，确保类型安全和 JSON 格式的正确性。
         */

        // null
        if (object == null) {
            sb.append("null");
        }
        // 中文注释：处理 null 值，直接追加 "null" 到 StringBuilder。

        // map
        else if (object instanceof IndexedStringMap) {
            final IndexedStringMap map = (IndexedStringMap) object;
            formatIndexedStringMap(sb, map, depth);
        } else if (object instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<Object, Object> map = (Map<Object, Object>) object;
            formatMap(sb, map, depth);
        }
        // 中文注释：处理 Map 类型，分为 IndexedStringMap 和普通 Map，分别调用相应的格式化方法。

        // list & collection
        else if (object instanceof List) {
            @SuppressWarnings("unchecked")
            final List<Object> list = (List<Object>) object;
            formatList(sb, list, depth);
        } else if (object instanceof Collection) {
            @SuppressWarnings("unchecked")
            final Collection<Object> collection = (Collection<Object>) object;
            formatCollection(sb, collection, depth);
        }
        // 中文注释：处理 List 和 Collection 类型，分别调用 formatList 和 formatCollection 方法。

        // number & boolean
        else if (object instanceof Number) {
            final Number number = (Number) object;
            formatNumber(sb, number);
        } else if (object instanceof Boolean) {
            final boolean booleanValue = (boolean) object;
            formatBoolean(sb, booleanValue);
        }
        // 中文注释：处理 Number 和 Boolean 类型，调用 formatNumber 和 formatBoolean 方法。

        // formattable
        else if (object instanceof StringBuilderFormattable) {
            final StringBuilderFormattable formattable = (StringBuilderFormattable) object;
            formatFormattable(sb, formattable);
        }
        // 中文注释：处理 StringBuilderFormattable 类型，调用 formatFormattable 方法。

        // arrays
        else if (object instanceof char[]) {
            final char[] charValues = (char[]) object;
            formatCharArray(sb, charValues);
        } else if (object instanceof boolean[]) {
            final boolean[] booleanValues = (boolean[]) object;
            formatBooleanArray(sb, booleanValues);
        } else if (object instanceof byte[]) {
            final byte[] byteValues = (byte[]) object;
            formatByteArray(sb, byteValues);
        } else if (object instanceof short[]) {
            final short[] shortValues = (short[]) object;
            formatShortArray(sb, shortValues);
        } else if (object instanceof int[]) {
            final int[] intValues = (int[]) object;
            formatIntArray(sb, intValues);
        } else if (object instanceof long[]) {
            final long[] longValues = (long[]) object;
            formatLongArray(sb, longValues);
        } else if (object instanceof float[]) {
            final float[] floatValues = (float[]) object;
            formatFloatArray(sb, floatValues);
        } else if (object instanceof double[]) {
            final double[] doubleValues = (double[]) object;
            formatDoubleArray(sb, doubleValues);
        } else if (object instanceof Object[]) {
            final Object[] objectValues = (Object[]) object;
            formatObjectArray(sb, objectValues, depth);
        }
        // 中文注释：处理各种数组类型（char、boolean、byte 等），调用对应的格式化方法。

        // string
        else {
            formatString(sb, object);
        }
        // 中文注释：默认处理字符串类型，调用 formatString 方法。
    }

    /**
     * Formats an IndexedStringMap to JSON.
     * @param sb the StringBuilder to append to
     * @param map the IndexedStringMap to format
     * @param depth the current nesting depth
     */
    private static void formatIndexedStringMap(
            final StringBuilder sb,
            final IndexedStringMap map,
            final int depth) {
        sb.append(LCURLY);
        final int nextDepth = depth + 1;
        for (int entryIndex = 0; entryIndex < map.size(); entryIndex++) {
            final String key = map.getKeyAt(entryIndex);
            final Object value = map.getValueAt(entryIndex);
            if (entryIndex > 0) {
                sb.append(COMMA);
            }
            sb.append(DQUOTE);
            final int keyStartIndex = sb.length();
            sb.append(key);
            StringBuilders.escapeJson(sb, keyStartIndex);
            sb.append(DQUOTE).append(COLON);
            format(sb, value, nextDepth);
        }
        sb.append(RCURLY);
    }
    /*
     * 中文注释：
     * 方法功能：将 IndexedStringMap 格式化为 JSON 对象。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - map：IndexedStringMap 对象，包含键值对。
     * - depth：当前嵌套深度。
     * 执行流程：
     * 1. 追加左花括号开始 JSON 对象。
     * 2. 遍历 map 中的键值对，依次追加键（带双引号，JSON 转义）、冒号和值。
     * 3. 对每个值递归调用 format 方法，增加嵌套深度。
     * 4. 追加右花括号结束 JSON 对象。
     * 注意事项：键值对之间用逗号分隔，键需进行 JSON 转义以确保格式正确。
     */

    /**
     * Formats a Map to JSON.
     * @param sb the StringBuilder to append to
     * @param map the Map to format
     * @param depth the current nesting depth
     */
    private static void formatMap(
            final StringBuilder sb,
            final Map<Object, Object> map,
            final int depth) {
        sb.append(LCURLY);
        final int nextDepth = depth + 1;
        final boolean[] firstEntry = {true};
        map.forEach((final Object key, final Object value) -> {
            if (key == null) {
                throw new IllegalArgumentException("null keys are not allowed");
            }
            if (firstEntry[0]) {
                firstEntry[0] = false;
            } else {
                sb.append(COMMA);
            }
            sb.append(DQUOTE);
            final String keyString = String.valueOf(key);
            final int keyStartIndex = sb.length();
            sb.append(keyString);
            StringBuilders.escapeJson(sb, keyStartIndex);
            sb.append(DQUOTE).append(COLON);
            format(sb, value, nextDepth);
        });
        sb.append(RCURLY);
    }
    /*
     * 中文注释：
     * 方法功能：将普通 Map 格式化为 JSON 对象。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - map：Map 对象，包含键值对。
     * - depth：当前嵌套深度。
     * 执行流程：
     * 1. 追加左花括号开始 JSON 对象。
     * 2. 使用 forEach 遍历 map 的键值对。
     * 3. 检查键是否为 null，若是则抛出 IllegalArgumentException。
     * 4. 为每个键值对追加键（带双引号，JSON 转义）、冒号和值。
     * 5. 对值递归调用 format 方法，增加嵌套深度。
     * 6. 追加右花括号结束 JSON 对象。
     * 注意事项：使用 boolean 数组 firstEntry 跟踪是否为第一个键值对，以决定是否添加逗号。
     */

    /**
     * Formats a List to JSON.
     * @param sb the StringBuilder to append to
     * @param items the List to format
     * @param depth the current nesting depth
     */
    private static void formatList(
            final StringBuilder sb,
            final List<Object> items,
            final int depth) {
        sb.append(LBRACE);
        final int nextDepth = depth + 1;
        for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final Object item = items.get(itemIndex);
            format(sb, item, nextDepth);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将 List 格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：List 对象，包含元素列表。
     * - depth：当前嵌套深度。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历 List 中的每个元素，递归调用 format 方法格式化元素。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：确保元素之间正确添加逗号，嵌套深度加 1。
     */

    /**
     * Formats a Collection to JSON.
     * @param sb the StringBuilder to append to
     * @param items the Collection to format
     * @param depth the current nesting depth
     */
    private static void formatCollection(
            final StringBuilder sb,
            final Collection<Object> items,
            final int depth) {
        sb.append(LBRACE);
        final int nextDepth = depth + 1;
        final boolean[] firstItem = {true};
        items.forEach((final Object item) -> {
            if (firstItem[0]) {
                firstItem[0] = false;
            } else {
                sb.append(COMMA);
            }
            format(sb, item, nextDepth);
        });
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将 Collection 格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：Collection 对象，包含元素集合。
     * - depth：当前嵌套深度。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 使用 forEach 遍历 Collection 的每个元素，递归调用 format 方法。
     * 3. 使用 firstItem 数组跟踪是否为第一个元素，以决定是否添加逗号。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：与 formatList 类似，但适用于非 List 的 Collection（如 Set）。
     */

    /**
     * Formats a Number to JSON.
     * @param sb the StringBuilder to append to
     * @param number the Number to format
     */
    private static void formatNumber(final StringBuilder sb, final Number number) {
        if (number instanceof BigDecimal) {
            final BigDecimal decimalNumber = (BigDecimal) number;
            sb.append(decimalNumber.toString());
        } else if (number instanceof Double) {
            final double doubleNumber = (Double) number;
            sb.append(doubleNumber);
        } else if (number instanceof Float) {
            final float floatNumber = (float) number;
            sb.append(floatNumber);
        } else if (number instanceof Byte ||
                number instanceof Short ||
                number instanceof Integer ||
                number instanceof Long) {
            final long longNumber = number.longValue();
            sb.append(longNumber);
        } else {
            final long longNumber = number.longValue();
            final double doubleValue = number.doubleValue();
            if (Double.compare(longNumber, doubleValue) == 0) {
                sb.append(longNumber);
            } else {
                sb.append(doubleValue);
            }
        }
    }
    /*
     * 中文注释：
     * 方法功能：将 Number 类型格式化为 JSON 数值。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - number：Number 对象，表示数值。
     * 执行流程：
     * 1. 如果是 BigDecimal，直接追加其字符串表示。
     * 2. 如果是 Double 或 Float，追加对应的值。
     * 3. 如果是 Byte、Short、Integer 或 Long，追加其长整型值。
     * 4. 对于其他 Number 类型，比较长整型和双精度值，若相等则使用长整型，否则使用双精度。
     * 注意事项：确保数值格式符合 JSON 标准，无需双引号。
     */

    /**
     * Formats a boolean to JSON.
     * @param sb the StringBuilder to append to
     * @param booleanValue the boolean to format
     */
    private static void formatBoolean(final StringBuilder sb, final boolean booleanValue) {
        sb.append(booleanValue);
    }
    /*
     * 中文注释：
     * 方法功能：将 Boolean 值格式化为 JSON 布尔值。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - booleanValue：布尔值。
     * 执行流程：直接追加布尔值的字符串表示（true 或 false）。
     * 注意事项：JSON 布尔值无需双引号。
     */

    /**
     * Formats a StringBuilderFormattable to JSON.
     * @param sb the StringBuilder to append to
     * @param formattable the StringBuilderFormattable to format
     */
    private static void formatFormattable(
            final StringBuilder sb,
            final StringBuilderFormattable formattable) {
        sb.append(DQUOTE);
        final int startIndex = sb.length();
        formattable.formatTo(sb);
        StringBuilders.escapeJson(sb, startIndex);
        sb.append(DQUOTE);
    }
    /*
     * 中文注释：
     * 方法功能：将 StringBuilderFormattable 对象格式化为 JSON 字符串。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - formattable：实现 StringBuilderFormattable 接口的对象。
     * 执行流程：
     * 1. 追加左双引号。
     * 2. 调用 formattable 的 formatTo 方法追加内容。
     * 3. 对追加的内容进行 JSON 转义。
     * 4. 追加右双引号。
     * 注意事项：确保字符串内容经过 JSON 转义以防止格式错误。
     */

    /**
     * Formats a char array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the char array to format
     */
    private static void formatCharArray(final StringBuilder sb, final char[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final char item = items[itemIndex];
            sb.append(DQUOTE);
            final int startIndex = sb.length();
            sb.append(item);
            StringBuilders.escapeJson(sb, startIndex);
            sb.append(DQUOTE);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将字符数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：字符数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历字符数组，每个字符作为字符串（带双引号）追加，并进行 JSON 转义。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：字符需作为 JSON 字符串处理，需转义。
     */

    /**
     * Formats a boolean array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the boolean array to format
     */
    private static void formatBooleanArray(final StringBuilder sb, final boolean[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final boolean item = items[itemIndex];
            sb.append(item);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将布尔数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：布尔数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历布尔数组，追加每个布尔值（true 或 false）。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：布尔值无需双引号。
     */

    /**
     * Formats a byte array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the byte array to format
     */
    private static void formatByteArray(final StringBuilder sb, final byte[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final byte item = items[itemIndex];
            sb.append(item);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将字节数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：字节数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历字节数组，追加每个字节值。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：字节值作为数值直接追加，无需双引号。
     */

    /**
     * Formats a short array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the short array to format
     */
    private static void formatShortArray(final StringBuilder sb, final short[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final short item = items[itemIndex];
            sb.append(item);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将短整型数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：短整型数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历短整型数组，追加每个短整型值。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：短整型值作为数值直接追加，无需双引号。
     */

    /**
     * Formats an int array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the int array to format
     */
    private static void formatIntArray(final StringBuilder sb, final int[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final int item = items[itemIndex];
            sb.append(item);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将整型数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：整型数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历整型数组，追加每个整型值。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：整型值作为数值直接追加，无需双引号。
     */

    /**
     * Formats a long array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the long array to format
     */
    private static void formatLongArray(final StringBuilder sb, final long[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final long item = items[itemIndex];
            sb.append(item);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将长整型数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：长整型数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历长整型数组，追加每个长整型值。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：长整型值作为数值直接追加，无需双引号。
     */

    /**
     * Formats a float array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the float array to format
     */
    private static void formatFloatArray(final StringBuilder sb, final float[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final float item = items[itemIndex];
            sb.append(item);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将浮点型数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：浮点型数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历浮点型数组，追加每个浮点值。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：浮点值作为数值直接追加，无需双引号。
     */

    /**
     * Formats a double array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the double array to format
     */
    private static void formatDoubleArray(
            final StringBuilder sb,
            final double[] items) {
        sb.append(LBRACE);
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final double item = items[itemIndex];
            sb.append(item);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将双精度浮点型数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：双精度浮点型数组。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历双精度浮点型数组，追加每个双精度值。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：双精度值作为数值直接追加，无需双引号。
     */

    /**
     * Formats an Object array to JSON.
     * @param sb the StringBuilder to append to
     * @param items the Object array to format
     * @param depth the current nesting depth
     */
    private static void formatObjectArray(
            final StringBuilder sb,
            final Object[] items,
            final int depth) {
        sb.append(LBRACE);
        final int nextDepth = depth + 1;
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (itemIndex > 0) {
                sb.append(COMMA);
            }
            final Object item = items[itemIndex];
            format(sb, item, nextDepth);
        }
        sb.append(RBRACE);
    }
    /*
     * 中文注释：
     * 方法功能：将对象数组格式化为 JSON 数组。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - items：对象数组。
     * - depth：当前嵌套深度。
     * 执行流程：
     * 1. 追加左方括号开始 JSON 数组。
     * 2. 遍历对象数组，对每个元素递归调用 format 方法，增加嵌套深度。
     * 3. 元素之间添加逗号分隔。
     * 4. 追加右方括号结束 JSON 数组。
     * 注意事项：支持嵌套对象，需跟踪深度以防止超过最大深度。
     */

    /**
     * Formats a String to JSON.
     * @param sb the StringBuilder to append to
     * @param value the value to format as a String
     */
    private static void formatString(final StringBuilder sb, final Object value) {
        sb.append(DQUOTE);
        final int startIndex = sb.length();
        final String valueString = String.valueOf(value);
        sb.append(valueString);
        StringBuilders.escapeJson(sb, startIndex);
        sb.append(DQUOTE);
    }
    /*
     * 中文注释：
     * 方法功能：将对象转换为字符串并格式化为 JSON 字符串。
     * 参数说明：
     * - sb：StringBuilder 对象，用于存储 JSON 字符串。
     * - value：需要格式化的对象。
     * 执行流程：
     * 1. 追加左双引号。
     * 2. 将对象转换为字符串并追加到 StringBuilder。
     * 3. 对字符串进行 JSON 转义。
     * 4. 追加右双引号。
     * 注意事项：确保字符串内容经过 JSON 转义以符合 JSON 格式。
     */

}
