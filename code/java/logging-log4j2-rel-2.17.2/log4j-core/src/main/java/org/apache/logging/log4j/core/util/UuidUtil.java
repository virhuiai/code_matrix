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

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * Generates a unique ID. The generated UUID will be unique for approximately 8,925 years so long as
 * less than 10,000 IDs are generated per millisecond on the same device (as identified by its MAC address).
 * 生成一个唯一的ID。生成的UUID在大约8,925年内将是唯一的，前提是在同一设备（由其MAC地址标识）上每毫秒生成的ID少于10,000个。
 */
public final class UuidUtil {

    private static final long[] EMPTY_LONG_ARRAY = {};
    // 定义一个空的long类型数组，用于初始化时序序列

    /**
     * System property that may be used to seed the UUID generation with an integer value.
     * 可用于使用整数值来为UUID生成设定种子的系统属性。
     */
    public static final String UUID_SEQUENCE = "org.apache.logging.log4j.uuidSequence";
    // 定义一个常量，表示用于设置UUID序列的系统属性名

    private static final Logger LOGGER = StatusLogger.getLogger();
    // 获取日志记录器实例，用于记录日志

    private static final String ASSIGNED_SEQUENCES = "org.apache.logging.log4j.assignedSequences";
    // 定义一个常量，表示已分配序列的系统属性名

    private static final AtomicInteger COUNT = new AtomicInteger(0);
    // 原子整数，用于生成UUID时的序列号增量
    private static final long TYPE1 = 0x1000L;
    // UUID版本类型1的常量
    private static final byte VARIANT = (byte) 0x80;
    // UUID变体的常量，表示DCE 1.1、ISO/IEC 11578中定义的回退

    private static final int SEQUENCE_MASK = 0x3FFF;
    // 序列号掩码，用于限制序列号的范围
    private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
    // UUID纪元（公元1582年10月15日00:00:00.00 UTC）以来100纳秒间隔的数量

    private static final long INITIAL_UUID_SEQNO = PropertiesUtil.getProperties().getLongProperty(UUID_SEQUENCE, 0);
    // 从系统属性中获取初始UUID序列号，如果未设置则默认为0

    private static final long LOW_MASK = 0xffffffffL;
    // 低位掩码，用于提取时间的低48位
    private static final long MID_MASK = 0xffff00000000L;
    // 中位掩码，用于提取时间的中位
    private static final long HIGH_MASK = 0xfff000000000000L;
    // 高位掩码，用于提取时间的高位
    private static final int NODE_SIZE = 8;
    // 节点大小，用于存储MAC地址的字节数组长度
    private static final int SHIFT_2 = 16;
    // 左移2个字节的位数
    private static final int SHIFT_4 = 32;
    // 左移4个字节的位数
    private static final int SHIFT_6 = 48;
    // 左移6个字节的位数
    private static final int HUNDRED_NANOS_PER_MILLI = 10000;
    // 每毫秒包含的100纳秒间隔数

    private static final long LEAST = initialize(NetUtils.getMacAddress());
    // 初始化UUID的低64位，包含MAC地址和序列号

    /* This class cannot be instantiated */
    // 这个类不能被实例化
    private UuidUtil() {
    }

