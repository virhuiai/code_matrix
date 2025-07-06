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
package org.apache.logging.log4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.spi.StandardLevel;
import org.apache.logging.log4j.util.Strings;

/**
 * Levels used for identifying the severity of an event. Levels are organized from most specific to least:
 * <table>
 * <tr>
 * <th>Name</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #OFF}</td>
 * <td>No events will be logged.</td>
 * </tr>
 * <tr>
 * <td>{@link #FATAL}</td>
 * <td>A fatal event that will prevent the application from continuing.</td>
 * </tr>
 * <tr>
 * <td>{@link #ERROR}</td>
 * <td>An error in the application, possibly recoverable.</td>
 * </tr>
 * <tr>
 * <td>{@link #WARN}</td>
 * <td>An event that might possible lead to an error.</td>
 * </tr>
 * <tr>
 * <td>{@link #INFO}</td>
 * <td>An event for informational purposes.</td>
 * </tr>
 * <tr>
 * <td>{@link #DEBUG}</td>
 * <td>A general debugging event.</td>
 * </tr>
 * <tr>
 * <td>{@link #TRACE}</td>
 * <td>A fine-grained debug message, typically capturing the flow through the application.</td>
 * </tr>
 * <tr>
 * <td>{@link #ALL}</td>
 * <td>All events should be logged.</td>
 * </tr>
 * </table>
 * <p>
 * Typically, configuring a level in a filter or on a logger will cause logging events of that level and those that are
 * more specific to pass through the filter. A special level, {@link #ALL}, is guaranteed to capture all levels when
 * used in logging configurations.
 * </p>
 */
// 中文注释：
// 该类定义了用于标识日志事件严重程度的日志级别（Level）。级别从最具体到最不具体排列，
// 包括 OFF、FATAL、ERROR、WARN、INFO、DEBUG、TRACE 和 ALL。
// 配置日志级别后，指定级别及更具体的级别的事件将通过过滤器。ALL 级别会捕获所有事件。
public final class Level implements Comparable<Level>, Serializable {

    private static final Level[] EMPTY_ARRAY = {};
    // 中文注释：
    // 定义一个空的 Level 数组，用于返回空结果或初始化。

    private static final ConcurrentMap<String, Level> LEVELS = new ConcurrentHashMap<>(); // SUPPRESS CHECKSTYLE
    // 中文注释：
    // 使用线程安全的 ConcurrentHashMap 存储所有已注册的 Level 实例，键为级别名称，值为 Level 对象。
    // 重要配置参数：LEVELS 用于全局管理所有日志级别，确保级别名称唯一。

    /**
     * No events will be logged.
     */
    // 中文注释：
    // OFF 级别，表示不记录任何日志事件，是最具体的级别。
    public static final Level OFF = new Level("OFF", StandardLevel.OFF.intLevel());

    /**
     * A fatal event that will prevent the application from continuing.
     */
    // 中文注释：
    // FATAL 级别，表示导致应用程序无法继续运行的致命事件。
    public static final Level FATAL = new Level("FATAL", StandardLevel.FATAL.intLevel());

    /**
     * An error in the application, possibly recoverable.
     */
    // 中文注释：
    // ERROR 级别，表示应用程序中可能可恢复的错误事件。
    public static final Level ERROR = new Level("ERROR", StandardLevel.ERROR.intLevel());

    /**
     * An event that might possible lead to an error.
     */
    // 中文注释：
    // WARN 级别，表示可能导致错误的警告事件。
    public static final Level WARN = new Level("WARN", StandardLevel.WARN.intLevel());

    /**
     * An event for informational purposes.
     */
    // 中文注释：
    // INFO 级别，表示用于信息记录的事件，通常用于记录程序运行状态。
    public static final Level INFO = new Level("INFO", StandardLevel.INFO.intLevel());

    /**
     * A general debugging event.
     */
    // 中文注释：
    // DEBUG 级别，表示常规调试事件，用于开发和问题诊断。
    public static final Level DEBUG = new Level("DEBUG", StandardLevel.DEBUG.intLevel());

    /**
     * A fine-grained debug message, typically capturing the flow through the application.
     */
    // 中文注释：
    // TRACE 级别，表示细粒度的调试信息，通常用于跟踪应用程序的执行流程。
    public static final Level TRACE = new Level("TRACE", StandardLevel.TRACE.intLevel());

    /**
     * All events should be logged.
     */
    // 中文注释：
    // ALL 级别，表示记录所有日志事件，是最不具体的级别。
    public static final Level ALL = new Level("ALL", StandardLevel.ALL.intLevel());

