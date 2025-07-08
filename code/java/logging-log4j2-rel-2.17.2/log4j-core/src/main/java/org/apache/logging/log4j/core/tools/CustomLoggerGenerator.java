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
 * Wrapper around {@link Generate.CustomLogger}.
 * 这个类是 {@link Generate.CustomLogger} 的一个包装器。
 * 主要目的是提供一个简单的入口点，用于生成自定义的日志记录器。
 */
public class CustomLoggerGenerator {
    /**
     * Delegates to {@link Generate.CustomLogger#main(String[])}
     * 将命令行参数委托给 {@link Generate.CustomLogger#main(String[])} 方法。
     * 这是该工具的入口点。
     * @param args the command line arguments to pass on
     * 命令行参数，这些参数将被传递给底层的 Generate.CustomLogger 工具。
     * 通常用于指定生成自定义日志记录器的相关配置，例如类名、包名等。
     */
    public static void main(final String[] args) {
        // 调用 Generate.CustomLogger 的 main 方法，执行实际的自定义日志记录器生成逻辑。
        // 代码执行流程：程序启动时，main方法被调用，然后它直接将所有传入的参数转发给 Generate.CustomLogger.main 方法。
        Generate.CustomLogger.main(args);
    }
}
