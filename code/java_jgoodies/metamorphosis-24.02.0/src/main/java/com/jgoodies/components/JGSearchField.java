package com.jgoodies.components;

import com.jgoodies.binding.adapter.TextComponentConnector;
import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.internal.MyListeners;
import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.components.internal.JGTextComponent;
import com.jgoodies.components.internal.PromptSupport;
import com.jgoodies.components.internal.TextFieldIcons;
import com.jgoodies.components.internal.TextFieldSupport;
import com.jgoodies.components.util.ComponentUtils;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSearchField.class */
public class JGSearchField extends JTextField implements JGTextComponent {
    public static final String PROPERTY_SEARCH_TEXT = "searchText";
    public static final String PROPERTY_SEARCH_MODE = "searchMode";
    public static final String PROPERTY_INSTANT_SEARCH_DELAY = "instantSearchDelay";
    private static final Action SEARCH_ACTION = new SearchAction();
    private static final Action CLEAR_ACTION = new ClearAction();
    private final ValueModel textHolder;
    private final DelayedPropertyChangeHandler instantSearchHandler;
    private SearchMode searchMode;
    private String searchText;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSearchField$SearchMode.class */
    public enum SearchMode {
        INSTANT,
        REGULAR
    }

    public JGSearchField() {
        this(SearchMode.INSTANT);
    }

    public JGSearchField(int columns) {
        this(columns, SearchMode.INSTANT);
    }

    public JGSearchField(SearchMode mode) {
        this(0, mode);
    }

    public JGSearchField(int columns, SearchMode mode) {
        super(columns);
        this.textHolder = new ValueHolder();
        this.searchMode = SearchMode.REGULAR;
        this.searchText = "";
        this.instantSearchHandler = MyListeners.delayed(this::onDelayedTextChange);
        this.instantSearchHandler.setCoalesce(true);
        setIconVisibleAlways(true);
        setSearchMode(mode);
        configureSearch();
        initEventHandling();
    }

    public String getSearchText() {
        return this.searchText;
    }

    private void setSearchText(String newSearchText) {
        String oldSearchText = getSearchText();
        this.searchText = newSearchText;
        firePropertyChange(PROPERTY_SEARCH_TEXT, oldSearchText, newSearchText);
    }

    public void resetSearchText() {
        this.searchText = "";
        boolean regular = this.searchMode == SearchMode.REGULAR;
        if (isEmpty() || regular) {
            configureSearch();
        }
    }

