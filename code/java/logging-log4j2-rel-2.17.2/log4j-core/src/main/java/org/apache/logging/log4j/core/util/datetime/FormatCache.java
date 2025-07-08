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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>FormatCache is a cache and factory for {@link Format}s.</p>
 * <p>FormatCache 是 {@link Format} 对象的缓存和工厂。</p>
 *
 * <p>
 * Copied and modified from <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>.
 * </p>
 * <p>
 * 从 <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a> 复制并修改。
 * </p>
 *
 * @since Apache Commons Lang 3.0
 * @since Apache Commons Lang 3.0
 */
// TODO: Before making public move from getDateTimeInstance(Integer,...) to int; or some other approach.
// TODO: 在公开之前，将 getDateTimeInstance(Integer,...) 移动到 int 类型；或者采用其他方法。
abstract class FormatCache<F extends Format> {

    /**
     * No date or no time.  Used in same parameters as DateFormat.SHORT or DateFormat.LONG
     * 没有日期或没有时间。与 DateFormat.SHORT 或 DateFormat.LONG 在相同的参数中使用
     */
    static final int NONE = -1; // 表示没有日期或时间样式

    private final ConcurrentMap<MultipartKey, F> cInstanceCache =
            new ConcurrentHashMap<>(7); // 缓存 Format 实例的并发 Map

    private static final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache =
            new ConcurrentHashMap<>(7); // 缓存日期时间模式字符串的并发 Map

    /**
     * <p>Gets a formatter instance using the default pattern in the
     * default timezone and locale.</p>
     * <p>使用默认时区和语言环境中的默认模式获取格式化程序实例。</p>
     *
     * @return a date/time formatter
     * @return 日期/时间格式化程序
     */
    public F getInstance() {
        // 返回一个使用 SHORT 日期样式和 SHORT 时间样式、默认时区和默认语言环境的格式化程序实例。
        return getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * <p>Gets a formatter instance using the specified pattern, time zone
     * and locale.</p>
     * <p>使用指定的模式、时区和语言环境获取格式化程序实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible
     *  pattern, non-null
     * @param pattern {@link java.text.SimpleDateFormat} 兼容的模式，非空
     * @param timeZone  the time zone, null means use the default TimeZone
     * @param timeZone 时区，为 null 表示使用默认时区
     * @param locale  the locale, null means use the default Locale
     * @param locale 语言环境，为 null 表示使用默认语言环境
     * @return a pattern based date/time formatter
     * @return 基于模式的日期/时间格式化程序
     * @throws IllegalArgumentException if pattern is invalid
     *  or <code>null</code>
     * @throws IllegalArgumentException 如果模式无效或为 <code>null</code>
     */
    public F getInstance(final String pattern, TimeZone timeZone, Locale locale) {
        if (pattern == null) {
            // 模式不能为空，否则抛出 NullPointerException
            throw new NullPointerException("pattern must not be null");
        }
        if (timeZone == null) {
            // 如果时区为空，则使用默认时区
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            // 如果语言环境为空，则使用默认语言环境
            locale = Locale.getDefault();
        }
        // 使用模式、时区和语言环境创建复合键
        final MultipartKey key = new MultipartKey(pattern, timeZone, locale);
        // 从缓存中获取格式化程序实例
        F format = cInstanceCache.get(key);
        if (format == null) {
            // 如果缓存中不存在，则创建新的实例
            format = createInstance(pattern, timeZone, locale);
            // 尝试将新实例放入缓存，如果已经存在，则返回已存在的值
            final F previousValue= cInstanceCache.putIfAbsent(key, format);
            if (previousValue != null) {
                // 另一个线程抢先完成了相同的操作，应该返回 ConcurrentMap 中的实例
                format= previousValue;
            }
        }
        // 返回获取到的格式化程序实例
        return format;
    }

    /**
     * <p>Create a format instance using the specified pattern, time zone
     * and locale.</p>
     * <p>使用指定的模式、时区和语言环境创建一个格式化程序实例。</p>
     *
     * @param pattern  {@link java.text.SimpleDateFormat} compatible pattern, this will not be null.
     * @param pattern {@link java.text.SimpleDateFormat} 兼容的模式，此参数不会为 null。
     * @param timeZone  time zone, this will not be null.
     * @param timeZone 时区，此参数不会为 null。
     * @param locale  locale, this will not be null.
     * @param locale 语言环境，此参数不会为 null。
     * @return a pattern based date/time formatter
     * @return 基于模式的日期/时间格式化程序
     * @throws IllegalArgumentException if pattern is invalid
     *  or <code>null</code>
     * @throws IllegalArgumentException 如果模式无效或为 <code>null</code>
     */
    abstract protected F createInstance(String pattern, TimeZone timeZone, Locale locale);

    /**
     * <p>Gets a date/time formatter instance using the specified style,
     * time zone and locale.</p>
     * <p>使用指定的样式、时区和语言环境获取日期/时间格式化程序实例。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT, null indicates no date in format
     * @param dateStyle 日期样式：FULL、LONG、MEDIUM 或 SHORT，null 表示格式中不包含日期
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT, null indicates no time in format
     * @param timeStyle 时间样式：FULL、LONG、MEDIUM 或 SHORT，null 表示格式中不包含时间
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date, null means use default Locale
     * @param timeZone 可选时区，覆盖格式化日期的时区，null 表示使用默认语言环境
     * @param locale  optional locale, overrides system locale
     * @param locale 可选语言环境，覆盖系统语言环境
     * @return a localized standard date/time formatter
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * @throws IllegalArgumentException 如果该语言环境没有定义日期/时间模式
     */
    // This must remain private, see LANG-884
    // 此方法必须保持私有，参见 LANG-884
    private F getDateTimeInstance(final Integer dateStyle, final Integer timeStyle, final TimeZone timeZone, Locale locale) {
        if (locale == null) {
            // 如果语言环境为空，则使用默认语言环境
            locale = Locale.getDefault();
        }
        // 根据日期样式、时间样式和语言环境获取模式字符串
        final String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
        // 使用获取到的模式、时区和语言环境获取格式化程序实例
        return getInstance(pattern, timeZone, locale);
    }

    /**
     * <p>Gets a date/time formatter instance using the specified style,
     * time zone and locale.</p>
     * <p>使用指定的样式、时区和语言环境获取日期/时间格式化程序实例。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT
     * @param dateStyle 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT
     * @param timeStyle 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date, null means use default Locale
     * @param timeZone 可选时区，覆盖格式化日期的时区，null 表示使用默认语言环境
     * @param locale  optional locale, overrides system locale
     * @param locale 可选语言环境，覆盖系统语言环境
     * @return a localized standard date/time formatter
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * @throws IllegalArgumentException 如果该语言环境没有定义日期/时间模式
     */
    // package protected, for access from FastDateFormat; do not make public or protected
    // 包级保护，供 FastDateFormat 访问；请勿公开或保护
    F getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        // 调用私有的 getDateTimeInstance 方法，将 int 类型的样式转换为 Integer
        return getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
    }

