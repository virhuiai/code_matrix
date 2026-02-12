package com.jgoodies.quicksearch;

/**
 * 快速搜索过程事件类
 */
public class QuickSearchProcessEvent {
    private final Object source;
    private final String searchText;
    private final Object[] results;
    
    public QuickSearchProcessEvent(Object source, String searchText, Object[] results) {
        this.source = source;
        this.searchText = searchText;
        this.results = results;
    }
    
    public Object getSource() {
        return source;
    }
    
    public String getSearchText() {
        return searchText;
    }
    
    public Object[] getResults() {
        return results;
    }
    
    // 添加缺失的 State 枚举
    public enum State {
        PROCESS_STARTED,
        PROCESS_SUCCEEDED,
        PROCESS_FAILED
    }
}