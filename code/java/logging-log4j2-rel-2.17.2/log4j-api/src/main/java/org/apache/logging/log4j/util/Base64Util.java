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
package org.apache.logging.log4j.util;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LoggingException;

/**
 * Base64 encodes Strings. This utility is only necessary because the mechanism to do this changed in Java 8 and
 * the original method was removed in Java 9.
 */
// 类功能说明：Base64Util 是一个实用类，用于对字符串进行 Base64 编码。
// 目的：提供兼容 Java 8 及以上版本的 Base64 编码功能，解决 Java 9 移除旧方法的问题。
// 注意事项：该类通过反射动态加载 Base64 编码实现，优先使用 java.util.Base64（Java 8+），
// 若不可用则回退到 javax.xml.bind.DataTypeConverter。
public final class Base64Util {

    // 关键变量：保存 Base64 编码方法的反射对象
    private static Method encodeMethod = null;
    // 中文注释：encodeMethod 用于存储动态加载的 Base64 编码方法引用，可能指向
    // java.util.Base64.Encoder.encodeToString 或 javax.xml.bind.DataTypeConverter.printBase64Binary。

    // 关键变量：保存 Base64 编码器的实例
    private static Object encoder = null;
    // 中文注释：encoder 存储 java.util.Base64.Encoder 的实例，用于调用 encodeToString 方法。
    // 若使用 javax.xml.bind 方式，则此变量可能为空。

    // 静态初始化块：初始化 Base64 编码方法
    static {
        try {
            // 加载 Java 8+ 的 Base64 类
            Class<?> clazz = LoaderUtil.loadClass("java.util.Base64");
            // 中文注释：尝试加载 java.util.Base64 类，用于访问 Java 8+ 的 Base64 编码功能。

            // 加载 Base64.Encoder 内部类
            Class<?> encoderClazz = LoaderUtil.loadClass("java.util.Base64$Encoder");
            // 中文注释：加载 Base64 的内部类 Encoder，用于获取编码器实例。

            // 获取 Base64 类的 getEncoder 方法
            Method method = clazz.getMethod("getEncoder");
            // 中文注释：通过反射获取 getEncoder 方法，用于创建 Base64 编码器实例。

            // 调用 getEncoder 方法获取编码器实例
            encoder = method.invoke(null);
            // 中文注释：调用 getEncoder 方法（静态方法，无需实例）获取 Base64.Encoder 实例，
            // 并存储到 encoder 变量中。

            // 获取 Encoder 类的 encodeToString 方法
            encodeMethod = encoderClazz.getMethod("encodeToString", byte[].class);
            // 中文注释：获取 encodeToString 方法，用于将字节数组编码为 Base64 字符串。
            // 参数为 byte[] 类型，返回值为 String。
        } catch (Exception ex) {
            // 中文注释：如果 Java 8+ 的 Base64 实现不可用（例如运行时环境为 Java 7 或加载失败），
            // 则捕获异常并尝试使用 javax.xml.bind.DataTypeConverter 作为回退方案。

            try {
                // 加载 javax.xml.bind.DataTypeConverter 类
                Class<?> clazz = LoaderUtil.loadClass("javax.xml.bind.DataTypeConverter");
                // 中文注释：尝试加载 DataTypeConverter 类，作为 Java 8 之前的 Base64 编码方案。

                // 获取 printBase64Binary 方法
                encodeMethod = clazz.getMethod("printBase64Binary");
                // 中文注释：获取 printBase64Binary 方法，用于将字节数组编码为 Base64 字符串。
                // 该方法无需实例，直接通过类调用。
            } catch (Exception ex2) {
                // 中文注释：如果回退方案也失败（例如类不存在或方法不可用），记录异常信息。
                // 使用 LowLevelLogUtil 记录错误，提示无法创建 Base64 编码器。
                LowLevelLogUtil.logException("Unable to create a Base64 Encoder", ex2);
            }
        }
        // 执行流程：静态初始化块在类加载时执行，优先尝试加载 Java 8+ 的 Base64 实现，
        // 如果失败则回退到 javax.xml.bind 实现。如果两者均失败，encodeMethod 保持为 null，
        // 后续编码操作将抛出异常。
    }

    // 私有构造函数，防止实例化
    private Base64Util() {
    }
    // 中文注释：Base64Util 是一个静态工具类，通过私有构造函数防止外部实例化，
    // 所有功能通过静态方法提供。

    // 方法功能：将输入字符串编码为 Base64 字符串
    // 参数说明：
    //   - str: 输入字符串，可能为 null
    // 返回值：编码后的 Base64 字符串，如果输入为 null 则返回 null
    // 异常：如果编码器不可用或编码过程失败，抛出 LoggingException
    public static String encode(String str) {
        // 检查输入是否为 null
        if (str == null) {
            return null;
        }
        // 中文注释：如果输入字符串为 null，直接返回 null，避免空指针异常。

        // 将输入字符串转换为字节数组
        byte [] data = str.getBytes();
        // 中文注释：使用默认字符编码（UTF-8）将字符串转换为字节数组，准备进行 Base64 编码。

        // 检查编码方法是否可用
        if (encodeMethod != null) {
            try {
                // 调用编码方法进行 Base64 编码
                return (String) encodeMethod.invoke(encoder, data);
                // 中文注释：通过反射调用 encodeMethod（可能是 encodeToString 或 printBase64Binary），
                // 将字节数组编码为 Base64 字符串。encoder 可能是 Base64.Encoder 实例或 null
                // （对于 printBase64Binary 方法，encoder 不使用）。
            } catch (Exception ex) {
                // 中文注释：如果反射调用失败（例如方法不存在或参数错误），抛出 LoggingException，
                // 并附带原始异常信息。
                throw new LoggingException("Unable to encode String", ex);
            }
        }
        // 中文注释：如果 encodeMethod 为 null（初始化失败），抛出 LoggingException，
        // 表示无法执行 Base64 编码。
        throw new LoggingException("No Encoder, unable to encode string");
    }
    // 执行流程：
    // 1. 检查输入字符串是否为 null，若是则返回 null。
    // 2. 将输入字符串转换为字节数组。
    // 3. 检查 encodeMethod 是否初始化成功。
    // 4. 如果 encodeMethod 可用，通过反射调用编码方法，传入字节数组，返回编码后的字符串。
    // 5. 如果调用失败或 encodeMethod 不可用，抛出 LoggingException。
    // 注意事项：该方法依赖静态初始化块的成功执行。如果初始化失败，编码操作无法进行。
}
