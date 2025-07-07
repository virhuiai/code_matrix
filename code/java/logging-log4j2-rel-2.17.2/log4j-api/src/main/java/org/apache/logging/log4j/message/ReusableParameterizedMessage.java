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
 * 本代码遵循 Apache 许可证 2.0 版，授权 Apache 软件基金会（ASF）及其贡献者。
 * 软件按“现状”分发，不提供任何明示或暗示的担保。
 * 许可证的完整文本可在 http://www.apache.org/licenses/LICENSE-2.0 获取。
 */

package org.apache.logging.log4j.message;

import java.util.Arrays;

import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

/**
 * Reusable parameterized message. This message is mutable and is not safe to be accessed or modified by multiple
 * threads concurrently.
 *
 * @see ParameterizedMessage
 * @since 2.6
 */
/*
 * 中文注释：
 * 类名：ReusableParameterizedMessage
 * 主要功能：实现可重用的参数化消息，支持动态格式化日志消息，允许重复使用以提高性能。
 * 目的：减少对象分配，提升日志记录效率，适用于高性能日志场景。
 * 注意事项：该类是可变的（mutable），不支持多线程并发访问或修改，需确保线程安全。
 * 自版本：2.6
 */
@PerformanceSensitive("allocation")
/*
 * 中文注释：
 * 注解说明：@PerformanceSensitive("allocation") 表示该类对内存分配敏感，需优化以减少对象创建。
 */
public class ReusableParameterizedMessage implements ReusableMessage, ParameterVisitable, Clearable {

    private static final int MIN_BUILDER_SIZE = 512;
    /*
     * 中文注释：
     * 常量：MIN_BUILDER_SIZE
     * 用途：定义 StringBuilder 的最小初始容量为 512 字节，用于格式化消息以减少动态扩展。
     * 含义：确保 StringBuilder 有足够的初始容量以处理常见消息长度，提高性能。
     */
    private static final int MAX_PARMS = 10;
    /*
     * 中文注释：
     * 常量：MAX_PARMS
     * 用途：限制参数数组的最大长度为 10，用于存储消息参数。
     * 含义：限制参数数量以控制内存使用，优化性能。
     */
    private static final long serialVersionUID = 7800075879295123856L;
    /*
     * 中文注释：
     * 常量：serialVersionUID
     * 用途：定义序列化版本标识，确保序列化兼容性。
     * 含义：用于 Java 序列化机制，防止版本不匹配问题。
     */
    private transient ThreadLocal<StringBuilder> buffer; // non-static: LOG4J2-1583
    /*
     * 中文注释：
     * 变量：buffer
     * 用途：ThreadLocal 类型的 StringBuilder，用于线程本地存储格式化消息的缓冲区。
     * 含义：每个线程拥有独立的 StringBuilder 实例，避免多线程竞争，提升性能。
     * 注意事项：非静态字段（LOG4J2-1583），防止嵌套日志调用导致日志混乱。
     */

    private String messagePattern;
    /*
     * 中文注释：
     * 变量：messagePattern
     * 用途：存储消息的格式化模板（例如 "User {} logged in"）。
     * 含义：定义日志消息的结构，包含占位符（如 {}）以插入参数。
     */
    private int argCount;
    /*
     * 中文注释：
     * 变量：argCount
     * 用途：记录传入参数的总数。
     * 含义：跟踪实际提供的参数数量，用于格式化消息。
     */
    private int usedCount;
    /*
     * 中文注释：
     * 变量：usedCount
     * 用途：记录实际使用的参数数量（占位符与参数数量的最小值）。
     * 含义：确保只使用有效的参数，避免多余参数导致错误。
     */
    private final int[] indices = new int[256];
    /*
     * 中文注释：
     * 变量：indices
     * 用途：存储消息模板中占位符（{}）的位置索引。
     * 含义：记录每个占位符在 messagePattern 中的位置，便于快速格式化。
     * 配置：固定长度为 256，限制最大占位符数量以优化性能。
     */
    private transient Object[] varargs;
    /*
     * 中文注释：
     * 变量：varargs
     * 用途：存储可变参数数组（当参数通过 varargs 方式传入时）。
     * 含义：支持动态数量的参数输入，临时存储以供格式化使用。
     * 注意事项：transient 修饰，序列化时不保存。
     */
    private transient Object[] params = new Object[MAX_PARMS];
    /*
     * 中文注释：
     * 变量：params
     * 用途：存储固定参数数组（最多 MAX_PARMS 个）。
     * 含义：用于存储固定数量的参数（1-10 个），优化常见用例的性能。
     * 注意事项：transient 修饰，序列化时不保存。
     */
    private transient Throwable throwable;
    /*
     * 中文注释：
     * 变量：throwable
     * 用途：存储最后一个参数（如果它是 Throwable 类型）作为异常信息。
     * 含义：支持在日志消息中附加异常信息，便于日志事件处理。
     * 注意事项：transient 修饰，序列化时不保存。
     */
    transient boolean reserved = false; // LOG4J2-1583 prevent scrambled logs with nested logging calls
    /*
     * 中文注释：
     * 变量：reserved
     * 用途：标记消息是否被保留（锁定）以防止嵌套日志调用导致混乱。
     * 含义：当 reserved 为 true 时，表示消息正在使用，避免被其他日志调用覆盖。
     * 注意事项：transient 修饰，序列化时不保存；LOG4J2-1583 修复嵌套日志问题。
     */

