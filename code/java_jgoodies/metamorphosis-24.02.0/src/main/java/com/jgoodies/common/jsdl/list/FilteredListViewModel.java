package com.jgoodies.common.jsdl.list;

import com.jgoodies.common.swing.collect.ArrayListModel;
import com.jgoodies.common.swing.collect.ObservableList2;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.event.ListDataEvent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/list/FilteredListViewModel.class */
public class FilteredListViewModel<E> extends ListViewModel<E> {
    private final ObservableList2<E> filteredItems = new ArrayListModel();
    private Predicate<E> filter;

    public final Predicate<E> getFilter() {
        return this.filter;
    }

    public final void setFilter(Predicate<E> newFilter) {
        Predicate<E> oldFilter = getFilter();
        if (oldFilter == newFilter) {
            return;
        }
        this.filter = newFilter;
        filter();
    }

    public final List<E> getFilteredItems() {
        return Collections.unmodifiableList(this.filteredItems);
    }

    public final void filter() {
        List<E> oldSelection = getSelectedItems();
        List<E> oldInclusions = getIncludedItems();
        filterWithoutRestore();
        restoreSelection(oldSelection);
        restoreInclusions(oldInclusions);
    }

    public final E filteredGet(int i) {
        return (E) this.filteredItems.get(i);
    }

    public final boolean filteredContains(E element) {
        return this.filteredItems.contains(element);
    }

    public final boolean filteredIsEmpty() {
        return this.filteredItems.isEmpty();
    }

    public final int filteredSize() {
        return this.filteredItems.size();
    }

    @Override // com.jgoodies.common.jsdl.list.ListViewModel
    public void includeAll() {
        super.includeAll();
        filter();
    }

    @Override // com.jgoodies.common.jsdl.list.ListViewModel
    public void clearInclusions() {
        super.clearInclusions();
        filter();
    }

    @Override // com.jgoodies.common.jsdl.list.ListViewModel
    protected ObservableList2<E> displayedList() {
        return this.filteredItems;
    }

    @Override // com.jgoodies.common.jsdl.list.ListViewModel
    protected final void setItemsImpl(List<E> rawItems) {
        List<E> oldSelection = getSelectedItems();
        List<E> oldInclusions = getIncludedItems();
        this.items.removeListDataListener(this.listDataListener);
        try {
            this.items.clear();
            this.items.addAll(rawItems);
            filterWithoutRestore();
            this.items.addListDataListener(this.listDataListener);
            restoreSelection(oldSelection);
            restoreInclusions(oldInclusions);
        } catch (Throwable th) {
            this.items.addListDataListener(this.listDataListener);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.common.jsdl.list.ListViewModel
    public void onListDataChanged(ListDataEvent evt) {
        super.onListDataChanged(evt);
        filter();
    }

    private void filterWithoutRestore() {
        this.filteredItems.clear();
        List<E> includedElements = (List) this.items.stream().filter(this::include).collect(Collectors.toList());
        this.filteredItems.addAll(includedElements);
    }

    private boolean include(E element) {
        return this.filter == null || this.filter.test(element);
    }
}
