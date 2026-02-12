package com.jgoodies.components;

import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.components.internal.ErrorUnderlineSupport;
import com.jgoodies.components.internal.JGTextComponent2;
import com.jgoodies.components.internal.PromptSupport;
import com.jgoodies.components.internal.TextFieldSupport;
import com.jgoodies.components.util.ComponentUtils;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.Consumer;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTextField.class */
public class JGTextField extends JTextField implements JGTextComponent2 {
    public JGTextField() {
    }

    public JGTextField(String text) {
        super(text);
    }

    public JGTextField(int columns) {
        super(columns);
    }

    public JGTextField(String text, int columns) {
        super(text, columns);
    }

    public JGTextField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final String getPrompt() {
        return PromptSupport.getPrompt(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPrompt(String prompt) {
        PromptSupport.setPrompt(this, prompt);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final int getPromptStyle() {
        return PromptSupport.getPromptStyle(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPromptStyle(int style) {
        PromptSupport.setPromptStyle(this, style);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final boolean isPromptVisibleWhenFocused() {
        return PromptSupport.isPromptVisibleWhenFocused(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPromptVisibleWhenFocused(boolean newValue) {
        PromptSupport.setPromptVisibleWhenFocused(this, newValue);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final boolean isJGFocusTraversable() {
        return FocusTraversalUtils.isFocusTraversable(this).booleanValue();
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setJGFocusTraversable(Boolean value) {
        FocusTraversalUtils.setFocusTraversable((JTextComponent) this, value);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final Boolean getSelectOnFocusGainEnabled() {
        return TextFieldSupport.getSelectOnFocusGainEnabled(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setSelectOnFocusGainEnabled(Boolean b) {
        TextFieldSupport.setSelectOnFocusGainEnabled(this, b);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final Icon getIcon() {
        return TextFieldSupport.getIcon(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setIcon(Icon newIcon) {
        TextFieldSupport.setIcon(this, newIcon);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final Icon getPressedIcon() {
        return TextFieldSupport.getPressedIcon(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setPressedIcon(Icon newIcon) {
        TextFieldSupport.setPressedIcon(this, newIcon);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final Icon getRolloverIcon() {
        return TextFieldSupport.getRolloverIcon(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setRolloverIcon(Icon newIcon) {
        TextFieldSupport.setRolloverIcon(this, newIcon);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final Action getIconAction() {
        return TextFieldSupport.getIconAction(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setIconAction(Action iconAction) {
        TextFieldSupport.setIconAction((JTextComponent) this, iconAction);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setIconAction(Consumer<ActionEvent> handler) {
        TextFieldSupport.setIconAction((JTextComponent) this, handler);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final Action getLinkAction() {
        return TextFieldSupport.getLinkAction(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setLinkAction(Action linkAction) {
        TextFieldSupport.setLinkAction((JTextComponent) this, linkAction);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setLinkAction(Consumer<ActionEvent> handler) {
        TextFieldSupport.setLinkAction((JTextComponent) this, handler);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setKeyboardAction(Action keyboardAction) {
        ComponentUtils.registerKeyboardAction(this, keyboardAction);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final boolean isButtonPaintedAlways() {
        return TextFieldSupport.isButtonPaintedAlways(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setButtonPaintedAlways(boolean newValue) {
        TextFieldSupport.setButtonPaintedAlways(this, newValue);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final boolean isIconVisibleAlways() {
        return TextFieldSupport.isIconVisibleAlways(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setIconVisibleAlways(boolean newValue) {
        TextFieldSupport.setIconVisibleAlways(this, newValue);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final JPopupMenu getMenu() {
        return TextFieldSupport.getMenu(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setMenu(JPopupMenu popupMenu) {
        TextFieldSupport.setMenu(this, popupMenu);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final Icon getMenuIcon() {
        return TextFieldSupport.getMenuIcon(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setMenuIcon(Icon newIcon) {
        TextFieldSupport.setMenuIcon(this, newIcon);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final boolean isErrorUnderlinePainted() {
        return ErrorUnderlineSupport.isErrorUnderlinePainted(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent2
    public final void setErrorUnderlinePainted(boolean newValue) {
        ErrorUnderlineSupport.setErrorUnderlinePainted(this, newValue);
    }

    public Dimension getPreferredSize() {
        return TextFieldSupport.getPreferredSize(this, super.getPreferredSize());
    }

    public String getToolTipText(MouseEvent e) {
        return TextFieldSupport.getToolTipText(this, e, super.getToolTipText());
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        PromptSupport.paintPrompt(this, g);
    }

    public void updateUI() {
        super.updateUI();
        JPopupMenu menu = getMenu();
        if (menu != null) {
            menu.updateUI();
        }
        TextFieldSupport.updateUI(this);
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJGTextField();
        }
        return this.accessibleContext;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTextField$AccessibleJGTextField.class */
    private final class AccessibleJGTextField extends AccessibleJTextField {
        private AccessibleJGTextField() {
            super(JGTextField.this);
        }

        public String getAccessibleName() {
            String name = super.getAccessibleName();
            if (name != null) {
                return name;
            }
            return JGTextField.this.getPrompt();
        }

        public String getAccessibleDescription() {
            String description = super.getAccessibleDescription();
            if (description != null) {
                return description;
            }
            String name = getAccessibleName();
            if (Objects.equals(name, JGTextField.this.getPrompt())) {
                return null;
            }
            return JGTextField.this.getPrompt();
        }
    }
}
