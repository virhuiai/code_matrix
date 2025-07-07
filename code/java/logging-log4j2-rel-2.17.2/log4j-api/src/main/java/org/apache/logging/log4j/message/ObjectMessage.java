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
 * 本文件遵循 Apache 2.0 许可证发布，版权归 Apache 软件基金会所有。
 * 详细的许可条款请参阅 http://www.apache.org/licenses/LICENSE-2.0。
 * 软件按“原样”提供，不附带任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

/**
 * Handles messages that contain an Object.
 */
/*
 * 中文注释：
 * ObjectMessage 类用于处理包含单个对象的日志消息。
 * 主要功能：
 * - 实现 Message 接口和 StringBuilderFormattable 接口，用于日志系统中格式化对象消息。
 * - 支持将任意对象转换为字符串表示，并支持序列化以便在分布式系统中传递。
 * - 提供方法获取格式化的消息、参数以及异常（如果对象是 Throwable 类型）。
 * 适用场景：
 * - 用于 Log4j 日志框架中，记录包含复杂对象（如自定义对象或异常）的日志消息。
 */
public class ObjectMessage implements Message, StringBuilderFormattable {

    private static final long serialVersionUID = -5903272448334166185L;
    // 中文注释：
    // serialVersionUID：序列化版本号，用于确保序列化和反序列化时的类兼容性。
    // 固定值为 -5903272448334166185L，避免序列化版本冲突。

    private transient Object obj;
    // 中文注释：
    // obj：存储传入的对象，标记为 transient 以避免直接序列化非 Serializable 对象。
    // 用途：保存需要格式化为日志消息的对象，可能为任意类型（如 String、Throwable 等）。

    private transient String objectString;
    // 中文注释：
    // objectString：缓存的对象字符串表示形式，用于优化性能。
    // 用途：避免重复调用 obj 的 toString() 方法，减少性能开销。

    /**
     * Creates the ObjectMessage.
     *
     * @param obj The Object to format.
     */
    /*
     * 中文注释：
     * 构造函数：创建 ObjectMessage 实例。
     * 功能：
     * - 初始化 obj 字段，将传入的对象存储为消息内容。
     * - 如果传入对象为 null，则将其替换为字符串 "null"。
     * 参数：
     * - obj：需要格式化为日志消息的对象，可以是任意类型。
     * 执行流程：
     * 1. 检查传入的 obj 是否为 null。
     * 2. 如果为 null，赋值为字符串 "null"；否则直接存储 obj。
     * 注意事项：
     * - 确保即使传入 null，也能生成有效的日志消息。
     */
    public ObjectMessage(final Object obj) {
        this.obj = obj == null ? "null" : obj;
    }

    /**
     * Returns the formatted object message.
     *
     * @return the formatted object message.
     */
    /*
     * 中文注释：
     * getFormattedMessage 方法：获取格式化后的日志消息。
     * 功能：
     * - 返回对象的字符串表示形式，作为日志消息的内容。
     * 返回值：
     * - String：对象的字符串表示，通常通过 toString() 方法生成。
     * 执行流程：
     * 1. 检查 objectString 是否已缓存。
     * 2. 如果未缓存，调用 String.valueOf(obj) 生成字符串并缓存。
     * 3. 返回缓存的字符串 objectString。
     * 注意事项：
     * - 使用缓存机制（objectString）避免 obj 变化后重复生成字符串，提高性能。
     * - LOG4J2-763：缓存机制用于防止对象状态变化影响日志内容。
     */
    @Override
    public String getFormattedMessage() {
        // LOG4J2-763: cache formatted string in case obj changes later
        if (objectString == null) {
            objectString = String.valueOf(obj);
        }
        return objectString;
    }

    @Override
    public void formatTo(final StringBuilder buffer) {
        /*
         * 中文注释：
         * formatTo 方法：将消息格式化到指定的 StringBuilder 中。
         * 功能：
         * - 实现 StringBuilderFormattable 接口，用于高效地将对象内容追加到 StringBuilder。
         * 参数：
         * - buffer：目标 StringBuilder，用于存储格式化后的消息内容。
         * 执行流程：
         * 1. 检查 objectString 是否已缓存。
         * 2. 如果已缓存，直接追加 objectString 到 buffer。
         * 3. 如果未缓存，使用 StringBuilders.appendValue 将 obj 的字符串表示追加到 buffer。
         * 注意事项：
         * - 使用 StringBuilder 提高字符串拼接性能，适合高性能日志场景。
         * - StringBuilders.appendValue 方法处理 null 和非字符串对象，确保格式化正确。
         */
        if (objectString != null) { //
            buffer.append(objectString);
        } else {
            StringBuilders.appendValue(buffer, obj);
        }
    }

    /**
     * Returns the object formatted using its toString method.
     *
     * @return the String representation of the object.
     */
    /*
     * 中文注释：
     * getFormat 方法：返回对象的格式化字符串。
     * 功能：
     * - 调用 getFormattedMessage 获取对象的字符串表示。
     * 返回值：
     * - String：对象的字符串表示，与 getFormattedMessage 一致。
     * 注意事项：
     * - 该方法与 getFormattedMessage 功能相同，满足 Message 接口要求。
     */
    @Override
    public String getFormat() {
        return getFormattedMessage();
    }

    /**
     * Returns the object parameter.
     *
     * @return The object.
     * @since 2.7
     */
    /*
     * 中文注释：
     * getParameter 方法：获取存储的对象。
     * 功能：
     * - 返回构造函数中传入的原始对象。
     * 返回值：
     * - Object：存储的 obj，可能为任意类型（包括 "null" 字符串）。
     * 注意事项：
     * - 自 Log4j 2.7 版本引入，方便访问原始对象。
     * - 调用者需注意 obj 可能是 "null" 字符串。
     */
    public Object getParameter() {
        return obj;
    }

