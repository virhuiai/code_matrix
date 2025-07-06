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

import java.util.Objects;
import java.util.Properties;

/**
 * PropertySource backed by the current system properties. Other than having a
 * higher priority over normal properties, this follows the same rules as
 * {@link PropertiesPropertySource}.
 *
 * @since 2.10.0
 */
// 中文注释：
// 该类实现 PropertySource 接口，用于从系统属性（System Properties）中获取配置。
// 与普通属性相比，具有更高的优先级，但遵循与 PropertiesPropertySource 相同的规则。
// 主要功能：提供对系统属性的访问，允许遍历属性并按照特定格式规范化属性名称。
// 自 2.10.0 版本引入。
public class SystemPropertiesPropertySource implements PropertySource {
//	类名 SystemPropertiesPropertySource 可以拆分为三个主要部分：SystemProperties：指明数据来源，即 Java 的系统属性（System.getProperties()）。
//	PropertySource：表明该类是一个属性源（Property Source），符合 Log4j 框架中用于提供配置属性的接口 PropertySource 的实现。
//	组合：整体名称清晰地描述了类的核心功能——从系统属性中获取配置的属性源。
	private static final int DEFAULT_PRIORITY = 100;
    // 中文注释：
    // 定义默认优先级为 100，用于确定该属性源在属性解析中的优先级。
    // 重要配置参数：优先级越高，属性源越优先被使用。

	private static final String PREFIX = "log4j2.";
    // 中文注释：
    // 定义属性名称的前缀 "log4j2."，用于规范化属性名称。
    // 重要配置参数：所有属性名称将以此作为前缀，构成特定格式。

	@Override
	public int getPriority() {
		return DEFAULT_PRIORITY;
	}
    // 中文注释：
    // 方法目的：返回该属性源的优先级。
    // 返回值：固定返回 DEFAULT_PRIORITY（100）。
    // 用途：用于属性源优先级排序，高优先级属性源优先于低优先级。

	@Override
	public void forEach(final BiConsumer<String, String> action) {
        // 中文注释：
        // 方法目的：遍历系统属性，并对每个属性键值对执行指定的操作。
        // 参数说明：
        // - action: BiConsumer<String, String> 类型，定义对每个键值对的处理逻辑。
        // 交互逻辑：通过 action 参数，调用者可以自定义如何处理每个键值对。
		Properties properties;
		try {
			properties = System.getProperties();
            // 中文注释：
            // 关键步骤：获取当前 JVM 的系统属性。
            // 特殊处理：可能抛出 SecurityException，需捕获以确保程序稳定性。
		} catch (final SecurityException e) {
			// (1) There is no status logger.
			// (2) LowLevelLogUtil also consults system properties ("line.separator") to
			// open a BufferedWriter, so this may fail as well. Just having a hard reference
			// in this code to LowLevelLogUtil would cause a problem.
			// (3) We could log to System.err (nah) or just be quiet as we do now.
            // 中文注释：
            // 事件处理逻辑：捕获 SecurityException，表示无法访问系统属性。
            // 特殊处理注意事项：
            // 1. 没有状态日志器，无法记录错误。
            // 2. 避免直接引用 LowLevelLogUtil，因为其可能导致类似的系统属性访问问题。
            // 3. 选择静默处理（不输出到 System.err），以避免进一步问题。
			return;
		}
		// Lock properties only long enough to get a thread-safe SAFE snapshot of its
		// current keys, an array.
        // 中文注释：
        // 关键步骤：对系统属性加锁，仅获取线程安全的键集合快照。
        // 特殊处理：通过同步块确保线程安全，避免并发修改问题。
		final Object[] keySet;
		synchronized (properties) {
			keySet = properties.keySet().toArray();
            // 中文注释：
            // 将系统属性的键集合转换为数组，存储在 keySet 中。
            // 关键变量用途：keySet 保存所有属性键，用于后续遍历。
		}
		// Then traverse for an unknown amount of time.
		// Some keys may now be absent, in which case, the value is null.
        // 中文注释：
        // 关键步骤：遍历键集合，获取对应的属性值。
        // 特殊处理注意事项：由于属性可能被并发修改，某些键可能已不存在，此时值为 null。
		for (final Object key : keySet) {
			final String keyStr = Objects.toString(key, null);
            // 中文注释：
            // 将键转换为字符串，确保键不为 null 时正确处理。
            // 关键变量用途：keyStr 是当前处理的属性键的字符串形式。
			action.accept(keyStr, properties.getProperty(keyStr));
            // 中文注释：
            // 交互逻辑：对每个键值对调用 action.accept，执行用户定义的处理逻辑。
            // 参数说明：
            // - keyStr: 属性键。
            // - properties.getProperty(keyStr): 对应键的属性值，可能为 null。
		}
	}

	@Override
	public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
		return PREFIX + Util.joinAsCamelCase(tokens);
	}
    // 中文注释：
    // 方法目的：将一组标记（tokens）规范化，生成符合 log4j2 格式的属性名称。
    // 参数说明：
    // - tokens: 可迭代的 CharSequence 对象，表示属性名称的各部分。
    // 返回值：以 PREFIX ("log4j2.") 开头，连接 tokens 形成驼峰式字符串。
    // 关键步骤：使用 Util.joinAsCamelCase 将 tokens 拼接为驼峰格式。
    // 用途：规范化属性名称，确保与 log4j2 的命名规则一致。
    // 特殊处理：确保输出的属性名称符合预期格式，便于系统属性解析。
}
