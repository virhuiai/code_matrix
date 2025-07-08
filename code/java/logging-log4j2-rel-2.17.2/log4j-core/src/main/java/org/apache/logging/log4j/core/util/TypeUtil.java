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

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for working with Java {@link Type}s and derivatives. This class is adapted heavily from the
 * <a href="http://projects.spring.io/spring-framework/">Spring Framework</a>, specifically the
 * <a href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/util/TypeUtils.html">TypeUtils</a>
 * class.
 *
 * 用于处理 Java {@link Type} 及其派生类型的工具类。
 * 这个类大量借鉴了 <a href="http://projects.spring.io/spring-framework/">Spring Framework</a>，
 * 特别是其中的 <a href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/util/TypeUtils.html">TypeUtils</a> 类。
 *
 * @see java.lang.reflect.Type
 * @see java.lang.reflect.GenericArrayType
 * @see java.lang.reflect.ParameterizedType
 * @see java.lang.reflect.WildcardType
 * @see java.lang.Class
 * @since 2.1
 */
public final class TypeUtil {

    private TypeUtil() {
    }

    /**
     * Gets all declared fields for the given class (including superclasses).
     * 获取给定类的所有声明字段（包括超类）。
     *
     * @param cls the class to examine
     * 要检查的类。
     * @return all declared fields for the given class (including superclasses).
     * 给定类的所有声明字段（包括超类）。
     * @see Class#getDeclaredFields()
     */
    public static List<Field> getAllDeclaredFields(Class<?> cls) {
        final List<Field> fields = new ArrayList<>();
        // 定义一个用于存储字段的列表，初始化为 ArrayList。
        while (cls != null) {
            // 循环遍历类的继承链，直到达到 Object 类。
            Collections.addAll(fields, cls.getDeclaredFields());
            // 将当前类声明的所有字段添加到列表中。
            cls = cls.getSuperclass();
            // 将当前类设置为其超类，以便在下一次迭代中检查超类的字段。
        }
        return fields;
        // 返回包含所有声明字段的列表。
    }
    /**
     * Indicates if two {@link Type}s are assignment compatible.
     * 判断两个 {@link Type} 是否赋值兼容。
     *
     * @param lhs the left hand side to check assignability to
     * 要检查可赋值性的左侧类型。
     * @param rhs the right hand side to check assignability from
     * 要检查可赋值性的右侧类型。
     * @return {@code true} if it is legal to assign a variable of type {@code rhs} to a variable of type {@code lhs}
     * 如果可以将 {@code rhs} 类型的变量赋值给 {@code lhs} 类型的变量，则返回 {@code true}。
     * @see Class#isAssignableFrom(Class)
     */
    public static boolean isAssignable(final Type lhs, final Type rhs) {
        Objects.requireNonNull(lhs, "No left hand side type provided");
        // 检查左侧类型是否为 null，如果为 null 则抛出 NullPointerException。
        Objects.requireNonNull(rhs, "No right hand side type provided");
        // 检查右侧类型是否为 null，如果为 null 则抛出 NullPointerException。
        if (lhs.equals(rhs)) {
            // 如果两个类型相同，则它们是赋值兼容的。
            return true;
        }
        if (Object.class.equals(lhs)) {
            // everything is assignable to Object
            // 如果左侧类型是 Object，则所有类型都可以赋值给 Object。
            return true;
        }
        // raw type on left
        // 左侧是原始类型
        if (lhs instanceof Class<?>) {
            // 如果左侧类型是 Class（原始类型）。
            final Class<?> lhsClass = (Class<?>) lhs;
            // 将左侧类型转换为 Class。
            if (rhs instanceof Class<?>) {
                // no generics involved
                // 如果右侧类型也是 Class（不涉及泛型）。
                final Class<?> rhsClass = (Class<?>) rhs;
                // 将右侧类型转换为 Class。
                return lhsClass.isAssignableFrom(rhsClass);
                // 使用 Class.isAssignableFrom() 方法检查赋值兼容性。
            }
            if (rhs instanceof ParameterizedType) {
                // check to see if the parameterized type has the same raw type as the lhs; this is legal
                // 如果右侧类型是 ParameterizedType（参数化类型），检查其原始类型是否与左侧原始类型相同，这是合法的。
                final Type rhsRawType = ((ParameterizedType) rhs).getRawType();
                // 获取右侧参数化类型的原始类型。
                if (rhsRawType instanceof Class<?>) {
                    // 如果右侧参数化类型的原始类型是 Class。
                    return lhsClass.isAssignableFrom((Class<?>) rhsRawType);
                    // 检查左侧原始类型是否可以从右侧参数化类型的原始类型赋值。
                }
            }
            if (lhsClass.isArray() && rhs instanceof GenericArrayType) {
                // check for compatible array component types
                // 如果左侧类型是数组且右侧是 GenericArrayType（泛型数组类型），检查兼容的数组组件类型。
                return isAssignable(lhsClass.getComponentType(), ((GenericArrayType) rhs).getGenericComponentType());
                // 递归检查数组组件类型的赋值兼容性。
            }
        }
        // parameterized type on left
        // 左侧是参数化类型
        if (lhs instanceof ParameterizedType) {
            // 如果左侧类型是 ParameterizedType。
            final ParameterizedType lhsType = (ParameterizedType) lhs;
            // 将左侧类型转换为 ParameterizedType。
            if (rhs instanceof Class<?>) {
                // 如果右侧类型是 Class。
                final Type lhsRawType = lhsType.getRawType();
                // 获取左侧参数化类型的原始类型。
                if (lhsRawType instanceof Class<?>) {
                    // 如果左侧参数化类型的原始类型是 Class。
                    return ((Class<?>) lhsRawType).isAssignableFrom((Class<?>) rhs);
                    // 检查左侧参数化类型的原始类型是否可以从右侧 Class 类型赋值。
                }
            } else if (rhs instanceof ParameterizedType) {
                // 如果右侧类型也是 ParameterizedType。
                final ParameterizedType rhsType = (ParameterizedType) rhs;
                // 将右侧类型转换为 ParameterizedType。
                return isParameterizedAssignable(lhsType, rhsType);
                // 调用辅助方法检查参数化类型的赋值兼容性。
            }
        }
        // generic array type on left
        // 左侧是泛型数组类型
        if (lhs instanceof GenericArrayType) {
            // 如果左侧类型是 GenericArrayType。
            final Type lhsComponentType = ((GenericArrayType) lhs).getGenericComponentType();
            // 获取左侧泛型数组的组件类型。
            if (rhs instanceof Class<?>) {
                // raw type on right
                // 如果右侧类型是 Class（原始类型）。
                final Class<?> rhsClass = (Class<?>) rhs;
                // 将右侧类型转换为 Class。
                if (rhsClass.isArray()) {
                    // 如果右侧 Class 类型是数组。
                    return isAssignable(lhsComponentType, rhsClass.getComponentType());
                    // 递归检查泛型数组组件类型与原始数组组件类型的赋值兼容性。
                }
            } else if (rhs instanceof GenericArrayType) {
                // 如果右侧类型也是 GenericArrayType。
                return isAssignable(lhsComponentType, ((GenericArrayType) rhs).getGenericComponentType());
                // 递归检查两个泛型数组组件类型的赋值兼容性。
            }
        }
        // wildcard type on left
        // 左侧是通配符类型
        if (lhs instanceof WildcardType) {
            // 如果左侧类型是 WildcardType。
            return isWildcardAssignable((WildcardType) lhs, rhs);
            // 调用辅助方法检查通配符类型的赋值兼容性。
        }
        // strange...
        // 异常情况，返回 false。
        return false;
    }

