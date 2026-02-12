package com.jgoodies.looks.common;

import com.jgoodies.looks.Options;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/RGBGrayFilter.class */
public final class RGBGrayFilter extends RGBImageFilter {
    private final float alphaFactor;

    private RGBGrayFilter() {
        this.canFilterIndexColorModel = true;
        this.alphaFactor = Options.getHiResGrayFilterAlphaFactor();
    }

    public static Icon getDisabledIcon(JComponent component, Icon icon) {
        Image img;
        if (icon == null || component == null || icon.getIconWidth() == 0 || icon.getIconHeight() == 0) {
            return null;
        }
        if (icon instanceof ImageIcon) {
            img = ((ImageIcon) icon).getImage();
        } else {
            img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), 2);
            icon.paintIcon(component, img.getGraphics(), 0, 0);
        }
        boolean standardFilter = !Options.isHiResGrayFilterEnabled() || Boolean.FALSE.equals(component.getClientProperty(Options.HI_RES_DISABLED_ICON_CLIENT_KEY));
        return new ImageIcon(component.createImage(new FilteredImageSource(img.getSource(), standardFilter ? new GrayFilter(true, 50) : new RGBGrayFilter())));
    }

    public int filterRGB(int x, int y, int rgb) {
        float alpha = ((rgb >> 24) & 255) / 255.0f;
        float alpha2 = alpha * this.alphaFactor;
        float avg = Math.min(1.0f, 0.35f + (0.65f * ((((((rgb >> 16) & 255) / 255.0f) + (((rgb >> 8) & 255) / 255.0f)) + ((rgb & 255) / 255.0f)) / 3.0f)));
        return (((int) (alpha2 * 255.0f)) << 24) | (((int) (avg * 255.0f)) << 16) | (((int) (avg * 255.0f)) << 8) | ((int) (avg * 255.0f));
    }
}
