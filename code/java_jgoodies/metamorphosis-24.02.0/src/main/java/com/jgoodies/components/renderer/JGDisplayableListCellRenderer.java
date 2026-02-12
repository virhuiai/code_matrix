package com.jgoodies.components.renderer;

import com.jgoodies.common.display.Displayable;
import com.jgoodies.common.display.ListDisplayable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/renderer/JGDisplayableListCellRenderer.class */
public final class JGDisplayableListCellRenderer extends JGDefaultListCellRenderer<Object> {
    public static final JGDisplayableListCellRenderer INSTANCE = new JGDisplayableListCellRenderer();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.renderer.JGDefaultListCellRenderer
    public void setValue(Object value) {
        if (value instanceof ListDisplayable) {
            super.setValue(((ListDisplayable) value).getListDisplayString());
        } else {
            if (value instanceof Displayable) {
                super.setValue(((Displayable) value).getDisplayString());
                return;
            }
            throw new ClassCastException("The value must implement Displayable and/or ListDisplayable.");
        }
    }
}