    /**
     * <p>Gets a date formatter instance using the specified style,
     * time zone and locale.</p>
     * <p>使用指定的样式、时区和语言环境获取日期格式化程序实例。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT
     * @param dateStyle 日期样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date, null means use default Locale
     * @param timeZone 可选时区，覆盖格式化日期的时区，null 表示使用默认语言环境
     * @param locale  optional locale, overrides system locale
     * @param locale 可选语言环境，覆盖系统语言环境
     * @return a localized standard date/time formatter
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * @throws IllegalArgumentException 如果该语言环境没有定义日期/时间模式
     */
    // package protected, for access from FastDateFormat; do not make public or protected
    // 包级保护，供 FastDateFormat 访问；请勿公开或保护
    F getDateInstance(final int dateStyle, final TimeZone timeZone, final Locale locale) {
        // 调用 getDateTimeInstance 方法，仅指定日期样式，时间样式为 null
        return getDateTimeInstance(Integer.valueOf(dateStyle), null, timeZone, locale);
    }

    /**
     * <p>Gets a time formatter instance using the specified style,
     * time zone and locale.</p>
     * <p>使用指定的样式、时区和语言环境获取时间格式化程序实例。</p>
     *
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT
     * @param timeStyle 时间样式：FULL、LONG、MEDIUM 或 SHORT
     * @param timeZone  optional time zone, overrides time zone of
     *  formatted date, null means use default Locale
     * @param timeZone 可选时区，覆盖格式化日期的时区，null 表示使用默认语言环境
     * @param locale  optional locale, overrides system locale
     * @param locale 可选语言环境，覆盖系统语言环境
     * @return a localized standard date/time formatter
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException if the Locale has no date/time
     *  pattern defined
     * @throws IllegalArgumentException 如果该语言环境没有定义日期/时间模式
     */
    // package protected, for access from FastDateFormat; do not make public or protected
    // 包级保护，供 FastDateFormat 访问；请勿公开或保护
    F getTimeInstance(final int timeStyle, final TimeZone timeZone, final Locale locale) {
        // 调用 getDateTimeInstance 方法，仅指定时间样式，日期样式为 null
        return getDateTimeInstance(null, Integer.valueOf(timeStyle), timeZone, locale);
    }

