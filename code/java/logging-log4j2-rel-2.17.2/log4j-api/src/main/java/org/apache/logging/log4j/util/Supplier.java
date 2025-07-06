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
// 本代码遵循Apache 2.0许可证发布，详细版权和许可信息请参阅上述链接。
// 该许可证规定了代码的使用、修改和分发的条件，软件按“原样”提供，不附带任何明示或暗示的保证。

package org.apache.logging.log4j.util;

/**
 * <em>Consider this class private.</em>
 * Classes implementing this interface know how to supply a value.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html">functional
 * interface</a> intended to support lambda expressions in log4j 2.
 *
 * <p>Implementors are free to cache values or return a new or distinct value each time the supplier is invoked.
 *
 * <p><strong>DEPRECATED:</strong> this class will be removed in 3.0 to be replaced with the Java 8 interface.</p>
 *
 * @param <T> the type of values returned by this supplier
 *
 * @since 2.4
 */
// 中文注释：
// 该接口被标记为私有，仅供内部使用。
// 实现此接口的类能够提供某种值（Supplier模式）。
// 该接口是一个功能接口，旨在支持Log4j 2中的Lambda表达式。
// 实现者可以选择缓存值，或在每次调用时返回新的或不同的值。
// 注意：此接口已被标记为过时，将在Log4j 3.0中被Java 8的Supplier接口替换。
// 参数说明：泛型T表示此接口返回的值的类型。
// 自2.4版本起引入。

public interface Supplier<T> {
//    音标: /səˈplaɪər/（美式英语）
//供应商，提供者
//在编程中，特指一个提供某种值的功能接口（Functional Interface），用于按需生成或返回值的对象。

    /**
     * Gets a value.
     *
     * @return a value
     */
    // 中文注释：
    // 方法目的：定义一个获取值的方法。
    // 功能说明：实现此接口的类必须提供get()方法，用于返回一个类型为T的值。
    // 返回值：返回类型为T的值，具体值由实现类决定。
    // 注意事项：实现者可以根据需求缓存返回值或每次生成新值。
    T get();
}
