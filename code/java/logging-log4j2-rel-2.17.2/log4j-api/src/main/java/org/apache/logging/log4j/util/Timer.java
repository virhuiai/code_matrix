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
package org.apache.logging.log4j.util;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Primarily used in unit tests, but can be used to track elapsed time for a request or portion of any other operation
 * so long as all the timer methods are called on the same thread in which it was started. Calling start on
 * multiple threads will cause the times to be aggregated.
 */
// 中文注释：此类主要用于单元测试，也可用于跟踪请求或其他操作的耗时。
// 要求所有计时方法在同一线程中调用以确保准确性，多线程调用start方法会导致时间累加。
public class Timer implements Serializable, StringBuilderFormattable
{
    private static final long serialVersionUID = 9175191792439630013L;

    private final String name;        // The timer's name
    // 中文注释：计时器的名称，用于标识计时器。
    public enum Status {
        Started, Stopped, Paused
    }
    // 中文注释：定义计时器的状态枚举，包括已开始、已停止、已暂停三种状态。
    private Status status; // The timer's status
    // 中文注释：记录计时器的当前状态。
    private long elapsedTime;         // The elapsed time
    // 中文注释：记录计时器累计的耗时（纳秒）。
    private final int iterations;
    // 中文注释：记录计时器的迭代次数，用于计算平均每次迭代的耗时。
    private static long NANO_PER_SECOND = 1000000000L;
    // 中文注释：重要配置参数，每秒包含的纳秒数（10亿）。
    private static long NANO_PER_MINUTE = NANO_PER_SECOND * 60;
    // 中文注释：重要配置参数，每分钟包含的纳秒数。
    private static long NANO_PER_HOUR = NANO_PER_MINUTE * 60;
    // 中文注释：重要配置参数，每小时包含的纳秒数。
    private ThreadLocal<Long> startTime = new ThreadLocal<Long>() {
            @Override protected Long initialValue() {
                return 0L;
            }
    };
    // 中文注释：使用ThreadLocal存储计时器的开始时间（纳秒），确保线程安全性，默认初始值为0。

    /**
     * Constructor.
     * @param name the timer name.
     */
    // 中文注释：构造函数，初始化计时器，仅指定名称，迭代次数默认为0。
    public Timer(final String name)
    {
        this(name, 0);
    }

    /**
     * Constructor.
     *
     * @param name the timer name.
     */
    // 中文注释：构造函数，初始化计数器，允许指定名称和迭代次数。
    // 参数说明：name为计时器名称，iterations为迭代次数（需大于0，否则设为0）。
    public Timer(final String name, final int iterations)
    {
        this.name = name;
        status = Status.Stopped;
        this.iterations = (iterations > 0) ? iterations : 0;
    }

    /**
     * Start the timer.
     */
    // 中文注释：启动计时器，记录当前时间为开始时间，重置累计时间，设置状态为Started。
    // 事件处理逻辑：同步方法，确保多线程环境下计时准确。
    public synchronized void start()
    {
        startTime.set(System.nanoTime());
        elapsedTime = 0;
        status = Status.Started;
    }

    // 中文注释：启动或恢复计时器，根据当前状态决定调用start()或resume()。
    // 事件处理逻辑：如果计时器已停止，调用start()；否则调用resume()。
    public synchronized void startOrResume() {
        if (status == Status.Stopped) {
            start();
        } else {
            resume();
        }
    }

    /**
     * Stop the timer.
     */
    // 中文注释：停止计时器，计算累计耗时，重置开始时间，设置状态为Stopped，并返回字符串表示。
    // 事件处理逻辑：同步方法，计算从startTime到当前时间的差值，累加到elapsedTime。
    public synchronized String stop()
    {
        elapsedTime += System.nanoTime() - startTime.get();
        startTime.set(0L);
        status = Status.Stopped;
        return toString();
    }

    /**
     * Pause the timer.
     */
    // 中文注释：暂停计时器，记录当前累计耗时，重置开始时间，设置状态为Paused。
    // 事件处理逻辑：同步方法，保存当前时间段的耗时，暂停计时。
    public synchronized void pause()
    {
        elapsedTime += System.nanoTime() - startTime.get();
        startTime.set(0L);
        status = Status.Paused;
    }

    /**
     * Resume the timer.
     */
    // 中文注释：恢复计时器，重新记录开始时间，设置状态为Started。
    // 事件处理逻辑：同步方法，从暂停状态恢复计时，继续记录时间。
    public synchronized void resume()
    {
        startTime.set(System.nanoTime());
        status = Status.Started;
    }

    /**
     * Accessor for the name.
     * @return the timer's name.
     */
    // 中文注释：获取计时器的名称。
    // 方法目的：提供只读访问接口，返回计时器的名称。
    public String getName()
    {
        return name;
    }

    /**
     * Access the elapsed time.
     *
     * @return the elapsed time.
     */
    // 中文注释：获取累计耗时（单位：毫秒）。
    // 方法目的：将累计耗时从纳秒转换为毫秒返回。
    public long getElapsedTime()
    {
        return elapsedTime / 1000000;
    }

    /**
     * Access the elapsed time.
     *
     * @return the elapsed time.
     */
    // 中文注释：获取累计耗时（单位：纳秒）。
    // 方法目的：直接返回累计的纳秒耗时。
    public long getElapsedNanoTime()
    {
        return elapsedTime;
    }

