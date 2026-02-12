package com.jgoodies.looks.plastic;

import com.jgoodies.looks.plastic.PlasticBorders;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders.class */
public final class PlasticModernBorders {
    private static Border comboBoxArrowButtonBorder;
    private static Border comboBoxEditorBorder;
    private static Border scrollPaneBorder;
    private static Border textFieldBorder;
    private static Border spinnerBorder;
    private static Border rolloverButtonBorder;

    static /* synthetic */ boolean access$300() {
        return isTextEditorBorderPaintingFocus();
    }

    private PlasticModernBorders() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getButtonBorder(Insets buttonMargin) {
        return new BorderUIResource.CompoundBorderUIResource(new ModernButtonBorder(buttonMargin), new BasicBorders.MarginBorder());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getComboBoxArrowButtonBorder() {
        if (comboBoxArrowButtonBorder == null) {
            comboBoxArrowButtonBorder = new CompoundBorder(new ModernComboBoxArrowButtonBorder(), new BasicBorders.MarginBorder());
        }
        return comboBoxArrowButtonBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getComboBoxEditorBorder() {
        if (comboBoxEditorBorder == null) {
            comboBoxEditorBorder = new CompoundBorder(new ModernComboBoxEditorBorder(), new BasicBorders.MarginBorder());
        }
        return comboBoxEditorBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getScrollPaneBorder() {
        if (scrollPaneBorder == null) {
            scrollPaneBorder = new ModernScrollPaneBorder();
        }
        return scrollPaneBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getTextFieldBorder() {
        if (textFieldBorder == null) {
            textFieldBorder = new BorderUIResource.CompoundBorderUIResource(new ModernTextFieldBorder(), new BasicBorders.MarginBorder());
        }
        return textFieldBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getToggleButtonBorder(Insets buttonMargin) {
        return new BorderUIResource.CompoundBorderUIResource(new ModernButtonBorder(buttonMargin), new BasicBorders.MarginBorder());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getSpinnerBorder() {
        if (spinnerBorder == null) {
            spinnerBorder = new ModernSpinnerBorder();
        }
        return spinnerBorder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Border getSpinnerButtonBorder() {
        return new ModernSpinnerButtonBorder();
    }

    static Border getRolloverButtonBorder() {
        if (rolloverButtonBorder == null) {
            rolloverButtonBorder = new CompoundBorder(new ModernRolloverButtonBorder(), new PlasticBorders.RolloverMarginBorder());
        }
        return rolloverButtonBorder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSpinnerEditorTextFieldFocused(JSpinner c) {
        if (!(c.getEditor() instanceof JSpinner.DefaultEditor)) {
            return false;
        }
        JSpinner.DefaultEditor editor = c.getEditor();
        return editor.getTextField().isFocusOwner();
    }

    private static boolean isTextEditorBorderPaintingFocus() {
        return UIManager.getBoolean("TextEditor.borderPaintsFocus");
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernButtonBorder.class */
    private static class ModernButtonBorder extends AbstractBorder implements UIResource {
        protected final Insets insets;

        protected ModernButtonBorder(Insets insets) {
            this.insets = insets;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JButton jButton = (AbstractButton) c;
            ButtonModel model = jButton.getModel();
            boolean isDisabled = !model.isEnabled();
            boolean isPressed = model.isSelected() || (model.isPressed() && model.isArmed());
            boolean isDefault = (jButton instanceof JButton) && jButton.isDefaultButton();
            boolean isRollover = model.isRollover();
            if (isDisabled) {
                PlasticModernUtils.drawDisabledButtonBorder(g, x, y, w, h);
                return;
            }
            if (isPressed) {
                PlasticModernUtils.drawPressedButtonBorder(g, x, y, w, h);
                return;
            }
            if (isDefault) {
                PlasticModernUtils.drawDefaultButtonBorder(g, x, y, w, h);
            } else if (isRollover) {
                PlasticModernUtils.drawRolloverButtonBorder(g, x, y, w, h);
            } else {
                PlasticModernUtils.drawPlainButtonBorder(g, x, y, w, h);
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

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernComboBoxArrowButtonBorder.class */
    private static final class ModernComboBoxArrowButtonBorder extends AbstractBorder implements UIResource {
        protected static final Insets INSETS = new Insets(1, 1, 1, 1);

        private ModernComboBoxArrowButtonBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            ColorUIResource colorUIResource;
            PlasticComboBoxButton<?> button = (PlasticComboBoxButton) c;
            ButtonModel model = button.getModel();
            boolean isDisabled = !model.isEnabled();
            boolean isPressed = model.isPressed() && model.isArmed();
            boolean isRollover = model.isRollover();
            boolean paintFocus = isComboFocused(button) && PlasticModernBorders.access$300();
            if (isDisabled) {
                colorUIResource = MetalLookAndFeel.getControlShadow();
            } else if (isPressed) {
                colorUIResource = PlasticModernUtils.DARK_BLUE;
            } else if (isRollover || paintFocus) {
                colorUIResource = PlasticModernUtils.LIGHT_BLUE;
            } else {
                colorUIResource = PlasticLookAndFeel.getControlDarkShadow();
            }
            g.setColor(colorUIResource);
            PlasticUtils.drawRect(g, x, y, w - 1, h - 1);
        }

        private static boolean isComboFocused(PlasticComboBoxButton<?> button) {
            JComboBox<?> parent = button.getParent();
            if (parent == null) {
                return false;
            }
            JComboBox<?> combo = parent;
            ComboBoxEditor editor = combo.getEditor();
            if (editor == null || editor.getEditorComponent() == null) {
                return false;
            }
            return editor.getEditorComponent().isFocusOwner();
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernComboBoxEditorBorder.class */
    static final class ModernComboBoxEditorBorder extends AbstractBorder {
        private static final Insets INSETS = new Insets(1, 1, 1, 0);

        ModernComboBoxEditorBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            ColorUIResource controlDarkShadow;
            if (!c.isEnabled()) {
                controlDarkShadow = MetalLookAndFeel.getControlShadow();
            } else if (c.isFocusOwner() && PlasticModernBorders.access$300()) {
                controlDarkShadow = PlasticModernUtils.LIGHT_BLUE;
            } else {
                controlDarkShadow = PlasticLookAndFeel.getControlDarkShadow();
            }
            g.setColor(controlDarkShadow);
            PlasticUtils.drawRect(g, x, y, w + 1, h - 1);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernTextFieldBorder.class */
    public static final class ModernTextFieldBorder extends AbstractBorder {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        ModernTextFieldBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            ColorUIResource controlDarkShadow;
            boolean enabled = c.isEnabled() && (!(c instanceof JTextComponent) || ((JTextComponent) c).isEditable());
            if (!enabled) {
                controlDarkShadow = MetalLookAndFeel.getControlShadow();
            } else if (c.isFocusOwner() && PlasticModernBorders.access$300()) {
                controlDarkShadow = PlasticModernUtils.LIGHT_BLUE;
            } else {
                controlDarkShadow = PlasticLookAndFeel.getControlDarkShadow();
            }
            g.setColor(controlDarkShadow);
            PlasticUtils.drawRect(g, x, y, w - 1, h - 1);
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

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernScrollPaneBorder.class */
    private static final class ModernScrollPaneBorder extends MetalBorders.ScrollPaneBorder {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        private ModernScrollPaneBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            ColorUIResource controlShadow;
            if (c.isEnabled()) {
                controlShadow = PlasticLookAndFeel.getControlDarkShadow();
            } else {
                controlShadow = MetalLookAndFeel.getControlShadow();
            }
            g.setColor(controlShadow);
            PlasticUtils.drawRect(g, x, y, w - 1, h - 1);
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

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernSpinnerBorder.class */
    static final class ModernSpinnerBorder extends MetalBorders.ScrollPaneBorder {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        ModernSpinnerBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            ColorUIResource controlDarkShadow;
            if (!c.isEnabled()) {
                controlDarkShadow = MetalLookAndFeel.getControlShadow();
            } else if (PlasticModernBorders.access$300() && PlasticModernBorders.isSpinnerEditorTextFieldFocused((JSpinner) c)) {
                controlDarkShadow = PlasticModernUtils.LIGHT_BLUE;
            } else {
                controlDarkShadow = PlasticLookAndFeel.getControlDarkShadow();
            }
            g.setColor(controlDarkShadow);
            int arrowButtonWidth = UIManager.getInt("ScrollBar.width") - 1;
            int w2 = w - arrowButtonWidth;
            g.fillRect(x, y, w2, 1);
            g.fillRect(x, y + 1, 1, h - 1);
            g.fillRect(x + 1, (y + h) - 1, w2 - 1, 1);
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

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernSpinnerButtonBorder.class */
    static final class ModernSpinnerButtonBorder extends AbstractBorder {
        private static final Insets INSETS = new Insets(1, 1, 1, 1);

        ModernSpinnerButtonBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            ColorUIResource controlDarkShadow;
            if (!c.isEnabled()) {
                controlDarkShadow = MetalLookAndFeel.getControlShadow();
            } else if (isSpinnerFocused(c) && PlasticModernBorders.access$300()) {
                controlDarkShadow = PlasticModernUtils.LIGHT_BLUE;
            } else {
                controlDarkShadow = PlasticLookAndFeel.getControlDarkShadow();
            }
            g.setColor(controlDarkShadow);
            g.fillRect(x, y, 1, h - 1);
            g.fillRect((x + w) - 1, y, 1, h - 1);
            g.fillRect(x, (y + h) - 1, w, 1);
            if ("Spinner.previousButton".equals(c.getName())) {
                g.setColor(PlasticLookAndFeel.getControl());
            }
            g.fillRect(x + 1, y, w - 2, 1);
        }

        private static boolean isSpinnerFocused(Component button) {
            JSpinner parent = button.getParent();
            if (parent instanceof JSpinner) {
                return PlasticModernBorders.isSpinnerEditorTextFieldFocused(parent);
            }
            return false;
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

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernBorders$ModernRolloverButtonBorder.class */
    private static final class ModernRolloverButtonBorder extends ModernButtonBorder {
        private ModernRolloverButtonBorder() {
            super(new Insets(3, 3, 3, 3));
        }

        @Override // com.jgoodies.looks.plastic.PlasticModernBorders.ModernButtonBorder
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
                    PlasticModernUtils.drawPressedButtonBorder(g, x, y, w, h);
                    return;
                } else {
                    PlasticModernUtils.drawPlainButtonBorder(g, x, y, w, h);
                    return;
                }
            }
            if (model.isSelected()) {
                PlasticModernUtils.drawPressedButtonBorder(g, x, y, w, h);
            }
        }
    }
}
