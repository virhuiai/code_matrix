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

package org.apache.commons.logging;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Factory for creating {@link Log} instances, with discovery and
 * configuration features similar to that employed by standard Java APIs
 * such as JAXP.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - This implementation is heavily
 * based on the SAXParserFactory and DocumentBuilderFactory implementations
 * (corresponding to the JAXP pluggability APIs) found in Apache Xerces.
 *
 * @version $Id$
 */
// 中文注释：日志工厂类，用于创建 Log 实例，具备与标准 Java API（如 JAXP）类似的发现和配置功能。
// 实现参考了 Apache Xerces 中的 SAXParserFactory 和 DocumentBuilderFactory。
public abstract class LogFactory {
    // Implementation note re AccessController usage
    //
    // It is important to keep code invoked via an AccessController to small
    // auditable blocks. Such code must carefully evaluate all user input
    // (parameters, system properties, config file contents, etc). As an
    // example, a Log implementation should not write to its logfile
    // with an AccessController anywhere in the call stack, otherwise an
    // insecure application could configure the log implementation to write
    // to a protected file using the privileges granted to JCL rather than
    // to the calling application.
    //
    // Under no circumstance should a non-private method return data that is
    // retrieved via an AccessController. That would allow an insecure app
    // to invoke that method and obtain data that it is not permitted to have.
    //
    // Invoking user-supplied code with an AccessController set is not a major
    // issue (eg invoking the constructor of the class specified by
    // HASHTABLE_IMPLEMENTATION_PROPERTY). That class will be in a different
    // trust domain, and therefore must have permissions to do whatever it
    // is trying to do regardless of the permissions granted to JCL. There is
    // a slight issue in that untrusted code may point that environment var
    // to another trusted library, in which case the code runs if both that
    // lib and JCL have the necessary permissions even when the untrusted
    // caller does not. That's a pretty hard route to exploit though.
    // 中文注释：关于 AccessController 的使用说明：
    // 通过 AccessController 调用的代码应保持简短且可审计，需仔细检查所有用户输入（如参数、系统属性、配置文件内容等）。
    // 例如，日志实现不应在调用栈中包含 AccessController 的情况下写入日志文件，否则不安全的应用程序可能利用 JCL 的权限写入受保护文件。
    // 非私有方法不得返回通过 AccessController 获取的数据，以防止不安全应用获取未授权数据。
    // 使用 AccessController 调用用户提供的代码（如 HASHTABLE_IMPLEMENTATION_PROPERTY 指定的类构造函数）问题不大，因为这些代码运行在不同的信任域，必须具备相应权限。
    // 但不信任代码可能将环境变量指向另一个受信任库，导致代码在调用者无权限的情况下运行，这种情况难以利用。

    // ----------------------------------------------------- Manifest Constants

    /**
     * The name (<code>priority</code>) of the key in the config file used to
     * specify the priority of that particular config file. The associated value
     * is a floating-point number; higher values take priority over lower values.
     */
    // 中文注释：配置文件中用于指定优先级的键名（"priority"），值为浮点数，数值越高优先级越高。
    public static final String PRIORITY_KEY = "priority";

    /**
     * The name (<code>use_tccl</code>) of the key in the config file used
     * to specify whether logging classes should be loaded via the thread
     * context class loader (TCCL), or not. By default, the TCCL is used.
     */
    // 中文注释：配置文件中用于指定是否通过线程上下文类加载器（TCCL）加载日志类的键名（"use_tccl"），默认使用 TCCL。
    public static final String TCCL_KEY = "use_tccl";

    /**
     * The name (<code>org.apache.commons.logging.LogFactory</code>) of the property
     * used to identify the LogFactory implementation
     * class name. This can be used as a system property, or as an entry in a
     * configuration properties file.
     */
    // 中文注释：用于标识 LogFactory 实现类名称的属性名（"org.apache.commons.logging.LogFactory"），可作为系统属性或配置文件中的条目。
    public static final String FACTORY_PROPERTY = "org.apache.commons.logging.LogFactory";

    /**
     * The fully qualified class name of the fallback <code>LogFactory</code>
     * implementation class to use, if no other can be found.
     */
    // 中文注释：当无法找到其他实现时，使用的默认 LogFactory 实现类的全限定名。
    public static final String FACTORY_DEFAULT = "org.apache.commons.logging.impl.LogFactoryImpl";

    /**
     * The name (<code>commons-logging.properties</code>) of the properties file to search for.
     */
    // 中文注释：要搜索的配置文件名称（"commons-logging.properties"）。
    public static final String FACTORY_PROPERTIES = "commons-logging.properties";

    /**
     * JDK1.3+ <a href="http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#Service%20Provider">
     * 'Service Provider' specification</a>.
     */
    // 中文注释：JDK1.3+ 服务提供者规范的引用，指向服务配置文件路径。
    protected static final String SERVICE_ID =
        "META-INF/services/org.apache.commons.logging.LogFactory";

    /**
     * The name (<code>org.apache.commons.logging.diagnostics.dest</code>)
     * of the property used to enable internal commons-logging
     * diagnostic output, in order to get information on what logging
     * implementations are being discovered, what classloaders they
     * are loaded through, etc.
     * <p>
     * If a system property of this name is set then the value is
     * assumed to be the name of a file. The special strings
     * STDOUT or STDERR (case-sensitive) indicate output to
     * System.out and System.err respectively.
     * <p>
     * Diagnostic logging should be used only to debug problematic
     * configurations and should not be set in normal production use.
     */
    // 中文注释：用于启用 commons-logging 内部诊断输出的属性名（"org.apache.commons.logging.diagnostics.dest"）。
    // 如果设置了此系统属性，其值被视为文件名，特殊值 STDOUT 或 STDERR（区分大小写）分别表示输出到 System.out 或 System.err。
    // 诊断日志仅用于调试配置问题，生产环境中不建议启用。
    public static final String DIAGNOSTICS_DEST_PROPERTY =
        "org.apache.commons.logging.diagnostics.dest";

    /**
     * When null (the usual case), no diagnostic output will be
     * generated by LogFactory or LogFactoryImpl. When non-null,
     * interesting events will be written to the specified object.
     */
    // 中文注释：诊断输出流对象，默认为 null（无诊断输出），非 null 时将事件写入指定对象。
    private static PrintStream diagnosticsStream = null;

    /**
     * A string that gets prefixed to every message output by the
     * logDiagnostic method, so that users can clearly see which
     * LogFactory class is generating the output.
     */
    // 中文注释：诊断日志消息的前缀字符串，用于明确标识 LogFactory 类生成的输出。
    private static final String diagnosticPrefix;

    /**
     * Setting this system property
     * (<code>org.apache.commons.logging.LogFactory.HashtableImpl</code>)
     * value allows the <code>Hashtable</code> used to store
     * classloaders to be substituted by an alternative implementation.
     * <p>
     * <strong>Note:</strong> <code>LogFactory</code> will print:
     * <pre>
     * [ERROR] LogFactory: Load of custom hashtable failed
     * </pre>
     * to system error and then continue using a standard Hashtable.
     * <p>
     * <strong>Usage:</strong> Set this property when Java is invoked
     * and <code>LogFactory</code> will attempt to load a new instance
     * of the given implementation class.
     * For example, running the following ant scriplet:
     * <pre>
     *  &lt;java classname="${test.runner}" fork="yes" failonerror="${test.failonerror}"&gt;
     *     ...
     *     &lt;sysproperty
     *        key="org.apache.commons.logging.LogFactory.HashtableImpl"
     *        value="org.apache.commons.logging.AltHashtable"/&gt;
     *  &lt;/java&gt;
     * </pre>
     * will mean that <code>LogFactory</code> will load an instance of
     * <code>org.apache.commons.logging.AltHashtable</code>.
     * <p>
     * A typical use case is to allow a custom
     * Hashtable implementation using weak references to be substituted.
     * This will allow classloaders to be garbage collected without
     * the need to release them (on 1.3+ JVMs only, of course ;).
     */
    // 中文注释：系统属性（"org.apache.commons.logging.LogFactory.HashtableImpl"）允许替换用于存储类加载器的 Hashtable 实现。
    // 如果加载自定义 Hashtable 失败，LogFactory 将打印错误信息并使用标准 Hashtable。
    // 使用示例：通过设置此系统属性，LogFactory 将尝试加载指定实现类（如 org.apache.commons.logging.AltHashtable）。
    // 典型用途是使用弱引用实现的自定义 Hashtable，以便在无需释放的情况下允许类加载器被垃圾回收（仅限 JDK 1.3+）。
    public static final String HASHTABLE_IMPLEMENTATION_PROPERTY =
        "org.apache.commons.logging.LogFactory.HashtableImpl";

    /** Name used to load the weak hashtable implementation by names. */
    // 中文注释：用于通过名称加载弱引用 Hashtable 实现类的名称。
    private static final String WEAK_HASHTABLE_CLASSNAME =
        "org.apache.commons.logging.impl.WeakHashtable";

    /**
     * A reference to the classloader that loaded this class. This is the
     * same as LogFactory.class.getClassLoader(). However computing this
     * value isn't quite as simple as that, as we potentially need to use
     * AccessControllers etc. It's more efficient to compute it once and
     * cache it here.
     */
    // 中文注释：保存加载当前类的类加载器的引用，等同于 LogFactory.class.getClassLoader()。
    // 由于可能需要使用 AccessController，直接获取可能复杂，因此一次性计算并缓存以提高效率。
    private static final ClassLoader thisClassLoader;

    // ----------------------------------------------------------- Constructors

    /**
     * Protected constructor that is not available for public use.
     */
    // 中文注释：受保护的构造函数，禁止公共访问，仅限内部使用。
    protected LogFactory() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the configuration attribute with the specified name (if any),
     * or <code>null</code> if there is no such attribute.
     *
     * @param name Name of the attribute to return
     */
    // 中文注释：获取指定名称的配置属性，若不存在则返回 null。
    // 参数说明：name - 要返回的属性名称。
    public abstract Object getAttribute(String name);

    /**
     * Return an array containing the names of all currently defined
     * configuration attributes.  If there are no such attributes, a zero
     * length array is returned.
     */
    // 中文注释：返回包含所有当前定义的配置属性名称的数组，若无属性则返回空数组。
    public abstract String[] getAttributeNames();

