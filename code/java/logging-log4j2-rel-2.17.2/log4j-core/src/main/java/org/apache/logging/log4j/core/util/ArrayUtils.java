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
 * Copied from Apache Commons Lang (including the {@code @since} tags.)
 * 从Apache Commons Lang库复制而来（包括 @since 标签）。
 */
public class ArrayUtils {

    /**
     * Checks if an array of Objects is empty or {@code null}.
     * 检查一个对象数组是否为空或为 {@code null}。
     *
     * @param array  the array to test
     * 要测试的数组
     * @return {@code true} if the array is empty or {@code null}
     * 如果数组为空或为 {@code null}，则返回 {@code true}
     * @since 2.1
     */
    public static boolean isEmpty(final byte[] array) {
        return getLength(array) == 0;
    }
    
    /**
     * <p>Returns the length of the specified array.
     * <p>返回指定数组的长度。</p>
     * This method can deal with {@code Object} arrays and with primitive arrays.</p>
     * 此方法可以处理 {@code Object} 数组和基本类型数组。</p>
     *
     * <p>If the input array is {@code null}, {@code 0} is returned.</p>
     * <p>如果输入数组为 {@code null}，则返回 {@code 0}。</p>
     *
     * <pre>
     * ArrayUtils.getLength(null)            = 0
     * ArrayUtils.getLength([])              = 0
     * ArrayUtils.getLength([null])          = 1
     * ArrayUtils.getLength([true, false])   = 2
     * ArrayUtils.getLength([1, 2, 3])       = 3
     * ArrayUtils.getLength(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array  the array to retrieve the length from, may be null
     * 要获取长度的数组，可能为 null
     * @return The length of the array, or {@code 0} if the array is {@code null}
     * 数组的长度，如果数组为 {@code null} 则返回 {@code 0}
     * @throws IllegalArgumentException if the object argument is not an array.
     * 如果对象参数不是一个数组，则抛出 IllegalArgumentException 异常。
     * @since 2.1
     */
    public static int getLength(final Object array) {
        // 如果数组为 null，返回 0
        if (array == null) {
            return 0;
        }
        // 使用 Array.getLength 方法获取数组的长度
        return Array.getLength(array);
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * <p>从指定数组中移除指定位置的元素。</p>
     * All subsequent elements are shifted to the left (subtracts one from
     * their indices).</p>
     * 所有后续元素都会向左移动（它们的索引减一）。</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * <p>此方法返回一个新数组，其中包含输入数组中除指定位置元素之外的所有元素。</p>
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     * 返回数组的组件类型始终与输入数组的组件类型相同。</p>
     *
     * <p>If the input array is {@code null}, an IndexOutOfBoundsException
     * <p>如果输入数组为 {@code null}，则会抛出 IndexOutOfBoundsException 异常，</p>
     * will be thrown, because in that case no valid index can be specified.</p>
     * 因为在这种情况下无法指定有效的索引。</p>
     *
     * @param array  the array to remove the element from, may not be {@code null}
     * 要从中移除元素的数组，不能为空
     * @param index  the position of the element to be removed
     * 要移除元素的索引位置
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * 一个新数组，包含除指定位置元素之外的所有现有元素。
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index &lt; 0 || index &gt;= array.length), or if the array is {@code null}.
     * 如果索引超出范围（index &lt; 0 || index &gt;= array.length），或者数组为 {@code null}，则抛出 IndexOutOfBoundsException 异常。
     * @since 2.1
     */
    private static Object remove(final Object array, final int index) {
        // 获取数组的长度
        final int length = getLength(array);
        // 检查索引是否越界
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        // 创建一个新数组，长度比原数组少一
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        // 将原数组从开头到 index-1 的元素复制到新数组
        System.arraycopy(array, 0, result, 0, index);
        // 如果 index 不在数组的最后一个位置，则将 index+1 到末尾的元素复制到新数组，从新数组的 index 位置开始
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        // 返回新数组
        return result;
    }

    /**
     * <p>Removes the element at the specified position from the specified array.
     * <p>从指定数组中移除指定位置的元素。</p>
     * All subsequent elements are shifted to the left (subtracts one from
     * their indices).</p>
     * 所有后续元素都会向左移动（它们的索引减一）。</p>
     *
     * <p>This method returns a new array with the same elements of the input
     * <p>此方法返回一个新数组，其中包含输入数组中除指定位置元素之外的所有元素。</p>
     * array except the element on the specified position. The component
     * type of the returned array is always the same as that of the input
     * array.</p>
     * 返回数组的组件类型始终与输入数组的组件类型相同。</p>
     *
     * <p>If the input array is {@code null}, an IndexOutOfBoundsException
     * <p>如果输入数组为 {@code null}，则会抛出 IndexOutOfBoundsException 异常，</p>
     * will be thrown, because in that case no valid index can be specified.</p>
     * 因为在这种情况下无法指定有效的索引。</p>
     *
     * <pre>
     * ArrayUtils.remove(["a"], 0)           = []
     * ArrayUtils.remove(["a", "b"], 0)      = ["b"]
     * ArrayUtils.remove(["a", "b"], 1)      = ["a"]
     * ArrayUtils.remove(["a", "b", "c"], 1) = ["a", "c"]
     * </pre>
     *
     * @param <T> the component type of the array
     * 数组的组件类型
     * @param array  the array to remove the element from, may not be {@code null}
     * 要从中移除元素的数组，不能为空
     * @param index  the position of the element to be removed
     * 要移除元素的索引位置
     * @return A new array containing the existing elements except the element
     *         at the specified position.
     * 一个新数组，包含除指定位置元素之外的所有现有元素。
     * @throws IndexOutOfBoundsException if the index is out of range
     * (index &lt; 0 || index &gt;= array.length), or if the array is {@code null}.
     * 如果索引超出范围（index &lt; 0 || index &gt;= array.length），或者数组为 {@code null}，则抛出 IndexOutOfBoundsException 异常。
     * @since 2.1
     */
    @SuppressWarnings("unchecked") // remove() always creates an array of the same type as its input
    // remove() 方法总是创建与其输入类型相同的数组，因此抑制未经检查的警告
    public static <T> T[] remove(final T[] array, final int index) {
        // 调用私有的 remove 方法，并进行类型转换
        return (T[]) remove((Object) array, index);
    }

}
