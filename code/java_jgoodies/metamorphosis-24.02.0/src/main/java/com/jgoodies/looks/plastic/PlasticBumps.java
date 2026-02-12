package com.jgoodies.looks.plastic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBumps.class */
final class PlasticBumps implements Icon {
    private static final List<BumpBuffer> BUFFERS = new ArrayList();
    private int xBumps;
    private int yBumps;
    private Color topColor;
    private Color shadowColor;
    private Color backColor;
    private BumpBuffer buffer;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlasticBumps(int width, int height, Color newTopColor, Color newShadowColor, Color newBackColor) {
        setBumpArea(width, height);
        setBumpColors(newTopColor, newShadowColor, newBackColor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBumpArea(int width, int height) {
        this.xBumps = width / 2;
        this.yBumps = height / 2;
    }

    void setBumpColors(Color newTopColor, Color newShadowColor, Color newBackColor) {
        this.topColor = newTopColor;
        this.shadowColor = newShadowColor;
        this.backColor = newBackColor;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        GraphicsConfiguration gc = g instanceof Graphics2D ? ((Graphics2D) g).getDeviceConfiguration() : null;
        this.buffer = getBuffer(gc, this.topColor, this.shadowColor, this.backColor);
        int bufferWidth = BumpBuffer.getImageSize().width;
        int bufferHeight = BumpBuffer.getImageSize().height;
        int iconWidth = getIconWidth();
        int iconHeight = getIconHeight();
        int x2 = x + iconWidth;
        int y2 = y + iconHeight;
        while (y < y2) {
            int h = Math.min(y2 - y, bufferHeight);
            int i = x;
            while (true) {
                int x3 = i;
                if (x3 < x2) {
                    int w = Math.min(x2 - x3, bufferWidth);
                    g.drawImage(this.buffer.getImage(), x3, y, x3 + w, y + h, 0, 0, w, h, (ImageObserver) null);
                    i = x3 + bufferWidth;
                }
            }
            y += bufferHeight;
        }
    }

    public int getIconWidth() {
        return this.xBumps * 2;
    }

    public int getIconHeight() {
        return this.yBumps * 2;
    }

    private BumpBuffer getBuffer(GraphicsConfiguration gc, Color aTopColor, Color aShadowColor, Color aBackColor) {
        if (this.buffer != null && this.buffer.hasSameConfiguration(gc, aTopColor, aShadowColor, aBackColor)) {
            return this.buffer;
        }
        BumpBuffer result = null;
        Iterator<BumpBuffer> it = BUFFERS.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object element = it.next();
            BumpBuffer aBuffer = (BumpBuffer) element;
            if (aBuffer.hasSameConfiguration(gc, aTopColor, aShadowColor, aBackColor)) {
                result = aBuffer;
                break;
            }
        }
        if (result == null) {
            result = new BumpBuffer(gc, this.topColor, this.shadowColor, this.backColor);
            BUFFERS.add(result);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticBumps$BumpBuffer.class */
    public static final class BumpBuffer {
        private static final int IMAGE_SIZE = 64;
        private static Dimension imageSize = new Dimension(IMAGE_SIZE, IMAGE_SIZE);
        transient Image image;
        private final Color topColor;
        private final Color shadowColor;
        private final Color backColor;
        private final GraphicsConfiguration gc;

        BumpBuffer(GraphicsConfiguration gc, Color aTopColor, Color aShadowColor, Color aBackColor) {
            this.gc = gc;
            this.topColor = aTopColor;
            this.shadowColor = aShadowColor;
            this.backColor = aBackColor;
            createImage();
            fillBumpBuffer();
        }

        boolean hasSameConfiguration(GraphicsConfiguration aGC, Color aTopColor, Color aShadowColor, Color aBackColor) {
            return Objects.equals(this.gc, aGC) && this.topColor.equals(aTopColor) && this.shadowColor.equals(aShadowColor) && this.backColor.equals(aBackColor);
        }

        Image getImage() {
            return this.image;
        }

        static Dimension getImageSize() {
            return imageSize;
        }

        private void fillBumpBuffer() {
            Graphics g = this.image.getGraphics();
            g.setColor(this.backColor);
            g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
            g.setColor(this.topColor);
            for (int x = 0; x < IMAGE_SIZE; x += 4) {
                for (int y = 0; y < IMAGE_SIZE; y += 4) {
                    g.drawLine(x, y, x, y);
                    g.drawLine(x + 2, y + 2, x + 2, y + 2);
                }
            }
            g.setColor(this.shadowColor);
            for (int x2 = 0; x2 < IMAGE_SIZE; x2 += 4) {
                for (int y2 = 0; y2 < IMAGE_SIZE; y2 += 4) {
                    g.drawLine(x2 + 1, y2 + 1, x2 + 1, y2 + 1);
                    g.drawLine(x2 + 3, y2 + 3, x2 + 3, y2 + 3);
                }
            }
            g.dispose();
        }

        private void createImage() {
            if (this.gc != null) {
                this.image = this.gc.createCompatibleImage(IMAGE_SIZE, IMAGE_SIZE);
                return;
            }
            int[] cmap = {this.backColor.getRGB(), this.topColor.getRGB(), this.shadowColor.getRGB()};
            IndexColorModel icm = new IndexColorModel(8, 3, cmap, 0, false, -1, 0);
            this.image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, 13, icm);
        }
    }
}
