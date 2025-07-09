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
package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.naming.NamingException;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * This class can be used to define a custom logger repository. It makes use of the fact that in J2EE environments, each
 * web-application is guaranteed to have its own JNDI context relative to the <code>java:comp/env</code> context. In
 * EJBs, each enterprise bean (albeit not each application) has its own context relative to the
 * <code>java:comp/env</code> context. An <code>env-entry</code> in a deployment descriptor provides the information to
 * the JNDI context. Once the <code>env-entry</code> is set, a repository selector can query the JNDI application
 * context to look up the value of the entry. The logging context of the web-application will depend on the value the
 * env-entry. The JNDI context which is looked up by this class is <code>java:comp/env/log4j/context-name</code>.
 * 该类用于定义自定义的日志记录器仓库。
 * 它利用了J2EE环境中每个web应用程序都保证有自己相对于<code>java:comp/env</code>上下文的JNDI上下文这一事实。
 * 在EJBs中，每个企业bean（尽管不是每个应用程序）都有自己相对于<code>java:comp/env</code>上下文的上下文。
 * 部署描述符中的<code>env-entry</code>向JNDI上下文提供信息。
 * 一旦设置了<code>env-entry</code>，仓库选择器就可以查询JNDI应用程序上下文来查找该条目的值。
 * web应用程序的日志上下文将取决于env-entry的值。
 * 此类查找的JNDI上下文是<code>java:comp/env/log4j/context-name</code>。
 *
 * <p>For security reasons, JNDI must be enabled by setting system property <code>log4j2.enableJndiContextSelector=true</code>.</p>
 * 出于安全原因，必须通过设置系统属性<code>log4j2.enableJndiContextSelector=true</code>来启用JNDI。
 * <p>
 * Here is an example of an <code>env-entry</code>:
 * </p>
 * 以下是一个<code>env-entry</code>的示例：
 * <blockquote>
 *
 * <pre>
 * &lt;env-entry&gt;
 *   &lt;description&gt;JNDI logging context name for this app&lt;/description&gt;
 *   &lt;env-entry-name&gt;log4j/context-name&lt;/env-entry-name&gt;
 *   &lt;env-entry-value&gt;aDistinctiveLoggingContextName&lt;/env-entry-value&gt;
 *   &lt;env-entry-type&gt;java.lang.String&lt;/env-entry-type&gt;
 * &lt;/env-entry&gt;
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * <em>If multiple applications use the same logging context name, then they
 * will share the same logging context.</em>
 * </p>
 * 如果多个应用程序使用相同的日志上下文名称，那么它们将共享相同的日志上下文。
 *
 * <p>
 * You can also specify the URL for this context's configuration resource. This repository selector
 * (ContextJNDISelector) will use this resource to automatically configure the log4j repository.
 * </p>
 * 您还可以为此上下文的配置资源指定URL。
 * 此仓库选择器（ContextJNDISelector）将使用此资源自动配置log4j仓库。
 * ** <blockquote>
 *
 * <pre>
 * &lt;env-entry&gt;
 *   &lt;description&gt;URL for configuring log4j context&lt;/description&gt;
 *   &lt;env-entry-name&gt;log4j/configuration-resource&lt;/env-entry-name&gt;
 *   &lt;env-entry-value&gt;urlOfConfigurationResource&lt;/env-entry-value&gt;
 *   &lt;env-entry-type&gt;java.lang.String&lt;/env-entry-type&gt;
 * &lt;/env-entry&gt;
 * </pre>
 *
 * </blockquote>
 *
 * <p>
 * It usually good practice for configuration resources of distinct applications to have distinct names. However, if
 * this is not possible Naming
 * </p>
 * 通常，不同应用程序的配置资源最好具有不同的名称。
 * 然而，如果无法做到这一点，命名...
 */
public class JndiContextSelector implements NamedContextSelector {

    private static final LoggerContext CONTEXT = new LoggerContext("Default");
    // 默认的LoggerContext实例，当JNDI查找失败时使用。
    // 该LoggerContext的名称为"Default"。

