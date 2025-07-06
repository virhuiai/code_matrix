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
package org.apache.logging.log4j;

import java.io.Serializable;

/**
 * Markers are objects that are used to add easily filterable information to log messages.
 *
 * Markers can be hierarchical - each Marker may have a parent. This allows for broad categories being subdivided into
 * more specific categories. An example might be a Marker named "Error" with children named "SystemError" and
 * "ApplicationError".
 */
// 中文注释：
// Marker 接口用于为日志消息添加可过滤的信息。
// Marker 支持层级结构，每个 Marker 可以有一个父 Marker，允许将大类别细分为更具体的子类别。
// 示例：一个名为“Error”的 Marker 可以有子 Marker，如“SystemError”和“ApplicationError”。
public interface Marker extends Serializable {

    /**
     * Adds a Marker as a parent to this Marker.
     * 
     * @param markers The parent markers to add.
     * @return The current Marker object, thus allowing multiple adds to be concatenated.
     * @throws IllegalArgumentException if the argument is {@code null}
     */
    // 中文注释：
    // 方法目的：将一个或多个 Marker 添加为此 Marker 的父 Marker。
    // 参数说明：markers - 要添加的父 Marker 数组。
    // 返回值：返回当前 Marker 对象，支持链式调用以添加多个父 Marker。
    // 特殊处理：如果参数为 null，将抛出 IllegalArgumentException 异常。
    Marker addParents(Marker... markers);

    /**
     * Returns true if the given marker has the same name as this marker.
     *
     * @param obj the reference object with which to compare.
     * @return true if the given marker has the same name as this marker.
     * @since 2.4
     */
    // 中文注释：
    // 方法目的：比较指定对象与当前 Marker 的名称是否相同。
    // 参数说明：obj - 用于比较的对象，通常是另一个 Marker。
    // 返回值：如果指定对象的名称与当前 Marker 名称相同，返回 true，否则返回 false。
    // 特殊处理：重写了 Object 类的 equals 方法，用于名称比较。
    @Override
    boolean equals(Object obj);

    /**
     * Returns the name of this Marker.
     * 
     * @return The name of the Marker.
     */
    // 中文注释：
    // 方法目的：获取当前 Marker 的名称。
    // 返回值：Marker 的名称字符串。
    // 关键变量说明：Marker 的名称是其唯一标识，用于区分不同的 Marker。
    String getName();

    /**
     * Returns a list of parents of this Marker.
     * 
     * @return The parent Markers or {@code null} if this Marker has no parents.
     */
    // 中文注释：
    // 方法目的：获取当前 Marker 的父 Marker 列表。
    // 返回值：父 Marker 数组，如果没有父 Marker 则返回 null。
    // 关键变量说明：父 Marker 表示当前 Marker 的层级关系，用于实现分类的层级结构。
    Marker[] getParents();

    /**
     * Returns a hash code value based on the name of this marker. Markers are equal if they have the same name.
     * 
     * @return the computed hash code
     * @since 2.4
     */
    // 中文注释：
    // 方法目的：根据 Marker 的名称生成哈希码。
    // 返回值：根据名称计算的哈希码值。
    // 特殊处理：重写了 Object 类的 hashCode 方法，确保名称相同的 Marker 具有相同的哈希码。
    @Override
    int hashCode();

    /**
     * Indicates whether this Marker has references to any other Markers.
     * 
     * @return {@code true} if the Marker has parent Markers
     */
    // 中文注释：
    // 方法目的：检查当前 Marker 是否有父 Marker。
    // 返回值：如果存在父 Marker，返回 true，否则返回 false。
    // 用途说明：用于判断 Marker 是否处于层级结构的非根节点。
    boolean hasParents();

    /**
     * Checks whether this Marker is an instance of the specified Marker.
     * 
     * @param m The Marker to check.
     * @return {@code true} if this Marker or one of its ancestors is the specified Marker, {@code false} otherwise.
     * @throws IllegalArgumentException if the argument is {@code null}
     */
    // 中文注释：
    // 方法目的：检查当前 Marker 或其祖先是否是指定 Marker。
    // 参数说明：m - 要检查的 Marker 对象。
    // 返回值：如果当前 Marker 或其祖先与指定 Marker 相同，返回 true，否则返回 false。
    // 特殊处理：如果参数为 null，将抛出 IllegalArgumentException 异常。
    // 交互逻辑：递归检查当前 Marker 及其父 Marker 是否匹配。
    boolean isInstanceOf(Marker m);

    /**
     * Checks whether this Marker is an instance of the specified Marker.
     * 
     * @param name The name of the Marker.
     * @return {@code true} if this Marker or one of its ancestors matches the specified name, {@code false} otherwise.
     * @throws IllegalArgumentException if the argument is {@code null}
     */
    // 中文注释：
    // 方法目的：检查当前 Marker 或其祖先的名称是否与指定名称匹配。
    // 参数说明：name - 要检查的 Marker 名称。
    // 返回值：如果当前 Marker 或其祖先的名称与指定名称相同，返回 true，否则返回 false。
    // 特殊处理：如果参数为 null，将抛出 IllegalArgumentException 异常。
    // 交互逻辑：递归检查当前 Marker 及其父 Marker 的名称是否匹配。
    boolean isInstanceOf(String name);

    /**
     * Removes the specified Marker as a parent of this Marker.
     * 
     * @param marker The marker to remove.
     * @return {@code true} if the marker was removed.
     * @throws IllegalArgumentException if the argument is {@code null}
     */
    // 中文注释：
    // 方法目的：从当前 Marker 的父 Marker 列表中移除指定的 Marker。
    // 参数说明：marker - 要移除的父 Marker。
    // 返回值：如果成功移除指定 Marker，返回 true，否则返回 false。
    // 特殊处理：如果参数为 null，将抛出 IllegalArgumentException 异常。
    // 交互逻辑：修改父 Marker 列表，影响层级关系。
    boolean remove(Marker marker);

    /**
     * Replaces the set of parent Markers with the provided Markers.
     * 
     * @param markers The new set of parent Markers or {@code null} to clear the parents.
     * @return The current Marker object.
     */
    // 中文注释：
    // 方法目的：用新的 Marker 集合替换当前 Marker 的父 Marker 列表。
    // 参数说明：markers - 新的父 Marker 数组，若为 null 则清除所有父 Marker。
    // 返回值：返回当前 Marker 对象，支持链式调用。
    // 交互逻辑：完全替换父 Marker 列表，重新定义层级关系。
    Marker setParents(Marker... markers);
}
