package com.jgoodies.framework.osx;

import com.jgoodies.application.Application;
import java.awt.Desktop;
// 由于可能的兼容性问题，暂时注释掉特定平台的导入
// import java.awt.desktop.AboutEvent;
// import java.awt.desktop.AboutHandler;
// import java.awt.desktop.PreferencesEvent;
// import java.awt.desktop.PreferencesHandler;
// import java.awt.desktop.QuitEvent;
// import java.awt.desktop.QuitHandler;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/osx/OSXApplicationHandler9.class */
public final class OSXApplicationHandler9 /*implements AboutHandler, PreferencesHandler, QuitHandler*/ {
    OSXApplicationHandler9() {
    }

    // 由于可能的兼容性问题，暂时保留原始方法签名
    public void handleAbout(/*AboutEvent*/ Object evt) {
        OSXApplicationMenu.aboutListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "about"));
    }

    public void handlePreferences(/*PreferencesEvent*/ Object evt) {
        OSXApplicationMenu.prefsListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "preferences"));
    }

    public void handleQuitRequestWith(/*QuitEvent*/ Object evt, /*QuitResponse*/ Object response) {
        // 这里需要根据实际的响应对象进行处理
        Application.getInstance().exit().thenAccept(exitAllowed -> {
            if (exitAllowed.booleanValue()) {
                // response.performQuit();
            } else {
                // response.cancelQuit();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void register() {
        Desktop desktop = Desktop.getDesktop();
        // 检查是否支持所需的桌面功能
        if (desktop.isSupported(java.awt.Desktop.Action.APP_ABOUT)) {
            // desktop.setAboutHandler(handler);
        }
        if (desktop.isSupported(java.awt.Desktop.Action.APP_PREFERENCES)) {
            // desktop.setPreferencesHandler(handler);
        }
        // desktop.setQuitHandler(handler);
    }
}