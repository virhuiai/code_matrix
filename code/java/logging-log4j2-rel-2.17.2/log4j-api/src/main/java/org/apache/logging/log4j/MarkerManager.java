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

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

/**
 * Applications create Markers by using the Marker Manager. All Markers created by this Manager are immutable.
 */
// 应用程序通过 MarkerManager 创建 Marker，所有由该管理器创建的 Marker 都是不可变的。
// 中文注释：MarkerManager 是一个用于管理和创建日志标记（Marker）的工具类，Marker 用于在日志系统中对日志事件进行分类或标记，便于日志过滤和处理。所有 Marker 实例不可修改，确保线程安全。
public final class MarkerManager {

    private static final ConcurrentMap<String, Marker> MARKERS = new ConcurrentHashMap<>();

    // 中文注释：定义一个线程安全的 ConcurrentHashMap，用于存储 Marker 实例，键为 Marker 的名称，值为对应的 Marker 对象。
    // 重要配置参数：ConcurrentHashMap 确保高并发场景下 Marker 的安全访问和存储。

    private MarkerManager() {
        // do nothing
    }
    // 中文注释：私有构造函数，防止外部实例化 MarkerManager，遵循单例模式设计。

    /**
     * Clears all markers.
     */
    // 清除所有 Marker。
    // 中文注释：清空 MARKERS 映射中的所有 Marker 实例，用于重置 Marker 管理器。
    // 方法目的：提供一种方法来清除所有已创建的 Marker，通常用于测试或重置环境。
    public static void clear() {
        MARKERS.clear();
    }

    /**
     * Tests existence of the given marker.
     *
     * @param key the marker name
     * @return true if the marker exists.
     * @since 2.4
     */
    // 测试指定 Marker 是否存在。
    // 中文注释：检查 MARKERS 映射中是否包含指定名称的 Marker。
    // 参数说明：key - Marker 的名称（字符串）。
    // 返回值：如果 Marker 存在，返回 true；否则返回 false。
    // 方法目的：用于验证某个 Marker 是否已被创建，避免重复创建或错误引用。
    public static boolean exists(final String key) {
        return MARKERS.containsKey(key);
    }

    /**
     * Retrieves a Marker or create a Marker that has no parent.
     *
     * @param name The name of the Marker.
     * @return The Marker with the specified name.
     * @throws IllegalArgumentException if the argument is {@code null}
     */
    // 获取或创建没有父节点的 Marker。
    // 中文注释：根据指定名称获取 Marker，如果不存在则创建新的 Marker（无父节点）。
    // 参数说明：name - Marker 的名称，不能为 null。
    // 返回值：指定名称的 Marker 实例。
    // 特殊处理：如果 name 为 null，抛出 IllegalArgumentException。
    // 方法目的：提供获取或创建 Marker 的主要入口，确保 Marker 的单例性和线程安全。
    public static Marker getMarker(final String name) {
        Marker result = MARKERS.get(name);
        if (result == null) {
            MARKERS.putIfAbsent(name, new Log4jMarker(name));
            result = MARKERS.get(name);
        }
        return result;
    }

    /**
     * Retrieves or creates a Marker with the specified parent. The parent must have been previously created.
     *
     * @param name The name of the Marker.
     * @param parent The name of the parent Marker.
     * @return The Marker with the specified name.
     * @throws IllegalArgumentException if the parent Marker does not exist.
     * @deprecated Use the Marker add or set methods to add parent Markers. Will be removed by final GA release.
     */
    // 获取或创建具有指定父节点的 Marker，父节点必须已存在。
    // 中文注释：根据名称获取或创建 Marker，并为其指定父 Marker（通过父 Marker 的名称）。
    // 参数说明：
    //   - name：Marker 的名称。
    //   - parent：父 Marker 的名称（字符串）。
    // 返回值：指定名称的 Marker 实例。
    // 特殊处理：如果父 Marker 不存在，抛出 IllegalArgumentException。
    // 方法目的：支持创建带有父子关系的 Marker，便于日志的层级管理。
    // 注意事项：此方法已标记为废弃，建议使用 Marker 的 addParents 或 setParents 方法。
    @Deprecated
    public static Marker getMarker(final String name, final String parent) {
        final Marker parentMarker = MARKERS.get(parent);
        if (parentMarker == null) {
            throw new IllegalArgumentException("Parent Marker " + parent + " has not been defined");
        }
        return getMarker(name, parentMarker);
    }

