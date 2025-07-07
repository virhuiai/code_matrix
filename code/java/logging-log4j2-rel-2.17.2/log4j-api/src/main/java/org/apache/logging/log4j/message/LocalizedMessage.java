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
package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.status.StatusLogger;

/**
 * Provides some level of compatibility with Log4j 1.x and convenience but is not the recommended way to Localize
 * messages.
 * 提供与 Log4j 1.x 一定程度的兼容性和便利性，但这不是推荐的消息本地化方式。
 * <p>
 * The recommended way to localize messages is to log a message id. Log events should then be recorded without
 * formatting into a data store. The application that is used to read the events and display them to the user can then
 * localize and format the messages for the end user.
 * 推荐的消息本地化方式是记录消息ID。日志事件应该不经过格式化地记录到数据存储中。
 * 然后，用于读取事件并向用户显示事件的应用程序可以为最终用户本地化和格式化消息。
 * </p>
 */
public class LocalizedMessage implements Message, LoggerNameAwareMessage {
    private static final long serialVersionUID = 3893703791567290742L;

    private String baseName;
    // 资源包的基本名称，用于定位资源文件

    // ResourceBundle is not Serializable.
    // ResourceBundle 是不可序列化的。
    private transient ResourceBundle resourceBundle;
    // 运行时加载的资源包实例，用于获取本地化字符串。由于其不可序列化特性，所以用 transient 关键字修饰。

    private final Locale locale;
    // 消息的区域设置，决定了使用哪个语言的资源包

    private transient StatusLogger logger = StatusLogger.getLogger();
    // 状态日志记录器，用于在出现问题时记录调试信息。由于其不可序列化特性，所以用 transient 关键字修饰。

    private String loggerName;
    // 记录器名称，用于在没有提供 baseName 时查找资源包

    private String key;
    // 消息的键，用于在资源包中查找对应的本地化字符串

    private String[] stringArgs;
    // 序列化时存储的字符串形式的参数数组

    private transient Object[] argArray;
    // 运行时存储的原始参数数组。由于其可能包含不可序列化的对象，所以用 transient 关键字修饰。

    private String formattedMessage;
    // 已经格式化好的消息字符串

    private transient Throwable throwable;
    // 与消息关联的异常对象。由于其可能包含不可序列化的对象，所以用 transient 关键字修饰。

    /**
     * Constructor with message pattern and arguments.
     * 带有消息模式和参数的构造函数。
     *
     * @param messagePattern the message pattern that to be checked for placeholders.
     * 需要检查占位符的消息模式。
     * @param arguments the argument array to be converted.
     * 待转换的参数数组。
     */
    public LocalizedMessage(final String messagePattern, final Object[] arguments) {
        // 调用另一个构造函数，其中 resourceBundle 和 locale 为 null，messagePattern 作为 key。
        this((ResourceBundle) null, (Locale) null, messagePattern, arguments);
    }

    public LocalizedMessage(final String baseName, final String key, final Object[] arguments) {
        // 调用另一个构造函数，其中 locale 为 null。
        this(baseName, (Locale) null, key, arguments);
    }

    public LocalizedMessage(final ResourceBundle bundle, final String key, final Object[] arguments) {
        // 调用另一个构造函数，其中 locale 为 null。
        this(bundle, (Locale) null, key, arguments);
    }

    public LocalizedMessage(final String baseName, final Locale locale, final String key, final Object[] arguments) {
        // 构造函数：初始化消息的键、参数数组、异常、资源包基本名称、资源包实例和区域设置。
        this.key = key; // 设置消息的键
        this.argArray = arguments; // 设置参数数组
        this.throwable = null; // 初始化异常为 null
        this.baseName = baseName; // 设置资源包基本名称
        this.resourceBundle = null; // 初始化资源包实例为 null
        this.locale = locale; // 设置区域设置
    }

    public LocalizedMessage(final ResourceBundle bundle, final Locale locale, final String key,
            final Object[] arguments) {
        // 构造函数：初始化消息的键、参数数组、异常、资源包基本名称、资源包实例和区域设置。
        this.key = key; // 设置消息的键
        this.argArray = arguments; // 设置参数数组
        this.throwable = null; // 初始化异常为 null
        this.baseName = null; // 初始化资源包基本名称为 null
        this.resourceBundle = bundle; // 设置资源包实例
        this.locale = locale; // 设置区域设置
    }

    public LocalizedMessage(final Locale locale, final String key, final Object[] arguments) {
        // 调用另一个构造函数，其中 resourceBundle 为 null。
        this((ResourceBundle) null, locale, key, arguments);
    }

    public LocalizedMessage(final String messagePattern, final Object arg) {
        // 调用接受单个参数的构造函数，将单个参数包装成一个 Object 数组。
        this((ResourceBundle) null, (Locale) null, messagePattern, new Object[] {arg});
    }

