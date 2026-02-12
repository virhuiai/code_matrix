package com.jgoodies.common.jsdl.check;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.HashMap;
import java.util.Map;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/check/StyleCheckConfiguration.class */
public final class StyleCheckConfiguration {
    private final StyleCheckConfiguration parent;
    private final Map<StyleCheck, SeverityLevel> map = new HashMap();

    public StyleCheckConfiguration(StyleCheckConfiguration parent) {
        this.parent = parent;
    }

    public SeverityLevel get(StyleCheck check) {
        Preconditions.checkNotNull(check, Messages.MUST_NOT_BE_NULL, "style check");
        SeverityLevel level = this.map.get(check);
        if (level != null) {
            return level;
        }
        return this.parent != null ? this.parent.get(check) : SeverityLevel.IGNORE;
    }

    public SeverityLevel put(StyleCheck check, SeverityLevel level) {
        Preconditions.checkNotNull(check, Messages.MUST_NOT_BE_NULL, "style check");
        Preconditions.checkNotNull(level, Messages.MUST_NOT_BE_NULL, "severity level");
        return this.map.put(check, level);
    }

    public void restore() {
        StyleChecker.restore(this);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString()).append("\nSeverity levels:");
        this.map.forEach((check, level) -> {
            builder.append("\n    ").append(check).append(" -> ").append(level);
        });
        return builder.toString();
    }
}
