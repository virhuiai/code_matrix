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

import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.TriConsumer;

/**
 * The actual ThreadContext Map. A new ThreadContext Map is created each time it is updated and the Map stored is always
 * immutable. This means the Map can be passed to other threads without concern that it will be updated. Since it is
 * expected that the Map will be passed to many more log events than the number of keys it contains the performance
 * should be much better than if the Map was copied for each event.
 *
 * ThreadContext 的实际 Map 实现。每次更新时都会创建一个新的 ThreadContext Map，并且存储的 Map 始终是不可变的。
 * 这意味着 Map 可以传递给其他线程，而无需担心它会被更新。由于 Map 预计将被传递给比其包含的键数量更多的日志事件，
 * 因此性能应该比为每个事件复制 Map 要好得多。
 */
public class DefaultThreadContextMap implements ThreadContextMap, ReadOnlyStringMap {
    private static final long serialVersionUID = 8218007901108944053L;
    // 用于序列化的版本 UID。

    /**
     * Property name ({@value} ) for selecting {@code InheritableThreadLocal} (value "true") or plain
     * {@code ThreadLocal} (value is not "true") in the implementation.
     *
     * 属性名称（“isThreadContextMapInheritable”），用于在实现中选择 {@code InheritableThreadLocal}（值为“true”）
     * 还是普通的 {@code ThreadLocal}（值不是“true”）。
     */
    public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";

    private final boolean useMap;
    // 控制是否使用 ThreadContext Map 的标志。如果为 false，则所有操作都将是空操作。
    private final ThreadLocal<Map<String, String>> localMap;
    // 存储线程局部 Map 的 ThreadLocal 实例。它确保每个线程都有自己独立的 Map 副本。

    private static boolean inheritableMap;
    // 静态标志，指示是否使用 InheritableThreadLocal。此值通过系统属性配置。

    static {
        // 静态初始化块，在类加载时执行。
        init();
        // 调用 init 方法初始化 inheritableMap 标志。
    }

    // LOG4J2-479: by default, use a plain ThreadLocal, only use InheritableThreadLocal if configured.
    // (This method is package protected for JUnit tests.)
    // LOG4J2-479: 默认情况下，使用普通的 ThreadLocal；仅在配置时才使用 InheritableThreadLocal。
    // （此方法是包私有的，以便进行 JUnit 测试。）
    static ThreadLocal<Map<String, String>> createThreadLocalMap(final boolean isMapEnabled) {
        // 根据配置创建并返回一个 ThreadLocal 或 InheritableThreadLocal 实例。
        // 参数:
        //   isMapEnabled: 一个布尔值，指示是否启用 Map。
        // 返回值:
        //   一个 ThreadLocal<Map<String, String>> 实例，可能是普通的 ThreadLocal 或 InheritableThreadLocal。
        if (inheritableMap) {
            // 如果 inheritableMap 为 true，则使用 InheritableThreadLocal。
            return new InheritableThreadLocal<Map<String, String>>() {
                // 创建一个 InheritableThreadLocal 匿名子类。
                @Override
                protected Map<String, String> childValue(final Map<String, String> parentValue) {
                    // 当子线程创建时，此方法用于计算子线程的初始值，基于父线程的值。
                    // 参数:
                    //   parentValue: 父线程的 ThreadLocal 值。
                    // 返回值:
                    //   子线程的 ThreadLocal 值，如果是 null 或 Map 未启用，则返回 null，否则返回父值的一个不可变副本。
                    return parentValue != null && isMapEnabled //
                    // 如果父值不为空且 Map 已启用，则复制父值并使其不可变。
                    ? Collections.unmodifiableMap(new HashMap<>(parentValue)) //
                            : null;
                    // 否则，返回 null。
                }
            };
        }
        // if not inheritable, return plain ThreadLocal with null as initial value
        // 如果不是可继承的，则返回一个初始值为 null 的普通 ThreadLocal。
        return new ThreadLocal<>();
    }

    static void init() {
        // 初始化 inheritableMap 标志。
        // 此方法从系统属性中获取 "isThreadContextMapInheritable" 的布尔值。
        inheritableMap = PropertiesUtil.getProperties().getBooleanProperty(INHERITABLE_MAP);
    }
    
    public DefaultThreadContextMap() {
        // 默认构造函数，启用 ThreadContext Map。
        this(true);
    }

