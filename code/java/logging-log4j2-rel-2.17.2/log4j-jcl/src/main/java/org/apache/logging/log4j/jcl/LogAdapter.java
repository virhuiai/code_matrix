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
package org.apache.logging.log4j.jcl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.AbstractLoggerAdapter;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;

// Commons Logging适配器注册类，用于将Commons Logging与Log4j集成。
/**
 * Commons Logging adapter registry.
 *
 * @since 2.1
 */
public class LogAdapter extends AbstractLoggerAdapter<Log> {

    // 创建新的日志实例，基于给定的名称和上下文。
    // 中文注释：此方法重写父类的newLogger方法，用于生成一个新的Log4jLog实例。
    // 参数说明：
    // - name: 日志的名称，用于标识日志来源。
    // - context: 日志上下文，提供了Log4j的日志记录器。
    // 返回值：返回一个新的Log4jLog对象，适配Commons Logging接口。
    @Override
    protected Log newLogger(final String name, final LoggerContext context) {
        return new Log4jLog(context.getLogger(name));
    }

    // 获取日志上下文，决定是否依赖类加载器。
    // 中文注释：此方法重写父类的getContext方法，用于获取Log4j的日志上下文。
    // 关键逻辑：根据LogManager是否依赖类加载器，动态选择调用者的类来获取上下文。
    // 参数说明：
    // - LogManager.getFactory().isClassLoaderDependent(): 检查是否需要类加载器依赖。
    // - StackLocatorUtil.getCallerClass(LogFactory.class): 获取调用者的类，通常是LogFactory。
    // 返回值：返回合适的LoggerContext对象，用于日志记录。
    // 注意事项：此方法通过类加载器动态决定上下文，确保日志系统的灵活性和兼容性。
    @Override
    protected LoggerContext getContext() {
        return getContext(LogManager.getFactory().isClassLoaderDependent()
                ? StackLocatorUtil.getCallerClass(LogFactory.class)
                : null);
    }

}
