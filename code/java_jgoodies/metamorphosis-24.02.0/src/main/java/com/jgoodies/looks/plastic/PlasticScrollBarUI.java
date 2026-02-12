package com.jgoodies.looks.plastic;

import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticScrollBarUI.class */
public final class PlasticScrollBarUI extends MetalScrollBarUI {
    private static final String PROPERTY_PREFIX = "ScrollBar.";
    public static final String MAX_BUMPS_WIDTH_KEY = "ScrollBar.maxBumpsWidth";
    private Color shadowColor;
    private Color highlightColor;
    private Color darkShadowColor;
    private Color thumbColor;
    private Color thumbShadow;
    private Color thumbHighlightColor;
    private PlasticBumps bumps;

    public static ComponentUI createUI(JComponent b) {
        return new PlasticScrollBarUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        int bumpSize = ScreenScaling.toPhysical(10);
        this.bumps = new PlasticBumps(bumpSize, bumpSize, this.thumbHighlightColor, this.thumbShadow, this.thumbColor);
    }

    protected JButton createDecreaseButton(int orientation) {
        this.decreaseButton = new PlasticArrowButton(orientation, this.scrollBarWidth, this.isFreeStanding);
        return this.decreaseButton;
    }

    protected JButton createIncreaseButton(int orientation) {
        this.increaseButton = new PlasticArrowButton(orientation, this.scrollBarWidth, this.isFreeStanding);
        return this.increaseButton;
    }

