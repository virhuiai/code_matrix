package com.microsoft.playwright.impl;


import com.google.gson.JsonObject;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.impl.driver.Driver2;
import com.virhuiai.ReflectionUtils;


import java.io.IOException;
import java.util.Collections;
import java.util.Map;


/**
 * Playwright实现类第二版
 * 继承自PlaywrightImpl，提供增强的Playwright创建功能
 */
public class PlaywrightImpl2 extends PlaywrightImpl {
    /**
     * 构造函数
     *
     * @param parent      父级通道所有者
     * @param type        类型标识
     * @param guid        全局唯一标识符
     * @param initializer 初始化配置对象
     */
    PlaywrightImpl2(ChannelOwner parent, String type, String guid, JsonObject initializer) {
        super(parent, type, guid, initializer);
    }


    /**
     * 创建Playwright实例的公共接口方法
     *
     * @param options 创建选项，包含环境变量等配置
     * @return 返回创建的PlaywrightImpl实例
     */
    public static PlaywrightImpl create(Playwright2.CreateOptions options) {
        // 调用内部实现方法，不强制创建新的驱动实例
        return createImpl(options, false);
    }


    /**
     * 创建Playwright实例的内部实现方法
     *
     * @param options                        创建选项，可能包含环境变量配置
     * @param forceNewDriverInstanceForTests 是否强制为测试创建新的驱动实例
     * @return 返回初始化完成的PlaywrightImpl实例
     */
    public static PlaywrightImpl createImpl(Playwright.CreateOptions options, boolean forceNewDriverInstanceForTests) {
        // 初始化环境变量映射为空集合
        Map<String, String> env = Collections.emptyMap();
        // 如果选项不为空且包含环境变量，则使用提供的环境变量
        if (options != null && options.env != null) {
            env = options.env;
        }


        // 根据是否强制创建新实例来决定驱动的创建方式
        Driver2 driver = forceNewDriverInstanceForTests ?
                Driver2.createAndInstall(env, true) :  // 强制创建新驱动实例
                Driver2.ensureDriverInstalled(env, true);  // 确保驱动已安装，复用现有实例


        try {
            // 创建进程构建器
            ProcessBuilder pb = driver.createProcessBuilder();
            // 添加运行驱动的命令参数
            pb.command().add("run-driver");
            // 将错误输出重定向到当前进程的错误输出流
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            // 启动驱动进程
            Process p = pb.start();
            // 创建连接对象，使用管道传输与驱动进程通信
            Connection connection = new Connection(new PipeTransport(p.getInputStream(), p.getOutputStream()), env);
            // 初始化Playwright实例
            PlaywrightImpl result = connection.initializePlaywright();
            // 使用反射设置私有字段driverProcess
            ReflectionUtils.setObjField(result, "driverProcess", p);//result.driverProcess = p;// 字段是私有的
            // 初始化共享选择器
            result.initSharedSelectors((PlaywrightImpl) null);
            // 返回完全初始化的Playwright实例
            return result;
        } catch (IOException var8) {
            // 如果启动驱动失败，抛出Playwright异常
            throw new PlaywrightException("Failed to launch driver", var8);
        }
    }
}