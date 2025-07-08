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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * ShutdownRegistrationStrategy that simply uses {@link Runtime#addShutdownHook(Thread)}. If no strategy is specified,
 * this one is used for shutdown hook registration.
 * 一个简单的关机注册策略，它使用 {@link Runtime#addShutdownHook(Thread)}。如果没有指定策略，则使用此策略进行关机钩子注册。
 *
 * @since 2.1
 */
public class DefaultShutdownCallbackRegistry implements ShutdownCallbackRegistry, LifeCycle2, Runnable {
    /** Status logger. */
    // 状态日志记录器
    protected static final Logger LOGGER = StatusLogger.getLogger();

    private final AtomicReference<State> state = new AtomicReference<>(State.INITIALIZED);
    // 当前生命周期状态的原子引用，初始为INITIALIZED（已初始化）
    private final ThreadFactory threadFactory;
    // 用于创建线程的线程工厂

    // use references to prevent memory leaks
    // 使用引用来防止内存泄漏
    private final Collection<Reference<Cancellable>> hooks = new CopyOnWriteArrayList<>();
    // 存储可取消的关机钩子引用的集合，使用CopyOnWriteArrayList保证线程安全
    private Reference<Thread> shutdownHookRef;
    // 关机钩子线程的引用

    /**
     * Constructs a DefaultShutdownRegistrationStrategy.
     * 构造一个 DefaultShutdownRegistrationStrategy 实例。
     */
    public DefaultShutdownCallbackRegistry() {
        this(Executors.defaultThreadFactory());
    }

    /**
     * Constructs a DefaultShutdownRegistrationStrategy using the given {@link ThreadFactory}.
     * 使用给定的 {@link ThreadFactory} 构造一个 DefaultShutdownRegistrationStrategy 实例。
     *
     * @param threadFactory the ThreadFactory to use to create a {@link Runtime} shutdown hook thread
     * 用于创建 {@link Runtime} 关机钩子线程的线程工厂
     */
    protected DefaultShutdownCallbackRegistry(final ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    /**
     * Executes the registered shutdown callbacks.
     * 执行所有已注册的关机回调。
     */
    @Override
    public void run() {
        // 当状态从 STARTED 转换为 STOPPING 时执行关机回调
        if (state.compareAndSet(State.STARTED, State.STOPPING)) {
            // 遍历所有注册的钩子
            for (final Reference<Cancellable> hookRef : hooks) {
                Cancellable hook = hookRef.get(); // 获取实际的钩子对象
                if (hook != null) { // 如果钩子对象存在
                    try {
                        hook.run(); // 执行钩子回调
                    } catch (final Throwable t1) {
                        try {
                            // 记录执行关机钩子时捕获的异常
                            LOGGER.error(SHUTDOWN_HOOK_MARKER, "Caught exception executing shutdown hook {}", hook, t1);
                        } catch (final Throwable t2) {
                            // 记录日志记录异常时的异常
                            System.err.println("Caught exception " + t2.getClass() + " logging exception " + t1.getClass());
                            t1.printStackTrace();
                        }
                    }
                }
            }
            // 设置状态为 STOPPED（已停止）
            state.set(State.STOPPED);
        }
    }

    private static class RegisteredCancellable implements Cancellable {
        private Runnable callback;
        // 实际要执行的回调任务
        private Collection<Reference<Cancellable>> registered;
        // 注册了此回调的集合，用于在取消时将其移除

        RegisteredCancellable(final Runnable callback, final Collection<Reference<Cancellable>> registered) {
            this.callback = callback;
            this.registered = registered;
        }

        @Override
        public void cancel() {
            callback = null; // 将回调设置为null，防止再次执行
            Collection<Reference<Cancellable>> references = registered;
            if (references != null) {
                registered = null; // 清除对注册集合的引用
                // 从注册集合中移除此Cancellable对象或已失效的引用
                references.removeIf(ref -> {
                    Cancellable value = ref.get();
                    return value == null || value == RegisteredCancellable.this;
                });
            }
        }

        @Override
        public void run() {
            final Runnable runnableHook = callback;
            if (runnableHook != null) { // 如果回调任务存在
                runnableHook.run(); // 执行回调任务
                callback = null; // 执行后将回调设置为null
            }
        }

        @Override
        public String toString() {
            return String.valueOf(callback); // 返回回调任务的字符串表示
        }
    }

    @Override
    public Cancellable addShutdownCallback(final Runnable callback) {
        if (isStarted()) { // 如果当前实例已启动
            final Cancellable receipt = new RegisteredCancellable(callback, hooks);
            // 创建一个新的可取消的注册回调实例
            hooks.add(new SoftReference<>(receipt)); // 使用SoftReference包装并添加到钩子集合中
            return receipt; // 返回可取消的注册回调
        }
        // 如果未启动，则抛出异常
        throw new IllegalStateException("Cannot add new shutdown hook as this is not started. Current state: " +
            state.get().name());
    }

    @Override
    public void initialize() {
        // 初始化方法，此实现为空
    }

    /**
     * Registers the shutdown thread only if this is initialized.
     * 只有在实例初始化后才注册关机线程。
     */
    @Override
    public void start() {
        // 尝试将状态从 INITIALIZED 转换为 STARTING
        if (state.compareAndSet(State.INITIALIZED, State.STARTING)) {
            try {
                // 添加由线程工厂创建的新线程作为关机钩子
                addShutdownHook(threadFactory.newThread(this));
                state.set(State.STARTED); // 设置状态为 STARTED（已启动）
            } catch (final IllegalStateException ex) {
                state.set(State.STOPPED); // 如果发生非法状态异常，设置状态为 STOPPED
                throw ex;
            } catch (final Exception e) {
                LOGGER.catching(e); // 记录异常
                state.set(State.STOPPED); // 设置状态为 STOPPED
            }
        }
    }

    private void addShutdownHook(final Thread thread) {
        shutdownHookRef = new WeakReference<>(thread); // 使用WeakReference保存关机线程的引用
        Runtime.getRuntime().addShutdownHook(thread); // 向Java运行时环境添加关机钩子
    }

    @Override
    public void stop() {
        // 使用默认超时参数停止
        stop(AbstractLifeCycle.DEFAULT_STOP_TIMEOUT, AbstractLifeCycle.DEFAULT_STOP_TIMEUNIT);
    }

    /**
     * Cancels the shutdown thread only if this is started.
     * 只有在实例启动后才取消关机线程。
     */
    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        // 尝试将状态从 STARTED 转换为 STOPPING
        if (state.compareAndSet(State.STARTED, State.STOPPING)) {
            try {
                removeShutdownHook(); // 移除关机钩子
            } finally {
                state.set(State.STOPPED); // 无论是否成功移除，最终设置状态为 STOPPED
            }
        }
        return true; // 总是返回true
    }

    private void removeShutdownHook() {
        final Thread shutdownThread = shutdownHookRef.get(); // 获取关机线程的引用
        if (shutdownThread != null) { // 如果关机线程存在
            Runtime.getRuntime().removeShutdownHook(shutdownThread); // 从Java运行时环境中移除关机钩子
            shutdownHookRef.enqueue(); // 将引用加入到其引用队列中（便于垃圾回收）
        }
    }

    @Override
    public State getState() {
        return state.get(); // 返回当前生命周期状态
    }

    /**
     * Indicates if this can accept shutdown hooks.
     * 指示此实例是否可以接受关机钩子。
     *
     * @return true if this can accept shutdown hooks
     * 如果可以接受关机钩子则返回 true
     */
    @Override
    public boolean isStarted() {
        return state.get() == State.STARTED; // 判断当前状态是否为 STARTED
    }

    @Override
    public boolean isStopped() {
        return state.get() == State.STOPPED; // 判断当前状态是否为 STOPPED
    }

}
