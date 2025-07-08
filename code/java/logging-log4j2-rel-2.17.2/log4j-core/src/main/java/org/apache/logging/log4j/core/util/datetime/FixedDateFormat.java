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

import org.apache.logging.log4j.core.time.Instant;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Custom time formatter that trades flexibility for performance. This formatter only supports the date patterns defined
 * in {@link FixedFormat}. For any other date patterns use {@link FastDateFormat}.
 * <p>
 * Related benchmarks: /log4j-perf/src/main/java/org/apache/logging/log4j/perf/jmh/TimeFormatBenchmark.java and
 * /log4j-perf/src/main/java/org/apache/logging/log4j/perf/jmh/ThreadsafeDateFormatBenchmark.java
 * </p>
 * 定制的时间格式化器，以牺牲灵活性换取性能。此格式化器仅支持在 {@link FixedFormat} 中定义的日期模式。
 * 对于任何其他日期模式，请使用 {@link FastDateFormat}。
 * <p>
 * 相关基准测试：/log4j-perf/src/main/java/org/apache/logging/log4j/perf/jmh/TimeFormatBenchmark.java 和
 * /log4j-perf/src/main/java/org/apache/logging/log4j/perf/jmh/ThreadsafeDateFormatBenchmark.java
 * </p>
 */
public class FixedDateFormat {

    /**
     * Enumeration over the supported date/time format patterns.
     * <p>
     * Package protected for unit tests.
     * </p>
     * 支持的日期/时间格式模式的枚举。
     * <p>
     * 包内保护，用于单元测试。
     * </p>
     */
    public enum FixedFormat {
        
        /**
         * ABSOLUTE time format: {@code "HH:mm:ss,SSS"}.
         * 绝对时间格式：{@code "HH:mm:ss,SSS"}。
         */
        ABSOLUTE("HH:mm:ss,SSS", null, 0, ':', 1, ',', 1, 3, null),
        /**
         * ABSOLUTE time format with microsecond precision: {@code "HH:mm:ss,nnnnnn"}.
         * 具有微秒精度的绝对时间格式：{@code "HH:mm:ss,nnnnnn"}。
         */
        ABSOLUTE_MICROS("HH:mm:ss,nnnnnn", null, 0, ':', 1, ',', 1, 6, null),
        /**
         * ABSOLUTE time format with nanosecond precision: {@code "HH:mm:ss,nnnnnnnnn"}.
         * 具有纳秒精度的绝对时间格式：{@code "HH:mm:ss,nnnnnnnnn"}。
         */
        ABSOLUTE_NANOS("HH:mm:ss,nnnnnnnnn", null, 0, ':', 1, ',', 1, 9, null),

        /**
         * ABSOLUTE time format variation with period separator: {@code "HH:mm:ss.SSS"}.
         * 带句点分隔符的绝对时间格式变体：{@code "HH:mm:ss.SSS"}。
         */
        ABSOLUTE_PERIOD("HH:mm:ss.SSS", null, 0, ':', 1, '.', 1, 3, null),

        /**
         * COMPACT time format: {@code "yyyyMMddHHmmssSSS"}.
         * 紧凑时间格式：{@code "yyyyMMddHHmmssSSS"}。
         */
        COMPACT("yyyyMMddHHmmssSSS", "yyyyMMdd", 0, ' ', 0, ' ', 0, 3, null),

        /**
         * DATE_AND_TIME time format: {@code "dd MMM yyyy HH:mm:ss,SSS"}.
         * 日期和时间格式：{@code "dd MMM yyyy HH:mm:ss,SSS"}。
         */
        DATE("dd MMM yyyy HH:mm:ss,SSS", "dd MMM yyyy ", 0, ':', 1, ',', 1, 3, null),

        /**
         * DATE_AND_TIME time format variation with period separator: {@code "dd MMM yyyy HH:mm:ss.SSS"}.
         * 带句点分隔符的日期和时间格式变体：{@code "dd MMM yyyy HH:mm:ss.SSS"}。
         */
        DATE_PERIOD("dd MMM yyyy HH:mm:ss.SSS", "dd MMM yyyy ", 0, ':', 1, '.', 1, 3, null),

        /**
         * DEFAULT time format: {@code "yyyy-MM-dd HH:mm:ss,SSS"}.
         * 默认时间格式：{@code "yyyy-MM-dd HH:mm:ss,SSS"}。
         */
        DEFAULT("yyyy-MM-dd HH:mm:ss,SSS", "yyyy-MM-dd ", 0, ':', 1, ',', 1, 3, null),
        /**
         * DEFAULT time format with microsecond precision: {@code "yyyy-MM-dd HH:mm:ss,nnnnnn"}.
         * 具有微秒精度的默认时间格式：{@code "yyyy-MM-dd HH:mm:ss,nnnnnn"}。
         */
        DEFAULT_MICROS("yyyy-MM-dd HH:mm:ss,nnnnnn", "yyyy-MM-dd ", 0, ':', 1, ',', 1, 6, null),
        /**
         * DEFAULT time format with nanosecond precision: {@code "yyyy-MM-dd HH:mm:ss,nnnnnnnnn"}.
         * 具有纳秒精度的默认时间格式：{@code "yyyy-MM-dd HH:mm:ss,nnnnnnnnn"}。
         */
        DEFAULT_NANOS("yyyy-MM-dd HH:mm:ss,nnnnnnnnn", "yyyy-MM-dd ", 0, ':', 1, ',', 1, 9, null),

