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
 * A type of builder that can be used to configure and create a instances using a Java DSL instead of
 * through a configuration file. These builders are primarily useful for internal code and unit tests, but they can
 * technically be used as a verbose alternative to configuration files.
 * <p>
 * 一种构建器类型，可用于使用 Java DSL 而非配置文件来配置和创建实例。
 * 这些构建器主要用于内部代码和单元测试，但实际上它们也可以作为配置文件的冗长替代方案。
 * </p>
 *
 * <p>
 *     When creating <em>plugin</em> builders, it is customary to create the builder class as a public static inner class
 *     called {@code Builder}. For instance, the builder class for
 *     {@link org.apache.logging.log4j.core.layout.PatternLayout PatternLayout} would be
 *     {@code PatternLayout.Builder}.
 * </p>
 * <p>
 * 在创建 <em>插件</em> 构建器时，通常会将构建器类创建为一个名为 {@code Builder} 的公共静态内部类。
 * 例如，{@link org.apache.logging.log4j.core.layout.PatternLayout PatternLayout} 的构建器类将是
 * {@code PatternLayout.Builder}。
 * </p>
 *
 * @param <T> This builder creates instances of this class.
 * @param <T> 此构建器创建此类的实例。
 */
public interface Builder<T> {

    /**
     * Builds the object after all configuration has been set. This will use default values for any
     * unspecified attributes for the object.
     * 构建所有配置设置完毕后的对象。这将为对象的任何未指定属性使用默认值。
     *
     * @return the configured instance.
     * 返回配置好的实例。
     * @throws org.apache.logging.log4j.core.config.ConfigurationException if there was an error building the
     * object.
     * @throws org.apache.logging.log4j.core.config.ConfigurationException 如果构建对象时出错，则抛出此异常。
     */
    T build();
}
