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
 * Apache软件基金会许可声明，详细说明版权归属及使用许可。
 */
package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * {@code SortedArrayStringMap}-based implementation of the {@code ThreadContextMap} interface that creates a copy of
 * the data structure on every modification. Any particular instance of the data structure is a snapshot of the
 * ThreadContext at some point in time and can safely be passed off to other threads.  Since it is
 * expected that the Map will be passed to many more log events than the number of keys it contains the performance
 * should be much better than if the Map was copied for each event.
 *
 * @since 2.7
 */
/**
 * 基于SortedArrayStringMap的ThreadContextMap接口实现，每次修改时创建数据结构的副本。
 * 每个数据结构实例是ThreadContext在某一时刻的快照，可安全传递给其他线程。
 * 由于预期Map将被传递给远多于其包含键数量的日志事件，性能优于每次事件都复制Map。
 *
 * 类的主要功能和目的：
 * - 提供线程安全的ThreadContextMap实现，用于存储和管理线程上下文数据。
 * - 每次修改时创建新副本，确保数据不可变性，适合多线程环境。
 * - 优化性能，减少不必要的复制操作。
 *
 * 执行流程：
 * - 初始化时根据系统属性设置初始容量和是否可继承。
 * - 使用ThreadLocal存储线程独立的StringMap实例。
 * - 修改操作（如put、remove）时创建新副本，更新后冻结数据。
 *
 * 特殊处理逻辑：
 * - 数据结构在修改后冻结（freeze），确保不可变性。
 * - 支持线程继承（InheritableThreadLocal）或普通线程局部变量（ThreadLocal），通过配置决定。
 *
 * @since 2.7
 */
class CopyOnWriteSortedArrayThreadContextMap implements ReadOnlyThreadContextMap, ObjectThreadContextMap, CopyOnWrite {

    /**
     * Property name ({@value} ) for selecting {@code InheritableThreadLocal} (value "true") or plain
     * {@code ThreadLocal} (value is not "true") in the implementation.
     */
    /**
     * 系统属性名称，用于选择使用InheritableThreadLocal（值为"true"）或普通ThreadLocal（值非"true"）。
     *
     * 变量用途：
     * - 控制线程上下文是否在子线程中继承。
     *
     * 配置参数含义：
     * - 属性值为"true"时，上下文数据可被子线程继承；否则，仅当前线程可见。
     */
    public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";

    /**
     * The default initial capacity.
     */
    /**
     * 默认初始容量。
     *
     * 变量用途：
     * - 指定StringMap的默认初始容量，用于优化内存分配。
     *
     * 配置参数含义：
     * - 默认值为16，可通过系统属性调整。
     */
    protected static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * System property name that can be used to control the data structure's initial capacity.
     */
    /**
     * 系统属性名称，用于控制数据结构的初始容量。
     *
     * 变量用途：
     * - 允许通过系统属性动态配置StringMap的初始容量。
     *
     * 配置参数含义：
     * - 属性值应为正整数，覆盖默认初始容量。
     */
    protected static final String PROPERTY_NAME_INITIAL_CAPACITY = "log4j2.ThreadContext.initial.capacity";

    private static final StringMap EMPTY_CONTEXT_DATA = new SortedArrayStringMap(1);
    /**
     * 空的上下文数据，容量为1。
     *
     * 变量用途：
     * - 提供空的StringMap实例，作为默认或初始状态的占位符。
     *
     * 特殊处理逻辑：
     * - 在静态初始化时冻结，确保不可修改。
     */

    private static volatile int initialCapacity;
    /**
     * 初始容量， volatile确保线程安全。
     *
     * 变量用途：
     * - 存储从系统属性读取的初始容量值。
     *
     * 注意事项：
     * - 使用volatile修饰符，确保多线程环境下可见性。
     */
    private static volatile boolean inheritableMap;
    /**
     * 是否使用可继承的ThreadLocal，volatile确保线程安全。
     *
     * 变量用途：
     * - 控制是否使用InheritableThreadLocal存储上下文数据。
     *
     * 注意事项：
     * - 使用volatile修饰符，确保多线程环境下可见性。
     */

    /**
     * Initializes static variables based on system properties. Normally called when this class is initialized by the VM
     * and when Log4j is reconfigured.
     */
    /**
     * 根据系统属性初始化静态变量。通常在类由虚拟机初始化或Log4j重新配置时调用。
     *
     * 方法功能：
     * - 读取系统属性，设置初始容量和是否可继承的标志。
     *
     * 执行流程：
     * 1. 获取PropertiesUtil实例。
     * 2. 读取初始容量属性，默认为DEFAULT_INITIAL_CAPACITY。
     * 3. 读取是否可继承属性，决定使用ThreadLocal或InheritableThreadLocal。
     */
    static void init() {
        final PropertiesUtil properties = PropertiesUtil.getProperties();
        initialCapacity = properties.getIntegerProperty(PROPERTY_NAME_INITIAL_CAPACITY, DEFAULT_INITIAL_CAPACITY);
        inheritableMap = properties.getBooleanProperty(INHERITABLE_MAP);
    }
    