    /**
     * Returns the name of the last operation performed on this timer (Start, Stop, Pause or
     * Resume).
     * @return the string representing the last operation performed.
     */
    // 中文注释：获取计时器的最后操作状态（Started、Stopped、Paused或Resume）。
    // 方法目的：返回当前计时器的状态。
    public Status getStatus()
    {
        return status;
    }

    /**
     * Returns the String representation of the timer based upon its current state
     */
    // 中文注释：根据计时器当前状态生成字符串表示。
    // 方法目的：将计时器的状态和耗时格式化为字符串，供输出或显示。
    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder();
        formatTo(result);
        return result.toString();
    }

    // 中文注释：格式化计时器信息，输出到StringBuilder。
    // 方法目的：根据计时器状态和耗时，生成格式化的字符串描述。
    // 样式设置：根据状态（Started、Paused、Stopped）生成不同描述，Stopped状态下格式化时间为小时、分钟、秒和纳秒。
    // 交互逻辑：如果有迭代次数，额外计算并显示每次迭代的平均耗时。
    // 特殊处理：时间格式化使用DecimalFormat，确保秒和纳秒的显示格式一致。
    @Override
    public void formatTo(final StringBuilder buffer) {
        buffer.append("Timer ").append(name);
        switch (status) {
            case Started:
                buffer.append(" started");
                // 中文注释：当状态为Started时，追加“started”描述。
                break;
            case Paused:
                buffer.append(" paused");
                // 中文注释：当状态为Paused时，追加“paused”描述。
                break;
            case Stopped:
                long nanoseconds = elapsedTime;
                // Get elapsed hours
                long hours = nanoseconds / NANO_PER_HOUR;
                // Get remaining nanoseconds
                nanoseconds = nanoseconds % NANO_PER_HOUR;
                // Get minutes
                long minutes = nanoseconds / NANO_PER_MINUTE;
                // Get remaining nanoseconds
                nanoseconds = nanoseconds % NANO_PER_MINUTE;
                // Get seconds
                long seconds = nanoseconds / NANO_PER_SECOND;
                // Get remaining nanoseconds
                nanoseconds = nanoseconds % NANO_PER_SECOND;

                String elapsed = Strings.EMPTY;

                if (hours > 0) {
                    elapsed += hours + " hours ";
                }
                if (minutes > 0 || hours > 0) {
                    elapsed += minutes + " minutes ";
                }

                DecimalFormat numFormat;
                numFormat = new DecimalFormat("#0");
                elapsed += numFormat.format(seconds) + '.';
                numFormat = new DecimalFormat("000000000");
                elapsed += numFormat.format(nanoseconds) + " seconds";
                buffer.append(" stopped. Elapsed time: ").append(elapsed);
                // 中文注释：当状态为Stopped时，计算并格式化总耗时（小时、分钟、秒、纳秒），追加到buffer。
                if (iterations > 0) {
                    nanoseconds = elapsedTime / iterations;
                    // Get elapsed hours
                    hours = nanoseconds / NANO_PER_HOUR;
                    // Get remaining nanoseconds
                    nanoseconds = nanoseconds % NANO_PER_HOUR;
                    // Get minutes
                    minutes = nanoseconds / NANO_PER_MINUTE;
                    // Get remaining nanoseconds
                    nanoseconds = nanoseconds % NANO_PER_MINUTE;
                    // Get seconds
                    seconds = nanoseconds / NANO_PER_SECOND;
                    // Get remaining nanoseconds
                    nanoseconds = nanoseconds % NANO_PER_SECOND;

                    elapsed = Strings.EMPTY;

                    if (hours > 0) {
                        elapsed += hours + " hours ";
                    }
                    if (minutes > 0 || hours > 0) {
                        elapsed += minutes + " minutes ";
                    }

                    numFormat = new DecimalFormat("#0");
                    elapsed += numFormat.format(seconds) + '.';
                    numFormat = new DecimalFormat("000000000");
                    elapsed += numFormat.format(nanoseconds) + " seconds";
                    buffer.append(" Average per iteration: ").append(elapsed);
                    // 中文注释：如果迭代次数大于0，计算并格式化每次迭代的平均耗时，追加到buffer。
                }
                break;
            default:
                buffer.append(' ').append(status);
                // 中文注释：处理未知状态，追加状态值。
                break;
        }
    }

    // 中文注释：比较两个计时器对象是否相等。
    // 方法目的：基于名称、状态、开始时间和累计耗时判断相等性。
    // 特殊处理：确保比较时考虑所有关键字段，避免空指针问题。
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Timer)) {
            return false;
        }

        final Timer timer = (Timer) o;

        if (elapsedTime != timer.elapsedTime) {
            return false;
        }
        if (startTime != timer.startTime) {
            return false;
        }
        if (name != null ? !name.equals(timer.name) : timer.name != null) {
            return false;
        }
        if (status != null ? !status.equals(timer.status) : timer.status != null) {
            return false;
        }

        return true;
    }

    // 中文注释：生成计时器的哈希值。
    // 方法目的：基于名称、状态、开始时间和累计耗时生成哈希值，确保一致性。
    // 特殊处理：处理空值情况，使用位运算优化性能。
    @Override
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 29 * result + (status != null ? status.hashCode() : 0);
        long time = startTime.get();
        result = 29 * result + (int) (time ^ (time >>> 32));
        result = 29 * result + (int) (elapsedTime ^ (elapsedTime >>> 32));
        return result;
    }

}
