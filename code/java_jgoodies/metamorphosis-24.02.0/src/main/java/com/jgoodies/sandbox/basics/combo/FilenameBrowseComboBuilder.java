package com.jgoodies.sandbox.basics.combo;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.components.internal.TextFieldIcons;
import com.jgoodies.dialogs.core.CommandValue;
import com.jgoodies.dialogs.core.CoreDialogResources;
import com.jgoodies.dialogs.core.util.JSDLUtils;
import com.jgoodies.sandbox.completion.FilenameCompletionProcessor;
import com.jgoodies.search.CompletionManager;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/basics/combo/FilenameBrowseComboBuilder.class */
public final class FilenameBrowseComboBuilder {
    private final FieldButtonCombo combo = new FieldButtonComboBuilder().actionListener(new BrowseActionListener()).build();
    private final FilenameCompletionProcessor.Mode mode = FilenameCompletionProcessor.Mode.FILES_AND_DIRECTORIES;
    private final FilenameCompletionProcessor completionProcessor = new FilenameCompletionProcessor(this.mode);

    public FilenameBrowseComboBuilder() {
        new CompletionManager(this.completionProcessor).install(this.combo.getField());
        defaultButtonText();
    }

    public FilenameBrowseComboBuilder autoCompletionEnabled(boolean b) {
        getManager().setAutoCompletionEnabled(b);
        return this;
    }

    public FilenameBrowseComboBuilder completionPrototypeDisplayValue(String prototypeDisplayValue) {
        getManager().setCompletionPrototypeDisplayValue(prototypeDisplayValue);
        return this;
    }

    public FilenameBrowseComboBuilder defaultButtonText() {
        this.combo.setButtonText(CommandValue.BROWSE.getMarkedText());
        return this;
    }

    public FilenameBrowseComboBuilder fieldIcon(Icon icon) {
        this.combo.setFieldIcon(icon);
        return this;
    }

    public FilenameBrowseComboBuilder fieldText(String text) {
        this.combo.setFieldText(text);
        return this;
    }

    public FilenameBrowseComboBuilder fieldPrompt(String prompt) {
        this.combo.setFieldPrompt(prompt);
        return this;
    }

    public FilenameBrowseComboBuilder mode(FilenameCompletionProcessor.Mode mode) {
        this.completionProcessor.setMode((FilenameCompletionProcessor.Mode) Preconditions.checkNotNull(mode, Messages.MUST_NOT_BE_NULL, "mode"));
        return this;
    }

    public FilenameBrowseComboBuilder prefixCompletionEnabled(boolean b) {
        getManager().setPrefixCompletionEnabled(b);
        return this;
    }

    public FilenameBrowseComboBuilder searchIcon() {
        fieldIcon(TextFieldIcons.getSearchIcon());
        return this;
    }

    public FilenameBrowseComboBuilder shortButtonText() {
        this.combo.setButtonText(CoreDialogResources.getString("common.browse.short"));
        return this;
    }

    public FieldButtonCombo build() {
        return this.combo;
    }

    private CompletionManager getManager() {
        return CompletionManager.getManager(this.combo.getField());
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/basics/combo/FilenameBrowseComboBuilder$BrowseActionListener.class */
    final class BrowseActionListener implements ActionListener {
        BrowseActionListener() {
        }

        public void actionPerformed(ActionEvent evt) {
            Frame windowFor = JSDLUtils.getWindowFor(evt);
            FileDialog dialog = windowFor instanceof Frame ? new FileDialog(windowFor) : new FileDialog((Dialog) windowFor);
            dialog.setTitle("Choose a File");
            dialog.setResizable(true);
            dialog.setVisible(true);
            FilenameBrowseComboBuilder.this.combo.setFieldText(dialog.getFile());
        }
    }
}
