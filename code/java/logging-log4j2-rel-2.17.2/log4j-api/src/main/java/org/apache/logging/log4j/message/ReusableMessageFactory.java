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
 * 本文件遵循 Apache 软件基金会（ASF）的 Apache 2.0 许可证发布。
 * 代码文件的使用和分发需遵守许可证条款，详情可查看 http://www.apache.org/licenses/LICENSE-2.0。
 * 本文件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;

import java.io.Serializable;

import org.apache.logging.log4j.util.PerformanceSensitive;

/**
 * Implementation of the {@link MessageFactory} interface that avoids allocating temporary objects where possible.
 * Message instances are cached in a ThreadLocal and reused when a new message is requested within the same thread.
 * @see ParameterizedMessageFactory
 * @see ReusableSimpleMessage
 * @see ReusableObjectMessage
 * @see ReusableParameterizedMessage
 * @since 2.6
 */
/*
 * 中文注释：
 * 类名：ReusableMessageFactory
 * 主要功能：实现 MessageFactory 接口，提供可重用的消息工厂，尽可能避免分配临时对象以提高性能。
 * 目的：通过 ThreadLocal 缓存消息实例，在同一线程内重用消息对象，减少内存分配开销。
 * 关联类：
 *   - ParameterizedMessageFactory：参数化消息工厂。
 *   - ReusableSimpleMessage：简单消息的可重用实现。
 *   - ReusableObjectMessage：对象消息的可重用实现。
 *   - ReusableParameterizedMessage：参数化消息的可重用实现。
 * 版本：自 Log4j 2.6 起引入。
 */
@PerformanceSensitive("allocation")
/*
 * 中文注释：
 * 注解说明：@PerformanceSensitive("allocation") 表示该类对内存分配敏感，需优化以减少对象创建。
 */
public final class ReusableMessageFactory implements MessageFactory2, Serializable {

    /**
     * Instance of ReusableMessageFactory.
     */
    /*
     * 中文注释：
     * 变量名：INSTANCE
     * 用途：ReusableMessageFactory 的单例实例，用于全局访问该消息工厂。
     * 注意事项：通过静态常量提供单例模式，确保全局唯一实例。
     */
    public static final ReusableMessageFactory INSTANCE = new ReusableMessageFactory();

    private static final long serialVersionUID = -8970940216592525651L;
    /*
     * 中文注释：
     * 变量名：serialVersionUID
     * 用途：用于序列化，标识类的版本，确保序列化和反序列化时的兼容性。
     * 值：固定为 -8970940216592525651L。
     */
    private static ThreadLocal<ReusableParameterizedMessage> threadLocalParameterized = new ThreadLocal<>();
    /*
     * 中文注释：
     * 变量名：threadLocalParameterized
     * 用途：存储线程局部变量，缓存 ReusableParameterizedMessage 实例，用于参数化消息的重用。
     * 类型：ThreadLocal，线程安全的单例存储。
     */
    private static ThreadLocal<ReusableSimpleMessage> threadLocalSimpleMessage = new ThreadLocal<>();
    /*
     * 中文注释：
     * 变量名：threadLocalSimpleMessage
     * 用途：存储线程局部变量，缓存 ReusableSimpleMessage 实例，用于简单消息的重用。
     */
    private static ThreadLocal<ReusableObjectMessage> threadLocalObjectMessage = new ThreadLocal<>();
    /*
     * 中文注释：
     * 变量名：threadLocalObjectMessage
     * 用途：存储线程局部变量，缓存 ReusableObjectMessage 实例，用于对象消息的重用。
     */

    /**
     * Constructs a message factory.
     */
    /*
     * 中文注释：
     * 方法名：ReusableMessageFactory
     * 功能：构造 ReusableMessageFactory 实例。
     * 执行流程：默认构造函数，无额外初始化逻辑。
     * 注意事项：该类为 final，无法被继承，构造函数仅用于创建单例实例。
     */
    public ReusableMessageFactory() {
    }