    /**
     * Creates a reusable message.
     */
    /*
     * 中文注释：
     * 方法：ReusableParameterizedMessage 构造函数
     * 主要功能：初始化一个可重用的参数化消息对象。
     * 执行流程：默认构造函数，不执行任何初始化操作，仅创建对象。
     * 注意事项：所有字段将在后续 set 方法调用时初始化。
     */
    public ReusableParameterizedMessage() {
    }

    private Object[] getTrimmedParams() {
        /*
         * 中文注释：
         * 方法：getTrimmedParams
         * 主要功能：返回裁剪后的参数数组，仅包含有效参数（长度为 argCount）。
         * 返回值：Object[]，裁剪后的参数数组。
         * 执行流程：
         *   1. 如果 varargs 为空，返回 params 数组的前 argCount 个元素。
         *   2. 如果 varargs 不为空，直接返回 varargs 数组。
         * 注意事项：确保返回的数组长度与实际参数数量一致，避免多余元素。
         */
        return varargs == null ? Arrays.copyOf(params, argCount) : varargs;
    }

    private Object[] getParams() {
        /*
         * 中文注释：
         * 方法：getParams
         * 主要功能：返回当前使用的参数数组（varargs 或 params）。
         * 返回值：Object[]，当前参数数组。
         * 执行流程：
         *   1. 如果 varargs 为空，返回 params 数组。
         *   2. 如果 varargs 不为空，返回 varargs 数组。
         * 注意事项：直接返回原始数组引用，不进行裁剪。
         */
        return varargs == null ? params : varargs;
    }

    // see interface javadoc
    @Override
    public Object[] swapParameters(final Object[] emptyReplacement) {
        /*
         * 中文注释：
         * 方法：swapParameters
         * 主要功能：交换参数数组，将当前参数数组返回并用新的空数组替换。
         * 参数：
         *   - emptyReplacement: Object[]，新的空参数数组，用于替换当前 params 数组。
         * 返回值：Object[]，当前的参数数组（varargs 或 params）。
         * 执行流程：
         *   1. 如果 varargs 为空（使用 params 数组）：
         *      a. 如果 emptyReplacement 长度 >= MAX_PARMS，直接用其替换 params。
         *      b. 如果 emptyReplacement 长度 >= argCount 但 < MAX_PARMS，将 params 复制到 emptyReplacement 并清空 params。
         *      c. 如果 emptyReplacement 长度 < argCount，丢弃 emptyReplacement，创建新的 MAX_PARMS 大小数组。
         *   2. 如果 varargs 不为空：
         *      a. 如果 argCount <= emptyReplacement 长度，使用 emptyReplacement。
         *      b. 否则，创建新的 argCount 大小数组（LOG4J2-1688 修复）。
         *      c. 将 varargs 内容复制到结果数组。
         * 注意事项：
         *   - 确保返回的数组长度至少为 MAX_PARMS，以支持未来的参数重用。
         *   - 清空 params 数组中的引用，防止内存泄漏。
         *   - LOG4J2-1688 修复：避免修改非 varargs 的应用数组。
         */
        Object[] result;
        if (varargs == null) {
            result = params;
            if (emptyReplacement.length >= MAX_PARMS) {
                params = emptyReplacement;
            } else if (argCount <= emptyReplacement.length) {
                // Bad replacement! Too small, may blow up future 10-arg messages.
                // copy params into the specified replacement array and return that
                System.arraycopy(params, 0, emptyReplacement, 0, argCount);
                // Do not retain references to objects in the reusable params array.
                for (int i = 0; i < argCount; i++) {
                    params[i] = null;
                }
                result = emptyReplacement;
            } else {
                // replacement array is too small for current content and future content: discard it
                params = new Object[MAX_PARMS];
            }
        } else {
            // The returned array will be reused by the caller in future swapParameter() calls.
            // Therefore we want to avoid returning arrays with less than 10 elements.
            // If the vararg array is less than 10 params we just copy its content into the specified array
            // and return it. This helps the caller to retain a reusable array of at least 10 elements.
            // NOTE: LOG4J2-1688 unearthed the use case that an application array (not a varargs array) is passed
            // as the argument array. This array should not be modified, so it cannot be passed to the caller
            // who will at some point null out the elements in the array).
            if (argCount <= emptyReplacement.length) {
                result = emptyReplacement;
            } else {
                result = new Object[argCount]; // LOG4J2-1688
            }
            // copy params into the specified replacement array and return that
            System.arraycopy(varargs, 0, result, 0, argCount);
        }
        return result;
    }

