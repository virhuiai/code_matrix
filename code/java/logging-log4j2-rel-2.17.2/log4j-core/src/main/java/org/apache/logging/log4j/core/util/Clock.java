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
 * Provides the time stamp used in log events.
 * 为日志事件提供时间戳。
 */
public interface Clock {
    /**
     * Returns the time in milliseconds since the epoch.
     * 返回自 Epoch（1970年1月1日00:00:00 GMT）以来的毫秒数。
     * @return the time in milliseconds since the epoch
     * @return 自 Epoch 以来的毫秒数
     */
    long currentTimeMillis();
}
