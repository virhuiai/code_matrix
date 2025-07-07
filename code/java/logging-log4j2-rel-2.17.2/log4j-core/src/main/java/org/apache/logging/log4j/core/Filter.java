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
/*
 * 根据Apache软件基金会的一个或多个贡献者许可协议获得许可。
 * 请参阅本作品分发的NOTICE文件，了解版权所有权的其他信息。
 * Apache软件基金会根据Apache许可证2.0版（“许可证”）将此文件授权给您；
 * 除非符合许可证的规定，否则您不得使用此文件。
 * 您可以在以下网址获取许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则根据许可证分发的软件是“按原样”分发的，
 * 不附带任何明示或暗示的保证或条件。
 * 请参阅许可证以了解管理权限和限制的特定语言。
 */

package org.apache.logging.log4j.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.EnglishEnums;

/**
 * Interface that must be implemented to allow custom event filtering. It is highly recommended that
 * applications make use of the Filters provided with this implementation before creating their own.
 *
 * <p>This interface supports "global" filters (i.e. - all events must pass through them first), attached to
 * specific loggers and associated with Appenders. It is recommended that, where possible, Filter implementations
 * create a generic filtering method that can be called from any of the filter methods.</p>
 */
/**
 * 必须实现的接口，用于允许自定义事件过滤。
 * 强烈建议应用程序在使用此实现提供的过滤器之前，不要创建自己的过滤器。
 *
 * <p>此接口支持“全局”过滤器（即所有事件都必须首先通过它们），
 * 也可以附加到特定的日志记录器并与Appenders关联。
 * 建议在可能的情况下，过滤器实现创建一个通用的过滤方法，可以从任何过滤方法中调用。</p>
 */
public interface Filter extends LifeCycle {

    /**
     * The empty array.
     */
    /**
     * 空数组，用于表示没有过滤器的情况。
     */
    Filter[] EMPTY_ARRAY = {};

    /**
     * Main {@linkplain org.apache.logging.log4j.core.config.plugins.Plugin#elementType() plugin element type} for
     * Filter plugins.
     *
     * @since 2.1
     */
    /**
     * Filter 插件的主要 {@linkplain org.apache.logging.log4j.core.config.plugins.Plugin#elementType() 插件元素类型}。
     *
     * @since 2.1 版本引入
     */
    String ELEMENT_TYPE = "filter";

    /**
     * The result that can returned from a filter method call.
     */
    /**
     * 过滤器方法调用可能返回的结果。
     */
     enum Result {
        /**
         * The event will be processed without further filtering based on the log Level.
         */
        /**
         * 事件将被接受并处理，无需基于日志级别进行进一步过滤。
         */
        ACCEPT,
        /**
         * No decision could be made, further filtering should occur.
         */
        /**
         * 无法做出决定，应进行进一步过滤（例如，由链中的下一个过滤器处理）。
         */
        NEUTRAL,
        /**
         * The event should not be processed.
         */
        /**
         * 事件不应被处理，将被拒绝。
         */
        DENY;

        /**
         * Returns the Result for the given string.
         *
         * @param name The Result enum name, case-insensitive. If null, returns, null
         * @return a Result enum value or null if name is null
         */
        /**
         * 根据给定的字符串返回对应的 Result 枚举值。
         *
         * @param name Result 枚举的名称，不区分大小写。如果为 null，则返回 null。
         * @return 一个 Result 枚举值，如果 name 为 null 则返回 null。
         */
        public static Result toResult(final String name) {
            return toResult(name, null);
        }

        /**
         * Returns the Result for the given string.
         *
         * @param name The Result enum name, case-insensitive. If null, returns, defaultResult
         * @param defaultResult the Result to return if name is null
         * @return a Result enum value or null if name is null
         */
        /**
         * 根据给定的字符串返回对应的 Result 枚举值。
         *
         * @param name Result 枚举的名称，不区分大小写。如果为 null，则返回 defaultResult。
         * @param defaultResult 如果 name 为 null 时要返回的默认 Result 值。
         * @return 一个 Result 枚举值，如果 name 为 null 则返回 defaultResult。
         */
        public static Result toResult(final String name, final Result defaultResult) {
            return EnglishEnums.valueOf(Result.class, name, defaultResult);
        }
}

