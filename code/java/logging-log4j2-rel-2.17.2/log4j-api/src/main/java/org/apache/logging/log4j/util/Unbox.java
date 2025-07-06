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
package org.apache.logging.log4j.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Utility for preventing primitive parameter values from being auto-boxed. Auto-boxing creates temporary objects
 * which contribute to pressure on the garbage collector. With this utility users can convert primitive values directly
 * into text without allocating temporary objects.
 * <p>
 * Example usage:
 * </p><pre>
 * import static org.apache.logging.log4j.util.Unbox.box;
 * ...
 * long longValue = 123456L;
 * double doubleValue = 3.14;
 * // prevent primitive values from being auto-boxed
 * logger.debug("Long value={}, double value={}", box(longValue), box(doubleValue));
 * </pre>
 * <p>
 * This class manages a small thread-local ring buffer of StringBuilders.
 * Each time one of the {@code box()} methods is called, the next slot in the ring buffer is used, until the ring
 * buffer is full and the first slot is reused. By default the Unbox ring buffer has 32 slots, so user code can
 * have up to 32 boxed primitives in a single logger call.
 * </p>
 * <p>
 * If more slots are required, set system property {@code log4j.unbox.ringbuffer.size} to the desired ring buffer size.
 * Note that the specified number will be rounded up to the nearest power of 2.
 * </p>
 * @since 2.6
 */
/*
 * 中文注释：
 * 该类用于防止基本类型参数被自动装箱，减少创建临时对象对垃圾回收器的压力。
 * 提供将基本类型值直接转换为文本的功能，避免分配临时对象。
 * 使用示例：通过静态导入 box 方法，将 long 和 double 值转换为 StringBuilder，避免自动装箱。
 * 管理一个线程局部的 StringBuilder 环形缓冲区，默认大小为32，循环使用缓冲区槽位。
 * 可通过系统属性 log4j.unbox.ringbuffer.size 配置缓冲区大小，实际大小将向上取整为2的幂。
 * 关键配置：log4j.unbox.ringbuffer.size 决定环形缓冲区大小，默认32。
 * 注意事项：缓冲区大小需为2的幂，确保高效索引；若配置无效，使用默认值。
 */
@PerformanceSensitive("allocation")
public class Unbox {
    private static final Logger LOGGER = StatusLogger.getLogger();
    // 中文注释：日志记录器，用于记录警告信息，例如无效的缓冲区大小配置。
    private static final int BITS_PER_INT = 32;
    // 中文注释：整数的位数，用于计算2的幂。
    private static final int RINGBUFFER_MIN_SIZE = 32;
    // 中文注释：环形缓冲区最小大小，默认为32。
    private static final int RINGBUFFER_SIZE = calculateRingBufferSize("log4j.unbox.ringbuffer.size");
    // 中文注释：环形缓冲区实际大小，通过系统属性计算，向上取整为2的幂。
    private static final int MASK = RINGBUFFER_SIZE - 1;
    // 中文注释：掩码，用于环形缓冲区索引计算，确保索引在缓冲区范围内。

    /**
     * State implementation that only puts JDK classes in ThreadLocals, so this is safe to be used from
     * web applications. Web application containers have thread pools that may hold on to ThreadLocal objects
     * after the application was stopped. This may prevent the classes of the application from being unloaded,
     * causing memory leaks.
     * <p>
     * Such memory leaks will not occur if only JDK classes are stored in ThreadLocals.
     * </p>
     */
    /*
     * 中文注释：
     * WebSafeState 类仅在 ThreadLocal 中存储 JDK 类，适用于 Web 应用程序。
     * 目的：避免 Web 容器线程池在应用停止后持有 ThreadLocal 对象，导致内存泄漏。
     * 注意事项：仅使用 JDK 类存储在 ThreadLocal 中，确保应用卸载时无内存泄漏。
     */
    private static class WebSafeState {
        private final ThreadLocal<StringBuilder[]> ringBuffer = new ThreadLocal<>();
        // 中文注释：线程局部变量，存储 StringBuilder 数组作为环形缓冲区。
        private final ThreadLocal<int[]> current = new ThreadLocal<>();
        // 中文注释：线程局部变量，存储当前缓冲区索引。

