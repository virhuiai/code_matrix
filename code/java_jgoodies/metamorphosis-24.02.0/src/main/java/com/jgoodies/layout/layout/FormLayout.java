package com.jgoodies.layout.layout;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout.class */
public final class FormLayout implements LayoutManager2, Serializable {
    private static final Measure MINIMUM_WIDTH_MEASURE = new MinimumWidthMeasure();
    private static final Measure MINIMUM_HEIGHT_MEASURE = new MinimumHeightMeasure();
    private static final Measure PREFERRED_WIDTH_MEASURE = new PreferredWidthMeasure();
    private static final Measure PREFERRED_HEIGHT_MEASURE = new PreferredHeightMeasure();
    private final List<ColumnSpec> colSpecs;
    private final List<RowSpec> rowSpecs;
    private int[][] colGroupIndices;
    private int[][] rowGroupIndices;
    private final Map<Component, CellConstraints> constraintMap;
    private boolean honorsVisibility;
    private transient List<Component>[] colComponents;
    private transient List<Component>[] rowComponents;
    private final ComponentSizeCache componentSizeCache;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout$Measure.class */
    public interface Measure {
        int sizeOf(Component component, ComponentSizeCache componentSizeCache);
    }

    public FormLayout() {
        this(new ColumnSpec[0], new RowSpec[0]);
    }

    public FormLayout(String encodedColumnSpecs) {
        this(encodedColumnSpecs, LayoutMap.getRoot());
    }

    public FormLayout(String encodedColumnSpecs, LayoutMap layoutMap) {
        this(ColumnSpec.decodeSpecs(encodedColumnSpecs, layoutMap), new RowSpec[0]);
    }

    public FormLayout(String encodedColumnSpecs, String encodedRowSpecs) {
        this(encodedColumnSpecs, encodedRowSpecs, LayoutMap.getRoot());
    }

    public FormLayout(String encodedColumnSpecs, String encodedRowSpecs, LayoutMap layoutMap) {
        this(ColumnSpec.decodeSpecs(encodedColumnSpecs, layoutMap), RowSpec.decodeSpecs(encodedRowSpecs, layoutMap));
    }

    public FormLayout(ColumnSpec[] colSpecs) {
        this(colSpecs, new RowSpec[0]);
    }

    /* JADX WARN: Type inference failed for: r1v6, types: [int[], int[][]] */
    /* JADX WARN: Type inference failed for: r1v8, types: [int[], int[][]] */
    public FormLayout(ColumnSpec[] colSpecs, RowSpec[] rowSpecs) {
        this.honorsVisibility = true;
        Preconditions.checkNotNull(colSpecs, Messages.MUST_NOT_BE_NULL, "column specifications");
        Preconditions.checkNotNull(rowSpecs, Messages.MUST_NOT_BE_NULL, "row specifications");
        this.colSpecs = new ArrayList(Arrays.asList(colSpecs));
        this.rowSpecs = new ArrayList(Arrays.asList(rowSpecs));
        this.colGroupIndices = new int[0];
        this.rowGroupIndices = new int[0];
        int initialCapacity = (colSpecs.length * rowSpecs.length) / 4;
        this.constraintMap = new HashMap(initialCapacity);
        this.componentSizeCache = new ComponentSizeCache(initialCapacity);
    }

    public int getColumnCount() {
        return this.colSpecs.size();
    }

    public ColumnSpec getColumnSpec(int columnIndex) {
        return this.colSpecs.get(columnIndex - 1);
    }

    public void setColumnSpec(int columnIndex, ColumnSpec columnSpec) {
        Preconditions.checkNotNull(columnSpec, Messages.MUST_NOT_BE_NULL, "column spec");
        this.colSpecs.set(columnIndex - 1, columnSpec);
    }

    public void appendColumn(ColumnSpec columnSpec) {
        Preconditions.checkNotNull(columnSpec, Messages.MUST_NOT_BE_NULL, "column spec");
        this.colSpecs.add(columnSpec);
    }

