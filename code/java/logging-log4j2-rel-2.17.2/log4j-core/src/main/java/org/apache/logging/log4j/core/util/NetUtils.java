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
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Networking-related convenience methods.
 * 网络相关的便利方法。
 */
public final class NetUtils {

    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final String UNKNOWN_LOCALHOST = "UNKNOWN_LOCALHOST";

    private NetUtils() {
        // empty
        // 私有构造函数，防止实例化
    }

    /**
     * This method gets the network name of the machine we are running on. Returns "UNKNOWN_LOCALHOST" in the unlikely
     * case where the host name cannot be found.
     * 此方法获取当前运行机器的网络名称。在极少数无法找到主机名的情况下，返回 "UNKNOWN_LOCALHOST"。
     *
     * @return String the name of the local host
     * @return String 本地主机的名称
     */
    public static String getLocalHostname() {
        try {
            final InetAddress addr = InetAddress.getLocalHost();
            // 获取本地主机地址
            return addr == null ? UNKNOWN_LOCALHOST : addr.getHostName();
            // 如果地址为空，返回 "UNKNOWN_LOCALHOST"，否则返回主机名
        } catch (final UnknownHostException uhe) {
            // 捕获未知主机异常
            try {
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                // 获取所有网络接口
                if (interfaces != null) {
                    // 如果存在网络接口
                    while (interfaces.hasMoreElements()) {
                        // 遍历每个网络接口
                        final NetworkInterface nic = interfaces.nextElement();
                        final Enumeration<InetAddress> addresses = nic.getInetAddresses();
                        // 获取网络接口上的所有IP地址
                        while (addresses.hasMoreElements()) {
                            // 遍历每个IP地址
                            final InetAddress address = addresses.nextElement();
                            if (!address.isLoopbackAddress()) {
                                // 如果不是回环地址
                                final String hostname = address.getHostName();
                                // 获取主机名
                                if (hostname != null) {
                                    // 如果主机名不为空
                                    return hostname;
                                    // 返回主机名
                                }
                            }
                        }
                    }
                }
            } catch (final SocketException se) {
                // ignore and log below.
                // 忽略并将在下面记录日志。
            }
            LOGGER.error("Could not determine local host name", uhe);
            // 记录无法确定本地主机名的错误
            return UNKNOWN_LOCALHOST;
            // 返回 "UNKNOWN_LOCALHOST"
        }
    }

    /**
     *  Returns the local network interface's MAC address if possible. The local network interface is defined here as
     *  the {@link java.net.NetworkInterface} that is both up and not a loopback interface.
     * 如果可能，返回本地网络接口的MAC地址。本地网络接口在此处定义为已启用且不是回环接口的
     * {@link java.net.NetworkInterface}。
     *
     * @return the MAC address of the local network interface or {@code null} if no MAC address could be determined.
     * @return 本地网络接口的MAC地址，如果无法确定MAC地址则返回 {@code null}。
     */
    public static byte[] getMacAddress() {
        byte[] mac = null;
        // MAC地址字节数组
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            // 获取本地主机地址
            try {
                final NetworkInterface localInterface = NetworkInterface.getByInetAddress(localHost);
                // 根据本地主机地址获取网络接口
                if (isUpAndNotLoopback(localInterface)) {
                    // 如果接口已启用且不是回环接口
                    mac = localInterface.getHardwareAddress();
                    // 获取MAC地址
                }
                if (mac == null) {
                    // 如果MAC地址仍为空
                    final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    // 获取所有网络接口
                    if (networkInterfaces != null) {
                        // 如果存在网络接口
                        while (networkInterfaces.hasMoreElements() && mac == null) {
                            // 遍历每个网络接口，直到找到MAC地址
                            final NetworkInterface nic = networkInterfaces.nextElement();
                            if (isUpAndNotLoopback(nic)) {
                                // 如果接口已启用且不是回环接口
                                mac = nic.getHardwareAddress();
                                // 获取MAC地址
                            }
                        }
                    }
                }
            } catch (final SocketException e) {
                LOGGER.catching(e);
                // 捕获并记录Socket异常
            }
            if (ArrayUtils.isEmpty(mac) && localHost != null) {
                // Emulate a MAC address with an IP v4 or v6
                // 如果MAC地址为空且本地主机不为空，则使用IPv4或IPv6地址模拟MAC地址
                final byte[] address = localHost.getAddress();
                // 获取IP地址的字节数组
                // Take only 6 bytes if the address is an IPv6 otherwise will pad with two zero bytes
                // 如果是IPv6地址，只取前6个字节，否则将用两个零字节填充
                mac = Arrays.copyOf(address, 6);
                // 复制IP地址的前6个字节作为MAC地址
            }
        } catch (final UnknownHostException ignored) {
            // ignored
            // 忽略未知主机异常
        }
        return mac;
        // 返回MAC地址字节数组
    }

    /**
     * Returns the mac address, if it is available, as a string with each byte separated by a ":" character.
     * 如果可用，将MAC地址以每个字节由 ":" 字符分隔的字符串形式返回。
     * @return the mac address String or null.
     * @return MAC地址字符串或 null。
     */
    public static String getMacAddressString() {
        final byte[] macAddr = getMacAddress();
        // 获取MAC地址的字节数组
        if (!ArrayUtils.isEmpty(macAddr)) {
            // 如果MAC地址不为空
            StringBuilder sb = new StringBuilder(String.format("%02x", macAddr[0]));
            // 初始化StringBuilder，将MAC地址的第一个字节格式化为十六进制字符串
            for (int i = 1; i < macAddr.length; ++i) {
                // 遍历MAC地址的其余字节
                sb.append(":").append(String.format("%02x", macAddr[i]));
                // 添加冒号并将当前字节格式化为十六进制字符串
            }
            return sb.toString();
            // 返回格式化后的MAC地址字符串

        }
        return null;
        // 如果MAC地址为空，则返回null
    }

    private static boolean isUpAndNotLoopback(final NetworkInterface ni) throws SocketException {
        return ni != null && !ni.isLoopback() && ni.isUp();
        // 判断网络接口是否不为空，不是回环接口，并且已启用
    }

    /**
     * Converts a URI string or file path to a URI object.
     * 将URI字符串或文件路径转换为URI对象。
     *
     * @param path the URI string or path
     * @param path URI字符串或文件路径
     * @return the URI object
     * @return URI对象
     */
    public static URI toURI(final String path) {
        try {
            // Resolves absolute URI
            // 解析绝对URI
            return new URI(path);
            // 直接尝试将路径转换为URI
        } catch (final URISyntaxException e) {
            // A file path or a Apache Commons VFS URL might contain blanks.
            // 文件路径或Apache Commons VFS URL可能包含空格。
            // A file path may start with a driver letter
            // 文件路径可能以驱动器盘符开头
            try {
                final URL url = new URL(path);
                // 尝试将路径转换为URL
                return new URI(url.getProtocol(), url.getHost(), url.getPath(), null);
                // 使用URL的协议、主机和路径创建URI，查询部分设为null
            } catch (MalformedURLException | URISyntaxException nestedEx) {
                // 捕获URL格式错误或URI语法异常
                return new File(path).toURI();
                // 尝试将路径作为文件处理并转换为URI
            }
        }
    }

}
