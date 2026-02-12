package com.jgoodies.framework.table;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.BuilderSupport;
import com.jgoodies.components.JGTable;
import com.jgoodies.components.util.TableUtils;
import com.jgoodies.layout.layout.ColumnSpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableBuilder.class */
public final class TableBuilder<R> {
    private final BuilderSupport support = new BuilderSupport();
    private final JGTable<R> table;
    private final List<TableColumnConfiguration<R, Object>> columnConfigurations;
    private String columnLayoutPreferencesId;
    private boolean tableSortable;
    private Boolean headerReorderingAllowed;

    public TableBuilder(JGTable<R> table) {
        this.table = table;
        this.tableSortable = table.getRowSorter() != null;
        this.columnConfigurations = new ArrayList();
    }

    public TableBuilder<R> persistAt(String columnLayoutPreferencesId) {
        this.support.checkNotCalledTwice("persistAt");
        this.columnLayoutPreferencesId = columnLayoutPreferencesId;
        return this;
    }

    public TableBuilder<R> sortable(boolean b) {
        this.support.checkNotCalledTwice("sortable");
        this.tableSortable = b;
        return this;
    }

    public TableBuilder<R> headerReorderingAllowed(boolean b) {
        this.support.checkNotCalledTwice("headerReorderingAllowed");
        this.headerReorderingAllowed = Boolean.valueOf(b);
        return this;
    }

