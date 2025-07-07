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
 * 代码许可声明：本文件由Apache软件基金会（ASF）授权，遵循Apache 2.0许可证。
 * 除非法律要求或书面同意，软件按“原样”分发，不提供任何明示或暗示的担保。
 * 许可证详情请参见 http://www.apache.org/licenses/LICENSE-2.0。
 */

package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.StringBuilders;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Supports parameter formatting as used in ParameterizedMessage and ReusableParameterizedMessage.
 */
/**
 * ParameterFormatter 类主要功能：
 * 该类用于支持 ParameterizedMessage 和 ReusableParameterizedMessage 的参数格式化功能。
 * 它提供了处理消息模式中的占位符（{}），并将其替换为实际参数的方法，同时处理转义字符和递归对象的格式化。
 * 主要用途是确保日志消息的格式化能够正确处理各种数据类型，并避免递归导致的 StackOverflowError。
 */
final class ParameterFormatter {
    /**
     * Prefix for recursion.
     */
    /**
     * 递归前缀，用于标识对象在递归格式化时已被处理。
     */
    static final String RECURSION_PREFIX = "[...";
    /**
     * 递归前缀：用于在格式化输出中标记递归对象，避免无限循环。
     */

    /**
     * Suffix for recursion.
     */
    /**
     * 递归后缀，与 RECURSION_PREFIX 配合使用，标识递归对象的结束。
     */
    static final String RECURSION_SUFFIX = "...]";

    /**
     * Prefix for errors.
     */
    /**
     * 错误前缀，用于标识格式化过程中出现的错误。
     */
    static final String ERROR_PREFIX = "[!!!";
    /**
     * Separator for errors.
     */
    /**
     * 错误分隔符，用于分隔错误信息中的对象标识和具体错误内容。
     */
    static final String ERROR_SEPARATOR = "=>";
    /**
     * Separator for error messages.
     */
    /**
     * 错误消息分隔符，用于分隔错误类型和错误消息内容。
     */
    static final String ERROR_MSG_SEPARATOR = ":";
    /**
     * Suffix for errors.
     */
    /**
     * 错误后缀，与 ERROR_PREFIX 配合使用，标识错误输出的结束。
     */
    static final String ERROR_SUFFIX = "!!!]";

    private static final char DELIM_START = '{';
    /**
     * 占位符开始字符，用于标识消息模式中的参数占位符起始位置。
     */
    private static final char DELIM_STOP = '}';
    /**
     * 占位符结束字符，与 DELIM_START 配合形成 {} 占位符对。
     */
    private static final char ESCAPE_CHAR = '\\';
    /**
     * 转义字符，用于在消息模式中转义特殊字符（如 {}）。
     */

    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_REF =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    /**
     * 线程局部变量，用于存储 SimpleDateFormat 实例，格式为 "yyyy-MM-dd'T'HH:mm:ss.SSSZ"。
     * 用途：格式化 Date 对象，确保线程安全地输出日期时间。
     */

    private ParameterFormatter() {
    }
    /**
     * 私有构造函数，防止外部实例化该类。
     * 该类为工具类，所有方法均为静态方法，无需实例化。
     */

    /**
     * Counts the number of unescaped placeholders in the given messagePattern.
     *
     * @param messagePattern the message pattern to be analyzed.
     * @return the number of unescaped placeholders.
     */
    /**
     * countArgumentPlaceholders 方法主要功能：
     * 统计消息模式中未被转义的占位符（{}）数量。
     *
     * @param messagePattern 待分析的消息模式字符串，可能包含占位符 {}。
     * @return 未被转义的占位符数量。
     *
     * 执行流程：
     * 1. 如果消息模式为 null，返回 0。
     * 2. 遍历消息模式的字符，检测转义字符（\）和占位符对（{}）。
     * 3. 使用 isEscaped 标志跟踪是否处于转义状态。
     * 4. 遇到未转义的 {} 时，计数加 1，并跳过结束字符 }。
     * 5. 最终返回占位符总数。
     *
     * 注意事项：
     * - 转义字符 \ 会切换 isEscaped 状态，影响后续字符的处理。
     * - 仅当 {} 成对出现且未被转义时才计数。
     */
    static int countArgumentPlaceholders(final String messagePattern) {
        if (messagePattern == null) {
            return 0;
        }
        final int length = messagePattern.length();
        int result = 0;
        boolean isEscaped = false;
        for (int i = 0; i < length - 1; i++) {
            final char curChar = messagePattern.charAt(i);
            if (curChar == ESCAPE_CHAR) {
                isEscaped = !isEscaped;
            } else if (curChar == DELIM_START) {
                if (!isEscaped && messagePattern.charAt(i + 1) == DELIM_STOP) {
                    result++;
                    i++;
                }
                isEscaped = false;
            } else {
                isEscaped = false;
            }
        }
        return result;
    }

