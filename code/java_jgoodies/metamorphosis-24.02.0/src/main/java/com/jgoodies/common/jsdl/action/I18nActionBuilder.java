package com.jgoodies.common.jsdl.action;

import com.jgoodies.application.Actions;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.Action;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/I18nActionBuilder.class */
public final class I18nActionBuilder {
    private Consumer<ActionEvent> handler;
    private String id;
    private ResourceMap resources;
    private boolean enabled = true;

    public I18nActionBuilder handler(Consumer<ActionEvent> handler) {
        this.handler = (Consumer) Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "action handler");
        return this;
    }

    public I18nActionBuilder handler(Runnable handler) {
        return handler(evt -> {
            handler.run();
        });
    }

    public I18nActionBuilder id(String id) {
        this.id = (String) Preconditions.checkNotBlank(id, Messages.MUST_NOT_BE_BLANK, "action resource id");
        return this;
    }

    public I18nActionBuilder resources(ResourceMap resources) {
        this.resources = (ResourceMap) Preconditions.checkNotNull(resources, Messages.MUST_NOT_BE_NULL, "action resource map");
        return this;
    }

    public I18nActionBuilder enabled(boolean b) {
        this.enabled = b;
        return this;
    }

    public I18nActionBuilder disabled() {
        this.enabled = false;
        return this;
    }

    public Action build() {
        if (this.handler == null) {
            throw new Exceptions.ActionConfigurationException("You must provide an action handler (call #handler).");
        }
        if (this.resources == null) {
            throw new Exceptions.ActionConfigurationException("You must provide the resource map used to look up resource from (call #resources).");
        }
        if (this.id == null) {
            throw new Exceptions.ActionConfigurationException("You must provide the resource id used to look up resource from (call #id).");
        }
        ConsumerAction consumerAction = new ConsumerAction(this.handler);
        Actions.configure(consumerAction, this.resources, this.id);
        consumerAction.setEnabled(this.enabled);
        return consumerAction;
    }
}
