package com.jgoodies.common.jsdl.internal;

import com.jgoodies.looks.Options;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/AudioUtils.class */
public final class AudioUtils {
    private AudioUtils() {
    }

    public static void playSound(JComponent c, Object actionKey) {
        ActionMap map;
        Action audioAction;
        LookAndFeel laf = UIManager.getLookAndFeel();
        if ((laf instanceof BasicLookAndFeel) && (map = c.getActionMap()) != null && (audioAction = map.get(actionKey)) != null) {
            playSound(audioAction);
        }
    }

    public static void installSharedActionMap(JComponent c, String defaultsKey) {
        ActionMap map = (ActionMap) UIManager.get(defaultsKey);
        if (map == null) {
            map = new ActionMapUIResource();
            installAudioActionMap(map);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        SwingUtilities.replaceUIActionMap(c, map);
    }

    static void installAudioActionMap(ActionMap map) {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (!"JGoodies Windows".equals(laf.getName())) {
            return;
        }
        try {
            try {
                Method method = Class.forName(Options.JGOODIES_WINDOWS_NAME).getDeclaredMethod("getAudioActionMap", new Class[0]);
                try {
                    ActionMap parentMap = (ActionMap) method.invoke(laf, new Object[0]);
                    map.setParent(parentMap);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            } catch (NoSuchMethodException | SecurityException ex2) {
                ex2.printStackTrace();
            }
        } catch (ClassNotFoundException ex3) {
            ex3.printStackTrace();
        }
    }

    private static void playSound(Action audioAction) {
        Object[] audioStrings = (Object[]) UIManager.get("AuditoryCues.playList");
        if (audioAction == null || audioStrings == null) {
            return;
        }
        String actionName = (String) audioAction.getValue("Name");
        for (Object audioString : audioStrings) {
            if (audioString.equals(actionName)) {
                audioAction.actionPerformed(new ActionEvent(AudioUtils.class, 1001, actionName));
            }
        }
    }
}
