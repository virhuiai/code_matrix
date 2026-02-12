package com.jgoodies.framework.util;

import com.jgoodies.application.Application;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.jsdl.action.I18nActionBuilder;
import com.jgoodies.looks.Options;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Objects;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/History.class */
public class History<E> extends Bean {
    public static final String PROPERTY_HOME = "home";
    public static final String PROPERTY_SELECTION = "selection";
    private static final String ACTION_BACK = "history.Back";
    private static final String ACTION_NEXT = "history.Next";
    private static final String ACTION_HOME = "history.Home";
    private static final int MAX_TOOLTIP_LENGTH = 60;
    private static final ResourceMap RESOURCES = Application.getResourceMap(History.class);
    private final int capacity;
    private final Action backAction;
    private final Action nextAction;
    private final Action homeAction;
    private final JPopupMenu backPopupMenu;
    private final JPopupMenu nextPopupMenu;
    private E home;
    private HistoryList<E> list;

    public History(int capacity) {
        this(capacity, ACTION_BACK, ACTION_NEXT, ACTION_HOME, RESOURCES);
    }

    public History(int capacity, String goBackActionName, String goNextActionName, String goHomeActionName, ResourceMap actionResources) {
        this.capacity = capacity;
        this.backAction = new I18nActionBuilder().handler(this::goBack).resources(actionResources).id(goBackActionName).build();
        this.nextAction = new I18nActionBuilder().handler(this::goNext).resources(actionResources).id(goNextActionName).build();
        this.homeAction = new I18nActionBuilder().handler(this::goHome).resources(actionResources).id(goHomeActionName).build();
        this.backPopupMenu = new JPopupMenu();
        this.backPopupMenu.putClientProperty(Options.NO_ICONS_KEY, Boolean.TRUE);
        this.nextPopupMenu = new JPopupMenu();
        this.nextPopupMenu.putClientProperty(Options.NO_ICONS_KEY, Boolean.TRUE);
        reset();
    }

    public E getHome() {
        return this.home;
    }

    public void setHome(E newHome) {
        E oldHome = getHome();
        this.home = newHome;
        firePropertyChange(PROPERTY_HOME, oldHome, newHome);
    }

    public E getSelection() {
        return this.list.getLastAdded();
    }

    public void setSelection(E newSelection) {
        E oldSelection = getSelection();
        if (Objects.equals(oldSelection, newSelection)) {
            return;
        }
        setSelection0(newSelection);
    }

    public void removeAll(E element) {
        this.list.removeAll(element);
        updateActions();
    }

    public void removeAndGoBack(E element) {
        this.list.removeAll(element);
        setSelection0(this.list.getLastAdded());
    }

    public void reset() {
        this.list = new HistoryList<>(this.capacity);
        this.backAction.setEnabled(false);
        this.nextAction.setEnabled(false);
        this.homeAction.setEnabled(false);
    }

    public void goBack() {
        setSelection0(this.list.getAndGoPrevious());
    }

    public void goNext() {
        setSelection0(this.list.getAndGoNext());
    }

    public void goHome() {
        setSelection0(this.home);
    }

    public Action getBackAction() {
        return this.backAction;
    }

    public Action getNextAction() {
        return this.nextAction;
    }

    public Action getHomeAction() {
        return this.homeAction;
    }

    public JPopupMenu getBackPopupMenu() {
        return this.backPopupMenu;
    }

    public JPopupMenu getNextPopupMenu() {
        return this.nextPopupMenu;
    }

    protected String createToolTip(E element) {
        return Strings.abbreviateCenter(element.toString(), MAX_TOOLTIP_LENGTH);
    }

    private String getBackText() {
        E previous = this.list.getPrevious();
        return null == previous ? RESOURCES.getString("history.back", new Object[0]) : RESOURCES.getString("history.backTo", createToolTip(previous));
    }

    private String getNextText() {
        E next = this.list.getNext();
        return null == next ? RESOURCES.getString("history.next", new Object[0]) : RESOURCES.getString("history.nextTo", createToolTip(next));
    }

    private void setSelection0(E newSelection) {
        if (null == newSelection) {
            return;
        }
        this.list.add(newSelection);
        updateMenu(this.backPopupMenu, this.list.getBackIterator(), true);
        updateMenu(this.nextPopupMenu, this.list.getNextIterator(), false);
        updateActions();
        firePropertyChange("selection", (Object) null, newSelection);
    }

    private void updateActions() {
        E selection = getSelection();
        E previous = this.list.getPrevious();
        E next = this.list.getNext();
        String goBackToolTip = getBackText();
        String goNextToolTip = getNextText();
        this.backAction.putValue("ShortDescription", goBackToolTip);
        this.backAction.putValue("Name", goBackToolTip);
        this.nextAction.putValue("ShortDescription", goNextToolTip);
        this.nextAction.putValue("Name", goNextToolTip);
        this.backAction.setEnabled(previous != null);
        this.nextAction.setEnabled(next != null);
        this.homeAction.setEnabled((this.home == null || this.home.equals(selection)) ? false : true);
    }

    private void updateMenu(JPopupMenu menu, Iterator<E> items, boolean goBack) {
        menu.removeAll();
        int steps = 1;
        while (items.hasNext()) {
            JMenuItem menuItem = new JMenuItem(createToolTip(items.next()));
            menuItem.putClientProperty("noIcon", Boolean.TRUE);
            menuItem.addActionListener(new GoMenuActionListener(steps, goBack));
            menu.add(menuItem);
            steps++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState() {
        updateMenu(this.backPopupMenu, this.list.getBackIterator(), true);
        updateMenu(this.nextPopupMenu, this.list.getNextIterator(), false);
        updateActions();
        firePropertyChange("selection", (Object) null, getSelection());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/History$GoMenuActionListener.class */
    public final class GoMenuActionListener implements ActionListener {
        private final int steps;
        private final boolean back;

        private GoMenuActionListener(int steps, boolean back) {
            this.steps = steps;
            this.back = back;
        }

        public void actionPerformed(ActionEvent e) {
            if (this.back) {
                History.this.list.goBack(this.steps);
            } else {
                History.this.list.goNext(this.steps);
            }
            History.this.updateState();
        }
    }
}
