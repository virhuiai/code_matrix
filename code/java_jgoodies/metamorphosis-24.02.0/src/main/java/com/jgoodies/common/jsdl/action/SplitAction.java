package com.jgoodies.common.jsdl.action;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import javax.swing.Action;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/SplitAction.class */
public final class SplitAction {
    private final Action action;
    private final ActionGroup group;

    public SplitAction(Action action, ActionGroup group) {
        this.action = (Action) Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action");
        this.group = (ActionGroup) Preconditions.checkNotNull(group, Messages.MUST_NOT_BE_NULL, "group");
    }

    public Action getAction() {
        return this.action;
    }

    public ActionGroup getGroup() {
        return this.group;
    }
}
