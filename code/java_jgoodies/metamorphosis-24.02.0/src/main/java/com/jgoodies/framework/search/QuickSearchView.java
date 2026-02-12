package com.jgoodies.framework.search;

import com.jgoodies.application.ResourceMap;
import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.Listeners;
import com.jgoodies.components.JGSearchField;
import com.jgoodies.layout.builder.FormBuilder;
import com.jgoodies.looks.Options;
import com.jgoodies.quicksearch.QuickSearchManager;
import com.jgoodies.quicksearch.QuickSearchProcessEvent;
import com.jgoodies.quicksearch.QuickSearchProcessListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import java.util.MissingResourceException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchView.class */
public final class QuickSearchView {
    private final QuickSearchManager manager;
    private final ResourceMap resources;
    private JPopupMenu popupMenu;
    private JLabel animationLabel;
    private JLabel searchLabel;
    private JGSearchField searchField;
    private JPanel searchArea;
    private QuickSearchCategoryView categoryView;
    private static final String ACTIVATE_SELECTION = "activate-selection";
    private static final String SELECTION_UP = "selection-up";
    private static final String SELECTION_DOWN = "selection-down";
    private static final String SELECTION_PAGE_UP = "selection-page-up";
    private static final String SELECTION_PAGE_DOWN = "selection-page-down";
    private static final String SELECTION_HOME = "selection-home";
    private static final String SELECTION_END = "selection-end";
    private static final Icon[] BUSY_ICONS = BusyIcons.getIcons();

    public QuickSearchView(QuickSearchManager manager, ResourceMap resources) {
        this.manager = (QuickSearchManager) Preconditions.checkNotNull(manager, Messages.MUST_NOT_BE_NULL, "QuickSearchManager");
        this.resources = (ResourceMap) Preconditions.checkNotNull(resources, Messages.MUST_NOT_BE_NULL, "ResourceMap");
    }

    public JPopupMenu getPopupMenu() {
        if (this.popupMenu == null) {
            this.popupMenu = buildPopupMenu();
        }
        return this.popupMenu;
    }

    private void initComponents() {
        Image searchBackgroundImage;
        this.animationLabel = new JLabel("");
        this.animationLabel.setVisible(false);
        this.searchLabel = new JLabel(this.resources.getString("QuickSearch.name", new Object[0]));
        this.searchLabel.setForeground(this.resources.getColor("QuickSearch.label.foreground"));
        this.searchField = new JGSearchField(JGSearchField.SearchMode.INSTANT);
        this.searchField.setInstantSearchDelay(500);
        this.searchField.getAccessibleContext().setAccessibleName(this.resources.getString("QuickSearch.name", new Object[0]));
        this.searchField.addFocusListener(Listeners.focusGained(QuickSearchView::onFocusGained));
        registerSearchFieldKeyboardActions();
        Color searchBackground = null;
        try {
            searchBackgroundImage = this.resources.getImage("QuickSearch.searchArea.background.image");
        } catch (MissingResourceException e) {
            searchBackgroundImage = null;
        }
        if (searchBackgroundImage == null) {
            searchBackground = this.resources.getColor("QuickSearch.searchArea.background");
        }
        this.searchArea = new SearchPanel(searchBackgroundImage, searchBackground);
        this.categoryView = new QuickSearchCategoryView(this.manager, this.resources);
    }

    private void initEventHandling() {
        this.manager.addQuickSearchProcessListener(new QuickSearchProcessHandler());
        this.searchField.addPropertyChangeListener(JGSearchField.PROPERTY_SEARCH_TEXT, this::onSearchTextChange);
        MouseHandler handler = new MouseHandler();
        this.categoryView.getPanel().addMouseListener(handler);
        this.categoryView.getPanel().addMouseMotionListener(handler);
    }

    private JPopupMenu buildPopupMenu() {
        initComponents();
        initEventHandling();
        JPopupMenu menu = new JPopupMenu();
        menu.putClientProperty(Options.NO_MARGIN_KEY, Boolean.TRUE);
        menu.setFocusable(true);
        menu.add(buildContent());
        menu.addPropertyChangeListener(ComponentModel.PROPERTY_VISIBLE, this::onPopupMenuVisibleChange);
        return menu;
    }

    private JComponent buildContent() {
        return new FormBuilder().columns("3dlu, right:65dlu, 3dlu, 1px, 2dlu, 3epx, 100dlu, 3dlu", new Object[0]).rows("3dlu, p, 3dlu, 1px, p", new Object[0]).add((Component) buildAnimatedLabel()).xy(2, 2, "fill, center").add((Component) this.searchField).xyw(6, 2, 2).add((Component) this.searchArea).xywh(1, 1, 8, 4).add((Component) this.categoryView.getPanel()).xyw(1, 5, 8).build();
    }

