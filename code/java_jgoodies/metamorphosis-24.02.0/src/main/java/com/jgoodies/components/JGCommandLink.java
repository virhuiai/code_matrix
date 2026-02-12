package com.jgoodies.components;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.components.plaf.ComponentSetup;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGCommandLink.class */
public final class JGCommandLink extends JGButton {
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_ICON_VISIBLE = "iconVisible";
    public static final String PROPERTY_FORCE_STANDARD_ICON = "forceStandardIcon";
    private static final String UI_CLASS_ID = "JSDL.CommandLinkUI";
    private String description;
    private boolean iconVisible;
    private boolean forceStandardIcon;

    public JGCommandLink() {
        this((Icon) null, "Mandatory Text", "This optional text describes this command.");
    }

    public JGCommandLink(String text) {
        this(null, text, null, true);
    }

    public JGCommandLink(String text, boolean iconVisible) {
        this(null, text, null, iconVisible);
    }

    public JGCommandLink(Icon icon, String text) {
        this(icon, text, null, true);
    }

    public JGCommandLink(String text, String description) {
        this(null, text, description, true);
    }

    public JGCommandLink(String text, String description, boolean iconVisible) {
        this(null, text, description, iconVisible);
    }

    public JGCommandLink(Icon icon, String text, String description) {
        this(icon, text, description, true);
    }

    public JGCommandLink(Icon icon, String text, String description, boolean iconVisible) {
        this(icon, text, description, iconVisible, false);
    }

    public JGCommandLink(Icon icon, String text, String description, boolean iconVisible, boolean forceStandardIcon) {
        super((String) Preconditions.checkNotNull(text, Messages.MUST_NOT_BE_NULL, "text"), icon);
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setDescription(description);
        setIconVisible(iconVisible);
        setForceStandardIcon(forceStandardIcon);
    }

    public JGCommandLink(Action action) {
        this(action, true);
    }

    public JGCommandLink(Action action, boolean iconVisible) {
        this(action, iconVisible, false);
    }

    public JGCommandLink(Action action, boolean iconVisible, boolean forceStandardIcon) {
        super((Action) Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action"));
        setContentAreaFilled(false);
        setRolloverEnabled(true);
        setIconVisible(iconVisible);
        setForceStandardIcon(forceStandardIcon);
    }

    public void setText(String newText) {
        Preconditions.checkNotBlank(newText, Messages.MUST_NOT_BE_BLANK, "CommandLink text");
        super.setText(newText);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String newDescription) {
        if (Strings.isNotEmpty(newDescription) && Strings.isBlank(newDescription)) {
            throw new IllegalArgumentException("The CommandLink description must not be whitespace.");
        }
        String oldText = getDescription();
        this.description = newDescription;
        firePropertyChange("description", oldText, newDescription);
    }

    public boolean isIconVisible() {
        return this.iconVisible;
    }

    public void setIconVisible(boolean newValue) {
        boolean oldValue = isIconVisible();
        this.iconVisible = newValue;
        firePropertyChange("iconVisible", oldValue, newValue);
    }

    public boolean getForceStandardIcon() {
        return this.forceStandardIcon;
    }

    public void setForceStandardIcon(boolean newValue) {
        boolean oldValue = getForceStandardIcon();
        this.forceStandardIcon = newValue;
        firePropertyChange(PROPERTY_FORCE_STANDARD_ICON, oldValue, newValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.JGButton
    public void configurePropertiesFromAction(Action action) {
        super.configurePropertiesFromAction(action);
        String longDescription = action == null ? null : (String) action.getValue("LongDescription");
        setDescription(longDescription);
    }

    public void updateUI() {
        ComponentSetup.ensureSetup();
        super.updateUI();
    }

    public String getUIClassID() {
        return UI_CLASS_ID;
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleCommandLink();
        }
        return this.accessibleContext;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGCommandLink$AccessibleCommandLink.class */
    protected final class AccessibleCommandLink extends AccessibleJButton {
        protected AccessibleCommandLink() {
            super(JGCommandLink.this);
        }

        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            String text = JGCommandLink.this.getText();
            StringBuilder builder = new StringBuilder();
            String prefix = text == null ? super.getAccessibleName() : text;
            builder.append(prefix);
            builder.append(": ");
            builder.append(JGCommandLink.this.getDescription());
            return builder.toString();
        }
    }
}