    private static ReusableParameterizedMessage getParameterized() {
        /*
         * 中文注释：
         * 方法名：getParameterized
         * 功能：获取线程局部的 ReusableParameterizedMessage 实例。
         * 返回值：ReusableParameterizedMessage 实例。
         * 执行流程：
         *   1. 从 threadLocalParameterized 获取当前线程的 ReusableParameterizedMessage 实例。
         *   2. 如果实例不存在，创建新的 ReusableParameterizedMessage 并存储到 ThreadLocal。
         *   3. 检查实例是否被占用（reserved），若占用则创建新实例并调用 reserve() 方法。
         *   4. 返回可用的 ReusableParameterizedMessage 实例。
         * 注意事项：确保线程安全，通过 ThreadLocal 隔离不同线程的实例。
         */
        ReusableParameterizedMessage result = threadLocalParameterized.get();
        if (result == null) {
            result = new ReusableParameterizedMessage();
            threadLocalParameterized.set(result);
        }
        return result.reserved ? new ReusableParameterizedMessage().reserve() : result.reserve();
    }

    private static ReusableSimpleMessage getSimple() {
        /*
         * 中文注释：
         * 方法名：getSimple
         * 功能：获取线程局部的 ReusableSimpleMessage 实例。
         * 返回值：ReusableSimpleMessage 实例。
         * 执行流程：
         *   1. 从 threadLocalSimpleMessage 获取当前线程的 ReusableSimpleMessage 实例。
         *   2. 如果实例不存在，创建新的 ReusableSimpleMessage 并存储到 ThreadLocal。
         *   3. 返回可用的 ReusableSimpleMessage 实例。
         * 注意事项：与 getParameterized 类似，确保线程安全。
         */
        ReusableSimpleMessage result = threadLocalSimpleMessage.get();
        if (result == null) {
            result = new ReusableSimpleMessage();
            threadLocalSimpleMessage.set(result);
        }
        return result;
    }

    private static ReusableObjectMessage getObject() {
        /*
         * 中文注释：
         * 方法名：getObject
         * 功能：获取线程局部的 ReusableObjectMessage 实例。
         * 返回值：ReusableObjectMessage 实例。
         * 执行流程：
         *   1. 从 threadLocalObjectMessage 获取当前线程的 ReusableObjectMessage 实例。
         *   2. 如果实例不存在，创建新的 ReusableObjectMessage 并存储到 ThreadLocal。
         *   3. 返回可用的 ReusableObjectMessage 实例。
         * 注意事项：与 getParameterized 类似，确保线程安全。
         */
        ReusableObjectMessage result = threadLocalObjectMessage.get();
        if (result == null) {
            result = new ReusableObjectMessage();
            threadLocalObjectMessage.set(result);
        }
        return result;
    }

    /**
     * Invokes {@link Clearable#clear()} when possible.
     * This flag is used internally to verify that a reusable message is no longer in use and
     * can be reused.
     * @param message the message to make available again
     * @since 2.7
     */
    /*
     * 中文注释：
     * 方法名：release
     * 功能：释放可重用消息对象，调用 Clearable 接口的 clear() 方法以重置消息状态。
     * 参数：
     *   - message：需要释放的 Message 对象。
     * 执行流程：
     *   1. 检查 message 是否实现 Clearable 接口。
     *   2. 如果实现，调用 clear() 方法重置消息状态。
     * 注意事项：
     *   - 用于确保消息对象不再使用，可以被重用。
     *   - 自 Log4j 2.7 起引入。
     *   - LOG4J2-1583 是相关问题编号，可能涉及性能优化或 bug 修复。
     */
    public static void release(final Message message) { // LOG4J2-1583
        if (message instanceof Clearable) {
            ((Clearable) message).clear();
        }
    }

