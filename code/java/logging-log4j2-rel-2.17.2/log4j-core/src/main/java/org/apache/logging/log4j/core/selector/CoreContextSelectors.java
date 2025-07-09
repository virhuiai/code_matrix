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

import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.apache.logging.log4j.core.async.BasicAsyncLoggerContextSelector;

public class CoreContextSelectors {
    // This class is not intended to be instantiated.
    // 这是一个不应被实例化的类。

    // The classes that are considered to be core ContextSelectors.
    // 被认为是核心上下文选择器的类。
    public static final Class<?>[] CLASSES = new Class<?>[] {
            ClassLoaderContextSelector.class,
            // Uses the ClassLoader to select the LoggerContext.
            // 使用类加载器来选择 LoggerContext。
            BasicContextSelector.class,
            // A basic ContextSelector.
            // 一个基本的上下文选择器。
            AsyncLoggerContextSelector.class,
            // An asynchronous ContextSelector.
            // 一个异步的上下文选择器。
            BasicAsyncLoggerContextSelector.class
            // A basic asynchronous ContextSelector.
            // 一个基本的异步上下文选择器。
    };

}
