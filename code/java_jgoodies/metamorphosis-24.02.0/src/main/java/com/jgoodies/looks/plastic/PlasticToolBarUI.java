package com.jgoodies.looks.plastic;

import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToolBarUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticToolBarUI.class */
public class PlasticToolBarUI extends MetalToolBarUI {
    private static final String PROPERTY_PREFIX = "ToolBar.";
    private PropertyChangeListener listener;

    public static ComponentUI createUI(JComponent b) {
        return new PlasticToolBarUI();
    }

    protected Border createRolloverBorder() {
        return PlasticBorders.getRolloverButtonBorder();
    }

    protected void setBorderToRollover(Component c) {
        if (c instanceof AbstractButton) {
            super.setBorderToRollover(c);
            return;
        }
        if (c instanceof Container) {
            Container cont = (Container) c;
            for (int i = 0; i < cont.getComponentCount(); i++) {
                super.setBorderToRollover(cont.getComponent(i));
            }
        }
    }

    protected void installDefaults() {
        super.installDefaults();
        installSpecialBorder();
    }

    protected void installListeners() {
        super.installListeners();
        this.listener = this::onPropertyChange;
        this.toolBar.addPropertyChangeListener(this.listener);
    }

    protected void uninstallListeners() {
        this.toolBar.removePropertyChangeListener(this.listener);
        this.listener = null;
        super.uninstallListeners();
    }

    private void onPropertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop.equals(HeaderStyle.KEY) || prop.equals(BorderStyle.PLASTIC_KEY)) {
            installSpecialBorder();
        }
    }

    private void installSpecialBorder() {
        String suffix;
        BorderStyle borderStyle = BorderStyle.from(this.toolBar, BorderStyle.PLASTIC_KEY);
        if (borderStyle != null) {
            suffix = borderStyle.getSuffix();
        } else {
            HeaderStyle headerStyle = HeaderStyle.from(this.toolBar);
            if (headerStyle == HeaderStyle.BOTH) {
                suffix = "headerBorder";
            } else if (headerStyle == HeaderStyle.SINGLE && is3D()) {
                suffix = "etchedBorder";
            } else {
                suffix = "border";
            }
        }
        LookAndFeel.installBorder(this.toolBar, PROPERTY_PREFIX + suffix);
    }

    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
            if (is3D()) {
                Rectangle bounds = new Rectangle(0, 0, c.getWidth(), c.getHeight());
                boolean isHorizontal = ((JToolBar) c).getOrientation() == 0;
                PlasticUtils.addLight3DEffekt(g, bounds, isHorizontal);
            }
        }
        paint(g, c);
    }

    private boolean is3D() {
        return (!PlasticUtils.is3D(PROPERTY_PREFIX) || HeaderStyle.from(this.toolBar) == null || BorderStyle.from(this.toolBar, BorderStyle.PLASTIC_KEY) == BorderStyle.EMPTY) ? false : true;
    }
}
