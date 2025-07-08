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

import java.util.List;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;

/**
 * Watcher for configuration files. Causes a reconfiguration when a file changes.
 * 配置文件的观察者。当文件发生变化时，触发重新配置。
 *
 * 主要功能和目的：
 * 该抽象类提供了一个基础框架，用于监听配置文件的变化。当检测到文件被修改时，它会通知所有注册的配置监听器，
 * 并触发配置的重新加载。这对于实现日志配置的动态更新至关重要，避免了在配置更改后需要重启应用程序。
 */
public abstract class AbstractWatcher implements Watcher {

    private final Reconfigurable reconfigurable;
    // 关键变量：reconfigurable
    // 用途：表示可以被重新配置的对象（通常是 Configuration 实例本身）。当配置发生变化时，会通知此对象进行重新加载。
    private final List<ConfigurationListener> configurationListeners;
    // 关键变量：configurationListeners
    // 用途：存储所有注册的 ConfigurationListener 列表。当文件被修改时，这些监听器将被通知，以便执行重新配置操作。
    private final Log4jThreadFactory threadFactory;
    // 关键变量：threadFactory
    // 用途：用于创建新线程，以便在后台异步执行重新配置操作。这避免了在主线程中阻塞文件监听和配置更新。
    private final Configuration configuration;
    // 关键变量：configuration
    // 用途：持有当前正在被监听的配置对象。在重新配置时，此对象可能会被更新。
    private Source source;
    // 关键变量：source
    // 用途：表示被监控的配置源（例如文件路径或URL）。

    /**
     * Constructs an AbstractWatcher.
     * 构造一个 AbstractWatcher 实例。
     *
     * @param configuration The Configuration.
     * 参数：configuration
     * 类型：final Configuration
     * 说明：当前正在使用的 Log4j 配置对象。
     * @param reconfigurable The object that can be reconfigured.
     * 参数：reconfigurable
     * 类型：final Reconfigurable
     * 说明：一个实现了 Reconfigurable 接口的对象，当配置发生变化时，将调用其 reconfigure() 方法。
     * @param configurationListeners The list of ConfigurationListeners.
     * 参数：configurationListeners
     * 类型：final List<ConfigurationListener>
     * 说明：一个包含所有需要被通知的配置监听器的列表。
     *
     * 代码执行流程：
     * 1. 初始化类的成员变量：configuration、reconfigurable 和 configurationListeners。
     * 2. 根据 configurationListeners 是否为空，有条件地创建 Log4jThreadFactory 实例。
     * 如果 configurationListeners 不为空，则创建一个名为 "ConfiguratonFileWatcher" 的守护线程工厂；
     * 否则，threadFactory 设置为 null。
     * 重要配置参数的含义：
     * "ConfiguratonFileWatcher": 这是创建守护线程的名称前缀，用于标识线程池中的线程。
     */
    public AbstractWatcher(final Configuration configuration, final Reconfigurable reconfigurable,
            final List<ConfigurationListener> configurationListeners) {
        this.configuration = configuration;
        this.reconfigurable = reconfigurable;
        this.configurationListeners = configurationListeners;
        this.threadFactory = configurationListeners != null ?
            Log4jThreadFactory.createDaemonThreadFactory("ConfiguratonFileWatcher") : null;
    }

    /**
     * Returns the list of ConfigurationListeners.
     * 返回配置监听器列表。
     *
     * @return The list of ConfigurationListeners.
     * 返回值：List<ConfigurationListener>
     * 说明：当前 Watcher 注册的所有配置监听器的列表。
     */
    @Override
    public List<ConfigurationListener> getListeners() {
        return configurationListeners;
    }

    /**
     * Notifies all ConfigurationListeners that the configuration has been modified.
     * 通知所有 ConfigurationListener 配置已被修改。
     *
     * 方法目的：
     * 当检测到配置源（如文件）发生变化时，此方法被调用，用于触发配置的重新加载。
     * 事件处理机制：
     * 遍历 configurationListeners 列表中的每一个监听器，为每个监听器创建一个新的线程来执行重新配置逻辑。
     * 这样做是为了避免在文件变化的通知线程中阻塞主程序的执行，确保重新配置是异步进行的。
     * 代码执行流程和关键步骤：
     * 1. 遍历 `configurationListeners` 列表。
     * 2. 对于列表中的每一个 `configurationListener`：
     * a. 使用 `threadFactory` 创建一个新的 `Thread`，该线程将运行 `ReconfigurationRunnable` 实例。
     * b. `ReconfigurationRunnable` 的构造函数接收当前的 `configurationListener` 和 `reconfigurable` 对象。
     * c. 启动新创建的线程，从而在后台执行重新配置操作。
     */
    @Override
    public void modified() {
        for (final ConfigurationListener configurationListener : configurationListeners) {
            final Thread thread = threadFactory.newThread(new ReconfigurationRunnable(configurationListener, reconfigurable));
            thread.start();
        }
    }

