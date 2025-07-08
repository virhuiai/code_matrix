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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.util.Integers;

/**
 * <p>FastDateParser is a fast and thread-safe version of
 * {@link java.text.SimpleDateFormat}.</p>
 * <p>FastDateParser 是 {@link java.text.SimpleDateFormat} 的一个快速且线程安全的版本。</p>
 *
 * <p>To obtain a proxy to a FastDateParser, use {@link FastDateFormat#getInstance(String, TimeZone, Locale)}
 * or another variation of the factory methods of {@link FastDateFormat}.</p>
 * <p>要获取 FastDateParser 的代理，请使用 {@link FastDateFormat#getInstance(String, TimeZone, Locale)} 或 {@link FastDateFormat} 的其他工厂方法。</p>
 *
 * <p>Since FastDateParser is thread safe, you can use a static member instance:</p>
 * <p>由于 FastDateParser 是线程安全的，您可以将其作为静态成员实例使用：</p>
 * <code>
 *     private static final DateParser DATE_PARSER = FastDateFormat.getInstance("yyyy-MM-dd");
 * </code>
 *
 * <p>This class can be used as a direct replacement for
 * <code>SimpleDateFormat</code> in most parsing situations.
 * This class is especially useful in multi-threaded server environments.
 * <code>SimpleDateFormat</code> is not thread-safe in any JDK version,
 * nor will it be as Sun has closed the
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4228335">bug</a>/RFE.
 * </p>
 * <p>在大多数解析场景中，这个类可以作为 <code>SimpleDateFormat</code> 的直接替代品。
 * 这个类在多线程服务器环境中特别有用。
 * <code>SimpleDateFormat</code> 在任何 JDK 版本中都不是线程安全的，
 * 也不会是，因为 Sun 已经关闭了相关的 <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4228335">bug</a>/RFE。</p>
 *
 * <p>Only parsing is supported by this class, but all patterns are compatible with
 * SimpleDateFormat.</p>
 * <p>此类的主要功能是解析日期字符串，但所有模式都与 SimpleDateFormat 兼容。</p>
 *
 * <p>The class operates in lenient mode, so for example a time of 90 minutes is treated as 1 hour 30 minutes.</p>
 * <p>该类以宽松模式运行，例如，90 分钟的时间将被视为 1 小时 30 分钟。</p>
 *
 * <p>Timing tests indicate this class is as about as fast as SimpleDateFormat
 * in single thread applications and about 25% faster in multi-thread applications.</p>
 * <p>计时测试表明，该类在单线程应用程序中与 SimpleDateFormat 的速度大致相同，在多线程应用程序中快约 25%。</p>
 *
 * <p>
 * Copied and modified from <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a>.
 * </p>
 * <p>
 * 从 <a href="https://commons.apache.org/proper/commons-lang/">Apache Commons Lang</a> 复制并修改。
 * </p>
 *
 * @since Apache Commons Lang 3.2
 * @see FastDatePrinter
 */
public class FastDateParser implements DateParser, Serializable {

    /**
     * Required for serialization support.
     * 序列化支持所必需。
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 3L;

    /**
     * 日本帝国历的 Locale，用于处理特殊年份。
     */
    static final Locale JAPANESE_IMPERIAL = new Locale("ja","JP","JP");

    // defining fields
    // 定义字段
    private final String pattern; // 日期格式模式字符串
    private final TimeZone timeZone; // 时区
    private final Locale locale; // 区域设置
    private final int century; // 两位数年份解析的世纪（例如：2000）
    private final int startYear; // 两位数年份解析的起始年份（例如：20）

    // derived fields
    // 派生字段
    private transient List<StrategyAndWidth> patterns; // 解析策略和宽度列表

    // comparator used to sort regex alternatives
    // 用于对正则表达式候选项进行排序的比较器
    // alternatives should be ordered longer first, and shorter last. ('february' before 'feb')
    // 候选项应按长度从长到短排序（例如 'february' 在 'feb' 之前）
    // all entries must be lowercase by locale.
    // 所有条目都必须根据区域设置转换为小写。
    private static final Comparator<String> LONGER_FIRST_LOWERCASE = (left, right) -> right.compareTo(left);

