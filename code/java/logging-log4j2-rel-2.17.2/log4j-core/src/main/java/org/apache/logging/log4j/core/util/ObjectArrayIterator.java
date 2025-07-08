/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link java.util.Iterator Iterator} over an array of objects.
 * 一个用于遍历对象数组的迭代器。
 * <p>
 * This iterator does not support {@link #remove}, as the object array cannot be
 * structurally modified.
 * 这个迭代器不支持 {@link #remove} 方法，因为对象数组的结构不能被修改。
 * <p>
 * The iterator implements a {@link #reset} method, allowing the reset of the iterator
 * back to the start if required.
 * 这个迭代器实现了 {@link #reset} 方法，如果需要可以将迭代器重置到起始位置。
 *
 * @param <E> the type to iterate over
 * 要迭代的元素类型
 * @since 3.0
 * @version $Id: ObjectArrayIterator.java 1734648 2016-03-11 23:51:22Z ggregory $
 */
public class ObjectArrayIterator<E> implements /*Resettable*/ Iterator<E> {

    /** The array */
    final E[] array;
    // 要迭代的数组
    /** The start index to loop from */
    final int startIndex;
    // 迭代的起始索引
    /** The end index to loop to */
    final int endIndex;
    // 迭代的结束索引（不包含）
    /** The current iterator index */
    int index = 0;
    // 当前迭代器的索引

    //-------------------------------------------------------------------------
    /**
     * Constructs an ObjectArrayIterator that will iterate over the values in the
     * specified array.
     * 构造一个 ObjectArrayIterator，它将遍历指定数组中的所有值。
     *
     * @param array the array to iterate over
     * 要迭代的数组
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * 如果 <code>array</code> 为 <code>null</code>，则抛出 NullPointerException
     */
    @SafeVarargs
    public ObjectArrayIterator(final E... array) {
        this(array, 0, array.length);
    }

    /**
     * Constructs an ObjectArrayIterator that will iterate over the values in the
     * specified array from a specific start index.
     * 构造一个 ObjectArrayIterator，它将从指定的起始索引开始遍历指定数组中的值。
     *
     * @param array  the array to iterate over
     * 要迭代的数组
     * @param start  the index to start iterating at
     * 开始迭代的索引
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * 如果 <code>array</code> 为 <code>null</code>，则抛出 NullPointerException
     * @throws IndexOutOfBoundsException if the start index is out of bounds
     * 如果起始索引超出范围，则抛出 IndexOutOfBoundsException
     */
    public ObjectArrayIterator(final E array[], final int start) {
        this(array, start, array.length);
    }

    /**
     * Construct an ObjectArrayIterator that will iterate over a range of values
     * in the specified array.
     * 构造一个 ObjectArrayIterator，它将遍历指定数组中某个范围内的值。
     *
     * @param array  the array to iterate over
     * 要迭代的数组
     * @param start  the index to start iterating at
     * 开始迭代的索引
     * @param end  the index (exclusive) to finish iterating at
     * 结束迭代的索引（不包含）
     * @throws IndexOutOfBoundsException if the start or end index is out of bounds
     * 如果起始或结束索引超出范围，则抛出 IndexOutOfBoundsException
     * @throws IllegalArgumentException if end index is before the start
     * 如果结束索引在起始索引之前，则抛出 IllegalArgumentException
     * @throws NullPointerException if <code>array</code> is <code>null</code>
     * 如果 <code>array</code> 为 <code>null</code>，则抛出 NullPointerException
     */
    public ObjectArrayIterator(final E array[], final int start, final int end) {
        if (start < 0) {
            throw new ArrayIndexOutOfBoundsException("Start index must not be less than zero");
            // 起始索引不能小于零
        }
        if (end > array.length) {
            throw new ArrayIndexOutOfBoundsException("End index must not be greater than the array length");
            // 结束索引不能大于数组长度
        }
        if (start > array.length) {
            throw new ArrayIndexOutOfBoundsException("Start index must not be greater than the array length");
            // 起始索引不能大于数组长度
        }
        if (end < start) {
            throw new IllegalArgumentException("End index must not be less than start index");
            // 结束索引不能小于起始索引
        }
        this.array = array; // 初始化数组
        this.startIndex = start; // 初始化起始索引
        this.endIndex = end; // 初始化结束索引
        this.index = start; // 将当前索引设置为起始索引
    }

    // Iterator interface
    // 迭代器接口方法
    //-------------------------------------------------------------------------

    /**
     * Returns true if there are more elements to return from the array.
     * 如果数组中还有更多元素可以返回，则返回 true。
     *
     * @return true if there is a next element to return
     * 如果存在下一个元素可以返回，则返回 true
     */
    @Override
    public boolean hasNext() {
        return this.index < this.endIndex;
        // 判断当前索引是否小于结束索引，以此判断是否还有下一个元素
    }

    /**
     * Returns the next element in the array.
     * 返回数组中的下一个元素。
     *
     * @return the next element in the array
     * 数组中的下一个元素
     * @throws NoSuchElementException if all the elements in the array
     *    have already been returned
     * 如果数组中的所有元素都已返回，则抛出 NoSuchElementException
     */
    @Override
    public E next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
            // 如果没有下一个元素，则抛出 NoSuchElementException
        }
        return this.array[this.index++];
        // 返回当前索引处的元素，并将索引自增
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     * 抛出 {@link UnsupportedOperationException}。
     *
     * @throws UnsupportedOperationException always
     * 总是抛出 UnsupportedOperationException
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported for an ObjectArrayIterator");
        // ObjectArrayIterator 不支持 remove() 方法
    }

    // Properties
    // 属性方法
    //-------------------------------------------------------------------------

    /**
     * Gets the array that this iterator is iterating over.
     * 获取此迭代器正在遍历的数组。
     *
     * @return the array this iterator iterates over
     * 此迭代器遍历的数组
     */
    public E[] getArray() {
        return this.array;
        // 返回内部存储的数组
    }

    /**
     * Gets the start index to loop from.
     * 获取开始遍历的索引。
     *
     * @return the start index
     * 起始索引
     */
    public int getStartIndex() {
        return this.startIndex;
        // 返回起始索引
    }

    /**
     * Gets the end index to loop to.
     * 获取结束遍历的索引。
     *
     * @return the end index
     * 结束索引
     */
    public int getEndIndex() {
        return this.endIndex;
        // 返回结束索引
    }

    /**
     * Resets the iterator back to the start index.
     * 将迭代器重置回起始索引。
     */
    //@Override
    public void reset() {
        this.index = this.startIndex;
        // 将当前索引设置为起始索引，实现重置
    }

}
