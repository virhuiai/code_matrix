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

/**
 * Interface to be implemented to add an Authorization header to an HTTP request.
 * 这是一个接口，旨在为HTTP请求添加认证（Authorization）头。
 * 实现此接口的类将负责提供认证信息。
 */
public interface AuthorizationProvider {

    /**
     * Adds an Authorization header to the provided URLConnection.
     * 为提供的 URLConnection 添加认证（Authorization）头。
     *
     * @param urlConnection The URLConnection to which the Authorization header should be added.
     * 需要添加认证头的 URLConnection 对象。
     * 这个方法不修改 URLConnection 的其他属性，只负责添加认证信息。
     */
    void addAuthorization(URLConnection urlConnection);
}
