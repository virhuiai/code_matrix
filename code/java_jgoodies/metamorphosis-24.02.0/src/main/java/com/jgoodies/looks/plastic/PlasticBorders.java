package com.jgoodies.looks.plastic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.text.JTextComponent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders.class */
public final class PlasticBorders {
    private static Border comboBoxEditorBorder;
    private static Border comboBoxArrowButtonBorder;
    private static Border etchedBorder;
    private static Border flush3DBorder;
    private static Border menuBarHeaderBorder;
    private static Border menuBorder;
    private static Border menuItemBorder;
    private static Border popupMenuBorder;
    private static Border noMarginPopupMenuBorder;
    private static Border rolloverButtonBorder;
    private static Border scrollPaneBorder;
    private static Border separatorBorder;
    private static Border textFieldBorder;
    private static Border thinLoweredBorder;
    private static Border thinRaisedBorder;
    private static Border toolBarHeaderBorder;

    private PlasticBorders() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getButtonBorder(Insets buttonMargin) {
        return new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(buttonMargin), new BasicBorders.MarginBorder());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getComboBoxArrowButtonBorder() {
        if (comboBoxArrowButtonBorder == null) {
            comboBoxArrowButtonBorder = new CompoundBorder(new ComboBoxArrowButtonBorder(), new BasicBorders.MarginBorder());
        }
        return comboBoxArrowButtonBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getComboBoxEditorBorder() {
        if (comboBoxEditorBorder == null) {
            comboBoxEditorBorder = new CompoundBorder(new ComboBoxEditorBorder(), new BasicBorders.MarginBorder());
        }
        return comboBoxEditorBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getEtchedBorder() {
        if (etchedBorder == null) {
            etchedBorder = new BorderUIResource.CompoundBorderUIResource(new EtchedBorder(), new BasicBorders.MarginBorder());
        }
        return etchedBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getFlush3DBorder() {
        if (flush3DBorder == null) {
            flush3DBorder = new Flush3DBorder();
        }
        return flush3DBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getInternalFrameBorder() {
        return new InternalFrameBorder();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getMenuBarHeaderBorder() {
        if (menuBarHeaderBorder == null) {
            menuBarHeaderBorder = new BorderUIResource.CompoundBorderUIResource(new MenuBarHeaderBorder(), new BasicBorders.MarginBorder());
        }
        return menuBarHeaderBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getMenuBorder() {
        if (menuBorder == null) {
            menuBorder = new BorderUIResource.CompoundBorderUIResource(new MenuBorder(), new BasicBorders.MarginBorder());
        }
        return menuBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getMenuItemBorder() {
        if (menuItemBorder == null) {
            menuItemBorder = new BorderUIResource(new BasicBorders.MarginBorder());
        }
        return menuItemBorder;
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
    public static Border getPaletteBorder() {
        return new PaletteBorder();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getRolloverButtonBorder() {
        if (rolloverButtonBorder == null) {
            rolloverButtonBorder = new CompoundBorder(new RolloverButtonBorder(), new RolloverMarginBorder());
        }
        return rolloverButtonBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getScrollPaneBorder() {
        if (scrollPaneBorder == null) {
            scrollPaneBorder = new ScrollPaneBorder();
        }
        return scrollPaneBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getSeparatorBorder() {
        if (separatorBorder == null) {
            separatorBorder = new BorderUIResource.CompoundBorderUIResource(new SeparatorBorder(), new BasicBorders.MarginBorder());
        }
        return separatorBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getTextFieldBorder() {
        if (textFieldBorder == null) {
            textFieldBorder = new BorderUIResource.CompoundBorderUIResource(new TextFieldBorder(), new BasicBorders.MarginBorder());
        }
        return textFieldBorder;
    }

    static Border getThinLoweredBorder() {
        if (thinLoweredBorder == null) {
            thinLoweredBorder = new ThinLoweredBorder();
        }
        return thinLoweredBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getThinRaisedBorder() {
        if (thinRaisedBorder == null) {
            thinRaisedBorder = new ThinRaisedBorder();
        }
        return thinRaisedBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getToggleButtonBorder(Insets buttonMargin) {
        return new BorderUIResource.CompoundBorderUIResource(new ToggleButtonBorder(buttonMargin), new BasicBorders.MarginBorder());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getToolBarHeaderBorder() {
        if (toolBarHeaderBorder == null) {
            toolBarHeaderBorder = new BorderUIResource.CompoundBorderUIResource(new ToolBarHeaderBorder(), new BasicBorders.MarginBorder());
        }
        return toolBarHeaderBorder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$Flush3DBorder.class */
    public static class Flush3DBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 2, 2);

        private Flush3DBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (c.isEnabled()) {
                PlasticUtils.drawFlush3DBorder(g, x, y, w, h);
            } else {
                PlasticUtils.drawDisabledBorder(g, x, y, w, h);
            }
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        public Insets getBorderInsets(Component c, Insets newInsets) {
            newInsets.top = INSETS.top;
            newInsets.left = INSETS.left;
            newInsets.bottom = INSETS.bottom;
            newInsets.right = INSETS.right;
            return newInsets;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ButtonBorder.class */
    public static class ButtonBorder extends AbstractBorder implements UIResource {
        protected final Insets insets;

        protected ButtonBorder(Insets insets) {
            this.insets = insets;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JButton jButton = (AbstractButton) c;
            ButtonModel model = jButton.getModel();
            if (!model.isEnabled()) {
                PlasticUtils.drawDisabledBorder(g, x, y, w - 1, h - 1);
                return;
            }
            boolean isPressed = model.isPressed() && model.isArmed();
            boolean isDefault = (jButton instanceof JButton) && jButton.isDefaultButton();
            if (isPressed && isDefault) {
                PlasticUtils.drawPressedDefaultButtonBorder(g, x, y, w, h);
                return;
            }
            if (isPressed) {
                PlasticUtils.drawPressed3DBorder(g, x, y, w, h);
            } else if (isDefault) {
                PlasticUtils.drawDefaultButtonBorder(g, x, y, w, h, false);
            } else {
                PlasticUtils.drawButtonBorder(g, x, y, w, h, false);
            }
        }

        public Insets getBorderInsets(Component c) {
            return this.insets;
        }

        public Insets getBorderInsets(Component c, Insets newInsets) {
            newInsets.top = this.insets.top;
            newInsets.left = this.insets.left;
            newInsets.bottom = this.insets.bottom;
            newInsets.right = this.insets.right;
            return newInsets;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ComboBoxArrowButtonBorder.class */
    public static final class ComboBoxArrowButtonBorder extends AbstractBorder implements UIResource {
        protected static final Insets INSETS = new Insets(1, 1, 1, 1);

        private ComboBoxArrowButtonBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            PlasticComboBoxButton<?> button = (PlasticComboBoxButton) c;
            ButtonModel model = button.getModel();
            if (!model.isEnabled()) {
                PlasticUtils.drawDisabledBorder(g, x, y, w - 1, h - 1);
                return;
            }
            boolean isPressed = model.isPressed() && model.isArmed();
            if (isPressed) {
                PlasticUtils.drawPressed3DBorder(g, x, y, w, h);
            } else {
                PlasticUtils.drawButtonBorder(g, x, y, w, h, false);
            }
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ComboBoxEditorBorder.class */
    public static final class ComboBoxEditorBorder extends AbstractBorder {
        private static final Insets INSETS = new Insets(2, 2, 2, 0);

        private ComboBoxEditorBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (!c.isEnabled()) {
                PlasticUtils.drawDisabledBorder(g, x, y, w + 2, h - 1);
                g.setColor(UIManager.getColor("control"));
                g.drawLine(x, (y + h) - 1, x + w, (y + h) - 1);
                return;
            }
            PlasticUtils.drawFlush3DBorder(g, x, y, w + 2, h);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$InternalFrameBorder.class */
    public static final class InternalFrameBorder extends AbstractBorder implements UIResource {
        private static final Insets NORMAL_INSETS = new Insets(1, 1, 1, 1);
        private static final Insets MAXIMIZED_INSETS = new Insets(1, 1, 0, 0);

        private InternalFrameBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JInternalFrame frame = (JInternalFrame) c;
            if (frame.isMaximum()) {
                paintMaximizedBorder(g, x, y, w, h);
            } else {
                PlasticUtils.drawThinFlush3DBorder(g, x, y, w, h);
            }
        }

        private static void paintMaximizedBorder(Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(PlasticLookAndFeel.getControlHighlight());
            g.drawLine(0, 0, w - 2, 0);
            g.drawLine(0, 0, 0, h - 2);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return ((JInternalFrame) c).isMaximum() ? MAXIMIZED_INSETS : NORMAL_INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$PaletteBorder.class */
    public static final class PaletteBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        private PaletteBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(PlasticLookAndFeel.getControlDarkShadow());
            g.drawRect(0, 0, w - 1, h - 1);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$SeparatorBorder.class */
    public static final class SeparatorBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(0, 0, 2, 0);

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

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ThinRaisedBorder.class */
    private static final class ThinRaisedBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 2, 2);

        private ThinRaisedBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            PlasticUtils.drawThinFlush3DBorder(g, x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ThinLoweredBorder.class */
    private static final class ThinLoweredBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 2, 2);

        private ThinLoweredBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            PlasticUtils.drawThinPressed3DBorder(g, x, y, w, h);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$EtchedBorder.class */
    public static final class EtchedBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 2, 2);

        private EtchedBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            PlasticUtils.drawThinPressed3DBorder(g, x, y, w, h);
            PlasticUtils.drawThinFlush3DBorder(g, x + 1, y + 1, w - 2, h - 2);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$MenuBarHeaderBorder.class */
    public static final class MenuBarHeaderBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 1, 2);

        private MenuBarHeaderBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            PlasticUtils.drawThinPressed3DBorder(g, x, y, w, h + 1);
            PlasticUtils.drawThinFlush3DBorder(g, x + 1, y + 1, w - 2, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ToolBarHeaderBorder.class */
    public static final class ToolBarHeaderBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 2, 2, 2);

        private ToolBarHeaderBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            PlasticUtils.drawThinPressed3DBorder(g, x, y - 1, w, h + 1);
            PlasticUtils.drawThinFlush3DBorder(g, x + 1, y, w - 2, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$MenuBorder.class */
    public static final class MenuBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(2, 2, 2, 2);

        private MenuBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JMenuItem b = (JMenuItem) c;
            ButtonModel model = b.getModel();
            if (model.isArmed() || model.isSelected()) {
                g.setColor(PlasticLookAndFeel.getControlDarkShadow());
                g.drawLine(0, 0, w - 2, 0);
                g.drawLine(0, 0, 0, h - 1);
                g.setColor(PlasticLookAndFeel.getPrimaryControlHighlight());
                g.drawLine(w - 1, 0, w - 1, h - 1);
                return;
            }
            if (model.isRollover()) {
                g.translate(x, y);
                PlasticUtils.drawFlush3DBorder(g, x, y, w, h);
                g.translate(-x, -y);
            }
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        public Insets getBorderInsets(Component c, Insets newInsets) {
            newInsets.top = INSETS.top;
            newInsets.left = INSETS.left;
            newInsets.bottom = INSETS.bottom;
            newInsets.right = INSETS.right;
            return newInsets;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$PopupMenuBorder.class */
    public static final class PopupMenuBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(3, 3, 3, 3);

        private PopupMenuBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(PlasticLookAndFeel.getControlDarkShadow());
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(PlasticLookAndFeel.getMenuItemBackground());
            g.drawRect(1, 1, w - 3, h - 3);
            g.drawRect(2, 2, w - 5, h - 5);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$NoMarginPopupMenuBorder.class */
    public static final class NoMarginPopupMenuBorder extends AbstractBorder implements UIResource {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        private NoMarginPopupMenuBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(PlasticLookAndFeel.getControlDarkShadow());
            g.drawRect(0, 0, w - 1, h - 1);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$RolloverButtonBorder.class */
    private static final class RolloverButtonBorder extends ButtonBorder {
        private RolloverButtonBorder() {
            super(new Insets(3, 3, 3, 3));
        }

        @Override // com.jgoodies.looks.plastic.PlasticBorders.ButtonBorder
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            if (!model.isEnabled()) {
                return;
            }
            if (!(c instanceof JToggleButton)) {
                if (model.isRollover()) {
                    if (!model.isPressed() || model.isArmed()) {
                        super.paintBorder(c, g, x, y, w, h);
                        return;
                    }
                    return;
                }
                return;
            }
            if (model.isRollover()) {
                if (model.isPressed() && model.isArmed()) {
                    PlasticUtils.drawPressed3DBorder(g, x, y, w, h);
                    return;
                } else {
                    PlasticUtils.drawFlush3DBorder(g, x, y, w, h);
                    return;
                }
            }
            if (model.isSelected()) {
                PlasticUtils.drawDark3DBorder(g, x, y, w, h);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$RolloverMarginBorder.class */
    static final class RolloverMarginBorder extends EmptyBorder {
        /* JADX INFO: Access modifiers changed from: package-private */
        public RolloverMarginBorder() {
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ScrollPaneBorder.class */
    public static final class ScrollPaneBorder extends MetalBorders.ScrollPaneBorder {
        private ScrollPaneBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(PlasticLookAndFeel.getControlDarkShadow());
            g.drawRect(0, 0, w - 2, h - 2);
            g.setColor(PlasticLookAndFeel.getControlHighlight());
            g.drawLine(w - 1, 0, w - 1, h - 1);
            g.drawLine(0, h - 1, w - 1, h - 1);
            g.translate(-x, -y);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$TextFieldBorder.class */
    public static final class TextFieldBorder extends Flush3DBorder {
        private TextFieldBorder() {
            super();
        }

        @Override // com.jgoodies.looks.plastic.PlasticBorders.Flush3DBorder
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (!(c instanceof JTextComponent)) {
                if (c.isEnabled()) {
                    PlasticUtils.drawFlush3DBorder(g, x, y, w, h);
                    return;
                } else {
                    PlasticUtils.drawDisabledBorder(g, x, y, w, h);
                    return;
                }
            }
            if (c.isEnabled() && ((JTextComponent) c).isEditable()) {
                PlasticUtils.drawFlush3DBorder(g, x, y, w, h);
            } else {
                PlasticUtils.drawDisabledBorder(g, x, y, w, h);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBorders$ToggleButtonBorder.class */
    public static final class ToggleButtonBorder extends ButtonBorder {
        private ToggleButtonBorder(Insets insets) {
            super(insets);
        }

        @Override // com.jgoodies.looks.plastic.PlasticBorders.ButtonBorder
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if (!model.isEnabled()) {
                PlasticUtils.drawDisabledBorder(g, x, y, w - 1, h - 1);
                return;
            }
            boolean isPressed = model.isPressed() && model.isArmed();
            if (isPressed) {
                PlasticUtils.drawPressed3DBorder(g, x, y, w, h);
            } else if (model.isSelected()) {
                PlasticUtils.drawDark3DBorder(g, x, y, w, h);
            } else {
                PlasticUtils.drawFlush3DBorder(g, x, y, w, h);
            }
        }
    }
}
