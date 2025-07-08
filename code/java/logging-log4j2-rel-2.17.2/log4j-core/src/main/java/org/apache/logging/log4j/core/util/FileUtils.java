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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * File utilities.
 * 文件工具类。
 */
public final class FileUtils {

    /** Constant for the file URL protocol. */
    // 文件URL协议的常量。
    private static final String PROTOCOL_FILE = "file";

    private static final String JBOSS_FILE = "vfsfile";

    private static final Logger LOGGER = StatusLogger.getLogger();

    private FileUtils() {
    }

    /**
     * Tries to convert the specified URI to a file object. If this fails, <b>null</b> is returned.
     * 尝试将指定的URI转换为文件对象。如果转换失败，则返回<b>null</b>。
     *
     * @param uri the URI
     * 要转换的URI
     * @return the resulting file object
     * 转换后的文件对象
     */
    public static File fileFromUri(URI uri) {
        // 如果URI为空，直接返回null
        if (uri == null) {
            return null;
        }
        // 如果URI是绝对路径
        if (uri.isAbsolute()) {
            // 如果URI的方案是JBOSS文件协议
            if (JBOSS_FILE.equals(uri.getScheme())) {
                try {
                    // patch the scheme
                    // 尝试将URI方案修改为“file”协议
                    uri = new URI(PROTOCOL_FILE, uri.getSchemeSpecificPart(), uri.getFragment());
                } catch (URISyntaxException use) {
                    // should not happen, ignore
                    // 不应该发生，忽略此异常
                }
            }
            try {
                // 如果URI的方案是“file”协议
                if (PROTOCOL_FILE.equals(uri.getScheme())) {
                    return new File(uri); // 直接通过URI创建File对象
                }
            } catch (final Exception ex) {
                LOGGER.warn("Invalid URI {}", uri); // 记录无效URI的警告
            }
        } else { // 如果URI是相对路径
            File file = new File(uri.toString()); // 尝试直接通过URI字符串创建File对象
            try {
                // 如果文件存在
                if (file.exists()) {
                    return file; // 返回该文件
                }
                final String path = uri.getPath(); // 获取URI的路径部分
                return new File(path); // 通过路径创建File对象
            } catch (final Exception ex) {
                LOGGER.warn("Invalid URI {}", uri); // 记录无效URI的警告
            }
        }
        return null; // 无法转换时返回null
    }

    /**
     * Checks if the given URL represents a file.
     * 检查给定的URL是否表示一个文件。
     * @param url the URL to check
     * 要检查的URL
     * @return true if the URL's protocol is 'file' or 'vfsfile', false otherwise
     * 如果URL的协议是“file”或“vfsfile”，则返回true，否则返回false
     */
    public static boolean isFile(final URL url) {
        return url != null && (url.getProtocol().equals(PROTOCOL_FILE) || url.getProtocol().equals(JBOSS_FILE));
    }

