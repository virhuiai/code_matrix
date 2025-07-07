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
// 本文件遵循 Apache 2.0 许可证发布，归 Apache 软件基金会所有。
// 详细的版权和许可信息请参见许可证文件。

package org.apache.logging.log4j.spi;

// 中文注释：
// 包名：org.apache.logging.log4j.spi
// 用途：该包包含 Log4j 2 日志系统的 SPI（Service Provider Interface）相关接口和类，
//       用于定义日志上下文和相关服务的扩展点。

/**
 * Interface to be implemented by LoggerContext's that provide a shutdown method.
 * @since 2.6
 */
// 中文注释：
// 接口名：Terminable
// 主要功能：定义一个接口，供 LoggerContext 实现，以提供日志上下文的关闭功能。
// 目的：确保日志系统能够在需要时安全、同步地关闭，释放资源并完成未处理的日志事件。
// 使用场景：适用于需要显式关闭日志上下文的场景，例如应用程序停止或模块卸载。
// 版本信息：自 Log4j 2.6 版本引入。

public interface Terminable {

    /**
     * Requests that the logging implementation shut down.
     *
     * This call is synchronous and will block until shut down is complete.
     * This may include flushing pending log events over network connections.
     */
    // 中文注释：
    // 方法名：terminate
    // 功能：请求日志系统执行关闭操作。
    // 执行流程：
    //   1. 调用该方法以启动日志系统的关闭流程。
    //   2. 方法为同步调用，会阻塞调用线程，直到关闭过程完全完成。
    //   3. 关闭过程可能包括刷新待处理的日志事件（例如通过网络连接发送日志）。
    // 参数：无
    // 返回值：无（void）
    // 注意事项：
    //   - 该方法是同步的，调用方需要等待关闭完成，可能涉及网络操作或资源释放。
    //   - 实现类需确保所有日志事件在关闭前被正确处理，以避免数据丢失。
    //   - 调用此方法后，日志上下文可能无法再次使用，具体取决于实现。
    // 事件处理机制：此方法不直接处理事件，但会触发日志系统的内部清理逻辑，
    //               包括处理未完成的事件队列或网络传输。
    void terminate();
}
