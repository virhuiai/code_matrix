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
 * 该文件遵循 Apache 许可证 2.0 版，详细的许可信息可在上述链接中查看。
 * 文件属于 Apache Logging Log4j 项目，用于定义日志消息的多格式处理接口。
 */

package org.apache.logging.log4j.message;

/**
 * A Message that can render itself in more than one way. The format string is used by the
 * Message implementation as extra information that it may use to help it to determine how
 * to format itself. For example, MapMessage accepts a format of "XML" to tell it to render
 * the Map as XML instead of its default format of {key1="value1" key2="value2"}.
 */
/**
 * 中文注释：
 * 接口名称：MultiformatMessage
 * 主要功能：定义一个可以以多种格式渲染自身的消息接口，继承自 Message 接口。
 * 目的：允许消息根据提供的格式字符串以不同方式进行格式化输出，增强日志消息的灵活性。
 * 使用场景：例如，MapMessage 可以通过指定格式 "XML" 将键值对数据渲染为 XML 格式，而非默认的键值对字符串形式。
 * 注意事项：具体格式化逻辑由实现类决定，格式字符串仅作为辅助信息。
 */
public interface MultiformatMessage extends Message {

    /**
     * Returns the Message formatted as a String.
     *
     * @param formats An array of Strings that provide extra information about how to format the message.
     * Each MultiformatMessage implementation is free to use the provided formats however they choose.
     *
     * @return The message String.
     */
    /**
     * 中文注释：
     * 方法名称：getFormattedMessage
     * 功能：根据指定的格式化参数返回格式化后的消息字符串。
     * 参数说明：
     *   - formats：字符串数组，包含用于指导消息格式化的额外信息（如 "XML"、"JSON" 等）。
     *             实现类可根据这些格式参数自由决定如何处理和渲染消息。
     * 返回值：格式化后的消息字符串。
     * 执行流程：
     *   1. 接收 formats 参数，确定消息的渲染方式。
     *   2. 根据实现类的逻辑，使用 formats 参数对消息进行格式化。
     *   3. 返回最终的格式化字符串。
     * 注意事项：
     *   - formats 参数的具体使用方式由实现类决定，可能忽略部分或全部格式。
     *   - 如果 formats 为 null 或空，可能会使用默认格式化方式。
     */
    String getFormattedMessage(String[] formats);

    /**
     * Returns the supported formats.
     * @return The supported formats.
     */
    /**
     * 中文注释：
     * 方法名称：getFormats
     * 功能：返回当前消息对象支持的格式化方式列表。
     * 返回值：字符串数组，包含支持的格式类型（如 "XML"、"JSON" 等）。
     * 执行流程：
     *   1. 由实现类提供其支持的所有格式化类型。
     *   2. 返回格式化类型的字符串数组。
     * 使用场景：调用者可通过此方法了解消息对象支持哪些格式化选项，以便在调用 getFormattedMessage 时传入合适的格式参数。
     * 注意事项：返回的格式列表由具体实现类决定，可能为空或包含多种格式。
     */
    String[] getFormats();
}
