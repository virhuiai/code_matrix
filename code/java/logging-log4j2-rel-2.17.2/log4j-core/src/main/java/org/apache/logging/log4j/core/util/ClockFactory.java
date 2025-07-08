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

import org.apache.logging.log4j.core.time.PreciseClock;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Supplier;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for {@code Clock} objects.
 */
// Clock对象的工厂类。
public final class ClockFactory {

    /**
     * Name of the system property that can be used to specify a {@code Clock}
     * implementation class. The value of this property is {@value}.
     */
    // 用于指定Clock接口实现类的系统属性名称。该属性的值为"log4j.Clock"。
    public static final String PROPERTY_NAME = "log4j.Clock";
    private static final StatusLogger LOGGER = StatusLogger.getLogger();
    // LOGGER变量用于记录状态信息。

    // private static final Clock clock = createClock();

    private ClockFactory() {
    }
    // 私有构造函数，防止外部实例化该工具类。

    /**
     * Returns a {@code Clock} instance depending on the value of system
     * property {@link #PROPERTY_NAME}.
     * <p>
     * If system property {@code log4j.Clock=CachedClock} is specified,
     * this method returns an instance of {@link CachedClock}. If system
     * property {@code log4j.Clock=CoarseCachedClock} is specified, this
     * method returns an instance of {@link CoarseCachedClock}.
     * </p>
     * <p>
     * If another value is specified, this value is taken as the fully qualified
     * class name of a class that implements the {@code Clock} interface. An
     * object of this class is instantiated and returned.
     * </p>
     * <p>
     * If no value is specified, or if the specified value could not correctly
     * be instantiated or did not implement the {@code Clock} interface, then an
     * instance of {@link SystemClock} is returned.
     * </p>
     *
     * @return a {@code Clock} instance
     */
    // 根据系统属性{@link #PROPERTY_NAME}的值返回一个{@code Clock}实例。
    // 如果系统属性log4j.Clock=CachedClock，则返回CachedClock实例。
    // 如果系统属性log4j.Clock=CoarseCachedClock，则返回CoarseCachedClock实例。
    // 如果指定了其他值，则该值被视为实现Clock接口的类的完全限定类名，并实例化该类的对象并返回。
    // 如果未指定值，或者指定的值无法正确实例化或未实现Clock接口，则返回SystemClock实例。
    // 返回值：一个{@code Clock}实例。
    public static Clock getClock() {
        // 调用createClock()方法来实际创建并返回Clock实例。
        return createClock();
    }

    private static Map<String, Supplier<Clock>> aliases() {
        // aliases方法返回一个Map，其中包含预定义的Clock实现别名及其对应的Supplier。
        final Map<String, Supplier<Clock>> result = new HashMap<>();
        // 创建一个新的HashMap来存储别名和Supplier。
        result.put("SystemClock",       SystemClock::new);
        // 将"SystemClock"别名映射到SystemClock的新实例。
        result.put("SystemMillisClock", SystemMillisClock::new);
        // 将"SystemMillisClock"别名映射到SystemMillisClock的新实例。
        result.put("CachedClock",       CachedClock::instance);
        // 将"CachedClock"别名映射到CachedClock的单例实例。
        result.put("CoarseCachedClock", CoarseCachedClock::instance);
        // 将"CoarseCachedClock"别名映射到CoarseCachedClock的单例实例。
        result.put("org.apache.logging.log4j.core.util.CachedClock", CachedClock::instance);
        // 将CachedClock的完整类名映射到其单例实例。
        result.put("org.apache.logging.log4j.core.util.CoarseCachedClock", CoarseCachedClock::instance);
        // 将CoarseCachedClock的完整类名映射到其单例实例。
        return result;
        // 返回包含所有别名的Map。
    }

    private static Clock createClock() {
        // createClock方法负责根据系统属性创建并返回一个Clock实例。
        final String userRequest = PropertiesUtil.getProperties().getStringProperty(PROPERTY_NAME);
        // 从系统属性中获取用户指定的Clock实现类名。PROPERTY_NAME是"log4j.Clock"。
        if (userRequest == null) {
            // 如果用户没有指定任何Clock实现（即userRequest为null）。
            LOGGER.trace("Using default SystemClock for timestamps.");
            // 记录日志，表示将使用默认的SystemClock。
            return logSupportedPrecision(new SystemClock());
            // 返回一个新的SystemClock实例，并记录其精度支持情况。
        }
        final Supplier<Clock> specified = aliases().get(userRequest);
        // 尝试从预定义的别名Map中获取对应的Clock Supplier。
        if (specified != null) {
            // 如果用户请求的名称是一个已知的别名。
            LOGGER.trace("Using specified {} for timestamps.", userRequest);
            // 记录日志，表示将使用用户指定的别名对应的Clock。
            return logSupportedPrecision(specified.get());
            // 通过Supplier获取Clock实例，并记录其精度支持情况。
        }
        try {
            // 如果用户请求的不是别名，则尝试将其作为完全限定类名进行加载。
            final Clock result = Loader.newCheckedInstanceOf(userRequest, Clock.class);
            // 使用Loader工具类加载并实例化用户指定的类，并确保它实现了Clock接口。
            LOGGER.trace("Using {} for timestamps.", result.getClass().getName());
            // 记录日志，表示将使用通过类名加载的Clock实例。
            return logSupportedPrecision(result);
            // 返回加载的Clock实例，并记录其精度支持情况。
        } catch (final Exception e) {
            // 如果在加载或实例化用户指定的类时发生任何异常。
            final String fmt = "Could not create {}: {}, using default SystemClock for timestamps.";
            // 定义错误消息的格式字符串。
            LOGGER.error(fmt, userRequest, e);
            // 记录错误日志，说明无法创建指定的Clock，并将使用默认的SystemClock。
            return logSupportedPrecision(new SystemClock());
            // 返回一个新的SystemClock实例，作为回退方案，并记录其精度支持情况。
        }
    }

    private static Clock logSupportedPrecision(final Clock clock) {
        // logSupportedPrecision方法用于记录给定Clock实例是否支持精确时间戳。
        final String support = clock instanceof PreciseClock ? "supports" : "does not support";
        // 检查Clock实例是否是PreciseClock的实例，以确定它是否支持精确时间戳。
        // 如果是，则support为"supports"，否则为"does not support"。
        LOGGER.debug("{} {} precise timestamps.", clock.getClass().getName(), support);
        // 记录调试日志，显示Clock的类名以及它是否支持精确时间戳。
        return clock;
        // 返回传入的Clock实例。
    }
}
