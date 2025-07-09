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

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * Contains a reference to a script defined elsewhere in the configuration.
 */
// 包含对配置中其他位置定义的脚本的引用。
@Plugin(name = "ScriptRef", category = Node.CATEGORY, printObject = true)
// 这是一个Log4j2插件，名称为"ScriptRef"，属于Node类别，并且在打印时会输出对象信息。
public class ScriptRef extends AbstractScript {

    private final ScriptManager scriptManager;
    // 脚本管理器，用于管理和获取脚本。

    public ScriptRef(final String name, final ScriptManager scriptManager) {
        super(name, null, null);
        // 调用父类AbstractScript的构造函数，设置脚本名称。
        // scriptText和language在这里设置为null，因为ScriptRef只是一个引用，实际的脚本内容和语言由引用的脚本提供。
        this.scriptManager = scriptManager;
        // 初始化脚本管理器。
    }

    @Override
    public String getLanguage() {
        // 获取引用脚本的语言。
        final AbstractScript script = this.scriptManager.getScript(getName());
        // 从脚本管理器中根据名称获取实际的脚本对象。
        return script != null ? script.getLanguage() : null;
        // 如果找到了脚本，则返回其语言；否则返回null。
    }


    @Override
    public String getScriptText() {
        // 获取引用脚本的文本内容。
        final AbstractScript script = this.scriptManager.getScript(getName());
        // 从脚本管理器中根据名称获取实际的脚本对象。
        return script != null ? script.getScriptText() : null;
        // 如果找到了脚本，则返回其脚本文本；否则返回null。
    }

    @PluginFactory
    // 这是一个插件工厂方法，用于创建ScriptRef实例。
    public static ScriptRef createReference(
            // @formatter:off
            @PluginAttribute("ref") final String name,
            // @PluginAttribute注解表示此参数是一个插件属性，属性名为"ref"，对应配置中的脚本引用名称。
            @PluginConfiguration final Configuration configuration) {
            // @PluginConfiguration注解表示此参数是Log4j2的配置对象，可以从中获取ScriptManager。
            // @formatter:on
        if (name == null) {
            // 检查脚本名称是否为空。
            LOGGER.error("No script name provided");
            // 如果脚本名称为空，则记录错误信息。
            return null;
            // 返回null，表示创建失败。
        }
        return new ScriptRef(name, configuration.getScriptManager());
        // 创建并返回一个新的ScriptRef实例，传入脚本名称和从配置中获取的ScriptManager。
    }

    @Override
    public String toString() {
        // 重写toString方法，用于返回对象的字符串表示。
        return "ref=" + getName();
        // 返回"ref="加上脚本的名称，表示这是一个脚本引用及其引用的脚本名称。
    }
}
