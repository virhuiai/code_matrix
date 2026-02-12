package com.jgoodies.layout.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.internal.AbstractButtonPanelBuilder;
import com.jgoodies.layout.layout.ColumnSpec;
import com.jgoodies.layout.layout.ConstantSize;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.layout.layout.FormSpec;
import com.jgoodies.layout.layout.FormSpecs;
import com.jgoodies.layout.layout.RowSpec;
import java.awt.LayoutManager;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/ButtonStackBuilder.class */
public final class ButtonStackBuilder extends AbstractButtonPanelBuilder<ButtonStackBuilder> {
    private static final ColumnSpec[] COL_SPECS = {FormSpecs.BUTTON_COLSPEC};
    private static final RowSpec[] ROW_SPECS = new RowSpec[0];

    public ButtonStackBuilder() {
        this(new JPanel((LayoutManager) null));
    }

    public ButtonStackBuilder(JPanel panel) {
        super(new FormLayout(COL_SPECS, ROW_SPECS), panel);
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addButton */
    public AbstractButtonPanelBuilder<ButtonStackBuilder> addButton2(JComponent button) {
        Preconditions.checkNotNull(button, Messages.MUST_NOT_BE_NULL, "button to add");
        getLayout().appendRow(FormSpecs.PREF_ROWSPEC);
        add(button);
        nextRow();
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addButton */
    public AbstractButtonPanelBuilder<ButtonStackBuilder> addButton2(JComponent... buttons) {
        super.addButton2(buttons);
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addButton */
    public AbstractButtonPanelBuilder<ButtonStackBuilder> addButton2(Action... actions) {
        super.addButton2(actions);
        return this;
    }

    public ButtonStackBuilder addComponent(JComponent component) {
        Preconditions.checkNotNull(component, Messages.MUST_NOT_BE_NULL, "component to add");
        getLayout().appendRow(FormSpecs.PREF_ROWSPEC);
        add(component);
        nextRow();
        return this;
    }

    public ButtonStackBuilder addGap(ConstantSize height) {
        getLayout().appendRow(new RowSpec(RowSpec.TOP, height, FormSpec.NO_GROW));
        nextRow();
        return this;
    }

    public ButtonStackBuilder addGrowingGap() {
        appendGrowingGapRow();
        nextRow();
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addRelatedGap */
    public AbstractButtonPanelBuilder<ButtonStackBuilder> addRelatedGap2() {
        appendRelatedComponentsGapRow();
        nextRow();
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addUnrelatedGap */
    public AbstractButtonPanelBuilder<ButtonStackBuilder> addUnrelatedGap2() {
        appendUnrelatedComponentsGapRow();
        nextRow();
        return this;
    }
}
