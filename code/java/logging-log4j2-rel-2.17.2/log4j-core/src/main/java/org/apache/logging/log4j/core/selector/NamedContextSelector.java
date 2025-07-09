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
package org.apache.logging.log4j.core.selector;

import java.net.URI;

import org.apache.logging.log4j.core.LoggerContext;

/**
 * ContextSelectors that have a name.
 * 具有名称的上下文选择器。
 */
public interface NamedContextSelector extends ContextSelector {

    /**
     * Locate the LoggerContext with the specified name.
     * 根据指定的名称定位LoggerContext。
     * @param name The LoggerContext name.
     * LoggerContext的名称。
     * @param externalContext The external context to associate with the LoggerContext.
     * 与LoggerContext关联的外部上下文。
     * @param configLocation The location of the configuration.
     * 配置文件的位置。
     * @return A LoggerContext.
     * 返回一个LoggerContext实例。
     */
    LoggerContext locateContext(String name, Object externalContext, URI configLocation);

    /**
     * Locate the LoggerContext with the specified name using the default configuration.
     * 使用默认配置定位具有指定名称的LoggerContext。
     * @param name The LoggerContext name.
     * LoggerContext的名称。
     * @return A LoggerContext.
     * 返回一个LoggerContext实例。
     */
    LoggerContext removeContext(String name);
}
