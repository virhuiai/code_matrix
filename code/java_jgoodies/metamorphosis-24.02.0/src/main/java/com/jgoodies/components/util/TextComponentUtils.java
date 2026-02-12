package com.jgoodies.components.util;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.binding.adapter.Bindings;
import java.awt.KeyboardFocusManager;
import java.awt.im.InputContext;
import java.text.ParseException;
import java.util.Optional;
import javax.swing.Action;
import javax.swing.JFormattedTextField;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/TextComponentUtils.class */
public final class TextComponentUtils {
    private TextComponentUtils() {
    }

    public static void commitImmediately() {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        JFormattedTextField permanentFocusOwner = focusManager.getPermanentFocusOwner();
        if (!(permanentFocusOwner instanceof JTextComponent)) {
            return;
        }
        JTextComponent tc = (JTextComponent) permanentFocusOwner;
        if (!tc.isEditable()) {
            return;
        }
        if (tc instanceof JFormattedTextField) {
            commitImmediately(permanentFocusOwner);
        } else {
            commitImmediately(tc);
        }
    }

    private static void commitImmediately(JFormattedTextField field) {
        Optional<Boolean> isEdited = isEdited(field);
        if (isEdited.isPresent() && isEdited.get() == Boolean.FALSE) {
            return;
        }
        InputContext ic = field.getInputContext();
        if (ic != null) {
            ic.endComposition();
        }
        int fb = field.getFocusLostBehavior();
        switch (fb) {
            case AnimatedLabel.CENTER /* 0 */:
            case 1:
                try {
                    field.commitEdit();
                    field.setValue(field.getValue());
                    return;
                } catch (ParseException e) {
                    if (fb == 1) {
                        field.setValue(field.getValue());
                        return;
                    }
                    return;
                }
            case AnimatedLabel.LEFT /* 2 */:
                field.setValue(field.getValue());
                return;
            default:
                return;
        }
    }

    private static void commitImmediately(JTextComponent textComponent) {
        Bindings.commitImmediately();
    }

    private static Optional<Boolean> isEdited(JFormattedTextField field) {
        for (Action action : field.getActions()) {
            String name = (String) action.getValue("Name");
            if ("reset-field-edit".equals(name)) {
                return Optional.of(Boolean.valueOf(action.isEnabled()));
            }
        }
        return Optional.empty();
    }
}
