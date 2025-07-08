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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.core.util.Throwables;

/**
 * <p>FastDatePrinter is a fast and thread-safe version of
 * {@link java.text.SimpleDateFormat}.</p>
 * <p>FastDatePrinter 是 {@link java.text.SimpleDateFormat} 的一个快速且线程安全的版本。</p>
 *
 * <p>To obtain a FastDatePrinter, use {@link FastDateFormat#getInstance(String, TimeZone, Locale)}
 * or another variation of the factory methods of {@link FastDateFormat}.</p>
 * <p>要获取 FastDatePrinter 实例，请使用 {@link FastDateFormat#getInstance(String, TimeZone, Locale)}
 * 或 {@link FastDateFormat} 的其他工厂方法。</p>
 *
 * <p>Since FastDatePrinter is thread safe, you can use a static member instance:</p>
 * <p>由于 FastDatePrinter 是线程安全的，因此可以使用静态成员实例：</p>
 * <code>
 *     private static final DatePrinter DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd");
 * </code>
 *
 * <p>This class can be used as a direct replacement to
 * {@code SimpleDateFormat} in most formatting situations.
 * This class is especially useful in multi-threaded server environments.
 * {@code SimpleDateFormat} is not thread-safe in any JDK version,
 * nor will it be as Sun have closed the bug/RFE.
 * </p>
 * <p>在大多数格式化场景中，这个类可以直接替代 {@code SimpleDateFormat}。
 * 这个类在多线程服务器环境中特别有用。
 * {@code SimpleDateFormat} 在任何 JDK 版本中都不是线程安全的，
 * 而且由于 Sun 公司已经关闭了相关的 bug/RFE，它也不会变得线程安全。
 * </p>
 *
 * <p>Only formatting is supported by this class, but all patterns are compatible with
 * SimpleDateFormat (except time zones and some year patterns - see below).</p>
 * <p>这个类只支持格式化，但所有模式都与 SimpleDateFormat 兼容（除了时区和一些年份模式 - 见下文）。</p>
 *
 * <p>Java 1.4 introduced a new pattern letter, {@code 'Z'}, to represent
 * time zones in RFC822 format (eg. {@code +0800} or {@code -1100}).
 * This pattern letter can be used here (on all JDK versions).</p>
 * <p>Java 1.4 引入了一个新的模式字母 {@code 'Z'}，用于表示 RFC822 格式的时区（例如 {@code +0800} 或 {@code -1100}）。
 * 这个模式字母可以在这里使用（适用于所有 JDK 版本）。</p>
 *
 * <p>In addition, the pattern {@code 'ZZ'} has been made to represent
 * ISO 8601 extended format time zones (eg. {@code +08:00} or {@code -11:00}).
 * This introduces a minor incompatibility with Java 1.4, but at a gain of
 * useful functionality.</p>
 * <p>此外，模式 {@code 'ZZ'} 用于表示 ISO 8601 扩展格式的时区（例如 {@code +08:00} 或 {@code -11:00}）。
 * 这引入了与 Java 1.4 的轻微不兼容性，但获得了有用的功能。</p>
 *
 * <p>Starting with JDK7, ISO 8601 support was added using the pattern {@code 'X'}.
 * To maintain compatibility, {@code 'ZZ'} will continue to be supported, but using
 * one of the {@code 'X'} formats is recommended.
 * <p>从 JDK7 开始，使用模式 {@code 'X'} 添加了对 ISO 8601 的支持。
 * 为了保持兼容性，将继续支持 {@code 'ZZ'}，但建议使用 {@code 'X'} 格式之一。</p>
 *
 * <p>Javadoc cites for the year pattern: <i>For formatting, if the number of
 * pattern letters is 2, the year is truncated to 2 digits; otherwise it is
 * interpreted as a number.</i> Starting with Java 1.7 a pattern of 'Y' or
 * 'YYY' will be formatted as '2003', while it was '03' in former Java
 * versions. FastDatePrinter implements the behavior of Java 7.</p>
 * <p>Javadoc 对年份模式的说明：<i>对于格式化，如果模式字母的数量是 2，则年份截断为 2 位；否则将其解释为数字。</i>
 * 从 Java 1.7 开始，模式 'Y' 或 'YYY' 将格式化为 '2003'，而在以前的 Java 版本中是 '03'。
 * FastDatePrinter 实现了 Java 7 的行为。</p>
 *
 * <p>
 * Copied and modified from <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>.
 * </p>
 * <p>
 * 从 <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a> 复制并修改。
 * </p>
 *
 * @since Apache Commons Lang 3.2
 * @see FastDateParser
 */
public class FastDatePrinter implements DatePrinter, Serializable {
    // A lot of the speed in this class comes from caching, but some comes
    // from the special int to StringBuffer conversion.
    // 这个类的很多速度来自于缓存，但也有一些来自于特殊的 int 到 StringBuffer 的转换。
    //
    // The following produces a padded 2 digit number:
    // 以下代码生成一个带填充的两位数：
    //   buffer.append((char)(value / 10 + '0'));
    //   buffer.append((char)(value % 10 + '0'));
    //
    // Note that the fastest append to StringBuffer is a single char (used here).
    // 注意，向 StringBuffer 追加的最快方式是单个字符（此处使用）。
    // Note that Integer.toString() is not called, the conversion is simply
    // taking the value and adding (mathematically) the ASCII value for '0'.
    // 请注意，没有调用 Integer.toString()，转换只是将值与 ASCII '0' 的值相加（数学上）。
    // So, don't change this code! It works and is very fast.
    // 所以，不要改变这段代码！它有效而且非常快。

    /**
     * Required for serialization support.
     * 序列化支持所必需。
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * FULL locale dependent date or time style.
     * 完全本地化相关的日期或时间样式。
     */
    public static final int FULL = DateFormat.FULL;
    /**
     * LONG locale dependent date or time style.
     * 长本地化相关的日期或时间样式。
     */
    public static final int LONG = DateFormat.LONG;
    /**
     * MEDIUM locale dependent date or time style.
     * 中等本地化相关的日期或时间样式。
     */
    public static final int MEDIUM = DateFormat.MEDIUM;
    /**
     * SHORT locale dependent date or time style.
     * 短本地化相关的日期或时间样式。
     */
    public static final int SHORT = DateFormat.SHORT;

    /**
     * The pattern.
     * 日期时间格式模式。
     */
    private final String mPattern;
    /**
     * The time zone.
     * 时区。
     */
    private final TimeZone mTimeZone;
    /**
     * The locale.
     * 语言环境。
     */
    private final Locale mLocale;
    /**
     * The parsed rules.
     * 解析后的规则数组。
     */
    private transient Rule[] mRules;
    /**
     * The estimated maximum length.
     * 估计的最大长度。
     */
    private transient int mMaxLengthEstimate;

    // Constructor
    // 构造函数
    //-----------------------------------------------------------------------
    /**
     * <p>Constructs a new FastDatePrinter.</p>
     * <p>构造一个新的 FastDatePrinter 实例。</p>
     * Use {@link FastDateFormat#getInstance(String, TimeZone, Locale)}  or another variation of the
     * factory methods of {@link FastDateFormat} to get a cached FastDatePrinter instance.
     * 使用 {@link FastDateFormat#getInstance(String, TimeZone, Locale)} 或 {@link FastDateFormat} 的其他工厂方法来获取缓存的 FastDatePrinter 实例。
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式字符串
     * @param timeZone  non-null time zone to use
     * 要使用的非空时区
     * @param locale  non-null locale to use
     * 要使用的非空语言环境
     * @throws NullPointerException if pattern, timeZone, or locale is null.
     * 如果 pattern, timeZone 或 locale 为 null，则抛出 NullPointerException。
     */
    protected FastDatePrinter(final String pattern, final TimeZone timeZone, final Locale locale) {
        mPattern = pattern; // 初始化模式
        mTimeZone = timeZone; // 初始化时区
        mLocale = locale;     // 初始化语言环境

        init(); // 调用初始化方法
    }