    /**
     * <p>Constructs a new FastDateParser.</p>
     * <p>构造一个新的 FastDateParser 实例。</p>
     *
     * Use {@link FastDateFormat#getInstance(String, TimeZone, Locale)} or another variation of the
     * factory methods of {@link FastDateFormat} to get a cached FastDateParser instance.
     * 使用 {@link FastDateFormat#getInstance(String, TimeZone, Locale)} 或 {@link FastDateFormat} 的其他工厂方法来获取缓存的 FastDateParser 实例。
     *
     * @param pattern non-null {@link java.text.SimpleDateFormat} compatible
     *  pattern
     * 非空的 {@link java.text.SimpleDateFormat} 兼容模式字符串
     * @param timeZone non-null time zone to use
     * 非空的时区
     * @param locale non-null locale
     * 非空的区域设置
     */
    protected FastDateParser(final String pattern, final TimeZone timeZone, final Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    /**
     * <p>Constructs a new FastDateParser.</p>
     * <p>构造一个新的 FastDateParser 实例。</p>
     *
     * @param pattern non-null {@link java.text.SimpleDateFormat} compatible
     *  pattern
     * 非空的 {@link java.text.SimpleDateFormat} 兼容模式字符串
     * @param timeZone non-null time zone to use
     * 非空的时区
     * @param locale non-null locale
     * 非空的区域设置
     * @param centuryStart The start of the century for 2 digit year parsing
     * 用于两位数年份解析的世纪起始日期
     * @since 3.5
     */
    protected FastDateParser(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = locale;

        final Calendar definingCalendar = Calendar.getInstance(timeZone, locale); // 获取指定时区和区域设置的日历实例

        int centuryStartYear; // 世纪起始年份
        if(centuryStart!=null) {
            definingCalendar.setTime(centuryStart); // 设置日历时间为世纪起始日期
            centuryStartYear= definingCalendar.get(Calendar.YEAR); // 获取世纪起始年份
        }
        else if(locale.equals(JAPANESE_IMPERIAL)) { // 如果是日本帝国历
            centuryStartYear= 0; // 世纪起始年份设为0
        }
        else {
            // from 80 years ago to 20 years from now
            // 从当前时间回溯80年到未来20年
            definingCalendar.setTime(new Date()); // 设置日历时间为当前日期
            centuryStartYear= definingCalendar.get(Calendar.YEAR)-80; // 世纪起始年份设为当前年份减80
        }
        century= centuryStartYear / 100 * 100; // 计算世纪值（例如，1980年则世纪为1900）
        startYear= centuryStartYear - century; // 计算世纪内的起始年份（例如，1980年则起始年份为80）

        init(definingCalendar); // 初始化解析器
    }

    /**
     * Initialize derived fields from defining fields.
     * 从定义字段初始化派生字段。
     * This is called from constructor and from readObject (de-serialization)
     * 这个方法在构造函数和 readObject（反序列化）中调用。
     * @param definingCalendar the {@link java.util.Calendar} instance used to initialize this FastDateParser
     * 用于初始化此 FastDateParser 的 {@link java.util.Calendar} 实例
     */
    private void init(final Calendar definingCalendar) {
        patterns = new ArrayList<>(); // 初始化模式列表

        final StrategyParser fm = new StrategyParser(definingCalendar); // 创建策略解析器
        for(;;) { // 无限循环，直到没有更多策略
            final StrategyAndWidth field = fm.getNextStrategy(); // 获取下一个策略和宽度
            if(field==null) { // 如果没有更多策略，则退出循环
                break;
            }
            patterns.add(field); // 将策略添加到列表中
        }
    }

    // helper classes to parse the format string
    // 帮助类，用于解析格式字符串
    //-----------------------------------------------------------------------

    /**
     * Holds strategy and field width
     * 包含策略和字段宽度
     */
    private static class StrategyAndWidth {
        final Strategy strategy; // 解析策略
        final int width; // 字段宽度

        StrategyAndWidth(final Strategy strategy, final int width) {
            this.strategy = strategy;
            this.width = width;
        }

        /**
         * 获取最大宽度。如果当前策略是数字类型且后面还有策略，则返回下一个数字策略的宽度；否则返回0。
         * @param lt 策略迭代器
         * @return 最大宽度
         */
        int getMaxWidth(final ListIterator<StrategyAndWidth> lt) {
            if(!strategy.isNumber() || !lt.hasNext()) { // 如果不是数字策略或没有下一个策略
                return 0; // 返回0
            }
            final Strategy nextStrategy = lt.next().strategy; // 获取下一个策略
            lt.previous(); // 将迭代器回退一位
            return nextStrategy.isNumber() ?width :0; // 如果下一个策略是数字策略，返回当前宽度，否则返回0
       }
    }

    /**
     * Parse format into Strategies
     * 将格式解析为策略
     */
    private class StrategyParser {
        final private Calendar definingCalendar; // 定义日历
        private int currentIdx; // 当前解析索引

        StrategyParser(final Calendar definingCalendar) {
            this.definingCalendar = definingCalendar;
        }

        /**
         * 获取下一个策略和宽度。
         * @return StrategyAndWidth 对象，如果没有更多模式则返回 null。
         */
        StrategyAndWidth getNextStrategy() {
            if (currentIdx >= pattern.length()) { // 如果当前索引超出模式长度
                return null; // 返回 null
            }

            final char c = pattern.charAt(currentIdx); // 获取当前字符
            if (isFormatLetter(c)) { // 如果是格式字母
                return letterPattern(c); // 解析字母模式
            }
            return literal(); // 解析字面量
        }

        /**
         * 解析日期格式模式中的字母部分（例如 'yyyy', 'MM'）。
         * @param c 当前字符
         * @return StrategyAndWidth 对象
         */
        private StrategyAndWidth letterPattern(final char c) {
            final int begin = currentIdx; // 记录起始索引
            while (++currentIdx < pattern.length()) { // 遍历模式字符串直到字符不同
                if (pattern.charAt(currentIdx) != c) {
                    break;
                }
            }

            final int width = currentIdx - begin; // 计算宽度
            return new StrategyAndWidth(getStrategy(c, width, definingCalendar), width); // 获取对应的策略并创建 StrategyAndWidth
        }

        /**
         * 解析日期格式模式中的字面量部分（非字母和引号）。
         * @return StrategyAndWidth 对象
         * @throws IllegalArgumentException 如果引号未闭合
         */
        private StrategyAndWidth literal() {
            boolean activeQuote = false; // 标记是否处于引用模式

            final StringBuilder sb = new StringBuilder(); // 用于构建字面量字符串
            while (currentIdx < pattern.length()) { // 遍历模式字符串
                final char c = pattern.charAt(currentIdx);
                if (!activeQuote && isFormatLetter(c)) { // 如果不在引用模式且是格式字母
                    break; // 停止解析字面量
                } else if (c == '\'' && (++currentIdx == pattern.length() || pattern.charAt(currentIdx) != '\'')) { // 如果是单引号且不是两个单引号
                    activeQuote = !activeQuote; // 切换引用模式
                    continue; // 继续下一个字符
                }
                ++currentIdx; // 索引递增
                sb.append(c); // 将字符添加到字面量字符串
            }

            if (activeQuote) { // 如果引用模式未关闭
                throw new IllegalArgumentException("Unterminated quote"); // 抛出未终止引号异常
            }

            final String formatField = sb.toString(); // 获取字面量字符串
            return new StrategyAndWidth(new CopyQuotedStrategy(formatField), formatField.length()); // 创建 CopyQuotedStrategy
        }
    }

    /**
     * 判断字符是否是日期格式模式中的字母。
     * @param c 要检查的字符
     * @return 如果是字母则返回 true，否则返回 false。
     */
    private static boolean isFormatLetter(final char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

    // Accessors
    // 访问器
    //-----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DateParser#getPattern()
     */
    @Override
    public String getPattern() {
        return pattern; // 返回日期格式模式
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DateParser#getTimeZone()
     */
    @Override
    public TimeZone getTimeZone() {
        return timeZone; // 返回时区
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DateParser#getLocale()
     */
    @Override
    public Locale getLocale() {
        return locale; // 返回区域设置
    }


    // Basics
    // 基本方法
    //-----------------------------------------------------------------------
    /**
     * <p>Compare another object for equality with this object.</p>
     * <p>将另一个对象与此对象进行相等性比较。</p>
     * @param obj  the object to compare to
     * 要比较的对象
     * @return <code>true</code>if equal to this instance
     * 如果与此实例相等则返回 <code>true</code>
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FastDateParser)) { // 如果不是 FastDateParser 实例
            return false; // 返回 false
        }
        final FastDateParser other = (FastDateParser) obj; // 类型转换
        return pattern.equals(other.pattern) // 比较模式字符串
            && timeZone.equals(other.timeZone) // 比较时区
            && locale.equals(other.locale); // 比较区域设置
    }

    /**
     * <p>Return a hash code compatible with equals.</p>
     * <p>返回一个与 equals 方法兼容的哈希码。</p>
     * @return a hash code compatible with equals
     * 与 equals 方法兼容的哈希码
     */
    @Override
    public int hashCode() {
        return pattern.hashCode() + 13 * (timeZone.hashCode() + 13 * locale.hashCode()); // 计算哈希码
    }

    /**
     * <p>Get a string version of this formatter.</p>
     * <p>获取此格式化程序的字符串表示形式。</p>
     * @return a debugging string
     * 一个用于调试的字符串
     */
    @Override
    public String toString() {
        return "FastDateParser[" + pattern + "," + locale + "," + timeZone.getID() + "]"; // 返回格式化程序的字符串表示
    }

    // Serializing
    // 序列化
    //-----------------------------------------------------------------------
    /**
     * Create the object after serialization. This implementation reinitializes the
     * transient properties.
     * 在反序列化后创建对象。此实现会重新初始化瞬态属性。
     * @param in ObjectInputStream from which the object is being deserialized.
     * 用于反序列化对象的 ObjectInputStream。
     * @throws IOException if there is an IO issue.
     * 如果发生 IO 问题。
     * @throws ClassNotFoundException if a class cannot be found.
     * 如果找不到类。
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // 调用默认的反序列化方法

        final Calendar definingCalendar = Calendar.getInstance(timeZone, locale); // 获取指定时区和区域设置的日历实例
        init(definingCalendar); // 重新初始化
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DateParser#parseObject(java.lang.String)
     */
    @Override
    public Object parseObject(final String source) throws ParseException {
        return parse(source); // 解析字符串并返回日期对象
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DateParser#parse(java.lang.String)
     */
    @Override
    public Date parse(final String source) throws ParseException {
        final ParsePosition pp = new ParsePosition(0); // 创建解析位置对象，从索引0开始
        final Date date= parse(source, pp); // 解析字符串
        if (date == null) { // 如果解析失败
            // Add a note re supported date range
            // 添加关于支持日期范围的说明
            if (locale.equals(JAPANESE_IMPERIAL)) { // 如果是日本帝国历
                throw new ParseException(
                        "(The " +locale + " locale does not support dates before 1868 AD)\n" +
                                "Unparseable date: \""+source, pp.getErrorIndex()); // 抛出解析异常，并说明不支持1868年之前的日期
            }
            throw new ParseException("Unparseable date: "+source, pp.getErrorIndex()); // 抛出解析异常
        }
        return date; // 返回解析后的日期
    }

    /* (non-Javadoc)
     * @see org.apache.commons.lang3.time.DateParser#parseObject(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        return parse(source, pos); // 解析字符串并返回日期对象，更新 ParsePosition
    }

    /**
     * This implementation updates the ParsePosition if the parse succeeds.
     * 此实现在解析成功时更新 ParsePosition。
     * However, it sets the error index to the position before the failed field unlike
     * the method {@link java.text.SimpleDateFormat#parse(String, ParsePosition)} which sets
     * the error index to after the failed field.
     * 然而，它将错误索引设置为失败字段之前的位置，这与 {@link java.text.SimpleDateFormat#parse(String, ParsePosition)} 方法不同，后者将错误索引设置为失败字段之后。
     * <p>
     * To determine if the parse has succeeded, the caller must check if the current parse position
     * given by {@link ParsePosition#getIndex()} has been updated. If the input buffer has been fully
     * parsed, then the index will point to just after the end of the input buffer.
     * 为了确定解析是否成功，调用者必须检查 {@link ParsePosition#getIndex()} 给出的当前解析位置是否已更新。如果输入缓冲区已完全解析，则索引将指向输入缓冲区末尾之后。
     * @see org.apache.commons.lang3.time.DateParser#parse(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Date parse(final String source, final ParsePosition pos) {
        // timing tests indicate getting new instance is 19% faster than cloning
        // 计时测试表明，获取新实例比克隆快 19%
        final Calendar cal= Calendar.getInstance(timeZone, locale); // 获取指定时区和区域设置的日历实例
        cal.clear(); // 清空日历

        return parse(source, pos, cal) ? cal.getTime() : null; // 解析并返回日期，如果解析失败则返回 null
    }

    /**
     * Parse a formatted date string according to the format.  Updates the Calendar with parsed fields.
     * 根据格式解析格式化的日期字符串。使用解析后的字段更新 Calendar。
     * Upon success, the ParsePosition index is updated to indicate how much of the source text was consumed.
     * 成功时，ParsePosition 索引会更新，指示已消耗的源文本量。
     * Not all source text needs to be consumed.  Upon parse failure, ParsePosition error index is updated to
     * the offset of the source text which does not match the supplied format.
     * 不需要消耗所有源文本。解析失败时，ParsePosition 错误索引会更新为与提供的格式不匹配的源文本的偏移量。
     * @param source The text to parse.
     * 要解析的文本。
     * @param pos On input, the position in the source to start parsing, on output, updated position.
     * 输入时，源中开始解析的位置；输出时，更新后的位置。
     * @param calendar The calendar into which to set parsed fields.
     * 要设置解析字段的日历。
     * @return true, if source has been parsed (pos parsePosition is updated); otherwise false (and pos errorIndex is updated)
     * 如果源已解析（pos 的解析位置已更新），则返回 true；否则返回 false（并且 pos 的错误索引已更新）。
     * @throws IllegalArgumentException when Calendar has been set to be not lenient, and a parsed field is
     * out of range.
     * 当 Calendar 设置为不宽松模式且解析的字段超出范围时抛出。
     */
    @Override
    public boolean parse(final String source, final ParsePosition pos, final Calendar calendar) {
        final ListIterator<StrategyAndWidth> lt = patterns.listIterator(); // 获取解析策略的列表迭代器
        while (lt.hasNext()) { // 遍历所有策略
            final StrategyAndWidth strategyAndWidth = lt.next(); // 获取当前策略和宽度
            final int maxWidth = strategyAndWidth.getMaxWidth(lt); // 获取最大宽度
            if (!strategyAndWidth.strategy.parse(this, calendar, source, pos, maxWidth)) { // 如果当前策略解析失败
                return false; // 返回 false
            }
        }
        return true; // 所有策略都解析成功，返回 true
    }

    // Support for strategies
    // 策略支持
    //-----------------------------------------------------------------------

    /**
     * 将给定的值进行简单引用，并追加到 StringBuilder 中。
     * @param sb 要追加的 StringBuilder
     * @param value 要引用的值
     * @return 包含引用值的 StringBuilder
     */
    private static StringBuilder simpleQuote(final StringBuilder sb, final String value) {
        for (int i = 0; i < value.length(); ++i) { // 遍历值中的每个字符
            final char c = value.charAt(i);
            switch (c) { // 对特殊字符进行转义
            case '\\':
            case '^':
            case '$':
            case '.':
            case '|':
            case '?':
            case '*':
            case '+':
            case '(':
            case ')':
            case '[':
            case '{':
                sb.append('\\'); // 添加转义符
            default:
                sb.append(c); // 添加字符
            }
        }
        return sb; // 返回 StringBuilder
    }

    /**
     * Get the short and long values displayed for a field
     * 获取字段的短名称和长名称显示值。
     * @param cal The calendar to obtain the short and long values
     * 用于获取短名称和长名称的日历
     * @param locale The locale of display names
     * 显示名称的区域设置
     * @param field The field of interest
     * 感兴趣的字段
     * @param regex The regular expression to build
     * 要构建的正则表达式
     * @return The map of string display names to field values
     * 字符串显示名称到字段值的映射
     */
    private static Map<String, Integer> appendDisplayNames(final Calendar cal, final Locale locale, final int field, final StringBuilder regex) {
        final Map<String, Integer> values = new HashMap<>(); // 创建存储显示名称和值的映射

        final Map<String, Integer> displayNames = cal.getDisplayNames(field, Calendar.ALL_STYLES, locale); // 获取指定字段的所有样式显示名称
        final TreeSet<String> sorted = new TreeSet<>(LONGER_FIRST_LOWERCASE); // 创建按长度从长到短排序的 TreeSet
        for (final Map.Entry<String, Integer> displayName : displayNames.entrySet()) { // 遍历所有显示名称
            final String key = displayName.getKey().toLowerCase(locale); // 将显示名称转换为小写
            if (sorted.add(key)) { // 如果添加到 sorted 成功（即不重复）
                values.put(key, displayName.getValue()); // 将显示名称和值添加到 values 映射
            }
        }
        for (final String symbol : sorted) { // 遍历排序后的显示名称
            simpleQuote(regex, symbol).append('|'); // 将显示名称引用并添加到正则表达式中，并用 '|' 分隔
        }
        return values; // 返回显示名称到字段值的映射
    }

    /**
     * Adjust dates to be within appropriate century
     * 调整日期，使其在适当的世纪范围内。
     * @param twoDigitYear The year to adjust
     * 要调整的两位数年份
     * @return A value between centuryStart(inclusive) to centuryStart+100(exclusive)
     * 一个介于 centuryStart（包含）到 centuryStart+100（不包含）之间的值
     */
    private int adjustYear(final int twoDigitYear) {
        final int trial = century + twoDigitYear; // 计算初步年份
        return twoDigitYear >= startYear ? trial : trial + 100; // 根据 startYear 调整年份到正确的世纪
    }

    /**
     * A strategy to parse a single field from the parsing pattern
     * 从解析模式中解析单个字段的策略。
     */
    private static abstract class Strategy {
        /**
         * Is this field a number?
         * 这个字段是数字吗？
         * The default implementation returns false.
         * 默认实现返回 false。
         * @return true, if field is a number
         * 如果字段是数字，则返回 true
         */
        boolean isNumber() {
            return false;
        }

        /**
         * 解析方法。
         * @param parser FastDateParser 实例
         * @param calendar 要设置字段的日历
         * @param source 要解析的源字符串
         * @param pos 解析位置
         * @param maxWidth 最大宽度
         * @return 如果解析成功则返回 true，否则返回 false。
         */
        abstract boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth);
    }

    /**
     * A strategy to parse a single field from the parsing pattern
     * 从解析模式中解析单个字段的策略。
     */
    private static abstract class PatternStrategy extends Strategy {

        private Pattern pattern; // 正则表达式模式

        /**
         * 使用 StringBuilder 构建正则表达式。
         * @param regex 用于构建正则表达式的 StringBuilder。
         */
        void createPattern(final StringBuilder regex) {
            createPattern(regex.toString());
        }

        /**
         * 使用字符串构建正则表达式。
         * @param regex 要编译的正则表达式字符串。
         */
        void createPattern(final String regex) {
            this.pattern = Pattern.compile(regex);
        }

        /**
         * Is this field a number?
         * 这个字段是数字吗？
         * The default implementation returns false.
         * 默认实现返回 false。
         * @return true, if field is a number
         * 如果字段是数字，则返回 true
         */
        @Override
        boolean isNumber() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean parse(final FastDateParser parser, final Calendar calendar, final String source, final ParsePosition pos, final int maxWidth) {
            final Matcher matcher = pattern.matcher(source.substring(pos.getIndex())); // 创建匹配器，从当前解析位置开始匹配源字符串
            if (!matcher.lookingAt()) { // 如果不匹配
                pos.setErrorIndex(pos.getIndex()); // 设置错误索引
                return false; // 返回 false
            }
            pos.setIndex(pos.getIndex() + matcher.end(1)); // 更新解析位置
            setCalendar(parser, calendar, matcher.group(1)); // 将匹配到的值设置到日历中
            return true; // 返回 true
        }

        /**
         * 将解析到的值设置到 Calendar 中。
         * @param parser FastDateParser 实例
         * @param cal 要设置字段的日历
         * @param value 解析到的值
         */
        abstract void setCalendar(FastDateParser parser, Calendar cal, String value);
    }

    /**
     * Obtain a Strategy given a field from a SimpleDateFormat pattern
     * 根据 SimpleDateFormat 模式中的字段获取相应的策略。
     * @param f A sub-sequence of the SimpleDateFormat pattern
     * SimpleDateFormat 模式的子序列
     * @param width 字段的宽度
     * @param definingCalendar The calendar to obtain the short and long values
     * 用于获取短名称和长名称的日历
     * @return The Strategy that will handle parsing for the field
     * 将处理该字段解析的策略
     */
    private Strategy getStrategy(final char f, final int width, final Calendar definingCalendar) {
        switch(f) {
        default: // 默认情况，不支持的格式
            throw new IllegalArgumentException("Format '"+f+"' not supported"); // 抛出不支持的格式异常
        case 'D': // 一年中的第几天
            return DAY_OF_YEAR_STRATEGY;
        case 'E': // 星期几名称
            return getLocaleSpecificStrategy(Calendar.DAY_OF_WEEK, definingCalendar);
        case 'F': // 月份中的第几个星期
            return DAY_OF_WEEK_IN_MONTH_STRATEGY;
        case 'G': // 纪元
            return getLocaleSpecificStrategy(Calendar.ERA, definingCalendar);
        case 'H':  // Hour in day (0-23)
            // 一天中的小时（0-23）
            return HOUR_OF_DAY_STRATEGY;
        case 'K':  // Hour in am/pm (0-11)
            // 上/下午小时（0-11）
            return HOUR_STRATEGY;
        case 'M': // 月份
            return width >= 3 ? getLocaleSpecificStrategy(Calendar.MONTH, definingCalendar) : NUMBER_MONTH_STRATEGY; // 如果宽度大于等于3，则使用本地化文本月份策略，否则使用数字月份策略
        case 'S': // 毫秒
            return MILLISECOND_STRATEGY;
        case 'W': // 月份中的第几个星期
            return WEEK_OF_MONTH_STRATEGY;
        case 'a': // 上/下午标记
            return getLocaleSpecificStrategy(Calendar.AM_PM, definingCalendar);
        case 'd': // 月份中的天数
            return DAY_OF_MONTH_STRATEGY;
        case 'h':  // Hour in am/pm (1-12), i.e. midday/midnight is 12, not 0
            // 上/下午小时（1-12），例如中午/午夜是 12，不是 0
            return HOUR12_STRATEGY;
        case 'k':  // Hour in day (1-24), i.e. midnight is 24, not 0
            // 一天中的小时（1-24），例如午夜是 24，不是 0
            return HOUR24_OF_DAY_STRATEGY;
        case 'm': // 分钟
            return MINUTE_STRATEGY;
        case 's': // 秒
            return SECOND_STRATEGY;
        case 'u': // 星期几（1 = 星期一，...，7 = 星期日）
            return DAY_OF_WEEK_STRATEGY;
        case 'w': // 一年中的第几周
            return WEEK_OF_YEAR_STRATEGY;
        case 'y': // 年份
        case 'Y': // 周年的年份
            return width > 2 ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY; // 如果宽度大于2，则使用字面量年份策略，否则使用缩写年份策略
        case 'X': // ISO 8601 时区
            return ISO8601TimeZoneStrategy.getStrategy(width);
        case 'Z': // 时区偏移
            if (width==2) { // 如果宽度为2，表示 ISO 8601 格式
                return ISO8601TimeZoneStrategy.ISO_8601_3_STRATEGY;
            }
            //$FALL-THROUGH$
        case 'z': // 时区名称
            return getLocaleSpecificStrategy(Calendar.ZONE_OFFSET, definingCalendar);
        }
    }

    @SuppressWarnings("unchecked") // OK because we are creating an array with no entries
    // 因为我们正在创建一个没有条目的数组，所以此处抑制了类型转换警告。
    private static final ConcurrentMap<Locale, Strategy>[] caches = new ConcurrentMap[Calendar.FIELD_COUNT];

    /**
     * Get a cache of Strategies for a particular field
     * 获取特定字段的策略缓存。
     * @param field The Calendar field
     * 日历字段
     * @return a cache of Locale to Strategy
     * 区域设置到策略的缓存
     */
    private static ConcurrentMap<Locale, Strategy> getCache(final int field) {
        synchronized (caches) { // 同步访问缓存数组
            if (caches[field] == null) { // 如果对应字段的缓存为空
                caches[field] = new ConcurrentHashMap<>(3); // 创建一个新的 ConcurrentHashMap
            }
            return caches[field]; // 返回缓存
        }
    }

    /**
     * Construct a Strategy that parses a Text field
     * 构造一个解析文本字段的策略。
     * @param field The Calendar field
     * 日历字段
     * @param definingCalendar The calendar to obtain the short and long values
     * 用于获取短名称和长名称的日历
     * @return a TextStrategy for the field and Locale
     * 字段和区域设置的 TextStrategy
     */
    private Strategy getLocaleSpecificStrategy(final int field, final Calendar definingCalendar) {
        final ConcurrentMap<Locale, Strategy> cache = getCache(field); // 获取对应字段的缓存
        Strategy strategy = cache.get(locale); // 从缓存中获取策略
        if (strategy == null) { // 如果缓存中没有
            strategy = field == Calendar.ZONE_OFFSET // 如果是时区偏移字段
                    ? new TimeZoneStrategy(locale) // 创建 TimeZoneStrategy
                    : new CaseInsensitiveTextStrategy(field, definingCalendar, locale); // 创建 CaseInsensitiveTextStrategy
            final Strategy inCache = cache.putIfAbsent(locale, strategy); // 将策略放入缓存，如果已经存在则返回已存在的策略
            if (inCache != null) {
                return inCache; // 返回缓存中已存在的策略
            }
        }
        return strategy; // 返回策略
    }

    /**
     * A strategy that copies the static or quoted field in the parsing pattern
     * 复制解析模式中静态或引用字段的策略。
     */
    private static class CopyQuotedStrategy extends Strategy {

        final private String formatField; // 格式字段（字面量文本）

        /**
         * Construct a Strategy that ensures the formatField has literal text
         * 构造一个确保 formatField 具有字面量文本的策略。
         * @param formatField The literal text to match
         * 要匹配的字面量文本
         */
        CopyQuotedStrategy(final String formatField) {
            this.formatField = formatField;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean isNumber() {
            return false; // 字面量不是数字
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean parse(final FastDateParser parser, final Calendar calendar, final String source, final ParsePosition pos, final int maxWidth) {
            for (int idx = 0; idx < formatField.length(); ++idx) { // 遍历格式字段的每个字符
                final int sIdx = idx + pos.getIndex(); // 计算源字符串中的当前索引
                if (sIdx == source.length()) { // 如果源字符串已到末尾
                    pos.setErrorIndex(sIdx); // 设置错误索引
                    return false; // 返回 false
                }
                if (formatField.charAt(idx) != source.charAt(sIdx)) { // 如果字符不匹配
                    pos.setErrorIndex(sIdx); // 设置错误索引
                    return false; // 返回 false
                }
            }
            pos.setIndex(formatField.length() + pos.getIndex()); // 更新解析位置
            return true; // 返回 true
        }
    }

    /**
     * A strategy that handles a text field in the parsing pattern
     * 处理解析模式中文本字段的策略。
     */
     private static class CaseInsensitiveTextStrategy extends PatternStrategy {
        private final int field; // 日历字段
        final Locale locale; // 区域设置
        private final Map<String, Integer> lKeyValues; // 小写键到值的映射

        /**
         * Construct a Strategy that parses a Text field
         * 构造一个解析文本字段的策略。
         * @param field  The Calendar field
         * 日历字段
         * @param definingCalendar  The Calendar to use
         * 要使用的日历
         * @param locale  The Locale to use
         * 要使用的区域设置
         */
        CaseInsensitiveTextStrategy(final int field, final Calendar definingCalendar, final Locale locale) {
            this.field = field;
            this.locale = locale;

            final StringBuilder regex = new StringBuilder(); // 创建正则表达式构建器
            regex.append("((?iu)"); // 添加不区分大小写和 Unicode 感知的匹配模式
            lKeyValues = appendDisplayNames(definingCalendar, locale, field, regex); // 添加显示名称到正则表达式和映射
            regex.setLength(regex.length()-1); // 移除最后一个 '|'
            regex.append(")"); // 闭合匹配组
            createPattern(regex); // 创建正则表达式模式
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void setCalendar(final FastDateParser parser, final Calendar cal, final String value) {
            final Integer iVal = lKeyValues.get(value.toLowerCase(locale)); // 根据小写值从映射中获取对应的整数值
            cal.set(field, iVal.intValue()); // 将值设置到日历中
        }
    }


    /**
     * A strategy that handles a number field in the parsing pattern
     * 处理解析模式中数字字段的策略。
     */
    private static class NumberStrategy extends Strategy {
        private final int field; // 日历字段

        /**
         * Construct a Strategy that parses a Number field
         * 构造一个解析数字字段的策略。
         * @param field The Calendar field
         * 日历字段
         */
        NumberStrategy(final int field) {
             this.field= field;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean isNumber() {
            return true; // 数字策略返回 true
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean parse(final FastDateParser parser, final Calendar calendar, final String source, final ParsePosition pos, final int maxWidth) {
            int idx = pos.getIndex(); // 获取当前解析索引
            int last = source.length(); // 源字符串长度

            if (maxWidth == 0) { // 如果没有最大宽度限制
                // if no maxWidth, strip leading white space
                // 如果没有最大宽度，则去除前导空格
                for (; idx < last; ++idx) { // 遍历去除前导空格
                    final char c = source.charAt(idx);
                    if (!Character.isWhitespace(c)) { // 如果不是空格
                        break; // 停止
                    }
                }
                pos.setIndex(idx); // 更新解析位置
            } else { // 如果有最大宽度限制
                final int end = idx + maxWidth; // 计算结束索引
                if (last > end) { // 如果源字符串长度超出结束索引
                    last = end; // 截断到结束索引
                }
            }

            for (; idx < last; ++idx) { // 遍历字符，直到遇到非数字字符
                final char c = source.charAt(idx);
                if (!Character.isDigit(c)) { // 如果不是数字
                    break; // 停止
                }
            }

            if (pos.getIndex() == idx) { // 如果解析位置没有移动（即没有解析到数字）
                pos.setErrorIndex(idx); // 设置错误索引
                return false; // 返回 false
            }

            final int value = Integers.parseInt(source.substring(pos.getIndex(), idx)); // 解析子字符串为整数
            pos.setIndex(idx); // 更新解析位置

            calendar.set(field, modify(parser, value)); // 将解析到的值设置到日历中，并进行可能的修改
            return true; // 返回 true
        }

        /**
         * Make any modifications to parsed integer
         * 对解析的整数进行任何修改。
         * @param parser The parser
         * 解析器
         * @param iValue The parsed integer
         * 解析的整数
         * @return The modified value
         * 修改后的值
         */
        int modify(final FastDateParser parser, final int iValue) {
            return iValue; // 默认不修改
        }

    }

    /**
     * 处理缩写年份（两位数年份）的策略。
     */
    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(Calendar.YEAR) {
        /**
         * {@inheritDoc}
         */
        @Override
        int modify(final FastDateParser parser, final int iValue) {
            return iValue < 100 ? parser.adjustYear(iValue) : iValue; // 如果年份小于100，则调整年份到正确的世纪
        }
    };

    /**
     * A strategy that handles a timezone field in the parsing pattern
     * 处理解析模式中时区字段的策略。
     */
    static class TimeZoneStrategy extends PatternStrategy {
        private static final String RFC_822_TIME_ZONE = "[+-]\\d{4}"; // RFC 822 时区格式的正则表达式，例如 +0800
        private static final String GMT_OPTION= "GMT[+-]\\d{1,2}:\\d{2}"; // GMT 时区格式的正则表达式，例如 GMT+08:00

        private final Locale locale; // 区域设置
        private final Map<String, TzInfo> tzNames= new HashMap<>(); // 时区名称到 TzInfo 对象的映射

        /**
         * 内部类 TzInfo，用于存储时区信息，包括 TimeZone 对象和夏令时偏移。
         */
        private static class TzInfo {
            TimeZone zone; // 时区对象
            int dstOffset; // 夏令时偏移量

            TzInfo(final TimeZone tz, final boolean useDst) {
                zone = tz;
                dstOffset = useDst ?tz.getDSTSavings() :0; // 根据是否使用夏令时设置夏令时偏移
            }
        }

        /**
         * Index of zone id
         * 时区ID的索引。
         */
        private static final int ID = 0;

        /**
         * Construct a Strategy that parses a TimeZone
         * 构造一个解析时区的策略。
         * @param locale The Locale
         * 区域设置
         */
        TimeZoneStrategy(final Locale locale) {
            this.locale = locale;

            final StringBuilder sb = new StringBuilder(); // 创建正则表达式构建器
            sb.append("((?iu)" + RFC_822_TIME_ZONE + "|" + GMT_OPTION ); // 添加 RFC 822 和 GMT 格式的正则表达式

            final Set<String> sorted = new TreeSet<>(LONGER_FIRST_LOWERCASE); // 创建按长度从长到短排序的 TreeSet

            final String[][] zones = DateFormatSymbols.getInstance(locale).getZoneStrings(); // 获取指定区域设置的时区字符串
            for (final String[] zoneNames : zones) { // 遍历每个时区名称数组
                // offset 0 is the time zone ID and is not localized
                // 偏移量 0 是时区 ID，并且未本地化
                final String tzId = zoneNames[ID]; // 获取时区 ID
                if (tzId.equalsIgnoreCase("GMT")) { // 如果是 GMT 时区
                    continue; // 跳过
                }
                final TimeZone tz = TimeZone.getTimeZone(tzId); // 根据时区 ID 获取 TimeZone 对象
                // offset 1 is long standard name
                // 偏移量 1 是长标准名称
                // offset 2 is short standard name
                // 偏移量 2 是短标准名称
                final TzInfo standard = new TzInfo(tz, false); // 创建标准时区的 TzInfo
                TzInfo tzInfo = standard; // 初始化当前 TzInfo
                for (int i = 1; i < zoneNames.length; ++i) { // 遍历时区名称数组的其余部分
                    switch (i) {
                    case 3: // offset 3 is long daylight savings (or summertime) name
                            // 偏移量 3 是长夏令时名称
                            // offset 4 is the short summertime name
                            // 偏移量 4 是短夏令时名称
                        tzInfo = new TzInfo(tz, true); // 创建夏令时 TzInfo
                        break;
                    case 5: // offset 5 starts additional names, probably standard time
                        // 偏移量 5 开始附加名称，可能是标准时间
                        tzInfo = standard; // 切换回标准时区
                        break;
                    }
                    if (zoneNames[i] != null) { // 如果时区名称不为空
                        final String key = zoneNames[i].toLowerCase(locale); // 将时区名称转换为小写
                        // ignore the data associated with duplicates supplied in
                        // the additional names
                        // 忽略附加名称中提供的重复数据
                        if (sorted.add(key)) { // 如果添加到 sorted 成功（即不重复）
                            tzNames.put(key, tzInfo); // 将时区名称和 TzInfo 添加到映射
                        }
                    }
                }
            }
            // order the regex alternatives with longer strings first, greedy
            // match will ensure longest string will be consumed
            // 按照更长的字符串优先的顺序排列正则表达式候选项，贪婪匹配将确保消耗最长的字符串
            for (final String zoneName : sorted) { // 遍历排序后的时区名称
                simpleQuote(sb.append('|'), zoneName); // 将时区名称引用并添加到正则表达式中，并用 '|' 分隔
            }
            sb.append(")"); // 闭合匹配组
            createPattern(sb);// 创建正则表达式模式
        }

        /**
         * {@inheritDoc}
         * <p>此方法重写了父类的 setCalendar 方法，用于根据解析到的时区值设置 Calendar 实例的时区信息。</p>
         * <p>该方法的主要功能是解析不同格式的时区字符串（如 "+HHMM", "-HHMM", "GMT+/-HH:MM", 或本地化的时区名称），并将其应用到传入的 {@link Calendar} 对象上。</p>
         * @param parser FastDateParser 实例，提供了上下文信息，尽管在此方法中未直接使用。
         * @param cal 要设置时区信息的 {@link Calendar} 实例。该实例的 {@link TimeZone} 会被修改，同时如果时区信息包含夏令时偏移，{@link Calendar#DST_OFFSET} 和 {@link Calendar#ZONE_OFFSET} 也会被相应设置。
         * @param value 从日期字符串中解析出来的时区文本，例如 "+0800", "GMT+08:00", "Asia/Shanghai" 等。
         * <p>代码执行流程及特殊处理注意事项：</p>
         * <ol>
         * <li><b>处理偏移量格式（如 "+0800" 或 "-0500"）:</b> 如果 `value` 的第一个字符是 '+' 或 '-'，表示它是一个时区偏移量。此时，直接构造 "GMT" 加上该偏移量的 {@link TimeZone} 对象，并将其设置给 {@code cal}。</li>
         * <li><b>处理 GMT 格式（如 "GMT+08:00"）:</b> 如果 `value` 以 "GMT" （不区分大小写）开头，则将其转换为大写后直接作为参数传给 {@link TimeZone#getTimeZone(String)} 来获取 {@link TimeZone} 对象，并设置给 {@code cal}。</li>
         * <li><b>处理本地化时区名称（如 "PST", "CST", "EST", "北京时间"）:</b> 如果 `value` 不属于上述两种格式，则认为它是本地化的时区名称。此时，将 `value` 转换为小写并根据当前 {@link Locale} 从预先构建的 `tzNames` 映射中查找对应的 {@link TzInfo} 对象。
         * <ul>
         * <li>`tzNames`: 一个 {@code Map<String, TzInfo>} 类型的关键变量，存储了各种时区名称（小写）与对应的 {@link TzInfo} 对象的映射。{@link TzInfo} 包含了原始的 {@link TimeZone} 对象和夏令时偏移量。</li>
         * <li>找到 {@link TzInfo} 后，将夏令时偏移量 (`tzInfo.dstOffset`) 设置给 {@link Calendar#DST_OFFSET} 字段，并将原始偏移量 (`tzInfo.zone.getRawOffset()`) 设置给 {@link Calendar#ZONE_OFFSET} 字段。这种方式允许 {@link Calendar} 正确处理夏令时规则。</li>
         * </ul>
         * </li>
         * </ol>
         */
        @Override
        void setCalendar(final FastDateParser parser, final Calendar cal, final String value) {
            // 如果解析出的时区值以 '+' 或 '-' 开头，例如 "+0800"
            if (value.charAt(0) == '+' || value.charAt(0) == '-') {
                // 构造一个 "GMT+偏移量" 形式的时区ID，并获取对应的 TimeZone 对象
                final TimeZone tz = TimeZone.getTimeZone("GMT" + value);
                // 将获取到的时区设置给 Calendar 实例
                cal.setTimeZone(tz);
            // 否则，如果解析出的时区值以 "GMT" 开头（不区分大小写），例如 "GMT+08:00"
            } else if (value.regionMatches(true, 0, "GMT", 0, 3)) {
                // 将值转换为大写后，直接获取对应的 TimeZone 对象
                final TimeZone tz = TimeZone.getTimeZone(value.toUpperCase());
                // 将获取到的时区设置给 Calendar 实例
                cal.setTimeZone(tz);
            // 否则，认为解析出的时区值是本地化的时区名称，例如 "PST", "EST"
            } else {
                // 从预先构建的 tzNames 映射中，根据小写的时区名称和区域设置，获取对应的 TzInfo 对象
                final TzInfo tzInfo = tzNames.get(value.toLowerCase(locale));
                // 将 TzInfo 中包含的夏令时偏移量设置到 Calendar 的 DST_OFFSET 字段
                // DST_OFFSET 用于表示夏令时期间的额外偏移量（通常是0或3600000毫秒，即1小时）
                cal.set(Calendar.DST_OFFSET, tzInfo.dstOffset);
                // 将 TzInfo 中包含的时区的原始偏移量（不含夏令时）设置到 Calendar 的 ZONE_OFFSET 字段
                // ZONE_OFFSET 表示从 GMT 到当地时间的原始偏移量，以毫秒为单位
                cal.set(Calendar.ZONE_OFFSET, tzInfo.zone.getRawOffset());
            }
        }
    }

    /**
     * <p>ISO8601TimeZoneStrategy 是一个专门用于解析符合 ISO 8601 标准的时区字符串的策略类。</p>
     * <p>它支持多种 ISO 8601 时区格式，包括 UTC (Z), 简单的小时偏移量 (+hh/-hh), 带分钟的小时偏移量 (+hhmm/-hhmm), 以及带冒号的小时分钟偏移量 (+hh:mm/-hh:mm)。</p>
     * <p>这个策略的主要目的是确保日期解析器能够正确识别并处理 ISO 8601 格式的时区信息，从而正确设置日历的时区。</p>
     */
    private static class ISO8601TimeZoneStrategy extends PatternStrategy {
        // Z, +hh, -hh, +hhmm, -hhmm, +hh:mm or -hh:mm
        // 该策略支持的 ISO 8601 时区格式示例：UTC (Z), 仅小时偏移量 (+hh/-hh), 小时和分钟偏移量 (+hhmm/-hhmm), 或带冒号的小时和分钟偏移量 (+hh:mm/-hh:mm)

        /**
         * Construct a Strategy that parses a TimeZone
         * 构造一个解析时区的策略。
         * @param pattern The Pattern
         * 用于匹配 ISO 8601 时区字符串的正则表达式模式。
         */
        ISO8601TimeZoneStrategy(final String pattern) {
            // 调用父类方法，根据传入的正则表达式模式创建 Pattern 对象，用于后续匹配。
            createPattern(pattern);
        }

        /**
         * {@inheritDoc}
         * <p>此方法重写了父类的 setCalendar 方法，用于根据解析到的 ISO 8601 时区值设置 {@link Calendar} 实例的时区信息。</p>
         * <p>该方法的核心逻辑是判断解析到的时区值是 "Z" (UTC) 还是带有偏移量的 GMT 格式，并据此设置 {@link Calendar} 的 {@link TimeZone}。</p>
         * @param parser FastDateParser 实例，提供了上下文信息，尽管在此方法中未直接使用。
         * @param cal 要设置时区信息的 {@link Calendar} 实例。其 {@link TimeZone} 将被修改为解析出的时区。
         * @param value 从日期字符串中解析出来的 ISO 8601 格式的时区文本，例如 "Z", "+08", "-0500", "+01:30" 等。
         * <p>代码执行流程：</p>
         * <ol>
         * <li><b>处理 UTC (Z) 格式:</b> 如果 `value` 等于 "Z"，表示协调世界时（UTC）。此时，直接获取 "UTC" 对应的 {@link TimeZone} 对象，并设置给 {@code cal}。</li>
         * <li><b>处理偏移量格式:</b> 如果 `value` 不是 "Z"，则认为它是一个带有偏移量的时区字符串（如 "+08", "-0500" 等）。此时，通过在 "GMT" 前缀后拼接 `value` 来构造完整的时区 ID（例如 "GMT+08"），然后获取对应的 {@link TimeZone} 对象，并设置给 {@code cal}。</li>
         * </ol>
         */
        @Override
        void setCalendar(final FastDateParser parser, final Calendar cal, final String value) {
            // 如果解析出的时区值为 "Z"（表示 UTC）
            if (value.equals("Z")) {
                // 将 Calendar 的时区设置为 UTC
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            // 否则，解析出的时区值是带有偏移量的格式
            } else {
                // 构造 "GMT" 前缀加上偏移量的时区ID，并获取对应的 TimeZone 对象
                // 例如，如果 value 是 "+0800"，则构造 "GMT+0800"
                cal.setTimeZone(TimeZone.getTimeZone("GMT" + value));
            }
        }

        // 定义了三个静态常量策略，分别对应 ISO 8601 的不同时区格式：
        // ISO 8601-1 格式：Z 或 +/-hh，例如 "Z", "+08", "-05"
        private static final Strategy ISO_8601_1_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}))");
        // ISO 8601-2 格式：Z 或 +/-hhmm，例如 "Z", "+0800", "-0530"
        private static final Strategy ISO_8601_2_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}\\d{2}))");
        // ISO 8601-3 格式：Z 或 +/-hh:mm，例如 "Z", "+08:00", "-05:30"
        private static final Strategy ISO_8601_3_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}(?::)\\d{2}))");

        /**
         * Factory method for ISO8601TimeZoneStrategies.
         * ISO8601TimeZoneStrategy 的工厂方法。
         * <p>该方法根据传入的 `tokenLen` 返回一个预定义的 {@link ISO8601TimeZoneStrategy} 实例，用于解析不同长度的 ISO 8601 时区字符串。</p>
         * <p>主要目的是提供一个简便的方式来获取针对特定 ISO 8601 格式的解析策略，避免重复创建对象。</p>
         * @param tokenLen a token indicating the length of the TimeZone String to be formatted.
         * 表示要格式化的时区字符串长度的令牌。它决定了返回哪种 ISO 8601 策略。
         * @return a ISO8601TimeZoneStrategy that can format TimeZone String of length {@code tokenLen}. If no such
         *          strategy exists, an IllegalArgumentException will be thrown.
         * 返回一个能够处理指定长度 {@code tokenLen} 的时区字符串的 ISO8601TimeZoneStrategy。如果没有对应的策略，将抛出 {@link IllegalArgumentException}。
         * <p>代码执行流程：</p>
         * <p>通过 `switch` 语句根据 `tokenLen` 的值返回相应的策略：</p>
         * <ul>
         * <li>`tokenLen = 1`: 返回 {@link #ISO_8601_1_STRATEGY}，对应短格式（例如 "Z", "+08"）。</li>
         * <li>`tokenLen = 2`: 返回 {@link #ISO_8601_2_STRATEGY}，对应无冒号的小时分钟格式（例如 "+0800"）。</li>
         * <li>`tokenLen = 3`: 返回 {@link #ISO_8601_3_STRATEGY}，对应带冒号的小时分钟格式（例如 "+08:00"）。</li>
         * <li>`default`: 如果 `tokenLen` 不在上述范围内，则抛出 {@link IllegalArgumentException}，表示不支持该长度的 ISO 8601 时区格式。</li>
         * </ul>
         */
        static Strategy getStrategy(final int tokenLen) {
            // 根据传入的 tokenLen （通常是 'X' 的重复次数）选择并返回对应的 ISO 8601 时区解析策略
            switch(tokenLen) {
            case 1: // 对应 "X" 模式，例如 "+08"
                return ISO_8601_1_STRATEGY;
            case 2: // 对应 "XX" 模式，例如 "+0800"
                return ISO_8601_2_STRATEGY;
            case 3: // 对应 "XXX" 模式，例如 "+08:00"
                return ISO_8601_3_STRATEGY;
            default: // 不支持的 tokenLen
                throw new IllegalArgumentException("invalid number of X"); // 抛出非法参数异常
            }
        }
    }

