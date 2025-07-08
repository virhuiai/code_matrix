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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.config.ConfigurationFileWatcher;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

/**
 * Manages {@link FileWatcher}s.
 * 管理文件观察器。
 *
 * @see FileWatcher
 * @see ConfigurationScheduler
 */
public class WatchManager extends AbstractLifeCycle {

    /**
     * 内部类 ConfigurationMonitor，用于封装配置观察器和上次修改时间。
     */
    private final class ConfigurationMonitor {
        private final Watcher watcher;
        private volatile long lastModifiedMillis;

        /**
         * 构造函数。
         *
         * @param lastModifiedMillis 文件上次修改的时间戳。
         * @param watcher 实际的观察器。
         */
        public ConfigurationMonitor(final long lastModifiedMillis, final Watcher watcher) {
            this.watcher = watcher;
            this.lastModifiedMillis = lastModifiedMillis;
        }

        /**
         * 获取观察器。
         *
         * @return 观察器实例。
         */
        public Watcher getWatcher() {
            return watcher;
        }

        /**
         * 设置文件上次修改的时间戳。
         *
         * @param lastModifiedMillis 文件上次修改的时间戳。
         */
        private void setLastModifiedMillis(final long lastModifiedMillis) {
            this.lastModifiedMillis = lastModifiedMillis;
        }

        /**
         * 转换为字符串表示。
         *
         * @return 字符串表示。
         */
        @Override
        public String toString() {
            return "ConfigurationMonitor [watcher=" + watcher + ", lastModifiedMillis=" + lastModifiedMillis + "]";
        }
    }

    /**
     * 内部静态类 LocalUUID，用于生成UUID。
     */
    private static class LocalUUID {
        private static final long LOW_MASK = 0xffffffffL;
        private static final long MID_MASK = 0xffff00000000L;
        private static final long HIGH_MASK = 0xfff000000000000L;
        private static final int NODE_SIZE = 8;
        private static final int SHIFT_2 = 16;
        private static final int SHIFT_4 = 32;
        private static final int SHIFT_6 = 48;
        private static final int HUNDRED_NANOS_PER_MILLI = 10000;
        private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
        private static final AtomicInteger COUNT = new AtomicInteger(0);
        private static final long TYPE1 = 0x1000L;
        private static final byte VARIANT = (byte) 0x80;
        private static final int SEQUENCE_MASK = 0x3FFF;


        /**
         * 生成并返回一个UUID。
         *
         * @return 生成的UUID。
         */
        public static UUID get() {
            // 计算时间戳，结合当前毫秒时间、UUID纪元以来的100纳秒间隔数和计数器。
            final long time = ((System.currentTimeMillis() * HUNDRED_NANOS_PER_MILLI) +
                    NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) + (COUNT.incrementAndGet() % HUNDRED_NANOS_PER_MILLI);
            // 提取时间戳的低位、中位和高位，并进行位移操作。
            final long timeLow = (time & LOW_MASK) << SHIFT_4;
            final long timeMid = (time & MID_MASK) >> SHIFT_2;
            final long timeHi = (time & HIGH_MASK) >> SHIFT_6;
            // 组合时间戳的各个部分，并设置UUID类型为Type 1。
            final long most = timeLow | timeMid | TYPE1 | timeHi;
            // 使用组合后的时间戳和递增的计数器创建UUID。
            return new UUID(most, COUNT.incrementAndGet());
        }
    }

    /**
     * 内部类 WatchRunnable，实现Runnable接口，用于定时检查文件修改。
     */
    private final class WatchRunnable implements Runnable {

        // Use a hard class reference here in case a refactoring changes the class name.
        // 使用硬编码的类名引用，以防重构导致类名改变。
        private final String SIMPLE_NAME = WatchRunnable.class.getSimpleName();

