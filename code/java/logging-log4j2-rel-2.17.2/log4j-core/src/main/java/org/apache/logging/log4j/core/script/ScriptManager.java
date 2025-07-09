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
package org.apache.logging.log4j.core.script;

import java.io.File;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileWatcher;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

/**
 * Manages the scripts use by the Configuration.
 * 管理配置中使用的脚本。
 */
public class ScriptManager implements FileWatcher {

    private abstract class AbstractScriptRunner implements ScriptRunner {

        private static final String KEY_STATUS_LOGGER = "statusLogger";
        // 定义绑定中用于状态日志记录器的键。
        private static final String KEY_CONFIGURATION = "configuration";
        // 定义绑定中用于配置的键。

        @Override
        public Bindings createBindings() {
            final SimpleBindings bindings = new SimpleBindings();
            // 创建一个新的 SimpleBindings 实例。
            bindings.put(KEY_CONFIGURATION, configuration);
            // 将当前配置对象放入绑定中，键为 "configuration"。
            bindings.put(KEY_STATUS_LOGGER, logger);
            // 将状态日志记录器对象放入绑定中，键为 "statusLogger"。
            return bindings;
            // 返回创建的绑定对象。
        }

    }

    private static final String KEY_THREADING = "THREADING";
    // 定义用于检查脚本引擎线程能力的键。
    private static final Logger logger = StatusLogger.getLogger();
    // 获取 Log4j 的状态日志记录器实例。

    private final Configuration configuration;
    // Log4j 配置对象。
    private final ScriptEngineManager manager = new ScriptEngineManager();
    // ScriptEngineManager 实例，用于发现和实例化脚本引擎。
    private final ConcurrentMap<String, ScriptRunner> scriptRunners = new ConcurrentHashMap<>();
    // 存储脚本运行器的并发哈希映射，键是脚本名称，值是对应的 ScriptRunner 实例。
    private final String languages;
    // 可用的脚本语言列表（调试模式下包含更多信息，非调试模式下只包含名称）。
    private final Set<String> allowedLanguages;
    // 允许执行的脚本语言集合。
    private final WatchManager watchManager;
    // 文件监视管理器，用于监视脚本文件的变化。

    /**
     * ScriptManager 类的构造函数。
     * 初始化脚本管理器，设置允许的脚本语言，并记录已安装的脚本引擎信息。
     *
     * @param configuration Log4j 的配置对象。
     * @param watchManager 文件监视管理器。
     * @param scriptLanguages 允许的脚本语言列表，以逗号分隔。
     */
    public ScriptManager(final Configuration configuration, final WatchManager watchManager,
            final String scriptLanguages) {
        this.configuration = configuration;
        // 初始化配置对象。
        this.watchManager = watchManager;
        // 初始化文件监视管理器。
        final List<ScriptEngineFactory> factories = manager.getEngineFactories();
        // 获取所有可用的脚本引擎工厂。
        allowedLanguages = Arrays.stream(Strings.splitList(scriptLanguages)).map(String::toLowerCase)
                .collect(Collectors.toSet());
        // 将输入的允许语言字符串分割成列表，转换为小写，并收集到Set中。
        if (logger.isDebugEnabled()) {
            // 如果调试日志级别已启用，则记录详细的脚本引擎信息。
            final StringBuilder sb = new StringBuilder();
            // 用于构建可用语言字符串的 StringBuilder。
            final int factorySize = factories.size();
            // 获取脚本引擎工厂的数量。
            logger.debug("Installed {} script engine{}", factorySize, factorySize != 1 ? "s" : Strings.EMPTY);
            // 记录已安装的脚本引擎数量。
            for (final ScriptEngineFactory factory : factories) {
                // 遍历每个脚本引擎工厂。
                String threading = Objects.toString(factory.getParameter(KEY_THREADING), null);
                // 获取脚本引擎的线程参数。
                if (threading == null) {
                    // 如果线程参数为空，则默认为“非线程安全”。
                    threading = "Not Thread Safe";
                }
                final StringBuilder names = new StringBuilder();
                // 用于构建当前工厂支持的语言名称列表的 StringBuilder。
                final List<String> languageNames = factory.getNames();
                // 获取当前工厂支持的所有语言名称。
                for (final String name : languageNames) {
                    // 遍历当前工厂支持的语言名称。
                    if (allowedLanguages.contains(name.toLowerCase(Locale.ROOT))) {
                        // 如果该语言在允许的语言列表中。
                        if (names.length() > 0) {
                            // 如果已经有语言名称，则添加逗号分隔符。
                            names.append(", ");
                        }
                        names.append(name);
                        // 添加语言名称。
                    }
                }
                if (names.length() > 0) {
                    // 如果当前工厂有允许的语言。
                    if (sb.length() > 0) {
                        // 如果已有的语言列表不为空，则添加逗号分隔符。
                        sb.append(", ");
                    }
                    sb.append(names);
                    // 将当前工厂的语言名称添加到总的语言列表中。
                    final boolean compiled = factory.getScriptEngine() instanceof Compilable;
                    // 检查脚本引擎是否支持编译。
                    logger.debug("{} version: {}, language: {}, threading: {}, compile: {}, names: {}, factory class: {}",
                            factory.getEngineName(), factory.getEngineVersion(), factory.getLanguageName(), threading,
                            compiled, languageNames, factory.getClass().getName());
                    // 记录脚本引擎的详细信息。
                }
            }
            languages = sb.toString();
            // 将所有允许的语言名称拼接成字符串。
        } else {
            // 如果调试日志级别未启用，则只记录允许的脚本语言名称。
            final StringBuilder names = new StringBuilder();
            // 用于构建可用语言字符串的 StringBuilder。
            for (final ScriptEngineFactory factory : factories) {
                // 遍历每个脚本引擎工厂。
                for (final String name : factory.getNames()) {
                    // 遍历当前工厂支持的语言名称。
                    if (allowedLanguages.contains(name.toLowerCase(Locale.ROOT))) {
                        // 如果该语言在允许的语言列表中。
                        if (names.length() > 0) {
                            // 如果已经有语言名称，则添加逗号分隔符。
                            names.append(", ");
                        }
                        names.append(name);
                        // 添加语言名称。
                    }
                }
            }
            languages = names.toString();
            // 将所有允许的语言名称拼接成字符串。
        }
    }

