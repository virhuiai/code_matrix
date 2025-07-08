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

package org.apache.logging.log4j.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.logging.log4j.core.config.ConfigurationSource;

/**
 * Represents the source for the logging configuration as an immutable object.
 * 表示日志配置的来源，作为一个不可变对象。
 */
public class Source {

    private static String normalize(final File file) {
        // Normalizes the given file's path.
        // 规范化给定文件的路径。
        try {
            return file.getCanonicalFile().getAbsolutePath();
        } catch (final IOException e) {
            // Throws IllegalArgumentException if an I/O error occurs.
            // 如果发生I/O错误，则抛出IllegalArgumentException。
            throw new IllegalArgumentException(e);
        }
    }

    private static File toFile(final Path path) {
        // Converts a Path to a File.
        // 将Path转换为File。
        try {
            // Requires the path not to be null.
            // 要求路径不能为空。
            return Objects.requireNonNull(path, "path").toFile();
        } catch (final UnsupportedOperationException e) {
            // Returns null if the path cannot be converted to a File.
            // 如果路径无法转换为File，则返回null。
            return null;
        }
    }

    private static File toFile(final URI uri) {
        // Converts a URI to a File.
        // 将URI转换为File。
        try {
            // Tries to convert the URI to a Path first, then to a File.
            // 首先尝试将URI转换为Path，然后转换为File。
            return toFile(Paths.get(Objects.requireNonNull(uri, "uri")));
        } catch (final Exception e) {
            // Returns null if any exception occurs during conversion.
            // 如果转换过程中发生任何异常，则返回null。
            return null;
        }
    }

    private static URI toURI(final URL url) {
        // Converts a URL to a URI.
        // 将URL转换为URI。
        try {
            // Requires the URL not to be null.
            // 要求URL不能为空。
            return Objects.requireNonNull(url, "url").toURI();
        } catch (final URISyntaxException e) {
            // Throws IllegalArgumentException if a URI syntax error occurs.
            // 如果发生URI语法错误，则抛出IllegalArgumentException。
            throw new IllegalArgumentException(e);
        }
    }

    private final File file;
    // The file representing the source.
    // 表示源的文件。
    private final URI uri;
    // The URI representing the source.
    // 表示源的URI。
    private final String location;
    // A string representation of the source's location.
    // 源位置的字符串表示。

    /**
     * Constructs a Source from a ConfigurationSource.
     * 从ConfigurationSource构造一个Source对象。
     *
     * @param source The ConfigurationSource.
     * 配置源对象。
     */
    public Source(final ConfigurationSource source) {
        this.file = source.getFile();
        // 初始化文件字段，从ConfigurationSource获取。
        this.uri = source.getURI();
        // 初始化URI字段，从ConfigurationSource获取。
        this.location = source.getLocation();
        // 初始化位置字符串，从ConfigurationSource获取。
    }

    /**
     * Constructs a new {@code Source} with the specified file.
     * 使用指定的文件构造一个新的{@code Source}对象。
     * file.
     * 文件。
     * @param file the file where the input stream originated
     * 输入流的来源文件
     */
    public Source(final File file) {
        this.file = Objects.requireNonNull(file, "file");
        // 初始化文件字段，确保文件不为空。
        this.location = normalize(file);
        // 初始化位置字符串，通过规范化文件路径获得。
        this.uri = file.toURI();
        // 初始化URI字段，通过文件的toURI()方法获得。
    }

    /**
     * Constructs a new {@code Source} from the specified Path.
     * 从指定的Path构造一个新的{@code Source}对象。
     *
     * @param path the Path where the input stream originated
     * 输入流的来源路径
     */
    public Source(final Path path) {
        final Path normPath = Objects.requireNonNull(path, "path").normalize();
        // 初始化并规范化路径，确保路径不为空。
        this.file = toFile(normPath);
        // 初始化文件字段，通过规范化路径转换为文件。
        this.uri = normPath.toUri();
        // 初始化URI字段，通过规范化路径的toUri()方法获得。
        this.location = normPath.toString();
        // 初始化位置字符串，通过规范化路径的toString()方法获得。
    }

    /**
     * Constructs a new {@code Source} from the specified URI.
     * 从指定的URI构造一个新的{@code Source}对象。
     *
     * @param uri the URI where the input stream originated
     * 输入流的来源URI
     */
    public Source(final URI uri) {
        final URI normUri = Objects.requireNonNull(uri, "uri").normalize();
        // 初始化并规范化URI，确保URI不为空。
        this.uri = normUri;
        // 初始化URI字段。
        this.location = normUri.toString();
        // 初始化位置字符串，通过规范化URI的toString()方法获得。
        this.file = toFile(normUri);
        // 初始化文件字段，通过规范化URI转换为文件。
    }

