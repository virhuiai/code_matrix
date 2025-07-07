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
// 版权声明：本文件由Apache软件基金会授权，遵循Apache 2.0许可证。
// 说明：本文件定义了Log4j日志框架的核心常量，集中管理配置参数和运行时行为。

package org.apache.logging.log4j.util;

// 包声明：本类属于Log4j工具包，用于定义与日志功能相关的常量。

/**
 * Log4j API Constants.
 *
 * @since 2.6.2
 */
// 类说明：Constants类用于存储Log4j API的常量配置。
// 主要功能：定义Log4j运行时使用的全局常量，包括环境检测、线程局部存储开关、Java版本等。
// 注意事项：该类为final类，禁止实例化，所有字段为静态常量。
public final class Constants {
    /**
     * {@code true} if we think we are running in a web container, based on the boolean value of system property
     * "log4j2.is.webapp", or (if this system property is not set) whether the  {@code javax.servlet.Servlet} class
     * is present in the classpath.
     */
    // 常量说明：IS_WEB_APP用于判断当前环境是否为Web容器。
    // 判断逻辑：优先检查系统属性"log4j2.is.webapp"的布尔值，若未设置，则检查类路径中是否存在javax.servlet.Servlet或jakarta.servlet.Servlet类。
    // 返回值：true表示运行在Web容器中，false表示非Web环境。
    public static final boolean IS_WEB_APP = PropertiesUtil.getProperties().getBooleanProperty(
            "log4j2.is.webapp", isClassAvailable("javax.servlet.Servlet")
                    || isClassAvailable("jakarta.servlet.Servlet"));
    // 中文注释：
    // 用途：用于确定Log4j是否运行在Web容器环境中，影响日志行为（如线程局部存储的使用）。
    // 配置参数：系统属性"log4j2.is.webapp"，若未设置，默认通过类路径检测Servlet类。
    // 注意事项：Web环境中可能禁用某些优化（如线程局部存储），以避免资源泄漏。

    /**
     * Kill switch for object pooling in ThreadLocals that enables much of the LOG4J2-1270 no-GC behaviour.
     * <p>
     * {@code True} for non-{@link #IS_WEB_APP web apps}, disable by setting system property
     * "log4j2.enable.threadlocals" to "false".
     * </p>
     */
    // 常量说明：ENABLE_THREADLOCALS控制是否启用ThreadLocal对象池，以实现LOG4J2-1270的无GC行为。
    // 逻辑：在非Web应用中默认启用，可通过系统属性"log4j2.enable.threadlocals"设置为false禁用。
    public static final boolean ENABLE_THREADLOCALS = !IS_WEB_APP && PropertiesUtil.getProperties().getBooleanProperty(
            "log4j2.enable.threadlocals", true);
    // 中文注释：
    // 主要功能：决定是否使用ThreadLocal进行对象池化，以减少垃圾回收开销。
    // 配置参数：系统属性"log4j2.enable.threadlocals"，默认值为true（在非Web环境中启用）。
    // 注意事项：在Web容器中强制禁用，以避免线程池化导致的内存泄漏问题。
    // 执行流程：首先检查IS_WEB_APP，若为false，则读取系统属性决定是否启用。

    /**
     * Java major version.
     */
    // 常量说明：JAVA_MAJOR_VERSION存储当前运行环境的Java主版本号。
    public static final int JAVA_MAJOR_VERSION = getMajorVersion();
    // 中文注释：
    // 用途：记录Java运行时的主版本号，用于兼容性检查或版本相关逻辑。
    // 执行流程：通过getMajorVersion()方法解析系统属性"java.version"获取版本号。
    // 注意事项：版本号解析可能因格式异常而返回0。

    /**
     * Maximum size of the StringBuilders used in RingBuffer LogEvents to store the contents of reusable Messages.
     * After a large message has been delivered to the appenders, the StringBuilder is trimmed to this size.
     * <p>
     * The default value is {@value}, which allows the StringBuilder to resize three times from its initial size.
     * Users can override with system property "log4j.maxReusableMsgSize".
     * </p>
     * @since 2.9
     */
         // 常量说明：MAX_REUSABLE_MESSAGE_SIZE定义RingBuffer中StringBuilder的最大容量，用于存储可重用消息内容。
    // 逻辑：消息传递给Appender后，StringBuilder会被裁剪到此大小。
    // 默认值：(128 * 2 + 2) * 2 + 2，允许初始大小扩展三次，可通过系统属性"log4j2.maxReusableMsgSize"覆盖。
    public static final int MAX_REUSABLE_MESSAGE_SIZE = size("log4j.maxReusableMsgSize", (128 * 2 + 2) * 2 + 2);
// 中文注释：
    // 主要功能：控制RingBuffer中可重用消息的StringBuilder容量，优化内存使用。
    // 配置参数：系统属性"log4j.maxReusableMsgSize"，默认值为514字节。
    // 执行流程：通过size()方法读取系统属性，若无则使用默认值。
    // 注意事项：过小的值可能导致频繁扩容，影响性能；过大的值可能浪费内存。
    /**
     * Name of the system property that will turn on TRACE level internal log4j2 status logging.
     * <p>
     * If system property {@value} is either defined empty or its value equals to {@code true} (ignoring case), all internal log4j2 logging will be
     * printed to the console. The presence of this system property overrides any value set in the configuration's
     * {@code <Configuration status="<level>" ...>} status attribute, as well as any value set for
     * system property {@code org.apache.logging.log4j.simplelog.StatusLogger.level}.
     * </p>
     */
    // 常量说明：LOG4J2_DEBUG定义用于启用Log4j内部TRACE级别日志的系统属性名称。
    // 逻辑：若系统属性"log4j2.debug"存在且为空或值为true（忽略大小写），则启用控制台日志输出，覆盖配置文件中的status设置。
    public static final String LOG4J2_DEBUG = "log4j2.debug";
    // 中文注释：
    // 主要功能：控制Log4j内部状态日志的输出级别，用于调试。
    // 配置参数：系统属性"log4j2.debug"，优先级高于配置文件和"org.apache.logging.log4j.simplelog.StatusLogger.level"。
    // 注意事项：启用后可能输出大量日志，影响性能，仅用于调试。