    /**
     * <p>Initializes the instance for first use.</p>
     * <p>初始化实例以供首次使用。</p>
     */
    private void init() {
        final List<Rule> rulesList = parsePattern(); // 解析模式，获取规则列表
        mRules = rulesList.toArray(Rule.EMPTY_ARRAY); // 将规则列表转换为数组

        int len = 0; // 初始化长度估计值
        for (int i=mRules.length; --i >= 0; ) { // 遍历规则数组，计算总长度
            len += mRules[i].estimateLength(); // 累加每个规则的估计长度
        }

        mMaxLengthEstimate = len; // 设置最大长度估计值
    }

    // Parse the pattern
    // 解析模式
    //-----------------------------------------------------------------------
    /**
     * <p>Returns a list of Rules given a pattern.</p>
     * <p>根据给定的模式返回规则列表。</p>
     *
     * @return a {@code List} of Rule objects
     * Rule 对象的 {@code List}
     * @throws IllegalArgumentException if pattern is invalid
     * 如果模式无效，则抛出 IllegalArgumentException
     */
    protected List<Rule> parsePattern() {
        final DateFormatSymbols symbols = new DateFormatSymbols(mLocale); // 根据语言环境获取日期格式符号
        final List<Rule> rules = new ArrayList<>(); // 创建一个存储规则的列表

        final String[] ERAs = symbols.getEras(); // 获取纪元字符串数组
        final String[] months = symbols.getMonths(); // 获取月份字符串数组
        final String[] shortMonths = symbols.getShortMonths(); // 获取缩写月份字符串数组
        final String[] weekdays = symbols.getWeekdays(); // 获取星期字符串数组
        final String[] shortWeekdays = symbols.getShortWeekdays(); // 获取缩写星期字符串数组
        final String[] AmPmStrings = symbols.getAmPmStrings(); // 获取上午/下午字符串数组

        final int length = mPattern.length(); // 获取模式字符串的长度
        final int[] indexRef = new int[1]; // 用于传递当前解析位置的引用数组

        for (int i = 0; i < length; i++) { // 遍历模式字符串
            indexRef[0] = i; // 更新当前解析位置
            final String token = parseToken(mPattern, indexRef); // 解析当前位置的令牌
            i = indexRef[0]; // 更新循环变量为解析后的位置

            final int tokenLen = token.length(); // 获取令牌的长度
            if (tokenLen == 0) { // 如果令牌长度为0，说明已经解析完毕或者遇到空令牌
                break; // 跳出循环
            }

            Rule rule; // 声明一个规则变量
            final char c = token.charAt(0); // 获取令牌的第一个字符

            switch (c) { // 根据令牌的第一个字符判断规则类型
            case 'G': // era designator (text) - 纪元标记符（文本）
                rule = new TextField(Calendar.ERA, ERAs); // 创建纪元文本字段规则
                break;
            case 'y': // year (number) - 年份（数字）
            case 'Y': // week year - 周年份
                if (tokenLen == 2) { // 如果令牌长度为2，表示两位数年份
                    rule = TwoDigitYearField.INSTANCE; // 使用两位数年份规则
                } else { // 否则，根据令牌长度选择合适的数字规则
                    rule = selectNumberRule(Calendar.YEAR, tokenLen < 4 ? 4 : tokenLen);
                }
                if (c == 'Y') { // 如果是周年份
                    rule = new WeekYear((NumberRule) rule); // 创建周年份规则，包装原有数字规则
                }
                break;
            case 'M': // month in year (text and number) - 年份中的月份（文本和数字）
                if (tokenLen >= 4) { // 如果令牌长度大于等于4，表示完整月份名称
                    rule = new TextField(Calendar.MONTH, months); // 使用完整月份名称文本字段规则
                } else if (tokenLen == 3) { // 如果令牌长度为3，表示缩写月份名称
                    rule = new TextField(Calendar.MONTH, shortMonths); // 使用缩写月份名称文本字段规则
                } else if (tokenLen == 2) { // 如果令牌长度为2，表示两位数月份
                    rule = TwoDigitMonthField.INSTANCE; // 使用两位数月份规则
                } else { // 否则，表示无填充月份
                    rule = UnpaddedMonthField.INSTANCE; // 使用无填充月份规则
                }
                break;
            case 'd': // day in month (number) - 月份中的日期（数字）
                rule = selectNumberRule(Calendar.DAY_OF_MONTH, tokenLen); // 根据令牌长度选择日期数字规则
                break;
            case 'h': // hour in am/pm (number, 1..12) - 上午/下午时（数字，1..12）
                rule = new TwelveHourField(selectNumberRule(Calendar.HOUR, tokenLen)); // 创建12小时制规则，包装小时数字规则
                break;
            case 'H': // hour in day (number, 0..23) - 一天中的小时（数字，0..23）
                rule = selectNumberRule(Calendar.HOUR_OF_DAY, tokenLen); // 根据令牌长度选择小时数字规则
                break;
            case 'm': // minute in hour (number) - 小时中的分钟（数字）
                rule = selectNumberRule(Calendar.MINUTE, tokenLen); // 根据令牌长度选择分钟数字规则
                break;
            case 's': // second in minute (number) - 分钟中的秒（数字）
                rule = selectNumberRule(Calendar.SECOND, tokenLen); // 根据令牌长度选择秒数字规则
                break;
            case 'S': // millisecond (number) - 毫秒（数字）
                rule = selectNumberRule(Calendar.MILLISECOND, tokenLen); // 根据令牌长度选择毫秒数字规则
                break;
            case 'E': // day in week (text) - 周中的天（文本）
                rule = new TextField(Calendar.DAY_OF_WEEK, tokenLen < 4 ? shortWeekdays : weekdays); // 根据令牌长度选择缩写或完整星期名称文本字段规则
                break;
            case 'u': // day in week (number) - 周中的天（数字）
                rule = new DayInWeekField(selectNumberRule(Calendar.DAY_OF_WEEK, tokenLen)); // 创建周中的天数字规则，包装星期数字规则
                break;
            case 'D': // day in year (number) - 年份中的天（数字）
                rule = selectNumberRule(Calendar.DAY_OF_YEAR, tokenLen); // 根据令牌长度选择年份中的天数字规则
                break;
            case 'F': // day of week in month (number) - 月份中周的天数（数字）
                rule = selectNumberRule(Calendar.DAY_OF_WEEK_IN_MONTH, tokenLen); // 根据令牌长度选择月份中周的天数数字规则
                break;
            case 'w': // week in year (number) - 年份中的周（数字）
                rule = selectNumberRule(Calendar.WEEK_OF_YEAR, tokenLen); // 根据令牌长度选择年份中的周数字规则
                break;
            case 'W': // week in month (number) - 月份中的周（数字）
                rule = selectNumberRule(Calendar.WEEK_OF_MONTH, tokenLen); // 根据令牌长度选择月份中的周数字规则
                break;
            case 'a': // am/pm marker (text) - 上午/下午标记（文本）
                rule = new TextField(Calendar.AM_PM, AmPmStrings); // 创建上午/下午文本字段规则
                break;
            case 'k': // hour in day (1..24) - 一天中的小时（1..24）
                rule = new TwentyFourHourField(selectNumberRule(Calendar.HOUR_OF_DAY, tokenLen)); // 创建24小时制规则，包装小时数字规则
                break;
            case 'K': // hour in am/pm (0..11) - 上午/下午时（0..11）
                rule = selectNumberRule(Calendar.HOUR, tokenLen); // 根据令牌长度选择小时数字规则
                break;
            case 'X': // ISO 8601
                rule = Iso8601_Rule.getRule(tokenLen); // 获取ISO 8601规则
                break;
            case 'z': // time zone (text) - 时区（文本）
                if (tokenLen >= 4) { // 如果令牌长度大于等于4，表示完整时区名称
                    rule = new TimeZoneNameRule(mTimeZone, mLocale, TimeZone.LONG); // 使用完整时区名称规则
                } else { // 否则，表示缩写时区名称
                    rule = new TimeZoneNameRule(mTimeZone, mLocale, TimeZone.SHORT); // 使用缩写时区名称规则
                }
                break;
            case 'Z': // time zone (value) - 时区（值）
                if (tokenLen == 1) { // 如果令牌长度为1，表示不带冒号的时区偏移量
                    rule = TimeZoneNumberRule.INSTANCE_NO_COLON; // 使用不带冒号的时区数字规则
                } else if (tokenLen == 2) { // 如果令牌长度为2，表示ISO 8601小时冒号分钟格式
                    rule = Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES; // 使用ISO 8601小时冒号分钟规则
                } else { // 否则，表示带冒号的时区偏移量
                    rule = TimeZoneNumberRule.INSTANCE_COLON; // 使用带冒号的时区数字规则
                }
                break;
            case '\'': // literal text - 字面文本
                final String sub = token.substring(1); // 获取除第一个单引号外的子字符串
                if (sub.length() == 1) { // 如果子字符串长度为1，表示单个字符字面量
                    rule = new CharacterLiteral(sub.charAt(0)); // 创建字符字面量规则
                } else { // 否则，表示字符串字面量
                    rule = new StringLiteral(sub); // 创建字符串字面量规则
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal pattern component: " + token); // 抛出非法模式组件异常
            }

            rules.add(rule); // 将解析出的规则添加到列表中
        }

        return rules; // 返回规则列表
    }

    /**
     * <p>Performs the parsing of tokens.</p>
     * <p>执行令牌的解析。</p>
     *
     * @param pattern  the pattern
     * 模式字符串
     * @param indexRef  index references
     * 索引引用（用于传递当前解析位置）
     * @return parsed token
     * 解析出的令牌
     */
    protected String parseToken(final String pattern, final int[] indexRef) {
        final StringBuilder buf = new StringBuilder(); // 用于构建令牌字符串的 StringBuilder

        int i = indexRef[0]; // 获取当前解析的起始索引
        final int length = pattern.length(); // 获取模式字符串的长度

        char c = pattern.charAt(i); // 获取当前字符
        if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') { // 如果当前字符是字母（表示时间模式）
            // Scan a run of the same character, which indicates a time
            // pattern.
            // 扫描相同字符的连续序列，这表示一个时间模式。
            buf.append(c); // 将当前字符添加到缓冲区

            while (i + 1 < length) { // 继续扫描直到模式结束或遇到不同字符
                final char peek = pattern.charAt(i + 1); // 查看下一个字符
                if (peek == c) { // 如果下一个字符与当前字符相同
                    buf.append(c); // 将其添加到缓冲区
                    i++; // 移动到下一个字符
                } else {
                    break; // 遇到不同字符，停止扫描
                }
            }
        } else {
            // This will identify token as text.
            // 这将把令牌识别为文本。
            buf.append('\''); // 在缓冲区中添加一个单引号，表示字面量文本的开始

            boolean inLiteral = false; // 标记是否在字面量内部

            for (; i < length; i++) { // 继续遍历模式字符串
                c = pattern.charAt(i); // 获取当前字符

                if (c == '\'') { // 如果当前字符是单引号
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                        // '' is treated as escaped '
                        // '' 被视为转义的单引号 '
                        i++; // 跳过下一个单引号
                        buf.append(c); // 将单个单引号添加到缓冲区
                    } else {
                        inLiteral = !inLiteral; // 切换字面量状态
                    }
                } else if (!inLiteral &&
                         (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')) { // 如果不在字面量内部且遇到字母
                    i--; // 将索引回退一位，因为这个字母是下一个令牌的开始
                    break; // 停止扫描
                } else {
                    buf.append(c); // 将字符添加到缓冲区（普通文本）
                }
            }
        }

        indexRef[0] = i; // 更新索引引用，指向解析结束的位置
        return buf.toString(); // 返回解析出的令牌字符串
    }

