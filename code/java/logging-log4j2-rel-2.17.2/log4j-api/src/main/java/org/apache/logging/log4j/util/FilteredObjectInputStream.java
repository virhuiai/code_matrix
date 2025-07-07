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

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Extends {@link ObjectInputStream} to only allow some built-in Log4j classes and caller-specified classes to be
 * deserialized.
 *
 * @since 2.8.2
 */
// 类功能说明：FilteredObjectInputStream 是 ObjectInputStream 的子类，用于限制反序列化仅允许特定的 Log4j 内置类和用户指定的类，增强安全性。
// 使用场景：防止不安全的类在反序列化过程中被加载，避免潜在的安全漏洞（如反序列化攻击）。
// 注意事项：通过白名单机制（REQUIRED_JAVA_CLASSES 和 REQUIRED_JAVA_PACKAGES）以及用户提供的额外允许类（allowedExtraClasses）进行类过滤。
public class FilteredObjectInputStream extends ObjectInputStream {

    // 变量说明：定义允许反序列化的 Java 内置类集合，包含基本数据类型和特定类，防止不安全的类加载。
    // 包含类：BigDecimal、BigInteger、MarshalledObject（用于 Message 委托）以及字节数组 ([B)。
    private static final Set<String> REQUIRED_JAVA_CLASSES = new HashSet<>(Arrays.asList(
    // @formatter:off
            "java.math.BigDecimal",
            "java.math.BigInteger",
            // for Message delegate
            "java.rmi.MarshalledObject",
            "[B"
    // @formatter:on
    ));
    // 中文注释：
    // 变量用途：存储允许反序列化的 Java 核心类名集合。
    // 内容说明：包括 BigDecimal、BigInteger 用于数值计算，MarshalledObject 用于 RMI 序列化支持，[B 表示字节数组。
    // 注意事项：这些类被认为是安全的，默认允许反序列化。

    // 变量说明：定义允许反序列化的包名前缀集合，包含 Java 核心包和 Log4j 相关包。
    private static final Set<String> REQUIRED_JAVA_PACKAGES = new HashSet<>(Arrays.asList(
    // @formatter:off
            "java.lang.",
            "java.time.",
            "java.util.",
            "org.apache.logging.log4j.",
            "[Lorg.apache.logging.log4j."
    // @formatter:on
    ));
    // 中文注释：
    // 变量用途：存储允许反序列化的包名前缀集合。
    // 内容说明：包括 Java 核心包（lang、time、util）和 Log4j 相关包（org.apache.logging.log4j），以及 Log4j 对象的数组形式。
    // 注意事项：以包名前缀匹配的方式确保整个包中的类被允许，简化过滤逻辑。

    // 变量说明：存储用户指定的额外允许反序列化的类集合，默认为空。
    private final Collection<String> allowedExtraClasses;
    // 中文注释：
    // 变量用途：保存用户通过构造方法传入的额外允许反序列化的类名集合。
    // 注意事项：不可变集合（Collections.emptySet() 或用户提供的集合），确保线程安全和不可修改。

    // 构造方法：默认构造函数，初始化空的额外允许类集合。
    public FilteredObjectInputStream() throws IOException, SecurityException {
        this.allowedExtraClasses = Collections.emptySet();
    }
    // 中文注释：
    // 方法功能：初始化 FilteredObjectInputStream，设置 allowedExtraClasses 为空集合。
    // 执行流程：调用父类 ObjectInputStream 的默认构造函数，设置 allowedExtraClasses 为不可变的空集合。
    // 异常处理：可能抛出 IOException（输入输出错误）或 SecurityException（安全权限不足）。
    // 注意事项：适用于不需要额外允许类的情况，仅依赖默认的白名单进行过滤。

    // 构造方法：带输入流的构造函数，初始化空的额外允许类集合。
    public FilteredObjectInputStream(final InputStream inputStream) throws IOException {
        super(inputStream);
        this.allowedExtraClasses = Collections.emptySet();
    }
    // 中文注释：
    // 方法功能：通过指定的输入流初始化 FilteredObjectInputStream，设置 allowedExtraClasses 为空集合。
    // 参数说明：
    //   - inputStream: 输入流，提供反序列化所需的字节流数据。
    // 执行流程：
    //   1. 调用父类 ObjectInputStream 的构造函数，绑定输入流。
    //   2. 设置 allowedExtraClasses 为不可变的空集合。
    // 异常处理：可能抛出 IOException（输入流错误）。
    // 注意事项：适用于需要从特定输入流反序列化的场景，默认无额外允许类。

