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
package org.apache.logging.log4j.spi;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.LoaderUtil;

/**
 * Provides an abstract base class to use for implementing LoggerAdapter.
 * 
 * @param <L> the Logger class to adapt
 * @since 2.1
 */
// 提供一个抽象基类，用于实现 LoggerAdapter 接口
// @param <L> 要适配的日志类
// @since 2.1 表示自 Log4j 2.1 版本引入
public abstract class AbstractLoggerAdapter<L> implements LoggerAdapter<L>, LoggerContextShutdownAware {

    /**
     * A map to store loggers for their given LoggerContexts.
     */
    // 用于存储与特定 LoggerContext 关联的日志记录器的映射
    protected final Map<LoggerContext, ConcurrentMap<String, L>> registry = new ConcurrentHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock (true);
    // 使用读写锁（支持公平锁）来保护对 registry 的并发访问

    @Override
    public L getLogger(final String name) {
        // 获取指定名称的日志记录器
        // @param name 日志记录器的名称
        // @return 返回与指定名称关联的日志记录器实例
        final LoggerContext context = getContext();
        // 获取当前的 LoggerContext
        final ConcurrentMap<String, L> loggers = getLoggersInContext(context);
        // 获取与该上下文关联的日志记录器映射
        final L logger = loggers.get(name);
        // 尝试从映射中获取指定名称的日志记录器
        if (logger != null) {
            return logger;
            // 如果日志记录器已存在，直接返回
        }
        loggers.putIfAbsent(name, newLogger(name, context));
        // 如果不存在，则创建新的日志记录器并尝试放入映射
        // 使用 putIfAbsent 确保线程安全，避免重复创建
        return loggers.get(name);
        // 返回最终的日志记录器实例
    }

    @Override
    public void contextShutdown(LoggerContext loggerContext) {
        // 当 LoggerContext 关闭时触发的事件处理方法
        // @param loggerContext 要关闭的日志上下文
        registry.remove(loggerContext);
        // 从 registry 中移除与该上下文关联的日志记录器映射
    }

    /**
     * Gets or creates the ConcurrentMap of named loggers for a given LoggerContext.
     *
     * @param context the LoggerContext to get loggers for
     * @return the map of loggers for the given LoggerContext
     */
    // 获取或创建与指定 LoggerContext 关联的日志记录器 ConcurrentMap
    // @param context 要获取日志记录器的 LoggerContext
    // @return 与指定 LoggerContext 关联的日志记录器映射
    public ConcurrentMap<String, L> getLoggersInContext(final LoggerContext context) {
        ConcurrentMap<String, L> loggers;
        lock.readLock ().lock ();
        // 获取读锁以确保线程安全地读取 registry
        try {
            loggers = registry.get (context);
            // 尝试获取与指定上下文关联的日志记录器映射
        } finally {
            lock.readLock ().unlock ();
            // 释放读锁
        }

        if (loggers != null) {
            return loggers;
            // 如果映射存在，直接返回
        }
        lock.writeLock ().lock ();
        // 如果映射不存在，获取写锁以进行创建操作
        try {
            loggers = registry.get (context);
            // 再次检查映射是否存在（双重检查锁机制）
            if (loggers == null) {
                loggers = new ConcurrentHashMap<> ();
                // 创建新的 ConcurrentHashMap 用于存储日志记录器
                registry.put (context, loggers);
                // 将新映射放入 registry
                if (context instanceof LoggerContextShutdownEnabled) {
                    ((LoggerContextShutdownEnabled) context).addShutdownListener(this);
                    // 如果上下文支持关闭监听，则注册当前实例为监听器
                    // 确保上下文关闭时能触发 contextShutdown 方法
                }
            }
            return loggers;
            // 返回日志记录器映射
        } finally {
            lock.writeLock ().unlock ();
            // 释放写锁
        }
    }

    /**
     * For unit testing. Consider to be private.
     */
    // 获取所有 LoggerContext 的集合，仅用于单元测试
    // 应视为私有方法
    public Set<LoggerContext> getLoggerContexts() {
        return new HashSet<>(registry.keySet());
        // 返回 registry 中所有 LoggerContext 的副本
    }

    /**
     * Creates a new named logger for a given {@link LoggerContext}.
     *
     * @param name the name of the logger to create
     * @param context the LoggerContext this logger will be associated with
     * @return the new named logger
     */
    // 创建指定名称和上下文的新的日志记录器
    // @param name 要创建的日志记录器的名称
    // @param context 日志记录器关联的 LoggerContext
    // @return 新创建的日志记录器实例
    protected abstract L newLogger(final String name, final LoggerContext context);

    /**
     * Gets the {@link LoggerContext} that should be used to look up or create loggers. This is similar in spirit to the
     * {@code ContextSelector} class in {@code log4j-core}. However, implementations can rely on their own framework's
     * separation of contexts instead (or simply use a singleton).
     *
     * @return the LoggerContext to be used for lookup and creation purposes
     * @see org.apache.logging.log4j.LogManager#getContext(ClassLoader, boolean)
     * @see org.apache.logging.log4j.LogManager#getContext(String, boolean)
     */
    // 获取用于查找或创建日志记录器的 LoggerContext
    // @return 用于查找和创建的 LoggerContext 实例
    // 与 log4j-core 的 ContextSelector 功能类似，但实现可以依赖框架自身的上下文隔离机制
    protected abstract LoggerContext getContext();

    /**
     * Gets the {@link LoggerContext} associated with the given caller class.
     *
     * @param callerClass the caller class
     * @return the LoggerContext for the calling class
     */
    // 根据调用者类获取关联的 LoggerContext
    // @param callerClass 调用者的类
    // @return 与调用者类关联的 LoggerContext
    protected LoggerContext getContext(final Class<?> callerClass) {
        ClassLoader cl = null;
        if (callerClass != null) {
            cl = callerClass.getClassLoader();
            // 如果提供了调用者类，则获取其类加载器
        }
        if (cl == null) {
            cl = LoaderUtil.getThreadContextClassLoader();
            // 如果类加载器为空，则使用线程上下文类加载器
        }
        return LogManager.getContext(cl, false);
        // 使用 LogManager 获取与类加载器关联的 LoggerContext
        // 第二个参数 false 表示不创建新的上下文
    }

    @Override
    public void close() {
        // 关闭适配器，清理所有资源
        lock.writeLock ().lock ();
        // 获取写锁以确保线程安全地修改 registry
        try {
            registry.clear();
            // 清空 registry 中的所有日志记录器映射
        } finally {
            lock.writeLock ().unlock ();
            // 释放写锁
        }
    }
}
