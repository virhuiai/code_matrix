package com.jgoodies.quicksearch;

/**
 * 快速搜索视图接口
 */
public interface QuickSearchView {
    void setSearchText(String text);
    String getSearchText();
    void addSearchListener(SearchListener listener);
    void removeSearchListener(SearchListener listener);
    void focusSearchField();
    void clearSearch();
}