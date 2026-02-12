package com.jgoodies.common.swing.focus;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/focus/JGLayoutFocusTraversalPolicy.class */
public class JGLayoutFocusTraversalPolicy extends LayoutFocusTraversalPolicy {
    private static FocusTraversalPolicy defaultInstance;
    private final Component initialComponent;

    public JGLayoutFocusTraversalPolicy() {
        this(null);
    }

    public JGLayoutFocusTraversalPolicy(Component initialComponent) {
        this.initialComponent = initialComponent;
    }

    public static void installAsDefault() {
        if (defaultInstance == null) {
            defaultInstance = new JGLayoutFocusTraversalPolicy();
        }
        new JFrame();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(defaultInstance);
    }

    public Component getInitialComponent(Window window) {
        return this.initialComponent != null ? this.initialComponent : super.getInitialComponent(window);
    }

    protected final boolean accept(Component aComponent) {
        boolean rawResult = acceptDelegate(aComponent);
        if (FocusTraversalUtils.isPopulatingGroup()) {
            return rawResult;
        }
        if (!rawResult) {
            return false;
        }
        return FocusTraversalUtils.accept(aComponent);
    }

    protected boolean acceptDelegate(Component aComponent) {
        if (!super.accept(aComponent)) {
            return false;
        }
        if (!(aComponent instanceof JTextComponent)) {
            return true;
        }
        JTextComponent textComponent = (JTextComponent) aComponent;
        if (textComponent.isEditable()) {
            return true;
        }
        Boolean focusTraversable = FocusTraversalUtils.isFocusTraversable(textComponent);
        if (focusTraversable == null) {
            return FocusTraversalUtils.getAcceptNonEditableTextComponents();
        }
        return focusTraversable.booleanValue();
    }
}
