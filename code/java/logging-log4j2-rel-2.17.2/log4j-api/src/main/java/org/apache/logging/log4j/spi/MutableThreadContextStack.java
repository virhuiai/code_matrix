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
// 中文注释：
// 本代码文件遵循 Apache 2.0 许可证发布，由 Apache 软件基金会授权。
// 代码以“现状”提供，不附带任何明示或暗示的保证，具体权限和限制请参阅许可证。

package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/**
 * TODO
 */
// 中文注释：
// 类名：MutableThreadContextStack
// 主要功能：实现了一个可变的线程上下文栈，用于存储和管理线程上下文中的字符串数据。
// 实现接口：ThreadContextStack（提供线程上下文栈操作接口）、StringBuilderFormattable（支持将栈内容格式化为字符串）。
// 用途：用于 Log4j 日志框架中管理线程上下文信息，例如跟踪调用栈或上下文数据。
// 注意事项：栈支持冻结（freeze）操作，冻结后不可修改，任何修改操作会抛出异常。

public class MutableThreadContextStack implements ThreadContextStack, StringBuilderFormattable {

    private static final long serialVersionUID = 50505011L;
    // 中文注释：
    // serialVersionUID：用于序列化版本控制，确保类在序列化和反序列化时版本兼容。

    /**
     * The underlying list (never null).
     */
    private final List<String> list;
    // 中文注释：
    // 变量：list
    // 用途：存储上下文栈中的字符串元素，基于 ArrayList 实现，永远不为 null。
    // 注意事项：所有栈操作（如 push、pop 等）都基于此列表进行。

    private boolean frozen;
    // 中文注释：
    // 变量：frozen
    // 用途：标记栈是否被冻结。冻结后，栈不可修改，任何修改操作会抛出 UnsupportedOperationException。
    // 默认值：false（未冻结）。

    /**
     * Constructs an empty MutableThreadContextStack.
     */
    public MutableThreadContextStack() {
        this(new ArrayList<String>());
    }
    // 中文注释：
    // 方法：MutableThreadContextStack()
    // 功能：默认构造函数，创建空的 MutableThreadContextStack 实例。
    // 执行流程：
    //   1. 调用带 List 参数的构造函数，传入一个空的 ArrayList。
    // 注意事项：初始化时栈未冻结，可进行修改操作。

    /**
     * Constructs a new instance.
     * @param list
     */
    public MutableThreadContextStack(final List<String> list) {
        this.list = new ArrayList<>(list);
    }
    // 中文注释：
    // 方法：MutableThreadContextStack(List<String> list)
    // 功能：通过给定的字符串列表创建 MutableThreadContextStack 实例。
    // 参数：
    //   - list：输入的字符串列表，用于初始化栈内容。
    // 执行流程：
    //   1. 创建一个新的 ArrayList，复制输入的 list 内容。
    // 注意事项：输入的 list 被复制以避免外部修改影响栈内容。

    private MutableThreadContextStack(final MutableThreadContextStack stack) {
        this.list = new ArrayList<>(stack.list);
    }
    // 中文注释：
    // 方法：MutableThreadContextStack(MutableThreadContextStack stack)
    // 功能：复制构造函数，创建当前栈的副本。
    // 参数：
    //   - stack：要复制的 MutableThreadContextStack 实例。
    // 执行流程：
    //   1. 创建一个新的 ArrayList，复制输入栈的 list 内容。
    // 注意事项：用于实现 copy() 方法，确保返回的栈是独立的副本。

    private void checkInvariants() {
        if (frozen) {
            throw new UnsupportedOperationException("context stack has been frozen");
        }
    }
    // 中文注释：
    // 方法：checkInvariants()
    // 功能：检查栈是否被冻结，若冻结则抛出异常。
    // 执行流程：
    //   1. 检查 frozen 标志。
    //   2. 如果 frozen 为 true，抛出 UnsupportedOperationException。
    // 注意事项：所有修改操作（如 push、pop、clear 等）调用前都会执行此检查。

    @Override
    public String pop() {
        checkInvariants();
        if (list.isEmpty()) {
            return null;
        }
        final int last = list.size() - 1;
        final String result = list.remove(last);
        return result;
    }
    // 中文注释：
    // 方法：pop()
    // 功能：从栈顶移除并返回一个元素（出栈操作）。
    // 返回值：栈顶的字符串元素，若栈为空则返回 null。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 若栈为空，返回 null。
    //   3. 获取栈顶元素索引（list.size() - 1）。
    //   4. 移除并返回栈顶元素。
    // 注意事项：栈为空时不会抛出异常，仅返回 null。

