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
package org.apache.logging.log4j.core.time;

import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.util.PerformanceSensitive;

import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.MICRO_OF_SECOND;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoUnit.NANOS;

/**
 * An instantaneous point on the time line, used for high-precision log event timestamps.
 * 时间轴上的一个瞬时点，用于高精度日志事件时间戳。
 * Modeled on <a href="https://docs.oracle.com/javase/9/docs/api/index.html?java/time/class-use/Instant.html">java.time.Instant</a>,
 * 模仿了 java.time.Instant 类，
 * except that this version is mutable to prevent allocating temporary objects that need to be garbage-collected later.
 * 但这个版本是可变的，以避免分配需要后续垃圾回收的临时对象。
 * <p>
 * Instances of this class are <em>not</em> thread-safe and should not be shared between threads.
 * </p>
 * 此类的实例**不是**线程安全的，不应在线程之间共享。
 *
 * @since 2.11
 */
@PerformanceSensitive("allocation")
// 标记此类对性能敏感，特别是关于内存分配
public class MutableInstant implements Instant, Serializable, TemporalAccessor {

    private static final int MILLIS_PER_SECOND = 1000;
    // 定义每秒的毫秒数
    private static final int NANOS_PER_MILLI = 1000_000;
    // 定义每毫秒的纳秒数
    private static final int NANOS_PER_SECOND = MILLIS_PER_SECOND * NANOS_PER_MILLI;
    // 定义每秒的纳秒数

    private long epochSecond;
    // 自1970-01-01T00:00:00Z（Java epoch）以来的秒数
    private int nanoOfSecond;
    // 当前秒内的纳秒数

    @Override
    public long getEpochSecond() {
        // 获取自epoch以来的秒数
        return epochSecond;
    }

    @Override
    public int getNanoOfSecond() {
        // 获取当前秒内的纳秒数
        return nanoOfSecond;
    }

    @Override
    public long getEpochMillisecond() {
        // 获取自epoch以来的毫秒数
        final int millis = nanoOfSecond / NANOS_PER_MILLI;
        // 计算当前秒内的毫秒数
        final long epochMillisecond = epochSecond * MILLIS_PER_SECOND + millis;
        // 计算总的毫秒数
        return epochMillisecond;
    }

    @Override
    public int getNanoOfMillisecond() {
        // 获取当前毫秒内的纳秒数
        final int millis = nanoOfSecond / NANOS_PER_MILLI;
        // 计算当前秒内的毫秒数
        final int nanoOfMillisecond = nanoOfSecond - (millis * NANOS_PER_MILLI); // cheaper than nanoOfSecond % NANOS_PER_MILLI
        // 计算当前毫秒内的纳秒数，使用减法代替取模运算，效率更高
        return nanoOfMillisecond;
    }

    /**
     * 根据另一个 Instant 对象初始化此 MutableInstant。
     * @param other 用于初始化的 Instant 对象
     */
    public void initFrom(final Instant other) {
        this.epochSecond = other.getEpochSecond();
        // 设置秒数
        this.nanoOfSecond = other.getNanoOfSecond();
        // 设置纳秒数
    }

    /**
     * Updates the fields of this {@code MutableInstant} from the specified epoch millis.
     * 根据指定的毫秒纪元和毫秒内纳秒数更新此 {@code MutableInstant} 的字段。
     * @param epochMilli the number of milliseconds from the Java epoch of 1970-01-01T00:00:00Z
     * 自 1970-01-01T00:00:00Z（Java 纪元）以来的毫秒数
     * @param nanoOfMillisecond the number of nanoseconds, later along the time-line, from the start of the millisecond
     * 从毫秒开始的时间线上的纳秒数
     */
    public void initFromEpochMilli(final long epochMilli, final int nanoOfMillisecond) {
        validateNanoOfMillisecond(nanoOfMillisecond);
        // 验证毫秒内纳秒数的有效性
        this.epochSecond = epochMilli / MILLIS_PER_SECOND;
        // 根据总毫秒数计算秒数
        this.nanoOfSecond = (int) (epochMilli - (epochSecond * MILLIS_PER_SECOND)) * NANOS_PER_MILLI + nanoOfMillisecond;
        // 计算当前秒内的纳秒数，包括毫秒部分的纳秒数和额外的纳秒数
    }

    /**
     * 验证毫秒内纳秒数的有效性。
     * @param nanoOfMillisecond 毫秒内纳秒数
     * @throws IllegalArgumentException 如果毫秒内纳秒数无效
     */
    private void validateNanoOfMillisecond(final int nanoOfMillisecond) {
        if (nanoOfMillisecond < 0 || nanoOfMillisecond >= NANOS_PER_MILLI) {
            // 检查纳秒数是否在有效范围内 [0, NANOS_PER_MILLI - 1]
            throw new IllegalArgumentException("Invalid nanoOfMillisecond " + nanoOfMillisecond);
        }
    }

