package com.jgoodies.framework.selection;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.internal.Messages;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/selection/AbstractRowSelectionManager.class */
public abstract class AbstractRowSelectionManager extends Bean {
    public static final String PROPERTY_LOOPING = "looping";
    public static final String PROPERTY_PAGE_INCREMENT = "pageIncrement";
    public static final String PROPERTY_SELECT_FIRST_ON_EMPTY_SELECTION = "selectFirstOnEmptySelection";
    protected static final int DEFAULT_PAGE_INCREMENT = 10;
    private static final String SELECTION_UP = "row-selection-up";
    private static final String SELECTION_DOWN = "row-selection-down";
    private static final String SELECTION_PAGE_UP = "row-selection-page-up";
    private static final String SELECTION_PAGE_DOWN = "row-selection-page-down";
    private static final String SELECTION_HOME = "row-selection-home";
    private static final String SELECTION_END = "row-selection-end";
    private int pageIncrement = DEFAULT_PAGE_INCREMENT;
    private boolean looping = false;
    private boolean selectFirstOnEmptySelection = true;

    protected abstract int getSelectedIndex();

    protected abstract void setSelectedIndex(int i);

    protected abstract int getRowCount();

    public void registerKeyboardActions(JComponent... components) {
        Preconditions.checkNotNull(components, Messages.MUST_NOT_BE_NULL, "components");
        for (JComponent c : components) {
            registerKeyboardActions0(c);
        }
    }

    public static final void unregisterKeyboardActions(JComponent c) {
        c.setInputMap(0, c.getInputMap().getParent());
    }

    public final boolean isLooping() {
        return this.looping;
    }

    public final void setLooping(boolean newValue) {
        boolean oldValue = isLooping();
        this.looping = newValue;
        firePropertyChange(PROPERTY_LOOPING, oldValue, newValue);
    }

    public final int getPageIncrement() {
        return this.pageIncrement;
    }

    public final void setPageIncrement(int newValue) {
        int oldValue = getPageIncrement();
        this.pageIncrement = newValue;
        firePropertyChange(PROPERTY_PAGE_INCREMENT, oldValue, newValue);
    }

    public final boolean getSelectFirstOnEmptySelection() {
        return this.selectFirstOnEmptySelection;
    }

    public final void setSelectFirstOnEmptySelection(boolean newValue) {
        boolean oldValue = getSelectFirstOnEmptySelection();
        this.selectFirstOnEmptySelection = newValue;
        firePropertyChange(PROPERTY_SELECT_FIRST_ON_EMPTY_SELECTION, oldValue, newValue);
    }

    protected final boolean isEmpty() {
        return getRowCount() == 0;
    }

    protected final boolean isSelectionEmpty() {
        return getSelectedIndex() == -1;
    }

    protected final void changeIndex(int increment, boolean loop) {
        if (isSelectionEmpty()) {
            if (getSelectFirstOnEmptySelection() && !isEmpty()) {
                setSelectedIndex(0);
                return;
            }
            return;
        }
        int newIndex = getSelectedIndex() + increment;
        int lastIndex = getRowCount() - 1;
        if (newIndex < 0) {
            newIndex = loop ? lastIndex : 0;
        } else if (newIndex > lastIndex) {
            newIndex = loop ? 0 : lastIndex;
        }
        setSelectedIndex(newIndex);
    }

    private void registerKeyboardActions0(JComponent c) {
        InputMap oldInputMap = c.getInputMap(0);
        InputMap newInputMap = new InputMap();
        newInputMap.setParent(oldInputMap);
        c.setInputMap(0, newInputMap);
        registerKeyboardAction(c, SELECTION_UP, KeyStroke.getKeyStroke(38, 0));
        registerKeyboardAction(c, SELECTION_DOWN, KeyStroke.getKeyStroke(40, 0));
        registerKeyboardAction(c, SELECTION_PAGE_UP, KeyStroke.getKeyStroke(33, 0));
        registerKeyboardAction(c, SELECTION_PAGE_DOWN, KeyStroke.getKeyStroke(34, 0));
        registerKeyboardAction(c, SELECTION_HOME, KeyStroke.getKeyStroke(36, 128));
        registerKeyboardAction(c, SELECTION_END, KeyStroke.getKeyStroke(35, 128));
    }

    private void registerKeyboardAction(JComponent c, String command, KeyStroke keyStroke) {
        c.getInputMap().put(keyStroke, command);
        c.getActionMap().put(command, new DispatchingSelectionAction(command));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/selection/AbstractRowSelectionManager$DispatchingSelectionAction.class */
    public static final class DispatchingSelectionAction extends AbstractAction {
        private final AbstractRowSelectionManager manager;

        private DispatchingSelectionAction(AbstractRowSelectionManager manager, String name) {
            super(name);
            this.manager = manager;
        }

        public void actionPerformed(ActionEvent e) {
            Object name = getValue("Name");
            if (name == AbstractRowSelectionManager.SELECTION_UP) {
                this.manager.changeIndex(-1, this.manager.isLooping());
                return;
            }
            if (name == AbstractRowSelectionManager.SELECTION_DOWN) {
                this.manager.changeIndex(1, this.manager.isLooping());
                return;
            }
            if (name == AbstractRowSelectionManager.SELECTION_PAGE_UP) {
                this.manager.changeIndex(-this.manager.getPageIncrement(), false);
                return;
            }
            if (name == AbstractRowSelectionManager.SELECTION_PAGE_DOWN) {
                this.manager.changeIndex(this.manager.getPageIncrement(), false);
                return;
            }
            if (name == AbstractRowSelectionManager.SELECTION_HOME) {
                if (!this.manager.isEmpty()) {
                    this.manager.setSelectedIndex(0);
                }
            } else {
                if (name == AbstractRowSelectionManager.SELECTION_END) {
                    if (!this.manager.isEmpty()) {
                        this.manager.setSelectedIndex(this.manager.getRowCount() - 1);
                        return;
                    }
                    return;
                }
                throw new IllegalStateException("Unknown action " + name);
            }
        }
    }
}
