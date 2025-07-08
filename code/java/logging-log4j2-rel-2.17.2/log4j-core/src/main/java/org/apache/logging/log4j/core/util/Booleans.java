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
 * Boolean helpers.
 * 布尔类型辅助类。
 */
public final class Booleans {

    private Booleans() {
    }

    /**
     * Returns {@code true} if {@code s} is {@code "true"} (case-insensitive), {@code false} if {@code s} is
     * {@code "false"} (case-insensitive), and {@code defaultValue} if {@code s} is anything else (including null or
     * empty).
     * 如果字符串 `s` 是 "true"（不区分大小写），则返回 `true`；如果 `s` 是 "false"（不区分大小写），则返回 `false`；
     * 如果 `s` 是其他任何值（包括 null 或空字符串），则返回 `defaultValue`。
     *
     * @param s The {@code String} to parse into a {@code boolean}
     * 要解析为布尔值的字符串。
     * @param defaultValue The default value to use if {@code s} is neither {@code "true"} nor {@code "false"}
     * 如果 `s` 既不是 "true" 也不是 "false" 时使用的默认值。
     * @return the {@code boolean} value represented by the argument, or {@code defaultValue}.
     * 参数表示的布尔值，或者 `defaultValue`。
     */
    public static boolean parseBoolean(final String s, final boolean defaultValue) {
        // 关键逻辑说明：
        // 1. "true".equalsIgnoreCase(s)：检查字符串 s 是否等于 "true"（不区分大小写）。
        // 2. defaultValue && !"false".equalsIgnoreCase(s)：如果 defaultValue 为 true 并且字符串 s 不等于 "false"（不区分大小写），则返回 true。
        // 这个逻辑确保了：
        // - 如果 s 是 "true"，则返回 true。
        // - 如果 s 是 "false"，则返回 false。
        // - 如果 s 是其他值，则返回 defaultValue。
        return "true".equalsIgnoreCase(s) || (defaultValue && !"false".equalsIgnoreCase(s));
    }

}
