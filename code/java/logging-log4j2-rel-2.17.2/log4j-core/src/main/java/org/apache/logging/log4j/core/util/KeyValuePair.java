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

import java.util.Objects;

import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

/**
 * Key/Value pair configuration item.
 * 键值对配置项。
 *
 * @since 2.1 implements {@link #hashCode()} and {@link #equals(Object)}
 * @since 2.1 版本实现了 {@link #hashCode()} 和 {@link #equals(Object)} 方法。
 */
@Plugin(name = "KeyValuePair", category = Node.CATEGORY, printObject = true)
// 这是一个插件，名称为 "KeyValuePair"，属于 Node.CATEGORY 类别，并且在打印时会输出对象内容。
public final class KeyValuePair {

    /**
     * The empty array.
     * 空的键值对数组。
     */
    public static final KeyValuePair[] EMPTY_ARRAY = {};
    // 定义一个空的 KeyValuePair 数组，用于初始化或表示没有键值对的情况。

    private final String key;
    // 键。
    private final String value;
    // 值。

    /**
     * Constructs a key/value pair. The constructor should only be called from test classes.
     * 构造一个键值对。此构造函数通常只应在测试类中调用。
     * @param key The key.
     * @param value The value.
     */
    public KeyValuePair(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key.
     * 返回键。
     * @return the key.
     * @return 键。
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value.
     * 返回值。
     * @return The value.
     * @return 值。
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + '=' + value;
    }
    // 重写 toString 方法，以 "key=value" 的形式返回字符串表示。

    @PluginBuilderFactory
    // 这是一个插件构建器工厂方法。
    public static Builder newBuilder() {
        return new Builder();
    }
    // 提供一个静态方法来获取 KeyValuePair 的构建器实例。

    public static class Builder implements org.apache.logging.log4j.core.util.Builder<KeyValuePair> {
        // 内部静态类 Builder，用于构建 KeyValuePair 对象，实现了 Builder 接口。

        @PluginBuilderAttribute
        // 标记此字段为插件构建器属性。
        private String key;
        // 构建器中的键。

        @PluginBuilderAttribute
        // 标记此字段为插件构建器属性。
        private String value;
        // 构建器中的值。

        public Builder setKey(final String aKey) {
            this.key = aKey;
            return this;
        }
        // 设置键的值，并返回当前构建器实例，支持链式调用。

        public Builder setValue(final String aValue) {
            this.value = aValue;
            return this;
        }
        // 设置值，并返回当前构建器实例，支持链式调用。

        @Override
        public KeyValuePair build() {
            return new KeyValuePair(key, value);
        }
        // 构建 KeyValuePair 对象，使用当前构建器中设置的键和值。

    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
    // 重写 hashCode 方法，基于 key 和 value 生成哈希码。

    @Override
    public boolean equals(final Object obj) {
        // 重写 equals 方法，用于比较两个 KeyValuePair 对象是否相等。
        if (this == obj) {
            // 如果是同一个对象，则返回 true。
            return true;
        }
        if (obj == null) {
            // 如果传入对象为 null，则返回 false。
            return false;
        }
        if (getClass() != obj.getClass()) {
            // 如果类类型不一致，则返回 false。
            return false;
        }
        final KeyValuePair other = (KeyValuePair) obj;
        // 将传入对象转换为 KeyValuePair 类型。
        if (!Objects.equals(key, other.key)) {
            // 比较键是否相等，使用 Objects.equals 处理 null 情况。
            return false;
        }
        if (!Objects.equals(value, other.value)) {
            // 比较值是否相等，使用 Objects.equals 处理 null 情况。
            return false;
        }
        return true;
        // 如果键和值都相等，则返回 true。
    }
}
