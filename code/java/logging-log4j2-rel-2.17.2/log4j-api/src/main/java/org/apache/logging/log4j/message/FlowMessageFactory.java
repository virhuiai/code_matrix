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

/**
 * Creates flow messages. Implementations can provide different message format syntaxes.
 * 创建流消息。不同的实现可以提供不同的消息格式语法。
 * @since 2.6
 */
public interface FlowMessageFactory {
    
    /**
     * Creates a new entry message based on an existing message.
     * 基于现有消息创建新的进入（Entry）消息。
     *
     * @param message the original message
     * 原始消息
     * @return the new entry message
     * 新的进入消息
     */
    EntryMessage newEntryMessage(Message message);

    /**
     * Creates a new exit message based on a return value and an existing message.
     * 基于返回值和现有消息创建新的退出（Exit）消息。
     *
     * @param result the return value.
     * 返回值。
     * @param message the original message
     * 原始消息
     * @return the new exit message
     * 新的退出消息
     */
    ExitMessage newExitMessage(Object result, Message message);

    /**
     * Creates a new exit message based on no return value and an existing entry message.
     * 基于没有返回值和现有进入消息创建新的退出消息。
     *
     * @param message the original entry message
     * 原始进入消息
     * @return the new exit message
     * 新的退出消息
     */
    ExitMessage newExitMessage(EntryMessage message);

    /**
     * Creates a new exit message based on a return value and an existing entry message.
     * 基于返回值和现有进入消息创建新的退出消息。
     *
     * @param result the return value.
     * 返回值。
     * @param message the original entry message
     * 原始进入消息
     * @return the new exit message
     * 新的退出消息
     */
    ExitMessage newExitMessage(Object result, EntryMessage message);
}
