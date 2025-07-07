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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

/**
 * A copy-on-write thread-safe variant of {@code org.apache.logging.log4j.spi.ThreadContextStack} in which all mutative
 * operations (add, pop, and so on) are implemented by making a fresh copy of the underlying list.
 *
 * <p>
 * 这是一个线程安全的 {@code org.apache.logging.log4j.spi.ThreadContextStack} 的写时复制（copy-on-write）变体。
 * 所有的修改操作（如添加、弹出等）都是通过创建一个底层列表的新副本来实现的，以确保线程安全。
 * </p>
 */
public class DefaultThreadContextStack implements ThreadContextStack, StringBuilderFormattable {

    private static final Object[] EMPTY_OBJECT_ARRAY = {};
    // 定义一个空的 Object 数组，用于 toArray 方法的默认返回值。

    private static final long serialVersionUID = 5050501L;
    // 序列化ID，用于确保序列化和反序列化时的兼容性。

    private static final ThreadLocal<MutableThreadContextStack> STACK = new ThreadLocal<>();
    // 使用 ThreadLocal 来存储每个线程独立的 MutableThreadContextStack 实例。
    // 这样可以确保每个线程都有自己的上下文堆栈，互不干扰。

    private final boolean useStack;
    // 一个布尔标志，指示是否启用堆栈功能。
    // 如果为 false，则大部分堆栈操作将不执行任何实际操作。

    public DefaultThreadContextStack(final boolean useStack) {
        // 构造方法
        // 参数:
        //   useStack: 是否启用线程上下文堆栈。
        this.useStack = useStack;
    }

    private MutableThreadContextStack getNonNullStackCopy() {
        // 获取当前线程的堆栈副本，如果堆栈为空则创建一个新的。
        // 返回值:
        //   MutableThreadContextStack: 当前线程堆栈的副本。
        final MutableThreadContextStack values = STACK.get();
        // 从 ThreadLocal 中获取当前线程的 MutableThreadContextStack 实例。
        return (MutableThreadContextStack) (values == null ? new MutableThreadContextStack() : values.copy());
        // 如果 values 为 null，则创建一个新的 MutableThreadContextStack 实例。
        // 否则，返回当前堆栈的一个深拷贝，以实现写时复制。
    }

    @Override
    public boolean add(final String s) {
        // 将一个字符串添加到堆栈顶部。
        // 参数:
        //   s: 要添加到堆栈的字符串。
        // 返回值:
        //   boolean: 如果成功添加则返回 true，如果 useStack 为 false 则返回 false。
        if (!useStack) {
            // 检查是否启用了堆栈功能。
            return false;
        }
        final MutableThreadContextStack copy = getNonNullStackCopy();
        // 获取当前堆栈的副本。这是写时复制的关键一步，确保修改操作不影响其他线程。
        copy.add(s);
        // 将字符串添加到副本中。
        copy.freeze();
        // 冻结副本，使其变为不可修改。
        STACK.set(copy);
        // 将修改后的副本设置回 ThreadLocal，替换旧的堆栈。
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends String> strings) {
        // 将一个字符串集合添加到堆栈顶部。
        // 参数:
        //   strings: 要添加到堆栈的字符串集合。
        // 返回值:
        //   boolean: 如果成功添加则返回 true，如果 useStack 为 false 或集合为空则返回 false。
        if (!useStack || strings.isEmpty()) {
            // 检查是否启用了堆栈功能或集合是否为空。
            return false;
        }
        final MutableThreadContextStack copy = getNonNullStackCopy();
        // 获取当前堆栈的副本。
        copy.addAll(strings);
        // 将集合中的所有字符串添加到副本中。
        copy.freeze();
        // 冻结副本。
        STACK.set(copy);
        // 将修改后的副本设置回 ThreadLocal。
        return true;
    }