    private JComponent buildAnimatedLabel() {
        return new FormBuilder().columns("p, 2dlu:grow, p", new Object[0]).rows("p", new Object[0]).add((Component) this.animationLabel).xy(1, 1).add((Component) this.searchLabel).xy(3, 1).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void activateSelection(EventObject evt) {
        getPopupMenu().setVisible(false);
        this.categoryView.activateSelection(evt);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateContent() {
        Dimension oldResultViewSize = this.categoryView.getPanel().getPreferredSize();
        this.categoryView.update(this.manager.getActivatables());
        Dimension newResultViewSize = this.categoryView.getPanel().getPreferredSize();
        if (oldResultViewSize.equals(newResultViewSize)) {
            return;
        }
        getPopupMenu().pack();
        this.searchField.requestFocusInWindow();
    }

    private void onPopupMenuVisibleChange(PropertyChangeEvent evt) {
        boolean visible = Boolean.TRUE == evt.getNewValue();
        if (visible) {
            this.searchField.requestFocusInWindow();
            this.searchField.setSelectionStart(0);
            this.searchField.setSelectionEnd(this.searchField.getText().length());
        }
    }

    private static void onFocusGained(FocusEvent e) {
        JTextField field = (JTextField) e.getSource();
        int length = field.getText().length();
        field.setSelectionStart(length);
        field.setSelectionEnd(length);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchView$QuickSearchProcessHandler.class */
    public final class QuickSearchProcessHandler implements QuickSearchProcessListener {
        private final Animator animator;

        QuickSearchProcessHandler() {
            this.animator = new Animator(QuickSearchView.this.animationLabel, QuickSearchView.this.resources.getInt("QuickSearch.animation.frameRate"), QuickSearchView.this.resources.getInt("QuickSearch.animation.initialDelay"));
        }

        public void searchProcessed(QuickSearchProcessEvent event) {
            if (event.isStarted()) {
                this.animator.start();
                return;
            }
            if (event.isStopped()) {
                this.animator.stop();
                QuickSearchView.this.updateContent();
            } else if (event.isChanged()) {
                QuickSearchView.this.updateContent();
            }
        }
    }

    private void onSearchTextChange(PropertyChangeEvent evt) {
        this.manager.setSearchText((String) evt.getNewValue());
    }

    private void registerSearchFieldKeyboardActions() {
        registerKeyboardAction(KeyStroke.getKeyStroke(10, 0), ACTIVATE_SELECTION);
        registerKeyboardAction(KeyStroke.getKeyStroke(38, 0), SELECTION_UP);
        registerKeyboardAction(KeyStroke.getKeyStroke(40, 0), SELECTION_DOWN);
        registerKeyboardAction(KeyStroke.getKeyStroke(33, 0), SELECTION_PAGE_UP);
        registerKeyboardAction(KeyStroke.getKeyStroke(34, 0), SELECTION_PAGE_DOWN);
        registerKeyboardAction(KeyStroke.getKeyStroke(36, 0), SELECTION_HOME);
        registerKeyboardAction(KeyStroke.getKeyStroke(35, 0), SELECTION_END);
    }

    private void registerKeyboardAction(KeyStroke keyStroke, String command) {
        this.searchField.getInputMap().put(keyStroke, command);
        this.searchField.getActionMap().put(command, new DispatchingAction(command));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchView$DispatchingAction.class */
    public final class DispatchingAction extends AbstractAction {
        private DispatchingAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            Object name = getValue("Name");
            if (name == QuickSearchView.ACTIVATE_SELECTION) {
                QuickSearchView.this.activateSelection(e);
                return;
            }
            if (name == QuickSearchView.SELECTION_UP) {
                QuickSearchView.this.categoryView.selectPrevious();
                return;
            }
            if (name == QuickSearchView.SELECTION_DOWN) {
                QuickSearchView.this.categoryView.selectNext();
                return;
            }
            if (name == QuickSearchView.SELECTION_PAGE_UP) {
                QuickSearchView.this.categoryView.selectFirst();
                return;
            }
            if (name == QuickSearchView.SELECTION_PAGE_DOWN) {
                QuickSearchView.this.categoryView.selectLast();
            } else if (name == QuickSearchView.SELECTION_HOME) {
                QuickSearchView.this.categoryView.selectFirst();
            } else {
                if (name == QuickSearchView.SELECTION_END) {
                    QuickSearchView.this.categoryView.selectLast();
                    return;
                }
                throw new IllegalStateException("Unknown action " + name);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchView$MouseHandler.class */
    public final class MouseHandler extends MouseAdapter {
        private MouseHandler() {
        }

        public void mouseEntered(MouseEvent e) {
            QuickSearchView.this.categoryView.select(e);
        }

        public void mouseExited(MouseEvent e) {
            QuickSearchView.this.categoryView.clearSelection();
        }

        public void mouseClicked(MouseEvent e) {
            QuickSearchView.this.activateSelection(e);
        }

        public void mouseMoved(MouseEvent e) {
            QuickSearchView.this.categoryView.select(e);
        }

        public void mouseDragged(MouseEvent e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchView$SearchPanel.class */
    public static final class SearchPanel extends JPanel {
        private final Image image;

        SearchPanel(Image image, Color background) {
            this.image = image;
            if (background != null) {
                setBackground(background);
            }
        }

        protected void paintComponent(Graphics g) {
            if (this.image != null) {
                g.drawImage(this.image, 0, 0, getWidth(), getHeight(), 0, 0, 1, 33, (ImageObserver) null);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/QuickSearchView$Animator.class */
    static final class Animator implements ActionListener {
        private final JLabel animationLabel;
        private final Timer timer;
        private int iconIndex = 0;

        Animator(JLabel animationLabel, int frameRate, int initialDelay) {
            this.animationLabel = animationLabel;
            int delay = 1000 / frameRate;
            this.timer = new Timer(delay, this);
            this.timer.setInitialDelay(initialDelay);
        }

        void start() {
            this.animationLabel.setVisible(true);
            this.timer.start();
        }

        void stop() {
            this.timer.stop();
            this.animationLabel.setVisible(false);
        }

        public void actionPerformed(ActionEvent e) {
            this.animationLabel.setIcon(QuickSearchView.BUSY_ICONS[this.iconIndex]);
            this.iconIndex = (this.iconIndex + 1) % QuickSearchView.BUSY_ICONS.length;
        }
    }
}