    // 构造方法：带额外允许类集合的构造函数，允许用户指定额外的类。
    public FilteredObjectInputStream(final Collection<String> allowedExtraClasses)
        throws IOException, SecurityException {
        this.allowedExtraClasses = allowedExtraClasses;
    }
    // 中文注释：
    // 方法功能：初始化 FilteredObjectInputStream，设置用户提供的额外允许类集合。
    // 参数说明：
    //   - allowedExtraClasses: 用户指定的允许反序列化的类名集合。
    // 执行流程：
    //   1. 调用默认构造函数（父类 ObjectInputStream）。
    //   2. 将传入的 allowedExtraClasses 赋值给实例变量。
    // 异常处理：可能抛出 IOException（输入输出错误）或 SecurityException（安全权限不足）。
    // 注意事项：用户需确保传入的类名是安全的，避免引入不安全的类。

    // 构造方法：带输入流和额外允许类集合的构造函数。
    public FilteredObjectInputStream(final InputStream inputStream, final Collection<String> allowedExtraClasses)
        throws IOException {
        super(inputStream);
        this.allowedExtraClasses = allowedExtraClasses;
    }
    // 中文注释：
    // 方法功能：通过指定的输入流和额外允许类集合初始化 FilteredObjectInputStream。
    // 参数说明：
    //   - inputStream: 输入流，提供反序列化所需的字节流数据。
    //   - allowedExtraClasses: 用户指定的允许反序列化的类名集合。
    // 执行流程：
    //   1. 调用父类 ObjectInputStream 的构造函数，绑定输入流。
    //   2. 将传入的 allowedExtraClasses 赋值给实例变量。
    // 异常处理：可能抛出 IOException（输入流错误）。
    // 注意事项：适用于需要从特定输入流反序列化并允许额外类的场景。

    // 方法说明：获取允许反序列化的额外类集合。
    public Collection<String> getAllowedClasses() {
        return allowedExtraClasses;
    }
    // 中文注释：
    // 方法功能：返回当前实例中配置的额外允许反序列化的类名集合。
    // 返回值：Collection<String>，用户指定的允许类集合（可能是空集合）。
    // 执行流程：直接返回实例变量 allowedExtraClasses。
    // 注意事项：返回值是只读的，调用者无法修改集合内容。

    // 方法说明：重写父类的 resolveClass 方法，限制反序列化的类。
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        final String name = desc.getName();
        if (!(isAllowedByDefault(name) || allowedExtraClasses.contains(name))) {
            throw new InvalidObjectException("Class is not allowed for deserialization: " + name);
        }
        return super.resolveClass(desc);
    }
    // 中文注释：
    // 方法功能：重写 ObjectInputStream 的 resolveClass 方法，检查反序列化的类是否在允许列表中。
    // 参数说明：
    //   - desc: ObjectStreamClass，表示待反序列化的类的描述信息。
    // 返回值：Class<?>，解析后的类对象。
    // 执行流程：
    //   1. 获取待反序列化类的全限定名（name）。
    //   2. 检查类名是否在默认允许列表（isAllowedByDefault）或用户指定的允许类集合（allowedExtraClasses）中。
    //   3. 如果类名不在允许列表中，抛出 InvalidObjectException 异常，阻止反序列化。
    //   4. 如果类名被允许，调用父类的 resolveClass 方法完成类解析。
    // 异常处理：
    //   - IOException：输入输出错误。
    //   - ClassNotFoundException：类未找到。
    //   - InvalidObjectException：类不在允许列表中。
    // 注意事项：此方法是安全过滤的核心逻辑，确保只有白名单中的类可以被反序列化。

    // 方法说明：检查类名是否在默认允许的类或包中。
    private static boolean isAllowedByDefault(final String name) {
        return isRequiredPackage(name) || REQUIRED_JAVA_CLASSES.contains(name);
    }
    // 中文注释：
    // 方法功能：判断类名是否属于默认允许反序列化的类或包。
    // 参数说明：
    //   - name: 待检查的类全限定名。
    // 返回值：boolean，true 表示类名在默认允许列表中，false 表示不在。
    // 执行流程：
    //   1. 检查类名是否属于允许的包（isRequiredPackage）。
    //   2. 检查类名是否在 REQUIRED_JAVA_CLASSES 集合中。
    //   3. 任一条件满足则返回 true，否则返回 false。
    // 注意事项：静态方法，逻辑简单高效，用于快速判断类是否在默认白名单中。

    // 方法说明：检查类名是否属于允许的包。
    private static boolean isRequiredPackage(final String name) {
        for (final String packageName : REQUIRED_JAVA_PACKAGES) {
            if (name.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }
    // 中文注释：
    // 方法功能：判断类名是否以允许的包名前缀开头。
    // 参数说明：
    //   - name: 待检查的类全限定名。
    // 返回值：boolean，true 表示类名属于允许的包，false 表示不属于。
    // 执行流程：
    //   1. 遍历 REQUIRED_JAVA_PACKAGES 中的包名前缀。
    //   2. 检查类名是否以某个包名前缀开头（startsWith）。
    //   3. 如果匹配任一包名前缀，返回 true；否则返回 false。
    // 注意事项：通过前缀匹配实现包级别的允许规则，适用于需要允许整个包的情况。
}