    // see interface javadoc
    @Override
    public short getParameterCount() {
        /*
         * 中文注释：
         * 方法：getParameterCount
         * 主要功能：返回当前参数数量。
         * 返回值：short，参数数量（argCount）。
         * 执行流程：直接返回 argCount 的短整型值。
         */
        return (short) argCount;
    }

    @Override
    public <S> void forEachParameter(final ParameterConsumer<S> action, final S state) {
        /*
         * 中文注释：
         * 方法：forEachParameter
         * 主要功能：遍历所有参数并对每个参数执行指定操作。
         * 参数：
         *   - action: ParameterConsumer<S>，参数处理函数，定义如何处理每个参数。
         *   - state: S，传递给 action 的状态对象。
         * 执行流程：
         *   1. 获取当前参数数组（getParams）。
         *   2. 遍历前 argCount 个参数，对每个参数调用 action.accept。
         * 注意事项：支持泛型状态，允许灵活的参数处理逻辑。
         */
        final Object[] parameters = getParams();
        for (short i = 0; i < argCount; i++) {
            action.accept(parameters[i], i, state);
        }
    }

    @Override
    public Message memento() {
        /*
         * 中文注释：
         * 方法：memento
         * 主要功能：创建当前消息的不可变快照（memento），用于保存当前状态。
         * 返回值：Message，新的 ParameterizedMessage 实例，包含当前 messagePattern 和参数。
         * 执行流程：
         *   1. 调用 getTrimmedParams 获取裁剪后的参数数组。
         *   2. 创建并返回新的 ParameterizedMessage 实例。
         * 注意事项：生成的快照不可变，适合在日志事件中长期保存。
         */
        return new ParameterizedMessage(messagePattern, getTrimmedParams());
    }

    private void init(final String messagePattern, final int argCount, final Object[] paramArray) {
        /*
         * 中文注释：
         * 方法：init
         * 主要功能：初始化消息对象，设置消息模板、参数数量和异常信息。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - argCount: int，传入参数数量。
         *   - paramArray: Object[]，参数数组（varargs 或 params）。
         * 执行流程：
         *   1. 清空 varargs，设置 messagePattern 和 argCount。
         *   2. 调用 count 方法计算 messagePattern 中的占位符数量，存储到 indices。
         *   3. 调用 initThrowable 检查并设置 throwable（如果最后一个参数是 Throwable）。
         *   4. 设置 usedCount 为占位符数量和 argCount 的最小值。
         * 注意事项：确保参数和占位符匹配，处理多余参数或占位符的情况。
         */
        this.varargs = null;
        this.messagePattern = messagePattern;
        this.argCount = argCount;
        final int placeholderCount = count(messagePattern, indices);
        initThrowable(paramArray, argCount, placeholderCount);
        this.usedCount = Math.min(placeholderCount, argCount);
    }

