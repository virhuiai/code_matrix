package com.jgoodies.framework.action;

import com.jgoodies.common.jsdl.action.ActionGroup;
import com.jgoodies.components.JGComponentFactory;
import javax.swing.Action;
import javax.swing.JMenuBar;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/action/DefaultMenuBarBuilder.class */
public final class DefaultMenuBarBuilder implements ActionGroup.ActionGroupBuilder {
    private final JMenuBar menuBar;
    private final JGComponentFactory factory;

    public DefaultMenuBarBuilder() {
        this(null, null);
    }

    public DefaultMenuBarBuilder(JMenuBar menuBar) {
        this(menuBar, null);
    }

    public DefaultMenuBarBuilder(JMenuBar menuBar, JGComponentFactory factory) {
        this.menuBar = menuBar != null ? menuBar : new JMenuBar();
        this.factory = factory != null ? factory : JGComponentFactory.getCurrent();
    }

    public static JMenuBar menuBarFor(ActionGroup group) {
        DefaultMenuBarBuilder builder = new DefaultMenuBarBuilder();
        group.build(builder);
        return builder.build();
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void add(Action action) {
        throw new UnsupportedOperationException("The DefaultMenuBarBuilder does not support actions in the menu bar.");
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void add(ActionGroup group) {
        DefaultMenuBuilder menuBuilder = new DefaultMenuBuilder(this.factory);
        group.build(menuBuilder);
        this.menuBar.add(menuBuilder.build());
    }

    public JMenuBar build() {
        return this.menuBar;
    }
}
