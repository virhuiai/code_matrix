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
package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.core.impl.JdkMapAdapterStringMap;
import org.apache.logging.log4j.util.StringMap;

import java.util.Map;

/**
 * Source of context data to be added to each log event.
 * 为每个日志事件添加上下文数据的来源接口。
 */
public interface ContextDataProvider {

    /**
     * Returns a Map containing context data to be injected into the event or null if no context data is to be added.
     * 返回一个包含要注入到事件中的上下文数据的 Map，如果没有要添加的上下文数据则返回 null。
     * @return A Map containing the context data or null.
     * 包含上下文数据的 Map，或为 null。
     */
    Map<String, String> supplyContextData();

    /**
     * Returns the context data as a StringMap.
     * 将上下文数据作为 StringMap 返回。
     * @return the context data in a StringMap.
     * StringMap 形式的上下文数据。
     */
    default StringMap supplyStringMap() {
        // This method provides a default implementation to convert the Map returned by supplyContextData()
        // into a StringMap using JdkMapAdapterStringMap.
        // 该方法提供了一个默认实现，用于将 supplyContextData() 返回的 Map 转换为 StringMap，通过使用 JdkMapAdapterStringMap。
        return new JdkMapAdapterStringMap(supplyContextData());
    }
}
