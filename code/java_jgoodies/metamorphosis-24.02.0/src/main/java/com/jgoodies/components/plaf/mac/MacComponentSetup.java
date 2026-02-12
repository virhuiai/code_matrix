package com.jgoodies.components.plaf.mac;

import com.jgoodies.components.plaf.basic.BasicComponentSetup;
import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/mac/MacComponentSetup.class */
public class MacComponentSetup extends BasicComponentSetup {
    private static final Color COMMAND_LINK_MAC_ACTIVE_BLUE = new ColorUIResource(0, 120, 215);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.plaf.basic.BasicComponentSetup
    public void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        Object commandLinkIcon = getIcon("icons/commandlink-glyph_mac.png", null);
        Object commandLinkDefaultIcon = getIcon("icons/commandlink-glyph_mac.png", null);
        Object[] uiDefaults = {"CommandLink.icon", commandLinkIcon, "CommandLink.defaultIcon", commandLinkDefaultIcon, "CommandLink.activeForeground", COMMAND_LINK_MAC_ACTIVE_BLUE};
        table.putDefaults(uiDefaults);
    }
}
