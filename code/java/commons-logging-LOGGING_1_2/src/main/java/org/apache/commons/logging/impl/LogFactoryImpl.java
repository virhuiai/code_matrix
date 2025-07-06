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

package org.apache.commons.logging.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete subclass of {@link LogFactory} that implements the
 * following algorithm to dynamically select a logging implementation
 * class to instantiate a wrapper for:
 * <ul>
 * <li>Use a factory configuration attribute named
 *     <code>org.apache.commons.logging.Log</code> to identify the
 *     requested implementation class.</li>
 * <li>Use the <code>org.apache.commons.logging.Log</code> system property
 *     to identify the requested implementation class.</li>
 * <li>If <em>Log4J</em> is available, return an instance of
 *     <code>org.apache.commons.logging.impl.Log4JLogger</code>.</li>
 * <li>If <em>JDK 1.4 or later</em> is available, return an instance of
 *     <code>org.apache.commons.logging.impl.Jdk14Logger</code>.</li>
 * <li>Otherwise, return an instance of
 *     <code>org.apache.commons.logging.impl.SimpleLog</code>.</li>
 * </ul>
 * <p>
 * If the selected {@link Log} implementation class has a
 * <code>setLogFactory()</code> method that accepts a {@link LogFactory}
 * parameter, this method will be called on each newly created instance
 * to identify the associated factory.  This makes factory configuration
 * attributes available to the Log instance, if it so desires.
 * <p>
 * This factory will remember previously created <code>Log</code> instances
 * for the same name, and will return them on repeated requests to the
 * <code>getInstance()</code> method.
 *
 * @version $Id$
 */
/*
 * LogFactoryImpl 是 LogFactory 的具体子类，实现了动态选择日志实现类的算法：
 * 1. 通过工厂配置属性 org.apache.commons.logging.Log 指定实现类。
 * 2. 通过系统属性 org.apache.commons.logging.Log 指定实现类。
 * 3. 如果 Log4J 可用，返回 Log4JLogger 实例。
 * 4. 如果 JDK 1.4 或更高版本可用，返回 Jdk14Logger 实例。
 * 5. 否则，返回 SimpleLog 实例。
 * 如果选定的 Log 实现类有 setLogFactory 方法，则会在每个新实例上调用以设置关联的工厂。
 * 该工厂会缓存已创建的 Log 实例，避免重复创建。
 */
public class LogFactoryImpl extends LogFactory {

    /**
     * Log4JLogger class name
     */
    // Log4JLogger 类名常量
    private static final String LOGGING_IMPL_LOG4J_LOGGER = "org.apache.commons.logging.impl.Log4JLogger";
    // 定义 Log4JLogger 的全限定类名，用于动态加载

    /**
     * Jdk14Logger class name
     */
    // Jdk14Logger 类名常量
    private static final String LOGGING_IMPL_JDK14_LOGGER = "org.apache.commons.logging.impl.Jdk14Logger";
    // 定义 Jdk14Logger 的全限定类名，用于动态加载

    /**
     * Jdk13LumberjackLogger class name
     */
    // Jdk13LumberjackLogger 类名常量
    private static final String LOGGING_IMPL_LUMBERJACK_LOGGER =
            "org.apache.commons.logging.impl.Jdk13LumberjackLogger";
    // 定义 Jdk13LumberjackLogger 的全限定类名，用于动态加载

    /**
     * SimpleLog class name
     */
    // SimpleLog 类名常量
    private static final String LOGGING_IMPL_SIMPLE_LOGGER = "org.apache.commons.logging.impl.SimpleLog";
    // 定义 SimpleLog 的全限定类名，作为默认日志实现

    private static final String PKG_IMPL = "org.apache.commons.logging.impl.";
    // 日志实现类的包名前缀
    private static final int PKG_LEN = PKG_IMPL.length();
    // 包名前缀长度，用于字符串比较

    // ----------------------------------------------------------- Constructors

    /**
     * Public no-arguments constructor required by the lookup mechanism.
     */
    /*
     * 无参构造函数，供查找机制调用，初始化诊断日志并记录实例创建。
     */
    public LogFactoryImpl() {
        super();
        initDiagnostics();  // method on this object
        // 初始化诊断日志功能
        if (isDiagnosticsEnabled()) {
            // 如果启用了诊断日志
            logDiagnostic("Instance created.");
            // 记录日志工厂实例创建的诊断信息
        }
    }

    // ----------------------------------------------------- Manifest Constants

    /**
     * The name (<code>org.apache.commons.logging.Log</code>) of the system
     * property identifying our {@link Log} implementation class.
     */
    /*
     * 系统属性名，用于指定 Log 实现类。
     */
    public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";
    // 定义系统属性名，用于配置日志实现类

    /**
     * The deprecated system property used for backwards compatibility with
     * old versions of JCL.
     */
    /*
     * 已废弃的系统属性名，用于向后兼容旧版 JCL。
     */
    protected static final String LOG_PROPERTY_OLD = "org.apache.commons.logging.log";
    // 定义旧版系统属性名，保持兼容性

    /**
     * The name (<code>org.apache.commons.logging.Log.allowFlawedContext</code>)
     * of the system property which can be set true/false to
     * determine system behaviour when a bad context-classloader is encountered.
     * When set to false, a LogConfigurationException is thrown if
     * LogFactoryImpl is loaded via a child classloader of the TCCL (this
     * should never happen in sane systems).
     * <p>
     * Default behaviour: true (tolerates bad context classloaders)
     * <p>
     * See also method setAttribute.
     */
    /*
     * 系统属性名，控制遇到错误上下文类加载器时的行为。
     * 默认值：true（容忍错误的上下文类加载器）。
     * 如果设为 false，加载器异常时抛出 LogConfigurationException。
     */
    public static final String ALLOW_FLAWED_CONTEXT_PROPERTY =
            "org.apache.commons.logging.Log.allowFlawedContext";
    // 配置是否容忍上下文类加载器问题

    /**
     * The name (<code>org.apache.commons.logging.Log.allowFlawedDiscovery</code>)
     * of the system property which can be set true/false to
     * determine system behaviour when a bad logging adapter class is
     * encountered during logging discovery. When set to false, an
     * exception will be thrown and the app will fail to start. When set
     * to true, discovery will continue (though the user might end up
     * with a different logging implementation than they expected).
     * <p>
     * Default behaviour: true (tolerates bad logging adapters)
     * <p>
     * See also method setAttribute.
     */
    /*
     * 系统属性名，控制日志发现过程中遇到错误适配器类的行为。
     * 默认值：true（容忍错误日志适配器，继续发现）。
     * 如果设为 false，遇到错误适配器时抛出异常并停止应用。
     */
    public static final String ALLOW_FLAWED_DISCOVERY_PROPERTY =
            "org.apache.commons.logging.Log.allowFlawedDiscovery";
    // 配置是否容忍日志适配器发现问题

    /**
     * The name (<code>org.apache.commons.logging.Log.allowFlawedHierarchy</code>)
     * of the system property which can be set true/false to
     * determine system behaviour when a logging adapter class is
     * encountered which has bound to the wrong Log class implementation.
     * When set to false, an exception will be thrown and the app will fail
     * to start. When set to true, discovery will continue (though the user
     * might end up with a different logging implementation than they expected).
     * <p>
     * Default behaviour: true (tolerates bad Log class hierarchy)
     * <p>
     * See also method setAttribute.
     */
    /*
     * 系统属性名，控制日志适配器绑定错误 Log 类时的行为。
     * 默认值：true（容忍错误的类层次结构，继续发现）。
     * 如果设为 false，遇到错误层次结构时抛出异常并停止应用。
     */
    public static final String ALLOW_FLAWED_HIERARCHY_PROPERTY =
            "org.apache.commons.logging.Log.allowFlawedHierarchy";
    // 配置是否容忍日志类层次结构问题

    /**
     * The names of classes that will be tried (in order) as logging
     * adapters. Each class is expected to implement the Log interface,
     * and to throw NoClassDefFound or ExceptionInInitializerError when
     * loaded if the underlying logging library is not available. Any
     * other error indicates that the underlying logging library is available
     * but broken/unusable for some reason.
     */
    /*
     * 日志适配器类的名称列表，按顺序尝试加载。
     * 每个类需实现 Log 接口，若底层日志库不可用，抛出 NoClassDefFound 或 ExceptionInInitializerError。
     * 其他错误表示日志库存在但不可用。
     */
    private static final String[] classesToDiscover = {
            LOGGING_IMPL_LOG4J_LOGGER,
            "org.apache.commons.logging.impl.Jdk14Logger",
            "org.apache.commons.logging.impl.Jdk13LumberjackLogger",
            "org.apache.commons.logging.impl.SimpleLog"
    };
    // 日志适配器类名数组，按顺序尝试实例化

