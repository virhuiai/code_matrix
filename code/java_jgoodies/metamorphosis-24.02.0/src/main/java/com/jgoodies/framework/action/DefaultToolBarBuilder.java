package com.jgoodies.framework.action;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.action.ActionGroup;
import com.jgoodies.common.jsdl.action.SplitAction;
import com.jgoodies.components.JGComponentFactory;
import com.jgoodies.components.JGToolBarMenuButton;
import com.jgoodies.components.JGToolBarSplitButton;
import com.jgoodies.components.util.ComponentUtils;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/action/DefaultToolBarBuilder.class */
public final class DefaultToolBarBuilder implements ActionGroup.ActionGroupBuilder {
    private final JToolBar toolBar;
    private final JGComponentFactory factory;

    public DefaultToolBarBuilder() {
        this(new JToolBar(), JGComponentFactory.getCurrent());
    }

    public DefaultToolBarBuilder(JToolBar toolBar) {
        this(toolBar, JGComponentFactory.getCurrent());
    }

    public DefaultToolBarBuilder(JToolBar toolBar, JGComponentFactory factory) {
        Preconditions.checkNotNull(toolBar, Messages.MUST_NOT_BE_NULL, "tool bar");
        Preconditions.checkNotNull(factory, Messages.MUST_NOT_BE_NULL, "component factory");
        this.toolBar = toolBar;
        this.factory = factory;
    }

    public static JToolBar toolBarFor(ActionGroup group) {
        if (group == null) {
            return null;
        }
        DefaultToolBarBuilder builder = new DefaultToolBarBuilder();
        group.build(builder);
        return builder.build();
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void setLabelAction(Action labelAction) {
        if (labelAction == null) {
            return;
        }
        this.toolBar.setName((String) labelAction.getValue("Name"));
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void add(Action action) {
        JButton button = this.factory.createButton(action);
        ComponentUtils.registerKeyboardAction((JComponent) button, action, 2);
        this.toolBar.add(button);
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void add(ActionGroup group) {
        DefaultPopupMenuBuilder menuBuilder = new DefaultPopupMenuBuilder();
        group.build(menuBuilder);
        JPopupMenu popupMenu = menuBuilder.build();
        JGToolBarMenuButton button = new JGToolBarMenuButton(group.getLabelAction(), popupMenu);
        this.toolBar.add(button);
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void add(SplitAction splitAction) {
        Action action = splitAction.getAction();
        DefaultPopupMenuBuilder menuBuilder = new DefaultPopupMenuBuilder();
        splitAction.getGroup().build(menuBuilder);
        JPopupMenu popupMenu = menuBuilder.build();
        JGToolBarSplitButton button = new JGToolBarSplitButton(action, popupMenu);
        ComponentUtils.registerKeyboardAction((JComponent) button, action, 2);
        this.toolBar.add(button);
    }

    @Override // com.jgoodies.common.jsdl.action.ActionGroup.ActionGroupBuilder
    public void addSeparator() {
        this.toolBar.addSeparator();
    }

    public JToolBar build() {
        return this.toolBar;
    }
}
