/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.logging;

// 中文注释：定义了一个异常类，属于 Apache Commons Logging 包，用于处理日志工厂或日志实例创建失败的情况。

/**
 * An exception that is thrown only if a suitable <code>LogFactory</code>
 * or <code>Log</code> instance cannot be created by the corresponding
 * factory methods.
 *
 * @version $Id$
 */
// 中文注释：此类为运行时异常，仅在无法通过工厂方法创建合适的 LogFactory 或 Log 实例时抛出。
// 中文注释：@version 表示代码版本标识，用于版本控制。
public class LogConfigurationException extends RuntimeException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 8486587136871052495L;
    // 中文注释：定义序列化版本标识，确保类在序列化和反序列化时的兼容性。
    // 中文注释：serialVersionUID 是一个重要的配置参数，用于标识类的版本，防止序列化冲突。

    /**
     * Construct a new exception with <code>null</code> as its detail message.
     */
    public LogConfigurationException() {
        super();
    }
    // 中文注释：无参构造函数，创建一个新的异常实例，详细信息设置为 null。
    // 中文注释：方法目的：提供默认的异常构造方式，用于简单的异常抛出场景。

    /**
     * Construct a new exception with the specified detail message.
     *
     * @param message The detail message
     */
    public LogConfigurationException(String message) {
        super(message);
    }
    // 中文注释：带详细信息的构造函数，允许指定异常的描述信息。
    // 中文注释：参数说明：message 表示异常的详细信息，用于描述错误原因。
    // 中文注释：方法目的：允许开发者提供自定义的错误消息，便于调试和错误追踪。

    /**
     * Construct a new exception with the specified cause and a derived
     * detail message.
     *
     * @param cause The underlying cause
     */
    public LogConfigurationException(Throwable cause) {
        this(cause == null ? null : cause.toString(), cause);
    }
    // 中文注释：带原因的构造函数，根据传入的异常原因构造新的异常实例，并生成派生的错误信息。
    // 中文注释：参数说明：cause 表示底层异常原因，用于追踪错误的根源。
    // 中文注释：方法目的：支持异常链，允许将原始异常作为原因传递，便于错误分析。
    // 中文注释：特殊处理注意事项：如果 cause 为 null，则详细信息设为 null，避免空指针问题。

    /**
     * Construct a new exception with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The underlying cause
     */
    public LogConfigurationException(String message, Throwable cause) {
        super(message + " (Caused by " + cause + ")");
        this.cause = cause; // Two-argument version requires JDK 1.4 or later
    }
    // 中文注释：带详细消息和原因的构造函数，构造异常并存储详细信息和底层原因。
    // 中文注释：参数说明：message 为异常的描述信息，cause 为底层异常原因。
    // 中文注释：方法目的：提供完整的异常信息，包括自定义消息和根本原因，支持详细的错误追踪。
    // 中文注释：特殊处理注意事项：异常信息通过拼接 message 和 cause 字符串生成，确保清晰描述错误。
    // 中文注释：重要配置参数：this.cause 用于存储异常的根本原因，需 JDK 1.4 或更高版本支持。

    /**
     * The underlying cause of this exception.
     */
    protected Throwable cause = null;
    // 中文注释：定义一个受保护的成员变量 cause，用于存储异常的根本原因。
    // 中文注释：关键变量用途说明：cause 保存导致当前异常的底层异常对象，支持异常链机制。

    /**
     * Return the underlying cause of this exception (if any).
     */
    public Throwable getCause() {
        return this.cause;
    }
    // 中文注释：获取异常的根本原因。
    // 中文注释：方法目的：返回存储在 cause 变量中的底层异常对象，便于错误分析。
    // 中文注释：返回值说明：返回 Throwable 类型，表示异常的根本原因，若无则返回 null。
}
