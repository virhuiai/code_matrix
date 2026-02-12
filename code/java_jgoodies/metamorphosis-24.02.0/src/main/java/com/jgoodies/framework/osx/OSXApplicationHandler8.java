package com.jgoodies.framework.osx;

import com.jgoodies.application.Application;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/osx/OSXApplicationHandler8.class */
public final class OSXApplicationHandler8 {
    OSXApplicationHandler8() {
    }

    public void handleAbout() {
        if (OSXApplicationMenu.aboutListener != null) {
            OSXApplicationMenu.aboutListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "about"));
        }
    }

    public void handlePreferences() {
        if (OSXApplicationMenu.prefsListener != null) {
            OSXApplicationMenu.prefsListener.actionPerformed(new ActionEvent(JOptionPane.getRootFrame(), 0, "preferences"));
        }
    }

    public void handleQuit() {
        Application.getInstance().exit().thenAccept(exitAllowed -> {
            if (exitAllowed.booleanValue()) {
                System.exit(0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void register() {
        // 在Java 8中，我们不能直接注册这些处理器
        // 可以通过其他方式实现类似功能
        Desktop desktop = Desktop.getDesktop();
        // 这里可以添加一些基本的桌面功能支持
    }
}