    /**
     * Retrieves the file extension from the given File.
     * 从给定的文件中获取文件扩展名。
     * @param file the File object
     * 文件对象
     * @return the file extension (e.g., "txt", "log"), or null if no extension is found
     * 文件扩展名（例如，“txt”，“log”），如果没有找到扩展名则返回null
     */
    public static String getFileExtension(final File file) {
        final String fileName = file.getName(); // 获取文件名
        // 检查文件名中是否存在“.”，且“.”不在文件名的开头
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1); // 返回“.”后面的子字符串作为扩展名
        }
        return null; // 如果没有扩展名则返回null
    }

    /**
     * Asserts that the given directory exists and creates it if necessary.
     * 断言给定目录存在，如果不存在则创建它。
     *
     * @param dir the directory that shall exist
     * 期望存在的目录
     * @param createDirectoryIfNotExisting specifies if the directory shall be created if it does not exist.
     * 指定如果目录不存在是否应该创建它。
     * @throws java.io.IOException thrown if the directory could not be created.
     * 如果无法创建目录，则抛出此异常。
     */
    public static void mkdir(final File dir, final boolean createDirectoryIfNotExisting) throws IOException {
        // commons io FileUtils.forceMkdir would be useful here, we just want to omit this dependency
        // 这里可以使用 commons io 的 FileUtils.forceMkdir，但我们希望避免引入此依赖
        if (!dir.exists()) { // 如果目录不存在
            if (!createDirectoryIfNotExisting) { // 如果不允许创建目录
                throw new IOException("The directory " + dir.getAbsolutePath() + " does not exist."); // 抛出目录不存在异常
            }
            if (!dir.mkdirs()) { // 尝试创建目录（包括所有不存在的父目录）
                throw new IOException("Could not create directory " + dir.getAbsolutePath()); // 抛出无法创建目录异常
            }
        }
        if (!dir.isDirectory()) { // 如果路径存在但不是一个目录
            throw new IOException("File " + dir + " exists and is not a directory. Unable to create directory."); // 抛出路径不是目录异常
        }
    }

    /**
     * Creates the parent directories for the given File.
     * 为给定文件创建父目录。
     *
     * @param file
     * 要为其创建父目录的文件
     * @throws IOException
     * 如果创建父目录时发生IO错误
     */
    public static void makeParentDirs(final File file) throws IOException {
        // 获取文件的规范路径的父目录
        final File parent = Objects.requireNonNull(file, "file").getCanonicalFile().getParentFile();
        if (parent != null) { // 如果父目录存在
            mkdir(parent, true); // 递归创建父目录
        }
    }

    /**
     * Define file POSIX attribute view on a path/file.
     * 在路径/文件上定义文件 POSIX 属性视图。
     *
     * @param path Target path
     * 目标路径
     * @param filePermissions Permissions to apply
     * 要应用的权限
     * @param fileOwner File owner
     * 文件所有者
     * @param fileGroup File group
     * 文件组
     * @throws IOException If IO error during definition of file attribute view
     * 如果在定义文件属性视图时发生IO错误
     */
    public static void defineFilePosixAttributeView(final Path path,
            final Set<PosixFilePermission> filePermissions,
            final String fileOwner,
            final String fileGroup) throws IOException {
        // 获取文件的PosixFileAttributeView
        final PosixFileAttributeView view = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        if (view != null) { // 如果视图可用
            // 获取用户主体查找服务
            final UserPrincipalLookupService lookupService = FileSystems.getDefault()
                    .getUserPrincipalLookupService();
            if (fileOwner != null) { // 如果指定了文件所有者
                final UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(fileOwner); // 根据名称查找用户主体
                if (userPrincipal != null) { // 如果找到用户主体
                    // If not sudoers member, it will throw Operation not permitted
                    // Only processes with an effective user ID equal to the user ID
                    // of the file or with appropriate privileges may change the ownership of a file.
                    // If _POSIX_CHOWN_RESTRICTED is in effect for path
                    // 如果不是sudoers成员，将抛出“操作不允许”异常
                    // 只有有效用户ID等于文件用户ID或具有适当权限的进程才能更改文件所有权。
                    // 如果_POSIX_CHOWN_RESTRICTED对路径生效
                    view.setOwner(userPrincipal); // 设置文件所有者
                }
            }
            if (fileGroup != null) { // 如果指定了文件组
                final GroupPrincipal groupPrincipal = lookupService.lookupPrincipalByGroupName(fileGroup); // 根据名称查找组主体
                if (groupPrincipal != null) { // 如果找到组主体
                    // The current user id should be members of this group,
                    // if not will raise Operation not permitted
                    // 当前用户ID应该是该组的成员，否则会引发“操作不允许”异常
                    view.setGroup(groupPrincipal); // 设置文件组
                }
            }
            if (filePermissions != null) { // 如果指定了文件权限
                view.setPermissions(filePermissions); // 设置文件权限
            }
        }
    }

    /**
     * Check if POSIX file attribute view is supported on the default FileSystem.
     * 检查默认文件系统是否支持 POSIX 文件属性视图。
     *
     * @return true if POSIX file attribute view supported, false otherwise
     * 如果支持 POSIX 文件属性视图，则返回 true，否则返回 false
     */
    public static boolean isFilePosixAttributeViewSupported() {
        return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
    }
}
