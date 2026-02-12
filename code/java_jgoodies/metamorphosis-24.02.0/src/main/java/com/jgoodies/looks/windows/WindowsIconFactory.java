package com.jgoodies.looks.windows;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsIconFactory.class */
final class WindowsIconFactory {
    private static Icon checkBoxIcon;
    private static Icon radioButtonIcon;

    private WindowsIconFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Icon getCheckBoxIcon() {
        if (checkBoxIcon == null) {
            checkBoxIcon = new CheckBoxIcon();
        }
        return checkBoxIcon;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Icon getRadioButtonIcon() {
        if (radioButtonIcon == null) {
            radioButtonIcon = new RadioButtonIcon();
        }
        return radioButtonIcon;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsIconFactory$CheckBoxIcon.class */
    private static class CheckBoxIcon implements Icon, Serializable {
        private static final int SIZE = 13;

        private CheckBoxIcon() {
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            JCheckBox cb = (JCheckBox) c;
            ButtonModel model = cb.getModel();
            if (!cb.isBorderPaintedFlat()) {
                g.setColor(UIManager.getColor("CheckBox.shadow"));
                g.drawLine(x, y, x + 11, y);
                g.drawLine(x, y + 1, x, y + 11);
                g.setColor(UIManager.getColor("CheckBox.highlight"));
                g.drawLine(x + 12, y, x + 12, y + 12);
                g.drawLine(x, y + 12, x + 11, y + 12);
                g.setColor(UIManager.getColor("CheckBox.darkShadow"));
                g.drawLine(x + 1, y + 1, x + 10, y + 1);
                g.drawLine(x + 1, y + 2, x + 1, y + 10);
                g.setColor(UIManager.getColor("CheckBox.light"));
                g.drawLine(x + 1, y + 11, x + 11, y + 11);
                g.drawLine(x + 11, y + 1, x + 11, y + 10);
            } else {
                g.setColor(UIManager.getColor("CheckBox.shadow"));
                g.drawRect(x + 1, y + 1, 10, 10);
            }
            g.setColor(UIManager.getColor((!(model.isPressed() && model.isArmed()) && model.isEnabled()) ? "CheckBox.interiorBackground" : "CheckBox.background"));
            g.fillRect(x + 2, y + 2, 9, 9);
            g.setColor(UIManager.getColor(model.isEnabled() ? "CheckBox.checkColor" : "CheckBox.shadow"));
            if (model.isSelected()) {
                g.drawLine(x + 9, y + 3, x + 9, y + 3);
                g.drawLine(x + 8, y + 4, x + 9, y + 4);
                g.drawLine(x + 7, y + 5, x + 9, y + 5);
                g.drawLine(x + 6, y + 6, x + 8, y + 6);
                g.drawLine(x + 3, y + 7, x + 7, y + 7);
                g.drawLine(x + 4, y + 8, x + 6, y + 8);
                g.drawLine(x + 5, y + 9, x + 5, y + 9);
                g.drawLine(x + 3, y + 5, x + 3, y + 5);
                g.drawLine(x + 3, y + 6, x + 4, y + 6);
            }
        }

        public int getIconWidth() {
            return SIZE;
        }

        public int getIconHeight() {
            return SIZE;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsIconFactory$RadioButtonIcon.class */
    private static class RadioButtonIcon implements Icon, UIResource, Serializable {
        private static final int SIZE = 13;

        private RadioButtonIcon() {
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            g.setColor(UIManager.getColor((!(model.isPressed() && model.isArmed()) && model.isEnabled()) ? "RadioButton.interiorBackground" : "RadioButton.background"));
            g.fillRect(x + 2, y + 2, 8, 8);
            g.setColor(UIManager.getColor("RadioButton.shadow"));
            g.drawLine(x + 4, y + 0, x + 7, y + 0);
            g.drawLine(x + 2, y + 1, x + 3, y + 1);
            g.drawLine(x + 8, y + 1, x + 9, y + 1);
            g.drawLine(x + 1, y + 2, x + 1, y + 3);
            g.drawLine(x + 0, y + 4, x + 0, y + 7);
            g.drawLine(x + 1, y + 8, x + 1, y + 9);
            g.setColor(UIManager.getColor("RadioButton.highlight"));
            g.drawLine(x + 2, y + 10, x + 3, y + 10);
            g.drawLine(x + 4, y + 11, x + 7, y + 11);
            g.drawLine(x + 8, y + 10, x + 9, y + 10);
            g.drawLine(x + 10, y + 9, x + 10, y + 8);
            g.drawLine(x + 11, y + 7, x + 11, y + 4);
            g.drawLine(x + 10, y + 3, x + 10, y + 2);
            g.setColor(UIManager.getColor("RadioButton.darkShadow"));
            g.drawLine(x + 4, y + 1, x + 7, y + 1);
            g.drawLine(x + 2, y + 2, x + 3, y + 2);
            g.drawLine(x + 8, y + 2, x + 9, y + 2);
            g.drawLine(x + 2, y + 3, x + 2, y + 3);
            g.drawLine(x + 1, y + 4, x + 1, y + 7);
            g.drawLine(x + 2, y + 8, x + 2, y + 8);
            g.setColor(UIManager.getColor("RadioButton.light"));
            g.drawLine(x + 2, y + 9, x + 3, y + 9);
            g.drawLine(x + 4, y + 10, x + 7, y + 10);
            g.drawLine(x + 8, y + 9, x + 9, y + 9);
            g.drawLine(x + 9, y + 8, x + 9, y + 8);
            g.drawLine(x + 10, y + 7, x + 10, y + 4);
            g.drawLine(x + 9, y + 3, x + 9, y + 3);
            if (model.isSelected()) {
                g.setColor(UIManager.getColor("RadioButton.checkColor"));
                g.fillRect(x + 4, y + 5, 4, 2);
                g.fillRect(x + 5, y + 4, 2, 4);
            }
        }

        public int getIconWidth() {
            return SIZE;
        }

        public int getIconHeight() {
            return SIZE;
        }
    }
}
