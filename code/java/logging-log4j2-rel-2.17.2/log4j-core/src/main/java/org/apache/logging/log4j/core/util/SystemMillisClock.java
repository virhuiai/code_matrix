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
 * Implementation of the {@code Clock} interface that returns the system time in millisecond granularity.
 * Clock接口的实现，以毫秒为粒度返回系统时间。
 * @since 2.11
 */
public final class SystemMillisClock implements Clock {

    /**
     * Returns the system time.
     * 返回系统时间。
     * @return the result of calling {@code System.currentTimeMillis()}
     * 返回调用 {@code System.currentTimeMillis()} 的结果
     */
    @Override
    public long currentTimeMillis() {
        // This method returns the current time in milliseconds.
        // 此方法返回当前时间的毫秒数。
        return System.currentTimeMillis();
    }

}
