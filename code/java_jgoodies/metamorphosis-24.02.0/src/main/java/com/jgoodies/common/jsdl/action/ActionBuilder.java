package com.jgoodies.common.jsdl.action;

import com.jgoodies.application.Actions;
import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.icon.IconValue;
import com.jgoodies.common.jsdl.internal.ActionResource;
import com.jgoodies.common.swing.MnemonicUtils;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/ActionBuilder.class */
public final class ActionBuilder {
    private Consumer<ActionEvent> handler;
    private String text;
    private KeyStroke accelerator;
    private String shortDescription;
    private String longDescription;
    private String accessibleName;
    private String accessibleDescription;
    private String command;
    private Icon smallIcon;
    private Icon largeIcon;
    private boolean enabled = true;

    public ActionBuilder() {
    }

    public ActionBuilder(ActionResource actionResource) {
        smallIcon(actionResource.getIcon());
        text(actionResource.getText(), new Object[0]);
        shortDescription(actionResource.getShortDescription(), new Object[0]);
        longDescription(actionResource.getLongDescription());
        accelerator(actionResource.getAccelerator());
    }

    public ActionBuilder handler(Consumer<ActionEvent> handler) {
        this.handler = (Consumer) Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "action handler");
        return this;
    }

    public ActionBuilder handler(Runnable handler) {
        Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "action handler");
        this.handler = evt -> {
            handler.run();
        };
        return this;
    }

    public ActionBuilder text(String markedText, Object... args) {
        this.text = Strings.get(markedText, args);
        return this;
    }

    public ActionBuilder shortDescription(String description, Object... args) {
        this.shortDescription = Strings.get(description, args);
        return this;
    }

    public ActionBuilder longDescription(String description) {
        this.longDescription = description;
        return this;
    }

    public ActionBuilder accessibleName(String name) {
        this.accessibleName = name;
        return this;
    }

    public ActionBuilder accessibleDescription(String description) {
        this.accessibleDescription = description;
        return this;
    }

    public ActionBuilder accelerator(KeyStroke keyStroke) {
        this.accelerator = keyStroke;
        return this;
    }

    public ActionBuilder accelerator(String keyStroke) {
        this.accelerator = KeyStroke.getKeyStroke(keyStroke);
        return this;
    }

    public ActionBuilder smallIcon(Icon icon) {
        this.smallIcon = icon;
        return this;
    }

    public ActionBuilder smallIcon(IconValue icon) {
        return smallIcon(icon.toIcon());
    }

    public ActionBuilder largeIcon(Icon icon) {
        this.largeIcon = icon;
        return this;
    }

    public ActionBuilder largeIcon(IconValue icon) {
        return largeIcon(icon.toIcon());
    }

    public ActionBuilder command(String command) {
        this.command = command;
        return this;
    }

    public ActionBuilder enabled(boolean b) {
        this.enabled = b;
        return this;
    }

    public ActionBuilder disabled() {
        this.enabled = false;
        return this;
    }

    public Action build() {
        if (this.handler == null) {
            throw new Exceptions.ActionConfigurationException("You must provide an action handler (call #handler).");
        }
        ConsumerAction consumerAction = new ConsumerAction(this.handler);
        if (Strings.isNotBlank(this.text)) {
            MnemonicUtils.configure((Action) consumerAction, this.text);
        } else if (this.smallIcon == null && this.largeIcon == null) {
            throw new Exceptions.ActionConfigurationException("You must provide either a text or an icon (call either #text, #smallIcon, or #largeIcon).");
        }
        consumerAction.putValue("SmallIcon", this.smallIcon);
        consumerAction.putValue("SwingLargeIconKey", this.largeIcon);
        consumerAction.putValue("AcceleratorKey", this.accelerator);
        consumerAction.putValue("ShortDescription", this.shortDescription);
        consumerAction.putValue("LongDescription", this.longDescription);
        consumerAction.putValue("ActionCommandKey", this.command);
        consumerAction.putValue(Actions.ACCESSIBLE_NAME_KEY, this.accessibleName);
        consumerAction.putValue(Actions.ACCESSIBLE_DESCRIPTION_KEY, this.accessibleDescription);
        consumerAction.setEnabled(this.enabled);
        return consumerAction;
    }
}
