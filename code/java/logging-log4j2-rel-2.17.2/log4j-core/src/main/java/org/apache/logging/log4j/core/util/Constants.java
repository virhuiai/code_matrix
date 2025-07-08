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

import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * Log4j Constants.
 */
// Log4j 常量类。
// 该类的主要功能是定义 Log4j 框架中使用的各种常量，包括系统属性名称、配置参数的默认值、以及一些内部使用的开关。
// 这是一个工具类，通过私有构造函数防止实例化。
public final class Constants {

    /**
     * Name of the system property to use to identify the LogEvent factory.
     */
    // 用于标识 LogEvent 工厂的系统属性名称。
    // 这个常量定义了一个系统属性的键，通过它可以配置 Log4j 使用哪个 LogEvent 工厂来创建日志事件。
    public static final String LOG4J_LOG_EVENT_FACTORY = "Log4jLogEventFactory";

    /**
     * Name of the system property to use to identify the ContextSelector Class.
     */
    // 用于标识 ContextSelector 类的系统属性名称。
    // 这个常量定义了一个系统属性的键，通过它可以配置 Log4j 使用哪个 ContextSelector 来选择日志上下文。
    public static final String LOG4J_CONTEXT_SELECTOR = "Log4jContextSelector";

    /**
     * Property name for the default status (internal log4j logging) level to use if not specified in configuration.
     */
    // 如果配置中未指定，则用于设置默认状态（Log4j 内部日志）级别的属性名称。
    // 这个常量定义了一个系统属性的键，用于控制 Log4j 内部状态日志的默认级别。
    public static final String LOG4J_DEFAULT_STATUS_LEVEL = "Log4jDefaultStatusLevel";

    /**
     * JNDI context name string literal.
     */
    // JNDI 上下文名称字符串字面量。
    // 这个常量定义了 Log4j 在 JNDI 中查找上下文名称时使用的字符串。
    public static final String JNDI_CONTEXT_NAME = "java:comp/env/log4j/context-name";

    /**
     * Control which script languages are allowed, if any.
     */
    // 控制允许哪些脚本语言（如果有）。
    // 这个常量定义了一个系统属性的键，用于限制 Log4j 配置中可以使用的脚本语言。
    public static final String SCRIPT_LANGUAGES = "log4j2.Script.enableLanguages";

    /**
     * Number of milliseconds in a second.
     */
    // 一秒钟的毫秒数。
    // 这是一个常用常量，表示1秒等于1000毫秒。
    public static final int MILLIS_IN_SECONDS = 1000;

    /**
     * Supports user request LOG4J2-898 to have the option to format a message in the background thread.
     */
    // 支持用户请求 LOG4J2-898，即可以选择在后台线程中格式化消息。
    // 这个布尔常量根据系统属性 "log4j.format.msg.async" 的值决定是否在后台线程格式化日志消息。
    // 默认值为 false，表示默认不在后台格式化。
    public static final boolean FORMAT_MESSAGES_IN_BACKGROUND = PropertiesUtil.getProperties().getBooleanProperty(
            "log4j.format.msg.async", false);

    /**
     * LOG4J2-3198 property which used to globally opt out of lookups in pattern layout message text, however
     * this is the default and this property is no longer read.
     *
     * Deprecated in 2.15.
     *
     * @since 2.10
     * @deprecated no longer used, lookups are only used when {@code %m{lookups}} is specified
     */
    // LOG4J2-3198 属性，曾用于全局禁用模式布局消息文本中的查找（lookups），但现在这是默认行为，此属性不再读取。
    // 该属性在 2.15 版本中已弃用。
    // 从 2.10 版本开始引入。
    // 弃用原因：不再使用，查找功能现在只在明确指定 `%m{lookups}` 时才启用。
    @Deprecated
    public static final boolean FORMAT_MESSAGES_PATTERN_DISABLE_LOOKUPS = PropertiesUtil.getProperties().getBooleanProperty(
            "log4j2.formatMsgNoLookups", true);

