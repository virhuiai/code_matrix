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

/**
 * Extension of the {@link Clock} interface that is able to provide more accurate time information than milliseconds
 * since the epoch. {@code PreciseClock} implementations are free to return millisecond-precision time
 * if that is the most accurate time information available on this platform.
 * Clock 接口的扩展，能够提供比自 epoch 以来的毫秒更精确的时间信息。
 * 如果平台只能提供毫秒精度的时间信息，PreciseClock 实现也可以返回毫秒精度的时间。
 * @since 2.11
 * 自 2.11 版本开始引入。
 */
public interface PreciseClock extends Clock {

    /**
     * Initializes the specified instant with time information as accurate as available on this platform.
     * 使用当前平台可用的最精确时间信息初始化指定的瞬间。
     * @param mutableInstant the container to be initialized with the accurate time information
     * 参数 mutableInstant：一个可变的瞬间对象，将用精确的时间信息进行初始化。
     * @since 2.11
     * 自 2.11 版本开始引入。
     */
    void init(final MutableInstant mutableInstant);
}
