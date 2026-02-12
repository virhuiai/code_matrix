package com.jgoodies.looks.common;

import java.awt.KeyEventPostProcessor;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.ComboPopup;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/MenuSelectionProcessor.class */
public final class MenuSelectionProcessor implements KeyEventPostProcessor {
    private boolean altKeyPressed = false;
    private boolean menuCanceledOnPress = false;

    public boolean postProcessKeyEvent(KeyEvent ev) {
        if (ev.isConsumed()) {
            return false;
        }
        if (ev.getKeyCode() == 18) {
            if (ev.getID() == 401) {
                if (!this.altKeyPressed) {
                    altPressed(ev);
                }
                this.altKeyPressed = true;
                return true;
            }
            if (ev.getID() == 402) {
                if (this.altKeyPressed) {
                    altReleased(ev);
                }
                this.altKeyPressed = false;
                return false;
            }
            return false;
        }
        this.altKeyPressed = false;
        return false;
    }

    private void altPressed(KeyEvent ev) {
        MenuSelectionManager msm = MenuSelectionManager.defaultManager();
        MenuElement[] path = msm.getSelectedPath();
        if (path.length > 0 && !(path[0] instanceof ComboPopup)) {
            msm.clearSelectedPath();
            this.menuCanceledOnPress = true;
            ev.consume();
        } else {
            if (path.length > 0) {
                this.menuCanceledOnPress = false;
                ev.consume();
                return;
            }
            this.menuCanceledOnPress = false;
            JMenuBar mbar = getMenuBar(ev);
            JMenu menu = mbar != null ? mbar.getMenu(0) : null;
            if (menu != null) {
                ev.consume();
            }
        }
    }

    private void altReleased(KeyEvent ev) {
        if (this.menuCanceledOnPress) {
            return;
        }
        MenuSelectionManager msm = MenuSelectionManager.defaultManager();
        if (msm.getSelectedPath().length == 0) {
            MenuElement menuBar = getMenuBar(ev);
            JMenu menu = menuBar != null ? menuBar.getMenu(0) : null;
            if (menu != null) {
                MenuElement[] path = {menuBar, menu};
                msm.setSelectedPath(path);
            }
        }
    }

    private static JMenuBar getMenuBar(KeyEvent ev) {
        JRootPane root = SwingUtilities.getRootPane(ev.getComponent());
        Window winAncestor = root == null ? null : SwingUtilities.getWindowAncestor(root);
        JMenuBar mbar = root != null ? root.getJMenuBar() : null;
        if (mbar == null && (winAncestor instanceof JFrame)) {
            mbar = ((JFrame) winAncestor).getJMenuBar();
        }
        return mbar;
    }
}