        /**
         * 运行方法，遍历所有观察器，检查文件是否被修改。
         */
        @Override
        public void run() {
            logger.trace("{} run triggered.", SIMPLE_NAME);
            // 遍历所有观察器条目。
            for (final Map.Entry<Source, ConfigurationMonitor> entry : watchers.entrySet()) {
                final Source source = entry.getKey();
                final ConfigurationMonitor monitor = entry.getValue();
                // 如果观察器检测到文件被修改。
                if (monitor.getWatcher().isModified()) {
                    final long lastModified = monitor.getWatcher().getLastModified();
                    // 记录文件修改信息。
                    if (logger.isInfoEnabled()) {
                        logger.info("Source '{}' was modified on {} ({}), previous modification was on {} ({})", source,
                            millisToString(lastModified), lastModified, millisToString(monitor.lastModifiedMillis),
                            monitor.lastModifiedMillis);
                    }
                    // 更新上次修改时间并通知观察器文件已被修改。
                    monitor.lastModifiedMillis = lastModified;
                    monitor.getWatcher().modified();
                }
            }
            logger.trace("{} run ended.", SIMPLE_NAME);
        }
    }
    private static Logger logger = StatusLogger.getLogger(); // 日志记录器实例。
    private final ConcurrentMap<Source, ConfigurationMonitor> watchers = new ConcurrentHashMap<>(); // 存储所有被观察的源及其监视器。
    private int intervalSeconds = 0; // 检查文件修改的间隔时间（秒）。
    private ScheduledFuture<?> future; // 定时任务的Future对象。

    private final ConfigurationScheduler scheduler; // 配置调度器，用于安排定时任务。

    private final List<WatchEventService> eventServiceList; // 观察事件服务列表。

    // This just needs to be a unique key within the WatchEventManager.
    // 这只需要在 WatchEventManager 中是唯一的键。
    private final UUID id = LocalUUID.get(); // 当前WatchManager实例的唯一标识符。

