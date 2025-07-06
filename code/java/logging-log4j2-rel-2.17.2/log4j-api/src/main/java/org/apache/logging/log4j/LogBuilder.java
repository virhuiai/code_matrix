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
package org.apache.logging.log4j;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;


/**
 * Interface for constructing log events before logging them. Instances of LogBuilders should only be created
 * by calling one of the Logger methods that return a LogBuilder.
 */
// 中文注释：定义了用于在记录日志前构建日志事件的接口。LogBuilder实例应仅通过调用Logger类中返回LogBuilder的方法创建。
// 主要功能：提供链式调用方法以配置日志事件的属性（如Marker、Throwable、堆栈信息等），最终触发日志记录。
// 注意事项：这是一个接口，默认方法为空实现，具体功能需由实现类完成。
public interface LogBuilder {

    public static final LogBuilder NOOP = new LogBuilder() {};
    // 中文注释：定义一个空的LogBuilder实现（NOOP），不执行任何操作，用于默认或占位场景。
    // 关键变量说明：NOOP是一个静态常量，表示无操作的LogBuilder实例。

    /**
     * Includes a Marker in the log event. Interface default method does nothing.
     * @param marker The Marker to log.
     * @return The LogBuilder.
     */
    // 中文注释：将指定的Marker包含到日志事件中。接口默认方法不执行任何操作。
    // 参数说明：marker - 用于标记日志事件的Marker对象，用于分类或过滤日志。
    // 方法目的：支持链式调用，允许在日志事件中添加Marker。
    // 注意事项：默认实现为空，需由具体实现类处理Marker的添加逻辑。
    default LogBuilder withMarker(Marker marker) {
        return this;
    }

    /**
     * Includes a Throwable in the log event. Interface default method does nothing.
     * @param throwable The Throwable to log.
     * @return the LogBuilder.
     */
    // 中文注释：将指定的Throwable异常包含到日志事件中。接口默认方法不执行任何操作。
    // 参数说明：throwable - 要记录的异常对象，用于提供错误上下文。
    // 方法目的：支持链式调用，允许在日志事件中添加异常信息。
    // 注意事项：默认实现为空，需由实现类处理异常的记录逻辑。
    default LogBuilder withThrowable(Throwable throwable) {
        return this;
    }

    /**
     * An implementation will calculate the caller's stack frame and include it in the log event.
     * Interface default method does nothing.
     * @return The LogBuilder.
     */
    // 中文注释：计算调用者的堆栈框架并包含到日志事件中。接口默认方法不执行任何操作。
    // 方法目的：支持链式调用，添加调用者的堆栈信息以便追踪日志来源。
    // 注意事项：默认实现为空，需由实现类完成堆栈信息的计算和添加。
    default LogBuilder withLocation() {
        return this;
    }

    /**
     * Adds the specified stack trace element to the log event. Interface default method does nothing.
     * @param location The stack trace element to include in the log event.
     * @return The LogBuilder.
     */
    // 中文注释：将指定的堆栈跟踪元素添加到日志事件中。接口默认方法不执行任何操作。
    // 参数说明：location - 要包含的堆栈跟踪元素，用于指定日志的调用位置。
    // 方法目的：支持链式调用，允许手动指定堆栈信息。
    // 注意事项：默认实现为空，需由实现类处理堆栈元素的添加逻辑。
    default LogBuilder withLocation(StackTraceElement location) {
        return this;
    }

    /**
     * Causes all the data collected to be logged along with the message. Interface default method does nothing.
     * @param message The message to log.
     */
    // 中文注释：触发记录收集的所有数据及指定的消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的字符序列消息。
    // 方法目的：执行日志记录操作，将配置的数据和消息写入日志。
    // 注意事项：默认实现为空，需由实现类完成日志的实际写入。
    default void log(CharSequence message) {
    }

