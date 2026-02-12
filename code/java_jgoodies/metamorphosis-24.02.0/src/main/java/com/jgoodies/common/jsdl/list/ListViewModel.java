package com.jgoodies.common.jsdl.list;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.action.ActionBean;
import com.jgoodies.common.swing.collect.ArrayListModel;
import com.jgoodies.common.swing.collect.ObservableList2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/list/ListViewModel.class */
public class ListViewModel<E> extends ActionBean {
    private BiPredicate<E, E> selectionComparator;
    protected final ListDataListener listDataListener = new ListDataHandler();
    private final ListSelectionListener selectionChangeListener = this::onListSelectionValueChanged;
    private final ListSelectionListener inclusionChangeListener = this::onListInclusionValueChanged;
    protected final ObservableList2<E> items = new ArrayListModel();
    private final ListSelectionModel selectionModel = new DefaultListSelectionModel();
    private final ListSelectionModel inclusionModel = new DefaultListSelectionModel();

    public ListViewModel() {
        this.items.addListDataListener(this.listDataListener);
        this.selectionModel.addListSelectionListener(this.selectionChangeListener);
        this.inclusionModel.addListSelectionListener(this.inclusionChangeListener);
    }

    public final ListModel<E> getDataModel() {
        return displayedList();
    }

    public final ListSelectionModel getSelectionModel() {
        return this.selectionModel;
    }

    public final ListSelectionModel getInclusionModel() {
        return this.inclusionModel;
    }

    public final BiPredicate<E, E> getSelectionComparator() {
        return this.selectionComparator;
    }

    public final void setSelectionComparator(BiPredicate<E, E> comparator) {
        this.selectionComparator = comparator;
    }

    public final void setSelectionComparator(Comparator<E> comparator) {
        this.selectionComparator = (e1, e2) -> {
            return comparator.compare(e1, e2) == 0;
        };
    }

    public final void setSelectionComparator(Function<E, Object> mapper) {
        setSelectionComparator((e1, e2) -> {
            return Objects.equals(mapper.apply(e1), mapper.apply(e2));
        });
    }

    public final List<E> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public final void setItems(E... items) {
        setItems(items == null ? Collections.EMPTY_LIST : Arrays.asList(items));
    }

    public final void setItems(List<E> items) {
        setItemsImpl(items);
    }

    public final E get(int i) {
        return (E) this.items.get(i);
    }

    public final int size() {
        return this.items.size();
    }

    public final void add(E element) {
        this.items.add(element);
    }

    public final void add(int index, E element) {
        this.items.add(index, element);
    }

    public final boolean addAll(Collection<? extends E> c) {
        return this.items.addAll(c);
    }

    public final boolean addAll(int index, Collection<? extends E> c) {
        return this.items.addAll(index, c);
    }

    public final void remove(E element) {
        this.items.remove(element);
    }

    public final E remove(int i) {
        return (E) this.items.remove(i);
    }

    public final boolean removeAll(Collection<?> c) {
        return this.items.removeAll(c);
    }

    public final E setItem(int i, E e) {
        return (E) this.items.set(i, e);
    }

    public final void clear() {
        this.items.clear();
    }

    public final boolean contains(E element) {
        return this.items.contains(element);
    }

    public final boolean isEmpty() {
        return this.items.isEmpty();
    }

    public final void moveSelectedItemDown() {
        if (!isSingleSelection() || isSelectedLast()) {
            return;
        }
        int oldSelectedIndex = getSelectedIndex();
        int newSelectedIndex = oldSelectedIndex + 1;
        E item = remove(oldSelectedIndex);
        add(newSelectedIndex, item);
        setSelectedIndex(newSelectedIndex);
    }

    public final void moveSelectedItemUp() {
        if (!isSingleSelection() || isSelectedFirst()) {
            return;
        }
        int oldSelectedIndex = getSelectedIndex();
        int newSelectedIndex = oldSelectedIndex - 1;
        E item = remove(oldSelectedIndex);
        add(newSelectedIndex, item);
        setSelectedIndex(newSelectedIndex);
    }

    public final int getSelectedIndex() {
        return getMinSelectionIndex();
    }

    public final void setSelectedIndex(int index) {
        this.selectionModel.setSelectionInterval(index, index);
    }

    public final List<Integer> getSelectedIndices() {
        return getSelectedIndices(getSelectionModel());
    }

    public final void setSelectedIndices(int... indices) {
        Preconditions.checkNotNull(indices, Messages.MUST_NOT_BE_NULL, "the array of selected indices");
        setSelectedIndices(getSelectionModel(), indices);
    }

