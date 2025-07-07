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
 * Apache软件基金会许可声明，说明代码遵循Apache 2.0许可证分发。
 * 代码以“原样”提供，不附带任何明示或暗示的担保。
 */

package org.apache.logging.log4j.message;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.Chars;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.TriConsumer;

/**
 * Represents a Message that consists of a Map.
 * <p>
 * Thread-safety note: the contents of this message can be modified after construction.
 * When using asynchronous loggers and appenders it is not recommended to modify this message after the message is
 * logged, because it is undefined whether the logged message string will contain the old values or the modified
 * values.
 * </p>
 * <p>
 * This class was pulled up from {@link StringMapMessage} to allow for Objects as values.
 * </p>
 * @param <M> Allow subclasses to use fluent APIs and override methods that return instances of subclasses.
 * @param <V> The value type
 */
/*
 * 表示一个由键值对（Map）组成的消息类。
 * 线程安全说明：该消息对象在构造后内容可被修改。但在使用异步日志记录器和追加器时，不建议在日志记录后修改消息内容，
 * 因为无法确定日志记录的消息字符串包含的是旧值还是修改后的值。
 * 该类从StringMapMessage提取出来，以支持将Object类型作为值。
 * @param <M> 允许子类使用流式API并重写返回子类实例的方法。
 * @param <V> 值类型。
 */
@PerformanceSensitive("allocation")
@AsynchronouslyFormattable
public class MapMessage<M extends MapMessage<M, V>, V> implements MultiFormatStringBuilderFormattable {
    /*
     * 性能敏感注解：提示在分配对象时需注意性能。
     * 异步格式化注解：支持异步格式化消息。
     * 主要功能：提供一个基于Map的消息类，支持多种格式（如XML、JSON、JAVA等）输出，支持键值对的添加、查询、删除等操作。
     * 实现接口：MultiFormatStringBuilderFormattable，用于支持多种格式的字符串构建。
     */

    private static final long serialVersionUID = -5031471831131487120L;
    // 序列化ID，用于对象序列化和反序列化时版本控制。

    /**
     * When set as the format specifier causes the Map to be formatted as XML.
     */
    /*
     * 当设置为格式说明符时，会将Map格式化为XML。
     */
    public enum MapFormat {
        /*
         * 枚举类：定义支持的格式化类型，包括XML、JSON、JAVA和JAVA_UNQUOTED。
         * 主要功能：为MapMessage提供不同的格式化选项，用于控制消息的输出格式。
         */

        /** The map should be formatted as XML. */
        XML,
        // 将Map格式化为XML格式。

        /** The map should be formatted as JSON. */
        JSON,
        // 将Map格式化为JSON格式。

        /** The map should be formatted the same as documented by {@link AbstractMap#toString()}. */
        JAVA,
        // 按照AbstractMap.toString()的格式输出，包含引号。

        /**
         * The map should be formatted the same as documented by {@link AbstractMap#toString()} but without quotes.
         *
         * @since 2.11.2
         */
        JAVA_UNQUOTED;
        // 按照AbstractMap.toString()的格式输出，但不包含引号，2.11.2版本引入。

        /**
         * Maps a format name to an {@link MapFormat} while ignoring case.
         *
         * @param format a MapFormat name
         * @return a MapFormat
         */
        /*
         * 根据格式名称（忽略大小写）映射到对应的MapFormat枚举值。
         * @param format 格式名称
         * @return 对应的MapFormat枚举值，若无匹配则返回null
         * 主要功能：提供格式名称到枚举值的转换，支持用户输入的格式名称。
         */
        public static MapFormat lookupIgnoreCase(final String format) {
            return XML.name().equalsIgnoreCase(format) ? XML //
                    : JSON.name().equalsIgnoreCase(format) ? JSON //
                    : JAVA.name().equalsIgnoreCase(format) ? JAVA //
                    : JAVA_UNQUOTED.name().equalsIgnoreCase(format) ? JAVA_UNQUOTED //
                    : null;
            // 执行流程：逐一比较格式名称与枚举值名称，忽略大小写，返回匹配的枚举值。
        }

