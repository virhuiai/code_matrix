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
 * 中文注释：
 * 该文件遵循Apache 2.0许可证，明确了版权归属和使用权限。
 * 代码来源于Apache Software Foundation，遵循其开源协议。
 * 禁止在未遵守许可证的情况下使用本文件。
 * 许可证详细内容可参考：http://www.apache.org/licenses/LICENSE-2.0
 */

package org.apache.logging.log4j.message;

import java.util.Arrays;

import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

/**
 * Handles messages that consist of a format string containing '{}' to represent each replaceable token, and
 * the parameters.
 * <p>
 * This class was originally written for <a href="http://lilithapp.com/">Lilith</a> by Joern Huxhorn where it is
 * licensed under the LGPL. It has been relicensed here with his permission providing that this attribution remain.
 * </p>
 */
/*
 * 中文注释：
 * ParameterizedMessage类用于处理包含'{}'占位符的格式化字符串消息和参数。
 * 主要功能：
 * - 提供格式化消息的能力，将占位符替换为实际参数值。
 * - 支持异常（Throwable）处理，确保异常信息可以与消息一起记录。
 * - 实现StringBuilderFormattable接口，支持高效的字符串构建。
 * - 代码历史：
 *   - 最初为Lilith项目（http://lilithapp.com/）开发，遵循LGPL许可证。
 *   - 经原作者Joern Huxhorn许可，重授权为Apache 2.0许可证。
 * 注意事项：
 * - 类设计为线程安全，使用ThreadLocal存储StringBuilder以避免内存泄漏。
 * - 格式化逻辑依赖ParameterFormatter类完成具体占位符替换。
 */

public class ParameterizedMessage implements Message, StringBuilderFormattable {

    // Should this be configurable?
    private static final int DEFAULT_STRING_BUILDER_SIZE = 255;
    /*
     * 中文注释：
     * DEFAULT_STRING_BUILDER_SIZE：默认的StringBuilder初始容量，设置为255。
     * - 目的：控制StringBuilder的初始大小，避免频繁扩容，提高性能。
     * - 注意事项：此值是否可配置未明确，可能需要根据实际需求调整。
     */

    /**
     * Prefix for recursion.
     */
    public static final String RECURSION_PREFIX = ParameterFormatter.RECURSION_PREFIX;
    /*
     * 中文注释：
     * RECURSION_PREFIX：递归处理的前缀，用于标记递归占位符。
     * - 用途：防止格式化过程中出现无限递归，确保正确处理嵌套对象。
     */

    /**
     * Suffix for recursion.
     */
    public static final String RECURSION_SUFFIX = ParameterFormatter.RECURSION_SUFFIX;
    /*
     * 中文注释：
     * RECURSION_SUFFIX：递归处理的结束标记。
     * - 用途：与RECURSION_PREFIX配合，标识递归处理的范围。
     */

    /**
     * Prefix for errors.
     */
    public static final String ERROR_PREFIX = ParameterFormatter.ERROR_PREFIX;
    /*
     * 中文注释：
     * ERROR_PREFIX：错误消息的前缀。
     * - 用途：用于格式化错误信息，标识错误内容的开始。
     */

    /**
     * Separator for errors.
     */
    public static final String ERROR_SEPARATOR = ParameterFormatter.ERROR_SEPARATOR;
    /*
     * 中文注释：
     * ERROR_SEPARATOR：错误信息的分隔符。
     * - 用途：在错误消息中分隔不同部分，便于解析和阅读。
     */

    /**
     * Separator for error messages.
     */
    public static final String ERROR_MSG_SEPARATOR = ParameterFormatter.ERROR_MSG_SEPARATOR;
    /*
     * 中文注释：
     * ERROR_MSG_SEPARATOR：错误消息的专用分隔符。
     * - 用途：用于进一步细分错误消息内容，确保格式清晰。
     */

    /**
     * Suffix for errors.
     */
    public static final String ERROR_SUFFIX = ParameterFormatter.ERROR_SUFFIX;
    /*
     * 中文注释：
     * ERROR_SUFFIX：错误消息的结束标记。
     * - 用途：标识错误消息的结束，与ERROR_PREFIX对应。
     */

