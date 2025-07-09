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
package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;

/**
 * Returns either this Thread's context or the default LoggerContext.
 * 返回当前线程的LoggerContext或者默认的LoggerContext。
 *
 * 这个类是Log4j的核心组件之一，用于选择合适的LoggerContext。
 * 它的主要功能是提供一个基本的上下文选择器，如果线程中有绑定上下文则返回线程上下文，否则返回一个默认的全局上下文。
 */
public class BasicContextSelector implements ContextSelector {

    // CONTEXT 是一个静态的最终LoggerContext实例，它被命名为“Default”。
    // 这个实例在类加载时就被初始化，并且在整个应用程序的生命周期中都可用。
    private static final LoggerContext CONTEXT = new LoggerContext("Default");
    // CONTEXT是一个静态常量，代表默认的LoggerContext实例。
    // 它的目的是作为当线程没有绑定特定LoggerContext时的备用LoggerContext。

    @Override
    public void shutdown(String fqcn, ClassLoader loader, boolean currentContext, boolean allContexts) {
        // 关闭给定的LoggerContext。
        // 获取与当前线程、FQCN和类加载器相关的LoggerContext。
        LoggerContext ctx = getContext(fqcn, loader, currentContext);
        // 检查获取到的LoggerContext是否不为空且已经启动。
        if (ctx != null && ctx.isStarted()) {
            // 如果满足条件，则停止该LoggerContext，并设置默认停止超时时间为毫秒。
            ctx.stop(DEFAULT_STOP_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean hasContext(String fqcn, ClassLoader loader, boolean currentContext) {
        // 检查是否存在与当前线程、FQCN和类加载器相关的LoggerContext。
        LoggerContext ctx = getContext(fqcn, loader, currentContext);
        // 返回该LoggerContext是否不为空且已启动。
        return ctx != null && ctx.isStarted();
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        // 从当前线程的ContextAnchor中获取LoggerContext。
        // ContextAnchor.THREAD_CONTEXT是一个ThreadLocal变量，用于存储每个线程的LoggerContext。
        final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        // 如果线程上下文不为空，则返回线程上下文，否则返回默认的LoggerContext。
        // 这个方法是该选择器的核心逻辑，优先使用线程绑定的上下文，如果没有则回退到全局默认上下文。
        return ctx != null ? ctx : CONTEXT;
    }


    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext,
                                    final URI configLocation) {
        // 从当前线程的ContextAnchor中获取LoggerContext。
        // 这里的逻辑与上一个getContext方法相同，尽管多了一个configLocation参数，但在此实现中并未用到。
        final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        // 如果线程上下文不为空，则返回线程上下文，否则返回默认的LoggerContext。
        return ctx != null ? ctx : CONTEXT;
    }

    public LoggerContext locateContext(final String name, final String configLocation) {
        // 定位并返回一个LoggerContext。
        // 在BasicContextSelector中，无论传入什么参数，都始终返回默认的LoggerContext。
        // 这表明它不根据名称或配置位置来创建或查找不同的上下文。
        return CONTEXT;
    }

    @Override
    public void removeContext(final LoggerContext context) {
        // 从选择器中移除一个LoggerContext。
        // 在BasicContextSelector中，此方法不执行任何操作，因为LoggerContexts不是由选择器管理的，也无法被移除。
        // 默认的LoggerContext是静态的，线程绑定的上下文由线程自身管理。
        // does not remove anything
    }

    @Override
    public boolean isClassLoaderDependent() {
        // 指示此上下文选择器是否依赖于类加载器。
        // 返回false表示该选择器不依赖于类加载器来区分或管理LoggerContexts。
        // 无论哪个类加载器请求上下文，它都提供相同（或基于线程）的上下文。
        return false;
    }

    @Override
    public List<LoggerContext> getLoggerContexts() {
        // 获取所有可用的LoggerContexts列表。
        final List<LoggerContext> list = new ArrayList<>();
        // 将默认的LoggerContext添加到列表中。
        list.add(CONTEXT);
        // 返回一个不可修改的列表，包含所有已知的LoggerContexts。
        // 在这个基本实现中，只有默认的LoggerContext是可见的。
        return Collections.unmodifiableList(list);
    }

}