    public DefaultThreadContextMap(final boolean useMap) {
        // 构造函数，允许指定是否使用 ThreadContext Map。
        // 参数:
        //   useMap: 一个布尔值，如果为 true 则启用 Map，否则禁用。
        this.useMap = useMap;
        // 设置 useMap 标志。
        this.localMap = createThreadLocalMap(useMap);
        // 根据 useMap 标志创建 ThreadLocal 或 InheritableThreadLocal 实例。
    }

    @Override
    public void put(final String key, final String value) {
        // 将键值对放入 ThreadContext Map。
        // 参数:
        //   key: 要放入的键。
        //   value: 要放入的值。
        // 特殊处理逻辑：如果 useMap 为 false，则此操作不执行任何操作。
        // 执行流程：
        // 1. 获取当前线程的 Map。
        // 2. 如果 Map 为空，则创建一个新的 HashMap。
        // 3. 如果 Map 不为空，则创建一个现有 Map 的新副本，以确保不变性。
        // 4. 将键值对放入新副本中。
        // 5. 将不可变的新副本设置回 ThreadLocal。
        if (!useMap) {
            // 如果 useMap 为 false，则直接返回，不执行任何操作。
            return;
        }
        Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        map = map == null ? new HashMap<>(1) : new HashMap<>(map);
        // 如果 Map 为 null，则创建一个容量为 1 的新 HashMap；否则，创建一个现有 Map 的副本。
        map.put(key, value);
        // 将键值对放入 Map。
        localMap.set(Collections.unmodifiableMap(map));
        // 将 Map 设置为 ThreadLocal 的不可变副本。
    }

    public void putAll(final Map<String, String> m) {
        // 将给定 Map 中的所有键值对放入 ThreadContext Map。
        // 参数:
        //   m: 包含要放入的键值对的 Map。
        // 特殊处理逻辑：如果 useMap 为 false，则此操作不执行任何操作。
        // 执行流程：
        // 1. 获取当前线程的 Map。
        // 2. 如果 Map 为空，则创建一个新的 HashMap，容量与传入的 Map 相同。
        // 3. 如果 Map 不为空，则创建一个现有 Map 的新副本。
        // 4. 遍历传入 Map 的所有条目，并将其放入新副本中。
        // 5. 将不可变的新副本设置回 ThreadLocal。
        if (!useMap) {
            // 如果 useMap 为 false，则直接返回。
            return;
        }
        Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        map = map == null ? new HashMap<>(m.size()) : new HashMap<>(map);
        // 如果 Map 为 null，则创建一个新的 HashMap，容量为 m.size()；否则，创建一个现有 Map 的副本。
        for (final Map.Entry<String, String> e : m.entrySet()) {
            // 遍历传入 Map 的所有条目。
            map.put(e.getKey(), e.getValue());
            // 将条目放入 Map。
        }
        localMap.set(Collections.unmodifiableMap(map));
        // 将 Map 设置为 ThreadLocal 的不可变副本。
    }

    @Override
    public String get(final String key) {
        // 从 ThreadContext Map 中获取指定键的值。
        // 参数:
        //   key: 要获取值的键。
        // 返回值:
        //   与键关联的值，如果 Map 为 null 或键不存在，则返回 null。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        return map == null ? null : map.get(key);
        // 如果 Map 为 null，则返回 null；否则，从 Map 中获取值。
    }

