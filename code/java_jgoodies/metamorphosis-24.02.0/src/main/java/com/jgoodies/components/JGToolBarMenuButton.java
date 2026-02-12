package com.jgoodies.components;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.components.internal.ToolBarComponentUtils;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGToolBarMenuButton.class */
public final class JGToolBarMenuButton extends JGMenuButton {
    private static final int POPUP_AREA_WIDTH = 15;

    public JGToolBarMenuButton(Action action, JPopupMenu menu) {
        super(action, menu);
        configure();
    }

    public JGToolBarMenuButton(Icon icon, JPopupMenu menu) {
        super(icon, menu);
        configure();
    }

    public JGToolBarMenuButton(String text, JPopupMenu menu) {
        super(text, menu);
        configure();
    }

    public JGToolBarMenuButton(String text, Icon icon, JPopupMenu menu) {
        super(text, icon, menu);
        configure();
    }

    public void setBorder(Border border) {
        if (border == null || border.getClass() != EmptyBorder.class) {
            super.setBorder(border);
        } else {
            super.setBorder(new BorderUIResource.CompoundBorderUIResource(border, new BasicBorders.MarginBorder()));
        }
    }

    private void configure() {
        ToolBarComponentUtils.configureButton(this);
    }

    @Override // com.jgoodies.components.JGMenuButton
    protected int getPopupAreaWidth() {
        return POPUP_AREA_WIDTH;
    }

    @Override // com.jgoodies.components.JGMenuButton
    protected int getPopupPlatformOffsetX() {
        return (SystemUtils.IS_OS_MAC && getIcon() == null) ? 3 : 0;
    }
}
