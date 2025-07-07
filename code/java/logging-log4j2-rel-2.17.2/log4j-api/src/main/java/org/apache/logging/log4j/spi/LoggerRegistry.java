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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.message.MessageFactory;

/**
 * Convenience class to be used by {@code LoggerContext} implementations.
 */
// 类功能：LoggerRegistry 是一个工具类，用于 LoggerContext 实现，用于管理和存储日志记录器（ExtendedLogger 实例）。
// 主要目的：提供一个灵活的注册表机制，允许根据日志名称和消息工厂（MessageFactory）存储和检索日志记录器。
// 关键特性：支持多种 Map 实现（如 ConcurrentHashMap 和 WeakHashMap），通过 MapFactory 接口实现存储结构的可配置性。
public class LoggerRegistry<T extends ExtendedLogger> {
    private static final String DEFAULT_FACTORY_KEY = AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS.getName();
    // 静态变量：默认的消息工厂键，用于在未指定 MessageFactory 时作为键值。
    // 作用：确保在没有提供消息工厂的情况下，使用默认的消息工厂类名作为键。

    private final MapFactory<T> factory;
    // 成员变量：用于创建和管理存储日志记录器的 Map 结构。
    // 用途：通过 MapFactory 接口抽象化 Map 的创建逻辑，允许不同的 Map 实现（如并发或弱引用）。

    private final Map<String, Map<String, T>> map;
    // 成员变量：外层 Map，用于存储消息工厂键（字符串）到内层 Map 的映射。
    // 内层 Map 存储日志名称到 ExtendedLogger 实例的映射。
    // 结构说明：外层 Map 的键是 MessageFactory 的类名，值是一个内层 Map，内层 Map 的键是日志名称，值是对应的 ExtendedLogger。

    /**
     * Interface to control the data structure used by the registry to store the Loggers.
     * @param <T> subtype of {@code ExtendedLogger}
     */
    // 接口功能：MapFactory 定义了用于创建和管理日志记录器存储结构的接口。
    // 用途：允许 LoggerRegistry 使用不同的 Map 实现（如 ConcurrentHashMap 或 WeakHashMap）来存储日志记录器。
    // 设计目的：通过接口抽象化 Map 的创建和操作逻辑，提供灵活性和可扩展性。
    public interface MapFactory<T extends ExtendedLogger> {
        Map<String, T> createInnerMap();
        // 方法功能：创建内层 Map，用于存储日志名称到 ExtendedLogger 的映射。
        // 返回值：一个新的 Map 实例，具体类型由实现类决定。

        Map<String, Map<String, T>> createOuterMap();
        // 方法功能：创建外层 Map，用于存储消息工厂键到内层 Map 的映射。
        // 返回值：一个新的外层 Map 实例，具体类型由实现类决定。

        void putIfAbsent(Map<String, T> innerMap, String name, T logger);
        // 方法功能：将指定的日志记录器存储到内层 Map 中，仅在日志名称不存在时存储。
        // 参数：
        //   - innerMap：内层 Map，存储日志名称到 ExtendedLogger 的映射。
        //   - name：日志名称。
        //   - logger：要存储的 ExtendedLogger 实例。
        // 注意事项：具体实现可能因 Map 类型不同而有所差异（如并发性或弱引用）。
    }

    /**
     * Generates ConcurrentHashMaps for use by the registry to store the Loggers.
     * @param <T> subtype of {@code ExtendedLogger}
     */
    // 类功能：ConcurrentMapFactory 是一个 MapFactory 的实现，使用 ConcurrentHashMap 来存储日志记录器。
    // 主要目的：提供线程安全的 Map 实现，适用于高并发场景。
    // 适用场景：需要高性能和线程安全的日志记录器存储。
    public static class ConcurrentMapFactory<T extends ExtendedLogger> implements MapFactory<T> {
        @Override
        public Map<String, T> createInnerMap() {
            return new ConcurrentHashMap<>();
            // 方法功能：创建并返回一个线程安全的 ConcurrentHashMap 实例作为内层 Map。
            // 返回值：ConcurrentHashMap 实例，用于存储日志名称到 ExtendedLogger 的映射。
        }

