
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

/**
 * All proper Java frameworks implement some sort of object life cycle. In Log4j, the main interface for handling
 * the life cycle context of an object is this one. An object first starts in the {@link State#INITIALIZED} state
 * by default to indicate the class has been loaded. From here, calling the {@link #start()} method will change this
 * state to {@link State#STARTING}. After successfully being started, this state is changed to {@link State#STARTED}.
 * When the {@link #stop()} is called, this goes into the {@link State#STOPPING} state. After successfully being
 * stopped, this goes into the {@link State#STOPPED} state. In most circumstances, implementation classes should
 * store their {@link State} in a {@code volatile} field or inside an
 * {@link java.util.concurrent.atomic.AtomicReference} dependent on synchronization and concurrency requirements.
 * 所有规范的Java框架都会实现某种对象生命周期管理。在Log4j中，处理对象生命周期上下文的主要接口就是这个LifeCycle接口。
 * 对象默认首先处于 {@link State#INITIALIZED} 状态，表示类已加载。
 * 之后，调用 {@link #start()} 方法会将其状态更改为 {@link State#STARTING}。
 * 成功启动后，状态变为 {@link State#STARTED}。
 * 当调用 {@link #stop()} 方法时，状态进入 {@link State#STOPPING}。
 * 成功停止后，状态变为 {@link State#STOPPED}。
 * 在大多数情况下，实现类应将其 {@link State} 存储在 {@code volatile} 字段或
 * {@link java.util.concurrent.atomic.AtomicReference} 中，具体取决于同步和并发要求。
 *
 * @see AbstractLifeCycle
 */
public interface LifeCycle {

    /**
     * Status of a life cycle like a {@link LoggerContext}.
     * 生命周期状态，例如 {@link LoggerContext} 的状态。
     */
    enum State {
        /** Object is in its initial state and not yet initialized. */
        INITIALIZING,
        /** 对象处于其初始状态，尚未初始化。*/
        /** Initialized but not yet started. */
        INITIALIZED,
        /** 已初始化但尚未启动。*/
        /** In the process of starting. */
        STARTING,
        /** 正在启动过程中。*/
        /** Has started. */
        STARTED,
        /** 已启动。*/
        /** Stopping is in progress. */
        STOPPING,
        /** 正在停止过程中。*/
        /** Has stopped. */
        STOPPED
        /** 已停止。*/
    }

    /**
     * Gets the life-cycle state.
     * 获取生命周期状态。
     *
     * @return the life-cycle state
     * @return 生命周期的当前状态
     */
    State getState();

    /**
     * Initializes the component. This method is called once after the component is constructed.
     * 初始化组件。此方法在组件构造后只被调用一次。
     *
     * 该方法通常用于执行一次性设置，例如资源分配或内部状态准备，但不会启动任何活动操作。
     * 实际的启动逻辑应在 {@link #start()} 方法中实现。
     */
    void initialize();

    /**
     * Starts the component. This method is called after {@link #initialize()} and puts the component into a running state.
     * 启动组件。此方法在 {@link #initialize()} 后调用，使组件进入运行状态。
     *
     * 当组件成功启动后，其状态应变为 {@link State#STARTED}。
     * 如果组件已经在运行中，再次调用此方法不应引起错误，但可能没有实际操作。
     */
    void start();

    /**
     * Stops the component. This method is called to gracefully shut down the component and release its resources.
     * 停止组件。此方法用于优雅地关闭组件并释放其资源。
     *
     * 当组件开始停止时，其状态应变为 {@link State#STOPPING}，并在成功停止后变为 {@link State#STOPPED}。
     * 如果组件已经停止，再次调用此方法不应引起错误，但可能没有实际操作。
     */
    void stop();

    /**
     * Checks if the component has been started.
     * 检查组件是否已经启动。
     *
     * @return true if the component is in the {@link State#STARTED} state, false otherwise.
     * @return 如果组件处于 {@link State#STARTED} 状态，则返回 true；否则返回 false。
     */
    boolean isStarted();

    /**
     * Checks if the component has been stopped.
     * 检查组件是否已经停止。
     *
     * @return true if the component is in the {@link State#STOPPED} state, false otherwise.
     * @return 如果组件处于 {@link State#STOPPED} 状态，则返回 true；否则返回 false。
     */
    boolean isStopped();

}
