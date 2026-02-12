package com.jgoodies.looks.plastic;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticSplitPaneDivider.class */
final class PlasticSplitPaneDivider extends BasicSplitPaneDivider {
    /* JADX INFO: Access modifiers changed from: package-private */
    public PlasticSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
    }

    protected JButton createLeftOneTouchButton() {
        JButton btn = super.createLeftOneTouchButton();
        btn.setOpaque(false);
        return btn;
    }

    protected JButton createRightOneTouchButton() {
        JButton btn = super.createRightOneTouchButton();
        btn.setOpaque(false);
        return btn;
    }

    public void paint(Graphics g) {
        Color bgColor;
        if (this.splitPane.isOpaque() && (bgColor = getBackground()) != null) {
            g.setColor(bgColor);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paint(g);
    }
}
