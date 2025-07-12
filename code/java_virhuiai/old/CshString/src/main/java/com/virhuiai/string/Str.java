package com.virhuiai.string;


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
 *
 * 使用枚举实现单例模式有几个好处:
 *
 * 它保证了单例的唯一性。每个枚举类型只能有一个实例。
 * 它是线程安全的。枚举常量的创建是线程安全的。
 * 它可以防止反序列化重新创建新的对象。枚举类型默认实现了 Serializable 接口,并确保反序列化时返回同一个实例。
 *
 <groupId>virhuiai</groupId>
 <artifactId>CshString</artifactId>
 <version>1.1</version>

 */
public enum Str
        implements Validation, Transformation,
        Split, Desensitize,
        Substring, Compare,
        ToString, ToJson,ToStringDepthLimited,ToStringnCycleDetection,
        Length, Url
{
    Utils;
    private Str() {
        // 私有化构造函数
    }
}