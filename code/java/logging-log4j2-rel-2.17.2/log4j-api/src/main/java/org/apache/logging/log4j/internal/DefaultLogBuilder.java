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
package org.apache.logging.log4j.internal;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Supplier;


/**
 * Collects data for a log event and then logs it. This class should be considered private.
 * 收集日志事件数据并进行日志记录。这个类应被视为内部私有类。
 */
public class DefaultLogBuilder implements LogBuilder {

    private static Message EMPTY_MESSAGE = new SimpleMessage("");
    // 静态常量，表示一个空的日志消息
    private static final String FQCN = DefaultLogBuilder.class.getName();
    // 静态常量，表示当前类的完全限定名，用于日志记录中的位置信息
    private static final Logger LOGGER = StatusLogger.getLogger();
    // 静态常量，用于记录LogBuilder自身的内部状态日志，例如警告信息

    private final Logger logger;
    // 当前LogBuilder实例关联的Logger对象，实际执行日志记录
    private Level level;
    // 日志级别
    private Marker marker;
    // 日志标记
    private Throwable throwable;
    // 异常信息
    private StackTraceElement location;
    // 源代码位置信息
    private volatile boolean inUse;
    // volatile关键字确保多线程环境下inUse变量的可见性。
    // 标记当前LogBuilder实例是否正在使用中，防止重复使用
    private long threadId;
    // 记录创建此LogBuilder实例的线程ID，用于验证后续操作是否在同一线程进行

    public DefaultLogBuilder(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
        this.threadId = Thread.currentThread().getId();
        // 初始化线程ID为当前线程ID
        this.inUse = true;
        // 构造时标记为正在使用中
    }

    public DefaultLogBuilder(Logger logger) {
        this.logger = logger;
        this.inUse = false;
        // 构造时标记为未在使用中，表示需要通过reset方法初始化
        this.threadId = Thread.currentThread().getId();
        // 初始化线程ID为当前线程ID
    }

    /**
     * This method should be considered internal. It is used to reset the LogBuilder for a new log message.
     * 这个方法应被视为内部方法。它用于为新的日志消息重置LogBuilder。
     * @param level The logging level for this event.
     * level: 此日志事件的日志级别。
     * @return This LogBuilder instance.
     * 返回： 当前LogBuilder实例。
     */
    public LogBuilder reset(Level level) {
        this.inUse = true;
        // 重置时标记为正在使用中
        this.level = level;
        // 设置新的日志级别
        this.marker = null;
        // 清空标记
        this.throwable = null;
        // 清空异常
        this.location = null;
        // 清空位置信息
        return this;
        // 返回当前实例，支持链式调用
    }

    @Override
    public LogBuilder withMarker(Marker marker) {
        this.marker = marker;
        // 设置日志标记
        return this;
        // 返回当前实例，支持链式调用
    }

    @Override
    public LogBuilder withThrowable(Throwable throwable) {
        this.throwable = throwable;
        // 设置异常信息
        return this;
        // 返回当前实例，支持链式调用
    }

    @Override
    public LogBuilder withLocation() {
        location = StackLocatorUtil.getStackTraceElement(2);
        // 自动获取调用者的堆栈信息，深度为2（跳过LogBuilder自身和withLocation方法）
        return this;
        // 返回当前实例，支持链式调用
    }

    @Override
    public LogBuilder withLocation(StackTraceElement location) {
        this.location = location;
        // 手动设置位置信息
        return this;
        // 返回当前实例，支持链式调用
    }

    public boolean isInUse() {
        return inUse;
        // 返回当前LogBuilder实例是否正在使用中
    }

    @Override
    public void log(Message message) {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效（是否正在使用中且在同一线程）
            logMessage(message);
            // 执行实际的日志记录
        }
    }

    @Override
    public void log(CharSequence message) {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效
            logMessage(logger.getMessageFactory().newMessage(message));
            // 使用Logger的消息工厂创建Message对象并记录
        }
    }

    @Override
    public void log(String message) {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效
            logMessage(logger.getMessageFactory().newMessage(message));
            // 使用Logger的消息工厂创建Message对象并记录
        }
    }

    @Override
    public void log(String message, Object... params) {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效
            logMessage(logger.getMessageFactory().newMessage(message, params));
            // 使用Logger的消息工厂创建带参数的Message对象并记录
        }
    }

    @Override
    public void log(String message, Supplier<?>... params) {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效
            logMessage(logger.getMessageFactory().newMessage(message, LambdaUtil.getAll(params)));
            // 处理Supplier类型的参数，获取所有实际值后创建Message对象并记录
        }
    }

    @Override
    public void log(Supplier<Message> messageSupplier) {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效
            logMessage(messageSupplier.get());
            // 通过Supplier获取Message对象并记录
        }
    }

    @Override
    public void log(Object message) {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效
            logMessage(logger.getMessageFactory().newMessage(message));
            // 使用Logger的消息工厂创建Message对象并记录
        }
    }

    @Override
    public void log(String message, Object p0) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2, Object p3) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2, p3));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override
    public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9) {
        if (isValid()) {
            logMessage(logger.getMessageFactory().newMessage(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override
    public void log() {
        if (isValid()) {
            // 在进行日志记录之前，检查LogBuilder的状态是否有效
            logMessage(EMPTY_MESSAGE);
            // 记录一个空消息
        }
    }

    private void logMessage(Message message) {
        try {
            logger.logMessage(level, marker, FQCN, location, message, throwable);
            // 调用底层的Logger实际执行日志记录，传入收集到的所有日志事件数据
        } finally {
            inUse = false;
            // 无论日志记录成功与否，最终都将inUse标记为false，表示LogBuilder实例已使用完毕，可以被重用或回收
        }
    }

    private boolean isValid() {
        // 验证LogBuilder是否处于可用状态的方法
        if (!inUse) {
            // 如果LogBuilder未标记为正在使用中（即inUse为false），则表示尝试重用一个已完成或未初始化的LogBuilder
            LOGGER.warn("Attempt to reuse LogBuilder was ignored. {}",
                    StackLocatorUtil.getCallerClass(2));
            // 记录警告信息，指明是哪个调用者尝试重用
            return false ;
            // 返回false，表示当前LogBuilder无效，无法进行日志记录
        }
        if (this.threadId != Thread.currentThread().getId()) {
            // 如果当前线程ID与创建LogBuilder实例时的线程ID不一致，则表示尝试在不同的线程中使用LogBuilder
            LOGGER.warn("LogBuilder can only be used on the owning thread. {}",
                    StackLocatorUtil.getCallerClass(2));
            // 记录警告信息，指明LogBuilder只能在所属线程使用，并指示调用者
            return false;
            // 返回false，表示当前LogBuilder无效
        }
        return true;
        // 如果通过所有检查，则LogBuilder有效，可以继续进行日志记录
    }
}
