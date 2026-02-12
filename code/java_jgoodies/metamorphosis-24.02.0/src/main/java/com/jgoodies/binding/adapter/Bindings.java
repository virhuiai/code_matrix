package com.jgoodies.binding.adapter;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.internal.TableRowSorterListSelectionModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings.class */
public final class Bindings {
    private static final String COMMIT_ON_FOCUS_LOST_MODEL_KEY = "commitOnFocusListModel";
    private static final String COMPONENT_VALUE_MODEL_KEY = "componentValueModel";
    private static final String COMPONENT_PROPERTY_HANDLER_KEY = "componentPropertyHandler";
    static final FocusLostHandler FOCUS_LOST_HANDLER = new FocusLostHandler();
    static final WeakTrigger FOCUS_LOST_TRIGGER = new WeakTrigger();

    private Bindings() {
    }

    public static void bind(AbstractButton toggleButton, ValueModel valueModel) {
        bind(toggleButton, valueModel, Boolean.TRUE, Boolean.FALSE);
    }

    public static void bind(AbstractButton toggleButton, ValueModel valueModel, Object selectedValue, Object deselectedValue) {
        ButtonModel oldModel = toggleButton.getModel();
        String oldActionCommand = oldModel.getActionCommand();
        boolean oldEnabled = oldModel.isEnabled();
        int oldMnemonic = oldModel.getMnemonic();
        int oldMnemonicIndex = toggleButton.getDisplayedMnemonicIndex();
        ToggleButtonAdapter toggleButtonAdapter = new ToggleButtonAdapter(valueModel, selectedValue, deselectedValue);
        toggleButtonAdapter.setActionCommand(oldActionCommand);
        toggleButtonAdapter.setEnabled(oldEnabled);
        toggleButtonAdapter.setMnemonic(oldMnemonic);
        toggleButton.setModel(toggleButtonAdapter);
        toggleButton.setDisplayedMnemonicIndex(oldMnemonicIndex);
        addComponentPropertyHandler(toggleButton, valueModel);
    }

    public static void bind(AbstractButton toggleButton, ValueModel model, Object choice) {
        ButtonModel oldModel = toggleButton.getModel();
        String oldActionCommand = oldModel.getActionCommand();
        boolean oldEnabled = oldModel.isEnabled();
        int oldMnemonic = oldModel.getMnemonic();
        int oldMnemonicIndex = toggleButton.getDisplayedMnemonicIndex();
        RadioButtonAdapter radioButtonAdapter = new RadioButtonAdapter(model, choice);
        radioButtonAdapter.setActionCommand(oldActionCommand);
        radioButtonAdapter.setEnabled(oldEnabled);
        radioButtonAdapter.setMnemonic(oldMnemonic);
        toggleButton.setModel(radioButtonAdapter);
        toggleButton.setDisplayedMnemonicIndex(oldMnemonicIndex);
        addComponentPropertyHandler(toggleButton, model);
    }

    public static <E> void bind(JComboBox<E> comboBox, ComboBoxModel<E> model) {
        Preconditions.checkNotNull(model, Messages.MUST_NOT_BE_NULL, "combo box model");
        comboBox.setModel(model);
    }

    public static <E> void bind(JComboBox<E> comboBox, ComboBoxModel<E> model, String nullText) {
        Preconditions.checkNotNull(model, Messages.MUST_NOT_BE_NULL, "combo box model");
        NullElement nullElement = new NullElement(nullText);
        comboBox.setModel(new NullElementComboBoxModel(model, nullElement));
        comboBox.setRenderer(new NullElementComboBoxRenderer(comboBox.getRenderer(), nullElement));
    }

    public static <E> void bind(JComboBox<E> comboBox, SelectionInList<E> selectionInList) {
        Preconditions.checkNotNull(selectionInList, Messages.MUST_NOT_BE_NULL, "SelectionInList");
        comboBox.setModel(new ComboBoxAdapter(selectionInList));
        addComponentPropertyHandler(comboBox, selectionInList.getSelectionHolder());
    }

    public static <E> void bind(JComboBox<E> comboBox, SelectionInList<E> selectionInList, String nullText) {
        NullElement nullElement = new NullElement(nullText);
        bind((JComboBox) comboBox, (SelectionInList) selectionInList);
        comboBox.setModel(new NullElementComboBoxModel(comboBox.getModel(), nullElement));
        comboBox.setRenderer(new NullElementComboBoxRenderer(comboBox.getRenderer(), nullElement));
    }

