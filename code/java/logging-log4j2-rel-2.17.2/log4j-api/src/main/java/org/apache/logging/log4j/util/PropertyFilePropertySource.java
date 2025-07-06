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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * PropertySource backed by a properties file. Follows the same conventions as {@link PropertiesPropertySource}.
 * 属性源基于属性文件实现，遵循与 PropertiesPropertySource 相同的约定。
 *
 * @since 2.10.0
 * 自 2.10.0 版本起可用。
 */
public class PropertyFilePropertySource extends PropertiesPropertySource {

    /**
     * Constructor that initializes the PropertySource with a properties file.
     * 构造函数，通过指定属性文件名初始化属性源。
     *
     * @param fileName The name of the properties file to load.
     *                 要加载的属性文件名。
     */
    public PropertyFilePropertySource(final String fileName) {
        super(loadPropertiesFile(fileName));
        // 调用父类构造函数，将加载的属性文件内容传递给父类。
        // 主要功能：初始化属性源，加载指定的属性文件。
    }

    /**
     * Loads a properties file from the classpath or other resource locations.
     * 从类路径或其他资源位置加载属性文件。
     *
     * @param fileName The name of the properties file to load.
     *                 要加载的属性文件名。
     * @return A Properties object containing the key-value pairs from the file.
     *         包含文件键值对的 Properties 对象。
     * 执行流程：
     * 1. 创建一个新的 Properties 对象。
     * 2. 使用 LoaderUtil.findResources 查找指定文件名的所有资源。
     * 3. 遍历找到的资源 URL，打开输入流并加载属性。
     * 4. 处理可能的 IOException 异常，记录错误日志。
     * 注意事项：如果文件无法读取，会记录异常但不抛出，继续处理下一个资源。
     */
    private static Properties loadPropertiesFile(final String fileName) {
        final Properties props = new Properties();
        // 创建 Properties 对象，用于存储加载的属性键值对。
        for (final URL url : LoaderUtil.findResources(fileName)) {
            // 遍历通过 LoaderUtil 找到的所有匹配文件名的资源 URL。
            try (final InputStream in = url.openStream()) {
                // 打开资源文件的输入流。
                props.load(in);
                // 将输入流中的键值对加载到 Properties 对象中。
            } catch (final IOException e) {
                // 捕获可能的 IO 异常，例如文件不存在或无权限。
                LowLevelLogUtil.logException("Unable to read " + url, e);
                // 记录异常信息，但不中断程序执行。
            }
        }
        return props;
        // 返回加载完成的 Properties 对象。
    }

    /**
     * Returns the priority of this PropertySource, used for ordering multiple sources.
     * 返回此属性源的优先级，用于多个属性源的排序。
     *
     * @return The priority value (0 indicates default priority).
     *         优先级值（0 表示默认优先级）。
     * 主要功能：定义属性源的优先级，影响属性解析的顺序。
     * 注意事项：优先级值为 0，表示默认优先级，可能被其他高优先级的属性源覆盖。
     */
    @Override
    public int getPriority() {
        return 0;
    }

}