    /**
     * Returns the result that should be returned when the filter does not match the event.
     * @return the Result that should be returned when the filter does not match the event.
     */
    /**
     * 返回当过滤器不匹配事件时应返回的结果。
     * 此方法定义了当事件不符合过滤器条件时的默认行为。
     * @return 当过滤器不匹配事件时应返回的 Result 枚举值。
     */
    Result getOnMismatch();
    /**
     * Returns the result that should be returned when the filter matches the event.
     * @return the Result that should be returned when the filter matches the event.
     */
    /**
     * 返回当过滤器匹配事件时应返回的结果。
     * 此方法定义了当事件符合过滤器条件时的默认行为。
     * @return 当过滤器匹配事件时应返回的 Result 枚举值。
     */
    Result getOnMatch();

    /**
     * Filter an event.
     * @param logger The Logger.
     * @param level The event logging Level.
     * @param marker The Marker for the event or null.
     * @param msg String text to filter on.
     * @param params An array of parameters or null.
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有可变参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param msg 用于过滤的字符串文本消息。
     * @param params 消息的参数数组，如果没有则为 null。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String msg, Object... params);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有一个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有两个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有三个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有四个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @param p3 消息的第四个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有五个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @param p3 消息的第四个参数。
     * @param p4 消息的第五个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有六个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @param p3 消息的第四个参数。
     * @param p4 消息的第五个参数。
     * @param p5 消息的第六个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有七个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @param p3 消息的第四个参数。
     * @param p4 消息的第五个参数。
     * @param p5 消息的第六个参数。
     * @param p6 消息的第七个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @param p7 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有八个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @param p3 消息的第四个参数。
     * @param p4 消息的第五个参数。
     * @param p5 消息的第六个参数。
     * @param p6 消息的第七个参数。
     * @param p7 消息的第八个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @param p7 the message parameters
     * @param p8 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有九个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @param p3 消息的第四个参数。
     * @param p4 消息的第五个参数。
     * @param p5 消息的第六个参数。
     * @param p6 消息的第七个参数。
     * @param p7 消息的第八个参数。
     * @param p8 消息的第九个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7, Object p8);

    /**
     * Filter an event.
     *
     * @param logger The Logger.
     * @param level the event logging level.
     * @param marker The Marker for the event or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @param p7 the message parameters
     * @param p8 the message parameters
     * @param p9 the message parameters
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理带有十个参数的字符串消息。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param message 字符串消息。
     * @param p0 消息的第一个参数。
     * @param p1 消息的第二个参数。
     * @param p2 消息的第三个参数。
     * @param p3 消息的第四个参数。
     * @param p4 消息的第五个参数。
     * @param p5 消息的第六个参数。
     * @param p6 消息的第七个参数。
     * @param p7 消息的第八个参数。
     * @param p8 消息的第九个参数。
     * @param p9 消息的第十个参数。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7, Object p8, Object p9);

    /**
     * Filter an event.
     * @param logger The Logger.
     * @param level The event logging Level.
     * @param marker The Marker for the event or null.
     * @param msg Any Object.
     * @param t A Throwable or null.
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理任意对象作为消息和可选的 Throwable。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param msg 任何对象作为消息内容。
     * @param t 与事件关联的 Throwable 异常，如果没有则为 null。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t);

    /**
     * Filter an event.
     * @param logger The Logger.
     * @param level The event logging Level.
     * @param marker The Marker for the event or null.
     * @param msg The Message
     * @param t A Throwable or null.
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理 Log4j Message 对象作为消息和可选的 Throwable。
     * @param logger 产生此事件的 Logger 实例。
     * @param level 事件的日志级别。
     * @param marker 事件的 Marker，如果没有则为 null。
     * @param msg Log4j 的 Message 对象。
     * @param t 与事件关联的 Throwable 异常，如果没有则为 null。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t);

    /**
     * Filter an event.
     * @param event The Event to filter on.
     * @return the Result.
     */
    /**
     * 过滤一个日志事件。
     * 此方法用于处理完整的 LogEvent 对象。
     * @param event 要进行过滤的 LogEvent 对象。
     * @return 过滤操作的结果（ACCEPT, NEUTRAL, DENY）。
     */
    Result filter(LogEvent event);

}