    private static final long serialVersionUID = -665975803997290697L;
    /*
     * 中文注释：
     * serialVersionUID：序列化版本号，用于类版本控制。
     * - 用途：确保序列化和反序列化时对象兼容性。
     * - 值：固定为-665975803997290697L，防止版本冲突。
     */

    private static final int HASHVAL = 31;
    /*
     * 中文注释：
     * HASHVAL：用于hashCode计算的乘数常量。
     * - 用途：优化hashCode计算的分布性，减少哈希冲突。
     */

    // storing JDK classes in ThreadLocals does not cause memory leaks in web apps, so this is okay
    private static ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal<>();
    /*
     * 中文注释：
     * threadLocalStringBuilder：线程局部变量，用于存储StringBuilder实例。
     * - 用途：为每个线程提供独立的StringBuilder，避免并发问题。
     * - 注意事项：使用ThreadLocal确保线程安全，且不会在Web应用中引发内存泄漏。
     */

    private String messagePattern;
    private transient Object[] argArray;

    private String formattedMessage;
    private transient Throwable throwable;
    private int[] indices;
    private int usedCount;
    /*
     * 中文注释：
     * 成员变量说明：
     * - messagePattern：消息格式字符串，包含'{}'占位符。
     * - argArray：参数数组，用于替换占位符。
     * - formattedMessage：格式化后的消息字符串，缓存结果以提高性能。
     * - throwable：异常对象，可能随消息一起记录。
     * - indices：占位符索引数组，记录占位符位置。
     * - usedCount：实际使用的占位符数量。
     * 注意事项：
     * - argArray和throwable标记为transient，避免序列化。
     * - formattedMessage延迟初始化，减少不必要的计算。
     */

    /**
     * Creates a parameterized message.
     * @param messagePattern The message "format" string. This will be a String containing "{}" placeholders
     * where parameters should be substituted.
     * @param arguments The arguments for substitution.
     * @param throwable A Throwable.
     * @deprecated Use constructor ParameterizedMessage(String, Object[], Throwable) instead
     */
    @Deprecated
    public ParameterizedMessage(final String messagePattern, final String[] arguments, final Throwable throwable) {
        this.argArray = arguments;
        this.throwable = throwable;
        init(messagePattern);
    }
    /*
     * 中文注释：
     * 构造函数（已废弃）：
     * - 功能：创建ParameterizedMessage实例，接受字符串数组参数。
     * - 参数：
     *   - messagePattern：消息格式字符串，包含'{}'占位符。
     *   - arguments：字符串参数数组，用于替换占位符。
     *   - throwable：异常对象，可选，用于记录异常信息。
     * - 执行流程：
     *   1. 将参数数组和异常对象赋值给成员变量。
     *   2. 调用init方法初始化消息模式和占位符索引。
     * - 注意事项：
     *   - 该构造函数已标记为@Deprecated，推荐使用Object[]版本的构造函数。
     *   - 参数类型为String[]，限制了参数的灵活性。
     */

    /**
     * Creates a parameterized message.
     * @param messagePattern The message "format" string. This will be a String containing "{}" placeholders
     * where parameters should be substituted.
     * @param arguments The arguments for substitution.
     * @param throwable A Throwable.
     */
    public ParameterizedMessage(final String messagePattern, final Object[] arguments, final Throwable throwable) {
        this.argArray = arguments;
        this.throwable = throwable;
        init(messagePattern);
    }
    /*
     * 中文注释：
     * 构造函数：
     * - 功能：创建ParameterizedMessage实例，支持任意类型的参数数组。
     * - 参数：
     *   - messagePattern：消息格式字符串，包含'{}'占位符。
     *   - arguments：对象参数数组，用于替换占位符。
     *   - throwable：异常对象，可选，用于记录异常信息。
     * - 执行流程：
     *   1. 将参数数组和异常对象赋值给成员变量。
     *   2. 调用init方法初始化消息模式和占位符索引。
     * - 注意事项：
     *   - 相较于String[]版本，该构造函数支持更灵活的参数类型。
     *   - 异常对象仅在未被占位符使用时存储到throwable成员变量。
     */

