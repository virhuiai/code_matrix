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

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * DateParser is the "missing" interface for the parsing methods of
 * {@link java.text.DateFormat}. You can obtain an object implementing this
 * interface by using one of the FastDateFormat factory methods.
 * <p>
 * Warning: Since binary compatible methods may be added to this interface in any
 * release, developers are not expected to implement this interface.
 *
 * <p>
 * Copied and modified from <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>.
 * </p>
 *
 * @since Apache Commons Lang 3.2
 */
/**
 * DateParser 是 {@link java.text.DateFormat} 解析方法“缺失”的接口。
 * 您可以通过使用 FastDateFormat 工厂方法之一来获取实现此接口的对象。
 * <p>
 * 警告：由于任何版本都可能向此接口添加二进制兼容方法，因此不期望开发人员实现此接口。
 * </p>
 * <p>
 * 从 <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a> 复制和修改。
 * </p>
 *
 * @since Apache Commons Lang 3.2
 */
public interface DateParser {

    /**
     * Equivalent to DateFormat.parse(String).
     *
     * See {@link java.text.DateFormat#parse(String)} for more information.
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return A <code>Date</code> parsed from the string
     * @throws ParseException if the beginning of the specified string cannot be parsed.
     */
    /**
     * 等同于 DateFormat.parse(String)。
     *
     * 有关更多信息，请参见 {@link java.text.DateFormat#parse(String)}。
     * @param source 需要被解析的字符串的开头部分。
     * @return 从字符串中解析出的 <code>Date</code> 对象。
     * @throws ParseException 如果指定字符串的开头无法被解析。
     */
    Date parse(String source) throws ParseException;

    /**
     * Equivalent to DateFormat.parse(String, ParsePosition).
     *
     * See {@link java.text.DateFormat#parse(String, ParsePosition)} for more information.
     *
     * @param source A <code>String</code>, part of which should be parsed.
     * @param pos A <code>ParsePosition</code> object with index and error index information
     * as described above.
     * @return A <code>Date</code> parsed from the string. In case of error, returns null.
     * @throws NullPointerException if text or pos is null.
     */
    /**
     * 等同于 DateFormat.parse(String, ParsePosition)。
     *
     * 有关更多信息，请参见 {@link java.text.DateFormat#parse(String, ParsePosition)}。
     *
     * @param source 需要被解析的字符串，其中一部分将被解析。
     * @param pos 一个 <code>ParsePosition</code> 对象，包含如上所述的索引和错误索引信息。
     * @return 从字符串中解析出的 <code>Date</code> 对象。如果发生错误，则返回 null。
     * @throws NullPointerException 如果文本或 pos 为 null。
     */
    Date parse(String source, ParsePosition pos);

    /**
     * Parses a formatted date string according to the format.  Updates the Calendar with parsed fields.
     * Upon success, the ParsePosition index is updated to indicate how much of the source text was consumed.
     * Not all source text needs to be consumed.  Upon parse failure, ParsePosition error index is updated to
     * the offset of the source text which does not match the supplied format.
     *
     * @param source The text to parse.
     * @param pos On input, the position in the source to start parsing, on output, updated position.
     * @param calendar The calendar into which to set parsed fields.
     * @return true, if source has been parsed (pos parsePosition is updated); otherwise false (and pos errorIndex is updated)
     * @throws IllegalArgumentException when Calendar has been set to be not lenient, and a parsed field is
     * out of range.
     *
     * @since 3.5
     */
    /**
     * 根据格式解析格式化的日期字符串。使用解析的字段更新 Calendar。
     * 成功时，ParsePosition 索引会更新，以指示消耗了多少源文本。
     * 不需要消耗所有源文本。解析失败时，ParsePosition 错误索引会更新为不匹配所提供格式的源文本的偏移量。
     *
     * @param source 要解析的文本。
     * @param pos 输入时，源中开始解析的位置；输出时，更新后的位置。
     * @param calendar 要设置解析字段的日历。
     * @return 如果源已解析（pos.parsePosition 已更新）则为 true；否则为 false（并且 pos.errorIndex 已更新）。
     * @throws IllegalArgumentException 当 Calendar 设置为不宽松模式，并且解析的字段超出范围时。
     *
     * @since 3.5
     */
    boolean parse(String source, ParsePosition pos, Calendar calendar);

    // Accessors
    // 访问器方法
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the pattern used by this parser.</p>
     *
     * @return the pattern, {@link java.text.SimpleDateFormat} compatible
     */
    /**
     * <p>获取此解析器使用的模式。</p>
     *
     * @return 模式，与 {@link java.text.SimpleDateFormat} 兼容。
     */
    String getPattern();

    /**
     * <p>
     * Gets the time zone used by this parser.
     * </p>
     *
     * <p>
     * The default {@link TimeZone} used to create a {@link Date} when the {@link TimeZone} is not specified by
     * the format pattern.
     * </p>
     *
     * @return the time zone
     */
    /**
     * <p>
     * 获取此解析器使用g的时区。
     * </p>
     *
     * <p>
     * 当格式模式未指定 {@link TimeZone} 时，用于创建 {@link Date} 的默认 {@link TimeZone}。
     * </p>
     *
     * @return 时区。
     */
    TimeZone getTimeZone();

    /**
     * <p>Gets the locale used by this parser.</p>
     *
     * @return the locale
     */
    /**
     * <p>获取此解析器使用的区域设置。</p>
     *
     * @return 区域设置。
     */
    Locale getLocale();

    /**
     * Parses text from a string to produce a Date.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return a <code>java.util.Date</code> object
     * @throws ParseException if the beginning of the specified string cannot be parsed.
     * @see java.text.DateFormat#parseObject(String)
     */
    /**
     * 从字符串中解析文本以生成 Date 对象。
     *
     * @param source 需要被解析的字符串的开头部分。
     * @return 一个 <code>java.util.Date</code> 对象。
     * @throws ParseException 如果指定字符串的开头无法被解析。
     * @see java.text.DateFormat#parseObject(String)
     */
    Object parseObject(String source) throws ParseException;

    /**
     * Parses a date/time string according to the given parse position.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @param pos the parse position
     * @return a <code>java.util.Date</code> object
     * @see java.text.DateFormat#parseObject(String, ParsePosition)
     */
    /**
     * 根据给定的解析位置解析日期/时间字符串。
     *
     * @param source 需要被解析的字符串的开头部分。
     * @param pos 解析位置。
     * @return 一个 <code>java.util.Date</code> 对象。
     * @see java.text.DateFormat#parseObject(String, ParsePosition)
     */
    Object parseObject(String source, ParsePosition pos);
}
