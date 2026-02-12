package com.jgoodies.quicksearch;

import javax.swing.Action;
import java.util.EventObject;

/**
 * 基于Action的可激活实现
 */
public class ActionActivatable implements Activatable {
    private final String category;
    private final Action action;
    private final int rank;
    
    public ActionActivatable(String category, Action action, int rank) {
        this.category = category;
        this.action = action;
        this.rank = rank;
    }
    
    @Override
    public void activate(EventObject event) {
        if (action != null && action.isEnabled()) {
            action.actionPerformed(null);
        }
    }
    
    @Override
    public String getCategory() {
        return category;
    }
    
    @Override
    public String getDisplayString() {
        Object name = action.getValue(Action.NAME);
        return name != null ? name.toString() : "";
    }
    
    @Override
    public javax.swing.Icon getIcon() {
        Object icon = action.getValue(Action.SMALL_ICON);
        return icon instanceof javax.swing.Icon ? (javax.swing.Icon) icon : null;
    }
    
    @Override
    public int getRank() {
        return rank;
    }
}