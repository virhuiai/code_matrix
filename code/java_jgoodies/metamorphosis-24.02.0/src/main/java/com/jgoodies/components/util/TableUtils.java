package com.jgoodies.components.util;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.components.JGTable;
import com.jgoodies.layout.layout.BoundedSize;
import com.jgoodies.layout.layout.ColumnSpec;
import com.jgoodies.layout.layout.ConstantSize;
import com.jgoodies.layout.layout.FormSpec;
import com.jgoodies.layout.layout.PrototypeSize;
import com.jgoodies.layout.layout.Size;
import java.awt.Container;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/TableUtils.class */
public final class TableUtils {
    private TableUtils() {
    }

    public static void resizeColumnsToPreferredWidth(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        TableModel model = table.getModel();
        int columnCount = model.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            TableColumn column = columnModel.getColumn(i);
            int preferredWidth = getMaximumColumnWidth(model, i);
            column.setPreferredWidth(Math.max(ScreenScaling.toPhysical(50), preferredWidth));
        }
    }

    private static int getMaximumColumnWidth(TableModel model, int column) {
        int maximumCellWidth = 0;
        int rows = model.getRowCount();
        for (int i = 0; i < rows; i++) {
            Object value = model.getValueAt(i, column);
            String cellString = value == null ? "" : value.toString();
            int cellWidth = new JLabel(cellString).getPreferredSize().width;
            if (cellWidth > maximumCellWidth) {
                maximumCellWidth = cellWidth;
            }
        }
        return maximumCellWidth + ScreenScaling.toPhysical(6);
    }

    public static void configureColumns(JTable table, String encodedColumnSpecs) {
        Preconditions.checkNotNull(encodedColumnSpecs, Messages.MUST_NOT_BE_NULL, "column specs");
        ColumnSpec[] specs = ColumnSpec.decodeSpecs(encodedColumnSpecs);
        configureColumns(table, (List<ColumnSpec>) Arrays.asList(specs));
    }

    public static void configureColumns(JTable table, List<ColumnSpec> specs) {
        FormSpec.DefaultAlignment alignment;
        Preconditions.checkNotNull(table, Messages.MUST_NOT_BE_NULL, "table");
        Preconditions.checkNotNull(specs, Messages.MUST_NOT_BE_NULL, "column specs");
        int columnCount = specs.size();
        Preconditions.checkArgument(table.getColumnCount() == columnCount, "Table column specifications size %d  must match the table column count %d.", Integer.valueOf(columnCount), Integer.valueOf(table.getColumnCount()));
        int oldMode = table.getAutoResizeMode();
        table.setAutoResizeMode(0);
        int[] alignmentHints = new int[specs.size()];
        boolean hasTrueAlignmentHint = false;
        for (int i = 0; i < columnCount; i++) {
            ColumnSpec spec = specs.get(i);
            TableColumn column = table.getColumnModel().getColumn(i);
            if (spec == null) {
                alignment = ColumnSpec.DEFAULT;
            } else {
                Size size = spec.getSize();
                if (size instanceof ConstantSize) {
                    configureColumnWidth(table, column, (ConstantSize) size);
                } else if (size instanceof BoundedSize) {
                    configureColumnWidth(table, column, (BoundedSize) size);
                } else if (size instanceof PrototypeSize) {
                    configureColumnWidth(table, column, (PrototypeSize) size);
                }
                alignment = spec.getDefaultAlignment();
                hasTrueAlignmentHint = hasTrueAlignmentHint || spec.getDefaultAlignmentExplictlySet();
            }
            alignmentHints[i] = horizontalAlignment(alignment);
        }
        table.setAutoResizeMode(oldMode);
        if (hasTrueAlignmentHint) {
            setColumnAlignmentHints(table, alignmentHints);
        }
    }

    private static void configureColumnWidth(JTable table, TableColumn column, ConstantSize size) {
        int width = pixelSize((Container) table, size);
        column.setMinWidth(width);
        column.setPreferredWidth(width);
        column.setMaxWidth(width);
        column.setResizable(false);
    }

    private static void configureColumnWidth(JTable table, TableColumn column, PrototypeSize size) {
        int width = pixelSize((Container) table, size);
        column.setMinWidth(width);
        column.setPreferredWidth(width);
        column.setMaxWidth(width);
        column.setResizable(false);
    }

    private static void configureColumnWidth(JTable table, TableColumn column, BoundedSize size) {
        if (size.getLowerBound() != null) {
            int minWidth = pixelSize((Container) table, size.getLowerBound());
            column.setMinWidth(minWidth);
        }
        if (size.getUpperBound() != null) {
            int maxWidth = pixelSize((Container) table, size.getUpperBound());
            column.setMaxWidth(maxWidth);
        }
        Size basis = size.getBasis();
        if ((basis instanceof ConstantSize) || (basis instanceof PrototypeSize)) {
            int prefWidth = pixelSize((Container) table, basis);
            column.setPreferredWidth(prefWidth);
        }
        column.setResizable(true);
    }

    private static int pixelSize(Container container, Size size) {
        if (size instanceof ConstantSize) {
            return pixelSize(container, (ConstantSize) size);
        }
        if (size instanceof PrototypeSize) {
            return pixelSize(container, (PrototypeSize) size);
        }
        throw new IllegalArgumentException("Cannot convert the given size to pixels:" + size);
    }

    private static int pixelSize(Container container, ConstantSize size) {
        return size.maximumSize(container, null, null, null, null, null, true);
    }

    private static int pixelSize(Container container, PrototypeSize size) {
        return (int) (ScreenScaling.toPhysical(14) + size.computeWidth(container) + (size.getPrototype().length() * 0.49f));
    }

    private static int horizontalAlignment(FormSpec.DefaultAlignment alignment) {
        if (alignment == ColumnSpec.LEFT) {
            return 2;
        }
        if (alignment == ColumnSpec.CENTER) {
            return 0;
        }
        if (alignment == ColumnSpec.RIGHT) {
            return 4;
        }
        if (alignment == ColumnSpec.FILL) {
            return 10;
        }
        if (alignment == ColumnSpec.NONE) {
            return -1;
        }
        return -1;
    }

    private static void setColumnAlignmentHints(JTable table, int[] columnAlignmentHints) {
        if (table instanceof JGTable) {
            ((JGTable) table).setColumnAlignmentHints(columnAlignmentHints);
        } else {
            table.putClientProperty(JGTable.COLUMN_ALIGNMENT_HINTS_KEY, columnAlignmentHints);
        }
    }
}
