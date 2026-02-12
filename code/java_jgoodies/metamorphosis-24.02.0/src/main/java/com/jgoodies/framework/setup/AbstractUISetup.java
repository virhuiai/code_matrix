package com.jgoodies.framework.setup;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.jsdl.JSDLCommonSetup;
import com.jgoodies.common.jsdl.util.HTMLUtils;
import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.dialogs.basics.accessibility.ControlList;
import com.jgoodies.framework.setup.AbstractUISetup;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticMetroLookAndFeel;
import com.jgoodies.sandbox.accessibility.AccessibilityChecker;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/setup/AbstractUISetup.class */
public abstract class AbstractUISetup<T extends AbstractUISetup<?>> {
    private static final Logger LOGGER = Logger.getLogger(AbstractUISetup.class.getName());
    protected Boolean optimizedForScreenReader = null;
    protected BodyTextMode bodyTextMode = null;
    protected boolean forceCrossPlatformLookAndFeelOnMac = false;
    protected boolean replaceWindowsClassicByPlastic = true;
    protected boolean muteAuditoryFeedback = false;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/setup/AbstractUISetup$BasicUISetup.class */
    public static final class BasicUISetup extends AbstractUISetup<BasicUISetup> {
    }

    protected AbstractUISetup() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T optimizedForScreenReader(Boolean b) {
        this.optimizedForScreenReader = b;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T bodyTextMode(BodyTextMode value) {
        this.bodyTextMode = value;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T forceCrossPlatformLookAndFeelOnMac(boolean b) {
        this.forceCrossPlatformLookAndFeelOnMac = b;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T replaceWindowsClassicByPlastic(boolean b) {
        this.replaceWindowsClassicByPlastic = b;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T muteAuditoryFeedback() {
        this.muteAuditoryFeedback = true;
        return this;
    }

    public void setup() {
        setupScaling();
        try {
            setupLookAndFeel();
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.WARNING, "Failed to setup the desired look&feel.", e);
            e.printStackTrace();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            LOGGER.log(Level.WARNING, "Failed to create the desired look&feel.", (Throwable) ex);
            ex.printStackTrace();
        }
        setupAuditoryFeedback();
        setupFonts();
        setupAccessibility();
        setupExtras();
    }

    protected void setupLookAndFeel() throws UnsupportedLookAndFeelException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        if (SystemUtils.IS_OS_MAC && !this.forceCrossPlatformLookAndFeelOnMac) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            return;
        }
        boolean isWindows = SystemUtils.IS_OS_WINDOWS;
        if (!isWindows) {
            UIManager.setLookAndFeel(Options.getCrossPlatformLookAndFeelClassName());
            return;
        }
        boolean isWindowsClassic = !SystemUtils.IS_LAF_WINDOWS_XP_ENABLED;
        boolean highContrast = highContrastRequested();
        boolean nativeLook = (isWindowsClassic && !highContrast && this.replaceWindowsClassicByPlastic) ? false : true;
        if (nativeLook) {
            UIManager.setLookAndFeel(Options.JGOODIES_WINDOWS_NAME);
        } else {
            UIManager.setLookAndFeel(new PlasticMetroLookAndFeel());
        }
    }

    protected void setupAccessibility() {
        if (this.optimizedForScreenReader == null) {
            return;
        }
        LOGGER.config("Optimized for screen reader: " + this.optimizedForScreenReader);
        JSDLCommonSetup.setOptimizedForScreenReader(this.optimizedForScreenReader.booleanValue());
        FocusTraversalUtils.setAcceptNonEditableTextComponents(this.optimizedForScreenReader.booleanValue());
        if (this.optimizedForScreenReader.booleanValue()) {
            ControlList.install();
        }
        AccessibilityChecker.installIfEnabledBySystemProperty();
    }

    protected void setupScaling() {
        if (this.bodyTextMode == null) {
            return;
        }
        Options.setLargerBodyTextEnabled(this.bodyTextMode == BodyTextMode.LARGER);
    }

    protected void setupFonts() {
        HTMLUtils.addDefaultStyleSheetRuleToSharedStyleSheet();
    }

    protected void setupExtras() {
        setupDialogs();
    }

    protected void setupAuditoryFeedback() {
        if (this.muteAuditoryFeedback) {
            UIManager.put("AuditoryCues.playList", new Object[0]);
        }
    }

    protected static final void setupDialogs() {
        try {
            Class<?> clazz = Class.forName("com.jgoodies.dialogs.core.style.StyleManager");
            Method method = clazz.getMethod("getStyle", new Class[0]);
            method.invoke(null, new Object[0]);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
        }
    }

    protected static final boolean highContrastRequested() {
        return Boolean.TRUE.equals(Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on"));
    }
}
