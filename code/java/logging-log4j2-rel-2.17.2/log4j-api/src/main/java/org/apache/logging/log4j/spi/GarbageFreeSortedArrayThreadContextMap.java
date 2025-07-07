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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.SortedArrayStringMap;

/**
 * {@code SortedArrayStringMap}-based implementation of the {@code ThreadContextMap} interface that attempts not to
 * create temporary objects. Adding and removing key-value pairs will not create temporary objects.
 * <p>
 * This implementation does <em>not</em> make a copy of its contents on every operation, so this data structure cannot
 * be passed to log events. Instead, client code needs to copy the contents when interacting with another thread.
 * </p>
 * @since 2.7
 */
// 类的主要功能和目的：
// 这是一个基于 SortedArrayStringMap 的 ThreadContextMap 接口实现，旨在避免创建临时对象。
// 添加和移除键值对的操作不会产生临时对象，从而减少垃圾回收的压力。
// 注意事项：
// 此实现不会在每次操作时复制其内容，因此此数据结构不能直接传递给日志事件。
// 客户端代码在与另一个线程交互时，需要自行复制其内容，以确保线程安全。
class GarbageFreeSortedArrayThreadContextMap implements ReadOnlyThreadContextMap, ObjectThreadContextMap  {

    /**
     * Property name ({@value} ) for selecting {@code InheritableThreadLocal} (value "true") or plain
     * {@code ThreadLocal} (value is not "true") in the implementation.
     */
    // 关键配置参数的含义：
    // 用于配置是否使用 InheritableThreadLocal 的系统属性名称。
    // 如果属性值为 "true"，则使用 InheritableThreadLocal；否则，使用普通的 ThreadLocal。
    public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";

    /**
     * The default initial capacity.
     */
    // 关键变量的用途：
    // 默认的初始容量，用于创建内部的 StringMap 实例。
    protected static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * System property name that can be used to control the data structure's initial capacity.
     */
    // 关键配置参数的含义：
    // 用于控制内部数据结构初始容量的系统属性名称。
    protected static final String PROPERTY_NAME_INITIAL_CAPACITY = "log4j2.ThreadContext.initial.capacity";

    protected final ThreadLocal<StringMap> localMap;
    // 关键变量的用途：
    // ThreadLocal 实例，用于存储每个线程独立的 StringMap 对象，确保线程上下文数据的隔离。

    private static volatile int initialCapacity;
    // 关键变量的用途：
    // 静态变量，存储从系统属性中读取的初始容量。volatile 关键字确保多线程环境下的可见性。
    private static volatile boolean inheritableMap;
    // 关键变量的用途：
    // 静态变量，存储从系统属性中读取的布尔值，指示是否使用 InheritableThreadLocal。volatile 关键字确保多线程环境下的可见性。

    /**
     * Initializes static variables based on system properties. Normally called when this class is initialized by the VM
     * and when Log4j is reconfigured.
     */
    // 方法的主要功能和目的：
    // 根据系统属性初始化静态变量 initialCapacity 和 inheritableMap。
    // 代码执行流程：
    // 1. 获取 PropertiesUtil 实例，用于读取系统属性。
    // 2. 从属性中读取 PROPERTY_NAME_INITIAL_CAPACITY 对应的整数值，如果未设置则使用 DEFAULT_INITIAL_CAPACITY。
    // 3. 从属性中读取 INHERITABLE_MAP 对应的布尔值。
    // 调用时机：
    // 通常在 JVM 初始化此类的时侯和 Log4j 重新配置时调用。
    static void init() {
        final PropertiesUtil properties = PropertiesUtil.getProperties();
        initialCapacity = properties.getIntegerProperty(PROPERTY_NAME_INITIAL_CAPACITY, DEFAULT_INITIAL_CAPACITY);
        inheritableMap = properties.getBooleanProperty(INHERITABLE_MAP);
    }
    
    static {
        // 静态代码块：
        // 在类加载时调用 init() 方法，确保静态变量在类首次使用前被初始化。
        init();
    }

    public GarbageFreeSortedArrayThreadContextMap() {
        // 构造函数：
        // 初始化 localMap 字段，通过调用 createThreadLocalMap() 方法来创建 ThreadLocal 实例。
        this.localMap = createThreadLocalMap();
    }

