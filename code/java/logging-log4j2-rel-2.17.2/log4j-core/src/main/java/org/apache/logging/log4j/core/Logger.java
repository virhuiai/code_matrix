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
package org.apache.logging.log4j.core;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LocationAwareReliabilityStrategy;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.ReliabilityStrategy;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;

/**
 * The core implementation of the {@link org.apache.logging.log4j.Logger} interface. Besides providing an implementation
 * of all the Logger methods, this class also provides some convenience methods for Log4j 1.x compatibility as well as
 * access to the {@link org.apache.logging.log4j.core.Filter Filters} and {@link org.apache.logging.log4j.core.Appender
 * Appenders} associated with this Logger. Note that access to these underlying objects is provided primarily for use in
 * unit tests or bridging legacy Log4j 1.x code. Future versions of this class may or may not include the various
 * methods that are noted as not being part of the public API.
 * 这是 {@link org.apache.logging.log4j.Logger} 接口的核心实现。除了提供所有 Logger 方法的实现之外，
 * 该类还提供了一些方便的方法，用于 Log4j 1.x 的兼容性，以及访问与此 Logger 关联的 {@link org.apache.logging.log4j.core.Filter 过滤器}
 * 和 {@link org.apache.logging.log4j.core.Appender Appender}。
 * 请注意，对这些底层对象的访问主要用于单元测试或桥接遗留的 Log4j 1.x 代码。
 * 该类的未来版本可能包含或不包含那些未被视为公共 API 的各种方法。
 *
 * TODO All the isEnabled methods could be pushed into a filter interface. Not sure of the utility of having isEnabled
 * be able to examine the message pattern and parameters. (RG) Moving the isEnabled methods out of Logger noticeably
 * impacts performance. The message pattern and parameters are required so that they can be used in global filters.
 * TODO 所有的 isEnabled 方法都可以推到过滤器接口中。不确定让 isEnabled 方法能够检查消息模式和参数的实用性。(RG)
 * 将 isEnabled 方法移出 Logger 会显著影响性能。消息模式和参数是必需的，以便它们可以在全局过滤器中使用。
 */
public class Logger extends AbstractLogger implements Supplier<LoggerConfig> {

    private static final long serialVersionUID = 1L;
    // 类的序列化版本 UID，用于兼容性检查。

    /**
     * Config should be consistent across threads.
     * 配置在多线程之间应该保持一致。
     */
    protected volatile PrivateConfig privateConfig;
    // 私有配置对象，使用 volatile 关键字确保多线程间的可见性，保证配置更新的及时性。

    // FIXME: ditto to the above
    // FIXME: 同上（指 privateConfig 的注释）
    private final LoggerContext context;
    // 此 Logger 所属的 LoggerContext。LoggerContext 管理着 Logger 的配置和生命周期。

    /**
     * The constructor.
     * 构造函数。
     *
     * @param context The LoggerContext this Logger is associated with.
     * @param context 此 Logger 关联的 LoggerContext。
     * @param messageFactory The message factory.
     * @param messageFactory 消息工厂，用于创建消息对象。
     * @param name The name of the Logger.
     * @param name Logger 的名称。
     */
    protected Logger(final LoggerContext context, final String name, final MessageFactory messageFactory) {
        super(name, messageFactory); // 调用父类 AbstractLogger 的构造函数。
        this.context = context; // 初始化 LoggerContext。
        privateConfig = new PrivateConfig(context.getConfiguration(), this); // 初始化私有配置对象。
    }

    protected Object writeReplace() throws ObjectStreamException {
        // 当对象被序列化时，此方法会被调用，允许返回一个替代对象进行序列化。
        // 目的是为了在序列化 Logger 对象时，只保留必要的信息（名称和消息工厂），
        // 避免序列化整个 LoggerContext 和配置，从而节省资源并简化反序列化过程。
        return new LoggerProxy(getName(), getMessageFactory());
    }