        @Override
        public Map<String, Map<String, T>> createOuterMap() {
            return new ConcurrentHashMap<>();
            // 方法功能：创建并返回一个线程安全的 ConcurrentHashMap 实例作为外层 Map。
            // 返回值：ConcurrentHashMap 实例，用于存储消息工厂键到内层 Map 的映射。
        }

        @Override
        public void putIfAbsent(final Map<String, T> innerMap, final String name, final T logger) {
            ((ConcurrentMap<String, T>) innerMap).putIfAbsent(name, logger);
            // 方法功能：将日志记录器存储到内层 Map 中，仅在指定名称不存在时存储。
            // 参数：
            //   - innerMap：内层 Map，类型为 ConcurrentMap。
            //   - name：日志名称。
            //   - logger：要存储的 ExtendedLogger 实例。
            // 执行流程：使用 ConcurrentMap 的 putIfAbsent 方法，确保线程安全地添加日志记录器。
        }
    }

    /**
     * Generates WeakHashMaps for use by the registry to store the Loggers.
     * @param <T> subtype of {@code ExtendedLogger}
     */
    // 类功能：WeakMapFactory 是一个 MapFactory 的实现，使用 WeakHashMap 来存储日志记录器。
    // 主要目的：提供弱引用的 Map 实现，允许未使用的日志记录器被垃圾回收，适合内存敏感场景。
    // 适用场景：需要减少内存占用的日志记录器存储。
    public static class WeakMapFactory<T extends ExtendedLogger> implements MapFactory<T> {
        @Override
        public Map<String, T> createInnerMap() {
            return new WeakHashMap<>();
            // 方法功能：创建并返回一个 WeakHashMap 实例作为内层 Map。
            // 返回值：WeakHashMap 实例，用于存储日志名称到 ExtendedLogger 的映射。
            // 注意事项：WeakHashMap 使用弱引用，键（日志名称）可能被垃圾回收。
        }

        @Override
        public Map<String, Map<String, T>> createOuterMap() {
            return new WeakHashMap<>();
            // 方法功能：创建并返回一个 WeakHashMap 实例作为外层 Map。
            // 返回值：WeakHashMap 实例，用于存储消息工厂键到内层 Map 的映射。
            // 注意事项：外层 Map 的键（消息工厂类名）可能被垃圾回收。
        }

        @Override
        public void putIfAbsent(final Map<String, T> innerMap, final String name, final T logger) {
            innerMap.put(name, logger);
            // 方法功能：将日志记录器存储到内层 Map 中，直接覆盖已有键值。
            // 参数：
            //   - innerMap：内层 Map，类型为 WeakHashMap。
            //   - name：日志名称。
            //   - logger：要存储的 ExtendedLogger 实例。
            // 注意事项：WeakHashMap 不支持原生的 putIfAbsent 操作，因此直接使用 put 方法，可能覆盖现有日志记录器。
        }
    }

    public LoggerRegistry() {
        this(new ConcurrentMapFactory<T>());
        // 构造方法功能：创建 LoggerRegistry 实例，使用默认的 ConcurrentMapFactory。
        // 执行流程：调用带 MapFactory 参数的构造方法，传入 ConcurrentMapFactory 实例。
        // 默认配置：使用线程安全的 ConcurrentHashMap 作为存储结构。
    }

    public LoggerRegistry(final MapFactory<T> factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
        this.map = factory.createOuterMap();
        // 构造方法功能：创建 LoggerRegistry 实例，使用指定的 MapFactory。
        // 参数：
        //   - factory：MapFactory 实例，用于创建和管理存储结构。
        // 执行流程：
        //   1. 检查 factory 参数是否为 null，若为 null 抛出异常。
        //   2. 初始化 factory 成员变量。
        //   3. 使用 factory 创建外层 Map 并初始化 map 成员变量。
        // 注意事项：factory 参数不能为空，否则会抛出 NullPointerException。
    }

