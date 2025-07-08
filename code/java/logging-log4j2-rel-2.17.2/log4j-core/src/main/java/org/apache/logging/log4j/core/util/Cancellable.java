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
 * Interface to cancel a Runnable callback.
 * 用于取消 Runnable 回调的接口。
 *
 * @since 2.1
 * @since 2.1 版本开始引入。
 */
public interface Cancellable extends Runnable {

    /**
     * Cancels the execution of this Runnable callback. This method has no effect if this has already executed.
     * 取消此 Runnable 回调的执行。如果此回调已执行，则此方法无效。
     * 方法目的说明：此方法旨在提供一种机制，允许在 Runnable 完成其任务之前停止其执行。
     * 特殊处理的注意事项：如果 Runnable 已经执行完毕，调用此方法将不会产生任何效果。
     */
    void cancel();
}