    public final int getMinSelectionIndex() {
        return getSelectionModel().getMinSelectionIndex();
    }

    public final int getMaxSelectionIndex() {
        return getSelectionModel().getMaxSelectionIndex();
    }

    public final E getSelectedItem() {
        if (isSingleSelection()) {
            return displayedGet(getMinSelectionIndex());
        }
        return null;
    }

    public final void setSelectedItem(E item) {
        int index = indexOf(this.items, item, this.selectionComparator);
        setSelectedIndex(index);
    }

    public final List<E> getSelectedItems() {
        return getSelectedItems(getSelectionModel());
    }

    public final void setSelectedItems(E... itemsToSelect) {
        Preconditions.checkNotNull(itemsToSelect, Messages.MUST_NOT_BE_NULL, "the array of selected items");
        setSelectedItems(getSelectionModel(), Arrays.asList(itemsToSelect));
    }

    public final void setSelectedItems(List<E> itemsToSelect) {
        Preconditions.checkNotNull(itemsToSelect, Messages.MUST_NOT_BE_NULL, "the list of selected items");
        setSelectedItems(getSelectionModel(), itemsToSelect);
    }

    public final int getSelectionMode() {
        return getSelectionModel().getSelectionMode();
    }

    public final void setSelectionMode(int selectionMode) {
        getSelectionModel().setSelectionMode(selectionMode);
    }

    public final boolean hasSelection() {
        return !this.selectionModel.isSelectionEmpty();
    }

    public final boolean isSelectionEmpty() {
        return this.selectionModel.isSelectionEmpty();
    }

    public final boolean isSingleSelection() {
        return getMinSelectionIndex() != -1 && getMinSelectionIndex() == getMaxSelectionIndex();
    }

    public final boolean isMultipleSelection() {
        return (isSelectionEmpty() || isSingleSelection()) ? false : true;
    }

    public final boolean isSelectedFirst() {
        return getMinSelectionIndex() == 0;
    }

    public final boolean isSelectedLast() {
        return getMaxSelectionIndex() == displayedSize() - 1;
    }

    public final void selectFirst() {
        this.selectionModel.setSelectionInterval(0, 0);
    }

    public final void selectAll() {
        this.selectionModel.setSelectionInterval(0, displayedSize() - 1);
    }

    public final void select(Predicate<E> predicate) {
        this.selectionModel.setValueIsAdjusting(true);
        this.selectionModel.clearSelection();
        for (int index = displayedSize() - 1; index >= 0; index--) {
            E item = get(index);
            if (predicate.test(item)) {
                this.selectionModel.addSelectionInterval(index, index);
            }
        }
        this.selectionModel.setValueIsAdjusting(false);
    }

    public final void clearSelection() {
        this.selectionModel.clearSelection();
    }

    public final List<Integer> getIncludedIndices() {
        return getSelectedIndices(getInclusionModel());
    }

    public final void setIncludedIndices(int... indices) {
        Preconditions.checkNotNull(indices, Messages.MUST_NOT_BE_NULL, "the array of included indices");
        setSelectedIndices(getInclusionModel(), indices);
    }

    public final List<E> getIncludedItems() {
        return getSelectedItems(getInclusionModel());
    }

    public final void setIncludedItems(E... itemsToInclude) {
        Preconditions.checkNotNull(itemsToInclude, Messages.MUST_NOT_BE_NULL, "the array of included items");
        setSelectedItems(getInclusionModel(), Arrays.asList(itemsToInclude));
    }

    public final void setIncludedItems(List<E> itemsToInclude) {
        Preconditions.checkNotNull(itemsToInclude, Messages.MUST_NOT_BE_NULL, "the list of included items");
        setSelectedItems(getInclusionModel(), itemsToInclude);
    }

    public final boolean hasInclusions() {
        return !getInclusionModel().isSelectionEmpty();
    }

    public final boolean isInclusionEmpty() {
        return this.inclusionModel.isSelectionEmpty();
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
        this.items.fireContentsChanged(index);
    }

    public final void toggleInclusion(int index) {
        setIncluded(index, !isIncluded(index));
    }

    public void includeAll() {
        int maxIndex = displayedSize() - 1;
        if (maxIndex != -1) {
            getInclusionModel().setSelectionInterval(0, maxIndex);
            displayedList().fireContentsChanged(0, maxIndex);
        }
    }

    public final void include(Predicate<E> predicate) {
        this.inclusionModel.setValueIsAdjusting(true);
        this.inclusionModel.clearSelection();
        for (int index = displayedSize() - 1; index >= 0; index--) {
            E item = get(index);
            if (predicate.test(item)) {
                this.inclusionModel.addSelectionInterval(index, index);
            }
        }
        this.inclusionModel.setValueIsAdjusting(false);
    }

