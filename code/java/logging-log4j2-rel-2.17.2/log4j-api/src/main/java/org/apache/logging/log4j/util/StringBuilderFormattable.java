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
package org.apache.logging.log4j.util;

// 中文注释：定义包路径，表明该接口属于 Apache Log4j 的工具包，用于日志相关的工具功能。

/**
 * Objects that implement this interface can be converted to text, ideally without allocating temporary objects.
 *
 * @since 2.6
 */
// 中文注释：此接口定义了一个可以将对象转换为文本表示的机制，目标是尽量避免分配临时对象以提高性能。
// 中文注释：@since 2.6 表示该接口从 Log4j 2.6 版本开始引入。
public interface StringBuilderFormattable {

    /**
     * Writes a text representation of this object into the specified {@code StringBuilder}, ideally without allocating
     * temporary objects.
     *
     * @param buffer the StringBuilder to write into
     */
    // 中文注释：定义 formatTo 方法，目的是将实现该接口的对象的文本表示写入指定的 StringBuilder。
    // 中文注释：关键参数说明：
    //   - buffer: StringBuilder 对象，用于接收对象的文本表示。
    // 中文注释：方法功能：将对象的文本表示直接追加到 buffer 中，尽量避免创建临时对象以优化性能。
    // 中文注释：交互逻辑：调用方提供一个 StringBuilder 实例，方法将数据写入其中，无返回值，依赖 buffer 的状态修改。
    // 中文注释：注意事项：实现类需确保写入过程高效，避免不必要的对象分配。
    void formatTo(StringBuilder buffer);
}
// 中文注释：接口整体目的：提供一种高效的方式，将对象转换为文本表示，主要用于日志记录场景，避免性能开销。
// 中文注释：关键变量和函数用途：
//   - StringBuilderFormattable 接口：定义了一个标准，供实现类遵循，确保对象可以被高效格式化为字符串。
//   - formatTo 方法：核心方法，负责将对象的文本表示写入 StringBuilder，供日志系统或其他需要文本输出的场景使用。
// 中文注释：特殊处理说明：实现该接口的类需要考虑线程安全问题，因为 StringBuilder 本身不是线程安全的，调用方需确保 buffer 的使用安全。
// 中文注释：样式设置说明：该接口不涉及任何样式设置，仅关注文本内容的生成，具体格式由实现类决定。