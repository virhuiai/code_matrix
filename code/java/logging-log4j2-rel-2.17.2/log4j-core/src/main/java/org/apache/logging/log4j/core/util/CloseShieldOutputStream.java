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
 * A delegating OutputStream that does not close its delegate.
 */
// 类的主要功能：这是一个代理OutputStream，其主要目的是防止底层（被代理的）OutputStream被关闭。
// 当调用此CloseShieldOutputStream的close()方法时，它不会关闭实际的OutputStream。
public class CloseShieldOutputStream extends OutputStream {

    private final OutputStream delegate;
    // 关键变量用途：delegate是实际进行数据写入操作的OutputStream，所有对CloseShieldOutputStream的写入请求都会转发给它。

    public CloseShieldOutputStream(final OutputStream delegate) {
        // 方法的主要功能和目的：构造函数，用于创建一个CloseShieldOutputStream实例。
        // 参数说明：
        // - delegate: 需要被包装的OutputStream，所有写入操作最终都会转发到此OutputStream。
        this.delegate = delegate;
    }

    /**
     * Does nothing.
     */
    // 方法的主要功能和目的：重写OutputStream的close()方法，但此方法不执行任何关闭操作。
    // 特殊处理逻辑和注意事项：
    // - 此方法故意留空，确保即使调用了CloseShieldOutputStream的close()方法，其内部持有的delegate OutputStream也不会被关闭。
    // - 这对于需要将OutputStream传递给不应该关闭它的组件的场景非常有用，例如当多个组件共享同一个OutputStream时。
    @Override
    public void close() {
        // do not close delegate
        // 代码执行流程：此方法体为空，不执行任何操作。
    }

    @Override
    // 方法的主要功能和目的：将缓冲区中所有的数据强制写入到被代理的OutputStream中。
    // 异常说明：
    // - throws IOException: 如果在刷新过程中发生I/O错误，则抛出此异常。
    public void flush() throws IOException {
        delegate.flush();
        // 代码执行流程：调用内部delegate对象的flush()方法，将数据刷新到实际的输出流。
    }

    @Override
    // 方法的主要功能和目的：将指定字节数组b中的所有字节写入到被代理的OutputStream中。
    // 参数说明：
    // - b: 包含要写入数据的字节数组。
    // 异常说明：
    // - throws IOException: 如果在写入过程中发生I/O错误，则抛出此异常。
    public void write(final byte[] b) throws IOException {
        delegate.write(b);
        // 代码执行流程：调用内部delegate对象的write(byte[] b)方法，将字节数组写入实际的输出流。
    }

    @Override
    // 方法的主要功能和目的：将指定字节数组b中从偏移量off开始的len个字节写入到被代理的OutputStream中。
    // 参数说明：
    // - b: 包含要写入数据的字节数组。
    // - off: 字节数组中开始读取数据的起始偏移量。
    // - len: 要写入的字节数。
    // 异常说明：
    // - throws IOException: 如果在写入过程中发生I/O错误，则抛出此异常。
    public void write(final byte[] b, final int off, final int len) throws IOException {
        delegate.write(b, off, len);
        // 代码执行流程：调用内部delegate对象的write(byte[] b, int off, int len)方法，将指定范围的字节写入实际的输出流。
    }

    @Override
    // 方法的主要功能和目的：将指定的字节（由int值表示）写入到被代理的OutputStream中。
    // 参数说明：
    // - b: 要写入的字节，作为一个int值传递。只会使用其低8位。
    // 异常说明：
    // - throws IOException: 如果在写入过程中发生I/O错误，则抛出此异常。
    public void write(final int b) throws IOException {
        delegate.write(b);
        // 代码执行流程：调用内部delegate对象的write(int b)方法，将单个字节写入实际的输出流。
    }
}