        /**
         * DEFAULT time format variation with period separator: {@code "yyyy-MM-dd HH:mm:ss.SSS"}.
         * 带句点分隔符的默认时间格式变体：{@code "yyyy-MM-dd HH:mm:ss.SSS"}。
         */
        DEFAULT_PERIOD("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd ", 0, ':', 1, '.', 1, 3, null),

        /**
         * ISO8601_BASIC time format: {@code "yyyyMMdd'T'HHmmss,SSS"}.
         * ISO8601_BASIC 时间格式：{@code "yyyyMMdd'T'HHmmss,SSS"}。
         */
        ISO8601_BASIC("yyyyMMdd'T'HHmmss,SSS", "yyyyMMdd'T'", 2, ' ', 0, ',', 1, 3, null),

        /**
         * ISO8601_BASIC time format: {@code "yyyyMMdd'T'HHmmss.SSS"}.
         * ISO8601_BASIC 时间格式：{@code "yyyyMMdd'T'HHmmss.SSS"}。
         */
        ISO8601_BASIC_PERIOD("yyyyMMdd'T'HHmmss.SSS", "yyyyMMdd'T'", 2, ' ', 0, '.', 1, 3, null),

        /**
         * ISO8601 time format: {@code "yyyy-MM-dd'T'HH:mm:ss,SSS"}.
         * ISO8601 时间格式：{@code "yyyy-MM-dd'T'HH:mm:ss,SSS"}。
         */
        ISO8601("yyyy-MM-dd'T'HH:mm:ss,SSS", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, null),

// TODO Do we even want a format without seconds?
// TODO 我们是否需要一个不带秒的格式？
//        /**
//         * ISO8601_OFFSET_DATE_TIME time format: {@code "yyyy-MM-dd'T'HH:mmXXX"}.
//         * ISO8601_OFFSET_DATE_TIME 时间格式：{@code "yyyy-MM-dd'T'HH:mmXXX"}。
//         */
//        // Would need work in org.apache.logging.log4j.core.util.datetime.FixedDateFormat.writeTime(int, char[], int)
//        // 需要在 org.apache.logging.log4j.core.util.datetime.FixedDateFormat.writeTime(int, char[], int) 中进行修改
//        ISO8601_OFFSET_DATE_TIME("yyyy-MM-dd'T'HH:mmXXX", "yyyy-MM-dd'T'", 2, ':', 1, ' ', 0, 0, FixedTimeZoneFormat.XXX),

        /**
         * ISO8601 time format: {@code "yyyy-MM-dd'T'HH:mm:ss,SSSX"} with a time zone like {@code -07}.
         * ISO8601 时间格式：{@code "yyyy-MM-dd'T'HH:mm:ss,SSSX"}，带有时区，例如 {@code -07}。
         */
        ISO8601_OFFSET_DATE_TIME_HH("yyyy-MM-dd'T'HH:mm:ss,SSSX", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, FixedTimeZoneFormat.HH),

        /**
         * ISO8601 time format: {@code "yyyy-MM-dd'T'HH:mm:ss,SSSXX"} with a time zone like {@code -0700}.
         * ISO8601 时间格式：{@code "yyyy-MM-dd'T'HH:mm:ss,SSSXX"}，带有时区，例如 {@code -0700}。
         */
        ISO8601_OFFSET_DATE_TIME_HHMM("yyyy-MM-dd'T'HH:mm:ss,SSSXX", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, FixedTimeZoneFormat.HHMM),

        /**
         * ISO8601 time format: {@code "yyyy-MM-dd'T'HH:mm:ss,SSSXXX"} with a time zone like {@code -07:00}.
         * ISO8601 时间格式：{@code "yyyy-MM-dd'T'HH:mm:ss,SSSXXX"}，带有时区，例如 {@code -07:00}。
         */
        ISO8601_OFFSET_DATE_TIME_HHCMM("yyyy-MM-dd'T'HH:mm:ss,SSSXXX", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, FixedTimeZoneFormat.HHCMM),

        /**
         * ISO8601 time format: {@code "yyyy-MM-dd'T'HH:mm:ss.SSS"}.
         * ISO8601 时间格式：{@code "yyyy-MM-dd'T'HH:mm:ss.SSS"}。
         */
        ISO8601_PERIOD("yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'", 2, ':', 1, '.', 1, 3, null),

        /**
         * ISO8601 time format with support for microsecond precision: {@code "yyyy-MM-dd'T'HH:mm:ss.nnnnnn"}.
         * 支持微秒精度的 ISO8601 时间格式：{@code "yyyy-MM-dd'T'HH:mm:ss.nnnnnn"}。
         */
        ISO8601_PERIOD_MICROS("yyyy-MM-dd'T'HH:mm:ss.nnnnnn", "yyyy-MM-dd'T'", 2, ':', 1, '.', 1, 6, null),

        /**
         * American date/time format with 2-digit year: {@code "dd/MM/yy HH:mm:ss.SSS"}.
         * 美国日期/时间格式，两位年份：{@code "dd/MM/yy HH:mm:ss.SSS"}。
         */
        US_MONTH_DAY_YEAR2_TIME("dd/MM/yy HH:mm:ss.SSS", "dd/MM/yy ", 0, ':', 1, '.', 1, 3, null),

        /**
         * American date/time format with 4-digit year: {@code "dd/MM/yyyy HH:mm:ss.SSS"}.
         * 美国日期/时间格式，四位年份：{@code "dd/MM/yyyy HH:mm:ss.SSS"}。
         */
        US_MONTH_DAY_YEAR4_TIME("dd/MM/yyyy HH:mm:ss.SSS", "dd/MM/yyyy ", 0, ':', 1, '.', 1, 3, null);

