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

import java.nio.charset.Charset;

/**
 * Encodes Strings to bytes.
 * 将字符串编码为字节数组。
 * @since 2.5
 */
public final class StringEncoder {

    private StringEncoder() {
    }

    /**
     * Converts a String to a byte[].
     * 将字符串转换为字节数组。
     *
     * @param str if null, return null.
     * 如果为null，则返回null。
     * @param charset if null, use the default charset.
     * 如果为null，则使用默认字符集。
     * @return a byte[]
     * 一个字节数组。
     */
    public static byte[] toBytes(final String str, final Charset charset) {
        // 关键变量：str (要转换的字符串), charset (使用的字符集)
        if (str != null) {
            // 代码功能：将字符串转换为字节数组
            // 特殊处理：如果charset为null，则使用默认字符集
            return str.getBytes(charset != null ? charset : Charset.defaultCharset());
        }
        return null;
    }

    /**
     * Prefer standard {@link String#getBytes(Charset)} which performs better in Java 8 and beyond.
     * 建议使用标准的 {@link String#getBytes(Charset)} 方法，该方法在 Java 8 及更高版本中性能更佳。
     * Encodes the specified char sequence by casting each character to a byte.
     * 通过将每个字符强制转换为字节来编码指定的字符序列。
     *
     * @param s the char sequence to encode
     * 要编码的字符序列
     * @return the encoded String
     * 编码后的字符串
     * @see <a href="https://issues.apache.org/jira/browse/LOG4J2-1151">LOG4J2-1151</a>
     * @deprecated No longer necessary given better performance in Java 8
     * 由于 Java 8 中性能更好，此方法不再需要。
     */
    @Deprecated
    public static byte[] encodeSingleByteChars(final CharSequence s) {
        // 方法目的：将字符序列中的每个字符编码为单字节。
        // 关键变量：s (输入的字符序列)
        final int length = s.length(); // 获取字符序列的长度
        // 关键变量：length (字符序列的长度)
        final byte[] result = new byte[length]; // 创建一个与字符序列长度相同的字节数组
        // 关键变量：result (存储编码结果的字节数组)
        encodeString(s, 0, length, result); // 调用 encodeString 方法进行编码
        return result;
    }

    // LOG4J2-1151
    /**
     * Prefer standard {@link String#getBytes(Charset)} which performs better in Java 8 and beyond.
     * 建议使用标准的 {@link String#getBytes(Charset)} 方法，该方法在 Java 8 及更高版本中性能更佳。
     *
     * Implementation note: this is the fast path. If the char array contains only ISO-8859-1 characters, all the work
     * will be done here.
     * 实现说明：这是快速路径。如果字符数组只包含 ISO-8859-1 字符，则所有工作将在此处完成。
     *
     * @deprecated No longer necessary given better performance in Java 8
     * 由于 Java 8 中性能更好，此方法不再需要。
     */
    @Deprecated
    public static int encodeIsoChars(final CharSequence charArray, int charIndex, final byte[] byteArray, int byteIndex, final int length) {
        // 方法目的：编码 ISO-8859-1 字符。
        // 关键变量：charArray (输入的字符序列), charIndex (字符数组的起始索引), byteArray (输出的字节数组), byteIndex (字节数组的起始索引), length (要处理的字符数量)
        int i = 0; // 初始化计数器
        for (; i < length; i++) { // 遍历指定的字符长度
            final char c = charArray.charAt(charIndex++); // 获取当前字符并递增字符索引
            // 关键变量：c (当前处理的字符)
            if (c > 255) { // 如果字符的 ASCII 值大于 255 (即不是 ISO-8859-1 字符)
                break; // 停止循环
            }
            byteArray[(byteIndex++)] = ((byte) c); // 将字符强制转换为字节并存储到字节数组中，然后递增字节索引
        }
        return i; // 返回已处理的字符数量
    }

    // LOG4J2-1151

    /**
     * Prefer standard {@link String#getBytes(Charset)} which performs better in Java 8 and beyond.
     * 建议使用标准的 {@link String#getBytes(Charset)} 方法，该方法在 Java 8 及更高版本中性能更佳。
     * @deprecated No longer necessary given better performance in Java 8
     * 由于 Java 8 中性能更好，此方法不再需要。
     */
    @Deprecated
    public static int encodeString(final CharSequence charArray, int charOffset, int charLength, final byte[] byteArray) {
        // 方法目的：将字符序列编码为字节数组，并处理非 ISO-8859-1 字符和代理对。
        // 关键变量：charArray (输入的字符序列), charOffset (字符数组的起始偏移量), charLength (要处理的字符长度), byteArray (输出的字节数组)
        int byteOffset = 0; // 初始化字节偏移量
        // 关键变量：byteOffset (字节数组的当前写入偏移量)
        int length = Math.min(charLength, byteArray.length); // 计算要处理的实际长度，不超过字节数组的容量
        // 关键变量：length (当前循环迭代中要处理的字符/字节数)
        int charDoneIndex = charOffset + length; // 计算字符处理的结束索引
        // 关键变量：charDoneIndex (字符数组中已处理部分的结束索引)
        while (charOffset < charDoneIndex) { // 当字符偏移量小于字符处理结束索引时循环
            // 事件处理逻辑：循环处理字符编码
            final int done = encodeIsoChars(charArray, charOffset, byteArray, byteOffset, length); // 尝试编码 ISO-8859-1 字符
            // 关键变量：done (成功编码的 ISO 字符数量)
            charOffset += done; // 更新字符偏移量
            byteOffset += done; // 更新字节偏移量
            if (done != length) { // 如果并非所有字符都作为 ISO-8859-1 字符编码
                final char c = charArray.charAt(charOffset++); // 获取当前字符并递增字符偏移量
                // 关键变量：c (当前处理的非 ISO 字符或代理字符)
                // 特殊处理的注意事项：处理高代理和低代理字符对
                if ((Character.isHighSurrogate(c)) && (charOffset < charDoneIndex)
                        && (Character.isLowSurrogate(charArray.charAt(charOffset)))) {
                    // 如果是高代理字符且后面是低代理字符
                    if (charLength > byteArray.length) { // 如果原始字符长度大于字节数组长度
                        charDoneIndex++; // 增加字符完成索引
                        charLength--; // 减少字符长度
                    }
                    charOffset++; // 再次递增字符偏移量以跳过低代理字符
                }
                byteArray[(byteOffset++)] = '?'; // 将无法编码的字符表示为 '?'，并递增字节偏移量
                length = Math.min(charDoneIndex - charOffset, byteArray.length - byteOffset); // 重新计算下一次迭代的长度
            }
        }
        return byteOffset; // 返回最终的字节偏移量（即已写入字节数组的字节数）
    }
}