    /**
     * <p>Gets a date/time format for the specified styles and locale.</p>
     * <p>获取指定样式和语言环境的日期/时间格式。</p>
     *
     * @param dateStyle  date style: FULL, LONG, MEDIUM, or SHORT, null indicates no date in format
     * @param dateStyle 日期样式：FULL、LONG、MEDIUM 或 SHORT，null 表示格式中不包含日期
     * @param timeStyle  time style: FULL, LONG, MEDIUM, or SHORT, null indicates no time in format
     * @param timeStyle 时间样式：FULL、LONG、MEDIUM 或 SHORT，null 表示格式中不包含时间
     * @param locale  The non-null locale of the desired format
     * @param locale 所需格式的非空语言环境
     * @return a localized standard date/time format
     * @return 本地化的标准日期/时间格式
     * @throws IllegalArgumentException if the Locale has no date/time pattern defined
     * @throws IllegalArgumentException 如果该语言环境没有定义日期/时间模式
     */
    // package protected, for access from test code; do not make public or protected
    // 包级保护，供测试代码访问；请勿公开或保护
    static String getPatternForStyle(final Integer dateStyle, final Integer timeStyle, final Locale locale) {
        // 使用日期样式、时间样式和语言环境创建复合键
        final MultipartKey key = new MultipartKey(dateStyle, timeStyle, locale);

        // 从缓存中获取模式字符串
        String pattern = cDateTimeInstanceCache.get(key);
        if (pattern == null) {
            try {
                DateFormat formatter;
                if (dateStyle == null) {
                    // 如果日期样式为 null，则仅获取时间格式化程序
                    formatter = DateFormat.getTimeInstance(timeStyle.intValue(), locale);
                }else if (timeStyle == null) {
                    // 如果时间样式为 null，则仅获取日期格式化程序
                    formatter = DateFormat.getDateInstance(dateStyle.intValue(), locale);
                }else {
                    // 否则，获取日期和时间格式化程序
                    formatter = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), locale);
                }
                // 将 DateFormat 转换为 SimpleDateFormat 并获取其模式字符串
                pattern = ((SimpleDateFormat)formatter).toPattern();
                // 尝试将模式字符串放入缓存，如果已经存在，则返回已存在的值
                final String previous = cDateTimeInstanceCache.putIfAbsent(key, pattern);
                if (previous != null) {
                    // 即使另一个线程放入模式无关紧要，但返回 ConcurrentMap 中实际存在的 String 实例仍然是很好的做法
                    pattern= previous;
                }
            } catch (final ClassCastException ex) {
                // 如果该语言环境没有定义日期/时间模式，则抛出 IllegalArgumentException
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        // 返回获取到的模式字符串
        return pattern;
    }

    // ----------------------------------------------------------------------
    /**
     * <p>Helper class to hold multi-part Map keys</p>
     * <p>用于存储多部分 Map 键的辅助类</p>
     */
    private static class MultipartKey {
        private final Object[] keys; // 存储构成键的对象的数组
        private int hashCode; // 缓存哈希码

        /**
         * Constructs an instance of <code>MultipartKey</code> to hold the specified objects.
         * 构造一个 <code>MultipartKey</code> 实例来存储指定的对象。
         *
         * @param keys the set of objects that make up the key.  Each key may be null.
         * @param keys 构成键的对象集合。每个键都可以为 null。
         */
        public MultipartKey(final Object... keys) {
            this.keys = keys;
        }

        /**
         * {@inheritDoc}
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            // Eliminate the usual boilerplate because
            // this inner static class is only used in a generic ConcurrentHashMap
            // which will not compare against other Object types
            // 省略常见的样板代码，因为
            // 这个内部静态类只在泛型 ConcurrentHashMap 中使用
            // 它不会与其他 Object 类型进行比较
            // 比较键数组是否相等
            return Arrays.equals(keys, ((MultipartKey)obj).keys);
        }

        /**
         * {@inheritDoc}
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            // 如果哈希码尚未计算，则进行计算
            if(hashCode==0) {
                int rc = 0; // 结果哈希码
                // 遍历所有键，计算哈希码
                for(final Object key : keys) {
                    if(key!=null) {
                        rc = rc * 7 + key.hashCode(); // 累加每个非空键的哈希码
                    }
                }
                hashCode = rc; // 缓存计算出的哈希码
            }
            // 返回缓存的哈希码
            return hashCode;
        }
    }

}
