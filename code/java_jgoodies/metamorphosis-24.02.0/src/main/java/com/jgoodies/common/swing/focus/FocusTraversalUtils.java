package com.jgoodies.common.swing.focus;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/focus/FocusTraversalUtils.class */
public final class FocusTraversalUtils {
    private static final String FOCUS_TRAVERSABLE_KEY = "JGoodies.isFocusTraversable";
    private static final String RADIO_BUTTON_KEY = "JGoodies.isRadioButton";
    private static final String STATIC_TEXT_KEY = "JGoodies.isStaticText";
    private static final String POOR_DEFAULT_FOCUS_OWNER_KEY = "JGoodies.poorDefaultFocusOwner";
    private static final String GROUP_ID_CLIENT_PROPERTY_KEY = "GroupId";
    private static boolean acceptNonEditableTextComponents = false;
    private static int currentGroupId = 0;
    private static boolean populatingGroup = false;
    private static final String SELECT_PREVIOUS = "select-previous";
    private static final DispatchingAction SELECT_PREVIOUS_ACTION = new DispatchingAction(SELECT_PREVIOUS);
    private static final String SELECT_NEXT = "select-next";
    private static final DispatchingAction SELECT_NEXT_ACTION = new DispatchingAction(SELECT_NEXT);

    private FocusTraversalUtils() {
    }

    public static boolean getAcceptNonEditableTextComponents() {
        return acceptNonEditableTextComponents;
    }

    public static void setAcceptNonEditableTextComponents(boolean b) {
        acceptNonEditableTextComponents = b;
    }

    public static Boolean isFocusTraversable(JTextComponent aComponent) {
        return (Boolean) aComponent.getClientProperty(FOCUS_TRAVERSABLE_KEY);
    }

    public static void setFocusTraversable(JTextComponent aComponent, Boolean value) {
        aComponent.putClientProperty(FOCUS_TRAVERSABLE_KEY, value);
    }

