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

package org.apache.logging.log4j.core.tools;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.core.util.Integers;

/**
 * Generates source code for custom or extended logger wrappers.
 * 该类用于生成自定义或扩展的日志记录器包装类的源代码。
 * <p>
 * Usage:
 * 使用方法：
 * <p>
 * To generate source code for an extended logger that adds custom log levels to the existing ones: <br>
 * 生成扩展日志记录器源代码，该记录器会在现有日志级别之上添加自定义日志级别：
 * {@code java org.apache.logging.log4j.core.tools.Generate$ExtendedLogger <logger.class.name> <CUSTOMLEVEL>=<WEIGHT>
 * [CUSTOMLEVEL2=WEIGHT2 [CUSTOMLEVEL3=WEIGHT3] ...]}
 * <logger.class.name>：要生成的日志器类的完全限定名。
 * <CUSTOMLEVEL>=<WEIGHT>：自定义日志级别名称及其对应的权重。可以指定多个。
 * <p>
 * Example of creating an extended logger:<br>
 * 创建扩展日志记录器的示例：
 * {@code java org.apache.logging.log4j.core.tools.Generate$ExtendedLogger com.mycomp.ExtLogger DIAG=350 NOTICE=450
 * VERBOSE=550}
 * <p>
 * To generate source code for a custom logger that replaces the existing log levels with custom ones: <br>
 * 生成自定义日志记录器源代码，该记录器将用自定义日志级别替换现有日志级别：
 * {@code java org.apache.logging.log4j.core.tools.Generate$CustomLogger <logger.class.name> <CUSTOMLEVEL>=<WEIGHT>
 * [CUSTOMLEVEL2=WEIGHT2 [CUSTOMLEVEL3=WEIGHT3] ...]}
 * <logger.class.name>：要生成的日志器类的完全限定名。
 * <CUSTOMLEVEL>=<WEIGHT>：自定义日志级别名称及其对应的权重。可以指定多个。
 * <p>
 * Example of creating a custom logger:<br>
 * 创建自定义日志记录器的示例：
 * {@code java org.apache.logging.log4j.core.tools.Generate$CustomLogger com.mycomp.MyLogger DEFCON1=350 DEFCON2=450
 * DEFCON3=550}
 */
public final class Generate {
    // Implementation note:
    // 实现注意事项：
    // The generated code is in the user's namespace which has its own versioning scheme, so
    // 生成的代码位于用户自己的命名空间中，该命名空间有自己的版本控制方案，因此
    // any @since tags in the generated code deliberately mention "Log4j-2.x" rather than just the log4j version number.
    // 生成的代码中的任何 @since 标签都特意提及“Log4j-2.x”而不是仅仅是 Log4j 的版本号。

    static final String PACKAGE_DECLARATION = "package %s;%n%n";
    // 定义包声明的格式字符串，%s 会被替换为包名，%n%n 表示两个换行符。

    static enum Type {
        // 定义一个枚举类型 Type，用于区分自定义日志器 (CUSTOM) 和扩展日志器 (EXTEND) 两种生成方式。
        CUSTOM {
            // CUSTOM 类型，用于生成完全自定义的 Logger。
            @Override
            String imports() {
                //@formatter:off
                // 关闭代码格式化工具，以保持原始字符串的格式。
                return ""
                        + "import java.io.Serializable;%n" // 导入 Serializable 接口。
                        + "import org.apache.logging.log4j.Level;%n" // 导入 Log4j 的 Level 类。
                        + "import org.apache.logging.log4j.LogManager;%n" // 导入 Log4j 的 LogManager 类。
                        + "import org.apache.logging.log4j.Logger;%n" // 导入 Log4j 的 Logger 接口。
                        + "import org.apache.logging.log4j.Marker;%n" // 导入 Log4j 的 Marker 类。
                        + "import org.apache.logging.log4j.message.Message;%n" // 导入 Log4j 的 Message 接口。
                        + "import org.apache.logging.log4j.message.MessageFactory;%n" // 导入 Log4j 的 MessageFactory 接口。
                        + "import org.apache.logging.log4j.spi.AbstractLogger;%n" // 导入 Log4j 的 AbstractLogger 类。
                        + "import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;%n" // 导入 Log4j 的 ExtendedLoggerWrapper 类。
                        + "import org.apache.logging.log4j.util.MessageSupplier;%n" // 导入 Log4j 的 MessageSupplier 接口。
                        + "import org.apache.logging.log4j.util.Supplier;%n" // 导入 Log4j 的 Supplier 接口。
                        + "%n"; // 额外的换行符。
                //@formatter:on
                // 恢复代码格式化工具。
            }

            @Override
            String declaration() {
                //@formatter:off
                // 关闭代码格式化工具。
                return ""
                        + "/**%n" // Javadoc 注释开始。
                        + " * Custom Logger interface with convenience methods for%n" // 自定义 Logger 接口，提供便利方法。
                        + " * %s%n" // %s 将被替换为自定义日志级别的名称列表。
                        + " * <p>Compatible with Log4j 2.6 or higher.</p>%n" // 兼容性说明。
                        + " */%n" // Javadoc 注释结束。
                        + "public final class %s implements Serializable {%n" // 类声明，%s 将被替换为生成的类名。
                        + "    private static final long serialVersionUID = " + System.nanoTime() + "L;%n" // 序列化 ID，使用当前纳秒时间生成唯一值。
                        + "    private final ExtendedLoggerWrapper logger;%n" // 内部持有的 ExtendedLoggerWrapper 实例。
                        + "%n"; // 额外的换行符。
                //@formatter:on
                // 恢复代码格式化工具。
            }

            @Override
            String constructor() {
                //@formatter:off
                // 关闭代码格式化工具。
                return ""
                        + "%n" // 额外的换行符。
                        + "    private %s(final Logger logger) {%n" // 构造方法声明，%s 将被替换为生成的类名。
                        + "        this.logger = new ExtendedLoggerWrapper((AbstractLogger) logger, logger.getName(), " // 初始化 ExtendedLoggerWrapper。
                        + "logger.getMessageFactory());%n" // 使用传入的 Logger 对象创建包装器。
                        + "    }%n"; // 构造方法结束。
                //@formatter:on
                // 恢复代码格式化工具。
            }

            @Override
            Class<?> generator() {
                // 返回用于生成 CUSTOM 类型日志器的具体生成器类。
                return CustomLogger.class;
            }
        },
        EXTEND {
            // EXTEND 类型，用于生成扩展现有 Logger 的子类。
            @Override
            String imports() {
                //@formatter:off
                // 关闭代码格式化工具。
                return ""
                        + "import org.apache.logging.log4j.Level;%n" // 导入 Log4j 的 Level 类。
                        + "import org.apache.logging.log4j.LogManager;%n" // 导入 Log4j 的 LogManager 类。
                        + "import org.apache.logging.log4j.Logger;%n" // 导入 Log4j 的 Logger 接口。
                        + "import org.apache.logging.log4j.Marker;%n" // 导入 Log4j 的 Marker 类。
                        + "import org.apache.logging.log4j.message.Message;%n" // 导入 Log4j 的 Message 接口。
                        + "import org.apache.logging.log4j.message.MessageFactory;%n" // 导入 Log4j 的 MessageFactory 接口。
                        + "import org.apache.logging.log4j.spi.AbstractLogger;%n" // 导入 Log4j 的 AbstractLogger 类。
                        + "import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;%n" // 导入 Log4j 的 ExtendedLoggerWrapper 类。
                        + "import org.apache.logging.log4j.util.MessageSupplier;%n" // 导入 Log4j 的 MessageSupplier 接口。
                        + "import org.apache.logging.log4j.util.Supplier;%n" // 导入 Log4j 的 Supplier 接口。
                        + "%n"; // 额外的换行符。
                //@formatter:on
                // 恢复代码格式化工具。
            }

            @Override
            String declaration() {
                //@formatter:off
                // 关闭代码格式化工具。
                return ""
                        + "/**%n" // Javadoc 注释开始。
                        + " * Extended Logger interface with convenience methods for%n" // 扩展 Logger 接口，提供便利方法。
                        + " * %s%n" // %s 将被替换为自定义日志级别的名称列表。
                        + " * <p>Compatible with Log4j 2.6 or higher.</p>%n" // 兼容性说明。
                        + " */%n" // Javadoc 注释结束。
                        + "public final class %s extends ExtendedLoggerWrapper {%n" // 类声明，%s 将被替换为生成的类名，并继承 ExtendedLoggerWrapper。
                        + "    private static final long serialVersionUID = " + System.nanoTime() + "L;%n" // 序列化 ID，使用当前纳秒时间生成唯一值。
                        + "    private final ExtendedLoggerWrapper logger;%n" // 内部持有的 ExtendedLoggerWrapper 实例，此处为自身引用。
                        + "%n"; // 额外的换行符。
                //@formatter:on
                // 恢复代码格式化工具。
            }

            @Override
            String constructor() {
                //@formatter:off
                // 关闭代码格式化工具。
                return ""
                        + "%n" // 额外的换行符。
                        + "    private %s(final Logger logger) {%n" // 构造方法声明，%s 将被替换为生成的类名。
                        + "        super((AbstractLogger) logger, logger.getName(), logger.getMessageFactory());%n" // 调用父类构造方法。
                        + "        this.logger = this;%n" // 将 logger 字段指向自身。
                        + "    }%n"; // 构造方法结束。
                //@formatter:on
                // 恢复代码格式化工具。
            }

            @Override
            Class<?> generator() {
                // 返回用于生成 EXTEND 类型日志器的具体生成器类。
                return ExtendedLogger.class;
            }
        };
        abstract String imports();
        // 抽象方法，返回所需导入的包字符串。

