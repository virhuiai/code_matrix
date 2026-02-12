package com.jgoodies.looks.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsSplitPaneDivider.class */
final class WindowsSplitPaneDivider extends BasicSplitPaneDivider {
    private static final int EXT_ONE_TOUCH_SIZE = 5;
    private static final int EXT_ONE_TOUCH_OFFSET = 2;
    private static final int EXT_BLOCKSIZE = 6;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsSplitPaneDivider$ExtWindowsDividerLayout.class */
    public final class ExtWindowsDividerLayout implements LayoutManager {
        public ExtWindowsDividerLayout() {
        }

        public void layoutContainer(Container c) {
            JButton theLeftButton = WindowsSplitPaneDivider.this.getLeftButtonFromSuper();
            JButton theRightButton = WindowsSplitPaneDivider.this.getRightButtonFromSuper();
            JSplitPane theSplitPane = WindowsSplitPaneDivider.this.getSplitPaneFromSuper();
            int theOrientation = WindowsSplitPaneDivider.this.getOrientationFromSuper();
            int oneTouchSize = WindowsSplitPaneDivider.getOneTouchSize();
            int oneTouchOffset = WindowsSplitPaneDivider.getOneTouchOffset();
            if (theLeftButton != null && theRightButton != null && c == WindowsSplitPaneDivider.this) {
                if (theSplitPane.isOneTouchExpandable()) {
                    if (theOrientation == 0) {
                        theLeftButton.setBounds(oneTouchOffset, 0, WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE * 2, WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE);
                        theRightButton.setBounds(oneTouchOffset + (oneTouchSize * 2), 0, WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE * 2, WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE);
                        return;
                    } else {
                        theLeftButton.setBounds(0, oneTouchOffset, WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE, WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE * 2);
                        theRightButton.setBounds(0, oneTouchOffset + (oneTouchSize * 2), WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE, WindowsSplitPaneDivider.EXT_ONE_TOUCH_SIZE * 2);
                        return;
                    }
                }
                theLeftButton.setBounds(-5, -5, 1, 1);
                theRightButton.setBounds(-5, -5, 1, 1);
            }
        }

        public Dimension minimumLayoutSize(Container c) {
            return new Dimension(0, 0);
        }

        public Dimension preferredLayoutSize(Container c) {
            return new Dimension(0, 0);
        }

        public void removeLayoutComponent(Component c) {
        }

        public void addLayoutComponent(String string, Component c) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowsSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
        setLayout(new ExtWindowsDividerLayout());
    }

    protected JButton createLeftOneTouchButton() {
        JButton b = new JButton() { // from class: com.jgoodies.looks.windows.WindowsSplitPaneDivider.1
            int[][] buffer = {new int[]{0, 0, 0, 2, 2, 0, 0, 0, 0}, new int[]{0, 0, 2, 1, 1, 1, 0, 0, 0}, new int[]{0, 2, 1, 1, 1, 1, 1, 0, 0}, new int[]{2, 1, 1, 1, 1, 1, 1, 1, 0}, new int[]{0, 3, 3, 3, 3, 3, 3, 3, 3}};

            public void setBorder(Border border) {
            }

            public void paint(Graphics g) {
                JSplitPane theSplitPane = WindowsSplitPaneDivider.this.getSplitPaneFromSuper();
                if (theSplitPane != null) {
                    int theOrientation = WindowsSplitPaneDivider.this.getOrientationFromSuper();
                    int blockSize = this.buffer.length + 1;
                    Color[] colors = {getBackground(), UIManager.getColor("controlDkShadow"), Color.black, UIManager.getColor("controlLtHighlight")};
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    if (getModel().isPressed()) {
                        colors[1] = colors[2];
                    }
                    if (theOrientation == 0) {
                        for (int i = 1; i <= this.buffer[0].length; i++) {
                            for (int j = 1; j < blockSize; j++) {
                                if (this.buffer[j - 1][i - 1] != 0) {
                                    g.setColor(colors[this.buffer[j - 1][i - 1]]);
                                    g.drawLine(i - 1, j, i - 1, j);
                                }
                            }
                        }
                        return;
                    }
                    for (int i2 = 1; i2 <= this.buffer[0].length; i2++) {
                        for (int j2 = 1; j2 < blockSize; j2++) {
                            if (this.buffer[j2 - 1][i2 - 1] != 0) {
                                g.setColor(colors[this.buffer[j2 - 1][i2 - 1]]);
                                g.drawLine(j2 - 1, i2, j2 - 1, i2);
                            }
                        }
                    }
                }
            }
        };
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFocusable(false);
        b.setOpaque(false);
        return b;
    }

