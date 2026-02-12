package com.jgoodies.looks.plastic;

import com.jgoodies.looks.MicroLayout;
import com.jgoodies.looks.plastic.PlasticModernIconFactory;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.InsetsUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticAeroLookAndFeel.class */
public class PlasticAeroLookAndFeel extends PlasticLookAndFeel {
    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public String getName() {
        return "JGoodies Plastic Aero";
    }

    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public String getDescription() {
        return "The JGoodies Plastic Aero Look and Feel";
    }

    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    protected boolean is3DEnabled() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        Object[] uiDefaults = {"SpinnerUI", "com.jgoodies.looks.plastic.PlasticModernSpinnerUI"};
        table.putDefaults(uiDefaults);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.looks.plastic.PlasticLookAndFeel
    public void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        Object menuBarBorder = PlasticBorders.getThinRaisedBorder();
        Object toolBarBorder = PlasticBorders.getThinRaisedBorder();
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
        Object[] defaults = {"Button.border", buttonBorder, "Button.focusInsets", focusInsets, "CheckBox.icon", PlasticModernIconFactory.getCheckBoxIcon(), PlasticModernIconFactory.State.DESELECTED_DISABLED.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_DISABLED), PlasticModernIconFactory.State.DESELECTED_PLAIN.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_PLAIN), PlasticModernIconFactory.State.DESELECTED_ROLLOVER.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_ROLLOVER), PlasticModernIconFactory.State.DESELECTED_PRESSED.checkKey(), checkIcon(PlasticModernIconFactory.State.DESELECTED_PRESSED), PlasticModernIconFactory.State.SELECTED_DISABLED.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_DISABLED), PlasticModernIconFactory.State.SELECTED_PLAIN.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_PLAIN), PlasticModernIconFactory.State.SELECTED_ROLLOVER.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_ROLLOVER), PlasticModernIconFactory.State.SELECTED_PRESSED.checkKey(), checkIcon(PlasticModernIconFactory.State.SELECTED_PRESSED), "ComboBox.arrowButtonBorder", comboBoxButtonBorder, "ComboBox.editorBorder", comboBoxEditorBorder, "ComboBox.editorBorderInsets", comboEditorBorderInsets, "ComboBox.tableEditorInsets", comboTableEditorInsets, "ComboBox.rendererBorder", comboRendererBorder, "ComboBox.focusInsets", new Insets(2, 2, 2, 2), "FormattedTextField.border", textFieldBorder, "MenuBar.border", menuBarBorder, "PasswordField.border", textFieldBorder, "RadioButton.icon", PlasticModernIconFactory.getRadioButtonIcon(), PlasticModernIconFactory.State.DESELECTED_DISABLED.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_DISABLED), PlasticModernIconFactory.State.DESELECTED_PLAIN.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_PLAIN), PlasticModernIconFactory.State.DESELECTED_ROLLOVER.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_ROLLOVER), PlasticModernIconFactory.State.DESELECTED_PRESSED.radioKey(), radioIcon(PlasticModernIconFactory.State.DESELECTED_PRESSED), PlasticModernIconFactory.State.SELECTED_DISABLED.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_DISABLED), PlasticModernIconFactory.State.SELECTED_PLAIN.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_PLAIN), PlasticModernIconFactory.State.SELECTED_ROLLOVER.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_ROLLOVER), PlasticModernIconFactory.State.SELECTED_PRESSED.radioKey(), radioIcon(PlasticModernIconFactory.State.SELECTED_PRESSED), "ScrollPane.border", scrollPaneBorder, "Spinner.border", spinnerBorder, "Spinner.arrowButtonBorder", spinnerButtonBorder, "Table.scrollPaneBorder", scrollPaneBorder, "TextField.border", textFieldBorder, "ToggleButton.border", toggleButtonBorder, "ToggleButton.focusInsets", focusInsets, "ToolBar.border", toolBarBorder, "TextEditor.borderPaintsFocus", Boolean.TRUE};
        table.putDefaults(defaults);
    }

    private static Object checkIcon(PlasticModernIconFactory.State state) {
        return makeIcon(PlasticAeroLookAndFeel.class, state.iconPath("aero", "check"));
    }

    private static Object radioIcon(PlasticModernIconFactory.State state) {
        return makeIcon(PlasticAeroLookAndFeel.class, state.iconPath("aero", "radio"));
    }
}