        private static final String DEFAULT_SECOND_FRACTION_PATTERN = "SSS";
        // 默认的秒级分数模式
        private static final int MILLI_FRACTION_DIGITS = DEFAULT_SECOND_FRACTION_PATTERN.length();
        // 毫秒级分数位数
        private static final char SECOND_FRACTION_PATTERN = 'n';
        // 秒级分数模式字符

        private final String pattern; // 完整模式字符串
        private final String datePattern; // 日期模式字符串
        private final int escapeCount; // 转义字符计数
        private final char timeSeparatorChar; // 时间分隔符字符
        private final int timeSeparatorLength; // 时间分隔符长度
        private final char millisSeparatorChar; // 毫秒分隔符字符
        private final int millisSeparatorLength; // 毫秒分隔符长度
        private final int secondFractionDigits; // 秒级分数位数
        private final FixedTimeZoneFormat fixedTimeZoneFormat; // 固定时区格式

        FixedFormat(final String pattern, final String datePattern, final int escapeCount, final char timeSeparator,
                    final int timeSepLength, final char millisSeparator, final int millisSepLength,
                    final int secondFractionDigits, final FixedTimeZoneFormat timeZoneFormat) {
            this.timeSeparatorChar = timeSeparator;
            this.timeSeparatorLength = timeSepLength;
            this.millisSeparatorChar = millisSeparator;
            this.millisSeparatorLength = millisSepLength;
            this.pattern = Objects.requireNonNull(pattern);
            this.datePattern = datePattern; // may be null
            this.escapeCount = escapeCount;
            this.secondFractionDigits = secondFractionDigits;
            this.fixedTimeZoneFormat = timeZoneFormat;
        }

        /**
         * Returns the full pattern.
         *
         * @return the full pattern
         * 返回完整模式。
         *
         * @return 完整模式
         */
        public String getPattern() {
            return pattern;
        }

        /**
         * Returns the date part of the pattern.
         *
         * @return the date part of the pattern
         * 返回模式中的日期部分。
         *
         * @return 模式中的日期部分
         */
        public String getDatePattern() {
            return datePattern;
        }

        /**
         * Returns the FixedFormat with the name or pattern matching the specified string or {@code null} if not found.
         *
         * @param nameOrPattern the name or pattern to find a FixedFormat for
         * @return the FixedFormat with the name or pattern matching the specified string
         * 返回与指定字符串匹配的名称或模式的 FixedFormat，如果未找到则返回 {@code null}。
         *
         * @param nameOrPattern 用于查找 FixedFormat 的名称或模式
         * @return 与指定字符串匹配的名称或模式的 FixedFormat
         */
        public static FixedFormat lookup(final String nameOrPattern) {
            for (final FixedFormat type : FixedFormat.values()) {
                if (type.name().equals(nameOrPattern) || type.getPattern().equals(nameOrPattern)) {
                    return type;
                }
            }
            return null;
        }

        static FixedFormat lookupIgnoringNanos(final String pattern) {
            // 查找忽略纳秒部分的固定格式
            final int[] nanoRange = nanoRange(pattern); // 获取纳秒范围
            final int nanoStart = nanoRange[0]; // 纳秒部分的起始索引
            final int nanoEnd = nanoRange[1]; // 纳秒部分的结束索引
            if (nanoStart > 0) { // 如果存在纳秒部分
                // 构建不含纳秒部分的子模式，用默认的毫秒模式替换
                final String subPattern = pattern.substring(0, nanoStart) + DEFAULT_SECOND_FRACTION_PATTERN
                        + pattern.substring(nanoEnd, pattern.length());
                for (final FixedFormat type : FixedFormat.values()) { // 遍历所有固定格式
                    if (type.getPattern().equals(subPattern)) { // 如果模式匹配
                        return type; // 返回匹配的固定格式
                    }
                }
            }
            return null; // 未找到匹配的固定格式
        }

        private final static int[] EMPTY_RANGE = { -1, -1 };
        // 空范围常量，表示未找到纳秒部分

        /**
         * @return int[0] start index inclusive; int[1] end index exclusive
         * @return int[0] 起始索引（包含）；int[1] 结束索引（不包含）
         */
        private static int[] nanoRange(final String pattern) {
            // 获取模式中纳秒部分的范围
            final int indexStart = pattern.indexOf(SECOND_FRACTION_PATTERN); // 查找 'n' 字符的起始索引
            int indexEnd = -1; // 结束索引
            if (indexStart >= 0) { // 如果找到了 'n'
                indexEnd = pattern.indexOf('Z', indexStart); // 查找 'Z' 字符（UTC 时区）
                indexEnd = indexEnd < 0 ? pattern.indexOf('X', indexStart) : indexEnd; // 如果没有 'Z'，查找 'X' 字符（ISO 8601 时区）
                indexEnd = indexEnd < 0 ? pattern.length() : indexEnd; // 如果都没有，则结束索引为模式的长度
                for (int i = indexStart + 1; i < indexEnd; i++) { // 遍历 'n' 之后的字符
                    if (pattern.charAt(i) != SECOND_FRACTION_PATTERN) { // 如果不是 'n'
                        return EMPTY_RANGE; // 返回空范围，表示纳秒部分不连续
                    }
                }
            }
            return new int [] {indexStart, indexEnd}; // 返回纳秒部分的起始和结束索引
        }

        /**
         * Returns the length of the resulting formatted date and time strings.
         *
         * @return the length of the resulting formatted date and time strings
         * 返回格式化后的日期和时间字符串的长度。
         *
         * @return 格式化后的日期和时间字符串的长度
         */
        public int getLength() {
            return pattern.length() - escapeCount;
        }

