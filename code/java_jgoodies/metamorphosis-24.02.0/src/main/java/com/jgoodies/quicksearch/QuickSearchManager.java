package com.jgoodies.quicksearch;

import java.util.List;
import javax.swing.Action;

/**
 * 快速搜索管理器接口
 */
public interface QuickSearchManager {
    void addSearchListener(SearchListener listener);
    void removeSearchListener(SearchListener listener);
    void performSearch(String searchText);
    void cancelSearch();
    
    // 添加缺失的方法
    Action getOpenPreferencesAction();
    String getSearchText();
    QuickSearchProcessEvent.State getState();
    List<Activatable> getActivatables();
}