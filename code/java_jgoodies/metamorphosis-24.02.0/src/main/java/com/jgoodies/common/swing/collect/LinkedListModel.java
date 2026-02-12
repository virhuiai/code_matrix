package com.jgoodies.common.swing.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/collect/LinkedListModel.class */
public final class LinkedListModel<E> extends LinkedList<E> implements ObservableList2<E> {
    private static final long serialVersionUID = 5753378113505707237L;
    private EventListenerList listenerList;

    public LinkedListModel() {
    }

    public LinkedListModel(Collection<? extends E> c) {
        super(c);
    }

    @Override // java.util.LinkedList, java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public void add(int index, E element) {
        super.add(index, element);
        fireIntervalAdded(index, index);
    }

    @Override // java.util.LinkedList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List, java.util.Deque, java.util.Queue
    public boolean add(E e) {
        int newIndex = size();
        super.add(e);
        fireIntervalAdded(newIndex, newIndex);
        return true;
    }

    @Override // java.util.LinkedList, java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean changed = super.addAll(index, c);
        if (changed) {
            int lastIndex = (index + c.size()) - 1;
            fireIntervalAdded(index, lastIndex);
        }
        return changed;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<?> e = iterator();
        while (e.hasNext()) {
            if (c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
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

    @Override // java.util.LinkedList, java.util.Deque
    public void addFirst(E e) {
        super.addFirst(e);
        fireIntervalAdded(0, 0);
    }

    @Override // java.util.LinkedList, java.util.Deque
    public void addLast(E e) {
        int newIndex = size();
        super.addLast(e);
        fireIntervalAdded(newIndex, newIndex);
    }

    @Override // java.util.LinkedList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        if (isEmpty()) {
            return;
        }
        int oldLastIndex = size() - 1;
        super.clear();
        fireIntervalRemoved(0, oldLastIndex);
    }

    @Override // java.util.LinkedList, java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public E remove(int i) {
        E e = (E) super.remove(i);
        fireIntervalRemoved(i, i);
        return e;
    }

    @Override // java.util.LinkedList, java.util.AbstractCollection, java.util.Collection, java.util.List, java.util.Deque
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override // java.util.LinkedList, java.util.Deque
    public E removeFirst() {
        E e = (E) super.removeFirst();
        fireIntervalRemoved(0, 0);
        return e;
    }

    @Override // java.util.LinkedList, java.util.Deque
    public E removeLast() {
        int size = size() - 1;
        E e = (E) super.removeLast();
        fireIntervalRemoved(size, size);
        return e;
    }

    @Override // java.util.AbstractList
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        fireIntervalRemoved(fromIndex, toIndex - 1);
    }

    @Override // java.util.LinkedList, java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public E set(int i, E e) {
        E e2 = (E) super.set(i, e);
        fireContentsChanged(i, i);
        return e2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.List
    public void sort(Comparator<? super E> comparator) {
        int size = size();
        if (size <= 1) {
            return;
        }
        Object[] a = toArray();
        Arrays.sort(a, comparator);
        ListIterator listIterator = super.listIterator(0);
        for (Object e : a) {
            listIterator.next();
            listIterator.set(e);
        }
        fireContentsChanged(0, size - 1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.List
    public void replaceAll(UnaryOperator<E> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        int size = size();
        if (size == 0) {
            return;
        }
        ListIterator listIterator = super.listIterator(0);
        while (listIterator.hasNext()) {
            listIterator.set(unaryOperator.apply(listIterator.next()));
        }
        fireContentsChanged(0, size - 1);
    }

    @Override // java.util.LinkedList, java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public ListIterator<E> listIterator(int index) {
        return new ReportingListIterator(super.listIterator(index));
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

    /* JADX INFO: Access modifiers changed from: private */
    public void fireIntervalAdded(int index0, int index1) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public void fireIntervalRemoved(int index0, int index1) {
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

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/collect/LinkedListModel$ReportingListIterator.class */
    private final class ReportingListIterator implements ListIterator<E> {
        private final ListIterator<E> delegate;
        private int lastReturnedIndex = -1;

        ReportingListIterator(ListIterator<E> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public E next() {
            this.lastReturnedIndex = nextIndex();
            return this.delegate.next();
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.delegate.hasPrevious();
        }

        @Override // java.util.ListIterator
        public E previous() {
            this.lastReturnedIndex = previousIndex();
            return this.delegate.previous();
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.delegate.nextIndex();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return this.delegate.previousIndex();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            int oldSize = LinkedListModel.this.size();
            this.delegate.remove();
            int newSize = LinkedListModel.this.size();
            if (newSize < oldSize) {
                LinkedListModel.this.fireIntervalRemoved(this.lastReturnedIndex, this.lastReturnedIndex);
            }
        }

        @Override // java.util.ListIterator
        public void set(E e) {
            this.delegate.set(e);
            LinkedListModel.this.fireContentsChanged(this.lastReturnedIndex);
        }

        @Override // java.util.ListIterator
        public void add(E e) {
            this.delegate.add(e);
            int newIndex = previousIndex();
            LinkedListModel.this.fireIntervalAdded(newIndex, newIndex);
            this.lastReturnedIndex = -1;
        }
    }
}
