package com.jgoodies.looks.common;

import com.jgoodies.looks.Options;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtDefaultCaret.class */
public class ExtDefaultCaret extends DefaultCaret implements UIResource {
    private boolean isKeyboardFocusEvent = true;

    public final void focusGained(FocusEvent e) {
        JTextComponent c = getComponent();
        if (c == null) {
            return;
        }
        if (c.isEnabled()) {
            setVisible(true);
            setSelectionVisible(true);
        }
        if (!c.isEnabled() || !this.isKeyboardFocusEvent || !Options.isSelectOnFocusGainActive(c)) {
            return;
        }
        if (c instanceof JFormattedTextField) {
            EventQueue.invokeLater(this::selectAll);
        } else {
            selectAll();
        }
    }

    private void selectAll() {
        JTextComponent c = getComponent();
        if (c == null) {
            return;
        }
        boolean backward = Boolean.TRUE.equals(c.getClientProperty(Options.INVERT_SELECTION_CLIENT_KEY));
        if (backward) {
            setDot(c.getDocument().getLength());
            moveDot(0);
        } else {
            setDot(0);
            moveDot(c.getDocument().getLength());
        }
    }

    public final void focusLost(FocusEvent e) {
        super.focusLost(e);
        if (!e.isTemporary()) {
            this.isKeyboardFocusEvent = true;
            if (getComponent() != null && Boolean.TRUE.equals(getComponent().getClientProperty(Options.SET_CARET_TO_START_ON_FOCUS_LOST_CLIENT_KEY))) {
                setDot(0);
            }
        }
    }

    public final void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) || e.isPopupTrigger()) {
            this.isKeyboardFocusEvent = false;
        }
        super.mousePressed(e);
    }

    public final void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.isPopupTrigger()) {
            this.isKeyboardFocusEvent = false;
            if (getComponent() != null && getComponent().isEnabled() && getComponent().isRequestFocusEnabled()) {
                getComponent().requestFocus();
            }
        }
    }
}
