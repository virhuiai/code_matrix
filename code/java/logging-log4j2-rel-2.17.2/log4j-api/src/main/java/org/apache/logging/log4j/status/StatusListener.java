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
// 本文件遵循Apache 2.0许可证发布，属于Apache Logging Log4j项目的一部分。
// 用途：定义日志系统中用于监听状态事件的接口。
// 注意事项：代码以“按原样”分发，不提供任何明示或暗示的担保。

package org.apache.logging.log4j.status;
// 中文注释：
// 包说明：该接口位于org.apache.logging.log4j.status包中，
// 主要用于处理Log4j日志系统的状态监听功能。

import java.io.Closeable;
import java.util.EventListener;

import org.apache.logging.log4j.Level;
// 中文注释：
// 导入说明：
// - java.io.Closeable：提供关闭资源的能力，确保监听器可以正确释放资源。
// - java.util.EventListener：标记该接口为事件监听器接口，用于处理日志系统事件。
/**
 * Interface that allows implementers to be notified of events in the logging system.
 */
// 中文注释：
// 接口说明：StatusListener 接口定义了日志系统中事件通知的契约。
// 主要功能：允许实现类监听并处理日志系统的状态事件。
// 实现要求：实现类需要处理事件数据并支持资源关闭操作。
// 使用场景：用于监控日志系统的运行状态，例如错误、警告或其他关键事件。
public interface StatusListener extends Closeable, EventListener {
    // 中文注释：
    // 接口继承说明：
    // - 继承 Closeable：提供 close() 方法，确保监听器可以释放资源。
    // - 继承 EventListener：表明这是一个事件监听器接口，用于接收日志系统事件。

    /**
     * Called as events occur to process the StatusData.
     * @param data The StatusData for the event.
     */
    // 中文注释：
    // 方法说明：log 方法用于处理日志系统中的状态事件。
    // 功能：当日志系统发生事件时，该方法会被调用以处理事件数据。
    // 参数说明：
    //   - data: StatusData 类型，表示事件的具体数据，包含事件详情（如日志级别、消息等）。
    // 返回值：无（void）。
    // 执行流程：
    //   1. 日志系统检测到事件（如错误、警告）。
    //   2. 将事件封装为 StatusData 对象。
    //   3. 调用实现类的 log 方法，传递 StatusData 对象。
    // 注意事项：实现类需确保能够正确处理 StatusData 数据，避免资源泄漏或异常。
    // 事件处理机制：该方法是事件驱动的核心，实时响应日志系统的事件。
    void log(StatusData data);

    /**
     * Return the Log Level that this listener wants included.
     * @return the Log Level.
     */
    // 中文注释：
    // 方法说明：getStatusLevel 方法用于获取监听器关心的日志级别。
    // 功能：返回实现类希望监听的日志级别（如 ERROR、WARN、INFO 等）。
    // 参数说明：无。
    // 返回值：Level 类型，表示日志级别。
    // 使用场景：日志系统根据返回的级别过滤事件，仅将符合级别的事件传递给 log 方法。
    // 执行流程：
    //   1. 日志系统调用该方法获取监听器的日志级别。
    //   2. 根据返回的级别，决定是否将事件传递给 log 方法。
    // 注意事项：
    //   - 实现类需返回有效的 Level 值，避免返回 null。
    //   - 级别设置影响监听器的事件过滤，需根据实际需求配置。
    // 配置参数说明：返回的 Level 值是关键配置，决定监听器的事件敏感度。
    Level getStatusLevel();
}
