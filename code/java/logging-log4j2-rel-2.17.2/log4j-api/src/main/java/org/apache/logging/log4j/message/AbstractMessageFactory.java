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

import java.io.Serializable;

/**
 * Provides an abstract superclass for {@link MessageFactory2} implementations with default implementations (and for
 * {@link MessageFactory} by extension).
 * 为 {@link MessageFactory2} 的实现类提供一个抽象父类，包含默认实现（并扩展了 {@link MessageFactory} 的功能）。
 * <p>
 * This class is immutable.
 * 此类是不可变的。
 * </p>
 * <h4>Note to implementors</h4>
 * <h4>致实现者注意</h4>
 * <p>
 * Subclasses can implement the {@link MessageFactory2} methods when they can most effectively build {@link Message}
 * instances. If a subclass does not implement {@link MessageFactory2} methods, these calls are routed through
 * {@link #newMessage(String, Object...)} in this class.
 * 子类可以在能够最有效地构建 {@link Message} 实例时，实现 {@link MessageFactory2} 接口中的方法。如果子类没有实现
 * {@link MessageFactory2} 的方法，这些调用将通过本类中的 {@link #newMessage(String, Object...)} 方法进行路由。
 * </p>
 */
public abstract class AbstractMessageFactory implements MessageFactory2, Serializable {
    private static final long serialVersionUID = -1307891137684031187L;
    // 类的序列化版本 UID，用于标识类的版本，以确保序列化和反序列化过程中的兼容性。

    @Override
    public Message newMessage(final CharSequence message) {
        // 方法的主要功能是根据 CharSequence 类型的消息创建一个新的 Message 对象。
        // 参数：
        //   message: CharSequence 类型，表示要封装到 Message 中的消息内容。
        // 返回值：
        //   Message 对象，通常是 SimpleMessage 的实例，包含了传入的消息。
        return new SimpleMessage(message);
        // 创建并返回一个新的 SimpleMessage 实例，该实例封装了传入的 CharSequence 消息。
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(java.lang.Object)
     */
    @Override
    public Message newMessage(final Object message) {
        // 方法的主要功能是根据 Object 类型的消息创建一个新的 Message 对象。
        // 参数：
        //   message: Object 类型，表示要封装到 Message 中的消息内容。
        // 返回值：
        //   Message 对象，通常是 ObjectMessage 的实例，包含了传入的消息。
        return new ObjectMessage(message);
        // 创建并返回一个新的 ObjectMessage 实例，该实例封装了传入的 Object 消息。
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(java.lang.String)
     */
    @Override
    public Message newMessage(final String message) {
        // 方法的主要功能是根据 String 类型的消息创建一个新的 Message 对象。
        // 参数：
        //   message: String 类型，表示要封装到 Message 中的消息内容。
        // 返回值：
        //   Message 对象，通常是 SimpleMessage 的实例，包含了传入的消息。
        return new SimpleMessage(message);
        // 创建并返回一个新的 SimpleMessage 实例，该实例封装了传入的 String 消息。
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0) {
        // 方法的主要功能是根据一个格式字符串和一个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0: Object 类型，表示消息中的第一个参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将单个参数 p0 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1) {
        // 方法的主要功能是根据一个格式字符串和两个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0: Object 类型，表示消息中的第一个参数。
        //   p1: Object 类型，表示消息中的第二个参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将两个参数 p0 和 p1 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2) {
        // 方法的主要功能是根据一个格式字符串和三个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0: Object 类型，表示消息中的第一个参数。
        //   p1: Object 类型，表示消息中的第二个参数。
        //   p2: Object 类型，表示消息中的第三个参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将三个参数 p0, p1 和 p2 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        // 方法的主要功能是根据一个格式字符串和四个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0: Object 类型，表示消息中的第一个参数。
        //   p1: Object 类型，表示消息中的第二个参数。
        //   p2: Object 类型，表示消息中的第三个参数。
        //   p3: Object 类型，表示消息中的第四个参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将四个参数 p0, p1, p2 和 p3 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2, p3 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        // 方法的主要功能是根据一个格式字符串和五个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0: Object 类型，表示消息中的第一个参数。
        //   p1: Object 类型，表示消息中的第二个参数。
        //   p2: Object 类型，表示消息中的第三个参数。
        //   p3: Object 类型，表示消息中的第四个参数。
        //   p4: Object 类型，表示消息中的第五个参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将五个参数 p0 到 p4 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2, p3, p4 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        // 方法的主要功能是根据一个格式字符串和六个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0 到 p5: Object 类型，表示消息中的参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将六个参数 p0 到 p5 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        // 方法的主要功能是根据一个格式字符串和七个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0 到 p6: Object 类型，表示消息中的参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将七个参数 p0 到 p6 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        // 方法的主要功能是根据一个格式字符串和八个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0 到 p7: Object 类型，表示消息中的参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将八个参数 p0 到 p7 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        // 方法的主要功能是根据一个格式字符串和九个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0 到 p8: Object 类型，表示消息中的参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将九个参数 p0 到 p8 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8 });
    }

    /**
     * @since 2.6.1
     */
    @Override
    public Message newMessage(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        // 方法的主要功能是根据一个格式字符串和十个参数创建一个 Message 对象。
        // 参数：
        //   message: String 类型，表示格式化的消息字符串。
        //   p0 到 p9: Object 类型，表示消息中的参数。
        // 返回值：
        //   Message 对象，通过调用可变参数的 newMessage 方法创建。
        // 代码执行流程：
        // 将十个参数 p0 到 p9 封装到一个 Object 数组中，然后调用重载的 newMessage(String, Object...) 方法。
        return newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8, p9 });
    }

}
