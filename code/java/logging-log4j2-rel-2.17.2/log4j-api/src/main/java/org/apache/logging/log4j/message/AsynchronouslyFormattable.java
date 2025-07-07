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
package org.apache.logging.log4j.message;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that signals to asynchronous logging components that messages of this type can safely be passed to
 * a background thread without calling {@link Message#getFormattedMessage()} first.
 * 注解，用于向异步日志组件指示此类型的消息可以安全地传递到后台线程，而无需首先调用 {@link Message#getFormattedMessage()} 方法。
 * <p>
 * Generally, logging mutable objects asynchronously always has the risk that the object is modified between the time
 * the logger is called and the time the log message is formatted and written to disk. Strictly speaking it is the
 * responsibility of the application to ensure that mutable objects are not modified after they have been logged,
 * but this is not always possible.
 * </p><p>
 * 通常，异步记录可变对象始终存在风险，即对象在调用日志记录器和格式化日志消息并写入磁盘之间被修改。严格来说，应用程序有责任确保可变对象在记录后不会被修改，但这并非总是可能的。
 * </p><p>
 * Log4j prevents the above race condition as follows:
 * Log4j 通过以下方式防止上述竞态条件：
 * </p><ol>
 * <li>If the Message implements {@link ReusableMessage}, asynchronous logging components in the Log4j implementation
 * will copy the message content (formatted message, parameters) onto the queue rather than passing the
 * {@code Message} instance itself. This ensures that the formatted message will not change
 * when the mutable object is modified.
 * </li>
 * <li>如果 Message 实现了 {@link ReusableMessage} 接口，Log4j 实现中的异步日志组件会将消息内容（格式化的消息、参数）复制到队列中，而不是传递 {@code Message} 实例本身。这确保了当可变对象被修改时，格式化的消息不会改变。
 * </li>
 * <li>If the Message is annotated with {@link AsynchronouslyFormattable}, it can be passed to another thread as is.</li>
 * <li>如果 Message 被 {@link AsynchronouslyFormattable} 注解标记，它可以按原样传递给另一个线程。</li>
 * <li>Otherwise, asynchronous logging components in the Log4j implementation will call
 * {@link Message#getFormattedMessage()} before passing the Message object to another thread.
 * This gives the Message implementation class a chance to create a formatted message String with the current value
 * of the mutable object. The intention is that the Message implementation caches this formatted message and returns
 * it on subsequent calls.
 * (See <a href="https://issues.apache.org/jira/browse/LOG4J2-763">LOG4J2-763</a>.)
 * </li>
 * <li>否则，Log4j 实现中的异步日志组件将在将 Message 对象传递给另一个线程之前调用 {@link Message#getFormattedMessage()} 方法。这使 Message 实现类有机会使用可变对象的当前值创建格式化的消息字符串。目的是 Message 实现会缓存此格式化的消息并在后续调用中返回它。（参见 <a href="https://issues.apache.org/jira/browse/LOG4J2-763">LOG4J2-763</a>。）
 * </li>
 * </ol>
 *
 * @see Message
 * @see Message 接口
 * @see ReusableMessage
 * @see ReusableMessage 接口
 * @see <a href="https://issues.apache.org/jira/browse/LOG4J2-763">LOG4J2-763</a>
 * @since 2.8
 * @since 2.8 版本引入
 */
@Documented // This annotation is part of the public API of annotated elements.
// 此注解是注解元素的公共 API 的一部分，应包含在 Javadoc 中。
@Target(ElementType.TYPE) // Only applies to types.
// 此注解只能应用于类型（如类、接口、枚举或注解）。
@Retention(RetentionPolicy.RUNTIME) //Needs to be reflectively discoverable runtime.
// 此注解在运行时需要通过反射机制发现和访问。
public @interface AsynchronouslyFormattable {
}
