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
package org.apache.logging.log4j.core.tools;

/**
 * Wrapper around {@link Generate.ExtendedLogger}.
 * 这个类是 {@link Generate.ExtendedLogger} 的包装器。
 * 它的主要目的是提供一个简单的入口点来调用 {@code Generate.ExtendedLogger} 的功能。
 */
public class ExtendedLoggerGenerator {
    /**
     * Delegates to {@link Generate.ExtendedLogger#main(String[])}
     * 将命令行参数委托给 {@link Generate.ExtendedLogger#main(String[])} 方法。
     * 这是程序的入口点。
     * @param args the command line arguments to pass on
     * 需要传递给 {@code Generate.ExtendedLogger.main} 方法的命令行参数。
     * 这些参数通常用于控制日志生成器的行为。
     */
    public static void main(final String[] args) {
        // 调用 Generate.ExtendedLogger 类的 main 方法，并将所有命令行参数传递给它。
        // 实际的日志扩展功能由 Generate.ExtendedLogger 实现。
        Generate.ExtendedLogger.main(args);
    }
}
