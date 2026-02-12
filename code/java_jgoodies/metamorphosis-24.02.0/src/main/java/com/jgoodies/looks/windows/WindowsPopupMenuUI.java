package com.jgoodies.looks.windows;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.common.PopupMenuLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsPopupMenuUI.class */
public final class WindowsPopupMenuUI extends com.sun.java.swing.plaf.windows.WindowsPopupMenuUI {
    private PropertyChangeListener borderListener;

    public static ComponentUI createUI(JComponent b) {
        return new WindowsPopupMenuUI();
    }

    public void installDefaults() {
        super.installDefaults();
        installBorder();
        if (this.popupMenu.getLayout() == null || (this.popupMenu.getLayout() instanceof UIResource)) {
            this.popupMenu.setLayout(new PopupMenuLayout(this.popupMenu, 1));
        }
    }

    public void installListeners() {
        super.installListeners();
        this.borderListener = this::onNoMarginKeyChanged;
        this.popupMenu.addPropertyChangeListener(Options.NO_MARGIN_KEY, this.borderListener);
    }

    protected void uninstallListeners() {
        this.popupMenu.removePropertyChangeListener(Options.NO_MARGIN_KEY, this.borderListener);
        super.uninstallListeners();
    }

    private void onNoMarginKeyChanged(PropertyChangeEvent evt) {
        installBorder();
    }

    private void installBorder() {
        boolean useNarrowBorder = Boolean.TRUE.equals(this.popupMenu.getClientProperty(Options.NO_MARGIN_KEY));
        String suffix = useNarrowBorder ? "noMarginBorder" : "border";
        LookAndFeel.installBorder(this.popupMenu, "PopupMenu." + suffix);
    }
}