    public static void setFocusTraversable(Boolean value, JTextComponent... components) {
        Preconditions.checkNotNull(components, Messages.MUST_NOT_BE_NULL, "components");
        for (JTextComponent c : components) {
            setFocusTraversable(c, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isRadioButton(Component c) {
        if (c instanceof JRadioButton) {
            return true;
        }
        if (!(c instanceof AbstractButton)) {
            return false;
        }
        AbstractButton jc = (AbstractButton) c;
        return jc.getClientProperty(RADIO_BUTTON_KEY) != null;
    }

    public static <B extends AbstractButton> B markAsRadioButton(B button) {
        button.putClientProperty(RADIO_BUTTON_KEY, Boolean.TRUE);
        return button;
    }

    private static boolean isStaticText(Component aComponent) {
        return (aComponent instanceof JComponent) && Boolean.TRUE.equals(((JComponent) aComponent).getClientProperty(STATIC_TEXT_KEY));
    }

    public static <C extends JComponent> C markAsStaticText(C aComponent) {
        aComponent.putClientProperty(STATIC_TEXT_KEY, Boolean.TRUE);
        return aComponent;
    }

    private static boolean isPoorDefaultFocusOwner(Component aComponent) {
        return (aComponent instanceof JComponent) && Boolean.TRUE.equals(((JComponent) aComponent).getClientProperty(POOR_DEFAULT_FOCUS_OWNER_KEY));
    }

    public static <C extends JComponent> C markAsPoorDefaultFocusOwner(C aComponent) {
        aComponent.putClientProperty(POOR_DEFAULT_FOCUS_OWNER_KEY, Boolean.TRUE);
        return aComponent;
    }

    public static void group(AbstractButton... components) {
        if (components == null || components.length == 0) {
            return;
        }
        Container parent = components[0].getParent();
        currentGroupId++;
        for (AbstractButton abstractButton : components) {
            if (abstractButton.getParent() == null) {
                throw new IllegalArgumentException("Group components must have a parent.\nAdd the components to a container before you group them.\nComponent=" + abstractButton);
            }
            if (abstractButton.getParent() != parent) {
                throw new IllegalArgumentException("All components in a group must have the same parent.\nComponent=" + abstractButton);
            }
            setGroupId(abstractButton, currentGroupId);
            registerKeyboardActions(abstractButton);
        }
    }

    public static void group(List<? extends AbstractButton> buttons) {
        group((AbstractButton[]) buttons.toArray(new AbstractButton[0]));
    }

    public static void ungroup(AbstractButton... components) {
        if (components == null || components.length == 0) {
            return;
        }
        Object groupId = getGroupId(components[0]);
        for (AbstractButton abstractButton : components) {
            if (!isGroupMember(abstractButton)) {
                throw new IllegalArgumentException("You must ungroup only grouped components.\nUngrouped component=" + abstractButton);
            }
            if (getGroupId(abstractButton) != groupId) {
                throw new IllegalArgumentException("All components to be ungrouped must have the same group id.\nComponent=" + abstractButton);
            }
            clearGroupId(abstractButton);
            unregisterKeyboardActions(abstractButton);
        }
    }

    public static Component getDefaultNonStaticTextFocusOwner(Container rootAncestor, Container container) {
        FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
        Component defaultComponent = policy.getDefaultComponent(container);
        Component component = policy.getDefaultComponent(container);
        while (component != null && (isStaticText(component) || isPoorDefaultFocusOwner(component))) {
            component = policy.getComponentAfter(rootAncestor, component);
            if (component == defaultComponent) {
                return null;
            }
        }
        return component;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean accept(Component component) {
        if (!isRadioGroupMember(component)) {
            return true;
        }
        Group group = Group.of(component);
        if (!group.hasSelection()) {
            return true;
        }
        return group.isSelected(component);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPopulatingGroup() {
        return populatingGroup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object getGroupId(Component c) {
        if (c instanceof JComponent) {
            return ((JComponent) c).getClientProperty(GROUP_ID_CLIENT_PROPERTY_KEY);
        }
        return null;
    }

    private static void setGroupId(JComponent c, int id) {
        c.putClientProperty(GROUP_ID_CLIENT_PROPERTY_KEY, Integer.valueOf(id));
    }

    private static void clearGroupId(JComponent c) {
        c.putClientProperty(GROUP_ID_CLIENT_PROPERTY_KEY, (Object) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isGroupMember(Component c) {
        return getGroupId(c) != null;
    }

    private static boolean isRadioGroupMember(Component c) {
        return isGroupMember(c) && isRadioButton(c);
    }

    private static void registerKeyboardActions(JComponent c) {
        registerKeyboardAction(c, KeyStroke.getKeyStroke(38, 0), SELECT_PREVIOUS_ACTION);
        registerKeyboardAction(c, KeyStroke.getKeyStroke(37, 0), SELECT_PREVIOUS_ACTION);
        registerKeyboardAction(c, KeyStroke.getKeyStroke(40, 0), SELECT_NEXT_ACTION);
        registerKeyboardAction(c, KeyStroke.getKeyStroke(39, 0), SELECT_NEXT_ACTION);
    }

    private static void unregisterKeyboardActions(JComponent c) {
        unregisterKeyboardAction(c, KeyStroke.getKeyStroke(38, 0), SELECT_PREVIOUS_ACTION);
        unregisterKeyboardAction(c, KeyStroke.getKeyStroke(37, 0), SELECT_PREVIOUS_ACTION);
        unregisterKeyboardAction(c, KeyStroke.getKeyStroke(40, 0), SELECT_NEXT_ACTION);
        unregisterKeyboardAction(c, KeyStroke.getKeyStroke(39, 0), SELECT_NEXT_ACTION);
    }

    private static void registerKeyboardAction(JComponent c, KeyStroke keyStroke, DispatchingAction action) {
        c.getInputMap().put(keyStroke, action.getCommand());
        c.getActionMap().put(action.getCommand(), action);
    }

    private static void unregisterKeyboardAction(JComponent c, KeyStroke keyStroke, DispatchingAction action) {
        c.getInputMap().put(keyStroke, (Object) null);
        c.getActionMap().put(action.getCommand(), (Action) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/focus/FocusTraversalUtils$DispatchingAction.class */
    public static final class DispatchingAction extends AbstractAction {
        private DispatchingAction(String name) {
            super(name);
        }

        String getCommand() {
            return (String) getValue("Name");
        }

        public void actionPerformed(ActionEvent e) {
            Object name = getValue("Name");
            if (name == FocusTraversalUtils.SELECT_PREVIOUS) {
                selectPrevious();
            } else {
                if (name == FocusTraversalUtils.SELECT_NEXT) {
                    selectNext();
                    return;
                }
                throw new IllegalStateException("Unknown action " + name);
            }
        }

        private static void selectPrevious() {
            Group group = getFocusOwnerGroup();
            if (group != null) {
                group.selectPrevious();
            }
        }

        private static void selectNext() {
            Group group = getFocusOwnerGroup();
            if (group != null) {
                group.selectNext();
            }
        }

        private static Group getFocusOwnerGroup() {
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            return Group.of(focusOwner);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/focus/FocusTraversalUtils$Group.class */
    public static final class Group {
        private int targetIndex;
        private final List<Component> members = new ArrayList();
        private int selectionIndex = -1;

        private Group() {
        }

        static Group of(Component component) {
            if (component == null || !FocusTraversalUtils.isGroupMember(component)) {
                return null;
            }
            Group group = new Group();
            group.populate(component);
            return group;
        }

        boolean hasSelection() {
            return this.selectionIndex != -1;
        }

        boolean isSelected(Component c) {
            return c == this.members.get(this.selectionIndex);
        }

        void selectPrevious() {
            int newSelectionIndex = this.targetIndex - 1;
            if (newSelectionIndex < 0) {
                newSelectionIndex = size() - 1;
            }
            select(newSelectionIndex);
        }

        void selectNext() {
            int newSelectionIndex = this.targetIndex + 1;
            if (newSelectionIndex > size() - 1) {
                newSelectionIndex = 0;
            }
            select(newSelectionIndex);
        }

        private void select(int index) {
            AbstractButton abstractButton = (Component) this.members.get(index);
            abstractButton.requestFocusInWindow();
            if (FocusTraversalUtils.isRadioButton(abstractButton)) {
                abstractButton.setSelected(true);
            }
        }

        private int size() {
            return this.members.size();
        }

        private void populate(Component target) {
            boolean unused = FocusTraversalUtils.populatingGroup = true;
            try {
                populate0(target);
                boolean unused2 = FocusTraversalUtils.populatingGroup = false;
            } catch (Throwable th) {
                boolean unused3 = FocusTraversalUtils.populatingGroup = false;
                throw th;
            }
        }

        private void populate0(Component target) {
            Object id = FocusTraversalUtils.getGroupId(target);
            this.targetIndex = 0;
            int index = 0;
            for (AbstractButton abstractButton : target.getParent().getComponents()) {
                if (id == FocusTraversalUtils.getGroupId(abstractButton)) {
                    this.members.add(abstractButton);
                    if (FocusTraversalUtils.isRadioButton(abstractButton)) {
                        AbstractButton radio = abstractButton;
                        if (radio.isSelected()) {
                            this.selectionIndex = index;
                        }
                    }
                    if (abstractButton == target) {
                        this.targetIndex = index;
                    }
                    index++;
                }
            }
        }
    }
}
