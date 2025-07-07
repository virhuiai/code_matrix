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
package org.apache.logging.log4j.status;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;

import static org.apache.logging.log4j.util.Chars.*;

/**
 * The Status data.
 */
 // 类注释：
// StatusData类用于封装日志系统的状态数据，记录日志事件的关键信息，如时间戳、调用栈、日志级别、消息内容、异常信息和线程名称。
// 该类实现了Serializable接口，支持序列化，便于在分布式系统中传输或存储日志状态数据。
// 主要用途：用于Log4j日志框架的状态日志记录，方便调试和监控日志系统运行状态。
public class StatusData implements Serializable {

    private static final long serialVersionUID = -4341916115118014017L;

    // 成员变量注释：
    // timestamp: 记录日志事件发生的时间戳，单位为毫秒。
    // caller: 保存触发日志事件的调用栈信息，用于定位调用日志的代码位置。
    // level: 日志级别（如ERROR、WARN、INFO等），表示日志事件的严重程度。
    // msg: 日志消息对象，包含格式化的消息内容和可能的参数。
    // threadName: 记录触发日志事件的线程名称，用于多线程环境下的日志追踪。
    // throwable: 异常对象，可能为null，表示与日志事件关联的错误或异常。
    private final long timestamp;
    private final StackTraceElement caller;
    private final Level level;
    private final Message msg;
    private String threadName;
    private final Throwable throwable;

    /**
     * Creates the StatusData object.
     * 
     * @param caller The method that created the event.
     * @param level The logging level.
     * @param msg The message String.
     * @param t The Error or Exception that occurred.
     * @param threadName The thread name
     */
    // 方法注释：
    // 构造函数：初始化StatusData对象，封装日志事件的所有相关信息。
    // 参数说明：
    // - caller: 调用栈元素，记录触发日志的代码位置。
    // - level: 日志级别，用于区分日志的优先级或严重性。
    // - msg: 日志消息对象，包含消息内容和可能的参数。
    // - t: 与日志事件关联的异常对象，可能为null。
    // - threadName: 触发日志事件的线程名称，可能为null。
    // 执行流程：
    // 1. 使用System.currentTimeMillis()记录当前时间戳。
    // 2. 初始化所有成员变量，保存传入的参数。
    // 注意事项：threadName可能在后续通过getThreadName()方法动态获取。
    public StatusData(final StackTraceElement caller, final Level level, final Message msg, final Throwable t,
            final String threadName) {
        this.timestamp = System.currentTimeMillis();
        this.caller = caller;
        this.level = level;
        this.msg = msg;
        this.throwable = t;
        this.threadName = threadName;
    }

    /**
     * Returns the event's timestamp.
     * 
     * @return The event's timestamp.
     */
    // 方法注释：
    // getTimestamp: 获取日志事件的时间戳。
    // 返回值：长整型时间戳，表示日志事件发生的时间（毫秒）。
    // 用途：用于日志排序或时间分析。
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the StackTraceElement for the method that created the event.
     * 
     * @return The StackTraceElement.
     */
    // 方法注释：
    // getStackTraceElement: 获取触发日志事件的调用栈信息。
    // 返回值：StackTraceElement对象，包含调用日志的类、方法、行号等信息。
    // 用途：帮助开发者定位日志事件的代码位置，方便调试。
    public StackTraceElement getStackTraceElement() {
        return caller;
    }

    /**
     * Returns the logging level for the event.
     * 
     * @return The logging level.
     */
    // 方法注释：
    // getLevel: 获取日志事件的日志级别。
    // 返回值：Level对象，表示日志的严重程度（如ERROR、WARN等）。
    // 用途：用于日志过滤或显示不同级别的日志信息。
    public Level getLevel() {
        return level;
    }

    /**
     * Returns the message associated with the event.
     * 
     * @return The message associated with the event.
     */
    // 方法注释：
    // getMessage: 获取与日志事件关联的消息对象。
    // 返回值：Message对象，包含格式化的日志消息内容和可能的参数。
    // 用途：提供日志的具体内容，便于查看事件详情。
    public Message getMessage() {
        return msg;
    }

    // 方法注释：
    // getThreadName: 获取触发日志事件的线程名称。
    // 返回值：字符串，表示线程名称。
    // 执行流程：
    // 1. 如果threadName为null，则通过Thread.currentThread().getName()获取当前线程名称并缓存。
    // 2. 返回threadName值。
    // 注意事项：延迟初始化threadName以确保线程名称始终有效，适合多线程环境。
    public String getThreadName() {
        if (threadName == null) {
            threadName = Thread.currentThread().getName();
        }
        return threadName;
    }

    /**
     * Returns the Throwable associated with the event.
     * 
     * @return The Throwable associated with the event.
     */
    // 方法注释：
    // getThrowable: 获取与日志事件关联的异常对象。
    // 返回值：Throwable对象，可能为null，表示异常信息。
    // 用途：提供异常的详细信息，用于错误分析和调试。
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Formats the StatusData for viewing.
     * 
     * @return The formatted status data as a String.
     */
    // 方法注释：
    // getFormattedStatus: 将日志事件格式化为字符串，便于显示或输出。
    // 返回值：格式化后的字符串，包含时间戳、线程名称、日志级别、消息内容和异常堆栈（如果存在）。
    // 执行流程：
    // 1. 使用SimpleDateFormat格式化时间戳为"yyyy-MM-dd HH:mm:ss,SSS"格式。
    // 2. 将时间戳、线程名称、日志级别、消息内容依次拼接到StringBuilder。
    // 3. 检查是否存在异常对象（优先检查msg参数中的最后一个对象是否为Throwable）。
    // 4. 如果存在异常，将其堆栈跟踪信息追加到字符串。
    // 5. 返回格式化后的字符串。
    // 注意事项：
    // - 如果throwable为null但消息参数包含异常，则使用消息参数中的异常。
    // - 使用ByteArrayOutputStream捕获异常堆栈的打印输出，确保格式化一致。
    public String getFormattedStatus() {
        final StringBuilder sb = new StringBuilder();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        // 配置参数说明：SimpleDateFormat使用"yyyy-MM-dd HH:mm:ss,SSS"格式，确保时间戳精确到毫秒。
        sb.append(format.format(new Date(timestamp)));
        sb.append(SPACE);
        sb.append(getThreadName());
        sb.append(SPACE);
        sb.append(level.toString());
        sb.append(SPACE);
        sb.append(msg.getFormattedMessage());
        final Object[] params = msg.getParameters();
        Throwable t;
        if (throwable == null && params != null && params[params.length - 1] instanceof Throwable) {
            t = (Throwable) params[params.length - 1];
        } else {
            t = throwable;
        }
        if (t != null) {
            sb.append(SPACE);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(baos));
            sb.append(baos.toString());
        }
        return sb.toString();
    }
}
