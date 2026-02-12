package com.jgoodies.search;

/**
 * 完成处理器接口
 */
public interface CompletionProcessor {
    String[] getSuggestions(String input);
    void processCompletion(String completion);
}