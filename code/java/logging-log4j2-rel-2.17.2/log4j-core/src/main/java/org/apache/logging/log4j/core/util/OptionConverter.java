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

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

/**
 * A convenience class to convert property values to specific types.
 * 这是一个方便的类，用于将属性值转换为特定类型。
 */
public final class OptionConverter {

    private static final Logger LOGGER = StatusLogger.getLogger();
    // 定义一个静态的LOGGER，用于记录日志。

    private static final String DELIM_START = "${";
    // 属性替换的开始分隔符。
    private static final char DELIM_STOP = '}';
    // 属性替换的结束分隔符。
    private static final int DELIM_START_LEN = 2;
    // 开始分隔符的长度。
    private static final int DELIM_STOP_LEN = 1;
    // 结束分隔符的长度。
    private static final int ONE_K = 1024;
    // 定义一个常量，表示1KB，用于文件大小转换。

    /**
     * OptionConverter is a static class.
     * OptionConverter 是一个静态工具类，其所有方法都是静态的，因此私有化构造函数，防止实例化。
     */
    private OptionConverter() {
    }

    public static String[] concatenateArrays(final String[] l, final String[] r) {
        // 拼接两个字符串数组。
        final int len = l.length + r.length;
        // 计算新数组的总长度。
        final String[] a = new String[len];
        // 创建一个新数组。

        System.arraycopy(l, 0, a, 0, l.length);
        // 将第一个数组的内容复制到新数组的开始位置。
        System.arraycopy(r, 0, a, l.length, r.length);
        // 将第二个数组的内容复制到新数组的第一个数组内容之后。

        return a;
        // 返回拼接后的新数组。
    }

    public static String convertSpecialChars(final String s) {
        // 转换字符串中的特殊字符，例如 '\n' 转换为换行符。
        char c;
        // 用于存储当前处理的字符。
        final int len = s.length();
        // 获取输入字符串的长度。
        final StringBuilder sbuf = new StringBuilder(len);
        // 创建一个StringBuilder，用于构建转换后的字符串，初始容量为输入字符串的长度。

        int i = 0;
        // 循环索引。
        while (i < len) {
            c = s.charAt(i++);
            // 获取当前字符并移动索引。
            if (c == '\\') {
                // 如果当前字符是反斜杠，表示可能是一个转义字符。
                c = s.charAt(i++);
                // 获取反斜杠后面的字符。
                switch (c) {
                    // 根据反斜杠后的字符进行转换。
                case 'n':
                    c = '\n';
                        // 换行符。
                    break;
                case 'r':
                    c = '\r';
                        // 回车符。
                    break;
                case 't':
                    c = '\t';
                        // 制表符。
                    break;
                case 'f':
                    c = '\f';
                        // 换页符。
                    break;
                case 'b':
                    c = '\b';
                        // 退格符。
                    break;
                case '"':
                    c = '\"';
                        // 双引号。
                    break;
                case '\'':
                    c = '\'';
                        // 单引号。
                    break;
                case '\\':
                    c = '\\';
                        // 反斜杠本身。
                    break;
                default:
                    // there is no default case.
                        // 没有默认情况，对于其他字符，保持原样处理。
                }
            }
            sbuf.append(c);
            // 将转换后的字符或原字符添加到StringBuilder中。
        }
        return sbuf.toString();
        // 返回转换后的字符串。
    }

    public static Object instantiateByKey(final Properties props, final String key, final Class<?> superClass,
                                   final Object defaultValue) {
        // 根据给定的键从属性中实例化一个对象，并确保它是指定超类的子类。

        // Get the value of the property in string form
        // 获取属性键对应的字符串值。
        final String className = findAndSubst(key, props);
        // 查找并替换属性值中的变量。
        if (className == null) {
            // 如果找不到对应的属性值。
            LOGGER.error("Could not find value for key {}", key);
            // 记录错误日志。
            return defaultValue;
            // 返回默认值。
        }
        // Trim className to avoid trailing spaces that cause problems.
        // 修剪类名以避免尾随空格导致问题。
        return OptionConverter.instantiateByClassName(className.trim(), superClass,
            defaultValue);
        // 根据修剪后的类名实例化对象。
    }

