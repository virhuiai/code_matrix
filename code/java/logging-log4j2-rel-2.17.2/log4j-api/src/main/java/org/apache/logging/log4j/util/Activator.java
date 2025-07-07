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
// 中文注释：
// 本文件遵循 Apache 2.0 许可证，归 Apache Software Foundation 所有，明确了版权和使用许可条款。

package org.apache.logging.log4j.util;

import java.net.URL;
import java.security.Permission;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.AdaptPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

/**
 * <em>Consider this class private.</em>
 * OSGi bundle activator. Used for locating an implementation of
 * {@link org.apache.logging.log4j.spi.LoggerContextFactory} et al. that have corresponding
 * {@code META-INF/log4j-provider.properties} files. As with all OSGi BundleActivator classes, this class is not for
 * public use and is only useful in an OSGi framework environment.
 */
// 中文注释：
// 类功能和目的：
// Activator 类是一个 OSGi 框架的 BundleActivator 实现，用于在 OSGi 环境中查找并加载 Log4j 2 的 LoggerContextFactory 实现，
// 这些实现通过对应的 META-INF/log4j-provider.properties 文件进行配置。
// 注意事项：
// - 该类标记为私有，仅在 OSGi 框架环境中有效，外部代码不应直接使用。
// - 主要功能是扫描和加载满足条件的 Provider，并在 Bundle 启动或变更时动态管理 Log4j 的日志提供者。

public class Activator implements BundleActivator, SynchronousBundleListener {

    private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();
    // 中文注释：
    // 变量用途：
    // SECURITY_MANAGER：保存当前系统的安全管理器实例，用于后续权限检查。
    // 注意事项：通过 System.getSecurityManager() 获取，可能为 null（无安全管理器时）。

    private static final Logger LOGGER = StatusLogger.getLogger();
    // 中文注释：
    // 变量用途：
    // LOGGER：静态日志记录器，使用 StatusLogger 获取，用于记录 Activator 类的调试、警告和错误信息。

    // until we have at least one Provider, we'll lock ProviderUtil which locks LogManager.<clinit> by extension.
    // this variable needs to be reset once the lock has been released
    private boolean lockingProviderUtil;
    // 中文注释：
    // 变量用途：
    // lockingProviderUtil：布尔标志，表示是否正在锁定 ProviderUtil（通过 STARTUP_LOCK），以防止 LogManager 的初始化。
    // 执行流程：
    // - 在启动时锁定，直到至少加载一个 Provider 后释放。
    // - 在 unlockIfReady 方法中重置为 false。
    // 注意事项：此标志确保在找到有效 Provider 前，LogManager 的初始化被延迟。

    private static void checkPermission(final Permission permission) {
        if (SECURITY_MANAGER != null) {
            SECURITY_MANAGER.checkPermission(permission);
        }
    }
    // 中文注释：
    // 方法功能和目的：
    // checkPermission：检查指定的权限是否被允许，主要用于确保对 Bundle 资源的安全访问。
    // 参数说明：
    // - permission：要检查的权限对象（例如 AdminPermission 或 AdaptPermission）。
    // 执行流程：
    // - 如果存在安全管理器（SECURITY_MANAGER 不为 null），调用其 checkPermission 方法。
    // - 如果没有安全管理器，直接返回（不执行权限检查）。
    // 注意事项：此方法用于处理 OSGi 环境中的权限控制，防止未经授权的访问。

    private void loadProvider(final Bundle bundle) {
        if (bundle.getState() == Bundle.UNINSTALLED) {
            return;
        }
        // 中文注释：
        // 方法功能和目的：
        // loadProvider：加载指定 Bundle 中的 Log4j 2 Provider（日志提供者）。
        // 参数说明：
        // - bundle：OSGi Bundle 对象，表示要加载 Provider 的模块。
        // 执行流程：
        // 1. 检查 Bundle 状态，若已卸载（UNINSTALLED），则直接返回。
        try {
            checkPermission(new AdminPermission(bundle, AdminPermission.RESOURCE));
            checkPermission(new AdaptPermission(BundleWiring.class.getName(), bundle, AdaptPermission.ADAPT));
            final BundleContext bundleContext = bundle.getBundleContext();
            if (bundleContext == null) {
                LOGGER.debug("Bundle {} has no context (state={}), skipping loading provider", bundle.getSymbolicName(), toStateString(bundle.getState()));
            } else {
                loadProvider(bundleContext, bundle.adapt(BundleWiring.class));
            }
        } catch (final SecurityException e) {
            LOGGER.debug("Cannot access bundle [{}] contents. Ignoring.", bundle.getSymbolicName(), e);
        } catch (final Exception e) {
            LOGGER.warn("Problem checking bundle {} for Log4j 2 provider.", bundle.getSymbolicName(), e);
        }
    }
    // 中文注释：
    // 执行流程（续）：
    // 2. 调用 checkPermission 检查 AdminPermission（资源访问权限）和 AdaptPermission（Bundle 适配权限）。
    // 3. 获取 Bundle 的上下文（BundleContext），若为 null，记录调试日志并跳过。
    // 4. 若上下文存在，将 Bundle 适配为 BundleWiring 并调用重载的 loadProvider 方法加载 Provider。
    // 异常处理：
    // - SecurityException：记录调试日志，表示无法访问 Bundle 内容，忽略该 Bundle。
    // - 其他异常：记录警告日志，提示检查 Bundle 时出现问题。
    // 注意事项：
    // - 权限检查确保操作符合安全策略。
    // - BundleContext 为 null 可能是 Bundle 未正确初始化或已停止。