    /**
     * Convenience method to derive a name from the specified class and
     * call <code>getInstance(String)</code> with it.
     *
     * @param clazz Class for which a suitable Log name will be derived
     * @throws LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    // 中文注释：便捷方法，从指定类派生日志名称并调用 getInstance(String) 获取 Log 实例。
    // 参数说明：clazz - 用于派生日志名称的类。
    // 异常说明：若无法返回合适的 Log 实例，则抛出 LogConfigurationException。
    public abstract Log getInstance(Class clazz)
        throws LogConfigurationException;

    /**
     * Construct (if necessary) and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.
     * <p>
     * <strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     * @throws LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    // 中文注释：根据工厂当前配置属性构造并返回 Log 实例。
    // 注意事项：根据 LogFactory 实现的不同，返回的 Log 实例可能是应用本地的，也可能不是，且后续调用可能返回不同实例。
    // 参数说明：name - 要返回的 Log 实例的逻辑名称，其含义仅由底层日志实现知晓。
    // 异常说明：若无法返回合适的 Log 实例，则抛出 LogConfigurationException。
    public abstract Log getInstance(String name)
        throws LogConfigurationException;

    /**
     * Release any internal references to previously created {@link Log}
     * instances returned by this factory.  This is useful in environments
     * like servlet containers, which implement application reloading by
     * throwing away a ClassLoader.  Dangling references to objects in that
     * class loader would prevent garbage collection.
     */
    // 中文注释：释放工厂创建的所有 Log 实例的内部引用。
    // 使用场景：在 Servlet 容器等支持应用重载的环境中，通过丢弃 ClassLoader 实现重载，释放引用以避免内存泄漏。
    public abstract void release();

    /**
     * Remove any configuration attribute associated with the specified name.
     * If there is no such attribute, no action is taken.
     *
     * @param name Name of the attribute to remove
     */
    // 中文注释：移除指定名称的配置属性，若属性不存在则不执行任何操作。
    // 参数说明：name - 要移除的属性名称。
    public abstract void removeAttribute(String name);

    /**
     * Set the configuration attribute with the specified name.  Calling
     * this with a <code>null</code> value is equivalent to calling
     * <code>removeAttribute(name)</code>.
     *
     * @param name Name of the attribute to set
     * @param value Value of the attribute to set, or <code>null</code>
     *  to remove any setting for this attribute
     */
    // 中文注释：设置指定名称的配置属性，值为 null 时等同于调用 removeAttribute(name)。
    // 参数说明：name - 要设置的属性名称；value - 属性值，null 表示移除该属性。
    public abstract void setAttribute(String name, Object value);

    // ------------------------------------------------------- Static Variables

    /**
     * The previously constructed <code>LogFactory</code> instances, keyed by
     * the <code>ClassLoader</code> with which it was created.
     */
    // 中文注释：存储已创建的 LogFactory 实例，以创建它们的 ClassLoader 为键。
    protected static Hashtable factories = null;

    /**
     * Previously constructed <code>LogFactory</code> instance as in the
     * <code>factories</code> map, but for the case where
     * <code>getClassLoader</code> returns <code>null</code>.
     * This can happen when:
     * <ul>
     * <li>using JDK1.1 and the calling code is loaded via the system
     *  classloader (very common)</li>
     * <li>using JDK1.2+ and the calling code is loaded via the boot
     *  classloader (only likely for embedded systems work).</li>
     * </ul>
     * Note that <code>factories</code> is a <i>Hashtable</i> (not a HashMap),
     * and hashtables don't allow null as a key.
     * @deprecated since 1.1.2
     */
    // 中文注释：存储在 getClassLoader 返回 null 时的 LogFactory 实例，适用于：
    // 1. JDK1.1 中调用代码通过系统类加载器加载（常见情况）；
    // 2. JDK1.2+ 中调用代码通过引导类加载器加载（嵌入式系统常见）。
    // 注意：factories 是 Hashtable 类型，不允许 null 作为键。
    // 已废弃：自 1.1.2 起。
    protected static volatile LogFactory nullClassLoaderFactory = null;

    /**
     * Create the hashtable which will be used to store a map of
     * (context-classloader -> logfactory-object). Version 1.2+ of Java
     * supports "weak references", allowing a custom Hashtable class
     * to be used which uses only weak references to its keys. Using weak
     * references can fix memory leaks on webapp unload in some cases (though
     * not all). Version 1.1 of Java does not support weak references, so we
     * must dynamically determine which we are using. And just for fun, this
     * code also supports the ability for a system property to specify an
     * arbitrary Hashtable implementation name.
     * <p>
     * Note that the correct way to ensure no memory leaks occur is to ensure
     * that LogFactory.release(contextClassLoader) is called whenever a
     * webapp is undeployed.
     */
    // 中文注释：创建用于存储 (上下文类加载器 -> LogFactory 对象) 映射的 Hashtable。
    // Java 1.2+ 支持弱引用，允许使用自定义的弱引用 Hashtable 类以解决 Web 应用卸载时的内存泄漏问题（部分情况）。
    // Java 1.1 不支持弱引用，因此需动态确定使用版本。
    // 支持通过系统属性指定任意 Hashtable 实现类。
    // 注意事项：为避免内存泄漏，需在 Web 应用卸载时调用 LogFactory.release(contextClassLoader)。
    private static final Hashtable createFactoryStore() {
        // 中文注释：创建并返回用于存储类加载器和 LogFactory 映射的 Hashtable。
        Hashtable result = null;
        String storeImplementationClass;
        try {
            storeImplementationClass = getSystemProperty(HASHTABLE_IMPLEMENTATION_PROPERTY, null);
            // 中文注释：尝试获取系统属性 HASHTABLE_IMPLEMENTATION_PROPERTY，指定自定义 Hashtable 实现类。
        } catch (SecurityException ex) {
            // Permissions don't allow this to be accessed. Default to the "modern"
            // weak hashtable implementation if it is available.
            // 中文注释：若权限不足无法访问系统属性，则默认使用弱引用 Hashtable 实现（如果可用）。
            storeImplementationClass = null;
        }

        if (storeImplementationClass == null) {
            storeImplementationClass = WEAK_HASHTABLE_CLASSNAME;
            // 中文注释：若未指定自定义实现，则使用默认的弱引用 Hashtable 类名。
        }
        try {
            Class implementationClass = Class.forName(storeImplementationClass);
            result = (Hashtable) implementationClass.newInstance();
            // 中文注释：尝试加载并实例化指定的 Hashtable 实现类。
        } catch (Throwable t) {
            handleThrowable(t); // may re-throw t
            // 中文注释：处理异常，可能重新抛出异常。

            // ignore
            if (!WEAK_HASHTABLE_CLASSNAME.equals(storeImplementationClass)) {
                // if the user's trying to set up a custom implementation, give a clue
                if (isDiagnosticsEnabled()) {
                    // use internal logging to issue the warning
                    logDiagnostic("[ERROR] LogFactory: Load of custom hashtable failed");
                    // 中文注释：若自定义 Hashtable 加载失败，使用内部日志记录警告。
                } else {
                    // we *really* want this output, even if diagnostics weren't
                    // explicitly enabled by the user.
                    System.err.println("[ERROR] LogFactory: Load of custom hashtable failed");
                    // 中文注释：若诊断未启用，仍将错误信息输出到 System.err。
                }
            }
        }
        if (result == null) {
            result = new Hashtable();
            // 中文注释：若未能创建自定义 Hashtable，则使用标准 Hashtable。
        }
        return result;
    }

    // --------------------------------------------------------- Static Methods

    /** Utility method to safely trim a string. */
    // 中文注释：安全修剪字符串的实用方法。
    private static String trim(String src) {
        if (src == null) {
            return null;
        }
        return src.trim();
        // 中文注释：若输入字符串为 null，则返回 null；否则返回修剪后的字符串。
    }

