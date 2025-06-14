package com.virhuiai.string;

import com.virhuiai.CshLogUtils.v2.LoggerUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;
import java.io.UnsupportedEncodingException;

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
public class StringUtils implements StringValidationUtils, StringTransformationUtils,
        StringSplitUtils, StringDesensitizeUtils,
        StringSubstringUtils, StringCompareUtils,
        ObjectStringUtils, StringLengthUtils {
    private static final LoggerUtils logger = LoggerUtils.getLog((Class<?>) StringUtils.class);


}