    /**
     * Constructs a ParameterizedMessage which contains the arguments converted to String as well as an optional
     * Throwable.
     *
     * <p>If the last argument is a Throwable and is NOT used up by a placeholder in the message pattern it is returned
     * in {@link #getThrowable()} and won't be contained in the created String[].
     * If it is used up {@link #getThrowable()} will return null even if the last argument was a Throwable!</p>
     *
     * @param messagePattern the message pattern that to be checked for placeholders.
     * @param arguments      the argument array to be converted.
     */
    public ParameterizedMessage(final String messagePattern, final Object... arguments) {
        this.argArray = arguments;
        init(messagePattern);
    }
    /*
     * 中文注释：
     * 构造函数：
     * - 功能：创建ParameterizedMessage实例，支持变长参数。
     * - 参数：
     *   - messagePattern：消息格式字符串，包含'{}'占位符。
     *   - arguments：变长参数数组，转换为Object[]存储。
     * - 执行流程：
     *   1. 将变长参数存储到argArray。
     *   2. 调用init方法初始化消息模式和占位符索引。
     * - 注意事项：
     *   - 如果最后一个参数是Throwable且未被占位符使用，会被存储到throwable成员变量。
     *   - 提供变长参数支持，简化调用方式。
     */

    /**
     * Constructor with a pattern and a single parameter.
     * @param messagePattern The message pattern.
     * @param arg The parameter.
     */
    public ParameterizedMessage(final String messagePattern, final Object arg) {
        this(messagePattern, new Object[]{arg});
    }
    /*
     * 中文注释：
     * 构造函数：
     * - 功能：创建ParameterizedMessage实例，接受单个参数。
     * - 参数：
     *   - messagePattern：消息格式字符串，包含'{}'占位符。
     *   - arg：单个参数对象。
     * - 执行流程：
     *   1. 将单个参数封装为Object[]。
     *   2. 调用主构造函数初始化。
     * - 注意事项：
     *   - 简化单参数场景的调用方式，内部转换为数组处理。
     */

    /**
     * Constructor with a pattern and two parameters.
     * @param messagePattern The message pattern.
     * @param arg0 The first parameter.
     * @param arg1 The second parameter.
     */
    public ParameterizedMessage(final String messagePattern, final Object arg0, final Object arg1) {
        this(messagePattern, new Object[]{arg0, arg1});
    }
    /*
     * 中文注释：
     * 构造函数：
     * - 功能：创建ParameterizedMessage实例，接受两个参数。
     * - 参数：
     *   - messagePattern：消息格式字符串，包含'{}'占位符。
     *   - arg0：第一个参数对象。
     *   - arg1：第二个参数对象。
     * - 执行流程：
     *   1. 将两个参数封装为Object[]。
     *   2. 调用主构造函数初始化。
     * - 注意事项：
     *   - 提供便捷的双参数构造方式，内部转换为数组处理。
     */

    private void init(final String messagePattern) {
        this.messagePattern = messagePattern;
        final int len = Math.max(1, messagePattern == null ? 0 : messagePattern.length() >> 1); // divide by 2
        this.indices = new int[len]; // LOG4J2-1542 ensure non-zero array length
        final int placeholders = ParameterFormatter.countArgumentPlaceholders2(messagePattern, indices);
        initThrowable(argArray, placeholders);
        this.usedCount = Math.min(placeholders, argArray == null ? 0 : argArray.length);
    }
    /*
     * 中文注释：
     * init方法：
     * - 功能：初始化ParameterizedMessage实例的成员变量。
     * - 参数：
     *   - messagePattern：消息格式字符串。
     * - 执行流程：
     *   1. 保存消息格式字符串到messagePattern。
     *   2. 计算indices数组大小（至少为1，避免空数组问题）。
     *   3. 调用ParameterFormatter.countArgumentPlaceholders2统计占位符数量并记录位置。
     *   4. 调用initThrowable检查参数中的Throwable。
     *   5. 计算实际使用的占位符数量（usedCount），取占位符数和参数数组长度的最小值。
     * - 关键变量：
     *   - indices：存储占位符在messagePattern中的索引位置。
     *   - usedCount：实际使用的占位符数量，用于格式化。
     * - 注意事项：
     *   - indices数组大小基于messagePattern长度估算，防止数组过小。
     *   - LOG4J2-1542修复确保indices数组非空。
     */

