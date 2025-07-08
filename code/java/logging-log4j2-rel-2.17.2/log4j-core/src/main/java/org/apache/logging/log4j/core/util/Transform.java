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


/**
 * Utility class for transforming strings.
 * 字符串转换的工具类。
 */
public final class Transform {

    private static final String CDATA_START = "<![CDATA[";
    // 定义CDATA部分的开始标记。
    private static final String CDATA_END = "]]>";
    // 定义CDATA部分的结束标记。
    private static final String CDATA_PSEUDO_END = "]]&gt;";
    // 定义CDATA伪结束标记，用于处理CDATA内部的结束标记。
    private static final String CDATA_EMBEDED_END = CDATA_END + CDATA_PSEUDO_END + CDATA_START;
    // 定义嵌入式CDATA结束标记，用于在CDATA内部遇到"&#93;&#93;>"时进行转义。
    private static final int CDATA_END_LEN = CDATA_END.length();
    // CDATA结束标记的长度。

    private Transform() {
    }

    /**
     * This method takes a string which may contain HTML tags (ie,
     * &lt;b&gt;, &lt;table&gt;, etc) and replaces any
     * '&lt;',  '&gt;' , '&amp;' or '&quot;'
     * characters with respective predefined entity references.
     * 此方法接收一个可能包含HTML标签（如`<b>`, `<table>`等）的字符串，
     * 并将其中的`&lt;`（<）、`&gt;`（>）、`&amp;`（&）或`&quot;`（"）字符替换为相应的预定义实体引用。
     *
     * @param input The text to be converted.
     * 待转换的文本。
     * @return The input string with the special characters replaced.
     * 特殊字符被替换后的输入字符串。
     */
    public static String escapeHtmlTags(final String input) {
        // Check if the string is null, zero length or devoid of special characters
        // if so, return what was sent in.
        // 检查字符串是否为空、长度为零或不包含特殊字符。
        // 如果是，则直接返回原始输入字符串。

        if (Strings.isEmpty(input)
            || (input.indexOf('"') == -1 &&
            input.indexOf('&') == -1 &&
            input.indexOf('<') == -1 &&
            input.indexOf('>') == -1)) {
            return input;
        }

        //Use a StringBuilder in lieu of String concatenation -- it is
        //much more efficient this way.
        // 使用StringBuilder代替字符串拼接，因为这种方式效率更高。

        final StringBuilder buf = new StringBuilder(input.length() + 6);
        // 初始化一个StringBuilder，其初始容量设置为输入字符串长度加6，以预留转义字符的空间。

        final int len = input.length();
        // 获取输入字符串的长度。
        for (int i = 0; i < len; i++) {
            // 遍历输入字符串的每一个字符。
            final char ch = input.charAt(i);
            // 获取当前字符。
            if (ch > '>') {
                // 如果当前字符的ASCII值大于'>'（即不可能是'<', '>', '&', '"'这些需要转义的字符），
                // 则直接追加到缓冲区。
                buf.append(ch);
            } else
                // 否则，根据字符进行判断和转义。
                switch (ch) {
                case '<':
                    // 如果是'<'，替换为"&lt;"。
                    buf.append("&lt;");
                    break;
                case '>':
                    // 如果是'>'，替换为"&gt;"。
                    buf.append("&gt;");
                    break;
                case '&':
                    // 如果是'&'，替换为"&amp;"。
                    buf.append("&amp;");
                    break;
                case '"':
                    // 如果是'"'，替换为"&quot;"。
                    buf.append("&quot;");
                    break;
                default:
                    // 对于其他字符，直接追加到缓冲区。
                    buf.append(ch);
                    break;
                }
        }
        return buf.toString();
        // 返回StringBuilder中构建的字符串。
    }

