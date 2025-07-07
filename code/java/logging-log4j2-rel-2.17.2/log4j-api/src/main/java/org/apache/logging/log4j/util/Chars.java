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

/**
 * <em>Consider this class private.</em>
 */
// 这是一个私有类，建议仅在内部使用，不对外暴露
public final class Chars {

    /** Carriage Return. */
    // 定义回车符（CR），ASCII值为13，表示换行前的回车
    public static final char CR = '\r';

    /** Double Quote. */
    // 定义双引号（DQUOTE），用于表示字符串的边界
    public static final char DQUOTE = '\"';

    /** Equals '='. */
    // 定义等号（EQ），通常用于键值对分隔
    public static final char EQ = '=';

    /** Line Feed. */
    // 定义换行符（LF），ASCII值为10，表示换行
    public static final char LF = '\n';

    /** NUL. */
    // 定义空字符（NUL），ASCII值为0，通常用于字符串终止
	public static final char NUL = 0;

	/** Single Quote [']. */
    // 定义单引号（QUOTE），用于表示字符或特定字符串的边界
    public static final char QUOTE = '\'';

    /** Space. */
    // 定义空格（SPACE），用于字符间分隔
    public static final char SPACE = ' ';

    /** Tab. */
    // 定义制表符（TAB），用于文本对齐
    public static final char TAB = '\t';

    /**
     * Converts a digit into an upper-case hexadecimal character or the null character if invalid.
     *
     * @param digit a number 0 - 15
     * @return the hex character for that digit or '\0' if invalid
     */
    // 将数字（0-15）转换为大写十六进制字符，若输入无效则返回空字符
    // 方法功能：将输入的数字转换为对应的十六进制字符（大写），如0->'0'，10->'A'
    // 参数：
    //   - digit：输入的整数，范围为0到15，表示待转换的十六进制位
    // 返回值：
    //   - 返回对应的十六进制字符（大写），或空字符'\0'（若输入无效）
    // 执行流程：
    //   1. 检查输入数字是否在有效范围（0-15）
    //   2. 若有效，调用辅助方法getNumericalDigit或getUpperCaseAlphaDigit进行转换
    // 注意事项：
    //   - 输入超出0-15范围时返回空字符，避免非法输入导致错误
    public static char getUpperCaseHex(final int digit) {
        if (digit < 0 || digit >= 16) {
            return '\0';
        }
        return digit < 10 ? getNumericalDigit(digit) : getUpperCaseAlphaDigit(digit);
    }

    /**
     * Converts a digit into an lower-case hexadecimal character or the null character if invalid.
     *
     * @param digit a number 0 - 15
     * @return the hex character for that digit or '\0' if invalid
     */
    // 将数字（0-15）转换为小写十六进制字符，若输入无效则返回空字符
    // 方法功能：将输入的数字转换为对应的十六进制字符（小写），如0->'0'，10->'a'
    // 参数：
    //   - digit：输入的整数，范围为0到15，表示待转换的十六进制位
    // 返回值：
    //   - 返回对应的十六进制字符（小写），或空字符'\0'（若输入无效）
    // 执行流程：
    //   1. 检查输入数字是否在有效范围（0-15）
    //   2. 若有效，调用辅助方法getNumericalDigit或getLowerCaseAlphaDigit进行转换
    // 注意事项：
    //   - 输入超出0-15范围时返回空字符，确保健壮性
    public static char getLowerCaseHex(final int digit) {
        if (digit < 0 || digit >= 16) {
            return '\0';
        }
        return digit < 10 ? getNumericalDigit(digit) : getLowerCaseAlphaDigit(digit);
    }

    // 私有辅助方法，将数字（0-9）转换为对应的字符表示
    // 方法功能：将0-9的数字转换为字符'0'-'9'
    // 参数：
    //   - digit：输入的整数，范围为0到9
    // 返回值：
    //   - 对应的数字字符（如0返回'0'）
    // 执行流程：
    //   - 通过ASCII运算，将输入数字加上'0'的ASCII值，得到对应字符
    // 注意事项：
    //   - 假定输入已验证在0-9范围内，无需额外检查
    private static char getNumericalDigit(final int digit) {
        return (char) ('0' + digit);
    }

    // 私有辅助方法，将数字（10-15）转换为大写字母表示（A-F）
    // 方法功能：将10-15的数字转换为大写十六进制字符'A'-'F'
    // 参数：
    //   - digit：输入的整数，范围为10到15
    // 返回值：
    //   - 对应的十六进制字符（如10返回'A'）
    // 执行流程：
    //   - 通过ASCII运算，将输入数字减去10后加上'A'的ASCII值，得到对应字符
    // 注意事项：
    //   - 假定输入已验证在10-15范围内
    private static char getUpperCaseAlphaDigit(final int digit) {
        return (char) ('A' + digit - 10);
    }

    // 私有辅助方法，将数字（10-15）转换为小写字母表示（a-f）
    // 方法功能：将10-15的数字转换为小写十六进制字符'a'-'f'
    // 参数：
    //   - digit：输入的整数，范围为10到15
    // 返回值：
    //   - 对应的十六进制字符（如10返回'a'）
    // 执行流程：
    //   - 通过ASCII运算，将输入数字减去10后加上'a'的ASCII值，得到对应字符
    // 注意事项：
    //   - 假定输入已验证在10-15范围内
    private static char getLowerCaseAlphaDigit(final int digit) {
        return (char) ('a' + digit - 10);
    }

    // 私有构造函数，防止类被实例化
    // 方法功能：确保Chars类为工具类，无法创建实例
    // 注意事项：
    //   - 作为final类且构造函数私有，禁止外部实例化和继承
    private Chars() {
    }
}
