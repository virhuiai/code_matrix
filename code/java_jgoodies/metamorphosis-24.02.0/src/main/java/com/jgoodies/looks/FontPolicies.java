package com.jgoodies.looks;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Font;
import javax.swing.UIDefaults;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/FontPolicies.class */
public final class FontPolicies {
    private FontPolicies() {
    }

    public static FontPolicy createFixedPolicy(FontSet fontSet) {
        return new FixedPolicy(fontSet);
    }

    public static FontPolicy customSettingsPolicy(FontPolicy defaultPolicy) {
        return new CustomSettingsPolicy(defaultPolicy);
    }

    public static FontPolicy getDefaultPlasticPolicy() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return getDefaultWindowsPolicy();
        }
        return getLogicalFontsPolicy();
    }

    public static FontPolicy getDefaultWindowsPolicy() {
        return new DefaultWindowsPolicy();
    }

    public static FontPolicy getLogicalFontsPolicy() {
        return createFixedPolicy(FontSets.getLogicalFontSet());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static FontSet getCustomFontSet(String lafName) {
        String controlFontKey = lafName + ".controlFont";
        String menuFontKey = lafName + ".menuFont";
        String decodedControlFont = LookUtils.getSystemProperty(controlFontKey);
        if (decodedControlFont == null) {
            return null;
        }
        Font controlFont = Fonts.asCompositeFontUIResource(Font.decode(decodedControlFont));
        String decodedMenuFont = LookUtils.getSystemProperty(menuFontKey);
        Font menuFont = decodedMenuFont != null ? Fonts.asCompositeFontUIResource(Font.decode(decodedMenuFont)) : null;
        return FontSets.createDefaultFontSet(controlFont, menuFont);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/FontPolicies$CustomSettingsPolicy.class */
    public static final class CustomSettingsPolicy implements FontPolicy {
        private final FontPolicy wrappedPolicy;

        CustomSettingsPolicy(FontPolicy wrappedPolicy) {
            this.wrappedPolicy = wrappedPolicy;
        }

        @Override // com.jgoodies.looks.FontPolicy
        public FontSet getFontSet(String lafName, UIDefaults table) {
            FontSet customFontSet = FontPolicies.getCustomFontSet(lafName);
            if (customFontSet != null) {
                return customFontSet;
            }
            return this.wrappedPolicy.getFontSet(lafName, table);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/FontPolicies$DefaultWindowsPolicy.class */
    public static final class DefaultWindowsPolicy implements FontPolicy {
        private DefaultWindowsPolicy() {
        }

        @Override // com.jgoodies.looks.FontPolicy
        public FontSet getFontSet(String lafName, UIDefaults table) {
            Font plainControlFont = Fonts.getWindowsControlFont();
            if (Options.isLargerBodyTextEnabled()) {
                plainControlFont = plainControlFont.deriveFont((plainControlFont.getSize2D() * 14.0f) / 12.0f);
            }
            Font controlFont = Fonts.asCompositeFontUIResource(plainControlFont);
            Font smallFont = lafName.equals("Plastic") ? controlFont.deriveFont(controlFont.getSize2D() - ScreenScaling.toPhysical(2.0f)) : plainControlFont;
            return FontSets.createDefaultFontSet(controlFont, controlFont, controlFont, controlFont, smallFont, controlFont);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/FontPolicies$FixedPolicy.class */
    public static final class FixedPolicy implements FontPolicy {
        private final FontSet fontSet;

        FixedPolicy(FontSet fontSet) {
            this.fontSet = fontSet;
        }

        @Override // com.jgoodies.looks.FontPolicy
        public FontSet getFontSet(String lafName, UIDefaults table) {
            return this.fontSet;
        }
    }
}
