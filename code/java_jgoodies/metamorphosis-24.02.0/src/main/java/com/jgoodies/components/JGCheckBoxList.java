package com.jgoodies.components;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.Listeners;
import com.jgoodies.components.internal.JGCheckBoxListCellRenderer;
import com.jgoodies.sandbox.basics.combo.FieldListCombo;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGCheckBoxList.class */
public class JGCheckBoxList<E> extends JList<E> {
    private ListSelectionModel inclusionModel;
    private ListSelectionListener listInclusionListener;
    private ListDataListener intervalAddedListener;
    private ListDataListener intervalRemovedListener;

    public JGCheckBoxList(ListModel<E> dataModel) {
        super(dataModel);
        init();
    }

    public JGCheckBoxList(E[] listData) {
        super(listData);
        init();
    }

    public JGCheckBoxList(Vector<E> listData) {
        super(listData);
        init();
    }

    public JGCheckBoxList() {
        init();
    }

    private void init() {
        this.inclusionModel = new DefaultListSelectionModel();
        this.inclusionModel.setSelectionMode(2);
        this.listInclusionListener = this::onInclusionListSelectionChanged;
        this.intervalAddedListener = Listeners.listIntervalAdded(this::onListIntervalAdded);
        this.intervalRemovedListener = Listeners.listIntervalRemoved(this::onListIntervalRemoved);
        getModel().addListDataListener(this.intervalAddedListener);
        getModel().addListDataListener(this.intervalRemovedListener);
        getInclusionModel().addListSelectionListener(this.listInclusionListener);
        addKeyListener(Listeners.keyPressed(this::onKeyPressed, null));
        addMouseListener(Listeners.mousePressed(this::onMousePressed, null));
        addPropertyChangeListener(FieldListCombo.PROPERTY_MODEL, this::onModelChanged);
    }

    public final void includeAll() {
        this.inclusionModel.addSelectionInterval(0, getModel().getSize() - 1);
    }

    public final void excludeAll() {
        this.inclusionModel.clearSelection();
    }

    public final ListSelectionModel getInclusionModel() {
        return this.inclusionModel;
    }

    public final void setInclusionModel(ListSelectionModel newInclusionModel) {
        Preconditions.checkNotNull(newInclusionModel, Messages.MUST_NOT_BE_NULL, "the inclusion model");
        Preconditions.checkArgument(newInclusionModel.getSelectionMode() == 2, "The included model's selection mode must be MULTIPLE_INTERVAL_SELECTION.");
        ListSelectionModel oldInclusionModel = getInclusionModel();
        if (oldInclusionModel != null) {
            oldInclusionModel.removeListSelectionListener(this.listInclusionListener);
        }
        this.inclusionModel = newInclusionModel;
        newInclusionModel.addListSelectionListener(this.listInclusionListener);
    }

    public final List<Integer> getIncludedIndices() {
        int minIndex = this.inclusionModel.getMinSelectionIndex();
        int maxIndex = this.inclusionModel.getMaxSelectionIndex();
        int maxSize = (maxIndex - minIndex) + 1;
        List<Integer> indices = new ArrayList<>(maxSize);
        for (int index = minIndex; index <= maxIndex; index++) {
            if (this.inclusionModel.isSelectedIndex(index)) {
                indices.add(Integer.valueOf(index));
            }
        }
        return Collections.unmodifiableList(indices);
    }

    public final void setIncludedIndices(int... indices) {
        Preconditions.checkNotNull(indices, Messages.MUST_NOT_BE_NULL, "the array of included indices");
        this.inclusionModel.setValueIsAdjusting(true);
        this.inclusionModel.clearSelection();
        for (int index : indices) {
            this.inclusionModel.addSelectionInterval(index, index);
        }
        this.inclusionModel.setValueIsAdjusting(false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final List<E> getIncludedItems() {
        int minIndex = this.inclusionModel.getMinSelectionIndex();
        int maxIndex = this.inclusionModel.getMaxSelectionIndex();
        int maxSize = (maxIndex - minIndex) + 1;
        ListModel<E> dataModel = getModel();
        ArrayList arrayList = new ArrayList(maxSize);
        for (int index = minIndex; index <= maxIndex; index++) {
            if (this.inclusionModel.isSelectedIndex(index)) {
                arrayList.add(dataModel.getElementAt(index));
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    public final void setIncludedItems(E... includedItems) {
        Preconditions.checkNotNull(includedItems, Messages.MUST_NOT_BE_NULL, "array of included items");
        setIncludedItems(Arrays.asList(includedItems));
    }

    public final void setIncludedItems(List<E> includedItems) {
        Preconditions.checkNotNull(includedItems, Messages.MUST_NOT_BE_NULL, "list of included items");
        this.inclusionModel.setValueIsAdjusting(true);
        this.inclusionModel.clearSelection();
        for (int index = 0; index < getModel().getSize(); index++) {
            if (includedItems.contains(getModel().getElementAt(index))) {
                this.inclusionModel.addSelectionInterval(index, index);
            }
        }
        this.inclusionModel.setValueIsAdjusting(false);
    }

    public final boolean isIncluded(int index) {
        return this.inclusionModel.isSelectedIndex(index);
    }

    public final void setIncluded(int index, boolean included) {
        if (included) {
            this.inclusionModel.addSelectionInterval(index, index);
        } else {
            this.inclusionModel.removeSelectionInterval(index, index);
        }
    }

    public final void setCellRenderer(ListCellRenderer<? super E> delegate) {
        super.setCellRenderer(new JGCheckBoxListCellRenderer(delegate));
    }

    private void onKeyPressed(KeyEvent evt) {
        if (evt.isConsumed() || evt.getModifiers() != 0 || evt.getKeyCode() != 32) {
            return;
        }
        toggleSelectedIncluded();
        repaint();
        evt.consume();
    }

    private void onMousePressed(MouseEvent evt) {
        int index;
        if (!isEnabled() || evt.isConsumed() || evt.getModifiersEx() != 1024 || evt.getX() > new JCheckBox().getPreferredSize().width || (index = locationToIndex(evt.getPoint())) == -1) {
            return;
        }
        toggleIncluded(index);
        repaint();
        evt.consume();
    }

    private void onModelChanged(PropertyChangeEvent evt) {
        excludeAll();
        ListModel<E> oldValue = (ListModel) evt.getOldValue();
        oldValue.removeListDataListener(this.intervalAddedListener);
        oldValue.removeListDataListener(this.intervalRemovedListener);
        ListModel<E> newValue = (ListModel) evt.getNewValue();
        newValue.addListDataListener(this.intervalAddedListener);
        newValue.addListDataListener(this.intervalRemovedListener);
    }

    private void onListIntervalAdded(ListDataEvent evt) {
        int minIndex = Math.min(evt.getIndex0(), evt.getIndex1());
        int maxIndex = Math.max(evt.getIndex0(), evt.getIndex1());
        this.inclusionModel.insertIndexInterval(minIndex, (maxIndex - minIndex) + 1, true);
    }

    private void onListIntervalRemoved(ListDataEvent evt) {
        this.inclusionModel.removeIndexInterval(evt.getIndex0(), evt.getIndex1());
    }

    private void onInclusionListSelectionChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            repaint();
        }
    }

    private void toggleIncluded(int index) {
        setIncluded(index, !isIncluded(index));
    }

    private void toggleSelectedIncluded() {
        this.inclusionModel.setValueIsAdjusting(true);
        for (int index : getSelectedIndices()) {
            toggleIncluded(index);
        }
        this.inclusionModel.setValueIsAdjusting(false);
    }
}