    /**
     * <p>Gets an appropriate rule for the padding required.</p>
     * <p>获取所需填充的适当规则。</p>
     *
     * @param field  the field to get a rule for
     * 要获取规则的日历字段
     * @param padding  the padding required
     * 所需的填充长度
     * @return a new rule with the correct padding
     * 具有正确填充的新规则
     */
    protected NumberRule selectNumberRule(final int field, final int padding) {
        switch (padding) { // 根据填充长度选择不同的数字规则
        case 1: // 填充长度为1，表示无填充
            return new UnpaddedNumberField(field);
        case 2: // 填充长度为2，表示两位数填充
            return new TwoDigitNumberField(field);
        default: // 默认情况，表示带填充的数字
            return new PaddedNumberField(field, padding);
        }
    }

    // Format methods
    // 格式化方法
    //-----------------------------------------------------------------------
    /**
     * <p>Formats a {@code Date}, {@code Calendar} or
     * {@code Long} (milliseconds) object.</p>
     * <p>格式化 {@code Date}、{@code Calendar} 或 {@code Long}（毫秒）对象。</p>
     * @deprecated Use {{@link #format(Date)}, {{@link #format(Calendar)}, {{@link #format(long)}, or {{@link #format(Object)}
     * 请使用 {{@link #format(Date)}、 {{@link #format(Calendar)}、 {{@link #format(long)} 或 {{@link #format(Object)}
     * @param obj  the object to format
     * 要格式化的对象
     * @param toAppendTo  the buffer to append to
     * 要追加到的缓冲区
     * @param pos  the position - ignored
     * 位置 - 忽略
     * @return the buffer passed in
     * 传入的缓冲区
     */
    @Deprecated
    @Override
    public StringBuilder format(final Object obj, final StringBuilder toAppendTo, final FieldPosition pos) {
        if (obj instanceof Date) { // 如果对象是 Date 类型
            return format((Date) obj, toAppendTo); // 格式化 Date 对象
        } else if (obj instanceof Calendar) { // 如果对象是 Calendar 类型
            return format((Calendar) obj, toAppendTo); // 格式化 Calendar 对象
        } else if (obj instanceof Long) { // 如果对象是 Long 类型
            return format(((Long) obj).longValue(), toAppendTo); // 格式化 Long 值
        } else { // 其他未知类型
            throw new IllegalArgumentException("Unknown class: " + // 抛出非法参数异常
                (obj == null ? "<null>" : obj.getClass().getName()));
        }
    }

