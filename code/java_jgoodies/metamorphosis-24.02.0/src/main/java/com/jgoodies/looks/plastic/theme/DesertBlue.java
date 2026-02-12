package com.jgoodies.looks.plastic.theme;

import com.jgoodies.looks.plastic.PlasticScrollBarUI;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/theme/DesertBlue.class */
public class DesertBlue extends DesertBluer {
    private static final ColorUIResource SECONDARY2 = new ColorUIResource(148, 144, 140);
    private static final ColorUIResource SECONDARY3 = new ColorUIResource(211, 210, 204);

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    public String getName() {
        return "Desert Blue";
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary1() {
        return Colors.GRAY_DARK;
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary2() {
        return Colors.BLUE_LOW_MEDIUM;
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary3() {
        return Colors.BLUE_LOW_LIGHTEST;
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary1() {
        return Colors.GRAY_MEDIUM;
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary2() {
        return SECONDARY2;
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary3() {
        return SECONDARY3;
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getTitleTextColor() {
        return Colors.BLUE_MEDIUM_DARKEST;
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getFocusColor() {
        return Colors.BLUE_MEDIUM_DARK;
    }

    public ColorUIResource getPrimaryControlShadow() {
        return getPrimary3();
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer, com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getMenuItemSelectedBackground() {
        return getPrimary1();
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Object[] uiDefaults = {"ScrollBar.is3DEnabled", Boolean.FALSE, "ScrollBar.thumbHighlight", getPrimaryControlHighlight(), PlasticScrollBarUI.MAX_BUMPS_WIDTH_KEY, null};
        table.putDefaults(uiDefaults);
    }
}
