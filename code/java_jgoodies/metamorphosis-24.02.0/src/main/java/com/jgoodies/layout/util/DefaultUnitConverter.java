package com.jgoodies.layout.util;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LayoutManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/util/DefaultUnitConverter.class */
public final class DefaultUnitConverter extends AbstractUnitConverter {
    public static final String PROPERTY_AVERAGE_CHARACTER_WIDTH_TEST_STRING = "averageCharacterWidthTestString";
    public static final String PROPERTY_DEFAULT_DIALOG_FONT = "defaultDialogFont";
    public static final String OLD_AVERAGE_CHARACTER_TEST_STRING = "X";
    public static final String MODERN_AVERAGE_CHARACTER_TEST_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final String BALANCED_AVERAGE_CHARACTER_TEST_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Logger LOGGER = Logger.getLogger(DefaultUnitConverter.class.getName());
    private static DefaultUnitConverter instance;
    private Font defaultDialogFont;
    private String averageCharWidthTestString = BALANCED_AVERAGE_CHARACTER_TEST_STRING;
    private DialogBaseUnits cachedGlobalDialogBaseUnits = null;
    private DialogBaseUnits cachedDialogBaseUnits = null;
    private FontMetrics cachedFontMetrics = null;
    private Font cachedDefaultDialogFont = null;

    private DefaultUnitConverter() {
    }

    public static DefaultUnitConverter getInstance() {
        if (instance == null) {
            instance = new DefaultUnitConverter();
        }
        return instance;
    }

    public String getAverageCharacterWidthTestString() {
        return this.averageCharWidthTestString;
    }

    public void setAverageCharacterWidthTestString(String newTestString) {
        Preconditions.checkNotBlank(newTestString, Messages.MUST_NOT_BE_BLANK, "test string");
        String oldTestString = this.averageCharWidthTestString;
        this.averageCharWidthTestString = newTestString;
        clearCache();
        firePropertyChange(PROPERTY_AVERAGE_CHARACTER_WIDTH_TEST_STRING, oldTestString, newTestString);
    }

    public Font getDefaultDialogFont() {
        return this.defaultDialogFont != null ? this.defaultDialogFont : getCachedDefaultDialogFont();
    }

    public void setDefaultDialogFont(Font newFont) {
        Font oldFont = this.defaultDialogFont;
        this.defaultDialogFont = newFont;
        clearCache();
        firePropertyChange(PROPERTY_DEFAULT_DIALOG_FONT, oldFont, newFont);
    }

    @Override // com.jgoodies.layout.util.AbstractUnitConverter
    protected double getDialogBaseUnitsX(Component component) {
        return getDialogBaseUnits(component).x;
    }

    @Override // com.jgoodies.layout.util.AbstractUnitConverter
    protected double getDialogBaseUnitsY(Component component) {
        return getDialogBaseUnits(component).y;
    }

    private DialogBaseUnits getGlobalDialogBaseUnits() {
        if (this.cachedGlobalDialogBaseUnits == null) {
            this.cachedGlobalDialogBaseUnits = computeGlobalDialogBaseUnits();
        }
        return this.cachedGlobalDialogBaseUnits;
    }

    private DialogBaseUnits getDialogBaseUnits(Component c) {
        FormUtils.ensureValidCache();
        if (c == null) {
            return getGlobalDialogBaseUnits();
        }
        FontMetrics fm = c.getFontMetrics(getDefaultDialogFont());
        if (fm.equals(this.cachedFontMetrics)) {
            return this.cachedDialogBaseUnits;
        }
        DialogBaseUnits dialogBaseUnits = computeDialogBaseUnits(fm);
        this.cachedFontMetrics = fm;
        this.cachedDialogBaseUnits = dialogBaseUnits;
        return dialogBaseUnits;
    }

    private DialogBaseUnits computeDialogBaseUnits(FontMetrics metrics) {
        double averageCharWidth = computeAverageCharWidth(metrics, this.averageCharWidthTestString);
        int ascent = metrics.getAscent();
        double height = ascent > 14 ? ascent : ascent + ((15 - ascent) / 3);
        DialogBaseUnits dialogBaseUnits = new DialogBaseUnits(averageCharWidth, height);
        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.config("Computed dialog base units " + dialogBaseUnits + " for: " + metrics.getFont());
        }
        return dialogBaseUnits;
    }

    private DialogBaseUnits computeGlobalDialogBaseUnits() {
        LOGGER.config("Computing global dialog base units...");
        Font dialogFont = getDefaultDialogFont();
        FontMetrics metrics = createDefaultGlobalComponent().getFontMetrics(dialogFont);
        return computeDialogBaseUnits(metrics);
    }

    private Font getCachedDefaultDialogFont() {
        FormUtils.ensureValidCache();
        if (this.cachedDefaultDialogFont == null) {
            this.cachedDefaultDialogFont = lookupDefaultDialogFont();
        }
        return this.cachedDefaultDialogFont;
    }

    private static Font lookupDefaultDialogFont() {
        Font buttonFont = UIManager.getFont("Button.font");
        return buttonFont != null ? buttonFont : new JButton().getFont();
    }

    private static Component createDefaultGlobalComponent() {
        return new JPanel((LayoutManager) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearCache() {
        this.cachedGlobalDialogBaseUnits = null;
        this.cachedFontMetrics = null;
        this.cachedDefaultDialogFont = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/util/DefaultUnitConverter$DialogBaseUnits.class */
    public static final class DialogBaseUnits {
        final double x;
        final double y;

        DialogBaseUnits(double dialogBaseUnitsX, double dialogBaseUnitsY) {
            this.x = dialogBaseUnitsX;
            this.y = dialogBaseUnitsY;
        }

        public String toString() {
            return "DBU(x=" + this.x + "; y=" + this.y + ")";
        }
    }
}
