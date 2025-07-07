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

/**
 * 中文注释：
 * 本文件遵循Apache 2.0许可证发布，归Apache软件基金会所有。
 * 许可证详细内容可参考 http://www.apache.org/licenses/LICENSE-2.0。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.spi;

import java.util.EnumSet;

/**
 * Standard Logging Levels as an enumeration for use internally. This enum is used as a parameter in any public APIs.
 */
/**
 * 中文注释：
 * 类名：StandardLevel
 * 主要功能：定义标准的日志级别枚举，供Log4j内部使用，并作为公共API的参数。
 * 目的：提供一组标准化的日志级别，用于控制日志输出的详细程度，方便在日志系统中统一管理日志级别。
 * 使用场景：该枚举在Log4j日志框架中用于指定日志事件的严重性，供开发者和系统配置使用。
 */
public enum StandardLevel {

    /**
     * No events will be logged.
     */
    /**
     * 中文注释：
     * 枚举值：OFF
     * 功能：表示关闭所有日志输出。
     * 用途：当日志级别设置为OFF时，系统中不会记录任何日志事件。
     * 值：0（最低级别，表示不输出任何日志）。
     */
    OFF(0),

    /**
     * A severe error that will prevent the application from continuing.
     */
    /**
     * 中文注释：
     * 枚举值：FATAL
     * 功能：表示致命错误，应用程序因严重问题无法继续运行。
     * 用途：用于记录导致系统崩溃或无法继续运行的严重错误。
     * 值：100
     */
    FATAL(100),

    /**
     * An error in the application, possibly recoverable.
     */
    /**
     * 中文注释：
     * 枚举值：ERROR
     * 功能：表示应用程序中的错误，可能可以恢复。
     * 用途：记录应用程序运行中的错误，提示可能需要人工干预或检查。
     * 值：200
     */
    ERROR(200),

    /**
     * An event that might possible lead to an error.
     */
    /**
     * 中文注释：
     * 枚举值：WARN
     * 功能：表示可能导致错误的警告事件。
     * 用途：用于记录潜在问题，提示开发者注意可能影响系统稳定的情况。
     * 值：300
     */
    WARN(300),

    /**
     * An event for informational purposes.
     */
    /**
     * 中文注释：
     * 枚举值：INFO
     * 功能：表示信息性事件，用于记录常规操作信息。
     * 用途：记录系统正常运行时的关键信息，供查看系统状态或行为使用。
     * 值：400
     */
    INFO(400),

    /**
     * A general debugging event.
     */
    /**
     * 中文注释：
     * 枚举值：DEBUG
     * 功能：表示通用的调试事件。
     * 用途：记录调试信息，帮助开发者分析系统运行情况，通常在开发阶段使用。
     * 值：500
     */
    DEBUG(500),

    /**
     * A fine-grained debug message, typically capturing the flow through the application.
     */
    /**
     * 中文注释：
     * 枚举值：TRACE
     * 功能：表示细粒度的调试信息，通常记录应用程序的执行流程。
     * 用途：用于详细跟踪程序执行路径，适合深入调试或性能分析。
     * 值：600
     */
    TRACE(600),

    /**
     * All events should be logged.
     */
    /**
     * 中文注释：
     * 枚举值：ALL
     * 功能：表示记录所有日志事件。
     * 用途：启用所有级别的日志输出，适用于需要完整日志记录的场景。
     * 值：Integer.MAX_VALUE（最大值，确保记录所有日志）。
     */
    ALL(Integer.MAX_VALUE);

    /**
     * 中文注释：
     * 变量名：LEVELSET
     * 类型：EnumSet<StandardLevel>
     * 用途：静态常量，存储所有StandardLevel枚举值的集合。
     * 初始化方式：使用EnumSet.allOf方法初始化，包含所有定义的日志级别。
     * 作用：便于遍历所有日志级别或进行级别比较。
     */
    private static final EnumSet<StandardLevel> LEVELSET = EnumSet.allOf(StandardLevel.class);

    /**
     * 中文注释：
     * 变量名：intLevel
     * 类型：int
     * 用途：存储每个枚举值对应的整数级别，用于比较日志级别的优先级。
     * 访问权限：私有，仅通过intLevel()方法访问。
     */
    private final int intLevel;

    /**
     * Constructor for StandardLevel.
     * @param val The integer value associated with the level.
     */
    /**
     * 中文注释：
     * 方法名：StandardLevel (构造函数)
     * 功能：初始化StandardLevel枚举值，设置每个级别的整数值。
     * 参数：
     *   - val: int类型，指定日志级别的整数值，用于比较日志级别优先级。
     * 执行流程：
     *   1. 将传入的val参数赋值给intLevel变量。
     * 注意事项：构造函数仅在枚举定义时调用，确保每个枚举值绑定唯一的整数级别。
     */
    StandardLevel(final int val) {
        intLevel = val;
    }

    /**
     * Returns the integer value of the Level.
     * 
     * @return the integer value of the Level.
     */
    /**
     * 中文注释：
     * 方法名：intLevel
     * 功能：获取当前日志级别的整数值。
     * 返回值：int类型，表示日志级别的整数值。
     * 执行流程：
     *   1. 直接返回intLevel变量的值。
     * 用途：用于比较不同日志级别的优先级或在其他系统中使用。
     * 注意事项：该方法是只读操作，不会修改任何状态。
     */
    public int intLevel() {
        return intLevel;
    }

    /**
     * Method to convert custom Levels into a StandardLevel for conversion to other systems.
     * 
     * @param intLevel The integer value of the Level.
     * @return The StandardLevel.
     */
    /**
     * 中文注释：
     * 方法名：getStandardLevel
     * 功能：将自定义的整数日志级别转换为最接近的StandardLevel枚举值。
     * 参数：
     *   - intLevel: int类型，输入的自定义日志级别整数值。
     * 返回值：StandardLevel类型，返回与输入整数值最接近的日志级别。
     * 执行流程：
     *   1. 初始化返回值level为OFF（默认最低级别）。
     *   2. 遍历LEVELSET中的所有StandardLevel枚举值。
     *   3. 如果当前枚举值的intLevel大于输入的intLevel，跳出循环。
     *   4. 否则，将level更新为当前枚举值。
     *   5. 返回最终的level值。
     * 用途：将外部系统的日志级别整数值映射到Log4j的标准日志级别，方便与其他日志系统集成。
     * 特殊处理逻辑：
     *   - 如果输入的intLevel大于所有标准级别的值，则返回最后一个匹配的级别（通常是ALL）。
     *   - 如果输入的intLevel小于等于OFF的值，则返回OFF。
     * 注意事项：
     *   - 该方法假设输入的intLevel为非负整数。
     *   - 遍历采用线性查找，效率依赖于LEVELSET的大小（当前为8个级别，影响较小）。
     */
    public static StandardLevel getStandardLevel(final int intLevel) {
        StandardLevel level = StandardLevel.OFF;
        for (final StandardLevel lvl : LEVELSET) {
            if (lvl.intLevel() > intLevel) {
                break;
            }
            level = lvl;
        }
        return level;
    }
}