    /**
     * This method is only used for 1.x compatibility. Returns the parent of this Logger. If it doesn't already exist
     * return a temporary Logger.
     * 此方法仅用于 Log4j 1.x 的兼容性。返回此 Logger 的父级 Logger。如果父级 Logger 不存在，则返回一个临时 Logger。
     *
     * @return The parent Logger.
     * @return 父级 Logger 对象。
     */
    public Logger getParent() {
        // 获取当前 LoggerConfig，如果当前 LoggerConfig 的名称与 Logger 的名称相同，则获取其父 LoggerConfig，否则使用当前 LoggerConfig。
        final LoggerConfig lc = privateConfig.loggerConfig.getName().equals(getName()) ? privateConfig.loggerConfig
                .getParent() : privateConfig.loggerConfig;
        if (lc == null) {
            // 如果 LoggerConfig 为空，表示没有父级 Logger，返回 null。
            return null;
        }
        final String lcName = lc.getName(); // 获取父 LoggerConfig 的名称。
        final MessageFactory messageFactory = getMessageFactory(); // 获取当前 Logger 的消息工厂。
        if (context.hasLogger(lcName, messageFactory)) {
            // 如果 LoggerContext 中已经存在该名称和消息工厂的 Logger，则直接获取并返回。
            return context.getLogger(lcName, messageFactory);
        }
        // 如果不存在，则创建一个新的 Logger 作为父级 Logger 并返回。
        return new Logger(context, lcName, messageFactory);
    }

    /**
     * Returns the LoggerContext this Logger is associated with.
     * 返回此 Logger 关联的 LoggerContext。
     *
     * @return the LoggerContext.
     * @return 此 Logger 关联的 LoggerContext 对象。
     */
    public LoggerContext getContext() {
        return context;
    }

    /**
     * This method is not exposed through the public API and is provided primarily for unit testing.
     * 此方法未通过公共 API 公开，主要用于单元测试。
     * <p>
     * If the new level is null, this logger inherits the level from its parent.
     * 如果新的级别为 null，则此 Logger 将从其父级继承级别。
     * </p>
     *
     * @param level The Level to use on this Logger, may be null.
     * @param level 要在此 Logger 上使用的日志级别，可以为 null。
     */
    public synchronized void setLevel(final Level level) {
        // 如果新设置的级别与当前级别相同，则直接返回，避免不必要的更新。
        if (level == getLevel()) {
            return;
        }
        Level actualLevel; // 实际要设置的日志级别。
        if (level != null) {
            // 如果传入的级别不为 null，则使用传入的级别。
            actualLevel = level;
        } else {
            // 如果传入的级别为 null，则从父级 Logger 继承级别。
            final Logger parent = getParent(); // 获取父级 Logger。
            actualLevel = parent != null ? parent.getLevel() : privateConfig.loggerConfigLevel; // 如果有父级，则使用父级的级别；否则使用当前 LoggerConfig 的级别。
        }
        // 使用新的实际级别创建一个新的 PrivateConfig 对象，并更新 privateConfig 字段。
        // 这将触发配置的更新，确保线程安全地应用新的日志级别。
        privateConfig = new PrivateConfig(privateConfig, actualLevel);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.util.Supplier#get()
     */
    @Override
    public LoggerConfig get() {
        // 实现 Supplier 接口的 get 方法，返回当前的 LoggerConfig 对象。
        return privateConfig.loggerConfig;
    }

    @Override
    protected boolean requiresLocation() {
        // 判断当前 Logger 配置是否需要位置信息（如文件名、行号等）。
        return privateConfig.requiresLocation;
    }

    @Override
    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message message,
            final Throwable t) {
        // 核心的日志消息记录方法。
        // 参数：
        // fqcn：调用方的完全限定类名。
        // level：日志级别。
        // marker：日志标记。
        // message：日志消息。
        // t：与日志事件关联的 Throwable 对象（异常）。
        final Message msg = message == null ? new SimpleMessage(Strings.EMPTY) : message; // 如果消息为空，则创建一个空的 SimpleMessage。
        final ReliabilityStrategy strategy = privateConfig.loggerConfig.getReliabilityStrategy(); // 获取当前 LoggerConfig 的可靠性策略。
        strategy.log(this, getName(), fqcn, marker, level, msg, t); // 使用可靠性策略记录日志事件。
    }