    private static boolean isParameterizedAssignable(final ParameterizedType lhs, final ParameterizedType rhs) {
        if (lhs.equals(rhs)) {
            // that was easy
            // 如果两个参数化类型相同，则它们是赋值兼容的。
            return true;
        }
        final Type[] lhsTypeArguments = lhs.getActualTypeArguments();
        // 获取左侧参数化类型的实际类型参数。
        final Type[] rhsTypeArguments = rhs.getActualTypeArguments();
        // 获取右侧参数化类型的实际类型参数。
        final int size = lhsTypeArguments.length;
        // 获取类型参数的数量。
        if (rhsTypeArguments.length != size) {
            // clearly incompatible types
            // 如果类型参数的数量不同，则它们显然不兼容。
            return false;
        }
        for (int i = 0; i < size; i++) {
            // verify all type arguments are assignable
            // 验证所有类型参数都是可赋值的。
            final Type lhsArgument = lhsTypeArguments[i];
            // 获取左侧的当前类型参数。
            final Type rhsArgument = rhsTypeArguments[i];
            // 获取右侧的当前类型参数。
            if (!lhsArgument.equals(rhsArgument) &&
                // 如果类型参数不相等。
                !(lhsArgument instanceof WildcardType &&
                    isWildcardAssignable((WildcardType) lhsArgument, rhsArgument))) {
                // 并且左侧类型参数不是通配符类型，或者虽然是通配符类型但与右侧类型参数不兼容，则返回 false。
                return false;
            }
        }
        return true;
        // 如果所有类型参数都兼容，则返回 true。
    }

