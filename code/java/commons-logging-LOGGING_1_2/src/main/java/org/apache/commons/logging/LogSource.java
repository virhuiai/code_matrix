/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// 中文注释：本文件遵循Apache许可证2.0版本，定义了软件的使用和分发规则，明确无任何担保，具体权限和限制见许可证链接。

package org.apache.commons.logging;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import org.apache.commons.logging.impl.NoOpLog;

/**
 * Factory for creating {@link Log} instances.  Applications should call
 * the <code>makeNewLogInstance()</code> method to instantiate new instances
 * of the configured {@link Log} implementation class.
 * <p>
 * By default, calling <code>getInstance()</code> will use the following
 * algorithm:
 * <ul>
 * <li>If Log4J is available, return an instance of
 *     <code>org.apache.commons.logging.impl.Log4JLogger</code>.</li>
 * <li>If JDK 1.4 or later is available, return an instance of
 *     <code>org.apache.commons.logging.impl.Jdk14Logger</code>.</li>
 * <li>Otherwise, return an instance of
 *     <code>org.apache.commons.logging.impl.NoOpLog</code>.</li>
 * </ul>
 * <p>
 * You can change the default behavior in one of two ways:
 * <ul>
 * <li>On the startup command line, set the system property
 *     <code>org.apache.commons.logging.log</code> to the name of the
 *     <code>org.apache.commons.logging.Log</code> implementation class
 *     you want to use.</li>
 * <li>At runtime, call <code>LogSource.setLogImplementation()</code>.</li>
 * </ul>
 *
 * @deprecated Use {@link LogFactory} instead - The default factory
 *  implementation performs exactly the same algorithm as this class did
 *
 * @version $Id$
 */
// 中文注释：
// 主要功能：LogSource 是一个工厂类，用于创建 Log 实例，应用程序通过 makeNewLogInstance() 方法创建配置的 Log 实现类实例。
// 默认算法：
// 1. 如果 Log4J 可用，返回 Log4JLogger 实例。
// 2. 如果 JDK 1.4 或更高版本可用，返回 Jdk14Logger 实例。
// 3. 否则，返回 NoOpLog 实例（无操作日志）。
// 配置修改方式：
// 1. 在启动命令行设置系统属性 org.apache.commons.logging.log，指定 Log 实现类的全限定名。
// 2. 在运行时调用 setLogImplementation() 方法。
// 注意事项：此类已标记为过时，推荐使用 LogFactory 类，其实现与本类算法相同。

public class LogSource {

    // ------------------------------------------------------- Class Attributes

    static protected Hashtable logs = new Hashtable();
    // 中文注释：定义一个静态 Hashtable，用于存储日志名称到 Log 实例的映射。
    // 用途：缓存已创建的 Log 实例，避免重复创建，提高性能。

    /** Is log4j available (in the current classpath) */
    static protected boolean log4jIsAvailable = false;
    // 中文注释：布尔变量，标记当前类路径中是否可用 Log4J。
    // 用途：决定是否使用 Log4JLogger 作为日志实现。

    /** Is JDK 1.4 logging available */
    static protected boolean jdk14IsAvailable = false;
    // 中文注释：布尔变量，标记是否可用 JDK 1.4 的日志功能。
    // 用途：决定是否使用 Jdk14Logger 作为日志实现。

    /** Constructor for current log class */
    static protected Constructor logImplctor = null;
    // 中文注释：存储当前日志实现类的构造函数。
    // 用途：用于动态创建 Log 实例，构造函数需接受单一 String 参数（日志名称）。

    // ----------------------------------------------------- Class Initializers