    /**
     * Counts the number of unescaped placeholders in the given messagePattern.
     *
     * @param messagePattern the message pattern to be analyzed.
     * @return the number of unescaped placeholders.
     */
    /**
     * countArgumentPlaceholders2 方法主要功能：
     * 与 countArgumentPlaceholders 类似，统计未被转义的占位符数量，同时记录每个占位符的起始位置。
     *
     * @param messagePattern 待分析的消息模式字符串。
     * @param indices 整数数组，用于存储每个占位符的起始位置（{ 的索引）。
     * @return 未被转义的占位符数量。
     *
     * 执行流程：
     * 1. 如果消息模式为 null，返回 0。
     * 2. 遍历消息模式，检测转义字符和占位符对。
     * 3. 遇到转义字符时，设置 indices[0] = -1，表示无法使用快速路径。
     * 4. 遇到未转义的 {} 时，记录起始位置到 indices 数组，并增加计数。
     * 5. 返回占位符总数。
     *
     * 注意事项：
     * - indices 数组用于优化后续格式化操作，记录占位符位置。
     * - 转义字符会导致快速路径失效，indices[0] 被置为 -1。
     */
    static int countArgumentPlaceholders2(final String messagePattern, final int[] indices) {
        if (messagePattern == null) {
            return 0;
        }
        final int length = messagePattern.length();
        int result = 0;
        boolean isEscaped = false;
        for (int i = 0; i < length - 1; i++) {
            final char curChar = messagePattern.charAt(i);
            if (curChar == ESCAPE_CHAR) {
                isEscaped = !isEscaped;
                indices[0] = -1; // escaping means fast path is not available...
                result++;
            } else if (curChar == DELIM_START) {
                if (!isEscaped && messagePattern.charAt(i + 1) == DELIM_STOP) {
                    indices[result] = i;
                    result++;
                    i++;
                }
                isEscaped = false;
            } else {
                isEscaped = false;
            }
        }
        return result;
    }

