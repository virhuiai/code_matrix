package com.jgoodies.looks.windows;

import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsMenuBarUI.class */
public final class WindowsMenuBarUI extends com.sun.java.swing.plaf.windows.WindowsMenuBarUI {
    private PropertyChangeListener listener;

    public static ComponentUI createUI(JComponent b) {
        return new WindowsMenuBarUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        installSpecialBorder();
    }

    protected void installListeners() {
        super.installListeners();
        this.listener = this::onBorderStyleChanged;
        this.menuBar.addPropertyChangeListener(this.listener);
    }

    protected void uninstallListeners() {
        this.menuBar.removePropertyChangeListener(this.listener);
        super.uninstallListeners();
    }

    private void onBorderStyleChanged(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop.equals(HeaderStyle.KEY) || prop.equals(BorderStyle.WINDOWS_KEY)) {
            installSpecialBorder();
        }
    }

    private void installSpecialBorder() {
        String suffix;
        BorderStyle borderStyle = BorderStyle.from(this.menuBar, BorderStyle.WINDOWS_KEY);
        if (borderStyle != null) {
            suffix = borderStyle.getSuffix();
        } else if (HeaderStyle.from(this.menuBar) == HeaderStyle.BOTH) {
            suffix = "headerBorder";
        } else {
            return;
        }
        LookAndFeel.installBorder(this.menuBar, "MenuBar." + suffix);
    }
}