    public void insertColumn(int columnIndex, ColumnSpec columnSpec) {
        if (columnIndex < 1 || columnIndex > getColumnCount()) {
            throw new IndexOutOfBoundsException(String.format("The column index %s must be in the range [1, %s].", Integer.valueOf(columnIndex), Integer.valueOf(getColumnCount())));
        }
        this.colSpecs.add(columnIndex - 1, columnSpec);
        shiftComponentsHorizontally(columnIndex, false);
        adjustGroupIndices(this.colGroupIndices, columnIndex, false);
    }

    public void removeColumn(int columnIndex) {
        if (columnIndex < 1 || columnIndex > getColumnCount()) {
            throw new IndexOutOfBoundsException(String.format("The column index %s must be in the range [1, %s].", Integer.valueOf(columnIndex), Integer.valueOf(getColumnCount())));
        }
        this.colSpecs.remove(columnIndex - 1);
        shiftComponentsHorizontally(columnIndex, true);
        adjustGroupIndices(this.colGroupIndices, columnIndex, true);
    }

    public int getRowCount() {
        return this.rowSpecs.size();
    }

    public RowSpec getRowSpec(int rowIndex) {
        return this.rowSpecs.get(rowIndex - 1);
    }

    public void setRowSpec(int rowIndex, RowSpec rowSpec) {
        Preconditions.checkNotNull(rowSpec, Messages.MUST_NOT_BE_NULL, "row spec");
        this.rowSpecs.set(rowIndex - 1, rowSpec);
    }

    public void appendRow(RowSpec rowSpec) {
        Preconditions.checkNotNull(rowSpec, Messages.MUST_NOT_BE_NULL, "row spec");
        this.rowSpecs.add(rowSpec);
    }

