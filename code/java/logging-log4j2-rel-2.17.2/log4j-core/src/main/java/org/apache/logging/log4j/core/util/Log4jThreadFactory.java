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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates {@link Log4jThread}s.
 * 创建 {@link Log4jThread} 实例的工厂类。
 *
 * @since 2.7
 * 从 Log4j 2.7 版本开始引入。
 */
public class Log4jThreadFactory implements ThreadFactory {

    private static final String PREFIX = "TF-";
    // 线程工厂名称前缀。

    /**
     * Creates a new daemon thread factory.
     * 创建一个新的守护线程工厂。
     *
     * @param threadFactoryName
     *            The thread factory name.
     * 线程工厂的名称。
     * @return a new daemon thread factory.
     * 返回一个新的守护线程工厂实例。
     */
    public static Log4jThreadFactory createDaemonThreadFactory(final String threadFactoryName) {
        // 调用构造函数创建一个守护线程工厂，默认优先级为 NORM_PRIORITY。
        return new Log4jThreadFactory(threadFactoryName, true, Thread.NORM_PRIORITY);
    }

    /**
     * Creates a new thread factory.
     * 创建一个新的非守护线程工厂。
     *
     * This is mainly used for tests. Production code should be very careful with creating
     * non-daemon threads since those will block application shutdown
     * (see https://issues.apache.org/jira/browse/LOG4J2-1748).
     * 这个方法主要用于测试。生产代码在使用非守护线程时应非常小心，因为它们会阻塞应用程序关闭
     * (详见 https://issues.apache.org/jira/browse/LOG4J2-1748)。
     *
     * @param threadFactoryName
     *            The thread factory name.
     * 线程工厂的名称。
     * @return a new daemon thread factory.
     * 返回一个新的非守护线程工厂实例。
     */
    public static Log4jThreadFactory createThreadFactory(final String threadFactoryName) {
        // 调用构造函数创建一个非守护线程工厂，默认优先级为 NORM_PRIORITY。
        return new Log4jThreadFactory(threadFactoryName, false, Thread.NORM_PRIORITY);
    }

    private static final AtomicInteger FACTORY_NUMBER = new AtomicInteger(1);
    // 用于生成线程工厂编号的原子整数，确保唯一性。
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    // 用于生成线程编号的原子整数，确保唯一性。
    private final boolean daemon;
    // 指示由该工厂创建的线程是否为守护线程。
    private final ThreadGroup group;
    // 线程所属的线程组。
    private final int priority;
    // 线程的优先级。
    private final String threadNamePrefix;
    // 由该工厂创建的线程的名称前缀。

    /**
     * Constructs an initialized thread factory.
     * 构造并初始化一个线程工厂实例。
     *
     * @param threadFactoryName
     *            The thread factory name.
     * 线程工厂的名称。
     * @param daemon
     *            Whether to create daemon threads.
     * 是否创建守护线程。如果为 true，则创建守护线程；否则创建非守护线程。
     * @param priority
     *            The thread priority.
     * 线程的优先级。
     */
    public Log4jThreadFactory(final String threadFactoryName, final boolean daemon, final int priority) {
        // 设置线程名称前缀，包含工厂编号和用户提供的名称。
        this.threadNamePrefix = PREFIX + FACTORY_NUMBER.getAndIncrement() + "-" + threadFactoryName + "-";
        // 设置是否为守护线程。
        this.daemon = daemon;
        // 设置线程优先级。
        this.priority = priority;
        // 获取当前系统的安全管理器。
        final SecurityManager securityManager = System.getSecurityManager();
        // 如果存在安全管理器，则使用安全管理器的线程组；否则使用当前线程的线程组。
        this.group = securityManager != null ? securityManager.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        // Log4jThread prefixes names with "Log4j2-".
        // Log4jThread 会将名称前缀设置为 "Log4j2-"。
        // 创建一个新的 Log4jThread 实例，并设置其线程组、要执行的 Runnable、线程名称（包含前缀和线程编号）和栈大小。
        final Thread thread = new Log4jThread(group, runnable, threadNamePrefix + THREAD_NUMBER.getAndIncrement(), 0);
        // 检查新创建的线程是否与期望的守护状态一致。
        if (thread.isDaemon() != daemon) {
            // 如果不一致，则设置线程的守护状态。
            thread.setDaemon(daemon);
        }
        // 检查新创建的线程优先级是否与期望的优先级一致。
        if (thread.getPriority() != priority) {
            // 如果不一致，则设置线程的优先级。
            thread.setPriority(priority);
        }
        // 返回新创建并配置好的线程。
        return thread;
    }

}