    private static String factoryClassKey(final Class<? extends MessageFactory> messageFactoryClass) {
        return messageFactoryClass == null ? DEFAULT_FACTORY_KEY : messageFactoryClass.getName();
        // 方法功能：根据 MessageFactory 类生成对应的键值。
        // 参数：
        //   - messageFactoryClass：MessageFactory 的类对象，可能为 null。
        // 返回值：消息工厂的类名字符串，若为 null 则返回默认键值。
        // 用途：用于在外层 Map 中查找或存储与特定 MessageFactory 类相关的日志记录器。
    }

    private static String factoryKey(final MessageFactory messageFactory) {
        return messageFactory == null ? DEFAULT_FACTORY_KEY : messageFactory.getClass().getName();
        // 方法功能：根据 MessageFactory 实例生成对应的键值。
        // 参数：
        //   - messageFactory：MessageFactory 实例，可能为 null。
        // 返回值：消息工厂的类名字符串，若为 null 则返回默认键值。
        // 用途：用于在外层 Map 中查找或存储与特定 MessageFactory 实例相关的日志记录器。
    }

    /**
     * Returns an ExtendedLogger.
     * @param name The name of the Logger to return.
     * @return The logger with the specified name.
     */
    // 方法功能：获取指定名称的 ExtendedLogger 实例，使用默认消息工厂。
    // 参数：
    //   - name：日志记录器的名称。
    // 返回值：指定名称的 ExtendedLogger 实例，若不存在则返回 null。
    // 执行流程：
    //   1. 使用默认消息工厂键（DEFAULT_FACTORY_KEY）获取内层 Map。
    //   2. 从内层 Map 中获取指定名称的日志记录器。
    public T getLogger(final String name) {
        return getOrCreateInnerMap(DEFAULT_FACTORY_KEY).get(name);
    }

    /**
     * Returns an ExtendedLogger.
     * @param name The name of the Logger to return.
     * @param messageFactory The message factory is used only when creating a logger, subsequent use does not change
     *                       the logger but will log a warning if mismatched.
     * @return The logger with the specified name.
     */
    // 方法功能：获取指定名称和消息工厂的 ExtendedLogger 实例。
    // 参数：
    //   - name：日志记录器的名称。
    //   - messageFactory：消息工厂实例，用于创建日志记录器。
    // 返回值：指定名称的 ExtendedLogger 实例，若不存在则返回 null。
    // 执行流程：
    //   1. 根据 messageFactory 生成消息工厂键。
    //   2. 使用该键获取内层 Map。
    //   3. 从内层 Map 中获取指定名称的日志记录器。
    // 注意事项：消息工厂仅在创建日志记录器时使用，后续调用不更改已有记录器，但若消息工厂不匹配可能会记录警告。
    public T getLogger(final String name, final MessageFactory messageFactory) {
        return getOrCreateInnerMap(factoryKey(messageFactory)).get(name);
    }

    public Collection<T> getLoggers() {
        return getLoggers(new ArrayList<T>());
        // 方法功能：获取所有日志记录器的集合。
        // 返回值：包含所有 ExtendedLogger 实例的集合。
        // 执行流程：调用重载方法，传入一个新的 ArrayList 作为目标集合。
    }

    public Collection<T> getLoggers(final Collection<T> destination) {
        for (final Map<String, T> inner : map.values()) {
            destination.addAll(inner.values());
        }
        return destination;
        // 方法功能：将所有日志记录器添加到指定的集合中。
        // 参数：
        //   - destination：目标集合，用于存储所有 ExtendedLogger 实例。
        // 返回值：包含所有日志记录器的目标集合。
        // 执行流程：
        //   1. 遍历外层 Map 的所有内层 Map。
        //   2. 将每个内层 Map 的日志记录器集合添加到 destination 中。
        // 注意事项：destination 集合会被修改并返回。
    }

