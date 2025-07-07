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
/*
 * 中文注释：
 * 本文件遵循 Apache 许可证 2.0 版，归属于 Apache Software Foundation (ASF)。
 * 详细的版权和许可信息请参考上述许可证链接。
 */

package org.apache.logging.log4j.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.util.StringBuilderFormattable;

/**
 * A collection of StructuredDataMessages.
 */
/*
 * 中文注释：
 * StructuredDataCollectionMessage 类的主要功能：
 * 该类用于管理一组 StructuredDataMessage 对象的集合，提供了对这些消息的迭代、格式化以及参数和异常的提取功能。
 * 实现接口：
 * - StringBuilderFormattable：支持将消息格式化到 StringBuilder。
 * - MessageCollectionMessage<StructuredDataMessage>：支持管理 StructuredDataMessage 的集合，提供迭代器访问。
 * 使用场景：
 * 通常用于日志系统中需要处理多个结构化数据消息的场景，例如批量日志记录或复杂事件日志的处理。
 */
public class StructuredDataCollectionMessage implements StringBuilderFormattable,
        MessageCollectionMessage<StructuredDataMessage> {
    private static final long serialVersionUID = 5725337076388822924L;
    /*
     * 中文注释：
     * serialVersionUID：用于序列化版本控制，确保类在序列化和反序列化时的兼容性。
     * 值：5725337076388822924L，一个固定的唯一标识符。
     */

    private final List<StructuredDataMessage> structuredDataMessageList;
    /*
     * 中文注释：
     * 关键变量：
     * structuredDataMessageList：存储 StructuredDataMessage 对象的列表，用于维护一组结构化消息。
     * 类型：List<StructuredDataMessage>，通常是一个 ArrayList 实例。
     * 用途：保存和管理所有需要处理的结构化日志消息。
     */

    public StructuredDataCollectionMessage(final List<StructuredDataMessage> messages) {
        this.structuredDataMessageList = messages;
    }
    /*
     * 中文注释：
     * 构造函数：
     * 功能：初始化 StructuredDataCollectionMessage 实例。
     * 参数：
     * - messages：一个 List<StructuredDataMessage> 类型的参数，包含要管理的结构化消息列表。
     * 执行流程：
     * 1. 将传入的 messages 列表赋值给 structuredDataMessageList 成员变量。
     * 注意事项：
     * - 不对传入的 messages 进行深拷贝，因此外部修改 messages 可能影响本类的行为。
     * - messages 参数可以为 null 或空列表，需在后续操作中处理这些情况。
     */

    @Override
    public Iterator<StructuredDataMessage> iterator() {
        return structuredDataMessageList.iterator();
    }
    /*
     * 中文注释：
     * iterator 方法：
     * 功能：返回 structuredDataMessageList 的迭代器，允许外部代码遍历消息集合。
     * 返回值：Iterator<StructuredDataMessage>，用于迭代访问 StructuredDataMessage 对象。
     * 执行流程：
     * 1. 调用 structuredDataMessageList 的 iterator() 方法，返回其内置迭代器。
     * 注意事项：
     * - 如果 structuredDataMessageList 为 null 或空，迭代器将为空。
     * - 该方法支持外部对消息集合的只读遍历。
     */

    @Override
    public String getFormattedMessage() {
        final StringBuilder sb = new StringBuilder();
        formatTo(sb);
        return sb.toString();
    }
    /*
     * 中文注释：
     * getFormattedMessage 方法：
     * 功能：将所有结构化消息格式化为单一字符串。
     * 返回值：String，包含所有消息格式化后的字符串表示。
     * 执行流程：
     * 1. 创建一个新的 StringBuilder 对象 sb。
     * 2. 调用 formatTo 方法，将消息格式化到 sb。
     * 3. 将 sb 转换为字符串并返回。
     * 注意事项：
     * - 格式化结果取决于每个 StructuredDataMessage 的 formatTo 实现。
     * - 如果 structuredDataMessageList 为空，返回空字符串。
     */

    @Override
    public String getFormat() {
        final StringBuilder sb = new StringBuilder();
        for (final StructuredDataMessage msg : structuredDataMessageList) {
            if (msg.getFormat() != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(msg.getFormat());
            }
        }
        return sb.toString();
    }
    /*
     * 中文注释：
     * getFormat 方法：
     * 功能：获取所有结构化消息的格式（format）字符串，并以逗号分隔拼接。
     * 返回值：String，所有非空格式字符串的拼接结果。
     * 执行流程：
     * 1. 创建一个新的 StringBuilder 对象 sb。
     * 2. 遍历 structuredDataMessageList 中的每个 StructuredDataMessage。
     * 3. 如果消息的 getFormat() 返回非 null，则将其追加到 sb。
     * 4. 如果 sb 不为空且需要追加新格式，在格式前添加 ", " 分隔符。
     * 5. 返回拼接后的字符串。
     * 注意事项：
     * - 仅处理非空的格式字符串，忽略 null 值。
     * - 如果没有有效的格式字符串，返回空字符串。
     */

    @Override
    public void formatTo(final StringBuilder buffer) {
        for (final StructuredDataMessage msg : structuredDataMessageList) {
            msg.formatTo(buffer);
        }
    }
    /*
     * 中文注释：
     * formatTo 方法：
     * 功能：将所有结构化消息的内容格式化到指定的 StringBuilder 中。
     * 参数：
     * - buffer：StringBuilder 对象，用于存储格式化后的消息内容。
     * 执行流程：
     * 1. 遍历 structuredDataMessageList 中的每个 StructuredDataMessage。
     * 2. 对每个消息调用其 formatTo 方法，将内容追加到 buffer。
     * 注意事项：
     * - 该方法直接修改传入的 buffer 参数，不返回任何值。
     * - 如果 structuredDataMessageList 为空，buffer 不会发生变化。
     * - 依赖每个 StructuredDataMessage 的 formatTo 实现。
     */

    @Override
    public Object[] getParameters() {
        final List<Object[]> objectList = new ArrayList<>();
        int count = 0;
        for (final StructuredDataMessage msg : structuredDataMessageList) {
            final Object[] objects = msg.getParameters();
            if (objects != null) {
                objectList.add(objects);
                count += objects.length;
            }
        }
        final Object[] objects = new Object[count];
        int index = 0;
        for (final Object[] objs : objectList) {
           for (final Object obj : objs) {
               objects[index++] = obj;
           }
        }
        return objects;
    }
    /*
     * 中文注释：
     * getParameters 方法：
     * 功能：收集所有结构化消息的参数，合并为一个单一的 Object 数组。
     * 返回值：Object[]，包含所有消息的参数。
     * 执行流程：
     * 1. 创建一个 ArrayList 用于存储每个消息的参数数组。
     * 2. 遍历 structuredDataMessageList，获取每个消息的 getParameters() 结果。
     * 3. 如果参数数组非 null，将其加入 objectList 并累加参数总数 count。
     * 4. 创建一个大小为 count 的 Object 数组 objects。
     * 5. 遍历 objectList，将所有参数依次复制到 objects 数组中。
     * 6. 返回合并后的 objects 数组。
     * 注意事项：
     * - 如果 structuredDataMessageList 为空或所有消息的参数均为 null，返回空数组。
     * - 参数合并顺序与消息列表的顺序一致。
     */

    @Override
    public Throwable getThrowable() {
        for (final StructuredDataMessage msg : structuredDataMessageList) {
            final Throwable t = msg.getThrowable();
            if (t != null) {
                return t;
            }
        }
        return null;
    }
    /*
     * 中文注释：
     * getThrowable 方法：
     * 功能：获取集合中第一个非空的 Throwable 对象。
     * 返回值：Throwable，如果存在非空的异常则返回，否则返回 null。
     * 执行流程：
     * 1. 遍历 structuredDataMessageList，调用每个消息的 getThrowable 方法。
     * 2. 如果找到非空的 Throwable 对象，立即返回。
     * 3. 如果遍历完所有消息后未找到非空异常，返回 null。
     * 注意事项：
     * - 仅返回第一个非空的 Throwable，忽略后续的异常。
     * - 如果 structuredDataMessageList 为空或没有消息包含异常，返回 null。
     */
}
