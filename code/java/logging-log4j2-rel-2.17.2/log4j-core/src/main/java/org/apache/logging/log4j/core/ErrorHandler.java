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
 * Appenders may delegate their error handling to <code>ErrorHandlers</code>.
 * Appender 可以将它们的错误处理委托给 `ErrorHandlers`。
 * TODO if the appender interface is simplified, then error handling could just be done by wrapping
 *  a nested appender. (RG) Please look at DefaultErrorHandler. It's purpose is to make sure the console
 * or error log isn't flooded with messages. I'm still considering the Appender refactoring.
 * TODO 如果 appender 接口被简化，那么错误处理可以通过包装一个嵌套的 appender 来完成。(RG) 请查看 DefaultErrorHandler。它的目的是确保控制台或错误日志不会被消息淹没。我仍在考虑 Appender 的重构。
 */
public interface ErrorHandler {

    /**
     * Handle an error with a message.
     * 处理带有消息的错误。
     * @param msg The message.
     * 参数 msg：错误消息。
     */
    void error(String msg);

    /**
     * Handle an error with a message and an exception.
     * 处理带有消息和异常的错误。
     * @param msg The message.
     * 参数 msg：错误消息。
     * @param t The Throwable.
     * 参数 t：Throwable 对象，表示发生的异常。
     */
    void error(String msg, Throwable t);

    /**
     * Handle an error with a message, and exception and a logging event.
     * 处理带有消息、异常和日志事件的错误。
     * @param msg The message.
     * 参数 msg：错误消息。
     * @param event The LogEvent.
     * 参数 event：LogEvent 对象，表示触发错误的日志事件。
     * @param t The Throwable.
     * 参数 t：Throwable 对象，表示发生的异常。
     */
    void error(String msg, LogEvent event, Throwable t);
}
