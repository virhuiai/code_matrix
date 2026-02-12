package com.jgoodies.components.internal;

import com.jgoodies.components.util.ComponentUtils;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/StaticTextArea.class */
public final class StaticTextArea extends JTextArea {
    public StaticTextArea(String text) {
        super(text);
        super.setEditable(false);
        setFocusable(false);
        setRequestFocusEnabled(false);
        ComponentUtils.clearFocusTraversalKeys(this);
        disableEvents(48L);
    }

    public void setEditable(boolean editable) {
    }

    public synchronized void addMouseListener(MouseListener l) {
    }

    public synchronized void addMouseMotionListener(MouseMotionListener l) {
    }

    public void updateUI() {
        super.updateUI();
        ComponentSupport.configureTransparentBackground(this);
        setBorder(null);
        if (getForeground() instanceof UIResource) {
            setForeground(UIManager.getColor("controlText"));
        }
        setCaret(new NonAdjustingCaret());
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleStaticTextArea();
        }
        return this.accessibleContext;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/StaticTextArea$AccessibleStaticTextArea.class */
    protected final class AccessibleStaticTextArea extends AccessibleJComponent {
        protected AccessibleStaticTextArea() {
            super(StaticTextArea.this);
        }

        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            if (StaticTextArea.this.getText() != null) {
                return StaticTextArea.this.getText();
            }
            return super.getAccessibleName();
        }

        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LABEL;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/StaticTextArea$NonAdjustingCaret.class */
    private static final class NonAdjustingCaret extends DefaultCaret {
        private NonAdjustingCaret() {
        }

        protected void adjustVisibility(Rectangle nloc) {
        }
    }
}
