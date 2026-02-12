package com.jgoodies.layout.layout;

import com.jgoodies.layout.layout.FormLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/Size.class */
public interface Size {
    int maximumSize(Container container, List<Component> list, FormLayout.ComponentSizeCache componentSizeCache, FormLayout.Measure measure, FormLayout.Measure measure2, FormLayout.Measure measure3, boolean z);

    boolean compressible();

    String encode();
}
