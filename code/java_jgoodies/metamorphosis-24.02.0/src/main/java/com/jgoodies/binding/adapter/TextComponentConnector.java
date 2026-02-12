package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/TextComponentConnector.class */
public final class TextComponentConnector {
    private final ValueModel subject;
    private final JTextComponent textComponent;
    private Document document;
    private final SubjectValueChangeHandler subjectValueChangeHandler;
    private final DocumentListener textChangeHandler;
    private final PropertyChangeListener documentChangeHandler;

    public TextComponentConnector(ValueModel subject, JTextArea textArea) {
        this(subject, (JTextComponent) textArea);
    }

    public TextComponentConnector(ValueModel subject, JTextField textField) {
        this(subject, (JTextComponent) textField);
    }

    private TextComponentConnector(ValueModel subject, JTextComponent textComponent) {
        this.subject = (ValueModel) Preconditions.checkNotNull(subject, Messages.MUST_NOT_BE_NULL, BufferedValueModel.PROPERTY_SUBJECT);
        this.textComponent = (JTextComponent) Preconditions.checkNotNull(textComponent, Messages.MUST_NOT_BE_NULL, "text component");
        this.subjectValueChangeHandler = new SubjectValueChangeHandler();
        this.textChangeHandler = new TextChangeHandler();
        this.document = textComponent.getDocument();
        reregisterTextChangeHandler(null, this.document);
        subject.addValueChangeListener(this.subjectValueChangeHandler);
        this.documentChangeHandler = this::onDocumentChanged;
        textComponent.addPropertyChangeListener("document", this.documentChangeHandler);
    }

    public static void connect(ValueModel subject, JTextArea textArea) {
        new TextComponentConnector(subject, textArea);
    }

    public static void connect(ValueModel subject, JTextField textField) {
        new TextComponentConnector(subject, textField);
    }

    public void updateSubject() {
        setSubjectText(getDocumentText());
    }

    public void updateTextComponent() {
        setDocumentTextSilently(getSubjectText());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getDocumentText() {
        return this.textComponent.getText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDocumentTextSilently(String newText) {
        this.textComponent.getDocument().removeDocumentListener(this.textChangeHandler);
        this.textComponent.setText(newText);
        this.textComponent.setCaretPosition(0);
        this.textComponent.getDocument().addDocumentListener(this.textChangeHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getSubjectText() {
        String str = (String) this.subject.getValue();
        return str == null ? "" : str;
    }

    private void setSubjectText(String newText) {
        this.subjectValueChangeHandler.setUpdateLater(true);
        try {
            this.subject.setValue(newText);
        } finally {
            this.subjectValueChangeHandler.setUpdateLater(false);
        }
    }

    private void reregisterTextChangeHandler(Document oldDocument, Document newDocument) {
        if (oldDocument != null) {
            oldDocument.removeDocumentListener(this.textChangeHandler);
        }
        if (newDocument != null) {
            newDocument.addDocumentListener(this.textChangeHandler);
        }
    }

    public synchronized void release() {
        reregisterTextChangeHandler(this.document, null);
        this.subject.removeValueChangeListener(this.subjectValueChangeHandler);
        this.textComponent.removePropertyChangeListener("document", this.documentChangeHandler);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/TextComponentConnector$TextChangeHandler.class */
    private final class TextChangeHandler implements DocumentListener {
        private TextChangeHandler() {
        }

        public void insertUpdate(DocumentEvent e) {
            TextComponentConnector.this.updateSubject();
        }

        public void removeUpdate(DocumentEvent e) {
            TextComponentConnector.this.updateSubject();
        }

        public void changedUpdate(DocumentEvent e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/TextComponentConnector$SubjectValueChangeHandler.class */
    public final class SubjectValueChangeHandler implements PropertyChangeListener {
        private boolean updateLater;

        private SubjectValueChangeHandler() {
        }

        void setUpdateLater(boolean updateLater) {
            this.updateLater = updateLater;
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            String oldText = TextComponentConnector.this.getDocumentText();
            Object newValue = evt.getNewValue();
            String newText = newValue == null ? TextComponentConnector.this.getSubjectText() : (String) newValue;
            if (Objects.equals(oldText, newText)) {
                return;
            }
            if (!this.updateLater) {
                TextComponentConnector.this.setDocumentTextSilently(newText);
            } else {
                EventQueue.invokeLater(() -> {
                    TextComponentConnector.this.setDocumentTextSilently(newText);
                });
            }
        }
    }

    private void onDocumentChanged(PropertyChangeEvent evt) {
        Document oldDocument = this.document;
        Document newDocument = this.textComponent.getDocument();
        reregisterTextChangeHandler(oldDocument, newDocument);
        this.document = newDocument;
    }
}