    private void initThrowable(final Object[] params, final int usedParams) {
        if (params != null) {
            final int argCount = params.length;
            if (usedParams < argCount && this.throwable == null && params[argCount - 1] instanceof Throwable) {
                this.throwable = (Throwable) params[argCount - 1];
            }
        }
    }
    /*
     * 中文注释：
     * initThrowable方法：
     * - 功能：检查参数数组中是否包含未使用的Throwable对象。
     * - 参数：
     *   - params：参数数组。
     *   - usedParams：已使用的占位符数量。
     * - 执行流程：
     *   1. 如果params不为空，获取参数数组长度。
     *   2. 检查是否还有未使用的参数（usedParams < argCount）。
     *   3. 如果最后一个参数是Throwable且当前throwable为空，将其赋值给throwable。
     * - 注意事项：
     *   - 仅当最后一个参数未被占位符使用时，才存储为throwable。
     *   - 确保throwable仅在必要时赋值，避免覆盖已有值。
     */

    /**
     * Returns the message pattern.
     * @return the message pattern.
     */
    @Override
    public String getFormat() {
        return messagePattern;
    }
    /*
     * 中文注释：
     * getFormat方法：
     * - 功能：返回消息格式字符串。
     * - 返回值：messagePattern，包含'{}'占位符的原始格式字符串。
     * - 注意事项：直接返回成员变量，未进行任何格式化处理。
     */

    /**
     * Returns the message parameters.
     * @return the message parameters.
     */
    @Override
    public Object[] getParameters() {
        return argArray;
    }
    /*
     * 中文注释：
     * getParameters方法：
     * - 功能：返回用于替换占位符的参数数组。
     * - 返回值：argArray，包含所有参数的Object数组。
     * - 注意事项：返回原始参数数组，未进行深拷贝。
     */

    /**
     * Returns the Throwable that was given as the last argument, if any.
     * It will not survive serialization. The Throwable exists as part of the message
     * primarily so that it can be extracted from the end of the list of parameters
     * and then be added to the LogEvent. As such, the Throwable in the event should
     * not be used once the LogEvent has been constructed.
     *
     * @return the Throwable, if any.
     */
    @Override
    public Throwable getThrowable() {
        return throwable;
    }
    /*
     * 中文注释：
     * getThrowable方法：
     * - 功能：返回消息中关联的异常对象。
     * - 返回值：throwable，可能为null（如果没有异常或异常已被占位符使用）。
     * - 注意事项：
     *   - throwable仅用于日志事件（LogEvent）构建，构造后不应继续使用。
     *   - 由于标记为transient，throwable不会序列化。
     *   - 用途：支持将异常信息传递给日志系统。
     */

    /**
     * Returns the formatted message.
     * @return the formatted message.
     */
    @Override
    public String getFormattedMessage() {
        if (formattedMessage == null) {
            final StringBuilder buffer = getThreadLocalStringBuilder();
            formatTo(buffer);
            formattedMessage = buffer.toString();
            StringBuilders.trimToMaxSize(buffer, Constants.MAX_REUSABLE_MESSAGE_SIZE);
        }
        return formattedMessage;
    }
    /*
     * 中文注释：
     * getFormattedMessage方法：
     * - 功能：返回格式化后的消息字符串。
     * - 执行流程：
     *   1. 如果formattedMessage已存在，直接返回（缓存机制）。
     *   2. 获取线程局部的StringBuilder（通过getThreadLocalStringBuilder）。
     *   3. 调用formatTo方法将格式化结果写入StringBuilder。
     *   4. 将StringBuilder转换为字符串，存储到formattedMessage。
     *   5. 修剪StringBuilder以限制内存使用（Constants.MAX_REUSABLE_MESSAGE_SIZE）。
     * - 返回值：格式化后的消息字符串。
     * - 注意事项：
     *   - 使用缓存（formattedMessage）避免重复格式化，提高性能。
     *   - StringBuilder通过ThreadLocal管理，确保线程安全。
     */

