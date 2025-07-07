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
package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

/**
 * Mutable Message wrapper around an Object message.
 * @since 2.6
 */
// 类功能：可重用的消息包装类，用于封装单个对象消息，支持动态修改和高效格式化
// 用途：为 Log4j 日志框架提供可重用消息对象，减少对象分配以提高性能
@PerformanceSensitive("allocation")
public class ReusableObjectMessage implements ReusableMessage, ParameterVisitable, Clearable {
    private static final long serialVersionUID = 6922476812535519960L;

    // 关键变量：存储消息的核心对象，支持任意类型
    private transient Object obj;

    // 方法功能：设置消息对象
    // 参数说明：object - 要封装的消息对象，可以是任意类型
    // 执行流程：将传入的对象赋值给 obj 字段
    // 注意事项：transient 修饰符确保 obj 在序列化时不被持久化
    public void set(final Object object) {
        this.obj = object;
    }

    /**
     * Returns the formatted object message.
     *
     * @return the formatted object message.
     */
    // 方法功能：获取格式化后的消息字符串
    // 返回值：obj 对象的字符串表示形式
    // 执行流程：调用 String.valueOf(obj) 将对象转换为字符串
    // 注意事项：如果 obj 为 null，返回 "null" 字符串
    @Override
    public String getFormattedMessage() {
        return String.valueOf(obj);
    }

    // 方法功能：将格式化后的消息追加到指定的 StringBuilder
    // 参数说明：buffer - 用于存储格式化结果的 StringBuilder
    // 执行流程：通过 StringBuilders.appendValue 将 obj 的字符串表示追加到 buffer
    // 用途：支持高效的字符串拼接，避免创建中间字符串对象
    @Override
    public void formatTo(final StringBuilder buffer) {
        StringBuilders.appendValue(buffer, obj);
    }

    /**
     * Returns the object formatted using its toString method.
     *
     * @return the String representation of the object.
     */
    // 方法功能：获取消息的格式化模板（仅当 obj 为 String 时返回）
    // 返回值：如果 obj 是 String 类型，返回其值；否则返回 null
    // 执行流程：检查 obj 是否为 String 类型，若是则返回，否则返回 null
    // 注意事项：仅适用于字符串类型的消息对象
    @Override
    public String getFormat() {
        return obj instanceof String ? (String) obj : null;
    }

    /**
     * Returns the object parameter.
     *
     * @return The object.
     * @since 2.7
     */
    // 方法功能：获取封装的消息对象
    // 返回值：obj 对象本身
    // 执行流程：直接返回 obj 字段
    // 用途：允许外部直接访问消息对象
    public Object getParameter() {
        return obj;
    }

    /**
     * Returns the object as if it were a parameter.
     *
     * @return The object.
     */
    // 方法功能：将消息对象作为参数数组返回
    // 返回值：包含 obj 的单元素对象数组
    // 执行流程：创建新数组并将 obj 放入第一个位置
    // 用途：支持日志框架处理参数化消息
    @Override
    public Object[] getParameters() {
        return new Object[] {obj};
    }

    // 方法功能：获取对象的字符串表示
    // 返回值：格式化后的消息字符串
    // 执行流程：直接调用 getFormattedMessage 方法
    // 注意事项：与 toString 方法保持一致，用于调试或日志输出
    @Override
    public String toString() {
        return getFormattedMessage();
    }

    /**
     * Gets the message if it is a throwable.
     *
     * @return the message if it is a throwable.
     */
    // 方法功能：获取消息对象是否为异常（Throwable）
    // 返回值：如果 obj 是 Throwable 类型，返回其值；否则返回 null
    // 执行流程：检查 obj 是否为 Throwable 类型并进行类型转换
    // 用途：支持日志框架处理异常信息
    @Override
    public Throwable getThrowable() {
        return obj instanceof Throwable ? (Throwable) obj : null;
    }

    /**
     * This message has exactly one parameter (the object), so returns it as the first parameter in the array.
     * @param emptyReplacement the parameter array to return
     * @return the specified array
     */
    // 方法功能：将 obj 作为参数填充到指定数组中
    // 参数说明：emptyReplacement - 用于存储参数的数组
    // 返回值：填充后的数组
    // 执行流程：
    // 1. 如果 emptyReplacement 长度为 0，创建默认大小为 10 的新数组
    // 2. 将 obj 放入数组的第一个位置
    // 3. 返回填充后的数组
    // 注意事项：设计为可重用，避免频繁分配内存
    // 配置参数：默认数组大小为 10，适合大多数日志参数场景
    @Override
    public Object[] swapParameters(final Object[] emptyReplacement) {
        // it's unlikely that emptyReplacement is of length 0, but if it is,
        // go ahead and allocate the memory now;
        // this saves an allocation in the future when this buffer is re-used
        if (emptyReplacement.length == 0) {
            final Object[] params = new Object[10]; // Default reusable parameter buffer size
            params[0] = obj;
            return params;
        }
        emptyReplacement[0] = obj;
        return emptyReplacement;
    }

    /**
     * This message has exactly one parameter (the object), so always returns one.
     * @return 1
     */
    // 方法功能：返回消息的参数数量
    // 返回值：固定返回 1，表示只有一个参数
    // 执行流程：直接返回常量 1
    // 用途：支持日志框架了解参数数量
    @Override
    public short getParameterCount() {
        return 1;
    }

    // 方法功能：对消息参数执行消费者操作
    // 参数说明：
    // - action：参数消费者，定义如何处理参数
    // - state：附加状态信息，传递给消费者
    // 执行流程：将 obj 作为第一个参数（索引 0）传递给 action 的 accept 方法
    // 用途：支持日志框架对参数的自定义处理
    @Override
    public <S> void forEachParameter(final ParameterConsumer<S> action, final S state) {
        action.accept(obj, 0, state);
    }

    // 方法功能：创建消息的不可变快照
    // 返回值：新的 ObjectMessage 实例，封装当前 obj
    // 执行流程：使用 obj 创建新的 ObjectMessage 对象
    // 用途：提供不可变消息对象以确保线程安全
    @Override
    public Message memento() {
        return new ObjectMessage(obj);
    }

    // 方法功能：清除消息对象
    // 执行流程：将 obj 字段置为 null
    // 用途：支持对象重用，释放引用以便垃圾回收
    @Override
    public void clear() {
        obj = null;
    }
}
