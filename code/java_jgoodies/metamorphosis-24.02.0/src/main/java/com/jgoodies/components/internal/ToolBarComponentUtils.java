package com.jgoodies.components.internal;

import com.jgoodies.common.base.Strings;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.looks.Options;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ToolBarComponentUtils.class */
public final class ToolBarComponentUtils {
    private static final char ELLIPSIS = 8230;

    private ToolBarComponentUtils() {
    }

    public static void configureButton(AbstractButton button) {
        Insets margin;
        button.setHorizontalTextPosition(0);
        button.setVerticalTextPosition(3);
        button.setAlignmentY(0.5f);
        if ((button.getMargin() instanceof UIResource) && (margin = createToolBarMargin(button)) != null) {
            button.setMargin(margin);
        }
        button.setMnemonic(0);
        button.setFocusable(false);
        button.setHideActionText(button.getIcon() != null);
    }

    public static void setFallbackToolTipText(AbstractButton button, Action a) {
        String defaultTooltip = a == null ? null : (String) a.getValue("ShortDescription");
        if (Strings.isNotBlank(defaultTooltip)) {
            return;
        }
        String text = a == null ? null : (String) a.getValue("Name");
        if (Strings.isNotBlank(text)) {
            String fallbackTooltip = stripEllipsis(text);
            if (Strings.isNotBlank(fallbackTooltip)) {
                button.setToolTipText(fallbackTooltip);
            }
        }
    }

    public static int getPopupAreaWidthOffset(AbstractButton button) {
        if (SystemUtils.IS_OS_MAC) {
            return button.getIcon() == null ? 16 : 4;
        }
        return 0;
    }

    private static Insets createToolBarMargin(AbstractButton button) {
        Dimension defaultIconSize = UIManager.getDimension(Options.DEFAULT_ICON_SIZE_KEY);
        Icon icon = button.getIcon();
        if (defaultIconSize == null || icon == null) {
            return null;
        }
        int hpad = Math.max(0, defaultIconSize.width - icon.getIconWidth());
        int vpad = Math.max(0, defaultIconSize.height - icon.getIconHeight());
        int top = vpad / 2;
        int left = hpad / 2;
        int bottom = top + (vpad % 2);
        int right = left + (hpad % 2);
        return new InsetsUIResource(top, left, bottom, right);
    }

    private static String stripEllipsis(String withEllipsis) {
        int length = withEllipsis.length();
        if (withEllipsis.charAt(length - 1) == ELLIPSIS) {
            return withEllipsis.substring(0, length - 1);
        }
        if (withEllipsis.endsWith(Strings.NO_ELLIPSIS_STRING)) {
            return withEllipsis.substring(0, length - Strings.NO_ELLIPSIS_STRING.length());
        }
        return withEllipsis;
    }
}
