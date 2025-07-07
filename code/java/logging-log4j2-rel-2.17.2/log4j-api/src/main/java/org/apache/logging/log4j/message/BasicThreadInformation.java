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
package org.apache.logging.log4j.message;

import static org.apache.logging.log4j.util.Chars.LF;
import static org.apache.logging.log4j.util.Chars.SPACE;

import org.apache.logging.log4j.util.StringBuilders;

/**
 * Generates information about the current Thread. Used internally by ThreadDumpMessage.
 * 生成当前线程的信息。由 ThreadDumpMessage 在内部使用。
 */
class BasicThreadInformation implements ThreadInformation {
    private static final int HASH_SHIFT = 32;
    // HASH_SHIFT: 用于计算哈希码时，将线程ID右移的位数。
    private static final int HASH_MULTIPLIER = 31;
    // HASH_MULTIPLIER: 用于计算哈希码时的乘数。
    private final long id;
    // id: 线程的唯一标识符。
    private final String name;
    // name: 线程的名称。
    private final String longName;
    // longName: 线程的完整名称，通常是线程对象的 toString() 返回值。
    private final Thread.State state;
    // state: 线程的当前状态，例如 RUNNABLE, BLOCKED 等。
    private final int priority;
    // priority: 线程的优先级。
    private final boolean isAlive;
    // isAlive: 表示线程是否存活。
    private final boolean isDaemon;
    // isDaemon: 表示线程是否是守护线程。
    private final String threadGroupName;
    // threadGroupName: 线程所属的线程组的名称。

    /**
     * The Constructor.
     * 构造函数。
     * @param thread The Thread to capture.
     * 要捕获信息的线程对象。
     */
    BasicThreadInformation(final Thread thread) {
        // 构造函数，用于初始化 BasicThreadInformation 实例。
        this.id = thread.getId();
        // 获取线程的唯一ID并赋值给id字段。
        this.name = thread.getName();
        // 获取线程的名称并赋值给name字段。
        this.longName = thread.toString();
        // 获取线程的字符串表示并赋值给longName字段。
        this.state = thread.getState();
        // 获取线程的当前状态并赋值给state字段。
        this.priority = thread.getPriority();
        // 获取线程的优先级并赋值给priority字段。
        this.isAlive = thread.isAlive();
        // 判断线程是否存活并赋值给isAlive字段。
        this.isDaemon = thread.isDaemon();
        // 判断线程是否是守护线程并赋值给isDaemon字段。
        final ThreadGroup group = thread.getThreadGroup();
        // 获取线程所属的线程组。
        threadGroupName = group == null ? null : group.getName();
        // 如果线程组存在，则获取线程组的名称并赋值给threadGroupName字段，否则为null。
    }

    @Override
    public boolean equals(final Object o) {
        // 重写 equals 方法，用于比较两个 BasicThreadInformation 对象是否相等。
        if (this == o) {
            // 如果是同一个对象，则直接返回 true。
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            // 如果传入对象为 null 或类型不一致，则返回 false。
            return false;
        }

        final BasicThreadInformation that = (BasicThreadInformation) o;
        // 将传入对象转换为 BasicThreadInformation 类型。

        if (id != that.id) {
            // 如果线程ID不相等，则返回 false。
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            // 如果线程名称不相等（考虑 null 情况），则返回 false。
            return false;
        }

        return true;
        // 如果ID和名称都相等，则认为对象相等，返回 true。
    }

    @Override
    public int hashCode() {
        // 重写 hashCode 方法，用于生成对象的哈希码。
        int result = (int) (id ^ (id >>> HASH_SHIFT));
        // 使用线程ID及其右移位的结果计算初始哈希码。
        result = HASH_MULTIPLIER * result + (name != null ? name.hashCode() : 0);
        // 将初始哈希码与线程名称的哈希码（如果名称为null则为0）结合，得到最终哈希码。
        return result;
        // 返回计算出的哈希码。
    }

    /**
     * Print the thread information.
     * 打印线程信息。
     * @param sb The StringBuilder.
     * 用于构建字符串的 StringBuilder 对象。
     */
    @Override
    public void printThreadInfo(final StringBuilder sb) {
        // 打印线程的基本信息到 StringBuilder。
        StringBuilders.appendDqValue(sb, name).append(SPACE);
        // 将线程名称用双引号括起来并添加到 StringBuilder，然后添加一个空格。
        if (isDaemon) {
            // 如果是守护线程。
            sb.append("daemon ");
            // 添加 "daemon " 字符串。
        }
        sb.append("prio=").append(priority).append(" tid=").append(id).append(' ');
        // 添加优先级和线程ID信息。
        if (threadGroupName != null) {
            // 如果线程组名称不为 null。
            StringBuilders.appendKeyDqValue(sb, "group", threadGroupName);
            // 添加线程组信息，格式为 key="value"。
        }
        sb.append(LF);
        // 添加换行符。
        sb.append("\tThread state: ").append(state.name()).append(LF);
        // 添加线程状态信息，并再次添加换行符。
    }

    /**
     * Format the StackTraceElements.
     * 格式化堆栈跟踪元素。
     * @param sb The StringBuilder.
     * 用于构建字符串的 StringBuilder 对象。
     * @param trace The stack trace element array to format.
     * 要格式化的堆栈跟踪元素数组。
     */
    @Override
    public void printStack(final StringBuilder sb, final StackTraceElement[] trace) {
        // 遍历堆栈跟踪元素数组，并将其格式化后添加到 StringBuilder。
        for (final StackTraceElement element : trace) {
            // 遍历数组中的每一个堆栈跟踪元素。
            sb.append("\tat ").append(element).append(LF);
            // 在每个堆栈跟踪元素前添加 "\tat "，然后添加元素本身，最后添加换行符。
        }
    }
}
