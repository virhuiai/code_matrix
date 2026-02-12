package com.jgoodies.components.internal;

import com.jgoodies.common.jsdl.check.SeverityLevel;
import com.jgoodies.common.jsdl.check.StyleCheck;
import com.jgoodies.common.jsdl.check.StyleChecks;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/JGComponentStyleChecks.class */
public final class JGComponentStyleChecks {
    public static final StyleCheck RADIO_BUTTON_TEXT_NOT_BLANK = StyleChecks.createAndRegister(SeverityLevel.ERROR, "The radio button text must not be null, empty, or whitespace; see Strings#isBlank(String).");

    private JGComponentStyleChecks() {
    }
}
