package com.jgoodies.components;

import com.jgoodies.common.base.Preconditions;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.function.Function;
import javax.swing.JPanel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGCardPanel.class */
public final class JGCardPanel extends JPanel {
    public JGCardPanel() {
        super(new Layout());
    }

    public Component getVisibleCard() {
        int index = getVisibleChildIndex();
        if (index != -1) {
            return getComponent(index);
        }
        return null;
    }

    public int getVisibleChildIndex() {
        int nChildren = getComponentCount();
        for (int i = 0; i < nChildren; i++) {
            Component child = getComponent(i);
            if (child.isVisible()) {
                return i;
            }
        }
        return -1;
    }

    public String getVisibleChildName() {
        int i = getVisibleChildIndex();
        if (-1 == i) {
            return null;
        }
        return getComponent(i).getName();
    }

    public void showCard(Component card) {
        Component visibleComponent = getVisibleCard();
        if (visibleComponent == card) {
            return;
        }
        visibleComponent.setVisible(false);
        card.setVisible(true);
        revalidate();
        repaint();
    }

    public void showCard(String cardName) {
        int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            Component child = getComponent(i);
            if (child.getName().equals(cardName) && !child.isVisible()) {
                showCard(child);
                return;
            }
        }
    }

    public void showFirstCard() {
        if (getComponentCount() <= 0) {
            return;
        }
        showCard(getComponent(0));
    }

    public void showLastCard() {
        if (getComponentCount() <= 0) {
            return;
        }
        showCard(getComponent(getComponentCount() - 1));
    }

    public void showNextCard() {
        if (getComponentCount() <= 0) {
            return;
        }
        int index = getVisibleChildIndex();
        if (index == -1 || index == getComponentCount() - 1) {
            showCard(getComponent(0));
        } else {
            showCard(getComponent(index + 1));
        }
    }

    public void showPreviousCard() {
        if (getComponentCount() <= 0) {
            return;
        }
        int index = getVisibleChildIndex();
        if (index == -1) {
            showCard(getComponent(0));
        } else if (index == 0) {
            showCard(getComponent(getComponentCount() - 1));
        } else {
            showCard(getComponent(index - 1));
        }
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        Preconditions.checkArgument(constraints == null || (constraints instanceof String), "The constraints must be null or String.\nConstraints=" + constraints);
        super.addImpl(comp, constraints, index);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGCardPanel$Layout.class */
    private static final class Layout implements LayoutManager {
        private Layout() {
        }

        public void addLayoutComponent(String name, Component child) {
            if (name != null) {
                child.setName(name);
            }
            child.setVisible(child.getParent().getComponentCount() == 1);
        }

        public void removeLayoutComponent(Component child) {
            if (child.isVisible()) {
                Container parent = child.getParent();
                if (parent.getComponentCount() > 0) {
                    parent.getComponent(0).setVisible(true);
                }
            }
        }

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent, (v0) -> {
                return v0.getPreferredSize();
            });
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent, (v0) -> {
                return v0.getMinimumSize();
            });
        }

        public void layoutContainer(Container parent) {
            int count = parent.getComponentCount();
            Insets insets = parent.getInsets();
            for (int i = 0; i < count; i++) {
                Component child = parent.getComponent(i);
                if (child.isVisible()) {
                    Rectangle r = parent.getBounds();
                    int width = r.width - (insets.left + insets.right);
                    int height = r.height - (insets.top + insets.bottom);
                    child.setBounds(insets.left, insets.top, width, height);
                    return;
                }
            }
        }

        private static Dimension layoutSize(Container parent, Function<Component, Dimension> sizeMeasure) {
            int count = parent.getComponentCount();
            Insets insets = parent.getInsets();
            int width = 0;
            int height = 0;
            for (int i = 0; i < count; i++) {
                Dimension d = sizeMeasure.apply(parent.getComponent(i));
                if (d.width > width) {
                    width = d.width;
                }
                if (d.height > height) {
                    height = d.height;
                }
            }
            return new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom);
        }
    }
}