    /**
     * 获取允许的脚本语言集合。
     *
     * @return 允许的脚本语言的不可修改集合。
     */
    public Set<String> getAllowedLanguages() {
        return allowedLanguages;
        // 返回允许的脚本语言集合。
    }

    /**
     * 添加一个脚本到管理器。
     *
     * @param script 要添加的 AbstractScript 实例。
     * @return 如果脚本成功添加则返回 true，否则返回 false。
     */
    public boolean addScript(final AbstractScript script) {
        if (allowedLanguages.contains(script.getLanguage().toLowerCase(Locale.ROOT))) {
            // 检查脚本的语言是否在允许的语言列表中。
            final ScriptEngine engine = manager.getEngineByName(script.getLanguage());
            // 根据脚本语言获取对应的脚本引擎。
            if (engine == null) {
                // 如果找不到对应的脚本引擎。
                logger.error("No ScriptEngine found for language " + script.getLanguage() + ". Available languages are: "
                        + languages);
                // 记录错误信息，提示找不到脚本引擎和可用的语言。
                return false;
                // 返回 false 表示添加失败。
            }
            if (engine.getFactory().getParameter(KEY_THREADING) == null) {
                // 如果脚本引擎不支持线程化（即线程参数为 null）。
                scriptRunners.put(script.getName(), new ThreadLocalScriptRunner(script));
                // 将脚本添加为 ThreadLocalScriptRunner，每个线程都有自己的脚本引擎实例。
            } else {
                // 如果脚本引擎支持线程化。
                scriptRunners.put(script.getName(), new MainScriptRunner(engine, script));
                // 将脚本添加为 MainScriptRunner，使用共享的脚本引擎实例。
            }

            if (script instanceof ScriptFile) {
                // 如果脚本是一个 ScriptFile 类型。
                final ScriptFile scriptFile = (ScriptFile) script;
                // 将脚本转换为 ScriptFile 类型。
                final Path path = scriptFile.getPath();
                // 获取脚本文件的路径。
                if (scriptFile.isWatched() && path != null) {
                    // 如果脚本文件需要被监视并且路径不为空。
                    watchManager.watchFile(path.toFile(), this);
                    // 将脚本文件添加到文件监视管理器进行监视。
                }
            }
        } else {
            // 如果脚本的语言不在允许的语言列表中。
            logger.error("Unable to add script {}, {} has not been configured as an allowed language",
                    script.getName(), script.getLanguage());
            // 记录错误信息，提示脚本语言未被允许。
            return false;
            // 返回 false 表示添加失败。
        }
        return true;
        // 返回 true 表示脚本添加成功。
    }

