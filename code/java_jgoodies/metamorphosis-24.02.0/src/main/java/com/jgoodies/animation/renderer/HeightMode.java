package com.jgoodies.animation.renderer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/renderer/HeightMode.class */
public final class HeightMode {
    private final String name;
    public static final HeightMode CAPITAL_ASCENT = new HeightMode("Capital ascent");
    public static final HeightMode TEXT_ASCENT = new HeightMode("Text ascent");
    public static final HeightMode TEXT_HEIGHT = new HeightMode("Text height");

    private HeightMode(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
