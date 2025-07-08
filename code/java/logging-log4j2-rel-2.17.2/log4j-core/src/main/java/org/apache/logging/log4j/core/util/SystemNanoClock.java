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
package org.apache.logging.log4j.core.util;

/**
 * Implementation of the {@code NanoClock} interface that returns the system nano time.
 * 实现 NanoClock 接口，返回系统纳秒时间。
 */
public final class SystemNanoClock implements NanoClock { // 定义一个名为 SystemNanoClock 的公共最终类，实现了 NanoClock 接口。

    /**
     * Returns the system high-resolution time.
     * 返回系统的高精度时间。
     * @return the result of calling {@code System.nanoTime()}
     * 返回调用 System.nanoTime() 方法的结果。
     */
    @Override // 标记此方法覆盖了父接口 NanoClock 中的方法。
    public long nanoTime() { // 定义一个公共方法 nanoTime，返回一个长整型数值。
        return System.nanoTime(); // 调用 System.nanoTime() 方法获取当前系统的高精度时间（纳秒），并返回该值。
    }

}