    /**
     * 构造函数。
     *
     * @param scheduler 配置调度器。
     */
    public WatchManager(final ConfigurationScheduler scheduler) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        eventServiceList = getEventServices(); // 获取所有可用的观察事件服务。
    }

    /**
     * 检查文件。
     * 立即执行一次文件修改检查。
     */
    public void checkFiles() {
        new WatchRunnable().run();
    }

    /**
     * Return the ConfigurationWaatchers.
     * 返回配置观察器。
     *
     * @return the ConfigurationWatchers.
     * 配置观察器。
     * @since 2.11.2
     */
    public Map<Source, Watcher> getConfigurationWatchers() {
        final Map<Source, Watcher> map = new HashMap<>(watchers.size());
        // 遍历内部存储的监视器，并将其转换为Source到Watcher的映射。
        for (final Map.Entry<Source, ConfigurationMonitor> entry : watchers.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getWatcher());
        }
        return map;
    }

    /**
     * 获取所有观察事件服务。
     *
     * @return 观察事件服务列表。
     */
    private List<WatchEventService> getEventServices() {
        List<WatchEventService> list = new ArrayList<>();
        // 遍历所有类加载器。
        for (final ClassLoader classLoader : LoaderUtil.getClassLoaders()) {
            try {
                // 通过ServiceLoader加载WatchEventService服务。
                final ServiceLoader<WatchEventService> serviceLoader = ServiceLoader
                    .load(WatchEventService.class, classLoader);
                // 将加载到的服务添加到列表中。
                for (final WatchEventService service : serviceLoader) {
                    list.add(service);
                }
            } catch (final Throwable ex) {
                // 记录无法从ClassLoader获取WatchEventService的调试信息。
                LOGGER.debug("Unable to retrieve WatchEventService from ClassLoader {}", classLoader, ex);
            }
        }
        return list;
    }

    /**
     * 获取当前WatchManager实例的唯一ID。
     *
     * @return UUID。
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Gets how often this manager checks for file modifications.
     * 获取此管理器检查文件修改的频率。
     *
     * @return how often, in seconds, this manager checks for file modifications.
     * 此管理器检查文件修改的频率，以秒为单位。
     */
    public int getIntervalSeconds() {
        return this.intervalSeconds;
    }

    /**
     * Returns a Map of the file watchers.
     * 返回文件观察器的映射。
     *
     * @return A Map of the file watchers.
     * 文件观察器的映射。
     * @deprecated use getConfigurationWatchers.
     * 请使用 getConfigurationWatchers 方法。
     */
    @Deprecated
    public Map<File, FileWatcher> getWatchers() {
        final Map<File, FileWatcher> map = new HashMap<>(watchers.size());
        for (Map.Entry<Source, ConfigurationMonitor> entry : watchers.entrySet()) {
            // 如果观察器是ConfigurationFileWatcher类型，则直接放入映射。
            if (entry.getValue().getWatcher() instanceof ConfigurationFileWatcher) {
                map.put(entry.getKey().getFile(), (FileWatcher) entry.getValue().getWatcher());
            } else {
                // 否则，将其包装成WrappedFileWatcher后放入映射。
                map.put(entry.getKey().getFile(), new WrappedFileWatcher((FileWatcher) entry.getValue().getWatcher()));
            }
        }
        return map;
    }

    /**
     * 检查是否有事件监听器。
     *
     * @return 如果有事件监听器则返回true，否则返回false。
     */
    public boolean hasEventListeners() {
        return eventServiceList.size() > 0;
    }

    /**
     * 将毫秒时间戳转换为字符串表示的日期。
     *
     * @param millis 毫秒时间戳。
     * @return 日期字符串。
     */
    private String millisToString(final long millis) {
        return new Date(millis).toString();
    }

    /**
     * Resets all file monitors to their current last modified time. If this manager does not watch any file, nothing
     * happens.
     * 重置所有文件监视器到它们当前的最后修改时间。如果此管理器没有监视任何文件，则不执行任何操作。
     * <p>
     * This allows you to start, stop, reset and start again a manager, without triggering file modified events if the a
     * watched file has changed during the period of time when the manager was stopped.
     * 这允许您启动、停止、重置并再次启动管理器，而不会在管理器停止期间被监视的文件发生更改时触发文件修改事件。
     * </p>
     *
     * @since 2.11.0
     */
    public void reset() {
        logger.debug("Resetting {}", this);
        // 遍历所有被观察的源，并逐一重置。
        for (final Source source : watchers.keySet()) {
            reset(source);
        }
    }

    /**
     * Resets the file monitor for the given file being watched to its current last modified time. If this manager does
     * not watch the given file, nothing happens.
     * 将给定被监视文件的文件监视器重置为其当前的最后修改时间。如果此管理器没有监视给定文件，则不执行任何操作。
     * <p>
     * This allows you to start, stop, reset and start again a manager, without triggering file modified events if the
     * given watched file has changed during the period of time when the manager was stopped.
     * 这允许您启动、停止、重置并再次启动管理器，而不会在管理器停止期间被监视文件发生更改时触发文件修改事件。
     * </p>
     *
     * @param file the file for the monitor to reset.
     * 要重置监视器的文件。
     * @since 2.11.0
     */
    public void reset(final File file) {
        if (file == null) {
            return;
        }
        Source source = new Source(file);
        reset(source);
    }

    /**
     * Resets the configuration monitor for the given file being watched to its current last modified time. If this
     * manager does not watch the given configuration, nothing happens.
     * 将给定被监视配置的配置监视器重置为其当前的最后修改时间。如果此管理器没有监视给定配置，则不执行任何操作。
     * <p>
     * This allows you to start, stop, reset and start again a manager, without triggering file modified events if the
     * given watched configuration has changed during the period of time when the manager was stopped.
     * 这允许您启动、停止、重置并再次启动管理器，而不会在管理器停止期间被监视配置发生更改时触发文件修改事件。
     * </p>
     *
     * @param source the Source for the monitor to reset.
     * 要重置监视器的源。
     * @since 2.12.0
     */
    public void reset(final Source source) {
        if (source == null) {
            return;
        }
        final ConfigurationMonitor monitor = watchers.get(source);
        if (monitor != null) {
            Watcher watcher = monitor.getWatcher();
            // 如果观察器检测到文件被修改。
            if (watcher.isModified()) {
                final long lastModifiedMillis = watcher.getLastModified();
                // 记录重置信息。
                if (logger.isDebugEnabled()) {
                    logger.debug("Resetting file monitor for '{}' from {} ({}) to {} ({})", source.getLocation(),
                        millisToString(monitor.lastModifiedMillis), monitor.lastModifiedMillis,
                        millisToString(lastModifiedMillis), lastModifiedMillis);
                }
                // 更新监视器的上次修改时间。
                monitor.setLastModifiedMillis(lastModifiedMillis);
            }
        }
    }

    /**
     * 设置检查文件修改的间隔时间。
     *
     * @param intervalSeconds 间隔时间，以秒为单位。
     */
    public void setIntervalSeconds(final int intervalSeconds) {
        // 只有在管理器未启动时才能设置间隔时间。
        if (!isStarted()) {
            if (this.intervalSeconds > 0 && intervalSeconds == 0) {
                scheduler.decrementScheduledItems(); // 如果从有间隔变为无间隔，减少调度项。
            } else if (this.intervalSeconds == 0 && intervalSeconds > 0) {
                scheduler.incrementScheduledItems(); // 如果从无间隔变为有间隔，增加调度项。
            }
            this.intervalSeconds = intervalSeconds;
        }
    }

    /**
     * 启动WatchManager。
     * 如果设置了间隔时间，则调度一个定时任务来定期检查文件修改。
     * 订阅所有可用的观察事件服务。
     */
    @Override
    public void start() {
        super.start();

        if (intervalSeconds > 0) {
            // 调度定时任务，以固定的延迟周期性执行WatchRunnable。
            future = scheduler
                .scheduleWithFixedDelay(new WatchRunnable(), intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        }
        // 订阅所有观察事件服务。
        for (WatchEventService service : eventServiceList) {
            service.subscribe(this);
        }
    }

    /**
     * 停止WatchManager。
     * 退订所有观察事件服务并取消定时任务。
     *
     * @param timeout 超时时间。
     * @param timeUnit 超时时间的单位。
     * @return 如果成功停止则返回true，否则返回false。
     */
    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        setStopping(); // 设置生命周期状态为停止中。
        // 退订所有观察事件服务。
        for (WatchEventService service : eventServiceList) {
            service.unsubscribe(this);
        }
        final boolean stopped = stop(future); // 停止定时任务。
        setStopped(); // 设置生命周期状态为已停止。
        return stopped;
    }

    /**
     * 返回WatchManager的字符串表示。
     *
     * @return 字符串表示。
     */
    @Override
    public String toString() {
        return "WatchManager [intervalSeconds=" + intervalSeconds + ", watchers=" + watchers + ", scheduler="
            + scheduler + ", future=" + future + "]";
    }

    /**
     * Unwatches the given file.
     * 停止监视给定文件。
     *
     * @param source the Source to stop watching.
     *               the file to stop watching.
     * 要停止监视的源。
     * @since 2.12.0
     */
    public void unwatch(final Source source) {
        logger.debug("Unwatching configuration {}", source);
        watchers.remove(source); // 从观察器映射中移除指定的源。
    }

    /**
     * Unwatches the given file.
     * 停止监视给定文件。
     *
     * @param file the file to stop watching.
     * 要停止监视的文件。
     * @since 2.11.0
     */
    public void unwatchFile(final File file) {
        Source source = new Source(file); // 根据文件创建源对象。
        unwatch(source); // 调用unwatch方法停止监视。
    }

    /**
     * Watches the given file.
     * 监视给定文件。
     *
     * @param source  the source to watch.
     * 要监视的源。
     * @param watcher the watcher to notify of file changes.
     * 文件更改时通知的观察器。
     */
    public void watch(final Source source, final Watcher watcher) {
        watcher.watching(source); // 通知观察器它正在监视指定的源。
        final long lastModified = watcher.getLastModified(); // 获取源的最后修改时间。
        // 记录调试信息。
        if (logger.isDebugEnabled()) {
            logger.debug("Watching configuration '{}' for lastModified {} ({})", source, millisToString(lastModified),
                lastModified);
        }
        // 将源和ConfigurationMonitor添加到观察器映射中。
        watchers.put(source, new ConfigurationMonitor(lastModified, watcher));
    }

    /**
     * Watches the given file.
     * 监视给定文件。
     *
     * @param file        the file to watch.
     * 要监视的文件。
     * @param fileWatcher the watcher to notify of file changes.
     * 文件更改时通知的观察器。
     */
    public void watchFile(final File file, final FileWatcher fileWatcher) {
        Watcher watcher;
        // 如果文件观察器是Watcher类型，则直接使用。
        if (fileWatcher instanceof Watcher) {
            watcher = (Watcher) fileWatcher;
        } else {
            // 否则，将其包装成WrappedFileWatcher。
            watcher = new WrappedFileWatcher(fileWatcher);
        }
        Source source = new Source(file); // 根据文件创建源对象。
        watch(source, watcher); // 调用watch方法进行监视。
    }
}
