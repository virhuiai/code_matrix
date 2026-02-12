package com.jgoodies.search;

import javax.swing.JTextField;

/**
 * 搜索完成管理器接口
 * 用于管理自动完成功能
 */
public interface CompletionManager {
    void addCompletionListener(CompletionListener listener);
    void removeCompletionListener(CompletionListener listener);
    void showCompletions(String text);
    void hideCompletions();
    
    // 添加静态方法
    static CompletionManager getManager(JTextField field) {
        // 简单实现，返回null
        return null;
    }
}