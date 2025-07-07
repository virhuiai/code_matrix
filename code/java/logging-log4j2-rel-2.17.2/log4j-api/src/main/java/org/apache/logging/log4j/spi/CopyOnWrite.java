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
 * 版权声明：该代码由Apache软件基金会（ASF）授权，基于Apache 2.0许可证发布。
 * 使用者需遵守许可证条款，详情请参阅http://www.apache.org/licenses/LICENSE-2.0。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.spi;
// 包声明：该接口位于org.apache.logging.log4j.spi包中，属于Log4j日志框架的SPI（服务提供接口）模块。

/**
 * Marker interface indicating that the implementing class is a copy-on-write data structure.
 *
 * @see ReadOnlyThreadContextMap#getReadOnlyContextData()
 * @since 2.7
 */
/**
 * 标记接口，用于指示实现该接口的类是写时复制（Copy-on-Write）数据结构。
 *
 * 中文注释：
 * - **主要功能和目的**：CopyOnWrite 是一个标记接口（marker interface），不定义任何方法，仅用于标识实现类具有写时复制的特性。这种设计模式常用于多线程环境中，确保数据在读取时无需加锁，提高并发性能。
 * - **关联引用**：接口与 ReadOnlyThreadContextMap#getReadOnlyContextData() 方法相关联，该方法可能返回一个实现了此接口的只读上下文数据对象。
 * - **版本信息**：自 Log4j 2.7 版本引入。
 * - **使用场景**：实现该接口的类通常用于线程安全的上下文数据管理，例如日志框架中的线程上下文映射（ThreadContextMap），以支持高效的读操作和安全的写操作。
 * - **注意事项**：写时复制模式在写操作时会创建数据副本，适合读多写少的场景，但写操作可能有较高的性能开销。
 * - **实现方式**：实现类需自行确保写时复制的逻辑，即在修改数据时创建新副本，而非直接修改原始数据。
 */
public interface CopyOnWrite {
}
