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
 * Provides the high-resolution time stamp used in log events.
 * 提供用于日志事件的高分辨率时间戳。
 */
public interface NanoClock {
    /**
     * Returns the current value of the running Java Virtual Machine's high-resolution time source, in nanoseconds.
     * 返回当前运行的 Java 虚拟机的高分辨率时间源的当前值，单位为纳秒。
     *
     * @return the current value of the running Java Virtual Machine's high-resolution time source, in nanoseconds
     * @return 当前运行的 Java 虚拟机的高分辨率时间源的当前值，单位为纳秒
     */
    long nanoTime();
}
