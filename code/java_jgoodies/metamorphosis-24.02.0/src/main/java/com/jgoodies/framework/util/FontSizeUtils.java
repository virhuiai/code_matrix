package com.jgoodies.framework.util;

import com.jgoodies.common.base.Preconditions;
import java.awt.Font;
import java.util.Enumeration;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/FontSizeUtils.class */
public final class FontSizeUtils {
    private FontSizeUtils() {
    }

    public static void installLargerFonts(float sizeFactor) {
        Font font;
        Preconditions.checkArgument(sizeFactor > 0.0f, "The size factor must be positive.");
        if (sizeFactor == 1.0f) {
            return;
        }
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<?> keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);
            if ((value instanceof Font) && (font = UIManager.getFont(key)) != null) {
                float size = font.getSize2D();
                FontUIResource f = new FontUIResource(font.deriveFont(size * sizeFactor));
                UIManager.put(key, f);
            }
        }
        int rowHeight = defaults.getInt("Tree.rowHeight");
        if (rowHeight != 0) {
            int scaledRowHeight = (int) (rowHeight * sizeFactor);
            UIManager.put("Tree.rowHeight", Integer.valueOf(scaledRowHeight));
        }
    }
}
