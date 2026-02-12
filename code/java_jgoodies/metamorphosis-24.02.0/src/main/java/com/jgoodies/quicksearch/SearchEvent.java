package com.jgoodies.quicksearch;

/**
 * 搜索事件类
 */
public class SearchEvent {
    private final Object source;
    private final String searchText;
    private final SearchType searchType;
    
    public SearchEvent(Object source, String searchText, SearchType searchType) {
        this.source = source;
        this.searchText = searchText;
        this.searchType = searchType;
    }
    
    public Object getSource() {
        return source;
    }
    
    public String getSearchText() {
        return searchText;
    }
    
    public SearchType getSearchType() {
        return searchType;
    }
    
    public enum SearchType {
        TEXT_CHANGED,
        SEARCH_PERFORMED,
        SEARCH_CANCELLED
    }
}