    @Override
    public Message newMessage(final CharSequence charSequence) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于 CharSequence 的 ReusableSimpleMessage 实例。
         * 参数：
         *   - charSequence：消息内容的字符序列。
         * 返回值：配置好的 ReusableSimpleMessage 实例。
         * 执行流程：
         *   1. 调用 getSimple() 获取线程局部的 ReusableSimpleMessage 实例。
         *   2. 调用 set 方法设置消息内容。
         *   3. 返回配置好的消息对象。
         * 注意事项：通过重用线程局部实例减少对象分配。
         */
        final ReusableSimpleMessage result = getSimple();
        result.set(charSequence);
        return result;
    }

    /**
     * Creates {@link ReusableParameterizedMessage} instances.
     *
     * @param message The message pattern.
     * @param params The message parameters.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(String, Object...)
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：创建基于字符串模式和参数的 ReusableParameterizedMessage 实例。
     * 参数：
     *   - message：消息模式字符串，通常包含占位符（如 "{}"）。
     *   - params：可变参数列表，用于填充消息模式中的占位符。
     * 返回值：配置好的 ReusableParameterizedMessage 实例。
     * 执行流程：
     *   1. 调用 getParameterized() 获取线程局部的 ReusableParameterizedMessage 实例。
     *   2. 调用 set 方法设置消息模式和参数。
     *   3. 返回配置好的消息对象。
     * 注意事项：
     *   - 支持动态参数数量，适合格式化复杂日志消息。
     *   - 重用线程局部实例以优化性能。
     */
    @Override
    public Message newMessage(final String message, final Object... params) {
        return getParameterized().set(message, params);
    }

    @Override
    public Message newMessage(final String message, final Object p0) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和单个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0：单个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         * 执行流程：与 newMessage(String, Object...) 类似，调用 getParameterized().set() 设置消息和参数。
         * 注意事项：专门优化单一参数场景，避免可变参数数组的创建。
         */
        return getParameterized().set(message, p0);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和两个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1：两个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         * 执行流程：与 newMessage(String, Object...) 类似，调用 getParameterized().set() 设置消息和参数。
         * 注意事项：优化固定数量参数场景，提高调用效率。
         */
        return getParameterized().set(message, p0, p1);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和三个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2：三个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         * 执行流程：与 newMessage(String, Object...) 类似。
         */
        return getParameterized().set(message, p0, p1, p2);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和四个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2, p3：四个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         */
        return getParameterized().set(message, p0, p1, p2, p3);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和五个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2, p3, p4：五个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         */
        return getParameterized().set(message, p0, p1, p2, p3, p4);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和六个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2, p3, p4, p5：六个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         */
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和七个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2, p3, p4, p5, p6：七个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         */
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和八个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2, p3, p4, p5, p6, p7：八个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         */
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和九个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2, p3, p4, p5, p6, p7, p8：九个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         */
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        /*
         * 中文注释：
         * 方法名：newMessage
         * 功能：创建基于字符串模式和十个参数的 ReusableParameterizedMessage 实例。
         * 参数：
         *   - message：消息模式字符串。
         *   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9：十个参数，用于填充消息模式。
         * 返回值：配置好的 ReusableParameterizedMessage 实例。
         */
        return getParameterized().set(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    /**
     * Creates {@link ReusableSimpleMessage} instances.
     *
     * @param message The message String.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(String)
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：创建基于字符串的 ReusableSimpleMessage 实例。
     * 参数：
     *   - message：消息内容的字符串。
     * 返回值：配置好的 ReusableSimpleMessage 实例。
     * 执行流程：
     *   1. 调用 getSimple() 获取线程局部的 ReusableSimpleMessage 实例。
     *   2. 调用 set 方法设置消息内容。
     *   3. 返回配置好的消息对象。
     * 注意事项：适用于简单字符串消息场景，优化性能。
     */
    @Override
    public Message newMessage(final String message) {
        final ReusableSimpleMessage result = getSimple();
        result.set(message);
        return result;
    }


    /**
     * Creates {@link ReusableObjectMessage} instances.
     *
     * @param message The message Object.
     * @return The Message.
     *
     * @see MessageFactory#newMessage(Object)
     */
    /*
     * 中文注释：
     * 方法名：newMessage
     * 功能：创建基于对象的 ReusableObjectMessage 实例。
     * 参数：
     *   - message：消息内容的对象。
     * 返回值：配置好的 ReusableObjectMessage 实例。
     * 执行流程：
     *   1. 调用 getObject() 获取线程局部的 ReusableObjectMessage 实例。
     *   2. 调用 set 方法设置消息对象。
     *   3. 返回配置好的消息对象。
     * 注意事项：适用于传递任意对象作为消息内容的场景。
     */
    @Override
    public Message newMessage(final Object message) {
        final ReusableObjectMessage result = getObject();
        result.set(message);
        return result;
    }
}
