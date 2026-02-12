package com.jgoodies.components.plaf.basic;

import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import com.jgoodies.common.jsdl.internal.ScaledIconAccess;
import com.jgoodies.common.jsdl.util.ColorUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/basic/BasicComponentSetup.class */
public class BasicComponentSetup {
    protected static final Color LIGHT_GRAY = new ColorUIResource(223, 223, 223);
    protected static final Color GRAY = new ColorUIResource(DelayedPropertyChangeHandler.DEFAULT_DELAY, DelayedPropertyChangeHandler.DEFAULT_DELAY, DelayedPropertyChangeHandler.DEFAULT_DELAY);
    protected static final Color LINK_BLUE = new ColorUIResource(0, 102, 204);
    protected static final Color VISITED_LINK_BLUE = new ColorUIResource(128, 0, 128);
    protected static final Color COMMAND_LINK_BLUE = new ColorUIResource(21, 28, 85);
    protected static final Color COMMAND_LINK_ACTIVE_BLUE = new ColorUIResource(7, 74, 229);
    protected static final Color TITLE_BLUE = new ColorUIResource(0, 43, 130);

    public void install(UIDefaults table) {
        initClassDefaults(table);
        initComponentDefaults(table);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initClassDefaults(UIDefaults table) {
        Object[] uiDefaults = {"JSDL.CommandLinkUI", "com.jgoodies.components.plaf.basic.BasicCommandLinkUI", "JSDL.HyperlinkUI", "com.jgoodies.components.plaf.basic.BasicHyperlinkUI"};
        table.putDefaults(uiDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initComponentDefaults(UIDefaults table) {
        Object commandLinkForeground;
        Object linkColor;
        Object visitedLinkColor;
        Object headerForeground;
        Object commandLinkIcon = getIcon(BasicComponentSetup.class, "icons/commandlink-glyph.png", null, new Insets(-4, 0, 0, 0));
        Object defaultForeground = table.getColor("controlText");
        Object commandLinkTextFont = getCommandLinkTextFont(table);
        Object commandLinkDescriptionFont = getCommandLinkDescriptionFont(table);
        Object headerFont = getHeaderFont(table);
        Color controlText = table.getColor("controlText");
        float brightness = ColorUtils.brightness(controlText);
        if (brightness <= 0.21d) {
            commandLinkForeground = COMMAND_LINK_BLUE;
            linkColor = LINK_BLUE;
            visitedLinkColor = VISITED_LINK_BLUE;
            headerForeground = TITLE_BLUE;
        } else {
            commandLinkForeground = defaultForeground;
            linkColor = GRAY;
            visitedLinkColor = LIGHT_GRAY;
            headerForeground = Color.DARK_GRAY;
        }
        Object[] uiDefaults = {"CommandLink.icon", commandLinkIcon, "CommandLink.defaultIcon", commandLinkIcon, "CommandLink.textFont", commandLinkTextFont, "CommandLink.descriptionFont", commandLinkDescriptionFont, "CommandLink.foreground", commandLinkForeground, "CommandLink.activeForeground", COMMAND_LINK_ACTIVE_BLUE, "Hyperlink.unvisited.foreground", linkColor, "Hyperlink.visited.foreground", visitedLinkColor, "Label.header.font", headerFont, "Label.header.foreground", headerForeground};
        table.putDefaults(uiDefaults);
    }

    protected Font getControlFont(UIDefaults table) {
        return table.getFont("Button.font");
    }

    protected FontUIResource getHeaderFont(UIDefaults table) {
        Font controlFont = getControlFont(table);
        float controlFontSize = controlFont.getSize2D();
        Font largerFont = controlFont.deriveFont(0, controlFontSize * 1.33344f);
        return new FontUIResource(largerFont);
    }

    protected FontUIResource getCommandLinkTextFont(UIDefaults table) {
        return getHeaderFont(table);
    }

    protected FontUIResource getCommandLinkDescriptionFont(UIDefaults table) {
        return new FontUIResource(getControlFont(table));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Icon getIcon(String filename, String description) {
        return getIcon(filename, description, null);
    }

    protected final Icon getIcon(String filename, String description, Insets insets) {
        return ScaledIconAccess.getIcon(getClass(), filename, description, insets);
    }

    private static Icon getIcon(Class<?> clazz, String filename, String description, Insets insets) {
        return ScaledIconAccess.getIcon(clazz, filename, description, insets);
    }
}
