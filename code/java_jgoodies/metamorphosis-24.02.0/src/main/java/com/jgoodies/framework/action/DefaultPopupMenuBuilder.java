package com.jgoodies.framework.action;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.action.ActionGroup;
import com.jgoodies.components.JGComponentFactory;
import javax.swing.Action;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/action/DefaultPopupMenuBuilder.class */
public final class DefaultPopupMenuBuilder implements ActionGroup.ActionGroupBuilder {
    private final JPopupMenu menu;
    private final JGComponentFactory factory;

    public DefaultPopupMenuBuilder() {
        this(new JPopupMenu(), JGComponentFactory.getCurrent());
    }

    public DefaultPopupMenuBuilder(JPopupMenu menu) {
        this(menu, JGComponentFactory.getCurrent());
    }

    public DefaultPopupMenuBuilder(JPopupMenu menu, JGComponentFactory factory) {
        Preconditions.checkNotNull(menu, Messages.MUST_NOT_BE_NULL, "popup menu");
        Preconditions.checkNotNull(factory, Messages.MUST_NOT_BE_NULL, "component factory");
        this.menu = menu;
        this.factory = factory;
    }

    public static JPopupMenu popupMenuFor(ActionGroup group) {
        DefaultPopupMenuBuilder builder = new DefaultPopupMenuBuilder();
        group.build(builder);
        return builder.build();
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void setLabelAction(Action labelAction) {
        if (labelAction != null) {
            this.menu.setLabel((String) labelAction.getValue("Name"));
        }
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

    public JPopupMenu build() {
        if (this.menu.getComponentCount() > 0 && (this.menu.getComponent(0) instanceof JPopupMenu.Separator)) {
            this.menu.remove(0);
        }
        int count = this.menu.getComponentCount();
        if (count > 0 && (this.menu.getComponent(count - 1) instanceof JPopupMenu.Separator)) {
            this.menu.remove(count - 1);
        }
        return this.menu;
    }
}
