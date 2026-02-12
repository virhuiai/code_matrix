package com.jgoodies.framework.osx;

import com.jgoodies.application.Application;
import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/osx/OSXApplicationHandler9.class */
public final class OSXApplicationHandler9 implements AboutHandler, PreferencesHandler, QuitHandler {
    OSXApplicationHandler9() {
    }

    public void handleAbout(AboutEvent evt) {
        OSXApplicationMenu.aboutListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "about"));
    }

    public void handlePreferences(PreferencesEvent evt) {
        OSXApplicationMenu.prefsListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "preferences"));
    }

    public void handleQuitRequestWith(QuitEvent evt, QuitResponse response) {
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
        Desktop desktop = Desktop.getDesktop();
        OSXApplicationHandler9 handler = new OSXApplicationHandler9();
        if (OSXApplicationMenu.aboutListener != null) {
            desktop.setAboutHandler(handler);
        }
        if (OSXApplicationMenu.prefsListener != null) {
            desktop.setPreferencesHandler(handler);
        }
        desktop.setQuitHandler(handler);
    }
}
