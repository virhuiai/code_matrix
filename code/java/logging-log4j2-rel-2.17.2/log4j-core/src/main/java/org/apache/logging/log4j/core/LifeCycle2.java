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

import java.util.concurrent.TimeUnit;

/**
 * Extends the LifeCycle interface.
 * 继承了 LifeCycle 接口。
 * <p>
 *  This interface should be merged with the super-interface in 3.0.
 * 此接口应在 3.0 版本中与父接口合并。
 * </p>
 * @since 2.7
 */
public interface LifeCycle2 extends LifeCycle {

    /**
     * Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current
     * thread is interrupted, whichever happens first.
     * 在关闭请求后，阻塞直到所有任务完成执行，或者超时发生，或者当前线程被中断，以先发生的为准。
     *
     * @param timeout the maximum time to wait
     * 参数 timeout：表示最大等待时间。
     * @param timeUnit the time unit of the timeout argument
     * 参数 timeUnit：表示 timeout 参数的时间单位。
     * @return true if the receiver was stopped cleanly and normally, false otherwise.
     * 返回值：如果接收者干净且正常地停止，则返回 true；否则返回 false。
     * @since 2.7
     */
    boolean stop(long timeout, TimeUnit timeUnit);
}