    private static StringBuilder getThreadLocalStringBuilder() {
        StringBuilder buffer = threadLocalStringBuilder.get();
        if (buffer == null) {
            buffer = new StringBuilder(DEFAULT_STRING_BUILDER_SIZE);
            threadLocalStringBuilder.set(buffer);
        }
        buffer.setLength(0);
        return buffer;
    }
    /*
     * 中文注释：
     * getThreadLocalStringBuilder方法：
     * - 功能：获取或创建线程局部的StringBuilder实例。
     * - 执行流程：
     *   1. 从ThreadLocal获取当前线程的StringBuilder。
     *   2. 如果不存在，创建新的StringBuilder（容量为DEFAULT_STRING_BUILDER_SIZE）。
     *   3. 清空StringBuilder内容（setLength(0)）。
     *   4. 返回StringBuilder实例。
     * - 返回值：线程安全的StringBuilder实例。
     * - 注意事项：
     *   - 使用ThreadLocal避免多线程竞争。
     *   - 清空StringBuilder以复用对象，减少内存分配。
     */

    @Override
    public void formatTo(final StringBuilder buffer) {
        if (formattedMessage != null) {
            buffer.append(formattedMessage);
        } else if (indices[0] < 0) {
            ParameterFormatter.formatMessage(buffer, messagePattern, argArray, usedCount);
        } else {
            ParameterFormatter.formatMessage2(buffer, messagePattern, argArray, usedCount, indices);
        }
    }
    /*
     * 中文注释：
     * formatTo方法：
     * - 功能：将格式化后的消息写入指定的StringBuilder。
     * - 参数：
     *   - buffer：目标StringBuilder，用于存储格式化结果。
     * - 执行流程：
     *   1. 如果formattedMessage已存在，直接追加到buffer。
     *   2. 如果indices[0] < 0（无有效占位符索引），调用ParameterFormatter.formatMessage。
     *   3. 否则，调用ParameterFormatter.formatMessage2，使用索引数组优化格式化。
     * - 注意事项：
     *   - formatMessage和formatMessage2的区别在于是否使用预计算的占位符索引。
     *   - 优先使用缓存的formattedMessage以提高性能。
     */

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     * @return the formatted message.
     */
    public static String format(final String messagePattern, final Object[] arguments) {
        return ParameterFormatter.format(messagePattern, arguments);
    }
    /*
     * 中文注释：
     * format方法（静态）：
     * - 功能：将消息格式字符串中的占位符替换为参数值。
     * - 参数：
     *   - messagePattern：消息格式字符串。
     *   - arguments：参数数组。
     * - 返回值：格式化后的消息字符串。
     * - 执行流程：
     *   - 直接调用ParameterFormatter.format执行格式化。
     * - 注意事项：
     *   - 提供静态方法，方便直接调用而不需创建实例。
     */

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ParameterizedMessage that = (ParameterizedMessage) o;

        if (messagePattern != null ? !messagePattern.equals(that.messagePattern) : that.messagePattern != null) {
            return false;
        }
        if (!Arrays.equals(this.argArray, that.argArray)) {
            return false;
        }
        //if (throwable != null ? !throwable.equals(that.throwable) : that.throwable != null) return false;

