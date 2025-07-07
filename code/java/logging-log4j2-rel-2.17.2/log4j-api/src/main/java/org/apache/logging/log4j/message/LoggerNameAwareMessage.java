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
 * 本文件遵循 Apache 2.0 许可证发布，由 Apache Software Foundation (ASF) 提供。
 * 文件包含版权声明和许可证信息，详细内容可参考上述许可证链接。
 */

package org.apache.logging.log4j.message;

/**
 *  Message that is interested in the name of the Logger.
 *
 *  中文注释：
 *  LoggerNameAwareMessage 是一个接口，定义了与 Logger 名称相关的消息处理功能。
 *  主要功能和目的：
 *    - 该接口用于需要感知或操作 Logger 名称的消息对象。
 *    - 提供方法来设置和获取 Logger 的名称，以便在日志记录过程中标识消息来源。
 *  使用场景：
 *    - 在日志框架中，某些消息需要关联特定的 Logger 名称，以便在日志输出时明确消息来源。
 *  注意事项：
 *    - 实现该接口的类需要确保 Logger 名称的正确设置和获取，以避免日志信息来源不明确。
 */
public interface LoggerNameAwareMessage {
    /**
     * The name of the Logger.
     * @param name The name of the Logger.
     *
     * 中文注释：
     * 方法功能：
     *   - setLoggerName 用于设置与消息关联的 Logger 名称。
     * 参数说明：
     *   - name: String 类型，表示 Logger 的名称，通常是 Logger 实例的唯一标识。
     * 执行流程：
     *   - 实现类通过此方法存储传入的 Logger 名称，以便后续通过 getLoggerName 获取。
     * 注意事项：
     *   - 实现类应确保 name 参数非空或有效，以避免后续处理中的异常。
     *   - 该方法通常在消息初始化或配置时调用。
     */
    void setLoggerName(String name);

    /**
     * Returns the name of the Logger.
     * @return The name of the Logger.
     *
     * 中文注释：
     * 方法功能：
     *   - getLoggerName 用于获取与消息关联的 Logger 名称。
     * 返回值：
     *   - String 类型，返回当前消息关联的 Logger 名称。
     * 执行流程：
     *   - 实现类返回之前通过 setLoggerName 设置的 Logger 名称。
     * 注意事项：
     *   - 如果未设置 Logger 名称，需明确返回 null 或默认值，具体取决于实现类的逻辑。
     *   - 该方法通常在日志记录或消息处理时调用，以标识消息的来源 Logger。
     */
    String getLoggerName();
}