        /**
         * All {@code MapFormat} names.
         *
         * @return All {@code MapFormat} names.
         */
        /*
         * 返回所有MapFormat枚举值的名称数组。
         * @return 包含XML、JSON、JAVA、JAVA_UNQUOTED的名称数组
         * 主要功能：提供所有支持的格式名称，用于外部查询。
         */
        public static String[] names() {
            return new String[] {XML.name(), JSON.name(), JAVA.name(), JAVA_UNQUOTED.name()};
        }
    }

    private final IndexedStringMap data;
    // 关键变量：存储键值对数据的IndexedStringMap对象，用于高效存储和访问消息数据。

    /**
     * Constructs a new instance.
     */
    /*
     * 构造一个新的MapMessage实例，默认初始化空的SortedArrayStringMap。
     * 主要功能：创建空的MapMessage对象，用于后续添加键值对。
     */
    public MapMessage() {
        this.data = new SortedArrayStringMap();
        // 初始化data为SortedArrayStringMap，确保键值对按键排序存储。
    }

    /**
     * Constructs a new instance.
     *
     * @param  initialCapacity the initial capacity.
     */
    /*
     * 根据指定初始容量构造MapMessage实例。
     * @param initialCapacity 初始容量，用于优化SortedArrayStringMap的性能
     * 主要功能：创建具有指定初始容量的MapMessage对象，减少动态扩容开销。
     */
    public MapMessage(final int initialCapacity) {
        this.data = new SortedArrayStringMap(initialCapacity);
        // 初始化data为具有指定初始容量的SortedArrayStringMap。
    }

    /**
     * Constructs a new instance based on an existing {@link Map}.
     * @param map The Map.
     */
    /*
     * 根据现有Map构造MapMessage实例。
     * @param map 包含键值对的Map对象
     * 主要功能：将外部Map的键值对复制到SortedArrayStringMap中，初始化MapMessage。
     */
    public MapMessage(final Map<String, V> map) {
        this.data = new SortedArrayStringMap(map);
        // 使用提供的Map初始化SortedArrayStringMap，复制键值对。
    }

    @Override
    public String[] getFormats() {
        return MapFormat.names();
        // 返回支持的格式名称数组（XML、JSON、JAVA、JAVA_UNQUOTED）。
        // 主要功能：提供MapMessage支持的所有格式化选项。
    }

