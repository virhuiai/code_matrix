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

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Helper class for closing JNDI resources.
 * JNDI资源关闭的辅助类。
 *
 * This class is separate from {@link Closer} because JNDI is not in Android.
 * 此类独立于 {@link Closer}，因为JNDI不在Android平台中。
 */
public final class JndiCloser {

    private JndiCloser() {
        // 私有构造函数，防止实例化
    }

    /**
     * Closes the specified {@code Context}.
     * 关闭指定的JNDI {@code Context}。
     *
     * @param context the JNDI Context to close, may be {@code null}
     * 要关闭的JNDI Context，可能为 {@code null}。
     * @throws NamingException if a problem occurred closing the specified JNDI Context
     * 如果关闭指定的JNDI Context时发生问题，则抛出此异常。
     * @see Context#close()
     * 参见 {@link Context#close()} 方法。
     */
    public static void close(final Context context) throws NamingException {
        // 检查Context是否为空，避免空指针异常
        if (context != null) {
            // 调用Context的close方法关闭资源
            context.close();
        }
    }

    /**
     * Closes the specified {@code Context}, ignoring any exceptions thrown by the close operation.
     * 关闭指定的JNDI {@code Context}，并忽略关闭操作可能抛出的任何异常。
     *
     * @param context the JNDI Context to close, may be {@code null}
     * 要关闭的JNDI Context，可能为 {@code null}。
     * @return Whether closing succeeded
     * 返回布尔值，表示关闭操作是否成功。
     * @see Context#close()
     * 参见 {@link Context#close()} 方法。
     */
    public static boolean closeSilently(final Context context) {
        try {
            // 尝试关闭Context
            close(context);
            // 如果成功关闭，返回true
            return true;
        } catch (final NamingException ignored) {
            // ignored
            // 捕获并忽略NamingException异常，不进行任何处理
            // 如果关闭失败（抛出异常），返回false
            return false;
        }
    }

}
