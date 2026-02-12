package com.jgoodies.dialogs.basics.accessibility;

/**
 * 默认可访问性检查器实现
 */
public class DefaultAccessibilityChecker implements AccessibilityChecker {
    
    @Override
    public void checkAccessibility(Object component) {
        // 简单实现，实际项目中应该包含详细的可访问性检查逻辑
        System.out.println("Checking accessibility for: " + component.getClass().getSimpleName());
    }
    
    @Override
    public boolean isAccessible(Object component) {
        // 简单实现，返回true
        return true;
    }
    
    @Override
    public String getAccessibilityReport(Object component) {
        // 简单实现，返回基础报告
        return "Accessibility report for " + component.getClass().getSimpleName() + ": OK";
    }
}