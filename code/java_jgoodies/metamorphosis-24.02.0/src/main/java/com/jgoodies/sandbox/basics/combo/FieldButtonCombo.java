package com.jgoodies.sandbox.basics.combo;

import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.components.JGTextField;
import com.jgoodies.layout.builder.FormBuilder;
import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/basics/combo/FieldButtonCombo.class */
public class FieldButtonCombo {
    private final JTextField field;
    private final JButton button;

    public FieldButtonCombo(JTextField field, JButton button) {
        this.field = field;
        this.button = button;
    }

    public final JTextField getField() {
        return this.field;
    }

    public final String getFieldText() {
        return this.field.getText();
    }

    public final void setFieldText(String text) {
        this.field.setText(text);
    }

    public final String getFieldPrompt() {
        if (!(this.field instanceof JGTextField)) {
            throw new UnsupportedOperationException("A field prompt is available only, if the field is a JGTextField");
        }
        return ((JGTextField) this.field).getPrompt();
    }

    public final void setFieldPrompt(String prompt) {
        if (!(this.field instanceof JGTextField)) {
            throw new UnsupportedOperationException("A field prompt can be set only, if the field is a JGTextField");
        }
        ((JGTextField) this.field).setPrompt(prompt);
    }

    public final Icon getFieldIcon() {
        if (!(this.field instanceof JGTextField)) {
            throw new UnsupportedOperationException("A field icon is available only, if the field is a JGTextField");
        }
        return ((JGTextField) this.field).getIcon();
    }

    public final void setFieldIcon(Icon icon) {
        if (!(this.field instanceof JGTextField)) {
            throw new UnsupportedOperationException("A field icon can be set only, if the field is a JGTextField");
        }
        ((JGTextField) this.field).setIcon(icon);
    }

    public final JButton getButton() {
        return this.button;
    }

    public final String getButtonText() {
        return this.button.getText();
    }

    public final void setButtonText(String markedText) {
        MnemonicUtils.configure((AbstractButton) this.button, markedText);
    }

    public void buildInto(FormBuilder builder) {
        builder.add((Component) this.field).xy(1, 1).add((Component) this.button).xy(3, 1);
    }

    public JComponent buildPanel() {
        String buttonSpec = getButtonText().equals("…") ? "pref" : "$button";
        return new FormBuilder().columns("fill:pref:grow, $rg, %s", buttonSpec).rows("p", new Object[0]).add(this::buildInto).xy(0, 0).build();
    }
}
