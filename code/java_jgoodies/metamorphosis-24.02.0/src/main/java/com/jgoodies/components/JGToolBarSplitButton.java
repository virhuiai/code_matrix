package com.jgoodies.components;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.components.internal.ToolBarComponentUtils;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGToolBarSplitButton.class */
public final class JGToolBarSplitButton extends JGSplitButton {
    public static final String PROPERTY_SEPARATOR_PAINTED_ALWAYS = "separatorPaintedAlways";
    private boolean separatorPaintedAlways;

    public JGToolBarSplitButton(Action action, JPopupMenu menu) {
        super(action, menu);
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
        setSeparatorPaintedAlways(SystemUtils.IS_OS_MAC);
    }

    @Override // com.jgoodies.components.JGSplitButton
    protected int getPopupPlatformOffsetX() {
        return (SystemUtils.IS_OS_MAC && getIcon() == null) ? 5 : 0;
    }

    @Override // com.jgoodies.components.JGSplitButton
    public boolean isSeparatorPaintedAlways() {
        return this.separatorPaintedAlways;
    }

    public void setSeparatorPaintedAlways(boolean newValue) {
        boolean oldValue = isSeparatorPaintedAlways();
        this.separatorPaintedAlways = newValue;
        firePropertyChange(PROPERTY_SEPARATOR_PAINTED_ALWAYS, oldValue, newValue);
    }
}