    public JGTable<R> build() {
        this.table.setModel(new BuiltTableModel(this.columnConfigurations));
        int columnCount = this.columnConfigurations.size();
        List<ColumnSpec> columnSpecs = new ArrayList<>();
        TableColumnModel columnModel = this.table.getColumnModel();
        TableRowSorter rowSorter = this.table.getRowSorter();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            TableColumnConfiguration<R, Object> configuration = this.columnConfigurations.get(columnIndex);
            ColumnSpec spec = Strings.isBlank(configuration.layout) ? null : ColumnSpec.decode(configuration.layout);
            columnSpecs.add(spec);
            TableColumn column = columnModel.getColumn(columnIndex);
            if (configuration.renderer != null) {
                column.setCellRenderer(configuration.renderer);
            }
            if (configuration.editor != null) {
                column.setCellEditor(configuration.editor);
            }
            if (rowSorter instanceof TableRowSorter) {
                boolean columnSortable = configuration.sortable == null ? this.tableSortable : configuration.sortable.booleanValue();
                rowSorter.setSortable(columnIndex, columnSortable);
            }
        }
        if (this.headerReorderingAllowed != null) {
            this.table.getTableHeader().setReorderingAllowed(this.headerReorderingAllowed.booleanValue());
        }
        TableUtils.configureColumns(this.table, columnSpecs);
        if (this.columnLayoutPreferencesId != null) {
            TableStateUtils.restoreNowAndStoreOnChange(this.table, this.columnLayoutPreferencesId);
        }
        return this.table;
    }

    public Adder<R, R> beginColumn() {
        return beginColumn(true);
    }

    public Adder<R, R> beginColumn(boolean expression) {
        return new Adder<>(this, !expression);
    }

    public TableBuilder<R> addEmptyColumn() {
        return beginColumn().getString(row -> {
            return "";
        }).endColumn();
    }

    void addColumn(TableColumnConfiguration<R, Object> configuration) {
        this.columnConfigurations.add(configuration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableBuilder$TableColumnConfiguration.class */
    public static final class TableColumnConfiguration<R, V> {
        final Class<?> columnClass;
        String name;
        String layout;
        V prototype;
        boolean editable;
        Boolean sortable;
        Function<Integer, V> indexGetter;
        Function<R, V> objectGetter;
        BiConsumer<V, Integer> indexSetter;
        BiConsumer<V, R> objectSetter;
        Function<V, String> formatter;
        TableCellRenderer renderer;
        TableCellEditor editor;

        TableColumnConfiguration(Class<?> columnClass) {
            this.columnClass = columnClass;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void checkValid() {
            Preconditions.checkArgument(this.indexGetter == null || this.objectGetter == null, "You must not provide an index getter and object getter at the same time.");
            Preconditions.checkArgument(this.indexSetter == null || this.objectSetter == null, "You must not provide an index setter and object setter at the same time.");
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableBuilder$Adder.class */
    public static final class Adder<R, V> {
        private final BuilderSupport support;
        private final TableBuilder<R> builder;
        private final TableColumnConfiguration<R, V> target;
        private final boolean ignored;

        Adder(TableBuilder<R> builder, boolean ignored) {
            this.support = new BuilderSupport();
            this.builder = builder;
            this.ignored = ignored;
            this.target = new TableColumnConfiguration<>(Object.class);
        }

        Adder(TableBuilder<R> builder, Class<V> columnClass, TableColumnConfiguration<R, ?> originalTarget, boolean ignored) {
            this.support = new BuilderSupport();
            this.builder = builder;
            this.ignored = ignored;
            this.target = new TableColumnConfiguration<>(columnClass);
            this.target.name = originalTarget.name;
            this.target.layout = originalTarget.layout;
            this.target.editable = originalTarget.editable;
            this.target.sortable = originalTarget.sortable;
        }

        public Adder<R, V> name(String name) {
            this.support.checkNotCalledTwice("name");
            this.target.name = name;
            return this;
        }

        public Adder<R, V> layout(String layout, Object... args) {
            this.support.checkNotCalledTwice("layout");
            this.target.layout = Strings.get(layout, args);
            return this;
        }

        public Adder<R, V> editable() {
            return editable(true);
        }

        public Adder<R, V> editable(boolean value) {
            this.support.checkNotCalledTwice(ComponentModel.PROPERTY_EDITABLE);
            this.target.editable = value;
            return this;
        }

        public Adder<R, V> sortable(boolean value) {
            this.support.checkNotCalledTwice("sortable");
            this.target.sortable = Boolean.valueOf(value);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, Object> getValue(Function<R, Object> function) {
            return getValue(Object.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public <V2> Adder<R, V2> getValue(Class<V2> columnClass, Function<R, V2> function) {
            this.support.checkNotCalledTwice("get*");
            Adder<R, V2> adder = new Adder<>(this.builder, columnClass, this.target, this.ignored);
            adder.target.objectGetter = function;
            return adder;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, Boolean> getBoolean(Function<R, Boolean> function) {
            return (Adder<R, Boolean>) getValue(Boolean.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, Date> getDate(Function<R, Date> function) {
            return (Adder<R, Date>) getValue(Date.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, Float> getFloat(Function<R, Float> function) {
            return (Adder<R, Float>) getValue(Float.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, Integer> getInteger(Function<R, Integer> function) {
            return (Adder<R, Integer>) getValue(Integer.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, LocalDate> getLocalDate(Function<R, LocalDate> function) {
            return (Adder<R, LocalDate>) getValue(LocalDate.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, LocalDateTime> getLocalDateTime(Function<R, LocalDateTime> function) {
            return (Adder<R, LocalDateTime>) getValue(LocalDateTime.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, LocalTime> getLocalTime(Function<R, LocalTime> function) {
            return (Adder<R, LocalTime>) getValue(LocalTime.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, Long> getLong(Function<R, Long> function) {
            return (Adder<R, Long>) getValue(Long.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Adder<R, String> getString(Function<R, String> function) {
            return (Adder<R, String>) getValue(String.class, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public <V2> Adder<R, V2> getValueAt(Class<V2> columnClass, Function<Integer, V2> function) {
            Adder<R, V2> adder = new Adder<>(this.builder, columnClass, this.target, this.ignored);
            adder.target.indexGetter = function;
            return adder;
        }

        public Adder<R, V> setValue(BiConsumer<V, R> objectSetter) {
            this.target.objectSetter = objectSetter;
            return this;
        }

        public Adder<R, V> setValueAt(BiConsumer<V, Integer> indexSetter) {
            this.target.indexSetter = indexSetter;
            return this;
        }

        public Adder<R, V> formatter(Function<V, String> formatter) {
            this.support.checkNotCalledTwice("formatter");
            this.target.formatter = formatter;
            return this;
        }

        public Adder<R, V> renderer(TableCellRenderer renderer) {
            this.support.checkNotCalledTwice("renderer");
            this.target.renderer = renderer;
            return this;
        }

        public Adder<R, V> editor(TableCellEditor editor) {
            this.support.checkNotCalledTwice("editor");
            this.target.editor = editor;
            return this;
        }

        public TableBuilder<R> endColumn() {
            this.target.checkValid();
            if (this.target.objectGetter == null) {
                this.target.objectGetter = Function.identity();
            }
            if (!this.ignored) {
                this.builder.addColumn(this.target);
            }
            return this.builder;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/table/TableBuilder$BuiltTableModel.class */
    public static final class BuiltTableModel<R> extends AbstractTableAdapter<R> {
        private final List<TableColumnConfiguration<R, Object>> colConfigs;

        BuiltTableModel(List<TableColumnConfiguration<R, Object>> columnConfigs) {
            this.colConfigs = columnConfigs;
        }

        @Override // com.jgoodies.binding.adapter.AbstractTableAdapter
        public int getColumnCount() {
            return this.colConfigs.size();
        }

        @Override // com.jgoodies.binding.adapter.AbstractTableAdapter
        public String getColumnName(int columnIndex) {
            return this.colConfigs.get(columnIndex).name;
        }

        public Class<?> getColumnClass(int columnIndex) {
            TableColumnConfiguration<R, Object> config = this.colConfigs.get(columnIndex);
            return config.formatter != null ? String.class : config.columnClass;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.colConfigs.get(columnIndex).editable;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Object value;
            TableColumnConfiguration<R, Object> config = this.colConfigs.get(columnIndex);
            if (config.indexGetter != null) {
                value = config.indexGetter.apply(Integer.valueOf(rowIndex));
            } else {
                value = config.objectGetter.apply(getRow(rowIndex));
            }
            return config.formatter != null ? config.formatter.apply(value) : value;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            TableColumnConfiguration<R, Object> config = this.colConfigs.get(columnIndex);
            if (!config.editable) {
                super.setValueAt(aValue, rowIndex, columnIndex);
            } else if (config.indexSetter != null) {
                config.indexSetter.accept(aValue, Integer.valueOf(rowIndex));
            } else {
                if (config.objectSetter != null) {
                    config.objectSetter.accept(aValue, getRow(rowIndex));
                    return;
                }
                throw new IllegalStateException("Missing index or object setter for column #" + columnIndex);
            }
        }
    }
}