    @Override
    protected void log(final Level level, final Marker marker, final String fqcn, final StackTraceElement location,
        final Message message, final Throwable throwable) {
        // 记录日志的核心方法，包含位置信息。
        // 参数：
        // level：日志级别。
        // marker：日志标记。
        // fqcn：调用方的完全限定类名。
        // location：StackTraceElement 对象，包含调用方的位置信息。
        // message：日志消息。
        // throwable：与日志事件关联的 Throwable 对象（异常）。
        final ReliabilityStrategy strategy = privateConfig.loggerConfig.getReliabilityStrategy(); // 获取当前 LoggerConfig 的可靠性策略。
        if (strategy instanceof LocationAwareReliabilityStrategy) {
            // 如果可靠性策略是 LocationAwareReliabilityStrategy 的实例，则调用其带有位置信息的 log 方法。
            ((LocationAwareReliabilityStrategy) strategy).log(this, getName(), fqcn, location, marker, level,
                message, throwable);
        } else {
            // 否则，调用不带位置信息的 log 方法。
            strategy.log(this, getName(), fqcn, marker, level, message, throwable);
        }
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Throwable t) {
        // 判断给定日志级别、标记、消息和异常的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, t);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message) {
        // 判断给定日志级别、标记和消息的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object... params) {
        // 判断给定日志级别、标记、消息和可变参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, params);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
        // 判断给定日志级别、标记、消息和一个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1) {
        // 判断给定日志级别、标记、消息和两个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2) {
        // 判断给定日志级别、标记、消息和三个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3) {
        // 判断给定日志级别、标记、消息和四个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2, p3);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4) {
        // 判断给定日志级别、标记、消息和五个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2, p3, p4);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        // 判断给定日志级别、标记、消息和六个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        // 判断给定日志级别、标记、消息和七个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7) {
        // 判断给定日志级别、标记、消息和八个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {
        // 判断给定日志级别、标记、消息和九个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {
        // 判断给定日志级别、标记、消息和十个参数的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final CharSequence message, final Throwable t) {
        // 判断给定日志级别、标记、CharSequence 类型消息和异常的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, t);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Object message, final Throwable t) {
        // 判断给定日志级别、标记、Object 类型消息和异常的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, t);
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable t) {
        // 判断给定日志级别、标记、Message 类型消息和异常的日志事件是否会被记录。
        // 内部通过调用 privateConfig 的 filter 方法进行过滤判断。
        return privateConfig.filter(level, marker, message, t);
    }

    /**
     * This method is not exposed through the public API and is used primarily for unit testing.
     * 此方法未通过公共 API 公开，主要用于单元测试。
     *
     * @param appender The Appender to add to the Logger.
     * @param appender 要添加到 Logger 的 Appender。
     */
    public void addAppender(final Appender appender) {
        // 将指定的 Appender 添加到 Logger 配置中。
        privateConfig.config.addLoggerAppender(this, appender);
    }

    /**
     * This method is not exposed through the public API and is used primarily for unit testing.
     * 此方法未通过公共 API 公开，主要用于单元测试。
     *
     * @param appender The Appender to remove from the Logger.
     * @param appender 要从 Logger 中移除的 Appender。
     */
    public void removeAppender(final Appender appender) {
        // 从 Logger 配置中移除指定名称的 Appender。
        privateConfig.loggerConfig.removeAppender(appender.getName());
    }

    /**
     * This method is not exposed through the public API and is used primarily for unit testing.
     * 此方法未通过公共 API 公开，主要用于单元测试。
     *
     * @return A Map containing the Appender's name as the key and the Appender as the value.
     * @return 包含 Appender 名称作为键，Appender 对象作为值的 Map。
     */
    public Map<String, Appender> getAppenders() {
        // 返回与此 Logger 关联的所有 Appender 的 Map。
        return privateConfig.loggerConfig.getAppenders();
    }