        public StringBuilder getStringBuilder() {
            StringBuilder[] array = ringBuffer.get();
            if (array == null) {
                array = new StringBuilder[RINGBUFFER_SIZE];
                for (int i = 0; i < array.length; i++) {
                    array[i] = new StringBuilder(21);
                }
                ringBuffer.set(array);
                current.set(new int[1]);
            }
            final int[] index = current.get();
            final StringBuilder result = array[MASK & index[0]++];
            result.setLength(0);
            return result;
        }
        /*
         * 中文注释：
         * 方法目的：获取一个清空的 StringBuilder 用于存储基本类型值的文本表示。
         * 关键步骤：
         * 1. 从 ThreadLocal 获取环形缓冲区数组，若为空则初始化。
         * 2. 初始化时创建指定大小的 StringBuilder 数组，每个容量为21。
         * 3. 使用掩码和索引获取当前 StringBuilder，并清空其内容。
         * 交互逻辑：通过线程局部变量确保线程安全，循环使用缓冲区槽位。
         */

        public boolean isBoxedPrimitive(final StringBuilder text) {
            final StringBuilder[] array = ringBuffer.get();
            if (array == null) {
                return false;
            }
            for (int i = 0; i < array.length; i++) {
                if (text == array[i]) {
                    return true;
                }
            }
            return false;
        }
        /*
         * 中文注释：
         * 方法目的：检查给定的 StringBuilder 是否属于当前线程的环形缓冲区。
         * 关键步骤：
         * 1. 获取线程局部缓冲区数组，若为空返回 false。
         * 2. 遍历数组，检查 text 是否与任一 StringBuilder 实例相同。
         * 用途：用于验证 StringBuilder 是否由本类的 box 方法生成。
         */
    }

    private static class State {
        private final StringBuilder[] ringBuffer = new StringBuilder[RINGBUFFER_SIZE];
        // 中文注释：非线程局部环形缓冲区，存储 StringBuilder 数组。
        private int current;
        // 中文注释：当前缓冲区索引，记录下一个使用的槽位。
        State() {
            for (int i = 0; i < ringBuffer.length; i++) {
                ringBuffer[i] = new StringBuilder(21);
            }
        }
        /*
         * 中文注释：
         * 构造方法：初始化环形缓冲区，创建指定大小的 StringBuilder 数组。
         * 关键参数：每个 StringBuilder 初始容量为21，适合存储基本类型值的文本。
         */

        public StringBuilder getStringBuilder() {
            final StringBuilder result = ringBuffer[MASK & current++];
            result.setLength(0);
            return result;
        }
        /*
         * 中文注释：
         * 方法目的：获取一个清空的 StringBuilder 用于存储基本类型值的文本表示。
         * 关键步骤：
         * 1. 使用掩码和当前索引获取 StringBuilder。
         * 2. 清空 StringBuilder 内容。
         * 交互逻辑：通过递增索引循环使用缓冲区，非线程安全，依赖外部同步。
         */

        public boolean isBoxedPrimitive(final StringBuilder text) {
            for (int i = 0; i < ringBuffer.length; i++) {
                if (text == ringBuffer[i]) {
                    return true;
                }
            }
            return false;
        }
        /*
         * 中文注释：
         * 方法目的：检查给定的 StringBuilder 是否属于环形缓冲区。
         * 关键步骤：遍历缓冲区数组，检查 text 是否与任一 StringBuilder 实例相同。
         * 用途：验证 StringBuilder 是否由本类的 box 方法生成。
         */
    }
    private static ThreadLocal<State> threadLocalState = new ThreadLocal<>();
    // 中文注释：线程局部变量，存储 State 实例，确保线程安全。
    private static WebSafeState webSafeState = new WebSafeState();
    // 中文注释：WebSafeState 实例，用于 Web 应用环境下的缓冲区管理。

    private Unbox() {
        // this is a utility
    }
    // 中文注释：私有构造方法，防止实例化，强调工具类性质。

