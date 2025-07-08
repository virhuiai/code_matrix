/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license license agreements. See the NOTICE file distributed with
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
package org.apache.logging.log4j.core.util;

/**
 * Implementation of the {@code NanoClock} interface that always returns a fixed value.
 * 实现 NanoClock 接口的类，它总是返回一个固定的纳秒时间值。
 */
public final class DummyNanoClock implements NanoClock {

    private final long fixedNanoTime; // 存储固定的纳秒时间值

    /**
     * Constructs a new DummyNanoClock with a default fixed value of 0L.
     * 构造一个新的 DummyNanoClock 实例，默认返回值为 0L。
     */
    public DummyNanoClock() {
        this(0L);
    }

    /**
     * Constructs a new DummyNanoClock with the specified value to return.
     * 构造一个新的 DummyNanoClock 实例，并指定其 {@link #nanoTime()} 方法将返回的固定值。
     * @param fixedNanoTime the value to return from {@link #nanoTime()}.
     * 从 {@link #nanoTime()} 方法返回的固定纳秒时间值。
     */
    public DummyNanoClock(final long fixedNanoTime) {
        // 初始化 fixedNanoTime 变量
        this.fixedNanoTime = fixedNanoTime;
    }

    /**
     * Returns the constructor value.
     * 返回构造函数中设置的固定纳秒时间值。
     *
     * @return the constructor value
     * 构造时传入的固定值。
     */
    @Override
    public long nanoTime() {
        // 返回预设的固定纳秒时间值，不进行实际的纳秒时间测量。
        return fixedNanoTime;
    }

}
