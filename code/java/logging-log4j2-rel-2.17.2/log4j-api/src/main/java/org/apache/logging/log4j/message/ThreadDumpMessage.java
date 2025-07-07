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
// 本文件遵循Apache 2.0许可证，属于Apache Software Foundation (ASF)授权的开源代码。
// 该许可证规定了代码的使用、复制和分发的权限和限制，具体条款见上述链接。

package org.apache.logging.log4j.message;

import static org.apache.logging.log4j.util.Chars.LF;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.Strings;

/**
 * Captures information about all running Threads.
 */
// 中文注释：
// ThreadDumpMessage类用于捕获和格式化所有运行线程的信息，主要用于日志记录。
// 主要功能：
// - 收集当前JVM中所有线程的堆栈跟踪信息。
// - 提供格式化的线程信息输出，便于调试和日志记录。
// - 支持序列化，通过代理模式(ThreadDumpMessageProxy)实现可序列化的线程信息。
// - 使用工厂模式(ThreadInfoFactory)动态加载线程信息生成器。
// 应用场景：常用于诊断系统性能瓶颈或死锁问题，通过日志输出线程状态。

@AsynchronouslyFormattable
public class ThreadDumpMessage implements Message, StringBuilderFormattable {
    private static final long serialVersionUID = -1103400781608841088L;
    private static ThreadInfoFactory FACTORY;

    private volatile Map<ThreadInformation, StackTraceElement[]> threads;
    private final String title;
    private String formattedMessage;

    // 中文注释：
    // 关键变量说明：
    // - serialVersionUID: 用于序列化版本控制，确保序列化兼容性。
    // - FACTORY: 静态变量，指向ThreadInfoFactory实例，用于生成线程信息。
    // - threads: 存储线程信息(ThreadInformation)和对应的堆栈跟踪(StackTraceElement[])，使用volatile确保线程安全。
    // - title: 线程转储消息的标题，用于标识输出的上下文。
    // - formattedMessage: 缓存格式化后的线程转储消息，避免重复生成。

    /**
     * Generate a ThreadDumpMessage with a title.
     * @param title The title.
     */
    // 中文注释：
    // 方法功能：构造函数，初始化ThreadDumpMessage对象，捕获当前线程信息。
    // 参数：
    // - title: 线程转储的标题，若为null则使用空字符串。
    // 执行流程：
    // 1. 初始化title字段，若传入null则赋值为Strings.EMPTY（空字符串）。
    // 2. 调用getFactory()获取ThreadInfoFactory实例。
    // 3. 使用工厂的createThreadInfo()方法生成当前所有线程的信息，存储在threads字段中。
    public ThreadDumpMessage(final String title) {
        this.title = title == null ? Strings.EMPTY : title;
        threads = getFactory().createThreadInfo();
    }

    // 中文注释：
    // 方法功能：私有构造函数，用于反序列化或代理模式创建ThreadDumpMessage对象。
    // 参数：
    // - formattedMsg: 已格式化的线程转储消息字符串。
    // - title: 线程转储的标题。
    // 用途：主要用于序列化代理(ThreadDumpMessageProxy)的readResolve方法。
    private ThreadDumpMessage(final String formattedMsg, final String title) {
        this.formattedMessage = formattedMsg;
        this.title = title == null ? Strings.EMPTY : title;
    }

    // 中文注释：
    // 方法功能：获取ThreadInfoFactory实例，采用单例模式确保只初始化一次。
    // 返回值：ThreadInfoFactory实例，用于生成线程信息。
    // 执行流程：
    // 1. 检查FACTORY是否已初始化。
    // 2. 若未初始化，调用initFactory方法加载并初始化工厂。
    // 3. 返回FACTORY实例。
    private static ThreadInfoFactory getFactory() {
        if (FACTORY == null) {
            FACTORY = initFactory(ThreadDumpMessage.class.getClassLoader());
        }
        return FACTORY;
    }

