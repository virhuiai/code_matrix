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

import java.util.Collection;
import java.util.Map;

/**
 * Utility class providing common validation logic.
 * 提供通用验证逻辑的工具类。
 */
public final class Assert {
    private Assert() {
    }

    /**
     * Checks if an object has empty semantics. The following scenarios are considered empty:
     * 检查一个对象是否为空。以下情况被认为是空的：
     * <ul>
     * <li>{@code null}</li>
     * <li>empty {@link CharSequence}</li>
     * <li>空的 {@link CharSequence}（字符序列）</li>
     * <li>empty array</li>
     * <li>空的数组</li>
     * <li>empty {@link Iterable}</li>
     * <li>空的 {@link Iterable}（可迭代对象）</li>
     * <li>empty {@link Map}</li>
     * <li>空的 {@link Map}（映射）</li>
     * </ul>
     *
     * @param o value to check for emptiness
     * 要检查空值的对象
     * @return true if the value is empty, false otherwise
     * 如果值为空则返回 true，否则返回 false
     * @since 2.8
     */
    public static boolean isEmpty(final Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof CharSequence) {
            return ((CharSequence) o).length() == 0;
        }
        if (o.getClass().isArray()) {
            return ((Object[]) o).length == 0;
        }
        if (o instanceof Collection) {
            return ((Collection<?>) o).isEmpty();
        }
        if (o instanceof Map) {
            return ((Map<?, ?>) o).isEmpty();
        }
        return false;
    }

    /**
     * Opposite of {@link #isEmpty(Object)}.
     * 与 {@link #isEmpty(Object)} 方法相反。
     *
     * @param o value to check for non-emptiness
     * 要检查非空值的对象
     * @return true if the value is non-empty, false otherwise
     * 如果值非空则返回 true，否则返回 false
     * @since 2.8
     */
    public static boolean isNonEmpty(final Object o) {
        return !isEmpty(o);
    }

    /**
     * Checks a value for emptiness and throws an IllegalArgumentException if it's empty.
     * 检查一个值是否为空，如果为空则抛出 IllegalArgumentException。
     *
     * @param value value to check for emptiness
     * 要检查空值的值
     * @param <T>   type of value
     * 值的类型
     * @return the provided value if non-empty
     * 如果值非空，则返回所提供的值
     * @since 2.8
     */
    public static <T> T requireNonEmpty(final T value) {
        return requireNonEmpty(value, "");
    }

    /**
     * Checks a value for emptiness and throws an IllegalArgumentException if it's empty.
     * 检查一个值是否为空，如果为空则抛出 IllegalArgumentException。
     *
     * @param value   value to check for emptiness
     * 要检查空值的值
     * @param message message to provide in exception
     * 异常中提供的消息
     * @param <T>     type of value
     * 值的类型
     * @return the provided value if non-empty
     * 如果值非空，则返回所提供的值
     * @since 2.8
     */
    public static <T> T requireNonEmpty(final T value, final String message) {
        if (isEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Ensures that a given integer value is at least a specified minimum value.
     * 确保给定的整数值至少达到指定的最小值。
     *
     * @param value the integer value to check
     * 要检查的整数值
     * @param minValue the minimum allowed value
     * 允许的最小值
     * @return the original value if it is greater than or equal to minValue
     * 如果原始值大于或等于 minValue，则返回原始值
     * @throws IllegalArgumentException if the value is less than minValue
     * 如果值小于 minValue，则抛出 IllegalArgumentException
     */
    public static int valueIsAtLeast(final int value, final int minValue) {
        if (value < minValue) {
            // Throws an exception with a descriptive message if the value is too low.
            // 如果值过低，则抛出带有描述性消息的异常。
            throw new IllegalArgumentException("Value should be at least " + minValue + " but was " + value);
        }
        // Returns the original value if the check passes.
        // 如果检查通过，则返回原始值。
        return value;
    }
}
