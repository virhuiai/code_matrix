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
import java.io.OutputStream;

/**
 * Writes all data to the famous <b>/dev/null</b>.
 * 将所有数据写入著名的 <b>/dev/null</b>。
 * <p>
 * This output stream has no destination (file/socket etc.) and all bytes written to it are ignored and lost.
 * 这个输出流没有实际目的地（文件/套接字等），所有写入它的字节都会被忽略并丢失。
 * </p>
 * Originally from Apache Commons IO.
 * 最初来源于 Apache Commons IO。
 *
 * @since 2.3
 * 自 2.3 版本开始提供。
 */
public class NullOutputStream extends OutputStream {

    private static final NullOutputStream INSTANCE = new NullOutputStream();
    // 定义一个私有的静态最终实例，确保 NullOutputStream 是一个单例。

    /**
     * @deprecated Deprecated in 2.7: use {@link #getInstance()}.
     * 自 2.7 版本开始废弃：请使用 {@link #getInstance()} 方法。
     */
    @Deprecated
    public static final NullOutputStream NULL_OUTPUT_STREAM = INSTANCE;
    // 这是一个已废弃的常量，指向单例实例，建议使用 getInstance() 方法。

    /**
     * Gets the singleton instance.
     * 获取单例实例。
     *
     * @return the singleton instance.
     * 返回单例实例。
     */
    public static NullOutputStream getInstance() {
        return INSTANCE;
    }
    // 提供一个公共静态方法来获取 NullOutputStream 的唯一实例，实现了单例模式。

    private NullOutputStream() {
        // do nothing
        // 构造函数私有化，防止外部直接创建实例。
    }

    /**
     * Does nothing - output to <code>/dev/null</code>.
     * 不执行任何操作 - 输出到 <code>/dev/null</code>。
     *
     * @param b
     *        The bytes to write
     * 要写入的字节数组。
     * @param off
     *        The start offset
     * 写入的起始偏移量。
     * @param len
     *        The number of bytes to write
     * 要写入的字节数量。
     */
    @Override
    public void write(final byte[] b, final int off, final int len) {
        // to /dev/null
        // 此方法不执行任何写入操作，模拟写入到空设备。
    }

    /**
     * Does nothing - output to <code>/dev/null</code>.
     * 不执行任何操作 - 输出到 <code>/dev/null</code>。
     *
     * @param b
     *        The byte to write
     * 要写入的单个字节。
     */
    @Override
    public void write(final int b) {
        // to /dev/null
        // 此方法不执行任何写入操作，模拟写入到空设备。
    }

    /**
     * Does nothing - output to <code>/dev/null</code>.
     * 不执行任何操作 - 输出到 <code>/dev/null</code>。
     *
     * @param b
     *        The bytes to write
     * 要写入的字节数组。
     * @throws IOException
     *         never
     * 永远不会抛出 IOException。
     */
    @Override
    public void write(final byte[] b) throws IOException {
        // to /dev/null
        // 此方法不执行任何写入操作，模拟写入到空设备。
    }
}
