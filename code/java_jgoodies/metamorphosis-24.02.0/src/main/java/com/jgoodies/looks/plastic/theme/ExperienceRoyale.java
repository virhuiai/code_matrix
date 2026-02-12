package com.jgoodies.looks.plastic.theme;

import com.jgoodies.looks.plastic.PlasticScrollBarUI;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/theme/ExperienceRoyale.class */
public class ExperienceRoyale extends DesertBluer {
    protected static final ColorUIResource ROYALE_BACKGROUND = new ColorUIResource(235, 233, 237);
    protected static final ColorUIResource ROYALE_BACKGROUND_DARKER = new ColorUIResource(167, 166, 170);
    protected static final ColorUIResource ROYALE_SELECTION = new ColorUIResource(51, 94, 168);
    private static final ColorUIResource SECONDARY1 = Colors.GRAY_MEDIUM;
    private static final ColorUIResource SECONDARY2 = ROYALE_BACKGROUND_DARKER;
    private static final ColorUIResource SECONDARY3 = ROYALE_BACKGROUND;

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    public String getName() {
        return "Experience Royale";
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary1() {
        return ROYALE_SELECTION;
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
        return SECONDARY1;
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
    public ColorUIResource getFocusColor() {
        return Colors.ORANGE_FOCUS;
    }

    public ColorUIResource getPrimaryControlShadow() {
        return getPrimary3();
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    public ColorUIResource getMenuSelectedBackground() {
        return getPrimary1();
    }

    public ColorUIResource getMenuSelectedForeground() {
        return WHITE;
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getMenuItemBackground() {
        return WHITE;
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getToggleButtonCheckColor() {
        return Colors.GREEN_CHECK;
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Object[] uiDefaults = {"ScrollBar.thumbHighlight", getPrimaryControlHighlight(), PlasticScrollBarUI.MAX_BUMPS_WIDTH_KEY, 22};
        table.putDefaults(uiDefaults);
    }
}
