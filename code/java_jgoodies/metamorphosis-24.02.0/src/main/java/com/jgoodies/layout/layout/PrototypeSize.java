package com.jgoodies.layout.layout;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.layout.util.DefaultUnitConverter;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.Serializable;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/PrototypeSize.class */
public final class PrototypeSize implements Size, Serializable {
    private final String prototype;

    public PrototypeSize(String prototype) {
        this.prototype = prototype;
    }

    public String getPrototype() {
        return this.prototype;
    }

    public int computeWidth(Container container) {
        Font font = DefaultUnitConverter.getInstance().getDefaultDialogFont();
        FontMetrics fm = container.getFontMetrics(font);
        return fm.stringWidth(getPrototype());
    }

    @Override // com.jgoodies.layout.layout.Size
    public int maximumSize(Container container, List<Component> components, FormLayout.ComponentSizeCache sizeCache, FormLayout.Measure minMeasure, FormLayout.Measure prefMeasure, FormLayout.Measure defaultMeasure, boolean horizontal) {
        Preconditions.checkArgument(horizontal, "Prototype sizes are allowed only for columns.");
        return computeWidth(container);
    }

    @Override // com.jgoodies.layout.layout.Size
    public boolean compressible() {
        return false;
    }

    @Override // com.jgoodies.layout.layout.Size
    public String encode() {
        return "'" + this.prototype + "'";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PrototypeSize)) {
            return false;
        }
        PrototypeSize size = (PrototypeSize) o;
        return this.prototype.equals(size.prototype);
    }

    public int hashCode() {
        return this.prototype.hashCode();
    }

    public String toString() {
        return encode();
    }
}
