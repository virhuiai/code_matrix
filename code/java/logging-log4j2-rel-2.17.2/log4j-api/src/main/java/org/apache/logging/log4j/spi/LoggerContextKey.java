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

import org.apache.logging.log4j.message.MessageFactory;

/**
 * Creates keys used in maps for use in LoggerContext implementations.
 * 用于在LoggerContext实现中创建映射所使用的键。
 *
 * @deprecated with no replacement - no longer used
 * 已废弃，无替代方案 - 不再使用
 * @since 2.5
 * 从2.5版本开始引入
 */
@Deprecated
public class LoggerContextKey {

    /**
     * Creates a key using the provided name and a default message factory class.
     * 使用提供的名称和默认消息工厂类创建一个键。
     *
     * @param name The name of the logger context.
     * Logger上下文的名称。
     * @return The generated key string.
     * 生成的键字符串。
     */
    public static String create(final String name) {
        // Calls the overloaded create method with the default message factory class.
        // 调用重载的create方法，使用默认的消息工厂类。
        return create(name, AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS);
    }

    /**
     * Creates a key using the provided name and message factory.
     * 使用提供的名称和消息工厂创建一个键。
     *
     * @param name The name of the logger context.
     * Logger上下文的名称。
     * @param messageFactory The message factory to use. If null, the default message factory class will be used.
     * 要使用的消息工厂。如果为null，将使用默认的消息工厂类。
     * @return The generated key string.
     * 生成的键字符串。
     */
    public static String create(final String name, final MessageFactory messageFactory) {
        // Determines the message factory class to use. If messageFactory is null, use the default class.
        // 确定要使用的消息工厂类。如果messageFactory为null，则使用默认类。
        final Class<? extends MessageFactory> messageFactoryClass = messageFactory != null ? messageFactory.getClass()
                : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
        // Calls the overloaded create method with the determined message factory class.
        // 调用重载的create方法，使用确定的消息工厂类。
        return create(name, messageFactoryClass);
    }

    /**
     * Creates a key using the provided name and message factory class.
     * 使用提供的名称和消息工厂类创建一个键。
     *
     * @param name The name of the logger context.
     * Logger上下文的名称。
     * @param messageFactoryClass The message factory class to use. If null, the default message factory class will be used.
     * 要使用的消息工厂类。如果为null，将使用默认的消息工厂类。
     * @return The generated key string, which is a concatenation of the name and the message factory class name.
     * 生成的键字符串，它是名称和消息工厂类名称的拼接。
     */
    public static String create(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
        // Determines the message factory class to use. If messageFactoryClass is null, use the default class.
        // 确定要使用的消息工厂类。如果messageFactoryClass为null，则使用默认类。
        final Class<? extends MessageFactory> mfClass = messageFactoryClass != null ? messageFactoryClass
                : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
        // Concatenates the logger context name and the full name of the message factory class, separated by a dot, to form the key.
        // 将Logger上下文名称和消息工厂类的完整名称拼接起来，以点号分隔，形成最终的键。
        return name + "." + mfClass.getName();
    }

}
