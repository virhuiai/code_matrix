package com.jgoodies.framework.table;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.components.util.TableUtils;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableColumns.class */
public final class TableColumns {
    private final JTable table;
    private TableColumn[] columns;

    private TableColumns(JTable table) {
        this.table = table;
    }

    public static TableColumns on(JTable table) {
        return new TableColumns(table);
    }

    public TableColumns layout(String columnLayoutSpecification, Object... args) {
        TableUtils.configureColumns(this.table, Strings.get(columnLayoutSpecification, args));
        return this;
    }

    public TableColumns persistAt(String columnLayoutPreferencesId) {
        TableStateUtils.restoreNowAndStoreOnChange(this.table, columnLayoutPreferencesId);
        return this;
    }

    public TableColumnRendererConfiguration render(int columnModelIndex) {
        return new TableColumnRendererConfiguration(this, getColumn(columnModelIndex));
    }

    public TableColumnEditorConfiguration edit(int columnModelIndex) {
        return new TableColumnEditorConfiguration(this, getColumn(columnModelIndex));
    }

    private TableColumn getColumn(int columnIndex) {
        if (this.columns == null) {
            TableColumnModel columnModel = this.table.getColumnModel();
            int columnCount = columnModel.getColumnCount();
            this.columns = new TableColumn[columnCount];
            for (int i = 0; i < columnCount; i++) {
                TableColumn column = columnModel.getColumn(i);
                this.columns[column.getModelIndex()] = column;
            }
        }
        return this.columns[columnIndex];
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableColumns$TableColumnRendererConfiguration.class */
    public static final class TableColumnRendererConfiguration {
        private final TableColumns configurator;
        private final TableColumn column;

        TableColumnRendererConfiguration(TableColumns configurator, TableColumn column) {
            this.configurator = configurator;
            this.column = column;
        }

        public TableColumns with(TableCellRenderer cellRenderer) {
            Preconditions.checkNotNull(cellRenderer, Messages.MUST_NOT_BE_NULL, "table cell renderer");
            this.column.setCellRenderer(cellRenderer);
            return this.configurator;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableColumns$TableColumnEditorConfiguration.class */
    public static final class TableColumnEditorConfiguration {
        private final TableColumns configurator;
        private final TableColumn column;

        TableColumnEditorConfiguration(TableColumns configurator, TableColumn column) {
            this.configurator = configurator;
            this.column = column;
        }

        public TableColumns with(TableCellEditor cellEditor) {
            Preconditions.checkNotNull(cellEditor, Messages.MUST_NOT_BE_NULL, "table cell editor");
            this.column.setCellEditor(cellEditor);
            return this.configurator;
        }
    }
}
