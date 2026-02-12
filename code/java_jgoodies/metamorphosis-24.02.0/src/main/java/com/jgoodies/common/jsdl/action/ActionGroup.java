package com.jgoodies.common.jsdl.action;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/ActionGroup.class */
public final class ActionGroup {
    private static final Object SEPARATOR = new StringBuilder("Separator");
    private final Action labelAction;
    private final List<Object> items;

    public ActionGroup() {
        this((Action) null);
    }

    public ActionGroup(String markedText) {
        this(ConsumerAction.noOp((String) Preconditions.checkNotBlank(markedText, Messages.MUST_NOT_BE_BLANK, "marked text")));
    }

    public ActionGroup(Action labelAction) {
        this.labelAction = labelAction;
        this.items = new ArrayList();
    }

    public ActionGroup add(Action action) {
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action");
        this.items.add(action);
        return this;
    }

    public ActionGroup add(ActionMap actionMap, String... actionNames) {
        Preconditions.checkNotNull(actionMap, Messages.MUST_NOT_BE_NULL, "action map");
        Preconditions.checkNotNull(actionNames, Messages.MUST_NOT_BE_NULL, "action names");
        for (String name : actionNames) {
            if (name == null || name.startsWith("---")) {
                addSeparator();
            } else {
                Action action = actionMap.get(name);
                Preconditions.checkArgument(action != null, "No action found for name %s.", name);
                add(action);
            }
        }
        return this;
    }

    public ActionGroup add(ActionGroup... groups) {
        Preconditions.checkNotNull(groups, Messages.MUST_NOT_BE_NULL, "group array");
        Collections.addAll(this.items, groups);
        return this;
    }

    public ActionGroup add(SplitAction splitAction) {
        Preconditions.checkNotNull(splitAction, Messages.MUST_NOT_BE_NULL, "split action");
        this.items.add(splitAction);
        return this;
    }

    public ActionGroup addSeparator() {
        if (!lastAddedWasSeparator()) {
            this.items.add(SEPARATOR);
        }
        return this;
    }

    public boolean contains(Action action) {
        return this.items.contains(action);
    }

    public Action getLabelAction() {
        return this.labelAction;
    }

    public void build(ActionGroupBuilder builder) {
        builder.setLabelAction(getLabelAction());
        for (Object item : this.items) {
            if (item instanceof Action) {
                builder.add((Action) item);
            } else if (item instanceof ActionGroup) {
                builder.add((ActionGroup) item);
            } else if (item instanceof SplitAction) {
                builder.add((SplitAction) item);
            } else if (item == SEPARATOR) {
                builder.addSeparator();
            } else {
                throw new IllegalStateException("Unknown item type " + item);
            }
        }
    }

    private boolean lastAddedWasSeparator() {
        return !this.items.isEmpty() && this.items.get(this.items.size() - 1).equals(SEPARATOR);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/ActionGroup$ActionGroupBuilder.class */
    public interface ActionGroupBuilder {
        void add(Action action);

        default void setLabelAction(Action labelAction) {
        }

        default void add(ActionGroup group) {
            throw new UnsupportedOperationException("Adding a sub-group is not supported by this builder.");
        }

        default void add(SplitAction splitAction) {
            throw new UnsupportedOperationException("Adding a split action is not supported by this builder.");
        }

        default void addSeparator() {
            throw new UnsupportedOperationException("Adding a separator is not supported by this builder.");
        }
    }
}
