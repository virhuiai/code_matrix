package com.jgoodies.framework.action;

import com.jgoodies.common.jsdl.action.ActionGroup;
import com.jgoodies.components.JGComponentFactory;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/action/DefaultMenuBuilder.class */
public final class DefaultMenuBuilder implements ActionGroup.ActionGroupBuilder {
    private final JMenu menu;
    private final JGComponentFactory factory;

    public DefaultMenuBuilder() {
        this(null, null);
    }

    public DefaultMenuBuilder(JMenu menu) {
        this(menu, null);
    }

    public DefaultMenuBuilder(JGComponentFactory factory) {
        this(null, factory);
    }

    public DefaultMenuBuilder(JMenu menu, JGComponentFactory factory) {
        this.menu = menu != null ? menu : new JMenu();
        this.factory = factory != null ? factory : JGComponentFactory.getCurrent();
    }

    public static JMenu menuFor(ActionGroup group) {
        DefaultMenuBuilder builder = new DefaultMenuBuilder();
        group.build(builder);
        return builder.build();
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void setLabelAction(Action labelAction) {
        this.menu.setAction(labelAction);
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void add(Action action) {
        this.menu.add(this.factory.createMenuItem(action));
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void add(ActionGroup group) {
        DefaultMenuBuilder submenuBuilder = new DefaultMenuBuilder();
        group.build(submenuBuilder);
        this.menu.add(submenuBuilder.build());
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void addSeparator() {
        this.menu.addSeparator();
    }

    public JMenu build() {
        if (this.menu.getMenuComponentCount() > 0 && (this.menu.getMenuComponent(0) instanceof JPopupMenu.Separator)) {
            this.menu.remove(0);
        }
        int count = this.menu.getMenuComponentCount();
        if (count > 0 && (this.menu.getMenuComponent(count - 1) instanceof JPopupMenu.Separator)) {
            this.menu.remove(count - 1);
        }
        return this.menu;
    }
}
