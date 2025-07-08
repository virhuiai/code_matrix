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
package org.apache.logging.log4j.core.util.datetime;

import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * DatePrinter is the "missing" interface for the format methods of
 * {@link java.text.DateFormat}. You can obtain an object implementing this
 * interface by using one of the FastDateFormat factory methods.
 * DatePrinter 是 {@link java.text.DateFormat} 中格式化方法“缺失”的接口。
 * 您可以通过使用 FastDateFormat 工厂方法之一来获取实现此接口的对象。
 * <p>
 * Warning: Since binary compatible methods may be added to this interface in any
 * release, developers are not expected to implement this interface.
 * </p>
 * 警告：由于任何版本中都可能向此接口添加二进制兼容方法，因此不期望开发人员实现此接口。
 *
 * <p>
 * Copied and modified from <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>.
 * </p>
 * 从 <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a> 复制并修改。
 *
 * @since Apache Commons Lang 3.2
 * 自 Apache Commons Lang 3.2 版本起可用。
 */
public interface DatePrinter {

    /**
     * <p>Formats a millisecond {@code long} value.</p>
     * 格式化一个毫秒值的 {@code long} 类型。
     *
     * @param millis  the millisecond value to format
     * 要格式化的毫秒值
     * @return the formatted string
     * 格式化后的字符串
     * @since 2.1
     * 自 2.1 版本起可用。
     */
    String format(long millis);

    /**
     * <p>Formats a {@code Date} object using a {@code GregorianCalendar}.</p>
     * 使用 {@code GregorianCalendar} 格式化一个 {@code Date} 对象。
     *
     * @param date  the date to format
     * 要格式化的日期对象
     * @return the formatted string
     * 格式化后的字符串
     */
    String format(Date date);

    /**
     * <p>Formats a {@code Calendar} object.</p>
     * 格式化一个 {@code Calendar} 对象。
     * The TimeZone set on the Calendar is only used to adjust the time offset.
     * Calendar 上设置的时区仅用于调整时间偏移量。
     * The TimeZone specified during the construction of the Parser will determine the TimeZone
     * used in the formatted string.
     * 在解析器构造期间指定的时区将决定格式化字符串中使用的时区。
     *
     * @param calendar  the calendar to format.
     * 要格式化的日历对象。
     * @return the formatted string
     * 格式化后的字符串
     */
    String format(Calendar calendar);

    /**
     * <p>Formats a millisecond {@code long} value into the
     * supplied {@code Appendable}.</p>
     * 将一个毫秒值的 {@code long} 类型格式化到提供的 {@code Appendable} 中。
     *
     * @param millis  the millisecond value to format
     * 要格式化的毫秒值
     * @param buf  the buffer to format into
     * 要格式化到的缓冲区
     * @param <B> the Appendable class type, usually StringBuilder or StringBuffer.
     * Appendable 类类型，通常是 StringBuilder 或 StringBuffer。
     * @return the specified string buffer
     * 指定的字符串缓冲区
     * @since 3.5
     * 自 3.5 版本起可用。
     */
    <B extends Appendable> B format(long millis, B buf);

    /**
     * <p>Formats a {@code Date} object into the
     * supplied {@code Appendable} using a {@code GregorianCalendar}.</p>
     * 使用 {@code GregorianCalendar} 将一个 {@code Date} 对象格式化到提供的 {@code Appendable} 中。
     *
     * @param date  the date to format
     * 要格式化的日期对象
     * @param buf  the buffer to format into
     * 要格式化到的缓冲区
     * @param <B> the Appendable class type, usually StringBuilder or StringBuffer.
     * Appendable 类类型，通常是 StringBuilder 或 StringBuffer。
     * @return the specified string buffer
     * 指定的字符串缓冲区
     * @since 3.5
     * 自 3.5 版本起可用。
     */
    <B extends Appendable> B format(Date date, B buf);

    /**
     * <p>Formats a {@code Calendar} object into the supplied {@code Appendable}.</p>
     * 将一个 {@code Calendar} 对象格式化到提供的 {@code Appendable} 中。
     * The TimeZone set on the Calendar is only used to adjust the time offset.
     * Calendar 上设置的时区仅用于调整时间偏移量。
     * The TimeZone specified during the construction of the Parser will determine the TimeZone
     * used in the formatted string.
     * 在解析器构造期间指定的时区将决定格式化字符串中使用的时区。
     *
     * @param calendar  the calendar to format
     * 要格式化的日历对象
     * @param buf  the buffer to format into
     * 要格式化到的缓冲区
     * @param <B> the Appendable class type, usually StringBuilder or StringBuffer.
     * Appendable 类类型，通常是 StringBuilder 或 StringBuffer。
     * @return the specified string buffer
     * 指定的字符串缓冲区
     * @since 3.5
     * 自 3.5 版本起可用。
     */
    <B extends Appendable> B format(Calendar calendar, B buf);


    // Accessors
    // 访问器方法
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the pattern used by this printer.</p>
     * 获取此打印机使用的日期时间格式模式。
     *
     * @return the pattern, {@link java.text.SimpleDateFormat} compatible
     * 格式模式，与 {@link java.text.SimpleDateFormat} 兼容。
     */
    String getPattern();

    /**
     * <p>Gets the time zone used by this printer.</p>
     * 获取此打印机使用的时区。
     *
     * <p>This zone is always used for {@code Date} printing. </p>
     * 此区域始终用于 {@code Date} 打印。
     *
     * @return the time zone
     * 时区对象
     */
    TimeZone getTimeZone();

    /**
     * <p>Gets the locale used by this printer.</p>
     * 获取此打印机使用的区域设置。
     *
     * @return the locale
     * 区域设置对象
     */
    Locale getLocale();

    /**
     * <p>Formats a {@code Date}, {@code Calendar} or
     * {@code Long} (milliseconds) object.</p>
     * 格式化一个 {@code Date}、{@code Calendar} 或 {@code Long} (毫秒) 对象。
     *
     * @param obj  the object to format
     * 要格式化的对象
     * @param toAppendTo  the buffer to append to
     * 要将格式化结果追加到的缓冲区
     * @param pos  the position - ignored
     * 位置信息 - 此参数被忽略
     * @return the buffer passed in
     * 传入的缓冲区
     * @see java.text.DateFormat#format(Object, StringBuffer, FieldPosition)
     * 参见 {@link java.text.DateFormat#format(Object, StringBuffer, FieldPosition)}
     */
    StringBuilder format(Object obj, StringBuilder toAppendTo, FieldPosition pos);
}
