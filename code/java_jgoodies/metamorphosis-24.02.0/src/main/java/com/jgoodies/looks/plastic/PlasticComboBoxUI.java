package com.jgoodies.looks.plastic;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.common.ExtBasicComboBoxEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.plaf.metal.MetalTextFieldUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticComboBoxUI.class */
public class PlasticComboBoxUI extends MetalComboBoxUI {
    static final String CELL_EDITOR_KEY = "JComboBox.isTableCellEditor";
    private static final JTextField PHANTOM = new JTextField("Phantom");
    private static Class<? extends LookAndFeel> phantomLafClass;
    private boolean tableCellEditor;
    private PropertyChangeListener propertyChangeListener;

    public static ComponentUI createUI(JComponent b) {
        ensurePhantomHasPlasticUI();
        return new PlasticComboBoxUI();
    }

    private static void ensurePhantomHasPlasticUI() {
        TextUI ui = PHANTOM.getUI();
        Class cls = UIManager.getLookAndFeel().getClass();
        if (phantomLafClass != cls || !(ui instanceof MetalTextFieldUI)) {
            phantomLafClass = cls;
            PHANTOM.updateUI();
        }
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        this.tableCellEditor = isTableCellEditorReplaced();
    }

    protected void installListeners() {
        super.installListeners();
        this.propertyChangeListener = this::onTableCellEditorPropertyChanged;
        this.comboBox.addPropertyChangeListener(CELL_EDITOR_KEY, this.propertyChangeListener);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        this.comboBox.removePropertyChangeListener(CELL_EDITOR_KEY, this.propertyChangeListener);
        this.propertyChangeListener = null;
    }

    protected JButton createArrowButton() {
        return new PlasticComboBoxButton(this.comboBox, UIManager.getIcon("ComboBox.arrowIcon"), this.comboBox.isEditable(), this.currentValuePane, this.listBox);
    }

    protected ComboBoxEditor createEditor() {
        return new ExtBasicComboBoxEditor(this.tableCellEditor);
    }

    protected LayoutManager createLayoutManager() {
        return new PlasticComboBoxLayoutManager();
    }

    protected ComboPopup createPopup() {
        return new PlasticComboPopup(this.comboBox);
    }

    protected ListCellRenderer createRenderer() {
        if (this.tableCellEditor) {
            return super.createRenderer();
        }
        BasicComboBoxRenderer.UIResource uIResource = new BasicComboBoxRenderer.UIResource();
        uIResource.setBorder(UIManager.getBorder("ComboBox.rendererBorder"));
        return uIResource;
    }

    public Dimension getMinimumSize(JComponent c) {
        if (!this.isMinimumSizeDirty) {
            return new Dimension(this.cachedMinimumSize);
        }
        Dimension size = getDisplaySize();
        Insets insets = getInsets();
        size.height += insets.top + insets.bottom;
        if (this.comboBox.isEditable()) {
            Insets editorBorderInsets = UIManager.getInsets("ComboBox.editorBorderInsets");
            size.width += editorBorderInsets.left + editorBorderInsets.right;
            size.width += ScreenScaling.toPhysical(2);
        } else if (this.arrowButton != null) {
            Insets arrowButtonInsets = this.arrowButton.getInsets();
            size.width += arrowButtonInsets.left;
        }
        int buttonWidth = getEditableButtonWidth();
        size.width += insets.left + insets.right + buttonWidth;
        JComponent renderer = this.comboBox.getRenderer();
        if (renderer instanceof JComponent) {
            JComponent component = renderer;
            Insets rendererInsets = component.getInsets();
            Insets editorInsets = UIManager.getInsets("ComboBox.editorInsets");
            int offsetLeft = Math.max(0, editorInsets.left - rendererInsets.left);
            int offsetRight = Math.max(0, editorInsets.right - rendererInsets.right);
            size.width += offsetLeft + offsetRight;
        }
        Dimension textFieldSize = PHANTOM.getMinimumSize();
        size.height = Math.max(textFieldSize.height, size.height);
        this.cachedMinimumSize.setSize(size.width, size.height);
        this.isMinimumSizeDirty = false;
        return new Dimension(size);
    }

