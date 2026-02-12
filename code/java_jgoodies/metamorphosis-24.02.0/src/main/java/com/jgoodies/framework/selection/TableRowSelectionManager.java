package com.jgoodies.framework.selection;

import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/selection/TableRowSelectionManager.class */
public final class TableRowSelectionManager extends AbstractRowSelectionManager {
    private final JTable table;

    public TableRowSelectionManager(JTable table) {
        this.table = table;
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected int getSelectedIndex() {
        return getSelectionModel().getMinSelectionIndex();
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected void setSelectedIndex(int newIndex) {
        getSelectionModel().setSelectionInterval(newIndex, newIndex);
        ensureIndexIsVisible(newIndex);
    }

    @Override // com.jgoodies.framework.selection.AbstractRowSelectionManager
    protected int getRowCount() {
        return this.table.getRowCount();
    }

    private void ensureIndexIsVisible(int index) {
        Rectangle rect = this.table.getCellRect(index, 0, true);
        this.table.scrollRectToVisible(rect);
    }

    private ListSelectionModel getSelectionModel() {
        return this.table.getSelectionModel();
    }
}