    protected JButton createRightOneTouchButton() {
        JButton b = new JButton() { // from class: com.jgoodies.looks.windows.WindowsSplitPaneDivider.2
            int[][] buffer = {new int[]{2, 2, 2, 2, 2, 2, 2, 2}, new int[]{0, 1, 1, 1, 1, 1, 1, 3}, new int[]{0, 0, 1, 1, 1, 1, 3, 0}, new int[]{0, 0, 0, 1, 1, 3, 0, 0}, new int[]{0, 0, 0, 0, 3, 0, 0, 0}};

            public void setBorder(Border border) {
            }

            public void paint(Graphics g) {
                JSplitPane theSplitPane = WindowsSplitPaneDivider.this.getSplitPaneFromSuper();
                if (theSplitPane != null) {
                    int theOrientation = WindowsSplitPaneDivider.this.getOrientationFromSuper();
                    int blockSize = this.buffer.length + 1;
                    Color[] colors = {getBackground(), UIManager.getColor("controlDkShadow"), Color.black, UIManager.getColor("controlLtHighlight")};
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    if (getModel().isPressed()) {
                        colors[1] = colors[2];
                    }
                    if (theOrientation == 0) {
                        for (int i = 1; i <= this.buffer[0].length; i++) {
                            for (int j = 1; j < blockSize; j++) {
                                if (this.buffer[j - 1][i - 1] != 0) {
                                    g.setColor(colors[this.buffer[j - 1][i - 1]]);
                                    g.drawLine(i, j, i, j);
                                }
                            }
                        }
                        return;
                    }
                    for (int i2 = 1; i2 <= this.buffer[0].length; i2++) {
                        for (int j2 = 1; j2 < blockSize; j2++) {
                            if (this.buffer[j2 - 1][i2 - 1] != 0) {
                                g.setColor(colors[this.buffer[j2 - 1][i2 - 1]]);
                                g.drawLine(j2 - 1, i2, j2 - 1, i2);
                            }
                        }
                    }
                }
            }
        };
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFocusable(false);
        b.setOpaque(false);
        return b;
    }

    static int getBlockSize() {
        return EXT_BLOCKSIZE;
    }

    static int getOneTouchOffset() {
        return 2;
    }

    static int getOneTouchSize() {
        return EXT_ONE_TOUCH_SIZE;
    }

    int getOrientationFromSuper() {
        return ((BasicSplitPaneDivider) this).orientation;
    }

    JButton getLeftButtonFromSuper() {
        return ((BasicSplitPaneDivider) this).leftButton;
    }

    JButton getRightButtonFromSuper() {
        return ((BasicSplitPaneDivider) this).rightButton;
    }

    JSplitPane getSplitPaneFromSuper() {
        return ((BasicSplitPaneDivider) this).splitPane;
    }

    public void paint(Graphics g) {
        Color background;
        if (this.splitPane.isOpaque()) {
            if (this.splitPane.hasFocus()) {
                background = UIManager.getColor("SplitPane.shadow");
            } else {
                background = getBackground();
            }
            Color bgColor = background;
            if (bgColor != null) {
                g.setColor(bgColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
        super.paint(g);
    }
}
