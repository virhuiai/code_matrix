package com.jgoodies.looks.plastic.theme;

import com.jgoodies.looks.plastic.PlasticTheme;
import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/theme/InvertedColorTheme.class */
public abstract class InvertedColorTheme extends PlasticTheme {
    private final ColorUIResource softWhite = new ColorUIResource(154, 154, 154);
    private final ColorUIResource primary1 = new ColorUIResource(83, 83, 61);
    private final ColorUIResource primary2 = new ColorUIResource(115, 107, 82);
    private final ColorUIResource primary3 = new ColorUIResource(156, 156, 123);
    private final ColorUIResource secondary1 = new ColorUIResource(32, 32, 32);
    private final ColorUIResource secondary2 = new ColorUIResource(96, 96, 96);
    private final ColorUIResource secondary3 = new ColorUIResource(84, 84, 84);

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getSimpleInternalFrameBackground() {
        return getWhite();
    }

    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Object[] uiDefaults = {"TextField.ineditableForeground", getSoftWhite(), "Plastic.brightenStop", new Color(255, 255, 255, 20), "Plastic.ltBrightenStop", new Color(255, 255, 255, 16), "SimpleInternalFrame.activeTitleBackground", getPrimary2()};
        table.putDefaults(uiDefaults);
    }

    public ColorUIResource getControlDisabled() {
        return getSoftWhite();
    }

    public ColorUIResource getControlHighlight() {
        return getSoftWhite();
    }

    public ColorUIResource getControlInfo() {
        return getWhite();
    }

    public ColorUIResource getInactiveSystemTextColor() {
        return getSoftWhite();
    }

    public ColorUIResource getMenuDisabledForeground() {
        return getSoftWhite();
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getMenuItemSelectedBackground() {
        return getPrimary3();
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getMenuItemSelectedForeground() {
        return getBlack();
    }

    public ColorUIResource getMenuSelectedBackground() {
        return getPrimary2();
    }

    public ColorUIResource getMenuSelectedForeground() {
        return getWhite();
    }

    protected ColorUIResource getPrimary1() {
        return this.primary1;
    }

    protected ColorUIResource getPrimary2() {
        return this.primary2;
    }

    protected ColorUIResource getPrimary3() {
        return this.primary3;
    }

    public ColorUIResource getPrimaryControlHighlight() {
        return getSoftWhite();
    }

    protected ColorUIResource getSecondary1() {
        return this.secondary1;
    }

    protected ColorUIResource getSecondary2() {
        return this.secondary2;
    }

    protected ColorUIResource getSecondary3() {
        return this.secondary3;
    }

    public ColorUIResource getSeparatorBackground() {
        return getSoftWhite();
    }

    protected ColorUIResource getSoftWhite() {
        return this.softWhite;
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getTitleTextColor() {
        return getControlInfo();
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getToggleButtonCheckColor() {
        return getWhite();
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getFocusColor() {
        return Colors.GRAY_FOCUS;
    }
}
