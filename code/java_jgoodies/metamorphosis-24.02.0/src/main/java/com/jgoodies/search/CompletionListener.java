package com.jgoodies.search;

/**
 * 完成监听器接口
 */
public interface CompletionListener {
    void completionSelected(CompletionEvent event);
    void completionCancelled(CompletionEvent event);
}