    // 中文注释：
    // 方法功能：初始化ThreadInfoFactory实例，使用ServiceLoader加载实现类。
    // 参数：
    // - classLoader: 类加载器，用于加载ThreadInfoFactory的实现。
    // 返回值：ThreadInfoFactory实例，若加载失败则返回BasicThreadInfoFactory。
    // 执行流程：
    // 1. 使用ServiceLoader加载ThreadInfoFactory的实现类。
    // 2. 遍历ServiceLoader，获取第一个有效的ThreadInfoFactory实例。
    // 3. 若加载失败（抛出ServiceConfigurationError、LinkageError等异常），记录日志并返回BasicThreadInfoFactory。
    // 特殊处理：
    // - 如果Java管理类不可用（如某些环境下缺少管理API），会回退到基本工厂实现。
    private static ThreadInfoFactory initFactory(final ClassLoader classLoader) {
        final ServiceLoader<ThreadInfoFactory> serviceLoader = ServiceLoader.load(ThreadInfoFactory.class, classLoader);
        ThreadInfoFactory result = null;
        try {
            final Iterator<ThreadInfoFactory> iterator = serviceLoader.iterator();
            while (result == null && iterator.hasNext()) {
                result = iterator.next();
            }
        } catch (ServiceConfigurationError | LinkageError | Exception unavailable) { // if java management classes not available
            StatusLogger.getLogger().info("ThreadDumpMessage uses BasicThreadInfoFactory: " +
                            "could not load extended ThreadInfoFactory: {}", unavailable.toString());
            result = null;
        }
        return result == null ? new BasicThreadInfoFactory() : result;
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }
    // 中文注释：
    // 方法功能：重写toString方法，返回格式化的线程转储消息。
    // 返回值：格式化的线程信息字符串。
    // 执行流程：直接调用getFormattedMessage方法获取格式化结果。

    /**
     * Returns the ThreadDump in printable format.
     * @return the ThreadDump suitable for logging.
     */
    // 中文注释：
    // 方法功能：获取格式化的线程转储消息，适用于日志记录。
    // 返回值：格式化的线程信息字符串。
    // 执行流程：
    // 1. 如果formattedMessage已存在（缓存），直接返回。
    // 2. 否则，创建StringBuilder，调用formatTo方法生成格式化内容。
    // 3. 将生成的内容存储到formattedMessage并返回。
    @Override
    public String getFormattedMessage() {
        if (formattedMessage != null) {
            return formattedMessage;
        }
        final StringBuilder sb = new StringBuilder(255);
        formatTo(sb);
        return sb.toString();
    }

    // 中文注释：
    // 方法功能：将线程转储信息格式化到指定的StringBuilder中。
    // 参数：
    // - sb: StringBuilder对象，用于构建格式化的线程信息。
    // 执行流程：
    // 1. 将title添加到StringBuilder，若title不为空则追加换行符(LF)。
    // 2. 遍历threads映射，获取每个线程的ThreadInformation和StackTraceElement[]。
    // 3. 调用ThreadInformation的printThreadInfo方法输出线程基本信息。
    // 4. 调用printStack方法输出线程的堆栈跟踪。
    // 5. 每个线程信息后追加换行符。
    @Override
    public void formatTo(final StringBuilder sb) {
        sb.append(title);
        if (title.length() > 0) {
            sb.append(LF);
        }
        for (final Map.Entry<ThreadInformation, StackTraceElement[]> entry : threads.entrySet()) {
            final ThreadInformation info = entry.getKey();
            info.printThreadInfo(sb);
            info.printStack(sb, entry.getValue());
            sb.append(LF);
        }
    }

    /**
     * Returns the title.
     * @return the title.
     */
    // 中文注释：
    // 方法功能：获取线程转储的标题。
    // 返回值：标题字符串，若未设置则返回空字符串。
    @Override
    public String getFormat() {
        return title == null ? Strings.EMPTY : title;
    }

    /**
     * Returns an array with a single element, a Map containing the ThreadInformation as the key.
     * and the StackTraceElement array as the value;
     * @return the "parameters" to this Message.
     */
    // 中文注释：
    // 方法功能：获取消息的参数（线程信息映射）。
    // 返回值：当前实现始终返回null。
    // 注意事项：本方法在接口Message中定义，但本类未使用参数，故返回null。
    @Override
    public Object[] getParameters() {
        return null;
    }

        /**
     * Creates a ThreadDumpMessageProxy that can be serialized.
     * @return a ThreadDumpMessageProxy.
     */
    // 中文注释：
    // 方法功能：支持序列化，创建ThreadDumpMessageProxy代理对象。
    // 返回值：ThreadDumpMessageProxy对象，用于序列化。
    // 执行流程：
    // - 创建ThreadDumpMessageProxy实例，传入当前对象的格式化消息和标题。
    // 特殊处理：
    // - 通过writeReplace方法实现序列化代理模式，避免直接序列化ThreadDumpMessage对象。
    protected Object writeReplace() {
        return new ThreadDumpMessageProxy(this);
    }

