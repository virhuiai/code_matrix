package com.jgoodies.framework.component;

import com.jgoodies.binding.binder.BeanBinder;
import com.jgoodies.binding.binder.Binders;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.jsdl.JSDLCommonSetup;
import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.components.util.OSXComponentProperties;
import com.jgoodies.dialogs.core.style.StyleManager;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.MissingResourceException;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/component/ExpandableView.class */
public final class ExpandableView extends Bean {
    public static final String PROPERTY_EXPANDED = "expanded";
    private JComponent collapsedView;
    private String collapsedToolTipText;
    private JComponent expandedView;
    private String expandedToolTipText;
    private JComponent containerView;
    private JToggleButton expansionControl;
    private boolean expanded;
    private boolean panelExpansionEnabled;

    static {
        StyleManager.getStyle();
    }

    public ExpandableView() {
        this.expanded = false;
        this.panelExpansionEnabled = false;
        initComponents();
        initEventHandling();
        initBindings();
    }

    public ExpandableView(JComponent collapsedView, String collapsedToolTipText, JComponent expandedView, String expandedToolTipText) {
        this();
        setCollapsedView(collapsedView);
        setCollapsedToolTipText(collapsedToolTipText);
        setExpandedView(expandedView);
        setExpandedToolTipText(expandedToolTipText);
    }

    public void setCollapsedView(JComponent collapsedView) {
        this.collapsedView = collapsedView;
        updateExpansionState();
    }

    public void setCollapsedToolTipText(String collapsedToolTipText) {
        this.collapsedToolTipText = collapsedToolTipText;
        updateExpansionState();
    }

    public void setExpandedView(JComponent expandedView) {
        this.expandedView = expandedView;
        updateExpansionState();
    }

    public void setExpandedToolTipText(String expandedToolTipText) {
        this.expandedToolTipText = expandedToolTipText;
        updateExpansionState();
    }

    public boolean isPanelExpansionEnabled() {
        return this.panelExpansionEnabled;
    }

    public void setPanelExpansionEnabled(boolean newValue) {
        this.panelExpansionEnabled = newValue;
    }

    private void initComponents() {
        this.containerView = new JPanel(new BorderLayout());
        this.containerView.setOpaque(false);
        this.expansionControl = createExpansionButton();
    }

    private void initEventHandling() {
        this.containerView.addMouseListener(new MouseAdapter() { // from class: com.jgoodies.framework.component.ExpandableView.1
            public void mouseClicked(MouseEvent evt) {
                ExpandableView.this.onPanelClicked(evt);
            }
        });
    }

    private void initBindings() {
        BeanBinder binder = Binders.binderFor(this);
        binder.bindProperty(PROPERTY_EXPANDED).to((AbstractButton) this.expansionControl);
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean newValue) {
        boolean oldValue = isExpanded();
        this.expanded = newValue;
        updateExpansionState();
        firePropertyChange(PROPERTY_EXPANDED, oldValue, newValue);
    }

    public JComponent getView() {
        return this.containerView;
    }

    public JComponent getExpansionControl() {
        return this.expansionControl;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPanelClicked(MouseEvent evt) {
        if (isPanelExpansionEnabled()) {
            setExpanded(!isExpanded());
        }
    }

    private void updateExpansionState() {
        JComponent currentView;
        Icon pressedIcon;
        String toolTip;
        if (isExpanded()) {
            currentView = this.expandedView;
            pressedIcon = UIManager.getIcon("ExpandedControl.pressedIcon");
            toolTip = this.expandedToolTipText;
        } else {
            currentView = this.collapsedView;
            pressedIcon = UIManager.getIcon("CollapsedControl.pressedIcon");
            toolTip = this.collapsedToolTipText;
        }
        this.containerView.removeAll();
        if (currentView != null) {
            this.containerView.add(currentView);
        }
        this.containerView.revalidate();
        this.containerView.repaint();
        this.expansionControl.setToolTipText(toolTip);
        this.expansionControl.setPressedIcon(pressedIcon);
    }

    private static JToggleButton createExpansionButton() {
        Icon deselectedIcon = UIManager.getIcon("CollapsedControl.icon");
        Icon rolloverIcon = UIManager.getIcon("CollapsedControl.rolloverIcon");
        Icon selectedIcon = UIManager.getIcon("ExpandedControl.icon");
        Icon rolloverSelectedIcon = UIManager.getIcon("ExpandedControl.rolloverIcon");
        if (deselectedIcon == null) {
            throw new MissingResourceException("Mising UIManager icon.", "ExpandableView", "CollapsedControl.icon");
        }
        if (selectedIcon == null) {
            throw new MissingResourceException("Mising UIManager icon.", "ExpandableView", "ExpandedControl.icon");
        }
        JToggleButton button = new JToggleButton(deselectedIcon);
        button.setName("expansion");
        button.setSelectedIcon(selectedIcon);
        button.setRolloverIcon(rolloverIcon);
        button.setRolloverSelectedIcon(rolloverSelectedIcon);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorderPainted(false);
        button.setFocusable(JSDLCommonSetup.isOptimizedForScreenReader());
        FocusTraversalUtils.markAsPoorDefaultFocusOwner(button);
        if (SystemUtils.IS_OS_MAC) {
            button.putClientProperty(OSXComponentProperties.SizeVariant.KEY, "regular");
            button.putClientProperty("JButton.buttonType", "normal");
        }
        return button;
    }
}