    static {
        EMPTY_CONTEXT_DATA.freeze();
        init();
    }
    /**
     * 静态初始化块，冻结空上下文数据并调用init方法。
     *
     * 执行流程：
     * 1. 冻结EMPTY_CONTEXT_DATA，确保其不可修改。
     * 2. 调用init方法初始化静态变量。
     *
     * 特殊处理逻辑：
     * - 确保空数据在类加载时即不可变。
     */

    private final ThreadLocal<StringMap> localMap;
    /**
     * 线程局部变量，存储当前线程的StringMap实例。
     *
     * 变量用途：
     * - 每个线程维护独立的StringMap实例，确保线程隔离。
     *
     * 注意事项：
     * - 根据inheritableMap的值，可能为ThreadLocal或InheritableThreadLocal。
     */

    public CopyOnWriteSortedArrayThreadContextMap() {
        this.localMap = createThreadLocalMap();
    }
    /**
     * 构造函数，初始化线程局部变量。
     *
     * 方法功能：
     * - 创建并设置线程局部变量localMap。
     *
     * 执行流程：
     * 1. 调用createThreadLocalMap方法，生成ThreadLocal或InheritableThreadLocal实例。
     * 2. 将结果赋值给localMap。
     */

    // LOG4J2-479: by default, use a plain ThreadLocal, only use InheritableThreadLocal if configured.
    // (This method is package protected for JUnit tests.)
    /**
     * 默认使用普通ThreadLocal，仅在配置时使用InheritableThreadLocal。
     * （此方法为包级别访问权限，用于JUnit测试。）
     */
    private ThreadLocal<StringMap> createThreadLocalMap() {
        if (inheritableMap) {
            return new InheritableThreadLocal<StringMap>() {
                @Override
                protected StringMap childValue(final StringMap parentValue) {
                    if (parentValue == null) {
                        return null;
                    }
                    final StringMap stringMap = createStringMap(parentValue);
                    stringMap.freeze();
                    return stringMap;
                }
            };
        }
        // if not inheritable, return plain ThreadLocal with null as initial value
        return new ThreadLocal<>();
    }
    /**
     * 创建ThreadLocal或InheritableThreadLocal实例。
     *
     * 方法功能：
     * - 根据inheritableMap的值，决定创建哪种ThreadLocal。
     *
     * 参数说明：
     * - 无参数。
     *
     * 返回值：
     * - ThreadLocal<StringMap>：普通ThreadLocal或InheritableThreadLocal实例。
     *
     * 执行流程：
     * 1. 如果inheritableMap为true，创建InheritableThreadLocal。
     * 2. 重写childValue方法，确保子线程继承父线程的上下文副本。
     * 3. 如果inheritableMap为false，返回普通ThreadLocal，初始值为null。
     *
     * 特殊处理逻辑：
     * - InheritableThreadLocal的childValue方法创建新副本并冻结，确保子线程数据不可变。
     */

    /**
     * Returns an implementation of the {@code StringMap} used to back this thread context map.
     * <p>
     * Subclasses may override.
     * </p>
     * @return an implementation of the {@code StringMap} used to back this thread context map
     */
    /**
     * 返回支持线程上下文映射的StringMap实现。
     * <p>
     * 子类可重写。
     * </p>
     *
     * 方法功能：
     * - 创建新的StringMap实例，用于存储上下文数据。
     *
     * 返回值：
     * - StringMap：新的SortedArrayStringMap实例，容量为initialCapacity。
     *
     * 注意事项：
     * - 子类可根据需要重写以提供自定义实现。
     */
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
    /**
     * 返回支持线程上下文映射的StringMap实现，预填充指定上下文数据的内容。
     * <p>
     * 子类可重写。
     * </p>
     *
     * 方法功能：
     * - 根据已有ReadOnlyStringMap创建并填充新的StringMap实例。
     *
     * 参数说明：
     * - original：ReadOnlyStringMap，包含初始化的键值对。
     *
     * 返回值：
     * - StringMap：新的SortedArrayStringMap实例，包含original中的数据。
     *
     * 注意事项：
     * - 子类可重写以自定义复制逻辑。
     */
    protected StringMap createStringMap(final ReadOnlyStringMap original) {
        return new SortedArrayStringMap(original);
    }

    @Override
    public void put(final String key, final String value) {
        putValue(key, value);
    }
    /**
     * 将键值对存入上下文映射。
     *
     * 方法功能：
     * - 向当前线程的上下文映射添加字符串键值对。
     *
     * 参数说明：
     * - key：字符串键。
     * - value：字符串值。
     *
     * 执行流程：
     * - 调用putValue方法处理实际存储逻辑。
     */

