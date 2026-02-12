package com.jgoodies.layout.layout;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.util.DefaultUnitConverter;
import com.jgoodies.layout.util.UnitConverter;
import java.awt.Component;
import java.util.Locale;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/Sizes.class */
public final class Sizes {
    private static UnitConverter unitConverter;
    public static final ConstantSize ZERO = pixel(0);
    public static final ConstantSize DLU1 = constant("1dlu");
    public static final ConstantSize DLU2 = constant("2dlu");
    public static final ConstantSize DLU3 = constant("3dlu");
    public static final ConstantSize DLU4 = constant("4dlu");
    public static final ConstantSize DLU5 = constant("5dlu");
    public static final ConstantSize DLU6 = constant("6dlu");
    public static final ConstantSize DLU7 = constant("7dlu");
    public static final ConstantSize DLU8 = constant("8dlu");
    public static final ConstantSize DLU9 = constant("9dlu");
    public static final ConstantSize DLU11 = constant("11dlu");
    public static final ConstantSize DLU14 = constant("14dlu");
    public static final ConstantSize DLU21 = constant("21dlu");
    public static final ConstantSize EPX4 = constant("4epx");
    public static final ConstantSize EPX8 = constant("8epx");
    public static final ConstantSize EPX12 = constant("12epx");
    public static final ConstantSize EPX16 = constant("16epx");
    public static final ConstantSize EPX24 = constant("24epx");
    public static final ConstantSize EPX48 = constant("48epx");
    private static Unit defaultUnit = Unit.PIXEL;

    private Sizes() {
    }

    public static ConstantSize constant(String encodedValueAndUnit) {
        String lowerCase = encodedValueAndUnit.toLowerCase(Locale.ENGLISH);
        String trimmed = lowerCase.trim();
        return ConstantSize.decode(trimmed);
    }

    public static ConstantSize dlu(int value) {
        return new ConstantSize(value, Unit.DIALOG_UNITS);
    }

    public static ConstantSize pixel(int value) {
        return new ConstantSize(value, Unit.PIXEL);
    }

    public static ConstantSize effectivePixel(int value) {
        return epx(value);
    }

    public static ConstantSize epx(int value) {
        return new ConstantSize(value, Unit.EFFECTIVE_PIXEL);
    }

    public static Size bounded(Size basis, Size lowerBound, Size upperBound) {
        return new BoundedSize(basis, lowerBound, upperBound);
    }

    public static int inchAsPixel(double in, Component component) {
        if (in == FormSpec.NO_GROW) {
            return 0;
        }
        return getUnitConverter().inchAsPixel(in, component);
    }

    public static int millimeterAsPixel(double mm, Component component) {
        if (mm == FormSpec.NO_GROW) {
            return 0;
        }
        return getUnitConverter().millimeterAsPixel(mm, component);
    }

    public static int centimeterAsPixel(double cm, Component component) {
        if (cm == FormSpec.NO_GROW) {
            return 0;
        }
        return getUnitConverter().centimeterAsPixel(cm, component);
    }

    public static int pointAsPixel(int pt, Component component) {
        if (pt == 0) {
            return 0;
        }
        return getUnitConverter().pointAsPixel(pt, component);
    }

    public static int dialogUnitXAsPixel(int dluX, Component component) {
        if (dluX == 0) {
            return 0;
        }
        return getUnitConverter().dialogUnitXAsPixel(dluX, component);
    }

    public static int dialogUnitYAsPixel(int dluY, Component component) {
        if (dluY == 0) {
            return 0;
        }
        return getUnitConverter().dialogUnitYAsPixel(dluY, component);
    }

    public static UnitConverter getUnitConverter() {
        if (unitConverter == null) {
            unitConverter = DefaultUnitConverter.getInstance();
        }
        return unitConverter;
    }

    public static void setUnitConverter(UnitConverter newUnitConverter) {
        unitConverter = newUnitConverter;
    }

    public static Unit getDefaultUnit() {
        return defaultUnit;
    }

    public static void setDefaultUnit(Unit unit) {
        defaultUnit = (Unit) Preconditions.checkNotNull(unit, Messages.MUST_NOT_BE_NULL, "default unit");
    }
}
