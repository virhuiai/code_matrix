package com.jgoodies.layout.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.swing.focus.JGContainerOrderFocusTraversalPolicy;
import com.jgoodies.common.swing.focus.JGLayoutFocusTraversalPolicy;
import com.jgoodies.layout.util.FocusTraversalType;
import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/internal/InternalFocusSetupUtils.class */
public final class InternalFocusSetupUtils {
    private InternalFocusSetupUtils() {
    }

    public static void checkValidFocusTraversalSetup(FocusTraversalPolicy policy, FocusTraversalType type, Component initialComponent) {
        Preconditions.checkState((policy != null && type == null && initialComponent == null) || policy == null, "Either use #focusTraversalPolicy or #focusTraversalType plus optional #initialComponent); don't mix them.");
    }

    public static void setupFocusTraversalPolicyAndProvider(JComponent container, FocusTraversalPolicy policy, FocusTraversalType type, Component initialComponent) {
        container.setFocusTraversalPolicy(getOrCreateFocusTraversalPolicy(policy, type, initialComponent));
        container.setFocusTraversalPolicyProvider(true);
    }

    public static FocusTraversalPolicy getOrCreateFocusTraversalPolicy(FocusTraversalPolicy policy, FocusTraversalType type, Component initialComponent) {
        if (policy != null) {
            return policy;
        }
        if (type == FocusTraversalType.CONTAINER_ORDER) {
            return createContainerOrderFocusTraversalPolicy(initialComponent);
        }
        return createLayoutFocusTraversalPolicy(initialComponent);
    }

    private static FocusTraversalPolicy createContainerOrderFocusTraversalPolicy(Component initialComponent) {
        return new JGContainerOrderFocusTraversalPolicy(initialComponent);
    }

    private static FocusTraversalPolicy createLayoutFocusTraversalPolicy(Component initialComponent) {
        return new JGLayoutFocusTraversalPolicy(initialComponent);
    }
}
