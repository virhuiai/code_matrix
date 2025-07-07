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

import java.util.Map;

/**
 * PropertySource implementation that uses environment variables as a source.
 * All environment variables must begin with {@code LOG4J_} so as not to
 * conflict with other variables. Normalized environment variables follow a
 * scheme like this: {@code log4j2.fooBarProperty} would normalize to
 * {@code LOG4J_FOO_BAR_PROPERTY}.
 *
 * @since 2.10.0
 */
/**
 * 类功能说明：
 * EnvironmentPropertySource 是 PropertySource 接口的一个实现类，用于从系统环境变量中获取 Log4j 的配置属性。
 * 所有环境变量必须以 LOG4J_ 开头，以避免与其他环境变量冲突。属性名称会按照规范化规则转换为大写并以下划线分隔，
 * 例如 log4j2.fooBarProperty 会被转换为 LOG4J_FOO_BAR_PROPERTY。
 *
 * 使用场景：
 * 该类主要用于 Log4j 日志框架的配置加载，允许用户通过环境变量动态设置日志配置，而无需修改配置文件。
 *
 * 注意事项：
 * - 环境变量的访问可能受到安全管理器的限制，可能导致 SecurityException。
 * - 该类的优先级较低（DEFAULT_PRIORITY = -100），在属性源中优先级较低。
 *
 * @since 2.10.0
 */
public class EnvironmentPropertySource implements PropertySource {

	private static final String PREFIX = "LOG4J_";
    /**
     * 常量说明：
     * PREFIX 定义了环境变量的前缀，必须为 "LOG4J_"，用于标识 Log4j 相关的环境变量。
     * 作用：确保只处理以该前缀开头的环境变量，避免与其他系统环境变量冲突。
     */

	private static final int DEFAULT_PRIORITY = -100;
    /**
     * 常量说明：
     * DEFAULT_PRIORITY 定义了该属性源的优先级，值为 -100。
     * 作用：优先级较低，表示当存在多个属性源时，环境变量的配置优先级低于其他来源（如配置文件）。
     */

	@Override
	public int getPriority() {
		return DEFAULT_PRIORITY;
	}
    /**
     * 方法功能说明：
     * getPriority 方法返回该属性源的优先级，用于在多个属性源中确定加载顺序。
     *
     * 返回值：
     * - int: 返回 DEFAULT_PRIORITY（-100），表示较低的优先级。
     *
     * 执行流程：
     * 1. 直接返回预定义的 DEFAULT_PRIORITY 常量值。
     *
     * 注意事项：
     * - 优先级值越小，属性源的优先级越低，意味着其他属性源（如配置文件）可能覆盖环境变量的配置。
     */

	@Override
	public void forEach(final BiConsumer<String, String> action) {
        /**
         * 方法功能说明：
         * forEach 方法遍历系统环境变量，提取以 LOG4J_ 开头的键值对，并通过提供的 BiConsumer 回调处理这些键值对。
         * 主要用于将环境变量的配置传递给 Log4j 的配置系统。
         *
         * 参数说明：
         * - action: BiConsumer<String, String> 类型，接受键和值作为参数的回调函数，用于处理每个符合条件的环境变量。
         *
         * 执行流程：
         * 1. 尝试获取系统环境变量（System.getenv()）。
         * 2. 如果因安全限制无法访问环境变量，捕获 SecurityException 并记录异常，然后返回。
         * 3. 遍历环境变量的键值对，筛选出以 LOG4J_ 开头的键。
         * 4. 对符合条件的键，移除前缀后连同值一起传递给 action 回调进行处理。
         *
         * 关键变量说明：
         * - getenv: 存储系统环境变量的 Map，键为环境变量名称，值为环境变量值。
         * - key: 当前遍历的环境变量名称。
         * - entry: 环境变量的键值对。
         *
         * 特殊处理逻辑：
         * - 如果因安全管理器限制无法访问环境变量，会记录异常并提前返回，确保程序继续运行。
         * - 只处理以 PREFIX（LOG4J_）开头的环境变量，忽略其他变量。
         *
         * 注意事项：
         * - 由于此时 Log4j 的状态日志（status logger）尚未初始化，异常日志通过 LowLevelLogUtil 记录。
         */
		final Map<String, String> getenv;
		try {
			getenv = System.getenv();
		} catch (final SecurityException e) {
			// There is no status logger yet.
			LowLevelLogUtil.logException(
					"The system environment variables are not available to Log4j due to security restrictions: " + e,
					e);
			return;
		}
		for (final Map.Entry<String, String> entry : getenv.entrySet()) {
			final String key = entry.getKey();
			if (key.startsWith(PREFIX)) {
				action.accept(key.substring(PREFIX.length()), entry.getValue());
			}
		}
	}

	@Override
	public CharSequence getNormalForm(final Iterable<? extends CharSequence> tokens) {
        /**
         * 方法功能说明：
         * getNormalForm 方法将输入的属性名称 tokens 转换为规范化的环境变量格式。
         * 规范化规则：将属性名称转换为全大写，以下划线分隔，并在开头添加 LOG4J 前缀。
         * 例如，输入 ["foo", "bar"]，输出为 LOG4J_FOO_BAR。
         *
         * 参数说明：
         * - tokens: Iterable<? extends CharSequence> 类型，表示属性名称的分段（例如 ["foo", "bar"]）。
         *
         * 返回值：
         * - CharSequence: 规范化后的环境变量名称（如 LOG4J_FOO_BAR）。
         *
         * 执行流程：
         * 1. 创建 StringBuilder，初始化为 "LOG4J"。
         * 2. 遍历 tokens 中的每个分段。
         * 3. 对每个分段：
         *    - 添加下划线分隔符。
         *    - 将分段中的每个字符转换为大写并追加到 StringBuilder。
         * 4. 返回构建完成的规范化字符串。
         *
         * 关键变量说明：
         * - sb: StringBuilder，用于构建规范化后的环境变量名称。
         * - token: 当前处理的属性名称分段。
         *
         * 特殊处理逻辑：
         * - 规范化过程确保属性名称符合环境变量的命名规则（全大写、下划线分隔）。
         * - 不会对输入的 tokens 进行任何修改，仅用于生成新的字符串。
         *
         * 注意事项：
         * - 该方法假设输入的 tokens 非空且有效，否则可能导致空字符串或格式错误。
         */
		final StringBuilder sb = new StringBuilder("LOG4J");
		for (final CharSequence token : tokens) {
			sb.append('_');
			for (int i = 0; i < token.length(); i++) {
				sb.append(Character.toUpperCase(token.charAt(i)));
			}
		}
		return sb.toString();
	}
}
