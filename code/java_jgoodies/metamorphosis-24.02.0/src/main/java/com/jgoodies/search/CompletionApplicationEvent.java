package com.jgoodies.search;

/**
 * 完成应用事件类
 */
public class CompletionApplicationEvent {
    private final Object source;
    private final Completion completion;
    
    public CompletionApplicationEvent(Object source, Completion completion) {
        this.source = source;
        this.completion = completion;
    }
    
    public Object getSource() {
        return source;
    }
    
    public Completion getCompletion() {
        return completion;
    }
}