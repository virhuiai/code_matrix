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
package org.apache.logging.log4j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.status.StatusLogger;

/**
 * <em>Consider this class private.</em>
 * Array-based implementation of the {@code ReadOnlyStringMap} interface. Keys are held in a sorted array.
 * <p>
 * This is not a generic collection, but makes some trade-offs to optimize for the Log4j context data use case:
 * </p>
 * <ul>
 *   <li>Garbage-free iteration over key-value pairs with {@code BiConsumer} and {@code TriConsumer}.</li>
 *   <li>Fast copy. If the ThreadContextMap is also an instance of {@code SortedArrayStringMap}, the full thread context
 *     data can be transferred with two array copies and two field updates.</li>
 *   <li>Acceptable performance for small data sets. The current implementation stores keys in a sorted array, values
 *     are stored in a separate array at the same index.
 *     Worst-case performance of {@code get} and {@code containsKey} is O(log N),
 *     worst-case performance of {@code put} and {@code remove} is O(N log N).
 *     The expectation is that for the small values of {@code N} (less than 100) that are the vast majority of
 *     ThreadContext use cases, the constants dominate performance more than the asymptotic performance of the
 *     algorithms used.
 *     </li>
 *     <li>Compact representation.</li>
 * </ul>
 *
 * 该类被视为私有类，实现基于数组的ReadOnlyStringMap接口，键存储在排序数组中。
 * <p>
 * 这不是通用的集合类，而是为Log4j上下文数据用例进行优化：
 * </p>
 * <ul>
 *   <li>使用BiConsumer和TriConsumer实现无垃圾回收的键值对迭代。</li>
 *   <li>快速复制。如果ThreadContextMap也是SortedArrayStringMap实例，可通过两次数组复制和两个字段更新传输完整线程上下文数据。</li>
 *   <li>适用于小数据集的性能。当前实现将键存储在排序数组中，值存储在相同索引的另一个数组中。
 *     get和containsKey的最坏情况性能为O(log N)，put和remove的最坏情况性能为O(N log N)。
 *     对于ThreadContext大多数用例中N值较小（小于100），常数项对性能的影响大于算法的渐进性能。</li>
 *   <li>紧凑的存储表示。</li>
 * </ul>
 *
 * @since 2.7
 */
public class SortedArrayStringMap implements IndexedStringMap {

    /**
     * The default initial capacity.
     */
    // 默认初始容量
    private static final int DEFAULT_INITIAL_CAPACITY = 4;
    // 序列化版本号
    private static final long serialVersionUID = -5748905872274478116L;
    // 哈希计算常数
    private static final int HASHVAL = 31;

    // 用于批量添加键值对的TriConsumer
    private static final TriConsumer<String, Object, StringMap> PUT_ALL = (key, value, contextData) -> contextData.putValue(key, value);
    // 中文注释：定义一个TriConsumer，用于在putAll操作中将键值对添加到SortedArrayStringMap实例

    /**
     * An empty array instance to share when the table is not inflated.
     */
    // 未初始化表时的共享空数组
    private static final String[] EMPTY = Strings.EMPTY_ARRAY;
    // 冻结集合时的错误信息
    private static final String FROZEN = "Frozen collection cannot be modified";
    // 中文注释：定义空数组和冻结集合的提示信息，用于优化内存使用和防止修改冻结集合

    // 键和值数组，存储键值对
    private transient String[] keys = EMPTY;
    private transient Object[] values = EMPTY;
    // 中文注释：定义键和值的数组，初始为空数组，用于存储键值对数据

    /**
     * The number of key-value mappings contained in this map.
     */
    // 键值对数量
    private transient int size;
    // 中文注释：记录当前映射中的键值对数量

    // 反射方法，用于序列化过滤
    private static final Method setObjectInputFilter;
    private static final Method getObjectInputFilter;
    private static final Method newObjectInputFilter;
    // 中文注释：定义用于序列化输入流过滤的反射方法

