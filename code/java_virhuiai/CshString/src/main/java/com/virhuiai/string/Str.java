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
 */
public enum Str
        implements StringValidationUtils, StringTransformationUtils,
        StringSplitUtils, StringDesensitizeUtils,
        StringSubstringUtils, StringCompareUtils,
        ObjectStringUtils, StringLengthUtils
{
    Utils;


    private Str() {
        // 私有化构造函数
    }


//    public void testMethod() {
//        System.out.println("StringUtils 已经初始化");
//    }
////    private static final LoggerUtils logger = LoggerUtils.getLog((Class<?>) StringUtils.class);
//
//    public static void main(String[] args) {
//        // 第一次访问 Str.Utils 时,init 方法将被调用
//        StringUtils utils = Str.Utils;
//
//
//        utils.testMethod();
//
//    }
}