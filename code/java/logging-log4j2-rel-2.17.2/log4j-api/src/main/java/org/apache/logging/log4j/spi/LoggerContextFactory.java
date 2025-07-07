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

import java.net.URI;

/**
 * Implemented by factories that create {@link LoggerContext} objects.
 * 由创建 {@link LoggerContext} 对象的工厂实现此接口。
 * 主要功能是定义用于管理日志器上下文的契约。
 */
public interface LoggerContextFactory {

    /**
     * Shuts down the LoggerContext.
     * 关闭 LoggerContext。
     *
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。用于确定哪个调用者正在请求关闭操作。
     * @param loader The ClassLoader to use or null.
     * 要使用的类加载器，如果为 null 则使用默认的类加载器。用于在特定类加载器环境中查找上下文。
     * @param currentContext If true shuts down the current Context, if false shuts down the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则关闭当前上下文；如果为 false，则在可以确定更合适的上下文时关闭适合调用者的上下文。
     * 此参数决定了关闭操作是针对当前线程/应用程序的上下文还是针对与调用者更相关的上下文。
     * @param allContexts if true all LoggerContexts that can be located will be shutdown.
     * 如果为 true，则所有可定位的 LoggerContexts 都将被关闭。
     * 此参数指示是否执行全局关闭操作。
     * @since 2.13.0
     */
    default void shutdown(String fqcn, ClassLoader loader, boolean currentContext, boolean allContexts) {
        // Check if a LoggerContext exists for the given parameters.
        // 检查是否存在与给定参数匹配的 LoggerContext。
        if (hasContext(fqcn, loader, currentContext)) {
            // Get the LoggerContext based on the provided parameters.
            // 根据提供的参数获取 LoggerContext。
            LoggerContext ctx = getContext(fqcn, loader, null, currentContext);
            // If the LoggerContext implements the Terminable interface, terminate it.
            // 如果 LoggerContext 实例实现了 Terminable 接口，则调用其 terminate 方法进行关闭。
            // Terminable 接口通常表示一个可以被终止或清理的资源。
            if (ctx instanceof Terminable) {
                ((Terminable) ctx).terminate();
            }
        }
        // Note: The 'allContexts' parameter is present in the signature but not used in this default implementation.
        // This default implementation only handles the shutdown of a single context if it exists.
        // 注意：'allContexts' 参数存在于方法签名中，但在此默认实现中未使用。
        // 此默认实现仅在存在单个上下文时处理其关闭。
    }

    /**
     * Checks to see if a LoggerContext is installed. The default implementation returns false.
     * 检查是否已安装 LoggerContext。默认实现返回 false。
     *
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader The ClassLoader to use or null.
     * 要使用的类加载器，如果为 null 则使用默认的类加载器。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则返回当前上下文；如果为 false，则在可以确定更合适的上下文时返回适合调用者的上下文。
     * @return true if a LoggerContext has been installed, false otherwise.
     * 如果已安装 LoggerContext 则返回 true，否则返回 false。
     * 默认实现始终返回 false，表示默认情况下不认为有上下文已安装，具体实现类需要覆盖此方法以提供实际的检查逻辑。
     * @since 2.13.0
     */
    default boolean hasContext(String fqcn, ClassLoader loader, boolean currentContext) {
        return false;
    }

    /**
     * Creates a {@link LoggerContext}.
     * 创建一个 {@link LoggerContext}。
     *
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader The ClassLoader to use or null.
     * 要使用的类加载器，如果为 null 则使用默认的类加载器。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则返回当前上下文；如果为 false，则在可以确定更合适的上下文时返回适合调用者的上下文。
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * 一个外部上下文（例如 ServletContext），将与 LoggerContext 关联。
     * 这允许将日志上下文与特定的应用程序环境绑定。
     * @return The LoggerContext.
     * 返回创建的 LoggerContext 实例。
     */
    LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext);

    /**
     * Creates a {@link LoggerContext}.
     * This is an overloaded method providing more control over configuration and naming.
     * 创建一个 {@link LoggerContext}。这是一个重载方法，提供对配置和命名更精细的控制。
     *
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader The ClassLoader to use or null.
     * 要使用的类加载器，如果为 null 则使用默认的类加载器。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则返回当前上下文；如果为 false，则在可以确定更合适的上下文时返回适合调用者的上下文。
     * @param configLocation The location of the configuration for the LoggerContext.
     * LoggerContext 配置文件的位置，通常是一个 URI。
     * 此参数允许指定自定义的日志配置。
     * @param externalContext An external context (such as a ServletContext) to be associated with the LoggerContext.
     * 一个外部上下文（例如 ServletContext），将与 LoggerContext 关联。
     * @param name The name of the context or null.
     * 上下文的名称，如果为 null 则使用默认名称。
     * 此名称可以用于区分不同的 LoggerContext 实例。
     * @return The LoggerContext.
     * 返回创建的 LoggerContext 实例。
     */
    LoggerContext getContext(String fqcn, ClassLoader loader, Object externalContext, boolean currentContext,
                             URI configLocation, String name);

    /**
     * Removes knowledge of a LoggerContext.
     * This effectively unregisters the context from the factory.
     * 移除对 LoggerContext 的管理。这实际上是从工厂中注销该上下文。
     *
     * @param context The context to remove.
     * 要移除的 LoggerContext 实例。
     */
    void removeContext(LoggerContext context);

    /**
     * Determines whether or not this factory and perhaps the underlying
     * ContextSelector behavior depend on the callers classloader.
     * 确定此工厂以及可能底层 ContextSelector 的行为是否依赖于调用者的类加载器。
     *
     * This method should be overridden by implementations, however a default method is provided which always
     * returns {@code true} to preserve the old behavior.
     * 此方法应由实现类覆盖，但提供了一个默认方法，该方法始终返回 {@code true} 以保留旧的行为。
     * 返回 true 意味着工厂在查找或创建 LoggerContext 时会考虑类加载器，这对于隔离不同应用程序的日志上下文非常重要。
     *
     * @since 2.15.0
     */
    default boolean isClassLoaderDependent() {
        return true;
    }
}
