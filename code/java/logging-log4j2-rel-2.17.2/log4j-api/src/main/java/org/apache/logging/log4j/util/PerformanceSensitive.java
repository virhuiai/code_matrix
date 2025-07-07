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

/*
 * Apache软件基金会（ASF）的许可声明，说明此文件的版权归属和使用许可。
 * 该文件遵循Apache 2.0许可证，用户需遵守相关条款。
 * 软件按“原样”分发，不提供任何明示或暗示的担保。
 */

package org.apache.logging.log4j.util;

// 包声明：定义该类属于Log4J工具包，位于org.apache.logging.log4j.util路径下。

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 导入Java注解相关的类，用于定义注解的保留策略。

/**
 * Indicates that a particular annotated construct was written with certain performance constraints in mind that
 * should be considered when modifying or testing. Descriptive values should be similar to the conventions used by
 * {@link SuppressWarnings}. For example, code that should not be allocating objects (like iterators) could use the
 * description "allocation".
 *
 * @since 2.6
 */
// Not @Documented: Do not (yet) make this annotation part of the public API of annotated elements.
// No @Target: No restrictions yet on what code elements may be annotated or not.
/**
 * 指示被注解的代码结构在编写时考虑了特定性能约束，修改或测试时需注意。
 * 描述值应遵循类似{@link SuppressWarnings}的惯例。例如，避免分配对象（如迭代器）的代码可使用描述“allocation”。
 *
 * 中文注释：
 * - **主要功能和目的**：此注解用于标记对性能敏感的代码，提醒开发者在修改或测试时注意性能约束。
 * - **关键变量和函数的用途**：提供value属性，用于描述性能敏感的原因。
 * - **代码执行流程和关键步骤**：注解本身不包含执行逻辑，仅作为元数据标记代码。
 * - **特殊处理逻辑和注意事项**：开发者需根据value描述（如“allocation”）避免引入性能问题，如不必要的对象分配。
 * - **自2.6版本起**：此注解在Log4J 2.6版本中引入。
 */
@Retention(RetentionPolicy.CLASS) // Currently no need to reflectively discover this annotation at runtime.
/*
 * 定义注解的保留策略为CLASS，表示注解仅保留在编译后的类文件中，运行时不可通过反射访问。
 *
 * 中文注释：
 * - **主要功能和目的**：设置注解的生命周期，仅用于编译时检查，不支持运行时反射。
 * - **特殊处理逻辑和注意事项**：选择CLASS策略表明此注解主要用于开发和测试阶段的静态分析，而非运行时动态处理。
 */
public @interface PerformanceSensitive {
    // 定义一个公共注解接口PerformanceSensitive，用于标记性能敏感的代码。

    /** Description of why this is written the way it is. */
    String[] value() default "";
    /*
     * value属性：描述代码为何以特定方式编写，通常说明性能敏感的原因，默认值为空字符串数组。
     *
     * 中文注释：
     * - **主要功能和目的**：允许开发者提供性能敏感的具体原因，如“allocation”表示避免对象分配。
     * - **参数和返回值的详细说明**：
     *   - **返回类型**：String[]，支持多个描述字符串，便于记录多种性能约束。
     *   - **默认值**：空字符串数组，表示未提供具体描述时使用默认值。
     * - **关键变量和函数的用途**：value属性用于向开发者传达代码的性能要求。
     * - **代码执行流程和关键步骤**：注解的value属性在编译时由开发者或工具读取，用于指导代码修改或优化。
     * - **特殊处理逻辑和注意事项**：开发者需根据value值调整代码逻辑，避免破坏性能优化（如避免引入高开销操作）。
     */
}
