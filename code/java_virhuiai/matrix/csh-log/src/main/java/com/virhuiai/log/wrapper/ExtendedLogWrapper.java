package com.virhuiai.log.wrapper;

import com.virhuiai.log.StackTraceUtil;
import org.apache.commons.logging.Log;

/**
 * 使用类继承方式，需要实现Log接口的所有方法
 * <p>
 * 日志包装类
 * 提供了包含行号信息的日志对象
 * <p>
 * ExtendedLogWrapper 类作为一个代理，包装了原始的 Log 对象。
 * 它重写了所有的日志方法（如debug, info, error等），
 * 在这些方法中添加了获取行号信息的逻辑。
 * <p>
 * 原始的 Log 实现通常不会去分析调用栈，因为这是一个相对昂贵的操作。
 * 而且，原始 Log 无法准确知道是在用户代码的哪个位置被调用的，因为它离实际调用点"太近"了。
 */
public class ExtendedLogWrapper implements Log {

    private final Log log;

    public ExtendedLogWrapper(Log log) {
        this.log = log;
    }

    @Override
    public void debug(Object message) {
        log.debug(addLineInfo(message));
    }

    @Override
    public void debug(Object message, Throwable t) {
        log.debug(addLineInfo(message), t);
    }

    @Override
    public void error(Object message) {
        log.error(addLineInfo(message));
    }

    @Override
    public void error(Object message, Throwable t) {
        log.error(addLineInfo(message), t);
    }

    @Override
    public void fatal(Object message) {
        log.fatal(addLineInfo(message));
    }

    @Override
    public void fatal(Object message, Throwable t) {
        log.fatal(addLineInfo(message), t);
    }

    @Override
    public void info(Object message) {
        log.info(addLineInfo(message));
    }

    @Override
    public void info(Object message, Throwable t) {
        log.info(addLineInfo(message), t);
    }

    @Override
    public void trace(Object message) {
        log.trace(addLineInfo(message));
    }

    @Override
    public void trace(Object message, Throwable t) {
        log.trace(addLineInfo(message), t);
    }

    @Override
    public void warn(Object message) {
        log.warn(addLineInfo(message));
    }

    @Override
    public void warn(Object message, Throwable t) {
        log.warn(addLineInfo(message), t);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return log.isFatalEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    /**
     * 获取调用日志方法的代码行信息
     * 当一个Java程序运行时，每个方法调用都会在调用栈上创建一个新的栈帧。这个栈帧包含了方法调用的信息，如类名、方法名、行号等。
     *
     * @return 包含类名、方法名和行号的字符串，如果无法获取则返回默认信息
     */
    private String getLineInfo() {
        // 定义目标栈帧的索引
        // 索引0是getStackTrace方法
        // 索引1是当前方法（getLineInfo）
        // 索引2是调用当前方法的方法（如addLineInfo）
        // 索引3是LogWrapper中的日志方法（如debug, info等）
        // 索引4通常是用户代码中调用日志方法的位置

        // 获取目标调用栈帧
        StackTraceElement ste = StackTraceUtil.getTargetStackTrace(5);// 注意：4这个值可能需要根据实际情况调整
        if(null == ste){
            return "[UnknownClass.unknownMethod():0]";
        }else{
            // 使用String.format格式化输出
            // %s 用于插入字符串（类名和方法名）
            // %d 用于插入整数（行号）
//    return String.format("[%s.%s():%d]",
            return String.format("[所在方法:%s,所在行:%d]",
//        ste.getClassName(),    // 获取类名
                    ste.getMethodName(),   // 获取方法名
                    ste.getLineNumber()    // 获取行号
            );
        }
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
}