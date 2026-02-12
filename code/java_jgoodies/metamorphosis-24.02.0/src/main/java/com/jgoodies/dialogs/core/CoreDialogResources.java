package com.jgoodies.dialogs.core;

/**
 * 对话框核心资源类
 */
public class CoreDialogResources {
    public static String getString(String key) {
        // 简单的资源获取实现
        if ("common.browse.short".equals(key)) {
            return "Browse";
        }
        return key;
    }
    
    public static String getString(String key, Object[] args) {
        String value = getString(key);
        if (args != null && args.length > 0) {
            return String.format(value, args);
        }
        return value;
    }
}