    @Override
    public List<String> asList() {
        // 将当前堆栈内容作为不可修改的列表返回。
        // 返回值:
        //   List<String>: 堆栈内容的列表。如果堆栈为空，则返回空列表。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null) {
            // 如果堆栈为空，返回一个空的不可修改列表。
            return Collections.emptyList();
        }
        return values.asList();
        // 返回堆栈内容的列表表示。
    }

    @Override
    public void clear() {
        // 清除当前线程的堆栈内容。
        STACK.remove();
        // 从 ThreadLocal 中移除当前线程的堆栈实例， effectively clears it.
    }

    @Override
    public boolean contains(final Object o) {
        // 检查堆栈是否包含指定的对象。
        // 参数:
        //   o: 要检查的对象。
        // 返回值:
        //   boolean: 如果堆栈包含该对象则返回 true，否则返回 false。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        return values != null && values.contains(o);
        // 如果堆栈不为空且包含指定对象，则返回 true。
    }

    @Override
    public boolean containsAll(final Collection<?> objects) {
        // 检查堆栈是否包含指定集合中的所有对象。
        // 参数:
        //   objects: 要检查的集合。
        // 返回值:
        //   boolean: 如果堆栈包含集合中的所有对象则返回 true，否则返回 false。
        if (objects.isEmpty()) { // quick check before accessing the ThreadLocal
            // 在访问 ThreadLocal 之前快速检查，如果集合为空，则根据 List 接口的约定，认为包含所有元素。
            return true; // looks counter-intuitive, but see
                         // j.u.AbstractCollection
        }
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        return values != null && values.containsAll(objects);
        // 如果堆栈不为空且包含集合中的所有对象，则返回 true。
    }

    @Override
    public ThreadContextStack copy() {
        // 返回当前堆栈的一个独立副本。
        // 返回值:
        //   ThreadContextStack: 当前堆栈的副本。
        MutableThreadContextStack values = null;
        if (!useStack || (values = STACK.get()) == null) {
            // 如果未启用堆栈或当前堆栈为空，则返回一个新的空 MutableThreadContextStack 实例。
            return new MutableThreadContextStack();
        }
        return values.copy();
        // 返回当前堆栈的深拷贝。
    }

    @Override
    public boolean equals(final Object obj) {
        // 比较当前堆栈与另一个对象是否相等。
        // 参数:
        //   obj: 要比较的对象。
        // 返回值:
        //   boolean: 如果相等则返回 true，否则返回 false。
        if (this == obj) {
            // 如果是同一个对象引用，则直接返回 true。
            return true;
        }
        if (obj == null) {
            // 如果比较对象为 null，则返回 false。
            return false;
        }
        if (obj instanceof DefaultThreadContextStack) {
            // 如果比较对象是 DefaultThreadContextStack 类型，则额外比较 useStack 字段。
            final DefaultThreadContextStack other = (DefaultThreadContextStack) obj;
            if (this.useStack != other.useStack) {
                // 如果 useStack 属性不同，则认为不相等。
                return false;
            }
        }
        if (!(obj instanceof ThreadContextStack)) {
            // 如果比较对象不是 ThreadContextStack 的实例，则返回 false。
            return false;
        }
        final ThreadContextStack other = (ThreadContextStack) obj;
        // 将比较对象转换为 ThreadContextStack 类型。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null) {
            // 如果当前堆栈为 null，则认为不相等。
            return false;
        }
        return values.equals(other);
        // 比较内部的 MutableThreadContextStack 实例与另一个 ThreadContextStack 是否相等。
    }

    @Override
    public int getDepth() {
        // 获取堆栈的当前深度（元素数量）。
        // 返回值:
        //   int: 堆栈的深度。如果堆栈为空，则返回 0。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        return values == null ? 0 : values.getDepth();
        // 如果堆栈为 null，返回 0，否则返回堆栈的深度。
    }

    @Override
    public int hashCode() {
        // 计算堆栈的哈希码。
        // 返回值:
        //   int: 堆栈的哈希码。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        final int prime = 31;
        // 用于哈希计算的质数。
        int result = 1;
        // 初始化哈希码。
        // Factor in the stack itself to compare vs. other implementors.
        // 将堆栈本身考虑在内以与其他实现者进行比较。
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        // 将内部堆栈的哈希码加入计算。
        return result;
    }

    @Override
    public boolean isEmpty() {
        // 检查堆栈是否为空。
        // 返回值:
        //   boolean: 如果堆栈为空则返回 true，否则返回 false。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        return values == null || values.isEmpty();
        // 如果堆栈为 null 或堆栈中没有元素，则返回 true。
    }

    @Override
    public Iterator<String> iterator() {
        // 返回一个迭代器，用于遍历堆栈中的元素。
        // 返回值:
        //   Iterator<String>: 堆栈元素的迭代器。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null) {
            // 如果堆栈为 null，返回一个空的列表迭代器。
            final List<String> empty = Collections.emptyList();
            return empty.iterator();
        }
        return values.iterator();
        // 返回堆栈的迭代器。
    }

    @Override
    public String peek() {
        // 查看堆栈顶部的元素，但不将其移除。
        // 返回值:
        //   String: 堆栈顶部的元素。如果堆栈为空，则返回空字符串。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null || values.isEmpty()) {
            // 如果堆栈为 null 或为空，返回空字符串。
            return Strings.EMPTY;
        }
        return values.peek();
        // 返回堆栈顶部的元素。
    }

    @Override
    public String pop() {
        // 从堆栈顶部移除并返回元素。
        // 返回值:
        //   String: 堆栈顶部的元素。如果堆栈为空，则返回空字符串。
        if (!useStack) {
            // 检查是否启用了堆栈功能。
            return Strings.EMPTY;
        }
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null || values.isEmpty()) {
            // Like version 1.2
            // 如果堆栈为 null 或为空，返回空字符串（行为与 1.2 版本一致）。
            return Strings.EMPTY;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        // 获取当前堆栈的副本。
        final String result = copy.pop();
        // 从副本中弹出元素。
        copy.freeze();
        // 冻结副本。
        STACK.set(copy);
        // 将修改后的副本设置回 ThreadLocal。
        return result;
    }

    @Override
    public void push(final String message) {
        // 将一个字符串推入堆栈顶部。
        // 参数:
        //   message: 要推入堆栈的字符串。
        if (!useStack) {
            // 检查是否启用了堆栈功能。
            return;
        }
        add(message);
        // 调用 add 方法将消息添加到堆栈。
    }

    @Override
    public boolean remove(final Object o) {
        // 从堆栈中移除指定对象的第一个匹配项。
        // 参数:
        //   o: 要移除的对象。
        // 返回值:
        //   boolean: 如果成功移除则返回 true，否则返回 false。
        if (!useStack) {
            // 检查是否启用了堆栈功能。
            return false;
        }
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null || values.isEmpty()) {
            // 如果堆栈为 null 或为空，则无法移除，返回 false。
            return false;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        // 获取当前堆栈的副本。
        final boolean result = copy.remove(o);
        // 从副本中移除指定对象。
        copy.freeze();
        // 冻结副本。
        STACK.set(copy);
        // 将修改后的副本设置回 ThreadLocal。
        return result;
    }

    @Override
    public boolean removeAll(final Collection<?> objects) {
        // 移除堆栈中与指定集合中所有元素匹配的项。
        // 参数:
        //   objects: 包含要移除元素的集合。
        // 返回值:
        //   boolean: 如果堆栈因调用而改变则返回 true，否则返回 false。
        if (!useStack || objects.isEmpty()) {
            // 检查是否启用了堆栈功能或集合是否为空。
            return false;
        }
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null || values.isEmpty()) {
            // 如果堆栈为 null 或为空，则无法移除，返回 false。
            return false;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        // 获取当前堆栈的副本。
        final boolean result = copy.removeAll(objects);
        // 从副本中移除所有匹配的元素。
        copy.freeze();
        // 冻结副本。
        STACK.set(copy);
        // 将修改后的副本设置回 ThreadLocal。
        return result;
    }

    @Override
    public boolean retainAll(final Collection<?> objects) {
        // 仅保留堆栈中与指定集合中所有元素匹配的项，移除其他所有项。
        // 参数:
        //   objects: 包含要保留元素的集合。
        // 返回值:
        //   boolean: 如果堆栈因调用而改变则返回 true，否则返回 false。
        if (!useStack || objects.isEmpty()) {
            // 检查是否启用了堆栈功能或集合是否为空。
            return false;
        }
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null || values.isEmpty()) {
            // 如果堆栈为 null 或为空，则无法保留，返回 false。
            return false;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        // 获取当前堆栈的副本。
        final boolean result = copy.retainAll(objects);
        // 在副本中保留所有匹配的元素，移除不匹配的。
        copy.freeze();
        // 冻结副本。
        STACK.set(copy);
        // 将修改后的副本设置回 ThreadLocal。
        return result;
    }

    @Override
    public int size() {
        // 获取堆栈中的元素数量。
        // 返回值:
        //   int: 堆栈中的元素数量。如果堆栈为空，则返回 0。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        return values == null ? 0 : values.size();
        // 如果堆栈为 null，返回 0，否则返回堆栈的大小。
    }

    @Override
    public Object[] toArray() {
        // 将堆栈内容转换为一个 Object 数组。
        // 返回值:
        //   Object[]: 包含堆栈所有元素的数组。
        final MutableThreadContextStack result = STACK.get();
        // 获取当前线程的堆栈实例。
        if (result == null) {
            // 如果堆栈为 null，返回一个空的字符串数组。
            return Strings.EMPTY_ARRAY;
        }
        return result.toArray(EMPTY_OBJECT_ARRAY);
        // 将堆栈内容转换为数组。
    }

    @Override
    public <T> T[] toArray(final T[] ts) {
        // 将堆栈内容转换为指定类型的数组。
        // 参数:
        //   ts: 要填充的数组。如果数组不够大，则会创建一个新数组。
        // 返回值:
        //   T[]: 包含堆栈所有元素的数组。
        final MutableThreadContextStack result = STACK.get();
        // 获取当前线程的堆栈实例。
        if (result == null) {
            // 如果堆栈为 null。
            if (ts.length > 0) { // as per the contract of j.u.List#toArray(T[])
                // 根据 j.u.List#toArray(T[]) 的约定，如果传入的数组长度大于 0，将第一个元素设置为 null。
                ts[0] = null;
            }
            return ts;
        }
        return result.toArray(ts);
        // 将堆栈内容转换为指定类型的数组。
    }

    @Override
    public String toString() {
        // 返回堆栈内容的字符串表示。
        // 返回值:
        //   String: 堆栈内容的字符串表示。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        return values == null ? "[]" : values.toString();
        // 如果堆栈为 null，返回 "[]"，否则返回堆栈的字符串表示。
    }

    @Override
    public void formatTo(final StringBuilder buffer) {
        // 将堆栈内容格式化并追加到给定的 StringBuilder 中。
        // 参数:
        //   buffer: 要追加堆栈内容的 StringBuilder。
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null) {
            // 如果堆栈为 null，则追加 "[]"。
            buffer.append("[]");
        } else {
            StringBuilders.appendValue(buffer, values);
            // 否则，使用 StringBuilders 工具类将堆栈内容追加到缓冲区。
        }
    }

    @Override
    public void trim(final int depth) {
        // 将堆栈修剪到指定的深度。如果堆栈深度大于指定深度，则移除顶部多余的元素。
        // 参数:
        //   depth: 期望的堆栈最大深度。
        if (depth < 0) {
            // 检查深度是否为负数。
            throw new IllegalArgumentException("Maximum stack depth cannot be negative");
            // 抛出非法参数异常。
        }
        final MutableThreadContextStack values = STACK.get();
        // 获取当前线程的堆栈实例。
        if (values == null) {
            // 如果堆栈为 null，则不需要修剪。
            return;
        }
        final MutableThreadContextStack copy = (MutableThreadContextStack) values.copy();
        // 获取当前堆栈的副本。
        copy.trim(depth);
        // 修剪副本到指定深度。
        copy.freeze();
        // 冻结副本。
        STACK.set(copy);
        // 将修剪后的副本设置回 ThreadLocal。
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.ThreadContext.ContextStack#getImmutableStackOrNull()
     */
    @Override
    public ContextStack getImmutableStackOrNull() {
        // 获取当前线程上下文堆栈的不可变视图，如果堆栈为空则返回 null。
        // 返回值:
        //   ContextStack: 线程上下文堆栈的不可变视图，或 null。
        return STACK.get();
        // 直接返回 ThreadLocal 中存储的堆栈实例，该实例在每次修改后都会被新的冻结副本替换，因此是不可变的。
    }
}
