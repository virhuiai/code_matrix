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

package org.apache.logging.log4j.core;

/**
 * Default implementation using the Core LoggerContext.
 * 核心日志上下文的默认实现。
 */
public class DefaultLoggerContextAccessor implements LoggerContextAccessor {

    /**
     * Singleton instance.
     * 单例实例。
     */
    public static DefaultLoggerContextAccessor INSTANCE = new DefaultLoggerContextAccessor();

    /*
     * Returns the current LoggerContext.
     * 返回当前的LoggerContext实例。
     *
     * @see org.apache.logging.log4j.core.LoggerContextAccessor#getLoggerContext()
     */
    @Override
    public LoggerContext getLoggerContext() {
        // This method returns the current LoggerContext.
        // 该方法返回当前的LoggerContext实例。
        // The LoggerContext.getContext() static method is used to retrieve the LoggerContext.
        // 通过调用LoggerContext的静态方法getContext()来获取LoggerContext实例。
        return LoggerContext.getContext();
    }

}
