package com.jgoodies.looks;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.text.StyleContext;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/Fonts.class */
public final class Fonts {
    public static final String SEGOE_UI_NAME = "Segoe UI";
    static final String WINDOWS_DEFAULT_GUI_FONT_KEY = "win.defaultGUI.font";
    static final String WINDOWS_ICON_FONT_KEY = "win.icon.font";

    private Fonts() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Font asCompositeFontUIResource(Font font) {
        if (font == null) {
            return null;
        }
        return StyleContext.getDefaultStyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    }

    public static Font getWindowsControlFont() {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new UnsupportedOperationException("The Windows control font can be computed only on the Windows platform.");
        }
        Font font = getDesktopFont(WINDOWS_ICON_FONT_KEY);
        return font != null ? font : getFallbackFont();
    }

    private static Font getFallbackFont() {
        return new Font("Dialog", 0, ScreenScaling.toPhysical(12));
    }

    private static Font getDesktopFont(String fontName) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return (Font) toolkit.getDesktopProperty(fontName);
    }
}
