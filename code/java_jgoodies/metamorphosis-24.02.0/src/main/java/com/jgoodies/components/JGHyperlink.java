package com.jgoodies.components;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.components.plaf.ComponentSetup;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGHyperlink.class */
public class JGHyperlink extends JGButton {
    public static final String PROPERTY_VISITED = "visited";
    public static final String PROPERTY_VISITED_ENABLED = "visitedEnabled";
    public static final String PROPERTY_ICON_VISIBLE = "iconVisible";
    public static final String PROPERTY_VISITED_FOREGROUND = "visitedForeground";
    private static final String UI_CLASS_ID = "JSDL.HyperlinkUI";
    private boolean visited;
    private boolean visitedEnabled;
    private Color visitedForeground;
    private boolean iconVisible;

    public JGHyperlink() {
        this(null, "Mandatory Text", false);
    }

    public JGHyperlink(String text) {
        this(null, text, false);
    }

    public JGHyperlink(Icon icon, String text, boolean iconVisible) {
        this.visited = false;
        this.visitedEnabled = false;
        Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "text");
        setModel(new DefaultButtonModel());
        setIcon(icon);
        setText(text);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setIconVisible(iconVisible);
        updateUI();
    }

    public JGHyperlink(Action action, boolean iconVisible) {
        this.visited = false;
        this.visitedEnabled = false;
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action");
        String actionName = (String) action.getValue("Name");
        Preconditions.checkNotBlank(actionName, Messages.MUST_NOT_BE_BLANK, "action name");
        setModel(new DefaultButtonModel());
        setAction(action);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setIconVisible(iconVisible);
        updateUI();
    }

    public boolean isIconVisible() {
        return this.iconVisible;
    }

    public void setIconVisible(boolean newValue) {
        boolean oldValue = isIconVisible();
        this.iconVisible = newValue;
        firePropertyChange("iconVisible", oldValue, newValue);
        revalidate();
        repaint();
    }

    public boolean getVisited() {
        return this.visited;
    }

    protected void setVisited(boolean newValue) {
        boolean z = this.visited;
        this.visited = newValue;
        firePropertyChange(PROPERTY_VISITED, z, newValue);
    }

    public boolean isVisitedEnabled() {
        return this.visitedEnabled;
    }

    public void setVisitedEnabled(boolean newValue) {
        boolean z = this.visitedEnabled;
        this.visitedEnabled = newValue;
        firePropertyChange(PROPERTY_VISITED_ENABLED, z, newValue);
    }

    public Color getVisitedForeground() {
        return this.visitedForeground;
    }

    public void setVisitedForeground(Color newValue) {
        Color oldValue = getVisitedForeground();
        this.visitedForeground = newValue;
        firePropertyChange(PROPERTY_VISITED_FOREGROUND, oldValue, newValue);
        repaint();
    }

    protected void fireActionPerformed(ActionEvent event) {
        boolean oldRollover = getModel().isRollover();
        getModel().setRollover(false);
        setVisited(true);
        super.fireActionPerformed(event);
        getModel().setRollover(oldRollover && isShowing());
    }

    public void setMnemonic(int mnemonic) {
    }

    public void setDisplayedMnemonicIndex(int index) {
    }

    public void updateUI() {
        ComponentSetup.ensureSetup();
        setUI((ButtonUI) UIManager.getUI(this));
    }

    public String getUIClassID() {
        return UI_CLASS_ID;
    }
}