    static {
        // 中文注释：静态初始化块，初始化日志系统的配置。
        // 主要功能：检测 Log4J 和 JDK 1.4 日志的可用性，并设置默认的 Log 实现类。

        // Is Log4J Available?
        try {
            log4jIsAvailable = null != Class.forName("org.apache.log4j.Logger");
        } catch (Throwable t) {
            log4jIsAvailable = false;
        }
        // 中文注释：检查 Log4J 是否在类路径中可用。
        // 关键步骤：尝试加载 org.apache.log4j.Logger 类，若成功则 log4jIsAvailable 设为 true，否则为 false。
        // 注意事项：捕获所有异常以确保初始化过程不中断。

        // Is JDK 1.4 Logging Available?
        try {
            jdk14IsAvailable = null != Class.forName("java.util.logging.Logger") &&
                               null != Class.forName("org.apache.commons.logging.impl.Jdk14Logger");
        } catch (Throwable t) {
            jdk14IsAvailable = false;
        }
        // 中文注释：检查 JDK 1.4 日志功能是否可用。
        // 关键步骤：尝试加载 java.util.logging.Logger 和 Jdk14Logger 类，若均成功则 jdk14IsAvailable 设为 true。
        // 注意事项：需同时确认两个类都存在，避免部分依赖缺失。

        // Set the default Log implementation
        String name = null;
        try {
            name = System.getProperty("org.apache.commons.logging.log");
            if (name == null) {
                name = System.getProperty("org.apache.commons.logging.Log");
            }
        } catch (Throwable t) {
        }
        // 中文注释：尝试获取系统属性 org.apache.commons.logging.log 或 org.apache.commons.logging.Log 指定的日志实现类名。
        // 用途：允许用户通过系统属性自定义日志实现。
        // 注意事项：捕获异常以确保初始化继续进行。

        if (name != null) {
            try {
                setLogImplementation(name);
            } catch (Throwable t) {
                try {
                    setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
                } catch (Throwable u) {
                    // ignored
                }
            }
        } else {
            try {
                if (log4jIsAvailable) {
                    setLogImplementation("org.apache.commons.logging.impl.Log4JLogger");
                } else if (jdk14IsAvailable) {
                    setLogImplementation("org.apache.commons.logging.impl.Jdk14Logger");
                } else {
                    setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
                }
            } catch (Throwable t) {
                try {
                    setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
                } catch (Throwable u) {
                    // ignored
                }
            }
        }
        // 中文注释：设置默认日志实现。
        // 逻辑说明：
        // 1. 如果系统属性指定了日志实现类名，则尝试使用该类。
        // 2. 否则根据可用性优先选择 Log4JLogger、Jdk14Logger 或 NoOpLog。
        // 3. 如果设置失败，回退到 NoOpLog（无操作日志）。
        // 注意事项：多层异常捕获确保初始化过程健壮，失败时使用 NoOpLog 作为默认实现。
    }

    // ------------------------------------------------------------ Constructor

    /** Don't allow others to create instances. */
    private LogSource() {
    }
    // 中文注释：私有构造函数，禁止外部实例化。
    // 用途：确保 LogSource 作为工厂类只能通过静态方法使用，防止直接创建对象。

    // ---------------------------------------------------------- Class Methods

    /**
     * Set the log implementation/log implementation factory
     * by the name of the class.  The given class must implement {@link Log},
     * and provide a constructor that takes a single {@link String} argument
     * (containing the name of the log).
     */
    static public void setLogImplementation(String classname)
        throws LinkageError, NoSuchMethodException, SecurityException, ClassNotFoundException {
        try {
            // Class.forName 是 Java 中 java.lang.Class 类的一个静态方法，用于通过类的全限定名（fully qualified name）动态加载类。它是 Java 反射机制的重要组成部分，允许在运行时根据字符串名称获取类的 Class 对象。
            Class logclass = Class.forName(classname);
            Class[] argtypes = new Class[1];
            // 中文注释：定义一个长度为 1 的 Class 数组，用于存储构造函数的参数类型。
            // 用途：指定日志实现类构造函数的参数类型（String 类型）。
            // 注意事项：数组长度固定为 1，因为日志实现类的构造函数需接受单一 String 参数（日志名称）。

            argtypes[0] = "".getClass();
            // 中文注释：将 String 类的 Class 对象赋值给 argtypes 数组的第一个元素。
            // 功能说明：通过空字符串的 getClass() 方法获取 java.lang.String 的 Class 对象。
            // 用途：表示日志实现类构造函数的第一个参数类型为 String。
            // 注意事项：使用空字符串 "".getClass() 是一种简洁的方式获取 String 类的 Class 对象。

            logImplctor = logclass.getConstructor(argtypes);
            // 中文注释：获取指定日志实现类的构造函数，并赋值给 logImplctor。
            // 功能说明：通过反射从 logclass 中获取接受单一 String 参数的构造函数。
            // 参数说明：
            //   - logclass：日志实现类的 Class 对象，必须实现 Log 接口。
            //   - argtypes：构造函数参数类型数组，指定 String 类型。
            // 用途：logImplctor 用于后续动态创建 Log 实例。
            // 异常处理：可能抛出 NoSuchMethodException（无匹配构造函数）或 SecurityException（安全限制）。
            // 注意事项：确保 logclass 具有接受 String 参数的公共构造函数，否则会抛出异常。
        } catch (Throwable t) {
            logImplctor = null;
        }
    }
    // 中文注释：通过类名设置日志实现。
    // 功能说明：动态加载指定的日志实现类，并获取其接受单一 String 参数的构造函数。
    // 参数说明：classname - 日志实现类的全限定名，必须实现 Log 接口。
    // 异常处理：捕获所有异常，若失败则将 logImplctor 置为 null。
    // 注意事项：指定的类必须有接受 String 参数的构造函数，否则会抛出 NoSuchMethodException。

