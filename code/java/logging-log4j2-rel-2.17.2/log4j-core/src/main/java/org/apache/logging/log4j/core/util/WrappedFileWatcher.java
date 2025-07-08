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

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;

/**
 *
 */
/**
 * 文件观察者的包装类。
 * 该类扩展了 AbstractWatcher 并实现了 FileWatcher 接口，用于包装另一个 FileWatcher 实例。
 */
public class WrappedFileWatcher extends AbstractWatcher implements FileWatcher {

    private final FileWatcher watcher;
    // 实际被包装的文件观察者实例
    // 这个变量用于存储上一次文件修改的时间戳（毫秒）
    private volatile long lastModifiedMillis;

    /**
     * 构造函数。
     * @param watcher 实际的文件观察者实例。
     * @param configuration 配置对象。
     * @param reconfigurable 可重新配置对象。
     * @param configurationListeners 配置监听器列表。
     * @param lastModifiedMillis 文件最后修改时间戳。
     */
    public WrappedFileWatcher(FileWatcher watcher, final Configuration configuration,
        final Reconfigurable reconfigurable, final List<ConfigurationListener> configurationListeners,
        final long lastModifiedMillis) {
        // 调用父类构造函数初始化配置、可重新配置对象和配置监听器
        super(configuration, reconfigurable, configurationListeners);
        // 初始化被包装的 FileWatcher 实例
        this.watcher = watcher;
        // 初始化文件最后修改时间
        this.lastModifiedMillis = lastModifiedMillis;
    }

    /**
     * 构造函数（简化版）。
     * @param watcher 实际的文件观察者实例。
     */
    public WrappedFileWatcher(FileWatcher watcher) {
        // 调用父类构造函数，传入 null，表示没有配置、可重新配置对象和监听器
        super(null, null, null);
        // 初始化被包装的 FileWatcher 实例
        this.watcher = watcher;
    }

    @Override
    public long getLastModified() {
        // 返回文件最后修改的时间戳
        return lastModifiedMillis;
    }

    @Override
    public void fileModified(File file) {
        // 调用被包装的 FileWatcher 实例的 fileModified 方法
        watcher.fileModified(file);
    }

    @Override
    public boolean isModified() {
        // 获取源文件的最后修改时间
        long lastModified = getSource().getFile().lastModified();
        // 比较当前文件修改时间与记录的最后修改时间
        if (lastModifiedMillis != lastModified) {
            // 如果不同，更新记录的最后修改时间
            lastModifiedMillis = lastModified;
            // 返回 true 表示文件已修改
            return true;
        }
        // 返回 false 表示文件未修改
        return false;
    }

    @Override
    public List<ConfigurationListener> getListeners() {
        // 检查父类是否有监听器
        if (super.getListeners() != null) {
            // 如果有，返回一个不可修改的监听器列表
            return Collections.unmodifiableList(super.getListeners());
        } else {
            // 否则返回 null
            return null;
        }
    }

    @Override
    public void modified() {
        // 检查是否有监听器
        if (getListeners() != null) {
            // 如果有，调用父类的 modified 方法触发监听器
            super.modified();
        }
        // 调用 fileModified 方法通知文件已修改
        fileModified(getSource().getFile());
        // 更新记录的最后修改时间为当前文件的最新修改时间
        lastModifiedMillis = getSource().getFile().lastModified();
    }

    @Override
    public void watching(Source source) {
        // 设置文件最后修改时间为源文件的最后修改时间
        lastModifiedMillis = source.getFile().lastModified();
        // 调用父类的 watching 方法
        super.watching(source);
    }

    @Override
    public Watcher newWatcher(final Reconfigurable reconfigurable, final List<ConfigurationListener> listeners,
        long lastModifiedMillis) {
        // 创建一个新的 WrappedFileWatcher 实例，并传入当前实例的 watcher
        WrappedFileWatcher watcher = new WrappedFileWatcher(this.watcher, getConfiguration(), reconfigurable, listeners,
            lastModifiedMillis);
        // 如果有源文件，则设置新观察者的源文件
        if (getSource() != null) {
            watcher.watching(getSource());
        }
        // 返回新创建的观察者实例
        return watcher;
    }
}
