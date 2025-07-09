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
import org.apache.logging.log4j.util.StringBuilderFormattable;

/**
 * Models a point in time, suitable for event timestamps.
 * * 表示一个时间点，适用于事件时间戳。
 * * <p>
 * Provides methods for obtaining high precision time information similar to the
 * <a href="https://docs.oracle.com/javase/9/docs/api/java/time/Instant.html">Instant</a> class introduced in Java 8,
 * while also supporting the legacy millisecond precision API.
 * </p><p>
 * 提供了获取高精度时间信息的方法，类似于Java 8中引入的`Instant`类，
 * 同时还支持传统的毫秒精度API。
 * * Depending on the platform, time sources ({@link Clock} implementations) may produce high precision or millisecond
 * precision time values. At the same time, some time value consumers (for example timestamp formatters) may only be
 * able to consume time values of millisecond precision, while some others may require a high precision time value.
 * </p><p>
 * 根据不同的平台，时间源（`Clock`接口的实现）可能会生成高精度或毫秒精度的时间值。
 * 同时，一些时间值消费者（例如时间戳格式化器）可能只能消费毫秒精度的时间值，
 * 而另一些则可能需要高精度的时间值。
 * * This class bridges these two time APIs.
 * </p>
 * 该类用于连接这两种时间API（高精度和毫秒精度）。
 * * @since 2.11
 * 从版本2.11开始引入。
 */
public interface Instant extends StringBuilderFormattable {
    /**
     * Gets the number of seconds from the Java epoch of 1970-01-01T00:00:00Z.
     * * 获取自Java纪元1970-01-01T00:00:00Z以来的秒数。
     * * <p>
     * The epoch second count is a simple incrementing count of seconds where second 0 is 1970-01-01T00:00:00Z.
     * The nanosecond part of the day is returned by {@link #getNanoOfSecond()}.
     * </p>
     * 纪元秒数是一个简单的递增秒计数，其中第0秒是1970-01-01T00:00:00Z。
     * 纳秒部分由`getNanoOfSecond()`方法返回。
     * * @return the seconds from the epoch of 1970-01-01T00:00:00Z
     * 返回自1970-01-01T00:00:00Z纪元以来的秒数。
     */
    long getEpochSecond();

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start of the second.
     * * 获取从当前秒开始，沿时间线向后推移的纳秒数。
     * * <p>
     * The nanosecond-of-second value measures the total number of nanoseconds from the second returned by {@link #getEpochSecond()}.
     * </p>
     * 秒内的纳秒值衡量的是从`getEpochSecond()`方法返回的秒数开始的总纳秒数。
     * * @return the nanoseconds within the second, always positive, never exceeds {@code 999,999,999}
     * 返回秒内的纳秒数，总是正数，永不超过999,999,999。
     */
    int getNanoOfSecond();

    /**
     * Gets the number of milliseconds from the Java epoch of 1970-01-01T00:00:00Z.
     * * 获取自Java纪元1970-01-01T00:00:00Z以来的毫秒数。
     * * <p>
     * The epoch millisecond count is a simple incrementing count of milliseconds where millisecond 0 is 1970-01-01T00:00:00Z.
     * The nanosecond part of the day is returned by {@link #getNanoOfMillisecond()}.
     * </p>
     * 纪元毫秒数是一个简单的递增毫秒计数，其中第0毫秒是1970-01-01T00:00:00Z。
     * 纳秒部分由`getNanoOfMillisecond()`方法返回。
     * * @return the milliseconds from the epoch of 1970-01-01T00:00:00Z
     * 返回自1970-01-01T00:00:00Z纪元以来的毫秒数。
     */
    long getEpochMillisecond();

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start of the millisecond.
     * * 获取从当前毫秒开始，沿时间线向后推移的纳秒数。
     * * <p>
     * The nanosecond-of-millisecond value measures the total number of nanoseconds from the millisecond returned by {@link #getEpochMillisecond()}.
     * </p>
     * 毫秒内的纳秒值衡量的是从`getEpochMillisecond()`方法返回的毫秒数开始的总纳秒数。
     * * @return the nanoseconds within the millisecond, always positive, never exceeds {@code 999,999}
     * 返回毫秒内的纳秒数，总是正数，永不超过999,999。
     */
    int getNanoOfMillisecond();
}
