package com.jgoodies.components;

import com.jgoodies.common.base.Strings;
import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.components.internal.ComponentSupport;
import com.jgoodies.components.internal.JGTextComponent;
import com.jgoodies.components.internal.PromptSupport;
import com.jgoodies.components.internal.TextFieldSupport;
import com.jgoodies.components.util.ComponentUtils;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Objects;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTextArea.class */
public class JGTextArea extends JTextArea implements JGTextComponent {
    private final JTextArea promptArea;
    private static final String KEY_EVENT_HANDLER = "JGTextArea.eventHandler";
    private static final EventHandler EVENT_HANDLER = new EventHandler();

    public JGTextArea() {
        this.promptArea = new JTextArea();
        updateUI();
    }

    public JGTextArea(String text) {
        super(text);
        this.promptArea = new JTextArea();
        updateUI();
    }

    public JGTextArea(int rows, int columns) {
        super(rows, columns);
        this.promptArea = new JTextArea();
        updateUI();
    }

    public JGTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
        this.promptArea = new JTextArea();
        updateUI();
    }

    public JGTextArea(Document doc) {
        super(doc);
        this.promptArea = new JTextArea();
        updateUI();
    }

    public JGTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
        this.promptArea = new JTextArea();
        updateUI();
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final String getPrompt() {
        return PromptSupport.getPrompt(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPrompt(String prompt) {
        setPrompt(prompt, (Object[]) null);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPrompt(String str, Object... args) {
        String formatted = Strings.get(str, args);
        PromptSupport.setPrompt(this, formatted);
        this.promptArea.setText(formatted);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final int getPromptStyle() {
        return PromptSupport.getPromptStyle(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPromptStyle(int style) {
        PromptSupport.setPromptStyle(this, style);
        this.promptArea.setFont(getFont().deriveFont(style));
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final boolean isPromptVisibleWhenFocused() {
        return PromptSupport.isPromptVisibleWhenFocused(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setPromptVisibleWhenFocused(boolean newValue) {
        PromptSupport.setPromptVisibleWhenFocused(this, newValue);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final boolean isJGFocusTraversable() {
        return FocusTraversalUtils.isFocusTraversable(this).booleanValue();
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setJGFocusTraversable(Boolean value) {
        FocusTraversalUtils.setFocusTraversable((JTextComponent) this, value);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final Boolean getSelectOnFocusGainEnabled() {
        return TextFieldSupport.getSelectOnFocusGainEnabled(this);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setSelectOnFocusGainEnabled(Boolean b) {
        TextFieldSupport.setSelectOnFocusGainEnabled(this, b);
    }

    @Override // com.jgoodies.components.internal.JGTextComponent
    public final void setKeyboardAction(Action keyboardAction) {
        ComponentUtils.registerKeyboardAction(this, keyboardAction);
    }

    public void reshape(int x, int y, int w, int h) {
        super.reshape(x, y, w, h);
        this.promptArea.setBounds(x, y, w, h);
    }

    public void setComponentOrientation(ComponentOrientation orientation) {
        super.setComponentOrientation(orientation);
        this.promptArea.setComponentOrientation(orientation);
    }

    public void setTabSize(int size) {
        super.setTabSize(size);
        this.promptArea.setTabSize(size);
    }

    public String getToolTipText(MouseEvent e) {
        return TextFieldSupport.getToolTipText(this, e, super.getToolTipText());
    }

    protected void paintComponent(Graphics g) {
        if (PromptSupport.isPromptPainted(this)) {
            this.promptArea.paint(g);
        } else {
            Rectangle clip = g.getClip().getBounds();
            g.setColor(getBackground());
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
        }
        super.paintComponent(g);
    }

    public void updateUI() {
        super.updateUI();
        if (this.promptArea != null) {
            ComponentSupport.configureTransparentBackground(this);
            this.promptArea.updateUI();
            this.promptArea.setForeground(UIManager.getColor("textInactiveText"));
            this.promptArea.setFont(getFont().deriveFont(getPromptStyle()));
            ensureEventHandlerInstalled();
        }
    }

    private void ensureEventHandlerInstalled() {
        boolean installed = getClientProperty(KEY_EVENT_HANDLER) != null;
        if (!installed) {
            addKeyListener(EVENT_HANDLER);
            putClientProperty(KEY_EVENT_HANDLER, EVENT_HANDLER);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTextArea$EventHandler.class */
    public static final class EventHandler implements KeyListener {
        private EventHandler() {
        }

        public void keyPressed(KeyEvent e) {
            repaint(e);
        }

        public void keyReleased(KeyEvent e) {
            repaint(e);
        }

        public void keyTyped(KeyEvent e) {
            repaint(e);
        }

        private static void repaint(EventObject evt) {
            ((Component) evt.getSource()).repaint();
        }
    }

    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJGTextArea();
        }
        return this.accessibleContext;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGTextArea$AccessibleJGTextArea.class */
    private final class AccessibleJGTextArea extends AccessibleJTextArea {
        private AccessibleJGTextArea() {
            super(JGTextArea.this);
        }

        public String getAccessibleName() {
            String name = super.getAccessibleName();
            if (name != null) {
                return name;
            }
            return JGTextArea.this.getPrompt();
        }

        public String getAccessibleDescription() {
            String description = super.getAccessibleDescription();
            if (description != null) {
                return description;
            }
            String name = getAccessibleName();
            if (Objects.equals(name, JGTextArea.this.getPrompt())) {
                return null;
            }
            return JGTextArea.this.getPrompt();
        }
    }
}
