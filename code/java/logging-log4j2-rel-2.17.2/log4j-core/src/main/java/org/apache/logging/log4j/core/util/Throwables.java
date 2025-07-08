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
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Helps with Throwable objects.
 * 帮助处理 Throwable 对象。
 */
public final class Throwables {

    private Throwables() {
    }

    /**
     * Returns the deepest cause of the given {@code throwable}.
     * 返回给定 {@code throwable} 的最深层原因。
     *
     * @param throwable the throwable to navigate
     * 要导航的 throwable 对象。
     * @return the deepest throwable or the given throwable
     * 最深层的 throwable 对象，如果不存在则返回给定的 throwable。
     */
    public static Throwable getRootCause(final Throwable throwable) {

        // Keep a second pointer that slowly walks the causal chain. If the fast
        // pointer ever catches the slower pointer, then there's a loop.
        // 维护第二个指针，该指针缓慢遍历因果链。如果快指针追上慢指针，则表示存在循环。
        Throwable slowPointer = throwable;
        boolean advanceSlowPointer = false;

        Throwable parent = throwable;
        Throwable cause;
        while ((cause = parent.getCause()) != null) { // 循环直到没有更多的原因
            parent = cause; // 将当前原因设置为父级
            if (parent == slowPointer) { // 检查是否检测到循环引用
                throw new IllegalArgumentException("loop in causal chain"); // 如果存在循环，则抛出异常
            }
            if (advanceSlowPointer) { // 只有在 advanceSlowPointer 为 true 时才移动慢指针
                slowPointer = slowPointer.getCause();
            }
            advanceSlowPointer = !advanceSlowPointer; // only advance every other iteration
            // 切换 advanceSlowPointer 的状态，确保慢指针每隔一次迭代才移动
        }
        return parent; // 返回最深层的原因

    }

    /**
     * Converts a Throwable stack trace into a List of Strings.
     * 将 Throwable 的堆栈跟踪转换为字符串列表。
     *
     * @param throwable the Throwable
     * 要转换的 Throwable 对象。
     * @return a List of Strings
     * 包含堆栈跟踪每一行的字符串列表。
     */
    public static List<String> toStringList(final Throwable throwable) {
        final StringWriter sw = new StringWriter(); // 用于捕获堆栈跟踪的字符串写入器
        final PrintWriter pw = new PrintWriter(sw); // 用于打印堆栈跟踪的打印写入器
        try {
            throwable.printStackTrace(pw); // 将 throwable 的堆栈跟踪打印到 PrintWriter 中
        } catch (final RuntimeException ex) {
            // Ignore any exceptions.
            // 忽略任何可能发生的运行时异常。
        }
        pw.flush(); // 刷新 PrintWriter，确保所有数据都被写入 StringWriter
        final List<String> lines = new ArrayList<>(); // 存储堆栈跟踪行的列表
        final LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString())); // 从 StringWriter 的内容创建行号读取器
        try {
            String line = reader.readLine(); // 读取第一行
            while (line != null) { // 循环直到没有更多行可读
                lines.add(line); // 将行添加到列表中
                line = reader.readLine(); // 读取下一行
            }
        } catch (final IOException ex) { // 捕获读取操作可能抛出的 IOException
            if (ex instanceof InterruptedIOException) { // 如果是中断的 IO 异常
                Thread.currentThread().interrupt(); // 重新设置中断状态
            }
            lines.add(ex.toString()); // 将异常的字符串表示添加到列表中
        } finally {
            Closer.closeSilently(reader); // 静默关闭 LineNumberReader，避免资源泄露
        }
        return lines; // 返回包含堆栈跟踪行的列表
    }

    /**
     * Rethrows a {@link Throwable}.
     * 重新抛出 {@link Throwable}。
     *
     * @param t the Throwable to throw.
     * 要抛出的 Throwable 对象。
     * @since 2.1
     * 此方法自 2.1 版本开始可用。
     */
    public static void rethrow(final Throwable t) {
        Throwables.<RuntimeException>rethrow0(t); // 调用泛型辅助方法重新抛出 Throwable
    }

    @SuppressWarnings("unchecked") // 抑制未经检查的强制转换警告
    private static <T extends Throwable> void rethrow0(final Throwable t) throws T {
        throw (T) t; // 将 Throwable 强制转换为泛型类型 T 并抛出
    }
}
