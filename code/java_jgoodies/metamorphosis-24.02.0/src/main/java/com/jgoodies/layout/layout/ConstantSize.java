package com.jgoodies.layout.layout;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.layout.layout.FormLayout;
import java.awt.Component;
import java.awt.Container;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/ConstantSize.class */
public final class ConstantSize implements Size, Serializable {
    private static final Map<String, ConstantSize> CACHE = new HashMap();
    private final double value;
    private final Unit unit;

    public ConstantSize(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public ConstantSize(double value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConstantSize decode(String encodedValueAndUnit) {
        ConstantSize size = CACHE.get(encodedValueAndUnit);
        if (size == null) {
            size = decode0(encodedValueAndUnit);
            CACHE.put(encodedValueAndUnit, size);
        }
        return size;
    }

    private static ConstantSize decode0(String encodedValueAndUnit) {
        String[] split = splitValueAndUnit(encodedValueAndUnit);
        String encodedValue = split[0];
        String encodedUnit = split[1];
        Unit unit = Unit.decode(encodedUnit);
        double value = Double.parseDouble(encodedValue);
        if (unit.requiresIntegers) {
            Preconditions.checkArgument(value == ((double) ((int) value)), "%s value %s must be an integer.", unit, encodedValue);
        }
        return new ConstantSize(value, unit);
    }

    public double getValue() {
        return this.value;
    }

    public Unit getUnit() {
        return this.unit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.jgoodies.layout.layout.ConstantSize$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/ConstantSize$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$layout$layout$Unit = new int[Unit.values().length];

        static {
            try {
                $SwitchMap$com$jgoodies$layout$layout$Unit[Unit.PIXEL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$jgoodies$layout$layout$Unit[Unit.EFFECTIVE_PIXEL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$jgoodies$layout$layout$Unit[Unit.DIALOG_UNITS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$jgoodies$layout$layout$Unit[Unit.POINT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$jgoodies$layout$layout$Unit[Unit.INCH.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$jgoodies$layout$layout$Unit[Unit.MILLIMETER.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$jgoodies$layout$layout$Unit[Unit.CENTIMETER.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public int getPixelSize(Component component, boolean horizontal) {
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$layout$layout$Unit[this.unit.ordinal()]) {
            case 1:
                return intValue();
            case AnimatedLabel.LEFT /* 2 */:
                return ScreenScaling.toPhysical(intValue());
            case 3:
                if (horizontal) {
                    return Sizes.dialogUnitXAsPixel(intValue(), component);
                }
                return Sizes.dialogUnitYAsPixel(intValue(), component);
            case AnimatedLabel.RIGHT /* 4 */:
                return Sizes.pointAsPixel(intValue(), component);
            case 5:
                return Sizes.inchAsPixel(this.value, component);
            case 6:
                return Sizes.millimeterAsPixel(this.value, component);
            case 7:
                return Sizes.centimeterAsPixel(this.value, component);
            default:
                throw new IllegalStateException("Invalid unit " + this.unit);
        }
    }

    @Override // com.jgoodies.layout.layout.Size
    public int maximumSize(Container container, List<Component> components, FormLayout.ComponentSizeCache sizeCache, FormLayout.Measure minMeasure, FormLayout.Measure prefMeasure, FormLayout.Measure defaultMeasure, boolean horizontal) {
        return getPixelSize(container, horizontal);
    }

    @Override // com.jgoodies.layout.layout.Size
    public boolean compressible() {
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConstantSize)) {
            return false;
        }
        ConstantSize size = (ConstantSize) o;
        return this.value == size.value && this.unit == size.unit;
    }

    public int hashCode() {
        return Double.hashCode(this.value) + (37 * this.unit.hashCode());
    }

    public String toString() {
        if (this.value == intValue()) {
            return Integer.toString(intValue()) + this.unit.abbreviation();
        }
        return Double.toString(this.value) + this.unit.abbreviation();
    }

    @Override // com.jgoodies.layout.layout.Size
    public String encode() {
        if (this.value == intValue()) {
            return Integer.toString(intValue()) + this.unit.encode();
        }
        return Double.toString(this.value) + this.unit.encode();
    }

    private int intValue() {
        return (int) Math.round(this.value);
    }

    private static String[] splitValueAndUnit(String encodedValueAndUnit) {
        String[] result = new String[2];
        int len = encodedValueAndUnit.length();
        int firstLetterIndex = len;
        while (firstLetterIndex > 0 && Character.isLetter(encodedValueAndUnit.charAt(firstLetterIndex - 1))) {
            firstLetterIndex--;
        }
        result[0] = encodedValueAndUnit.substring(0, firstLetterIndex);
        result[1] = encodedValueAndUnit.substring(firstLetterIndex);
        return result;
    }
}
