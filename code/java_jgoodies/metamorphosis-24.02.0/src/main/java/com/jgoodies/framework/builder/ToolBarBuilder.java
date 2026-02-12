package com.jgoodies.framework.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.BuilderSupport;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.internal.IActionObject;
import com.jgoodies.components.JGToolBarButton;
import com.jgoodies.components.JGToolBarSplitButton;
import com.jgoodies.components.util.ComponentUtils;
import com.jgoodies.components.util.OSXComponentProperties;
import java.awt.Component;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/builder/ToolBarBuilder.class */
public final class ToolBarBuilder {
    private JToolBar toolBar;
    private IActionObject actionProvider;
    private final BuilderSupport support = new BuilderSupport();
    private boolean registerKeyboardActions = false;

    public JToolBar getToolBar() {
        if (this.toolBar == null) {
            this.toolBar = new JToolBar();
            configureToolBar(this.toolBar);
        }
        return this.toolBar;
    }

    private static void configureToolBar(JToolBar toolBar) {
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
    }

    private Component add(Component component) {
        return getToolBar().add(component);
    }

    public JToolBar build() {
        return getToolBar();
    }

    public ToolBarBuilder toolBar(JToolBar toolBar) {
        this.support.checkNotCalledTwice("toolBar");
        this.toolBar = toolBar;
        return this;
    }

    public ToolBarBuilder actionProvider(IActionObject actionProvider) {
        this.support.checkNotCalledTwice("actionProvider");
        this.actionProvider = actionProvider;
        return this;
    }

    public ToolBarBuilder clientProperty(Object key, Object value) {
        getToolBar().putClientProperty(key, value);
        return this;
    }

    public ToolBarBuilder border(Border border) {
        this.support.checkNotCalledTwice("border");
        getToolBar().setBorder(border);
        return this;
    }

    public ToolBarBuilder padding(EmptyBorder padding) {
        this.support.checkNotCalledTwice("padding");
        getToolBar().setBorder(padding);
        return this;
    }

    public ToolBarBuilder opaque(boolean opaque) {
        this.support.checkNotCalledTwice("opaque");
        getToolBar().setOpaque(opaque);
        return this;
    }

    public ToolBarBuilder registerKeyboardActions(boolean b) {
        this.support.checkNotCalledTwice("registerKeyboardActions");
        this.registerKeyboardActions = b;
        return this;
    }

    public ToolBarBuilder doWith(Consumer<ToolBarBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public ToolBarBuilder gap() {
        return gap(6);
    }

    public ToolBarBuilder largeGap() {
        return gap(12);
    }

    public ToolBarBuilder gap(int size) {
        add(Box.createRigidArea(new Dimension(size, size)));
        return this;
    }

    public ToolBarBuilder glue() {
        add(Box.createGlue());
        return this;
    }

    public ToolBarBuilder separator() {
        getToolBar().addSeparator();
        return this;
    }

    public ToolBarBuilder button(Component component) {
        if (component instanceof JComponent) {
            ((JComponent) component).setOpaque(getToolBar().isOpaque());
        }
        add(component);
        return this;
    }

    public ToolBarBuilder segmented(AbstractButton... buttons) {
        Preconditions.checkNotNull(buttons, Messages.MUST_NOT_BE_NULL, "components array");
        Preconditions.checkArgument(buttons.length >= 2, "You must provide at least two components.");
        for (int i = 0; i < buttons.length; i++) {
            AbstractButton button = buttons[i];
            OSXComponentProperties.setSegmentedTypeAndPosition(button, OSXComponentProperties.SegmentedButtonType.TEXTURED, i, buttons.length);
            add(button);
        }
        return this;
    }

    public ToolBarBuilder splitButton(Action action, JPopupMenu popupMenu) {
        JButton button = new JGToolBarSplitButton(action, popupMenu);
        button.setOpaque(getToolBar().isOpaque());
        OSXComponentProperties.setButtonType(button, OSXComponentProperties.ButtonType.TEXTURED);
        if (this.registerKeyboardActions) {
            ComponentUtils.registerKeyboardAction((JComponent) button, action, 2);
        }
        add(button);
        return this;
    }

    public ToolBarBuilder action(Action action) {
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action");
        JButton button = new JGToolBarButton(action);
        button.setOpaque(getToolBar().isOpaque());
        OSXComponentProperties.setButtonType(button, OSXComponentProperties.ButtonType.TEXTURED);
        if (this.registerKeyboardActions) {
            ComponentUtils.registerKeyboardAction((JComponent) button, action, 2);
        }
        add(button);
        return this;
    }

    public ToolBarBuilder action(String actionName) {
        return action(getActionProvider(), actionName);
    }

    public ToolBarBuilder action(IActionObject actionProvider, String actionName) {
        Preconditions.checkNotNull(actionProvider, Messages.MUST_NOT_BE_NULL, "action provider");
        Preconditions.checkNotNull(actionName, Messages.MUST_NOT_BE_NULL, "action name");
        return action(actionProvider.getAction(actionName));
    }

    private IActionObject getActionProvider() {
        Preconditions.checkNotNull(this.actionProvider, "To use the #action(String) feature, a default action provider must be set, see #actionProvider.");
        return this.actionProvider;
    }
}
