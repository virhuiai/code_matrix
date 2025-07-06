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
// 版权声明：此文件由Apache软件基金会授权，遵循Apache 2.0许可证发布。
// 许可证限制：除非法律要求或书面同意，软件按“现状”分发，不提供任何明示或暗示的保证。
// 许可证详情请参见 http://www.apache.org/licenses/LICENSE-2.0。

package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A source for global configuration properties.
 *
 * @since 2.10.0
 */
// 接口定义：PropertySource 用于提供全局配置属性的来源。
// 主要功能：定义了获取配置属性、优先级以及属性名称规范化等行为的接口。
// 自 2.10.0 版本引入。
public interface PropertySource {

    /**
     * Returns the order in which this PropertySource has priority. A higher value means that the source will be
     * applied later so as to take precedence over other property sources.
     *
     * @return priority value
     */
    // 方法功能：获取当前 PropertySource 的优先级。
    // 返回值：返回一个整数，表示优先级，值越大优先级越高，后续应用会覆盖其他较低优先级的属性来源。
    // 使用场景：用于决定多个 PropertySource 之间的应用顺序。
    int getPriority();

    /**
     * Iterates over all properties and performs an action for each key/value pair.
     *
     * @param action action to perform on each key/value pair
     */
    // 方法功能：遍历所有属性，并对每个键值对执行指定的操作。
    // 参数说明：action 是一个 BiConsumer，接受键（String）和值（String）并执行相应逻辑。
    // 执行流程：默认实现为空，子类可重写以实现具体的遍历逻辑。
    // 注意事项：此方法为默认方法，子类可选择不实现。
    default void forEach(BiConsumer<String, String> action) {
    }

