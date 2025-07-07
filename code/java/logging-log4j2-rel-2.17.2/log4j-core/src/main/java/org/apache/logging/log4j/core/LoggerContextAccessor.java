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

/**
 * Provides access to a Core Logger Context.
 * 提供对核心日志上下文 (Core Logger Context) 的访问。
 * <p>
 * 此接口定义了一个单一方法，用于获取与当前上下文关联的 LoggerContext 实例。
 * 它可以被那些需要操作或查询日志上下文的组件实现。
 * </p>
 */
public interface LoggerContextAccessor {

    /**
     * Gets the LoggerContext.
     * 获取日志上下文。
     *
     * @return The LoggerContext.
     * @return 返回 {@link LoggerContext} 实例。这个实例代表了 Log4j 的核心配置和运行时环境。
     */
    LoggerContext getLoggerContext();

}
