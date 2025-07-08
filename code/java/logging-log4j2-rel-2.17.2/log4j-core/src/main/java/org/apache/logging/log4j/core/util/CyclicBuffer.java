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
package org.apache.logging.log4j.core.util;

import java.lang.reflect.Array;

/**
 * A bounded buffer containing elements of type T. When the number of elements to be added will exceed the
 * size of the buffer the oldest element will be overwritten. Access to the buffer is thread safe.
 * 一个有界缓冲区，包含类型为T的元素。当要添加的元素数量超过缓冲区大小时，最旧的元素将被覆盖。
 * 对缓冲区的访问是线程安全的。
 * @param <T> The type of object stored in the buffer.
 * <T> 存储在缓冲区中的对象类型。
 */
public final class CyclicBuffer<T> {
    private final T[] ring; // 存储元素的环形数组
    private int first = 0; // 缓冲区中第一个元素的索引
    private int last = 0;  // 缓冲区中最后一个元素的下一个位置的索引
    private int numElems = 0; // 缓冲区中当前元素的数量
    private final Class<T> clazz; // 缓冲区中存储的对象类型

    /**
     * Instantiates a new CyclicBuffer of at most <code>maxSize</code> events.
     * 实例化一个最多包含 <code>maxSize</code> 个事件的新 CyclicBuffer。
     * @param clazz The Class associate with the type of object in the buffer.
     * 与缓冲区中对象类型关联的类。
     * @param size The number of items in the buffer.
     * 缓冲区中的项数（最大容量）。
     * @throws IllegalArgumentException if the size is negative.
     * 如果大小为负数，则抛出 IllegalArgumentException 异常。
     */
    public CyclicBuffer(final Class<T> clazz, final int size) throws IllegalArgumentException {
        if (size < 0) {
            // 特殊处理的注意事项：确保缓冲区大小不能为负数，否则抛出异常。
            throw new IllegalArgumentException("The maxSize argument (" + size + ") cannot be negative.");
        }
        this.ring = makeArray(clazz, size); // 初始化环形数组
        this.clazz = clazz; // 存储类类型，用于后续创建新数组
    }

    @SuppressWarnings("unchecked")
    private T[] makeArray(final Class<T> cls, final int size) {
        // 方法的目的说明：创建一个指定类型和大小的泛型数组。
        return (T[]) Array.newInstance(cls, size);
    }

    /**
     * Adds an item as the last event in the buffer.
     * 将一个项作为缓冲区的最后一个事件添加。
     * @param item The item to add to the buffer.
     * 要添加到缓冲区中的项。
     */
    public synchronized void add(final T item) {
        // 事件处理逻辑的说明：向环形缓冲区添加新元素。
        if (ring.length > 0) { // 检查缓冲区容量是否大于0
            ring[last] = item; // 将新元素放入当前last指向的位置
            if (++last == ring.length) { // 移动last指针，如果到达数组末尾则循环到开头
                last = 0;
            }

            if (numElems < ring.length) { // 如果当前元素数量小于缓冲区容量，则增加元素数量
                numElems++;
            } else if (++first == ring.length) { // 如果缓冲区已满，则移动first指针，覆盖最旧的元素
                first = 0;
            }
        }
    }

    /**
     * Removes all the elements from the buffer and returns them.
     * 从缓冲区中移除所有元素并返回它们。
     * @return An array of the elements in the buffer.
     * 缓冲区中元素的数组。
     */
    public synchronized T[] removeAll() {
        // 事件处理逻辑的说明：清空缓冲区并返回所有元素。
        final T[] array = makeArray(clazz, numElems); // 创建一个与当前元素数量相同的新数组来存储结果
        int index = 0; // 结果数组的索引
        while (numElems > 0) { // 循环直到缓冲区清空
            numElems--; // 减少元素数量
            array[index++] = ring[first]; // 将first指向的元素添加到结果数组中
            ring[first] = null; // 将原位置的元素置为null，帮助垃圾回收
            if (++first == ring.length) { // 移动first指针，如果到达数组末尾则循环到开头
                first = 0;
            }
        }
        return array; // 返回包含所有元素的数组
    }

    /**
     * Determines if the buffer contains elements.
     * 判断缓冲区是否包含元素。
     * @return true if the buffer is empty, false otherwise.
     * 如果缓冲区为空则返回 true，否则返回 false。
     */
    public boolean isEmpty() {
        // 关键变量和函数的用途说明：检查numElems是否为0来判断缓冲区是否为空。
        return 0 == numElems;
    }
}
