package com.jgoodies.layout.util;

import com.jgoodies.layout.layout.ConstantSize;
import com.jgoodies.layout.layout.Size;
import com.jgoodies.layout.layout.Sizes;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/util/WindowsLayoutStyle.class */
public final class WindowsLayoutStyle extends LayoutStyle {
    static final WindowsLayoutStyle INSTANCE = new WindowsLayoutStyle();
    private static final Size BUTTON_WIDTH = Sizes.dlu(50);
    private static final Size BUTTON_HEIGHT = Sizes.DLU14;
    private static final ConstantSize DIALOG_MARGIN_X = Sizes.DLU7;
    private static final ConstantSize DIALOG_MARGIN_Y = Sizes.DLU7;
    private static final ConstantSize TABBED_DIALOG_MARGIN_X = Sizes.DLU4;
    private static final ConstantSize TABBED_DIALOG_MARGIN_Y = Sizes.DLU4;
    private static final ConstantSize LABEL_COMPONENT_PADX = Sizes.DLU3;
    private static final ConstantSize RELATED_COMPONENTS_PADX = Sizes.DLU4;
    private static final ConstantSize UNRELATED_COMPONENTS_PADX = Sizes.DLU7;
    private static final ConstantSize LABEL_COMPONENT_PADY = Sizes.DLU2;
    private static final ConstantSize RELATED_COMPONENTS_PADY = Sizes.DLU4;
    private static final ConstantSize UNRELATED_COMPONENTS_PADY = Sizes.DLU7;
    private static final ConstantSize NARROW_LINE_PAD = Sizes.DLU2;
    private static final ConstantSize LINE_PAD = Sizes.DLU3;
    private static final ConstantSize PARAGRAPH_PAD = Sizes.DLU9;
    private static final ConstantSize BUTTON_BAR_PAD = Sizes.DLU5;

    private WindowsLayoutStyle() {
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public Size getDefaultButtonWidth() {
        return BUTTON_WIDTH;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public Size getDefaultButtonHeight() {
        return BUTTON_HEIGHT;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getDialogMarginX() {
        return DIALOG_MARGIN_X;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getDialogMarginY() {
        return DIALOG_MARGIN_Y;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getTabbedDialogMarginX() {
        return TABBED_DIALOG_MARGIN_X;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getTabbedDialogMarginY() {
        return TABBED_DIALOG_MARGIN_Y;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getLabelComponentPadX() {
        return LABEL_COMPONENT_PADX;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getLabelComponentPadY() {
        return LABEL_COMPONENT_PADY;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getRelatedComponentsPadX() {
        return RELATED_COMPONENTS_PADX;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getRelatedComponentsPadY() {
        return RELATED_COMPONENTS_PADY;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getUnrelatedComponentsPadX() {
        return UNRELATED_COMPONENTS_PADX;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getUnrelatedComponentsPadY() {
        return UNRELATED_COMPONENTS_PADY;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getNarrowLinePad() {
        return NARROW_LINE_PAD;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getLinePad() {
        return LINE_PAD;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getParagraphPad() {
        return PARAGRAPH_PAD;
    }

    @Override // com.jgoodies.layout.util.LayoutStyle
    public ConstantSize getButtonBarPad() {
        return BUTTON_BAR_PAD;
    }
}
