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
        boolean z = -1;
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
                    z = false;
                    break;
                }
                break;
            case 100653:
                if (name.equals("epx")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case AnimatedLabel.CENTER /* 0 */:
                return DIALOG_UNITS;
            case true:
                return EFFECTIVE_PIXEL;
            case AnimatedLabel.LEFT /* 2 */:
                return PIXEL;
            case true:
                return POINT;
            case AnimatedLabel.RIGHT /* 4 */:
                return INCH;
            case true:
                return MILLIMETER;
            case true:
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
