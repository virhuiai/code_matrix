package com.jgoodies.binding.value;

import com.jgoodies.common.bean.ObservableBean2;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ComponentModel.class */
public interface ComponentModel extends ObservableBean2 {
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_VISIBLE = "visible";
    public static final String PROPERTY_EDITABLE = "editable";

    boolean isEnabled();

    void setEnabled(boolean z);

    boolean isVisible();

    void setVisible(boolean z);

    boolean isEditable();

    void setEditable(boolean z);
}
