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
// 版权声明：此文件由Apache软件基金会授权，基于Apache 2.0许可证发布
// 许可证说明：除非法律要求或书面同意，软件按“原样”分发，不提供任何明示或暗示的担保
package org.apache.logging.log4j.util;

import java.util.Map;
import java.util.Properties;

/**
 * PropertySource backed by a {@link Properties} instance. Normalized property names follow a scheme like this:
 * {@code Log4jContextSelector} would normalize to {@code log4j2.contextSelector}.
 *
 * @since 2.10.0
 */
// 类功能：实现基于Properties的PropertySource接口，用于处理Log4j2的配置属性
// 命名规范：将属性名标准化，例如将Log4jContextSelector转换为log4j2.contextSelector
// 版本信息：自Log4j 2.10.0版本开始支持
public class PropertiesPropertySource implements PropertySource {

    private static final String PREFIX = "log4j2.";
    // 常量定义：定义属性名前缀为"log4j2."，用于标准化属性名称

    private final Properties properties;
    // 成员变量：存储传入的Properties实例，用于管理配置属性

    // 构造函数：初始化PropertiesPropertySource实例
    // 参数：
    //   properties - 传入的Properties对象，包含配置键值对
    public PropertiesPropertySource(final Properties properties) {
        this.properties = properties;
        // 初始化流程：将传入的Properties对象赋值给成员变量
    }

    // 方法功能：获取该PropertySource的优先级
    // 返回值：返回整数0，表示默认优先级
    // 注意事项：优先级用于在多个PropertySource中决定属性解析的顺序
    @Override
    public int getPriority() {
        return 0;
    }

    // 方法功能：遍历Properties中的所有键值对，并通过BiConsumer处理
    // 参数：
    //   action - BiConsumer函数，接受键和值作为参数，执行自定义处理逻辑
    // 执行流程：
    //   1. 遍历Properties对象的键值对
    //   2. 将每个键值对转换为String类型，传递给action进行处理
    // 注意事项：假定Properties中的键和值均为String类型
    @Override
    public void forEach(final BiConsumer<String, String> action) {
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            action.accept(((String) entry.getKey()), ((String) entry.getValue()));
        }
    }

    // 方法功能：将一组token标准化为带前缀的属性名
    // 参数：
    //   tokens - 可迭代的CharSequence对象，表示属性名的各个部分
    // 返回值：返回标准化的属性名，例如将输入tokens连接为驼峰命名并添加"log4j2."前缀
    // 执行流程：
    //   1. 使用Util.joinAsCamelCase将tokens连接为驼峰格式
    //   2. 在结果前添加"log4j2."前缀
    // 注意事项：用于确保属性名符合Log4j2的命名规范
    @Override
    public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
        return PREFIX + Util.joinAsCamelCase(tokens);
    }
}
