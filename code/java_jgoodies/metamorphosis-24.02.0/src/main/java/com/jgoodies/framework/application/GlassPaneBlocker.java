package com.jgoodies.framework.application;

import com.jgoodies.application.AbstractInputBlocker;
import com.jgoodies.application.Application;
import com.jgoodies.application.BlockingScope;
import com.jgoodies.application.Task;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/application/GlassPaneBlocker.class */
public class GlassPaneBlocker extends AbstractInputBlocker {
    private static final String OLD_GLASS_PANE_KEY = "GlassPaneBlocker.oldGlassPane";
    private static final String OLD_GLASS_PANE_VISIBLE_KEY = "GlassPaneBlocker.oldGlassPane.visible";

    @Override // com.jgoodies.application.AbstractInputBlocker
    protected void block(Task<?, ?> task, Window window, int oldCounter) {
        logBlock(task, BlockingScope.WINDOW, window);
        if (window instanceof RootPaneContainer) {
            block((RootPaneContainer) window, oldCounter);
        }
    }

    @Override // com.jgoodies.application.AbstractInputBlocker
    protected void block(Task<?, ?> task, Application application, int oldCounter) {
        for (RootPaneContainer rootPaneContainer : Frame.getFrames()) {
            if (rootPaneContainer instanceof RootPaneContainer) {
                block(rootPaneContainer, oldCounter);
            }
            for (RootPaneContainer rootPaneContainer2 : rootPaneContainer.getOwnedWindows()) {
                if (rootPaneContainer2 instanceof RootPaneContainer) {
                    int oldWindowCounter = increaseBlockingCounter(rootPaneContainer2);
                    block(rootPaneContainer2, oldWindowCounter);
                }
            }
        }
    }

    @Override // com.jgoodies.application.AbstractInputBlocker
    protected void unblock(Task<?, ?> task, Window window, int newCounter) {
        logUnblock(task, BlockingScope.WINDOW, window);
        if (window instanceof RootPaneContainer) {
            unblock((RootPaneContainer) window, newCounter);
        }
    }

    @Override // com.jgoodies.application.AbstractInputBlocker
    protected void unblock(Task<?, ?> task, Application application, int newCounter) {
        for (RootPaneContainer rootPaneContainer : Frame.getFrames()) {
            if (rootPaneContainer instanceof RootPaneContainer) {
                unblock(rootPaneContainer, newCounter);
            }
            for (RootPaneContainer rootPaneContainer2 : rootPaneContainer.getOwnedWindows()) {
                if (rootPaneContainer2 instanceof RootPaneContainer) {
                    int newWindowCounter = decreaseBlockingCounter(rootPaneContainer2);
                    unblock(rootPaneContainer2, newWindowCounter);
                }
            }
        }
    }

    private static void block(RootPaneContainer c, int oldCounter) {
        JRootPane rootPane = c.getRootPane();
        if (oldCounter == 0) {
            Component oldGlassPane = c.getGlassPane();
            rootPane.putClientProperty(OLD_GLASS_PANE_KEY, oldGlassPane);
            rootPane.putClientProperty(OLD_GLASS_PANE_VISIBLE_KEY, Boolean.valueOf(oldGlassPane.isVisible()));
            JComponent blockingGlassPane = new BlockingGlassPane();
            c.setGlassPane(blockingGlassPane);
            blockingGlassPane.setVisible(true);
        }
    }

    private static void unblock(RootPaneContainer c, int newCounter) {
        JRootPane rootPane = c.getRootPane();
        if (newCounter == 0) {
            Component glassPane = c.getGlassPane();
            if (glassPane instanceof BlockingGlassPane) {
                glassPane.setVisible(false);
            }
            Component oldGlassPane = (Component) rootPane.getClientProperty(OLD_GLASS_PANE_KEY);
            Boolean oldGlassPaneVisible = (Boolean) rootPane.getClientProperty(OLD_GLASS_PANE_VISIBLE_KEY);
            c.setGlassPane(oldGlassPane);
            oldGlassPane.setVisible(oldGlassPaneVisible.booleanValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/application/GlassPaneBlocker$BlockingGlassPane.class */
    public static final class BlockingGlassPane extends JComponent {
        private static final KeyEventDispatcher BLOCKING_DISPATCHER = new BlockingKeyEventDispatcher();
        private static final long EVENT_MASK = 131120;

        BlockingGlassPane() {
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(3));
            enableEvents(EVENT_MASK);
        }

        public void setVisible(boolean visible) {
            if (visible) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(BLOCKING_DISPATCHER);
            } else {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(BLOCKING_DISPATCHER);
            }
            super.setVisible(visible);
        }

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/application/GlassPaneBlocker$BlockingGlassPane$BlockingKeyEventDispatcher.class */
        private static final class BlockingKeyEventDispatcher implements KeyEventDispatcher {
            private BlockingKeyEventDispatcher() {
            }

            public boolean dispatchKeyEvent(KeyEvent e) {
                JRootPane rootPane;
                Object source = e.getSource();
                if (!(source instanceof JComponent) || (rootPane = ((JComponent) source).getRootPane()) == null) {
                    return false;
                }
                Component glassPane = rootPane.getGlassPane();
                return (glassPane instanceof BlockingGlassPane) && glassPane.isVisible();
            }
        }
    }
}
