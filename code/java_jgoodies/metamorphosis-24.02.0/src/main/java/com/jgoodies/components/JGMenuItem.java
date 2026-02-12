package com.jgoodies.components;

import com.jgoodies.components.internal.ActionConfigurationSupport;
import com.jgoodies.components.util.Mode;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGMenuItem.class */
public class JGMenuItem extends JMenuItem {
    private boolean iconVisible;
    private Icon backedIcon;
    private Icon backedSelectedIcon;
    private Icon backedDisabledIcon;
    private Icon backedDisabledSelectedIcon;
    private Icon backedPressedIcon;
    private Icon backedRollOverIcon;
    private Icon backedRollOverSelectedIcon;
    private Mode iconVisibleMode;

    public JGMenuItem() {
        this((String) null, (Icon) null);
    }

    public JGMenuItem(Icon icon) {
        this((String) null, icon);
    }

    public JGMenuItem(String text) {
        this(text, (Icon) null);
    }

    public JGMenuItem(Action action) {
        super(action);
        this.iconVisible = true;
    }

    public JGMenuItem(String text, Icon icon) {
        super(text, icon);
        this.iconVisible = true;
    }

    public JGMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
        this.iconVisible = true;
    }

    public JGMenuItem(String text, Icon icon, int mnemonic) {
        super(text, icon);
        this.iconVisible = true;
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
