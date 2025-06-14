package com.virhuiai.CshLogUtils;

// Log4j2核心包导入
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java集合框架导入
import java.util.HashMap;
import java.util.Map;

/**
 * 日志工具类 - 基于Log4j2的单例模式日志封装
 * 提供统一的日志记录接口，支持多种日志级别
 * 通过堆栈跟踪自动获取调用者信息，实现精确的日志定位
 *
 * TODO: 考虑改为枚举单例模式或使用双重检查锁定模式确保线程安全
 * TODO: 添加日志级别检查，避免不必要的字符串拼接
 * TODO: 考虑添加异步日志支持
 */
public class LoggerUtils {
    // 单例实例 - 静态变量保存唯一实例
    private static LoggerUtils logger;

    // Logger缓存映射 - 用于缓存不同类名对应的Logger实例，避免重复创建
    // TODO: 考虑使用ConcurrentHashMap确保线程安全
    private Map<String, Logger> loggerMap = new HashMap<>();

    // 静态初始化块 - 类加载时创建单例实例
    static {
        logger = null; // 先置空（可选，Java会自动初始化为null）
        logger = new LoggerUtils(); // 创建单例实例
    }

    /**
     * 根据类名获取Logger实例
     * 实现Logger缓存机制，避免重复创建相同的Logger
     *
     * @param clazzName 类的完全限定名
     * @return 对应的Logger实例
     */
    private Logger getLogger(String clazzName) {
        // 先从缓存中尝试获取
        Logger logger2 = this.loggerMap.get(clazzName);
        if (logger2 == null) {
            // 使用Log4j2的LogManager.getLogger()  Log4j1 使用的则是Logger.getLogger()
            // 缓存中不存在，创建新的Logger实例
            logger2 = LogManager.getLogger(clazzName);
            // 将新创建的Logger放入缓存
            this.loggerMap.put(clazzName, logger2);
        }
        return logger2;
    }

    /**
     * 私有构造函数 - 防止外部直接创建实例，确保单例模式
     */
    private LoggerUtils() {
    }

    /**
     * 获取日志工具实例 - 基于类对象的静态方法
     *
     * @param clz 调用日志的类对象
     * @return LoggerUtils单例实例
     * TODO: 参数clz当前未使用，考虑移除或实现类级别的Logger缓存
     */
    public static LoggerUtils getLog(Class<?> clz) {
        return logger;
    }

    /**
     * 获取日志工具实例 - 基于日志名称的静态方法
     *
     * @param logName 日志名称
     * @return LoggerUtils单例实例
     * TODO: 参数logName当前未使用，考虑移除或实现命名Logger支持
     */
    public static LoggerUtils getLog(String logName) {
        return logger;
    }