    protected void configureScrollBarColors() {
        super.configureScrollBarColors();
        this.shadowColor = UIManager.getColor("ScrollBar.shadow");
        this.highlightColor = UIManager.getColor("ScrollBar.highlight");
        this.darkShadowColor = UIManager.getColor("ScrollBar.darkShadow");
        this.thumbColor = UIManager.getColor("ScrollBar.thumb");
        this.thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");
        this.thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.translate(trackBounds.x, trackBounds.y);
        boolean leftToRight = PlasticUtils.isLeftToRight(c);
        if (this.scrollbar.getOrientation() == 1) {
            if (!this.isFreeStanding) {
                if (!leftToRight) {
                    trackBounds.width++;
                    g.translate(-1, 0);
                } else {
                    trackBounds.width += 2;
                }
            }
            if (c.isEnabled()) {
                g.setColor(this.darkShadowColor);
                g.drawLine(0, 0, 0, trackBounds.height - 1);
                g.drawLine(trackBounds.width - 2, 0, trackBounds.width - 2, trackBounds.height - 1);
                g.drawLine(1, trackBounds.height - 1, trackBounds.width - 1, trackBounds.height - 1);
                g.drawLine(1, 0, trackBounds.width - 2, 0);
                g.setColor(this.shadowColor);
                g.drawLine(1, 1, 1, trackBounds.height - 2);
                g.drawLine(1, 1, trackBounds.width - 3, 1);
                if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
                    int y = (this.thumbRect.y + this.thumbRect.height) - trackBounds.y;
                    g.drawLine(1, y, trackBounds.width - 1, y);
                }
                g.setColor(this.highlightColor);
                g.drawLine(trackBounds.width - 1, 0, trackBounds.width - 1, trackBounds.height - 1);
            } else {
                PlasticUtils.drawDisabledBorder(g, 0, 0, trackBounds.width, trackBounds.height);
            }
            if (!this.isFreeStanding) {
                if (!leftToRight) {
                    trackBounds.width--;
                    g.translate(1, 0);
                } else {
                    trackBounds.width -= 2;
                }
            }
        } else {
            if (!this.isFreeStanding) {
                trackBounds.height += 2;
            }
            if (c.isEnabled()) {
                g.setColor(this.darkShadowColor);
                g.drawLine(0, 0, trackBounds.width - 1, 0);
                g.drawLine(0, 1, 0, trackBounds.height - 2);
                g.drawLine(0, trackBounds.height - 2, trackBounds.width - 1, trackBounds.height - 2);
                g.drawLine(trackBounds.width - 1, 1, trackBounds.width - 1, trackBounds.height - 1);
                g.setColor(this.shadowColor);
                g.drawLine(1, 1, trackBounds.width - 2, 1);
                g.drawLine(1, 1, 1, trackBounds.height - 3);
                g.drawLine(0, trackBounds.height - 1, trackBounds.width - 1, trackBounds.height - 1);
                if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
                    int x = (this.thumbRect.x + this.thumbRect.width) - trackBounds.x;
                    g.drawLine(x, 1, x, trackBounds.height - 1);
                }
            } else {
                PlasticUtils.drawDisabledBorder(g, 0, 0, trackBounds.width, trackBounds.height);
            }
            if (!this.isFreeStanding) {
                trackBounds.height -= 2;
            }
        }
        g.translate(-trackBounds.x, -trackBounds.y);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!c.isEnabled()) {
            return;
        }
        boolean leftToRight = PlasticUtils.isLeftToRight(c);
        g.translate(thumbBounds.x, thumbBounds.y);
        if (this.scrollbar.getOrientation() == 1) {
            if (!this.isFreeStanding) {
                if (!leftToRight) {
                    thumbBounds.width++;
                    g.translate(-1, 0);
                } else {
                    thumbBounds.width += 2;
                }
            }
            g.setColor(this.thumbColor);
            g.fillRect(0, 0, thumbBounds.width - 2, thumbBounds.height - 1);
            g.setColor(this.thumbShadow);
            g.drawRect(0, 0, thumbBounds.width - 2, thumbBounds.height - 1);
            g.setColor(this.thumbHighlightColor);
            g.drawLine(1, 1, thumbBounds.width - 3, 1);
            g.drawLine(1, 1, 1, thumbBounds.height - 2);
            paintBumps(g, c, 3, 4, thumbBounds.width - 6, thumbBounds.height - 7);
            if (!this.isFreeStanding) {
                if (!leftToRight) {
                    thumbBounds.width--;
                    g.translate(1, 0);
                } else {
                    thumbBounds.width -= 2;
                }
            }
        } else {
            if (!this.isFreeStanding) {
                thumbBounds.height += 2;
            }
            g.setColor(this.thumbColor);
            g.fillRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 2);
            g.setColor(this.thumbShadow);
            g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 2);
            g.setColor(this.thumbHighlightColor);
            g.drawLine(1, 1, thumbBounds.width - 2, 1);
            g.drawLine(1, 1, 1, thumbBounds.height - 3);
            paintBumps(g, c, 4, 3, thumbBounds.width - 7, thumbBounds.height - 6);
            if (!this.isFreeStanding) {
                thumbBounds.height -= 2;
            }
        }
        g.translate(-thumbBounds.x, -thumbBounds.y);
        if (PlasticUtils.is3D(PROPERTY_PREFIX)) {
            paintThumb3D(g, thumbBounds);
        }
    }

    private void paintBumps(Graphics g, JComponent c, int x, int y, int width, int height) {
        if (!useNarrowBumps()) {
            this.bumps.setBumpArea(width, height);
            this.bumps.paintIcon(c, g, x, y);
            return;
        }
        int maxWidth = UIManager.getInt(MAX_BUMPS_WIDTH_KEY);
        int myWidth = Math.min(maxWidth, width);
        int myHeight = Math.min(maxWidth, height);
        int myX = x + ((width - myWidth) / 2);
        int myY = y + ((height - myHeight) / 2);
        this.bumps.setBumpArea(myWidth, myHeight);
        this.bumps.paintIcon(c, g, myX, myY);
    }

    private void paintThumb3D(Graphics g, Rectangle thumbBounds) {
        boolean isHorizontal = this.scrollbar.getOrientation() == 0;
        int width = thumbBounds.width - (isHorizontal ? 3 : 1);
        int height = thumbBounds.height - (isHorizontal ? 1 : 3);
        Rectangle r = new Rectangle(thumbBounds.x + 2, thumbBounds.y + 2, width, height);
        PlasticUtils.addLight3DEffekt(g, r, isHorizontal);
    }

    private static boolean useNarrowBumps() {
        return UIManager.get(MAX_BUMPS_WIDTH_KEY) instanceof Integer;
    }
}
