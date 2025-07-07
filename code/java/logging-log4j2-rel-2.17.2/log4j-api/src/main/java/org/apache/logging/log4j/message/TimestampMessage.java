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
 * 版权声明：本文件由Apache软件基金会（ASF）授权，遵循Apache 2.0许可证。
 * 使用本文件需遵守许可证条款，详情见上述许可证链接。
 * 本文件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;
// 包声明：该接口位于Log4j日志框架的消息处理包，用于定义消息相关的功能。

/**
 * Messages that use this interface will cause the timestamp in the message to be used instead of the timestamp in
 * the LogEvent.
 */
// 中文注释：
// 接口功能：TimestampMessage 是一个接口，用于定义实现该接口的消息对象能够提供自己的时间戳，
//            该时间戳将优先于 LogEvent 中的时间戳被使用。
// 使用场景：适用于需要自定义时间戳的日志消息场景，确保日志记录使用消息本身的时间戳。
// 注意事项：实现此接口的类必须提供 getTimestamp 方法，确保返回正确的时间戳值。

public interface TimestampMessage {
    /**
     * Returns the timestamp.
     * @return The timestamp.
     */
    // 中文注释：
    // 方法功能：获取消息对象的时间戳。
    // 返回值：返回一个 long 类型的毫秒时间戳，表示消息的创建时间或指定时间。
    // 执行流程：实现类需提供具体的时间戳值，通常基于消息生成的时间或自定义时间逻辑。
    // 注意事项：返回值应为有效的毫秒时间戳，调用方依赖此值进行日志记录。
    long getTimestamp();
}