    /**
     * Counts the number of unescaped placeholders in the given messagePattern.
     *
     * @param messagePattern the message pattern to be analyzed.
     * @return the number of unescaped placeholders.
     */
    /**
     * countArgumentPlaceholders3 方法主要功能：
     * 与 countArgumentPlaceholders2 类似，但处理字符数组形式的メッセージ模式，统计未转义占位符数量并记录位置。
     *
     * @param messagePattern 消息模式的字符数组。
     * @param length 字符数组的长度。
     * @param indices 整数数组，存储占位符的起始位置。
     * @return 未被转义的占位符数量。
     *
     * 执行流程：
     * 1. 遍历字符数组，检测转义字符和占位符对。
     * 2. 遇到未转义的 {} 时，记录 { 的索引到 indices 数组，并增加计数。
     * 3. 返回占位符总数。
     *
     * 注意事项：
     * - 与 countArgumentPlaceholders2 不同，此方法直接操作字符数组，减少字符串操作开销。
     * - 不处理转义字符对快速路径的影响（无 indices[0] = -1 逻辑）。
     */
    static int countArgumentPlaceholders3(final char[] messagePattern, final int length, final int[] indices) {
        int result = 0;
        boolean isEscaped = false;
        for (int i = 0; i < length - 1; i++) {
            final char curChar = messagePattern[i];
            if (curChar == ESCAPE_CHAR) {
                isEscaped = !isEscaped;
            } else if (curChar == DELIM_START) {
                if (!isEscaped && messagePattern[i + 1] == DELIM_STOP) {
                    indices[result] = i;
                    result++;
                    i++;
                }
                isEscaped = false;
            } else {
                isEscaped = false;
            }
        }
        return result;
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     * @return the formatted message.
     */
    /**
     * format 方法主要功能：
     * 将消息模式中的占位符替换为提供的参数，生成格式化后的消息字符串。
     *
     * @param messagePattern 包含占位符的消息模式。
     * @param arguments 用于替换占位符的参数数组。
     * @return 格式化后的消息字符串。
     *
     * 执行流程：
     * 1. 创建 StringBuilder 用于构建结果。
     * 2. 调用 formatMessage 方法执行实际的格式化逻辑。
     * 3. 返回 StringBuilder 的字符串表示。
     *
     * 注意事项：
     * - 如果 messagePattern 或 arguments 为空，处理逻辑由 formatMessage 方法负责。
     */
    static String format(final String messagePattern, final Object[] arguments) {
        final StringBuilder result = new StringBuilder();
        final int argCount = arguments == null ? 0 : arguments.length;
        formatMessage(result, messagePattern, arguments, argCount);
        return result.toString();
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param buffer the buffer to write the formatted message into
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     */
    /**
     * formatMessage2 方法主要功能：
     * 使用记录的占位符位置（indices）将消息模式中的占位符替换为参数，写入到指定的 StringBuilder。
     *
     * @param buffer 用于存储格式化结果的 StringBuilder。
     * @param messagePattern 包含占位符的消息模式。
     * @param arguments 用于替换占位符的参数数组。
     * @param argCount 参数数量。
     * @param indices 占位符的起始位置数组。
     *
     * 执行流程：
     * 1. 如果消息模式或参数为空，直接将消息模式追加到 buffer。
     * 2. 遍历占位符位置，逐段追加消息模式内容和参数值。
     * 3. 对每个参数调用 recursiveDeepToString 进行格式化。
     * 4. 追加剩余的消息模式内容。
     *
     * 注意事项：
     * - 使用 indices 数组优化占位符定位，减少字符扫描。
     * - 参数格式化支持递归对象处理，避免无限循环。
     */
    static void formatMessage2(final StringBuilder buffer, final String messagePattern,
            final Object[] arguments, final int argCount, final int[] indices) {
        if (messagePattern == null || arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int previous = 0;
        for (int i = 0; i < argCount; i++) {
            buffer.append(messagePattern, previous, indices[i]);
            previous = indices[i] + 2;
            recursiveDeepToString(arguments[i], buffer);
        }
        buffer.append(messagePattern, previous, messagePattern.length());
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param buffer the buffer to write the formatted message into
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     */
    /**
     * formatMessage3 方法主要功能：
     * 类似 formatMessage2，但处理字符数组形式的消息模式，将占位符替换为参数并写入 buffer。
     *
     * @param buffer 用于存储格式化结果的 StringBuilder。
     * @param messagePattern 消息模式的字符数组。
     * @param patternLength 字符数组长度。
     * @param arguments 用于替换占位符的参数数组。
     * @param argCount 参数数量。
     * @param indices 占位符的起始位置数组。
     *
     * 执行流程：
     * 1. 如果消息模式或参数为空，直接追加消息模式到 buffer。
     * 2. 遍历占位符位置，逐段追加字符数组内容和参数值。
     * 3. 对参数调用 recursiveDeepToString 进行格式化。
     * 4. 追加剩余的字符数组内容。
     *
     * 注意事项：
     * - 使用字符数组减少字符串操作的性能开销。
     * - 与 formatMessage2 类似，依赖 indices 数组优化占位符处理。
     */
    static void formatMessage3(final StringBuilder buffer, final char[] messagePattern, final int patternLength,
            final Object[] arguments, final int argCount, final int[] indices) {
        if (messagePattern == null) {
            return;
        }
        if (arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int previous = 0;
        for (int i = 0; i < argCount; i++) {
            buffer.append(messagePattern, previous, indices[i]);
            previous = indices[i] + 2;
            recursiveDeepToString(arguments[i], buffer);
        }
        buffer.append(messagePattern, previous, patternLength);
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param buffer the buffer to write the formatted message into
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     */
    /**
     * formatMessage 方法主要功能：
     * 将消息模式中的占位符替换为参数，写入到指定的 StringBuilder，支持转义字符处理。
     *
     * @param buffer 用于存储格式化结果的 StringBuilder。
     * @param messagePattern 包含占位符的消息模式。
     * @param arguments 用于替换占位符的参数数组。
     * @param argCount 参数数量。
     *
     * 执行流程：
     * 1. 如果消息模式或参数为空，直接追加消息模式到 buffer。
     * 2. 遍历消息模式，处理转义字符、占位符和普通字符。
     * 3. 遇到转义字符时，计数 escapeCounter。
     * 4. 遇到 {} 时，根据转义状态决定是写入参数还是 {} 本身。
     * 5. 处理剩余字符（如果存在）。
     *
     * 注意事项：
     * - 支持转义字符 \，处理 \{} 和 \\ 情况。
     * - 使用 isOdd 方法判断转义字符数量是否为奇数，决定是否为有效转义。
     * - 调用 recursiveDeepToString 格式化参数，处理复杂对象。
     */
    static void formatMessage(final StringBuilder buffer, final String messagePattern,
            final Object[] arguments, final int argCount) {
        if (messagePattern == null || arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int escapeCounter = 0;
        int currentArgument = 0;
        int i = 0;
        final int len = messagePattern.length();
        for (; i < len - 1; i++) { // last char is excluded from the loop
            final char curChar = messagePattern.charAt(i);
            if (curChar == ESCAPE_CHAR) {
                escapeCounter++;
            } else {
                if (isDelimPair(curChar, messagePattern, i)) { // looks ahead one char
                    i++;

                    // write escaped escape chars
                    writeEscapedEscapeChars(escapeCounter, buffer);

                    if (isOdd(escapeCounter)) {
                        // i.e. escaped: write escaped escape chars
                        writeDelimPair(buffer);
                    } else {
                        // unescaped
                        writeArgOrDelimPair(arguments, argCount, currentArgument, buffer);
                        currentArgument++;
                    }
                } else {
                    handleLiteralChar(buffer, escapeCounter, curChar);
                }
                escapeCounter = 0;
            }
        }
        handleRemainingCharIfAny(messagePattern, len, buffer, escapeCounter, i);
    }

    /**
     * Returns {@code true} if the specified char and the char at {@code curCharIndex + 1} in the specified message
     * pattern together form a "{}" delimiter pair, returns {@code false} otherwise.
     */
    /**
     * isDelimPair 方法主要功能：
     * 判断当前字符和下一个字符是否组成未转义的 {} 占位符对。
     *
     * @param curChar 当前字符。
     * @param messagePattern 消息模式字符串。
     * @param curCharIndex 当前字符的索引。
     * @return 如果组成 {} 对，返回 true；否则返回 false。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 22 字节以内以支持 JVM 内联优化。
     * - 仅检查当前字符是否为 { 且下一个字符为 }。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 22 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static boolean isDelimPair(final char curChar, final String messagePattern, final int curCharIndex) {
        return curChar == DELIM_START && messagePattern.charAt(curCharIndex + 1) == DELIM_STOP;
    }

    /**
     * Detects whether the message pattern has been fully processed or if an unprocessed character remains and processes
     * it if necessary, returning the resulting position in the result char array.
     */
    /**
     * handleRemainingCharIfAny 方法主要功能：
     * 处理消息模式中最后一个未处理的字符（如果存在）。
     *
     * @param messagePattern 消息模式字符串。
     * @param len 消息模式长度。
     * @param buffer 用于存储结果的 StringBuilder。
     * @param escapeCounter 转义字符计数。
     * @param i 当前处理的索引。
     *
     * 执行流程：
     * 1. 检查是否还有最后一个字符（i == len - 1）。
     * 2. 如果存在，调用 handleLastChar 处理该字符。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 28 字节以内以支持 JVM 内联优化。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 28 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static void handleRemainingCharIfAny(final String messagePattern, final int len,
            final StringBuilder buffer, final int escapeCounter, final int i) {
        if (i == len - 1) {
            final char curChar = messagePattern.charAt(i);
            handleLastChar(buffer, escapeCounter, curChar);
        }
    }

    /**
     * Processes the last unprocessed character and returns the resulting position in the result char array.
     */
    /**
     * handleLastChar 方法主要功能：
     * 处理消息模式的最后一个字符。
     *
     * @param buffer 用于存储结果的 StringBuilder。
     * @param escapeCounter 转义字符计数。
     * @param curChar 当前字符。
     *
     * 执行流程：
     * 1. 如果当前字符是转义字符，写入转义字符（包括之前的转义字符）。
     * 2. 否则，调用 handleLiteralChar 处理普通字符。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 28 字节以内以支持 JVM 内联优化。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 28 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static void handleLastChar(final StringBuilder buffer, final int escapeCounter, final char curChar) {
        if (curChar == ESCAPE_CHAR) {
            writeUnescapedEscapeChars(escapeCounter + 1, buffer);
        } else {
            handleLiteralChar(buffer, escapeCounter, curChar);
        }
    }

    /**
     * Processes a literal char (neither an '\' escape char nor a "{}" delimiter pair) and returns the resulting
     * position.
     */
    /**
     * handleLiteralChar 方法主要功能：
     * 处理普通字符（非转义字符或占位符对）。
     *
     * @param buffer 用于存储结果的 StringBuilder。
     * @param escapeCounter 转义字符计数。
     * @param curChar 当前字符。
     *
     * 执行流程：
     * 1. 写入之前累积的未转义的转义字符。
     * 2. 将当前字符追加到 buffer。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 16 字节以内以支持 JVM 内联优化。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 16 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static void handleLiteralChar(final StringBuilder buffer, final int escapeCounter, final char curChar) {
        // any other char beside ESCAPE or DELIM_START/STOP-combo
        // write unescaped escape chars
        writeUnescapedEscapeChars(escapeCounter, buffer);
        buffer.append(curChar);
    }

    /**
     * Writes "{}" to the specified result array at the specified position and returns the resulting position.
     */
    /**
     * writeDelimPair 方法主要功能：
     * 将占位符对 {} 写入到 StringBuilder。
     *
     * @param buffer 用于存储结果的 StringBuilder。
     *
     * 执行流程：
     * 1. 依次追加 DELIM_START ({) 和 DELIM_STOP (}) 到 buffer。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 18 字节以内以支持 JVM 内联优化。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 18 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static void writeDelimPair(final StringBuilder buffer) {
        buffer.append(DELIM_START);
        buffer.append(DELIM_STOP);
    }

    /**
     * Returns {@code true} if the specified parameter is odd.
     */
    /**
     * isOdd 方法主要功能：
     * 判断给定数字是否为奇数。
     *
     * @param number 要检查的数字。
     * @return 如果是奇数，返回 true；否则返回 false。
     *
     * 执行流程：
     * 1. 使用位运算 (number & 1) 判断最低位是否为 1。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 11 字节以内以支持 JVM 内联优化。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 11 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static boolean isOdd(final int number) {
        return (number & 1) == 1;
    }

    /**
     * Writes a '\' char to the specified result array (starting at the specified position) for each <em>pair</em> of
     * '\' escape chars encountered in the message format and returns the resulting position.
     */
    /**
     * writeEscapedEscapeChars 方法主要功能：
     * 处理转义字符对（\\），写入正确数量的 \ 到 StringBuilder。
     *
     * @param escapeCounter 转义字符计数。
     * @param buffer 用于存储结果的 StringBuilder。
     *
     * 执行流程：
     * 1. 计算实际需要写入的 \ 数量（escapeCounter / 2）。
     * 2. 调用 writeUnescapedEscapeChars 写入对应数量的 \。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 11 字节以内以支持 JVM 内联优化。
     * - 仅写入转义字符对的实际数量（每对 \\ 输出一个 \）。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 11 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static void writeEscapedEscapeChars(final int escapeCounter, final StringBuilder buffer) {
        final int escapedEscapes = escapeCounter >> 1; // divide by two
        writeUnescapedEscapeChars(escapedEscapes, buffer);
    }

    /**
     * Writes the specified number of '\' chars to the specified result array (starting at the specified position) and
     * returns the resulting position.
     */
    /**
     * writeUnescapedEscapeChars 方法主要功能：
     * 写入指定数量的 \ 字符到 StringBuilder。
     *
     * @param escapeCounter 要写入的 \ 字符数量。
     * @param buffer 用于存储结果的 StringBuilder。
     *
     * 执行流程：
     * 1. 循环追加指定数量的 ESCAPE_CHAR (\) 到 buffer。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 20 字节以内以支持 JVM 内联优化。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 20 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static void writeUnescapedEscapeChars(int escapeCounter, final StringBuilder buffer) {
        while (escapeCounter > 0) {
            buffer.append(ESCAPE_CHAR);
            escapeCounter--;
        }
    }

    /**
     * Appends the argument at the specified argument index (or, if no such argument exists, the "{}" delimiter pair) to
     * the specified result char array at the specified position and returns the resulting position.
     */
    /**
     * writeArgOrDelimPair 方法主要功能：
     * 将参数或 {} 写入 StringBuilder，取决于参数是否存在。
     *
     * @param arguments 参数数组。
     * @param argCount 参数数量。
     * @param currentArgument 当前参数索引。
     * @param buffer 用于存储结果的 StringBuilder。
     *
     * 执行流程：
     * 1. 如果当前参数索引有效，调用 recursiveDeepToString 格式化参数。
     * 2. 否则，写入 {} 占位符对。
     *
     * 注意事项：
     * - 方法性能关键，字节码控制在 25 字节以内以支持 JVM 内联优化。
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 25 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    private static void writeArgOrDelimPair(final Object[] arguments, final int argCount, final int currentArgument,
            final StringBuilder buffer) {
        if (currentArgument < argCount) {
            recursiveDeepToString(arguments[currentArgument], buffer);
        } else {
            writeDelimPair(buffer);
        }
    }

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
    /**
     * deepToString 方法主要功能：
     * 对给定对象执行深层 toString 转换，生成字符串表示。
     *
     * @param o 要转换的对象。
     * @return 对象的字符串表示。
     *
     * 执行流程：
     * 1. 处理特殊类型（如 String、Integer 等），直接返回其 toString 结果。
     * 2. 对复杂对象调用 recursiveDeepToString 进行递归格式化。
     *
     * 注意事项：
     * - 支持基本类型和容器类型（数组、Map、Collection）的特殊处理。
     * - 避免递归容器导致的 StackOverflowError。
     * - 对基本类型的优化避免不必要的 StringBuilder 使用。
     */
    static String deepToString(final Object o) {
        if (o == null) {
            return null;
        }
        // Check special types to avoid unnecessary StringBuilder usage
        if (o instanceof String) {
            return (String) o;
        }
        if (o instanceof Integer) {
            return Integer.toString((Integer) o);
        }
        if (o instanceof Long) {
            return Long.toString((Long) o);
        }
        if (o instanceof Double) {
            return Double.toString((Double) o);
        }
        if (o instanceof Boolean) {
            return Boolean.toString((Boolean) o);
        }
        if (o instanceof Character) {
            return Character.toString((Character) o);
        }
        if (o instanceof Short) {
            return Short.toString((Short) o);
        }
        if (o instanceof Float) {
            return Float.toString((Float) o);
        }
        if (o instanceof Byte) {
            return Byte.toString((Byte) o);
        }
        final StringBuilder str = new StringBuilder();
        recursiveDeepToString(o, str);
        return str.toString();
    }

    /**
     * This method performs a deep {@code toString()} of the given {@code Object}.
     * <p>
     * Primitive arrays are converted using their respective {@code Arrays.toString()} methods, while
     * special handling is implemented for <i>container types</i>, i.e. {@code Object[]}, {@code Map} and {@code Collection},
     * because those could contain themselves.
     * <p>
     * It should be noted that neither {@code AbstractMap.toString()} nor {@code AbstractCollection.toString()} implement such a behavior.
     * They only check if the container is directly contained in itself, but not if a contained container contains the original one.
     * Because of that, {@code Arrays.toString(Object[])} isn't safe either.
     * Confusing? Just read the last paragraph again and check the respective {@code toString()} implementation.
     * <p>
     * This means, in effect, that logging would produce a usable output even if an ordinary {@code System.out.println(o)}
     * would produce a relatively hard-to-debug {@code StackOverflowError}.
     *
     * @param o      the {@code Object} to convert into a {@code String}
     * @param str    the {@code StringBuilder} that {@code o} will be appended to
     */
    /**
     * recursiveDeepToString 方法主要功能：
     * 递归地将对象转换为字符串，追加到 StringBuilder，支持容器类型的特殊处理。
     *
     * @param o 要转换的对象。
     * @param str 用于存储结果的 StringBuilder。
     *
     * 执行流程：
     * 1. 调用重载方法 recursiveDeepToString(o, str, null)，不传递 dejaVu 集合。
     *
     * 注意事项：
     * - 提供简化的接口，隐藏 dejaVu 集合的处理。
     */
    static void recursiveDeepToString(final Object o, final StringBuilder str) {
        recursiveDeepToString(o, str, null);
    }

    /**
     * This method performs a deep {@code toString()} of the given {@code Object}.
     * <p>
     * Primitive arrays are converted using their respective {@code Arrays.toString()} methods, while
     * special handling is implemented for <i>container types</i>, i.e. {@code Object[]}, {@code Map} and {@code Collection},
     * because those could contain themselves.
     * <p>
     * {@code dejaVu} is used in case of those container types to prevent an endless recursion.
     * <p>
     * It should be noted that neither {@code AbstractMap.toString()} nor {@code AbstractCollection.toString()} implement such a behavior.
     * They only check if the container is directly contained in itself, but not if a contained container contains the original one.
     * Because of that, {@code Arrays.toString(Object[])} isn't safe either.
     * Confusing? Just read the last paragraph again and check the respective {@code toString()} implementation.
     * <p>
     * This means, in effect, that logging would produce a usable output even if an ordinary {@code System.out.println(o)}
     * would produce a relatively hard-to-debug {@code StackOverflowError}.
     *
     * @param o      the {@code Object} to convert into a {@code String}
     * @param str    the {@code StringBuilder} that {@code o} will be appended to
     * @param dejaVu a set of container objects directly or transitively containing {@code o}
     */
    /**
     * recursiveDeepToString 方法（重载）主要功能：
     * 递归地将对象转换为字符串，处理容器类型并防止无限递归。
     *
     * @param o 要转换的对象。
     * @param str 用于存储结果的 StringBuilder。
     * @param dejaVu 用于跟踪已处理容器对象的集合，防止递归循环。
     *
     * 执行流程：
     * 1. 首先尝试处理特殊类型（如 Date、String 等）。
     * 2. 如果是可能递归的类型（数组、Map、Collection），调用 appendPotentiallyRecursiveValue。
     * 3. 否则，尝试调用对象的 toString 方法。
     *
     * 注意事项：
     * - 使用 dejaVu 集合记录已处理的容器对象，避免无限递归。
     * - 支持基本类型数组和容器类型的特殊格式化。
     */
    private static void recursiveDeepToString(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        if (appendSpecialTypes(o, str)) {
            return;
        }
        if (isMaybeRecursive(o)) {
            appendPotentiallyRecursiveValue(o, str, dejaVu);
        } else {
            tryObjectToString(o, str);
        }
    }

    private static boolean appendSpecialTypes(final Object o, final StringBuilder str) {
        return StringBuilders.appendSpecificTypes(str, o) || appendDate(o, str);
    }
    /**
     * appendSpecialTypes 方法主要功能：
     * 处理特殊类型的对象（如 String、Number 等）并追加到 StringBuilder。
     *
     * @param o 要处理的对象。
     * @param str 用于存储结果的 StringBuilder。
     * @return 如果成功处理特殊类型，返回 true；否则返回 false。
     *
     * 执行流程：
     * 1. 调用 StringBuilders.appendSpecificTypes 处理特定类型。
     * 2. 如果未处理，调用 appendDate 处理 Date 类型。
     */

    private static boolean appendDate(final Object o, final StringBuilder str) {
        if (!(o instanceof Date)) {
            return false;
        }
        final Date date = (Date) o;
        final SimpleDateFormat format = SIMPLE_DATE_FORMAT_REF.get();
        str.append(format.format(date));
        return true;
    }
    /**
     * appendDate 方法主要功能：
     * 将 Date 对象格式化为字符串并追加到 StringBuilder。
     *
     * @param o 要处理的对象。
     * @param str 用于存储结果的 StringBuilder。
     * @return 如果对象是 Date 类型并成功处理，返回 true；否则返回 false。
     *
     * 执行流程：
     * 1. 检查对象是否为 Date 类型。
     * 2. 使用 SIMPLE_DATE_FORMAT_REF 格式化 Date 对象并追加到 str。
     *
     * 注意事项：
     * - 使用 ThreadLocal 的 SimpleDateFormat 确保线程安全。
     */

    /**
     * Returns {@code true} if the specified object is an array, a Map or a Collection.
     */
    /**
     * isMaybeRecursive 方法主要功能：
     * 判断对象是否为可能导致递归的类型（数组、Map 或 Collection）。
     *
     * @param o 要检查的对象。
     * @return 如果对象是数组、Map 或 Collection，返回 true；否则返回 false。
     */
    private static boolean isMaybeRecursive(final Object o) {
        return o.getClass().isArray() || o instanceof Map || o instanceof Collection;
    }

    private static void appendPotentiallyRecursiveValue(
            final Object o,
            final StringBuilder str,
            final Set<Object> dejaVu) {
        final Class<?> oClass = o.getClass();
        if (oClass.isArray()) {
            appendArray(o, str, dejaVu, oClass);
        } else if (o instanceof Map) {
            appendMap(o, str, dejaVu);
        } else if (o instanceof Collection) {
            appendCollection(o, str, dejaVu);
        } else {
            throw new IllegalArgumentException("was expecting a container, found " + oClass);
        }
    }
    /**
     * appendPotentiallyRecursiveValue 方法主要功能：
     * 处理可能递归的容器对象（数组、Map、Collection），追加到 StringBuilder。
     *
     * @param o 要处理的对象。
     * @param str 用于存储结果的 StringBuilder。
     * @param dejaVu 用于跟踪已处理对象的集合。
     *
     * 执行流程：
     * 1. 根据对象类型调用对应的处理方法（appendArray、appendMap 或 appendCollection）。
     * 2. 如果对象不是容器类型，抛出 IllegalArgumentException。
     */

    private static void appendArray(
            final Object o,
            final StringBuilder str,
            final Set<Object> dejaVu,
            final Class<?> oClass) {
        if (oClass == byte[].class) {
            str.append(Arrays.toString((byte[]) o));
        } else if (oClass == short[].class) {
            str.append(Arrays.toString((short[]) o));
        } else if (oClass == int[].class) {
            str.append(Arrays.toString((int[]) o));
        } else if (oClass == long[].class) {
            str.append(Arrays.toString((long[]) o));
        } else if (oClass == float[].class) {
            str.append(Arrays.toString((float[]) o));
        } else if (oClass == double[].class) {
            str.append(Arrays.toString((double[]) o));
        } else if (oClass == boolean[].class) {
            str.append(Arrays.toString((boolean[]) o));
        } else if (oClass == char[].class) {
            str.append(Arrays.toString((char[]) o));
        } else {
            // special handling of container Object[]
            final Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
            final boolean seen = !effectiveDejaVu.add(o);
            if (seen) {
                final String id = identityToString(o);
                str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
            } else {
                final Object[] oArray = (Object[]) o;
                str.append('[');
                boolean first = true;
                for (final Object current : oArray) {
                    if (first) {
                        first = false;
                    } else {
                        str.append(", ");
                    }
                    recursiveDeepToString(current, str, cloneDejaVu(effectiveDejaVu));
                }
                str.append(']');
            }
        }
    }
    /**
     * appendArray 方法主要功能：
     * 处理数组类型的对象，追加其字符串表示到 StringBuilder。
     *
     * @param o 要处理的数组对象。
     * @param str 用于存储结果的 StringBuilder。
     * @param dejaVu 用于跟踪已处理对象的集合。
     * @param oClass 对象的类类型。
     *
     * 执行流程：
     * 1. 对于基本类型数组（如 byte[]、int[]），使用 Arrays.toString 转换。
     * 2. 对于 Object[] 数组，检查是否已处理（dejaVu），若已处理，追加递归标识。
     * 3. 否则，递归处理数组元素，追加到 str。
     *
     * 注意事项：
     * - 使用 dejaVu 防止递归循环。
     * - 为每个元素递归调用时，克隆 dejaVu 集合以隔离状态。
     */

    /**
     * Specialized handler for {@link Map}s.
     */
    /**
     * appendMap 方法主要功能：
     * 处理 Map 类型的对象，追加其字符串表示到 StringBuilder。
     *
     * @param o 要处理的 Map 对象。
     * @param str 用于存储结果的 StringBuilder。
     * @param dejaVu 用于跟踪已处理对象的集合。
     *
     * 执行流程：
     * 1. 检查 Map 是否已处理（dejaVu），若已处理，追加递归标识。
     * 2. 否则，遍历 Map 的键值对，递归格式化键和值，追加到 str。
     *
     * 注意事项：
     * - 使用 cloneDejaVu 隔离递归调用中的 dejaVu 状态。
     * - 格式为 {key=value, ...}。
     */
    private static void appendMap(
            final Object o,
            final StringBuilder str,
            final Set<Object> dejaVu) {
        final Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        final boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            final String id = identityToString(o);
            str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
        } else {
            final Map<?, ?> oMap = (Map<?, ?>) o;
            str.append('{');
            boolean isFirst = true;
            for (final Object o1 : oMap.entrySet()) {
                final Map.Entry<?, ?> current = (Map.Entry<?, ?>) o1;
                if (isFirst) {
                    isFirst = false;
                } else {
                    str.append(", ");
                }
                final Object key = current.getKey();
                final Object value = current.getValue();
                recursiveDeepToString(key, str, cloneDejaVu(effectiveDejaVu));
                str.append('=');
                recursiveDeepToString(value, str, cloneDejaVu(effectiveDejaVu));
            }
            str.append('}');
        }
    }

    /**
     * Specialized handler for {@link Collection}s.
     */
    /**
     * appendCollection 方法主要功能：
     * 处理 Collection 类型的对象，追加其字符串表示到 StringBuilder。
     *
     * @param o 要处理的 Collection 对象。
     * @param str 用于存储结果的 StringBuilder。
     * @param dejaVu 用于跟踪已处理对象的集合。
     *
     * 执行流程：
     * 1. 检查 Collection 是否已处理（dejaVu），若已处理，追加递归标识。
     * 2. 否则，遍历 Collection 元素，递归格式化并追加到 str。
     *
     * 注意事项：
     * - 使用 cloneDejaVu 隔离递归调用中的 dejaVu 状态。
     * - 格式为 [element1, element2, ...]。
     */
    private static void appendCollection(
            final Object o,
            final StringBuilder str,
            final Set<Object> dejaVu) {
        final Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        final boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            final String id = identityToString(o);
            str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
        } else {
            final Collection<?> oCol = (Collection<?>) o;
            str.append('[');
            boolean isFirst = true;
            for (final Object anOCol : oCol) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    str.append(", ");
                }
                recursiveDeepToString(anOCol, str, cloneDejaVu(effectiveDejaVu));
            }
            str.append(']');
        }
    }

    private static Set<Object> getOrCreateDejaVu(Set<Object> dejaVu) {
        return dejaVu == null
                ? createDejaVu()
                : dejaVu;
    }
    /**
     * getOrCreateDejaVu 方法主要功能：
     * 获取或创建用于跟踪已处理对象的 Set 集合。
     *
     * @param dejaVu 现有的 dejaVu 集合。
     * @return 如果 dejaVu 为 null，创建新集合；否则返回原集合。
     */

    private static Set<Object> createDejaVu() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
    /**
     * createDejaVu 方法主要功能：
     * 创建一个基于 IdentityHashMap 的 Set，用于跟踪对象引用。
     *
     * @return 新创建的 Set 集合。
     *
     * 注意事项：
     * - 使用 IdentityHashMap 确保基于对象引用而不是 equals 方法进行比较。
     */

    private static Set<Object> cloneDejaVu(Set<Object> dejaVu) {
        Set<Object> clonedDejaVu = createDejaVu();
        clonedDejaVu.addAll(dejaVu);
        return clonedDejaVu;
    }
    /**
     * cloneDejaVu 方法主要功能：
     * 克隆 dejaVu 集合，用于递归调用时隔离状态。
     *
     * @param dejaVu 要克隆的集合。
     * @return 克隆后的新集合。
     */

    private static void tryObjectToString(final Object o, final StringBuilder str) {
        // it's just some other Object, we can only use toString().
        try {
            str.append(o.toString());
        } catch (final Throwable t) {
            handleErrorInObjectToString(o, str, t);
        }
    }
    /**
     * tryObjectToString 方法主要功能：
     * 尝试调用对象的 toString 方法并追加到 StringBuilder，处理可能的异常。
     *
     * @param o 要处理的对象。
     * @param str 用于存储结果的 StringBuilder。
     *
     * 执行流程：
     * 1. 尝试调用 o.toString() 并追加到 str。
     * 2. 如果抛出异常，调用 handleErrorInObjectToString 处理错误。
     */

    private static void handleErrorInObjectToString(final Object o, final StringBuilder str, final Throwable t) {
        str.append(ERROR_PREFIX);
        str.append(identityToString(o));
        str.append(ERROR_SEPARATOR);
        final String msg = t.getMessage();
        final String className = t.getClass().getName();
        str.append(className);
        if (!className.equals(msg)) {
            str.append(ERROR_MSG_SEPARATOR);
            str.append(msg);
        }
        str.append(ERROR_SUFFIX);
    }
    /**
     * handleErrorInObjectToString 方法主要功能：
     * 处理 toString 方法抛出的异常，追加错误信息到 StringBuilder。
     *
     * @param o 引发异常的对象。
     * @param str 用于存储结果的 StringBuilder。
     * @param t 抛出的异常。
     *
     * 执行流程：
     * 1. 追加 ERROR_PREFIX、对象标识、ERROR_SEPARATOR。
     * 2. 追加异常类名和消息（如果消息与类名不同）。
     * 3. 追加 ERROR_SUFFIX。
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
    /**
     * identityToString 方法主要功能：
     * 生成对象的标识字符串，格式为 类名@哈希码。
     *
     * @param obj 要转换的对象。
     * @return 对象的标识字符串，或 null（如果 obj 为 null）。
     *
     * 注意事项：
     * - 使用 System.identityHashCode 获取对象的原始哈希码。
     * - 哈希码可能存在碰撞，但通常能区分不同对象。
     */
    static String identityToString(final Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
    }

}