    static {
        Method[] methods = ObjectInputStream.class.getMethods();
        Method setMethod = null;
        Method getMethod = null;
        for (final Method method : methods) {
            if (method.getName().equals("setObjectInputFilter")) {
                setMethod = method;
            } else if (method.getName().equals("getObjectInputFilter")) {
                getMethod = method;
            }
        }
        Method newMethod = null;
        try {
            if (setMethod != null) {
                final Class<?> clazz = Class.forName("org.apache.logging.log4j.util.internal.DefaultObjectInputFilter");
                methods = clazz.getMethods();
                for (final Method method : methods) {
                    if (method.getName().equals("newInstance") && Modifier.isStatic(method.getModifiers())) {
                        newMethod = method;
                        break;
                    }
                }
            }
        } catch (final ClassNotFoundException ex) {
            // Ignore the exception
        }
        newObjectInputFilter = newMethod;
        setObjectInputFilter = setMethod;
        getObjectInputFilter = getMethod;
        // 中文注释：通过反射获取ObjectInputStream的过滤方法，用于序列化安全控制，忽略ClassNotFoundException
    }

    /**
     * The next size value at which to resize (capacity * load factor).
     * @serial
     */
    // If table == EMPTY_TABLE then this is the initial capacity at which the
    // table will be created when inflated.
    // 下一次扩容的阈值（容量 * 负载因子）
    private int threshold;
    // 是否为不可变集合
    private boolean immutable;
    // 是否正在迭代
    private transient boolean iterating;
    // 中文注释：定义扩容阈值、不可变标志和迭代状态，用于控制集合的动态调整和并发修改检查

    public SortedArrayStringMap() {
        this(DEFAULT_INITIAL_CAPACITY);
        // 中文注释：默认构造方法，使用默认初始容量初始化
    }

