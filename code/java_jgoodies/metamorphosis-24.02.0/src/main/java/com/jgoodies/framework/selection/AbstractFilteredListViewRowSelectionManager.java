package com.jgoodies.framework.selection;

import com.jgoodies.common.jsdl.list.FilteredListViewModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/selection/AbstractFilteredListViewRowSelectionManager.class */
public abstract class AbstractFilteredListViewRowSelectionManager extends AbstractRowSelectionManager {
    private final FilteredListViewModel<?> model;

    protected abstract void ensureIndexIsVisble(int i);

    public AbstractFilteredListViewRowSelectionManager(FilteredListViewModel<?> model) {
        this.model = model;
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected int getSelectedIndex() {
        return this.model.getMinSelectionIndex();
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected void setSelectedIndex(int newIndex) {
        this.model.setSelectedIndex(newIndex);
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected int getRowCount() {
        return this.model.filteredSize();
    }
}
