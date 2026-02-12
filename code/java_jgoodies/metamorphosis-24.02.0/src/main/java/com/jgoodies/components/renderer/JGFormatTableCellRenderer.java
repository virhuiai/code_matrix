package com.jgoodies.components.renderer;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.text.Format;
import javax.swing.table.DefaultTableCellRenderer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/renderer/JGFormatTableCellRenderer.class */
public final class JGFormatTableCellRenderer extends DefaultTableCellRenderer {
    private final Format format;

    public JGFormatTableCellRenderer(Format format) {
        this.format = (Format) Preconditions.checkNotNull(format, Messages.MUST_NOT_BE_NULL, "format");
    }

    protected void setValue(Object value) {
        setText(this.format.format(value));
    }
}
