package com.jgoodies.components;

import com.jgoodies.components.internal.ActionConfigurationSupport;
import com.jgoodies.components.util.Mode;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGCheckBoxMenuItem.class */
public class JGCheckBoxMenuItem extends JCheckBoxMenuItem {
    private boolean iconVisible;
    private Icon backedIcon;
    private Icon backedSelectedIcon;
    private Icon backedDisabledIcon;
    private Icon backedDisabledSelectedIcon;
    private Icon backedPressedIcon;
    private Icon backedRollOverIcon;
    private Icon backedRollOverSelectedIcon;
    private Mode iconVisibleMode;

    public JGCheckBoxMenuItem() {
        this((String) null, (Icon) null, false);
    }

    public JGCheckBoxMenuItem(Icon icon) {
        this((String) null, icon, false);
    }

    public JGCheckBoxMenuItem(String text) {
        this(text, (Icon) null, false);
    }

    public JGCheckBoxMenuItem(String text, boolean selected) {
        this(text, (Icon) null, selected);
    }

    public JGCheckBoxMenuItem(Icon icon, boolean selected) {
        this((String) null, icon, selected);
    }

    public JGCheckBoxMenuItem(Action action) {
        super(action);
        this.iconVisible = true;
    }

    public JGCheckBoxMenuItem(String text, Icon icon) {
        this(text, icon, false);
    }

    public JGCheckBoxMenuItem(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        this.iconVisible = true;
    }

    public JGCheckBoxMenuItem(String text, int mnemonic) {
        this(text, (Icon) null, false);
        setMnemonic(mnemonic);
    }

    public JGCheckBoxMenuItem(String text, int mnemonic, boolean selected) {
        this(text, (Icon) null, selected);
        setMnemonic(mnemonic);
    }

    public JGCheckBoxMenuItem(String text, Icon icon, int mnemonic) {
        this(text, icon, false);
        setMnemonic(mnemonic);
    }

    public JGCheckBoxMenuItem(String text, Icon icon, int mnemonic, boolean selected) {
        this(text, icon, selected);
        setMnemonic(mnemonic);
    }

    public boolean isIconVisible() {
        return this.iconVisible;
    }

    public void setIconVisible(boolean newVisible) {
        boolean oldVisible = isIconVisible();
        if (oldVisible == newVisible) {
            return;
        }
        this.iconVisible = newVisible;
        if (newVisible) {
            setIcon(this.backedIcon);
            setSelectedIcon(this.backedSelectedIcon);
            setDisabledIcon(this.backedDisabledIcon);
            setDisabledSelectedIcon(this.backedDisabledSelectedIcon);
            setPressedIcon(this.backedPressedIcon);
            setRolloverIcon(this.backedRollOverIcon);
            setRolloverSelectedIcon(this.backedRollOverSelectedIcon);
        } else {
            this.backedIcon = getIcon();
            this.backedSelectedIcon = getSelectedIcon();
            this.backedDisabledIcon = getDisabledIcon();
            this.backedDisabledSelectedIcon = getDisabledSelectedIcon();
            this.backedPressedIcon = getPressedIcon();
            this.backedRollOverIcon = getRolloverIcon();
            this.backedRollOverSelectedIcon = getRolloverSelectedIcon();
            setIcon(null);
            setSelectedIcon(null);
            setDisabledIcon(null);
            setDisabledSelectedIcon(null);
            setPressedIcon(null);
            setRolloverIcon(null);
            setRolloverSelectedIcon(null);
        }
        firePropertyChange("iconVisible", oldVisible, newVisible);
    }

    public Mode getIconVisibleMode() {
        return this.iconVisibleMode;
    }

    public void setIconVisibleMode(Mode newMode) {
        Mode oldMode = getIconVisibleMode();
        this.iconVisibleMode = newMode;
        updateIconVisibility();
        firePropertyChange("iconVisibleMode", oldMode, newMode);
    }

    protected void configurePropertiesFromAction(Action action) {
        super.configurePropertiesFromAction(action);
        ActionConfigurationSupport.configureAccessiblePropertiesFromAction(this, action);
        updateIconVisibility();
    }

    protected void actionPropertyChanged(Action action, String propertyName) {
        super.actionPropertyChanged(action, propertyName);
        if (propertyName == "SwingLargeIconKey" || propertyName == "SmallIcon") {
            updateIconVisibility();
        }
    }

    public void updateUI() {
        super.updateUI();
        updateIconVisibility();
    }

    private void updateIconVisibility() {
        if (getIconVisibleMode() != null) {
            setIconVisible(getIconVisibleMode().enabled());
        }
    }
}