    /**
     * Set the log implementation/log implementation factory by class.
     * The given class must implement {@link Log}, and provide a constructor
     * that takes a single {@link String} argument (containing the name of the log).
     */
    static public void setLogImplementation(Class logclass)
        throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException {
        Class[] argtypes = new Class[1];
        argtypes[0] = "".getClass();
        logImplctor = logclass.getConstructor(argtypes);
    }
    // 中文注释：通过 Class 对象设置日志实现。
    // 功能说明：直接使用提供的 Class 对象，获取其接受单一 String 参数的构造函数。
    // 参数说明：logclass - 实现 Log 接口的类。
    // 异常处理：可能抛出 NoSuchMethodException（无合适构造函数）或 SecurityException（安全限制）。
    // 注意事项：与 setLogImplementation(String) 类似，但直接使用 Class 对象，效率更高。

    /** Get a <code>Log</code> instance by class name. */
    static public Log getInstance(String name) {
        Log log = (Log) logs.get(name);
        if (null == log) {
            log = makeNewLogInstance(name);
            logs.put(name, log);
        }
        return log;
    }
    // 中文注释：根据日志名称获取 Log 实例。
    // 功能说明：从 logs 缓存中查找指定名称的 Log 实例，若不存在则创建并缓存。
    // 参数说明：name - 日志名称（或类别）。
    // 交互逻辑：先检查缓存，若无则调用 makeNewLogInstance 创建新实例并存入缓存。
    // 注意事项：使用缓存避免重复创建，提高性能。

    /** Get a <code>Log</code> instance by class. */
    static public Log getInstance(Class clazz) {
        return getInstance(clazz.getName());
    }
    // 中文注释：根据类对象获取 Log 实例。
    // 功能说明：通过类的全限定名调用 getInstance(String) 获取日志实例。
    // 参数说明：clazz - 需要日志的类。
    // 用途：便于以类名为日志名称，简化日志获取。

    /**
     * Create a new {@link Log} implementation, based on the given <i>name</i>.
     * <p>
     * The specific {@link Log} implementation returned is determined by the
     * value of the <tt>org.apache.commons.logging.log</tt> property. The value
     * of <tt>org.apache.commons.logging.log</tt> may be set to the fully specified
     * name of a class that implements the {@link Log} interface. This class must
     * also have a public constructor that takes a single {@link String} argument
     * (containing the <i>name</i> of the {@link Log} to be constructed.
     * <p>
     * When <tt>org.apache.commons.logging.log</tt> is not set, or when no corresponding
     * class can be found, this method will return a Log4JLogger if the log4j Logger
     * class is available in the {@link LogSource}'s classpath, or a Jdk14Logger if we
     * are on a JDK 1.4 or later system, or NoOpLog if neither of the above conditions is true.
     *
     * @param name the log name (or category)
     */
    static public Log makeNewLogInstance(String name) {
        Log log;
        try {
            Object[] args = { name };
            log = (Log) logImplctor.newInstance(args);
        } catch (Throwable t) {
            log = null;
        }
        if (null == log) {
            log = new NoOpLog(name);
        }
        return log;
    }
    // 中文注释：创建新的 Log 实例。
    // 功能说明：根据配置的日志实现类构造函数创建 Log 实例，若失败则回退到 NoOpLog。
    // 参数说明：name - 日志名称（或类别）。
    // 交互逻辑：使用 logImplctor 构造函数动态创建实例，若失败则使用 NoOpLog。
    // 注意事项：确保 logImplctor 已正确设置，否则会使用默认无操作日志。

    /**
     * Returns a {@link String} array containing the names of
     * all logs known to me.
     */
    static public String[] getLogNames() {
        return (String[]) logs.keySet().toArray(new String[logs.size()]);
    }
    // 中文注释：返回所有已知日志名称的数组。
    // 功能说明：从 logs 缓存中提取所有日志名称并返回。
    // 用途：便于外部查询当前缓存的日志名称列表。
}
