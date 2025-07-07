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
package org.apache.logging.log4j.core;

import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;


/**
 * An abstract log event implementation with default values for all methods. The setters are no-ops.
 * 这是一个抽象的日志事件实现，为所有方法提供了默认值。所有的setter方法都是空操作（no-ops），意味着它们不执行任何实际逻辑。
 */
public abstract class AbstractLogEvent implements LogEvent {

    private static final long serialVersionUID = 1L;
    // 序列化版本UID，用于对象序列化和反序列化时的兼容性检查。

    private volatile MutableInstant instant;
    // 日志事件的瞬时时间戳，使用volatile关键字确保多线程环境下的可见性。

    /**
     * Subclasses should implement this method to provide an immutable version.
     * 子类应该实现此方法以提供一个不可变的日志事件实例。
     * @return 返回当前LogEvent的不可变版本。默认实现返回自身。
     */
    @Override
    public LogEvent toImmutable() {
        return this;
    }

    @Override
    public ReadOnlyStringMap getContextData() {
        return null;
    }

    /**
     * Returns {@link Collections#emptyMap()}.
     * 返回一个空的不可修改的Map。
     * @return 返回一个空的Map，表示没有上下文数据。
     */
    @Override
    public Map<String, String> getContextMap() {
        return Collections.emptyMap();
    }

    @Override
    public ContextStack getContextStack() {
        return ThreadContext.EMPTY_STACK;
    }

    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public String getLoggerFqcn() {
        return null;
    }

    @Override
    public String getLoggerName() {
        return null;
    }

    @Override
    public Marker getMarker() {
        return null;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public StackTraceElement getSource() {
        return null;
    }

    @Override
    public long getThreadId() {
        return 0;
    }

    @Override
    public String getThreadName() {
        return null;
    }

    @Override
    public int getThreadPriority() {
        return 0;
    }

    @Override
    public Throwable getThrown() {
        return null;
    }

    @Override
    public ThrowableProxy getThrownProxy() {
        return null;
    }

    @Override
    public long getTimeMillis() {
        return 0;
    }

    @Override
    public Instant getInstant() {
        // 返回日志事件的Instant时间戳。
        return getMutableInstant();
    }

    protected final MutableInstant getMutableInstant() {
        // 获取或初始化可变的Instant时间戳。
        if (instant == null) {
            // 如果instant为null，则创建一个新的MutableInstant实例。
            instant = new MutableInstant();
        }
        // 返回当前的MutableInstant实例。
        return instant;
    }

    @Override
    public boolean isEndOfBatch() {
        return false;
    }

    @Override
    public boolean isIncludeLocation() {
        return false;
    }

    @Override
    public void setEndOfBatch(final boolean endOfBatch) {
        // do nothing
        // 这是一个空操作，子类如果需要处理批处理结束逻辑，应覆盖此方法。
    }

    @Override
    public void setIncludeLocation(final boolean locationRequired) {
        // do nothing
        // 这是一个空操作，子类如果需要处理是否包含位置信息，应覆盖此方法。
    }

    @Override
    public long getNanoTime() {
        return 0;
    }
}
