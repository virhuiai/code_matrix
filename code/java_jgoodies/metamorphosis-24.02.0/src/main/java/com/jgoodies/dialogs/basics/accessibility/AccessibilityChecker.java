package com.jgoodies.dialogs.basics.accessibility;

/**
 * 可访问性检查器接口
 */
public interface AccessibilityChecker {
    void checkAccessibility(Object component);
    boolean isAccessible(Object component);
    String getAccessibilityReport(Object component);
}