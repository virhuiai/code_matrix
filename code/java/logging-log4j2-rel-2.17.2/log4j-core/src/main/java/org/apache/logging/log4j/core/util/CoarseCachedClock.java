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

import java.util.concurrent.locks.LockSupport;

/**
 * This Clock implementation is similar to CachedClock. It is slightly faster at
 * the cost of some accuracy.
 * 这个Clock接口的实现类似于CachedClock。它以牺牲部分准确性为代价，提供了更快的时钟读取速度。
 * 主要功能是提供一个高性能、近似准确的当前时间，适用于对时间精度要求不是极高但需要频繁获取时间戳的场景。
 */
public final class CoarseCachedClock implements Clock {
    private static volatile CoarseCachedClock instance;
    // instance 变量用于存储 CoarseCachedClock 的单例实例。
    // 使用 volatile 关键字确保多线程环境下对 instance 的修改是可见的。
    private static final Object INSTANCE_LOCK = new Object();
    // INSTANCE_LOCK 是一个用于同步控制单例实例创建的锁对象。
    // 确保在多线程环境中只有一个线程能创建实例，避免重复创建和竞态条件。
    // ignore IDE complaints; volatile long is fine
    // 忽略 IDE 关于 volatile long 的警告，因为在这里 volatile long 的使用是正确的，可以确保线程间的可见性。
    private volatile long millis = System.currentTimeMillis();
    // millis 变量存储当前缓存的时间戳（毫秒）。
    // volatile 确保每次读取都从主内存获取最新值，写入也立即刷新到主内存，保证了 updater 线程对它的更新对所有读取线程立即可见。

    private final Thread updater = new Log4jThread("CoarseCachedClock Updater Thread") {
        // updater 是一个后台线程，负责周期性地更新 millis 缓存的时间戳。
        // 它是一个 Log4jThread 类型的线程，并指定了线程名称，方便调试和监控。
        @Override
        public void run() {
            // run 方法是 updater 线程的执行体。
            while (true) {
                // 这是一个无限循环，表示 updater 线程会一直运行。
                millis = System.currentTimeMillis();
                // 核心步骤：将当前系统的精确时间（毫秒）赋值给 millis 变量，实现时间缓存的更新。

                // avoid explicit dependency on sun.misc.Util
                // 避免对 sun.misc.Util 的显式依赖。LockSupport.parkNanos 提供了一种跨平台且高效的线程暂停机制。
                LockSupport.parkNanos(1000 * 1000);
                // 使用 LockSupport.parkNanos 方法使当前线程（updater 线程）暂停指定纳秒数。
                // 1000 * 1000 纳秒等于 1 毫秒，这意味着 updater 线程大约每毫秒更新一次 millis 值。
                // 这是一个重要的配置参数，决定了缓存时间的更新频率和精度。
            }
        }
    };

    private CoarseCachedClock() {
        // CoarseCachedClock 类的私有构造函数，确保只能通过静态方法 instance() 获取单例。
        updater.setDaemon(true);
        // 将 updater 线程设置为守护线程。
        // 守护线程的特点是，当所有非守护线程都结束时，守护线程会自动终止，不会阻止JVM退出。
        updater.start();
        // 启动 updater 线程，使其开始执行 run 方法，周期性地更新时间缓存。
    }

    /**
     * Returns the singleton instance.
     * 返回单例实例。
     *
     * @return the singleton instance
     * 返回 CoarseCachedClock 的单例实例。
     */
    public static CoarseCachedClock instance() {
        // instance 方法是获取 CoarseCachedClock 单例的公共静态方法，实现了双重检查锁定（Double-Checked Locking）模式。
        // LOG4J2-819: use lazy initialization of threads
        // 这个注释指的是 LOG4J2-819 缺陷修复，它强调了使用线程的延迟初始化。
        // 即只有在首次需要 CoarseCachedClock 实例时才创建并启动其内部的 updater 线程。
        CoarseCachedClock result = instance;
        // 首次检查：将 volatile 变量 instance 的值赋给局部变量 result。
        // 这样可以减少对 volatile 变量的直接访问，提高性能。
        if (result == null) {
            // 如果 result 为 null，说明 instance 尚未被初始化。
            synchronized (INSTANCE_LOCK) {
                // 进入同步块，使用 INSTANCE_LOCK 对临界区进行保护，确保线程安全。
                result = instance;
                // 第二次检查：在同步块内部再次检查 instance 是否为 null。
                // 这是双重检查锁定模式的关键，避免了不必要的同步开销。
                if (result == null) {
                    // 如果 instance 仍然为 null，则表示这是第一次创建实例。
                    instance = result = new CoarseCachedClock();
                    // 创建 CoarseCachedClock 的新实例，并将其赋值给 instance 和 result。
                    // 此时，构造函数会被调用，updater 线程也会被启动。
                }
            }
        }
        return result;
        // 返回 CoarseCachedClock 的单例实例。
    }

    /**
     * Returns the value of a private long field that is updated by a background
     * thread once every millisecond. Because timers on most platforms do not
     * have millisecond granularity, the returned value may "jump" every 10 or
     * 16 milliseconds.
     * 返回一个私有的 long 字段的值，该字段由后台线程每毫秒更新一次。
     * 由于大多数平台上的计时器不具有毫秒级精度，因此返回的值可能会每隔 10 或 16 毫秒“跳跃”一次。
     * 主要功能是提供一个缓存的当前时间，以避免频繁调用 System.currentTimeMillis() 带来的性能开销。
     * @return the cached time
     * 返回缓存的当前时间（毫秒）。
     */
    @Override
    public long currentTimeMillis() {
        // currentTimeMillis 方法是 Clock 接口的实现，用于获取当前时间（毫秒）。
        // 它的核心是返回缓存的 millis 变量的值。
        return millis;
        // 直接返回由后台线程周期性更新的 millis 变量的值。
        // 这种方式比每次都调用 System.currentTimeMillis() 更快，但可能存在最多 1 毫秒的误差（取决于 updater 线程的更新周期）。
    }
}
