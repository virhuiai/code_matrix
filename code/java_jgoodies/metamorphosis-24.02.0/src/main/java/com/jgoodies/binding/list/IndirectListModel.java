package com.jgoodies.binding.list;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.collect.ArrayListModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/list/IndirectListModel.class */
public class IndirectListModel<E> extends Bean implements ListModel<E> {
    public static final String PROPERTY_LIST = "list";
    public static final String PROPERTY_LIST_HOLDER = "listHolder";
    private static final ListModel<?> EMPTY_LIST_MODEL = new EmptyListModel();
    private ValueModel listHolder;
    private Object list;
    private int listSize;
    private final PropertyChangeListener listChangeHandler;
    private final ListDataListener listDataChangeHandler;
    private final EventListenerList listenerList;

    public IndirectListModel() {
        this((ListModel) new ArrayListModel());
    }

    public IndirectListModel(E[] listItems) {
        this(Arrays.asList(listItems));
    }

    public IndirectListModel(List<E> list) {
        this(new ValueHolder(list, true));
    }

    public IndirectListModel(ListModel<E> listModel) {
        this(new ValueHolder(listModel, true));
    }

    public IndirectListModel(ValueModel listHolder) {
        this.listenerList = new EventListenerList();
        Preconditions.checkNotNull(listHolder, Messages.MUST_NOT_BE_NULL, "list holder");
        checkListHolderIdentityCheck(listHolder);
        this.listChangeHandler = this::onListChanged;
        this.listDataChangeHandler = createListDataChangeHandler();
        this.listHolder = listHolder;
        this.listHolder.addValueChangeListener(this.listChangeHandler);
        this.list = listHolder.getValue();
        this.listSize = getSize(this.list);
        if (this.list != null) {
            if (this.list instanceof ListModel) {
                ((ListModel) this.list).addListDataListener(this.listDataChangeHandler);
            } else if (!(this.list instanceof List)) {
                throw new ClassCastException("The listHolder's value must be a List or ListModel.");
            }
        }
    }

    public final List<E> getList() {
        Object aList = getListHolder().getValue();
        if (aList == null) {
            return Collections.emptyList();
        }
        if (aList instanceof List) {
            return (List) aList;
        }
        throw new ClassCastException("#getList assumes that the list holder holds a List");
    }

    public final void setList(List<E> newList) {
        getListHolder().setValue(newList);
    }

    public final void setListItems(E... listItems) {
        setList(Arrays.asList(listItems));
    }

    public final ListModel<E> getListModel() {
        Object value = getListHolder().getValue();
        if (value == null) {
            return (ListModel<E>) EMPTY_LIST_MODEL;
        }
        if (value instanceof ListModel) {
            return (ListModel) value;
        }
        throw new ClassCastException("#getListModel assumes that the list holder holds a ListModel");
    }

    public final void setListModel(ListModel<E> newListModel) {
        getListHolder().setValue(newListModel);
    }

    public final ValueModel getListHolder() {
        return this.listHolder;
    }

    public final void setListHolder(ValueModel newListHolder) {
        Preconditions.checkNotNull(newListHolder, Messages.MUST_NOT_BE_NULL, "new list holder");
        checkListHolderIdentityCheck(newListHolder);
        ValueModel oldListHolder = getListHolder();
        if (oldListHolder == newListHolder) {
            return;
        }
        Object oldList = this.list;
        int oldSize = this.listSize;
        Object newList = newListHolder.getValue();
        oldListHolder.removeValueChangeListener(this.listChangeHandler);
        this.listHolder = newListHolder;
        newListHolder.addValueChangeListener(this.listChangeHandler);
        updateList(oldList, oldSize, newList);
        firePropertyChange(PROPERTY_LIST_HOLDER, oldListHolder, newListHolder);
    }

    public final boolean isEmpty() {
        return getSize() == 0;
    }

    public final int getSize() {
        return getSize(getListHolder().getValue());
    }

    public final E getElementAt(int index) {
        return getElementAt(getListHolder().getValue(), index);
    }

    public final void addListDataListener(ListDataListener l) {
        this.listenerList.add(ListDataListener.class, l);
    }

    public final void removeListDataListener(ListDataListener l) {
        this.listenerList.remove(ListDataListener.class, l);
    }

