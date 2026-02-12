package com.jgoodies.looks.plastic;

import com.jgoodies.common.swing.internal.RenderingUtils;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticToggleButtonUI.class */
public class PlasticToggleButtonUI extends MetalToggleButtonUI {
    private static final PlasticToggleButtonUI INSTANCE = new PlasticToggleButtonUI();
    protected static final String HTML_KEY = "html";
    private boolean borderPaintsFocus;
    private Insets focusInsets;
    private Color disabledSelectedTextColor;

    public static ComponentUI createUI(JComponent b) {
        return INSTANCE;
    }

    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        this.borderPaintsFocus = Boolean.TRUE.equals(UIManager.get(getPropertyPrefix() + "borderPaintsFocus"));
        this.focusInsets = UIManager.getInsets(getPropertyPrefix() + "focusInsets");
        this.disabledSelectedTextColor = UIManager.getColor(getPropertyPrefix() + "disabledSelectedText");
    }

    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new ActiveBasicButtonListener(b);
    }

    public void update(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        if (c.isOpaque()) {
            if (isToolBarButton(b)) {
                c.setOpaque(false);
            } else if (b.isContentAreaFilled()) {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                if (is3D(b)) {
                    Rectangle r = new Rectangle(1, 1, c.getWidth() - 2, c.getHeight() - 1);
                    PlasticUtils.add3DEffekt(g, r);
                }
            }
        }
        paint(g, c);
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        if (this.borderPaintsFocus) {
            return;
        }
        int x = this.focusInsets.left;
        int y = this.focusInsets.top;
        int w = (b.getWidth() - x) - this.focusInsets.right;
        int h = (b.getHeight() - y) - this.focusInsets.bottom;
        g.setColor(getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
    }

    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        Dimension size = b.getSize();
        FontMetrics fm = b.getFontMetrics(b.getFont());
        Insets i = c.getInsets();
        Rectangle viewRect = new Rectangle(size);
        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= i.right + viewRect.x;
        viewRect.height -= i.bottom + viewRect.y;
        Rectangle iconRect = new Rectangle();
        Rectangle textRect = new Rectangle();
        Font f = c.getFont();
        g.setFont(f);
        String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), b.getIcon(), b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect, iconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());
        g.setColor(b.getBackground());
        if ((model.isArmed() && model.isPressed()) || model.isSelected()) {
            paintButtonPressed(g, b);
        }
        if (b.getIcon() != null) {
            paintIcon(g, b, iconRect);
        }
        if (text != null && !text.equals("")) {
            View v = (View) c.getClientProperty(HTML_KEY);
            if (v != null) {
                v.paint(g, textRect);
            } else {
                paintText(g, c, textRect, text);
            }
        }
        if (b.isFocusPainted() && b.hasFocus()) {
            paintFocus(g, b, viewRect, textRect, iconRect);
        }
    }

    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        FontMetrics fm = b.getFontMetrics(g.getFont());
        int mnemIndex = b.getDisplayedMnemonicIndex();
        if (model.isEnabled()) {
            g.setColor(b.getForeground());
        } else if (model.isSelected()) {
            if (this.disabledSelectedTextColor != null) {
                g.setColor(this.disabledSelectedTextColor);
            } else {
                g.setColor(c.getBackground());
            }
        } else {
            g.setColor(getDisabledTextColor());
        }
        RenderingUtils.drawStringUnderlineCharAt(c, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
    }

    protected boolean isToolBarButton(AbstractButton b) {
        Container parent = b.getParent();
        return parent != null && ((parent instanceof JToolBar) || (parent.getParent() instanceof JToolBar));
    }

    protected boolean is3D(AbstractButton b) {
        ButtonModel model = b.getModel();
        return PlasticUtils.is3D("ToggleButton.") && b.isBorderPainted() && model.isEnabled() && !model.isPressed();
    }
}
