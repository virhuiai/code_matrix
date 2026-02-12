package com.jgoodies.sandbox.basics.combo;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.Listeners;
import com.jgoodies.components.JGComponentFactory;
import com.jgoodies.components.JGTextField;
import com.jgoodies.framework.selection.ListRowSelectionManager;
import com.jgoodies.layout.builder.FormBuilder;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/basics/combo/FieldListCombo.class */
public final class FieldListCombo<E> extends Bean {
    public static final String PROPERTY_MODEL = "model";
    private ComboBoxModel<E> model;
    private ValueModel textModel;
    private JGTextField field;
    private JList<E> list;
    private ListDataListener contentsChangedListener;
    private PropertyChangeListener textChangeListener;

    public FieldListCombo(ComboBoxModel<E> model) {
        initComponents();
        initEventHandling();
        setModel(model);
    }

    public FieldListCombo(E[] items) {
        this((ComboBoxModel) new DefaultComboBoxModel(items));
    }

    public FieldListCombo(Vector<E> items) {
        this((ComboBoxModel) new DefaultComboBoxModel(items));
    }

    public FieldListCombo() {
        this((ComboBoxModel) new DefaultComboBoxModel());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void initComponents() {
        JGComponentFactory current = JGComponentFactory.getCurrent();
        this.field = current.createTextField();
        this.list = current.createList(new Object[0]);
        this.list.setSelectionMode(0);
        this.textModel = new ValueHolder();
        Bindings.bind(this.field, this.textModel);
    }

    private void initEventHandling() {
        new ListRowSelectionManager(this.list).registerKeyboardActions(this.field);
        this.list.getSelectionModel().addListSelectionListener(this::onListSelectionChanged);
        this.textChangeListener = this::onTextChanged;
        this.contentsChangedListener = Listeners.listContentsChanged(this::onListContentsChanged);
        this.textModel.addValueChangeListener(this.textChangeListener);
    }

    public ComboBoxModel<E> getModel() {
        return this.model;
    }

    public void setModel(ComboBoxModel<E> newModel) {
        Preconditions.checkNotNull(newModel, Messages.MUST_NOT_BE_NULL, "combo box model");
        ComboBoxModel<E> oldModel = getModel();
        if (oldModel != null) {
            oldModel.removeListDataListener(this.contentsChangedListener);
        }
        this.model = newModel;
        this.list.setModel(newModel);
        setFieldText(formatItem(newModel.getSelectedItem()));
        newModel.addListDataListener(this.contentsChangedListener);
        firePropertyChange(PROPERTY_MODEL, oldModel, newModel);
    }

    public JGTextField getField() {
        return this.field;
    }

    public Icon getFieldIcon() {
        return this.field.getIcon();
    }

    public void setFieldIcon(Icon icon) {
        this.field.setIcon(icon);
    }

    public String getFieldText() {
        return this.field.getText();
    }

    public void setFieldText(String text) {
        if (Objects.equals(text, getFieldText())) {
            return;
        }
        this.field.setText(text);
    }

    public String getFieldPrompt() {
        return this.field.getPrompt();
    }

    public void setFieldPrompt(String prompt) {
        this.field.setPrompt(prompt);
    }

    public JList<E> getList() {
        return this.list;
    }

    public ListCellRenderer<? super E> getCellRenderer() {
        return getList().getCellRenderer();
    }

    public void setCellRenderer(ListCellRenderer<? super E> cellRenderer) {
        getList().setCellRenderer(cellRenderer);
    }

    public JComponent buildPanel() {
        return new FormBuilder().columns("fill:pref:grow", new Object[0]).rows("p, 0, f:p:g", new Object[0]).add((Component) this.field).xy(1, 1).add((Component) this.list).xy(1, 3).build();
    }

    private void onListSelectionChanged(ListSelectionEvent evt) {
        Object selectedValue = this.list.getSelectedValue();
        if (selectedValue != null) {
            getModel().setSelectedItem(selectedValue);
        }
    }

    private void onTextChanged(PropertyChangeEvent evt) {
        String text = (String) this.textModel.getValue();
        getModel().setSelectedItem(text);
    }

    private void onListContentsChanged(ListDataEvent evt) {
        Object selectedItem = getModel().getSelectedItem();
        getModel().removeListDataListener(this.contentsChangedListener);
        this.textModel.removeValueChangeListener(this.textChangeListener);
        try {
            setFieldText(formatItem(selectedItem));
            this.list.setSelectedValue(selectedItem, true);
            if (!Objects.equals(this.list.getSelectedValue(), selectedItem)) {
                this.list.clearSelection();
            }
        } finally {
            getModel().addListDataListener(this.contentsChangedListener);
            this.textModel.addValueChangeListener(this.textChangeListener);
        }
    }

    private static String formatItem(Object item) {
        return item == null ? "" : item.toString();
    }
}
