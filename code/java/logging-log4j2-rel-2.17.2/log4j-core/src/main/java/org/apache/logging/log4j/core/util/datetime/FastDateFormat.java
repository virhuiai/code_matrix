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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>FastDateFormat is a fast and thread-safe version of
 * {@link java.text.SimpleDateFormat}.</p>
 * <p>FastDateFormat 是 {@link java.text.SimpleDateFormat} 的一个快速且线程安全的版本。</p>
 *
 * <p>To obtain an instance of FastDateFormat, use one of the static factory methods:
 * {@link #getInstance(String, TimeZone, Locale)}, {@link #getDateInstance(int, TimeZone, Locale)},
 * {@link #getTimeInstance(int, TimeZone, Locale)}, or {@link #getDateTimeInstance(int, int, TimeZone, Locale)}
 * </p>
 * <p>要获取 FastDateFormat 的实例，请使用以下静态工厂方法之一：
 * {@link #getInstance(String, TimeZone, Locale)}、{@link #getDateInstance(int, TimeZone, Locale)}、
 * {@link #getTimeInstance(int, TimeZone, Locale)} 或 {@link #getDateTimeInstance(int, int, TimeZone, Locale)}
 * </p>
 *
 * <p>Since FastDateFormat is thread safe, you can use a static member instance:</p>
 * <p>由于 FastDateFormat 是线程安全的，您可以使用静态成员实例：</p>
 * <code>
 *   private static final FastDateFormat DATE_FORMATTER = FastDateFormat.getDateTimeInstance(FastDateFormat.LONG, FastDateFormat.SHORT);
 * </code>
 *
 * <p>This class can be used as a direct replacement to
 * {@code SimpleDateFormat} in most formatting and parsing situations.
 * This class is especially useful in multi-threaded server environments.
 * {@code SimpleDateFormat} is not thread-safe in any JDK version,
 * nor will it be as Sun have closed the bug/RFE.
 * </p>
 * <p>在大多数格式化和解析场景中，这个类可以作为 {@code SimpleDateFormat} 的直接替代品。
 * 这个类在多线程服务器环境中特别有用。在任何 JDK 版本中，{@code SimpleDateFormat} 都不是线程安全的，
 * 而且 Sun 已经关闭了相关的 bug/RFE，因此它也不会变得线程安全。
 * </p>
 *
 * <p>All patterns are compatible with
 * SimpleDateFormat (except time zones and some year patterns - see below).</p>
 * <p>所有模式都与 SimpleDateFormat 兼容（除了时区和一些年份模式——详见下文）。</p>
 *
 * <p>Since 3.2, FastDateFormat supports parsing as well as printing.</p>
 * <p>从 3.2 版本开始，FastDateFormat 同时支持解析和打印功能。</p>
 *
 * <p>Java 1.4 introduced a new pattern letter, {@code 'Z'}, to represent
 * time zones in RFC822 format (eg. {@code +0800} or {@code -1100}).
 * This pattern letter can be used here (on all JDK versions).</p>
 * <p>Java 1.4 引入了一个新的模式字母 {@code 'Z'}，用于表示 RFC822 格式的时区（例如 {@code +0800} 或 {@code -1100}）。
 * 这个模式字母可以在这里使用（在所有 JDK 版本上）。</p>
 *
 * <p>In addition, the pattern {@code 'ZZ'} has been made to represent
 * ISO 8601 extended format time zones (eg. {@code +08:00} or {@code -11:00}).
 * This introduces a minor incompatibility with Java 1.4, but at a gain of
 * useful functionality.</p>
 * <p>此外，模式 {@code 'ZZ'} 被用来表示 ISO 8601 扩展格式的时区（例如 {@code +08:00} 或 {@code -11:00}）。
 * 这与 Java 1.4 引入了轻微的不兼容性，但换来了有用的功能。</p>
 *
 * <p>Javadoc cites for the year pattern: <i>For formatting, if the number of
 * pattern letters is 2, the year is truncated to 2 digits; otherwise it is
 * interpreted as a number.</i> Starting with Java 1.7 a pattern of 'Y' or
 * 'YYY' will be formatted as '2003', while it was '03' in former Java
 * versions. FastDateFormat implements the behavior of Java 7.</p>
 * <p>Javadoc 关于年份模式的说明：<i>对于格式化，如果模式字母的数量是 2，则年份将被截断为 2 位数字；否则，它将被解释为一个数字。</i>
 * 从 Java 1.7 开始，'Y' 或 'YYY' 模式将被格式化为 '2003'，而在以前的 Java 版本中是 '03'。
 * FastDateFormat 实现了 Java 7 的行为。</p>
 *
 * <p>
 * Copied and modified from <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>.
 * </p>
 * <p>
 * 从 <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a> 复制并修改。
 * </p>
 *
 * @since Apache Commons Lang 2.0
 */
public class FastDateFormat extends Format implements DateParser, DatePrinter {

    /**
     * Required for serialization support.
     * 序列化支持所必需。
     * @see java.io.Serializable
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 2L;
    // 序列化版本UID，用于确保序列化和反序列化时的兼容性。

    /**
     * FULL locale dependent date or time style.
     * 完整的、依赖于语言环境的日期或时间样式。
     */
    public static final int FULL = DateFormat.FULL;
    
    /**
     * LONG locale dependent date or time style.
     * 长的、依赖于语言环境的日期或时间样式。
     */
    public static final int LONG = DateFormat.LONG;
    
    /**
     * MEDIUM locale dependent date or time style.
     * 中等的、依赖于语言环境的日期或时间样式。
     */
    public static final int MEDIUM = DateFormat.MEDIUM;
    
    /**
     * SHORT locale dependent date or time style.
     * 短的、依赖于语言环境的日期或时间样式。
     */
    public static final int SHORT = DateFormat.SHORT;

    // 格式缓存，用于存储和重用 FastDateFormat 实例，提高性能。
    private static final FormatCache<FastDateFormat> cache= new FormatCache<FastDateFormat>() {
        @Override
        protected FastDateFormat createInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
            // 根据指定的模式、时区和语言环境创建 FastDateFormat 实例。
            return new FastDateFormat(pattern, timeZone, locale);
        }
    };

    // FastDatePrinter 实例，负责日期和时间的格式化（打印）。
    private final FastDatePrinter printer;
    // FastDateParser 实例，负责日期和时间的解析。
    private final FastDateParser parser;

    //-----------------------------------------------------------------------
    /**
     * <p>Gets a formatter instance using the default pattern in the
     * default locale.</p>
     * <p>获取使用默认语言环境中的默认模式的格式化器实例。</p>
     *
     * @return a date/time formatter
     * 一个日期/时间格式化器
     */
    public static FastDateFormat getInstance() {
        // 从缓存中获取默认的 FastDateFormat 实例。
        return cache.getInstance();
    }

    /**
     * <p>Gets a formatter instance using the specified pattern in the
     * default locale.</p>
     * <p>获取使用默认语言环境中的指定模式的格式化器实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible
     *  pattern
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式
     * @return a pattern based date/time formatter
     * 一个基于模式的日期/时间格式化器
     * @throws IllegalArgumentException if pattern is invalid
     * 如果模式无效则抛出 IllegalArgumentException
     */
    public static FastDateFormat getInstance(final String pattern) {
        // 获取使用指定模式、默认时区和默认语言环境的 FastDateFormat 实例。
        return cache.getInstance(pattern, null, null);
    }

    /**
     * <p>Gets a formatter instance using the specified pattern and
     * time zone.</p>
     * <p>获取使用指定模式和时区的格式化器实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible
     *  pattern
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date
     * 可选时区，覆盖格式化日期的时区
     * @return a pattern based date/time formatter
     * 一个基于模式的日期/时间格式化器
     * @throws IllegalArgumentException if pattern is invalid
     * 如果模式无效则抛出 IllegalArgumentException
     */
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone) {
        // 获取使用指定模式、指定时区和默认语言环境的 FastDateFormat 实例。
        return cache.getInstance(pattern, timeZone, null);
    }

    /**
     * <p>Gets a formatter instance using the specified pattern and
     * locale.</p>
     * <p>获取使用指定模式和语言环境的格式化器实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible
     *  pattern
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a pattern based date/time formatter
     * 一个基于模式的日期/时间格式化器
     * @throws IllegalArgumentException if pattern is invalid
     * 如果模式无效则抛出 IllegalArgumentException
     */
    public static FastDateFormat getInstance(final String pattern, final Locale locale) {
        // 获取使用指定模式、默认时区和指定语言环境的 FastDateFormat 实例。
        return cache.getInstance(pattern, null, locale);
    }

    /**
     * <p>Gets a formatter instance using the specified pattern, time zone
     * and locale.</p>
     * <p>获取使用指定模式、时区和语言环境的格式化器实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible
     *  pattern
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date
     * 可选时区，覆盖格式化日期的时区
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a pattern based date/time formatter
     * 一个基于模式的日期/时间格式化器
     * @throws IllegalArgumentException if pattern is invalid
     *  or {@code null}
     * 如果模式无效或为 {@code null} 则抛出 IllegalArgumentException
     */
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
        // 获取使用指定模式、指定时区和指定语言环境的 FastDateFormat 实例。
        return cache.getInstance(pattern, timeZone, locale);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Gets a date formatter instance using the specified style in the
     * default time zone and locale.</p>
     * <p>获取使用默认时区和语言环境中的指定样式的日期格式化器实例。</p>
     *
     * @param style  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @return a localized standard date formatter
     * 一个本地化的标准日期格式化器
     * @throws IllegalArgumentException if the Locale has no date
     *  pattern defined
     * 如果 Locale 没有定义日期模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getDateInstance(final int style) {
        // 获取使用指定日期样式、默认时区和默认语言环境的 FastDateFormat 实例。
        return cache.getDateInstance(style, null, null);
    }

    /**
     * <p>Gets a date formatter instance using the specified style and
     * locale in the default time zone.</p>
     * <p>获取使用默认时区中指定样式和语言环境的日期格式化器实例。</p>
     *
     * @param style  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a localized standard date formatter
     * 一个本地化的标准日期格式化器
     * @throws IllegalArgumentException if the Locale has no date
     *  pattern defined
     * 如果 Locale 没有定义日期模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getDateInstance(final int style, final Locale locale) {
        // 获取使用指定日期样式、默认时区和指定语言环境的 FastDateFormat 实例。
        return cache.getDateInstance(style, null, locale);
    }

    /**
     * <p>Gets a date formatter instance using the specified style and
     * time zone in the default locale.</p>
     * <p>获取使用默认语言环境中指定样式和时区的日期格式化器实例。</p>
     *
     * @param style  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date
     * 可选时区，覆盖格式化日期的时区
     * @return a localized standard date formatter
     * 一个本地化的标准日期格式化器
     * @throws IllegalArgumentException if the Locale has no date
     *  pattern defined
     * 如果 Locale 没有定义日期模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone) {
        // 获取使用指定日期样式、指定时区和默认语言环境的 FastDateFormat 实例。
        return cache.getDateInstance(style, timeZone, null);
    }

    /**
     * <p>Gets a date formatter instance using the specified style, time
     * zone and locale.</p>
     * <p>获取使用指定样式、时区和语言环境的日期格式化器实例。</p>
     *
     * @param style  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date
     * 可选时区，覆盖格式化日期的时区
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a localized standard date formatter
     * 一个本地化的标准日期格式化器
     * @throws IllegalArgumentException if the Locale has no date
     *  pattern defined
     * 如果 Locale 没有定义日期模式则抛出 IllegalArgumentException
     */
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone, final Locale locale) {
        // 获取使用指定日期样式、指定时区和指定语言环境的 FastDateFormat 实例。
        return cache.getDateInstance(style, timeZone, locale);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Gets a time formatter instance using the specified style in the
     * default time zone and locale.</p>
     * <p>获取使用默认时区和语言环境中的指定样式的时间格式化器实例。</p>
     *
     * @param style  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @return a localized standard time formatter
     * 一个本地化的标准时间格式化器
     * @throws IllegalArgumentException if the Locale has no time
     *  pattern defined
     * 如果 Locale 没有定义时间模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getTimeInstance(final int style) {
        // 获取使用指定时间样式、默认时区和默认语言环境的 FastDateFormat 实例。
        return cache.getTimeInstance(style, null, null);
    }

    /**
     * <p>Gets a time formatter instance using the specified style and
     * locale in the default time zone.</p>
     * <p>获取使用默认时区中指定样式和语言环境的时间格式化器实例。</p>
     *
     * @param style  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a localized standard time formatter
     * 一个本地化的标准时间格式化器
     * @throws IllegalArgumentException if the Locale has no time
     *  pattern defined
     * 如果 Locale 没有定义时间模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getTimeInstance(final int style, final Locale locale) {
        // 获取使用指定时间样式、默认时区和指定语言环境的 FastDateFormat 实例。
        return cache.getTimeInstance(style, null, locale);
    }

    /**
     * <p>Gets a time formatter instance using the specified style and
     * time zone in the default locale.</p>
     * <p>获取使用默认语言环境中指定样式和时区的时间格式化器实例。</p>
     *
     * @param style  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted time
     * 可选时区，覆盖格式化时间的时区
     * @return a localized standard time formatter
     * 一个本地化的标准时间格式化器
     * @throws IllegalArgumentException if the Locale has no time
     *  pattern defined
     * 如果 Locale 没有定义时间模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone) {
        // 获取使用指定时间样式、指定时区和默认语言环境的 FastDateFormat 实例。
        return cache.getTimeInstance(style, timeZone, null);
    }

    /**
     * <p>Gets a time formatter instance using the specified style, time
     * zone and locale.</p>
     * <p>获取使用指定样式、时区和语言环境的时间格式化器实例。</p>
     *
     * @param style  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted time
     * 可选时区，覆盖格式化时间的时区
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a localized standard time formatter
     * 一个本地化的标准时间格式化器
     * @throws IllegalArgumentException if the Locale has no time
     *  pattern defined
     * 如果 Locale 没有定义时间模式则抛出 IllegalArgumentException
     */
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone, final Locale locale) {
        // 获取使用指定时间样式、指定时区和指定语言环境的 FastDateFormat 实例。
        return cache.getTimeInstance(style, timeZone, locale);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Gets a date/time formatter instance using the specified style
     * in the default time zone and locale.</p>
     * <p>获取使用默认时区和语言环境中的指定样式的日期/时间格式化器实例。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @return a localized standard date/time formatter
     * 一个本地化的标准日期/时间格式化器
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * 如果 Locale 没有定义日期/时间模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle) {
        // 获取使用指定日期样式、时间样式、默认时区和默认语言环境的 FastDateFormat 实例。
        return cache.getDateTimeInstance(dateStyle, timeStyle, null, null);
    }

    /**
     * <p>Gets a date/time formatter instance using the specified style and
     * locale in the default time zone.</p>
     * <p>获取使用默认时区中指定样式和语言环境的日期/时间格式化器实例。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a localized standard date/time formatter
     * 一个本地化的标准日期/时间格式化器
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * 如果 Locale 没有定义日期/时间模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final Locale locale) {
        // 获取使用指定日期样式、时间样式、默认时区和指定语言环境的 FastDateFormat 实例。
        return cache.getDateTimeInstance(dateStyle, timeStyle, null, locale);
    }

    /**
     * <p>Gets a date/time formatter instance using the specified style and
     * time zone in the default locale.</p>
     * <p>获取使用默认语言环境中指定样式和时区的日期/时间格式化器实例。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date
     * 可选时区，覆盖格式化日期的时区
     * @return a localized standard date/time formatter
     * 一个本地化的标准日期/时间格式化器
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * 如果 Locale 没有定义日期/时间模式则抛出 IllegalArgumentException
     * @since 2.1
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone) {
        // 获取使用指定日期样式、时间样式、指定时区和默认语言环境的 FastDateFormat 实例。
        return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
    }
    /**
     * <p>Gets a date/time formatter instance using the specified style,
     * time zone and locale.</p>
     * <p>获取使用指定样式、时区和语言环境的日期/时间格式化器实例。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT
     * 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT
     * 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date
     * 可选时区，覆盖格式化日期的时区
     * @param locale  optional locale, overrides system locale
     * 可选语言环境，覆盖系统语言环境
     * @return a localized standard date/time formatter
     * 一个本地化的标准日期/时间格式化器
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * 如果 Locale 没有定义日期/时间模式则抛出 IllegalArgumentException
     */
    public static FastDateFormat getDateTimeInstance(
            final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        // 获取使用指定日期样式、时间样式、时区和语言环境的 FastDateFormat 实例。
        return cache.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
    }

    // Constructor
    // 构造函数
    //-----------------------------------------------------------------------
    /**
     * <p>Constructs a new FastDateFormat.</p>
     * <p>构造一个新的 FastDateFormat 实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式
     * @param timeZone  non-null time zone to use
     * 要使用的非空时区
     * @param locale  non-null locale to use
     * 要使用的非空语言环境
     * @throws NullPointerException if pattern, timeZone, or locale is null.
     * 如果模式、时区或语言环境为 null 则抛出 NullPointerException。
     */
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale) {
        // 调用另一个构造函数，将 centuryStart 参数设置为 null。
        this(pattern, timeZone, locale, null);
    }

    // Constructor
    // 构造函数
    //-----------------------------------------------------------------------
    /**
     * <p>Constructs a new FastDateFormat.</p>
     * <p>构造一个新的 FastDateFormat 实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式
     * @param timeZone  non-null time zone to use
     * 要使用的非空时区
     * @param locale  non-null locale to use
     * 要使用的非空语言环境
     * @param centuryStart The start of the 100 year period to use as the "default century" for 2 digit year parsing.  If centuryStart is null, defaults to now - 80 years
     * 用于两位数年份解析的“默认世纪”的100年周期的开始。如果 centuryStart 为 null，则默认为当前时间减去 80 年
     * @throws NullPointerException if pattern, timeZone, or locale is null.
     * 如果模式、时区或语言环境为 null 则抛出 NullPointerException。
     */
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        // 初始化 FastDatePrinter 实例，用于日期格式化。
        printer= new FastDatePrinter(pattern, timeZone, locale);
        // 初始化 FastDateParser 实例，用于日期解析。
        parser= new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    // Format methods
    // 格式化方法
    //-----------------------------------------------------------------------
    /**
     * <p>Formats a {@code Date}, {@code Calendar} or
     * {@code Long} (milliseconds) object.</p>
     * <p>格式化一个 {@code Date}、{@code Calendar} 或
     * {@code Long}（毫秒）对象。</p>
     * This method is an implementation of {@link Format#format(Object, StringBuilder, FieldPosition)}
     * 此方法是 {@link Format#format(Object, StringBuilder, FieldPosition)} 的实现。
     *
     * @param obj  the object to format
     * 要格式化的对象
     * @param toAppendTo  the buffer to append to
     * 要追加到的缓冲区
     * @param pos  the position - ignored
     * 位置 - 忽略
     * @return the buffer passed in
     * 传入的缓冲区
     */
    @Override
    public StringBuilder format(final Object obj, final StringBuilder toAppendTo, final FieldPosition pos) {
        // 使用 printer 格式化对象，并将结果追加到 StringBuilder。
        return toAppendTo.append(printer.format(obj));
    }

    /**
     * <p>Formats a millisecond {@code long} value.</p>
     * <p>格式化一个毫秒 {@code long} 值。</p>
     *
     * @param millis  the millisecond value to format
     * 要格式化的毫秒值
     * @return the formatted string
     * 格式化后的字符串
     * @since 2.1
     */
    @Override
    public String format(final long millis) {
        // 使用 printer 格式化毫秒值。
        return printer.format(millis);
    }

    /**
     * <p>Formats a {@code Date} object using a {@code GregorianCalendar}.</p>
     * <p>使用 {@code GregorianCalendar} 格式化一个 {@code Date} 对象。</p>
     *
     * @param date  the date to format
     * 要格式化的日期
     * @return the formatted string
     * 格式化后的字符串
     */
    @Override
    public String format(final Date date) {
        // 使用 printer 格式化 Date 对象。
        return printer.format(date);
    }

    /**
     * <p>Formats a {@code Calendar} object.</p>
     * <p>格式化一个 {@code Calendar} 对象。</p>
     *
     * @param calendar  the calendar to format
     * 要格式化的日历
     * @return the formatted string
     * 格式化后的字符串
     */
    @Override
    public String format(final Calendar calendar) {
        // 使用 printer 格式化 Calendar 对象。
        return printer.format(calendar);
    }

    /**
     * <p>Formats a millisecond {@code long} value into the
     * supplied {@code StringBuffer}.</p>
     * <p>将毫秒 {@code long} 值格式化到提供的 {@code StringBuffer} 中。</p>
     *
     * @param millis  the millisecond value to format
     * 要格式化的毫秒值
     * @param buf  the buffer to format into
     * 要格式化到的缓冲区
     * @return the specified string buffer
     * 指定的字符串缓冲区
     * @since 3.5
     */
    @Override
    public <B extends Appendable> B format(final long millis, final B buf) {
        // 使用 printer 格式化毫秒值，并将结果写入指定的缓冲区。
        return printer.format(millis, buf);
    }

    /**
     * <p>Formats a {@code Date} object into the
     * supplied {@code StringBuffer} using a {@code GregorianCalendar}.</p>
     * <p>使用 {@code GregorianCalendar} 将 {@code Date} 对象格式化到提供的 {@code StringBuffer} 中。</p>
     *
     * @param date  the date to format
     * 要格式化的日期
     * @param buf  the buffer to format into
     * 要格式化到的缓冲区
     * @return the specified string buffer
     * 指定的字符串缓冲区
     * @since 3.5
     */
    @Override
    public <B extends Appendable> B format(final Date date, final B buf) {
        // 使用 printer 格式化 Date 对象，并将结果写入指定的缓冲区。
        return printer.format(date, buf);
    }

    /**
     * <p>Formats a {@code Calendar} object into the
     * supplied {@code StringBuffer}.</p>
     * <p>将 {@code Calendar} 对象格式化到提供的 {@code StringBuffer} 中。</p>
     *
     * @param calendar  the calendar to format
     * 要格式化的日历
     * @param buf  the buffer to format into
     * 要格式化到的缓冲区
     * @return the specified string buffer
     * 指定的字符串缓冲区
     * @since 3.5
    */
    @Override
    public <B extends Appendable> B format(final Calendar calendar, final B buf) {
        // 使用 printer 格式化 Calendar 对象，并将结果写入指定的缓冲区。
        return printer.format(calendar, buf);
    }

    // Parsing
    // 解析
    //-----------------------------------------------------------------------


    /* (non-Javadoc)
     * @see DateParser#parse(java.lang.String)
     */
    // 实现 DateParser 接口的 parse 方法，将字符串解析为 Date 对象。
    @Override
    public Date parse(final String source) throws ParseException {
        // 使用 parser 将源字符串解析为 Date 对象。
        return parser.parse(source);
    }

    /* (non-Javadoc)
     * @see DateParser#parse(java.lang.String, java.text.ParsePosition)
     */
    // 实现 DateParser 接口的 parse 方法，从指定位置开始解析字符串为 Date 对象。
    @Override
    public Date parse(final String source, final ParsePosition pos) {
        // 使用 parser 从指定位置解析源字符串为 Date 对象。
        return parser.parse(source, pos);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.commons.lang3.time.DateParser#parse(java.lang.String, java.text.ParsePosition, java.util.Calendar)
     */
    // 实现 DateParser 接口的 parse 方法，将字符串解析并填充到给定的 Calendar 对象中。
    @Override
    public boolean parse(final String source, final ParsePosition pos, final Calendar calendar) {
        // 使用 parser 将源字符串解析并填充到 Calendar 对象中。
        return parser.parse(source, pos, calendar);
    }

    /* (non-Javadoc)
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    // 实现 Format 接口的 parseObject 方法，将字符串解析为 Object。
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        // 使用 parser 将源字符串解析为 Object。
        return parser.parseObject(source, pos);
    }

    // Accessors
    // 访问器
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the pattern used by this formatter.</p>
     * <p>获取此格式化器使用的模式。</p>
     *
     * @return the pattern, {@link java.text.SimpleDateFormat} compatible
     * 与 {@link java.text.SimpleDateFormat} 兼容的模式
     */
    @Override
    public String getPattern() {
        // 返回 printer 使用的模式。
        return printer.getPattern();
    }

    /**
     * <p>Gets the time zone used by this formatter.</p>
     * <p>获取此格式化器使用的时区。</p>
     *
     * <p>This zone is always used for {@code Date} formatting. </p>
     * <p>此区域始终用于 {@code Date} 格式化。</p>
     *
     * @return the time zone
     * 时区
     */
    @Override
    public TimeZone getTimeZone() {
        // 返回 printer 使用的时区。
        return printer.getTimeZone();
    }

    /**
     * <p>Gets the locale used by this formatter.</p>
     * <p>获取此格式化器使用的语言环境。</p>
     *
     * @return the locale
     * 语言环境
     */
    @Override
    public Locale getLocale() {
        // 返回 printer 使用的语言环境。
        return printer.getLocale();
    }

    /**
     * <p>Gets an estimate for the maximum string length that the
     * formatter will produce.</p>
     * <p>获取格式化器将产生的最大字符串长度的估计值。</p>
     *
     * <p>The actual formatted length will almost always be less than or
     * equal to this amount.</p>
     * <p>实际格式化的长度几乎总是小于或等于此值。</p>
     *
     * @return the maximum formatted length
     * 最大格式化长度
     */
    public int getMaxLengthEstimate() {
        // 返回 printer 估计的最大长度。
        return printer.getMaxLengthEstimate();
    }

    // Basics
    // 基本方法
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
        // 检查对象是否为 FastDateFormat 的实例。
        if (obj instanceof FastDateFormat == false) {
            return false;
        }
        // 将对象强制转换为 FastDateFormat。
        final FastDateFormat other = (FastDateFormat) obj;
        // no need to check parser, as it has same invariants as printer
        // 无需检查 parser，因为它与 printer 具有相同的不变性。
        // 比较当前 FastDateFormat 实例的 printer 与另一个实例的 printer 是否相等。
        return printer.equals(other.printer);
    }

    /**
     * <p>Returns a hash code compatible with equals.</p>
     * <p>返回一个与 equals 方法兼容的哈希码。</p>
     *
     * @return a hash code compatible with equals
     * 一个与 equals 方法兼容的哈希码
     */
    @Override
    public int hashCode() {
        // 返回 printer 的哈希码。
        return printer.hashCode();
    }

    /**
     * <p>Gets a debugging string version of this formatter.</p>
     * <p>获取此格式化器的调试字符串版本。</p>
     *
     * @return a debugging string
     * 一个调试字符串
     */
    @Override
    public String toString() {
        // 返回包含模式、语言环境和时区 ID 的调试字符串表示。
        return "FastDateFormat[" + printer.getPattern() + "," + printer.getLocale() + "," + printer.getTimeZone().getID() + "]";
    }
}
