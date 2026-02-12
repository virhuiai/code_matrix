package com.jgoodies.looks.plastic.theme;

import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/theme/DesertBluer.class */
public class DesertBluer extends SkyBluer {
    private final ColorUIResource primary1 = new ColorUIResource(10, 36, 106);
    private final ColorUIResource primary2 = new ColorUIResource(85, 115, 170);
    private final ColorUIResource primary3 = new ColorUIResource(172, 210, 248);
    private final ColorUIResource secondary2 = new ColorUIResource(148, 144, 140);
    private final ColorUIResource secondary3 = new ColorUIResource(212, 208, DelayedPropertyChangeHandler.DEFAULT_DELAY);

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    public String getName() {
        return "Desert Bluer";
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary1() {
        return this.primary1;
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary2() {
        return this.primary2;
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getPrimary3() {
        return this.primary3;
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary1() {
        return Colors.GRAY_MEDIUM;
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary2() {
        return this.secondary2;
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer
    protected ColorUIResource getSecondary3() {
        return this.secondary3;
    }

    public ColorUIResource getTextHighlightColor() {
        return getPrimary1();
    }

    public ColorUIResource getHighlightedTextColor() {
        return getWhite();
    }

    @Override // com.jgoodies.looks.plastic.theme.SkyBluer, com.jgoodies.looks.plastic.PlasticTheme
    public ColorUIResource getMenuItemSelectedBackground() {
        return getPrimary1();
    }
}