    protected Rectangle rectangleForCurrentValue() {
        int width = this.comboBox.getWidth();
        int height = this.comboBox.getHeight();
        Insets insets = getInsets();
        int buttonWidth = getEditableButtonWidth();
        if (this.arrowButton != null) {
            buttonWidth = this.arrowButton.getWidth();
        }
        if (this.comboBox.getComponentOrientation().isLeftToRight()) {
            return new Rectangle(insets.left, insets.top, width - ((insets.left + insets.right) + buttonWidth), height - (insets.top + insets.bottom));
        }
        return new Rectangle(insets.left + buttonWidth, insets.top, width - ((insets.left + insets.right) + buttonWidth), height - (insets.top + insets.bottom));
    }

    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
            if (isToolBarComboBox(c)) {
                c.setOpaque(false);
            }
        }
        paint(g, c);
    }

    protected boolean isToolBarComboBox(JComponent c) {
        Container parent = c.getParent();
        return parent != null && ((parent instanceof JToolBar) || (parent.getParent() instanceof JToolBar));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getEditableButtonWidth() {
        return UIManager.getInt("ScrollBar.width") - 1;
    }

    private boolean isTableCellEditorReplaced() {
        return Boolean.TRUE.equals(this.comboBox.getClientProperty(CELL_EDITOR_KEY));
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticComboBoxUI$PlasticComboBoxLayoutManager.class */
    private final class PlasticComboBoxLayoutManager extends MetalComboBoxLayoutManager {
        private PlasticComboBoxLayoutManager() {
            super(PlasticComboBoxUI.this);
        }

        public void layoutContainer(Container parent) {
            JComboBox<?> cb = (JComboBox) parent;
            if (!cb.isEditable()) {
                super.layoutContainer(parent);
                return;
            }
            int width = cb.getWidth();
            int height = cb.getHeight();
            Insets insets = PlasticComboBoxUI.this.getInsets();
            int buttonWidth = PlasticComboBoxUI.getEditableButtonWidth();
            int buttonHeight = height - (insets.top + insets.bottom);
            if (PlasticComboBoxUI.this.arrowButton != null) {
                if (cb.getComponentOrientation().isLeftToRight()) {
                    PlasticComboBoxUI.this.arrowButton.setBounds(width - (insets.right + buttonWidth), insets.top, buttonWidth, buttonHeight);
                } else {
                    PlasticComboBoxUI.this.arrowButton.setBounds(insets.left, insets.top, buttonWidth, buttonHeight);
                }
            }
            if (PlasticComboBoxUI.this.editor != null) {
                PlasticComboBoxUI.this.editor.setBounds(PlasticComboBoxUI.this.rectangleForCurrentValue());
            }
        }
    }

    public PropertyChangeListener createPropertyChangeListener() {
        return new PlasticPropertyChangeListener();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticComboBoxUI$PlasticPropertyChangeListener.class */
    private final class PlasticPropertyChangeListener extends PropertyChangeHandler {
        private PlasticPropertyChangeListener() {
            super(PlasticComboBoxUI.this);
        }

        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange(e);
            String propertyName = e.getPropertyName();
            boolean z = -1;
            switch (propertyName.hashCode()) {
                case -1332194002:
                    if (propertyName.equals("background")) {
                        z = true;
                        break;
                    }
                    break;
                case 1602416228:
                    if (propertyName.equals(ComponentModel.PROPERTY_EDITABLE)) {
                        z = false;
                        break;
                    }
                    break;
                case 1984457027:
                    if (propertyName.equals(AnimatedLabel.PROPERTY_FOREGROUND)) {
                        z = 2;
                        break;
                    }
                    break;
            }
            switch (z) {
                case AnimatedLabel.CENTER /* 0 */:
                    PlasticComboBoxButton<?> button = (PlasticComboBoxButton) PlasticComboBoxUI.this.arrowButton;
                    button.setIconOnly(PlasticComboBoxUI.this.comboBox.isEditable());
                    PlasticComboBoxUI.this.comboBox.repaint();
                    return;
                case true:
                    Color color1 = (Color) e.getNewValue();
                    PlasticComboBoxUI.this.arrowButton.setBackground(color1);
                    PlasticComboBoxUI.this.listBox.setBackground(color1);
                    return;
                case AnimatedLabel.LEFT /* 2 */:
                    Color color2 = (Color) e.getNewValue();
                    PlasticComboBoxUI.this.arrowButton.setForeground(color2);
                    PlasticComboBoxUI.this.listBox.setForeground(color2);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticComboBoxUI$PlasticComboPopup.class */
    private static final class PlasticComboPopup extends BasicComboPopup {
        private PlasticComboPopup(JComboBox<?> combo) {
            super(combo);
        }

        protected void configureList() {
            super.configureList();
            this.list.setForeground(UIManager.getColor("MenuItem.foreground"));
            this.list.setBackground(UIManager.getColor("MenuItem.background"));
        }

        protected void configureScroller() {
            super.configureScroller();
            this.scroller.getVerticalScrollBar().putClientProperty("JScrollBar.isFreeStanding", Boolean.FALSE);
        }

        protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
            Rectangle defaultBounds = super.computePopupBounds(px, py, pw, ph);
            Object popupPrototypeDisplayValue = this.comboBox.getClientProperty(Options.COMBO_POPUP_PROTOTYPE_DISPLAY_VALUE_KEY);
            if (popupPrototypeDisplayValue == null) {
                return defaultBounds;
            }
            ListCellRenderer<Object> renderer = this.list.getCellRenderer();
            Component c = renderer.getListCellRendererComponent(this.list, popupPrototypeDisplayValue, -1, true, true);
            int pw2 = c.getPreferredSize().width;
            boolean hasVerticalScrollBar = this.comboBox.getItemCount() > this.comboBox.getMaximumRowCount();
            if (hasVerticalScrollBar) {
                JScrollBar verticalBar = this.scroller.getVerticalScrollBar();
                pw2 += verticalBar.getPreferredSize().width;
            }
            Rectangle prototypeBasedBounds = super.computePopupBounds(px, py, pw2, ph);
            return prototypeBasedBounds.width > defaultBounds.width ? prototypeBasedBounds : defaultBounds;
        }
    }

    private void onTableCellEditorPropertyChanged(PropertyChangeEvent evt) {
        this.tableCellEditor = isTableCellEditorReplaced();
        if (this.comboBox.getRenderer() == null || (this.comboBox.getRenderer() instanceof UIResource)) {
            this.comboBox.setRenderer(createRenderer());
        }
        if (this.comboBox.getEditor() == null || (this.comboBox.getEditor() instanceof UIResource)) {
            this.comboBox.setEditor(createEditor());
        }
    }
}
