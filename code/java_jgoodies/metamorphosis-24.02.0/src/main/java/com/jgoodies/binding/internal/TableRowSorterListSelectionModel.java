package com.jgoodies.binding.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/internal/TableRowSorterListSelectionModel.class */
public final class TableRowSorterListSelectionModel extends DefaultListSelectionModel {
    private final ListSelectionModel delegate;
    private final JTable table;
    private final ListSelectionListener updateListener = this::onListSelectionChanged;

    public TableRowSorterListSelectionModel(ListSelectionModel listSelectionModel, JTable table) {
        this.delegate = (ListSelectionModel) Preconditions.checkNotNull(listSelectionModel, Messages.MUST_NOT_BE_NULL, "delegate ListSelectionModel");
        this.table = (JTable) Preconditions.checkNotNull(table, Messages.MUST_NOT_BE_NULL, "The table");
        initEventHandling();
    }

    private void initEventHandling() {
        this.delegate.addListSelectionListener(this.updateListener);
        initViewSelectionFromModel();
    }

    private void initViewSelectionFromModel() {
        int firstModelIndex = this.delegate.getMinSelectionIndex();
        int lastModelIndex = this.delegate.getMaxSelectionIndex();
        super.clearSelection();
        for (int modelIndex = firstModelIndex; modelIndex <= lastModelIndex; modelIndex++) {
            int viewIndex = convertIndexToView(modelIndex);
            boolean modelSelected = this.delegate.isSelectedIndex(modelIndex);
            if (modelSelected) {
                super.addSelectionInterval(viewIndex, viewIndex);
            }
        }
    }

    private void updateViewSelectionFromModel(int firstModelIndex, int lastModelIndex) {
        for (int modelIndex = firstModelIndex; modelIndex <= lastModelIndex; modelIndex++) {
            int viewIndex = convertIndexToView(modelIndex);
            boolean modelSelected = this.delegate.isSelectedIndex(modelIndex);
            boolean viewSelected = isSelectedIndex(viewIndex);
            if (modelSelected != viewSelected) {
                if (modelSelected) {
                    super.addSelectionInterval(viewIndex, viewIndex);
                } else {
                    super.removeSelectionInterval(viewIndex, viewIndex);
                }
            }
        }
    }

    public void setSelectionInterval(int index0, int index1) {
        super.setSelectionInterval(index0, index1);
        this.delegate.removeListSelectionListener(this.updateListener);
        try {
            if (getSelectionMode() == 0) {
                this.delegate.setSelectionInterval(0, convertIndexToModel(index1));
            } else {
                this.delegate.setValueIsAdjusting(true);
                this.delegate.clearSelection();
                int start = Math.min(index0, index1);
                int end = Math.max(index0, index1);
                for (int i = start; i <= end; i++) {
                    int modelIndex = convertIndexToModel(i);
                    this.delegate.addSelectionInterval(modelIndex, modelIndex);
                }
                this.delegate.setValueIsAdjusting(false);
            }
        } finally {
            this.delegate.addListSelectionListener(this.updateListener);
        }
    }

    public void addSelectionInterval(int index0, int index1) {
        super.addSelectionInterval(index0, index1);
        this.delegate.removeListSelectionListener(this.updateListener);
        try {
            if (getSelectionMode() == 0) {
                this.delegate.addSelectionInterval(0, convertIndexToModel(index1));
            } else {
                this.delegate.setValueIsAdjusting(true);
                int start = Math.min(index0, index1);
                int end = Math.max(index0, index1);
                for (int i = start; i <= end; i++) {
                    int modelIndex = convertIndexToModel(i);
                    this.delegate.addSelectionInterval(modelIndex, modelIndex);
                }
                this.delegate.setValueIsAdjusting(false);
            }
        } finally {
            this.delegate.addListSelectionListener(this.updateListener);
        }
    }

    public void removeSelectionInterval(int index0, int index1) {
        super.removeSelectionInterval(index0, index1);
        this.delegate.removeListSelectionListener(this.updateListener);
        try {
            if (getSelectionMode() == 0) {
                this.delegate.removeSelectionInterval(0, convertIndexToModel(index1));
            } else {
                this.delegate.setValueIsAdjusting(true);
                int start = Math.min(index0, index1);
                int end = Math.max(index0, index1);
                for (int i = start; i <= end; i++) {
                    int modelIndex = convertIndexToModel(i);
                    this.delegate.removeSelectionInterval(modelIndex, modelIndex);
                }
                this.delegate.setValueIsAdjusting(false);
            }
        } finally {
            this.delegate.addListSelectionListener(this.updateListener);
        }
    }

    public void clearSelection() {
        super.clearSelection();
        this.delegate.removeListSelectionListener(this.updateListener);
        try {
            this.delegate.clearSelection();
        } finally {
            this.delegate.addListSelectionListener(this.updateListener);
        }
    }

    public void insertIndexInterval(int index, int length, boolean before) {
        throw new UnsupportedOperationException("#insertIndexInterval not yet implemented");
    }

    public void removeIndexInterval(int index0, int index1) {
        throw new UnsupportedOperationException("#removeIndexInterval not yet implemented");
    }

    public void setSelectionMode(int selectionMode) {
        this.delegate.setSelectionMode(selectionMode);
    }

    public int getSelectionMode() {
        return this.delegate.getSelectionMode();
    }

    private int convertIndexToModel(int index) {
        if (index == -1) {
            return -1;
        }
        RowSorter<?> rowSorter = this.table.getRowSorter();
        return rowSorter == null ? index : rowSorter.convertRowIndexToModel(index);
    }

    private int convertIndexToView(int index) {
        if (index == -1) {
            return -1;
        }
        RowSorter<?> rowSorter = this.table.getRowSorter();
        return rowSorter == null ? index : rowSorter.convertRowIndexToView(index);
    }

    private void onListSelectionChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting()) {
            return;
        }
        updateViewSelectionFromModel(evt.getFirstIndex(), inBounds(evt.getLastIndex()));
    }

    private int inBounds(int index) {
        int viewRowCount;
        RowSorter<?> rowSorter = this.table.getRowSorter();
        if (rowSorter == null) {
            viewRowCount = this.table.getRowCount();
        } else {
            viewRowCount = rowSorter.getViewRowCount();
        }
        int rowCount = viewRowCount;
        return Math.min(index, rowCount - 1);
    }
}
