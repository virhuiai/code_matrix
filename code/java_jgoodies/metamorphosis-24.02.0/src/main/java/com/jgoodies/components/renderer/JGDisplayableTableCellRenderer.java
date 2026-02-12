package com.jgoodies.components.renderer;

import com.jgoodies.common.display.Displayable;
import com.jgoodies.common.display.TableDisplayable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/renderer/JGDisplayableTableCellRenderer.class */
public final class JGDisplayableTableCellRenderer extends JGDefaultTableCellRenderer<Object> {
    public static final JGDisplayableTableCellRenderer INSTANCE = new JGDisplayableTableCellRenderer();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.renderer.JGDefaultTableCellRenderer
    public void setValue(Object value) {
        if (value instanceof TableDisplayable) {
            super.setValue(((TableDisplayable) value).getTableDisplayString());
        } else {
            if (value instanceof Displayable) {
                super.setValue(((Displayable) value).getDisplayString());
                return;
            }
            throw new ClassCastException("The value must implement Displayable and/or TableDisplayable.");
        }
    }
}
