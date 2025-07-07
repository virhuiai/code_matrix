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
 * 中文注释：
 * 本文件由Apache软件基金会（ASF）根据Apache 2.0许可证授权。
 * 许可证详细内容可在 http://www.apache.org/licenses/LICENSE-2.0 查看。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;

import java.io.Serializable;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/**
 * An interface for various Message implementations that can be logged. Messages can act as wrappers
 * around Objects so that user can have control over converting Objects to Strings when necessary without
 * requiring complicated formatters and as a way to manipulate the message based on information available
 * at runtime such as the locale of the system.
 * <p>
 * Custom Message implementations should consider implementing the {@link StringBuilderFormattable}
 * interface for more efficient processing. Garbage-free Layouts will call
 * {@link StringBuilderFormattable#formatTo(StringBuilder) formatTo(StringBuilder)} instead of
 * {@link Message#getFormattedMessage()} if the Message implements StringBuilderFormattable.
 * </p>
 * <p>
 * Note: Message objects should not be considered to be thread safe nor should they be assumed to be
 * safely reusable even on the same thread. The logging system may provide information to the Message
 * objects and the Messages might be queued for asynchronous delivery. Thus, any modifications to a
 * Message object by an application should by avoided after the Message has been passed as a parameter on
 * a Logger method.
 * </p>
 *
 * @see StringBuilderFormattable
 */
/*
 * Implementation note: this interface extends Serializable since LogEvents must be serializable.
 * 中文注释：
 * Message 接口定义了可被日志系统记录的各种消息实现。
 *
 * 主要功能和目的：
 * - 作为对象的包装器，允许用户控制对象到字符串的转换，而无需复杂的格式化工具。
 * - 支持根据运行时信息（如系统区域设置）动态操作消息内容。
 *
 * 实现建议：
 * - 自定义消息实现应考虑实现 StringBuilderFormattable 接口，以提高处理效率。
 * - 如果实现 StringBuilderFormattable 接口，无垃圾布局（Garbage-free Layouts）会优先调用 formatTo(StringBuilder) 方法，而非 getFormattedMessage()，以减少临时字符串对象的创建。
 *
 * 注意事项：
 * - Message 对象非线程安全，且即使在同一线程上也不应假定可安全重用。
 * - 日志系统可能向 Message 对象提供信息，且消息可能被排队用于异步传递。
 * - 应用程序应避免在 Message 对象传递给 Logger 方法后对其进行修改。
 *
 * 关键接口：
 * - 继承 Serializable 接口，因为 LogEvents 必须可序列化。
 * - 可实现 StringBuilderFormattable 接口以优化格式化性能。
 */
public interface Message extends Serializable {

    /**
     * Gets the Message formatted as a String. Each Message implementation determines the
     * appropriate way to format the data encapsulated in the Message. Messages that provide
     * more than one way of formatting the Message will implement MultiformatMessage.
     * <p>
     * When configured to log asynchronously, this method is called before the Message is queued, unless this
     * message implements {@link ReusableMessage} or is annotated with {@link AsynchronouslyFormattable}.
     * This gives the Message implementation class a chance to create a formatted message String with the current value
     * of any mutable objects.
     * The intention is that the Message implementation caches this formatted message and returns it on subsequent
     * calls. (See <a href="https://issues.apache.org/jira/browse/LOG4J2-763">LOG4J2-763</a>.)
     * </p>
     * <p>
     * When logging synchronously, this method will not be called for Messages that implement the
     * {@link StringBuilderFormattable} interface: instead, the
     * {@link StringBuilderFormattable#formatTo(StringBuilder) formatTo(StringBuilder)} method will be called so the
     * Message can format its contents without creating intermediate String objects.
     * </p>
     *
     * @return The message String.
     */
    /*
     * 中文注释：
     * 方法功能：
     * - 获取消息格式化为字符串的结果。
     * - 每种 Message 实现类决定如何格式化其封装的数据。
     * - 支持多种格式化方式的消息应实现 MultiformatMessage 接口。
     *
     * 执行流程：
     * - 异步日志记录时，除非消息实现 ReusableMessage 接口或标注了 AsynchronouslyFormattable 注解，
     *   该方法会在消息入队前被调用，以生成包含可变对象当前值的格式化字符串。
     * - 实现类应缓存格式化结果，以便后续调用直接返回缓存值（参考 LOG4J2-763）。
     * - 同步日志记录时，若消息实现 StringBuilderFormattable 接口，则优先调用 formatTo(StringBuilder) 方法，
     *   以避免创建中间字符串对象。
     *
     * 返回值：
     * - 格式化后的消息字符串。
     *
     * 注意事项：
     * - 异步日志场景下，需确保格式化结果反映可变对象的当前状态。
     * - 同步日志场景下，优先使用 StringBuilderFormattable 的 formatTo 方法以优化性能。
     */
    String getFormattedMessage();

    /**
     * Gets the format portion of the Message.
     *
     * @return The message format. Some implementations, such as ParameterizedMessage, will use this as
     * the message "pattern". Other Messages may simply return an empty String.
     * TODO Do all messages have a format?  What syntax?  Using a Formatter object could be cleaner.
     * (RG) In SimpleMessage the format is identical to the formatted message. In ParameterizedMessage and
     * StructuredDataMessage it is not. It is up to the Message implementer to determine what this
     * method will return. A Formatter is inappropriate as this is very specific to the Message
     * implementation so it isn't clear to me how having a Formatter separate from the Message would be cleaner.
     */
    /*
     * 中文注释：
     * 方法功能：
     * - 获取消息的格式部分（格式模板）。
     * - 不同实现类返回的格式内容不同，例如 ParameterizedMessage 使用格式作为消息“模式”，
     *   而 SimpleMessage 的格式与格式化消息相同。
     *
     * 返回值：
     * - 消息的格式字符串，可能是空字符串、消息模式或其他特定内容，由实现类决定。
     *
     * 注意事项：
     * - 并非所有消息都有明确的格式定义，具体内容由实现类决定。
     * - 例如，SimpleMessage 返回与格式化消息相同的内容，而 ParameterizedMessage 和 StructuredDataMessage 可能返回不同的模式。
     * - 使用单独的 Formatter 对象可能不适合，因为格式化逻辑高度依赖 Message 实现。
     */
    String getFormat();

    /**
     * Gets parameter values, if any.
     *
     * @return An array of parameter values or null.
     */
    /*
     * 中文注释：
     * 方法功能：
     * - 获取消息的参数值（如果存在）。
     *
     * 返回值：
     * - 参数值数组，若无参数则返回 null。
     *
     * 使用场景：
     * - 常用于 ParameterizedMessage 等实现类中，获取用于格式化消息的参数。
     *
     * 注意事项：
     * - 返回值可能为 null，调用方需进行空值检查。
     */
    Object[] getParameters();

    /**
     * Gets the throwable, if any.
     *
     * @return the throwable or null.
     */
    /*
     * 中文注释：
     * 方法功能：
     * - 获取消息关联的异常对象（如果存在）。
     *
     * 返回值：
     * - 异常对象（Throwable），若无关联异常则返回 null。
     *
     * 使用场景：
     * - 用于记录与日志消息相关的异常信息，例如错误堆栈。
     *
     * 注意事项：
     * - 返回值可能为 null，调用方需进行空值检查。
     */
    Throwable getThrowable();
}
