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

/**
 * This class is borrowed from <a href="https://github.com/FasterXML/jackson-core">Jackson</a>.
 * 这个类借鉴自 <a href="https://github.com/FasterXML/jackson-core">Jackson</a> 项目。
 */
public final class JsonUtils {

    private static final char[] HC = "0123456789ABCDEF".toCharArray();
    // HC 是一个包含十六进制字符的数组，用于将数字转换为十六进制表示。

    /**
     * Read-only encoding table for first 128 Unicode code points (single-byte UTF-8 characters).
     * Value of 0 means "no escaping"; other positive values that value is character
     * to use after backslash; and negative values that generic (backslash - u)
     * escaping is to be used.
     */
    // 这是一个只读的编码表，用于处理前 128 个 Unicode 码点（单字节 UTF-8 字符）。
    // 值为 0 表示“无需转义”；其他正值表示在反斜杠后使用的转义字符；
    // 负值表示应使用通用（反斜杠 - u）转义。
    private static final int[] ESC_CODES;
    static {
        final int[] table = new int[128];
        // Control chars need generic escape sequence
        // 控制字符需要通用的转义序列
        for (int i = 0; i < 32; ++i) {
            // 04-Mar-2011, tatu: Used to use "-(i + 1)", replaced with constant
            // 2011年3月4日，tatu：以前使用“-(i + 1)”，现已替换为常量。
            table[i] = -1;
        }
        /* Others (and some within that range too) have explicit shorter
         * sequences
         */
        // 其他字符（以及该范围内的某些字符）具有明确的短序列。
        table['"'] = '"'; // 双引号转义为双引号
        table['\\'] = '\\'; // 反斜杠转义为反斜杠
        // Escaping of slash is optional, so let's not add it
        // 斜杠的转义是可选的，因此我们不添加它。
        table[0x08] = 'b';// 退格符转义为 'b'
        table[0x09] = 't';// 制表符转义为 't'
        table[0x0C] = 'f';// 换页符转义为 'f'
        table[0x0A] = 'n';// 换行符转义为 'n'
        table[0x0D] = 'r';// 回车符转义为 'r'
        ESC_CODES = table; // 初始化转义码表
    }

    /**
     * Temporary buffer used for composing quote/escape sequences
     */
    // 用于组合引用/转义序列的临时缓冲区。
    private final static ThreadLocal<char[]> _qbufLocal = new ThreadLocal<>();

    // 获取线程本地的字符缓冲区。
    private static char[] getQBuf() {
        char[] _qbuf = _qbufLocal.get(); // 从线程本地存储中获取缓冲区
        if (_qbuf == null) { // 如果缓冲区不存在
            _qbuf = new char[6]; // 创建一个大小为 6 的新字符数组
            _qbuf[0] = '\\'; // 设置第一个字符为反斜杠
            _qbuf[2] = '0'; // 设置第三个字符为 '0'
            _qbuf[3] = '0'; // 设置第四个字符为 '0'

            _qbufLocal.set(_qbuf); // 将新创建的缓冲区设置到线程本地存储中
        }
        return _qbuf; // 返回缓冲区
    }

    /**
     * Quote text contents using JSON standard quoting, and append results to a supplied {@link StringBuilder}.
     */
    // 使用 JSON 标准引用规则对文本内容进行转义，并将结果附加到提供的 {@link StringBuilder} 中。
    public static void quoteAsString(final CharSequence input, final StringBuilder output) {
        final char[] qbuf = getQBuf(); // 获取字符缓冲区
        final int escCodeCount = ESC_CODES.length; // 获取转义码表的长度
        int inPtr = 0; // 输入字符串的当前指针
        final int inputLen = input.length(); // 输入字符串的长度

        outer: // 外部循环标签
        while (inPtr < inputLen) { // 当指针未到达输入字符串末尾时循环
            tight_loop: // 内部紧凑循环标签
            while (true) {
                final char c = input.charAt(inPtr); // 获取当前字符
                // 检查字符是否需要转义
                if (c < escCodeCount && ESC_CODES[c] != 0) {
                    break tight_loop; // 如果需要转义，则跳出内部循环
                }
                output.append(c); // 将当前字符附加到输出
                if (++inPtr >= inputLen) { // 指针后移，如果到达末尾则跳出外部循环
                    break outer;
                }
            }
            // something to escape; 2 or 6-char variant?
            // 需要转义的字符；是 2 字符还是 6 字符变体？
            final char d = input.charAt(inPtr++); // 获取需要转义的字符，并移动指针
            final int escCode = ESC_CODES[d]; // 获取转义码
            final int length = (escCode < 0) // 根据转义码判断使用哪种转义方式
                    ? _appendNumeric(d, qbuf) // 如果小于 0，使用数字转义（6 字符）
                    : _appendNamed(escCode, qbuf); // 否则使用命名转义（2 字符）

            output.append(qbuf, 0, length); // 将转义后的字符序列附加到输出
        }
    }

    // 附加数字转义序列（例如 \\u00XX）。
    private static int _appendNumeric(final int value, final char[] qbuf) {
        qbuf[1] = 'u'; // 设置转义类型为 'u'
        // We know it's a control char, so only the last 2 chars are non-0
        // 我们知道它是一个控制字符，因此只有最后两个字符是非 '0' 的。
        qbuf[4] = HC[value >> 4]; // 将值的高 4 位转换为十六进制字符
        qbuf[5] = HC[value & 0xF]; // 将值的低 4 位转换为十六进制字符
        return 6; // 返回转义序列的长度（6个字符）
    }

    // 附加命名转义序列（例如 \" 或 \\）。
    private static int _appendNamed(final int esc, final char[] qbuf) {
        qbuf[1] = (char) esc; // 设置转义字符
        return 2; // 返回转义序列的长度（2个字符）
    }

}