    // LOG4J2-479: by default, use a plain ThreadLocal, only use InheritableThreadLocal if configured.
    // (This method is package protected for JUnit tests.)
    // 方法的主要功能和目的：
    // 根据 inheritableMap 配置决定创建并返回一个普通的 ThreadLocal 还是 InheritableThreadLocal 实例。
    // 特殊处理逻辑和注意事项：
    // 默认情况下使用普通的 ThreadLocal，只有当 inheritableMap 为 true 时才使用 InheritableThreadLocal。
    // InheritableThreadLocal 允许子线程继承父线程的 ThreadLocal 值。
    // 返回值：
    // 一个 ThreadLocal<StringMap> 实例，用于存储线程上下文数据。
    private ThreadLocal<StringMap> createThreadLocalMap() {
        if (inheritableMap) {
            // 如果配置为可继承，则创建 InheritableThreadLocal
            return new InheritableThreadLocal<StringMap>() {
                @Override
                protected StringMap childValue(final StringMap parentValue) {
                    // 当子线程访问 InheritableThreadLocal 时，此方法会被调用，用于初始化子线程的值。
                    // 如果父线程的值不为空，则创建一个新的 StringMap 实例并复制父线程的值；否则返回 null。
                    return parentValue != null ? createStringMap(parentValue) : null;
                }
            };
        }
        // if not inheritable, return plain ThreadLocal with null as initial value
        // 如果不可继承，则返回普通的 ThreadLocal，其初始值为 null。
        return new ThreadLocal<>();
    }

    /**
     * Returns an implementation of the {@code StringMap} used to back this thread context map.
     * <p>
     * Subclasses may override.
     * </p>
     * @return an implementation of the {@code StringMap} used to back this thread context map
     */
    // 方法的主要功能和目的：
    // 创建并返回一个用于支持线程上下文映射的 StringMap 实现（SortedArrayStringMap）。
    // 返回值：
    // 一个新的 SortedArrayStringMap 实例，其初始容量由 initialCapacity 决定。
    // 子类可以重写此方法以提供不同的 StringMap 实现。
    protected StringMap createStringMap() {
        return new SortedArrayStringMap(initialCapacity);
    }

    /**
     * Returns an implementation of the {@code StringMap} used to back this thread context map, pre-populated
     * with the contents of the specified context data.
     * <p>
     * Subclasses may override.
     * </p>
     * @param original the key-value pairs to initialize the returned context data with
     * @return an implementation of the {@code StringMap} used to back this thread context map
     */
    // 方法的主要功能和目的：
    // 创建并返回一个用于支持线程上下文映射的 StringMap 实现（SortedArrayStringMap），并使用给定 ReadOnlyStringMap 的内容进行预填充。
    // 参数：
    // original: 用于初始化返回的上下文数据的键值对。
    // 返回值：
    // 一个新的 SortedArrayStringMap 实例，包含 original 中的所有键值对。
    // 子类可以重写此方法以提供不同的 StringMap 实现。
    protected StringMap createStringMap(final ReadOnlyStringMap original) {
        return new SortedArrayStringMap(original);
    }

    // 方法的主要功能和目的：
    // 获取当前线程的 StringMap 实例。如果当前线程没有关联的 StringMap，则会创建一个新的并设置到 ThreadLocal 中。
    // 代码执行流程：
    // 1. 尝试从 localMap 中获取当前线程的 StringMap。
    // 2. 如果获取到的 map 为 null，则调用 createStringMap() 创建一个新的 StringMap。
    // 3. 将新创建的 map 设置回 localMap 中，以便当前线程后续使用。
    // 4. 返回获取或创建的 StringMap。
    private StringMap getThreadLocalMap() {
        StringMap map = localMap.get();
        if (map == null) {
            map = createStringMap();
            localMap.set(map);
        }
        return map;
    }

    @Override
    public void put(final String key, final String value) {
        // 方法的主要功能和目的：
        // 将一个字符串键值对放入当前线程的上下文映射中。
        // 参数：
        // key: 要添加的键。
        // value: 要添加的值。
        // 实现方式：
        // 通过 getThreadLocalMap() 获取当前线程的 StringMap，然后调用其 putValue 方法。
        getThreadLocalMap().putValue(key, value);
    }