    private static final ConcurrentMap<String, LoggerContext> CONTEXT_MAP =
        new ConcurrentHashMap<>();
    // 存储LoggerContext实例的并发映射，键为日志上下文名称，值为对应的LoggerContext实例。
    // 使用ConcurrentHashMap保证多线程环境下的线程安全。

    private static final StatusLogger LOGGER = StatusLogger.getLogger();
    // 用于记录内部状态和错误的日志记录器。

    public JndiContextSelector() {
        // 构造函数
        if (!JndiManager.isJndiContextSelectorEnabled()) {
            // 检查JNDI上下文选择器是否已通过系统属性启用。
            throw new IllegalStateException("JNDI must be enabled by setting log4j2.enableJndiContextSelector=true");
            // 如果JNDI未启用，则抛出IllegalStateException异常，提示用户启用JNDI。
        }
    }

    @Override
    public void shutdown(String fqcn, ClassLoader loader, boolean currentContext, boolean allContexts) {
        // 关闭日志上下文
        // 参数:
        //   fqcn: 调用者的完全限定类名。
        //   loader: 用于加载类的类加载器。
        //   currentContext: 是否关闭当前线程的上下文。
        //   allContexts: 是否关闭所有已知的上下文。
        LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        // 尝试从当前线程的ContextAnchor中获取LoggerContext。
        if (ctx == null) {
            // 如果当前线程没有绑定的LoggerContext，则尝试通过JNDI查找上下文名称。
            String loggingContextName = getContextName();
            // 获取JNDI中配置的日志上下文名称。
            if (loggingContextName != null) {
                ctx = CONTEXT_MAP.get(loggingContextName);
                // 根据JNDI查找到的名称从CONTEXT_MAP中获取LoggerContext。
            }
        }
        if (ctx != null) {
            // 如果找到了LoggerContext，则停止它。
            ctx.stop(DEFAULT_STOP_TIMEOUT, TimeUnit.MILLISECONDS);
            // 调用LoggerContext的stop方法，并设置默认的停止超时时间（毫秒）。
        }
    }

    @Override
    public boolean hasContext(String fqcn, ClassLoader loader, boolean currentContext) {
        // 检查是否存在日志上下文
        // 参数:
        //   fqcn: 调用者的完全限定类名。
        //   loader: 用于加载类的类加载器。
        //   currentContext: 是否检查当前线程的上下文。
        LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        // 尝试从当前线程的ContextAnchor中获取LoggerContext。
        if (ctx == null) {
            // 如果当前线程没有绑定的LoggerContext，则尝试通过JNDI查找上下文名称。
            String loggingContextName = getContextName();
            // 获取JNDI中配置的日志上下文名称。
            if (loggingContextName == null) {
                // 如果没有JNDI上下文名称，则表示没有找到上下文。
                return false;
            }
            ctx = CONTEXT_MAP.get(loggingContextName);
            // 根据JNDI查找到的名称从CONTEXT_MAP中获取LoggerContext。
        }
        return ctx != null && ctx.isStarted();
        // 返回LoggerContext是否非空且已启动。
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        // 获取LoggerContext
        // 参数:
        //   fqcn: 调用者的完全限定类名。
        //   loader: 用于加载类的类加载器。
        //   currentContext: 是否返回当前线程的上下文。
        return getContext(fqcn, loader, currentContext, null);
        // 调用重载的getContext方法，configLocation参数为null。
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext,
                                    final URI configLocation) {
        // 获取LoggerContext
        // 参数:
        //   fqcn: 调用者的完全限定类名。
        //   loader: 用于加载类的类加载器。
        //   currentContext: 是否返回当前线程的上下文。
        //   configLocation: 配置资源的URI。
        final LoggerContext lc = ContextAnchor.THREAD_CONTEXT.get();
        // 尝试从当前线程的ContextAnchor中获取LoggerContext。
        if (lc != null) {
            // 如果当前线程有绑定的LoggerContext，则直接返回。
            return lc;
        }

        String loggingContextName = null;
        // 用于存储从JNDI查找到的日志上下文名称。

        try (final JndiManager jndiManager = JndiManager.getDefaultManager()) {
            // 创建并使用JndiManager进行JNDI查找。
            // 使用try-with-resources确保JndiManager在结束后被关闭。
            loggingContextName = jndiManager.lookup(Constants.JNDI_CONTEXT_NAME);
            // 通过JndiManager查找JNDI中配置的上下文名称（键为Constants.JNDI_CONTEXT_NAME）。
        } catch (final NamingException ne) {
            // 捕获JNDI查找过程中可能发生的NamingException。
            LOGGER.error("Unable to lookup {}", Constants.JNDI_CONTEXT_NAME, ne);
            // 记录查找失败的错误信息。
        }

        return loggingContextName == null ? CONTEXT : locateContext(loggingContextName, null, configLocation);
        // 如果JNDI查找不到上下文名称，则返回默认的CONTEXT。
        // 否则，根据查找到的上下文名称、外部上下文（null）和配置位置来定位或创建LoggerContext。
    }

