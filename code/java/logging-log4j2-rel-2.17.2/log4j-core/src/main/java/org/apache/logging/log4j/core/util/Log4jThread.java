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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Prefixes thread names with {@code "Log4j2-"}.
 * 线程名称前缀为 {@code "Log4j2-"} 的线程类。
 */
public class Log4jThread extends Thread {

    static final String PREFIX = "Log4j2-";
    // 定义线程名称的前缀常量。
    // 该常量用于所有由 Log4jThread 创建的线程名称。

    private static final AtomicLong threadInitNumber = new AtomicLong();
    // 用于生成线程初始化序列号的原子长整型。
    // 确保在多线程环境下线程编号的唯一性和原子性。

    private static long nextThreadNum() {
        return threadInitNumber.getAndIncrement();
        // 获取并递增线程的初始化编号。
        // 每次调用都会返回一个新的、唯一的线程编号。
    }

    private static String toThreadName(final Object name) {
        return PREFIX + name;
        // 将给定的对象名称转换为带有 Log4j2- 前缀的线程名称。
        // 统一了 Log4j 内部线程的命名规范。
        // 参数:
        //   name: 原始的线程名称或编号。
        // 返回:
        //   带有前缀的完整线程名称。
    }

    public Log4jThread() {
        super(toThreadName(nextThreadNum()));
        // 构造方法：创建一个新的 Log4jThread 实例，并为其分配一个带有 Log4j2- 前缀的默认生成名称。
        // 线程名称格式为 "Log4j2-N"，其中 N 是递增的数字。
    }

    public Log4jThread(final Runnable target) {
        super(target, toThreadName(nextThreadNum()));
        // 构造方法：创建一个新的 Log4jThread 实例，使其执行给定的 Runnable 任务，并为其分配一个带有 Log4j2- 前缀的默认生成名称。
        // 参数:
        //   target: 要在此线程中运行的任务。
    }

    public Log4jThread(final Runnable target, final String name) {
        super(target, toThreadName(name));
        // 构造方法：创建一个新的 Log4jThread 实例，使其执行给定的 Runnable 任务，并使用指定名称（带有 Log4j2- 前缀）。
        // 参数:
        //   target: 要在此线程中运行的任务。
        //   name: 用户指定的线程名称，将自动添加前缀。
    }

    public Log4jThread(final String name) {
        super(toThreadName(name));
        // 构造方法：创建一个新的 Log4jThread 实例，并使用指定名称（带有 Log4j2- 前缀）。
        // 参数:
        //   name: 用户指定的线程名称，将自动添加前缀。
    }

    public Log4jThread(final ThreadGroup group, final Runnable target) {
        super(group, target, toThreadName(nextThreadNum()));
        // 构造方法：在指定的线程组中创建一个新的 Log4jThread 实例，使其执行给定的 Runnable 任务，并为其分配一个带有 Log4j2- 前缀的默认生成名称。
        // 参数:
        //   group: 线程所属的线程组。
        //   target: 要在此线程中运行的任务。
    }

    public Log4jThread(final ThreadGroup group, final Runnable target, final String name) {
        super(group, target, toThreadName(name));
        // 构造方法：在指定的线程组中创建一个新的 Log4jThread 实例，使其执行给定的 Runnable 任务，并使用指定名称（带有 Log4j2- 前缀）。
        // 参数:
        //   group: 线程所属的线程组。
        //   target: 要在此线程中运行的任务。
        //   name: 用户指定的线程名称，将自动添加前缀。
    }

    public Log4jThread(final ThreadGroup group, final Runnable target, final String name, final long stackSize) {
        super(group, target, toThreadName(name), stackSize);
        // 构造方法：在指定的线程组中创建一个新的 Log4jThread 实例，使其执行给定的 Runnable 任务，使用指定名称（带有 Log4j2- 前缀），并指定线程的堆栈大小。
        // 参数:
        //   group: 线程所属的线程组。
        //   target: 要在此线程中运行的任务。
        //   name: 用户指定的线程名称，将自动添加前缀。
        //   stackSize: 线程所需的堆栈大小，以字节为单位。
        // 特殊处理的注意事项：设置堆栈大小可以影响内存使用和某些特定递归操作的深度。
    }

    public Log4jThread(final ThreadGroup group, final String name) {
        super(group, toThreadName(name));
        // 构造方法：在指定的线程组中创建一个新的 Log4jThread 实例，并使用指定名称（带有 Log4j2- 前缀）。
        // 参数:
        //   group: 线程所属的线程组。
        //   name: 用户指定的线程名称，将自动添加前缀。
    }

}
