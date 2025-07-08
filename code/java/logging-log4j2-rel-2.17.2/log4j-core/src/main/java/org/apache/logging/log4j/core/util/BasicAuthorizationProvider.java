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

import java.net.URLConnection;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Base64Util;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * Provides the Basic Authorization header to a request.
 * 为请求提供基本的授权头部（Basic Authorization header）。
 */
public class BasicAuthorizationProvider implements AuthorizationProvider {
    private static final String[] PREFIXES = {"log4j2.config.", "log4j2.Configuration.", "logging.auth."};
    // 定义用于查找配置属性的前缀数组。
    private static final String AUTH_USER_NAME = "username";
    // 定义用于授权用户名的属性键。
    private static final String AUTH_PASSWORD = "password";
    // 定义用于授权密码的属性键。
    private static final String AUTH_PASSWORD_DECRYPTOR = "passwordDecryptor";
    // 定义用于密码解密器的属性键。
    public static final String CONFIG_USER_NAME = "log4j2.configurationUserName";
    // 定义配置中用户名的完整属性键。
    public static final String CONFIG_PASSWORD = "log4j2.configurationPassword";
    // 定义配置中密码的完整属性键。
    public static final String PASSWORD_DECRYPTOR = "log4j2.passwordDecryptor";
    // 定义配置中密码解密器的完整属性键。

    private static Logger LOGGER = StatusLogger.getLogger();
    // 获取一个StatusLogger实例，用于记录内部状态信息和警告。

    private String authString = null;
    // 存储生成的Basic认证字符串，默认为null。

    public BasicAuthorizationProvider(PropertiesUtil props) {
        // 构造函数，接收一个PropertiesUtil实例，用于获取配置属性。
        String userName = props.getStringProperty(PREFIXES,AUTH_USER_NAME,
                () -> props.getStringProperty(CONFIG_USER_NAME));
        // 从配置中获取用户名，优先使用带有前缀的属性（如log4j2.config.username），如果不存在则尝试使用log4j2.configurationUserName。
        String password = props.getStringProperty(PREFIXES, AUTH_PASSWORD,
                () -> props.getStringProperty(CONFIG_PASSWORD));
        // 从配置中获取密码，优先使用带有前缀的属性（如log4j2.config.password），如果不存在则尝试使用log4j2.configurationPassword。
        String decryptor = props.getStringProperty(PREFIXES, AUTH_PASSWORD_DECRYPTOR,
                () -> props.getStringProperty(PASSWORD_DECRYPTOR));
        // 从配置中获取密码解密器类的名称，优先使用带有前缀的属性，如果不存在则尝试使用log4j2.passwordDecryptor。
        if (decryptor != null) {
            // 如果配置了密码解密器。
            try {
                Object obj = LoaderUtil.newInstanceOf(decryptor);
                // 使用LoaderUtil加载并实例化指定的解密器类。
                if (obj instanceof PasswordDecryptor) {
                    // 如果实例化对象是PasswordDecryptor的实例。
                    password = ((PasswordDecryptor) obj).decryptPassword(password);
                    // 调用解密器对密码进行解密。
                }
            } catch (Exception ex) {
                // 捕获解密过程中可能发生的异常。
                LOGGER.warn("Unable to decrypt password.", ex);
                // 记录警告信息，表示无法解密密码。
            }
        }
        if (userName != null && password != null) {
            // 如果用户名和密码都存在。
            authString = "Basic " + Base64Util.encode(userName + ":" + password);
            // 将用户名和密码用冒号连接后进行Base64编码，并加上"Basic "前缀，形成完整的认证字符串。
        }
    }

    @Override
    public void addAuthorization(URLConnection urlConnection) {
        // 实现AuthorizationProvider接口的addAuthorization方法，用于向URL连接添加授权头部。
        if (authString != null) {
            // 如果认证字符串已生成。
            urlConnection.setRequestProperty("Authorization", authString);
            // 将"Authorization"头部及其值添加到URL连接的请求属性中。
        }
    }
}