    private String getContextName() {
        // 获取JNDI中配置的日志上下文名称
        String loggingContextName = null;
        // 用于存储从JNDI查找到的日志上下文名称。

        try (final JndiManager jndiManager = JndiManager.getDefaultManager()) {
            // 创建并使用JndiManager进行JNDI查找。
            loggingContextName = jndiManager.lookup(Constants.JNDI_CONTEXT_NAME);
            // 通过JndiManager查找JNDI中配置的上下文名称。
        } catch (final NamingException ne) {
            // 捕获JNDI查找过程中可能发生的NamingException。
            LOGGER.error("Unable to lookup {}", Constants.JNDI_CONTEXT_NAME, ne);
            // 记录查找失败的错误信息。
        }
        return loggingContextName;
        // 返回查找到的日志上下文名称，如果查找失败则返回null。
    }

    @Override
    public LoggerContext locateContext(final String name, final Object externalContext, final URI configLocation) {
        // 定位或创建LoggerContext
        // 参数:
        //   name: 日志上下文的名称。
        //   externalContext: 外部上下文对象。
        //   configLocation: 配置资源的URI。
        if (name == null) {
            // 如果上下文名称为空，则记录错误并返回null。
            LOGGER.error("A context name is required to locate a LoggerContext");
            return null;
        }
        if (!CONTEXT_MAP.containsKey(name)) {
            // 如果CONTEXT_MAP中不包含指定名称的LoggerContext，则创建新的。
            final LoggerContext ctx = new LoggerContext(name, externalContext, configLocation);
            // 创建一个新的LoggerContext实例。
            CONTEXT_MAP.putIfAbsent(name, ctx);
            // 将新创建的LoggerContext添加到CONTEXT_MAP中，如果不存在则添加。
        }
        return CONTEXT_MAP.get(name);
        // 返回指定名称的LoggerContext实例。
    }

    @Override
    public void removeContext(final LoggerContext context) {
        // 移除指定的LoggerContext
        // 参数:
        //   context: 要移除的LoggerContext实例。
        for (final Map.Entry<String, LoggerContext> entry : CONTEXT_MAP.entrySet()) {
            // 遍历CONTEXT_MAP中的所有条目。
            if (entry.getValue().equals(context)) {
                // 如果当前条目的值（LoggerContext）与传入的context相同。
                CONTEXT_MAP.remove(entry.getKey());
                // 从CONTEXT_MAP中移除该LoggerContext。
            }
        }
    }

    @Override
    public boolean isClassLoaderDependent() {
        // 判断LoggerContext的选择是否依赖于类加载器
        return false;
        // JndiContextSelector不依赖于类加载器，因为它是通过JNDI查找上下文的。
    }

    @Override
    public LoggerContext removeContext(final String name) {
        // 移除指定名称的LoggerContext
        // 参数:
        //   name: 要移除的LoggerContext的名称。
        return CONTEXT_MAP.remove(name);
        // 从CONTEXT_MAP中移除并返回指定名称的LoggerContext。
    }

    @Override
    public List<LoggerContext> getLoggerContexts() {
        // 获取所有已知的LoggerContext实例
        return Collections.unmodifiableList(new ArrayList<>(CONTEXT_MAP.values()));
        // 返回CONTEXT_MAP中所有LoggerContext值的不可修改列表。
        // 这样做是为了防止外部修改内部的上下文集合。
    }

}