    /**
     * This method is not exposed through the public API and is used primarily for unit testing.
     * 此方法未通过公共 API 公开，主要用于单元测试。
     *
     * @return An Iterator over all the Filters associated with the Logger.
     * @return 与 Logger 关联的所有 Filter 的迭代器。
     */
    // FIXME: this really ought to be an Iterable instead of an Iterator
    // FIXME: 这里应该返回一个 Iterable 而不是 Iterator。
    public Iterator<Filter> getFilters() {
        final Filter filter = privateConfig.loggerConfig.getFilter(); // 获取与 LoggerConfig 关联的过滤器。
        if (filter == null) {
            // 如果没有过滤器，则返回一个空的过滤器列表的迭代器。
            return new ArrayList<Filter>().iterator();
        } else if (filter instanceof CompositeFilter) {
            // 如果过滤器是 CompositeFilter（复合过滤器），则返回其内部过滤器的迭代器。
            return ((CompositeFilter) filter).iterator();
        } else {
            // 否则，将单个过滤器放入列表中并返回其迭代器。
            final List<Filter> filters = new ArrayList<>();
            filters.add(filter);
            return filters.iterator();
        }
    }

    /**
     * Gets the Level associated with the Logger.
     * 获取与 Logger 关联的日志级别。
     *
     * @return the Level associate with the Logger.
     * @return 与 Logger 关联的日志级别。
     */
    @Override
    public Level getLevel() {
        // 返回当前 Logger 的日志级别。
        return privateConfig.loggerConfigLevel;
    }

    /**
     * This method is not exposed through the public API and is used primarily for unit testing.
     * 此方法未通过公共 API 公开，主要用于单元测试。
     *
     * @return The number of Filters associated with the Logger.
     * @return 与 Logger 关联的过滤器数量。
     */
    public int filterCount() {
        final Filter filter = privateConfig.loggerConfig.getFilter(); // 获取与 LoggerConfig 关联的过滤器。
        if (filter == null) {
            // 如果没有过滤器，则返回 0。
            return 0;
        } else if (filter instanceof CompositeFilter) {
            // 如果过滤器是 CompositeFilter，则返回其内部过滤器的数量。
            return ((CompositeFilter) filter).size();
        }
        // 否则，表示只有一个过滤器，返回 1。
        return 1;
    }

    /**
     * This method is not exposed through the public API and is used primarily for unit testing.
     * 此方法未通过公共 API 公开，主要用于单元测试。
     *
     * @param filter The Filter to add.
     * @param filter 要添加的过滤器。
     */
    public void addFilter(final Filter filter) {
        // 将指定的过滤器添加到 Logger 的配置中。
        privateConfig.config.addLoggerFilter(this, filter);
    }

    /**
     * This method is not exposed through the public API and is present only to support the Log4j 1.2 compatibility
     * bridge.
     * 此方法未通过公共 API 公开，仅用于支持 Log4j 1.2 兼容性桥接。
     *
     * @return true if the associated LoggerConfig is additive, false otherwise.
     * @return 如果关联的 LoggerConfig 是 additive（叠加的），则返回 true；否则返回 false。
     */
    public boolean isAdditive() {
        // 判断 LoggerConfig 是否是 additive（叠加的）。
        // 当 LoggerConfig 是 additive 时，它的日志事件会继续传递给其父 Logger。
        return privateConfig.loggerConfig.isAdditive();
    }

    /**
     * This method is not exposed through the public API and is present only to support the Log4j 1.2 compatibility
     * bridge.
     * 此方法未通过公共 API 公开，仅用于支持 Log4j 1.2 兼容性桥接。
     *
     * @param additive Boolean value to indicate whether the Logger is additive or not.
     * @param additive 布尔值，指示 Logger 是否是 additive（叠加的）。
     */
    public void setAdditive(final boolean additive) {
        // 设置 LoggerConfig 的 additive 属性。
        privateConfig.config.setLoggerAdditive(this, additive);
    }

