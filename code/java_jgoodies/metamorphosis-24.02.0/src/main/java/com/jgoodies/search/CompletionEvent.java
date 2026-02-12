package com.jgoodies.search;

/**
 * 完成事件类
 */
public class CompletionEvent {
    private final Object source;
    private final String completionText;
    
    public CompletionEvent(Object source, String completionText) {
        this.source = source;
        this.completionText = completionText;
    }
    
    public Object getSource() {
        return source;
    }
    
    public String getCompletionText() {
        return completionText;
    }
}