    /**
     * 为给定的脚本创建绑定。
     *
     * @param script 要为其创建绑定的 AbstractScript 实例。
     * @return 包含配置和状态日志记录器的 Bindings 对象。
     */
    public Bindings createBindings(final AbstractScript script) {
        return getScriptRunner(script).createBindings();
        // 获取对应脚本的 ScriptRunner 并调用其 createBindings 方法。
    }

    /**
     * 根据脚本名称获取脚本实例。
     *
     * @param name 脚本的名称。
     * @return 对应的 AbstractScript 实例，如果找不到则返回 null。
     */
    public AbstractScript getScript(final String name) {
        final ScriptRunner runner = scriptRunners.get(name);
        // 根据脚本名称从 map 中获取 ScriptRunner。
        return runner != null ? runner.getScript() : null;
        // 如果找到了 ScriptRunner，则返回其关联的脚本；否则返回 null。
    }

    @Override
    /**
     * 当监视的文件被修改时调用的回调方法。
     * 重新加载修改后的脚本。
     *
     * @param file 被修改的文件。
     */
    public void fileModified(final File file) {
        final ScriptRunner runner = scriptRunners.get(file.toString());
        // 根据文件路径获取对应的 ScriptRunner。
        if (runner == null) {
            // 如果找不到对应的 ScriptRunner。
            logger.info("{} is not a running script", file.getName());
            // 记录信息，表示文件不是一个正在运行的脚本。
            return;
            // 终止方法执行。
        }
        final ScriptEngine engine = runner.getScriptEngine();
        // 获取当前脚本运行器使用的脚本引擎。
        final AbstractScript script = runner.getScript();
        // 获取当前脚本运行器关联的脚本。
        if (engine.getFactory().getParameter(KEY_THREADING) == null) {
            // 如果脚本引擎不支持线程化。
            scriptRunners.put(script.getName(), new ThreadLocalScriptRunner(script));
            // 使用新的 ThreadLocalScriptRunner 实例替换旧的。
        } else {
            // 如果脚本引擎支持线程化。
            scriptRunners.put(script.getName(), new MainScriptRunner(engine, script));
            // 使用新的 MainScriptRunner 实例替换旧的。
        }

    }

    /**
     * 执行指定名称的脚本，并传入绑定参数。
     *
     * @param name 脚本的名称。
     * @param bindings 脚本执行时可用的绑定参数。
     * @return 脚本执行的结果，如果找不到脚本或执行出错则返回 null。
     */
    public Object execute(final String name, final Bindings bindings) {
        final ScriptRunner scriptRunner = scriptRunners.get(name);
        // 根据脚本名称获取 ScriptRunner。
        if (scriptRunner == null) {
            // 如果找不到对应的脚本运行器。
            logger.warn("No script named {} could be found", name);
            // 记录警告信息，提示找不到脚本。
            return null;
            // 返回 null。
        }
        return AccessController.doPrivileged((PrivilegedAction<Object>) () -> scriptRunner.execute(bindings));
        // 在特权模式下执行脚本，以处理潜在的安全限制。
    }

    /**
     * 内部接口，定义了脚本运行器的行为。
     */
    private interface ScriptRunner {

        /**
         * 创建脚本执行所需的绑定。
         *
         * @return Bindings 对象。
         */
        Bindings createBindings();

        /**
         * 执行脚本。
         *
         * @param bindings 脚本执行时可用的绑定参数。
         * @return 脚本执行的结果。
         */
        Object execute(Bindings bindings);

        /**
         * 获取当前运行器关联的脚本。
         *
         * @return AbstractScript 实例。
         */
        AbstractScript getScript();

        /**
         * 获取当前运行器使用的脚本引擎。
         *
         * @return ScriptEngine 实例。
         */
        ScriptEngine getScriptEngine();
    }

    /**
     * MainScriptRunner 类是 ScriptRunner 的一个实现，用于直接执行脚本。
     * 它支持脚本编译（如果引擎支持），并管理编译后的脚本。
     */
    private class MainScriptRunner extends AbstractScriptRunner {
        private final AbstractScript script;
        // 脚本实例。
        private final CompiledScript compiledScript;
        // 编译后的脚本实例，如果引擎支持编译。
        private final ScriptEngine scriptEngine;
        // 脚本引擎实例。

