package com.jgoodies.common.swing.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/collect/ArrayListModel.class */
public final class ArrayListModel<E> extends ArrayList<E> implements ObservableList2<E> {
    private static final long serialVersionUID = -6165677201152015546L;
    private EventListenerList listenerList;

    public ArrayListModel() {
        this(10);
    }

    public ArrayListModel(int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayListModel(Collection<? extends E> c) {
        super(c);
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public void add(int index, E element) {
        super.add(index, element);
        fireIntervalAdded(index, index);
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(E e) {
        int newIndex = size();
        super.add(e);
        fireIntervalAdded(newIndex, newIndex);
        return true;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean changed = super.addAll(index, c);
        if (changed) {
            int lastIndex = (index + c.size()) - 1;
            fireIntervalAdded(index, lastIndex);
        }
        return changed;
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends E> c) {
        int firstIndex = size();
        boolean changed = super.addAll(c);
        if (changed) {
            int lastIndex = (firstIndex + c.size()) - 1;
            fireIntervalAdded(firstIndex, lastIndex);
        }
        return changed;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        if (isEmpty()) {
            return;
        }
        int oldLastIndex = size() - 1;
        super.clear();
        fireIntervalRemoved(0, oldLastIndex);
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public E remove(int i) {
        E e = (E) super.remove(i);
        fireIntervalRemoved(i, i);
        return e;
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object o) {
        int index = indexOf(o);
        boolean contained = index != -1;
        if (contained) {
            remove(index);
        }
        return contained;
    }

    @Override // java.util.ArrayList, java.util.Collection
    public boolean removeIf(Predicate<? super E> filter) {
        int oldLastIndex = size() - 1;
        boolean anyElementRemoved = super.removeIf(filter);
        if (anyElementRemoved) {
            if (isEmpty()) {
                fireIntervalRemoved(0, oldLastIndex);
            } else {
                int newLastIndex = size() - 1;
                fireIntervalRemoved(newLastIndex + 1, oldLastIndex);
                fireContentsChanged(0, newLastIndex);
            }
        }
        return anyElementRemoved;
    }

    @Override // java.util.ArrayList, java.util.AbstractList
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        fireIntervalRemoved(fromIndex, toIndex - 1);
    }

    @Override // java.util.ArrayList, java.util.List
    public void replaceAll(UnaryOperator<E> operator) {
        if (isEmpty()) {
            Objects.requireNonNull(operator);
        } else {
            super.replaceAll(operator);
            fireContentsChanged(0, size() - 1);
        }
    }

    @Override // java.util.ArrayList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> e = iterator();
        while (e.hasNext()) {
            if (!c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override // java.util.ArrayList, java.util.AbstractList, java.util.List
    public E set(int i, E e) {
        E e2 = (E) super.set(i, e);
        fireContentsChanged(i, i);
        return e2;
    }

    @Override // java.util.ArrayList, java.util.List
    public void sort(Comparator<? super E> comparator) {
        super.sort(comparator);
        int lastIndex = size() - 1;
        if (lastIndex > 0) {
            fireContentsChanged(0, lastIndex);
        }
    }

    public void addListDataListener(ListDataListener l) {
        getEventListenerList().add(ListDataListener.class, l);
    }

    public void removeListDataListener(ListDataListener l) {
        getEventListenerList().remove(ListDataListener.class, l);
    }

    public E getElementAt(int index) {
        return get(index);
    }

    public int getSize() {
        return size();
    }

    @Override // com.jgoodies.common.swing.collect.ObservableList2
    public void fireContentsChanged(int index) {
        fireContentsChanged(index, index);
    }

    @Override // com.jgoodies.common.swing.collect.ObservableList2
    public void fireContentsChanged(int index0, int index1) {
        Object[] listeners = getEventListenerList().getListenerList();
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

    public ListDataListener[] getListDataListeners() {
        return getEventListenerList().getListeners(ListDataListener.class);
    }

    private void fireIntervalAdded(int index0, int index1) {
        Object[] listeners = getEventListenerList().getListenerList();
        ListDataEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, 1, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    private void fireIntervalRemoved(int index0, int index1) {
        Object[] listeners = getEventListenerList().getListenerList();
        ListDataEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(this, 2, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    private EventListenerList getEventListenerList() {
        if (this.listenerList == null) {
            this.listenerList = new EventListenerList();
        }
        return this.listenerList;
    }
}
