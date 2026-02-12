package com.jgoodies.common.jsdl.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/check/StyleChecks.class */
public class StyleChecks {
    private static StyleChecks current;
    private final List<StyleCheck> allChecks = new ArrayList();

    protected StyleChecks() {
    }

    public static StyleChecks getCurrent() {
        if (current == null) {
            current = new StyleChecks();
        }
        return current;
    }

    public static void setCurrent(StyleChecks newInstance) {
        current = newInstance;
    }

    public static StyleCheck createAndRegister(SeverityLevel defaultSeverity, String name) {
        StyleCheck styleCheck = new StyleCheck(defaultSeverity, name);
        getCurrent().allChecks.add(styleCheck);
        return styleCheck;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<StyleCheck> allChecks() {
        return Collections.unmodifiableList(this.allChecks);
    }
}