    /**
     * 记录INFO级别日志
     * 自动获取调用者类名，无异常信息
     *
     * @param info 要记录的信息内容
     */
    public void info(String info) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // stack[0]是当前方法，stack[1]是调用此方法的方法所在的类
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录INFO级别日志，无异常信息
        logger2.log(Level.INFO, info, (Throwable) null);
    }

    /**
     * 记录INFO级别日志
     * 自动获取调用者类名，包含异常信息
     *
     * @param info 要记录的信息内容
     * @param tx 相关的异常对象
     */
    public void info(String info, Throwable tx) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录INFO级别日志，包含异常信息
        logger2.log(Level.INFO, info, tx);
    }

    /**
     * 记录DEBUG级别日志
     * 自动获取调用者类名，无异常信息
     *
     * @param info 要记录的调试信息
     */
    public void debug(String info) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录DEBUG级别日志，无异常信息
        logger2.log(Level.DEBUG, info, (Throwable) null);
    }

    /**
     * 记录DEBUG级别日志
     * 自动获取调用者类名，包含异常信息
     *
     * @param info 要记录的调试信息
     * @param tx 相关的异常对象
     */
    public void debug(String info, Throwable tx) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录DEBUG级别日志，包含异常信息
        logger2.log(Level.DEBUG, info, tx);
    }

    /**
     * 记录WARN级别日志
     * 自动获取调用者类名，无异常信息
     *
     * @param info 要记录的警告信息
     */
    public void warn(String info) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录WARN级别日志，无异常信息
        logger2.log(Level.WARN, info, (Throwable) null);
    }

    /**
     * 记录WARN级别日志
     * 自动获取调用者类名，包含异常信息
     *
     * @param info 要记录的警告信息
     * @param tx 相关的异常对象
     */
    public void warn(String info, Throwable tx) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录WARN级别日志，包含异常信息
        logger2.log(Level.WARN, info, tx);
    }

    /**
     * 记录TRACE级别日志
     * 自动获取调用者类名，无异常信息
     * TRACE是最详细的日志级别，通常用于跟踪程序执行流程
     *
     * @param info 要记录的跟踪信息
     */
    public void trace(String info) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录TRACE级别日志，无异常信息
        logger2.log(Level.TRACE, info, (Throwable) null);
    }

    /**
     * 记录TRACE级别日志
     * 自动获取调用者类名，包含异常信息
     *
     * @param info 要记录的跟踪信息
     * @param tx 相关的异常对象
     */
    public void trace(String info, Throwable tx) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录TRACE级别日志，包含异常信息
        logger2.log(Level.TRACE, info, tx);
    }

    /**
     * 记录ERROR级别日志
     * 自动获取调用者类名，无异常信息
     *
     * @param info 要记录的错误信息
     */
    public void error(String info) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录ERROR级别日志，无异常信息
        logger2.log(Level.ERROR, info, (Throwable) null);
    }

    /**
     * 记录ERROR级别日志
     * 自动获取调用者类名，包含异常信息
     *
     * @param info 要记录的错误信息
     * @param tx 相关的异常对象
     */
    public void error(String info, Throwable tx) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 记录ERROR级别日志，包含异常信息
        logger2.log(Level.ERROR, info, tx);
    }

    /**
     * 记录ERROR级别日志
     * 自动获取调用者类名，仅传入异常对象
     * 使用异常的getMessage()作为日志消息
     *
     * @param tx 要记录的异常对象
     */
    public void error(Throwable tx) {
        // 获取当前线程的堆栈跟踪信息
        StackTraceElement[] stack = new Throwable().getStackTrace();
        // 根据调用者类名获取对应的Logger
        Logger logger2 = getLogger(stack[1].getClassName());
        // 使用异常的getMessage()作为日志消息，记录ERROR级别日志
        logger2.log(Level.ERROR, tx.getMessage(), tx);
    }

    /**
     * 字符串格式化方法
     * 支持使用#1, #2, #3...的占位符格式
     * 将占位符替换为args数组中对应位置的值
     *
     * @param msg 包含占位符的消息模板
     * @param args 用于替换占位符的参数数组
     * @return 格式化后的字符串
     *
     * TODO: 考虑使用正则表达式优化字符串处理性能
     * TODO: 添加参数边界检查和异常处理
     * TODO: 支持更多格式化选项（如日期、数字格式化）
     */
    public static String format(String msg, String[] args) {
        int val; // 用于存储占位符对应的数字值

        // 检查消息是否为空以及是否包含占位符标识符'#'（ASCII码35）
        if (msg != null && msg.length() > 0 && msg.indexOf(35) > -1) {
            StringBuilder sb = new StringBuilder(); // 用于构建格式化后的字符串
            boolean isArg = false; // 标识当前是否处于占位符处理状态

            // 逐字符遍历消息字符串
            for (int x = 0; x < msg.length(); x++) {
                char c = msg.charAt(x); // 获取当前字符

                if (isArg) {
                    // 当前处于占位符处理状态
                    isArg = false; // 重置占位符状态标识

                    // 检查是否为数字字符，且数字范围在参数数组范围内
                    if (Character.isDigit(c) && (val = Character.getNumericValue(c)) >= 1 && val <= args.length) {
                        // 将占位符替换为对应的参数值（数组索引从0开始，所以要减1）
                        sb.append(args[val - 1]);
                    } else {
                        // 不是有效的占位符，将'#'和当前字符都添加到结果中
                        sb.append('#');
                        sb.append(c); // 添加当前字符
                    }
                } else if (c == '#') {
                    // 遇到占位符标识符，设置占位符处理状态
                    isArg = true;
                } else {
                    // 普通字符，直接添加到结果中
                    sb.append(c);
                }
            }

            // 如果字符串以'#'结尾但没有跟随数字，需要将最后的'#'添加到结果中
            if (isArg) {
                sb.append('#');
            }

            // 返回格式化后的字符串
            return sb.toString();
        }

        // 消息为空或不包含占位符，直接返回原消息
        return msg;
    }
}
