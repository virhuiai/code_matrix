package com.jgoodies.layout.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.FormsSetup;
import com.jgoodies.layout.factories.ComponentFactory;
import com.jgoodies.layout.factories.Paddings;
import com.jgoodies.layout.internal.AbstractBuilder;
import com.jgoodies.layout.layout.CellConstraints;
import com.jgoodies.layout.layout.FormLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/internal/AbstractBuilder.class */
public abstract class AbstractBuilder<B extends AbstractBuilder<B>> {
    private final JPanel panel;
    private final FormLayout layout;
    protected final CellConstraints currentCellConstraints;
    private ComponentFactory componentFactory;

    public abstract JPanel build();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractBuilder(FormLayout layout, JPanel panel) {
        this.layout = (FormLayout) Preconditions.checkNotNull(layout, Messages.MUST_NOT_BE_NULL, "layout");
        this.panel = (JPanel) Preconditions.checkNotNull(panel, Messages.MUST_NOT_BE_NULL, "panel");
        panel.setLayout(layout);
        this.currentCellConstraints = new CellConstraints();
    }

    public JPanel getPanel() {
        return this.panel;
    }

    public final FormLayout getLayout() {
        return this.layout;
    }

    public final int getColumnCount() {
        return getLayout().getColumnCount();
    }

    public final int getRowCount() {
        return getLayout().getRowCount();
    }

    public final B background(Color background) {
        getPanel().setBackground(background);
        opaque(true);
        return this;
    }

    public final B border(Border border) {
        getPanel().setBorder(border);
        return this;
    }

    public final B padding(EmptyBorder padding) {
        getPanel().setBorder(padding);
        return this;
    }

    public final B padding(String paddingSpec, Object... args) {
        padding(Paddings.createPadding(paddingSpec, args));
        return this;
    }

    public final B opaque(boolean b) {
        getPanel().setOpaque(b);
        return this;
    }

    public final ComponentFactory getComponentFactory() {
        if (this.componentFactory == null) {
            this.componentFactory = createComponentFactory();
        }
        return this.componentFactory;
    }

    public final void setComponentFactory(ComponentFactory newFactory) {
        this.componentFactory = newFactory;
    }

    protected ComponentFactory createComponentFactory() {
        return FormsSetup.getComponentFactoryDefault();
    }
}
