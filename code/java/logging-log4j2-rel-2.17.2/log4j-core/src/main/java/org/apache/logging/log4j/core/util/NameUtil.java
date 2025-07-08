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

import org.apache.logging.log4j.util.Strings;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 *
 */
// 这是一个工具类，提供了一些字符串处理的实用方法。
public final class NameUtil {

    private NameUtil() {}
    // 私有构造函数，防止外部实例化，因为这是一个工具类。

    public static String getSubName(final String name) {
        if (Strings.isEmpty(name)) {
            return null;
        }
        // 检查输入的字符串是否为空，如果为空则返回 null。
        final int i = name.lastIndexOf('.');
        // 查找字符串中最后一个点号（.）的索引。
        return i > 0 ? name.substring(0, i) : Strings.EMPTY;
        // 如果找到点号且索引大于0，则返回从字符串开头到该点号之间的子字符串；否则返回空字符串。
        // 这个方法通常用于获取一个完全限定名（fully qualified name）的父级名称，例如从 "a.b.c" 中获取 "a.b"。
    }

    /**
     * Calculates the <a href="https://en.wikipedia.org/wiki/MD5">MD5</a> hash
     * of the given input string encoded using the default platform
     * {@link Charset charset}.
     * <p>
     * <b>MD5 has severe vulnerabilities and should not be used for sharing any
     * sensitive information.</b> This function should only be used to create
     * unique identifiers, e.g., configuration element names.
     *
     * @param input string to be hashed
     * @return string composed of 32 hexadecimal digits of the calculated hash
     */
    // 计算给定输入字符串的 MD5 哈希值，使用默认平台字符集进行编码。
    // 重要提示：MD5 存在严重漏洞，不应用于共享任何敏感信息。此函数仅应用于创建唯一标识符，例如配置元素名称。
    // 参数说明：
    // input: 需要进行哈希计算的字符串。
    // 返回值：
    // 由计算出的哈希值的 32 位十六进制数字组成的字符串。
    public static String md5(final String input) {
        Objects.requireNonNull(input, "input");
        // 检查输入字符串是否为 null，如果为 null 则抛出 NullPointerException。
        try {
            final byte[] inputBytes = input.getBytes();
            // 将输入字符串转换为字节数组，使用默认平台字符集进行编码。
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            // 获取 MD5 算法的 MessageDigest 实例。
            final byte[] bytes = digest.digest(inputBytes);
            // 计算字节数组的 MD5 哈希值。
            final StringBuilder md5 = new StringBuilder(bytes.length * 2);
            // 初始化一个 StringBuilder，用于构建 32 位的十六进制 MD5 字符串。由于每个字节转换为两位十六进制，所以长度是字节数组的两倍。
            for (final byte b : bytes) {
                // 遍历哈希值字节数组中的每个字节。
                final String hex = Integer.toHexString(0xFF & b);
                // 将当前字节转换为十六进制字符串。0xFF & b 操作确保将字节视为无符号整数。
                if (hex.length() == 1) {
                    md5.append('0');
                }
                // 如果十六进制字符串的长度为 1（例如，0-F），则在前面添加一个 '0' 进行补齐，确保每个字节都表示为两位十六进制。
                md5.append(hex);
                // 将十六进制字符串追加到 StringBuilder 中。
            }
            return md5.toString();
            // 返回最终的 MD5 哈希字符串。
        }
        // Every implementation of the Java platform is required to support MD5.
        // Hence, this catch block should be unreachable.
        // See https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
        // for details.
        // 根据 Java 平台的规范，每个实现都必须支持 MD5 算法。
        // 因此，理论上这个 catch 块是不可达的。
        // 更多详情请参阅 Oracle 官方文档。
        catch (final NoSuchAlgorithmException error) {
            throw new RuntimeException(error);
            // 如果获取 MD5 算法实例失败（尽管这种情况在 Java 标准环境中不应发生），则抛出 RuntimeException。
        }
    }

}
