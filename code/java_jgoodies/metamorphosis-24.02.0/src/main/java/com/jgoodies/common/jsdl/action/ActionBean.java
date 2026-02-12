package com.jgoodies.common.jsdl.action;

import com.jgoodies.application.Application;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.internal.IActionObject;
import javax.swing.Action;
import javax.swing.ActionMap;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/ActionBean.class */
public class ActionBean extends Bean implements IActionObject {
    private ActionMap actionMap;

    public final synchronized ActionMap getActionMap() {
        if (this.actionMap == null) {
            this.actionMap = Application.createActionMap(this);
        }
        return this.actionMap;
    }

    @Override // com.jgoodies.common.swing.internal.IActionObject
    public final Action getAction(String actionName) {
        Preconditions.checkNotNull(actionName, Messages.MUST_NOT_BE_NULL, "action name");
        return getActionMap().get(actionName);
    }

    public final boolean isActionEnabled(String actionName) {
        return getAction(actionName).isEnabled();
    }

    public final void setActionsEnabled(boolean enabled, String... actionNames) {
        Preconditions.checkNotNullOrEmpty(actionNames, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "action names");
        for (String actionName : actionNames) {
            getAction(actionName).setEnabled(enabled);
        }
    }
}
