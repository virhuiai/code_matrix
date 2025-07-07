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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;

/**
 * Anchor point for logging implementations.
 * 日志实现的锚点接口。定义了与日志上下文交互的核心方法。
 */
public interface LoggerContext {

    /**
     * Empty array.
     * 空数组常量，用于避免在需要空LoggerContext数组时重复创建。
     */
    LoggerContext[] EMPTY_ARRAY = {};

    /**
     * Gets the anchor for some other context, such as a ClassLoader or ServletContext.
     * 获取其他上下文（如 ClassLoader 或 ServletContext）的锚点。
     * @return The external context.
     * 返回外部上下文对象。
     */
    Object getExternalContext();

    /**
     * Gets an ExtendedLogger using the fully qualified name of the Class as the Logger name.
     * 使用类的完全限定名作为日志器名称获取一个 ExtendedLogger 实例。
     * @param cls The Class whose name should be used as the Logger name.
     * 要用作日志器名称的 Class 对象。
     * @return The logger.
     * 返回对应的 ExtendedLogger 实例。
     * @since 2.14.0
     */
    default ExtendedLogger getLogger(Class<?> cls) {
        // 获取类的规范名称，如果不存在则使用普通名称
        final String canonicalName = cls.getCanonicalName();
        // 调用重载方法获取日志器
        return getLogger(canonicalName != null ? canonicalName : cls.getName());
    }


    /**
     * Gets an ExtendedLogger using the fully qualified name of the Class as the Logger name.
     * 使用类的完全限定名作为日志器名称获取一个 ExtendedLogger 实例。
     * @param cls The Class whose name should be used as the Logger name.
     * 要用作日志器名称的 Class 对象。
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change the
     *                       logger but will log a warning if mismatched.
     * 消息工厂，仅在创建日志器时使用。后续对同一名称的日志器调用此方法不会改变其消息工厂，但如果消息工厂不匹配会记录警告。
     * @return The logger.
     * 返回对应的 ExtendedLogger 实例。
     * @since 2.14.0
     */
    default ExtendedLogger getLogger(Class<?> cls, MessageFactory messageFactory) {
        // 获取类的规范名称，如果不存在则使用普通名称
        final String canonicalName = cls.getCanonicalName();
        // 调用重载方法获取日志器，并传入消息工厂
        return getLogger(canonicalName != null ? canonicalName : cls.getName(), messageFactory);
    }

    /**
     * Gets an ExtendedLogger.
     * 获取一个 ExtendedLogger 实例。
     * @param name The name of the Logger to return.
     * 要返回的日志器的名称。
     * @return The logger with the specified name.
     * 返回具有指定名称的日志器。
     */
    ExtendedLogger getLogger(String name);

    /**
     * Gets an ExtendedLogger.
     * 获取一个 ExtendedLogger 实例。
     * @param name The name of the Logger to return.
     * 要返回的日志器的名称。
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change
     *                       the logger but will log a warning if mismatched.
     * 消息工厂，仅在创建日志器时使用。后续对同一名称的日志器调用此方法不会改变其消息工厂，但如果消息工厂不匹配会记录警告。
     * @return The logger with the specified name.
     * 返回具有指定名称的日志器。
     */
    ExtendedLogger getLogger(String name, MessageFactory messageFactory);

    /**
     * Gets the LoggerRegistry.
     * 获取 LoggerRegistry 实例。
     *
     * @return the LoggerRegistry.
     * 返回 LoggerRegistry 实例。
     * @since 2.17.2
     */
    default LoggerRegistry<? extends Logger> getLoggerRegistry() {
        // 默认实现返回 null，具体实现类将提供实际的 LoggerRegistry
        return null;
    }

    /**
     * Gets an object by its name.
     * 根据名称获取一个对象。
     * @param key The object's key.
     * 对象的键。
     * @return The Object that is associated with the key, if any.
     * 与键关联的对象，如果不存在则返回 null。
     * @since 2.13.0
     */
    default Object getObject(String key) {
        // 默认实现返回 null，具体实现类将提供实际的对象存储和检索逻辑
        return null;
    }

    /**
     * Tests if a Logger with the specified name exists.
     * 检查是否存在具有指定名称的日志器。
     * @param name The Logger name to search for.
     * 要搜索的日志器名称。
     * @return true if the Logger exists, false otherwise.
     * 如果日志器存在则返回 true，否则返回 false。
     */
    boolean hasLogger(String name);

    /**
     * Tests if a Logger with the specified name and MessageFactory type exists.
     * 检查是否存在具有指定名称和指定消息工厂类型的日志器。
     * @param name The Logger name to search for.
     * 要搜索的日志器名称。
     * @param messageFactoryClass The message factory class to search for.
     * 要搜索的消息工厂类。
     * @return true if the Logger exists, false otherwise.
     * 如果日志器存在则返回 true，否则返回 false。
     * @since 2.5
     */
    boolean hasLogger(String name, Class<? extends MessageFactory> messageFactoryClass);

    /**
     * Tests if a Logger with the specified name and MessageFactory exists.
     * 检查是否存在具有指定名称和指定消息工厂实例的日志器。
     * @param name The Logger name to search for.
     * 要搜索的日志器名称。
     * @param messageFactory The message factory to search for.
     * 要搜索的消息工厂实例。
     * @return true if the Logger exists, false otherwise.
     * 如果日志器存在则返回 true，否则返回 false。
     * @since 2.5
     */
    boolean hasLogger(String name, MessageFactory messageFactory);

    /**
     * Associates an object into the LoggerContext by name for later use.
     * 将一个对象按名称关联到 LoggerContext 中，以便后续使用。
     * @param key The object's key.
     * 对象的键。
     * @param value The object.
     * 要关联的对象。
     * @return The previous object or null.
     * 返回与此键关联的旧对象，如果没有则返回 null。
     * @since 2.13.0
     */
    default Object putObject(String key, Object value) {
        // 默认实现返回 null，具体实现类将提供实际的存储逻辑
        return null;
    }

    /**
     * Associates an object into the LoggerContext by name for later use if an object is not already stored with that key.
     * 如果指定的键尚未关联对象，则将一个对象按名称关联到 LoggerContext 中，以便后续使用。
     * @param key The object's key.
     * 对象的键。
     * @param value The object.
     * 要关联的对象。
     * @return The previous object or null.
     * 返回与此键关联的旧对象，如果没有则返回 null。
     * @since 2.13.0
     */
    default Object putObjectIfAbsent(String key, Object value) {
        // 默认实现返回 null，具体实现类将提供实际的条件存储逻辑
        return null;
    }

    /**
     * Removes an object if it is present.
     * 如果存在，则移除一个对象。
     * @param key The object's key.
     * 对象的键。
     * @return The object if it was present, null if it was not.
     * 如果对象存在并被移除，则返回该对象，否则返回 null。
     * @since 2.13.0
     */
    default Object removeObject(String key) {
        // 默认实现返回 null，具体实现类将提供实际的移除逻辑
        return null;
    }

    /**
     * Removes an object if it is present and the provided object is stored.
     * 如果对象存在且与提供的对象实例相同，则移除该对象。
     * @param key The object's key.
     * 对象的键。
     * @param value The object.
     * 要检查并移除的对象实例。
     * @return The object if it was present, null if it was not.
     * 如果对象存在且匹配并被移除，则返回 true，否则返回 false。
     * @since 2.13.0
     */
    default boolean removeObject(String key, Object value) {
        // 默认实现返回 false，具体实现类将提供实际的条件移除逻辑
        return false;
    }
}