    /**
     * <p>Formats a {@code Date}, {@code Calendar} or
     * {@code Long} (milliseconds) object.</p>
     * <p>格式化 {@code Date}、{@code Calendar} 或 {@code Long}（毫秒）对象。</p>
     * @since 3.5
     * @param obj  the object to format
     * 要格式化的对象
     * @return The formatted value.
     * 格式化后的值。
     */
    String format(final Object obj) {
        if (obj instanceof Date) { // 如果对象是 Date 类型
            return format((Date) obj); // 格式化 Date 对象
        } else if (obj instanceof Calendar) { // 如果对象是 Calendar 类型
            return format((Calendar) obj); // 格式化 Calendar 对象
        } else if (obj instanceof Long) { // 如果对象是 Long 类型
            return format(((Long) obj).longValue()); // 格式化 Long 值
        } else { // 其他未知类型
            throw new IllegalArgumentException("Unknown class: " + // 抛出非法参数异常
                (obj == null ? "<null>" : obj.getClass().getName()));
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(long)
     * @see org.apache.commons.lang3.time.DatePrinter#format(long)
     */
    @Override
    public String format(final long millis) {
        final Calendar c = newCalendar(); // 创建新的 Calendar 实例
        c.setTimeInMillis(millis); // 设置时间为传入的毫秒值
        return applyRulesToString(c); // 应用规则并返回格式化后的字符串
    }

    /**
     * Creates a String representation of the given Calendar by applying the rules of this printer to it.
     * 通过将此打印机的规则应用于给定的 Calendar，创建其字符串表示形式。
     * @param c the Calender to apply the rules to.
     * 要应用规则的 Calendar。
     * @return a String representation of the given Calendar.
     * 给定 Calendar 的字符串表示形式。
     */
    private String applyRulesToString(final Calendar c) {
        return applyRules(c, new StringBuilder(mMaxLengthEstimate)).toString(); // 应用规则到 StringBuilder 并转换为字符串
    }

    /**
     * Creation method for new calender instances.
     * 用于创建新的日历实例的方法。
     * @return a new Calendar instance.
     * 一个新的 Calendar 实例。
     */
    private Calendar newCalendar() {
        return Calendar.getInstance(mTimeZone, mLocale); // 根据时区和语言环境获取 Calendar 实例
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date)
     */
    @Override
    public String format(final Date date) {
        final Calendar c = newCalendar(); // 创建新的 Calendar 实例
        c.setTime(date); // 设置时间为传入的 Date 对象
        return applyRulesToString(c); // 应用规则并返回格式化后的字符串
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar)
     */
    @Override
    public String format(final Calendar calendar) {
        return format(calendar, new StringBuilder(mMaxLengthEstimate)).toString(); // 格式化 Calendar 对象并返回字符串
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(long, java.lang.Appendable)
     * @see org.apache.commons.lang3.time.DatePrinter#format(long, java.lang.Appendable)
     */
    @Override
    public <B extends Appendable> B format(final long millis, final B buf) {
        final Calendar c = newCalendar(); // 创建新的 Calendar 实例
        c.setTimeInMillis(millis); // 设置时间为传入的毫秒值
        return applyRules(c, buf); // 应用规则并返回缓冲区
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date, java.lang.Appendable)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Date, java.lang.Appendable)
     */
    @Override
    public <B extends Appendable> B format(final Date date, final B buf) {
        final Calendar c = newCalendar(); // 创建新的 Calendar 实例
        c.setTime(date); // 设置时间为传入的 Date 对象
        return applyRules(c, buf); // 应用规则并返回缓冲区
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar, java.lang.Appendable)
     * @see org.apache.commons.lang3.time.DatePrinter#format(java.util.Calendar, java.lang.Appendable)
     */
    @Override
    public <B extends Appendable> B format(Calendar calendar, final B buf) {
        // do not pass in calendar directly, this will cause TimeZone of FastDatePrinter to be ignored
        // 不要直接传入 calendar，这会导致 FastDatePrinter 的 TimeZone 被忽略
        if(!calendar.getTimeZone().equals(mTimeZone)) { // 如果传入的 Calendar 的时区与当前 FastDatePrinter 的时区不同
            calendar = (Calendar)calendar.clone(); // 克隆 Calendar 对象
            calendar.setTimeZone(mTimeZone); // 设置克隆对象的时区为当前 FastDatePrinter 的时区
        }
        return applyRules(calendar, buf); // 应用规则并返回缓冲区
    }

    /**
     * Performs the formatting by applying the rules to the
     * specified calendar.
     * 通过将规则应用于指定的日历来执行格式化。
     *
     * @param calendar the calendar to format
     * 要格式化的日历
     * @param buf the buffer to format into
     * 要格式化到的缓冲区
     * @return the specified string buffer
     * 指定的字符串缓冲区
     *
     * @deprecated use {@link #format(Calendar)} or {@link #format(Calendar, Appendable)}
     * 请使用 {@link #format(Calendar)} 或 {@link #format(Calendar, Appendable)}
     */
    @Deprecated
    protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
        return (StringBuffer) applyRules(calendar, (Appendable)buf); // 将 StringBuffer 转换为 Appendable 后应用规则
    }

    /**
     * <p>Performs the formatting by applying the rules to the
     * specified calendar.</p>
     * <p>通过将规则应用于指定的日历来执行格式化。</p>
     *
     * @param calendar  the calendar to format
     * 要格式化的日历
     * @param buf  the buffer to format into
     * 要格式化到的缓冲区
     * @param <B> the Appendable class type, usually StringBuilder or StringBuffer.
     * Appendable 类类型，通常是 StringBuilder 或 StringBuffer。
     * @return the specified string buffer
     * 指定的字符串缓冲区
     */
    private <B extends Appendable> B applyRules(final Calendar calendar, final B buf) {
        try {
            for (final Rule rule : mRules) { // 遍历所有规则
                rule.appendTo(buf, calendar); // 将日历的值根据规则追加到缓冲区
            }
        } catch (final IOException ioe) { // 捕获 IO 异常
            Throwables.rethrow(ioe); // 重新抛出异常
        }
        return buf; // 返回缓冲区
    }

    // Accessors
    // 访问器
    //-----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#getPattern()
     * @see org.apache.commons.lang3.time.DatePrinter#getPattern()
     */
    @Override
    public String getPattern() {
        return mPattern; // 返回模式字符串
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#getTimeZone()
     * @see org.apache.commons.lang3.time.DatePrinter#getTimeZone()
     */
    @Override
    public TimeZone getTimeZone() {
        return mTimeZone; // 返回时区
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DatePrinter#getLocale()
     * @see org.apache.commons.lang3.time.DatePrinter#getLocale()
     */
    @Override
    public Locale getLocale() {
        return mLocale; // 返回语言环境
    }

    /**
     * <p>Gets an estimate for the maximum string length that the
     * formatter will produce.</p>
     * <p>获取格式化程序将产生的最大字符串长度的估计值。</p>
     *
     * <p>The actual formatted length will almost always be less than or
     * equal to this amount.</p>
     * <p>实际格式化的长度几乎总是小于或等于此值。</p>
     *
     * @return the maximum formatted length
     * 最大格式化长度
     */
    public int getMaxLengthEstimate() {
        return mMaxLengthEstimate; // 返回最大长度估计值
    }

    // Basics
    // 基础方法
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two objects for equality.</p>
     * <p>比较两个对象是否相等。</p>
     *
     * @param obj  the object to compare to
     * 要比较的对象
     * @return {@code true} if equal
     * 如果相等则为 {@code true}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FastDatePrinter == false) { // 如果不是 FastDatePrinter 的实例
            return false; // 返回 false
        }
        final FastDatePrinter other = (FastDatePrinter) obj; // 将对象转换为 FastDatePrinter 类型
        return mPattern.equals(other.mPattern) // 比较模式是否相等
            && mTimeZone.equals(other.mTimeZone) // 比较时区是否相等
            && mLocale.equals(other.mLocale); // 比较语言环境是否相等
    }

    /**
     * <p>Returns a hash code compatible with equals.</p>
     * <p>返回与 equals 兼容的哈希码。</p>
     *
     * @return a hash code compatible with equals
     * 与 equals 兼容的哈希码
     */
    @Override
    public int hashCode() {
        return mPattern.hashCode() + 13 * (mTimeZone.hashCode() + 13 * mLocale.hashCode()); // 计算哈希码
    }

    /**
     * <p>Gets a debugging string version of this formatter.</p>
     * <p>获取此格式化程序的调试字符串版本。</p>
     *
     * @return a debugging string
     * 调试字符串
     */
    @Override
    public String toString() {
        return "FastDatePrinter[" + mPattern + "," + mLocale + "," + mTimeZone.getID() + "]"; // 返回格式化程序的字符串表示
    }

    // Serializing
    // 序列化
    //-----------------------------------------------------------------------
    /**
     * Create the object after serialization. This implementation reinitializes the
     * transient properties.
     * 在序列化后创建对象。此实现会重新初始化瞬态属性。
     *
     * @param in ObjectInputStream from which the object is being deserialized.
     * 从中反序列化对象的 ObjectInputStream。
     * @throws IOException if there is an IO issue.
     * 如果存在 IO 问题。
     * @throws ClassNotFoundException if a class cannot be found.
     * 如果找不到类。
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // 调用默认的反序列化方法
        init(); // 重新初始化瞬态属性
    }

    /**
     * Appends two digits to the given buffer.
     * 将两位数字追加到给定的缓冲区。
     *
     * @param buffer the buffer to append to.
     * 要追加到的缓冲区。
     * @param value the value to append digits from.
     * 要从中追加数字的值。
     */
    private static void appendDigits(final Appendable buffer, final int value) throws IOException {
        buffer.append((char)(value / 10 + '0')); // 追加十位数字
        buffer.append((char)(value % 10 + '0')); // 追加个位数字
    }

    private static final int MAX_DIGITS = 10; // log10(Integer.MAX_VALUE) ~= 9.3
    // 最大数字位数，根据 Integer.MAX_VALUE 的对数（以10为底）估算约为 9.3

    /**
     * Appends all digits to the given buffer.
     * 将所有数字追加到给定的缓冲区。
     *
     * @param buffer the buffer to append to.
     * 要追加到的缓冲区。
     * @param value the value to append digits from.
     * 要从中追加数字的值。
     */
    private static void appendFullDigits(final Appendable buffer, int value, int minFieldWidth) throws IOException {
        // specialized paths for 1 to 4 digits -> avoid the memory allocation from the temporary work array
        // 针对 1 到 4 位数字的专用路径 -> 避免临时工作数组的内存分配
        // see LANG-1248
        // 参考 LANG-1248
        if (value < 10000) { // 如果值小于 10000（即最多四位数）
            // less memory allocation path works for four digits or less
            // 内存分配较少的路径适用于四位或更少的数字

            int nDigits = 4; // 初始化数字位数为 4
            if (value < 1000) { // 如果值小于 1000
                --nDigits; // 数字位数减 1
                if (value < 100) { // 如果值小于 100
                    --nDigits; // 数字位数减 1
                    if (value < 10) { // 如果值小于 10
                        --nDigits; // 数字位数减 1
                    }
                }
            }
            // left zero pad
            // 左侧补零
            for (int i = minFieldWidth - nDigits; i > 0; --i) { // 根据最小字段宽度和实际数字位数计算需要补零的数量
                buffer.append('0'); // 追加 '0' 进行填充
            }

            switch (nDigits) { // 根据实际数字位数进行追加
            case 4: // 四位数
                buffer.append((char) (value / 1000 + '0')); // 追加千位
                value %= 1000; // 更新值为剩余的百、十、个位
            case 3: // 三位数
                if (value >= 100) { // 如果有百位
                    buffer.append((char) (value / 100 + '0')); // 追加百位
                    value %= 100; // 更新值为剩余的十、个位
                } else {
                    buffer.append('0'); // 否则补 '0'
                }
            case 2: // 两位数
                if (value >= 10) { // 如果有十位
                    buffer.append((char) (value / 10 + '0')); // 追加十位
                    value %= 10; // 更新值为剩余的个位
                } else {
                    buffer.append('0'); // 否则补 '0'
                }
            case 1: // 一位数
                buffer.append((char) (value + '0')); // 追加个位
            }
        } else {
            // more memory allocation path works for any digits
            // 更多内存分配的路径适用于任意位数的数字

            // build up decimal representation in reverse
            // 以反向顺序构建十进制表示
            final char[] work = new char[MAX_DIGITS]; // 创建一个字符数组用于存储数字的每一位
            int digit = 0; // 初始化数字位数计数器
            while (value != 0) { // 当值不为0时循环
                work[digit++] = (char) (value % 10 + '0'); // 获取个位数字并转换为字符，存储到数组中，然后递增位数计数器
                value = value / 10; // 将值除以10，去除已处理的个位
            }

            // pad with zeros
            // 补零
            while (digit < minFieldWidth) { // 如果当前数字位数小于最小字段宽度
                buffer.append('0'); // 追加 '0' 进行填充
                --minFieldWidth; // 减少最小字段宽度
            }

            // reverse
            // 反转并追加
            while (--digit >= 0) { // 从数组末尾开始遍历
                buffer.append(work[digit]); // 将字符从数组中追加到缓冲区
            }
        }
    }

    // Rules
    // 规则
    //-----------------------------------------------------------------------
    /**
     * <p>Inner class defining a rule.</p>
     * <p>定义规则的内部接口。</p>
     */
    private interface Rule {
        
        static final Rule[] EMPTY_ARRAY = {}; // 空规则数组，用于将列表转换为数组

        /**
         * Returns the estimated length of the result.
         * 返回结果的估计长度。
         *
         * @return the estimated length
         * 估计长度
         */
        int estimateLength();

        /**
         * Appends the value of the specified calendar to the output buffer based on the rule implementation.
         * 根据规则实现，将指定日历的值追加到输出缓冲区。
         *
         * @param buf the output buffer
         * 输出缓冲区
         * @param calendar calendar to be appended
         * 要追加的日历
         * @throws IOException if an I/O error occurs
         * 如果发生 I/O 错误
         */
        void appendTo(Appendable buf, Calendar calendar) throws IOException;
    }

    /**
     * <p>Inner class defining a numeric rule.</p>
     * <p>定义数字规则的内部接口。</p>
     */
    private interface NumberRule extends Rule {
        /**
         * Appends the specified value to the output buffer based on the rule implementation.
         * 根据规则实现，将指定值追加到输出缓冲区。
         *
         * @param buffer the output buffer
         * 输出缓冲区
         * @param value the value to be appended
         * 要追加的值
         * @throws IOException if an I/O error occurs
         * 如果发生 I/O 错误
         */
        void appendTo(Appendable buffer, int value) throws IOException;
    }

    /**
     * <p>Inner class to output a constant single character.</p>
     * <p>用于输出单个常量字符的内部类。</p>
     */
    private static class CharacterLiteral implements Rule {
        private final char mValue; // 存储要输出的字符

        /**
         * Constructs a new instance of {@code CharacterLiteral}
         * to hold the specified value.
         * 构造一个新的 {@code CharacterLiteral} 实例以保存指定值。
         *
         * @param value the character literal
         * 字符字面量
         */
        CharacterLiteral(final char value) {
            mValue = value; // 初始化字符值
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#estimateLength()} 方法。
         */
        @Override
        public int estimateLength() {
            return 1; // 字符字面量的估计长度为1
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            buffer.append(mValue); // 将字符追加到缓冲区
        }
    }

    /**
     * <p>Inner class to output a constant string.</p>
     * <p>用于输出常量字符串的内部类。</p>
     */
    private static class StringLiteral implements Rule {
        private final String mValue; // 存储要输出的字符串

        /**
         * Constructs a new instance of {@code StringLiteral}
         * to hold the specified value.
         * 构造一个新的 {@code StringLiteral} 实例以保存指定值。
         *
         * @param value the string literal
         * 字符串字面量
         */
        StringLiteral(final String value) {
            mValue = value; // 初始化字符串值
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#estimateLength()} 方法。
         */
        @Override
        public int estimateLength() {
            return mValue.length(); // 字符串字面量的估计长度是其自身的长度
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            buffer.append(mValue); // 将字符串追加到缓冲区
        }
    }

    /**
     * <p>Inner class to output one of a set of values.</p>
     * <p>用于输出一组值中的一个的内部类。</p>
     */
    private static class TextField implements Rule {
        private final int mField; // 存储日历字段
        private final String[] mValues; // 存储对应字段的值数组（如月份名称、星期名称）

        /**
         * Constructs an instance of {@code TextField}
         * with the specified field and values.
         * 构造一个具有指定字段和值的 {@code TextField} 实例。
         *
         * @param field the field
         * 日历字段
         * @param values the field values
         * 字段值数组
         */
        TextField(final int field, final String[] values) {
            mField = field; // 初始化字段
            mValues = values; // 初始化值数组
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#estimateLength()} 方法。
         */
        @Override
        public int estimateLength() {
            int max = 0; // 初始化最大长度为0
            for (int i=mValues.length; --i >= 0; ) { // 遍历值数组
                final int len = mValues[i].length(); // 获取当前值的长度
                if (len > max) { // 如果当前长度大于最大长度
                    max = len; // 更新最大长度
                }
            }
            return max; // 返回最大长度
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            buffer.append(mValues[calendar.get(mField)]); // 根据日历字段的值，从值数组中获取对应字符串并追加到缓冲区
        }
    }

    /**
     * <p>Inner class to output an unpadded number.</p>
     * <p>用于输出不带填充的数字的内部类。</p>
     */
    private static class UnpaddedNumberField implements NumberRule {
        private final int mField; // 存储日历字段

        /**
         * Constructs an instance of {@code UnpadedNumberField} with the specified field.
         * 构造一个具有指定字段的 {@code UnpadedNumberField} 实例。
         *
         * @param field the field
         * 日历字段
         */
        UnpaddedNumberField(final int field) {
            mField = field; // 初始化字段
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#estimateLength()} 方法。
         */
        @Override
        public int estimateLength() {
            return 4; // 估计长度为4，因为数字可能最大为4位数（如年份）
        }

        /**
         * {@inheritDoc}
         * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(mField)); // 获取日历字段的值并调用 appendTo(Appendable, int) 方法
        }

        /**
         * {@inheritDoc}
         * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
         */
        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            if (value < 10) { // 如果值小于10（个位数）
                buffer.append((char)(value + '0')); // 直接追加字符表示的个位数
            } else if (value < 100) { // 如果值小于100（两位数）
                appendDigits(buffer, value); // 调用 appendDigits 方法追加两位数
            } else { // 如果值大于等于100（多位数）
               appendFullDigits(buffer, value, 1); // 调用 appendFullDigits 方法追加多位数，最小字段宽度为1
            }
        }
    }

    /**
     * <p>Inner class to output an unpadded month.</p>
     * <p>用于输出不带填充的月份的内部类。</p>
     */
    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField(); // 单例实例

        /**
         * Constructs an instance of {@code UnpaddedMonthField}.
         * 构造一个 {@code UnpaddedMonthField} 实例。
         *
         */
        UnpaddedMonthField() {
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 返回月份的最大估计长度，即两位数。
         */
        @Override
        public int estimateLength() {
            return 2;
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 将日历中的月份值（+1是因为Calendar中月份从0开始）追加到缓冲区。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(Calendar.MONTH) + 1);
        }

        /**
         * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 根据传入的月份值，将其转换为字符并追加到缓冲区。如果是个位数，直接追加；否则，按两位数处理。
         */
        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            if (value < 10) {
                buffer.append((char)(value + '0'));
            } else {
                appendDigits(buffer, value);
            }
        }
    }

    /**
     * <p>Inner class to output a padded number.</p>
 * <p>用于输出带填充数字的内部类。</p>
     */
    private static class PaddedNumberField implements NumberRule {
    private final int mField; // 存储日历字段
    private final int mSize; // 存储输出字段的最小尺寸，不足用0填充

        /**
         * Constructs an instance of {@code PaddedNumberField}.
     * 构造一个 {@code PaddedNumberField} 实例。
         *
         * @param field the field
     * 日历字段
         * @param size size of the output field
     * 输出字段的最小尺寸
     * @throws IllegalArgumentException 如果尺寸小于3，则抛出此异常，因为小于3应该使用其他规则。
         */
        PaddedNumberField(final int field, final int size) {
            if (size < 3) {
                // Should use UnpaddedNumberField or TwoDigitNumberField.
            // 对于小于3的尺寸，应该使用 UnpaddedNumberField 或 TwoDigitNumberField。
                throw new IllegalArgumentException();
            }
        mField = field; // 初始化字段
        mSize = size; // 初始化尺寸
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，即填充后的最小尺寸。
         */
        @Override
        public int estimateLength() {
            return mSize;
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 从日历中获取指定字段的值，并将其追加到缓冲区，确保达到指定填充尺寸。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(mField));
        }

        /**
         * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值追加到缓冲区，并使用0进行左填充以达到指定尺寸。
         */
        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            appendFullDigits(buffer, value, mSize);
        }
    }

    /**
     * <p>Inner class to output a two digit number.</p>
 * <p>用于输出两位数字的内部类。</p>
     */
    private static class TwoDigitNumberField implements NumberRule {
    private final int mField; // 存储日历字段

        /**
         * Constructs an instance of {@code TwoDigitNumberField} with the specified field.
     * 构造一个具有指定字段的 {@code TwoDigitNumberField} 实例。
         *
         * @param field the field
     * 日历字段
         */
        TwoDigitNumberField(final int field) {
        mField = field; // 初始化字段
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，固定为2位。
         */
        @Override
        public int estimateLength() {
            return 2;
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 从日历中获取指定字段的值，并将其作为两位数字追加到缓冲区。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(mField));
        }

        /**
         * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值作为两位数字追加到缓冲区。如果值小于100，直接追加；否则，截取后两位追加。
         */
        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            if (value < 100) {
                appendDigits(buffer, value);
            } else {
                appendFullDigits(buffer, value, 2);
            }
        }
    }

