package com.virhuiai.CshLogUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;



public class LoggerUtils {
    private static LoggerUtils logger;
    private Map<String, Logger> loggerMap = new HashMap<>();

    static {
        logger = null;
        logger = new LoggerUtils();
    }

    private Logger getLogger(String clazzName) {
        Logger logger2 = this.loggerMap.get(clazzName);
        if (logger2 == null) {
            // 使用Log4j2的LogManager.getLogger()  Log4j1 使用的则是Logger.getLogger()
            logger2 = LogManager.getLogger(clazzName);
            this.loggerMap.put(clazzName, logger2);
        }
        return logger2;
    }

    private LoggerUtils() {
    }

    public static LoggerUtils getLog(Class<?> clz) {
        return logger;
    }

    public static LoggerUtils getLog(String logName) {
        return logger;
    }

    public void info(String info) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.INFO, info, (Throwable) null);
    }

    public void info(String info, Throwable tx) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.INFO, info, tx);
    }

    public void debug(String info) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.DEBUG, info, (Throwable) null);
    }

    public void debug(String info, Throwable tx) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.DEBUG, info, tx);
    }

    public void warn(String info) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.WARN, info, (Throwable) null);
    }

    public void warn(String info, Throwable tx) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.WARN, info, tx);
    }

    public void trace(String info) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.TRACE, info, (Throwable) null);
    }

    public void trace(String info, Throwable tx) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.TRACE, info, tx);
    }

    public void error(String info) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.ERROR, info, (Throwable) null);
    }

    public void error(String info, Throwable tx) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.ERROR, info, tx);
    }

    public void error(Throwable tx) {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        Logger logger2 = getLogger(stack[1].getClassName());
        logger2.log(Level.ERROR, tx.getMessage(), tx);
    }

    public static String format(String msg, String[] args) {
        int val;
        if (msg != null && msg.length() > 0 && msg.indexOf(35) > -1) {
            StringBuilder sb = new StringBuilder();
            boolean isArg = false;
            for (int x = 0; x < msg.length(); x++) {
                char c = msg.charAt(x);
                if (isArg) {
                    isArg = false;
                    if (Character.isDigit(c) && (val = Character.getNumericValue(c)) >= 1 && val <= args.length) {
                        sb.append(args[val - 1]);
                    } else {
                        sb.append('#');
                        sb.append(c); // 添加当前字符
                    }
                } else if (c == '#') {
                    isArg = true;
                } else {
                    sb.append(c);
                }
            }
            if (isArg) {
                sb.append('#');
            }
            return sb.toString();
        }
        return msg;
    }
}
