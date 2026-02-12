package com.jgoodies.looks.plastic;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.common.PopupMenuLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticPopupMenuUI.class */
public final class PlasticPopupMenuUI extends BasicPopupMenuUI {
    private PropertyChangeListener borderListener;

    public static ComponentUI createUI(JComponent b) {
        return new PlasticPopupMenuUI();
    }

    public void installDefaults() {
        super.installDefaults();
        installBorder();
        if (this.popupMenu.getLayout() == null || (this.popupMenu.getLayout() instanceof UIResource)) {
            this.popupMenu.setLayout(new PopupMenuLayout(this.popupMenu, 1));
        }
    }

    protected void installListeners() {
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
