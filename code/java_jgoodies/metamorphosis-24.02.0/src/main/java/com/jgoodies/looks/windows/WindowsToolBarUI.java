package com.jgoodies.looks.windows;

import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.plaf.metal.MetalToolBarUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsToolBarUI.class */
public final class WindowsToolBarUI extends MetalToolBarUI {
    private PropertyChangeListener listener;

    public static ComponentUI createUI(JComponent b) {
        return new WindowsToolBarUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        installSpecialBorder();
    }

    protected void installListeners() {
        super.installListeners();
        this.listener = this::onBorderStyleChanged;
        this.toolBar.addPropertyChangeListener(this.listener);
    }

    protected void uninstallListeners() {
        this.toolBar.removePropertyChangeListener(this.listener);
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
        BorderStyle borderStyle = BorderStyle.from(this.toolBar, BorderStyle.WINDOWS_KEY);
        if (borderStyle != null) {
            suffix = borderStyle.getSuffix();
        } else if (HeaderStyle.from(this.toolBar) == HeaderStyle.BOTH) {
            suffix = "headerBorder";
        } else {
            return;
        }
        LookAndFeel.installBorder(this.toolBar, "ToolBar." + suffix);
    }

    protected MouseInputListener createDockingListener() {
        return new DockingListener(this, this.toolBar);
    }

    protected Border createRolloverBorder() {
        return WindowsBorders.getRolloverButtonBorder();
    }

    protected void setBorderToRollover(Component c) {
        if (c instanceof AbstractButton) {
            super.setBorderToRollover(c);
            return;
        }
        if (c instanceof Container) {
            Container cont = (Container) c;
            for (int i = 0; i < cont.getComponentCount(); i++) {
                super.setBorderToRollover(cont.getComponent(i));
            }
        }
    }
}