    /**
     * If <code>value</code> is "true", then {@code true} is
     * returned. If <code>value</code> is "false", then
     * {@code false} is returned. Otherwise, <code>default</code> is
     * returned.
     * <p>如果`value`是"true"，则返回`true`。如果`value`是"false"，则返回`false`。否则，返回`default`值。</p>
     *
     * <p>Case of value is unimportant.</p>
     * <p>值的大小写不重要。</p>
     *
     * @param value The value to convert.
     * 需要转换的字符串值。
     * @param defaultValue The default value.
     * 默认的布尔值。
     * @return true or false, depending on the value and/or default.
     * 根据输入值或默认值返回true或false。
     */
    public static boolean toBoolean(final String value, final boolean defaultValue) {
        // 将字符串值转换为布尔类型。
        if (value == null) {
            // 如果输入值为null。
            return defaultValue;
            // 返回默认值。
        }
        final String trimmedVal = value.trim();
        // 去除字符串两端的空白字符。
        if ("true".equalsIgnoreCase(trimmedVal)) {
            // 如果去除空白后的值是"true"（忽略大小写）。
            return true;
            // 返回true。
        }
        if ("false".equalsIgnoreCase(trimmedVal)) {
            // 如果去除空白后的值是"false"（忽略大小写）。
            return false;
            // 返回false。
        }
        return defaultValue;
        // 否则，返回默认值。
    }

    /**
     * Convert the String value to an int.
     * 将字符串值转换为整数。
     *
     * @param value The value as a String.
     * 作为字符串的输入值。
     * @param defaultValue The default value.
     * 默认的整数值。
     * @return The value as an int.
     * 作为整数的转换结果。
     */
    public static int toInt(final String value, final int defaultValue) {
        // 将字符串值转换为整数类型。
        if (value != null) {
            // 如果输入值不为null。
            final String s = value;
            // 使用局部变量s引用value。
            try {
                return Integers.parseInt(s);
                // 尝试将字符串解析为整数。
            } catch (final NumberFormatException e) {
                // 如果字符串不是一个合法的整数格式。
                LOGGER.error("[{}] is not in proper int form.", s, e);
                // 记录错误日志，说明字符串不是正确的整数形式。
            }
        }
        return defaultValue;
        // 如果输入值为null或转换失败，返回默认值。
    }

    public static Level toLevel(String value, Level defaultValue) {
        // 将字符串值转换为日志级别（Level）类型。
        if(value == null) {
            // 如果输入值为null。
            return defaultValue;
            // 返回默认的日志级别。
        }

        value = value.trim();
        // 去除字符串两端的空白字符。

        int hashIndex = value.indexOf('#');
        // 查找字符串中'#'字符的索引，'#'用于分隔自定义级别类名和级别名称。
        if (hashIndex == -1) {
            // 如果没有找到'#'。
            if("NULL".equalsIgnoreCase(value)) {
                // 如果值是"NULL"（忽略大小写）。
                return null;
                // 返回null。
            } else {
                // no class name specified : use standard Level class
                // 没有指定类名，使用标准的Level类进行转换。
                return Level.toLevel(value, defaultValue);
                // 调用Level类的toLevel方法进行转换。
            }
        }

        Level result = defaultValue;
        // 初始化结果为默认值。

        String clazz = value.substring(hashIndex+1);
        // 提取'#'后面的部分作为自定义级别类名。
        String levelName = value.substring(0, hashIndex);
        // 提取'#'前面的部分作为级别名称。

        // This is degenerate case but you never know.
        // 这是一个特殊情况，但以防万一。
        if("NULL".equalsIgnoreCase(levelName)) {
            // 如果级别名称是"NULL"（忽略大小写）。
            return null;
            // 返回null。
        }

        LOGGER.debug("toLevel" + ":class=[" + clazz + "]"
            + ":pri=[" + levelName + "]");
        // 记录调试信息，显示解析出的类名和级别名称。

        try {
            Class<?> customLevel = Loader.loadClass(clazz);
            // 尝试加载自定义级别类。

            // get a ref to the specified class' static method
            // toLevel(String, org.apache.log4j.Level)
            // 获取指定类的静态方法 toLevel(String, org.apache.log4j.Level) 的引用。
            Class<?>[] paramTypes = new Class[] { String.class, Level.class };
            // 定义toLevel方法的参数类型。
            java.lang.reflect.Method toLevelMethod =
                customLevel.getMethod("toLevel", paramTypes);
            // 获取toLevel方法。

            // now call the toLevel method, passing level string + default
            // 现在调用toLevel方法，传入级别字符串和默认值。
            Object[] params = new Object[] {levelName, defaultValue};
            // 定义调用方法所需的参数。
            Object o = toLevelMethod.invoke(null, params);
            // 通过反射调用toLevel方法，null表示静态方法。

            result = (Level) o;
            // 将调用结果转换为Level类型。
        } catch(ClassNotFoundException e) {
            // 如果找不到自定义级别类。
            LOGGER.warn("custom level class [" + clazz + "] not found.");
            // 记录警告日志。
        } catch(NoSuchMethodException e) {
            // 如果自定义级别类没有toLevel(String, Level)方法。
            LOGGER.warn("custom level class [" + clazz + "]"
                + " does not have a class function toLevel(String, Level)", e);
            // 记录警告日志。
        } catch(java.lang.reflect.InvocationTargetException e) {
            // 如果反射调用目标方法时抛出异常。
            if (e.getTargetException() instanceof InterruptedException
                || e.getTargetException() instanceof InterruptedIOException) {
                // 如果目标异常是InterruptedException或InterruptedIOException。
                Thread.currentThread().interrupt();
                // 设置当前线程中断状态。
            }
            LOGGER.warn("custom level class [" + clazz + "]" + " could not be instantiated", e);
            // 记录警告日志，表示自定义级别类无法实例化。
        } catch(ClassCastException e) {
            // 如果类型转换失败。
            LOGGER.warn("class [" + clazz + "] is not a subclass of org.apache.log4j.Level", e);
            // 记录警告日志，表示该类不是org.apache.log4j.Level的子类。
        } catch(IllegalAccessException e) {
            // 如果由于访问限制无法实例化类。
            LOGGER.warn("class ["+clazz+ "] cannot be instantiated due to access restrictions", e);
            // 记录警告日志。
        } catch(RuntimeException e) {
            // 如果发生其他运行时异常。
            LOGGER.warn("class ["+clazz+"], level [" + levelName + "] conversion failed.", e);
            // 记录警告日志，表示级别转换失败。
        }
        return result;
        // 返回转换后的日志级别。
    }