    /**
     * 根据 Clock 对象初始化此 MutableInstant。
     * 如果 Clock 是 PreciseClock 的实例，则使用 PreciseClock 的 init 方法进行精确初始化；
     * 否则，使用 currentTimeMillis 和 0 纳秒进行初始化。
     * @param clock 用于初始化的 Clock 对象
     */
    public void initFrom(final Clock clock) {
        if (clock instanceof PreciseClock) {
            // 如果是精确时钟，则调用其初始化方法
            ((PreciseClock) clock).init(this);
        } else {
            // 否则，使用其当前毫秒数进行初始化，纳秒部分设为0
            initFromEpochMilli(clock.currentTimeMillis(), 0);
        }
    }

    /**
     * Updates the fields of this {@code MutableInstant} from the specified instant components.
     * 根据指定的秒纪元和纳秒数更新此 {@code MutableInstant} 的字段。
     * @param epochSecond the number of seconds from the Java epoch of 1970-01-01T00:00:00Z
     * 自 1970-01-01T00:00:00Z（Java 纪元）以来的秒数
     * @param nano the number of nanoseconds, later along the time-line, from the start of the second
     * 从秒开始的时间线上的纳秒数
     */
    public void initFromEpochSecond(final long epochSecond, final int nano) {
        validateNanoOfSecond(nano);
        // 验证秒内纳秒数的有效性
        this.epochSecond = epochSecond;
        // 设置秒数
        this.nanoOfSecond = nano;
        // 设置纳秒数
    }

    /**
     * 验证秒内纳秒数的有效性。
     * @param nano 秒内纳秒数
     * @throws IllegalArgumentException 如果秒内纳秒数无效
     */
    private void validateNanoOfSecond(final int nano) {
        if (nano < 0 || nano >= NANOS_PER_SECOND) {
            // 检查纳秒数是否在有效范围内 [0, NANOS_PER_SECOND - 1]
            throw new IllegalArgumentException("Invalid nanoOfSecond " + nano);
        }
    }

    /**
     * Updates the elements of the specified {@code long[]} result array from the specified instant components.
     * 根据指定的秒纪元和纳秒数，更新给定的 {@code long[]} 结果数组的元素。
     * @param epochSecond (input) the number of seconds from the Java epoch of 1970-01-01T00:00:00Z
     * （输入）自 1970-01-01T00:00:00Z（Java 纪元）以来的秒数
     * @param nano (input) the number of nanoseconds, later along the time-line, from the start of the second
     * （输入）从秒开始的时间线上的纳秒数
     * @param result (output) a two-element array to store the result: the first element is the number of milliseconds
     * （输出）一个两元素的数组用于存储结果：
     *               from the Java epoch of 1970-01-01T00:00:00Z,
     * 第一个元素是自 1970-01-01T00:00:00Z（Java 纪元）以来的毫秒数，
     *               the second element is the number of nanoseconds, later along the time-line, from the start of the millisecond
     * 第二个元素是从毫秒开始的时间线上的纳秒数
     */
    public static void instantToMillisAndNanos(final long epochSecond, final int nano, final long[] result) {
        final int millis = nano / NANOS_PER_MILLI;
        // 计算当前秒内的毫秒数
        result[0] = epochSecond * MILLIS_PER_SECOND + millis;
        // 计算总的毫秒数并存储在结果数组的第一个元素
        result[1] = nano - (millis * NANOS_PER_MILLI); // cheaper than nanoOfSecond % NANOS_PER_MILLI
        // 计算当前毫秒内的纳秒数，并存储在结果数组的第二个元素
    }

    @Override
    public boolean isSupported(final TemporalField field) {
        // 检查是否支持给定的时间字段
        if (field instanceof ChronoField) {
            // 如果是 ChronoField 类型的字段
            return field == INSTANT_SECONDS ||
                    // 支持 INSTANT_SECONDS 字段
                    field == NANO_OF_SECOND ||
                    // 支持 NANO_OF_SECOND 字段
                    field == MICRO_OF_SECOND ||
                    // 支持 MICRO_OF_SECOND 字段
                    field == MILLI_OF_SECOND;
                    // 支持 MILLI_OF_SECOND 字段
        }
        return field != null && field.isSupportedBy(this);
        // 如果字段不为 null 且该字段支持当前 TemporalAccessor
    }

