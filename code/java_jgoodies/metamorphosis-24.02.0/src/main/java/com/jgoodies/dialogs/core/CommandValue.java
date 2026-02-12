package com.jgoodies.dialogs.core;

/**
 * 命令值枚举
 */
public enum CommandValue {
    BROWSE("Browse");
    
    private final String markedText;
    
    CommandValue(String markedText) {
        this.markedText = markedText;
    }
    
    public String getMarkedText() {
        return markedText;
    }
}