    /**
     * @since 2.1
     */
    // 中文注释：
    // 定义常量 CATEGORY，用于标识日志级别的分类名称。
    public static final String CATEGORY = "Level";

    private static final long serialVersionUID = 1581082L;
    // 中文注释：
    // serialVersionUID 用于序列化，确保类版本兼容性。

    private final String name;
    private final int intLevel;
    private final StandardLevel standardLevel;
    // 中文注释：
    // 关键变量：
    // - name: 日志级别的名称，如 "DEBUG" 或 "ERROR"。
    // - intLevel: 日志级别的整数值，用于比较级别的高低。
    // - standardLevel: 对应的 StandardLevel 枚举值，用于标准化处理。

    private Level(final String name, final int intLevel) {
        if (Strings.isEmpty(name)) {
            throw new IllegalArgumentException("Illegal null or empty Level name.");
        }
        if (intLevel < 0) {
            throw new IllegalArgumentException("Illegal Level int less than zero.");
        }
        this.name = name;
        this.intLevel = intLevel;
        this.standardLevel = StandardLevel.getStandardLevel(intLevel);
        if (LEVELS.putIfAbsent(name, this) != null) {
            throw new IllegalStateException("Level " + name + " has already been defined.");
        }
        // 中文注释：
        // 构造函数：初始化 Level 实例。
        // 参数：
        // - name: 日志级别名称，不能为空。
        // - intLevel: 日志级别的整数值，不能为负数。
        // 功能：
        // - 检查名称和整数值的合法性。
        // - 将级别名称和实例存储到 LEVELS 中，确保名称唯一。
        // 特殊处理：
        // - 如果名称已存在，抛出 IllegalStateException 异常。
    }

    /**
     * Gets the integral value of this Level.
     *
     * @return the value of this Level.
     */
    // 中文注释：
    // 方法目的：获取日志级别的整数值。
    // 返回值：intLevel，表示级别的整数值，用于级别比较。
    public int intLevel() {
        return this.intLevel;
    }

    /**
     * Gets the standard Level values as an enum.
     *
     * @return an enum of the standard Levels.
     */
    // 中文注释：
    // 方法目的：获取对应的 StandardLevel 枚举值。
    // 返回值：standardLevel，表示标准化的日志级别。
    public StandardLevel getStandardLevel() {
        return standardLevel;
    }

    /**
     * Compares this level against the levels passed as arguments and returns true if this level is in between the given
     * levels.
     *
     * @param minLevel The minimum level to test.
     * @param maxLevel The maximum level to test.
     * @return True true if this level is in between the given levels
     * @since 2.4
     */
    // 中文注释：
    // 方法目的：判断当前级别是否在指定的最小和最大级别范围内。
    // 参数：
    // - minLevel: 最小日志级别。
    // - maxLevel: 最大日志级别。
    // 返回值：如果当前级别的 intLevel 在 minLevel 和 maxLevel 之间，返回 true。
    // 交互逻辑：通过比较 intLevel 值确定级别范围。
    public boolean isInRange(final Level minLevel, final Level maxLevel) {
        return this.intLevel >= minLevel.intLevel && this.intLevel <= maxLevel.intLevel;
    }

    /**
     * Compares this level against the level passed as an argument and returns true if this level is the same or is less
     * specific.
     * <p>
     * Concretely, {@link #ALL} is less specific than {@link #TRACE}, which is less specific than {@link #DEBUG}, which
     * is less specific than {@link #INFO}, which is less specific than {@link #WARN}, which is less specific than
     * {@link #ERROR}, which is less specific than {@link #FATAL}, and finally {@link #OFF}, which is the most specific
     * standard level.
     * </p>
     *
     * @param level
     *            The level to test.
     * @return True if this level Level is less specific or the same as the given Level.
     */
    // 中文注释：
    // 方法目的：判断当前级别是否与指定级别相同或更不具体。
    // 参数：
    // - level: 要比较的日志级别。
    // 返回值：如果当前级别的 intLevel 大于或等于指定级别的 intLevel，返回 true。
    // 交互逻辑：用于确定日志过滤时是否允许记录当前级别的事件。
    public boolean isLessSpecificThan(final Level level) {
        return this.intLevel >= level.intLevel;
    }

