package com.jgoodies.framework.selection;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/selection/RowSelectionUtils.class */
public final class RowSelectionUtils {
    private RowSelectionUtils() {
    }

    public static void registerKeyboardActions(JList<?> list, JComponent... components) {
        new ListRowSelectionManager(list).registerKeyboardActions(components);
    }

    public static void registerKeyboardActions(JTable table, JComponent... components) {
        new TableRowSelectionManager(table).registerKeyboardActions(components);
    }

    public static void unregisterKeyboardActions(JComponent c) {
        AbstractRowSelectionManager.unregisterKeyboardActions(c);
    }
}
