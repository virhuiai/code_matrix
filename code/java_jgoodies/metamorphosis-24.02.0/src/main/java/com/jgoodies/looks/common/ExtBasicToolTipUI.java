package com.jgoodies.looks.common;

import com.jgoodies.common.base.Strings;
import com.jgoodies.common.swing.internal.AcceleratorUtils;
import com.jgoodies.common.swing.internal.RenderingUtils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicToolTipUI.class */
public final class ExtBasicToolTipUI extends BasicToolTipUI {
    private static ComponentUI sharedInstance;
    private static int padSpaceBetweenStrings = 4;
    private String acceleratorDelimiter;

    public static ComponentUI createUI(JComponent c) {
        if (sharedInstance == null) {
            sharedInstance = new ExtBasicToolTipUI();
        }
        return sharedInstance;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
        if (this.acceleratorDelimiter == null) {
            this.acceleratorDelimiter = "+";
        }
    }

    public void paint(Graphics g, JComponent c) {
        int accelBL;
        JToolTip tip = (JToolTip) c;
        Font font = c.getFont();
        FontMetrics metrics = c.getFontMetrics(font);
        Dimension size = c.getSize();
        g.setColor(c.getForeground());
        String tipText = tip.getTipText();
        if (tipText == null) {
            tipText = "";
        }
        String accelString = getAcceleratorString(tip);
        int accelSpacing = calcAccelSpacing(c, metrics, accelString);
        Insets insets = tip.getInsets();
        Rectangle paintTextR = new Rectangle(insets.left + 3, insets.top, ((size.width - (insets.left + insets.right)) - 6) - accelSpacing, size.height - (insets.top + insets.bottom));
        View v = (View) c.getClientProperty("html");
        if (v != null) {
            v.paint(g, paintTextR);
            accelBL = BasicHTML.getHTMLBaseline(v, paintTextR.width, paintTextR.height);
        } else {
            g.setFont(font);
            RenderingUtils.drawString(tip, g, tipText, paintTextR.x, paintTextR.y + metrics.getAscent());
            accelBL = metrics.getAscent();
        }
        if (!accelString.equals("")) {
            RenderingUtils.drawString(tip, g, accelString, ((((tip.getWidth() - 1) - insets.right) - accelSpacing) + padSpaceBetweenStrings) - 3, paintTextR.y + accelBL);
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);
        String key = getAcceleratorString((JToolTip) c);
        if (!key.equals("")) {
            d.width += calcAccelSpacing(c, c.getFontMetrics(c.getFont()), key);
        }
        return d;
    }

    private static int calcAccelSpacing(JComponent c, FontMetrics fm, String accel) {
        if (accel.equals("")) {
            return 0;
        }
        return padSpaceBetweenStrings + RenderingUtils.stringWidth(c, fm, accel);
    }

    private static boolean isAcceleratorHidden() {
        return Boolean.FALSE != UIManager.get("ToolTip.hideAccelerator");
    }

    private String getAcceleratorString(JToolTip tip) {
        KeyStroke[] keys;
        if (tip == null || isAcceleratorHidden()) {
            return "";
        }
        JComponent comp = tip.getComponent();
        if (!(comp instanceof AbstractButton) || (keys = comp.getInputMap(2).keys()) == null || keys.length == 0) {
            return "";
        }
        String acceleratorText = AcceleratorUtils.getAcceleratorText(comp, keys[0], this.acceleratorDelimiter);
        return Strings.isBlank(acceleratorText) ? acceleratorText : "(" + acceleratorText + ')';
    }
}