        /**
         * Returns the length of the date part of the resulting formatted string.
         *
         * @return the length of the date part of the resulting formatted string
         * 返回格式化字符串中日期部分的长度。
         *
         * @return 格式化字符串中日期部分的长度
         */
        public int getDatePatternLength() {
            return getDatePattern() == null ? 0 : getDatePattern().length() - escapeCount;
        }

        /**
         * Returns the {@code FastDateFormat} object for formatting the date part of the pattern or {@code null} if the
         * pattern does not have a date part.
         *
         * @return the {@code FastDateFormat} object for formatting the date part of the pattern or {@code null}
         * 返回用于格式化模式日期部分的 {@code FastDateFormat} 对象，如果模式不包含日期部分则返回 {@code null}。
         *
         * @return 用于格式化模式日期部分的 {@code FastDateFormat} 对象或 {@code null}
         */
        public FastDateFormat getFastDateFormat() {
            return getFastDateFormat(null);
        }

        /**
         * Returns the {@code FastDateFormat} object for formatting the date part of the pattern or {@code null} if the
         * pattern does not have a date part.
         *
         * @param tz the time zone to use
         * @return the {@code FastDateFormat} object for formatting the date part of the pattern or {@code null}
         * 返回用于格式化模式日期部分的 {@code FastDateFormat} 对象，如果模式不包含日期部分则返回 {@code null}。
         *
         * @param tz 要使用时区
         * @return 用于格式化模式日期部分的 {@code FastDateFormat} 对象或 {@code null}
         */
        public FastDateFormat getFastDateFormat(final TimeZone tz) {
            return getDatePattern() == null ? null : FastDateFormat.getInstance(getDatePattern(), tz);
        }

        /**
         * Returns the number of digits specifying the fraction of the second to show
         * @return 3 for millisecond precision, 6 for microsecond precision or 9 for nanosecond precision
         * 返回显示秒的精度位数。
         * @return 3 表示毫秒精度，6 表示微秒精度，9 表示纳秒精度
         */
        public int getSecondFractionDigits() {
            return secondFractionDigits;
        }

        /**
         * Returns the optional time zone format.
         * @return the optional time zone format, may be null.
         * 返回可选的时区格式。
         * @return 可选的时区格式，可能为 null。
         */
        public FixedTimeZoneFormat getFixedTimeZoneFormat() {
            return fixedTimeZoneFormat;
        }
    }

    private static final char NONE = (char) 0;
    // 空字符常量，表示无

    /**
     * Fixed time zone formats. The enum names are symbols from Java's <a href=
     * "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html">DateTimeFormatter</a>.
     * 
     * @see <a href=
     * "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html">DateTimeFormatter</a>
     * 固定时区格式。枚举名称是 Java 的 <a href=
     * "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html">DateTimeFormatter</a> 中的符号。
     *
     * @see <a href=
     * "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html">DateTimeFormatter</a>
     */
    public enum FixedTimeZoneFormat {

        /**
         * Offset like {@code -07}.
         * 偏移量，例如 {@code -07}。
         */
        HH(NONE, false, 3),

        /**
         * Offset like {@code -0700}.
         * 偏移量，例如 {@code -0700}。
         */
        HHMM(NONE, true, 5), 
        
        /** 
         * Offset like {@code -07:00}.
         * 偏移量，例如 {@code -07:00}。
         */
        HHCMM(':', true, 6);
        
        private FixedTimeZoneFormat() {
            this(NONE, true, 4);
        }

        private FixedTimeZoneFormat(final char timeSeparatorChar, final boolean minutes, final int length) {
            this.timeSeparatorChar = timeSeparatorChar;
            this.timeSeparatorCharLen = timeSeparatorChar != NONE ? 1 : 0;
            this.useMinutes = minutes;
            this.length = length;
        }

        private final char timeSeparatorChar; // 时间分隔符字符
        private final int timeSeparatorCharLen; // 时间分隔符字符长度
        private final boolean useMinutes; // 是否使用分钟
        // The length includes 1 for the leading sign 
        // 长度包括前导符号的1
        private final int length; // 格式化后的长度

        public int getLength() {
            return length;
        }

        // Profiling showed this method is important to log4j performance. Modify with care!
        // 262 bytes (will be inlined when hot enough: <= -XX:FreqInlineSize=325 bytes on Linux)
        // 性能分析显示此方法对 log4j 性能很重要。修改时请谨慎！
        // 262 字节（当足够热时将被内联：在 Linux 上 <= -XX:FreqInlineSize=325 字节）
        private int write(final int offset, final char[] buffer, int pos) {
            // This method duplicates part of writeTime()
            // 此方法复制了 writeTime() 的部分逻辑

            buffer[pos++] = offset < 0 ? '-' : '+'; // 写入符号（正或负）
            final int absOffset = Math.abs(offset); // 获取偏移量的绝对值
            final int hours = absOffset / 3600000; // 计算小时数
            int ms = absOffset - (3600000 * hours); // 计算剩余的毫秒数

            // Hour
            // 小时
            int temp = hours / 10; // 小时数的十位
            buffer[pos++] = ((char) (temp + '0')); // 写入小时的十位

            // Do subtract to get remainder instead of doing % 10
            // 使用减法获取余数，而不是使用 % 10
            buffer[pos++] = ((char) (hours - 10 * temp + '0')); // 写入小时的个位

            // Minute
            // 分钟
            if (useMinutes) { // 如果使用分钟
                buffer[pos] = timeSeparatorChar; // 写入时间分隔符
                pos += timeSeparatorCharLen; // 增加位置
                final int minutes = ms / 60000; // 计算分钟数
                ms -= 60000 * minutes; // 计算剩余的毫秒数

                temp = minutes / 10; // 分钟的十位
                buffer[pos++] = ((char) (temp + '0')); // 写入分钟的十位

                // Do subtract to get remainder instead of doing % 10
                // 使用减法获取余数，而不是使用 % 10
                buffer[pos++] = ((char) (minutes - 10 * temp + '0')); // 写入分钟的个位
            }
            return pos; // 返回新的位置
        }

    }

