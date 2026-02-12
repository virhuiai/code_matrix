package com.jgoodies.looks.windows;

import com.jgoodies.common.swing.ScreenScaling;
import com.sun.java.swing.plaf.windows.WindowsTableHeaderUI;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsXPTableHeaderUI.class */
public final class WindowsXPTableHeaderUI extends WindowsTableHeaderUI {
    private TableCellRenderer xpRenderer;

    public static ComponentUI createUI(JComponent h) {
        return new WindowsXPTableHeaderUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        this.xpRenderer = this.header.getDefaultRenderer();
    }

    public void uninstallUI(JComponent c) {
        this.xpRenderer = null;
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        TableColumnModel cm = this.header.getColumnModel();
        if (cm.getColumnCount() <= 0) {
            return;
        }
        boolean ltr = this.header.getComponentOrientation().isLeftToRight();
        Rectangle clip = g.getClipBounds();
        Point left = clip.getLocation();
        Point right = new Point((clip.x + clip.width) - 1, clip.y);
        int cMin = this.header.columnAtPoint(ltr ? left : right);
        int cMax = this.header.columnAtPoint(ltr ? right : left);
        if (cMin == -1) {
            cMin = 0;
        }
        if (cMax == -1) {
            cMax = cm.getColumnCount() - 1;
        }
        TableColumn draggedColumn = this.header.getDraggedColumn();
        Rectangle cellRect = this.header.getHeaderRect(cMin);
        if (ltr) {
            for (int column = cMin; column <= cMax; column++) {
                TableColumn aColumn = cm.getColumn(column);
                int columnWidth = aColumn.getWidth();
                cellRect.width = columnWidth;
                if (aColumn != draggedColumn) {
                    paintCell(g, cellRect, column);
                }
                cellRect.x += columnWidth;
            }
        } else {
            for (int column2 = cMax; column2 >= cMin; column2--) {
                TableColumn aColumn2 = cm.getColumn(column2);
                int columnWidth2 = aColumn2.getWidth();
                cellRect.width = columnWidth2;
                if (aColumn2 != draggedColumn) {
                    paintCell(g, cellRect, column2);
                }
                cellRect.x += columnWidth2;
            }
        }
        if (draggedColumn != null) {
            int draggedColumnIndex = viewIndexForColumn(draggedColumn);
            Rectangle draggedCellRect = this.header.getHeaderRect(draggedColumnIndex);
            g.setColor(this.header.getParent().getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);
            draggedCellRect.x += this.header.getDraggedDistance();
            g.setColor(this.header.getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height);
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }
        this.rendererPane.removeAll();
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        TableColumn aColumn = this.header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = this.header.getDefaultRenderer();
        }
        JTable table = this.header.getTable();
        JComponent tableCellRendererComponent = this.xpRenderer.getTableCellRendererComponent(table, (Object) null, false, false, -1, columnIndex);
        JComponent tableCellRendererComponent2 = renderer.getTableCellRendererComponent(table, aColumn.getHeaderValue(), false, false, -1, columnIndex);
        if (!ScreenScaling.isScale100() && (tableCellRendererComponent instanceof JComponent)) {
            JComponent jc = tableCellRendererComponent;
            EmptyBorder border = jc.getBorder();
            if (border instanceof EmptyBorder) {
                EmptyBorder eb = border;
                jc.setBorder(ScreenScaling.toPhysical(eb));
            }
        }
        if (tableCellRendererComponent2 != tableCellRendererComponent) {
            this.rendererPane.add(tableCellRendererComponent2);
            if (!tableCellRendererComponent2.isOpaque()) {
                this.rendererPane.paintComponent(g, tableCellRendererComponent, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
                if ((tableCellRendererComponent2 instanceof JComponent) && (tableCellRendererComponent instanceof JComponent)) {
                    tableCellRendererComponent2.setBorder(tableCellRendererComponent.getBorder());
                }
            }
        }
        this.rendererPane.paintComponent(g, tableCellRendererComponent2, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = this.header.getColumnModel();
        for (int column = cm.getColumnCount() - 1; column >= 0; column--) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }
}
