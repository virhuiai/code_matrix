package com.jgoodies.common.jsdl.action;

import com.jgoodies.common.swing.MnemonicUtils;
import javax.swing.AbstractAction;
import javax.swing.Action;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/action/AbstractMnemonicAction.class */
public abstract class AbstractMnemonicAction extends AbstractAction {
    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMnemonicAction(String markedText) {
        super(markedText);
        MnemonicUtils.configure((Action) this, markedText);
    }

    public void setName(String markedText) {
        MnemonicUtils.configure((Action) this, markedText);
    }
}
