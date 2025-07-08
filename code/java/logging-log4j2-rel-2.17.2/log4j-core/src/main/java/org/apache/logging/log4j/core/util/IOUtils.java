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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Copied from Apache Commons IO revision 1686747.
 * 从 Apache Commons IO revision 1686747 复制而来。
 */
public class IOUtils {

    /**
     * The default buffer size ({@value}) to use for
     * {@link #copyLarge(InputStream, OutputStream)}
     * and
     * {@link #copyLarge(Reader, Writer)}
     * 默认缓冲区大小({@value})，用于 {@link #copyLarge(InputStream, OutputStream)}
     * 和 {@link #copyLarge(Reader, Writer)} 方法。
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Represents the end-of-file (or stream).
     * 表示文件（或流）的末尾。
     */
    public static final int EOF = -1;

    /**
     * Copies chars from a <code>Reader</code> to a <code>Writer</code>.
     * 从一个 `Reader` 复制字符到一个 `Writer`。
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * 此方法在内部缓冲输入，因此无需使用 `BufferedReader`。
     * <p/>
     * Large streams (over 2GB) will return a chars copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the <code>copyLarge(Reader, Writer)</code> method.
     * 大型流（超过2GB）在复制完成后将返回字符复制值为 `-1`，因为正确的字符数不能作为 int 返回。
     * 对于大型流，请使用 `copyLarge(Reader, Writer)` 方法。
     *
     * @param input the <code>Reader</code> to read from
     * 要从中读取的 `Reader`。
     * @param output the <code>Writer</code> to write to
     * 要写入的 `Writer`。
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * 复制的字符数，如果大于 `Integer.MAX_VALUE` 则返回 -1。
     * @throws NullPointerException if the input or output is null
     * 如果输入或输出为 null，则抛出此异常。
     * @throws IOException          if an I/O error occurs
     * 如果发生 I/O 错误，则抛出此异常。
     * @since 1.1
     */
    public static int copy(final Reader input, final Writer output) throws IOException {
        // 调用 copyLarge 方法进行大文件复制，并获取复制的字符数
        final long count = copyLarge(input, output);
        // 如果复制的字符数超过了 Integer.MAX_VALUE，则返回 -1
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        // 将复制的字符数转换为 int 类型并返回
        return (int) count;
    }

    /**
     * Copies chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * 从一个大型（超过2GB）的 `Reader` 复制字符到一个 `Writer`。
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * 此方法在内部缓冲输入，因此无需使用 `BufferedReader`。
     * <p/>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * 缓冲区大小由 {@link #DEFAULT_BUFFER_SIZE} 指定。
     *
     * @param input the <code>Reader</code> to read from
     * 要从中读取的 `Reader`。
     * @param output the <code>Writer</code> to write to
     * 要写入的 `Writer`。
     * @return the number of characters copied
     * 复制的字符数。
     * @throws NullPointerException if the input or output is null
     * 如果输入或输出为 null，则抛出此异常。
     * @throws IOException          if an I/O error occurs
     * 如果发生 I/O 错误，则抛出此异常。
     * @since 1.3
     */
    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        // 调用重载的 copyLarge 方法，并使用默认缓冲区大小创建一个新的字符数组作为缓冲区
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copies chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * 从一个大型（超过2GB）的 `Reader` 复制字符到一个 `Writer`。
     * <p/>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * 此方法使用提供的缓冲区，因此无需使用 `BufferedReader`。
     * <p/>
     *
     * @param input the <code>Reader</code> to read from
     * 要从中读取的 `Reader`。
     * @param output the <code>Writer</code> to write to
     * 要写入的 `Writer`。
     * @param buffer the buffer to be used for the copy
     * 用于复制的缓冲区。
     * @return the number of characters copied
     * 复制的字符数。
     * @throws NullPointerException if the input or output is null
     * 如果输入或输出为 null，则抛出此异常。
     * @throws IOException          if an I/O error occurs
     * 如果发生 I/O 错误，则抛出此异常。
     * @since 2.2
     */
    public static long copyLarge(final Reader input, final Writer output, final char[] buffer) throws IOException {
        // 初始化复制的字符总数
        long count = 0;
        // 存储每次读取的字符数
        int n;
        // 循环读取输入流直到文件结束（EOF）
        while (EOF != (n = input.read(buffer))) {
            // 将读取到的字符写入输出流
            output.write(buffer, 0, n);
            // 累加复制的字符数
            count += n;
        }
        // 返回复制的字符总数
        return count;
    }

    /**
     * Gets the contents of a <code>Reader</code> as a String.
     * 将 `Reader` 的内容获取为 String。
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * 此方法在内部缓冲输入，因此无需使用 `BufferedReader`。
     *
     * @param input the <code>Reader</code> to read from
     * 要从中读取的 `Reader`。
     * @return the requested String
     * 返回请求的 String。
     * @throws NullPointerException if the input is null
     * 如果输入为 null，则抛出此异常。
     * @throws IOException          if an I/O error occurs
     * 如果发生 I/O 错误，则抛出此异常。
     */
    public static String toString(final Reader input) throws IOException {
        // 创建一个 StringBuilderWriter，用于将字符写入内部的 StringBuilder
        final StringBuilderWriter sw = new StringBuilderWriter();
        // 将输入流的内容复制到 StringBuilderWriter
        copy(input, sw);
        // 返回 StringBuilderWriter 中存储的字符串内容
        return sw.toString();
    }

}
