package com.jgoodies.components;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTable.class */
public class JGTable<E> extends JTable {
    public static final String COLUMN_ALIGNMENT_HINTS_KEY = "JGTable.columnAlignmentHints";

    public JGTable() {
    }

    public JGTable(TableModel dm) {
        super(dm, (TableColumnModel) null, (ListSelectionModel) null);
    }

    public JGTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm, (ListSelectionModel) null);
    }

    public JGTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public JGTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public JGTable(Vector rowData, Vector<String> columnNames) {
        super(rowData, columnNames);
    }

    public JGTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initializeLocalVars() {
        super.initializeLocalVars();
        setFillsViewportHeight(true);
        setPreferredScrollableViewportSize(ScreenScaling.physicalDimension(450, 400));
        setDefaultRenderer(Boolean.class, new BooleanRenderer());
        setDefaultEditor(Boolean.class, new BooleanEditor());
    }

    public void updateUI() {
        configureEnclosingScrollPane();
        super.updateUI();
        setRowHeight(computeRowHeight());
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        JLabel prepareRenderer = super.prepareRenderer(renderer, row, column);
        if (prepareRenderer instanceof JLabel) {
            JLabel label = prepareRenderer;
            int alignment = getColumnAlignmentHint(convertColumnIndexToModel(column));
            if (alignment != -1) {
                label.setHorizontalAlignment(alignment);
            }
        }
        return prepareRenderer;
    }

    protected int computeRowHeight() {
        int tableFontSize = getFont().getSize();
        int rowHeight = ((tableFontSize + (tableFontSize / 2)) + getRowMargin()) - 1;
        return Math.max(ScreenScaling.toPhysical(18), rowHeight);
    }

    public int[] getColumnAlignmentHints() {
        return (int[]) getClientProperty(COLUMN_ALIGNMENT_HINTS_KEY);
    }

    public void setColumnAlignmentHints(int[] alignmentHints) {
        if (alignmentHints != null) {
            for (int i = 0; i < alignmentHints.length; i++) {
                int hint = alignmentHints[i];
                if (hint != -1 && hint != 2 && hint != 0 && hint != 4 && hint != 10 && hint != 11) {
                    throw new IllegalArgumentException("The hint at index " + i + " must be one of: -1, JLabel.LEFT, JLabel.CENTER, JLabel.RIGHT, JLabel.LEADING, JLabel.TRAILING.");
                }
            }
        }
        putClientProperty(COLUMN_ALIGNMENT_HINTS_KEY, alignmentHints);
    }

    protected final int getColumnAlignmentHint(int columnIndex) {
        int[] hints = getColumnAlignmentHints();
        if (hints == null) {
            return -1;
        }
        if (hints.length == getColumnCount()) {
            return hints[columnIndex];
        }
        throw new IndexOutOfBoundsException(String.format("The length of the column alignment hints (%1$d) must match the table column count (%2$d).", Integer.valueOf(hints.length), Integer.valueOf(getColumnCount())));
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTable$BooleanRenderer.class */
    public static class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
        private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
        private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

        public BooleanRenderer() {
            setHorizontalAlignment(0);
            setBorderPainted(true);
            setBorderPaintedFlat(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                if (SystemUtils.isLafAqua()) {
                    super.setBackground(TRANSPARENT_COLOR);
                } else {
                    setBackground(table.getBackground());
                }
            }
            setSelected(value != null && ((Boolean) value).booleanValue());
            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            } else {
                setBorder(NO_FOCUS_BORDER);
            }
            return this;
        }

        public void validate() {
        }

        public void revalidate() {
        }

        public void repaint(long tm, int x, int y, int width, int height) {
        }

        public void repaint(Rectangle r) {
        }

        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            if ("text".equals(propertyName)) {
                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTable$BooleanEditor.class */
    public static class BooleanEditor extends DefaultCellEditor {
        public BooleanEditor() {
            super(new JCheckBox());
            JCheckBox checkBox = getComponent();
            checkBox.setHorizontalAlignment(0);
            checkBox.setBorderPaintedFlat(true);
        }
    }
}
