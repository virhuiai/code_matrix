package com.jgoodies.layout.util;

import com.jgoodies.common.bean.Bean;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Toolkit;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/util/AbstractUnitConverter.class */
public abstract class AbstractUnitConverter extends Bean implements UnitConverter {
    private static final int DTP_RESOLUTION = 72;
    private static int defaultScreenResolution = -1;

    protected abstract double getDialogBaseUnitsX(Component component);

    protected abstract double getDialogBaseUnitsY(Component component);

    @Override // com.jgoodies.layout.util.UnitConverter
    public int inchAsPixel(double in, Component component) {
        return inchAsPixel(in, getScreenResolution(component));
    }

    @Override // com.jgoodies.layout.util.UnitConverter
    public int millimeterAsPixel(double mm, Component component) {
        return millimeterAsPixel(mm, getScreenResolution(component));
    }

    @Override // com.jgoodies.layout.util.UnitConverter
    public int centimeterAsPixel(double cm, Component component) {
        return centimeterAsPixel(cm, getScreenResolution(component));
    }

    @Override // com.jgoodies.layout.util.UnitConverter
    public int pointAsPixel(int pt, Component component) {
        return pointAsPixel(pt, getScreenResolution(component));
    }

    @Override // com.jgoodies.layout.util.UnitConverter
    public int dialogUnitXAsPixel(int dluX, Component c) {
        return dialogUnitXAsPixel(dluX, getDialogBaseUnitsX(c));
    }

    @Override // com.jgoodies.layout.util.UnitConverter
    public int dialogUnitYAsPixel(int dluY, Component c) {
        return dialogUnitYAsPixel(dluY, getDialogBaseUnitsY(c));
    }

    protected static final int inchAsPixel(double in, int dpi) {
        return (int) Math.round(dpi * in);
    }

    protected static final int millimeterAsPixel(double mm, int dpi) {
        return (int) Math.round(((dpi * mm) * 10.0d) / 254.0d);
    }

    protected static final int centimeterAsPixel(double cm, int dpi) {
        return (int) Math.round(((dpi * cm) * 100.0d) / 254.0d);
    }

    protected static final int pointAsPixel(double pt, int dpi) {
        return (int) Math.round((dpi * pt) / 72.0d);
    }

    protected int dialogUnitXAsPixel(int dluX, double dialogBaseUnitsX) {
        return (int) Math.round((dluX * dialogBaseUnitsX) / 4.0d);
    }

    protected int dialogUnitYAsPixel(int dluY, double dialogBaseUnitsY) {
        return (int) Math.round((dluY * dialogBaseUnitsY) / 8.0d);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public double computeAverageCharWidth(FontMetrics metrics, String testString) {
        int width = metrics.stringWidth(testString);
        double average = width / testString.length();
        return average;
    }

    protected int getScreenResolution(Component c) {
        if (c == null) {
            return getDefaultScreenResolution();
        }
        Toolkit toolkit = c.getToolkit();
        if (toolkit != null) {
            return toolkit.getScreenResolution();
        }
        return getDefaultScreenResolution();
    }

    protected int getDefaultScreenResolution() {
        if (defaultScreenResolution == -1) {
            defaultScreenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
        }
        return defaultScreenResolution;
    }
}
