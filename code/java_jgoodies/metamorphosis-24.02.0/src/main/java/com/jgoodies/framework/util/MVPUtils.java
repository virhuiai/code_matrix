package com.jgoodies.framework.util;

import com.jgoodies.binding.adapter.ListModelBindable;
import com.jgoodies.common.swing.collect.ArrayListModel;
import com.jgoodies.components.JGTable;
import java.util.Arrays;
import java.util.List;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/MVPUtils.class */
public final class MVPUtils {
    private MVPUtils() {
    }

    public static <E> void setData(JList<E> list, E... items) {
        setData(list, Arrays.asList(items));
    }

    public static <E> void setData(JList<E> list, List<E> items) {
        list.setModel(items == null ? new ArrayListModel() : new ArrayListModel(items));
    }

    public static <E> void setData(JGTable<E> table, E... items) {
        setData(table, Arrays.asList(items));
    }

    public static <E> void setData(JGTable<E> table, List<E> data) {
        ListModelBindable<E> tableModel = table.getModel();
        tableModel.setListModel(data == null ? new ArrayListModel<>() : new ArrayListModel<>(data));
    }

    public static void fireContentsChanged(JList<?> list, int index) {
        fireContentsChanged(list, index, index);
    }

    public static void fireContentsChanged(JTable table, int index) {
        fireContentsChanged(table, index, index);
    }

    public static void fireContentsChanged(JList<?> list, int index0, int index1) {
        ListModel<?> model = list.getModel();
        if (!(model instanceof ArrayListModel)) {
            throw new UnsupportedOperationException("This feature requires that the ListModel is an instance of ArrayListModel.");
        }
        ((ArrayListModel) model).fireContentsChanged(index0, index1);
    }

    public static void fireContentsChanged(JTable table, int index0, int index1) {
        ListModelBindable model = table.getModel();
        if (!(model instanceof ListModelBindable)) {
            throw new UnsupportedOperationException("This feature requires that the table's model is an instance of ListModelBindable.");
        }
        ListModel<?> listModel = model.getListModel();
        if (!(listModel instanceof ArrayListModel)) {
            throw new UnsupportedOperationException("This feature requires that the table's model holds an instance of ArrayListModel.");
        }
        ((ArrayListModel) listModel).fireContentsChanged(index0, index1);
    }

    public static void fireSelectedContentsChanged(JList<?> list) {
        fireContentsChanged(list, getMinSelectionIndex(list), getMaxSelectionIndex(list));
    }

    public static void fireSelectedContentsChanged(JTable table) {
        fireContentsChanged(table, getMinSelectionIndex(table), getMaxSelectionIndex(table));
    }

    public static int getMinSelectionIndex(JList<?> list) {
        return list.getSelectionModel().getMinSelectionIndex();
    }

    public static int getMinSelectionIndex(JTable table) {
        return table.getSelectionModel().getMinSelectionIndex();
    }

    public static int getMaxSelectionIndex(JList<?> list) {
        return list.getSelectionModel().getMaxSelectionIndex();
    }

    public static int getMaxSelectionIndex(JTable table) {
        return table.getSelectionModel().getMaxSelectionIndex();
    }

    public static <E> E getSelectedItem(JList<E> jList) {
        return (E) jList.getSelectedValue();
    }

    public static <E> E getSelectedItem(JGTable<E> jGTable) {
        int minSelectionIndex = jGTable.getSelectionModel().getMinSelectionIndex();
        if (minSelectionIndex == -1) {
            return null;
        }
        return (E) jGTable.getModel().getListModel().getElementAt(jGTable.convertRowIndexToModel(minSelectionIndex));
    }

    public static boolean hasSelection(JList<?> list) {
        return !list.isSelectionEmpty();
    }

    public static boolean hasSelection(JTable table) {
        return !table.getSelectionModel().isSelectionEmpty();
    }

    public static boolean isMultipleSelection(JList<?> list) {
        return (list.isSelectionEmpty() || isSingleSelection(list)) ? false : true;
    }

    public static boolean isMultipleSelection(JTable table) {
        return (table.getSelectionModel().isSelectionEmpty() || isSingleSelection(table)) ? false : true;
    }

    public static boolean isSingleSelection(JList<?> list) {
        return list.getMinSelectionIndex() != -1 && list.getMinSelectionIndex() == list.getMaxSelectionIndex();
    }

    public static boolean isSingleSelection(JTable table) {
        return table.getSelectionModel().getMinSelectionIndex() != -1 && table.getSelectionModel().getMinSelectionIndex() == table.getSelectionModel().getMaxSelectionIndex();
    }

    public static boolean isSelectedFirst(JList<?> list) {
        return list.getMinSelectionIndex() == 0;
    }

    public static boolean isSelectedFirst(JTable table) {
        return getMinSelectionIndex(table) == 0;
    }

    public static boolean isSelectedLast(JList<?> list) {
        return list.getMaxSelectionIndex() == list.getModel().getSize() - 1;
    }

    public static boolean isSelectedLast(JTable table) {
        return getMaxSelectionIndex(table) == table.getModel().getRowCount() - 1;
    }
}
