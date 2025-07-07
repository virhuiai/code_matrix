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

import java.io.Closeable;

/**
 * A basic registry for {@link LoggerContext} objects and their associated external
 * Logger classes. This registry should not be used for Log4j Loggers; it is instead used for creating bridges to
 * other external log systems.
 * 一个用于管理 LoggerContext 对象及其关联的外部 Logger 类的基本注册接口。
 * 此注册表不应用于 Log4j 自身的 Loggers；它的主要目的是为其他外部日志系统创建桥接。
 *
 * @param <L> the external logger class for this registry (e.g., {@code org.slf4j.Logger})
 * @param <L> 此注册表所使用的外部日志器类，例如 {@code org.slf4j.Logger}。
 * @since 2.1
 * @since 2.1 版本开始引入。
 */
public interface LoggerAdapter<L> extends Closeable {

    /**
     * Gets a named logger. Implementations should defer to the abstract methods in {@link AbstractLoggerAdapter}.
     * 获取一个指定名称的日志器。
     * 具体实现应委托给 {@link AbstractLoggerAdapter} 中的抽象方法。
     *
     * @param name the name of the logger to get
     * @param name 要获取的日志器的名称。
     * @return the named logger
     * @return 对应名称的日志器实例。
     */
    L getLogger(String name);
}
