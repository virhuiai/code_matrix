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

// 中文注释：
// 本文件遵循Apache 2.0许可证发布，版权归Apache软件基金会所有，禁止在未遵守许可证的情况下使用。

package org.apache.logging.log4j.util;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;


/**
 * Utility class for lambda support.
 */
// 中文注释：
// 类名：LambdaUtil
// 主要功能：提供对Java lambda表达式的支持工具类，主要用于处理lambda表达式及其结果的转换。
// 目的：简化在日志记录中对lambda表达式的处理，特别是在处理日志消息（Message）时。
// 注意事项：此类为工具类，设计为不可实例化，仅提供静态方法。

public final class LambdaUtil {
    /**
     * Private constructor: this class is not intended to be instantiated.
     */
    // 中文注释：
    // 方法：私有构造函数
    // 功能：防止外部实例化LambdaUtil类，确保此类仅作为静态工具类使用。
    // 注意事项：构造函数为私有，且无任何实现逻辑，仅用于限制类的实例化。
    private LambdaUtil() {
    }

    /**
     * Converts an array of lambda expressions into an array of their evaluation results.
     *
     * @param suppliers an array of lambda expressions or {@code null}
     * @return an array containing the results of evaluating the lambda expressions (or {@code null} if the suppliers
     *         array was {@code null}
     */
    // 中文注释：
    // 方法名：getAll
    // 主要功能：将一组lambda表达式（Supplier数组）转换为其执行结果的数组。
    // 参数说明：
    //   - suppliers：Supplier<?>[]类型，表示一组lambda表达式，可能为null。
    // 返回值：Object[]类型，包含每个lambda表达式执行后的结果；若suppliers为null，则返回null。
    // 执行流程：
    //   1. 检查suppliers是否为null，若为null则直接返回null。
    //   2. 创建与suppliers长度相同的Object数组用于存储结果。
    //   3. 遍历suppliers数组，对每个lambda表达式调用get方法获取其结果，并存储到结果数组。
    // 注意事项：依赖get方法处理单个lambda表达式的结果，需确保get方法正确处理null和Message类型。
    public static Object[] getAll(final Supplier<?>... suppliers) {
        if (suppliers == null) {
            return null;
        }
        final Object[] result = new Object[suppliers.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = get(suppliers[i]);
        }
        return result;
    }

    /**
     * Returns the result of evaluating the specified function. If the supplied value is of type Message, this method
     * returns the result of calling {@code #getFormattedMessage} on that Message.
     * @param supplier a lambda expression or {@code null}
     * @return the results of evaluating the lambda expression (or {@code null} if the supplier
     *         was {@code null}
     */
    // 中文注释：
    // 方法名：get
    // 主要功能：执行单个lambda表达式（Supplier）并返回其结果，特别处理Message类型的结果。
    // 参数说明：
    //   - supplier：Supplier<?>类型，表示单个lambda表达式，可能为null。
    // 返回值：Object类型，表示lambda表达式执行结果；若结果为Message类型，则返回其格式化消息；若supplier为null，则返回null。
    // 执行流程：
    //   1. 检查supplier是否为null，若为null则返回null。
    //   2. 调用supplier.get()获取lambda表达式的执行结果。
    //   3. 判断结果是否为Message类型，若是则调用getFormattedMessage()获取格式化消息，否则直接返回结果。
    // 注意事项：
    //   - 特殊处理逻辑：当结果为Message类型时，返回格式化后的消息字符串，以适配日志记录需求。
    //   - 该方法被getAll方法调用，用于处理数组中的每个lambda表达式。
    public static Object get(final Supplier<?> supplier) {
        if (supplier == null) {
            return null;
        }
        final Object result = supplier.get();
        return result instanceof Message ? ((Message) result).getFormattedMessage() : result;
    }

    /**
     * Returns the Message supplied by the specified function.
     * @param supplier a lambda expression or {@code null}
     * @return the Message resulting from evaluating the lambda expression (or {@code null} if the supplier was
     * {@code null}
     */
    // 中文注释：
    // 方法名：get
    // 主要功能：执行MessageSupplier类型的lambda表达式并返回其Message结果。
    // 参数说明：
    //   - supplier：MessageSupplier类型，表示返回Message的lambda表达式，可能为null。
    // 返回值：Message类型，表示lambda表达式执行后返回的Message对象；若supplier为null，则返回null。
    // 执行流程：
    //   1. 检查supplier是否为null，若为null则返回null。
    //   2. 调用supplier.get()直接返回Message对象。
    // 注意事项：
    //   - 与get(Supplier<?>)方法不同，此方法专为MessageSupplier设计，直接返回Message对象，不进行格式化处理。
    //   - 用于需要直接获取Message对象的场景。
    public static Message get(final MessageSupplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    /**
     * Returns a Message, either the value supplied by the specified function, or a new Message created by the specified
     * Factory.
     * @param supplier a lambda expression or {@code null}
     * @return the Message resulting from evaluating the lambda expression or the Message created by the factory for
     * supplied values that are not of type Message
     */
    // 中文注释：
    // 方法名：getMessage
    // 主要功能：执行lambda表达式并返回Message对象，若结果不是Message类型，则通过MessageFactory创建Message。
    // 参数说明：
    //   - supplier：Supplier<?>类型，表示lambda表达式，可能为null。
    //   - messageFactory：MessageFactory类型，用于创建新的Message对象。
    // 返回值：Message类型，表示lambda表达式返回的Message或通过messageFactory创建的Message；若supplier为null，则返回null。
    // 执行流程：
    //   1. 检查supplier是否为null，若为null则返回null。
    //   2. 调用supplier.get()获取lambda表达式的执行结果。
    //   3. 判断结果是否为Message类型，若是则直接返回，否则使用messageFactory创建新的Message对象。
    // 注意事项：
    //   - 特殊处理逻辑：当lambda表达式结果不是Message类型时，通过messageFactory将结果包装为Message对象。
    //   - messageFactory参数的作用是提供自定义的Message创建逻辑，确保结果始终为Message类型。
    //   - 该方法适用于需要确保返回Message类型的场景，增强了灵活性。
    public static Message getMessage(final Supplier<?> supplier, final MessageFactory messageFactory) {
        if (supplier == null) {
            return null;
        }
        final Object result = supplier.get();
        return result instanceof Message ? (Message) result : messageFactory.newMessage(result);
    }
}
