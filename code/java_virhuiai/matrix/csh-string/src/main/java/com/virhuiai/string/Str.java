package com.virhuiai.string;

/**
 * 字符串工具类，实现了多个字符串操作相关接口，具备字符串验证、转换、分割、脱敏等功能。
 * 该类使用枚举实现单例模式，确保全局只有一个实例。
 * 
 * 涉及的接口及对应功能如下：
 * - StringValidationUtils：字符串验证
 * - StringTransformationUtils：字符串转换
 * - StringSplitUtils：字符串分割
 * - StringDesensitizeUtils：字符串脱敏
 * - StringSubstringUtils：字符串截取
 * - StringCompareUtils：字符串比较
 * - ObjectStringUtils：对象转字符串
 * - StringLengthUtils：字符串长度处理
 * 
 * 该类实现了上述接口定义的所有方法。
 * 
 * 使用枚举实现单例模式的优势：
 * - 唯一性保证：每个枚举类型只能有一个实例，确保单例的唯一性。
 * - 线程安全：枚举常量的创建过程是线程安全的。
 * - 防止反序列化问题：枚举类型默认实现了 Serializable 接口，能确保反序列化时返回同一个实例。
 * 
 * 项目信息：
<groupId>virhuiai</groupId>
<artifactId>CshString</artifactId>
<version>1.1</version>
 */
public enum Str
        implements Validation, Transformation,
        Split, Desensitize,
        Substring, Compare,
        ToString, ToJson, ToStringDepthLimited, ToStringnCycleDetection,
        Length, Url
{
    Utils;

    private Str() {
        // 私有化构造函数
    }

    // 显式重写 endsWith 方法，这里使用 Validation 接口的实现
    @Override
    public boolean endsWith(CharSequence str, CharSequence suffix) {
        return Validation.super.endsWith(str, suffix);
    }

    @Override
    public boolean startsWith(CharSequence str, CharSequence suffix) {
        return Validation.super.endsWith(str, suffix);
    }

    @Override
    public boolean contains(CharSequence str, CharSequence searchStr) {
        return Validation.super.contains(str, searchStr);
    }

}