    /**
     * Causes all the data collected to be logged along with the message. Interface default method does nothing.
     * @param message The message to log.
     */
    // 中文注释：触发记录收集的所有数据及指定的字符串消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的字符串消息。
    // 方法目的：执行日志记录操作，将配置的数据和消息写入日志。
    // 注意事项：默认实现为空，需由实现类完成日志的实际写入。
    default void log(String message) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param params parameters to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；params - 消息的参数，支持动态替换。
    // 方法目的：支持带参数的日志记录，允许动态填充消息内容。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object... params) {
    }

    /**
     * Causes all the data collected to be logged along with the message and parameters.
     * Interface default method does nothing.
     * @param message The message.
     * @param params Parameters to the message.
     */
    // 中文注释：记录带参数的日志消息及收集的数据。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息；params - 消息的Supplier参数，提供延迟加载的消息内容。
    // 方法目的：支持延迟加载参数的日志记录，优化性能。
    // 注意事项：默认实现为空，需由实现类处理Supplier参数的解析和日志记录。
    default void log(String message, Supplier<?>... params) {
    }

    /**
     * Causes all the data collected to be logged along with the message. Interface default method does nothing.
     * @param message The message to log.
     */
    // 中文注释：触发记录收集的所有数据及指定的Message对象。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的Message对象，封装了日志消息的结构化内容。
    // 方法目的：支持结构化日志消息的记录。
    // 事件处理逻辑：调用此方法触发日志记录事件，将Message对象写入日志。
    // 注意事项：默认实现为空，需由实现类完成Message对象的处理和日志记录。
    default void log(Message message) {
    }

    /**
     * Causes all the data collected to be logged along with the message. Interface default method does nothing.
     * @param messageSupplier The supplier of the message to log.
     */
    // 中文注释：触发记录收集的所有数据及通过Supplier提供的消息。接口默认方法不执行任何操作。
    // 参数说明：messageSupplier - 提供日志消息的Supplier，延迟生成消息内容。
    // 方法目的：支持延迟加载日志消息，优化性能。
    // 事件处理逻辑：调用此方法触发日志记录事件，通过Supplier获取消息并写入日志。
    // 注意事项：默认实现为空，需由实现类处理Supplier的调用和日志记录。
    default void log(Supplier<Message> messageSupplier) {
    }

    /**
     * Causes all the data collected to be logged along with the message. Interface default method does nothing.
     * @param message The message to log.
     */
    // 中文注释：触发记录收集的所有数据及指定的对象消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的对象消息，自动转换为字符串。
    // 方法目的：支持记录任意对象的日志消息。
    // 注意事项：默认实现为空，需由实现类处理对象的字符串转换和日志记录。
    default void log(Object message) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带单个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0 - 消息的第一个参数。
    // 方法目的：支持带单个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带两个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1 - 消息的前两个参数。
    // 方法目的：支持带两个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带三个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2 - 消息的前三个参数。
    // 方法目的：支持带三个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带四个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2, p3 - 消息的前四个参数。
    // 方法目的：支持带四个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2, Object p3) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带五个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2, p3, p4 - 消息的前五个参数。
    // 方法目的：支持带五个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带六个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2, p3, p4, p5 - 消息的前六个参数。
    // 方法目的：支持带六个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
    }

    /**
     * Logs a message with parameters.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带七个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2, p3, p4, p5, p6 - 消息的前七个参数。
    // 方法目的：支持带七个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带八个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2, p3, p4, p5, p6, p7 - 消息的前八个参数。
    // 方法目的：支持带八个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带九个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2, p3, p4, p5, p6, p7, p8 - 消息的前九个参数。
    // 方法目的：支持带九个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8) {
    }

    /**
     * Logs a message with parameters. Interface default method does nothing.
     *
     * @param message the message to log; the format depends on the message factory.
     * @param p0 parameter to the message.
     * @param p1 parameter to the message.
     * @param p2 parameter to the message.
     * @param p3 parameter to the message.
     * @param p4 parameter to the message.
     * @param p5 parameter to the message.
     * @param p6 parameter to the message.
     * @param p7 parameter to the message.
     * @param p8 parameter to the message.
     * @param p9 parameter to the message.
     *
     * @see org.apache.logging.log4j.util.Unbox
     */
    // 中文注释：记录带十个参数的日志消息。接口默认方法不执行任何操作。
    // 参数说明：message - 要记录的消息，格式取决于消息工厂；p0, p1, p2, p3, p4, p5, p6, p7, p8, p9 - 消息的前十个参数。
    // 方法目的：支持带十个参数的日志记录，简化调用。
    // 注意事项：默认实现为空，需由实现类处理参数格式化和日志记录。参考Unbox工具类以优化参数处理。
    default void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9) {
    }

    /**
     * Causes all the data collected to be logged. Default implementatoin does nothing.
     */
    // 中文注释：触发记录收集的所有数据。接口默认方法不执行任何操作。
    // 方法目的：执行日志记录操作，将所有配置的数据写入日志，无需指定消息。
    // 注意事项：默认实现为空，需由实现类完成日志的实际写入。
    default void log() {
    }
}
