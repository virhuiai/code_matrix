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
package org.apache.logging.log4j.spi;

/**
 * Interface allowing interested classes to know when a LoggerContext has shutdown - if the LoggerContext
 * implementation provides a way to register listeners.
 * 接口，允许感兴趣的类在 LoggerContext 关闭时得到通知——前提是 LoggerContext
 * 实现提供了注册监听器的方式。
 *
 * 主要功能和目的：
 * 此接口定义了一个回调方法，用于在 Log4j 的 LoggerContext（日志上下文）实例关闭时通知相关的监听器。
 * 它提供了一种机制，使得应用程序或其他组件可以在日志系统停止运行时执行清理或收尾工作。
 */
public interface LoggerContextShutdownAware {

    /**
     * Called when the LoggerContext is shut down.
     * 当 LoggerContext 关闭时调用此方法。
     *
     * @param loggerContext The LoggerContext that is shutting down.
     * loggerContext: 正在关闭的 LoggerContext 实例。
     *
     * 方法执行流程和关键步骤：
     * 当 Log4j 框架决定关闭一个 LoggerContext 时，如果该 LoggerContext 实现了注册 LoggerContextShutdownAware 监听器的方式，
     * 那么它会遍历所有已注册的监听器，并对每个监听器调用此 contextShutdown 方法，传入即将关闭的 LoggerContext 实例。
     * 特殊处理逻辑和注意事项：
     * 实现此接口的类应在此方法中处理 LoggerContext 关闭后的清理工作，例如释放资源、关闭连接等。
     * 需要注意的是，此方法是在 LoggerContext 关闭过程中被调用的，因此不应在此方法中执行耗时或可能阻塞关闭流程的操作。
     */
    void contextShutdown(LoggerContext loggerContext);
}
