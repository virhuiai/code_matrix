package com.jgoodies.sandbox.util;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.components.JGFormattedTextField;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Date;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/util/AbstractOpenMonthViewPopupAction.class */
public abstract class AbstractOpenMonthViewPopupAction extends AbstractAction {
    private final JFormattedTextField textField;
    private JPopupMenu popup;
    private EventHandler handler;
    private Component invoker;
    private boolean oldIconVisibleAlways;
    private boolean oldButtonPaintedAlways;

    protected abstract void initMonthView();

    protected abstract void configurePopup(JPopupMenu jPopupMenu);

    protected abstract void configureAction();

    protected abstract void postVisible();

    protected abstract Date getDate();

    protected abstract void setDate(Date date);

    protected AbstractOpenMonthViewPopupAction(JFormattedTextField textField) {
        this.textField = textField;
        configureAction();
    }

    private void buildPopup(EventObject e) {
        this.handler = new EventHandler();
        this.textField.addPropertyChangeListener(this.handler);
        this.popup = new JPopupMenu();
        this.popup.addPropertyChangeListener(this.handler);
        initMonthView();
        configurePopup(this.popup);
        this.invoker = (Component) e.getSource();
        if (this.invoker instanceof JFormattedTextField) {
            try {
                this.invoker.commitEdit();
            } catch (ParseException e2) {
            }
        }
        initializeSelection();
        if (this.invoker instanceof JGFormattedTextField) {
            JGFormattedTextField field = this.invoker;
            this.oldIconVisibleAlways = field.isIconVisibleAlways();
            this.oldButtonPaintedAlways = field.isButtonPaintedAlways();
            field.setIconVisibleAlways(true);
            field.setButtonPaintedAlways(true);
        }
    }

    protected void cleanup() {
        this.textField.removePropertyChangeListener(this.handler);
        this.popup.removePropertyChangeListener(this.handler);
        if (this.invoker instanceof JGFormattedTextField) {
            JGFormattedTextField field = this.invoker;
            field.setIconVisibleAlways(this.oldIconVisibleAlways);
            field.setButtonPaintedAlways(this.oldButtonPaintedAlways);
        }
        this.invoker = null;
        this.popup = null;
        this.handler = null;
    }

    protected void initializeSelection() {
        updateSelection();
    }

    protected final void setToday() {
        setDate(new Date());
    }

    protected final EventHandler getHandler() {
        return this.handler;
    }

    public void actionPerformed(ActionEvent e) {
        if (this.popup == null) {
            buildPopup(e);
        }
        if (this.popup.isVisible()) {
            this.popup.setVisible(false);
        } else {
            this.popup.show(this.invoker, 0, this.invoker.getHeight());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSelection() {
        Date date = (Date) this.textField.getValue();
        if (date != null) {
            setDate(date);
        }
    }

    protected final void commitAndClose() {
        try {
            this.textField.setValue(getDate());
        } finally {
            if (this.popup != null) {
                this.popup.setVisible(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/util/AbstractOpenMonthViewPopupAction$EventHandler.class */
    public final class EventHandler implements ActionListener, FocusListener, PropertyChangeListener {
        private EventHandler() {
        }

        public void actionPerformed(ActionEvent arg0) {
            AbstractOpenMonthViewPopupAction.this.commitAndClose();
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            boolean z = -1;
            switch (propertyName.hashCode()) {
                case 111972721:
                    if (propertyName.equals(ValueModel.PROPERTY_VALUE)) {
                        z = true;
                        break;
                    }
                    break;
                case 466743410:
                    if (propertyName.equals(ComponentModel.PROPERTY_VISIBLE)) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case AnimatedLabel.CENTER /* 0 */:
                    JPopupMenu thePopup = (JPopupMenu) evt.getSource();
                    boolean visible = thePopup.isVisible();
                    if (visible) {
                        AbstractOpenMonthViewPopupAction.this.postVisible();
                        return;
                    } else {
                        AbstractOpenMonthViewPopupAction.this.cleanup();
                        return;
                    }
                case true:
                    AbstractOpenMonthViewPopupAction.this.updateSelection();
                    return;
                default:
                    return;
            }
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            AbstractOpenMonthViewPopupAction.this.popup.setVisible(false);
        }
    }
}