    @Override
    public String peek() {
        if (list.isEmpty()) {
            return null;
        }
        final int last = list.size() - 1;
        return list.get(last);
    }
    // 中文注释：
    // 方法：peek()
    // 功能：查看栈顶元素但不移除（查看操作）。
    // 返回值：栈顶的字符串元素，若栈为空则返回 null。
    // 执行流程：
    //   1. 若栈为空，返回 null。
    //   2. 获取栈顶元素索引（list.size() - 1）。
    //   3. 返回栈顶元素。
    // 注意事项：此方法不会修改栈内容，也无需检查冻结状态。

    @Override
    public void push(final String message) {
        checkInvariants();
        list.add(message);
    }
    // 中文注释：
    // 方法：push(String message)
    // 功能：将指定字符串元素压入栈顶（入栈操作）。
    // 参数：
    //   - message：要压入栈的字符串。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 将 message 添加到 list 末尾。
    // 注意事项：栈大小会增加，需确保栈未冻结。

    @Override
    public int getDepth() {
        return list.size();
    }
    // 中文注释：
    // 方法：getDepth()
    // 功能：返回栈的深度（元素数量）。
    // 返回值：栈中元素的数量（整数）。
    // 执行流程：
    //   1. 返回 list.size()。
    // 注意事项：此方法不修改栈内容，仅返回当前栈大小。

    @Override
    public List<String> asList() {
        return list;
    }
    // 中文注释：
    // 方法：asList()
    // 功能：返回栈内容的只读列表视图。
    // 返回值：包含栈中所有元素的 List<String>。
    // 注意事项：返回的列表是底层 list 的引用，外部应避免直接修改。

    @Override
    public void trim(final int depth) {
        checkInvariants();
        if (depth < 0) {
            throw new IllegalArgumentException("Maximum stack depth cannot be negative");
        }
        if (list == null) {
            return;
        }
        final List<String> copy = new ArrayList<>(list.size());
        final int count = Math.min(depth, list.size());
        for (int i = 0; i < count; i++) {
            copy.add(list.get(i));
        }
        list.clear();
        list.addAll(copy);
    }
    // 中文注释：
    // 方法：trim(int depth)
    // 功能：修剪栈，使其深度不超过指定值。
    // 参数：
    //   - depth：目标栈深度（非负整数）。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 检查 depth 是否为负数，若是则抛出 IllegalArgumentException。
    //   3. 若 list 为 null，直接返回。
    //   4. 创建一个新的 ArrayList，复制前 depth 个元素（或全部元素，如果 depth 大于栈大小）。
    //   5. 清空原 list，并添加复制的元素。
    // 注意事项：此方法会修改栈内容，保留栈底的前 depth 个元素。

    @Override
    public ThreadContextStack copy() {
        return new MutableThreadContextStack(this);
    }
    // 中文注释：
    // 方法：copy()
    // 功能：创建当前栈的深拷贝。
    // 返回值：一个新的 ThreadContextStack 实例，包含相同的元素。
    // 执行流程：
    //   1. 调用复制构造函数，创建新的 MutableThreadContextStack 实例。
    // 注意事项：返回的副本是独立的，修改副本不会影响原栈。

    @Override
    public void clear() {
        checkInvariants();
        list.clear();
    }
    // 中文注释：
    // 方法：clear()
    // 功能：清空栈中的所有元素。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 调用 list.clear() 清空列表。
    // 注意事项：清空后栈为空，需确保栈未冻结。

    @Override
    public int size() {
        return list.size();
    }
    // 中文注释：
    // 方法：size()
    // 功能：返回栈中的元素数量。
    // 返回值：栈中元素的数量（整数）。
    // 执行流程：
    //   1. 返回 list.size()。
    // 注意事项：与 getDepth() 功能相同。

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
    // 中文注释：
    // 方法：isEmpty()
    // 功能：检查栈是否为空。
    // 返回值：若栈为空返回 true，否则返回 false。
    // 执行流程：
    //   1. 返回 list.isEmpty() 的结果。
    // 注意事项：此方法不修改栈内容。

