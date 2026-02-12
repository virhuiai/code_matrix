package com.jgoodies.looks.windows;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.looks.common.ExtBasicSpinnerLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsSpinnerUI.class */
public final class WindowsSpinnerUI extends com.sun.java.swing.plaf.windows.WindowsSpinnerUI {
    public static ComponentUI createUI(JComponent b) {
        return new WindowsSpinnerUI();
    }

    protected Component createPreviousButton() {
        if (SystemUtils.IS_LAF_WINDOWS_XP_ENABLED) {
            return super.createPreviousButton();
        }
        WindowsArrowButton windowsArrowButton = new WindowsArrowButton(5);
        installPreviousButtonListeners(windowsArrowButton);
        return windowsArrowButton;
    }

    protected Component createNextButton() {
        if (SystemUtils.IS_LAF_WINDOWS_XP_ENABLED) {
            return super.createNextButton();
        }
        WindowsArrowButton windowsArrowButton = new WindowsArrowButton(1);
        installNextButtonListeners(windowsArrowButton);
        return windowsArrowButton;
    }

    protected JComponent createEditor() {
        JComponent editor = this.spinner.getEditor();
        configureEditorBorder(editor);
        return editor;
    }

    protected LayoutManager createLayout() {
        return new ExtBasicSpinnerLayout();
    }

    protected void replaceEditor(JComponent oldEditor, JComponent newEditor) {
        this.spinner.remove(oldEditor);
        configureEditorBorder(newEditor);
        this.spinner.add(newEditor, "Editor");
    }

    private static void configureEditorBorder(JComponent editor) {
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            JFormattedTextField textField = defaultEditor.getTextField();
            Insets insets = UIManager.getInsets("Spinner.defaultEditorInsets");
            textField.setBorder(new EmptyBorder(insets));
            return;
        }
        if ((editor instanceof JPanel) && editor.getBorder() == null && editor.getComponentCount() > 0) {
            JComponent editorField = editor.getComponent(0);
            Insets insets2 = UIManager.getInsets("Spinner.defaultEditorInsets");
            editorField.setBorder(new EmptyBorder(insets2));
        }
    }
}
