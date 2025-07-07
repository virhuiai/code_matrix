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

// 中文注释：
// 本文件属于 Apache Log4j 消息处理包，遵循 Apache 2.0 许可证。
// 该许可证定义了代码的使用、修改和分发的权限和限制，具体条款参见上述链接。

/**
 * Interface used to print basic or extended thread information.
 */
// 中文注释：
// ThreadInformation 接口定义了用于格式化和输出线程信息的标准方法。
// 主要功能：提供统一的接口，用于将线程信息或堆栈跟踪信息格式化输出到 StringBuilder。
// 使用场景：通常用于日志记录系统（如 Log4j），以便在日志中记录线程的详细信息或异常堆栈。
// 注意事项：实现类需要确保线程安全，因为日志记录可能在多线程环境中调用。
public interface ThreadInformation {
    
    /**
     * Formats the thread information into the provided StringBuilder.
     * @param sb The StringBuilder.
     */
    // 中文注释：
    // 方法名：printThreadInfo
    // 功能：将线程的基本信息（如线程名称、ID、状态等）格式化后追加到指定的 StringBuilder 对象中。
    // 参数：
    //   - sb: StringBuilder 对象，用于存储格式化后的线程信息。
    // 返回值：无（void），结果直接追加到输入的 StringBuilder。
    // 执行流程：
    //   1. 实现类获取当前线程的相关信息（可能包括线程名称、ID、优先级等）。
    //   2. 将这些信息按照特定格式（由实现类决定）追加到 StringBuilder。
    // 注意事项：
    //   - 实现类需确保格式化内容清晰且适合日志输出。
    //   - 应避免在多线程环境中修改共享资源，确保线程安全。
    void printThreadInfo(StringBuilder sb);

    /**
     * Formats the stack trace into the provided StringBuilder.
     * @param sb The StringBuilder.
     * @param trace The stack trace element array to format.
     */
    // 中文注释：
    // 方法名：printStack
    // 功能：将堆栈跟踪信息（由 StackTraceElement 数组提供）格式化后追加到指定的 StringBuilder 对象中。
    // 参数：
    //   - sb: StringBuilder 对象，用于存储格式化后的堆栈跟踪信息。
    //   - trace: StackTraceElement 数组，包含堆栈跟踪的详细信息（如类名、方法名、行号等）。
    // 返回值：无（void），结果直接追加到输入的 StringBuilder。
    // 执行流程：
    //   1. 接收堆栈跟踪数组（通常来自异常对象）。
    //   2. 遍历 StackTraceElement 数组，将每个元素的类名、方法名、文件名和行号等信息格式化。
    //   3. 将格式化后的信息追加到 StringBuilder。
    // 注意事项：
    //   - 实现类需确保格式化后的堆栈信息易于阅读，通常遵循标准的堆栈跟踪格式。
    //   - 如果 trace 数组为空或 null，需妥善处理以避免异常。
    //   - 实现类应考虑性能优化，避免在格式化大量堆栈信息时造成性能瓶颈。
    void printStack(StringBuilder sb, StackTraceElement[] trace);

}