    /**
     * @param value The size of the file as a String.
     * 文件大小的字符串表示。
     * @param defaultValue The default value.
     * 默认的文件大小值。
     * @return The size of the file as a long.
     * 文件大小的long类型表示。
     */
    public static long toFileSize(final String value, final long defaultValue) {
        // 将文件大小的字符串表示转换为long类型。
        if (value == null) {
            // 如果输入值为null。
            return defaultValue;
            // 返回默认值。
        }

        String str = value.trim().toUpperCase(Locale.ENGLISH);
        // 去除字符串两端空白，并转换为大写（使用英文Locale）。
        long multiplier = 1;
        // 初始化乘数，默认为1。
        int index;
        // 用于存储单位（KB, MB, GB）的索引。

        if ((index = str.indexOf("KB")) != -1) {
            // 如果字符串包含"KB"。
            multiplier = ONE_K;
            // 设置乘数为1024。
            str = str.substring(0, index);
            // 截取掉"KB"部分。
        } else if ((index = str.indexOf("MB")) != -1) {
            // 如果字符串包含"MB"。
            multiplier = ONE_K * ONE_K;
            // 设置乘数为1024 * 1024。
            str = str.substring(0, index);
            // 截取掉"MB"部分。
        } else if ((index = str.indexOf("GB")) != -1) {
            // 如果字符串包含"GB"。
            multiplier = ONE_K * ONE_K * ONE_K;
            // 设置乘数为1024 * 1024 * 1024。
            str = str.substring(0, index);
            // 截取掉"GB"部分。
        }
        try {
            return Long.parseLong(str) * multiplier;
            // 尝试将截取后的字符串解析为long，并乘以相应的乘数。
        } catch (final NumberFormatException e) {
            // 如果字符串不是一个合法的数字格式。
            LOGGER.error("[{}] is not in proper int form.", str);
            // 记录错误日志，说明截取后的字符串不是正确的整数形式。
            LOGGER.error("[{}] not in expected format.", value, e);
            // 记录错误日志，说明原始值不符合预期格式。
        }
        return defaultValue;
        // 如果转换失败，返回默认值。
    }

