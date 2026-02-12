package com.jgoodies.framework.selection;

import javax.swing.JList;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/selection/ListRowSelectionManager.class */
public final class ListRowSelectionManager extends AbstractRowSelectionManager {
    private final JList<?> list;

    public ListRowSelectionManager(JList<?> list) {
        this.list = list;
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected int getSelectedIndex() {
        return this.list.getSelectedIndex();
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected void setSelectedIndex(int newIndex) {
        this.list.setSelectedIndex(newIndex);
        this.list.ensureIndexIsVisible(newIndex);
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected int getRowCount() {
        return this.list.getModel().getSize();
    }
}