    @Override
    public boolean contains(final Object o) {
        return list.contains(o);
    }
    // 中文注释：
    // 方法：contains(Object o)
    // 功能：检查栈中是否包含指定对象。
    // 参数：
    //   - o：要检查的对象。
    // 返回值：若栈中包含指定对象返回 true，否则返回 false。
    // 执行流程：
    //   1. 调用 list.contains(o) 检查对象是否存在。
    // 注意事项：依赖于 list 的 contains 方法，通常用于检查字符串元素。

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }
    // 中文注释：
    // 方法：iterator()
    // 功能：返回栈内容的迭代器。
    // 返回值：Iterator<String>，用于遍历栈中的字符串元素。
    // 执行流程：
    //   1. 返回 list.iterator()。
    // 注意事项：迭代器是 list 的迭代器，外部应避免通过迭代器修改栈内容。

    @Override
    public Object[] toArray() {
        return list.toArray();
    }
    // 中文注释：
    // 方法：toArray()
    // 功能：将栈内容转换为对象数组。
    // 返回值：包含栈中所有元素的 Object 数组。
    // 执行流程：
    //   1. 调用 list.toArray()。
    // 注意事项：返回的数组是独立副本，修改数组不影响栈。

    @Override
    public <T> T[] toArray(final T[] ts) {
        return list.toArray(ts);
    }
    // 中文注释：
    // 方法：toArray(T[] ts)
    // 功能：将栈内容转换为指定类型的数组。
    // 参数：
    //   - ts：目标数组，用于存储栈元素。
    // 返回值：包含栈元素的指定类型数组。
    // 执行流程：
    //   1. 调用 list.toArray(ts)。
    // 注意事项：若 ts 数组长度不足，会创建新数组。

    @Override
    public boolean add(final String s) {
        checkInvariants();
        return list.add(s);
    }
    // 中文注释：
    // 方法：add(String s)
    // 功能：将字符串添加到栈顶（与 push 类似）。
    // 参数：
    //   - s：要添加的字符串。
    // 返回值：始终返回 true（ArrayList.add 总是成功）。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 调用 list.add(s) 添加字符串。
    // 注意事项：与 push 功能相同，需确保栈未冻结。

    @Override
    public boolean remove(final Object o) {
        checkInvariants();
        return list.remove(o);
    }
    // 中文注释：
    // 方法：remove(Object o)
    // 功能：移除栈中第一次出现的指定对象。
    // 参数：
    //   - o：要移除的对象。
    // 返回值：若成功移除返回 true，否则返回 false。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 调用 list.remove(o) 移除对象。
    // 注意事项：移除操作可能改变栈的顺序，需确保栈未冻结。

    @Override
    public boolean containsAll(final Collection<?> objects) {
        return list.containsAll(objects);
    }
    // 中文注释：
    // 方法：containsAll(Collection<?> objects)
    // 功能：检查栈是否包含指定集合中的所有元素。
    // 参数：
    //   - objects：要检查的元素集合。
    // 返回值：若栈包含所有指定元素返回 true，否则返回 false。
    // 执行流程：
    //   1. 调用 list.containsAll(objects)。
    // 注意事项：不修改栈内容，仅进行查询。

    @Override
    public boolean addAll(final Collection<? extends String> strings) {
        checkInvariants();
        return list.addAll(strings);
    }
    // 中文注释：
    // 方法：addAll(Collection<? extends String> strings)
    // 功能：将指定集合中的所有字符串添加到栈中。
    // 参数：
    //   - strings：要添加的字符串集合。
    // 返回值：若栈内容发生改变返回 true，否则返回 false。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 调用 list.addAll(strings) 添加集合中的元素。
    // 注意事项：元素按集合迭代顺序添加到栈顶，需确保栈未冻结。

    @Override
    public boolean removeAll(final Collection<?> objects) {
        checkInvariants();
        return list.removeAll(objects);
    }
    // 中文注释：
    // 方法：removeAll(Collection<?> objects)
    // 功能：移除栈中包含在指定集合中的所有元素。
    // 参数：
    //   - objects：要移除的元素集合。
    // 返回值：若栈内容发生改变返回 true，否则返回 false。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 调用 list.removeAll(objects) 移除指定元素。
    // 注意事项：可能改变栈的顺序，需确保栈未冻结。

    @Override
    public boolean retainAll(final Collection<?> objects) {
        checkInvariants();
        return list.retainAll(objects);
    }
    // 中文注释：
    // 方法：retainAll(Collection<?> objects)
    // 功能：保留栈中包含在指定集合中的元素，移除其他元素。
    // 参数：
    //   - objects：要保留的元素集合。
    // 返回值：若栈内容发生改变返回 true，否则返回 false。
    // 执行流程：
    //   1. 调用 checkInvariants() 检查栈是否可修改。
    //   2. 调用 list.retainAll(objects) 保留指定元素。
    // 注意事项：可能改变栈的顺序，需确保栈未冻结。

    @Override
    public String toString() {
        return String.valueOf(list);
    }
    // 中文注释：
    // 方法：toString()
    // 功能：返回栈内容的字符串表示。
    // 返回值：栈内容的字符串形式，通常为列表的字符串表示。
    // 执行流程：
    //   1. 调用 list.toString()。
    // 注意事项：用于调试或日志输出，不修改栈内容。

    @Override
    public void formatTo(final StringBuilder buffer) {
        buffer.append('[');
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                buffer.append(',').append(' ');
            }
            buffer.append(list.get(i));
        }
        buffer.append(']');
    }
    // 中文注释：
    // 方法：formatTo(StringBuilder buffer)
    // 功能：将栈内容格式化为字符串，追加到指定的 StringBuilder 中。
    // 参数：
    //   - buffer：目标 StringBuilder，用于存储格式化结果。
    // 执行流程：
    //   1. 追加 '[' 到 buffer。
    //   2. 遍历 list 中的元素，每个元素后追加逗号和空格（第一个元素除外）。
    //   3. 追加 ']' 到 buffer。
    // 注意事项：实现 StringBuilderFormattable 接口，用于自定义格式化输出。

    @Override
    public int hashCode() {
        return 31 + Objects.hashCode(list);
    }
    // 中文注释：
    // 方法：hashCode()
    // 功能：计算栈的哈希值。
    // 返回值：基于 list 的哈希值。
    // 执行流程：
    //   1. 使用 Objects.hashCode(list) 计算哈希值并加上常数 31。
    // 注意事项：用于对象比较和哈希表操作，确保与 equals 方法一致。

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ThreadContextStack)) {
            return false;
        }
        final ThreadContextStack other = (ThreadContextStack) obj;
        final List<String> otherAsList = other.asList();
        return Objects.equals(this.list, otherAsList);
    }
    // 中文注释：
    // 方法：equals(Object obj)
    // 功能：比较当前栈与指定对象是否相等。
    // 参数：
    //   - obj：要比较的对象。
    // 返回值：若栈内容相等返回 true，否则返回 false。
    // 执行流程：
    //   1. 检查是否为同一对象，若是返回 true。
    //   2. 检查 obj 是否为 null，若是返回 false。
    //   3. 检查 obj 是否实现 ThreadContextStack 接口，若否返回 false。
    //   4. 比较两个栈的 list 内容是否相等。
    // 注意事项：基于 list 的内容比较，忽略 frozen 状态。

    @Override
    public ContextStack getImmutableStackOrNull() {
        return copy();
    }
    // 中文注释：
    // 方法：getImmutableStackOrNull()
    // 功能：返回栈的不可变副本。
    // 返回值：ThreadContextStack 类型的栈副本。
    // 执行流程：
    //   1. 调用 copy() 方法创建栈的深拷贝。
    // 注意事项：返回的副本可独立修改，不影响原栈。

    /**
     * "Freezes" this context stack so it becomes immutable: all mutator methods will throw an exception from now on.
     */
    public void freeze() {
        frozen = true;
    }
    // 中文注释：
    // 方法：freeze()
    // 功能：冻结栈，使其不可修改。
    // 执行流程：
    //   1. 将 frozen 标志设置为 true。
    // 注意事项：冻结后，所有修改操作（如 push、pop、clear 等）都会抛出 UnsupportedOperationException。

    /**
     * Returns whether this context stack is frozen.
     * @return whether this context stack is frozen.
     */
    public boolean isFrozen() {
        return frozen;
    }
    // 中文注释：
    // 方法：isFrozen()
    // 功能：检查栈是否已被冻结。
    // 返回值：若栈已冻结返回 true，否则返回 false。
    // 执行流程：
    //   1. 返回 frozen 标志的值。
    // 注意事项：用于查询栈的冻结状态，不修改栈内容。
}