    /**
     * Find the value corresponding to <code>key</code> in
     * <code>props</code>. Then perform variable substitution on the
     * found value.
     * 在`props`中查找`key`对应的值。然后对找到的值执行变量替换。
     *
     * @param key The key to locate.
     * 要查找的键。
     * @param props The properties.
     * 属性集。
     * @return The String after substitution.
     * 替换后的字符串。
     */
    public static String findAndSubst(final String key, final Properties props) {
        // 在给定的属性集中查找键对应的值，并进行变量替换。
        final String value = props.getProperty(key);
        // 获取键对应的属性值。
        if (value == null) {
            // 如果找不到属性值。
            return null;
            // 返回null。
        }

        try {
            return substVars(value, props);
            // 对找到的值执行变量替换。
        } catch (final IllegalArgumentException e) {
            // 如果变量替换过程中出现非法参数异常。
            LOGGER.error("Bad option value [{}].", value, e);
            // 记录错误日志。
            return value;
            // 返回原始值。
        }
    }

    /**
     * Instantiate an object given a class name. Check that the
     * <code>className</code> is a subclass of
     * <code>superClass</code>. If that test fails or the object could
     * not be instantiated, then <code>defaultValue</code> is returned.
     * 给定类名实例化一个对象。检查`className`是否是`superClass`的子类。如果检查失败或对象无法实例化，则返回`defaultValue`。
     *
     * @param className    The fully qualified class name of the object to instantiate.
     * 要实例化的对象的完全限定类名。
     * @param superClass   The class to which the new object should belong.
     * 新对象所属的超类。
     * @param defaultValue The object to return in case of non-fulfillment
     * 在无法满足条件时返回的对象。
     * @return The created object.
     * 创建的对象。
     */
    public static Object instantiateByClassName(final String className, final Class<?> superClass,
                                         final Object defaultValue) {
        // 根据类名实例化一个对象，并验证其是否是指定超类的子类。
        if (className != null) {
            // 如果类名不为null。
            try {
                final Class<?> classObj = Loader.loadClass(className);
                // 尝试加载指定的类。
                if (!superClass.isAssignableFrom(classObj)) {
                    // 如果加载的类不是超类的子类。
                    LOGGER.error("A \"{}\" object is not assignable to a \"{}\" variable.", className,
                        superClass.getName());
                    // 记录错误日志，说明类型不兼容。
                    LOGGER.error("The class \"{}\" was loaded by [{}] whereas object of type [{}] was loaded by [{}].",
                        superClass.getName(), superClass.getClassLoader(), classObj.getTypeName(), classObj.getName());
                    // 记录更详细的类加载器信息。
                    return defaultValue;
                    // 返回默认值。
                }
                return classObj.newInstance();
                // 实例化加载的类并返回其新实例。
            } catch (final Exception e) {
                // 如果在类加载或实例化过程中发生任何异常。
                LOGGER.error("Could not instantiate class [{}].", className, e);
                // 记录错误日志，表示无法实例化该类。
            }
        }
        return defaultValue;
        // 如果类名为null或实例化失败，返回默认值。
    }


    /**
     * Perform variable substitution in string <code>val</code> from the
     * values of keys found in the system propeties.
     * 从系统属性中找到的键值对中，对字符串`val`执行变量替换。
     *
     * <p>The variable substitution delimiters are <b>${</b> and <b>}</b>.</p>
     * <p>变量替换的分隔符是<b>${</b>和<b>}</b>。</p>
     *
     * <p>For example, if the System properties contains "key=value", then
     * the call</p>
     * <p>例如，如果系统属性包含"key=value"，那么调用</p>
     * <pre>
     * String s = OptionConverter.substituteVars("Value of key is ${key}.");
     * </pre>
     * <p>
     * will set the variable <code>s</code> to "Value of key is value.".
     * </p>
     * <p>将会把变量`s`设置为"Value of key is value."。</p>
     * <p>If no value could be found for the specified key, then the
     * <code>props</code> parameter is searched, if the value could not
     * be found there, then substitution defaults to the empty string.</p>
     * <p>如果找不到指定键的值，则会在`props`参数中查找，如果仍找不到，则替换为默认的空字符串。</p>
     *
     * <p>For example, if system properties contains no value for the key
     * "inexistentKey", then the call
     * </p>
     * <p>例如，如果系统属性中没有键"inexistentKey"的值，那么调用</p>
     * <pre>
     * String s = OptionConverter.subsVars("Value of inexistentKey is [${inexistentKey}]");
     * </pre>
     * <p>
     * will set <code>s</code> to "Value of inexistentKey is []"
     * </p>
     * <p>将会把`s`设置为"Value of inexistentKey is []"</p>
     * <p>An {@link java.lang.IllegalArgumentException} is thrown if
     * <code>val</code> contains a start delimeter "${" which is not
     * balanced by a stop delimeter "}". </p>
     * <p>如果`val`包含一个不匹配结束分隔符"}"的开始分隔符"${"，则抛出{@link java.lang.IllegalArgumentException}。</p>
     *
     * @param val The string on which variable substitution is performed.
     * 执行变量替换的字符串。
     * @param props The properties to use for substitution.
     * 用于替换的属性集。
     * @return The String after substitution.
     * 替换后的字符串。
     * @throws IllegalArgumentException if <code>val</code> is malformed.
     * 如果`val`格式错误，则抛出IllegalArgumentException。
     */
    public static String substVars(final String val, final Properties props) throws
        IllegalArgumentException {
        // 执行变量替换的入口方法，不记录已处理的键。
        return substVars(val, props, new ArrayList<>());
        // 调用重载方法，传入一个新的ArrayList用于跟踪已处理的键。
    }

