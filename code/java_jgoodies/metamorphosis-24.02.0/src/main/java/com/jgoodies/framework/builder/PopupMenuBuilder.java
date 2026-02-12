package com.jgoodies.framework.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.action.ActionBuilder;
import com.jgoodies.common.swing.internal.IActionObject;
import com.jgoodies.components.JGCheckBoxMenuItem;
import com.jgoodies.components.JGComponentFactory;
import com.jgoodies.components.JGMenuItem;
import com.jgoodies.components.JGRadioButtonMenuItem;
import com.jgoodies.components.util.Mode;
import com.jgoodies.components.util.Modes;
import com.jgoodies.looks.Options;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/builder/PopupMenuBuilder.class */
public final class PopupMenuBuilder {
    private static Mode iconVisibleDefaultMode = Modes.LAF_NON_AQUA;
    private Mode iconVisibleMode;
    private String title;
    private JPopupMenu popupMenu;
    private IActionObject actionProvider;
    private JGComponentFactory factory;

    public PopupMenuBuilder() {
        this(null, null, null);
    }

    public PopupMenuBuilder(String titleText) {
        this(null, null, null);
        title(titleText);
    }

    public PopupMenuBuilder(IActionObject actionProvider) {
        this(null, null, null);
        actionProvider(actionProvider);
    }

    public PopupMenuBuilder(JPopupMenu menu, IActionObject actionProvider, JGComponentFactory factory) {
        popupMenu(menu);
        actionProvider(actionProvider);
        factory(factory);
        iconVisibleMode(getIconVisibleDefaultMode());
    }

    public static Mode getIconVisibleDefaultMode() {
        return iconVisibleDefaultMode;
    }

    public static void setIconVisibleDefaultMode(Mode newDefaultMode) {
        iconVisibleDefaultMode = newDefaultMode;
    }

    public PopupMenuBuilder title(String text) {
        this.title = text;
        return this;
    }

    public PopupMenuBuilder popupMenu(JPopupMenu popupMenu) {
        Preconditions.checkArgument(this.popupMenu == null, "The popup menu must be set only once.");
        this.popupMenu = popupMenu;
        return this;
    }

    public PopupMenuBuilder actionProvider(IActionObject actionProvider) {
        this.actionProvider = actionProvider;
        return this;
    }

    public PopupMenuBuilder factory(JGComponentFactory factory) {
        this.factory = factory;
        return this;
    }

    public PopupMenuBuilder iconVisibleMode(Mode newMode) {
        this.iconVisibleMode = newMode;
        return this;
    }

    public PopupMenuBuilder clientProperty(Object key, Object value) {
        getPopupMenu().putClientProperty(key, value);
        return this;
    }

    public PopupMenuBuilder noIcons() {
        clientProperty(Options.NO_ICONS_KEY, Boolean.TRUE);
        return this;
    }

    public PopupMenuBuilder doWith(Consumer<PopupMenuBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public JPopupMenu getPopupMenu() {
        if (this.popupMenu == null) {
            this.popupMenu = new JPopupMenu(this.title);
        }
        return this.popupMenu;
    }

    public JPopupMenu build() {
        JPopupMenu menu = getPopupMenu();
        if (menu.getComponentCount() > 0 && (menu.getComponent(0) instanceof JPopupMenu.Separator)) {
            menu.remove(0);
        }
        int count = menu.getComponentCount();
        if (count > 0 && (menu.getComponent(count - 1) instanceof JPopupMenu.Separator)) {
            menu.remove(count - 1);
        }
        return menu;
    }

    public void show(MouseEvent evt) {
        build().show(evt.getComponent(), evt.getX(), evt.getY());
    }

    public void show(ActionEvent evt) {
        Component source = (Component) evt.getSource();
        build().show(source, 0, source.getHeight());
    }

    public PopupMenuBuilder action(Action action) {
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action");
        item(getFactory().createMenuItem(action));
        return this;
    }

    public PopupMenuBuilder action(String actionName) {
        return action(getTarget(), actionName);
    }

    public PopupMenuBuilder action(IActionObject actionProvider, String actionName) {
        Preconditions.checkNotNull(actionProvider, Messages.MUST_NOT_BE_NULL, "action provider");
        Preconditions.checkNotNull(actionName, Messages.MUST_NOT_BE_NULL, "action name");
        action(actionProvider.getAction(actionName));
        return this;
    }

    public PopupMenuBuilder item(String markedText, Consumer<ActionEvent> handler) {
        return item(markedText, handler, true);
    }

    public PopupMenuBuilder item(String markedText, Consumer<ActionEvent> handler, boolean enabled) {
        return action(new ActionBuilder().handler(handler).text(markedText, new Object[0]).enabled(enabled).build());
    }

    public PopupMenuBuilder item(String markedText) {
        item(getFactory().createMenuItem(markedText));
        return this;
    }

    public PopupMenuBuilder item(String markedText, Icon icon) {
        item(getFactory().createMenuItem(markedText, icon));
        return this;
    }

    public PopupMenuBuilder item(JMenuItem item) {
        getPopupMenu().add(item);
        return this;
    }

    public PopupMenuBuilder item(JGMenuItem item) {
        item.setIconVisibleMode(this.iconVisibleMode);
        item((JMenuItem) item);
        return this;
    }

    public PopupMenuBuilder item(JGRadioButtonMenuItem item) {
        item.setIconVisibleMode(this.iconVisibleMode);
        item((JMenuItem) item);
        return this;
    }

    public PopupMenuBuilder item(JGCheckBoxMenuItem item) {
        item.setIconVisibleMode(this.iconVisibleMode);
        item((JMenuItem) item);
        return this;
    }

    public PopupMenuBuilder separator() {
        getPopupMenu().addSeparator();
        return this;
    }

    private JGComponentFactory getFactory() {
        if (this.factory == null) {
            this.factory = JGComponentFactory.getCurrent();
        }
        return this.factory;
    }

    private IActionObject getTarget() {
        Preconditions.checkNotNull(this.actionProvider, "To use the #action(String) feature, a default action provider must be set, see #target.");
        return this.actionProvider;
    }
}
