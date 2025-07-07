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
package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;

/**
 * Logger that accepts the location of the caller.
 * 接受调用者位置信息的日志记录器接口。
 */
public interface LocationAwareLogger {
    /**
     * Logs a message with the location of the caller.
     * 使用调用者位置信息记录一条消息。
     *
     * @param level The logging Level.
     * 日志级别。
     * @param marker The Marker for the message.
     * 消息的标记。
     * @param fqcn The fully qualified class name of the caller. This is used to identify the caller correctly
     * when filtering stack trace elements.
     * 调用者的完全限定类名。这用于在过滤堆栈跟踪元素时正确识别调用者。
     * @param location The StackTraceElement of the caller.
     * 调用者的堆栈跟踪元素。
     * @param message The message to log.
     * 要记录的消息。
     * @param throwable The Throwable associated with the message, if any.
     * 与消息关联的异常，如果没有则为null。
     */
    void logMessage(final Level level, final Marker marker, final String fqcn, final StackTraceElement location,
        final Message message, final Throwable throwable);
}
