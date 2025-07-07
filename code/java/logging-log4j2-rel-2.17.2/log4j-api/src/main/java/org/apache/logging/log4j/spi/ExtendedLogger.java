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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

/**
 * Extends the {@code Logger} interface with methods that facilitate implementing or extending {@code Logger}s. Users
 * should not need to use this interface.
 */
// 类的主要功能和目的：该接口扩展了Logger接口，提供了更多用于实现或扩展Logger的方法。
// 注意事项：通常情况下，用户不应该直接使用此接口。它主要用于框架内部的实现。
public interface ExtendedLogger extends Logger {

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The Message.
     * @param t A Throwable.
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、消息和可抛出对象的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。用于更细粒度的日志过滤。
    // - message: 日志消息对象。
    // - t: 伴随日志的可抛出异常对象。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, Message message, Throwable t);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param t A Throwable.
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、字符序列消息和可抛出对象的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（CharSequence类型）。
    // - t: 伴随日志的可抛出异常对象。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param t A Throwable.
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、对象消息和可抛出对象的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（Object类型）。
    // - t: 伴随日志的可抛出异常对象。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, Object message, Throwable t);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @return True if logging is enabled, false otherwise.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、字符串消息和可抛出对象的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（String类型）。
    // - t: 要记录的异常，包括其堆栈跟踪。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Throwable t);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记和字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（String类型）。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param params The parameters.
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - params: 用于格式化消息的参数数组。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object... params);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带一个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带两个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带三个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带四个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带五个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带六个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5);

    /**
     * Determines if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带七个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @param p7 the message parameters
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带八个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    // - p7: 消息参数7。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
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
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带九个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    // - p7: 消息参数7。
    // - p8: 消息参数8。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7, Object p8);

    /**
     * Tests if logging is enabled.
     *
     * @param level The logging Level to check.
     * @param marker A Marker or null.
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
     * @return True if logging is enabled, false otherwise.
     */
    // 方法的主要功能和目的：检查给定日志级别、标记、带十个参数的字符串消息的情况下，日志功能是否启用。
    // 参数说明：
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    // - p7: 消息参数7。
    // - p8: 消息参数8。
    // - p9: 消息参数9。
    // 返回值：如果日志功能启用则返回true，否则返回false。
    boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7, Object p8, Object p9);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The Message.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名。在需要记录位置信息（如类名和方法名）时，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息对象。
    // - t: 要记录的异常，包括其堆栈跟踪。
    void logIfEnabled(String fqcn, Level level, Marker marker, Message message, Throwable t);

    /**
     * Logs a CharSequence message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The CharSequence message.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个字符序列消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（CharSequence类型）。
    // - t: 要记录的异常，包括其堆栈跟踪。
    void logIfEnabled(String fqcn, Level level, Marker marker, CharSequence message, Throwable t);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个对象消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（Object类型）。
    // - t: 要记录的异常，包括其堆栈跟踪。
    void logIfEnabled(String fqcn, Level level, Marker marker, Object message, Throwable t);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（String类型）。
    // - t: 要记录的异常，包括其堆栈跟踪。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Throwable t);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个字符串消息（不带异常）。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息（String类型）。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param params The message parameters.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - params: 用于格式化消息的参数数组。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object... params);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带一个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带两个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带三个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带四个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
            Object p3);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带五个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
            Object p3, Object p4);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带六个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
            Object p3, Object p4, Object p5);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带七个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
            Object p3, Object p4, Object p5, Object p6);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @param p7 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带八个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    // - p7: 消息参数7。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
            Object p3, Object p4, Object p5, Object p6, Object p7);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param p0 the message parameters
     * @param p1 the message parameters
     * @param p2 the message parameters
     * @param p3 the message parameters
     * @param p4 the message parameters
     * @param p5 the message parameters
     * @param p6 the message parameters
     * @param p7 the message parameters
     * @param p8 the message parameters
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带九个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    // - p7: 消息参数7。
    // - p8: 消息参数8。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
            Object p3, Object p4, Object p5, Object p6, Object p7, Object p8);

    /**
     * Logs a message if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
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
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个带十个参数的格式化字符串消息。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - p0: 消息参数0。
    // - p1: 消息参数1。
    // - p2: 消息参数2。
    // - p3: 消息参数3。
    // - p4: 消息参数4。
    // - p5: 消息参数5。
    // - p6: 消息参数6。
    // - p7: 消息参数7。
    // - p8: 消息参数8。
    // - p9: 消息参数9。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Object p0, Object p1, Object p2,
            Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9);

    /**
     * Logs a message at the specified level. It is the responsibility of the caller to ensure the specified
     * level is enabled.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The Message.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：在指定的日志级别记录一个消息。调用者有责任确保指定的级别已启用。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要记录的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 日志消息对象。
    // - t: 要记录的异常，包括其堆栈跟踪。
    void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t);

    /**
     * Logs a message which is only to be constructed if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param msgSupplier A function, which when called, produces the desired log message.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则通过消息供应器（MessageSupplier）记录一个消息。消息仅在需要时才被构造。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - msgSupplier: 一个函数式接口，当调用时会产生所需的日志消息。
    // - t: 要记录的异常，包括其堆栈跟踪。
    // 特殊处理逻辑和注意事项：使用消息供应器可以延迟消息的构建，从而在日志级别未启用时避免不必要的性能开销。
    void logIfEnabled(String fqcn, Level level, Marker marker, MessageSupplier msgSupplier, Throwable t);

    /**
     * Logs a message whose parameters are only to be constructed if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param message The message format.
     * @param paramSuppliers An array of functions, which when called, produce the desired log message parameters.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则记录一个消息，其参数仅在需要时通过参数供应器（Supplier）构建。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - message: 格式化字符串消息。
    // - paramSuppliers: 一个函数数组，当调用时会产生所需的日志消息参数。
    // 特殊处理逻辑和注意事项：使用参数供应器可以延迟消息参数的构建，从而在日志级别未启用时避免不必要的性能开销。
    void logIfEnabled(String fqcn, Level level, Marker marker, String message, Supplier<?>... paramSuppliers);

    /**
     * Logs a message which is only to be constructed if the specified level is active.
     *
     * @param fqcn The fully qualified class name of the logger entry point, used to determine the caller class and
     *            method when location information needs to be logged.
     * @param level The logging Level to check.
     * @param marker A Marker or null.
     * @param msgSupplier A function, which when called, produces the desired log message.
     * @param t the exception to log, including its stack trace.
     */
    // 方法的主要功能和目的：如果指定的日志级别处于活动状态，则通过通用供应器（Supplier）记录一个消息。消息仅在需要时才被构造。
    // 参数说明：
    // - fqcn: 日志入口点的完全限定类名，用于确定调用者类和方法。
    // - level: 要检查的日志级别。
    // - marker: 日志标记，可以为null。
    // - msgSupplier: 一个函数式接口，当调用时会产生所需的日志消息。
    // - t: 要记录的异常，包括其堆栈跟踪。
    // 特殊处理逻辑和注意事项：与MessageSupplier类似，使用通用供应器可以延迟消息的构建，优化性能。
    void logIfEnabled(String fqcn, Level level, Marker marker, Supplier<?> msgSupplier, Throwable t);

}
