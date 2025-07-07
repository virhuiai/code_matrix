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
package org.apache.logging.log4j.message;

import java.util.ResourceBundle;

/**
 * Creates {@link FormattedMessage} instances for {@link MessageFactory2} methods (and {@link MessageFactory} by
 * extension.)
 * 用于为 {@link MessageFactory2} 方法（以及通过扩展的 {@link MessageFactory}）创建 {@link FormattedMessage} 实例。
 *
 * <h4>Note to implementors</h4>
 * <h4>致实现者须知</h4>
 * <p>
 * This class does <em>not</em> implement any {@link MessageFactory2} methods and lets the superclass funnel those calls
 * through {@link #newMessage(String, Object...)}.
 * 此类不实现任何 {@link MessageFactory2} 方法，而是让其父类通过 {@link #newMessage(String, Object...)} 处理这些调用。
 * </p>
 */
public class LocalizedMessageFactory extends AbstractMessageFactory {
    private static final long serialVersionUID = -1996295808703146741L;
    // 序列化ID，用于版本控制

    // FIXME: cannot use ResourceBundle name for serialization until Java 8
    // FIXME: 在 Java 8 之前，不能将 ResourceBundle 名称用于序列化
    private transient final ResourceBundle resourceBundle;
    // transient 关键字表示此字段在对象序列化时不会被保存。它是一个资源束，用于根据键查找本地化消息。
    private final String baseName;
    // 资源束的基名，如果通过基名构造函数创建此工厂，则会使用此名称。

    /**
     * Constructs a LocalizedMessageFactory with a specified ResourceBundle.
     * 使用指定的 ResourceBundle 构造一个 LocalizedMessageFactory。
     *
     * @param resourceBundle The ResourceBundle to use for message localization.
     * @param resourceBundle 用于消息本地化的 ResourceBundle。
     */
    public LocalizedMessageFactory(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.baseName = null;
        // 如果提供了 ResourceBundle，则基名为 null。
    }

    /**
     * Constructs a LocalizedMessageFactory with a specified base name for the ResourceBundle.
     * 使用 ResourceBundle 的指定基名构造一个 LocalizedMessageFactory。
     *
     * @param baseName The base name of the ResourceBundle to load.
     * @param baseName 要加载的 ResourceBundle 的基名。
     */
    public LocalizedMessageFactory(final String baseName) {
        this.resourceBundle = null;
        // 如果提供了基名，则 ResourceBundle 为 null，将按需加载。
        this.baseName = baseName;
    }

    /**
     * Gets the resource bundle base name if set.
     * 获取资源束的基名（如果已设置）。
     *
     * @return the resource bundle base name if set. May be null.
     * @return 资源束的基名（如果已设置）。可能为 null。
     */
    public String getBaseName() {
        return this.baseName;
    }

    /**
     * Gets the resource bundle if set.
     * 获取资源束（如果已设置）。
     *
     * @return the resource bundle if set. May be null.
     * @return 资源束（如果已设置）。可能为 null。
     */
    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    /**
     * Creates a new {@link Message} instance based on a given key.
     * 根据给定的键创建一个新的 {@link Message} 实例。
     *
     * @param key The key string, which will be used to look up the message in the resource bundle.
     * @param key 键字符串，将用于在资源束中查找消息。
     * @return A {@link LocalizedMessage} instance.
     * @return 一个 {@link LocalizedMessage} 实例。
     * @since 2.8
     * 自 2.8 版本开始可用。
     */
    @Override
    public Message newMessage(final String key) {
        // 判断是使用 ResourceBundle 还是 baseName 来创建 LocalizedMessage
        if (resourceBundle == null) {
            // 如果 resourceBundle 为 null，则使用 baseName 和 key 创建 LocalizedMessage。
            return new LocalizedMessage(baseName,  key);
        }
        // 否则，使用 resourceBundle 和 key 创建 LocalizedMessage。
        return new LocalizedMessage(resourceBundle, key);
    }
    
    /**
     * Creates {@link LocalizedMessage} instances.
     * 创建 {@link LocalizedMessage} 实例。
     *
     * @param key The key String, used as a message if the key is absent.
     * @param key 键字符串，如果键不存在，则用作消息。
     * @param params The parameters for the message at the given key.
     * @param params 给定键的消息的参数。
     * @return The LocalizedMessage.
     * @return 本地化消息。
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(String, Object...)
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(String, Object...)
     */
    @Override
    public Message newMessage(final String key, final Object... params) {
        // 判断是使用 ResourceBundle 还是 baseName 来创建 LocalizedMessage
        if (resourceBundle == null) {
            // 如果 resourceBundle 为 null，则使用 baseName、key 和 params 创建 LocalizedMessage。
            return new LocalizedMessage(baseName, key, params);
        }
        // 否则，使用 resourceBundle、key 和 params 创建 LocalizedMessage。
        return new LocalizedMessage(resourceBundle, key, params);
    }

}