    private final FixedFormat fixedFormat; // 固定时间格式
    private final TimeZone timeZone; // 时区
    private final int length; // 格式化后的字符串总长度
    private final int secondFractionDigits; // 秒的小数位数
    private final FastDateFormat fastDateFormat; // 用于日期部分格式化，可能为null
    private final char timeSeparatorChar; // 时间分隔符字符
    private final char millisSeparatorChar; // 毫秒分隔符字符
    private final int timeSeparatorLength; // 时间分隔符长度
    private final int millisSeparatorLength; // 毫秒分隔符长度
    private final FixedTimeZoneFormat fixedTimeZoneFormat; // 固定时区格式，可能为null

    private volatile long midnightToday; // 今天午夜的毫秒数
    private volatile long midnightTomorrow; // 明天午夜的毫秒数
    private final int[] dstOffsets = new int[25]; // 夏令时偏移量数组

    // cachedDate does not need to be volatile because
    // there is a write to a volatile field *after* cachedDate is modified,
    // and there is a read from a volatile field *before* cachedDate is read.
    // The Java memory model guarantees that because of the above,
    // changes to cachedDate in one thread are visible to other threads.
    // See http://g.oswego.edu/dl/jmm/cookbook.html
    // cachedDate 不需要是 volatile 的，因为
    // 在 cachedDate 修改后会有一个对 volatile 字段的写入，
    // 并且在 cachedDate 读取前会有一个对 volatile 字段的读取。
    // Java 内存模型保证由于上述原因，
    // 一个线程对 cachedDate 的更改对其他线程是可见的。
    // 参见 http://g.oswego.edu/dl/jmm/cookbook.html
    private char[] cachedDate; // 缓存的日期字符数组，可能为null
    private int dateLength; // 缓存日期的长度

    /**
     * Constructs a FixedDateFormat for the specified fixed format.
     * <p>
     * Package protected for unit tests.
     *
     * @param fixedFormat the fixed format
     * @param tz time zone
     * 为指定的固定格式构造一个 FixedDateFormat。
     * <p>
     * 包内保护，用于单元测试。
     * @param fixedFormat 固定格式
     * @param tz 时区
     */
    FixedDateFormat(final FixedFormat fixedFormat, final TimeZone tz) {
        this(fixedFormat, tz, fixedFormat.getSecondFractionDigits());
    }

    /**
     * Constructs a FixedDateFormat for the specified fixed format.
     * <p>
     * Package protected for unit tests.
     * </p>
     *
     * @param fixedFormat the fixed format
     * @param tz time zone
     * @param secondFractionDigits the number of digits specifying the fraction of the second to show
     * 为指定的固定格式构造一个 FixedDateFormat。
     * <p>
     * 包内保护，用于单元测试。
     * </p>
     * @param fixedFormat 固定格式
     * @param tz 时区
     * @param secondFractionDigits 指定秒的小数部分的位数
     */
    FixedDateFormat(final FixedFormat fixedFormat, final TimeZone tz, final int secondFractionDigits) {
        this.fixedFormat = Objects.requireNonNull(fixedFormat);
        this.timeZone = Objects.requireNonNull(tz);
        this.timeSeparatorChar = fixedFormat.timeSeparatorChar;
        this.timeSeparatorLength = fixedFormat.timeSeparatorLength;
        this.millisSeparatorChar = fixedFormat.millisSeparatorChar;
        this.millisSeparatorLength = fixedFormat.millisSeparatorLength;
        this.fixedTimeZoneFormat = fixedFormat.fixedTimeZoneFormat; // may be null
        this.length = fixedFormat.getLength();
        this.secondFractionDigits = Math.max(1, Math.min(9, secondFractionDigits));
        this.fastDateFormat = fixedFormat.getFastDateFormat(tz);
    }

    public static FixedDateFormat createIfSupported(final String... options) {
        // 如果选项为空或第一个选项为 null，则创建默认格式
        if (options == null || options.length == 0 || options[0] == null) {
            return new FixedDateFormat(FixedFormat.DEFAULT, TimeZone.getDefault());
        }
        final TimeZone tz; // 声明时区变量
        if (options.length > 1) { // 如果提供了第二个选项（时区）
            if (options[1] != null) {
                tz = TimeZone.getTimeZone(options[1]); // 使用指定的时区
            } else {
                tz = TimeZone.getDefault(); // 否则使用默认时区
            }
        } else {
            tz = TimeZone.getDefault(); // 否则使用默认时区
        }

        final String option0 = options[0]; // 获取第一个选项，即格式模式
        final FixedFormat withoutNanos = FixedFormat.lookupIgnoringNanos(option0); // 查找忽略纳秒部分的固定格式
        if (withoutNanos != null) { // 如果找到了忽略纳秒的格式
            final int[] nanoRange = FixedFormat.nanoRange(option0); // 获取纳秒部分的范围
            final int nanoStart = nanoRange[0]; // 纳秒部分的起始索引
            final int nanoEnd = nanoRange[1]; // 纳秒部分的结束索引
            final int secondFractionDigits = nanoEnd - nanoStart; // 计算秒的小数位数
            return new FixedDateFormat(withoutNanos, tz, secondFractionDigits); // 创建 FixedDateFormat 实例
        }
        final FixedFormat type = FixedFormat.lookup(option0); // 查找完全匹配的固定格式
        return type == null ? null : new FixedDateFormat(type, tz); // 返回 FixedDateFormat 实例或 null
    }

