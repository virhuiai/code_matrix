package com.virhuiai;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class StatusEnumMain {

    private static void modifyForNumberMethod(CtClass ctClass)
            throws javassist.NotFoundException, javassist.CannotCompileException {
        // 获取forNumber方法
        CtMethod forNumberMethod = ctClass.getDeclaredMethod("forNumber");

        // 修改方法体，使其始终返回VALID状态
        String newBody =
                "{ " +
                        "    return com.virhuiai.StatusEnum.VALID; " +
                        "}";
        forNumberMethod.setBody(newBody);

        // 重新加载修改后的类
        ctClass.toClass();
        System.out.println("枚举方法修改成功");
    }

    private static void testModifiedEnum() {
        // 测试修改后的方法
        StatusEnum status1 = StatusEnum.forNumber(0);  // 应该返回 VALID
        StatusEnum status2 = StatusEnum.forNumber(-1); // 应该返回 VALID
        StatusEnum status3 = StatusEnum.forNumber(1);  // 应该返回 VALID

        System.out.println("Status1: " + status1.name() + ", code=" + status1.getCode());
        System.out.println("Status2: " + status2.name() + ", code=" + status2.getCode());
        System.out.println("Status3: " + status3.name() + ", code=" + status3.getCode());
    }

    public static void main(String[] args) {
        String TARGET_ENUM = "com.virhuiai.StatusEnum";
        ClassPool pool = null;
        CtClass ctClass = null;

        try {
            pool = ClassPool.getDefault();
            ctClass = pool.get(TARGET_ENUM);

            // 修改 forNumber 方法
            modifyForNumberMethod(ctClass);

            // 测试修改后的方法
            testModifiedEnum();

        } catch (javassist.NotFoundException e) {
            System.out.println("找不到目标枚举类或方法");
        } catch (javassist.CannotCompileException e) {
            System.out.println("代码编译错误");
        } catch (Exception e) {
            System.out.println("未知错误");
        } finally {
            if (ctClass != null) {
                ctClass.detach();

            }
        }
    }
}
