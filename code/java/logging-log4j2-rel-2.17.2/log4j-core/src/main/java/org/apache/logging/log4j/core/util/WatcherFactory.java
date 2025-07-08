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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFileWatcher;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Creates Watchers of various types.
 * 用于创建各种类型Watcher（监视器）的工厂类。
 */
public class WatcherFactory {

    private static Logger LOGGER = StatusLogger.getLogger();
    // 静态日志记录器，用于记录工厂类的运行状态信息。
    private static PluginManager pluginManager = new PluginManager(Watcher.CATEGORY);
    // 插件管理器，用于管理和加载Watcher类型的插件。

    private static volatile WatcherFactory factory;
    // WatcherFactory的单例实例，使用volatile关键字确保多线程环境下的可见性。

    private final Map<String, PluginType<?>> plugins;
    // 存储已加载的插件类型，键是插件名称，值是对应的插件类型。

    private WatcherFactory(List<String> packages) {
        pluginManager.collectPlugins(packages);
        // 调用插件管理器收集指定包中的插件。
        plugins = pluginManager.getPlugins();
        // 获取所有已加载的插件。
    }

    public static WatcherFactory getInstance(List<String> packages) {
        // 获取WatcherFactory的单例实例。
        // packages: 需要扫描的插件包列表。
        if (factory == null) {
            // 双重检查锁定，确保线程安全地创建单例。
            synchronized (pluginManager) {
                if (factory == null) {
                    factory = new WatcherFactory(packages);
                    // 如果实例为空，则创建一个新的WatcherFactory实例。
                }
            }
        }
        return factory;
        // 返回WatcherFactory的单例实例。
    }

    @SuppressWarnings("unchecked")
    public Watcher newWatcher(Source source, final Configuration configuration, final Reconfigurable reconfigurable,
        final List<ConfigurationListener> configurationListeners, long lastModifiedMillis) {
        // 根据Source类型创建并返回一个新的Watcher实例。
        // source: 监视的资源源。
        // configuration: 当前的配置对象。
        // reconfigurable: 可重新配置的对象，通常是Configuration。
        // configurationListeners: 配置监听器列表。
        // lastModifiedMillis: 资源最后修改的时间戳。
        if (source.getFile() != null) {
            // 如果Source是文件，则创建一个ConfigurationFileWatcher。
            return new ConfigurationFileWatcher(configuration, reconfigurable, configurationListeners,
                lastModifiedMillis);
        } else {
            // 如果Source不是文件，则尝试根据URI的scheme查找并实例化Watcher插件。
            String name = source.getURI().getScheme();
            // 获取URI的scheme作为插件名称。
            PluginType<?> pluginType = plugins.get(name);
            // 根据名称从已加载的插件中获取对应的插件类型。
            if (pluginType != null) {
                // 如果找到了对应的插件类型，则实例化该插件。
                return instantiate(name, (Class<? extends Watcher>) pluginType.getPluginClass(), configuration,
                    reconfigurable, configurationListeners, lastModifiedMillis);
            }
            LOGGER.info("No Watcher plugin is available for protocol '{}'", name);
            // 如果没有找到对应的Watcher插件，则记录一条信息。
            return null;
            // 返回null表示未能创建Watcher。
        }
    }

    public static <T extends Watcher> T instantiate(String name, final Class<T> clazz,
        final Configuration configuration, final Reconfigurable reconfigurable,
        final List<ConfigurationListener> listeners, long lastModifiedMillis) {
        // 实例化一个Watcher插件。
        // name: 插件名称。
        // clazz: 插件的Class对象。
        // configuration: 当前的配置对象。
        // reconfigurable: 可重新配置的对象。
        // listeners: 配置监听器列表。
        // lastModifiedMillis: 资源最后修改的时间戳。
        Objects.requireNonNull(clazz, "No class provided");
        // 确保提供的类不为空。
        try {
            Constructor<T> constructor = clazz
                .getConstructor(Configuration.class, Reconfigurable.class, List.class, long.class);
            // 获取匹配指定参数类型的构造函数。
            return constructor.newInstance(configuration, reconfigurable, listeners, lastModifiedMillis);
            // 调用构造函数创建并返回Watcher实例。
        } catch (NoSuchMethodException ex) {
            // 如果没有找到匹配的构造函数。
            throw new IllegalArgumentException("No valid constructor for Watcher plugin " + name, ex);
        } catch (final LinkageError | InstantiationException e) {
            // LOG4J2-1051
            // On platforms like Google App Engine and Android, some JRE classes are not supported: JMX, JNDI, etc.
            // 在某些平台（如Google App Engine和Android）上，不支持某些JRE类（如JMX、JNDI等），导致链接或实例化错误。
            throw new IllegalArgumentException(e);
        } catch (final IllegalAccessException e) {
            // 如果无法访问构造函数。
            throw new IllegalStateException(e);
        } catch (final InvocationTargetException e) {
            // 如果构造函数内部抛出异常。
            Throwables.rethrow(e.getCause());
            // 重新抛出原始异常。
            throw new InternalError("Unreachable");
            // 这行代码理论上无法达到，因为上面的rethrow会抛出异常。
        }
    }
}
