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

package org.apache.logging.log4j;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.DefaultThreadContextStack;
import org.apache.logging.log4j.spi.NoOpThreadContextMap;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextMap2;
import org.apache.logging.log4j.spi.CleanableThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;
import org.apache.logging.log4j.spi.ThreadContextStack;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * The ThreadContext allows applications to store information either in a Map or a Stack.
 * <p>
 * <b><em>The MDC is managed on a per thread basis</em></b>. To enable automatic inheritance of <i>copies</i> of the MDC
 * to newly created threads, enable the {@value org.apache.logging.log4j.spi.DefaultThreadContextMap#INHERITABLE_MAP}
 * Log4j system property.
 * </p>
 * @see <a href="https://logging.apache.org/log4j/2.x/manual/thread-context.html">Thread Context Manual</a>
 */
// 中文注释：
// 该类提供线程上下文管理功能，允许应用程序在 Map 或 Stack 中存储信息。
// 功能：支持线程级别的上下文存储（MDC，Mapped Diagnostic Context），可通过系统属性启用子线程继承上下文。
// 注意事项：MDC 是线程隔离的，需启用 INHERITABLE_MAP 属性以实现子线程继承。
// 关键功能：提供键值对（Map）和栈（Stack）两种方式管理线程上下文数据。
public final class ThreadContext {

    /**
     * An empty read-only ThreadContextStack.
     */
    // 中文注释：
    // 内部类：EmptyThreadContextStack，一个空的只读线程上下文栈实现。
    // 功能：提供不可修改的空栈，支持 ThreadContextStack 接口的只读操作。
    // 注意事项：不支持添加、删除等修改操作，抛出 UnsupportedOperationException。
    private static class EmptyThreadContextStack extends AbstractCollection<String> implements ThreadContextStack {

        private static final long serialVersionUID = 1L;

        private static final Iterator<String> EMPTY_ITERATOR = new EmptyIterator<>();

        /**
         * Returns the element at the top of the stack.
         *
         * @return The element at the top of the stack.
         * @throws java.util.NoSuchElementException if the stack is empty.
         */
        // 中文注释：
        // 方法：从栈顶弹出元素。
        // 功能：由于是空栈，直接返回 null。
        // 返回值：始终为 null。
        @Override
        public String pop() {
            return null;
        }

        /**
         * Returns the element at the top of the stack without removing it or null if the stack is empty.
         *
         * @return the element at the top of the stack or null if the stack is empty.
         */
        // 中文注释：
        // 方法：查看栈顶元素，不移除。
        // 功能：由于是空栈，返回 null。
        // 返回值：始终为 null。
        @Override
        public String peek() {
            return null;
        }

        /**
         * Pushes an element onto the stack.
         *
         * @param message The element to add.
         */
        // 中文注释：
        // 方法：向栈中压入元素。
        // 功能：不支持修改操作，抛出 UnsupportedOperationException。
        // 参数说明：message - 要压入的元素。
        // 注意事项：空栈不可修改，调用此方法会抛出异常。
        @Override
        public void push(final String message) {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the number of elements in the stack.
         *
         * @return the number of elements in the stack.
         */
        // 中文注释：
        // 方法：获取栈的深度（元素数量）。
        // 功能：返回空栈的元素数量，始终为 0。
        // 返回值：0。
        @Override
        public int getDepth() {
            return 0;
        }

        /**
         * Returns all the elements in the stack in a List.
         *
         * @return all the elements in the stack in a List.
         */
        // 中文注释：
        // 方法：将栈中所有元素转为 List。
        // 功能：返回空的 List（Collections.emptyList()）。
        // 返回值：空 List。
        @Override
        public List<String> asList() {
            return Collections.emptyList();
        }

        /**
         * Trims elements from the end of the stack.
         *
         * @param depth The maximum number of items in the stack to keep.
         */
        // 中文注释：
        // 方法：修剪栈，保留指定数量的元素。
        // 功能：空栈无需修剪，无操作。
        // 参数说明：depth - 要保留的元素数量。
        @Override
        public void trim(final int depth) {
            // Do nothing
        }

        @Override
        public boolean equals(final Object o) {
            // Similar to java.util.Collections.EmptyList.equals(Object)
            // 中文注释：
            // 方法：比较对象是否相等。
            // 功能：判断传入对象是否为 Collection 且为空。
            // 参数说明：o - 要比较的对象。
            // 返回值：true 表示传入对象为空集合，false 否则。
            return (o instanceof Collection) && ((Collection<?>) o).isEmpty();
        }

        @Override
        public int hashCode() {
            // Same as java.util.Collections.EmptyList.hashCode()
            // 中文注释：
            // 方法：获取哈希值。
            // 功能：返回固定值 1，与 Collections.EmptyList 一致。
            // 返回值：1。
            return 1;
        }

        @Override
        public ContextStack copy() {
            return this;
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            // 中文注释：
            // 方法：将栈元素转为数组。
            // 功能：不支持操作，抛出 UnsupportedOperationException。
            // 参数说明：a - 目标数组。
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(final String e) {
            // 中文注释：
            // 方法：添加元素到栈。
            // 功能：不支持修改，抛出 UnsupportedOperationException。
            // 参数说明：e - 要添加的元素。
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            // 中文注释：
            // 方法：检查是否包含指定集合的所有元素。
            // 功能：空栈不包含任何元素，返回 false。
            // 参数说明：c - 要检查的集合。
            return false;
        }

        @Override
        public boolean addAll(final Collection<? extends String> c) {
            // 中文注释：
            // 方法：添加集合中的所有元素到栈。
            // 功能：不支持修改，抛出 UnsupportedOperationException。
            // 参数说明：c - 要添加的集合。
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            // 中文注释：
            // 方法：移除指定集合中的所有元素。
            // 功能：不支持修改，抛出 UnsupportedOperationException。
            // 参数说明：c - 要移除的元素集合。
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            // 中文注释：
            // 方法：保留指定集合中的元素。
            // 功能：不支持修改，抛出 UnsupportedOperationException。
            // 参数说明：c - 要保留的元素集合。
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<String> iterator() {
            // 中文注释：
            // 方法：获取栈的迭代器。
            // 功能：返回空的迭代器 EMPTY_ITERATOR。
            // 返回值：空迭代器。
            return EMPTY_ITERATOR;
        }

        @Override
        public int size() {
            // 中文注释：
            // 方法：获取栈的大小。
            // 功能：返回空栈的大小，始终为 0。
            // 返回值：0。
            return 0;
        }

        @Override
        public ContextStack getImmutableStackOrNull() {
            return this;
        }
    }

    /**
     * An empty iterator. Since Java 1.7 added the Collections.emptyIterator() method, we have to make do.
     *
     * @param <E> the type of the empty iterator
     */
    // 中文注释：
    // 内部类：EmptyIterator，一个空的迭代器实现。
    // 功能：提供空迭代器的行为，用于 EmptyThreadContextStack。
    // 注意事项：不支持迭代操作，调用 next() 会抛出 NoSuchElementException。
    private static class EmptyIterator<E> implements Iterator<E> {

        @Override
        public boolean hasNext() {
            // 中文注释：
            // 方法：检查是否还有下一个元素。
            // 功能：空迭代器始终返回 false。
            // 返回值：false。
            return false;
        }

        @Override
        public E next() {
            // 中文注释：
            // 方法：获取下一个元素。
            // 功能：抛出 NoSuchElementException，因为迭代器为空。
            // 注意事项：调用此方法会抛出异常。
            throw new NoSuchElementException("This is an empty iterator!");
        }

        @Override
        public void remove() {
            // no-op

            // 中文注释：
            // 方法：移除当前元素。
            // 功能：空迭代器无操作（no-op）。
            // 注意事项：调用此方法无效果。
            // Do nothing
        }
    }

    /**
     * Empty, immutable Map.
     */
    // ironically, this annotation gives an "unsupported @SuppressWarnings" warning in Eclipse
    @SuppressWarnings("PublicStaticCollectionField")
    // I like irony, so I won't delete it...
    // 中文注释：
    // 常量：EMPTY_MAP，一个空的不可变 Map。
    // 功能：提供空的键值对集合，用于表示空的线程上下文 Map。
    // 注意事项：不可修改，任何修改操作会抛出异常。
    public static final Map<String, String> EMPTY_MAP = Collections.emptyMap();

    /**
     * Empty, immutable ContextStack.
     */
    // ironically, this annotation gives an "unsupported @SuppressWarnings" warning in Eclipse
    @SuppressWarnings("PublicStaticCollectionField")
    // 中文注释：
    // 常量：EMPTY_STACK，一个空的不可变 ThreadContextStack。
    // 功能：提供空的线程上下文栈实例，用于表示空的栈。
    // 注意事项：不可修改，任何修改操作会抛出异常。
    public static final ThreadContextStack EMPTY_STACK = new EmptyThreadContextStack();

    // 中文注释：
    // 常量：配置属性名，用于控制是否禁用 Map 或 Stack 功能。
    // 功能：通过系统属性控制 ThreadContext 的行为。
    // 重要配置：
    // - DISABLE_MAP：禁用线程上下文 Map。
    // - DISABLE_STACK：禁用线程上下文 Stack。
    // - DISABLE_ALL：同时禁用 Map 和 Stack。
    private static final String DISABLE_MAP = "disableThreadContextMap";
    private static final String DISABLE_STACK = "disableThreadContextStack";
    private static final String DISABLE_ALL = "disableThreadContext";

    // 中文注释：
    // 变量：useStack，控制是否启用线程上下文 Stack。
    // 功能：决定是否允许使用 Stack 存储上下文数据。
    // 注意事项：由系统属性 DISABLE_STACK 或 DISABLE_ALL 控制。
    private static boolean useStack;

    // 中文注释：
    // 变量：contextMap，存储线程上下文的键值对数据。
    // 功能：管理线程级别的键值对（MDC）。
    // 注意事项：具体实现由 ThreadContextMapFactory 创建。
    private static ThreadContextMap contextMap;

    // 中文注释：
    // 变量：contextStack，存储线程上下文的栈数据。
    // 功能：管理线程级别的栈（NDC，Nested Diagnostic Context）。
    // 注意事项：默认使用 DefaultThreadContextStack 实现。
    private static ThreadContextStack contextStack;

    // 中文注释：
    // 变量：readOnlyContextMap，只读的线程上下文 Map。
    // 功能：提供只读访问的上下文 Map，可能为 null。
    // 注意事项：仅当 contextMap 实现 ReadOnlyThreadContextMap 接口时非 null。
    private static ReadOnlyThreadContextMap readOnlyContextMap;

    static {
        init();
    }

    // 中文注释：
    // 构造函数：私有，防止外部实例化。
    // 功能：确保 ThreadContext 作为工具类仅通过静态方法使用。
    private ThreadContext() {
        // empty
    }

    /**
     * <em>Consider private, used for testing.</em>
     */
    // 中文注释：
    // 方法：init，初始化 ThreadContext 的静态配置。
    // 功能：设置 contextMap 和 contextStack，根据系统属性决定是否启用 Map 和 Stack。
    // 关键步骤：
    // 1. 初始化 ThreadContextMapFactory。
    // 2. 读取系统属性，决定是否禁用 Map 或 Stack。
    // 3. 初始化 contextStack 为 DefaultThreadContextStack。
    // 4. 根据 useMap 设置 contextMap（NoOpThreadContextMap 或 ThreadContextMapFactory 创建的实现）。
    // 5. 设置 readOnlyContextMap（如果 contextMap 支持只读接口）。
    // 重要配置：通过 DISABLE_ALL、DISABLE_MAP、DISABLE_STACK 属性控制行为。
    // 注意事项：仅用于测试，视为私有方法。
    static void init() {
        ThreadContextMapFactory.init();
        contextMap = null;
        final PropertiesUtil managerProps = PropertiesUtil.getProperties();
        boolean disableAll = managerProps.getBooleanProperty(DISABLE_ALL);
        useStack = !(managerProps.getBooleanProperty(DISABLE_STACK) || disableAll);
        boolean useMap = !(managerProps.getBooleanProperty(DISABLE_MAP) || disableAll);

        contextStack = new DefaultThreadContextStack(useStack);
        if (!useMap) {
            contextMap = new NoOpThreadContextMap();
        } else {
            contextMap = ThreadContextMapFactory.createThreadContextMap();
        }
        if (contextMap instanceof ReadOnlyThreadContextMap) {
            readOnlyContextMap = (ReadOnlyThreadContextMap) contextMap;
        } else {
            readOnlyContextMap = null;
        }
    }



    /**
     * Puts a context value (the <code>value</code> parameter) as identified with the <code>key</code> parameter into
     * the current thread's context map.
     *
     * <p>
     * If the current thread does not have a context map it is created as a side effect.
     * </p>
     *
     * @param key The key name.
     * @param value The key value.
     */
    // 中文注释：
    // 方法：put，将键值对存储到当前线程的上下文 Map 中。
    // 功能：将指定的键和值存入 contextMap，若 Map 不存在则自动创建。
    // 参数说明：
    // - key：键名。
    // - value：键值。
    // 交互逻辑：直接委托给 contextMap 的 put 方法，自动处理 Map 的初始化。
    public static void put(final String key, final String value) {
        contextMap.put(key, value);
    }

    /**
     * Puts a context value (the <code>value</code> parameter) as identified with the <code>key</code> parameter into
     * the current thread's context map if the key does not exist.
     *
     * <p>
     * If the current thread does not have a context map it is created as a side effect.
     * </p>
     *
     * @param key The key name.
     * @param value The key value.
     * @since 2.13.0
     */
    // 中文注释：
    // 方法：putIfNull，仅当键不存在时将键值对存储到当前线程的上下文 Map。
    // 功能：检查 key 是否存在，若不存在则调用 put 方法存储。
    // 参数说明：
    // - key：键名。
    // - value：键值。
    // 交互逻辑：通过 containsKey 检查键是否存在，避免覆盖已有值。
    // 注意事项：自 Log4j 2.13.0 起支持。
    public static void putIfNull(final String key, final String value) {
        if(!contextMap.containsKey(key)) {
            contextMap.put(key, value);
        }
    }

    /**
     * Puts all given context map entries into the current thread's
     * context map.
     *
     * <p>If the current thread does not have a context map it is
     * created as a side effect.</p>
     * @param m The map.
     * @since 2.7
     */
    // 中文注释：
    // 方法：putAll，将指定 Map 的所有键值对存入当前线程的上下文 Map。
    // 功能：批量添加键值对，支持 ThreadContextMap2 或 DefaultThreadContextMap 的优化实现。
    // 参数说明：m - 包含键值对的 Map。
    // 交互逻辑：根据 contextMap 类型调用 putAll 或逐个 put。
    // 注意事项：若 Map 不存在则自动创建，自 Log4j 2.7 起支持。
    public static void putAll(final Map<String, String> m) {
        if (contextMap instanceof ThreadContextMap2) {
            ((ThreadContextMap2) contextMap).putAll(m);
        } else if (contextMap instanceof DefaultThreadContextMap) {
            ((DefaultThreadContextMap) contextMap).putAll(m);
        } else {
            for (final Map.Entry<String, String> entry: m.entrySet()) {
                contextMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Gets the context value identified by the <code>key</code> parameter.
     *
     * <p>
     * This method has no side effects.
     * </p>
     *
     * @param key The key to locate.
     * @return The value associated with the key or null.
     */
    // 中文注释：
    // 方法：get，获取指定键对应的垢 contextMap。
    // 功能：返回与 key 关联的值，若不存在则返回 null。
    // 参数说明：key - 要查询的键名。
    // 交互逻辑：直接调用 contextMap 的 get 方法，无副作用。
    // 返回值：键对应的值或 null。
    public static String get(final String key) {
        return contextMap.get(key);
    }

    /**
     * Removes the context value identified by the <code>key</code> parameter.
     *
     * @param key The key to remove.
     */
    // 中文注释：
    // 方法：remove，移除指定键的上下文值。
    // 功能：从 contextMap 中删除指定的键值对。
    // 参数说明：key - 要移除的键名。
    // 交互逻辑：直接调用 contextMap 的 remove 方法。
    public static void remove(final String key) {
        contextMap.remove(key);
    }

    /**
     * Removes the context values identified by the <code>keys</code> parameter.
     *
     * @param keys The keys to remove.
     *
     * @since 2.8
     */
    // 中文注释：
    // 方法：removeAll，批量移除指定键的上下文值。
    // 功能：根据 contextMap 类型调用 removeAll 或逐个移除。
    // 参数说明：keys - 要移除的键集合。
    // 交互逻辑：支持 CleanableThreadContextMap 和 DefaultThreadContextMap 的优化实现。
    // 注意事项：自 Log4j 2.8 起支持。
    public static void removeAll(final Iterable<String> keys) {
        if (contextMap instanceof CleanableThreadContextMap) {
            ((CleanableThreadContextMap) contextMap).removeAll(keys);
        } else if (contextMap instanceof DefaultThreadContextMap) {
            ((DefaultThreadContextMap) contextMap).removeAll(keys);
        } else {
            for (final String key : keys) {
                contextMap.remove(key);
            }
        }
    }

    /**
     * Clears the context map.
     */
    // 中文注释：
    // 方法：clearMap，清空当前线程的上下文 Map。
    // 功能：移除 contextMap 中的所有键值对。
    // 交互逻辑：调用 contextMap 的 clear 方法。
    public static void clearMap() {
        contextMap.clear();
    }

    /**
     * Clears the context map and stack.
     */
    // 中文注释：
    // 方法：clearAll，清空当前线程的上下文 Map 和 Stack。
    // 功能：同时清除 contextMap 和 contextStack。
    // 交互逻辑：调用 clearMap 和 clearStack 方法。
    public static void clearAll() {
        clearMap();
        clearStack();
    }

    /**
     * Determines if the key is in the context.
     *
     * @param key The key to locate.
     * @return True if the key is in the context, false otherwise.
     */
    // 中文注释：
    // 方法：containsKey，检查上下文 Map 中是否包含指定键。
    // 功能：查询 contextMap 是否包含 key。
    // 参数说明：key - 要检查的键名。
    // 返回值：true 表示键存在，false 表示不存在。
    public static boolean containsKey(final String key) {
        return contextMap.containsKey(key);
    }

    /**
     * Returns a mutable copy of current thread's context Map.
     *
     * @return a mutable copy of the context.
     */
    // 中文注释：
    // 方法：getContext，返回当前线程上下文 Map 的可变副本。
    // 功能：获取 contextMap 的可修改副本。
    // 返回值：可变的 Map 副本。
    // 注意事项：副本可独立修改，不影响原始 contextMap。
    public static Map<String, String> getContext() {
        return contextMap.getCopy();
    }

    /**
     * Returns an immutable view of the current thread's context Map.
     *
     * @return An immutable view of the ThreadContext Map.
     */
    // 中文注释：
    // 方法：getImmutableContext，返回当前线程上下文 Map 的不可变视图。
    // 功能：获取 contextMap 的不可变视图，若无则返回 EMPTY_MAP。
    // 返回值：不可变的 Map 或 EMPTY_MAP。
    // 注意事项：返回的 Map 不可修改，尝试修改可能抛出异常。
    public static Map<String, String> getImmutableContext() {
        final Map<String, String> map = contextMap.getImmutableMapOrNull();
        return map == null ? EMPTY_MAP : map;
    }

    /**
     * Returns a read-only view of the internal data structure used to store thread context key-value pairs,
     * or {@code null} if the internal data structure does not implement the
     * {@code ReadOnlyThreadContextMap} interface.
     * <p>
     * The {@link DefaultThreadContextMap} implementation does not implement {@code ReadOnlyThreadContextMap}, so by
     * default this method returns {@code null}.
     * </p>
     *
     * @return the internal data structure used to store thread context key-value pairs or {@code null}
     * @see ThreadContextMapFactory
     * @see DefaultThreadContextMap
     * @see org.apache.logging.log4j.spi.CopyOnWriteSortedArrayThreadContextMap
     * @see org.apache.logging.log4j.spi.GarbageFreeSortedArrayThreadContextMap
     * @since 2.8
     */
    // 中文注释：
    // 方法：getThreadContextMap，返回只读的上下文 Map 内部数据结构。
    // 功能：返回 readOnlyContextMap，若不支持 ReadOnlyThreadContextMap 则返回 null。
    // 返回值：只读 Map 或 null。
    // 注意事项：DefaultThreadContextMap 不支持此接口，默认返回 null，自 Log4j 2.8 起支持。
    public static ReadOnlyThreadContextMap getThreadContextMap() {
        return readOnlyContextMap;
    }

    /**
     * Returns true if the Map is empty.
     *
     * @return true if the Map is empty, false otherwise.
     */
    // 中文注释：
    // 方法：isEmpty，检查上下文 Map 是否为空。
    // 功能：调用 contextMap 的 isEmpty 方法。
    // 返回值：true 表示 Map 为空，false 表示非空。
    public static boolean isEmpty() {
        return contextMap.isEmpty();
    }

    /**
     * Clears the stack for this thread.
     */
    // 中文注释：
    // 方法：clearStack，清空当前线程的上下文 Stack。
    // 功能：移除 contextStack 中的所有元素。
    // 交互逻辑：调用 contextStack 的 clear 方法。
    public static void clearStack() {
        contextStack.clear();
    }

    /**
     * Returns a copy of this thread's stack.
     *
     * @return A copy of this thread's stack.
     */
    // 中文注释：
    // 方法：cloneStack，返回当前线程上下文 Stack 的副本。
    // 功能：获取 contextStack 的可修改副本。
    // 返回值：ContextStack 的副本。
    // 注意事项：副本可独立修改，不影响原始 Stack。
    public static ContextStack cloneStack() {
        return contextStack.copy();
    }

    /**
     * Gets an immutable copy of this current thread's context stack.
     *
     * @return an immutable copy of the ThreadContext stack.
     */
    // 中文注释：
    // 方法：getImmutableStack，返回当前线程上下文 Stack 的不可变副本。
    // 功能：获取 contextStack 的不可变视图，若无则返回 EMPTY_STACK。
    // 返回值：不可变的 ContextStack 或 EMPTY_STACK。
    // 注意事项：返回的 Stack 不可修改，尝试修改可能抛出异常。
    public static ContextStack getImmutableStack() {
        final ContextStack result = contextStack.getImmutableStackOrNull();
        return result == null ? EMPTY_STACK : result;
    }

    /**
     * Sets this thread's stack.
     *
     * @param stack The stack to use.
     */
    // 中文注释：
    // 方法：setStack，设置当前线程的上下文 Stack。
    // 功能：清空现有 Stack，并添加指定集合中的元素。
    // 参数说明：stack - 要设置的元素集合。
    // 交互逻辑：若 stack 为空或 useStack 为 false，则无操作；否则清空后添加。
    // 注意事项：仅在 useStack 为 true 时有效。
    public static void setStack(final Collection<String> stack) {
        if (stack.isEmpty() || !useStack) {
            return;
        }
        contextStack.clear();
        contextStack.addAll(stack);
    }

    /**
     * Gets the current nesting depth of this thread's stack.
     *
     * @return the number of items in the stack.
     *
     * @see #trim
     */
    // 中文注释：
    // 方法：getDepth，获取当前线程上下文 Stack 的深度。
    // 功能：返回 contextStack 的元素数量。
    // 返回值：栈中的元素个数。
    // 交互逻辑：直接调用 contextStack 的 getDepth 方法。
    public static int getDepth() {
        return contextStack.getDepth();
    }

    /**
     * Returns the value of the last item placed on the stack.
     *
     * <p>
     * The returned value is the value that was pushed last. If no context is available, then the empty string "" is
     * returned.
     * </p>
     *
     * @return String The innermost diagnostic context.
     */
    // 中文注释：
    // 方法：pop，从栈顶弹出元素。
    // 功能：移除并返回 contextStack 的栈顶元素，若为空返回空字符串。
    // 返回值：栈顶元素或空字符串。
    // 交互逻辑：调用 contextStack 的 pop 方法。
    public static String pop() {
        return contextStack.pop();
    }

    /**
     * Looks at the last diagnostic context at the top of this NDC without removing it.
     *
     * <p>
     * The returned value is the value that was pushed last. If no context is available, then the empty string "" is
     * returned.
     * </p>
     *
     * @return String The innermost diagnostic context.
     */
    // 中文注释：
    // 方法：peek，查看栈顶元素，不移除。
    // 功能：返回 contextStack 的栈顶元素，若为空返回空字符串。
    // 返回值：栈顶元素或空字符串。
    // 交互逻辑：调用 contextStack 的 peek 方法。
    public static String peek() {
        return contextStack.peek();
    }

    /**
     * Pushes new diagnostic context information for the current thread.
     *
     * <p>
     * The contents of the <code>message</code> parameter is determined solely by the client.
     * </p>
     *
     * @param message The new diagnostic context information.
     */
    // 中文注释：
    // 方法：push，将新的诊断上下文信息压入当前线程的 Stack。
    // 功能：将 message 压入 contextStack。
    // 参数说明：message - 要压入的诊断上下文信息。
    // 交互逻辑：直接调用 contextStack 的 push 方法。
    public static void push(final String message) {
        contextStack.push(message);
    }

    /**
     * Pushes new diagnostic context information for the current thread.
     *
     * <p>
     * The contents of the <code>message</code> and args parameters are determined solely by the client. The message
     * will be treated as a format String and tokens will be replaced with the String value of the arguments in
     * accordance with ParameterizedMessage.
     * </p>
     *
     * @param message The new diagnostic context information.
     * @param args Parameters for the message.
     */
    // 中文注释：
    // 方法：push，将格式化的诊断上下文信息压入当前线程的 Stack。
    // 功能：使用 ParameterizedMessage 格式化 message 和 args，然后压入 contextStack。
    // 参数说明：
    // - message：格式化的诊断上下文信息。
    // - args：格式化参数。
    // 交互逻辑：通过 ParameterizedMessage.format 格式化后调用 push 方法。
    public static void push(final String message, final Object... args) {
        contextStack.push(ParameterizedMessage.format(message, args));
    }

    /**
     * Removes the diagnostic context for this thread.
     *
     * <p>
     * Each thread that created a diagnostic context by calling {@link #push} should call this method before exiting.
     * Otherwise, the memory used by the <b>thread</b> cannot be reclaimed by the VM.
     * </p>
     *
     * <p>
     * As this is such an important problem in heavy duty systems and because it is difficult to always guarantee that
     * the remove method is called before exiting a thread, this method has been augmented to lazily remove references
     * to dead threads. In practice, this means that you can be a little sloppy and occasionally forget to call
     * {@link #remove} before exiting a thread. However, you must call <code>remove</code> sometime. If you never call
     * it, then your application is sure to run out of memory.
     * </p>
     */
    // 中文注释：
    // 方法：removeStack，清空当前线程的诊断上下文 Stack。
    // 功能：调用 contextStack 的 clear 方法，移除所有元素。
    // 注意事项：建议在线程退出前调用此方法以释放内存，Log4j 支持延迟清理死线程，但仍需调用。
    // 交互逻辑：直接调用 contextStack 的 clear 方法。
    public static void removeStack() {
        contextStack.clear();
    }

    /**
     * Trims elements from this diagnostic context. If the current depth is smaller or equal to <code>maxDepth</code>,
     * then no action is taken. If the current depth is larger than newDepth then all elements at maxDepth or higher are
     * discarded.
     *
     * <p>
     * This method is a convenient alternative to multiple {@link #pop} calls. Moreover, it is often the case that at
     * the end of complex call sequences, the depth of the ThreadContext is unpredictable. The <code>trim</code> method
     * circumvents this problem.
     * </p>
     *
     * <p>
     * For example, the combination
     * </p>
     *
     * <pre>
     * void foo() {
     *     final int depth = ThreadContext.getDepth();
     *
     *     // ... complex sequence of calls
     *
     *     ThreadContext.trim(depth);
     * }
     * </pre>
     *
     * <p>
     * ensures that between the entry and exit of {@code foo} the depth of the diagnostic stack is conserved.
     * </p>
     *
     * @see #getDepth
     * @param depth The number of elements to keep.
     */
    // 中文注释：
    // 方法：trim，修剪诊断上下文 Stack，保留指定数量的元素。
    // 功能：若当前栈深度大于 depth，则移除多余元素。
    // 参数说明：depth - 要保留的元素数量。
    // 交互逻辑：调用 contextStack 的 trim 方法，简化多重 pop 操作。
    // 注意事项：用于确保复杂调用序列后栈深度可控。
    public static void trim(final int depth) {
        contextStack.trim(depth);
    }

    /**
     * The ThreadContext Stack interface.
     */
    // 中文注释：
    // 接口：ContextStack，定义线程上下文栈的操作。
    // 功能：提供栈的增删查改方法，支持序列化和集合操作。
    // 关键方法：
    // - pop：弹出栈顶元素。
    // - peek：查看栈顶元素。
    // - push：压入元素。
    // - getDepth：获取栈深度。
    // - asList：转为 List。
    // - trim：修剪栈。
    // - copy：复制栈。
    // - getImmutableStackOrNull：获取不可变栈。
    public interface ContextStack extends Serializable, Collection<String> {

        /**
         * Returns the element at the top of the stack.
         *
         * @return The element at the top of the stack.
         * @throws java.util.NoSuchElementException if the stack is empty.
         */
        // 中文注释：
        // 方法：pop，弹出栈顶元素。
        // 功能：移除并返回栈顶元素，若为空抛出 NoSuchElementException。
        // 返回值：栈顶元素。
        String pop();

        /**
         * Returns the element at the top of the stack without removing it or null if the stack is empty.
         *
         * @return the element at the top of the stack or null if the stack is empty.
         */
        // 中文注释：
        // 方法：peek，查看栈顶元素，不移除。
        // 功能：返回栈顶元素，若为空返回 null。
        // 返回值：栈顶元素或 null。
        String peek();

        /**
         * Pushes an element onto the stack.
         *
         * @param message The element to add.
         */
        // 中文注释：
        // 方法：push，压入元素到栈顶。
        // 功能：将 message 添加到 contextStack。
        // 参数说明：message - 要压入的元素。
        void push(String message);

        /**
         * Returns the number of elements in the stack.
         *
         * @return the number of elements in the stack.
         */
        // 中文注释：
        // 方法：getDepth，获取栈的元素数量。
        // 功能：返回 contextStack 的深度。
        // 返回值：栈中的元素个数。
        int getDepth();

        /**
         * Returns all the elements in the stack in a List.
         *
         * @return all the elements in the stack in a List.
         */
        // 中文注释：
        // 方法：asList，将栈元素转为 List。
        // 功能：返回 contextStack 的所有元素列表。
        // 返回值：包含栈元素的 List。
        List<String> asList();

        /**
         * Trims elements from the end of the stack.
         *
         * @param depth The maximum number of items in the stack to keep.
         */
        // 中文注释：
        // 方法：trim，修剪栈，保留指定数量的元素。
        // 功能：移除超过 depth 的元素。
        // 参数说明：depth - 要保留的元素数量。
        void trim(int depth);

        /**
         * Returns a copy of the ContextStack.
         *
         * @return a copy of the ContextStack.
         */
        // 中文注释：
        // 方法：copy，复制栈。
        // 功能：返回 contextStack 的副本。
        // 返回值：ContextStack 的副本。
        ContextStack copy();

        /**
         * Returns a ContextStack with the same contents as this ContextStack or {@code null}. Attempts to modify the
         * returned stack may or may not throw an exception, but will not affect the contents of this ContextStack.
         *
         * @return a ContextStack with the same contents as this ContextStack or {@code null}.
         */
        // 中文注释：
        // 方法：getImmutableStackOrNull，获取不可变栈副本。
        // 功能：返回不可变的 contextStack 副本或 null。
        // 返回值：不可变栈或 null。
        // 注意事项：修改返回的栈不会影响原栈。
        ContextStack getImmutableStackOrNull();
    }
}
