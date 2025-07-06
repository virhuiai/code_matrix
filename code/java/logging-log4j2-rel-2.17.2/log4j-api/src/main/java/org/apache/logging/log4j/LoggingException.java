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
// 中文注释：Apache软件基金会许可声明，指定代码使用Apache 2.0许可证，明确版权归属和使用限制。

package org.apache.logging.log4j;
// 中文注释：定义代码所在的包路径，属于Apache Log4j日志框架的核心包。

/**
 * Exception thrown when an error occurs while logging.  In most cases exceptions will be handled
 * within Log4j but certain Appenders may be configured to allow exceptions to propagate to the
 * application. This is a RuntimeException so that the exception may be thrown in those cases without
 * requiring all Logger methods be contained with try/catch blocks.
 */
// 中文注释：LoggingException类，日志记录过程中发生错误时抛出的异常。
// - 主要功能：表示日志记录中的错误，允许某些Appender配置将异常传播到应用程序。
// - 关键说明：继承RuntimeException，确保无需在所有Logger方法中强制使用try/catch块。
// - 特殊处理：异常通常由Log4j内部处理，但在特定配置下可传播到上层应用。

public class LoggingException extends RuntimeException {
    // 中文注释：定义LoggingException类，继承RuntimeException，用于日志错误处理。
    // - 用途：提供日志记录错误的异常封装，支持错误信息的传递和追踪。

    private static final long serialVersionUID = 6366395965071580537L;
    // 中文注释：定义序列化版本ID，用于类版本控制。
    // - 重要配置参数：serialVersionUID = 6366395965071580537L，确保序列化兼容性。
    // - 用途：支持对象的序列化和反序列化，保持跨版本的稳定性。

    /**
     * Construct an exception with a message.
     *
     * @param message The reason for the exception
     */
    // 中文注释：构造函数，仅接受错误信息。
    // - 方法目的：创建带错误信息的LoggingException实例。
    // - 参数说明：message（字符串），描述异常原因。
    // - 交互逻辑：通过super调用父类RuntimeException的构造函数，传递错误信息。
    public LoggingException(final String message) {
        super(message);
    }

    /**
     * Construct an exception with a message and underlying cause.
     *
     * @param message The reason for the exception
     * @param cause The underlying cause of the exception
     */
    // 中文注释：构造函数，接受错误信息和根本原因。
    // - 方法目的：创建带错误信息和根本原因的LoggingException实例。
    // - 参数说明：
    //   - message（字符串）：描述异常原因。
    //   - cause（Throwable）：异常的根本原因，用于错误追踪。
    // - 交互逻辑：通过super调用父类构造函数，传递错误信息和原因。
    // - 特殊处理：允许嵌套异常，便于调试和错误链分析。
    public LoggingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an exception with an underlying cause.
     *
     * @param cause The underlying cause of the exception
     */
    // 中文注释：构造函数，仅接受根本原因。
    // - 方法目的：创建仅包含根本原因的LoggingException实例。
    // - 参数说明：cause（Throwable），异常的根本原因。
    // - 交互逻辑：通过super调用父类构造函数，传递根本原因。
    // - 特殊处理：适用于只需要传递根本原因的场景，简化错误信息构造。
    public LoggingException(final Throwable cause) {
        super(cause);
    }
}