    public final ListDataListener[] getListDataListeners() {
        return this.listenerList.getListeners(ListDataListener.class);
    }

    public final void fireContentsChanged(int index0, int index1) {
        Object[] listeners = this.listenerList.getListenerList();
        ListDataEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, 0, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    public final void fireIntervalAdded(int index0, int index1) {
        Object[] listeners = this.listenerList.getListenerList();
        ListDataEvent e = null;
        this.listSize = getSize();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, 1, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    public final void fireIntervalRemoved(int index0, int index1) {
        Object[] listeners = this.listenerList.getListenerList();
        ListDataEvent e = null;
        this.listSize = getSize();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, 2, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    public void release() {
        this.listHolder.removeValueChangeListener(this.listChangeHandler);
        if (this.list instanceof ListModel) {
            ((ListModel) this.list).removeListDataListener(this.listDataChangeHandler);
        }
        this.listHolder = null;
        this.list = null;
    }

    protected ListDataListener createListDataChangeHandler() {
        return new ListDataChangeHandler();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateList(Object oldList, int oldSize, Object newList) {
        if (oldList instanceof ListModel) {
            ((ListModel) oldList).removeListDataListener(this.listDataChangeHandler);
        }
        if (newList instanceof ListModel) {
            ((ListModel) newList).addListDataListener(this.listDataChangeHandler);
        }
        int newSize = getSize(newList);
        this.list = newList;
        this.listSize = newSize;
        firePropertyChange(PROPERTY_LIST, oldList, newList);
        fireListChanged(oldSize - 1, newSize - 1);
    }

    protected final void fireListChanged(int oldLastIndex, int newLastIndex) {
        if (newLastIndex < oldLastIndex) {
            fireIntervalRemoved(newLastIndex + 1, oldLastIndex);
        } else if (oldLastIndex < newLastIndex) {
            fireIntervalAdded(oldLastIndex + 1, newLastIndex);
        }
        int lastCommonIndex = Math.min(oldLastIndex, newLastIndex);
        if (lastCommonIndex >= 0) {
            fireContentsChanged(0, lastCommonIndex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final int getSize(Object aListListModelOrNull) {
        if (aListListModelOrNull == null) {
            return 0;
        }
        if (aListListModelOrNull instanceof ListModel) {
            return ((ListModel) aListListModelOrNull).getSize();
        }
        return ((List) aListListModelOrNull).size();
    }

    private E getElementAt(Object obj, int i) {
        Preconditions.checkNotNull(obj, "The list contents is null.");
        if (obj instanceof ListModel) {
            return (E) ((ListModel) obj).getElementAt(i);
        }
        return (E) ((List) obj).get(i);
    }

    private static void checkListHolderIdentityCheck(ValueModel aListHolder) {
        if (!(aListHolder instanceof ValueHolder)) {
            return;
        }
        ValueHolder valueHolder = (ValueHolder) aListHolder;
        Preconditions.checkArgument(valueHolder.isIdentityCheckEnabled(), "The list holder must have the identity check enabled.");
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/list/IndirectListModel$EmptyListModel.class */
    private static final class EmptyListModel<T> implements ListModel<T>, Serializable {
        private EmptyListModel() {
        }

        public int getSize() {
            return 0;
        }

        public T getElementAt(int index) {
            return null;
        }

        public void addListDataListener(ListDataListener l) {
        }

        public void removeListDataListener(ListDataListener l) {
        }
    }

    private void onListChanged(PropertyChangeEvent evt) {
        Object oldList = this.list;
        int oldSize = this.listSize;
        Object newList = evt.getNewValue();
        updateList(oldList, oldSize, newList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/list/IndirectListModel$ListDataChangeHandler.class */
    public final class ListDataChangeHandler implements ListDataListener {
        private ListDataChangeHandler() {
        }

        public void intervalAdded(ListDataEvent evt) {
            IndirectListModel.this.fireIntervalAdded(evt.getIndex0(), evt.getIndex1());
        }

        public void intervalRemoved(ListDataEvent evt) {
            IndirectListModel.this.fireIntervalRemoved(evt.getIndex0(), evt.getIndex1());
        }

        public void contentsChanged(ListDataEvent evt) {
            IndirectListModel.this.fireContentsChanged(evt.getIndex0(), evt.getIndex1());
        }
    }
}