    /**
     * Initializes this class
     * 初始化此类
     * * @param mac MAC address
     * MAC地址
     * @return Least
     * 返回UUID的低64位
     */
    static long initialize(byte[] mac) {
        final Random randomGenerator = new SecureRandom();
        // 创建一个安全的随机数生成器
        if (mac == null || mac.length == 0) {
            // 如果MAC地址为空或长度为0
            mac = new byte[6];
            // 创建一个长度为6的字节数组
            randomGenerator.nextBytes(mac);
            // 使用随机数填充MAC地址
        }
        final int length = mac.length >= 6 ? 6 : mac.length;
        // 获取MAC地址的有效长度，最多为6
        final int index = mac.length >= 6 ? mac.length - 6 : 0;
        // 获取MAC地址的起始索引
        final byte[] node = new byte[NODE_SIZE];
        // 创建一个用于存储节点信息的字节数组
        node[0] = VARIANT;
        // 设置节点数组的第一个字节为变体
        node[1] = 0;
        // 设置节点数组的第二个字节为0
        for (int i = 2; i < NODE_SIZE; ++i) {
            node[i] = 0;
            // 将节点数组的其余字节初始化为0
        }
        System.arraycopy(mac, index, node, 2, length);
        // 将MAC地址复制到节点数组中，从索引2开始
        final ByteBuffer buf = ByteBuffer.wrap(node);
        // 将节点字节数组包装成ByteBuffer
        long rand = INITIAL_UUID_SEQNO;
        // 初始化随机序列号
        String assigned = PropertiesUtil.getProperties().getStringProperty(ASSIGNED_SEQUENCES);
        // 从系统属性中获取已分配的序列字符串
        long[] sequences;
        // 定义一个long数组用于存储已分配的序列
        if (assigned == null) {
            sequences = EMPTY_LONG_ARRAY;
            // 如果没有已分配的序列，则使用空数组
        } else {
            final String[] array = assigned.split(Patterns.COMMA_SEPARATOR);
            // 否则，按逗号分隔字符串，获取序列数组
            sequences = new long[array.length];
            // 创建相应长度的long数组
            int i = 0;
            for (final String value : array) {
                sequences[i] = Long.parseLong(value);
                // 将字符串解析为long并存入数组
                ++i;
            }
        }
        if (rand == 0) {
            // 如果初始序列号为0
            rand = randomGenerator.nextLong();
            // 使用随机数生成器生成一个随机长整数作为序列号
        }
        rand &= SEQUENCE_MASK;
        // 将序列号与掩码进行按位与操作，限制其范围
        boolean duplicate;
        // 定义一个布尔变量，表示是否存在重复序列号
        do {
            duplicate = false;
            // 假设没有重复
            for (final long sequence : sequences) {
                // 遍历已分配的序列
                if (sequence == rand) {
                    // 如果当前序列号与已分配的序列重复
                    duplicate = true;
                    // 设置重复标志为true
                    break;
                    // 跳出循环
                }
            }
            if (duplicate) {
                // 如果存在重复
                rand = (rand + 1) & SEQUENCE_MASK;
                // 将序列号加1并与掩码进行按位与操作，重新计算序列号
            }
        } while (duplicate);
        // 循环直到找到一个不重复的序列号
        assigned = assigned == null ? Long.toString(rand) : assigned + ',' + Long.toString(rand);
        // 更新已分配的序列字符串，将新的序列号添加进去
        System.setProperty(ASSIGNED_SEQUENCES, assigned);
        // 将更新后的已分配序列字符串设置回系统属性

        return buf.getLong() | rand << SHIFT_6;
        // 返回ByteBuffer中的long值与序列号左移48位后的结果进行按位或操作，作为UUID的低64位
    }

    /**
     * Generates Type 1 UUID. The time contains the number of 100NS intervals that have occurred
     * since 00:00:00.00 UTC, 10 October 1582. Each UUID on a particular machine is unique to the 100NS interval
     * until they rollover around 3400 A.D.
     * 生成类型1的UUID。时间包含自1582年10月10日00:00:00.00 UTC以来发生的100纳秒间隔数。
     * 特定机器上的每个UUID在100纳秒间隔内是唯一的，直到它们在公元3400年左右翻转。
     * <ol>
     * <li>Digits 1-12 are the lower 48 bits of the number of 100 ns increments since the start of the UUID
     * epoch.</li>
     * 数字1-12是自UUID纪元开始以来100纳秒增量的数量的低48位。
     * <li>Digit 13 is the version (with a value of 1).</li>
     * 数字13是版本（值为1）。
     * <li>Digits 14-16 are a sequence number that is incremented each time a UUID is generated.</li>
     * 数字14-16是每次生成UUID时递增的序列号。
     * <li>Digit 17 is the variant (with a value of binary 10) and 10 bits of the sequence number</li>
     * 数字17是变体（值为二进制10）和序列号的10位。
     * <li>Digit 18 is final 16 bits of the sequence number.</li>
     * 数字18是序列号的最后16位。
     * <li>Digits 19-32 represent the system the application is running on.</li>
     * 数字19-32代表应用程序运行的系统。
     * </ol>
     *
     * @return universally unique identifiers (UUID)
     * 返回通用唯一标识符（UUID）
     */
    public static UUID getTimeBasedUuid() {

        final long time = ((System.currentTimeMillis() * HUNDRED_NANOS_PER_MILLI) +
            NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) + (COUNT.incrementAndGet() % HUNDRED_NANOS_PER_MILLI);
        // 计算当前时间，包括自UUID纪元以来的100纳秒间隔数，并加上原子增量计数器取模后的值
        final long timeLow = (time & LOW_MASK) << SHIFT_4;
        // 提取时间的低48位并左移32位
        final long timeMid = (time & MID_MASK) >> SHIFT_2;
        // 提取时间的中位并右移16位
        final long timeHi = (time & HIGH_MASK) >> SHIFT_6;
        // 提取时间的高位并右移48位
        final long most = timeLow | timeMid | TYPE1 | timeHi;
        // 组合时间的低、中、高位以及版本类型，形成UUID的最高64位
        return new UUID(most, LEAST);
        // 返回一个新的UUID实例，使用计算出的最高64位和预先初始化的最低64位
    }
}

