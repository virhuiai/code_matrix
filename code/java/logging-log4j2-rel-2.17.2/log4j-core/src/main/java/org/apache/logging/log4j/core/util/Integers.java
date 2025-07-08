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

import org.apache.logging.log4j.util.Strings;

/**
 * Helps deal with integers.
 * 帮助处理整数相关的工具类。
 */
public final class Integers {

    private static final int BITS_PER_INT = 32;
    // 定义一个整数的位数，通常为 32 位。

    private Integers() {
    }
    // 私有构造函数，防止外部实例化，因为这是一个工具类。

    /**
     * Parses the string argument as a signed decimal integer.
     * 将字符串参数解析为有符号的十进制整数。
     * <p>
     * The input may be surrounded by whitespace.
     * 输入字符串可能包含前后空格。
     * </p>
     *
     * @param s a {@code String} containing the {@code int} representation to parse, may be {@code null} or {@code ""}
     * 一个包含要解析的整数表示的字符串，可以是 null 或空字符串。
     * @param defaultValue the return value, use {@code defaultValue} if {@code s} is {@code null} or {@code ""}
     * 默认返回值，如果字符串 {@code s} 为 null 或空，则使用此默认值。
     * @return the integer value represented by the argument in decimal.
     * 以十进制表示的整数值。
     * @throws NumberFormatException if the string does not contain a parsable integer.
     * 如果字符串不包含可解析的整数，则抛出此异常。
     */
    public static int parseInt(final String s, final int defaultValue) {
        // 检查字符串是否为空，如果为空则返回默认值，否则去除空格后解析为整数。
        return Strings.isEmpty(s) ? defaultValue : Integer.parseInt(s.trim());
    }

    /**
     * Parses the string argument as a signed decimal integer.
     * 将字符串参数解析为有符号的十进制整数。
     *
     * @param s a {@code String} containing the {@code int} representation to parse, may be {@code null} or {@code ""}
     * 一个包含要解析的整数表示的字符串，可以是 null 或空字符串。
     * @return the integer value represented by the argument in decimal.
     * 以十进制表示的整数值。
     * @throws NumberFormatException if the string does not contain a parsable integer.
     * 如果字符串不包含可解析的整数，则抛出此异常。
     */
    public static int parseInt(final String s) {
        // 调用重载的 parseInt 方法，默认值为 0。
        return parseInt(s, 0);
    }

    /**
     * Calculate the next power of 2, greater than or equal to x.
     * 计算大于或等于 x 的下一个 2 的幂。
     * <p>
     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
     * 该算法来源于《Hacker's Delight》一书的第三章，作者 Harry S. Warren Jr.。
     *
     * @param x Value to round up
     * 需要向上取整的值。
     * @return The next power of 2 from x inclusive
     * 大于或等于 x 的下一个 2 的幂。
     */
    public static int ceilingNextPowerOfTwo(final int x) {
        // 核心逻辑：
        // 1. (x - 1)：将 x 减 1，为了处理 x 本身就是 2 的幂的情况。
        // 2. Integer.numberOfLeadingZeros(x - 1)：计算 (x - 1) 的二进制表示中，从最高位开始有多少个连续的零。
        //    例如，如果 x - 1 是 7 (0...00111)，则 numberOfLeadingZeros 返回 29 (32 - 3)。
        // 3. (BITS_PER_INT - Integer.numberOfLeadingZeros(x - 1))：计算有效位数，也就是从最低位到最高有效位有多少位。
        //    例如，对于 7，有效位数是 3 (32 - 29)。
        // 4. 1 << ...：将 1 左移计算出的位数。这会得到大于或等于 x 的最小的 2 的幂。
        //    例如，对于 7，左移 3 位得到 8 (0...1000)。
        return 1 << (BITS_PER_INT - Integer.numberOfLeadingZeros(x - 1));
    }
}