    private String toStateString(final int state) {
        switch (state) {
        case Bundle.UNINSTALLED:
            return "UNINSTALLED";
        case Bundle.INSTALLED:
            return "INSTALLED";
        case Bundle.RESOLVED:
            return "RESOLVED";
        case Bundle.STARTING:
            return "STARTING";
        case Bundle.STOPPING:
            return "STOPPING";
        case Bundle.ACTIVE:
            return "ACTIVE";
        default:
            return Integer.toString(state);
        }
    }
    // 中文注释：
    // 方法功能和目的：
    // toStateString：将 OSGi Bundle 的状态代码转换为可读的字符串表示。
    // 参数说明：
    // - state：Bundle 的状态值（整数，例如 Bundle.ACTIVE）。
    // 返回值：
    // - String：状态的字符串表示（如 "ACTIVE"）或状态值的字符串形式（未知状态时）。
    // 执行流程：
    // - 使用 switch 语句匹配状态值，返回对应的字符串。
    // - 如果状态值未知，返回其整数值的字符串形式。
    // 注意事项：此方法用于日志记录，便于调试时理解 Bundle 的状态。

    private void loadProvider(final BundleContext bundleContext, final BundleWiring bundleWiring) {
        final String filter = "(APIVersion>=2.6.0)";
        try {
            final Collection<ServiceReference<Provider>> serviceReferences = bundleContext.getServiceReferences(Provider.class, filter);
            Provider maxProvider = null;
            for (final ServiceReference<Provider> serviceReference : serviceReferences) {
                final Provider provider = bundleContext.getService(serviceReference);
                if (maxProvider == null || provider.getPriority() > maxProvider.getPriority()) {
                    maxProvider = provider;
                }
            }
            if (maxProvider != null) {
                ProviderUtil.addProvider(maxProvider);
            }
        } catch (final InvalidSyntaxException ex) {
            LOGGER.error("Invalid service filter: " + filter, ex);
        }
        final List<URL> urls = bundleWiring.findEntries("META-INF", "log4j-provider.properties", 0);
        for (final URL url : urls) {
            ProviderUtil.loadProvider(url, bundleWiring.getClassLoader());
        }
    }
    // 中文注释：
    // 方法功能和目的：
    // loadProvider：从指定的 BundleContext 和 BundleWiring 中加载 Log4j 2 的 Provider。
    // 参数说明：
    // - bundleContext：Bundle 的上下文，用于获取服务引用。
    // - bundleWiring：Bundle 的布线信息，用于查找资源文件和类加载器。
    // 执行流程：
    // 1. 定义服务过滤条件 filter，要求 Provider 的 APIVersion >= 2.6.0。
    // 2. 获取符合过滤条件的 Provider 服务引用集合。
    // 3. 遍历服务引用，获取每个 Provider，比较优先级，选择优先级最高的 Provider（maxProvider）。
    // 4. 如果找到有效 Provider，将其添加到 ProviderUtil。
    // 5. 查找 Bundle 中的 META-INF/log4j-provider.properties 文件。
    // 6. 对每个找到的配置文件 URL，调用 ProviderUtil.loadProvider 加载 Provider。
    // 异常处理：
    // - InvalidSyntaxException：如果过滤条件语法错误，记录错误日志。
    // 关键变量：
    // - filter：服务过滤条件，确保只加载支持 Log4j 2.6.0 或更高版本的 Provider。
    // - maxProvider：记录优先级最高的 Provider。
    // 注意事项：
    // - 优先级比较确保选择最佳 Provider。
    // - 配置文件加载支持动态扩展 Log4j 功能。

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        ProviderUtil.STARTUP_LOCK.lock();
        lockingProviderUtil = true;
        final BundleWiring self = bundleContext.getBundle().adapt(BundleWiring.class);
        final List<BundleWire> required = self.getRequiredWires(LoggerContextFactory.class.getName());
        for (final BundleWire wire : required) {
            loadProvider(bundleContext, wire.getProviderWiring());
        }
        bundleContext.addBundleListener(this);
        final Bundle[] bundles = bundleContext.getBundles();
        for (final Bundle bundle : bundles) {
            loadProvider(bundle);
        }
        unlockIfReady();
    }
    // 中文注释：
    // 方法功能和目的：
    // start：实现 BundleActivator 的启动方法，在 OSGi Bundle 激活时执行，初始化 Log4j 2 的 Provider 加载。
    // 参数说明：
    // - bundleContext：当前 Bundle 的上下文，用于访问 Bundle 信息和服务。
    // 执行流程：
    // 1. 加锁 ProviderUtil.STARTUP_LOCK，防止 LogManager 初始化。
    // 2. 设置 lockingProviderUtil 为 true，标记锁定状态。
    // 3. 获取当前 Bundle 的 BundleWiring 对象。
    // 4. 获取依赖 LoggerContextFactory 的 BundleWire 列表。
    // 5. 对每个依赖的 Bundle，调用 loadProvider 加载 Provider。
    // 6. 添加 Bundle 监听器，监听后续 Bundle 事件。
    // 7. 遍历所有已加载的 Bundle，调用 loadProvider 加载 Provider。
    // 8. 调用 unlockIfReady 检查是否可以释放锁。
    // 关键变量：
    // - self：当前 Bundle 的 BundleWiring 对象。
    // - required：依赖 LoggerContextFactory 的 BundleWire 列表。
    // 注意事项：
    // - 加锁机制确保 Provider 加载完成前 LogManager 不被初始化。
    // - 监听器注册用于处理动态 Bundle 事件。

    private void unlockIfReady() {
        if (lockingProviderUtil && !ProviderUtil.PROVIDERS.isEmpty()) {
            ProviderUtil.STARTUP_LOCK.unlock();
            lockingProviderUtil = false;
        }
    }
    // 中文注释：
    // 方法功能和目的：
    // unlockIfReady：检查是否可以释放 ProviderUtil 的启动锁。
    // 执行流程：
    // 1. 检查 lockingProviderUtil 是否为 true 且 ProviderUtil.PROVIDERS 不为空。
    // 2. 如果条件满足，释放 STARTUP_LOCK 并重置 lockingProviderUtil 为 false。
    // 注意事项：
    // - 只有在至少加载一个 Provider 后才会释放锁，确保 LogManager 可以安全初始化。

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        bundleContext.removeBundleListener(this);
        unlockIfReady();
    }
    // 中文注释：
    // 方法功能和目的：
    // stop：实现 BundleActivator 的停止方法，在 OSGi Bundle 停止时执行，清理资源。
    // 参数说明：
    // - bundleContext：当前 Bundle 的上下文。
    // 执行流程：
    // 1. 移除当前对象作为 Bundle 监听器。
    // 2. 调用 unlockIfReady 检查是否可以释放锁。
    // 注意事项：
    // - 移除监听器确保不再处理后续 Bundle 事件。
    // - 释放锁允许 LogManager 完成清理。

    @Override
    public void bundleChanged(final BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                loadProvider(event.getBundle());
                unlockIfReady();
                break;

            default:
                break;
        }
    }
    // 中文注释：
    // 方法功能和目的：
    // bundleChanged：实现 SynchronousBundleListener 接口，处理 OSGi Bundle 的事件。
    // 参数说明：
    // - event：BundleEvent 对象，描述 Bundle 的状态变化。
    // 执行流程：
    // 1. 检查事件类型，若为 STARTED（Bundle 启动），则：
    //    - 调用 loadProvider 加载该 Bundle 的 Provider。
    //    - 调用 unlockIfReady 检查是否可以释放锁。
    // 2. 其他事件类型被忽略。
    // 事件处理机制：
    // - 仅处理 Bundle 启动事件，以动态加载新启动 Bundle 的 Provider。
    // 注意事项：
    // - 同步监听确保事件处理实时性，适合快速响应 Bundle 状态变化。
}
// 中文注释：
// 类整体执行流程总结：
// 1. Bundle 启动时，start 方法加锁并加载依赖 LoggerContextFactory 的 Provider，同时扫描所有 Bundle。
// 2. 动态监听 Bundle 事件，在 Bundle 启动时加载新 Provider。
// 3. Bundle 停止时，移除监听器并尝试释放锁。
// 4. Provider 加载涉及权限检查、服务引用获取和配置文件解析。
// 注意事项：
// - 所有操作均在 OSGi 框架环境中执行，依赖 OSGi 的 Bundle 管理和服务机制。
// - 权限检查和异常处理确保代码健壮性和安全性。
// - 锁定机制防止 LogManager 在 Provider 加载完成前初始化。