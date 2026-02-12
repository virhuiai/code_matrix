package com.jgoodies.common.jsdl.check;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/check/StyleCheck.class */
public class StyleCheck {
    private final SeverityLevel defaultSeverity;
    private final String description;

    /* JADX INFO: Access modifiers changed from: protected */
    public StyleCheck(SeverityLevel defaultSeverity, String description) {
        this.defaultSeverity = (SeverityLevel) Preconditions.checkNotNull(defaultSeverity, Messages.MUST_NOT_BE_NULL, "default severity");
        this.description = (String) Preconditions.checkNotNull(description, Messages.MUST_NOT_BE_NULL, "name");
    }

    public SeverityLevel getDefaultSeverityLevel() {
        return this.defaultSeverity;
    }

    public String getDescription() {
        return this.description;
    }

    public final String toString() {
        return super.toString() + " description=" + this.description;
    }
}
