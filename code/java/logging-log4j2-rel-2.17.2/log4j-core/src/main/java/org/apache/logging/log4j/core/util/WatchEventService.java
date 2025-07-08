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

/**
 *
 */
// 这是一个接口，用于处理文件或资源变化的事件。
// 当被监控的文件或资源发生变化时，将触发相应的事件。
public interface WatchEventService {

    /**
     *
     */
    // 订阅文件或资源变化的事件。
    // 当文件或资源发生变化时，WatchManager 将收到通知。
    // 参数:
    //   manager: WatchManager 实例，用于管理和分发事件。
    void subscribe(WatchManager manager);

    /**
     *
     */
    // 取消订阅文件或资源变化的事件。
    // 一旦取消订阅，WatchManager 将不再接收到该 WatchEventService 的事件通知。
    // 参数:
    //   manager: WatchManager 实例，用于取消订阅。
    void unsubscribe(WatchManager manager);
}