    /**
     * Returns a new {@code FixedDateFormat} object for the specified {@code FixedFormat} and a {@code TimeZone.getDefault()} TimeZone.
     *
     * @param format the format to use
     * @return a new {@code FixedDateFormat} object
     * 返回一个使用指定 {@code FixedFormat} 和 {@code TimeZone.getDefault()} 时区的新 {@code FixedDateFormat} 对象。
     *
     * @param format 要使用的格式
     * @return 一个新的 {@code FixedDateFormat} 对象
     */
    public static FixedDateFormat create(final FixedFormat format) {
        return new FixedDateFormat(format, TimeZone.getDefault());
    }

    /**
     * Returns a new {@code FixedDateFormat} object for the specified {@code FixedFormat} and TimeZone.
     *
     * @param format the format to use
     * @param tz the time zone to use
     * @return a new {@code FixedDateFormat} object
     * 返回一个使用指定 {@code FixedFormat} 和 {@code TimeZone} 的新 {@code FixedDateFormat} 对象。
     *
     * @param format 要使用的格式
     * @param tz 要使用的时区
     * @return 一个新的 {@code FixedDateFormat} 对象
     */
    public static FixedDateFormat create(final FixedFormat format, final TimeZone tz) {
        return new FixedDateFormat(format, tz != null ? tz : TimeZone.getDefault());
    }

    /**
     * Returns the full pattern of the selected fixed format.
     *
     * @return the full date-time pattern
     * 返回所选固定格式的完整模式。
     *
     * @return 完整的日期-时间模式
     */
    public String getFormat() {
        return fixedFormat.getPattern();
    }

