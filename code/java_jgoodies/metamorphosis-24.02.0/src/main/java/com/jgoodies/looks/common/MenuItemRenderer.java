package com.jgoodies.looks.common;

import com.jgoodies.common.swing.internal.AcceleratorUtils;
import com.jgoodies.common.swing.internal.RenderingUtils;
import com.jgoodies.looks.Options;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/MenuItemRenderer.class */
public class MenuItemRenderer {
    protected static final String HTML_KEY = "html";
    static final String MAX_TEXT_WIDTH = "maxTextWidth";
    static final String MAX_ACC_WIDTH = "maxAccWidth";
    private static final Icon NO_ICON = new NullIcon();
    static Rectangle zeroRect = new Rectangle(0, 0, 0, 0);
    static Rectangle iconRect = new Rectangle();
    static Rectangle textRect = new Rectangle();
    static Rectangle acceleratorRect = new Rectangle();
    static Rectangle checkIconRect = new Rectangle();
    static Rectangle arrowIconRect = new Rectangle();
    static Rectangle viewRect = new Rectangle(32767, 32767);
    static Rectangle r = new Rectangle();
    private final JMenuItem menuItem;
    private final boolean iconBorderEnabled;
    private final Font acceleratorFont;
    private final Color selectionForeground;
    private final Color disabledForeground;
    private final Color acceleratorForeground;
    private final Color acceleratorSelectionForeground;
    private final String acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
    private final Icon fillerIcon = new MinimumSizedIcon();

    public MenuItemRenderer(JMenuItem menuItem, boolean iconBorderEnabled, Font acceleratorFont, Color selectionForeground, Color disabledForeground, Color acceleratorForeground, Color acceleratorSelectionForeground) {
        this.menuItem = menuItem;
        this.iconBorderEnabled = iconBorderEnabled;
        this.acceleratorFont = acceleratorFont;
        this.selectionForeground = selectionForeground;
        this.disabledForeground = disabledForeground;
        this.acceleratorForeground = acceleratorForeground;
        this.acceleratorSelectionForeground = acceleratorSelectionForeground;
    }

    private static Icon getIcon(JMenuItem aMenuItem, Icon defaultIcon) {
        Icon icon = aMenuItem.getIcon();
        if (icon == null) {
            return defaultIcon;
        }
        ButtonModel model = aMenuItem.getModel();
        if (!model.isEnabled()) {
            if (model.isSelected()) {
                return aMenuItem.getDisabledSelectedIcon();
            }
            return aMenuItem.getDisabledIcon();
        }
        if (model.isPressed() && model.isArmed()) {
            Icon pressedIcon = aMenuItem.getPressedIcon();
            return pressedIcon != null ? pressedIcon : icon;
        }
        if (model.isSelected()) {
            Icon selectedIcon = aMenuItem.getSelectedIcon();
            return selectedIcon != null ? selectedIcon : icon;
        }
        return icon;
    }

    private boolean hasCustomIcon() {
        return getIcon(this.menuItem, null) != null;
    }

    private Icon getWrappedIcon(Icon icon) {
        if (hideIcons()) {
            return NO_ICON;
        }
        if (icon == null) {
            return this.fillerIcon;
        }
        return (this.iconBorderEnabled && hasCustomIcon()) ? new MinimumSizedCheckIcon(icon, this.menuItem) : new MinimumSizedIcon(icon);
    }

    private static void resetRects() {
        iconRect.setBounds(zeroRect);
        textRect.setBounds(zeroRect);
        acceleratorRect.setBounds(zeroRect);
        checkIconRect.setBounds(zeroRect);
        arrowIconRect.setBounds(zeroRect);
        viewRect.setBounds(0, 0, 32767, 32767);
        r.setBounds(zeroRect);
    }

