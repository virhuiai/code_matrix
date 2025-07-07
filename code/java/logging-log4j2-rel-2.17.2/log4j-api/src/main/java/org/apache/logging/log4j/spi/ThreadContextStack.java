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
// 版权声明：此文件由Apache软件基金会（ASF）根据Apache 2.0许可证授权。
// 使用限制：除非适用法律要求或书面同意，软件按“原样”分发，不提供任何明示或暗示的担保。
// 许可证详情：请参阅 http://www.apache.org/licenses/LICENSE-2.0 查看具体权限和限制。

package org.apache.logging.log4j.spi;
// 中文注释：
// 包说明：此接口位于 org.apache.logging.log4j.spi 包中，属于 Log4j 2 的服务提供者接口（SPI）模块，
// 用于定义日志记录相关的扩展点，供实现自定义日志行为。

import org.apache.logging.log4j.ThreadContext;
// 中文注释：
// 导入说明：导入 ThreadContext 类，ThreadContext 是 Log4j 2 中用于管理线程上下文（如 MDC 和 NDC）的核心类，
// 本接口与 ThreadContext 配合使用以实现嵌套诊断上下文（NDC）的自定义行为。

/**
 * Service provider interface to implement custom NDC behavior for {@link ThreadContext}.
 */
// 中文注释：
// 接口功能：ThreadContextStack 是一个服务提供者接口（SPI），用于实现自定义的嵌套诊断上下文（NDC）行为，
// 扩展 ThreadContext 的上下文堆栈功能。
// 目的：允许开发者通过实现此接口，自定义 NDC 的堆栈操作逻辑（如 push、pop、clear 等），以满足特定场景的日志记录需求。
// 使用场景：NDC 用于在日志中跟踪线程的上下文信息（如用户会话、事务 ID 等），本接口提供了一种机制，
// 允许用户自定义堆栈数据结构和管理方式。
// 注意事项：实现此接口的类需要与 ThreadContext 配合使用，确保线程安全的上下文管理。

public interface ThreadContextStack extends ThreadContext.ContextStack {
    // empty
}
// 中文注释：
// 接口定义：ThreadContextStack 继承自 ThreadContext.ContextStack，定义了一个空的接口，
// 不包含任何方法声明，具体的方法由父接口 ContextStack 提供。
// 父接口说明：ThreadContext.ContextStack 是一个抽象接口，通常定义了堆栈操作的基本方法，如 push、pop、peek、clear 等，
// 用于管理 NDC 的上下文堆栈。
// 实现要求：实现类需要提供线程安全的堆栈操作逻辑，确保多线程环境下 NDC 数据的正确性。
// 执行流程：
// 1. 用户通过实现此接口，定义自定义的 NDC 堆栈行为。
// 2. ThreadContext 在运行时通过 SPI 机制加载实现类。
// 3. 日志记录时，ThreadContext 调用堆栈方法（如 push、pop）管理上下文数据。
// 4. 日志输出时，NDC 数据被附加到日志消息中，用于追踪线程上下文。
// 注意事项：
// - 实现类必须考虑线程安全，因为 ThreadContext 通常在多线程环境中使用。
// - 堆栈操作应高效，避免对日志性能产生显著影响。
// - 实现类需要遵循父接口 ContextStack 的契约，确保方法行为一致。