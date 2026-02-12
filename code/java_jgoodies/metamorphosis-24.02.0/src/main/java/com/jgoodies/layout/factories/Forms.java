package com.jgoodies.layout.factories;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.builder.ButtonBarBuilder;
import com.jgoodies.layout.builder.ButtonStackBuilder;
import com.jgoodies.layout.builder.FormBuilder;
import com.jgoodies.layout.factories.Paddings;
import com.jgoodies.layout.layout.FormSpecs;
import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/Forms.class */
public final class Forms {
    private Forms() {
    }

    public static JComponent single(String columnSpec, String rowSpec, JComponent component) {
        Preconditions.checkNotBlank(columnSpec, Messages.MUST_NOT_BE_BLANK, "column specification");
        Preconditions.checkNotBlank(rowSpec, Messages.MUST_NOT_BE_BLANK, "row specification");
        Preconditions.checkNotNull(component, Messages.MUST_NOT_BE_NULL, "component");
        return new FormBuilder().columns(columnSpec, new Object[0]).rows(rowSpec, new Object[0]).add((Component) component).xy(1, 1).build();
    }

    public static JComponent centered(JComponent component) {
        return single("center:pref:grow", "c:p:g", component);
    }

    public static JComponent padded(JComponent component, Paddings.Padding padding) {
        JComponent container = single("fill:pref", "f:p", component);
        container.setBorder(padding);
        return container;
    }

    public static JComponent padded(JComponent component, String paddingSpec, Object... args) {
        return padded(component, Paddings.createPadding(paddingSpec, args));
    }

    public static JComponent paddedGrow(JComponent component, Paddings.Padding padding) {
        JComponent container = single("fill:default:grow", "f:d:g", component);
        container.setBorder(padding);
        return container;
    }

    public static JComponent paddedGrow(JComponent component, String paddingSpec, Object... args) {
        return paddedGrow(component, Paddings.createPadding(paddingSpec, args));
    }

    public static JComponent horizontal(String gapColSpec, JComponent... components) {
        Preconditions.checkNotBlank(gapColSpec, Messages.MUST_NOT_BE_BLANK, "gap column specification");
        Preconditions.checkNotNull(components, Messages.MUST_NOT_BE_NULL, "component array");
        Preconditions.checkArgument(components.length > 1, "You must provide more than one component.");
        FormBuilder builder = new FormBuilder().columns("%1$s*(pref, %2$s), pref", Integer.valueOf(components.length - 1), gapColSpec).rows(FormSpecs.PREF_ROWSPEC);
        int column = 1;
        for (JComponent component : components) {
            builder.add((Component) component).xy(column, 1);
            column += 2;
        }
        return builder.build();
    }

    public static JComponent vertical(String gapRowSpec, JComponent... components) {
        Preconditions.checkNotBlank(gapRowSpec, Messages.MUST_NOT_BE_BLANK, "gap row specification");
        Preconditions.checkNotNull(components, Messages.MUST_NOT_BE_NULL, "component array");
        Preconditions.checkArgument(components.length > 1, "You must provide more than one component.");
        FormBuilder builder = new FormBuilder().columns(FormSpecs.PREF_COLSPEC).rows("%1$s*(p, %2$s), p", Integer.valueOf(components.length - 1), gapRowSpec);
        int row = 1;
        for (JComponent component : components) {
            builder.add((Component) component).xy(1, row);
            row += 2;
        }
        return builder.build();
    }

    public static JComponent buttonBar(JComponent... buttons) {
        return new ButtonBarBuilder().addButton2(buttons).build();
    }

    public static JComponent buttonStack(JComponent... buttons) {
        return new ButtonStackBuilder().addButton2(buttons).build();
    }

    public static JComponent checkBoxBar(JCheckBox... checkBoxes) {
        return buildGroupedButtonBar(checkBoxes);
    }

    public static JComponent checkBoxStack(JCheckBox... checkBoxes) {
        return buildGroupedButtonStack(checkBoxes);
    }

    public static JComponent radioButtonBar(JRadioButton... radioButtons) {
        return buildGroupedButtonBar(radioButtons);
    }

    public static JComponent radioButtonStack(JRadioButton... radioButtons) {
        return buildGroupedButtonStack(radioButtons);
    }

    private static JComponent buildGroupedButtonBar(AbstractButton... buttons) {
        Preconditions.checkArgument(buttons.length > 1, "You must provide more than one button.");
        FormBuilder builder = new FormBuilder().columns("pref, %s*($rgap, pref)", Integer.valueOf(buttons.length - 1)).rows(FormSpecs.PREF_ROWSPEC);
        int column = 1;
        for (AbstractButton button : buttons) {
            builder.add((Component) button).xy(column, 1);
            column += 2;
        }
        builder.focusGroup(buttons);
        return builder.build();
    }

    private static JComponent buildGroupedButtonStack(AbstractButton... buttons) {
        Preconditions.checkArgument(buttons.length > 1, "You must provide more than one button.");
        FormBuilder builder = new FormBuilder().columns(FormSpecs.PREF_COLSPEC).rows("p, %s*(0, p)", Integer.valueOf(buttons.length - 1));
        int row = 1;
        for (AbstractButton button : buttons) {
            builder.add((Component) button).xy(1, row, "left, center");
            row += 2;
        }
        builder.focusGroup(buttons);
        return builder.build();
    }
}
