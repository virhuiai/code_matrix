package com.jgoodies.components.internal;

import com.jgoodies.components.util.ComponentUtils;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/StaticEditorPane.class */
public class StaticEditorPane extends JEditorPane {
    public StaticEditorPane(String type) {
        this(type, null);
    }

    public StaticEditorPane(String type, String text) {
        super(type, text);
        super.setEditable(false);
        setRequestFocusEnabled(false);
        ComponentUtils.clearFocusTraversalKeys(this);
        Action insertBreakAction = getActionMap().get("insert-break");
        if (insertBreakAction != null) {
            getActionMap().put("insert-break", new TextActionWrapper(this, insertBreakAction));
        }
    }

    public void setEditable(boolean editable) {
    }

    public void updateUI() {
        super.updateUI();
        ComponentSupport.configureTransparentBackground(this);
        setMargin(new Insets(0, 0, 0, 0));
        if (getForeground() instanceof UIResource) {
            setForeground(UIManager.getColor("controlText"));
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/StaticEditorPane$TextActionWrapper.class */
    private static final class TextActionWrapper extends TextAction {
        private final JTextComponent editor;
        private final Action delegate;

        TextActionWrapper(JTextComponent editor, Action action) {
            super((String) action.getValue("Name"));
            this.editor = editor;
            this.delegate = action;
        }

        public void actionPerformed(ActionEvent e) {
            this.delegate.actionPerformed(e);
        }

        public boolean isEnabled() {
            return this.editor != null && this.editor.isEditable() && this.delegate.isEnabled();
        }
    }
}
