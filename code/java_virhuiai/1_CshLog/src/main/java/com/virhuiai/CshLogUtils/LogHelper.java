package com.virhuiai.CshLogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface LogHelper {
    public static Log getLogger(Class<?> cls) {
        return LogFactory.getFactory().getInstance(cls);
    }

    public static Log getLogger(String name) {
        return LogFactory.getFactory().getInstance(name);
    }

    public static Log getLogger() {
        return getLogger("");
    }
}