    public LocalizedMessage(final String baseName, final String key, final Object arg) {
        // 调用接受单个参数的构造函数，将单个参数包装成一个 Object 数组。
        this(baseName, (Locale) null, key, new Object[] {arg});
    }

    /**
     * @since 2.8
     */
    public LocalizedMessage(final ResourceBundle bundle, final String key) {
        // 构造函数：只有资源包和键，参数为空数组。
        this(bundle, (Locale) null, key, new Object[] {});
    }

    public LocalizedMessage(final ResourceBundle bundle, final String key, final Object arg) {
        // 调用接受单个参数的构造函数，将单个参数包装成一个 Object 数组。
        this(bundle, (Locale) null, key, new Object[] {arg});
    }

    public LocalizedMessage(final String baseName, final Locale locale, final String key, final Object arg) {
        // 调用接受单个参数的构造函数，将单个参数包装成一个 Object 数组。
        this(baseName, locale, key, new Object[] {arg});
    }

    public LocalizedMessage(final ResourceBundle bundle, final Locale locale, final String key, final Object arg) {
        // 调用接受单个参数的构造函数，将单个参数包装成一个 Object 数组。
        this(bundle, locale, key, new Object[] {arg});
    }

    public LocalizedMessage(final Locale locale, final String key, final Object arg) {
        // 调用接受单个参数的构造函数，将单个参数包装成一个 Object 数组。
        this((ResourceBundle) null, locale, key, new Object[] {arg});
    }

    public LocalizedMessage(final String messagePattern, final Object arg1, final Object arg2) {
        // 调用接受两个参数的构造函数，将两个参数包装成一个 Object 数组。
        this((ResourceBundle) null, (Locale) null, messagePattern, new Object[] {arg1, arg2});
    }

    public LocalizedMessage(final String baseName, final String key, final Object arg1, final Object arg2) {
        // 调用接受两个参数的构造函数，将两个参数包装成一个 Object 数组。
        this(baseName, (Locale) null, key, new Object[] {arg1, arg2});
    }

    public LocalizedMessage(final ResourceBundle bundle, final String key, final Object arg1, final Object arg2) {
        // 调用接受两个参数的构造函数，将两个参数包装成一个 Object 数组。
        this(bundle, (Locale) null, key, new Object[] {arg1, arg2});
    }

    public LocalizedMessage(final String baseName, final Locale locale, final String key, final Object arg1,
            final Object arg2) {
        // 调用接受两个参数的构造函数，将两个参数包装成一个 Object 数组。
        this(baseName, locale, key, new Object[] {arg1, arg2});
    }

    public LocalizedMessage(final ResourceBundle bundle, final Locale locale, final String key, final Object arg1,
            final Object arg2) {
        // 调用接受两个参数的构造函数，将两个参数包装成一个 Object 数组。
        this(bundle, locale, key, new Object[] {arg1, arg2});
    }

    public LocalizedMessage(final Locale locale, final String key, final Object arg1, final Object arg2) {
        // 调用接受两个参数的构造函数，将两个参数包装成一个 Object 数组。
        this((ResourceBundle) null, locale, key, new Object[] {arg1, arg2});
    }

    /**
     * Set the name of the Logger.
     * 设置记录器名称。
     *
     * @param name The name of the Logger.
     * 记录器名称。
     */
    @Override
    public void setLoggerName(final String name) {
        this.loggerName = name; // 设置记录器名称
    }

    /**
     * Returns the name of the Logger.
     * 返回记录器名称。
     *
     * @return the name of the Logger.
     * 记录器名称。
     */
    @Override
    public String getLoggerName() {
        return this.loggerName; // 返回记录器名称
    }

    /**
     * Returns the formatted message after looking up the format in the resource bundle.
     * 在资源包中查找格式后返回格式化后的消息。
     *
     * @return The formatted message String.
     * 格式化后的消息字符串。
     */
    @Override
    public String getFormattedMessage() {
        if (formattedMessage != null) {
            // 如果消息已经格式化，直接返回。
            return formattedMessage;
        }
        ResourceBundle bundle = this.resourceBundle;
        // 获取当前或初始化的资源包。
        if (bundle == null) {
            // 如果资源包为 null，则尝试加载。
            if (baseName != null) {
                // 如果指定了 baseName，则根据 baseName 和 locale 加载资源包，不进行循环查找。
                bundle = getResourceBundle(baseName, locale, false);
            } else {
                // 如果没有指定 baseName，则根据 loggerName 和 locale 加载资源包，进行循环查找。
                bundle = getResourceBundle(loggerName, locale, true);
            }
        }
        final String myKey = getFormat(); // 获取消息的键
        // 从资源包中获取消息模式，如果资源包为 null 或者不包含该键，则直接使用键作为消息模式。
        final String msgPattern = (bundle == null || !bundle.containsKey(myKey)) ? myKey : bundle.getString(myKey);
        // 获取参数数组，如果 argArray 为 null 则使用 stringArgs。
        final Object[] array = argArray == null ? stringArgs : argArray;
        // 创建 FormattedMessage 实例进行消息格式化。
        final FormattedMessage msg = new FormattedMessage(msgPattern, array);
        formattedMessage = msg.getFormattedMessage(); // 获取格式化后的消息
        throwable = msg.getThrowable(); // 获取关联的异常
        return formattedMessage; // 返回格式化后的消息
    }

