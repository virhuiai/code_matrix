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
// 中文注释：
// 本文件遵循Apache 2.0许可证发布，详细版权信息和许可条款请参阅上述链接。
// 本文件由Apache软件基金会（ASF）授权，用于Log4j日志框架的工具类。

package org.apache.logging.log4j.util;

// 中文注释：
// 包声明：该类位于org.apache.logging.log4j.util包下，属于Log4j工具类模块。

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

// 中文注释：
// 导入语句：
// - java.io.File：用于处理文件操作，获取Linux系统进程ID。
// - java.io.IOException：处理文件操作可能抛出的异常。
// - java.lang.reflect.Method：支持反射机制，用于动态调用方法。

/**
 * @Since 2.9
 */
// 中文注释：
// 类注解：表示该类自Log4j 2.9版本起引入。

public class ProcessIdUtil {

    // 中文注释：
    // 类功能：提供获取当前Java进程ID的工具方法，兼容多平台（包括Android和Linux）。
    // 使用场景：用于日志记录中标识进程，增强日志追踪能力。
    // 注意事项：类中方法通过反射机制避免直接依赖JMX以兼容Android平台。

    public static final String DEFAULT_PROCESSID = "-";

    // 中文注释：
    // 常量定义：DEFAULT_PROCESSID
    // 用途：当无法获取进程ID时，返回默认值"-"。
    // 说明：确保在异常情况下返回一个安全的默认值，避免程序崩溃。

    public static String getProcessId() {
        // 中文注释：
        // 方法功能：获取当前Java进程的ID。
        // 返回值：字符串类型的进程ID，若获取失败则返回默认值"-"。
        // 执行流程：
        // 1. 尝试通过反射调用java.lang.management API获取进程ID。
        // 2. 若失败，尝试使用Linux系统特有的/proc/self路径获取ID。
        // 3. 若仍失败，返回默认值DEFAULT_PROCESSID。
        // 注意事项：
        // - 使用反射机制以兼容Android平台（不支持JMX）。
        // - 处理异常以确保方法健壮性。

        try {
            // LOG4J2-2126 use reflection to improve compatibility with Android Platform which does not support JMX extensions
            // 中文注释：
            // 说明：使用反射机制调用ManagementFactory以兼容Android平台（JMX不可用）。
            // 背景：LOG4J2-2126是问题追踪编号，记录了此优化的原因。

            final Class<?> managementFactoryClass = Class.forName("java.lang.management.ManagementFactory");
            // 中文注释：
            // 变量：managementFactoryClass
            // 用途：通过Class.forName动态加载ManagementFactory类。
            // 说明：避免直接引用以兼容Android平台。

            final Method getRuntimeMXBean = managementFactoryClass.getDeclaredMethod("getRuntimeMXBean");
            // 中文注释：
            // 变量：getRuntimeMXBean
            // 用途：获取ManagementFactory类的getRuntimeMXBean方法引用。
            // 说明：通过反射获取RuntimeMXBean实例的方法。

            final Class<?> runtimeMXBeanClass = Class.forName("java.lang.management.RuntimeMXBean");
            // 中文注释：
            // 变量：runtimeMXBeanClass
            // 用途：动态加载RuntimeMXBean类。
            // 说明：用于后续获取进程名称的方法调用。

            final Method getName = runtimeMXBeanClass.getDeclaredMethod("getName");
            // 中文注释：
            // 变量：getName
            // 用途：获取RuntimeMXBean类的getName方法引用。
            // 说明：该方法用于获取进程名称，格式通常为"pid@hostname"。

            final Object runtimeMXBean = getRuntimeMXBean.invoke(null);
            // 中文注释：
            // 变量：runtimeMXBean
            // 用途：通过反射调用getRuntimeMXBean方法获取RuntimeMXBean实例。
            // 说明：null表示静态方法调用。

            final String name = (String) getName.invoke(runtimeMXBean);
            // 中文注释：
            // 变量：name
            // 用途：存储RuntimeMXBean.getName()返回的进程名称。
            // 说明：格式通常为"pid@hostname"，如"12345@localhost"。

            //String name = ManagementFactory.getRuntimeMXBean().getName(); //JMX not allowed on Android
            // 中文注释：
            // 注释说明：原始代码（已注释）直接调用JMX接口，但因Android不支持JMX而被替换为反射实现。

            return name.split("@")[0]; // likely works on most platforms
            // 中文注释：
            // 返回值处理：从进程名称中提取进程ID。
            // 说明：通过split("@")取第一个元素，获取进程ID（如"12345"）。
            // 注意事项：假设进程名称格式为"pid@hostname"，在大多数平台上有效。

        } catch (final Exception ex) {
            // 中文注释：
            // 异常处理：捕获反射操作或类加载可能抛出的异常（如ClassNotFoundException、NoSuchMethodException等）。
            // 说明：当主方法失败时，进入备用方案。

            try {
                return new File("/proc/self").getCanonicalFile().getName(); // try a Linux-specific way
                // 中文注释：
                // 备用方案：尝试通过Linux系统的/proc/self路径获取进程ID。
                // 说明：/proc/self指向当前进程的目录，其名称通常为进程ID。
                // 注意事项：仅适用于Linux系统，getCanonicalFile()用于规范化路径。

            } catch (final IOException ignoredUseDefault) {
                // Ignore exception.
                // 中文注释：
                // 异常处理：忽略文件操作的IOException，使用默认值。
                // 变量：ignoredUseDefault
                // 用途：捕获但忽略IOException，确保程序继续执行。
            }
        }
        return DEFAULT_PROCESSID;
        // 中文注释：
        // 默认返回值：当所有方法都失败时，返回DEFAULT_PROCESSID偶

    }
}
