package com.jgoodies.looks.plastic.theme;

import com.jgoodies.looks.plastic.PlasticScrollBarUI;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/theme/ExperienceBlue.class */
public class ExperienceBlue extends DesertBluer {
    protected static final ColorUIResource LUNA_BACKGROUND = new ColorUIResource(236, 233, 216);
    protected static final ColorUIResource LUNA_BACKGROUND_DARKER = new ColorUIResource(189, 190, 176);
    private static final ColorUIResource SECONDARY1 = Colors.GRAY_MEDIUM;
    private static final ColorUIResource SECONDARY2 = LUNA_BACKGROUND_DARKER;
    private static final ColorUIResource SECONDARY3 = LUNA_BACKGROUND;

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    public String getName() {
        return "Experience Blue";
    }

    @Override // com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary1() {
        return Colors.BLUE_MEDIUM_DARK;
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
