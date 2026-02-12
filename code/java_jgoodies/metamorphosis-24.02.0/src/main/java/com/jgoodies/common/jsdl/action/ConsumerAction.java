package com.jgoodies.common.jsdl.action;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.Action;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/ConsumerAction.class */
public final class ConsumerAction extends AbstractMnemonicAction {
    private final Consumer<ActionEvent> handler;

    public ConsumerAction(String markedText, Consumer<ActionEvent> handler) {
        super(markedText);
        this.handler = (Consumer) Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "handler");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConsumerAction(Consumer<ActionEvent> handler) {
        super("");
        this.handler = (Consumer) Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "handler");
    }

    public static Action noOp(String markedText) {
        return new ConsumerAction(markedText, evt -> {
        });
    }

    public void actionPerformed(ActionEvent evt) {
        this.handler.accept(evt);
    }
}
