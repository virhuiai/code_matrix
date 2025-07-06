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
package org.apache.logging.log4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Adds entries to the {@link ThreadContext stack or map} and them removes them when the object is closed, e.g. as part
 * of a try-with-resources. User code can now look like this:
 * <pre>
 * try (final CloseableThreadContext.Instance ignored = CloseableThreadContext.put(key1, value1).put(key2, value2)) {
 *     callSomeMethodThatLogsALot();
 *
 * // Entries for key1 and key2 are automatically removed from the ThreadContext map when done.
 * }
 * </pre>
 *
 * @since 2.6
 */
// 中文注释：
// 该类用于向 ThreadContext 的堆栈或映射添加条目，并在对象关闭时自动移除，支持 try-with-resources 语法。
// 主要功能：管理线程上下文的诊断信息，自动恢复原始状态。
// 使用场景：日志记录中需要临时添加上下文信息，并在完成后自动清理。
public class CloseableThreadContext {

    private CloseableThreadContext() {
    }
    // 中文注释：
    // 私有构造函数，防止外部实例化，确保通过静态方法创建实例。
    // 注意事项：此类设计为工具类，仅通过静态方法操作。

    /**
     * Pushes new diagnostic context information on to the Thread Context Stack. The information will be popped off when
     * the instance is closed.
     *
     * @param message The new diagnostic context information.
     * @return a new instance that will back out the changes when closed.
     */
    // 中文注释：
    // 方法目的：将诊断上下文信息推入 ThreadContext 堆栈。
    // 参数说明：
    //   - message：要添加的诊断上下文信息（字符串）。
    // 返回值：返回一个 Instance 对象，支持链式调用，关闭时自动移除堆栈信息。
    // 事件处理逻辑：信息推入堆栈后，关闭实例时会自动弹出。
    public static CloseableThreadContext.Instance push(final String message) {
        return new CloseableThreadContext.Instance().push(message);
    }

    /**
     * Pushes new diagnostic context information on to the Thread Context Stack. The information will be popped off when
     * the instance is closed.
     *
     * @param message The new diagnostic context information.
     * @param args    Parameters for the message.
     * @return a new instance that will back out the changes when closed.
     */
    // 中文注释：
    // 方法目的：将带参数的诊断上下文信息推入 ThreadContext 堆栈。
    // 参数说明：
    //   - message：诊断上下文信息（字符串）。
    //   - args：消息的格式化参数。
    // 返回值：返回 Instance 对象，支持链式调用，关闭时自动移除堆栈信息。
    // 事件处理逻辑：支持格式化消息，关闭实例时自动弹出堆栈。
    public static CloseableThreadContext.Instance push(final String message, final Object... args) {
        return new CloseableThreadContext.Instance().push(message, args);
    }

    /**
     * Populates the Thread Context Map with the supplied key/value pair. Any existing key in the
     * {@link ThreadContext} will be replaced with the supplied value, and restored back to their original value when
     * the instance is closed.
     *
     * @param key   The  key to be added
     * @param value The value to be added
     * @return a new instance that will back out the changes when closed.
     */
    // 中文注释：
    // 方法目的：向 ThreadContext 映射添加键值对。
    // 参数说明：
    //   - key：要添加的键（字符串）。
    //   - value：要添加的值（字符串）。
    // 返回值：返回 Instance 对象，支持链式调用，关闭时恢复原始值。
    // 事件处理逻辑：替换现有键值，关闭时恢复原始值或移除键。
    // 特殊处理：如果键已存在，原值会被记录以便恢复。
    public static CloseableThreadContext.Instance put(final String key, final String value) {
        return new CloseableThreadContext.Instance().put(key, value);
    }

    /**
     * Populates the Thread Context Stack with the supplied stack. The information will be popped off when
     * the instance is closed.
     *
     * @param messages The list of messages to be added
     * @return a new instance that will back out the changes when closed.
     * @since 2.8
     */
    // 中文注释：
    // 方法目的：将一组诊断信息批量推入 ThreadContext 堆栈。
    // 参数说明：
    //   - messages：要添加的诊断信息列表。
    // 返回值：返回 Instance 对象，关闭时自动移除所有堆栈信息。
    // 事件处理逻辑：逐条推入消息，关闭时按序弹出。
    // 注意事项：自 2.8 版本引入，适合批量操作。
    public static CloseableThreadContext.Instance pushAll(final List<String> messages) {
        return new CloseableThreadContext.Instance().pushAll(messages);
    }