    public void search() {
        if (isEmpty()) {
            configureSearch();
        } else {
            configureClear();
        }
        setSearchText(getText());
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final String getPrompt() {
        return PromptSupport.getPrompt(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPrompt(String prompt) {
        PromptSupport.setPrompt(this, prompt);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final int getPromptStyle() {
        return PromptSupport.getPromptStyle(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPromptStyle(int style) {
        PromptSupport.setPromptStyle(this, style);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final boolean isPromptVisibleWhenFocused() {
        return PromptSupport.isPromptVisibleWhenFocused(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPromptVisibleWhenFocused(boolean newValue) {
        PromptSupport.setPromptVisibleWhenFocused(this, newValue);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final boolean isJGFocusTraversable() {
        return FocusTraversalUtils.isFocusTraversable(this).booleanValue();
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setJGFocusTraversable(Boolean value) {
        FocusTraversalUtils.setFocusTraversable((JTextComponent) this, value);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final Boolean getSelectOnFocusGainEnabled() {
        return TextFieldSupport.getSelectOnFocusGainEnabled(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setSelectOnFocusGainEnabled(Boolean b) {
        TextFieldSupport.setSelectOnFocusGainEnabled(this, b);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setKeyboardAction(Action keyboardAction) {
        ComponentUtils.registerKeyboardAction(this, keyboardAction);
    }

    public final SearchMode getSearchMode() {
        return this.searchMode;
    }

    public final void setSearchMode(SearchMode newValue) {
        Preconditions.checkNotNull(newValue, Messages.MUST_NOT_BE_NULL, "search mode");
        SearchMode oldValue = getSearchMode();
        this.searchMode = newValue;
        boolean regular = this.searchMode == SearchMode.REGULAR;
        setButtonPaintedAlways(regular);
        if (oldValue == newValue) {
            return;
        }
        firePropertyChange(PROPERTY_SEARCH_MODE, oldValue, newValue);
        if (isEmpty() || regular) {
            configureSearch();
        } else {
            configureClear();
        }
        if (regular) {
            this.textHolder.removeValueChangeListener(this.instantSearchHandler);
        } else {
            this.textHolder.addValueChangeListener(this.instantSearchHandler);
        }
    }

    public final int getInstantSearchDelay() {
        return this.instantSearchHandler.getDelay();
    }

    public final void setInstantSearchDelay(int newDelay) {
        int oldDelay = getInstantSearchDelay();
        if (oldDelay == newDelay) {
            return;
        }
        this.instantSearchHandler.setDelay(newDelay);
        firePropertyChange(PROPERTY_INSTANT_SEARCH_DELAY, oldDelay, newDelay);
    }

    public final JPopupMenu getMenu() {
        return TextFieldSupport.getMenu(this);
    }

    public final void setMenu(JPopupMenu popupMenu) {
        TextFieldSupport.setMenu(this, popupMenu);
    }

    public Dimension getPreferredSize() {
        return TextFieldSupport.getPreferredSize(this, super.getPreferredSize());
    }

    public String getToolTipText(MouseEvent e) {
        return TextFieldSupport.getToolTipText(this, e, super.getToolTipText());
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        PromptSupport.paintPrompt(this, g);
    }

    protected void processComponentKeyEvent(KeyEvent evt) {
        if (evt.getID() == 401) {
            switch (evt.getKeyCode()) {
                case 10:
                    if (isRegular() && hasPendingSearch()) {
                        search();
                        evt.consume();
                        break;
                    }
                    break;
                case 27:
                    if (!isEmpty()) {
                        clear();
                        evt.consume();
                        break;
                    }
                    break;
            }
        }
        super.processComponentKeyEvent(evt);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasPendingSearch() {
        return !getSearchText().equals(getText());
    }

    public void updateUI() {
        super.updateUI();
        JPopupMenu menu = getMenu();
        if (menu != null) {
            menu.updateUI();
        }
        TextFieldSupport.updateUI(this);
    }

    private boolean isEmpty() {
        return Strings.isEmpty(getText());
    }

    private boolean isRegular() {
        return getSearchMode() == SearchMode.REGULAR;
    }

    private void setIcon(Icon newIcon) {
        TextFieldSupport.setIcon(this, newIcon);
    }

    private void setIconVisibleAlways(boolean newValue) {
        TextFieldSupport.setIconVisibleAlways(this, newValue);
    }

    private void setIconAction(Action iconAction) {
        TextFieldSupport.setIconAction((JTextComponent) this, iconAction);
    }

    private void setButtonPaintedAlways(boolean newValue) {
        TextFieldSupport.setButtonPaintedAlways(this, newValue);
    }

    private void initEventHandling() {
        TextComponentConnector.connect(this.textHolder, this);
        this.textHolder.addValueChangeListener(this::onTextChange);
    }

    private void configureSearch() {
        setIcon(TextFieldIcons.getSearchIcon());
        setIconAction(isRegular() ? SEARCH_ACTION : null);
    }

    private void configureClear() {
        setIcon(TextFieldIcons.getClearIcon());
        setIconAction(CLEAR_ACTION);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clear() {
        setText("");
        setSearchText(getText());
    }

    private void onTextChange(PropertyChangeEvent evt) {
        if (isRegular() || isEmpty()) {
            configureSearch();
        } else {
            configureClear();
        }
    }

    private void onDelayedTextChange(PropertyChangeEvent evt) {
        search();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSearchField$ClearAction.class */
    private static final class ClearAction extends AbstractAction {
        private ClearAction() {
        }

        public void actionPerformed(ActionEvent evt) {
            ((JGSearchField) evt.getSource()).clear();
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSearchField$SearchAction.class */
    private static final class SearchAction extends AbstractAction {
        private SearchAction() {
        }

        public void actionPerformed(ActionEvent evt) {
            JGSearchField source = (JGSearchField) evt.getSource();
            if (source.hasPendingSearch()) {
                source.search();
            }
        }
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJGSearchField();
        }
        return this.accessibleContext;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSearchField$AccessibleJGSearchField.class */
    private final class AccessibleJGSearchField extends AccessibleJTextField {
        private AccessibleJGSearchField() {
            super(JGSearchField.this);
        }

        public String getAccessibleName() {
            String name = super.getAccessibleName();
            if (name != null) {
                return name;
            }
            return JGSearchField.this.getPrompt();
        }

        public String getAccessibleDescription() {
            String description = super.getAccessibleDescription();
            if (description != null) {
                return description;
            }
            String name = getAccessibleName();
            if (Objects.equals(name, JGSearchField.this.getPrompt())) {
                return null;
            }
            return JGSearchField.this.getPrompt();
        }
    }
}