    /**
     * <p>Inner class to output a two digit year.</p>
 * <p>用于输出两位年份的内部类。</p>
     */
    private static class TwoDigitYearField implements NumberRule {
    static final TwoDigitYearField INSTANCE = new TwoDigitYearField(); // 单例实例

        /**
         * Constructs an instance of {@code TwoDigitYearField}.
     * 构造一个 {@code TwoDigitYearField} 实例。
         */
        TwoDigitYearField() {
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，固定为2位。
         */
        @Override
        public int estimateLength() {
            return 2;
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 获取日历中的年份，并取模100，将其作为两位数字追加到缓冲区。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(Calendar.YEAR) % 100);
        }

        /**
         * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值作为两位数字追加到缓冲区。
         */
        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            appendDigits(buffer, value);
        }
    }

    /**
     * <p>Inner class to output a two digit month.</p>
 * <p>用于输出两位月份的内部类。</p>
     */
    private static class TwoDigitMonthField implements NumberRule {
    static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField(); // 单例实例

        /**
         * Constructs an instance of {@code TwoDigitMonthField}.
     * 构造一个 {@code TwoDigitMonthField} 实例。
         */
        TwoDigitMonthField() {
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，固定为2位。
         */
        @Override
        public int estimateLength() {
            return 2;
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 获取日历中的月份（+1，因为Calendar中月份从0开始），将其作为两位数字追加到缓冲区。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(Calendar.MONTH) + 1);
        }

        /**
         * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值作为两位数字追加到缓冲区。
         */
        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            appendDigits(buffer, value);
        }
    }

    /**
     * <p>Inner class to output the twelve hour field.</p>
 * <p>用于输出12小时制小时字段的内部类。</p>
     */
    private static class TwelveHourField implements NumberRule {
    private final NumberRule mRule; // 存储实际用于格式化数字的规则

        /**
         * Constructs an instance of {@code TwelveHourField} with the specified
         * {@code NumberRule}.
     * 构造一个具有指定 {@code NumberRule} 的 {@code TwelveHourField} 实例。
         *
         * @param rule the rule
     * 用于格式化数字的规则
         */
        TwelveHourField(final NumberRule rule) {
        mRule = rule; // 初始化数字规则
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，委托给内部的数字规则。
         */
        @Override
        public int estimateLength() {
            return mRule.estimateLength();
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 从日历中获取12小时制的小时值。如果为0，则将其转换为12（如午夜12点），然后委托给内部数字规则进行追加。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            int value = calendar.get(Calendar.HOUR);
            if (value == 0) {
                value = calendar.getLeastMaximum(Calendar.HOUR) + 1;
            }
            mRule.appendTo(buffer, value);
        }

        /**
         * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值委托给内部的数字规则进行追加。
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            mRule.appendTo(buffer, value);
        }
    }

    /**
     * <p>Inner class to output the twenty four hour field.</p>
 * <p>用于输出24小时制小时字段的内部类。</p>
     */
    private static class TwentyFourHourField implements NumberRule {
    private final NumberRule mRule; // 存储实际用于格式化数字的规则

        /**
         * Constructs an instance of {@code TwentyFourHourField} with the specified
         * {@code NumberRule}.
     * 构造一个具有指定 {@code NumberRule} 的 {@code TwentyFourHourField} 实例。
         *
         * @param rule the rule
     * 用于格式化数字的规则
         */
        TwentyFourHourField(final NumberRule rule) {
        mRule = rule; // 初始化数字规则
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，委托给内部的数字规则。
         */
        @Override
        public int estimateLength() {
            return mRule.estimateLength();
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 从日历中获取24小时制的小时值。如果为0，则将其转换为24（如午夜24点），然后委托给内部数字规则进行追加。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            int value = calendar.get(Calendar.HOUR_OF_DAY);
            if (value == 0) {
                value = calendar.getMaximum(Calendar.HOUR_OF_DAY) + 1;
            }
            mRule.appendTo(buffer, value);
        }

        /**
         * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值委托给内部的数字规则进行追加。
         */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            mRule.appendTo(buffer, value);
        }
    }

    /**
     * <p>Inner class to output the numeric day in week.</p>
 * <p>用于输出星期几的数字表示的内部类。</p>
     */
    private static class DayInWeekField implements NumberRule {
    private final NumberRule mRule; // 存储实际用于格式化数字的规则

    /**
     * Constructs an instance of {@code DayInWeekField} with the specified
     * {@code NumberRule}.
     * 构造一个具有指定 {@code NumberRule} 的 {@code DayInWeekField} 实例。
     *
     * @param rule the rule
     * 用于格式化数字的规则
     */
        DayInWeekField(final NumberRule rule) {
        mRule = rule; // 初始化数字规则
        }

    /**
     * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，委托给内部的数字规则。
     */
        @Override
        public int estimateLength() {
            return mRule.estimateLength();
        }

    /**
     * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 从日历中获取星期几的值。如果不是星期日（Calendar.SUNDAY），则减1（将星期一设为1），否则设为7。
     * 然后委托给内部数字规则进行追加。
     */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            final int value = calendar.get(Calendar.DAY_OF_WEEK);
            mRule.appendTo(buffer, value != Calendar.SUNDAY ? value - 1 : 7);
        }

    /**
     * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值委托给内部的数字规则进行追加。
     */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            mRule.appendTo(buffer, value);
        }
    }

    /**
     * <p>Inner class to output the numeric day in week.</p>
 * <p>用于输出周年份的数字表示的内部类。</p>
     */
    private static class WeekYear implements NumberRule {
    private final NumberRule mRule; // 存储实际用于格式化数字的规则

    /**
     * Constructs an instance of {@code WeekYear} with the specified
     * {@code NumberRule}.
     * 构造一个具有指定 {@code NumberRule} 的 {@code WeekYear} 实例。
     *
     * @param rule the rule
     * 用于格式化数字的规则
     */
        WeekYear(final NumberRule rule) {
        mRule = rule; // 初始化数字规则
        }

    /**
     * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，委托给内部的数字规则。
     */
        @Override
        public int estimateLength() {
            return mRule.estimateLength();
        }

    /**
     * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 获取日历中的周年份值，并委托给内部数字规则进行追加。
     */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            mRule.appendTo(buffer, calendar.getWeekYear());
        }

    /**
     * {@inheritDoc}
     * 实现 {@link NumberRule#appendTo(Appendable, int)} 方法。
     * 将整数值委托给内部的数字规则进行追加。
     */
        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            mRule.appendTo(buffer, value);
        }
    }

    //-----------------------------------------------------------------------

    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache =
    new ConcurrentHashMap<>(7); // 线程安全的并发哈希映射，用于缓存时区显示名称，初始容量为7。

    /**
     * <p>Gets the time zone display name, using a cache for performance.</p>
 * <p>获取时区显示名称，使用缓存以提高性能。</p>
     *
     * @param tz  the zone to query
 * 要查询的时区
     * @param daylight  true if daylight savings
 * 如果为true，表示夏令时
     * @param style  the style to use {@code TimeZone.LONG} or {@code TimeZone.SHORT}
 * 要使用的样式，可以是 {@code TimeZone.LONG} 或 {@code TimeZone.SHORT}
     * @param locale  the locale to use
 * 要使用的语言环境
     * @return the textual name of the time zone
 * 时区的文本名称
     */
    static String getTimeZoneDisplay(final TimeZone tz, final boolean daylight, final int style, final Locale locale) {
    final TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale); // 创建时区显示键
    String value = cTimeZoneDisplayCache.get(key); // 从缓存中获取值
    if (value == null) { // 如果缓存中不存在
            // This is a very slow call, so cache the results.
        // 这是一个非常慢的调用，因此缓存结果。
        value = tz.getDisplayName(daylight, style, locale); // 调用 TimeZone 的 getDisplayName 方法获取显示名称
        final String prior = cTimeZoneDisplayCache.putIfAbsent(key, value); // 将结果放入缓存，如果已存在则返回现有值
        if (prior != null) { // 如果在putIfAbsent期间有其他线程先放入了值
            value= prior; // 使用已存在的值
            }
        }
    return value; // 返回时区显示名称
    }

    /**
     * <p>Inner class to output a time zone name.</p>
 * <p>用于输出时区名称的内部类。</p>
     */
    private static class TimeZoneNameRule implements Rule {
    private final Locale mLocale; // 存储语言环境
    private final int mStyle; // 存储时区样式（TimeZone.LONG 或 TimeZone.SHORT）
    private final String mStandard; // 存储标准时区名称
    private final String mDaylight; // 存储夏令时时区名称

        /**
         * Constructs an instance of {@code TimeZoneNameRule} with the specified properties.
     * 构造一个具有指定属性的 {@code TimeZoneNameRule} 实例。
         *
         * @param timeZone the time zone
     * 时区对象
         * @param locale the locale
     * 语言环境
         * @param style the style
     * 样式（LONG 或 SHORT）
         */
        TimeZoneNameRule(final TimeZone timeZone, final Locale locale, final int style) {
        mLocale = locale; // 初始化语言环境
        mStyle = style; // 初始化样式

        // 预先获取并缓存标准时和夏令时名称，以避免在 appendTo 方法中重复调用慢速方法
        mStandard = getTimeZoneDisplay(timeZone, false, style, locale); // 获取标准时名称
        mDaylight = getTimeZoneDisplay(timeZone, true, style, locale); // 获取夏令时名称
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度。由于无法访问将传递给 appendTo 的 Calendar 对象，因此基于构造函数中传入的时区的标准名称和夏令时名称的最大长度进行估计。
         */
        @Override
        public int estimateLength() {
            // We have no access to the Calendar object that will be passed to
            // appendTo so base estimate on the TimeZone passed to the
            // constructor
        // 我们无法访问将传递给 appendTo 的 Calendar 对象，因此根据传递给构造函数的 TimeZone 估算长度。
            return Math.max(mStandard.length(), mDaylight.length());
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 将当前日历的时区名称追加到缓冲区。如果当前是夏令时，则追加夏令时名称；否则追加标准时名称。
     * 运行时会根据实际的 Calendar 对象来获取时区信息，并利用缓存来获取显示名称。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
        final TimeZone zone = calendar.getTimeZone(); // 获取日历中的时区
        if (calendar.get(Calendar.DST_OFFSET) != 0) { // 如果存在夏令时偏移量，说明当前是夏令时
            buffer.append(getTimeZoneDisplay(zone, true, mStyle, mLocale)); // 追加夏令时名称
            } else {
            buffer.append(getTimeZoneDisplay(zone, false, mStyle, mLocale)); // 追加标准时名称
            }
        }
    }

    /**
     * <p>Inner class to output a time zone as a number {@code +/-HHMM}
     * or {@code +/-HH:MM}.</p>
 * <p>用于以数字形式 {@code +/-HHMM} 或 {@code +/-HH:MM} 输出时区的内部类。</p>
     */
    private static class TimeZoneNumberRule implements Rule {
    // 静态常量实例，用于表示带冒号和不带冒号的时区数字格式
    static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true); // 带冒号的实例 (+/-HH:MM)
    static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false); // 不带冒号的实例 (+/-HHMM)

    final boolean mColon; // 控制输出中小时和分钟之间是否有冒号

        /**
         * Constructs an instance of {@code TimeZoneNumberRule} with the specified properties.
     * 构造一个具有指定属性的 {@code TimeZoneNumberRule} 实例。
         *
         * @param colon add colon between HH and MM in the output if {@code true}
     * 如果为 {@code true}，则在输出的小时和分钟之间添加冒号
         */
        TimeZoneNumberRule(final boolean colon) {
        mColon = colon; // 初始化是否带冒号
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，例如 "+HHMM" 是5位，"+HH:MM" 是6位，但这里统一返回5位作为基本估计。
         */
        @Override
        public int estimateLength() {
            return 5;
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 将时区偏移量格式化为 {@code +/-HHMM} 或 {@code +/-HH:MM} 形式并追加到缓冲区。
     * 首先计算总偏移量（标准偏移 + 夏令时偏移），然后根据正负添加符号，最后分别计算小时和分钟并追加。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {

        int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET); // 计算总偏移量（毫秒）

        if (offset < 0) { // 如果偏移量为负，表示西区
            buffer.append('-'); // 追加负号
            offset = -offset; // 将偏移量变为正值进行计算
        } else { // 如果偏移量为正，表示东区
            buffer.append('+'); // 追加正号
            }

        final int hours = offset / (60 * 60 * 1000); // 计算小时数
        appendDigits(buffer, hours); // 追加两位小时数

        if (mColon) { // 如果配置为带冒号
            buffer.append(':'); // 追加冒号
            }

        final int minutes = offset / (60 * 1000) - 60 * hours; // 计算分钟数（去除小时部分）
        appendDigits(buffer, minutes); // 追加两位分钟数
        }
    }

    /**
     * <p>Inner class to output a time zone as a number {@code +/-HHMM}
     * or {@code +/-HH:MM}.</p>
 * <p>用于以数字形式 {@code +/-HHMM} 或 {@code +/-HH:MM} 输出时区的内部类。</p>
     */
    private static class Iso8601_Rule implements Rule {

        // Sign TwoDigitHours or Z
    // 符号 两位小时 或 Z (例如：+01, -05, Z)
        static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
        // Sign TwoDigitHours Minutes or Z
    // 符号 两位小时 分钟 或 Z (例如：+0100, -0530, Z)
        static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
        // Sign TwoDigitHours : Minutes or Z
    // 符号 两位小时 : 分钟 或 Z (例如：+01:00, -05:30, Z)
        static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);

        /**
         * Factory method for Iso8601_Rules.
     * Iso8601_Rule 的工厂方法。
         *
         * @param tokenLen a token indicating the length of the TimeZone String to be formatted.
     * 一个指示要格式化的时区字符串长度的标记。
     * tokenLen = 1 -> X  (+HH or Z)  -> 实际输出长度 3
     * tokenLen = 2 -> XX (+HHMM or Z) -> 实际输出长度 5
     * tokenLen = 3 -> XXX (+HH:MM or Z) -> 实际输出长度 6
         * @return a Iso8601_Rule that can format TimeZone String of length {@code tokenLen}. If no such
         *          rule exists, an IllegalArgumentException will be thrown.
     * 一个可以格式化长度为 {@code tokenLen} 的时区字符串的 Iso8601_Rule。如果不存在这样的规则，将抛出 IllegalArgumentException。
         */
        static Iso8601_Rule getRule(final int tokenLen) {
            switch(tokenLen) {
            case 1:
            return Iso8601_Rule.ISO8601_HOURS; // 对应 "+HH" 或 "Z" 格式
            case 2:
            return Iso8601_Rule.ISO8601_HOURS_MINUTES; // 对应 "+HHMM" 或 "Z" 格式
            case 3:
            return Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES; // 对应 "+HH:MM" 或 "Z" 格式
            default:
            throw new IllegalArgumentException("invalid number of X"); // 无效的格式标记
            }
        }

    final int length; // 存储输出字符串的预期长度（除非是'Z'）

        /**
         * Constructs an instance of {@code Iso8601_Rule} with the specified properties.
     * 构造一个具有指定属性的 {@code Iso8601_Rule} 实例。
         *
         * @param length The number of characters in output (unless Z is output)
     * 输出中的字符数（除非输出为 Z）
         */
        Iso8601_Rule(final int length) {
        this.length = length; // 初始化预期长度
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#estimateLength()} 方法。
     * 估计输出字符串的长度，即构造函数中传入的预期长度。
         */
        @Override
        public int estimateLength() {
            return length;
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Rule#appendTo(Appendable, Calendar)} 方法。
     * 将时区偏移量格式化为 ISO 8601 标准形式（{@code Z} 或 {@code +/-HH} / {@code +/-HHMM} / {@code +/-HH:MM}），并追加到缓冲区。
     * 首先检查偏移量是否为0，如果是则输出 'Z'。否则，计算小时和分钟，并根据配置的长度添加符号和冒号。
         */
        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
        int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET); // 计算总偏移量（毫秒）
        if (offset == 0) { // 如果偏移量为0，表示UTC
            buffer.append("Z"); // 追加 'Z'
            return; // 结束方法
            }

        if (offset < 0) { // 如果偏移量为负，表示西区
            buffer.append('-'); // 追加负号
            offset = -offset; // 将偏移量变为正值进行计算
        } else { // 如果偏移量为正，表示东区
            buffer.append('+'); // 追加正号
            }

        final int hours = offset / (60 * 60 * 1000); // 计算小时数
        appendDigits(buffer, hours); // 追加两位小时数

        if (length < 5) { // 如果长度小于5，表示只输出小时部分（例如 "+HH"）
            return; // 结束方法
            }

        if (length == 6) { // 如果长度为6，表示需要冒号分隔 (例如 "+HH:MM")
            buffer.append(':'); // 追加冒号
            }

        final int minutes = offset / (60 * 1000) - 60 * hours; // 计算分钟数（去除小时部分）
        appendDigits(buffer, minutes); // 追加两位分钟数
        }
    }

    // ----------------------------------------------------------------------
    /**
     * <p>Inner class that acts as a compound key for time zone names.</p>
 * <p>用作时区名称复合键的内部类。</p>
     */
    private static class TimeZoneDisplayKey {
    private final TimeZone mTimeZone; // 存储时区对象
    private final int mStyle; // 存储样式（包含夏令时标志）
    private final Locale mLocale; // 存储语言环境

        /**
         * Constructs an instance of {@code TimeZoneDisplayKey} with the specified properties.
     * 构造一个具有指定属性的 {@code TimeZoneDisplayKey} 实例。
         *
         * @param timeZone the time zone
     * 时区对象
         * @param daylight adjust the style for daylight saving time if {@code true}
     * 如果为 {@code true}，则为夏令时调整样式
         * @param style the timezone style
     * 时区样式（例如 TimeZone.LONG, TimeZone.SHORT）
         * @param locale the timezone locale
     * 时区语言环境
         */
        TimeZoneDisplayKey(final TimeZone timeZone,
                           final boolean daylight, final int style, final Locale locale) {
        mTimeZone = timeZone; // 初始化时区
        if (daylight) { // 如果是夏令时
            mStyle = style | 0x80000000; // 将最高位设置为1，表示夏令时样式
            } else {
            mStyle = style; // 否则直接使用样式
            }
        mLocale = locale; // 初始化语言环境
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Object#hashCode()} 方法。
     * 计算此对象的哈希码，用于哈希表（如ConcurrentHashMap）中的键查找。
     * 哈希码的计算基于样式、语言环境和时区对象的哈希码。
         */
        @Override
        public int hashCode() {
            return (mStyle * 31 + mLocale.hashCode() ) * 31 + mTimeZone.hashCode();
        }

        /**
         * {@inheritDoc}
     * 实现 {@link Object#equals(Object)} 方法。
     * 比较此对象与另一个对象是否相等。
     * 只有当另一个对象也是 TimeZoneDisplayKey 且它们的时区、样式和语言环境都相等时，才返回 true。
         */
        @Override
        public boolean equals(final Object obj) {
        if (this == obj) { // 如果是同一个对象，直接返回true
                return true;
            }
        if (obj instanceof TimeZoneDisplayKey) { // 如果是 TimeZoneDisplayKey 的实例
            final TimeZoneDisplayKey other = (TimeZoneDisplayKey)obj; // 类型转换
                return
                mTimeZone.equals(other.mTimeZone) && // 比较时区是否相等
                mStyle == other.mStyle && // 比较样式是否相等
                mLocale.equals(other.mLocale); // 比较语言环境是否相等
            }
        return false; // 不是 TimeZoneDisplayKey 实例或者属性不相等，返回false
        }
    }
}
