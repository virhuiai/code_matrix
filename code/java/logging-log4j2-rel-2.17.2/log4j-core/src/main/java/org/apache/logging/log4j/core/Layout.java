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

import java.io.Serializable;
import java.util.Map;

import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.layout.Encoder;

/**
 * Lays out a {@linkplain LogEvent} in different formats.
 * 将 LogEvent 转换为不同的输出格式。
 *
 * The formats are:
 * 支持的格式包括：
 * <ul>
 * <li>
 * {@code byte[]}</li>
 * 字节数组
 * <li>
 * an implementer of {@linkplain Serializable}, like {@code byte[]}</li>
 * 实现 Serializable 接口的对象，例如字节数组
 * <li>
 * {@linkplain String}</li>
 * 字符串
 * <li>
 * {@linkplain LogEvent}</li>
 * LogEvent 对象本身
 * </ul>
 * <p>
 * Since 2.6, Layouts can {@linkplain Encoder#encode(Object, ByteBufferDestination) encode} a {@code LogEvent} directly
 * to a {@link ByteBufferDestination} without creating temporary intermediary objects.
 * 从 2.6 版本开始，Layout 可以直接将 LogEvent 编码到 ByteBufferDestination，而无需创建临时的中间对象，提高了效率。
 * </p>
 *
 * @param <T>
 *            The {@link Serializable} type returned by {@link #toSerializable(LogEvent)}
 * toSerializable(LogEvent) 方法返回的可序列化类型。
 */
public interface Layout<T extends Serializable> extends Encoder<LogEvent> {

    /**
     * Main {@linkplain org.apache.logging.log4j.core.config.plugins.Plugin#elementType() plugin element type} for
     * Layout plugins.
     * Layout 插件的主要插件元素类型。
     *
     * @since 2.1
     * 从 2.1 版本开始引入。
     */
    String ELEMENT_TYPE = "layout";

    /**
     * Returns the format for the layout format.
     * 返回布局格式的页脚。
     * @return The footer.
     * 页脚内容，通常是字节数组形式。
     */
    byte[] getFooter();

    /**
     * Returns the header for the layout format.
     * 返回布局格式的页眉。
     * @return The header.
     * 页眉内容，通常是字节数组形式。
     */
    byte[] getHeader();

    /**
     * Formats the event suitable for display.
     * 格式化日志事件，使其适合显示。
     *
     * @param event The Logging Event.
     * 要格式化的日志事件。
     * @return The formatted event.
     * 格式化后的日志事件，以字节数组形式返回。
     */
    byte[] toByteArray(LogEvent event);

    /**
     * Formats the event as an Object that can be serialized.
     * 将日志事件格式化为可序列化的对象。
     *
     * @param event The Logging Event.
     * 要格式化的日志事件。
     * @return The formatted event.
     * 格式化后的日志事件，以指定的可序列化类型 T 返回。
     */
    T toSerializable(LogEvent event);

    /**
     * Returns the content type output by this layout. The base class returns "text/plain".
     * 返回此布局输出的内容类型。默认实现通常返回 "text/plain"。
     *
     * @return the content type.
     * 内容类型字符串，例如 "text/plain" 或 "application/json"。
     */
    String getContentType();

    /**
     * Returns a description of the content format.
     * 返回内容格式的描述。
     *
     * @return a Map of key/value pairs describing the Layout-specific content format, or an empty Map if no content
     * format descriptors are specified.
     * 一个包含键值对的 Map，描述了 Layout 特定的内容格式。如果没有指定内容格式描述符，则返回一个空 Map。
     */
    Map<String, String> getContentFormat();
}