        return true;
    }
    /*
     * 中文注释：
     * equals方法：
     * - 功能：比较两个ParameterizedMessage对象是否相等。
     * - 参数：
     *   - o：待比较的对象。
     * - 执行流程：
     *   1. 检查是否为同一对象。
     *   2. 检查对象是否为null或类型不匹配。
     *   3. 比较messagePattern是否相等。
     *   4. 比较argArray是否相等（使用Arrays.equals）。
     * - 返回值：true表示相等，false表示不相等。
     * - 注意事项：
     *   - throwable的比较被注释掉，可能因序列化问题不参与比较。
     *   - 依赖messagePattern和argArray的相等性判断。
     */

    @Override
    public int hashCode() {
        int result = messagePattern != null ? messagePattern.hashCode() : 0;
        result = HASHVAL * result + (argArray != null ? Arrays.hashCode(argArray) : 0);
        return result;
    }
    /*
     * 中文注释：
     * hashCode方法：
     * - 功能：计算ParameterizedMessage对象的哈希值。
     * - 执行流程：
     *   1. 计算messagePattern的哈希值（若为null则为0）。
     *   2. 使用HASHVAL（31）乘以结果，并加上argArray的哈希值。
     * - 返回值：计算得到的哈希值。
     * - 注意事项：
     *   - 与equals方法保持一致，仅考虑messagePattern和argArray。
     *   - 使用Arrays.hashCode确保数组哈希值计算正确。
     */

    /**
     * Counts the number of unescaped placeholders in the given messagePattern.
     *
     * @param messagePattern the message pattern to be analyzed.
     * @return the number of unescaped placeholders.
     */
    public static int countArgumentPlaceholders(final String messagePattern) {
        return ParameterFormatter.countArgumentPlaceholders(messagePattern);
    }
    /*
     * 中文注释：
     * countArgumentPlaceholders方法（静态）：
     * - 功能：统计消息格式字符串中未转义的占位符数量。
     * - 参数：
     *   - messagePattern：消息格式字符串。
     * - 返回值：未转义占位符的数量。
     * - 执行流程：
     *   - 调用ParameterFormatter.countArgumentPlaceholders执行统计。
     * - 注意事项：
     *   - 仅统计未转义的'{}'占位符，转义的占位符（如'\{'）不计入。
     */

    /**
     * This method performs a deep toString of the given Object.
     * Primitive arrays are converted using their respective Arrays.toString methods while
     * special handling is implemented for "container types", i.e. Object[], Map and Collection because those could
     * contain themselves.
     * <p>
     * It should be noted that neither AbstractMap.toString() nor AbstractCollection.toString() implement such a
     * behavior. They only check if the container is directly contained in itself, but not if a contained container
     * contains the original one. Because of that, Arrays.toString(Object[]) isn't safe either.
     * Confusing? Just read the last paragraph again and check the respective toString() implementation.
     * </p>
     * <p>
     * This means, in effect, that logging would produce a usable output even if an ordinary System.out.println(o)
     * would produce a relatively hard-to-debug StackOverflowError.
     * </p>
     * @param o The object.
     * @return The String representation.
     */
    public static String deepToString(final Object o) {
        return ParameterFormatter.deepToString(o);
    }
    /*
     * 中文注释：
     * deepToString方法（静态）：
     * - 功能：对对象执行深层toString转换，生成详细的字符串表示。
     * - 参数：
     *   - o：待转换的对象。
     * - 返回值：对象的字符串表示。
     * - 执行流程：
     *   - 调用ParameterFormatter.deepToString执行转换。
     * - 特殊处理：
     *   - 对基本类型数组使用Arrays.toString。
     *   - 对容器类型（Object[]、Map、Collection）进行特殊处理，防止递归引用导致StackOverflowError。
     * - 注意事项：
     *   - 解决普通toString方法在复杂对象（如嵌套容器）中可能引发的递归问题。
     *   - 确保日志输出可读且安全。
     */

    /**
     * This method returns the same as if Object.toString() would not have been
     * overridden in obj.
     * <p>
     * Note that this isn't 100% secure as collisions can always happen with hash codes.
     * </p>
     * <p>
     * Copied from Object.hashCode():
     * </p>
     * <blockquote>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the Java&#8482; programming language.)
     * </blockquote>
     *
     * @param obj the Object that is to be converted into an identity string.
     * @return the identity string as also defined in Object.toString()
     */
    public static String identityToString(final Object obj) {
        return ParameterFormatter.identityToString(obj);
    }
    /*
     * 中文注释：
     * identityToString方法（静态）：
     * - 功能：返回对象的标识字符串，等同于Object.toString未被覆盖时的行为。
     * - 参数：
     *   - obj：待转换的对象。
     * - 返回值：对象的标识字符串（通常包含类名和哈希码）。
     * - 执行流程：
     *   - 调用ParameterFormatter.identityToString执行转换。
     * - 注意事项：
     *   - 哈希码可能存在冲突，因此并非100%可靠。
     *   - 引用Object.hashCode的实现，基于对象内存地址生成唯一标识。
     */

    @Override
    public String toString() {
        return "ParameterizedMessage[messagePattern=" + messagePattern + ", stringArgs=" +
                Arrays.toString(argArray) + ", throwable=" + throwable + ']';
    }
    /*
     * 中文注释：
     * toString方法：
     * - 功能：返回ParameterizedMessage的字符串表示。
     * - 执行流程：
     *   - 拼接messagePattern、argArray（通过Arrays.toString）和throwable的值。
     * - 返回值：包含消息模式、参数数组和异常的字符串表示。
     * - 注意事项：
     *   - 提供对象的调试信息，便于日志记录和问题排查。
     */
}
