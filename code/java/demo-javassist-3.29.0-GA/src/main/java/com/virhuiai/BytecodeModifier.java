package com.virhuiai;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

//使用Javassist进行字节码修改的主类
public class BytecodeModifier {
    public static void main(String[] args) {
        //        该版本支持从Java 6到Java 14的字节码操作。
        try {
            // 获取默认的ClassPool实例，用于管理CtClass对象
            ClassPool pool = ClassPool.getDefault();

            // 获取目标枚举类
            // 通过完整类名获取目标类的CtClass对象
            // CtClass用于表示被加载的类文件
            CtClass enumClass = pool.get("com.virhuiai.GreetingService");

            // 获取forNumber方法
            CtMethod forNumberMethod = enumClass.getDeclaredMethod("sayHi");
//            CtMethod forNumberMethod = enumClass.getDeclaredMethod("forNumber", new CtClass[]{CtClass.intType}); // forNumber方法接收一个int参数

            // 修改方法内容，使其始终返回VALID
            // 修改方法体的内容
            // 将原来输出"Hello,world"改为输出"Hello,world2"
            forNumberMethod.setBody(
                    "{ System.out.println(\"Hello,world2\"); }"
            );
            // 重新加载修改后的类
            // 将修改后的类重新加载到JVM中
            enumClass.toClass();

            System.out.println(" patch applied successfully");

        } catch (Exception e) {
            System.err.println("Failed to apply patch: " + e.getMessage());
            e.printStackTrace();
        }

        // 创建Test1实例并调用被修改的方法
        GreetingService t = new GreetingService();
        t.sayHi();
    }
}