    // ----------------------------------------------------- Instance Variables

    /**
     * Determines whether logging classes should be loaded using the thread-context
     * classloader, or via the classloader that loaded this LogFactoryImpl class.
     */
    /*
     * 决定是否使用线程上下文类加载器或 LogFactoryImpl 的类加载器加载日志类。
     */
    private boolean useTCCL = true;
    // 控制类加载器选择，默认为 true（使用线程上下文类加载器）

    /**
     * The string prefixed to every message output by the logDiagnostic method.
     */
    /*
     * logDiagnostic 方法输出的每条消息的前缀字符串。
     */
    private String diagnosticPrefix;
    // 日志诊断消息的前缀，用于标识消息来源

    /**
     * Configuration attributes.
     */
    /*
     * 配置属性存储。
     */
    protected Hashtable attributes = new Hashtable();
    // 用于存储工厂的配置属性，键值对形式

    /**
     * The {@link org.apache.commons.logging.Log} instances that have
     * already been created, keyed by logger name.
     */
    /*
     * 已创建的 Log 实例，以日志名称为键存储。
     */
    protected Hashtable instances = new Hashtable();
    // 缓存已创建的 Log 实例，避免重复创建

    /**
     * Name of the class implementing the Log interface.
     */
    /*
     * 实现 Log 接口的类名。
     */
    private String logClassName;
    // 记录当前使用的日志实现类名

    /**
     * The one-argument constructor of the
     * {@link org.apache.commons.logging.Log}
     * implementation class that will be used to create new instances.
     * This value is initialized by <code>getLogConstructor()</code>,
     * and then returned repeatedly.
     */
    /*
     * 用于创建新 Log 实例的单参数构造函数。
     * 由 getLogConstructor 方法初始化，重复返回。
     */
    protected Constructor logConstructor = null;
    // 缓存 Log 实现类的构造函数

    /**
     * The signature of the Constructor to be used.
     */
    /*
     * 构造函数的签名，接受一个 String 参数。
     */
    protected Class logConstructorSignature[] = {java.lang.String.class};
    // 定义构造函数的参数类型签名

    /**
     * The one-argument <code>setLogFactory</code> method of the selected
     * {@link org.apache.commons.logging.Log} method, if it exists.
     */
    /*
     * 选定 Log 实现类的 setLogFactory 方法（如果存在）。
     */
    protected Method logMethod = null;
    // 缓存 setLogFactory 方法，用于设置 LogFactory

    /**
     * The signature of the <code>setLogFactory</code> method to be used.
     */
    /*
     * setLogFactory 方法的签名，接受 LogFactory 参数。
     */
    protected Class logMethodSignature[] = {LogFactory.class};
    // 定义 setLogFactory 方法的参数类型签名

    /**
     * See getBaseClassLoader and initConfiguration.
     */
    /*
     * 控制是否容忍错误的上下文类加载器。
     */
    private boolean allowFlawedContext;
    // 是否允许错误的上下文类加载器，影响类加载行为

    /**
     * See handleFlawedDiscovery and initConfiguration.
     */
    /*
     * 控制是否容忍日志发现过程中的错误。
     */
    private boolean allowFlawedDiscovery;
    // 是否允许日志适配器发现错误，影响发现过程

    /**
     * See handleFlawedHierarchy and initConfiguration.
     */
    /*
     * 控制是否容忍错误的日志类层次结构。
     */
    private boolean allowFlawedHierarchy;
    // 是否允许日志类层次结构错误，影响发现过程

    // --------------------------------------------------------- Public Methods

    /**
     * Return the configuration attribute with the specified name (if any),
     * or <code>null</code> if there is no such attribute.
     *
     * @param name Name of the attribute to return
     */
    /*
     * 获取指定名称的配置属性，若不存在返回 null。
     * @param name 属性名称
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
        // 从 attributes 哈希表中获取指定属性的值
    }

    /**
     * Return an array containing the names of all currently defined
     * configuration attributes.  If there are no such attributes, a zero
     * length array is returned.
     */
    /*
     * 返回包含所有当前定义的配置属性名称的数组。
     * 如果没有属性，返回空数组。
     */
    public String[] getAttributeNames() {
        return (String[]) attributes.keySet().toArray(new String[attributes.size()]);
        // 将 attributes 的键集合转换为字符串数组
    }

    /**
     * Convenience method to derive a name from the specified class and
     * call <code>getInstance(String)</code> with it.
     *
     * @param clazz Class for which a suitable Log name will be derived
     * @throws LogConfigurationException if a suitable <code>Log</code>
     *                                   instance cannot be returned
     */
    /*
     * 从指定类派生日志名称并调用 getInstance(String) 获取 Log 实例。
     * @param clazz 用于派生日志名称的类
     * @throws LogConfigurationException 如果无法返回合适的 Log 实例
     */
    public Log getInstance(Class clazz) throws LogConfigurationException {
        return getInstance(clazz.getName());
        // 使用类的全限定名调用 getInstance 方法
    }

    /**
     * <p>Construct (if necessary) and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.</p>
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *             returned (the meaning of this name is only known to the underlying
     *             logging implementation that is being wrapped)
     * @throws LogConfigurationException if a suitable <code>Log</code>
     *                                   instance cannot be returned
     */
    /*
     * 根据工厂配置属性构造并返回 Log 实例。
     * 注意：返回的 Log 实例可能与当前应用无关，重复调用可能返回不同实例。
     * @param name 要返回的 Log 实例的逻辑名称
     * @throws LogConfigurationException 如果无法返回合适的 Log 实例
     */
    public Log getInstance(String name) throws LogConfigurationException {
        Log instance = (Log) instances.get(name);
        if (instance == null) {
            instance = newInstance(name);
            instances.put(name, instance);
        }
        return instance;
        // 检查缓存中是否存在指定名称的 Log 实例
        // 如果不存在，创建新实例并缓存
        // 返回 Log 实例
    }

    /**
     * Release any internal references to previously created
     * {@link org.apache.commons.logging.Log}
     * instances returned by this factory.  This is useful in environments
     * like servlet containers, which implement application reloading by
     * throwing away a ClassLoader.  Dangling references to objects in that
     * class loader would prevent garbage collection.
     */
    /*
     * 释放工厂持有的所有 Log 实例引用。
     * 在需要重新加载应用的场景（如 servlet 容器）中，避免类加载器引用导致垃圾回收失败。
     */
    public void release() {

        logDiagnostic("Releasing all known loggers");
        // 记录释放所有日志实例的诊断信息
        instances.clear();
        // 清空缓存的 Log 实例
    }

    /**
     * Remove any configuration attribute associated with the specified name.
     * If there is no such attribute, no action is taken.
     *
     * @param name Name of the attribute to remove
     */
    /*
     * 删除指定名称的配置属性，若不存在则不执行操作。
     * @param name 要删除的属性名称
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
        // 从 attributes 哈希表中移除指定属性
    }

    /**
     * Set the configuration attribute with the specified name.  Calling
     * this with a <code>null</code> value is equivalent to calling
     * <code>removeAttribute(name)</code>.
     * <p>
     * This method can be used to set logging configuration programmatically
     * rather than via system properties. It can also be used in code running
     * within a container (such as a webapp) to configure behaviour on a
     * per-component level instead of globally as system properties would do.
     * To use this method instead of a system property, call
     * <pre>
     * LogFactory.getFactory().setAttribute(...)
     * </pre>
     * This must be done before the first Log object is created; configuration
     * changes after that point will be ignored.
     * <p>
     * This method is also called automatically if LogFactory detects a
     * commons-logging.properties file; every entry in that file is set
     * automatically as an attribute here.
     *
     * @param name  Name of the attribute to set
     * @param value Value of the attribute to set, or <code>null</code>
     *              to remove any setting for this attribute
     */
    /*
     * 设置指定名称的配置属性，值为 null 等同于 removeAttribute(name)。
     * 可通过编程方式配置日志行为，替代系统属性。
     * 在容器（如 webapp）中可按组件级别配置。
     * 必须在创建第一个 Log 对象前调用，之后配置更改将被忽略。
     * 如果检测到 commons-logging.properties 文件，会自动设置属性。
     * @param name 要设置的属性名称
     * @param value 要设置的属性值，null 表示移除
     */
    public void setAttribute(String name, Object value) {
        if (logConstructor != null) {
            logDiagnostic("setAttribute: call too late; configuration already performed.");
            // 如果已初始化构造函数，记录配置已完成的诊断信息
        }

        if (value == null) {
            attributes.remove(name);
            // 如果值为空，移除指定属性
        } else {
            attributes.put(name, value);
            // 否则，将属性和值存入 attributes 哈希表
        }

        if (name.equals(TCCL_KEY)) {
            useTCCL = value != null && Boolean.valueOf(value.toString()).booleanValue();
            // 如果设置的是 TCCL_KEY 属性，更新 useTCCL 变量，控制类加载器选择
        }
    }

