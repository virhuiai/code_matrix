package com.jgoodies.components;

import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGStripedTable.class */
public class JGStripedTable<E> extends JGTable<E> {
    public static final String PROPERTY_SHOW_STRIPES = "showStripes";
    public static final String PROPERTY_STRIPES_EVEN_COLOR = "stripesEvenColor";
    public static final String PROPERTY_STRIPES_ODD_COLOR = "stripesOddColor";
    public static final String STRIPES_EVEN_COLOR_KEY = "Table.stripesEvenColor";
    public static final String STRIPES_ODD_COLOR_KEY = "Table.stripesOddColor";
    private boolean showStripes;
    private Color stripesEvenColor;
    private Color stripesOddColor;
    private Border rendererBorder;
    public static final Color DEFAULT_STRIPES_ODD_COLOR = new Color(237, 243, 254);
    private static final Border DEFAULT_RENDERER_BORDER = createDefaultRendererBorder();
    private static final String CUSTOM_CELL_BACKGROUND_KEY = new String("JGoodies.customBackground");

    public JGStripedTable() {
    }

    public JGStripedTable(TableModel dm) {
        super(dm, null, null);
    }

    public JGStripedTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm, null);
    }

    public JGStripedTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public JGStripedTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public JGStripedTable(Vector<?> rowData, Vector<String> columnNames) {
        super(rowData, columnNames);
    }

    public JGStripedTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.JGTable
    public void initializeLocalVars() {
        super.initializeLocalVars();
        setOpaque(false);
        setShowGrid(false);
        setShowStripes(true);
        setIntercellSpacing(new Dimension(0, 0));
        this.rendererBorder = getDefaultRendererBorder();
    }

    protected Border getDefaultRendererBorder() {
        return DEFAULT_RENDERER_BORDER;
    }

    @Override // com.jgoodies.components.JGTable
    protected int computeRowHeight() {
        int tableFontSize = getFont().getSize();
        return tableFontSize + (tableFontSize / 2) + ScreenScaling.toPhysical(4);
    }

    public final boolean getShowStripes() {
        return this.showStripes;
    }

    public final void setShowStripes(boolean newShowStripes) {
        boolean oldShowStripes = getShowStripes();
        this.showStripes = newShowStripes;
        firePropertyChange(PROPERTY_SHOW_STRIPES, oldShowStripes, newShowStripes);
    }

    public static Color getStripesEvenDefaultColor() {
        return UIManager.getColor(STRIPES_EVEN_COLOR_KEY);
    }

    public static void setStripesEvenDefaultColor(Color evenDefaultColor) {
        UIManager.put(STRIPES_EVEN_COLOR_KEY, evenDefaultColor);
    }

    public final Color getStripesEvenColor() {
        return this.stripesEvenColor;
    }

    public final void setStripesEvenColor(Color evenColor) {
        Color oldValue = getStripesEvenColor();
        this.stripesEvenColor = evenColor;
        firePropertyChange(PROPERTY_STRIPES_EVEN_COLOR, oldValue, evenColor);
    }

    public static Color getStripesOddDefaultColor() {
        return UIManager.getColor(STRIPES_ODD_COLOR_KEY);
    }

    public static void setStripesOddDefaultColor(Color oddDefaultColor) {
        UIManager.put(STRIPES_ODD_COLOR_KEY, oddDefaultColor);
    }

    public final Color getStripesOddColor() {
        return this.stripesOddColor;
    }

    public final void setStripesOddColor(Color oddColor) {
        Color oldValue = getStripesOddColor();
        this.stripesOddColor = oddColor;
        firePropertyChange(PROPERTY_STRIPES_ODD_COLOR, oldValue, oddColor);
    }

    public static boolean hasCustomTableCellBackground(JComponent rendererComponent) {
        return Boolean.TRUE.equals(rendererComponent.getClientProperty(CUSTOM_CELL_BACKGROUND_KEY));
    }

    public static void setCustomTableCellBackground(JComponent rendererComponent, boolean customBackground) {
        rendererComponent.putClientProperty(CUSTOM_CELL_BACKGROUND_KEY, Boolean.valueOf(customBackground));
    }

    @Override // com.jgoodies.components.JGTable
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        JComponent prepareRenderer = super.prepareRenderer(renderer, row, column);
        if (prepareRenderer instanceof JComponent) {
            JComponent jc = prepareRenderer;
            jc.setBorder(this.rendererBorder);
            jc.setOpaque(isCellSelected(row, column) || hasCustomTableCellBackground(jc));
        }
        return prepareRenderer;
    }

    public void paint(Graphics g) {
        if (!getShowStripes()) {
            super.paint(g);
            return;
        }
        Color evenColor = lookUpEvenColor();
        Color oddColor = lookUpOddColor();
        int stripeHeight = getRowHeight();
        Rectangle clip = g.getClipBounds();
        int x = clip.x;
        int y = clip.y;
        int w = clip.width;
        int h = clip.height;
        int row = 0;
        int y2 = y + h;
        if (y != 0) {
            int diff = y % stripeHeight;
            row = y / stripeHeight;
            y -= diff;
        }
        while (y < y2) {
            Color color = row % 2 == 0 ? evenColor : oddColor;
            g.setColor(color);
            g.fillRect(x, y, w, stripeHeight);
            y += stripeHeight;
            row++;
        }
        super.paint(g);
    }

    private Color lookUpEvenColor() {
        Color color = getStripesEvenColor();
        if (color != null) {
            return color;
        }
        Color color2 = getStripesEvenDefaultColor();
        if (color2 != null) {
            return color2;
        }
        return getBackground();
    }

    private Color lookUpOddColor() {
        Color color = getStripesOddColor();
        if (color != null) {
            return color;
        }
        Color color2 = getStripesOddDefaultColor();
        if (color2 != null) {
            return color2;
        }
        return DEFAULT_STRIPES_ODD_COLOR;
    }

    private static Border createDefaultRendererBorder() {
        return ScreenScaling.physicalEmptyBorder(1, 6, 1, 6);
    }
}
