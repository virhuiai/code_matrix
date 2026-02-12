package com.jgoodies.components.internal;

import com.jgoodies.application.Application;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.jsdl.internal.ScaledIconAccess;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.ImageObserver;
import javax.swing.Icon;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/TextFieldIcons.class */
public final class TextFieldIcons {
    private static ResourceMap resources;
    private static String resourcePrefix;
    private static String lookAndFeelName;

    private TextFieldIcons() {
    }

    public static Icon getPopupIcon() {
        if (SystemUtils.IS_OS_MAC) {
            return new PopupIcon();
        }
        ensureValidImages();
        return ScaledIconAccess.getIcon(getResources(), getResourcePrefix() + ".popup.image");
    }

    public static Icon getSearchIcon() {
        ensureValidImages();
        return ScaledIconAccess.getIcon(getResources(), getResourcePrefix() + ".search.image");
    }

    public static Icon getSearchIconSmall() {
        ensureValidImages();
        return ScaledIconAccess.getIcon(getResources(), getResourcePrefix() + ".search.image.small");
    }

    public static Icon getClearIcon() {
        ensureValidImages();
        return ScaledIconAccess.getIcon(getResources(), getResourcePrefix() + ".clear.image");
    }

    public static Icon getEllipsisIcon() {
        ensureValidImages();
        return ScaledIconAccess.getIcon(getResources(), getResourcePrefix() + ".ellipsis.image");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Icon getButtonIcon(int width, int height) {
        Image buttonImage = getResources().getImage(getResourcePrefix() + ".button.image");
        return new ScalingIcon(buttonImage, getButtonInsets(), width, height);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Icon getButtonIconRollover(int width, int height) {
        Image buttonImageRollover = getResources().getImage(getResourcePrefix() + ".button.image.rollover");
        return new ScalingIcon(buttonImageRollover, getButtonInsets(), width, height);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Icon getButtonIconPressed(int width, int height) {
        Image buttonImagePressed = getResources().getImage(getResourcePrefix() + ".button.image.pressed");
        return new ScalingIcon(buttonImagePressed, getButtonInsets(), width, height);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void ensureValidImages() {
        String currentLookAndFeelName = UIManager.getLookAndFeel().getName();
        if (!currentLookAndFeelName.equals(lookAndFeelName)) {
            resources = null;
            resourcePrefix = null;
            lookAndFeelName = currentLookAndFeelName;
        }
    }

    private static ResourceMap getResources() {
        if (resources == null) {
            resources = Application.getResourceMap(TextFieldIcons.class);
        }
        return resources;
    }

    private static String getResourcePrefix() {
        if (resourcePrefix == null) {
            resourcePrefix = computeResourcePrefix();
        }
        return resourcePrefix;
    }

    private static String computeResourcePrefix() {
        return "metro";
    }

    private static Insets getButtonInsets() {
        return getResources().getInsets(getResourcePrefix() + ".button.insets");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/TextFieldIcons$ScalingIcon.class */
    public static final class ScalingIcon implements Icon {
        private final Image image;
        private final Insets insets;
        private final int w;
        private final int h;
        private final int width;
        private final int height;

        ScalingIcon(Image image, Insets insets, int width, int height) {
            this.image = image;
            this.insets = insets;
            this.width = image.getWidth((ImageObserver) null);
            this.height = image.getHeight((ImageObserver) null);
            this.w = width;
            this.h = height;
        }

        public int getIconWidth() {
            return this.w;
        }

        public int getIconHeight() {
            return this.h;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            int t = this.insets.top;
            int l = this.insets.left;
            int b = this.insets.bottom;
            int r = this.insets.right;
            g.translate(x, y);
            g.drawImage(this.image, 0, 0, l, t, 0, 0, l, t, (ImageObserver) null);
            g.drawImage(this.image, l, 0, this.w - r, t, l, 0, this.width - r, t, (ImageObserver) null);
            g.drawImage(this.image, this.w - r, 0, this.w, t, this.width - r, 0, this.width, t, (ImageObserver) null);
            g.drawImage(this.image, 0, t, l, this.h - b, 0, t, l, this.height - b, (ImageObserver) null);
            g.drawImage(this.image, l, this.h - b, this.w - r, this.h, l, this.height - b, this.width - r, this.height, (ImageObserver) null);
            g.drawImage(this.image, 0, this.h - b, l, this.h, 0, this.height - b, l, this.height, (ImageObserver) null);
            g.drawImage(this.image, this.w - r, this.h - b, this.w, this.h, this.width - r, this.height - b, this.width, this.height, (ImageObserver) null);
            g.drawImage(this.image, this.w - r, t, this.w, this.h - b, this.width - r, t, this.width, this.height - b, (ImageObserver) null);
            g.drawImage(this.image, l, t, this.w - r, this.h - b, l, t, this.width - r, this.height - b, (ImageObserver) null);
            g.translate(-x, -y);
        }
    }
}