    /**
     * Populates the Thread Context Map with the supplied key/value pairs. Any existing keys in the
     * {@link ThreadContext} will be replaced with the supplied values, and restored back to their original value when
     * the instance is closed.
     *
     * @param values The map of key/value pairs to be added
     * @return a new instance that will back out the changes when closed.
     * @since 2.8
     */
    // 中文注释：
    // 方法目的：向 ThreadContext 映射批量添加键值对。
    // 参数说明：
    //   - values：要添加的键值对映射。
    // 返回值：返回 Instance 对象，关闭时恢复原始值。
    // 事件处理逻辑：替换现有键值，关闭时恢复原始值或移除键。
    // 注意事项：自 2.8 版本引入，适合批量添加键值对。
    public static CloseableThreadContext.Instance putAll(final Map<String, String> values) {
        return new CloseableThreadContext.Instance().putAll(values);
    }

    public static class Instance implements AutoCloseable {
        // 中文注释：
        // 内部类用途：实现 AutoCloseable 接口，用于管理 ThreadContext 的堆栈和映射操作。
        // 关键变量说明：
        //   - pushCount：记录推入堆栈的次数，用于关闭时弹出。
        //   - originalValues：存储原始键值对，用于关闭时恢复。
        private int pushCount = 0;
        private final Map<String, String> originalValues = new HashMap<>();

        private Instance() {
        }
        // 中文注释：
        // 私有构造函数，防止外部实例化，确保通过外部静态方法创建。
        // 注意事项：仅由外部类静态方法调用。

        /**
         * Pushes new diagnostic context information on to the Thread Context Stack. The information will be popped off when
         * the instance is closed.
         *
         * @param message The new diagnostic context information.
         * @return the instance that will back out the changes when closed.
         */
        // 中文注释：
        // 方法目的：将诊断信息推入 ThreadContext 堆栈。
        // 参数说明：
        //   - message：诊断上下文信息（字符串）。
        // 返回值：返回当前 Instance 对象，支持链式调用。
        // 事件处理逻辑：推入堆栈并增加计数，关闭时自动弹出。
        public Instance push(final String message) {
            ThreadContext.push(message);
            pushCount++;
            return this;
        }

        /**
         * Pushes new diagnostic context information on to the Thread Context Stack. The information will be popped off when
         * the instance is closed.
         *
         * @param message The new diagnostic context information.
         * @param args    Parameters for the message.
         * @return the instance that will back out the changes when closed.
         */
        // 中文注释：
        // 方法目的：将带参数的诊断信息推入 ThreadContext 堆栈。
        // 参数说明：
        //   - message：诊断上下文信息（字符串）。
        //   - args：消息的格式化参数。
        // 返回值：返回当前 Instance 对象，支持链式调用。
        // 事件处理逻辑：推入格式化消息并增加计数，关闭时自动弹出。
        public Instance push(final String message, final Object[] args) {
            ThreadContext.push(message, args);
            pushCount++;
            return this;
        }

        /**
         * Populates the Thread Context Map with the supplied key/value pair. Any existing key in the
         * {@link ThreadContext} will be replaced with the supplied value, and restored back to their original value when
         * the instance is closed.
         *
         * @param key   The  key to be added
         * @param value The value to be added
         * @return a new instance that will back out the changes when closed.
         */
        // 中文注释：
        // 方法目的：向 ThreadContext 映射添加单个键值对。
        // 参数说明：
        //   - key：要添加的键（字符串）。
        //   - value：要添加的值（字符串）。
        // 返回值：返回当前 Instance 对象，支持链式调用。
        // 事件处理逻辑：记录原始值（如果存在），替换为新值，关闭时恢复。
        // 特殊处理：若键不存在，记录 null 作为原始值。
        public Instance put(final String key, final String value) {
            // If there are no existing values, a null will be stored as an old value
            if (!originalValues.containsKey(key)) {
                originalValues.put(key, ThreadContext.get(key));
            }
            ThreadContext.put(key, value);
            return this;
        }

