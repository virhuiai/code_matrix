package com.jgoodies.binding.adapter;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/AbstractTableAdapter.class */
public abstract class AbstractTableAdapter<E> extends AbstractTableModel implements ListModelBindable<E> {
    private final ListDataListener changeHandler;
    private final String[] columnNames;
    private ListModel<E> listModel;

    public AbstractTableAdapter() {
        this(null, (String[]) null);
    }

    public AbstractTableAdapter(ListModel<E> listModel) {
        this(listModel, (String[]) null);
    }

    public AbstractTableAdapter(String... columnNames) {
        this(null, columnNames);
    }

    public AbstractTableAdapter(ListModel<E> listModel, String... columnNames) {
        this.changeHandler = createChangeHandler();
        setListModel(listModel);
        if (columnNames == null || columnNames.length == 0) {
            this.columnNames = null;
        } else {
            this.columnNames = new String[columnNames.length];
            System.arraycopy(columnNames, 0, this.columnNames, 0, columnNames.length);
        }
    }

    @Override // com.jgoodies.binding.adapter.ListModelBindable
    public ListModel<E> getListModel() {
        return this.listModel;
    }

    @Override // com.jgoodies.binding.adapter.ListModelBindable
    public void setListModel(ListModel<E> newListModel) {
        ListModel<E> oldListModel = getListModel();
        if (oldListModel == newListModel) {
            return;
        }
        if (oldListModel != null) {
            oldListModel.removeListDataListener(this.changeHandler);
        }
        this.listModel = newListModel;
        fireTableDataChanged();
        if (newListModel != null) {
            newListModel.addListDataListener(this.changeHandler);
        }
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public String getColumnName(int columnIndex) {
        return this.columnNames[columnIndex];
    }

    public final int getRowCount() {
        if (this.listModel == null) {
            return 0;
        }
        return this.listModel.getSize();
    }

    public final E getRow(int i) {
        return (E) this.listModel.getElementAt(i);
    }

    protected ListDataListener createChangeHandler() {
        return new ListDataChangeHandler();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/AbstractTableAdapter$ListDataChangeHandler.class */
    public final class ListDataChangeHandler implements ListDataListener {
        private ListDataChangeHandler() {
        }

        public void intervalAdded(ListDataEvent evt) {
            AbstractTableAdapter.this.fireTableRowsInserted(evt.getIndex0(), evt.getIndex1());
        }

        public void intervalRemoved(ListDataEvent evt) {
            AbstractTableAdapter.this.fireTableRowsDeleted(evt.getIndex0(), evt.getIndex1());
        }

        public void contentsChanged(ListDataEvent evt) {
            AbstractTableAdapter.this.fireTableRowsUpdated(evt.getIndex0(), evt.getIndex1());
        }
    }
}