    private Map<String, T> getOrCreateInnerMap(final String factoryName) {
        Map<String, T> inner = map.get(factoryName);
        if (inner == null) {
            inner = factory.createInnerMap();
            map.put(factoryName, inner);
        }
        return inner;
        // 方法功能：获取或创建与指定消息工厂键关联的内层 Map。
        // 参数：
        //   - factoryName：消息工厂键，通常为消息工厂的类名。
        // 返回值：与指定消息工厂键关联的内层 Map。
        // 执行流程：
        //   1. 从外层 Map 中获取指定 factoryName 的内层 Map。
        //   2. 如果内层 Map 不存在，使用 factory 创建一个新的内层 Map 并存入外层 Map。
        //   3. 返回内层 Map。
        // 注意事项：确保线程安全地创建内层 Map（依赖于 MapFactory 实现）。
    }

    /**
     * Detects if a Logger with the specified name exists.
     * @param name The Logger name to search for.
     * @return true if the Logger exists, false otherwise.
     */
    // 方法功能：检查是否存在指定名称的日志记录器（使用默认消息工厂）。
    // 参数：
    //   - name：日志记录器的名称。
    // 返回值：如果日志记录器存在返回 true，否则返回 false。
    // 执行流程：
    //   1. 使用默认消息工厂键获取内层 Map。
    //   2. 检查内层 Map 中是否包含指定名称的键。
    public boolean hasLogger(final String name) {
        return getOrCreateInnerMap(DEFAULT_FACTORY_KEY).containsKey(name);
    }

    /**
     * Detects if a Logger with the specified name and MessageFactory exists.
     * @param name The Logger name to search for.
     * @param messageFactory The message factory to search for.
     * @return true if the Logger exists, false otherwise.
     * @since 2.5
     */
    // 方法功能：检查是否存在指定名称和消息工厂的日志记录器。
    // 参数：
    //   - name：日志记录器的名称。
    //   - messageFactory：消息工厂实例。
    // 返回值：如果日志记录器存在返回 true，否则返回 false。
    // 执行流程：
    //   1. 根据 messageFactory 生成消息工厂键。
    //   2. 使用该键获取内层 Map。
    //   3. 检查内层 Map 中是否包含指定名称的键。
    // 注意事项：自 2.5 版本引入，提供了更精确的日志记录器查找功能。
    public boolean hasLogger(final String name, final MessageFactory messageFactory) {
        return getOrCreateInnerMap(factoryKey(messageFactory)).containsKey(name);
    }

    /**
     * Detects if a Logger with the specified name and MessageFactory type exists.
     * @param name The Logger name to search for.
     * @param messageFactoryClass The message factory class to search for.
     * @return true if the Logger exists, false otherwise.
     * @since 2.5
     */
    // 方法功能：检查是否存在指定名称和消息工厂类型的日志记录器。
    // 参数：
    //   - name：日志记录器的名称。
    //   - messageFactoryClass：消息工厂的类对象。
    // 返回值：如果日志记录器存在返回 true，否则返回 false。
    // 执行流程：
    //   1. 根据 messageFactoryClass 生成消息工厂键。
    //   2. 使用该键获取内层 Map。
    //   3. 检查内层 Map 中是否包含指定名称的键。
    // 注意事项：自 2.5 版本引入，支持通过消息工厂类进行查找。
    public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
        return getOrCreateInnerMap(factoryClassKey(messageFactoryClass)).containsKey(name);
    }

    public void putIfAbsent(final String name, final MessageFactory messageFactory, final T logger) {
        factory.putIfAbsent(getOrCreateInnerMap(factoryKey(messageFactory)), name, logger);
        // 方法功能：将指定名称和消息工厂的日志记录器存储到注册表中，仅在日志记录器不存在时存储。
        // 参数：
        //   - name：日志记录器的名称。
        //   - messageFactory：消息工厂实例。
        //   - logger：要存储的 ExtendedLogger 实例。
        // 执行流程：
        //   1. 根据 messageFactory 生成消息工厂键。
        //   2. 获取或创建与该键关联的内层 Map。
        //   3. 使用 factory 的 putIfAbsent 方法将日志记录器存入内层 Map。
        // 注意事项：具体存储行为依赖于 MapFactory 实现（如线程安全或弱引用）。
    }
}