    /**
     * Retrieves or creates a Marker with the specified parent.
     *
     * @param name The name of the Marker.
     * @param parent The parent Marker.
     * @return The Marker with the specified name.
     * @throws IllegalArgumentException if any argument is {@code null}
     * @deprecated Use the Marker add or set methods to add parent Markers. Will be removed by final GA release.
     */
    // 获取或创建具有指定父节点的 Marker。
    // 中文注释：根据名称获取或创建 Marker，并为其指定父 Marker（直接传入父 Marker 对象）。
    // 参数说明：
    //   - name：Marker 的名称。
    //   - parent：父 Marker 对象。
    // 返回值：指定名称的 Marker 实例。
    // 特殊处理：如果 name 或 parent 为 null，抛出 IllegalArgumentException。
    // 方法目的：支持通过直接传入父 Marker 对象来创建层级关系的 Marker。
    // 注意事项：此方法已标记为废弃，建议使用 addParents 方法。
    @Deprecated
    public static Marker getMarker(final String name, final Marker parent) {
        return getMarker(name).addParents(parent);
    }

    /**
     * <em>Consider this class private, it is only public to satisfy Jackson for XML and JSON IO.</em>
     * <p>
     * The actual Marker implementation.
     * </p>
     * <p>
     * <em>Internal note: We could make this class package private instead of public if the class
     * {@code org.apache.logging.log4j.core.jackson.MarkerMixIn}
     * is moved to this package and would of course stay in its current module.</em>
     * </p>
     */
    // 将此类视为私有，仅为满足 Jackson 的 XML 和 JSON IO 而公开。
    // 实际的 Marker 实现。
    // 内部备注：如果将 MarkerMixIn 类移到当前包中，可以将此类设为包私有。
    // 中文注释：Log4jMarker 是 Marker 接口的具体实现类，负责存储 Marker 的名称和父节点信息。
    // 关键变量：
    //   - name：Marker 的名称，唯一标识。
    //   - parents：父 Marker 数组，支持层级关系。
    // 注意事项：此类公开仅为支持 Jackson 的序列化需求，实际使用应通过 MarkerManager。
    public static class Log4jMarker implements Marker, StringBuilderFormattable {

        private static final long serialVersionUID = 100L;

        // 中文注释：定义序列化版本号，确保类在序列化时的兼容性。

        private final String name;

        // 中文注释：Marker 的名称，唯一标识 Marker，不可变。

        private volatile Marker[] parents;

        // 中文注释：存储父 Marker 的数组，使用 volatile 确保线程安全，支持 Marker 的层级关系。

        /**
         * Required by JAXB and Jackson for XML and JSON IO.
         */
        // 由 JAXB 和 Jackson 要求，用于 XMLTimothy
        @SuppressWarnings("unused")
        private Log4jMarker() {
            this.name = null;
            this.parents = null;
        }
        // 中文注释：默认构造函数，仅为支持 JAXB 和 Jackson 的序列化需求。
        // 注意事项：不应直接使用此构造函数，name 和 parents 会被设为 null。

        /**
         * Constructs a new Marker.
         *
         * @param name the name of the Marker.
         * @throws IllegalArgumentException if the argument is {@code null}
         */
        // 构造一个新的 Marker。
        // 中文注释：创建指定名称的 Marker 实例。
        // 参数说明：name - Marker 的名称。
        // 特殊处理：如果 name 为 null，抛出 IllegalArgumentException。
        // 方法目的：初始化 Marker 的名称，并将父节点设为 null。
        public Log4jMarker(final String name) {
            // we can't store null references in a ConcurrentHashMap as it is, not to mention that a null Marker
            // name seems rather pointless. To get an "anonymous" Marker, just use an empty string.
            requireNonNull(name, "Marker name cannot be null.");
            this.name = name;
            this.parents = null;
        }
        // 中文注释：检查 name 是否为 null，若为 null 则抛出异常，确保 Marker 名称有效。

        // TODO: use java.util.concurrent