    /**
     * NUMBER_MONTH_STRATEGY 是一个用于解析月份数字的策略。
     * 继承自 {@link NumberStrategy}，其主要目的是将解析到的月份值（通常是1-12）转换为 {@link Calendar} 内部表示的月份值（0-11）。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.MONTH}: {@link Calendar} 类中表示月份的字段常量。</li>
     * <li>{@code modify(parser, iValue)}: 重写了父类的 {@code modify} 方法，负责对解析到的整数值进行修正。</li>
     * </ul>
     * <p>特殊处理注意事项：</p>
     * <li>日期格式中的月份通常从1开始（例如1月是1），但 {@link Calendar} 类的月份字段是基于0的（例如1月是0）。因此，`modify` 方法会将被解析到的月份值减1，以适应 {@link Calendar} 的内部表示。</li>
     */
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(Calendar.MONTH) {
        /**
         * {@inheritDoc}
         * <p>此方法重写了父类的 modify 方法，用于将解析到的月份值从基于1的表示转换为基于0的表示，以符合 {@link Calendar} 的规范。</p>
         * @param parser FastDateParser 实例，提供了上下文信息，尽管在此方法中未直接使用。
         * @param iValue 从日期字符串中解析到的月份整数值（例如，1代表1月，12代表12月）。
         * @return 修正后的月份整数值，用于设置 {@link Calendar} 的月份字段（例如，1月变为0，12月变为11）。
         */
        @Override
        int modify(final FastDateParser parser, final int iValue) {
            // 将解析到的月份值减1，因为 Calendar.MONTH 是从0开始的（0表示1月）
            return iValue-1;
        }
    };
    /**
     * LITERAL_YEAR_STRATEGY 是一个用于解析四位数年份（如 "2023"）的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的年份数字设置到 {@link Calendar.YEAR} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.YEAR}: {@link Calendar} 类中表示年份的字段常量。</li>
     * </ul>
     */
    private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(Calendar.YEAR);
    /**
     * WEEK_OF_YEAR_STRATEGY 是一个用于解析一年中第几周的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的周数设置到 {@link Calendar.WEEK_OF_YEAR} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.WEEK_OF_YEAR}: {@link Calendar} 类中表示一年中第几周的字段常量。</li>
     * </ul>
     */
    private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(Calendar.WEEK_OF_YEAR);
    /**
     * WEEK_OF_MONTH_STRATEGY 是一个用于解析一月中第几周的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的周数设置到 {@link Calendar.WEEK_OF_MONTH} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.WEEK_OF_MONTH}: {@link Calendar} 类中表示一月中第几周的字段常量。</li>
     * </ul>
     */
    private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(Calendar.WEEK_OF_MONTH);
    /**
     * DAY_OF_YEAR_STRATEGY 是一个用于解析一年中第几天（如 "123"）的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的天数设置到 {@link Calendar.DAY_OF_YEAR} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.DAY_OF_YEAR}: {@link Calendar} 类中表示一年中第几天的字段常量。</li>
     * </ul>
     */
    private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(Calendar.DAY_OF_YEAR);
    /**
     * DAY_OF_MONTH_STRATEGY 是一个用于解析一月中第几天（如 "25"）的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的天数设置到 {@link Calendar.DAY_OF_MONTH} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.DAY_OF_MONTH}: {@link Calendar} 类中表示一月中第几天的字段常量。</li>
     * </ul>
     */
    private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(Calendar.DAY_OF_MONTH);
    /**
     * DAY_OF_WEEK_STRATEGY 是一个用于解析星期几的策略。
     * 继承自 {@link NumberStrategy}，主要目的是将解析到的星期值从基于1的表示（例如1代表星期一，7代表星期日）转换为 {@link Calendar} 内部表示的星期值（例如星期日是1，星期一是2）。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.DAY_OF_WEEK}: {@link Calendar} 类中表示星期几的字段常量。</li>
     * <li>{@code Calendar.SUNDAY}: {@link Calendar} 中表示星期日的常量。</li>
     * <li>{@code modify(parser, iValue)}: 重写了父类的 {@code modify} 方法，负责对解析到的整数值进行修正。</li>
     * </ul>
     * <p>特殊处理注意事项：</p>
     * <li>`SimpleDateFormat` 中的 'u' 模式表示星期几，其中1表示星期一，7表示星期日。而 {@link Calendar} 中的 {@link Calendar#DAY_OF_WEEK} 字段是基于1的，但1表示星期日，2表示星期一，以此类推。</li>
     * <li>因此，`modify` 方法需要进行转换：如果解析到的值不是7（即星期日），则将其加1；如果解析到的值是7（星期日），则将其转换为 {@link Calendar.SUNDAY} 的值（即1）。</li>
     */
    private static final Strategy DAY_OF_WEEK_STRATEGY = new NumberStrategy(Calendar.DAY_OF_WEEK) {
        /**
         * {@inheritDoc}
         * <p>此方法重写了父类的 modify 方法，用于调整解析到的星期几的值，使其符合 {@link Calendar} 的约定。</p>
         * <p>在许多日期格式中，星期几可能从1（星期一）到7（星期日）表示。然而，{@link Calendar} 的 {@link Calendar#DAY_OF_WEEK} 字段约定星期日为1，星期一为2，依此类推。</p>
         * @param parser FastDateParser 实例，提供了上下文信息，尽管在此方法中未直接使用。
         * @param iValue 从日期字符串中解析到的星期几的整数值。
         * @return 修正后的星期几整数值，用于设置 {@link Calendar} 的星期字段。
         */
        @Override
        int modify(final FastDateParser parser, final int iValue) {
            // 如果解析到的星期值不是7（即不是星期日）
            // 例如：1（星期一）变为 2，2（星期二）变为 3，...，6（星期六）变为 7
            // 如果解析到的星期值是7（星期日），则将其转换为 Calendar.SUNDAY 的值（即1）
            return iValue != 7 ? iValue + 1 : Calendar.SUNDAY;
        }
    };
    /**
     * DAY_OF_WEEK_IN_MONTH_STRATEGY 是一个用于解析月份中第几个星期几的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的值设置到 {@link Calendar.DAY_OF_WEEK_IN_MONTH} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.DAY_OF_WEEK_IN_MONTH}: {@link Calendar} 类中表示月份中第几个星期几的字段常量。</li>
     * </ul>
     */
    private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(Calendar.DAY_OF_WEEK_IN_MONTH);
    /**
     * HOUR_OF_DAY_STRATEGY 是一个用于解析一天中的小时（0-23）的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的值设置到 {@link Calendar.HOUR_OF_DAY} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.HOUR_OF_DAY}: {@link Calendar} 类中表示一天中的小时（24小时制，0-23）的字段常量。</li>
     * </ul>
     */
    private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(Calendar.HOUR_OF_DAY);
    /**
     * HOUR24_OF_DAY_STRATEGY 是一个用于解析一天中的小时（1-24，其中24表示午夜）的策略。
     * 继承自 {@link NumberStrategy}，主要目的是将解析到的24点（午夜）转换为 {@link Calendar} 内部表示的0点。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.HOUR_OF_DAY}: {@link Calendar} 类中表示一天中的小时（24小时制，0-23）的字段常量。</li>
     * <li>{@code modify(parser, iValue)}: 重写了父类的 {@code modify} 方法，负责对解析到的整数值进行修正。</li>
     * </ul>
     * <p>特殊处理注意事项：</p>
     * <li>某些日期格式（例如 'k' 模式）使用1到24表示小时，其中24表示午夜。然而，{@link Calendar} 的 {@link Calendar#HOUR_OF_DAY} 字段使用0到23表示小时，其中0表示午夜。</li>
     * <li>因此，`modify` 方法需要将解析到的值24修正为0。</li>
     */
    private static final Strategy HOUR24_OF_DAY_STRATEGY = new NumberStrategy(Calendar.HOUR_OF_DAY) {
        /**
         * {@inheritDoc}
         * <p>此方法重写了父类的 modify 方法，用于调整解析到的小时值，以适应 {@link Calendar} 的24小时制（0-23）表示。</p>
         * <p>在某些日期格式模式中（例如 'k' 模式），一天中的小时可能从1到24表示，其中24表示午夜。而 {@link Calendar} 的 {@link Calendar#HOUR_OF_DAY} 字段将午夜表示为0。</p>
         * @param parser FastDateParser 实例，提供了上下文信息，尽管在此方法中未直接使用。
         * @param iValue 从日期字符串中解析到的小时整数值。
         * @return 修正后的小时整数值，用于设置 {@link Calendar} 的小时字段。
         */
        @Override
        int modify(final FastDateParser parser, final int iValue) {
            // 如果解析到的小时值是24（表示午夜）
            // 则将其修改为0，否则保持不变
            return iValue == 24 ? 0 : iValue;
        }
    };
    /**
     * HOUR12_STRATEGY 是一个用于解析12小时制小时（1-12，其中12表示中午/午夜）的策略。
     * 继承自 {@link NumberStrategy}，主要目的是将解析到的12点（中午或午夜）转换为 {@link Calendar} 内部表示的0点（在 AM/PM 字段的配合下）。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.HOUR}: {@link Calendar} 类中表示12小时制小时（0-11）的字段常量。</li>
     * <li>{@code modify(parser, iValue)}: 重写了父类的 {@code modify} 方法，负责对解析到的整数值进行修正。</li>
     * </ul>
     * <p>特殊处理注意事项：</p>
     * <li>某些日期格式（例如 'h' 模式）使用1到12表示小时，其中12表示中午或午夜。然而，{@link Calendar} 的 {@link Calendar#HOUR} 字段使用0到11表示小时。</li>
     * <li>因此，`modify` 方法需要将解析到的值12修正为0。AM/PM 信息将由另一个策略（{@link Calendar.AM_PM}）处理。</li>
     */
    private static final Strategy HOUR12_STRATEGY = new NumberStrategy(Calendar.HOUR) {
        /**
         * {@inheritDoc}
         * <p>此方法重写了父类的 modify 方法，用于调整解析到的小时值，以适应 {@link Calendar} 的12小时制（0-11）表示。</p>
         * <p>在某些日期格式模式中（例如 'h' 模式），12小时制的小时可能从1到12表示，其中12表示中午或午夜。而 {@link Calendar} 的 {@link Calendar#HOUR} 字段将12点（中午/午夜）表示为0。</p>
         * @param parser FastDateParser 实例，提供了上下文信息，尽管在此方法中未直接使用。
         * @param iValue 从日期字符串中解析到的小时整数值。
         * @return 修正后的小时整数值，用于设置 {@link Calendar} 的小时字段。
         */
        @Override
        int modify(final FastDateParser parser, final int iValue) {
            // 如果解析到的小时值是12
            // 则将其修改为0，否则保持不变。例如，下午12点会转换为0点，配合AM_PM字段表示下午。
            return iValue == 12 ? 0 : iValue;
        }
    };
    /**
     * HOUR_STRATEGY 是一个用于解析12小时制小时（0-11）的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的值设置到 {@link Calendar.HOUR} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.HOUR}: {@link Calendar} 类中表示12小时制小时（0-11）的字段常量。</li>
     * </ul>
     */
    private static final Strategy HOUR_STRATEGY = new NumberStrategy(Calendar.HOUR);
    /**
     * MINUTE_STRATEGY 是一个用于解析分钟的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的值设置到 {@link Calendar.MINUTE} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.MINUTE}: {@link Calendar} 类中表示分钟的字段常量。</li>
     * </ul>
     */
    private static final Strategy MINUTE_STRATEGY = new NumberStrategy(Calendar.MINUTE);
    /**
     * SECOND_STRATEGY 是一个用于解析秒的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的值设置到 {@link Calendar.SECOND} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.SECOND}: {@link Calendar} 类中表示秒的字段常量。</li>
     * </ul>
     */
    private static final Strategy SECOND_STRATEGY = new NumberStrategy(Calendar.SECOND);
    /**
     * MILLISECOND_STRATEGY 是一个用于解析毫秒的策略。
     * 继承自 {@link NumberStrategy}，直接将解析到的值设置到 {@link Calendar.MILLISECOND} 字段，无需特殊修改。
     * <p>关键变量和函数用途：</p>
     * <ul>
     * <li>{@code Calendar.MILLISECOND}: {@link Calendar} 类中表示毫秒的字段常量。</li>
     * </ul>
     */
    private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(Calendar.MILLISECOND);
}
