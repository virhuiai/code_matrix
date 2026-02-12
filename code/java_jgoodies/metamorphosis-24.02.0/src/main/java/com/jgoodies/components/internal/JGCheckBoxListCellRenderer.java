package com.jgoodies.components.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.components.JGCheckBoxList;
import com.jgoodies.layout.builder.FormBuilder;
import com.jgoodies.layout.factories.CC;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/JGCheckBoxListCellRenderer.class */
public class JGCheckBoxListCellRenderer<E> implements ListCellRenderer<E> {
    private final ListCellRenderer<E> delegate;
    private final JCheckBox checkBox = new JCheckBox();
    private final JComponent container;

    public JGCheckBoxListCellRenderer(ListCellRenderer<E> delegate) {
        this.delegate = delegate;
        this.checkBox.setBorderPaintedFlat(true);
        this.container = buildPanel();
    }

    private JComponent buildPanel() {
        return new FormBuilder().columns("1dlu, pref, 2dlu, fill:default:grow", new Object[0]).rows("p", new Object[0]).opaque(SystemUtils.isLafAqua()).add((Component) this.checkBox).xy(2, 1).build();
    }

    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
        Component rendererComponent = this.delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        this.checkBox.setSelected(isIncluded(list, index));
        this.checkBox.setEnabled(list.isEnabled());
        if (SystemUtils.isLafAqua()) {
            this.container.setBackground(rendererComponent.getBackground());
        }
        if (this.container.getComponentCount() == 2) {
            this.container.remove(1);
        }
        this.container.add(rendererComponent, CC.xy(4, 1));
        return this.container;
    }

    protected boolean isIncluded(JList<? extends E> list, int index) {
        Preconditions.checkArgument(list instanceof JGCheckBoxList, "The JGCheckBoxListCellRender must be used only with JGCheckBoxLists.");
        JGCheckBoxList<? extends E> checkBoxList = (JGCheckBoxList) list;
        return checkBoxList.isIncluded(index);
    }
}
