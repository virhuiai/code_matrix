package com.jgoodies.framework.table;

import com.jgoodies.framework.selection.AbstractRowSelectionManager;
import com.jgoodies.framework.selection.TableRowSelectionManager;
import javax.swing.JComponent;
import javax.swing.JTable;

@Deprecated
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableSelectionUtils.class */
public final class TableSelectionUtils {
    private TableSelectionUtils() {
    }

    public static void registerKeyboardActions(JComponent c, JTable table) {
        new TableRowSelectionManager(table).registerKeyboardActions(c);
    }

    public static void unregisterKeyboardActions(JComponent c) {
        AbstractRowSelectionManager.unregisterKeyboardActions(c);
    }
}
