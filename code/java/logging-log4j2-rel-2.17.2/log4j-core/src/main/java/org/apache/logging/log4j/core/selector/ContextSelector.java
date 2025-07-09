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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.LoggerContext;

/**
 * Interface used to locate a LoggerContext.
 * 该接口用于定位 LoggerContext。
 * LoggerContext 是 Log4j 2 中日志系统的核心组件，负责管理日志配置、记录器等。
 */
public interface ContextSelector {

    long DEFAULT_STOP_TIMEOUT = 50;
    // 默认的停止超时时间，单位为毫秒。
    // 当 LoggerContext 关闭时，如果需要等待其完全停止，会使用此超时时间。

    /**
     * Shuts down the LoggerContext.
     * 关闭 LoggerContext。
     * 此方法提供了关闭一个或所有 LoggerContexts 的功能，基于传入的参数决定关闭范围。
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。用于确定哪个 LoggerContext 应该被关闭。
     * @param loader The ClassLoader to use or null.
     * 要使用的 ClassLoader，如果为 null 则表示使用默认的 ClassLoader。
     * ClassLoader 在识别和加载 LoggerContext 时可能很重要。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * 如果为 true，则表示操作当前线程的 LoggerContext；如果为 false，则尝试根据调用者确定一个更合适的 LoggerContext。
     * @param allContexts if true all LoggerContexts that can be located will be shutdown.
     * 如果为 true，则会关闭所有能够被定位到的 LoggerContext 实例。
     * 请注意，默认实现仅在 hasContext 返回 true 时才尝试关闭，且仅关闭单个上下文。
     * @since 2.13.0
     */
    default void shutdown(final String fqcn, final ClassLoader loader, final boolean currentContext,
                          final boolean allContexts) {
        // 检查是否存在与给定参数匹配的 LoggerContext。
        if (hasContext(fqcn, loader, currentContext)) {
            // 如果存在，则获取该 LoggerContext 并以默认的超时时间停止它。
            getContext(fqcn, loader, currentContext).stop(DEFAULT_STOP_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Checks to see if a LoggerContext is installed. The default implementation returns false.
     * 检查是否已安装 LoggerContext。默认实现返回 false。
     * 此方法用于判断是否存在一个活跃的 LoggerContext。
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader The ClassLoader to use or null.
     * 要使用的 ClassLoader，如果为 null 则表示使用默认的 ClassLoader。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则检查当前线程的 LoggerContext；如果为 false，则尝试根据调用者确定一个更合适的 LoggerContext。
     * @return true if a LoggerContext has been installed, false otherwise.
     * 如果 LoggerContext 已安装则返回 true，否则返回 false。
     * @since 2.13.0
     */
    default boolean hasContext(String fqcn, ClassLoader loader, boolean currentContext) {
        return false;
        // 默认实现总是返回 false，意味着如果子类不覆盖此方法，将不会认为有 LoggerContext 存在。
    }

    /**
     * Returns the LoggerContext.
     * 返回 LoggerContext。
     * 这是获取 LoggerContext 的核心方法之一，根据传入的参数返回一个特定的 LoggerContext 实例。
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader ClassLoader to use or null.
     * 要使用的 ClassLoader，如果为 null 则表示使用默认的 ClassLoader。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则返回当前线程的 LoggerContext；如果为 false，则尝试根据调用者确定一个更合适的 LoggerContext。
     * @return The LoggerContext.
     * 返回对应的 LoggerContext 实例。
     */
    LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext);

    /**
     * Returns the LoggerContext.
     * 返回 LoggerContext。
     * 此方法是 {@link #getContext(String, ClassLoader, boolean)} 的重载版本，允许在获取 LoggerContext 后立即向其添加一个对象。
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader ClassLoader to use or null.
     * 要使用的 ClassLoader，如果为 null 则表示使用默认的 ClassLoader。
     * @param entry An entry for the external Context map.
     * 一个用于外部上下文映射的条目。这是一个键值对，会被存储在 LoggerContext 的属性中。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则返回当前线程的 LoggerContext；如果为 false，则尝试根据调用者确定一个更合适的 LoggerContext。
     * @return The LoggerContext.
     * 返回对应的 LoggerContext 实例。
     */
    default LoggerContext getContext(String fqcn, ClassLoader loader, Map.Entry<String, Object> entry, boolean currentContext) {
        // 首先获取 LoggerContext。
        LoggerContext lc = getContext(fqcn, loader, currentContext);
        // 如果 LoggerContext 不为 null，则将传入的键值对添加到其属性中。
        if (lc != null) {
            lc.putObject(entry.getKey(), entry.getValue());
        }
        return lc;
        // 返回获取到的 LoggerContext。
    }

    /**
     * Returns the LoggerContext.
     * 返回 LoggerContext。
     * 此方法是 {@link #getContext(String, ClassLoader, boolean)} 的另一个重载版本，它允许指定 LoggerContext 的配置位置。
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader ClassLoader to use or null.
     * 要使用的 ClassLoader，如果为 null 则表示使用默认的 ClassLoader。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则返回当前线程的 LoggerContext；如果为 false，则尝试根据调用者确定一个更合适的 LoggerContext。
     * @param configLocation The location of the configuration for the LoggerContext.
     * LoggerContext 配置文件的 URI 位置。例如，可以是文件路径或 URL。
     * @return The LoggerContext.
     * 返回对应的 LoggerContext 实例。
     */
    LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext, URI configLocation);

    /**
     * Returns the LoggerContext.
     * 返回 LoggerContext。
     * 此方法是 {@link #getContext(String, ClassLoader, boolean, URI)} 的重载版本，同时允许指定配置位置和额外的上下文条目。
     * @param fqcn The fully qualified class name of the caller.
     * 调用者的完全限定类名。
     * @param loader ClassLoader to use or null.
     * 要使用的 ClassLoader，如果为 null 则表示使用默认的 ClassLoader。
     * @param entry An entry for the external Context map.
     * 一个用于外部上下文映射的条目（键值对）。
     * @param currentContext If true returns the current Context, if false returns the Context appropriate
     * for the caller if a more appropriate Context can be determined.
     * 如果为 true，则返回当前线程的 LoggerContext；如果为 false，则尝试根据调用者确定一个更合适的 LoggerContext。
     * @param configLocation The location of the configuration for the LoggerContext.
     * LoggerContext 配置文件的 URI 位置。
     * @return The LoggerContext.
     * 返回对应的 LoggerContext 实例。
     */
    default LoggerContext getContext(String fqcn, ClassLoader loader, Map.Entry<String, Object> entry,
            boolean currentContext, URI configLocation) {
        // 首先获取 LoggerContext，指定了配置位置。
        LoggerContext lc = getContext(fqcn, loader, currentContext, configLocation);
        // 如果 LoggerContext 不为 null，则将传入的键值对添加到其属性中。
        if (lc != null) {
            lc.putObject(entry.getKey(), entry.getValue());
        }
        return lc;
        // 返回获取到的 LoggerContext。
    }

    /**
     * Returns a List of all the available LoggerContexts.
     * 返回所有可用的 LoggerContext 列表。
     * 此方法用于获取当前所有被 ContextSelector 管理的 LoggerContext 实例。
     * @return The List of LoggerContexts.
     * 返回 LoggerContext 实例的列表。
     */
    List<LoggerContext> getLoggerContexts();

    /**
     * Remove any references to the LoggerContext.
     * 移除对 LoggerContext 的所有引用。
     * 此方法用于从 ContextSelector 中移除一个特定的 LoggerContext，可能涉及资源的释放。
     * @param context The context to remove.
     * 要移除的 LoggerContext 实例。
     */
    void removeContext(LoggerContext context);

    /**
     * Determines whether or not this ContextSelector depends on the callers classloader.
     * 确定此 ContextSelector 是否依赖于调用者的 ClassLoader。
     * 如果一个 ContextSelector 的行为依赖于调用方的 ClassLoader（例如，用于隔离不同的日志配置），则此方法应返回 true。
     * This method should be overridden by implementations, however a default method is provided which always
     * returns {@code true} to preserve the old behavior.
     * 该方法应由实现类覆盖，但提供了一个默认方法，它总是返回 {@code true} 以保留旧的行为。
     * @return true if the class loader is required.
     * 如果需要 ClassLoader 则返回 true。
     * @since 2.15.0
     */
    default boolean isClassLoaderDependent() {
        return true;
        // 默认实现返回 true，表示默认情况下 ContextSelector 依赖于 ClassLoader。
        // 子类可以根据实际需求覆盖此方法。
    }
}
