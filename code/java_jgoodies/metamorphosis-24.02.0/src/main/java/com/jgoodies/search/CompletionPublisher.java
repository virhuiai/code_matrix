package com.jgoodies.search;

/**
 * 完成发布者接口
 */
public interface CompletionPublisher {
    void publishCompletion(Completion completion);
    void publishCompletions(Completion[] completions);
}