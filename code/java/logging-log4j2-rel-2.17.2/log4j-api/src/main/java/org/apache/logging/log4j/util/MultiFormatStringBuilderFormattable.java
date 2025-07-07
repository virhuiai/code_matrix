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
// 本文件遵循 Apache 许可证 2.0 版本，归属于 Apache 软件基金会 (ASF)。
// 该许可证规定了代码的版权归属、使用限制和免责声明，具体条款可在上述链接查看。

package org.apache.logging.log4j.util;

// 中文注释：
// 包名：org.apache.logging.log4j.util
// 说明：此包包含 Log4j 框架的工具类和接口，提供日志记录相关的辅助功能。

import org.apache.logging.log4j.message.MultiformatMessage;

// 中文注释：
// 导入 MultiformatMessage 接口，用于支持多种格式的消息处理。

/**
 * A Message that can render itself in more than one way. The format string is used by the
 * Message implementation as extra information that it may use to help it to determine how
 * to format itself. For example, MapMessage accepts a format of "XML" to tell it to render
 * the Map as XML instead of its default format of {key1="value1" key2="value2"}.
 *
 * @since 2.10
 */
// 中文注释：
// 接口名：MultiFormatStringBuilderFormattable
// 主要功能：定义一个可以以多种方式渲染的消息接口，允许消息根据指定的格式字符串以不同形式输出。
// 目的：为 Log4j 的消息处理提供灵活的格式化支持，允许实现类根据格式化参数动态调整输出内容。
// 使用示例：例如，MapMessage 可通过指定格式 "XML" 将键值对渲染为 XML 格式，而非默认的字符串格式。
// 版本说明：自 Log4j 2.10 版本引入。

public interface MultiFormatStringBuilderFormattable extends MultiformatMessage, StringBuilderFormattable {
    // 中文注释：
    // 接口继承：继承 MultiformatMessage 和 StringBuilderFormattable 接口。
    // MultiformatMessage：提供多格式消息处理的基础能力。
    // StringBuilderFormattable：支持将消息直接写入 StringBuilder 以提高性能。
    // 目的：通过继承这两个接口，确保实现类既支持多种格式化方式，又能高效地将消息写入 StringBuilder。

    /**
     * Writes a text representation of this object into the specified {@code StringBuilder}, ideally without allocating
     * temporary objects.
     *
     * @param formats An array of Strings that provide extra information about how to format the message.
     * Each MultiFormatStringBuilderFormattable implementation is free to use the provided formats however they choose.
     * @param buffer the StringBuilder to write into
     */
    // 中文注释：
    // 方法名：formatTo
    // 主要功能：将消息对象的文本表示形式写入指定的 StringBuilder 中，并尽量避免分配临时对象以提高性能。
    // 参数说明：
    //   - formats：字符串数组，包含格式化消息的附加信息。实现类可根据此参数自由决定如何格式化消息。
    //   - buffer：目标 StringBuilder 对象，用于存储格式化后的消息内容。
    // 返回值：无（void），直接将结果写入提供的 StringBuilder。
    // 执行流程：
    //   1. 接收 formats 数组和 StringBuilder 对象。
    //   2. 根据 formats 数组中的格式信息，决定如何将消息对象渲染为文本。
    //   3. 将格式化后的文本写入 buffer。
    // 特殊处理逻辑：
    //   - 实现类可以自由解释 formats 参数，例如选择 XML、JSON 或其他格式。
    //   - 应尽量避免创建临时对象，以优化性能。
    // 注意事项：
    //   - formats 参数可能为 null 或空数组，实现类需妥善处理。
    //   - buffer 参数不能为空，且调用方负责确保其可用性。
    void formatTo(String[] formats, StringBuilder buffer);

}
