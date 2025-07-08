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

/*
 * This file originated from the Quartz scheduler with no change in licensing.
 * Copyright Terracotta, Inc.
 */
// 这个文件来源于 Quartz 调度器，没有改变许可。
// 版权所有 Terracotta, Inc.

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Provides a parser and evaluator for unix-like cron expressions. Cron
 * expressions provide the ability to specify complex time combinations such as
 * &quot;At 8:00am every Monday through Friday&quot; or &quot;At 1:30am every
 * last Friday of the month&quot;.
 * 提供一个用于解析和评估类 Unix cron 表达式的解析器和评估器。Cron 表达式能够指定复杂的时间组合，
 * 例如“每周一到周五上午 8:00”或“每月最后一个星期五凌晨 1:30”。
 * <P>
 * Cron expressions are comprised of 6 required fields and one optional field
 * separated by white space. The fields respectively are described as follows:
 * Cron 表达式由 6 个必填字段和一个可选字段组成，字段之间用空格分隔。这些字段分别描述如下：
 * <p/>
 * <table cellspacing="8">
 * <tr>
 * <th align="left">Field Name</th>
 * <th align="left">&nbsp;</th>
 * <th align="left">Allowed Values</th>
 * <th align="left">&nbsp;</th>
 * <th align="left">Allowed Special Characters</th>
 * </tr>
 * <tr>
 * <td align="left"><code>Seconds</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>0-59</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Minutes</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>0-59</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Hours</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>0-23</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Day-of-month</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>1-31</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * ? / L W</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Month</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>0-11 or JAN-DEC</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Day-of-Week</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>1-7 or SUN-SAT</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * ? / L #</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Year (Optional)</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>empty, 1970-2199</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * </table>
 * <P>
 * The '*' character is used to specify all values. For example, &quot;*&quot;
 * in the minute field means &quot;every minute&quot;.
 * 字符 '*' 用于指定所有值。例如，分钟字段中的 "*" 表示“每分钟”。
 * <P>
 * The '?' character is allowed for the day-of-month and day-of-week fields. It
 * is used to specify 'no specific value'. This is useful when you need to
 * specify something in one of the two fields, but not the other.
 * 字符 '?' 允许用于月份中的日期和星期几字段。它用于指定“无特定值”。
 * 这在您需要在两个字段中的一个指定某些内容，但不是另一个时很有用。
 * <P>
 * The '-' character is used to specify ranges For example &quot;10-12&quot; in
 * the hour field means &quot;the hours 10, 11 and 12&quot;.
 * 字符 '-' 用于指定范围。例如，小时字段中的 "10-12" 表示“10、11 和 12 小时”。
 * <P>
 * The ',' character is used to specify additional values. For example
 * &quot;MON,WED,FRI&quot; in the day-of-week field means &quot;the days Monday,
 * Wednesday, and Friday&quot;.
 * 字符 ',' 用于指定附加值。例如，星期几字段中的 "MON,WED,FRI" 表示“星期一、星期三和星期五”。
 * <P>
 * The '/' character is used to specify increments. For example &quot;0/15&quot;
 * in the seconds field means &quot;the seconds 0, 15, 30, and 45&quot;. And
 * &quot;5/15&quot; in the seconds field means &quot;the seconds 5, 20, 35, and
 * 50&quot;.  Specifying '*' before the  '/' is equivalent to specifying 0 is
 * the value to start with. Essentially, for each field in the expression, there
 * is a set of numbers that can be turned on or off. For seconds and minutes,
 * the numbers range from 0 to 59. For hours 0 to 23, for days of the month 0 to
 * 31, and for months 0 to 11 (JAN to DEC). The &quot;/&quot; character simply helps you turn
 * on every &quot;nth&quot; value in the given set. Thus &quot;7/6&quot; in the
 * month field only turns on month &quot;7&quot;, it does NOT mean every 6th
 * month, please note that subtlety.
 * 字符 '/' 用于指定增量。例如，秒字段中的 "0/15" 表示“第 0、15、30 和 45 秒”。
 * 而秒字段中的 "5/15" 表示“第 5、20、35 和 50 秒”。在 '/' 之前指定 '*' 等同于指定从 0 开始的值。
 * 本质上，对于表达式中的每个字段，都有一组可以启用或禁用的数字。
 * 对于秒和分钟，数字范围是 0 到 59。对于小时 0 到 23，对于月份中的日期 0 到 31，
 * 对于月份 0 到 11（JAN 到 DEC）。字符 "/" 只是帮助您在给定集合中启用每“第 n 个”值。
 * 因此，月份字段中的 "7/6" 只会启用月份 "7"，它不表示每 6 个月，请注意这种细微差别。
 * <P>
 * The 'L' character is allowed for the day-of-month and day-of-week fields.
 * This character is short-hand for &quot;last&quot;, but it has different
 * meaning in each of the two fields. For example, the value &quot;L&quot; in
 * the day-of-month field means &quot;the last day of the month&quot; - day 31
 * for January, day 28 for February on non-leap years. If used in the
 * day-of-week field by itself, it simply means &quot;7&quot; or
 * &quot;SAT&quot;. But if used in the day-of-week field after another value, it
 * means &quot;the last xxx day of the month&quot; - for example &quot;6L&quot;
 * means &quot;the last friday of the month&quot;. You can also specify an offset
 * from the last day of the month, such as "L-3" which would mean the third-to-last
 * day of the calendar month. <i>When using the 'L' option, it is important not to
 * specify lists, or ranges of values, as you'll get confusing/unexpected results.</i>
 * 字符 'L' 允许用于月份中的日期和星期几字段。这个字符是“最后”的缩写，但它在这两个字段中含义不同。
 * 例如，月份中的日期字段中的值 "L" 表示“当月的最后一天”——一月是第 31 天，非闰年的二月是第 28 天。
 * 如果单独用于星期几字段，它只表示“7”或“SAT”。但如果用于星期几字段中的另一个值之后，
 * 它表示“当月的最后一个 xxx 日”——例如 "6L" 表示“当月的最后一个星期五”。
 * 您还可以指定与当月最后一天的偏移量，例如 "L-3" 表示日历月的倒数第三天。
 * <i>当使用 'L' 选项时，重要的是不要指定列表或值范围，否则会得到令人困惑/意外的结果。</i>
 * <P>
 * The 'W' character is allowed for the day-of-month field.  This character
 * is used to specify the weekday (Monday-Friday) nearest the given day.  As an
 * example, if you were to specify &quot;15W&quot; as the value for the
 * day-of-month field, the meaning is: &quot;the nearest weekday to the 15th of
 * the month&quot;. So if the 15th is a Saturday, the trigger will fire on
 * Friday the 14th. If the 15th is a Sunday, the trigger will fire on Monday the
 * 16th. If the 15th is a Tuesday, then it will fire on Tuesday the 15th.
 * However if you specify &quot;1W&quot; as the value for day-of-month, and the
 * 1st is a Saturday, the trigger will fire on Monday the 3rd, as it will not
 * 'jump' over the boundary of a month's days.  The 'W' character can only be
 * specified when the day-of-month is a single day, not a range or list of days.
 * 字符 'W' 允许用于月份中的日期字段。此字符用于指定给定日期最近的工作日（星期一至星期五）。
 * 例如，如果您指定 "15W" 作为月份中的日期字段的值，则其含义是：“离当月 15 号最近的工作日”。
 * 因此，如果 15 号是星期六，触发器将在 14 号星期五触发。
 * 如果 15 号是星期日，触发器将在 16 号星期一触发。
 * 如果 15 号是星期二，那么它将在 15 号星期二触发。
 * 但是，如果您指定 "1W" 作为月份中的日期值，并且 1 号是星期六，触发器将在 3 号星期一触发，
 * 因为它不会“跳过”月份日期的边界。字符 'W' 只能在月份中的日期是单个日期时指定，不能是日期范围或列表。
 * <P>
 * The 'L' and 'W' characters can also be combined for the day-of-month
 * expression to yield 'LW', which translates to &quot;last weekday of the
 * month&quot;.
 * 字符 'L' 和 'W' 也可以组合用于月份中的日期表达式，形成 'LW'，表示“当月的最后一个工作日”。
 * <P>
 * The '#' character is allowed for the day-of-week field. This character is
 * used to specify &quot;the nth&quot; XXX day of the month. For example, the
 * value of &quot;6#3&quot; in the day-of-week field means the third Friday of
 * the month (day 6 = Friday and &quot;#3&quot; = the 3rd one in the month).
 * Other examples: &quot;2#1&quot; = the first Monday of the month and
 * &quot;4#5&quot; = the fifth Wednesday of the month. Note that if you specify
 * &quot;#5&quot; and there is not 5 of the given day-of-week in the month, then
 * no firing will occur that month.  If the '#' character is used, there can
 * only be one expression in the day-of-week field (&quot;3#1,6#3&quot; is
 * not valid, since there are two expressions).
 * 字符 '#' 允许用于星期几字段。此字符用于指定“当月的第 n 个”XXX 日。
 * 例如，星期几字段中的值 "6#3" 表示当月的第三个星期五（第 6 天 = 星期五，"#3" = 当月第三个）。
 * 其他示例："2#1" = 当月的第一个星期一，"4#5" = 当月的第五个星期三。
 * 请注意，如果您指定 "#5" 并且当月没有 5 个给定星期几的日期，那么该月将不会触发。
 * 如果使用 '#' 字符，则星期几字段中只能有一个表达式（"3#1,6#3" 无效，因为有两个表达式）。
 * <P>
 * * <P>
 * The legal characters and the names of months and days of the week are not
 * case sensitive.
 * 合法的字符以及月份和星期几的名称不区分大小写。
 * <p/>
 * <p>
 * <b>NOTES:</b>
 * <b>注意：</b>
 * <ul>
 * <li>Support for specifying both a day-of-week and a day-of-month value is
 * not complete (you'll need to use the '?' character in one of these fields).
 * 不支持同时指定星期几和月份中的日期值（您需要在其中一个字段中使用 '?' 字符）。
 * </li>
 * <li>Overflowing ranges is supported - that is, having a larger number on
 * the left hand side than the right. You might do 22-2 to catch 10 o'clock
 * at night until 2 o'clock in the morning, or you might have NOV-FEB. It is
 * very important to note that overuse of overflowing ranges creates ranges
 * that don't make sense and no effort has been made to determine which
 * interpretation CronExpression chooses. An example would be
 * "0 0 14-6 ? * FRI-MON". </li>
 * 支持溢出范围——即左侧的数字大于右侧。例如，您可以使用 22-2 来表示晚上 10 点到凌晨 2 点，
 * 或者使用 NOV-FEB。非常重要的是要注意，过度使用溢出范围会创建没有意义的范围，
 * 并且没有努力确定 CronExpression 选择哪种解释。一个例子是 "0 0 14-6 ? * FRI-MON"。
 * </ul>
 * </p>
 */
public final class CronExpression {

    protected static final int SECOND = 0;
    // 定义表示秒的字段索引
    protected static final int MINUTE = 1;
    // 定义表示分钟的字段索引
    protected static final int HOUR = 2;
    // 定义表示小时的字段索引
    protected static final int DAY_OF_MONTH = 3;
    // 定义表示月份中日期的字段索引
    protected static final int MONTH = 4;
    // 定义表示月份的字段索引
    protected static final int DAY_OF_WEEK = 5;
    // 定义表示星期中日期的字段索引
    protected static final int YEAR = 6;
    // 定义表示年份的字段索引
    protected static final int ALL_SPEC_INT = 99; // '*'
    // 定义通配符 '*' 的内部整数表示
    protected static final int NO_SPEC_INT = 98; // '?'
    // 定义无特定值 '?' 的内部整数表示
    protected static final Integer ALL_SPEC = ALL_SPEC_INT;
    // ALL_SPEC_INT 的包装类实例
    protected static final Integer NO_SPEC = NO_SPEC_INT;
    // NO_SPEC_INT 的包装类实例

    protected static final Map<String, Integer> monthMap = new HashMap<>(20);
    // 存储月份名称到数字映射的 Map
    protected static final Map<String, Integer> dayMap = new HashMap<>(60);
    // 存储星期几名称到数字映射的 Map

    static {
        // 静态初始化块，用于填充 monthMap 和 dayMap
        monthMap.put("JAN", 0);
        // 一月
        monthMap.put("FEB", 1);
        // 二月
        monthMap.put("MAR", 2);
        // 三月
        monthMap.put("APR", 3);
        // 四月
        monthMap.put("MAY", 4);
        // 五月
        monthMap.put("JUN", 5);
        // 六月
        monthMap.put("JUL", 6);
        // 七月
        monthMap.put("AUG", 7);
        // 八月
        monthMap.put("SEP", 8);
        // 九月
        monthMap.put("OCT", 9);
        // 十月
        monthMap.put("NOV", 10);
        // 十一月
        monthMap.put("DEC", 11);
        // 十二月

        dayMap.put("SUN", 1);
        // 星期日
        dayMap.put("MON", 2);
        // 星期一
        dayMap.put("TUE", 3);
        // 星期二
        dayMap.put("WED", 4);
        // 星期三
        dayMap.put("THU", 5);
        // 星期四
        dayMap.put("FRI", 6);
        // 星期五
        dayMap.put("SAT", 7);
        // 星期六
    }

    private final String cronExpression;
    // Cron 表达式的字符串表示
    private TimeZone timeZone = null;
    // Cron 表达式使用的时间区域
    protected transient TreeSet<Integer> seconds;
    // 存储秒的有效值集合
    protected transient TreeSet<Integer> minutes;
    // 存储分钟的有效值集合
    protected transient TreeSet<Integer> hours;
    // 存储小时的有效值集合
    protected transient TreeSet<Integer> daysOfMonth;
    // 存储月份中日期的有效值集合
    protected transient TreeSet<Integer> months;
    // 存储月份的有效值集合
    protected transient TreeSet<Integer> daysOfWeek;
    // 存储星期中日期的有效值集合
    protected transient TreeSet<Integer> years;
    // 存储年份的有效值集合

    protected transient boolean lastdayOfWeek = false;
    // 标记是否为星期中最后一天
    protected transient int nthdayOfWeek = 0;
    // 标记是否为星期中第 n 天（例如：第三个星期五）
    protected transient boolean lastdayOfMonth = false;
    // 标记是否为月份中最后一天
    protected transient boolean nearestWeekday = false;
    // 标记是否为最近的工作日
    protected transient int lastdayOffset = 0;
    // 从当月最后一天算起的偏移量（例如：L-3）
    protected transient boolean expressionParsed = false;
    // 标记表达式是否已解析

    public static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) + 100;
    // 定义 Cron 表达式支持的最大年份，为当前年份加 100
    public static final Calendar MIN_CAL = Calendar.getInstance();
    // 定义最小日历实例，用于设置最小日期
    static {
        // 静态初始化块，设置 MIN_CAL 到 1970 年 1 月 1 日
        MIN_CAL.set(1970, 0, 1);
    }
    public static final Date MIN_DATE = MIN_CAL.getTime();
    // 定义 Cron 表达式支持的最小日期，为 1970 年 1 月 1 日

    /**
     * Constructs a new <CODE>CronExpression</CODE> based on the specified
     * parameter.
     * 根据指定的参数构造一个新的 <CODE>CronExpression</CODE>。
     *
     * @param cronExpression String representation of the cron expression the
     *                       new object should represent
     * Cron 表达式的字符串表示，新对象将表示该表达式
     * @throws java.text.ParseException if the string expression cannot be parsed into a valid
     *                                  <CODE>CronExpression</CODE>
     * 如果字符串表达式无法解析为有效的 <CODE>CronExpression</CODE>
     */
    public CronExpression(final String cronExpression) throws ParseException {
        if (cronExpression == null) {
            // 如果 cronExpression 为 null，抛出 IllegalArgumentException
            throw new IllegalArgumentException("cronExpression cannot be null");
        }

        this.cronExpression = cronExpression.toUpperCase(Locale.US);
        // 将 cronExpression 转换为大写并存储

        buildExpression(this.cronExpression);
        // 调用 buildExpression 方法解析表达式
    }

    /**
     * Indicates whether the given date satisfies the cron expression. Note that
     * milliseconds are ignored, so two Dates falling on different milliseconds
     * of the same second will always have the same result here.
     * 指示给定日期是否满足 cron 表达式。请注意，毫秒被忽略，
     * 因此在同一秒的不同毫秒上落下的两个 Date 将始终具有相同的结果。
     *
     * @param date the date to evaluate
     * 要评估的日期
     * @return a boolean indicating whether the given date satisfies the cron
     * expression
     * 一个布尔值，指示给定日期是否满足 cron 表达式
     */
    public boolean isSatisfiedBy(final Date date) {
        final Calendar testDateCal = Calendar.getInstance(getTimeZone());
        // 获取一个 Calendar 实例，并设置时区
        testDateCal.setTime(date);
        // 设置 Calendar 的时间为给定日期
        testDateCal.set(Calendar.MILLISECOND, 0);
        // 将毫秒设置为 0，忽略毫秒差异
        final Date originalDate = testDateCal.getTime();
        // 存储原始日期（毫秒归零后）

        testDateCal.add(Calendar.SECOND, -1);
        // 将日期回退一秒，用于寻找下一个有效时间

        final Date timeAfter = getTimeAfter(testDateCal.getTime());
        // 获取回退一秒后的日期之后，满足 cron 表达式的下一个时间

        return ((timeAfter != null) && (timeAfter.equals(originalDate)));
        // 如果找到的下一个时间不为空且等于原始日期，则表示原始日期满足表达式
    }

    /**
     * Returns the next date/time <I>after</I> the given date/time which
     * satisfies the cron expression.
     * 返回给定日期/时间<I>之后</I>满足 cron 表达式的下一个日期/时间。
     *
     * @param date the date/time at which to begin the search for the next valid
     *             date/time
     * 开始搜索下一个有效日期/时间
     * @return the next valid date/time
     * 下一个有效日期/时间
     */
    public Date getNextValidTimeAfter(final Date date) {
        return getTimeAfter(date);
        // 调用 getTimeAfter 方法获取下一个有效时间
    }

    /**
     * Returns the next date/time <I>after</I> the given date/time which does
     * <I>not</I> satisfy the expression
     * 返回给定日期/时间<I>之后</I>不满足表达式的下一个日期/时间
     *
     * @param date the date/time at which to begin the search for the next
     *             invalid date/time
     * 开始搜索下一个无效日期/时间
     * @return the next valid date/time
     * 下一个有效日期/时间
     */
    public Date getNextInvalidTimeAfter(final Date date) {
        long difference = 1000;
        // 初始化时间差为 1000 毫秒 (1 秒)

        //move back to the nearest second so differences will be accurate
        // 移动到最近的秒，以便差异准确
        final Calendar adjustCal = Calendar.getInstance(getTimeZone());
        // 获取一个 Calendar 实例，并设置时区
        adjustCal.setTime(date);
        // 设置 Calendar 的时间为给定日期
        adjustCal.set(Calendar.MILLISECOND, 0);
        // 将毫秒设置为 0
        Date lastDate = adjustCal.getTime();
        // 存储调整后的日期作为上一个有效日期

        Date newDate;
        // 声明一个新的日期变量

        //FUTURE_TODO: (QUARTZ-481) IMPROVE THIS! The following is a BAD solution to this problem. Performance will be very bad here, depending on the cron expression. It is, however A solution.
        // TODO: 改进此处！以下是解决此问题的一个糟糕的解决方案。根据 cron 表达式，性能会非常差。
        // 但它确实是一个解决方案。

        //keep getting the next included time until it's farther than one second
        // apart. At that point, lastDate is the last valid fire time. We return
        // the second immediately following it.
        // 不断获取下一个包含的时间，直到它与上一个时间相隔超过一秒。
        // 此时，lastDate 是最后一个有效的触发时间。我们返回紧随其后的下一秒。
        while (difference == 1000) {
            // 当时间差为 1000 毫秒（1 秒）时循环
            newDate = getTimeAfter(lastDate);
            // 获取 lastDate 之后满足表达式的下一个时间
            if (newDate == null) {
                // 如果没有找到下一个有效时间，则跳出循环
                break;
            }

            difference = newDate.getTime() - lastDate.getTime();
            // 计算新旧时间之间的时间差

            if (difference == 1000) {
                // 如果时间差正好是 1 秒，说明连续的有效时间，继续迭代
                lastDate = newDate;
            }
        }

        return new Date(lastDate.getTime() + 1000);
        // 返回最后一个有效时间之后的一秒，即第一个不满足表达式的时间
    }

    /**
     * Returns the time zone for which this <code>CronExpression</code>
     * will be resolved.
     * 返回此 <code>CronExpression</code> 将被解析的时区。
     */
    public TimeZone getTimeZone() {
        if (timeZone == null) {
            // 如果时区未设置，则使用默认时区
            timeZone = TimeZone.getDefault();
        }

        return timeZone;
        // 返回当前设置的时区
    }

    /**
     * Sets the time zone for which  this <code>CronExpression</code>
     * will be resolved.
     * 设置此 <code>CronExpression</code> 将被解析的时区。
     */
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
        // 设置 Cron 表达式的时区
    }

    /**
     * Returns the string representation of the <CODE>CronExpression</CODE>
     * 返回 <CODE>CronExpression</CODE> 的字符串表示。
     *
     * @return a string representation of the <CODE>CronExpression</CODE>
     * <CODE>CronExpression</CODE> 的字符串表示
     */
    @Override
    public String toString() {
        return cronExpression;
        // 返回存储的原始 cron 表达式字符串
    }

    /**
     * Indicates whether the specified cron expression can be parsed into a
     * valid cron expression
     * 指示指定的 cron 表达式是否可以解析为有效的 cron 表达式。
     *
     * @param cronExpression the expression to evaluate
     * 要评估的表达式
     * @return a boolean indicating whether the given expression is a valid cron
     * expression
     * 一个布尔值，指示给定表达式是否是有效的 cron 表达式
     */
    public static boolean isValidExpression(final String cronExpression) {

        try {
            new CronExpression(cronExpression);
            // 尝试创建一个 CronExpression 实例来验证表达式
        } catch (final ParseException pe) {
            return false;
            // 如果解析失败，则表达式无效
        }

        return true;
        // 如果解析成功，则表达式有效
    }

    public static void validateExpression(final String cronExpression) throws ParseException {

        new CronExpression(cronExpression);
        // 尝试创建一个 CronExpression 实例来验证表达式，如果无效则抛出 ParseException
    }


    ////////////////////////////////////////////////////////////////////////////
    //
    // Expression Parsing Functions
    // 表达式解析函数
    //
    ////////////////////////////////////////////////////////////////////////////

    protected void buildExpression(final String expression) throws ParseException {
        expressionParsed = true;
        // 标记表达式已开始解析

        try {

            if (seconds == null) {
                seconds = new TreeSet<>();
            }
            // 如果秒的集合为空，则初始化为 TreeSet
            if (minutes == null) {
                minutes = new TreeSet<>();
            }
            // 如果分钟的集合为空，则初始化为 TreeSet
            if (hours == null) {
                hours = new TreeSet<>();
            }
            // 如果小时的集合为空，则初始化为 TreeSet
            if (daysOfMonth == null) {
                daysOfMonth = new TreeSet<>();
            }
            // 如果月份中日期的集合为空，则初始化为 TreeSet
            if (months == null) {
                months = new TreeSet<>();
            }
            // 如果月份的集合为空，则初始化为 TreeSet
            if (daysOfWeek == null) {
                daysOfWeek = new TreeSet<>();
            }
            // 如果星期中日期的集合为空，则初始化为 TreeSet
            if (years == null) {
                years = new TreeSet<>();
            }
            // 如果年份的集合为空，则初始化为 TreeSet

            int exprOn = SECOND;
            // 初始化当前正在解析的表达式字段索引为秒

            final StringTokenizer exprsTok = new StringTokenizer(expression, " \t",
                    false);
            // 使用空格和制表符作为分隔符，创建 StringTokenizer 来分割 Cron 表达式

            while (exprsTok.hasMoreTokens() && exprOn <= YEAR) {
                // 遍历表达式的每个字段，直到所有字段或达到年份字段
                final String expr = exprsTok.nextToken().trim();
                // 获取下一个字段表达式并去除首尾空格

                // throw an exception if L is used with other days of the month
                // 如果 'L' 与月份中的其他日期一起使用，则抛出异常
                if (exprOn == DAY_OF_MONTH && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(",")) {
                    throw new ParseException("Support for specifying 'L' and 'LW' with other days of the month is not implemented", -1);
                }
                // throw an exception if L is used with other days of the week
                // 如果 'L' 与星期中的其他日期一起使用，则抛出异常
                if (exprOn == DAY_OF_WEEK && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(",")) {
                    throw new ParseException("Support for specifying 'L' with other days of the week is not implemented", -1);
                }
                if (exprOn == DAY_OF_WEEK && expr.indexOf('#') != -1 && expr.indexOf('#', expr.indexOf('#') + 1) != -1) {
                    // 如果星期中日期字段包含多个 '#'，则抛出异常
                    throw new ParseException("Support for specifying multiple \"nth\" days is not implemented.", -1);
                }

                final StringTokenizer vTok = new StringTokenizer(expr, ",");
                // 使用逗号作为分隔符，创建 StringTokenizer 来分割当前字段的值
                while (vTok.hasMoreTokens()) {
                    // 遍历当前字段的每个值
                    final String v = vTok.nextToken();
                    // 获取下一个值
                    storeExpressionVals(0, v, exprOn);
                    // 存储表达式值到相应的 TreeSet
                }

                exprOn++;
                // 移动到下一个表达式字段
            }

            if (exprOn <= DAY_OF_WEEK) {
                // 如果在处理到星期中日期字段之前表达式就结束了，则表示表达式不完整
                throw new ParseException("Unexpected end of expression.",
                        expression.length());
            }

            if (exprOn <= YEAR) {
                // 如果表达式在年份字段之前结束，则默认年份字段为 '*'
                storeExpressionVals(0, "*", YEAR);
            }

            final TreeSet<Integer> dow = getSet(DAY_OF_WEEK);
            // 获取星期中日期的集合
            final TreeSet<Integer> dom = getSet(DAY_OF_MONTH);
            // 获取月份中日期的集合

            // Copying the logic from the UnsupportedOperationException below
            // 复制下方 UnsupportedOperationException 的逻辑
            final boolean dayOfMSpec = !dom.contains(NO_SPEC);
            // 检查月份中日期是否指定了具体值（不是 '?'）
            final boolean dayOfWSpec = !dow.contains(NO_SPEC);
            // 检查星期中日期是否指定了具体值（不是 '?'）

            if (!dayOfMSpec || dayOfWSpec) {
                // 如果月份中日期未指定但星期中日期指定，或两者都指定
                if (!dayOfWSpec || dayOfMSpec) {
                    // 如果星期中日期未指定但月份中日期指定，或两者都指定
                    // 这种情况表示同时指定了月份中日期和星期中日期，这是不支持的组合
                    throw new ParseException(
                            "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.", 0);
                }
            }
        } catch (final ParseException pe) {
            throw pe;
            // 重新抛出解析异常
        } catch (final Exception e) {
            throw new ParseException("Illegal cron expression format ("
                    + e.toString() + ")", 0);
            // 捕获其他异常并包装为 ParseException
        }
    }

    protected int storeExpressionVals(final int pos, final String s, final int type)
            throws ParseException {

        int incr = 0;
        // 初始化增量值
        int i = skipWhiteSpace(pos, s);
        // 跳过字符串开头的空白字符
        if (i >= s.length()) {
            return i;
            // 如果到达字符串末尾，直接返回当前位置
        }
        char c = s.charAt(i);
        // 获取当前字符
        if ((c >= 'A') && (c <= 'Z') && (!s.equals("L")) && (!s.equals("LW")) && (!s.matches("^L-[0-9]*[W]?"))) {
            // 如果当前字符是大写字母，并且不是 "L", "LW", 或 "L-nW" 形式
            String sub = s.substring(i, i + 3);
            // 提取三个字符的子串（例如：JAN, MON）
            int sval = -1;
            // 启动值
            int eval = -1;
            // 结束值
            if (type == MONTH) {
                // 如果当前字段是月份
                sval = getMonthNumber(sub) + 1;
                // 获取月份的数字表示 (1-12)
                if (sval <= 0) {
                    throw new ParseException("Invalid Month value: '" + sub + "'", i);
                    // 如果月份值无效，抛出异常
                }
                if (s.length() > i + 3) {
                    c = s.charAt(i + 3);
                    // 检查是否有 '-'
                    if (c == '-') {
                        i += 4;
                        sub = s.substring(i, i + 3);
                        eval = getMonthNumber(sub) + 1;
                        // 获取结束月份的数字表示
                        if (eval <= 0) {
                            throw new ParseException("Invalid Month value: '" + sub + "'", i);
                            // 如果结束月份值无效，抛出异常
                        }
                    }
                }
            } else if (type == DAY_OF_WEEK) {
                // 如果当前字段是星期中日期
                sval = getDayOfWeekNumber(sub);
                // 获取星期中日期的数字表示 (1-7)
                if (sval < 0) {
                    throw new ParseException("Invalid Day-of-Week value: '"
                            + sub + "'", i);
                    // 如果星期中日期值无效，抛出异常
                }
                if (s.length() > i + 3) {
                    c = s.charAt(i + 3);
                    // 检查后续字符
                    switch (c) {
                    case '-':
                        // 范围表示
                        i += 4;
                        sub = s.substring(i, i + 3);
                        eval = getDayOfWeekNumber(sub);
                        if (eval < 0) {
                            throw new ParseException(
                                    "Invalid Day-of-Week value: '" + sub
                                            + "'", i);
                        }
                        break;
                    case '#':
                        // 第 N 个星期几
                        try {
                            i += 4;
                            nthdayOfWeek = Integers.parseInt(s.substring(i));
                            // 解析第 N 个值
                            if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
                                throw new Exception();
                            }
                        } catch (final Exception e) {
                            throw new ParseException(
                                    "A numeric value between 1 and 5 must follow the '#' option",
                                    i);
                        }
                        break;
                    case 'L':
                        // 最后一个星期几
                        lastdayOfWeek = true;
                        i++;
                        break;
                    default:
                        break;
                    }
                }

            } else {
                throw new ParseException(
                        "Illegal characters for this position: '" + sub + "'",
                        i);
                // 对于当前位置的非法字符
            }
            if (eval != -1) {
                incr = 1;
                // 如果有结束值，增量设置为 1
            }
            addToSet(sval, eval, incr, type);
            // 将解析到的值添加到对应的 TreeSet 中
            return (i + 3);
            // 返回处理后的位置
        }

        switch (c) {
        case '?':
            // '?' 表示无特定值
            i++;
            if ((i + 1) < s.length()
                    && (s.charAt(i) != ' ' && s.charAt(i + 1) != '\t')) {
                // '?' 后不能有其他字符（除了空格或制表符）
                throw new ParseException("Illegal character after '?': "
                        + s.charAt(i), i);
            }
            if (type != DAY_OF_WEEK && type != DAY_OF_MONTH) {
                // '?' 只能用于星期中日期或月份中日期
                throw new ParseException(
                        "'?' can only be specfied for Day-of-Month or Day-of-Week.",
                        i);
            }
            if (type == DAY_OF_WEEK && !lastdayOfMonth) {
                // 如果是星期中日期且月份中日期没有指定 'L'，则不允许同时使用 '?'
                final int val = daysOfMonth.last();
                if (val == NO_SPEC_INT) {
                    throw new ParseException(
                            "'?' can only be specfied for Day-of-Month -OR- Day-of-Week.",
                            i);
                }
            }
            addToSet(NO_SPEC_INT, -1, 0, type);
            // 添加无特定值到集合
            return i;
        case '*':
        case '/':
            // '*' 或 '/' 表示所有值或增量
            if (c == '*' && (i + 1) >= s.length()) {
                // 如果是 '*' 且是最后一个字符
                addToSet(ALL_SPEC_INT, -1, incr, type);
                // 添加所有值到集合
                return i + 1;
            } else if (c == '/'
                    && ((i + 1) >= s.length() || s.charAt(i + 1) == ' ' || s
                    .charAt(i + 1) == '\t')) {
                // 如果是 '/' 但后面没有数字
                throw new ParseException("'/' must be followed by an integer.", i);
            } else if (c == '*') {
                i++;
                // 如果是 '*'，跳过当前字符
            }
            c = s.charAt(i);
            if (c == '/') { // is an increment specified?
                // 如果是增量指定
                i++;
                if (i >= s.length()) {
                    throw new ParseException("Unexpected end of string.", i);
                    // 字符串意外结束
                }

                incr = getNumericValue(s, i);
                // 获取增量值

                i++;
                if (incr > 10) {
                    i++;
                }
                if (incr > 59 && (type == SECOND || type == MINUTE)) {
                    // 秒和分钟增量不能大于 59
                    throw new ParseException("Increment > 60 : " + incr, i);
                } else if (incr > 23 && (type == HOUR)) {
                    // 小时增量不能大于 23
                    throw new ParseException("Increment > 24 : " + incr, i);
                } else if (incr > 31 && (type == DAY_OF_MONTH)) {
                    // 月份中日期增量不能大于 31
                    throw new ParseException("Increment > 31 : " + incr, i);
                } else if (incr > 7 && (type == DAY_OF_WEEK)) {
                    // 星期中日期增量不能大于 7
                    throw new ParseException("Increment > 7 : " + incr, i);
                } else if (incr > 12 && (type == MONTH)) {
                    // 月份增量不能大于 12
                    throw new ParseException("Increment > 12 : " + incr, i);
                }
            } else {
                incr = 1;
                // 如果没有增量指定，默认为 1
            }
            addToSet(ALL_SPEC_INT, -1, incr, type);
            // 添加所有值和增量到集合
            return i;
        case 'L':
            // 'L' 表示最后一天/星期
            i++;
            if (type == DAY_OF_MONTH) {
                lastdayOfMonth = true;
                // 如果是月份中日期，标记为最后一天
            }
            if (type == DAY_OF_WEEK) {
                addToSet(7, 7, 0, type);
                // 如果是星期中日期，添加星期六（7）
            }
            if (type == DAY_OF_MONTH && s.length() > i) {
                c = s.charAt(i);
                if (c == '-') {
                    // L-offset
                    final ValueSet vs = getValue(0, s, i + 1);
                    lastdayOffset = vs.value;
                    // 解析偏移量
                    if (lastdayOffset > 30) {
                        throw new ParseException("Offset from last day must be <= 30", i + 1);
                        // 偏移量不能大于 30
                    }
                    i = vs.pos;
                }
                if (s.length() > i) {
                    c = s.charAt(i);
                    if (c == 'W') {
                        // LW
                        nearestWeekday = true;
                        i++;
                    }
                }
            }
            return i;
        default:
            if (c >= '0' && c <= '9') {
                // 数字值
                int val = Integer.parseInt(String.valueOf(c));
                // 解析第一个数字
                i++;
                if (i >= s.length()) {
                    addToSet(val, -1, -1, type);
                    // 如果到达字符串末尾，添加单个值
                } else {
                    c = s.charAt(i);
                    if (c >= '0' && c <= '9') {
                        final ValueSet vs = getValue(val, s, i);
                        val = vs.value;
                        // 解析多位数字
                        i = vs.pos;
                    }
                    i = checkNext(i, s, val, type);
                    // 检查后续字符
                    return i;
                }
            } else {
                throw new ParseException("Unexpected character: " + c, i);
                // 意外的字符
            }
            break;
        }

        return i;
    }

    protected int checkNext(final int pos, final String s, final int val, final int type)
            throws ParseException {

        int end = -1;
        // 结束值
        // The end value of a range. Initialized to -1.
        int i = pos;

        if (i >= s.length()) {
            addToSet(val, end, -1, type);
            // 如果到达字符串末尾，添加单个值
            // If the current position 'i' has reached or exceeded the length of the string 's',
            // it means we are at the end of the expression. Add the 'val' to the set
            // with no specified end or increment, effectively adding it as a single value.
            return i;
        }

        char c = s.charAt(pos);
        // 获取当前字符
        // Get the character at the current parsing position.

        if (c == 'L') {
            // 'L' 表示最后一天/星期
            // 'L' stands for "last day" (of month) or "last weekday" (of week).
            if (type == DAY_OF_WEEK) {
                if (val < 1 || val > 7) {
                    throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
                    // 星期中日期值必须在 1 到 7 之间
                    // Throws an exception if 'L' is used with a day-of-week value outside the valid range [1, 7].
                }
                lastdayOfWeek = true;
                // 标记为星期中最后一天
                // Set the flag to indicate that "last day of week" logic should be applied.
            } else {
                throw new ParseException("'L' option is not valid here. (pos=" + i + ")", i);
                // 'L' 选项在此处无效
                // Throws an exception if 'L' is used for types other than DAY_OF_WEEK.
            }
            final TreeSet<Integer> set = getSet(type);
            set.add(val);
            // 将值添加到集合
            // Add the parsed value to the corresponding set (e.g., daysOfWeek).
            i++;
            return i;
        }

        if (c == 'W') {
            // 'W' 表示最近的工作日
            // 'W' stands for "weekday nearest day of month".
            if (type == DAY_OF_MONTH) {
                nearestWeekday = true;
                // 标记为最近的工作日
                // Set the flag to indicate that "nearest weekday" logic should be applied.
            } else {
                throw new ParseException("'W' option is not valid here. (pos=" + i + ")", i);
                // 'W' 选项在此处无效
                // Throws an exception if 'W' is used for types other than DAY_OF_MONTH.
            }
            if (val > 31) {
                throw new ParseException("The 'W' option does not make sense with values larger than 31 (max number of days in a month)", i);
                // 'W' 选项不适用于大于 31 的值
                // Throws an exception if 'W' is used with a day-of-month value greater than 31, as it's illogical.
            }
            final TreeSet<Integer> set = getSet(type);
            set.add(val);
            // 将值添加到集合
            // Add the parsed value to the corresponding set (e.g., daysOfMonth).
            i++;
            return i;
        }

        switch (c) {
        case '#':
            // '#' 表示第 N 个星期几
            // '#' stands for "Nth day of week" in a month.
            if (type != DAY_OF_WEEK) {
                throw new ParseException("'#' option is not valid here. (pos=" + i + ")", i);
                // '#' 选项在此处无效
                // Throws an exception if '#' is used for types other than DAY_OF_WEEK.
            }
            i++;
            try {
                nthdayOfWeek = Integers.parseInt(s.substring(i));
                // 解析第 N 个值
                // Parse the integer following '#' to determine which occurrence of the day of the week.
                if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
                    throw new Exception();
                }
            } catch (final Exception e) {
                throw new ParseException(
                        "A numeric value between 1 and 5 must follow the '#' option",
                        i);
                // '#' 选项后必须跟一个 1 到 5 之间的数字
                // Throws an exception if the value after '#' is not a number between 1 and 5.
            }
            final TreeSet<Integer> set = getSet(type);
            set.add(val);
            // 将值添加到集合
            // Add the parsed value to the corresponding set (e.g., daysOfWeek).
            i++;
            return i;
        case '-':
            // 范围表示
            // '-' indicates a range of values (e.g., 10-15).
            i++;
            c = s.charAt(i);
            final int v = Integer.parseInt(String.valueOf(c));
            end = v;
            // 解析范围的结束值
            // Parse the first digit of the end value of the range.
            i++;
            if (i >= s.length()) {
                addToSet(val, end, 1, type);
                // 如果到达字符串末尾，添加范围值（增量为 1）
                // If the string ends after parsing the end of the range, add the range with an increment of 1.
                return i;
            }
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                final ValueSet vs = getValue(v, s, i);
                end = vs.value;
                // 解析多位数字的结束值
                // If there are more digits, parse the complete multi-digit end value.
                i = vs.pos;
            }
            if (i < s.length() && ((c = s.charAt(i)) == '/')) {
                // 如果有增量
                // If a '/' character is found, it indicates an increment (step) value.
                i++;
                c = s.charAt(i);
                final int v2 = Integer.parseInt(String.valueOf(c));
                i++;
                if (i >= s.length()) {
                    addToSet(val, end, v2, type);
                    // 添加范围值和增量
                    // If the string ends after the increment, add the range with the parsed increment.
                    return i;
                }
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    final ValueSet vs = getValue(v2, s, i);
                    final int v3 = vs.value;
                    addToSet(val, end, v3, type);
                    // 添加范围值和多位增量
                    // If there are more digits for the increment, parse the complete multi-digit increment.
                    i = vs.pos;
                } else {
                    addToSet(val, end, v2, type);
                    // 添加范围值和增量
                    // If no more digits for increment, add the range with the single-digit increment.
                }
                return i;
            } else {
                addToSet(val, end, 1, type);
                // 添加范围值（增量为 1）
                // If no increment '/' is found, add the range with a default increment of 1.
                return i;
            }
        case '/':
            // 增量表示
            // '/' indicates an increment (step) value (e.g., */5, 10/2).
            i++;
            c = s.charAt(i);
            final int v2 = Integer.parseInt(String.valueOf(c));
            i++;
            if (i >= s.length()) {
                addToSet(val, end, v2, type);
                // 添加值和增量
                // If the string ends after the increment, add the value with the parsed increment.
                return i;
            }
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                final ValueSet vs = getValue(v2, s, i);
                final int v3 = vs.value;
                addToSet(val, end, v3, type);
                // 添加值和多位增量
                // If there are more digits for the increment, parse the complete multi-digit increment.
                i = vs.pos;
                return i;
            } else {
                throw new ParseException("Unexpected character '" + c + "' after '/'", i);
            }
        default:
            break;
        }

        addToSet(val, end, 0, type);
        // This is the default case if no special character ('L', 'W', '#', '-', '/') is found.
        // It means 'val' is a single value without a range or increment.
        i++;
        return i;
    }

    public String getCronExpression() {
        // 方法功能：获取原始的 Cron 表达式字符串。
        // Main function: Retrieves the original Cron expression string.
        // 返回值：表示 Cron 表达式的字符串。
        // Return value: A string representing the Cron expression.
        return cronExpression;
    }

    public String getExpressionSummary() {
        // 方法功能：生成 Cron 表达式各部分的摘要信息。
        // Main function: Generates a summary string of the various components of the Cron expression.
        // 它会按照秒、分、小时、月中日期、月份、星期中日期、以及特殊标记（如 lastdayOfWeek、nearestWeekday、nthdayOfWeek、lastdayOfMonth、年份）的顺序，
        // 拼接成一个易于阅读的字符串。
        // It concatenates a readable string in the order of seconds, minutes, hours, days of month, months, days of week,
        // and special flags (like lastdayOfWeek, nearestWeekday, nthdayOfWeek, lastdayOfMonth, years).
        // 返回值：Cron 表达式的摘要字符串。
        // Return value: A summary string of the Cron expression.
        final StringBuilder buf = new StringBuilder();

        buf.append("seconds: ");
        // UI/样式设置的目的: 添加标签“seconds:”用于标识秒字段的摘要。
        // Purpose of UI/style setting: Adds the label "seconds:" to identify the summary for the seconds field.
        buf.append(getExpressionSetSummary(seconds));
        // 将秒字段的解析结果摘要添加到缓冲区。
        // Appends the summary of the parsed seconds field to the buffer.
        buf.append("\n");
        // 添加换行符，使每个字段的摘要独立一行。
        // Adds a newline character to put each field's summary on a separate line.
        buf.append("minutes: ");
        // UI/样式设置的目的: 添加标签“minutes:”。
        // Purpose of UI/style setting: Adds the label "minutes:".
        buf.append(getExpressionSetSummary(minutes));
        // 将分钟字段的解析结果摘要添加到缓冲区。
        // Appends the summary of the parsed minutes field to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("hours: ");
        // UI/样式设置的目的: 添加标签“hours:”。
        // Purpose of UI/style setting: Adds the label "hours:".
        buf.append(getExpressionSetSummary(hours));
        // 将小时字段的解析结果摘要添加到缓冲区。
        // Appends the summary of the parsed hours field to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("daysOfMonth: ");
        // UI/样式设置的目的: 添加标签“daysOfMonth:”。
        // Purpose of UI/style setting: Adds the label "daysOfMonth:".
        buf.append(getExpressionSetSummary(daysOfMonth));
        // 将月中日期字段的解析结果摘要添加到缓冲区。
        // Appends the summary of the parsed days of month field to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("months: ");
        // UI/样式设置的目的: 添加标签“months:”。
        // Purpose of UI/style setting: Adds the label "months:".
        buf.append(getExpressionSetSummary(months));
        // 将月份字段的解析结果摘要添加到缓冲区。
        // Appends the summary of the parsed months field to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("daysOfWeek: ");
        // UI/样式设置的目的: 添加标签“daysOfWeek:”。
        // Purpose of UI/style setting: Adds the label "daysOfWeek:".
        buf.append(getExpressionSetSummary(daysOfWeek));
        // 将星期中日期字段的解析结果摘要添加到缓冲区。
        // Appends the summary of the parsed days of week field to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("lastdayOfWeek: ");
        // UI/样式设置的目的: 添加标签“lastdayOfWeek:”。
        // Purpose of UI/style setting: Adds the label "lastdayOfWeek:".
        buf.append(lastdayOfWeek);
        // 将 lastdayOfWeek 标志的值添加到缓冲区。
        // Appends the value of the lastdayOfWeek flag to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("nearestWeekday: ");
        // UI/样式设置的目的: 添加标签“nearestWeekday:”。
        // Purpose of UI/style setting: Adds the label "nearestWeekday:".
        buf.append(nearestWeekday);
        // 将 nearestWeekday 标志的值添加到缓冲区。
        // Appends the value of the nearestWeekday flag to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("NthDayOfWeek: ");
        // UI/样式设置的目的: 添加标签“NthDayOfWeek:”。
        // Purpose of UI/style setting: Adds the label "NthDayOfWeek:".
        buf.append(nthdayOfWeek);
        // 将 nthdayOfWeek 标志的值添加到缓冲区。
        // Appends the value of the nthdayOfWeek flag to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("lastdayOfMonth: ");
        // UI/样式设置的目的: 添加标签“lastdayOfMonth:”。
        // Purpose of UI/style setting: Adds the label "lastdayOfMonth:".
        buf.append(lastdayOfMonth);
        // 将 lastdayOfMonth 标志的值添加到缓冲区。
        // Appends the value of the lastdayOfMonth flag to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.
        buf.append("years: ");
        // UI/样式设置的目的: 添加标签“years:”。
        // Purpose of UI/style setting: Adds the label "years:".
        buf.append(getExpressionSetSummary(years));
        // 将年份字段的解析结果摘要添加到缓冲区。
        // Appends the summary of the parsed years field to the buffer.
        buf.append("\n");
        // 添加换行符。
        // Adds a newline character.

        return buf.toString();
        // 返回构建好的摘要字符串。
        // Returns the built summary string.
    }

    protected String getExpressionSetSummary(final java.util.Set<Integer> set) {
        // 方法功能：为整数集合生成 Cron 表达式风格的摘要字符串。
        // Main function: Generates a Cron expression-style summary string for a set of integers.
        // 参数：set - 包含整数的集合，代表 Cron 表达式某个字段的值（如秒、分钟等）。
        // Parameters: set - A set containing integers, representing values for a specific Cron expression field (e.g., seconds, minutes).
        // 返回值：表示集合内容的字符串摘要。
        // Return value: A string summary representing the content of the set.

        if (set.contains(NO_SPEC)) {
            return "?";
        }
        if (set.contains(ALL_SPEC)) {
            return "*";
        }

        final StringBuilder buf = new StringBuilder();

        final Iterator<Integer> itr = set.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            final Integer iVal = itr.next();
            final String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }

        return buf.toString();
    }

    protected String getExpressionSetSummary(final java.util.ArrayList<Integer> list) {
        // 方法功能：为整数列表生成 Cron 表达式风格的摘要字符串。
        // Main function: Generates a Cron expression-style summary string for an ArrayList of integers.
        // 参数：list - 包含整数的列表，代表 Cron 表达式某个字段的值。
        // Parameters: list - An ArrayList containing integers, representing values for a specific Cron expression field.
        // 返回值：表示列表内容的字符串摘要。
        // Return value: A string summary representing the content of the list.

        if (list.contains(NO_SPEC)) {
            return "?";
        }
        if (list.contains(ALL_SPEC)) {
            return "*";
        }

        final StringBuilder buf = new StringBuilder();

        final Iterator<Integer> itr = list.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            final Integer iVal = itr.next();
            final String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }

        return buf.toString();
    }

    protected int skipWhiteSpace(int i, final String s) {
        // 方法功能：跳过字符串中的空白字符（空格和制表符）。
        // Main function: Skips whitespace characters (spaces and tabs) in a string.
        // 参数：
        //   i - 当前在字符串中开始跳过的位置。
        //   s - 要处理的输入字符串。
        // Parameters:
        //   i - The current position in the string to start skipping from.
        //   s - The input string to process.
        // 返回值：跳过所有空白字符后的新位置索引。
        // Return value: The new index after skipping all whitespace characters.
        // 代码执行流程：从当前位置 'i' 开始，循环检查字符是否为空格或制表符，如果是则递增 'i'。
        // Code execution flow: Starts from the current position 'i', loops to check if the character is a space or tab, and increments 'i' if it is.
        for (; i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t'); i++) {
            // empty
            // 循环体为空，只用于递增索引。
            // The loop body is empty, serving only to increment the index.
        }

        return i;
    }

    protected int findNextWhiteSpace(int i, final String s) {
        // 方法功能：查找字符串中下一个空白字符（空格或制表符）的位置。
        // Main function: Finds the position of the next whitespace character (space or tab) in a string.
        // 参数：
        //   i - 当前在字符串中开始查找的位置。
        //   s - 要处理的输入字符串。
        // Parameters:
        //   i - The current position in the string to start searching from.
        //   s - The input string to process.
        // 返回值：下一个空白字符的索引，如果没有找到则返回字符串的长度。
        // Return value: The index of the next whitespace character, or the length of the string if none is found.
        // 代码执行流程：从当前位置 'i' 开始，循环检查字符是否为非空白字符，如果是则递增 'i'。当遇到空白字符或到达字符串末尾时停止。
        // Code execution flow: Starts from the current position 'i', loops to check if the character is a non-whitespace character, and increments 'i' if it is. Stops when a whitespace character is encountered or the end of the string is reached.
        for (; i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t'); i++) {
            // empty
            // 循环体为空，只用于递增索引。
            // The loop body is empty, serving only to increment the index.
        }

        return i;
    }

    protected void addToSet(final int val, final int end, int incr, final int type)
            throws ParseException {
        // 方法功能：根据给定的值、范围和增量，将时间单元（如秒、分钟、小时等）添加到对应的集合中。
        // Main function: Adds time unit values (like seconds, minutes, hours, etc.) to their respective sets based on the given value, range, and increment.
        // 它会根据类型对值进行有效性检查，并处理单值、范围和带增量的范围。
        // It performs validation checks on values based on their type and handles single values, ranges, and ranges with increments.
        // 参数：
        //   val - 要添加的起始值或单个值。
        //   end - 范围的结束值（如果不是范围，则为 -1）。
        //   incr - 值的增量或步长（如果不是增量，则为 0 或 -1）。
        //   type - 时间单元的类型（例如 SECOND, MINUTE, HOUR, DAY_OF_MONTH, MONTH, DAY_OF_WEEK, YEAR）。
        // Parameters:
        //   val - The starting value or a single value to be added.
        //   end - The end value of a range (or -1 if not a range).
        //   incr - The increment or step for values (or 0 or -1 if no increment).
        //   type - The type of time unit (e.g., SECOND, MINUTE, HOUR, DAY_OF_MONTH, MONTH, DAY_OF_WEEK, YEAR).
        // 抛出：ParseException - 如果值超出其类型的有效范围。
        // Throws: ParseException - If a value is outside its valid range for the given type.

        final TreeSet<Integer> set = getSet(type);
        // 获取对应时间类型的集合，用于存储解析后的值。
        // Retrieves the TreeSet corresponding to the given time 'type' to store parsed values.

        switch (type) {
        case SECOND:
        case MINUTE:
            if ((val < 0 || val > 59 || end > 59) && (val != ALL_SPEC_INT)) {
                throw new ParseException(
                        "Minute and Second values must be between 0 and 59",
                        -1);
            }
            break;
        case HOUR:
            if ((val < 0 || val > 23 || end > 23) && (val != ALL_SPEC_INT)) {
                throw new ParseException(
                        "Hour values must be between 0 and 23", -1);
            }
            break;
        case DAY_OF_MONTH:
            if ((val < 1 || val > 31 || end > 31) && (val != ALL_SPEC_INT)
                    && (val != NO_SPEC_INT)) {
                throw new ParseException(
                        "Day of month values must be between 1 and 31", -1);
            }
            break;
        case MONTH:
            if ((val < 1 || val > 12 || end > 12) && (val != ALL_SPEC_INT)) {
                throw new ParseException(
                        "Month values must be between 1 and 12", -1);
            }
            break;
        case DAY_OF_WEEK:
            if ((val == 0 || val > 7 || end > 7) && (val != ALL_SPEC_INT)
                    && (val != NO_SPEC_INT)) {
                throw new ParseException(
                        "Day-of-Week values must be between 1 and 7", -1);
            }
            break;
        default:
            break;
        }

        if ((incr == 0 || incr == -1) && val != ALL_SPEC_INT) {
            // 如果增量为 0 或 -1 (表示没有指定增量)，且不是 "所有值" 的特殊标记。
            // If the increment is 0 or -1 (meaning no increment specified), and the value is not the "all values" special marker.
            if (val != -1) {
                set.add(val);
                // 如果值不是 -1 (表示未指定)，则将单个值添加到集合。
                // If 'val' is not -1 (unspecified), add the single value to the set.
            } else {
                set.add(NO_SPEC);
                // 如果值是 -1，表示不指定，则添加 NO_SPEC 标记。
                // If 'val' is -1, it indicates no specific value, so add the NO_SPEC marker.
            }

            return;
        }

        int startAt = val;
        int stopAt = end;

        if (val == ALL_SPEC_INT && incr <= 0) {
            // 如果是 "所有值" 且增量未指定或无效。
            // If the value is "all values" (ALL_SPEC_INT) and the increment is not specified or invalid.
            incr = 1;
            // 将增量设置为 1，以便遍历所有可能的值。
            // Set increment to 1 to iterate through all possible values.
            set.add(ALL_SPEC); // put in a marker, but also fill values
            // 添加 ALL_SPEC 标记，同时继续填充具体值。
            // Add the ALL_SPEC marker, but also proceed to fill in individual values.
        }

        switch (type) {
        case SECOND:
        case MINUTE:
            if (stopAt == -1) {
                stopAt = 59;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 0;
            }
            break;
        case HOUR:
            if (stopAt == -1) {
                stopAt = 23;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 0;
            }
            break;
        case DAY_OF_MONTH:
            if (stopAt == -1) {
                stopAt = 31;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
            break;
        case MONTH:
            if (stopAt == -1) {
                stopAt = 12;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
            break;
        case DAY_OF_WEEK:
            if (stopAt == -1) {
                stopAt = 7;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
            break;
        case YEAR:
            if (stopAt == -1) {
                stopAt = MAX_YEAR;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1970;
            }
            break;
        default:
            break;
        }

        // if the end of the range is before the start, then we need to overflow into
        // the next day, month etc. This is done by adding the maximum amount for that
        // type, and using modulus max to determine the value being added.
        // 如果范围的结束值在开始值之前，这意味着范围跨越了时间单位的边界（例如，23-2 表示从晚上11点到凌晨2点）。
        // 这需要通过加上该时间单位的最大值来处理，并使用模运算确定要添加的实际值。
        // 特殊处理逻辑和注意事项: 处理跨越边界的范围（如“23-2”表示小时）。
        // Special handling logic and notes: Handles ranges that span boundaries (e.g., "23-2" for hours).
        int max = -1;
        if (stopAt < startAt) {
            switch (type) {
                case SECOND:
                    max = 60;
                    break;
                case MINUTE:
                    max = 60;
                    break;
                case HOUR:
                    max = 24;
                    break;
                case MONTH:
                    max = 12;
                    break;
                case DAY_OF_WEEK:
                    max = 7;
                    break;
                case DAY_OF_MONTH:
                    max = 31;
                    break;
                case YEAR:
                    throw new IllegalArgumentException("Start year must be less than stop year");
                default:
                    throw new IllegalArgumentException("Unexpected type encountered");
            }
            stopAt += max;
            // 将结束值增加最大值，以表示跨越边界的范围。
            // Add the maximum value to the stopAt to represent the range spanning the boundary.
        }

        for (int i = startAt; i <= stopAt; i += incr) {
            // 遍历从 startAt 到 stopAt 的所有值，并按 incr 递增。
            // Iterate through all values from startAt to stopAt, incrementing by incr.
            if (max == -1) {
                // ie: there's no max to overflow over
                // 如果没有最大值（即范围不跨越边界），直接添加当前值。
                // If there's no maximum (i.e., the range does not span a boundary), directly add the current value.
                set.add(i);
            } else {
                // take the modulus to get the real value
                // 如果存在最大值（即范围跨越边界），则使用模运算获取实际值。
                // If a maximum exists (i.e., the range spans a boundary), use the modulus operation to get the real value.
                int i2 = i % max;

                // 1-indexed ranges should not include 0, and should include their max
                // 对于从 1 开始计数的范围（如月份、星期中的日期、月中日期），0 应该被视为最大值。
                // For 1-indexed ranges (like MONTH, DAY_OF_WEEK, DAY_OF_MONTH), 0 should be treated as the maximum value.
                if (i2 == 0 && (type == MONTH || type == DAY_OF_WEEK || type == DAY_OF_MONTH)) {
                    i2 = max;
                }

                set.add(i2);
                // 将计算出的实际值添加到集合中。
                // Add the calculated real value to the set.
            }
        }
    }

    TreeSet<Integer> getSet(final int type) {
        // 方法功能：根据给定的时间单元类型，返回对应的 TreeSet 集合。
        // Main function: Returns the corresponding TreeSet collection based on the given time unit type.
        // 参数：type - 时间单元的类型（例如 SECOND, MINUTE, HOUR 等）。
        // Parameters: type - The type of time unit (e.g., SECOND, MINUTE, HOUR, etc.).
        // 返回值：指定类型的整数集合，如果类型无效则返回 null。
        // Return value: The TreeSet of integers for the specified type, or null if the type is invalid.
        // 关键变量用途：seconds, minutes, hours, daysOfMonth, months, daysOfWeek, years - 分别存储对应时间单位的 Cron 表达式值。
        // Purpose of key variables: seconds, minutes, hours, daysOfMonth, months, daysOfWeek, years - Store the Cron expression values for the corresponding time units.
        switch (type) {
            case SECOND:
                return seconds;
            case MINUTE:
                return minutes;
            case HOUR:
                return hours;
            case DAY_OF_MONTH:
                return daysOfMonth;
            case MONTH:
                return months;
            case DAY_OF_WEEK:
                return daysOfWeek;
            case YEAR:
                return years;
            default:
                return null;
        }
    }

    protected ValueSet getValue(final int v, final String s, int i) {
        // 方法功能：从字符串中解析一个多位数字的值。
        // Main function: Parses a multi-digit numeric value from a string.
        // 参数：
        //   v - 已经解析的第一个数字。
        //   s - 要解析的输入字符串。
        //   i - 当前在字符串中开始解析的位置。
        // Parameters:
        //   v - The first digit already parsed.
        //   s - The input string to parse from.
        //   i - The current position in the string to start parsing.
        // 返回值：一个 ValueSet 对象，包含解析到的完整值和解析结束后的新位置。
        // Return value: A ValueSet object containing the parsed complete value and the new position after parsing.
        // 代码执行流程：从给定位置开始，将连续的数字字符追加到 StringBuilder 中，直到遇到非数字字符或字符串末尾。
        // Code execution flow: Starting from the given position, appends consecutive digit characters to a StringBuilder until a non-digit character or the end of the string is encountered.
        char c = s.charAt(i);
        final StringBuilder s1 = new StringBuilder(String.valueOf(v));
        // 将第一个数字添加到 StringBuilder。
        // Appends the first digit to the StringBuilder.
        while (c >= '0' && c <= '9') {
            // 循环直到遇到非数字字符。
            // Loop until a non-digit character is encountered.
            s1.append(c);
            // 将当前数字字符追加到 StringBuilder。
            // Appends the current digit character to the StringBuilder.
            i++;
            // 移动到下一个字符。
            // Move to the next character.
            if (i >= s.length()) {
                // 如果到达字符串末尾，则退出循环。
                // If the end of the string is reached, break the loop.
                break;
            }
            c = s.charAt(i);
            // 获取下一个字符。
            // Get the next character.
        }
        final ValueSet val = new ValueSet();

        val.pos = (i < s.length()) ? i : i + 1;
        // 设置 ValueSet 的 pos 字段为解析结束后的新位置。
        // Sets the 'pos' field of the ValueSet to the new position after parsing.
        val.value = Integers.parseInt(s1.toString());
        // 将 StringBuilder 中的字符串转换为整数，并设置 ValueSet 的 value 字段。
        // Converts the string in the StringBuilder to an integer and sets the 'value' field of the ValueSet.
        return val;
    }

    protected int getNumericValue(final String s, final int i) {
        // 方法功能：从字符串的指定位置开始，解析并返回一个数字值，直到遇到空白字符。
        // Main function: Parses and returns a numeric value from the specified position in a string until a whitespace character is encountered.
        // 参数：
        //   s - 要解析的输入字符串。
        //   i - 当前在字符串中开始解析的位置。
        // Parameters:
        //   s - The input string to parse from.
        //   i - The current position in the string to start parsing.
        // 返回值：解析到的整数值。
        // Return value: The parsed integer value.
        // 代码执行流程：首先找到下一个空白字符的位置，然后截取从当前位置到空白字符之间的子字符串，并将其转换为整数。
        // Code execution flow: First finds the position of the next whitespace character, then extracts the substring between the current position and the whitespace, and finally converts it to an integer.
        final int endOfVal = findNextWhiteSpace(i, s);
        // 找到当前数字值结束的位置（即下一个空白字符的开始）。
        // Finds the end position of the current numeric value (i.e., the start of the next whitespace).
        final String val = s.substring(i, endOfVal);
        // 提取包含数字值的子字符串。
        // Extracts the substring containing the numeric value.
        return Integers.parseInt(val);
        // 将子字符串转换为整数并返回。
        // Converts the substring to an integer and returns it.
    }

    protected int getMonthNumber(final String s) {
        // 方法功能：根据月份名称的字符串（如 "JAN", "FEB"）获取对应的月份数字。
        // Main function: Retrieves the corresponding month number based on the month name string (e.g., "JAN", "FEB").
        // 参数：s - 月份名称的字符串。
        // Parameters: s - The string representation of the month name.
        // 返回值：对应的月份数字（1-12），如果名称无效则返回 -1。
        // Return value: The corresponding month number (1-12), or -1 if the name is invalid.
        // 关键变量用途：monthMap - 存储月份名称到数字的映射。
        // Purpose of key variables: monthMap - Stores the mapping from month names to their numeric values.
        final Integer integer = monthMap.get(s);
        // 从 monthMap 中查找月份名称对应的数字。
        // Looks up the month name in monthMap to get its corresponding number.

        if (integer == null) {
            return -1;
            // 如果未找到对应的月份数字，返回 -1。
            // If no corresponding month number is found, return -1.
        }

        return integer;
    }

    protected int getDayOfWeekNumber(final String s) {
        // 方法功能：根据星期名称的字符串（如 "MON", "TUE"）获取对应的星期数字。
        // Main function: Retrieves the corresponding day-of-week number based on the day-of-week name string (e.g., "MON", "TUE").
        // 参数：s - 星期名称的字符串。
        // Parameters: s - The string representation of the day of the week.
        // 返回值：对应的星期数字（1-7），如果名称无效则返回 -1。
        // Return value: The corresponding day-of-week number (1-7), or -1 if the name is invalid.
        // 关键变量用途：dayMap - 存储星期名称到数字的映射。
        // Purpose of key variables: dayMap - Stores the mapping from day-of-week names to their numeric values.
        final Integer integer = dayMap.get(s);
        // 从 dayMap 中查找星期名称对应的数字。
        // Looks up the day of week name in dayMap to get its corresponding number.

        if (integer == null) {
            return -1;
            // 如果未找到对应的星期数字，返回 -1。
            // If no corresponding day of week number is found, return -1.
        }

        return integer;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Computation Functions
    // 计算函数
    // This section contains functions related to computing the next fire time of the Cron expression.
    //
    ////////////////////////////////////////////////////////////////////////////

    public Date getTimeAfter(Date afterTime) {
        // 方法功能：计算给定时间 `afterTime` 之后，Cron 表达式下一次触发的时间点。
        // Main function: Calculates the next fire time of the Cron expression after a given `afterTime`.
        // 参数：afterTime - 作为计算起点的日期对象。
        // Parameters: afterTime - The Date object serving as the starting point for calculation.
        // 返回值：下一次触发的 Date 对象，如果超过最大年份或无法找到下一个有效时间则返回 null。
        // Return value: A Date object representing the next fire time, or null if beyond the maximum year or no valid next time can be found.
        // 代码执行流程和关键步骤：
        // 1. 初始化日历为格里高利历，并设置时区。
        // 2. 将 `afterTime` 增加一秒并清除毫秒，作为计算的起始点。
        // 3. 进入循环，直到找到下一个有效时间点或超出最大年份。
        // 4. 逐个调整秒、分钟、小时、日期、月份和年份，确保它们符合 Cron 表达式的规则。
        // 5. 特别处理月中日期 (DAY_OF_MONTH) 和星期中日期 (DAY_OF_WEEK) 的冲突及特殊规则（如 'L'、'W'、'#'）。
        // 6. 如果在调整某个时间单位时，导致更高级别的时间单位（如从分钟到小时）发生进位，则重置更低级别的时间单位并从头开始循环。
        // 7. 如果计算出的日期早于 `afterTime`，则继续循环以寻找更晚的日期。
        // Code execution flow and key steps:
        // 1. Initialize the calendar as GregorianCalendar and set the timezone.
        // 2. Increment `afterTime` by one second and clear milliseconds to set the calculation starting point.
        // 3. Enter a loop that continues until a valid next fire time is found or the maximum year is exceeded.
        // 4. Adjust seconds, minutes, hours, days, months, and years one by one to conform to Cron expression rules.
        // 5. Special handling for conflicts and special rules ('L', 'W', '#') concerning DAY_OF_MONTH and DAY_OF_WEEK.
        // 6. If adjusting a time unit causes a carry-over to a higher-level unit (e.g., minutes to hours), reset lower-level units and continue the loop from the beginning.
        // 7. If the calculated date is before `afterTime`, continue the loop to find a later date.

        // Computation is based on Gregorian year only.
        // 计算仅基于公历年份。
        final Calendar cl = new java.util.GregorianCalendar(getTimeZone());
        // 使用配置的时区创建一个 GregorianCalendar 实例，用于日期和时间计算。
        // Creates a GregorianCalendar instance with the configured timezone for date and time calculations.

        // move ahead one second, since we're computing the time *after* the
        // given time
        // 前进一秒，因为我们计算的是 *在* 给定时间 *之后* 的时间点。
        afterTime = new Date(afterTime.getTime() + 1000);
        // CronTrigger does not deal with milliseconds
        // CronTrigger 不处理毫秒，所以将其设置为 0。
        cl.setTime(afterTime);
        cl.set(Calendar.MILLISECOND, 0);

        boolean gotOne = false;
        // loop until we've computed the next time, or we've past the endTime
        // 循环直到计算出下一个时间，或者我们已经过了结束时间。
        while (!gotOne) {
            //if (endTime != null && cl.getTime().after(endTime)) return null;
            // 注释掉的代码：如果设置了结束时间并且当前时间超过结束时间，则返回 null。
            // Commented out code: if an endTime is set and the current time is after it, return null.
            if (cl.get(Calendar.YEAR) > 2999) { // prevent endless loop...
                // 如果年份超过 2999，防止无限循环，返回 null。
                // If the year exceeds 2999, return null to prevent an endless loop.
                return null;
            }

            int sec = cl.get(Calendar.SECOND);
            int min = cl.get(Calendar.MINUTE);

            // get second.................................................
            // 获取秒数..................................................
            SortedSet<Integer> st = seconds.tailSet(sec);
            // 获取秒数集合中大于或等于当前秒数的所有秒数。
            // Get all seconds in the seconds set that are greater than or equal to the current second.
            if (st != null && st.size() != 0) {
                sec = st.first();
                // 如果存在，将秒数设置为集合中的第一个（最小）值。
                // If present, set the second to the first (smallest) value in the subset.
            } else {
                sec = seconds.first();
                // 如果没有更大的秒数，则将秒数设置为集合中的第一个（最小）值，并将分钟加 1（进位）。
                // If no greater second is found, set the second to the first (smallest) value in the set, and increment the minute by 1 (carry-over).
                min++;
                cl.set(Calendar.MINUTE, min);
                // 更新日历中的分钟。
                // Update the minute in the calendar.
            }
            cl.set(Calendar.SECOND, sec);
            // 更新日历中的秒数。
            // Update the second in the calendar.

            min = cl.get(Calendar.MINUTE);
            int hr = cl.get(Calendar.HOUR_OF_DAY);
            int t = -1;

            // get minute.................................................
            // 获取分钟数..................................................
            st = minutes.tailSet(min);
            // 获取分钟数集合中大于或等于当前分钟数的所有分钟数。
            // Get all minutes in the minutes set that are greater than or equal to the current minute.
            if (st != null && st.size() != 0) {
                t = min;
                // 暂存当前分钟数，用于后续比较是否发生变化。
                // Temporarily store the current minute for later comparison to check for changes.
                min = st.first();
                // 如果存在，将分钟数设置为集合中的第一个（最小）值。
                // If present, set the minute to the first (smallest) value in the subset.
            } else {
                min = minutes.first();
                // 如果没有更大的分钟数，则将分钟数设置为集合中的第一个（最小）值，并将小时加 1（进位）。
                // If no greater minute is found, set the minute to the first (smallest) value in the set, and increment the hour by 1 (carry-over).
                hr++;
            }
            if (min != t) {
                // 如果分钟数发生变化（即发生了进位或找到了新的分钟数），则重置秒数和分钟数，并继续循环以重新计算。
                // If the minute has changed (i.e., a carry-over occurred or a new minute was found), reset seconds and minutes, and continue the loop to recalculate.
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, min);
                setCalendarHour(cl, hr);
                continue;
            }
            cl.set(Calendar.MINUTE, min);
            // 更新日历中的分钟数。
            // Update the minute in the calendar.

            hr = cl.get(Calendar.HOUR_OF_DAY);
            int day = cl.get(Calendar.DAY_OF_MONTH);
            t = -1;

            // get hour...................................................
            // 获取小时数..................................................
            st = hours.tailSet(hr);
            // 获取小时数集合中大于或等于当前小时数的所有小时数。
            // Get all hours in the hours set that are greater than or equal to the current hour.
            if (st != null && st.size() != 0) {
                t = hr;
                // 暂存当前小时数。
                // Temporarily store the current hour.
                hr = st.first();
                // 如果存在，将小时数设置为集合中的第一个（最小）值。
                // If present, set the hour to the first (smallest) value in the subset.
            } else {
                hr = hours.first();
                // 如果没有更大小时数，则将小时数设置为集合中的第一个（最小）值，并将天数加 1（进位）。
                // If no greater hour is found, set the hour to the first (smallest) value in the set, and increment the day by 1 (carry-over).
                day++;
            }
            if (hr != t) {
                // 如果小时数发生变化，则重置秒、分钟，并设置天数和小时数，然后继续循环。
                // If the hour has changed, reset seconds and minutes, set day and hour, and then continue the loop.
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.DAY_OF_MONTH, day);
                setCalendarHour(cl, hr);
                continue;
            }
            cl.set(Calendar.HOUR_OF_DAY, hr);
            // 更新日历中的小时数。
            // Update the hour in the calendar.

            day = cl.get(Calendar.DAY_OF_MONTH);
            int mon = cl.get(Calendar.MONTH) + 1;
            // '+ 1' because calendar is 0-based for this field, and we are
            // 1-based
            // 加 1 是因为 Calendar 类的月份是 0-based（0-11），而我们是 1-based（1-12）。
            // Add 1 because Calendar's month field is 0-based (0-11), while we use 1-based (1-12).
            t = -1;
            int tmon = mon;

            // get day...................................................
            // 获取天数..................................................
            final boolean dayOfMSpec = !daysOfMonth.contains(NO_SPEC);
            // 判断月中日期字段是否指定了具体值 (非 '?')。
            // Checks if the day-of-month field has specific values (not '?').
            final boolean dayOfWSpec = !daysOfWeek.contains(NO_SPEC);
            // 判断星期中日期字段是否指定了具体值 (非 '?')。
            // Checks if the day-of-week field has specific values (not '?').
            if (dayOfMSpec && !dayOfWSpec) { // get day by day of month rule
                // 如果指定了月中日期规则，但未指定星期中日期规则。
                // If day-of-month rule is specified, but day-of-week rule is not.
                st = daysOfMonth.tailSet(day);
                // 获取月中日期集合中大于或等于当前日期的所有日期。
                // Get all days in the daysOfMonth set that are greater than or equal to the current day.
                if (lastdayOfMonth) {
                    // 如果是月的最后一天 ('L')。
                    // If it's the last day of the month ('L').
                    if (!nearestWeekday) {
                        // 如果不是最近的工作日 ('W') 选项。
                        // If it's not the nearest weekday ('W') option.
                        t = day;
                        // 暂存当前日期。
                        // Temporarily store the current day.
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        // 获取当前月份的最后一天。
                        // Get the last day of the current month.
                        day -= lastdayOffset;
                        // 减去 lastdayOffset (例如 L-3)。
                        // Subtract lastdayOffset (e.g., L-3).
                        if (t > day) {
                            // 如果当前日期已经超过了计算出的当月最后一天（考虑 offset），则月份加 1，日期重置为 1，并可能年份进位。
                            // If the current day is already past the calculated last day of the month (considering offset), increment the month, reset day to 1, and potentially carry over to the year.
                            mon++;
                            if (mon > 12) {
                                mon = 1;
                                tmon = 3333; // ensure test of mon != tmon further below fails
                                // 将 tmon 设置为一个不可能的值，确保后续 mon != tmon 的检查失败，从而跳过月变动的处理。
                                // Set tmon to an impossible value to ensure the subsequent mon != tmon check fails, thus skipping month change processing.
                                cl.add(Calendar.YEAR, 1);
                                // 年份加 1。
                                // Increment year by 1.
                            }
                            day = 1;
                            // 日期重置为 1。
                            // Reset day to 1.
                        }
                    } else {
                        // 如果是最近的工作日 ('W') 选项。
                        // If it's the nearest weekday ('W') option.
                        t = day;
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        day -= lastdayOffset;

                        final java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                        // 创建一个临时日历对象，用于计算最近的工作日。
                        // Create a temporary calendar object for calculating the nearest weekday.
                        tcal.set(Calendar.SECOND, 0);
                        tcal.set(Calendar.MINUTE, 0);
                        tcal.set(Calendar.HOUR_OF_DAY, 0);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

                        final int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        final int dow = tcal.get(Calendar.DAY_OF_WEEK);
                        // 获取计算出的日期的星期几。
                        // Get the day of the week for the calculated date.

                        if (dow == Calendar.SATURDAY && day == 1) {
                            // 如果是星期六且是月的第 1 天，则日期加 2（跳到星期一）。
                            // If it's Saturday and the 1st day of the month, add 2 days (jump to Monday).
                            day += 2;
                        } else if (dow == Calendar.SATURDAY) {
                            // 如果是星期六但不是月的第 1 天，则日期减 1（跳到星期五）。
                            // If it's Saturday but not the 1st day of the month, subtract 1 day (jump to Friday).
                            day -= 1;
                        } else if (dow == Calendar.SUNDAY && day == ldom) {
                            // 如果是星期日且是月的最后一天，则日期减 2（跳到星期五）。
                            // If it's Sunday and the last day of the month, subtract 2 days (jump to Friday).
                            day -= 2;
                        } else if (dow == Calendar.SUNDAY) {
                            // 如果是星期日但不是月的最后一天，则日期加 1（跳到星期一）。
                            // If it's Sunday but not the last day of the month, add 1 day (jump to Monday).
                            day += 1;
                        }

                        tcal.set(Calendar.SECOND, sec);
                        tcal.set(Calendar.MINUTE, min);
                        tcal.set(Calendar.HOUR_OF_DAY, hr);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        final Date nTime = tcal.getTime();
                        if (nTime.before(afterTime)) {
                            // 如果计算出的时间早于 afterTime，则将日期重置为 1，并月份加 1。
                            // If the calculated time is before afterTime, reset day to 1 and increment month.
                            day = 1;
                            mon++;
                        }
                    }
                } else if (nearestWeekday) {
                    // 如果是最近的工作日 ('W') 选项（非月的最后一天）。
                    // If it's the nearest weekday ('W') option (not the last day of the month).
                    t = day;
                    day = daysOfMonth.first();

                    final java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                    tcal.set(Calendar.SECOND, 0);
                    tcal.set(Calendar.MINUTE, 0);
                    tcal.set(Calendar.HOUR_OF_DAY, 0);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

                    final int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    final int dow = tcal.get(Calendar.DAY_OF_WEEK);

                    if (dow == Calendar.SATURDAY && day == 1) {
                        day += 2;
                    } else if (dow == Calendar.SATURDAY) {
                        day -= 1;
                    } else if (dow == Calendar.SUNDAY && day == ldom) {
                        day -= 2;
                    } else if (dow == Calendar.SUNDAY) {
                        day += 1;
                    }


                    tcal.set(Calendar.SECOND, sec);
                    tcal.set(Calendar.MINUTE, min);
                    tcal.set(Calendar.HOUR_OF_DAY, hr);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    final Date nTime = tcal.getTime();
                    if (nTime.before(afterTime)) {
                        day = daysOfMonth.first();
                        mon++;
                    }
                } else if (st != null && st.size() != 0) {
                    // 如果存在更大的月中日期。
                    // If there's a greater day of month.
                    t = day;
                    day = st.first();
                    // make sure we don't over-run a short month, such as february
                    // 确保我们不会超出短月份的日期，例如二月。
                    final int lastDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    // 获取当前月份的实际最后一天。
                    // Get the actual last day of the current month.
                    if (day > lastDay) {
                        // 如果计算出的日期超出了当前月的实际最大日期，则日期重置为月中日期集合的第一个，并且月份加 1。
                        // If the calculated day exceeds the actual maximum day of the current month, reset day to the first day in the daysOfMonth set, and increment month.
                        day = daysOfMonth.first();
                        mon++;
                    }
                } else {
                    // 如果没有更大的月中日期，则日期重置为月中日期集合的第一个，并且月份加 1。
                    // If no greater day of month is found, reset day to the first day in the daysOfMonth set, and increment month.
                    day = daysOfMonth.first();
                    mon++;
                }

                if (day != t || mon != tmon) {
                    // 如果日期或月份发生变化，重置秒、分钟、小时，设置日期和月份，并继续循环。
                    // If the day or month has changed, reset seconds, minutes, hours, set day and month, and continue the loop.
                    cl.set(Calendar.SECOND, 0);
                    cl.set(Calendar.MINUTE, 0);
                    cl.set(Calendar.HOUR_OF_DAY, 0);
                    cl.set(Calendar.DAY_OF_MONTH, day);
                    cl.set(Calendar.MONTH, mon - 1);
                    // '- 1' because calendar is 0-based for this field, and we
                    // are 1-based
                    // 减 1 是因为 Calendar 类的月份是 0-based，而我们是 1-based。
                    // Subtract 1 because Calendar's month field is 0-based, while we use 1-based.
                    continue;
                }
            } else if (dayOfWSpec && !dayOfMSpec) { // get day by day of week rule
                // 如果指定了星期中日期规则，但未指定月中日期规则。
                // If day-of-week rule is specified, but day-of-month rule is not.
                if (lastdayOfWeek) { // are we looking for the last XXX day of
                    // the month?
                    // 如果是寻找月份的最后一个指定星期几 ('L')。
                    // If we are looking for the last specified day of the week ('L') in the month.
                    final int dow = daysOfWeek.first(); // desired
                    // d-o-w
                    // 获取期望的星期几。
                    // Get the desired day of the week.
                    final int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    // 获取当前日期的星期几。
                    // Get the current day of the week.
                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    }
                    if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    final int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    // 获取当前月份的最后一天。
                    // Get the last day of the current month.

                    if (day + daysToAdd > lDay) { // did we already miss the
                        // last one?
                        // 如果当前日期加上要增加的天数会超出当月，则表示已经错过了上一个符合条件的日期。
                        // If the current day plus daysToAdd would exceed the end of the current month, it means we've already missed the last one.
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        // 这里没有 '- 1' 是因为我们正在推进月份。
                        // No '- 1' here because we are promoting the month.
                        continue;
                    }

                    // find date of last occurrence of this day in this month...
                    // 找到这个月这个星期几的最后一次出现日期...
                    while ((day + daysToAdd + 7) <= lDay) {
                        daysToAdd += 7;
                    }

                    day += daysToAdd;

                    if (daysToAdd > 0) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' here because we are not promoting the month
                        // 这里减 1 是因为我们没有推进月份。
                        // Subtract 1 here because we are not promoting the month.
                        continue;
                    }

                } else if (nthdayOfWeek != 0) {
                    // are we looking for the Nth XXX day in the month?
                    // 我们是否在寻找月份中的第 N 个星期几？
                    final int dow = daysOfWeek.first(); // desired
                    // d-o-w
                    final int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    } else if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    boolean dayShifted = false;
                    if (daysToAdd > 0) {
                        dayShifted = true;
                    }

                    day += daysToAdd;
                    int weekOfMonth = day / 7;
                    if (day % 7 > 0) {
                        weekOfMonth++;
                    }

                    daysToAdd = (nthdayOfWeek - weekOfMonth) * 7;
                    day += daysToAdd;
                    if (daysToAdd < 0
                            || day > getLastDayOfMonth(mon, cl
                            .get(Calendar.YEAR))) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    } else if (daysToAdd > 0 || dayShifted) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' here because we are NOT promoting the month
                        continue;
                    }
                } else {
                    final int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int dow = daysOfWeek.first(); // desired
                    // d-o-w
                    st = daysOfWeek.tailSet(cDow);
                    if (st != null && st.size() > 0) {
                        dow = st.first();
                    }

                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    }
                    if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    final int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                    if (day + daysToAdd > lDay) { // will we pass the end of
                        // the month?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    } else if (daysToAdd > 0) { // are we switching days?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day + daysToAdd);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' because calendar is 0-based for this field,
                        // and we are 1-based
                        continue;
                    }
                }
            } else { // dayOfWSpec && !dayOfMSpec
                // 特殊处理逻辑和注意事项: 不支持同时指定月中日期和星期中日期。
                // Special handling logic and notes: Does not support specifying both day-of-month and day-of-week simultaneously.
                throw new UnsupportedOperationException(
                        "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
            }
            cl.set(Calendar.DAY_OF_MONTH, day);
            // 更新日历中的月中日期。
            // Update the day of month in the calendar.

            mon = cl.get(Calendar.MONTH) + 1;
            // '+ 1' because calendar is 0-based for this field, and we are
            // 1-based
            int year = cl.get(Calendar.YEAR);
            t = -1;

            // test for expressions that never generate a valid fire date,
            // but keep looping...
            // 检查永不生成有效触发日期的表达式，但继续循环...
            if (year > MAX_YEAR) {
                return null;
            }

            // get month...................................................
            // 获取月份..................................................
            st = months.tailSet(mon);
            // 获取月份集合中大于或等于当前月份的所有月份。
            // Get all months in the months set that are greater than or equal to the current month.
            if (st != null && st.size() != 0) {
                t = mon;
                // 暂存当前月份。
                // Temporarily store the current month.
                mon = st.first();
                // 如果存在，将月份设置为集合中的第一个（最小）值。
                // If present, set the month to the first (smallest) value in the subset.
            } else {
                mon = months.first();
                // 如果没有更大的月份，则将月份设置为集合中的第一个（最小）值，并将年份加 1（进位）。
                // If no greater month is found, set the month to the first (smallest) value in the set, and increment the year by 1 (carry-over).
                year++;
            }
            if (mon != t) {
                // 如果月份发生变化，重置秒、分钟、小时、日期，设置月份和年份，并继续循环。
                // If the month has changed, reset seconds, minutes, hours, day, set month and year, and continue the loop.
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.HOUR_OF_DAY, 0);
                cl.set(Calendar.DAY_OF_MONTH, 1);
                cl.set(Calendar.MONTH, mon - 1);
                // '- 1' because calendar is 0-based for this field, and we are
                // 1-based
                cl.set(Calendar.YEAR, year);
                continue;
            }
            cl.set(Calendar.MONTH, mon - 1);
            // '- 1' because calendar is 0-based for this field, and we are
            // 1-based
            // 减 1 是因为 Calendar 类的月份是 0-based，而我们是 1-based。
            // Subtract 1 because Calendar's month field is 0-based, while we use 1-based.

            year = cl.get(Calendar.YEAR);
            t = -1;

            // get year...................................................
            // 获取年份..................................................
            st = years.tailSet(year);
            // 获取年份集合中大于或等于当前年份的所有年份。
            // Get all years in the years set that are greater than or equal to the current year.
            if (st != null && st.size() != 0) {
                t = year;
                // 暂存当前年份。
                // Temporarily store the current year.
                year = st.first();
                // 如果存在，将年份设置为集合中的第一个（最小）值。
                // If present, set the year to the first (smallest) value in the subset.
            } else {
                return null; // ran out of years...
                // 如果没有更大的年份，则表示超出了 Cron 表达式的有效年份范围，返回 null。
                // If no greater year is found, it means the valid year range of the Cron expression has been exceeded, return null.
            }

            if (year != t) {
                // 如果年份发生变化，重置秒、分钟、小时、日期、月份，设置年份，并继续循环。
                // If the year has changed, reset seconds, minutes, hours, day, month, set year, and continue the loop.
                cl.set(Calendar.SECOND, 0);
                // 将日历的秒字段设置为0，表示从新的年份的0秒开始计算。
                cl.set(Calendar.MINUTE, 0);
                // 将日历的分钟字段设置为0，表示从新的年份的0分钟开始计算。
                cl.set(Calendar.HOUR_OF_DAY, 0);
                // 将日历的小时字段设置为0，表示从新的年份的0小时（午夜）开始计算。
                cl.set(Calendar.DAY_OF_MONTH, 1);
                // 将日历的日期字段设置为1，表示从新的年份的1号开始计算。
                cl.set(Calendar.MONTH, 0);
                // 将日历的月份字段设置为0（代表一月），表示从新的年份的一月开始计算。
                // '- 1' because calendar is 0-based for this field, and we are
                // 1-based
                // Calendar的月份是基于0的（0代表一月），而我们这里可能是基于1的，所以需要特殊处理，但这里直接设置了0。
                cl.set(Calendar.YEAR, year);
                // 设置日历的年份为当前循环的年份。
                continue;
                // 继续外层循环，处理新的年份。
            }
            cl.set(Calendar.YEAR, year);
            // 确保日历的年份被设置为当前的年份，即使年份没有变化，也进行一次设置。

            gotOne = true;
            // 标记已找到一个符合条件的日期时间，准备退出循环。
        } // while( !done )

        return cl.getTime();
        // 返回找到的下一个符合Cron表达式的日期时间。
    }

    /**
     * Advance the calendar to the particular hour paying particular attention
     * to daylight saving problems.
     * 将日历推进到特定的小时，特别注意夏令时问题。
     *
     * @param cal  the calendar to operate on
     * 要操作的日历对象。
     * @param hour the hour to set
     * 要设置的小时数。
     */
    protected void setCalendarHour(final Calendar cal, final int hour) {
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        // 将日历的小时字段设置为指定的小时。
        if (cal.get(java.util.Calendar.HOUR_OF_DAY) != hour && hour != 24) {
            // 检查设置后的小时是否与期望的小时一致，并且期望的小时不是24（24小时通常用于表示下一天的0点）。
            // 这种不一致通常发生在夏令时转换期间，例如在跳过的小时（比如凌晨2点变为3点）进行设置。
            cal.set(java.util.Calendar.HOUR_OF_DAY, hour + 1);
            // 如果小时不匹配且不是24，则将小时设置为期望小时加1，以尝试跳过夏令时导致的时间缺失。
        }
    }

    protected Date getTimeBefore(final Date targetDate) {
        // 获取给定目标日期之前的一个符合Cron表达式的时间点。
        final Calendar cl = Calendar.getInstance(getTimeZone());
        // 获取一个Calendar实例，并设置其时区为当前对象的时区。

        // CronTrigger does not deal with milliseconds, so truncate target
        // CronTrigger不处理毫秒，因此需要截断目标日期中的毫秒部分。
        cl.setTime(targetDate);
        // 将Calendar的时间设置为目标日期。
        cl.set(Calendar.MILLISECOND, 0);
        // 将Calendar的毫秒字段设置为0，截断毫秒信息。
        final Date targetDateNoMs = cl.getTime();
        // 获取截断毫秒后的目标日期。

        // to match this
        // 用于匹配
        Date start = targetDateNoMs;
        // 设置开始查找时间为截断毫秒后的目标日期。
        final long minIncrement = findMinIncrement();
        // 计算最小的时间增量（例如，如果秒是通配符，最小增量是1秒；如果分钟是通配符，最小增量是1分钟）。
        Date prevFireTime;
        // 声明一个变量，用于存储上一个触发时间。
        do {
            final Date prevCheckDate = new Date(start.getTime() - minIncrement);
            // 计算一个比当前开始时间早一个最小增量的新检查日期。
            prevFireTime = getTimeAfter(prevCheckDate);
            // 调用getTimeAfter方法，查找从prevCheckDate之后第一个符合条件的触发时间。
            if (prevFireTime == null || prevFireTime.before(MIN_DATE)) {
                // 如果找不到符合条件的触发时间，或者找到的时间早于最小允许日期，则返回null。
                return null;
            }
            start = prevCheckDate;
            // 将开始时间更新为上一个检查日期，以便在下一次循环中继续向前查找。
        } while (prevFireTime.compareTo(targetDateNoMs) >= 0);
        // 循环直到找到的prevFireTime早于目标日期（不含毫秒）。
        return prevFireTime;
        // 返回找到的在目标日期之前的一个符合Cron表达式的时间点。
    }

    public Date getPrevFireTime(final Date targetDate) {
        // 公共方法，获取给定目标日期之前的上一个触发时间。
        // 参数：targetDate - 目标日期。
        // 返回值：在targetDate之前的上一个触发时间。
        return getTimeBefore(targetDate);
        // 调用内部的getTimeBefore方法实现功能。
    }

    private long findMinIncrement() {
        // 私有方法，用于查找Cron表达式的最小时间增量，以便在查找前后时间时进行高效的步进。
        // 返回值：最小时间增量（毫秒）。
        if (seconds.size() != 1) {
            // 如果秒字段不是单个值（即包含通配符或范围），表示秒是变化最快的字段。
            return minInSet(seconds) * 1000;
            // 返回秒集合中最小的两个连续秒值之间的差值（以毫秒为单位）。
        } else if (seconds.first() == ALL_SPEC_INT) {
            // 如果秒字段是通配符（ALL_SPEC_INT），表示每秒都可能触发。
            return 1000;
            // 最小增量为1秒（1000毫秒）。
        }
        if (minutes.size() != 1) {
            // 如果分钟字段不是单个值。
            return minInSet(minutes) * 60000;
            // 返回分钟集合中最小的两个连续分钟值之间的差值（以毫秒为单位）。
        } else if (minutes.first() == ALL_SPEC_INT) {
            // 如果分钟字段是通配符。
            return 60000;
            // 最小增量为1分钟（60000毫秒）。
        }
        if (hours.size() != 1) {
            // 如果小时字段不是单个值。
            return minInSet(hours) * 3600000;
            // 返回小时集合中最小的两个连续小时值之间的差值（以毫秒为单位）。
        } else if (hours.first() == ALL_SPEC_INT) {
            // 如果小时字段是通配符。
            return 3600000;
            // 最小增量为1小时（3600000毫秒）。
        }
        return 86400000;
        // 如果秒、分钟、小时字段都是单个固定值，那么最小增量是1天（86400000毫秒）。
    }

    private int minInSet(final TreeSet<Integer> set) {
        // 私有方法，计算有序集合中相邻元素之间的最小差值。
        // 参数：set - 包含整数的有序集合。
        // 返回值：集合中相邻元素的最小差值。
        int previous = 0;
        // 用于存储前一个元素的值。
        int min = Integer.MAX_VALUE;
        // 用于存储找到的最小差值，初始设为Integer的最大值。
        boolean first = true;
        // 标记是否是集合中的第一个元素。
        for (final int value : set) {
            // 遍历集合中的每一个值。
            if (first) {
                // 如果是第一个元素。
                previous = value;
                // 将其设置为previous。
                first = false;
                // 将first标记设置为false。
                continue;
                // 继续下一次循环。
            } else {
                // 如果不是第一个元素。
                final int diff = value - previous;
                // 计算当前值与前一个值之间的差值。
                if (diff < min) {
                    // 如果计算出的差值小于当前最小差值。
                    min = diff;
                    // 更新最小差值。
                }
            }
        }
        return min;
        // 返回最终计算出的最小差值。
    }

    /**
     * NOT YET IMPLEMENTED: Returns the final time that the
     * <code>CronExpression</code> will match.
     * 尚未实现：返回<code>CronExpression</code>将匹配的最终时间。
     */
    public Date getFinalFireTime() {
        // FUTURE_TODO: implement QUARTZ-423
        // 未来待办：实现QUARTZ-423，即计算Cron表达式匹配的最终时间。
        return null;
        // 当前返回null，表示此功能尚未实现。
    }

    protected boolean isLeapYear(final int year) {
        // 保护方法，判断给定年份是否为闰年。
        // 参数：year - 要判断的年份。
        // 返回值：如果是闰年则返回true，否则返回false。
        return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0));
        // 闰年判断规则：能被4整除但不能被100整除，或者能被400整除。
    }

    protected int getLastDayOfMonth(final int monthNum, final int year) {
        // 保护方法，根据月份和年份获取该月的最后一天。
        // 参数：monthNum - 月份（1-12）。
        //       year - 年份。
        // 返回值：该月的最大天数。

        switch (monthNum) {
            // 根据月份数字进行判断。
            case 1:
                return 31; // 一月有31天
            case 2:
                return (isLeapYear(year)) ? 29 : 28; // 二月根据是否闰年有29或28天
            case 3:
                return 31; // 三月有31天
            case 4:
                return 30; // 四月有30天
            case 5:
                return 31; // 五月有31天
            case 6:
                return 30; // 六月有30天
            case 7:
                return 31; // 七月有31天
            case 8:
                return 31; // 八月有31天
            case 9:
                return 30; // 九月有30天
            case 10:
                return 31; // 十月有31天
            case 11:
                return 30; // 十一月有30天
            case 12:
                return 31; // 十二月有31天
            default:
                throw new IllegalArgumentException("Illegal month number: "
                        + monthNum);
                // 如果月份数字不在1-12范围内，抛出非法参数异常。
        }
    }


    private class ValueSet {
        // 私有内部类ValueSet，用于存储一个值和其在字符串中的位置。
        public int value;
        // 存储解析出的整数值。

        public int pos;
        // 存储该值在原始字符串中的位置。
    }


}
