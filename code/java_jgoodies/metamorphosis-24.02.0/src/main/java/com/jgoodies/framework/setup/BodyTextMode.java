package com.jgoodies.framework.setup;

import com.jgoodies.common.display.Displayable;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/setup/BodyTextMode.class */
public enum BodyTextMode implements Displayable {
    CLASSIC("Classic"),
    LARGER("Larger");

    private final String displayString;

    BodyTextMode(String displayString) {
        this.displayString = displayString;
    }

    @Override // com.jgoodies.common.display.Displayable
    public String getDisplayString() {
        return this.displayString;
    }
}