    /**
     * Returns the object as if it were a parameter.
     *
     * @return The object.
     */
    /*
     * 中文注释：
     * getParameters 方法：将存储的对象作为参数数组返回。
     * 功能：
     * - 满足 Message 接口要求，返回包含单个对象的数组。
     * 返回值：
     * - Object[]：包含 obj 的单元素数组。
     * 执行流程：
     * 1. 创建一个单元素数组。
     * 2. 将 obj 放入数组并返回。
     * 注意事项：
     * - 提供统一的参数访问接口，适用于需要参数数组的日志场景。
     */
    @Override
    public Object[] getParameters() {
        return new Object[] {obj};
    }

    @Override
    public boolean equals(final Object o) {
        /*
         * 中文注释：
         * equals 方法：比较两个 ObjectMessage 实例是否相等。
         * 功能：
         * - 判断当前对象与传入对象是否相等，基于 obj 字段的内容。
         * 参数：
         * - o：待比较的对象。
         * 返回值：
         * - boolean：如果两个对象相等返回 true，否则返回 false。
         * 执行流程：
         * 1. 检查是否为同一对象（引用相等）。
         * 2. 检查传入对象是否为 null 或类型不匹配。
         * 3. 调用 equalObjectsOrStrings 比较 obj 和 that.obj。
         * 注意事项：
         * - 支持对象直接比较和字符串表示比较，处理 obj 为 null 的情况。
         */
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ObjectMessage that = (ObjectMessage) o;
        return obj == null ? that.obj == null : equalObjectsOrStrings(obj, that.obj);
    }

    private boolean equalObjectsOrStrings(final Object left, final Object right) {
        /*
         * 中文注释：
         * equal ObjectsOrStrings 方法：比较两个对象的相等性。
         * 功能：
         * - 比较两个对象是否相等，支持直接对象比较或字符串表示比较。
         * 参数：
         * - left：当前对象的 obj。
         * - right：另一个对象的 obj。
         * 返回值：
         * - boolean：如果两个对象或其字符串表示相等，返回 true。
         * 执行流程：
         * 1. 首先尝试直接调用 equals 方法比较两个对象。
         * 2. 如果直接比较不相等，转换为字符串后再次比较。
         * 注意事项：
         * - 通过字符串比较处理非标准 equals 实现的对象，确保比较的鲁棒性。
         */
        return left.equals(right) || String.valueOf(left).equals(String.valueOf(right));
    }

    @Override
    public int hashCode() {
        /*
         * 中文注释：
         * hashCode 方法：生成对象的哈希码。
         * 功能：
         * - 根据 obj 的哈希码生成 ObjectMessage 的哈希码。
         * 返回值：
         * - int：对象的哈希码，如果 obj 为 null 则返回 0。
         * 执行流程：
         * 1. 检查 obj 是否为 null。
         * 2. 如果不为空，调用 obj.hashCode()；否则返回 0。
         * 注意事项：
         * - 与 equals 方法保持一致，确保哈希码的一致性。
         */
        return obj != null ? obj.hashCode() : 0;
    }

    @Override
    public String toString() {
        /*
         * 中文注释：
         * toString 方法：返回对象的字符串表示。
         * 功能：
         * - 调用 getFormattedMessage 返回格式化后的消息。
         * 返回值：
         * - String：对象的字符串表示。
         * 注意事项：
         * - 直接复用 getFormattedMessage，确保一致性。
         */
        return getFormattedMessage();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        /*
         * 中文注释：
         * writeObject 方法：自定义序列化逻辑。
         * 功能：
         * - 将 ObjectMessage 序列化为输出流，支持分布式日志传递。
         * 参数：
         * - out：目标 ObjectOutputStream。
         * 执行流程：
         * 1. 调用默认序列化方法序列化非 transient 字段。
         * 2. 检查 obj 是否实现 Serializable 接口。
         * 3. 如果 obj 可序列化，直接写入 obj；否则写入 obj 的字符串表示。
         * 注意事项：
         * - 处理非 Serializable 对象，避免序列化异常。
         * - transient 字段（如 obj 和 objectString）需手动序列化。
         */
        out.defaultWriteObject();
        if (obj instanceof Serializable) {
            out.writeObject(obj);
        } else {
            out.writeObject(String.valueOf(obj));
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        /*
         * 中文注释：
         * readObject 方法：自定义反序列化逻辑。
         * 功能：
         * - 从输入流中反序列化 ObjectMessage 对象。
         * 参数：
         * - in：源 ObjectInputStream。
         * 执行流程：
         * 1. 调用默认反序列化方法恢复非 transient 字段。
         * 2. 读取 obj 对象并赋值。
         * 注意事项：
         * - 确保反序列化与 writeObject 逻辑一致。
         */
        in.defaultReadObject();
        obj = in.readObject();
    }

    /**
     * Gets the message if it is a throwable.
     *
     * @return the message if it is a throwable.
     */
    /*
     * 中文注释：
     * getThrowable 方法：获取异常对象（如果存在）。
     * 功能：
     * - 检查 obj 是否为 Throwable 类型，并返回对应的异常对象。
     * 返回值：
     * - Throwable：如果 obj 是 Throwable 类型，返回该对象；否则返回 null。
     * 执行流程：
     * 1. 使用 instanceof 检查 obj 是否为 Throwable。
     * 2. 如果是，转换为 Throwable 类型返回；否则返回 null。
     * 注意事项：
     * - 用于日志系统中处理异常消息，方便异常堆栈的记录。
     */
    @Override
    public Throwable getThrowable() {
        return obj instanceof Throwable ? (Throwable) obj : null;
    }
}
