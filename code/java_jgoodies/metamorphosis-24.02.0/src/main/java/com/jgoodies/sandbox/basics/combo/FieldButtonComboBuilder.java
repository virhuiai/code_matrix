package com.jgoodies.sandbox.basics.combo;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.components.JGComponentFactory;
import com.jgoodies.components.JGTextField;
import com.jgoodies.search.CompletionManager;
import com.jgoodies.search.CompletionProcessor;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/basics/combo/FieldButtonComboBuilder.class */
public final class FieldButtonComboBuilder {
    private final JGComponentFactory factory = JGComponentFactory.getCurrent();
    private JGTextField field = this.factory.createTextField();
    private JButton button = this.factory.createButton();

    public FieldButtonComboBuilder action(Action action) {
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_BLANK, "button action");
        return button(this.factory.createButton(action));
    }

    public FieldButtonComboBuilder actionListener(ActionListener listener) {
        Preconditions.checkNotNull(listener, Messages.MUST_NOT_BE_NULL, "button action listener");
        this.button.addActionListener(listener);
        return this;
    }

    public FieldButtonComboBuilder button(JButton button) {
        this.button = (JButton) Preconditions.checkNotNull(button, Messages.MUST_NOT_BE_NULL, "button");
        return this;
    }

    public FieldButtonComboBuilder buttonText(String markedText) {
        Preconditions.checkNotBlank(markedText, Messages.MUST_NOT_BE_BLANK, "button text");
        Preconditions.checkArgument(markedText.endsWith("…"), "The button text must have an ellipsis to indicate that more information is required.");
        MnemonicUtils.configure((AbstractButton) this.button, markedText);
        return this;
    }

    public FieldButtonComboBuilder completionManager(CompletionManager manager) {
        Preconditions.checkNotNull(manager, Messages.MUST_NOT_BE_NULL, "completion manager");
        manager.install(this.field);
        return this;
    }

    public FieldButtonComboBuilder completionProcessor(CompletionProcessor processor) {
        Preconditions.checkNotNull(processor, Messages.MUST_NOT_BE_NULL, "completion processor");
        return completionManager(new CompletionManager(processor));
    }

    public FieldButtonComboBuilder field(JGTextField field) {
        this.field = (JGTextField) Preconditions.checkNotNull(field, Messages.MUST_NOT_BE_NULL, "text field");
        return this;
    }

    public FieldButtonComboBuilder fieldIcon(Icon icon) {
        this.field.setIcon(icon);
        return this;
    }

    public FieldButtonComboBuilder fieldText(String text) {
        this.field.setText(text);
        return this;
    }

    public FieldButtonComboBuilder fieldPrompt(String prompt) {
        this.field.setPrompt(prompt);
        return this;
    }

    public FieldButtonCombo build() {
        return new FieldButtonCombo(this.field, this.button);
    }
}
