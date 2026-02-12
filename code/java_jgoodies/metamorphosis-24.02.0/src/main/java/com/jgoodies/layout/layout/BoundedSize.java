package com.jgoodies.layout.layout;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.layout.FormLayout;
import java.awt.Component;
import java.awt.Container;
import java.io.Serializable;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/BoundedSize.class */
public final class BoundedSize implements Size, Serializable {
    private final Size basis;
    private final Size lowerBound;
    private final Size upperBound;

    public BoundedSize(Size basis, Size lowerBound, Size upperBound) {
        this.basis = (Size) Preconditions.checkNotNull(basis, Messages.MUST_NOT_BE_NULL, "basis");
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        Preconditions.checkArgument((lowerBound == null && upperBound == null) ? false : true, "A bounded size must have a non-null lower or upper bound.");
    }

    public Size getBasis() {
        return this.basis;
    }

    public Size getLowerBound() {
        return this.lowerBound;
    }

    public Size getUpperBound() {
        return this.upperBound;
    }

    @Override // com.jgoodies.layout.layout.Size
    public int maximumSize(Container container, List<Component> components, FormLayout.ComponentSizeCache sizeCache, FormLayout.Measure minMeasure, FormLayout.Measure prefMeasure, FormLayout.Measure defaultMeasure, boolean horizontal) {
        int size = this.basis.maximumSize(container, components, sizeCache, minMeasure, prefMeasure, defaultMeasure, horizontal);
        if (this.lowerBound != null) {
            size = Math.max(size, this.lowerBound.maximumSize(container, components, sizeCache, minMeasure, prefMeasure, defaultMeasure, horizontal));
        }
        if (this.upperBound != null) {
            size = Math.min(size, this.upperBound.maximumSize(container, components, sizeCache, minMeasure, prefMeasure, defaultMeasure, horizontal));
        }
        return size;
    }

    @Override // com.jgoodies.layout.layout.Size
    public boolean compressible() {
        return getBasis().compressible();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BoundedSize)) {
            return false;
        }
        BoundedSize size = (BoundedSize) object;
        return this.basis.equals(size.basis) && ((this.lowerBound == null && size.lowerBound == null) || (this.lowerBound != null && this.lowerBound.equals(size.lowerBound))) && ((this.upperBound == null && size.upperBound == null) || (this.upperBound != null && this.upperBound.equals(size.upperBound)));
    }

    public int hashCode() {
        int hashValue = this.basis.hashCode();
        if (this.lowerBound != null) {
            hashValue = (hashValue * 37) + this.lowerBound.hashCode();
        }
        if (this.upperBound != null) {
            hashValue = (hashValue * 37) + this.upperBound.hashCode();
        }
        return hashValue;
    }

    public String toString() {
        return encode();
    }

    @Override // com.jgoodies.layout.layout.Size
    public String encode() {
        StringBuilder builder = new StringBuilder("[");
        if (this.lowerBound != null) {
            builder.append(this.lowerBound.encode()).append(',');
        }
        builder.append(this.basis.encode());
        if (this.upperBound != null) {
            builder.append(',').append(this.upperBound.encode());
        }
        return builder.append(']').toString();
    }
}