    public static <E> void bind(JComboBox<E> comboBox, ValueModel valueModel) {
        Preconditions.checkNotNull(comboBox, Messages.MUST_NOT_BE_NULL, "combo box");
        comboBox.setSelectedItem(valueModel.getValue());
        valueModel.addValueChangeListener(new ComboBoxSelectedItemUpdateHandler(valueModel, comboBox));
        comboBox.addItemListener(new SelectedItemChangeHandler(comboBox, valueModel));
        addComponentPropertyHandler(comboBox, valueModel);
    }

    public static <E> void bind(JComboBox<E> comboBox, ValueModel valueModel, String nullText) {
        NullElement nullElement = new NullElement(nullText);
        bind(comboBox, valueModel);
        comboBox.setModel(new NullElementComboBoxModel(comboBox.getModel(), nullElement));
        comboBox.setRenderer(new NullElementComboBoxRenderer(comboBox.getRenderer(), nullElement));
    }

    public static void bind(JFormattedTextField textField, ValueModel valueModel) {
        bind((JComponent) textField, ValueModel.PROPERTY_VALUE, valueModel);
    }

    public static void bind(JLabel label, ValueModel valueModel) {
        bind((JComponent) label, "text", valueModel);
    }

    public static <E> void bind(JList<E> list, SelectionInList<E> selectionInList) {
        Preconditions.checkNotNull(selectionInList, Messages.MUST_NOT_BE_NULL, "SelectionInList");
        list.setModel(selectionInList);
        list.setSelectionModel(new SingleListSelectionAdapter(selectionInList.getSelectionIndexHolder()));
        addComponentPropertyHandler(list, selectionInList.getSelectionHolder());
    }

    public static <E> void bind(JList<E> list, ListModel<E> listModel, ListSelectionModel listSelectionModel) {
        list.setModel(listModel);
        list.setSelectionModel(listSelectionModel);
    }

    public static <E> void bind(JTable table, SelectionInList<E> selectionInList) {
        ListModel<E> listModel = (ListModel) Preconditions.checkNotNull(selectionInList, Messages.MUST_NOT_BE_NULL, "SelectionInList");
        ListSelectionModel listSelectionModel = new SingleListSelectionAdapter(selectionInList.getSelectionIndexHolder());
        bind(table, listModel, listSelectionModel);
    }

    public static <E> void bind(JTable table, ListModel<E> listModel, ListSelectionModel listSelectionModel) {
        ListModelBindable model = table.getModel();
        Preconditions.checkNotNull(listSelectionModel, Messages.MUST_NOT_BE_NULL, "ListSelectionModel");
        Preconditions.checkArgument(model instanceof ListModelBindable, "The table's TableModel must implement ListModelBindable to be bound.");
        model.setListModel(listModel);
        if (table.getRowSorter() != null) {
            table.setSelectionModel(new TableRowSorterListSelectionModel(listSelectionModel, table));
        } else {
            table.setSelectionModel(listSelectionModel);
        }
    }

    public static void bind(JTextArea textArea, ValueModel valueModel) {
        bind(textArea, valueModel, false);
    }

