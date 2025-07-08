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
 * Implementation of the {@code Clock} interface that returns the system time.
 * 实现 Clock 接口，返回系统时间的类。
 */
public final class SystemClock implements Clock {

    /**
     * Returns the system time.
     * 返回系统时间。
     * @return the result of calling {@code System.currentTimeMillis()}
     * @return 调用 System.currentTimeMillis() 方法返回的当前时间（毫秒）。
     */
    @Override
    public long currentTimeMillis() {
        // Calls the native method to get the current time in milliseconds.
        // 调用本地方法获取当前时间的毫秒值。
        return System.currentTimeMillis();
    }

}
