package com.jgoodies.looks.plastic;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalRadioButtonUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticRadioButtonUI.class */
public final class PlasticRadioButtonUI extends MetalRadioButtonUI {
    private static final PlasticRadioButtonUI INSTANCE = new PlasticRadioButtonUI();

    public static ComponentUI createUI(JComponent b) {
        return INSTANCE;
    }

    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        LookAndFeel.installProperty(b, "iconTextGap", Integer.valueOf(UIManager.getInt("RadioButton.iconTextGap")));
    }

    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new ActiveBasicButtonListener(b);
    }

    protected void paintFocus(Graphics g, Rectangle t, Dimension d) {
        g.setColor(getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, t.x - 1, t.y, t.width + 2, t.height + 1);
    }
}
