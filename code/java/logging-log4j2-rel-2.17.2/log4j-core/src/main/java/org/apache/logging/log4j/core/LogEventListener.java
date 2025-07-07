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
package org.apache.logging.log4j.core;

import java.util.EventListener;

import org.apache.logging.log4j.status.StatusLogger;

/**
 * Base class for server classes that listen to {@link LogEvent}s.
 * 监听 {@link LogEvent} 的服务器类的基类。
 * TODO (MS) How is this class any different from Appender?
 * TODO (MS) 这个类与 Appender 有何不同？
 */
public class LogEventListener implements EventListener {

    protected static final StatusLogger LOGGER = StatusLogger.getLogger();
    // LOGGER：用于记录状态信息的日志记录器。
    private final LoggerContext context;
    // context：当前日志上下文，用于获取Logger实例。

    protected LogEventListener() {
        // 无参构造函数
        context = LoggerContext.getContext(false);
        // 获取当前的LoggerContext实例，参数false表示不创建新的上下文，如果不存在则返回null。
    }

    /**
     * Logs a {@link LogEvent}.
     * 记录一个 {@link LogEvent}。
     * @param event The LogEvent to log.
     * 要记录的日志事件。
     */
    public void log(final LogEvent event) {
        // 方法的主要功能是处理并记录传入的LogEvent。
        if (event == null) {
            // 如果传入的LogEvent为空，则直接返回，不进行处理。
            return;
        }
        final Logger logger = context.getLogger(event.getLoggerName());
        // 根据LogEvent中的Logger名称从LoggerContext获取对应的Logger实例。
        // logger：根据日志事件的loggerName获取到的Logger实例。
        if (logger.privateConfig.filter(event.getLevel(), event.getMarker(), event.getMessage(), event.getThrown())) {
            // 检查Logger的私有配置（privateConfig）是否允许记录此事件。
            // 过滤条件包括日志级别、标记、消息和异常信息。
            // 如果通过过滤，则继续记录日志。
            logger.privateConfig.logEvent(event);
            // 调用Logger的私有配置（privateConfig）来实际记录这个LogEvent。
            // 这是日志事件最终被处理和发布的地方。
        }
    }
}