    public void insertRow(int rowIndex, RowSpec rowSpec) {
        if (rowIndex < 1 || rowIndex > getRowCount()) {
            throw new IndexOutOfBoundsException(String.format("The row index %s must be in the range [1, %s].", Integer.valueOf(rowIndex), Integer.valueOf(getRowCount())));
        }
        this.rowSpecs.add(rowIndex - 1, rowSpec);
        shiftComponentsVertically(rowIndex, false);
        adjustGroupIndices(this.rowGroupIndices, rowIndex, false);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex < 1 || rowIndex > getRowCount()) {
            throw new IndexOutOfBoundsException(String.format("The row index %s must be in the range [1, %s].", Integer.valueOf(rowIndex), Integer.valueOf(getRowCount())));
        }
        this.rowSpecs.remove(rowIndex - 1);
        shiftComponentsVertically(rowIndex, true);
        adjustGroupIndices(this.rowGroupIndices, rowIndex, true);
    }

    private void shiftComponentsHorizontally(int columnIndex, boolean remove) {
        int offset = remove ? -1 : 1;
        for (Map.Entry<Component, CellConstraints> entry : this.constraintMap.entrySet()) {
            CellConstraints constraints = entry.getValue();
            int x1 = constraints.gridX;
            int w = constraints.gridWidth;
            int x2 = (x1 + w) - 1;
            if (x1 == columnIndex && remove) {
                throw new IllegalStateException("The removed column " + columnIndex + " must not contain component origins.\nIllegal component=" + entry.getKey());
            }
            if (x1 >= columnIndex) {
                constraints.gridX += offset;
            } else if (x2 >= columnIndex) {
                constraints.gridWidth += offset;
            }
        }
    }

    private void shiftComponentsVertically(int rowIndex, boolean remove) {
        int offset = remove ? -1 : 1;
        for (Map.Entry<Component, CellConstraints> entry : this.constraintMap.entrySet()) {
            CellConstraints constraints = entry.getValue();
            int y1 = constraints.gridY;
            int h = constraints.gridHeight;
            int y2 = (y1 + h) - 1;
            if (y1 == rowIndex && remove) {
                throw new IllegalStateException("The removed row " + rowIndex + " must not contain component origins.\nIllegal component=" + entry.getKey());
            }
            if (y1 >= rowIndex) {
                constraints.gridY += offset;
            } else if (y2 >= rowIndex) {
                constraints.gridHeight += offset;
            }
        }
    }

    private static void adjustGroupIndices(int[][] allGroupIndices, int modifiedIndex, boolean remove) {
        int offset = remove ? -1 : 1;
        for (int[] allGroupIndice : allGroupIndices) {
            for (int i = 0; i < allGroupIndice.length; i++) {
                int index = allGroupIndice[i];
                if (index == modifiedIndex && remove) {
                    throw new IllegalStateException("The removed index " + modifiedIndex + " must not be grouped.");
                }
                if (index >= modifiedIndex) {
                    int i2 = i;
                    allGroupIndice[i2] = allGroupIndice[i2] + offset;
                }
            }
        }
    }

    public CellConstraints getConstraints(Component component) {
        return (CellConstraints) getConstraints0(component).clone();
    }

    private CellConstraints getConstraints0(Component component) {
        Preconditions.checkNotNull(component, Messages.MUST_NOT_BE_NULL, "component");
        CellConstraints constraints = this.constraintMap.get(component);
        Preconditions.checkState(constraints != null, "The component has not been added to the container.");
        return constraints;
    }

    public void setConstraints(Component component, CellConstraints constraints) {
        Preconditions.checkNotNull(component, Messages.MUST_NOT_BE_NULL, "component");
        Preconditions.checkNotNull(constraints, Messages.MUST_NOT_BE_NULL, "constraints");
        constraints.ensureValidGridBounds(getColumnCount(), getRowCount());
        this.constraintMap.put(component, (CellConstraints) constraints.clone());
    }

    private void removeConstraints(Component component) {
        this.constraintMap.remove(component);
        this.componentSizeCache.removeEntry(component);
    }

    public int[][] getColumnGroups() {
        return deepClone(this.colGroupIndices);
    }

    public void setColumnGroups(int[][] groupOfIndices) {
        setColumnGroupsImpl(groupOfIndices, true);
    }

    private void setColumnGroupsImpl(int[][] groupOfIndices, boolean checkIndices) {
        int maxColumn = getColumnCount();
        boolean[] usedIndices = new boolean[maxColumn + 1];
        for (int group = 0; group < groupOfIndices.length; group++) {
            int[] indices = groupOfIndices[group];
            if (checkIndices) {
                Preconditions.checkArgument(indices.length >= 2, "Each indice group must contain at least two indices.");
            }
            for (int indice : indices) {
                if (indice < 1 || indice > maxColumn) {
                    throw new IndexOutOfBoundsException("Invalid column group index " + indice + " in group " + (group + 1));
                }
                if (usedIndices[indice]) {
                    throw new IllegalArgumentException("Column index " + indice + " must not be used in multiple column groups.");
                }
                usedIndices[indice] = true;
            }
        }
        this.colGroupIndices = deepClone(groupOfIndices);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v4, types: [int[], int[][]] */
    public void setColumnGroup(int... indices) {
        Preconditions.checkNotNull(indices, Messages.MUST_NOT_BE_NULL, "column group indices");
        Preconditions.checkArgument(indices.length >= 2, "You must specify at least two indices.");
        setColumnGroups(new int[]{indices});
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addGroupedColumn(int columnIndex) {
        int[][] newColGroups = getColumnGroups();
        if (newColGroups.length == 0) {
            newColGroups = new int[]{new int[]{columnIndex}};
        } else {
            int lastGroupIndex = newColGroups.length - 1;
            int[] lastGroup = newColGroups[lastGroupIndex];
            int groupSize = lastGroup.length;
            int[] newLastGroup = new int[groupSize + 1];
            System.arraycopy(lastGroup, 0, newLastGroup, 0, groupSize);
            newLastGroup[groupSize] = columnIndex;
            newColGroups[lastGroupIndex] = newLastGroup;
        }
        setColumnGroupsImpl(newColGroups, false);
    }

    public int[][] getRowGroups() {
        return deepClone(this.rowGroupIndices);
    }

    public void setRowGroups(int[][] groupOfIndices) {
        setRowGroupsImpl(groupOfIndices, true);
    }

    private void setRowGroupsImpl(int[][] groupOfIndices, boolean checkIndices) {
        int rowCount = getRowCount();
        boolean[] usedIndices = new boolean[rowCount + 1];
        for (int group = 0; group < groupOfIndices.length; group++) {
            int[] indices = groupOfIndices[group];
            if (checkIndices) {
                Preconditions.checkArgument(indices.length >= 2, "Each indice group must contain at least two indices.");
            }
            for (int indice : indices) {
                if (indice < 1 || indice > rowCount) {
                    throw new IndexOutOfBoundsException("Invalid row group index " + indice + " in group " + (group + 1));
                }
                if (usedIndices[indice]) {
                    throw new IllegalArgumentException("Row index " + indice + " must not be used in multiple row groups.");
                }
                usedIndices[indice] = true;
            }
        }
        this.rowGroupIndices = deepClone(groupOfIndices);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v4, types: [int[], int[][]] */
    public void setRowGroup(int... indices) {
        Preconditions.checkNotNull(indices, Messages.MUST_NOT_BE_NULL, "row group indices");
        Preconditions.checkArgument(indices.length >= 2, "You must specify at least two indices.");
        setRowGroups(new int[]{indices});
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void addGroupedRow(int rowIndex) {
        int[][] newRowGroups = getRowGroups();
        if (newRowGroups.length == 0) {
            newRowGroups = new int[]{new int[]{rowIndex}};
        } else {
            int lastGroupIndex = newRowGroups.length - 1;
            int[] lastGroup = newRowGroups[lastGroupIndex];
            int groupSize = lastGroup.length;
            int[] newLastGroup = new int[groupSize + 1];
            System.arraycopy(lastGroup, 0, newLastGroup, 0, groupSize);
            newLastGroup[groupSize] = rowIndex;
            newRowGroups[lastGroupIndex] = newLastGroup;
        }
        setRowGroupsImpl(newRowGroups, false);
    }

    public boolean getHonorsVisibility() {
        return this.honorsVisibility;
    }

    public void setHonorsVisibility(boolean b) {
        boolean oldHonorsVisibility = getHonorsVisibility();
        if (oldHonorsVisibility == b) {
            return;
        }
        this.honorsVisibility = b;
        Set<Component> componentSet = this.constraintMap.keySet();
        if (componentSet.isEmpty()) {
            return;
        }
        Component firstComponent = componentSet.iterator().next();
        Container container = firstComponent.getParent();
        invalidateAndRepaint(container);
    }

    public void setHonorsVisibility(Component component, Boolean b) {
        CellConstraints constraints = getConstraints0(component);
        if (Objects.equals(b, constraints.honorsVisibility)) {
            return;
        }
        constraints.honorsVisibility = b;
        invalidateAndRepaint(component.getParent());
    }

    public void addLayoutComponent(String name, Component component) {
        throw new UnsupportedOperationException("Use #addLayoutComponent(Component, Object) instead.");
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        Preconditions.checkNotNull(constraints, Messages.MUST_NOT_BE_NULL, "constraints");
        if (constraints instanceof String) {
            setConstraints(comp, new CellConstraints((String) constraints));
        } else {
            if (constraints instanceof CellConstraints) {
                setConstraints(comp, (CellConstraints) constraints);
                return;
            }
            throw new IllegalArgumentException("Illegal constraint type " + constraints.getClass());
        }
    }

    public void removeLayoutComponent(Component comp) {
        removeConstraints(comp);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return computeLayoutSize(parent, MINIMUM_WIDTH_MEASURE, MINIMUM_HEIGHT_MEASURE);
    }

    public Dimension preferredLayoutSize(Container parent) {
        return computeLayoutSize(parent, PREFERRED_WIDTH_MEASURE, PREFERRED_HEIGHT_MEASURE);
    }

    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    public void invalidateLayout(Container target) {
        invalidateCaches();
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            initializeColAndRowComponentLists();
            Dimension size = parent.getSize();
            Insets insets = parent.getInsets();
            int totalWidth = (size.width - insets.left) - insets.right;
            int totalHeight = (size.height - insets.top) - insets.bottom;
            int[] x = computeGridOrigins(parent, totalWidth, insets.left, this.colSpecs, this.colComponents, this.colGroupIndices, this.componentSizeCache, MINIMUM_WIDTH_MEASURE, PREFERRED_WIDTH_MEASURE, true);
            int[] y = computeGridOrigins(parent, totalHeight, insets.top, this.rowSpecs, this.rowComponents, this.rowGroupIndices, this.componentSizeCache, MINIMUM_HEIGHT_MEASURE, PREFERRED_HEIGHT_MEASURE, false);
            layoutComponents(x, y);
        }
    }

    private void initializeColAndRowComponentLists() {
        this.colComponents = new List[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            this.colComponents[i] = new ArrayList();
        }
        this.rowComponents = new List[getRowCount()];
        for (int i2 = 0; i2 < getRowCount(); i2++) {
            this.rowComponents[i2] = new ArrayList();
        }
        for (Map.Entry<Component, CellConstraints> entry : this.constraintMap.entrySet()) {
            Component component = entry.getKey();
            CellConstraints constraints = entry.getValue();
            if (takeIntoAccount(component, constraints)) {
                if (constraints.gridWidth == 1) {
                    this.colComponents[constraints.gridX - 1].add(component);
                }
                if (constraints.gridHeight == 1) {
                    this.rowComponents[constraints.gridY - 1].add(component);
                }
            }
        }
    }

    private Dimension computeLayoutSize(Container parent, Measure defaultWidthMeasure, Measure defaultHeightMeasure) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            initializeColAndRowComponentLists();
            int[] colWidths = maximumSizes(parent, this.colSpecs, this.colComponents, this.componentSizeCache, MINIMUM_WIDTH_MEASURE, PREFERRED_WIDTH_MEASURE, defaultWidthMeasure, true);
            int[] rowHeights = maximumSizes(parent, this.rowSpecs, this.rowComponents, this.componentSizeCache, MINIMUM_HEIGHT_MEASURE, PREFERRED_HEIGHT_MEASURE, defaultHeightMeasure, false);
            int[] groupedWidths = groupedSizes(this.colGroupIndices, colWidths);
            int[] groupedHeights = groupedSizes(this.rowGroupIndices, rowHeights);
            int[] xOrigins = computeOrigins(groupedWidths, 0);
            int[] yOrigins = computeOrigins(groupedHeights, 0);
            int width1 = sum(groupedWidths);
            int height1 = sum(groupedHeights);
            int maxWidth = width1;
            int maxHeight = height1;
            int[] maxFixedSizeColsTable = computeMaximumFixedSpanTable(this.colSpecs);
            int[] maxFixedSizeRowsTable = computeMaximumFixedSpanTable(this.rowSpecs);
            for (Map.Entry<Component, CellConstraints> entry : this.constraintMap.entrySet()) {
                Component component = entry.getKey();
                CellConstraints constraints = entry.getValue();
                if (takeIntoAccount(component, constraints)) {
                    if (constraints.gridWidth > 1 && constraints.gridWidth > maxFixedSizeColsTable[constraints.gridX - 1]) {
                        int compWidth = defaultWidthMeasure.sizeOf(component, this.componentSizeCache);
                        int gridX1 = constraints.gridX - 1;
                        int gridX2 = gridX1 + constraints.gridWidth;
                        int lead = xOrigins[gridX1];
                        int trail = width1 - xOrigins[gridX2];
                        int myWidth = lead + compWidth + trail;
                        if (myWidth > maxWidth) {
                            maxWidth = myWidth;
                        }
                    }
                    if (constraints.gridHeight > 1 && constraints.gridHeight > maxFixedSizeRowsTable[constraints.gridY - 1]) {
                        int compHeight = defaultHeightMeasure.sizeOf(component, this.componentSizeCache);
                        int gridY1 = constraints.gridY - 1;
                        int gridY2 = gridY1 + constraints.gridHeight;
                        int lead2 = yOrigins[gridY1];
                        int trail2 = height1 - yOrigins[gridY2];
                        int myHeight = lead2 + compHeight + trail2;
                        if (myHeight > maxHeight) {
                            maxHeight = myHeight;
                        }
                    }
                }
            }
            Insets insets = parent.getInsets();
            int width = maxWidth + insets.left + insets.right;
            int height = maxHeight + insets.top + insets.bottom;
            dimension = new Dimension(width, height);
        }
        return dimension;
    }

    private static int[] computeGridOrigins(Container container, int totalSize, int offset, List<? extends FormSpec> formSpecs, List<Component>[] componentLists, int[][] groupIndices, ComponentSizeCache sizeCache, Measure minMeasure, Measure prefMeasure, boolean horizontal) {
        int[] minSizes = maximumSizes(container, formSpecs, componentLists, sizeCache, minMeasure, prefMeasure, minMeasure, horizontal);
        int[] prefSizes = maximumSizes(container, formSpecs, componentLists, sizeCache, minMeasure, prefMeasure, prefMeasure, horizontal);
        int[] groupedMinSizes = groupedSizes(groupIndices, minSizes);
        int[] groupedPrefSizes = groupedSizes(groupIndices, prefSizes);
        int totalMinSize = sum(groupedMinSizes);
        int totalPrefSize = sum(groupedPrefSizes);
        int[] compressedSizes = compressedSizes(formSpecs, totalSize, totalMinSize, totalPrefSize, groupedMinSizes, prefSizes);
        int[] groupedSizes = groupedSizes(groupIndices, compressedSizes);
        int totalGroupedSize = sum(groupedSizes);
        int[] sizes = distributedSizes(formSpecs, totalSize, totalGroupedSize, groupedSizes);
        return computeOrigins(sizes, offset);
    }

    private static int[] computeOrigins(int[] sizes, int offset) {
        int count = sizes.length;
        int[] origins = new int[count + 1];
        origins[0] = offset;
        for (int i = 1; i <= count; i++) {
            origins[i] = origins[i - 1] + sizes[i - 1];
        }
        return origins;
    }

    private void layoutComponents(int[] x, int[] y) {
        Rectangle cellBounds = new Rectangle();
        for (Map.Entry<Component, CellConstraints> entry : this.constraintMap.entrySet()) {
            Component component = entry.getKey();
            CellConstraints constraints = entry.getValue();
            int gridX = constraints.gridX - 1;
            int gridY = constraints.gridY - 1;
            int gridWidth = constraints.gridWidth;
            int gridHeight = constraints.gridHeight;
            cellBounds.x = x[gridX];
            cellBounds.y = y[gridY];
            cellBounds.width = x[gridX + gridWidth] - cellBounds.x;
            cellBounds.height = y[gridY + gridHeight] - cellBounds.y;
            constraints.setBounds(component, this, cellBounds, MINIMUM_WIDTH_MEASURE, MINIMUM_HEIGHT_MEASURE, PREFERRED_WIDTH_MEASURE, PREFERRED_HEIGHT_MEASURE, this.componentSizeCache);
        }
    }

    private void invalidateCaches() {
        this.componentSizeCache.invalidate();
    }

    private static int[] maximumSizes(Container container, List<? extends FormSpec> formSpecs, List<Component>[] componentLists, ComponentSizeCache sizeCache, Measure minMeasure, Measure prefMeasure, Measure defaultMeasure, boolean horizontal) {
        int size = formSpecs.size();
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = formSpecs.get(i).maximumSize(container, componentLists[i], sizeCache, minMeasure, prefMeasure, defaultMeasure, horizontal);
        }
        return result;
    }

    private static int[] compressedSizes(List<? extends FormSpec> formSpecs, int totalSize, int totalMinSize, int totalPrefSize, int[] minSizes, int[] prefSizes) {
        if (totalSize < totalMinSize) {
            return minSizes;
        }
        if (totalSize >= totalPrefSize) {
            return prefSizes;
        }
        int count = formSpecs.size();
        int[] sizes = new int[count];
        double totalCompressionSpace = totalPrefSize - totalSize;
        double maxCompressionSpace = totalPrefSize - totalMinSize;
        double compressionFactor = totalCompressionSpace / maxCompressionSpace;
        for (int i = 0; i < count; i++) {
            FormSpec formSpec = formSpecs.get(i);
            sizes[i] = prefSizes[i];
            if (formSpec.getSize().compressible()) {
                int i2 = i;
                sizes[i2] = sizes[i2] - ((int) Math.round((prefSizes[i] - minSizes[i]) * compressionFactor));
            }
        }
        return sizes;
    }

    private static int[] groupedSizes(int[][] groups, int[] rawSizes) {
        if (groups == null || groups.length == 0) {
            return rawSizes;
        }
        int[] sizes = new int[rawSizes.length];
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = rawSizes[i];
        }
        for (int[] groupIndices : groups) {
            int groupMaxSize = 0;
            for (int groundIndex : groupIndices) {
                int index = groundIndex - 1;
                groupMaxSize = Math.max(groupMaxSize, sizes[index]);
            }
            for (int groupIndex : groupIndices) {
                int index2 = groupIndex - 1;
                sizes[index2] = groupMaxSize;
            }
        }
        return sizes;
    }

    private static int[] distributedSizes(List<? extends FormSpec> formSpecs, int totalSize, int totalPrefSize, int[] inputSizes) {
        double totalFreeSpace = totalSize - totalPrefSize;
        if (totalFreeSpace < FormSpec.NO_GROW) {
            return inputSizes;
        }
        double totalWeight = 0.0d;
        for (FormSpec formSpec : formSpecs) {
            totalWeight += formSpec.getResizeWeight();
        }
        if (totalWeight == FormSpec.NO_GROW) {
            return inputSizes;
        }
        int count = formSpecs.size();
        int[] sizes = new int[count];
        double restSpace = totalFreeSpace;
        int roundedRestSpace = (int) totalFreeSpace;
        for (int i = 0; i < count; i++) {
            FormSpec formSpec2 = formSpecs.get(i);
            double weight = formSpec2.getResizeWeight();
            if (weight == FormSpec.NO_GROW) {
                sizes[i] = inputSizes[i];
            } else {
                double roundingCorrection = restSpace - roundedRestSpace;
                double extraSpace = (totalFreeSpace * weight) / totalWeight;
                double correctedExtraSpace = extraSpace - roundingCorrection;
                int roundedExtraSpace = (int) Math.round(correctedExtraSpace);
                sizes[i] = inputSizes[i] + roundedExtraSpace;
                restSpace -= extraSpace;
                roundedRestSpace -= roundedExtraSpace;
            }
        }
        return sizes;
    }

    private static int[] computeMaximumFixedSpanTable(List<? extends FormSpec> formSpecs) {
        int size = formSpecs.size();
        int[] table = new int[size];
        int maximumFixedSpan = Integer.MAX_VALUE;
        for (int i = size - 1; i >= 0; i--) {
            FormSpec spec = formSpecs.get(i);
            if (spec.canGrow()) {
                maximumFixedSpan = 0;
            }
            table[i] = maximumFixedSpan;
            if (maximumFixedSpan < Integer.MAX_VALUE) {
                maximumFixedSpan++;
            }
        }
        return table;
    }

    private static int sum(int[] sizes) {
        int sum = 0;
        for (int i = sizes.length - 1; i >= 0; i--) {
            sum += sizes[i];
        }
        return sum;
    }

    private static void invalidateAndRepaint(Container container) {
        if (container == null) {
            return;
        }
        if (container instanceof JComponent) {
            ((JComponent) container).revalidate();
        } else {
            container.invalidate();
        }
        container.repaint();
    }

    private boolean takeIntoAccount(Component component, CellConstraints cc) {
        return component.isVisible() || (cc.honorsVisibility == null && !getHonorsVisibility()) || Boolean.FALSE.equals(cc.honorsVisibility);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout$MinimumWidthMeasure.class */
    static final class MinimumWidthMeasure implements Measure {
        MinimumWidthMeasure() {
        }

        @Override // com.jgoodies.layout.layout.FormLayout.Measure
        public int sizeOf(Component c, ComponentSizeCache cache) {
            return cache.getMinimumSize(c).width;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout$MinimumHeightMeasure.class */
    static final class MinimumHeightMeasure implements Measure {
        MinimumHeightMeasure() {
        }

        @Override // com.jgoodies.layout.layout.FormLayout.Measure
        public int sizeOf(Component c, ComponentSizeCache cache) {
            return cache.getMinimumSize(c).height;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout$PreferredWidthMeasure.class */
    static final class PreferredWidthMeasure implements Measure {
        PreferredWidthMeasure() {
        }

        @Override // com.jgoodies.layout.layout.FormLayout.Measure
        public int sizeOf(Component c, ComponentSizeCache cache) {
            return cache.getPreferredSize(c).width;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout$PreferredHeightMeasure.class */
    static final class PreferredHeightMeasure implements Measure {
        PreferredHeightMeasure() {
        }

        @Override // com.jgoodies.layout.layout.FormLayout.Measure
        public int sizeOf(Component c, ComponentSizeCache cache) {
            return cache.getPreferredSize(c).height;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout$ComponentSizeCache.class */
    public static final class ComponentSizeCache implements Serializable {
        private final Map<Component, Dimension> minimumSizes;
        private final Map<Component, Dimension> preferredSizes;

        ComponentSizeCache(int initialCapacity) {
            this.minimumSizes = new HashMap(initialCapacity);
            this.preferredSizes = new HashMap(initialCapacity);
        }

        void invalidate() {
            this.minimumSizes.clear();
            this.preferredSizes.clear();
        }

        Dimension getMinimumSize(Component component) {
            Dimension size = this.minimumSizes.get(component);
            if (size == null) {
                size = component.getMinimumSize();
                this.minimumSizes.put(component, size);
            }
            return size;
        }

        Dimension getPreferredSize(Component component) {
            Dimension size = this.preferredSizes.get(component);
            if (size == null) {
                size = component.getPreferredSize();
                this.preferredSizes.put(component, size);
            }
            return size;
        }

        void removeEntry(Component component) {
            this.minimumSizes.remove(component);
            this.preferredSizes.remove(component);
        }
    }

    public LayoutInfo getLayoutInfo(Container parent) {
        LayoutInfo layoutInfo;
        synchronized (parent.getTreeLock()) {
            initializeColAndRowComponentLists();
            Dimension size = parent.getSize();
            Insets insets = parent.getInsets();
            int totalWidth = (size.width - insets.left) - insets.right;
            int totalHeight = (size.height - insets.top) - insets.bottom;
            int[] x = computeGridOrigins(parent, totalWidth, insets.left, this.colSpecs, this.colComponents, this.colGroupIndices, this.componentSizeCache, MINIMUM_WIDTH_MEASURE, PREFERRED_WIDTH_MEASURE, true);
            int[] y = computeGridOrigins(parent, totalHeight, insets.top, this.rowSpecs, this.rowComponents, this.rowGroupIndices, this.componentSizeCache, MINIMUM_HEIGHT_MEASURE, PREFERRED_HEIGHT_MEASURE, false);
            layoutInfo = new LayoutInfo(x, y);
        }
        return layoutInfo;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/layout/FormLayout$LayoutInfo.class */
    public static final class LayoutInfo {
        public final int[] columnOrigins;
        public final int[] rowOrigins;

        private LayoutInfo(int[] xOrigins, int[] yOrigins) {
            this.columnOrigins = xOrigins;
            this.rowOrigins = yOrigins;
        }

        public int getX() {
            return this.columnOrigins[0];
        }

        public int getY() {
            return this.rowOrigins[0];
        }

        public int getWidth() {
            return this.columnOrigins[this.columnOrigins.length - 1] - this.columnOrigins[0];
        }

        public int getHeight() {
            return this.rowOrigins[this.rowOrigins.length - 1] - this.rowOrigins[0];
        }
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [int[], int[][]] */
    private static int[][] deepClone(int[][] array) {
        ?? r0 = new int[array.length];
        for (int i = 0; i < r0.length; i++) {
            r0[i] = (int[]) array[i].clone();
        }
        return r0;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        invalidateCaches();
        out.defaultWriteObject();
    }
}