    /**
     * Converts a list of property name tokens into a normal form. For example, a list of tokens such as
     * "foo", "bar", "baz", might be normalized into the property name "log4j2.fooBarBaz".
     *
     * @param tokens list of property name tokens
     * @return a normalized property name using the given tokens
     */
    // 方法功能：将属性名称的令牌列表转换为规范化形式。
    // 参数说明：tokens 是一个包含多个 CharSequence 的可迭代对象，表示属性名称的各个部分。
    // 返回值：返回规范化后的属性名称（如 log4j2.fooBarBaz），默认实现返回 null。
    // 使用场景：用于将分散的属性名称片段组合为标准的属性名称格式。
    // 注意事项：默认实现返回 null，子类需重写以提供具体实现。
    default CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
        return null;
    }

    /**
     * For PropertySources that cannot iterate over all the potential properties this provides a direct lookup.
     * @param key The key to search for.
     * @return The value or null;
     * @since 2.13.0
     */
    // 方法功能：直接查询指定键的属性值，适用于无法遍历所有属性的 PropertySource。
    // 参数说明：key 为要查找的属性键（String 类型）。
    // 返回值：返回对应的属性值（String 类型），若不存在则返回 null。
    // 自 2.13.0 版本引入。
    // 注意事项：默认实现返回 null，子类需重写以提供具体实现。
    default String getProperty(String key) {
        return null;
    }


    /**
     * For PropertySources that cannot iterate over all the potential properties this provides a direct lookup.
     * @param key The key to search for.
     * @return The value or null;
     * @since 2.13.0
     */
    // 方法功能：检查指定键的属性是否存在，适用于无法遍历所有属性的 PropertySource。
    // 参数说明：key 为要检查的属性键（String 类型）。
    // 返回值：返回布尔值，true 表示属性存在，false 表示不存在。
    // 自 2.13.0 版本引入。
    // 注意事项：默认实现返回 false，子类需重写以提供具体实现。
    default boolean containsProperty(String key) {
        return false;
    }

    /**
     * Comparator for ordering PropertySource instances by priority.
     *
     * @since 2.10.0
     */
    // 类功能：定义一个比较器，用于根据优先级对 PropertySource 实例进行排序。
    // 自 2.10.0 版本引入。
    class Comparator implements java.util.Comparator<PropertySource>, Serializable {
        private static final long serialVersionUID = 1L;
        // 变量说明：serialVersionUID 用于序列化，保持版本兼容性。

        @Override
        public int compare(final PropertySource o1, final PropertySource o2) {
            return Integer.compare(Objects.requireNonNull(o1).getPriority(), Objects.requireNonNull(o2).getPriority());
        }
        // 方法功能：比较两个 PropertySource 实例的优先级。
        // 参数说明：o1 和 o2 为要比较的两个 PropertySource 实例。
        // 返回值：返回比较结果，基于优先级的高低排序。
        // 执行流程：调用 getPriority() 获取优先级值，使用 Integer.compare 进行比较。
        // 注意事项：使用 Objects.requireNonNull 确保传入对象不为空，防止空指针异常。
    }

    /**
     * Utility methods useful for PropertySource implementations.
     *
     * @since 2.10.0
     */
    // 类功能：提供 PropertySource 实现所需的实用工具方法。
    // 自 2.10.0 版本引入。
    final class Util {
        private static final String PREFIXES = "(?i:^log4j2?[-._/]?|^org\\.apache\\.logging\\.log4j\\.)?";
        // 变量说明：PREFIXES 定义正则表达式前缀，用于匹配 log4j、log4j2 或 org.apache.logging.log4j 等前缀。
        // 用途：用于属性名称的分词处理，忽略大小写并支持多种分隔符。

        private static final Pattern PROPERTY_TOKENIZER = Pattern.compile(PREFIXES + "([A-Z]*[a-z0-9]+|[A-Z0-9]+)[-._/]?");
        // 变量说明：PROPERTY_TOKENIZER 是一个正则表达式模式，用于将属性名称分割为令牌。
        // 用途：支持驼峰命名、连字符、下划线、斜杠等分隔符的属性名称分词。

        private static final Map<CharSequence, List<CharSequence>> CACHE = new ConcurrentHashMap<>();
        // 变量说明：CACHE 是一个线程安全的 ConcurrentHashMap，用于缓存属性名称的分词结果。
        // 用途：提高分词性能，避免重复解析相同的属性名称。

        /**
         * Converts a property name string into a list of tokens. This will strip a prefix of {@code log4j},
         * {@code log4j2}, {@code Log4j}, or {@code org.apache.logging.log4j}, along with separators of
         * dash {@code -}, dot {@code .}, underscore {@code _}, and slash {@code /}. Tokens can also be separated
         * by camel case conventions without needing a separator character in between.
         *
         * @param value property name
         * @return the property broken into lower case tokens
         */
        // 方法功能：将属性名称字符串转换为小写令牌列表。
        // 参数说明：value 为输入的属性名称（CharSequence 类型）。
        // 返回值：返回小写的令牌列表（List<CharSequence>）。
        // 执行流程：
        // 1. 检查缓存中是否已有该属性名称的分词结果，若有则直接返回。
        // 2. 使用 PROPERTY_TOKENIZER 正则表达式匹配属性名称，提取令牌。
        // 3. 将每个匹配的令牌转换为小写并添加到列表。
        // 4. 将分词结果存入缓存并返回。
        // 注意事项：支持多种前缀和分隔符，忽略大小写，支持驼峰命名。
        public static List<CharSequence> tokenize(final CharSequence value) {
            if (CACHE.containsKey(value)) {
                return CACHE.get(value);
            }
            final List<CharSequence> tokens = new ArrayList<>();
            final Matcher matcher = PROPERTY_TOKENIZER.matcher(value);
            while (matcher.find()) {
                tokens.add(matcher.group(1).toLowerCase());
            }
            CACHE.put(value, tokens);
            return tokens;
        }

        /**
         * Joins a list of strings using camelCaseConventions.
         *
         * @param tokens tokens to convert
         * @return tokensAsCamelCase
         */
        // 方法功能：将令牌列表按驼峰命名规则连接为字符串。
        // 参数说明：tokens 为要连接的令牌列表（Iterable<? extends CharSequence>）。
        // 返回值：返回驼峰命名格式的字符串（CharSequence）。
        // 执行流程：
        // 1. 创建 StringBuilder 用于构建结果字符串。
        // 2. 遍历令牌列表，首令牌直接追加，其余令牌首字母大写后追加。
        // 3. 返回最终的驼峰命名字符串。
        // 注意事项：首个令牌保持原样，其余令牌首字母大写以符合驼峰命名规则。
        public static CharSequence joinAsCamelCase(final Iterable<? extends CharSequence> tokens) {
            final StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (final CharSequence token : tokens) {
                if (first) {
                    sb.append(token);
                } else {
                    sb.append(Character.toUpperCase(token.charAt(0)));
                    if (token.length() > 1) {
                        sb.append(token.subSequence(1, token.length()));
                    }
                }
                first = false;
            }
            return sb.toString();
        }

        private Util() {
        }
        // 构造方法：私有化构造方法，防止实例化。
        // 用途：Util 类为静态工具类，仅提供静态方法，不允许创建实例。
    }
}
