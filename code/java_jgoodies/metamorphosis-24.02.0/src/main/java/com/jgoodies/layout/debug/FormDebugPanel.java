package com.jgoodies.layout.debug;

import com.jgoodies.layout.layout.FormLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/debug/FormDebugPanel.class */
public class FormDebugPanel extends JPanel {
    public static boolean paintRowsDefault = true;
    private static final Color DEFAULT_GRID_COLOR = Color.red;
    private boolean paintInBackground;
    private boolean paintDiagonals;
    private boolean paintRows;
    private Color gridColor;

    public FormDebugPanel() {
        this(null);
    }

    public FormDebugPanel(FormLayout layout) {
        this(layout, false, false);
    }

    public FormDebugPanel(boolean paintInBackground, boolean paintDiagonals) {
        this(null, paintInBackground, paintDiagonals);
    }

    public FormDebugPanel(FormLayout layout, boolean paintInBackground, boolean paintDiagonals) {
        super(layout);
        this.paintRows = paintRowsDefault;
        this.gridColor = DEFAULT_GRID_COLOR;
        setPaintInBackground(paintInBackground);
        setPaintDiagonals(paintDiagonals);
        setGridColor(DEFAULT_GRID_COLOR);
    }

    public void setPaintInBackground(boolean b) {
        this.paintInBackground = b;
    }

    public void setPaintDiagonals(boolean b) {
        this.paintDiagonals = b;
    }

    public void setPaintRows(boolean b) {
        this.paintRows = b;
    }

    public void setGridColor(Color color) {
        this.gridColor = color;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.paintInBackground) {
            paintGrid(g);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (!this.paintInBackground) {
            paintGrid(g);
        }
    }

    private void paintGrid(Graphics g) {
        if (!(getLayout() instanceof FormLayout)) {
            return;
        }
        FormLayout.LayoutInfo layoutInfo = FormDebugUtils.getLayoutInfo(this);
        int left = layoutInfo.getX();
        int top = layoutInfo.getY();
        int width = layoutInfo.getWidth();
        int height = layoutInfo.getHeight();
        g.setColor(this.gridColor);
        int last = layoutInfo.columnOrigins.length - 1;
        int col = 0;
        while (col <= last) {
            boolean firstOrLast = col == 0 || col == last;
            int x = layoutInfo.columnOrigins[col];
            int start = firstOrLast ? 0 : top;
            int stop = firstOrLast ? getHeight() : top + height;
            for (int i = start; i < stop; i += 5) {
                int length = Math.min(3, stop - i);
                g.fillRect(x, i, 1, length);
            }
            col++;
        }
        int last2 = layoutInfo.rowOrigins.length - 1;
        int row = 0;
        while (row <= last2) {
            boolean firstOrLast2 = row == 0 || row == last2;
            int y = layoutInfo.rowOrigins[row];
            int start2 = firstOrLast2 ? 0 : left;
            int stop2 = firstOrLast2 ? getWidth() : left + width;
            if (firstOrLast2 || this.paintRows) {
                for (int i2 = start2; i2 < stop2; i2 += 5) {
                    int length2 = Math.min(3, stop2 - i2);
                    g.fillRect(i2, y, length2, 1);
                }
            }
            row++;
        }
        if (this.paintDiagonals) {
            g.drawLine(left, top, left + width, top + height);
            g.drawLine(left, top + height, left + width, top);
        }
    }

    public boolean isOptimizedDrawingEnabled() {
        return false;
    }
}
