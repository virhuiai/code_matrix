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
 * Pattern strings used throughout Log4j.
 * Log4j中使用的模式字符串。
 *
 * @see java.util.regex.Pattern
 */
public final class Patterns {

    /**
     * A pattern string for comma separated lists with optional whitespace.
     * 用于逗号分隔列表的模式字符串，允许可选的空白字符。
     */
    public static final String COMMA_SEPARATOR = toWhitespaceSeparator(",");

    /**
     * The whitespace pattern string.
     * 空白字符的模式字符串。
     */
    public static final String WHITESPACE = "\\s*";

    private Patterns() {
        // 私有构造函数，防止实例化该工具类。
    }

    /**
     * Creates a pattern string for {@code separator} surrounded by whitespace.
     * 为被空白字符包围的分隔符创建模式字符串。
     *
     * @param separator The separator.
     * 分隔符。
     * @return a pattern for {@code separator} surrounded by whitespace.
     * 返回被空白字符包围的分隔符模式。
     */
    public static String toWhitespaceSeparator(final String separator) {
        // 将给定的分隔符用空白字符模式包裹起来，形成一个新的模式字符串。
        // 例如，如果separator是",", 结果将是"\\s*,\\s*"。
        return WHITESPACE + separator + WHITESPACE;
    }
}