    @Override
    public String getFormat() {
        return key; // 返回消息的键
    }

    @Override
    public Object[] getParameters() {
        if (argArray != null) {
            // 如果原始参数数组不为 null，则返回原始参数数组。
            return argArray;
        }
        // 否则返回序列化时存储的字符串形式的参数数组。
        return stringArgs;
    }

    @Override
    public Throwable getThrowable() {
        return throwable; // 返回关联的异常
    }

    /**
     * Override this to use a ResourceBundle.Control in Java 6
     * 重写此方法以在 Java 6 中使用 ResourceBundle.Control。
     *
     * @param rbBaseName The base name of the resource bundle, a fully qualified class name.
     * 资源包的基本名称，一个完全限定的类名。
     * @param resourceBundleLocale The locale to use when formatting the message.
     * 格式化消息时使用的区域设置。
     * @param loop If true the key will be treated as a package or class name and a resource bundle will be located
     *            based on all or part of the package name. If false the key is expected to be the exact bundle id.
     * 如果为 true，则键将被视为包名或类名，并将根据包名的全部或部分定位资源包。
     * 如果为 false，则键应该就是精确的 bundle ID。
     * @return The ResourceBundle.
     * 资源包。
     */
    protected ResourceBundle getResourceBundle(final String rbBaseName, final Locale resourceBundleLocale,
            final boolean loop) {
        ResourceBundle rb = null; // 初始化资源包为 null

        if (rbBaseName == null) {
            // 如果资源包基本名称为 null，则直接返回 null。
            return null;
        }
        try {
            if (resourceBundleLocale != null) {
                // 如果指定了区域设置，则根据基本名称和区域设置获取资源包。
                rb = ResourceBundle.getBundle(rbBaseName, resourceBundleLocale);
            } else {
                // 如果没有指定区域设置，则根据基本名称获取资源包。
                rb = ResourceBundle.getBundle(rbBaseName);
            }
        } catch (final MissingResourceException ex) {
            // 捕获资源丢失异常。
            if (!loop) {
                // 如果不进行循环查找，则记录调试信息并返回 null。
                logger.debug("Unable to locate ResourceBundle " + rbBaseName);
                return null;
            }
        }

        String substr = rbBaseName; // 用于逐步缩短基本名称以进行查找
        int i;
        // 当资源包为 null 且基本名称中包含点号时进行循环查找。
        while (rb == null && (i = substr.lastIndexOf('.')) > 0) {
            substr = substr.substring(0, i); // 截取基本名称的父级部分
            try {
                if (resourceBundleLocale != null) {
                    // 尝试根据缩短后的名称和区域设置获取资源包。
                    rb = ResourceBundle.getBundle(substr, resourceBundleLocale);
                } else {
                    // 尝试根据缩短后的名称获取资源包。
                    rb = ResourceBundle.getBundle(substr);
                }
            } catch (final MissingResourceException ex) {
                // 捕获资源丢失异常，记录调试信息。
                logger.debug("Unable to locate ResourceBundle " + substr);
            }
        }
        return rb; // 返回找到的资源包
    }

    @Override
    public String toString() {
        return getFormattedMessage(); // 返回格式化后的消息
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // 写入对象的非 transient 字段
        getFormattedMessage(); // 确保消息已格式化，以便序列化 formattedMessage
        out.writeUTF(formattedMessage); // 写入格式化后的消息
        out.writeUTF(key); // 写入消息的键
        out.writeUTF(baseName); // 写入资源包的基本名称
        out.writeInt(argArray.length); // 写入参数数组的长度
        stringArgs = new String[argArray.length]; // 初始化字符串参数数组
        int i = 0;
        // 将原始参数数组中的每个对象转换为字符串并存储到 stringArgs 中
        for (final Object obj : argArray) {
            stringArgs[i] = obj.toString();
            ++i;
        }
        out.writeObject(stringArgs); // 写入字符串参数数组
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // 读取对象的非 transient 字段
        formattedMessage = in.readUTF(); // 读取格式化后的消息
        key = in.readUTF(); // 读取消息的键
        baseName = in.readUTF(); // 读取资源包的基本名称
        in.readInt(); // 读取参数数组的长度（但此处不使用）
        stringArgs = (String[]) in.readObject(); // 读取字符串参数数组
        logger = StatusLogger.getLogger(); // 重新获取 StatusLogger 实例
        resourceBundle = null; // 将 resourceBundle 设置为 null，因为它不可序列化
        argArray = null; // 将 argArray 设置为 null，因为原始对象在序列化时未保存
    }
}
