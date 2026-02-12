package com.jgoodies.common.jsdl.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/HistoryList.class */
public final class HistoryList<E> {
    private final List<E> list = new ArrayList();
    private int indexOfNextAdd = 0;

    public boolean add(E e) {
        if (e.equals(getLastAdded())) {
            return false;
        }
        for (int index = this.list.size() - 1; index > 0 && index >= this.indexOfNextAdd; index--) {
            this.list.remove(index);
        }
        this.list.add(this.indexOfNextAdd, e);
        this.indexOfNextAdd++;
        return true;
    }

    public void removeAll(E item) {
        for (int index = this.list.size() - 1; index > 0; index--) {
            E element = this.list.get(index);
            if (element.equals(item)) {
                this.list.remove(index);
                if (index < this.indexOfNextAdd) {
                    this.indexOfNextAdd--;
                }
            }
        }
    }

    public E getAndGoPrevious() {
        if (!hasPrevious()) {
            return null;
        }
        List<E> list = this.list;
        int i = this.indexOfNextAdd - 1;
        this.indexOfNextAdd = i;
        return list.get(i - 1);
    }

    public E getAndGoNext() {
        if (!hasNext()) {
            return null;
        }
        List<E> list = this.list;
        int i = this.indexOfNextAdd;
        this.indexOfNextAdd = i + 1;
        return list.get(i);
    }

    public Iterator<E> getBackIterator() {
        List<E> sublist = subListNoDuplicates(0, this.indexOfNextAdd - 1);
        Collections.reverse(sublist);
        return sublist.iterator();
    }

    public Iterator<E> getNextIterator() {
        return subListNoDuplicates(this.indexOfNextAdd, this.list.size()).iterator();
    }

    public E getLastAdded() {
        if (this.indexOfNextAdd > 0) {
            return this.list.get(this.indexOfNextAdd - 1);
        }
        return null;
    }

    public E getPrevious() {
        if (hasPrevious()) {
            return this.list.get(this.indexOfNextAdd - 2);
        }
        return null;
    }

    public E getNext() {
        if (hasNext()) {
            return this.list.get(this.indexOfNextAdd);
        }
        return null;
    }

    public void goBack(int steps) {
        this.indexOfNextAdd = Math.max(0, this.indexOfNextAdd - steps);
    }

    public void goNext(int steps) {
        this.indexOfNextAdd = Math.min(this.indexOfNextAdd + steps, this.list.size());
    }

    public boolean hasPrevious() {
        return this.indexOfNextAdd > 1;
    }

    public boolean hasNext() {
        return this.indexOfNextAdd < this.list.size();
    }

    private List<E> subListNoDuplicates(int startIndex, int stopIndex) {
        E lastAdded = getLastAdded();
        List<E> result = new ArrayList<>();
        for (int i = startIndex; i < stopIndex; i++) {
            E e = this.list.get(i);
            if (e != lastAdded) {
                result.add(e);
            }
        }
        return result;
    }

    public List<E> getList() {
        return this.list;
    }

    public String toString() {
        return "(indexOfNextAdd: " + this.indexOfNextAdd + ")" + this.list.toString();
    }
}