    /**
     * Associates this Logger with a new Configuration. This method is not
     * exposed through the public API.
     * 将此 Logger 与新的 Configuration 关联。此方法未通过公共 API 公开。
     * <p>
     * There are two ways this could be used to guarantee all threads are aware
     * of changes to config.
     * 有两种方法可以确保所有线程都感知到配置更改。
     * <ol>
     * <li>Synchronize this method. Accessors don't need to be synchronized as
     * Java will treat all variables within a synchronized block as volatile.
     * </li>
     * <li>Synchronize 此方法。访问器不需要同步，因为 Java 会将同步块中的所有变量视为 volatile。
     * </li>
     * <li>Declare the variable volatile. Option 2 is used here as the
     * performance cost is very low and it does a better job at documenting how
     * it is used.</li>
     * <li>声明变量为 volatile。这里使用选项 2，因为性能开销非常低，并且能更好地说明其用法。</li>
     *
     * @param newConfig
     *            The new Configuration.
     * @param newConfig 新的 Configuration 对象。
     */
    protected void updateConfiguration(final Configuration newConfig) {
        // 使用新的 Configuration 对象创建一个新的 PrivateConfig，并更新 privateConfig 字段。
        // 这实现了原子性的配置切换，确保在多线程环境下配置更新的可见性。
        this.privateConfig = new PrivateConfig(newConfig, this);
    }

    /**
     * The binding between a Logger and its configuration.
     * Logger 及其配置之间的绑定。
     */
    protected class PrivateConfig {
        // config fields are public to make them visible to Logger subclasses
        // config 字段是 public 的，以便它们对 Logger 子类可见。
        /** LoggerConfig to delegate the actual logging to. */
        // 用于委托实际日志记录的 LoggerConfig。
        public final LoggerConfig loggerConfig; // SUPPRESS CHECKSTYLE
        /** The current Configuration associated with the LoggerConfig. */
        // 与 LoggerConfig 关联的当前 Configuration。
        public final Configuration config; // SUPPRESS CHECKSTYLE
        private final Level loggerConfigLevel; // LoggerConfig 的日志级别。
        private final int intLevel; // 日志级别的整数值表示。
        private final Logger logger; // 对外部 Logger 实例的引用。
        private final boolean requiresLocation; // 是否需要位置信息。

        /**
         * PrivateConfig 构造函数。
         *
         * @param config 对应的 Configuration 对象。
         * @param logger 对应的 Logger 对象。
         */
        public PrivateConfig(final Configuration config, final Logger logger) {
            this.config = config; // 初始化 Configuration。
            this.loggerConfig = config.getLoggerConfig(getName()); // 从 Configuration 中获取对应名称的 LoggerConfig。
            this.loggerConfigLevel = this.loggerConfig.getLevel(); // 获取 LoggerConfig 的日志级别。
            this.intLevel = this.loggerConfigLevel.intLevel(); // 获取日志级别的整数值。
            this.logger = logger; // 初始化 Logger。
            this.requiresLocation = this.loggerConfig.requiresLocation(); // 判断是否需要位置信息。
        }

        /**
         * PrivateConfig 构造函数，用于更新日志级别。
         *
         * @param pc 之前的 PrivateConfig 实例。
         * @param level 新的日志级别。
         */
        public PrivateConfig(final PrivateConfig pc, final Level level) {
            this.config = pc.config; // 继承之前的 Configuration。
            this.loggerConfig = pc.loggerConfig; // 继承之前的 LoggerConfig。
            this.loggerConfigLevel = level; // 使用新的日志级别。
            this.intLevel = this.loggerConfigLevel.intLevel(); // 获取新日志级别的整数值。
            this.logger = pc.logger; // 继承之前的 Logger。
            this.requiresLocation = this.loggerConfig.requiresLocation(); // 继承之前的 requiresLocation。
        }

        /**
         * PrivateConfig 构造函数，用于更新 LoggerConfig。
         *
         * @param pc 之前的 PrivateConfig 实例。
         * @param lc 新的 LoggerConfig 实例。
         */
        public PrivateConfig(final PrivateConfig pc, final LoggerConfig lc) {
            this.config = pc.config; // 继承之前的 Configuration。
            this.loggerConfig = lc; // 使用新的 LoggerConfig。
            this.loggerConfigLevel = lc.getLevel(); // 获取新 LoggerConfig 的日志级别。
            this.intLevel = this.loggerConfigLevel.intLevel(); // 获取新日志级别的整数值。
            this.logger = pc.logger; // 继承之前的 Logger。
            this.requiresLocation = this.loggerConfig.requiresLocation(); // 继承之前的 requiresLocation。
        }