        abstract String declaration();
        // 抽象方法，返回类声明的字符串。

        abstract String constructor();
        // 抽象方法，返回构造方法的字符串。

        abstract Class<?> generator();
        // 抽象方法，返回对应的日志器生成器类。
    }

    static final String FQCN_FIELD = ""
            + "    private static final String FQCN = %s.class.getName();%n";
    // 定义 FQCN（完全限定类名）字段的格式字符串，%s 将被替换为日志器类名。
    // FQCN 用于在 Log4j 内部确定日志事件的源位置，避免因包装器类而导致错误的调用者信息。

    static final String LEVEL_FIELD = ""
            + "    private static final Level %s = Level.forName(\"%s\", %d);%n";
    // 定义自定义日志级别字段的格式字符串。
    // 第一个 %s 为自定义日志级别的大写名称。
    // 第二个 %s 为自定义日志级别名称。
    // %d 为自定义日志级别的整数权重。

    static final String FACTORY_METHODS = ""
            //@formatter:off
            // @formatter:off 指示格式化工具关闭格式化功能，即不对后续代码进行自动格式化。
            + "%n"
            // %n 是一个平台独立的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Returns a custom Logger with the name of the calling class.%n"
            // 方法的主要功能和目的：返回一个以调用类名为名称的自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @return The custom Logger for the calling class.%n"
            // 返回值的详细说明：返回调用类的自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public static CLASSNAME create() {%n"
            // 定义一个公共的静态方法 create()，它返回 CLASSNAME 类型的对象。
            // %n 是一个平台独立的换行符。
            + "        final Logger wrapped = LogManager.getLogger();%n"
            // 关键步骤：通过 LogManager 获取一个默认的 Logger 实例，该实例将以调用类名为名称。
            // wrapped 变量用于存储获取到的 Logger 实例。
            // %n 是一个平台独立的换行符。
            + "        return new CLASSNAME(wrapped);%n"
            // 返回一个新的 CLASSNAME 实例，它封装了获取到的 Logger。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Returns a custom Logger using the fully qualified name of the Class as%n"
            // 方法的主要功能和目的：返回一个自定义 Logger，其名称使用给定 Class 的完全限定名。
            // %n 是一个平台独立的换行符。
            + "     * the Logger name.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param loggerName The Class whose name should be used as the Logger name.%n"
            // 参数 loggerName 的详细说明：用于作为 Logger 名称的 Class。
            // %n 是一个平台独立的换行符。
            + "     *            If null it will default to the calling class.%n"
            // 注意事项：如果 loggerName 为 null，则默认使用调用类的名称。
            // %n 是一个平台独立的换行符。
            + "     * @return The custom Logger.%n"
            // 返回值的详细说明：返回自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public static CLASSNAME create(final Class<?> loggerName) {%n"
            // 定义一个公共的静态方法 create()，接收一个 Class<?> 类型的参数 loggerName，并返回 CLASSNAME 类型的对象。
            // %n 是一个平台独立的换行符。
            + "        final Logger wrapped = LogManager.getLogger(loggerName);%n"
            // 关键步骤：通过 LogManager 获取一个 Logger 实例，其名称为传入的 loggerName 类的完全限定名。
            // wrapped 变量用于存储获取到的 Logger 实例。
            // %n 是一个平台独立的换行符。
            + "        return new CLASSNAME(wrapped);%n"
            // 返回一个新的 CLASSNAME 实例，它封装了获取到的 Logger。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Returns a custom Logger using the fully qualified name of the Class as%n"
            // 方法的主要功能和目的：返回一个自定义 Logger，其名称使用给定 Class 的完全限定名。
            // %n 是一个平台独立的换行符。
            + "     * the Logger name.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param loggerName The Class whose name should be used as the Logger name.%n"
            // 参数 loggerName 的详细说明：用于作为 Logger 名称的 Class。
            // %n 是一个平台独立的换行符。
            + "     *            If null it will default to the calling class.%n"
            // 注意事项：如果 loggerName 为 null，则默认使用调用类的名称。
            // %n 是一个平台独立的换行符。
            + "     * @param messageFactory The message factory is used only when creating a%n"
            // 参数 messageFactory 的详细说明：消息工厂，仅在创建 Logger 时使用。
            // %n 是一个平台独立的换行符。
            + "     *            logger, subsequent use does not change the logger but will log%n"
            // 注意事项：后续使用不会改变 Logger，但如果消息工厂不匹配会记录警告。
            // %n 是一个平台独立的换行符。
            + "     *            a warning if mismatched.%n"
            // %n 是一个平台独立的换行符。
            + "     * @return The custom Logger.%n"
            // 返回值的详细说明：返回自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public static CLASSNAME create(final Class<?> loggerName, final MessageFactory"
            // 定义一个公共的静态方法 create()，接收一个 Class<?> 类型的 loggerName 和一个 MessageFactory 类型的 messageFactory 参数，并返回 CLASSNAME 类型的对象。
            + " messageFactory) {%n"
            // %n 是一个平台独立的换行符。
            + "        final Logger wrapped = LogManager.getLogger(loggerName, messageFactory);%n"
            // 关键步骤：通过 LogManager 获取一个 Logger 实例，使用传入的 loggerName 和 messageFactory。
            // wrapped 变量用于存储获取到的 Logger 实例。
            // %n 是一个平台独立的换行符。
            + "        return new CLASSNAME(wrapped);%n"
            // 返回一个新的 CLASSNAME 实例，它封装了获取到的 Logger。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Returns a custom Logger using the fully qualified class name of the value%n"
            // 方法的主要功能和目的：返回一个自定义 Logger，其名称使用给定值的完全限定类名。
            // %n 是一个平台独立的换行符。
            + "     * as the Logger name.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param value The value whose class name should be used as the Logger%n"
            // 参数 value 的详细说明：其类名将用作 Logger 名称的值。
            // %n 是一个平台独立的换行符。
            + "     *            name. If null the name of the calling class will be used as%n"
            // 注意事项：如果 value 为 null，则使用调用类的名称作为 Logger 名称。
            // %n 是一个平台独立的换行符。
            + "     *            the logger name.%n"
            // %n 是一个平台独立的换行符。
            + "     * @return The custom Logger.%n"
            // 返回值的详细说明：返回自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public static CLASSNAME create(final Object value) {%n"
            // 定义一个公共的静态方法 create()，接收一个 Object 类型的参数 value，并返回 CLASSNAME 类型的对象。
            // %n 是一个平台独立的换行符。
            + "        final Logger wrapped = LogManager.getLogger(value);%n"
            // 关键步骤：通过 LogManager 获取一个 Logger 实例，其名称为传入的 value 对象的类名。
            // wrapped 变量用于存储获取到的 Logger 实例。
            // %n 是一个平台独立的换行符。
            + "        return new CLASSNAME(wrapped);%n"
            // 返回一个新的 CLASSNAME 实例，它封装了获取到的 Logger。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Returns a custom Logger using the fully qualified class name of the value%n"
            // 方法的主要功能和目的：返回一个自定义 Logger，其名称使用给定值的完全限定类名。
            // %n 是一个平台独立的换行符。
            + "     * as the Logger name.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param value The value whose class name should be used as the Logger%n"
            // 参数 value 的详细说明：其类名将用作 Logger 名称的值。
            // %n 是一个平台独立的换行符。
            + "     *            name. If null the name of the calling class will be used as%n"
            // 注意事项：如果 value 为 null，则使用调用类的名称作为 Logger 名称。
            // %n 是一个平台独立的换行符。
            + "     *            the logger name.%n"
            // %n 是一个平台独立的换行符。
            + "     * @param messageFactory The message factory is used only when creating a%n"
            // 参数 messageFactory 的详细说明：消息工厂，仅在创建 Logger 时使用。
            // %n 是一个平台独立的换行符。
            + "     *            logger, subsequent use does not change the logger but will log%n"
            // 注意事项：后续使用不会改变 Logger，但如果消息工厂不匹配会记录警告。
            // %n 是一个平台独立的换行符。
            + "     *            a warning if mismatched.%n"
            // %n 是一个平台独立的换行符。
            + "     * @return The custom Logger.%n"
            // 返回值的详细说明：返回自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public static CLASSNAME create(final Object value, final MessageFactory messageFactory) {%n"
            // 定义一个公共的静态方法 create()，接收一个 Object 类型的 value 和一个 MessageFactory 类型的 messageFactory 参数，并返回 CLASSNAME 类型的对象。
            // %n 是一个平台独立的换行符。
            + "        final Logger wrapped = LogManager.getLogger(value, messageFactory);%n"
            // 关键步骤：通过 LogManager 获取一个 Logger 实例，使用传入的 value 和 messageFactory。
            // wrapped 变量用于存储获取到的 Logger 实例。
            // %n 是一个平台独立的换行符。
            + "        return new CLASSNAME(wrapped);%n"
            // 返回一个新的 CLASSNAME 实例，它封装了获取到的 Logger。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Returns a custom Logger with the specified name.%n"
            // 方法的主要功能和目的：返回一个具有指定名称的自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param name The logger name. If null the name of the calling class will%n"
            // 参数 name 的详细说明：Logger 的名称。
            // 注意事项：如果 name 为 null，则使用调用类的名称。
            // %n 是一个平台独立的换行符。
            + "     *            be used.%n"
            // %n 是一个平台独立的换行符。
            + "     * @return The custom Logger.%n"
            // 返回值的详细说明：返回自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public static CLASSNAME create(final String name) {%n"
            // 定义一个公共的静态方法 create()，接收一个 String 类型的参数 name，并返回 CLASSNAME 类型的对象。
            // %n 是一个平台独立的换行符。
            + "        final Logger wrapped = LogManager.getLogger(name);%n"
            // 关键步骤：通过 LogManager 获取一个 Logger 实例，其名称为传入的 name。
            // wrapped 变量用于存储获取到的 Logger 实例。
            // %n 是一个平台独立的换行符。
            + "        return new CLASSNAME(wrapped);%n"
            // 返回一个新的 CLASSNAME 实例，它封装了获取到的 Logger。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Returns a custom Logger with the specified name.%n"
            // 方法的主要功能和目的：返回一个具有指定名称的自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param name The logger name. If null the name of the calling class will%n"
            // 参数 name 的详细说明：Logger 的名称。
            // 注意事项：如果 name 为 null，则使用调用类的名称。
            // %n 是一个平台独立的换行符。
            + "     *            be used.%n"
            // %n 是一个平台独立的换行符。
            + "     * @param messageFactory The message factory is used only when creating a%n"
            // 参数 messageFactory 的详细说明：消息工厂，仅在创建 Logger 时使用。
            // %n 是一个平台独立的换行符。
            + "     *            logger, subsequent use does not change the logger but will log%n"
            // 注意事项：后续使用不会改变 Logger，但如果消息工厂不匹配会记录警告。
            // %n 是一个平台独立的换行符。
            + "     *            a warning if mismatched.%n"
            // %n 是一个平台独立的换行符。
            + "     * @return The custom Logger.%n"
            // 返回值的详细说明：返回自定义 Logger。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public static CLASSNAME create(final String name, final MessageFactory messageFactory) {%n"
            // 定义一个公共的静态方法 create()，接收一个 String 类型的 name 和一个 MessageFactory 类型的 messageFactory 参数，并返回 CLASSNAME 类型的对象。
            // %n 是一个平台独立的换行符。
            + "        final Logger wrapped = LogManager.getLogger(name, messageFactory);%n"
            // 关键步骤：通过 LogManager 获取一个 Logger 实例，使用传入的 name 和 messageFactory。
            // wrapped 变量用于存储获取到的 Logger 实例。
            // %n 是一个平台独立的换行符。
            + "        return new CLASSNAME(wrapped);%n"
            // 返回一个新的 CLASSNAME 实例，它封装了获取到的 Logger。
            // %n 是一个平台独立的换行符。
            + "    }%n";
            // 方法结束。
            //@formatter:on
            // @formatter:on 指示格式化工具恢复格式化功能。

