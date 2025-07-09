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
package org.apache.logging.log4j.core.time.internal;

import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.time.PreciseClock;

/**
 * Implementation of the {@code PreciseClock} interface that always returns a fixed time value.
 * 实现 PreciseClock 接口，始终返回一个固定的时间值。
 * @since 2.11
 */
public class FixedPreciseClock implements PreciseClock {
    private final long currentTimeMillis;
    // 存储固定的毫秒时间值。
    private final int nanosOfMillisecond;
    // 存储毫秒内的纳秒值。

    /**
     * Constructs a {@code FixedPreciseClock} that always returns the epoch.
     * 构造一个 FixedPreciseClock，它总是返回纪元时间（即1970年1月1日00:00:00 GMT）。
     */
    public FixedPreciseClock() {
        this(0);
    }

    /**
     * Constructs a {@code FixedPreciseClock} that always returns the specified time in milliseconds since the epoch.
     * 构造一个 FixedPreciseClock，它总是返回从纪元开始指定毫秒数的时间。
     * @param currentTimeMillis milliseconds since the epoch
     * 从纪元开始的毫秒数。
     */
    public FixedPreciseClock(final long currentTimeMillis) {
        this(currentTimeMillis, 0);
    }

    /**
     * Constructs a {@code FixedPreciseClock} that always returns the specified time in milliseconds since the epoch
     * and nanosecond of the millisecond.
     * 构造一个 FixedPreciseClock，它总是返回从纪元开始指定毫秒数以及毫秒内的纳秒数的时间。
     * @param currentTimeMillis milliseconds since the epoch
     * 从纪元开始的毫秒数。
     * @param nanosOfMillisecond nanosecond of the specified millisecond
     * 指定毫秒内的纳秒数。
     */
    public FixedPreciseClock(final long currentTimeMillis, final int nanosOfMillisecond) {
        this.currentTimeMillis = currentTimeMillis;
        // 将传入的毫秒时间赋值给内部变量。
        this.nanosOfMillisecond = nanosOfMillisecond;
        // 将传入的纳秒时间赋值给内部变量。
    }

    @Override
    public void init(final MutableInstant instant) {
        // 实现 PreciseClock 接口的 init 方法。
        // 该方法用于初始化一个 MutableInstant 对象。
        instant.initFromEpochMilli(currentTimeMillis, nanosOfMillisecond);
        // 使用内部存储的固定毫秒和纳秒时间初始化传入的 MutableInstant 对象。
        // 这确保了无论何时调用此方法，instant 对象都将被设置为这个固定的时间。
    }

    @Override
    public long currentTimeMillis() {
        // 实现 PreciseClock 接口的 currentTimeMillis 方法。
        // 该方法用于返回当前的毫秒时间。
        return currentTimeMillis;
        // 直接返回构造函数中设置的固定毫秒时间。
        // 这意味着无论何时调用此方法，都将获得相同的固定时间值。
    }
}
