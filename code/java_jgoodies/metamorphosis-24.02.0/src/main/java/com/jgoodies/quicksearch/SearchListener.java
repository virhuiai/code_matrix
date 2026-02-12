package com.jgoodies.quicksearch;

/**
 * 搜索监听器接口
 */
public interface SearchListener {
    void searchPerformed(SearchEvent event);
    void searchCancelled(SearchEvent event);
}