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

import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;

/**
 * Watches for changes in a Source and performs an action when it is modified.
 * 监视 Source 的变化并在其被修改时执行相应的动作。
 *
 * @see WatchManager
 */
public interface Watcher {

    String CATEGORY = "Watcher";
    // 定义Watcher的类别字符串。
    String ELEMENT_TYPE = "watcher";
    // 定义Watcher的元素类型字符串。

    /**
     * Returns the list of listeners for this configuration.
     * 返回此配置的监听器列表。
     * @return The list of listeners.
     * 监听器列表。
     */
    List<ConfigurationListener> getListeners();

    /**
     * Called when the configuration has been modified.
     * 当配置被修改时调用。
     */
    void modified();

    /**
     * Periodically called to determine if the configuration has been modified.
     * 周期性地调用以确定配置是否已被修改。
     * @return true if the configuration was modified, false otherwise.
     * 如果配置已修改则返回 true，否则返回 false。
     */
    boolean isModified();

    /**
     * Returns the time the source was last modified or 0 if it is not available.
     * 返回源最后修改的时间，如果不可用则返回 0。
     * @return the time the source was last modified.
     * 源最后修改的时间。
     */
    long getLastModified();

    /**
     * Called when the Watcher is registered.
     * 当 Watcher 被注册时调用。
     * @param source the Source that is being watched.
     * 正在被监视的 Source。
     */
    void watching(Source source);

    /**
     * Returns the Source being monitored.
     * 返回正在被监视的 Source。
     * @return the Source.
     * Source 对象。
     */
    Source getSource();

    /**
     * Creates a new Watcher by copying the original and using the new Reconfigurable and listeners.
     * 通过复制原始 Watcher 并使用新的 Reconfigurable 和监听器来创建一个新的 Watcher。
     * @param reconfigurable The Reconfigurable.
     * 可重新配置的实例。
     * @param listeners the listeners.
     * 监听器列表。
     * @param lastModifiedMillis The time the resource was last modified in milliseconds.
     * 资源最后修改的时间，单位毫秒。
     * @return A new Watcher.
     * 一个新的 Watcher 实例。
     */
    Watcher newWatcher(Reconfigurable reconfigurable, List<ConfigurationListener> listeners, long lastModifiedMillis);
}
