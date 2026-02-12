package com.jgoodies.looks.plastic.theme;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.looks.plastic.PlasticScrollBarUI;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/theme/LightBlue.class */
public class LightBlue extends ExperienceBlue {
    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    public String getName() {
        return "Light Blue";
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary1() {
        return new ColorUIResource(51, 153, 255);
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary2() {
        return Colors.GRAY_217;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary3() {
        return Colors.BLUE_FOCUS;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary1() {
        return Colors.GRAY_MEDIUM;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary2() {
        return Colors.GRAY_217;
    }

    @Override // com.jgoodies.looks.plastic.theme.ExperienceBlue, com.jgoodies.looks.plastic.theme.DesertBluer, com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary3() {
        return Colors.GRAY_243;
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
        Object[] uiDefaults = {PlasticScrollBarUI.MAX_BUMPS_WIDTH_KEY, 0, "Button.select", Colors.BLUE_METRO, "Button.disabledText", Colors.GRAY_MEDIUMLIGHT, "Button.background", Colors.GRAY_225, "ToggleButton.select", Colors.BLUE_METRO, "ToggleButton.disabledText", Colors.GRAY_MEDIUMLIGHT, "ToggleButton.disabledSelectedText", getBlack(), "ToggleButton.background", Colors.GRAY_225, "MenuBar.background", SystemUtils.IS_OS_WINDOWS_11 ? Colors.GRAY_243 : getWhite(), "ScrollBar.shadow", getPrimary3(), "ScrollBar.thumb", getSecondary2(), "ScrollBar.thumbShadow", getSecondary1(), "ScrollBar.thumbHighlight", getSecondary3(), "TabbedPane.selected", getWhite(), "TabbedPane.selectHighlight", Colors.GRAY_MEDIUM};
        table.putDefaults(uiDefaults);
    }
}