        /**
         * Populates the Thread Context Map with the supplied key/value pairs. Any existing keys in the
         * {@link ThreadContext} will be replaced with the supplied values, and restored back to their original value when
         * the instance is closed.
         *
         * @param values The map of key/value pairs to be added
         * @return a new instance that will back out the changes when closed.
         * @since 2.8
         */
        // 中文注释：
        // 方法目的：向 ThreadContext 映射批量添加键值对。
        // 参数说明：
        //   - values：要添加的键值对映射。
        // 返回值：返回当前 Instance 对象，支持链式调用。
        // 事件处理逻辑：记录现有键的原始值，替换为新值，关闭时恢复。
        // 特殊处理：仅记录未存在于 originalValues 的键的原始值。
        public Instance putAll(final Map<String, String> values) {
            final Map<String, String> currentValues = ThreadContext.getContext();
            ThreadContext.putAll(values);
            for (final String key : values.keySet()) {
                if (!originalValues.containsKey(key)) {
                    originalValues.put(key, currentValues.get(key));
                }
            }
            return this;
        }

        /**
         * Populates the Thread Context Stack with the supplied stack. The information will be popped off when
         * the instance is closed.
         *
         * @param messages The list of messages to be added
         * @return a new instance that will back out the changes when closed.
         * @since 2.8
         */
        // 中文注释：
        // 方法目的：将一组诊断信息批量推入 ThreadContext 堆栈。
        // 参数说明：
        //   - messages：要添加的诊断信息列表。
        // 返回值：返回当前 Instance 对象，支持链式调用。
        // 事件处理逻辑：逐条调用 push 方法推入消息，关闭时自动弹出。
        public Instance pushAll(final List<String> messages) {
            for (final String message : messages) {
                push(message);
            }
            return this;
        }

        /**
         * Removes the values from the {@link ThreadContext}.
         * <p>
         * Values pushed to the {@link ThreadContext} <em>stack</em> will be popped off.
         * </p>
         * <p>
         * Values put on the {@link ThreadContext} <em>map</em> will be removed, or restored to their original values it they already existed.
         * </p>
         */
        // 中文注释：
        // 方法目的：清理 ThreadContext 中的堆栈和映射内容。
        // 事件处理逻辑：
        //   - 堆栈：根据 pushCount 计数弹出所有堆栈条目。
        //   - 映射：移除或恢复原始键值对。
        // 交互逻辑：通过 AutoCloseable 接口在 try-with-resources 结束时自动调用。
        // 注意事项：确保堆栈和映射恢复到初始状态。
        @Override
        public void close() {
            closeStack();
            closeMap();
        }

        private void closeMap() {
            for (final Iterator<Map.Entry<String, String>> it = originalValues.entrySet().iterator(); it.hasNext(); ) {
                final Map.Entry<String, String> entry = it.next();
                final String key = entry.getKey();
                final String originalValue = entry.getValue();
                if (null == originalValue) {
                    ThreadContext.remove(key);
                } else {
                    ThreadContext.put(key, originalValue);
                }
                it.remove();
            }
        }
        // 中文注释：
        // 方法目的：清理 ThreadContext 映射，恢复原始值或移除键。
        // 事件处理逻辑：遍历 originalValues，若原始值为 null 则移除键，否则恢复原始值。
        // 注意事项：确保所有键值对处理后从 originalValues 中移除，避免重复处理。

        private void closeStack() {
            for (int i = 0; i < pushCount; i++) {
                ThreadContext.pop();
            }
            pushCount = 0;
        }
        // 中文注释：
        // 方法目的：清理 ThreadContext 堆栈，弹出所有推入的条目。
        // 事件处理逻辑：根据 pushCount 计数循环调用 pop 方法，清空堆栈。
        // 注意事项：重置 pushCount 为 0，确保下次使用时计数正确。
    }
}
