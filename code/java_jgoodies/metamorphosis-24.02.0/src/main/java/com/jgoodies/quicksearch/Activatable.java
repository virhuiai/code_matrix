package com.jgoodies.quicksearch;

import java.util.EventObject;
import javax.swing.Icon;

/**
 * 可激活接口
 */
public interface Activatable {
    void activate(EventObject event);
    String getCategory();
    String getDisplayString();
    Icon getIcon();
    int getRank();
}