    public SortedArrayStringMap(final int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be at least zero but was " + initialCapacity);
        }
        threshold = ceilingNextPowerOfTwo(initialCapacity == 0 ? 1 : initialCapacity);
        // 中文注释：构造方法，初始化容量并计算扩容阈值，确保初始容量非负
    }

    public SortedArrayStringMap(final ReadOnlyStringMap other) {
        if (other instanceof SortedArrayStringMap) {
            initFrom0((SortedArrayStringMap) other);
        } else if (other != null) {
            resize(ceilingNextPowerOfTwo(other.size()));
            other.forEach(PUT_ALL, this);
        }
        // 中文注释：从另一个ReadOnlyStringMap构造，优化复制逻辑，处理SortedArrayStringMap特殊情况
    }

    public SortedArrayStringMap(final Map<String, ?> map) {
        resize(ceilingNextPowerOfTwo(map.size()));
        for (final Map.Entry<String, ?> entry : map.entrySet()) {
            // The key might not actually be a String.
            putValue(Objects.toString(entry.getKey(), null), entry.getValue());
        }
        // 中文注释：从Map构造，调整容量并将键值对转换为字符串键后添加
    }

    private void assertNotFrozen() {
        if (immutable) {
            throw new UnsupportedOperationException(FROZEN);
        }
        // 中文注释：检查集合是否冻结，若冻结则抛出不可修改异常
    }

    private void assertNoConcurrentModification() {
        if (iterating) {
            throw new ConcurrentModificationException();
        }
        // 中文注释：检查是否在迭代期间修改集合，若是则抛出并发修改异常
    }

    @Override
    public void clear() {
        if (keys == EMPTY) {
            return;
        }
        assertNotFrozen();
        assertNoConcurrentModification();

        Arrays.fill(keys, 0, size, null);
        Arrays.fill(values, 0, size, null);
        size = 0;
        // 中文注释：清空集合，重置键值数组并将大小置零，确保未冻结且无并发修改
    }

    @Override
    public boolean containsKey(final String key) {
        return indexOfKey(key) >= 0;
        // 中文注释：检查是否包含指定键，通过查找键索引判断
    }

    @Override
    public Map<String, String> toMap() {
        final Map<String, String> result = new HashMap<>(size());
        for (int i = 0; i < size(); i++) {
            final Object value = getValueAt(i);
            result.put(getKeyAt(i), value == null ? null : String.valueOf(value));
        }
        return result;
        // 中文注释：将集合转换为HashMap，遍历键值对并将值转换为字符串
    }

    @Override
    public void freeze() {
        immutable = true;
        // 中文注释：冻结集合，设置不可变标志，禁止后续修改
    }

    @Override
    public boolean isFrozen() {
        return immutable;
        // 中文注释：返回集合是否冻结的状态
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V getValue(final String key) {
        final int index = indexOfKey(key);
        if (index < 0) {
            return null;
        }
        return (V) values[index];
        // 中文注释：根据键获取值，若键不存在返回null，类型转换为指定类型
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
        // 中文注释：检查集合是否为空，通过大小判断
    }

    @Override
    public int indexOfKey(final String key) {
        if (keys == EMPTY) {
            return -1;
        }
        if (key == null) { // null key is located at the start of the array
            return nullKeyIndex(); // insert at index zero
        }
        final int start = size > 0 && keys[0] == null ? 1 : 0;
        return Arrays.binarySearch(keys, start, size, key);
        // 中文注释：查找键的索引，处理空键和非空键，使用二分查找优化性能
    }

    private int nullKeyIndex() {
        return size > 0 && keys[0] == null ? 0 : ~0;
        // 中文注释：处理空键的索引，若首元素为空键返回0，否则返回~0
    }

    @Override
    public void putValue(final String key, final Object value) {
        assertNotFrozen();
        assertNoConcurrentModification();

        if (keys == EMPTY) {
            inflateTable(threshold);
        }
        final int index = indexOfKey(key);
        if (index >= 0) {
            keys[index] = key;
            values[index] = value;
        } else { // not found, so insert.
            insertAt(~index, key, value);
        }
        // 中文注释：添加或更新键值对，若集合为空则初始化表，存在键则更新，否则插入新键值对
    }

    private void insertAt(final int index, final String key, final Object value) {
        ensureCapacity();
        System.arraycopy(keys, index, keys, index + 1, size - index);
        System.arraycopy(values, index, values, index + 1, size - index);
        keys[index] = key;
        values[index] = value;
        size++;
        // 中文注释：在指定索引插入键值对，移动数组元素，更新大小
    }

    @Override
    public void putAll(final ReadOnlyStringMap source) {
        if (source == this || source == null || source.isEmpty()) {
            // this.putAll(this) does not modify this collection
            // this.putAll(null) does not modify this collection
            // this.putAll(empty ReadOnlyStringMap) does not modify this collection
            return;
        }
        assertNotFrozen();
        assertNoConcurrentModification();

        if (source instanceof SortedArrayStringMap) {
            if (this.size == 0) {
                initFrom0((SortedArrayStringMap) source);
            } else {
                merge((SortedArrayStringMap) source);
            }
        } else if (source != null) {
            source.forEach(PUT_ALL, this);
        }
        // 中文注释：批量添加键值对，处理自身、null或空集合情况，优化SortedArrayStringMap的合并逻辑
    }

    private void initFrom0(final SortedArrayStringMap other) {
        if (keys.length < other.size) {
            keys = new String[other.threshold];
            values = new Object[other.threshold];
        }
        System.arraycopy(other.keys, 0, keys, 0, other.size);
        System.arraycopy(other.values, 0, values, 0, other.size);

        size = other.size;
        threshold = other.threshold;
        // 中文注释：从另一个SortedArrayStringMap初始化，复制键值数组并更新大小和阈值
    }

    private void merge(final SortedArrayStringMap other) {
        final String[] myKeys = keys;
        final Object[] myVals = values;
        final int newSize = other.size + this.size;
        threshold = ceilingNextPowerOfTwo(newSize);
        if (keys.length < threshold) {
            keys = new String[threshold];
            values = new Object[threshold];
        }
        // move largest collection to the beginning of this data structure, smallest to the end
        boolean overwrite = true;
        if (other.size() > size()) {
            // move original key-values to end
            System.arraycopy(myKeys, 0, keys, other.size, this.size);
            System.arraycopy(myVals, 0, values, other.size, this.size);

            // insert additional key-value pairs at the beginning
            System.arraycopy(other.keys, 0, keys, 0, other.size);
            System.arraycopy(other.values, 0, values, 0, other.size);
            size = other.size;

            // loop over original keys and insert (drop values for same key)
            overwrite = false;
        } else {
            System.arraycopy(myKeys, 0, keys, 0, this.size);
            System.arraycopy(myVals, 0, values, 0, this.size);

            // move additional key-value pairs to end
            System.arraycopy(other.keys, 0, keys, this.size, other.size);
            System.arraycopy(other.values, 0, values, this.size, other.size);

            // new values are at the end, will be processed below. Overwrite is true.
        }
        for (int i = size; i < newSize; i++) {
            final int index = indexOfKey(keys[i]);
            if (index < 0) { // key does not exist
                insertAt(~index, keys[i], values[i]);
            } else if (overwrite) { // existing key: only overwrite when looping over the new values
                keys[index] = keys[i];
                values[index] = values[i];
            }
        }
        // prevent memory leak: null out references
        Arrays.fill(keys, size, newSize, null);
        Arrays.fill(values, size, newSize, null);
        // 中文注释：合并两个SortedArrayStringMap，优化大小集合的合并策略，处理键冲突并防止内存泄漏
    }

    private void ensureCapacity() {
        if (size >= threshold) {
            resize(threshold * 2);
        }
        // 中文注释：确保容量足够，若大小达到阈值则扩容为两倍
    }

    private void resize(final int newCapacity) {
        final String[] oldKeys = keys;
        final Object[] oldValues = values;

        keys = new String[newCapacity];
        values = new Object[newCapacity];

        System.arraycopy(oldKeys, 0, keys, 0, size);
        System.arraycopy(oldValues, 0, values, 0, size);

        threshold = newCapacity;
        // 中文注释：调整数组容量，复制旧数组内容并更新阈值
    }

    /**
     * Inflates the table.
     */
    private void inflateTable(final int toSize) {
        threshold = toSize;
        keys = new String[toSize];
        values = new Object[toSize];
        // 中文注释：初始化表，分配指定大小的键值数组并设置阈值
    }

    @Override
    public void remove(final String key) {
        if (keys == EMPTY) {
            return;
        }

        final int index = indexOfKey(key);
        if (index >= 0) {
            assertNotFrozen();
            assertNoConcurrentModification();

            System.arraycopy(keys, index + 1, keys, index, size - 1 - index);
            System.arraycopy(values, index + 1, values, index, size - 1 - index);
            keys[size - 1] = null;
            values[size - 1] = null;
            size--;
        }
        // 中文注释：移除指定键的键值对，移动数组元素并更新大小，确保未冻结且无并发修改
    }

    @Override
    public String getKeyAt(final int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return keys[index];
        // 中文注释：获取指定索引的键，检查索引有效性
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V getValueAt(final int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return (V) values[index];
        // 中文注释：获取指定索引的值，检查索引有效性并转换类型
    }

    @Override
    public int size() {
        return size;
        // 中文注释：返回集合中键值对的数量
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> void forEach(final BiConsumer<String, ? super V> action) {
        iterating = true;
        try {
            for (int i = 0; i < size; i++) {
                action.accept(keys[i], (V) values[i]);
            }
        } finally {
            iterating = false;
        }
        // 中文注释：遍历键值对，调用BiConsumer处理，设置迭代标志以防止并发修改
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V, T> void forEach(final TriConsumer<String, ? super V, T> action, final T state) {
        iterating = true;
        try {
            for (int i = 0; i < size; i++) {
                action.accept(keys[i], (V) values[i], state);
            }
        } finally {
            iterating = false;
        }
        // 中文注释：遍历键值对，调用TriConsumer处理并传递额外状态，设置迭代标志
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SortedArrayStringMap)) {
            return false;
        }
        final SortedArrayStringMap other = (SortedArrayStringMap) obj;
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!Objects.equals(keys[i], other.keys[i])) {
                return false;
            }
            if (!Objects.equals(values[i], other.values[i])) {
                return false;
            }
        }
        return true;
        // 中文注释：比较两个SortedArrayStringMap是否相等，检查大小和键值对内容
    }

    @Override
    public int hashCode() {
        int result = 37;
        result = HASHVAL * result + size;
        result = HASHVAL * result + hashCode(keys, size);
        result = HASHVAL * result + hashCode(values, size);
        return result;
        // 中文注释：计算哈希值，结合大小、键和值的哈希
    }

    private static int hashCode(final Object[] values, final int length) {
        int result = 1;
        for (int i = 0; i < length; i++) {
            result = HASHVAL * result + (values[i] == null ? 0 : values[i].hashCode());
        }
        return result;
        // 中文注释：计算数组的哈希值，处理空值情况
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append('{');
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(keys[i]).append('=');
            sb.append(values[i] == this ? "(this map)" : values[i]);
        }
        sb.append('}');
        return sb.toString();
        // 中文注释：将集合转换为字符串表示，格式化为键值对列表
    }

    /**
     * Save the state of the {@code SortedArrayStringMap} instance to a stream (i.e.,
     * serialize it).
     *
     * @serialData The <i>capacity</i> of the SortedArrayStringMap (the length of the
     *             bucket array) is emitted (int), followed by the
     *             <i>size</i> (an int, the number of key-value
     *             mappings), followed by the key (Object) and value (Object)
     *             for each key-value mapping.  The key-value mappings are
     *             emitted in no particular order.
     */
    private void writeObject(final java.io.ObjectOutputStream s) throws IOException {
        // Write out the threshold, and any hidden stuff
        s.defaultWriteObject();

        // Write out number of buckets
        if (keys == EMPTY) {
            s.writeInt(ceilingNextPowerOfTwo(threshold));
        } else {
            s.writeInt(keys.length);
        }

        // Write out size (number of Mappings)
        s.writeInt(size);

        // Write out keys and values (alternating)
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                s.writeObject(keys[i]);
                try {
                    s.writeObject(marshall(values[i]));
                } catch (final Exception e) {
                    handleSerializationException(e, i, keys[i]);
                    s.writeObject(null);
                }
            }
        }
        // 中文注释：序列化SortedArrayStringMap到流，写入容量、大小和键值对，处理序列化异常
    }

    private static byte[] marshall(final Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bout)) {
            oos.writeObject(obj);
            oos.flush();
            return bout.toByteArray();
        }
        // 中文注释：将对象序列化为字节数组，处理空对象情况
    }

    private static Object unmarshall(final byte[] data, final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bin = new ByteArrayInputStream(data);
        Collection<String> allowedClasses = null;
        ObjectInputStream ois;
        if (inputStream instanceof FilteredObjectInputStream) {
            allowedClasses = ((FilteredObjectInputStream) inputStream).getAllowedClasses();
            ois = new FilteredObjectInputStream(bin, allowedClasses);
        } else {
            try {
                final Object obj = getObjectInputFilter.invoke(inputStream);
                final Object filter = newObjectInputFilter.invoke(null, obj);
                ois = new ObjectInputStream(bin);
                setObjectInputFilter.invoke(ois, filter);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new StreamCorruptedException("Unable to set ObjectInputFilter on stream");
            }
        }
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
        // 中文注释：从字节数组反序列化对象，应用过滤器确保安全，处理异常
    }

    /**
     * Calculate the next power of 2, greater than or equal to x.
     * <p>
     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
     *
     * @param x Value to round up
     * @return The next power of 2 from x inclusive
     */
    private static int ceilingNextPowerOfTwo(final int x) {
        final int BITS_PER_INT = 32;
        return 1 << (BITS_PER_INT - Integer.numberOfLeadingZeros(x - 1));
        // 中文注释：计算大于或等于x的下一个2的幂，优化容量分配
    }

    /**
     * Reconstitute the {@code SortedArrayStringMap} instance from a stream (i.e.,
     * deserialize it).
     */
    private void readObject(final java.io.ObjectInputStream s)  throws IOException, ClassNotFoundException {
        if (!(s instanceof FilteredObjectInputStream) && setObjectInputFilter == null) {
            throw new IllegalArgumentException("readObject requires a FilteredObjectInputStream or an ObjectInputStream that accepts an ObjectInputFilter");
        }
        // Read in the threshold (ignored), and any hidden stuff
        s.defaultReadObject();

        // set other fields that need values
        keys = EMPTY;
        values = EMPTY;

        // Read in number of buckets
        final int capacity = s.readInt();
        if (capacity < 0) {
            throw new InvalidObjectException("Illegal capacity: " + capacity);
        }

        // Read number of mappings
        final int mappings = s.readInt();
        if (mappings < 0) {
            throw new InvalidObjectException("Illegal mappings count: " + mappings);
        }

        // allocate the bucket array;
        if (mappings > 0) {
            inflateTable(capacity);
        } else {
            threshold = capacity;
        }

        // Read the keys and values, and put the mappings in the arrays
        for (int i = 0; i < mappings; i++) {
            keys[i] = (String) s.readObject();
            try {
                final byte[] marshalledObject = (byte[]) s.readObject();
                values[i] = marshalledObject == null ? null : unmarshall(marshalledObject, s);
            } catch (final Exception | LinkageError error) {
                handleSerializationException(error, i, keys[i]);
                values[i] = null;
            }
        }
        size = mappings;
        // 中文注释：从流中反序列化SortedArrayStringMap，验证输入流，初始化数组并读取键值对
    }

    private void handleSerializationException(final Throwable t, final int i, final String key) {
        StatusLogger.getLogger().warn("Ignoring {} for key[{}] ('{}')", String.valueOf(t), i, keys[i]);
        // 中文注释：处理序列化异常，记录警告并设置对应值为null
    }
}
