package com.jgoodies.looks.common;

import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicOptionPaneUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtButtonAreaLayout.class */
public final class ExtButtonAreaLayout extends BasicOptionPaneUI.ButtonAreaLayout {
    private final int buttonMinimumWidth;

    public ExtButtonAreaLayout() {
        super(true, 6);
        int lafMinimumWidth = UIManager.getInt("OptionPane.buttonMinimumWidth");
        this.buttonMinimumWidth = lafMinimumWidth != 0 ? lafMinimumWidth : ScreenScaling.toPhysical(75);
    }

    public void layoutContainer(Container container) {
        int xLocation;
        int xOffset;
        int xLocation2;
        int xOffset2;
        Component[] children = container.getComponents();
        if (children != null && children.length > 0) {
            int numChildren = children.length;
            Dimension[] sizes = new Dimension[numChildren];
            int yLocation = container.getInsets().top;
            if (this.syncAllWidths) {
                int maxWidth = this.buttonMinimumWidth;
                for (int counter = 0; counter < numChildren; counter++) {
                    sizes[counter] = children[counter].getPreferredSize();
                    maxWidth = Math.max(maxWidth, sizes[counter].width);
                }
                if (getCentersChildren()) {
                    xLocation2 = (container.getSize().width - ((maxWidth * numChildren) + ((numChildren - 1) * this.padding))) / 2;
                    xOffset2 = this.padding + maxWidth;
                } else if (numChildren > 1) {
                    xLocation2 = 0;
                    xOffset2 = ((container.getSize().width - (maxWidth * numChildren)) / (numChildren - 1)) + maxWidth;
                } else {
                    xLocation2 = (container.getSize().width - maxWidth) / 2;
                    xOffset2 = 0;
                }
                boolean ltr = container.getComponentOrientation().isLeftToRight();
                for (int counter2 = 0; counter2 < numChildren; counter2++) {
                    int index = ltr ? counter2 : (numChildren - counter2) - 1;
                    children[index].setBounds(xLocation2, yLocation, maxWidth, sizes[index].height);
                    xLocation2 += xOffset2;
                }
                return;
            }
            int totalWidth = 0;
            for (int counter3 = 0; counter3 < numChildren; counter3++) {
                Dimension prefSize = children[counter3].getPreferredSize();
                sizes[counter3] = new Dimension(Math.max(prefSize.width, this.buttonMinimumWidth), prefSize.height);
                totalWidth += sizes[counter3].width;
            }
            int totalWidth2 = totalWidth + ((numChildren - 1) * this.padding);
            boolean cc = getCentersChildren();
            if (cc) {
                xLocation = (container.getSize().width - totalWidth2) / 2;
                xOffset = this.padding;
            } else if (numChildren > 1) {
                xOffset = (container.getSize().width - totalWidth2) / (numChildren - 1);
                xLocation = 0;
            } else {
                xLocation = (container.getSize().width - totalWidth2) / 2;
                xOffset = 0;
            }
            boolean ltr2 = container.getComponentOrientation().isLeftToRight();
            for (int counter4 = 0; counter4 < numChildren; counter4++) {
                int index2 = ltr2 ? counter4 : (numChildren - counter4) - 1;
                children[index2].setBounds(xLocation, yLocation, sizes[index2].width, sizes[index2].height);
                xLocation += xOffset + sizes[index2].width;
            }
        }
    }

    public Dimension minimumLayoutSize(Container c) {
        Component[] children;
        if (c != null && (children = c.getComponents()) != null && children.length > 0) {
            int numChildren = children.length;
            int height = 0;
            Insets cInsets = c.getInsets();
            int extraHeight = cInsets.top + cInsets.bottom;
            if (this.syncAllWidths) {
                int maxWidth = this.buttonMinimumWidth;
                for (Component component : children) {
                    Dimension aSize = component.getPreferredSize();
                    height = Math.max(height, aSize.height);
                    maxWidth = Math.max(maxWidth, aSize.width);
                }
                return new Dimension((maxWidth * numChildren) + ((numChildren - 1) * this.padding), extraHeight + height);
            }
            int totalWidth = 0;
            for (Component component2 : children) {
                Dimension prefSize = component2.getPreferredSize();
                Dimension aSize2 = new Dimension(Math.max(prefSize.width, this.buttonMinimumWidth), prefSize.height);
                height = Math.max(height, aSize2.height);
                totalWidth += aSize2.width;
            }
            return new Dimension(totalWidth + ((numChildren - 1) * this.padding), extraHeight + height);
        }
        return new Dimension(0, 0);
    }
}
