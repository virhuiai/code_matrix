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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Utility class for performing common reflective operations.
 * 用于执行常见反射操作的工具类。
 *
 * @since 2.1
 */
public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    /**
     * Indicates whether or not a {@link Member} is both public and is contained in a public class.
     * 判断一个 {@link Member} 是否是公共的，并且它所在的类也是公共的。
     *
     * @param <T> type of the object whose accessibility to test
     * 要测试其可访问性的对象类型
     * @param member the Member to check for public accessibility (must not be {@code null}).
     * 要检查公共可访问性的成员（不能为 {@code null}）。
     * @return {@code true} if {@code member} is public and contained in a public class.
     * 如果 {@code member} 是公共的且包含在公共类中，则返回 {@code true}。
     * @throws NullPointerException if {@code member} is {@code null}.
     * 如果 {@code member} 为 {@code null}，则抛出 NullPointerException。
     */
    public static <T extends AccessibleObject & Member> boolean isAccessible(final T member) {
        Objects.requireNonNull(member, "No member provided"); // 检查成员是否为空。
        // 检查成员的修饰符是否为公共的，并且其声明类的修饰符是否为公共的。
        return Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers());
    }

    /**
     * Makes a {@link Member} {@link AccessibleObject#isAccessible() accessible} if the member is not public.
     * 如果成员不是公共的，则使其变为 {@link AccessibleObject#isAccessible() accessible} 可访问。
     *
     * @param <T> type of the object to make accessible
     * 要使其可访问的对象类型
     * @param member the Member to make accessible (must not be {@code null}).
     * 要使其可访问的成员（不能为 {@code null}）。
     * @throws NullPointerException if {@code member} is {@code null}.
     * 如果 {@code member} 为 {@code null}，则抛出 NullPointerException。
     */
    public static <T extends AccessibleObject & Member> void makeAccessible(final T member) {
        // 如果成员不可访问且当前不可访问，则设置其为可访问。
        if (!isAccessible(member) && !member.isAccessible()) {
            member.setAccessible(true);
        }
    }

    /**
     * Makes a {@link Field} {@link AccessibleObject#isAccessible() accessible} if it is not public or if it is final.
     * 如果一个 {@link Field} 不是公共的或者它是 final 的，则使其变为 {@link AccessibleObject#isAccessible() accessible} 可访问。
     *
     * <p>Note that using this method to make a {@code final} field writable will most likely not work very well due to
     * compiler optimizations and the like.</p>
     * <p>注意：使用此方法使 {@code final} 字段可写，由于编译器优化等原因，很可能无法正常工作。</p>
     *
     * @param field the Field to make accessible (must not be {@code null}).
     * 要使其可访问的字段（不能为 {@code null}）。
     * @throws NullPointerException if {@code field} is {@code null}.
     * 如果 {@code field} 为 {@code null}，则抛出 NullPointerException。
     */
    public static void makeAccessible(final Field field) {
        Objects.requireNonNull(field, "No field provided"); // 检查字段是否为空。
        // 如果字段不可访问或者它是 final 的，并且当前不可访问，则设置其为可访问。
        if ((!isAccessible(field) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Gets the value of a {@link Field}, making it accessible if required.
     * 获取一个 {@link Field} 的值，如果需要的话使其可访问。
     *
     * @param field    the Field to obtain a value from (must not be {@code null}).
     * 要从中获取值的字段（不能为 {@code null}）。
     * @param instance the instance to obtain the field value from or {@code null} only if the field is static.
     * 要从中获取字段值的实例，如果字段是静态的则可以为 {@code null}。
     * @return the value stored by the field.
     * 字段存储的值。
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code instance} is {@code null} but
     *                              {@code field} is not {@code static}.
     * 如果 {@code field} 为 {@code null}，或者 {@code instance} 为 {@code null} 但
     * {@code field} 不是 {@code static}，则抛出 NullPointerException。
     * @see Field#get(Object)
     */
    public static Object getFieldValue(final Field field, final Object instance) {
        makeAccessible(field); // 使字段可访问。
        // 如果字段不是静态的，则实例不能为 null。
        if (!Modifier.isStatic(field.getModifiers())) {
            Objects.requireNonNull(instance, "No instance given for non-static field");
        }
        try {
            return field.get(instance); // 获取字段的值。
        } catch (final IllegalAccessException e) {
            throw new UnsupportedOperationException(e); // 包装并抛出异常。
        }
    }

    /**
     * Gets the value of a static {@link Field}, making it accessible if required.
     * 获取一个静态 {@link Field} 的值，如果需要的话使其可访问。
     *
     * @param field the Field to obtain a value from (must not be {@code null}).
     * 要从中获取值的字段（不能为 {@code null}）。
     * @return the value stored by the static field.
     * 静态字段存储的值。
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code field} is not {@code static}.
     * 如果 {@code field} 为 {@code null}，或者 {@code field} 不是 {@code static}，则抛出 NullPointerException。
     * @see Field#get(Object)
     */
    public static Object getStaticFieldValue(final Field field) {
        return getFieldValue(field, null); // 调用 getFieldValue 方法获取静态字段值，实例为 null。
    }

    /**
     * Sets the value of a {@link Field}, making it accessible if required.
     * 设置一个 {@link Field} 的值，如果需要的话使其可访问。
     *
     * @param field    the Field to write a value to (must not be {@code null}).
     * 要写入值的字段（不能为 {@code null}）。
     * @param instance the instance to write the value to or {@code null} only if the field is static.
     * 要写入值的实例，如果字段是静态的则可以为 {@code null}。
     * @param value    the (possibly wrapped) value to write to the field.
     * 要写入字段的值（可能已包装）。
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code instance} is {@code null} but
     *                              {@code field} is not {@code static}.
     * 如果 {@code field} 为 {@code null}，或者 {@code instance} 为 {@code null} 但
     * {@code field} 不是 {@code static}，则抛出 NullPointerException。
     * @see Field#set(Object, Object)
     */
    public static void setFieldValue(final Field field, final Object instance, final Object value) {
        makeAccessible(field); // 使字段可访问。
        // 如果字段不是静态的，则实例不能为 null。
        if (!Modifier.isStatic(field.getModifiers())) {
            Objects.requireNonNull(instance, "No instance given for non-static field");
        }
        try {
            field.set(instance, value); // 设置字段的值。
        } catch (final IllegalAccessException e) {
            throw new UnsupportedOperationException(e); // 包装并抛出异常。
        }
    }

    /**
     * Sets the value of a static {@link Field}, making it accessible if required.
     * 设置一个静态 {@link Field} 的值，如果需要的话使其可访问。
     *
     * @param field the Field to write a value to (must not be {@code null}).
     * 要写入值的字段（不能为 {@code null}）。
     * @param value the (possibly wrapped) value to write to the field.
     * 要写入字段的值（可能已包装）。
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code field} is not {@code static}.
     * 如果 {@code field} 为 {@code null}，或者 {@code field} 不是 {@code static}，则抛出 NullPointerException。
     * @see Field#set(Object, Object)
     */
    public static void setStaticFieldValue(final Field field, final Object value) {
        setFieldValue(field, null, value); // 调用 setFieldValue 方法设置静态字段值，实例为 null。
    }

    /**
     * Gets the default (no-arg) constructor for a given class.
     * 获取给定类的默认（无参数）构造函数。
     *
     * @param clazz the class to find a constructor for
     * 要查找构造函数的类
     * @param <T>   the type made by the constructor
     * 构造函数创建的类型
     * @return the default constructor for the given class
     * 给定类的默认构造函数
     * @throws IllegalStateException if no default constructor can be found
     * 如果没有找到默认构造函数，则抛出 IllegalStateException
     */
    public static <T> Constructor<T> getDefaultConstructor(final Class<T> clazz) {
        Objects.requireNonNull(clazz, "No class provided"); // 检查类是否为空。
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor(); // 获取声明的无参数构造函数。
            makeAccessible(constructor); // 使构造函数可访问。
            return constructor; // 返回构造函数。
        } catch (final NoSuchMethodException ignored) { // 如果没有找到声明的构造函数，则尝试获取公共构造函数。
            try {
                final Constructor<T> constructor = clazz.getConstructor(); // 获取公共无参数构造函数。
                makeAccessible(constructor); // 使构造函数可访问。
                return constructor; // 返回构造函数。
            } catch (final NoSuchMethodException e) {
                throw new IllegalStateException(e); // 如果仍然没有找到，则抛出异常。
            }
        }
    }

    /**
     * Constructs a new {@code T} object using the default constructor of its class. Any exceptions thrown by the
     * constructor will be rethrown by this method, possibly wrapped in an
     * {@link java.lang.reflect.UndeclaredThrowableException}.
     * 使用类的默认构造函数构造一个新的 {@code T} 对象。构造函数抛出的任何异常都将由本方法重新抛出，可能包装在
     * {@link java.lang.reflect.UndeclaredThrowableException} 中。
     *
     * @param clazz the class to use for instantiation.
     * 用于实例化的类。
     * @param <T>   the type of the object to construct.
     * 要构造的对象的类型。
     * @return a new instance of T made from its default constructor.
     * 通过其默认构造函数创建的 T 的新实例。
     * @throws IllegalArgumentException if the given class is abstract, an interface, an array class, a primitive type,
     *                                  or void
     * 如果给定类是抽象类、接口、数组类、原始类型或 void，则抛出 IllegalArgumentException。
     * @throws IllegalStateException    if access is denied to the constructor, or there are no default constructors
     * 如果拒绝访问构造函数，或者没有默认构造函数，则抛出 IllegalStateException。
     * @throws InternalException        wrapper of the underlying exception if checked
     * 如果底层异常是受检异常，则包装为 InternalException。
     */
    public static <T> T instantiate(final Class<T> clazz) {
        Objects.requireNonNull(clazz, "No class provided"); // 检查类是否为空。
        final Constructor<T> constructor = getDefaultConstructor(clazz); // 获取默认构造函数。
        try {
            return constructor.newInstance(); // 调用构造函数创建新实例。
        } catch (final LinkageError | InstantiationException e) {
            // LOG4J2-1051
            // On platforms like Google App Engine and Android, some JRE classes are not supported: JMX, JNDI, etc.
            // 在 Google App Engine 和 Android 等平台上，某些 JRE 类（如 JMX、JNDI 等）不受支持。
            throw new IllegalArgumentException(e); // 包装并抛出非法参数异常。
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e); // 包装并抛出非法状态异常。
        } catch (final InvocationTargetException e) {
            Throwables.rethrow(e.getCause()); // 重新抛出构造函数内部的实际异常。
            throw new InternalError("Unreachable"); // 此行代码理论上不可达。
        }
    }
}
