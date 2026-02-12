package com.jgoodies.binding.binder;

import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/BinderUtils.class */
public final class BinderUtils {
    private BinderUtils() {
    }

    public static void setValidationMessageKey(JComponent comp, Object messageKey) {
        Object[] keyArray = messageKey == null ? null : new Object[]{messageKey};
        comp.putClientProperty("validation.messageKeys", keyArray);
    }
}
