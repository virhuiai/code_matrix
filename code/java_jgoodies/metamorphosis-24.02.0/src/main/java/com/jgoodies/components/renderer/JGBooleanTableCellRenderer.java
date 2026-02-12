package com.jgoodies.components.renderer;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.components.JGTable;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/renderer/JGBooleanTableCellRenderer.class */
public final class JGBooleanTableCellRenderer extends DefaultTableCellRenderer {
    private static final String CHECKMARK = "✓";
    private final TableCellRenderer defaultRenderer = new JGTable.BooleanRenderer();
    public static final JGBooleanTableCellRenderer INSTANCE = new JGBooleanTableCellRenderer();
    private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table.isCellEditable(row, column)) {
            JCheckBox tableCellRendererComponent = this.defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected && (tableCellRendererComponent instanceof JCheckBox) && SystemUtils.isLafAqua()) {
                JCheckBox jc = tableCellRendererComponent;
                jc.setBackground(TRANSPARENT_COLOR);
            }
            return tableCellRendererComponent;
        }
        Object v = Boolean.TRUE.equals(value) ? CHECKMARK : "";
        return super.getTableCellRendererComponent(table, v, isSelected, hasFocus, row, column);
    }
}