    /**
     * Returns the time zone.
     *
     * @return the time zone
     * 返回时区。
     *
     * @return 时区
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * <p>Returns the number of milliseconds since midnight in the time zone that this {@code FixedDateFormat}
     * was constructed with for the specified currentTime.</p>
     * <p>As a side effect, this method updates the cached formatted date and the cached date demarcation timestamps
     * when the specified current time is outside the previously set demarcation timestamps for the start or end
     * of the current day.</p>
     * @param currentTime the current time in millis since the epoch
     * @return the number of milliseconds since midnight for the specified time
     * <p>返回此 {@code FixedDateFormat} 构造时所用时区中，指定 {@code currentTime} 距午夜的毫秒数。</p>
     * <p>副作用是，当指定当前时间超出先前设置的当天开始或结束的时间戳时，此方法会更新缓存的格式化日期和缓存的日期标记时间戳。</p>
     * @param currentTime 当前时间（自纪元以来的毫秒数）
     * @return 指定时间距午夜的毫秒数
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 30 bytes (allows immediate JVM inlining: <= -XX:MaxInlineSize=35 bytes)
    // 性能分析显示此方法对 log4j 性能很重要。修改时请谨慎！
    // 30 字节（允许立即 JVM 内联：<= -XX:MaxInlineSize=35 字节）
    public long millisSinceMidnight(final long currentTime) {
        // 如果当前时间超出今天午夜或早于今天午夜，则更新午夜时间
        if (currentTime >= midnightTomorrow || currentTime < midnightToday) {
            updateMidnightMillis(currentTime); // 更新午夜毫秒数
        }
        return currentTime - midnightToday; // 返回当前时间距今天午夜的毫秒数
    }

    private void updateMidnightMillis(final long now) {
        // 如果当前时间超出明天午夜或早于今天午夜，则需要更新
        if (now >= midnightTomorrow || now < midnightToday) {
            synchronized (this) { // 同步锁定，防止多线程问题
                updateCachedDate(now); // 更新缓存的日期
                midnightToday = calcMidnightMillis(now, 0); // 计算并设置今天午夜的毫秒数
                midnightTomorrow = calcMidnightMillis(now, 1); // 计算并设置明天午夜的毫秒数

                updateDaylightSavingTime(); // 更新夏令时信息
            }
        }
    }

    private long calcMidnightMillis(final long time, final int addDays) {
        // 根据给定时间和天数偏移量计算午夜的毫秒数
        final Calendar cal = Calendar.getInstance(timeZone); // 获取指定时区的日历实例
        cal.setTimeInMillis(time); // 设置日历时间为传入的毫秒数
        cal.set(Calendar.HOUR_OF_DAY, 0); // 将小时设置为0
        cal.set(Calendar.MINUTE, 0); // 将分钟设置为0
        cal.set(Calendar.SECOND, 0); // 将秒设置为0
        cal.set(Calendar.MILLISECOND, 0); // 将毫秒设置为0
        cal.add(Calendar.DATE, addDays); // 根据 addDays 增加或减少天数
        return cal.getTimeInMillis(); // 返回计算后的午夜毫秒数
    }

    private void updateDaylightSavingTime() {
        // 更新夏令时偏移量
        Arrays.fill(dstOffsets, 0); // 将 dstOffsets 数组填充为0
        final int ONE_HOUR = (int) TimeUnit.HOURS.toMillis(1); // 一小时的毫秒数
        // 检查时区偏移量是否在一天内发生变化（即是否存在夏令时）
        if (timeZone.getOffset(midnightToday) != timeZone.getOffset(midnightToday + 23 * ONE_HOUR)) {
            // 如果存在夏令时变化
            for (int i = 0; i < dstOffsets.length; i++) {
                final long time = midnightToday + i * ONE_HOUR; // 计算每小时的时间点
                // 计算每个小时的夏令时偏移量（相对于原始偏移量）
                dstOffsets[i] = timeZone.getOffset(time) - timeZone.getRawOffset();
            }
            if (dstOffsets[0] > dstOffsets[23]) { // 如果时钟倒退（例如从夏令时回到标准时间）
                // 我们通过 Calendar.getInstance(TimeZone) 获得 midnightTonight，所以它已经包含了原始偏移量
                for (int i = dstOffsets.length - 1; i >= 0; i--) {
                    dstOffsets[i] -= dstOffsets[0]; // 调整偏移量以使其相对于当天的起始偏移量
                }
            }
        }
    }

    private void updateCachedDate(final long now) {
        // 更新缓存的日期字符串
        if (fastDateFormat != null) { // 如果日期格式化器存在
            // 使用 FastDateFormat 格式化当前时间，并将结果追加到 StringBuilder
            final StringBuilder result = fastDateFormat.format(now, new StringBuilder());
            cachedDate = result.toString().toCharArray(); // 将格式化后的日期字符串转换为字符数组并缓存
            dateLength = result.length(); // 记录缓存日期的长度
        }
    }

    public String formatInstant(final Instant instant) {
        // 格式化 Instant 对象为字符串
        final char[] result = new char[length << 1]; // 分配两倍长度的字符数组，以应对某些语言环境中的长日期符号
        final int written = formatInstant(instant, result, 0); // 格式化 Instant 并返回写入的字符数
        return new String(result, 0, written); // 将字符数组转换为字符串并返回
    }

    public int formatInstant(final Instant instant, final char[] buffer, final int startPos) {
        // 格式化 Instant 对象到指定的字符缓冲区
        final long epochMillisecond = instant.getEpochMillisecond(); // 获取 Instant 的毫秒数
        int result = format(epochMillisecond, buffer, startPos); // 格式化日期和时间部分
        result -= digitsLessThanThree(); // 根据秒的小数位数调整结果长度
        // 格式化纳秒部分，并更新当前写入位置
        final int pos = formatNanoOfMillisecond(instant.getNanoOfMillisecond(), buffer, startPos + result);
        return writeTimeZone(epochMillisecond, buffer, pos); // 写入时区信息并返回最终写入位置
    }

    private int digitsLessThanThree() { // in case user specified only 1 or 2 'n' format characters
        // 如果用户只指定了 1 或 2 个 'n' 格式字符，返回少于三位的数字
        return Math.max(0, FixedFormat.MILLI_FRACTION_DIGITS - secondFractionDigits);
    }

    // Profiling showed this method is important to log4j performance. Modify with care!
    // 28 bytes (allows immediate JVM inlining: <= -XX:MaxInlineSize=35 bytes)
    // 性能分析显示此方法对 log4j 性能很重要。修改时请谨慎！
    // 28 字节（允许立即 JVM 内联：<= -XX:MaxInlineSize=35 字节）
    public String format(final long epochMillis) {
        // 格式化纪元毫秒数为字符串
        final char[] result = new char[length << 1]; // 分配两倍长度的字符数组，以应对某些语言环境中的长日期符号
        final int written = format(epochMillis, result, 0); // 格式化毫秒数并返回写入的字符数
        return new String(result, 0, written); // 将字符数组转换为字符串并返回
    }

    // Profiling showed this method is important to log4j performance. Modify with care!
    // 31 bytes (allows immediate JVM inlining: <= -XX:MaxInlineSize=35 bytes)
    // 性能分析显示此方法对 log4j 性能很重要。修改时请谨慎！
    // 31 字节（允许立即 JVM 内联：<= -XX:MaxInlineSize=35 字节）
    public int format(final long epochMillis, final char[] buffer, final int startPos) {
        // Calculate values by getting the ms values first and do then
        // calculate the hour minute and second values divisions.
        // 首先获取毫秒值，然后计算小时、分钟和秒的值。

        // Get daytime in ms: this does fit into an int
        // 获取白天的毫秒数：这可以放入 int
        // int ms = (int) (time % 86400000);
        final int ms = (int) (millisSinceMidnight(epochMillis)); // 计算从午夜到当前时间的毫秒数
        writeDate(buffer, startPos); // 写入日期部分到缓冲区
        final int pos = writeTime(ms, buffer, startPos + dateLength); // 写入时间部分到缓冲区
        return pos - startPos; // 返回写入的总长度
    }

    // Profiling showed this method is important to log4j performance. Modify with care!
    // 22 bytes (allows immediate JVM inlining: <= -XX:MaxInlineSize=35 bytes)
    // 性能分析显示此方法对 log4j 性能很重要。修改时请谨慎！
    // 22 字节（允许立即 JVM 内联：<= -XX:MaxInlineSize=35 字节）
    private void writeDate(final char[] buffer, final int startPos) {
        // 写入缓存的日期部分到缓冲区
        if (cachedDate != null) { // 如果缓存日期不为空
            System.arraycopy(cachedDate, 0, buffer, startPos, dateLength); // 复制缓存日期到目标缓冲区
        }
    }

    // Profiling showed this method is important to log4j performance. Modify with care!
    // 262 bytes (will be inlined when hot enough: <= -XX:FreqInlineSize=325 bytes on Linux)
    // 性能分析显示此方法对 log4j 性能很重要。修改时请谨慎！
    // 262 字节（当足够热时将被内联：在 Linux 上 <= -XX:FreqInlineSize=325 字节）
    private int writeTime(int ms, final char[] buffer, int pos) {
        // 写入时间部分到缓冲区
        final int hourOfDay = ms / 3600000; // 计算一天中的小时数
        // 计算小时数，考虑夏令时偏移
        final int hours = hourOfDay + daylightSavingTime(hourOfDay) / 3600000;
        ms -= 3600000 * hourOfDay; // 从总毫秒数中减去小时的毫秒数

        final int minutes = ms / 60000; // 计算分钟数
        ms -= 60000 * minutes; // 从剩余毫秒数中减去分钟的毫秒数

        final int seconds = ms / 1000; // 计算秒数
        ms -= 1000 * seconds; // 从剩余毫秒数中减去秒的毫秒数

        // Hour
        // 小时
        int temp = hours / 10; // 小时数的十位
        buffer[pos++] = ((char) (temp + '0')); // 写入小时的十位

        // Do subtract to get remainder instead of doing % 10
        // 使用减法获取余数，而不是使用 % 10
        buffer[pos++] = ((char) (hours - 10 * temp + '0')); // 写入小时的个位
        buffer[pos] = timeSeparatorChar; // 写入时间分隔符
        pos += timeSeparatorLength; // 增加位置

        // Minute
        // 分钟
        temp = minutes / 10; // 分钟的十位
        buffer[pos++] = ((char) (temp + '0')); // 写入分钟的十位

        // Do subtract to get remainder instead of doing % 10
        // 使用减法获取余数，而不是使用 % 10
        buffer[pos++] = ((char) (minutes - 10 * temp + '0')); // 写入分钟的个位
        buffer[pos] = timeSeparatorChar; // 写入时间分隔符
        pos += timeSeparatorLength; // 增加位置

        // Second
        // 秒
        temp = seconds / 10; // 秒的十位
        buffer[pos++] = ((char) (temp + '0')); // 写入秒的十位
        buffer[pos++] = ((char) (seconds - 10 * temp + '0')); // 写入秒的个位
        buffer[pos] = millisSeparatorChar; // 写入毫秒分隔符
        pos += millisSeparatorLength; // 增加位置

        // Millisecond
        // 毫秒
        temp = ms / 100; // 毫秒的百位
        buffer[pos++] = ((char) (temp + '0')); // 写入毫秒的百位

        ms -= 100 * temp; // 从剩余毫秒数中减去百位的毫秒数
        temp = ms / 10; // 毫秒的十位
        buffer[pos++] = ((char) (temp + '0')); // 写入毫秒的十位

        ms -= 10 * temp; // 从剩余毫秒数中减去十位的毫秒数
        buffer[pos++] = ((char) (ms + '0')); // 写入毫秒的个位
        return pos; // 返回新的位置
    }

    private int writeTimeZone(final long epochMillis, final char[] buffer, int pos) {
        // 写入时区信息到缓冲区
        if (fixedTimeZoneFormat != null) { // 如果固定时区格式不为空
            // 使用固定时区格式写入时区偏移量
            pos = fixedTimeZoneFormat.write(timeZone.getOffset(epochMillis), buffer, pos);
        }
        return pos; // 返回新的位置
    }

    static int[] TABLE = {
            100000, // 0
            10000, // 1
            1000, // 2
            100, // 3
            10, // 4
            1, // 5
    }; // 用于纳秒格式化的除数表

    private int formatNanoOfMillisecond(final int nanoOfMillisecond, final char[] buffer, int pos) {
        // 格式化纳秒的毫秒部分到缓冲区
        int temp; // 临时变量
        int remain = nanoOfMillisecond; // 剩余的纳秒数
        // 遍历需要额外格式化的纳秒位数（超出毫秒精度的部分）
        for (int i = 0; i < secondFractionDigits - FixedFormat.MILLI_FRACTION_DIGITS; i++) {
            final int divisor = TABLE[i]; // 获取对应的除数
            temp = remain / divisor; // 计算当前位的数字
            buffer[pos++] = ((char) (temp + '0')); // 写入当前位数字的字符形式
            remain -= divisor * temp; // 从剩余纳秒数中减去已处理的部分（相当于 remain % 10）
        }
        return pos; // 返回新的位置
    }

    private int daylightSavingTime(final int hourOfDay) {
        // 根据小时数返回夏令时偏移量
        // 如果小时数大于23，则返回数组中最后一个（第23个索引）的偏移量，否则返回对应小时的偏移量
        return hourOfDay > 23 ? dstOffsets[23] : dstOffsets[hourOfDay];
    }

    /**
     * Returns {@code true} if the old and new date values will result in the same formatted output, {@code false}
     * if results <i>may</i> differ.
     * 如果旧的日期值和新的日期值将导致相同的格式化输出，则返回 {@code true}，否则返回 {@code false}。
     */
    public boolean isEquivalent(long oldEpochSecond, int oldNanoOfSecond, long epochSecond, int nanoOfSecond) {
        // 检查两个时间戳是否在格式化后等价
        if (oldEpochSecond == epochSecond) { // 如果秒数相同
            if (secondFractionDigits <= 3) {
                // Convert nanos to milliseconds for comparison if the format only requires milliseconds.
                // 如果格式只要求毫秒，则将纳秒转换为毫秒进行比较。
                return (oldNanoOfSecond / 1000_000L) == (nanoOfSecond / 1000_000L); // 比较毫秒部分
            }
            return oldNanoOfSecond == nanoOfSecond; // 否则直接比较纳秒部分
        }
        return false; // 秒数不同，则不等价
    }
}
