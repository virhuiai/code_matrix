package com.jgoodies.looks.plastic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticComboBoxButton.class */
public class PlasticComboBoxButton<E> extends JButton {
    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    private static final Border EMPTY_BORDER = new EmptyBorder(EMPTY_INSETS);
    private static final int LEFT_MARGIN = 2;
    private static final int RIGHT_MARGIN = 2;
    private final JList<E> listBox;
    private final CellRendererPane rendererPane;
    private JComboBox<E> comboBox;
    private Icon comboIcon;
    private boolean iconOnly;
    private final boolean borderPaintsFocus;
    private final Insets focusInsets;

    public PlasticComboBoxButton(JComboBox<E> comboBox, Icon comboIcon, boolean iconOnly, CellRendererPane rendererPane, JList<E> listBox) {
        super("");
        this.iconOnly = false;
        setModel(new DefaultButtonModel() { // from class: com.jgoodies.looks.plastic.PlasticComboBoxButton.1
            public void setArmed(boolean armed) {
                super.setArmed(isPressed() || armed);
            }
        });
        this.comboBox = comboBox;
        this.comboIcon = comboIcon;
        this.iconOnly = iconOnly;
        this.rendererPane = rendererPane;
        this.listBox = listBox;
        setEnabled(comboBox.isEnabled());
        setFocusable(false);
        setRequestFocusEnabled(comboBox.isEnabled());
        setBorder(UIManager.getBorder("ComboBox.arrowButtonBorder"));
        setMargin(new Insets(0, 2, 0, 2));
        this.borderPaintsFocus = UIManager.getBoolean("ComboBox.borderPaintsFocus");
        this.focusInsets = UIManager.getInsets("ComboBox.focusInsets");
    }

    public final JComboBox<E> getComboBox() {
        return this.comboBox;
    }

    public final void setComboBox(JComboBox<E> cb) {
        this.comboBox = cb;
    }

    public final Icon getComboIcon() {
        return this.comboIcon;
    }

    public final void setComboIcon(Icon i) {
        this.comboIcon = i;
    }

    public final boolean isIconOnly() {
        return this.iconOnly;
    }

    public final void setIconOnly(boolean b) {
        this.iconOnly = b;
    }

    public final void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setBackground(this.comboBox.getBackground());
            setForeground(this.comboBox.getForeground());
        } else {
            setBackground(UIManager.getColor("ComboBox.disabledBackground"));
            setForeground(UIManager.getColor("ComboBox.disabledForeground"));
        }
    }

    public final boolean isFocusable() {
        return false;
    }

    public final void paintComponent(Graphics g) {
        int iconLeft;
        super.paintComponent(g);
        boolean leftToRight = PlasticUtils.isLeftToRight(this.comboBox);
        Insets insets = getInsets();
        int width = getWidth() - (insets.left + insets.right);
        int height = getHeight() - (insets.top + insets.bottom);
        if (height <= 0 || width <= 0) {
            return;
        }
        int left = insets.left;
        int top = insets.top;
        int right = (left + width) - 1;
        int iconWidth = 0;
        int i = leftToRight ? right : left;
        if (this.comboIcon != null) {
            iconWidth = this.comboIcon.getIconWidth();
            int iconHeight = this.comboIcon.getIconHeight();
            if (this.iconOnly) {
                iconLeft = (getWidth() - iconWidth) / 2;
            } else {
                iconLeft = leftToRight ? ((left + width) - 1) - iconWidth : left;
            }
            int iconTop = (getHeight() - iconHeight) / 2;
            this.comboIcon.paintIcon(this, g, iconLeft, iconTop);
        }
        if (!this.iconOnly && this.comboBox != null) {
            ListCellRenderer renderer = this.comboBox.getRenderer();
            boolean renderPressed = getModel().isPressed();
            JComponent listCellRendererComponent = renderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, renderPressed, false);
            int x = leftToRight ? left : left + iconWidth;
            int y = top;
            int w = (getWidth() - left) - PlasticComboBoxUI.getEditableButtonWidth();
            int h = height;
            Border oldBorder = null;
            if ((listCellRendererComponent instanceof JComponent) && !isTableCellEditor()) {
                JComponent component = listCellRendererComponent;
                if (listCellRendererComponent instanceof BasicComboBoxRenderer.UIResource) {
                    oldBorder = component.getBorder();
                    component.setBorder(EMPTY_BORDER);
                }
                Insets rendererInsets = component.getInsets();
                Insets editorInsets = UIManager.getInsets("ComboBox.editorInsets");
                int offsetTop = Math.max(0, editorInsets.top - rendererInsets.top);
                int offsetBottom = Math.max(0, editorInsets.bottom - rendererInsets.bottom);
                y += offsetTop;
                h -= offsetTop + offsetBottom;
            }
            listCellRendererComponent.setFont(this.rendererPane.getFont());
            configureColors(listCellRendererComponent);
            boolean shouldValidate = listCellRendererComponent instanceof JPanel;
            if (!is3D() || !(listCellRendererComponent instanceof JComponent) || !listCellRendererComponent.isOpaque()) {
                this.rendererPane.paintComponent(g, listCellRendererComponent, this, x, y, w, h, shouldValidate);
            } else {
                JComponent component2 = listCellRendererComponent;
                boolean oldOpaque = component2.isOpaque();
                component2.setOpaque(false);
                this.rendererPane.paintComponent(g, listCellRendererComponent, this, x, y, w, h, shouldValidate);
                component2.setOpaque(oldOpaque);
            }
            if (oldBorder != null) {
                listCellRendererComponent.setBorder(oldBorder);
            }
        }
        if (this.comboIcon != null) {
            boolean hasFocus = this.comboBox.hasFocus();
            if (!this.borderPaintsFocus && hasFocus) {
                g.setColor(PlasticLookAndFeel.getFocusColor());
                int x2 = this.focusInsets.left;
                int y2 = this.focusInsets.top;
                BasicGraphicsUtils.drawDashedRect(g, x2, y2, (getWidth() - x2) - this.focusInsets.left, (getHeight() - y2) - this.focusInsets.bottom);
            }
        }
    }

    protected void configureColors(Component c) {
        if (this.model.isArmed() && this.model.isPressed()) {
            if (isOpaque()) {
                c.setBackground(UIManager.getColor("Button.select"));
            }
            c.setForeground(this.comboBox.getForeground());
        } else if (!this.comboBox.isEnabled()) {
            if (isOpaque()) {
                c.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
            }
            c.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
        } else {
            c.setForeground(this.comboBox.getForeground());
            c.setBackground(this.comboBox.getBackground());
        }
    }

    private static boolean is3D() {
        return PlasticUtils.is3D("ComboBox.");
    }

    private boolean isTableCellEditor() {
        return Boolean.TRUE.equals(this.comboBox.getClientProperty("JComboBox.isTableCellEditor"));
    }
}
