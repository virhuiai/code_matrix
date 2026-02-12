package com.jgoodies.framework.search;

import com.jgoodies.binding.binder.BinderUtils;
import com.jgoodies.binding.binder.ValueModelBindable;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.components.JGTextField;
import com.jgoodies.framework.search.FieldSearchModel;
import com.jgoodies.search.CompletionManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchBindable.class */
public final class FieldSearchBindable implements ValueModelBindable {
    private final JGTextField textField;
    private final CompletionManager manager;
    private final FieldSearchModel.InvalidTextBehavior behavior;

    public FieldSearchBindable(JGTextField textField, CompletionManager manager) {
        this(textField, manager, FieldSearchModel.InvalidTextBehavior.PERSIST);
    }

    public FieldSearchBindable(JGTextField textField, CompletionManager manager, FieldSearchModel.InvalidTextBehavior behavior) {
        this.textField = (JGTextField) Preconditions.checkNotNull(textField, Messages.MUST_NOT_BE_NULL, "text field");
        this.manager = (CompletionManager) Preconditions.checkNotNull(manager, Messages.MUST_NOT_BE_NULL, "completion manager");
        this.behavior = behavior;
    }

    @Override // com.jgoodies.binding.binder.ValueModelBindable
    public void bind(ValueModel model, String propertyName) {
        FieldSearchBindings.bind(this.textField, model, this.manager, this.behavior);
        if (propertyName != null) {
            BinderUtils.setValidationMessageKey(this.textField, propertyName);
        }
    }
}