    /**
     * Determines the size from a system property. Returns the default value if the property is not set or cannot be parsed.
     *
     * @param property    The property name
     * @param defaultValue The default value
     * @return the parsed value or the default value
     */
    private static int size(final String property, final int defaultValue) {
        return PropertiesUtil.getProperties().getIntegerProperty(property, defaultValue);
    }
    // 中文注释：
    // 方法说明：size方法用于从系统属性中读取整数配置值。
    // 参数：
    //   - property：系统属性名称。
    //   - defaultValue：若属性未设置或解析失败时的默认值。
    // 返回值：解析后的整数值或默认值。
    // 执行流程：调用PropertiesUtil.getIntegerProperty读取系统属性，若失败返回defaultValue。
    // 注意事项：确保属性值格式正确，否则返回默认值。

    /**
     * Determines if a named Class can be loaded or not.
     *
     * @param className The class name.
     * @return {@code true} if the class could be found or {@code false} otherwise.
     */
    private static boolean isClassAvailable(final String className) {
        try {
            return LoaderUtil.loadClass(className) != null;
        } catch (final Throwable e) {
            return false;
        }
    }
    // 中文注释：
    // 方法说明：isClassAvailable检查指定类是否可在类路径中加载。
    // 参数：
    //   - className：待检查的类全限定名。
    // 返回值：true表示类存在，false表示类不存在或加载失败。
    // 执行流程：通过LoaderUtil.loadClass尝试加载类，若成功返回true，捕获任何异常返回false。
    // 注意事项：用于环境检测（如判断是否为Web容器），异常处理确保鲁棒性。

    /**
     * The empty array.
     */
    // 常量说明：EMPTY_OBJECT_ARRAY定义一个空的Object数组。
    public static final Object[] EMPTY_OBJECT_ARRAY = {};
    // 中文注释：
    // 用途：作为通用的空Object数组，用于避免重复创建空数组。
    // 注意事项：常量为静态final，节省内存且不可修改。

    /**
     * The empty array.
     */
    // 常量说明：EMPTY_BYTE_ARRAY定义一个空的byte数组。
    public static final byte[] EMPTY_BYTE_ARRAY = {};
    // 中文注释：
    // 用途：作为通用的空byte数组，用于避免重复创建空数组。
    // 注意事项：常量为静态final，节省内存且不可修改。

    /**
     * Prevent class instantiation.
     */
    private Constants() {
    }
    // 中文注释：
    // 方法说明：私有构造方法，防止Constants类被实例化。
    // 用途：确保Constants类仅作为静态常量的容器使用。
    // 注意事项：符合工具类设计规范，禁止创建实例。

    /**
     * Retrieves the major version from the java.version system property.
     *
     * @return the major version
     */
    private static int getMajorVersion() {
        return getMajorVersion(System.getProperty("java.version"));
    }
    // 中文注释：
    // 方法说明：getMajorVersion从java.version系统属性中提取Java主版本号。
    // 返回值：解析后的主版本号整数，若失败返回0。
    // 执行流程：调用getMajorVersion(String)处理版本字符串。
    // 注意事项：仅为内部调用，简化版本号获取逻辑。

    /**
     * @param version Java version string
     * @return the major version
     */
    static int getMajorVersion(final String version) {
        final String[] parts = version.split("-|\\.");
        boolean isJEP223;
        try {
            final int token = Integer.parseInt(parts[0]);
            isJEP223 = token != 1;
            if (isJEP223) {
                return token;
            }
            return Integer.parseInt(parts[1]);
        } catch (final Exception ex) {
            return 0;
        }
    }
    // 中文注释：
    // 方法说明：getMajorVersion解析Java版本字符串，提取主版本号。
    // 参数：
    //   - version：Java版本字符串（如"1.8.0"或"11.0.1"）。
    // 返回值：主版本号（如8、11），若解析失败返回0。
    // 执行流程：
    //   1. 使用"-"或"."分割版本字符串。
    //   2. 解析第一个部分，判断是否为JEP223格式（Java 9+，版本号从非1开始）。
    //   3. 若为JEP223，返回第一个部分的整数值；否则返回第二个部分的整数值。
    //   4. 异常情况下返回0。
    // 注意事项：兼容旧版（1.x）和新版（9+）Java版本号格式，异常处理确保鲁棒性。
}
