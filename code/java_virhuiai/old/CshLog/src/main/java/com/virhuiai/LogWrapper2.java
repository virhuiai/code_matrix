package com.virhuiai;

import org.apache.commons.logging.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LogWrapper2 类
 * 这个类实现了 InvocationHandler 接口，用于创建 Log 接口的动态代理
 * 主要功能是在日志信息前添加调用位置的行信息
 */
public class LogWrapper2 implements InvocationHandler {

    // 原始的 Log 对象
    private final Log log;

    // 定义需要处理的日志方法名集合
    private static final Set<String> LOG_METHODS = new HashSet<>(Arrays.asList(
            "debug", "info", "warn", "error", "fatal", "trace"
    ));

    /**
     * 私有构造函数，防止直接实例化
     * @param log 原始的 Log 对象
     */
    private LogWrapper2(Log log) {
        this.log = log;
    }

    /**
     * InvocationHandler 接口的 invoke 方法实现
     * 这个方法会拦截对 Log 接口方法的所有调用
     *
     * @param proxy  代理对象
     * @param method 被调用的方法
     * @param args   方法参数
     * @return 方法调用的结果
     * @throws Throwable 如果调用过程中发生异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 检查是否是日志方法，并且有参数
        if (LOG_METHODS.contains(method.getName()) && args != null && args.length > 0) {
            // 在第一个参数（日志消息）前添加行信息
            args[0] = addLineInfo(args[0]);
        }
        try {
            // 调用原始 Log 对象的方法
            return method.invoke(log, args);
        } catch (Exception e) {
            // 如果发生异常，包装成 RuntimeException 并抛出
            throw new RuntimeException("调用日志方法时发生错误", e);
        }
    }

    /**
     * 获取调用日志方法的代码行信息
     *
     * @return 包含方法名和行号的字符串
     */
    private String getLineInfo() {
        // 获取当前线程的调用栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final int targetIndex = 5; // 注意：这个值可能需要根据实际情况调整

        // 检查调用栈深度是否足够
        if (stackTrace.length <= targetIndex) {
            return "[UnknownClass.unknownMethod():0]";
        }

        // 获取目标调用栈帧
        StackTraceElement ste = stackTrace[targetIndex];
        // 格式化并返回行信息
        return String.format("[所在方法:%s,所在行:%d]",
                ste.getMethodName(),
                ste.getLineNumber()
        );
    }

    /**
     * 为日志消息添加行信息
     *
     * @param message 原始日志消息
     * @return 添加了行信息的日志消息
     */
    private String addLineInfo(Object message) {
        return getLineInfo() + " " + message;
    }

    /**
     * 创建一个包装了原始 Log 对象的代理对象
     *
     * @param log 原始的 Log 对象
     * @return 包装后的 Log 对象
     *
     * 1。 什么是代理对象？
     * 代理对象是一个类的包装，它可以控制对原始对象的访问。通过代理对象，我们可以在不修改原始类的情况下，添加额外的功能或控制逻辑。
     *
     * 2。Java的动态代理：
     * Java提供了java.lang.reflect.Proxy类来创建动态代理对象。这种方式可以在运行时动态地创建实现了指定接口的代理类。
     *
     * 3。创建代理对象的步骤：
     *  a. 定义一个接口（在这个例子中是Log接口）。
     *  b. 创建一个实现了InvocationHandler接口的类（在这个例子中是LogWrapper2类）。
     *  c. 使用Proxy.newProxyInstance()方法创建代理对象。
     *
     * 4。Proxy.newProxyInstance() 方法：
     * 这个方法接受三个参数：
     *      a.类加载器
     *      b.一个要实现的接口数组
     *      c.一个InvocationHandler实例
     *
     * 6.工作原理：
     * 当调用代理对象的方法时，这个调用会被转发到InvocationHandler的invoke方法。在invoke方法中，我们可以在调用原始方法之前或之后添加自定义逻辑。
     *
     * 7.优点：
     * 可以在不修改原始类的情况下添加功能
     * 可以控制对对象的访问
     * 可以在运行时动态地创建代理
     *
     * 8.使用场景：
     * 日志记录（就像这个例子）
     * 事务管理
     * 权限检查
     * 延迟加载
     * 远程方法调用
     */
    public static Log wrap(Log log) {
        return (Log) java.lang.reflect.Proxy.newProxyInstance(
                Log.class.getClassLoader(),
                new Class<?>[] { Log.class },
                new LogWrapper2(log)
        );
    }


}