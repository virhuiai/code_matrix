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
package org.apache.logging.log4j.core;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.status.StatusLogger;

/**
 * A life cycle to be extended.
 * 一个可扩展的生命周期。
 * <p>
 * Wraps a {@link LifeCycle.State}.
 * 封装了 {@link LifeCycle.State} 枚举。
 * </p>
 */
public class AbstractLifeCycle implements LifeCycle2 {

    public static final int DEFAULT_STOP_TIMEOUT = 0;
    // 默认的停止超时时间，单位由 DEFAULT_STOP_TIMEUNIT 定义。
    public static final TimeUnit DEFAULT_STOP_TIMEUNIT = TimeUnit.MILLISECONDS;
    // 默认的停止超时时间单位，这里是毫秒。

    /**
     * Allow subclasses access to the status logger without creating another instance.
     * 允许子类访问状态日志器，而无需创建新的实例。
     */
    protected static final org.apache.logging.log4j.Logger LOGGER = StatusLogger.getLogger();
    // 状态日志器，用于记录组件生命周期中的状态信息。

    /**
     * Gets the status logger.
     * 获取状态日志器。
     *
     * @return the status logger.
     * @return 状态日志器实例。
     */
    protected static org.apache.logging.log4j.Logger getStatusLogger() {
        return LOGGER;
    }

    private volatile LifeCycle.State state = LifeCycle.State.INITIALIZED;
    // 当前生命周期状态，使用 volatile 关键字确保多线程环境下的可见性，默认为 INITIALIZED（已初始化）。

    protected boolean equalsImpl(final Object obj) {
        // equals 方法的具体实现。
        if (this == obj) {
            // 如果是同一个对象，直接返回 true。
            return true;
        }
        if (obj == null) {
            // 如果传入对象为 null，返回 false。
            return false;
        }
        if (getClass() != obj.getClass()) {
            // 如果类类型不一致，返回 false。
            return false;
        }
        final LifeCycle other = (LifeCycle) obj;
        // 将传入对象转换为 LifeCycle 类型。
        if (state != other.getState()) {
            // 如果状态不一致，返回 false。
            return false;
        }
        return true;
        // 状态一致，返回 true。
    }

    @Override
    public LifeCycle.State getState() {
        // 获取当前生命周期状态。
        return this.state;
        // 返回当前对象的生命周期状态。
    }

    protected int hashCodeImpl() {
        // hashCode 方法的具体实现。
        final int prime = 31;
        // 用于哈希计算的质数。
        int result = 1;
        // 哈希计算的初始结果。
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        // 将当前状态的哈希值加入计算。如果状态为 null，则哈希值为 0。
        return result;
        // 返回计算出的哈希值。
    }

    public boolean isInitialized() {
        // 判断当前对象是否处于 INITIALIZED（已初始化）状态。
        return this.state == LifeCycle.State.INITIALIZED;
        // 如果状态是 INITIALIZED，返回 true，否则返回 false。
    }

    @Override
    public boolean isStarted() {
        // 判断当前对象是否处于 STARTED（已启动）状态。
        return this.state == LifeCycle.State.STARTED;
        // 如果状态是 STARTED，返回 true，否则返回 false。
    }

    public boolean isStarting() {
        // 判断当前对象是否处于 STARTING（正在启动）状态。
        return this.state == LifeCycle.State.STARTING;
        // 如果状态是 STARTING，返回 true，否则返回 false。
    }

    @Override
    public boolean isStopped() {
        // 判断当前对象是否处于 STOPPED（已停止）状态。
        return this.state == LifeCycle.State.STOPPED;
        // 如果状态是 STOPPED，返回 true，否则返回 false。
    }

    public boolean isStopping() {
        // 判断当前对象是否处于 STOPPING（正在停止）状态。
        return this.state == LifeCycle.State.STOPPING;
        // 如果状态是 STOPPING，返回 true，否则返回 false。
    }

    protected void setStarted() {
        // 设置当前生命周期状态为 STARTED（已启动）。
        this.setState(LifeCycle.State.STARTED);
        // 调用 setState 方法将状态设置为 STARTED。
    }

    protected void setStarting() {
        // 设置当前生命周期状态为 STARTING（正在启动）。
        this.setState(LifeCycle.State.STARTING);
        // 调用 setState 方法将状态设置为 STARTING。
    }

    protected void setState(final LifeCycle.State newState) {
        // 设置新的生命周期状态。
        // 参数: newState - 要设置的新状态。
        this.state = newState;
        // 更新当前对象的生命周期状态。
        // Need a better string than this.toString() for the message
        // LOGGER.trace("{} {}", this.state, this);
        // TODO: 需要一个比 this.toString() 更好的字符串用于日志消息。
    }

    protected void setStopped() {
        // 设置当前生命周期状态为 STOPPED（已停止）。
        this.setState(LifeCycle.State.STOPPED);
        // 调用 setState 方法将状态设置为 STOPPED。
    }

    protected void setStopping() {
        // 设置当前生命周期状态为 STOPPING（正在停止）。
        this.setState(LifeCycle.State.STOPPING);
        // 调用 setState 方法将状态设置为 STOPPING。
    }

    @Override
    public void initialize() {
        // 初始化生命周期状态。
        this.state = State.INITIALIZED;
        // 将状态设置为 INITIALIZED。
    }

    @Override
    public void start() {
        // 启动生命周期。
        this.setStarted();
        // 调用 setStarted 方法将状态设置为 STARTED。
    }

    @Override
    public void stop() {
        // 停止生命周期，使用默认的超时时间和单位。
        stop(DEFAULT_STOP_TIMEOUT, DEFAULT_STOP_TIMEUNIT);
        // 调用带超时参数的 stop 方法。
    }

    protected boolean stop(final Future<?> future) {
        // 停止一个异步操作的 Future。
        // 参数: future - 要停止的 Future 对象。
        // 返回值: 如果成功停止或 Future 已经完成/取消，则返回 true；否则返回 false。
        boolean stopped = true;
        // 标记是否成功停止，默认为 true。
        if (future != null) {
            // 如果 Future 不为 null。
            if (future.isCancelled() || future.isDone()) {
                // 如果 Future 已经被取消或已完成，则直接返回 true。
                return true;
            }
            stopped = future.cancel(true);
            // 尝试取消 Future，参数为 true 表示如果可能的话，中断正在执行的任务。
        }
        return stopped;
        // 返回停止结果。
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        // 停止生命周期，并指定超时时间和单位。
        // 参数: timeout - 停止的超时时间。
        // 参数: timeUnit - 超时时间的单位。
        // 返回值: 总是返回 true，表示停止操作被尝试执行。
        this.state = LifeCycle.State.STOPPED;
        // 将状态设置为 STOPPED（已停止）。
        return true;
        // 总是返回 true。
    }

}