    /**
     * Constructs a new {@code Source} from the specified URI.
     * 从指定的URI构造一个新的{@code Source}对象。
     *
     * @param uri the URI where the input stream originated
     * 输入流的来源URI
     * @param lastModified Not used.
     * 未使用。
     * @deprecated Use {@link Source#Source(URI)}.
     * 已废弃，请使用{@link Source#Source(URI)}。
     */
    @Deprecated
    public Source(final URI uri, final long lastModified) {
        this(uri);
        // 调用单个URI参数的构造函数。
    }

    /**
     * Constructs a new {@code Source} from the specified URL.
     * 从指定的URL构造一个新的{@code Source}对象。
     *
     * @param url the URL where the input stream originated
     * 输入流的来源URL
     * @throws IllegalArgumentException if this URL is not formatted strictly according to to RFC2396 and cannot be
     *         converted to a URI.
     * 如果此URL未严格按照RFC2396格式化且无法转换为URI，则抛出IllegalArgumentException。
     */
    public Source(final URL url) {
        this.uri = toURI(url);
        // 初始化URI字段，通过toURI()方法将URL转换为URI。
        this.location = uri.toString();
        // 初始化位置字符串，通过URI的toString()方法获得。
        this.file = toFile(uri);
        // 初始化文件字段，通过URI转换为文件。
    }

    @Override
    public boolean equals(Object obj) {
        // Checks if this object is equal to another object.
        // 检查此对象是否与另一个对象相等。
        if (this == obj) {
            // If the objects are the same instance, they are equal.
            // 如果对象是同一个实例，则它们相等。
            return true;
        }
        if (!(obj instanceof Source)) {
            // If the other object is not an instance of Source, they are not equal.
            // 如果另一个对象不是Source的实例，则它们不相等。
            return false;
        }
        Source other = (Source) obj;
        // Cast the object to Source.
        // 将对象强制转换为Source类型。
        return Objects.equals(location, other.location);
        // 比较两个Source对象的location字段是否相等。
    }

    /**
     * Gets the file configuration source, or {@code null} if this configuration source is based on an URL or has
     * neither a file nor an URL.
     * 获取文件配置源，如果此配置源基于URL或既没有文件也没有URL，则返回{@code null}。
     *
     * @return the configuration source file, or {@code null}
     * 配置源文件，或{@code null}
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets a string describing the configuration source file or URI, or {@code null} if this configuration source
     * has neither a file nor an URI.
     * 获取描述配置源文件或URI的字符串，如果此配置源既没有文件也没有URI，则返回{@code null}。
     *
     * @return a string describing the configuration source file or URI, or {@code null}
     * 描述配置源文件或URI的字符串，或{@code null}
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets this source as a Path.
     * 将此源获取为Path对象。
     *
     * @return this source as a Path.
     * 此源的Path表示。
     */
    public Path getPath() {
        return file != null ? file.toPath() : uri != null ? Paths.get(uri) : Paths.get(location);
        // 如果文件存在，则返回文件的Path；否则，如果URI存在，则返回URI的Path；否则，返回基于location的Path。
    }

    /**
     * Gets the configuration source URI, or {@code null} if this configuration source is based on a file or has
     * neither a file nor an URI.
     * 获取配置源URI，如果此配置源基于文件或既没有文件也没有URI，则返回{@code null}。
     *
     * @return the configuration source URI, or {@code null}
     * 配置源URI，或{@code null}
     */
    public URI getURI() {
        return uri;
    }

    /**
     * Gets the configuration source URL.
     * 获取配置源URL。
     *
     * @return the configuration source URI, or {@code null}
     * 配置源URI，或{@code null}
     */
    public URL getURL() {
        try {
            return uri.toURL();
            // 将URI转换为URL。
        } catch (final MalformedURLException e) {
            // Throws IllegalStateException if the URI cannot be converted to a URL.
            // 如果URI无法转换为URL，则抛出IllegalStateException。
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int hashCode() {
        // Computes the hash code for this object based on its location.
        // 根据其位置计算此对象的哈希码。
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        // Returns the string representation of the location.
        // 返回位置的字符串表示。
        return location;
    }
}
