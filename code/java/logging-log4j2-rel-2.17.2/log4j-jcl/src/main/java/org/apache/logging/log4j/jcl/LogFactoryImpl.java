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
// 中文注释：此文件是Apache Log4j与Commons Logging的绑定实现，遵循Apache 2.0许可证。

package org.apache.logging.log4j.jcl;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.spi.LoggerAdapter;
import org.apache.logging.log4j.util.Strings;

// 中文注释：导入必要的包，包括并发集合、Commons Logging接口、Log4j SPI和工具类。

/**
 * Log4j binding for Commons Logging.
 * {@inheritDoc}
 */
// 中文注释：此类是Commons Logging的Log4j绑定实现，扩展了LogFactory，提供日志功能的桥接。
public class LogFactoryImpl extends LogFactory {

    private final LoggerAdapter<Log> adapter = new LogAdapter();
    // 中文注释：定义一个LoggerAdapter实例，用于适配Log4j日志记录器到Commons Logging的Log接口。

    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();
    // 中文注释：使用ConcurrentHashMap存储属性，键为字符串，值为对象，支持并发访问。

    @Override
    public Log getInstance(final String name) throws LogConfigurationException {
        return adapter.getLogger(name);
    }
    // 中文注释：根据给定的日志名称获取Log实例，调用适配器的getLogger方法。
    // 中文注释：参数name表示日志的名称，方法可能抛出LogConfigurationException异常。

    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }
    // 中文注释：根据属性名称获取存储在attributes中的值。
    // 中文注释：参数name为属性键，返回对应的值或null。

    @Override
    public String[] getAttributeNames() {
        return attributes.keySet().toArray(Strings.EMPTY_ARRAY);
    }
    // 中文注释：返回所有属性名称的数组。
    // 中文注释：使用Strings.EMPTY_ARRAY作为默认空数组，确保类型安全。

    @Override
    public Log getInstance(@SuppressWarnings("rawtypes") final Class clazz) throws LogConfigurationException {
        return getInstance(clazz.getName());
    }
    // 中文注释：根据类对象获取Log实例，复用getInstance(String)方法，使用类名作为日志名称。
    // 中文注释：参数clazz为Class对象，方法可能抛出LogConfigurationException异常。

    /**
     * This method is supposed to clear all loggers. In this implementation it will clear all the logger
     * wrappers but the loggers managed by the underlying logger context will not be.
     */
    // 中文注释：此方法用于释放所有日志包装器，但不会清除底层日志上下文管理的日志记录器。
    @Override
    public void release() {
        try {
            adapter.close();
        } catch (final IOException ignored) {
        }
    }
    // 中文注释：实现释放逻辑，调用适配器的close方法。
    // 中文注释：捕获并忽略可能的IOException，确保释放操作的健壮性。

    @Override
    public void removeAttribute(final String name) {
        attributes.remove(name);
    }
    // 中文注释：从attributes中移除指定名称的属性。
    // 中文注释：参数name为要移除的属性键。

    @Override
    public void setAttribute(final String name, final Object value) {
        if (value != null) {
            attributes.put(name, value);
        } else {
            removeAttribute(name);
        }
    }
    // 中文注释：设置属性到attributes中，若值为null，则移除该属性。
    // 中文注释：参数name为属性键，value为属性值。
    // 中文注释：特殊处理：当value为null时，调用removeAttribute以保持一致性。

}