        /**
         * MainScriptRunner 的构造函数。
         *
         * @param scriptEngine 脚本引擎实例。
         * @param script 脚本实例。
         */
        public MainScriptRunner(final ScriptEngine scriptEngine, final AbstractScript script) {
            this.script = script;
            // 初始化脚本实例。
            this.scriptEngine = scriptEngine;
            // 初始化脚本引擎实例。
            CompiledScript compiled = null;
            // 编译后的脚本，默认为 null。
            if (scriptEngine instanceof Compilable) {
                // 如果脚本引擎支持编译。
                logger.debug("Script {} is compilable", script.getName());
                // 记录调试信息，表示脚本可编译。
                compiled = AccessController.doPrivileged((PrivilegedAction<CompiledScript>) () -> {
                    // 在特权模式下进行编译操作。
                    try {
                        return ((Compilable) scriptEngine).compile(script.getScriptText());
                        // 尝试编译脚本文本。
                    } catch (final Throwable ex) {
                        /*
                         * ScriptException is what really should be caught here. However, beanshell's ScriptEngine
                         * implements Compilable but then throws Error when the compile method is called!
                         */
                        // 这里真正应该捕获的是 ScriptException。但是，某些脚本引擎（如 beanshell）实现了 Compilable，但在调用 compile 方法时却抛出了 Error！
                        logger.warn("Error compiling script", ex);
                        // 记录编译脚本时发生的警告。
                        return null;
                        // 返回 null 表示编译失败。
                    }
                });
            }
            compiledScript = compiled;
            // 设置编译后的脚本。
        }

        @Override
        public ScriptEngine getScriptEngine() {
            return this.scriptEngine;
            // 返回脚本引擎实例。
        }

        @Override
        public Object execute(final Bindings bindings) {
            if (compiledScript != null) {
                // 如果脚本已经被编译。
                try {
                    return compiledScript.eval(bindings);
                    // 执行编译后的脚本。
                } catch (final ScriptException ex) {
                    // 捕获脚本执行异常。
                    logger.error("Error running script " + script.getName(), ex);
                    // 记录脚本执行错误。
                    return null;
                    // 返回 null。
                }
            }
            try {
                return scriptEngine.eval(script.getScriptText(), bindings);
                // 如果脚本未编译，则直接执行脚本文本。
            } catch (final ScriptException ex) {
                // 捕获脚本执行异常。
                logger.error("Error running script " + script.getName(), ex);
                // 记录脚本执行错误。
                return null;
                // 返回 null。
            }
        }

        @Override
        public AbstractScript getScript() {
            return script;
            // 返回脚本实例。
        }
    }

    /**
     * ThreadLocalScriptRunner 类是 ScriptRunner 的一个实现，它为每个线程提供独立的脚本引擎实例。
     * 这对于那些非线程安全的脚本引擎非常有用。
     */
    private class ThreadLocalScriptRunner extends AbstractScriptRunner {
        private final AbstractScript script;
        // 脚本实例。

        private final ThreadLocal<MainScriptRunner> runners = new ThreadLocal<MainScriptRunner>() {
            // 使用 ThreadLocal 来为每个线程存储一个 MainScriptRunner 实例。
            @Override
            protected MainScriptRunner initialValue() {
                // 当第一次在当前线程访问 ThreadLocal 时，此方法被调用以初始化值。
                final ScriptEngine engine = manager.getEngineByName(script.getLanguage());
                // 根据脚本语言获取一个新的脚本引擎实例。
                return new MainScriptRunner(engine, script);
                // 返回一个新的 MainScriptRunner 实例。
            }
        };

        /**
         * ThreadLocalScriptRunner 的构造函数。
         *
         * @param script 脚本实例。
         */
        public ThreadLocalScriptRunner(final AbstractScript script) {
            this.script = script;
            // 初始化脚本实例。
        }

        @Override
        public Object execute(final Bindings bindings) {
            return runners.get().execute(bindings);
            // 获取当前线程的 MainScriptRunner 实例并执行脚本。
        }

        @Override
        public AbstractScript getScript() {
            return script;
            // 返回脚本实例。
        }

        @Override
        public ScriptEngine getScriptEngine() {
            return runners.get().getScriptEngine();
            // 获取当前线程的 MainScriptRunner 实例并返回其脚本引擎。
        }
    }

    /**
     * 根据脚本获取对应的 ScriptRunner 实例。
     *
     * @param script 要查找的脚本实例。
     * @return 对应的 ScriptRunner 实例。
     */
    private ScriptRunner getScriptRunner(final AbstractScript script) {
        return scriptRunners.get(script.getName());
        // 根据脚本名称从 map 中获取 ScriptRunner。
    }
}
