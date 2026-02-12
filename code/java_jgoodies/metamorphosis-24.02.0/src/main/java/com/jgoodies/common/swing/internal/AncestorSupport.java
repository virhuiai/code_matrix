package com.jgoodies.common.swing.internal;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.Window;
import java.awt.dnd.DropTargetEvent;
import java.util.EventObject;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/internal/AncestorSupport.class */
public final class AncestorSupport {
    private AncestorSupport() {
    }

    public static Object getSourceFor(EventObject evt) {
        if (evt == null) {
            return null;
        }
        if (evt instanceof DropTargetEvent) {
            DropTargetEvent dte = (DropTargetEvent) evt;
            return dte.getDropTargetContext().getComponent();
        }
        return evt.getSource();
    }

    public static Component getComponentFor(Object eventSource) {
        if (eventSource instanceof Component) {
            return (Component) eventSource;
        }
        if (eventSource instanceof MenuComponent) {
            Component parent = ((MenuComponent) eventSource).getParent();
            if (parent instanceof Component) {
                return parent;
            }
            return null;
        }
        return null;
    }

    public static Window getWindowFor(EventObject evt) {
        return getWindowFor(getSourceFor(evt));
    }

    public static Window getWindowFor(Object eventSource) {
        Component c = getComponentFor(eventSource);
        if (c != null) {
            return getWindowFor(c);
        }
        if (eventSource != null && eventSource.getClass().getName().equals("com.apple.eawt.Application")) {
            return JOptionPane.getRootFrame();
        }
        return null;
    }

    public static Window getWindowFor(Component c) {
        Component p;
        Component component = c;
        while (true) {
            p = component;
            if (p != null) {
                if (p instanceof JPopupMenu) {
                    component = ((JPopupMenu) p).getInvoker();
                } else {
                    if ((p instanceof Frame) || (p instanceof Dialog)) {
                        break;
                    }
                    component = p.getParent();
                }
            } else {
                return null;
            }
        }
        return (Window) p;
    }
}
