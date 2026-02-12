package com.jgoodies.components.plaf.basic;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.components.JGHyperlink;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/basic/BasicHyperlinkUI.class */
public class BasicHyperlinkUI extends BasicButtonUI {
    private static BasicHyperlinkUI hyperlinkUI;
    protected static final Rectangle VIEW_RECT = new Rectangle();
    protected static final Rectangle TEXT_RECT = new Rectangle();
    protected static final Rectangle ICON_RECT = new Rectangle();

    public static ComponentUI createUI(JComponent x) {
        if (hyperlinkUI == null) {
            hyperlinkUI = new BasicHyperlinkUI();
        }
        return hyperlinkUI;
    }

    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        this.defaultTextShiftOffset = 0;
        b.setHorizontalAlignment(10);
        b.setBorder((Border) null);
        JGHyperlink link = (JGHyperlink) b;
        if (link.getForeground() == null || (link.getForeground() instanceof ColorUIResource)) {
            link.setForeground(UIManager.getColor("Hyperlink.unvisited.foreground"));
        }
        if (link.getVisitedForeground() == null || (link.getVisitedForeground() instanceof ColorUIResource)) {
            link.setVisitedForeground(UIManager.getColor("Hyperlink.visited.foreground"));
        }
    }

    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new Handler((JGHyperlink) b);
    }

    public void paint(Graphics g, JComponent c) {
        JGHyperlink b = (JGHyperlink) c;
        ButtonModel model = b.getModel();
        Font f = b.getFont();
        g.setFont(f);
        FontMetrics fm = b.getFontMetrics(f);
        Insets i = b.getInsets();
        VIEW_RECT.x = i.left;
        VIEW_RECT.y = i.top;
        VIEW_RECT.width = b.getWidth() - (i.right + VIEW_RECT.x);
        VIEW_RECT.height = b.getHeight() - (i.bottom + VIEW_RECT.y);
        Rectangle rectangle = TEXT_RECT;
        Rectangle rectangle2 = TEXT_RECT;
        Rectangle rectangle3 = TEXT_RECT;
        TEXT_RECT.height = 0;
        rectangle3.width = 0;
        rectangle2.y = 0;
        rectangle.x = 0;
        Rectangle rectangle4 = ICON_RECT;
        Rectangle rectangle5 = ICON_RECT;
        Rectangle rectangle6 = ICON_RECT;
        ICON_RECT.height = 0;
        rectangle6.width = 0;
        rectangle5.y = 0;
        rectangle4.x = 0;
        Icon icon = getIcon(b);
        String text = SwingUtilities.layoutCompoundLabel(b, fm, b.getText(), icon, b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), VIEW_RECT, ICON_RECT, TEXT_RECT, b.getIconTextGap());
        clearTextShiftOffset();
        if (model.isArmed() && model.isPressed()) {
            paintButtonPressed(g, b);
        }
        if (icon != null) {
            paintIcon(g, b, ICON_RECT);
        }
        if (text != null && text.length() != 0) {
            View v = (View) b.getClientProperty("html");
            if (v != null) {
                v.paint(g, TEXT_RECT);
            } else {
                paintText(g, b, TEXT_RECT, text);
            }
        }
        if (b.isFocusPainted() && b.hasFocus()) {
            paintFocus(g, b, VIEW_RECT, TEXT_RECT, ICON_RECT);
        }
    }

    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRectangle) {
        JGHyperlink b = (JGHyperlink) c;
        ButtonModel model = b.getModel();
        Icon icon = getIcon(b);
        Icon tmpIcon = null;
        if (icon == null) {
            return;
        }
        if (!model.isEnabled()) {
            tmpIcon = model.isSelected() ? b.getDisabledSelectedIcon() : b.getDisabledIcon();
        } else if (model.isPressed() && model.isArmed()) {
            tmpIcon = b.getPressedIcon();
            if (tmpIcon != null) {
                clearTextShiftOffset();
            }
        } else if (b.isRolloverEnabled() && model.isRollover()) {
            tmpIcon = model.isSelected() ? b.getRolloverSelectedIcon() : b.getRolloverIcon();
        } else if (model.isSelected()) {
            tmpIcon = b.getSelectedIcon();
        }
        if (tmpIcon != null) {
            icon = tmpIcon;
        }
        icon.paintIcon(c, g, iconRectangle.x, iconRectangle.y);
    }

    protected void paintText(Graphics g, AbstractButton b, Rectangle textRectangle, String text) {
        JGHyperlink link = (JGHyperlink) b;
        if (b.isEnabled()) {
            super.paintText(g, b, textRectangle, text);
            ButtonModel model = b.getModel();
            boolean underline = !b.isRolloverEnabled() || model.isRollover() || model.isPressed();
            if (underline) {
                FontMetrics fm = b.getFontMetrics(b.getFont());
                boolean visited = link.getVisited() && link.isVisitedEnabled();
                g.setColor(visited ? link.getVisitedForeground() : b.getForeground());
                g.fillRect(textRectangle.x, textRectangle.y + fm.getAscent() + 1, textRectangle.width, 1);
                return;
            }
            return;
        }
        textRectangle.x++;
        textRectangle.y++;
        super.paintText(g, b, textRectangle, text);
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRectangle, Rectangle textRectangle, Rectangle iconRectangle) {
        int width = b.getWidth();
        int height = b.getHeight();
        Color focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
        g.setColor(focusColor);
        BasicGraphicsUtils.drawDashedRect(g, 0, 0, width, height);
    }

    private static Icon getIcon(JGHyperlink link) {
        if (link.isIconVisible()) {
            return link.getIcon();
        }
        return null;
    }

    public Dimension getPreferredSize(JComponent c) {
        AbstractButton b = (AbstractButton) c;
        return getPreferredButtonSize(b, b.getIconTextGap());
    }

    private static Dimension getPreferredButtonSize(AbstractButton b, int textIconGap) {
        Icon icon = getIcon((JGHyperlink) b);
        String text = b.getText();
        Font font = b.getFont();
        FontMetrics fm = b.getFontMetrics(font);
        Rectangle rectangle = VIEW_RECT;
        VIEW_RECT.y = 0;
        rectangle.x = 0;
        Rectangle rectangle2 = VIEW_RECT;
        VIEW_RECT.height = 32767;
        rectangle2.width = 32767;
        Rectangle rectangle3 = TEXT_RECT;
        Rectangle rectangle4 = TEXT_RECT;
        Rectangle rectangle5 = TEXT_RECT;
        TEXT_RECT.height = 0;
        rectangle5.width = 0;
        rectangle4.y = 0;
        rectangle3.x = 0;
        Rectangle rectangle6 = ICON_RECT;
        Rectangle rectangle7 = ICON_RECT;
        Rectangle rectangle8 = ICON_RECT;
        ICON_RECT.height = 0;
        rectangle8.width = 0;
        rectangle7.y = 0;
        rectangle6.x = 0;
        SwingUtilities.layoutCompoundLabel(b, fm, text, icon, b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), VIEW_RECT, ICON_RECT, TEXT_RECT, textIconGap);
        Rectangle r = ICON_RECT.union(TEXT_RECT);
        Insets insets = b.getInsets();
        r.width += insets.left + insets.right;
        r.height += insets.top + insets.bottom;
        return r.getSize();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/basic/BasicHyperlinkUI$Handler.class */
    private static final class Handler extends BasicButtonListener {
        Handler(JGHyperlink link) {
            super(link);
        }

        public void mouseEntered(MouseEvent evt) {
            super.mouseEntered(evt);
            AbstractButton b = (AbstractButton) evt.getSource();
            b.setCursor(Cursor.getPredefinedCursor(12));
        }

        public void mouseExited(MouseEvent evt) {
            super.mouseExited(evt);
            AbstractButton b = (AbstractButton) evt.getSource();
            b.setCursor((Cursor) null);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            boolean z = -1;
            switch (propertyName.hashCode()) {
                case -410121203:
                    if (propertyName.equals(JGHyperlink.PROPERTY_VISITED_FOREGROUND)) {
                        z = 2;
                        break;
                    }
                    break;
                case 466760490:
                    if (propertyName.equals(JGHyperlink.PROPERTY_VISITED)) {
                        z = false;
                        break;
                    }
                    break;
                case 1041755319:
                    if (propertyName.equals(JGHyperlink.PROPERTY_VISITED_ENABLED)) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case AnimatedLabel.CENTER /* 0 */:
                case true:
                case AnimatedLabel.LEFT /* 2 */:
                    JGHyperlink link = (JGHyperlink) evt.getSource();
                    boolean visitedActive = link.getVisited() && link.isVisitedEnabled();
                    Color foreground = visitedActive ? link.getVisitedForeground() : link.getForeground();
                    if (link.getForeground() instanceof ColorUIResource) {
                        link.setForeground(foreground);
                    }
                    BasicHTML.updateRenderer(link, link.getText());
                    return;
                default:
                    super.propertyChange(evt);
                    return;
            }
        }
    }
}