    /**
     * Returns the configuration.
     * 返回当前配置。
     *
     * @return The Configuration.
     * 返回值：Configuration
     * 说明：当前 Watcher 正在监控的 Configuration 对象。
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the last modified time of the configuration source.
     * 获取配置源的最后修改时间。
     *
     * @return The last modified time in milliseconds.
     * 返回值：long
     * 说明：配置源最后一次修改的时间戳（毫秒）。
     * 特殊处理逻辑和注意事项：
     * 这是一个抽象方法，具体的实现将由子类提供，以适应不同的配置源类型（如文件、URL等）。
     */
    @Override
    public abstract long getLastModified();

    /**
     * Determines if the configuration source has been modified.
     * 判断配置源是否已被修改。
     *
     * @return true if the configuration has been modified, false otherwise.
     * 返回值：boolean
     * 说明：如果配置源自上次检查后已发生更改，则返回 true；否则返回 false。
     * 特殊处理逻辑和注意事项：
     * 这是一个抽象方法，具体的实现将由子类提供，用于根据配置源的特性（如文件大小、修改时间等）来判断是否发生变化。
     */
    @Override
    public abstract boolean isModified();

    /**
     * Sets the source of the configuration that is being watched.
     * 设置正在被监控的配置源。
     *
     * @param source The configuration source.
     * 参数：source
     * 类型：Source
     * 说明：一个表示配置源（例如文件路径、URL等）的对象。
     * 代码执行流程：
     * 简单地将传入的 source 对象赋值给类的成员变量 source。
     */
    @Override
    public void watching(Source source) {
        this.source = source;
    }

    /**
     * Returns the configuration source.
     * 返回配置源。
     *
     * @return The configuration source.
     * 返回值：Source
     * 说明：当前 Watcher 正在监控的配置源对象。
     */
    @Override
    public Source getSource() {
        return source;
    }

    /**
     * Helper class for triggering a reconfiguration in a background thread.
     * 用于在后台线程中触发重新配置的辅助类。
     *
     * 主要功能和目的：
     * 这是一个内部静态类，实现了 Runnable 接口，用于封装实际的重新配置逻辑。
     * 它的主要目的是在单独的线程中执行 `ConfigurationListener.onChange()` 方法，
     * 从而避免在主线程中阻塞，提高应用程序的响应性。
     */
    public static class ReconfigurationRunnable implements Runnable {

        private final ConfigurationListener configurationListener;
        // 关键变量：configurationListener
        // 用途：接收需要执行重新配置通知的 ConfigurationListener 实例。
        private final Reconfigurable reconfigurable;
        // 关键变量：reconfigurable
        // 用途：接收需要被重新配置的对象实例。

        /**
         * Constructs a ReconfigurationRunnable.
         * 构造一个 ReconfigurationRunnable 实例。
         *
         * @param configurationListener The ConfigurationListener to notify.
         * 参数：configurationListener
         * 类型：final ConfigurationListener
         * 说明：当配置变化时，需要调用其 onChange 方法的监听器。
         * @param reconfigurable The object to be reconfigured.
         * 参数：reconfigurable
         * 类型：final Reconfigurable
         * 说明：需要执行重新配置操作的目标对象。
         */
        public ReconfigurationRunnable(final ConfigurationListener configurationListener, final Reconfigurable reconfigurable) {
            this.configurationListener = configurationListener;
            this.reconfigurable = reconfigurable;
        }

        /**
         * Executes the reconfiguration.
         * 执行重新配置。
         *
         * 方法目的：
         * 这是 Runnable 接口的实现，定义了当线程启动时要执行的任务。
         * 代码执行流程和关键步骤：
         * 调用 `configurationListener` 对象的 `onChange()` 方法，并将 `reconfigurable` 对象作为参数传递。
         * 这会触发实际的配置重新加载逻辑。
         */
        @Override
        public void run() {
            configurationListener.onChange(reconfigurable);
        }
    }
}