    @Override
    public void putValue(final String key, final Object value) {
        StringMap map = localMap.get();
        map = map == null ? createStringMap() : createStringMap(map);
        map.putValue(key, value);
        map.freeze();
        localMap.set(map);
    }
    /**
     * 将键值对存入上下文映射，支持任意对象作为值。
     *
     * 方法功能：
     * - 向当前线程的上下文映射添加键值对。
     *
     * 参数说明：
     * - key：字符串键。
     * - value：任意对象值。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空，创建新StringMap；否则，复制现有map。
     * 3. 将键值对存入map。
     * 4. 冻结map，确保不可变。
     * 5. 更新localMap。
     *
     * 特殊处理逻辑：
     * - 每次修改创建新副本，确保线程安全和数据不可变性。
     */

    @Override
    public void putAll(final Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        StringMap map = localMap.get();
        map = map == null ? createStringMap() : createStringMap(map);
        for (final Map.Entry<String, String> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
        }
        map.freeze();
        localMap.set(map);
    }
    /**
     * 批量添加字符串键值对到上下文映射。
     *
     * 方法功能：
     * - 将一组字符串键值对批量存入当前线程的上下文映射。
     *
     * 参数说明：
     * - values：包含字符串键值对的Map。
     *
     * 执行流程：
     * 1. 检查values是否为空或null，若是则直接返回。
     * 2. 获取当前线程的StringMap。
     * 3. 如果map为空，创建新StringMap；否则，复制现有map。
     * 4. 遍历values，将所有键值对存入map。
     * 5. 冻结map，确保不可变。
     * 6. 更新localMap。
     *
     * 特殊处理逻辑：
     * - 空输入直接返回，避免不必要操作。
     */

    @Override
    public <V> void putAllValues(final Map<String, V> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        StringMap map = localMap.get();
        map = map == null ? createStringMap() : createStringMap(map);
        for (final Map.Entry<String, V> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
        }
        map.freeze();
        localMap.set(map);
    }
    /**
     * 批量添加任意类型的键值对到上下文映射。
     *
     * 方法功能：
     * - 将一组键值对（值类型任意）批量存入当前线程的上下文映射。
     *
     * 参数说明：
     * - values：包含键值对的Map，值类型为V。
     *
     * 执行流程：
     * 1. 检查values是否为空或null，若是则直接返回。
     * 2. 获取当前线程的StringMap。
     * 3. 如果map为空，创建新StringMap；否则，复制现有map。
     * 4. 遍历values，将所有键值对存入map。
     * 5. 冻结map，确保不可变。
     * 6. 更新localMap。
     *
     * 特殊处理逻辑：
     * - 支持任意类型的值，增加灵活性。
     */

    @Override
    public String get(final String key) {
        return (String) getValue(key);
    }
    /**
     * 获取指定键对应的字符串值。
     *
     * 方法功能：
     * - 从当前线程的上下文映射中获取指定键的字符串值。
     *
     * 参数说明：
     * - key：要查询的字符串键。
     *
     * 返回值：
     * - String：键对应的字符串值，若不存在返回null。
     *
     * 执行流程：
     * - 调用getValue方法获取值并转换为字符串。
     */

    @Override
    public <V> V getValue(final String key) {
        final StringMap map = localMap.get();
        return map == null ? null : map.<V>getValue(key);
    }
    /**
     * 获取指定键对应的值。
     *
     * 方法功能：
     * - 从当前线程的上下文映射中获取指定键的值。
     *
     * 参数说明：
     * - key：要查询的字符串键。
     *
     * 返回值：
     * - V：键对应的值，若不存在返回null。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空，返回null；否则，返回指定键的值。
     */

    @Override
    public void remove(final String key) {
        final StringMap map = localMap.get();
        if (map != null) {
            final StringMap copy = createStringMap(map);
            copy.remove(key);
            copy.freeze();
            localMap.set(copy);
        }
    }
    /**
     * 移除指定键的键值对。
     *
     * 方法功能：
     * - 从当前线程的上下文映射中移除指定键。
     *
     * 参数说明：
     * - key：要移除的字符串键。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map不为空，创建其副本。
     * 3. 从副本中移除指定键。
     * 4. 冻结副本，确保不可变。
     * 5. 更新localMap。
     *
     * 特殊处理逻辑：
     * - 如果map为空，无需操作。
     */

