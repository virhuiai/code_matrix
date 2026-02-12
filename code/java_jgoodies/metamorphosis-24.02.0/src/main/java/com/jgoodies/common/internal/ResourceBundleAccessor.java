package com.jgoodies.common.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.resource.StringAndIconResourceAccessor;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/internal/ResourceBundleAccessor.class */
public final class ResourceBundleAccessor implements StringAndIconResourceAccessor {
    private final ResourceBundle bundle;

    public ResourceBundleAccessor(ResourceBundle bundle) {
        this.bundle = (ResourceBundle) Preconditions.checkNotNull(bundle, Messages.MUST_NOT_BE_NULL, "resource bundle");
    }

    @Override // com.jgoodies.common.resource.IconResourceAccessor
    public Icon getIcon(String key) {
        return (Icon) this.bundle.getObject(key);
    }

    @Override // com.jgoodies.common.resource.StringResourceAccessor
    public String getString(String key, Object... args) {
        try {
            return Strings.get(this.bundle.getString(key), args);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
