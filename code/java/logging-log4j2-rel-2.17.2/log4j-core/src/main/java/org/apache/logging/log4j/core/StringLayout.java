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
package org.apache.logging.log4j.core;

import java.nio.charset.Charset;

/**
 * Instantiates the @{link Layout} type for String-based layouts.
 * 为基于字符串的布局实例化 {@link Layout} 类型。
 */
public interface StringLayout extends Layout<String> {

    /**
     * Gets the Charset this layout uses to encode Strings into bytes.
     * 获取此布局用于将字符串编码为字节的字符集。
     *
     * @return the Charset this layout uses to encode Strings into bytes.
     * @return 此布局用于将字符串编码为字节的字符集。
     */
    Charset getCharset();

}
