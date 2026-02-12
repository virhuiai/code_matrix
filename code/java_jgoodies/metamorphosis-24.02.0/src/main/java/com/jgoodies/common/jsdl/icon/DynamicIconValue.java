package com.jgoodies.common.jsdl.icon;

import java.awt.Color;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/icon/DynamicIconValue.class */
public interface DynamicIconValue extends IconValue {
    Icon toIcon(int i, Color color);

    default Icon toIcon(int size) {
        return toIcon(size, null);
    }
}
