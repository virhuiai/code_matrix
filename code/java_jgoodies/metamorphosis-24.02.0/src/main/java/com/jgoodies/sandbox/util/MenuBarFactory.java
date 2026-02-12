package com.jgoodies.sandbox.util;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.framework.osx.OSXUtils;
import com.jgoodies.looks.common.MenuSelectionProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JMenuBar;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/util/MenuBarFactory.class */
public final class MenuBarFactory {
    private MenuBarFactory() {
    }

    public static JMenuBar createMenuBar(boolean visibleAlways) {
        JMenuBar menuBar = new JMenuBar();
        if (SystemUtils.IS_OS_MAC && OSXUtils.getUseScreenMenuBar()) {
            return menuBar;
        }
        if (!visibleAlways) {
            menuBar.setVisible(false);
            MenuSelectionManager.defaultManager().addChangeListener(new MenuActivitationHandler(menuBar));
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusedWindow", new FocusedWindowChangeHandler(menuBar));
        }
        if (UIManager.getLookAndFeel().getID() != "Windows") {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new MenuSelectionProcessor());
        }
        return menuBar;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/util/MenuBarFactory$MenuActivitationHandler.class */
    private static final class MenuActivitationHandler implements ChangeListener {
        private final JMenuBar menuBar;

        private MenuActivitationHandler(JMenuBar menuBar) {
            this.menuBar = menuBar;
        }

        public void stateChanged(ChangeEvent e) {
            JMenuBar[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            boolean visible = selectedPath.length > 0 && selectedPath[0] == this.menuBar;
            this.menuBar.setVisible(visible);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/util/MenuBarFactory$FocusedWindowChangeHandler.class */
    private static final class FocusedWindowChangeHandler implements PropertyChangeListener {
        private final JMenuBar menuBar;

        private FocusedWindowChangeHandler(JMenuBar menuBar) {
            this.menuBar = menuBar;
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            Window focusedWindow = (Window) evt.getNewValue();
            Window parent = SwingUtilities.getWindowAncestor(this.menuBar);
            if (parent != focusedWindow) {
                this.menuBar.setVisible(false);
            }
        }
    }
}