    public Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        String text = b.getText();
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = AcceleratorUtils.getAcceleratorText(c, accelerator, this.acceleratorDelimiter);
        FontMetrics fm = b.getFontMetrics(b.getFont());
        FontMetrics fmAccel = b.getFontMetrics(this.acceleratorFont);
        resetRects();
        Icon wrappedIcon = getWrappedIcon(getIcon(this.menuItem, checkIcon));
        Icon wrappedArrowIcon = new MinimumSizedIcon(arrowIcon);
        Icon icon = wrappedIcon.getIconHeight() > this.fillerIcon.getIconHeight() ? wrappedIcon : null;
        layoutMenuItem(fm, text, fmAccel, acceleratorText, icon, wrappedIcon, wrappedArrowIcon, b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect, iconRect, textRect, acceleratorRect, checkIconRect, arrowIconRect, text == null ? 0 : defaultTextIconGap, defaultTextIconGap);
        r.setBounds(textRect);
        r = SwingUtilities.computeUnion(iconRect.x, iconRect.y, iconRect.width, iconRect.height, r);
        JComponent parent = this.menuItem.getParent();
        if ((parent instanceof JComponent) && (!(this.menuItem instanceof JMenu) || !this.menuItem.isTopLevelMenu())) {
            JComponent p = parent;
            Integer maxTextWidth = (Integer) p.getClientProperty(MAX_TEXT_WIDTH);
            Integer maxAccWidth = (Integer) p.getClientProperty(MAX_ACC_WIDTH);
            int maxTextValue = maxTextWidth != null ? maxTextWidth.intValue() : 0;
            int maxAccValue = maxAccWidth != null ? maxAccWidth.intValue() : 0;
            if (r.width < maxTextValue) {
                r.width = maxTextValue;
            } else {
                p.putClientProperty(MAX_TEXT_WIDTH, Integer.valueOf(r.width));
            }
            if (acceleratorRect.width > maxAccValue) {
                maxAccValue = acceleratorRect.width;
                p.putClientProperty(MAX_ACC_WIDTH, Integer.valueOf(acceleratorRect.width));
            }
            r.width += maxAccValue;
            r.width += 10;
        }
        if (useCheckAndArrow()) {
            r.width += checkIconRect.width;
            r.width += defaultTextIconGap;
            r.width += defaultTextIconGap;
            r.width += arrowIconRect.width;
        }
        r.width += 2 * defaultTextIconGap;
        Insets insets = b.getInsets();
        if (insets != null) {
            r.width += insets.left + insets.right;
            r.height += insets.top + insets.bottom;
        }
        if ((r.height & 1) == 1) {
            r.height++;
        }
        return r.getSize();
    }

    public void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, Icon arrowIcon, Color background, Color foreground, int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        ButtonModel model = b.getModel();
        int menuWidth = b.getWidth();
        int menuHeight = b.getHeight();
        Insets i = c.getInsets();
        resetRects();
        viewRect.setBounds(0, 0, menuWidth, menuHeight);
        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= i.right + viewRect.x;
        viewRect.height -= i.bottom + viewRect.y;
        Font oldFont = g.getFont();
        Font font = c.getFont();
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        FontMetrics fmAccel = g.getFontMetrics(this.acceleratorFont);
        KeyStroke accelerator = b.getAccelerator();
        String acceleratorText = AcceleratorUtils.getAcceleratorText(c, accelerator, this.acceleratorDelimiter);
        Icon wrappedIcon = getWrappedIcon(getIcon(this.menuItem, checkIcon));
        Icon wrappedArrowIcon = new MinimumSizedIcon(arrowIcon);
        String text = layoutMenuItem(fm, b.getText(), fmAccel, acceleratorText, null, wrappedIcon, wrappedArrowIcon, b.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect, iconRect, textRect, acceleratorRect, checkIconRect, arrowIconRect, b.getText() == null ? 0 : defaultTextIconGap, defaultTextIconGap);
        paintBackground(g, b, background);
        Color oldColor = g.getColor();
        if (model.isArmed() || ((c instanceof JMenu) && model.isSelected())) {
            g.setColor(foreground);
        }
        wrappedIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
        g.setColor(oldColor);
        if (text != null) {
            View v = (View) c.getClientProperty(HTML_KEY);
            if (v != null) {
                v.paint(g, textRect);
            } else {
                paintText(g, b, textRect, text);
            }
        }
        if (!acceleratorText.equals("")) {
            int accOffset = 0;
            JComponent parent = this.menuItem.getParent();
            if (parent instanceof JComponent) {
                JComponent p = parent;
                Integer maxValueInt = (Integer) p.getClientProperty(MAX_ACC_WIDTH);
                int maxValue = maxValueInt != null ? maxValueInt.intValue() : acceleratorRect.width;
                accOffset = isLeftToRight(this.menuItem) ? maxValue - acceleratorRect.width : acceleratorRect.width - maxValue;
            }
            g.setFont(this.acceleratorFont);
            if (!model.isEnabled()) {
                if (!disabledTextHasShadow()) {
                    g.setColor(this.disabledForeground);
                    RenderingUtils.drawString(c, g, acceleratorText, acceleratorRect.x - accOffset, acceleratorRect.y + fmAccel.getAscent());
                } else {
                    g.setColor(b.getBackground().brighter());
                    RenderingUtils.drawString(c, g, acceleratorText, acceleratorRect.x - accOffset, acceleratorRect.y + fmAccel.getAscent());
                    g.setColor(b.getBackground().darker());
                    RenderingUtils.drawString(c, g, acceleratorText, (acceleratorRect.x - accOffset) - 1, (acceleratorRect.y + fmAccel.getAscent()) - 1);
                }
            } else {
                if (model.isArmed() || ((c instanceof JMenu) && model.isSelected())) {
                    g.setColor(this.acceleratorSelectionForeground);
                } else {
                    g.setColor(this.acceleratorForeground);
                }
                RenderingUtils.drawString(c, g, acceleratorText, acceleratorRect.x - accOffset, acceleratorRect.y + fmAccel.getAscent());
            }
        }
        if (arrowIcon != null) {
            if (model.isArmed() || ((c instanceof JMenu) && model.isSelected())) {
                g.setColor(foreground);
            }
            if (useCheckAndArrow()) {
                wrappedArrowIcon.paintIcon(c, g, arrowIconRect.x, arrowIconRect.y);
            }
        }
        g.setColor(oldColor);
        g.setFont(oldFont);
    }

    private String layoutMenuItem(FontMetrics fm, String text, FontMetrics fmAccel, String acceleratorText, Icon icon, Icon checkIcon, Icon arrowIcon, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition, Rectangle viewRectangle, Rectangle iconRectangle, Rectangle textRectangle, Rectangle acceleratorRectangle, Rectangle checkIconRectangle, Rectangle arrowIconRectangle, int textIconGap, int menuItemGap) {
        SwingUtilities.layoutCompoundLabel(this.menuItem, fm, text, icon, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition, viewRectangle, iconRectangle, textRectangle, textIconGap);
        if (acceleratorText == null || acceleratorText.equals("")) {
            acceleratorRectangle.height = 0;
            acceleratorRectangle.width = 0;
        } else {
            acceleratorRectangle.width = SwingUtilities.computeStringWidth(fmAccel, acceleratorText);
            acceleratorRectangle.height = fmAccel.getHeight();
        }
        boolean useCheckAndArrow = useCheckAndArrow();
        if (useCheckAndArrow) {
            if (checkIcon != null) {
                checkIconRectangle.width = checkIcon.getIconWidth();
                checkIconRectangle.height = checkIcon.getIconHeight();
            } else {
                checkIconRectangle.height = 0;
                checkIconRectangle.width = 0;
            }
            if (arrowIcon != null) {
                arrowIconRectangle.width = arrowIcon.getIconWidth();
                arrowIconRectangle.height = arrowIcon.getIconHeight();
            } else {
                arrowIconRectangle.height = 0;
                arrowIconRectangle.width = 0;
            }
        }
        Rectangle labelRect = iconRectangle.union(textRectangle);
        if (isLeftToRight(this.menuItem)) {
            textRectangle.x += menuItemGap;
            iconRectangle.x += menuItemGap;
            acceleratorRectangle.x = (((viewRectangle.x + viewRectangle.width) - arrowIconRectangle.width) - menuItemGap) - acceleratorRectangle.width;
            if (useCheckAndArrow) {
                checkIconRectangle.x = viewRectangle.x;
                textRectangle.x += menuItemGap + checkIconRectangle.width;
                iconRectangle.x += menuItemGap + checkIconRectangle.width;
                arrowIconRectangle.x = ((viewRectangle.x + viewRectangle.width) - menuItemGap) - arrowIconRectangle.width;
            }
        } else {
            textRectangle.x -= menuItemGap;
            iconRectangle.x -= menuItemGap;
            acceleratorRectangle.x = viewRectangle.x + arrowIconRectangle.width + menuItemGap;
            if (useCheckAndArrow) {
                checkIconRectangle.x = (viewRectangle.x + viewRectangle.width) - checkIconRectangle.width;
                textRectangle.x -= menuItemGap + checkIconRectangle.width;
                iconRectangle.x -= menuItemGap + checkIconRectangle.width;
                arrowIconRectangle.x = viewRectangle.x + menuItemGap;
            }
        }
        acceleratorRectangle.y = (labelRect.y + (labelRect.height / 2)) - (acceleratorRectangle.height / 2);
        if (useCheckAndArrow) {
            arrowIconRectangle.y = (labelRect.y + (labelRect.height / 2)) - (arrowIconRectangle.height / 2);
            checkIconRectangle.y = (labelRect.y + (labelRect.height / 2)) - (checkIconRectangle.height / 2);
        }
        return text;
    }

    private boolean useCheckAndArrow() {
        boolean isTopLevelMenu = (this.menuItem instanceof JMenu) && this.menuItem.isTopLevelMenu();
        return !isTopLevelMenu;
    }

    private static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    private static void paintBackground(Graphics g, JMenuItem aMenuItem, Color bgColor) {
        ButtonModel model = aMenuItem.getModel();
        if (aMenuItem.isOpaque()) {
            int menuWidth = aMenuItem.getWidth();
            int menuHeight = aMenuItem.getHeight();
            Color c = (model.isArmed() || ((aMenuItem instanceof JMenu) && model.isSelected())) ? bgColor : aMenuItem.getBackground();
            Color oldColor = g.getColor();
            g.setColor(c);
            g.fillRect(0, 0, menuWidth, menuHeight);
            g.setColor(oldColor);
        }
    }

    private void paintText(Graphics g, JMenuItem aMenuItem, Rectangle textRectangle, String text) {
        ButtonModel model = aMenuItem.getModel();
        FontMetrics fm = aMenuItem.getFontMetrics(g.getFont());
        int mnemIndex = aMenuItem.getDisplayedMnemonicIndex();
        if (isMnemonicHidden()) {
            mnemIndex = -1;
        }
        if (!model.isEnabled()) {
            if (!disabledTextHasShadow()) {
                g.setColor(UIManager.getColor("MenuItem.disabledForeground"));
                RenderingUtils.drawStringUnderlineCharAt(aMenuItem, g, text, mnemIndex, textRectangle.x, textRectangle.y + fm.getAscent());
                return;
            } else {
                g.setColor(aMenuItem.getBackground().brighter());
                RenderingUtils.drawStringUnderlineCharAt(aMenuItem, g, text, mnemIndex, textRectangle.x, textRectangle.y + fm.getAscent());
                g.setColor(aMenuItem.getBackground().darker());
                RenderingUtils.drawStringUnderlineCharAt(aMenuItem, g, text, mnemIndex, textRectangle.x - 1, (textRectangle.y + fm.getAscent()) - 1);
                return;
            }
        }
        if (model.isArmed() || ((aMenuItem instanceof JMenu) && model.isSelected())) {
            g.setColor(this.selectionForeground);
        }
        RenderingUtils.drawStringUnderlineCharAt(aMenuItem, g, text, mnemIndex, textRectangle.x, textRectangle.y + fm.getAscent());
    }

    protected boolean isMnemonicHidden() {
        return false;
    }

    protected boolean disabledTextHasShadow() {
        return false;
    }

    private boolean hideIcons() {
        JPopupMenu parent = this.menuItem.getParent();
        if (!(parent instanceof JPopupMenu)) {
            return false;
        }
        JPopupMenu popupMenu = parent;
        Object value = popupMenu.getClientProperty(Options.NO_ICONS_KEY);
        if (value == null) {
            JMenu invoker = popupMenu.getInvoker();
            if (invoker instanceof JMenu) {
                value = invoker.getClientProperty(Options.NO_ICONS_KEY);
            }
        }
        return Boolean.TRUE.equals(value);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/MenuItemRenderer$NullIcon.class */
    private static class NullIcon implements Icon {
        private NullIcon() {
        }

        public int getIconWidth() {
            return 0;
        }

        public int getIconHeight() {
            return 0;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
        }
    }
}
