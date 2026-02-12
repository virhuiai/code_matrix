package com.jgoodies.components.internal;

import com.jgoodies.application.Actions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import javax.swing.AbstractButton;
import javax.swing.Action;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ActionConfigurationSupport.class */
public final class ActionConfigurationSupport {
    private ActionConfigurationSupport() {
    }

    public static void configureAccessiblePropertiesFromAction(AbstractButton button, Action action) {
        Preconditions.checkNotNull(button, Messages.MUST_NOT_BE_NULL, "button");
        String accessibleName = action == null ? null : (String) action.getValue(Actions.ACCESSIBLE_NAME_KEY);
        String accessibleDescription = action == null ? null : (String) action.getValue(Actions.ACCESSIBLE_DESCRIPTION_KEY);
        button.getAccessibleContext().setAccessibleName(accessibleName);
        button.getAccessibleContext().setAccessibleDescription(accessibleDescription);
    }
}
