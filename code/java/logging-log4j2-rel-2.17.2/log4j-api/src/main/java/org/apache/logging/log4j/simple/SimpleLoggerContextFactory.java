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
package org.apache.logging.log4j.simple;

import java.net.URI;

import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

/**
 * Simple and stateless {@link LoggerContextFactory}.
 */
// 简易且无状态的日志上下文工厂类，用于创建和管理日志上下文。
// 主要功能：提供单一的日志上下文实例，简化日志系统的配置和使用。
public class SimpleLoggerContextFactory implements LoggerContextFactory {

    /**
     * Singleton instance.
     */
    // 单例实例，定义一个静态的工厂实例以确保全局唯一性。
    public static final SimpleLoggerContextFactory INSTANCE = new SimpleLoggerContextFactory();

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final Object externalContext, final boolean currentContext) {
        // 获取日志上下文实例。
        // 参数说明：
        // - fqcn: 调用者的完全限定类名，用于日志记录的上下文标识。
        // - loader: 类加载器，用于加载日志相关资源（在此实现中未使用）。
        // - externalContext: 外部上下文对象，通常用于传递额外的环境信息（在此实现中未使用）。
        // - currentContext: 是否使用当前上下文的标志（在此实现中忽略）。
        // 返回值：始终返回单例的 SimpleLoggerContext 实例。
        // 执行流程：直接返回预定义的单例日志上下文，忽略输入参数。
        return SimpleLoggerContext.INSTANCE;
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final Object externalContext, final boolean currentContext,
        final URI configLocation, final String name) {
        // 获取日志上下文实例（重载方法，包含配置位置和名称参数）。
        // 参数说明：
        // - fqcn: 调用者的完全限定类名，用于日志记录的上下文标识。
        // - loader: 类加载器，用于加载日志相关资源（在此实现中未使用）。
        // - externalContext: 外部上下文对象，通常用于传递额外的环境信息（在此实现中未使用）。
        // - currentContext: 是否使用当前上下文的标志（在此实现中忽略）。
        // - configLocation: 日志配置文件的位置（URI格式），在此实现中未使用。
        // - name: 日志上下文的名称（在此实现中未使用）。
        // 返回值：始终返回单例的 SimpleLoggerContext 实例。
        // 执行流程：与第一个 getContext 方法相同，直接返回单例日志上下文，配置参数被忽略。
        // 注意事项：此方法为接口要求实现，但不处理配置参数，保持简单性。
        return SimpleLoggerContext.INSTANCE;
    }

    @Override
    public void removeContext(final LoggerContext removeContext) {
        // 删除指定的日志上下文。
        // 参数说明：
        // - removeContext: 需要删除的日志上下文对象。
        // 执行流程：此方法为空实现，因为 SimpleLoggerContext 是单例且无状态的，无需删除操作。
        // 注意事项：方法保留为空以符合接口要求，但不执行任何实际操作。
        // do nothing
    }

    @Override
    public boolean isClassLoaderDependent() {
        // 判断工厂是否依赖于类加载器。
        // 返回值：始终返回 false，表示此工厂不依赖类加载器。
        // 执行流程：直接返回 false，表明日志上下文的创建与类加载器无关。
        // 注意事项：此实现简化了日志系统的依赖管理，适合轻量级日志场景。
        return false;
    }
}
