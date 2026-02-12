package com.jgoodies.components.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JScrollPane;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/ComponentTreeUtils.class */
public final class ComponentTreeUtils {
    private ComponentTreeUtils() {
    }

    public static void updateAllUIs() {
        Logger.getLogger(ComponentTreeUtils.class.getName()).info("Updating all UIs.");
        for (Component component : Frame.getFrames()) {
            updateComponentTreeUI(component);
            for (Component component2 : component.getOwnedWindows()) {
                updateComponentTreeUI(component2);
            }
        }
    }

    public static void updateComponentTreeUI(Component c) {
        updateComponentTreeUI0(c);
        c.invalidate();
        c.validate();
        c.repaint();
    }

    private static void updateComponentTreeUI0(Component c) {
        if (c instanceof JScrollPane) {
            ((JComponent) c).updateUI();
        }
        Component[] children = null;
        if (c instanceof JMenu) {
            children = ((JMenu) c).getMenuComponents();
        } else if (c instanceof Container) {
            children = ((Container) c).getComponents();
        }
        if (children != null) {
            for (Component element : children) {
                updateComponentTreeUI0(element);
            }
        }
        if ((c instanceof JComponent) && !(c instanceof JScrollPane)) {
            ((JComponent) c).updateUI();
        }
    }
}
