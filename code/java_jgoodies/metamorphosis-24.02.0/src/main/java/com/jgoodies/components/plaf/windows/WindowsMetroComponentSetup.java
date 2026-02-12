package com.jgoodies.components.plaf.windows;

import com.jgoodies.components.plaf.basic.BasicComponentSetup;
import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/windows/WindowsMetroComponentSetup.class */
public class WindowsMetroComponentSetup extends BasicComponentSetup {
    private static final Color COMMAND_LINK_METRO_ACTIVE_BLUE = new ColorUIResource(0, 120, 215);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.plaf.basic.BasicComponentSetup
    public void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {"JSDL.CommandLinkUI", "com.jgoodies.components.plaf.windows.WindowsCommandLinkUI"};
        table.putDefaults(uiDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.plaf.basic.BasicComponentSetup
    public void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        Object commandLinkIcon = getIcon("icons/metro/commandlink-glyph_win10.png", null);
        Object commandLinkDefaultIcon = getIcon("icons/metro/commandlink-glyph_win10.png", null);
        Object[] uiDefaults = {"CommandLink.icon", commandLinkIcon, "CommandLink.defaultIcon", commandLinkDefaultIcon, "CommandLink.activeForeground", COMMAND_LINK_METRO_ACTIVE_BLUE};
        table.putDefaults(uiDefaults);
    }
}
