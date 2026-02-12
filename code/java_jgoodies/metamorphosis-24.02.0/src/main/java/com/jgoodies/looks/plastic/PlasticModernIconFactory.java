package com.jgoodies.looks.plastic;

import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernIconFactory.class */
public final class PlasticModernIconFactory {
    private static CheckBoxIcon checkBoxIcon;
    private static RadioButtonIcon radioButtonIcon;
    private static ComboBoxButtonIcon comboBoxButtonIcon;

    private PlasticModernIconFactory() {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Icon getComboBoxButtonIcon() {
        if (comboBoxButtonIcon == null) {
            comboBoxButtonIcon = new ComboBoxButtonIcon();
        }
        return comboBoxButtonIcon;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernIconFactory$State.class */
    public enum State {
        SELECTED_PLAIN("on-plain"),
        SELECTED_ROLLOVER("on-rollover"),
        SELECTED_PRESSED("on-pressed"),
        SELECTED_DISABLED("on-disabled"),
        DESELECTED_PLAIN("off-plain"),
        DESELECTED_ROLLOVER("off-rollover"),
        DESELECTED_PRESSED("off-pressed"),
        DESELECTED_DISABLED("off-disabled");

        private final String code;

        State(String code) {
            this.code = code;
        }

        public String radioKey() {
            return uiKey("RadioButton");
        }

        public String checkKey() {
            return uiKey("CheckBox");
        }

        private String uiKey(String componentName) {
            return componentName + ".icon." + this.code;
        }

        public String iconPath(String lafName, String type) {
            return "icons/" + lafName + '/' + type + '-' + this.code + ".png";
        }

        static State valueOf(ButtonModel m) {
            return !m.isEnabled() ? m.isSelected() ? SELECTED_DISABLED : DESELECTED_DISABLED : (m.isArmed() && m.isPressed()) ? m.isSelected() ? SELECTED_PRESSED : DESELECTED_PRESSED : m.isRollover() ? m.isSelected() ? SELECTED_ROLLOVER : DESELECTED_ROLLOVER : m.isSelected() ? SELECTED_PLAIN : DESELECTED_PLAIN;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernIconFactory$ComboBoxButtonIcon.class */
    private static final class ComboBoxButtonIcon implements Icon, UIResource, Serializable {
        private ComboBoxButtonIcon() {
        }

        public int getIconWidth() {
            return 8;
        }

        public int getIconHeight() {
            return 4;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            JComponent jc = (JComponent) c;
            String suffix = jc.isEnabled() ? ComponentModel.PROPERTY_ENABLED : "disabled";
            UIManager.getIcon("ComboBox.arrowIcon." + suffix).paintIcon(c, g, x - 1, y);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernIconFactory$CheckBoxIcon.class */
    private static final class CheckBoxIcon implements Icon, UIResource, Serializable {
        private static final int SIZE;

        private CheckBoxIcon() {
        }

        static {
            SIZE = ScreenScaling.isScale100() ? 13 : 15;
        }

        public int getIconWidth() {
            return SIZE;
        }

        public int getIconHeight() {
            return SIZE;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            JCheckBox cb = (JCheckBox) c;
            State state = State.valueOf(cb.getModel());
            String uiKey = state.checkKey();
            UIManager.getIcon(uiKey).paintIcon(c, g, x, y);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernIconFactory$RadioButtonIcon.class */
    private static final class RadioButtonIcon implements Icon, UIResource, Serializable {
        private static final int SIZE;

        private RadioButtonIcon() {
        }

        static {
            SIZE = ScreenScaling.isScale100() ? 13 : 15;
        }

        public int getIconWidth() {
            return SIZE;
        }

        public int getIconHeight() {
            return SIZE;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            AbstractButton rb = (AbstractButton) c;
            State state = State.valueOf(rb.getModel());
            String uiKey = state.radioKey();
            UIManager.getIcon(uiKey).paintIcon(c, g, x, y);
        }
    }
}
