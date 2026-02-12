package com.jgoodies.components;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSplitPane.class */
public class JGSplitPane extends JSplitPane {
    public static final String PROPERTY_DIVIDER_BORDER_VISIBLE = "dividerBorderVisible";
    private boolean dividerBorderVisible;

    public JGSplitPane() {
        this(1, false, new JButton(UIManager.getString("SplitPane.leftButtonText")), new JButton(UIManager.getString("SplitPane.rightButtonText")));
    }

    public JGSplitPane(int newOrientation) {
        this(newOrientation, false);
    }

    public JGSplitPane(int newOrientation, boolean newContinuousLayout) {
        this(newOrientation, newContinuousLayout, null, null);
    }

    public JGSplitPane(int orientation, Component leftComponent, Component rightComponent) {
        this(orientation, false, leftComponent, rightComponent);
    }

    public JGSplitPane(int orientation, boolean continuousLayout, Component leftComponent, Component rightComponent) {
        super(orientation, continuousLayout, leftComponent, rightComponent);
        this.dividerBorderVisible = false;
    }

    public boolean isDividerBorderVisible() {
        return this.dividerBorderVisible;
    }

    public void setDividerBorderVisible(boolean newVisibility) {
        boolean oldVisibility = isDividerBorderVisible();
        if (oldVisibility == newVisibility) {
            return;
        }
        this.dividerBorderVisible = newVisibility;
        firePropertyChange(PROPERTY_DIVIDER_BORDER_VISIBLE, oldVisibility, newVisibility);
    }

    public void updateUI() {
        super.updateUI();
        if (!isDividerBorderVisible()) {
            setEmptyDividerBorder();
        }
    }

    private void setEmptyDividerBorder() {
        BasicSplitPaneUI ui = getUI();
        if (ui instanceof BasicSplitPaneUI) {
            BasicSplitPaneUI basicUI = ui;
            basicUI.getDivider().setBorder(BorderFactory.createEmptyBorder());
        }
    }
}
