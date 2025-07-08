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
import java.io.Writer;

/**
 * A {@link Writer} that prevents the underlying {@link Writer} from being closed.
 * This class is useful when you want to provide a Writer to a component that might
 * attempt to close it, but you want to keep the underlying resource open for further use.
 * 这是一个防止底层 {@link Writer} 被关闭的 {@link Writer}。
 * 当您希望将一个 Writer 提供给可能会尝试关闭它的组件，但又希望底层资源保持打开以供后续使用时，此类非常有用。
 */
public class CloseShieldWriter extends Writer {

    // The underlying Writer that this CloseShieldWriter delegates to.
    // 这个 CloseShieldWriter 委托操作的底层 Writer。
    private final Writer delegate;

    /**
     * Constructs a new CloseShieldWriter.
     * 构造一个新的 CloseShieldWriter。
     *
     * @param delegate The Writer to wrap and protect from being closed.
     * 要包装并防止被关闭的 Writer。
     */
    public CloseShieldWriter(final Writer delegate) {
        this.delegate = delegate;
    }

    /**
     * Overrides the close method to prevent the delegate Writer from being closed.
     * This method does nothing, effectively "shielding" the delegate from close calls.
     * 重写 close 方法以防止委托的 Writer 被关闭。
     * 此方法不执行任何操作，有效地“屏蔽”了委托的关闭调用。
     *
     * @throws IOException This implementation does not throw IOException, but it's part of the overridden signature.
     * 此实现不抛出 IOException，但它是重写签名的一部分。
     */
    @Override
    public void close() throws IOException {
        // do not close delegate
        // 不关闭委托对象
    }

    /**
     * Flushes the delegate Writer.
     * This method ensures that any buffered output in the delegate is written to its destination.
     * 刷新委托的 Writer。
     * 此方法确保委托中任何缓冲的输出都被写入其目的地。
     *
     * @throws IOException if an I/O error occurs during flushing.
     * 如果在刷新过程中发生 I/O 错误。
     */
    @Override
    public void flush() throws IOException {
        delegate.flush();

    }

    /**
     * Writes a portion of an array of characters to the delegate Writer.
     * This method delegates the write operation to the underlying Writer.
     * 将字符数组的一部分写入委托的 Writer。
     * 此方法将写入操作委托给底层 Writer。
     *
     * @param cbuf An array of characters.
     * 字符数组。
     * @param off The offset from which to start writing characters.
     * 开始写入字符的偏移量。
     * @param len The number of characters to write.
     * 要写入的字符数。
     * @throws IOException if an I/O error occurs during writing.
     * 如果在写入过程中发生 I/O 错误。
     */
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        delegate.write(cbuf, off, len);
    }

}