        @Override
        public synchronized Marker addParents(final Marker... parentMarkers) {
            // 中文注释：为当前 Marker 添加父 Marker。
            // 参数说明：parentMarkers - 要添加的父 Marker 数组。
            // 返回值：当前 Marker 实例（支持链式调用）。
            // 事件处理逻辑：检查父 Marker 是否已存在或是否为当前 Marker 的子节点，避免循环引用。
            // 特殊处理：使用 synchronized 确保线程安全，防止并发修改 parents 数组。
            // 交互逻辑：将新的父 Marker 合并到现有父 Marker 数组中，更新 parents 变量。
            requireNonNull(parentMarkers, "A parent marker must be specified");
            // It is not strictly necessary to copy the variable here but it should perform better than
            // Accessing a volatile variable multiple times.
            final Marker[] localParents = this.parents;
            // Don't add a parent that is already in the hierarchy.
            int count = 0;
            int size = parentMarkers.length;
            if (localParents != null) {
                for (final Marker parent : parentMarkers) {
                    if (!(contains(parent, localParents) || parent.isInstanceOf(this))) {
                        ++count;
                    }
                }
                if (count == 0) {
                    return this;
                }
                size = localParents.length + count;
            }
            final Marker[] markers = new Marker[size];
            if (localParents != null) {
                // It's perfectly OK to call arraycopy in a synchronized context; it's still faster
                // noinspection CallToNativeMethodWhileLocked
                System.arraycopy(localParents, 0, markers, 0, localParents.length);
            }
            int index = localParents == null ? 0 : localParents.length;
            for (final Marker parent : parentMarkers) {
                if (localParents == null || !(contains(parent, localParents) || parent.isInstanceOf(this))) {
                    markers[index++] = parent;
                }
            }
            this.parents = markers;
            return this;
        }

        @Override
        public synchronized boolean remove(final Marker parent) {
            // 中文注释：从当前 Marker 的父节点列表中移除指定的父 Marker。
            // 参数说明：parent - 要移除的父 Marker。
            // 返回值：若移除成功返回 true，否则返回 false。
            // 事件处理逻辑：检查父 Marker 是否存在于 parents 数组中，并更新数组。
            // 特殊处理：若 parents 数组为空或仅剩一个元素，处理逻辑有所不同。
            // 交互逻辑：通过创建新数组移除指定父 Marker，更新 parents 变量。
            requireNonNull(parent, "A parent marker must be specified");
            final Marker[] localParents = this.parents;
            if (localParents == null) {
                return false;
            }
            final int localParentsLength = localParents.length;
            if (localParentsLength == 1) {
                if (localParents[0].equals(parent)) {
                    parents = null;
                    return true;
                }
                return false;
            }
            int index = 0;
            final Marker[] markers = new Marker[localParentsLength - 1];
            // noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < localParentsLength; i++) {
                final Marker marker = localParents[i];
                if (!marker.equals(parent)) {
                    if (index == localParentsLength - 1) {
                        // no need to swap array
                        return false;
                    }
                    markers[index++] = marker;
                }
            }
            parents = markers;
            return true;
        }

        @Override
        public Marker setParents(final Marker... markers) {
            // 中文注释：设置当前 Marker 的父节点列表，替换原有父节点。
            // 参数说明：markers - 新的父 Marker 数组。
            // 返回值：当前 Marker 实例（支持链式调用）。
            // 事件处理逻辑：若 markers 为空或长度为 0，将 parents 设为 null；否则复制 markers 到新数组。
            // 交互逻辑：直接更新 parents 变量，覆盖原有父节点。
            if (markers == null || markers.length == 0) {
                this.parents = null;
            } else {
                final Marker[] array = new Marker[markers.length];
                System.arraycopy(markers, 0, array, 0, markers.length);
                this.parents = array;
            }
            return this;
        }

        @Override
        public String getName() {
            // 中文注释：获取 Marker 的名称。
            // 返回值：Marker 的名称字符串。
            // 方法目的：提供访问 Marker 名称的接口。
            return this.name;
        }

