package com.jgoodies.looks;

import java.awt.Insets;
import javax.swing.plaf.InsetsUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/MicroLayout.class */
public final class MicroLayout {
    private final InsetsUIResource textInsets;
    private final InsetsUIResource wrappedTextInsets;
    private final InsetsUIResource comboBoxEditorInsets;
    private final Insets buttonBorderInsets;
    private final InsetsUIResource buttonMargin;
    private final int buttonPadding;
    private final int comboBorderSize;
    private final int comboPopupBorderSize;
    private final InsetsUIResource checkBoxMargin;
    private final InsetsUIResource menuItemMargin;
    private final InsetsUIResource menuMargin;
    private final InsetsUIResource popupMenuSeparatorMargin;

    public MicroLayout(InsetsUIResource textInsets, InsetsUIResource wrappedTextInsets, InsetsUIResource comboBoxEditorInsets, int comboBorderSize, int comboPopupBorderSize, Insets buttonBorderInsets, InsetsUIResource buttonMargin, int buttonPadding, InsetsUIResource checkBoxMargin, InsetsUIResource menuItemMargin, InsetsUIResource menuMargin, InsetsUIResource popupMenuSeparatorMargin) {
        this.textInsets = textInsets;
        this.wrappedTextInsets = wrappedTextInsets;
        this.comboBoxEditorInsets = comboBoxEditorInsets;
        this.buttonBorderInsets = buttonBorderInsets;
        this.buttonMargin = buttonMargin;
        this.buttonPadding = buttonPadding;
        this.comboBorderSize = comboBorderSize;
        this.comboPopupBorderSize = comboPopupBorderSize;
        this.checkBoxMargin = checkBoxMargin;
        this.menuItemMargin = menuItemMargin;
        this.menuMargin = menuMargin;
        this.popupMenuSeparatorMargin = popupMenuSeparatorMargin;
    }

    public Insets getButtonBorderInsets() {
        return this.buttonBorderInsets;
    }

    public InsetsUIResource getButtonMargin() {
        return this.buttonMargin;
    }

    public int getButtonPadding() {
        return this.buttonPadding;
    }

    public int getComboBorderSize() {
        return this.comboBorderSize;
    }

    public int getComboPopupBorderSize() {
        return this.comboPopupBorderSize;
    }

    public InsetsUIResource getComboBoxEditorInsets() {
        return this.comboBoxEditorInsets;
    }

    public InsetsUIResource getCheckBoxMargin() {
        return this.checkBoxMargin;
    }

    public InsetsUIResource getMenuItemMargin() {
        return this.menuItemMargin;
    }

    public InsetsUIResource getMenuMargin() {
        return this.menuMargin;
    }

    public InsetsUIResource getPopupMenuSeparatorMargin() {
        return this.popupMenuSeparatorMargin;
    }

    public InsetsUIResource getTextInsets() {
        return this.textInsets;
    }

    public InsetsUIResource getWrappedTextInsets() {
        return this.wrappedTextInsets;
    }
}