    /**
     * Checks whether the supplied Throwable is one that needs to be
     * re-thrown and ignores all others.
     *
     * The following errors are re-thrown:
     * <ul>
     *   <li>ThreadDeath</li>
     *   <li>VirtualMachineError</li>
     * </ul>
     *
     * @param t the Throwable to check
     */
    // 中文注释：检查指定异常是否需要重新抛出，仅重新抛出 ThreadDeath 和 VirtualMachineError。
    // 参数说明：t - 要检查的异常对象。
    protected static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently ignored
        // 中文注释：其他异常类型将被静默忽略。
    }

    /**
     * Construct (if necessary) and return a <code>LogFactory</code>
     * instance, using the following ordered lookup procedure to determine
     * the name of the implementation class to be loaded.
     * <p>
     * <ul>
     * <li>The <code>org.apache.commons.logging.LogFactory</code> system
     *     property.</li>
     * <li>The JDK 1.3 Service Discovery mechanism</li>
     * <li>Use the properties file <code>commons-logging.properties</code>
     *     file, if found in the class path of this class.  The configuration
     *     file is in standard <code>java.util.Properties</code> format and
     *     contains the fully qualified name of the implementation class
     *     with the key being the system property defined above.</li>
     * <li>Fall back to a default implementation class
     *     (<code>org.apache.commons.logging.impl.LogFactoryImpl</code>).</li>
     * </ul>
     * <p>
     * <em>NOTE</em> - If the properties file method of identifying the
     * <code>LogFactory</code> implementation class is utilized, all of the
     * properties defined in this file will be set as configuration attributes
     * on the corresponding <code>LogFactory</code> instance.
     * <p>
     * <em>NOTE</em> - In a multi-threaded environment it is possible
     * that two different instances will be returned for the same
     * classloader environment.
     *
     * @throws LogConfigurationException if the implementation class is not
     *  available or cannot be instantiated.
     */
    // 中文注释：构造并返回 LogFactory 实例，通过以下顺序查找确定要加载的实现类：
    // 1. 系统属性 org.apache.commons.logging.LogFactory；
    // 2. JDK 1.3 服务发现机制；
    // 3. 类路径中的 commons-logging.properties 文件，格式为 java.util.Properties，包含实现类的全限定名；
    // 4. 默认实现类 org.apache.commons.logging.impl.LogFactoryImpl。
    // 注意事项：若使用配置文件方式，所有属性将设置为 LogFactory 实例的配置属性；在多线程环境中，同一类加载器可能返回不同实例。
    // 异常说明：若实现类不可用或无法实例化，则抛出 LogConfigurationException。
    public static LogFactory getFactory() throws LogConfigurationException {
        // Identify the class loader we will be using
        ClassLoader contextClassLoader = getContextClassLoaderInternal();
        // 中文注释：获取当前线程的上下文类加载器。

        if (contextClassLoader == null) {
            // This is an odd enough situation to report about. This
            // output will be a nuisance on JDK1.1, as the system
            // classloader is null in that environment.
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Context classloader is null.");
                // 中文注释：若上下文类加载器为 null，记录诊断信息（在 JDK1.1 中常见）。
            }
        }

        // Return any previously registered factory for this class loader
        LogFactory factory = getCachedFactory(contextClassLoader);
        // 中文注释：检查缓存中是否存在与该类加载器关联的 LogFactory 实例，若存在则返回。
        if (factory != null) {
            return factory;
        }

        if (isDiagnosticsEnabled()) {
            logDiagnostic(
                    "[LOOKUP] LogFactory implementation requested for the first time for context classloader " +
                    objectId(contextClassLoader));
            logHierarchy("[LOOKUP] ", contextClassLoader);
            // 中文注释：记录首次为该上下文类加载器请求 LogFactory 实现的诊断信息，并记录类加载器层次结构。
        }

        // Load properties file.
        //
        // If the properties file exists, then its contents are used as
        // "attributes" on the LogFactory implementation class. One particular
        // property may also control which LogFactory concrete subclass is
        // used, but only if other discovery mechanisms fail..
        //
        // As the properties file (if it exists) will be used one way or
        // another in the end we may as well look for it first.
        // 中文注释：加载配置文件。
        // 若配置文件存在，其内容将被用作 LogFactory 实现类的属性。
        // 特定属性可能控制使用哪个 LogFactory 子类，但仅在其他发现机制失败时生效。
        // 由于配置文件最终会被使用，因此优先查找。

        Properties props = getConfigurationFile(contextClassLoader, FACTORY_PROPERTIES);
        // 中文注释：通过类加载器查找 commons-logging.properties 配置文件。

        // Determine whether we will be using the thread context class loader to
        // load logging classes or not by checking the loaded properties file (if any).
        ClassLoader baseClassLoader = contextClassLoader;
        if (props != null) {
            String useTCCLStr = props.getProperty(TCCL_KEY);
            if (useTCCLStr != null) {
                // The Boolean.valueOf(useTCCLStr).booleanValue() formulation
                // is required for Java 1.2 compatibility.
                if (Boolean.valueOf(useTCCLStr).booleanValue() == false) {
                    // Don't use current context classloader when locating any
                    // LogFactory or Log classes, just use the class that loaded
                    // this abstract class. When this class is deployed in a shared
                    // classpath of a container, it means webapps cannot deploy their
                    // own logging implementations. It also means that it is up to the
                    // implementation whether to load library-specific config files
                    // from the TCCL or not.
                    baseClassLoader = thisClassLoader;
                    // 中文注释：检查配置文件中的 TCCL_KEY 属性。
                    // 若属性存在且值为 false，则不使用上下文类加载器，而是使用加载本抽象类的类加载器。
                    // 在容器共享类路径中，这意味着 Web 应用无法部署自己的日志实现，且是否加载特定配置文件取决于实现。
                }
            }
        }

        // Determine which concrete LogFactory subclass to use.
        // First, try a global system property
        if (isDiagnosticsEnabled()) {
            logDiagnostic("[LOOKUP] Looking for system property [" + FACTORY_PROPERTY +
                          "] to define the LogFactory subclass to use...");
            // 中文注释：记录正在查找系统属性 FACTORY_PROPERTY 以确定 LogFactory 子类的诊断信息。
        }

        try {
            String factoryClass = getSystemProperty(FACTORY_PROPERTY, null);
            if (factoryClass != null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] Creating an instance of LogFactory class '" + factoryClass +
                                  "' as specified by system property " + FACTORY_PROPERTY);
                    // 中文注释：记录通过系统属性 FACTORY_PROPERTY 创建指定 LogFactory 类的诊断信息。
                }
                factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
                // 中文注释：创建指定的 LogFactory 子类实例。
            } else {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] No system property [" + FACTORY_PROPERTY + "] defined.");
                    // 中文注释：记录未定义系统属性 FACTORY_PROPERTY 的诊断信息。
                }
            }
        } catch (SecurityException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] A security exception occurred while trying to create an" +
                              " instance of the custom factory class" + ": [" + trim(e.getMessage()) +
                              "]. Trying alternative implementations...");
                // 中文注释：记录因权限问题无法创建自定义工厂类实例的诊断信息，继续尝试其他实现。
            }
            // ignore
        } catch (RuntimeException e) {
            // This is not consistent with the behaviour when a bad LogFactory class is
            // specified in a services file.
            //
            // One possible exception that can occur here is a ClassCastException when
            // the specified class wasn't castable to this LogFactory type.
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] An exception occurred while trying to create an" +
                              " instance of the custom factory class" + ": [" +
                              trim(e.getMessage()) +
                              "] as specified by a system property.");
                // 中文注释：记录因运行时异常（如 ClassCastException）无法创建自定义工厂类实例的诊断信息。
            }
            throw e;
        }

        // Second, try to find a service by using the JDK1.3 class
        // discovery mechanism, which involves putting a file with the name
        // of an interface class in the META-INF/services directory, where the
        // contents of the file is a single line specifying a concrete class
        // that implements the desired interface.
        // 中文注释：通过 JDK 1.3 服务发现机制查找 LogFactory 实现。
        // 该机制在 META-INF/services 目录中放置接口类名称的文件，文件内容为实现该接口的具体类名。

        if (factory == null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] Looking for a resource file of name [" + SERVICE_ID +
                              "] to define the LogFactory subclass to use...");
                // 中文注释：记录正在查找服务文件 SERVICE_ID 以确定 LogFactory 子类的诊断信息。
            }
            try {
                final InputStream is = getResourceAsStream(contextClassLoader, SERVICE_ID);
                // 中文注释：从上下文类加载器获取服务文件输入流。

                if( is != null ) {
                    // This code is needed by EBCDIC and other strange systems.
                    // It's a fix for bugs reported in xerces
                    BufferedReader rd;
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    } catch (java.io.UnsupportedEncodingException e) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                    // 中文注释：为支持 EBCDIC 等系统，优先尝试 UTF-8 编码创建 BufferedReader，若失败则使用默认编码。

                    String factoryClassName = rd.readLine();
                    rd.close();
                    // 中文注释：读取服务文件中的实现类名并关闭输入流。

                    if (factoryClassName != null && ! "".equals(factoryClassName)) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("[LOOKUP]  Creating an instance of LogFactory class " +
                                          factoryClassName +
                                          " as specified by file '" + SERVICE_ID +
                                          "' which was present in the path of the context classloader.");
                            // 中文注释：记录通过服务文件创建指定 LogFactory 类实例的诊断信息。
                        }
                        factory = newFactory(factoryClassName, baseClassLoader, contextClassLoader );
                        // 中文注释：创建服务文件中指定的 LogFactory 子类实例。
                    }
                } else {
                    // is == null
                    if (isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP] No resource file with name '" + SERVICE_ID + "' found.");
                        // 中文注释：记录未找到服务文件 SERVICE_ID 的诊断信息。
                    }
                }
            } catch (Exception ex) {
                // note: if the specified LogFactory class wasn't compatible with LogFactory
                // for some reason, a ClassCastException will be caught here, and attempts will
                // continue to find a compatible class.
                if (isDiagnosticsEnabled()) {
                    logDiagnostic(
                        "[LOOKUP] A security exception occurred while trying to create an" +
                        " instance of the custom factory class" +
                        ": [" + trim(ex.getMessage()) +
                        "]. Trying alternative implementations...");
                    // 中文注释：记录因异常（如 ClassCastException）无法创建自定义工厂类实例的诊断信息，继续尝试其他实现。
                }
                // ignore
            }
        }

        // Third try looking into the properties file read earlier (if found)
        // 中文注释：尝试从之前加载的配置文件中查找 LogFactory 实现。

        if (factory == null) {
            if (props != null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic(
                        "[LOOKUP] Looking in properties file for entry with key '" + FACTORY_PROPERTY +
                        "' to define the LogFactory subclass to use...");
                    // 中文注释：记录正在从配置文件中查找 FACTORY_PROPERTY 键以确定 LogFactory 子类的诊断信息。
                }
                String factoryClass = props.getProperty(FACTORY_PROPERTY);
                if (factoryClass != null) {
                    if (isDiagnosticsEnabled()) {
                        logDiagnostic(
                            "[LOOKUP] Properties file specifies LogFactory subclass '" + factoryClass + "'");
                        // 中文注释：记录配置文件中指定的 LogFactory 子类的诊断信息。
                    }
                    factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
                    // 中文注释：创建配置文件中指定的 LogFactory 子类实例。

                    // TODO: think about whether we need to handle exceptions from newFactory
                } else {
                    if (isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP] Properties file has no entry specifying LogFactory subclass.");
                        // 中文注释：记录配置文件中未指定 LogFactory 子类的诊断信息。
                    }
                }
            } else {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] No properties file available to determine" + " LogFactory subclass from..");
                    // 中文注释：记录未找到配置文件以确定 LogFactory 子类的诊断信息。
                }
            }
        }

        // Fourth, try the fallback implementation class
        // 中文注释：尝试使用默认的 LogFactory 实现类。

        if (factory == null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic(
                    "[LOOKUP] Loading the default LogFactory implementation '" + FACTORY_DEFAULT +
                    "' via the same classloader that loaded this LogFactory" +
                    " class (ie not looking in the context classloader).");
                // 中文注释：记录通过加载本类的类加载器加载默认 LogFactory 实现类的诊断信息。
            }

            // Note: unlike the above code which can try to load custom LogFactory
            // implementations via the TCCL, we don't try to load the default LogFactory
            // implementation via the context classloader because:
            // * that can cause problems (see comments in newFactory method)
            // * no-one should be customising the code of the default class
            // Yes, we do give up the ability for the child to ship a newer
            // version of the LogFactoryImpl class and have it used dynamically
            // by an old LogFactory class in the parent, but that isn't
            // necessarily a good idea anyway.
            // 中文注释：默认实现不通过上下文类加载器加载，因为：
            // 1. 可能引发问题（见 newFactory 方法注释）；
            // 2. 默认类的代码不应被自定义。
            // 这放弃了子类加载器提供新版 LogFactoryImpl 的能力，但这未必是好主意。
            factory = newFactory(FACTORY_DEFAULT, thisClassLoader, contextClassLoader);
            // 中文注释：创建默认 LogFactory 实现类的实例。
        }

        if (factory != null) {
            /**
             * Always cache using context class loader.
             */
            cacheFactory(contextClassLoader, factory);
            // 中文注释：将创建的 LogFactory 实例缓存到上下文类加载器中。

            if (props != null) {
                Enumeration names = props.propertyNames();
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    String value = props.getProperty(name);
                    factory.setAttribute(name, value);
                    // 中文注释：遍历配置文件中的属性，将其设置为 LogFactory 实例的配置属性。
                }
            }
        }

        return factory;
    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param clazz Class from which a log name will be derived
     * @throws LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    // 中文注释：便捷方法，返回基于类名派生的日志实例，应用无需关心工厂实现。
    // 参数说明：clazz - 用于派生日志名称的类。
    // 异常说明：若无法返回合适的 Log 实例，则抛出 LogConfigurationException。
    public static Log getLog(Class clazz) throws LogConfigurationException {
        return getFactory().getInstance(clazz);
        // 中文注释：调用 getFactory 获取工厂实例，并基于指定类获取 Log 实例。
    }

    /**
     * Convenience method to return a named logger, without the application
     * having to care about factories.
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *  returned (the meaning of this name is only known to the underlying
     *  logging implementation that is being wrapped)
     * @throws LogConfigurationException if a suitable <code>Log</code>
     *  instance cannot be returned
     */
    // 中文注释：便捷方法，返回指定名称的日志实例，应用无需关心工厂实现。
    // 参数说明：name - 要返回的 Log 实例的逻辑名称，其含义仅由底层日志实现知晓。
    // 异常说明：若无法返回合适的 Log 实例，则抛出 LogConfigurationException。
    public static Log getLog(String name) throws LogConfigurationException {
        return getFactory().getInstance(name);
        // 中文注释：调用 getFactory 获取工厂实例，并基于指定名称获取 Log 实例。
    }

    /**
     * Release any internal references to previously created {@link LogFactory}
     * instances that have been associated with the specified class loader
     * (if any), after calling the instance method <code>release()</code> on
     * each of them.
     *
     * @param classLoader ClassLoader for which to release the LogFactory
     */
    // 中文注释：释放与指定类加载器关联的所有 LogFactory 实例的内部引用，并在每个实例上调用 release 方法。
    // 参数说明：classLoader - 要释放的类加载器。
    public static void release(ClassLoader classLoader) {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Releasing factory for classloader " + objectId(classLoader));
            // 中文注释：记录释放指定类加载器的 LogFactory 实例的诊断信息。
        }
        // factories is not final and could be replaced in this block.
        final Hashtable factories = LogFactory.factories;
        synchronized (factories) {
            if (classLoader == null) {
                if (nullClassLoaderFactory != null) {
                    nullClassLoaderFactory.release();
                    nullClassLoaderFactory = null;
                    // 中文注释：若类加载器为 null，释放 nullClassLoaderFactory 实例并置空。
                }
            } else {
                final LogFactory factory = (LogFactory) factories.get(classLoader);
                if (factory != null) {
                    factory.release();
                    factories.remove(classLoader);
                    // 中文注释：释放与指定类加载器关联的 LogFactory 实例并从缓存中移除。
                }
            }
        }
    }

    /**
     * Release any internal references to previously created {@link LogFactory}
     * instances, after calling the instance method <code>release()</code> on
     * each of them.  This is useful in environments like servlet containers,
     * which implement application reloading by throwing away a ClassLoader.
     * Dangling references to objects in that class loader would prevent
     * garbage collection.
     */
    // 中文注释：释放所有已创建的 LogFactory 实例的内部引用，并在每个实例上调用 release 方法。
    // 使用场景：在 Servlet 容器等支持应用重载的环境中，通过丢弃 ClassLoader 实现重载，释放引用以避免内存泄漏。
    public static void releaseAll() {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Releasing factory for all classloaders.");
            // 中文注释：记录释放所有类加载器的 LogFactory 实例的诊断信息。
        }
        // factories is not final and could be replaced in this block.
        final Hashtable factories = LogFactory.factories;
        synchronized (factories) {
            final Enumeration elements = factories.elements();
            while (elements.hasMoreElements()) {
                LogFactory element = (LogFactory) elements.nextElement();
                element.release();
                // 中文注释：遍历所有 LogFactory 实例并调用其 release 方法。
            }
            factories.clear();
            // 中文注释：清空 factories 缓存。

            if (nullClassLoaderFactory != null) {
                nullClassLoaderFactory.release();
                nullClassLoaderFactory = null;
                // 中文注释：释放 nullClassLoaderFactory 实例并置空。
            }
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Safely get access to the classloader for the specified class.
     * <p>
     * Theoretically, calling getClassLoader can throw a security exception,
     * and so should be done under an AccessController in order to provide
     * maximum flexibility. However in practice people don't appear to use
     * security policies that forbid getClassLoader calls. So for the moment
     * all code is written to call this method rather than Class.getClassLoader,
     * so that we could put AccessController stuff in this method without any
     * disruption later if we need to.
     * <p>
     * Even when using an AccessController, however, this method can still
     * throw SecurityException. Commons-logging basically relies on the
     * ability to access classloaders, ie a policy that forbids all
     * classloader access will also prevent commons-logging from working:
     * currently this method will throw an exception preventing the entire app
     * from starting up. Maybe it would be good to detect this situation and
     * just disable all commons-logging? Not high priority though - as stated
     * above, security policies that prevent classloader access aren't common.
     * <p>
     * Note that returning an object fetched via an AccessController would
     * technically be a security flaw anyway; untrusted code that has access
     * to a trusted JCL library could use it to fetch the classloader for
     * a class even when forbidden to do so directly.
     *
     * @since 1.1
     */
    // 中文注释：安全获取指定类的类加载器。
    // 注意事项：调用 getClassLoader 可能抛出 SecurityException，因此应在 AccessController 下执行以提供最大灵活性。
    // 当前实现未使用 AccessController，因为禁止 getClassLoader 的安全策略不常见。
    // 若安全策略禁止所有类加载器访问，commons-logging 将无法工作，可能导致应用启动失败。
    // 返回通过 AccessController 获取的对象可能存在安全隐患，不信任代码可能利用受信任的 JCL 库获取类加载器。
    // 参数说明：clazz - 要获取类加载器的类。
    protected static ClassLoader getClassLoader(Class clazz) {
        try {
            return clazz.getClassLoader();
            // 中文注释：尝试获取指定类的类加载器。
        } catch (SecurityException ex) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to get classloader for class '" + clazz +
                              "' due to security restrictions - " + ex.getMessage());
                // 中文注释：记录因安全限制无法获取类加载器的诊断信息。
            }
            throw ex;
        }
    }

    /**
     * Returns the current context classloader.
     * <p>
     * In versions prior to 1.1, this method did not use an AccessController.
     * In version 1.1, an AccessController wrapper was incorrectly added to
     * this method, causing a minor security flaw.
     * <p>
     * In version 1.1.1 this change was reverted; this method no longer uses
     * an AccessController. User code wishing to obtain the context classloader
     * must invoke this method via AccessController.doPrivileged if it needs
     * support for that.
     *
     * @return the context classloader associated with the current thread,
     *  or null if security doesn't allow it.
     * @throws LogConfigurationException if there was some weird error while
     *  attempting to get the context classloader.
     */
    // 中文注释：返回当前线程的上下文类加载器。
    // 注意事项：1.1 版之前未使用 AccessController，1.1 版错误添加了 AccessController 导致轻微安全问题，1.1.1 版已移除。
    // 用户代码需通过 AccessController.doPrivileged 调用此方法以支持安全环境。
    // 返回值：当前线程的上下文类加载器，若安全策略禁止则返回 null。
    // 异常说明：若获取类加载器时发生错误，则抛出 LogConfigurationException。
    protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
        return directGetContextClassLoader();
        // 中文注释：直接调用 directGetContextClassLoader 获取上下文类加载器。
    }

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
     *  or null if security doesn't allow it.
     * @throws LogConfigurationException if there was some weird error while
     *  attempting to get the context classloader.
     */
    // 中文注释：在 AccessController 控制下调用 directGetContextClassLoader。
    // 功能说明：允许在安全管理器禁止访问类加载器的情况下工作，前提是本类具有适当权限。
    // 返回值：当前线程的上下文类加载器，若安全策略禁止则返回 null。
    // 异常说明：若获取类加载器时发生错误，则抛出 LogConfigurationException。
    private static ClassLoader getContextClassLoaderInternal() throws LogConfigurationException {
        return (ClassLoader)AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    return directGetContextClassLoader();
                }
            });
        // 中文注释：通过 AccessController 执行特权操作，调用 directGetContextClassLoader。
    }

    /**
     * Return the thread context class loader if available; otherwise return null.
     * <p>
     * Most/all code should call getContextClassLoaderInternal rather than
     * calling this method directly.
     * <p>
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * <p>
     * Note that no internal logging is done within this method because
     * this method is called every time LogFactory.getLogger() is called,
     * and we don't want too much output generated here.
     *
     * @throws LogConfigurationException if a suitable class loader
     *  cannot be identified.
     * @return the thread's context classloader or {@code null} if the java security
     *  policy forbids access to the context classloader from one of the classes
     *  in the current call stack.
     * @since 1.1
     */
    // 中文注释：返回当前线程的上下文类加载器，若不可用则返回 null。
    // 注意事项：建议调用 getContextClassLoaderInternal 而非直接调用此方法。
    // JDK 1.2+ 在满足安全条件时支持上下文类加载器。
    // 不记录内部日志，因为此方法在每次 LogFactory.getLogger() 调用时执行，避免过多输出。
    // 返回值：线程上下文类加载器，若安全策略禁止则返回 null。
    // 异常说明：若无法识别合适的类加载器，则抛出 LogConfigurationException。
    protected static ClassLoader directGetContextClassLoader() throws LogConfigurationException {
        ClassLoader classLoader = null;

        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            // 中文注释：尝试获取当前线程的上下文类加载器。
        } catch (SecurityException ex) {
            /**
             * getContextClassLoader() throws SecurityException when
             * the context class loader isn't an ancestor of the
             * calling class's class loader, or if security
             * permissions are restricted.
             *
             * We ignore this exception to be consistent with the previous
             * behavior (e.g. 1.1.3 and earlier).
             */
            // 中文注释：若上下文类加载器不是调用类类加载器的祖先，或权限受限，则抛出 SecurityException，忽略以保持与早期版本一致。
            // ignore
        }

        // Return the selected class loader
        return classLoader;
    }

    /**
     * Check cached factories (keyed by contextClassLoader)
     *
     * @param contextClassLoader is the context classloader associated
     * with the current thread. This allows separate LogFactory objects
     * per component within a container, provided each component has
     * a distinct context classloader set. This parameter may be null
     * in JDK1.1, and in embedded systems where jcl-using code is
     * placed in the bootclasspath.
     *
     * @return the factory associated with the specified classloader if
     *  one has previously been created, or null if this is the first time
     *  we have seen this particular classloader.
     */
    // 中文注释：检查缓存中是否存在与指定上下文类加载器关联的 LogFactory 实例。
    // 参数说明：contextClassLoader - 当前线程的上下文类加载器，允许容器中每个组件使用独立的 LogFactory（需设置不同的上下文类加载器）。
    // 在 JDK1.1 或嵌入式系统中，参数可能为 null。
    // 返回值：与指定类加载器关联的工厂实例，若首次遇到该类加载器则返回 null。
    private static LogFactory getCachedFactory(ClassLoader contextClassLoader) {
        if (contextClassLoader == null) {
            // We have to handle this specially, as factories is a Hashtable
            // and those don't accept null as a key value.
            //
            // nb: nullClassLoaderFactory might be null. That's ok.
            return nullClassLoaderFactory;
            // 中文注释：若上下文类加载器为 null，返回 nullClassLoaderFactory（可能为 null），因为 Hashtable 不接受 null 键。
        } else {
            return (LogFactory) factories.get(contextClassLoader);
            // 中文注释：从 factories 缓存中获取与指定类加载器关联的 LogFactory 实例。
        }
    }

    /**
     * Remember this factory, so later calls to LogFactory.getCachedFactory
     * can return the previously created object (together with all its
     * cached Log objects).
     *
     * @param classLoader should be the current context classloader. Note that
     *  this can be null under some circumstances; this is ok.
     * @param factory should be the factory to cache. This should never be null.
     */
    // 中文注释：缓存 LogFactory 实例，以便后续调用 getCachedFactory 返回之前创建的对象及其缓存的 Log 对象。
    // 参数说明：classLoader - 当前上下文类加载器，可能为 null；factory - 要缓存的工厂实例，不应为 null。
    private static void cacheFactory(ClassLoader classLoader, LogFactory factory) {
        // Ideally we would assert(factory != null) here. However reporting
        // errors from within a logging implementation is a little tricky!

        if (factory != null) {
            if (classLoader == null) {
                nullClassLoaderFactory = factory;
                // 中文注释：若类加载器为 null，将工厂实例缓存到 nullClassLoaderFactory。
            } else {
                factories.put(classLoader, factory);
                // 中文注释：将工厂实例缓存到 factories 中，以类加载器为键。
            }
        }
    }

    /**
     * Return a new instance of the specified <code>LogFactory</code>
     * implementation class, loaded by the specified class loader.
     * If that fails, try the class loader used to load this
     * (abstract) LogFactory.
     * <h2>ClassLoader conflicts</h2>
     * <p>
     * Note that there can be problems if the specified ClassLoader is not the
     * same as the classloader that loaded this class, ie when loading a
     * concrete LogFactory subclass via a context classloader.
     * <p>
     * The problem is the same one that can occur when loading a concrete Log
     * subclass via a context classloader.
     * <p>
     * The problem occurs when code running in the context classloader calls
     * class X which was loaded via a parent classloader, and class X then calls
     * LogFactory.getFactory (either directly or via LogFactory.getLog). Because
     * class X was loaded via the parent, it binds to LogFactory loaded via
     * the parent. When the code in this method finds some LogFactoryYYYY
     * class in the child (context) classloader, and there also happens to be a
     * LogFactory class defined in the child classloader, then LogFactoryYYYY
     * will be bound to LogFactory@childloader. It cannot be cast to
     * LogFactory@parentloader, ie this method cannot return the object as
     * the desired type. Note that it doesn't matter if the LogFactory class
     * in the child classloader is identical to the LogFactory class in the
     * parent classloader, they are not compatible.
     * <p>
     * The solution taken here is to simply print out an error message when
     * this occurs then throw an exception. The deployer of the application
     * must ensure they remove all occurrences of the LogFactory class from
     * the child classloader in order to resolve the issue. Note that they
     * do not have to move the custom LogFactory subclass; that is ok as
     * long as the only LogFactory class it can find to bind to is in the
     * parent classloader.
     *
     * @param factoryClass Fully qualified name of the <code>LogFactory</code>
     *  implementation class
     * @param classLoader ClassLoader from which to load this class
     * @param contextClassLoader is the context that this new factory will
     *  manage logging for.
     * @throws LogConfigurationException if a suitable instance
     *  cannot be created
     * @since 1.1
     */
    // 中文注释：创建并返回指定 LogFactory 实现类的新实例，通过指定类加载器加载，若失败则尝试使用加载本抽象类的类加载器。
    // 类加载器冲突说明：若指定类加载器与加载本类的类加载器不同，可能导致问题（如通过上下文类加载器加载具体子类）。
    // 问题场景：上下文类加载器中的代码调用父类加载器加载的类 X，类 X 调用 LogFactory.getFactory，可能导致子类加载器中的 LogFactory 类无法转换为父类加载器中的类型。
    // 解决方法：出现此类问题时打印错误信息并抛出异常，部署者需移除子类加载器中的 LogFactory 类。
    // 参数说明：factoryClass - LogFactory 实现类的全限定名；classLoader - 加载类的类加载器；contextClassLoader - 新工厂管理的上下文类加载器。
    // 异常说明：若无法创建合适实例，则抛出 LogConfigurationException。
    protected static LogFactory newFactory(final String factoryClass,
                                           final ClassLoader classLoader,
                                           final ClassLoader contextClassLoader)
        throws LogConfigurationException {
        // Note that any unchecked exceptions thrown by the createFactory
        // method will propagate out of this method; in particular a
        // ClassCastException can be thrown.
        Object result = AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    return createFactory(factoryClass, classLoader);
                }
            });
        // 中文注释：在 AccessController 下调用 createFactory 创建 LogFactory 实例。

        if (result instanceof LogConfigurationException) {
            LogConfigurationException ex = (LogConfigurationException) result;
            if (isDiagnosticsEnabled()) {
                logDiagnostic("An error occurred while loading the factory class:" + ex.getMessage());
                // 中文注释：记录加载工厂类时发生的错误的诊断信息。
            }
            throw ex;
        }
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Created object " + objectId(result) + " to manage classloader " +
                          objectId(contextClassLoader));
            // 中文注释：记录创建的 LogFactory 对象及其管理的类加载器的诊断信息。
        }
        return (LogFactory)result;
    }

    /**
     * Method provided for backwards compatibility; see newFactory version that
     * takes 3 parameters.
     * <p>
     * This method would only ever be called in some rather odd situation.
     * Note that this method is static, so overriding in a subclass doesn't
     * have any effect unless this method is called from a method in that
     * subclass. However this method only makes sense to use from the
     * getFactory method, and as that is almost always invoked via
     * LogFactory.getFactory, any custom definition in a subclass would be
     * pointless. Only a class with a custom getFactory method, then invoked
     * directly via CustomFactoryImpl.getFactory or similar would ever call
     * this. Anyway, it's here just in case, though the "managed class loader"
     * value output to the diagnostics will not report the correct value.
     */
    // 中文注释：为向后兼容提供的 newFactory 方法，仅接受两个参数。
    // 注意事项：此方法仅在特殊情况下调用，子类重写无效果，除非从子类方法调用。
    // 通常通过 LogFactory.getFactory 调用，子类自定义无意义，仅为特殊情况保留。
    protected static LogFactory newFactory(final String factoryClass,
                                           final ClassLoader classLoader) {
        return newFactory(factoryClass, classLoader, null);
        // 中文注释：调用三参数版本的 newFactory，上下文类加载器设为 null。
    }

    /**
     * Implements the operations described in the javadoc for newFactory.
     *
     * @param factoryClass
     * @param classLoader used to load the specified factory class. This is
     *  expected to be either the TCCL or the classloader which loaded this
     *  class. Note that the classloader which loaded this class might be
     *  "null" (ie the bootloader) for embedded systems.
     * @return either a LogFactory object or a LogConfigurationException object.
     * @since 1.1
     */
    // 中文注释：实现 newFactory 方法的操作，加载并创建指定 LogFactory 实现类的实例。
    // 参数说明：factoryClass - 实现类的全限定名；classLoader - 用于加载实现类的类加载器（TCCL 或本类的类加载器，可能为 null）。
    // 返回值：LogFactory 对象或 LogConfigurationException 对象。
    protected static Object createFactory(String factoryClass, ClassLoader classLoader) {
        // This will be used to diagnose bad configurations
        // and allow a useful message to be sent to the user
        Class logFactoryClass = null;
        // 中文注释：定义用于诊断配置错误的类变量。
        try {
            if (classLoader != null) {
                try {
                    // First the given class loader param (thread class loader)

                    // Warning: must typecast here & allow exception
                    // to be generated/caught & recast properly.
                    logFactoryClass = classLoader.loadClass(factoryClass);
                    if (LogFactory.class.isAssignableFrom(logFactoryClass)) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Loaded class " + logFactoryClass.getName() +
                                          " from classloader " + objectId(classLoader));
                            // 中文注释：记录成功从指定类加载器加载实现类的诊断信息。
                        }
                    } else {
                        //
                        // This indicates a problem with the ClassLoader tree.
                        // An incompatible ClassLoader was used to load the
                        // implementation.
                        // As the same classes
                        // must be available in multiple class loaders,
                        // it is very likely that multiple JCL jars are present.
                        // The most likely fix for this
                        // problem is to remove the extra JCL jars from the
                        // ClassLoader hierarchy.
                        //
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Factory class " + logFactoryClass.getName() +
                                          " loaded from classloader " + objectId(logFactoryClass.getClassLoader()) +
                                          " does not extend '" + LogFactory.class.getName() +
                                          "' as loaded by this classloader.");
                            logHierarchy("[BAD CL TREE] ", classLoader);
                            // 中文注释：记录实现类未继承 LogFactory 的诊断信息，可能是类加载器树不兼容，建议移除多余的 JCL jar。
                        }
                    }

                    return (LogFactory) logFactoryClass.newInstance();
                    // 中文注释：实例化并返回 LogFactory 实现类对象。

                } catch (ClassNotFoundException ex) {
                    if (classLoader == thisClassLoader) {
                        // Nothing more to try, onwards.
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Unable to locate any class called '" + factoryClass +
                                          "' via classloader " + objectId(classLoader));
                            // 中文注释：记录通过指定类加载器无法找到实现类的诊断信息。
                        }
                        throw ex;
                    }
                    // ignore exception, continue
                    // 中文注释：忽略异常，继续尝试其他类加载器。
                } catch (NoClassDefFoundError e) {
                    if (classLoader == thisClassLoader) {
                        // Nothing more to try, onwards.
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Class '" + factoryClass + "' cannot be loaded" +
                                          " via classloader " + objectId(classLoader) +
                                          " - it depends on some other class that cannot be found.");
                            // 中文注释：记录因依赖类缺失无法加载实现类的诊断信息。
                        }
                        throw e;
                    }
                    // ignore exception, continue
                    // 中文注释：忽略异常，继续尝试其他类加载器。
                } catch (ClassCastException e) {
                    if (classLoader == thisClassLoader) {
                        // There's no point in falling through to the code below that
                        // tries again with thisClassLoader, because we've just tried
                        // loading with that loader (not the TCCL). Just throw an
                        // appropriate exception here.

                        final boolean implementsLogFactory = implementsLogFactory(logFactoryClass);

                        //
                        // Construct a good message: users may not actual expect that a custom implementation
                        // has been specified. Several well known containers use this mechanism to adapt JCL
                        // to their native logging system.
                        //
                        final StringBuffer msg = new StringBuffer();
                        msg.append("The application has specified that a custom LogFactory implementation ");
                        msg.append("should be used but Class '");
                        msg.append(factoryClass);
                        msg.append("' cannot be converted to '");
                        msg.append(LogFactory.class.getName());
                        msg.append("'. ");
                        if (implementsLogFactory) {
                            msg.append("The conflict is caused by the presence of multiple LogFactory classes ");
                            msg.append("in incompatible classloaders. ");
                            msg.append("Background can be found in http://commons.apache.org/logging/tech.html. ");
                            msg.append("If you have not explicitly specified a custom LogFactory then it is likely ");
                            msg.append("that the container has set one without your knowledge. ");
                            msg.append("In this case, consider using the commons-logging-adapters.jar file or ");
                            msg.append("specifying the standard LogFactory from the command line. ");
                        } else {
                            msg.append("Please check the custom implementation. ");
                        }
                        msg.append("Help can be found @http://commons.apache.org/logging/troubleshooting.html.");

                        if (isDiagnosticsEnabled()) {
                            logDiagnostic(msg.toString());
                            // 中文注释：记录类转换异常的详细诊断信息，提示类加载器冲突或实现类问题。
                        }

                        throw new ClassCastException(msg.toString());
                        // 中文注释：抛出类转换异常，包含详细错误信息。
                    }

                    // Ignore exception, continue. Presumably the classloader was the
                    // TCCL; the code below will try to load the class via thisClassLoader.
                    // This will handle the case where the original calling class is in
                    // a shared classpath but the TCCL has a copy of LogFactory and the
                    // specified LogFactory implementation; we will fall back to using the
                    // LogFactory implementation from the same classloader as this class.
                    //
                    // Issue: this doesn't handle the reverse case, where this LogFactory
                    // is in the webapp, and the specified LogFactory implementation is
                    // in a shared classpath. In that case:
                    // (a) the class really does implement LogFactory (bad log msg above)
                    // (b) the fallback code will result in exactly the same problem.
                    // 中文注释：忽略类转换异常，继续尝试使用 thisClassLoader 加载。
                    // 处理场景：调用类在共享类路径中，但 TCCL 包含 LogFactory 副本，将回退到使用与本类相同的类加载器。
                    // 注意事项：无法处理反向情况（本 LogFactory 在 Web 应用中，指定实现类在共享类路径中）。
                }
            }

            /* At this point, either classLoader == null, OR
             * classLoader was unable to load factoryClass.
             *
             * In either case, we call Class.forName, which is equivalent
             * to LogFactory.class.getClassLoader().load(name), ie we ignore
             * the classloader parameter the caller passed, and fall back
             * to trying the classloader associated with this class. See the
             * javadoc for the newFactory method for more info on the
             * consequences of this.
             *
             * Notes:
             * * LogFactory.class.getClassLoader() may return 'null'
             *   if LogFactory is loaded by the bootstrap classloader.
             */
            // Warning: must typecast here & allow exception
            // to be generated/caught & recast properly.
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to load factory class via classloader " + objectId(classLoader) +
                              " - trying the classloader associated with this LogFactory.");
                // 中文注释：记录无法通过指定类加载器加载工厂类，尝试使用本类的类加载器的诊断信息。
            }
            logFactoryClass = Class.forName(factoryClass);
            return (LogFactory) logFactoryClass.newInstance();
            // 中文注释：通过 Class.forName 加载工厂类并实例化，返回 LogFactory 对象。
        } catch (Exception e) {
            // Check to see if we've got a bad configuration
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to create LogFactory instance.");
                // 中文注释：记录无法创建 LogFactory 实例的诊断信息。
            }
            if (logFactoryClass != null && !LogFactory.class.isAssignableFrom(logFactoryClass)) {
                return new LogConfigurationException(
                    "The chosen LogFactory implementation does not extend LogFactory." +
                    " Please check your configuration.", e);
                // 中文注释：若加载的类不继承 LogFactory，返回包含配置错误的 LogConfigurationException。
            }
            return new LogConfigurationException(e);
            // 中文注释：返回包含异常信息的 LogConfigurationException。
        }
    }

    /**
     * Determines whether the given class actually implements <code>LogFactory</code>.
     * Diagnostic information is also logged.
     * <p>
     * <strong>Usage:</strong> to diagnose whether a classloader conflict is the cause
     * of incompatibility. The test used is whether the class is assignable from
     * the <code>LogFactory</code> class loaded by the class's classloader.
     * @param logFactoryClass <code>Class</code> which may implement <code>LogFactory</code>
     * @return true if the <code>logFactoryClass</code> does extend
     * <code>LogFactory</code> when that class is loaded via the same
     * classloader that loaded the <code>logFactoryClass</code>.
     */
    // 中文注释：判断指定类是否实际实现了 LogFactory 接口，并记录诊断信息。
    // 使用场景：诊断类加载器冲突是否导致不兼容，测试类是否可分配给由其类加载器加载的 LogFactory 类。
    // 参数说明：logFactoryClass - 可能实现 LogFactory 的类。
    // 返回值：若类通过其类加载器加载的 LogFactory 可分配，则返回 true。
    private static boolean implementsLogFactory(Class logFactoryClass) {
        boolean implementsLogFactory = false;
        if (logFactoryClass != null) {
            try {
                ClassLoader logFactoryClassLoader = logFactoryClass.getClassLoader();
                if (logFactoryClassLoader == null) {
                    logDiagnostic("[CUSTOM LOG FACTORY] was loaded by the boot classloader");
                    // 中文注释：记录自定义工厂类由引导类加载器加载的诊断信息。
                } else {
                    logHierarchy("[CUSTOM LOG FACTORY] ", logFactoryClassLoader);
                    Class factoryFromCustomLoader
                        = Class.forName("org.apache.commons.logging.LogFactory", false, logFactoryClassLoader);
                    implementsLogFactory = factoryFromCustomLoader.isAssignableFrom(logFactoryClass);
                    if (implementsLogFactory) {
                        logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() +
                                      " implements LogFactory but was loaded by an incompatible classloader.");
                        // 中文注释：记录类实现 LogFactory 但由不兼容类加载器加载的诊断信息。
                    } else {
                        logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() +
                                      " does not implement LogFactory.");
                        // 中文注释：记录类未实现 LogFactory 的诊断信息。
                    }
                }
            } catch (SecurityException e) {
                //
                // The application is running within a hostile security environment.
                // This will make it very hard to diagnose issues with JCL.
                // Consider running less securely whilst debugging this issue.
                //
                logDiagnostic("[CUSTOM LOG FACTORY] SecurityException thrown whilst trying to determine whether " +
                              "the compatibility was caused by a classloader conflict: " + e.getMessage());
                // 中文注释：记录因安全异常无法确定类加载器冲突的诊断信息，建议降低安全级别以调试。
            } catch (LinkageError e) {
                //
                // This should be an unusual circumstance.
                // LinkageError's usually indicate that a dependent class has incompatibly changed.
                // Another possibility may be an exception thrown by an initializer.
                // Time for a clean rebuild?
                //
                logDiagnostic("[CUSTOM LOG FACTORY] LinkageError thrown whilst trying to determine whether " +
                              "the compatibility was caused by a classloader conflict: " + e.getMessage());
                // 中文注释：记录因 LinkageError 无法确定类加载器冲突的诊断信息，可能需重新构建。
            } catch (ClassNotFoundException e) {
                //
                // LogFactory cannot be loaded by the classloader which loaded the custom factory implementation.
                // The custom implementation is not viable until this is corrected.
                // Ensure that the JCL jar and the custom class are available from the same classloader.
                // Running with diagnostics on should give information about the classloaders used
                // to load the custom factory.
                //
                logDiagnostic("[CUSTOM LOG FACTORY] LogFactory class cannot be loaded by classloader which loaded " +
                              "the custom LogFactory implementation. Is the custom factory in the right classloader?");
                // 中文注释：记录因类加载器无法加载 LogFactory 类导致自定义实现不可用的诊断信息，需确保 JCL jar 和自定义类在同一类加载器中。
            }
        }
        return implementsLogFactory;
    }

    /**
     * Applets may run in an environment where accessing resources of a loader is
     * a secure operation, but where the commons-logging library has explicitly
     * been granted permission for that operation. In this case, we need to
     * run the operation using an AccessController.
     */
    // 中文注释：Applet 环境中访问类加载器资源可能需权限，commons-logging 库若被授权，需通过 AccessController 执行。
    // 方法目的：安全获取指定资源的输入流。
    // 参数说明：loader - 用于加载资源的类加载器；name - 资源名称。
    // 特殊处理：若 loader 为 null，使用系统类加载器。
    private static InputStream getResourceAsStream(final ClassLoader loader, final String name) {
        return (InputStream)AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    if (loader != null) {
                        return loader.getResourceAsStream(name);
                    } else {
                        return ClassLoader.getSystemResourceAsStream(name);
                    }
                }
            });
        // 中文注释：通过 AccessController 执行特权操作，获取指定类加载器的资源输入流。
        // 关键步骤：检查 loader 是否为 null，选择适当的类加载器获取资源。
    }

    /**
     * Given a filename, return an enumeration of URLs pointing to
     * all the occurrences of that filename in the classpath.
     * <p>
     * This is just like ClassLoader.getResources except that the
     * operation is done under an AccessController so that this method will
     * succeed when this jarfile is privileged but the caller is not.
     * This method must therefore remain private to avoid security issues.
     * <p>
     * If no instances are found, an Enumeration is returned whose
     * hasMoreElements method returns false (ie an "empty" enumeration).
     * If resources could not be listed for some reason, null is returned.
     */
    // 中文注释：根据文件名返回类路径中所有匹配资源的 URL 枚举。
    // 方法目的：查找类路径中指定文件的 URL，支持权限分离。
    // 参数说明：loader - 用于加载资源的类加载器；name - 文件名。
    // 特殊处理：需保持私有以避免安全问题；若无法列出资源或 JDK 1.1 不支持，返回 null。
    // 事件处理：捕获 IOException 和 NoSuchMethodError，记录诊断信息并返回 null。
    private static Enumeration getResources(final ClassLoader loader, final String name) {
        PrivilegedAction action =
            new PrivilegedAction() {
                public Object run() {
                    try {
                        if (loader != null) {
                            return loader.getResources(name);
                        } else {
                            return ClassLoader.getSystemResources(name);
                        }
                    } catch (IOException e) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Exception while trying to find configuration file " +
                                          name + ":" + e.getMessage());
                            // 中文注释：记录查找配置文件时发生的 IO 异常信息。
                        }
                        return null;
                    } catch (NoSuchMethodError e) {
                        // we must be running on a 1.1 JVM which doesn't support
                        // ClassLoader.getSystemResources; just return null in
                        // this case.
                        // 中文注释：在 JDK 1.1 中不支持 ClassLoader.getSystemResources，返回 null。
                        return null;
                    }
                }
            };
        Object result = AccessController.doPrivileged(action);
        return (Enumeration) result;
        // 中文注释：通过 AccessController 执行特权操作，返回资源 URL 枚举。
        // 关键步骤：根据 loader 是否为 null，选择适当的类加载器获取资源。
    }

    /**
     * Given a URL that refers to a .properties file, load that file.
     * This is done under an AccessController so that this method will
     * succeed when this jarfile is privileged but the caller is not.
     * This method must therefore remain private to avoid security issues.
     * <p>
     * {@code Null} is returned if the URL cannot be opened.
     */
    // 中文注释：加载指定 URL 的 .properties 文件。
    // 方法目的：读取属性文件内容，支持权限分离。
    // 参数说明：url - 指向属性文件的 URL。
    // 特殊处理：需保持私有以避免安全问题；禁用缓存以防 Windows 文件锁定；无法打开 URL 返回 null。
    // 事件处理：捕获 IOException，记录诊断信息并确保关闭输入流。
    private static Properties getProperties(final URL url) {
        PrivilegedAction action =
            new PrivilegedAction() {
                public Object run() {
                    InputStream stream = null;
                    try {
                        // We must ensure that useCaches is set to false, as the
                        // default behaviour of java is to cache file handles, and
                        // this "locks" files, preventing hot-redeploy on windows.
                        URLConnection connection = url.openConnection();
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                        // 中文注释：打开 URL 连接，禁用缓存以避免 Windows 文件锁定，获取输入流。
                        if (stream != null) {
                            Properties props = new Properties();
                            props.load(stream);
                            stream.close();
                            stream = null;
                            return props;
                            // 中文注释：若输入流有效，加载属性到 Properties 对象，关闭流并返回。
                        }
                    } catch (IOException e) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Unable to read URL " + url);
                            // 中文注释：记录无法读取 URL 的诊断信息。
                        }
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                // ignore exception; this should not happen
                                if (isDiagnosticsEnabled()) {
                                    logDiagnostic("Unable to close stream for URL " + url);
                                    // 中文注释：记录无法关闭输入流的诊断信息。
                                }
                            }
                        }
                    }

                    return null;
                    // 中文注释：若发生异常或无法打开 URL，返回 null。
                }
            };
        return (Properties) AccessController.doPrivileged(action);
        // 中文注释：通过 AccessController 执行特权操作，返回加载的 Properties 对象。
    }

    /**
     * Locate a user-provided configuration file.
     * <p>
     * The classpath of the specified classLoader (usually the context classloader)
     * is searched for properties files of the specified name. If none is found,
     * null is returned. If more than one is found, then the file with the greatest
     * value for its PRIORITY property is returned. If multiple files have the
     * same PRIORITY value then the first in the classpath is returned.
     * <p>
     * This differs from the 1.0.x releases; those always use the first one found.
     * However as the priority is a new field, this change is backwards compatible.
     * <p>
     * The purpose of the priority field is to allow a webserver administrator to
     * override logging settings in all webapps by placing a commons-logging.properties
     * file in a shared classpath location with a priority > 0; this overrides any
     * commons-logging.properties files without priorities which are in the
     * webapps. Webapps can also use explicit priorities to override a configuration
     * file in the shared classpath if needed.
     */
    // 中文注释：查找用户提供的配置文件（commons-logging.properties）。
    // 方法目的：在类路径中搜索指定名称的属性文件，返回优先级最高的配置文件。
    // 参数说明：classLoader - 用于搜索的类加载器（通常为上下文类加载器）；fileName - 配置文件名。
    // 重要配置参数：PRIORITY_KEY（priority） - 属性文件中的优先级键，值越高优先级越高，默认为 0.0。
    // 关键步骤：遍历类路径中的配置文件 URL，选择优先级最高的 Properties 对象。
    // 特殊处理：若多个文件优先级相同，选择类路径中第一个；1.0.x 版本总是选第一个，优先级字段为新功能，保持向后兼容。
    // 事件处理：捕获 SecurityException，记录诊断信息。
    // 使用场景：允许管理员通过高优先级配置文件覆盖 Web 应用的日志设置。
    private static final Properties getConfigurationFile(ClassLoader classLoader, String fileName) {
        Properties props = null;
        double priority = 0.0;
        URL propsUrl = null;
        // 中文注释：初始化属性对象、优先级值和配置文件 URL。
        // 关键变量用途：props - 存储最终选定的属性；priority - 当前最高优先级；propsUrl - 选定配置文件的 URL。
        try {
            Enumeration urls = getResources(classLoader, fileName);
            // 中文注释：获取类路径中所有匹配文件名的资源 URL 枚举。

            if (urls == null) {
                return null;
                // 中文注释：若未找到资源，返回 null。
            }

            while (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                // 中文注释：遍历资源 URL。

                Properties newProps = getProperties(url);
                if (newProps != null) {
                    if (props == null) {
                        propsUrl = url;
                        props = newProps;
                        String priorityStr = props.getProperty(PRIORITY_KEY);
                        priority = 0.0;
                        if (priorityStr != null) {
                            priority = Double.parseDouble(priorityStr);
                        }
                        // 中文注释：首次找到有效属性文件，保存 URL 和属性，解析优先级（默认 0.0）。

                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("[LOOKUP] Properties file found at '" + url + "'" +
                                          " with priority " + priority);
                            // 中文注释：记录找到配置文件及其优先级的诊断信息。
                        }
                    } else {
                        String newPriorityStr = newProps.getProperty(PRIORITY_KEY);
                        double newPriority = 0.0;
                        if (newPriorityStr != null) {
                            newPriority = Double.parseDouble(newPriorityStr);
                        }
                        // 中文注释：获取新配置文件的优先级（默认 0.0）。

                        if (newPriority > priority) {
                            if (isDiagnosticsEnabled()) {
                                logDiagnostic("[LOOKUP] Properties file at '" + url + "'" +
                                              " with priority " + newPriority +
                                              " overrides file at '" + propsUrl + "'" +
                                              " with priority " + priority);
                                // 中文注释：记录新配置文件因优先级更高覆盖旧文件的诊断信息。
                            }

                            propsUrl = url;
                            props = newProps;
                            priority = newPriority;
                            // 中文注释：更新属性对象、URL 和优先级。
                        } else {
                            if (isDiagnosticsEnabled()) {
                                logDiagnostic("[LOOKUP] Properties file at '" + url + "'" +
                                              " with priority " + newPriority +
                                              " does not override file at '" + propsUrl + "'" +
                                              " with priority " + priority);
                                // 中文注释：记录新配置文件因优先级不高于旧文件未覆盖的诊断信息。
                            }
                        }
                    }

                }
            }
        } catch (SecurityException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("SecurityException thrown while trying to find/read config files.");
                // 中文注释：记录因安全异常无法查找或读取配置文件的诊断信息。
            }
        }

        if (isDiagnosticsEnabled()) {
            if (props == null) {
                logDiagnostic("[LOOKUP] No properties file of name '" + fileName + "' found.");
                // 中文注释：记录未找到指定名称的配置文件的诊断信息。
            } else {
                logDiagnostic("[LOOKUP] Properties file of name '" + fileName + "' found at '" + propsUrl + '"');
                // 中文注释：记录找到配置文件及其 URL 的诊断信息。
            }
        }

        return props;
        // 中文注释：返回最终选定的 Properties 对象。
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
    // 中文注释：读取指定系统属性，支持 JCL 有权限但调用者无权限的情况。
    // 方法目的：安全获取系统属性值。
    // 参数说明：key - 系统属性键；def - 默认值。
    // 特殊处理：通过 AccessController 执行，防止调用者访问未授权数据。
    private static String getSystemProperty(final String key, final String def)
        throws SecurityException {
        return (String) AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        return System.getProperty(key, def);
                    }
                });
        // 中文注释：通过 AccessController 获取系统属性值，若不存在返回默认值。
    }

    /**
     * Determines whether the user wants internal diagnostic output. If so,
     * returns an appropriate writer object. Users can enable diagnostic
     * output by setting the system property named {@link #DIAGNOSTICS_DEST_PROPERTY} to
     * a filename, or the special values STDOUT or STDERR.
     */
    // 中文注释：判断是否启用内部诊断输出，返回适当的输出流。
    // 方法目的：初始化诊断输出流。
    // 重要配置参数：DIAGNOSTICS_DEST_PROPERTY（org.apache.commons.logging.diagnostics.dest） - 指定诊断输出目标（文件名、STDOUT 或 STDERR）。
    // 关键步骤：检查系统属性，选择输出目标（System.out、System.err 或文件流）。
    // 特殊处理：若无权限或文件打开失败，返回 null。
    // 事件处理：捕获 SecurityException 和 IOException，记录诊断信息。
    private static PrintStream initDiagnostics() {
        String dest;
        try {
            dest = getSystemProperty(DIAGNOSTICS_DEST_PROPERTY, null);
            if (dest == null) {
                return null;
                // 中文注释：若未设置诊断输出目标，返回 null。
            }
        } catch (SecurityException ex) {
            // We must be running in some very secure environment.
            // We just have to assume output is not wanted..
            return null;
            // 中文注释：若因安全异常无法获取系统属性，假设无需输出，返回 null。
        }

        if (dest.equals("STDOUT")) {
            return System.out;
            // 中文注释：若目标为 STDOUT，返回 System.out。
        } else if (dest.equals("STDERR")) {
            return System.err;
            // 中文注释：若目标为 STDERR，返回 System.err。
        } else {
            try {
                // open the file in append mode
                FileOutputStream fos = new FileOutputStream(dest, true);
                return new PrintStream(fos);
                // 中文注释：以追加模式打开指定文件，返回 PrintStream 对象。
            } catch (IOException ex) {
                // We should report this to the user - but how?
                return null;
                // 中文注释：若打开文件失败，返回 null。
            }
        }
    }

    /**
     * Indicates true if the user has enabled internal logging.
     * <p>
     * By the way, sorry for the incorrect grammar, but calling this method
     * areDiagnosticsEnabled just isn't java beans style.
     *
     * @return true if calls to logDiagnostic will have any effect.
     * @since 1.1
     */
    // 中文注释：检查是否启用内部日志记录。
    // 方法目的：判断诊断输出是否有效。
    // 返回值：若 diagnosticsStream 非 null，返回 true。
    // 特殊处理：方法名未遵循 JavaBeans 风格（使用 isDiagnosticsEnabled 而非 areDiagnosticsEnabled）。
    protected static boolean isDiagnosticsEnabled() {
        return diagnosticsStream != null;
        // 中文注释：检查 diagnosticsStream 是否非 null。
        // 关键变量用途：diagnosticsStream - 诊断输出流，决定是否启用诊断。
    }

    /**
     * Write the specified message to the internal logging destination.
     * <p>
     * Note that this method is private; concrete subclasses of this class
     * should not call it because the diagnosticPrefix string this
     * method puts in front of all its messages is LogFactory@....,
     * while subclasses should put SomeSubClass@...
     * <p>
     * Subclasses should instead compute their own prefix, then call
     * logRawDiagnostic. Note that calling isDiagnosticsEnabled is
     * fine for subclasses.
     * <p>
     * Note that it is safe to call this method before initDiagnostics
     * is called; any output will just be ignored (as isDiagnosticsEnabled
     * will return false).
     *
     * @param msg is the diagnostic message to be output.
     */
    // 中文注释：将指定消息写入内部日志目标，添加 LogFactory 前缀。
    // 方法目的：记录带前缀的诊断信息。
    // 参数说明：msg - 要输出的诊断消息。
    // 特殊处理：方法为私有，子类应使用 logRawDiagnostic 并定义自己的前缀；在 initDiagnostics 前调用安全，输出将被忽略。
    // 关键变量用途：diagnosticPrefix - 日志消息前缀，格式为 [LogFactory from classloader OID]。
    private static final void logDiagnostic(String msg) {
        if (diagnosticsStream != null) {
            diagnosticsStream.print(diagnosticPrefix);
            diagnosticsStream.println(msg);
            diagnosticsStream.flush();
            // 中文注释：若诊断流存在，打印前缀和消息，并刷新流。
            // 交互逻辑：确保诊断信息按顺序输出并立即写入目标。
        }
    }

    /**
     * Write the specified message to the internal logging destination.
     *
     * @param msg is the diagnostic message to be output.
     * @since 1.1
     */
    // 中文注释：将指定消息写入内部日志目标，不添加前缀。
    // 方法目的：记录原始诊断信息，供子类使用。
    // 参数说明：msg - 要输出的诊断消息。
    // 交互逻辑：直接输出消息到诊断流，适合子类自定义前缀。
    protected static final void logRawDiagnostic(String msg) {
        if (diagnosticsStream != null) {
            diagnosticsStream.println(msg);
            diagnosticsStream.flush();
            // 中文注释：若诊断流存在，打印消息并刷新流。
        }
    }

    /**
     * Generate useful diagnostics regarding the classloader tree for
     * the specified class.
     * <p>
     * As an example, if the specified class was loaded via a webapp's
     * classloader, then you may get the following output:
     * <pre>
     * Class com.acme.Foo was loaded via classloader 11111
     * ClassLoader tree: 11111 -> 22222 (SYSTEM) -> 33333 -> BOOT
     * </pre>
     * <p>
     * This method returns immediately if isDiagnosticsEnabled()
     * returns false.
     *
     * @param clazz is the class whose classloader + tree are to be
     * output.
     */
    // 中文注释：生成指定类的类加载器树诊断信息。
    // 方法目的：记录类加载器及其层次结构，辅助调试。
    // 参数说明：clazz - 要输出类加载器信息的类。
    // 关键步骤：检查诊断是否启用，记录扩展目录、类路径和类加载器树。
    // 特殊处理：若未启用诊断或因安全限制无法访问，提前返回。
    // 事件处理：捕获 SecurityException，记录诊断信息。
    private static void logClassLoaderEnvironment(Class clazz) {
        if (!isDiagnosticsEnabled()) {
            return;
            // 中文注释：若未启用诊断，立即返回。
        }

        try {
            // Deliberately use System.getProperty here instead of getSystemProperty; if
            // the overall security policy for the calling application forbids access to
            // these variables then we do not want to output them to the diagnostic stream.
            logDiagnostic("[ENV] Extension directories (java.ext.dir): " + System.getProperty("java.ext.dir"));
            logDiagnostic("[ENV] Application classpath (java.class.path): " + System.getProperty("java.class.path"));
            // 中文注释：记录扩展目录和类路径信息，使用 System.getProperty 避免安全问题。
        } catch (SecurityException ex) {
            logDiagnostic("[ENV] Security setting prevent interrogation of system classpaths.");
            // 中文注释：记录因安全限制无法查询类路径的诊断信息。
        }

        String className = clazz.getName();
        ClassLoader classLoader;
        // 中文注释：初始化类名和类加载器变量。
        // 关键变量用途：className - 类全限定名；classLoader - 加载指定类的类加载器。

        try {
            classLoader = getClassLoader(clazz);
            // 中文注释：获取指定类的类加载器。
        } catch (SecurityException ex) {
            // not much useful diagnostics we can print here!
            logDiagnostic("[ENV] Security forbids determining the classloader for " + className);
            // 中文注释：记录因安全限制无法获取类加载器的诊断信息。
            return;
        }

        logDiagnostic("[ENV] Class " + className + " was loaded via classloader " + objectId(classLoader));
        logHierarchy("[ENV] Ancestry of classloader which loaded " + className + " is ", classLoader);
        // 中文注释：记录类加载器信息并调用 logHierarchy 记录其层次结构。
    }

    /**
     * Logs diagnostic messages about the given classloader
     * and it's hierarchy. The prefix is prepended to the message
     * and is intended to make it easier to understand the logs.
     * @param prefix
     * @param classLoader
     */
    // 中文注释：记录指定类加载器及其层次结构的诊断信息。
    // 方法目的：输出类加载器树结构，辅助调试类加载问题。
    // 参数说明：prefix - 日志消息前缀；classLoader - 要记录的类加载器。
    // 关键步骤：记录类加载器标识，遍历其父加载器构建树结构。
    // 特殊处理：若未启用诊断或因安全限制无法访问，提前返回。
    // 事件处理：捕获 SecurityException，标记为 SECRET 并终止遍历。
    private static void logHierarchy(String prefix, ClassLoader classLoader) {
        if (!isDiagnosticsEnabled()) {
            return;
            // 中文注释：若未启用诊断，立即返回。
        }
        ClassLoader systemClassLoader;
        if (classLoader != null) {
            final String classLoaderString = classLoader.toString();
            logDiagnostic(prefix + objectId(classLoader) + " == '" + classLoaderString + "'");
            // 中文注释：记录类加载器的标识和字符串表示。
            // 关键变量用途：classLoaderString - 类加载器的字符串表示，增强日志可读性。
        }

        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
            // 中文注释：获取系统类加载器。
        } catch (SecurityException ex) {
            logDiagnostic(prefix + "Security forbids determining the system classloader.");
            // 中文注释：记录因安全限制无法获取系统类加载器的诊断信息。
            return;
        }
        if (classLoader != null) {
            final StringBuffer buf = new StringBuffer(prefix + "ClassLoader tree:");
            for(;;) {
                buf.append(objectId(classLoader));
                if (classLoader == systemClassLoader) {
                    buf.append(" (SYSTEM) ");
                    // 中文注释：若当前类加载器为系统类加载器，添加 (SYSTEM) 标记。
                }

                try {
                    classLoader = classLoader.getParent();
                    // 中文注释：获取父类加载器。
                } catch (SecurityException ex) {
                    buf.append(" --> SECRET");
                    break;
                    // 中文注释：若因安全限制无法获取父类加载器，标记为 SECRET 并终止循环。
                }

                buf.append(" --> ");
                if (classLoader == null) {
                    buf.append("BOOT");
                    break;
                    // 中文注释：若类加载器为 null，标记为 BOOT 并终止循环。
                }
            }
            logDiagnostic(buf.toString());
            // 中文注释：记录完整的类加载器树结构。
        }
    }

    /**
     * Returns a string that uniquely identifies the specified object, including
     * its class.
     * <p>
     * The returned string is of form "classname@hashcode", ie is the same as
     * the return value of the Object.toString() method, but works even when
     * the specified object's class has overidden the toString method.
     *
     * @param o may be null.
     * @return a string of form classname@hashcode, or "null" if param o is null.
     * @since 1.1
     */
    // 中文注释：返回唯一标识对象的字符串，包含类名和哈希码。
    // 方法目的：生成对象标识，适用于诊断日志。
    // 参数说明：o - 要标识的对象，可能为 null。
    // 返回值：若对象为 null，返回 "null"；否则返回 "类名@哈希码"。
    // 特殊处理：即使对象类重写了 toString 方法，仍返回标准格式。
    public static String objectId(Object o) {
        if (o == null) {
            return "null";
        } else {
            return o.getClass().getName() + "@" + System.identityHashCode(o);
        }
        // 中文注释：根据对象是否为 null，返回 "null" 或类名与哈希码组合的字符串。
    }

    // ----------------------------------------------------------------------
    // Static initialiser block to perform initialisation at class load time.
    //
    // We can't do this in the class constructor, as there are many
    // static methods on this class that can be called before any
    // LogFactory instances are created, and they depend upon this
    // stuff having been set up.
    //
    // Note that this block must come after any variable declarations used
    // by any methods called from this block, as we want any static initialiser
    // associated with the variable to run first. If static initialisers for
    // variables run after this code, then (a) their value might be needed
    // by methods called from here, and (b) they might *override* any value
    // computed here!
    //
    // So the wisest thing to do is just to place this code at the very end
    // of the class file.
    // ----------------------------------------------------------------------
    // 中文注释：静态初始化块，在类加载时执行初始化。
    // 方法目的：设置类加载器、诊断前缀、输出流和工厂存储。
    // 关键步骤：初始化 thisClassLoader、diagnosticPrefix、diagnosticsStream 和 factories。
    // 特殊处理：需置于类文件末尾，确保变量的静态初始化器先执行；诊断输出在 initDiagnostics 前调用安全。
    // 事件处理：捕获 SecurityException，设置默认类加载器名称。
    // 关键变量用途：
    //   - thisClassLoader：缓存加载本类的类加载器。
    //   - diagnosticPrefix：诊断日志前缀，格式为 [LogFactory from classloader OID]。
    //   - diagnosticsStream：诊断输出流。
    //   - factories：存储 LogFactory 实例的 Hashtable。
    static {
        // note: it's safe to call methods before initDiagnostics (though
        // diagnostic output gets discarded).
        thisClassLoader = getClassLoader(LogFactory.class);
        // 中文注释：获取并缓存加载 LogFactory 类的类加载器。
        // In order to avoid confusion where multiple instances of JCL are
        // being used via different classloaders within the same app, we
        // ensure each logged message has a prefix of form
        // [LogFactory from classloader OID]
        //
        // Note that this prefix should be kept consistent with that
        // in LogFactoryImpl. However here we don't need to output info
        // about the actual *instance* of LogFactory, as all methods that
        // output diagnostics from this class are static.
        String classLoaderName;
        try {
            ClassLoader classLoader = thisClassLoader;
            if (thisClassLoader == null) {
                classLoaderName = "BOOTLOADER";
            } else {
                classLoaderName = objectId(classLoader);
            }
            // 中文注释：设置类加载器名称，若为 null 则为 "BOOTLOADER"，否则使用 objectId 生成。
        } catch (SecurityException e) {
            classLoaderName = "UNKNOWN";
            // 中文注释：若因安全异常无法获取类加载器，设为 "UNKNOWN"。
        }
        diagnosticPrefix = "[LogFactory from " + classLoaderName + "] ";
        // 中文注释：设置诊断日志前缀，包含类加载器名称。
        diagnosticsStream = initDiagnostics();
        // 中文注释：初始化诊断输出流。
        logClassLoaderEnvironment(LogFactory.class);
        // 中文注释：记录 LogFactory 类的类加载器环境信息。
        factories = createFactoryStore();
        // 中文注释：创建并初始化存储 LogFactory 实例的 Hashtable。
        if (isDiagnosticsEnabled()) {
            logDiagnostic("BOOTSTRAP COMPLETED");
            // 中文注释：记录启动初始化完成的诊断信息。
        }
    }
}