    private static int count(final String messagePattern, final int[] indices) {
        /*
         * 中文注释：
         * 方法：count
         * 主要功能：计算消息模板中的占位符数量，并记录占位符位置。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - indices: int[]，存储占位符位置的数组。
         * 返回值：int，占位符数量。
         * 执行流程：
         *   1. 尝试调用 ParameterFormatter.countArgumentPlaceholders2（快速路径）。
         *   2. 如果失败（例如占位符超过 256 个），回退到 ParameterFormatter.countArgumentPlaceholders。
         * 注意事项：优先使用快速路径以提高性能，异常处理确保健壮性。
         */
        try {
            // try the fast path first
            return ParameterFormatter.countArgumentPlaceholders2(messagePattern, indices);
        } catch (final Exception ex) { // fallback if more than int[] length (256) parameter placeholders
            return ParameterFormatter.countArgumentPlaceholders(messagePattern);
        }
    }

    private void initThrowable(final Object[] params, final int argCount, final int usedParams) {
        /*
         * 中文注释：
         * 方法：initThrowable
         * 主要功能：检查最后一个参数是否为 Throwable，并设置 throwable 字段。
         * 参数：
         *   - params: Object[]，参数数组。
         *   - argCount: int，参数总数。
         *   - usedParams: int，实际使用的参数数量（占位符数量）。
         * 执行流程：
         *   1. 如果 usedParams < argCount 且最后一个参数是 Throwable 类型，设置为 throwable。
         *   2. 否则，将 throwable 设置为 null。
         * 注意事项：支持附加异常信息，便于日志事件处理。
         */
        if (usedParams < argCount && params[argCount - 1] instanceof Throwable) {
            this.throwable = (Throwable) params[argCount - 1];
        } else {
            this.throwable = null;
        }
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object... arguments) {
        /*
         * 中文注释：
         * 方法：set（可变参数版本）
         * 主要功能：设置消息模板和可变参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - arguments: Object...，可变参数数组。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 调用 init 方法，传入 messagePattern、参数数量和 arguments 数组。
         *   2. 设置 varargs 为 arguments。
         *   3. 返回当前对象。
         * 注意事项：支持动态参数数量，适合灵活的调用场景。
         */
        init(messagePattern, arguments == null ? 0 : arguments.length, arguments);
        varargs = arguments;
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0) {
        /*
         * 中文注释：
         * 方法：set（单参数版本）
         * 主要功能：设置消息模板和单个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0: Object，第一个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0 存储到 params[0]。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 1 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化单参数场景，减少数组分配。
         */
        params[0] = p0;
        init(messagePattern, 1, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1) {
        /*
         * 中文注释：
         * 方法：set（双参数版本）
         * 主要功能：设置消息模板和两个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1: Object，第一个和第二个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1 存储到 params[0]、params[1]。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 2 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化双参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        init(messagePattern, 2, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2) {
        /*
         * 中文注释：
         * 方法：set（三参数版本）
         * 主要功能：设置消息模板和三个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2: Object，三个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 3 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化三参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        init(messagePattern, 3, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2, final Object p3) {
        /*
         * 中文注释：
         * 方法：set（四参数版本）
         * 主要功能：设置消息模板和四个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2, p3: Object，四个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2、p3 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 4 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化四参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        params[3] = p3;
        init(messagePattern, 4, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        /*
         * 中文注释：
         * 方法：set（五参数版本）
         * 主要功能：设置消息模板和五个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2, p3, p4: Object，五个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2、p3、p4 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 5 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化五参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        params[3] = p3;
        params[4] = p4;
        init(messagePattern, 5, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        /*
         * 中文注释：
         * 方法：set（六参数版本）
         * 主要功能：设置消息模板和六个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2, p3, p4, p5: Object，六个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2、p3、p4、p5 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 6 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化六参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        params[3] = p3;
        params[4] = p4;
        params[5] = p5;
        init(messagePattern, 6, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {
        /*
         * 中文注释：
         * 方法：set（七参数版本）
         * 主要功能：设置消息模板和七个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2, p3, p4, p5, p6: Object，七个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2、p3、p4、p5、p6 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 7 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化七参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        params[3] = p3;
        params[4] = p4;
        params[5] = p5;
        params[6] = p6;
        init(messagePattern, 7, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {
        /*
         * 中文注释：
         * 方法：set（八参数版本）
         * 主要功能：设置消息模板和八个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2, p3, p4, p5, p6, p7: Object，八个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2、p3、p4、p5、p6、p7 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 8 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化八参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        params[3] = p3;
        params[4] = p4;
        params[5] = p5;
        params[6] = p6;
        params[7] = p7;
        init(messagePattern, 8, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {
        /*
         * 中文注释：
         * 方法：set（九参数版本）
         * 主要功能：设置消息模板和九个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2, p3, p4, p5, p6, p7, p8: Object，九个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2、p3、p4、p5、p6、p7、p8 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 9 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化九参数场景，减少数组分配。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        params[3] = p3;
        params[4] = p4;
        params[5] = p5;
        params[6] = p6;
        params[7] = p7;
        params[8] = p8;
        init(messagePattern, 9, params);
        return this;
    }

    ReusableParameterizedMessage set(final String messagePattern, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {
        /*
         * 中文注释：
         * 方法：set（十参数版本）
         * 主要功能：设置消息模板和十个参数，初始化消息对象。
         * 参数：
         *   - messagePattern: String，消息格式模板。
         *   - p0, p1, p2, p3, p4, p5, p6, p7, p8, p9: Object，十个参数。
         * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
         * 执行流程：
         *   1. 将 p0、p1、p2、p3、p4、p5、p6、p7、p8、p9 存储到 params 数组。
         *   2. 调用 init 方法，传入 messagePattern、参数数量 10 和 params 数组。
         *   3. 返回当前对象。
         * 注意事项：优化十参数场景，减少数组分配，达到 MAX_PARMS 上限。
         */
        params[0] = p0;
        params[1] = p1;
        params[2] = p2;
        params[3] = p3;
        params[4] = p4;
        params[5] = p5;
        params[6] = p6;
        params[7] = p7;
        params[8] = p8;
        params[9] = p9;
        init(messagePattern, 10, params);
        return this;
    }

    /**
     * Returns the message pattern.
     * @return the message pattern.
     */
    /*
     * 中文注释：
     * 方法：getFormat
     * 主要功能：返回消息格式模板。
     * 返回值：String，消息模板（messagePattern）。
     * 执行流程：直接返回 messagePattern 字段。
     */
    @Override
    public String getFormat() {
        return messagePattern;
    }

    /**
     * Returns the message parameters.
     * @return the message parameters.
     */
    /*
     * 中文注释：
     * 方法：getParameters
     * 主要功能：返回裁剪后的参数数组。
     * 返回值：Object[]，裁剪后的参数数组（长度为 argCount）。
     * 执行流程：调用 getTrimmedParams 返回裁剪后的参数数组。
     */
    @Override
    public Object[] getParameters() {
        return getTrimmedParams();
    }

    /**
     * Returns the Throwable that was given as the last argument, if any.
     * It will not survive serialization. The Throwable exists as part of the message
     * primarily so that it can be extracted from the end of the list of parameters
     * and then be added to the LogEvent. As such, the Throwable in the event should
     * not be used once the LogEvent has been constructed.
     *
     * @return the Throwable, if any.
     */
    /*
     * 中文注释：
     * 方法：getThrowable
     * 主要功能：返回附加的异常对象（如果存在）。
     * 返回值：Throwable，最后一个参数为 Throwable 类型时返回，否则为 null。
     * 执行流程：直接返回 throwable 字段。
     * 注意事项：
     *   - throwable 不支持序列化（transient）。
     *   - 主要用于将异常附加到 LogEvent，事件构造后不应继续使用。
     */
    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Returns the formatted message.
     * @return the formatted message.
     */
    /*
     * 中文注释：
     * 方法：getFormattedMessage
     * 主要功能：返回格式化后的完整消息字符串。
     * 返回值：String，格式化后的消息。
     * 执行流程：
     *   1. 调用 getBuffer 获取线程本地的 StringBuilder。
     *   2. 调用 formatTo 将消息格式化到 StringBuilder。
     *   3. 将 StringBuilder 转换为字符串并返回。
     *   4. 使用 StringBuilders.trimToMaxSize 限制 StringBuilder 大小（Constants.MAX_REUSABLE_MESSAGE_SIZE）。
     * 注意事项：优化内存使用，限制缓冲区大小以防止内存溢出。
     */
    @Override
    public String getFormattedMessage() {
        final StringBuilder sb = getBuffer();
        formatTo(sb);
        final String result = sb.toString();
        StringBuilders.trimToMaxSize(sb, Constants.MAX_REUSABLE_MESSAGE_SIZE);
        return result;
    }

    private StringBuilder getBuffer() {
        /*
         * 中文注释：
         * 方法：getBuffer
         * 主要功能：获取或初始化线程本地的 StringBuilder 缓冲区。
         * 返回值：StringBuilder，线程本地的缓冲区。
         * 执行流程：
         *   1. 如果 buffer 未初始化，创建新的 ThreadLocal。
         *   2. 获取线程本地的 StringBuilder。
         *   3. 如果 StringBuilder 为 null，创建新的 StringBuilder，初始容量为 MIN_BUILDER_SIZE 或 messagePattern 长度的两倍。
         *   4. 清空 StringBuilder 并返回。
         * 注意事项：
         *   - 使用 ThreadLocal 确保线程安全。
         *   - 动态计算初始容量以优化性能。
         */
        if (buffer == null) {
            buffer = new ThreadLocal<>();
        }
        StringBuilder result = buffer.get();
        if (result == null) {
            final int currentPatternLength = messagePattern == null ? 0 : messagePattern.length();
            result = new StringBuilder(Math.max(MIN_BUILDER_SIZE, currentPatternLength * 2));
            buffer.set(result);
        }
        result.setLength(0);
        return result;
    }

    @Override
    public void formatTo(final StringBuilder builder) {
        /*
         * 中文注释：
         * 方法：formatTo
         * 主要功能：将消息格式化到指定的 StringBuilder。
         * 参数：
         *   - builder: StringBuilder，目标缓冲区，用于存储格式化结果。
         * 执行流程：
         *   1. 如果 indices[0] < 0（无占位符位置索引），调用 ParameterFormatter.formatMessage 进行格式化。
         *   2. 否则，调用 ParameterFormatter.formatMessage2 使用索引快速格式化。
         * 注意事项：根据占位符索引的存在选择合适的格式化方法，优化性能。
         */
        if (indices[0] < 0) {
            ParameterFormatter.formatMessage(builder, messagePattern, getParams(), argCount);
        } else {
            ParameterFormatter.formatMessage2(builder, messagePattern, getParams(), usedCount, indices);
        }
    }

    /**
     * Sets the reserved flag to true and returns this object.
     * @return this object
     * @since 2.7
     */
    /*
     * 中文注释：
     * 方法：reserve
     * 主要功能：将 reserved 标志设为 true，锁定消息对象以防止嵌套调用干扰。
     * 返回值：ReusableParameterizedMessage，当前对象（支持方法链）。
     * 执行流程：设置 reserved = true 并返回当前对象。
     * 注意事项：
     *   - 自版本 2.7 引入。
     *   - 用于防止嵌套日志调用导致日志混乱（LOG4J2-1583）。
     */
    ReusableParameterizedMessage reserve() {
        reserved = true;
        return this;
    }

    @Override
    public String toString() {
        /*
         * 中文注释：
         * 方法：toString
         * 主要功能：返回消息对象的字符串表示。
         * 返回值：String，包含消息模板、参数数组和异常信息的描述。
         * 执行流程：
         *   1. 拼接 messagePattern、getParameters() 和 throwable 的字符串表示。
         *   2. 返回格式化的字符串。
         */
        return "ReusableParameterizedMessage[messagePattern=" + getFormat() + ", stringArgs=" +
                Arrays.toString(getParameters()) + ", throwable=" + getThrowable() + ']';
    }

    @Override
    public void clear() { // LOG4J2-1583
        // This method does not clear parameter values, those are expected to be swapped to a
        // reusable message, which is responsible for clearing references.
        /*
         * 中文注释：
         * 方法：clear
         * 主要功能：清空消息对象的状态，重置为初始状态。
         * 执行流程：
         *   1. 设置 reserved = false。
         *   2. 清空 varargs、messagePattern 和 throwable。
         * 注意事项：
         *   - 不清空 params 数组中的参数引用，期望通过 swapParameters 替换。
         *   - LOG4J2-1583 修复：确保嵌套日志调用后正确清理状态。
         */
        reserved = false;
        varargs = null;
        messagePattern = null;
        throwable = null;
    }
}