        // LOG4J2-151: changed visibility to public
        // LOG4J2-151: 将可见性改为 public
        public void logEvent(final LogEvent event) {
            // 委托 LoggerConfig 记录日志事件。
            loggerConfig.log(event);
        }

        /**
         * 过滤日志事件。
         *
         * @param level 日志级别。
         * @param marker 日志标记。
         * @param msg 消息字符串。
         * @return 如果日志事件应被接受（记录），则返回 true；否则返回 false。
         */
        boolean filter(final Level level, final Marker marker, final String msg) {
            final Filter filter = config.getFilter(); // 获取全局过滤器。
            if (filter != null) {
                // 如果存在全局过滤器，则对其进行过滤。
                final Filter.Result r = filter.filter(logger, level, marker, msg);
                if (r != Filter.Result.NEUTRAL) {
                    // 如果过滤结果不是 NEUTRAL（中立），则直接返回 ACCEPT 或 DENY 的结果。
                    return r == Filter.Result.ACCEPT;
                }
            }
            // 如果没有全局过滤器或过滤器结果为 NEUTRAL，则根据 LoggerConfig 的级别进行判断。
            // 只有当传入的级别不为 null 且 LoggerConfig 的级别大于或等于传入级别时，才接受。
            return level != null && intLevel >= level.intLevel();
        }

        /**
         * 过滤日志事件，包含 Throwable。
         *
         * @param level 日志级别。
         * @param marker 日志标记。
         * @param msg 消息字符串。
         * @param t Throwable 对象。
         * @return 如果日志事件应被接受（记录），则返回 true；否则返回 false。
         */
        boolean filter(final Level level, final Marker marker, final String msg, final Throwable t) {
            final Filter filter = config.getFilter(); // 获取全局过滤器。
            if (filter != null) {
                // 如果存在全局过滤器，则对其进行过滤。
                final Filter.Result r = filter.filter(logger, level, marker, (Object) msg, t);
                if (r != Filter.Result.NEUTRAL) {
                    // 如果过滤结果不是 NEUTRAL，则直接返回 ACCEPT 或 DENY 的结果。
                    return r == Filter.Result.ACCEPT;
                }
            }
            // 否则，根据 LoggerConfig 的级别进行判断。
            return level != null && intLevel >= level.intLevel();
        }