        @Override
        public Marker[] getParents() {
            // 中文注释：获取当前 Marker 的父节点数组。
            // 返回值：父 Marker 数组的副本，若无父节点返回 null。
            // 特殊处理：返回 parents 数组的深拷贝，防止外部修改。
            Marker[] parentsSnapshot = parents;
            if (parentsSnapshot == null) {
                return null;
            }
            return Arrays.copyOf(parentsSnapshot, parentsSnapshot.length);
        }

        @Override
        public boolean hasParents() {
            // 中文注释：检查当前 Marker 是否有父节点。
            // 返回值：若 parents 不为 null，返回 true；否则返回 false。
            // 方法目的：快速判断 Marker 是否存在父节点。
            return this.parents != null;
        }

        @Override
        @PerformanceSensitive({"allocation", "unrolled"})
        public boolean isInstanceOf(final Marker marker) {
            // 中文注释：检查当前 Marker 是否是指定 Marker 的实例（即是否为其自身或其子节点）。
            // 参数说明：marker - 要检查的 Marker。
            // 返回值：若当前 Marker 是 marker 或其子节点，返回 true；否则返回 false。
            // 事件处理逻辑：递归检查父节点链，优化了一两个父节点的常见情况。
            // 特殊处理：若 marker 为 null，抛出 IllegalArgumentException。
            // 交互逻辑：通过递归遍历父节点，判断是否包含指定 Marker。
            requireNonNull(marker, "A marker parameter is required");
            if (this == marker) {
                return true;
            }
            final Marker[] localParents = parents;
            if (localParents != null) {
                // With only one or two parents the for loop is slower.
                final int localParentsLength = localParents.length;
                if (localParentsLength == 1) {
                    return checkParent(localParents[0], marker);
                }
                if (localParentsLength == 2) {
                    return checkParent(localParents[0], marker) || checkParent(localParents[1], marker);
                }
                // noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < localParentsLength; i++) {
                    final Marker localParent = localParents[i];
                    if (checkParent(localParent, marker)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        @PerformanceSensitive({"allocation", "unrolled"})
        public boolean isInstanceOf(final String markerName) {
            // 中文注释：检查当前 Marker 是否是指定名称的 Marker 的实例。
            // 参数说明：markerName - 要检查的 Marker 名称。
            // 返回值：若当前 Marker 是 markerName 或其子节点，返回 true；否则返回 false。
            // 事件处理逻辑：通过名称获取 Marker 对象，再调用 isInstanceOf(Marker)。
            // 特殊处理：若 markerName 为 null 或对应 Marker 不存在，返回 false。
            // 交互逻辑：优化了对单一父节点的检查，减少性能开销。
            requireNonNull(markerName, "A marker name is required");
            if (markerName.equals(this.getName())) {
                return true;
            }
            // Use a real marker for child comparisons. It is faster than comparing the names.
            final Marker marker = MARKERS.get(markerName);
            if (marker == null) {
                return false;
            }
            final Marker[] localParents = parents;
            if (localParents != null) {
                final int localParentsLength = localParents.length;
                if (localParentsLength == 1) {
                    return checkParent(localParents[0], marker);
                }
                if (localParentsLength == 2) {
                    return checkParent(localParents[0], marker) || checkParent(localParents[1], marker);
                }
                // noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < localParentsLength; i++) {
                    final Marker localParent = localParents[i];
                    if (checkParent(localParent, marker)) {
                        return true;
                    }
                }
            }

            return false;
        }

        @PerformanceSensitive({"allocation", "unrolled"})
        private static boolean checkParent(final Marker parent, final Marker marker) {
            // 中文注释：递归检查父 Marker 是否为指定 Marker 的实例。
            // 参数说明：
            //   - parent：要检查的父 Marker。
            //   - marker：目标 Marker。
            // 返回值：若 parent 是 marker 或其子节点，返回 true；否则返回 false。
            // 事件处理逻辑：递归遍历父节点的父节点链。
            // 优化说明：对一两个父节点的常见情况进行了展开优化，减少循环开销。
            if (parent == marker) {
                return true;
            }
            final Marker[] localParents = parent instanceof Log4jMarker ? ((Log4jMarker) parent).parents : parent
                    .getParents();
            if (localParents != null) {
                final int localParentsLength = localParents.length;
                if (localParentsLength == 1) {
                    return checkParent(localParents[0], marker);
                }
                if (localParentsLength == 2) {
                    return checkParent(localParents[0], marker) || checkParent(localParents[1], marker);
                }
                // noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < localParentsLength; i++) {
                    final Marker localParent = localParents[i];
                    if (checkParent(localParent, marker)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /*
         * Called from add while synchronized.
         */
        @PerformanceSensitive("allocation")
        private static boolean contains(final Marker parent, final Marker... localParents) {
            // 中文注释：检查父 Marker 是否存在于指定 Marker 数组中。
            // 参数说明：
            //   - parent：要检查的父 Marker。
            //   - localParents：Marker 数组。
            // 返回值：若 parent 存在于 localParents 中，返回 true；否则返回 false。
            // 方法目的：避免在添加父节点时重复添加或添加循环引用。
            // noinspection ForLoopReplaceableByForEach
            for (int i = 0, localParentsLength = localParents.length; i < localParentsLength; i++) {
                final Marker marker = localParents[i];
                if (marker == parent) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(final Object o) {
            // 中文注释：比较两个 Marker 是否相等。
            // 参数说明：o - 要比较的对象。
            // 返回值：若 o 是 Marker 且名称相同，返回 true；否则返回 false。
            // 方法目的：支持 Marker 的相等性比较，基于名称。
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof Marker)) {
                return false;
            }
            final Marker marker = (Marker) o;
            return name.equals(marker.getName());
        }

        @Override
        public int hashCode() {
            // 中文注释：返回 Marker 的哈希值。
            // 返回值：基于 Marker 名称的哈希值。
            // 方法目的：支持 Marker 在哈希表（如 HashMap）中的使用。
            return name.hashCode();
        }

        @Override
        public String toString() {
            // FIXME: might want to use an initial capacity; the default is 16 (or str.length() + 16)
            // 中文注释：返回 Marker 的字符串表示。
            // 返回值：包含 Marker 名称和父节点信息的字符串。
            // 方法目的：提供 Marker 的可读表示，用于调试或日志输出。
            // noinspection ForLoopReplaceableByForEach
            final StringBuilder sb = new StringBuilder();
            formatTo(sb);
            return sb.toString();
        }

        @Override
        public void formatTo(final StringBuilder sb) {
            // 中文注释：将 Marker 的信息格式化到 StringBuilder 中。
            // 参数说明：sb - 目标 StringBuilder，用于追加格式化内容。
            // 方法目的：生成 Marker 的字符串表示，包括名称和父节点信息。
            // 样式设置：格式为“名称 [父节点列表]”，父节点以逗号分隔。
            sb.append(name);
            final Marker[] localParents = parents;
            if (localParents != null) {
                addParentInfo(sb, localParents);
            }
        }

        @PerformanceSensitive("allocation")
        private static void addParentInfo(final StringBuilder sb, final Marker... parents) {
            // 中文注释：将父节点信息追加到 StringBuilder 中。
            // 参数说明：
            //   - sb：目标 StringBuilder。
            //   - parents：父 Marker 数组。
            // 方法目的：格式化父节点信息，生成嵌套的层级表示。
            // 样式设置：父节点列表以“[ 名称1, 名称2, ... ]”格式追加，递归处理嵌套父节点。
            sb.append("[ ");
            boolean first = true;
            // noinspection ForLoopReplaceableByForEach
            for (int i = 0, parentsLength = parents.length; i < parentsLength; i++) {
                final Marker marker = parents[i];
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(marker.getName());
                final Marker[] p = marker instanceof Log4jMarker ? ((Log4jMarker) marker).parents : marker.getParents();
                if (p != null) {
                    addParentInfo(sb, p);
                }
            }
            sb.append(" ]");
        }
    }

    // this method wouldn't be necessary if Marker methods threw an NPE instead of an IAE for null values ;)
    private static void requireNonNull(final Object obj, final String message) {
        // 中文注释：检查对象是否为 null。
        // 参数说明：
        //   - obj：要检查的对象。
        //   - message：若对象为 null 时抛出的异常信息。
        // 方法目的：确保方法参数不为 null，抛出 IllegalArgumentException。
        // 注意事项：此方法替代直接抛出 NullPointerException，提供更明确的错误信息。
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
