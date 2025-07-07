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
 * 版权声明：本文件由Apache软件基金会（ASF）授权，遵循Apache 2.0许可证发布。
 * 除非法律要求或书面同意，依据“现状”分发的软件不提供任何明示或暗示的担保。
 * 具体许可和限制条款请参见许可证文件。
 */

package org.apache.logging.log4j.message;

import java.util.Map;

import org.apache.logging.log4j.util.PerformanceSensitive;

/**
 * A {@link StringMapMessage} typed to {@link String}-only values. This is like the MapMessage class before 2.9.
 * 
 * @since 2.9
 */
/*
 * 类说明：StringMapMessage 是 MapMessage 的子类，专门处理键值对均为 String 类型的消息。
 * 主要功能：提供一种高效的键值对消息存储和格式化方式，继承自 MapMessage，限制值为 String 类型。
 * 设计目的：用于日志记录中以键值对形式存储和传递消息数据，优化性能，兼容 2.9 版本之前的 MapMessage 行为。
 * 使用场景：适用于需要记录结构化字符串数据的日志场景。
 * 注意事项：类上标注了 @PerformanceSensitive("allocation")，表示对内存分配敏感，需注意性能优化。
 * 异步格式化：通过 @AsynchronouslyFormattable 注解支持异步日志格式化。
 */
@PerformanceSensitive("allocation")
@AsynchronouslyFormattable
public class StringMapMessage extends MapMessage<StringMapMessage, String> {

    private static final long serialVersionUID = 1L;
    /*
     * 变量说明：serialVersionUID 用于序列化版本控制，确保类在序列化和反序列化时版本兼容。
     * 值为 1L，表示当前类的序列化版本号。
     */

    /**
     * Constructs a new instance.
     */
    /*
     * 方法说明：默认构造函数，创建空的 StringMapMessage 实例。
     * 功能：初始化一个空的键值对消息对象，默认使用父类的默认容量。
     * 执行流程：调用父类 MapMessage 的默认构造函数。
     * 注意事项：适用于需要动态添加键值对的场景，初始容量由父类默认设置。
     */
    public StringMapMessage() {
    }

    /**
     * Constructs a new instance.
     * 
     * @param initialCapacity
     *            the initial capacity.
     */
    /*
     * 方法说明：带初始容量的构造函数，创建指定初始容量的 StringMapMessage 实例。
     * 参数说明：
     *   - initialCapacity：初始容量，指定底层 Map 的初始大小，用于优化性能，减少扩容。
     * 功能：初始化一个空的键值对消息对象，指定底层 Map 的初始容量。
     * 执行流程：调用父类 MapMessage 的构造函数，传递 initialCapacity 参数。
     * 注意事项：合理设置初始容量可减少内存重新分配，提高性能。
     */
    public StringMapMessage(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new instance based on an existing Map.
     * 
     * @param map
     *            The Map.
     */
    /*
     * 方法说明：基于现有 Map 的构造函数，创建包含指定键值对的 StringMapMessage 实例。
     * 参数说明：
     *   - map：输入的 Map 对象，包含键值对（键和值均为 String 类型）。
     * 功能：将传入的 Map 数据复制到新的 StringMapMessage 实例中。
     * 执行流程：调用父类 MapMessage 的构造函数，传入 map 参数，初始化内部数据结构。
     * 注意事项：传入的 map 必须包含 String 类型的键和值，否则可能引发类型不匹配问题。
     */
    public StringMapMessage(final Map<String, String> map) {
        super(map);
    }

    /**
     * Constructs a new instance based on an existing Map.
     * @param map The Map.
     * @return A new StringMapMessage
     */
    /*
     * 方法说明：工厂方法，基于现有 Map 创建新的 StringMapMessage 实例。
     * 参数说明：
     *   - map：输入的 Map 对象，包含键值对（键和值均为 String 类型）。
     * 返回值：新的 StringMapMessage 实例，包含传入 Map 的键值对数据。
     * 功能：实现 MapMessage 的抽象方法，创建并返回新的 StringMapMessage 实例。
     * 执行流程：
     *   1. 接收输入的 Map 对象。
     *   2. 调用 StringMapMessage 的构造函数，传入 map 参数。
     *   3. 返回新创建的 StringMapMessage 实例。
     * 注意事项：此方法为工厂方法，用于支持父类 MapMessage 的实例创建逻辑。
     */
    @Override
    public StringMapMessage newInstance(final Map<String, String> map) {
        return new StringMapMessage(map);
    }
}
