package com.jgoodies.components.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/AbstractUnderlineSupport.class */
public abstract class AbstractUnderlineSupport {
    final String propertyNameEnabled = getClass().getName() + ".enabled";
    protected final String keyUnderlinePainterTag = getClass().getName() + ".underlinePainterTag";
    protected final String keyWeakDocumentUpdateHandler = getClass().getName() + ".weakDocumentUpdateHandler";
    private final PropertyChangeListener documentChangeHandler = this::onDocumentChange;

    protected abstract DefaultHighlighter.DefaultHighlightPainter createHighlightPainter();

    public boolean isEnabled(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(this.propertyNameEnabled));
    }

    public void setEnabled(JTextComponent c, boolean newValue) {
        boolean oldValue = isEnabled(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(this.propertyNameEnabled, Boolean.valueOf(newValue));
        if (newValue) {
            install(c);
        } else {
            uninstall(c);
        }
        c.repaint();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void install(JTextComponent c) {
        c.addPropertyChangeListener("document", this.documentChangeHandler);
        installDocumentUpdateHandler(c);
        updateUnderline(c);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void uninstall(JTextComponent c) {
        c.removePropertyChangeListener("document", this.documentChangeHandler);
        uninstallDocumentUpdateHandler(c, c.getDocument());
        Object tag = c.getClientProperty(this.keyUnderlinePainterTag);
        if (tag != null) {
            c.getHighlighter().removeHighlight(tag);
            c.putClientProperty(this.keyUnderlinePainterTag, (Object) null);
        }
    }

    protected void updateUnderline(JTextComponent c) {
        Highlighter highlighter = c.getHighlighter();
        int length = c.getDocument().getLength();
        Object tag = c.getClientProperty(this.keyUnderlinePainterTag);
        try {
            if (tag == null) {
                c.putClientProperty(this.keyUnderlinePainterTag, highlighter.addHighlight(0, length, createHighlightPainter()));
            } else {
                highlighter.changeHighlight(tag, 0, length);
            }
        } catch (BadLocationException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/AbstractUnderlineSupport$DocumentUpdateHandler.class */
    public final class DocumentUpdateHandler implements DocumentListener {
        private final JTextComponent target;

        DocumentUpdateHandler(JTextComponent target) {
            this.target = target;
        }

        public void insertUpdate(DocumentEvent e) {
            AbstractUnderlineSupport.this.updateUnderline(this.target);
        }

        public void removeUpdate(DocumentEvent e) {
            AbstractUnderlineSupport.this.updateUnderline(this.target);
        }

        public void changedUpdate(DocumentEvent e) {
            AbstractUnderlineSupport.this.updateUnderline(this.target);
        }
    }

    private void installDocumentUpdateHandler(JTextComponent c) {
        DocumentListener handler = new DocumentUpdateHandler(c);
        c.getDocument().addDocumentListener(handler);
        c.putClientProperty(this.keyWeakDocumentUpdateHandler, new WeakReference(handler));
    }

    private void uninstallDocumentUpdateHandler(JTextComponent c, Document oldDocument) {
        DocumentListener handler = getDocumentUpdateHandler(c);
        if (handler != null) {
            oldDocument.removeDocumentListener(handler);
        }
        c.putClientProperty(this.keyWeakDocumentUpdateHandler, (Object) null);
    }

    private DocumentListener getDocumentUpdateHandler(JTextComponent c) {
        Object value = c.getClientProperty(this.keyWeakDocumentUpdateHandler);
        if (value == null || !(value instanceof WeakReference)) {
            return null;
        }
        return (DocumentListener) ((WeakReference) value).get();
    }

    private void onDocumentChange(PropertyChangeEvent evt) {
        JTextComponent target = (JTextComponent) evt.getSource();
        Document oldDocument = (Document) evt.getOldValue();
        uninstallDocumentUpdateHandler(target, oldDocument);
        installDocumentUpdateHandler(target);
    }
}