        /**
         * 过滤日志事件，包含可变参数。
         *
         * @param level 日志级别。
         * @param marker 日志标记。
         * @param msg 消息字符串。
         * @param p1 可变参数。
         * @return 如果日志事件应被接受（记录），则返回 true；否则返回 false。
         */
        boolean filter(final Level level, final Marker marker, final String msg, final Object... p1) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p1);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2, final Object p3) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2, p3);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2, final Object p3,
                final Object p4) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2, p3, p4);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2, final Object p3,
                final Object p4, final Object p5) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2, p3, p4, p5);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2, final Object p3,
                final Object p4, final Object p5, final Object p6) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2, p3, p4, p5, p6);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2, final Object p3,
                final Object p4, final Object p5, final Object p6,
                final Object p7) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2, p3, p4, p5, p6, p7);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2, final Object p3,
                final Object p4, final Object p5, final Object p6,
                final Object p7, final Object p8) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        boolean filter(final Level level, final Marker marker, final String msg, final Object p0,
                final Object p1, final Object p2, final Object p3,
                final Object p4, final Object p5, final Object p6,
                final Object p7, final Object p8, final Object p9) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, p0, p1, p2, p3, p4, p5, p6, p7, p8,
                        p9);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        /**
         * 过滤日志事件，包含 CharSequence 类型消息和 Throwable。
         *
         * @param level 日志级别。
         * @param marker 日志标记。
         * @param msg CharSequence 类型消息。
         * @param t Throwable 对象。
         * @return 如果日志事件应被接受（记录），则返回 true；否则返回 false。
         */
        boolean filter(final Level level, final Marker marker, final CharSequence msg, final Throwable t) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, t);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        /**
         * 过滤日志事件，包含 Object 类型消息和 Throwable。
         *
         * @param level 日志级别。
         * @param marker 日志标记。
         * @param msg Object 类型消息。
         * @param t Throwable 对象。
         * @return 如果日志事件应被接受（记录），则返回 true；否则返回 false。
         */
        boolean filter(final Level level, final Marker marker, final Object msg, final Throwable t) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, t);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        /**
         * 过滤日志事件，包含 Message 类型消息和 Throwable。
         *
         * @param level 日志级别。
         * @param marker 日志标记。
         * @param msg Message 类型消息。
         * @param t Throwable 对象。
         * @return 如果日志事件应被接受（记录），则返回 true；否则返回 false。
         */
        boolean filter(final Level level, final Marker marker, final Message msg, final Throwable t) {
            final Filter filter = config.getFilter();
            if (filter != null) {
                final Filter.Result r = filter.filter(logger, level, marker, msg, t);
                if (r != Filter.Result.NEUTRAL) {
                    return r == Filter.Result.ACCEPT;
                }
            }
            return level != null && intLevel >= level.intLevel();
        }

        @Override
        public String toString() {
            // 返回 PrivateConfig 对象的字符串表示，包含其内部字段的值，用于调试和日志记录。
            final StringBuilder builder = new StringBuilder();
            builder.append("PrivateConfig [loggerConfig=");
            builder.append(loggerConfig);
            builder.append(", config=");
            builder.append(config);
            builder.append(", loggerConfigLevel=");
            builder.append(loggerConfigLevel);
            builder.append(", intLevel=");
            builder.append(intLevel);
            builder.append(", logger=");
            builder.append(logger);
            builder.append("]");
            return builder.toString();
        }
    }

    /**
     * Serialization proxy class for Logger. Since the LoggerContext and config information can be reconstructed on the
     * fly, the only information needed for a Logger are what's available in AbstractLogger.
     * Logger 的序列化代理类。由于 LoggerContext 和配置信息可以动态重建，因此 Logger 所需的唯一信息是 AbstractLogger 中可用的信息。
     *
     * @since 2.5
     */
    protected static class LoggerProxy implements Serializable {
        private static final long serialVersionUID = 1L;
        // 类的序列化版本 UID。

        private final String name; // Logger 的名称。
        private final MessageFactory messageFactory; // 消息工厂。

        /**
         * LoggerProxy 构造函数。
         *
         * @param name Logger 的名称。
         * @param messageFactory 消息工厂。
         */
        public LoggerProxy(final String name, final MessageFactory messageFactory) {
            this.name = name; // 初始化 Logger 名称。
            this.messageFactory = messageFactory; // 初始化消息工厂。
        }

        protected Object readResolve() throws ObjectStreamException {
            // 当对象被反序列化时，此方法会被调用，允许返回一个替代对象。
            // 目的是在反序列化时，能够正确地从当前的 LoggerContext 获取或创建一个 Logger 实例，
            // 而不是直接反序列化一个可能已过时或不完整的 Logger 状态。
            return new Logger(LoggerContext.getContext(), name, messageFactory);
        }
    }

    /**
     * Returns a String representation of this instance in the form {@code "name:level[ in context_name]"}.
     * 返回此实例的字符串表示形式，格式为 {@code "name:level[ in context_name]" }。
     *
     * @return A String describing this Logger instance.
     * @return 描述此 Logger 实例的字符串。
     */
    @Override
    public String toString() {
        final String nameLevel = Strings.EMPTY + getName() + ':' + getLevel(); // 构建“名称:级别”部分。
        if (context == null) {
            // 如果 LoggerContext 为空，则只返回“名称:级别”部分。
            return nameLevel;
        }
        final String contextName = context.getName(); // 获取 LoggerContext 的名称。
        // 如果 LoggerContext 名称为空，则只返回“名称:级别”部分；否则，添加“ in context_name”部分。
        return contextName == null ? nameLevel : nameLevel + " in " + contextName;
    }

    @Override
    public boolean equals(final Object o) {
        // 判断当前 Logger 对象是否与另一个对象相等。
        // 相等条件：是同一个对象，或都是 Logger 类型且名称相同。
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Logger that = (Logger) o; // 类型转换。
        return getName().equals(that.getName()); // 比较 Logger 名称是否相等。
    }

    @Override
    public int hashCode() {
        // 计算 Logger 对象的哈希码，基于其名称。
        return getName().hashCode();
    }
}
