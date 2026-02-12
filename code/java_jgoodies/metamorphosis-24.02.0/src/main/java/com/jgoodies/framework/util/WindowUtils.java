package com.jgoodies.framework.util;

import com.jgoodies.binding.value.DelayedReadValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.internal.CommonFormats;
import com.jgoodies.common.jsdl.internal.MyListeners;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/WindowUtils.class */
public final class WindowUtils {
    private static final String WINDOW_BOUNDS_KEYPART = "window_bounds";
    private static final String FRAME_STATE_KEYPART = "frame_state";

    private WindowUtils() {
    }

    public static void storeBounds(Preferences prefs, JWindow window) {
        storeBounds0(prefs, window);
    }

    public static void storeBounds(Preferences prefs, JDialog dialog) {
        storeBounds0(prefs, dialog);
    }

    public static void storeBounds(Preferences prefs, Frame frame) {
        boolean maximizedBoth = frame.getExtendedState() == 6;
        if (frame.isVisible() && !maximizedBoth) {
            storeBounds0(prefs, frame);
        }
    }

    public static void storeState(Preferences prefs, Frame frame) {
        Preferences screenConfigNode = ScreenUtils.screenConfigurationNode(prefs);
        screenConfigNode.putInt(frameStateKey(frame), frame.getExtendedState());
    }

    public static void restoreBounds(JWindow window, Preferences prefs) {
        restoreBounds0(window, prefs);
    }

    public static void restoreBounds(JDialog dialog, Preferences prefs) {
        restoreBounds0(dialog, prefs);
    }

    public static boolean restoreBounds(JFrame frame, Preferences prefs) {
        return restoreBounds0(frame, prefs);
    }

    public static void restoreState(Frame frame, Preferences prefs) {
        restoreState(frame, prefs, true);
    }

    public static void restoreState(Frame frame, Preferences prefs, boolean restoreIconified) {
        int state = getStoredState(frame, prefs);
        if (state == -1) {
            return;
        }
        if (state != 1 || !restoreIconified) {
            frame.setExtendedState(state);
        }
    }

    public static void clearBounds(Preferences prefs, Window window) {
        ScreenUtils.screenConfigurationNode(prefs).put(windowBoundsKey(window), "");
    }

    public static void clearState(Preferences prefs, Frame frame) {
        ScreenUtils.screenConfigurationNode(prefs).putInt(frameStateKey(frame), -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void storeBounds0(Preferences prefs, Window window) {
        Preferences screenConfigNode = ScreenUtils.screenConfigurationNode(prefs);
        screenConfigNode.put(windowBoundsKey(window), encodeRectangle(window.getBounds()));
    }

    private static boolean restoreBounds0(Window window, Preferences prefs) {
        Rectangle bounds = getStoredBounds(prefs, window);
        if (bounds != null) {
            window.setBounds(bounds);
            return true;
        }
        return false;
    }

    private static Rectangle getStoredBounds(Preferences prefs, Window window) {
        Preferences screenConfigNode = ScreenUtils.screenConfigurationNode(prefs);
        return decodeRectangle(screenConfigNode.get(windowBoundsKey(window), ""));
    }

    private static int getStoredState(Frame frame, Preferences prefs) {
        Preferences screenConfigNode = ScreenUtils.screenConfigurationNode(prefs);
        return screenConfigNode.getInt(frameStateKey(frame), -1);
    }

    private static String windowBoundsKey(Window window) {
        Preconditions.checkNotNull(window, Messages.MUST_NOT_BE_NULL, "window");
        Preconditions.checkArgument(Strings.isNotBlank(window.getName()), "The window must have a non-blank name.");
        return window.getName() + '.' + WINDOW_BOUNDS_KEYPART;
    }

    private static String frameStateKey(Frame frame) {
        Preconditions.checkNotNull(frame, Messages.MUST_NOT_BE_NULL, "frame");
        Preconditions.checkArgument(Strings.isNotBlank(frame.getName()), "The frame must have a non-blank name.");
        return frame.getName() + '.' + FRAME_STATE_KEYPART;
    }

    private static String encodeRectangle(Rectangle r) {
        return Integer.toString(r.x) + CommonFormats.COMMA_DELIMITER + r.y + CommonFormats.COMMA_DELIMITER + r.width + CommonFormats.COMMA_DELIMITER + r.height;
    }

    private static Rectangle decodeRectangle(String encodedRectangle) {
        StringTokenizer tokenizer = new StringTokenizer(encodedRectangle, " ,");
        try {
            int x = Integer.parseInt(tokenizer.nextToken());
            int y = Integer.parseInt(tokenizer.nextToken());
            int w = Integer.parseInt(tokenizer.nextToken());
            int h = Integer.parseInt(tokenizer.nextToken());
            return new Rectangle(x, y, w, h);
        } catch (Exception e) {
            return null;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/WindowUtils$PersistencyHandler.class */
    public static final class PersistencyHandler extends ComponentAdapter {
        private static final int COMPONENT_MOVED_DELAY = 50;
        private final Preferences preferences;
        private final Window window;
        private ValueModel componentMovedModel;

        public PersistencyHandler(JDialog dialog, Preferences preferences) {
            this.window = dialog;
            this.preferences = preferences;
            init();
        }

        public PersistencyHandler(JFrame frame, Preferences preferences) {
            this.window = frame;
            this.preferences = preferences;
            init();
        }

        public PersistencyHandler(JWindow window, Preferences preferences) {
            this.window = window;
            this.preferences = preferences;
            init();
        }

        private void init() {
            this.componentMovedModel = new ValueHolder();
            ValueModel delayedComponentMovedModel = new DelayedReadValueModel(this.componentMovedModel, COMPONENT_MOVED_DELAY, true);
            delayedComponentMovedModel.addValueChangeListener(MyListeners.delayed(this::delayedComponentMoved));
        }

        public void componentMoved(ComponentEvent evt) {
            this.componentMovedModel.setValue(this.window.getBounds());
        }

        public void componentResized(ComponentEvent evt) {
            if (!(this.window instanceof JFrame)) {
                WindowUtils.storeBounds0(this.preferences, this.window);
            } else {
                WindowUtils.storeBounds(this.preferences, (Frame) this.window);
                WindowUtils.storeState(this.preferences, this.window);
            }
        }

        private void delayedComponentMoved(PropertyChangeEvent evt) {
            WindowUtils.storeBounds0(this.preferences, this.window);
            if (this.window instanceof JFrame) {
                WindowUtils.storeState(this.preferences, this.window);
            }
        }
    }
}
