package com.jgoodies.layout.layout;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.framework.completion.Suggest;
import com.jgoodies.layout.layout.FormLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Locale;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/ComponentSize.class */
public enum ComponentSize implements Size {
    MINIMUM(false),
    PREFERRED(false),
    DEFAULT(true);

    private final boolean compressible;

    ComponentSize(boolean compressible) {
        this.compressible = compressible;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ComponentSize decode(String str) {
        boolean z = -1;
        switch (str.hashCode()) {
            case Suggest.DEFAULT_CAPACITY /* 100 */:
                if (str.equals("d")) {
                    z = 3;
                    break;
                }
                break;
            case 109:
                if (str.equals("m")) {
                    z = 5;
                    break;
                }
                break;
            case 112:
                if (str.equals("p")) {
                    z = true;
                    break;
                }
                break;
            case 108114:
                if (str.equals("min")) {
                    z = 4;
                    break;
                }
                break;
            case 3449379:
                if (str.equals("pref")) {
                    z = false;
                    break;
                }
                break;
            case 1544803905:
                if (str.equals("default")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case AnimatedLabel.CENTER /* 0 */:
            case true:
                return PREFERRED;
            case AnimatedLabel.LEFT /* 2 */:
            case true:
                return DEFAULT;
            case AnimatedLabel.RIGHT /* 4 */:
            case true:
                return MINIMUM;
            default:
                return null;
        }
    }

    @Override // com.jgoodies.layout.layout.Size
    public int maximumSize(Container container, List<Component> components, FormLayout.ComponentSizeCache sizeCache, FormLayout.Measure minMeasure, FormLayout.Measure prefMeasure, FormLayout.Measure defaultMeasure, boolean horizontal) {
        FormLayout.Measure measure = this == MINIMUM ? minMeasure : this == PREFERRED ? prefMeasure : defaultMeasure;
        int maximum = 0;
        for (Component c : components) {
            maximum = Math.max(maximum, measure.sizeOf(c, sizeCache));
        }
        return maximum;
    }

    @Override // com.jgoodies.layout.layout.Size
    public boolean compressible() {
        return this.compressible;
    }

    @Override // java.lang.Enum
    public String toString() {
        return encode();
    }

    @Override // com.jgoodies.layout.layout.Size
    public String encode() {
        return name().substring(0, 1).toLowerCase(Locale.ENGLISH);
    }
}
