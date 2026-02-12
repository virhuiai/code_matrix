package com.jgoodies.looks.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders.class */
final class WindowsBorders {
    private static Border menuBorder;
    private static Border xpMenuBorder;
    private static Border menuItemBorder;
    private static Border popupMenuBorder;
    private static Border noMarginPopupMenuBorder;
    private static Border separatorBorder;
    private static Border etchedBorder;
    private static Border menuBarHeaderBorder;
    private static Border toolBarHeaderBorder;
    private static Border rolloverButtonBorder;

    private WindowsBorders() {
    }

    public static Border getButtonBorder() {
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(table.getColor("Button.shadow"), table.getColor("Button.darkShadow"), table.getColor("Button.light"), table.getColor("Button.highlight"), table.getColor("controlText")), new BasicBorders.MarginBorder());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getMenuBorder() {
        if (menuBorder == null) {
            menuBorder = new BorderUIResource.CompoundBorderUIResource(new MenuBorder(), new BasicBorders.MarginBorder());
        }
        return menuBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getXPMenuBorder() {
        if (xpMenuBorder == null) {
            xpMenuBorder = new BasicBorders.MarginBorder();
        }
        return xpMenuBorder;
    }

    static Border getMenuItemBorder() {
        if (menuItemBorder == null) {
            menuItemBorder = new BorderUIResource(new BasicBorders.MarginBorder());
        }
        return menuItemBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getSeparatorBorder() {
        if (separatorBorder == null) {
            separatorBorder = new BorderUIResource.CompoundBorderUIResource(new SeparatorBorder(), new BasicBorders.MarginBorder());
        }
        return separatorBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getEtchedBorder() {
        if (etchedBorder == null) {
            etchedBorder = new BorderUIResource.CompoundBorderUIResource(new EtchedBorder(), new BasicBorders.MarginBorder());
        }
        return etchedBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getMenuBarHeaderBorder() {
        if (menuBarHeaderBorder == null) {
            menuBarHeaderBorder = new BorderUIResource.CompoundBorderUIResource(new MenuBarHeaderBorder(), new BasicBorders.MarginBorder());
        }
        return menuBarHeaderBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getPopupMenuBorder() {
        if (popupMenuBorder == null) {
            popupMenuBorder = new PopupMenuBorder();
        }
        return popupMenuBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getNoMarginPopupMenuBorder() {
        if (noMarginPopupMenuBorder == null) {
            noMarginPopupMenuBorder = new NoMarginPopupMenuBorder();
        }
        return noMarginPopupMenuBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getToolBarHeaderBorder() {
        if (toolBarHeaderBorder == null) {
            toolBarHeaderBorder = new BorderUIResource.CompoundBorderUIResource(new ToolBarHeaderBorder(), new BasicBorders.MarginBorder());
        }
        return toolBarHeaderBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getRolloverButtonBorder() {
        if (rolloverButtonBorder == null) {
            rolloverButtonBorder = new CompoundBorder(new RolloverButtonBorder(), new RolloverMarginBorder());
        }
        return rolloverButtonBorder;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$ButtonBorder.class */
    private static final class ButtonBorder extends AbstractBorder implements UIResource {
        private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
        private final Color shadow;
        private final Color darkShadow;
        private final Color highlight;
        private final Color lightHighlight;
        private final Color defaultColor;

        ButtonBorder(Color shadow, Color darkShadow, Color highlight, Color lightHighlight, Color defaultColor) {
            this.shadow = shadow;
            this.darkShadow = darkShadow;
            this.highlight = highlight;
            this.lightHighlight = lightHighlight;
            this.defaultColor = defaultColor;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            boolean isPressed = false;
            boolean isDefault = false;
            if (c instanceof AbstractButton) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                isPressed = model.isPressed() && model.isArmed();
                if (c instanceof JButton) {
                    isDefault = ((JButton) c).isDefaultButton();
                }
            }
            WindowsBorders.drawBezel(g, x, y, width, height, isPressed, isDefault, this.shadow, this.darkShadow, this.highlight, this.lightHighlight, this.defaultColor);
        }

        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, EMPTY_INSETS);
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 2;
            insets.right = 3;
            insets.bottom = 3;
            insets.left = 3;
            return insets;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$AbstractButtonBorder.class */
    private static abstract class AbstractButtonBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 2, 2);

        private AbstractButtonBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if (model.isPressed()) {
                WindowsUtils.drawPressed3DBorder(g, x, y, w, h);
            } else {
                WindowsUtils.drawFlush3DBorder(g, x, y, w, h);
            }
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$RolloverButtonBorder.class */
    private static final class RolloverButtonBorder extends AbstractButtonBorder {
        private RolloverButtonBorder() {
            super();
        }

        @Override // com.jgoodies.looks.windows.WindowsBorders.AbstractButtonBorder
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            if (!model.isEnabled()) {
                return;
            }
            if (!(c instanceof JToggleButton)) {
                if (model.isRollover()) {
                    super.paintBorder(c, g, x, y, w, h);
                }
            } else if (model.isSelected()) {
                WindowsUtils.drawPressed3DBorder(g, x, y, w, h);
            } else if (model.isRollover()) {
                super.paintBorder(c, g, x, y, w, h);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$RolloverMarginBorder.class */
    private static final class RolloverMarginBorder extends EmptyBorder {
        private RolloverMarginBorder() {
            super(1, 1, 1, 1);
        }

        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            Insets margin = null;
            if (c instanceof AbstractButton) {
                margin = ((AbstractButton) c).getMargin();
            }
            if (margin == null || (margin instanceof UIResource)) {
                insets.left = this.left;
                insets.top = this.top;
                insets.right = this.right;
                insets.bottom = this.bottom;
            } else {
                insets.left = margin.left;
                insets.top = margin.top;
                insets.right = margin.right;
                insets.bottom = margin.bottom;
            }
            return insets;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$SeparatorBorder.class */
    private static final class SeparatorBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(0, 3, 2, 1);

        private SeparatorBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(UIManager.getColor("Separator.foreground"));
            g.drawLine(0, h - 2, w - 1, h - 2);
            g.setColor(UIManager.getColor("Separator.background"));
            g.drawLine(0, h - 1, w - 1, h - 1);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$ThinRaisedBorder.class */
    static final class ThinRaisedBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        ThinRaisedBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            WindowsUtils.drawFlush3DBorder(g, x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$ThinLoweredBorder.class */
    static final class ThinLoweredBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        ThinLoweredBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            WindowsUtils.drawPressed3DBorder(g, x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$EtchedBorder.class */
    private static final class EtchedBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 2, 2);

        private EtchedBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            WindowsUtils.drawPressed3DBorder(g, x, y, w, h);
            WindowsUtils.drawFlush3DBorder(g, x + 1, y + 1, w - 2, h - 2);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$MenuBarHeaderBorder.class */
    private static final class MenuBarHeaderBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 1, 2);

        private MenuBarHeaderBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            WindowsUtils.drawPressed3DBorder(g, x, y, w, h + 1);
            WindowsUtils.drawFlush3DBorder(g, x + 1, y + 1, w - 2, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$PopupMenuBorder.class */
    private static final class PopupMenuBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(3, 3, 3, 3);

        private PopupMenuBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(UIManager.getColor("MenuItem.background"));
            g.drawRect(1, 1, w - 3, h - 3);
            g.drawRect(2, 2, w - 5, h - 5);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$NoMarginPopupMenuBorder.class */
    private static final class NoMarginPopupMenuBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        private NoMarginPopupMenuBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(UIManager.getColor("controlShadow"));
            g.drawRect(0, 0, w - 1, h - 1);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$ToolBarHeaderBorder.class */
    private static final class ToolBarHeaderBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 2, 2, 2);

        private ToolBarHeaderBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            WindowsUtils.drawPressed3DBorder(g, x, y - 1, w, h + 1);
            WindowsUtils.drawFlush3DBorder(g, x + 1, y, w - 2, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsBorders$MenuBorder.class */
    private static final class MenuBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        private MenuBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            if (model.isSelected()) {
                WindowsUtils.drawPressed3DBorder(g, x, y, w, h);
            } else if (model.isRollover()) {
                WindowsUtils.drawFlush3DBorder(g, x, y, w, h);
            }
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void drawBezel(Graphics g, int x, int y, int w, int h, boolean isPressed, boolean isDefault, Color shadow, Color darkShadow, Color highlight, Color lightHighlight, Color defaultColor) {
        Color oldColor = g.getColor();
        g.translate(x, y);
        if (isPressed && isDefault) {
            g.setColor(darkShadow);
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(shadow);
            g.drawRect(1, 1, w - 3, h - 3);
        } else if (isPressed) {
            BasicGraphicsUtils.drawLoweredBezel(g, x, y, w, h, shadow, darkShadow, highlight, lightHighlight);
        } else if (isDefault) {
            g.setColor(defaultColor);
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(lightHighlight);
            g.drawLine(1, 1, 1, h - 3);
            g.drawLine(2, 1, w - 3, 1);
            g.setColor(highlight);
            g.drawLine(2, 2, 2, h - 4);
            g.drawLine(3, 2, w - 4, 2);
            g.setColor(shadow);
            g.drawLine(2, h - 3, w - 3, h - 3);
            g.drawLine(w - 3, 2, w - 3, h - 4);
            g.setColor(darkShadow);
            g.drawLine(1, h - 2, w - 2, h - 2);
            g.drawLine(w - 2, h - 2, w - 2, 1);
        } else {
            g.setColor(lightHighlight);
            g.drawLine(0, 0, 0, h - 1);
            g.drawLine(1, 0, w - 2, 0);
            g.setColor(highlight);
            g.drawLine(1, 1, 1, h - 3);
            g.drawLine(2, 1, w - 3, 1);
            g.setColor(shadow);
            g.drawLine(1, h - 2, w - 2, h - 2);
            g.drawLine(w - 2, 1, w - 2, h - 3);
            g.setColor(darkShadow);
            g.drawLine(0, h - 1, w - 1, h - 1);
            g.drawLine(w - 1, h - 1, w - 1, 0);
        }
        g.translate(-x, -y);
        g.setColor(oldColor);
    }
}
