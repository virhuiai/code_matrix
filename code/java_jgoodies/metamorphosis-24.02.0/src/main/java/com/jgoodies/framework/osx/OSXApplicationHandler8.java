package com.jgoodies.framework.osx;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.jgoodies.application.Application;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/osx/OSXApplicationHandler8.class */
public final class OSXApplicationHandler8 implements AboutHandler, PreferencesHandler, QuitHandler {
    OSXApplicationHandler8() {
    }

    public void handleAbout(AppEvent.AboutEvent evt) {
        OSXApplicationMenu.aboutListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "about"));
    }

    public void handlePreferences(AppEvent.PreferencesEvent evt) {
        OSXApplicationMenu.prefsListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "preferences"));
    }

    public void handleQuitRequestWith(AppEvent.QuitEvent evt, QuitResponse response) {
        Application.getInstance().exit().thenAccept(exitAllowed -> {
            if (exitAllowed.booleanValue()) {
                response.performQuit();
            } else {
                response.cancelQuit();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void register() {
        com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
        OSXApplicationHandler8 handler = new OSXApplicationHandler8();
        if (OSXApplicationMenu.aboutListener != null) {
            application.setAboutHandler(handler);
        }
        if (OSXApplicationMenu.prefsListener != null) {
            application.setPreferencesHandler(handler);
        }
        application.setQuitHandler(handler);
    }
}
