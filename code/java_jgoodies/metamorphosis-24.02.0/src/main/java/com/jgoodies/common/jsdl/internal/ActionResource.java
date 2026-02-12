package com.jgoodies.common.jsdl.internal;

import com.jgoodies.common.base.Strings;
import com.jgoodies.common.swing.internal.AcceleratorUtils;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/ActionResource.class */
public final class ActionResource {
    private String text;
    private KeyStroke accelerator;
    private String shortDescription;
    private String longDescription;
    private String accessibleName;
    private String accessibleDescription;
    private Icon icon;
    private Icon rolloverIcon;

    public String getText() {
        return this.text;
    }

    public KeyStroke getAccelerator() {
        return this.accelerator;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public String getLongDescription() {
        return this.longDescription;
    }

    public String getAccessibleName() {
        return this.accessibleName;
    }

    public String getAccessibleDescription() {
        return this.accessibleDescription;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public Icon getRolloverIcon() {
        return this.rolloverIcon;
    }

    public String getMandatoryToolTipTextWithAccelerator(Component c) {
        return getAccelerator() == null ? getToolTipText() : String.format("%s (%s)", getToolTipText(), AcceleratorUtils.getAcceleratorText(c, getAccelerator()));
    }

    private String getToolTipText() {
        return Strings.isNotBlank(this.shortDescription) ? this.shortDescription : this.text;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/ActionResource$Builder.class */
    public static final class Builder {
        private final ActionResource target = new ActionResource();

        public Builder text(String markedText, Object... args) {
            this.target.text = Strings.get(markedText, args);
            return this;
        }

        public Builder shortDescription(String description, Object... args) {
            this.target.shortDescription = Strings.get(description, args);
            return this;
        }

        public Builder longDescription(String description) {
            this.target.longDescription = description;
            return this;
        }

        public Builder accessibleName(String name) {
            this.target.accessibleName = name;
            return this;
        }

        public Builder accessibleDescription(String description) {
            this.target.accessibleDescription = description;
            return this;
        }

        public Builder accelerator(KeyStroke keyStroke) {
            this.target.accelerator = keyStroke;
            return this;
        }

        public Builder accelerator(String keyStroke) {
            this.target.accelerator = KeyStroke.getKeyStroke(keyStroke);
            return this;
        }

        public Builder icon(Icon icon) {
            this.target.icon = icon;
            return this;
        }

        public Builder rolloverIcon(Icon icon) {
            this.target.rolloverIcon = icon;
            return this;
        }

        public ActionResource build() {
            return this.target;
        }
    }
}