    private static int calculateRingBufferSize(final String propertyName) {
        final String userPreferredRBSize = PropertiesUtil.getProperties().getStringProperty(propertyName,
                String.valueOf(RINGBUFFER_MIN_SIZE));
        try {
            int size = Integer.parseInt(userPreferredRBSize.trim());
            if (size < RINGBUFFER_MIN_SIZE) {
                size = RINGBUFFER_MIN_SIZE;
                LOGGER.warn("Invalid {} {}, using minimum size {}.", propertyName, userPreferredRBSize,
                        RINGBUFFER_MIN_SIZE);
            }
            return ceilingNextPowerOfTwo(size);
        } catch (final Exception ex) {
            LOGGER.warn("Invalid {} {}, using default size {}.", propertyName, userPreferredRBSize,
                    RINGBUFFER_MIN_SIZE);
            return RINGBUFFER_MIN_SIZE;
        }
    }
    /*
     * 中文注释：
     * 方法目的：计算环形缓冲区大小，根据系统属性配置。
     * 关键参数：
     * - propertyName：系统属性名，例如 log4j.unbox.ringbuffer.size。
     * 关键步骤：
     * 1. 获取用户配置的缓冲区大小，默认为 RINGBUFFER_MIN_SIZE（32）。
     * 2. 验证大小是否合法，若小于最小值则使用默认值并记录警告。
     * 3. 将大小向上取整为2的幂。
     * 事件处理：捕获异常，若配置无效则使用默认大小并记录警告。
     * 注意事项：确保缓冲区大小为2的幂以优化索引计算。
     */

    /**
     * Calculate the next power of 2, greater than or equal to x.
     * <p>
     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
     *
     * @param x Value to round up
     * @return The next power of 2 from x inclusive
     */
    private static int ceilingNextPowerOfTwo(final int x) {
        return 1 << (BITS_PER_INT - Integer.numberOfLeadingZeros(x - 1));
    }
    /*
     * 中文注释：
     * 方法目的：计算大于或等于输入值的下一个2的幂。
     * 关键参数：
     * - x：需要向上取整的整数。
     * 关键步骤：使用位运算，基于整数前导零的个数计算2的幂。
     * 用途：确保环形缓冲区大小为2的幂，优化索引计算。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final float value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 float 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 float 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 float 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final double value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 double 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 double 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 double 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final short value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 short 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 short 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 short 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final int value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 int 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 int 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 int 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final char value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 char 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 char 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 char 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final long value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 long 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 long 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 long 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final byte value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 byte 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 byte 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 byte 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    /**
     * Returns a {@code StringBuilder} containing the text representation of the specified primitive value.
     * This method will not allocate temporary objects.
     *
     * @param value the value whose text representation to return
     * @return a {@code StringBuilder} containing the text representation of the specified primitive value
     */
    @PerformanceSensitive("allocation")
    public static StringBuilder box(final boolean value) {
        return getSB().append(value);
    }
    /*
     * 中文注释：
     * 方法目的：将 boolean 值转换为 StringBuilder 表示，避免自动装箱。
     * 关键参数：
     * - value：要转换的 boolean 值。
     * 关键步骤：
     * 1. 获取 StringBuilder（通过 getSB 方法）。
     * 2. 将 boolean 值追加到 StringBuilder。
     * 交互逻辑：通过环形缓冲区循环使用 StringBuilder，确保无临时对象分配。
     * 注意事项：方法性能敏感，需避免额外内存分配。
     */

    private static State getState() {
        State state = threadLocalState.get();
        if (state == null) {
            state = new State();
            threadLocalState.set(state);
        }
        return state;
    }
    /*
     * 中文注释：
     * 方法目的：获取或初始化线程局部的 State 实例。
     * 关键步骤：
     * 1. 从 ThreadLocal 获取 State，若为空则创建并存储。
     * 用途：确保每个线程拥有独立的 State 实例，管理非 Web 环境的环形缓冲区。
     * 交互逻辑：通过 ThreadLocal 实现线程隔离。
     */

    private static StringBuilder getSB() {
        return Constants.ENABLE_THREADLOCALS ? getState().getStringBuilder() : webSafeState.getStringBuilder();
    }
    /*
     * 中文注释：
     * 方法目的：根据环境选择合适的 StringBuilder 获取方式。
     * 关键步骤：
     * 1. 若启用 ThreadLocal（由 Constants.ENABLE_THREADLOCALS 控制），使用 State 获取 StringBuilder。
     * 2. 否则使用 WebSafeState 获取 StringBuilder，适用于 Web 环境。
     * 交互逻辑：动态选择线程安全或 Web 安全的缓冲区管理方式。
     * 注意事项：确保根据运行环境选择合适的实现，兼顾性能和内存安全。
     */

    /** For testing. */
    static int getRingbufferSize() {
        return RINGBUFFER_SIZE;
    }
    /*
     * 中文注释：
     * 方法目的：返回环形缓冲区大小，仅用于测试。
     * 用途：便于验证配置的缓冲区大小是否正确。
     */
}
