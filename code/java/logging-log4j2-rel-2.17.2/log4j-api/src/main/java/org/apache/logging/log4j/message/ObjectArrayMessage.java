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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.logging.log4j.util.Constants;

/**
 * Handles messages that contain an Object[].
 * <p>
 * Created for use with the CSV layout. For example:
 * </p>
 * <p>
 * {@code logger.debug(new ObjectArrayMessage(1, 2, "Bob"));}
 * </p>
 * 
 * @since 2.4
 */
/**
 * 处理包含 Object 数组的消息。
 * <p>
 * 专为 CSV 布局设计。例如：
 * </p>
 * <p>
 * {@code logger.debug(new ObjectArrayMessage(1, 2, "Bob"));}
 * </p>
 * <ul>
 *   <li>类的主要功能：用于处理和格式化包含对象数组的日志消息，主要用于 Log4j 的日志记录系统，特别适合 CSV 格式的日志输出。</li>
 *   <li>类的目的：提供一种标准化的方式，将对象数组转换为字符串表示，并支持序列化以便在日志系统中使用。</li>
 *   <li>注意事项：此类实现 Message 接口，确保与 Log4j 的日志框架兼容；支持序列化以便在分布式系统中传递消息。</li>
 *   <li>版本：自 Log4j 2.4 版本引入。</li>
 * </ul>
 */
public final class ObjectArrayMessage implements Message {

    private static final long serialVersionUID = -5903272448334166185L;
    // 序列化版本号，用于确保序列化和反序列化时的兼容性
    // 序列化版本号，用于在对象序列化和反序列化时保持一致性，防止版本不匹配导致的错误

    private transient Object[] array;
    // 存储传入的对象数组，标记为 transient 以避免直接序列化
    // 变量用途：保存消息中的对象数组数据，transient 修饰符表示该字段在序列化时由自定义方法处理

    private transient String arrayString;
    // 缓存对象数组的字符串表示，优化性能
    // 变量用途：存储对象数组的格式化字符串表示，用于避免重复调用 Arrays.toString()，提高效率

    /**
     * Creates the ObjectMessage.
     * 
     * @param obj
     *            The Object to format.
     */
    /**
     * 创建 ObjectArrayMessage 实例。
     * <ul>
     *   <li>方法功能：构造一个新的 ObjectArrayMessage 实例，用于初始化对象数组消息。</li>
     *   <li>参数说明：
     *     <ul>
     *       <li>obj：可变参数，类型为 Object 数组，表示需要格式化的对象数组，可以为空。</li>
     *     </ul>
     *   </li>
     *   <li>执行流程：
     *     <ol>
     *       <li>检查传入的 obj 是否为空。</li>
     *       <li>若为空，使用 Constants.EMPTY_OBJECT_ARRAY（空对象数组）赋值给 array；否则直接赋值 obj。</li>
     *     </ol>
     *   </li>
     *   <li>注意事项：支持可变参数，允许灵活传递任意数量的对象；若传入 null，则使用空数组以避免空指针问题。</li>
     * </ul>
     */
    public ObjectArrayMessage(final Object... obj) {
        this.array = obj == null ? Constants.EMPTY_OBJECT_ARRAY : obj;
    }

