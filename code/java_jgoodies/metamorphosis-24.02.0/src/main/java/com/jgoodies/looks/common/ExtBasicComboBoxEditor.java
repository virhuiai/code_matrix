package com.jgoodies.looks.common;

import java.awt.KeyboardFocusManager;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicComboBoxEditor.class */
public final class ExtBasicComboBoxEditor extends BasicComboBoxEditor.UIResource {
    private final boolean isTableCellEditor;

    public ExtBasicComboBoxEditor(boolean isTableCellEditor) {
        this.isTableCellEditor = isTableCellEditor;
    }

    protected JTextField createEditorComponent() {
        JTextField editor = super.createEditorComponent();
        editor.setColumns(UIManager.getInt("ComboBox.editorColumns"));
        if (this.isTableCellEditor) {
            editor.setMargin(UIManager.getInsets("ComboBox.tableEditorInsets"));
        }
        Border border = UIManager.getBorder("ComboBox.editorBorder");
        if (border != null) {
            editor.setBorder(border);
        }
        return editor;
    }

    public void setItem(Object item) {
        super.setItem(item);
        Object focus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focus == this.editor || focus == this.editor.getParent()) {
            this.editor.selectAll();
        }
    }
}
