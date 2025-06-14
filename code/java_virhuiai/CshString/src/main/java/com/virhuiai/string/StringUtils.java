package com.virhuiai.string;

import com.virhuiai.CshLogUtils.v2.LoggerUtils;



/**
 * 字符串工具类

 StringValidationUtils
 StringTransformationUtils
 StringSplitUtils
 StringDesensitizeUtils
 StringSubstringUtils
 StringCompareUtils
 ObjectStringUtils
 StringLengthUtils

 StringUtils类  具有了这些接口定义的所有方法。

 *
 */
//public class StringUtils
//        implements StringValidationUtils, StringTransformationUtils,
//        StringSplitUtils, StringDesensitizeUtils,
//        StringSubstringUtils, StringCompareUtils,
//        ObjectStringUtils, StringLengthUtils
//{
//    public StringUtils() {
//    }
////    private static final LoggerUtils logger = LoggerUtils.getLog((Class<?>) StringUtils.class);
//}

public enum StringUtils
        implements StringValidationUtils, StringTransformationUtils,
        StringSplitUtils, StringDesensitizeUtils,
        StringSubstringUtils, StringCompareUtils,
        ObjectStringUtils, StringLengthUtils
{
    INSTANCE;

    // 私有化构造函数
    private StringUtils() {
    }

    // 可选：如果需要在初始化时执行一些操作,可以使用实例初始化方法
    // private void init() {
    //     // 初始化操作
    // }

    // 在这里实现接口的方法
    // ...

//    private static final LoggerUtils logger = LoggerUtils.getLog((Class<?>) StringUtils.class);
}