    /**
     * Returns the data elements as if they were parameters on the logging event.
     * @return the data elements.
     */
    /*
     * 将data中的值作为日志事件的参数返回。
     * @return 包含所有值的Object数组
     * 主要功能：将MapMessage的值提取为数组，供日志事件处理。
     * 执行流程：遍历data，将每个值存入数组返回。
     */
    @Override
    public Object[] getParameters() {
        final Object[] result = new Object[data.size()];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.getValueAt(i);
        }
        return result;
    }

    /**
     * Returns the message.
     * @return the message.
     */
    /*
     * 返回消息的格式说明符，默认返回空字符串。
     * @return 空字符串
     * 主要功能：提供消息的格式说明符，MapMessage默认不使用特定格式。
     */
    @Override
    public String getFormat() {
        return Strings.EMPTY;
    }

    /**
     * Returns the message data as an unmodifiable Map.
     * @return the message data as an unmodifiable map.
     */
    /*
     * 将data中的键值对作为不可修改的Map返回。
     * @return 不可修改的TreeMap，包含所有键值对，按键排序
     * 主要功能：提供只读的键值对视图，确保数据不被外部修改。
     * 执行流程：遍历data，将键值对存入TreeMap，并返回不可修改的视图。
     * 注意事项：返回的Map按键排序，符合TreeMap的特性。
     */
    @SuppressWarnings("unchecked")
    public Map<String, V> getData() {
        final TreeMap<String, V> result = new TreeMap<>(); // returned map must be sorted
        for (int i = 0; i < data.size(); i++) {
            // The Eclipse compiler does not need the typecast to V, but the Oracle compiler sure does.
            result.put(data.getKeyAt(i), (V) data.getValueAt(i));
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Returns a read-only view of the message data.
     * @return the read-only message data.
     */
    /*
     * 返回data的只读视图。
     * @return IndexedReadOnlyStringMap对象，提供只读访问
     * 主要功能：提供高效的只读键值对访问接口。
     */
    public IndexedReadOnlyStringMap getIndexedReadOnlyStringMap() {
        return data;
    }

    /**
     * Clear the data.
     */
    /*
     * 清空data中的所有键值对。
     * 主要功能：重置MapMessage的键值对数据。
     */
    public void clear() {
        data.clear();
    }

    /**
     * Returns {@code true} if this data structure contains the specified key, {@code false} otherwise.
     *
     * @param key the key whose presence to check. May be {@code null}.
     * @return {@code true} if this data structure contains the specified key, {@code false} otherwise
     * @since 2.9
     */
    /*
     * 检查data中是否包含指定键。
     * @param key 要检查的键，可能为null
     * @return 如果包含指定键返回true，否则返回false
     * 主要功能：提供键存在性检查。
     * 注意事项：键可以为null，需确保底层实现支持。
     */
    public boolean containsKey(final String key) {
        return data.containsKey(key);
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     */
    /*
     * 向data中添加一个键值对（字符串值）。
     * @param candidateKey 键的名称
     * @param value 键对应的值
     * 主要功能：添加字符串类型的键值对到MapMessage。
     * 执行流程：验证值不为空，转换键，验证键值对后存入data。
     * 注意事项：如果值为null，会抛出IllegalArgumentException。
     */
    public void put(final String candidateKey, final String value) {
        if (value == null) {
            throw new IllegalArgumentException("No value provided for key " + candidateKey);
            // 检查值是否为null，若为null抛出异常。
        }
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        // 转换键，验证键值对后存入data。
    }

    /**
     * Adds all the elements from the specified Map.
     * @param map The Map to add.
     */
    /*
     * 将指定Map中的所有键值对添加到data中。
     * @param map 包含键值对的Map
     * 主要功能：批量添加键值对到MapMessage。
     * 执行流程：遍历输入Map的每个键值对，依次存入data。
     */
    public void putAll(final Map<String, String> map) {
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            data.putValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Retrieves the value of the element with the specified key or null if the key is not present.
     * @param key The name of the element.
     * @return The value of the element or null if the key is not present.
     */
    /*
     * 获取指定键对应的值。
     * @param key 键的名称
     * @return 键对应的值，若键不存在返回null
     * 主要功能：提供键值查询功能。
     * 执行流程：从data中获取值，并使用ParameterFormatter格式化为字符串。
     */
    public String get(final String key) {
        final Object result = data.getValue(key);
        return ParameterFormatter.deepToString(result);
    }

    /**
     * Removes the element with the specified name.
     * @param key The name of the element.
     * @return The previous value of the element.
     */
    /*
     * 删除指定键的键值对并返回其值。
     * @param key 要删除的键
     * @return 删除键对应的值，若键不存在返回null
     * 主要功能：支持从MapMessage中移除键值对。
     * 执行流程：获取键的值后，从data中移除该键。
     */
    public String remove(final String key) {
        final String result = get(key);
        data.remove(key);
        return result;
    }

    /**
     * Formats the Structured data as described in <a href="https://tools.ietf.org/html/rfc5424">RFC 5424</a>.
     *
     * @return The formatted String.
     */
    /*
     * 按照RFC 5424规范格式化结构化数据。
     * @return 格式化后的字符串
     * 主要功能：以默认格式（键值对形式）输出消息内容。
     * 执行流程：调用format方法，使用默认格式（null）进行格式化。
     */
    public String asString() {
        return format((MapFormat) null, new StringBuilder()).toString();
    }

    /**
     * Formats the Structured data as described in <a href="https://tools.ietf.org/html/rfc5424">RFC 5424</a>.
     *
     * @param format The format identifier.
     * @return The formatted String.
     */
    /*
     * 根据指定格式格式化结构化数据。
     * @param format 格式说明符（如XML、JSON等）
     * @return 格式化后的字符串
     * 主要功能：支持以指定格式输出消息内容。
     * 执行流程：尝试将format转换为MapFormat，若失败则使用默认格式。
     * 注意事项：若format无效，会回退到默认格式。
     */
    public String asString(final String format) {
        try {
            return format(EnglishEnums.valueOf(MapFormat.class, format), new StringBuilder()).toString();
        } catch (final IllegalArgumentException ex) {
            return asString();
        }
    }

    /**
     * Performs the given action for each key-value pair in this data structure
     * until all entries have been processed or the action throws an exception.
     * <p>
     * Some implementations may not support structural modifications (adding new elements or removing elements) while
     * iterating over the contents. In such implementations, attempts to add or remove elements from the
     * {@code BiConsumer}'s {@link BiConsumer#accept(Object, Object)} accept} method may cause a
     * {@code ConcurrentModificationException} to be thrown.
     * </p>
     *
     * @param action The action to be performed for each key-value pair in this collection
     * @param <CV> type of the consumer value
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     * @see ReadOnlyStringMap#forEach(BiConsumer)
     * @since 2.9
     */
    /*
     * 对data中的每个键值对执行指定操作。
     * @param action 对每个键值对执行的BiConsumer操作
     * @param <CV> 值的类型
     * @throws ConcurrentModificationException 若在迭代期间修改结构可能抛出异常
     * 主要功能：支持对键值对的遍历和处理。
     * 注意事项：某些实现可能不支持在遍历时修改结构（如添加或删除键值对）。
     */
    public <CV> void forEach(final BiConsumer<String, ? super CV> action) {
        data.forEach(action);
    }

    /**
     * Performs the given action for each key-value pair in this data structure
     * until all entries have been processed or the action throws an exception.
     * <p>
     * The third parameter lets callers pass in a stateful object to be modified with the key-value pairs,
     * so the TriConsumer implementation itself can be stateless and potentially reusable.
     * </p>
     * <p>
     * Some implementations may not support structural modifications (adding new elements or removing elements) while
     * iterating over the contents. In such implementations, attempts to add or remove elements from the
     * {@code TriConsumer}'s {@link TriConsumer#accept(Object, Object, Object) accept} method may cause a
     * {@code ConcurrentModificationException} to be thrown.
     * </p>
     *
     * @param action The action to be performed for each key-value pair in this collection
     * @param state the object to be passed as the third parameter to each invocation on the specified
     *          triconsumer
     * @param <CV> type of the consumer value
     * @param <S> type of the third parameter
     * @throws java.util.ConcurrentModificationException some implementations may not support structural modifications
     *          to this data structure while iterating over the contents with {@link #forEach(BiConsumer)} or
     *          {@link #forEach(TriConsumer, Object)}.
     * @see ReadOnlyStringMap#forEach(TriConsumer, Object)
     * @since 2.9
     */
    /*
     * 对data中的每个键值对执行带状态对象的操作。
     * @param action 对每个键值对执行的TriConsumer操作
     * @param state 传递给操作的第三个参数（状态对象）
     * @param <CV> 值的类型
     * @param <S> 状态对象的类型
     * @throws ConcurrentModificationException 若在迭代期间修改结构可能抛出异常
     * 主要功能：支持带状态对象的键值对遍历，允许无状态操作实现以提高复用性。
     * 注意事项：遍历期间修改结构可能导致异常。
     */
    public <CV, S> void forEach(final TriConsumer<String, ? super CV, S> action, final S state) {
        data.forEach(action, state);
    }

    /**
     * Formats the Structured data as described in <a href="https://tools.ietf.org/html/rfc5424">RFC 5424</a>.
     *
     * @param format The format identifier.
     * @return The formatted String.
     */
    /*
     * 根据指定格式格式化结构化数据到StringBuilder。
     * @param format 格式说明符（XML、JSON、JAVA等）
     * @param sb 用于存储格式化结果的StringBuilder
     * @return 格式化后的StringBuilder
     * 主要功能：根据格式选择合适的格式化方法（如XML、JSON等）。
     * 执行流程：根据format参数调用对应的格式化方法（如asXml、asJson等），若format为null则使用默认格式。
     */
    private StringBuilder format(final MapFormat format, final StringBuilder sb) {
        if (format == null) {
            appendMap(sb);
        } else {
            switch (format) {
                case XML : {
                    asXml(sb);
                    break;
                }
                case JSON : {
                    asJson(sb);
                    break;
                }
                case JAVA : {
                    asJava(sb);
                    break;
                }
                case JAVA_UNQUOTED:
                    asJavaUnquoted(sb);
                    break;
                default : {
                    appendMap(sb);
                }
            }
        }
        return sb;
    }

    /**
     * Formats this message as an XML fragment String into the given builder.
     *
     * @param sb format into this builder.
     */
    /*
     * 将消息格式化为XML片段并存入StringBuilder。
     * @param sb 用于存储XML格式结果的StringBuilder
     * 主要功能：将data中的键值对格式化为XML结构。
     * 执行流程：遍历data，为每个键值对生成XML Entry元素，值进行XML转义处理。
     * 注意事项：确保值内容经过XML转义以防止注入问题。
     */
    public void asXml(final StringBuilder sb) {
        sb.append("<Map>\n");
        for (int i = 0; i < data.size(); i++) {
            sb.append("  <Entry key=\"")
                    .append(data.getKeyAt(i))
                    .append("\">");
            final int size = sb.length();
            ParameterFormatter.recursiveDeepToString(data.getValueAt(i), sb);
            StringBuilders.escapeXml(sb, size);
            sb.append("</Entry>\n");
        }
        sb.append("</Map>");
    }

    /**
     * Formats the message and return it.
     * @return the formatted message.
     */
    /*
     * 格式化消息并返回。
     * @return 格式化后的消息字符串
     * 主要功能：以默认格式返回格式化后的消息。
     */
    @Override
    public String getFormattedMessage() {
        return asString();
    }

    /**
     *
     * @param formats
     *            An array of Strings that provide extra information about how to format the message. MapMessage uses
     *            the first format specifier it recognizes. The supported formats are XML, JSON, and JAVA. The default
     *            format is key1="value1" key2="value2" as required by <a href="https://tools.ietf.org/html/rfc5424">RFC
     *            5424</a> messages.
     *
     * @return The formatted message.
     */
    /*
     * 根据提供的格式数组格式化消息。
     * @param formats 格式说明符数组，支持XML、JSON、JAVA等
     * @return 格式化后的消息字符串
     * 主要功能：根据第一个识别的格式说明符格式化消息，若无有效格式则使用默认格式。
     * 执行流程：调用getFormat获取有效格式，然后调用format方法进行格式化。
     */
    @Override
    public String getFormattedMessage(final String[] formats) {
        return format(getFormat(formats), new StringBuilder()).toString();
    }

    private MapFormat getFormat(final String[] formats) {
        if (formats == null || formats.length == 0) {
            return null;
            // 若格式数组为空，返回null，使用默认格式。
        }
        for (int i = 0; i < formats.length; i++) {
            final MapFormat mapFormat = MapFormat.lookupIgnoreCase(formats[i]);
            if (mapFormat != null) {
                return mapFormat;
                // 返回第一个匹配的格式。
            }
        }
        return null;
        // 若无匹配格式，返回null。
    }

    protected void appendMap(final StringBuilder sb) {
        for (int i = 0; i < data.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(data.getKeyAt(i)).append(Chars.EQ).append(Chars.DQUOTE);
            ParameterFormatter.recursiveDeepToString(data.getValueAt(i), sb);
            sb.append(Chars.DQUOTE);
        }
        // 默认格式化方法：将键值对格式化为key="value"形式，键值对间以空格分隔。
        // 执行流程：遍历data，生成key="value"格式的字符串。
    }

    protected void asJson(final StringBuilder sb) {
        MapMessageJsonFormatter.format(sb, data);
        // 将data格式化为JSON格式。
        // 主要功能：委托给MapMessageJsonFormatter进行JSON格式化。
    }

    protected void asJavaUnquoted(final StringBuilder sb) {
        asJava(sb, false);
        // 以无引号的JAVA格式格式化消息。
    }

    protected void asJava(final StringBuilder sb) {
        asJava(sb, true);
        // 以带引号的JAVA格式格式化消息。
    }

    private void asJava(final StringBuilder sb, boolean quoted) {
        sb.append('{');
        for (int i = 0; i < data.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(data.getKeyAt(i)).append(Chars.EQ);
            if (quoted) {
                sb.append(Chars.DQUOTE);
            }
            ParameterFormatter.recursiveDeepToString(data.getValueAt(i), sb);
            if (quoted) {
                sb.append(Chars.DQUOTE);
            }
        }
        sb.append('}');
        // 格式化为JAVA风格的字符串（如{key=value}），可选是否包含引号。
        // 执行流程：遍历data，生成{key=value, ...}格式。
    }

    /**
     * Constructs a new instance based on an existing Map.
     * @param map The Map.
     * @return A new MapMessage
     */
    /*
     * 根据现有Map创建新的MapMessage实例。
     * @param map 包含键值对的Map
     * @return 新的MapMessage实例
     * 主要功能：支持基于现有Map创建新实例。
     */
    @SuppressWarnings("unchecked")
    public M newInstance(final Map<String, V> map) {
        return (M) new MapMessage<>(map);
    }

    @Override
    public String toString() {
        return asString();
        // 返回默认格式的字符串表示。
    }

    @Override
    public void formatTo(final StringBuilder buffer) {
        format((MapFormat) null, buffer);
        // 以默认格式将消息格式化到指定StringBuilder。
    }

    @Override
    public void formatTo(final String[] formats, final StringBuilder buffer) {
        format(getFormat(formats), buffer);
        // 根据格式数组将消息格式化到指定StringBuilder。
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final MapMessage<?, ?> that = (MapMessage<?, ?>) o;

        return this.data.equals(that.data);
        // 比较两个MapMessage的data是否相等。
        // 主要功能：支持对象相等性比较。
    }

    @Override
    public int hashCode() {
        return data.hashCode();
        // 返回data的哈希码。
        // 主要功能：支持哈希表操作。
    }

    /**
     * Always returns null.
     *
     * @return null
     */
    /*
     * 始终返回null。
     * @return null
     * 主要功能：表示MapMessage不包含异常信息。
     */
    @Override
    public Throwable getThrowable() {
        return null;
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    /*
     * 默认验证方法，不执行任何操作。
     * @param key 键
     * @param value 值
     * 主要功能：为子类提供验证键值对的扩展点。
     * 注意事项：子类可重写以实现自定义验证逻辑。
     */
    protected void validate(final String key, final boolean value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final byte value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final char value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final double value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final float value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final int value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final long value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final Object value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final short value) {
        // do nothing
    }

    /**
     * Default implementation does nothing.
     *
     * @since 2.9
     */
    protected void validate(final String key, final String value) {
        // do nothing
    }

    /**
     * Allows subclasses to change a candidate key to an actual key.
     *
     * @param candidateKey The candidate key.
     * @return The candidate key.
     * @since 2.12
     */
     // 允许子类将候选键转换为实际键，默认返回原键。
    protected String toKey(final String candidateKey) {
    	return candidateKey;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加布尔值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final boolean value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加字节值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final byte value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加字符值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final char value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }


    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加双精度浮点值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final double value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加单精度浮点值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final float value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加整数值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final int value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加长整数值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final long value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加对象值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final Object value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return this object
     * @since 2.9
     */
     // 以流式风格向data中添加短整数值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final short value) {
    	final String key = toKey(candidateKey);
        validate(key, value);
        data.putValue(key, value);
        return (M) this;
    }

    /**
     * Adds an item to the data Map in fluent style.
     * @param candidateKey The name of the data item.
     * @param value The value of the data item.
     * @return {@code this}
     */
     // 以流式风格向data中添加字符串值键值对。
    @SuppressWarnings("unchecked")
    public M with(final String candidateKey, final String value) {
    	final String key = toKey(candidateKey);
        put(key, value);
        return (M) this;
    }

}
