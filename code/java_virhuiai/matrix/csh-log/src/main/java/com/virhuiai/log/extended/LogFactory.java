package com.virhuiai.log.extended;

import com.virhuiai.log.CshLogUtils;
import org.apache.commons.logging.Log;

// LogFactory 类是一个日志工厂，用于获取 Log 实例。
public class LogFactory {

    private LogFactory() {
        // 私有构造函数，防止实例化
    }

    public static Log getLog(){
        // 该方法用于获取一个通用的 Log 实例。
        return CshLogUtils.createLogExtended();
        // 调用 CshLogUtils 工具类的 createLogExtended 方法创建并返回一个扩展的 Log 实例。
    }

    //LogFactory.getLog(xxxx.class);
    // 这是一个重载方法，用于根据指定的类获取 Log 实例。
    public static Log getLog(Class<?> clazz){
        // 参数 clazz：需要为其创建 Log 实例的类。
        // 返回值：一个绑定到指定类的 Log 实例。
        return CshLogUtils.createLogExtended(clazz);
        // 调用 CshLogUtils 工具类的 createLogExtended 方法，并传入指定的 Class 对象，
        // 以创建并返回一个与该类相关的扩展 Log 实例。
    }
}