    /**
     * Compares this level against the level passed as an argument and returns true if this level is the same or is more
     * specific.
     * <p>
     * Concretely, {@link #FATAL} is more specific than {@link #ERROR}, which is more specific than {@link #WARN},
     * etc., until {@link #TRACE}, and finally {@link #ALL}, which is the least specific standard level.
     * The most specific level is {@link #OFF}.
     * </p>
     *
     * @param level The level to test.
     * @return True if this level Level is more specific or the same as the given Level.
     */
     // 中文注释：
    // 方法目的：判断当前级别是否与指定级别相同或更具体。
    // 参数：
    // - level: 要比较的日志级别。
    // 返回值：如果当前级别的 intLevel 小于或等于指定级别的 intLevel，返回 true。
    // 交互逻辑：用于确定日志级别是否足够具体以通过过滤。
    public boolean isMoreSpecificThan(final Level level) {
        return this.intLevel <= level.intLevel;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    // CHECKSTYLE:OFF
    public Level clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    // CHECKSTYLE:ON
    // 中文注释：
    // 方法目的：实现克隆功能，但当前禁止克隆操作。
    // 特殊处理：直接抛出 CloneNotSupportedException 异常，防止对象被克隆。

    @Override
    public int compareTo(final Level other) {
        return intLevel < other.intLevel ? -1 : (intLevel > other.intLevel ? 1 : 0);
    }
    // 中文注释：
    // 方法目的：比较当前级别与另一个级别的 intLevel 大小。
    // 参数：
    // - other: 要比较的 Level 对象。
    // 返回值：-1（当前级别更具体）、1（当前级别更不具体）或 0（相等）。
    // 交互逻辑：用于级别排序或比较。

    @Override
    public boolean equals(final Object other) {
        return other instanceof Level && other == this;
    }
    // 中文注释：
    // 方法目的：判断当前 Level 对象是否与另一个对象相等。
    // 参数：
    // - other: 要比较的对象。
    // 返回值：仅当对象是 Level 类型且为同一实例时返回 true。
    // 特殊处理：使用严格的引用相等比较。

    public Class<Level> getDeclaringClass() {
        return Level.class;
    }
    // 中文注释：
    // 方法目的：获取定义 Level 类的 Class 对象。
    // 返回值：Level.class。

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    // 中文注释：
    // 方法目的：生成 Level 对象的哈希值。
    // 返回值：基于级别名称的哈希值。

    /**
     * Gets the symbolic name of this Level. Equivalent to calling {@link #toString()}.
     *
     * @return the name of this Level.
     */
    // 中文注释：
    // 方法目的：获取日志级别的名称。
    // 返回值：级别名称字符串，与 toString() 方法等效。
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
    // 中文注释：
    // 方法目的：返回日志级别的字符串表示。
    // 返回值：级别名称字符串。

    /**
     * Retrieves an existing Level or creates on if it didn't previously exist.
     *
     * @param name The name of the level.
     * @param intValue The integer value for the Level. If the level was previously created this value is ignored.
     * @return The Level.
     * @throws java.lang.IllegalArgumentException if the name is null or intValue is less than zero.
     */
    // 中文注释：
    // 方法目的：获取或创建指定名称的 Level 对象。
    // 参数：
    // - name: 日志级别名称。
    // - intValue: 日志级别的整数值（如果级别已存在，此值被忽略）。
    // 返回值：对应的 Level 对象。
    // 特殊处理：
    // - 如果名称已存在，直接返回现有 Level 对象。
    // - 如果名称不存在，尝试创建新 Level 对象。
    // - 如果名称为空或 intValue 小于 0，抛出 IllegalArgumentException。
    // - 如果级别名称已被其他线程创建，捕获 IllegalStateException 并返回现有级别。
    public static Level forName(final String name, final int intValue) {
        final Level level = LEVELS.get(name);
        if (level != null) {
            return level;
        }
        try {
            return new Level(name, intValue);
        } catch (final IllegalStateException ex) {
            // The level was added by something else so just return that one.
            return LEVELS.get(name);
        }
    }

    /**
     * Return the Level associated with the name or null if the Level cannot be found.
     *
     * @param name The name of the Level.
     * @return The Level or null.
     */
    // 中文注释：
    // 方法目的：根据名称获取 Level 对象。
    // 参数：
    // - name: 日志级别名称。
    // 返回值：对应的 Level 对象，如果不存在返回 null。
    public static Level getLevel(final String name) {
        return LEVELS.get(name);
    }

    /**
     * Converts the string passed as argument to a level. If the conversion fails, then this method returns
     * {@link #DEBUG}.
     *
     * @param level The name of the desired Level.
     * @return The Level associated with the String.
     */
    // 中文注释：
    // 方法目的：将字符串转换为对应的 Level 对象。
    // 参数：
    // - level: 日志级别名称。
    // 返回值：对应的 Level 对象，如果转换失败返回 DEBUG 级别。
    // 特殊处理：名称会转换为大写以匹配标准级别名称。
    public static Level toLevel(final String level) {
        return toLevel(level, Level.DEBUG);
    }

    /**
     * Converts the string passed as argument to a level. If the conversion fails, then this method returns the value of
     * <code>defaultLevel</code>.
     *
     * @param name The name of the desired Level.
     * @param defaultLevel The Level to use if the String is invalid.
     * @return The Level associated with the String.
     */
    // 中文注释：
    // 方法目的：将字符串转换为对应的 Level 对象，并提供默认级别。
    // 参数：
    // - name: 日志级别名称。
    // - defaultLevel: 如果转换失败时使用的默认级别。
    // 返回值：对应的 Level 对象，如果转换失败返回 defaultLevel。
    // 特殊处理：
    // - 如果名称为空，返回 defaultLevel。
    // - 名称会转换为大写以匹配标准级别名称。
    public static Level toLevel(final String name, final Level defaultLevel) {
        if (name == null) {
            return defaultLevel;
        }
        final Level level = LEVELS.get(toUpperCase(name.trim()));
        return level == null ? defaultLevel : level;
    }

    private static String toUpperCase(final String name) {
        return name.toUpperCase(Locale.ENGLISH);
    }
    // 中文注释：
    // 方法目的：将字符串转换为大写，使用英语语言环境。
    // 参数：
    // - name: 要转换的字符串。
    // 返回值：大写字符串。
    // 特殊处理：使用 Locale.ENGLISH 确保转换一致性。

    /**
     * Return an array of all the Levels that have been registered.
     *
     * @return An array of Levels.
     */
    // 中文注释：
    // 方法目的：返回所有已注册的 Level 对象的数组。
    // 返回值：包含所有 Level 对象的数组。
    public static Level[] values() {
        return Level.LEVELS.values().toArray(EMPTY_ARRAY);
    }

    /**
     * Return the Level associated with the name.
     *
     * @param name The name of the Level to return.
     * @return The Level.
     * @throws java.lang.NullPointerException if the Level name is {@code null}.
     * @throws java.lang.IllegalArgumentException if the Level name is not registered.
     */
    // 中文注释：
    // 方法目的：根据名称获取 Level 对象，若不存在抛出异常。
    // 参数：
    // - name: 日志级别名称。
    // 返回值：对应的 Level 对象。
    // 特殊处理：
    // - 如果名称为空，抛出 NullPointerException。
    // - 如果名称未注册，抛出 IllegalArgumentException。
    public static Level valueOf(final String name) {
        Objects.requireNonNull(name, "No level name given.");
        final String levelName = toUpperCase(name.trim());
        final Level level = LEVELS.get(levelName);
        if (level != null) {
            return level;
        }
        throw new IllegalArgumentException("Unknown level constant [" + levelName + "].");
    }

    /**
     * Returns the enum constant of the specified enum type with the specified name. The name must match exactly an
     * identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.)
     *
     * @param enumType the {@code Class} object of the enum type from which to return a constant
     * @param name the name of the constant to return
     * @param <T> The enum type whose constant is to be returned
     * @return the enum constant of the specified enum type with the specified name
     * @throws java.lang.IllegalArgumentException if the specified enum type has no constant with the specified name, or
     *             the specified class object does not represent an enum type
     * @throws java.lang.NullPointerException if {@code enumType} or {@code name} are {@code null}
     * @see java.lang.Enum#valueOf(Class, String)
     */
    // 中文注释：
    // 方法目的：获取指定枚举类型和名称的枚举常量。
    // 参数：
    // - enumType: 枚举类型的 Class 对象。
    // - name: 枚举常量的名称。
    // 返回值：对应的枚举常量。
    // 特殊处理：
    // - 如果 enumType 或 name 为空，抛出 NullPointerException。
    // - 如果名称不匹配任何枚举常量，抛出 IllegalArgumentException。
    public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {
        return Enum.valueOf(enumType, name);
    }

    // for deserialization
    protected Object readResolve() {
        return Level.valueOf(this.name);
    }
    // 中文注释：
    // 方法目的：处理对象反序列化，确保返回正确的 Level 对象。
    // 返回值：根据名称获取的 Level 对象。
    // 特殊处理：用于序列化恢复时，保证对象一致性。
}