    @Override
    public void putValue(final String key, final Object value) {
        // 方法的主要功能和目的：
        // 将一个键值对（值可以是任意对象）放入当前线程的上下文映射中。
        // 参数：
        // key: 要添加的键。
        // value: 要添加的值（Object 类型）。
        // 实现方式：
        // 通过 getThreadLocalMap() 获取当前线程的 StringMap，然后调用其 putValue 方法。
        getThreadLocalMap().putValue(key, value);
    }

    @Override
    public void putAll(final Map<String, String> values) {
        // 方法的主要功能和目的：
        // 将一个 Map 中所有的字符串键值对添加到当前线程的上下文映射中。
        // 参数：
        // values: 包含要添加的键值对的 Map。
        // 特殊处理逻辑和注意事项：
        // 如果传入的 values 为 null 或为空，则不执行任何操作。
        // 实现方式：
        // 遍历传入的 Map，将每个键值对通过 putValue 方法添加到当前线程的 StringMap 中。
        if (values == null || values.isEmpty()) {
            return;
        }
        final StringMap map = getThreadLocalMap();
        for (final Map.Entry<String, String> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public <V> void putAllValues(final Map<String, V> values) {
        // 方法的主要功能和目的：
        // 将一个 Map 中所有的键值对（值可以是任意类型）添加到当前线程的上下文映射中。
        // 参数：
        // values: 包含要添加的键值对的 Map。
        // 特殊处理逻辑和注意事项：
        // 如果传入的 values 为 null 或为空，则不执行任何操作。
        // 实现方式：
        // 遍历传入的 Map，将每个键值对通过 putValue 方法添加到当前线程的 StringMap 中。
        if (values == null || values.isEmpty()) {
            return;
        }
        final StringMap map = getThreadLocalMap();
        for (final Map.Entry<String, V> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String get(final String key) {
        // 方法的主要功能和目的：
        // 从当前线程的上下文映射中获取指定键对应的字符串值。
        // 参数：
        // key: 要获取值的键。
        // 返回值：
        // 键对应的字符串值，如果键不存在或值为非字符串类型则返回 null。
        // 实现方式：
        // 调用 getValue(key) 获取 Object 类型的值，然后强制转换为 String。
        return (String) getValue(key);
    }

    @Override
    public <V> V getValue(final String key) {
        // 方法的主要功能和目的：
        // 从当前线程的上下文映射中获取指定键对应的泛型值。
        // 参数：
        // key: 要获取值的键。
        // 返回值：
        // 键对应的泛型值，如果键不存在或当前线程没有关联的 StringMap 则返回 null。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 为 null，则直接返回 null。
        // 否则，调用 map 的 getValue 方法获取值。
        final StringMap map = localMap.get();
        return map == null ? null : map.<V>getValue(key);
    }

    @Override
    public void remove(final String key) {
        // 方法的主要功能和目的：
        // 从当前线程的上下文映射中移除指定键及其对应的值。
        // 参数：
        // key: 要移除的键。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 不为 null，则调用其 remove 方法。
        final StringMap map = localMap.get();
        if (map != null) {
            map.remove(key);
        }
    }

    @Override
    public void removeAll(final Iterable<String> keys) {
        // 方法的主要功能和目的：
        // 从当前线程的上下文映射中移除指定集合中所有键及其对应的值。
        // 参数：
        // keys: 包含要移除的键的 Iterable 集合。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 不为 null，则遍历 keys 集合，对每个键调用 map 的 remove 方法。
        final StringMap map = localMap.get();
        if (map != null) {
            for (final String key : keys) {
                map.remove(key);
            }
        }
    }

    @Override
    public void clear() {
        // 方法的主要功能和目的：
        // 清除当前线程上下文映射中的所有键值对。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 不为 null，则调用其 clear 方法。
        final StringMap map = localMap.get();
        if (map != null) {
            map.clear();
        }
    }

    @Override
    public boolean containsKey(final String key) {
        // 方法的主要功能和目的：
        // 检查当前线程的上下文映射是否包含指定的键。
        // 参数：
        // key: 要检查的键。
        // 返回值：
        // 如果包含指定的键则返回 true，否则返回 false。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 为 null，则返回 false。
        // 否则，调用 map 的 containsKey 方法。
        final StringMap map = localMap.get();
        return map != null && map.containsKey(key);
    }

    @Override
    public Map<String, String> getCopy() {
        // 方法的主要功能和目的：
        // 获取当前线程上下文映射内容的副本，返回一个 HashMap。
        // 返回值：
        // 包含上下文映射所有键值对的 HashMap 副本。如果当前线程没有关联的 StringMap，则返回一个空的 HashMap。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 为 null，则返回一个新的空 HashMap。
        // 否则，调用 map 的 toMap() 方法将内容转换为 HashMap。
        final StringMap map = localMap.get();
        return map == null ? new HashMap<>() : map.toMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringMap getReadOnlyContextData() {
        // 方法的主要功能和目的：
        // 获取当前线程的只读上下文数据。如果当前线程没有关联的 StringMap，则会创建一个新的并设置到 ThreadLocal 中。
        // 返回值：
        // 当前线程的 StringMap 实例，作为只读上下文数据。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 为 null，则创建一个新的 StringMap 并设置到 localMap 中。
        // 返回获取或创建的 StringMap。
        StringMap map = localMap.get();
        if (map == null) {
            map = createStringMap();
            localMap.set(map);
        }
        return map;
    }

    @Override
    public Map<String, String> getImmutableMapOrNull() {
        // 方法的主要功能和目的：
        // 获取当前线程上下文映射内容的不可变 Map 视图。
        // 返回值：
        // 包含上下文映射所有键值对的不可变 Map。如果当前线程没有关联的 StringMap，则返回 null。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 为 null，则返回 null。
        // 否则，调用 map 的 toMap() 方法将内容转换为 Map，然后使用 Collections.unmodifiableMap 包装成不可变 Map。
        final StringMap map = localMap.get();
        return map == null ? null : Collections.unmodifiableMap(map.toMap());
    }

    @Override
    public boolean isEmpty() {
        // 方法的主要功能和目的：
        // 检查当前线程的上下文映射是否为空。
        // 返回值：
        // 如果映射为空或当前线程没有关联的 StringMap 则返回 true，否则返回 false。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 为 null，则返回 true。
        // 否则，调用 map 的 isEmpty 方法。
        final StringMap map = localMap.get();
        return map == null || map.isEmpty();
    }

    @Override
    public String toString() {
        // 方法的主要功能和目的：
        // 返回当前线程上下文映射的字符串表示。
        // 返回值：
        // 映射内容的字符串表示。如果当前线程没有关联的 StringMap，则返回 "{}"。
        // 实现方式：
        // 尝试从 localMap 中获取当前线程的 StringMap。如果 map 为 null，则返回 "{}"。
        // 否则，调用 map 的 toString 方法。
        final StringMap map = localMap.get();
        return map == null ? "{}" : map.toString();
    }

    @Override
    public int hashCode() {
        // 方法的主要功能和目的：
        // 计算当前线程上下文映射的哈希码。
        // 返回值：
        // 映射内容的哈希码。
        // 实现方式：
        // 基于 localMap 中存储的 StringMap 的哈希码计算。如果 StringMap 为 null，则哈希码为 0。
        final int prime = 31;// 常量，用于哈希码计算的乘数
        int result = 1;// 初始结果
        final StringMap map = this.localMap.get();
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        // 方法的主要功能和目的：
        // 比较当前对象与另一个对象是否相等。
        // 参数：
        // obj: 要比较的对象。
        // 返回值：
        // 如果两个对象相等则返回 true，否则返回 false。
        // 实现方式：
        // 1. 如果是同一个对象引用，直接返回 true。
        // 2. 如果 obj 为 null，返回 false。
        // 3. 如果 obj 不是 ThreadContextMap 的实例，返回 false。
        // 4. 获取当前对象和另一个对象的不可变 Map 视图，然后使用 Objects.equals 进行比较。
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ThreadContextMap)) {
            return false;
        }
        final ThreadContextMap other = (ThreadContextMap) obj;
        final Map<String, String> map = this.getImmutableMapOrNull();
        final Map<String, String> otherMap = other.getImmutableMapOrNull();
        return Objects.equals(map, otherMap);
    }
}
