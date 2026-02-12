package com.jgoodies.components.plaf.windows;

import com.jgoodies.components.internal.ComponentSupport;
import com.jgoodies.components.plaf.basic.BasicCommandLinkUI;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/windows/WindowsCommandLinkUI.class */
public class WindowsCommandLinkUI extends BasicCommandLinkUI {
    public static ComponentUI createUI(JComponent x) {
        return new WindowsCommandLinkUI();
    }

    @Override // com.jgoodies.components.plaf.basic.BasicCommandLinkUI
    protected boolean isMnemonicHidden() {
        return ComponentSupport.isMnemonicHidden();
    }
}