    /**
     * Ensures that embedded CDEnd strings (]]&gt;) are handled properly
     * within message, NDC and throwable tag text.
     * 确保在消息、NDC和可抛出标签文本中正确处理嵌入的CDATA结束字符串（`]]&gt;`）。
     *
     * @param buf StringBuilder holding the XML data to this point.  The
     *            initial CDStart (&lt;![CDATA[) and final CDEnd (]]&gt;) of the CDATA
     *            section are the responsibility of the calling method.
     * 保存当前XML数据的StringBuilder。CDATA部分的初始CDStart（`<![CDATA[`）
     * 和最终CDEnd（`]]>`）是调用方法的责任。
     * @param str The String that is inserted into an existing CDATA Section within buf.
     * 要插入到buf中现有CDATA部分的字符串。
     */
    public static void appendEscapingCData(final StringBuilder buf, final String str) {
        if (str != null) {
            // 只有当输入字符串不为null时才进行处理。
            int end = str.indexOf(CDATA_END);
            // 查找字符串中第一个CDATA结束标记"&#93;&#93;>"的位置。
            if (end < 0) {
                // 如果没有找到CDATA结束标记，则直接将整个字符串追加到缓冲区。
                buf.append(str);
            } else {
                // 如果找到了CDATA结束标记。
                int start = 0;
                // 定义当前处理段的起始位置。
                while (end > -1) {
                    // 循环直到字符串中不再有CDATA结束标记。
                    buf.append(str.substring(start, end));
                    // 将从起始位置到CDATA结束标记之前的部分追加到缓冲区。
                    buf.append(CDATA_EMBEDED_END);
                    // 追加嵌入式CDATA结束标记，进行转义。
                    start = end + CDATA_END_LEN;
                    // 更新起始位置，跳过已处理的CDATA结束标记。
                    if (start < str.length()) {
                        // 如果起始位置仍在字符串范围内，则继续查找下一个CDATA结束标记。
                        end = str.indexOf(CDATA_END, start);
                    } else {
                        // 如果起始位置超出字符串长度，表示所有内容已处理完毕，直接返回。
                        return;
                    }
                }
                buf.append(str.substring(start));
                // 将最后一个CDATA结束标记之后的部分追加到缓冲区。
            }
        }
    }

    /**
     * This method takes a string which may contain JSON reserved chars and
     * escapes them.
     * 此方法接收一个可能包含JSON保留字符的字符串，并对其进行转义。
     *
     * @param input The text to be converted.
     * 待转换的文本。
     * @return The input string with the special characters replaced.
     * 特殊字符被替换后的输入字符串。
     */
    public static String escapeJsonControlCharacters(final String input) {
        // Check if the string is null, zero length or devoid of special characters
        // if so, return what was sent in.
        // 检查字符串是否为空、长度为零或不包含特殊字符。
        // 如果是，则直接返回原始输入字符串。

        // TODO: escaped Unicode chars.
        // TODO: 待处理转义的Unicode字符。

        if (Strings.isEmpty(input)
            || (input.indexOf('"') == -1 &&
            input.indexOf('\\') == -1 &&
            input.indexOf('/') == -1 &&
            input.indexOf('\b') == -1 &&
            input.indexOf('\f') == -1 &&
            input.indexOf('\n') == -1 &&
            input.indexOf('\r') == -1 &&
            input.indexOf('\t') == -1)) {
            return input;
        }

        final StringBuilder buf = new StringBuilder(input.length() + 6);
        // 初始化一个StringBuilder，其初始容量设置为输入字符串长度加6，以预留转义字符的空间。

        final int len = input.length();
        // 获取输入字符串的长度。
        for (int i = 0; i < len; i++) {
            // 遍历输入字符串的每一个字符。
            final char ch = input.charAt(i);
            // 获取当前字符。
            final String escBs = "\\";
            // 定义反斜杠转义字符。
            switch (ch) {
            case '"':
                // 如果是双引号，转义为\"。
                buf.append(escBs);
                buf.append(ch);
                break;
            case '\\':
                // 如果是反斜杠，转义为\\。
                buf.append(escBs);
                buf.append(ch);
                break;
            case '/':
                // 如果是正斜杠，转义为\/。
                buf.append(escBs);
                buf.append(ch);
                break;
            case '\b':
                // 如果是退格符，转义为\b。
                buf.append(escBs);
                buf.append('b');
                break;
            case '\f':
                // 如果是换页符，转义为\f。
                buf.append(escBs);
                buf.append('f');
                break;
            case '\n':
                // 如果是换行符，转义为\n。
                buf.append(escBs);
                buf.append('n');
                break;
            case '\r':
                // 如果是回车符，转义为\r。
                buf.append(escBs);
                buf.append('r');
                break;
            case '\t':
                // 如果是制表符，转义为\t。
                buf.append(escBs);
                buf.append('t');
                break;
            default:
                // 对于其他字符，直接追加到缓冲区。
                buf.append(ch);
            }
        }
        return buf.toString();
        // 返回StringBuilder中构建的字符串。
    }
}
