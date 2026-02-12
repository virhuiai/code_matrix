package com.jgoodies.looks.plastic;

import com.jgoodies.looks.common.ExtBasicSpinnerLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticSpinnerUI.class */
public class PlasticSpinnerUI extends BasicSpinnerUI {
    public static ComponentUI createUI(JComponent b) {
        return new PlasticSpinnerUI();
    }

    protected Component createPreviousButton() {
        Component c = createArrowButton(5);
        c.setName("Spinner.previousButton");
        installPreviousButtonListeners(c);
        return c;
    }

    protected Component createNextButton() {
        Component c = createArrowButton(1);
        c.setName("Spinner.nextButton");
        installNextButtonListeners(c);
        return c;
    }

    protected Component createArrowButton(int direction) {
        JButton b = createArrowButton0(direction);
        Border buttonBorder = UIManager.getBorder("Spinner.arrowButtonBorder");
        if (buttonBorder instanceof UIResource) {
            b.setBorder(new CompoundBorder(buttonBorder, (Border) null));
        } else {
            b.setBorder(buttonBorder);
        }
        b.setInheritsPopupMenu(true);
        return b;
    }

    protected JButton createArrowButton0(int direction) {
        return new SpinnerArrowButton(direction);
    }

    protected LayoutManager createLayout() {
        return new ExtBasicSpinnerLayout();
    }

    protected JComponent createEditor() {
        JComponent editor = this.spinner.getEditor();
        configureEditorBorder(editor);
        return editor;
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticSpinnerUI$SpinnerArrowButton.class */
    public static final class SpinnerArrowButton extends PlasticArrowButton {
        private SpinnerArrowButton(int direction) {
            super(direction, UIManager.getInt("ScrollBar.width"), true);
        }

        @Override // com.jgoodies.looks.plastic.PlasticArrowButton
        protected int calculateArrowHeight(int height, int width) {
            int arrowHeight = Math.min((height - 4) / 3, (width - 4) / 3);
            return Math.max(arrowHeight, 3);
        }

        @Override // com.jgoodies.looks.plastic.PlasticArrowButton
        protected int calculateArrowOffset() {
            return 1;
        }

        @Override // com.jgoodies.looks.plastic.PlasticArrowButton
        protected boolean isPaintingNorthBottom() {
            return true;
        }
    }
}
