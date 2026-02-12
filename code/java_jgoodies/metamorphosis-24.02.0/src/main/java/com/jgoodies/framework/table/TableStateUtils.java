package com.jgoodies.framework.table;

import com.jgoodies.application.Application;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.prefs.Preferences;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableStateUtils.class */
public final class TableStateUtils {
    private static TableStateHandler handler;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableStateUtils$TableStateHandler.class */
    public interface TableStateHandler {
        void store(Preferences preferences, JTable jTable);

        void restore(JTable jTable, Preferences preferences);

        void registerListeners(JTable jTable, Preferences preferences);
    }

    private TableStateUtils() {
    }

    public static TableStateHandler getHandler() {
        if (handler == null) {
            handler = new DefaultTableStateHandler();
        }
        return handler;
    }

    public static void setHandler(TableStateHandler newHandler) {
        Preconditions.checkNotNull(newHandler, Messages.MUST_NOT_BE_NULL, "new handler");
        handler = newHandler;
    }

    public static void restoreNowAndStoreOnChange(JTable table, String id) {
        Preferences userRoot = Application.getInstance().getContext().getUserPreferences();
        Preferences tableStateNode = userRoot.node("tables");
        restoreNowAndStoreOnChange(table, tableStateNode.node(id));
    }

    public static void restoreNowAndStoreOnChange(JTable table, Preferences prefs) {
        getHandler().restore(table, prefs);
        getHandler().registerListeners(table, prefs);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableStateUtils$DefaultTableStateHandler.class */
    public static class DefaultTableStateHandler implements TableStateHandler {
        @Override // com.jgoodies.framework.table.TableStateUtils.TableStateHandler
        public void store(Preferences prefs, JTable table) {
            Preconditions.checkNotNull(prefs, Messages.MUST_NOT_BE_NULL, "preferences");
            Preconditions.checkNotNull(table, Messages.MUST_NOT_BE_NULL, "table");
            TableColumnModel columnModel = table.getColumnModel();
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                TableColumn column = columnModel.getColumn(i);
                int modelIndex = column.getModelIndex();
                prefs.putInt(keyColumnIndex(i), modelIndex);
                prefs.putInt(keyColumnWidth(i), column.getWidth());
            }
        }

        @Override // com.jgoodies.framework.table.TableStateUtils.TableStateHandler
        public void restore(JTable table, Preferences prefs) {
            Preconditions.checkNotNull(prefs, Messages.MUST_NOT_BE_NULL, "preferences");
            Preconditions.checkNotNull(table, Messages.MUST_NOT_BE_NULL, "table");
            TableColumnModel columnModel = table.getColumnModel();
            int columnCount = columnModel.getColumnCount();
            TableColumn[] backup = new TableColumn[columnCount];
            for (int i = columnCount - 1; i >= 0; i--) {
                backup[i] = columnModel.getColumn(i);
                columnModel.removeColumn(columnModel.getColumn(i));
            }
            for (int i2 = 0; i2 < columnCount; i2++) {
                int modelIndex = prefs.getInt(keyColumnIndex(i2), i2);
                TableColumn column = backup[modelIndex];
                columnModel.addColumn(column);
                column.setModelIndex(modelIndex);
                int width = prefs.getInt(keyColumnWidth(i2), -1);
                if (width != -1) {
                    column.setPreferredWidth(width);
                }
            }
        }

        @Override // com.jgoodies.framework.table.TableStateUtils.TableStateHandler
        public void registerListeners(JTable table, Preferences prefs) {
            table.getColumnModel().addColumnModelListener(new TableColumnChangeHandler(prefs, table));
        }

        private static String keyColumnIndex(int index) {
            return "col[" + index + "].index";
        }

        private static String keyColumnWidth(int index) {
            return "col[" + index + "].width";
        }

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableStateUtils$DefaultTableStateHandler$TableColumnChangeHandler.class */
        private static final class TableColumnChangeHandler implements TableColumnModelListener {
            private final Preferences prefs;
            private final JTable table;

            TableColumnChangeHandler(Preferences prefs, JTable table) {
                this.prefs = prefs;
                this.table = table;
            }

            public void columnMarginChanged(ChangeEvent evt) {
                TableStateUtils.getHandler().store(this.prefs, this.table);
            }

            public void columnMoved(TableColumnModelEvent evt) {
                TableStateUtils.getHandler().store(this.prefs, this.table);
            }

            public void columnAdded(TableColumnModelEvent evt) {
                TableStateUtils.getHandler().store(this.prefs, this.table);
            }

            public void columnRemoved(TableColumnModelEvent evt) {
                TableStateUtils.getHandler().store(this.prefs, this.table);
            }

            public void columnSelectionChanged(ListSelectionEvent evt) {
            }
        }
    }
}
