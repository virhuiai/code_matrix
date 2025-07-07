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
package org.apache.logging.log4j.core;

import java.util.List;

import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.core.impl.ThreadContextDataInjector;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;

/**
 * Responsible for initializing the context data of LogEvents. Context data is data that is set by the application to be
 * included in all subsequent log events.
 * 负责初始化 LogEvents 的上下文数据。上下文数据是应用程序设置的，用于包含在所有后续日志事件中的数据。
 * <p><b>NOTE: It is no longer recommended that custom implementations of this interface be provided as it is
 * difficult to do. Instead, provide a custom ContextDataProvider.</b></p>
 * <p><b>注意：不再推荐提供此接口的自定义实现，因为这很难做到。相反，请提供自定义的 ContextDataProvider。</b></p>
 * <p>
 * <p>
 * The source of the context data is implementation-specific. The default source for context data is the ThreadContext.
 * </p><p>
 * 上下文数据的来源是与实现相关的。上下文数据的默认来源是 ThreadContext。
 * </p><p>
 * In some asynchronous models, work may be delegated to several threads, while conceptually this work shares the same
 * context. In such models, storing context data in {@code ThreadLocal} variables is not convenient or desirable.
 * Users can configure the {@code ContextDataInjectorFactory} to provide custom {@code ContextDataInjector} objects,
 * in order to initialize log events with context data from any arbitrary context.
 * </p><p>
 * 在某些异步模型中，工作可能会委托给多个线程，但概念上这些工作共享相同的上下文。在这种模型中，将上下文数据存储在 {@code ThreadLocal} 变量中既不方便也不可取。
 * 用户可以配置 {@code ContextDataInjectorFactory} 来提供自定义的 {@code ContextDataInjector} 对象，
 * 以便使用来自任何任意上下文的上下文数据来初始化日志事件。
 * </p><p>
 * When providing a custom {@code ContextDataInjector}, be aware that the {@code ContextDataInjectorFactory} may be
 * invoked multiple times and the various components in Log4j that need access to context data may each have their own
 * instance of {@code ContextDataInjector}.
 * This includes the object(s) that populate log events, but also various lookups and filters that look at
 * context data to determine whether an event should be logged.
 * </p><p>
 * 当提供自定义的 {@code ContextDataInjector} 时，请注意 {@code ContextDataInjectorFactory} 可能会被多次调用，
 * 并且 Log4j 中需要访问上下文数据的各个组件可能都有自己的 {@code ContextDataInjector} 实例。
 * 这包括填充日志事件的对象，以及查看上下文数据以确定事件是否应该被记录的各种查找器和过滤器。
 * </p><p>
 * Implementors should take particular note of how the different methods in the interface have different thread-safety
 * guarantees to enable optimal performance.
 * </p>
 * 实现者应特别注意接口中不同方法的线程安全保证如何不同，以实现最佳性能。
 *
 * @see StringMap
 * @see ReadOnlyStringMap
 * @see ContextDataInjectorFactory
 * @see org.apache.logging.log4j.ThreadContext
 * @see ThreadContextDataInjector
 * @since 2.7
 */
public interface ContextDataInjector {
    /**
     * Returns a {@code StringMap} object initialized with the specified properties and the appropriate
     * context data. The returned value may be the specified parameter or a different object.
     * 返回一个 {@code StringMap} 对象，该对象使用指定的属性和相应的上下文数据进行初始化。返回的值可以是指定的参数，也可以是不同的对象。
     * <p>
     * This method will be called for each log event to initialize its context data and implementors should take
     * care to make this method as performant as possible while preserving at least the following thread-safety
     * guarantee.
     * </p><p>
     * 此方法将为每个日志事件调用以初始化其上下文数据，实现者应注意使此方法尽可能高效，同时至少保留以下线程安全保证。
     * </p><p>
     * Thread-safety note: The returned object can safely be passed off to another thread: future changes in the
     * underlying context data will not be reflected in the returned object.
     * </p><p>
     * 线程安全注意事项：返回的对象可以安全地传递给另一个线程：底层上下文数据的未来更改将不会反映在返回的对象中。
     * </p><p>
     * Example implementation:
     * </p>
     * 示例实现：
     * <pre>
     * public StringMap injectContextData(List<Property> properties, StringMap reusable) {
     *     if (properties == null || properties.isEmpty()) {
     *         // assume context data is stored in a copy-on-write data structure that is safe to pass to another thread
     * // 假设上下文数据存储在写时复制的数据结构中，可以安全地传递给另一个线程
     *         return (StringMap) rawContextData();
     *     }
     *     // first copy configuration properties into the result
     * // 首先将配置属性复制到结果中
     *     ThreadContextDataInjector.copyProperties(properties, reusable);
     *
     *     // then copy context data key-value pairs (may overwrite configuration properties)
     * // 然后复制上下文数据键值对（可能会覆盖配置属性）
     *     reusable.putAll(rawContextData());
     *     return reusable;
     * }
     * </pre>
     *
     * @param properties Properties from the log4j configuration to be added to the resulting ReadOnlyStringMap. May be
     *          {@code null} or empty
     * 要添加到结果 ReadOnlyStringMap 中的来自 log4j 配置的属性。可能为 {@code null} 或为空。
     * @param reusable a {@code StringMap} instance that may be reused to avoid creating temporary objects
     * 一个 {@code StringMap} 实例，可以重用以避免创建临时对象。
     * @return a {@code StringMap} instance initialized with the specified properties and the appropriate
     *          context data. The returned value may be the specified parameter or a different object.
     * 一个使用指定属性和相应上下文数据初始化的 {@code StringMap} 实例。返回的值可以是指定的参数，也可以是不同的对象。
     * @see ThreadContextDataInjector#copyProperties(List, StringMap)
     */
    StringMap injectContextData(final List<Property> properties, final StringMap reusable);

    /**
     * Returns a {@code ReadOnlyStringMap} object reflecting the current state of the context. Configuration properties
     * are not included in the result.
     * 返回一个 {@code ReadOnlyStringMap} 对象，反映上下文的当前状态。配置属性不包含在结果中。
     * <p>
     * This method may be called multiple times for each log event by Filters and Lookups and implementors should take
     * care to make this method as performant as possible while preserving at least the following thread-safety
     * guarantee.
     * </p><p>
     * 此方法可能被过滤器和查找器为每个日志事件多次调用，实现者应注意使此方法尽可能高效，同时至少保留以下线程安全保证。
     * </p><p>
     * Thread-safety note: The returned object can only be safely used <em>in the current thread</em>. Changes in the
     * underlying context may or may not be reflected in the returned object, depending on the context data source and
     * the implementation of this method. It is not safe to pass the returned object to another thread.
     * </p>
     * 线程安全注意事项：返回的对象只能在**当前线程**中安全使用。底层上下文中的更改可能反映或不反映在返回的对象中，具体取决于上下文数据源和此方法的实现。将返回的对象传递给另一个线程是不安全的。
     * @return a {@code ReadOnlyStringMap} object reflecting the current state of the context, may not return {@code null}
     * 一个反映上下文当前状态的 {@code ReadOnlyStringMap} 对象，可能不会返回 {@code null}。
     */
    ReadOnlyStringMap rawContextData();
}
