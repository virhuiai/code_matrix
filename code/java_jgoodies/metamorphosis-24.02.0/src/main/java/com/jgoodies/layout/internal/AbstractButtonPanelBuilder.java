package com.jgoodies.layout.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.focus.FocusTraversalUtils;
import com.jgoodies.layout.internal.AbstractButtonPanelBuilder;
import com.jgoodies.layout.layout.ColumnSpec;
import com.jgoodies.layout.layout.FormLayout;
import com.jgoodies.layout.layout.FormSpecs;
import com.jgoodies.layout.layout.RowSpec;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/internal/AbstractButtonPanelBuilder.class */
public abstract class AbstractButtonPanelBuilder<B extends AbstractButtonPanelBuilder<B>> extends AbstractBuilder<B> {
    private boolean leftToRight;
    protected boolean focusGrouped;

    /* renamed from: addButton */
    protected abstract AbstractButtonPanelBuilder<B> addButton2(JComponent jComponent);

    /* renamed from: addRelatedGap */
    protected abstract AbstractButtonPanelBuilder<B> addRelatedGap2();

    /* renamed from: addUnrelatedGap */
    protected abstract AbstractButtonPanelBuilder<B> addUnrelatedGap2();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractButtonPanelBuilder(FormLayout layout, JPanel container) {
        super(layout, container);
        this.focusGrouped = false;
        opaque(false);
        ComponentOrientation orientation = container.getComponentOrientation();
        this.leftToRight = orientation.isLeftToRight() || !orientation.isHorizontal();
    }

    @Override // com.jgoodies.layout.internal.AbstractBuilder
    public final JPanel build() {
        if (!this.focusGrouped) {
            List<AbstractButton> buttons = new ArrayList<>();
            for (AbstractButton abstractButton : getPanel().getComponents()) {
                if (abstractButton instanceof AbstractButton) {
                    buttons.add(abstractButton);
                }
            }
            FocusTraversalUtils.group((List<? extends AbstractButton>) buttons);
            this.focusGrouped = true;
        }
        return getPanel();
    }

    public final boolean isLeftToRight() {
        return this.leftToRight;
    }

    public final void setLeftToRight(boolean b) {
        this.leftToRight = b;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void nextColumn() {
        nextColumn(1);
    }

    private void nextColumn(int columns) {
        this.currentCellConstraints.gridX += columns * getColumnIncrementSign();
    }

    protected final int getColumn() {
        return this.currentCellConstraints.gridX;
    }

    protected final int getRow() {
        return this.currentCellConstraints.gridY;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void nextRow() {
        nextRow(1);
    }

    private void nextRow(int rows) {
        this.currentCellConstraints.gridY += rows;
    }

    protected final void appendColumn(ColumnSpec columnSpec) {
        getLayout().appendColumn(columnSpec);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void appendGrowingGapColumn() {
        appendColumn(FormSpecs.GROWING_GAP_COLSPEC);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void appendRelatedComponentsGapColumn() {
        appendColumn(FormSpecs.RELATED_GAP_COLSPEC);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void appendUnrelatedComponentsGapColumn() {
        appendColumn(FormSpecs.UNRELATED_GAP_COLSPEC);
    }

    protected final void appendRow(RowSpec rowSpec) {
        getLayout().appendRow(rowSpec);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void appendGrowingGapRow() {
        appendRow(FormSpecs.GROWING_GAP_ROWSPEC);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void appendRelatedComponentsGapRow() {
        appendRow(FormSpecs.RELATED_GAP_ROWSPEC);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void appendUnrelatedComponentsGapRow() {
        appendRow(FormSpecs.UNRELATED_GAP_ROWSPEC);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Component add(Component component) {
        getPanel().add(component, this.currentCellConstraints);
        this.focusGrouped = false;
        return component;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: addButton */
    public AbstractButtonPanelBuilder<B> addButton2(JComponent... buttons) {
        boolean z;
        Preconditions.checkNotNullOrEmpty(buttons, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "button array");
        boolean needsGap = false;
        for (JComponent button : buttons) {
            if (button == null) {
                addUnrelatedGap2();
                z = false;
            } else {
                if (needsGap) {
                    addRelatedGap2();
                }
                addButton2(button);
                z = true;
            }
            needsGap = z;
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: addButton */
    public AbstractButtonPanelBuilder<B> addButton2(Action... actions) {
        Preconditions.checkNotNullOrEmpty(actions, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "Action array");
        int length = actions.length;
        JButton[] buttons = new JButton[length];
        for (int i = 0; i < length; i++) {
            Action action = actions[i];
            buttons[i] = action == null ? null : createButton(action);
        }
        return addButton2((JComponent[]) buttons);
    }

    protected JButton createButton(Action action) {
        return getComponentFactory().createButton(action);
    }

    private int getColumnIncrementSign() {
        return isLeftToRight() ? 1 : -1;
    }
}