    public void clearInclusions() {
        int maxIndex = displayedSize() - 1;
        getInclusionModel().clearSelection();
        if (maxIndex != -1) {
            displayedList().fireContentsChanged(0, maxIndex);
        }
    }

    protected final void fireContentsChanged(int index) {
        this.items.fireContentsChanged(index);
    }

    protected final void fireContentsChanged(int index0, int index1) {
        this.items.fireContentsChanged(index0, index1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onListDataChanged(ListDataEvent evt) {
    }

    protected void onListSelectionChanged(ListSelectionEvent evt) {
    }

    protected void onListInclusionChanged(ListSelectionEvent evt) {
    }

    private static <E> int indexOf(List<E> list, E element, BiPredicate<E, E> comparator) {
        if (element == null) {
            return -1;
        }
        if (comparator == null) {
            return list.indexOf(element);
        }
        int size = list.size();
        for (int index = 0; index < size; index++) {
            E item = list.get(index);
            if (comparator.test(item, element)) {
                return index;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void restoreSelection(List<E> oldSelectedItems) {
        setSelectedItems(getSelectionModel(), oldSelectedItems);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void restoreInclusions(List<E> oldIncludedItems) {
        setSelectedItems(getInclusionModel(), oldIncludedItems);
    }

    protected void setItemsImpl(List<E> newItems) {
        List<E> oldSelection = getSelectedItems();
        this.items.clear();
        this.items.addAll(newItems);
        restoreSelection(oldSelection);
    }

    protected ObservableList2<E> displayedList() {
        return this.items;
    }

    protected final E displayedGet(int i) {
        return (E) displayedList().get(i);
    }

    protected final int displayedSize() {
        return displayedList().size();
    }

    protected static List<Integer> getSelectedIndices(ListSelectionModel listSelectionModel) {
        int minIndex = listSelectionModel.getMinSelectionIndex();
        int maxIndex = listSelectionModel.getMaxSelectionIndex();
        int maxSize = (maxIndex - minIndex) + 1;
        List<Integer> indices = new ArrayList<>(maxSize);
        for (int index = minIndex; index <= maxIndex; index++) {
            if (listSelectionModel.isSelectedIndex(index)) {
                indices.add(Integer.valueOf(index));
            }
        }
        return Collections.unmodifiableList(indices);
    }

    protected static void setSelectedIndices(ListSelectionModel listSelectionModel, int... indices) {
        listSelectionModel.setValueIsAdjusting(true);
        listSelectionModel.clearSelection();
        for (int index : indices) {
            listSelectionModel.addSelectionInterval(index, index);
        }
        listSelectionModel.setValueIsAdjusting(false);
    }

    protected final List<E> getSelectedItems(ListSelectionModel listSelectionModel) {
        int minIndex = listSelectionModel.getMinSelectionIndex();
        int maxIndex = listSelectionModel.getMaxSelectionIndex();
        int maxSize = (maxIndex - minIndex) + 1;
        List<E> items = new ArrayList<>(maxSize);
        for (int index = minIndex; index <= maxIndex; index++) {
            if (listSelectionModel.isSelectedIndex(index)) {
                items.add(displayedGet(index));
            }
        }
        return Collections.unmodifiableList(items);
    }

    protected final void setSelectedItems(ListSelectionModel listSelectionModel, List<E> itemsToSelect) {
        listSelectionModel.setValueIsAdjusting(true);
        listSelectionModel.clearSelection();
        for (E item : itemsToSelect) {
            int index = indexOf(displayedList(), item, this.selectionComparator);
            if (index != -1) {
                listSelectionModel.addSelectionInterval(index, index);
            }
        }
        listSelectionModel.setValueIsAdjusting(false);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/list/ListViewModel$ListDataHandler.class */
    private final class ListDataHandler implements ListDataListener {
        private ListDataHandler() {
        }

        public void contentsChanged(ListDataEvent evt) {
            ListViewModel.this.onListDataChanged(evt);
        }

        public void intervalAdded(ListDataEvent evt) {
            ListViewModel.this.onListDataChanged(evt);
        }

        public void intervalRemoved(ListDataEvent evt) {
            ListViewModel.this.onListDataChanged(evt);
        }
    }

    private void onListSelectionValueChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting()) {
            return;
        }
        onListSelectionChanged(evt);
    }

    private void onListInclusionValueChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting()) {
            return;
        }
        onListInclusionChanged(evt);
    }
}
