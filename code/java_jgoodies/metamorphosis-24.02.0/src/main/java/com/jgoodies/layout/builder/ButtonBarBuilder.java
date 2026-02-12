package com.jgoodies.layout.builder;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.internal.AbstractButtonPanelBuilder;
import com.jgoodies.layout.layout.ColumnSpec;
import com.jgoodies.layout.layout.ConstantSize;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.layout.layout.FormSpecs;
import com.jgoodies.layout.layout.RowSpec;
import java.awt.LayoutManager;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/builder/ButtonBarBuilder.class */
public final class ButtonBarBuilder extends AbstractButtonPanelBuilder<ButtonBarBuilder> {
    private static final ColumnSpec[] COL_SPECS = new ColumnSpec[0];
    private static final RowSpec[] ROW_SPECS = {RowSpec.decode("center:pref")};

    public ButtonBarBuilder() {
        this(new JPanel((LayoutManager) null));
    }

    public ButtonBarBuilder(JPanel panel) {
        super(new FormLayout(COL_SPECS, ROW_SPECS), panel);
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addButton, reason: merged with bridge method [inline-methods] */
    public AbstractButtonPanelBuilder<ButtonBarBuilder> addButton2(JComponent button) {
        Preconditions.checkNotNull(button, Messages.MUST_NOT_BE_NULL, "button to add");
        getLayout().appendColumn(FormSpecs.BUTTON_COLSPEC);
        add(button);
        nextColumn();
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addButton, reason: merged with bridge method [inline-methods] */
    public AbstractButtonPanelBuilder<ButtonBarBuilder> addButton2(JComponent... buttons) {
        super.addButton2(buttons);
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addButton, reason: merged with bridge method [inline-methods] */
    public AbstractButtonPanelBuilder<ButtonBarBuilder> addButton2(Action... actions) {
        super.addButton2(actions);
        return this;
    }

    public ButtonBarBuilder addFixed(JComponent component) {
        getLayout().appendColumn(FormSpecs.PREF_COLSPEC);
        add(component);
        nextColumn();
        return this;
    }

    public ButtonBarBuilder addGrowing(JComponent component) {
        getLayout().appendColumn(FormSpecs.GROWING_BUTTON_COLSPEC);
        add(component);
        nextColumn();
        return this;
    }

    public ButtonBarBuilder addGap(ConstantSize width) {
        getLayout().appendColumn(ColumnSpec.createGap(width));
        nextColumn();
        return this;
    }

    public ButtonBarBuilder addGrowingGap() {
        appendGrowingGapColumn();
        nextColumn();
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addRelatedGap, reason: merged with bridge method [inline-methods] */
    public AbstractButtonPanelBuilder<ButtonBarBuilder> addRelatedGap2() {
        appendRelatedComponentsGapColumn();
        nextColumn();
        return this;
    }

    @Override // com.jgoodies.layout.internal.AbstractButtonPanelBuilder
    /* renamed from: addUnrelatedGap, reason: merged with bridge method [inline-methods] */
    public AbstractButtonPanelBuilder<ButtonBarBuilder> addUnrelatedGap2() {
        appendUnrelatedComponentsGapColumn();
        nextColumn();
        return this;
    }
}
