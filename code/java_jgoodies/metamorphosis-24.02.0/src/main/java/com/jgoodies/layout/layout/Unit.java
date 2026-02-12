package com.jgoodies.layout.layout;

import com.jgoodies.animation.swing.components.AnimatedLabel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/Unit.class */
public enum Unit {
    PIXEL("px", true),
    EFFECTIVE_PIXEL("epx", true),
    POINT("pt", true),
    DIALOG_UNITS("dlu", true),
    MILLIMETER("mm", false),
    CENTIMETER("cm", false),
    INCH("in", false);

    private final String abbreviation;
    final boolean requiresIntegers;

    Unit(String abbreviation, boolean requiresIntegers) {
        this.abbreviation = abbreviation;
        this.requiresIntegers = requiresIntegers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Unit decode(String name) {
        if (name.isEmpty()) {
            return Sizes.getDefaultUnit();
        }
        int z = -1;
        switch (name.hashCode()) {
            case 3178:
                if (name.equals("cm")) {
                    z = 6;
                    break;
                }
                break;
            case 3365:
                if (name.equals("in")) {
                    z = 4;
                    break;
                }
                break;
            case 3488:
                if (name.equals("mm")) {
                    z = 5;
                    break;
                }
                break;
            case 3588:
                if (name.equals("pt")) {
                    z = 3;
                    break;
                }
                break;
            case 3592:
                if (name.equals("px")) {
                    z = 2;
                    break;
                }
                break;
            case 99565:
                if (name.equals("dlu")) {
                    z = 0;
                    break;
                }
                break;
            case 100653:
                if (name.equals("epx")) {
                    z = 1;
                    break;
                }
                break;
        }
        switch (z) {
            case 0:
                return DIALOG_UNITS;
            case 1:
                return EFFECTIVE_PIXEL;
            case 2:
                return PIXEL;
            case 3:
                return POINT;
            case 4:
                return INCH;
            case 5:
                return MILLIMETER;
            case 6:
                return CENTIMETER;
            default:
                throw new IllegalArgumentException("Invalid unit name '" + name + "'. Must be one of: px, epx, dlu, pt, mm, cm, in");
        }
    }

    boolean requiresIntegers() {
        return this.requiresIntegers;
    }

    public String encode() {
        return this.abbreviation;
    }

    public String abbreviation() {
        return this.abbreviation;
    }
}
