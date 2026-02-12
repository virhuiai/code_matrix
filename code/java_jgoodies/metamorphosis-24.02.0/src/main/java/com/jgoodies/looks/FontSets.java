package com.jgoodies.looks;

import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Font;
import javax.swing.plaf.FontUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/FontSets.class */
public final class FontSets {
    private static FontSet logicalFontSet;

    private FontSets() {
    }

    public static FontSet createDefaultFontSet(Font controlFont) {
        return createDefaultFontSet(controlFont, null);
    }

    public static FontSet createDefaultFontSet(Font controlFont, Font menuFont) {
        return createDefaultFontSet(controlFont, menuFont, null, null, null, null);
    }

    public static FontSet createDefaultFontSet(Font controlFont, Font menuFont, Font titleFont) {
        return createDefaultFontSet(controlFont, menuFont, titleFont, null, null, null);
    }

    public static FontSet createDefaultFontSet(Font controlFont, Font menuFont, Font titleFont, Font messageFont, Font smallFont, Font windowTitleFont) {
        return new DefaultFontSet(controlFont, menuFont, titleFont, messageFont, smallFont, windowTitleFont);
    }

    public static FontSet getLogicalFontSet() {
        if (logicalFontSet == null) {
            logicalFontSet = new LogicalFontSet();
        }
        return logicalFontSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/FontSets$DefaultFontSet.class */
    public static final class DefaultFontSet implements FontSet {
        private final FontUIResource controlFont;
        private final FontUIResource menuFont;
        private final FontUIResource titleFont;
        private final FontUIResource messageFont;
        private final FontUIResource smallFont;
        private final FontUIResource windowTitleFont;

        DefaultFontSet(Font controlFont, Font menuFont, Font titleFont, Font messageFont, Font smallFont, Font windowTitleFont) {
            this.controlFont = FontSets.asFontUIResource(controlFont);
            this.menuFont = menuFont != null ? FontSets.asFontUIResource(menuFont) : this.controlFont;
            this.titleFont = titleFont != null ? FontSets.asFontUIResource(titleFont) : this.controlFont;
            this.messageFont = messageFont != null ? FontSets.asFontUIResource(messageFont) : this.controlFont;
            this.smallFont = FontSets.asFontUIResource(smallFont != null ? smallFont : controlFont.deriveFont(controlFont.getSize2D() - ScreenScaling.toPhysical(2.0f)));
            this.windowTitleFont = windowTitleFont != null ? FontSets.asFontUIResource(windowTitleFont) : this.titleFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getControlFont() {
            return this.controlFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getMenuFont() {
            return this.menuFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getTitleFont() {
            return this.titleFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getWindowTitleFont() {
            return this.windowTitleFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getSmallFont() {
            return this.smallFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getMessageFont() {
            return this.messageFont;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/FontSets$LogicalFontSet.class */
    private static final class LogicalFontSet implements FontSet {
        private FontUIResource controlFont;
        private FontUIResource titleFont;
        private FontUIResource systemFont;
        private FontUIResource smallFont;

        private LogicalFontSet() {
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getControlFont() {
            if (this.controlFont == null) {
                this.controlFont = FontSets.asFontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", 0, 12)));
            }
            return this.controlFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getMenuFont() {
            return getControlFont();
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getTitleFont() {
            if (this.titleFont == null) {
                this.titleFont = FontSets.asFontUIResource(getControlFont().deriveFont(1));
            }
            return this.titleFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getSmallFont() {
            if (this.smallFont == null) {
                this.smallFont = FontSets.asFontUIResource(Font.getFont("swing.plaf.metal.smallFont", new Font("Dialog", 0, 10)));
            }
            return this.smallFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getMessageFont() {
            if (this.systemFont == null) {
                this.systemFont = FontSets.asFontUIResource(Font.getFont("swing.plaf.metal.systemFont", new Font("Dialog", 0, 12)));
            }
            return this.systemFont;
        }

        @Override // com.jgoodies.looks.FontSet
        public FontUIResource getWindowTitleFont() {
            return getTitleFont();
        }
    }

    static FontUIResource asFontUIResource(Font font) {
        return font instanceof FontUIResource ? (FontUIResource) font : new FontUIResource(font);
    }
}
