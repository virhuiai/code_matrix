package com.jgoodies.looks.plastic;

import com.jgoodies.looks.MicroLayout;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticModernIconFactory;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticMetroLookAndFeel.class */
public class PlasticMetroLookAndFeel extends PlasticLookAndFeel {
    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public String getName() {
        return "JGoodies Plastic Metro";
    }

    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public String getDescription() {
        return "The JGoodies Plastic Metro Look & Feel";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {"SpinnerUI", "com.jgoodies.looks.plastic.PlasticModernSpinnerUI", "ToolTipUI", "com.jgoodies.looks.common.ExtBasicToolTipUI"};
        table.putDefaults(uiDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        Object menuBarBorder = PlasticBorders.getSeparatorBorder();
        Object toolBarBorder = new BasicBorders.MarginBorder();
        MicroLayout microLayout = getMicroLayoutPolicy().getMicroLayout(getName(), table);
        Insets buttonBorderInsets = microLayout.getButtonBorderInsets();
        Object buttonBorder = PlasticModernBorders.getButtonBorder(buttonBorderInsets);
        Object toggleButtonBorder = PlasticModernBorders.getToggleButtonBorder(buttonBorderInsets);
        Object comboBoxButtonBorder = PlasticModernBorders.getComboBoxArrowButtonBorder();
        Border comboBoxEditorBorder = PlasticModernBorders.getComboBoxEditorBorder();
        Object scrollPaneBorder = PlasticModernBorders.getScrollPaneBorder();
        Object textFieldBorder = PlasticModernBorders.getTextFieldBorder();
        Object spinnerBorder = PlasticModernBorders.getSpinnerBorder();
        Object spinnerButtonBorder = PlasticModernBorders.getSpinnerButtonBorder();
        InsetsUIResource comboBoxEditorInsets = microLayout.getComboBoxEditorInsets();
        Insets comboEditorBorderInsets = comboBoxEditorBorder.getBorderInsets((Component) null);
        int comboBorderSize = comboEditorBorderInsets.left;
        int comboPopupBorderSize = microLayout.getComboPopupBorderSize();
        int comboRendererGap = (((Insets) comboBoxEditorInsets).left + comboBorderSize) - comboPopupBorderSize;
        Object comboRendererBorder = new EmptyBorder(1, comboRendererGap, 1, comboRendererGap);
        Object comboTableEditorInsets = new Insets(0, 0, 0, 0);
        Object focusInsets = new Insets(3, 3, 3, 3);
        Object[] defaults = {"Button.border", buttonBorder, "Button.focusInsets", focusInsets, "CheckBox.icon", PlasticModernIconFactory.getCheckBoxIcon(), PlasticModernIconFactory.State.DESELECTED_DISABLED.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_DISABLED), PlasticModernIconFactory.State.DESELECTED_PLAIN.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_PLAIN), PlasticModernIconFactory.State.DESELECTED_ROLLOVER.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_ROLLOVER), PlasticModernIconFactory.State.DESELECTED_PRESSED.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_PRESSED), PlasticModernIconFactory.State.SELECTED_DISABLED.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_DISABLED), PlasticModernIconFactory.State.SELECTED_PLAIN.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_PLAIN), PlasticModernIconFactory.State.SELECTED_ROLLOVER.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_ROLLOVER), PlasticModernIconFactory.State.SELECTED_PRESSED.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_PRESSED), "ComboBox.arrowIcon", PlasticModernIconFactory.getComboBoxButtonIcon(), "ComboBox.arrowIcon.enabled", makeIcon(PlasticMetroLookAndFeel.class, "icons/metro/chevron-light-down-plain.png"), "ComboBox.arrowIcon.disabled", makeIcon(PlasticMetroLookAndFeel.class, "icons/metro/chevron-light-down-disabled.png"), "ComboBox.arrowButtonBorder", comboBoxButtonBorder, "ComboBox.editorBorder", comboBoxEditorBorder, "ComboBox.editorBorderInsets", comboEditorBorderInsets, "ComboBox.tableEditorInsets", comboTableEditorInsets, "ComboBox.rendererBorder", comboRendererBorder, "ComboBox.focusInsets", new Insets(2, 2, 2, 2), "FormattedTextField.border", textFieldBorder, "MenuBar.border", menuBarBorder, "PasswordField.border", textFieldBorder, "RadioButton.icon", PlasticModernIconFactory.getRadioButtonIcon(), PlasticModernIconFactory.State.DESELECTED_DISABLED.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_DISABLED), PlasticModernIconFactory.State.DESELECTED_PLAIN.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_PLAIN), PlasticModernIconFactory.State.DESELECTED_ROLLOVER.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_ROLLOVER), PlasticModernIconFactory.State.DESELECTED_PRESSED.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_PRESSED), PlasticModernIconFactory.State.SELECTED_DISABLED.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_DISABLED), PlasticModernIconFactory.State.SELECTED_PLAIN.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_PLAIN), PlasticModernIconFactory.State.SELECTED_ROLLOVER.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_ROLLOVER), PlasticModernIconFactory.State.SELECTED_PRESSED.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_PRESSED), "ScrollPane.border", scrollPaneBorder, "Spinner.border", spinnerBorder, "Spinner.arrowButtonBorder", spinnerButtonBorder, "Table.scrollPaneBorder", scrollPaneBorder, "TextField.border", textFieldBorder, "ToggleButton.border", toggleButtonBorder, "ToggleButton.focusInsets", focusInsets, "ToolBar.border", toolBarBorder, Options.TREE_PAINT_LINES_KEY, Boolean.FALSE, "Tree.expandedIcon", makeIcon(PlasticMetroLookAndFeel.class, "icons/metro/tree-expanded.png"), "Tree.collapsedIcon", makeIcon(PlasticMetroLookAndFeel.class, "icons/metro/tree-collapsed.png"), "TextEditor.borderPaintsFocus", Boolean.TRUE};
        table.putDefaults(defaults);
    }

    private static Object checkIcon(PlasticModernIconFactory.State state) {
        return makeIcon(PlasticMetroLookAndFeel.class, state.iconPath("metro", "check"));
    }

    private static Object radioIcon(PlasticModernIconFactory.State state) {
        return makeIcon(PlasticMetroLookAndFeel.class, state.iconPath("metro", "radio"));
    }
}
