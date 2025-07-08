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
 * Implementation of the {@code Clock} interface that tracks the time in a
 * private long field that is updated by a background thread once every
 * millisecond. Timers on most platforms do not have millisecond granularity, so
 * the returned value may "jump" every 10 or 16 milliseconds. To reduce this
 * problem, this class also updates the internal time value every 1024 calls to
 * {@code currentTimeMillis()}.
 * 实现 Clock 接口，通过一个后台线程每毫秒更新一次私有 long 字段来跟踪时间。
 * 大多数平台上的计时器不具备毫秒级精度，因此返回的值可能会每隔 10 或 16 毫秒“跳跃”一次。
 * 为了减少这个问题，该类还在每调用 1024 次 {@code currentTimeMillis()} 时更新内部时间值。
 */
public final class CachedClock implements Clock {
    private static final int UPDATE_THRESHOLD = 1000;
    // 更新阈值，当 currentTimeMillis() 方法被调用次数超过此阈值时，会强制更新时间。
    private static volatile CachedClock instance;
    // CachedClock 的单例实例。使用 volatile 关键字确保多线程环境下的可见性。
    private static final Object INSTANCE_LOCK = new Object();
    // 用于同步创建单例实例的锁对象。
    private volatile long millis = System.currentTimeMillis();
    // 缓存的时间戳，默认为当前系统时间。使用 volatile 关键字确保时间戳的最新值对所有线程可见。
    private short count = 0;
    // 记录 currentTimeMillis() 方法被调用的次数，用于触发强制更新。

    private CachedClock() {
        // 私有构造函数，确保只能通过静态方法 instance() 获取实例，实现单例模式。
        final Thread updater = new Log4jThread(() -> {
            // 创建一个后台线程，用于定期更新缓存的时间戳。
            while (true) {
                // 循环持续运行，不断更新时间。
                final long time = System.currentTimeMillis();
                // 获取当前系统时间。
                millis = time;
                // 更新 volatile 字段 millis，使新时间对所有线程可见。

                // avoid explicit dependency on sun.misc.Util
                // 避免对 sun.misc.Util 的显式依赖。
                LockSupport.parkNanos(1000 * 1000);
                // 线程休眠 1 毫秒 (1000微秒 * 1000纳秒/微秒)，以每毫秒更新一次时间。
            }
        }, "CachedClock Updater Thread");
        // 为更新线程命名，便于调试和监控。
        updater.setDaemon(true);
        // 将更新线程设置为守护线程，这意味着当所有非守护线程结束时，此线程也会自动终止。
        updater.start();
        // 启动更新线程。
    }

    public static CachedClock instance() {
        // LOG4J2-819: use lazy initialization of threads
        // LOG4J2-819: 使用线程的延迟初始化。
        CachedClock result = instance;
        // 第一次检查：尝试快速获取实例，避免进入同步块，提高性能。
        if (result == null) {
            // 如果实例为空，则进入同步块。
            synchronized (INSTANCE_LOCK) {
                // 同步块，确保在多线程环境下只有一个线程能创建实例。
                result = instance;
                // 第二次检查：在同步块内部再次检查实例是否为空，防止重复创建（双重检查锁定）。
                if (result == null) {
                    instance = result = new CachedClock();
                    // 如果实例仍然为空，则创建新的 CachedClock 实例并赋值给 instance 和 result。
                }
            }
        }
        return result;
        // 返回 CachedClock 的单例实例。
    }

    /**
     * Returns the value of a private long field that is updated by a background
     * thread once every millisecond. Timers on most platforms do not
     * have millisecond granularity, the returned value may "jump" every 10 or
     * 16 milliseconds. To reduce this problem, this method also updates the
     * internal time value every 1024 calls.
     * 返回一个私有 long 字段的值，该字段由后台线程每毫秒更新一次。
     * 大多数平台上的计时器不具备毫秒级精度，返回的值可能会每隔 10 或 16 毫秒“跳跃”一次。
     * 为了减少这个问题，此方法还在每调用 1024 次时更新内部时间值。
     * @return the cached time
     * @return 缓存的时间值
     */
    @Override
    public long currentTimeMillis() {
        // 实现 Clock 接口的 currentTimeMillis 方法，返回当前缓存的时间。

        // The count field is not volatile on purpose to reduce contention on this field.
        // count 字段特意没有声明为 volatile，旨在减少对该字段的竞争。
        // This means that some threads may not see the increments made to this field
        // by other threads. This is not a problem: the timestamp does not need to be
        // updated exactly every 1000 calls.
        // 这意味着某些线程可能看不到其他线程对该字段进行的增量操作。
        // 这不是一个问题：时间戳不需要精确地每 1000 次调用更新一次。
        if (++count > UPDATE_THRESHOLD) {
            // 每次调用方法时，将 count 加 1。如果 count 超过更新阈值 (UPDATE_THRESHOLD)。
            millis = System.currentTimeMillis(); // update volatile field: store-store barrier
            // 强制更新 volatile 字段 millis 为当前系统时间。
            // 这会创建一个存储-存储屏障 (store-store barrier)，确保在此之前的写入操作在更新 millis 之前完成。
            count = 0; // after a memory barrier: this change _is_ visible to other threads
            // 重置 count 为 0。由于 millis 是 volatile 字段，对其的写入操作会创建一个内存屏障，
            // 确保 count 的重置对其他线程也是可见的。
        }
        return millis;
        // 返回当前缓存的时间戳。
    }
}