    @Override
    public long getLong(final TemporalField field) {
        // 获取指定时间字段的 long 值
        if (field instanceof ChronoField) {
            // 如果是 ChronoField 类型的字段
            switch ((ChronoField) field) {
                case NANO_OF_SECOND: return nanoOfSecond;
                // 返回秒内的纳秒数
                case MICRO_OF_SECOND: return nanoOfSecond / 1000;
                // 返回秒内的微秒数
                case MILLI_OF_SECOND: return nanoOfSecond / 1000_000;
                // 返回秒内的毫秒数
                case INSTANT_SECONDS: return epochSecond;
                // 返回自epoch以来的秒数
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            // 对于不支持的 ChronoField 抛出异常
        }
        return field.getFrom(this);
        // 对于其他类型的 TemporalField，通过字段本身获取值
    }

    @Override
    public ValueRange range(final TemporalField field) {
        // 获取指定时间字段的有效值范围
        return TemporalAccessor.super.range(field);
        // 调用父接口的默认实现
    }

    @Override
    public int get(final TemporalField field) {
        // 获取指定时间字段的 int 值
        if (field instanceof ChronoField) {
            // 如果是 ChronoField 类型的字段
            switch ((ChronoField) field) {
                case NANO_OF_SECOND: return nanoOfSecond;
                // 返回秒内的纳秒数
                case MICRO_OF_SECOND: return nanoOfSecond / 1000;
                // 返回秒内的微秒数
                case MILLI_OF_SECOND: return nanoOfSecond / 1000_000;
                // 返回秒内的毫秒数
                case INSTANT_SECONDS: INSTANT_SECONDS.checkValidIntValue(epochSecond);
                // 检查秒数是否在 int 范围内
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            // 对于不支持的 ChronoField 抛出异常
        }
        return range(field).checkValidIntValue(field.getFrom(this), field);
        // 对于其他类型的 TemporalField，先获取其范围，然后检查并返回 int 值
    }

    @Override
    public <R> R query(final TemporalQuery<R> query) {
        // 对时间对象执行查询操作
        if (query == TemporalQueries.precision()) {
            // 如果查询的是精度
            return (R) NANOS;
            // 返回纳秒精度
        }
        // inline TemporalAccessor.super.query(query) as an optimization
        // 内联 TemporalAccessor.super.query(query) 作为优化
        if (query == TemporalQueries.chronology() ||
                // 如果查询的是年表
                query == TemporalQueries.zoneId() ||
                // 如果查询的是时区ID
                query == TemporalQueries.zone() ||
                // 如果查询的是时区
                query == TemporalQueries.offset() ||
                // 如果查询的是偏移量
                query == TemporalQueries.localDate() ||
                // 如果查询的是本地日期
                query == TemporalQueries.localTime()) {
            // 如果查询的是本地时间
            return null;
            // 对于这些查询，返回 null，表示此 Instant 不包含这些信息
        }
        return query.queryFrom(this);
        // 对于其他查询，通过查询对象本身从当前实例中查询
    }

    @Override
    public boolean equals(final Object object) {
        // 比较当前对象与另一个对象是否相等
        if (object == this) {
            // 如果是同一个对象
            return true;
        }
        if (!(object instanceof MutableInstant)) {
            // 如果不是 MutableInstant 的实例
            return false;
        }
        final MutableInstant other = (MutableInstant) object;
        // 将对象转换为 MutableInstant 类型
        return epochSecond == other.epochSecond && nanoOfSecond == other.nanoOfSecond;
        // 比较秒数和纳秒数是否相等
    }

    @Override
    public int hashCode() {
        // 计算当前对象的哈希码
        int result = 17;
        // 初始化哈希码
        result = 31 * result + (int) (epochSecond ^ (epochSecond >>> 32));
        // 将秒数的高32位和低32位进行异或运算，并与当前哈希码结合
        result = 31 * result + nanoOfSecond;
        // 将纳秒数与当前哈希码结合
        return result;
    }

    @Override
    public String toString() {
        // 返回当前对象的字符串表示
        final StringBuilder sb = new StringBuilder(64);
        // 创建一个 StringBuilder，初始容量为64
        formatTo(sb);
        // 将对象的格式化字符串追加到 StringBuilder
        return sb.toString();
        // 返回 StringBuilder 的字符串
    }

    @Override
    public void formatTo(final StringBuilder buffer) {
        // 将当前对象的详细信息格式化并追加到 StringBuilder
        buffer.append("MutableInstant[epochSecond=").append(epochSecond).append(", nano=").append(nanoOfSecond).append("]");
        // 格式化输出 MutableInstant 的秒数和纳秒数
    }

}