    private static boolean isWildcardAssignable(final WildcardType lhs, final Type rhs) {
        final Type[] lhsUpperBounds = getEffectiveUpperBounds(lhs);
        // 获取左侧通配符类型的有效上限。
        final Type[] lhsLowerBounds = getEffectiveLowerBounds(lhs);
        // 获取左侧通配符类型的有效下限。
        if (rhs instanceof WildcardType) {
            // oh boy, this scenario requires checking a lot of assignability!
            // 如果右侧类型也是通配符类型，这种情况下需要检查大量的赋值兼容性。
            final WildcardType rhsType = (WildcardType) rhs;
            // 将右侧类型转换为 WildcardType。
            final Type[] rhsUpperBounds = getEffectiveUpperBounds(rhsType);
            // 获取右侧通配符类型的有效上限。
            final Type[] rhsLowerBounds = getEffectiveLowerBounds(rhsType);
            // 获取右侧通配符类型的有效下限。
            for (final Type lhsUpperBound : lhsUpperBounds) {
                // 遍历左侧通配符的每一个上限。
                for (final Type rhsUpperBound : rhsUpperBounds) {
                    // 遍历右侧通配符的每一个上限。
                    if (!isBoundAssignable(lhsUpperBound, rhsUpperBound)) {
                        // 如果左侧上限不能从右侧上限赋值，则返回 false。
                        return false;
                    }
                }
                for (final Type rhsLowerBound : rhsLowerBounds) {
                    // 遍历右侧通配符的每一个下限。
                    if (!isBoundAssignable(lhsUpperBound, rhsLowerBound)) {
                        // 如果左侧上限不能从右侧下限赋值，则返回 false。
                        return false;
                    }
                }
            }
            for (final Type lhsLowerBound : lhsLowerBounds) {
                // 遍历左侧通配符的每一个下限。
                for (final Type rhsUpperBound : rhsUpperBounds) {
                    // 遍历右侧通配符的每一个上限。
                    if (!isBoundAssignable(rhsUpperBound, lhsLowerBound)) {
                        // 如果右侧上限不能从左侧下限赋值，则返回 false。
                        return false;
                    }
                }
                for (final Type rhsLowerBound : rhsLowerBounds) {
                    // 遍历右侧通配符的每一个下限。
                    if (!isBoundAssignable(rhsLowerBound, lhsLowerBound)) {
                        // 如果右侧下限不能从左侧下限赋值，则返回 false。
                        return false;
                    }
                }
            }
        } else {
            // phew, far less bounds to check
            // 否则（右侧不是通配符类型），需要检查的边界少得多。
            for (final Type lhsUpperBound : lhsUpperBounds) {
                // 遍历左侧通配符的每一个上限。
                if (!isBoundAssignable(lhsUpperBound, rhs)) {
                    // 如果左侧上限不能从右侧类型赋值，则返回 false。
                    return false;
                }
            }
            for (final Type lhsLowerBound : lhsLowerBounds) {
                // 遍历左侧通配符的每一个下限。
                if (!isBoundAssignable(lhsLowerBound, rhs)) {
                    // 如果左侧下限不能从右侧类型赋值，则返回 false。
                    return false;
                }
            }
        }
        return true;
        // 如果所有边界都兼容，则返回 true。
    }

    private static Type[] getEffectiveUpperBounds(final WildcardType type) {
        final Type[] upperBounds = type.getUpperBounds();
        // 获取通配符的原始上限。
        return upperBounds.length == 0 ? new Type[]{Object.class} : upperBounds;
        // 如果没有指定上限，则默认上限为 Object.class；否则返回原始上限。
    }

    private static Type[] getEffectiveLowerBounds(final WildcardType type) {
        final Type[] lowerBounds = type.getLowerBounds();
        // 获取通配符的原始下限。
        return lowerBounds.length == 0 ? new Type[]{null} : lowerBounds;
        // 如果没有指定下限，则默认下限为 null；否则返回原始下限。
    }

    private static boolean isBoundAssignable(final Type lhs, final Type rhs) {
        return (rhs == null) || ((lhs != null) && isAssignable(lhs, rhs));
        // 检查边界是否可赋值。如果右侧为 null，则认为可赋值；否则，如果左侧不为 null 且左侧可以从右侧赋值，则为可赋值。
    }
}