    @Override
    public void removeAll(final Iterable<String> keys) {
        final StringMap map = localMap.get();
        if (map != null) {
            final StringMap copy = createStringMap(map);
            for (final String key : keys) {
                copy.remove(key);
            }
            copy.freeze();
            localMap.set(copy);
        }
    }
    /**
     * 批量移除指定键的键值对。
     *
     * 方法功能：
     * - 从当前线程的上下文映射中移除一组指定键。
     *
     * 参数说明：
     * - keys：要移除的键集合。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map不为空，创建其副本。
     * 3. 遍历keys，逐一移除指定键。
     * 4. 冻结副本，确保不可变。
     * 5. 更新localMap。
     *
     * 特殊处理逻辑：
     * - 如果map为空，无需操作。
     */

    @Override
    public void clear() {
        localMap.remove();
    }
    /**
     * 清空当前线程的上下文映射。
     *
     * 方法功能：
     * - 移除当前线程的整个StringMap。
     *
     * 执行流程：
     * - 调用ThreadLocal的remove方法，清空localMap。
     */

    @Override
    public boolean containsKey(final String key) {
        final StringMap map = localMap.get();
        return map != null && map.containsKey(key);
    }
    /**
     * 检查是否包含指定键。
     *
     * 方法功能：
     * - 检查当前线程的上下文映射是否包含指定键。
     *
     * 参数说明：
     * - key：要检查的字符串键。
     *
     * 返回值：
     * - boolean：true表示包含，false表示不包含。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空或不包含键，返回false；否则返回true。
     */

    @Override
    public Map<String, String> getCopy() {
        final StringMap map = localMap.get();
        return map == null ? new HashMap<>() : map.toMap();
    }
    /**
     * 获取上下文映射的副本。
     *
     * 方法功能：
     * - 返回当前线程上下文映射的键值对副本。
     *
     * 返回值：
     * - Map<String, String>：包含所有键值对的Map，若map为空返回空HashMap。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空，返回新HashMap；否则，返回map的键值对副本。
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public StringMap getReadOnlyContextData() {
        final StringMap map = localMap.get();
        return map == null ? EMPTY_CONTEXT_DATA : map;
    }
    /**
     * 获取只读的上下文数据。
     *
     * 方法功能：
     * - 返回当前线程的只读StringMap实例。
     *
     * 返回值：
     * - StringMap：当前线程的StringMap，若为空返回EMPTY_CONTEXT_DATA。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空，返回冻结的空数据；否则返回map。
     */

    @Override
    public Map<String, String> getImmutableMapOrNull() {
        final StringMap map = localMap.get();
        return map == null ? null : Collections.unmodifiableMap(map.toMap());
    }
    /**
     * 获取不可变的上下文映射或null。
     *
     * 方法功能：
     * - 返回当前线程上下文映射的不可变副本。
     *
     * 返回值：
     * - Map<String, String>：不可变的键值对Map，若map为空返回null。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空，返回null；否则，返回不可变的Map副本。
     */

    @Override
    public boolean isEmpty() {
        final StringMap map = localMap.get();
        return map == null || map.isEmpty();
    }
    /**
     * 检查上下文映射是否为空。
     *
     * 方法功能：
     * - 判断当前线程的上下文映射是否为空。
     *
     * 返回值：
     * - boolean：true表示为空，false表示非空。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空或其isEmpty方法返回true，返回true；否则返回false。
     */

    @Override
    public String toString() {
        final StringMap map = localMap.get();
        return map == null ? "{}" : map.toString();
    }
    /**
     * 返回上下文映射的字符串表示。
     *
     * 方法功能：
     * - 将当前线程的上下文映射转换为字符串。
     *
     * 返回值：
     * - String：map的字符串表示，若map为空返回"{}"。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 如果map为空，返回"{}"；否则，返回map的字符串表示。
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final StringMap map = this.localMap.get();
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        return result;
    }
    /**
     * 计算对象的哈希值。
     *
     * 方法功能：
     * - 根据当前线程的StringMap计算哈希值。
     *
     * 返回值：
     * - int：对象的哈希值。
     *
     * 执行流程：
     * 1. 获取当前线程的StringMap。
     * 2. 使用固定素数31和map的哈希值（若map不为空）计算结果。
     *
     * 特殊处理逻辑：
     * - 如果map为空，哈希值为固定值。
     */

    @Override
    public boolean equals(final Object obj) {
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
    /**
     * 判断对象是否相等。
     *
     * 方法功能：
     * - 比较当前对象与另一个ThreadContextMap对象的上下文映射。
     *
     * 参数说明：
     * - obj：要比较的对象。
     *
     * 返回值：
     * - boolean：true表示相等，false表示不相等。
     *
     * 执行流程：
     * 1. 如果对象相同，返回true。
     * 2. 如果obj为空或不是ThreadContextMap实例，返回false。
     * 3. 获取当前对象和比较对象的不可变Map。
     * 4. 使用Objects.equals比较两个Map。
     *
     * 特殊处理逻辑：
     * - 使用Objects.equals处理null情况，确保比较安全。
     */
}
