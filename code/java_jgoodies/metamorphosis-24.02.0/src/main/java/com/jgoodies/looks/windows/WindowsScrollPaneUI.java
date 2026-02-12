package com.jgoodies.looks.windows;

import com.jgoodies.looks.Options;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsScrollPaneUI.class */
public final class WindowsScrollPaneUI extends com.sun.java.swing.plaf.windows.WindowsScrollPaneUI {
    private PropertyChangeListener borderStyleChangeHandler;

    public static ComponentUI createUI(JComponent b) {
        return new WindowsScrollPaneUI();
    }

    protected void installDefaults(JScrollPane scrollPane) {
        super.installDefaults(scrollPane);
        installEtchedBorder(scrollPane);
    }

    private static void installEtchedBorder(JScrollPane scrollPane) {
        Object value = scrollPane.getClientProperty(Options.IS_ETCHED_KEY);
        boolean hasEtchedBorder = Boolean.TRUE.equals(value);
        LookAndFeel.installBorder(scrollPane, hasEtchedBorder ? "ScrollPane.etchedBorder" : "ScrollPane.border");
    }

    public void installListeners(JScrollPane scrollPane) {
        super.installListeners(scrollPane);
        this.borderStyleChangeHandler = this::onBorderStyleChanged;
        scrollPane.addPropertyChangeListener(Options.IS_ETCHED_KEY, this.borderStyleChangeHandler);
    }

    protected void uninstallListeners(JComponent c) {
        ((JScrollPane) c).removePropertyChangeListener(Options.IS_ETCHED_KEY, this.borderStyleChangeHandler);
        super.uninstallListeners(c);
    }

    private void onBorderStyleChanged(PropertyChangeEvent evt) {
        JScrollPane scrollPane = (JScrollPane) evt.getSource();
        installEtchedBorder(scrollPane);
    }
}