    /**
     * {@code true} if we think we are running in a web container, based on the boolean value of system property
     * "log4j2.is.webapp", or (if this system property is not set) whether the  {@code javax.servlet.Servlet} class
     * is present in the classpath.
     */
    // 如果根据系统属性 "log4j2.is.webapp" 的布尔值，或者（如果未设置此系统属性）类路径中是否存在 `javax.servlet.Servlet` 类，
    // 我们认为当前运行在 Web 容器中，则此值为 `true`。
    // 该常量用于判断 Log4j 是否在 Web 应用程序环境中运行。
    public static final boolean IS_WEB_APP = org.apache.logging.log4j.util.Constants.IS_WEB_APP;

    /**
     * Kill switch for object pooling in ThreadLocals that enables much of the LOG4J2-1270 no-GC behaviour.
     * <p>
     * {@code True} for non-{@link #IS_WEB_APP web apps}, disable by setting system property
     * "log4j2.enable.threadlocals" to "false".
     *
     * @since 2.6
     */
    // 用于控制 ThreadLocals 中对象池的“终止开关”，该对象池实现了 LOG4J2-1270 中的无 GC 行为。
    // 对于非 Web 应用程序，此值为 `true`。可以通过将系统属性 "log4j2.enable.threadlocals" 设置为 "false" 来禁用。
    // 从 2.6 版本开始引入。
    // 该常量决定是否启用基于 ThreadLocal 的对象池，以减少垃圾回收。
    public static final boolean ENABLE_THREADLOCALS = org.apache.logging.log4j.util.Constants.ENABLE_THREADLOCALS;

    /**
     * Kill switch for garbage-free Layout behaviour that encodes LogEvents directly into
     * {@link org.apache.logging.log4j.core.layout.ByteBufferDestination}s without creating intermediate temporary
     * Objects.
     * <p>
     * {@code True} by default iff all loggers are asynchronous because system property
     * {@code Log4jContextSelector} is set to {@code org.apache.logging.log4j.core.async.AsyncLoggerContextSelector}.
     * Disable by setting system property "log4j2.enable.direct.encoders" to "false".
     *
     * @since 2.6
     */
    // 用于控制无垃圾回收布局行为的“终止开关”，该行为将 LogEvents 直接编码到 ByteBufferDestination 中，而不创建中间临时对象。
    // 默认情况下，如果所有 Logger 都是异步的（因为系统属性 `Log4jContextSelector` 设置为
    // `org.apache.logging.log4j.core.async.AsyncLoggerContextSelector`），则此值为 `true`。
    // 可以通过将系统属性 "log4j2.enable.direct.encoders" 设置为 "false" 来禁用。
    // 从 2.6 版本开始引入。
    // 这个常量控制是否启用直接编码器，以实现无 GC 的日志事件编码，提高性能。
    public static final boolean ENABLE_DIRECT_ENCODERS = PropertiesUtil.getProperties().getBooleanProperty(
            "log4j2.enable.direct.encoders", true); // enable GC-free text encoding by default
    // 默认启用无 GC 的文本编码。
            // the alternative is to enable GC-free encoding only by default only when using all-async loggers:
    // 另一种选择是仅在使用所有异步日志器时默认启用无 GC 编码：
            //AsyncLoggerContextSelector.class.getName().equals(PropertiesUtil.getProperties().getStringProperty(LOG4J_CONTEXT_SELECTOR)));

    /**
     * Initial StringBuilder size used in RingBuffer LogEvents to store the contents of reusable Messages.
     * <p>
     * The default value is {@value}, users can override with system property "log4j.initialReusableMsgSize".
     * </p>
     * @since 2.6
     */
    // RingBuffer LogEvents 中用于存储可重用消息内容的 StringBuilder 的初始大小。
    // 默认值为 128。用户可以通过系统属性 "log4j.initialReusableMsgSize" 进行覆盖。
    // 从 2.6 版本开始引入。
    // 该常量定义了用于日志消息构建的 StringBuilder 的起始容量，有助于优化内存使用。
    public static final int INITIAL_REUSABLE_MESSAGE_SIZE = size("log4j.initialReusableMsgSize", 128);

