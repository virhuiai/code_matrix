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
package org.apache.logging.log4j.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
/**
 * ExtensionLanguageMapping 枚举
 * 该枚举定义了文件扩展名与其对应编程语言的映射关系。
 * 例如：.js 映射到 JavaScript，.gvy 映射到 Groovy 等。
 */
public enum ExtensionLanguageMapping {
    JS("js", "JavaScript"), JAVASCRIPT("javascript", "JavaScript"), GVY("gvy", "Groovy"),
    GROOVY("groovy", "Groovy"), BSH("bsh", "beanshell"), BEANSHELL("beanshell", "beanshell"),
    JY("jy", "jython"), JYTHON("jython", "jython"), FTL("ftl", "freemarker"),
    FREEMARKER("freemarker", "freemarker"), VM("vm", "velocity"), VELOCITY("velocity", "velocity"),
    AWK("awk", "awk"), EJS("ejs", "ejs"), TCL("tcl", "tcl"), HS("hs", "jaskell"), JELLY("jelly", "jelly"),
    JEP("jep", "jep"), JEXL("jexl", "jexl"), JEXL2("jexl2", "jexl2"),
    RB("rb", "ruby"), RUBY("ruby", "ruby"), JUDO("judo", "judo"), JUDI("judi", "judo"), SCALA("scala", "scala"),
    CLJ("clj", "Clojure"); // 定义各种文件扩展名到编程语言的映射


    private final String extension;// 文件扩展名
    private final String language; // 编程语言

    /**
     * 构造函数
     *
     * @param extension 文件扩展名，例如 "js"
     * @param language 编程语言名称，例如 "JavaScript"
     */
    ExtensionLanguageMapping(final String extension, final String language) {
        this.extension = extension; // 初始化扩展名
        this.language = language; // 初始化语言
    }

    /**
     * 获取当前枚举实例的文件扩展名。
     *
     * @return 文件扩展名的字符串表示。
     */
    public String getExtension() {
        return this.extension; // 返回文件扩展名
    }

    /**
     * 获取当前枚举实例对应的编程语言名称。
     *
     * @return 编程语言的字符串表示。
     */
    public String getLanguage() {
        return this.language; // 返回编程语言
    }

    /**
     * 根据给定的文件扩展名查找对应的 ExtensionLanguageMapping 枚举实例。
     * 遍历所有枚举值，如果找到匹配的扩展名，则返回该枚举实例。
     *
     * @param extension 要查找的文件扩展名。
     * @return 匹配的 ExtensionLanguageMapping 枚举实例，如果未找到则返回 null。
     */
    public static ExtensionLanguageMapping getByExtension(final String extension) {
        // 遍历所有定义的枚举映射
        for (final ExtensionLanguageMapping mapping : values()) {
            // 如果当前映射的扩展名与传入的扩展名相同
            if (mapping.extension.equals(extension)) {
                return mapping; // 返回找到的映射
            }
        }
        return null; // 如果没有找到匹配的扩展名，则返回 null
    }

    /**
     * 根据给定的编程语言名称查找所有对应的 ExtensionLanguageMapping 枚举实例。
     * 遍历所有枚举值，将所有语言名称匹配的枚举实例添加到一个列表中并返回。
     *
     * @param language 要查找的编程语言名称。
     * @return 包含所有匹配的 ExtensionLanguageMapping 枚举实例的列表。
     */
    public static List<ExtensionLanguageMapping> getByLanguage(final String language) {
        final List<ExtensionLanguageMapping> list = new ArrayList<>(); // 创建一个新的列表用于存放结果
        // 遍历所有定义的枚举映射
        for (final ExtensionLanguageMapping mapping : values()) {
            // 如果当前映射的语言与传入的语言相同
            if (mapping.language.equals(language)) {
                list.add(mapping); // 将匹配的映射添加到列表中
            }
        }
        return list; // 返回包含所有匹配映射的列表
    }

}
