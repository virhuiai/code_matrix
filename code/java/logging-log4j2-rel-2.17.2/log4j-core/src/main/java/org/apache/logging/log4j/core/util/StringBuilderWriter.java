/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.Serializable;
import java.io.Writer;

/**
 * {@link Writer} implementation that outputs to a {@link StringBuilder}.
 * 一个将输出写入到 {@link StringBuilder} 的 {@link Writer} 实现。
 * <p>
 * <strong>NOTE:</strong> This implementation, as an alternative to
 * <code>java.io.StringWriter</code>, provides an <i>un-synchronized</i>
 * (i.e. for use in a single thread) implementation for better performance.
 * For safe usage with multiple {@link Thread}s then
 * <code>java.io.StringWriter</code> should be used.
 * <strong>注意：</strong> 作为 <code>java.io.StringWriter</code> 的替代方案，
 * 此实现提供了一个**非同步**（即用于单线程）的实现，以获得更好的性能。
 * 如果需要在多个 {@link Thread} 中安全使用，则应该使用 <code>java.io.StringWriter</code>。
 *
 * <h3>History</h3>
 * <h3>历史</h3>
 * <ol>
 * <li>Copied from Apache Commons IO revision 1681000.</li>
 * <li>从 Apache Commons IO 的版本 1681000 复制而来。</li>
 * <li>Pick up Javadoc updates from revision 1722253.</li>
 * <li>采纳了版本 1722253 的 Javadoc 更新。</li>
 * <ol>
 */
public class StringBuilderWriter extends Writer implements Serializable {

    private static final long serialVersionUID = -146927496096066153L;
    // 序列化版本 UID，用于标识类的版本。
    private final StringBuilder builder;
    // 内部使用的 StringBuilder 实例，所有写入操作都会委托给它。

    /**
     * Constructs a new {@link StringBuilder} instance with default capacity.
     * 构造一个新的 {@link StringBuilder} 实例，使用默认容量。
     */
    public StringBuilderWriter() {
        this.builder = new StringBuilder();
        // 初始化一个默认容量的 StringBuilder。
    }

    /**
     * Constructs a new {@link StringBuilder} instance with the specified capacity.
     * 构造一个新的 {@link StringBuilder} 实例，并指定其初始容量。
     *
     * @param capacity The initial capacity of the underlying {@link StringBuilder}
     * 底层 {@link StringBuilder} 的初始容量。
     */
    public StringBuilderWriter(final int capacity) {
        this.builder = new StringBuilder(capacity);
        // 初始化一个指定容量的 StringBuilder。
    }

    /**
     * Constructs a new instance with the specified {@link StringBuilder}.
     * 使用指定的 {@link StringBuilder} 构造一个新实例。
     *
     * <p>If {@code builder} is null a new instance with default capacity will be created.</p>
     * <p>如果 {@code builder} 为空，则会创建一个具有默认容量的新实例。</p>
     *
     * @param builder The String builder. May be null.
     * 字符串构建器。可以为 null。
     */
    public StringBuilderWriter(final StringBuilder builder) {
        this.builder = builder != null ? builder : new StringBuilder();
        // 如果传入的 builder 不为空，则使用它；否则创建一个新的默认容量的 StringBuilder。
    }

    /**
     * Appends a single character to this Writer.
     * 向此 Writer 附加单个字符。
     *
     * @param value The character to append
     * 要附加的字符。
     * @return This writer instance
     * 当前 Writer 实例。
     */
    @Override
    public Writer append(final char value) {
        builder.append(value);
        // 将单个字符附加到内部的 StringBuilder。
        return this;
        // 返回当前 Writer 实例，支持链式调用。
    }

    /**
     * Appends a character sequence to this Writer.
     * 向此 Writer 附加一个字符序列。
     *
     * @param value The character to append
     * 要附加的字符序列。
     * @return This writer instance
     * 当前 Writer 实例。
     */
    @Override
    public Writer append(final CharSequence value) {
        builder.append(value);
        // 将字符序列附加到内部的 StringBuilder。
        return this;
        // 返回当前 Writer 实例，支持链式调用。
    }

    /**
     * Appends a portion of a character sequence to the {@link StringBuilder}.
     * 将字符序列的一部分附加到 {@link StringBuilder}。
     *
     * @param value The character to append
     * 要附加的字符序列。
     * @param start The index of the first character
     * 第一个字符的索引（包含）。
     * @param end The index of the last character + 1
     * 最后一个字符的索引加一（不包含）。
     * @return This writer instance
     * 当前 Writer 实例。
     */
    @Override
    public Writer append(final CharSequence value, final int start, final int end) {
        builder.append(value, start, end);
        // 将字符序列的指定部分附加到内部的 StringBuilder。
        return this;
        // 返回当前 Writer 实例，支持链式调用。
    }

    /**
     * Closing this writer has no effect.
     * 关闭此 Writer 没有实际效果。
     */
    @Override
    public void close() {
        // no-op
        // 这是一个空操作，因为 StringBuilderWriter 不需要关闭任何底层资源。
    }

    /**
     * Flushing this writer has no effect.
     * 刷新此 Writer 没有实际效果。
     */
    @Override
    public void flush() {
        // no-op
        // 这是一个空操作，因为 StringBuilderWriter 没有缓冲区需要强制写入。
    }


    /**
     * Writes a String to the {@link StringBuilder}.
     * 将一个字符串写入到 {@link StringBuilder}。
     *
     * @param value The value to write
     * 要写入的字符串。
     */
    @Override
    public void write(final String value) {
        if (value != null) {
            builder.append(value);
            // 如果传入的字符串不为空，则将其附加到内部的 StringBuilder。
        }
    }

    /**
     * Writes a portion of a character array to the {@link StringBuilder}.
     * 将字符数组的一部分写入到 {@link StringBuilder}。
     *
     * @param value The value to write
     * 要写入的字符数组。
     * @param offset The index of the first character
     * 第一个字符的起始偏移量。
     * @param length The number of characters to write
     * 要写入的字符数量。
     */
    @Override
    public void write(final char[] value, final int offset, final int length) {
        if (value != null) {
            builder.append(value, offset, length);
            // 如果传入的字符数组不为空，则将其指定部分附加到内部的 StringBuilder。
        }
    }

    /**
     * Returns the underlying builder.
     * 返回底层的 StringBuilder 实例。
     *
     * @return The underlying builder
     * 底层的 StringBuilder。
     */
    public StringBuilder getBuilder() {
        return builder;
        // 提供对内部 StringBuilder 的访问，允许直接操作其内容。
    }

    /**
     * Returns {@link StringBuilder#toString()}.
     * 返回 {@link StringBuilder#toString()} 的结果。
     *
     * @return The contents of the String builder.
     * 字符串构建器的内容。
     */
    @Override
    public String toString() {
        return builder.toString();
        // 返回内部 StringBuilder 的当前内容，作为一个 String。
    }
}