    // ------------------------------------------------------
    // Static Methods
    //
    // These methods only defined as workarounds for a java 1.2 bug;
    // theoretically none of these are needed.
    // ------------------------------------------------------
    /*
     * 静态方法部分，仅为解决 Java 1.2 的 bug 提供变通方法，理论上不需要。
     */

    /**
     * Gets the context classloader.
     * This method is a workaround for a java 1.2 compiler bug.
     *
     * @since 1.1
     */
    /*
     * 获取上下文类加载器，解决 Java 1.2 编译器 bug。
     * @throws LogConfigurationException 如果获取类加载器失败
     */
    protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
        return LogFactory.getContextClassLoader();
        // 调用 LogFactory 的方法获取上下文类加载器
    }

    /**
     * Workaround for bug in Java1.2; in theory this method is not needed.
     * See LogFactory.isDiagnosticsEnabled.
     */
    /*
     * 检查是否启用诊断日志，解决 Java 1.2 bug。
     */
    protected static boolean isDiagnosticsEnabled() {
        return LogFactory.isDiagnosticsEnabled();
        // 调用 LogFactory 方法检查诊断日志是否启用
    }

    /**
     * Workaround for bug in Java1.2; in theory this method is not needed.
     * See LogFactory.getClassLoader.
     *
     * @since 1.1
     */
    /*
     * 获取指定类的类加载器，解决 Java 1.2 bug。
     * @param clazz 要获取类加载器的类
     */
    protected static ClassLoader getClassLoader(Class clazz) {
        return LogFactory.getClassLoader(clazz);
        // 调用 LogFactory 方法获取指定类的类加载器
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Calculate and cache a string that uniquely identifies this instance,
     * including which classloader the object was loaded from.
     * <p>
     * This string will later be prefixed to each "internal logging" message
     * emitted, so that users can clearly see any unexpected behaviour.
     * <p>
     * Note that this method does not detect whether internal logging is
     * enabled or not, nor where to output stuff if it is; that is all
     * handled by the parent LogFactory class. This method just computes
     * its own unique prefix for log messages.
     */
    /*
     * 计算并缓存唯一标识此实例的字符串，包括加载此对象的类加载器。
     * 该字符串将作为内部日志消息的前缀，方便用户识别异常行为。
     * 不负责检查日志是否启用或输出位置，仅计算前缀。
     */
    private void initDiagnostics() {
        // It would be nice to include an identifier of the context classloader
        // that this LogFactoryImpl object is responsible for. However that
        // isn't possible as that information isn't available. It is possible
        // to figure this out by looking at the logging from LogFactory to
        // see the context & impl ids from when this object was instantiated,
        // in order to link the impl id output as this object's prefix back to
        // the context it is intended to manage.
        // Note that this prefix should be kept consistent with that
        // in LogFactory.
        /*
         * 希望包含上下文类加载器的标识，但此信息不可用。
         * 可通过 LogFactory 的日志查看上下文和实现 ID。
         * 前缀需与 LogFactory 保持一致。
         */
        Class clazz = this.getClass();
        ClassLoader classLoader = getClassLoader(clazz);
        String classLoaderName;
        try {
            if (classLoader == null) {
                classLoaderName = "BOOTLOADER";
                // 如果类加载器为空，使用 "BOOTLOADER" 作为名称
            } else {
                classLoaderName = objectId(classLoader);
                // 获取类加载器的唯一标识
            }
        } catch (SecurityException e) {
            classLoaderName = "UNKNOWN";
            // 安全异常时，使用 "UNKNOWN" 作为名称
        }
        diagnosticPrefix = "[LogFactoryImpl@" + System.identityHashCode(this) + " from " + classLoaderName + "] ";
        // 设置诊断消息前缀，包含实例 ID 和类加载器名称
    }

    /**
     * Output a diagnostic message to a user-specified destination (if the
     * user has enabled diagnostic logging).
     *
     * @param msg diagnostic message
     * @since 1.1
     */
    /*
     * 将诊断消息输出到用户指定目标（如果启用了诊断日志）。
     * @param msg 诊断消息内容
     */
    protected void logDiagnostic(String msg) {
        if (isDiagnosticsEnabled()) {
            // 如果诊断日志启用
            logRawDiagnostic(diagnosticPrefix + msg);
            // 输出带前缀的诊断消息
        }
    }

    /**
     * Return the fully qualified Java classname of the {@link Log}
     * implementation we will be using.
     *
     * @deprecated Never invoked by this class; subclasses should not assume
     * it will be.
     */
    /*
     * 返回当前使用的 Log 实现类的全限定类名。
     * @deprecated 本类不再调用，子类不应依赖。
     */
    protected String getLogClassName() {
        if (logClassName == null) {
            discoverLogImplementation(getClass().getName());
            // 如果未初始化类名，调用发现方法初始化
        }

        return logClassName;
        // 返回日志实现类名
    }


    /**
     * <p>Return the <code>Constructor</code> that can be called to instantiate
     * new {@link org.apache.commons.logging.Log} instances.</p>
     *
     * <p><strong>IMPLEMENTATION NOTE</strong> - Race conditions caused by
     * calling this method from more than one thread are ignored, because
     * the same <code>Constructor</code> instance will ultimately be derived
     * in all circumstances.</p>
     *
     * @throws LogConfigurationException if a suitable constructor
     *                                   cannot be returned
     * @deprecated Never invoked by this class; subclasses should not assume
     * it will be.
     */
    /*
     * 返回用于实例化 Log 对象的构造函数。
     * 注意：忽略多线程调用导致的竞争条件，最终返回相同的构造函数。
     * @throws LogConfigurationException 如果无法返回合适的构造函数
     * @deprecated 本类不再调用，子类不应依赖。
     */
    protected Constructor getLogConstructor()
            throws LogConfigurationException {

        // Return the previously identified Constructor (if any)
        if (logConstructor == null) {
            discoverLogImplementation(getClass().getName());
            // 如果构造函数未初始化，调用发现方法初始化
        }

        return logConstructor;
        // 返回缓存的构造函数
    }

    /**
     * Is <em>JDK 1.3 with Lumberjack</em> logging available?
     *
     * @deprecated Never invoked by this class; subclasses should not assume
     * it will be.
     */
    /*
     * 检查 JDK 1.3 Lumberjack 日志是否可用。
     * @deprecated 本类不再调用，子类不应依赖。
     */
    protected boolean isJdk13LumberjackAvailable() {
        return isLogLibraryAvailable(
                "Jdk13Lumberjack",
                "org.apache.commons.logging.impl.Jdk13LumberjackLogger");
        // 检查指定日志库是否可用
    }

    /**
     * Return <code>true</code> if <em>JDK 1.4 or later</em> logging
     * is available.  Also checks that the <code>Throwable</code> class
     * supports <code>getStackTrace()</code>, which is required by
     * Jdk14Logger.
     *
     * @deprecated Never invoked by this class; subclasses should not assume
     * it will be.
     */
    /*
     * 检查 JDK 1.4 或更高版本日志是否可用，并验证 Throwable 类是否支持 getStackTrace。
     * @deprecated 本类不再调用，子类不应依赖。
     */
    protected boolean isJdk14Available() {
        return isLogLibraryAvailable(
                "Jdk14",
                "org.apache.commons.logging.impl.Jdk14Logger");
        // 检查指定日志库是否可用
    }

    /**
     * Is a <em>Log4J</em> implementation available?
     *
     * @deprecated Never invoked by this class; subclasses should not assume
     * it will be.
     */
    /*
     * 检查 Log4J 实现是否可用。
     * @deprecated 本类不再调用，子类不应依赖。
     */
    protected boolean isLog4JAvailable() {
        return isLogLibraryAvailable(
                "Log4J",
                LOGGING_IMPL_LOG4J_LOGGER);
        // 检查 Log4J 日志库是否可用
    }

    /**
     * Create and return a new {@link org.apache.commons.logging.Log}
     * instance for the specified name.
     *
     * @param name Name of the new logger
     * @throws LogConfigurationException if a new instance cannot
     *                                   be created
     */
    /*
     * 为指定名称创建并返回新的 Log 实例。
     * @param name 新日志的名称
     * @throws LogConfigurationException 如果无法创建新实例
     */
    protected Log newInstance(String name) throws LogConfigurationException {
        Log instance;
        try {
            if (logConstructor == null) {
                instance = discoverLogImplementation(name);
                // 如果构造函数未初始化，调用发现方法创建实例
            } else {
                Object params[] = {name};
                instance = (Log) logConstructor.newInstance(params);
                // 使用缓存的构造函数创建 Log 实例
            }

            if (logMethod != null) {
                Object params[] = {this};
                logMethod.invoke(instance, params);
                // 如果存在 setLogFactory 方法，调用以设置工厂
            }

            return instance;
            // 返回创建的 Log 实例

        } catch (LogConfigurationException lce) {

            // this type of exception means there was a problem in discovery
            // and we've already output diagnostics about the issue, etc.;
            // just pass it on
            /*
             * 捕获发现过程中的异常，已输出诊断信息，直接抛出。
             */
            throw lce;

        } catch (InvocationTargetException e) {
            // A problem occurred invoking the Constructor or Method
            // previously discovered
            /*
             * 调用构造函数或方法时发生异常。
             */
            Throwable c = e.getTargetException();
            throw new LogConfigurationException(c == null ? e : c);
            // 抛出目标异常或原始异常

        } catch (Throwable t) {
            handleThrowable(t); // may re-throw t
            // A problem occurred invoking the Constructor or Method
            // previously discovered
            /*
             * 处理调用构造函数或方法时的其他异常。
             */
            throw new LogConfigurationException(t);
            // 抛出异常
        }
    }

    //  ------------------------------------------------------ Private Methods

    /**
     * Calls LogFactory.directGetContextClassLoader under the control of an
     * AccessController class. This means that java code running under a
     * security manager that forbids access to ClassLoaders will still work
     * if this class is given appropriate privileges, even when the caller
     * doesn't have such privileges. Without using an AccessController, the
     * the entire call stack must have the privilege before the call is
     * allowed.
     *
     * @return the context classloader associated with the current thread,
     * or null if security doesn't allow it.
     * @throws LogConfigurationException if there was some weird error while
     *                                   attempting to get the context classloader.
     * @throws SecurityException         if the current java security policy doesn't
     *                                   allow this class to access the context classloader.
     */
    /*
     * 使用 AccessController 调用 LogFactory.directGetContextClassLoader。
     * 确保在安全管理器限制下仍能获取类加载器（如果有权限）。
     * @return 当前线程的上下文类加载器，若无权限返回 null
     * @throws LogConfigurationException 如果获取类加载器时发生错误
     * @throws SecurityException 如果安全策略禁止访问类加载器
     */
    private static ClassLoader getContextClassLoaderInternal()
            throws LogConfigurationException {
        return (ClassLoader) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return LogFactory.directGetContextClassLoader();
                        // 执行特权操作，获取上下文类加载器
                    }
                });
    }

    /**
     * Read the specified system property, using an AccessController so that
     * the property can be read if JCL has been granted the appropriate
     * security rights even if the calling code has not.
     * <p>
     * Take care not to expose the value returned by this method to the
     * calling application in any way; otherwise the calling app can use that
     * info to access data that should not be available to it.
     */
    /*
     * 使用 AccessController 读取指定系统属性。
     * 确保 JCL 有权限时可读取属性，避免调用者权限不足。
     * 注意：不要将返回值暴露给调用应用，以防访问未授权数据。
     * @param key 系统属性键
     * @param def 默认值
     * @return 系统属性值
     * @throws SecurityException 如果无权限访问系统属性
     */
    private static String getSystemProperty(final String key, final String def)
            throws SecurityException {
        return (String) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return System.getProperty(key, def);
                        // 执行特权操作，获取系统属性
                    }
                });
    }

    /**
     * Fetch the parent classloader of a specified classloader.
     * <p>
     * If a SecurityException occurs, null is returned.
     * <p>
     * Note that this method is non-static merely so logDiagnostic is available.
     */
    /*
     * 获取指定类加载器的父类加载器。
     * 如果发生 SecurityException，返回 null。
     * 注意：此方法非静态，以便使用 logDiagnostic。
     * @param cl 类加载器
     * @return 父类加载器或 null
     */
    private ClassLoader getParentClassLoader(final ClassLoader cl) {
        try {
            return (ClassLoader) AccessController.doPrivileged(
                    new PrivilegedAction() {
                        public Object run() {
                            return cl.getParent();
                            // 执行特权操作，获取父类加载器
                        }
                    });
        } catch (SecurityException ex) {
            logDiagnostic("[SECURITY] Unable to obtain parent classloader");
            // 记录无法获取父类加载器的诊断信息
            return null;
            // 安全异常时返回 null
        }

    }

    /**
     * Utility method to check whether a particular logging library is
     * present and available for use. Note that this does <i>not</i>
     * affect the future behaviour of this class.
     */
    private boolean isLogLibraryAvailable(String name, String classname) {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Checking for '" + name + "'.");
        }
        try {
            Log log = createLogFromClass(
                    classname,
                    this.getClass().getName(), // dummy category
                    false);

            if (log == null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("Did not find '" + name + "'.");
                }
                return false;
            } else {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("Found '" + name + "'.");
                }
                return true;
            }
        } catch (LogConfigurationException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Logging system '" + name + "' is available but not useable.");
            }
            return false;
        }
    }

    /**
     * Attempt to find an attribute (see method setAttribute) or a
     * system property with the provided name and return its value.
     * <p>
     * The attributes associated with this object are checked before
     * system properties in case someone has explicitly called setAttribute,
     * or a configuration property has been set in a commons-logging.properties
     * file.
     *
     * @return the value associated with the property, or null.
     */
    /*
     * 尝试查找指定名称的属性或系统属性并返回其值。
     * 优先检查对象属性（setAttribute 或 commons-logging.properties 设置），再检查系统属性。
     * @param property 属性名称
     * @return 属性值或 null
     */
    private String getConfigurationValue(String property) {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("[ENV] Trying to get configuration for item " + property);
            // 记录尝试获取配置的诊断信息
        }

        Object valueObj = getAttribute(property);
        if (valueObj != null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[ENV] Found LogFactory attribute [" + valueObj + "] for " + property);
                // 记录找到工厂属性的诊断信息
            }
            return valueObj.toString();
            // 返回属性值
        }

        if (isDiagnosticsEnabled()) {
            logDiagnostic("[ENV] No LogFactory attribute found for " + property);
            // 记录未找到工厂属性的诊断信息
        }

        try {
            // warning: minor security hole here, in that we potentially read a system
            // property that the caller cannot, then output it in readable form as a
            // diagnostic message. However it's only ever JCL-specific properties
            // involved here, so the harm is truly trivial.
            /*
             * 警告：此处存在轻微安全隐患，可能读取调用者无权限的系统属性并输出。
             * 但仅涉及 JCL 特定属性，危害较小。
             */
            String value = getSystemProperty(property, null);
            if (value != null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[ENV] Found system property [" + value + "] for " + property);
                    // 记录找到系统属性的诊断信息
                }
                return value;
                // 返回系统属性值
            }

            if (isDiagnosticsEnabled()) {
                logDiagnostic("[ENV] No system property found for property " + property);
                // 记录未找到系统属性的诊断信息
            }
        } catch (SecurityException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[ENV] Security prevented reading system property " + property);
                // 记录安全异常阻止读取系统属性的诊断信息
            }
        }

        if (isDiagnosticsEnabled()) {
            logDiagnostic("[ENV] No configuration defined for item " + property);
            // 记录未定义配置的诊断信息
        }

        return null;
        // 未找到配置，返回 null
    }

    /**
     * Get the setting for the user-configurable behaviour specified by key.
     * If nothing has explicitly been set, then return dflt.
     */
    /*
     * 获取指定键的用户配置行为设置。
     * 如果未显式设置，返回默认值。
     * @param key 配置键
     * @param dflt 默认值
     * @return 配置的布尔值
     */
    private boolean getBooleanConfiguration(String key, boolean dflt) {
        String val = getConfigurationValue(key);
        if (val == null) {
            return dflt;
            // 如果未找到配置，返回默认值
        }
        return Boolean.valueOf(val).booleanValue();
        // 将配置值转换为布尔值
    }

    /**
     * Initialize a number of variables that control the behaviour of this
     * class and that can be tweaked by the user. This is done when the first
     * logger is created, not in the constructor of this class, because we
     * need to give the user a chance to call method setAttribute in order to
     * configure this object.
     */
    /*
     * 初始化控制类行为的变量，在创建第一个日志时执行。
     * 不在构造函数中初始化，以允许用户通过 setAttribute 配置。
     */
    private void initConfiguration() {
        allowFlawedContext = getBooleanConfiguration(ALLOW_FLAWED_CONTEXT_PROPERTY, true);
        // 初始化是否容忍上下文类加载器问题，默认为 true
        allowFlawedDiscovery = getBooleanConfiguration(ALLOW_FLAWED_DISCOVERY_PROPERTY, true);
        // 初始化是否容忍日志发现问题，默认为 true
        allowFlawedHierarchy = getBooleanConfiguration(ALLOW_FLAWED_HIERARCHY_PROPERTY, true);
        // 初始化是否容忍日志类层次结构问题，默认为 true
    }

    /**
     * Attempts to create a Log instance for the given category name.
     * Follows the discovery process described in the class javadoc.
     *
     * @param logCategory the name of the log category
     * @throws LogConfigurationException if an error in discovery occurs,
     *                                   or if no adapter at all can be instantiated
     */
    /*
     * 为指定日志类别名称创建 Log 实例。
     * 遵循类文档描述的发现过程。
     * @param logCategory 日志类别名称
     * @throws LogConfigurationException 如果发现过程出错或无法实例化适配器
     */
    private Log discoverLogImplementation(String logCategory)
            throws LogConfigurationException {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Discovering a Log implementation...");
            // 记录开始日志实现发现的诊断信息
        }

        initConfiguration();
        // 初始化配置变量

        Log result = null;

        // See if the user specified the Log implementation to use
        String specifiedLogClassName = findUserSpecifiedLogClassName();
        // 检查用户是否指定了日志实现类

        if (specifiedLogClassName != null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Attempting to load user-specified log class '" +
                        specifiedLogClassName + "'...");
                // 记录尝试加载用户指定日志类的诊断信息
            }

            result = createLogFromClass(specifiedLogClassName,
                    logCategory,
                    true);
            // 尝试创建用户指定的日志类实例

            if (result == null) {
                StringBuffer messageBuffer = new StringBuffer("User-specified log class '");
                messageBuffer.append(specifiedLogClassName);
                messageBuffer.append("' cannot be found or is not useable.");
                // 构造错误消息，用户指定的日志类不可用

                // Mistyping or misspelling names is a common fault.
                // Construct a good error message, if we can
                /*
                 * 用户常因拼写错误导致失败，构造详细错误消息。
                 */
                informUponSimilarName(messageBuffer, specifiedLogClassName, LOGGING_IMPL_LOG4J_LOGGER);
                informUponSimilarName(messageBuffer, specifiedLogClassName, LOGGING_IMPL_JDK14_LOGGER);
                informUponSimilarName(messageBuffer, specifiedLogClassName, LOGGING_IMPL_LUMBERJACK_LOGGER);
                informUponSimilarName(messageBuffer, specifiedLogClassName, LOGGING_IMPL_SIMPLE_LOGGER);
                // 检查并提示可能的拼写错误
                throw new LogConfigurationException(messageBuffer.toString());
                // 抛出配置异常
            }

            return result;
            // 返回用户指定的日志实例
        }

        // No user specified log; try to discover what's on the classpath
        //
        // Note that we deliberately loop here over classesToDiscover and
        // expect method createLogFromClass to loop over the possible source
        // classloaders. The effect is:
        //   for each discoverable log adapter
        //      for each possible classloader
        //          see if it works
        //
        // It appears reasonable at first glance to do the opposite:
        //   for each possible classloader
        //     for each discoverable log adapter
        //        see if it works
        //
        // The latter certainly has advantages for user-installable logging
        // libraries such as log4j; in a webapp for example this code should
        // first check whether the user has provided any of the possible
        // logging libraries before looking in the parent classloader.
        // Unfortunately, however, Jdk14Logger will always work in jvm>=1.4,
        // and SimpleLog will always work in any JVM. So the loop would never
        // ever look for logging libraries in the parent classpath. Yet many
        // users would expect that putting log4j there would cause it to be
        // detected (and this is the historical JCL behaviour). So we go with
        // the first approach. A user that has bundled a specific logging lib
        // in a webapp should use a commons-logging.properties file or a
        // service file in META-INF to force use of that logging lib anyway,
        // rather than relying on discovery.
        /*
         * 未指定日志实现，尝试从类路径发现可用日志库。
         * 按 classesToDiscover 顺序遍历每个适配器，尝试所有类加载器。
         * 注意：优先遍历适配器而非类加载器，以符合历史 JCL 行为。
         * 用户应通过 commons-logging.properties 或 META-INF 服务文件强制指定日志库。
         */
        if (isDiagnosticsEnabled()) {
            logDiagnostic(
                    "No user-specified Log implementation; performing discovery" +
                            " using the standard supported logging implementations...");
            // 记录开始标准日志发现的诊断信息
        }
        for (int i = 0; i < classesToDiscover.length && result == null; ++i) {
            result = createLogFromClass(classesToDiscover[i], logCategory, true);
            // 按顺序尝试创建每个日志适配器实例
        }

        if (result == null) {
            throw new LogConfigurationException
                    ("No suitable Log implementation");
            // 如果未找到合适的日志实现，抛出异常
        }

        return result;
        // 返回发现的日志实例
    }

    /**
     * Appends message if the given name is similar to the candidate.
     *
     * @param messageBuffer <code>StringBuffer</code> the message should be appended to,
     *                      not null
     * @param name          the (trimmed) name to be test against the candidate, not null
     * @param candidate     the candidate name (not null)
     */
    /*
     * 如果给定名称与候选名称相似，追加错误提示消息。
     * @param messageBuffer 错误消息缓冲区
     * @param name 测试的名称
     * @param candidate 候选名称
     */
    private void informUponSimilarName(final StringBuffer messageBuffer, final String name,
                                       final String candidate) {
        if (name.equals(candidate)) {
            // Don't suggest a name that is exactly the same as the one the
            // user tried...
            /*
             * 不建议与用户输入完全相同的名称。
             */
            return;
        }

        // If the user provides a name that is in the right package, and gets
        // the first 5 characters of the adapter class right (ignoring case),
        // then suggest the candidate adapter class name.
        /*
         * 如果用户输入的名称在正确包中，且前 5 个字符（忽略大小写）与适配器类名匹配，建议候选类名。
         */
        if (name.regionMatches(true, 0, candidate, 0, PKG_LEN + 5)) {
            messageBuffer.append(" Did you mean '");
            messageBuffer.append(candidate);
            messageBuffer.append("'?");
            // 追加建议的候选类名
        }
    }

    /**
     * Checks system properties and the attribute map for
     * a Log implementation specified by the user under the
     * property names {@link #LOG_PROPERTY} or {@link #LOG_PROPERTY_OLD}.
     *
     * @return classname specified by the user, or <code>null</code>
     */
    /*
     * 检查系统属性和属性映射中用户指定的 Log 实现类名。
     * 检查 LOG_PROPERTY 和 LOG_PROPERTY_OLD 属性。
     * @return 用户指定的类名，或 null
     */
    private String findUserSpecifiedLogClassName() {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Trying to get log class from attribute '" + LOG_PROPERTY + "'");
            // 记录尝试从属性获取日志类名的诊断信息
        }
        String specifiedClass = (String) getAttribute(LOG_PROPERTY);
        // 检查 LOG_PROPERTY 属性

        if (specifiedClass == null) { // @deprecated
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Trying to get log class from attribute '" +
                        LOG_PROPERTY_OLD + "'");
                // 记录尝试从旧属性获取日志类名的诊断信息
            }
            specifiedClass = (String) getAttribute(LOG_PROPERTY_OLD);
            // 检查旧版 LOG_PROPERTY_OLD 属性
        }

        if (specifiedClass == null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Trying to get log class from system property '" +
                        LOG_PROPERTY + "'");
                // 记录尝试从系统属性获取日志类名的诊断信息
            }
            try {
                specifiedClass = getSystemProperty(LOG_PROPERTY, null);
                // 尝试读取系统属性 LOG_PROPERTY
            } catch (SecurityException e) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("No access allowed to system property '" +
                            LOG_PROPERTY + "' - " + e.getMessage());
                    // 记录无权限访问系统属性的诊断信息
                }
            }
        }

        if (specifiedClass == null) { // @deprecated
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Trying to get log class from system property '" +
                        LOG_PROPERTY_OLD + "'");
                // 记录尝试从旧系统属性获取日志类名的诊断信息
            }
            try {
                specifiedClass = getSystemProperty(LOG_PROPERTY_OLD, null);
                // 尝试读取旧系统属性 LOG_PROPERTY_OLD
            } catch (SecurityException e) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("No access allowed to system property '" +
                            LOG_PROPERTY_OLD + "' - " + e.getMessage());
                    // 记录无权限访问旧系统属性的诊断信息
                }
            }
        }

        // Remove any whitespace; it's never valid in a classname so its
        // presence just means a user mistake. As we know what they meant,
        // we may as well strip the spaces.
        /*
         * 移除类名中的空格，空格在类名中无效，视为用户错误。
         */
        if (specifiedClass != null) {
            specifiedClass = specifiedClass.trim();
            // 去除类名两端的空格
        }

        return specifiedClass;
        // 返回用户指定的类名
    }

    /**
     * Attempts to load the given class, find a suitable constructor,
     * and instantiate an instance of Log.
     *
     * @param logAdapterClassName classname of the Log implementation
     * @param logCategory         argument to pass to the Log implementation's constructor
     * @param affectState         <code>true</code> if this object's state should
     *                            be affected by this method call, <code>false</code> otherwise.
     * @return an instance of the given class, or null if the logging
     * library associated with the specified adapter is not available.
     * @throws LogConfigurationException if there was a serious error with
     *                                   configuration and the handleFlawedDiscovery method decided this
     *                                   problem was fatal.
     */
    /*
     * 尝试加载指定类，查找合适的构造函数并实例化 Log。
     * @param logAdapterClassName 日志实现类名
     * @param logCategory 传递给构造函数的日志类别
     * @param affectState 是否影响对象状态
     * @return Log 实例，若日志库不可用返回 null
     * @throws LogConfigurationException 如果配置错误且 handleFlawedDiscovery 认为问题严重
     */
    private Log createLogFromClass(String logAdapterClassName,
                                   String logCategory,
                                   boolean affectState)
            throws LogConfigurationException {

        if (isDiagnosticsEnabled()) {
            logDiagnostic("Attempting to instantiate '" + logAdapterClassName + "'");
            // 记录尝试实例化日志类的诊断信息
        }

        Object[] params = {logCategory};
        Log logAdapter = null;
        Constructor constructor = null;

        Class logAdapterClass = null;
        ClassLoader currentCL = getBaseClassLoader();
        // 获取基础类加载器

        for (; ; ) {
            // Loop through the classloader hierarchy trying to find
            // a viable classloader.
            /*
             * 遍历类加载器层次结构，寻找可用类加载器。
             */
            logDiagnostic("Trying to load '" + logAdapterClassName + "' from classloader " + objectId(currentCL));
            // 记录尝试从类加载器加载日志类的诊断信息
            try {
                if (isDiagnosticsEnabled()) {
                    // Show the location of the first occurrence of the .class file
                    // in the classpath. This is the location that ClassLoader.loadClass
                    // will load the class from -- unless the classloader is doing
                    // something weird.
                    /*
                     * 显示类路径中 .class 文件的首次出现位置。
                     * 这是 ClassLoader.loadClass 加载类的位置，除非类加载器行为异常。
                     */
                    URL url;
                    String resourceName = logAdapterClassName.replace('.', '/') + ".class";
                    if (currentCL != null) {
                        url = currentCL.getResource(resourceName);
                    } else {
                        url = ClassLoader.getSystemResource(resourceName + ".class");
                    }

                    if (url == null) {
                        logDiagnostic("Class '" + logAdapterClassName + "' [" + resourceName + "] cannot be found.");
                        // 记录无法找到类的诊断信息
                    } else {
                        logDiagnostic("Class '" + logAdapterClassName + "' was found at '" + url + "'");
                        // 记录找到类的位置的诊断信息
                    }
                }

                Class c;
                try {
                    c = Class.forName(logAdapterClassName, true, currentCL);
                    // 尝试使用当前类加载器加载类
                } catch (ClassNotFoundException originalClassNotFoundException) {
                    // The current classloader was unable to find the log adapter
                    // in this or any ancestor classloader. There's no point in
                    // trying higher up in the hierarchy in this case..
                    /*
                     * 当前类加载器无法找到日志适配器类，无需继续尝试上层类加载器。
                     */
                    String msg = originalClassNotFoundException.getMessage();
                    logDiagnostic("The log adapter '" + logAdapterClassName + "' is not available via classloader " +
                            objectId(currentCL) + ": " + msg.trim());
                    // 记录类不可用的诊断信息
                    try {
                        // Try the class classloader.
                        // This may work in cases where the TCCL
                        // does not contain the code executed or JCL.
                        // This behaviour indicates that the application
                        // classloading strategy is not consistent with the
                        // Java 1.2 classloading guidelines but JCL can
                        // and so should handle this case.
                        /*
                         * 尝试使用类加载器加载类，处理 TCCL 不包含代码的情况。
                         * 此行为不符合 Java 1.2 类加载指南，但 JCL 应处理。
                         */
                        c = Class.forName(logAdapterClassName);
                    } catch (ClassNotFoundException secondaryClassNotFoundException) {
                        // no point continuing: this adapter isn't available
                        /*
                         * 适配器不可用，无需继续。
                         */
                        msg = secondaryClassNotFoundException.getMessage();
                        logDiagnostic("The log adapter '" + logAdapterClassName +
                                "' is not available via the LogFactoryImpl class classloader: " + msg.trim());
                        // 记录类加载器无法加载类的诊断信息
                        break;
                    }
                }

                constructor = c.getConstructor(logConstructorSignature);
                // 获取指定签名的构造函数
                Object o = constructor.newInstance(params);
                // 使用构造函数创建实例

                // Note that we do this test after trying to create an instance
                // [rather than testing Log.class.isAssignableFrom(c)] so that
                // we don't complain about Log hierarchy problems when the
                // adapter couldn't be instantiated anyway.
                /*
                 * 在创建实例后再检查类型，以避免因实例化失败而报告类层次问题。
                 */
                if (o instanceof Log) {
                    logAdapterClass = c;
                    logAdapter = (Log) o;
                    break;
                    // 如果实例是 Log 类型，保存类和实例并退出循环
                }

                // Oops, we have a potential problem here. An adapter class
                // has been found and its underlying lib is present too, but
                // there are multiple Log interface classes available making it
                // impossible to cast to the type the caller wanted. We
                // certainly can't use this logger, but we need to know whether
                // to keep on discovering or terminate now.
                //
                // The handleFlawedHierarchy method will throw
                // LogConfigurationException if it regards this problem as
                // fatal, and just return if not.
                /*
                 * 发现潜在问题：适配器类存在，但存在多个 Log 接口类，无法转换。
                 * 调用 handleFlawedHierarchy 判断是否继续发现或终止。
                 */
                handleFlawedHierarchy(currentCL, c);
            } catch (NoClassDefFoundError e) {
                // We were able to load the adapter but it had references to
                // other classes that could not be found. This simply means that
                // the underlying logger library is not present in this or any
                // ancestor classloader. There's no point in trying higher up
                // in the hierarchy in this case..
                /*
                 * 适配器类加载成功，但引用的其他类未找到，表明日志库缺失。
                 * 无需继续尝试上层类加载器。
                 */
                String msg = e.getMessage();
                logDiagnostic("The log adapter '" + logAdapterClassName +
                        "' is missing dependencies when loaded via classloader " + objectId(currentCL) +
                        ": " + msg.trim());
                // 记录依赖缺失的诊断信息
                break;
            } catch (ExceptionInInitializerError e) {
                // A static initializer block or the initializer code associated
                // with a static variable on the log adapter class has thrown
                // an exception.
                //
                // We treat this as meaning the adapter's underlying logging
                // library could not be found.
                /*
                 * 适配器类的静态初始化块或变量抛出异常，视为日志库不可用。
                 */
                String msg = e.getMessage();
                logDiagnostic("The log adapter '" + logAdapterClassName +
                        "' is unable to initialize itself when loaded via classloader " + objectId(currentCL) +
                        ": " + msg.trim());
                // 记录初始化失败的诊断信息
                break;
            } catch (LogConfigurationException e) {
                // call to handleFlawedHierarchy above must have thrown
                // a LogConfigurationException, so just throw it on
                /*
                 * handleFlawedHierarchy 抛出异常，直接传递。
                 */
                throw e;
            } catch (Throwable t) {
                handleThrowable(t); // may re-throw t
                // handleFlawedDiscovery will determine whether this is a fatal
                // problem or not. If it is fatal, then a LogConfigurationException
                // will be thrown.
                /*
                 * 处理其他异常，调用 handleFlawedDiscovery 判断是否致命。
                 */
                handleFlawedDiscovery(logAdapterClassName, currentCL, t);
            }

            if (currentCL == null) {
                break;
                // 如果当前类加载器为空，退出循环
            }

            // try the parent classloader
            // currentCL = currentCL.getParent();
            currentCL = getParentClassLoader(currentCL);
            // 尝试使用父类加载器
        }

        if (logAdapterClass != null && affectState) {
            // We've succeeded, so set instance fields
            /*
             * 成功加载类且 affectState 为 true，设置实例字段。
             */
            this.logClassName = logAdapterClassName;
            this.logConstructor = constructor;
            // 保存类名和构造函数

            // Identify the <code>setLogFactory</code> method (if there is one)
            /*
             * 查找 setLogFactory 方法（如果存在）。
             */
            try {
                this.logMethod = logAdapterClass.getMethod("setLogFactory", logMethodSignature);
                logDiagnostic("Found method setLogFactory(LogFactory) in '" + logAdapterClassName + "'");
                // 记录找到 setLogFactory 方法的诊断信息
            } catch (Throwable t) {
                handleThrowable(t); // may re-throw t
                this.logMethod = null;
                logDiagnostic("[INFO] '" + logAdapterClassName + "' from classloader " + objectId(currentCL) +
                        " does not declare optional method " + "setLogFactory(LogFactory)");
                // 记录未找到 setLogFactory 方法的诊断信息
            }

            logDiagnostic("Log adapter '" + logAdapterClassName + "' from classloader " +
                    objectId(logAdapterClass.getClassLoader()) + " has been selected for use.");
            // 记录选择日志适配器的诊断信息
        }

        return logAdapter;
        // 返回创建的 Log 实例或 null
    }

    /**
     * Return the classloader from which we should try to load the logging
     * adapter classes.
     * <p>
     * This method usually returns the context classloader. However if it
     * is discovered that the classloader which loaded this class is a child
     * of the context classloader <i>and</i> the allowFlawedContext option
     * has been set then the classloader which loaded this class is returned
     * instead.
     * <p>
     * The only time when the classloader which loaded this class is a
     * descendant (rather than the same as or an ancestor of the context
     * classloader) is when an app has created custom classloaders but
     * failed to correctly set the context classloader. This is a bug in
     * the calling application; however we provide the option for JCL to
     * simply generate a warning rather than fail outright.
     */
    /*
     * 返回用于加载日志适配器类的类加载器。
     * 通常返回上下文类加载器，但如果发现本类加载器是上下文类加载器的子类且 allowFlawedContext 为 true，则返回本类加载器。
     * 注意：仅当应用创建自定义类加载器但未正确设置上下文类加载器时发生。
     * @return 类加载器
     * @throws LogConfigurationException 如果类加载器层次结构错误
     */
    private ClassLoader getBaseClassLoader() throws LogConfigurationException {
        ClassLoader thisClassLoader = getClassLoader(LogFactoryImpl.class);
        // 获取本类的类加载器

        if (!useTCCL) {
            return thisClassLoader;
            // 如果不使用线程上下文类加载器，返回本类加载器
        }

        ClassLoader contextClassLoader = getContextClassLoaderInternal();
        // 获取上下文类加载器

        ClassLoader baseClassLoader = getLowestClassLoader(
                contextClassLoader, thisClassLoader);
        // 获取上下文类加载器和本类加载器中较低的类加载器

        if (baseClassLoader == null) {
            // The two classloaders are not part of a parent child relationship.
            // In some classloading setups (e.g. JBoss with its
            // UnifiedLoaderRepository) this can still work, so if user hasn't
            // forbidden it, just return the contextClassLoader.
            /*
             * 两个类加载器无父子关系。
             * 在某些类加载设置（如 JBoss）中仍可工作，若用户未禁止，返回上下文类加载器。
             */
            if (allowFlawedContext) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[WARNING] the context classloader is not part of a" +
                            " parent-child relationship with the classloader that" +
                            " loaded LogFactoryImpl.");
                    // 记录类加载器无父子关系的警告
                }
                // If contextClassLoader were null, getLowestClassLoader() would
                // have returned thisClassLoader.  The fact we are here means
                // contextClassLoader is not null, so we can just return it.
                return contextClassLoader;
                // 返回上下文类加载器
            } else {
                throw new LogConfigurationException("Bad classloader hierarchy; LogFactoryImpl was loaded via" +
                        " a classloader that is not related to the current context" +
                        " classloader.");
                // 抛出类加载器层次结构错误的异常
            }
        }

        if (baseClassLoader != contextClassLoader) {
            // We really should just use the contextClassLoader as the starting
            // point for scanning for log adapter classes. However it is expected
            // that there are a number of broken systems out there which create
            // custom classloaders but fail to set the context classloader so
            // we handle those flawed systems anyway.
            /*
             * 应优先使用上下文类加载器，但某些系统未正确设置上下文类加载器，需处理此类问题。
             */
            if (allowFlawedContext) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic(
                            "Warning: the context classloader is an ancestor of the" +
                                    " classloader that loaded LogFactoryImpl; it should be" +
                                    " the same or a descendant. The application using" +
                                    " commons-logging should ensure the context classloader" +
                                    " is used correctly.");
                    // 记录上下文类加载器为祖先的警告
                }
            } else {
                throw new LogConfigurationException(
                        "Bad classloader hierarchy; LogFactoryImpl was loaded via" +
                                " a classloader that is not related to the current context" +
                                " classloader.");
                // 抛出类加载器层次结构错误的异常
            }
        }

        return baseClassLoader;
        // 返回基础类加载器
    }

    /**
     * Given two related classloaders, return the one which is a child of
     * the other.
     * <p>
     *
     * @param c1 is a classloader (including the null classloader)
     * @param c2 is a classloader (including the null classloader)
     * @return c1 if it has c2 as an ancestor, c2 if it has c1 as an ancestor,
     * and null if neither is an ancestor of the other.
     */
    /*
     * 给定两个相关类加载器，返回其中之一为另一个的子类加载器。
     * @param c1 类加载器（可能为 null）
     * @param c2 类加载器（可能为 null）
     * @return c1 如果 c2 是其祖先，c2 如果 c1 是其祖先，否则 null
     */
    private ClassLoader getLowestClassLoader(ClassLoader c1, ClassLoader c2) {
        // TODO: use AccessController when dealing with classloaders here
        /*
         * TODO：在处理类加载器时使用 AccessController。
         */

        if (c1 == null) {
            return c2;
            // 如果 c1 为空，返回 c2
        }

        if (c2 == null) {
            return c1;
            // 如果 c2 为空，返回 c1
        }

        ClassLoader current;

        // scan c1's ancestors to find c2
        current = c1;
        while (current != null) {
            if (current == c2) {
                return c1;
                // 如果 c2 是 c1 的祖先，返回 c1
            }
            // current = current.getParent();
            current = getParentClassLoader(current);
            // 获取当前类加载器的父类加载器
        }

        // scan c2's ancestors to find c1
        current = c2;
        while (current != null) {
            if (current == c1) {
                return c2;
                // 如果 c1 是 c2 的祖先，返回 c2
            }
            // current = current.getParent();
            current = getParentClassLoader(current);
            // 获取当前类加载器的父类加载器
        }

        return null;
        // 两个类加载器无父子关系，返回 null
    }

    /**
     * Generates an internal diagnostic logging of the discovery failure and
     * then throws a <code>LogConfigurationException</code> that wraps
     * the passed <code>Throwable</code>.
     *
     * @param logAdapterClassName is the class name of the Log implementation
     *                            that could not be instantiated. Cannot be <code>null</code>.
     * @param classLoader         is the classloader that we were trying to load the
     *                            logAdapterClassName from when the exception occurred.
     * @param discoveryFlaw       is the Throwable created by the classloader
     * @throws LogConfigurationException ALWAYS
     */
    /*
     * 记录发现失败的诊断信息并抛出包装了指定异常的 LogConfigurationException。
     * @param logAdapterClassName 无法实例化的日志实现类名
     * @param classLoader 尝试加载类的类加载器
     * @param discoveryFlaw 类加载器抛出的异常
     * @throws LogConfigurationException 总是抛出此异常
     */
    private void handleFlawedDiscovery(String logAdapterClassName,
                                       ClassLoader classLoader, // USED?
                                       Throwable discoveryFlaw) {

        if (isDiagnosticsEnabled()) {
            logDiagnostic("Could not instantiate Log '" +
                    logAdapterClassName + "' -- " +
                    discoveryFlaw.getClass().getName() + ": " +
                    discoveryFlaw.getLocalizedMessage());
            // 记录无法实例化日志类的诊断信息

            if (discoveryFlaw instanceof InvocationTargetException) {
                // Ok, the lib is there but while trying to create a real underlying
                // logger something failed in the underlying lib; display info about
                // that if possible.
                /*
                 * 日志库存在，但创建底层日志器时失败，显示相关信息。
                 */
                InvocationTargetException ite = (InvocationTargetException) discoveryFlaw;
                Throwable cause = ite.getTargetException();
                if (cause != null) {
                    logDiagnostic("... InvocationTargetException: " +
                            cause.getClass().getName() + ": " +
                            cause.getLocalizedMessage());
                    // 记录目标异常信息

                    if (cause instanceof ExceptionInInitializerError) {
                        ExceptionInInitializerError eiie = (ExceptionInInitializerError) cause;
                        Throwable cause2 = eiie.getException();
                        if (cause2 != null) {
                            final StringWriter sw = new StringWriter();
                            cause2.printStackTrace(new PrintWriter(sw, true));
                            logDiagnostic("... ExceptionInInitializerError: " + sw.toString());
                            // 记录初始化错误的堆栈信息
                        }
                    }
                }
            }
        }

        if (!allowFlawedDiscovery) {
            throw new LogConfigurationException(discoveryFlaw);
            // 如果不容忍发现错误，抛出异常
        }
    }

    /**
     * Report a problem loading the log adapter, then either return
     * (if the situation is considered recoverable) or throw a
     * LogConfigurationException.
     * <p>
     * There are two possible reasons why we successfully loaded the
     * specified log adapter class then failed to cast it to a Log object:
     * <ol>
     * <li>the specific class just doesn't implement the Log interface
     *     (user screwed up), or
     * <li> the specified class has bound to a Log class loaded by some other
     *      classloader; Log@classloaderX cannot be cast to Log@classloaderY.
     * </ol>
     * <p>
     * Here we try to figure out which case has occurred so we can give the
     * user some reasonable feedback.
     *
     * @param badClassLoader is the classloader we loaded the problem class from,
     *                       ie it is equivalent to badClass.getClassLoader().
     * @param badClass       is a Class object with the desired name, but which
     *                       does not implement Log correctly.
     * @throws LogConfigurationException when the situation
     *                                   should not be recovered from.
     */
    /*
     * 报告加载日志适配器的问题，若可恢复则返回，否则抛出 LogConfigurationException。
     * 加载成功但无法转换为 Log 的原因：
     * 1. 类未实现 Log 接口（用户错误）。
     * 2. 类绑定到其他类加载器加载的 Log 接口。
     * 分析问题原因并提供用户反馈。
     * @param badClassLoader 加载问题类的类加载器
     * @param badClass 未正确实现 Log 接口的类
     * @throws LogConfigurationException 如果问题不可恢复
     */
    private void handleFlawedHierarchy(ClassLoader badClassLoader, Class badClass)
            throws LogConfigurationException {

        boolean implementsLog = false;
        String logInterfaceName = Log.class.getName();
        Class interfaces[] = badClass.getInterfaces();
        // 定义变量以检查类是否实现 Log 接口，获取 Log 接口全限定名和目标类的接口列表
        for (int i = 0; i < interfaces.length; i++) {
            if (logInterfaceName.equals(interfaces[i].getName())) {
                implementsLog = true;
                break;
                // 检查类是否实现 Log 接口
                // 遍历接口列表，若找到 Log 接口，设置标志并退出循环
            }
        }

        if (implementsLog) {
            // the class does implement an interface called Log, but
            // it is in the wrong classloader
            /*
             * 类实现了 Log 接口，但使用错误的类加载器。
             */
            if (isDiagnosticsEnabled()) {
                try {
                    ClassLoader logInterfaceClassLoader = getClassLoader(Log.class);
                    logDiagnostic("Class '" + badClass.getName() + "' was found in classloader " +
                            objectId(badClassLoader) + ". It is bound to a Log interface which is not" +
                            " the one loaded from classloader " + objectId(logInterfaceClassLoader));
                    // 记录类绑定错误类加载器的诊断信息
                    // 尝试获取 Log 接口的类加载器，记录诊断信息以提示类加载器不匹配
                } catch (Throwable t) {
                    handleThrowable(t); // may re-throw t
                    logDiagnostic("Error while trying to output diagnostics about" + " bad class '" + badClass + "'");
                    // 处理获取类加载器时的异常，记录诊断错误信息
                }
            }

            if (!allowFlawedHierarchy) {
                StringBuffer msg = new StringBuffer();
                msg.append("Terminating logging for this context ");
                msg.append("due to bad log hierarchy. ");
                msg.append("You have more than one version of '");
                msg.append(Log.class.getName());
                msg.append("' visible.");
                if (isDiagnosticsEnabled()) {
                    logDiagnostic(msg.toString());
                }
                throw new LogConfigurationException(msg.toString());
                // 如果不容忍类层次错误，构造错误消息并抛出异常
                // 重要配置参数 allowFlawedHierarchy：控制是否容忍 Log 接口的多版本问题，默认为 true
                // 事件处理逻辑：当发现多个 Log 接口版本时，终止日志并抛出异常
            }

            if (isDiagnosticsEnabled()) {
                StringBuffer msg = new StringBuffer();
                msg.append("Warning: bad log hierarchy. ");
                msg.append("You have more than one version of '");
                msg.append(Log.class.getName());
                msg.append("' visible.");
                logDiagnostic(msg.toString());
                // 记录警告信息，提示存在多个 Log 接口版本
                // 特殊处理：当 allowFlawedHierarchy 为 true 时，仅记录警告，继续执行
            }
        } else {
            // this is just a bad adapter class
            /*
             * 类未实现 Log 接口，仅为错误适配器类。
             */
            if (!allowFlawedDiscovery) {
                StringBuffer msg = new StringBuffer();
                msg.append("Terminating logging for this context. ");
                msg.append("Log class '");
                msg.append(badClass.getName());
                msg.append("' does not implement the Log interface.");
                if (isDiagnosticsEnabled()) {
                    logDiagnostic(msg.toString());
                }

                throw new LogConfigurationException(msg.toString());
                // 如果不容忍发现错误，构造错误消息并抛出异常
                // 重要配置参数 allowFlawedDiscovery：控制是否容忍无效日志适配器，默认为 true
                // 事件处理逻辑：当类未实现 Log 接口时，终止日志并抛出异常
            }

            if (isDiagnosticsEnabled()) {
                StringBuffer msg = new StringBuffer();
                msg.append("[WARNING] Log class '");
                msg.append(badClass.getName());
                msg.append("' does not implement the Log interface.");
                logDiagnostic(msg.toString());
                // 记录警告信息，提示类未实现 Log 接口
                // 特殊处理：当 allowFlawedDiscovery 为 true 时，仅记录警告，继续执行
            }
        }
        // 方法目的：分析并处理日志适配器加载失败的原因，提供用户反馈
        // 交互逻辑：根据配置参数（allowFlawedHierarchy 和 allowFlawedDiscovery）决定是抛出异常还是记录警告
        // 特殊处理注意事项：支持容忍错误配置以提高兼容性，但可能导致非预期日志实现
    }
}
