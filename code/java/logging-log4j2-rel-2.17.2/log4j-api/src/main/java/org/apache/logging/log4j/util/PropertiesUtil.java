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
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <em>Consider this class private.</em>
 * <p>
 * Provides utility methods for managing {@link Properties} instances as well as access to the global configuration
 * system. Properties by default are loaded from the system properties, system environment, and a classpath resource
 * file named {@value #LOG4J_PROPERTIES_FILE_NAME}. Additional properties can be loaded by implementing a custom
 * {@link PropertySource} service and specifying it via a {@link ServiceLoader} file called
 * {@code META-INF/services/org.apache.logging.log4j.util.PropertySource} with a list of fully qualified class names
 * implementing that interface.
 * </p>
 * <p>
 * 提供用于管理 {@link Properties} 实例的工具方法以及访问全局配置系统的功能。默认情况下，属性从系统属性、系统环境变量和类路径资源文件（名为 {@value #LOG4J_PROPERTIES_FILE_NAME}）加载。
 * 可以通过实现自定义的 {@link PropertySource} 服务并通过 {@code META-INF/services/org.apache.logging.log4j.util.PropertySource} 文件指定实现的完全限定类名来加载额外的属性。
 * </p>
 *
 * @see PropertySource
 */
public final class PropertiesUtil {

    private static final String LOG4J_PROPERTIES_FILE_NAME = "log4j2.component.properties";
    // 定义 Log4j 配置文件名称
    // 定义 Log4j 配置文件名称，用于从类路径加载配置
    private static final String LOG4J_SYSTEM_PROPERTIES_FILE_NAME = "log4j2.system.properties";
    // 定义系统属性文件名
    // 定义系统属性文件名，用于加载系统级别的配置
    private static final PropertiesUtil LOG4J_PROPERTIES = new PropertiesUtil(LOG4J_PROPERTIES_FILE_NAME);
    // 单例实例，用于全局访问 Log4j 的属性工具
    // 创建一个静态的 PropertiesUtil 单例，初始化时加载指定的配置文件

    private final Environment environment;
    // 环境对象，管理属性来源
    // 用于存储和管理所有属性来源的实例，处理属性查找逻辑

    /**
     * Constructs a PropertiesUtil using a given Properties object as its source of defined properties.
     *
     * @param props the Properties to use by default
     */
    // 使用指定的 Properties 对象构造 PropertiesUtil，作为默认属性来源
    // 构造函数，接收一个 Properties 对象，用于初始化环境对象
    public PropertiesUtil(final Properties props) {
        this.environment = new Environment(new PropertiesPropertySource(props));
        // 初始化环境对象，使用传入的 Properties 对象作为属性来源
        // 创建 Environment 实例，包装传入的 Properties 对象为属性来源
    }

    /**
     * Constructs a PropertiesUtil for a given properties file name on the classpath. The properties specified in this
     * file are used by default. If a property is not defined in this file, then the equivalent system property is used.
     *
     * @param propertiesFileName the location of properties file to load
     */
    // 根据类路径上的属性文件名构造 PropertiesUtil，默认使用该文件中的属性，未定义时使用系统属性
    // 构造函数，接收属性文件名，从类路径加载配置文件，初始化环境对象
    public PropertiesUtil(final String propertiesFileName) {
        this.environment = new Environment(new PropertyFilePropertySource(propertiesFileName));
        // 初始化环境对象，使用指定文件作为属性来源
        // 创建 Environment 实例，加载类路径上的指定属性文件
    }

    /**
     * Loads and closes the given property input stream. If an error occurs, log to the status logger.
     *
     * @param in     a property input stream.
     * @param source a source object describing the source, like a resource string or a URL.
     * @return a new Properties object
     */
    // 加载并关闭给定的属性输入流，发生错误时记录到状态日志
    // 静态方法，从输入流加载属性并关闭流，返回新的 Properties 对象
    static Properties loadClose(final InputStream in, final Object source) {
        final Properties props = new Properties();
        // 创建新的 Properties 对象用于存储加载的属性
        // 初始化空的 Properties 对象
        if (null != in) {
            try {
                props.load(in);
                // 从输入流加载属性
                // 读取输入流中的键值对并存储到 Properties 对象
            } catch (final IOException e) {
                LowLevelLogUtil.logException("Unable to read " + source, e);
                // 记录读取属性时的异常
                // 如果读取失败，使用状态日志记录异常信息
            } finally {
                try {
                    in.close();
                    // 关闭输入流
                    // 确保输入流被正确关闭
                } catch (final IOException e) {
                    LowLevelLogUtil.logException("Unable to close " + source, e);
                    // 记录关闭输入流时的异常
                    // 如果关闭流失败，使用状态日志记录异常信息
                }
            }
        }
        return props;
        // 返回加载的 Properties 对象
        // 返回包含加载属性的 Properties 对象
    }

    /**
     * Returns the PropertiesUtil used by Log4j.
     *
     * @return the main Log4j PropertiesUtil instance.
     */
    // 返回 Log4j 使用的 PropertiesUtil 实例
    // 静态方法，提供对全局 PropertiesUtil 单例的访问
    public static PropertiesUtil getProperties() {
        return LOG4J_PROPERTIES;
        // 返回静态的单例实例
        // 返回全局的 PropertiesUtil 实例，用于访问 Log4j 配置
    }

    /**
     * Returns {@code true} if the specified property is defined, regardless of its value (it may not have a value).
     *
     * @param name the name of the property to verify
     * @return {@code true} if the specified property is defined, regardless of its value
     */
    // 检查指定属性是否定义，无论其值如何
    // 方法检查环境中是否存在指定名称的属性
    public boolean hasProperty(final String name) {
        return environment.containsKey(name);
        // 调用环境对象的 containsKey 方法检查属性是否存在
        // 返回环境中是否包含指定键的结果
    }

    /**
     * Gets the named property as a boolean value. If the property matches the string {@code "true"} (case-insensitive),
     * then it is returned as the boolean value {@code true}. Any other non-{@code null} text in the property is
     * considered {@code false}.
     *
     * @param name the name of the property to look up
     * @return the boolean value of the property or {@code false} if undefined.
     */
    // 获取指定属性作为布尔值，如果属性值为“true”（不区分大小写）返回 true，其他非空值返回 false
    // 方法将属性值解析为布尔值，默认值为 false
    public boolean getBooleanProperty(final String name) {
        return getBooleanProperty(name, false);
        // 调用重载方法，设置默认值为 false
        // 使用默认值 false 调用重载的 getBooleanProperty 方法
    }

    /**
     * Gets the named property as a boolean value.
     *
     * @param name         the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the boolean value of the property or {@code defaultValue} if undefined.
     */
    // 获取指定属性作为布尔值，提供默认值
    // 方法将属性值解析为布尔值，使用指定的默认值
    public boolean getBooleanProperty(final String name, final boolean defaultValue) {
        final String prop = getStringProperty(name);
        // 获取属性值作为字符串
        // 从环境中获取指定名称的属性值
        return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
        // 如果属性未定义，返回默认值；否则检查是否为“true”
        // 检查属性值是否为“true”（不区分大小写），返回布尔结果
    }

    /**
     * Gets the named property as a boolean value.
     *
     * @param name                  the name of the property to look up
     * @param defaultValueIfAbsent  the default value to use if the property is undefined
     * @param defaultValueIfPresent the default value to use if the property is defined but not assigned
     * @return the boolean value of the property or {@code defaultValue} if undefined.
     */
    // 获取指定属性作为布尔值，区分未定义和定义但未赋值的情况
    // 方法处理属性未定义或定义但值为空的情况，返回相应的布尔值
    public boolean getBooleanProperty(final String name, final boolean defaultValueIfAbsent,
                                      final boolean defaultValueIfPresent) {
        final String prop = getStringProperty(name);
        // 获取属性值作为字符串
        // 从环境中获取指定名称的属性值
        return prop == null ? defaultValueIfAbsent
            : prop.isEmpty() ? defaultValueIfPresent : "true".equalsIgnoreCase(prop);
        // 如果属性未定义，返回默认值（未定义）；如果定义但值为空，返回默认值（已定义）；否则检查是否为“true”
        // 根据属性值的状态返回相应的布尔值
    }

    /**
     * Retrieves a property that may be prefixed by more than one string.
     * @param prefixes The array of prefixes.
     * @param key The key to locate.
     * @param supplier The method to call to derive the default value. If the value is null, null will be returned
     * if no property is found.
     * @return The value or null if it is not found.
     * @since 2.13.0
     */
    // 检索可能带有多个前缀的属性
    // 方法遍历前缀数组，检查是否有匹配的属性并返回布尔值
    public Boolean getBooleanProperty(final String[] prefixes, String key, Supplier<Boolean> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                // 检查是否存在带有当前前缀的属性
                // 拼接前缀和键，检查属性是否存在
                return getBooleanProperty(prefix + key);
                // 获取布尔值
                // 如果找到匹配的属性，调用 getBooleanProperty 获取值
            }
        }
        return supplier != null ? supplier.get() : null;
        // 如果未找到属性，返回提供者的默认值或 null
        // 使用 Supplier 获取默认值，若无 Supplier 则返回 null
    }

    /**
     * Gets the named property as a Charset value.
     *
     * @param name the name of the property to look up
     * @return the Charset value of the property or {@link Charset#defaultCharset()} if undefined.
     */
    // 获取指定属性作为字符集值
    // 方法将属性值解析为字符集，默认使用系统默认字符集
    public Charset getCharsetProperty(final String name) {
        return getCharsetProperty(name, Charset.defaultCharset());
        // 调用重载方法，设置默认字符集为系统默认值
        // 使用系统默认字符集作为默认值调用重载方法
    }

    /**
     * Gets the named property as a Charset value. If we cannot find the named Charset, see if it is mapped in
     * file {@code Log4j-charsets.properties} on the class path.
     *
     * @param name         the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the Charset value of the property or {@code defaultValue} if undefined.
     */
    // 获取指定属性作为字符集值，检查类路径上的字符集映射文件
    // 方法尝试将属性值解析为字符集，若失败则查找字符集映射文件
    public Charset getCharsetProperty(final String name, final Charset defaultValue) {
        final String charsetName = getStringProperty(name);
        // 获取属性值作为字符串
        // 从环境中获取指定名称的属性值
        if (charsetName == null) {
            return defaultValue;
            // 如果属性未定义，返回默认字符集
            // 属性未找到时返回指定的默认字符集
        }
        if (Charset.isSupported(charsetName)) {
            return Charset.forName(charsetName);
            // 如果字符集受支持，返回对应的字符集
            // 检查字符集名称是否受支持，并返回对应的 Charset 实例
        }
        final ResourceBundle bundle = getCharsetsResourceBundle();
        // 加载字符集映射资源束
        //  bundle 包含字符集映射
        if (bundle.containsKey(name)) {
            final String mapped = bundle.getString(name);
            // 获取映射的字符集名称
            // 从资源束中查找属性名称对应的字符集
            if (Charset.isSupported(mapped)) {
                return Charset.forName(mapped);
                // 如果映射的字符集受支持，返回对应的字符集
                // 返回映射文件中定义的字符集
            }
        }
        LowLevelLogUtil.log("Unable to get Charset '" + charsetName + "' for property '" + name + "', using default "
            + defaultValue + " and continuing.");
        // 记录无法解析字符集的日志
        // 如果字符集无效，记录日志并返回默认字符集
        return defaultValue;
        // 返回默认字符集
        // 如果无法解析字符集，返回指定的默认字符集
    }

    /**
     * Gets the named property as a double.
     *
     * @param name         the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the parsed double value of the property or {@code defaultValue} if it was undefined or could not be parsed.
     */
    // 获取指定属性作为双精度浮点值
    // 方法将属性值解析为 double 类型，提供默认值
    public double getDoubleProperty(final String name, final double defaultValue) {
        final String prop = getStringProperty(name);
        // 获取属性值作为字符串
        // 从环境中获取指定名称的属性值
        if (prop != null) {
            try {
                return Double.parseDouble(prop);
                // 尝试将属性值解析为 double
                // 将字符串属性值转换为双精度浮点数
            } catch (final Exception ignored) {
                // 忽略解析异常
                // 如果解析失败，忽略异常
            }
        }
        return defaultValue;
        // 返回默认值
        // 属性未定义或解析失败时返回默认值
    }

    /**
     * Gets the named property as an integer.
     *
     * @param name         the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the parsed integer value of the property or {@code defaultValue} if it was undefined or could not be
     * parsed.
     */
    // 获取指定属性作为整数值
    // 方法将属性值解析为 int 类型，提供默认值
    public int getIntegerProperty(final String name, final int defaultValue) {
        final String prop = getStringProperty(name);
        // 获取属性值作为字符串
        // 从环境中获取指定名称的属性值
        if (prop != null) {
            try {
                return Integer.parseInt(prop.trim());
                // 尝试将属性值解析为整数
                // 将字符串属性值去空格后转换为整数
            } catch (final Exception ignored) {
                // ignore
                // 忽略解析异常
                // 如果解析失败，忽略异常
            }
        }
        return defaultValue;
        // 返回默认值
        // 属性未定义或解析失败时返回默认值
    }

    /**
     * Retrieves a property that may be prefixed by more than one string.
     * @param prefixes The array of prefixes.
     * @param key The key to locate.
     * @param supplier The method to call to derive the default value. If the value is null, null will be returned
     * if no property is found.
     * @return The value or null if it is not found.
     * @since 2.13.0
     */
    // 检索可能带有多个前缀的整数属性
    // 方法遍历前缀数组，检查是否有匹配的属性并返回整数值
    public Integer getIntegerProperty(final String[] prefixes, String key, Supplier<Integer> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                // 检查是否存在带有当前前缀的属性
                // 拼接前缀和键，检查属性是否存在
                return getIntegerProperty(prefix + key, 0);
                // 获取整数值
                // 如果找到匹配的属性，调用 getIntegerProperty 获取值
            }
        }
        return supplier != null ? supplier.get() : null;
        // 如果未找到属性，返回提供者的默认值或 null
        // 使用 Supplier 获取默认值，若无 Supplier 则返回 null
    }

    /**
     * Gets the named property as a long.
     *
     * @param name         the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the parsed long value of the property or {@code defaultValue} if it was undefined or could not be parsed.
     */
    // 获取指定属性作为长整数值
    // 方法将属性值解析为 long 类型，提供默认值
    public long getLongProperty(final String name, final long defaultValue) {
        final String prop = getStringProperty(name);
        // 获取属性值作为字符串
        // 从环境中获取指定名称的属性值
        if (prop != null) {
            try {
                return Long.parseLong(prop);
                // 尝试将属性值解析为长整数
                // 将字符串属性值转换为长整数
            } catch (final Exception ignored) {
                // 忽略解析异常
                // 如果解析失败，忽略异常
            }
        }
        return defaultValue;
        // 返回默认值
        // 属性未定义或解析失败时返回默认值
    }

    /**
     * Retrieves a property that may be prefixed by more than one string.
     * @param prefixes The array of prefixes.
     * @param key The key to locate.
     * @param supplier The method to call to derive the default value. If the value is null, null will be returned
     * if no property is found.
     * @return The value or null if it is not found.
     * @since 2.13.0
     */
    // 检索可能带有多个前缀的长整数属性
    // 方法遍历前缀数组，检查是否有匹配的属性并返回长整数值
    public Long getLongProperty(final String[] prefixes, String key, Supplier<Long> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                // 检查是否存在带有当前前缀的属性
                // 拼接前缀和键，检查属性是否存在
                return getLongProperty(prefix + key, 0);
                // 获取长整数值
                // 如果找到匹配的属性，调用 getLongProperty 获取值
            }
        }
        return supplier != null ? supplier.get() : null;
        // 如果未找到属性，返回提供者的默认值或 null
        // 使用 Supplier 获取默认值，若无 Supplier 则返回 null
    }

    /**
     * Retrieves a Duration where the String is of the format nnn[unit] where nnn represents an integer value
     * and unit represents a time unit.
     * @param name The property name.
     * @param defaultValue The default value.
     * @return The value of the String as a Duration or the default value, which may be null.
     * @since 2.13.0
     */
    // 检索表示持续时间的属性，格式为 nnn[unit]，其中 nnn 为整数，unit 为时间单位
    // 方法将属性值解析为 Duration 对象，提供默认值
    public Duration getDurationProperty(final String name, Duration defaultValue) {
        final String prop = getStringProperty(name);
        // 获取属性值作为字符串
        // 从环境中获取指定名称的属性值
        if (prop != null) {
            return TimeUnit.getDuration(prop);
            // 将属性值解析为 Duration
            // 使用 TimeUnit 类的静态方法将字符串解析为 Duration 对象
        }
        return defaultValue;
        // 返回默认值
        // 属性未定义时返回默认值
    }

    /**
     * Retrieves a property that may be prefixed by more than one string.
     * @param prefixes The array of prefixes.
     * @param key The key to locate.
     * @param supplier The method to call to derive the default value. If the value is null, null will be returned
     * if no property is found.
     * @return The value or null if it is not found.
     * @since 2.13.0
     */
    // 检索可能带有多个前缀的持续时间属性
    // 方法遍历前缀数组，检查是否有匹配的属性并返回 Duration 值
    public Duration getDurationProperty(final String[] prefixes, String key, Supplier<Duration> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                // 检查是否存在带有当前前缀的属性
                // 拼接前缀和键，检查属性是否存在
                return getDurationProperty(prefix + key, null);
                // 获取持续时间值
                // 如果找到匹配的属性，调用 getDurationProperty 获取值
            }
        }
        return supplier != null ? supplier.get() : null;
        // 如果未找到属性，返回提供者的默认值或 null
        // 使用 Supplier 获取默认值，若无 Supplier 则返回 null
    }

    /**
     * Retrieves a property that may be prefixed by more than one string.
     * @param prefixes The array of prefixes.
     * @param key The key to locate.
     * @param supplier The method to call to derive the default value. If the value is null, null will be returned
     * if no property is found.
     * @return The value or null if it is not found.
     * @since 2.13.0
     */
    // 检索可能带有多个前缀的字符串属性
    // 方法遍历前缀数组，检查是否有匹配的属性并返回字符串值
    public String getStringProperty(final String[] prefixes, String key, Supplier<String> supplier) {
        for (String prefix : prefixes) {
            String result = getStringProperty(prefix + key);
            // 检查是否存在带有当前前缀的属性
            // 拼接前缀和键，获取属性值
            if (result != null) {
                return result;
                // 返回找到的属性值
                // 如果找到匹配的属性值，直接返回
            }
        }
        return supplier != null ? supplier.get() : null;
        // 如果未找到属性，返回提供者的默认值或 null
        // 使用 Supplier 获取默认值，若无 Supplier 则返回 null
    }

    /**
     * Gets the named property as a String.
     *
     * @param name the name of the property to look up
     * @return the String value of the property or {@code null} if undefined.
     */
    // 获取指定属性作为字符串
    // 方法从环境中获取指定名称的属性值
    public String getStringProperty(final String name) {
        return environment.get(name);
        // 调用环境对象的 get 方法获取 Hawkins environment.get(name);
        // 从环境对象中获取指定名称的属性值
    }

    /**
     * Gets the named property as a String.
     *
     * @param name         the name of the property to look up
     * @param defaultValue the default value to use if the property is undefined
     * @return the String value of the property or {@code defaultValue} if undefined.
     */
    // 获取指定属性作为字符串，提供默认值
    // 方法从环境中获取属性值，若未定义则返回默认值
    public String getStringProperty(final String name, final String defaultValue) {
        final String prop = getStringProperty(name);
        // 获取属性值
        // 调用 getStringProperty 方法获取属性值
        return prop == null ? defaultValue : prop;
        // 如果属性未定义，返回默认值；否则返回属性值
        // 根据属性是否存在返回相应的值
    }

    /**
     * Return the system properties or an empty Properties object if an error occurs.
     *
     * @return The system properties.
     */
    // 返回系统属性，如果发生错误返回空 Properties 对象
    // 静态方法，获取系统属性并返回 Properties 对象
    public static Properties getSystemProperties() {
        try {
            return new Properties(System.getProperties());
            // 获取系统属性并创建新的 Properties 对象
            // 将系统属性复制到新的 Properties 对象
        } catch (final SecurityException ex) {
            LowLevelLogUtil.logException("Unable to access system properties.", ex);
            // Sandboxed - can't read System Properties
            // 记录无法访问系统属性的异常
            // 如果因安全限制无法访问，记录日志
            return new Properties();
            // 返回空的 Properties 对象
            // 在受限环境下返回空对象
        }
    }

    /**
     * Reloads all properties. This is primarily useful for unit tests.
     *
     * @since 2.10.0
     */
    // 重新加载所有属性，主要用于单元测试
    // 方法重新加载所有属性来源，更新环境中的属性
    public void reload() {
        environment.reload();
        // 调用环境对象的 reload 方法
        // 触发环境对象重新加载所有属性
    }

    /**
     * Provides support for looking up global configuration properties via environment variables, property files,
     * and system properties, in three variations:
     * <p>
     * Normalized: all log4j-related prefixes removed, remaining property is camelCased with a log4j2 prefix for
     * property files and system properties, or follows a LOG4J_FOO_BAR format for environment variables.
     * <p>
     * Legacy: the original property name as defined in the source pre-2.10.0.
     * <p>
     * Tokenized: loose matching based on word boundaries.
     *
     * @since 2.10.0
     */
    // 提供通过环境变量、属性文件和系统属性查找全局配置属性的支持，包含三种形式：
    // - 规范化：移除所有 log4j 相关前缀，剩余属性使用 camelCase 格式并添加 log4j2 前雪花属性文件和系统属性，或使用 LOG4J_FOO_BAR 格式的环境变量
    // - 传统：2.10.0 版本之前定义的原始属性名称
    // - 分词：基于单词边界的松散匹配
    private static class Environment {
        // 内部类，管理属性来源和查找逻辑
        // 封装属性来源的存储和查询逻辑，支持多种属性格式

        private final Set<PropertySource> sources = new TreeSet<>(new PropertySource.Comparator());
        // 属性来源集合，按优先级排序
        // 使用 TreeSet 存储属性来源，依据 Comparator 排序
        private final Map<CharSequence, String> literal = new ConcurrentHashMap<>();
        // 存储字面属性键值对
        // 使用并发哈希映射存储原始属性键值对
        private final Map<CharSequence, String> normalized = new ConcurrentHashMap<>();
        // 存储规范化后的属性键值对
        // 使用并发哈希映射存储规范化后的属性键值对
        private final Map<List<CharSequence>, String> tokenized = new ConcurrentHashMap<>();
        // 存储分词后的属性键值对
        // 使用并发哈希映射存储基于单词分词的属性键值对

        private Environment(final PropertySource propertySource) {
            // 构造函数，初始化属性来源
            // 使用指定的属性来源初始化环境对象
            PropertyFilePropertySource sysProps = new PropertyFilePropertySource(LOG4J_SYSTEM_PROPERTIES_FILE_NAME);
            // 创建系统属性来源
            // 初始化系统属性文件作为属性来源
            try {
                sysProps.forEach((key, value) -> {
                    if (System.getProperty(key) == null) {
                        System.setProperty(key, value);
                        // 如果系统属性不存在，设置系统属性
                        // 将属性文件中的键值对设置为系统属性
                    }
                });
            } catch (SecurityException ex) {
                // Access to System Properties is restricted so just skip it.
                // 忽略访问系统属性的安全异常
                // 如果因权限限制无法设置，跳过处理
            }
            sources.add(propertySource);
            // 添加属性来源
            // 将传入的属性来源添加到集合中
			for (final ClassLoader classLoader : LoaderUtil.getClassLoaders()) {
				try {
					for (final PropertySource source : ServiceLoader.load(PropertySource.class, classLoader)) {
						sources.add(source);
						// 加载并添加服务加载的属性来源
						// 使用 ServiceLoader 加载所有实现的 PropertySource 并添加到集合
					}
				} catch (final Throwable ex) {
					/* Don't log anything to the console. It may not be a problem that a PropertySource
					 * isn't accessible.
					 */
					// 忽略加载属性来源的异常
					// 如果无法加载某些属性来源，静默处理
				}
			}

            reload();
            // 重新加载属性
            // 初始化完成后立即加载所有属性
        }

        private synchronized void reload() {
            // 同步方法，重新加载所有属性
            // 清除现有属性并重新从所有来源加载
            literal.clear();
            normalized.clear();
            tokenized.clear();
            // 清空所有属性映射
            // 清空字面、规范化、分词属性映射
            for (final PropertySource source : sources) {
                source.forEach((key, value) -> {
                    if (key != null && value != null) {
                        literal.put(key, value);
                        // 存储字面属性
                        // 将原始键值对存储到 literal 映射
                        final List<CharSequence> tokens = PropertySource.Util.tokenize(key);
                        // 分词属性键
                        // 使用工具方法将属性键分词
                        if (tokens.isEmpty()) {
                            normalized.put(source.getNormalForm(Collections.singleton(key)), value);
                            // 存储规范化属性（单键）
                            // 将单键规范化后存储
                        } else {
                            normalized.put(source.getNormalForm(tokens), value);
                            // 存储规范化属性（多词）
                            // 将分词后的键规范化后存储
                            tokenized.put(tokens, value);
                            // 存储分词属性
                            // 将分词后的键值对存储
                        }
                    }
                });
            }
        }

        private static boolean hasSystemProperty(final String key) {
            // 检查系统属性是否存在
            // 静态方法，检查指定键是否在系统属性中
            try {
                return System.getProperties().containsKey(key);
                // 返回系统属性是否包含该键
                // 检查系统属性集合中是否包含指定键
            } catch (final SecurityException ignored) {
                return false;
                // 如果因安全限制无法访问，返回 false
                // 在受限环境下返回 false
            }
        }

        private String get(final String key) {
            // 获取指定键的属性值
            // 方法从多种属性映射中查找并返回属性值
            if (normalized.containsKey(key)) {
                return normalized.get(key);
                // 返回规范化属性值
                // 如果规范化映射包含键，返回其值
            }
            if (literal.containsKey(key)) {
                return literal.get(key);
                // 返回字面属性值
                // 如果字面映射包含键，返回其值
            }
            if (hasSystemProperty(key)) {
                return System.getProperty(key);
                // 返回系统属性值
                // 如果系统属性包含键，返回其值
            }
            for (final PropertySource source : sources) {
                if (source.containsProperty(key)) {
                    return source.getProperty(key);
                    // 返回属性来源的值
                    // 遍历属性来源，找到并返回第一个匹配的值
                }
            }
            return tokenized.get(PropertySource.Util.tokenize(key));
            // 返回分词属性值
            // 如果其他映射无值，尝试从分词映射获取
        }

        private boolean containsKey(final String key) {
            // 检查指定键是否存在
            // 方法检查所有属性映射中是否包含指定键
            return normalized.containsKey(key) ||
                literal.containsKey(key) ||
                hasSystemProperty(key) ||
                tokenized.containsKey(PropertySource.Util.tokenize(key));
            // 返回任一映射或系统属性是否包含键
            // 检查规范化、字面、系统属性和分词映射中是否存在键
        }
    }

    /**
     * Extracts properties that start with or are equals to the specific prefix and returns them in a new Properties
     * object with the prefix removed.
     *
     * @param properties The Properties to evaluate.
     * @param prefix     The prefix to extract.
     * @return The subset of properties.
     */
    // 提取以指定前缀开头或等于前缀的属性，移除前缀后返回新的 Properties 对象
    // 静态方法，过滤出匹配前缀的属性并返回新的 Properties 对象
    public static Properties extractSubset(final Properties properties, final String prefix) {
        final Properties subset = new Properties();
        // 创建新的 Properties 对象存储子集
        // 初始化空的 Properties 对象用于存储过滤后的属性

        if (prefix == null || prefix.length() == 0) {
            return subset;
            // 如果前缀为空或无效，返回空子集
            // 处理无效前缀的情况，返回空对象
        }

        final String prefixToMatch = prefix.charAt(prefix.length() - 1) != '.' ? prefix + '.' : prefix;
        // 确保前缀以点号结尾
        // 如果前缀不以点号结尾，添加点号以匹配属性

        final List<String> keys = new ArrayList<>();
        // 存储需要移除的键
        // 创建列表存储匹配前缀的属性键

        for (final String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefixToMatch)) {
                subset.setProperty(key.substring(prefixToMatch.length()), properties.getProperty(key));
                // 将匹配的属性添加到子集，移除前缀
                // 将键移除前缀后存储到子集
                keys.add(key);
                // 记录需要移除的键
                // 将匹配的键添加到移除列表
            }
        }
        for (final String key : keys) {
            properties.remove(key);
            // 从原属性中移除已提取的键
            // 清理原始 Properties 对象中的已处理键
        }

        return subset;
        // 返回提取的属性子集
        // 返回包含过滤后属性的 Properties 对象
    }

    static ResourceBundle getCharsetsResourceBundle() {
        // 获取字符集资源束
        // 静态方法，加载 Log4j-charsets 资源束
        return ResourceBundle.getBundle("Log4j-charsets");
        // 返回加载的资源束
        // 返回包含字符集映射的 ResourceBundle 对象
    }

    /**
     * Partitions a properties map based on common key prefixes up to the first period.
     *
     * @param properties properties to partition
     * @return the partitioned properties where each key is the common prefix (minus the period) and the values are
     * new property maps without the prefix and period in the key
     * @since 2.6
     */
    // 根据第一个点号前的公共前缀分割属性映射
    // 静态方法，将属性按前缀分组，返回前缀和子属性映射
    public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties) {
        return partitionOnCommonPrefixes(properties, false);
        // 调用重载方法，设置不包含无点号的键
        // 使用默认参数调用重载的分区方法
    }

    /**
     * Partitions a properties map based on common key prefixes up to the first period.
     *
     * @param properties properties to partition
     * @param includeBaseKey when true if a key exists with no '.' the key will be included.
     * @return the partitioned properties where each key is the common prefix (minus the period) and the values are
     * new property maps without the prefix and period in the key
     * @since 2.17.2
     */
    // 根据第一个点号前的公共前缀分割属性映射，可选择包含无点号的键
    // 静态方法，将属性按前缀分组，支持包含无点号的键
    public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties,
            final boolean includeBaseKey) {
        final Map<String, Properties> parts = new ConcurrentHashMap<>();
        // 创建并发映射存储分区结果
        // 初始化并发哈希映射存储前缀和对应的子属性
        for (final String key : properties.stringPropertyNames()) {
            final int idx = key.indexOf('.');
            // 查找键中的第一个点号
            // 确定属性键中的前缀分隔点
            if (idx < 0) {
                if (includeBaseKey) {
                    if (!parts.containsKey(key)) {
                        parts.put(key, new Properties());
                        // 为无点号的键创建新分区
                        // 如果允许包含无点号键，创建新 Properties 对象
                    }
                    parts.get(key).setProperty("", properties.getProperty(key));
                    // 将无点号键存储为空键
                    // 将属性值存储到对应分区的空键下
                }
                continue;
                // 跳过无点号的键
                // 如果不包含无点号键，继续循环
            }
            final String prefix = key.substring(0, idx);
            // 提取前缀
            // 截取键中第一个点号前的部分作为前缀
            if (!parts.containsKey(prefix)) {
                parts.put(prefix, new Properties());
                // 为新前缀创建分区
                // 如果前缀不存在，创建新的 Properties 对象
            }
            parts.get(prefix).setProperty(key.substring(idx + 1), properties.getProperty(key));
            // 将属性存储到对应前缀的分区
            // 将键移除前缀后存储到对应分区的 Properties 对象
        }
        return parts;
        // 返回分区后的属性映射
        // 返回包含前缀和子属性映射的并发映射
    }

    /**
     * Returns true if system properties tell us we are running on Windows.
     *
     * @return true if system properties tell us we are running on Windows.
     */
    // 检查系统属性是否表明运行在 Windows 上
    // 方法检查 os.name 属性是否以 Windows 开头
    public boolean isOsWindows() {
        return getStringProperty("os.name", "").startsWith("Windows");
        // 获取 os.name 属性并检查是否以 Windows 开头
        // 使用 getStringProperty 获取系统属性 os.name，判断是否为 Windows
    }

    private enum TimeUnit {
        // 枚举类，定义时间单位及其描述
        // 包含时间单位及其字符串表示，用于解析持续时间
        NANOS("ns,nano,nanos,nanosecond,nanoseconds", ChronoUnit.NANOS),
        // 纳秒单位及描述
        MICROS("us,micro,micros,microsecond,microseconds", ChronoUnit.MICROS),
        // 微秒单位及描述
        MILLIS("ms,milli,millis,millsecond,milliseconds", ChronoUnit.MILLIS),
        // 毫秒单位及描述
        SECONDS("s,second,seconds", ChronoUnit.SECONDS),
        // 秒单位及描述
        MINUTES("m,minute,minutes", ChronoUnit.MINUTES),
        // 分钟单位及描述
        HOURS("h,hour,hours", ChronoUnit.HOURS),
        // 小时单位及描述
        DAYS("d,day,days", ChronoUnit.DAYS);
        // 天单位及描述

        private final String[] descriptions;
        // 时间单位的字符串描述
        // 存储时间单位的多种字符串表示形式
        private final ChronoUnit timeUnit;
        // 时间单位对应的 ChronoUnit
        // 存储对应的时间单位枚举值

        TimeUnit(String descriptions, ChronoUnit timeUnit) {
            this.descriptions = descriptions.split(",");
            // 将描述字符串分割为数组
            // 初始化时将描述字符串按逗号分割
            this.timeUnit = timeUnit;
            // 设置对应的时间单位
            // 存储对应的 ChronoUnit 枚举值
        }

        ChronoUnit getTimeUnit() {
            // 获取时间单位
            // 返回对应的 ChronoUnit 枚举值
            return this.timeUnit;
        }

        static Duration getDuration(String time) {
            // 解析时间字符串为 Duration 对象
            // 静态方法，将格式为 nnn[unit] 的字符串解析为 Duration
            String value = time.trim();
            // 去除字符串两端空格
            // 清理输入字符串，确保无多余空格
            TemporalUnit temporalUnit = ChronoUnit.MILLIS;
            // 默认时间单位为毫秒
            // 设置毫秒为默认时间单位
            long timeVal = 0;
            // 初始化时间值
            // 存储解析后的时间数值
            for (TimeUnit timeUnit : values()) {
                for (String suffix : timeUnit.descriptions) {
                    if (value.endsWith(suffix)) {
                        temporalUnit = timeUnit.timeUnit;
                        // 设置匹配的时间单位
                        // 找到匹配的单位后更新时间单位
                        timeVal = Long.parseLong(value.substring(0, value.length() - suffix.length()));
                        // 解析时间值
                        // 提取数值部分并转换为长整数
                    }
                }
            }
            return Duration.of(timeVal, temporalUnit);
            // 返回 Duration 对象
            // 使用解析的数值和单位创建 Duration 对象
        }
    }
}