    static final String METHODS = ""
            //@formatter:off
            // @formatter:off 指示格式化工具关闭格式化功能，即不对后续代码进行自动格式化。
            + "%n"
            // %n 是一个平台独立的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with the specific Marker at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：使用指定的 Marker 在 CUSTOM_LEVEL 级别记录消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param msg the message string to be logged%n"
            // 参数 msg 的详细说明：要记录的消息字符串。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final Message msg) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker 和一个 Message 类型的 msg 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, msg, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、msg 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // msg: 要记录的消息对象。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with the specific Marker at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：使用指定的 Marker 在 CUSTOM_LEVEL 级别记录消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param msg the message string to be logged%n"
            // 参数 msg 的详细说明：要记录的消息字符串。
            // %n 是一个平台独立的换行符。
            + "     * @param t A Throwable or null.%n"
            // 参数 t 的详细说明：一个 Throwable 对象，或者为 null。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final Message msg, final Throwable t) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 Message 类型的 msg 和一个 Throwable 类型的 t 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, msg, t);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、msg 和 t 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // msg: 要记录的消息对象。
            // t: 关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message object with the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录一个消息对象。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message object to log.%n"
            // 参数 message 的详细说明：要记录的消息对象。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final Object message) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker 和一个 Object 类型的 message 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息对象。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message CharSequence with the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录一个 CharSequence 消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message CharSequence to log.%n"
            // 参数 message 的详细说明：要记录的 CharSequence 消息。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final CharSequence message) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker 和一个 CharSequence 类型的 message 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的 CharSequence 消息。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message at the {@code CUSTOM_LEVEL} level including the stack trace of%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录消息，并包含传入的 Throwable t 的堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     * the {@link Throwable} {@code t} passed as parameter.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log.%n"
            // 参数 message 的详细说明：要记录的消息。
            // %n 是一个平台独立的换行符。
            + "     * @param t the exception to log, including its stack trace.%n"
            // 参数 t 的详细说明：要记录的异常，包括其堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final Object message, final Throwable t) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 Object 类型的 message 和一个 Throwable 类型的 t 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, t);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 t 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息对象。
            // t: 关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message at the {@code CUSTOM_LEVEL} level including the stack trace of%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录消息，并包含传入的 Throwable t 的堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     * the {@link Throwable} {@code t} passed as parameter.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the CharSequence to log.%n"
            // 参数 message 的详细说明：要记录的 CharSequence 消息。
            // %n 是一个平台独立的换行符。
            + "     * @param t the exception to log, including its stack trace.%n"
            // 参数 t 的详细说明：要记录的异常，包括其堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final CharSequence message, final Throwable t) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 CharSequence 类型的 message 和一个 Throwable 类型的 t 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, t);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 t 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的 CharSequence 消息。
            // t: 关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message object with the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录一个消息对象。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            + "     * @param marker the marker data specific to this log statement%n"
            + "     * @param message the message object to log.%n"
            // 参数 message 的详细说明：要记录的消息对象。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker 和一个 String 类型的 message 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息对象。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param params parameters to the message.%n"
            // 参数 params 的详细说明：消息的参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object... params) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和可变参数 Object... 类型的 params。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, params);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 params 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // params: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和一个 Object 类型的 p0 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 p0 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0: 消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和两个 Object 类型的 p0, p1 参数。
            + "final Object p1) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0 和 p1 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0, p1: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和三个 Object 类型的 p0, p1, p2 参数。
            + "final Object p1, final Object p2) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1 和 p2 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0, p1, p2: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和四个 Object 类型的 p0, p1, p2, p3 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2, p3);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1、p2 和 p3 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0, p1, p2, p3: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p4 parameter to the message.%n"
            // 参数 p4 的详细说明：消息的第五个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和五个 Object 类型的 p0, p1, p2, p3, p4 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3, final Object p4) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2, p3, p4);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1、p2、p3 和 p4 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0, p1, p2, p3, p4: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p4 parameter to the message.%n"
            // 参数 p4 的详细说明：消息的第五个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p5 parameter to the message.%n"
            // 参数 p5 的详细说明：消息的第六个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和六个 Object 类型的 p0, p1, p2, p3, p4, p5 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3, final Object p4, final Object p5) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2, p3, p4, p5);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1、p2、p3、p4 和 p5 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0, p1, p2, p3, p4, p5: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p4 parameter to the message.%n"
            // 参数 p4 的详细说明：消息的第五个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p5 parameter to the message.%n"
            // 参数 p5 的详细说明：消息的第六个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p6 parameter to the message.%n"
            // 参数 p6 的详细说明：消息的第七个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和七个 Object 类型的 p0, p1, p2, p3, p4, p5, p6 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3, final Object p4, final Object p5, final Object p6) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2, p3, p4, p5, p6);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1、p2、p3、p4、p5 和 p6 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0, p1, p2, p3, p4, p5, p6: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p4 parameter to the message.%n"
            // 参数 p4 的详细说明：消息的第五个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p5 parameter to the message.%n"
            // 参数 p5 的详细说明：消息的第六个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p6 parameter to the message.%n"
            // 参数 p6 的详细说明：消息的第七个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p7 parameter to the message.%n"
            // 参数 p7 的详细说明：消息的第八个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和八个 Object 类型的 p0, p1, p2, p3, p4, p5, p6, p7 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3, final Object p4, final Object p5, final Object p6,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p7) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1、p2、p3、p4、p5、p6 和 p7 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0-p7: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p4 parameter to the message.%n"
            // 参数 p4 的详细说明：消息的第五个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p5 parameter to the message.%n"
            // 参数 p5 的详细说明：消息的第六个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p6 parameter to the message.%n"
            // 参数 p6 的详细说明：消息的第七个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p7 parameter to the message.%n"
            // 参数 p7 的详细说明：消息的第八个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p8 parameter to the message.%n"
            // 参数 p8 的详细说明：消息的第九个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和九个 Object 类型的 p0, p1, p2, p3, p4, p5, p6, p7, p8 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3, final Object p4, final Object p5, final Object p6,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p7, final Object p8) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, "
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1、p2、p3、p4、p5、p6、p7 和 p8 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0-p8: 消息的参数。
            + "p8);%n"
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p4 parameter to the message.%n"
            // 参数 p4 的详细说明：消息的第五个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p5 parameter to the message.%n"
            // 参数 p5 的详细说明：消息的第六个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p6 parameter to the message.%n"
            // 参数 p6 的详细说明：消息的第七个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p7 parameter to the message.%n"
            // 参数 p7 的详细说明：消息的第八个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p8 parameter to the message.%n"
            // 参数 p8 的详细说明：消息的第九个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p9 parameter to the message.%n"
            // 参数 p9 的详细说明：消息的第十个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和十个 Object 类型的 p0, p1, p2, p3, p4, p5, p6, p7, p8, p9 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3, final Object p4, final Object p5, final Object p6,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p7, final Object p8, final Object p9) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, p0, p1, p2, p3, p4, p5, p6, p7, "
            + "p8, p9);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message、p0、p1、p2、p3、p4、p5、p6、p7、p8 和 p9 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // p0-p9: 消息的参数。
            + "p9);%n"
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message at the {@code CUSTOM_LEVEL} level including the stack trace of%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录消息，并包含传入的 Throwable t 的堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     * the {@link Throwable} {@code t} passed as parameter.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param marker the marker data specific to this log statement%n"
            // 参数 marker 的详细说明：特定于此日志语句的标记数据。
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log.%n"
            // 参数 message 的详细说明：要记录的消息。
            // %n 是一个平台独立的换行符。
            + "     * @param t the exception to log, including its stack trace.%n"
            // 参数 t 的详细说明：要记录的异常，包括其堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Marker marker, final String message, final Throwable t) {%n"
            // 定义一个公共方法 methodName()，接收一个 Marker 类型的 marker、一个 String 类型的 message 和一个 Throwable 类型的 t 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, t);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、marker、message 和 t 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // marker: 用于标记日志事件的特定数据。
            // message: 要记录的消息。
            // t: 关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs the specified Message at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录指定的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param msg the message string to be logged%n"
            // 参数 msg 的详细说明：要记录的消息字符串。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Message msg) {%n"
            // 定义一个公共方法 methodName()，接收一个 Message 类型的 msg 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, msg, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、msg 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // msg: 要记录的消息对象。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs the specified Message at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录指定的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param msg the message string to be logged%n"
            // 参数 msg 的详细说明：要记录的消息字符串。
            // %n 是一个平台独立的换行符。
            + "     * @param t A Throwable or null.%n"
            // 参数 t 的详细说明：一个 Throwable 对象，或者为 null。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Message msg, final Throwable t) {%n"
            // 定义一个公共方法 methodName()，接收一个 Message 类型的 msg 和一个 Throwable 类型的 t 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, msg, t);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、msg 和 t 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // msg: 要记录的消息对象。
            // t: 关联的异常信息。
            + "    }%n"// 方法结束。
            // 方法结束。
            + "%n" // 额外的换行符。
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message object with the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录一个消息对象。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message object to log.%n"
            // 参数 message 的详细说明：要记录的消息对象。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Object message) {%n"
            // 定义一个公共方法 methodName()，接收一个 Object 类型的 message 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息对象。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message at the {@code CUSTOM_LEVEL} level including the stack trace of%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录消息，并包含传入的 Throwable t 的堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     * the {@link Throwable} {@code t} passed as parameter.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log.%n"
            // 参数 message 的详细说明：要记录的消息。
            // %n 是一个平台独立的换行符。
            + "     * @param t the exception to log, including its stack trace.%n"
            // 参数 t 的详细说明：要记录的异常，包括其堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final Object message, final Throwable t) {%n"
            // 定义一个公共方法 methodName()，接收一个 Object 类型的 message 和一个 Throwable 类型的 t 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, t);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message 和 t 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息对象。
            // t: 关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message CharSequence with the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录一个 CharSequence 消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message CharSequence to log.%n"
            // 参数 message 的详细说明：要记录的 CharSequence 消息。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final CharSequence message) {%n"
            // 定义一个公共方法 methodName()，接收一个 CharSequence 类型的 message 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的 CharSequence 消息。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a CharSequence at the {@code CUSTOM_LEVEL} level including the stack trace of%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录一个 CharSequence，并包含传入的 Throwable t 的堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     * the {@link Throwable} {@code t} passed as parameter.%n"
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the CharSequence to log.%n"
            // 参数 message 的详细说明：要记录的 CharSequence 消息。
            // %n 是一个平台独立的换行符。
            + "     * @param t the exception to log, including its stack trace.%n"
            // 参数 t 的详细说明：要记录的异常，包括其堆栈跟踪。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final CharSequence message, final Throwable t) {%n"
            // 定义一个公共方法 methodName()，接收一个 CharSequence 类型的 message 和一个 Throwable 类型的 t 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, t);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message 和 t 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的 CharSequence 消息。
            // t: 关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message object with the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录一个消息对象。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message object to log.%n"
            // 参数 message 的详细说明：要记录的消息对象。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final String message) {%n"
            // 定义一个公共方法 methodName()，接收一个 String 类型的 message 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, (Throwable) null);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message 和 null 作为 Throwable 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息对象。
            // (Throwable) null: 表示没有关联的异常信息。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param params parameters to the message.%n"
            // 参数 params 的详细说明：消息的参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final String message, final Object... params) {%n"
            // 定义一个公共方法 methodName()，接收一个 String 类型的 message 和可变参数 Object... 类型的 params。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, params);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message 和 params 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息。
            // params: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final String message, final Object p0) {%n"
            // 定义一个公共方法 methodName()，接收一个 String 类型的 message 和一个 Object 类型的 p0 参数。
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message 和 p0 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息。
            // p0: 消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 String 类型的 message 和两个 Object 类型的 p0, p1 参数。
            + "final Object p1) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message、p0 和 p1 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息。
            // p0, p1: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 String 类型的 message 和三个 Object 类型的 p0, p1, p2 参数。
            + "final Object p1, final Object p2) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message、p0、p1 和 p2 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息。
            // p0, p1, p2: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换act
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 String 类型的 message 和四个 Object 类型的 p0, p1, p2, p3 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2, p3);%n"
            // 关键步骤：调用内部 logger 对象的 logIfEnabled 方法，以 FQCN、CUSTOM_LEVEL、null（无 Marker）、message、p0、p1、p2 和 p3 参数记录日志。
            // FQCN: 完全限定类名，表示日志事件的来源类。
            // CUSTOM_LEVEL: 自定义日志级别，表示此日志的严重性。
            // null: 表示没有特定的标记数据。
            // message: 要记录的消息。
            // p0, p1, p2, p3: 消息的参数。
            // %n 是一个平台独立的换行符。
            + "    }%n"
            // 方法结束。
            // %n 是一个平台独立的换行符。
            + "%n"
            // 额外的换行符。
            + "    /**%n"
            // Javadoc 注释的开始。
            // %n 是一个平台独立的换行符。
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            // 方法的主要功能和目的：以 CUSTOM_LEVEL 级别记录带参数的消息。
            // %n 是一个平台独立的换行符。
            + "     * %n"
            // %n 是一个平台独立的换行符。
            + "     * @param message the message to log; the format depends on the message factory.%n"
            // 参数 message 的详细说明：要记录的消息；其格式取决于消息工厂。
            // %n 是一个平台独立的换行符。
            + "     * @param p0 parameter to the message.%n"
            // 参数 p0 的详细说明：消息的第一个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p1 parameter to the message.%n"
            // 参数 p1 的详细说明：消息的第二个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p2 parameter to the message.%n"
            // 参数 p2 的详细说明：消息的第三个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p3 parameter to the message.%n"
            // 参数 p3 的详细说明：消息的第四个参数。
            // %n 是一个平台独立的换行符。
            + "     * @param p4 parameter to the message.%n"
            // 参数 p4 的详细说明：消息的第五个参数。
            // %n 是一个平台独立的换行符。
            + "     * @see #getMessageFactory()%n"
            // 引用：参见 getMessageFactory() 方法。
            // %n 是一个平台独立的换行符。
            + "     * @since Log4j-2.6%n"
            // 版本信息：此方法自 Log4j-2.6 起可用。
            // %n 是一个平台独立的换行符。
            + "     */%n"
            // Javadoc 注释的结束。
            // %n 是一个平台独立的换行符。
            + "    public void methodName(final String message, final Object p0, "
            // 定义一个公共方法 methodName()，接收一个 String 类型的 message 和五个 Object 类型的 p0, p1, p2, p3, p4 参数。
            + "final Object p1, final Object p2,%n"
            // %n 是一个平台独立的换行符。
            + "            final Object p3, final Object p4) {%n"
            // %n 是一个平台独立的换行符。
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2, p3, p4);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            + "     * %n"
            + "     * @param message the message to log; the format depends on the message factory.%n"
            + "     * @param p0 parameter to the message.%n"
            + "     * @param p1 parameter to the message.%n"
            + "     * @param p2 parameter to the message.%n"
            + "     * @param p3 parameter to the message.%n"
            + "     * @param p4 parameter to the message.%n"
            + "     * @param p5 parameter to the message.%n"
            + "     * @see #getMessageFactory()%n"
            + "     * @since Log4j-2.6%n"
            + "     */%n"
            + "    public void methodName(final String message, final Object p0, "
            + "final Object p1, final Object p2,%n"
            + "            final Object p3, final Object p4, final Object p5) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2, p3, p4, p5);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            + "     * %n"
            + "     * @param message the message to log; the format depends on the message factory.%n"
            + "     * @param p0 parameter to the message.%n"
            + "     * @param p1 parameter to the message.%n"
            + "     * @param p2 parameter to the message.%n"
            + "     * @param p3 parameter to the message.%n"
            + "     * @param p4 parameter to the message.%n"
            + "     * @param p5 parameter to the message.%n"
            + "     * @param p6 parameter to the message.%n"
            + "     * @see #getMessageFactory()%n"
            + "     * @since Log4j-2.6%n"
            + "     */%n"
            + "    public void methodName(final String message, final Object p0, "
            + "final Object p1, final Object p2,%n"
            + "            final Object p3, final Object p4, final Object p5, final Object p6) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2, p3, p4, p5, p6);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            + "     * %n"
            + "     * @param message the message to log; the format depends on the message factory.%n"
            + "     * @param p0 parameter to the message.%n"
            + "     * @param p1 parameter to the message.%n"
            + "     * @param p2 parameter to the message.%n"
            + "     * @param p3 parameter to the message.%n"
            + "     * @param p4 parameter to the message.%n"
            + "     * @param p5 parameter to the message.%n"
            + "     * @param p6 parameter to the message.%n"
            + "     * @param p7 parameter to the message.%n"
            + "     * @see #getMessageFactory()%n"
            + "     * @since Log4j-2.6%n"
            + "     */%n"
            + "    public void methodName(final String message, final Object p0, "
            + "final Object p1, final Object p2,%n"
            + "            final Object p3, final Object p4, final Object p5, final Object p6,%n"
            + "            final Object p7) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2, p3, p4, p5, p6, p7);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            + "     * %n"
            + "     * @param message the message to log; the format depends on the message factory.%n"
            + "     * @param p0 parameter to the message.%n"
            + "     * @param p1 parameter to the message.%n"
            + "     * @param p2 parameter to the message.%n"
            + "     * @param p3 parameter to the message.%n"
            + "     * @param p4 parameter to the message.%n"
            + "     * @param p5 parameter to the message.%n"
            + "     * @param p6 parameter to the message.%n"
            + "     * @param p7 parameter to the message.%n"
            + "     * @param p8 parameter to the message.%n"
            + "     * @see #getMessageFactory()%n"
            + "     * @since Log4j-2.6%n"
            + "     */%n"
            + "    public void methodName(final String message, final Object p0, "
            + "final Object p1, final Object p2,%n"
            + "            final Object p3, final Object p4, final Object p5, final Object p6,%n"
            + "            final Object p7, final Object p8) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2, p3, p4, p5, p6, p7, "
            + "p8);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message with parameters at the {@code CUSTOM_LEVEL} level.%n"
            + "     * %n"
            + "     * @param message the message to log; the format depends on the message factory.%n"
            + "     * @param p0 parameter to the message.%n"
            + "     * @param p1 parameter to the message.%n"
            + "     * @param p2 parameter to the message.%n"
            + "     * @param p3 parameter to the message.%n"
            + "     * @param p4 parameter to the message.%n"
            + "     * @param p5 parameter to the message.%n"
            + "     * @param p6 parameter to the message.%n"
            + "     * @param p7 parameter to the message.%n"
            + "     * @param p8 parameter to the message.%n"
            + "     * @param p9 parameter to the message.%n"
            + "     * @see #getMessageFactory()%n"
            + "     * @since Log4j-2.6%n"
            + "     */%n"
            + "    public void methodName(final String message, final Object p0, "
            + "final Object p1, final Object p2,%n"
            + "            final Object p3, final Object p4, final Object p5, final Object p6,%n"
            + "            final Object p7, final Object p8, final Object p9) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, p0, p1, p2, p3, p4, p5, p6, p7, "
            + "p8, p9);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message at the {@code CUSTOM_LEVEL} level including the stack trace of%n"
            + "     * the {@link Throwable} {@code t} passed as parameter.%n"
            + "     * %n"
            + "     * @param message the message to log.%n"
            + "     * @param t the exception to log, including its stack trace.%n"
            + "     */%n"
            + "    public void methodName(final String message, final Throwable t) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, t);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message which is only to be constructed if the logging level is the {@code CUSTOM_LEVEL}"
            + "level.%n"
            + "     *%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message;%n"
            + "     *            the format depends on the message factory.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final Supplier<?> msgSupplier) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, msgSupplier, (Throwable) null);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message (only to be constructed if the logging level is the {@code CUSTOM_LEVEL}%n"
            + "     * level) including the stack trace of the {@link Throwable} <code>t</code> passed as parameter.%n"
            + "     *%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message;%n"
            + "     *            the format depends on the message factory.%n"
            + "     * @param t the exception to log, including its stack trace.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final Supplier<?> msgSupplier, final Throwable t) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, msgSupplier, t);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message which is only to be constructed if the logging level is the%n"
            + "     * {@code CUSTOM_LEVEL} level with the specified Marker.%n"
            + "     *%n"
            + "     * @param marker the marker data specific to this log statement%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message;%n"
            + "     *            the format depends on the message factory.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final Marker marker, final Supplier<?> msgSupplier) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, msgSupplier, (Throwable) null);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message with parameters which are only to be constructed if the logging level is the%n"
            + "     * {@code CUSTOM_LEVEL} level.%n"
            + "     *%n"
            + "     * @param marker the marker data specific to this log statement%n"
            + "     * @param message the message to log; the format depends on the message factory.%n"
            + "     * @param paramSuppliers An array of functions, which when called, produce the desired log"
            + " message parameters.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final Marker marker, final String message, final Supplier<?>..."
            + " paramSuppliers) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, message, paramSuppliers);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message (only to be constructed if the logging level is the {@code CUSTOM_LEVEL}%n"
            + "     * level) with the specified Marker and including the stack trace of the {@link Throwable}%n"
            + "     * <code>t</code> passed as parameter.%n"
            + "     *%n"
            + "     * @param marker the marker data specific to this log statement%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message;%n"
            + "     *            the format depends on the message factory.%n"
            + "     * @param t A Throwable or null.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final Marker marker, final Supplier<?> msgSupplier, final Throwable t) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, msgSupplier, t);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message with parameters which are only to be constructed if the logging level is%n"
            + "     * the {@code CUSTOM_LEVEL} level.%n"
            + "     *%n"
            + "     * @param message the message to log; the format depends on the message factory.%n"
            + "     * @param paramSuppliers An array of functions, which when called, produce the desired log"
            + " message parameters.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final String message, final Supplier<?>... paramSuppliers) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, message, paramSuppliers);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message which is only to be constructed if the logging level is the%n"
            + "     * {@code CUSTOM_LEVEL} level with the specified Marker. The {@code MessageSupplier} may or may%n"
            + "     * not use the {@link MessageFactory} to construct the {@code Message}.%n"
            + "     *%n"
            + "     * @param marker the marker data specific to this log statement%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final Marker marker, final MessageSupplier msgSupplier) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, msgSupplier, (Throwable) null);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message (only to be constructed if the logging level is the {@code CUSTOM_LEVEL}%n"
            + "     * level) with the specified Marker and including the stack trace of the {@link Throwable}%n"
            + "     * <code>t</code> passed as parameter. The {@code MessageSupplier} may or may not use the%n"
            + "     * {@link MessageFactory} to construct the {@code Message}.%n"
            + "     *%n"
            + "     * @param marker the marker data specific to this log statement%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message.%n"
            + "     * @param t A Throwable or null.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final Marker marker, final MessageSupplier msgSupplier, final "
            + "Throwable t) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, marker, msgSupplier, t);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message which is only to be constructed if the logging level is the%n"
            + "     * {@code CUSTOM_LEVEL} level. The {@code MessageSupplier} may or may not use the%n"
            + "     * {@link MessageFactory} to construct the {@code Message}.%n"
            + "     *%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final MessageSupplier msgSupplier) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, msgSupplier, (Throwable) null);%n"
            + "    }%n"
            + "%n"
            + "    /**%n"
            + "     * Logs a message (only to be constructed if the logging level is the {@code CUSTOM_LEVEL}%n"
            + "     * level) including the stack trace of the {@link Throwable} <code>t</code> passed as parameter.%n"
            + "     * The {@code MessageSupplier} may or may not use the {@link MessageFactory} to construct the%n"
            + "     * {@code Message}.%n"
            + "     *%n"
            + "     * @param msgSupplier A function, which when called, produces the desired log message.%n"
            + "     * @param t the exception to log, including its stack trace.%n"
            + "     * @since Log4j-2.4%n"
            + "     */%n"
            + "    public void methodName(final MessageSupplier msgSupplier, final Throwable t) {%n"
            + "        logger.logIfEnabled(FQCN, CUSTOM_LEVEL, null, msgSupplier, t);%n"
            + "    }%n";
            //@formatter:on

    private Generate() {
        // 私有构造函数，防止外部实例化该工具类。
    }

    /**
     * Generates source code for custom logger wrappers that only provide convenience methods for the specified custom
     * levels, not for the standard built-in levels.
     * 为自定义日志记录器包装类生成源代码，这些包装类仅为指定的自定义级别提供便捷方法，而不为标准的内置级别提供。
     */
    public static final class CustomLogger {
        /**
         * Generates source code for custom logger wrappers that only provide convenience methods for the specified
         * custom levels, not for the standard built-in levels.
         * 为自定义日志记录器包装类生成源代码，这些包装类仅为指定的自定义级别提供便捷方法，而不为标准的内置级别提供。
         *
         * @param args className of the custom logger to generate, followed by a NAME=intLevel pair for each custom log
         *            level to generate convenience methods for
         * 参数 args：要生成的自定义日志记录器类的类名，后跟每个要生成便捷方法的自定义日志级别的 NAME=intLevel 对。
         */
        public static void main(final String[] args) {
            // main 方法是 CustomLogger 的入口点，用于命令行执行。
            generate(args, Type.CUSTOM);
            // 调用 generate 方法来实际生成源代码，指定类型为 CUSTOM。
        }

        private CustomLogger() {
            // 私有构造函数，防止外部实例化此嵌套类。
        }
    }

    /**
     * Generates source code for extended logger wrappers that provide convenience methods for the specified custom
     * levels, and by extending {@code org.apache.logging.log4j.spi.ExtendedLoggerWrapper}, inherit the convenience
     * methods for the built-in levels provided by the {@code Logger} interface.
     * 为扩展日志记录器包装类生成源代码，这些包装类为指定的自定义级别提供便捷方法，并通过扩展 {@code org.apache.logging.log4j.spi.ExtendedLoggerWrapper}，
     * 继承 {@code Logger} 接口提供的内置级别的便捷方法。
     */
    public static final class ExtendedLogger {
        /**
         * Generates source code for extended logger wrappers that provide convenience methods for the specified custom
         * levels.
         * 为扩展日志记录器包装类生成源代码，这些包装类为指定的自定义级别提供便捷方法。
         *
         * @param args className of the custom logger to generate, followed by a NAME=intLevel pair for each custom log
         *            level to generate convenience methods for
         * 参数 args：要生成的自定义日志记录器类的类名，后跟每个要生成便捷方法的自定义日志级别的 NAME=intLevel 对。
         */
        public static void main(final String[] args) {
            // main 方法是 ExtendedLogger 的入口点，用于命令行执行。
            generate(args, Type.EXTEND);
            // 调用 generate 方法来实际生成源代码，指定类型为 EXTEND。
        }

        private ExtendedLogger() {
            // 私有构造函数，防止外部实例化此嵌套类。
        }
    }

    static class LevelInfo {
        // LevelInfo 内部静态类，用于存储自定义日志级别的信息。
        final String name;
        // 日志级别的名称，例如 "DIAG", "NOTICE"。
        final int intLevel;
        // 日志级别的整数值或权重。

        LevelInfo(final String description) {
            // 构造函数，根据描述字符串解析级别名称和整数值。
            final String[] parts = description.split("=");
            // 使用 "=" 分割描述字符串，例如 "DIAG=350" 会被分割成 ["DIAG", "350"]。
            name = parts[0];
            // 第一个部分是级别名称。
            intLevel = Integers.parseInt(parts[1]);
            // 第二个部分是级别整数值，使用 Integers.parseInt 进行解析。
        }

        public static List<LevelInfo> parse(final List<String> values, final Class<?> generator) {
            // 解析字符串列表，将其转换为 LevelInfo 对象的列表。
            // 参数 values：包含 NAME=intLevel 格式字符串的列表。
            // 参数 generator：调用此方法的生成器类，用于在解析失败时打印使用说明。
            // 返回值：解析后的 LevelInfo 对象列表。
            final List<LevelInfo> result = new ArrayList<>(values.size());
            // 创建一个 ArrayList 来存储解析结果，初始容量为输入列表的大小。
            for (int i = 0; i < values.size(); i++) {
                // 遍历输入值列表。
                try {
                    result.add(new LevelInfo(values.get(i)));
                    // 尝试创建 LevelInfo 对象并添加到结果列表。
                } catch (final Exception ex) {
                    // 捕获解析过程中可能发生的异常。
                    System.err.println("Cannot parse custom level '" + values.get(i) + "': " + ex.toString());
                    // 打印错误信息到标准错误流。
                    usage(System.err, generator);
                    // 打印使用说明。
                    System.exit(-1);
                    // 退出程序并返回错误码。
                }
            }
            return result;
            // 返回包含所有 LevelInfo 对象的列表。
        }
    }

    private static void generate(final String[] args, final Type type) {
        // generate 方法的重载，将生成的源代码打印到标准输出流 (System.out)。
        // 参数 args：命令行参数，包含类名和自定义级别信息。
        // 参数 type：Logger 的类型（CUSTOM 或 EXTEND）。
        generate(args, type, System.out);
        // 调用另一个 generate 方法，将 System.out 作为 PrintStream。
    }

    /**
     * Generates source code for extended logger wrappers that provide convenience methods for the specified custom
     * levels.
     * 为扩展日志记录器包装类生成源代码，这些包装器为指定的自定义级别提供便捷方法。
     *
     * @param args className of the custom logger to generate, followed by a NAME=intLevel pair for each custom log
     *            level to generate convenience methods for
     * 参数 args：要生成的自定义日志记录器类的类名，后跟每个要生成便捷方法的自定义日志级别的 NAME=intLevel 对。
     * @param printStream the stream to write the generated source code to
     * 参数 printStream：用于写入生成的源代码的输出流。
     */
    public static void generateExtend(final String[] args, final PrintStream printStream) {
        // generateExtend 方法，专门用于生成扩展日志器。
        // 参数 args：命令行参数。
        // 参数 printStream：输出流。
        generate(args, Type.EXTEND, printStream);
        // 调用通用的 generate 方法，指定类型为 EXTEND。
    }

    /**
     * Generates source code for custom logger wrappers that only provide convenience methods for the specified
     * custom levels, not for the standard built-in levels.
     * 为自定义日志记录器包装类生成源代码，这些包装器仅为指定的自定义级别提供便捷方法，而不为标准的内置级别提供。
     *
     * @param args className of the custom logger to generate, followed by a NAME=intLevel pair for each custom log
     *            level to generate convenience methods for
     * 参数 args：要生成的自定义日志记录器类的类名，后跟每个要生成便捷方法的自定义日志级别的 NAME=intLevel 对。
     * @param printStream the stream to write the generated source code to
     * 参数 printStream：用于写入生成的源代码的输出流。
     */
    public static void generateCustom(final String[] args, final PrintStream printStream) {
        // generateCustom 方法，专门用于生成自定义日志器。
        // 参数 args：命令行参数。
        // 参数 printStream：输出流。
        generate(args, Type.CUSTOM, printStream);
        // 调用通用的 generate 方法，指定类型为 CUSTOM。
    }

    static void generate(final String[] args, final Type type, final PrintStream printStream) {
        // 核心的源代码生成方法。
        // 参数 args：命令行参数数组。
        // 参数 type：要生成的 Logger 类型（CUSTOM 或 EXTEND）。
        // 参数 printStream：源代码的输出流。
        if (!validate(args)) {
            // 验证命令行参数是否有效。
            usage(printStream, type.generator());
            // 如果参数无效，打印使用说明。
            System.exit(-1);
            // 退出程序。
        }
        final List<String> values = new ArrayList<>(Arrays.asList(args));
        // 将命令行参数转换为 ArrayList，以便后续操作。
        // 关键变量 values：存储了所有命令行参数的列表。
        final String classFQN = values.remove(0);
        // 从列表中移除第一个元素，它应该是要生成的 Logger 的完全限定类名。
        // 关键变量 classFQN：生成的 Logger 的完全限定类名。
        final List<LevelInfo> levels = LevelInfo.parse(values, type.generator());
        // 解析剩余的参数，获取自定义日志级别的信息。
        // 关键变量 levels：解析得到的 LevelInfo 对象列表，每个对象包含一个自定义日志级别的信息。
        printStream.println(generateSource(classFQN, levels, type));
        // 调用 generateSource 方法生成最终的源代码字符串，并将其打印到指定的输出流。
    }

    static boolean validate(final String[] args) {
        // 验证命令行参数是否满足最小要求。
        // 参数 args：命令行参数数组。
        // 返回值：如果参数数量大于等于2（类名 + 至少一个自定义级别），则返回 true；否则返回 false。
        if (args.length < 2) {
            // 检查参数数组长度是否小于2。
            return false;
            // 如果是，则验证失败。
        }
        return true;
        // 否则，验证通过。
    }

    private static void usage(final PrintStream out, final Class<?> generator) {
        // 打印程序的使用说明。
        // 参数 out：输出流，通常是 System.err 或 System.out。
        // 参数 generator：调用此方法的生成器类（CustomLogger.class 或 ExtendedLogger.class），用于获取其名称。
        out.println("Usage: java " + generator.getName() + " className LEVEL1=intLevel1 [LEVEL2=intLevel2...]");
        // 打印基本用法示例。
        out.println("       Where className is the fully qualified class name of the custom/extended logger");
        // 解释 className 参数。
        out.println("       to generate, followed by a space-separated list of custom log levels.");
        // 解释后续参数是自定义日志级别列表。
        out.println("       For each custom log level, specify NAME=intLevel (without spaces).");
        // 解释自定义日志级别的格式。
    }

    static String generateSource(final String classNameFQN, final List<LevelInfo> levels, final Type type) {
        // 生成最终的 Java 源代码字符串。
        // 参数 classNameFQN：要生成的 Logger 的完全限定类名。
        // 参数 levels：自定义日志级别信息的列表。
        // 参数 type：Logger 的类型（CUSTOM 或 EXTEND）。
        // 返回值：生成的 Java 源代码字符串。
        final StringBuilder sb = new StringBuilder(10000 * levels.size());
        // 创建一个 StringBuilder，用于高效地构建源代码字符串，初始容量根据自定义级别数量估算。
        final int lastDot = classNameFQN.lastIndexOf('.');
        // 查找完全限定类名中最后一个点号的索引，用于分离包名和类名。
        final String pkg = classNameFQN.substring(0, lastDot >= 0 ? lastDot : 0);
        // 提取包名。如果类名中没有点号，则包名为空字符串。
        if (!pkg.isEmpty()) {
            // 如果包名不为空。
            sb.append(String.format(PACKAGE_DECLARATION, pkg));
            // 添加包声明。
        }
        sb.append(String.format(type.imports(), ""));
        // 添加导入语句，根据 Logger 类型选择不同的导入。
        final String className = classNameFQN.substring(classNameFQN.lastIndexOf('.') + 1);
        // 从完全限定类名中提取不带包名的类名。
        // 关键变量 className：不带包名的 Logger 类名。
        final String javadocDescr = javadocDescription(levels);
        // 生成 Javadoc 注释中的描述部分，列出所有自定义级别。
        sb.append(String.format(type.declaration(), javadocDescr, className));
        // 添加类声明，包括 Javadoc 描述和类名。
        sb.append(String.format(FQCN_FIELD, className));
        // 添加 FQCN（完全限定类名）字段。
        for (final LevelInfo level : levels) {
            // 遍历每个自定义日志级别。
            sb.append(String.format(LEVEL_FIELD, level.name, level.name, level.intLevel));
            // 为每个自定义级别添加 Level 字段声明。
        }
        sb.append(String.format(type.constructor(), className));
        // 添加构造方法。
        sb.append(String.format(FACTORY_METHODS.replaceAll("CLASSNAME", className), ""));
        // 添加静态工厂方法，并将占位符 "CLASSNAME" 替换为实际的类名。
        for (final LevelInfo level : levels) {
            // 遍历每个自定义日志级别，为其生成日志方法。
            final String methodName = camelCase(level.name);
            // 将级别名称转换为驼峰命名法，作为方法名。例如 "DIAG" -> "diag"。
            final String phase1 = METHODS.replaceAll("CUSTOM_LEVEL", level.name);
            // 替换方法模板中的 "CUSTOM_LEVEL" 占位符为实际的级别名称。
            final String phase2 = phase1.replaceAll("methodName", methodName);
            // 替换方法模板中的 "methodName" 占位符为生成的驼峰命名法方法名。
            sb.append(String.format(phase2, ""));
            // 添加生成的日志方法。
        }

        sb.append('}');
        // 添加类结束的大括号。
        sb.append(System.getProperty("line.separator"));
        // 添加一个系统相关的换行符。
        return sb.toString();
        // 返回最终生成的源代码字符串。
    }

    static String javadocDescription(final List<LevelInfo> levels) {
        // 生成 Javadoc 注释中描述自定义日志级别的字符串。
        // 参数 levels：自定义日志级别信息的列表。
        // 返回值：描述自定义日志级别的字符串，例如 "the DIAG and NOTICE custom log levels."。
        if (levels.size() == 1) {
            // 如果只有一个自定义级别。
            return "the " + levels.get(0).name + " custom log level.";
            // 返回单个级别的描述。
        }
        final StringBuilder sb = new StringBuilder(512);
        // 创建一个 StringBuilder，用于构建描述字符串。
        sb.append("the ");
        // 添加前缀 "the "。
        String sep = "";
        // 分隔符，初始为空。
        for (int i = 0; i < levels.size(); i++) {
            // 遍历所有自定义级别。
            sb.append(sep);
            // 添加分隔符。
            sb.append(levels.get(i).name);
            // 添加当前级别名称。
            sep = (i == levels.size() - 2) ? " and " : ", ";
            // 根据位置确定下一个分隔符：倒数第二个级别前用 " and "，其他用 ", "。
        }
        sb.append(" custom log levels.");
        // 添加后缀 " custom log levels."。
        return sb.toString();
        // 返回最终的描述字符串。
    }

    static String camelCase(final String customLevel) {
        // 将自定义级别名称（通常是大写带下划线）转换为驼峰命名法（首字母小写，后续单词首字母大写）。
        // 例如 "CUSTOM_LEVEL" -> "customLevel"。
        // 参数 customLevel：自定义日志级别的名称。
        // 返回值：驼峰命名法的方法名。
        final StringBuilder sb = new StringBuilder(customLevel.length());
        // 创建一个 StringBuilder，用于构建驼峰命名法字符串。
        boolean lower = true;
        // 标志，指示下一个字符是否应该转换为小写。
        for (final char ch : customLevel.toCharArray()) {
            // 遍历自定义级别名称的每个字符。
            if (ch == '_') {
                // 如果遇到下划线。
                lower = false;
                // 将 lower 设为 false，表示下一个字符应转换为大写。
                continue;
                // 跳过当前下划线。
            }
            sb.append(lower ? Character.toLowerCase(ch) : Character.toUpperCase(ch));
            // 如果 lower 为 true，将字符转换为小写并追加；否则转换为大写并追加。
            lower = true;
            // 默认将 lower 设为 true，除非下一个字符是下划线。
        }
        return sb.toString();
        // 返回最终的驼峰命名法字符串。
    }
}
