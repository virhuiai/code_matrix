package com.jgoodies.search;

/**
 * 完成处理事件类
 */
public class CompletionProcessEvent {
    private final Object source;
    private final String inputText;
    private final Completion[] suggestions;
    
    public CompletionProcessEvent(Object source, String inputText, Completion[] suggestions) {
        this.source = source;
        this.inputText = inputText;
        this.suggestions = suggestions;
    }
    
    public Object getSource() {
        return source;
    }
    
    public String getInputText() {
        return inputText;
    }
    
    public Completion[] getSuggestions() {
        return suggestions;
    }
}