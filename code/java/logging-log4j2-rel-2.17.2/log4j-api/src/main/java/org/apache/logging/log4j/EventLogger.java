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
package org.apache.logging.log4j;

import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;

/**
 *  Logs "Events" that are represented as {@link StructuredDataMessage}.
 */
// 中文注释：此类用于记录以 StructuredDataMessage 表示的日志事件，提供事件日志的处理功能。
public final class EventLogger {

    /**
     * Defines the Event Marker.
     */
    // 中文注释：定义事件标记，用于标识日志事件，名称为 "EVENT"。
    public static final Marker EVENT_MARKER = MarkerManager.getMarker("EVENT");

    // 中文注释：定义日志记录器的名称，固定为 "EventLogger"。
    private static final String NAME = "EventLogger";

    // 中文注释：定义完全限定类名（FQCN），用于日志记录的调用者标识。
    private static final String FQCN = EventLogger.class.getName();

    // 中文注释：初始化日志记录器，基于 LogManager 获取上下文中的 ExtendedLogger 实例，名称为 NAME。
    private static final ExtendedLogger LOGGER = LogManager.getContext(false).getLogger(NAME);

    // 中文注释：私有构造函数，防止类被实例化，确保 EventLogger 作为工具类使用。
    private EventLogger() {
        // empty
    }

    /**
     * Logs events with a level of ALL.
     * @param msg The event StructuredDataMessage.
     */
    // 中文注释：记录日志事件，使用 ALL 级别（最低级别，记录所有日志）。
    // 参数说明：msg 为 StructuredDataMessage 类型的日志消息，包含结构化的事件数据。
    // 事件处理逻辑：通过 LOGGER 调用 logIfEnabled 方法，仅在日志级别启用时记录事件。
    // 特殊处理：使用 EVENT_MARKER 标记事件，异常参数为 null。
    public static void logEvent(final StructuredDataMessage msg) {
        LOGGER.logIfEnabled(FQCN, Level.OFF, EVENT_MARKER, msg, null);
    }

    /**
     * Logs events and specify the logging level.
     * @param msg The event StructuredDataMessage.
     * @param level The logging Level.
     */
    // 中文注释：记录日志事件，并允许指定日志级别。
    // 参数说明：
    //   - msg：StructuredDataMessage 类型的日志消息，包含结构化的事件数据。
    //   - level：指定的日志级别（如 DEBUG、INFO、ERROR 等）。
    // 事件处理逻辑：通过 LOGGER 调用 logIfEnabled 方法，根据指定的 level 决定是否记录事件。
    // 特殊处理：使用 EVENT_MARKER 标记事件，异常参数为 null。
    // 方法用途：提供灵活的日志级别控制，允许根据需求选择不同级别的日志记录。
    public static void logEvent(final StructuredDataMessage msg, final Level level) {
        LOGGER.logIfEnabled(FQCN, level, EVENT_MARKER, msg, null);
    }
}