    public static void bind(JTextArea textArea, ValueModel valueModel, boolean commitOnFocusLost) {
        ValueModel textModel;
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "value model");
        if (commitOnFocusLost) {
            textModel = createCommitOnFocusLostModel(valueModel, textArea);
            textArea.putClientProperty(COMMIT_ON_FOCUS_LOST_MODEL_KEY, textModel);
        } else {
            textModel = valueModel;
        }
        TextComponentConnector connector = new TextComponentConnector(textModel, textArea);
        connector.updateTextComponent();
        addComponentPropertyHandler(textArea, valueModel);
    }

    public static void bind(JTextField textField, ValueModel valueModel) {
        bind(textField, valueModel, false);
    }

    public static void bind(JTextField textField, ValueModel valueModel, boolean commitOnFocusLost) {
        ValueModel textModel;
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "value model");
        if (commitOnFocusLost) {
            textModel = createCommitOnFocusLostModel(valueModel, textField);
            textField.putClientProperty(COMMIT_ON_FOCUS_LOST_MODEL_KEY, textModel);
        } else {
            textModel = valueModel;
        }
        TextComponentConnector connector = new TextComponentConnector(textModel, textField);
        connector.updateTextComponent();
        addComponentPropertyHandler(textField, valueModel);
    }

    public static void bind(JComponent component, String propertyName, ValueModel valueModel) {
        Preconditions.checkNotNull(component, Messages.MUST_NOT_BE_NULL, "component");
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "value model");
        Preconditions.checkNotBlank(propertyName, Messages.MUST_NOT_BE_BLANK, "property name");
        PropertyConnector.connectAndUpdate(valueModel, component, propertyName);
        addComponentPropertyHandler(component, valueModel);
    }

    public static void addComponentPropertyHandler(JComponent component, ValueModel valueModel) {
        if (!(valueModel instanceof ComponentValueModel)) {
            return;
        }
        ComponentValueModel cvm = (ComponentValueModel) valueModel;
        PropertyChangeListener componentHandler = new ComponentPropertyHandler(component);
        cvm.addPropertyChangeListener(componentHandler);
        component.putClientProperty(COMPONENT_VALUE_MODEL_KEY, cvm);
        component.putClientProperty(COMPONENT_PROPERTY_HANDLER_KEY, componentHandler);
        component.setEnabled(cvm.isEnabled());
        component.setVisible(cvm.isVisible());
        if (component instanceof JTextComponent) {
            ((JTextComponent) component).setEditable(cvm.isEditable());
        }
    }

    public static void removeComponentPropertyHandler(JComponent component) {
        ComponentValueModel componentValueModel = (ComponentValueModel) component.getClientProperty(COMPONENT_VALUE_MODEL_KEY);
        PropertyChangeListener componentHandler = (PropertyChangeListener) component.getClientProperty(COMPONENT_PROPERTY_HANDLER_KEY);
        if (componentValueModel != null && componentHandler != null) {
            componentValueModel.removePropertyChangeListener(componentHandler);
            component.putClientProperty(COMPONENT_VALUE_MODEL_KEY, (Object) null);
            component.putClientProperty(COMPONENT_PROPERTY_HANDLER_KEY, (Object) null);
        } else if (componentValueModel != null || componentHandler != null) {
            if (componentValueModel != null) {
                throw new IllegalStateException("The component has a ComponentValueModel stored, but lacks the ComponentPropertyHandler.");
            }
            throw new IllegalStateException("The component has a ComponentPropertyHandler stored, but lacks the ComponentValueModel.");
        }
    }

    public static void commitImmediately() {
        FOCUS_LOST_TRIGGER.triggerCommit();
    }

    public static boolean flushImmediately() {
        boolean buffering = isFocusOwnerBuffering();
        if (buffering) {
            FOCUS_LOST_TRIGGER.triggerFlush();
        }
        return buffering;
    }

    public static boolean isFocusOwnerBuffering() {
        JComponent focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (!(focusOwner instanceof JComponent)) {
            return false;
        }
        Object value = focusOwner.getClientProperty(COMMIT_ON_FOCUS_LOST_MODEL_KEY);
        if (!(value instanceof BufferedValueModel)) {
            return false;
        }
        BufferedValueModel commitOnFocusLostModel = (BufferedValueModel) value;
        return commitOnFocusLostModel.isBuffering();
    }

    private static ValueModel createCommitOnFocusLostModel(ValueModel valueModel, Component component) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "value model");
        ValueModel model = new BufferedValueModel(valueModel, FOCUS_LOST_TRIGGER);
        component.addFocusListener(FOCUS_LOST_HANDLER);
        return model;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$SelectedItemChangeHandler.class */
    public static final class SelectedItemChangeHandler<E> implements ItemListener {
        private final JComboBox<E> comboBox;
        private final ValueModel valueModel;

        private SelectedItemChangeHandler(JComboBox<E> comboBox, ValueModel valueModel) {
            this.comboBox = comboBox;
            this.valueModel = valueModel;
        }

        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() != 1) {
                return;
            }
            Object selectedItem = this.comboBox.getModel().getSelectedItem();
            Object selectedValue = selectedItem instanceof NullElement ? null : selectedItem;
            this.valueModel.setValue(selectedValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$ComboBoxSelectedItemUpdateHandler.class */
    public static final class ComboBoxSelectedItemUpdateHandler<E> implements PropertyChangeListener {
        private final ValueModel valueModel;
        private final JComboBox<E> comboBox;

        private ComboBoxSelectedItemUpdateHandler(ValueModel valueModel, JComboBox<E> comboBox) {
            this.valueModel = valueModel;
            this.comboBox = comboBox;
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            this.comboBox.setSelectedItem(this.valueModel.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$FocusLostHandler.class */
    public static final class FocusLostHandler extends FocusAdapter {
        private FocusLostHandler() {
        }

        public void focusLost(FocusEvent evt) {
            if (!evt.isTemporary()) {
                Bindings.FOCUS_LOST_TRIGGER.triggerCommit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$ComponentPropertyHandler.class */
    public static final class ComponentPropertyHandler implements PropertyChangeListener {
        private final JComponent component;

        private ComponentPropertyHandler(JComponent component) {
            this.component = component;
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            ComponentValueModel model = (ComponentValueModel) evt.getSource();
            boolean z = -1;
            switch (propertyName.hashCode()) {
                case -1609594047:
                    if (propertyName.equals(ComponentModel.PROPERTY_ENABLED)) {
                        z = false;
                        break;
                    }
                    break;
                case 466743410:
                    if (propertyName.equals(ComponentModel.PROPERTY_VISIBLE)) {
                        z = true;
                        break;
                    }
                    break;
                case 1602416228:
                    if (propertyName.equals(ComponentModel.PROPERTY_EDITABLE)) {
                        z = 2;
                        break;
                    }
                    break;
            }
            switch (z) {
                case AnimatedLabel.CENTER /* 0 */:
                    this.component.setEnabled(model.isEnabled());
                    return;
                case true:
                    this.component.setVisible(model.isVisible());
                    return;
                case AnimatedLabel.LEFT /* 2 */:
                    if (this.component instanceof JTextComponent) {
                        this.component.setEditable(model.isEditable());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$NullElement.class */
    private static final class NullElement {
        private final String text;

        NullElement(String text) {
            this.text = (String) Preconditions.checkNotBlank(text, Messages.MUST_NOT_BE_BLANK, "text");
        }

        public String toString() {
            return this.text;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$NullElementComboBoxModel.class */
    private static final class NullElementComboBoxModel<E> implements ComboBoxModel<Object> {
        private final ComboBoxModel<E> delegate;
        private final Object nullElement;

        NullElementComboBoxModel(ComboBoxModel<E> delegate, Object nullElement) {
            this.delegate = (ComboBoxModel) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "wrapped ComboBoxModel");
            this.nullElement = Preconditions.checkNotNull(nullElement, Messages.MUST_NOT_BE_NULL, "null element");
        }

        public int getSize() {
            return this.delegate.getSize() + 1;
        }

        public Object getElementAt(int index) {
            return index == 0 ? this.nullElement : this.delegate.getElementAt(index - 1);
        }

        public Object getSelectedItem() {
            Object selected = this.delegate.getSelectedItem();
            return selected == null ? this.nullElement : selected;
        }

        public void setSelectedItem(Object anItem) {
            this.delegate.setSelectedItem(anItem != this.nullElement ? anItem : null);
        }

        public void addListDataListener(ListDataListener l) {
            this.delegate.addListDataListener(l);
        }

        public void removeListDataListener(ListDataListener l) {
            this.delegate.removeListDataListener(l);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$NullElementComboBoxRenderer.class */
    private static final class NullElementComboBoxRenderer<E> extends DefaultListCellRenderer {
        private final ListCellRenderer<E> delegate;
        private final NullElement nullElement;

        NullElementComboBoxRenderer(ListCellRenderer<E> delegate, NullElement nullElement) {
            this.delegate = (ListCellRenderer) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "wrapped ListCellRenderer");
            this.nullElement = (NullElement) Preconditions.checkNotNull(nullElement, Messages.MUST_NOT_BE_NULL, "null element");
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == this.nullElement) {
                return super.getListCellRendererComponent(list, this.nullElement, index, isSelected, cellHasFocus);
            }
            return this.delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$WeakTrigger.class */
    public static final class WeakTrigger implements ValueModel {
        private Boolean value = null;
        private final transient WeakPropertyChangeSupport changeSupport = new WeakPropertyChangeSupport(this);

        WeakTrigger() {
        }

        @Override // com.jgoodies.binding.value.ValueModel
        public Object getValue() {
            return this.value;
        }

        @Override // com.jgoodies.binding.value.ValueModel
        public void setValue(Object newValue) {
            Preconditions.checkArgument(newValue == null || (newValue instanceof Boolean), "Trigger values must be of type Boolean.");
            Object oldValue = this.value;
            this.value = (Boolean) newValue;
            fireValueChange(oldValue, newValue);
        }

        @Override // com.jgoodies.binding.value.ValueModel
        public void addValueChangeListener(PropertyChangeListener listener) {
            if (listener == null) {
                return;
            }
            this.changeSupport.addPropertyChangeListener(ValueModel.PROPERTY_VALUE, listener);
        }

        @Override // com.jgoodies.binding.value.ValueModel
        public void removeValueChangeListener(PropertyChangeListener listener) {
            if (listener == null) {
                return;
            }
            this.changeSupport.removePropertyChangeListener(ValueModel.PROPERTY_VALUE, listener);
        }

        private void fireValueChange(Object oldValue, Object newValue) {
            this.changeSupport.firePropertyChange(ValueModel.PROPERTY_VALUE, oldValue, newValue);
        }

        void triggerCommit() {
            if (Boolean.TRUE.equals(getValue())) {
                setValue((Object) null);
            }
            setValue(Boolean.TRUE);
        }

        void triggerFlush() {
            if (Boolean.FALSE.equals(getValue())) {
                setValue((Object) null);
            }
            setValue(Boolean.FALSE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$WeakPropertyChangeSupport.class */
    public static final class WeakPropertyChangeSupport extends PropertyChangeSupport {
        static final ReferenceQueue<PropertyChangeListener> QUEUE = new ReferenceQueue<>();

        WeakPropertyChangeSupport(Object sourceBean) {
            super(sourceBean);
        }

        @Override // java.beans.PropertyChangeSupport
        public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
            if (listener == null) {
                return;
            }
            if (listener instanceof PropertyChangeListenerProxy) {
                PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
                addPropertyChangeListener(proxy.getPropertyName(), (PropertyChangeListener) proxy.getListener());
            } else {
                super.addPropertyChangeListener(new WeakPropertyChangeListener(listener));
            }
        }

        @Override // java.beans.PropertyChangeSupport
        public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            if (listener == null) {
                return;
            }
            super.addPropertyChangeListener(propertyName, new WeakPropertyChangeListener(propertyName, listener));
        }

        @Override // java.beans.PropertyChangeSupport
        public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
            if (listener == null) {
                return;
            }
            if (listener instanceof PropertyChangeListenerProxy) {
                PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
                removePropertyChangeListener(proxy.getPropertyName(), (PropertyChangeListener) proxy.getListener());
                return;
            }
            PropertyChangeListener[] listeners = getPropertyChangeListeners();
            for (int i = listeners.length - 1; i >= 0; i--) {
                if (!(listeners[i] instanceof PropertyChangeListenerProxy)) {
                    WeakPropertyChangeListener wpcl = (WeakPropertyChangeListener) listeners[i];
                    if (wpcl.get() == listener) {
                        super.removePropertyChangeListener(wpcl);
                        return;
                    }
                }
            }
        }

        @Override // java.beans.PropertyChangeSupport
        public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            if (listener == null) {
                return;
            }
            PropertyChangeListener[] listeners = getPropertyChangeListeners(propertyName);
            for (int i = listeners.length - 1; i >= 0; i--) {
                WeakPropertyChangeListener wpcl = (WeakPropertyChangeListener) listeners[i];
                if (wpcl.get() == listener) {
                    super.removePropertyChangeListener(propertyName, wpcl);
                    return;
                }
            }
        }

        @Override // java.beans.PropertyChangeSupport
        public void firePropertyChange(PropertyChangeEvent evt) {
            cleanUp();
            super.firePropertyChange(evt);
        }

        @Override // java.beans.PropertyChangeSupport
        public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            cleanUp();
            super.firePropertyChange(propertyName, oldValue, newValue);
        }

        private static void cleanUp() {
            while (true) {
                WeakPropertyChangeListener wpcl = (WeakPropertyChangeListener) QUEUE.poll();
                if (wpcl != null) {
                    wpcl.removeListener();
                } else {
                    return;
                }
            }
        }

        void removeWeakPropertyChangeListener(WeakPropertyChangeListener l) {
            if (l.propertyName == null) {
                super.removePropertyChangeListener(l);
            } else {
                super.removePropertyChangeListener(l.propertyName, l);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/Bindings$WeakPropertyChangeSupport$WeakPropertyChangeListener.class */
        public final class WeakPropertyChangeListener extends WeakReference<PropertyChangeListener> implements PropertyChangeListener {
            final String propertyName;

            private WeakPropertyChangeListener(WeakPropertyChangeSupport weakPropertyChangeSupport, PropertyChangeListener delegate) {
                this((String) null, delegate);
            }

            private WeakPropertyChangeListener(String propertyName, PropertyChangeListener delegate) {
                super(delegate, WeakPropertyChangeSupport.QUEUE);
                this.propertyName = propertyName;
            }

            @Override // java.beans.PropertyChangeListener
            public void propertyChange(PropertyChangeEvent evt) {
                PropertyChangeListener delegate = (PropertyChangeListener) get();
                if (delegate != null) {
                    delegate.propertyChange(evt);
                }
            }

            void removeListener() {
                WeakPropertyChangeSupport.this.removeWeakPropertyChangeListener(this);
            }
        }
    }
}