    /**
     * Maximum size of the StringBuilders used in RingBuffer LogEvents to store the contents of reusable Messages.
     * After a large message has been delivered to the appenders, the StringBuilder is trimmed to this size.
     * <p>
     * The default value is {@value}, which allows the StringBuilder to resize three times from its initial size.
     * Users can override with system property "log4j.maxReusableMsgSize".
     * </p>
     * @since 2.6
     */
    // RingBuffer LogEvents 中用于存储可重用消息内容的 StringBuilder 的最大大小。
    // 在将一个大型消息传递给 Appender 后，StringBuilder 会被修剪到这个大小。
    // 默认值为 (128 * 2 + 2) * 2 + 2，这允许 StringBuilder 在初始大小的基础上进行三次扩容。
    // 用户可以通过系统属性 "log4j.maxReusableMsgSize" 进行覆盖。
    // 从 2.6 版本开始引入。
    // 该常量定义了可重用 StringBuilder 的最大容量，用于防止因过大消息导致内存占用过多，并在使用后回收内存。
    public static final int MAX_REUSABLE_MESSAGE_SIZE = size("log4j.maxReusableMsgSize", (128 * 2 + 2) * 2 + 2);

    /**
     * Size of CharBuffers used by text encoders.
     * <p>
     * The default value is {@value}, users can override with system property "log4j.encoder.charBufferSize".
     * </p>
     * @since 2.6
     */
    // 文本编码器使用的 CharBuffer 的大小。
    // 默认值为 2048。用户可以通过系统属性 "log4j.encoder.charBufferSize" 进行覆盖。
    // 从 2.6 版本开始引入。
    // 该常量定义了用于字符编码的缓冲区大小。
    public static final int ENCODER_CHAR_BUFFER_SIZE = size("log4j.encoder.charBufferSize", 2048);

    /**
     * Default size of ByteBuffers used to encode LogEvents without allocating temporary objects.
     * <p>
     * The default value is {@value}, users can override with system property "log4j.encoder.byteBufferSize".
     * </p>
     * @see org.apache.logging.log4j.core.layout.ByteBufferDestination
     * @since 2.6
     */
    // 用于编码 LogEvents 而不分配临时对象的 ByteBuffer 的默认大小。
    // 默认值为 8 * 1024 (8KB)。用户可以通过系统属性 "log4j.encoder.byteBufferSize" 进行覆盖。
    // 参考：org.apache.logging.log4j.core.layout.ByteBufferDestination
    // 从 2.6 版本开始引入。
    // 该常量定义了用于字节编码的缓冲区大小，对于无 GC 的日志写入至关重要。
    public static final int ENCODER_BYTE_BUFFER_SIZE = size("log4j.encoder.byteBufferSize", 8 * 1024);

    /**
     * Helper method to retrieve an integer property from system properties or return a default value.
     * @param property The name of the system property.
     * @param defaultValue The default value to return if the property is not found or cannot be parsed.
     * @return The integer value of the property, or the default value.
     */
    // 辅助方法，用于从系统属性中检索整数属性或返回默认值。
    // 参数：
    // - property: 系统属性的名称。
    // - defaultValue: 如果未找到属性或无法解析，则返回的默认值。
    // 返回值：属性的整数值，或者默认值。
    // 此方法统一了从 PropertiesUtil 获取整数类型配置的逻辑。
    private static int size(final String property, final int defaultValue) {
        return PropertiesUtil.getProperties().getIntegerProperty(property, defaultValue);
    }

    /**
     * Prevent class instantiation.
     */
    // 防止类实例化。
    // 私有构造函数，确保该工具类不能被实例化，所有成员都是静态的。
    private Constants() {
    }
}