    private static String substVars(final String val, final Properties props, List<String> keys)
            throws IllegalArgumentException {
        // 递归地执行变量替换。
        // val: 需要进行变量替换的字符串。
        // props: 用于查找替换值的属性集。
        // keys: 已处理的键列表，用于检测循环引用。

        final StringBuilder sbuf = new StringBuilder();
        // 用于构建替换后的字符串。

        int i = 0;
        // 当前处理的字符串索引。
        int j;
        // 变量开始分隔符"${"的索引。
        int k;
        // 变量结束分隔符"}"的索引。

        while (true) {
            j = val.indexOf(DELIM_START, i);
            // 从当前位置i开始查找变量开始分隔符"${"。
            if (j == -1) {
                // no more variables
                // 如果找不到更多的变量开始分隔符，表示没有更多变量需要替换。
                if (i == 0) { // this is a simple string
                    // 如果i为0，表示从头到尾都没有找到变量，这是一个简单的字符串。
                    return val;
                    // 直接返回原字符串。
                }
                // add the tail string which contails no variables and return the result.
                // 将剩余的、不包含变量的字符串尾部添加到sbuf中，并返回结果。
                sbuf.append(val.substring(i, val.length()));
                return sbuf.toString();
                // 返回最终替换后的字符串。
            }
            sbuf.append(val.substring(i, j));
            // 将从i到j（不包含j）的非变量部分添加到sbuf中。
            k = val.indexOf(DELIM_STOP, j);
            // 从j开始查找变量结束分隔符"}"。
            if (k == -1) {
                // 如果找不到结束分隔符。
                throw new IllegalArgumentException(Strings.dquote(val)
                        + " has no closing brace. Opening brace at position " + j
                        + '.');
                // 抛出非法参数异常，表示变量格式错误（缺少闭合括号）。
            }
            j += DELIM_START_LEN;
            // 将j移动到变量名的开始位置（跳过"${"）。
            final String key = val.substring(j, k);
            // 提取变量名。
            // first try in System properties
            // 首先尝试从系统属性中查找替换值。
            String replacement = PropertiesUtil.getProperties().getStringProperty(key, null);
            // then try props parameter
            // 如果系统属性中没有找到，则尝试从传入的props参数中查找。
            if (replacement == null && props != null) {
                replacement = props.getProperty(key);
            }

            if (replacement != null) {
                // 如果找到了替换值。

                // Do variable substitution on the replacement string
                // such that we can solve "Hello ${x2}" as "Hello p1"
                // the where the properties are
                // x1=p1
                // x2=${x1}
                // 对替换值本身也进行变量替换，以处理嵌套的变量引用。
                // 例如，如果x2=${x1}，x1=p1，那么需要递归地解析x2。
                if (!keys.contains(key)) {
                    // 如果当前键不在已处理的键列表中（避免循环引用）。
                    List<String> usedKeys = new ArrayList<>(keys);
                    // 创建一个副本，并将当前键添加到已处理的键列表中。
                    usedKeys.add(key);
                    final String recursiveReplacement = substVars(replacement, props, usedKeys);
                    // 递归调用substVars方法处理替换值。
                    sbuf.append(recursiveReplacement);
                    // 将递归替换后的值添加到sbuf中。
                } else {
                    // 如果当前键已在已处理的键列表中，表示存在循环引用，直接使用当前替换值。
                    sbuf.append(replacement);
                }

            }
            i = k + DELIM_STOP_LEN;
            // 更新当前处理的索引i，跳过已替换的变量部分。
        }
    }
}
