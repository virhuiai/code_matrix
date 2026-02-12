package com.jgoodies.looks.plastic.theme;

import com.jgoodies.looks.plastic.PlasticScrollBarUI;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/theme/LightGray.class */
public class LightGray extends ExperienceBlue {
    private static final ColorUIResource GRAY_VERY_LIGHT = new ColorUIResource(244, 244, 244);

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    public String getName() {
        return "Light Gray";
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary1() {
        return new ColorUIResource(51, 153, 255);
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary2() {
        return Colors.GRAY_MEDIUMLIGHT;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary3() {
        return new ColorUIResource(225, 240, 255);
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary1() {
        return Colors.GRAY_MEDIUM;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary2() {
        return getPrimary2();
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary3() {
        return GRAY_VERY_LIGHT;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getFocusColor() {
        return getBlack();
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getTitleTextColor() {
        return Colors.GRAY_DARKEST;
    }

    @Override // com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getSimpleInternalFrameBackground() {
        return Colors.GRAY_MEDIUMDARK;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.SkyBluer
    public void addCustomEntriesToTable(UIDefaults table) {
        super.addCustomEntriesToTable(table);
        Object[] uiDefaults = {PlasticScrollBarUI.MAX_BUMPS_WIDTH_KEY, 0, "ScrollBar.shadow", getPrimary3(), "ScrollBar.thumb", getSecondary1(), "ScrollBar.thumbShadow", getSecondary1(), "ScrollBar.thumbHighlight", getSecondary1(), "TabbedPane.selected", getWhite(), "TabbedPane.selectHighlight", Colors.GRAY_MEDIUM};
        table.putDefaults(uiDefaults);
    }
}