    @Override
    public void remove(final String key) {
        // 从 ThreadContext Map 中移除指定键。
        // 参数:
        //   key: 要移除的键。
        // 执行流程：
        // 1. 获取当前线程的 Map。
        // 2. 如果 Map 不为空，则创建一个现有 Map 的新副本。
        // 3. 从新副本中移除指定的键。
        // 4. 将不可变的新副本设置回 ThreadLocal。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        if (map != null) {
            // 如果 Map 不为 null。
            final Map<String, String> copy = new HashMap<>(map);
            // 创建 Map 的一个可变副本。
            copy.remove(key);
            // 从副本中移除指定的键。
            localMap.set(Collections.unmodifiableMap(copy));
            // 将不可变的副本设置回 ThreadLocal。
        }
    }

    public void removeAll(final Iterable<String> keys) {
        // 从 ThreadContext Map 中移除指定集合中的所有键。
        // 参数:
        //   keys: 包含要移除的键的 Iterable 集合。
        // 执行流程：
        // 1. 获取当前线程的 Map。
        // 2. 如果 Map 不为空，则创建一个现有 Map 的新副本。
        // 3. 遍历传入的键集合，并从新副本中移除每个键。
        // 4. 将不可变的新副本设置回 ThreadLocal。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        if (map != null) {
            // 如果 Map 不为 null。
            final Map<String, String> copy = new HashMap<>(map);
            // 创建 Map 的一个可变副本。
            for (final String key : keys) {
                // 遍历要移除的键。
                copy.remove(key);
                // 从副本中移除键。
            }
            localMap.set(Collections.unmodifiableMap(copy));
            // 将不可变的副本设置回 ThreadLocal。
        }
    }

    @Override
    public void clear() {
        // 清空 ThreadContext Map。
        // 此操作通过从 ThreadLocal 中移除 Map 来实现。
        localMap.remove();
        // 从 ThreadLocal 中移除当前线程的 Map。
    }

    @Override
    public Map<String, String> toMap() {
        // 返回 ThreadContext Map 的一个可变副本。
        // 返回值:
        //   ThreadContext Map 的可变副本。
        return getCopy();
        // 调用 getCopy 方法获取 Map 的副本。
    }

    @Override
    public boolean containsKey(final String key) {
        // 检查 ThreadContext Map 是否包含指定的键。
        // 参数:
        //   key: 要检查的键。
        // 返回值:
        //   如果 Map 包含该键，则返回 true；否则返回 false。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        return map != null && map.containsKey(key);
        // 如果 Map 不为 null 且包含该键，则返回 true。
    }

    @Override
    public <V> void forEach(final BiConsumer<String, ? super V> action) {
        // 遍历 ThreadContext Map 中的每个键值对，并对它们执行给定的操作。
        // 参数:
        //   action: 要执行的操作，一个 BiConsumer 接口实例，接受键和值作为参数。
        // 事件处理机制：
        //   通过 BiConsumer 接口实现对 Map 中每个元素的迭代和处理。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        if (map == null) {
            // 如果 Map 为 null，则直接返回。
            return;
        }
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            // 遍历 Map 中的每个条目。
            //BiConsumer should be able to handle values of any type V. In our case the values are of type String.
            // BiConsumer 应该能够处理任何类型 V 的值。在此处，值是 String 类型。
            @SuppressWarnings("unchecked")
            final
            V value = (V) entry.getValue();
            // 将值强制转换为类型 V。
            action.accept(entry.getKey(), value);
            // 对键和值执行给定的操作。
        }
    }

    @Override
    public <V, S> void forEach(final TriConsumer<String, ? super V, S> action, final S state) {
        // 遍历 ThreadContext Map 中的每个键值对，并对它们执行给定的操作，同时传入一个额外的状态对象。
        // 参数:
        //   action: 要执行的操作，一个 TriConsumer 接口实例，接受键、值和状态对象作为参数。
        //   state: 一个额外的状态对象，将在每次迭代时传递给 action。
        // 事件处理机制：
        //   通过 TriConsumer 接口实现对 Map 中每个元素的迭代和处理，同时允许传递上下文状态。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        if (map == null) {
            // 如果 Map 为 null，则直接返回。
            return;
        }
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            // 遍历 Map 中的每个条目。
            //TriConsumer should be able to handle values of any type V. In our case the values are of type String.
            // TriConsumer 应该能够处理任何类型 V 的值。在此处，值是 String 类型。
            @SuppressWarnings("unchecked")
            final
            V value = (V) entry.getValue();
            // 将值强制转换为类型 V。
            action.accept(entry.getKey(), value, state);
            // 对键、值和状态执行给定的操作。
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V getValue(final String key) {
        // 从 ThreadContext Map 中获取指定键的值，并将其强制转换为指定的类型 V。
        // 参数:
        //   key: 要获取值的键。
        // 返回值:
        //   与键关联的值，已强制转换为类型 V；如果 Map 为 null 或键不存在，则返回 null。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        return (V) (map == null ? null : map.get(key));
        // 如果 Map 为 null，则返回 null；否则，从 Map 中获取值并强制转换为类型 V。
    }

    @Override
    public Map<String, String> getCopy() {
        // 获取 ThreadContext Map 的一个可变副本。
        // 返回值:
        //   ThreadContext Map 的一个 HashMap 副本。如果原始 Map 为 null，则返回一个空的 HashMap。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        return map == null ? new HashMap<>() : new HashMap<>(map);
        // 如果 Map 为 null，则返回一个新的空 HashMap；否则，返回 Map 的一个副本。
    }

    @Override
    public Map<String, String> getImmutableMapOrNull() {
        // 获取 ThreadContext Map 的不可变实例，如果不存在则返回 null。
        // 返回值:
        //   ThreadContext Map 的不可变实例，或者 null。
        // 注意事项：返回的 Map 是不可变的，不应尝试修改它。
        return localMap.get();
        // 直接返回 ThreadLocal 中存储的 Map，它始终是不可变的。
    }

    @Override
    public boolean isEmpty() {
        // 检查 ThreadContext Map 是否为空。
        // 返回值:
        //   如果 Map 为 null 或不包含任何键值对，则返回 true；否则返回 false。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        return map == null || map.isEmpty();
        // 如果 Map 为 null 或为空，则返回 true。
    }

    @Override
    public int size() {
        // 获取 ThreadContext Map 中键值对的数量。
        // 返回值:
        //   Map 中键值对的数量。如果 Map 为 null，则返回 0。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        return map == null ? 0 : map.size();
        // 如果 Map 为 null，则返回 0；否则返回 Map 的大小。
    }

    @Override
    public String toString() {
        // 返回 ThreadContext Map 的字符串表示形式。
        // 返回值:
        //   ThreadContext Map 的字符串表示形式。如果 Map 为 null，则返回 "{}"。
        final Map<String, String> map = localMap.get();
        // 获取当前线程的 Map。
        return map == null ? "{}" : map.toString();
        // 如果 Map 为 null，则返回 "{}"；否则返回 Map 的 toString() 表示。
    }

    @Override
    public int hashCode() {
        // 计算 ThreadContext Map 的哈希码。
        // 返回值:
        //   ThreadContext Map 的哈希码。
        // 关键变量：
        //   prime: 用于哈希码计算的素数。
        //   result: 累积哈希码的结果。
        // 交互逻辑的实现方式：
        //   哈希码的计算基于底层 Map 的哈希码以及 useMap 标志的哈希码。
        final int prime = 31;
        // 定义一个素数，用于哈希码计算。
        int result = 1;
        // 初始化哈希码结果。
        final Map<String, String> map = this.localMap.get();
        // 获取当前线程的 Map。
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        // 将 Map 的哈希码（如果 Map 为 null 则为 0）累加到结果中。
        result = prime * result + Boolean.valueOf(this.useMap).hashCode();
        // 将 useMap 标志的哈希码累加到结果中。
        return result;
        // 返回计算出的哈希码。
    }

    @Override
    public boolean equals(final Object obj) {
        // 比较当前 DefaultThreadContextMap 实例与另一个对象是否相等。
        // 参数:
        //   obj: 要比较的对象。
        // 返回值:
        //   如果对象相等则返回 true；否则返回 false。
        // 交互逻辑的实现方式：
        //   首先进行引用相等性检查。
        //   然后检查 null 值和类型兼容性。
        //   如果 obj 是 DefaultThreadContextMap 实例，则比较 useMap 标志。
        //   最后，比较底层 Map 的内容是否相等。
        if (this == obj) {
            // 如果是同一个对象引用，则直接返回 true。
            return true;
        }
        if (obj == null) {
            // 如果比较对象为 null，则返回 false。
            return false;
        }
        if (obj instanceof DefaultThreadContextMap) {
            // 如果比较对象是 DefaultThreadContextMap 的实例。
            final DefaultThreadContextMap other = (DefaultThreadContextMap) obj;
            // 强制转换为 DefaultThreadContextMap 类型。
            if (this.useMap != other.useMap) {
                // 如果 useMap 标志不同，则返回 false。
                return false;
            }
        }
        if (!(obj instanceof ThreadContextMap)) {
            // 如果比较对象不是 ThreadContextMap 的实例，则返回 false。
            return false;
        }
        final ThreadContextMap other = (ThreadContextMap) obj;
        // 强制转换为 ThreadContextMap 类型。
        final Map<String, String> map = this.localMap.get();
        // 获取当前实例的 Map。
        final Map<String, String> otherMap = other.getImmutableMapOrNull();
        // 获取比较对象的 Map。
        return Objects.equals(map, otherMap);
        // 比较两个 Map 是否相等（包括 null 值）。
    }
}