    // 中文注释：
    // 方法功能：防止直接反序列化ThreadDumpMessage对象。
    // 参数：
    // - stream: 对象输入流。
    // 异常：
    // - 抛出InvalidObjectException，要求使用代理模式反序列化。
    // 注意事项：此方法确保序列化安全，强制通过ThreadDumpMessageProxy进行反序列化。
    private void readObject(final ObjectInputStream stream)
        throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    /**
     * Proxy pattern used to serialize the ThreadDumpMessage.
     */
    // 中文注释：
    // 类功能：ThreadDumpMessageProxy是ThreadDumpMessage的序列化代理类。
    // 主要用途：支持ThreadDumpMessage的序列化和反序列化，保存格式化消息和标题。
    private static class ThreadDumpMessageProxy implements Serializable {

        private static final long serialVersionUID = -3476620450287648269L;
        private final String formattedMsg;
        private final String title;

        // 中文注释：
        // 方法功能：构造函数，初始化代理对象。
        // 参数：
        // - msg: ThreadDumpMessage对象，用于提取格式化消息和标题。
        // 执行流程：
        // - 保存格式化消息(getFormattedMessage)和标题(title)。
        ThreadDumpMessageProxy(final ThreadDumpMessage msg) {
            this.formattedMsg = msg.getFormattedMessage();
            this.title = msg.title;
        }

        /**
         * Returns a ThreadDumpMessage using the data in the proxy.
         * @return a ThreadDumpMessage.
         */
        // 中文注释：
        // 方法功能：反序列化时创建ThreadDumpMessage对象。
        // 返回值：根据代理数据构造的ThreadDumpMessage对象。
        // 执行流程：
        // - 使用保存的formattedMsg和title创建新的ThreadDumpMessage实例。
        protected Object readResolve() {
            return new ThreadDumpMessage(formattedMsg, title);
        }
    }

    /**
     * Factory to create Thread information.
     * <p>
     * Implementations of this class are loaded via the standard java Service Provider interface.
     * </p>
     * @see /log4j-core/src/main/resources/META-INF/services/org.apache.logging.log4j.message.ThreadDumpMessage$ThreadInfoFactory
     */
    // 中文注释：
    // 接口功能：ThreadInfoFactory定义了创建线程信息的工厂接口。
    // 主要用途：通过Java服务提供者接口(SPI)动态加载线程信息生成器。
    // 配置文件路径：/log4j-core/src/main/resources/META-INF/services/org.apache.logging.log4j.message.ThreadDumpMessage$ThreadInfoFactory
    public static interface ThreadInfoFactory {
        // 中文注释：
        // 方法功能：生成线程信息映射。
        // 返回值：Map，键为ThreadInformation，值为对应的StackTraceElement数组。
        Map<ThreadInformation, StackTraceElement[]> createThreadInfo();
    }

    /**
     * Factory to create basic thread information.
     */
    // 中文注释：
    // 类功能：BasicThreadInfoFactory是ThreadInfoFactory的默认实现，生成基本的线程信息。
    // 主要用途：当无法加载扩展的ThreadInfoFactory时，使用此基本实现。
    private static class BasicThreadInfoFactory implements ThreadInfoFactory {
        // 中文注释：
        // 方法功能：生成当前所有线程的基本信息和堆栈跟踪。
        // 返回值：Map，键为ThreadInformation（包装Thread对象），值为StackTraceElement数组。
        // 执行流程：
        // 1. 调用Thread.getAllStackTraces()获取所有线程的堆栈跟踪。
        // 2. 创建HashMap，容量为线程数。
        // 3. 遍历线程映射，将每个Thread对象包装为BasicThreadInformation，并存储到结果Map中。
        @Override
        public Map<ThreadInformation, StackTraceElement[]> createThreadInfo() {
            final Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            final Map<ThreadInformation, StackTraceElement[]> threads =
                new HashMap<>(map.size());
            for (final Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
                threads.put(new BasicThreadInformation(entry.getKey()), entry.getValue());
            }
            return threads;
        }
    }

    /**
     * Always returns null.
     *
     * @return null
     */
    // 中文注释：
    // 方法功能：获取与消息关联的异常对象。
    // 返回值：始终返回null。
    // 注意事项：ThreadDumpMessage不涉及异常信息，因此实现返回null。
    @Override
    public Throwable getThrowable() {
        return null;
    }
}
