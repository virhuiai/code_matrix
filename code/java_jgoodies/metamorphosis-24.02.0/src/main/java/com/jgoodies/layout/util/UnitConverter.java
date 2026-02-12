package com.jgoodies.layout.util;

import java.awt.Component;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/util/UnitConverter.class */
public interface UnitConverter {
    int inchAsPixel(double d, Component component);

    int millimeterAsPixel(double d, Component component);

    int centimeterAsPixel(double d, Component component);

    int pointAsPixel(int i, Component component);

    int dialogUnitXAsPixel(int i, Component component);

    int dialogUnitYAsPixel(int i, Component component);
}
