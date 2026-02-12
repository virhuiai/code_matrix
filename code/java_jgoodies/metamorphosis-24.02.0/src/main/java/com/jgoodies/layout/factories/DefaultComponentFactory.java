package com.jgoodies.layout.factories;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.MnemonicUtils;
import com.jgoodies.layout.layout.Sizes;
import com.jgoodies.layout.util.FormUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/DefaultComponentFactory.class */
public class DefaultComponentFactory implements ComponentFactory {
    private static final DefaultComponentFactory INSTANCE = new DefaultComponentFactory();

    public static DefaultComponentFactory getInstance() {
        return INSTANCE;
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createLabel(String markedText) {
        JLabel label = new FormsLabel();
        MnemonicUtils.configure(label, markedText);
        return label;
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createReadOnlyLabel(String markedText) {
        JLabel label = new ReadOnlyLabel();
        MnemonicUtils.configure(label, markedText);
        return label;
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JButton createButton(Action action) {
        return new JButton(action);
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createTitle(String markedText) {
        JLabel label = new TitleLabel();
        MnemonicUtils.configure(label, markedText);
        label.setVerticalAlignment(0);
        return label;
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JLabel createHeaderLabel(String markedText) {
        return createTitle(markedText);
    }

    public JComponent createSeparator(String markedText) {
        return createSeparator(markedText, 2);
    }

    @Override // com.jgoodies.layout.factories.ComponentFactory
    public JComponent createSeparator(String markedText, int alignment) {
        if (Strings.isBlank(markedText)) {
            return new JSeparator();
        }
        JLabel title = createTitle(markedText);
        title.setHorizontalAlignment(alignment);
        return createSeparator(title);
    }

    public JComponent createSeparator(JLabel label) {
        Preconditions.checkNotNull(label, Messages.MUST_NOT_BE_NULL, "label");
        int horizontalAlignment = label.getHorizontalAlignment();
        Preconditions.checkArgument(horizontalAlignment == 2 || horizontalAlignment == 0 || horizontalAlignment == 4, "The label's horizontal alignment must be one of: LEFT, CENTER, RIGHT.");
        JPanel panel = new JPanel(new TitledSeparatorLayout(!FormUtils.isLafAqua()));
        panel.setOpaque(false);
        panel.add(label);
        panel.add(new JSeparator());
        if (horizontalAlignment == 0) {
            panel.add(new JSeparator());
        }
        return panel;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/DefaultComponentFactory$FormsLabel.class */
    public static class FormsLabel extends JLabel {
        private FormsLabel() {
        }

        public AccessibleContext getAccessibleContext() {
            if (this.accessibleContext == null) {
                this.accessibleContext = new AccessibleFormsLabel();
            }
            return this.accessibleContext;
        }

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/DefaultComponentFactory$FormsLabel$AccessibleFormsLabel.class */
        private final class AccessibleFormsLabel extends AccessibleJLabel {
            private AccessibleFormsLabel() {
                super(FormsLabel.this);
            }

            public String getAccessibleName() {
                if (this.accessibleName != null) {
                    return this.accessibleName;
                }
                String text = FormsLabel.this.getText();
                if (text == null) {
                    return super.getAccessibleName();
                }
                return text.endsWith(":") ? text.substring(0, text.length() - 1) : text;
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/DefaultComponentFactory$ReadOnlyLabel.class */
    private static final class ReadOnlyLabel extends FormsLabel {
        private static final String[] UIMANAGER_KEYS = {"Label.disabledForeground", "Label.disabledText", "Label[Disabled].textForeground", "textInactiveText"};

        private ReadOnlyLabel() {
            super();
        }

        public void updateUI() {
            super.updateUI();
            setForeground(getDisabledForeground());
        }

        private static Color getDisabledForeground() {
            for (String key : UIMANAGER_KEYS) {
                Color foreground = UIManager.getColor(key);
                if (foreground != null) {
                    return foreground;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/DefaultComponentFactory$TitleLabel.class */
    public static final class TitleLabel extends FormsLabel {
        private TitleLabel() {
            super();
        }

        public void updateUI() {
            super.updateUI();
            Color foreground = getTitleColor();
            if (foreground != null) {
                setForeground(foreground);
            }
            setFont(getTitleFont());
        }

        private static Color getTitleColor() {
            return UIManager.getColor("TitledBorder.titleColor");
        }

        private static Font getTitleFont() {
            if (FormUtils.isLafAqua()) {
                return UIManager.getFont("Label.font").deriveFont(1);
            }
            return UIManager.getFont("TitledBorder.font");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/DefaultComponentFactory$TitledSeparatorLayout.class */
    public static final class TitledSeparatorLayout implements LayoutManager {
        private final boolean centerSeparators;

        private TitledSeparatorLayout(boolean centerSeparators) {
            this.centerSeparators = centerSeparators;
        }

        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        public Dimension preferredLayoutSize(Container parent) {
            Dimension labelSize = getLabel(parent).getPreferredSize();
            Insets insets = parent.getInsets();
            int width = labelSize.width + insets.left + insets.right;
            int height = labelSize.height + insets.top + insets.bottom;
            return new Dimension(width, height);
        }

        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension size = parent.getSize();
                Insets insets = parent.getInsets();
                int width = (size.width - insets.left) - insets.right;
                JLabel label = getLabel(parent);
                Dimension labelSize = label.getPreferredSize();
                int labelWidth = labelSize.width;
                int labelHeight = labelSize.height;
                Component separator1 = parent.getComponent(1);
                int separatorHeight = separator1.getPreferredSize().height;
                FontMetrics metrics = label.getFontMetrics(label.getFont());
                int ascent = metrics.getMaxAscent();
                int hGapDlu = this.centerSeparators ? 3 : 1;
                int hGap = Sizes.dialogUnitXAsPixel(hGapDlu, label);
                int vOffset = this.centerSeparators ? 1 + ((labelHeight - separatorHeight) / 2) : ascent - (separatorHeight / 2);
                int alignment = label.getHorizontalAlignment();
                int y = insets.top;
                if (alignment == 2) {
                    int x = insets.left;
                    label.setBounds(x, y, labelWidth, labelHeight);
                    int x2 = x + labelWidth + hGap;
                    int separatorWidth = (size.width - insets.right) - x2;
                    separator1.setBounds(x2, y + vOffset, separatorWidth, separatorHeight);
                } else if (alignment == 4) {
                    int x3 = (insets.left + width) - labelWidth;
                    label.setBounds(x3, y, labelWidth, labelHeight);
                    int separatorWidth2 = ((x3 - hGap) - 1) - insets.left;
                    separator1.setBounds(insets.left, y + vOffset, separatorWidth2, separatorHeight);
                } else {
                    int xOffset = ((width - labelWidth) - (2 * hGap)) / 2;
                    int x4 = insets.left;
                    separator1.setBounds(x4, y + vOffset, xOffset - 1, separatorHeight);
                    int x5 = x4 + xOffset + hGap;
                    label.setBounds(x5, y, labelWidth, labelHeight);
                    int x6 = x5 + labelWidth + hGap;
                    Component separator2 = parent.getComponent(2);
                    int separatorWidth3 = (size.width - insets.right) - x6;
                    separator2.setBounds(x6, y + vOffset, separatorWidth3, separatorHeight);
                }
            }
        }

        private static JLabel getLabel(Container parent) {
            return parent.getComponent(0);
        }
    }
}
