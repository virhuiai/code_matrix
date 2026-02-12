package com.jgoodies.looks.common;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicSpinnerLayout.class */
public final class ExtBasicSpinnerLayout implements LayoutManager {
    private static final Dimension ZERO_SIZE = new Dimension(0, 0);
    private Component nextButton = null;
    private Component previousButton = null;
    private Component editor = null;

    public void addLayoutComponent(String name, Component c) {
        boolean z = -1;
        switch (name.hashCode()) {
            case -1209131241:
                if (name.equals("Previous")) {
                    z = true;
                    break;
                }
                break;
            case 2424595:
                if (name.equals("Next")) {
                    z = false;
                    break;
                }
                break;
            case 2071006605:
                if (name.equals("Editor")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case AnimatedLabel.CENTER /* 0 */:
                this.nextButton = c;
                return;
            case true:
                this.previousButton = c;
                return;
            case AnimatedLabel.LEFT /* 2 */:
                this.editor = c;
                return;
            default:
                return;
        }
    }

    public void removeLayoutComponent(Component c) {
        if (c != this.nextButton) {
            if (c == this.previousButton) {
                this.previousButton = null;
            } else if (c == this.editor) {
                this.editor = null;
            }
        }
    }

    private static Dimension preferredSize(Component c) {
        return c == null ? ZERO_SIZE : c.getPreferredSize();
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension nextD = preferredSize(this.nextButton);
        Dimension previousD = preferredSize(this.previousButton);
        Dimension editorD = preferredSize(this.editor);
        Dimension size = new Dimension(editorD.width, editorD.height);
        size.width += Math.max(nextD.width, previousD.width);
        Insets insets = parent.getInsets();
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
        return size;
    }

    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    private static void setBounds(Component c, int x, int y, int width, int height) {
        if (c != null) {
            c.setBounds(x, y, width, height);
        }
    }

    public void layoutContainer(Container parent) {
        int buttonsX;
        int editorX;
        int editorWidth;
        int width = parent.getWidth();
        int height = parent.getHeight();
        Insets insets = parent.getInsets();
        Dimension nextD = preferredSize(this.nextButton);
        Dimension previousD = preferredSize(this.previousButton);
        int buttonsWidth = Math.max(nextD.width, previousD.width);
        int editorHeight = height - (insets.top + insets.bottom);
        Insets buttonInsets = UIManager.getInsets("Spinner.arrowButtonInsets");
        if (buttonInsets == null) {
            buttonInsets = insets;
        }
        if (parent.getComponentOrientation().isLeftToRight()) {
            editorX = insets.left;
            editorWidth = ((width - insets.left) - buttonsWidth) - buttonInsets.right;
            buttonsX = (width - buttonsWidth) - buttonInsets.right;
        } else {
            buttonsX = buttonInsets.left;
            editorX = buttonsX + buttonsWidth;
            editorWidth = ((width - buttonInsets.left) - buttonsWidth) - insets.right;
        }
        int nextY = buttonInsets.top;
        int nextHeight = ((height / 2) + (height % 2)) - nextY;
        int previousY = buttonInsets.top + nextHeight;
        int previousHeight = (height - previousY) - buttonInsets.bottom;
        setBounds(this.editor, editorX, insets.top, editorWidth, editorHeight);
        setBounds(this.nextButton, buttonsX, nextY, buttonsWidth, nextHeight);
        setBounds(this.previousButton, buttonsX, previousY, buttonsWidth, previousHeight);
    }
}