    /**
     * Compares two Object arrays for equality by comparing either the arrays themselves or their toString values.
     */
    private boolean equalObjectsOrStrings(final Object[] left, final Object[] right) {
        return Arrays.equals(left, right) || Arrays.toString(left).equals(Arrays.toString(right));
    }
    /**
     * 比较两个对象数组是否相等，通过比较数组本身或其字符串表示。
     * <ul>
     *   <li>方法功能：判断两个对象数组是否相等，支持直接数组比较或字符串表示比较。</li>
     *   <li>参数说明：
     *     <ul>
     *       <li>left：左侧对象数组，用于比较。</li>
     *       <li>right：右侧对象数组，用于比较。</li>
     *     </ul>
     *   </li>
     *   <li>返回值：布尔值，true 表示两个数组相等，false 表示不相等。</li>
     *   <li>执行流程：
     *     <ol>
     *       <li>首先使用 Arrays.equals() 比较两个数组的元素是否完全相同。</li>
     *       <li>若数组不相等，则比较 Arrays.toString() 生成的字符串表示是否相同。</li>
     *     </ol>
     *   </li>
     *   <li>注意事项：通过字符串比较提供了一种宽松的相等性检查，适用于数组对象本身的 equals 方法不可靠的情况。</li>
     * </ul>
     */

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ObjectArrayMessage that = (ObjectArrayMessage) o;
        return array == null ? that.array == null : equalObjectsOrStrings(array, that.array);
    }
    /**
     * 判断当前对象是否与指定对象相等。
     * <ul>
     *   <li>方法功能：实现 equals 方法，比较两个 ObjectArrayMessage 对象的相等性。</li>
     *   <li>参数说明：
     *     <ul>
     *       <li>o：待比较的对象。</li>
     *     </ul>
     *   </li>
     *   <li>返回值：布尔值，true 表示相等，false 表示不相等。</li>
     *   <li>执行流程：
     *     <ol>
     *       <li>检查是否为同一对象，若是返回 true。</li>
     *       <li>检查传入对象是否为 null 或类型不匹配，若是返回 false。</li>
     *       <li>将传入对象转换为 ObjectArrayMessage 类型，比较两者的 array 字段。</li>
     *       <li>若 array 为 null，检查对方 array 是否也为 null；否则调用 equalObjectsOrStrings 方法比较数组。</li>
     *     </ol>
     *   </li>
     *   <li>注意事项：确保相等性检查符合 Java 的 equals 契约，支持 null 值处理，依赖 equalObjectsOrStrings 方法进行数组比较。</li>
     * </ul>
     */

    /**
     * Returns the object formatted using its toString method.
     * 
     * @return the String representation of the object.
     */
    @Override
    public String getFormat() {
        return getFormattedMessage();
    }
    /**
     * 返回对象的格式化字符串表示。
     * <ul>
     *   <li>方法功能：实现 Message 接口的 getFormat 方法，返回对象数组的字符串表示。</li>
     *   <li>返回值：字符串，表示对象数组的格式化结果。</li>
     *   <li>执行流程：直接调用 getFormattedMessage 方法获取格式化字符串。</li>
     *   <li>注意事项：此方法为接口方法，实际逻辑委托给 getFormattedMessage，确保格式化一致性。</li>
     * </ul>
     */

    /**
     * Returns the formatted object message.
     * 
     * @return the formatted object message.
     */
    @Override
    public String getFormattedMessage() {
        // LOG4J2-763: cache formatted string in case obj changes later
        if (arrayString == null) {
            arrayString = Arrays.toString(array);
        }
        return arrayString;
    }
    /**
     * 返回格式化的对象消息。
     * <ul>
     *   <li>方法功能：实现 Message 接口的 getFormattedMessage 方法，返回对象数组的格式化字符串表示。</li>
     *   <li>返回值：字符串，表示对象数组的格式化结果（如 "[1, 2, Bob]"）。</li>
     *   <li>执行流程：
     *     <ol>
     *       <li>检查 arrayString 是否为 null（即是否已缓存格式化字符串）。</li>
     *       <li>若为 null，调用 Arrays.toString(array) 生成字符串表示并缓存到 arrayString。</li>
     *       <li>返回缓存的 arrayString。</li>
     *     </ol>
     *   </li>
     *   <li>注意事项：
     *     <ul>
     *       <li>使用缓存（arrayString）避免重复格式化，提高性能（解决 LOG4J2-763 问题）。</li>
     *       <li>缓存机制确保即使 array 内容变化，格式化结果保持一致。</li>
     *     </ul>
     *   </li>
     * </ul>
     */

    /**
     * Returns the object as if it were a parameter.
     * 
     * @return The object.
     */
    @Override
    public Object[] getParameters() {
        return array;
    }
    /**
     * 返回对象数组作为参数。
     * <ul>
     *   <li>方法功能：实现 Message 接口的 getParameters 方法，返回存储的对象数组。</li>
     *   <li>返回值：Object 数组，表示消息中的原始对象数据。</li>
     *   <li>执行流程：直接返回 array 字段。</li>
     *   <li>注意事项：此方法允许日志框架访问原始对象数组，以便进一步处理或格式化。</li>
     * </ul>
     */

    /**
     * Returns null.
     *
     * @return null.
     */
    @Override
    public Throwable getThrowable() {
        return null;
    }
    /**
     * 返回 null，表示无异常信息。
     * <ul>
     *   <li>方法功能：实现 Message 接口的 getThrowable 方法，表示此消息不包含异常信息。</li>
     *   <li>返回值：始终返回 null。</li>
     *   <li>执行流程：直接返回 null，无任何逻辑处理。</li>
     *   <li>注意事项：此方法满足接口要求，表明 ObjectArrayMessage 不用于传递异常信息。</li>
     * </ul>
     */

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
    /**
     * 返回对象数组的哈希值。
     * <ul>
     *   <li>方法功能：实现 hashCode 方法，计算对象数组的哈希值以支持 equals 方法。</li>
     *   <li>返回值：整数，表示对象数组的哈希值。</li>
     *   <li>执行流程：调用 Arrays.hashCode(array) 计算 array 字段的哈希值。</li>
     *   <li>注意事项：哈希值计算与 equals 方法一致，确保符合 Java 的 hashCode 契约。</li>
     * </ul>
     */

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        array = (Object[]) in.readObject();
    }
    /**
     * 自定义反序列化方法，从输入流中读取对象。
     * <ul>
     *   <li>方法功能：实现自定义反序列化逻辑，恢复 ObjectArrayMessage 对象的状态。</li>
     *   <li>参数说明：
     *     <ul>
     *       <li>in：ObjectInputStream，对象输入流，用于读取序列化数据。</li>
     *     </ul>
     *   </li>
     *   <li>异常：抛出 IOException（输入/输出错误）或 ClassNotFoundException（类未找到）。</li>
     *   <li>执行流程：
     *     <ol>
     *       <li>调用 defaultReadObject() 恢复非 transient 字段（serialVersionUID）。</li>
     *       <li>读取输入流中的对象并转换为 Object 数组，赋值给 array 字段。</li>
     *     </ol>
     *   </li>
     *   <li>注意事项：由于 array 和 arrayString 标记为 transient，此方法负责恢复 array 字段，arrayString 将在首次调用 getFormattedMessage 时重新生成。</li>
     * </ul>
     */

    @Override
    public String toString() {
        return getFormattedMessage();
    }
    /**
     * 返回对象的字符串表示。
     * <ul>
     *   <li>方法功能：实现 toString 方法，返回对象数组的格式化字符串表示。</li>
     *   <li>返回值：字符串，表示对象数组的格式化结果。</li>
     *   <li>执行流程：直接调用 getFormattedMessage 方法获取格式化字符串。</li>
     *   <li>注意事项：与 getFormat 方法类似，依赖缓存的 arrayString 以提高性能。</li>
     * </ul>
     */

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(array);
    }
    /**
     * 自定义序列化方法，将对象写入输出流。
     * <ul>
     *   <li>方法功能：实现自定义序列化逻辑，将 ObjectArrayMessage 对象的状态写入输出流。</li>
     *   <li>参数说明：
     *     <ul>
     *       <li>out：ObjectOutputStream，对象输出流，用于写入序列化数据。</li>
     *     </ul>
     *   </li>
     *   <li>异常：抛出 IOException（输入/输出错误）。</li>
     *   <li>执行流程：
     *     <ol>
     *       <li>调用 defaultWriteObject() 序列化非 transient 字段（serialVersionUID）。</li>
     *       <li>将 array 字段写入输出流。</li>
     *     </ol>
     *   </li>
     *   <li>注意事项：仅序列化 array 字段，arrayString 为 transient，不参与序列化，以减少序列化数据量。</li>
     * </ul>
     */
}
