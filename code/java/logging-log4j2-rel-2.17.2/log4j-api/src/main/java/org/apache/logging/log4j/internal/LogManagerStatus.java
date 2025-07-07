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
package org.apache.logging.log4j.internal;

/**
 * Keeps track of LogManager initialization status;
 * 跟踪 LogManager 的初始化状态；
 */
public class LogManagerStatus {

    private static boolean initialized = false;
    // 私有静态变量，用于存储 LogManager 的初始化状态，默认未初始化。

    public static void setInitialized(boolean managerStatus) {
        // Sets the initialization status of the LogManager.
        // 设置 LogManager 的初始化状态。
        // 参数:
        // managerStatus - LogManager 的初始化状态，true 表示已初始化，false 表示未初始化。
        initialized = managerStatus;
        // 将传入的状态值赋给 initialized 变量。
    }

    public static boolean isInitialized() {
        // Returns the initialization status of the LogManager.
        // 返回 LogManager 的初始化状态。
        // 返回值:
        // boolean - LogManager 的初始化状态，true 表示已初始化，false 表示未初始化。
        return initialized;
        // 返回当前 initialized 变量的值。
    }
}
