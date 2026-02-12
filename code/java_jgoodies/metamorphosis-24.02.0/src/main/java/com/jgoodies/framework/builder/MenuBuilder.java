package com.jgoodies.framework.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.common.swing.internal.IActionObject;
import com.jgoodies.components.JGCheckBoxMenuItem;
import com.jgoodies.components.JGComponentFactory;
import com.jgoodies.components.JGMenuItem;
import com.jgoodies.components.JGRadioButtonMenuItem;
import com.jgoodies.components.util.Mode;
import com.jgoodies.components.util.Modes;
import com.jgoodies.looks.Options;
import java.util.function.Consumer;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/builder/MenuBuilder.class */
public final class MenuBuilder {
    private static Mode iconVisibleDefaultMode = Modes.LAF_NON_AQUA;
    private Mode iconVisibleMode;
    private String label;
    private JMenu menu;
    private IActionObject actionProvider;
    private JGComponentFactory factory;

    public MenuBuilder() {
        iconVisibleMode(getIconVisibleDefaultMode());
    }

    public static Mode getIconVisibleDefaultMode() {
        return iconVisibleDefaultMode;
    }

    public static void setIconVisibleDefaultMode(Mode newDefaultMode) {
        iconVisibleDefaultMode = newDefaultMode;
    }

    public JMenu getMenu() {
        if (this.menu == null) {
            this.menu = new JMenu(this.label);
            MnemonicUtils.configure((AbstractButton) this.menu, this.label);
        }
        return this.menu;
    }

    public void add(ActionMap actionMap, String... actionNames) {
        Preconditions.checkNotNull(actionNames, Messages.MUST_NOT_BE_NULL, "action names");
        for (String actionName : actionNames) {
            if (actionName == null || actionName.startsWith("---")) {
                separator();
            } else {
                Action action = actionMap.get(actionName);
                Preconditions.checkArgument(action != null, "No action found for name %s.", actionName);
                action(action);
            }
        }
    }

    public MenuBuilder label(String markedText) {
        Preconditions.checkArgument(this.label == null, "The label must be set only once.");
        this.label = markedText;
        return this;
    }

    public MenuBuilder menu(JMenu menu) {
        Preconditions.checkArgument(this.menu == null, "The menu must be set only once.");
        this.menu = menu;
        return this;
    }

    public MenuBuilder actionProvider(IActionObject actionProvider) {
        Preconditions.checkArgument(this.actionProvider == null, "The action provider must be set only once.");
        this.actionProvider = actionProvider;
        return this;
    }

    public MenuBuilder factory(JGComponentFactory factory) {
        Preconditions.checkArgument(this.factory == null, "The factory must be set only once.");
        this.factory = factory;
        return this;
    }

    public JMenu build() {
        JMenu menu = getMenu();
        if (menu.getMenuComponentCount() > 0 && (menu.getMenuComponent(0) instanceof JPopupMenu.Separator)) {
            menu.remove(0);
        }
        int count = menu.getMenuComponentCount();
        if (count > 0 && (menu.getMenuComponent(count - 1) instanceof JPopupMenu.Separator)) {
            menu.remove(count - 1);
        }
        return menu;
    }

    public MenuBuilder iconVisibleMode(Mode newMode) {
        this.iconVisibleMode = newMode;
        return this;
    }

    public MenuBuilder clientProperty(Object key, Object value) {
        getMenu().putClientProperty(key, value);
        return this;
    }

    public MenuBuilder noIcons() {
        clientProperty(Options.NO_ICONS_KEY, Boolean.TRUE);
        return this;
    }

    public MenuBuilder lightWeightPopupEnabled(boolean b) {
        getMenu().getPopupMenu().setLightWeightPopupEnabled(b);
        return this;
    }

    public MenuBuilder doWith(Consumer<MenuBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public MenuBuilder action(Action action) {
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action");
        return item(getFactory().createMenuItem(action));
    }

    public MenuBuilder action(String actionName) {
        return action(getTarget(), actionName);
    }

    public MenuBuilder action(IActionObject actionProvider, String actionName) {
        Preconditions.checkNotNull(actionProvider, Messages.MUST_NOT_BE_NULL, "action provider");
        Preconditions.checkNotNull(actionName, Messages.MUST_NOT_BE_NULL, "action name");
        return action(actionProvider.getAction(actionName));
    }

    public MenuBuilder item(String markedText) {
        JMenuItem item = getFactory().createMenuItem(markedText);
        return item(item);
    }

    public MenuBuilder item(String markedText, KeyStroke accelerator) {
        JMenuItem item = getFactory().createMenuItem(markedText);
        item.setAccelerator(accelerator);
        return item(item);
    }

    public MenuBuilder item(String markedText, String accelerator) {
        return item(markedText, KeyStroke.getKeyStroke(accelerator));
    }

    public MenuBuilder item(String markedText, Icon icon) {
        JMenuItem item = getFactory().createMenuItem(markedText, icon);
        return item(item);
    }

    public MenuBuilder item(String markedText, Icon icon, KeyStroke accelerator) {
        JMenuItem item = getFactory().createMenuItem(markedText, icon);
        item.setAccelerator(accelerator);
        return item(item);
    }

    public MenuBuilder item(String markedText, Icon icon, String accelerator) {
        return item(markedText, icon, KeyStroke.getKeyStroke(accelerator));
    }

    public MenuBuilder item(JMenuItem item) {
        if (item instanceof JGMenuItem) {
            ((JGMenuItem) item).setIconVisibleMode(this.iconVisibleMode);
        } else if (item instanceof JGCheckBoxMenuItem) {
            ((JGCheckBoxMenuItem) item).setIconVisibleMode(this.iconVisibleMode);
        } else if (item instanceof JGRadioButtonMenuItem) {
            ((JGRadioButtonMenuItem) item).setIconVisibleMode(this.iconVisibleMode);
        }
        getMenu().add(item);
        return this;
    }

    public MenuBuilder separator() {
        getMenu().addSeparator();
        return this;
    }

    public MenuBuilder action(boolean expression, Action action) {
        if (expression) {
            action(action);
        }
        return this;
    }

    public MenuBuilder action(boolean expression, IActionObject actionProvider, String actionName) {
        if (expression) {
            Preconditions.checkNotNull(actionProvider, Messages.MUST_NOT_BE_NULL, "action provider");
            Preconditions.checkNotNull(actionName, Messages.MUST_NOT_BE_NULL, "action name");
            action(actionProvider.getAction(actionName));
        }
        return this;
    }

    public MenuBuilder action(boolean expression, String actionName) {
        if (expression) {
            action(actionName);
        }
        return this;
    }

    public MenuBuilder separator(boolean expression) {
        if (expression) {
            separator();
        }
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
