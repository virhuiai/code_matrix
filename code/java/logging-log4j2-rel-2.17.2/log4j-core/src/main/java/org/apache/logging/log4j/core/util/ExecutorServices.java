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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * 这是一个实用工具类，用于处理 ExecutorService 的关闭操作。
 */
public class ExecutorServices {

    private static final Logger LOGGER = StatusLogger.getLogger();
    // LOGGER 变量用于记录日志信息。

    /**
     * Shuts down the given {@link ExecutorService} in an orderly fashion. Disables new tasks from submission and then
     * waits for existing tasks to terminate. Eventually cancels running tasks if too much time elapses.
     * <p>
     * If the timeout is 0, then a plain shutdown takes place.
     * </p>
     * 优雅地关闭给定的 {@link ExecutorService}。
     * 该方法首先禁止提交新任务，然后等待现有任务终止。
     * 如果等待时间过长，最终会取消正在运行的任务。
     * <p>
     * 如果 timeout 参数为 0，则执行普通的关闭操作（不等待）。
     * </p>
     *
     * @param executorService
     *            the pool to shutdown.
     * 要关闭的线程池。
     * @param timeout
     *            the maximum time to wait, or 0 to not wait for existing tasks to terminate.
     * 等待现有任务终止的最长时间，如果为 0 则表示不等待。
     * @param timeUnit
     *            the time unit of the timeout argument
     * timeout 参数的时间单位。
     * @param source
     *            use this string in any log messages.
     * 在任何日志消息中使用的字符串，通常表示调用者。
     * @return {@code true} if the given executor terminated and {@code false} if the timeout elapsed before
     *         termination.
     * 如果给定的执行器终止则返回 {@code true}，如果在超时之前未能终止则返回 {@code false}。
     */
    public static boolean shutdown(final ExecutorService executorService, final long timeout, final TimeUnit timeUnit, final String source) {
        // 检查 executorService 是否为空或已经终止。
        // 如果是，则直接返回 true，表示已经处于终止状态。
        if (executorService == null || executorService.isTerminated()) {
            return true;
        }
        // 调用 shutdown() 方法，禁止提交新任务到线程池。
        executorService.shutdown(); // Disable new tasks from being submitted
        // 检查 timeout 是否大于 0 但 timeUnit 为空，这是一种无效的参数组合。
        // 如果是，则抛出 IllegalArgumentException。
        if (timeout > 0 && timeUnit == null) {
            throw new IllegalArgumentException(
                    String.format("%s can't shutdown %s when timeout = %,d and timeUnit = %s.", source, executorService,
                            timeout, timeUnit));
        }
        // 如果 timeout 大于 0，表示需要等待任务终止。
        if (timeout > 0) {
            try {
                // 等待现有任务在指定的时间内终止。
                if (!executorService.awaitTermination(timeout, timeUnit)) {
                    // 如果在指定时间内未能终止，则尝试立即关闭线程池，中断所有正在执行的任务。
                    executorService.shutdownNow(); // Cancel currently executing tasks
                    // 再次等待一段时间，让被取消的任务响应中断并终止。
                    if (!executorService.awaitTermination(timeout, timeUnit)) {
                        // 如果仍然未能终止，则记录错误日志。
                        LOGGER.error("{} pool {} did not terminate after {} {}", source, executorService, timeout,
                                timeUnit);
                    }
                    // 返回 false，表示未能完全终止。
                    return false;
                }
            } catch (final InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                // 捕获 InterruptedException 异常，表示当前线程被中断。
                // 此时，再次调用 shutdownNow() 确保所有任务被取消。
                executorService.shutdownNow();
                // Preserve interrupt status
                // 重新设置当前线程的中断状态，以便上层调用者能够感知到中断。
                Thread.currentThread().interrupt();
            }
        } else {
            // 如果 timeout 为 0，则只执行普通的 shutdown，不等待。
            executorService.shutdown();
        }
        // 如果执行器成功终止（或者 timeout 为 0 且 shutdown 已被调用），则返回 true。
        return true;
    }

    /** no-op method which can be invoked to ensure this class has been initialized per jls-12.4.2. */
    /**
     * 一个空操作方法，可以调用它以确保根据 JLS-12.4.2 规范，此类别已初始化。
     */
    public static void ensureInitialized() {}
}
