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
package org.apache.logging.log4j.core.util.datetime;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * The basic methods for performing date formatting.
 * 执行日期格式化的基本方法。
 */
public abstract class Format {

    /**
     * Formats an object to a string.
     * 将对象格式化为字符串。
     *
     * @param obj The object to format.
     * 要格式化的对象。
     * @return The formatted string.
     * 格式化后的字符串。
     */
    public final String format (final Object obj) {
        // Creates a new StringBuilder and FieldPosition, then calls the abstract format method.
        // 创建一个新的 StringBuilder 和 FieldPosition 对象，然后调用抽象的 format 方法。
        // Finally, converts the StringBuilder content to a String.
        // 最后，将 StringBuilder 的内容转换为 String。
        return format(obj, new StringBuilder(), new FieldPosition(0)).toString();
    }

    /**
     * Formats an object and appends the result to a StringBuilder.
     * 格式化一个对象并将结果追加到 StringBuilder。
     *
     * @param obj The object to format.
     * 要格式化的对象。
     * @param toAppendTo The StringBuilder to append the formatted text to.
     * 要追加格式化文本的 StringBuilder。
     * @param pos On input: an alignment field. On output: the offsets of the alignment field.
     * 输入时：一个对齐字段。输出时：对齐字段的偏移量。
     * @return The StringBuilder with the formatted text appended.
     * 追加了格式化文本的 StringBuilder。
     */
    public abstract StringBuilder format(Object obj, StringBuilder toAppendTo, FieldPosition pos);

    /**
     * Parses a string to an object, using the given parse position.
     * 使用给定的解析位置，将字符串解析为对象。
     *
     * @param source The string to parse.
     * 要解析的字符串。
     * @param pos The parse position.
     * 解析位置。
     * @return The parsed object.
     * 解析后的对象。
     */
    public abstract Object parseObject (String source, ParsePosition pos);

    /**
     * Parses a string to an object.
     * 将字符串解析为对象。
     *
     * This method creates a new ParsePosition and then calls the abstract parseObject method.
     * 此方法创建一个新的 ParsePosition，然后调用抽象的 parseObject 方法。
     * It also handles error checking based on the parse position.
     * 它还根据解析位置处理错误检查。
     *
     * @param source The string to parse.
     * 要解析的字符串。
     * @return The parsed object.
     * 解析后的对象。
     * @throws ParseException If parsing fails.
     * 如果解析失败则抛出 ParseException。
     */
    public Object parseObject(final String source) throws ParseException {
        // Initializes a ParsePosition with an initial index of 0.
        // 初始化一个 ParsePosition，其初始索引为 0。
        final ParsePosition pos = new ParsePosition(0);
        // Calls the abstract parseObject method to perform the actual parsing.
        // 调用抽象的 parseObject 方法执行实际的解析。
        final Object result = parseObject(source, pos);
        // Checks if the parsing was successful by verifying if the index in ParsePosition has advanced.
        // 通过检查 ParsePosition 中的索引是否已前进，来验证解析是否成功。
        if (pos.getIndex() == 0) {
            // If the index is still 0, it means no characters were parsed, indicating a parse failure.
            // 如果索引仍然是 0，表示没有字符被解析，这表明解析失败。
            throw new ParseException("Format.parseObject(String) failed", pos.getErrorIndex());
        }
        // Returns the successfully parsed object.
        // 返回成功解析的对象。
        return result;
    }
}
